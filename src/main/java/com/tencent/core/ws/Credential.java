package com.tencent.core.ws;

/**
 * 密钥信息
 */
public class Credential {

    /**
     * appid
     */
    private String appid;

    /**
     * secretId,在控制台申请
     */
    private String secretId;

    /**
     * secretKey,在控制台申请
     */
    private String secretKey;

    /**
     * token,用于临时授权场景
     */
    private String token;

    public Credential() {
    }

    public Credential(String appid, String secretId, String secretKey) {
        this.appid = appid;
        this.secretId = secretId;
        this.secretKey = secretKey;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
