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

package com.tencent.asr.service;

import com.tencent.asr.constant.AsrConstant;

/**
 * SdkRunException
 */
public class SdkRunException extends RuntimeException {

    /**
     * error code
     */
    private int code;

    /**
     * error message
     */
    private String message;

    public SdkRunException(AsrConstant.Code code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }


    public SdkRunException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public SdkRunException(String message, int code, String message1) {
        super(message);
        this.code = code;
        this.message = message1;
    }

    public SdkRunException(String message, Throwable cause, int code, String message1) {
        super(message, cause);
        this.code = code;
        this.message = message1;
    }

    public SdkRunException(Throwable cause, int code, String message) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public SdkRunException(Throwable cause, AsrConstant.Code code) {
        super(cause);
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public SdkRunException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
            int code,
            String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.message = message1;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
