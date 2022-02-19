package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum IMPType {
	/**
	 * ==默认
	 */
	Default(0, "默认"),
	/**
	 * 工艺BOP
	 */
	BOP(1, "工艺BOP"),
	/**
	 * 标准BOM
	 */
	BOM(2, "标准BOM"),
	/**
	 * 检验标准
	 */
	Standard(3, "检验标准"),
	/**
	 * 物料
	 */
	Material(4, "物料");

	private int value;
	private String lable;

	private IMPType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IMPType getEnumType(int val) {
		for (IMPType type : IMPType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (IMPType type : IMPType.values()) {
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
