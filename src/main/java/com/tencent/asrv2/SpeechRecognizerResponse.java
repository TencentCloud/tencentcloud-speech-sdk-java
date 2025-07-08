package com.tencent.asrv2;

import com.google.gson.annotations.SerializedName;

/**
 * 实时语音识别请求结果
 */
public class SpeechRecognizerResponse {

    /**
     * 0：正常，其他，发生错误
     */
    @SerializedName("code")
    private int code;

    /**
     * message
     */
    @SerializedName("message")
    private String message;

    /**
     * voiceId
     */
    @SerializedName("voice_id")
    private String voiceId;

    /**
     * 0表示还在整个音频流的中间部分
     * 1表示是整个音频流的最后一个包。
     * 主要是在电信场景中，客户端发送完了之后，要知道是否返回的是最后一个包。
     */
    @SerializedName(value = "final")
    private int end;


    @SerializedName(value = "result")
    private SpeechRecognizerResult result;

    @SerializedName(value = "message_id")
    private String messageId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }


    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public SpeechRecognizerResult getResult() {
        return result;
    }

    public void setResult(SpeechRecognizerResult result) {
        this.result = result;
    }
}