package com.mes.qms.server.service.mesenum;

public enum IPTItemType {

	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 填写
	 */
	Write(1, "填写"),
	/**
	 * 预检入场状态
	 */
	InPlant(2, "预检入场状态"),
	/**
	 * 预检问题项
	 */
	ProblemItem(3, "预检问题项"),
	/**
	 * 组
	 */
	Group(4, "组");

	private int value;
	private String lable;

	private IPTItemType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IPTItemType getEnumType(int val) {
		for (IPTItemType type : IPTItemType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return IPTItemType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
