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

import com.google.gson.annotations.SerializedName;

/**
 * TextToStreamAudioWSV2响应
 */
public class FlowingSpeechSynthesizerResponse {

    /**
     * 音频流唯一 id，由客户端在握手阶段生成并赋值在调用参数中
     */
    @SerializedName("session_id")
    private String sessionId;
    /**
     * 状态码，0代表正常，非0值表示发生错误
     * 客户端错误
     * 数值	说明
     * 10001	参数不合法，具体详情参考 message 字段
     * 10002	账号当前调用并发超限
     * 10003	鉴权失败
     * 10004	客户端数据上传超时
     * 10005	客户端连接断开
     * 10006	流式输入文本包含SSML
     * 10007	流式输入文本超过最大长度限制
     * 10008	流式输入文本通道已关闭
     * 10009	流式输入文本超时未发送，服务端合成完毕后将正常关闭连接
     * 服务端错误
     * 数值	说明
     * 20000	后台错误
     * 20001	后台服务处理失败
     * 20002	后台引擎合成失败
     * 20003	后台引擎合成超时
     */
    @SerializedName("code")
    private Integer code;
    /**
     * 该字段返回1时表示文本全部合成结束，客户端收到后需主动关闭 websocket 连接
     */
    @SerializedName("final")
    private Integer end;

    @SerializedName("ready")
    private Integer ready;
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

    public Integer getReady() {
        return ready;
    }

    public void setReady(Integer ready) {
        this.ready = ready;
    }
}
