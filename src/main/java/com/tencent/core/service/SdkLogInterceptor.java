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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SdkLogInterceptor 拦截器
 */
public class SdkLogInterceptor {

    /**
     * info
     * @param info info
     */
    public void info(String info) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("[" + date + "]" + info);
    }

    /**
     * error
     * @param error err
     */
    public void error(String error) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("[" + date + "]" + error);
    }
}
