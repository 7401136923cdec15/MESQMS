package com.mes.qms.server.service.mesenum;

/**
 * 转向架互换
 */
public enum TCMTechChangeNoticeStatus {
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
	ExceptionClose(21, "已撤销"),
	/**
	 * 已驳回
	 */
	Canceled(22, "已驳回");

	private int value;
	private String lable;

	private TCMTechChangeNoticeStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static TCMTechChangeNoticeStatus getEnumType(int val) {
		for (TCMTechChangeNoticeStatus type : TCMTechChangeNoticeStatus.values()) {
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
