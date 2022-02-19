package com.mes.qms.server.service.po.crm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CRMSaleOrder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	 public int ID=0;

     public String OrderNo= "";

     public int CustomerID=0;

     public String CustomerName= "";

     public String CustomerCode= "";

     public String TaxCode= "";

     public int Status=0;

     public String StatusText= "";

     public String Description= "";

     public int Priority=0;

     public int UnitID=0;
     
     public int EditorID=0;

     public String UnitText= "";

     public float ContractAmount=0.0f;

     public float AmountReceived=0.0f;

     public String ContractFile= "";

     public String CustomerMan= "";

     public String LinkPhone= "";

     public String Editor= "";

     public Calendar EditTime= Calendar.getInstance();

     public int Active=0;

     public Calendar ImportTime= Calendar.getInstance();

     public List<CRMSaleOrderItem> SaleOrderItemList= new ArrayList<>();
     
     public CRMSaleOrder()
     {
         this.SaleOrderItemList = new ArrayList<>();
     }
}
