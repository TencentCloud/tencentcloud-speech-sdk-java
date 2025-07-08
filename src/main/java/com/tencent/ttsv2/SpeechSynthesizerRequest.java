package com.tencent.ttsv2;

import com.google.gson.annotations.SerializedName;
import com.tencent.core.ws.CommonRequest;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * 实时语音合成参数
 */
public class SpeechSynthesizerRequest extends CommonRequest {
    /**
     * 调用接口名，取值为：TextToStreamAudioWS
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
     * 合成语音的源文本，按UTF-8编码统一计算。中文最大支持 600 个汉字（全角标点符号算一个汉字）；英文最大支持 1800 个字母（半角标点符号算一个字母）。
     */
    @SerializedName("Text")
    private String text;


    /**
     * 否
     * Int详见：语音合成 API 文档中的 VoiceType 参数。
     */
    @SerializedName("VoiceType")
    private Integer voiceType;

    /**
     * 否
     * Float音量大小，范围：[0，10]，分别对应11个等级的音量，默认值为0，代表正常音量。没有静音选项。
     */
    @SerializedName("Volume")
    private Float volume;


    /**
     * 否
     * Int语速，范围：[-2，2]分别对应不同语速：
     * -2代表0.6倍
     * -1代表0.8倍
     * 0代表1.0倍（默认）
     * 1代表1.2倍
     * 2代表1.5倍
     * 输入除以上整数之外的其他参数不生效，按默认值处理。
     * 若需要更细化的语速档次，可以保留小数点一位，如-1.1 0.5 1.7等
     */
    @SerializedName("Speed")
    private Float speed;
    /**
     * 否
     * Int 音频采样率：
     * 16000:16k（默认）
     * 8000:8k
     */
    @SerializedName("SampleRate")
    private Integer sampleRate;


    /**
     * 否
     * String 返回音频格式：
     * opus：
     * 返回多段含 opus
     * 压缩分片音频，数据量小，建议使用（默认）。
     * pcm：
     * 返回二进制 pcm
     * 音频，使用简单，但数据量大。
     */
    @SerializedName("Codec")
    private String codec;

    /**
     * 是否开启时间戳功能，默认为false。
     */
    @SerializedName("EnableSubtitle")
    private Boolean enableSubtitle;

    /**
     * 控制合成音频的情感，仅支持多情感音色使用。
     * neutral(中性)、sad(悲伤)、happy(高兴)、angry(生气)、fear(恐惧)、news(新闻)、story(故事)、radio(广播)、poetry(诗歌)、call(客服)
     */
    @SerializedName("EmotionCategory")
    private String emotionCategory;
    /**
     * 控制合成音频情感程度，取值范围为[50,200],默认为100；只有 EmotionCategory 不为空时生效；
     */
    @SerializedName("EmotionIntensity")
    private Integer emotionIntensity;

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
