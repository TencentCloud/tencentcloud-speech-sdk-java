package com.tencent.virtualnumber;

public class VirtualNumberConstant {

    //实时识别请求地址
    public static String DEFAULT_RT_REQ_URL = "wss://asr.cloud.tencent.com/asr/virtual_number/v1/";
    //实时识别签名字符串前缀
    public static String DEFAULT_RT_SIGN_PREFIX = "asr.cloud.tencent.com/asr/virtual_number/v1/";
    //请求Host
    public static String DEFAULT_HOST = "asr.cloud.tencent.com";
    // start、stop方法countdown超时时间 单位ms
    public static int DEFAULT_START_TIMEOUT_MILLISECONDS = 15000;
}
