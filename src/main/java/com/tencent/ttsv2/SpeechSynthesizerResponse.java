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
package com.tencent.ttsv2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.tencent.tts.model.Subtitles;

/**
 * 实时语音合成响应
 */
public class SpeechSynthesizerResponse {
    /**
     * 音频流唯一 id，由客户端在握手阶段生成并赋值在调用参数中
     */
    @SerializedName("session_id")
    private String sessionId;
    /**
     * 状态码，0代表正常，非0值表示发生错误
     */
    @SerializedName("code")
    private Integer code;
    /**
     * 该字段返回1时表示文本全部合成结束，客户端收到后需主动关闭 websocket 连接
     */
    @SerializedName("final")
    private Integer end;
    /**
     * 错误说明，发生错误时显示这个错误发生的具体原因，随着业务发展或体验优化，此文本可能会经常保持变更或更新
     */
    @SerializedName("message")
    private String message;
    /**
     * 音频流唯一 id，由服务端在握手阶段自动生成
     */
    @SerializedName("request_id")
    private String requestId;
    /**
     * 本 message 唯一 id
     */
    @SerializedName("message_id")
    private String messageId;
    /**
     * 最新语音合成文本结果
     */
    @SerializedName("result")
    private SpeechSynthesizerResult result;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public SpeechSynthesizerResult getResult() {
        return result;
    }

    public void setResult(SpeechSynthesizerResult result) {
        this.result = result;
    }
}
