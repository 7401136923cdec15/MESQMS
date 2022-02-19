package com.mes.qms.server.service.po.mes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bms.BMSRoleItem;
import com.mes.qms.server.shristool.LoggerTool;

public class BMSModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public int CompanyID;
	
	public int ErrorCode=0;

    public List<BMSEmployee> EmployeeList= new ArrayList<>();

    public List<BMSRoleItem> PowerList= new ArrayList<>();

    public List<BMSRoleItem> RangeList= new ArrayList<>();
    
    public BMSModel()
    {
        this.CompanyID = 0;
        this.ErrorCode = 0;
        this.EmployeeList = new ArrayList<>();
        this.PowerList = new ArrayList<>();
        this.RangeList = new ArrayList<>();
    }
    public BMSModel Clone()
    {
        BMSModel wPowerModel = new BMSModel();
        try
        {
        	wPowerModel.CompanyID=this.CompanyID;
        	wPowerModel.ErrorCode=this.ErrorCode;
            wPowerModel.EmployeeList = new ArrayList<BMSEmployee>(this.EmployeeList);
            wPowerModel.PowerList = new ArrayList<BMSRoleItem>(this.PowerList);
            wPowerModel.RangeList = new ArrayList<BMSRoleItem>(this.RangeList);
        }
        catch (Exception ex)
        {
            LoggerTool.SaveException("MESPower", "Clone", "Function Exception:" + ex.toString());
        }
        return wPowerModel;
    }
}
