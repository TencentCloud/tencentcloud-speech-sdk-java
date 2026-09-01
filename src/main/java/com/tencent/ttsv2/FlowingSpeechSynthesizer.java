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
 * This service uses the WebSocket protocol to synthesize the requested text into audio, synchronously returning the synthesized audio data along with related text information, achieving a "synthesize while playing" effect.
 */
public class FlowingSpeechSynthesizer extends StateMachine {
    static Logger logger = LoggerFactory.getLogger(FlowingSpeechSynthesizer.class);

    /**
     * Context information.
     */
    private final Map<String, Object> ctx = new HashMap<>();

    /**
     * Request identifier. When voice_id in the request is empty, rec_uuid is used as the voice_id.
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
     * Send the request to the server. Throws an exception on timeout.
     *
     * @throws Exception
     */
    public void start() throws Exception {
        start(TtsConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
    }

    /**
     * Send the request to the server. Throws an exception on timeout.
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
        request.setExpired(System.currentTimeMillis() / 1000 + 86400); // Expires after 1 day.
        Map<String, Object> sortParamMap = request.toTreeMap();
        String signUrl = new StringBuilder().append(TtsConstant.DEFAULT_TTS_V2_SIGN_PREFIX).append(SignHelper.createUrl(sortParamMap)).toString();
        logger.debug(signUrl);
        String sign = SignBuilder.base64_hmac_sha1(signUrl, credential.getSecretKey());
        String serverUrl = SignHelper.createUrl(SignHelper.encode(sortParamMap));
        boolean isInternational = TtsConstant.ACCOUNT_AREA_INTERNATIONAL.equals(request.getAccountArea());
        String reqUrlPrefix = isInternational ? TtsConstant.DEFAULT_TTS_V2_INTL_REQ_URL : TtsConstant.DEFAULT_TTS_V2_REQ_URL;
        String host = isInternational ? TtsConstant.DEFAULT_INTL_HOST : TtsConstant.DEFAULT_HOST;
        String url = new StringBuilder().append(reqUrlPrefix).append(serverUrl).append("&Signature=").append(URLEncoder.encode(sign, "UTF-8")).toString();
        logger.debug(url);
        ConnectionProfile connectionProfile = new ConnectionProfile(sign, url, host, this.credential.getToken());
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
     * Send data.
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

    public void reset() {
        if (state == STATE_COMPLETE) {
            logger.info("state is {} stop send", STATE_COMPLETE);
            return;
        }
        state.checkSend();
        try {
            String message = newWsRequestMessage("", TtsConstant.getFlowingSpeechSynthesizer_ACTION_RESET());
            conn.sendText(message);
            lastSendTime = System.currentTimeMillis();
        } catch (Exception e) {
            logger.error("fail to reset ,current_task_id:{},state:{}", sessionId, state, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Build the synthesis text message.
     *
     * @param text   text
     * @param action event
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
     * Finish speech recognition: send the end-of-recognition notification and wait for the server's acknowledgment.
     *
     * @throws Exception
     */
    public void stop() throws Exception {
        stop(TtsConstant.DEFAULT_TTS_FLOWING_STOP_TIMEOUT_MILLISECONDS);
    }

    /**
     * Finish speech recognition: send the end-of-recognition notification and wait for the server's acknowledgment. Throws an exception if no response is returned within the timeout.
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
     * @param interval wait interval
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
     * Close the connection.
     */
    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
            if (TtsConstant.DEFAULT_TTS_FLOWING_CLOSE_SLEEP_MILLISECONDS > 0) {
                Thread.sleep(TtsConstant.DEFAULT_TTS_FLOWING_CLOSE_SLEEP_MILLISECONDS); // Sleep 500ms to avoid the server-side connection not being closed in time.
            }
        } catch (Exception e) {

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
     * Internal callback method.
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
