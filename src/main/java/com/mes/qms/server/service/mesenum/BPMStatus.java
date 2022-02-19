package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

public enum BPMStatus {
	Default(0, "默认"),
	/**
	 * SCH
	 */
	Save(1, "保存"),
	/**
	 * 待审
	 */
	ToAudit(2, "待审"),
	/**
	 * 审核通过
	 */
	Audited(3, "审核通过"),
	/**
	 * 关闭/反审核
	 */
	Reject(4, "已退回"),
	/**
	 * 撤回
	 */
	Cancle(5, "撤回"),
	/**
	 * 关闭
	 */
	Closed(6, "关闭");

	private int value;
	private String lable;

	private BPMStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BPMStatus getEnumType(int val) {
		for (BPMStatus type : BPMStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return null;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (BPMStatus type : BPMStatus.values()) {
			CFGItem wItem = new CFGItem();
			wItem.ID = type.getValue();
			wItem.ItemName = type.getLable();
			wItem.ItemText = type.getLable();
			wItemList.add(wItem);
		}
		return wItemList;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
