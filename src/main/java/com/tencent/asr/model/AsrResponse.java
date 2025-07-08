
package com.tencent.asr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.asr.constant.AsrConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AsrResponse {

    public AsrResponse(int code, String message, String voiceId, int seq) {
        this.code = code;
        this.message = message;
        this.voiceId = voiceId;
        this.seq = seq;
    }

    public AsrResponse(AsrConstant.Code code, String voiceId, int seq) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.voiceId = voiceId;
        this.seq = seq;
    }

    private String streamId;

    /**
     * 请求标示 streamId_voiceId_seq
     */
    private String stamp;


    /**
     * 0：正常，其他，发生错误
     */
    private int code;

    /**
     * 0：success；不为0：其他
     */
    private String message;

    @JsonProperty(value = "voice_id")
    private String voiceId;

    /**
     * 语音分片的信号
     * 如果请求参数 needvad 为0的话，表示不需要后台做 vad，这里的 seq 就是发送过来的 seq 的序号。
     * 如果请求参数 needvad 为1的话，表示需要后台做 vad，vad 会重新分片，送入识别的 seq 会和发送过来的 seq 不一样。
     */
    private int seq;

    /**
     * 语音分片的识别结果
     * 如果请求参数 needvad 为0的话，表示不需要后台做 vad，这里的 text 的指是这个分片的识别结果。
     * 如果请求参数 needvad 为1的话，表示需要后台做 vad，vad 会重新分片，text 为 vad 分片后的结果。
     */
    private String text;

    @JsonProperty(value = "original_text")
    private String originalText;

    /**
     * 表示后面的
     * result_list 里面有几段结果，如果是0表示没有结果，遇到中间是静音。
     * 如果是1表示 result_list
     * 有一个结果，在发给服务器分片很大的情况下可能会出现多个结果，正常情况下都是1个结果。
     */
    private Integer resultNumber;

    /**
     * 0表示还在整个音频流的中间部分
     * 1表示是整个音频流的最后一个包。
     * 主要是在电信场景中，客户端发送完了之后，要知道是否返回的是最后一个包。
     */
    @JsonProperty(value = "final")
    private Integer finalSpeech;

    @JsonProperty(value = "result_list")
    private List<SpeechRecognitionResponseResult> resultList;

}
