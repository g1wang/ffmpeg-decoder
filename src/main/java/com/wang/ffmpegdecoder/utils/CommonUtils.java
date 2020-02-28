package com.wang.ffmpegdecoder.utils;

import java.io.File;

/**
 * 通用方法类
 *
 * @author Guilin Wang
 */
public class CommonUtils {

    /**
     * @param minSize
     * @param path
     * @return
     */
    public static boolean isSpaceFree(long minSize, String path) {
        return new File(path).getFreeSpace() < minSize;
    }

}
