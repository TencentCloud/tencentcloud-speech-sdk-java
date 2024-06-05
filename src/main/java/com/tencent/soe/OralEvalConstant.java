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
package com.tencent.soe;

public class OralEvalConstant {

    //实时识别请求地址
    public static String DEFAULT_ORAL_EVAL_REQ_URL = "wss://soe.cloud.tencent.com/soe/api/";
    //实时识别签名字符串前缀
    public static String DEFAULT_ORAL_EVAL_SIGN_PREFIX = "soe.cloud.tencent.com/soe/api/";
    //请求Host
    public static String DEFAULT_ORAL_EVAL_HOST = "soe.cloud.tencent.com";
    // start、stop方法countdown超时时间 单位ms
    public static int DEFAULT_START_TIMEOUT_MILLISECONDS = 15000;
}
