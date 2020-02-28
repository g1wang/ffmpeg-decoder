package com.wang.ffmpegdecoder.engine.thread;

import com.wang.ffmpegdecoder.ffmpeg.FFMpegUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 解码线程
 */
public class DecodeThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(DecodeThread.class);

    private String taskPath;
    //解码出的文件位置
    private String destPath;
    private String tmpSuffix;
    private boolean isExtFrames;
    private boolean isExtAudio;


    public DecodeThread(String taskPath, String destPath, String tmpSuffix, boolean isExtFrames, boolean isExtAudio) {
        this.taskPath = taskPath;
        this.destPath = destPath;
        this.tmpSuffix = tmpSuffix;
        this.isExtFrames = isExtFrames;
        this.isExtAudio = isExtAudio;
    }

    @Override
    public void run() {
        decode(this.taskPath, this.destPath, this.tmpSuffix, this.isExtFrames, this.isExtAudio);
    }

    private void decode(String taskPath, String destPath, String tmpSuffix, boolean isExtFrames, boolean isExtAudio) {
        File file = new File(taskPath);
        //文件解码
        String ffmpegUri = "";
        FFMpegUtil ffmpegUtil = new FFMpegUtil(ffmpegUri, taskPath);
        boolean validate = validateFile(ffmpegUtil, file);
        // 文件校验失败
        if (!validate) {
            //record the result
            return;
        }
        // 抽帧并返回结果
        if (isExtFrames) {
            if (!extFrames(taskPath, destPath, tmpSuffix, ffmpegUtil)) {
                // 抽帧失败
                //record the result
            }
        }

        //音频提取
        if (isExtAudio) {
            if (!extAudio(taskPath, destPath, tmpSuffix, ffmpegUtil)) {
                //record the result
            }
        }
    }


    /**
     * 校验文件
     *
     *
     * @param ffMpegUtil
     * @param file
     * @return
     */
    private boolean validateFile(FFMpegUtil ffMpegUtil, File file) {
        if (!file.exists()) {
            logger.error("【解码校验】：待解码的视频不存在-->" + file.getName());
            return false;
        }
        try {
            // 视频
            boolean isSupported = ffMpegUtil.isSupported();
            if (!isSupported) {
                // 1 表示文件不支持解码
                logger.info("【解码校验】:视频不支持解码-->" + file.getName());
                return false;
            }
            // 获取视频时长
            int runtime = ffMpegUtil.getRuntime();
            if (runtime == 0) {
                logger.info("【解码校验】:解码的视频runtime为0-->" + file.getName());
                return false;
            }
        } catch (Exception e) {
            logger.warn("【解码校验】:视频解码异常-->" + file.getName());
            return false;
        }
        return true;

    }

    /**
     * 抽帧
     *
     *
     * @param filePath
     * @param destPath
     * @param tmpSuffix
     * @param ffmpegUtil
     * @return
     */

    private boolean extFrames(String filePath, String destPath, String tmpSuffix, FFMpegUtil ffmpegUtil) {


        //从配置文件读取
        String screenSize = "96x96";
        double fps = 1;
        String outputDirType = "frame";

        File file = new File(filePath);
        String outputDirPath = destPath + "/" + outputDirType + "/" + file.getName();
        String tmpOutputDirPath = outputDirPath + tmpSuffix;
        try {
            ffmpegUtil.frameRetrieve(tmpOutputDirPath, screenSize, fps, file.getName());
            // 获取解码后的文件数量
            File tmpOutputDir = new File(tmpOutputDirPath);
            int fileCount = 0;
            if (tmpOutputDir.exists() && tmpOutputDir.isDirectory()) {
                fileCount = tmpOutputDir.listFiles().length;
            }
            if (fileCount == 0) {
                if (tmpOutputDir.exists()) {
                    try {
                        FileUtils.forceDelete(tmpOutputDir);
                    } catch (Exception e) {
                    }
                }
                return false;
            } else {
                // 重命名文件夹
                tmpOutputDir.renameTo(new File(outputDirPath));
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 提取音轨
     *
     * @param filename
     * @param destPath
     * @param tmpSuffix
     * @param ffMpegUtil
     * @return
     */
    private boolean extAudio(String filename, String destPath, String tmpSuffix, FFMpegUtil ffMpegUtil) {

        //读取配置文件
        int freq = 8000;
        int channels = 1;
        String outputDirType = "aodio";
        String tmpFileName = filename + tmpSuffix;
        String outputDirPath = destPath + "/" + outputDirType;
        String outputFilePath = outputDirPath + "/" + filename;
        File tmpFile = new File(outputDirPath + "/" + filename + tmpSuffix);
        try {
            // 视频音轨抽取
            ffMpegUtil.audioConvert(tmpFileName, outputDirPath, freq, channels);
            // 音轨
            if (tmpFile.exists()) {
                if (tmpFile.length() > 0) {
                    tmpFile.renameTo(new File(outputFilePath));
                    return true;
                } else {
                    try {
                        FileUtils.forceDelete(tmpFile);
                    } catch (Exception e) {
                    }
                }
                return false;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
