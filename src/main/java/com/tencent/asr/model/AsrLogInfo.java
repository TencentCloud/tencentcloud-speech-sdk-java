package com.tencent.asr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.core.model.LogStatistics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AsrLogInfo {
    @JsonProperty(value = "Log")
    private String log;
    @JsonProperty(value = "AppInfo")
    private String appInfo;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Log {
        @JsonProperty(value = "Time")
        private String time;
        @JsonProperty(value = "Response")
        private AsrResponse response;
        @JsonProperty(value = "Request")
        private AsrRequest request;
        @JsonProperty(value = "Sign")
        private String sign;
        @JsonProperty(value = "Url")
        private String url;
        @JsonProperty(value = "Stat")
        private LogStatistics stat;
    }


    @Setter
    @Getter
    public static class AppInfo {
        @JsonProperty(value = "Time")
        private String time;
        @JsonProperty(value = "AppVerName")
        private String appVerName;
        @JsonProperty(value = "AppVerCode")
        private String appVerCode;
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
    }

}
