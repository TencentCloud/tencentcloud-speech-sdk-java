# 简介
欢迎使用腾讯云语音SDK，腾讯云语音SDK为开发者提供了访问腾讯云语音识别、语音合成等语音服务的配套开发工具，简化腾讯云语音服务的接入流程。
# 依赖环境
1. 依赖环境: JDK 1.8版本及以上
2. 使用相关产品前需要在腾讯云控制台已开通相关语音产品。
3. 在腾讯云控制台[账号信息](https://console.cloud.tencent.com/developer)页面查看账号APPID，[访问管理](https://console.cloud.tencent.com/cam/capi)页面获取 SecretID 和 SecretKey 。


# 获取安装
安装 Java SDK 前,先获取安全凭证。在第一次使用SDK之前，用户首先需要在腾讯云控制台上申请安全凭证，安全凭证包括 SecretID 和 SecretKey，SecretID 是用于标识 API 调用者的身份，SecretKey 是用于加密签名字符串和服务器端验证签名字符串的密钥 SecretKey 必须严格保管，避免泄露。


## 通过 Maven 安装
从maven服务器下载最新版本SDK
```xml
<!-- https://mvnrepository.com/artifact/com.tencentcloudapi/tencentcloud-speech-sdk-java -->
<dependency>
    <groupId>com.tencentcloudapi</groupId>
    <artifactId>tencentcloud-speech-sdk-java</artifactId>
    <version>1.0.18</version>
</dependency>
```

## 示例
参见[example](https://github.com/TencentCloud/tencentcloud-speech-sdk-java-example)

