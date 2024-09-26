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
package com.tencent.vc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;

/**
 * 声音变换（websocket）响应
 */
public class VoiceConversionResponse {

    /**
     * 16位 String 串作为每个音频的唯一标识，用户自己生成
     */
    @SerializedName("VoiceId")
    private String voiceId;

    /**
     * 本 message 唯一 id
     */
    @SerializedName("MessageId")
    private String messageId;

    /**
     * 错误说明，发生错误时显示这个错误发生的具体原因，随着业务发展或体验优化，此文本可能会经常保持变更或更新
     */
    @SerializedName("Message")
    private String message;


    /**
     * 该字段返回1时表示文本全部合成结束，客户端收到后需主动关闭 websocket 连接
     */
    @SerializedName("Final")
    private Integer end;


    /**
     * 状态码，0代表正常，非0值表示发生错误
     * 4001
     * 参数不合法，具体详情参考 message
     * 4002
     * 鉴权失败
     * 4003
     * AppID 服务未开通，请在控制台开通服务
     * 4004
     * 无可使用的免费额度
     * 4005
     * 账户欠费停止服务，请及时充值
     * 4006
     * 账号当前调用并发超限
     * 4007
     * 音频解码失败，请检查上传音频数据格式与调用参数一致
     * 4008
     * 客户端数据上传超时
     * 4100
     * 服务未开通
     * 4102
     * 欠费停服
     * 4103
     * 用户主动停服
     * 4109
     * 资源包余量已用尽
     * 4009
     * 客户端连接断开
     * 5000
     * 后台错误，请重试
     * 5001
     * 后台识别服务器变换失败，请重试
     * 5002
     * 后台识别服务器变换失败，请重试
     */
    @SerializedName("Code")
    private Integer code;

    /**
     * 音频数据 注意：audio可能为空
     */
    private byte[] audio;

    public byte[] getAudio() {
        return audio;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }


}
