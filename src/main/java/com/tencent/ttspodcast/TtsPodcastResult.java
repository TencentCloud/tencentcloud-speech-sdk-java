package com.tencent.ttspodcast;

import com.google.gson.annotations.SerializedName;

public class TtsPodcastResult {

    @SerializedName("scripts")
    private TtsPodcastScripts[] scripts;

    @SerializedName("context_id")
    private String contextId;

    @SerializedName("usage_tokens")
    private Integer[] usageTokens;

    public TtsPodcastScripts[] getScripts() {
        return scripts;
    }

    public String getContextId() {
        return contextId;
    }

    public Integer[] getUsageTokens() {
        return usageTokens;
    }

    public Integer getUsageTokensInput() {
        return getUsageTokens()[0];
    }

    public Integer getUsageTokensOutput() {
        return getUsageTokens()[1];
    }

    public void setScripts(TtsPodcastScripts[] scripts) {
        this.scripts = scripts;
    }
}
