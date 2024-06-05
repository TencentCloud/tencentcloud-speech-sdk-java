/*
 * Copyright (c) 2017-2018 THL A29 Limited, a Tencent company. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tencent.soe;

import com.google.gson.Gson;
import com.tencent.core.ws.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class OralEvaluationListener  implements ConnectionListener {

    Logger logger = LoggerFactory.getLogger(OralEvaluationListener.class);
    protected OralEvaluator oralEvaluator;

    private String status = "init";

    public void setOralEvaluation(OralEvaluator oralEvaluator) {
        this.oralEvaluator = oralEvaluator;
    }

    /**
     * 一段话识别中，voice_text_str 为非稳态结果(该段识别结果还可能变化)
     *
     * @param response 识别结果
     */
    public abstract void OnIntermediateResults(OralEvaluationResponse response);

    /**
     * 识别开始
     *
     * @param response 识别结果
     */
    public abstract void onRecognitionStart(OralEvaluationResponse response);


    /**
     * 识别结束
     *
     * @param response 识别结果
     */
    public abstract void onRecognitionComplete(OralEvaluationResponse response);

    /**
     * 错误回调
     *
     * @param response 识别结果
     */
    public abstract void onFail(OralEvaluationResponse response);

    /**
     * 响应结果（包含稳态和非稳态）
     *
     * @param response 识别结果
     */
    public abstract void onMessage(OralEvaluationResponse response);

    @Override
    public void onOpen() {
        logger.debug("onOpen is ok");

    }

    @Override
    public void onClose(int closeCode, String reason) {
        if (oralEvaluator != null) {
            oralEvaluator.markClosed();
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
        OralEvaluationResponse response = gson.fromJson(message, OralEvaluationResponse.class);
        onMessage(response);
        if (isRecReady(response)) {
            onRecognitionStart(response);
            oralEvaluator.markReady();
        } else if (isRecResult(response) && response.getResult() != null) {
            OnIntermediateResults(response);
        } else if (isRecComplete(response)) {
            onRecognitionComplete(response);
            oralEvaluator.markComplete();
        } else if (isTaskFailed(response)) {
            onFail(response);
            oralEvaluator.markFail();
        } else {
            logger.error(message);
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {

    }

    private boolean isRecReady(OralEvaluationResponse response) {
        if (response.getCode() == 0 && Objects.equals(status, "init") && response.getEnd() != 1) {
            status = "rec";
            return true;
        }
        return false;
    }

    private boolean isRecResult(OralEvaluationResponse response) {
        if (response.getCode() == 0 && Objects.equals(status, "rec") && response.getEnd() != 1) {
            return true;
        }
        return false;
    }

    private boolean isRecComplete(OralEvaluationResponse response) {
        if (response.getCode() == 0 && response.getEnd() == 1) {
            status = "complete";
            return true;
        }
        return false;
    }

    private boolean isTaskFailed(OralEvaluationResponse response) {
        int code = response.getCode();
        if (code != 0) {
            status = "failed";
            return true;
        }
        return false;
    }
}
