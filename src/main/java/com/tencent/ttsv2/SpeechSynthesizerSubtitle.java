package com.tencent.ttsv2;

import com.google.gson.annotations.SerializedName;

public class SpeechSynthesizerSubtitle {

    /**
     * Text information.
     */
    @SerializedName("Text")
    private String text;
    /**
     * Start timestamp of the TTS audio corresponding to the text.
     */
    @SerializedName("BeginTime")
    private Integer beginTime;

    /**
     * End timestamp of the TTS audio corresponding to the text.
     */
    @SerializedName("EndTime")
    private Integer endTime;
    /**
     * Start position of this character within the whole sentence.
     */
    @SerializedName("BeginIndex")
    private Integer beginIndex;

    /**
     * End position of this character within the whole sentence.
     */
    @SerializedName("EndIndex")
    private Integer endIndex;

    /**
     * Phoneme of this character.
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
