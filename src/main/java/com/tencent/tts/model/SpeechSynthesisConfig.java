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

import com.tencent.core.model.TConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Setter
@Getter
public class SpeechSynthesisConfig extends TConfig {
    /**
     * 是	本接口取值：TextToStreamAudio，不可更改。
     */
    private String action;

    /**
     * tts url
     */
    private String ttsUrl;

    /**
     * 签名url
     */
    private String signUrl;

    /**
     * 数据上报地址
     */
    private String logUrl;

    @Builder
    public SpeechSynthesisConfig(Long appId, String secretKey, String secretId, String ttsUrl, String signUrl, String logUrl,String token) {
        super(secretId, secretKey, appId,token);
        this.ttsUrl = Optional.ofNullable(ttsUrl).orElse("https://tts.cloud.tencent.com/stream");
        this.signUrl = Optional.ofNullable(signUrl).orElse("https://tts.cloud.tencent.com/stream");
        this.logUrl = Optional.ofNullable(logUrl).orElse("https://asr.tencentcloudapi.com/");
        this.action="TextToStreamAudio";
    }
}
