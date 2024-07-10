package com.abysscat.catdfs.controller;

import com.abysscat.catdfs.syncer.FileSyncer;
import com.abysscat.catdfs.syncer.HttpSyncer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.net.URLEncoder;
import java.util.UUID;

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

	@SneakyThrows
	@PostMapping("/upload")
	public String upload(@RequestParam MultipartFile file, HttpServletRequest request) {
		String originalFilename = file.getOriginalFilename();
		String fileName = request.getHeader(HttpSyncer.X_FILENAME_HEADER);

		// 区分是否是其他服务同步过来的文件
		boolean sync = false;
		if(fileName == null || fileName.isEmpty()) {
			// http头里没有fileName，即此时是用户上传的文件，需要同步到备份服务器
			fileName = UUID.randomUUID() + originalFilename;
			sync = true;
		}
		File path = new File(uploadPath);
        if(!path.exists()) path.mkdirs();

		System.out.println(file.getSize());
		System.out.println(file.getOriginalFilename());
		File dest = new File(uploadPath + "/" + fileName);
		file.transferTo(dest);
		if(sync) {
			syncer.sync(dest, backupUrl);
		}
		return fileName;
	}

	@RequestMapping("/download")
	public void download(String name, HttpServletResponse response) {
		try {
			String path = uploadPath + "/" + name;
			File file = new File(path);
			log.info(file.getPath());
			String filename = file.getName();
			// 将文件写入输入流
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStream fis = new BufferedInputStream(fileInputStream);
			byte[] buffer = new byte[16*1024];

			response.setCharacterEncoding("UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ URLEncoder.encode(filename, "UTF-8"));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");

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
