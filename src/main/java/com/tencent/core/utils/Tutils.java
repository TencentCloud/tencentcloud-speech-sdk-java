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

    /**
     * 获取异常的堆栈信息
     *
     * @param t
     * @return st
     */
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            t.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }
}
