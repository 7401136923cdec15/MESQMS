package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum MRPProduceMode {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * BMS
	 */
	Sequence(1, "顺序生产"),
	/**
	 * MSS
	 */
	Parallel(2, "并行生产");
	
	private int value;
	private String lable;

	private MRPProduceMode(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MRPProduceMode getEnumType(int val) {
		for (MRPProduceMode type : MRPProduceMode.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (MRPProduceMode type : MRPProduceMode.values()) {
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
