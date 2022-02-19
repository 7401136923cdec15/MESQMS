package com.mes.qms.server.service.po.pic;

import java.io.Serializable;

/**
 * 上传的图片信息
 */
public class SFCUploadPic implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 部位ID
	 */
	public int PartID = 0;
	/**
	 * 图片路径
	 */
	public String PicUrl = "";
	/**
	 * 编号
	 */
	public int No = 0;
	/**
	 * 备注
	 */
	public String Remark = "";
	/**
	 * 图片描述
	 */
	public String Describe = "";

	public SFCUploadPic() {
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getPartID() {
		return PartID;
	}

	public void setPartID(int partID) {
		PartID = partID;
	}

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	public int getNo() {
		return No;
	}

	public void setNo(int no) {
		No = no;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getDescribe() {
		return Describe;
	}

	public void setDescribe(String describe) {
		Describe = describe;
	}
}
