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

import cn.hutool.core.util.RandomUtil;
import com.tencent.asr.constant.AsrConstant;
import com.tencent.asr.constant.AsrConstant.RequestWay;
import com.tencent.asr.model.AsrConfig;
import com.tencent.asr.model.Credential;
import com.tencent.asr.model.SpeechRecognitionRequest;
import com.tencent.asr.model.SpeechRecognitionSysConfig;
import com.tencent.asr.model.SpeechWebsocketConfig;
import com.tencent.asr.service.FlashRecognizer;
import com.tencent.asr.service.SdkRunException;
import com.tencent.asr.service.SpeechHttpRecognizer;
import com.tencent.asr.service.SpeechRecognitionListener;
import com.tencent.asr.service.SpeechRecognizer;
import com.tencent.asr.service.SpeechWsRecognizer;
import com.tencent.asr.service.WsClientService;
import com.tencent.core.model.GlobalConfig;
import com.tencent.core.service.StatService;
import com.tencent.tts.model.SpeechSynthesisConfig;
import com.tencent.tts.model.SpeechSynthesisRequest;
import com.tencent.tts.service.SpeechSynthesisListener;
import com.tencent.tts.service.SpeechSynthesizer;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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

    private WsClientService wsClientService;


    private SpeechClient() {
    }

    /**
     * 创建实例
     *
     * @param appId appId
     * @param secretId secretId
     * @param secretKey secretKey
     * @return SpeechClient
     */
    public static SpeechClient newInstance(String appId, String secretId, String secretKey) {
        return newInstance(appId, secretId, secretKey, null);
    }

    /**
     * 创建实例
     *
     * @param appId appId
     * @param secretId secretId
     * @param secretKey secretKey
     * @param token token
     * @return SpeechClient
     */
    public static SpeechClient newInstance(String appId, String secretId, String secretKey, String token) {
        return newInstance(appId, secretId, secretKey, token, null);
    }

    /**
     * 创建ws实例
     *
     * @param appId appId
     * @param secretId secretId
     * @param secretKey secretKey
     * @param token token
     * @param config ws config
     * @return SpeechClient
     */
    public static SpeechClient newInstance(String appId, String secretId,
            String secretKey, String token, SpeechWebsocketConfig config) {
        if (StringUtils.isEmpty(secretId)) {
            throw new SdkRunException(AsrConstant.Code.CODE_10009);
        }
        if (StringUtils.isEmpty(secretKey)) {
            throw new SdkRunException(AsrConstant.Code.CODE_10009);
        }
        if (speechClient == null) {
            synchronized (SpeechClient.class) {
                if (speechClient == null) {
                    speechClient = new SpeechClient();
                    speechClient.setAppId(appId);
                    speechClient.setSecretId(secretId);
                    speechClient.setSecretKey(secretKey);
                    speechClient.setToken(token);
                    if (RequestWay.Websocket.equals(SpeechRecognitionSysConfig.requestWay)) {
                        if (config == null) {
                            config = SpeechWebsocketConfig.init();
                        }
                        speechClient.wsClientService = new WsClientService(config);
                    }
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
     * @param eventListener 回调
     * @return SpeechSynthesizer
     */
    public SpeechSynthesizer newSpeechSynthesizer(SpeechSynthesisRequest speechSynthesisRequest,
            SpeechSynthesisListener eventListener) {
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
     * @param request 请求参数
     * @param speechRecognitionListener 回调
     * @return SpeechRecognizer
     */
    public SpeechRecognizer newSpeechRecognizer(SpeechRecognitionRequest request,
            SpeechRecognitionListener speechRecognitionListener) {
        AsrConfig config = AsrConfig.builder()
                .appId(this.appId)
                .secretId(this.secretId)
                .secretKey(this.secretKey)
                .token(token)
                .build();
        if (request.getEngineModelType() == null) {
            throw new RuntimeException("engineModelType can not be null,please "
                    + "set SpeechRecognitionRequest EngineModelType !!");
        }
        if (AsrConstant.RequestWay.Http.equals(SpeechRecognitionSysConfig.requestWay)) {
            return new SpeechHttpRecognizer(RandomUtil.randomString(8),
                    config, request, speechRecognitionListener);
        }
        return newWsSpeechRecognizer(request, speechRecognitionListener);
    }

    /**
     * 创建SpeechRecognizer，websocket
     *
     * @param request 请求参数
     * @param speechRecognitionListener 回调
     * @return SpeechRecognizer
     */
    public SpeechRecognizer newWsSpeechRecognizer(SpeechRecognitionRequest request,
            SpeechRecognitionListener speechRecognitionListener) {
        AsrConfig config = AsrConfig.builder()
                .appId(this.appId)
                .secretId(this.secretId)
                .secretKey(this.secretKey)
                .token(token)
                .build();
        if (request.getEngineModelType() == null) {
            throw new RuntimeException("engineModelType can not be null,please "
                    + "set SpeechRecognitionRequest EngineModelType !!");
        }
        return new SpeechWsRecognizer(this.wsClientService, RandomUtil.randomString(8),
                config, request, speechRecognitionListener);
    }


    /**
     * newSpeechFlashRecognizer
     *
     * @return SpeechRecognizer
     */
    public static FlashRecognizer newFlashRecognizer(String appId, Credential credential) {
        AsrConfig config = AsrConfig.builder()
                .appId(appId)
                .secretId(credential.getSecretId())
                .secretKey(credential.getSecretKey())
                .token(credential.getToken())
                .build();
        return new FlashRecognizer(config);
    }
}
