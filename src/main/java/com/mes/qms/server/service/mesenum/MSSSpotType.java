package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum MSSSpotType {
	/**
	 *  [EnumMember]
        Default = 0,
        [EnumMember]
        RawMaterial = 1,     //原材料
        [EnumMember]
        HalfProduct = 2,     //半成品
        [EnumMember]
        EndProduct = 3,      //成品
	 */
	Default(0, "其他"),
	/**
	 * SCH
	 */
	Material(1, "物料"),
	/**
	 * APP
	 */
	Device(6, "设备");
	
	private int value;
	private String lable;

	private MSSSpotType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MSSSpotType getEnumType(int val) {
		for (MSSSpotType type : MSSSpotType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (MSSSpotType type : MSSSpotType.values()) {
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
