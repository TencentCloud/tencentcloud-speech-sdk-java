/*
 * Copyright (c) 2017-2018 THL A29 Limited, a Tencent company. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tencent.soe;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 语音过程中断句的中间结果
 */
public class SentenceInfo {
    /**
     * 建议评分，取值范围[0,100]，评分方式为建议评分 = 准确度（PronAccuracyfloat） 完整度（PronCompletionfloat）（2 - 完整度（PronCompletionfloat）），如若评分策略不符合请参考Words数组中的详细分数自定义评分逻辑。
     */
    @SerializedName("SuggestedScore")
    private double suggestedScore;
    /**
     * 发音精准度，取值范围[-1, 100]，当取-1时指完全不匹配，当为句子模式时，是所有已识别单词准确度的加权平均值，在reftext中但未识别出来的词不计入分数中。
     */
    @SerializedName("PronAccuracy")
    private double pronAccuracy;
    /**
     * 发音流利度，取值范围[0, 1]，当为词模式时，取值无意义；当为流式模式且请求中IsEnd未置1时，取值无意义
     */
    @SerializedName("PronFluency")
    private double pronFluency;
    /**
     * 发音完整度，取值范围[0, 1]，当为词模式时，取值无意义；当为流式模式且请求中IsEnd未置1时，取值无意义
     */
    @SerializedName("PronCompletion")
    private double pronCompletion;
    /**
     * 详细发音评估结果
     */
    @SerializedName("Words")
    private List<WordRsp> words;
    /**
     * 句子序号，在段落、自由说模式下有效，表示断句序号，最后的综合结果的为-1.
     */
    @SerializedName("SentenceId")
    private long sentenceId;
    /**
     * 匹配候选文本的序号，在句子多分支、情景对 话、段落模式下表示匹配到的文本序号
     */
    @SerializedName("RefTextId")
    private long refTextId;
    /**
     * 主题词命中标志，0表示没命中，1表示命中
     */
    @SerializedName("KeyWordHits")
    private List<Float> keyWordHits;
    /**
     * 负向主题词命中标志，0表示没命中，1表示命中
     */
    @SerializedName("UnKeyWordHits")
    private List<Float> unKeyWordHits;

    public double getSuggestedScore() {
        return suggestedScore;
    }

    public void setSuggestedScore(double suggestedScore) {
        this.suggestedScore = suggestedScore;
    }

    public double getPronAccuracy() {
        return pronAccuracy;
    }

    public void setPronAccuracy(double pronAccuracy) {
        this.pronAccuracy = pronAccuracy;
    }

    public double getPronFluency() {
        return pronFluency;
    }

    public void setPronFluency(double pronFluency) {
        this.pronFluency = pronFluency;
    }

    public double getPronCompletion() {
        return pronCompletion;
    }

    public void setPronCompletion(double pronCompletion) {
        this.pronCompletion = pronCompletion;
    }

    public List<WordRsp> getWords() {
        return words;
    }

    public void setWords(List<WordRsp> words) {
        this.words = words;
    }

    public long getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(long sentenceId) {
        this.sentenceId = sentenceId;
    }

    public long getRefTextId() {
        return refTextId;
    }

    public void setRefTextId(long refTextId) {
        this.refTextId = refTextId;
    }

    public List<Float> getKeyWordHits() {
        return keyWordHits;
    }

    public void setKeyWordHits(List<Float> keyWordHits) {
        this.keyWordHits = keyWordHits;
    }

    public List<Float> getUnKeyWordHits() {
        return unKeyWordHits;
    }

    public void setUnKeyWordHits(List<Float> unKeyWordHits) {
        this.unKeyWordHits = unKeyWordHits;
    }
}
