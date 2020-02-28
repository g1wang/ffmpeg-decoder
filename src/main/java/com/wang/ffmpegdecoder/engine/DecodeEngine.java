package com.wang.ffmpegdecoder.engine;

import com.wang.ffmpegdecoder.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DecodeEngine implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(DecodeEngine.class);

    private ArrayBlockingQueue<String> blockingQueue;
    private long freeSpace = 5;
    //解码出的文件位置
    private String destPath = "";
    private int framesMaxTaskSize = 100;

    public DecodeEngine(ArrayBlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {

        while (true) {
            // 当空间不足时休眠
            while (!CommonUtils.isSpaceFree(freeSpace, destPath)) {
                try {
                    Thread.sleep(10 * 1000l);
                } catch (InterruptedException e) {
                }
            }
            //
            File destDir = new File(destPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File[] framesDirs = destDir.listFiles();
            if (framesDirs != null && framesDirs.length > framesMaxTaskSize) {
                try {
                    Thread.sleep((long) (Math.random() * 5000));
                } catch (InterruptedException e) {
                }
                continue;
            }
            String filePath = "";
            String sufName = "";
            try {
                filePath = blockingQueue.poll(500, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.error("decode 获取队列元素异常", e);
            }
            if (!StringUtils.isNotBlank(filePath)) {
                continue;
            }
            //任务分发

        }


    }
}
