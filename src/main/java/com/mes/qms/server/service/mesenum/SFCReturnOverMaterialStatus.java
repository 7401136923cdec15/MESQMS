package com.mes.qms.server.service.mesenum;

public enum SFCReturnOverMaterialStatus {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * 已确认
	 */
	NomalClose(20, "已确认"),
	/**
	 * 异常关闭
	 */
	ExceptionClose(21, "已关闭"),
	/**
	 * 已撤销
	 */
	Canceled(22, "已撤销");

	private int value;
	private String lable;

	private SFCReturnOverMaterialStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCReturnOverMaterialStatus getEnumType(int val) {
		for (SFCReturnOverMaterialStatus type : SFCReturnOverMaterialStatus.values()) {
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
