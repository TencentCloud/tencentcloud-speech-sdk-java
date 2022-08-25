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


import com.tencent.core.model.GlobalConfig;
import com.tencent.core.model.ReportInfo;
import com.tencent.core.model.TConfig;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.Tutils;
import java.util.Optional;

public class ReportService {


    public static void report(Boolean success, String code, TConfig config, String id, Object request,
            Object response, String url, final String e) {
        report(success, code, config, id, request, response, url, e, 0);
    }

    public static void report(Boolean success, String code, TConfig config, String id, Object request,
            Object response, String url, final String e, long delayTime) {

    }

    /**
     * 过滤重复的错误，避免请求过多
     *
     * @param config TConfig
     * @param id id
     * @param request request
     * @param response response
     * @param url url
     * @param e 错误
     */
    public static void filterRepeatError(TConfig config, String id, Object request,
            Object response, String url, final String e, final long delayTime) {

    }

    public static void filterRepeatError(TConfig config, String id, Object request,
            Object response, String url, final String e) {
        filterRepeatError(config, id, request, response, url, e, 0);
    }


    /**
     * 上报错误
     *
     * @param config TConfig
     * @param id id
     * @param request request
     * @param response response
     * @param url url
     * @param e 错误
     */
    public static void reportError(TConfig config, String id, Object request,
            Object response, String url, String e, long delayTime) {
        ReportInfo reportInfo = new ReportInfo();
        ReportInfo.Log log = ReportInfo.Log.builder().request(request).url(url).delayTime(delayTime)
                .response(response).time(Tutils.getNowData()).build();
        reportInfo.setLog(JsonUtil.toJson(log));
        ReportInfo.AppInfo appInfo = ReportInfo.getAppInfo(e, "_ERROR");
        reportInfo.setAppInfo(JsonUtil.toJson(appInfo));
        doReportError(config.getSecretId(), config.getSecretKey(), config.getToken(), id, reportInfo);
    }


    /**
     * 错误上报
     *
     * @param secretId secretId
     * @param secretKey secretKey
     * @param id 标志
     * @param data 上报数据
     */
    public static void doReportError(String secretId, String secretKey, String token, String id, Object data) {

    }


    /**
     * 打印日志
     *
     * @param id 日志标示
     * @param message 日志信息
     * @param error 是否异常日志
     */
    public static void ifLogMessage(String id, String message, Boolean error) {
        if (GlobalConfig.ifLog) {
            id = Optional.ofNullable(id).orElse("");
            if (error) {
                GlobalConfig.sdkLogInterceptor.error(id + "||" + message);
            } else {
                GlobalConfig.sdkLogInterceptor.info(id + "||" + message);
            }
        }
    }


}
