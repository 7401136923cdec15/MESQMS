package com.mes.qms.server.service.mesenum;

public enum IPTPreCheckReportStatus {
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
	 * 异常关闭
	 */
	ExceptionClose(21, "已关闭"),
	/**
	 * 已撤销
	 */
	Canceled(22, "已撤销"),
	/**
	 * 驳回
	 */
	Rejected(23, "驳回");

	private int value;
	private String lable;

	private IPTPreCheckReportStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IPTPreCheckReportStatus getEnumType(int val) {
		for (IPTPreCheckReportStatus type : IPTPreCheckReportStatus.values()) {
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
