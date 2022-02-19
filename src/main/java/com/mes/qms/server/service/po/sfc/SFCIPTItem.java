package com.mes.qms.server.service.po.sfc;

import java.io.Serializable;

import com.mes.qms.server.service.po.ipt.IPTItem;

public class SFCIPTItem implements Serializable {
	private static final long serialVersionUID = 1L;

	public int ID = 0;

	public int ParentID = 0; // 检验表单ID

	public int ItemID = 0; // 参数ID

	public String ItemText = ""; // 参数文本

	public float ItemValue = 0.0f; // 参数值

	// 标准项(辅助属性)
	public IPTItem IPTItem = new IPTItem();

	public SFCIPTItem() {
		this.ID = 0;
		this.ParentID = 0;
		this.ItemID = 0;
		this.ItemText = "";
		this.ItemValue = 0.0f;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public IPTItem getIPTItem() {
		return IPTItem;
	}

	public void setIPTItem(IPTItem iPTItem) {
		IPTItem = iPTItem;
	}

	public int getParentID() {
		return ParentID;
	}

	public void setParentID(int parentID) {
		ParentID = parentID;
	}

	public int getItemID() {
		return ItemID;
	}

	public void setItemID(int itemID) {
		ItemID = itemID;
	}

	public String getItemText() {
		return ItemText;
	}

	public void setItemText(String itemText) {
		ItemText = itemText;
	}

	public float getItemValue() {
		return ItemValue;
	}

	public void setItemValue(float itemValue) {
		ItemValue = itemValue;
	}
}
