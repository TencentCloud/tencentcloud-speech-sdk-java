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

package com.tencent.asr.service;

import com.tencent.asr.constant.AsrConstant;
import com.tencent.asr.model.AsrConfig;
import com.tencent.asr.model.AsrRequest;
import com.tencent.asr.model.AsrResponse;
import com.tencent.asr.model.SpeechRecognitionResponse;
import com.tencent.core.handler.BaseEventListener;
import com.tencent.core.handler.RealTimeEventListener;
import com.tencent.core.service.ReportService;
import com.tencent.core.service.TCall;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpeechHttpRecognizer extends HttpBaseService implements TCall<byte[]>, SpeechRecognizer {
    private AtomicBoolean startFlag = new AtomicBoolean(false);


    public SpeechHttpRecognizer(String streamId, AsrConfig config, AsrRequest request,
                                SpeechRecognitionListener speechRecognitionListener) {
        super(streamId, config, request, speechRecognitionListener);
    }

    public SpeechHttpRecognizer(String streamId, AsrConfig config, AsrRequest request,
                                RealTimeEventListener realTimeEventListener,
                                BaseEventListener<AsrResponse> baseEventListener) {
        super(streamId, config, request, realTimeEventListener, baseEventListener);
    }


    /**
     * 请求前置方法，开启线程监听请求结果
     */
    public Boolean start() {
        if (startFlag.get()) {
            return startFlag.get();
        }
        tractionManager.beginTraction(streamId);
        startFlag.set(true);
        startListerResult();
        if (speechRecognitionListener != null) {
            //start
            SpeechRecognitionResponse recognitionResponse = new SpeechRecognitionResponse();
            recognitionResponse.setCode(0);
            recognitionResponse.setFinalSpeech(0);
            recognitionResponse.setStreamId(streamId);
            recognitionResponse.setVoiceId(staging.getVoiceId());
            recognitionResponse.setMessage("success");
            speechRecognitionListener.onRecognitionStart(recognitionResponse);
        }
        return startFlag.get();
    }


    /**
     * 请求后置方法
     */
    public void after() {
        tractionManager.endTraction(streamId);
    }

    /**
     * 兼容1.0.0版本
     *
     * @return 是否结束
     */
    @Override
    public Boolean end() {
        return stop();
    }

    /**
     * 兼容1.0.0版本
     *
     * @param stream 音频数据
     * @return TCall
     * @throws IOException IOException
     */
    @Override
    public TCall execute(byte[] stream) throws IOException {
        write(stream);
        return this;
    }

    /**
     * 结束识别
     *
     * @return 识别标志位 通过该标志可关闭流
     */
    @Override
    public Boolean stop() {
        if (finishFlag.get()) {
            return true;
        }
        ReportService.ifLogMessage(staging.getStreamId(), "speech end", false);
        endFlag.set(true);
        boolean flag = asrRequest.getVoiceFormat() != AsrConstant.VoiceFormat.silk.getFormatId();
        byte[] data = flag ? new byte[2] : new byte[1];
        sendData(data,true);
        after();
        finishFlag.set(true);
        return finishFlag.get();
    }

    /**
     * 识别逻辑的处理方法
     *
     * @param data 语音数据
     */
    @Override
    public void write(byte[] data) {
        if (!startFlag.get()) {
            throw new RuntimeException("please first call start method ");
        }
        if (finishFlag.get()) {
            return;
        }
        sendData(data,false);
    }
}
