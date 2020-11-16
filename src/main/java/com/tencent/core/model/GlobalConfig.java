package com.tencent.core.model;

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
    private static String sdkVer = "1.0.4";

    public static String getSdkVer() {
        return sdkVer;
    }


    /**
     * 是否打印日志
     */
    public static boolean ifLog = false;

    public static String region = "ap-shanghai";

    public static Boolean ifOpenStat = true;

    public static Boolean ifSpeechClient = true;
}
