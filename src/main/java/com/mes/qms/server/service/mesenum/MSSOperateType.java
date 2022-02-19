package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 部件操作类型
 */
public enum MSSOperateType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 拆解
	 */
	ChaiJie(1, "拆解"),
	/**
	 * 维修
	 */
	WeiXiu(2, "维修"),
	/**
	 * 组装
	 */
	Zuzhuang(3, "组装");

	private int value;
	private String lable;

	private MSSOperateType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MSSOperateType getEnumType(int val) {
		for (MSSOperateType type : MSSOperateType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (MSSOperateType type : MSSOperateType.values()) {
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
