package com.mes.qms.server.service.mesenum;

public enum BMSRange {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * SCH
	 */
	Factory(1, "工厂"),
	/**
	 * APP
	 */
	Business(2, "事业部"),
	/**
	 * APP
	 */
	Workshop(3, "车间"),
	/**
	 * APP
	 */
	Line(4, "产线");
	private int value;
	private String lable;

	private BMSRange(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BMSRange getEnumType(int val) {
		for (BMSRange type : BMSRange.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
