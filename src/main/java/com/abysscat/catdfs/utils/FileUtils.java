package com.abysscat.catdfs.utils;

import com.abysscat.catdfs.model.FileMeta;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

/**
 * file utils.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/16 1:55
 */
public class FileUtils {

	/**
	 * 默认二进制流文件类型
	 */
	static String DEFAULT_MIME_TYPE = "application/octet-stream";

	static int SUB_DIRS_NUM = 256;

	/**
	 * 根据文件名获取文件 content-type 类型，使其可以直接被浏览器打开
	 */
	public static String getMimeType(String fileName) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String content = fileNameMap.getContentTypeFor(fileName);
		return content == null ? DEFAULT_MIME_TYPE : content;
	}

	/**
	 * 创建文件子目录
	 */
	public static void createSubDirs(String uploadPath) {
		File dir = new File(uploadPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		for (int i = 0; i < SUB_DIRS_NUM; i++) {
			// 目录名转成16进制
			String subDir = String.format("%02x", i);
			File file = new File(uploadPath + "/" + subDir);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	public static String getUUIDFile(String fileName) {
		return UUID.randomUUID() + getExt(fileName);
	}

	public static String getSubDir(String fileName) {
		return fileName.substring(0, 2);
	}

	public static String getExt(String originalFilename) {
		return originalFilename.substring(originalFilename.lastIndexOf("."));
	}

	@SneakyThrows
	public static void writeMeta(File metaFile, FileMeta meta) {
		String json = JSON.toJSONString(meta);
		Files.writeString(Paths.get(metaFile.getAbsolutePath()), json,
				StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}

}
