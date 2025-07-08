package com.tencent.vc;

import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

public class VoiceConversionUtils {

    /**
     * byte[]转大端序int
     *
     * @param byteArray
     * @return
     */
    public static int byteArrayToInt(byte[] byteArray) {
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        buffer.order(ByteOrder.BIG_ENDIAN); // 设置为大端序
        return buffer.getInt();
    }


    /**
     * 包装请求数据
     *
     * @param end   0，1
     * @param audio 音频数据
     * @return
     */
    public static byte[] wrapSendRequest(int end, byte[] audio) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("End", end);
        byte[] json = new Gson().toJson(param).getBytes();
        byte[] head = intToByteArray(json.length);
        return concatenateByteArrays(concatenateByteArrays(head, json), audio);
    }

    /**
     * 大端序int转byte[]
     *
     * @param number 数字
     * @return
     */
    public static byte[] intToByteArray(int number) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN); // 设置为大端序
        buffer.putInt(number);
        return buffer.array();
    }

    /**
     * 拼接byte[]
     *
     * @param array1 array1
     * @param array2 array2
     * @return
     */
    public static byte[] concatenateByteArrays(byte[] array1, byte[] array2) {
        ByteBuffer buffer = ByteBuffer.allocate(array1.length + array2.length);
        buffer.put(array1);
        buffer.put(array2);
        buffer.flip(); // 切换为读模式
        return buffer.array();
    }

    /**
     * 解析数据
     *
     * @param resp response
     * @return
     */
    public static VoiceConversionResponse parseResponse(byte[] resp) {
        VoiceConversionResponse conversionResponse = null;
        try {
            if (resp.length > 4) {
                int headLen = 4;
                byte[] head = new byte[headLen];
                System.arraycopy(resp, 0, head, 0, headLen);
                int jsonLen = byteArrayToInt(head);
                if ((resp.length - headLen - jsonLen) >= 0) {
                    byte[] json = new byte[jsonLen];
                    System.arraycopy(resp, headLen, json, 0, jsonLen);
                    conversionResponse = new Gson().fromJson(new String(json), VoiceConversionResponse.class);
                    int audioLen = resp.length - headLen - jsonLen;
                    if (conversionResponse != null && audioLen > 0) {
                        byte[] audio = new byte[audioLen];
                        System.arraycopy(resp, headLen + jsonLen, audio, 0, audioLen);
                        conversionResponse.setAudio(audio);
                    }
                }
            }
            if (conversionResponse == null) {
                conversionResponse = new VoiceConversionResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conversionResponse;
    }
}
