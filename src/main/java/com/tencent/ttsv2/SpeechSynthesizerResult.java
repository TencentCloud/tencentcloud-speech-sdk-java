package com.tencent.ttsv2;

import com.google.gson.annotations.SerializedName;
import com.tencent.tts.model.Subtitle;

public class SpeechSynthesizerResult {

    @SerializedName("subtitles")
    private SpeechSynthesizerSubtitle[] subtitles;

    public SpeechSynthesizerSubtitle[] getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(SpeechSynthesizerSubtitle[] subtitles) {
        this.subtitles = subtitles;
    }
}
