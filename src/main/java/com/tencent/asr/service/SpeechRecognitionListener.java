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

import com.tencent.asr.model.SpeechRecognitionResponse;

public abstract class SpeechRecognitionListener {

    /**
     * 识别结果
     *
     * @param response 识别结果
     */
    public abstract void onRecognitionResultChange(SpeechRecognitionResponse response);

    /**
     * 识别开始
     *
     * @param response 识别结果
     */
    public abstract void onRecognitionStart(SpeechRecognitionResponse response);

    /**
     * 一句话开始
     *
     * @param response 识别结果
     */
    public abstract void onSentenceBegin(SpeechRecognitionResponse response);

    /**
     * 一句话识别结束
     *
     * @param response 识别结果
     */
    public abstract void onSentenceEnd(SpeechRecognitionResponse response);

    /**
     * 识别结束
     *
     * @param response 识别结果
     */
    public abstract void onRecognitionComplete(SpeechRecognitionResponse response);

    /**
     * 错误回调
     *
     * @param response 识别结果
     */
    public abstract void onFail(SpeechRecognitionResponse response);

}
