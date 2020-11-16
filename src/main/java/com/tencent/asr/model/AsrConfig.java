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

import com.tencent.core.model.TConfig;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

/**
 * 全局配置
 */
@Getter
public class AsrConfig extends TConfig {

    private AsrConfig() {
    }

    /**
     * 等待时间 默认6s
     */
    private Long waitTime;

    /**
     * 实时语音服务URL地址。选用默认值即可。
     */
    private String realAsrUrl;
    /**
     * 签名时使用的URL。此参数值仅用于腾讯内部测试。
     */
    private String signUrl;

    /**
     * 数据上报地址
     */
    private String logUrl;

    /**
     * ws地址
     */
    private String wsUrl;


    @Builder
    public AsrConfig(String appId, String secretKey, String secretId,
                     Long waitTime, String realAsrUrl, String signUrl, String logUrl, String wsUrl,String token) {
        super(secretId, secretKey, Long.valueOf(appId),token);
        this.realAsrUrl = Optional.ofNullable(realAsrUrl).orElse("https://asr.cloud.tencent.com/asr/v1/");
        this.signUrl = Optional.ofNullable(signUrl).orElse("https://asr.cloud.tencent.com/asr/v1/");
        this.logUrl = Optional.ofNullable(logUrl).orElse("https://asr.tencentcloudapi.com/");
        this.wsUrl = Optional.ofNullable(wsUrl).orElse("wss://asr.cloud.tencent.com/asr/v2/");
        this.waitTime = Optional.ofNullable(waitTime).orElse(6 * 1000L);
    }
}
