package com.tencent.tts.model;

import com.tencent.core.model.TConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Setter
@Getter
public class SpeechSynthesisConfig extends TConfig {

    /**
     * 是本接口取值：TextToStreamAudio，不可更改。
     */
    private String action;

    /**
     * tts url
     */
    private String ttsUrl;

    /**
     * 签名url
     */
    private String signUrl;

    /**
     * 数据上报地址
     */
    private String logUrl;

    @Builder
    public SpeechSynthesisConfig(Long appId, String secretKey, String secretId,
            String ttsUrl, String signUrl, String logUrl, String token) {
        super(secretId, secretKey, appId, token);
        this.ttsUrl = Optional.ofNullable(ttsUrl).orElse("https://tts.cloud.tencent.com/stream");
        this.signUrl = Optional.ofNullable(signUrl).orElse("POSTtts.cloud.tencent.com/stream");
        this.logUrl = Optional.ofNullable(logUrl).orElse("https://asr.tencentcloudapi.com/");
        this.action = "TextToStreamAudio";
    }
}
