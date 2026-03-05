package com.tencent.speechtranslate;

import com.google.gson.Gson;
import com.tencent.core.ws.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * 语音翻译监听器抽象类
 * 用户需继承此类并实现相应的回调方法以获取翻译结果
 */
public abstract class SpeechTranslatorListener implements ConnectionListener {
    Logger logger = LoggerFactory.getLogger(SpeechTranslatorListener.class);
    protected SpeechTranslator translator;

    private String status = "init";
    private String lastSentenceId = "";

    public void setSpeechTranslator(SpeechTranslator translator) {
        this.translator = translator;
    }

    /**
     * 翻译开始
     *
     * @param response 翻译结果
     */
    public abstract void onTranslationStart(SpeechTranslatorResponse response);

    /**
     * 一段话开始翻译
     *
     * @param response 翻译结果
     */
    public abstract void onSentenceBegin(SpeechTranslatorResponse response);

    /**
     * 翻译中间结果，source_text 和 target_text 为非稳态结果（该段结果还可能变化）
     *
     * @param response 翻译结果
     */
    public abstract void onTranslationResultChange(SpeechTranslatorResponse response);

    /**
     * 一段话翻译结束，source_text 和 target_text 为稳态结果（该段结果不再变化）
     *
     * @param response 翻译结果
     */
    public abstract void onSentenceEnd(SpeechTranslatorResponse response);

    /**
     * 翻译完成
     *
     * @param response 翻译结果
     */
    public abstract void onTranslationComplete(SpeechTranslatorResponse response);

    /**
     * 错误回调
     *
     * @param response 翻译结果
     */
    public abstract void onFail(SpeechTranslatorResponse response);

    /**
     * 响应结果（包含稳态和非稳态）
     *
     * @param response 翻译结果
     */
    public abstract void onMessage(SpeechTranslatorResponse response);

    @Override
    public void onOpen() {
        logger.debug("onOpen is ok");
    }

    @Override
    public void onClose(int closeCode, String reason) {
        if (translator != null) {
            translator.markClosed();
        }
        logger.debug("connection is closed due to {}, code:{}", reason, closeCode);
    }

    @Override
    public void onMessage(String message) {
        if (message == null || message.trim().length() == 0) {
            return;
        }
        logger.debug("on message:{}", message);
        Gson gson = new Gson();
        SpeechTranslatorResponse response = gson.fromJson(message, SpeechTranslatorResponse.class);
        onMessage(response);

        if (isTranslateReady(response)) {
            onTranslationStart(response);
            translator.markReady();
        } else if (isTranslateResult(response)) {
            // 判断是否是新句子开始
            String currentSentenceId = response.getSentenceId();
            if (currentSentenceId != null && !Objects.equals(currentSentenceId, lastSentenceId)) {
                lastSentenceId = currentSentenceId;
                onSentenceBegin(response);
            }
            // 判断是否是句子结束
            else if (response.getResult() != null && Boolean.TRUE.equals(response.getResult().getSentenceEnd())) {
                onSentenceEnd(response);
            } else {
                onTranslationResultChange(response);
            }
        } else if (isTranslateComplete(response)) {
            onTranslationComplete(response);
            translator.markComplete();
        } else if (isTaskFailed(response)) {
            onFail(response);
            translator.markFail();
        } else {
            logger.error(message);
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        // 不处理二进制消息
    }

    private boolean isTranslateReady(SpeechTranslatorResponse response) {
        if (response.getCode() == 0 && Objects.equals(status, "init") && response.getEnd() != 1) {
            status = "translate";
            return true;
        }
        return false;
    }

    private boolean isTranslateResult(SpeechTranslatorResponse response) {
        if (response.getCode() == 0 && Objects.equals(status, "translate") && response.getEnd() != 1) {
            return true;
        }
        return false;
    }

    private boolean isTranslateComplete(SpeechTranslatorResponse response) {
        if (response.getCode() == 0 && response.getEnd() == 1) {
            status = "complete";
            return true;
        }
        return false;
    }

    private boolean isTaskFailed(SpeechTranslatorResponse response) {
        int code = response.getCode();
        if (code != 0) {
            status = "failed";
            return true;
        }
        return false;
    }
}
