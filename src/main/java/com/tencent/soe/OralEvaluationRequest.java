package com.tencent.soe;

import com.google.gson.annotations.SerializedName;
import com.tencent.core.help.SignHelper;
import com.tencent.core.ws.CommonRequest;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class OralEvaluationRequest extends CommonRequest {

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
     * 引擎模型类型电话场景：16k_zh：中文； 16k_en：英文
     */
    @SerializedName("server_engine_type")
    protected String serverEngineType;

    /**
     * 音频流识别全局唯一标识，一个 websocket 连接对应一个，用户自己生成（推荐使用 uuid），最长128位。
     */
    @SerializedName("voice_id")
    protected String voiceId;

    /**
     * 语音编码方式，可选，默认值为0。0：pcm；1：wav；2：mp3；4：speex(sp)
     */
    @SerializedName("voice_format")
    protected Integer voiceFormat;
    /**
     * 输入文本模式   0: 普通文本（默认）1：音素结构文本
     */
    @SerializedName("text_mode")
    protected Integer textMode;
    /**
     * 被评估语音对应的文本，仅支持中文和英文。
     * 句子模式下不超过 30个 单词或者中文文字。
     * 段落模式不超过 120 个单词或者中文文字。
     * 中文评估使用 utf-8 编码。
     * 自由说模式 RefText 可以不填。关于 RefText 的文本键入要求，请参考评测模式介绍。
     * 如需要在评测模式下使用自定义注音（支持中英文），可以通过设置 TextMode 参数实现，设置方式请参考音素标注。
     * 示例值：apple
     */
    @SerializedName("ref_text")
    protected String refText;
    /**
     * 主题词和关键词 示例值:keyword
     */
    @SerializedName("keyword")
    protected String keyword;
    /**
     * 评测模式
     * 0：单词/单字模式（中文评测模式下为单字模式）
     * 1：句子模式
     * 2：段落模式
     * 3：自由说模式
     * 4：单词音素纠错模式
     * 5：情景评测模式
     * 6：句子多分支评测模式
     * 7：单词实时评测模式
     * 8：拼音评测模式
     * 关于每种评测模式的详细介绍，以及适用场景，请参考评测模式介绍。
     * 示例值：1
     */
    @SerializedName("eval_mode")
    protected Integer evalMode;
    /**
     * 评价苛刻指数。取值为[1.0 - 4.0]范围内的浮点数，用于平滑不同年龄段的分数。
     * 1.0：适用于最小年龄段用户，一般对应儿童应用场景；
     * 4.0：适用于最高年龄段用户，一般对应成人严格打分场景。
     * 苛刻度影响范围请参考 苛刻指数介绍
     * 示例值：1.0
     */
    @SerializedName("score_coeff")
    protected Double scoreCoeff;
    /**
     * 输出断句中间结果标识
     * 0:不输出(默认) 1:输出，通过设置该参数可以在评估过程中的分 片传输请求中，返回已经评估断句的中间结果，中 间结果可用于客户端 UI 更新
     */
    @SerializedName("sentence_info_enabled")
    protected Integer sentenceInfoEnabled;

    /**
     * 是否为录音识别模式标识
     * 0:  实时识别（默认）
     * 1:  录音识别
     * 录音识别下可发送单个大长度分片(上限300s）
     * 单次连接只能发一个分片,得到识别结果后需要关闭此条websocket连接，再次识别需要重新建立连接
     */
    @SerializedName("rec_mode")
    protected Integer recMode;

    public String getSecretid() {
        return secretid;
    }

    public void setSecretid(String secretid) {
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

    public String getServerEngineType() {
        return serverEngineType;
    }

    public void setServerEngineType(String serverEngineType) {
        this.serverEngineType = serverEngineType;
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

    public Integer getTextMode() {
        return textMode;
    }

    public void setTextMode(Integer textMode) {
        this.textMode = textMode;
    }

    public String getRefText() {
        return refText;
    }

    public void setRefText(String refText) {
        this.refText = refText;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getEvalMode() {
        return evalMode;
    }

    public void setEvalMode(Integer evalMode) {
        this.evalMode = evalMode;
    }

    public Double getScoreCoeff() {
        return scoreCoeff;
    }

    public void setScoreCoeff(Double scoreCoeff) {
        this.scoreCoeff = scoreCoeff;
    }

    public Integer getSentenceInfoEnabled() {
        return sentenceInfoEnabled;
    }

    public void setSentenceInfoEnabled(Integer sentenceInfoEnabled) {
        this.sentenceInfoEnabled = sentenceInfoEnabled;
    }

    public Integer getRecMode() {
        return recMode;
    }

    public void setRecMode(Integer recMode) {
        this.recMode = recMode;
    }
}
