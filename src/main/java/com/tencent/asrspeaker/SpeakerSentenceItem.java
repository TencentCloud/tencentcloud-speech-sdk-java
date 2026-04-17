package com.tencent.asrspeaker;

import com.google.gson.annotations.SerializedName;

/**
 * 句子模式下的单个句子
 */
public class SpeakerSentenceItem {

    @SerializedName("sentence")
    private String sentence;

    /**
     * 0：非稳态（中间结果，可能变化）；1：稳态（最终结果，不再变化）
     */
    @SerializedName("sentence_type")
    private int sentenceType;

    @SerializedName("sentence_id")
    private int sentenceId;

    @SerializedName("speaker_id")
    private int speakerId;

    /** 毫秒 */
    @SerializedName("start_time")
    private long startTime;

    /** 毫秒 */
    @SerializedName("end_time")
    private long endTime;

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public int getSentenceType() {
        return sentenceType;
    }

    public void setSentenceType(int sentenceType) {
        this.sentenceType = sentenceType;
    }

    public int getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    public int getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(int speakerId) {
        this.speakerId = speakerId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
