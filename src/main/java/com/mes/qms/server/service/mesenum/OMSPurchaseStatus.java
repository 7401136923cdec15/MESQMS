package com.mes.qms.server.service.mesenum;

/**
 *   -- 采购订单状态
 * 
 * @author PengYouWang
 * @CreateTime 2019年12月26日17:52:10
 * @LastEditTime 2019年12月26日17:52:27
 */
public enum OMSPurchaseStatus {
	/**
	 * 默认
	 */
	Default(0, "默认"),
	/**
	 * 未到货
	 */
	NotFinish(1, "未到货"),
	/**
	 * 已到货
	 */
	Finished(2, "已到货");

	private int value;
	private String lable;

	private OMSPurchaseStatus(int value, String lable) {
		this.value = value;
		this.lable = lable;
	}

	/**
	 * 通过 value 的数值获取枚举实例
	 *
	 * @param val
	 * @return
	 */
	public static OMSPurchaseStatus getEnumType(int val) {
		for (OMSPurchaseStatus type : OMSPurchaseStatus.values()) {
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
