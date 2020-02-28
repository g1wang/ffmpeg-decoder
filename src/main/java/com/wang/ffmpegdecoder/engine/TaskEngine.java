package com.wang.ffmpegdecoder.engine;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 任务引擎:获取任务,分发任务
 */
public class TaskEngine implements Runnable {

    private ArrayBlockingQueue<String> blockingDeque;
    private String path;

    public TaskEngine(ArrayBlockingQueue<String> blockingDeque, String path) {
        this.blockingDeque = blockingDeque;
        this.path = path;
    }

    @Override
    public void run() {

    }
}
