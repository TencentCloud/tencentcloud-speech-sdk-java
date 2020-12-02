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

import cn.hutool.core.lang.Assert;
import com.tencent.asr.constant.AsrConstant;
import com.tencent.asr.model.AsrConfig;
import com.tencent.asr.model.AsrRequest;
import com.tencent.asr.model.AsrResponse;
import com.tencent.core.handler.BaseEventListener;
import com.tencent.core.handler.RealTimeEventListener;
import com.tencent.core.service.ReportService;
import com.tencent.core.service.StatService;
import com.tencent.core.service.TCall;
import com.tencent.core.service.TClient;
import com.tencent.core.utils.Tutils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Asr客户端
 */
public class AsrClient implements TClient {
    /**
     * 配置
     */
    private AsrConfig asrConfig;

    /**
     * 处理流线程池
     */
    private ThreadPoolExecutor executor;

    /**
     * 构造方法
     *
     * @param config          配置{@link AsrConfig}
     * @param executorService 配置处理流的线程池，默认线程池大小为cpu核心*2
     */
    public AsrClient(AsrConfig config, ThreadPoolExecutor executorService) {
        checkConfig(config);
        this.asrConfig = config;
        this.executor = executorService;
    }

    /**
     * 实例化方法
     *
     * @param config 配置{@link AsrConfig}
     * @return Asr客户端
     */
    public static AsrClient newInstance(AsrConfig config) {
        return new AsrClient(config, null);
    }

    /**
     * 实例化Client
     *
     * @param config          配置{@link AsrConfig}
     * @param executorService 线程池
     * @return Asr客户端
     */
    public static AsrClient newInstance(AsrConfig config, ThreadPoolExecutor executorService) {
        return new AsrClient(config, executorService);
    }


    /**
     * 开启错误上报和统计
     */
    @Override
    public void start() {
        StatService.setConfig(asrConfig.getSecretId(), asrConfig.getSecretKey(),
                String.valueOf(asrConfig.getAppId()), null);
        StatService.startReportStat();
    }

    /**
     * 正常关闭请求线程池
     */
    @Override
    public void close() {
        Tutils.closeThreadPool(executor);
        //AsrLogService.shutdown();
        ReportService.ifLogMessage("close", "Close the request thread pool", false);
    }


    /**
     * 创建请求
     *
     * @param streamId              流的唯一标示
     * @param request               请求参数 {@link AsrRequest}
     * @param realTimeEventListener 监听器 {@link RealTimeEventListener}
     * @param baseEventListener     回调
     * @param type                  类型表示具体是通过http还是ws请求，指定请求参数是流或byte[]
     * @return 执行语音识别TCall
     */
    public TCall newCall(String streamId, AsrRequest request, BaseEventListener<AsrResponse> baseEventListener,
                         RealTimeEventListener realTimeEventListener, AsrConstant.DataType type) {
        if (AsrConstant.DataType.BYTE.equals(type)) {
            SpeechHttpRecognizer speechRecognizer = new SpeechHttpRecognizer(streamId, asrConfig, request,
                    realTimeEventListener, baseEventListener);
            speechRecognizer.start();
            return speechRecognizer;
        }
        HttpStreamService call = new HttpStreamService(streamId, asrConfig, request,
                realTimeEventListener, executor, baseEventListener);
        call.start();
        return call;
    }


    /**
     * 创建请求
     *
     * @param streamId              流的唯一标示
     * @param request               请求参数 {@link AsrRequest}
     * @param realTimeEventListener 监听器 {@link RealTimeEventListener}
     * @param type                  类型表示具体是通过http还是ws请求，指定请求参数是流或byte[]
     * @return 执行语音识别TCall
     */
    public TCall newCall(String streamId, AsrRequest request, RealTimeEventListener realTimeEventListener,
                         AsrConstant.DataType type) {
        return newCall(streamId, request, null, realTimeEventListener, type);
    }


    /**
     * 校验必填参数
     *
     * @param asrConfig 配置{@link AsrConfig}
     */
    private void checkConfig(AsrConfig asrConfig) {
        Assert.isFalse(asrConfig == null, "asrConfig Cannot be empty");
        Assert.isFalse(asrConfig.getAppId() == null, "appId Cannot be empty");
        Assert.notBlank(asrConfig.getSecretKey(), "secretKey Cannot be empty");
        Assert.notBlank(asrConfig.getSecretId(), "secretId Cannot be empty");
    }
}
