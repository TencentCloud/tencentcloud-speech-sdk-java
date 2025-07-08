package com.tencent.virtualnumber;

import com.google.gson.annotations.SerializedName;
import com.tencent.core.ws.CommonRequest;

/**
 * 虚拟号真人判定请求参数
 */
public class VirtualNumberRecognizerRequest extends CommonRequest {
    /**
     * 腾讯云注册账号的密钥 SecretId，可通过 API 密钥管理页面 获取
     */
    @SerializedName("secretid")
    private String secretid;

    /**
     * 当前 UNIX 时间戳，可记录发起 API 请求的时间。如果与当前时间相差过大，会引起签名过期错误。可以取值为当前请求的系统时间戳即可。
     */
    @SerializedName("timestamp")
    protected Long timestamp;

    /**
     * 签名的有效期，是一个符合 UNIX Epoch 时间戳规范的数值，单位为秒；Expired 必须大于 Timestamp 且 Expired - Timestamp 小于90天。
     */
    @SerializedName("expired")
    protected Long expired;

    /**
     * 随机正整数。用户需自行生成，最长 10 位。
     */
    @SerializedName("nonce")
    protected Integer nonce;
    /**
     * 语音编码方式，可选，默认值为4。1：pcm；4：speex(sp)；6：silk；8：mp3；10：opus；12：wav；14：m4a（每个分片须是一个完整的 m4a 音频）；16：aac
     */
    @SerializedName("voice_format")
    private Integer voiceFormat;

    /**
     * 音频流识别全局唯一标识，一个 websocket 连接对应一个，用户自己生成（推荐使用 uuid），最长128位
     */
    @SerializedName("voice_id")
    private String voiceId;

    /**
     * 接通等待时长，默认为30秒，最长60秒，识别超过等待时长返回未接通。
     */
    @SerializedName("wait_time")
    private Integer waitTime;

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

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public Integer getVoiceFormat() {
        return voiceFormat;
    }

    public void setVoiceFormat(Integer voiceFormat) {
        this.voiceFormat = voiceFormat;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Integer waitTime) {
        this.waitTime = waitTime;
    }
}
