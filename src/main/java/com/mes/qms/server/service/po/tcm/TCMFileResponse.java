package com.mes.qms.server.service.po.tcm;

import java.io.Serializable;

/**
 * 文件预览返回信息
 */
public class TCMFileResponse implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 返回处理的错误码，当statuscode不为200时，必须返回对应的错误信息 （返回码参照下表）
	 */
	private int code = 0;
	/**
	 * 文档唯一id，建议用文档sha1，预览服务通过这个id来判断预览文件是否已经生成。必须返回。
	 */
	private String uniqueId = "";
	/**
	 * 文件的名字，包含后缀，预览服务根据文档名的后缀来判断是否支持此文档的预览。必须返回。
	 */
	private String fname = "";
	/**
	 * 获取文档的方式，目前支持三种：localfile, localfilewait, download。必须返回。
	 */
	private String getFileWay = "";
	/**
	 * 当获取方式为localfile、download时，表示文档的本地路径或下载地址，根据getFileWay来判断是否必须
	 */
	private String url = "";
	/**
	 * 是否可复制，默认为true
	 */
	private boolean enableCopy = true;
	/**
	 * 是否有水印，0:无水印 1：字符串水印 2：图片水印
	 */
	private int watermarkType = 0;
	/**
	 * watermarkType为1：水印字符串 2：图片的url地址
	 */
	private String watermark = "";
	/**
	 * WatermarkSetting ; //json结构体，水印相关设置，可选
	 */
	private String watermarkSetting = "";
	/**
	 * 错误信息，可以给web显示的
	 */
	private String msg = "";
	/**
	 * 错误信息，一个json数据，如果是错误，预览将把错误信息全部返回给前端，便于定位对接模块错误范围
	 */
	private String detail = "";

	public TCMFileResponse() {
		super();
	}

	public TCMFileResponse(int code, String uniqueId, String fname, String getFileWay, String url, boolean enableCopy,
			int watermarkType, String watermark, String watermarkSetting, String msg, String detail) {
		super();
		this.code = code;
		this.uniqueId = uniqueId;
		this.fname = fname;
		this.getFileWay = getFileWay;
		this.url = url;
		this.enableCopy = enableCopy;
		this.watermarkType = watermarkType;
		this.watermark = watermark;
		this.watermarkSetting = watermarkSetting;
		this.msg = msg;
		this.detail = detail;
	}

	public int getCode() {
		return code;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public String getFname() {
		return fname;
	}

	public String getGetFileWay() {
		return getFileWay;
	}

	public String getUrl() {
		return url;
	}

	public int getWatermarkType() {
		return watermarkType;
	}

	public String getWatermark() {
		return watermark;
	}

	public String getWatermarkSetting() {
		return watermarkSetting;
	}

	public String getMsg() {
		return msg;
	}

	public String getDetail() {
		return detail;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public void setGetFileWay(String getFileWay) {
		this.getFileWay = getFileWay;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isEnableCopy() {
		return enableCopy;
	}

	public void setEnableCopy(boolean enableCopy) {
		this.enableCopy = enableCopy;
	}

	public void setWatermarkType(int watermarkType) {
		this.watermarkType = watermarkType;
	}

	public void setWatermark(String watermark) {
		this.watermark = watermark;
	}

	public void setWatermarkSetting(String watermarkSetting) {
		this.watermarkSetting = watermarkSetting;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}
