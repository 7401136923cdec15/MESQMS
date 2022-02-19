package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum MBSRoleTree {
	/**
	 * 默认
	 */
	Default(0, ""),
	/**
	 * 申请转序
	 */
	SQZX(240001, "申请转序"),
	/**
	 * 问题项技术中心权限
	 */
	JSZX(502702, "问题项技术中心权限"),
	/**
	 * 问题项现场工艺权限
	 */
	XCGY(502701, "问题项现场工艺权限"),
	/**
	 * 问题项班组长权限
	 */
	Monitor(502703, "问题项班组长权限");

	private int value;
	private String lable;

	private MBSRoleTree(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MBSRoleTree getEnumType(int val) {
		for (MBSRoleTree type : MBSRoleTree.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (MBSRoleTree type : MBSRoleTree.values()) {
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
