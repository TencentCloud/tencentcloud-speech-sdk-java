package com.tencent.tts.service;

import cn.hutool.core.util.RandomUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencent.core.help.SignHelper;
import com.tencent.core.service.ReportService;
import com.tencent.core.utils.ByteUtils;
import com.tencent.core.utils.JsonUtil;
import com.tencent.core.utils.SignBuilder;
import com.tencent.tts.model.SpeechSynthesisConfig;
import com.tencent.tts.model.SpeechSynthesisRequest;
import com.tencent.tts.model.SpeechSynthesisRequestContent;
import com.tencent.tts.model.SpeechSynthesisResponse;
import com.tencent.tts.model.SpeechSynthesisSysConfig;
import com.tencent.tts.utils.LineSplitUtils;
import com.tencent.tts.utils.Ttsutils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class SpeechSynthesizer {

    private CloseableHttpClient httpclient;

    /**
     * 请求配置
     */
    private SpeechSynthesisConfig speechSynthesisConfig;

    /**
     * 请求参数
     */
    private SpeechSynthesisRequest speechSynthesisRequest;

    /**
     * 用户实时结果监听器
     */
    private SpeechSynthesisListener eventListener;

    /**
     * 签名service
     */
    private SpeechSynthesisSignService speechSynthesisSignService = new SpeechSynthesisSignService();

    /**
     * sessionId
     */
    private String sessionParentId;


    private AtomicInteger index;

    /**
     * 计算执行时间
     */
    private long time;

    /**
     * 初始化
     *
     * @param speechSynthesisConfig 配置
     * @param speechSynthesisRequest 请求参数
     * @param eventListener 结果回调函数
     */
    public SpeechSynthesizer(SpeechSynthesisConfig speechSynthesisConfig,
            SpeechSynthesisRequest speechSynthesisRequest, SpeechSynthesisListener eventListener) {
        this.speechSynthesisConfig = speechSynthesisConfig;
        this.speechSynthesisRequest = speechSynthesisRequest;
        this.eventListener = eventListener;
        if (StringUtils.isEmpty(speechSynthesisRequest.getSessionId())) {
            sessionParentId = speechSynthesisConfig.getAppId() + "_tts_"
                    + System.currentTimeMillis() + RandomUtil.randomString(4);
        } else {
            sessionParentId = speechSynthesisRequest.getSessionId();
        }
        index = new AtomicInteger(0);
        initClient();
    }

    /**
     * initClient 初始化client
     */
    public void initClient() {
        if (SpeechSynthesisSysConfig.httpclient == null) {
            synchronized (SpeechSynthesizer.class) {
                if (SpeechSynthesisSysConfig.httpclient == null) {
                    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                    cm.setMaxTotal(500);
                    cm.setDefaultMaxPerRoute(250);
                    RequestConfig.Builder rb = RequestConfig.custom();
                    if (SpeechSynthesisSysConfig.UseProxy) {
                        rb.setProxy(SpeechSynthesisSysConfig.HostProxy);
                    }
                    RequestConfig requestConfig = rb.setConnectTimeout(1000)
                            .setSocketTimeout(30000)
                            .setConnectionRequestTimeout(1500)
                            .build();
                    SpeechSynthesisSysConfig.httpclient = HttpClients.custom().setConnectionManager(cm)
                            .setConnectionManagerShared(true)
                            .setDefaultRequestConfig(requestConfig).build();
                }
            }
        }
        this.httpclient = SpeechSynthesisSysConfig.httpclient;
    }


    /**
     * 执行tts请求
     *
     * @param texts 文本
     * @return SpeechSynthesizer
     */

    public SpeechSynthesizer synthesis(String texts) {
        //StatService.heartbeat();
        if (StringUtils.isEmpty(texts)) {
            ReportService.ifLogMessage(sessionParentId, "text is empty", false);
            return this;
        }
        SpeechSynthesisResponse response = null;
        byte[] audioData = new byte[0];
        response = speechRequest(texts, index.intValue(), sessionParentId);
        //拼接数据
        if (response != null && response.getSuccess()
                && response.getAudio() != null && response.getAudio().length > 0) {
            audioData = ByteUtils.concat(audioData, response.getAudio());
        }
        response.setSeq(-1);
        response.setEnd(true);
        response.setAudio(audioData);
        //如果请求成功回调成功，否则调用失败
        if (response.getSuccess()) {
            eventListener.onComplete(response);
        } else {
            eventListener.onFail(response);
        }
        index.incrementAndGet();
        return this;
    }

    /**
     * 长文本合成
     *
     * @param texts 文本
     * @return SpeechSynthesizer
     */
    public SpeechSynthesizer synthesisLongText(String texts) {
        //StatService.heartbeat();
        if (StringUtils.isEmpty(texts)) {
            ReportService.ifLogMessage(sessionParentId, "text is empty", false);
            return this;
        }
        SpeechSynthesisResponse response = null;
        byte[] audioData = new byte[0];
        List<String> longTexts = LineSplitUtils.smartSplit(texts);
        for (String text : longTexts) {
            response = speechRequest(text, index.intValue(), sessionParentId);
            //拼接数据
            if (response != null && response.getSuccess() && response.getAudio() != null
                    && response.getAudio().length > 0) {
                audioData = ByteUtils.concat(audioData, response.getAudio());
            }
        }
        response.setEnd(true);
        response.setAudio(audioData);
        //如果请求成功回调成功，否则调用失败
        if (response.getSuccess()) {
            eventListener.onComplete(response);
        } else {
            eventListener.onFail(response);
        }
        index.incrementAndGet();
        return this;
    }

    /**
     * 请求重试
     *
     * @param text
     * @param seq
     * @param sessionParentId
     * @return
     */
    private SpeechSynthesisResponse speechRequest(String text, Integer seq, String sessionParentId) {
        SpeechSynthesisResponse response = request(text, seq);
        if (!response.getSuccess() && "-1".equals(response.getCode())) {
            int retryNum = 2;
            //失败重试2次
            for (int i = 0; i < retryNum; i++) {
                response = request(text, seq);
                if (response.getSuccess()) {
                    ReportService.ifLogMessage(response.getSessionId(), "retry success:" + i, false);
                    break;
                }
                ReportService.ifLogMessage(response.getSessionId(), "retry fail:" + i, false);
            }
        }
        if (!response.getSuccess()) {
            ReportService.filterRepeatError(speechSynthesisConfig,
                    sessionParentId, speechSynthesisRequest, response,
                    speechSynthesisConfig.getTtsUrl(), response.getMessage());
        }
        return response;
    }

    /**
     * 分发请求
     *
     * @param text 文本
     * @param seq 序列
     */
    private SpeechSynthesisResponse request(String text, Integer seq) {
        String sessionId = sessionParentId;
        if (StringUtils.isNotEmpty(speechSynthesisRequest.getSessionId())) {
            sessionId = speechSynthesisRequest.getSessionId();
        }
        SpeechSynthesisResponse synthesizerResponse = new SpeechSynthesisResponse();
        synthesizerResponse.setSeq(seq);
        synthesizerResponse.setSessionId(sessionId);
        synthesizerResponse.setEnd(false);
        SpeechSynthesisRequestContent content = SpeechSynthesisRequestContent.builder()
                .text(text).sessionId(sessionId).build();
        //签名
        speechSynthesisRequest.setTimestamp(System.currentTimeMillis() / 1000);
        speechSynthesisRequest.setExpired(speechSynthesisRequest.getTimestamp() + 86400);
        TreeMap<String, Object> map = SpeechSynthesisSignService.getParams(speechSynthesisConfig,
                speechSynthesisRequest, content);

        String paramUrl = SignHelper.createUrl(map);
        String signUrl = speechSynthesisConfig.getSignUrl() + paramUrl;
        String sign = SignBuilder.base64_hmac_sha1(signUrl, speechSynthesisConfig.getSecretKey());

        try {
            HttpPost httpPost = new HttpPost(speechSynthesisConfig.getTtsUrl());
            httpPost.addHeader("Authorization", sign);
            httpPost.addHeader("Content-Type", "application/json");
            HttpEntity httpEntity = new ByteArrayEntity(JsonUtil.toJson(map).getBytes(Charset.forName("utf-8")));
            httpPost.setEntity(httpEntity);
            ReportService.ifLogMessage("tt request:", signUrl, false);
            time = System.currentTimeMillis();
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                synthesizerResponse.setCode(String.valueOf(response.getStatusLine().getStatusCode()));
                //请求返回非200失败
                if (!"200".equals(synthesizerResponse.getCode())) {
                    ReportService.ifLogMessage(sessionId, response.getStatusLine().toString(), true);
                    synthesizerResponse.setSuccess(false);
                    return synthesizerResponse;
                }
                //如果contentType是text/plain，则为失败请求
                if (response.getEntity().getContentType().getValue().contains("text/plain")
                        && !response.getEntity().isChunked()) {
                    synthesizerResponse.setSuccess(false);
                    try {
                        //转换数据
                        InputStream inputStream = response.getEntity().getContent();
                        byte[] data = new byte[0];
                        List<byte[]> resp = ByteUtils.subToSmallBytes(inputStream, 500);
                        for (byte[] item : resp) {
                            data = ByteUtils.concat(data, item);
                        }
                        String result = new String(data, "utf-8");
                        ReportService.ifLogMessage(sessionId, result, true);

                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        synthesizerResponse.setMessage(jsonObject.getAsJsonObject("Response")
                                .getAsJsonObject("Error").getAsJsonPrimitive("Message").getAsString());
                        synthesizerResponse.setCode(jsonObject.getAsJsonObject("Response")
                                .getAsJsonObject("Error").getAsJsonPrimitive("Code").getAsString());
                        synthesizerResponse.setRequestId(jsonObject.getAsJsonObject("Response")
                                .getAsJsonPrimitive("RequestId").getAsString());
                    } catch (Exception e) {
                        ReportService.ifLogMessage("request error:", e.getMessage(), true);
                    }
                    return synthesizerResponse;
                }
                //成功的则读取chunked信息
                InputStream inputStream = response.getEntity().getContent();
                synthesizerResponse.setAudio(read(inputStream, response));
                synthesizerResponse.setMessage("success");
                synthesizerResponse.setSuccess(true);
                return synthesizerResponse;
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            synthesizerResponse.setSuccess(false);
            synthesizerResponse.setCode("-1");
            synthesizerResponse.setMessage("ClientProtocolException:" + e.getMessage());
            //e.printStackTrace();
        } catch (IOException exception) {
            synthesizerResponse.setSuccess(false);
            synthesizerResponse.setCode("-1");
            synthesizerResponse.setMessage("IOException:" + exception.getMessage());
            //exception.printStackTrace();
        } finally {

        }
        return synthesizerResponse;
    }

    public byte[] read(InputStream inputStream, CloseableHttpResponse response) {
        return readStream(inputStream, response);
    }

    /**
     * 读取chunked数据 pcm
     *
     * @param inputStream inputStream
     * @return byte[]
     */
    public byte[] readStream(InputStream inputStream, CloseableHttpResponse response) {
        byte[] responseDatas = new byte[0];
        AtomicInteger adder = new AtomicInteger(0);
        while (true) {
            byte[] pcmData = new byte[10240]; // 经实测inputStream.available()并不能加速接收，因此使用固定值。
            try {
                int readSize = Ttsutils.fill(inputStream, pcmData);
                if (readSize == 0) { // 加此判断相当于多尝试了一次读数据，确保数据都收完了。
                    break;
                }
                if (readSize < pcmData.length) {
                    pcmData = ByteUtils.subBytes(pcmData, 0, readSize);
                }
                responseDatas = ByteUtils.concat(responseDatas, pcmData);
                ReportService.ifLogMessage("execution time", "[" + (System.currentTimeMillis() - time) + "ms]", false);
                if (eventListener != null) {
                    eventListener.onMessage(pcmData);
                }
                if (eventListener.ifCancel()) {
                    response.close();
                    //重置
                    eventListener.setIfCancel(true);
                    break;
                }
            } catch (IOException e) {
                //e.printStackTrace();
                //IOException 回调onFail方法，一般情况不会回调
                SpeechSynthesisResponse synthesizerResponse = new SpeechSynthesisResponse();
                synthesizerResponse.setSeq(index.get());
                synthesizerResponse.setSessionId(sessionParentId + "_" + index.get() + "_" + adder.get());
                synthesizerResponse.setEnd(false);
                synthesizerResponse.setSuccess(false);
                synthesizerResponse.setCode("-1");
                synthesizerResponse.setMessage(e.getMessage());
                if (eventListener != null) {
                    eventListener.onFail(synthesizerResponse);
                }
            }
            adder.incrementAndGet();
        }
        return responseDatas;
    }

    protected static byte[] readChunkData(InputStream in) throws IOException {
        byte[] sum = new byte[0];
        while (true) {
            byte[] temp = new byte[4096];
            int currentRead = in.read(temp, 0, temp.length);
            if (currentRead > 0) {
                if (currentRead < temp.length) {
                    temp = ByteUtils.subBytes(temp, 0, currentRead);
                }
                sum = ByteUtils.concat(sum, temp);
            }

            if (in.available() == 0) {
                break;
            }
            if (currentRead == -1) {
                break;
            }
        }
        return sum;
    }
}
