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

import com.tencent.asr.model.AsrConfig;
import com.tencent.asr.model.AsrRequest;
import com.tencent.asr.model.AsrRequestContent;
import com.tencent.core.help.SignHelper;

import java.util.Map;
import java.util.TreeMap;

class SpeechRecognitionSignService {

    public String signUrl(AsrConfig asrConfig, AsrRequest request, AsrRequestContent content) {
        String paramUrl = SignHelper.createUrl(getParams(asrConfig, request, content));
        return asrConfig.getRealAsrUrl() + asrConfig.getAppId() + paramUrl;
    }

    public String signWsUrl(AsrConfig asrConfig, AsrRequest request, AsrRequestContent content) {
        String paramUrl = SignHelper.createUrl(getWsParams(asrConfig, request, content));
        return asrConfig.getWsUrl() + asrConfig.getAppId() + paramUrl;
    }


    /**
     * 拼装参数 后期如果新增参数，需要在这个方法进行维护，同时需要在{@link AsrRequest} 维护对应的参数
     *
     * @param asrConfig
     * @param request
     * @param content
     * @return
     */
    private TreeMap<String, Object> getParams(AsrConfig asrConfig, AsrRequest request,
                                              AsrRequestContent content) {
        TMap<String, Object> treeMap = getRequestParamMap(asrConfig, request, content);

        treeMap.put("seq", content.getSeq());
        treeMap.put("end", content.getEnd());
        if (request.getExtendsParam() != null) {
            for (Map.Entry<String, Object> entry : request.getExtendsParam().entrySet()) {
                treeMap.put(entry.getKey(), entry.getValue());
            }
        }
        return treeMap;
    }


    /**
     * 拼装参数 后期如果新增参数，需要在这个方法进行维护，同时需要在{@link AsrRequest} 维护对应的参数
     *
     * @param asrConfig
     * @param request
     * @param content
     * @return
     */
    private TreeMap<String, Object> getWsParams(AsrConfig asrConfig, AsrRequest request,
                                                AsrRequestContent content) {
        TMap<String, Object> treeMap = getRequestParamMap(asrConfig, request, content);
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
    static class TMap<K, V> extends TreeMap {
        @Override
        public Object put(Object key, Object value) {
            if (value != null) {
                return super.put(key, value);
            }
            return null;
        }
    }

    private TMap<String, Object> getRequestParamMap(AsrConfig asrConfig,
                                                    AsrRequest request, AsrRequestContent content) {
        TMap<String, Object> treeMap = new TMap<String, Object>();
        treeMap.put("secretid", asrConfig.getSecretId());
        treeMap.put("projectid", request.getProjectId());
        treeMap.put("sub_service_type", request.getSubServiceType());
        treeMap.put("engine_model_type", request.getEngineModelType());
        treeMap.put("res_type", request.getResType());
        treeMap.put("result_text_format", request.getResultTextFormat());
        treeMap.put("voice_id", content.getVoiceId());
        treeMap.put("timeout", request.getTimeout()); // 暂时默认为200ms
        treeMap.put("source", request.getSource()); // 目前默认为0
        treeMap.put("voice_format", request.getVoiceFormat());
        treeMap.put("timestamp", request.getTimestamp());
        treeMap.put("expired", request.getExpired()); // 1天后过期
        treeMap.put("nonce", request.getNonce());
        treeMap.put("needvad", request.getNeedVad());
        treeMap.put("hotword_id", request.getHotWordId());
        treeMap.put("filter_dirty", request.getFilterDirty());
        treeMap.put("filter_modal", request.getFilterModal());
        treeMap.put("filter_punc", request.getFilterPunc());
        treeMap.put("convert_num_mode", request.getConvertNumMode());
        treeMap.put("word_info", request.getWordInfo());
        treeMap.put("vad_silence_time", request.getVadSilenceTime());
        return treeMap;
    }
}
