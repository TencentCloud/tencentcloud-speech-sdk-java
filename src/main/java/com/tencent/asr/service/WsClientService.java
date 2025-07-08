package com.tencent.asr.service;

import com.tencent.asr.constant.AsrConstant;
import com.tencent.asr.model.SpeechWebsocketConfig;
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
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class WsClientService {

    protected OkHttpClient client;

    /**
     * 自定义场景
     * @param client okhttpclient
     */
    public WsClientService(OkHttpClient client){
        this.client=client;
    }

    /**
     * @param config okhttp client config
     */
    public WsClientService(SpeechWebsocketConfig config) {
        ExecutorService treadPool = config.getExecutorService();
        if (config.getExecutorService() == null) {
            treadPool = new ThreadPoolExecutor(config.getWsMaxRequests(),
                    Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
        }

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.dispatcher(new Dispatcher(treadPool))
                .connectionPool(new ConnectionPool(config.getWsMaxIdleConnections(),
                        config.getWsKeepAliveDuration(), TimeUnit.MILLISECONDS))
                .writeTimeout(config.getWsWriteTimeOut(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getWsReadTimeOut(), TimeUnit.MILLISECONDS)
                .connectTimeout(config.getWsConnectTimeOut(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true);

        if (GlobalConfig.ifLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }
        if (config.getUseProxy() != null && config.getUseProxy()) {
            if (config.getAuthenticator() != null) {
                okHttpBuilder.authenticator(config.getAuthenticator());
            }
            if (config.getProxySelector() != null) {
                okHttpBuilder.proxySelector(config.getProxySelector());
            } else if (config.getProxyHost() != null && config.getProxyPort() != null) {
                okHttpBuilder.proxy(new Proxy(Type.HTTP,
                        new InetSocketAddress(config.getProxyHost(), config.getProxyPort())));
            }
        }
        client = okHttpBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).build();
        client.dispatcher().setMaxRequests(config.getWsMaxRequests());
        client.dispatcher().setMaxRequestsPerHost(config.getWsMaxRequestsPerHost());
    }

    public WebSocket asrWebSocket(String token, String wsUrl, String sign, WebSocketListener listener) {
        Headers.Builder builder = new Headers.Builder().add("Authorization", sign)
                .add("Host", "asr.cloud.tencent.com").add("User-Agent", AsrConstant.SDK);
        if (StringUtils.isNotEmpty(token)) {
            builder.add("X-TC-Token", token);
        }
        Headers headers = builder.build();
        Request request = new Request.Builder().url(wsUrl).headers(headers).build();
        WebSocket webSocket = client.newWebSocket(request, listener);
        return webSocket;
    }
}
