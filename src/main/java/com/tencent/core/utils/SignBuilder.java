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

import com.tencent.asr.model.AsrRequest;
import com.tencent.core.model.TRequest;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

/**
 * 特定的签名构建工具类。按照腾讯云只能语音服务的接口鉴权要求，对传入的URL创建签名，用于URL请求鉴权。
 *
 * 签名方法详见： <a href="https://cloud.tencent.com/document/product/441/17366">接口鉴权</a>
 */
public class SignBuilder {

    private static final String REQUEST_ENCODE = "UTF-8";
    private static final String SIGN_TYPE = "HmacSHA1"; // 采用的签名算法

    private static final Set<String> VALIDE_REQUEST_TYPES = new HashSet<String>();

    static {
        VALIDE_REQUEST_TYPES.add("GET");
        VALIDE_REQUEST_TYPES.add("POST");
    }

    /**
     * 对Post请求创建签名。先处理serverUrl参数：去掉“http://”,加上“POST”，然后再制作签名。
     *
     * @param serverUrl 请求URL
     * @param secretKey 请求秘钥
     * @param request   请求参数
     * @return 签名
     */
    public static String createPostSign(String serverUrl, String secretKey, TRequest request) {
        return createSign(serverUrl, secretKey, "POST", request);
    }

    /**
     * 对Get请求创建签名。先处理serverUrl参数：去掉“http://”,加上“GET”，然后再制作签名。
     *
     * @param serverUrl 请求URL
     * @param secretKey 请求秘钥
     * @param request   请求参数
     * @return 签名
     */
    public static String createGetSign(String serverUrl, String secretKey, TRequest request) {
        return createSign(serverUrl, secretKey, "GET", request);
    }

    /**
     * 对Get请求创建签名。先处理serverUrl参数：去掉“http://”,加上requestType值，然后再制作签名。
     *
     * @param serverUrl   请求URL
     * @param secretKey   请求秘钥
     * @param requestType 请求类型，必须是：GET或 POST
     * @param request     请求参数
     * @return 签名
     */
    public static String createSign(String serverUrl, String secretKey, String requestType, TRequest request) {
        validateRequestType(requestType);
        String url = serverUrl.toLowerCase();
        int position = 0;
        String strToBeEncoded = serverUrl;
        if (url.contains("https://")) {
            position = 8;
            strToBeEncoded = requestType + serverUrl.substring(position);
        }
        if (url.contains("http://")) {
            position = 7;
            strToBeEncoded = requestType + serverUrl.substring(position);
        }
        if (url.contains("ws://")) {
            position = 5;
            strToBeEncoded = serverUrl.substring(position);
        }
        if (url.contains("wss://")) {
            position = 6;
            strToBeEncoded = serverUrl.substring(position);
        }

        if (request != null) {
            request.setStrToBeEncoded(strToBeEncoded);
        }
        return base64_hmac_sha1(strToBeEncoded, secretKey);
    }

    /**
     * 出现鉴权失败时，可尝试调用本方法，创建签名并做一次URL encode操作。
     *
     * 详细说明请查看 <a href="https://cloud.tencent.com/document/product/441/17366">接口鉴权</a> 中的第 3点：签名串编码
     *
     * @param serverUrl   请求URL
     * @param secretKey   请求秘钥
     * @param requestType 请求类型，必须是：GET或 POST
     * @param request     请求参数
     * @return sign
     */
    public static String createSignAndUrlEncode(String serverUrl, String secretKey,
                                                String requestType, AsrRequest request) {
        validateRequestType(requestType);
        String sign = createSign(serverUrl, secretKey, requestType, request);
        try {
            return URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return sign;
        }
    }

    /**
     * 使用HMAC-SHA1算法对拼接字符串签名，并将生成的签名串 使用Base64进行编码，返回编码结果。
     *
     * @param originalText 需要被签名的字符串
     * @param secretKey    秘钥字符串
     * @return 签名和编码之后的字符串
     */
    private static String base64_hmac_sha1(String originalText, String secretKey) {
        try {
            Mac hmac = Mac.getInstance(SIGN_TYPE);
            hmac.init(new SecretKeySpec((secretKey).getBytes(REQUEST_ENCODE), SIGN_TYPE)); // 初始化
            byte[] hash = hmac.doFinal((originalText).getBytes(REQUEST_ENCODE)); // 签名
            return Base64.encodeBase64String(hash); // 编码
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // 签名编码失败。
        }
    }

    private static void validateRequestType(String requestType) {
        if (requestType == null || !VALIDE_REQUEST_TYPES.contains(requestType)) {
            throw new IllegalArgumentException("Unsupported request type: "
                    + requestType + ", must be: GET or POST");
        }
    }
}
