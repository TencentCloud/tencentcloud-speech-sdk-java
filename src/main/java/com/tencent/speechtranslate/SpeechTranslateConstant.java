package com.tencent.speechtranslate;

/**
 * 语音翻译常量定义
 */
public class SpeechTranslateConstant {
    // 语音翻译请求地址
    public static String DEFAULT_TRANSLATE_REQ_URL = "wss://asr.cloud.tencent.com/asr/speech_translate/";
    // 语音翻译签名字符串前缀
    public static String DEFAULT_TRANSLATE_SIGN_PREFIX = "asr.cloud.tencent.com/asr/speech_translate/";
    // 请求Host
    public static String DEFAULT_HOST = "asr.cloud.tencent.com";
    // start、stop方法countdown超时时间 单位ms
    public static int DEFAULT_START_TIMEOUT_MILLISECONDS = 15000;
}
