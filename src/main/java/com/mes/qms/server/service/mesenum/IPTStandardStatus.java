package com.mes.qms.server.service.mesenum;

public enum IPTStandardStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 待审批
	 */
	ToAudit(1, "待审批"),
	/**
	 * 已审批
	 */
	Audited(2, "已审批");

	private int value;
	private String lable;

	private IPTStandardStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IPTStandardStatus getEnumType(int val) {
		for (IPTStandardStatus type : IPTStandardStatus.values()) {
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
