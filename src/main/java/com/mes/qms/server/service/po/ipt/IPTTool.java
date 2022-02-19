package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.ipt.IPTTool;
import java.io.Serializable;
import java.util.Calendar;

/**
 * 工装、工具
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-1-5 14:10:08
 * @LastEditTime 2021-1-5 14:10:11
 *
 */
public class IPTTool implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 标准ID
	 */
	public int StandardID = 0;
	/**
	 * 序号
	 */
	public int OrderNum = 0;
	/**
	 * 名称
	 */
	public String Name = "";
	/**
	 * 规格型号
	 */
	public String Modal = "";
	/**
	 * 数量
	 */
	public int Number = 0;
	/**
	 * 单位ID
	 */
	public int UnitID = 0;
	/**
	 * 单位名称
	 */
	public String UnitText = "";
	/**
	 * 创建人
	 */
	public int CreateID = 0;
	/**
	 * 创建人名称
	 */
	public String Creator = "";
	/**
	 * 创建时间
	 */
	public Calendar CreateTime = Calendar.getInstance();
	/**
	 * 编辑人
	 */
	public int EditID = 0;
	/**
	 * 编辑人名称
	 */
	public String Editor = "";
	/**
	 * 编辑时间
	 */
	public Calendar EditTime = Calendar.getInstance();

	public IPTTool() {
		super();
	}

	public int getOrderNum() {
		return OrderNum;
	}

	public String getName() {
		return Name;
	}

	public String getModal() {
		return Modal;
	}

	public int getNumber() {
		return Number;
	}

	public int getUnitID() {
		return UnitID;
	}

	public String getUnitText() {
		return UnitText;
	}

	public void setOrderNum(int orderNum) {
		OrderNum = orderNum;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setModal(String modal) {
		Modal = modal;
	}

	public void setNumber(int number) {
		Number = number;
	}

	public void setUnitID(int unitID) {
		UnitID = unitID;
	}

	public void setUnitText(String unitText) {
		UnitText = unitText;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getStandardID() {
		return StandardID;
	}

	public void setStandardID(int standardID) {
		StandardID = standardID;
	}

	public int getCreateID() {
		return CreateID;
	}

	public String getCreator() {
		return Creator;
	}

	public Calendar getCreateTime() {
		return CreateTime;
	}

	public int getEditID() {
		return EditID;
	}

	public String getEditor() {
		return Editor;
	}

	public Calendar getEditTime() {
		return EditTime;
	}

	public void setCreateID(int createID) {
		CreateID = createID;
	}

	public void setCreator(String creator) {
		Creator = creator;
	}

	public void setCreateTime(Calendar createTime) {
		CreateTime = createTime;
	}

	public void setEditID(int editID) {
		EditID = editID;
	}

	public void setEditor(String editor) {
		Editor = editor;
	}

	public void setEditTime(Calendar editTime) {
		EditTime = editTime;
	}
}
