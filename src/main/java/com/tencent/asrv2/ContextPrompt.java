package com.tencent.asrv2;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 上下文提示结构
 */
public class ContextPrompt {
    @SerializedName("context_type")
    private final String contextType  = "context";

    /**
     * 动态热词列表，如 "腾讯云|10,语音识别|5,ASR|11"
     */
    @SerializedName("hotword_list")
    private String hotwordList;

    /**
     * 提示项数组
     */
    @SerializedName("prompt")
    private List<PromptItem> prompt;

    public ContextPrompt() {
        this.prompt = new ArrayList<>();
    }

    public String getHotwordList() {
        return hotwordList;
    }

    public void setHotwordList(String hotwordList) {
        this.hotwordList = hotwordList;
    }

    public List<PromptItem> getPrompt() {
        return prompt;
    }

    public void setPrompt(List<PromptItem> prompt) {
        this.prompt = prompt;
    }
}
