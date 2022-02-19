package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 转序工位任务的状态
 * 
 * @author ShrisJava
 *
 */
public enum RSMPartTaskStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 未转序
	 */
	NotTurnOrder(1, "未转序"),
	/**
	 * 主动转序中
	 */
	AcTurnOrdering(2, "主动转序中"),
	/**
	 * 主动转序完成
	 */
	AcTurnOrdered(3, "主动转序完成"),
	/**
	 * 自动转序中
	 */
	AutoTurnOrdering(4, "自动转序中"),
	/**
	 * 自动转序完成
	 */
	AutoTurnOrdered(5, "自动转序完成");

	private int value;
	private String lable;

	private RSMPartTaskStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static RSMPartTaskStatus getEnumType(int val) {
		for (RSMPartTaskStatus type : RSMPartTaskStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (RSMPartTaskStatus type : RSMPartTaskStatus.values()) {
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
