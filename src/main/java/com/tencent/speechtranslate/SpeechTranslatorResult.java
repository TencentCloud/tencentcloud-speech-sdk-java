package com.tencent.speechtranslate;

import com.google.gson.annotations.SerializedName;

/**
 * 语音翻译结果详情
 */
public class SpeechTranslatorResult {

    /**
     * 源语言
     */
    @SerializedName("source")
    private String source;

    /**
     * 目标语言
     */
    @SerializedName("target")
    private String target;

    /**
     * 源语言识别文本
     */
    @SerializedName("source_text")
    private String sourceText;

    /**
     * 翻译后的目标语言文本
     */
    @SerializedName("target_text")
    private String targetText;

    /**
     * 当前一段话结果在整个音频流中的起始时间（毫秒）
     */
    @SerializedName("start_time")
    private Long startTime;

    /**
     * 当前一段话结果在整个音频流中的结束时间（毫秒）
     */
    @SerializedName("end_time")
    private Long endTime;

    /**
     * 是否是句子结束
     */
    @SerializedName("sentence_end")
    private Boolean sentenceEnd;

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

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTargetText() {
        return targetText;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
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

    public Boolean getSentenceEnd() {
        return sentenceEnd;
    }

    public void setSentenceEnd(Boolean sentenceEnd) {
        this.sentenceEnd = sentenceEnd;
    }
}
