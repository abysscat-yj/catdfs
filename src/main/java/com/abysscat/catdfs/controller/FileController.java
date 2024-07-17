package com.abysscat.catdfs.controller;

import com.abysscat.catdfs.model.FileMeta;
import com.abysscat.catdfs.syncer.FileSyncer;
import com.abysscat.catdfs.syncer.HttpSyncer;
import com.abysscat.catdfs.utils.FileUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * file controller.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/10 23:28
 */
@RestController
@Slf4j
public class FileController {

	@Value("${catdfs.path}")
	private String uploadPath;

	@Value("${catdfs.backupUrl:null}")
	private String backupUrl;

	@Autowired
	FileSyncer syncer;

	@Value("${catdfs.autoMd5}")
	private boolean autoMd5;

	@SneakyThrows
	@PostMapping("/upload")
	public String upload(@RequestParam MultipartFile file, HttpServletRequest request) {
		String originalFileName = file.getOriginalFilename();
		String uuidFileName = FileUtils.getUUIDFile(originalFileName);

		// 获取当前文件待上传的目录名
		String subDir = FileUtils.getSubDir(uuidFileName);
		File dest = new File(uploadPath + "/" + subDir + "/" + uuidFileName);
		file.transferTo(dest);

		// 存放meta信息
		FileMeta meta = new FileMeta();
		meta.setName(uuidFileName);
		meta.setOriginalFilename(originalFileName);
		meta.setSize(file.getSize());
		if(autoMd5) {
			meta.getTags().put("md5", DigestUtils.md5DigestAsHex(new FileInputStream(dest)));
		}
		String metaName = uuidFileName + ".meta";
		File metaFile = new File(uploadPath + "/" + subDir + "/" + metaName);
		FileUtils.writeMeta(metaFile, meta);

		// 同步到备份服务器
		syncer.sync(dest, backupUrl, originalFileName);

		return uuidFileName;
	}

	@SneakyThrows
	@PostMapping("/sync")
	public String sync(@RequestParam MultipartFile file, HttpServletRequest request) {
		String uuidFileName = file.getOriginalFilename();
		// 从请求头获取原始文件名
		String originFileName = request.getHeader(HttpSyncer.X_ORIGIN_FILENAME_HEADER);

		if (originFileName == null || originFileName.isEmpty()) {
			throw new RuntimeException("origin file name is empty");
		}

		// 获取当前文件待上传的目录名
		String subDir = FileUtils.getSubDir(uuidFileName);
		File dest = new File(uploadPath + "/" + subDir + "/" + uuidFileName);
		file.transferTo(dest);

		// 存放meta信息
		FileMeta meta = new FileMeta();
		meta.setName(uuidFileName);
		meta.setOriginalFilename(originFileName);
		meta.setSize(file.getSize());
		if(autoMd5) {
			meta.getTags().put("md5", DigestUtils.md5DigestAsHex(new FileInputStream(dest)));
		}
		String metaName = uuidFileName + ".meta";
		File metaFile = new File(uploadPath + "/" + subDir + "/" + metaName);
		FileUtils.writeMeta(metaFile, meta);

		return uuidFileName;
	}

	@RequestMapping("/download")
	public void download(String name, HttpServletResponse response) {
		try {
			String subDir = FileUtils.getSubDir(name);
			String path = uploadPath + "/" + subDir + "/" + name;
			File file = new File(path);

			log.info(file.getPath());
			String filename = file.getName();

			// 将文件写入输入流
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStream fis = new BufferedInputStream(fileInputStream);
			byte[] buffer = new byte[16*1024];

			response.setCharacterEncoding("UTF-8");
//			response.addHeader("Content-Disposition", "attachment;filename="
//					+ URLEncoder.encode(filename, "UTF-8"));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
			response.setContentType(FileUtils.getMimeType(name));

			while (fis.read(buffer) > 0){
				outputStream.write(buffer);
			}
			fis.close();
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
