package com.tencent.asrspeaker;

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
 * 实时语音识别（句子模式 + 话者分离）。
 */
public class SpeakerRecognizer extends StateMachine {

    static Logger logger = LoggerFactory.getLogger(SpeakerRecognizer.class);

    private final String recUuid = UUID.randomUUID().toString();

    private final CountDownLatch stopLatch;
    private final CountDownLatch startLatch;
    protected long lastSendTime = -1;

    protected Connection conn;

    private final Credential credential;
    private final SpeakerRecognizerRequest request;
    private final SpeechClient client;
    private final SpeakerRecognitionListener listener;

    private String speakerContextId;

    public SpeakerRecognizer(SpeechClient client, Credential credential,
                             SpeakerRecognizerRequest request, SpeakerRecognitionListener listener) {
        Optional.ofNullable(client).orElseThrow(() -> new RuntimeException("client cannot be null"));
        Optional.ofNullable(credential).orElseThrow(() -> new RuntimeException("credential cannot be null"));
        Optional.ofNullable(request).orElseThrow(() -> new RuntimeException("request cannot be null"));
        Optional.ofNullable(listener).orElseThrow(() -> new RuntimeException("listener cannot be null"));

        if (request.getVoiceId() == null) {
            request.setVoiceId(recUuid);
        }

        this.client = client;
        this.credential = credential;
        this.request = request;
        this.listener = listener;
        this.stopLatch = new CountDownLatch(1);
        this.startLatch = new CountDownLatch(1);

        listener.setSpeakerRecognizer(this);
    }

    public void start() throws Exception {
        start(SpeakerConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
    }

    /**
     * @param milliSeconds 超时时间（ms），超时未收到服务端确认则抛出异常
     */
    public void start(long milliSeconds) throws Exception {
        state.checkStart();
        logger.debug("recUuid:{}, start state from {}", recUuid, state);
        state = state.start();
        logger.debug("recUuid:{}, start state to {}", recUuid, state);

        request.setSecretId(credential.getSecretId());
        request.setTimestamp(System.currentTimeMillis() / 1000);
        request.setExpired(System.currentTimeMillis() / 1000 + 86400);

        Map<String, Object> sortParamMap = request.toTreeMap();
        String sign = SignHelper.createSign(
                SpeakerConstant.DEFAULT_RT_SIGN_PREFIX,
                SignHelper.createUrl(sortParamMap),
                credential.getAppid(),
                credential.getSecretKey());
        String url = SignHelper.createRequestUrl(
                SpeakerConstant.DEFAULT_RT_REQ_URL,
                SignHelper.createUrl(SignHelper.encode(sortParamMap)),
                credential.getAppid());
        url = url + "&signature=" + URLEncoder.encode(sign, "UTF-8");

        logger.debug(url);
        ConnectionProfile profile = new ConnectionProfile(sign, url, SpeakerConstant.DEFAULT_HOST, credential.getToken());
        this.conn = client.connect(profile, this.listener);

        boolean result = startLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
        if (!result) {
            String msg = String.format("timeout after %d ms waiting for start confirmation. recUuid:%s, state:%s",
                    milliSeconds, recUuid, state);
            logger.error(msg);
            throw new Exception(msg);
        }
    }

    public void write(byte[] data) {
        write(data, data.length);
    }

    public void write(byte[] data, int length) {
        if (state == STATE_COMPLETE) {
            logger.info("state is {}, stop send", STATE_COMPLETE);
            return;
        }
        long sendInterval;
        if (lastSendTime != -1 && (sendInterval = (System.currentTimeMillis() - lastSendTime)) > 5000) {
            logger.warn("too large binary send interval: {} ms", sendInterval);
        }
        state.checkSend();
        try {
            conn.sendBinary(Arrays.copyOfRange(data, 0, length));
            lastSendTime = System.currentTimeMillis();
        } catch (Exception e) {
            logger.error("fail to send binary, recUuid:{}, state:{}", recUuid, state, e);
            throw new RuntimeException(e);
        }
    }

    public void stop() throws Exception {
        stop(SpeakerConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
    }

    /**
     * @param milliSeconds 超时时间（ms），超时未收到服务端确认则抛出异常
     */
    public void stop(long milliSeconds) throws Exception {
        if (state == STATE_COMPLETE) {
            logger.info("state is {}, stop message is discarded", STATE_COMPLETE);
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
                String msg = String.format("timeout after %d ms waiting for stop confirmation. recUuid:%s, state:%s",
                        milliSeconds, recUuid, state);
                logger.error(msg);
                throw new Exception(msg);
            }
        }
    }

    public void close() {
        if (conn != null) {
            conn.close();
        }
    }

    void markReady() {
        logger.debug("recUuid:{}, markReady state from {}", recUuid, state);
        state = state.send();
        logger.debug("recUuid:{}, markReady state to {}", recUuid, state);
        if (startLatch != null) {
            startLatch.countDown();
        }
    }

    void markComplete() {
        logger.debug("recUuid:{}, markComplete state from {}", recUuid, state);
        state = state.complete();
        logger.debug("recUuid:{}, markComplete state to {}", recUuid, state);
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }

    void markFail() {
        logger.debug("recUuid:{}, markFail state from {}", recUuid, state);
        state = state.fail();
        logger.debug("recUuid:{}, markFail state to {}", recUuid, state);
        if (startLatch != null) {
            startLatch.countDown();
        }
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }

    void markClosed() {
        logger.debug("recUuid:{}, markClosed state from {}", recUuid, state);
        state = state.closed();
        logger.debug("recUuid:{}, markClosed state to {}", recUuid, state);
        if (startLatch != null) {
            startLatch.countDown();
        }
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }

    public String getSpeakerContextId() {
        return speakerContextId;
    }

    public void setSpeakerContextId(String speakerContextId) {
        this.speakerContextId = speakerContextId;
    }

    public String getRecUuid() {
        return recUuid;
    }
}
