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

import com.tencent.asr.model.VirtualNumberResponse;
import java.util.Date;

public abstract class VirtualNumberRecognitionListener {

    public String Id;

    public VirtualNumberRecognitionListener(String id) {
        this.Id = id;
    }

    // OnRecognitionStart implementation of VirtualNumberRecognitionListener
    public void onRecognitionStart(VirtualNumberResponse response) {
        String message = "OnRecognitionStart: ".concat(this.Id).concat(" ").concat(new Date().toString());
        System.out.println(message);
    }

    // OnRecognitionComplete implementation of VirtualNumberRecognitionListener
    public void onRecognitionComplete(VirtualNumberResponse response) {
        String message = "OnRecognitionComplete: ".concat(this.Id).concat(" ").concat(new Date().toString());
        System.out.println(message);
    }

    // OnFail implementation of VirtualNumberRecognitionListener
    public void onFail(VirtualNumberResponse response) {
        String message = "OnFail: ".concat(this.Id).concat(" ").concat(new Date().toString());
        System.out.println(message);
    }
}
