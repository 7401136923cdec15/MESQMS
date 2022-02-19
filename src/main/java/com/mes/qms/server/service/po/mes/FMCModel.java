package com.mes.qms.server.service.po.mes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.fmc.FMCBusinessUnit;
import com.mes.qms.server.service.po.fmc.FMCFactory;
import com.mes.qms.server.service.po.fmc.FMCLine;
import com.mes.qms.server.service.po.fmc.FMCResource;
import com.mes.qms.server.service.po.fmc.FMCStation;
import com.mes.qms.server.service.po.fmc.FMCWorkDay;
import com.mes.qms.server.service.po.fmc.FMCWorkShop;
import com.mes.qms.server.shristool.LoggerTool;

public class FMCModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int CompanyID=0;

	public int ErrorCode=0;
	
    public List<FMCFactory> FactoryList= new ArrayList<>();             //工厂模型

    public List<FMCBusinessUnit> BusinessUnitList= new ArrayList<>();   //事业部

    public List<FMCWorkShop> WorkShopList= new ArrayList<>();           //车间

    public List<FMCLine> LineList= new ArrayList<>();                   //产线

    public List<FMCResource> ResourceList= new ArrayList<>();           //制造资源

    public List<FMCStation> StationList= new ArrayList<>();            //工位

    public List<FMCWorkDay> ShiftList= new ArrayList<>();              //班次模板
    
    public FMCModel()
    {
        this.CompanyID = 0;
        this.ErrorCode = 0;
        this.FactoryList = new ArrayList<>();
        this.BusinessUnitList = new ArrayList<>();
        this.WorkShopList = new ArrayList<>();
        this.LineList = new ArrayList<>();
        this.ResourceList = new ArrayList<>();
        this.StationList = new ArrayList<>();
        this.ShiftList = new ArrayList<>();
    }
    public FMCModel Clone() 
    {
        FMCModel wFactoryModel = new FMCModel();
        try
        {
            wFactoryModel.FactoryList = new ArrayList<FMCFactory>(this.FactoryList);
            wFactoryModel.BusinessUnitList = new ArrayList<FMCBusinessUnit>(this.BusinessUnitList);
            wFactoryModel.WorkShopList = new ArrayList<FMCWorkShop>(this.WorkShopList);
            wFactoryModel.LineList = new ArrayList<FMCLine>(this.LineList);
            wFactoryModel.ResourceList = new ArrayList<FMCResource>(this.ResourceList);
            wFactoryModel.StationList = new ArrayList<FMCStation>(this.StationList);
            wFactoryModel.ShiftList = new ArrayList<FMCWorkDay>(this.ShiftList);
        }
        catch (Exception ex)
        {
            LoggerTool.SaveException("MESFactoryModel", "Clone", "Function Exception:" + ex.toString());
        }
        return wFactoryModel;
    }
	public int getCompanyID() {
		return CompanyID;
	}
	public void setCompanyID(int companyID) {
		CompanyID = companyID;
	}
	public int getErrorCode() {
		return ErrorCode;
	}
	public void setErrorCode(int errorCode) {
		ErrorCode = errorCode;
	}
	public List<FMCFactory> getFactoryList() {
		return FactoryList;
	}
	public void setFactoryList(List<FMCFactory> factoryList) {
		FactoryList = factoryList;
	}
	public List<FMCBusinessUnit> getBusinessUnitList() {
		return BusinessUnitList;
	}
	public void setBusinessUnitList(List<FMCBusinessUnit> businessUnitList) {
		BusinessUnitList = businessUnitList;
	}
	public List<FMCWorkShop> getWorkShopList() {
		return WorkShopList;
	}
	public void setWorkShopList(List<FMCWorkShop> workShopList) {
		WorkShopList = workShopList;
	}
	public List<FMCLine> getLineList() {
		return LineList;
	}
	public void setLineList(List<FMCLine> lineList) {
		LineList = lineList;
	}
	public List<FMCResource> getResourceList() {
		return ResourceList;
	}
	public void setResourceList(List<FMCResource> resourceList) {
		ResourceList = resourceList;
	}
	public List<FMCStation> getStationList() {
		return StationList;
	}
	public void setStationList(List<FMCStation> stationList) {
		StationList = stationList;
	}
	public List<FMCWorkDay> getShiftList() {
		return ShiftList;
	}
	public void setShiftList(List<FMCWorkDay> shiftList) {
		ShiftList = shiftList;
	}
}
