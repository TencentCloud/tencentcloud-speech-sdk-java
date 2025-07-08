package com.tencent.core.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TConfig {

    /**
     * 用户在腾讯云注册账号 AppId 对应的 SecretId，可以进入 API 密钥管理页面 获取。
     */
    public String secretId;

    /**
     * secretKey
     */
    public String secretKey;

    /**
     * 用户在腾讯云注册账号的 AppId，可以进入 API 密钥管理页面 获取。
     */
    public Long appId;

    /**
     * token
     */
    public String token;
}
