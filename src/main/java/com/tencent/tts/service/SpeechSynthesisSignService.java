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

package com.tencent.tts.service;

import com.tencent.core.help.SignHelper;
import com.tencent.tts.model.SpeechSynthesisConfig;
import com.tencent.tts.model.SpeechSynthesisRequest;
import com.tencent.tts.model.SpeechSynthesisRequestContent;

import java.util.Map;
import java.util.TreeMap;

class SpeechSynthesisSignService {

    public String signUrl(SpeechSynthesisConfig speechSynthesisConfig, SpeechSynthesisRequest request,
                          SpeechSynthesisRequestContent content, TreeMap<String, Object> map) {
        String paramUrl = SignHelper.createUrl(map);
        return speechSynthesisConfig.getSignUrl() + paramUrl;
    }


    /**
     * 拼装参数 后期如果新增参数，需要在这个方法进行维护，同时需要在{@link SpeechSynthesisRequest} 维护对应的参数
     *
     * @param speechSynthesisConfig
     * @param request
     * @param content
     * @return
     */
    public static TreeMap<String, Object> getParams(SpeechSynthesisConfig speechSynthesisConfig,
                                                    SpeechSynthesisRequest request,
                                                    SpeechSynthesisRequestContent content) {
        TMap<String, Object> treeMap = new TMap<String, Object>();
        treeMap.put("SecretId", speechSynthesisConfig.getSecretId());
        treeMap.put("Text", content.getText());
        treeMap.put("SessionId", content.getSessionId());
        treeMap.put("ModelType", request.getModelType());
        treeMap.put("Volume", request.getVolume());
        //这里需要注意，后段解析float 如果是0.0则解析为0 如果是1.1则解析为1.1
        if (request.getSpeed().intValue() == request.getSpeed()) {
            treeMap.put("Speed", request.getSpeed().intValue());
        } else {
            treeMap.put("Speed", request.getSpeed());
        }
        treeMap.put("VoiceType", request.getVoiceType());
        treeMap.put("PrimaryLanguage", request.getPrimaryLanguage());
        treeMap.put("SampleRate", request.getSampleRate());
        treeMap.put("Codec", request.getCodec());
        treeMap.put("Timestamp", request.getTimestamp());
        treeMap.put("Expired", request.getExpired()); // 1天后过期
        treeMap.put("Action", speechSynthesisConfig.getAction());
        //这里AppID为long类型
        treeMap.put("AppId", speechSynthesisConfig.getAppId());
        treeMap.put("ProjectId", request.getProjectId());

        if (request.getExtendsParam() != null) {
            for (Map.Entry<String, Object> entry : request.getExtendsParam().entrySet()) {
                treeMap.put(entry.getKey(), entry.getValue());
            }
        }
        return treeMap;
    }


    /**
     * 用于过滤Map中值为空的数据
     *
     * @param <K>
     * @param <V>
     */
    public static class TMap<K, V> extends TreeMap {
        @Override
        public Object put(Object key, Object value) {
            if (value != null) {
                return super.put(key, value);
            }
            return null;
        }
    }
}
