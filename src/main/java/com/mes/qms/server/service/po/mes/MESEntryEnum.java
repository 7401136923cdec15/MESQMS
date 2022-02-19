package com.mes.qms.server.service.po.mes;

public enum MESEntryEnum {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * BMS
	 */
	BMSModel(1, "BMS"),
	/**
	 * MSS
	 */
	MSSModel(2, "MSS"),
	/**
	 * RSS
	 */
	Region(3, "RSS"),
	/**
	 * 默认
	 */
	BPMModel(4, "BPM"),
	/**
	 * FMC
	 */
	FactoryModel(5, "FMC"),
	/**
	 * FPC
	 */
	RouteModel(6, "FPC"),
	/**
	 * CRM
	 */
	CRMModel(7, "CRM"),
	/**
	 * SCH
	 */
	SCHModel(8, "SCH"),
	/**
	 * APP
	 */
	APPMessage(9, "APP");
	private int value;
	private String lable;

	private MESEntryEnum(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MESEntryEnum getEnumType(int val) {
		for (MESEntryEnum type : MESEntryEnum.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
