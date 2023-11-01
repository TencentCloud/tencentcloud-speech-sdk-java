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

package com.tencent.tts.service;

import com.tencent.asr.constant.AsrConstant;
import com.tencent.asr.constant.AsrConstant.Code;
import com.tencent.asr.service.SdkRunException;
import com.tencent.core.help.SignHelper;
import com.tencent.core.service.ReportService;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.SignBuilder;
import com.tencent.core.utils.Tutils;
import com.tencent.tts.model.SpeechWsSynthesisRequest;
import com.tencent.tts.model.SpeechWsSynthesisResponse;
import com.tencent.tts.model.SpeechWsSynthesisServerConfig;
import com.tencent.tts.service.SpeechSynthesisSignService.TMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.commons.lang3.StringUtils;

public class SpeechWsSynthesizer {

    private SpeechWsSynthesisServerConfig serverConfig;

    private SpeechWsSynthesisRequest request;

    private SpeechWsSynthesisListener listener;

    public SpeechWsSynthesizer(SpeechWsSynthesisServerConfig serverConfig, SpeechWsSynthesisRequest request,
            SpeechWsSynthesisListener listener) {
        this.serverConfig = serverConfig;
        this.request = request;
        this.listener = listener;

    }

    private WebSocket webSocket;
    //唯一标识一个链接 用于问题排查
    private final String wsId = UUID.randomUUID().toString();
    //startWs 开启标志
    private AtomicBoolean startWs = new AtomicBoolean(false);
    //endWs 结束 可能出现错误(code!=0 or onFailed) 或者 收到尾包
    private AtomicBoolean endWs = new AtomicBoolean(false);
    //firstWs 标志
    private AtomicBoolean firstWs = new AtomicBoolean(false);
    //finalWs 标志 收到尾包 final=1
    private AtomicBoolean finalWs = new AtomicBoolean(false);
    //errWs  标志错误(code!=0 or onFailed )
    private AtomicBoolean errWs = new AtomicBoolean(false);
    private final CountDownLatch onopenWait = new CountDownLatch(1);
    private final CountDownLatch closeWait = new CountDownLatch(1);

