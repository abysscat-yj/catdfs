package com.abysscat.catdfs.syncer;

import java.io.File;

/**
 * file syncer.
 *
 * @Author: abysscat-yj
 * @Create: 2024/7/11 2:33
 */
public interface FileSyncer {

	boolean sync(File file, String backupUrl);

}
