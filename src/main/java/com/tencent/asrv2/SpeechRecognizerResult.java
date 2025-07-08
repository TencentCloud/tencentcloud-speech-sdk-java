package com.tencent.asrv2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 最新语音识别结果
 */
public class SpeechRecognizerResult {


    /**
     * 识别结果类型：
     * 0：一段话开始识别
     * 1：一段话识别中，voice_text_str 为非稳态结果(该段识别结果还可能变化)
     * 2：一段话识别结束，voice_text_str 为稳态结果(该段识别结果不再变化)
     * 根据发送的音频情况，识别过程中可能返回的 slice_type 序列有：
     * 0-1-2：一段话开始识别、识别中(可能有多次1返回)、识别结束
     * 0-2：一段话开始识别、识别结束
     * 2：直接返回一段话完整的识别结果
     * 注意：如果需要0和2配对返回，需要设置filter_empty_result=0（slice_type=0时，识别结果可能为空，默认是不返回空识别结果的）。一般在外呼场景需要配对返回，通过slice_type=0来判断是否有人声出现。
     */
    @SerializedName(value = "slice_type")
    private Integer sliceType;

    /**
     * 当前一段话结果在整个音频流中的序号，从0开始逐句递增
     */
    private Integer index;

    /**
     * 当前一段话结果在整个音频流中的起始时间
     */
    @SerializedName(value = "start_time")
    private Long startTime;

    /**
     * 当前一段话结果在整个音频流中的结束时间
     */
    @SerializedName(value = "end_time")
    private Long endTime;

    /**
     * 当前一段话文本结果，编码为 UTF8
     */
    @SerializedName(value = "voice_text_str")
    private String voiceTextStr;

    /**
     * 当前一段话的词结果个数
     */
    @SerializedName(value = "word_size")
    private Integer wordSize;
    /**
     * 当前一段话的词列表，Word 结构体格式为：
     * word：String 类型，该词的内容
     * start_time：Integer 类型，该词在整个音频流中的起始时间
     * end_time：Integer 类型，该词在整个音频流中的结束时间
     * stable_flag：Integer 类型，该词的稳态结果，0表示该词在后续识别中可能发生变化，1表示该词在后续识别过程中不会变化
     */
    @SerializedName(value = "word_list")
    private List<Word> wordList;

    public Integer getSliceType() {
        return sliceType;
    }

    public void setSliceType(Integer sliceType) {
        this.sliceType = sliceType;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getVoiceTextStr() {
        return voiceTextStr;
    }

    public void setVoiceTextStr(String voiceTextStr) {
        this.voiceTextStr = voiceTextStr;
    }

    public Integer getWordSize() {
        return wordSize;
    }

    public void setWordSize(Integer wordSize) {
        this.wordSize = wordSize;
    }

    public List<Word> getWordList() {
        return wordList;
    }

    public void setWordList(List<Word> wordList) {
        this.wordList = wordList;
    }

    public static class Word {
        /**
         * 表示这个词的内容
         */
        private String word;
        /**
         * 表示该词在整个音频中的起始时间，
         */
        @SerializedName(value = "start_time")
        private Long startTime;

        /**
         * 表示该词在整个音频中的结束时间，
         */
        @SerializedName(value = "end_time")
        private Long endTime;
        /**
         * 表示词的稳态结果，0：该词在后续识别中可能发生变化，1：表示该词在后续识别过程中不会变化。
         */
        @SerializedName(value = "stable_flag")
        private Integer stableFlag;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }

        public Long getEndTime() {
            return endTime;
        }

        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }

        public Integer getStableFlag() {
            return stableFlag;
        }

        public void setStableFlag(Integer stableFlag) {
            this.stableFlag = stableFlag;
        }
    }
}
