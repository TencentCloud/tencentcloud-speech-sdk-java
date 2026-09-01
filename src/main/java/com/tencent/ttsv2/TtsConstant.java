package com.tencent.ttsv2;

public class TtsConstant {
    // Real-time recognition request URL.
    public static String DEFAULT_TTS_REQ_URL = "wss://tts.cloud.tencent.com/stream_ws";
    // International account real-time recognition request URL.
    public static String DEFAULT_TTS_INTL_REQ_URL = "wss://tts-international.tencentcloud.com/stream_ws";
    // Prefix of the signature string for real-time recognition.
    public static String DEFAULT_TTS_SIGN_PREFIX = "GETtts.cloud.tencent.com/stream_ws";

    // Real-time recognition request URL.
    public static String DEFAULT_TTS_V2_REQ_URL = "wss://tts.cloud.tencent.com/stream_wsv2";
    // International account real-time recognition request URL.
    public static String DEFAULT_TTS_V2_INTL_REQ_URL = "wss://tts-international.tencentcloud.com/stream_wsv2";
    // Prefix of the signature string for real-time recognition.
    public static String DEFAULT_TTS_V2_SIGN_PREFIX = "GETtts.cloud.tencent.com/stream_wsv2";

    // Account area value indicating a china account (default).
    public static String ACCOUNT_AREA_CHINA = "0";
    // Account area value indicating an international-station account.
    public static String ACCOUNT_AREA_INTERNATIONAL = "1";

    // Request Host.
    public static String DEFAULT_HOST = "tts.cloud.tencent.com";
    // International account request Host.
    public static String DEFAULT_INTL_HOST = "tts-international.tencentcloud.com";
    // Countdown timeout for the start and stop methods, in milliseconds.
    public static int DEFAULT_START_TIMEOUT_MILLISECONDS = 60000;

    public static int DEFAULT_TTS_FLOWING_STOP_TIMEOUT_MILLISECONDS = 0;

    public static int DEFAULT_TTS_FLOWING_CLOSE_SLEEP_MILLISECONDS = 500;

    private static String FlowingSpeechSynthesizer_ACTION_SYNTHESIS ="ACTION_SYNTHESIS";
    private static String FlowingSpeechSynthesizer_ACTION_COMPLETE ="ACTION_COMPLETE";

    private static String FlowingSpeechSynthesizer_ACTION_RESET ="ACTION_RESET";

    public static String getFlowingSpeechSynthesizer_ACTION_SYNTHESIS() {
        return FlowingSpeechSynthesizer_ACTION_SYNTHESIS;
    }

    public static String getFlowingSpeechSynthesizer_ACTION_COMPLETE() {
        return FlowingSpeechSynthesizer_ACTION_COMPLETE;
    }

    public static String getFlowingSpeechSynthesizer_ACTION_RESET() {
        return FlowingSpeechSynthesizer_ACTION_RESET;
    }
}
