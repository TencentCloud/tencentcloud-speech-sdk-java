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
package com.tencent.tts.model;

import com.tencent.core.model.TRequest;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeechSynthesisRequest extends TRequest {

    /**
     * 是	Int	当前 UNIX 时间戳，可记录发起 API 请求的时间。如果与当前时间相差过大，会引起签名过期错误。SDK 会自动赋值当前时间戳。
     */
    private Long timestamp;

    /**
     * 是	Int	签名的有效期，是一个符合 UNIX Epoch 时间戳规范的数值，单位为秒；Expired 必须大于 Timestamp 且 Expired-Timestamp 小于90天。SDK 默认设置 1 h。
     */
    private Long expired;


    /**
     * 是	String	一次请求对应一个 SessionId，会原样返回，建议传入类似于 uuid 的字符串防止重复。
     */
    private String sessionId;
    /**
     * 否	Int	模型类型，1：默认模型，此字段只需设置为1即可。
     */
    private Integer modelType;

    /**
     * 否	Float	音量大小，范围：[0，10]，分别对应11个等级的音量，默认值为0，代表正常音量。没有静音选项。
     */
    private Integer volume;

    /**
     * 否	Int	语速，范围：[-2，2]分别对应不同语速：
     * -2代表0.6倍
     * -1代表0.8倍
     * 0代表1.0倍（默认）
     * 1代表1.2倍
     * 2代表1.5倍
     * 输入除以上整数之外的其他参数不生效，按默认值处理。
     * 若需要更细化的语速档次，可以保留小数点一位，如-1.1 0.5 1.7等
     */
    private Float speed;

    /**
     * 否	Int	项目 ID，可以根据控制台-账号中心-项目管理中的配置填写，如无配置请填写默认项目ID:0 。
     */
    private Integer projectId;
    /**
     * 否	Int	详见：语音合成 API 文档中的 VoiceType 参数。
     */
    private Integer voiceType;

    /**
     * 否 主语言类型：1：中文（默认）2：英文
     */
    private Integer primaryLanguage;
    /**
     * 否  Int 音频采样率：
     * 16000:16k（默认）
     * 8000:8k
     */
    private Integer sampleRate;


    /**
     * 否 String 返回音频格式：
     * opus：
     * 返回多段含 opus
     * 压缩分片音频，数据量小，建议使用（默认）。
     * pcm：
     * 返回二进制 pcm
     * 音频，使用简单，但数据量大。
     */
    private String codec;

    /**
     * 扩展字段
     */
    private Map<String, Object> extendsParam;

    /**
     * 对参数进行默认初始化
     *
     * @return TtsRequest
     */
    public static SpeechSynthesisRequest initialize() {
        SpeechSynthesisRequest request = new SpeechSynthesisRequest();
        request.setVoiceType(0);
        request.setVolume(5);

        request.setTimestamp(System.currentTimeMillis() / 1000);
        request.setExpired(request.getTimestamp() + 86400);
        request.setCodec("opus");
        request.setSpeed(0f);
        request.setSampleRate(16000);
        request.setPrimaryLanguage(1);
        request.setModelType(1);
        request.setProjectId(0);
        return request;
    }
}
