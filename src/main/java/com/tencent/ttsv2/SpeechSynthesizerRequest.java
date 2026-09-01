package com.tencent.ttsv2;

import com.google.gson.annotations.SerializedName;
import com.tencent.core.ws.CommonRequest;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * Real-time speech synthesis request parameters.
 */
public class SpeechSynthesizerRequest extends CommonRequest {
    /**
     * API action name. The value is: TextToStreamAudioWS.
     */
    @SerializedName("Action")
    private String action;

    @SerializedName("AppId")
    private Integer appId;

    /**
     * SecretId of the Tencent Cloud registered account, which can be obtained from the API key management page.
     */
    @SerializedName("SecretId")
    private String secretid;

    /**
     * The current UNIX timestamp, which records the time the API request is initiated. If it differs too much from the current time, a signature-expired error will occur. Simply use the system timestamp of the current request.
     */
    @SerializedName("Timestamp")
    protected Long timestamp;

    /**
     * The signature validity period, a value conforming to the UNIX Epoch timestamp specification, in seconds. Expired must be greater than Timestamp, and (Expired - Timestamp) must be less than 90 days.
     */
    @SerializedName("Expired")
    protected Long expired;


    /**
     * Required.
     * String: each request corresponds to one SessionId and it will be returned as-is. A UUID-like string is recommended to prevent duplication.
     */
    @SerializedName("SessionId")
    private String sessionId;

    /**
     * The source text of the synthesized speech, calculated uniformly in UTF-8 encoding. Chinese supports up to 600 Chinese characters (a full-width punctuation mark counts as one Chinese character). English supports up to 1800 letters (a half-width punctuation mark counts as one letter).
     */
    @SerializedName("Text")
    private String text;


    /**
     * Optional.
     * Int: see the VoiceType parameter in the Speech Synthesis API documentation for details.
     */
    @SerializedName("VoiceType")
    private Integer voiceType;

    /**
     * Optional.
     * Float: volume level, range [0, 10], corresponding to 11 volume levels. The default value is 0, representing normal volume. There is no mute option.
     */
    @SerializedName("Volume")
    private Float volume;


    /**
     * Optional.
     * Int: speech rate, range [-2, 2], corresponding to different rates:
     * -2 stands for 0.6x
     * -1 stands for 0.8x
     * 0 stands for 1.0x (default)
     * 1 stands for 1.2x
     * 2 stands for 1.5x
     * Any integer other than the ones above is ignored and treated as default.
     * If a finer speech-rate step is needed, one decimal digit is supported, e.g. -1.1, 0.5, 1.7, etc.
     */
    @SerializedName("Speed")
    private Float speed;
    /**
     * Optional.
     * Int: audio sample rate:
     * 16000: 16k (default)
     * 8000: 8k
     */
    @SerializedName("SampleRate")
    private Integer sampleRate;


    /**
     * Optional.
     * String: returned audio format:
     * opus:
     * returns multiple segments of opus-compressed sharded audio;
     * the data size is small, recommended (default).
     * pcm:
     * returns binary pcm audio;
     * easy to use, but the data size is large.
     */
    @SerializedName("Codec")
    private String codec;

    /**
     * Whether to enable timestamps. Default is false.
     */
    @SerializedName("EnableSubtitle")
    private Boolean enableSubtitle;

    /**
     * Controls the emotion of the synthesized audio. Only supported by multi-emotion voice types.
     * neutral (neutral), sad (sad), happy (happy), angry (angry), fear (fear), news (news), story (story), radio (radio), poetry (poetry), call (customer service).
     */
    @SerializedName("EmotionCategory")
    private String emotionCategory;
    /**
     * Controls the emotion intensity of the synthesized audio. Range: [50, 200], default 100. Only takes effect when EmotionCategory is not empty.
     */
    @SerializedName("EmotionIntensity")
    private Integer emotionIntensity;

    /**
     * Account area.
     * 0: China account (default)
     * 1: International account
     */
    @SerializedName("AccountArea")
    private String accountArea;

    public String getSecretid() {
        return secretid;
    }

    public void setSecretid(String secretid) {
        this.secretid = secretid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getExpired() {
        return expired;
    }

    public void setExpired(Long expired) {
        this.expired = expired;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(Integer voiceType) {
        this.voiceType = voiceType;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public Boolean getEnableSubtitle() {
        return enableSubtitle;
    }

    public void setEnableSubtitle(Boolean enableSubtitle) {
        this.enableSubtitle = enableSubtitle;
    }

    public String getEmotionCategory() {
        return emotionCategory;
    }

    public void setEmotionCategory(String emotionCategory) {
        this.emotionCategory = emotionCategory;
    }

    public Integer getEmotionIntensity() {
        return emotionIntensity;
    }

    public void setEmotionIntensity(Integer emotionIntensity) {
        this.emotionIntensity = emotionIntensity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getAccountArea() {
        return accountArea;
    }

    public void setAccountArea(String accountArea) {
        this.accountArea = accountArea;
    }

    public Map<String, Object> toTreeMap() {
        Map<String, Object> src = super.toTreeMap();
        if (src != null) {
            for (String key : src.keySet()) {
                if (src.get(key) instanceof Float) {
                    Float value = Float.parseFloat(src.get(key).toString());
                    if (value != null && value.intValue() == value) {
                        src.put(key, value.intValue());
                    } else {
                        src.put(key, value);
                    }
                }
            }
        }
        return src;
    }
}
