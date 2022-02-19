package com.mes.qms.server.service.po.mrp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MRPInstance implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public int ID=0;      

    public List<MRPOrder> OrderList=new ArrayList<>();               //生产预测订单

    public MRPEntry Entry= new MRPEntry();                           //MRP计算条件

    public List<MRPOrderEntry> OrderEntryList=new ArrayList<>();     //MRP报告
    
    public MRPInstance()
    {
        this.ID = 0;
        this.OrderList =new ArrayList<>();
        this.Entry = new MRPEntry();
        this.OrderEntryList =new ArrayList<>();
    }
}
