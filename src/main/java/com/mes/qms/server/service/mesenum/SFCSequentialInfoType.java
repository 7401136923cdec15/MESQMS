package com.mes.qms.server.service.mesenum;

/**
 * 电子履历类型枚举
 */
public enum SFCSequentialInfoType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 机车进厂
	 */
	Inplant(1, "机车进厂"),
	/**
	 * 工位任务
	 */
	StationTask(2, "工位任务"),
	/**
	 * 工序任务
	 */
	StepTask(3, "工序任务"),
	/**
	 * 自检
	 */
	SelfCheck(4, "自检"),
	/**
	 * 互检
	 */
	MutualCheck(5, "互检"),
	/**
	 * 专检
	 */
	SpecialCheck(6, "专检"),
	/**
	 * 不合格评审
	 */
	NCR(7, "不合格评审"),
	/**
	 * 返修
	 */
	Repair(8, "返修"),
	/**
	 * 终检
	 */
	FinalCheck(9, "终检"),
	/**
	 * 竣工确认
	 */
	CompleteConfirm(10, "竣工确认"),
	/**
	 * 出厂检
	 */
	OutCheck(11, "出厂检"),
	/**
	 * 机车出厂
	 */
	OutPlant(12, "机车出厂"),
	/**
	 * 异常
	 */
	Exception(13, "异常"),
	/**
	 * 收到电报
	 */
	Telegraph(14, "收到电报"),
	/**
	 * 移车
	 */
	MoveCar(15, "移车"),
	/**
	 * 预检
	 */
	PreCheck(16, "预检"),
	/**
	 * 预检问题项
	 */
	ProblemCheck(17, "预检问题项"),
	/**
	 * 开工打卡
	 */
	ClockIn(18, "开工打卡"),
	/**
	 * 完工打卡
	 */
	ClockOut(19, "完工打卡"),
	/**
	 * 出厂申请
	 */
	OutApply(20, "出厂申请");

	private int value;
	private String lable;

	SFCSequentialInfoType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	public static SFCSequentialInfoType getEnumType(int val) {
		for (SFCSequentialInfoType type : SFCSequentialInfoType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return Default;
	}

	public int getValue() {
		return this.value;
	}

	public String getLable() {
		return this.lable;
	}
}