package com.tencent.asr.model;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeechRecognitionWsData {

    private Integer seq;

    private Integer end;

    private byte[] data;
}
