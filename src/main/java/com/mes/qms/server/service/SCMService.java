package com.mes.qms.server.service;

import java.util.Calendar;

import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.crm.CRMCustomer;
import com.mes.qms.server.utils.Configuration;

public interface SCMService {

	static String ServerUrl = Configuration.readConfigString("scm.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("scm.server.project.name", "config/config");

	APIResult CRM_QueryCustomerList(BMSEmployee wLoginUser, String wCustomerName, int wCountryID, int wProvinceID,
			int wCityID, int wActive);

	APIResult CRM_QueryCustomerByID(BMSEmployee wLoginUser, int wCustomerID);
	
	APIResult CRM_SaveCustomer(BMSEmployee wLoginUser, CRMCustomer wCRMCustomer);

	/**
	 * 根据TaxCode查询Customer  若wID>0则查询不等于此ID且TaxCode相同的Customer
	 * @param wLoginUser
	 * @param wID    CustomerID
	 * @param wTaxCode 
	 * @return
	 */
	APIResult CRM_QueryCustomerByTaxCode(BMSEmployee wLoginUser, int wID, String wTaxCode);

	APIResult CRM_QueryCustomerListByTaxCode(BMSEmployee wLoginUser, String wTaxCode);

	APIResult CRM_QueryLinkManList(BMSEmployee wLoginUser, int wCustomerID, int wActive);

	APIResult CRM_QueryLinkManByID(BMSEmployee wLoginUser, int wID);

	APIResult CRM_QuerySaleOrderByID(BMSEmployee wLoginUser, int wID);

	APIResult CRM_QuerySaleOrderListByCustomerID(BMSEmployee wLoginUser, int wCustomerID, int wStatus,
			Calendar wStartTime, Calendar wEndTime);

	APIResult CRM_QuerySaleOrderItemListByOrderID(BMSEmployee wLoginUser, int wOrderID);

	 
	APIResult SCM_QuerySupplierList(BMSEmployee wLoginUser, String wSupplierName, int wCountryID, int wProvinceID, int wCityID, int wActive);
    
	APIResult SCM_QuerySupplierByID(BMSEmployee wLoginUser, int wCustomerID);

	APIResult  SCM_QuerySupplierListByTaxCode(BMSEmployee wLoginUser, String wTaxCode);
	
	APIResult SCM_QueryLinkManList(BMSEmployee wLoginUser, int wCustomerID, int wActive);

	APIResult SCM_QueryLinkManByID(BMSEmployee wLoginUser, int wID);

	APIResult SCM_QueryOrderListBySupplierID(BMSEmployee wLoginUser, int wSupplierID, int wStatus, int wActive, Calendar wStartTime, Calendar wEndTime);

	APIResult SCM_QueryOrderByID(BMSEmployee wLoginUser, int wID,String wOrderNo);


}
