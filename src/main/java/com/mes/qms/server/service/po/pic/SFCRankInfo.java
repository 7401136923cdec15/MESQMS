package com.mes.qms.server.service.po.pic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传的位次信息
 */
public class SFCRankInfo implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 车辆ID
	 */
	public int CarID = 0;
	/**
	 * 编号
	 */
	public int No = 0;
	/**
	 * 备注
	 */
	public String Remark = "";

	// 辅助属性
	/**
	 * 车型
	 */
	public String ProductNo = "";
	/**
	 * 车号
	 */
	public String CarNo = "";
	/**
	 * 部位信息集合
	 */
	public List<SFCPartInfo> SFCPartInfoList = new ArrayList<SFCPartInfo>();

	// 辅助睡醒
	/**
	 * 进度
	 */
	public String Progress = "";
	/**
	 * 总数
	 */
	public int TotalSize = 0;
	/**
	 * 完成数
	 */
	public int FinishSize = 0;

	public SFCRankInfo() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public int getNo() {
		return No;
	}

	public void setNo(int no) {
		No = no;
	}

	public int getCarID() {
		return CarID;
	}

	public void setCarID(int carID) {
		CarID = carID;
	}

	public String getProductNo() {
		return ProductNo;
	}

	public void setProductNo(String productNo) {
		ProductNo = productNo;
	}

	public String getCarNo() {
		return CarNo;
	}

	public void setCarNo(String carNo) {
		CarNo = carNo;
	}

	public List<SFCPartInfo> getSFCPartInfoList() {
		return SFCPartInfoList;
	}

	public void setSFCPartInfoList(List<SFCPartInfo> sFCPartInfoList) {
		SFCPartInfoList = sFCPartInfoList;
	}

	public String getProgress() {
		return Progress;
	}

	public void setProgress(String progress) {
		Progress = progress;
	}

	public int getTotalSize() {
		return TotalSize;
	}

	public void setTotalSize(int totalSize) {
		TotalSize = totalSize;
	}

	public int getFinishSize() {
		return FinishSize;
	}

	public void setFinishSize(int finishSize) {
		FinishSize = finishSize;
	}
}
