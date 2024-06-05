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

/**
 * 中文声调检测结果
 */
public class Tone {
    /**
     * 检测结果是否有效
     * 注意：此字段可能返回 null，表示取不到有效值。
     * 示例值：true
     */
    @SerializedName("Valid")
    private boolean valid;
    /**
     * 文本标准声调，数值范围[-1,1,2,3,4]
     * 注意：此字段可能返回 null，表示取不到有效值。
     * 示例值：1
     */
    @SerializedName("RefTone")
    private int refTone;
    /**
     * 实际发音声调，数值范围[-1,1,2,3,4]
     * 注意：此字段可能返回 null，表示取不到有效值。
     * 示例值：1
     */
    @SerializedName("HypothesisTone")
    private int hypothesisTone;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getRefTone() {
        return refTone;
    }

    public void setRefTone(int refTone) {
        this.refTone = refTone;
    }

    public int getHypothesisTone() {
        return hypothesisTone;
    }

    public void setHypothesisTone(int hypothesisTone) {
        this.hypothesisTone = hypothesisTone;
    }
}
