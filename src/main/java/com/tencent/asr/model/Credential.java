package com.tencent.asr.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credential {

    /**
     * secretId,在控制台申请
     */
    private String secretId;

    /**
     * secretKey,在控制台申请
     */
    private String secretKey;

    /**
     * token
     */
    private String token;
}
