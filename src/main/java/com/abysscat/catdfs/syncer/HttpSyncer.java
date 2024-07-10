package com.abysscat.catdfs.syncer;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * http file syncer.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/11 2:34
 */
@Component
public class HttpSyncer implements FileSyncer{

	public final static String X_FILENAME_HEADER = "X-Filename";

	@Override
	public boolean sync(File file, String backupUrl) {

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set(X_FILENAME_HEADER, file.getName());
		MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new FileSystemResource(file));

		HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(builder.build(), headers);
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(backupUrl, httpEntity, String.class);
		System.out.println("sync result = " + responseEntity.getBody());

		return true;
	}


}
