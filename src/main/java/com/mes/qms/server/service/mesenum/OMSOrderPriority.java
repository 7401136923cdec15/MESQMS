package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum OMSOrderPriority {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 进车时刻
	 */
	RealReceiveDate(1, "进车时刻"),
	/**
	 * 修程
	 */
	Line(2, "修程"),
	/**
	 * 预计完工时刻
	 */
	PlanFinishDate(3, "预计完工时刻"),
	/**
	 * 局段
	 */
	BureauSection(4, "局段"),
	/**
	 * 车型
	 */
	ProductNo(5, "车型");

	private int value;
	private String lable;

	private OMSOrderPriority(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static OMSOrderPriority getEnumType(int val) {
		for (OMSOrderPriority type : OMSOrderPriority.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (OMSOrderPriority type : OMSOrderPriority.values()) {
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
