package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum MSSSpotStatus {
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
	Default(0, "默认"),
	/**
	 * SCH
	 */
	Active(1, "未开始"),
	
	ToDo(2, "待完成"),
	/**
	 * APP
	 */
	Done(4, "已完成");
	
	private int value;
	private String lable;

	private MSSSpotStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MSSSpotStatus getEnumType(int val) {
		for (MSSSpotStatus type : MSSSpotStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (MSSSpotStatus type : MSSSpotStatus.values()) {
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
