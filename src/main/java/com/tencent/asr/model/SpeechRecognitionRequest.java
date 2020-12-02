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
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpeechRecognitionRequest extends AsrRequest {

    /**
     * 初始化
     *
     * @return SpeechRecognizerRequest
     */
    public static SpeechRecognitionRequest initialize() {
        SpeechRecognitionRequest request = new SpeechRecognitionRequest();
        request.needVad = 1;
        //request.voiceFormat = AsrConstant.VoiceFormat.sp.getFormatId();
        request.timestamp = System.currentTimeMillis() / 1000;
        request.expired = System.currentTimeMillis() / 1000 + 86400;
        request.nonce = RandomUtil.randomInt(1000, 99999);

        if (AsrConstant.RequestWay.Http.equals(SpeechRecognitionSysConfig.requestWay)) {
            request.resultTextFormat = AsrConstant.ResponseEncode.UTF_8.getId();
            request.resType = AsrConstant.ReturnType.REALTIME_FOLLOW.getTypeId();
            request.subServiceType = 1;
            request.projectId = 1013976;
            request.cutLength = 3200;
            request.timeout = 200;
            request.source = 0;
        }
        return request;
    }
}
