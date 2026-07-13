package com.tencent.asrv2;

import com.google.gson.Gson;
import com.tencent.core.utils.SignBuilder;
import com.tencent.core.ws.Credential;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 实时语音识别 V2，句子模式，话者分离作为可选开关（默认关闭）。
 */
public class RealtimeRecognizerV2 {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeRecognizerV2.class);

    private static final String PROTOCOL = "wss";
    private static final String HOST = "asr.cloud.tencent.com";
    private static final String PATH = "/asr/v2/";
    private static final int DEFAULT_TIMEOUT_MILLISECONDS = 15000;

    public static final int AUDIO_FORMAT_PCM = 1;
    public static final int AUDIO_FORMAT_SPEEX = 4;
    public static final int AUDIO_FORMAT_SILK = 6;
    public static final int AUDIO_FORMAT_MP3 = 8;
    public static final int AUDIO_FORMAT_OPUS = 10;
    public static final int AUDIO_FORMAT_WAV = 12;
    public static final int AUDIO_FORMAT_M4A = 14;
    public static final int AUDIO_FORMAT_AAC = 16;

    private final String appId;
    private final Credential credential;
    private final String engineModelType;
    private final RealtimeRecognitionListenerV2 listener;

    private int voiceFormat = 1;
    private int needVad = 1;
    private int convertNumMode = 1;
    private int reinforceHotword = 0;
    private int vadSilenceTime = 0;
    private float noiseThreshold = 0;
    private String hotwordId = "";
    private String hotwordList = "";
    private String customizationId = "";
    private String replaceTextId = "";
    private int sentenceStrategy = 1;
    private int domain = 0;

    private int speakerDiarization = 0;
    private int enableSpeakerContext = 0;
    private String speakerContextId = "";
    private int languageJudgment = 0;
    private int emotionRecognition = 0;

    private final Map<String, String> extraParams = new HashMap<>();

    private String voiceId = "";

    private final OkHttpClient client;
    private WebSocket webSocket;

    private final CountDownLatch startLatch = new CountDownLatch(1);
    private final CountDownLatch stopLatch = new CountDownLatch(1);

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final AtomicBoolean failed = new AtomicBoolean(false);
    private final AtomicBoolean completed = new AtomicBoolean(false);

    public RealtimeRecognizerV2(String appId, Credential credential, String engineModelType,
                                RealtimeRecognitionListenerV2 listener) {
        this.appId = appId;
        this.credential = credential;
        this.engineModelType = engineModelType;
        this.listener = listener;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
    }

    public void start() throws Exception {
        start(DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public void start(long milliSeconds) throws Exception {
        if (closed.get()) {
            throw new IllegalStateException("recognizer has been stopped, please create a new instance");
        }
        if (started.get()) {
            throw new IllegalStateException("recognizer is already started");
        }
        if (voiceId == null || voiceId.isEmpty()) {
            voiceId = UUID.randomUUID().toString();
        }
        String signUrl = buildSignUrl();
        String sign = SignBuilder.base64_hmac_sha1(signUrl, credential.getSecretKey());
        String url = PROTOCOL + "://" + signUrl + "&signature=" + URLEncoder.encode(sign, "UTF-8");
        logger.debug(url);

        Request request = new Request.Builder().url(url).build();
        this.webSocket = client.newWebSocket(request, createWebSocketListener());
        started.set(true);

        boolean result = startLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
        if (!result) {
            close();
            throw new Exception(String.format("timeout after %d ms waiting for start confirmation, voice_id:%s",
                    milliSeconds, voiceId));
        }
        if (failed.get()) {
            throw new Exception(String.format("recognizer start failed, voice_id:%s", voiceId));
        }
    }

    public void write(byte[] data) {
        if (!started.get() || closed.get()) {
            return;
        }
        webSocket.send(ByteString.of(data));
    }

    public void write(byte[] data, int length) {
        if (!started.get() || closed.get()) {
            return;
        }
        webSocket.send(ByteString.of(data, 0, length));
    }

    public void stop() throws Exception {
        stop(DEFAULT_TIMEOUT_MILLISECONDS);
    }

    public void stop(long milliSeconds) throws Exception {
        if (!started.get() || closed.get()) {
            return;
        }
        Map<String, String> end = new HashMap<>();
        end.put("type", "end");
        webSocket.send(new Gson().toJson(end));
        boolean result = stopLatch.await(milliSeconds, TimeUnit.MILLISECONDS);
        close();
        if (!result) {
            throw new Exception(String.format("timeout after %d ms waiting for stop confirmation, voice_id:%s",
                    milliSeconds, voiceId));
        }
    }

    public void close() {
        if (closed.compareAndSet(false, true)) {
            started.set(false);
            if (webSocket != null) {
                webSocket.cancel();
            }
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
            if (client.cache() != null) {
                try {
                    client.cache().close();
                } catch (Exception e) {
                    logger.warn("close okhttp cache failed", e);
                }
            }
        }
    }

    private WebSocketListener createWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if (text == null || text.trim().isEmpty()) {
                    return;
                }
                logger.debug("on message:{}", text);
                RealtimeRecognitionResponseV2 response = new Gson().fromJson(text, RealtimeRecognitionResponseV2.class);
                if (!extraParams.isEmpty()) {
                    response.setRawMessage(text);
                }
                if (response.getVoiceId() == null || response.getVoiceId().isEmpty()) {
                    response.setVoiceId(voiceId);
                }
                if (response.getCode() != 0) {
                    onError(response, new Exception(String.format("voice_id:%s, code:%d, message:%s",
                            voiceId, response.getCode(), response.getMessage())));
                    return;
                }
                if (startLatch.getCount() > 0) {
                    if (response.getSpeakerContextId() != null && !response.getSpeakerContextId().isEmpty()) {
                        speakerContextId = response.getSpeakerContextId();
                    }
                    RealtimeRecognitionResponseV2 startResponse = newResponse(0, "success");
                    startResponse.setMessageId(voiceId + "-RecognitionStart");
                    startResponse.setSpeakerContextId(speakerContextId);
                    startLatch.countDown();
                    listener.onRecognitionStart(startResponse);
                    return;
                }
                if (response.getEnd() == 1) {
                    completed.set(true);
                    listener.onSentenceEnd(response);
                    stopLatch.countDown();
                    return;
                }
                listener.onRecognitionSentences(response);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                String message = t.getMessage() != null ? t.getMessage() : t.toString();
                onError(newResponse(-1, message), new Exception(t));
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                logger.debug("connection closed:{},code:{}", reason, code);
            }
        };
    }

    private void onError(RealtimeRecognitionResponseV2 response, Exception e) {
        if (completed.get() || closed.get()) {
            return;
        }
        if (failed.compareAndSet(false, true)) {
            listener.onFail(response, e);
            startLatch.countDown();
            stopLatch.countDown();
        }
    }

    private String buildSignUrl() {
        Map<String, Object> query = new TreeMap<>();
        query.put("secretid", credential.getSecretId());
        long timestamp = System.currentTimeMillis() / 1000;
        query.put("timestamp", timestamp);
        query.put("expired", timestamp + 24 * 60 * 60);
        query.put("nonce", timestamp);

        query.put("engine_model_type", engineModelType);
        query.put("voice_id", voiceId);
        query.put("voice_format", voiceFormat);
        query.put("needvad", needVad);

        query.put("result_mod", 1);
        query.put("sentence_strategy", sentenceStrategy);

        query.put("speaker_diarization", speakerDiarization);
        query.put("enable_speaker_context", enableSpeakerContext);
        query.put("speaker_context_id", speakerContextId);
        query.put("language_judgment", languageJudgment);
        query.put("emotion_recognition", emotionRecognition);

        if (hotwordId != null && !hotwordId.isEmpty()) {
            query.put("hotword_id", hotwordId);
        }
        if (hotwordList != null && !hotwordList.isEmpty()) {
            query.put("hotword_list", hotwordList);
        }
        if (customizationId != null && !customizationId.isEmpty()) {
            query.put("customization_id", customizationId);
        }
        if (replaceTextId != null && !replaceTextId.isEmpty()) {
            query.put("replace_text_id", replaceTextId);
        }
        if (domain > 0) {
            query.put("domain", domain);
        }

        query.put("convert_num_mode", convertNumMode);
        query.put("reinforce_hotword", reinforceHotword);
        if (vadSilenceTime > 0) {
            query.put("vad_silence_time", vadSilenceTime);
        }
        if (noiseThreshold != 0) {
            query.put("noise_threshold", String.format(Locale.US, "%.3f", noiseThreshold));
        }

        for (Map.Entry<String, String> entry : extraParams.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isEmpty()) {
                continue;
            }
            query.put(entry.getKey(), entry.getValue());
        }

        StringBuilder sb = new StringBuilder();
        sb.append(HOST).append(PATH).append(appId).append("?");
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private RealtimeRecognitionResponseV2 newResponse(int code, String message) {
        RealtimeRecognitionResponseV2 response = new RealtimeRecognitionResponseV2();
        response.setCode(code);
        response.setMessage(message);
        response.setVoiceId(voiceId);
        return response;
    }

    public void setExtraParam(String key, String value) {
        if (key == null || key.isEmpty()) {
            return;
        }
        extraParams.put(key, value);
    }

    public void setExtraParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isEmpty()) {
                continue;
            }
            extraParams.put(entry.getKey(), entry.getValue());
        }
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public void setVoiceFormat(int voiceFormat) {
        this.voiceFormat = voiceFormat;
    }

    public void setNeedVad(int needVad) {
        this.needVad = needVad;
    }

    public void setConvertNumMode(int convertNumMode) {
        this.convertNumMode = convertNumMode;
    }

    public void setReinforceHotword(int reinforceHotword) {
        this.reinforceHotword = reinforceHotword;
    }

    public void setVadSilenceTime(int vadSilenceTime) {
        this.vadSilenceTime = vadSilenceTime;
    }

    public void setNoiseThreshold(float noiseThreshold) {
        this.noiseThreshold = noiseThreshold;
    }

    public void setHotwordId(String hotwordId) {
        this.hotwordId = hotwordId;
    }

    public void setHotwordList(String hotwordList) {
        this.hotwordList = hotwordList;
    }

    public void setCustomizationId(String customizationId) {
        this.customizationId = customizationId;
    }

    public void setReplaceTextId(String replaceTextId) {
        this.replaceTextId = replaceTextId;
    }

    public void setSentenceStrategy(int sentenceStrategy) {
        this.sentenceStrategy = sentenceStrategy;
    }

    public void setDomain(int domain) {
        this.domain = domain;
    }

    public void setSpeakerDiarization(int speakerDiarization) {
        this.speakerDiarization = speakerDiarization;
    }

    public void setEnableSpeakerContext(int enableSpeakerContext) {
        this.enableSpeakerContext = enableSpeakerContext;
    }

    public String getSpeakerContextId() {
        return speakerContextId;
    }

    public void setSpeakerContextId(String speakerContextId) {
        this.speakerContextId = speakerContextId;
    }

    public void setLanguageJudgment(int languageJudgment) {
        this.languageJudgment = languageJudgment;
    }

    public void setEmotionRecognition(int emotionRecognition) {
        this.emotionRecognition = emotionRecognition;
    }
}
