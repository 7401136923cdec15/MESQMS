package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum BPMFunctionModule {
	/**
	 *  [EnumMember]
        Default = 0,        //默认值
        [EnumMember]
        Production = 1,     //生产
        [EnumMember]
        Quality = 2,        //质量
        [EnumMember]
        Technology = 3,     //工艺
        [EnumMember]
        Equipment = 4,      //设备
        [EnumMember]
        Warehouse = 5,      //仓库
	 */
	Default(0, "默认"),
	/**
	 * SCH
	 */
	Production(1, "生产"),
	/**
	 * APP
	 */
	Quality(2, "质量"),
	/**
	 * APP
	 */
	Technology(3, "工艺"),
	/**
	 * APP
	 */
	Equipment(4, "设备"),
	/**
	 * APP
	 */
	Warehouse(5, "仓库");
	private int value;
	private String lable;

	private BPMFunctionModule(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BPMFunctionModule getEnumType(int val) {
		for (BPMFunctionModule type : BPMFunctionModule.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}
	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();
		
		for (BPMFunctionModule type : BPMFunctionModule.values()) {
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
