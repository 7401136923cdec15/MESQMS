package com.mes.qms.server.service.mesenum;

/**
 * 变更类型
 */
public enum TCMChangeType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 物料新增
	 */
	MaterialInsert(1, "物料新增"),
	/**
	 * 物料删除
	 */
	MaterialDelete(2, "物料删除"),
	/**
	 * 工序新增
	 */
	StepInsert(3, "工序新增"),
	/**
	 * 工序删除
	 */
	StepDelete(4, "工序删除"),
	/**
	 * 物料数量变更
	 */
	MaterialNumberChange(5, "物料数量变更"),
	/**
	 * 物料属性变更
	 */
	MaterialPropertyChange(6, "物料属性变更"),
	/**
	 * 工序工位变更
	 */
	StepChange(7, "工序工位变更"),
	/**
	 * 工序修改(主要是工艺文件修改)
	 */
	StepUpdate(8, "工序修改");

	private int value;
	private String lable;

	private TCMChangeType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static TCMChangeType getEnumType(int val) {
		for (TCMChangeType type : TCMChangeType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return TCMChangeType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
