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

package com.tencent.asr.model;

import java.util.concurrent.ExecutorService;

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

    public static SpeechWebsocketConfig init() {
        SpeechWebsocketConfig config = new SpeechWebsocketConfig();
        config.setWsConnectTimeOut(SpeechRecognitionSysConfig.wsConnectTimeOut);
        config.setWsWriteTimeOut(SpeechRecognitionSysConfig.wsWriteTimeOut);
        config.setWsReadTimeOut(SpeechRecognitionSysConfig.wsReadTimeOut);
        config.setWsKeepAliveDuration(SpeechRecognitionSysConfig.wsKeepAliveDuration);
        config.setWsMaxIdleConnections(SpeechRecognitionSysConfig.wsMaxIdleConnections);
        config.setWsMaxRequests(SpeechRecognitionSysConfig.wsMaxRequests);
        config.setWsMaxRequestsPerHost(SpeechRecognitionSysConfig.wsMaxRequestsPerHost);
        return config;
    }
}
