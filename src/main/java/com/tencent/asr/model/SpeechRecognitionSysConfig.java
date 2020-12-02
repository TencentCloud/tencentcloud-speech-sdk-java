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

import com.tencent.asr.constant.AsrConstant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpeechRecognitionSysConfig {

    /**
     * IOThread
     */
    public static Integer maxRequests = Runtime.getRuntime().availableProcessors();

    /**
     * 1.0.4以后版本不再使用该属性
     * 处理请求结果线程池大小,默认为cpu核心数*2
     */
    @Deprecated
    public static Integer maxDealResultThreadNum = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 重试次数
     */
    public static int retryRequestNum = 2;

    /**
     * 目前使用httpClient该配置已经废弃，1.0.4以后版本不再使用该属性。http协议请求框架
     */
    @Deprecated
    public static AsrConstant.HttpFrame httpFrame = AsrConstant.HttpFrame.HTTP_CLIENT;

    /**
     * 默认使用websocket
     */
    public static AsrConstant.RequestWay requestWay = AsrConstant.RequestWay.Websocket;

    /**
     * 响应超时时间
     */
    public static int socketTimeout = 120000;

    /**
     * 连接超时时间
     */
    public static int connectTimeout = 1000;

    /**
     * 连接池获取连接超时时间
     */
    public static int connectionRequestTimeout = 1500;


    public static int defaultMaxPerRoute = 2000;
    public static int MaxTotal = 2000;
    /**
     * 是否同步方式
     */
    public static boolean ifSyncHttp = false;

    public static long waitResultTimeout = 1000;

    public static boolean interestOpQueued = true;

    /**
     * wsWriteTimeOut
     */
    public static int wsWriteTimeOut = 60000;
    /**
     * wsReadTimeOut
     */
    public static int wsReadTimeOut = 60000;
    /**
     * wsConnectTimeOut
     */
    public static int wsConnectTimeOut = 60000;
}
