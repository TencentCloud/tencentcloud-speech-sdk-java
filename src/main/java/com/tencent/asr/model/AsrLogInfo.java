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
    public static class Log{
        @JsonProperty(value = "Time")
        private String time;
        @JsonProperty(value = "Response")
        private AsrResponse response;
        @JsonProperty(value = "Request")
        private AsrRequest  request;
        @JsonProperty(value = "Sign")
        private String sign;
        @JsonProperty(value = "Url")
        private String url;
        @JsonProperty(value = "Stat")
        private LogStatistics stat;
    }


    @Setter
    @Getter
    public static class AppInfo{
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
