package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 计划审批操作类型
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-19 10:34:10
 * @LastEditTime 2020-1-19 10:34:14
 *
 */
public enum APSOperateType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 保存
	 */
	Save(1, "保存"),
	/**
	 * 提交
	 */
	Submit(2, "提交"),
	/**
	 * 审批
	 */
	Audit(3, "审批"),
	/**
	 * 驳回
	 */
	Reject(4, "驳回"),
	/**
	 * 撤销
	 */
	Cancel(5, "撤销"),
	/**
	 * 终止、关闭
	 */
	Abort(6, "终止、关闭"),
	/**
	 * 下达
	 */
	Issued(7, "下达");

	private int value;
	private String lable;

	private APSOperateType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static APSOperateType getEnumType(int val) {
		for (APSOperateType type : APSOperateType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return APSOperateType.Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (APSOperateType type : APSOperateType.values()) {
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
