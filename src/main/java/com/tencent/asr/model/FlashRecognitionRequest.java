
package com.tencent.asr.model;

import com.tencent.core.model.TRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class FlashRecognitionRequest extends TRequest {

    /**
     * 热词 id。用于调用对应的热词表，如果在调用语音识别服务时，不进行单独的热词 id 设置，自动生效默认热词；如果进行了单独的热词 id 设置，那么将生效单独设置的热词 id。
     */
    protected String hotWordId;

    /**
     * 当前 UNIX 时间戳，可记录发起 API 请求的时间。如果与当前时间相差过大，会引起签名过期错误。可以取值为当前请求的系统时间戳即可。
     */
    protected Long timestamp;

    /**
     * 引擎模型类型。
     */
    protected String engineType;


    protected String voiceFormat;

    /**
     * 是否过滤脏词（目前支持中文普通话引擎）。默认为0。0：不过滤脏词；1：过滤脏词；2：将脏词替换为 * 。
     */
    protected Integer filterDirty;

    /**
     * 是否过滤语气词（目前支持中文普通话引擎）。默认为0。0：不过滤语气词；1：部分过滤；2：严格过滤 。
     */
    protected Integer filterModal;

    /**
     * 是否过滤句末的句号（目前支持中文普通话引擎）。默认为0。0：不过滤句末的句号；1：过滤句末的句号。
     */
    protected Integer filterPunc;

    /**
     * 是否进行阿拉伯数字智能转换。0：全部转为中文数字；1：根据场景智能转换为阿拉伯数字。
     */
    protected Integer convertNumMode;

    /**
     * 是否显示词级别时间戳。0：不显示；1：显示。支持引擎：8k_zh, 8k_zh_finance, 16k_zh, 16k_en, 16k_ca，默认为0。
     */
    protected Integer wordInfo;

    /**
     * 话者分离
     */
    private Integer speakerDiarization;

    /**
     * 通道数
     */
    private Integer firstChannelOnly;

    /**
     * 自学习模型id
     */
    private String customizationId;

    /**
     * 单标点最多字数，取值范围：[6，40]。默认为0，不开启该功能。该参数可用于字幕生成场景，控制单行字幕最大字数。
     */
    private Integer sentenceMaxLength;

    /**
     * 热词增强功能。默认为0，0：不开启，1：开启。
     * 开启后（仅支持8k_zh，16k_zh），将开启同音替换功能，同音字、词在热词中配置。
     * 举例：热词配置“蜜制”并开启增强功能后，与“蜜制”同拼音（mizhi）的“秘制”、“蜜汁”等的识别结果会被强制替换成“蜜制”。因此建议客户根据自己的实际情况开启该功能。
     */
    private Integer reinforceHotword;


    /**
     * 临时热词表，该参数用于提升热词识别准确率。
     * 单个热词规则："热词|权重"，不超过30个字符（最多10个汉字），权重1-10；
     * 临时热词表限制：多个热词用英文逗号分割，最多128个热词，参数示例："腾讯云|10,语音识别|5,ASR|10"；
     * 参数 hotword_list 与 hotword_id 区别：
     * hotword_id：需要先在控制台或接口创建热词表，获得对应hotword_id传入参数来使用热词功能；
     * hotword_list：每次请求时直接传入临时热词表来使用热词功能，云端不保留临时热词表；
     * 注意：如果同时传入了 hotword_id 和 hotword_list，会优先使用 hotword_list。
     */
    private String hotwordList;

    /**
     * 扩展字段
     */
    protected Map<String, Object> extendsParam;


    /**
     * 初始化
     *
     * @return SpeechRecognizerRequest
     */
    public static FlashRecognitionRequest initialize() {
        FlashRecognitionRequest request = new FlashRecognitionRequest();
        return request;
    }
}
