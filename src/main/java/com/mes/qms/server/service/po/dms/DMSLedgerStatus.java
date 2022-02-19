package com.mes.qms.server.service.po.dms;

public enum DMSLedgerStatus {
	/**
	 * 就绪
	 */
	Default(0, "就绪"),
	/**
	 * 使用
	 */
	Using(1, "使用"),
	/**
	 * 闲置
	 */
	Free(2, "闲置"),
	/**
	 * 维修
	 */
	Repair(3, "维修"),
	/**
	 * 保养
	 */
	Maintain(4, "保养"),
	/**
	 * 报废
	 */
	Scrap(5, "报废"),
	/**
	 * 封存
	 */
	SealUp(6, "封存");

	private int value;
	private String lable;

	private DMSLedgerStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static DMSLedgerStatus getEnumType(int val) {
		for (DMSLedgerStatus type : DMSLedgerStatus.values()) {
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