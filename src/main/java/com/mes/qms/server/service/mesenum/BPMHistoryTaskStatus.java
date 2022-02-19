package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum BPMHistoryTaskStatus {
	/**
	 * 待办
	 */
	ToDo(0, "待办"),
	/**
	 * 正常完成
	 */
	NomalFinished(1, "正常完成"),
	/**
	 * 取消
	 */
	Canceled(2, "取消");

	private int value;
	private String lable;

	private BPMHistoryTaskStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BPMHistoryTaskStatus getEnumType(int val) {
		for (BPMHistoryTaskStatus type : BPMHistoryTaskStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (BPMHistoryTaskStatus type : BPMHistoryTaskStatus.values()) {
			CFGItem wItem = new CFGItem();
			wItem.ID = type.getValue();
			wItem.ItemName = type.getLable();
			wItem.ItemText = type.getLable();
			wItemList.add(wItem);
		}
		return wItemList;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
