package com.mes.qms.server.service.mesenum;

import com.mes.qms.server.service.mesenum.IPTPreCheckProblemStatus;

public enum IPTPreCheckProblemStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 待现场工艺发起评审
	 */
	ToCraftSendAudit(1, "待现场工艺发起评审"),
	/**
	 * 待相关人员评审
	 */
	ToRelaPersonAudit(2, "待相关人员评审"),
	/**
	 * 待现场工艺给解决方案
	 */
	ToCraftGiveSolve(3, "待现场工艺给解决方案"),
	/**
	 * 待提交审批
	 */
	ToSendItem(4, "待提交审批"),
	/**
	 * 审批中
	 */
	Auditing(5, "审批中"),
	/**
	 * 问题项已下发
	 */
	Issued(6, "问题项已下发"),
	/**
	 * 已自检，待互检
	 */
	ToMutual(7, "已自检，待互检"),
	/**
	 * 已互检，待专检
	 */
	ToSpecial(8, "已互检，待专检"),
	/**
	 * 已专检
	 */
	Done(9, "已专检"),
	/**
	 * 待确认
	 */
	ToConfirm(10, "待确认"),
	/**
	 * 开工
	 */
	Start(11, "开工");

	private int value;
	private String lable;

	IPTPreCheckProblemStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	public static IPTPreCheckProblemStatus getEnumType(int val) {
		byte b;
		int i;
		IPTPreCheckProblemStatus[] arrayOfIPTPreCheckProblemStatus;
		for (i = (arrayOfIPTPreCheckProblemStatus = values()).length, b = 0; b < i;) {
			IPTPreCheckProblemStatus type = arrayOfIPTPreCheckProblemStatus[b];
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
 * IPTPreCheckProblemStatus.class Java compiler version: 8 (52.0) JD-Core
 * Version: 1.1.2
 */