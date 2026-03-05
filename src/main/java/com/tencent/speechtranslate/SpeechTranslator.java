package com.tencent.speechtranslate;

import com.google.gson.Gson;
import com.tencent.core.help.SignHelper;
import com.tencent.core.ws.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.tencent.core.ws.StateMachine.State.*;

/**
 * 语音翻译器，是语音翻译服务的入口
 */
public class SpeechTranslator extends StateMachine {
    static Logger logger = LoggerFactory.getLogger(SpeechTranslator.class);

    /**
     * 上下文信息
     */
    private final Map<String, Object> ctx = new HashMap<>();

    /**
     * 请求标识 当request中voice_id为空时，translateUuid为voice_id
     */
    private final String translateUuid = UUID.randomUUID().toString();

    private final CountDownLatch stopLatch;
    private final CountDownLatch startLatch;
    protected long lastSendTime = -1;

    protected Connection conn;

    private Credential credential;
    private SpeechTranslatorRequest request;

    private SpeechClient client;

    private SpeechTranslatorListener listener;

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public SpeechTranslatorRequest getRequest() {
        return request;
    }

    public void setRequest(SpeechTranslatorRequest request) {
        this.request = request;
    }

    public SpeechClient getClient() {
        return client;
    }

    public void setClient(SpeechClient client) {
        this.client = client;
    }

    public SpeechTranslatorListener getListener() {
        return listener;
    }

    public void setListener(SpeechTranslatorListener listener) {
        this.listener = listener;
    }

    /**
     * 创建语音翻译器实例
     *
     * @param client     语音客户端
     * @param credential 凭证信息
     * @param request    翻译请求参数
     * @param listener   翻译监听器
     * @throws Exception 参数校验异常
     */
    public SpeechTranslator(SpeechClient client, Credential credential, SpeechTranslatorRequest request,
                            SpeechTranslatorListener listener) throws Exception {
        Optional.ofNullable(client).orElseThrow(() -> new RuntimeException("client cannot be null"));
        Optional.ofNullable(request).orElseThrow(() -> new RuntimeException("request cannot be null"));
        Optional.ofNullable(credential).orElseThrow(() -> new RuntimeException("credential cannot be null"));
        Optional.ofNullable(listener).orElseThrow(() -> new RuntimeException("listener cannot be null"));
        if (request.getVoiceId() == null) {
            request.setVoiceId(translateUuid);
        }
        this.request = request;
        this.credential = credential;
        this.client = client;
        this.listener = listener;
        stopLatch = new CountDownLatch(1);
        startLatch = new CountDownLatch(1);
        listener.setSpeechTranslator(this);
    }

    /**
     * 请求服务端，超时则抛出异常
     *
     * @throws Exception 连接或超时异常
     */
    public void start() throws Exception {
        start(SpeechTranslateConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
    }

    /**
     * 请求服务端，超时则抛出异常
     *
     * @param milliSeconds 超时时间（毫秒）
     * @throws Exception 连接或超时异常
     */
    public void start(long milliSeconds) throws Exception {
        state.checkStart();
        logger.debug("translateUuid:{}, start change state from {} ", translateUuid, state);
        state = state.start();
        logger.debug("translateUuid:{}, start change state to {} ", translateUuid, state);
        request.setSecretid(credential.getSecretId());
        request.setTimestamp(System.currentTimeMillis() / 1000);
        request.setExpired(System.currentTimeMillis() / 1000 + 86400); // 1天后过期
        Map<String, Object> sortParamMap = request.toTreeMap();
        String sign = SignHelper.createSign(SpeechTranslateConstant.DEFAULT_TRANSLATE_SIGN_PREFIX,
                SignHelper.createUrl(sortParamMap), credential.getAppid(), credential.getSecretKey());
        String url = SignHelper.createRequestUrl(SpeechTranslateConstant.DEFAULT_TRANSLATE_REQ_URL,
                SignHelper.createUrl(SignHelper.encode(sortParamMap)), credential.getAppid());
        url = url + "&signature=" + URLEncoder.encode(sign, "UTF-8");
        logger.debug(url);
        ConnectionProfile connectionProfile = new ConnectionProfile(sign, url, SpeechTranslateConstant.DEFAULT_HOST,
                this.credential.getToken());
        this.conn = client.connect(connectionProfile, this.listener);
        Map<String, Long> network = new HashMap<>();
        network.put(Constant.CONNECTING_LATENCY_KEY, conn.getConnectingLatency());
        network.put(Constant.HANDSHAKE_LATENCY_KEY, conn.getHandshakeLatency());
        ctx.put(Constant.NETWORK_KEY, network);
        boolean result = startLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
        if (!result) {
            String msg = String.format("timeout after %d ms waiting for start confirmation. translateUuid:%s, state:%s",
                    milliSeconds, translateUuid, state);
            logger.error(msg);
            throw new Exception(msg);
        }
    }

    /**
     * 发送音频数据
     *
     * @param data 音频数据
     */
    public void write(byte[] data) {
        write(data, data.length);
    }

    /**
     * 发送音频数据
     *
     * @param data   音频数据
     * @param length 数据长度
     */
    public void write(byte[] data, int length) {
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
            conn.sendBinary(Arrays.copyOfRange(data, 0, length));
            lastSendTime = System.currentTimeMillis();
        } catch (Exception e) {
            logger.error("fail to send binary message, current_task_id:{}, state:{}", translateUuid, state, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 结束语音翻译：发送结束翻译通知，接收服务端确认
     *
     * @throws Exception 超时异常
     */
    public void stop() throws Exception {
        stop(SpeechTranslateConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
    }

    /**
     * 结束语音翻译：发送结束翻译通知，接收服务端确认，超时未返回则抛出异常
     *
     * @param milliSeconds 超时时间（毫秒）
     * @throws Exception 超时异常
     */
    public void stop(long milliSeconds) throws Exception {
        if (state == STATE_COMPLETE) {
            logger.info("state is {} stop message is discarded", STATE_COMPLETE);
            return;
        }
        state.checkStop();
        state = state.stopSend();
        if (conn != null) {
            Map<String, String> end = new HashMap<>();
            end.put("type", "end");
            conn.sendText(new Gson().toJson(end));
            boolean result = stopLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
            if (!result) {
                String msg = String.format(
                        "timeout after %d ms waiting for stop confirmation. translateUuid:%s, state:%s",
                        milliSeconds, translateUuid, state);
                logger.error(msg);
                throw new Exception(msg);
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
        logger.debug("translateUuid:{}, markReady change state from {} ", translateUuid, state);
        state = state.send();
        logger.debug("translateUuid:{}, markReady change state to {} ", translateUuid, state);
        if (this.startLatch != null) {
            this.startLatch.countDown();
        }
    }

    /**
     * 内部调用方法
     */
    void markComplete() {
        logger.debug("translateUuid:{}, markComplete change state from {} ", translateUuid, state);
        state = state.complete();
        logger.debug("translateUuid:{}, markComplete change state to {} ", translateUuid, state);
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }

    void markFail() {
        logger.debug("translateUuid:{}, markFail change state from {} ", translateUuid, state);
        state = state.fail();
        logger.debug("translateUuid:{}, markFail change state to {} ", translateUuid, state);
        if (startLatch != null) {
            startLatch.countDown();
        }
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }

    void markClosed() {
        logger.debug("translateUuid:{}, markClosed change state from {} ", translateUuid, state);
        state = state.closed();
        logger.debug("translateUuid:{}, markClosed change state to {} ", translateUuid, state);
        if (startLatch != null) {
            startLatch.countDown();
        }
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }
}
