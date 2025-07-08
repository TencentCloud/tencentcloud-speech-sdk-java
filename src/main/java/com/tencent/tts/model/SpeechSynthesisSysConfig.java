package com.tencent.tts.model;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;

public class SpeechSynthesisSysConfig {

    /**
     * 每次发出请求时，最多携带的字符数。如果一行文字的长度超过此值，则会被截成多条请求发出。
     */
    public static int SEPARATOR_LENGTH_LIMIT = 100;

    /**
     * 对每行文字做分割时的关键字，遇到这里的字符肯定会切分开。
     */
    public static String[] SEPARATOR_CHARS = new String[]{"。", "！", "？", "!", "?", "."};

    /**
     * 代理
     */
    public static HttpHost HostProxy = null;
    /**
     * 是否开启代理
     */
    public static boolean UseProxy = false;

    public static CloseableHttpClient httpclient = null;
}
