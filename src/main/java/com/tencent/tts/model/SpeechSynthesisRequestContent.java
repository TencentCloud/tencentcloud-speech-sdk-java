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

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SpeechSynthesisRequestContent {

    /**
     * 是	String	合成语音的源文本。中文最大支持600个汉字（全角标点符号算一个汉字），英文最大支持1800个字母（半角标点符号算一个字母）。包含空格等字符时需要 URL encode 再传输。
     */
    private String text;

    /**
     * 拼接规则 appId_uuid_四位随机字符串_seq_end
     */
    private String sessionId;
}
