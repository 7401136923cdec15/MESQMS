package com.mes.qms.server.service.mesenum;

/**
 * 流程类型
 * 
 * @author ShrisJava
 *
 */
public enum BPMStepTypes {
	/**
	 * Undo
	 */
	Default(0, "默认"),
	/**
	 * 开始
	 */
	Start(1, "开始"),
	/**
	 * 合并等待前置
	 */
	CombineWite(2, "合并"),

	/**
	 * 合并关闭前置
	 */
	CombineClose(3, "合并"),
	/**
	 * 结束
	 */
	End(4, "结束"),
	/**
	 * 中断结束
	 */
	Forward(5, "中断结束");

	private int value;
	private String lable;

	private BPMStepTypes(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BPMStepTypes getEnumType(int val) {
		for (BPMStepTypes type : BPMStepTypes.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return BPMStepTypes.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
