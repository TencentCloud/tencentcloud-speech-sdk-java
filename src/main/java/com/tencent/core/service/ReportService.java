package com.tencent.core.service;


import com.tencent.core.model.GlobalConfig;
import com.tencent.core.model.ReportInfo;
import com.tencent.core.model.TConfig;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.Tutils;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReportService {

    /**
     * 带过期时间map
     */
    private static ExpiringMap<String, String> errorMap = ExpiringMap.builder()
            .maxSize(200)
            .expiration(60, TimeUnit.SECONDS)
            .variableExpiration().expirationPolicy(ExpirationPolicy.CREATED).build();


    public static void report(Boolean success, String code, TConfig config, String id, Object request,
                              Object response, String url, final String e) {
        report(success, code, config, id, request, response, url, e, 0);
    }

    public static void report(Boolean success, String code, TConfig config, String id, Object request,
                              Object response, String url, final String e, long delayTime) {
        try {
            StatService.statAsr(success, code, delayTime);
            StatService.heartbeat();
            if (!success) {
                filterRepeatError(config, id, request, response, url, e, delayTime);
            }
        } catch (Exception exception) {

        }
    }

    /**
     * 过滤重复的错误，避免请求过多
     *
     * @param config   TConfig
     * @param id       id
     * @param request  request
     * @param response response
     * @param url      url
     * @param e        错误
     */
    public static void filterRepeatError(TConfig config, String id, Object request,
                                         Object response, String url, final String e, final long delayTime) {
        //保证线程安全
        synchronized (ReportService.class) {
            if (errorMap.containsKey(id)) {
                return;
            }
            errorMap.put(id, String.valueOf(e));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                reportError(config, id, request, response, url, e, delayTime);
            }
        }).start();
    }

    public static void filterRepeatError(TConfig config, String id, Object request,
                                         Object response, String url, final String e) {
        filterRepeatError(config, id, request, response, url, e, 0);
    }


    /**
     * 上报错误
     *
     * @param config   TConfig
     * @param id       id
     * @param request  request
     * @param response response
     * @param url      url
     * @param e        错误
     */
    public static void reportError(TConfig config, String id, Object request, Object response, String url, String e, long delayTime) {
        ReportInfo reportInfo = new ReportInfo();
        ReportInfo.Log log = ReportInfo.Log.builder().request(request).url(url).delayTime(delayTime).response(response).time(Tutils.getNowData()).build();
        reportInfo.setLog(JsonUtil.toJson(log));
        ReportInfo.AppInfo appInfo = ReportInfo.getAppInfo(e, "_ERROR");
        reportInfo.setAppInfo(JsonUtil.toJson(appInfo));
        doReportError(config.getSecretId(), config.getSecretKey(), config.getToken(), id, reportInfo);
    }


    /**
     * 错误上报
     *
     * @param secretId  secretId
     * @param secretKey secretKey
     * @param id        标志
     * @param data      上报数据
     */
    public static void doReportError(String secretId, String secretKey, String token, String id, Object data) {
        try {
            Credential cred;
            if (StringUtils.isNotEmpty(token)) {
                cred = new Credential(secretId, secretKey, token);
            } else {
                cred = new Credential(secretId, secretKey);
            }
            AsrClient client = new AsrClient(cred, GlobalConfig.region);
            //ReportService.ifLogMessage(id, "Start data reporting:" + JsonUtil.toJson(data), true);
            String resp = client.call("UploadSDKLog", JsonUtil.toJson(data));
            //ReportService.ifLogMessage(id, "Error data report result:" + resp, true);
        } catch (TencentCloudSDKException e) {
            // e.printStackTrace();
        }
    }


    /**
     * 打印日志
     *
     * @param id      日志标示
     * @param message 日志信息
     * @param error   是否异常日志
     */
    public static void ifLogMessage(String id, String message, Boolean error) {
        id = Optional.ofNullable(id).orElse("");
        if (GlobalConfig.ifLog) {
            if (error) {
                log.error("[" + id + "]" + message);
            } else {
                log.info("[" + id + "]" + message);
            }
        }
    }


}
