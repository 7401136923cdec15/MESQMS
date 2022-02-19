package com.mes.qms.server.service.mesenum;

/**
 * 指导书类型
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-2-12 13:20:22
 * @LastEditTime 2020-2-12 13:20:25
 *
 */
public enum IPTSOPType {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 图片
	 */
	Image(1, "图片"),
	/**
	 * PDF
	 */
	Pdf(2, "PDF"),
	/**
	 * 视频
	 */
	Video(3, "视频");

	private int value;
	private String lable;

	private IPTSOPType(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static IPTSOPType getEnumType(int val) {
		for (IPTSOPType type : IPTSOPType.values()) {
			if (type.getValue() == val) {
				return type;
			}
		}
		return IPTSOPType.Default;
	}

	public int getValue() {
		return value;
	}

	public String getLable() {
		return lable;
	}
}
