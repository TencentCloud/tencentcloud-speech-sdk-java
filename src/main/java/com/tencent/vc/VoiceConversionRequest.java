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

import com.google.gson.annotations.SerializedName;
import com.tencent.core.ws.CommonRequest;

/**
 * 声音变换（websocket）请求参数
 */
public class VoiceConversionRequest extends CommonRequest {

    /**
     * 腾讯云注册账号的密钥 SecretId，可通过 API 密钥管理页面 获取
     */
    @SerializedName("SecretId")
    private String secretid;

    /**
     * 当前 UNIX 时间戳，可记录发起 API 请求的时间。如果与当前时间相差过大，会引起签名过期错误。可以取值为当前请求的系统时间戳即可。
     */
    @SerializedName("Timestamp")
    protected Long timestamp;

    /**
     * 签名的有效期，是一个符合 UNIX Epoch 时间戳规范的数值，单位为秒；Expired 必须大于 Timestamp 且 Expired - Timestamp 小于90天。
     */
    @SerializedName("Expired")
    protected Long expired;

    @SerializedName("AppId")
    private Integer appId;

    /**
     * 301005-小新，男童声
     * 301006-小姐姐，女声
     * 301007-童声，男童声
     * 301008-东北话，男声
     * 301009-影视解说，男声
     * 301010-萝莉，女声
     * 301011-小黄人，男童声
     */
    @SerializedName("VoiceType")
    private Integer voiceType;

    /**
     * 音频采样率：
     * 16000：16k
     */
    @SerializedName("SampleRate")
    private Integer sampleRate;

    /**
     * 音频格式：
     * pcm：pcm 音频
     */
    @SerializedName("Codec")
    private String codec;

    /**
     * 该字段为1时表示音频流全部发送完成
     */
    @SerializedName("End")
    private Integer end;

    /**
     * 音频流识别全局唯一标识，一个 websocket 连接对应一个，用户自己生成（推荐使用 uuid），最长128位
     */
    @SerializedName("VoiceId")
    private String voiceId;

    /**
     * 音量大小，范围[-10，10]，用于调整变换后音频音量。默认为0，代表正常音量。音量值大于0，增加音量。音量值小于0，降低音量。
     */
    @SerializedName("Volume")
    private Float volume;

    public String getSecretid() {
        return secretid;
    }

    public void setSecretid(String secretid) {
        this.secretid = secretid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getExpired() {
        return expired;
    }

    public void setExpired(Long expired) {
        this.expired = expired;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(Integer voiceType) {
        this.voiceType = voiceType;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }
}
