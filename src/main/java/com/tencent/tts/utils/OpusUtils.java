package com.tencent.tts.utils;

import com.tencent.core.service.ReportService;
import com.tencent.core.utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OpusUtils {

    /**
     * 这里需要注意golang的byte是uint8 java是int8 不能使用BytesReader的toInt方法转换
     *
     * @param bytes
     * @return uint8 数据
     */
    public static int convertGoByte2Int(byte[] bytes) {
        if (bytes == null || bytes.length != 4) {
            return 0;
        }
        byte b1 = bytes[0];
        byte b2 = bytes[1];
        byte b3 = bytes[2];
        byte b4 = bytes[3];
        return (getUint8(b1) * 1 + getUint8(b2) * 256 + getUint8(b3) * 256 * 256 + getUint8(b4) * 256 * 256 * 256);
    }

    public static int getUint8(short s) {
        return (s & 0x00ff);
    }

    /**
     * 验证 opus header
     *
     * @param headBuffer
     * @return 是否正确
     */
    public static boolean verifyHeader(byte[] headBuffer) {
        String header = new String(headBuffer);
        return header.equals("opus");
    }

    /**
     * 读取数据
     *
     * @param in
     * @param buffer
     * @return 是否成功
     * @throws IOException
     */
    public static boolean read(InputStream in, byte[] buffer) throws IOException {
        int length = buffer.length;
        int offset = 0;
        while (true) {
            int count = length - offset;
            int currentRead = in.read(buffer, offset, count);
            if (currentRead >= 0) {
                offset += currentRead;
                if (offset == length) {
                    return true;
                }
            }
            if (currentRead == -1) {
                return false;
            }
        }
    }


    /**
     * 解析opus
     *
     * @param audio audio
     * @return List<byte[]>
     */
    public static List<byte[]> readOpusSheet(byte[] audio) {
        byte[] data = ByteUtils.copy(audio);
        int lengthSum=0;
        List<byte[]> sheets = new ArrayList<>();
        while (true) {
            // read header
            if (data.length < 4) {
                return sheets;
            }
            byte[] headBytes = ByteUtils.subBytes(data, 0, 4);
            if (!OpusUtils.verifyHeader(headBytes)) {
                ReportService.ifLogMessage("readOpus", "Get header values abnormal, not opus but: " + new String(headBytes), false);
                break;
            }
            // read seq
            if(data.length<8){
                ReportService.ifLogMessage("readOpus", "Get seq values abnormal, not seq  ", false);
                return sheets;
            }
            byte[] seqBytes = ByteUtils.subBytes(data, 4, 4);
            int seq = OpusUtils.convertGoByte2Int(seqBytes);
            if (seq < -1) {
                ReportService.ifLogMessage("readOpus", "Get seq abnormal: " + seq, false);
                break;
            }
            if(data.length<12){
                return sheets;
            }
            // read pkg size
            byte[] lengthBytes = ByteUtils.subBytes(data, 8, 4);
            int length = OpusUtils.convertGoByte2Int(lengthBytes);
            ReportService.ifLogMessage("readOpus", "seq:" + seq + ",length:" + length, false);
            if (seq == -1) {
                //其中最后一片音频（序号 S = -1）数据固定为“AAAA”，该段数据无效。 直接返回
                byte[] leftBytes = ByteUtils.subBytes(data, 8, data.length - 8);
                ReportService.ifLogMessage("readOpus end", new String(leftBytes), false);
                break;
            }
            if (length <= 0) {
                break;
            }
            byte[] datas = new byte[0];
            if (length > 0) {
                // read pkg
                length = (data.length - 12) < length ? data.length - 12 : length;
                datas = ByteUtils.subBytes(data, 12, length);
            }
            byte[] result = new byte[0];
            result = ByteUtils.concat(result, headBytes);
            result = ByteUtils.concat(result, seqBytes);
            result = ByteUtils.concat(result, lengthBytes);
            result = ByteUtils.concat(result, datas);
            sheets.add(result);
            lengthSum=lengthSum+length+12;
            data = ByteUtils.subBytes(data, 12 + length, data.length - 12 - length);
        }
        return sheets;
    }

}
