package com.tencent.asrv2;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PromptItem {
    /**
     * 提示类型（自定义）
     */
    @SerializedName("context_type")
    private String contextType;

    /**
     * 上下文数据数组
     */
    @SerializedName("context_data")
    private List<ContextDataItem> contextData;

    public PromptItem() {
        this.contextData = new ArrayList<>();
    }

    public String getContextType() {
        return contextType;
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }

    public List<ContextDataItem> getContextData() {
        return contextData;
    }

    public void setContextData(List<ContextDataItem> contextData) {
        this.contextData = contextData;
    }
}
