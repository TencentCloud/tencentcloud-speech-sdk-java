package com.tencent.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.core.utils.Tutils;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportInfo {

    @JsonProperty(value = "Log")
    private String Log;
    @JsonProperty(value = "AppInfo")
    private String AppInfo;

    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Log {
        @JsonProperty(value = "DelayTime")
        private long delayTime;
        @JsonProperty(value = "Time")
        private String time;
        @JsonProperty(value = "Response")
        private Object response;
        @JsonProperty(value = "Request")
        private Object request;
        @JsonProperty(value = "Url")
        private String url;
        @JsonProperty(value = "Stat")
        private LogStatistics stat;
        @JsonProperty(value = "Sign")
        private String sign;
    }


    @Setter
    @Getter
    public static class AppInfo {
        @JsonProperty(value = "Time")
        private String time;
        @JsonProperty(value = "OsVer")
        private String osVer;
        @JsonProperty(value = "OsName")
        private String osName;
        @JsonProperty(value = "Sdk")
        private String sdk;
        @JsonProperty(value = "SdkVer")
        private String sdkVer;
        @JsonProperty(value = "Exception")
        private String exception;
        @JsonProperty(value = "AppVerName")
        private String appVerName;
        @JsonProperty(value = "AppVerCode")
        private String appVerCode;
    }

    public static AppInfo getAppInfo(String e, String type) {
        AppInfo appInfo = new AppInfo();
        appInfo.setOsVer(System.getProperty("os.version"));
        appInfo.setOsName(System.getProperty("os.name"));
        appInfo.setAppVerName(GlobalConfig.appVerName);
        appInfo.setAppVerCode(GlobalConfig.appVerCode);
        appInfo.setSdk(GlobalConfig.getSdk() + type);
        appInfo.setSdkVer(GlobalConfig.getSdkVer());
        appInfo.setTime(Tutils.getNowData());
        appInfo.setException(e);
        return appInfo;
    }
}
