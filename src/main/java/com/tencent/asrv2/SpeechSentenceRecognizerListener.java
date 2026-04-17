package com.tencent.asrv2;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 句子模式专用 Listener（result_mod=1 时使用）。
 * <p>
 * 将服务端返回的 sentence_list 拆解为逐句回调，用户无需自己遍历列表或判断 sentence_type。
 * <p>
 * 用户只需实现以下三个句子级回调：
 * <ul>
 *   <li>{@link #onSentenceBegin(SentenceItem)} — 一个新 sentence_id 首次出现</li>
 *   <li>{@link #onSentenceChange(SentenceItem)} — 未确定句子内容更新</li>
 *   <li>{@link #onSentenceEnd(SentenceItem)} — 句子确定，不再变化</li>
 * </ul>
 * <p>
 * 连接级回调 {@link #onRecognitionStart}、{@link #onRecognitionComplete}、{@link #onFail}、{@link #onMessage(SpeechRecognizerResponse)}
 * 有默认空实现，按需覆盖即可。
 * <p>
 * 使用方式与 {@link SpeechRecognizerListener} 完全一致，直接传入
 * {@link SpeechRecognizer} 构造函数。
 */
public abstract class SpeechSentenceRecognizerListener extends SpeechRecognizerListener {
    private static final Logger logger = LoggerFactory.getLogger(SpeechSentenceRecognizerListener.class);

    /**
     * 记录已经出现过的 sentence_id，用于区分 begin 和 change
     */
    private final Set<Integer> knownSentenceIds = new HashSet<>();

    private String sentenceStatus = "init";

    /**
     * 一个新句子开始出现（首次出现新的 sentence_id）
     *
     * @param item 句子信息
     */
    public abstract void onSentenceBegin(SentenceItem item);

    /**
     * 未确定句子内容更新（sentence_type=0，同一 sentence_id 的后续推送）
     *
     * @param item 句子信息
     */
    public abstract void onSentenceChange(SentenceItem item);

    /**
     * 句子确定（sentence_type=1），该句子不再变化
     *
     * @param item 句子信息
     */
    public abstract void onSentenceEnd(SentenceItem item);


    @Override
    public void onRecognitionResultChange(SpeechRecognizerResponse response) {
    }

    @Override
    public void onRecognitionStart(SpeechRecognizerResponse response) {
    }

    @Override
    public void onSentenceBegin(SpeechRecognizerResponse response) {
    }

    @Override
    public void onSentenceEnd(SpeechRecognizerResponse response) {
    }

    @Override
    public void onRecognitionComplete(SpeechRecognizerResponse response) {
    }

    @Override
    public void onFail(SpeechRecognizerResponse response) {
    }

    @Override
    public void onMessage(SpeechRecognizerResponse response) {
    }

    @Override
    public void onMessage(String message) {
        if (message == null || message.trim().length() == 0) {
            return;
        }
        logger.debug("on message:{}", message);
        Gson gson = new Gson();
        SpeechRecognizerResponse response = gson.fromJson(message, SpeechRecognizerResponse.class);
        onMessage(response);

        if (isSentenceRecReady(response)) {
            onRecognitionStart(response);
            recognizer.markReady();
        } else if (isSentenceRecResult(response) && response.getSentences() != null) {
            dispatchSentenceItems(response.getSentences());
        } else if (isSentenceRecComplete(response)) {
            onRecognitionComplete(response);
            recognizer.markComplete();
        } else if (isSentenceTaskFailed(response)) {
            onFail(response);
            recognizer.markFail();
        } else {
            logger.error(message);
        }
    }

    private void dispatchSentenceItems(SentenceResult sentences) {
        List<SentenceItem> list = sentences.getSentenceList();
        if (list == null || list.isEmpty()) {
            return;
        }
        for (SentenceItem item : list) {
            int id = item.getSentenceId() != null ? item.getSentenceId() : 0;
            int type = item.getSentenceType() != null ? item.getSentenceType() : 0;

            if (type == 1) {
                if (!knownSentenceIds.contains(id)) {
                    onSentenceBegin(item);
                }
                onSentenceEnd(item);
                knownSentenceIds.remove(id);
            } else {
                if (knownSentenceIds.add(id)) {
                    onSentenceBegin(item);
                } else {
                    onSentenceChange(item);
                }
            }
        }
    }

    private boolean isSentenceRecReady(SpeechRecognizerResponse response) {
        if (response.getCode() == 0 && Objects.equals(sentenceStatus, "init") && response.getEnd() != 1) {
            sentenceStatus = "rec";
            return true;
        }
        return false;
    }

    private boolean isSentenceRecResult(SpeechRecognizerResponse response) {
        return response.getCode() == 0 && Objects.equals(sentenceStatus, "rec") && response.getEnd() != 1;
    }

    private boolean isSentenceRecComplete(SpeechRecognizerResponse response) {
        if (response.getCode() == 0 && response.getEnd() == 1) {
            sentenceStatus = "complete";
            return true;
        }
        return false;
    }

    private boolean isSentenceTaskFailed(SpeechRecognizerResponse response) {
        if (response.getCode() != 0) {
            sentenceStatus = "failed";
            return true;
        }
        return false;
    }
}
