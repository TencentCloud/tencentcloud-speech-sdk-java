package com.tencent.speechtranslate;

import com.google.gson.annotations.SerializedName;

/**
 * 语音翻译响应结果
 */
public class SpeechTranslatorResponse {

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
     * voiceId 语音标识
     */
    @SerializedName("voice_id")
    private String voiceId;

    /**
     * sentenceId 句子标识
     */
    @SerializedName("sentence_id")
    private String sentenceId;

    /**
     * 0表示还在整个音频流的中间部分
     * 1表示是整个音频流的最后一个包
     */
    @SerializedName(value = "final")
    private int end;

    /**
     * 翻译结果
     */
    @SerializedName(value = "result")
    private SpeechTranslatorResult result;

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

    public String getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(String sentenceId) {
        this.sentenceId = sentenceId;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public SpeechTranslatorResult getResult() {
        return result;
    }

    public void setResult(SpeechTranslatorResult result) {
        this.result = result;
    }
}
