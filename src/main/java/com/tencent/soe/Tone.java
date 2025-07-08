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
