package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum BMSDepartmentType {
	/**
	 * ==默认
	 */
	Default(0, "默认"),
	/**
	 * 部门
	 */
	Department(1, "部门"),
	/**
	 * 工区
	 */
	Area(2, "工区"),
	/**
	 * 班组
	 */
	Class(3, "班组");

	private int value;
	private String lable;

	private BMSDepartmentType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BMSDepartmentType getEnumType(int val) {
		for (BMSDepartmentType type : BMSDepartmentType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (BMSDepartmentType type : BMSDepartmentType.values()) {
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
