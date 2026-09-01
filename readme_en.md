# Introduction
Welcome to the Tencent Cloud Speech SDK. The Tencent Cloud Speech SDK provides developers with a supporting development toolkit for accessing Tencent Cloud speech services such as speech recognition and speech synthesis, simplifying the integration process of Tencent Cloud speech services.

# Environment Requirements
1. Environment: JDK 1.8 or higher
2. Before using the related products, you need to activate the corresponding speech products in the Tencent Cloud console.
3. Obtain your account APPID on the [Account Information](https://console.cloud.tencent.com/developer) page of the Tencent Cloud console, and obtain your SecretID and SecretKey on the [CAM (Cloud Access Management)](https://console.cloud.tencent.com/cam/capi) page.


# Installation
Before installing the Java SDK, obtain your security credentials. Before using the SDK for the first time, you need to apply for security credentials in the Tencent Cloud console. Security credentials include a SecretID and a SecretKey. The SecretID is used to identify the API caller, and the SecretKey is used to encrypt the signature string and verify the signature string on the server side. The SecretKey must be kept strictly confidential to avoid leakage.


## Install via Maven
Download the latest version of the SDK from the Maven server:
```xml
<!-- https://mvnrepository.com/artifact/com.tencentcloudapi/tencentcloud-speech-sdk-java -->
<dependency>
    <groupId>com.tencentcloudapi</groupId>
    <artifactId>tencentcloud-speech-sdk-java</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
```

## Examples
See [example](https://github.com/TencentCloud/tencentcloud-speech-sdk-java-example)

