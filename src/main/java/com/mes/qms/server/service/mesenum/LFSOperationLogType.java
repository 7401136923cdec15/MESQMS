package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 系统操作日志类型
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-11-16 13:43:47
 *
 */
public enum LFSOperationLogType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 修改转序控制
	 */
	UpdateTurnOrderControl(1, "修改转序控制"),
	/**
	 * 设置工序拍照
	 */
	SetStepPic(2, "设置工序拍照"),
	/**
	 * 专检控制
	 */
	SpecialControl(3, "修改专检控制");

	private int value;
	private String lable;

	private LFSOperationLogType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static LFSOperationLogType getEnumType(int val) {
		for (LFSOperationLogType type : LFSOperationLogType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (LFSOperationLogType type : LFSOperationLogType.values()) {
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
