package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 转序控制枚举类型
 */
public enum FPCChangeControlType {
	/**
	 * 自检、互检
	 */
	Default(0, "自检、互检"),
	/**
	 * 自检、互检、专检
	 */
	SpecialTask(1, "自检、互检、专检"),
	/**
	 * 放行，后面做任务
	 */
	LetGo(2, "放行，后面做任务"),
	/**
	 * 不做任务
	 */
	NoDoTask(3, "不做任务");

	private int value;
	private String lable;

	private FPCChangeControlType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static FPCChangeControlType getEnumType(int val) {
		for (FPCChangeControlType type : FPCChangeControlType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (FPCChangeControlType type : FPCChangeControlType.values()) {
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
