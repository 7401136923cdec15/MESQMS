package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.bpm.BPMTaskBase;

/**
 * 标准审批
 */
public class IPTStandardBPM extends BPMTaskBase implements Serializable {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 标准ID(逗号分隔)
	 */
	public String StandardID = "";

	// 辅助属性
	/**
	 * 标准
	 */
	public List<IPTStandard> IPTStandard = new ArrayList<IPTStandard>();
	/**
	 * 标准值
	 */
	public List<IPTItem> IPTItemList = new ArrayList<IPTItem>();
	public int TagTypes = 0;

	public String getStandardID() {
		return StandardID;
	}

	public void setStandardID(String standardID) {
		StandardID = standardID;
	}

	public List<IPTItem> getIPTItemList() {
		return IPTItemList;
	}

	public List<IPTStandard> getIPTStandard() {
		return IPTStandard;
	}

	public void setIPTStandard(List<IPTStandard> iPTStandard) {
		IPTStandard = iPTStandard;
	}

	public void setIPTItemList(List<IPTItem> iPTItemList) {
		IPTItemList = iPTItemList;
	}

	public int getTagTypes() {
		return TagTypes;
	}

	public void setTagTypes(int tagTypes) {
		TagTypes = tagTypes;
	}
}
