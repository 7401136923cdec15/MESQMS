package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum IMPSameType {
	/**
	 * ==相同
	 */
	Same(0, "相同"),
	/**
	 * 修改
	 */
	Update(1, "修改"),
	/**
	 * 删除
	 */
	Delete(2, "删除"),
	/**
	 * 新增
	 */
	Add(3, "新增"),
	/**
	 * 父项相同，子项不同
	 */
	SonSpe(4,"父项相同，子项不同");

	private int value;
	private String lable;

	private IMPSameType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IMPSameType getEnumType(int val) {
		for (IMPSameType type : IMPSameType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (IMPSameType type : IMPSameType.values()) {
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
