package com.mes.qms.server.service.mesenum;

import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.cfg.CFGItem;

/**
 * 工位任务状态
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-19 11:09:21
 * @LastEditTime 2020-1-19 11:09:25
 *
 */
public enum APSTaskStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 保存
	 */
	Saved(1, "保存"),
	/**
	 * 下达
	 */
	Issued(2, "下达"),
	/**
	 * 已确认 用于日计划 与排程无关
	 */
	Confirm(3, "已确认"),
	/**
	 * 开工
	 */
	Started(4, "开工"),
	/**
	 * 完工
	 */
	Done(5, "完工"),
	/**
	 * 暂停
	 */
	Suspend(6, "暂停"),
	/**
	 * 终止
	 */
	Aborted(7, "终止"),
	/**
	 * 提交
	 */
	Submit(8, "提交"),
	/**
	 * 待主任调度审批
	 */
	ToZRAudit(9, "待主任调度审批"),
	/**
	 * 主任调度已驳回
	 */
	ZRRejected(10, "主任调度已驳回"),
	/**
	 * 待生产管理室审批
	 */
	ToSCAudit(11, "待生产管理室审批"),
	/**
	 * 生产管理室已驳回
	 */
	SCRejected(12, "生产管理室已驳回"),
	/**
	 * 待制造中心审批
	 */
	ToZZAudit(13, "待制造中心审批"),
	/**
	 * 制造中心已驳回
	 */
	ZZRejected(14, "制造中心已驳回"),
	/**
	 * 制造中心已审批
	 */
	ZZAudited(15, "制造中心已审批"),
	/**
	 * 待公司副总审批
	 */
	ToCompanyAudit(16, "待公司副总审批"),
	/**
	 * 公司副总已驳回
	 */
	CompanyRejected(17, "公司副总已驳回"),
	/**
	 * 公司副总已审批
	 */
	Audited(18, "公司副总已审批");

	private int value;
	private String lable;

	private APSTaskStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static APSTaskStatus getEnumType(int val) {
		for (APSTaskStatus type : APSTaskStatus.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public static List<CFGItem> getEnumList() {
		List<CFGItem> wItemList = new ArrayList<CFGItem>();

		for (APSTaskStatus type : APSTaskStatus.values()) {
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
