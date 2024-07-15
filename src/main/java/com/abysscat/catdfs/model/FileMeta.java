package com.abysscat.catdfs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * file meta.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/16 1:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMeta {

	private String name;

	private String originalFilename;

	private long size;

	private Map<String, String> tags = new HashMap<>();

}
