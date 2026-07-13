package com.tencent.asrv2;

/**
 * 实时语音识别 V2 回调接口，用户需实现。
 * 句子模式：服务端按句返回 sentence_list，话者分离作为可选开关。
 */
public interface RealtimeRecognitionListenerV2 {

    /**
     * 识别开始
     *
     * @param response 识别结果
     */
    void onRecognitionStart(RealtimeRecognitionResponseV2 response);

    /**
     * 句子结果推送，每条消息都是句子列表
     *
     * @param response 识别结果
     */
    void onRecognitionSentences(RealtimeRecognitionResponseV2 response);

    /**
     * 识别结束（final=1）
     *
     * @param response 识别结果
     */
    void onSentenceEnd(RealtimeRecognitionResponseV2 response);

    /**
     * 错误回调
     *
     * @param response 识别结果
     * @param e        异常
     */
    void onFail(RealtimeRecognitionResponseV2 response, Exception e);
}
