package com.tencent.ttspodcast;

import com.google.gson.Gson;
import com.tencent.core.ws.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * TextToStreamAudioWSV2回调
 */
public abstract class TtsPodcastListener implements ConnectionListener {
    Logger logger = LoggerFactory.getLogger(TtsPodcastListener.class);
    protected TtsPodcastSynthesizer synthesizer;

    private int status = TtsPodcastConstant.TTS_PODCAST_LISTENER_STATUS_INIT;

    public void setSpeechSynthesizer(TtsPodcastSynthesizer synthesizer) {
        this.synthesizer = synthesizer;
    }

    public abstract void onSynthesisStart(TtsPodcastResponse response);

    public abstract void onSynthesisEnd(TtsPodcastResponse response);

    public abstract void onAudioResult(ByteBuffer data);

    public abstract void onTextResult(TtsPodcastResponse response);

    public abstract void onSynthesisFail(TtsPodcastResponse response);

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
        TtsPodcastResponse response = gson.fromJson(message, TtsPodcastResponse.class);
        if (isHeartbeatMsg(response)) {
            logger.info("heartbeat");
        } else if (isReadyMsg(response)) {
            onSynthesisStart(response);
            synthesizer.markReady();
        } else if (isResultMsg(response)) {
            // 仅当收到有效的 scripts 才回调 onTextResult
            TtsPodcastScripts[] scripts = response.getResult().getScripts();
            if (scripts != null && scripts.length > 0) {
                onTextResult(response);
            }
        } else if (isCompleteMsg(response)) {
            // 收到 final 主动关闭连接，防止留下半连接
            synthesizer.close("final msg received");
            onSynthesisEnd(response);
            synthesizer.markComplete();
        } else if (isFailedMsg(response)) {
            // 如果不是超时错误，也主动关闭连接
            if (response.getCode() != TtsPodcastConstant.TTS_PODCAST_TIMEOUT_CODE) {
                synthesizer.close("failed msg received");
            }
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

    private boolean isReadyMsg(TtsPodcastResponse rsp) {
        if (rsp.getCode() == 0 && rsp.getReady() == 1 && rsp.getFinal_() != 1) {
            status = TtsPodcastConstant.TTS_PODCAST_LISTENER_STATUS_DOING;
            return true;
        }
        return false;
    }

    private boolean isHeartbeatMsg(TtsPodcastResponse rsp) {
        if (rsp.getCode() == 0 && rsp.getHeartbeat() == 1) {
            return true;
        }
        return false;
    }

    private boolean isResultMsg(TtsPodcastResponse rsp) {
        if (
            rsp.getCode() == 0 &&
            status == TtsPodcastConstant.TTS_PODCAST_LISTENER_STATUS_DOING &&
            rsp.getFinal_() != 1
        ) {
            return rsp.getResult() != null; // result.scripts 可能为 null
        }
        return false;
    }

    private boolean isCompleteMsg(TtsPodcastResponse response) {
        if (response.getCode() == 0 && response.getFinal_() == 1) {
            status = TtsPodcastConstant.TTS_PODCAST_LISTENER_STATUS_COMPLETE;
            return true;
        }
        return false;
    }

    private boolean isFailedMsg(TtsPodcastResponse response) {
        int code = response.getCode();
        if (code != 0) {
            status = TtsPodcastConstant.TTS_PODCAST_LISTENER_STATUS_FAILED;
            return true;
        }
        return false;
    }
}
