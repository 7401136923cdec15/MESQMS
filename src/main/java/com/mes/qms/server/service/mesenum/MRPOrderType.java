package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum MRPOrderType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * BMS
	 */
	SaleOrder(1, "销售订单"),
	/**
	 * MSS
	 */
	FactoryOrder(2, "工厂订单"),
	/**
	 * MSS
	 */
	MRPOrder(3, "MRP订单");
	
	private int value;
	private String lable;

	private MRPOrderType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MRPOrderType getEnumType(int val) {
		for (MRPOrderType type : MRPOrderType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (MRPOrderType type : MRPOrderType.values()) {
			CFGItem wItem=new CFGItem();
			wItem.ID=type.getValue();
			wItem.ItemName=type.getLable();
			wItem.ItemText=type.getLable();
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
