package com.tencent.tts.utils;

public class BytesReader {

    /**
     * 得到对应的int类型数据。如果参数为null或长度不为4则返回0.
     *
     * @param bytes 字节数据
     * @return 对应的int类型数据
     */
    public static int toInt(byte[] bytes) {
        if (bytes == null || bytes.length != 4) {
            return 0;
        }
        byte b1 = bytes[0];
        byte b2 = bytes[1];
        byte b3 = bytes[2];
        byte b4 = bytes[3];
        return ((b1 & 0xFF) << 24) + ((b2 & 0xFF) << 16) + ((b3 & 0xFF) << 8) + (b4 & 0xFF);
    }

    /**
     * 获取一个unsigned 4-byte的整数，结果以long表示，取值范围为0~4294967295
     *
     * 如果参数为null或长度不为4则返回0.
     *
     * @param bytes 字节数据
     * @return unsigned 4-byte的整数
     */
    public static long toUnsignedInt(byte[] bytes) {
        if (bytes == null || bytes.length != 4) {
            System.err.println("toInt failed, bytes length not 4.");
            return 0;
        }
        byte b1 = bytes[0];
        byte b2 = bytes[1];
        byte b3 = bytes[2];
        byte b4 = bytes[3];
        return ((b1 & 0xFFL) << 24) + ((b2 & 0xFF) << 16) + ((b3 & 0xFF) << 8) + (b4 & 0xFF);
    }

}
