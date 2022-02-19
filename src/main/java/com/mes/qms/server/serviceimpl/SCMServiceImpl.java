package com.mes.qms.server.serviceimpl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.SCMService;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.crm.CRMCustomer;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RemoteInvokeUtils;

@Service
public class SCMServiceImpl implements SCMService {
	private static Logger logger = LoggerFactory.getLogger(SCMServiceImpl.class);

	public SCMServiceImpl() {
		super();
	}

	private static SCMService Instance;

	public static SCMService getInstance() {
		if (Instance == null)
			Instance = new SCMServiceImpl();
		return Instance;
	}

	@Override
	public APIResult CRM_QueryCustomerList(BMSEmployee wLoginUser, String wCustomerName, int wCountryID,
			int wProvinceID, int wCityID, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("customer_name", wCustomerName);
			wParms.put("country_id", wCountryID);
			wParms.put("province_id", wProvinceID);
			wParms.put("city_id", wCityID);
			wParms.put("active", wActive);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Customer/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QueryCustomerByID(BMSEmployee wLoginUser, int wCustomerID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("customer_id", wCustomerID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Customer/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QueryCustomerByTaxCode(BMSEmployee wLoginUser, int wID, String wTaxCode) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("tax_code", wTaxCode);
			wParms.put("id", wID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Customer/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QueryCustomerListByTaxCode(BMSEmployee wLoginUser, String wTaxCode) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("tax_code", wTaxCode);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Customer/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QueryLinkManList(BMSEmployee wLoginUser, int wCustomerID, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("active", wActive);
			wParms.put("customer_id", wCustomerID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/LinkManCustomer/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QueryLinkManByID(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("id", wID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/LinkManCustomer/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QuerySaleOrderByID(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("id", wID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/CRMSaleOrder/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QuerySaleOrderListByCustomerID(BMSEmployee wLoginUser, int wCustomerID, int wStatus,
			Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("status", wStatus);
			wParms.put("customer_id", wCustomerID);
			wParms.put("starttime", wStartTime);
			wParms.put("endtime", wEndTime);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/CRMSaleOrder/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_QuerySaleOrderItemListByOrderID(BMSEmployee wLoginUser, int wOrderID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("orderid", wOrderID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/CRMSaleOrder/CRMItemAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCM_QuerySupplierList(BMSEmployee wLoginUser, String wSupplierName, int wCountryID,
			int wProvinceID, int wCityID, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("supplier_name", wSupplierName);
			wParms.put("country_id", wCountryID);
			wParms.put("province_id", wProvinceID);
			wParms.put("city_id", wCityID);
			wParms.put("active", wActive);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Supplier/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCM_QuerySupplierByID(BMSEmployee wLoginUser, int wCustomerID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("supplier_id", wCustomerID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Supplier/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCM_QuerySupplierListByTaxCode(BMSEmployee wLoginUser, String wTaxCode) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("tax_code", wTaxCode);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Supplier/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCM_QueryLinkManList(BMSEmployee wLoginUser, int wCustomerID, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("customer_id", wCustomerID);
			wParms.put("active", wActive);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/LinkManSupplier/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCM_QueryLinkManByID(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("id", wID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/LinkManSupplier/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCM_QueryOrderListBySupplierID(BMSEmployee wLoginUser, int wSupplierID, int wStatus, int wActive,
			Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("supplierid", wSupplierID);
			wParms.put("status", wStatus);
			wParms.put("active", wActive);
			wParms.put("starttime", wStartTime);
			wParms.put("endtime", wEndTime);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCMOrder/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCM_QueryOrderByID(BMSEmployee wLoginUser, int wID, String wOrderNo) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("orderno", wOrderNo);
			wParms.put("id", wID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCMOrder/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CRM_SaveCustomer(BMSEmployee wLoginUser, CRMCustomer wCRMCustomer) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wCRMCustomer);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Customer/Update?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}
}
