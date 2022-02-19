package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum IPTTableTypes {
	/**
	 * 默认
	 */
	Default(0, ""),
	/**
	 * 静态记录表(序号、检查项点、入场状态、厂家/编号、异常情况、备注)
	 */
	StaticRecord(1, "静态记录表"),
	/**
	 * 绝缘胶套表(序号、检查项点、图例、检查记录)
	 */
	InsulationRubberSleeve(2, "绝缘胶套表"),
	/**
	 * 关键部件表(序号、项点、厂家、型号、编号、入场状态)
	 */
	KeyComponent(3, "关键部件表"),
	/**
	 * 动态记录表(检查项点、合格标准、检查记录、故障原因分析与记录)
	 */
	DynamicRecord(4, "动态记录表"),
	/**
	 * 软件版本表(序号、检查项目、检查记录、自检、互检)
	 */
	SoftwardVerson(5, "软件版本表");

	private int value;
	private String lable;

	private IPTTableTypes(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IPTTableTypes getEnumType(int val) {
		for (IPTTableTypes type : IPTTableTypes.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return IPTTableTypes.Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (IPTTableTypes type : IPTTableTypes.values()) {
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
