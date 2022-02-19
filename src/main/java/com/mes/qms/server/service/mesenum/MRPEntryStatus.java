package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum MRPEntryStatus {

	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * BMS
	 */
	ToAudit(1, "待审核"),
	/**
	 * MSS
	 */
	Audited(2, "已审核"),
	/**
	 * MSS
	 */
	Revoke(3, "反审核");

	private int value;
	private String lable;

	private MRPEntryStatus(int value, String lable) {
	        			this.value = value;
	        			this.lable = lable;
	        		}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MRPEntryStatus getEnumType(int val) {
		for (MRPEntryStatus type : MRPEntryStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (MRPEntryStatus type : MRPEntryStatus.values()) {
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
