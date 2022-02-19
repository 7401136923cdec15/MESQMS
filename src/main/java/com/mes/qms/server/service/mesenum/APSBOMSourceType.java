package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 台车BOM来源类型
 */
public enum APSBOMSourceType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 预检问题项评审
	 */
	PreCheckProblem(1, "预检问题项评审"),
	/**
	 * 偶换件评审
	 */
	SFCBOMTask(2, "偶换件评审"),
	/**
	 * 原始标准BOM
	 */
	StandardBOM(3, "原始标准BOM"),
	/**
	 * 标准BOM变更
	 */
	BOMChange(4, "标准BOM变更");

	private int value;
	private String lable;

	private APSBOMSourceType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static APSBOMSourceType getEnumType(int val) {
		for (APSBOMSourceType type : APSBOMSourceType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (APSBOMSourceType type : APSBOMSourceType.values()) {
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
