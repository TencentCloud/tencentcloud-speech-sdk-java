/*
 * Copyright (c) 2017-2018 THL A29 Limited, a Tencent company. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tencent.asr.service;

import cn.hutool.core.collection.CollectionUtil;
import com.tencent.asr.constant.AsrConstant;
import com.tencent.asr.model.*;
import com.tencent.asr.utils.AsrUtils;
import com.tencent.core.handler.BaseEventListener;
import com.tencent.core.handler.RealTimeEventListener;
import com.tencent.core.service.ReportService;
import com.tencent.core.utils.ByteUtils;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.SignBuilder;
import com.tencent.core.utils.Tutils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.concurrent.FutureCallback;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpBaseService {

    /**
     * 全局配置
     */
    protected AsrConfig asrConfig;

    /**
     * 请求参数
     */
    protected AsrRequest asrRequest;

    /**
     * 用户实时结果监听器
     */
    protected RealTimeEventListener realTimeEventListener;

    /**
     * 用户结果监听器
     */
    protected SpeechRecognitionListener speechRecognitionListener;

    /**
     * 标示流的唯一主键
     */
    protected String streamId;

    /**
     * 事务管理器
     */
    protected TractionManager tractionManager;

    /**
     * 用于记录链路的seq streamId end
     */
    protected AsrRequestContent staging;

    /**
     * 回调监听器
     */
    protected BaseEventListener<AsrResponse> baseEventListeners;

    /**
     * 过期时间，用于判断请求间隔是否超时
     */
    protected long expireTime = System.currentTimeMillis() + 6000;

    /**
     * 缓存状态
     */
    protected volatile Boolean cacheStatus = false;
    /**
     * 缓存重制的seq
     */
    protected volatile AtomicInteger cacheSeq = new AtomicInteger(0);

    /**
     * 结束标志
     */
    protected volatile AtomicBoolean endFlag = new AtomicBoolean(false);
    protected volatile AtomicBoolean finishFlag = new AtomicBoolean(false);

    /**
     * 签名service
     */
    protected SpeechRecognitionSignService speechRecognitionSignService = new SpeechRecognitionSignService();

    /**
     * httpClient service
     */
    protected HttpClientService httpClientService = new HttpClientService();

    /**
     * 记录请求的stamp
     */
    protected List<String> requestStamps = new CopyOnWriteArrayList<>();

    /**
     * 结果缓冲队列
     */
    protected BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>();

    /**
     * 处理同步结果
     */
    protected BlockingQueue<AsrResponse> syncResponseQueue = new LinkedBlockingQueue<>();

    /**
     * 响应结果
     */
    protected ConcurrentHashMap<String, AsrResponse> result = new ConcurrentHashMap<>();

    /**
     * 用于判断请求是否处理完毕
     */
    protected volatile AtomicInteger surplus = new AtomicInteger(0);

    /**
     * 缓存发送数据
     */
    private byte[] sendData = new byte[0];
    private long sendExpireTime = System.currentTimeMillis() + 5000;

    //用于处理只有sliceType=2的情况
    private Boolean begin = false;

    /**
     * @param streamId              流的唯一标示
     * @param config                配置{@link AsrConfig}
     * @param request               请求参数{@link AsrRequest}
     * @param realTimeEventListener 结果回调函数{@link RealTimeEventListener}
     * @param baseEventListener     回调函数{@link BaseEventListener}
     */
    public HttpBaseService(String streamId, AsrConfig config, AsrRequest request, RealTimeEventListener realTimeEventListener,
                           BaseEventListener<AsrResponse> baseEventListener) {
        this.streamId = streamId;
        this.asrConfig = config;
        this.asrRequest = checkAsrRequest(request);
        this.realTimeEventListener = realTimeEventListener;
        this.baseEventListeners = baseEventListener;
        staging = AsrRequestContent.builder().seq(0).end(0).voiceId(AsrUtils.getVoiceId(asrConfig.getAppId())).streamId(streamId).build();
        tractionManager = new TractionManager(asrConfig.getAppId());
    }

    public HttpBaseService(String streamId, AsrConfig config, AsrRequest request, SpeechRecognitionListener speechRecognitionListener) {
        this.streamId = streamId;
        this.asrConfig = config;
        this.asrRequest = checkAsrRequest(request);
        this.speechRecognitionListener = speechRecognitionListener;
        staging = AsrRequestContent.builder().seq(0).end(0).voiceId(AsrUtils.getVoiceId(asrConfig.getAppId())).streamId(streamId).build();
        tractionManager = new TractionManager(asrConfig.getAppId());
    }

    /**
     * 发送数据
     *
     * @param data 语音数据
     * @param end  是否结束
     */
    protected void send(byte[] data, boolean end) {
        if (data == null) {
            return;
        }
        //fix 如果数据为空则返回
        if (data.length == 0 && !end) {
            return;
        }
        //fix 尾包且是空包的情况
        if (data.length == 2 && end && staging.getSeq() == 0) {
            ReportService.ifLogMessage(staging.getVoiceId(), "send end null package", false);
            AsrRequestContent content = AsrRequestContent.builder().voiceId(staging.getVoiceId()).seq(0)
                    .end(1).streamId(staging.getStreamId()).costTime(System.currentTimeMillis()).build();
            String stamp = content.getStreamId() + "_asr_" + content.getVoiceId() + "_" + content.getSeq();
            requestStamps.add(stamp);
            processFinally(null, null, stamp, "", asrRequest, content);
            return;
        }
        //重置voiceId和seq
        if (staging.getSeq() != 0 && (System.currentTimeMillis() > expireTime || cacheStatus)) {
            synchronized (this) {
                cacheStatus = false;
                expireTime = System.currentTimeMillis() + asrConfig.getWaitTime();
                ReportService.ifLogMessage(staging.getVoiceId(), "Retransmission settings:" + JsonUtil.toJson(staging), true);
                staging.setVoiceId(AsrUtils.getVoiceId(asrConfig.getAppId()));
                staging.setEnd(0);
                staging.setSeq(0);
            }
        }
        //设置过期时间，如果超过6s没有发送流，则重新设置voiceId
        expireTime = System.currentTimeMillis() + asrConfig.getWaitTime();
        ReportService.ifLogMessage(staging.getVoiceId(), "read data length:" + data.length, false);
        String stamp = dispatcherRequest(data, end);
        requestStamps.add(stamp);
    }


    /**
     * 请求分发
     *
     * @param bytes   语音数据
     * @param endFlag 结束标志
     * @return 请求唯一标示stamp
     */
    protected String dispatcherRequest(byte[] bytes, Boolean endFlag) {
        //开启事物根据线程配置seq end voiceId
        AsrRequestContent temp = staging;
        int end = endFlag ? 1 : 0;
        AsrRequestContent content = AsrRequestContent.builder().voiceId(temp.getVoiceId()).seq(temp.getSeq())
                .end(end).bytes(bytes).streamId(temp.getStreamId()).costTime(System.currentTimeMillis()).build();
        String stamp = speechRec(content);
        temp.setSeq(temp.getSeq() + 1);
        temp.setEnd(end);
        return stamp;
    }

    /**
     * 异步调用
     *
     * @param content 请求语音内容对象
     * @return 请求唯一标示stamp
     */
    private String speechRec(AsrRequestContent content) {
        String stamp = content.getStreamId() + "_asr_" + content.getVoiceId() + "_" + content.getSeq();
        String url = speechRecognitionSignService.signUrl(asrConfig, asrRequest, content);
        String sign = SignBuilder.createPostSign(url, asrConfig.getSecretKey(), asrRequest);
        AsrRequest tempRequest = JsonUtil.fromJson(JsonUtil.toJson(asrRequest), AsrRequest.class);
        httpClientRequest(content, stamp, url, sign, tempRequest);
        return stamp;
    }


    /**
     * httpClient 请求处理
     *
     * @param content     请求内容对象
     * @param stamp       流唯一标示
     * @param url         请求URL
     * @param sign        签名
     * @param tempRequest 请求参数
     */
    private void httpClientRequest(AsrRequestContent content,
                                   String stamp, String url, String sign, AsrRequest tempRequest) {
        surplus.incrementAndGet();
        //同步
        if (SpeechRecognitionSysConfig.ifSyncHttp) {
            syncRecRequest(asrConfig, content, stamp, url, sign, tempRequest);
            return;
        }
        //异步
        asyncRecRequest(asrConfig, content, stamp, url, sign, tempRequest);
    }


    /**
     * 异步识别请求
     *
     * @param config      配置
     * @param content     发送内容
     * @param stamp       标签
     * @param url         请求URL
     * @param sign        签名
     * @param tempRequest 请求参数
     */
    private void asyncRecRequest(AsrConfig config, AsrRequestContent content, String stamp, String url, String sign, AsrRequest tempRequest) {
        httpClientService.asrAsyncHttp(stamp, sign, url, config.getToken(), content, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                AsrResponse asrResponse = null;
                try {
                    asrResponse = parseRecResponse(httpResponse, stamp);
                } finally {
                    processFinally(asrResponse, null, stamp, url, tempRequest, content);
                    surplus.decrementAndGet();
                }
            }

            @Override
            public void failed(Exception e) {
                AsrResponse asrResponse = null;
                try {
                    //异常重试
                    asrResponse = processException(e, stamp, sign, url, content, tempRequest, config);
                } finally {
                    processFinally(asrResponse, e, stamp, url, tempRequest, content);
                    surplus.decrementAndGet();
                }
            }

            @Override
            public void cancelled() {
                //do nothing
            }
        });
    }


    /**
     * 同步请求
     *
     * @param config      配置
     * @param content     发送内容
     * @param stamp       标签
     * @param url         请求URL
     * @param sign        签名
     * @param tempRequest 请求参数
     */
    private void syncRecRequest(AsrConfig config, AsrRequestContent content, String stamp, String url,
                                String sign, AsrRequest tempRequest) {
        AsrResponse asrResponse = null;
        try {
            //同步请求返回结果，可能异常，需要对异常处理
            CloseableHttpResponse response = httpClientService.syncHttp(stamp, sign, url, config.getToken(), content);
            //解析请求结果
            asrResponse = this.parseRecResponse(response, stamp);
        } catch (Exception e) {
            //异常重试
            asrResponse = processException(e, stamp, sign, url, content, tempRequest, config);
        } finally {
            processFinally(asrResponse, null, stamp, url, tempRequest, content);
            surplus.decrementAndGet();
        }
    }


    /**
     * 创建异常response
     *
     * @param content content
     * @param stamp   stamp
     * @param e       exception
     * @return AsrResponse
     */
    private AsrResponse createExceptionResponse(AsrRequestContent content, String stamp, Exception e) {
        AsrResponse asrResponse = new AsrResponse();
        if (e instanceof IOException) {
            asrResponse.setCode(AsrConstant.Code.IO_EXCEPTION.getCode());
        } else {
            asrResponse.setCode(AsrConstant.Code.EXCEPTION.getCode());
        }

        asrResponse.setStreamId(content.getStreamId());
        asrResponse.setStamp(stamp);
        asrResponse.setVoiceId(content.getVoiceId());
        asrResponse.setSeq(content.getSeq());
        asrResponse.setFinalSpeech(content.getEnd());
        asrResponse.setMessage(Tutils.getStackTraceAsString(e));
        return asrResponse;
    }


    /**
     * 处理结果
     *
     * @param asrResponse
     * @param e
     * @param stamp
     */
    private void processFinally(AsrResponse asrResponse, Exception e, String stamp, String url, AsrRequest tempRequest, AsrRequestContent content) {
        content.setCostTime(System.currentTimeMillis() - content.getCostTime());
        if (asrResponse == null && content.getEnd() == 1) {
            asrResponse = new AsrResponse();
            asrResponse.setStamp(stamp);
            asrResponse.setVoiceId(content.getVoiceId());
            asrResponse.setSeq(content.getSeq());
            asrResponse.setFinalSpeech(content.getEnd());
            asrResponse.setMessage("success");
            asrResponse.setCode(AsrConstant.Code.SUCCESS.getCode());
        }
        if (asrResponse != null) {
            //fix 错误情况结果不带final，手动赋值
            if (asrResponse.getFinalSpeech() == null) {
                asrResponse.setFinalSpeech(content.getEnd());
            }
            //ReportService.ifLogMessage(stamp, "processFinally:" + JsonUtil.toJson(asrResponse), false);
            //同步结果返回是顺序的直接放到queue中
            if (SpeechRecognitionSysConfig.ifSyncHttp) {
                syncResponseQueue.add(asrResponse);
            } else {
                resultQueue.add(stamp);
                result.put(asrResponse.getStamp(), asrResponse);
            }
            ReportService.report(asrResponse.getCode() == 0, String.valueOf(asrResponse.getCode()),
                    asrConfig, streamId, tempRequest, asrResponse, url,
                    asrResponse.getMessage(), content.getCostTime());
            //从下一个分片开始重新生成voiceId
            if (AsrConstant.Code.ifInRetryCode(asrResponse.getCode())) {
                cacheStatus = true;
                cacheSeq.set(Math.max(asrResponse.getSeq(), cacheSeq.get()));
            }
            if (baseEventListeners != null) {
                if (asrResponse.getCode() == AsrConstant.Code.SUCCESS.getCode()) {
                    baseEventListeners.success(asrResponse);
                } else if (e != null) {
                    baseEventListeners.fail(asrResponse, e);
                }else{
                    baseEventListeners.success(asrResponse);
                }
            }
        }
    }


    /**
     * 返回
     *
     * @param asrResponse asrResponse
     */
    private void returnResult(AsrResponse asrResponse) {
        //根据code进行处理
        if (asrResponse != null) {
            if (asrResponse.getCode() == AsrConstant.Code.SUCCESS.getCode()) {
                if (realTimeEventListener != null) {
                    realTimeEventListener.translation(asrResponse);
                }
                //SUCCESS
                speechRecognitionListenerCallBack(asrResponse);
            } else if (AsrConstant.Code.ifInRetryCode(asrResponse.getCode())) {
                //重试
            } else {
                if (realTimeEventListener != null) {
                    realTimeEventListener.translation(asrResponse);
                }
                //其他错误,需要给用户展示的错误
                speechRecognitionListenerCallBack(asrResponse);
            }
        }
    }


    private AsrResponse retryRecRequest(String stamp, String sign, String url, AsrRequestContent content) {
        for (int retry = 0; retry < SpeechRecognitionSysConfig.retryRequestNum; retry++) {
            ReportService.ifLogMessage(stamp, "retry send request:" + retry, false);
            try {
                Thread.sleep(50);
                CloseableHttpResponse httpResponse = httpClientService.syncHttp(stamp, sign, url, asrConfig.getToken(), content);
                AsrResponse asrResponse = this.parseRecResponse(httpResponse, stamp);
                if (asrResponse != null) {
                    ReportService.ifLogMessage(stamp, "retry send request  success :" + retry, false);
                    return asrResponse;
                }
            } catch (Exception exception) {
                //ignore
                ReportService.ifLogMessage(stamp, "retry :" + exception.getMessage(), false);
            }
        }
        return null;
    }

    /**
     * 解析请求结果
     *
     * @param httpResponse httpResponse
     * @param stamp        stamp
     * @return AsrResponse
     */
    private AsrResponse parseRecResponse(HttpResponse httpResponse, String stamp) {
        if (httpResponse == null) {
            return null;
        }
        String responseStr = httpClientService.dealHttpClientResponse(httpResponse, stamp);
        AsrResponse response = null;
        try {
            response = JsonUtil.fromJson(responseStr, AsrResponse.class);
            response.setStreamId(streamId);
            response.setStamp(stamp);
        } catch (Exception e) {
            response = new AsrResponse();
            response.setStreamId(streamId);
            response.setVoiceId(staging.getVoiceId());
            response.setStamp(stamp);
            response.setMessage(responseStr);
            response.setCode(AsrConstant.Code.FAIL.getCode());
        }
        return response;
    }


    /**
     * 检查请求参数是否合法
     *
     * @param request 请求参数
     * @return 请求参数
     */
    private AsrRequest checkAsrRequest(AsrRequest request) {
        if (request.getEngineModelType() == null) {
            request.setEngineModelType("8k_0");
        }
        if (request.getCutLength() == null) {
            resetCutLength(request);
        }
        if (request.getEngineModelType().contains("8k")) {
            if (request.getCutLength() <= 0 || request.getCutLength() >= (3200 * 5)) {
                request.setCutLength(3200);
            }
        }
        if (request.getEngineModelType().contains("16k")) {
            if (request.getCutLength() <= 0 || request.getCutLength() >= (6400 * 5)) {
                request.setCutLength(6400);
            }
        }
        return request;
    }

    /**
     * 重置cutLength
     *
     * @param request request
     */
    private void resetCutLength(AsrRequest request) {
        if (request.getEngineModelType().contains("8k")) {
            request.setCutLength(3200);
        }
        if (request.getEngineModelType().contains("16k")) {
            request.setCutLength(6400);
        }
    }


    /**
     * 处理异常
     *
     * @param e           e
     * @param stamp       stamp
     * @param sign        sign
     * @param url         url
     * @param content     content
     * @param tempRequest tempRequest
     * @param config      config
     * @return
     */
    private AsrResponse processException(Exception e, String stamp, String sign, String url, AsrRequestContent content, AsrRequest tempRequest, AsrConfig config) {
        AsrResponse asrResponse = null;
        if (SpeechRecognitionSysConfig.ifSyncHttp) {
            asrResponse = retryRecRequest(stamp, sign, url, content);
        }
        if (!SpeechRecognitionSysConfig.ifSyncHttp && !(e instanceof SocketTimeoutException)) {
            asrResponse = retryRecRequest(stamp, sign, url, content);
        }
        if (asrResponse == null) {
            //重试失败,返回默认响应
            asrResponse = createExceptionResponse(content, stamp, e);
            long delayTime = System.currentTimeMillis() - content.getCostTime();
            ReportService.report(false, String.valueOf(AsrConstant.Code.IO_EXCEPTION.getCode()),
                    config, streamId, tempRequest, asrResponse, url, asrResponse.getMessage(), delayTime);
            try {
                asrResponse.setMessage(asrResponse.getMessage().substring(0, asrResponse.getMessage().indexOf("\n")));
            } catch (Exception exception) {

            }
        }
        return asrResponse;
    }

    /**
     * 处理语音数据
     *
     * @param data 语音数据
     * @param size 语音数据大小
     * @return 处理后语音数据
     */
    public byte[] createBytes(byte[] data, int size) {
        if (size > 0 && size < asrRequest.getCutLength()) {
            data = ByteUtils.subBytes(data, 0, size);
        }
        if (size <= 0) {
            data = new byte[1];
        }
        return data;
    }

    /**
     * 获取返回结果，对结果进行排序
     */
    public void startListerResult() {
        //避免死循环导致服务问题，添加等待时间，通过超时判断
        new Thread(new Runnable() {
            @Override
            public void run() {
                AtomicInteger expireNum = new AtomicInteger(0);
                while (true) {
                    //同步的处理
                    if (SpeechRecognitionSysConfig.ifSyncHttp) {
                        try {
                            AsrResponse response = syncResponseQueue.poll(SpeechRecognitionSysConfig.waitResultTimeout, TimeUnit.MILLISECONDS);
                            returnResult(response);
                            if (response != null && response.getFinalSpeech() != null && response.getFinalSpeech() == 1) {
                                ReportService.ifLogMessage(response.getStamp(), "final Exit monitoring", false);
                                closeClient();
                                break;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            //阻塞等待结果
                            try {
                                resultQueue.poll(SpeechRecognitionSysConfig.waitResultTimeout, TimeUnit.MILLISECONDS);
                            } catch (InterruptedException e) {
                            }
                            //包含结果
                            if (!requestStamps.isEmpty() && result.containsKey(requestStamps.get(0))) {
                                expireNum.set(0);
                                String stamp = requestStamps.get(0);
                                AsrResponse response = result.get(stamp);
                                if (response != null) {
                                    returnResult(response);
                                    if (response.getFinalSpeech() != null && response.getFinalSpeech() == 1) {
                                        ReportService.ifLogMessage(response.getStamp(), "final Exit monitoring", false);
                                        closeClient();
                                        break;
                                    }
                                    result.remove(stamp);
                                    if (!requestStamps.isEmpty()) {
                                        requestStamps.remove(0);
                                    }
                                }
                            } else {
                                expireNum.incrementAndGet();
                            }
                            //如果超过一定10次获取不到结果则抛弃
                            if (expireNum.get() > 10 && !requestStamps.isEmpty()) {
                                requestStamps.remove(0);
                            }
                            if (requestStamps.isEmpty() && finishFlag.get() && surplus.get() <= 0) {
                                ReportService.ifLogMessage(staging.getVoiceId(), "Exit monitoring", false);
                                closeClient();
                                break;
                            }
                        } catch (RuntimeException e) {

                        }
                    }
                }
            }
        }).start();
    }


    /**
     * 关闭httpClient
     */
    protected void closeClient() {
        if (httpClientService != null) {
            httpClientService.closeClient();
        }
    }

    /**
     * 添加数据到缓冲队列
     *
     * @param audio 语音数据
     */
    protected void sendData(byte[] audio) {
        if (audio == null) {
            return;
        }
        sendData = ByteUtils.concat(sendData, audio);
        if (sendData.length > asrRequest.getCutLength()) {
            int posi = 0;
            while (sendData.length - posi >= asrRequest.getCutLength()) {
                send(ByteUtils.subBytes(sendData, posi, asrRequest.getCutLength()), false);
                posi = posi + asrRequest.getCutLength();
            }
            sendData = ByteUtils.subBytes(sendData, posi, sendData.length - posi);
            sendExpireTime = System.currentTimeMillis() + asrConfig.getWaitTime() / 2;
        }
        //结束发送剩余数据
        if (endFlag.get()) {
            send(sendData, true);
        }
        //避免因为缓冲区数据未满，造成最后的数据不进行发送
        if (System.currentTimeMillis() > sendExpireTime) {
            send(sendData, false);
            sendExpireTime = System.currentTimeMillis() + asrConfig.getWaitTime() / 2;
            sendData = new byte[0];
        }
    }


    /**
     * 处理结果回调
     *
     * @param asrResponse asrResponse
     */
    private void speechRecognitionListenerCallBack(AsrResponse asrResponse) {
        if (speechRecognitionListener == null) {
            return;
        }
        List<SpeechRecognitionResponseResult> resultList = asrResponse.getResultList();
        SpeechRecognitionResponse response = JsonUtil.fromJson(JsonUtil.toJson(asrResponse), SpeechRecognitionResponse.class);
        if (asrResponse != null && asrResponse.getCode() == 0) {
            if (CollectionUtil.isNotEmpty(resultList)) {
                resultList.forEach(item -> {
                    response.setResult(item);
                    if (item.getSliceType() == 0) {
                        begin = true;
                        speechRecognitionListener.onSentenceBegin(response);
                    } else if (item.getSliceType() == 2) {
                        //解决sliceType只有2的情况
                        if (!begin && response.getResult() != null) {
                            SpeechRecognitionResponse beginResp = JsonUtil.fromJson(JsonUtil.toJson(response), SpeechRecognitionResponse.class);
                            beginResp.getResult().setSliceType(0);
                            speechRecognitionListener.onSentenceBegin(beginResp);
                        }
                        begin = false;
                        speechRecognitionListener.onSentenceEnd(response);
                    } else {
                        speechRecognitionListener.onRecognitionResultChange(response);
                    }
                });
            }
            if (response.getFinalSpeech() != null && response.getFinalSpeech() == 1) {
                SpeechRecognitionResponse recognitionResponse = new SpeechRecognitionResponse();
                recognitionResponse.setCode(0);
                recognitionResponse.setFinalSpeech(1);
                recognitionResponse.setStreamId(asrResponse.getStreamId());
                recognitionResponse.setVoiceId(asrResponse.getVoiceId());
                recognitionResponse.setMessage("success");
                speechRecognitionListener.onRecognitionComplete(recognitionResponse);
            }
        } else {
            speechRecognitionListener.onFail(response);
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ByteData {
        byte[] data;
    }

}
