package com.mes.qms.server.service.mesenum;

public enum SFCTaskMode {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * SCH
	 */
	BeforeShift(1, "班前"),
	/**
	 * APP
	 */
	AfterShift(2, "班后"),
	/**
	 * APP
	 */
	FreeShift(3, "主动"),
	/**
	 * APP
	 */
	PeriodShift(4, "周期");
	private int value;
	private String lable;

	private SFCTaskMode(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCTaskMode getEnumType(int val) {
		for (SFCTaskMode type : SFCTaskMode.values()) {
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
