package com.tencent.tts.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 本类内容来自：https://blog.csdn.net/hesong1120/article/details/79043482 感谢作者和网站。
 *
 * 作者：Ihesong
 */
public class PcmUtils {


    /**
     * PCM文件转WAV文件
     *
     * @param inPcmFilePath  输入PCM文件路径
     * @param outWavFilePath 输出WAV文件路径
     * @param sampleRate     采样率，例如44100
     * @param channels       声道数 单声道：1或双声道：2
     * @param bitNum         采样位数，8或16
     */

    public static void convert2Wav(String inPcmFilePath, String outWavFilePath,
                                   int sampleRate, int channels, int bitNum) {
        convert2Wav(new File(inPcmFilePath), new File(outWavFilePath),
                sampleRate, channels, bitNum);
    }


    /**
     * PCM文件转WAV文件
     *
     * @param inPcmFile  输入PCM文件路径
     * @param outWavFile 输出WAV文件路径
     * @param sampleRate 采样率，例如44100
     * @param channels   声道数 单声道：1或双声道：2
     * @param bitNum     采样位数，8或16
     */
    public static void convert2Wav(File inPcmFile, File outWavFile, int sampleRate, int channels, int bitNum) {

        FileInputStream in = null;
        FileOutputStream out = null;
        byte[] data = new byte[1024];

        try {
            // 采样字节byte率
            long byteRate = sampleRate * channels * bitNum / 8;

            in = new FileInputStream(inPcmFile);
            out = new FileOutputStream(outWavFile, false); // 如需设置成追加模式，则改为true即可。

            // PCM文件大小
            long totalAudioLen = in.getChannel().size();

            // 总大小，由于不包括RIFF和WAV，所以是44 - 8 = 36，在加上PCM文件大小
            long totalDataLen = totalAudioLen + 36;

            writeWaveFileHeader(out, totalAudioLen, totalDataLen, sampleRate, channels, byteRate);

            int length = 0;
            while ((length = in.read(data)) > 0) {
                out.write(data, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * PCM转WAV字节数组
     *
     * @param pcm        pcm 字节组
     * @param wav        wav 字节组
     * @param sampleRate 采样率，例如44100
     * @param channels   声道数 单声道：1或双声道：2
     * @param bitNum     采样位数，8或16
     */

    public static void pcm2WavBytes(byte[] pcm, byte[] wav, int sampleRate, int channels, int bitNum) {
        pcm2Wav(pcm, wav, sampleRate, channels, bitNum);
    }

    /**
     * PCM转WAV字节数组
     *
     * @param pcm        pcm 字节组
     * @param wav        wav 字节组
     * @param sampleRate 采样率，例如44100
     * @param channels   声道数 单声道：1或双声道：2
     * @param bitNum     采样位数，8或16
     */
    public static void pcm2Wav(byte[] pcm, byte[] wav, int sampleRate, int channels, int bitNum) {
        byte[] header = new byte[44];

        // 采样字节byte率
        long byteRate = sampleRate * channels * bitNum / 8;
        // PCM文件大小
        long totalAudioLen = pcm.length;

        // 总大小，由于不包括RIFF和WAV，所以是44 - 8 = 36，在加上PCM文件大小
        long totalDataLen = totalAudioLen + 36;
        writeWaveBytesHeader(header, totalAudioLen, totalDataLen, sampleRate, channels, byteRate);
        System.arraycopy(header, 0, wav, 0, header.length);
        System.arraycopy(pcm, 0, wav, header.length, pcm.length);
    }

    /**
     * 输出WAV文件
     *
     * @param out           WAV输出文件流
     * @param totalAudioLen 整个音频PCM数据大小
     * @param totalDataLen  整个数据大小
     * @param sampleRate    采样率
     * @param channels      声道数
     * @param byteRate      采样字节byte率
     * @throws IOException
     */
    private static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen,
                                            int sampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        writeWaveBytesHeader(header, totalAudioLen, totalDataLen, sampleRate, channels, byteRate);
        out.write(header, 0, 44);
    }

    /**
     * writeWaveBytesHeader
     *
     * @param header        header
     * @param totalAudioLen 整个音频PCM数据大小
     * @param totalDataLen  整个数据大小
     * @param sampleRate    采样率
     * @param channels      声道数
     * @param byteRate      采样字节byte率
     */
    private static void writeWaveBytesHeader(byte[] header, long totalAudioLen, long totalDataLen,
                                             int sampleRate, int channels, long byteRate) {
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);// 数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';// WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        // FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';// 过渡字节
        // 数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // 编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        // 通道数
        header[22] = (byte) channels;
        header[23] = 0;
        // 采样率，每个通道的播放速度
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        // 音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (channels * 16 / 8);
        header[33] = 0;
        // 每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        // Data chunk
        header[36] = 'd';// data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
    }
}
