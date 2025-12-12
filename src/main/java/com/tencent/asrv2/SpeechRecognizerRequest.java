package com.tencent.asrv2;

import com.google.gson.annotations.SerializedName;
import com.tencent.core.ws.CommonRequest;

import java.util.Random;

/**
 * 实时语音识别请求参数
 */
public class SpeechRecognizerRequest extends CommonRequest {

    /**
     * 腾讯云注册账号的密钥 SecretId，可通过 API 密钥管理页面 获取
     */
    @SerializedName("secretid")
    private String secretid;

    /**
     * 当前 UNIX 时间戳，可记录发起 API 请求的时间。如果与当前时间相差过大，会引起签名过期错误。可以取值为当前请求的系统时间戳即可。
     */
    @SerializedName("timestamp")
    protected Long timestamp;

    /**
     * 签名的有效期，是一个符合 UNIX Epoch 时间戳规范的数值，单位为秒；Expired 必须大于 Timestamp 且 Expired - Timestamp 小于90天。
     */
    @SerializedName("expired")
    protected Long expired;

    /**
     * 随机正整数。用户需自行生成，最长 10 位。
     */
    @SerializedName("nonce")
    protected Integer nonce;

    /**
     * 引擎模型类型
     * 电话场景：
     * 8k_zh：中文电话通用；
     * 8k_zh_finance：中文电话金融；
     * 8k_en：英文电话通用；
     * <p>
     * 非电话场景：
     * 16k_zh：中文通用；
     * 16k_zh-PY：中英粤；
     * 16k_zh-TW：中文繁体；
     * 16k_zh_edu：中文教育；
     * 16k_zh_medical：中文医疗；
     * 16k_zh_court：中文法庭；
     * 16k_yue：粤语；
     * 16k_en：英文通用；
     * 16k_en_game：英文游戏；
     * 16k_en_edu：英文教育；
     * 16k_ko：韩语；
     * 16k_ja：日语；
     * 16k_th：泰语；
     * 16k_id：印度尼西亚语；
     * 16k_vi:越南语；
     * 16k_ms: 马来语；
     * 16k_fil: 菲律宾语；
     * 16k_pt：葡萄牙语；
     * 16k_tr：土耳其语；
     * 16k_ar：阿拉伯语；
     * 16k_es: 西班牙语；
     * 16k_hi: 印地语；
     * 16k_fr：法语；
     * 16k_zh_dialect：多方言
     */
    @SerializedName("engine_model_type")
    protected String engineModelType;

    /**
     * 音频流识别全局唯一标识，一个 websocket 连接对应一个，用户自己生成（推荐使用 uuid），最长128位
     */
    @SerializedName("voice_id")
    protected String voiceId;

    /**
     * 语音编码方式，可选，默认值为4。1：pcm；4：speex(sp)；6：silk；8：mp3；10：opus（opus 格式音频流封装说明）；12：wav；14：m4a（每个分片须是一个完整的 m4a 音频）；16：aac
     */
    @SerializedName("voice_format")
    protected Integer voiceFormat;


    /**
     * 0：关闭 vad，1：开启 vad，默认为0。
     * 如果语音分片长度超过60秒，用户需开启 vad（人声检测切分功能）
     */
    @SerializedName("needvad")
    protected Integer needVad;

    /**
     * 热词表 id。如不设置该参数，自动生效默认热词表；如果设置了该参数，那么将生效对应的热词表
     */
    @SerializedName("hotword_id")
    protected String hotWordId;

    /**
     * 热词增强功能。默认为0，0：不开启，1：开启。
     * 开启后（仅支持8k_zh，16k_zh），将开启同音替换功能，同音字、词在热词中配置。
     * 举例：热词配置“蜜制”并开启增强功能后，与“蜜制”同拼音（mizhi）的“秘制”、“蜜汁”等的识别结果会被强制替换成“蜜制”。因此建议客户根据自己的实际情况开启该功能。
     */
    @SerializedName("reinforce_hotword")
    private Integer reinforceHotword;


    /**
     * 自学习模型 id。如设置了该参数，将生效对应的自学习模型
     */
    @SerializedName("customization_id")
    private String customizationId;


    /**
     * 是否过滤脏词（目前支持中文普通话引擎）。默认为0。0：不过滤脏词；1：过滤脏词；2：将脏词替换为 * 。
     */
    @SerializedName("filter_dirty")
    protected Integer filterDirty;

    /**
     * 是否过滤语气词（目前支持中文普通话引擎）。默认为0。0：不过滤语气词；1：部分过滤；2：严格过滤 。
     */
    @SerializedName("filter_modal")
    protected Integer filterModal;

