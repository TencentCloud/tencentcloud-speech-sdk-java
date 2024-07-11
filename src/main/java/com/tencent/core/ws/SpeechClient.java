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
package com.tencent.core.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpeechClient 应全局单例
 */
public class SpeechClient {

    static Logger logger = LoggerFactory.getLogger(SpeechClient.class);
    WebSocketClient client;

    /**
     * 连接建立失败最大重试次数
     */
    public static int connectMaxTryTimes = 3;
    /**
     * 连接建立默认超时时间,单位毫秒
     */
    public static int connectTimeout = 5000;
    /**
     * frame length
     */
    public static int maxFramePayloadLength = 65536 * 50;


    public SpeechClient(String url) {
        try {
            client = new WebSocketClient(url, WebsocketProfile.defaultWebsocketProfile());
        } catch (Exception e) {
            logger.error("fail to create SpeechClient", e);
            throw new RuntimeException(e);
        }
    }

    public SpeechClient(String url, WebsocketProfile profile) {
        try {
            client = new WebSocketClient(url, profile);
        } catch (Exception e) {
            logger.error("fail to create SpeechClient", e);
            throw new RuntimeException(e);
        }
    }

    public Connection connect(ConnectionProfile connectionProfile, ConnectionListener listener) throws Exception {
        for (int i = 0; i <= connectMaxTryTimes; i++) {
            try {
                return client.connect(connectionProfile, listener, connectTimeout, maxFramePayloadLength);
            } catch (Exception e) {
                if (i == 2) {
                    logger.error("failed to connect to server after {} tries,error msg is :{}", i, e.getMessage());
                    throw e;
                }
                Thread.sleep(100);
                logger.warn("failed to connect to server the {} time:{} ,try again ", i, e.getMessage());
            }
        }
        return null;
    }

    /**
     * 在应用的最后调用此方法,释放资源
     */
    public void shutdown() {
        client.shutdown();
    }
}
