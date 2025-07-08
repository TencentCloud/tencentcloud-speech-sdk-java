package com.tencent.tts.service;

import com.tencent.tts.model.SpeechSynthesisResponse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SpeechSynthesisListener {

    /**
     * 是否取消
     */
    private AtomicBoolean ifCancel = new AtomicBoolean(false);


    //语音合成结束
    public abstract void onComplete(SpeechSynthesisResponse response);

    //语音合成的语音二进制数据
    public abstract void onMessage(byte[] data);

    //语音合成失败
    public abstract void onFail(SpeechSynthesisResponse exception);

    //取消请求 true:取消  false:非取消
    final public void cancel() {
        ifCancel.set(true);
    }

    //是否取消请求
    protected boolean ifCancel() {
        return ifCancel.get();
    }

    protected void setIfCancel(boolean cancel) {
        ifCancel.set(cancel);
    }
}
