package com.tencent.ttsv2;

public class TtsConstant {
    //实时识别请求地址
    public static String DEFAULT_TTS_REQ_URL = "wss://tts.cloud.tencent.com/stream_ws";
    //实时识别签名字符串前缀
    public static String DEFAULT_TTS_SIGN_PREFIX = "GETtts.cloud.tencent.com/stream_ws";

    //实时识别请求地址
    public static String DEFAULT_TTS_V2_REQ_URL = "wss://tts.cloud.tencent.com/stream_wsv2";
    //实时识别签名字符串前缀
    public static String DEFAULT_TTS_V2_SIGN_PREFIX = "GETtts.cloud.tencent.com/stream_wsv2";
    //请求Host
    public static String DEFAULT_HOST = "tts.cloud.tencent.com";
    // start、stop方法countdown超时时间 单位ms
    public static int DEFAULT_START_TIMEOUT_MILLISECONDS = 60000;

    public static int DEFAULT_TTS_FLOWING_STOP_TIMEOUT_MILLISECONDS = 0;

    public static int DEFAULT_TTS_FLOWING_CLOSE_SLEEP_MILLISECONDS = 500;

    private static String FlowingSpeechSynthesizer_ACTION_SYNTHESIS ="ACTION_SYNTHESIS";
    private static String FlowingSpeechSynthesizer_ACTION_COMPLETE ="ACTION_COMPLETE";

    public static String getFlowingSpeechSynthesizer_ACTION_SYNTHESIS() {
        return FlowingSpeechSynthesizer_ACTION_SYNTHESIS;
    }

    public static String getFlowingSpeechSynthesizer_ACTION_COMPLETE() {
        return FlowingSpeechSynthesizer_ACTION_COMPLETE;
    }
}
