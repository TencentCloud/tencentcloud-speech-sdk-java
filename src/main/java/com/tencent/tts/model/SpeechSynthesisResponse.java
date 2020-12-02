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

package com.tencent.tts.model;

import com.tencent.core.model.TResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpeechSynthesisResponse extends TResponse {

    /**
     * base64编码的wav/mp3音频数据
     */
    private byte[] Audio;

    /**
     * 一次请求对应一个SessionId
     */
    private String SessionId;

    /**
     * 唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId。
     */
    private String RequestId;

    /**
     * 序号
     */
    private Integer seq;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * message
     */
    private String message;

    /**
     * 编码
     */
    private String code;

    /**
     * 是否结束
     */
    private Boolean end;

    /**
     * 流标志
     */
    private String streamId;
}
