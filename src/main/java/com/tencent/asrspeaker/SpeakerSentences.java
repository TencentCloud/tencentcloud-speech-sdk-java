package com.tencent.asrspeaker;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 句子列表
 */
public class SpeakerSentences {

    @SerializedName("sentence_list")
    private List<SpeakerSentenceItem> sentenceList;

    public List<SpeakerSentenceItem> getSentenceList() {
        return sentenceList;
    }

    public void setSentenceList(List<SpeakerSentenceItem> sentenceList) {
        this.sentenceList = sentenceList;
    }
}
