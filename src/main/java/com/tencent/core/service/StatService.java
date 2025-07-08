package com.tencent.core.service;

import com.tencent.core.model.LogStatistics;
import com.tencent.core.model.StatItem;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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

    }

    /**
     * 开始上报统计数据
     */
    public static void startReportStat() {

    }


    /**
     * report
     */
    private static void reportToCloud() {
    }

    /**
     * 心跳保证定时上报，如果没有心跳则停止上报
     */
    public static void heartbeat() {
    }

    /**
     * 上报请求统计数据
     *
     * @param secretId   secretId
     * @param secretKey  secretKey
     * @param statistics statistics
     */
    private static void reportStat(String secretId, String secretKey, LogStatistics statistics) {

    }

    /**
     * 统计
     *
     * @param statItem 记录
     */
    private static void statistics(StatItem statItem) {

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

    }
}
