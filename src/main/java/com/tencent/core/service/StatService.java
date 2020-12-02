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

package com.tencent.core.service;

import com.tencent.asr.model.AsrLogInfo;
import com.tencent.core.model.GlobalConfig;
import com.tencent.core.model.LogStatistics;
import com.tencent.core.model.ReportInfo;
import com.tencent.core.model.StatItem;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.Tutils;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.common.Credential;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StatService {

    private static AtomicBoolean open = new AtomicBoolean(false);

    private static long expireTime = System.currentTimeMillis();

    private static LogStatistics asrStatistics = LogStatistics.createLogStatistics();

    private static BlockingQueue<StatItem> statItemBlockingQueue = new LinkedBlockingQueue<>();

    private static String appId;

    private static String secretId;

    private static String secretKey;

    private static String token;

    public static void setConfig(String secretIds, String secretKeys, String appIds, String tokens) {
        appId = appIds;
        secretId = secretIds;
        secretKey = secretKeys;
        token = tokens;
    }

    public static LogStatistics getAsrStatistics() {
        return asrStatistics;
    }


    /**
     * 统计语音识别请求
     *
     * @param success
     * @param code
     */
    protected static void statAsr(boolean success, String code, long delayTime) {
        if (delayTime > 0) {
            statItemBlockingQueue.add(StatItem.builder().success(success).code(code).delayTime(delayTime).build());
        }
    }

    /**
     * 开始上报统计数据
     */
    public static void startReportStat() {
        if (open.get()) {
            return;
        }
        synchronized (StatService.class) {
            if (open.get()) {
                return;
            }
            ReportService.ifLogMessage("scheduledExecutor", "restart stat", false);
            open.set(true);
            asrStatistics.setAppId(Long.valueOf(appId));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                long sendTime = System.currentTimeMillis();
                while (true) {
                    try {
                        StatItem statItem = statItemBlockingQueue.poll(1, TimeUnit.SECONDS);
                        if (statItem != null) {
                            expireTime = System.currentTimeMillis() + 30000;
                            statistics(statItem);
                        }
                        Long scheduleTime = System.currentTimeMillis() - sendTime;
                        if (scheduleTime >= 10000) {
                            sendTime = System.currentTimeMillis();
                            reportToCloud();
                        }
                    } catch (Exception e) {
                        //ignore
                    } finally {
                        //超过时间且队列为空则退出
                        if (System.currentTimeMillis() > expireTime && statItemBlockingQueue.isEmpty()) {
                            open.set(false);
                            ReportService.ifLogMessage("scheduledExecutor", "break", false);
                            break;
                        }
                    }
                }
            }
        }).start();
    }


    /**
     * report
     */
    private static void reportToCloud() {
        LogStatistics statistics = JsonUtil.fromJson(JsonUtil.toJson(asrStatistics), LogStatistics.class);
        if (statistics.getReqNum().get() > 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            statistics.setSuccessRate(Float.parseFloat(df.format((float) statistics.getSuccessNum().get()
                    * 100 / statistics.getReqNum().get())));
            statistics.setNonBusinessFailNumRate(Float.parseFloat(df.format(
                    (float) statistics.getNonBusinessFailNum().get() * 100 / statistics.getReqNum().get())));
            statistics.setBusinessFailNumRate(Float.parseFloat(df.format(
                    (float) statistics.getBusinessFailNum().get() * 100 / statistics.getReqNum().get())));
        }
        if (statistics.getReqNum().get() == 0 && statistics.getSuccessNum().get() == 0) {
            return;
        }
        reportStat(secretId, secretKey, statistics);
    }

    /**
     * 心跳保证定时上报，如果没有心跳则停止上报
     */
    public static void heartbeat() {
        expireTime = System.currentTimeMillis() + 30000;
        if (!open.get()) {
            startReportStat();
        }
    }

    /**
     * 上报请求统计数据
     *
     * @param secretId   secretId
     * @param secretKey  secretKey
     * @param statistics statistics
     */
    private static void reportStat(String secretId, String secretKey, LogStatistics statistics) {
        Credential cred;
        if (StringUtils.isNotEmpty(token)) {
            cred = new Credential(secretId, secretKey, token);
        } else {
            cred = new Credential(secretId, secretKey);
        }
        AsrClient client = new AsrClient(cred, GlobalConfig.region);
        AsrLogInfo.Log log = new AsrLogInfo.Log();
        statistics.setReportTime(new Date().getTime());
        //必须是这种时间开头的才会写
        log.setTime(Tutils.getNowData());
        log.setStat(statistics);
        //同类的错误只上报一次
        AsrLogInfo cloudReqInfo = new AsrLogInfo();
        cloudReqInfo.setLog(JsonUtil.toJson(log));
        ReportInfo.AppInfo appInfo = ReportInfo.getAppInfo("", "_STAT");
        cloudReqInfo.setAppInfo(JsonUtil.toJson(appInfo));
        try {
            String resp = client.call("UploadSDKLog", JsonUtil.toJson(cloudReqInfo));
            ReportService.ifLogMessage("stamp", "Statistics report results:" + resp, false);
        } catch (Exception e) {
            //
        } finally {
            LogStatistics.resetLogStatistics(asrStatistics);
        }
    }

    /**
     * 统计
     *
     * @param statItem 记录
     */
    private static void statistics(StatItem statItem) {
        asrStatistics.getReqNum().incrementAndGet();
        if (!statItem.getSuccess()) {
            asrStatistics.getFailNum().incrementAndGet();
            ConcurrentHashMap<String, AtomicInteger> map = asrStatistics.getFailCodeStat();
            if (map.get(String.valueOf(statItem.getCode())) != null) {
                map.get(String.valueOf(statItem.getCode())).incrementAndGet();
            } else {
                map.put(String.valueOf(statItem.getCode()), new AtomicInteger(1));
            }
            int codeResult = Integer.valueOf(statItem.getCode());
            if (codeResult > 0) {
                asrStatistics.getBusinessFailNum().incrementAndGet();
            }
            if (codeResult < 0) {
                asrStatistics.getNonBusinessFailNum().incrementAndGet();
            }
        } else {
            asrStatistics.getSuccessNum().incrementAndGet();
        }
        if (statItem.getDelayTime() > 0) {
            setDelayTime(statItem.getDelayTime(), "Delay0To100", 0, 100);
            setDelayTime(statItem.getDelayTime(), "Delay100To200", 100, 200);
            setDelayTime(statItem.getDelayTime(), "Delay200To500", 200, 500);
            setDelayTime(statItem.getDelayTime(), "Delay500To1000", 500, 1000);
            setDelayTime(statItem.getDelayTime(), "Delay1000To6000", 1000, 6000);
            setDelayTime(statItem.getDelayTime(), "Delay6000Over", 6000, Long.MAX_VALUE);
        }
    }

    /**
     * 设置延迟时间
     *
     * @param delayTime 延迟时间
     * @param key       key
     * @param start     开始间隔
     * @param end       结束间隔
     */
    private static void setDelayTime(long delayTime, String key, long start, long end) {
        ConcurrentHashMap<String, AtomicInteger> delayMap = asrStatistics.getDelayTimeStat();
        if (delayTime >= start && delayTime < end) {
            if (delayMap.get(key) != null) {
                delayMap.get(key).incrementAndGet();
            } else {
                delayMap.put(key, new AtomicInteger(1));
            }
        }
    }
}
