package com.mes.qms.server.service.po.mes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.fpc.FPCPartPoint;
import com.mes.qms.server.service.po.fpc.FPCProduct;
import com.mes.qms.server.service.po.fpc.FPCProductType;
import com.mes.qms.server.service.po.fpc.FPCRoute;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.shristool.LoggerTool;

public class FPCModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int CompanyID=0;
	
	public int ErrorCode=0;
	
    public List<FPCPart> PartList= new ArrayList<>();           //标准工序

    public List<FPCPartPoint> PartPointList= new ArrayList<>();              //标准工序

    public List<FPCRoute> RouteList= new ArrayList<>();                      //工艺版本

    public List<FPCRoutePart> RoutePartList= new ArrayList<>();              //工艺版本--工序段

    public List<FPCRoutePartPoint> RoutePartPointList= new ArrayList<>();    //工艺版本--工序集合

    public List<FPCProductType> ProductTypeList= new ArrayList<>();          //工艺版本--产品类型

    public List<FPCProduct> ProductList= new ArrayList<>();                  //工艺版本--产品

//    public List<FPCProductRoute> ProductRouteList= new ArrayList<>();        //工艺版本--产品工艺路线
    
    public FPCModel()
    {
        this.CompanyID = 0;
        this.ErrorCode = 0;
        this.PartList = new ArrayList<>(); 
        this.PartPointList = new ArrayList<>(); 
        this.RouteList = new ArrayList<>(); 
        this.RoutePartList = new ArrayList<>(); 
        this.RoutePartPointList = new ArrayList<>(); 
        this.ProductTypeList = new ArrayList<>(); 
        this.ProductList = new ArrayList<>(); 
//        this.ProductRouteList = new ArrayList<>(); 
    }
    public FPCModel Clone()
    {
        FPCModel wRouteModel = new FPCModel();
        try
        {
            wRouteModel.CompanyID = this.CompanyID;
            wRouteModel.ErrorCode = this.ErrorCode;
            wRouteModel.PartList = new ArrayList<FPCPart>(this.PartList);
            wRouteModel.PartPointList = new ArrayList<FPCPartPoint>(this.PartPointList);
            wRouteModel.RouteList = new ArrayList<FPCRoute>(this.RouteList);
            wRouteModel.RoutePartList = new ArrayList<FPCRoutePart>(this.RoutePartList);
            wRouteModel.RoutePartPointList = new ArrayList<FPCRoutePartPoint>(this.RoutePartPointList);
            wRouteModel.ProductTypeList = new ArrayList<FPCProductType>(this.ProductTypeList);
            wRouteModel.ProductList = new ArrayList<FPCProduct>(this.ProductList);
//            wRouteModel.ProductRouteList = new ArrayList<FPCProductRoute>(this.ProductRouteList);
        }
        catch (Exception ex)
        {
            LoggerTool.SaveException("MESRouteModel", "Clone", "Function Exception:" + ex.toString());
        }
        return wRouteModel;
    }
}
