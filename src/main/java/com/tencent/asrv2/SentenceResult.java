package com.tencent.asrv2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 句子模式返回结果（result_mod=1 时，response 中 sentences 字段的结构）
 */
public class SentenceResult {

    @SerializedName(value = "sentence_list")
    private List<SentenceItem> sentenceList;

    public List<SentenceItem> getSentenceList() {
        return sentenceList;
    }

    public void setSentenceList(List<SentenceItem> sentenceList) {
        this.sentenceList = sentenceList;
    }
}
