package com.tencent.asrspeaker;

import com.google.gson.Gson;
import com.tencent.core.ws.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * 实时语音识别（句子模式 + 话者分离）回调基类。
 * <p>
 * 继承此类并实现以下回调方法：
 * <ul>
 *   <li>{@link #onRecognitionStart} — 识别开始，可在此获取 speakerContextId</li>
 *   <li>{@link #onRecognitionSentences} — 收到句子列表</li>
 *   <li>{@link #onSentenceEnd} — 识别结束</li>
 *   <li>{@link #onFail} — 发生错误</li>
 * </ul>
 */
public abstract class SpeakerRecognitionListener implements ConnectionListener {

    private static final Logger logger = LoggerFactory.getLogger(SpeakerRecognitionListener.class);

    protected SpeakerRecognizer recognizer;
    private String status = "init";

    void setSpeakerRecognizer(SpeakerRecognizer recognizer) {
        this.recognizer = recognizer;
    }

    /**
     * 识别开始，可在此获取 {@link SpeakerRecognitionResponse#getSpeakerContextId()}
     */
    public abstract void onRecognitionStart(SpeakerRecognitionResponse response);

    /**
     * 收到句子列表
     */
    public abstract void onRecognitionSentences(SpeakerRecognitionResponse response);

    /**
     * 识别结束（final=1）
     */
    public abstract void onSentenceEnd(SpeakerRecognitionResponse response);

    /**
     * 发生错误
     */
    public abstract void onFail(SpeakerRecognitionResponse response, Exception error);

    @Override
    public void onOpen() {
    }

    @Override
    public void onClose(int closeCode, String reason) {
        if (recognizer != null) {
            recognizer.markClosed();
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
    }

    @Override
    public void onMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        logger.debug("onMessage: {}", message);

        SpeakerRecognitionResponse response;
        try {
            response = new Gson().fromJson(message, SpeakerRecognitionResponse.class);
        } catch (Exception e) {
            logger.error("failed to parse response: {}", message, e);
            return;
        }

        if (response.getCode() != 0) {
            status = "failed";
            onFail(response, new Exception(String.format("code=%d, message=%s",
                    response.getCode(), response.getMessage())));
            recognizer.markFail();
            return;
        }

        if (Objects.equals(status, "init") && response.getEnd() != 1) {
            status = "rec";
            onRecognitionStart(response);
            recognizer.markReady();
            return;
        }

        if (response.getEnd() == 1) {
            status = "complete";
            onSentenceEnd(response);
            recognizer.markComplete();
            return;
        }

        if (Objects.equals(status, "rec")) {
            onRecognitionSentences(response);
            return;
        }

        logger.error("unhandled message: {}", message);
    }
}
