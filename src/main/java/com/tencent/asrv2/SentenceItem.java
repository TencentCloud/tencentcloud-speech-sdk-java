package com.tencent.asrv2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 句子模式下的单个句子结构
 */
public class SentenceItem {

    /**
     * 句子文本
     */
    @SerializedName(value = "sentence")
    private String sentence;

    /**
     * 句子类型：0=未确定（中间结果，可能变化），1=确定（完整句子，不再变化）
     */
    @SerializedName(value = "sentence_type")
    private Integer sentenceType;

    /**
     * 句子 ID，从 1 递增
     */
    @SerializedName(value = "sentence_id")
    private Integer sentenceId;

    /**
     * 说话人 ID（仅开启话者分离时有意义）
     */
    @SerializedName(value = "speaker_id")
    private Integer speakerId;

    /**
     * 句子在整个音频流中的起始时间（毫秒）
     */
    @SerializedName(value = "start_time")
    private Long startTime;

    /**
     * 句子在整个音频流中的结束时间（毫秒）
     */
    @SerializedName(value = "end_time")
    private Long endTime;

    /**
     * 词级别时间戳列表（仅 word_info=100 时返回）
     */
    @SerializedName(value = "word_list")
    private List<SpeechRecognizerResult.Word> wordList;

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public Integer getSentenceType() {
        return sentenceType;
    }

    public void setSentenceType(Integer sentenceType) {
        this.sentenceType = sentenceType;
    }

    public Integer getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(Integer sentenceId) {
        this.sentenceId = sentenceId;
    }

    public Integer getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(Integer speakerId) {
        this.speakerId = speakerId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<SpeechRecognizerResult.Word> getWordList() {
        return wordList;
    }

    public void setWordList(List<SpeechRecognizerResult.Word> wordList) {
        this.wordList = wordList;
    }
}
