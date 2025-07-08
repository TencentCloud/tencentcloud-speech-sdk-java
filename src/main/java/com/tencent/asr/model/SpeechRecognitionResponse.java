
package com.tencent.asr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpeechRecognitionResponse {

    @JsonIgnore()
    private String streamId;

    /**
     * 0：正常，其他，发生错误
     */
    private int code;

    /**
     * message
     */
    private String message;

    /**
     * voiceId
     */
    @JsonProperty("voice_id")
    private String voiceId;

    /**
     * 0表示还在整个音频流的中间部分
     * 1表示是整个音频流的最后一个包。
     * 主要是在电信场景中，客户端发送完了之后，要知道是否返回的是最后一个包。
     */
    @JsonProperty(value = "final")
    private Integer finalSpeech;


    @JsonProperty(value = "result")
    private SpeechRecognitionResponseResult result;

    @JsonProperty(value = "message_id")
    private String messageId;


}
