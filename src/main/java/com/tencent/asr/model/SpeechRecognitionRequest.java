package com.tencent.asr.model;

import cn.hutool.core.util.RandomUtil;
import com.tencent.asr.constant.AsrConstant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SpeechRecognitionRequest extends AsrRequest {

    private String voiceId;

    /**
     * 初始化
     *
     * @return SpeechRecognizerRequest
     */
    public static SpeechRecognitionRequest initialize() {
        SpeechRecognitionRequest request = new SpeechRecognitionRequest();
        request.needVad = 1;
        //request.voiceFormat = AsrConstant.VoiceFormat.sp.getFormatId();
        request.timestamp = System.currentTimeMillis() / 1000;
        request.expired = System.currentTimeMillis() / 1000 + 86400;
        request.nonce = RandomUtil.randomInt(1000, 99999);

        if (AsrConstant.RequestWay.Http.equals(SpeechRecognitionSysConfig.requestWay)) {
            request.resultTextFormat = AsrConstant.ResponseEncode.UTF_8.getId();
            request.resType = AsrConstant.ReturnType.REALTIME_FOLLOW.getTypeId();
            request.subServiceType = 1;
            request.projectId = 1013976;
            request.cutLength = 3200;
            request.timeout = 200;
            request.source = 0;
        }
        return request;
    }
}
