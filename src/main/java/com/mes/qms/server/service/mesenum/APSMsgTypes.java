package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum APSMsgTypes {
	/**
	 * 默认
	 */
	Default(0, ""),
	/**
	 * 物料
	 */
	Material(1, "物料"),
	/**
	 * 工位负荷
	 */
	Station(2, "工位负荷"),
	/**
	 * 加工周期
	 */
	Work(3, "加工周期"),

	/**
	 * 进厂日期
	 */
	Receive(4, "进厂日期"),

	/**
	 * 工艺周期
	 */
	Tech(5, "工艺问题");

	private int value;
	private String lable;

	private APSMsgTypes(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static APSMsgTypes getEnumType(int val) {
		for (APSMsgTypes type : APSMsgTypes.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (APSMsgTypes type : APSMsgTypes.values()) {
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
