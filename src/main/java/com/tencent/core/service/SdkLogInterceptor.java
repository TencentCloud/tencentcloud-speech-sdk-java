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
