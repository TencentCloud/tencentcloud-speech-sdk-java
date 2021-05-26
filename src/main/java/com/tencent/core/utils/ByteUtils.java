/*
 * Copyright (c) 2017-2018 THL A29 Limited, a Tencent company. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.core.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ByteUtils {

    /**
     * 返回一个byte[]中指定的一部分。为了性能考虑，未对参数进行任何验证。
     *
     * @param bs         原byte[]
     * @param startIndex 开始索引
     * @param length     要拷贝的字节长度
     * @return byte[]中指定的一部分
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static byte[] subBytes(byte[] bs, int startIndex, int length) {
        byte[] sub = new byte[length];
        System.arraycopy(bs, startIndex, sub, 0, length);
        return sub;
    }


    public static byte[] copy(byte[] bs) {
        return Arrays.copyOf(bs, bs.length);
    }

    /**
     * 合并两个byte[]
     *
     * @param bytes1 第一个byte[]
     * @param bytes2 第二个byte[]
     * @return 合并后的数组
     * @throws IllegalArgumentException 如果任一参数为null
     */
    public static byte[] concat(byte[] bytes1, byte[] bytes2) {
        byte[] target = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, target, 0, bytes1.length);
        System.arraycopy(bytes2, 0, target, bytes1.length, bytes2.length);
        return target;
    }

    /**
     * subToSmallBytes
     *
     * @param bs        bs
     * @param minLength minLength
     * @param maxLength maxLength
     * @return List
     */
    public static List<byte[]> subToSmallBytes(byte[] bs, int minLength, int maxLength) {
        int length = bs.length;
        if (maxLength > length) {
            maxLength = length;
        }
        List<byte[]> list = new ArrayList<byte[]>();
        int posi = 0;
        while (posi < length) {
            int randomLen = getRandomValue(minLength, maxLength);
            if (posi + randomLen > length) {
                randomLen = length - posi;
            }
            list.add(subBytes(bs, posi, randomLen));
            posi += randomLen;
        }
        return list;
    }

    /**
     * 使用流的方式将文件读取一遍，然后切分成小的数组返回，数组大小为一个固定的值。
     *
     * 切分大文件时会比{@link com.tencent.core.utils.ByteUtils#subToSmallBytes(byte[], int, int)}方法快一点。
     *
     * @param file   文件
     * @param subLen 切成指定的大小。
     * @return 字节数组
     */
    public static List<byte[]> subToSmallBytes(File file, int subLen) throws FileNotFoundException {
        return subToSmallBytes(new FileInputStream(file), subLen);
    }

    /**
     * 切分成小的数组返回，数组大小为一个固定的值。
     *
     * @param inputStream inputStream
     * @param subLen      subLen
     * @return list
     */
    public static List<byte[]> subToSmallBytes(InputStream inputStream, int subLen) {
        List<byte[]> list = new ArrayList<byte[]>();
        int available = 0, readLength = 0;
        try {
            available = inputStream.available();
            while (available > 0) {
                byte[] subBytes = new byte[subLen]; // 每次使用新的字节数组，避免add到缓存中的数组是同一条，造成异常。
                readLength = inputStream.read(subBytes);
                if (readLength == subLen) {
                    list.add(subBytes);
                } else if (readLength > 0) {
                    list.add(ByteUtils.subBytes(subBytes, 0, readLength));
                }
                available = inputStream.available();
            }
        } catch (IOException e) {
            System.err.println("Unexpected IOException: " + e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                // ignore
            }
        }
        return list;
    }

    private static int getRandomValue(int minLength, int maxLength) {
        Random random = new Random();
        return random.nextInt(maxLength - minLength) + minLength;
    }

    public static byte[] inputStream2ByteArray(String filePath) {
        File file = new File(filePath);
        return inputStream2ByteArray(file);
    }

    public static byte[] inputStream2ByteArray(File file) {
        InputStream in;
        try {
            in = new FileInputStream(file);
            byte[] data = toByteArray(in);
            in.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] toByteArray(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        try {
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
