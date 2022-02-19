package com.mes.qms.server.service.mesenum;

import com.mes.qms.server.service.mesenum.IPTOrderReportPartType;
import com.mes.qms.server.service.po.cfg.CFGItem;
import java.util.ArrayList;
import java.util.List;

public enum IPTOrderReportPartType {
	Default(

			0, "默认"),
	Process(

			1, "过程"),
	PreCheck(

			2, "预检"),
	Quality(

			3, "质量");

	private int value;
	private String lable;

	IPTOrderReportPartType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	public static IPTOrderReportPartType getEnumType(int val) {
		byte b;
		int i;
		IPTOrderReportPartType[] arrayOfIPTOrderReportPartType;
		for (i = (arrayOfIPTOrderReportPartType = values()).length, b = 0; b < i;) {
			IPTOrderReportPartType type = arrayOfIPTOrderReportPartType[b];
			if (type.getValue() == val)
				return type;
			b++;
		}

		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<>();
		byte b;
		int i;
		IPTOrderReportPartType[] arrayOfIPTOrderReportPartType;
		for (i = (arrayOfIPTOrderReportPartType = values()).length, b = 0; b < i;) {
			IPTOrderReportPartType type = arrayOfIPTOrderReportPartType[b];
			CFGItem wItem = new CFGItem();
			wItem.ID = type.getValue();
			wItem.ItemName = type.getLable();
			wItem.ItemText = type.getLable();
			wItemList.add(wItem);
			b++;
		}

		return wItemList;
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
 * IPTOrderReportPartType.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */