package com.tencent.asrv2;

import com.google.gson.annotations.SerializedName;

/**
 * 实时语音识别 V2 响应结果，句子模式返回 sentences。
 */
public class RealtimeRecognitionResponseV2 {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("voice_id")
    private String voiceId;

    @SerializedName("message_id")
    private String messageId;

    @SerializedName("speaker_context_id")
    private String speakerContextId;

    @SerializedName("final")
    private int end;

    @SerializedName("sentences")
    private SentenceResult sentences;

    /**
     * 服务端返回的原始 JSON。仅当设置了扩展参数时填充，用于解析 SDK 未显式定义的字段。
     */
    private transient String rawMessage;

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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSpeakerContextId() {
        return speakerContextId;
    }

    public void setSpeakerContextId(String speakerContextId) {
        this.speakerContextId = speakerContextId;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public SentenceResult getSentences() {
        return sentences;
    }

    public void setSentences(SentenceResult sentences) {
        this.sentences = sentences;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }
}
