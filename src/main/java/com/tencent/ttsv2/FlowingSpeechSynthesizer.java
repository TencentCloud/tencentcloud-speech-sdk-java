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
package com.tencent.ttsv2;

import com.google.gson.Gson;
import com.tencent.core.help.SignHelper;
import com.tencent.core.utils.SignBuilder;
import com.tencent.core.ws.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.tencent.core.ws.StateMachine.State.STATE_COMPLETE;

/**
 * 本接口服务采用 websocket 协议，将请求文本合成为音频，同步返回合成音频数据及相关文本信息，达到“边合成边播放”的效果。
 */
public class FlowingSpeechSynthesizer extends StateMachine {
    static Logger logger = LoggerFactory.getLogger(FlowingSpeechSynthesizer.class);

    /**
     * 上下文信息
     */
    private final Map<String, Object> ctx = new HashMap<>();

    /**
     * 请求标识 当request中voice_id为空时，rec_uuid为voice_id
     */
    private final String sessionId = UUID.randomUUID().toString();

    private final CountDownLatch stopLatch;
    private final CountDownLatch startLatch;
    protected long lastSendTime = -1;

    protected Connection conn;

    private Credential credential;
    private FlowingSpeechSynthesizerRequest request;

    private SpeechClient client;

    private FlowingSpeechSynthesizerListener listener;

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public FlowingSpeechSynthesizerRequest getRequest() {
        return request;
    }

    public void setRequest(FlowingSpeechSynthesizerRequest request) {
        this.request = request;
    }

    public SpeechClient getClient() {
        return client;
    }

    public void setClient(SpeechClient client) {
        this.client = client;
    }

    public FlowingSpeechSynthesizerListener getListener() {
        return listener;
    }

    public void setListener(FlowingSpeechSynthesizerListener listener) {
        this.listener = listener;
    }

    public FlowingSpeechSynthesizer(SpeechClient client, Credential credential, FlowingSpeechSynthesizerRequest request, FlowingSpeechSynthesizerListener listener) throws Exception {
        Optional.ofNullable(client).orElseThrow(() -> new RuntimeException("client cannot be null"));
        Optional.ofNullable(request).orElseThrow(() -> new RuntimeException("request cannot be null"));
        Optional.ofNullable(credential).orElseThrow(() -> new RuntimeException("credential cannot be null"));
        Optional.ofNullable(listener).orElseThrow(() -> new RuntimeException("listener cannot be null"));
        if (request.getSessionId() == null) {
            request.setSessionId(sessionId);
        }
        request.setAction("TextToStreamAudioWSv2");
        request.setAppId(Integer.valueOf(credential.getAppid()));
        this.request = request;
        this.credential = credential;
        this.client = client;
        this.listener = listener;
        stopLatch = new CountDownLatch(1);
        startLatch = new CountDownLatch(1);
        listener.setSpeechSynthesizer(this);
    }

