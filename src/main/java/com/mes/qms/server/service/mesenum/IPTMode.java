package com.mes.qms.server.service.mesenum;

public enum IPTMode {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 工艺
	 */
	TechXJ(2, "工艺"),
	/**
	 * 计量
	 */
	JLXJ(3, "计量"),
	/**
	 * 报工
	 */
	Report(4, "报工"),
	/**
	 * 来料检验
	 */
	Material(5, "来料检验"),
	// -----------------------广机特有---------------------------
	/**
	 * 过程
	 */
	QTXJ(1, "过程"),
	/**
	 * 预检
	 */
	PreCheck(9, "预检"),
	/**
	 * 质量
	 */
	Quality(10, "质量"),
	/**
	 * 段改
	 */
	PeriodChange(11, "段改"),
	/**
	 * 普查
	 */
	OutCheck(12, "普查");

	private int value;
	private String lable;

	private IPTMode(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IPTMode getEnumType(int val) {
		for (IPTMode type : IPTMode.values()) {
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