package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 转向架互换异常类型
 */
public enum SFCResponseLevel {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * A
	 */
	A(1, "A"),
	/**
	 * B
	 */
	B(2, "B"),
	/**
	 * C
	 */
	C(3, "C"),
	/**
	 * D
	 */
	D(4, "D");

	private int value;
	private String lable;

	private SFCResponseLevel(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCResponseLevel getEnumType(int val) {
		for (SFCResponseLevel type : SFCResponseLevel.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (SFCResponseLevel type : SFCResponseLevel.values()) {
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
