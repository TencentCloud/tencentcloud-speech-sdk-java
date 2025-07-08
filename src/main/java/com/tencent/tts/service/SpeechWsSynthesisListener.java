package com.tencent.tts.service;

import com.tencent.tts.model.SpeechWsSynthesisResponse;

public class SpeechWsSynthesisListener {

    public String Id;

    public SpeechWsSynthesisListener(String id) {
        this.Id = id;
    }

    public void onSynthesisStart(SpeechWsSynthesisResponse response) {

    }

    public void onSynthesisEnd(SpeechWsSynthesisResponse response) {
    }

    public void onAudioResult(byte[] data) {

    }

    public void onTextResult(SpeechWsSynthesisResponse response) {

    }

    public void onSynthesisFail(SpeechWsSynthesisResponse response) {

    }
}
