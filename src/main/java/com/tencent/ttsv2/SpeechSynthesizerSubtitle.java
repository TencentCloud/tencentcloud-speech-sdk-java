package com.tencent.ttsv2;

import com.google.gson.annotations.SerializedName;

public class SpeechSynthesizerSubtitle {

    /**
     * ⽂本信息
     */
    @SerializedName("Text")
    private String text;
    /**
     * ⽂本对应tts语⾳开始时间戳
     */
    @SerializedName("BeginTime")
    private Integer beginTime;

    /**
     * ⽂本对应tts语⾳结束时间戳
     */
    @SerializedName("EndTime")
    private Integer endTime;
    /**
     * 该字在整句中的开始位置
     */
    @SerializedName("BeginIndex")
    private Integer beginIndex;

    /**
     * 该字在整句中的结束位置
     */
    @SerializedName("EndIndex")
    private Integer endIndex;

    /**
     * 该字的音素
     */
    @SerializedName("Phoneme")
    private String phoneme;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Integer beginTime) {
        this.beginTime = beginTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public Integer getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(Integer beginIndex) {
        this.beginIndex = beginIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public String getPhoneme() {
        return phoneme;
    }

    public void setPhoneme(String phoneme) {
        this.phoneme = phoneme;
    }
}
