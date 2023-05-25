package com.tencent.asr.model;

import com.tencent.asr.service.VirtualNumberRecognizer;
import com.tencent.core.model.GlobalConfig;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 虚拟号配置类 按需更改
 */
public class VirtualNumberServerConfig {

    private String SignPrefixUrl = "asr.cloud.tencent.com/asr/virtual_number/v1/";
    private String Host = "asr.cloud.tencent.com";
    private String HostSuffix = "/asr/virtual_number/v1/";
    private String Proto = "wss";

    /**
     * OkHttpClient
     */
    private OkHttpClient client;

    /**
     * onopen方法等待时间
     */
    private int onopenWaitTime;

    /**
     * close方法等待时间
     */
    private int closeWaitTime;

    /**
     * 发送数据包失败后重试次数
     */
    private int retryRequestNum;

    /**
     * okhttp参数
     */
    private int maxIdlConnections;

    private int keepAliveDuration;

    private int writeTimeOut;

    private int readTimeOut;

    private int connectTimeOut;

    private boolean useProxy;

    private String proxyHost;

    private int proxyPort;

    private int maxRequests;

    private int maxRequestsPerHost;

    public String getHostSuffix() {
        return HostSuffix;
    }

    public void setHostSuffix(String hostSuffix) {
        HostSuffix = hostSuffix;
    }

    public String getSignPrefixUrl() {
        return SignPrefixUrl;
    }

    public void setSignPrefixUrl(String signPrefixUrl) {
        SignPrefixUrl = signPrefixUrl;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public String getProto() {
        return Proto;
    }

    public void setProto(String proto) {
        Proto = proto;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public int getOnopenWaitTime() {
        return onopenWaitTime;
    }

    public void setOnopenWaitTime(int onopenWaitTime) {
        this.onopenWaitTime = onopenWaitTime;
    }

    public int getCloseWaitTime() {
        return closeWaitTime;
    }

    public void setCloseWaitTime(int closeWaitTime) {
        this.closeWaitTime = closeWaitTime;
    }

    public int getRetryRequestNum() {
        return retryRequestNum;
    }

    public void setRetryRequestNum(int retryRequestNum) {
        this.retryRequestNum = retryRequestNum;
    }

    public int getMaxIdlConnections() {
        return maxIdlConnections;
    }

    public void setMaxIdlConnections(int maxIdlConnections) {
        this.maxIdlConnections = maxIdlConnections;
    }

    public int getKeepAliveDuration() {
        return keepAliveDuration;
    }

    public void setKeepAliveDuration(int keepAliveDuration) {
        this.keepAliveDuration = keepAliveDuration;
    }

    public int getWriteTimeOut() {
        return writeTimeOut;
    }

    public void setWriteTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    public int getMaxRequestsPerHost() {
        return maxRequestsPerHost;
    }

    public void setMaxRequestsPerHost(int maxRequestsPerHost) {
        this.maxRequestsPerHost = maxRequestsPerHost;
    }

    public OkHttpClient initOkHttp() {
        ExecutorService treadPool = treadPool = new ThreadPoolExecutor(10,
                Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.dispatcher(new Dispatcher(treadPool))
                .connectionPool(new ConnectionPool(maxIdlConnections,
                        keepAliveDuration, TimeUnit.MILLISECONDS))
                .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                .connectTimeout(connectTimeOut, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true);
        if (GlobalConfig.ifLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }
        if (useProxy) {
            okHttpBuilder.proxy(new Proxy(Type.HTTP,
                    new InetSocketAddress(proxyHost, proxyPort)));
        }
        client = okHttpBuilder.build();
        client.dispatcher().setMaxRequests(maxRequests);
        client.dispatcher().setMaxRequestsPerHost(maxRequestsPerHost);
        return client;
    }


    /**
     * 保持全局唯一
     *
     * @return VirtualNumberServerConfig
     */
    public static VirtualNumberServerConfig InitVirtualNumberServerConfig() {
        VirtualNumberServerConfig config = new VirtualNumberServerConfig();
        config.setOnopenWaitTime(60);
        config.setCloseWaitTime(60);
        config.setRetryRequestNum(3);
        config.setMaxIdlConnections(600);
        config.setKeepAliveDuration(300000);
        config.setConnectTimeOut(60000);
        config.setUseProxy(false);
        config.setMaxRequests(500);
        config.setMaxRequestsPerHost(5);
        config.setReadTimeOut(60000);
        //必须放到最后初始化 依赖前面配置
        config.setClient(config.initOkHttp());
        return config;
    }

    private static VirtualNumberServerConfig config;

    public static VirtualNumberServerConfig getInstance() {
        if (config == null) {
            synchronized (VirtualNumberServerConfig.class) {
                if (config == null) {
                    config = InitVirtualNumberServerConfig();
                }
            }
        }
        return config;
    }
}
