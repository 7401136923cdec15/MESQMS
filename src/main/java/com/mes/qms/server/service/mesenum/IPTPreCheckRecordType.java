package com.mes.qms.server.service.mesenum;

import com.mes.qms.server.service.mesenum.IPTPreCheckRecordType;

public enum IPTPreCheckRecordType {
	Default(

			0, "默认"),
	PeriodChangeItem(

			1, "段改要求项目"),
	ExceptionAll(

			2, "异常信息汇总"),
	KeyComponents(

			3, "关键部件入场检查"),
	ControlRecord(

			4, "预检控制记录"),
	SelfCheck(

			5, "自检"),
	MutualCheck(

			6, "互检"),
	SpecialCheck(

			7, "专检");

	private int value;
	private String lable;

	IPTPreCheckRecordType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	public static IPTPreCheckRecordType getEnumType(int val) {
		byte b;
		int i;
		IPTPreCheckRecordType[] arrayOfIPTPreCheckRecordType;
		for (i = (arrayOfIPTPreCheckRecordType = values()).length, b = 0; b < i;) {
			IPTPreCheckRecordType type = arrayOfIPTPreCheckRecordType[b];
			if (type.getValue() == val)
				return type;
			b++;
		}

		return Default;
	}

	public int getValue() {
		return this.value;
	}

	public String getLable() {
		return this.lable;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\mesenum\
 * IPTPreCheckRecordType.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */