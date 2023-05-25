package com.tencent.asr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VirtualNumberResponse {
    @JsonProperty("Code")
    private Integer Code;
    @JsonProperty("message")
    private String Message;
    @JsonProperty("voice_id")
    private String VoiceID;
    @JsonProperty("message_id")
    private String MessageID;
    @JsonProperty("final")
    private Integer Final;
    @JsonProperty("result")
    private Integer Result;

    public VirtualNumberResponse(){}

    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getVoiceID() {
        return VoiceID;
    }

    public void setVoiceID(String voiceID) {
        VoiceID = voiceID;
    }

    public String getMessageID() {
        return MessageID;
    }

    public void setMessageID(String messageID) {
        MessageID = messageID;
    }

    public Integer getFinal() {
        return Final;
    }

    public void setFinal(Integer aFinal) {
        Final = aFinal;
    }

    public Integer getResult() {
        return Result;
    }

    public void setResult(Integer result) {
        Result = result;
    }
}
