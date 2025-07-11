package com.tencent.asr.service;

import com.tencent.asr.model.AsrConfig;
import com.tencent.asr.model.AsrRequest;
import com.tencent.asr.model.AsrRequestContent;
import com.tencent.asr.model.FlashRecognitionRequest;
import com.tencent.core.help.SignHelper;

import java.util.Map;
import java.util.TreeMap;

class SpeechRecognitionSignService {

    public String signUrl(AsrConfig asrConfig, AsrRequest request, AsrRequestContent content) {
        String paramUrl = SignHelper.createUrl(getParams(asrConfig, request, content));
        return asrConfig.getSignUrl() + asrConfig.getAppId() + paramUrl;
    }

    public String signWsUrl(AsrConfig asrConfig, AsrRequest request, AsrRequestContent content) {
        String paramUrl = SignHelper.createUrl(getWsParams(asrConfig, request, content));
        return asrConfig.getWsSignUrl() + asrConfig.getAppId() + paramUrl;
    }

    public String signFlashUrl(String url, AsrConfig asrConfig, FlashRecognitionRequest request) {
        String paramUrl = SignHelper.createUrl(getFlashParams(asrConfig, request));
        return url + asrConfig.getAppId() + paramUrl;
    }

    /**
     * 拼装参数 后期如果新增参数，需要在这个方法进行维护，同时需要在{@link FlashRecognitionRequest} 维护对应的参数
     *
     * @param asrConfig
     * @param request
     * @return
     */
    public  Map<String, Object> getFlashParams(AsrConfig asrConfig, FlashRecognitionRequest request) {
        TMap<String, Object> treeMap = getFlashRequestParamMap(asrConfig, request);
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
    public TreeMap<String, Object> getParams(AsrConfig asrConfig, AsrRequest request,
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
    public TreeMap<String, Object> getWsParams(AsrConfig asrConfig, AsrRequest request,
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
        treeMap.put("customization_id", request.getCustomizationId());
        treeMap.put("noise_threshold", request.getNoiseThreshold());
        treeMap.put("hotword_list", request.getHotwordList());
        treeMap.put("reinforce_hotword", request.getReinforceHotword());
        treeMap.put("max_speak_time", request.getMaxSpeakTime());
        return treeMap;
    }

    private  TMap<String, Object> getFlashRequestParamMap(AsrConfig asrConfig,
                                                         FlashRecognitionRequest request) {
        TMap<String, Object> treeMap = new TMap<String, Object>();
        treeMap.put("secretid", asrConfig.getSecretId());
        treeMap.put("engine_type", request.getEngineType());
        treeMap.put("voice_format", request.getVoiceFormat());
        treeMap.put("timestamp", request.getTimestamp());
        treeMap.put("speaker_diarization", request.getSpeakerDiarization());
        treeMap.put("filter_dirty", request.getFilterDirty());
        treeMap.put("filter_modal", request.getFilterModal());
        treeMap.put("filter_punc", request.getFilterPunc());
        treeMap.put("convert_num_mode", request.getConvertNumMode());
        treeMap.put("word_info", request.getWordInfo());
        treeMap.put("first_channel_only", request.getFirstChannelOnly());
        treeMap.put("hotword_id", request.getHotWordId());
        treeMap.put("customization_id", request.getCustomizationId());
        treeMap.put("sentence_max_length", request.getSentenceMaxLength());
        treeMap.put("hotword_list", request.getHotwordList());
        treeMap.put("reinforce_hotword", request.getReinforceHotword());
        return treeMap;
    }
}
