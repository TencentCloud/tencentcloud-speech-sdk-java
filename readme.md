# 简介
欢迎使用腾讯云实时语音识别和实时语音合成SDK。
# 依赖环境
1. 依赖环境: JDK 1.8版本及以上
2. 从 腾讯云控制台 开通相应产品。
3. 获取 SecretID、SecretKey ，具体参考[官网文档](https://cloud.tencent.com/document/product/1093)。


# 获取安装
安装 Java SDK 前,先获取安全凭证。在第一次使用SDK之前，用户首先需要在腾讯云控制台上申请安全凭证，安全凭证包括 SecretID 和 SecretKey，SecretID 是用于标识 API 调用者的身份，SecretKey 是用于加密签名字符串和服务器端验证签名字符串的密钥 SecretKey 必须严格保管，避免泄露。


## 通过 Maven 安装
从maven服务器下载最新版本SDK
```xml
<!-- https://mvnrepository.com/artifact/com.tencentcloudapi/tencentcloud-speech-sdk-java -->
<dependency>
    <groupId>com.tencentcloudapi</groupId>
    <artifactId>tencentcloud-speech-sdk-java</artifactId>
    <version>1.0.8</version>
</dependency>
```
# ASR SDK说明
##  关键类说明

- SpeechClient  用于创建SpeechRecognizer语音识别器的客户端，通过SpeechClient.newInstance创建该实例，newInstance为单例实现。
- SpeechRecognizer 语音识别器，通过客户端speechClient.newSpeechRecognizer创建实例。
- SpeechRecognitionRequest 用于配置请求参数，可通过SpeechRecognitionRequest.initialize()方法进行初始化。
- SpeechRecognitionResponse 请求响应。
- SpeechRecognitionListener 请求回调。包含识别开始，识别结束等回调方法。
#### 注意事项



## SDK使用说明
1. 创建SpeechClient实例。
2. 创建SpeechRecognitionRequest,这里配置请求相关参数包含切片大、引擎模型类型、文件格式等，具体参考[官网请求参数](https://cloud.tencent.com/document/product/1093/35799)。
3. 创建SpeechRecognizer实例，该实例是语音识别的处理者。
4. 调用SpeechRecognizer的start方法,开始识别。
5. 调用SpeechRecognizer的write方法开始发送语音数据。
6. 调用SpeechRecognizer的stop方法,结束识别。

# 示例
参见[example](https://github.com/TencentCloud/tencentcloud-speech-sdk-java-example/tree/main/src/main/java/com/tencentcloud/asr)

# TTS SDK说明
##  关键类说明

- SpeechClient  通过SpeechClient.newInstance创建该实例，newInstance为单例实现。
- SpeechSynthesizer 语音合成器，通过客户端speechClient.newSpeechSynthesizer创建实例。
- SpeechSynthesizerRequest 用于配置请求参数，可通过SpeechSynthesizerRequest.initialize()方法进行初始化。
- SpeechSynthesizerResponse 请求响应。
- SpeechSynthesizerListener 请求回调。包含onMessage onComplete  onFail 回调方法。
#### 注意事项



## SDK使用说明
1. 创建SpeechClient实例。
2. 创建SpeechSynthesisRequest,这里配置请求相关参数，具体参考[官网请求参数](https://cloud.tencent.com/document/product/1073/34093)。
3. 创建SpeechSynthesizer实例，该实例是语音识别的处理者。
4. 调用SpeechSynthesizer的synthesis方法开始发送语音数据。

# 示例
参见[example](https://github.com/TencentCloud/tencentcloud-speech-sdk-java-example/tree/main/src/main/java/com/tencentcloud/tts)

