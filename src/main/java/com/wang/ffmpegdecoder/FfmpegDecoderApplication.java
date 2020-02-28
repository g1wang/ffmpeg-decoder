package com.wang.ffmpegdecoder;

import com.wang.ffmpegdecoder.engine.DecodeEngine;
import com.wang.ffmpegdecoder.engine.TaskEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class FfmpegDecoderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FfmpegDecoderApplication.class, args);
        int queueSize = 20;
        String videoPath = "";
        ExecutorService taskService = Executors.newSingleThreadExecutor();
        // 启动解码视频任务添加线程
        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(queueSize);
        taskService.execute(new TaskEngine(blockingQueue, videoPath));

        // 启动解码引擎
        ExecutorService decodeService = Executors.newSingleThreadExecutor();
        decodeService.execute(new DecodeEngine(blockingQueue));
    }

}
