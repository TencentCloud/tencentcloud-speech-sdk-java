### 第4次发布

版本号 1.0.4 （最新版本）

本次发布包含了以下内容：

1. 修改已知问题。
2. 规范文档描述。
3. asr 修改调用方法名，原有的 recognize修改为write ,end修改为stop ,添加start方法。
4. asr 1.0.4版本方法调用顺序由recognize->end修改为 start->write->stop。
5. tts 修改类名，原有前缀为SpeechSynthesizer 修改为SpeechSynthesis。
6. asr添加websocket的实现方式。
7. json解析框架fastjson替换为jackson。
8. asr http添加同步实现方式可通过SpeechRecognitionSysConfig.ifSyncHttp=true; 指定。

### 第3次发布

版本号 1.0.3 

本次发布包含了以下内容：

1. TTS  SpeechSynthesizerListener回调添加cancel方法，用户可取消请求。
2. 规范文档。

### 第2次发布

版本号 1.0.2
本次发布包含了以下内容：

1. 规范调用方式以及命名方式。具体调用方式可参考案例。
2. 支持TTS SDK .
3. 优化ASR SDK 调用方式。

### 第1次发布
版本号 1.0.1

发布时间：2020-09-12 08:22:02
本次发布包含了以下内容：

初始化语音识别SDK代码。




