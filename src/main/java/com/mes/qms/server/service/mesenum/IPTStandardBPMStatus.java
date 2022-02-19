package com.mes.qms.server.service.mesenum;

public enum IPTStandardBPMStatus {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * 待审批
	 */
	ToAudit(1, "待审批"),
	/**
	 * 已确认
	 */
	NomalClose(20, "已确认"),
	/**
	 * 已驳回
	 */
	ExceptionClose(22, "已驳回"),
	/**
	 * 已撤销
	 */
	Canceled(21, "已撤销");

	private int value;
	private String lable;

	private IPTStandardBPMStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IPTStandardBPMStatus getEnumType(int val) {
		for (IPTStandardBPMStatus type : IPTStandardBPMStatus.values()) {
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
