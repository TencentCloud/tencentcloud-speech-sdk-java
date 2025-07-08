package com.tencent.ttsv2;

import com.google.gson.Gson;
import com.tencent.core.ws.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * TextToStreamAudioWSV2回调
 */
public abstract class FlowingSpeechSynthesizerListener implements ConnectionListener {
    Logger logger = LoggerFactory.getLogger(FlowingSpeechSynthesizerListener.class);
    protected FlowingSpeechSynthesizer synthesizer;

    private String status = "init";

    public void setSpeechSynthesizer(FlowingSpeechSynthesizer synthesizer) {
        this.synthesizer = synthesizer;
    }

    public abstract void onSynthesisStart(SpeechSynthesizerResponse response);

    public abstract void onSynthesisEnd(SpeechSynthesizerResponse response);

    public abstract void onAudioResult(ByteBuffer data);

    public abstract void onTextResult(SpeechSynthesizerResponse response);

    /**
     * 请求失败
     *
     * @param response
     */
    public abstract void onSynthesisFail(SpeechSynthesizerResponse response);

    @Override
    public void onOpen() {
        logger.debug("onOpen is ok");

    }

    @Override
    public void onClose(int closeCode, String reason) {
        if (synthesizer != null) {
            synthesizer.markClosed();
        }
        logger.debug("connection is closed due to {},code:{}", reason, closeCode);

    }

    @Override
    public void onMessage(String message) {
        if (message == null || message.trim().length() == 0) {
            return;
        }
        logger.debug("on message:{}", message);
        Gson gson = new Gson();
        SpeechSynthesizerResponse response = gson.fromJson(message, SpeechSynthesizerResponse.class);
        if (isRecReady(response)) {
            onSynthesisStart(response);
            synthesizer.markReady();
        } else if (isRecResult(response) && response.getResult() != null) {
            onTextResult(response);
        } else if (isRecComplete(response)) {
            onSynthesisEnd(response);
            synthesizer.markComplete();
        } else if (isTaskFailed(response)) {
            onSynthesisFail(response);
            synthesizer.markFail();
        } else {
            logger.error(message);
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        onAudioResult(message);
    }

    private boolean isRecReady(SpeechSynthesizerResponse response) {
        if (response.getCode() == 0 && Objects.equals(status, "init") && response.getEnd() != 1) {
            status = "rec";
            return true;
        }
        return false;
    }

    private boolean isRecResult(SpeechSynthesizerResponse response) {
        if (response.getCode() == 0 && Objects.equals(status, "rec") && response.getEnd() != 1) {
            return true;
        }
        return false;
    }

    private boolean isRecComplete(SpeechSynthesizerResponse response) {
        if (response.getCode() == 0 && response.getEnd() == 1) {
            status = "complete";
            return true;
        }
        return false;
    }

    private boolean isTaskFailed(SpeechSynthesizerResponse response) {
        int code = response.getCode();
        if (code != 0) {
            status = "failed";
            return true;
        }
        return false;
    }
}