    /**
     * 是否过滤句末的句号（目前支持中文普通话引擎）。默认为0。0：不过滤句末的句号；1：过滤句末的句号。
     */
    @SerializedName("filter_punc")
    protected Integer filterPunc;

    /**
     * 是否回调识别空结果，默认为1。0：回调空结果；1：不回调空结果;
     * 注意：如果需要slice_type=0和slice_type=2配对回调，需要设置filter_empty_result=0。一般在外呼场景需要配对返回，通过slice_type=0来判断是否有人声出现。
     */
    @SerializedName("filter_empty_result")
    protected Integer filterEmptyResult;

    /**
     * 是否进行阿拉伯数字智能转换（目前支持中文普通话引擎）。0：不转换，直接输出中文数字，1：根据场景智能转换为阿拉伯数字，3: 打开数学相关数字转换。默认值为1
     */
    @SerializedName("convert_num_mode")
    protected Integer convertNumMode;

    /**
     * 是否显示词级别时间戳。0：不显示；1：显示，不包含标点时间戳，2：显示，包含标点时间戳。支持引擎 8k_en、8k_zh、8k_zh_finance、16k_zh、16k_en、16k_ca、16k_zh-TW、16k_ja、16k_wuu-SH，默认为0
     */
    @SerializedName("word_info")
    protected Integer wordInfo;

    /**
     * 语音断句检测阈值，静音时长超过该阈值会被认为断句（多用在智能客服场景，需配合 needvad = 1 使用），取值范围：240-2000（默认1000），单位 ms，此参数建议不要随意调整，可能会影响识别效果，目前仅支持 8k_zh、8k_zh_finance、16k_zh 引擎模型
     */
    @SerializedName("vad_silence_time")
    protected Integer vadSilenceTime;

    /**
     * 强制断句功能，取值范围 5000-90000(单位:毫秒），默认值0(不开启)。 在连续说话不间断情况下，该参数将实现强制断句（此时结果变成稳态，slice_type=2）。如：游戏解说场景，解说员持续不间断解说，无法断句的情况下，将此参数设置为10000，则将在每10秒收到 slice_type=2的回调。 （目前仅支持8k zh/16k zh引擎。）
     */
    @SerializedName("max_speak_time")
    private Integer maxSpeakTime;

    /**
     * 噪音参数阈值，默认为0，取值范围：[-1,1]，对于一些音频片段，取值越大，判定为噪音情况越大。取值越小，判定为人声情况越大。
     * 慎用：可能影响识别效果
     */
    @SerializedName("noise_threshold")
    private Float noiseThreshold;


    /**
     * 临时热词表：该参数用于提升识别准确率。
     * 单个热词限制："热词|权重"，单个热词不超过30个字符（最多10个汉字），权重1-11，如：“腾讯云|5” 或 “ASR|11”；
     * 临时热词表限制：多个热词用英文逗号分割，最多支持128个热词，如：“腾讯云|10,语音识别|5,ASR|11”；
     * 参数 hotword_id（热词表） 与 hotword_list（临时热词表） 区别：
     * hotword_id：热词表。需要先在控制台或接口创建热词表，获得对应hotword_id传入参数来使用热词功能；
     * hotword_list：临时热词表。每次请求时直接传入临时热词表来使用热词功能，云端不保留临时热词表。适用于有极大量热词需求的用户；
     * 注意：
     * 如果同时传入了 hotword_id 和 hotword_list，会优先使用 hotword_list；
     * 热词权重设置为11时，当前热词将升级为超级热词，建议仅将重要且必须生效的热词设置到11，设置过多权重为11的热词将影响整体字准率。
     */
    @SerializedName("hotword_list")
    private String hotwordList;

    /**
     * 支持 pcm 格式的8k音频在与引擎采样率不匹配的情况下升采样到16k后识别，能有效提升识别准确率。仅支持：8000。如：传入 8000 ，则pcm音频采样率为8k，当引擎选用16k_zh， 那么该8k采样率的 pcm 音频可以在16k_zh引擎下正常识别。
     * 注：此参数仅适用于 pcm 格式音频，不传入值将维持默认状态，即默认调用的引擎采样率等于 pcm 音频采样率。
     */
    @SerializedName("input_sample_rate")
    private Integer inputSampleRate;

    @SerializedName("speaker_diarization")
    private Integer speakerDiarization;

