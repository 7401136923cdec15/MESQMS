package com.mes.qms.server.utils.qms;

import com.alibaba.fastjson.JSONObject;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.qms.MESFileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件下载工具类
 */
public class MESFileUtils {
	private static Logger logger = LoggerFactory.getLogger(MESFileUtils.class);

	@SuppressWarnings("unused")
	public static void DownloadPicture(String urlList, String wDesFile) {
		URL url = null;
		int imageNumber = 0;
		try {
			url = new URL(urlList);
			DataInputStream dataInputStream = new DataInputStream(url.openStream());

			FileOutputStream fileOutputStream = new FileOutputStream(new File(wDesFile));
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int length;

			while ((length = dataInputStream.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			byte[] context = output.toByteArray();
			fileOutputStream.write(output.toByteArray());
			dataInputStream.close();
			fileOutputStream.close();
		} catch (MalformedURLException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}

	/**
	 * 删除本地文件夹及包含文件
	 * 
	 * @param dir
	 */
	public static void deleteLocalDir(String dir) {
		File file = new File(dir);
		try {
			if (file.exists()) {
				// delete()方法不能删除非空文件夹，所以得用递归方式将file下所有包含内容删除掉，然后再删除file
				if (file.isDirectory()) {
					File[] files = file.listFiles();
					for (File f : files) {
						deleteLocalDir(f.getPath());
					}
				}
				file.delete();
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 压缩文件
	 *
	 * @param sourceFilePath 源文件路径
	 * @param zipFilePath    压缩后文件存储路径
	 * @param zipFilename    压缩文件名
	 */
	public static void compressToZip(String sourceFilePath, String zipFilePath, String zipFilename) {
		File sourceFile = new File(sourceFilePath);
		File zipPath = new File(zipFilePath);
		if (!zipPath.exists()) {
			zipPath.mkdirs();
		}
		File zipFile = new File(zipPath + File.separator + zipFilename);
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
			zos.setEncoding("gbk");
			zos.setLevel(1);
			writeZip(sourceFile, "", zos);
			// 文件压缩完成后，删除被压缩文件
			boolean flag = deleteDir(sourceFile);
			logger.info("删除被压缩文件[" + sourceFile + "]标志：{}", flag);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	/**
	 * 遍历所有文件，压缩
	 *
	 * @param file       源文件目录
	 * @param parentPath 压缩文件目录
	 * @param zos        文件流
	 */
	public static void writeZip(File file, String parentPath, ZipOutputStream zos) {
		if (file.isDirectory()) {
			// 目录
			parentPath += file.getName() + File.separator;
			File[] files = file.listFiles();
			for (File f : files) {
				writeZip(f, parentPath, zos);
			}
		} else {
			// 文件
			try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
				// 指定zip文件夹
				ZipEntry zipEntry = new ZipEntry(parentPath + file.getName());
				zos.putNextEntry(zipEntry);
				int len;
				byte[] buffer = new byte[1024 * 10];
				while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
					zos.write(buffer, 0, len);
					zos.flush();
				}
			} catch (Exception e) {
				logger.error(e.toString());
			}
		}
	}

	/**
	 * 删除文件夹
	 *
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 删除空文件夹
		return dir.delete();
	}

	// 复制文件(应用场景：复制的同时更改文件名称)
	public static void copyFile(String srcPath, String destPath) {
		try {
			if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(destPath)) {
				return;
			}

			File src = new File(srcPath);
			File des = new File(destPath);
			FileUtils.copyFile(src, des);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * post请求 带file,map是其余参数
	 */

	public static JSONObject sendPostWithFile(String wUrl, MultipartFile file, HashMap<String, Object> map) {
		DataOutputStream out = null;
		DataInputStream in = null;
		final String newLine = "\r\n";
		final String prefix = "--";
		JSONObject json = null;
		try {
			URL url = new URL(wUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			String BOUNDARY = "-------KingKe0520a";
			conn.setRequestMethod("POST");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			out = new DataOutputStream(conn.getOutputStream());

			// 添加参数file
			// File file = new File(filePath);
			StringBuilder sb1 = new StringBuilder();
			sb1.append(prefix);
			sb1.append(BOUNDARY);
			sb1.append(newLine);
			sb1.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"" + newLine);
			sb1.append("Content-Type:application/octet-stream");
			sb1.append(newLine);
			sb1.append(newLine);
			out.write(sb1.toString().getBytes());
			// in = new DataInputStream(new FileInputStream(file));
			in = new DataInputStream(file.getInputStream());
			byte[] bufferOut = new byte[1024];
			int bytes = 0;
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			out.write(newLine.getBytes());

			StringBuilder sb = new StringBuilder();
			int k = 1;
			for (String key : map.keySet()) {
				if (k != 1) {
					sb.append(newLine);
				}
				sb.append(prefix);
				sb.append(BOUNDARY);
				sb.append(newLine);
				sb.append("Content-Disposition: form-data;name=" + key + "");
				sb.append(newLine);
				sb.append(newLine);
				sb.append(map.get(key));
				out.write(sb.toString().getBytes());
				sb.delete(0, sb.length());
				k++;
			}

			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(end_data);
			out.flush();

			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			StringBuffer resultStr = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				resultStr.append(line);
			}
			json = (JSONObject) JSONObject.parse(resultStr.toString());

		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			logger.error(e.toString());
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		return json;
	}

	/**
	 * 连接ftp服务器
	 * 
	 * @param ip       ftp地址
	 * @param port     端口
	 * @param username 账号
	 * @param password 密码
	 * @return
	 * @throws IOException
	 */
	public static FTPClient ftpConnection(String ip, String port, String username, String password) throws IOException {
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(ip, Integer.parseInt(port));
			ftpClient.login(username, password);
			int replyCode = ftpClient.getReplyCode(); // 是否成功登录服务器
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				ftpClient.disconnect();
				logger.error("--ftp连接失败--");
				System.exit(1);
			}
			ftpClient.enterLocalPassiveMode();// 这句最好加告诉对面服务器开一个端口
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ftpClient;
	}

	/**
	 * 断开FTP服务器连接
	 * 
	 * @param ftpClient 初始化的对象
	 * @throws IOException
	 */
	public static void close(FTPClient ftpClient) throws IOException {
		if (ftpClient != null && ftpClient.isConnected()) {
			ftpClient.logout();
			ftpClient.disconnect();
		}
	}

	/**
	 * 下载ftp服务器文件方法
	 * 
	 * @param ftpClient   FTPClient对象
	 * @param newFileName 新文件名
	 * @param fileName    原文件（路径＋文件名）
	 * @param downUrl     下载路径
	 * @return
	 * @throws IOException
	 */
	public static boolean downFile(FTPClient ftpClient, String newFileName, String fileName, String downUrl)
			throws IOException {
		boolean isTrue = false;
		OutputStream os = null;
		File localFile = new File(downUrl + "/" + newFileName);
		if (!localFile.getParentFile().exists()) {// 文件夹目录不存在创建目录
			localFile.getParentFile().mkdirs();
			localFile.createNewFile();
		}
		os = new FileOutputStream(localFile);
		isTrue = ftpClient.retrieveFile(new String(fileName.getBytes(), "ISO-8859-1"), os);
		os.close();
		return isTrue;
	}

	/**
	 * 获取文件名
	 */
	public static String GetFileName(String wPic) {
		String wResult = "";
		try {
			if (StringUtils.isEmpty(wPic)) {
				return wResult;
			}

			int wFistrIndex = wPic.lastIndexOf("/");
			int wLastIndex = wPic.lastIndexOf(".");
			wResult = wPic.substring(wFistrIndex + 1, wLastIndex);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public static String GetSuffiex(String wPic) {
		String wResult = "";
		try {
			if (StringUtils.isEmpty(wPic)) {
				return wResult;
			}

			int wLastIndex = wPic.lastIndexOf(".");
			wResult = wPic.substring(wLastIndex + 1);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}