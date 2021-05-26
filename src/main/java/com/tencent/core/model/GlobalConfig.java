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

import lombok.Getter;

@Getter
public class GlobalConfig {
    public static String appVerName = "SDK";

    public static String appVerCode = "1.0";

    /**
     * 不建议修改此值
     */
    private static String sdk = "JAVA_TENCENT_CLOUD_SPEECH_SDK";

    public static String getSdk() {
        return sdk;
    }

    /**
     * 不建议修改此值
     */
    private static String sdkVer = "1.0.12";

    public static String getSdkVer() {
        return sdkVer;
    }


    /**
     * 是否打印日志
     */
    public static boolean ifLog = false;

    public static String region = "ap-shanghai";

    public static Boolean ifOpenStat = false;

    public static Boolean ifSpeechClient = true;
}
