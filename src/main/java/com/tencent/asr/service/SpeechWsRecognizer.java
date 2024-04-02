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

import cn.hutool.core.map.MapUtil;
import com.tencent.asr.constant.AsrConstant;
import com.tencent.asr.model.AsrConfig;
import com.tencent.asr.model.AsrRequestContent;
import com.tencent.asr.model.SpeechRecognitionRequest;
import com.tencent.asr.model.SpeechRecognitionResponse;
import com.tencent.asr.model.SpeechRecognitionSysConfig;
import com.tencent.asr.utils.AsrUtils;
import com.tencent.core.help.SignHelper;
import com.tencent.core.model.GlobalConfig;
import com.tencent.core.service.ReportService;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.SignBuilder;
import com.tencent.core.utils.Tutils;

import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.commons.lang3.StringUtils;

/**
 * websocket asr
 */
public class SpeechWsRecognizer implements SpeechRecognizer {

    /**
     * 全局配置
     */
    protected AsrConfig asrConfig;

    /**
     * 请求参数
     */
    protected SpeechRecognitionRequest asrRequest;

    /**
     * 请求内容
     */
    protected AsrRequestContent asrRequestContent;

    /**
     * 用户实时结果监听器
     */
    protected SpeechRecognitionListener listener;

    /**
     * websocket
     */
    protected WebSocket webSocket;

    /**
     * 最大重连次数
     */
    protected int reConnectMaxNum = 10;

    /**
     * 连接数
     */
    protected int connectNum = 0;

    /**
     * 标志
     */
    protected volatile AtomicBoolean startFlag = new AtomicBoolean(false);
    protected volatile AtomicBoolean endFlag = new AtomicBoolean(false);
    protected volatile AtomicBoolean firstFlag = new AtomicBoolean(false);
    protected volatile AtomicBoolean errorFlag = new AtomicBoolean(false);

    /**
     * 签名service
     */
    protected SpeechRecognitionSignService speechRecognitionSignService = new SpeechRecognitionSignService();

    /**
     * 互斥锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * start 方法等待
     */
    private final CountDownLatch startLatch = new CountDownLatch(1);

    /**
     * stop 方法等待
     */
    private final CountDownLatch closeLatch = new CountDownLatch(1);
    /**
     * 一句话是否开始
     */
    private boolean begin = false;


    /**
     * 计数器
     */
    private AtomicLong adder = new AtomicLong(0);

    /**
     * WsClientService
     */
    private WsClientService wsClientService;

    /**
     * 初始化参数
     *
     * @param config   配置
     * @param request  请求参数
     * @param listener 回调
     */
    public SpeechWsRecognizer(WsClientService wsClientService,
                              String streamId, AsrConfig config,
                              SpeechRecognitionRequest request, SpeechRecognitionListener listener) {
        this.wsClientService = wsClientService;
        this.asrConfig = config;
        this.asrRequest = request;
        //这里使用到voiceId
        if (StringUtils.isEmpty(request.getVoiceId())) {
            request.setVoiceId(AsrUtils.getVoiceId(config.getAppId()));
        }
        this.asrRequestContent = AsrRequestContent.builder()
                .seq(0).end(0).streamId(streamId)
                .voiceId(request.getVoiceId()).build();
        this.listener = listener;
    }

