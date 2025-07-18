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
 * 实时语音合成
 */
public class SpeechSynthesizer extends StateMachine {

    static Logger logger = LoggerFactory.getLogger(SpeechSynthesizer.class);

    /**
     * 上下文信息
     */
    private final Map<String, Object> ctx = new HashMap<>();

    /**
     * 请求标识 当request中voice_id为空时，rec_uuid为voice_id
     */
    private final String recUuid = UUID.randomUUID().toString();

    private final CountDownLatch stopLatch;
    private final CountDownLatch startLatch;
    protected long lastSendTime = -1;

    protected Connection conn;

    private Credential credential;
    private SpeechSynthesizerRequest request;

    private SpeechClient client;

    private SpeechSynthesizerListener listener;

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public SpeechSynthesizerRequest getRequest() {
        return request;
    }

    public void setRequest(SpeechSynthesizerRequest request) {
        this.request = request;
    }

    public SpeechClient getClient() {
        return client;
    }

    public void setClient(SpeechClient client) {
        this.client = client;
    }

    public SpeechSynthesizerListener getListener() {
        return listener;
    }

    public void setListener(SpeechSynthesizerListener listener) {
        this.listener = listener;
    }

    public SpeechSynthesizer(SpeechClient client, Credential credential, SpeechSynthesizerRequest request, SpeechSynthesizerListener listener) throws Exception {
        Optional.ofNullable(client).orElseThrow(() -> new RuntimeException("client cannot be null"));
        Optional.ofNullable(request).orElseThrow(() -> new RuntimeException("request cannot be null"));
        Optional.ofNullable(credential).orElseThrow(() -> new RuntimeException("credential cannot be null"));
        Optional.ofNullable(listener).orElseThrow(() -> new RuntimeException("listener cannot be null"));
        if (request.getSessionId() == null) {
            request.setSessionId(recUuid);
        }
        request.setAction("TextToStreamAudioWS");
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
        logger.debug("recUuid:{},start change state from {} ", recUuid, state);
        state = state.start();
        logger.debug("recUuid:{},start change state to {} ", recUuid, state);
        request.setSecretid(credential.getSecretId());
        request.setTimestamp(System.currentTimeMillis() / 1000);
        request.setExpired(System.currentTimeMillis() / 1000 + 86400); // 1天后过期
        Map<String, Object> sortParamMap = request.toTreeMap();
        String signUrl = new StringBuilder().append(TtsConstant.DEFAULT_TTS_SIGN_PREFIX).append(SignHelper.createUrl(sortParamMap)).toString();
        logger.debug(signUrl);
        String sign = SignBuilder.base64_hmac_sha1(signUrl, credential.getSecretKey());
        String serverUrl = SignHelper.createUrl(SignHelper.encode(sortParamMap));
        String url = new StringBuilder().append(TtsConstant.DEFAULT_TTS_REQ_URL).append(serverUrl).append("&Signature=").append(URLEncoder.encode(sign, "UTF-8")).toString();
        logger.debug(url);
        ConnectionProfile connectionProfile = new ConnectionProfile(sign, url, TtsConstant.DEFAULT_HOST, this.credential.getToken());
        this.conn = client.connect(connectionProfile, this.listener);
        Map<String, Long> network = new HashMap<>();
        network.put(Constant.CONNECTING_LATENCY_KEY, conn.getConnectingLatency());
        network.put(Constant.HANDSHAKE_LATENCY_KEY, conn.getHandshakeLatency());
        ctx.put(Constant.NETWORK_KEY, network);
        boolean result = startLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
        if (!result) {
            String msg = String.format("timeout after %d ms waiting for start confirmation.recUuid:%s,state:%s", milliSeconds, recUuid, state);
            logger.error(msg);
            throw new Exception(msg);
        }
    }

    /**
     * 发送数据
     *
     * @param data
     */
   /* public void write(byte[] data) {
        write(data, data.length);
    }*/

    /**
     * 发送数据
     *
     * @param data
     * @param length
     */
    /*public void write(byte[] data, int length) {
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
            logger.error("fail to send binary,current_task_id:{},state:{}", recUuid, state, e);
            throw new RuntimeException(e);
        }
    }*/


    /**
     * 结束语音识别:发送结束识别通知,接收服务端确认
     *
     * @throws Exception
     */
    public void stop() throws Exception {
        stop(TtsConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
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
            Map<String, String> end = new HashMap<String, String>();
            end.put("type", "end");
            conn.sendText(new Gson().toJson(end));
            boolean result = stopLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
            if (!result) {
                String msg = String.format("timeout after %d ms waiting for stop confirmation.recUuid:%s,state:%s", milliSeconds, recUuid, state);
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
        logger.debug("recUuid:{},markReady change state from {} ", recUuid, state);
        state = state.send();
        logger.debug("recUuid:{},markReady change state to {} ", recUuid, state);
        if (this.startLatch != null) {
            this.startLatch.countDown();
        }
    }

    /**
     * 内部调用方法
     */
    void markComplete() {
        logger.debug("recUuid:{},markComplete change state from {} ", recUuid, state);
        state = state.complete();
        logger.debug("recUuid:{},markComplete change state to {} ", recUuid, state);
        if (stopLatch != null) {
            stopLatch.countDown();
        }

    }

    void markFail() {
        logger.debug("recUuid:{},markFail change state from {} ", recUuid, state);
        state = state.fail();
        logger.debug("recUuid:{},markFail change state to {} ", recUuid, state);
        if (startLatch != null) {
            startLatch.countDown();
        }
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }


    void markClosed() {
        logger.debug("recUuid:{},markClosed change state from {} ", recUuid, state);
        state = state.closed();
        logger.debug("recUuid:{},markClosed change state to {} ", recUuid, state);
        if (startLatch != null) {
            startLatch.countDown();
        }
        if (stopLatch != null) {
            stopLatch.countDown();
        }
    }
}
