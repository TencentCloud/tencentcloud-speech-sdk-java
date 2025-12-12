package com.tencent.ttspodcast;

import com.google.gson.annotations.SerializedName;

/**
 * 实时语音合成响应
 */
public class TtsPodcastResponse {
    /**
     * 状态码，0代表正常，非0值表示发生错误
     */
    @SerializedName("code")
    private Integer code;

    /**
     * 错误说明，发生错误时显示这个错误发生的具体原因，随着业务发展或体验优化，此文本可能会经常保持变更或更新
     */
    @SerializedName("message")
    private String message;

    /**
     * 音频流唯一 id，由客户端在握手阶段生成并赋值在调用参数中
     */
    @SerializedName("session_id")
    private String sessionId;

    /**
     * 音频流唯一 id，由服务端在握手阶段自动生成
     */
    @SerializedName("request_id")
    private String requestId;
    
    /**
     * 本 message 唯一 id
     */
    @SerializedName("message_id")
    private String messageId;

    /**
     * 最新语音合成文本结果
     */
    @SerializedName("result")
    private TtsPodcastResult result;
    
    /**
     * READY 事件：该字段返回1时表示服务端已初始化，客户端可以开始发送输入文本的请求
     */
    @SerializedName("ready")
    private Integer ready;

    /**
     * FINAL 事件：该字段返回1时表示播客音频合成结束，客户端收到后需主动关闭 WebSocket 连接
     */
    @SerializedName("final")
    private Integer final_;

    /**
     * HEARTBEAT 事件：该字段返回1时表示心跳报文，客户端收到后可忽略
     */
    @SerializedName("heartbeat")
    private Integer heartbeat;


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public TtsPodcastResult getResult() {
        return result;
    }

    public void setResult(TtsPodcastResult result) {
        this.result = result;
    }

    public Integer getReady() {
        return ready;
    }

    public void setReady(Integer ready) {
        this.ready = ready;
    }

    public Integer getFinal_() {
        return final_;
    }

    public void setFinal_(Integer final_) {
        this.final_ = final_;
    }

    public Integer getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Integer heartbeat) {
        this.heartbeat = heartbeat;
    }
}
