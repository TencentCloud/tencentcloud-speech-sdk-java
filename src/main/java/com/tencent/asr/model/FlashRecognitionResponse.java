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
public class FlashRecognitionResponse {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("audio_duration")
    private Long audioDuration;

    @JsonProperty("flash_result")
    private List<FlashRecognitionResult> flashResult;

    @Setter
    @Getter
    @NoArgsConstructor
    public static class FlashRecognitionResult {
        @JsonProperty("text")
        private String text;
        @JsonProperty("channel_id")
        private Integer channelId;
        @JsonProperty("sentence_list")
        private List<FlashRecognitionSentence> sentenceList;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class FlashRecognitionSentence {
        @JsonProperty("text")
        private String text;
        @JsonProperty("start_time")
        private Long startTime;
        @JsonProperty("end_time")
        private Long endTime;
        @JsonProperty("speaker_id")
        private Integer speakerId;
        @JsonProperty("word_list")
        private List<FlashWordData> wordList;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class FlashWordData {
        @JsonProperty("word")
        private String word;
        @JsonProperty("start_time")
        private Long startTime;
        @JsonProperty("end_time")
        private Long endTime;
        @JsonProperty("stable_flag")
        private Integer stableFlag;
    }
}
