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

package com.tencent.asr.model;

import cn.hutool.core.util.RandomUtil;
import com.tencent.asr.constant.AsrConstant;
import com.tencent.core.model.TRequest;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsrRequest extends TRequest {


    /**
     * 语音切片字节长度，如果是8k引擎则长度区间为0-3200*5  如果16K引擎则长度区间为0-6400*5.如果超出区间则设置默认值，8k对应默认值为3200， 16k对应默认值为6400。
     * 每次发往服务端的语音分片的字节长度，8K语音建议设为3200,16K语音建议设为6400。
     * <pre>
     * 原因是：
     * 1. 如果设置太小，则发出的分片数量特别多，会影响最终的识别速度；
     * 2. 如果设置太大，则中间结果返回的相对较慢，总的识别速度也并不是最快。
     * </pre>
     */
    protected Integer cutLength;

    /**
     * 腾讯云项目 ID，语音识别目前不区分项目，所以填0即可。
     */
    protected Integer projectId;


    /**
     * 子服务类型。1：实时流式识别。
     */
    protected Integer subServiceType;

    /**
     * 引擎模型类型。
     * • 8k_zh：电话 8k 中文普通话通用；
     * • 8k_zh_finance：电话 8k 金融领域模型；
     * • 16k_zh：16k 中文普通话通用；
     * • 16k_en：16k 英语；
     * • 16k_ca：16k 粤语；
     * • 16k_ko：16k 韩语；
     * • 16k_zh-TW：16k 中文普通话繁体。
     */
    protected String engineModelType;

    /**
     * 热词 id。用于调用对应的热词表，如果在调用语音识别服务时，不进行单独的热词 id 设置，自动生效默认热词；如果进行了单独的热词 id 设置，那么将生效单独设置的热词 id。
     */
    protected String hotWordId;

    /**
     * 识别结果文本编码方式。0：UTF-8；1：GB2312；2：GBK；3：BIG5。
     */
    protected Integer resultTextFormat;

    /**
     * 结果返回方式。 0：同步返回；1：尾包返回。
     */
    protected Integer resType;

    /**
     * 语音编码方式，可选，默认值为 4。1：wav(pcm)；4：speex(sp)；6：silk；8：mp3；10：opus（opus 格式音频流封装说明
     */
    protected Integer voiceFormat;

    /**
     * 0：关闭 vad，1：开启 vad。 如果音频流总时长超过60秒，用户需开启 vad。
     */
    protected Integer needVad;

    /**
     * 语音断句检测阈值，静音时长超过该阈值会被认为断句（多用在智能客服场景，需配合 needvad=1 使用），取值范围150-2000，单位 ms，目前仅支持 8k_zh 引擎模型。
     */
    protected Integer vadSilenceTime;

    /**
     * 默认值为 0。
     */
    protected Integer source;


    /**
     * 当前 UNIX 时间戳，可记录发起 API 请求的时间。如果与当前时间相差过大，会引起签名过期错误。可以取值为当前请求的系统时间戳即可。
     */
    protected Long timestamp;

    /**
     * 签名的有效期，是一个符合 UNIX Epoch 时间戳规范的数值，单位为秒；Expired 必须大于 Timestamp 且 Expired - Timestamp 小于90天。
     */
    protected Long expired;

    /**
     * 设置超时时间，单位为毫秒。
     */
    protected Integer timeout;

    /**
     * 随机正整数。用户需自行生成，最长 10 位。
     */
    protected Integer nonce;

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
     * 扩展字段
     */
    protected Map<String, Object> extendsParam;


    /**
     * 对参数进行默认初始化
     *
     * @return AsrRequest
     */
    public static AsrRequest init() {
        AsrRequest request = new AsrRequest();
        request.projectId = 1013976;
        request.subServiceType = 1;
        request.resultTextFormat = AsrConstant.ResponseEncode.UTF_8.getId();
        request.resType = AsrConstant.ReturnType.REALTIME_FOLLOW.getTypeId();
        request.voiceFormat = AsrConstant.VoiceFormat.wav.getFormatId();
        request.needVad = 1;
        request.source = 0;
        request.timestamp = System.currentTimeMillis() / 1000;
        request.expired = System.currentTimeMillis() / 1000 + 86400;
        request.timeout = 200;
        request.nonce = RandomUtil.randomInt(1000, 99999);
        request.cutLength = 3200;
        return request;
    }
}
