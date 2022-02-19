package com.mes.qms.server.service.mesenum;

/**
 * 转向架互换
 */
public enum TCMReworkStatus {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * 已确认
	 */
	NomalClose(20, "已确认"),
	/**
	 * 已撤销
	 */
	Canceled(21, "已撤销"),
	/**
	 * 已驳回
	 */
	ExceptionClose(22, "已驳回");

	private int value;
	private String lable;

	private TCMReworkStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static TCMReworkStatus getEnumType(int val) {
		for (TCMReworkStatus type : TCMReworkStatus.values()) {
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
