package com.tencent.core.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TRequest {
    /**
     * 签名加密串
     */
    private String strToBeEncoded;
}
