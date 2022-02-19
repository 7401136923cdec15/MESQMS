package com.mes.qms.server.service.mesenum;

/**
 * 流程类型
 * 
 * @author ShrisJava
 *
 */
public enum BPMFlowTypes {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 移车
	 */
	MoveCar(1, "移车"),
	/**
	 * 不合格评审
	 */
	NCR(2, "不合格评审"),
	/**
	 * 返修
	 */
	Repair(3, "返修"),
	/**
	 * 预检项申请
	 */
	IptItemApply(4, "预检项申请"),
	/**
	 * 预检单
	 */
	PreCheck(5, "预检单"),
	/**
	 * 预检问题项
	 */
	PTIProblem(6, "预检问题项");

	private int value;
	private String lable;

	private BPMFlowTypes(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BPMFlowTypes getEnumType(int val) {
		for (BPMFlowTypes type : BPMFlowTypes.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return BPMFlowTypes.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
