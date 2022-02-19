package com.mes.qms.server.service.mesenum;

/**
 * 用作前端判断是否合格
 * 
 * @author ShrisJava
 *
 */
public enum IPTStandardType {
	/**
	 * 文本
	 */
	Text(0, "文本"),
	/**
	 * 单选
	 */
	Combo(1, "单选"),
	/**
	 * 全开区间
	 */
	Range(2, "全开区间"),
	/**
	 * 闭区间
	 */
	RangeEQ(3, "闭区间"),
	/**
	 * 左闭右开区间
	 */
	RangeLTE(4, "左闭右开区间"),
	/**
	 * 左开右闭区间
	 */
	RangeGTE(5, "左开右闭区间"),
	/**
	 * 小于
	 */
	LT(6, "小于"),
	/**
	 * 大于
	 */
	GT(7, "大于"),
	/**
	 * 小于等于
	 */
	LTE(8, "小于等于"),
	/**
	 * 大于等于
	 */
	GTE(9, "大于等于"),
	/**
	 * 等于
	 */
	EQ(10, "等于"),
	/**
	 * 多选
	 */
	Check(11, "多选"),
	/**
	 * 不合格原因 单选且自己填 不显示Comment
	 */
	BadReason(12, "不合格原因   单选且自己填 不显示Comment"),
	/**
	 * 多数字输入
	 */
	ArrayNumber(13, "多数字输入"),
	/**
	 * 多文本输入
	 */
	ArrayText(14, "多文本输入"),
	/**
	 * 无标准值
	 */
	ArrayNull(15, "无标准值"),
	/**
	 * 数字
	 */
	Number(16, "数字");

	private int value;
	private String lable;

	private IPTStandardType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IPTStandardType getEnumType(int val) {
		for (IPTStandardType type : IPTStandardType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Text;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}