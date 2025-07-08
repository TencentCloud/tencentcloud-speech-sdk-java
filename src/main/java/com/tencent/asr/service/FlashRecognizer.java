package com.tencent.asr.service;

import cn.hutool.core.util.RandomUtil;
import com.tencent.asr.model.AsrConfig;
import com.tencent.asr.model.FlashRecognitionRequest;
import com.tencent.asr.model.FlashRecognitionResponse;
import com.tencent.asr.model.SpeechRecognitionSysConfig;
import com.tencent.core.help.SignHelper;
import com.tencent.core.service.ReportService;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.SignBuilder;
import com.tencent.core.utils.Tutils;

import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

//极速版
public class FlashRecognizer {

    /**
     * 签名service
     */
    protected SpeechRecognitionSignService speechRecognitionSignService = new SpeechRecognitionSignService();

    private AsrConfig config;

    private static CloseableHttpClient client;


    static {
        final SSLConnectionSocketFactory sslsf;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(),
                    NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(SpeechRecognitionSysConfig.MaxTotal);
        cm.setDefaultMaxPerRoute(SpeechRecognitionSysConfig.defaultMaxPerRoute);
        RequestConfig.Builder rb = RequestConfig.custom();
        if (SpeechRecognitionSysConfig.httpUseProxy) {
            rb.setProxy(SpeechRecognitionSysConfig.httpHostProxy);
        }
        RequestConfig requestConfig = rb.setConnectTimeout(SpeechRecognitionSysConfig.flashConnectTimeout)
                .setSocketTimeout(SpeechRecognitionSysConfig.flashSocketTimeout)
                .setConnectionRequestTimeout(SpeechRecognitionSysConfig.flashConnectionRequestTimeout)
                .build();
        client = HttpClients.custom().setConnectionManager(cm)
                .setSSLSocketFactory(sslsf)
                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setDefaultRequestConfig(requestConfig).build();

    }

    public FlashRecognizer(AsrConfig config) {
        this.config = config;
    }

    /**
     * 写入数据
     *
     * @param data 音频数据
     * @return FlashRecognitionResponse
     */
    public FlashRecognitionResponse recognize(FlashRecognitionRequest request, byte[] data) {
        if (data == null || data.length == 0) {
            throw new RuntimeException("write data is null!!!");
        }
        request.setTimestamp(System.currentTimeMillis() / 1000);
        Map<String, Object> paramMap = speechRecognitionSignService.getFlashParams(config, request);
        String paramUrl = SignHelper.createUrl(paramMap);
        String signUrl = "POST" + config.getFlashSignUrl() + config.getAppId() + paramUrl;
        String sign = SignBuilder.base64_hmac_sha1(signUrl, config.getSecretKey());
        //url编码
        if (request.getHotwordList() != null) {
            paramMap.put("hotword_list", URLEncoder.encode(request.getHotwordList()));
        }
        String url = config.getFlashUrl() + config.getAppId() + SignHelper.createUrl(paramMap);
        CloseableHttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Authorization", sign);
            httpPost.addHeader("Content-Type", "application/octet-stream");
            if (StringUtils.isNotEmpty(config.getToken())) {
                httpPost.addHeader("X-TC-Token", config.getToken());
            }
            httpPost.setEntity(new ByteArrayEntity(data));
            httpResponse = client.execute(httpPost, HttpClientContext.create());
            String responseStr = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            FlashRecognitionResponse response = JsonUtil.fromJson(responseStr, FlashRecognitionResponse.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            FlashRecognitionResponse response = new FlashRecognitionResponse();
            response.setMessage(Tutils.getStackTraceAsString(e));
            response.setRequestId(RandomUtil.randomString(11));
            response.setCode(-1);
            ReportService.report(false, String.valueOf(-1), config, response.getRequestId(),
                    request, response, config.getFlashUrl(), e.getMessage());
            return response;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
