
package com.tencent.asr.model;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsrRequestContent {

    private String streamId;

    /**
     * 语音分片的序号，序号从 0 开始。
     */
    private Integer seq;

    /**
     * 是否为最后一片，最后一片语音片为 1，其余为 0。
     */
    private Integer end;

    /**
     * 数据包
     */
    private byte[] bytes;

    /**
     * 串作为每个音频的唯一标识，用户自己生成。
     */
    private String voiceId;

    /**
     * 计算延迟时间
     */
    private long costTime;

}
