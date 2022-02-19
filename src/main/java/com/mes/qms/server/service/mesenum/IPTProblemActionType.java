package com.mes.qms.server.service.mesenum;

import com.mes.qms.server.service.mesenum.IPTProblemActionType;

public enum IPTProblemActionType {
	Default(

			0, "默认"),
	CraftSendAudit(

			1, "现场工艺发起评审"),
	CraftGiveSOP(

			2, "现场工艺给解决方案"),
	ManagerConfirm(

			3, "负责人确认");

	private int value;
	private String lable;

	IPTProblemActionType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	public static IPTProblemActionType getEnumType(int val) {
		byte b;
		int i;
		IPTProblemActionType[] arrayOfIPTProblemActionType;
		for (i = (arrayOfIPTProblemActionType = values()).length, b = 0; b < i;) {
			IPTProblemActionType type = arrayOfIPTProblemActionType[b];
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
 * IPTProblemActionType.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */