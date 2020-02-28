package com.wang.ffmpegdecoder.ffmpeg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * CmdExecuter 命令执行器 封装对操作系统命令行发送指令相关操作
 */
public class CmdExecuter {
    /**
     * 执行指令
     *
     * @param cmd    执行指令
     * @param getter 指令返回处理接口，若为null则不处理输出
     * @throws Exception
     */
    static public void exec(List<String> cmd, IStringGetter getter) throws Exception {
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(cmd);
            builder.redirectErrorStream(true);
            Process proc = builder.start();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = stdout.readLine()) != null) {
                if (getter != null)
                    getter.dealString(line);
            }
            proc.waitFor();
            stdout.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}