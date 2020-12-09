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

package com.tencent.asr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class SpeechRecognitionResponseResult {

    /**
     * 返回分片类型标记， 0表示一小段话开始，1表示在小段话的进行中，2表示小段话的结束
     */
    @JsonProperty(value = "slice_type")
    private Integer sliceType;

    /**
     * 表示第几段话
     */
    private Integer index;

    /**
     * 这个分片在整个音频流中的开始时间
     */
    @JsonProperty(value = "start_time")
    private Long startTime;

    /**
     * 这个分片在整个音频流中的结束时间
     */
    @JsonProperty(value = "end_time")
    private Long endTime;

    /**
     * 识别结果
     */
    @JsonProperty(value = "voice_text_str")
    private String voiceTextStr;

    /**
     * 表示后面的
     * word_list 的长度，即有多少个词。
     */
    @JsonProperty(value = "word_size")
    private Integer wordSize;

    @JsonProperty(value = "word_list")
    private List<SpeechRecognitionResponseResult.Word> wordList;


    @Setter
    @Getter
    @NoArgsConstructor
    public static class Word {
        /**
         * 表示这个词的内容
         */
        private String word;
        /**
         * 表示该词在整个音频中的起始时间，
         */
        @JsonProperty(value = "start_time")
        private Long startTime;

        /**
         * 表示该词在整个音频中的结束时间，
         */
        @JsonProperty(value = "end_time")
        private Long endTime;
        /**
         * 表示词的稳态结果，0：该词在后续识别中可能发生变化，1：表示该词在后续识别过程中不会变化。
         */
        @JsonProperty(value = "stable_flag")
        private Integer stableFlag;
    }
}
