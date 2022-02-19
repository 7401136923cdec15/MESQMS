package com.mes.qms.server.service.po.focas;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * focas需要的统计图数据
 */
public class FocasReport implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 机车竣工数
	 */
	private int JCJG_Total_Year = 0;
	/**
	 * C6修竣工数
	 */
	private int JCJG_C6_Year = 0;
	/**
	 * C5修竣工数
	 */
	private int JCJG_C5_Year = 0;
	/**
	 * C6修停时
	 */
	private double JCTS_C6_Year = 0;
	/**
	 * C5修停时
	 */
	private double JCTS_C5_Year = 0;

	// 2021-5-24 19:04:26
	/**
	 * C6修年度台车返工件数
	 */
	private double TCFG_C6_Year = 0;
	/**
	 * C5修年度台车返工件数
	 */
	private double TCFG_C5_Year = 0;
	/**
	 * C6修年度交验合格率
	 */
	private double JYHGL_C6_Year = 0.0;
	/**
	 * C5修年度交验合格率
	 */
	private double JYHGL_C5_Year = 0.0;

	/**
	 * 在修机车数
	 */
	private int ZXJC_Month = 0;
	/**
	 * 在厂机车数
	 */
	private int ZCJC_Month = 0;
	/**
	 * 月竣工数
	 */
	private int JCJG_Total_Month = 0;
	/**
	 * C6修竣工数
	 */
	private int JCJG_C6_Month = 0;
	/**
	 * C5修竣工数
	 */
	private int JCJG_C5_Month = 0;

	/**
	 * C6修月度台车返工件数
	 */
	private double TCFG_C6_Month = 0;
	/**
	 * C5修月度台车返工件数
	 */
	private double TCFG_C5_Month = 0;
	/**
	 * C6修月度交验合格率
	 */
	private double JYHGL_C6_Month = 0.0;
	/**
	 * C5修月度交验合格率
	 */
	private double JYHGL_C5_Month = 0.0;

	public FocasReport() {
		super();
	}

	@JSONField(name = "JCJG_Total_Year")
	public int getJCJG_Total_Year() {
		return JCJG_Total_Year;
	}

	@JSONField(name = "JCJG_C6_Year")
	public int getJCJG_C6_Year() {
		return JCJG_C6_Year;
	}

	@JSONField(name = "JCJG_C5_Year")
	public int getJCJG_C5_Year() {
		return JCJG_C5_Year;
	}

	@JSONField(name = "JCTS_C6_Year")
	public double getJCTS_C6_Year() {
		return JCTS_C6_Year;
	}

	@JSONField(name = "JCTS_C5_Year")
	public double getJCTS_C5_Year() {
		return JCTS_C5_Year;
	}

	@JSONField(name = "ZXJC_Month")
	public int getZXJC_Month() {
		return ZXJC_Month;
	}

	@JSONField(name = "ZCJC_Month")
	public int getZCJC_Month() {
		return ZCJC_Month;
	}

	@JSONField(name = "JCJG_Total_Month")
	public int getJCJG_Total_Month() {
		return JCJG_Total_Month;
	}

	@JSONField(name = "JCJG_C6_Month")
	public int getJCJG_C6_Month() {
		return JCJG_C6_Month;
	}

	@JSONField(name = "JCJG_C5_Month")
	public int getJCJG_C5_Month() {
		return JCJG_C5_Month;
	}

	@JSONField(name = "TCFG_C6_Year")
	public double getTCFG_C6_Year() {
		return TCFG_C6_Year;
	}

	@JSONField(name = "TCFG_C5_Year")
	public double getTCFG_C5_Year() {
		return TCFG_C5_Year;
	}

	@JSONField(name = "JYHGL_C6_Year")
	public double getJYHGL_C6_Year() {
		return JYHGL_C6_Year;
	}

	@JSONField(name = "JYHGL_C5_Year")
	public double getJYHGL_C5_Year() {
		return JYHGL_C5_Year;
	}

	public void setJCJG_Total_Year(int jCJG_Total_Year) {
		JCJG_Total_Year = jCJG_Total_Year;
	}

	public void setTCFG_C6_Year(double tCFG_C6_Year) {
		TCFG_C6_Year = tCFG_C6_Year;
	}

	public void setTCFG_C5_Year(double tCFG_C5_Year) {
		TCFG_C5_Year = tCFG_C5_Year;
	}

	public void setJYHGL_C6_Year(double jYHGL_C6_Year) {
		JYHGL_C6_Year = jYHGL_C6_Year;
	}

	public void setJYHGL_C5_Year(double jYHGL_C5_Year) {
		JYHGL_C5_Year = jYHGL_C5_Year;
	}

	public void setJCJG_C6_Year(int jCJG_C6_Year) {
		JCJG_C6_Year = jCJG_C6_Year;
	}

	public void setJCJG_C5_Year(int jCJG_C5_Year) {
		JCJG_C5_Year = jCJG_C5_Year;
	}

	public void setJCTS_C6_Year(double jCTS_C6_Year) {
		JCTS_C6_Year = jCTS_C6_Year;
	}

	public void setJCTS_C5_Year(double jCTS_C5_Year) {
		JCTS_C5_Year = jCTS_C5_Year;
	}

	public void setZXJC_Month(int zXJC_Month) {
		ZXJC_Month = zXJC_Month;
	}

	public void setZCJC_Month(int zCJC_Month) {
		ZCJC_Month = zCJC_Month;
	}

	public void setJCJG_Total_Month(int jCJG_Total_Month) {
		JCJG_Total_Month = jCJG_Total_Month;
	}

	public void setJCJG_C6_Month(int jCJG_C6_Month) {
		JCJG_C6_Month = jCJG_C6_Month;
	}

	public void setJCJG_C5_Month(int jCJG_C5_Month) {
		JCJG_C5_Month = jCJG_C5_Month;
	}

	public double getTCFG_C6_Month() {
		return TCFG_C6_Month;
	}

	public double getTCFG_C5_Month() {
		return TCFG_C5_Month;
	}

	public double getJYHGL_C6_Month() {
		return JYHGL_C6_Month;
	}

	public double getJYHGL_C5_Month() {
		return JYHGL_C5_Month;
	}

	public void setTCFG_C6_Month(double tCFG_C6_Month) {
		TCFG_C6_Month = tCFG_C6_Month;
	}

	public void setTCFG_C5_Month(double tCFG_C5_Month) {
		TCFG_C5_Month = tCFG_C5_Month;
	}

	public void setJYHGL_C6_Month(double jYHGL_C6_Month) {
		JYHGL_C6_Month = jYHGL_C6_Month;
	}

	public void setJYHGL_C5_Month(double jYHGL_C5_Month) {
		JYHGL_C5_Month = jYHGL_C5_Month;
	}
}
