package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum APSInstallCheckMode {
	/**
	 * ==默认
	 */
	Default(0, "默认"),
	/**
	 * ==检查并提示
	 */
	OnlyCheck(1, "检查并提示"),
	/**
	 * ==检查并影响本工位转序
	 */
	CheckChange(2, "检查并影响本工位转序"),
	/**
	 * ==检查并影响上工位转序
	 */
	CheckChangePrev(3, "检查并影响上工位转序"),
	/**
	 * APP
	 */
	Uncheck(4, "不检查冲突");
	
	private int value;
	private String lable;

	private APSInstallCheckMode(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static APSInstallCheckMode getEnumType(int val) {
		for (APSInstallCheckMode type : APSInstallCheckMode.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (APSInstallCheckMode type : APSInstallCheckMode.values()) {
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
