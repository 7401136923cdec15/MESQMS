package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 预检需填字段枚举
 * 
 * @author ShrisJava
 *
 */
public enum IPTOtherValue {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 厂家
	 */
	Manufactor(1, "厂家"),
	/**
	 * 厂家、型号
	 */
	ManufactorModal(2, "厂家、型号"),
	/**
	 * 厂家、编号
	 */
	ManufactorNumber(3, "厂家、编号");

	private int value;
	private String lable;

	private IPTOtherValue(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IPTOtherValue getEnumType(int val) {
		for (IPTOtherValue type : IPTOtherValue.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return IPTOtherValue.Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (IPTOtherValue type : IPTOtherValue.values()) {
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
