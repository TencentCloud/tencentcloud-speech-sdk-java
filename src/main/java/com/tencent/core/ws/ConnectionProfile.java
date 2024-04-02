package com.tencent.core.ws;

public class ConnectionProfile {

    private String sign;

    private String url;

    private String host;

    private String token;

    public ConnectionProfile(String sign, String url, String host, String token) {
        this.sign = sign;
        this.url = url;
        this.host = host;
        this.token = token;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
