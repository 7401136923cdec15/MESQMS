package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 预检项申请单
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-12 13:41:28
 * @LastEditTime 2020-2-12 13:41:33
 *
 */
public class IPTItemApply implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	// 预检项
	/**
	 * 标准ID
	 */
	public int StandardID = 0;
	/**
	 * 预检项名称/标准项简述(检查项点)
	 */
	public String ItemName = "";
	/**
	 * 预检过程
	 */
	public String Process = "";
	/**
	 * 标准描述
	 */
	public String Standard = "";
	/**
	 * 标准项备注
	 */
	public String Remark = "";
	// 问题描述

	// 辅助属性
	/**
	 * 问题描述详情
	 */
	public String Details = "";
	/**
	 * 问题描述图片列表
	 */
	public List<String> ImageList = new ArrayList<String>();
	/**
	 * 问题描述视频列表
	 */
	public List<String> VideoList = new ArrayList<String>();
	/**
	 * 项类型
	 */
	public int ItemType = 0;

	public IPTItemApply() {
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getStandard() {
		return Standard;
	}

	public void setStandard(String standard) {
		Standard = standard;
	}

	public String getProcess() {
		return Process;
	}

	public void setProcess(String process) {
		Process = process;
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public String getDetails() {
		return Details;
	}

	public void setDetails(String details) {
		Details = details;
	}

	public List<String> getImageList() {
		return ImageList;
	}

	public void setImageList(List<String> imageList) {
		ImageList = imageList;
	}

	public List<String> getVideoList() {
		return VideoList;
	}

	public void setVideoList(List<String> videoList) {
		VideoList = videoList;
	}

	public int getItemType() {
		return ItemType;
	}

	public void setItemType(int itemType) {
		ItemType = itemType;
	}

	public int getStandardID() {
		return StandardID;
	}

	public void setStandardID(int standardID) {
		StandardID = standardID;
	}
}