    /**
     * 增值付费功能情绪识别能力（目前仅支持 16k_zh,8k_zh）
     * 0：不开启。
     * 1：开启情绪识别，但不在文本展示情绪标签。
     * 2：开启情绪识别，并且在文本展示情绪标签。
     * 默认值为0
     * 支持的情绪分类为：高兴、伤心、愤怒。
     * ﻿
     * 注意：
     * 本功能为增值服务，需将参数设置为1或2时方可按对应方式生效。
     * 如果传入参数值1或2，需确保账号已购买情绪识别资源包，或账号开启后付费；若当前账号已开启后付费功能，并传入参数值1或2，将自动计费。
     * 参数设置为0时，无需购买资源包，也不会消耗情绪识别对应资源。
     * 示例值：0
     */
    @SerializedName("emotion_recognition")
    private Integer emotionRecognition;

    /**
     * 替换词汇表 ID,  适用于热词和自学习场景也无法解决的极端 case 词组，会对识别结果强制替换。具体可参考（词汇替换） ；强制替换功能可能会影响正常识别结果，请谨慎使用。
     * ﻿
     * 注意：
     * 本功能配置完成后，预计在10分钟后生效。
     */
    @SerializedName("replace_text_id")
    private String replaceTextId;

    public Integer getEmotionRecognition() {
        return emotionRecognition;
    }

    public void setEmotionRecognition(Integer emotionRecognition) {
        this.emotionRecognition = emotionRecognition;
    }

    public String getReplaceTextId() {
        return replaceTextId;
    }

    public void setReplaceTextId(String replaceTextId) {
        this.replaceTextId = replaceTextId;
    }

    protected String getSecretid() {
        return secretid;
    }

    protected void setSecretid(String secretid) {
        this.secretid = secretid;
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

    public String getHotWordId() {
        return hotWordId;
    }

    public void setHotWordId(String hotWordId) {
        this.hotWordId = hotWordId;
    }

    public Integer getReinforceHotword() {
        return reinforceHotword;
    }

    public void setReinforceHotword(Integer reinforceHotword) {
        this.reinforceHotword = reinforceHotword;
    }

    public String getCustomizationId() {
        return customizationId;
    }

    public void setCustomizationId(String customizationId) {
        this.customizationId = customizationId;
    }

    public Integer getFilterDirty() {
        return filterDirty;
    }

    public void setFilterDirty(Integer filterDirty) {
        this.filterDirty = filterDirty;
    }

    public Integer getFilterModal() {
        return filterModal;
    }

    public void setFilterModal(Integer filterModal) {
        this.filterModal = filterModal;
    }

    public Integer getFilterPunc() {
        return filterPunc;
    }

    public void setFilterPunc(Integer filterPunc) {
        this.filterPunc = filterPunc;
    }

    public Integer getFilterEmptyResult() {
        return filterEmptyResult;
    }

    public void setFilterEmptyResult(Integer filterEmptyResult) {
        this.filterEmptyResult = filterEmptyResult;
    }

    public Integer getConvertNumMode() {
        return convertNumMode;
    }

    public void setConvertNumMode(Integer convertNumMode) {
        this.convertNumMode = convertNumMode;
    }

    public Integer getWordInfo() {
        return wordInfo;
    }

    public void setWordInfo(Integer wordInfo) {
        this.wordInfo = wordInfo;
    }

    public Integer getVadSilenceTime() {
        return vadSilenceTime;
    }

    public void setVadSilenceTime(Integer vadSilenceTime) {
        this.vadSilenceTime = vadSilenceTime;
    }

    public Integer getMaxSpeakTime() {
        return maxSpeakTime;
    }

    public void setMaxSpeakTime(Integer maxSpeakTime) {
        this.maxSpeakTime = maxSpeakTime;
    }

    public Float getNoiseThreshold() {
        return noiseThreshold;
    }

    public void setNoiseThreshold(Float noiseThreshold) {
        this.noiseThreshold = noiseThreshold;
    }

    public String getHotwordList() {
        return hotwordList;
    }

    public void setHotwordList(String hotwordList) {
        this.hotwordList = hotwordList;
    }

    public Integer getInputSampleRate() {
        return inputSampleRate;
    }

    public void setInputSampleRate(Integer inputSampleRate) {
        this.inputSampleRate = inputSampleRate;
    }

    public Integer getSpeakerDiarization() {
        return speakerDiarization;
    }

    public void setSpeakerDiarization(Integer speakerDiarization) {
        this.speakerDiarization = speakerDiarization;
    }

    public static SpeechRecognizerRequest init() {
        SpeechRecognizerRequest request = new SpeechRecognizerRequest();
        request.setVoiceFormat(12);
        request.setNonce(new Random().nextInt(1000000));
        request.setEngineModelType("16k_zh");
        return request;
    }
}
