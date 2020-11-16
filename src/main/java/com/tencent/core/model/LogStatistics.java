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
package com.tencent.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LogStatistics {

    /**
     * 上报时间
     */
    @JsonProperty(value = "ReportTime")
    private Long reportTime;

    @JsonProperty(value = "AppId")
    private Long AppId;

    /**
     * 总stream个数
     */
    @JsonProperty(value = "StreamNum")
    private AtomicInteger streamNum;

    /**
     * 总voiceid个数
     */
    @JsonProperty(value = "VoiceIdNum")
    private AtomicInteger voiceIdNum;

    /**
     * 总请求次数
     */
    @JsonProperty(value = "ReqNum")
    private AtomicInteger reqNum;

    /**
     * 总成功请求次数
     */
    @JsonProperty(value = "SuccessNum")
    private AtomicInteger successNum;

    /**
     * 总失败请求次数
     */
    @JsonProperty(value = "FailNum")
    private AtomicInteger failNum;

    /**
     * 非业务失败请求次数
     */
    @JsonProperty(value = "NonBusinessFailNum")
    private AtomicInteger nonBusinessFailNum;

    /**
     * 非业务失败请求率
     */
    @JsonProperty(value = "NonBusinessFailNumRate")
    private float nonBusinessFailNumRate;

    /**
     * 业务失败请求次数
     */
    @JsonProperty(value = "BusinessFailNum")
    private AtomicInteger businessFailNum;

    /**
     * 业务失败请求次数
     */
    @JsonProperty(value = "BusinessFailNumRate")
    private float businessFailNumRate;

    /**
     * 失败分类统计（按code统计）
     */
    @JsonProperty(value = "FailCodeStat")
    private ConcurrentHashMap<String,AtomicInteger> FailCodeStat;


    @JsonProperty(value = "DelayTimeStat")
    private ConcurrentHashMap<String,AtomicInteger> delayTimeStat;

    /**
     * 成功率
     */
    @JsonProperty(value = "SuccessRate")
    private float successRate;

    @JsonProperty(value = "Id")
    private String id;


    public static LogStatistics createLogStatistics(){
        LogStatistics statistics=new LogStatistics();
        statistics.setStreamNum(new AtomicInteger(0));
        statistics.setVoiceIdNum(new AtomicInteger(0));
        statistics.setReqNum(new AtomicInteger(0));
        statistics.setSuccessNum(new AtomicInteger(0));
        statistics.setFailNum(new AtomicInteger(0));
        statistics.setBusinessFailNum(new AtomicInteger(0));
        statistics.setNonBusinessFailNum(new AtomicInteger(0));
        statistics.setFailCodeStat(new ConcurrentHashMap<>());
        statistics.setDelayTimeStat(new ConcurrentHashMap<>());
        statistics.setId(UUID.randomUUID().toString());
        statistics.setSuccessRate(1);
        return statistics;
    }

    public static LogStatistics resetLogStatistics(LogStatistics statistics){
        statistics.getStreamNum().set(0);
        statistics.getVoiceIdNum().set(0);
        statistics.getReqNum().set(0);
        statistics.getSuccessNum().set(0);
        statistics.setFailCodeStat(new ConcurrentHashMap<>());
        statistics.setDelayTimeStat(new ConcurrentHashMap<>());
        statistics.setId(UUID.randomUUID().toString());
        statistics.getFailNum().set(0);
        statistics.getBusinessFailNum().set(0);
        statistics.getNonBusinessFailNum().set(0);
        statistics.setSuccessRate(1);
        return statistics;
    }
}
