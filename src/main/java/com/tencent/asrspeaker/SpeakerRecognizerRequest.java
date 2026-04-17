package com.tencent.asrspeaker;

import com.google.gson.annotations.SerializedName;
import com.tencent.core.ws.CommonRequest;

import java.util.Random;

/**
 * 实时语音识别（句子模式 + 话者分离）请求参数。
 */
public class SpeakerRecognizerRequest extends CommonRequest {

    @SerializedName("secretid")
    private String secretId;

    @SerializedName("timestamp")
    private Long timestamp;

    @SerializedName("expired")
    private Long expired;

    @SerializedName("nonce")
    private Integer nonce;

    @SerializedName("engine_model_type")
    private String engineModelType;

    @SerializedName("voice_id")
    private String voiceId;

    @SerializedName("voice_format")
    private Integer voiceFormat;

    @SerializedName("needvad")
    private Integer needVad;

    @SerializedName("result_mod")
    private final Integer resultMod = 1;

    @SerializedName("sentence_strategy")
    private Integer sentenceStrategy;

    @SerializedName("hotword_id")
    private String hotwordId;

    @SerializedName("hotword_list")
    private String hotwordList;

    @SerializedName("customization_id")
    private String customizationId;

    @SerializedName("convert_num_mode")
    private Integer convertNumMode;

    @SerializedName("vad_silence_time")
    private Integer vadSilenceTime;

    @SerializedName("reinforce_hotword")
    private Integer reinforceHotword;

    @SerializedName("noise_threshold")
    private Float noiseThreshold;

    @SerializedName("replace_text_id")
    private String replaceTextId;

    @SerializedName("speaker_diarization")
    private Integer speakerDiarization;

    @SerializedName("enable_speaker_context")
    private Integer enableSpeakerContext;

    @SerializedName("speaker_context_id")
    private String speakerContextId;

    @SerializedName("language_judgment")
    private Integer languageJudgment;

    @SerializedName("emotion_recognition")
    private Integer emotionRecognition;

    @SerializedName("word_info")
    private Integer wordInfo;

    /**
     * 创建默认请求
     */
    public static SpeakerRecognizerRequest init() {
        SpeakerRecognizerRequest req = new SpeakerRecognizerRequest();
        req.setVoiceFormat(SpeakerConstant.AUDIO_FORMAT_PCM);
        req.setNeedVad(1);
        req.setConvertNumMode(1);
        req.setReinforceHotword(0);
        req.setSentenceStrategy(1);
        req.setEngineModelType("16k_zh");
        req.setNonce(new Random().nextInt(1000000));
        return req;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getExpired() {
        return expired;
    }

    public void setExpired(Long expired) {
        this.expired = expired;
    }

    public Integer getNonce() {
        return nonce;
    }

    public void setNonce(Integer nonce) {
        this.nonce = nonce;
    }

    public String getEngineModelType() {
        return engineModelType;
    }

    public void setEngineModelType(String engineModelType) {
        this.engineModelType = engineModelType;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public Integer getVoiceFormat() {
        return voiceFormat;
    }

    public void setVoiceFormat(Integer voiceFormat) {
        this.voiceFormat = voiceFormat;
    }

    public Integer getNeedVad() {
        return needVad;
    }

    public void setNeedVad(Integer needVad) {
        this.needVad = needVad;
    }

    public Integer getResultMod() {
        return resultMod;
    }

    public Integer getSentenceStrategy() {
        return sentenceStrategy;
    }

    public void setSentenceStrategy(Integer sentenceStrategy) {
        this.sentenceStrategy = sentenceStrategy;
    }

    public String getHotwordId() {
        return hotwordId;
    }

    public void setHotwordId(String hotwordId) {
        this.hotwordId = hotwordId;
    }

    public String getHotwordList() {
        return hotwordList;
    }

    public void setHotwordList(String hotwordList) {
        this.hotwordList = hotwordList;
    }

    public String getCustomizationId() {
        return customizationId;
    }

    public void setCustomizationId(String customizationId) {
        this.customizationId = customizationId;
    }

    public Integer getConvertNumMode() {
        return convertNumMode;
    }

    public void setConvertNumMode(Integer convertNumMode) {
        this.convertNumMode = convertNumMode;
    }

    public Integer getVadSilenceTime() {
        return vadSilenceTime;
    }

    public void setVadSilenceTime(Integer vadSilenceTime) {
        this.vadSilenceTime = vadSilenceTime;
    }

    public Integer getReinforceHotword() {
        return reinforceHotword;
    }

    public void setReinforceHotword(Integer reinforceHotword) {
        this.reinforceHotword = reinforceHotword;
    }

    public Float getNoiseThreshold() {
        return noiseThreshold;
    }

    public void setNoiseThreshold(Float noiseThreshold) {
        this.noiseThreshold = noiseThreshold;
    }

    public String getReplaceTextId() {
        return replaceTextId;
    }

    public void setReplaceTextId(String replaceTextId) {
        this.replaceTextId = replaceTextId;
    }

    public Integer getSpeakerDiarization() {
        return speakerDiarization;
    }

    public void setSpeakerDiarization(Integer speakerDiarization) {
        this.speakerDiarization = speakerDiarization;
    }

    public Integer getEnableSpeakerContext() {
        return enableSpeakerContext;
    }

    public void setEnableSpeakerContext(Integer enableSpeakerContext) {
        this.enableSpeakerContext = enableSpeakerContext;
    }

    public String getSpeakerContextId() {
        return speakerContextId;
    }

    public void setSpeakerContextId(String speakerContextId) {
        this.speakerContextId = speakerContextId;
    }

    public Integer getLanguageJudgment() {
        return languageJudgment;
    }

    public void setLanguageJudgment(Integer languageJudgment) {
        this.languageJudgment = languageJudgment;
    }

    public Integer getEmotionRecognition() {
        return emotionRecognition;
    }

    public void setEmotionRecognition(Integer emotionRecognition) {
        this.emotionRecognition = emotionRecognition;
    }

    public Integer getWordInfo() {
        return wordInfo;
    }

    public void setWordInfo(Integer wordInfo) {
        this.wordInfo = wordInfo;
    }
}
