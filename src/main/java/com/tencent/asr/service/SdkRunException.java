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
