package com.mes.qms.server.service.mesenum;

public enum SFCTaskStatus {
	/**
	 * SCH
	 */
	Default(0, "默认"),
	/**
	 * SCH
	 */
	Active(1, "未完成"),
	/**
	 * APP
	 */
	Done(2, "完成"),
	/**
	 * APP
	 */
	OutTime(3, "超时"),
	/**
	 * APP
	 */
	Closed(4, "关闭");

	private int value;
	private String lable;

	private SFCTaskStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCTaskStatus getEnumType(int val) {
		for (SFCTaskStatus type : SFCTaskStatus.values()) {
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
