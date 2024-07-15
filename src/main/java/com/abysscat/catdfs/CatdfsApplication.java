package com.abysscat.catdfs;

import com.abysscat.catdfs.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CatdfsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatdfsApplication.class, args);
	}

	@Value("${catdfs.path}")
	private String uploadPath;

	@Bean
	ApplicationRunner runner() {
		return args -> {
			FileUtils.createSubDirs(uploadPath);
			System.out.println("catdfs started.");
		};
	}

}
