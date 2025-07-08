package com.tencent.asr.service;

/**
 * 语音识别接口
 */
public interface SpeechRecognizer {

    Boolean start();

    Boolean stop();

    void write(byte[] stream);
}
