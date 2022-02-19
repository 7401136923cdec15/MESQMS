package com.mes.qms.server.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.LOCOAPSService;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RemoteInvokeUtils;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-3-21 22:07:25
 * @LastEditTime 2020-3-21 22:07:31
 *
 */
@Service
public class LOCOAPSServiceImpl implements LOCOAPSService {
	private static Logger logger = LoggerFactory.getLogger(LOCOAPSServiceImpl.class);

	public LOCOAPSServiceImpl() {
	}

	private static LOCOAPSService Instance;

	public static LOCOAPSService getInstance() {
		if (Instance == null)
			Instance = new LOCOAPSServiceImpl();
		return Instance;
	}

	@Override
	public APIResult SFC_QueryTaskStepListByEmployee(BMSEmployee wLoginUser, Calendar wShiftDate) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String wShiftTime = wSimpleDateFormat.format(wShiftDate.getTime());
			wParms.put("ShiftDate", wShiftTime);

			String wUri = StringUtils.Format("api/SFCTaskStep/EmployeeAll?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_QueryTaskStepByID(BMSEmployee wLoginUser, int wSFCTaskStepID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wSFCTaskStepID);

			String wUri = StringUtils.Format("api/SFCTaskStep/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_QueryTaskStepByID(BMSEmployee wLoginUser, int wAPSTaskStepID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wAPSTaskStepID);

			String wUri = StringUtils.Format("api/APSTaskStep/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_UpdateTaskStep(BMSEmployee wLoginUser, APSTaskStep wAPSTaskStep) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wAPSTaskStep);

			String wUri = StringUtils.Format("api/APSTaskStep/Update?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);

			// 维护交车工位的状态
			List<APSTaskStep> wTaskStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, wAPSTaskStep.OrderID, 103, -1, null).List(APSTaskStep.class);
			if (wTaskStepList != null && wTaskStepList.stream().allMatch(p -> p.Status == 5)) {
				APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskPartByID(wLoginUser, wAPSTaskStep.TaskPartID).Info(APSTaskPart.class);
				if (wTaskPart != null && wTaskPart.ID > 0) {
					wTaskPart.FinishWorkTime = Calendar.getInstance();
					wTaskPart.Status = 5;
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wLoginUser, wTaskPart);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult OMS_QueryOrderByID(BMSEmployee wLoginUser, int wOrderID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wOrderID);
			String wUri = StringUtils.Format("api/OMSOrder/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult OMS_QueryOrderList(BMSEmployee wLoginUser, int wCommandID, String wOrderNo, int wLineID,
			int wProductID, int wBureauSectionID, String wPartNo, String wBOMNo, int wActive,
			List<Integer> wStatusList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("CommandID", wCommandID);
			wParms.put("OrderNo", wOrderNo);
			wParms.put("LineID", wLineID);
			wParms.put("ProductID", wProductID);
			wParms.put("BureauSectionID", wBureauSectionID);
			wParms.put("BOMNo", wBOMNo);
			wParms.put("PartNo", wPartNo);
			wParms.put("Active", wActive);
			if (wStatusList != null)
				wParms.put("StatusList", StringUtils.Join(",", wStatusList));
			String wUri = StringUtils.Format("api/OMSOrder/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_QueryTaskPartList(BMSEmployee wLoginUser, int wShiftID, int wShiftPeriod) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ShiftID", wShiftID);
			wParms.put("ShiftPeriod", wShiftPeriod);
			String wUri = StringUtils.Format("api/APSTaskPart/List?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_QueryTaskStepList(BMSEmployee wLoginUser, int wOrderID, int wStationID, int wTaskPartID,
			List<Integer> wStateIDList) {
		APIResult wResult = new APIResult();
		try {
			if (wStateIDList == null) {
				wStateIDList = new ArrayList<Integer>();
			}

			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("APSTaskPartID", wTaskPartID);
			wParms.put("OrderID", wOrderID);
			wParms.put("StationID", wStationID);
			wParms.put("StateIDList", wStateIDList);
			String wUri = StringUtils.Format("api/APSTaskStep/AllList?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_UpdateTaskStep(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wSFCTaskStep);
			String wUri = StringUtils.Format("api/SFCTaskStep/Update?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_QueryTaskStepList(BMSEmployee wLoginUser, int wAPSTaskStepID, int wShiftID, int wOperatorID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("TaskStepID", wAPSTaskStepID);
			wParms.put("ShiftID", wShiftID);
			wParms.put("OperatorID", wOperatorID);
			String wUri = StringUtils.Format("api/SFCTaskStep/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_QueryTaskPartByID(BMSEmployee wLoginUser, int wTaskPartID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("ID", wTaskPartID);
			String wUri = StringUtils.Format("api/APSTaskPart/Info?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_UpdateTaskPart(BMSEmployee wLoginUser, APSTaskPart wAPSTaskPart) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wAPSTaskPart);

			String wUri = StringUtils.Format("api/APSTaskPart/Update?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_QueryTaskPartList(BMSEmployee wLoginUser, List<Integer> wStateIDList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wStateIDList);

			String wUri = StringUtils.Format("api/APSTaskPart/StatusList?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult OMS_QueryRFOrderList(BMSEmployee wLoginUser, int wCustomerID, int wLineID, int wProductID,
			String wPartNo, Calendar wStartTime, Calendar wEndTime) {
		APIResult wResult = new APIResult();
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("CustomerID", wCustomerID);
			wParms.put("LineID", wLineID);
			wParms.put("ProductID", wProductID);
			wParms.put("PartNo", wPartNo);
			wParms.put("StartTime", wSDF.format(wStartTime.getTime()));
			wParms.put("EndTime", wSDF.format(wEndTime.getTime()));

			String wUri = StringUtils.Format("api/OMSOrder/RFOrderList?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult OMS_UpdateOrder(BMSEmployee wLoginUser, OMSOrder wOrder) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wOrder);

			String wUri = StringUtils.Format("api/OMSOrder/Update?cadv_ao={0}&cade_po={1}&company_id={2}",
					wLoginUser.LoginName, wLoginUser.Password, wLoginUser.CompanyID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public APIResult OMS_QueryOrderListByIDList(BMSEmployee wLoginUser, List<Integer> wIDList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();
			wParms.put("data", wIDList);

			String wUri = StringUtils.Format("api/OMSOrder/IDList?cadv_ao={0}&cade_po={1}&company_id={2}",
					new Object[] { wLoginUser.LoginName, wLoginUser.Password, Integer.valueOf(wLoginUser.CompanyID) });
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_QueryTaskStepList(BMSEmployee wLoginUser, List<Integer> wAPSTaskStepIDList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();
			wParms.put("data", wAPSTaskStepIDList);

			String wUri = StringUtils.Format(
					"api/SFCTaskStep/TaskAllByTaskStepIDList?cadv_ao={0}&cade_po={1}&company_id={2}",
					new Object[] { wLoginUser.LoginName, wLoginUser.Password, Integer.valueOf(wLoginUser.CompanyID) });
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QuerySecondmentList(BMSEmployee wLoginUser, int wOperateID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();
			wParms.put("PersonID", wOperateID);
			wParms.put("Type", -1);
			wParms.put("UpFlowID", -1);

			String wUri = StringUtils.Format("api/SCHSecondmentApply/History?cadv_ao={0}&cade_po={1}&company_id={2}",
					new Object[] { wLoginUser.LoginName, wLoginUser.Password, Integer.valueOf(wLoginUser.CompanyID) });
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SFC_DeleteTaskStepList(BMSEmployee wLoginUser, List<SFCTaskStep> wList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();
			wParms.put("data", wList);

			String wUri = StringUtils.Format("api/SFCTaskStep/DeleteAll?cadv_ao={0}&cade_po={1}&company_id={2}",
					new Object[] { wLoginUser.LoginName, wLoginUser.Password, Integer.valueOf(wLoginUser.CompanyID) });
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.POST);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_QueryWorkHour(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();

			String wUri = StringUtils.Format("api/APSTaskPart/WorkHours?cadv_ao={0}&cade_po={1}&company_id={2}",
					new Object[] { wLoginUser.LoginName, wLoginUser.Password, Integer.valueOf(wLoginUser.CompanyID) });
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_QueryTaskPartAll(BMSEmployee wLoginUser, int wOrderID, int wShiftPeriod) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();
			wParms.put("OrderID", wOrderID);
			wParms.put("ShiftPeriod", wShiftPeriod);

			String wUri = StringUtils.Format("api/APSTaskPart/All?cadv_ao={0}&cade_po={1}&company_id={2}",
					new Object[] { wLoginUser.LoginName, wLoginUser.Password, Integer.valueOf(wLoginUser.CompanyID) });
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, wUri, wParms,
					HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_BOMItemUpdate(BMSEmployee wLoginUser, APSBOMItem wAPSBOMItem) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wAPSBOMItem);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/APSBOM/Update?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_BOMItemDelete(BMSEmployee wLoginUser, APSBOMItem wBOMItem) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wBOMItem);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/APSBOM/Delete?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_BOMItemUpdateProperty(BMSEmployee wLoginUser, APSBOMItem wBOMItem) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wBOMItem);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/APSBOM/UpdateProperty?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult Andon_QueryProductStatus(BMSEmployee wLoginUser) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Andon/ProductionStatus?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_EmployeePartList(BMSEmployee wLoginUser, int wClassID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("APSTaskStepID", Integer.MAX_VALUE);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SFCTaskStep/PGEmployeeList?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}
}
