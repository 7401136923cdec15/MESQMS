package com.mes.qms.server.service.po.bpm;

import java.io.Serializable;
 

/**
 * 流程步骤自定义字段结构
 * 
 * @author ShrisJava
 *
 */

public class BPMCustom implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 需填充的字段名称
	 */
	public String PropertyName = "";
	/**
	 * 需填充的字段类型
	 */
	public String PropertyTypeName = "";
	/**
	 * 需填充的值
	 */
	public Object PropertyValue =new Object() ;

	/**
	 * 字段类型枚举
	 */
	public int PropertyType = 0;

	/**
	 * 业务类型 给前端用的 可以不用 种类太多，先放着不管 以后完善
	 */
	public int BusinessType = 0;

	/**
	 * 是否必填
	 */
	public int NotNull = 0;

	/**
	 * 填充主流程数据
	 */
	public int FillFlow = 0;

	/**
	 * 在哪流程之前填充
	 */
	public int PrevStepFill = 0;

	/**
	 * 在哪流程结束填充
	 */
	public int StepFill = 0;

	public BPMCustom() {

	}
	

	public String getPropertyName() {
		return PropertyName;
	}

	public void setPropertyName(String propertyName) {
		PropertyName = propertyName;
	}

	public String getPropertyTypeName() {
		return PropertyTypeName;
	}

	public void setPropertyTypeName(String propertyTypeName) {
		PropertyTypeName = propertyTypeName;
	}

	public Object getPropertyValue() {
		return PropertyValue;
	}

	public void setPropertyValue(Object propertyValue) {
		PropertyValue = propertyValue;
	}

	public int getPropertyType() {
		return PropertyType;
	}

	public void setPropertyType(int propertyType) {
		PropertyType = propertyType;
	}

	public int getBusinessType() {
		return BusinessType;
	}

	public void setBusinessType(int businessType) {
		BusinessType = businessType;
	}

	public int getNotNull() {
		return NotNull;
	}

	public void setNotNull(int notNull) {
		NotNull = notNull;
	}

	public int getFillFlow() {
		return FillFlow;
	}

	public void setFillFlow(int fillFlow) {
		FillFlow = fillFlow;
	}

	public int getPrevStepFill() {
		return PrevStepFill;
	}

	public void setPrevStepFill(int prevStepFill) {
		PrevStepFill = prevStepFill;
	}

	public int getStepFill() {
		return StepFill;
	}

	public void setStepFill(int stepFill) {
		StepFill = stepFill;
	}

}
