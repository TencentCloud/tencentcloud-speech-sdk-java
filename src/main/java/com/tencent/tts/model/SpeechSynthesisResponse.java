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
     * wav/mp3音频数据
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
