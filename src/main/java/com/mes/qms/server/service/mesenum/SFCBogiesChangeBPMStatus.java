package com.mes.qms.server.service.mesenum;

/**
 * 转向架互换
 */
public enum SFCBogiesChangeBPMStatus {
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

	private SFCBogiesChangeBPMStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCBogiesChangeBPMStatus getEnumType(int val) {
		for (SFCBogiesChangeBPMStatus type : SFCBogiesChangeBPMStatus.values()) {
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
