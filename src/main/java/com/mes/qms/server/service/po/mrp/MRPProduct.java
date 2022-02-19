package com.mes.qms.server.service.po.mrp;

import java.io.Serializable;
import java.util.Calendar;

public class MRPProduct implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int ID=0;            //层级ID

    public int MRPID=0;               //MRPID

    public int MaterialID=0;         //物料ID

    public String MaterialNo="";     //物料编码

    public String MaterialName="";    //物料名称

    public int UnitID=0;              //物料单位ID
    
    public String BOMType="";        //BOM类型

    public int BOMID=0;              //BOMID

    public String BOMNo="";          //BOMNo

    public float MarginFQTY=0.0f;         //净需求量

    public float ProduceFQTY=0.0f;         //建议生产量


    public Calendar DemandStartTime= Calendar.getInstance();    //需求时间

    public Calendar DemandEndTime= Calendar.getInstance();      //需求时间

    public Calendar StartTime= Calendar.getInstance();          //建议开始时间

    public Calendar EndTime= Calendar.getInstance();            //建议结束时间

    public int Status=0;                  //状态(0:初始状态，1：审核；2.下单，3.开工，4.完工，5.暂停)

    public int Type=0;                    //生产订单类型(1:自制，2：委外)

    public String UnitText="";             //物料单位
    public MRPProduct()
    {
        this.DemandStartTime = Calendar.getInstance();
        this.DemandEndTime = Calendar.getInstance();
        this.StartTime = Calendar.getInstance();
        this.EndTime = Calendar.getInstance();
    }
    public MRPProduct(MRPMaterial wMaterial)
    {
        this.MRPID = 0;
        this.MaterialID = wMaterial.MaterialID;
        this.MaterialNo = wMaterial.MaterialNo;
        this.MaterialName = wMaterial.MaterialName;
        this.UnitID = wMaterial.UnitID;
        this.UnitText = wMaterial.UnitText;
        this.MarginFQTY = wMaterial.MarginFQTY;

        this.StartTime = wMaterial.DemandStartTime;
        this.EndTime = wMaterial.DemandEndTime;
    }
}
