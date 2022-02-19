package com.mes.qms.server.service.po.pic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传的部位信息
 */
public class SFCPartInfo implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 位次ID
	 */
	public int RankID = 0;
	/**
	 * 备注信息
	 */
	public String Remark = "";
	/**
	 * 编号
	 */
	public int No = 0;
	/**
	 * 图片信息
	 */
	public List<SFCUploadPic> SFCUploadPicList = new ArrayList<SFCUploadPic>();
	/**
	 * 状态 1保存 2提交
	 */
	public int Status = 0;
	// 辅助属性
	public String Progress = "";
	/**
	 * 总数
	 */
	public int TotalSize = 0;
	/**
	 * 完成数
	 */
	public int FinishSize = 0;

	public SFCPartInfo() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getRankID() {
		return RankID;
	}

	public void setRankID(int rankID) {
		RankID = rankID;
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

	public List<SFCUploadPic> getSFCUploadPicList() {
		return SFCUploadPicList;
	}

	public void setSFCUploadPicList(List<SFCUploadPic> sFCUploadPicList) {
		SFCUploadPicList = sFCUploadPicList;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
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
