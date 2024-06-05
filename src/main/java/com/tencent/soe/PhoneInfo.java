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

public class PhoneInfo {
    /**
     * 当前音节语音起始时间点，单位为ms
     */
    @SerializedName("MemBeginTime")
    private long memBeginTime;
    /**
     * 当前音节语音终止时间点，单位为ms
     */
    @SerializedName("MemEndTime")
    private long memEndTime;
    /**
     * 音节发音准确度，取值范围[-1, 100]，当取-1时指完全不匹配
     */
    @SerializedName("PronAccuracy")
    private double pronAccuracy;
    /**
     * 当前音节是否检测为重音
     */
    @SerializedName("DetectedStress")
    private boolean detectedStress;
    /**
     * 当前音节，当前评测识别的音素
     */
    @SerializedName("Phone")
    private String phone;
    /**
     * 参考音素，在单词诊断模式下，代表标准音素
     */
    @SerializedName("ReferencePhone")
    private String referencePhone;
    /**
     * 参考字符，在单词诊断模式下，代表音素对应的原始文本
     */
    @SerializedName("ReferenceLetter")
    private String referenceLetter;
    /**
     * 当前音节是否应为重音
     */
    @SerializedName("Stress")
    private boolean stress;
    /**
     * 当前词与输入语句的匹配情况，0：匹配单词、1：新增单词、2：缺少单词、3：错读的词、4：未录入单词。
     */
    @SerializedName("MatchTag")
    private long matchTag;

    public long getMemBeginTime() {
        return memBeginTime;
    }

    public void setMemBeginTime(long memBeginTime) {
        this.memBeginTime = memBeginTime;
    }

    public long getMemEndTime() {
        return memEndTime;
    }

    public void setMemEndTime(long memEndTime) {
        this.memEndTime = memEndTime;
    }

    public double getPronAccuracy() {
        return pronAccuracy;
    }

    public void setPronAccuracy(double pronAccuracy) {
        this.pronAccuracy = pronAccuracy;
    }

    public boolean isDetectedStress() {
        return detectedStress;
    }

    public void setDetectedStress(boolean detectedStress) {
        this.detectedStress = detectedStress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReferencePhone() {
        return referencePhone;
    }

    public void setReferencePhone(String referencePhone) {
        this.referencePhone = referencePhone;
    }

    public String getReferenceLetter() {
        return referenceLetter;
    }

    public void setReferenceLetter(String referenceLetter) {
        this.referenceLetter = referenceLetter;
    }

    public boolean isStress() {
        return stress;
    }

    public void setStress(boolean stress) {
        this.stress = stress;
    }

    public long getMatchTag() {
        return matchTag;
    }

    public void setMatchTag(long matchTag) {
        this.matchTag = matchTag;
    }
}
