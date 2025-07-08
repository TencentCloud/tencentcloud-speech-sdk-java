package com.tencent.soe;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 单词评分细则
 */
public class WordRsp {
    /**
     * 当前单词语音起始时间点，单位为ms，该字段段落模式下无意义。
     */
    @SerializedName("MemBeginTime")
    private long memBeginTime;
    /**
     * 当前单词语音终止时间点，单位为ms，该字段段落模式下无意义。
     */
    @SerializedName("MemEndTime")
    private long memEndTime;
    /**
     * 单词发音准确度，取值范围[-1, 100]，当取-1时指完全不匹配
     */
    @SerializedName("PronAccuracy")
    private double pronAccuracy;
    /**
     * 单词发音流利度，取值范围[0, 1]
     */
    @SerializedName("PronFluency")
    private double pronFluency;
    /**
     * 参考词，目前为保留字段。
     */
    @SerializedName("ReferenceWord")
    private String referenceWord;
    /**
     * 当前词
     */
    @SerializedName("Word")
    private String word;
    /**
     * 当前词与输入语句的匹配情况，0：匹配单词、1：新增单词、2：缺少单词、3：错读的词、4：未录入单词。
     */
    @SerializedName("MatchTag")
    private long matchTag;
    /**
     * 主题词命中标志，0表示没命中，1表示命中
     */
    @SerializedName("KeywordTag")
    private long keywordTag;
    /**
     * 音节评估详情
     */
    @SerializedName("PhoneInfos")
    private List<PhoneInfo> phoneInfos;
    /**
     * 声调检测结果
     */
    @SerializedName("Tone")
    private Tone tone;

    public long getMemBeginTime() {
        return memBeginTime;
    }

    public void setMemBeginTime(long memBeginTime) {
        this.memBeginTime = memBeginTime;
    }

    public long getMemEndTime() {
        return memEndTime;
    }

    public void setMemEndTime(long memEndTime) {
        this.memEndTime = memEndTime;
    }

    public double getPronAccuracy() {
        return pronAccuracy;
    }

    public void setPronAccuracy(double pronAccuracy) {
        this.pronAccuracy = pronAccuracy;
    }

    public double getPronFluency() {
        return pronFluency;
    }

    public void setPronFluency(double pronFluency) {
        this.pronFluency = pronFluency;
    }

    public String getReferenceWord() {
        return referenceWord;
    }

    public void setReferenceWord(String referenceWord) {
        this.referenceWord = referenceWord;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getMatchTag() {
        return matchTag;
    }

    public void setMatchTag(long matchTag) {
        this.matchTag = matchTag;
    }

    public long getKeywordTag() {
        return keywordTag;
    }

    public void setKeywordTag(long keywordTag) {
        this.keywordTag = keywordTag;
    }

    public List<PhoneInfo> getPhoneInfos() {
        return phoneInfos;
    }

    public void setPhoneInfos(List<PhoneInfo> phoneInfos) {
        this.phoneInfos = phoneInfos;
    }

    public Tone getTone() {
        return tone;
    }

    public void setTone(Tone tone) {
        this.tone = tone;
    }
}
