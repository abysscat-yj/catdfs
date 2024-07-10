package com.abysscat.catdfs;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * file controller.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/10 23:28
 */
@RestController
public class FileController {

	@Value(value = "${catdfs.path}")
	private String uploadPath;

	@SneakyThrows
	@PostMapping("/upload")
	public String upload(@RequestParam("file") MultipartFile file) {
		File dir = new File(uploadPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filename = file.getOriginalFilename();
		File dest = new File(uploadPath + "/" + filename);
		file.transferTo(dest);
		return filename;
	}

}
