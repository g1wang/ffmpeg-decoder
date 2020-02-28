package com.wang.ffmpegdecoder.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHelper {
	private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);
	public final static Map<String, String> PIC_TYPE_MAP = new HashMap<String, String>();
	private final static Map<String, String> AUDIO_TYPE_MAP = new HashMap<String, String>();
	private final static Map<String, String> VIDEO_TYPE_MAP = new HashMap<String, String>();
	// 删除失败重试
	private static int retryTimes = 10;

	/**
	 * Discription:[getAllFileType,常见文件头信息]
	 */
	private static void getFileType() {
		// 图片
		PIC_TYPE_MAP.put("424d", "bmp"); // (bmp,dib)
		PIC_TYPE_MAP.put("ffd8ffe0", "jpg"); // JPEG (jpg,jpe,jfif)
		PIC_TYPE_MAP.put("89504e47", "png"); // PNG (png)
		PIC_TYPE_MAP.put("49492a00", "tif"); // TIFF (tif,tiff)
		PIC_TYPE_MAP.put("41433130", "dwg"); // CAD (dwg)
		PIC_TYPE_MAP.put("38425053", "psd"); // Adobe Photoshop (psd)

		// 音频
		AUDIO_TYPE_MAP.put("49443303", "mp3");
		AUDIO_TYPE_MAP.put("664c6143", "flac");
		AUDIO_TYPE_MAP.put("00000020", "m4a"); // m4a
		AUDIO_TYPE_MAP.put("52494646", "wav"); // Wave (wav)
		AUDIO_TYPE_MAP.put("4d414320", "ape"); // ape
		AUDIO_TYPE_MAP.put("4f676753", "ogg"); // Ogg
		AUDIO_TYPE_MAP.put("3026b275", "wma"); // wma

		AUDIO_TYPE_MAP.put("fffd8004", "mp2"); // mp2
		AUDIO_TYPE_MAP.put("2e736e64", "au"); // au
		AUDIO_TYPE_MAP.put("464f524d", "aiff"); // aiff
		AUDIO_TYPE_MAP.put("f07e0001", "sds"); // sds
		AUDIO_TYPE_MAP.put("fff15080", "aac"); // aac
		AUDIO_TYPE_MAP.put("2E7261FD", "ram"); // ram
		AUDIO_TYPE_MAP.put("2E7261FD", "mid"); // MIDI

		// 视频
		VIDEO_TYPE_MAP.put("00000020", "mp4"); // mp4
		VIDEO_TYPE_MAP.put("3026b275", "wmv"); // wmv与asf相同
		VIDEO_TYPE_MAP.put("52494646", "avi");
		VIDEO_TYPE_MAP.put("00000014", "3gp");
		VIDEO_TYPE_MAP.put("464c5601", "flv"); // flv与f4v相同
		VIDEO_TYPE_MAP.put("00000014", "mov"); // Quicktime (mov)
		VIDEO_TYPE_MAP.put("2e424f56", "bov"); // bov
		VIDEO_TYPE_MAP.put("2e524d46", "rmvb"); // rmvb/rm相同
		VIDEO_TYPE_MAP.put("000001b3", "mpg"); // 没有音频数据
		VIDEO_TYPE_MAP.put("000001ba", "mpg"); // 包含音频数据和视频数据
		VIDEO_TYPE_MAP.put("47494638", "gif"); // GIF (gif)

	}

	/**
	 * 得到上传文件的文件头
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取后缀名
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileSuffix(File file) {
		String filename = file.getName();
		int index = filename.lastIndexOf(".");
		if ((index > -1) && (index < (filename.length() - 1))) {
			return filename.substring(index + 1);
		}
		return "";
	}

	/**
	 * 扫描目录
	 * 
	 * @param dir
	 * @param list
	 */
	public static void scanDir(File dir, List<File> list,String suffixFilter) {
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String s) {
				return !s.endsWith(suffixFilter);
			}
		});
		for (File file : files) {
			if (file.isDirectory()) {
				scanDir(file, list,suffixFilter);
			}
			if (file.isFile()) {
				list.add(file);
			}
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void forceDel(File file) {
		forceDelRedo(file, retryTimes);
	}

	/**
	 * 文件删除,重试机制
	 * 
	 * @param file
	 * @param retryTimes
	 */
	private static void forceDelRedo(File file, int retryTimes) {
		if (retryTimes < 0) {
			logger.warn(String.format("[%s]文件删除失败!!", file.getPath()));
			return;
		}
		if (file.exists()) {
			try {
				FileUtils.forceDelete(file);
			} catch (IOException e) {
				logger.warn(String.format("[%s]文件删除失败,剩余重试次数[%d]!!", file.getPath(), retryTimes));
				forceDelRedo(file, --retryTimes);
			}
		}
	}

	/**
	 * 复制文件
	 * 
	 */
	public static void copyFileToDir(File srcFile, File destDir) {
		copyFileToDirectoryRedo(srcFile, destDir, retryTimes);
	}

	/**
	 * 文件删除,重试机制
	 * 
	 * @param retryTimes
	 */
	private static void copyFileToDirectoryRedo(File srcFile, File destDir, int retryTimes) {
		if (retryTimes < 0) {
			logger.warn(String.format("[%s]文件复制失败!!", srcFile.getPath()));
			return;
		}
		if (srcFile.exists()) {
			try {
				FileUtils.copyFileToDirectory(srcFile, destDir);
			} catch (IOException e) {
				logger.warn(String.format("[%s]文件复制失败,剩余重试次数[%d]!!", srcFile.getPath(), retryTimes));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
				copyFileToDirectoryRedo(srcFile, destDir, --retryTimes);
			}
		}
	}

	/**
	 * 复制文件
	 * 
	 */
	public static void copyFile(File srcFile, File destFile) {
		copyFileRedo(srcFile, destFile, retryTimes);
	}

	/**
	 * 文件删除,重试机制
	 * 
	 * @param retryTimes
	 */
	private static void copyFileRedo(File srcFile, File destFile, int retryTimes) {
		if (retryTimes < 0) {
			logger.warn(String.format("[%s]文件复制失败!!", srcFile.getPath()));
			return;
		}
		if (srcFile.exists()) {
			try {
				FileUtils.copyFile(srcFile, destFile);
			} catch (IOException e) {
				logger.warn(String.format("[%s]文件复制失败,剩余重试次数[%d]!!", srcFile.getPath(), retryTimes));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
				copyFileRedo(srcFile, destFile, --retryTimes);
			}
		}
	}

	/**
	 * 获取相对路径
	 * 
	 * @param rootPath
	 * @param targetPath
	 * @return
	 */
	public String getRelativePath(String rootPath, String targetPath) {
		String relativePath = "";
		rootPath = rootPath.replace("\\", "/");
		targetPath = targetPath.replace("\\", "/");
		if (targetPath.startsWith(rootPath)) {
			relativePath = targetPath.substring(rootPath.length());
		}
		return relativePath;

	}

}
