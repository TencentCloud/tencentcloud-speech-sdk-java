package com.tencent.ttspodcast;

import com.google.gson.Gson;
import com.tencent.core.help.SignHelper;
import com.tencent.core.utils.SignBuilder;
import com.tencent.core.ws.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static com.tencent.core.ws.StateMachine.State.STATE_COMPLETE;

/**
 * 本接口服务采用 websocket 协议，同步返回合成音频数据及相关文本信息，达到“边合成边播放”的效果。
 */
public class TtsPodcastSynthesizer extends StateMachine {
    static Logger logger = LoggerFactory.getLogger(TtsPodcastSynthesizer.class);

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

    protected Connection conn;

    private Credential credential;
    private TtsPodcastRequest request;

    private SpeechClient client;

    private TtsPodcastListener listener;

    private List<TtsPodcastInputObject> inputObjects;

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public TtsPodcastRequest getRequest() {
        return request;
    }

    public void setRequest(TtsPodcastRequest request) {
        this.request = request;
    }

    public SpeechClient getClient() {
        return client;
    }

    public void setClient(SpeechClient client) {
        this.client = client;
    }

    public TtsPodcastListener getListener() {
        return listener;
    }

    public void setListener(TtsPodcastListener listener) {
        this.listener = listener;
    }

    public TtsPodcastSynthesizer(SpeechClient client, Credential credential, TtsPodcastRequest request,
            TtsPodcastListener listener) throws Exception {
        Optional.ofNullable(client).orElseThrow(() -> new RuntimeException("client cannot be null"));
        Optional.ofNullable(request).orElseThrow(() -> new RuntimeException("request cannot be null"));
        Optional.ofNullable(credential).orElseThrow(() -> new RuntimeException("credential cannot be null"));
        Optional.ofNullable(listener).orElseThrow(() -> new RuntimeException("listener cannot be null"));
        if (request.getSessionId() == null) {
            request.setSessionId(sessionId);
        }
        request.setAction(TtsPodcastConstant.PODCAST_REQUEST_ACTION);
        request.setAppId(Integer.valueOf(credential.getAppid()));
        this.request = request;
        this.credential = credential;
        this.client = client;
        this.listener = listener;
        this.inputObjects = new ArrayList<>();
        stopLatch = new CountDownLatch(1);
        startLatch = new CountDownLatch(1);
        listener.setSpeechSynthesizer(this);
    }

    /**
     * 添加一个 TEXT 类型的 InputObject
     */
    public void addText(String text) {
        TtsPodcastInputObject obj = new TtsPodcastInputObject();
        obj.setObjectType(TtsPodcastConstant.INPUT_OBJECT_TYPE_TEXT);
        obj.setText(text);
        inputObjects.add(obj);
    }

    /**
     * 添加一个 URL 类型的 InputObject
     */
    public void addUrl(String url) {
        TtsPodcastInputObject obj = new TtsPodcastInputObject();
        obj.setObjectType(TtsPodcastConstant.INPUT_OBJECT_TYPE_URL);
        obj.setUrl(url);
        inputObjects.add(obj);
    }

    /**
     * 添加一个 FILE 类型的 InputObject
     */
    public void addFile(String url, String fileFormat) {
        if (!TtsPodcastConstant.SUPPORT_FILE_FORMAT.contains(fileFormat)) {
            String msg = String.format("fileFormat %s is not support, support format: %s", fileFormat,
                    TtsPodcastConstant.SUPPORT_FILE_FORMAT);
            throw new RuntimeException(msg);
        }

        TtsPodcastInputObject obj = new TtsPodcastInputObject();
        obj.setObjectType(TtsPodcastConstant.INPUT_OBJECT_TYPE_FILE);
        obj.setUrl(url);
        obj.setFileFormat(fileFormat);
        inputObjects.add(obj);
    }

    /**
     * 请求服务端，超时则抛出异常
     *
     * @throws Exception
     */
    public void start() throws Exception {
        start(TtsPodcastConstant.DEFAULT_START_TIMEOUT_MILLISECONDS);
    }

