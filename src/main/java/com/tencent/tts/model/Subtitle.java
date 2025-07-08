package com.tencent.tts.model;

public class Subtitle {

    /**
     * ⽂本信息
     */
    private String Text;
    /**
     * ⽂本对应tts语⾳开始时间戳
     */
    private Integer BeginTime;

    /**
     * ⽂本对应tts语⾳结束时间戳
     */
    private Integer EndTime;
    /**
     * 该字在整句中的开始位置
     */
    private Integer BeginIndex;

    /**
     * 该字在整句中的结束位置
     */
    private Integer EndIndex;

    /**
     * 该字的音素
     */
    private String Phoneme;

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public Integer getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(Integer beginTime) {
        BeginTime = beginTime;
    }

    public Integer getEndTime() {
        return EndTime;
    }

    public void setEndTime(Integer endTime) {
        EndTime = endTime;
    }

    public Integer getBeginIndex() {
        return BeginIndex;
    }

    public void setBeginIndex(Integer beginIndex) {
        BeginIndex = beginIndex;
    }

    public Integer getEndIndex() {
        return EndIndex;
    }

    public void setEndIndex(Integer endIndex) {
        EndIndex = endIndex;
    }

    public String getPhoneme() {
        return Phoneme;
    }

    public void setPhoneme(String phoneme) {
        Phoneme = phoneme;
    }
}
