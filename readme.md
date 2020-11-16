# 简介
欢迎使用腾讯云实时语音识别和实时语音合成SDK。
# 依赖环境
1. 依赖环境: JDK 1.8版本及以上
2. 从 腾讯云控制台 开通相应产品。
3. 获取 SecretID、SecretKey ，具体参考<https://cloud.tencent.com/document/product/1093>。


# 获取安装
安装 Java SDK 前,先获取安全凭证。在第一次使用SDK之前，用户首先需要在腾讯云控制台上申请安全凭证，安全凭证包括 SecretID 和 SecretKey，SecretID 是用于标识 API 调用者的身份，SecretKey 是用于加密签名字符串和服务器端验证签名字符串的密钥 SecretKey 必须严格保管，避免泄露。


## 通过 Maven 安装
从maven服务器下载最新版本SDK
```xml
<dependency>
    <groupId>com.tencentcloudapi</groupId>
    <artifactId>tencentcloud-speech-sdk-java</artifactId>
    <version>1.0.5</version>
</dependency>
```

## 代码参考

[参考案例](https://github.com/TencentCloud/tencentcloud-speech-sdk-java-example "参考案例")

# ASR SDK说明
##  关键类说明

- SpeechClient  用于创建SpeechRecognizer语音识别器的客户端，通过SpeechClient.newInstance创建该实例，newInstance为单例实现。
- SpeechRecognizer 语音识别器，通过客户端speechClient.newSpeechRecognizer创建实例。
- SpeechRecognitionRequest 用于配置请求参数，可通过SpeechRecognitionRequest.initialize()方法进行初始化。
- SpeechRecognitionResponse 请求响应。
- SpeechRecognitionListener 请求回调。包含识别开始，识别结束等回调方法。

## SDK使用说明

1. 创建SpeechClient实例。
2. 创建SpeechRecognitionRequest,这里配置请求相关参数包含切片大、引擎模型类型、文件格式等，具体参考官网请求参数<https://cloud.tencent.com/document/product/1093/35799>。
3. 创建SpeechRecognizer实例，该实例是语音识别的处理者。
4. 调用SpeechRecognizer的start方法,开始识别。
5. 调用SpeechRecognizer的write方法开始发送语音数据。
6. 调用SpeechRecognizer的stop方法,结束识别。


# 示例
#### 代码示例

```java
package com.tencentcloud.asr;

import com.tencent.SpeechClient;
import com.tencent.asr.model.*;
import com.tencent.asr.service.SpeechRecognitionListener;
import com.tencent.asr.service.SpeechRecognizer;
import com.tencent.core.model.GlobalConfig;
import com.tencent.core.utils.ByteUtils;
import com.tencent.core.utils.JsonUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class NewSpeechRecognitionHttpByteArrayExample {

    public static void main(String[] args) throws InterruptedException, IOException {
        GlobalConfig.ifLog = true;
        //默认使用websocket协议，可通过该配置指定协议类型
        //SpeechRecognitionSysConfig.requestWay= AsrConstant.RequestWay.Http;
        Properties props = new Properties();
        //从配置文件读取密钥
        props.load(new FileInputStream("../config.properties"));
        String appId = props.getProperty("appId");
        String secretId = props.getProperty("secretId");
        String secretKey = props.getProperty("secretKey");
        
        //1.创建client实例 client为单例
        final SpeechClient speechClient = SpeechClient.newInstance(appId, secretId, secretKey);
        //2.创建SpeechRecognizerRequest,这里配置请求相关参数包含切片大小、文件格式等
        final SpeechRecognitionRequest request = SpeechRecognitionRequest.initialize();
        //必须手动设置EngineModelType
        request.setEngineModelType("16k_zh");
        //根据文件格式设置VoiceFormat
        request.setVoiceFormat(1);
        //3.创建SpeechRecognizer实例，该实例是语音识别的处理者。
        SpeechRecognizer speechRecognizer = speechClient.newSpeechRecognizer(request, new MySpeechRecognitionListener());
        //案例使用文件模拟实时获取语音流，用户使用可直接调用recognize(data)传入字节数据
        FileInputStream fileInputStream = new FileInputStream(new File("8k.wav"));
        List<byte[]> speechData = ByteUtils.subToSmallBytes(fileInputStream, 3200);
        //4.调用SpeechRecognizer的start方法,开始识别。
        speechRecognizer.start();
        for (byte[] item : speechData) {
            // 休眠用于模拟语音时长，方便测试，休眠时间根据传输数据选择对应值。实际使用不用休眠
            // 参考时长：8k 3200字节 对应200ms  16k 6400字节对应200ms
            Thread.sleep(200);
            //5.调用SpeechRecognizer的recognize方法开始发送语音数据。
            speechRecognizer.write(item);
        }
        //6.调用SpeechRecognizer的end方法,结束识别。
        speechRecognizer.stop();
        fileInputStream.close();
    }

   

    public static class MySpeechRecognitionListener extends SpeechRecognitionListener {
        @Override
        public void onRecognitionResultChange(SpeechRecognitionResponse response) {
            //System.out.println("识别结果:"+JsonUtil.toJson(response));
        }

        @Override
        public void onRecognitionStart(SpeechRecognitionResponse response) {
            System.out.println("开始识别:" + JsonUtil.toJson(response));
        }

        @Override
        public void onSentenceBegin(SpeechRecognitionResponse response) {
            System.out.println("一句话开始:" + JsonUtil.toJson(response));
        }

        @Override
        public void onSentenceEnd(SpeechRecognitionResponse response) {
            System.out.println("一句话结束:" + JsonUtil.toJson(response));
        }

        @Override
        public void onRecognitionComplete(SpeechRecognitionResponse response) {
            System.out.println("识别结束:" + JsonUtil.toJson(response));
        }

        @Override
        public void onFail(SpeechRecognitionResponse response) {
            System.out.println("错误:" + JsonUtil.toJson(response));
        }
    }
}


```


# TTS SDK说明
##  关键类说明

- SpeechClient  用于创建SpeechRecognizer语音合成器的客户端，通过SpeechClient.newInstance创建该实例，newInstance为单例实现。
- SpeechSynthesizer 语音合成器，通过客户端speechClient.newSpeechSynthesizer创建实例。
- SpeechSynthesisRequest 用于配置请求参数，SpeechSynthesisRequest.initialize()方法进行初始化。
- SpeechSynthesisResponse 请求响应。
- SpeechSynthesisListener 请求回调。包含onMessage onComplete  onFail 回调方法。
#### 注意事项



## SDK使用说明

1. 创建SpeechClient实例。
2. 创建SpeechSynthesisRequest,这里配置请求相关参数，具体参考官网请求参数<https://cloud.tencent.com/document/product/1073/34093>。
3. 创建SpeechSynthesizer实例，该实例是语音识别的处理者。
4. 调用SpeechSynthesizer的synthesis方法开始发送语音数据。

# 示例
#### 代码示例

```java
package com.tencentcloud.tts;

import com.tencent.SpeechClient;
import com.tencent.core.model.GlobalConfig;
import com.tencent.tts.model.*;
import com.tencent.tts.service.SpeechSynthesizer;
import com.tencent.tts.service.SpeechSynthesisListener;
import com.tencent.tts.utils.Ttsutils;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 语音合成 example
 */
public class SpeechTtsExample {

    public static void main(String[] args) throws IOException {
        GlobalConfig.ifLog=true;
        //从配置文件读取密钥
        Properties props = new Properties();
        props.load(new FileInputStream("../config.properties"));
        String appId = props.getProperty("appId");
        String secretId = props.getProperty("secretId");
        String secretKey = props.getProperty("secretKey");


        //创建SpeechSynthesizerClient实例，目前是单例
        SpeechClient client = SpeechClient.newInstance(appId, secretId, secretKey);
        //初始化SpeechSynthesizerRequest，SpeechSynthesizerRequest包含请求参数
        SpeechSynthesisRequest request = SpeechSynthesisRequest.initialize();


        //使用客户端client创建语音合成实例
        SpeechSynthesizer speechSynthesizer = client.newSpeechSynthesizer(request, new SpeechTtsExample.MySpeechSynthesizerListener());
        //执行语音合成
        String ttsTextLong = "暖国的雨，向来没有变过冰冷的坚硬的灿烂的雪花。博识的人们觉得他单调，他自己也以为不幸否耶？江南的雪，可是滋润美艳之至了；" +
                "那是还在隐约着的青春的消息，是极壮健的处子的皮肤。雪野中有血红的宝珠山茶，白中隐青的单瓣梅花，深黄的磬口的蜡梅花；雪下面还有冷绿的杂草。" +
                "蝴蝶确乎没有；蜜蜂是否来采山茶花和梅花的蜜，我可记不真切了。但我的眼前仿佛看见冬花开在雪野中，有许多蜜蜂们忙碌地飞着，也听得他们嗡嗡地闹着。" +
                "孩子们呵着冻得通红，像紫芽姜一般的小手，七八个一齐来塑雪罗汉。因为不成功，谁的父亲也来帮忙了。罗汉就塑得比孩子们高得多，虽然不过是上小下大的一堆，" +
                "终于分不清是壶卢还是罗汉；然而很洁白，很明艳，以自身的滋润相粘结，整个地闪闪地生光。孩子们用龙眼核给他做眼珠，又从谁的母亲的脂粉奁中偷得胭脂来涂在嘴唇上。" +
                "这回确是一个大阿罗汉了。他也就目光灼灼地嘴唇通红地坐在雪地里。";
        speechSynthesizer.synthesis(ttsTextLong);
    }


    public static class MySpeechSynthesizerListener extends SpeechSynthesisListener {

        private AtomicInteger sessionId = new AtomicInteger(0);

        @Override
        public void onComplete(SpeechSynthesisResponse response) {
            System.out.println("onComplete");
            if (response.getSuccess()) {
                Ttsutils.printAndSaveResponse(16000, response.getAudio(), response.getSessionId());
            }
            System.out.println("结束：" + response.getSuccess() + " " + response.getCode() + " " + response.getMessage() + " " + response.getEnd());
        }

        //语音合成的语音二进制数据
        @Override
        public void onMessage(byte[] data) {
            System.out.println("onMessage:" + data.length);
            // Your own logic.
            String filePath = "logs/handler_" + "result_" + sessionId + ".pcm";
            //Ttsutils.saveResponseToFile(data, filePath);
            sessionId.incrementAndGet();
        }

        @Override
        public void onFail(SpeechSynthesisResponse response) {
            System.out.println("onFail");
        }
    }
}


```


