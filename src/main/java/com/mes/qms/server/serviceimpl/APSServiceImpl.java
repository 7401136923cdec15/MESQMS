package com.mes.qms.server.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.APSService;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSMaterialReturn;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.excel.MyExcelSheet;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.aps.APSMaterialReturnDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTaskIPTDAO;
import com.mes.qms.server.utils.RemoteInvokeUtils;
import com.mes.qms.server.utils.qms.ExcelUtil;

@Service
public class APSServiceImpl implements APSService {

	private static Logger logger = LoggerFactory.getLogger(APSServiceImpl.class);

	private static APSService Instance = new APSServiceImpl();

	public static APSService getInstance() {

		return Instance;
	}

	@Override
	public APIResult SCH_QueryWorkerListByShiftID(BMSEmployee wLoginUser, int wWorkShopID, int wLineID,
			int wFunctionModule, int wShiftID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("WorkShopID", wWorkShopID);
			wParms.put("LineID", wLineID);
			wParms.put("ModuleID", wFunctionModule);
			wParms.put("ShiftID", wShiftID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHWorker/ShiftAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QuerySubWorkerListByLoginID(BMSEmployee wLoginUser, int wEventID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("EventID", wEventID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHWorker/SubWorker?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryLeadWorkerByUserID(BMSEmployee wLoginUser, int wUserID, int wWorkShopID, boolean wIsTop) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("WorkShopID", wWorkShopID);
			wParms.put("ID", wUserID);
			wParms.put("IsTop", wIsTop);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHWorker/LeadWorker?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryWorkerByPositionID(BMSEmployee wLoginUser, int wPositionID, int wWorkShopID,
			int wShiftID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("WorkShopID", wWorkShopID);
			wParms.put("PositionID", wPositionID);
			wParms.put("ShiftID", wShiftID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHWorker/PositionWorker?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryLeadWorkerByPositionID(BMSEmployee wLoginUser, int wPositionID, int wWorkShopID,
			int wShiftID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("WorkShopID", wWorkShopID);
			wParms.put("PositionID", wPositionID);
			wParms.put("ShiftID", wShiftID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHWorker/PositionLeader?cadv_ao={0}&cade_po={1}",
							wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryPositionListByDeviceID(BMSEmployee wLoginUser, int wDeviceID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("DeviceID", wDeviceID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHWorker/DeviceAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryPositionListByStationID(BMSEmployee wLoginUser, int wStationID, int wEventID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("StationID", wStationID);
			wParms.put("EventID", wEventID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHPosition/StationAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryPositionListByLoginID(BMSEmployee wLoginUser, int wEventID, boolean wIncludeSub) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("IncludeSub", wIncludeSub);
			wParms.put("EventID", wEventID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHPosition/OwnAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryPositionListByShiftID(BMSEmployee wLoginUser, int wLineID, int wWorkShopID,
			int wPositionLevel, int wShiftID, boolean wFillShift) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("LineID", wLineID);
			wParms.put("WorkShopID", wWorkShopID);
			wParms.put("ShiftID", wShiftID);
			wParms.put("PositionLevel", wPositionLevel);
			wParms.put("FillShift", wFillShift);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHPosition/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryPositionListByShiftID(BMSEmployee wLoginUser, int wShiftID, int wWorkShopID,
			int wStationID, int wEventID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("LineID", wShiftID);
			wParms.put("WorkShopID", wWorkShopID);
			wParms.put("StationID", wStationID);
			wParms.put("EventID", wEventID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHPosition/EventAll?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult SCH_QueryShiftID(BMSEmployee wLoginUser, int wWorkShopID, int wShiftPeriod, int wShifts) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("shift_id", wShifts);
			wParms.put("ShiftPeriod", wShiftPeriod);
			wParms.put("WorkShopID", wWorkShopID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/SCHShift/CurrentShiftID?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<APSMaterialReturn>> APS_QueryMaterialReturnList(BMSEmployee wLoginUser, String wWBSNo,
			int wPartID, int wStepID) {
		ServiceResult<List<APSMaterialReturn>> wResult = new ServiceResult<List<APSMaterialReturn>>();
		wResult.Result = new ArrayList<APSMaterialReturn>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = APSMaterialReturnDAO.getInstance().APS_QueryBOMItemList(wLoginUser, -1, wWBSNo, "", -1, -1,
					-1, wPartID, wStepID, -1, "", -1, -1, -1, null, -1, -1, -1, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportReturnMaterialList(BMSEmployee wLoginUser, List<APSMaterialReturn> wList) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			MyExcelSheet wMyExcelSheet = GetMyExcelSheet(wList);

			List<MyExcelSheet> wMyExcelSheetList = new ArrayList<MyExcelSheet>(Arrays.asList(wMyExcelSheet));

			wResult.Result = ExcelUtil.ExportData(wMyExcelSheetList, "退料清单");

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取退料清单表格数据
	 */
	private MyExcelSheet GetMyExcelSheet(List<APSMaterialReturn> wList) {
		MyExcelSheet wResult = new MyExcelSheet();
		try {
			wResult.HeaderList = new ArrayList<String>(
					Arrays.asList("车型", "车号", "台位", "工序描述", "物料", "物料描述", "数量", "基本计量单位", "必换(1)/偶换(2)", "预定的项目编号"));

			wResult.SheetName = "退料清单";

			wResult.TitleName = "";

			wResult.DataList = new ArrayList<List<String>>();

			for (APSMaterialReturn wAPSMaterialReturn : wList) {
				List<String> wValueList = new ArrayList<String>();

				// ①车型
				wValueList.add(wAPSMaterialReturn.ProductNo);
				// ②车号
				wValueList.add(wAPSMaterialReturn.PartNo.split("#")[1]);
				// ③台位
				wValueList.add(wAPSMaterialReturn.PartName);
				// ④工序描述
				wValueList.add(wAPSMaterialReturn.PartPointName);
				// ⑤物料
				wValueList.add(wAPSMaterialReturn.MaterialNo);
				// ⑥物料描述
				wValueList.add(wAPSMaterialReturn.MaterialName);
				// ⑦数量
				wValueList.add(wAPSMaterialReturn.Number.toString());
				// ⑧基本计量单位
				wValueList.add(wAPSMaterialReturn.UnitText.toString());
				// ⑨必换(1)/偶换(2)
				wValueList.add(String.valueOf(wAPSMaterialReturn.ReplaceType));
				// ⑩预定的项目编号
				wValueList.add(wAPSMaterialReturn.WBSNo);

				wResult.DataList.add(wValueList);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> APS_UpdateTaskPartStartWorkTime(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①查询开工时间为空的周计划
			List<Integer> wTaskPartIDList = SFCTaskIPTDAO.getInstance().APS_QueryTaskPartIDList(wLoginUser, wErrorCode);
			// ②遍历查询开始时间
			for (int wTaskPartID : wTaskPartIDList) {
				Calendar wMinTime = SFCTaskIPTDAO.getInstance().APS_QueryMinStartTime(wLoginUser, wTaskPartID,
						wErrorCode);
				Calendar wBaseTime = Calendar.getInstance();
				wBaseTime.set(2010, 0, 1, 0, 0, 0);
				if (wMinTime.compareTo(wBaseTime) < 0) {
					continue;
				}
				// ③赋值
				APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance().APS_QueryTaskPartByID(wLoginUser, wTaskPartID)
						.Info(APSTaskPart.class);
				if (wTaskPart == null || wTaskPart.ID <= 0) {
					continue;
				}
				wTaskPart.StartWorkTime = wMinTime;
				LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wLoginUser, wTaskPart);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}
}
