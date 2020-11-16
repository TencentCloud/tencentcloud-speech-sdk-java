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
package com.tencent.tts.model;

public class SpeechSynthesisSysConfig {

    /**
     * 每次发出请求时，最多携带的字符数。如果一行文字的长度超过此值，则会被截成多条请求发出。
     */
    public static int SEPARATOR_LENGTH_LIMIT = 100;

    /**
     * 对每行文字做分割时的关键字，遇到这里的字符肯定会切分开。
     */
    public static String[] SEPARATOR_CHARS = new String[] { "。", "！", "？", "!", "?", "." };
}
