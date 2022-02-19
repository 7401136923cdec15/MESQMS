package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 打卡类型
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-21 13:31:33
 * @LastEditTime 2020-3-16 12:16:28
 *
 */
public enum SFCLoginType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 开工打卡
	 */
	StartWork(1, "开工打卡"),
	/**
	 * 完工打卡
	 */
	AfterWork(2, "完工打卡"),
	/**
	 * 暂停打卡
	 */
	StopWork(3, "暂停打卡");

	private int value;
	private String lable;

	private SFCLoginType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCLoginType getEnumType(int val) {
		for (SFCLoginType type : SFCLoginType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (SFCLoginType type : SFCLoginType.values()) {
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