    /**
     * 创建websocket
     */
    private Boolean createWebsocket() throws SdkRunException {
        if (webSocket == null) {
            //如果没有连接则创建连接
            try {
                lock.lock();
                if (webSocket == null) {
                    ReportService.ifLogMessage(getId(), "create websocket", false);
                    asrRequest.setTimestamp(System.currentTimeMillis() / 1000);
                    asrRequest.setExpired(System.currentTimeMillis() / 1000 + 86400);
                    Map<String, Object> paramMap = speechRecognitionSignService.getWsParams(asrConfig,
                            asrRequest, asrRequestContent);
                    String paramUrl = SignHelper.createUrl(paramMap);
                    String sign = "";
                    if (asrRequest.getHotwordList() != null) {
                        paramMap.put("hotword_list", URLEncoder.encode(asrRequest.getHotwordList()));
                    }
                    String url = asrConfig.getWsUrl() + asrConfig.getAppId() + SignHelper.createUrl(paramMap);
                    if (GlobalConfig.privateSdk) {
                        url = asrConfig.getWsUrl() + paramUrl;
                    } else {
                        String signUrl = asrConfig.getWsSignUrl() + asrConfig.getAppId() + paramUrl;
                        sign = SignBuilder.base64_hmac_sha1(signUrl, asrConfig.getSecretKey());
                    }
                    WebSocketListener webSocketListener = createWebSocketListener();
                    webSocket = wsClientService.asrWebSocket(asrConfig.getToken(), url, sign, webSocketListener);
                    boolean countDown = startLatch.await(SpeechRecognitionSysConfig.wsStartMethodWait,
                            SpeechRecognitionSysConfig.wsMethodWaitTimeUnit);
                    if (!countDown) {
                        try {
                            webSocket.close(1002, "close");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ReportService.ifLogMessage(getId(), "start timeout", false);
                        return false;
                    }
                    if (errorFlag.get() || endFlag.get()) {
                        ReportService.ifLogMessage(getId(), "start error or end", false);
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                lock.unlock();
            }
        }
        return true;
    }

    /**
     * 创建ws连接
     */
    @Override
    public Boolean start() throws SdkRunException {
        Boolean success = createWebsocket();
        if (success) {
            startFlag.set(true);
        }
        return startFlag.get();
    }

    /**
     * 语音识别发送音频数据
     *
     * @param data 数据
     */
    @Override
    public void write(byte[] data) throws SdkRunException {
        if (!startFlag.get()) {
            ReportService.ifLogMessage(getId(), "method " + adder.get() +
                    " package please call start method!!", false);
            throw new SdkRunException(AsrConstant.Code.CODE_10002);
        }
        if (endFlag.get()) {
            ReportService.ifLogMessage(getId(), "method " + adder.get() +
                    " can`t write,because you call stop method or send message fail", false);
            throw new SdkRunException(AsrConstant.Code.CODE_10003);
        }
        //发送数据
        ReportService.ifLogMessage(getId(), "send " + adder.get() + " package", false);
        boolean success = webSocket.send(ByteString.of(data));
        ReportService.ifLogMessage(getId(), "send " + adder.get() + " package " + success, false);
        adder.incrementAndGet();
        if (!success) {
            for (int i = 0; i < SpeechRecognitionSysConfig.retryRequestNum; i++) {
                success = webSocket.send(ByteString.of(data));
                if (success) {
                    break;
                }
            }
        }
    }


    /**
     * 发送尾包数据
     *
     * @param data 数据
     */
    private boolean write(String data) {
        if (!endFlag.get()) {
            //发送数据
            ReportService.ifLogMessage(getId(), "send " + adder.get() + " end package", false);
            adder.incrementAndGet();
            if(!webSocket.send(data)){
                ReportService.ifLogMessage(getId(), "send " + adder.get() + " end package failed", false);
                return false;
            }
        }
        return true;
    }


    /**
     * 结束关闭websocket连接
     *
     * @return end
     */
    public Boolean stop() {
        if (endFlag.get()) {
            return true;
        }
        //发送尾包
        boolean writeSuccess = write(JsonUtil.toJson(MapUtil.builder().put("type", "end").build()));
        endFlag.set(true);
        try {
            closeLatch.await(SpeechRecognitionSysConfig.wsStopMethodWait, SpeechRecognitionSysConfig.wsMethodWaitTimeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
            ReportService.ifLogMessage(getId(), "stop_exception:" + e.getMessage(), false);
        } finally {
            try {
                ReportService.ifLogMessage(getId(), "send websocket close", false);
                webSocket.close(1000, "success");
                ReportService.ifLogMessage(getId(), "websocket closed", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return writeSuccess;
    }

    /**
     * 返回WebSocketListener
     *
     * @return WebSocketListener WebSocketListener
     */
    private WebSocketListener createWebSocketListener() {

        return new WebSocketListener() {
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                ReportService.ifLogMessage(getId(), "ws onClosed" + reason, false);
                countDownStop("onClosed");
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                ReportService.ifLogMessage(getId(), "ws onClosing", false);
                countDownStop("onClosing");
            }


            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                try {
                    String trace = Tutils.getStackTrace(t);
                    if (StringUtils.contains(trace, "Socket closed") || endFlag.get()) {
                        //服务端主动释放连接socket close 直接return
                        ReportService.ifLogMessage(getId(), "Socket closed", false);
                        return;
                    }
                    ReportService.ifLogMessage(getId(), "onFailure:" + trace, true);
                    SpeechRecognitionResponse rs = new SpeechRecognitionResponse();
                    rs.setCode(AsrConstant.Code.EXCEPTION.getCode());
                    rs.setMessage(trace);
                    rs.setStreamId(asrRequestContent.getStreamId());
                    rs.setVoiceId(asrRequestContent.getVoiceId());
                    ReportService.ifLogMessage(getId(), "onFailure", false);
                    ReportService.report(false, String.valueOf(rs.getCode()), asrConfig, getId(),
                            asrRequest, rs, asrConfig.getWsUrl(), t.getMessage());
                    listener.onFail(rs);
                } catch (Exception e) {
                    throw e;
                } finally {
                    errorFlag.set(true);
                    endFlag.set(true);
                    countDownStart("onFailure");
                    countDownStop("onFailure");
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    ReportService.ifLogMessage(getId(), "onMessage:" + text, false);
                    SpeechRecognitionResponse response = JsonUtil.fromJson(text, SpeechRecognitionResponse.class);
                    if (listener != null && response != null) {
                        listener.onMessage(response);
                        if (response.getCode() == 0) {
                            if (!firstFlag.get()) {
                                firstFlag.set(true);
                                countDownStart("onMessage first package");
                                if (listener != null) {
                                    //start
                                    SpeechRecognitionResponse recognitionResponse = new SpeechRecognitionResponse();
                                    recognitionResponse.setCode(0);
                                    recognitionResponse.setStreamId(asrRequestContent.getStreamId());
                                    recognitionResponse.setFinalSpeech(0);
                                    recognitionResponse.setVoiceId(asrRequestContent.getVoiceId());
                                    recognitionResponse.setMessage("success");
                                    listener.onRecognitionStart(recognitionResponse);
                                }
                            }
                            //回调
                            resultCallBack(response);
                            ReportService.report(true, String.valueOf(response.getCode()), asrConfig,
                                    getId(), asrRequest, response, asrConfig.getWsUrl(), response.getMessage());
                        } else {
                            ReportService.report(false, String.valueOf(response.getCode()),
                                    asrConfig, getId(), asrRequest, response, asrConfig.getWsUrl(),
                                    response.getMessage());
                            response.setStreamId(asrRequestContent.getStreamId());
                            response.setVoiceId(asrRequestContent.getVoiceId());
                            //错误，直接结束
                            errorFlag.set(true);
                            endFlag.set(true);
                            countDownStart("onMessage");
                            countDownStop("onMessage");
                            listener.onFail(response);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                ReportService.ifLogMessage(getId(), "onOpen:" + JsonUtil.toJson(response), false);
                if (!(response.code() == 101)) {
                    ReportService.ifLogMessage(getId(), "onOpen: fail", false);
                    webSocket.close(1001, "onOpen");
                }
            }
        };
    }


    /**
     * 处理回调
     *
     * @param response SpeechRecognitionResponse
     */
    private void resultCallBack(SpeechRecognitionResponse response) {
        response.setStreamId(asrRequestContent.getStreamId());
        if (response.getFinalSpeech() == null) {
            response.setFinalSpeech(0);
        }
        if (response.getResult() != null && listener != null) {
            if (response.getResult().getSliceType() == 0) {
                begin = true;
                listener.onSentenceBegin(response);
            } else if (response.getResult().getSliceType() == 2) {
                //解决一句话sliceType结果只有2的情况
                if (!begin) {
                    SpeechRecognitionResponse beginResp = JsonUtil.fromJson(JsonUtil.toJson(response),
                            SpeechRecognitionResponse.class);
                    beginResp.getResult().setSliceType(0);
                    listener.onSentenceBegin(beginResp);
                }
                begin = false;
                listener.onSentenceEnd(response);
            } else {
                listener.onRecognitionResultChange(response);
            }

        }

        //如果final=1处理尾包
        if (response.getFinalSpeech() != null && response.getFinalSpeech() == 1) {
            if (listener != null) {
                SpeechRecognitionResponse recognizerResponse = new SpeechRecognitionResponse();
                recognizerResponse.setCode(0);
                recognizerResponse.setVoiceId(asrRequestContent.getVoiceId());
                recognizerResponse.setFinalSpeech(1);
                recognizerResponse.setStreamId(asrRequestContent.getStreamId());
                recognizerResponse.setMessage("success");
                recognizerResponse.setMessageId(response.getMessageId());
                listener.onRecognitionComplete(recognizerResponse);
            }
            countDownStop("final");
            webSocket.cancel();
        }
    }


    private String getId() {
        return asrRequestContent.getStreamId()
                + "_" + asrRequestContent.getVoiceId();
    }


    /**
     * 重连
     */
    private void reconnect(byte[] data) {
        if (endFlag.get()) {
            return;
        }
        if (connectNum <= reConnectMaxNum) {
            try {
                Thread.sleep(10);
                write(data);
                connectNum++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void countDownStop(String source) {
        try {
            if (closeLatch.getCount() > 0) {
                closeLatch.countDown();
                ReportService.ifLogMessage(asrRequestContent.getVoiceId(),
                        source + "_closeLatch_countDown", false);
            }
        } catch (Exception e) {
            ReportService.ifLogMessage(asrRequestContent.getVoiceId(),
                    source + "_closeLatch_exception" + e.getMessage(), true);
        }
    }

    private void countDownStart(String source) {
        try {
            if (startLatch.getCount() > 0) {
                startLatch.countDown();
                ReportService.ifLogMessage(asrRequestContent.getVoiceId(),
                        source + "_startLatch_countDown", false);
            }
        } catch (Exception e) {
            ReportService.ifLogMessage(asrRequestContent.getVoiceId(),
                    source + " _startLatch_countDown" + e.getMessage(), true);
        }
    }
}
