package com.abysscat.catdfs.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

/**
 * catdfs configuration.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/17 23:38
 */
public class CatdfsConfig {

	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setLocation("/private/tmp/tomcat");
		return factory.createMultipartConfig();
	}

}
