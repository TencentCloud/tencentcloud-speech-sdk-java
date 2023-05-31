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

package com.tencent.tts.service;

import com.tencent.tts.model.SpeechSynthesisResponse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SpeechSynthesisListener {

    /**
     * 是否取消
     */
    private AtomicBoolean ifCancel = new AtomicBoolean(false);


    //语音合成结束
    public abstract void onComplete(SpeechSynthesisResponse response);

    //语音合成的语音二进制数据
    public abstract void onMessage(byte[] data);

    //语音合成失败
    public abstract void onFail(SpeechSynthesisResponse exception);

    //取消请求 true:取消  false:非取消
    final public void cancel() {
        ifCancel.set(true);
    }

    //是否取消请求
    protected boolean ifCancel() {
        return ifCancel.get();
    }

    protected void setIfCancel(boolean cancel) {
        ifCancel.set(cancel);
    }
}
