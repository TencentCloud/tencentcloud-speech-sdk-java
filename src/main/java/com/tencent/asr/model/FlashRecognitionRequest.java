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
