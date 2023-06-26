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

import com.tencent.core.model.GlobalConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;

public class SpeechWsSynthesisServerConfig {

    private static SpeechWsSynthesisServerConfig config;

    private String signPrefixUrl = "GETtts.cloud.tencent.com/stream_ws";
    private String host = "tts.cloud.tencent.com";
    private String path = "/stream_ws";
    private String proto = "wss://";
    private String action = "TextToStreamAudioWS";

    /**
     * OkHttpClient
     */
    private OkHttpClient client;

    /**
     * onopen方法等待时间
     */
    private int onopenWaitTime = 6;


    /**
     * 发送数据包失败后重试次数
     */
    private int retryRequestNum = 3;

    public int getOnopenWaitTime() {
        return onopenWaitTime;
    }

    public void setOnopenWaitTime(int onopenWaitTime) {
        this.onopenWaitTime = onopenWaitTime;
    }


    public int getRetryRequestNum() {
        return retryRequestNum;
    }

    public void setRetryRequestNum(int retryRequestNum) {
        this.retryRequestNum = retryRequestNum;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public String getSignPrefixUrl() {
        return signPrefixUrl;
    }

    public void setSignPrefixUrl(String signPrefixUrl) {
        this.signPrefixUrl = signPrefixUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * 初始化SpeechWsSynthesisServerConfig
     *
     * @return
     */
    public static SpeechWsSynthesisServerConfig initSpeechWsSynthesisServerConfig() {
        SpeechWsSynthesisServerConfig config = new SpeechWsSynthesisServerConfig();
        //必须放到最后初始化 依赖前面配置
        config.setClient(config.initOkHttp());
        return config;
    }

    /**
     * 初始化okhttpclient
     *
     * @return
     */
    public OkHttpClient initOkHttp() {
        ExecutorService treadPool = treadPool = new ThreadPoolExecutor(10,
                Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.dispatcher(new Dispatcher(treadPool))
                .connectionPool(new ConnectionPool(600,
                        300000, TimeUnit.MILLISECONDS))
                .writeTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true);
        if (GlobalConfig.ifLog) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(loggingInterceptor);
        }
        client = okHttpBuilder.build();
        client.dispatcher().setMaxRequests(500);
        client.dispatcher().setMaxRequestsPerHost(5);
        return client;
    }

    /**
     * getInstance
     *
     * @return
     */
    public static SpeechWsSynthesisServerConfig getInstance() {
        if (config == null) {
            synchronized (SpeechWsSynthesisServerConfig.class) {
                if (config == null) {
                    config = initSpeechWsSynthesisServerConfig();
                }
            }
        }
        return config;
    }
}
