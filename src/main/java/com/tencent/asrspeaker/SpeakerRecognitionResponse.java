package com.tencent.asrspeaker;

import com.google.gson.annotations.SerializedName;

/**
 * 实时语音识别（句子模式）响应
 */
public class SpeakerRecognitionResponse {

    /** 0 表示正常，其他表示错误 */
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

    /**
     * 0 表示还在音频流中间，1 表示是最后一个包
     */
    @SerializedName("final")
    private int end;

    @SerializedName("sentences")
    private SpeakerSentences sentences;

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

    public SpeakerSentences getSentences() {
        return sentences;
    }

    public void setSentences(SpeakerSentences sentences) {
        this.sentences = sentences;
    }
}
