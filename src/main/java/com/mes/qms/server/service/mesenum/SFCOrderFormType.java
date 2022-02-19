package com.mes.qms.server.service.mesenum;

public enum SFCOrderFormType {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * 竣工确认单
	 */
	CompleteConfirm(1, "竣工确认单"),
	/**
	 * 出厂申请单
	 */
	OutApply(2, "出厂申请单"),
	/**
	 * 进厂确认单
	 */
	InConfirm(3, "进厂确认单");

	private int value;
	private String lable;

	private SFCOrderFormType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCOrderFormType getEnumType(int val) {
		for (SFCOrderFormType type : SFCOrderFormType.values()) {
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
