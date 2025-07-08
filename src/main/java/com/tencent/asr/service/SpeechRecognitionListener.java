package com.tencent.asr.service;

import com.tencent.asr.model.SpeechRecognitionResponse;

public abstract class SpeechRecognitionListener {

    /**
     * 识别结果
     *
     * @param response 识别结果
     */
    public abstract void onRecognitionResultChange(SpeechRecognitionResponse response);

    /**
     * 识别开始
     *
     * @param response 识别结果
     */
    public abstract void onRecognitionStart(SpeechRecognitionResponse response);

    /**
     * 一句话开始
     *
     * @param response 识别结果
     */
    public abstract void onSentenceBegin(SpeechRecognitionResponse response);

    /**
     * 一句话识别结束
     *
     * @param response 识别结果
     */
    public abstract void onSentenceEnd(SpeechRecognitionResponse response);

    /**
     * 识别结束
     *
     * @param response 识别结果
     */
    public abstract void onRecognitionComplete(SpeechRecognitionResponse response);

    /**
     * 错误回调
     *
     * @param response 识别结果
     */
    public abstract void onFail(SpeechRecognitionResponse response);

    /**
     * 响应结果（包含稳态和非稳态）
     *
     * @param response 识别结果
     */
    public abstract void onMessage(SpeechRecognitionResponse response);

}
