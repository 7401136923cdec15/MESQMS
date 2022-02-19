package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum MRPMergeMode {

	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * BMS
	 */
	Supplier(1, "供应商"),
	/**
	 * MSS
	 */
	Stock(2, "仓库"),
	/**
	 * RSS
	 */
	Time(3, "采购日期");
	
	private int value;
	private String lable;

	private MRPMergeMode(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MRPMergeMode getEnumType(int val) {
		for (MRPMergeMode type : MRPMergeMode.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (MRPMergeMode type : MRPMergeMode.values()) {
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
