package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 台车日志
 * 
 * @author ZhangYi
 * @CreateTime 2019年12月27日12:49:23
 * @LastEditTime 2019年12月27日12:49:28
 *
 */
public class APSPartsLog implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 序号
	 */
	public int ID = 0;

	/**
	 * 车型
	 */
	public int ProductID = 0;

	/**
	 * 车型编码
	 */
	public String ProductNo = "";

	/**
	 * 车号
	 */
	public String PartNo = "";

	/**
	 * 到场时间
	 */
	public Calendar ArrivedTime = Calendar.getInstance();

	/**
	 * 离开时间
	 */
	public Calendar DepartureTime = Calendar.getInstance();

	/**
	 * 创建人
	 */
	public int CreatorID = 0;

	public String Creator = "";

	/**
	 * 创建时间
	 */
	public Calendar CreateTime = Calendar.getInstance();

	/**
	 * 编辑人
	 */
	public int EditorID = 0;

	public String Editor = "";

	/**
	 * 编辑时间
	 */
	public Calendar EditTime = Calendar.getInstance();

	/**
	 * 状态
	 */
	public int Status = 0;
	
	public APSPartsLog() {
		ArrivedTime.set(2000, 1, 1);
		DepartureTime.set(2000, 1, 1);
		CreateTime.set(2000, 1, 1);
		EditTime.set(2000, 1, 1);
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
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

	public String getPartNo() {
		return PartNo;
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public Calendar getArrivedTime() {
		return ArrivedTime;
	}

	public void setArrivedTime(Calendar arrivedTime) {
		ArrivedTime = arrivedTime;
	}

	public Calendar getDepartureTime() {
		return DepartureTime;
	}

	public void setDepartureTime(Calendar departureTime) {
		DepartureTime = departureTime;
	}

	public int getCreatorID() {
		return CreatorID;
	}

	public void setCreatorID(int creatorID) {
		CreatorID = creatorID;
	}

	public String getCreator() {
		return Creator;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public int getEditorID() {
		return EditorID;
	}

	public void setEditorID(int editorID) {
		EditorID = editorID;
	}

	public String getEditor() {
		return Editor;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}
}
