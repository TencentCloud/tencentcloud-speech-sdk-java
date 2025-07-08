
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
