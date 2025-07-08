package com.tencent.core.help;

import com.tencent.core.utils.SignBuilder;

import java.net.URLEncoder;
import java.util.Map;

public class SignHelper {

    public static String createUrl(Map<String, Object> paramMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue() != "") {
                sb.append(entry.getKey());
                sb.append('=');
                sb.append(entry.getValue());
                sb.append('&');
            }
        }
        if (paramMap.size() > 0) {
            sb.setLength(sb.length() - 1); // 去掉最后面的 '&'号
        }
        return sb.toString();
    }


    public static String createSign(String signPrefixUrl,String param,String appid,String secretKey){
        return SignBuilder.base64_hmac_sha1(createRequestUrl(signPrefixUrl,param,appid),secretKey);
    }

    public static String createRequestUrl(String prefixUrl,String param,String appid){
        return new StringBuilder().append(prefixUrl).append(appid).append(param).toString();
    }
    public static Map<String, Object> encode(Map<String, Object> src) {
        if (src != null) {
            for (String key : src.keySet()) {
                if(src.get(key) instanceof String){
                    src.put(key, URLEncoder.encode(String.valueOf(src.get(key))));
                }
            }
        }
        return src;
    }
}
