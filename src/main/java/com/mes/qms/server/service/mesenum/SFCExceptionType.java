package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 转向架互换异常类型
 */
public enum SFCExceptionType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 设备
	 */
	Device(1, "设备"),
	/**
	 * 物料
	 */
	Material(2, "物料"),
	/**
	 * 人员
	 */
	Person(3, "人员"),
	/**
	 * 工艺
	 */
	Craft(4, "工艺"),
	/**
	 * 环境
	 */
	Environment(5, "环境"),
	/**
	 * 检测
	 */
	Testring(6, "检测");

	private int value;
	private String lable;

	private SFCExceptionType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCExceptionType getEnumType(int val) {
		for (SFCExceptionType type : SFCExceptionType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (SFCExceptionType type : SFCExceptionType.values()) {
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
