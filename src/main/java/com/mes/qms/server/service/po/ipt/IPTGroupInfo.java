package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.ipt.IPTGroupInfo;
import java.io.Serializable;

/**
 * 组信息，用于导入标准
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-6-29 12:50:31
 * @LastEditTime 2020-6-29 12:50:34
 *
 */
public class IPTGroupInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 工序
	 */
	public String StepName = "";
	/**
	 * 级别
	 */
	public int Level = 0;
	/**
	 * 一级项点
	 */
	public String OneItem = "";
	/**
	 * 二级项点
	 */
	public String TwoItem = "";
	/**
	 * 三级项点
	 */
	public String ThreeItem = "";
	/**
	 * 四级项点
	 */
	public String FourItem = "";
	/**
	 * 组ID
	 */
	public int GroupID = 0;

	public IPTGroupInfo() {
		super();
	}

	public IPTGroupInfo(String stepName, int level, String oneItem, String twoItem, String threeItem, String fourItem,
			int groupID) {
		super();
		StepName = stepName;
		Level = level;
		OneItem = oneItem;
		TwoItem = twoItem;
		ThreeItem = threeItem;
		FourItem = fourItem;
		GroupID = groupID;
	}

	public String getStepName() {
		return StepName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}

	public int getLevel() {
		return Level;
	}

	public void setLevel(int level) {
		Level = level;
	}

	public String getOneItem() {
		return OneItem;
	}

	public void setOneItem(String oneItem) {
		OneItem = oneItem;
	}

	public String getTwoItem() {
		return TwoItem;
	}

	public void setTwoItem(String twoItem) {
		TwoItem = twoItem;
	}

	public String getThreeItem() {
		return ThreeItem;
	}

	public void setThreeItem(String threeItem) {
		ThreeItem = threeItem;
	}

	public String getFourItem() {
		return FourItem;
	}

	public void setFourItem(String fourItem) {
		FourItem = fourItem;
	}

	public int getGroupID() {
		return GroupID;
	}

	public void setGroupID(int groupID) {
		GroupID = groupID;
	}
}
