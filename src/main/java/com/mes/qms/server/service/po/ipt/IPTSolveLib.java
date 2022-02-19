package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 预检问题解决方案知识库
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-12 15:41:58
 * @LastEditTime 2020-2-12 15:42:02
 *
 */
public class IPTSolveLib implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键ID
	 */
	public int ID = 0;
	/**
	 * 预检标准项ID
	 */
	public int IPTItemID = 0;
	/**
	 * 预检标准项名称(预检标准项中获取)
	 */
	public String IPTItemName = "";
	/**
	 * 问题简述，必填10字以内
	 */
	public String Description = "";
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
	 * 车型
	 */
	public int ProductID = 0;
	public String ProductNo = "";
	/**
	 * 修程
	 */
	public int LineID = 0;
	public String LineName = "";
	/**
	 * 局段
	 */
	public int CustomID = 0;
	public String CustomName = "";
	/**
	 * 解决方案列表
	 */
	public List<IPTSOP> IPTSOPList = new ArrayList<IPTSOP>();
	/**
	 * 解决方案完整描述
	 */
	public String FullDescribe = "";
	/**
	 * 创建时间
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 创建人
	 */
	public int CreateID = 0;
	public String Creator = "";
	/**
	 * 修改时间
	 */
	public Calendar EditTime = Calendar.getInstance();
	/**
	 * 修改人
	 */
	public int EditID = 0;
	public String Editor = "";

	public IPTSolveLib() {
		CreateTime.set(2000, 1, 1);
		EditTime.set(2000, 1, 1);
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getIPTItemID() {
		return IPTItemID;
	}

	public void setIPTItemID(int iPTItemID) {
		IPTItemID = iPTItemID;
	}

	public String getIPTItemName() {
		return IPTItemName;
	}

	public void setIPTItemName(String iPTItemName) {
		IPTItemName = iPTItemName;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
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

	public int getProductID() {
		return ProductID;
	}

	public void setProductID(int productID) {
		ProductID = productID;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public int getLineID() {
		return LineID;
	}

	public void setLineID(int lineID) {
		LineID = lineID;
	}

	public String getLineName() {
		return LineName;
	}

	public void setLineName(String lineName) {
		LineName = lineName;
	}

	public int getCustomID() {
		return CustomID;
	}

	public void setCustomID(int customID) {
		CustomID = customID;
	}

	public String getCustomName() {
		return CustomName;
	}

	public void setCustomName(String customName) {
		CustomName = customName;
	}

	public List<IPTSOP> getIPTSOPList() {
		return IPTSOPList;
	}

	public void setIPTSOPList(List<IPTSOP> iPTSOPList) {
		IPTSOPList = iPTSOPList;
	}

	public String getFullDescribe() {
		return FullDescribe;
	}

	public void setFullDescribe(String fullDescribe) {
		FullDescribe = fullDescribe;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getCreateID() {
		return CreateID;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getEditID() {
		return EditID;
	}

	public void setEditID(int editID) {
		EditID = editID;
	}

	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}
}
