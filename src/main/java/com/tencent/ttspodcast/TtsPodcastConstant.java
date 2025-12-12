package com.tencent.ttspodcast;

import java.util.HashSet;
import java.util.Set;

public class TtsPodcastConstant {
    public static String PODCAST_REQUEST_ACTION = "TextToPodcastStreamAudioWS";
    //大模型播客请求地址
    public static String DEFAULT_PODCAST_REQ_URL = "wss://tts.cloud.tencent.com/stream_ws_podcast";
    //大模型播客签名字符串前缀
    public static String DEFAULT_PODCAST_SIGN_PREFIX = "GETtts.cloud.tencent.com/stream_ws_podcast";

    //三种 InputObject 类型
    public static String INPUT_OBJECT_TYPE_TEXT = "TYPE_TEXT";
    public static String INPUT_OBJECT_TYPE_URL = "TYPE_URL";
    public static String INPUT_OBJECT_TYPE_FILE = "TYPE_FILE";

    public static Set<String> SUPPORT_FILE_FORMAT = new HashSet<>();
    static {
        // "txt", "md", "pdf", "docx"
        SUPPORT_FILE_FORMAT.add("txt");
        SUPPORT_FILE_FORMAT.add("md");
        SUPPORT_FILE_FORMAT.add("pdf");
        SUPPORT_FILE_FORMAT.add("docx");
    }

    //请求Host
    public static String DEFAULT_HOST = "tts.cloud.tencent.com";
    // start、stop方法countdown超时时间 单位ms
    public static int DEFAULT_START_TIMEOUT_MILLISECONDS = 60000;

    public static int DEFAULT_FLOWING_STOP_TIMEOUT_MILLISECONDS = 0;

    public static int DEFAULT_FLOWING_CLOSE_SLEEP_MILLISECONDS = 500;

    //listener 的四种状态
    public static int TTS_PODCAST_LISTENER_STATUS_INIT = 0;
    public static int TTS_PODCAST_LISTENER_STATUS_DOING = 1;
    public static int TTS_PODCAST_LISTENER_STATUS_COMPLETE = 2;
    public static int TTS_PODCAST_LISTENER_STATUS_FAILED = 3;

    public static int TTS_PODCAST_TIMEOUT_CODE = 10009;

    private static String FlowingSpeechSynthesizer_ACTION_SYNTHESIS ="ACTION_SYNTHESIS";
    private static String FlowingSpeechSynthesizer_ACTION_COMPLETE ="ACTION_COMPLETE";

    public static String getFlowingSpeechSynthesizer_ACTION_SYNTHESIS() {
        return FlowingSpeechSynthesizer_ACTION_SYNTHESIS;
    }

    public static String getFlowingSpeechSynthesizer_ACTION_COMPLETE() {
        return FlowingSpeechSynthesizer_ACTION_COMPLETE;
    }
}
