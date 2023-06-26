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

package com.tencent.tts.model;

import java.util.Map;

public class SpeechWsSynthesisRequest {

    /**
     * 用户信息 SecretKey
     */
    private String SecretKey;

    /**
     * 用户信息 Token
     */
    private String Token;
    /**
     * AppId
     */
    private Integer AppId;
    /**
     * SecretId
     */
    private String SecretId;
    /**
     * ModelType
     */
    private Integer ModelType;
    /**
     * 音色 ID，包括标准音色与精品音色，精品音色拟真度更高，价格不同于标准音色，请参见购买指南。完整的音色 ID 列表请参见音色列表。
     */
    private Integer VoiceType;
    /**
     * 返回音频格式： </br> opus: 返回多段含 opus 压缩分片音频（默认） </br> pcm: 返回二进制 pcm 音频 </br> mp3: 返回二进制 mp3 音频
     */
    private String Codec;
    /**
     * 音频采样率：
     * 16000：16k（默认）
     * 8000：8k
     */
    private Long SampleRate;
    /**
     * 语速，范围：[-2，6]
     */
    private Float Speed;
    /**
     * 音量大小，范围：[0，10]
     */
    private Float Volume;
    /**
     * 一次请求对应一个 SessionId，会原样返回，建议传入类似于 uuid 的字符串防止重复
     */
    private String SessionId;
    /**
     * 合成语音的源文本
     */
    private String Text;
    /**
     * 是否开启时间戳功能，默认为false。
     */
    private Boolean EnableSubtitle;
    /**
     * 断句敏感阈值，取值范围：[0,1,2]，默认值：0
     * 该值越大越不容易断句，模型会更倾向于仅按照标点符号断句。此参数建议不要随意调整，可能会影响合成效果
     */
    private Integer SegmentRate;
    /**
     * 控制合成音频的情感，仅支持多情感音色使用。
     * neutral(中性)、sad(悲伤)、happy(高兴)、angry(生气)、fear(恐惧)、news(新闻)、story(故事)、radio(广播)、poetry(诗歌)、call(客服)
     */
    private String EmotionCategory;
    /**
     * 控制合成音频情感程度，取值范围为[50,200],默认为100；只有 EmotionCategory 不为空时生效；
     */
    private Integer EmotionIntensity;

    /**
     * 扩展字段
     */
    private Map<String, Object> extendsParam;

    public SpeechWsSynthesisRequest() {
    }

    public String getSecretKey() {
        return SecretKey;
    }

    public void setSecretKey(String secretKey) {
        SecretKey = secretKey;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public Integer getAppId() {
        return AppId;
    }

    public void setAppId(Integer appId) {
        AppId = appId;
    }

    public String getSecretId() {
        return SecretId;
    }

    public void setSecretId(String secretId) {
        SecretId = secretId;
    }

    public Integer getModelType() {
        return ModelType;
    }

    public void setModelType(Integer modelType) {
        ModelType = modelType;
    }

    public Integer getVoiceType() {
        return VoiceType;
    }

    public void setVoiceType(Integer voiceType) {
        VoiceType = voiceType;
    }

    public String getCodec() {
        return Codec;
    }

    public void setCodec(String codec) {
        Codec = codec;
    }

    public Long getSampleRate() {
        return SampleRate;
    }

    public void setSampleRate(Long sampleRate) {
        SampleRate = sampleRate;
    }

    public Float getSpeed() {
        return Speed;
    }

    public void setSpeed(Float speed) {
        Speed = speed;
    }

    public Float getVolume() {
        return Volume;
    }

    public void setVolume(Float volume) {
        Volume = volume;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public Boolean getEnableSubtitle() {
        return EnableSubtitle;
    }

    public String getEmotionCategory() {
        return EmotionCategory;
    }

    public void setEmotionCategory(String emotionCategory) {
        EmotionCategory = emotionCategory;
    }

    public Integer getEmotionIntensity() {
        return EmotionIntensity;
    }

    public void setEmotionIntensity(Integer emotionIntensity) {
        EmotionIntensity = emotionIntensity;
    }

    public void setEnableSubtitle(Boolean enableSubtitle) {
        EnableSubtitle = enableSubtitle;
    }

    public Integer getSegmentRate() {
        return SegmentRate;
    }

    public void setSegmentRate(Integer segmentRate) {
        SegmentRate = segmentRate;
    }

    public Map<String, Object> getExtendsParam() {
        return extendsParam;
    }

    public void setExtendsParam(Map<String, Object> extendsParam) {
        this.extendsParam = extendsParam;
    }
}
