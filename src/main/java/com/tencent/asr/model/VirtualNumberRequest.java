package com.tencent.asr.model;

import java.util.Map;

public class VirtualNumberRequest {

    /**
     * 请求参数 VoiceFormat
     * AudioFormatPCM   = 1
     * AudioFormatSpeex = 4
     * AudioFormatSilk  = 6
     * AudioFormatMp3   = 8
     * AudioFormatOpus  = 10
     * AudioFormatWav   = 12
     * AudioFormatM4A   = 14
     * AudioFormatAAC   = 16
     */
    private Integer VoiceFormat;

    /**
     * 请求参数 VoiceId
     */
    private String VoiceId;

    /**
     * 用户信息 AppId
     */
    private Integer AppId;
    /**
     * 用户信息 SecretId
     */
    private String SecretId;

    /**
     * 用户信息 SecretKey
     */
    private String SecretKey;

    /**
     * 用户信息 Token
     */
    private String Token;

    /**
     * 扩展字段
     */
    protected Map<String, Object> extendsParam;

    public Integer getVoiceFormat() {
        return VoiceFormat;
    }

    public void setVoiceFormat(Integer voiceFormat) {
        VoiceFormat = voiceFormat;
    }

    public String getVoiceId() {
        return VoiceId;
    }

    public void setVoiceId(String voiceId) {
        VoiceId = voiceId;
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

    public Map<String, Object> getExtendsParam() {
        return extendsParam;
    }

    public void setExtendsParam(Map<String, Object> extendsParam) {
        this.extendsParam = extendsParam;
    }
}
