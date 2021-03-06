package com.mes.qms.server.utils.qms;

import com.mes.qms.server.utils.qms.ZipUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zip压缩工具类
 */
public class ZipUtils {
	private static Logger logger = LoggerFactory.getLogger(ZipUtils.class);

	/**
	 * 解压缩
	 * 
	 * @param warPath   包地址
	 * @param unzipPath 解压后地址
	 */
	public static void UnZip(String warPath, String unzipPath) {
		File warFile = new File(warPath);
		try {
			// 获得输出流
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(warFile));
			ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.JAR,
					bufferedInputStream);
			JarArchiveEntry entry = null;
			// 循环遍历解压
			while ((entry = (JarArchiveEntry) in.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					new File(unzipPath, entry.getName()).mkdir();
				} else {
					OutputStream out = FileUtils.openOutputStream(new File(unzipPath, entry.getName()));
					IOUtils.copy(in, out);
					out.close();
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			logger.error("未找到war文件");
		} catch (ArchiveException e) {
			logger.error("不支持的压缩格式");
		} catch (IOException e) {
			logger.error("文件写入发生错误");
		}
	}

	/**
	 * 压缩
	 * 
	 * @param destFile 创建的地址及名称
	 * @param zipDir   要打包的目录
	 */
	public static void Zip(String destFile, String zipDir) {
		File outFile = new File(destFile);
		try {
			outFile.createNewFile();
			// 创建文件
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outFile));
			ArchiveOutputStream out = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.JAR,
					bufferedOutputStream);
			if (zipDir.charAt(zipDir.length() - 1) != '/') {
				zipDir += '/';
			}

			zipDir = zipDir.replace("\\", "/");

			Iterator<File> files = FileUtils.iterateFiles(new File(zipDir), null, true);
			while (files.hasNext()) {
				File file = files.next();
//				ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file,
//						file.getPath().replace(zipDir.replace("/", "\\"), ""));

				String wFilePath = file.getPath();
				wFilePath = wFilePath.replace("\\", "/");

				String wRelaName = wFilePath.replace(zipDir, "");

				ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, wRelaName);

				out.putArchiveEntry(zipArchiveEntry);
				IOUtils.copy(new FileInputStream(file), out);
				out.closeArchiveEntry();
			}
			out.finish();
			out.close();
		} catch (IOException e) {
			logger.error("创建文件失败");
		} catch (ArchiveException e) {
			logger.error("不支持的压缩格式");
		}
	}

}