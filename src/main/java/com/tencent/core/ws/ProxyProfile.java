package com.tencent.core.ws;

import java.util.Map;

public class ProxyProfile {

    private String proxyUserName;

    private String proxyPwd;

    private Map<String,String> proxyHeader;


    public ProxyProfile() {
    }

    public ProxyProfile(String proxyUserName, String proxyPwd) {
        this.proxyUserName = proxyUserName;
        this.proxyPwd = proxyPwd;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public String getProxyPwd() {
        return proxyPwd;
    }

    public void setProxyPwd(String proxyPwd) {
        this.proxyPwd = proxyPwd;
    }

    public Map<String, String> getProxyHeader() {
        return proxyHeader;
    }

    public void setProxyHeader(Map<String, String> proxyHeader) {
        this.proxyHeader = proxyHeader;
    }
}
