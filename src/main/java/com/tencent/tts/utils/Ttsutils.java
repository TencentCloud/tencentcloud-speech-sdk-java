package com.tencent.tts.utils;

import com.tencent.core.service.ReportService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Ttsutils {

    /**
     * pcm文件转wav
     *
     * @param sampleRate sampleRate
     * @param response   response
     * @param sessionId  sessionId
     * @return 文件地址
     */
    public static String responsePcm2Wav(int sampleRate, byte[] response, String sessionId) {
        return printAndSaveResponse(sampleRate, response, sessionId);
    }

    /**
     * printAndSaveResponse
     *
     * @param sampleRate sampleRate
     * @param response   response
     * @param sessionId  sessionId
     * @return 文件地址
     */
    public static String printAndSaveResponse(int sampleRate, byte[] response, String sessionId) {
        if (response != null) {
            new File("logs").mkdirs();
            //获取返回包大小
            int rspLen = response.length;
            //wav大小为pcm字节+44wav头大小
            byte[] wav = new byte[44 + rspLen];
            int bitNum = sampleRate == 16000 ? 16 : 8;
            PcmUtils.pcm2WavBytes(response, wav, sampleRate, 1, bitNum);

            File wavFile = new File(sessionId + ".wav");
            saveWavFile(wav, wavFile);
            ReportService.ifLogMessage(sessionId, "Response: " + sessionId + ", length: "
                    + response.length + ", result saved at: " + wavFile.getAbsolutePath(), false);
            return wavFile.getAbsolutePath();
        } else {
            ReportService.ifLogMessage(sessionId, "Result is null.", true);
            return null;
        }
    }


    /**
     * 保存结果
     *
     * @param response response
     * @param file     file
     */
    private static void saveWavFile(byte[] response, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file, false);
            out.write(response);
            out.close();
        } catch (IOException e) {
            ReportService.ifLogMessage("saveWavFile", "Failed save data to: "
                    + file + ", error: " + e.getMessage(), true);
        }
    }

    /**
     * 填充数据
     *
     * @param in     in
     * @param buffer buffer
     * @return offset
     */
    public static int fill(InputStream in, byte[] buffer) throws IOException {
        int length = buffer.length;
        int offset = 0;
        while (true) {
            int count = length - offset;
            int currentRead = in.read(buffer, offset, count);
            if (currentRead >= 0) {
                offset += currentRead;
                /* System.out.println("offset: "+offset); */
                if (offset == length) {
                    return length;
                }
            }
            if (currentRead == -1) { // 表示数据已收完
                return offset;
            }
        }
    }

    /**
     * 保存数据到file
     *
     * @param response response
     * @param filePath filePath
     */
    public static void saveResponseToFile(byte[] response, String filePath) {
        try {
            new File(filePath).getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(filePath, true);
            out.write(response);
            out.close();
        } catch (IOException e) {
            ReportService.ifLogMessage("saveResponseToFile", "Failed save data to: " + filePath
                    + ", error: " + e.getMessage(), true);
        }
    }
}
