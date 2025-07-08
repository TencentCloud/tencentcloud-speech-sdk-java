package com.tencent.tts.utils;

public class TempUtils {

    public static String bytesToHexStringWithSpace(final byte[] bytes) {
        if (bytes.length == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(toHex(b) + " ");
        }
        return sb.toString().trim();
    }

    private static String toHex(final byte b) {
        String hex = Integer.toHexString(unsignedValue(b));
        if (hex.length() == 2) {
            return hex.toUpperCase();
        } else {
            return "0" + hex.toUpperCase();
        }
    }

    public static int unsignedValue(final byte b) {
        // 使用&操作符时，byte会自动变成int。如果该byte的第一位为1，则前面多出来的24位都是1。
        // 把它的前24位变成0，这样负数就变成了正数
        return b & 0xFF;
    }

}
