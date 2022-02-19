package com.mes.qms.server.service.po.focas;

import java.io.Serializable;

/**
 * focas需要的历史数据
 */
public class FocasHistoryData implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	private int id = 0;
	/**
	 * 年份
	 */
	private int year = 0;
	/**
	 * 机车竣工数
	 */
	private int completeNumber = 0;
	/**
	 * C6修竣工数
	 */
	private int completeNumberC6 = 0;
	/**
	 * C5修竣工数
	 */
	private int completeNumberC5 = 0;
	/**
	 * C6修停时
	 */
	private double stopTimeC6 = 0;
	/**
	 * C5修停时
	 */
	private double stopTimeC5 = 0;
	/**
	 * 台车返工件数
	 */
	private double repairNumber = 0;
	/**
	 * 交验合格率
	 */
	private double qualityRate = 0.0;

	public FocasHistoryData() {
		super();
	}

	public FocasHistoryData(int id, int year, int completeNumber, int completeNumberC6, int completeNumberC5,
			double stopTimeC6, double stopTimeC5, double repairNumber, double qualityRate) {
		super();
		this.id = id;
		this.year = year;
		this.completeNumber = completeNumber;
		this.completeNumberC6 = completeNumberC6;
		this.completeNumberC5 = completeNumberC5;
		this.stopTimeC6 = stopTimeC6;
		this.stopTimeC5 = stopTimeC5;
		this.repairNumber = repairNumber;
		this.qualityRate = qualityRate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setStopTimeC6(double stopTimeC6) {
		this.stopTimeC6 = stopTimeC6;
	}

	public void setStopTimeC5(double stopTimeC5) {
		this.stopTimeC5 = stopTimeC5;
	}

	public void setRepairNumber(double repairNumber) {
		this.repairNumber = repairNumber;
	}

	public int getCompleteNumber() {
		return completeNumber;
	}

	public int getCompleteNumberC6() {
		return completeNumberC6;
	}

	public int getCompleteNumberC5() {
		return completeNumberC5;
	}

	public double getStopTimeC6() {
		return stopTimeC6;
	}

	public double getStopTimeC5() {
		return stopTimeC5;
	}

	public double getRepairNumber() {
		return repairNumber;
	}

	public double getQualityRate() {
		return qualityRate;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setCompleteNumber(int completeNumber) {
		this.completeNumber = completeNumber;
	}

	public void setCompleteNumberC6(int completeNumberC6) {
		this.completeNumberC6 = completeNumberC6;
	}

	public void setCompleteNumberC5(int completeNumberC5) {
		this.completeNumberC5 = completeNumberC5;
	}

	public void setStopTimeC6(int stopTimeC6) {
		this.stopTimeC6 = stopTimeC6;
	}

	public void setStopTimeC5(int stopTimeC5) {
		this.stopTimeC5 = stopTimeC5;
	}

	public void setQualityRate(double qualityRate) {
		this.qualityRate = qualityRate;
	}
}
