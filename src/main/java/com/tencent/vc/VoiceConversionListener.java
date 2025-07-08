package com.tencent.vc;

import com.google.gson.Gson;
import com.tencent.core.ws.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class VoiceConversionListener implements ConnectionListener {
    Logger logger = LoggerFactory.getLogger(VoiceConversionListener.class);
    protected VoiceConverter voiceConverter;

    private String status = "init";

    public void setVoiceConverter(VoiceConverter voiceConverter) {
        this.voiceConverter = voiceConverter;
    }

    public abstract void OnVoiceConversionStart(VoiceConversionResponse response);

    public abstract void OnVoiceConversionResultChange(VoiceConversionResponse response);

    public abstract void OnVoiceConversionComplete(VoiceConversionResponse response);

    /**
     * 请求失败
     *
     * @param response
     */
    public abstract void OnFail(VoiceConversionResponse response);

    @Override
    public void onOpen() {
        logger.debug("onOpen is ok");

    }

    @Override
    public void onClose(int closeCode, String reason) {
        if (voiceConverter != null) {
            voiceConverter.markClosed();
        }
        logger.debug("connection is closed due to {},code:{}", reason, closeCode);

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onMessage(ByteBuffer message) {
        if (message == null) {
            return;
        }
        logger.debug("on message:{}", message);
        byte[] result = new byte[message.remaining()];
        message.get(result);
        VoiceConversionResponse response = VoiceConversionUtils.parseResponse(result);
        if (response == null) {
            logger.error("onMessage response null");
            return;
        }
        if (isRecReady(response)) {
            OnVoiceConversionStart(response);
            voiceConverter.markReady();
        } else if (isRecResult(response)) {
            OnVoiceConversionResultChange(response);
        } else if (isRecComplete(response)) {
            OnVoiceConversionComplete(response);
            voiceConverter.markComplete();
        } else if (isTaskFailed(response)) {
            OnFail(response);
            voiceConverter.markFail();
        } else {
            logger.error(new Gson().toJson(response));
        }

    }

    private boolean isRecReady(VoiceConversionResponse response) {
        if (response.getCode() == 0 && Objects.equals(status, "init") && response.getEnd() != 1) {
            status = "rec";
            return true;
        }
        return false;
    }

    private boolean isRecResult(VoiceConversionResponse response) {
        if (response.getCode() == 0 && Objects.equals(status, "rec") && response.getEnd() != 1) {
            return true;
        }
        return false;
    }

    private boolean isRecComplete(VoiceConversionResponse response) {
        if (response.getCode() == 0 && response.getEnd() == 1) {
            status = "complete";
            return true;
        }
        return false;
    }

    private boolean isTaskFailed(VoiceConversionResponse response) {
        int code = response.getCode();
        if (code != 0) {
            status = "failed";
            return true;
        }
        return false;
    }
}
