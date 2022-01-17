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

package com.tencent.asr.service;

import com.tencent.asr.model.AsrRequestContent;
import com.tencent.asr.model.SpeechRecognitionSysConfig;
import com.tencent.core.service.ReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

/**
 * HttpClientService
 */
public class HttpClientService {

    private static final String STREAM_TYPE = "application/octet-stream";

    private CloseableHttpAsyncClient asyncClient;

    private CloseableHttpClient syncClient;

    private RequestConfig requestConfig;

    private IOReactorConfig ioReactorConfig;

    private PoolingNHttpClientConnectionManager connManager;

    private volatile boolean closed = false;

    private static HttpClientService ins = new HttpClientService();

    public static HttpClientService getInstance() {
        return ins;
    }

    /**
     * HttpClientService
     */
    public HttpClientService() {
        this.requestConfig = RequestConfig.custom()
                /*  .setConnectTimeout(SpeechRecognitionSysConfig.connectTimeout)
                  .setSocketTimeout(SpeechRecognitionSysConfig.socketTimeout)*/
                .build();
        this.ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(SpeechRecognitionSysConfig.maxRequests)
                .setConnectTimeout(SpeechRecognitionSysConfig.connectTimeout)
                .setSoTimeout(SpeechRecognitionSysConfig.socketTimeout)
                .setInterestOpQueued(SpeechRecognitionSysConfig.interestOpQueued)
                .setSoKeepAlive(true)
                .build();
        ConnectingIOReactor ioReactor = null;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            //ignore
        }
        this.connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        this.connManager.setMaxTotal(SpeechRecognitionSysConfig.MaxTotal);
        this.connManager.setDefaultMaxPerRoute(SpeechRecognitionSysConfig.defaultMaxPerRoute);
        this.asyncClient = createAsyncHttpClient();
        this.asyncClient.start();
        this.syncClient = createSyncHttpClient();
    }

    public void closeClient() {
        try {
            closed = true;
            ReportService.ifLogMessage("", "close client", false);
            this.syncClient.close();
            this.asyncClient.close();
        } catch (Exception e) {
            //ignore
        }
    }

    /**
     * ASR 异步http请求
     *
     * @param stamp 请求标示
     * @param sign 签名
     * @param url 请求URL
     * @param item 请求内容
     * @param callback 回调
     */
    public void asrAsyncHttp(String stamp, String sign, String url, String token,
            AsrRequestContent item, FutureCallback<HttpResponse> callback) {
        if (!closed) {
            ReportService.ifLogMessage(stamp, url, false);
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Authorization", sign);
            httpPost.addHeader("Content-Type", STREAM_TYPE);
            if (StringUtils.isNotEmpty(token)) {
                httpPost.addHeader("X-TC-Token", token);
            }
            httpPost.setEntity(new ByteArrayEntity(item.getBytes()));
            Future future = asyncClient.execute(httpPost, callback);
        }
    }


    /**
     * 同步请求
     *
     * @param stamp 请求标示
     * @param sign 签名
     * @param url 请求URL
     * @param item 请求内容
     * @return CloseableHttpResponse
     */
    public CloseableHttpResponse syncHttp(String stamp, String sign, String url,
            String token, AsrRequestContent item) throws IOException {
        if (!closed) {
            ReportService.ifLogMessage(stamp, "url:" + url, false);
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Authorization", sign);
            httpPost.addHeader("Content-Type", STREAM_TYPE);
            if (StringUtils.isNotEmpty(token)) {
                httpPost.addHeader("X-TC-Token", token);
            }
            httpPost.setEntity(new ByteArrayEntity(item.getBytes()));
            return getSyncClient().execute(httpPost, HttpClientContext.create());
        }
        return null;
    }


    /**
     * 处理httpClient请求结果
     *
     * @param httpResponse httpClient response
     * @param stamp 请求标示
     * @return 解析结果
     */
    public String dealHttpClientResponse(HttpResponse httpResponse, String stamp) {
        String responseStr = null;
        try {
            responseStr = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            ReportService.ifLogMessage(stamp, responseStr, false);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                ReportService.ifLogMessage(stamp, "ERROR:" + responseStr, true);
            }
        } catch (Exception e) {
            ReportService.ifLogMessage(stamp, "Exception:" + e.getMessage() + "_" + responseStr, true);
        } finally {
            try {
                InputStream inputStream = httpResponse.getEntity().getContent();
                inputStream.close();
            } catch (Exception e) {
                // ignore
            }
            if (httpResponse instanceof CloseableHttpResponse) {
                CloseableHttpResponse closeableHttpResponse = (CloseableHttpResponse) httpResponse;
                try {
                    closeableHttpResponse.close();
                } catch (IOException exception) {
                    //ignore
                }
            }
        }
        return responseStr;
    }


    /**
     * 获取异步请求client
     *
     * @return CloseableHttpAsyncClient
     */
    private CloseableHttpAsyncClient getAsyncClient() {
        if (this.asyncClient == null || !asyncClient.isRunning()) {
            synchronized (this) {
                if (this.asyncClient == null || !asyncClient.isRunning()) {
                    this.asyncClient = createAsyncHttpClient();
                    this.asyncClient.start();
                }
            }
        }
        return asyncClient;
    }

    /**
     * 获取同步请求client
     *
     * @return CloseableHttpClient
     */
    private CloseableHttpClient getSyncClient() {
        if (this.syncClient == null) {
            this.syncClient = createSyncHttpClient();
        }
        return syncClient;
    }


    /**
     * 创建异步请求client
     *
     * @return CloseableHttpAsyncClient
     */
    private CloseableHttpAsyncClient createAsyncHttpClient() {
        return HttpAsyncClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    /**
     * 创建同步请求client
     *
     * @return CloseableHttpClient
     */
    private CloseableHttpClient createSyncHttpClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(SpeechRecognitionSysConfig.MaxTotal);
        cm.setDefaultMaxPerRoute(SpeechRecognitionSysConfig.defaultMaxPerRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(SpeechRecognitionSysConfig.connectTimeout)
                .setSocketTimeout(SpeechRecognitionSysConfig.socketTimeout)
                .setConnectionRequestTimeout(SpeechRecognitionSysConfig.connectionRequestTimeout)
                .build();
        return HttpClients.custom().setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig).build();
    }
}
