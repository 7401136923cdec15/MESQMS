package com.mes.qms.server.service.po.mes;

import java.io.Serializable;
import java.util.Calendar;

public class MESCompany implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int ID= 0;

    public String Name= "";

    public String LinkMan= "";

    public String Phone= "";

    public int Grad= 0;

    public String Creator= "";

    public String Manager= "";

    public String Auditor= "";

    public Calendar CreateTime= Calendar.getInstance();

    public Calendar EditTime= Calendar.getInstance();

    public Calendar AuditTime= Calendar.getInstance();

    public int Status= 0;

    public int Active= 0;

    public String Key= "";
    
    public MESCompany()
    {
    	this.Name= "";
    	this.LinkMan= "";
    	this.Phone= "";
    	
    	this.Creator= "";
    	this.Manager= "";
    	this.Auditor= "";
    	this.Key= "";
    	
    	this.CreateTime= Calendar.getInstance();
    	this.EditTime= Calendar.getInstance();
    	this.AuditTime= Calendar.getInstance();
    }
}
