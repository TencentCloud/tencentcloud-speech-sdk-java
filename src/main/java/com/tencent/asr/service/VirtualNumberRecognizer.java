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
import com.tencent.asr.constant.AsrConstant.Code;
import com.tencent.asr.model.VirtualNumberRequest;
import com.tencent.asr.model.VirtualNumberResponse;
import com.tencent.asr.model.VirtualNumberServerConfig;
import com.tencent.asr.service.SpeechRecognitionSignService.TMap;
import com.tencent.core.help.SignHelper;
import com.tencent.core.service.ReportService;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.SignBuilder;
import com.tencent.core.utils.Tutils;
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

public class VirtualNumberRecognizer {

    private VirtualNumberServerConfig serverConfig;

    private VirtualNumberRequest request;

    private VirtualNumberRecognitionListener listener;

    public VirtualNumberRecognizer(VirtualNumberServerConfig serverConfig, VirtualNumberRequest request,
            VirtualNumberRecognitionListener listener) {
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
    //finalWs 标志 收到尾包 final=1
    private AtomicBoolean finalWs = new AtomicBoolean(false);
    //errWs  标志错误(code!=0 or onFailed )
    private AtomicBoolean errWs = new AtomicBoolean(false);
    private final CountDownLatch onopenWait = new CountDownLatch(1);
    private final CountDownLatch closeWait = new CountDownLatch(1);

    /**
     * start 开启
     */
    public boolean start() {
        try {
            if (serverConfig == null || listener == null || request == null) {
                throw new SdkRunException(Code.CODE_10010);
            }
            boolean start = startWs.compareAndSet(false, true);
            if (!start) {
                ReportService.ifLogMessage(wsId, "started!!", true);
                return false;
            }
            String suffix = SignHelper.createUrl(getVirtualNumberRequestParam());
            String signUrl = serverConfig.getSignPrefixUrl().concat(request.getAppId().toString())
                    .concat(suffix);
            String sign = SignBuilder.base64_hmac_sha1(signUrl, request.getSecretKey());
            String paramUrl = suffix.concat("&signature=").concat(URLEncoder.encode(sign, "UTF-8"));

            final String url = serverConfig.getProto().concat("://").concat(serverConfig.getHost())
                    .concat(serverConfig.getHostSuffix())
                    .concat(request.getAppId().toString()).concat(paramUrl);
            WebSocketListener webSocketListener = createWebSocketListener();
            Headers.Builder builder = new Headers.Builder()
                    .add("Host", serverConfig.getHost()).add("User-Agent", AsrConstant.SDK);
            //.add("Authorization", sign)
            if (StringUtils.isNotEmpty(request.getToken())) {
                builder.add("X-TC-Token", request.getToken());
            }
            ReportService.ifLogMessage(wsId, "signUrl: ".concat(signUrl).concat(" sign: ").concat(sign), false);
            ReportService.ifLogMessage(wsId, "key: ".concat(request.getSecretKey()), false);
            ReportService.ifLogMessage(wsId, "url: ".concat(url), false);
            Headers headers = new Headers.Builder().build();
            Request request = new Request.Builder().url(url).headers(headers).build();
            webSocket = serverConfig.getClient().newWebSocket(request, webSocketListener);
            boolean onopen = onopenWait.await(serverConfig.getOnopenWaitTime(), TimeUnit.SECONDS);
            if (!onopen) {
                webSocket.cancel();
                //throw new SdkRunException(AsrConstant.Code.CODE_10001);
                ReportService.ifLogMessage(wsId, AsrConstant.Code.CODE_10001.getMessage(), true);
                return false;
            }
            if (errWs.get()) {
                webSocket.cancel();
                //throw new SdkRunException(AsrConstant.Code.CODE_10012);
                ReportService.ifLogMessage(wsId, AsrConstant.Code.CODE_10012.getMessage(), true);
                return false;
            }
        } catch (SdkRunException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            //失败则重置
            startWs.set(false);
            ReportService.ifLogMessage(wsId, AsrConstant.Code.CODE_10001.getMessage(), true);
            //throw new SdkRunException(e.getCause(), AsrConstant.Code.CODE_10001);
            return false;
        }
        return true;
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
                VirtualNumberResponse virtualNumberResponse = new VirtualNumberResponse();
                try {
                    String trace = Tutils.getStackTrace(t);
                    if (StringUtils.contains(trace, "Socket closed")) {
                        //ReportService.ifLogMessage(wsId, "onFailure:" + trace, true);
                        ReportService.ifLogMessage(wsId, "onFailure: socket close" , true);
                        //服务端主动释放连接socket close 直接return
                        return;
                    }
                    if (endWs.get()) {
                        ReportService.ifLogMessage(wsId, "now end:" + endWs.get(), false);
                        //已经结束则无响应
                        return;
                    }
                    ReportService.ifLogMessage(wsId, "onFailure:" + trace + " end:" + endWs.get(), true);
                    virtualNumberResponse.setCode(-1);
                    virtualNumberResponse.setMessage(trace);
                    virtualNumberResponse.setVoiceID(request.getVoiceId());
                    callback = true;
                } catch (Exception e) {
                    throw e;
                } finally {
                    errWs.set(true);
                    endWs.set(true);
                    if (callback) {
                        listener.onFail(virtualNumberResponse);
                    }
                    closeWait.countDown();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                boolean success = false;
                boolean finalTag = false;
                ReportService.ifLogMessage(wsId, "onMessage:" + text, false);
                VirtualNumberResponse response = JsonUtil.fromJson(text, VirtualNumberResponse.class);
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
                        listener.onFail(response);
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
                            closeWait.countDown();
                            ReportService.ifLogMessage(wsId, "closeWait countDown", false);
                            listener.onRecognitionComplete(response);
                        } else {
                            listener.onRecognitionStart(response);
                        }
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                ReportService.ifLogMessage(wsId, "onOpen:" + JsonUtil.toJson(response), false);
                try {
                    VirtualNumberResponse virtualNumberResponse = new VirtualNumberResponse();
                    virtualNumberResponse.setFinal(0);
                    virtualNumberResponse.setResult(0);
                    virtualNumberResponse.setVoiceID(request.getVoiceId());
                    if (!(response.code() == 101)) {
                        ReportService.ifLogMessage(wsId, "onOpen: fail", true);
                        virtualNumberResponse.setCode(-1);
                        virtualNumberResponse.setMessage("start failed");
                        listener.onFail(virtualNumberResponse);
                        endWs.set(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        };
    }


    public Boolean stop() {
        try {
            if (!finalWs.get()) {
                ReportService.ifLogMessage(wsId, "send end package", false);
                webSocket.send(JsonUtil.toJson(MapUtil.builder().put("type", "end").build()));
            }
            closeWait.await(serverConfig.getCloseWaitTime(), TimeUnit.SECONDS);
            ReportService.ifLogMessage(wsId, "websocket close", false);
            boolean closed = webSocket.close(1000, "success");
            if (!closed) {
                ReportService.ifLogMessage(wsId, "websocket closed", false);
            }
        } catch (InterruptedException e) {
            ReportService.ifLogMessage(wsId, "stop_exception:" + e.getMessage(), false);
            //throw new SdkRunException(e.getCause(), AsrConstant.Code.CODE_10004);
            return false;
        }
        return true;
    }

    public boolean write(byte[] data) {
        if (finalWs.get()) {
            //收到结果则直接返回
            return true;
        }
        if (endWs.get()) {
            ReportService.ifLogMessage(wsId, "has ended stop sending", true);
            throw new SdkRunException(Code.CODE_10003);
        }
        //发送数据
        ReportService.ifLogMessage(wsId, "send package", false);
        boolean success = webSocket.send(ByteString.of(data));
        ReportService.ifLogMessage(wsId, "send  package " + success, false);
        if (!success) {
            for (int i = 0; i < serverConfig.getRetryRequestNum(); i++) {
                success = webSocket.send(ByteString.of(data));
                if (success) {
                    break;
                }
            }
        }
        return false;
    }

    /**
     * getVirtualNumberRequestParam
     *
     * @return param
     */
    private Map<String, Object> getVirtualNumberRequestParam() {
        Map<String, Object> queryMap = new TMap<>();
        queryMap.put("secretid", request.getSecretId());
        queryMap.put("timestamp", System.currentTimeMillis() / 1000);
        queryMap.put("expired", System.currentTimeMillis() / 1000 + 86400);
        queryMap.put("nonce", System.currentTimeMillis());
        queryMap.put("voice_id", request.getVoiceId());
        queryMap.put("voice_format", request.getVoiceFormat());
        queryMap.put("wait_time", request.getWaitTime());
        if (request.getExtendsParam() != null) {
            for (Map.Entry<String, Object> entry : request.getExtendsParam().entrySet()) {
                queryMap.put(entry.getKey(), entry.getValue());
            }
        }
        return queryMap;
    }


}
