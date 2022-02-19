package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.ipt.IPTProblemBomItem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.oms.OMSOrder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IPTValue implements Serializable {
	private static final long serialVersionUID = 1L;
	public long ID;
	public long StandardID;
	public long IPTItemID;
	public String Value;
	public String Remark;
	public int Result;
	public int TaskID = 0;
	public int IPTMode = 0;

	public int ItemType = 0;
	public List<String> ImagePath = new ArrayList<>();
	public List<String> VideoPath = new ArrayList<>();
	public int SolveID = 0;
	public int SubmitID = 0;
	public String Submitor = "";
	public Calendar SubmitTime = Calendar.getInstance();

	public String Manufactor = "";

	public String Modal = "";

	public String Number = "";

	public int Status = 0;

	public List<IPTProblemBomItem> IPTProblemBomItemList = new ArrayList<>();

	public int OrderID = 0;
	public OMSOrder OMSOrder = new OMSOrder();

	/**
	 * 拆解部件
	 */
	public String DisassemblyComponents = "";
	/**
	 * 维修部件
	 */
	public String RepairParts = "";
	/**
	 * 组装部件
	 */
	public String AssemblyParts = "";

	// 自修件2021-7-1 14:05:29
	/**
	 * 拆解自修件
	 */
	public String SRDisassemblyComponents = "";
	/**
	 * 维修自修件
	 */
	public String SRRepairParts = "";
	/**
	 * 组装自修件
	 */
	public String SRAssemblyParts = "";
	/**
	 * 报废自修件
	 */
	public String SRScrapParts = "";
	/**
	 * 领用自修件
	 */
	public String SRLYParts = "";

	/**
	 * 物料ID
	 */
	public int MaterialID = 0;
	/**
	 * 物料编码
	 */
	public String MaterialNo = "";
	/**
	 * 物料名称
	 */
	public String MaterialName = "";

	public IPTValue() {
		this.Value = "";
		this.Result = 1;
		this.Remark = "";
	}

	public long getID() {
		return this.ID;
	}

	public void setID(long iD) {
		this.ID = iD;
	}

	public int getMaterialID() {
		return MaterialID;
	}

	public String getMaterialNo() {
		return MaterialNo;
	}

	public String getMaterialName() {
		return MaterialName;
	}

	public void setMaterialID(int materialID) {
		MaterialID = materialID;
	}

	public void setMaterialNo(String materialNo) {
		MaterialNo = materialNo;
	}

	public void setMaterialName(String materialName) {
		MaterialName = materialName;
	}

	public int getStatus() {
		return this.Status;
	}

	public String getSRDisassemblyComponents() {
		return SRDisassemblyComponents;
	}

	public String getSRRepairParts() {
		return SRRepairParts;
	}

	public String getSRAssemblyParts() {
		return SRAssemblyParts;
	}

	public String getSRScrapParts() {
		return SRScrapParts;
	}

	public String getSRLYParts() {
		return SRLYParts;
	}

	public void setSRDisassemblyComponents(String sRDisassemblyComponents) {
		SRDisassemblyComponents = sRDisassemblyComponents;
	}

	public void setSRRepairParts(String sRRepairParts) {
		SRRepairParts = sRRepairParts;
	}

	public void setSRAssemblyParts(String sRAssemblyParts) {
		SRAssemblyParts = sRAssemblyParts;
	}

	public void setSRScrapParts(String sRScrapParts) {
		SRScrapParts = sRScrapParts;
	}

	public void setSRLYParts(String sRLYParts) {
		SRLYParts = sRLYParts;
	}

	public void setStatus(int status) {
		this.Status = status;
	}

	public OMSOrder getOMSOrder() {
		return OMSOrder;
	}

	public String getDisassemblyComponents() {
		return DisassemblyComponents;
	}

	public String getRepairParts() {
		return RepairParts;
	}

	public String getAssemblyParts() {
		return AssemblyParts;
	}

	public void setDisassemblyComponents(String disassemblyComponents) {
		DisassemblyComponents = disassemblyComponents;
	}

	public void setRepairParts(String repairParts) {
		RepairParts = repairParts;
	}

	public void setAssemblyParts(String assemblyParts) {
		AssemblyParts = assemblyParts;
	}

	public void setOMSOrder(OMSOrder oMSOrder) {
		OMSOrder = oMSOrder;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getTaskID() {
		return this.TaskID;
	}

	public String getManufactor() {
		return this.Manufactor;
	}

	public void setManufactor(String manufactor) {
		this.Manufactor = manufactor;
	}

	public String getModal() {
		return this.Modal;
	}

	public void setModal(String modal) {
		this.Modal = modal;
	}

	public String getNumber() {
		return this.Number;
	}

	public void setNumber(String number) {
		this.Number = number;
	}

	public void setTaskID(int taskID) {
		this.TaskID = taskID;
	}

	public int getIPTMode() {
		return this.IPTMode;
	}

	public void setIPTMode(int iPTMode) {
		this.IPTMode = iPTMode;
	}

	public long getStandardID() {
		return this.StandardID;
	}

	public void setStandardID(long standardID) {
		this.StandardID = standardID;
	}

	public long getIPTItemID() {
		return this.IPTItemID;
	}

	public void setIPTItemID(long iPTItemID) {
		this.IPTItemID = iPTItemID;
	}

	public String getValue() {
		return this.Value;
	}

	public void setValue(String value) {
		this.Value = value;
	}

	public String getRemark() {
		return this.Remark;
	}

	public void setRemark(String remark) {
		this.Remark = remark;
	}

	public int getResult() {
		return this.Result;
	}

	public void setResult(int result) {
		this.Result = result;
	}

	public int getItemType() {
		return this.ItemType;
	}

	public void setItemType(int itemType) {
		this.ItemType = itemType;
	}

	public List<String> getImagePath() {
		return this.ImagePath;
	}

	public void setImagePath(List<String> imagePath) {
		this.ImagePath = imagePath;
	}

	public List<String> getVideoPath() {
		return this.VideoPath;
	}

	public void setVideoPath(List<String> videoPath) {
		this.VideoPath = videoPath;
	}

	public int getSolveID() {
		return this.SolveID;
	}

	public void setSolveID(int solveID) {
		this.SolveID = solveID;
	}

	public int getSubmitID() {
		return this.SubmitID;
	}

	public void setSubmitID(int submitID) {
		this.SubmitID = submitID;
	}

	public String getSubmitor() {
		return this.Submitor;
	}

	public void setSubmitor(String submitor) {
		this.Submitor = submitor;
	}

	public Calendar getSubmitTime() {
		return this.SubmitTime;
	}

	public void setSubmitTime(Calendar submitTime) {
		this.SubmitTime = submitTime;
	}

	public List<IPTProblemBomItem> getIPTProblemBomItemList() {
		return this.IPTProblemBomItemList;
	}

	public void setIPTProblemBomItemList(List<IPTProblemBomItem> iPTProblemBomItemList) {
		this.IPTProblemBomItemList = iPTProblemBomItemList;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\ipt\IPTValue
 * .class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */