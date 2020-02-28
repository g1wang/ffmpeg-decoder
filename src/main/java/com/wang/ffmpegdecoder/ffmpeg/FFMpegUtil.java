package com.wang.ffmpegdecoder.ffmpeg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FFMpegUntil FFMpeg工具类 封装FFMpeg对视频的操作
 */
public class FFMpegUtil implements IStringGetter {

    private int runtime = 0;

    // ffmpeg地址
    private String ffmpegUri;
    // 视频源文件地址
    private String originFileUri;

    private enum FFMpegUtilStatus {
        Empty, CheckingFile, GettingRuntime
    }

    private FFMpegUtilStatus status = FFMpegUtilStatus.Empty;
    private List<String> cmd = new ArrayList<String>();
    private boolean isSupported;// 文件是否是支持的格式

    /**
     * 创建目录
     *
     * @param destDirName 创建目录名称
     */
    private static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            // System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        // 创建目录
        if (dir.mkdirs()) {
            // System.out.println("创建目录" + destDirName + "成功！");
            return true;
        } else {
            // System.out.println("创建目录" + destDirName + "失败！");
            return false;
        }
    }

    /**
     * 构造函数
     *
     * @param ffmpegUri     ffmpeg的全路径 如e:/ffmpeg/ffmpeg.exe 或 /root/ffmpeg/bin/ffmpeg
     * @param originFileUri 所操作视频文件的全路径 如e:/upload/temp/test.wmv
     */
    public FFMpegUtil(String ffmpegUri, String originFileUri) {
        this.ffmpegUri = ffmpegUri;
        this.originFileUri = originFileUri;
    }

    /**
     * 获取视频时长
     *
     * @return
     * @throws Exception
     */
    public int getRuntime() throws Exception {
        runtime = 0;
        status = FFMpegUtilStatus.GettingRuntime;
        cmd.clear();
        cmd.add(ffmpegUri);
        cmd.add("-i");
        cmd.add(originFileUri);
        CmdExecuter.exec(cmd, this);
        return runtime;
    }

    /**
     * 检测文件是否是支持的格式 将检测视频文件本身，而不是扩展名
     *
     * @return
     * @throws Exception
     */
    public boolean isSupported() throws Exception {
        isSupported = true;
        status = FFMpegUtilStatus.CheckingFile;
        cmd.clear();
        cmd.add(ffmpegUri);
        cmd.add("-i");
        cmd.add(originFileUri);
        CmdExecuter.exec(cmd, this);
        return isSupported;
    }

    /**
     * 生成视频截图
     *
     * @param imageSavePath 截图文件保存全路径
     * @param timeLocation  截图秒长时轴位置
     * @throws Exception
     */
    public void makeScreenCut(String imageSavePath, int timeLocation) throws Exception {
        cmd.clear();
        cmd.add(ffmpegUri);
        cmd.add("-i");
        cmd.add(originFileUri);
        cmd.add("-y");
        cmd.add("-f");
        cmd.add("image2");
        cmd.add("-ss");
        cmd.add(Integer.toString(timeLocation));
        cmd.add("-t");
        cmd.add("0.001");
        // cmd.add("-s");
        // cmd.add(screenSize);
        cmd.add(imageSavePath);
        CmdExecuter.exec(cmd, null);
    }

    /**
     * 视频转换
     *
     * @param fileSavePath    文件保存全路径（包括扩展名）如 e:/abc/test.flv
     * @param screenSize      视频分辨率 如640x480
     * @param audioByte       音频比特率
     * @param audioCollection 音频采样率
     * @param quality         视频质量(0.01-255)越低质量越好
     * @param fps             每秒帧数（15或29.97）
     * @throws Exception
     */
    public void videoTransfer(String fileSavePath, String screenSize, int audioByte, int audioCollection, double quality,
                              double fps) throws Exception {
        cmd.clear();
        cmd.add(ffmpegUri);
        cmd.add("-i");
        cmd.add(originFileUri);
        cmd.add("-y");
        cmd.add("-ab");
        cmd.add(Integer.toString(audioByte));
        cmd.add("-ar");
        cmd.add(Integer.toString(audioCollection));
        cmd.add("-qscale");
        cmd.add(Double.toString(quality));
        cmd.add("-r");
        cmd.add(Double.toString(fps));
        cmd.add("-s");
        cmd.add(screenSize);
        cmd.add(fileSavePath);
        CmdExecuter.exec(cmd, null);
    }

    /**
     * 视频抽帧
     *
     * @param outputDir  截图保存路径
     * @param screenSize 截图分辨率 如640x480
     * @param fps        帧率,也就是每秒截取图片的数量
     * @throws Exception
     */
    public void frameRetrieve(String outputDir, String screenSize, double fps, String filename) throws Exception {
        cmd.clear();
        cmd.add(ffmpegUri);
        cmd.add("-i");
        cmd.add(originFileUri);
        cmd.add("-r");
        cmd.add(Double.toString(fps));
        cmd.add("-s");
        cmd.add(screenSize);
        cmd.add("-f");
        cmd.add("image2");
        String outputImg = outputDir + "/" + filename + "_frame%09d.jpg";
        createDir(outputDir);
        cmd.add(outputImg);
        CmdExecuter.exec(cmd, null);
    }

    /**
     * 抽取并转换视频中音频为指定格式
     *
     * @param fileSavePath 音频保存路径
     * @param freq         音频采样率
     * @param channels     通道
     * @throws Exception
     */
    public void audioConvert(String filename, String fileSavePath, int freq, int channels) throws Exception {
        cmd.clear();
        cmd.add(ffmpegUri);
        cmd.add("-i");
        cmd.add(originFileUri);
        cmd.add("-ar");
        cmd.add(Integer.toString(freq));
        cmd.add("-ac");
        cmd.add(Integer.toString(channels));
        cmd.add("-f");
        cmd.add("wav");
        createDir(fileSavePath);
        String outputStr = fileSavePath + "//" + filename;
        cmd.add(outputStr);
        CmdExecuter.exec(cmd, null);
    }

    /**
     * gif图片转换为jpg
     *
     * @param fileSavePath jpg保存路径
     * @throws Exception
     */
    public void gifConvert2Jpg(String gifOriPath, String fileSavePath, String screenSize, double fps) throws Exception {
        cmd.clear();
        cmd.add(ffmpegUri);
        cmd.add("-i");
        cmd.add(originFileUri);
        cmd.add("-r");
        cmd.add(Double.toString(fps));
        cmd.add("-s");
        cmd.add(screenSize);
        File vFile = new File(originFileUri);
        // gif临时目录
        String outputDir = gifOriPath + "//" + vFile.getName();
        createDir(outputDir);
        String outputImg = outputDir + "//" + vFile.getName() + "_frame%09d.jpg";
        cmd.add(outputImg);
        CmdExecuter.exec(cmd, null);
        File outputDirFile = new File(outputDir);

    }


    @Override
    public void dealString(String str) {
        switch (status) {
            case Empty:
                break;
            case CheckingFile: {
                Matcher m = Pattern.compile("Invalid data found when processing input").matcher(str);
                if (m.find()) this.isSupported = false;
                break;
            }
            case GettingRuntime: {
                Matcher m = Pattern.compile("Duration: \\d+:\\d+:\\d+").matcher(str);
                while (m.find()) {
                    String msg = m.group();
                    msg = msg.replace("Duration: ", "");
                    String[] timeStr = msg.split(":");
                    long time = Integer.parseInt(timeStr[0]) * 3600;
                    time += Integer.parseInt(timeStr[1]) * 60;
                    time += Integer.parseInt(timeStr[2]);
                    runtime = new Long(time).intValue();
                }
                break;
            }
        }
    }

    /**
     * ts格式的文件进行加时间轴操作
     *
     * @throws Exception
     */
    public void tsCopy(File destFile) throws Exception {
        cmd.clear();
        cmd.add(ffmpegUri);
        cmd.add("-i");
        cmd.add(originFileUri);
        cmd.add("-c");
        cmd.add("copy");
        cmd.add(destFile.getAbsolutePath());
        CmdExecuter.exec(cmd, null);
    }

}
