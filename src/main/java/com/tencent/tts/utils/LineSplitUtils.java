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
package com.tencent.tts.utils;

import com.tencent.tts.model.SpeechSynthesisSysConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LineSplitUtils {


    /**
     * 比较智能地将一行较长的文字分隔成多个独立的句子，用于发送给远程服务器做识别。 用户可以覆盖此方法，从而实现自己的分割逻辑。
     *
     * <pre>
     * 目前已默认的做法是：
     * 1. 优先判断：句号，问号和叹号，发现时就做断句处理。使用的符号可在TtsConfig.SEPARATOR_CHARS配置
     * 2. 若分割后的句子依然超过限定长度，则再采用逗号分割一轮，同时分割后也会做短小片段的拼接，确保只会分割成大的语句块。
     * </pre>
     *
     * @param line 文本
     * @return 返回文本分割结果
     */
    public static List<String> smartSplit(String line) {
        List<String> list = new ArrayList<String>();
        if (line.length() <= SpeechSynthesisSysConfig.SEPARATOR_LENGTH_LIMIT) {
            list.add(line);
            return list;
        }
        // 关键字分割：
        splitAndAdd(list, line, 0);
        // 用逗号做进一步的细粒度分割：
        List<String> resultList = new ArrayList<String>();
        for (String item : list) {
            if (item.length() <= SpeechSynthesisSysConfig.SEPARATOR_LENGTH_LIMIT) {
                resultList.add(item);
            } else {
                slipByComma(resultList, item);
            }
        }
        return resultList;
    }

    private static void splitAndAdd(List<String> list, String line, int charPosi) {
        String mark = SpeechSynthesisSysConfig.SEPARATOR_CHARS[charPosi];
        String[] items = StringUtils.split(line, mark);
        for (String item : items) {
            if (items.length > 1 && notEndWithSeparator(item))
                item = item + mark; // 把符号补回来
            if (charPosi == SpeechSynthesisSysConfig.SEPARATOR_CHARS.length - 1) {
                list.add(item); // 递归结束。
            } else if (item.length() <= SpeechSynthesisSysConfig.SEPARATOR_LENGTH_LIMIT) {
                list.add(item);
            } else {
                splitAndAdd(list, item, charPosi + 1);
            }
        }
    }


    /**
     * 使用逗号做分割符，并且拼接短小的片段，将结果分割成几个大块。
     */

    private static void slipByComma(List<String> resultList, String item) {
        String[] subItems = StringUtils.split(item, "，,");
        if (subItems.length == 1) {
            resultList.add(item);
            return;
        }
        String subRes = subItems[0] + "，";
        for (int i = 1; i < subItems.length; i++) {
            String sub = subItems[i];
            if (sub.length() == 0)
                continue;
            if (notEndWithSeparator(sub))
                sub = sub + "，"; // 把逗号补回来
            if (subRes.length() + sub.length() > SpeechSynthesisSysConfig.SEPARATOR_LENGTH_LIMIT) {
                resultList.add(subRes);
                subRes = sub;
            } else {
                subRes += sub;
            }
        }
        if (subRes.length() > 0)
            resultList.add(subRes);
    }

    private static boolean notEndWithSeparator(String str) {
        return !ArrayUtils.contains(SpeechSynthesisSysConfig.SEPARATOR_CHARS, str.substring(str.length() - 1));
    }
}

