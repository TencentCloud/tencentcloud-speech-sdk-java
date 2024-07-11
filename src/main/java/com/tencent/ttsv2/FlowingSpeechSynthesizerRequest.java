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
import com.tencent.core.ws.CommonRequest;

import java.util.Map;

/**
 * TextToStreamAudioWSV2参数
 */
public class FlowingSpeechSynthesizerRequest extends CommonRequest {
    /**
     * 调用接口名，取值为：TextToStreamAudioWSV2
     */
    @SerializedName("Action")
    private String action;

    /**
     * 账号 AppId（请确保该字段数据类型为整型 int）
     */
    @SerializedName("AppId")
    private Integer appId;

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


    /**
     * 是
     * 语音合成全局唯一标识，一个 websocket 连接对应一个，用户自己生成（推荐使用 uuid），最长128位。
     */
    @SerializedName("SessionId")
    private String sessionId;


    /**
     * 否
     * Int详见：语音合成 API 文档中的 VoiceType 参数。
     */
    @SerializedName("VoiceType")
    private Integer voiceType;

    /**
     * 音量大小，范围[-10，10]，对应音量大小。
     * 默认为0，代表正常音量，值越大音量越高。
     */
    @SerializedName("Volume")
    private Float volume;


    /**
     * 语速，范围：[-2，6]，分别对应不同语速：
     * -2: 代表0.6倍
     * -1: 代表0.8倍
     * 0: 代表1.0倍（默认）
     * 1: 代表1.2倍
     * 2: 代表1.5倍
     * 6: 代表2.5倍
     * 如果需要更细化的语速，可以保留小数点后 2 位，例如0.5/1.25/2.81等。
     * 参数值与实际语速转换，可参考 代码示例
     */
    @SerializedName("Speed")
    private Float speed;
    /**
     * 音频采样率：
     * 24000：24k（部分音色支持，请参见 音色列表）
     * 16000：16k（默认）
     * 8000：8k
     */
    @SerializedName("SampleRate")
    private Integer sampleRate;


    /**
     * 返回音频格式：
     * pcm: 返回二进制 pcm 音频（默认）
     * mp3: 返回二进制 mp3 音频
     */
    @SerializedName("Codec")
    private String codec;

    /**
     * 是否开启时间戳功能，默认为false。
     */
    @SerializedName("EnableSubtitle")
    private Boolean enableSubtitle;

    /**
     * 控制合成音频的情感，仅支持多情感音色使用。取值: neutral(中性)、sad(悲伤)、happy(高兴)、angry(生气)、fear(恐惧)、news(新闻)、story(故事)、radio(广播)、poetry(诗歌)、call(客服)、撒娇(sajiao)、厌恶(disgusted)、震惊(amaze)、平静(peaceful)、兴奋(exciting)、傲娇(aojiao)、解说(jieshuo)
     * 示例值:neutral
     */
    @SerializedName("EmotionCategory")
    private String emotionCategory;
    /**
     * 控制合成音频情感程度，取值范围为 [50,200]，默认为 100；只有 EmotionCategory 不为空时生效。
     */
    @SerializedName("EmotionIntensity")
    private Integer emotionIntensity;

    /**
     * 断句敏感阈值，取值范围：[0,1,2]，默认值：0
     * 该值越大越不容易断句，模型会更倾向于仅按照标点符号断句。此参数建议不要随意调整，可能会影响合成效果
     */
    @SerializedName("SegmentRate")
    private Integer segmentRate;

    public Integer getSegmentRate() {
        return segmentRate;
    }

    public void setSegmentRate(Integer segmentRate) {
        this.segmentRate = segmentRate;
    }

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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(Integer voiceType) {
        this.voiceType = voiceType;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
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

    public Boolean getEnableSubtitle() {
        return enableSubtitle;
    }

    public void setEnableSubtitle(Boolean enableSubtitle) {
        this.enableSubtitle = enableSubtitle;
    }

    public String getEmotionCategory() {
        return emotionCategory;
    }

    public void setEmotionCategory(String emotionCategory) {
        this.emotionCategory = emotionCategory;
    }

    public Integer getEmotionIntensity() {
        return emotionIntensity;
    }

    public void setEmotionIntensity(Integer emotionIntensity) {
        this.emotionIntensity = emotionIntensity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Map<String, Object> toTreeMap() {
        Map<String, Object> src = super.toTreeMap();
        if (src != null) {
            for (String key : src.keySet()) {
                if (src.get(key) instanceof Float) {
                    Float value = Float.parseFloat(src.get(key).toString());
                    if (value != null && value.intValue() == value) {
                        src.put(key, value.intValue());
                    } else {
                        src.put(key, value);
                    }
                }
            }
        }
        return src;
    }
}
