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

package com.tencent.core.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

public class Tutils {

    /**
     * 关闭线程池
     *
     * @param executor 线程池
     */
    public static void closeThreadPool(ThreadPoolExecutor executor) {
        if (executor != null) {
            try {
                executor.shutdown();
            } catch (Exception ie) {
                executor.shutdownNow();
            }
        }
    }

    public static String getNowData() {
        return DateUtil.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
    }


    public static String getStackTraceAsString(Exception e) {
        // StringWriter将包含堆栈信息
        StringWriter stringWriter = new StringWriter();
        //必须将StringWriter封装成PrintWriter对象，
        //以满足printStackTrace的要求
        PrintWriter printWriter = new PrintWriter(stringWriter);
        //获取堆栈信息
        e.printStackTrace(printWriter);
        //转换成String，并返回该String
        StringBuffer error = stringWriter.getBuffer();
        return error.toString();
    }
}
