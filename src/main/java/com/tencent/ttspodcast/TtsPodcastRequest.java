package com.tencent.ttspodcast;

import com.google.gson.annotations.SerializedName;
import com.tencent.core.ws.CommonRequest;

import java.util.Map;

/**
 * 实时语音合成参数
 */
public class TtsPodcastRequest extends CommonRequest {
    /**
     * 调用接口名，取值为：TextToPodcastStreamAudioWS
     */
    @SerializedName("Action")
    private String action;

    @SerializedName("AppId")
    private Integer appId;

    /**
     * 腾讯云注册账号的密钥 SecretId，可通过 API 密钥管理页面 获取
     */
    @SerializedName("SecretId")
    private String secretid;

    /**
     * 当前 UNIX 时间戳，可记录发起 API 请求的时间。如果与当前时间相差过大，会引起签名过期错误。可以取值为当前请求的系统时间戳即可。
     */
    @SerializedName("Timestamp")
    protected Long timestamp;

    /**
     * 签名的有效期，是一个符合 UNIX Epoch 时间戳规范的数值，单位为秒；Expired 必须大于 Timestamp 且 Expired - Timestamp 小于90天。
     */
    @SerializedName("Expired")
    protected Long expired;


    /**
     * 是
     * String一次请求对应一个 SessionId，会原样返回，建议传入类似于 uuid 的字符串防止重复。
     */
    @SerializedName("SessionId")
    private String sessionId;

    /**
     * 是
     * Int 音频采样率：
     * 24000:24k（默认）
     */
    @SerializedName("SampleRate")
    private Integer sampleRate;

    /**
     * 否
     * String 返回音频格式：
     * pcm：
     * 返回二进制 pcm 音频
     */
    @SerializedName("Codec")
    private String codec;

    /**
     * 否
     * Int 发音人数量：
     * 0：默认（双人播客，音色 zixin/acan）
     * 1：单人播客，参数 Speaker1Voice 指定音色
     * 2：双人播客，参数 Speaker1Voice 和 Speaker2Voice 指定音色
     */
    @SerializedName("SpeakerNumber")
    private String speakerNumber;

    /**
     * 否
     * String：主持人1音色
     */
    @SerializedName("Speaker1Voice")
    private String speaker1Voice;

    /**
     * 否
     * String：主持人2音色
     */
    @SerializedName("Speaker2Voice")
    private String speaker2Voice;

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

    public String getSpeakerNumber() {
        return speakerNumber;
    }

    public void setSpeakerNumber(String speakerNumber) {
        this.speakerNumber = speakerNumber;
    }

    public String getSpeaker1Voice() {
        return speaker1Voice;
    }

    public void setSpeaker1Voice(String speaker1Voice) {
        this.speaker1Voice = speaker1Voice;
    }

    public String getSpeaker2Voice() {
        return speaker2Voice;
    }

    public void setSpeaker2Voice(String speaker2Voice) {
        this.speaker2Voice = speaker2Voice;
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
