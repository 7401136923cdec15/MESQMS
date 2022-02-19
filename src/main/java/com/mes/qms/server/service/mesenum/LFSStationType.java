package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum LFSStationType {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * 整车
	 */
	WholeTrain(1, "整车"),
	/**
	 * 车体
	 */
	Body(2, "车体"),
	/**
	 * 转向架
	 */
	Bogies(3, "转向架");

	private int value;
	private String lable;

	private LFSStationType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static LFSStationType getEnumType(int val) {
		for (LFSStationType type : LFSStationType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (LFSStationType type : LFSStationType.values()) {
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
