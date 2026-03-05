package com.tencent.speechtranslate;

import com.google.gson.annotations.SerializedName;
import com.tencent.core.ws.CommonRequest;

import java.util.Random;

/**
 * 语音翻译请求参数
 */
public class SpeechTranslatorRequest extends CommonRequest {

    /**
     * 腾讯云注册账号的密钥 SecretId，可通过 API 密钥管理页面获取
     */
    @SerializedName("secretid")
    private String secretid;

    /**
     * 当前 UNIX 时间戳，可记录发起 API 请求的时间
     */
    @SerializedName("timestamp")
    protected Long timestamp;

    /**
     * 签名的有效期，是一个符合 UNIX Epoch 时间戳规范的数值，单位为秒
     */
    @SerializedName("expired")
    protected Long expired;

    /**
     * 随机正整数。用户需自行生成，最长 10 位
     */
    @SerializedName("nonce")
    protected Integer nonce;

    /**
     * 音频流识别全局唯一标识，一个 websocket 连接对应一个，用户自己生成（推荐使用 uuid），最长128位
     */
    @SerializedName("voice_id")
    protected String voiceId;

    /**
     * 语音编码方式，可选，默认值为1
     * 1：pcm
     * 8：mp3
     * 12：wav
     */
    @SerializedName("voice_format")
    protected Integer voiceFormat;

    /**
     * 源语言，支持：
     * zh：中文
     * en：英文
     * ja：日语
     * ko：韩语
     * yue：粤语
     */
    @SerializedName("source")
    protected String source;

    /**
     * 目标语言，支持：
     * zh：中文
     * en：英文
     * ja：日语
     * ko：韩语
     * yue：粤语
     */
    @SerializedName("target")
    protected String target;

    /**
     * 翻译模型类型
     */
    @SerializedName("trans_model")
    protected String transModel;

    protected String getSecretid() {
        return secretid;
    }

    protected void setSecretid(String secretid) {
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

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public Integer getVoiceFormat() {
        return voiceFormat;
    }

    public void setVoiceFormat(Integer voiceFormat) {
        this.voiceFormat = voiceFormat;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTransModel() {
        return transModel;
    }

    public void setTransModel(String transModel) {
        this.transModel = transModel;
    }

    /**
     * 创建默认的语音翻译请求
     *
     * @return 默认配置的请求对象
     */
    public static SpeechTranslatorRequest init() {
        SpeechTranslatorRequest request = new SpeechTranslatorRequest();
        request.setVoiceFormat(1); // 默认 PCM 格式
        request.setNonce(new Random().nextInt(1000000));
        request.setSource("zh");
        request.setTarget("en");
        return request;
    }
}
