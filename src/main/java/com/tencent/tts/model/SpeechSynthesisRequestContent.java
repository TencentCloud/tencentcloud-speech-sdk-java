package com.tencent.tts.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SpeechSynthesisRequestContent {

    /**
     * 合成语音的源文本。中文最大支持600个汉字（全角标点符号算一个汉字），英文最大支持1800个字母（半角标点符号算一个字母）。包含空格等字符时需要 URL encode 再传输。
     */
    private String text;

    /**
     * 拼接规则 appId_uuid_四位随机字符串_seq_end
     */
    private String sessionId;
}
