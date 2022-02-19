package com.mes.qms.server.service.mesenum;

public enum SCHPositionLevel {
	/**
	 *  [EnumMember]
        Default = 0,            //未知
        [EnumMember]
        WorkShop = 1,           //车间
        [EnumMember]
        Line = 2,               //产线
        [EnumMember]
        Part = 3,               //工段
        [EnumMember]
        PartPoint = 4,          //工序
        [EnumMember]
        Station = 5,            //工位
        [EnumMember]
        Device = 6,             //设备
        [EnumMember]
        Stock = 7,              //仓库
	 */
	Default(0, "默认"),
	/**
	 * SCH
	 */
	WorkShop(1, "车间"),
	/**
	 * APP
	 */
	Line(2, "产线"),
	/**
	 * APP
	 */
	Part(3, "工段"),
	/**
	 * APP
	 */
	PartPoint(4, "工序"),
	/**
	 * APP
	 */
	Station(5, "工位"),
	/**
	 * APP
	 */
	Device(6, "设备"),
	/**
	 * APP
	 */
	Stock(7, "仓库");
	private int value;
	private String lable;

	private SCHPositionLevel(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static SCHPositionLevel getEnumType(int val) {
		for (SCHPositionLevel type : SCHPositionLevel.values()) {
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
