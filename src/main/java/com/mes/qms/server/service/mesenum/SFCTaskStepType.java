package com.mes.qms.server.service.mesenum;

/**
 * 派工任务类型
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-5-27 13:43:57
 * @LastEditTime 2020-5-27 13:44:01
 *
 */
public enum SFCTaskStepType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 工序任务派工
	 */
	Step(1, "工序任务派工"),
	/**
	 * 预检问题项派工
	 */
	Question(2, "预检问题项派工"),
	/**
	 * 质量派工
	 */
	Quality(3, "质量派工");

	private int value;
	private String lable;

	private SFCTaskStepType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SFCTaskStepType getEnumType(int val) {
		for (SFCTaskStepType type : SFCTaskStepType.values()) {
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
