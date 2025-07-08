package com.tencent.core.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatItem {

    Boolean success;

    String code;

    Long delayTime;
}
