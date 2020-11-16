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
package com.tencent;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.tencent.asr.constant.AsrConstant;
import com.tencent.asr.model.AsrConfig;
import com.tencent.asr.model.SpeechRecognitionRequest;
import com.tencent.asr.model.SpeechRecognitionSysConfig;
import com.tencent.asr.service.SpeechHttpRecognizer;
import com.tencent.asr.service.SpeechRecognitionListener;
import com.tencent.asr.service.SpeechRecognizer;
import com.tencent.asr.service.SpeechWsRecognizer;
import com.tencent.core.model.GlobalConfig;
import com.tencent.core.service.StatService;
import com.tencent.tts.model.SpeechSynthesisConfig;
import com.tencent.tts.model.SpeechSynthesisRequest;
import com.tencent.tts.service.SpeechSynthesisListener;
import com.tencent.tts.service.SpeechSynthesizer;
import lombok.Getter;
import lombok.Setter;

/**
 * SpeechClient
 */
@Setter
@Getter
public class SpeechClient {


    private static SpeechClient speechClient;

    private String appId;

    private String secretId;

    private String secretKey;

    private String token;


    private SpeechClient() {
    }

    public static SpeechClient newInstance(String appId, String secretId, String secretKey) {
        return newInstance(appId, secretId, secretKey, null);
    }

    public static SpeechClient newInstance(String appId, String secretId, String secretKey, String token) {
        Assert.isFalse(appId == null, "appId Cannot be empty");
        Assert.notBlank(secretKey, "secretKey Cannot be empty");
        Assert.notBlank(secretId, "secretId Cannot be empty");
        if (speechClient == null) {
            synchronized (SpeechClient.class) {
                if (speechClient == null) {
                    speechClient = new SpeechClient();
                    speechClient.setAppId(appId);
                    speechClient.setSecretId(secretId);
                    speechClient.setSecretKey(secretKey);
                    speechClient.setToken(token);
                    if (GlobalConfig.ifOpenStat) {
                        StatService.setConfig(secretId, secretKey, appId, token);
                        StatService.startReportStat();
                    }
                }
            }
        }
        return speechClient;
    }

    public void setSpeechClientConfig(String appId, String secretId, String secretKey, String token) {
        if (speechClient != null) {
            speechClient.setAppId(appId);
            speechClient.setSecretId(secretId);
            speechClient.setSecretKey(secretKey);
            speechClient.setToken(token);
            if (GlobalConfig.ifOpenStat) {
                StatService.setConfig(secretId, secretKey, appId, token);
            }
        }
    }

    /**
     * 创建SpeechSynthesizer
     *
     * @param speechSynthesisRequest request
     * @param eventListener          回调
     * @return SpeechSynthesizer
     */
    public SpeechSynthesizer newSpeechSynthesizer(SpeechSynthesisRequest speechSynthesisRequest, SpeechSynthesisListener eventListener) {
        SpeechSynthesisConfig config = SpeechSynthesisConfig.builder()
                .appId(Long.valueOf(this.appId))
                .secretId(this.secretId)
                .secretKey(this.secretKey)
                .token(token)
                .build();
        return new SpeechSynthesizer(config, speechSynthesisRequest, eventListener);
    }


    /**
     * 创建SpeechRecognizer，默认使用websocket
     *
     * @param request                   请求参数
     * @param speechRecognitionListener 回调
     * @return SpeechRecognizer
     */
    public SpeechRecognizer newSpeechRecognizer(SpeechRecognitionRequest request, SpeechRecognitionListener speechRecognitionListener) {
        AsrConfig config = AsrConfig.builder()
                .appId(this.appId)
                .secretId(this.secretId)
                .secretKey(this.secretKey)
                .token(token)
                .build();
        if (request.getEngineModelType() == null) {
            throw new RuntimeException("engineModelType can not be null,please set SpeechRecognitionRequest EngineModelType !!");
        }
        if (AsrConstant.RequestWay.Http.equals(SpeechRecognitionSysConfig.requestWay)) {
            return new SpeechHttpRecognizer(RandomUtil.randomString(8), config, request, speechRecognitionListener);
        }
        return new SpeechWsRecognizer(RandomUtil.randomString(8), config, request, speechRecognitionListener);
    }
}
