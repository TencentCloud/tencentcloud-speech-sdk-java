package com.tencent.asr.model;

import java.net.ProxySelector;
import java.util.concurrent.ExecutorService;
import okhttp3.Authenticator;

/**
 * SpeechWebsocketConfig websocket 请求配置
 */
public class SpeechWebsocketConfig {

    /**
     * wsWriteTimeOut
     */
    private Integer wsWriteTimeOut;
    /**
     * wsReadTimeOut
     */
    private Integer wsReadTimeOut;
    /**
     * wsConnectTimeOut
     */
    private Integer wsConnectTimeOut;

    /**
     * 连接池大小，指单个okhttpclient实例所有连接的连接池。
     * 默认：600，值的设置与业务请求量有关，如果请求三方的tps是200，建议这个值设置在200左右。
     */
    private Integer wsMaxIdleConnections;

    /**
     * 连接池中连接的最大时长 默认5分钟，依据业务场景来确定有效时间，如果不确定，那就保持5分钟
     */
    private Integer wsKeepAliveDuration;

    /**
     * 当前okhttpclient实例最大的并发请求数
     */
    private Integer wsMaxRequests;

    /**
     * 单个主机最大请求并发数，这里的主机指被请求方主机，一般可以理解对调用方有限流作用。注意：websocket请求不受这个限制。
     */
    private Integer wsMaxRequestsPerHost;

    /**
     * dispatch executorService
     */
    private ExecutorService executorService;

    /**
     * 是否开启代理模式
     */
    private Boolean useProxy;

    /**
     * 代理hostname
     */
    private String proxyHost;

    /**
     * 代理port
     */
    private Integer proxyPort;

    /**
     * okhttp proxySelector
     */
    private ProxySelector proxySelector;

    /**
     * okhttp authenticator
     */
    private Authenticator authenticator;

    public Integer getWsWriteTimeOut() {
        return wsWriteTimeOut;
    }

    public void setWsWriteTimeOut(Integer wsWriteTimeOut) {
        this.wsWriteTimeOut = wsWriteTimeOut;
    }

    public Integer getWsReadTimeOut() {
        return wsReadTimeOut;
    }

    public void setWsReadTimeOut(Integer wsReadTimeOut) {
        this.wsReadTimeOut = wsReadTimeOut;
    }

    public Integer getWsConnectTimeOut() {
        return wsConnectTimeOut;
    }

    public void setWsConnectTimeOut(Integer wsConnectTimeOut) {
        this.wsConnectTimeOut = wsConnectTimeOut;
    }

    public Integer getWsMaxIdleConnections() {
        return wsMaxIdleConnections;
    }

    public void setWsMaxIdleConnections(Integer wsMaxIdleConnections) {
        this.wsMaxIdleConnections = wsMaxIdleConnections;
    }

    public Integer getWsKeepAliveDuration() {
        return wsKeepAliveDuration;
    }

    public void setWsKeepAliveDuration(Integer wsKeepAliveDuration) {
        this.wsKeepAliveDuration = wsKeepAliveDuration;
    }

    public Integer getWsMaxRequests() {
        return wsMaxRequests;
    }

    public void setWsMaxRequests(Integer wsMaxRequests) {
        this.wsMaxRequests = wsMaxRequests;
    }

    public Integer getWsMaxRequestsPerHost() {
        return wsMaxRequestsPerHost;
    }

    public void setWsMaxRequestsPerHost(Integer wsMaxRequestsPerHost) {
        this.wsMaxRequestsPerHost = wsMaxRequestsPerHost;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public Boolean getUseProxy() {
        return useProxy;
    }

    public void setUseProxy(Boolean useProxy) {
        this.useProxy = useProxy;
    }

    public ProxySelector getProxySelector() {
        return proxySelector;
    }

    public void setProxySelector(ProxySelector proxySelector) {
        this.proxySelector = proxySelector;
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public static SpeechWebsocketConfig init() {
        SpeechWebsocketConfig config = new SpeechWebsocketConfig();
        config.setWsConnectTimeOut(SpeechRecognitionSysConfig.wsConnectTimeOut);
        config.setWsWriteTimeOut(SpeechRecognitionSysConfig.wsWriteTimeOut);
        config.setWsReadTimeOut(SpeechRecognitionSysConfig.wsReadTimeOut);
        config.setWsKeepAliveDuration(SpeechRecognitionSysConfig.wsKeepAliveDuration);
        config.setWsMaxIdleConnections(SpeechRecognitionSysConfig.wsMaxIdleConnections);
        config.setWsMaxRequests(SpeechRecognitionSysConfig.wsMaxRequests);
        config.setWsMaxRequestsPerHost(SpeechRecognitionSysConfig.wsMaxRequestsPerHost);
        config.setUseProxy(false);
        return config;
    }
}