    /**
     * start 开启
     */
    public void start() {
        try {
            if (serverConfig == null || listener == null || request == null) {
                throw new SdkRunException(Code.CODE_10010);
            }
            boolean start = startWs.compareAndSet(false, true);
            if (!start) {
                ReportService.ifLogMessage(wsId, "started!!", true);
                return;
            }
            ReportService.ifLogMessage(wsId, "synthesizer start: begin", false);
            String suffix = SignHelper.createUrl(genParams(request, serverConfig,false));
            String signUrl = serverConfig.getSignPrefixUrl().concat(suffix);
            String sign = SignBuilder.base64_hmac_sha1(signUrl, request.getSecretKey());
            suffix = SignHelper.createUrl(genParams(request, serverConfig,true));
            String paramUrl = suffix.concat("&Signature=").concat(URLEncoder.encode(sign, "UTF-8"));
            final String url = serverConfig.getProto().concat(serverConfig.getHost())
                    .concat(serverConfig.getPath()).concat(paramUrl);
            Headers.Builder builder = new Headers.Builder()
                    .add("Host", serverConfig.getHost()).add("User-Agent", AsrConstant.SDK);
            if (StringUtils.isNotEmpty(request.getToken())) {
                builder.add("X-TC-Token", request.getToken());
            }
            ReportService.ifLogMessage(wsId, "signUrl: ".concat(signUrl).concat(" sign: ").concat(sign), false);
            ReportService.ifLogMessage(wsId, "key: ".concat(request.getSecretKey()), false);
            ReportService.ifLogMessage(wsId, "url: ".concat(url), false);
            Headers headers = new Headers.Builder().build();
            Request request = new Request.Builder().url(url).headers(headers).build();
            WebSocketListener webSocketListener = createWebSocketListener();
            webSocket = serverConfig.getClient().newWebSocket(request, webSocketListener);
            boolean onopen = onopenWait.await(serverConfig.getOnopenWaitTime(), serverConfig.getOnopenWaitTimeUnit());
            if (!onopen) {
                throw new SdkRunException(AsrConstant.Code.CODE_10001);
            }
            if (errWs.get()) {
                webSocket.cancel();
                throw new SdkRunException(AsrConstant.Code.CODE_10012);
            }
        } catch (SdkRunException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            //失败则重置
            startWs.set(false);
            throw new SdkRunException(e.getCause(), AsrConstant.Code.CODE_10001);
        }
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
                ReportService.ifLogMessage(wsId, "ws onClosed:" + code + " reason:" + reason, false);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                ReportService.ifLogMessage(wsId, "ws onClosing:" + code + " reason:" + reason, false);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                ReportService.ifLogMessage(wsId, "now process", false);
                boolean callback = false;
                SpeechWsSynthesisResponse synthesisResponse = new SpeechWsSynthesisResponse();
                try {
                    if (endWs.get()) {
                        ReportService.ifLogMessage(wsId, "now end:" + endWs.get(), false);
                        //已经结束则无响应
                        return;
                    }
                    String trace = Tutils.getStackTrace(t);
                    if (StringUtils.contains(trace, "Socket closed")) {
                        ReportService.ifLogMessage(wsId, "onFailure:" + trace, true);
                        //服务端主动释放连接socket close 直接return
                        return;
                    }
                    ReportService.ifLogMessage(wsId, "onFailure:" + trace + " end:" + endWs.get(), true);
                    synthesisResponse.setCode(-1);
                    synthesisResponse.setMessage(trace);
                    synthesisResponse.setSessionId(request.getSessionId());
                    callback = true;
                } catch (Exception e) {
                    throw e;
                } finally {
                    errWs.set(true);
                    endWs.set(true);
                    if (callback) {
                        listener.onSynthesisFail(synthesisResponse);
                    }
                    closeWait.countDown();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                boolean success = false;
                boolean finalTag = false;
                ReportService.ifLogMessage(wsId, "onMessage:" + text, false);
                SpeechWsSynthesisResponse response = JsonUtil.fromJson(text, SpeechWsSynthesisResponse.class);
                try {
                    if (response != null && response.getCode() == 0) {
                        success = true;
                        if (response.getFinal() != null && response.getFinal() == 1) {
                            finalTag = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    if (!success) {
                        errWs.set(true);
                        endWs.set(true);
                        listener.onSynthesisFail(response);
                        closeWait.countDown();
                    }
                    if (onopenWait.getCount() > 0) {
                        onopenWait.countDown();
                        ReportService.ifLogMessage(wsId, "onopenWait_countDown", false);
                    }
                    if (success) {
                        if (finalTag) {
                            endWs.set(true);
                            finalWs.set(true);
                            ReportService.ifLogMessage(wsId, "closeWait countDown", false);
                            listener.onSynthesisEnd(response);
                            closeWait.countDown();
                        } else {
                            if (!firstWs.get()) {
                                listener.onSynthesisStart(response);
                                firstWs.set(true);
                            } else {
                                listener.onTextResult(response);
                            }
                        }
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                if (listener != null) {
                    listener.onAudioResult(bytes.toByteArray());
                }
            }

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                ReportService.ifLogMessage(wsId, "onOpen:" + JsonUtil.toJson(response), false);
                try {
                    SpeechWsSynthesisResponse synthesisResponse = new SpeechWsSynthesisResponse();
                    synthesisResponse.setFinal(0);
                    synthesisResponse.setCode(0);
                    synthesisResponse.setSessionId(request.getSessionId());
                    if (!(response.code() == 101)) {
                        ReportService.ifLogMessage(wsId, "onOpen: fail", true);
                        synthesisResponse.setCode(-1);
                        synthesisResponse.setMessage("start failed");
                        listener.onSynthesisFail(synthesisResponse);
                        endWs.set(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        };
    }

    /**
     * 等待任务结束
     *
     * @return
     */
    public Boolean stop() {
        return stop(0);
    }

    /**
     * 等待任务结束
     *
     * @param waitTime 等待时间
     * @return
     */
    public Boolean stop(Integer waitTime) {
        try {
            if (waitTime == 0) {
                closeWait.await();
            } else {
                closeWait.await(waitTime, TimeUnit.SECONDS);
            }
            ReportService.ifLogMessage(wsId, "websocket close", false);
            boolean closed = webSocket.close(1000, "success");
            if (!closed) {
                ReportService.ifLogMessage(wsId, "websocket closed", false);
            }
        } catch (InterruptedException e) {
            ReportService.ifLogMessage(wsId, "stop_exception:" + e.getMessage(), false);
            throw new SdkRunException(e.getCause(), AsrConstant.Code.CODE_10004);
        }
        return true;
    }

    /**
     * getVirtualNumberRequestParam
     *
     * @return param
     */
    private Map<String, Object> genParams(SpeechWsSynthesisRequest request,
            SpeechWsSynthesisServerConfig serverConfig,boolean escape) {
        TMap<String, Object> treeMap = new TMap<String, Object>();
        treeMap.put("Action", serverConfig.getAction());
        treeMap.put("AppId", request.getAppId());
        treeMap.put("SecretId", request.getSecretId());
        treeMap.put("ModelType", request.getModelType());
        treeMap.put("VoiceType", request.getVoiceType());
        treeMap.put("Codec", request.getCodec());
        treeMap.put("SampleRate", request.getSampleRate());
        //这里需要注意，后段解析float 如果是0.0则解析为0 如果是1.1则解析为1.1
        if (request.getSpeed() != null && request.getSpeed().intValue() == request.getSpeed()) {
            treeMap.put("Speed", request.getSpeed().intValue());
        } else {
            treeMap.put("Speed", request.getSpeed());
        }
        if (request.getVolume() != null && request.getVolume().intValue() == request.getVolume()) {
            treeMap.put("Volume", request.getVolume().intValue());
        } else {
            treeMap.put("Volume", request.getVolume());
        }
        if(escape){
            try {
                treeMap.put("Text",URLEncoder.encode(request.getText(),"UTF-8") );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else{
            treeMap.put("Text", request.getText());
        }
        treeMap.put("EnableSubtitle", request.getEnableSubtitle());
        treeMap.put("SegmentRate", request.getSegmentRate());
        treeMap.put("EmotionCategory", request.getEmotionCategory());
        treeMap.put("EmotionIntensity", request.getEmotionIntensity());
        treeMap.put("SessionId", request.getSessionId());
        treeMap.put("Timestamp", System.currentTimeMillis() / 1000);
        treeMap.put("Expired", System.currentTimeMillis() / 1000 + 86400); // 1天后过期
        if (request.getExtendsParam() != null) {
            for (Map.Entry<String, Object> entry : request.getExtendsParam().entrySet()) {
                treeMap.put(entry.getKey(), entry.getValue());
            }
        }
        return treeMap;
    }
}
