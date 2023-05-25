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

package com.tencent.asr.constant;

public class AsrConstant {

    /**
     * 语音识别的方式
     * http:采用http协议进行语音识别
     * websocket:采用ws协议实现语音识别
     */
    public enum RequestWay {
        Http, Websocket;
    }

    /**
     * http 协议请求框架
     * 目前两种 默认使用okhttp进行网络请求 可通过AsrSysConfig.httpFrame配置
     */
    @Deprecated
    public enum HttpFrame {
        OK_HTTP, HTTP_CLIENT;
    }

    /**
     * 请求类型
     */
    public enum DataType {
        /**
         * 流式处理
         */
        STREAM(0),
        /**
         * 字节数组式处理
         */
        BYTE(1),
        /**
         * ws协议 stream
         */
        WS_STREAM(2),
        /**
         * ws协议 byte[]
         */
        WS_BYTE(2);

        private DataType(int type) {
            this.type = type;
        }

        private Integer type;

        public Integer getType() {
            return type;
        }
    }

    /**
     * 返回类型：实时返回或者尾包返回
     */
    public enum ReturnType {

        REALTIME_FOLLOW(0), TAILER(1);

        private int typeId;

        private ReturnType(int typeId) {
            this.typeId = typeId;
        }

        public int getTypeId() {
            return typeId;
        }
    }


    /**
     * 返回值数据的编码
     */
    public enum ResponseEncode {

        UTF_8(0, "utf-8"), GB2312(1, "gb2312"), GBK(2, "gbk"), BIG5(3, "BIG5");

        private int id;
        private String name;

        private ResponseEncode(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }


    /**
     * 声音类型 mp3仅仅16k_0模型支持，其它的则都能支持。
     */
    public enum VoiceFormat {
        wav(1, "wav"),
        sp(4, "sp"),
        silk(6, "silk"),
        mp3(8, "mp3");

        private int formatId;
        private String formatName;

        VoiceFormat(int formatId, String formatName) {
            this.formatId = formatId;
            this.formatName = formatName;
        }

        public int getFormatId() {
            return formatId;
        }

        public String getFormatName() {
            return formatName;
        }

        public int getFormatIdByName(String formatName) {
            VoiceFormat[] voiceFormats = VoiceFormat.values();
            for (VoiceFormat voiceFormat : voiceFormats) {
                if (voiceFormat.formatName.equals(formatName)) {
                    return voiceFormat.formatId;
                }
            }
            throw new RuntimeException("未找到对应的声音类型");
        }

        public String getFormatNameById(int formatId) {
            VoiceFormat[] voiceFormats = VoiceFormat.values();
            for (VoiceFormat voiceFormat : voiceFormats) {
                if (voiceFormat.formatId == formatId) {
                    return voiceFormat.formatName;
                }
            }
            throw new RuntimeException("未找到对应的声音类型");
        }
    }


    public enum Code {
        IO_EXCEPTION(-4, "IO EXCEPTION"),
        FAIL(-3, "请求失败"),
        NO_SUCCESS(-2, "状态码错误"),
        EXCEPTION(-1, "Runtime异常"),
        SUCCESS(0, "success"),
        CODE_100(100, "获取语音分片信息失败"),
        CODE_101(101, "语音分片过大"),
        CODE_102(102, "参数不合法，具体详情参考 message"),
        CODE_103(103, "访问数据库失败"),
        CODE_104(104, "AppID 未注册"),
        CODE_105(105, "模板不存在"),
        CODE_106(106, "模板停用"),
        CODE_107(107, "鉴权失败"),
        CODE_108(108, "拼接签名串失败"),
        CODE_109(109, "l5获取 IP、port 失败"),
        CODE_110(110, "后台识别服务器故障，请从 seq = 0重传"),
        CODE_111(111, "后台识别模块回包格式错误"),
        CODE_112(112, "语音分片为空"),
        CODE_113(113, "后台服务器识别超时"),
        CODE_114(114, "引擎编号不合法"),
        CODE_115(115, "时长计算时音频类型不合法"),
        CODE_116(116, "无可使用的免费额度"),
        CODE_117(117, "禁止访问"),
        CODE_118(118, "请求限流"),
        CODE_119(119, "账户欠费停止服务，请及时充值"),
        CODE_120(120, "获取 rpcClient 错误"),
        CODE_121(121, "后台识别服务器错误，请从seq = 0重传"),
        CODE_122(122, "后台识别服务器收到的包格式错误"),
        CODE_123(123, "后台识别服务器音频解压失败，请从seq = 0重传"),
        CODE_124(124, "后台识别服务器识别失败，请从seq = 0重传"),
        CODE_125(125, "后台识别服务器识别失败，请重新尝试"),
        CODE_126(126, "后台识别服务器音频分片等待超时，请从seq = 0重传"),
        CODE_127(127, "后台识别服务器音频分片重复"),

        CODE_10001(10001, "创建websocket连接失败"),
        CODE_10002(10002, "请调用start方法开始创建链接"),
        CODE_10003(10003, "写入失败，由于失败（onFail）或者已调用stop方法"),
        CODE_10004(10004, "连接关闭"),
        CODE_10009(10009, "secretId 或 secretKey 为空"),
        CODE_10010(10010, "参数错误"),
        CODE_10011(10011, "回调异常"),
        CODE_10012(10012, "请求失败");

        Code(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        private Integer code;
        private String message;


        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public static boolean ifInRetryCode(int code) {
            Code[] codes = {CODE_110, CODE_121, CODE_123, CODE_124, CODE_126, CODE_113, CODE_125};
            for (Code temp : codes) {
                if (code == temp.getCode()) {
                    return true;
                }
            }
            return false;
        }

        public static boolean ifInBreakCode(int code) {
            Code[] codes = {CODE_104, CODE_107};
            for (Code temp : codes) {
                if (code == temp.getCode()) {
                    return true;
                }
            }
            return false;
        }

        public static Code getCodeByCode(int code) {
            for (Code temp : Code.values()) {
                if (temp.getCode().equals(code)) {
                    return temp;
                }
            }
            return null;
        }
    }

}
