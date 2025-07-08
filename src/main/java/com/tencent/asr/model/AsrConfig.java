package com.tencent.asr.model;

import com.tencent.core.model.TConfig;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;
import lombok.Setter;

/**
 * 全局配置
 */
@Getter
@Setter
public class AsrConfig extends TConfig {

    private AsrConfig() {
    }

    /**
     * 等待时间 默认6s
     */
    private Long waitTime;

    /**
     * 实时语音服务URL地址。选用默认值即可。
     */
    private String realAsrUrl;
    /**
     * 签名时使用的URL。此参数值仅用于腾讯内部测试。
     */
    private String signUrl;

    /**
     * 数据上报地址
     */
    private String logUrl;

    /**
     * ws签名地址
     */
    private String wsUrl;

    /**
     * ws地址
     */
    private String wsSignUrl;

    /**
     * 极速版地址
     */
    private String flashUrl;

    /**
     * 极速版签名地址
     */
    private String flashSignUrl;


    @Builder
    public AsrConfig(String appId, String secretKey, String secretId,
            Long waitTime, String realAsrUrl, String signUrl,
            String logUrl, String wsUrl, String token) {
        super(Optional.ofNullable(secretId).orElse(""), Optional.ofNullable(secretKey).orElse(""),
                Long.valueOf(Optional.ofNullable(appId).orElse("0")), token);
        this.realAsrUrl = Optional.ofNullable(realAsrUrl).orElse("https://asr.cloud.tencent.com/asr/v1/");
        this.signUrl = Optional.ofNullable(signUrl).orElse("asr.cloud.tencent.com/asr/v1/");
        this.logUrl = Optional.ofNullable(logUrl).orElse("https://asr.tencentcloudapi.com/");
        this.wsUrl = Optional.ofNullable(wsUrl).orElse("wss://asr.cloud.tencent.com/asr/v2/");
        this.wsSignUrl = "asr.cloud.tencent.com/asr/v2/";
        this.flashUrl = "https://asr.cloud.tencent.com/asr/flash/v1/";
        this.flashSignUrl = "asr.cloud.tencent.com/asr/flash/v1/";
        this.waitTime = Optional.ofNullable(waitTime).orElse(6 * 1000L);
    }
}
