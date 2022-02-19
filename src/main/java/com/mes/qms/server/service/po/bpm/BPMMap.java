package com.mes.qms.server.service.po.bpm;

import java.io.Serializable;

public class BPMMap implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID;

	/**
	 * 步骤ID
	 */
	public int StepID;
	
	/**
	 * Key
	 */
	public String Key;
	
	/**
	 * Value
	 */
	public String Value;

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	
	
}
