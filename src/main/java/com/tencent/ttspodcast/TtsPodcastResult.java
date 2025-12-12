package com.tencent.ttspodcast;

import com.google.gson.annotations.SerializedName;

public class TtsPodcastResult {

    @SerializedName("scripts")
    private TtsPodcastScripts[] scripts;

    public TtsPodcastScripts[] getScripts() {
        return scripts;
    }

    public void setScripts(TtsPodcastScripts[] scripts) {
        this.scripts = scripts;
    }
}
