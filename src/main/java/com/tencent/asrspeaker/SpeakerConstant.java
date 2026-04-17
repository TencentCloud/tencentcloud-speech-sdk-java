package com.tencent.asrspeaker;

public class SpeakerConstant {

    public static final String DEFAULT_RT_REQ_URL = "wss://asr.cloud.tencent.com/asr/v2/";
    public static final String DEFAULT_RT_SIGN_PREFIX = "asr.cloud.tencent.com/asr/v2/";
    public static final String DEFAULT_HOST = "asr.cloud.tencent.com";
    /** start、stop 方法超时时间，单位 ms */
    public static final int DEFAULT_START_TIMEOUT_MILLISECONDS = 15000;

    public static final int AUDIO_FORMAT_PCM = 1;
    public static final int AUDIO_FORMAT_SPEEX = 4;
    public static final int AUDIO_FORMAT_SILK = 6;
    public static final int AUDIO_FORMAT_MP3 = 8;
    public static final int AUDIO_FORMAT_OPUS = 10;
    public static final int AUDIO_FORMAT_WAV = 12;
    public static final int AUDIO_FORMAT_M4A = 14;
    public static final int AUDIO_FORMAT_AAC = 16;
}
