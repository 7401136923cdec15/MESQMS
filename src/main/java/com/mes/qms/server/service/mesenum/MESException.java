package com.mes.qms.server.service.mesenum;

public enum MESException {
	/**
	 * SCH
	 */
	Default(0, ""),
	/**
	 * SCH
	 */
	Logic(1, "逻辑错误"),
	/**
	 * APP
	 */
	DBInstance(2, "数据库错误"),
	/**
	 * APP
	 */
	DBSQL(3, "SQL语法错误"),
	/**
	 * APP
	 */
	Parameter(4, "参数错误"),
	/**
	 * APP
	 */
	Exception(5, "系统异常"),
	/**
	 * APP
	 */
	UnPower(6, "无授权"),/**
	 * APP
	 */
	File(7, "文件异常");
	private int value;
	private String lable;

	private MESException(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static MESException getEnumType(int val) {
		for (MESException type : MESException.values()) {
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
