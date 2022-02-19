package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum APSUnitLevel {
	/**
	 * SCH
	 */
	Line(1, "产线"),
	/**
	 * APP
	 */
	Part(2, "工段"),
	/**
	 * APP
	 */
	Step(3, "工序"),
	/**
	 * APP
	 */
	Station(4, "工位");
	private int value;
	private String lable;

	private APSUnitLevel(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static APSUnitLevel getEnumType(int val) {
		for (APSUnitLevel type : APSUnitLevel.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (APSUnitLevel type : APSUnitLevel.values()) {
			CFGItem wItem=new CFGItem();
			wItem.ID=type.getValue();
			wItem.ItemName=type.getLable();
			wItem.ItemText=type.getLable();
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