    /**
     * 请求服务端，超时则抛出异常
     *
     * @param milliSeconds
     * @throws Exception
     */
    public void start(long milliSeconds) throws Exception {
        try {
            state.checkStart();
            logger.debug("sessionId:{},start change state from {} ", sessionId, state);
            state = state.start();
            logger.debug("sessionId:{},start change state to {} ", sessionId, state);
            request.setSecretid(credential.getSecretId());
            request.setTimestamp(System.currentTimeMillis() / 1000);
            request.setExpired(System.currentTimeMillis() / 1000 + 86400); // 1天后过期
            Map<String, Object> sortParamMap = request.toTreeMap();
            String signUrl = new StringBuilder().append(TtsPodcastConstant.DEFAULT_PODCAST_SIGN_PREFIX)
                    .append(SignHelper.createUrl(sortParamMap)).toString();
            logger.debug(signUrl);
            String sign = SignBuilder.base64_hmac_sha1(signUrl, credential.getSecretKey());
            String serverUrl = SignHelper.createUrl(SignHelper.encode(sortParamMap));
            String url = new StringBuilder().append(TtsPodcastConstant.DEFAULT_PODCAST_REQ_URL).append(serverUrl)
                    .append("&Signature=").append(URLEncoder.encode(sign, "UTF-8")).toString();
            logger.debug(url);
            ConnectionProfile connectionProfile = new ConnectionProfile(sign, url, TtsPodcastConstant.DEFAULT_HOST,
                    this.credential.getToken());
            this.conn = client.connect(connectionProfile, this.listener);
            Map<String, Long> network = new HashMap<>();
            network.put(Constant.CONNECTING_LATENCY_KEY, conn.getConnectingLatency());
            network.put(Constant.HANDSHAKE_LATENCY_KEY, conn.getHandshakeLatency());
            ctx.put(Constant.NETWORK_KEY, network);
            boolean result = startLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
            if (!result) {
                String msg = String.format("timeout after %d ms waiting for start confirmation.sessionId:%s,state:%s",
                        milliSeconds, sessionId, state);
                logger.error(msg);
                throw new Exception(msg);
            }
        } catch (Exception e) {
            close("start failed");
            throw e;
        }
    }

    public void process() throws Exception {
            sendInputObjects();
            complete();
    }

    private void sendInputObjects() {
        int idx = 0;
        for (TtsPodcastInputObject obj : inputObjects) {
            sendInputObject(obj, idx);
            idx++;
        }
    }

    private void sendInputObject(TtsPodcastInputObject obj, int idx) {
        String action = TtsPodcastConstant.getFlowingSpeechSynthesizer_ACTION_SYNTHESIS();
        if (state == STATE_COMPLETE) {
            logger.info("state is {} stop send", STATE_COMPLETE);
            return;
        }
        state.checkSend();
        try {
            String text = new Gson().toJson(obj);
            String msg = String.format("process[%d]: action=%s, data=%s", idx, action, text);
            logger.info(msg);
            conn.sendText(newWsRequestMessage(text, action));
        } catch (Exception e) {
            logger.error("fail to send text,current_task_id:{},state:{}", sessionId, state, e);
            close(String.format("send obj[%d]=%s failed", idx, obj));
            throw new RuntimeException(e);
        }
    }

    private String newWsRequestMessage(String text, String action) {
        Map<String, String> message = new HashMap<String, String>();
        message.put("session_id", request.getSessionId());
        message.put("message_id", UUID.randomUUID().toString());
        message.put("data", text);
        message.put("action", action);
        return new Gson().toJson(message);
    }

    /**
     * 结束合成:发送结束合成通知,接收服务端确认
     *
     * @throws Exception
     */
    public void complete() throws Exception {
        complete(TtsPodcastConstant.DEFAULT_FLOWING_STOP_TIMEOUT_MILLISECONDS);
    }

    /**
     * 结束合成:发送结束合成通知,接收服务端确认, 超时未返回则抛出异常
     *
     * @throws Exception
     */
    public void complete(long milliSeconds) throws Exception {
        try {
            if (state == STATE_COMPLETE) {
                logger.info("state is {} stop message is discarded", STATE_COMPLETE);
                return;
            }
            state.checkStop();
            state = state.stopSend();
            if (conn != null) {
                String end = newWsRequestMessage("", TtsPodcastConstant.getFlowingSpeechSynthesizer_ACTION_COMPLETE());
                logger.debug(end);
                conn.sendText(end);
                if (milliSeconds > 0) {
                    boolean result = stopLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
                    if (!result) {
                        String msg = String.format(
                                "timeout after %d ms waiting for stop confirmation.sessionId:%s,state:%s", milliSeconds,
                                sessionId, state);
                        logger.error(msg);
                        throw new Exception(msg);
                    }
                } else {
                    stopLatch.await();
                }
            }
        } catch (Exception e) {
            close("complete failed");
            throw e;
        }
    }

    /**
     * 关闭连接
     */
    public void close(String reason) {
        try {
            if (conn != null) {
                conn.close();
            }
            logger.info("close sessionId:{},reason:{}", sessionId, reason);
            if (TtsPodcastConstant.DEFAULT_FLOWING_CLOSE_SLEEP_MILLISECONDS > 0) {
                Thread.sleep(TtsPodcastConstant.DEFAULT_FLOWING_CLOSE_SLEEP_MILLISECONDS); // 休眠500ms 避免服务端连接未及时关闭
            }
        } catch (Exception e) {}
    }

    /**
     * 内部调用方法
     */
    void markReady() {
        logger.debug("sessionId:{},markReady change state from {} ", sessionId, state);
        state = state.send();
        logger.debug("sessionId:{},markReady change state to {} ", sessionId, state);
        if (this.startLatch != null) {
            this.startLatch.countDown();
        }
    }

    
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
