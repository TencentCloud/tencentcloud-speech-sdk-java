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

import com.tencent.asr.model.SpeechRecognitionSysConfig;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class WsClientService {
    static OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(SpeechRecognitionSysConfig.wsMaxIdleConnections,
                    SpeechRecognitionSysConfig.wsKeepAliveDuration, TimeUnit.MILLISECONDS))
            .writeTimeout(SpeechRecognitionSysConfig.wsWriteTimeOut, TimeUnit.MILLISECONDS)
            .readTimeout(SpeechRecognitionSysConfig.wsReadTimeOut, TimeUnit.MILLISECONDS)
            .connectTimeout(SpeechRecognitionSysConfig.wsConnectTimeOut, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build();
    static {
        client.dispatcher().setMaxRequests(SpeechRecognitionSysConfig.wsMaxRequests);
        client.dispatcher().setMaxRequestsPerHost(SpeechRecognitionSysConfig.wsMaxRequestsPerHost);
    }
    public static WebSocket asrWebSocket(String token, String wsUrl, String sign, WebSocketListener listener) {
        Headers.Builder builder = new Headers.Builder().add("Authorization", sign)
                .add("Host", "asr.cloud.tencent.com");
        if (StringUtils.isNotEmpty(token)) {
            builder.add("X-TC-Token", token);
        }
        Headers headers = builder.build();
        Request request = new Request.Builder().url(wsUrl).headers(headers).build();
        WebSocket webSocket = client.newWebSocket(request, listener);
        return webSocket;
    }
}
