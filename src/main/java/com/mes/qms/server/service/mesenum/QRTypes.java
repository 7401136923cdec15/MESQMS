package com.mes.qms.server.service.mesenum;

public enum QRTypes {

	/**
	 * 默认
	 * 
	 * @param value
	 * @param lable
	 */
	Default(0, "默认"),
	/**
	 * 事业部
	 */
	Business(1, "事业部"),
	/**
	 * 工厂
	 */
	Factory(2, "工厂"),
	/**
	 * 车间
	 */
	WorkShop(3, "车间"),
	/**
	 * 产线
	 */
	Line(4, "产线"),
	/**
	 * 工位
	 */
	Station(5, "工位"),
	/**
	 * 仓库
	 */
	Storehouse(6, "仓库"),
	/**
	 * 仓位
	 */
	StoreLocation(7, "仓位"),
	/**
	 * 设备
	 */
	Device(8, "设备"),
	/**
	 * 备件
	 */
	Spare(9, "备件"),

	/**
	 * 
	 */
	WorkPlace(10, "台位"),

	/**
	 * PS 
	 */
	PartPoint(11, "工位"),

	Part(12, "工区");

	private int value;
	private String lable;

	private QRTypes(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static QRTypes getEnumType(int val) {
		for (QRTypes type : QRTypes.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return QRTypes.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
