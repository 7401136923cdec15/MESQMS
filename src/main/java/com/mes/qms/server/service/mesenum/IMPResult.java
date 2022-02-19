package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum IMPResult {
	/**
	 * ==默认
	 */
	Default(0, "默认"),
	/**
	 * 成功
	 */
	Success(1, "成功"),
	/**
	 * 失败
	 */
	Failed(2, "失败"),
	/**
	 * 导入中
	 */
	Doding(3, "导入中");

	private int value;
	private String lable;

	private IMPResult(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IMPResult getEnumType(int val) {
		for (IMPResult type : IMPResult.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (IMPResult type : IMPResult.values()) {
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
