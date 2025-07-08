package com.tencent.asr.utils;

import cn.hutool.core.util.RandomUtil;

public class AsrUtils {

    public static String getVoiceId(Long id) {
        return id + "_" + System.currentTimeMillis() + "_" + RandomUtil.randomString(5);
    }
}
