package com.tencent.tts.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpeechWsSynthesisResponse {

    @JsonProperty(value = "session_id")
    private String SessionId;
    @JsonProperty(value = "code")
    private Integer Code;
    @JsonProperty(value = "final")
    private Integer Final;
    @JsonProperty(value = "message")
    private String Message;
    @JsonProperty(value = "request_id")
    private String RequestId;
    @JsonProperty(value = "message_id")
    private String MessageId;
    @JsonProperty(value = "result")
    private Subtitles Result;

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    public Integer getFinal() {
        return Final;
    }

    public void setFinal(Integer aFinal) {
        Final = aFinal;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public Subtitles getResult() {
        return Result;
    }

    public void setResult(Subtitles result) {
        Result = result;
    }
}
