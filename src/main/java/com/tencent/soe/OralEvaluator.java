package com.tencent.soe;

import com.google.gson.Gson;
import com.tencent.core.help.SignHelper;
import com.tencent.core.ws.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.tencent.core.ws.StateMachine.State.STATE_COMPLETE;

public class OralEvaluator extends StateMachine {

    static Logger logger = LoggerFactory.getLogger(OralEvaluator.class);

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
    private OralEvaluationRequest request;

    private SpeechClient client;

    private OralEvaluationListener listener;

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public OralEvaluationRequest getRequest() {
        return request;
    }

    public void setRequest(OralEvaluationRequest request) {
        this.request = request;
    }

    public SpeechClient getClient() {
        return client;
    }

    public void setClient(SpeechClient client) {
        this.client = client;
    }

    public OralEvaluationListener getListener() {
        return listener;
    }

    public void setListener(OralEvaluationListener listener) {
        this.listener = listener;
    }

    public OralEvaluator(SpeechClient client, Credential credential, OralEvaluationRequest request, OralEvaluationListener listener) throws Exception {
        Optional.ofNullable(client).orElseThrow(() -> new RuntimeException("client cannot be null"));
        Optional.ofNullable(request).orElseThrow(() -> new RuntimeException("request cannot be null"));
        Optional.ofNullable(credential).orElseThrow(() -> new RuntimeException("credential cannot be null"));
        Optional.ofNullable(listener).orElseThrow(() -> new RuntimeException("listener cannot be null"));
        if (request.getVoiceId() == null) {
            request.setVoiceId(recUuid);
        }
        this.request = request;
        this.credential = credential;
        this.client = client;
        this.listener = listener;
        stopLatch = new CountDownLatch(1);
        startLatch = new CountDownLatch(1);
        listener.setOralEvaluation(this);
    }

    /**
     * 请求服务端，超时则抛出异常
     *
     * @throws Exception
     */
    public void start() throws Exception {
        start(OralEvalConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
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
        String sign = SignHelper.createSign(OralEvalConstant.DEFAULT_ORAL_EVAL_SIGN_PREFIX, SignHelper.createUrl(sortParamMap), credential.getAppid(), credential.getSecretKey());
        Map<String, Object> encodeParam = SignHelper.encode(sortParamMap);
        String url = SignHelper.createRequestUrl(OralEvalConstant.DEFAULT_ORAL_EVAL_REQ_URL, SignHelper.createUrl(encodeParam), credential.getAppid());
        url = url + "&signature=" + URLEncoder.encode(sign, "UTF-8");
        logger.debug(url);
        ConnectionProfile connectionProfile = new ConnectionProfile(sign, url, OralEvalConstant.DEFAULT_ORAL_EVAL_HOST, this.credential.getToken());
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
    public void write(byte[] data) {
        write(data, data.length);
    }

    /**
     * 发送数据
     *
     * @param data
     * @param length
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
            logger.error("fail to send binary,current_task_id:{},state:{}", recUuid, state, e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 结束语音识别:发送结束识别通知,接收服务端确认
     *
     * @throws Exception
     */
    public void stop() throws Exception {
        stop(OralEvalConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
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
