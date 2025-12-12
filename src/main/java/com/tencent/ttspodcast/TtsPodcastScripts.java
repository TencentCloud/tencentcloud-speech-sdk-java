package com.tencent.ttspodcast;

import com.google.gson.annotations.SerializedName;

public class TtsPodcastScripts {

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
     * 该段序号，从0开始
     */
    @SerializedName("Index")
    private Integer Index;

    /**
     * 该段的说话人，如：主持人1、主持人2
     */
    @SerializedName("Speaker")
    private String Speaker;


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

    public Integer getIndex() {
        return Index;
    }

    public void setIndex(Integer index) {
        Index = index;
    }

    public String getSpeaker() {
        return Speaker;
    }

    public void setSpeaker(String speaker) {
        Speaker = speaker;
    }
}
