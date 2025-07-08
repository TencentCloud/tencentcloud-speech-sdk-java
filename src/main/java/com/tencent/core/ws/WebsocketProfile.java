package com.tencent.core.ws;

public class WebsocketProfile {

    private int eventGroupThreadNum;

    private int handshakeTimeout;

    private boolean isCompression;

    private int connectTimeout;

   private ProxyProfile proxyProfile;

    public int getHandshakeTimeout() {
        return handshakeTimeout;
    }

    public void setHandshakeTimeout(int handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
    }

    public boolean isCompression() {
        return isCompression;
    }

    public void setCompression(boolean compression) {
        isCompression = compression;
    }

    public int getEventGroupThreadNum() {
        return eventGroupThreadNum;
    }

    public void setEventGroupThreadNum(int eventGroupThreadNum) {
        this.eventGroupThreadNum = eventGroupThreadNum;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public static WebsocketProfile defaultWebsocketProfile() {
        WebsocketProfile profile = new WebsocketProfile();
        profile.setCompression(false);
        profile.setHandshakeTimeout(5000);
        profile.setEventGroupThreadNum(0);
        profile.setConnectTimeout(5000);
        return profile;
    }

    public ProxyProfile getProxyProfile() {
        return proxyProfile;
    }

    public void setProxyProfile(ProxyProfile proxyProfile) {
        this.proxyProfile = proxyProfile;
    }
}
