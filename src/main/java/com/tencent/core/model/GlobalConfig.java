package com.tencent.core.model;

import com.tencent.core.service.SdkLogInterceptor;
import lombok.Getter;

@Getter
public class GlobalConfig {
    public static String appVerName = "SDK";

    public static String appVerCode = "1.0";

    /**
     * 不建议修改此值
     */
    private static String sdk = "JAVA_TENCENT_CLOUD_SPEECH_SDK";

    public static String getSdk() {
        return sdk;
    }

    /**
     * 不建议修改此值
     */
    private static String sdkVer = "1.0.49";

    public static String getSdkVer() {
        return sdkVer;
    }


    /**
     * 是否打印日志
     */
    public static boolean ifLog = false;

    public static String region = "ap-shanghai";

    /**
     *  私有化场景 其他场景切莫修改
     */
    public static boolean privateSdk = false;

    /**
     * 是否上报请求
     */
    public static Boolean ifOpenStat = false;
    /**
     * 是否上报错误
     */
    public static Boolean ifOpenReportError = true;

    /**
     * 默认
     */
    public static Boolean ifSpeechClient = true;

    /**
     * 日志拦截器
     */
    public static SdkLogInterceptor sdkLogInterceptor = new SdkLogInterceptor();
}
