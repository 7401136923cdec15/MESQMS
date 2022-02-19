package com.mes.qms.server.service.mesenum;

/**
 * 流程类型
 * 
 * @author ShrisJava
 *
 */
public enum BPMResultEnum {
	/**
	 * Undo
	 */
	Undo(0, "未执行"),
	/**
	 * 正常
	 */
	OK(1, "正常"),
	/**
	 * 不正常
	 */
	NotOK(2, "不正常"),
	/**
	 * 超时
	 */
	TimeOver(3, "超时"),
	
	/**
	 * 转发
	 */
	Forward(4, "转发");

	private int value;
	private String lable;

	private BPMResultEnum(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static BPMResultEnum getEnumType(int val) {
		for (BPMResultEnum type : BPMResultEnum.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return BPMResultEnum.Undo;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
