package com.tencent.asr.service;

import com.tencent.asr.model.AsrConfig;
import com.tencent.asr.model.AsrRequest;
import com.tencent.asr.model.AsrResponse;
import com.tencent.asr.utils.AsrUtils;
import com.tencent.core.handler.BaseEventListener;
import com.tencent.core.handler.RealTimeEventListener;
import com.tencent.core.service.ReportService;
import com.tencent.core.service.TCall;
import com.tencent.core.utils.JsonUtil;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

public class HttpStreamService extends HttpBaseService implements TCall<InputStream>, SpeechRecognizer {

    public HttpStreamService(String streamId, AsrConfig config, AsrRequest request,
                             RealTimeEventListener realTimeEventListener,
                             ExecutorService executor, BaseEventListener<AsrResponse> baseEventListener) {
        super(streamId, config, request, realTimeEventListener, baseEventListener);
    }


    /**
     * 请求前置方法，开启线程监听请求结果
     */
    public Boolean start() {
        tractionManager.beginTraction(streamId);
        startListerResult();
        return true;
    }


    /**
     * 请求后置方法
     */
    public void after() {
        tractionManager.endTraction(streamId);
    }

    @Override
    public Boolean end() {
        return stop();
    }

    @Override
    public TCall execute(InputStream stream) throws IOException {
        return this;
    }

    /**
     * 结束识别
     *
     * @return 识别标志位 通过该标志可关闭流
     */
    @Override
    public Boolean stop() {
        ReportService.ifLogMessage(staging.getStreamId(), "speech end", false);
        if (!endFlag.get()) {
            endFlag.set(true);
        }
        after();
        return finishFlag.get();
    }

    @Override
    public void write(byte[] stream) {

    }

    /**
     * 识别逻辑的处理方法
     *
     * @param stream 语音流
     */
    public void write(InputStream stream) {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                expireTime = System.currentTimeMillis() + asrConfig.getWaitTime() / 2;
                boolean end = false;
                while (!finishFlag.get()) {
                    int len = stream.available();
                    if (!endFlag.get() && len <= 0) {
                        Thread.sleep(5);
                        continue;
                    }
                    //解决尾包问题，根据endFlag进行判断，设置end标志位避免因为没有执行尾包跳出循环
                    byte[] data = new byte[asrRequest.getCutLength()];
                    if (endFlag.get() && (len <= 0 || len < asrRequest.getCutLength())) {
                        finishFlag.set(true);
                        end = true;
                    }
                    int size = 0;
                    if (len > 0) {
                        size = stream.read(data);
                    }
                    data = createBytes(data, size);
                    ReportService.ifLogMessage(staging.getVoiceId(), "read data length:" + data.length, false);
                    //解决seq=0重传问题
                    if ((cacheStatus) || System.currentTimeMillis() > expireTime) {
                        synchronized (this) {
                            cacheStatus = false;
                            staging.setVoiceId(AsrUtils.getVoiceId(asrConfig.getAppId()));
                            staging.setEnd(0);
                            staging.setSeq(0);
                            ReportService.ifLogMessage(staging.getVoiceId(),
                                    "Retransmission settings:"
                                            + JsonUtil.toJson(staging) + ",进行seq=0重传", true);
                        }
                    }
                    //设置过期时间，如果超过2m没有发送流，则重新设置voiceId
                    expireTime = System.currentTimeMillis() + asrConfig.getWaitTime();
                    ReportService.ifLogMessage(staging.getVoiceId(), "dispatcher", false);

                    //语音处理
                    String stamp = dispatcherRequest(data, end);
                    requestStamps.add(stamp);
                    if (finishFlag.get()) {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (Exception e) {
                                //ignore
                            }
                        }
                    }
                }

            }
        }).start();
    }

}
