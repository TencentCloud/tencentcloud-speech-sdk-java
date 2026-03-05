package com.tencent.asrv2;

/**
 * 上下文数据项结构
 */
public class ContextDataItem {
    /**
     * 上下文文本内容
     */
    private String text;

    public ContextDataItem() {}

    public ContextDataItem(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
