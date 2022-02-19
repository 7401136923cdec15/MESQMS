package com.mes.qms.server.serviceimpl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.CoreService;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.bfc.BFCAuditAction;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.bfc.BFCMessageResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bms.BMSWorkCharge;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.cfg.CFGUnit;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RemoteInvokeUtils;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2019年12月27日12:45:42
 * @LastEditTime 2020-4-9 22:08:41
 *
 */
@Service
public class CoreServiceImpl implements CoreService {
	private static Logger logger = LoggerFactory.getLogger(CoreServiceImpl.class);

	public CoreServiceImpl() {
		super();
	}

	private static CoreService Instance;

	public static CoreService getInstance() {
		if (Instance == null)
			Instance = new CoreServiceImpl();
		return Instance;
	}

	@Override
	public APIResult BMS_LoginEmployee(String wLoginName, String wPassword, String wToken, long wMac, int wnetJS) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("user_id", wLoginName);
			wParms.put("passWord", wPassword);
			wParms.put("token", wToken);
			wParms.put("PhoneMac", wMac);
			wParms.put("netJS", wMac);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, "api/User/Login", wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_GetEmployeeAll(BMSEmployee wLoginUser, int wDepartmentID, int wPosition, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("active", wActive);
			wParms.put("DepartmentID", wDepartmentID);
			wParms.put("Position", wPosition);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/User/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_QueryEmployeeByID(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("user_info", wID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/User/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_CheckPowerByAuthorityID(int wCompanyID, int wUserID, int wFunctionID, int wRangeID,
			int wTypeID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("AuthortyID", wFunctionID);
			wParms.put("RangeID", wRangeID);
			wParms.put("TypeID", wTypeID);
			wParms.put("CompanyID", wCompanyID);
			wParms.put("UserID", wUserID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, "api/Role/Check", wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取岗位列表
	 * 
	 * @param wLoginUser
	 * @param wCompanyID
	 * @return
	 */
	@Override
	public APIResult BMS_QueryPositionList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format("api/Department/AllPosition?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_QueryPosition(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wID);
			String wUri = StringUtils.Format("api/Department/InfoPosition?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取部门列表
	 * 
	 * @param wLoginUser
	 * @param wCompanyID
	 * @param wLoginID
	 * @return
	 */
	@Override
	public APIResult BMS_QueryDepartmentList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format("api/Department/AllDepartment?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_QueryDepartment(BMSEmployee wLoginUser, int wID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wID);
			String wUri = StringUtils.Format("api/Department/InfoDepartment?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CFG_QueryCalendarList(BMSEmployee wLoginUser, int wYear, int wWorkShopID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("year", wYear);
			wParms.put("WorkShopID", wWorkShopID);
			String wUri = StringUtils.Format("api/Holiday/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CFG_QueryCalendarList(BMSEmployee wLoginUser, int wWorkShopID, Calendar wStartTime,
			Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {

			if (wEndTime == null && wStartTime != null) {
				wEndTime = Calendar.getInstance();
				wEndTime.add(Calendar.YEAR, 2);
			}

			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("StartTime", wStartTime);
			wParms.put("EndTime", wEndTime);
			wParms.put("WorkShopID", wWorkShopID);
			String wUri = StringUtils.Format("api/Holiday/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CFG_QueryRegionList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			String wUri = StringUtils.Format("api/Area/All?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_QueryRangeList(BMSEmployee wLoginUser, int wUserID, int wFunctionID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("FunctionID", wFunctionID);
			wParms.put("UserID", wUserID);
			String wUri = StringUtils.Format("api/Role/Range?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wType, int wActive,
			int wShiftID, Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("ModuleID", wModuleID);
			wParms.put("ShiftID", wShiftID);
			wParms.put("Type", wType);
			wParms.put("Active", wActive);
			wParms.put("StartTime", wStartTime);
			wParms.put("EndTime", wEndTime);
			String wUri = StringUtils.Format("api/HomePage/MsgAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wMessageID,
			int wType, int wActive, int wShiftID, Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("ModuleID", wModuleID);
			wParms.put("ShiftID", wShiftID);
			wParms.put("MessageID", wMessageID);
			wParms.put("Type", wType);
			wParms.put("Active", wActive);
			wParms.put("StairtTime", wStartTime);
			wParms.put("EndTime", wEndTime);
			String wUri = StringUtils.Format("/api/HomePage/MsgAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID,
			List<Integer> wMessageID, int wType, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ResponsorID", wResponsorID);
			wParms.put("ModuleID", wModuleID);
			wParms.put("ShiftID", -1);
			wParms.put("MessageID", StringUtils.Join(",", wMessageID));
			wParms.put("Type", wType);
			wParms.put("Active", wActive);
			String wUri = StringUtils.Format("/api/HomePage/MsgAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_UpdateMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wBFCMessageList);
			wParms.put("Send", 0);
			String wUri = StringUtils.Format("api/HomePage/MsgUpdate?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_SendMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wBFCMessageList);
			wParms.put("Send", 1);
			String wUri = StringUtils.Format("api/HomePage/MsgUpdate?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_QueryWorkChargeList(BMSEmployee wLoginUser, int wStationID, int wClassID, int wActive) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("StationID", wStationID);
			wParms.put("Active", wActive);
			wParms.put("ClassID", wClassID);

			String wUri = StringUtils.Format("api/WorkCharge/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CFG_QueryUnitList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			String wUri = StringUtils.Format("api/Unit/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_SaveWorkCharge(BMSEmployee wLoginUser, BMSWorkCharge wBMSWorkCharge) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wBMSWorkCharge);

			String wUri = StringUtils.Format("api/WorkCharge/Update?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public APIResult BFC_UpdateAction(BMSEmployee wLoginUser, BFCAuditAction wBFCAuditAction, String wTitle) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wBFCAuditAction);
			wParms.put("Title", wTitle);
			String wUri = StringUtils.Format("api/BFCAudit/UpdateAction?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_CurrentConfig(BMSEmployee wLoginUser, int wModuleID, int wTaskID, int wUserID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ModuleID", wModuleID);
			wParms.put("TaskID", wTaskID);
			wParms.put("UserID", wUserID);
			String wUri = StringUtils.Format("api/BFCAudit/CurrentConfig?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BFC_ActionAll(BMSEmployee wLoginUser, int wModuleID, int wTaskID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ModuleID", wModuleID);
			wParms.put("TaskID", wTaskID);
			String wUri = StringUtils.Format("api/BFCAudit/ActionAll?cadv_ao={0}&cade_po={1}", wLoginUser.LoginName,
					wLoginUser.Password);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult FMC_QueryWorkChargeList(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("StationID", 0);
			wParms.put("Active", 1);
			wParms.put("ClassID", 0);

			String wUri = StringUtils.Format("api/WorkCharge/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_UserAllByFunction(BMSEmployee wLoginUser, int wFunctionID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("FunctionID", wFunctionID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Role/UserAllByFunctionID?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult BMS_UserAll(BMSEmployee wLoginUser, int wRoleID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("role_id", wRoleID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Role/UserAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult CFG_SaveUnit(BMSEmployee wLoginUser, CFGUnit wUnit) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wUnit);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Unit/Update?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_PartItemAll(BMSEmployee wLoginUser, int wOrderID, String wPartItemNo, int wCustomerID,
			int wLineID, String wProductNo) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("OrderID", wOrderID);
			wParms.put("PartItemNo", wPartItemNo);
			wParms.put("CustomerID", wCustomerID);
			wParms.put("LineID", wLineID);
			wParms.put("ProductNo", wProductNo);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/MSSPart/PartItemAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult QMS_StartInstance(BMSEmployee wLoginUser, String processDefinitionKey) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("processDefinitionKey", processDefinitionKey);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, "MESQMS",
					StringUtils.Format("api/Runtime/startProcessByProcessDefinitionKey?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult QMS_CompleteInstance(BMSEmployee wLoginUser, BPMTaskBase wBPMTaskBase, String wTaskID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("data", wBPMTaskBase);
			wParms.put("TaskID", wTaskID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, "MESQMS",
					StringUtils.Format("api/Runtime/CompleteMyPersonalTask?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult QMS_DynamicTurnBop(BMSEmployee wLoginUser, int wOldRouteID, int wNewRouteID,
			List<FPCRoutePartPoint> wAddedList, List<FPCRoutePartPoint> wRemovedList,
			List<FPCRoutePartPoint> wChangedList, List<String> wPartNoList, List<Integer> wReworkOrdreIDList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("OldRouteID", wOldRouteID);
			wParms.put("NewRouteID", wNewRouteID);
			wParms.put("AddedList", wAddedList);
			wParms.put("RemovedList", wRemovedList);
			wParms.put("ChangedList", wChangedList);
			wParms.put("PartNoList", wPartNoList);
			wParms.put("ReworkOrdreIDList", wReworkOrdreIDList);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, "MESQMS",
					StringUtils.Format("api/FPCRoute/DynamicTurnBop?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * http://10.200.3.7:8081/gxhspush/platform/platformPushMsg?userAccountList=012100010418&title=%E6%A0%87%E9%A2%98
	 * &content=%E6%B6%88%E6%81%AF%E5%86%85%E5%AE%B9&appName=com.crrcgzgs.portal&insideAppName=mail&type=1
	 * 
	 * @return
	 */
	@Override
	public BFCMessageResult BFC_MessageSend(BMSEmployee wLoginUser, String wLoginID, String wTitle, String wContent) {
		BFCMessageResult wResult = new BFCMessageResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("userAccountList", wLoginID);
			wParms.put("title", wTitle);
			wParms.put("content", wContent);
			wParms.put("appName", "com.crrcgzgs.portal");
			wParms.put("insideAppName", "mail");
			wParms.put("type", 1);

			@SuppressWarnings("unchecked")
			Map<String, Object> wMap = RemoteInvokeUtils.getInstance().HttpInvoke(
					"http://10.200.3.7:8081/gxhspush/platform/platformPushMsg", wParms, HttpMethod.GET, HashMap.class);

			wResult = CloneTool.Clone(wMap, BFCMessageResult.class);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