    /**
     * 请求服务端，超时则抛出异常
     *
     * @throws Exception
     */
    public void start() throws Exception {
        start(TtsConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
    }

    /**
     * 请求服务端，超时则抛出异常
     *
     * @param milliSeconds
     * @throws Exception
     */
    public void start(long milliSeconds) throws Exception {
        state.checkStart();
        logger.debug("sessionId:{},start change state from {} ", sessionId, state);
        state = state.start();
        logger.debug("sessionId:{},start change state to {} ", sessionId, state);
        request.setSecretid(credential.getSecretId());
        request.setTimestamp(System.currentTimeMillis() / 1000);
        request.setExpired(System.currentTimeMillis() / 1000 + 86400); // 1天后过期
        Map<String, Object> sortParamMap = request.toTreeMap();
        String signUrl = new StringBuilder().append(TtsConstant.DEFAULT_TTS_V2_SIGN_PREFIX).append(SignHelper.createUrl(sortParamMap)).toString();
        logger.debug(signUrl);
        String sign = SignBuilder.base64_hmac_sha1(signUrl, credential.getSecretKey());
        String serverUrl = SignHelper.createUrl(SignHelper.encode(sortParamMap));
        String url = new StringBuilder().append(TtsConstant.DEFAULT_TTS_V2_REQ_URL).append(serverUrl).append("&Signature=").append(URLEncoder.encode(sign, "UTF-8")).toString();
        logger.debug(url);
        ConnectionProfile connectionProfile = new ConnectionProfile(sign, url, TtsConstant.DEFAULT_HOST, this.credential.getToken());
        this.conn = client.connect(connectionProfile, this.listener);
        Map<String, Long> network = new HashMap<>();
        network.put(Constant.CONNECTING_LATENCY_KEY, conn.getConnectingLatency());
        network.put(Constant.HANDSHAKE_LATENCY_KEY, conn.getHandshakeLatency());
        ctx.put(Constant.NETWORK_KEY, network);
        boolean result = startLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
        if (!result) {
            String msg = String.format("timeout after %d ms waiting for start confirmation.sessionId:%s,state:%s", milliSeconds, sessionId, state);
            logger.error(msg);
            throw new Exception(msg);
        }
    }

    /**
     * 发送数据
     *
     * @param text
     */
    public void process(String text) {
        if (state == STATE_COMPLETE) {
            logger.info("state is {} stop send", STATE_COMPLETE);
            return;
        }
        long sendInterval;
        if (lastSendTime != -1 && (sendInterval = (System.currentTimeMillis() - lastSendTime)) > 5000) {
            logger.warn("too large binary send interval: {} million second", sendInterval);
        }
        state.checkSend();
        try {
            String message = newWsRequestMessage(text, TtsConstant.getFlowingSpeechSynthesizer_ACTION_SYNTHESIS());
            conn.sendText(message);
            lastSendTime = System.currentTimeMillis();
        } catch (Exception e) {
            logger.error("fail to send binary,current_task_id:{},state:{}", sessionId, state, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 合成文本message
     *
     * @param text   文本
     * @param action 事件
     * @return
     */
    private String newWsRequestMessage(String text, String action) {
        Map<String, String> message = new HashMap<String, String>();
        message.put("session_id", request.getSessionId());
        message.put("message_id", UUID.randomUUID().toString());
        message.put("data", text);
        message.put("action", action);
        return new Gson().toJson(message);
    }


    /**
     * 结束语音识别:发送结束识别通知,接收服务端确认
     *
     * @throws Exception
     */
    public void stop() throws Exception {
        stop(TtsConstant.DEFAULT_TTS_FLOWING_START_TIMEOUT_MILLISECONDS);
    }

    /**
     * 结束语音识别:发送结束识别通知,接收服务端确认, 超时未返回则抛出异常
     *
     * @throws Exception
     */
    public void stop(long milliSeconds) throws Exception {
        if (state == STATE_COMPLETE) {
            logger.info("state is {} stop message is discarded", STATE_COMPLETE);
            return;
        }
        state.checkStop();
        state = state.stopSend();
        if (conn != null) {
            String end = newWsRequestMessage("", TtsConstant.getFlowingSpeechSynthesizer_ACTION_COMPLETE());
            logger.debug(end);
            conn.sendText(end);
            if (milliSeconds > 0) {
                boolean result = stopLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
                if (!result) {
                    String msg = String.format("timeout after %d ms waiting for stop confirmation.sessionId:%s,state:%s", milliSeconds, sessionId, state);
                    logger.error(msg);
                    throw new Exception(msg);
                }
            } else {
                stopLatch.await();
            }
        }

    }


    /**
     * wait for processing complete
     *
     * @param interval 等待间隔
     */
    private void waitComplete(int interval) {
        while (conn.isActive()) {
            try {
                Thread.sleep(interval);
            } catch (Exception e) {
                break;
            }
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (conn != null) {
            conn.close();
        }
    }


    void markReady() {
        logger.debug("sessionId:{},markReady change state from {} ", sessionId, state);
        state = state.send();
        logger.debug("sessionId:{},markReady change state to {} ", sessionId, state);
        if (this.startLatch != null) {
            this.startLatch.countDown();
        }
    }

    /**
     * 内部调用方法
     */
    void markComplete() {
        logger.debug("sessionId:{},markComplete change state from {} ", sessionId, state);
        state = state.complete();
        logger.debug("sessionId:{},markComplete change state to {} ", sessionId, state);
        if (stopLatch != null) {
            stopLatch.countDown();
        }

    }

    void markFail() {
        logger.debug("sessionId:{},markFail change state from {} ", sessionId, state);
        state = state.fail();
        logger.debug("sessionId:{},markFail change state to {} ", sessionId, state);
        if (startLatch != null) {
            startLatch.countDown();
        }
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }


    void markClosed() {
        logger.debug("sessionId:{},markClosed change state from {} ", sessionId, state);
        state = state.closed();
        logger.debug("sessionId:{},markClosed change state to {} ", sessionId, state);
        if (startLatch != null) {
            startLatch.countDown();
        }
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }
}
