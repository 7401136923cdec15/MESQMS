package com.mes.qms.server.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.WDWService;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RemoteInvokeUtils;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-3-31 18:31:56
 * @LastEditTime 2020-4-2 15:52:06
 *
 */
@Service
public class WDWServiceImpl implements WDWService {
	private static Logger logger = LoggerFactory.getLogger(WDWServiceImpl.class);

	public WDWServiceImpl() {
	}

	private static WDWService Instance;

	public static WDWService getInstance() {
		if (Instance == null)
			Instance = new WDWServiceImpl();
		return Instance;
	}

	@Override
	public APIResult WDW_SpecialItemAll_NCR(BMSEmployee wAdminUser, int wTaskID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("TaskStepID", wTaskID);

			String wUri = StringUtils.Format("api/NCR/SpecialItemAll?cadv_ao={0}&cade_po={1}", wAdminUser.LoginName,
					wAdminUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult WDW_SpecialItemAll_Repair(BMSEmployee wAdminUser, int wTaskID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("SpecialTaskID", wTaskID);

			String wUri = StringUtils.Format("api/RRO/SpecialItemAll?cadv_ao={0}&cade_po={1}", wAdminUser.LoginName,
					wAdminUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult WDW_IsAllRepairItemClosed(BMSEmployee wAdminUser, int wOrderID, int wPartID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("OrderID", wOrderID);
			wParms.put("PartID", wPartID);

			String wUri = StringUtils.Format("api/RRO/JugdeItemClose?cadv_ao={0}&cade_po={1}", wAdminUser.LoginName,
					wAdminUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult WDW_QueryItemByOrderID(BMSEmployee wAdminUser, int wOrderID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("OrderID", wOrderID);

			String wUri = StringUtils.Format("api/RRO/QueryItemByOrderID?cadv_ao={0}&cade_po={1}", wAdminUser.LoginName,
					wAdminUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult NCR_QueryTimeAll(BMSEmployee wAdminUser, int wOrderID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("OrderID", wOrderID);
			wParms.put("StatusIDList", new ArrayList<Integer>());

			String wUri = StringUtils.Format("api/NCR/TimeAll?cadv_ao={0}&cade_po={1}", wAdminUser.LoginName,
					wAdminUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
