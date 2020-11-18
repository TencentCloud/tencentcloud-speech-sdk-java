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
import com.tencent.asr.utils.AsrUtils;
import com.tencent.core.service.ReportService;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.SignBuilder;
import lombok.SneakyThrows;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

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
     * 是否连接
     */
    protected volatile boolean isConnect = false;

    /**
     * 结束标志
     */
    protected volatile AtomicBoolean endFlag = new AtomicBoolean(false);

    /**
     * 开始标志
     */
    protected volatile AtomicBoolean startFlag = new AtomicBoolean(false);

    /**
     * 签名service
     */
    protected SpeechRecognitionSignService speechRecognitionSignService = new SpeechRecognitionSignService();

    /**
     * 互斥锁
     */
    private ReentrantLock lock = new ReentrantLock();


    /**
     * 一句话是否开始
     */
    private boolean begin = false;


    /**
     * 计数器
     */
    private AtomicLong adder = new AtomicLong(0);


    private TractionManager tractionManager;

    /**
     * 初始化参数
     *
     * @param config   配置
     * @param request  请求参数
     * @param listener 回调
     */
    public SpeechWsRecognizer(String streamId, AsrConfig config, SpeechRecognitionRequest request, SpeechRecognitionListener listener) {
        this.asrConfig = config;
        this.asrRequest = request;
        //这里使用到voiceId
        this.asrRequestContent = AsrRequestContent.builder()
                .seq(0).end(0).streamId(streamId)
                .voiceId(AsrUtils.getVoiceId(config.getAppId())).build();
        this.listener = listener;
        this.tractionManager = new TractionManager(config.getAppId());
    }

    /**
     * 创建ws连接
     */
    @Override
    public void start() {
        startFlag.set(true);
        createWebsocket();
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
        //执行统计逻辑
        tractionManager.beginTraction(asrRequestContent.getStreamId());
    }

    /**
     * 语音识别发送音频数据
     *
     * @param data 数据
     */
    @Override
    public void write(byte[] data) {
        if (!startFlag.get()) {
            throw new RuntimeException("please call start method!!");
        }
        if (!endFlag.get()) {
            createWebsocket();
            //发送数据
            ReportService.ifLogMessage(getId(), "send " + adder.get() + " package", false);
            adder.incrementAndGet();
            webSocket.send(ByteString.of(data));
        }
    }

    /**
     * 发送尾包数据
     *
     * @param data 数据
     */
    private void write(String data) {
        if (!endFlag.get()) {
            createWebsocket();
            //发送数据
            ReportService.ifLogMessage(getId(), "send " + adder.get() + " end package", false);
            adder.incrementAndGet();
            webSocket.send(data);
        }
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
        write(JsonUtil.toJson(MapUtil.builder().put("type", "end").build()));
        endFlag.set(true);
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
                super.onClosed(webSocket, code, reason);
                ReportService.ifLogMessage(getId(), "ws onClosed" + reason, false);
                isConnect = false;
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                ReportService.ifLogMessage(getId(), "ws onClosing", false);
                isConnect = false;
                webSocket.close(1003, "onClosing");
            }

            @SneakyThrows
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                isConnect = false;
                if (t.getMessage() != null && !t.getMessage().equals("Socket closed")) {
                    ReportService.ifLogMessage(getId(), "onFailure:reconnect," + t.getMessage() + t.getLocalizedMessage(), true);
                    //连接中断则重新连接
                    webSocket.close(1002, "onFailure");
                    reconnect(new byte[0]);
                    return;
                }
                if (response != null) {
                    ReportService.ifLogMessage(getId(), "onFailure:" + response.message() + "_"
                            + t.getMessage(), false);
                    SpeechRecognitionResponse rs = new SpeechRecognitionResponse();
                    rs.setCode(AsrConstant.Code.EXCEPTION.getCode());
                    rs.setMessage(response.message());
                    rs.setStreamId(asrRequestContent.getStreamId());
                    rs.setVoiceId(asrRequestContent.getVoiceId());
                    ReportService.ifLogMessage(getId(), "onFailure", false);
                    ReportService.report(false, String.valueOf(rs.getCode()),asrConfig, getId(), asrRequest, rs, asrConfig.getWsUrl(), t.getMessage());
                    listener.onFail(rs);
                }
            }

            @SneakyThrows
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                ReportService.ifLogMessage(getId(), "onMessage:" + text, false);
                SpeechRecognitionResponse response = JsonUtil.fromJson(text, SpeechRecognitionResponse.class);
                if (listener != null) {
                    if (response.getCode() == 0) {
                        //回调
                        resultCallBack(response);
                        ReportService.report(true, String.valueOf(response.getCode()), asrConfig, getId(), asrRequest,
                                response, asrConfig.getWsUrl(), response.getMessage());
                    } else {
                        ReportService.report(false, String.valueOf(response.getCode()), asrConfig, getId(), asrRequest,
                                response, asrConfig.getWsUrl(), response.getMessage());
                        response.setStreamId(asrRequestContent.getStreamId());
                        response.setVoiceId(asrRequestContent.getVoiceId());
                        //错误，直接结束
                        endFlag.set(true);
                        ReportService.report(false, String.valueOf(response.getCode()), asrConfig, getId(), asrRequest,
                                response, asrConfig.getWsUrl(), response.getMessage());
                        listener.onFail(response);
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                ReportService.ifLogMessage(getId(), "onOpen:" + JsonUtil.toJson(response), false);
                isConnect = response.code() == 101;
                if (!isConnect) {
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
        if (response.getResult() != null) {
            if (response.getResult().getSliceType() == 0) {
                begin = true;
                listener.onSentenceBegin(response);
            } else if (response.getResult().getSliceType() == 2) {
                //解决一句话sliceType结果只有2的情况
                if (!begin) {
                    SpeechRecognitionResponse beginResp = JsonUtil.fromJson(JsonUtil.toJson(response), SpeechRecognitionResponse.class);
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
        }
    }


    private String getId() {
        return asrRequestContent.getStreamId() + "_" + asrRequestContent.getVoiceId();
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

    /**
     * 创建websocket
     */
    private void createWebsocket() {
        if (!isConnect || webSocket == null) {
            //如果没有连接则创建连接
            try {
                lock.lock();
                if (!isConnect || webSocket == null) {
                    ReportService.ifLogMessage(getId(), "create websocket", false);
                    String url = speechRecognitionSignService.signWsUrl(asrConfig, asrRequest, asrRequestContent);
                    String sign = SignBuilder.createGetSign(url, asrConfig.getSecretKey(), asrRequest);
                    WebSocketListener webSocketListener = createWebSocketListener();
                    webSocket = WsClientService.asrWebSocket(url, sign, webSocketListener);
                    isConnect = true;
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
