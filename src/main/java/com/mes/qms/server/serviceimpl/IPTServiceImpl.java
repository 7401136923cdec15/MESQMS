package com.mes.qms.server.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.IPTService;
import com.mes.qms.server.service.mesenum.APSBOMSourceType;
import com.mes.qms.server.service.mesenum.APSOperateType;
import com.mes.qms.server.service.mesenum.APSShiftPeriod;
import com.mes.qms.server.service.mesenum.APSTaskStatus;
import com.mes.qms.server.service.mesenum.BFCMessageStatus;
import com.mes.qms.server.service.mesenum.BFCMessageType;
import com.mes.qms.server.service.mesenum.BMSDepartmentType;
import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.FMCShiftLevel;
import com.mes.qms.server.service.mesenum.FPCPartTypes;
import com.mes.qms.server.service.mesenum.IMPResult;
import com.mes.qms.server.service.mesenum.IMPSameType;
import com.mes.qms.server.service.mesenum.IMPType;
import com.mes.qms.server.service.mesenum.IPTItemType;
import com.mes.qms.server.service.mesenum.IPTMode;
import com.mes.qms.server.service.mesenum.IPTOrderReportPartType;
import com.mes.qms.server.service.mesenum.IPTPreCheckProblemStatus;
import com.mes.qms.server.service.mesenum.IPTPreCheckRecordType;
import com.mes.qms.server.service.mesenum.IPTPreCheckReportStatus;
import com.mes.qms.server.service.mesenum.IPTProblemActionType;
import com.mes.qms.server.service.mesenum.IPTStandardBPMStatus;
import com.mes.qms.server.service.mesenum.IPTStandardStatus;
import com.mes.qms.server.service.mesenum.IPTStandardType;
import com.mes.qms.server.service.mesenum.LFSOperationLogType;
import com.mes.qms.server.service.mesenum.LFSStationType;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.OMSOrderStatus;
import com.mes.qms.server.service.mesenum.RSMTurnOrderTaskStatus;
import com.mes.qms.server.service.mesenum.SFCTaskStatus;
import com.mes.qms.server.service.mesenum.SFCTaskStepType;
import com.mes.qms.server.service.mesenum.SFCTaskType;
import com.mes.qms.server.service.mesenum.TagTypes;
import com.mes.qms.server.service.mesenum.TaskQueryType;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bfc.BFCAuditConfig;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.bms.BMSDepartment;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.cfg.CFGCalendar;
import com.mes.qms.server.service.po.cfg.CFGUnit;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.excel.ExcelLineData;
import com.mes.qms.server.service.po.excel.ExcelSheetData;
import com.mes.qms.server.service.po.fmc.FMCLineUnit;
import com.mes.qms.server.service.po.fmc.FMCWorkCharge;
import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.fpc.FPCPartPoint;
import com.mes.qms.server.service.po.fpc.FPCProduct;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.po.imp.IMPErrorRecord;
import com.mes.qms.server.service.po.imp.IMPResultRecord;
import com.mes.qms.server.service.po.ipt.IPTCheckRecord;
import com.mes.qms.server.service.po.ipt.IPTConfigs;
import com.mes.qms.server.service.po.ipt.IPTConstants;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTItemApply;
import com.mes.qms.server.service.po.ipt.IPTItemC;
import com.mes.qms.server.service.po.ipt.IPTOrderReport;
import com.mes.qms.server.service.po.ipt.IPTOrderReportPart;
import com.mes.qms.server.service.po.ipt.IPTOrderReportPartPoint;
import com.mes.qms.server.service.po.ipt.IPTPreCheckItem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblemCar;
import com.mes.qms.server.service.po.ipt.IPTPreCheckReport;
import com.mes.qms.server.service.po.ipt.IPTProblemAssess;
import com.mes.qms.server.service.po.ipt.IPTProblemBomItem;
import com.mes.qms.server.service.po.ipt.IPTProblemConfirmer;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.ipt.IPTSolveLib;
import com.mes.qms.server.service.po.ipt.IPTStandard;
import com.mes.qms.server.service.po.ipt.IPTStandardBPM;
import com.mes.qms.server.service.po.ipt.IPTStandardC;
import com.mes.qms.server.service.po.ipt.IPTTool;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.lfs.LFSOperationLog;
import com.mes.qms.server.service.po.lfs.LFSWorkAreaStation;
import com.mes.qms.server.service.po.mss.MSSBOM;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.mss.MSSMaterial;
import com.mes.qms.server.service.po.ncr.NCRTask;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.record.IPTCheckResult;
import com.mes.qms.server.service.po.record.IPTExportCheckRecord;
import com.mes.qms.server.service.po.rro.RROItemTask;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.qms.server.service.po.sfc.SFCLoginEvent;
import com.mes.qms.server.service.po.sfc.SFCOrderForm;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.Configuration;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.imp.IMPErrorRecordDAO;
import com.mes.qms.server.serviceimpl.dao.imp.IMPResultRecordDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTCheckRecordDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTCheckResultDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTItemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTOrderReportDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTOrderReportPartDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTOrderReportPartPointDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTPreCheckItemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTPreCheckProblemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTPreCheckReportDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTProblemAssessDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTProblemConfirmerDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTSOPDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTSolveLibDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTStandardBPMDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTStandardDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTToolDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTValueDAO;
import com.mes.qms.server.serviceimpl.dao.mss.MSSPartRecordDAO;
import com.mes.qms.server.serviceimpl.dao.rsm.RSMTurnOrderTaskDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCBogiesChangeBPMDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCOrderFormDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTaskIPTDAO;
import com.mes.qms.server.serviceimpl.utils.MESServer;
import com.mes.qms.server.serviceimpl.utils.qms.QMSUtils;
import com.mes.qms.server.serviceimpl.utils.qms.NewCreditReportUtil;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import com.mes.qms.server.serviceimpl.utils.qms.SQLStringUtils;
import com.mes.qms.server.shristool.LoggerTool;
import com.mes.qms.server.utils.Constants;
import com.mes.qms.server.utils.RetCode;
import com.mes.qms.server.utils.qms.ExcelUtil;
import com.mes.qms.server.utils.qms.MESFileUtils;
import com.mes.qms.server.utils.qms.ZipUtils;

@Service
public class IPTServiceImpl implements IPTService {
	private static Logger logger = LoggerFactory.getLogger(IPTServiceImpl.class);

	private static IPTService _instance;

	public static IPTService getInstance() {
		if (_instance == null)
			_instance = new IPTServiceImpl();

		return _instance;
	}

	@Override
	public ServiceResult<String> IPT_SetIPTConfig(BMSEmployee wLoginUser, IPTConfigs wIPTConfigs) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			if (wIPTConfigs.getTOPNum() < IPTConstants.getIPTConfigsObject().getTOPNumMin())
				wIPTConfigs.setTOPNum(IPTConstants.getIPTConfigsObject().getTOPNumMin());
			if (wIPTConfigs.getTOPNum() > IPTConstants.getIPTConfigsObject().getTOPNumMax())
				wIPTConfigs.setTOPNum(IPTConstants.getIPTConfigsObject().getTOPNumMax());

			IPTConstants.getIPTConfigsObject().setTOPNum(wIPTConfigs.getTOPNum());

			if (wIPTConfigs.getKeepDay() < IPTConstants.getIPTConfigsObject().getKeepDayMin())
				wIPTConfigs.setKeepDay(IPTConstants.getIPTConfigsObject().getKeepDayMin());

			IPTConstants.getIPTConfigsObject().setKeepDay(wIPTConfigs.getKeepDay());

			IPTConstants.setIPTConfigsObject(IPTConstants.getIPTConfigsObject());

		} catch (Exception ex) {
			wResult.FaultCode += ex.toString();
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTConfigs> IPT_GetIPTConfig(BMSEmployee wLoginUser) {
		ServiceResult<IPTConfigs> wResult = new ServiceResult<IPTConfigs>();
		try {
			wResult.setResult(IPTConstants.getIPTConfigsObject());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTStandard> IPT_InsertStandard(BMSEmployee wLoginUser, IPTStandard wIPTStandard) {
		ServiceResult<IPTStandard> wResult = new ServiceResult<IPTStandard>();
		wResult.Result = new IPTStandard();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// 自动维护版本号
			if (StringUtils.isEmpty(wIPTStandard.Version)) {
				wIPTStandard.Version = IPTStandardDAO.getInstance().SelectNewVersion_V2(wLoginUser,
						wIPTStandard.ProductID, wIPTStandard.LineID, wIPTStandard.CustomID, wIPTStandard.PartID,
						wIPTStandard.PartPointID, wErrorCode);
			}

			wIPTStandard.TModify = Calendar.getInstance();
			ServiceResult<Long> wServiceResult = IPTStandardDAO.getInstance().InsertStandard(wLoginUser, wIPTStandard,
					wErrorCode);
			String wFaultCode = wServiceResult.FaultCode;
			if (wFaultCode != null & wFaultCode.trim().length() > 0)
				return wResult;
			List<String> wSqlString = new ArrayList<String>();

			wSqlString.add(SQLStringUtils.getInstance().InsertItem(wLoginUser, wIPTStandard.ItemList, wIPTStandard.ID,
					wErrorCode));

			IPTStandardDAO.getInstance().ExecuteSqlTransaction(wSqlString);

			wResult.Result = wIPTStandard;

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> IPT_SaveStandard(BMSEmployee wLoginUser, IPTStandard wIPTStandard) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<String> wSqlString = new ArrayList<String>();

			if (wIPTStandard.ItemList == null)
				wIPTStandard.ItemList = new ArrayList<IPTItem>();

			wSqlString.add(SQLStringUtils.getInstance().DeleteItem(wLoginUser, wIPTStandard.ItemList, wIPTStandard.ID,
					wErrorCode));
			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
			if (wIPTStandard.ItemList.size() > 0) {

				List<IPTItem> wItemListTemp = wIPTStandard.ItemList.stream().filter(p -> p.ID > 0)
						.collect(Collectors.toList());
				wSqlString.addAll(SQLStringUtils.getInstance().UpdateItem(wLoginUser, wItemListTemp, wIPTStandard.ID,
						wErrorCode));
				wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
				wItemListTemp = wIPTStandard.ItemList.stream().filter(p -> p.ID <= 0).collect(Collectors.toList());

				wSqlString.add(SQLStringUtils.getInstance().InsertItem(wLoginUser, wItemListTemp, wIPTStandard.ID,
						wErrorCode));
				wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
			}

			wSqlString.add(SQLStringUtils.getInstance().UpdateStandardDetail(wLoginUser, wIPTStandard.ID,
					wIPTStandard.UserID, wErrorCode));

			wSqlString.removeIf(p -> StringUtils.isEmpty(p));

			IPTStandardDAO.getInstance().ExecuteSqlTransaction(wSqlString);

			wIPTStandard.TModify = Calendar.getInstance();
			IPTStandardDAO.getInstance().Update(wLoginUser, wIPTStandard, wErrorCode);

			// 预检可以修改
			if (wIPTStandard.IPTMode == IPTMode.PreCheck.getValue()) {
				IPTStandardDAO.getInstance().Update(wLoginUser, wIPTStandard, wErrorCode);
				wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
			}
		} catch (Exception ex) {
			wResult.setResult(wResult.Result + ex.toString());
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> IPT_SaveStandardState(BMSEmployee wLoginUser, long wStandardID, int wIsCurrent) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			ServiceResult<IPTStandard> wServiceResult = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wStandardID, wErrorCode);

			IPTStandard wIPTStandard = wServiceResult.Result;
			wResult.setFaultCode(wServiceResult.getFaultCode());

			if (wIPTStandard.ID < 0) {
				wResult.setResult(StringUtils.Format("未找到ID={0} 的标准。", wStandardID));
				return wResult;
			}
			List<String> wSqlString = new ArrayList<String>();

			if (wIsCurrent == 1) {
				wSqlString.add(SQLStringUtils.getInstance().UpdateStandardCurrent(wLoginUser, wIPTStandard.CompanyID,
						wIPTStandard.IPTMode, wIPTStandard.WorkShopID, wIPTStandard.LineID, wIPTStandard.PartID,
						wIPTStandard.PartPointID, wIPTStandard.ProductNo, 0, wIPTStandard.CustomID, wErrorCode));

				List<IPTStandard> wSList = IPT_SelectStandardList(wLoginUser, -1,
						IPTMode.getEnumType(wIPTStandard.IPTMode), wIPTStandard.CustomID, -1, -1, -1, -1,
						wIPTStandard.LineID, wIPTStandard.PartID, wIPTStandard.PartPointID, -1, wIPTStandard.ProductID,
						"").Result;
				if (wSList != null && wSList.size() > 0) {
					wSList = (List<IPTStandard>) wSList.stream().filter(p -> (p.ID != wIPTStandard.ID))
							.collect(Collectors.toList());
					wSList.forEach(p -> {
						p.IsCurrent = 0;
					});
					for (IPTStandard wItem : wSList) {
						IPTStandardDAO.getInstance().Update(wLoginUser, wItem, wErrorCode);
					}
				}
			}
			wSqlString.add(SQLStringUtils.getInstance().UpdateStandardCurrent(wLoginUser, wStandardID, wIsCurrent,
					wErrorCode));

			IPTStandardDAO.getInstance().ExecuteSqlTransaction(wSqlString);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.setResult(wResult.Result + e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> IPT_DeleteStandard(BMSEmployee wLoginUser, long wStandardID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<String> wSqlString = new ArrayList<String>();

			wSqlString.add(
					SQLStringUtils.getInstance().DeleteStandardItemByStandardID(wLoginUser, wStandardID, wErrorCode));
			wSqlString.add(SQLStringUtils.getInstance().DeleteStandardByID(wLoginUser, wStandardID, wErrorCode));

			IPTStandardDAO.getInstance().ExecuteSqlTransaction(wSqlString);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.setResult(wResult.Result + e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTStandard> IPT_GetStandard(BMSEmployee wLoginUser, long wStandardID) {
		ServiceResult<IPTStandard> wResult = new ServiceResult<IPTStandard>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			ServiceResult<IPTStandard> wServiceResult = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wStandardID, wErrorCode);
			wResult.setResult(wServiceResult.Result);
			wResult.setFaultCode(wServiceResult.getFaultCode());
			String wFaultCode = wResult.getFaultCode();

			if (wFaultCode != null && wFaultCode.trim().length() > 0) {
				return wResult;
			}

			ServiceResult<Map<Long, List<IPTItem>>> wServiceResult1 = IPTStandardDAO.getInstance()
					.SelectItem(wLoginUser, new ArrayList<Long>(Arrays.asList(wResult.Result.ID)), wErrorCode);
			wResult.setFaultCode(wServiceResult1.getFaultCode());
			Map<Long, List<IPTItem>> wIPTItemList = wServiceResult1.Result;
			if (wFaultCode != null && wFaultCode.trim().length() > 0) {
				return wResult;
			}

			wResult.Result.ItemList = wIPTItemList.get(wResult.Result.ID);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.setFaultCode(wResult.getFaultCode() + e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTStandard> IPT_GetStandardCurrent(BMSEmployee wLoginUser, int wCompanyID, IPTMode wIPTMode,
			int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID, int wLineID, int wPartID,
			int wPartPointID, int wStationID, String wProductNo) {
		ServiceResult<IPTStandard> wResult = new ServiceResult<IPTStandard>();
		String wFaultCode = "";
		IPTStandard wIPTStandard = new IPTStandard();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			ServiceResult<IPTStandard> wServiceResult = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(
					wLoginUser, wCompanyID, wIPTMode, wCustomID, wBusinessUnitID, wBaseID, wFactoryID, wWorkShopID,
					wLineID, wPartID, wPartPointID, wStationID, wProductNo, wErrorCode);
			wFaultCode = wServiceResult.FaultCode;
			wIPTStandard = wServiceResult.Result;

			if (wFaultCode != null && wFaultCode.trim().length() > 0) {
				wResult.FaultCode = wFaultCode;
				return wResult;
			}

			Calendar wCalendar = (Calendar) wIPTStandard.TModify.clone();
			wCalendar.add(Calendar.DATE, IPTConstants.getIPTConfigsObject().getKeepDay());
//			if (wCalendar.compareTo(Calendar.getInstance()) < 0 && wIPTStandard.IsEnd < 1) {
//				IPT_SaveStandardState(wLoginUser, wIPTStandard.ID, 0);
//				wIPTStandard.IsCurrent = 0;
//				wResult.Result = wIPTStandard;
//				return wResult;
//			}

			List<Long> wIDList = new ArrayList<Long>();
			wIDList.add(wIPTStandard.getID());
			ServiceResult<Map<Long, List<IPTItem>>> wServiceResult1 = IPTStandardDAO.getInstance()
					.SelectItem(wLoginUser, wIDList, wErrorCode);
			wFaultCode = wServiceResult1.FaultCode;
			Map<Long, List<IPTItem>> wIPTItemList = wServiceResult1.Result;

			if (wFaultCode != null && wFaultCode.trim().length() > 0) {
				wResult.FaultCode = wFaultCode;
				wResult.Result = wIPTStandard;
				return wResult;
			}

			wIPTStandard.ItemList = wIPTItemList.get(wIPTStandard.ID);
			wResult.Result = wIPTStandard;

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.setFaultCode(wResult.getFaultCode() + e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTStandard> IPT_GetStandardCurrentByProductID(BMSEmployee wLoginUser, int wCompanyID,
			IPTMode wIPTMode, int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID,
			int wLineID, int wPartID, int wPartPointID, int wStationID, int wProductID) {
		ServiceResult<IPTStandard> wResult = new ServiceResult<IPTStandard>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			ServiceResult<IPTStandard> wServiceResult = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(
					wLoginUser, wCompanyID, wIPTMode, wCustomID, wBusinessUnitID, wBaseID, wFactoryID, wWorkShopID,
					wLineID, wPartID, wPartPointID, wStationID, wProductID, wErrorCode);
			wResult.Result = wServiceResult.Result;

			if (wServiceResult.FaultCode != null && wServiceResult.FaultCode.trim().length() > 0) {
				wResult.FaultCode = wServiceResult.FaultCode;
				return wResult;
			}

			Calendar wCalendar = (Calendar) wResult.Result.TModify.clone();
			wCalendar.add(Calendar.DATE, IPTConstants.getIPTConfigsObject().getKeepDay());
			if (wCalendar.compareTo(Calendar.getInstance()) < 0 && wResult.Result.IsEnd < 1) {
				IPT_SaveStandardState(wLoginUser, wResult.Result.ID, 0);
				wResult.Result.IsCurrent = 0;
				return wResult;
			}

			List<Long> wIDList = new ArrayList<Long>();
			wIDList.add(wResult.Result.ID);
			ServiceResult<Map<Long, List<IPTItem>>> wServiceResult1 = IPTStandardDAO.getInstance()
					.SelectItem(wLoginUser, wIDList, wErrorCode);
			wResult.FaultCode = wServiceResult1.FaultCode;
			Map<Long, List<IPTItem>> wIPTItemList = wServiceResult1.Result;

			if (wResult.FaultCode != null && wResult.FaultCode.trim().length() > 0) {
				return wResult;
			}

			wResult.Result.ItemList = wIPTItemList.get(wResult.Result.ID);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTStandard>> IPT_GetStandardListByTime(BMSEmployee wLoginUser, int wCompanyID,
			IPTMode wIPTMode, int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID,
			int wLineID, int wPartID, int wPartPointID, int wStationID, int wProductID, String wProductNo,
			Calendar wTimeS, Calendar wTimeE) {
		ServiceResult<List<IPTStandard>> wResult = new ServiceResult<List<IPTStandard>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			ServiceResult<List<IPTStandard>> wServiceResult = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wCompanyID, wIPTMode, wCustomID, wBusinessUnitID, wBaseID, wFactoryID, wWorkShopID, wLineID,
					wPartID, wPartPointID, wStationID, wProductID, wProductNo,
					IPTConstants.getIPTConfigsObject().getTOPNum(), wTimeS, wTimeE, wErrorCode);
			wResult.FaultCode = wServiceResult.FaultCode;
			wResult.Result = wServiceResult.Result;

			if (wResult.FaultCode != null && wResult.FaultCode.trim().length() > 0) {
				return wResult;
			}

			ServiceResult<Map<Long, List<IPTItem>>> wServiceResult1 = IPTStandardDAO.getInstance().SelectItem(
					wLoginUser, wResult.Result.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);
			Map<Long, List<IPTItem>> wIPTItemList = wServiceResult1.Result;

			for (IPTStandard wIPTStandard : wResult.Result) {
				if (wIPTItemList.containsKey(wIPTStandard.ID)) {
					wIPTStandard.ItemList = wIPTItemList.get(wIPTStandard.ID);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTStandard>> IPT_SelectStandardList(BMSEmployee wLoginUser, int wCompanyID,
			IPTMode wIPTMode, int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID,
			int wLineID, int wPartID, int wPartPointID, int wStationID, int wProductID, String wProductNo) {
		ServiceResult<List<IPTStandard>> wResult = new ServiceResult<List<IPTStandard>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			Calendar wCalendar = Calendar.getInstance();
			wCalendar.set(1, 1, 1);
			ServiceResult<List<IPTStandard>> wServiceResult = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wCompanyID, wIPTMode, wCustomID, wBusinessUnitID, wBaseID, wFactoryID, wWorkShopID, wLineID,
					wPartID, wPartPointID, wStationID, wProductID, wProductNo,
					IPTConstants.getIPTConfigsObject().getTOPNum(), wCalendar, wCalendar, wErrorCode);
			wResult.FaultCode = wServiceResult.FaultCode;
			wResult.Result = wServiceResult.Result;

			if (wResult.FaultCode != null && wResult.FaultCode.trim().length() > 0) {
				return wResult;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTStandard>> IPT_GetStandardListCurrent(BMSEmployee wLoginUser, int wCompanyID,
			IPTMode wIPTMode, int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID,
			int wLineID, int wPartID, int wProductID, String wProductNo, int wNum) {
		ServiceResult<List<IPTStandard>> wResult = new ServiceResult<List<IPTStandard>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			ServiceResult<List<IPTStandard>> wServiceResult = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(
					wLoginUser, wCompanyID, wIPTMode, wCustomID, wBusinessUnitID, wBaseID, wFactoryID, wWorkShopID,
					wLineID, wPartID, wProductID, wProductNo, wNum, wErrorCode);
			wResult.Result = wServiceResult.Result;
			wResult.FaultCode = wServiceResult.FaultCode;
			if (wResult.FaultCode != null && wResult.FaultCode.trim().length() > 0) {
				return wResult;
			}
			List<IPTStandard> wTemp = new ArrayList<IPTStandard>();
			for (IPTStandard wItem : wResult.Result) {
				Calendar wCalendar = (Calendar) wItem.TModify.clone();
				wCalendar.add(Calendar.DATE, IPTConstants.getIPTConfigsObject().getKeepDay());
				if (wCalendar.compareTo(Calendar.getInstance()) < 0 && wItem.IsEnd != 1) {
					wTemp.add(wItem);
				}
			}

			List<IPTStandard> wNewTemp = new ArrayList<IPTStandard>();
			for (IPTStandard wItem : wResult.Result) {
				if (!wTemp.contains(wItem)) {
					wNewTemp.add(wItem);
				}
			}
			wResult.Result = wNewTemp;

			List<String> wSqlString = new ArrayList<String>();

			for (int i = 0; i < wTemp.size(); i++) {
				wSqlString.add(
						SQLStringUtils.getInstance().UpdateStandardCurrent(wLoginUser, wTemp.get(i).ID, 0, wErrorCode));
			}

			IPTStandardDAO.getInstance().ExecuteSqlTransaction(wSqlString);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> IPT_SaveStandardEnd(BMSEmployee wLoginUser, long wStandardID, int wIsEnd) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			ServiceResult<IPTStandard> wServiceResult = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wStandardID, wErrorCode);
			wResult.FaultCode = wServiceResult.FaultCode;
			IPTStandard wIPTStandard = wServiceResult.Result;
			if (wIPTStandard.ID < 0) {
				wResult.FaultCode = StringUtils.Format("未找到ID={0} 的标准。", String.valueOf(wStandardID));
				return wResult;
			}
			List<String> wSqlString = new ArrayList<String>();

			if (wIsEnd == 1) {
				if (wIPTStandard.IsCurrent != 1) {
					wResult.FaultCode = StringUtils.Format("ID={0} 的标准不是当前标准。", String.valueOf(wStandardID));
					return wResult;
				}
			}
			wSqlString.add(SQLStringUtils.getInstance().UpdateStandardEnd(wLoginUser, wStandardID, wIsEnd, wErrorCode));

			IPTStandardDAO.getInstance().ExecuteSqlTransaction(wSqlString);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_SaveIPTValue(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList, int wTaskID,
			int wEventID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			if (wIPTValueList == null || wIPTValueList.size() < 1)
				return wResult;
			List<String> wSqlString = new ArrayList<String>();

			List<IPTValue> wInsetIPTValueList = wIPTValueList.stream().filter(p -> p.ID <= 0)
					.collect(Collectors.toList());

			List<IPTValue> wUpdateIPTValueList = wIPTValueList.stream().filter(p -> p.ID > 0)
					.collect(Collectors.toList());

			wSqlString.add(SQLStringUtils.getInstance().InsertIPTValue(wLoginUser, wInsetIPTValueList, wTaskID,
					wEventID, wErrorCode));

			wSqlString.addAll(SQLStringUtils.getInstance().UpdateIPTValue(wLoginUser, wUpdateIPTValueList, wTaskID,
					wEventID, wErrorCode));

			wSqlString.removeIf(p -> StringUtils.isEmpty(p));

			if (wSqlString == null || wSqlString.size() < 1)
				return wResult;

			IPTStandardDAO.getInstance().ExecuteSqlTransaction(wSqlString);
			wResult = IPT_BadResonAI(wLoginUser, wIPTValueList);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_BadResonAI(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			if (wIPTValueList == null || wIPTValueList.size() < 1)
				return wResult;

			Long wStandardID = 0L;
			if (wIPTValueList.size() > 0)
				wStandardID = wIPTValueList.get(0).StandardID;
			if (wStandardID <= 0)
				return wResult;

			List<Long> wIDList = new ArrayList<Long>();
			wIDList.add((long) wStandardID);

			ServiceResult<Map<Long, List<IPTItem>>> wServiceResult = IPTStandardDAO.getInstance().SelectItem(wLoginUser,
					wIDList, wErrorCode);
			wResult.FaultCode = wServiceResult.FaultCode;
			Map<Long, List<IPTItem>> wIPTItemListDic = wServiceResult.Result;

			if (wIPTItemListDic == null || !wIPTItemListDic.containsKey(wStandardID))
				return wResult;

			List<IPTItem> wIPTItemList = wIPTItemListDic.get(wStandardID).stream()
					.filter(p -> p.StandardType == IPTStandardType.BadReason.getValue()).collect(Collectors.toList());

			if (wIPTItemList == null || wIPTItemList.size() <= 0)
				return wResult;
			Map<Long, IPTItem> wIPTItemDic = new HashMap<Long, IPTItem>();
			for (IPTItem wItem : wIPTItemList) {
				wIPTItemDic.put(wItem.ID, wItem);
			}
			wIPTItemList = null;
			wIPTItemList = new ArrayList<IPTItem>();
			for (IPTValue wIPTValue : wIPTValueList) {
				if (!wIPTItemDic.containsKey(wIPTValue.IPTItemID))
					continue;

				if (wIPTItemDic.get(wIPTValue.IPTItemID).ValueSource.contains(wIPTValue.Value))
					continue;

				wIPTItemDic.get(wIPTValue.IPTItemID).ValueSource.add(wIPTValue.Value);
				wIPTItemList.add(wIPTItemDic.get(wIPTValue.IPTItemID));
			}
			if (wIPTItemList.size() > 0) {
				List<String> wSqlString = SQLStringUtils.getInstance().UpdateItem(wLoginUser, wIPTItemList, wStandardID,
						wErrorCode);
				IPTStandardDAO.getInstance().ExecuteSqlTransaction(wSqlString);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTValue>> IPT_GetIPTValueByTaskID(BMSEmployee wLoginUser, int wTaskID, int wEventID,
			int wItemType) {
		ServiceResult<List<IPTValue>> wResult = new ServiceResult<List<IPTValue>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wTaskID, wEventID, wItemType, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Map<Integer, List<IPTValue>>> IPT_GetIPTValue(BMSEmployee wLoginUser,
			List<Integer> wTaskIDList, int wEventID, int wItemType) {
		ServiceResult<Map<Integer, List<IPTValue>>> wResult = new ServiceResult<Map<Integer, List<IPTValue>>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wTaskIDList, wEventID, wItemType,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListByShiftID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wTaskType, int wShiftID) {
		ServiceResult<List<SFCTaskIPT>> wResult = new ServiceResult<List<SFCTaskIPT>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);
//			wResult.Result = SFCIPTDAO.getInstance().SFC_QueryTaskIPTListByShiftID(wCompanyID, wLoginID, wTaskType,
//					wShiftID, wErrorCode);
//			wResult.FaultCode = MESServiceDAO.getInstance().MES_QueryErrorText(wErrorCode.get());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCTaskIPT> SFC_QueryTaskIPTByID(BMSEmployee wLoginUser, int wCompanyID, int wLoginID,
			int wID) {
		// TODO Auto-generated method stub
		ServiceResult<SFCTaskIPT> wResult = new ServiceResult<SFCTaskIPT>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);
//			wResult.Result = SFCIPTDAO.getInstance().SFC_QueryTaskIPTByID(wCompanyID, wLoginID, wID, wErrorCode);
//			wResult.FaultCode = MESServiceDAO.getInstance().MES_QueryErrorText(wErrorCode.get());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_AddTaskIPT(BMSEmployee wLoginUser, int wCompanyID, int wLoginID,
			SFCTaskIPT wTaskIPT) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);
//			wResult.Result = SFCIPTDAO.getInstance().SFC_AddTaskIPT(wCompanyID, wLoginID, wTaskIPT, wErrorCode);
//			wResult.FaultCode = MESServiceDAO.getInstance().MES_QueryErrorText(wErrorCode.get());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_SaveTaskIPT(BMSEmployee wLoginUser, int wCompanyID, int wLoginID,
			SFCTaskIPT wTaskIPT) {
		// TODO Auto-generated method stub
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);
//			wResult.Result = SFCIPTDAO.getInstance().SFC_SaveTaskIPT(wCompanyID, wLoginID, wTaskIPT, wErrorCode);
//			wResult.FaultCode = MESServiceDAO.getInstance().MES_QueryErrorText(wErrorCode.get());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListByLoginID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wEventID, boolean wIncludeSub) {
		// TODO Auto-generated method stub
		ServiceResult<List<SFCTaskIPT>> wResult = new ServiceResult<List<SFCTaskIPT>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);
//			wResult.Result = SFCIPTDAO.getInstance().SFC_QueryTaskIPTListByLoginID(wCompanyID, wLoginID, wEventID,
//					wIncludeSub, wErrorCode);
//			wResult.FaultCode = MESServiceDAO.getInstance().MES_QueryErrorText(wErrorCode.get());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListByStationID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wStationID, int wEventID) {
		// TODO Auto-generated method stub
		ServiceResult<List<SFCTaskIPT>> wResult = new ServiceResult<List<SFCTaskIPT>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);
//			wResult.Result = SFCIPTDAO.getInstance().SFC_QueryTaskIPTListByStationID(wCompanyID, wLoginID, wStationID,
//					wEventID, wErrorCode);
//			wResult.FaultCode = MESServiceDAO.getInstance().MES_QueryErrorText(wErrorCode.get());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCLoginEvent> SFC_QueryActiveLoginEventByLoginID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wWorkShopID, int wStationID, int wAPPEventID) {
		// TODO Auto-generated method stub
		ServiceResult<SFCLoginEvent> wResult = new ServiceResult<SFCLoginEvent>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);
//			wResult.Result = SFCIPTDAO.getInstance().SFC_QueryActiveLoginEventByLoginID(wCompanyID, wLoginID,
//					wWorkShopID, wStationID, wAPPEventID, wErrorCode);
//			wResult.FaultCode = MESServiceDAO.getInstance().MES_QueryErrorText(wErrorCode.get());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCLoginEvent>> SFC_QueryLoginEventListByLoginID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wAPPEventID) {
		// TODO Auto-generated method stub
		ServiceResult<List<SFCLoginEvent>> wResult = new ServiceResult<List<SFCLoginEvent>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);
//			wResult.Result = SFCIPTDAO.getInstance().SFC_QueryLoginEventListByLoginID(wCompanyID, wLoginID, wAPPEventID,
//					wErrorCode);
//			wResult.FaultCode = MESServiceDAO.getInstance().MES_QueryErrorText(wErrorCode.get());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_LoginByAPPEvent(BMSEmployee wLoginUser, int wCompanyID, int wLoginID,
			int wWorkShopID, int wStationID, int wEventID) {
		// TODO Auto-generated method stub
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);
//			wResult.Result = SFCIPTDAO.getInstance().SFC_LoginByAPPEvent(wCompanyID, wLoginID, wWorkShopID, wStationID,
//					wEventID, wErrorCode);
//			wResult.FaultCode = MESServiceDAO.getInstance().MES_QueryErrorText(wErrorCode.get());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_LayoutByAPPEvent(BMSEmployee wLoginUser, int wCompanyID, int wLoginID,
			int wWorkShopID, int wStationID, int wEventID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wErrorCode.set(0);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 添加导入错误记录
	 * 
	 * @param wLoginUser 登录信息
	 * @param wFileName  文件名
	 * @param wMsg       错误信息
	 * @param wDataCount 数据行数
	 * @param wType      导入类型
	 */
	private void AddImportRecord(BMSEmployee wLoginUser, String wFileName, String wMsg, int wDataCount, int wType) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(), wType, "",
					null, IMPResult.Failed.getValue(), wFileName, wDataCount, 1);
			wIMPResultRecord.ID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
			if (wIMPResultRecord.ID <= 0) {
				return;
			}

			IMPErrorRecord wIMPErrorRecord = new IMPErrorRecord(0, wIMPResultRecord.ID, 0, wMsg);
			IMPErrorRecordDAO.getInstance().Update(wLoginUser, wIMPErrorRecord, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<Integer> IPT_ImportStandard(BMSEmployee wLoginUser, int wIPTMode, ExcelData wExcelData,
			int wProductID, int wLineID, int wStationID, int wCustomerID, String wFileName) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// 去除空行
			if (wExcelData.sheetData.size() > 0 && wExcelData.sheetData.get(0).lineData != null
					&& wExcelData.sheetData.get(0).lineData.size() > 0) {
				wExcelData.sheetData.get(0).lineData.removeIf(p -> p.colData != null && p.colData.size() > 0
						&& p.colData.stream().allMatch(q -> StringUtils.isEmpty(q)));
			}

			if (wExcelData == null || wExcelData.sheetData == null || wExcelData.sheetData.size() <= 0
					|| wExcelData.sheetData.get(0).lineData == null || wExcelData.sheetData.get(0).lineData.size() <= 0
					|| wExcelData.sheetData.get(0).lineData.get(0).colData == null
					|| wExcelData.sheetData.get(0).lineData.get(0).colData.size() <= 0
					|| (!wExcelData.sheetData.get(0).lineData.get(0).colData.get(0).equals("工序"))) {
				wResult.FaultCode += "提示：Excel格式不正确!";

				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wFileName, wResult.FaultCode,
						wExcelData.sheetData.get(0).lineData.size() - 1, IMPType.Standard.getValue());

				return wResult;
			}

			List<FPCPartPoint> wStepList = FMCServiceImpl.getInstance().FPC_QueryPartPointList(wLoginUser, -1, -1, -1)
					.List(FPCPartPoint.class);

			// 补全数据
			int wTotalSum = wExcelData.sheetData.get(0).lineData.get(0).colSum;
			for (int i = 1; i < wExcelData.sheetData.get(0).lineData.size(); i++) {
				int wSum = wExcelData.sheetData.get(0).lineData.get(i).colSum;
				if (wSum < wTotalSum) {
					for (int j = wSum; j < wTotalSum; j++) {
						wExcelData.sheetData.get(0).lineData.get(i).colData.add("");
						wExcelData.sheetData.get(0).lineData.get(i).colSum = wTotalSum;
					}
				}
			}

			// 检查数据
			String wMsg = this.CheckData(wLoginUser, wExcelData, wFileName, wStepList);
			if (StringUtils.isNotEmpty(wMsg)) {
				wResult.FaultCode += wMsg;
				return wResult;
			}

			IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
					IMPType.Standard.getValue(), "", null, IMPResult.Doding.getValue(), wFileName,
					wExcelData.sheetData.get(0).lineData.size() - 1, 0);
			wIMPResultRecord.ID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

			// 导入标准
			List<Integer> wIDList = QMSUtils.getInstance().IPT_ImportPreCheck(wLoginUser, wExcelData, wProductID,
					wLineID, wIPTMode, wStationID, wCustomerID, wStepList, wIMPResultRecord);

			// 更新导入成功记录
			wIMPResultRecord.PID = wIDList;
			wIMPResultRecord.Progress = wExcelData.sheetData.get(0).lineData.size() - 1;
			wIMPResultRecord.Result = IMPResult.Success.getValue();
			IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

			// 返回错误码
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (

		Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 添加错误记录
	 */
	private void AddRecored(BMSEmployee wLoginUser, List<ExcelLineData> wExcelLineDataList, String wFileName,
			List<FPCPartPoint> wStepList, OutResult<Integer> wErrorCode) {
		try {
			List<IMPErrorRecord> wRecordList = new ArrayList<IMPErrorRecord>();

			for (int i = 1; i < wExcelLineDataList.size(); i++) {
				int wIndex = i;

				// 工序
				String wPartPointName = wExcelLineDataList.get(wIndex).colData.get(0);
				// 一级项点
				String wItemName1 = wExcelLineDataList.get(wIndex).colData.get(1);
				// 二级项点
				String wItemName2 = wExcelLineDataList.get(wIndex).colData.get(2);
				// 三级项点
				String wItemName3 = wExcelLineDataList.get(wIndex).colData.get(3);
				// 四级项点
				String wItemName4 = wExcelLineDataList.get(wIndex).colData.get(4);
				// 五级项点
				String wItemName5 = wExcelLineDataList.get(wIndex).colData.get(5);
				// 厂家必填
				String wFactorFill = wExcelLineDataList.get(wIndex).colData.get(9);
				// 型号必填
				String wModalFill = wExcelLineDataList.get(wIndex).colData.get(10);
				// 编号必填
				String wNumberFill = wExcelLineDataList.get(wIndex).colData.get(11);
				// 填写值类型
				String wType = wExcelLineDataList.get(wIndex).colData.get(12);
				// 填写值必填
				String wValueFill = wExcelLineDataList.get(wIndex).colData.get(13);
				// 填写值选项列表
				String wOptionList = wExcelLineDataList.get(wIndex).colData.get(15);
				// 图片必填
				String wPictureFill = wExcelLineDataList.get(wIndex).colData.get(21);
				// 视频必填
				String wVideoFill = wExcelLineDataList.get(wIndex).colData.get(22);
				// 上限
				String wLeft = wExcelLineDataList.get(wIndex).colData.get(24);
				// 下限
				String wRight = wExcelLineDataList.get(wIndex).colData.get(25);

				// ①检查工序
				if (StringUtils.isEmpty(wPartPointName)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，工序不能为空!", i + 1)));
				} else if (!wStepList.stream().anyMatch(p -> p.Name.equals(wPartPointName))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【{1}】工序不存在!", i + 1, wPartPointName)));
				}
				// ②检查一级项点
				if (StringUtils.isEmpty(wItemName1)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，一级项点不能为空!", i + 1)));
				}
				// ③检查二级项点
				if (StringUtils.isEmpty(wItemName1) && StringUtils.isNotEmpty(wItemName2)) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，二级项点的上级项点不能为空!", i + 1)));
				}
				// ④检查三级项点
				if (StringUtils.isEmpty(wItemName2) && StringUtils.isNotEmpty(wItemName3)) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，三级项点的上级项点不能为空!", i + 1)));
				}
				// ⑤检查四级项点
				if (StringUtils.isEmpty(wItemName3) && StringUtils.isNotEmpty(wItemName4)) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，四级项点的上级项点不能为空!", i + 1)));
				}
				// ⑥检查五级项点
				if (StringUtils.isEmpty(wItemName4) && StringUtils.isNotEmpty(wItemName5)) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，五级项点的上级项点不能为空!", i + 1)));
				}
				// ⑦检查厂家必填
				if (!(wFactorFill.equals("") || wFactorFill.equals("是") || wFactorFill.equals("否")
						|| wFactorFill.equals("不显示"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，厂家必填输入值不合法!", i + 1)));
				}
				// ⑧检查型号必填
				if (!(wModalFill.equals("") || wModalFill.equals("是") || wModalFill.equals("否")
						|| wModalFill.equals("不显示"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，型号必填输入值不合法!", i + 1)));
				}
				// ⑨检查编号必填
				if (!(wNumberFill.equals("") || wNumberFill.equals("是") || wNumberFill.equals("否")
						|| wNumberFill.equals("不显示"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，编号必填输入值不合法!", i + 1)));
				}
				// ⑩检查填写值类型
				if (!(wType.equals("") || wType.equals("大于") || wType.equals("大于等于") || wType.equals("单选")
						|| wType.equals("等于") || wType.equals("多选") || wType.equals("全包区间") || wType.equals("全开区间")
						|| wType.equals("数字") || wType.equals("文本") || wType.equals("小于") || wType.equals("小于等于")
						|| wType.equals("右包区间") || wType.equals("左包区间"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，填写值类型输入值不合法!", i + 1)));
				}
				// ①检查填写值必填
				if (!(wValueFill.equals("") || wValueFill.equals("是") || wValueFill.equals("否")
						|| wValueFill.equals("不显示"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，填写值必填输入值不合法!", i + 1)));
				}
				// ②检查填写值选项列表
				if ((wType.equals("单选") || wType.equals("多选")) && StringUtils.isEmpty(wOptionList)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，填写值类型为单选或多选时，填写值选项不能为空!", i + 1)));
				}
				// ⑤检查图片必填
				if (!(wPictureFill.equals("") || wPictureFill.equals("是") || wPictureFill.equals("否")
						|| wPictureFill.equals("不显示"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，图片必填输入值不合法!", i + 1)));
				}
				// ⑥检查视频必填
				if (!(wVideoFill.equals("") || wVideoFill.equals("是") || wVideoFill.equals("否")
						|| wVideoFill.equals("不显示"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，视频必填输入值不合法!", i + 1)));
				}
				// ①检查上限值
				if (StringUtils.isNotEmpty(wLeft) && !QMSUtils.getInstance().isNumber(wLeft)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，上限值输入值不合法!", i + 1)));
				}
				// ②检查下限值
				if (StringUtils.isNotEmpty(wRight) && !QMSUtils.getInstance().isNumber(wRight)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，下限值输入值不合法!", i + 1)));
				}
			}

			if (wRecordList.size() > 0) {
				IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
						IMPType.Standard.getValue(), "", null, IMPResult.Failed.getValue(), wFileName,
						wExcelLineDataList.size() - 1, wRecordList.size());
				int wNewID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

				wRecordList.forEach(p -> p.ParentID = wNewID);
				for (IMPErrorRecord wIMPErrorRecord : wRecordList) {
					IMPErrorRecordDAO.getInstance().Update(wLoginUser, wIMPErrorRecord, wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 检查数据的合理性
	 * 
	 * @param wLoginUser 登录信息
	 * @param wExcelData Excel数据源
	 * @param wFileName  文件名
	 * @param wStepList  工序列表
	 * @return 提示信息
	 */
	private String CheckData(BMSEmployee wLoginUser, ExcelData wExcelData, String wFileName,
			List<FPCPartPoint> wStepList) {
		String wResult = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// 添加错误提示记录
			AddRecored(wLoginUser, wExcelData.sheetData.get(0).lineData, wFileName, wStepList, wErrorCode);

			List<ExcelLineData> wExcelLineDataList = wExcelData.sheetData.get(0).lineData;
			for (int i = 1; i < wExcelLineDataList.size(); i++) {
				int wIndex = i;

				// 工序
				String wPartPointName = wExcelLineDataList.get(wIndex).colData.get(0);
				// 一级项点
				String wItemName1 = wExcelLineDataList.get(wIndex).colData.get(1);
				// 二级项点
				String wItemName2 = wExcelLineDataList.get(wIndex).colData.get(2);
				// 三级项点
				String wItemName3 = wExcelLineDataList.get(wIndex).colData.get(3);
				// 四级项点
				String wItemName4 = wExcelLineDataList.get(wIndex).colData.get(4);
				// 五级项点
				String wItemName5 = wExcelLineDataList.get(wIndex).colData.get(5);
				// 厂家必填
				String wFactorFill = wExcelLineDataList.get(wIndex).colData.get(9);
				// 型号必填
				String wModalFill = wExcelLineDataList.get(wIndex).colData.get(10);
				// 编号必填
				String wNumberFill = wExcelLineDataList.get(wIndex).colData.get(11);
				// 填写值类型
				String wType = wExcelLineDataList.get(wIndex).colData.get(12);
				// 填写值必填
				String wValueFill = wExcelLineDataList.get(wIndex).colData.get(13);
				// 填写值选项列表
				String wOptionList = wExcelLineDataList.get(wIndex).colData.get(15);
				// 图片必填
				String wPictureFill = wExcelLineDataList.get(wIndex).colData.get(21);
				// 视频必填
				String wVideoFill = wExcelLineDataList.get(wIndex).colData.get(22);
				// 上限
				String wLeft = wExcelLineDataList.get(wIndex).colData.get(24);
				// 下限
				String wRight = wExcelLineDataList.get(wIndex).colData.get(25);

				// ①检查工序
				if (StringUtils.isEmpty(wPartPointName)) {
					return StringUtils.Format("提示：第{0}行数据不合法，工序不能为空!", i + 1);
				} else if (!wStepList.stream().anyMatch(p -> p.Name.equals(wPartPointName))) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】工序不存在!", i + 1, wPartPointName);
				}
				// ②检查一级项点
				if (StringUtils.isEmpty(wItemName1)) {
					return StringUtils.Format("提示：第{0}行数据不合法，一级项点不能为空!", i + 1);
				}
				// ③检查二级项点
				if (StringUtils.isEmpty(wItemName1) && StringUtils.isNotEmpty(wItemName2)) {
					return StringUtils.Format("提示：第{0}行数据不合法，二级项点的上级项点不能为空!", i + 1);
				}
				// ④检查三级项点
				if (StringUtils.isEmpty(wItemName2) && StringUtils.isNotEmpty(wItemName3)) {
					return StringUtils.Format("提示：第{0}行数据不合法，三级项点的上级项点不能为空!", i + 1);
				}
				// ⑤检查四级项点
				if (StringUtils.isEmpty(wItemName3) && StringUtils.isNotEmpty(wItemName4)) {
					return StringUtils.Format("提示：第{0}行数据不合法，四级项点的上级项点不能为空!", i + 1);
				}
				// ⑥检查五级项点
				if (StringUtils.isEmpty(wItemName4) && StringUtils.isNotEmpty(wItemName5)) {
					return StringUtils.Format("提示：第{0}行数据不合法，五级项点的上级项点不能为空!", i + 1);
				}
				// ⑦检查厂家必填
				if (!(wFactorFill.equals("") || wFactorFill.equals("是") || wFactorFill.equals("否")
						|| wFactorFill.equals("不显示"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，厂家必填输入值不合法!", i + 1);
				}
				// ⑧检查型号必填
				if (!(wModalFill.equals("") || wModalFill.equals("是") || wModalFill.equals("否")
						|| wModalFill.equals("不显示"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，型号必填输入值不合法!", i + 1);
				}
				// ⑨检查编号必填
				if (!(wNumberFill.equals("") || wNumberFill.equals("是") || wNumberFill.equals("否")
						|| wNumberFill.equals("不显示"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，编号必填输入值不合法!", i + 1);
				}
				// ⑩检查填写值类型
				if (!(wType.equals("") || wType.equals("大于") || wType.equals("大于等于") || wType.equals("单选")
						|| wType.equals("等于") || wType.equals("多选") || wType.equals("全包区间") || wType.equals("全开区间")
						|| wType.equals("数字") || wType.equals("文本") || wType.equals("小于") || wType.equals("小于等于")
						|| wType.equals("右包区间") || wType.equals("左包区间"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，填写值类型输入值不合法!", i + 1);
				}
				// ①检查填写值必填
				if (!(wValueFill.equals("") || wValueFill.equals("是") || wValueFill.equals("否")
						|| wValueFill.equals("不显示"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，填写值必填输入值不合法!", i + 1);
				}
				// ②检查填写值选项列表
				if ((wType.equals("单选") || wType.equals("多选")) && StringUtils.isEmpty(wOptionList)) {
					return StringUtils.Format("提示：第{0}行数据不合法，填写值类型为单选或多选时，填写值选项不能为空!", i + 1);
				}
				// ⑤检查图片必填
				if (!(wPictureFill.equals("") || wPictureFill.equals("是") || wPictureFill.equals("否")
						|| wPictureFill.equals("不显示"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，图片必填输入值不合法!", i + 1);
				}
				// ⑥检查视频必填
				if (!(wVideoFill.equals("") || wVideoFill.equals("是") || wVideoFill.equals("否")
						|| wVideoFill.equals("不显示"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，视频必填输入值不合法!", i + 1);
				}
				// ①检查上限值
				if (StringUtils.isNotEmpty(wLeft) && !QMSUtils.getInstance().isNumber(wLeft)) {
					return StringUtils.Format("提示：第{0}行数据不合法，上限值输入值不合法!", i + 1);
				}
				// ②检查下限值
				if (StringUtils.isNotEmpty(wRight) && !QMSUtils.getInstance().isNumber(wRight)) {
					return StringUtils.Format("提示：第{0}行数据不合法，下限值输入值不合法!", i + 1);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTItem> IPT_ItemApply(BMSEmployee wLoginUser, IPTItemApply wIPTItemApply) {
		ServiceResult<IPTItem> wResult = new ServiceResult<IPTItem>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// 生成预检项和知识库
			if (wIPTItemApply != null) {
				IPTStandard wIPTStandard = null;
				wIPTStandard = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, wIPTItemApply.StandardID,
						wErrorCode).Result;

				if (wIPTStandard == null || wIPTStandard.ID <= 0)
					return wResult;

				// 保存标准项
				IPTItem wIPTItem = new IPTItem();
				wIPTItem.DefaultValue = "";
				wIPTItem.ID = 0;
				wIPTItem.ItemType = wIPTItemApply.ItemType;
				wIPTItem.Standard = "";
				wIPTItem.StandardType = IPTStandardType.Text.getValue();
				wIPTItem.Text = wIPTItemApply.ItemName;
				wIPTItem.Visiable = true;
				wIPTItem.Active = 1;
				wIPTItem.Process = wIPTItemApply.Process;
				wIPTItem.Standard = wIPTItemApply.Standard;
				wIPTItem.Details = wIPTItemApply.Remark;

				long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, wIPTStandard.ID, wIPTItem, wErrorCode);
				if (wItemID <= 0)
					return wResult;
				wIPTItem.ID = wItemID;

				wResult.Result = wIPTItem;

			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTPreCheckProblem> IPT_QueryPreCheckProblem(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTPreCheckProblem> wResult = new ServiceResult<IPTPreCheckProblem>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);

			if (wResult.Result != null && ((IPTPreCheckProblem) wResult.Result).ID > 0) {
				if (((Boolean) CoreServiceImpl.getInstance()
						.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 502701, 0, 0)
						.Info(Boolean.class)).booleanValue()) {
					if (((IPTPreCheckProblem) wResult.Result).Status == IPTPreCheckProblemStatus.ToCraftGiveSolve
							.getValue()
							|| ((IPTPreCheckProblem) wResult.Result).Status == IPTPreCheckProblemStatus.ToCraftSendAudit
									.getValue()) {
						((IPTPreCheckProblem) wResult.Result).IsPower = true;
					}
				}

				if (((IPTPreCheckProblem) wResult.Result).Status == IPTPreCheckProblemStatus.ToConfirm.getValue()
						&& wResult.Result.IPTProblemConfirmerList != null && wResult.Result.IPTProblemConfirmerList
								.stream().anyMatch(p -> p.ConfirmerID == wLoginUser.ID && p.Status == 0)) {
					((IPTPreCheckProblem) wResult.Result).IsPower = true;
				}
			}

			if (wResult.Result != null && ((IPTPreCheckProblem) wResult.Result).ID > 0) {
				List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1L,
						((IPTPreCheckProblem) wResult.Result).IPTItemID,
						((IPTPreCheckProblem) wResult.Result).IPTPreCheckTaskID, -1, -1, -1, wErrorCode);
				if (wValueList != null && wValueList.size() > 0) {
					((IPTPreCheckProblem) wResult.Result).PreCheckValue = wValueList.get(0);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_SavePreCheckProblem(BMSEmployee wLoginUser,
			IPTPreCheckProblem wIPTPreCheckProblem) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);

			// 发送通知消息
//			if (wIPTPreCheckProblem.Status == IPTPreCheckProblemStatus.ToDeparHandle.getValue()
//					&& wIPTPreCheckProblem.EmployeeList != null && wIPTPreCheckProblem.EmployeeList.size() > 0) {
//				List<BFCMessage> wMessageList = new ArrayList<BFCMessage>();
//				this.BFC_SetValue(wLoginUser, wMessageList, wIPTPreCheckProblem);
//				CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
//			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_SaveSOPList(BMSEmployee wLoginUser, IPTItem wIPTItem) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			if (wIPTItem == null || wIPTItem.ID <= 0)
				return wResult;

			// 保存解决方案
			if (wIPTItem.IPTSOPList != null && wIPTItem.IPTSOPList.size() > 0) {
				for (IPTSOP wIPTSOP : wIPTItem.IPTSOPList) {
					int wSOPID = IPTSOPDAO.getInstance().Update(wLoginUser, wIPTSOP, wErrorCode);
					if (wSOPID > 0) {
						wIPTSOP.ID = wSOPID;
					}
				}
			}

			// 保存预检项
			IPTItemDAO.getInstance().Update(wLoginUser, wIPTItem.VID, wIPTItem, wErrorCode);

			// 获取标准
			ServiceResult<IPTStandard> wIPTStandard = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wIPTItem.VID, wErrorCode);
			if (wIPTStandard.Result != null && wIPTStandard.Result.ID > 0) {
				// 保存解决方案知识库
				List<IPTSolveLib> wLibList = IPTSolveLibDAO.getInstance().SelectList(wLoginUser, -1, (int) wIPTItem.ID,
						wIPTStandard.Result.ProductID, wIPTStandard.Result.LineID, wIPTStandard.Result.CustomID,
						wErrorCode);
				if (wLibList != null && wLibList.size() == 1) {
					IPTSolveLib wIPTSolveLib = wLibList.get(0);
					wIPTSolveLib.IPTSOPList = wIPTItem.IPTSOPList;
					IPTSolveLibDAO.getInstance().Update(wLoginUser, wIPTSolveLib, wErrorCode);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTItem>> IPT_QueryIPTItemList(BMSEmployee wLoginUser, int wSFCTaskIPTID) {
		ServiceResult<List<IPTItem>> wResult = new ServiceResult<List<IPTItem>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			SFCTaskIPT wTask = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wSFCTaskIPTID, wErrorCode);
			if (wTask == null || wTask.ID <= 0) {
				return wResult;
			}

			IPTMode wIPTMode = IPTMode.Default;
			switch (SFCTaskType.getEnumType(wTask.TaskType)) {
			case SelfCheck:
			case MutualCheck:
			case SpecialCheck:
				wIPTMode = IPTMode.QTXJ;
				break;
			case PreCheck:
				wIPTMode = IPTMode.PreCheck;
				break;
			default:
				break;
			}

			IPTStandard wIPTStandard = null;
			if (wTask.ModuleVersionID <= 0) {
				wIPTStandard = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, -1, wIPTMode, -1, -1,
						-1, -1, -1, wTask.LineID, wTask.StationID, wTask.PartPointID, -1, wTask.ProductNo,
						wErrorCode).Result;
			} else {
				wIPTStandard = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, wTask.ModuleVersionID,
						wErrorCode).Result;
			}

			if (wIPTStandard != null && wIPTStandard.ID > 0) {
				// 获取子项
				ServiceResult<Map<Long, List<IPTItem>>> wMapResult = IPTStandardDAO.getInstance().SelectItem(wLoginUser,
						new ArrayList<Long>(Arrays.asList(wIPTStandard.ID)), wErrorCode);
				if (wMapResult.Result != null && wMapResult.Result.containsKey(wIPTStandard.ID)) {
					wResult.Result = wMapResult.Result.get(wIPTStandard.ID);
				}
			} else {
				wResult.FaultCode += "该工序无当前标准!";
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ServiceResult<Integer> IPT_TriggerTask(BMSEmployee wLoginUser, int wSFCTaskIPTID,
			List<IPTValue> wValueList1) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			SFCTaskIPT wSFCTaskIPT = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wSFCTaskIPTID, wErrorCode);
			int wTaskType = wSFCTaskIPT.TaskType;

			// 如果做自检，并且互检任务没有，那么触发一条互检任务
			if (wSFCTaskIPT.TaskType == SFCTaskType.SelfCheck.getValue()) {
				List<SFCTaskIPT> wHJList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
						wSFCTaskIPT.TaskStepID, SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null,
						SFCTaskStepType.Step.getValue(), null, null, wErrorCode);
				if (wHJList == null || wHJList.size() <= 0) {
					wSFCTaskIPT.TaskType = SFCTaskType.MutualCheck.getValue();
					int wNewID = this.SFC_TriggerTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
					// 互检任务创建出来时，发送通知消息给同工位不同操作工和班组长
					SendTaskMessageToOperatorAndMonitorWhenMutualTaskCreated(wLoginUser, wNewID, wSFCTaskIPT);
				} else if (wValueList1.stream().anyMatch(p -> p.Result == 1)) {
					// 将互检任务状态改为未完成
					wHJList.get(0).Status = SFCTaskStatus.Active.getValue();
					SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wHJList.get(0), wErrorCode);
				}
			}
			// 如果做互检、并且专检任务没有，那么触发一条专检任务
			else if (wSFCTaskIPT.TaskType == SFCTaskType.MutualCheck.getValue()) {
				List<SFCTaskIPT> wZJList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
						wSFCTaskIPT.TaskStepID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, null,
						SFCTaskStepType.Step.getValue(), null, null, wErrorCode);
				if (wZJList == null || wZJList.size() <= 0) {
					wSFCTaskIPT.TaskType = SFCTaskType.SpecialCheck.getValue();
					int wNewID = this.SFC_TriggerTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
					// 专检任务触发时发送通知消息给班组长，发送任务消息给检验员
					SendMessageToMonitorAndCheckerWhenSpecialTaskCreated(wLoginUser, wNewID, wSFCTaskIPT);
				}
				// 如果所有互检项点合格提交，系统自动完工工序任务
				List<IPTValue> wValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSFCTaskIPT.ID, -1, -1,
						wErrorCode).Result;
				IPTStandard wIPTStandard = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0,
						IPTMode.QTXJ, -1, -1, -1, -1, -1, wSFCTaskIPT.LineID, wSFCTaskIPT.StationID,
						wSFCTaskIPT.PartPointID, -1, wSFCTaskIPT.ProductID, wErrorCode).Result;
				if (wValueList != null && wValueList.size() > 0 && wIPTStandard != null && wIPTStandard.ID > 0) {
					List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wIPTStandard.ID, -1,
							-1, wErrorCode);
					// 去除组
					wItemList.removeIf(p -> p.ItemType == IPTItemType.Group.getValue());
					// 更新相关任务的状态
					UpdateTaskStatus(wLoginUser, wSFCTaskIPT, wValueList, wItemList);
				}
			} else if (wSFCTaskIPT.TaskType == SFCTaskType.SpecialCheck.getValue()) {
				wSFCTaskIPT.OperatorID = wLoginUser.ID;
				wSFCTaskIPT.OperateTime = Calendar.getInstance();
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			}
			// 如果做预检，所有预检项都做完，预检任务自动完工，预检工序任务自动完工
			else if (wSFCTaskIPT.TaskType == SFCTaskType.PreCheck.getValue()
					|| wSFCTaskIPT.TaskType == SFCTaskType.Final.getValue()
					|| wSFCTaskIPT.TaskType == SFCTaskType.OutPlant.getValue()) {
				ServiceResult<List<Object>> wObjectList = SFCServiceImpl.getInstance()
						.SFC_QueryToDoAndDoneList(wLoginUser, wSFCTaskIPT.TaskStepID, wSFCTaskIPT.TaskType);
				if (((List<IPTItem>) wObjectList.Result.get(0)).size() <= 0) {
					wSFCTaskIPT.Status = SFCTaskStatus.Done.getValue();
					wSFCTaskIPT.EndTime = Calendar.getInstance();
					wSFCTaskIPT.SubmitTime = Calendar.getInstance();
					SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
					// 将工序任务状态更新为完工
					UpdatePreTaskStatus(wLoginUser, wSFCTaskIPT);
				}
			}

			// 更新任务状态
			List<Object> wObjectList = SFCServiceImpl.getInstance().SFC_QueryToDoAndDoneList(wLoginUser,
					wSFCTaskIPT.TaskStepID, wTaskType).Result;
			if (wObjectList != null && wObjectList.size() > 0) {
				List<IPTItem> wIPTItemList = (List<IPTItem>) wObjectList.get(0);

				Calendar wBaseTime = Calendar.getInstance();
				wBaseTime.set(2010, 0, 1);
				if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
					wSFCTaskIPT.StartTime = wSFCTaskIPT.ActiveTime;
				}

				if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
					wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
				}

				if (wIPTItemList == null || wIPTItemList.size() <= 0) {
					wSFCTaskIPT.SubmitTime = Calendar.getInstance();
					wSFCTaskIPT.EndTime = Calendar.getInstance();
					wSFCTaskIPT.Status = SFCTaskStatus.Done.getValue();
				} else {
					wSFCTaskIPT.Status = SFCTaskStatus.Active.getValue();
				}
				// 任务完工时，未完工时更新消息状态
				UpdateMessageStateWhenTaskFinishOrNotFinish(wLoginUser, wSFCTaskIPT);
				// 更新任务
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			}

			// 更新巡检任务状态(全部完成变成完工，否者变成未完工)
			UpdateTaskIPTStatus(wLoginUser, wErrorCode, wSFCTaskIPT);

			// 返回错误信息
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 完工 工序任务 和派工任务 (预检)
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPT
	 */
	private void UpdatePreTaskStatus(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		try {
			APSTaskStep wInfo = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
			if (wInfo != null && wInfo.ID > 0 && wInfo.Status == APSTaskStatus.Started.getValue()) {

				// 获取休息日和工作时间点
				List<CFGCalendar> wCFGCalendarList = CoreServiceImpl.getInstance()
						.CFG_QueryCalendarList(wLoginUser, Calendar.getInstance().get(Calendar.YEAR), 0)
						.List(CFGCalendar.class);
				List<Calendar> wHolidayList = new ArrayList<Calendar>();
				if (wCFGCalendarList != null && wCFGCalendarList.size() > 0) {
					wHolidayList = wCFGCalendarList.stream().map(p -> p.HolidayDate).collect(Collectors.toList());
				}
				APIResult wAPIResult = LOCOAPSServiceImpl.getInstance().APS_QueryWorkHour(wLoginUser);
				int wMaxHour = 0;
				int wMinHour = 0;
				if (wAPIResult != null) {
					if (wAPIResult.getReturnObject().containsKey("MaxWorkHour")) {
						wMaxHour = wAPIResult.Custom("MaxWorkHour", Integer.class);
					}
					if (wAPIResult.getReturnObject().containsKey("MinWorkHour")) {
						wMinHour = wAPIResult.Custom("MinWorkHour", Integer.class);
					}
				}

				wInfo.EndTime = Calendar.getInstance();
				wInfo.RealHour = QMSUtils.getInstance().QMS_CalTimeDuration(wInfo.StartTime, wInfo.EndTime,
						wHolidayList, wMinHour, wMaxHour);
				wInfo.Status = APSTaskStatus.Done.getValue();
				LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wInfo);
				// 将此工序的派工任务自动完工
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wInfo.ID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.RealHour += QMSUtils.getInstance().QMS_CalTimeDuration(wSFCTaskStep.EditTime,
								Calendar.getInstance(), wHolidayList, wMinHour, wMaxHour);
						wSFCTaskStep.IsStartWork = 2;
						wSFCTaskStep.EditTime = Calendar.getInstance();
					}
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}

			// 预检工位任务完工
			if (wInfo != null && wInfo.ID > 0) {
				List<APSTaskStep> wStepList = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepList(wLoginUser, -1, -1, wInfo.TaskPartID, null).List(APSTaskStep.class);
				if (wStepList.stream().allMatch(p -> p.Status == APSTaskStatus.Done.getValue())) {
					APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskPartByID(wLoginUser, wInfo.TaskPartID).Info(APSTaskPart.class);
					if (wTaskPart != null && wTaskPart.ID > 0) {
						wTaskPart.Status = APSTaskStatus.Done.getValue();
						wTaskPart.FinishWorkTime = Calendar.getInstance();
						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wLoginUser, wTaskPart);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 完工 工序任务和派工任务
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPT
	 * @param wValueList
	 * @param wItemList
	 */
	private void UpdateTaskStatus(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, List<IPTValue> wValueList,
			List<IPTItem> wItemList) {
		try {
			if (wItemList != null && wItemList.size() > 0 && wItemList.stream()
					.allMatch(p -> wValueList.stream().anyMatch(q -> q.Result == 1 && q.IPTItemID == p.ID))) {

				// 获取休息日和工作时间点
				List<CFGCalendar> wCFGCalendarList = CoreServiceImpl.getInstance()
						.CFG_QueryCalendarList(wLoginUser, Calendar.getInstance().get(Calendar.YEAR), 0)
						.List(CFGCalendar.class);
				List<Calendar> wHolidayList = new ArrayList<Calendar>();
				if (wCFGCalendarList != null && wCFGCalendarList.size() > 0) {
					wHolidayList = wCFGCalendarList.stream().map(p -> p.HolidayDate).collect(Collectors.toList());
				}
				APIResult wAPIResult = LOCOAPSServiceImpl.getInstance().APS_QueryWorkHour(wLoginUser);
				int wMaxHour = 0;
				int wMinHour = 0;
				if (wAPIResult != null) {
					if (wAPIResult.getReturnObject().containsKey("MaxWorkHour")) {
						wMaxHour = wAPIResult.Custom("MaxWorkHour", Integer.class);
					}
					if (wAPIResult.getReturnObject().containsKey("MinWorkHour")) {
						wMinHour = wAPIResult.Custom("MinWorkHour", Integer.class);
					}
				}

				// 将工序任务状态更新为完工
				APSTaskStep wInfo = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wInfo != null && wInfo.ID > 0 && wInfo.Status == APSTaskStatus.Started.getValue()) {
					wInfo.EndTime = Calendar.getInstance();
					wInfo.RealHour = QMSUtils.getInstance().QMS_CalTimeDuration(wInfo.StartTime, wInfo.EndTime,
							wHolidayList, wMinHour, wMaxHour);
					wInfo.Status = APSTaskStatus.Done.getValue();
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wInfo);
					// 将此工序的派工任务自动完工
					List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
							.SFC_QueryTaskStepList(wLoginUser, wInfo.ID, -1, -1).List(SFCTaskStep.class);
					if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
						for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
							wSFCTaskStep.IsStartWork = 2;
							wSFCTaskStep.RealHour += QMSUtils.getInstance().QMS_CalTimeDuration(wSFCTaskStep.EditTime,
									Calendar.getInstance(), wHolidayList, wMinHour, wMaxHour);
							wSFCTaskStep.EditTime = Calendar.getInstance();
						}
						for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
							LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
						}
						// 关闭派工任务的消息
						if (wSFCTaskStepList.size() > 0) {
							List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
									.BFC_GetMessageList(wLoginUser, -1, BPMEventModule.SCDispatching.getValue(),
											wSFCTaskStepList.stream().map(p -> p.ID).collect(Collectors.toList()),
											BFCMessageType.Task.getValue(), -1)
									.List(BFCMessage.class);
							if (wMessageList != null && wMessageList.size() > 0) {
								wMessageList = wMessageList.stream()
										.filter(p -> p.Active == 0 || p.Active == 1 || p.Active == 2)
										.collect(Collectors.toList());
								if (wMessageList.size() > 0) {
									wMessageList.forEach(p -> p.Active = 4);
									CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
								}
							}
						}
					}
					// 更新最后的工位任务状态为完工
					UpdateLastTaskPartStatus(wLoginUser, wInfo);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private void UpdateLastTaskPartStatus(BMSEmployee wLoginUser, APSTaskStep wInfo) {
		try {
			List<FPCPart> wPartList = (List<FPCPart>) (RSMServiceImpl.getInstance().RSM_QueryNextStationList(wLoginUser,
					wInfo.OrderID, wInfo.PartID)).Result;
			if (wPartList != null && wPartList.size() > 0) {
				return;
			}
			if (wInfo.TaskPartID <= 0) {
				return;
			}

			List<APSTaskStep> wTaskStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, -1, -1, wInfo.TaskPartID, null).List(APSTaskStep.class);
			if (wTaskStepList == null || wTaskStepList.size() <= 0) {
				return;
			}

			if (wTaskStepList.stream().allMatch(p -> (p.Status == APSTaskStatus.Done.getValue()))) {
				APSTaskPart wTaskPart = (APSTaskPart) LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskPartByID(wLoginUser, wInfo.TaskPartID).Info(APSTaskPart.class);
				if (wTaskPart == null || wTaskPart.ID <= 0) {
					return;
				}
				wTaskPart.Status = APSTaskStatus.Done.getValue();
				wTaskPart.FinishWorkTime = Calendar.getInstance();
				LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wLoginUser, wTaskPart);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 专检任务完工时，未完工时更新消息状态
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPT
	 */
	private void UpdateMessageStateWhenTaskFinishOrNotFinish(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		try {
			int wModuleID = 0;
			switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
			case SelfCheck:
				wModuleID = BPMEventModule.SCZJ.getValue();
				break;
			case MutualCheck:
				wModuleID = BPMEventModule.MutualCheck.getValue();
				break;
			case SpecialCheck:
				wModuleID = BPMEventModule.SpecialCheck.getValue();
				break;
			case PreCheck:
				wModuleID = BPMEventModule.PreCheck.getValue();
				break;
			default:
				break;
			}

			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, -1, wModuleID, BFCMessageType.Task.getValue(), -1, -1, null, null)
					.List(BFCMessage.class);
			if (wMessageList == null || wMessageList.size() <= 0) {
				return;
			}

			wMessageList = wMessageList.stream().filter(p -> p.MessageID == wSFCTaskIPT.ID)
					.collect(Collectors.toList());
			if (wMessageList == null || wMessageList.size() <= 0) {
				return;
			}

			wMessageList.forEach(p -> {
				if (wSFCTaskIPT.Status == SFCTaskStatus.Done.getValue()) {
					if (p.ResponsorID == wLoginUser.ID) {
						p.Active = BFCMessageStatus.Finished.getValue();
					} else {
						p.Active = BFCMessageStatus.Close.getValue();
					}
				} else {
					p.Active = BFCMessageStatus.Sent.getValue();
				}
			});

			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 专检任务触发时发送通知消息给班组长，发送任务消息给检验员
	 * 
	 * @param wLoginUser  登录信息
	 * @param wNewID      专检任务ID
	 * @param wSFCTaskIPT 触发专检的互检任务
	 */
	private void SendMessageToMonitorAndCheckerWhenSpecialTaskCreated(BMSEmployee wLoginUser, int wNewID,
			SFCTaskIPT wSFCTaskIPT) {
		try {
			// ①查找工序任务
			APSTaskStep wAPSTaskStep = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
			if (wAPSTaskStep == null || wAPSTaskStep.ID <= 0) {
				return;
			}
			// ②查找此工序任务的派工任务
			List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(wLoginUser, wAPSTaskStep.ID, -1, -1).List(SFCTaskStep.class);
			if (wSFCTaskStepList == null || wSFCTaskStepList.size() <= 0) {
				return;
			}
			// ③获取班组长
			int wMonitorID = wSFCTaskStepList.get(0).MonitorID;
			// ④创建发送给班组长的通知消息
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);
			List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
			BFCMessage wMessage = new BFCMessage();
			wMessage.Active = 0;
			wMessage.CompanyID = wLoginUser.CompanyID;
			wMessage.CreateTime = Calendar.getInstance();
			wMessage.EditTime = Calendar.getInstance();
			wMessage.ID = 0;
			wMessage.MessageID = wNewID;
			wMessage.Title = StringUtils.Format("专检 {0} {1}",
					new Object[] { wSFCTaskIPT.PartPointName, wSFCTaskIPT.PartNo });
			wMessage.MessageText = StringUtils.Format("【{0}】 {1}触发了专检任务【{2}】",
					new Object[] { BPMEventModule.SpecialCheck.getLable(), wLoginUser.Name,
							QMSConstants.GetFPCStepName(wSFCTaskIPT.PartPointID) });
			wMessage.ModuleID = BPMEventModule.SpecialCheck.getValue();
			wMessage.ResponsorID = wMonitorID;
			wMessage.ShiftID = wShiftID;
			wMessage.StationID = wSFCTaskIPT.StationID;
			wMessage.Type = BFCMessageType.Notify.getValue();
			wBFCMessageList.add(wMessage);
			// ⑤根据工区工位缓存数据获取工区
//			List<LFSWorkAreaStation> wLFSWorkAreaStationList = QMSConstants.GetLFSWorkAreaStationList().values()
//					.stream().filter(p -> p.StationID == wSFCTaskIPT.StationID).collect(Collectors.toList());
//			if (wLFSWorkAreaStationList != null && wLFSWorkAreaStationList.size() > 0) {
////				int wAreaID = wLFSWorkAreaStationList.get(0).WorkAreaID;
//			}
			// ⑥根据工区人员缓存数据获取工区检验员
			List<Integer> wCheckerIDList = QMSConstants.GetFPCPart(wSFCTaskIPT.StationID).CheckerList;
			if (wCheckerIDList != null && wCheckerIDList.size() > 0) {
				// ⑦创建发送给检验员的任务消息
				for (Integer wCheckerID : wCheckerIDList) {
					wMessage = new BFCMessage();
					wMessage.Active = 0;
					wMessage.CompanyID = wLoginUser.CompanyID;
					wMessage.CreateTime = Calendar.getInstance();
					wMessage.EditTime = Calendar.getInstance();
					wMessage.ID = 0;
					wMessage.MessageID = wNewID;
					wMessage.Title = StringUtils.Format("专检 {0} {1}",
							new Object[] { wSFCTaskIPT.PartPointName, wSFCTaskIPT.PartNo });
					wMessage.MessageText = StringUtils.Format("【{0}】 {1}触发了专检任务【{2}】",
							new Object[] { BPMEventModule.SpecialCheck.getLable(), wLoginUser.Name,
									QMSConstants.GetFPCStepName(wSFCTaskIPT.PartPointID) });
					wMessage.ModuleID = BPMEventModule.SpecialCheck.getValue();
					wMessage.ResponsorID = wCheckerID;
					wMessage.ShiftID = wShiftID;
					wMessage.StationID = wSFCTaskIPT.StationID;

					wMessage.Type = BFCMessageType.Task.getValue();
					wBFCMessageList.add(wMessage);
				}
			}
			// ⑧批量发送消息
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 互检任务创建出来时，发送通知消息给同工位不同操作工和班组长
	 * 
	 * @param wLoginUser 登录人
	 * @param wNewID     互检任务ID
	 */
	private void SendTaskMessageToOperatorAndMonitorWhenMutualTaskCreated(BMSEmployee wLoginUser, int wNewID,
			SFCTaskIPT wSFCTaskIPT) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<Integer> wOtherPersonIDList = new ArrayList<Integer>();

			List<SFCTaskStep> wTempSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(wLoginUser, new ArrayList<Integer>(Arrays.asList(wSFCTaskIPT.TaskStepID)))
					.List(SFCTaskStep.class);
			if (wTempSFCTaskStepList == null || wTempSFCTaskStepList.size() <= 0) {
				return;
			}

			wTempSFCTaskStepList.removeIf(p -> p.Type == SFCTaskStepType.Question.getValue());

			if (wTempSFCTaskStepList == null || wTempSFCTaskStepList.size() <= 0) {
				return;
			}

			List<SFCTaskIPT> wAllTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByTaskStepIDList(wLoginUser,
					new ArrayList<Integer>(Arrays.asList(wSFCTaskIPT.TaskStepID)), SFCTaskStepType.Step.getValue(),
					wErrorCode);

			List<SFCTaskIPT> wSelfCheckList = wAllTaskIPTList.stream().filter(
					p -> p.TaskStepID == wSFCTaskIPT.TaskStepID && p.TaskType == SFCTaskType.SelfCheck.getValue())
					.collect(Collectors.toList());

			// 所有填写值集合
			List<IPTValue> wAllValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					wAllTaskIPTList.stream().map(p -> p.ID).distinct().collect(Collectors.toList()), wErrorCode);

			List<IPTValue> wSelfValueList = new ArrayList<IPTValue>();
			if (wSelfCheckList != null && wSelfCheckList.size() > 0) {
				wSelfValueList = wAllValueList.stream().filter(p -> p.TaskID == wSelfCheckList.get(0).ID)
						.collect(Collectors.toList());
			}

			// 单独派工
			if (wTempSFCTaskStepList.size() == 1) {
				// 班组其他人都可互检
				wOtherPersonIDList = GetOhterPersonIDListWhenOne(wLoginUser, wSFCTaskIPT, wTempSFCTaskStepList);
			} else {
				for (SFCTaskStep wSFCTaskStep : wTempSFCTaskStepList) {
					if (wSelfValueList.stream().anyMatch(p -> p.SubmitID == wSFCTaskStep.OperatorID)) {
						continue;
					}
					wOtherPersonIDList.add(wSFCTaskStep.OperatorID);
				}
			}

			// ④找到班组长
			int wMonitorID = wTempSFCTaskStepList.get(0).MonitorID;
			// ⑨发送通知消息给操作工
			List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
			BFCMessage wMessage = null;
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);
			for (Integer wPersonID : wOtherPersonIDList) {
				// 发送任务消息到指定工区主管
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wNewID;
				wMessage.Title = StringUtils.Format("互检 {0} {1}",
						new Object[] { wSFCTaskIPT.PartPointName, wSFCTaskIPT.PartNo });
				wMessage.MessageText = StringUtils.Format("【{0}】 {1}触发了互检任务【{2}】", new Object[] {
						BPMEventModule.MutualCheck.getLable(), wLoginUser.Name, wSFCTaskIPT.PartPointName });
				wMessage.ModuleID = BPMEventModule.MutualCheck.getValue();
				wMessage.ResponsorID = wPersonID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = wSFCTaskIPT.PartID;
				wMessage.Type = BFCMessageType.Task.getValue();
				wBFCMessageList.add(wMessage);
			}
			// ⑩发送通知消息给班组长
			wMessage = new BFCMessage();
			wMessage.Active = 0;
			wMessage.CompanyID = 0;
			wMessage.CreateTime = Calendar.getInstance();
			wMessage.EditTime = Calendar.getInstance();
			wMessage.ID = 0;
			wMessage.MessageID = wNewID;
			wMessage.Title = StringUtils.Format("互检 {0} {1}",
					new Object[] { wSFCTaskIPT.PartPointName, wSFCTaskIPT.PartNo });
			wMessage.MessageText = StringUtils.Format("【{0}】 {1}触发了互检任务【{2}】",
					new Object[] { BPMEventModule.MutualCheck.getLable(), wLoginUser.Name, wSFCTaskIPT.PartPointName });
			wMessage.ModuleID = BPMEventModule.MutualCheck.getValue();
			wMessage.ResponsorID = wMonitorID;
			wMessage.ShiftID = wShiftID;
			wMessage.StationID = 0;
			wMessage.Type = BFCMessageType.Notify.getValue();
			wBFCMessageList.add(wMessage);
			// 11发送消息
			wBFCMessageList = new ArrayList<BFCMessage>(wBFCMessageList.stream()
					.collect(Collectors.toMap(BFCMessage::getResponsorID, account -> account, (k1, k2) -> k2))
					.values());
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private List<Integer> GetOhterPersonIDListWhenOne(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<SFCTaskStep> wTempSFCTaskStepList) {
		List<Integer> wResult = new ArrayList<>();
		try {
			// ①获取班组长所在班组
			int wClassID = QMSConstants.GetBMSEmployee(wTempSFCTaskStepList.get(0).MonitorID).DepartmentID;
			if (wClassID <= 0) {
				return wResult;
			}
			// ②根据班组获取人员
			List<BMSEmployee> wList = QMSConstants.GetBMSEmployeeList().values().stream()
					.filter(p -> p.DepartmentID == wClassID && p.ID != wTempSFCTaskStepList.get(0).OperatorID)
					.collect(Collectors.toList());
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}
			// ③添加人员列表
			wResult = wList.stream().map(p -> p.ID).collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 更新巡检任务状态
	 * 
	 * @param wLoginUser  登录信息
	 * @param wErrorCode  错误码
	 * @param wSFCTaskIPT 巡检任务
	 */
	@SuppressWarnings("unchecked")
	private void UpdateTaskIPTStatus(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode, SFCTaskIPT wSFCTaskIPT) {
		try {
			if (wSFCTaskIPT.TaskType == SFCTaskType.OutPlant.getValue()) {
				List<APSTaskStep> wList = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepList(wLoginUser, wSFCTaskIPT.OrderID, wSFCTaskIPT.PartID, -1, null)
						.List(APSTaskStep.class);
				if (wList != null && wList.size() > 0
						&& wList.stream().allMatch(p -> p.Status == APSTaskStatus.Done.getValue())) {
					OMSOrder wOrder = LOCOAPSServiceImpl.getInstance()
							.OMS_QueryOrderByID(wLoginUser, wSFCTaskIPT.OrderID).Info(OMSOrder.class);
					if (wOrder != null && wOrder.ID > 0) {
						wOrder.Status = OMSOrderStatus.ToOutConfirm.getValue();
						LOCOAPSServiceImpl.getInstance().OMS_UpdateOrder(BaseDAO.SysAdmin, wOrder);
					}
				}
			} else if (wSFCTaskIPT.TaskType == SFCTaskType.Final.getValue()) {
				List<APSTaskStep> wList = LOCOAPSServiceImpl.getInstance().APS_QueryTaskStepList(wLoginUser,
						wSFCTaskIPT.OrderID, wSFCTaskIPT.StationID, -1, new ArrayList<Integer>())
						.List(APSTaskStep.class);
				if (wList != null && wList.size() > 0
						&& wList.stream().allMatch(p -> p.Status == APSTaskStatus.Done.getValue())) {
					// 生成竣工确认单
					SFCOrderForm wSFCOrderForm = new SFCOrderForm();
					wSFCOrderForm.CreateID = wLoginUser.ID;
					wSFCOrderForm.CreateTime = Calendar.getInstance();
					wSFCOrderForm.ID = 0;
					wSFCOrderForm.OrderID = wSFCTaskIPT.OrderID;
					wSFCOrderForm.OrderNo = wSFCTaskIPT.OrderNo;
					wSFCOrderForm.PartNo = wSFCTaskIPT.PartNo;
					wSFCOrderForm.Status = 1;
					wSFCOrderForm.Type = 1;
					SFCOrderFormDAO.getInstance().Update(wLoginUser, wSFCOrderForm, wErrorCode);
				}
				if (!wSFCTaskIPT.OperatorList.stream().anyMatch(p -> (p.intValue() == wLoginUser.ID))) {
					wSFCTaskIPT.OperatorList.add(Integer.valueOf(wLoginUser.ID));
					SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
				}
			}

			// 更新自检、互检、专检单的状态
			List<SFCTaskIPT> wTaskList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskIPT.TaskStepID,
					-1, -1, -1, -1, null, SFCTaskStepType.Step.getValue(), null, null, wErrorCode);
			if (wTaskList != null && wTaskList.size() > 0) {
				for (SFCTaskIPT wTaskIPT : wTaskList) {
					List<Object> wObjectList = SFCServiceImpl.getInstance().SFC_QueryIPTItemValueList(wLoginUser,
							wTaskIPT.ID).Result;
					if (wObjectList == null || wObjectList.size() <= 0) {
						continue;
					}
					List<IPTItem> wIPTItemList = (List<IPTItem>) wObjectList.get(0);
					if (wIPTItemList == null || wIPTItemList.size() <= 0) {
						wTaskIPT.Status = SFCTaskStatus.Done.getValue();
						UpdateMessageStateWhenTaskFinishOrNotFinish(wLoginUser, wTaskIPT);
					} else {
						wTaskIPT.Status = SFCTaskStatus.Active.getValue();
						UpdateMessageStateWhenTaskFinishOrNotFinish(wLoginUser, wTaskIPT);
					}
					SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	// 巡检任务（质量巡检、工艺巡检）
	// 逻辑函数
	private int SFC_TriggerTaskIPT(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		wErrorCode.set(0);
		try {
			SFCTaskIPT wTaskTrigger = wTaskIPT.Clone();
			wTaskTrigger.ID = 0;
			wTaskTrigger.TaskType = wTaskIPT.TaskType;
			if (wTaskTrigger.TaskType > 0) {
				wTaskTrigger.EventID = wTaskIPT.EventID;
				wTaskTrigger.Status = SFCTaskStatus.Active.getValue();
				wTaskTrigger.FQTYGood = 0;
				wTaskTrigger.FQTYBad = 0;
				wResult = SFCTaskIPTDAO.getInstance().SFC_AddTaskIPT(wLoginUser, wTaskTrigger, wErrorCode);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.Exception.getValue());
			LoggerTool.SaveException("SFCService", "SFC_TriggerTaskIPT", "Function Exception:" + ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTPreCheckProblem>> IPT_QueryProblemList(BMSEmployee wLoginUser, int wOrderID,
			int wProductID, int wLineID, int wStationID, int wStepID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<IPTPreCheckProblem>> wResult = new ServiceResult<List<IPTPreCheckProblem>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = new ArrayList<IPTPreCheckProblem>();

			List<Integer> wStatusList = null;

			wResult.Result = IPTPreCheckProblemDAO.getInstance().SelectListByTask(wLoginUser, -1, wOrderID, wLineID,
					wProductID, wStationID, wStepID, wStatusList, wStartTime, wEndTime, wErrorCode);

			SFCTaskIPT wIPT = null;
			List<IPTValue> wValueList = null;
			for (IPTPreCheckProblem wIPTPreCheckProblem : wResult.Result) {
				// 预检赋值
				wIPTPreCheckProblem.IPTItem = IPTItemDAO.getInstance().SelectByID(wLoginUser,
						wIPTPreCheckProblem.IPTItemID, wErrorCode);
				// 预检人赋值
				wIPT = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wIPTPreCheckProblem.IPTPreCheckTaskID,
						wErrorCode);
				if (wIPT != null && wIPT.ID > 0) {
					wValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wIPT.ID, -1, -1,
							wErrorCode).Result;
					if (wValueList != null && wValueList.size() > 0
							&& wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTPreCheckProblem.IPTItemID)) {
						wIPTPreCheckProblem.PreCheckID = wValueList.stream()
								.filter(p -> p.IPTItemID == wIPTPreCheckProblem.IPTItemID).findFirst().get().SubmitID;
						wIPTPreCheckProblem.PreCheckName = QMSConstants
								.GetBMSEmployeeName(wIPTPreCheckProblem.PreCheckID);
					}
				}
			}

			if (wResult.Result != null && (wResult.Result).size() > 0) {
				for (IPTPreCheckProblem wIPTPreCheckProblem : wResult.Result) {
					List<SFCTaskIPT> wIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
							wIPTPreCheckProblem.ID, -1, -1, -1, -1, null, SFCTaskStepType.Question.getValue(), null,
							null, wErrorCode);
					if (wIPTList == null || wIPTList.size() <= 0) {
						continue;
					}
					List<IPTValue> wAllList = new ArrayList<>();
					for (SFCTaskIPT wSFCTaskIPT : wIPTList) {
						wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1L, -1L, wSFCTaskIPT.ID, -1, -1,
								-1, wErrorCode);
						if (wValueList != null && wValueList.size() > 0) {
							IPTValue wValue = wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime))
									.get();
							wAllList.add(wValue);
						}
					}
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTPreCheckProblem>> IPT_QueryPreCheckProblemListByTime(BMSEmployee wLoginUser,
			int wCraftID, int wDoClassID, int wClassIssueID, int wManager, Calendar wStartTime, Calendar wEndTime,
			List<Integer> wStateList) {
		ServiceResult<List<IPTPreCheckProblem>> wResult = new ServiceResult<List<IPTPreCheckProblem>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = new ArrayList<IPTPreCheckProblem>();
			wResult.Result.addAll(IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, wCraftID, wDoClassID,
					wClassIssueID, wManager, wStartTime, wEndTime, wStateList, wErrorCode));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_TriggerYJProblem(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wSFCTaskIPT == null || wSFCTaskIPT.ID <= 0 || wSFCTaskIPT.TaskType != SFCTaskType.PreCheck.getValue()) {
				return wResult;
			}

			IPTPreCheckProblem wIPTPreCheckProblem = null;

			IPTItem wIPTItem = null;

			List<Long> wStandardIDList = new ArrayList<Long>(Arrays.asList((long) wSFCTaskIPT.ModuleVersionID));
			if (wSFCTaskIPT.PeriodChangeStandard > 0) {
				wStandardIDList.add((long) wSFCTaskIPT.PeriodChangeStandard);
			}

			Map<Long, List<IPTItem>> wMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser, wStandardIDList,
					wErrorCode).Result;
			List<IPTItem> wItemList = new ArrayList<IPTItem>();
			for (Long wID : wMap.keySet()) {
				wItemList.addAll(wMap.get(wID));
			}

			List<IPTItem> wAllItemList = IPTItemDAO.getInstance().SelectByIDList(wLoginUser, wIPTValueList.stream()
					.map(p -> Integer.valueOf((int) p.IPTItemID)).distinct().collect(Collectors.toList()), wErrorCode);

			for (IPTValue wIPTValue : wIPTValueList) {
				if (wAllItemList.stream().anyMatch(p -> (p.ID == wIPTValue.IPTItemID))) {
					wIPTItem = wAllItemList.stream().filter(p -> (p.ID == wIPTValue.IPTItemID)).findFirst().get();
				}
				if (wIPTItem == null || wIPTItem.ID <= 0L) {
					continue;
				}

				if (!(wIPTValue.Result == 2 || wIPTItem.IsPeriodChange > 0)) {
					continue;
				}

				List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
				String wDescription = this.GetDescription(wFatherText, wIPTItem);
				// 保存知识库
				IPTSolveLib wIPTSolveLib = new IPTSolveLib();
				wIPTSolveLib.CreateID = wLoginUser.ID;
				wIPTSolveLib.CreateTime = Calendar.getInstance();
				wIPTSolveLib.Creator = wLoginUser.Name;
				wIPTSolveLib.CustomID = wSFCTaskIPT.CustomerID;
				wIPTSolveLib.Description = wDescription;
				String wDetails = wIPTItem.Standard;
				if (StringUtils.isNotEmpty(wIPTValue.Remark)) {
					wDetails += "::" + wIPTValue.Remark;
				}
				wIPTSolveLib.Details = wDetails;
				wIPTSolveLib.EditID = wLoginUser.ID;
				wIPTSolveLib.Editor = wLoginUser.Name;
				wIPTSolveLib.EditTime = Calendar.getInstance();
				wIPTSolveLib.ID = 0;
				wIPTSolveLib.ImageList = wIPTValue.ImagePath;
				wIPTSolveLib.IPTItemID = (int) wIPTValue.IPTItemID;
				wIPTSolveLib.LineID = wSFCTaskIPT.LineID;
				wIPTSolveLib.ProductID = wSFCTaskIPT.ProductID;
				wIPTSolveLib.VideoList = wIPTValue.VideoPath;
				int wSolveID = IPTSolveLibDAO.getInstance().Update(wLoginUser, wIPTSolveLib, wErrorCode);
				if (wSolveID <= 0) {
					continue;
				}

				// 保存问项题
				wIPTPreCheckProblem = new IPTPreCheckProblem();
				wIPTPreCheckProblem.IPTPreCheckTaskID = wSFCTaskIPT.ID;
				wIPTPreCheckProblem.IPTItemID = (int) wIPTValue.IPTItemID;
				wIPTPreCheckProblem.SolveID = wSolveID;
				wIPTPreCheckProblem.ProductID = wSFCTaskIPT.ProductID;
				wIPTPreCheckProblem.CarNumber = wSFCTaskIPT.PartNo;
				wIPTPreCheckProblem.LineID = wSFCTaskIPT.LineID;
				wIPTPreCheckProblem.CustomID = wSFCTaskIPT.CustomerID;
				wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.ToCraftSendAudit.getValue();
				wIPTPreCheckProblem.OrderID = wSFCTaskIPT.OrderID;
				wIPTPreCheckProblem.OrderNo = wSFCTaskIPT.OrderNo;
				wIPTPreCheckProblem.PreCheckID = wLoginUser.ID;
				wIPTPreCheckProblem.PreCheckTime = Calendar.getInstance();
				wIPTPreCheckProblem.APSTaskStepID = wSFCTaskIPT.TaskStepID;
				wIPTPreCheckProblem.StepID = wSFCTaskIPT.PartPointID;
				int wNewID = IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);

				// 预检问题项触发出来时，发送任务消息给现场工艺人员
				String wText = wIPTItem.Text;
				IPT_SendMessageToCraftWhenProblemTaskCreated(wLoginUser, wNewID, wText);
			}

			// 触发不需要预检的段改项的问题项
			TriggerNotPreCheckQuestions(wLoginUser, wSFCTaskIPT);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 触发不需要预检的段改项的问题项
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPT
	 */
	private void TriggerNotPreCheckQuestions(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wSFCTaskIPT.TaskType != SFCTaskType.PreCheck.getValue() || wSFCTaskIPT.PeriodChangeStandard <= 0) {
				return;
			}

			IPTStandard wIPTStandard = (IPTStandard) (IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wSFCTaskIPT.PeriodChangeStandard, wErrorCode)).Result;
			if (wIPTStandard == null || wIPTStandard.ID <= 0) {
				return;
			}

			List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1L, wIPTStandard.ID, -1, -1,
					wErrorCode);
			if (wItemList == null || wItemList.size() <= 0) {
				return;
			}

			wItemList = wItemList.stream().filter(p -> p.IsPeriodChange == 2).collect(Collectors.toList());
			if (wItemList == null || wItemList.size() <= 0) {
				return;
			}

			for (IPTItem wItem : wItemList) {
				List<IPTPreCheckProblem> wList = IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, -1,
						wSFCTaskIPT.ID, (int) wItem.ID, -1, -1, -1, null, wErrorCode);
				if (wList != null && wList.size() > 0) {
					continue;
				}

				List<String> wFatherText = this.GetFatherText(wItemList, wItem);
				String wDescription = this.GetDescription(wFatherText, wItem);
				// 保存知识库
				IPTSolveLib wIPTSolveLib = new IPTSolveLib();
				wIPTSolveLib.CreateID = wLoginUser.ID;
				wIPTSolveLib.CreateTime = Calendar.getInstance();
				wIPTSolveLib.Creator = wLoginUser.Name;
				wIPTSolveLib.CustomID = wSFCTaskIPT.CustomerID;
				wIPTSolveLib.Description = wDescription;
				wIPTSolveLib.Details = wItem.Standard;
				wIPTSolveLib.EditID = wLoginUser.ID;
				wIPTSolveLib.Editor = wLoginUser.Name;
				wIPTSolveLib.EditTime = Calendar.getInstance();
				wIPTSolveLib.ID = 0;
				wIPTSolveLib.ImageList = new ArrayList<String>();
				wIPTSolveLib.IPTItemID = (int) wItem.ID;
				wIPTSolveLib.LineID = wSFCTaskIPT.LineID;
				wIPTSolveLib.ProductID = wSFCTaskIPT.ProductID;
				wIPTSolveLib.VideoList = new ArrayList<String>();
				int wSolveID = IPTSolveLibDAO.getInstance().Update(wLoginUser, wIPTSolveLib, wErrorCode);
				if (wSolveID <= 0) {
					continue;
				}

				// 保存问项题
				IPTPreCheckProblem wIPTPreCheckProblem = new IPTPreCheckProblem();
				wIPTPreCheckProblem.IPTPreCheckTaskID = wSFCTaskIPT.ID;
				wIPTPreCheckProblem.IPTItemID = (int) wItem.ID;
				wIPTPreCheckProblem.SolveID = wSolveID;
				wIPTPreCheckProblem.ProductID = wSFCTaskIPT.ProductID;
				wIPTPreCheckProblem.CarNumber = wSFCTaskIPT.PartNo;
				wIPTPreCheckProblem.LineID = wSFCTaskIPT.LineID;
				wIPTPreCheckProblem.CustomID = wSFCTaskIPT.CustomerID;
				wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.ToCraftSendAudit.getValue();
				wIPTPreCheckProblem.OrderID = wSFCTaskIPT.OrderID;
				wIPTPreCheckProblem.OrderNo = wSFCTaskIPT.OrderNo;
				wIPTPreCheckProblem.PreCheckID = wLoginUser.ID;
				wIPTPreCheckProblem.PreCheckTime = Calendar.getInstance();
				wIPTPreCheckProblem.APSTaskStepID = wSFCTaskIPT.TaskStepID;
				int wNewID = IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);

				// 预检问题项触发出来时，发送任务消息给现场工艺人员
				String wText = wItem.Text;
				IPT_SendMessageToCraftWhenProblemTaskCreated(wLoginUser, wNewID, wText);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 预检问题项触发出来时，发送任务消息给现场工艺人员
	 * 
	 * @param wLoginUser
	 * @param wNewID
	 */
	private void IPT_SendMessageToCraftWhenProblemTaskCreated(BMSEmployee wLoginUser, int wNewID, String wItemName) {
		try {
//			List<BMSRoleItem> wRoleItemList = CoreServiceImpl.getInstance().BMS_UserAllByFunction(wLoginUser, 502701)
//					.List(BMSRoleItem.class);
//			if (wRoleItemList == null || wRoleItemList.size() <= 0) {
//				return;
//			}

			// ①获取预检工位工艺师列表
			List<Integer> wTechnicianList = this.GetTechnicianList(wLoginUser);

			List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
			// QMS
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);
			BFCMessage wMessage = null;
			for (Integer wPersonID : wTechnicianList) {
				// 发送任务消息给现场工艺人员
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wNewID;
				wMessage.MessageText = StringUtils.Format("【{0}】 {1}提交了不合格项，触发了预检问题项【{2}】",
						BPMEventModule.PreProblemHandle.getLable(), wLoginUser.Name, wItemName);
				wMessage.ModuleID = BPMEventModule.PreProblemHandle.getValue();
				wMessage.ResponsorID = wPersonID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = 0;
				wMessage.Title = StringUtils.Format("【{0}】 {1}提交了不合格项，触发了预检问题项【{2}】",
						BPMEventModule.PreProblemHandle.getLable(), wLoginUser.Name, wItemName);
				wMessage.Type = BFCMessageType.Task.getValue();
				wBFCMessageList.add(wMessage);
			}
			// 批量发送消息
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private String GetDescription(List<String> wFatherText, IPTItem wIPTItem) {
		String wResult = "";
		try {
			if (wFatherText == null || wFatherText.size() <= 0) {
				wResult = StringUtils.Format("{0}", wIPTItem.Text);
			} else {
				Collections.reverse(wFatherText);
				for (String wFather : wFatherText) {
					wResult += StringUtils.Format("【{0}】+|;|+", wFather);
				}
				wResult += wIPTItem.Text;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取某项的所有上级的完整描述
	 * 
	 * @return
	 */
	private List<String> GetFatherText(List<IPTItem> wItemList, IPTItem wIPTItem) {
		List<String> wResult = new ArrayList<String>();
		try {
			if (wItemList == null || wItemList.size() <= 0) {
				return wResult;
			}

			Optional<IPTItem> wOption = wItemList.stream().filter(p -> p.ID == wIPTItem.GroupID).findFirst();
			if (wOption.isPresent()) {
				wResult.add(wOption.get().Text);
				List<String> wTempList = GetFatherText(wItemList, wOption.get());
				if (wTempList != null && wTempList.size() > 0) {
					wResult.addAll(wTempList);
				}
			} else {
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override

	public ServiceResult<Integer> IPT_GiveSOPList(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem,
			List<IPTSOP> wIPTSOPList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wIPTPreCheckProblem == null || wIPTPreCheckProblem.ID <= 0) {
				return wResult;
			}

			if (wIPTPreCheckProblem.IPTProblemConfirmerList != null
					&& wIPTPreCheckProblem.IPTProblemConfirmerList.size() > 0) {
				wIPTPreCheckProblem.IPTProblemConfirmerList.forEach(p -> p.ProblemID = wIPTPreCheckProblem.ID);
				for (IPTProblemConfirmer wIPTProblemConfirmer : wIPTPreCheckProblem.IPTProblemConfirmerList) {
					IPTProblemConfirmerDAO.getInstance().Update(wLoginUser, wIPTProblemConfirmer, wErrorCode);
				}
				wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.ToConfirm.getValue();
			} else {
				wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.ToSendItem.getValue();

				// 更新台车BOM
				UpdateProblemBOM(wLoginUser, wIPTPreCheckProblem);
			}
			wIPTPreCheckProblem.CarftID = wLoginUser.ID;
			wIPTPreCheckProblem.CarftTime = Calendar.getInstance();

			IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);

			// ①关闭消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, -1, BPMEventModule.PreProblemHandle.getValue(),
							wIPTPreCheckProblem.ID, BFCMessageType.Task.getValue(), -1, -1, null, null)
					.List(BFCMessage.class);
			wMessageList = wMessageList.stream().filter(p -> p.StepID == 2).collect(Collectors.toList());
			for (BFCMessage wMessage : wMessageList) {
				if (wMessage.ResponsorID == wLoginUser.ID) {
					wMessage.Active = BFCMessageStatus.Finished.getValue();
				} else {
					wMessage.Active = BFCMessageStatus.Close.getValue();
				}
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
			// ②发送消息给确认人
			if (wIPTPreCheckProblem.Status == IPTPreCheckProblemStatus.ToConfirm.getValue()) {
				List<BFCMessage> wBFCMessageList = new ArrayList<>();
				BFCMessage wMessage = null;
				int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
						FMCShiftLevel.Day);
				for (IPTProblemConfirmer wItem : wIPTPreCheckProblem.IPTProblemConfirmerList) {
					// 发送任务消息到人员
					wMessage = new BFCMessage();
					wMessage.Active = 0;
					wMessage.CompanyID = 0;
					wMessage.CreateTime = Calendar.getInstance();
					wMessage.EditTime = Calendar.getInstance();
					wMessage.ID = 0;
					wMessage.MessageID = wIPTPreCheckProblem.ID;
					wMessage.Title = StringUtils.Format("【{0}】 {1}", BPMEventModule.PreProblemHandle.getLable(),
							String.valueOf(wShiftID));
					wMessage.MessageText = StringUtils.Format("模块：{0} 发起人：{1}  发起时刻：{2} 待确认",
							new Object[] { BPMEventModule.PreProblemHandle.getLable(), wLoginUser.Name,
									StringUtils.parseCalendarToString(Calendar.getInstance(), "yyyy-MM-dd HH:mm") })
							.trim();
					wMessage.ModuleID = BPMEventModule.PreProblemHandle.getValue();
					wMessage.ResponsorID = wItem.ConfirmerID;
					wMessage.ShiftID = wShiftID;
					wMessage.StationID = 0;
					wMessage.Type = BFCMessageType.Task.getValue();
					wMessage.StepID = 3;
					wBFCMessageList.add(wMessage);
				}
				CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
			}

			if (wIPTSOPList == null || wIPTSOPList.size() <= 0) {
				return wResult;
			}

			// 保存解决方案到数据库中
			int wSOPID = 0;
			for (IPTSOP wIPTSOP : wIPTSOPList) {
				wSOPID = IPTSOPDAO.getInstance().Update(wLoginUser, wIPTSOP, wErrorCode);
				if (wSOPID > 0) {
					wIPTSOP.ID = wSOPID;
				}
			}

			// 查询解决方案知识库
			IPTSolveLib wSolveLib = IPTSolveLibDAO.getInstance().SelectByID(wLoginUser, wIPTPreCheckProblem.SolveID,
					wErrorCode);
			if (wSolveLib == null || wSolveLib.ID <= 0) {
				return wResult;
			}

			// 更新解决方案知识库
			wSolveLib.IPTSOPList = wIPTSOPList;
			IPTSolveLibDAO.getInstance().Update(wLoginUser, wSolveLib, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 每次预检异常的物料数据单条评审完后添加到台车BOM中
	 * 
	 * @param wLoginUser
	 * @param wIPTPreCheckProblem
	 */
	private void UpdateProblemBOM(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem) {
		try {
			if (wIPTPreCheckProblem == null || wIPTPreCheckProblem.ID <= 0) {
				return;
			}

			if (wIPTPreCheckProblem.IPTProblemBomItemList == null
					|| wIPTPreCheckProblem.IPTProblemBomItemList.size() <= 0) {
				return;
			}

			List<MSSBOMItem> wMSSBOMItemList = new ArrayList<MSSBOMItem>();
			for (IPTProblemBomItem wIPTProblemBomItem : wIPTPreCheckProblem.IPTProblemBomItemList) {
				MSSBOMItem wItem = WMSServiceImpl.getInstance()
						.MSS_QueryBOMItemByID(wLoginUser, wIPTProblemBomItem.BOMID).Info(MSSBOMItem.class);
				if (wItem == null || wItem.ID <= 0) {
					continue;
				}
				wItem.MaterialNumber = wIPTProblemBomItem.Number;
				wMSSBOMItemList.add(wItem);
			}

			OMSOrder wOMSOrder = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderByID(wLoginUser, wIPTPreCheckProblem.OrderID).Info(OMSOrder.class);
			if (wOMSOrder == null || wOMSOrder.ID <= 0) {
				return;
			}

			for (MSSBOMItem wMSSBOMItem : wMSSBOMItemList) {
				APSBOMItem wAPSBOMItem = new APSBOMItem(wMSSBOMItem, wOMSOrder.LineID, wOMSOrder.ProductID,
						wOMSOrder.BureauSectionID, wOMSOrder.ID, wOMSOrder.OrderNo, wOMSOrder.PartNo);

				wAPSBOMItem.SourceID = wIPTPreCheckProblem.ID;
				wAPSBOMItem.SourceType = APSBOMSourceType.PreCheckProblem.getValue();
				wAPSBOMItem.AuditTime = Calendar.getInstance();
				wAPSBOMItem.EditTime = Calendar.getInstance();

				LOCOAPSServiceImpl.getInstance().APS_BOMItemUpdate(wLoginUser, wAPSBOMItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<Integer> IPT_SaveProblemList(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList,
			SFCTaskIPT wSFCTaskIPT) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wIPTValueList == null || wIPTValueList.size() <= 0
					|| wIPTValueList.stream().allMatch(p -> p.ItemType == IPTItemType.Write.getValue())) {
				return wResult;
			}

			wIPTValueList = wIPTValueList.stream().filter(p -> p.ItemType == IPTItemType.InPlant.getValue())
					.collect(Collectors.toList());
			if (wIPTValueList == null || wIPTValueList.size() <= 0) {
				return wResult;
			}

			int wDoClassID = 0;
			List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
			if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
				wDoClassID = QMSConstants.GetBMSEmployee(wSFCTaskStepList.get(0).MonitorID).DepartmentID;
			}

			// 遍历修改问题项的状态
			List<IPTPreCheckProblem> wProblemList = null;
			for (IPTValue wIPTValue : wIPTValueList) {
				wProblemList = IPTPreCheckProblemDAO.getInstance().SelectListByTask(wLoginUser, -1, wSFCTaskIPT.OrderID,
						wSFCTaskIPT.LineID, wSFCTaskIPT.ProductID, wSFCTaskIPT.StationID, wSFCTaskIPT.PartPointID,
						new ArrayList<Integer>(Arrays.asList(IPTPreCheckProblemStatus.Issued.getValue(),
								IPTPreCheckProblemStatus.ToMutual.getValue(),
								IPTPreCheckProblemStatus.ToSpecial.getValue())),
						null, null, wErrorCode);
				if (wProblemList == null || wProblemList.size() <= 0) {
					continue;
				}
				switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
				case SelfCheck:// 自检
					if (wIPTValue.Result == 1) {
						int wClassID = wDoClassID;
						wProblemList.forEach(p -> p.Status = IPTPreCheckProblemStatus.ToMutual.getValue());
						wProblemList.forEach(p -> p.DoClassID = wClassID);
						wProblemList.forEach(p -> p.DoPersonID = wLoginUser.ID);
					} else if (wIPTValue.Result == 2) {
						wProblemList.forEach(p -> p.Status = IPTPreCheckProblemStatus.Issued.getValue());
					}
					break;
				case MutualCheck:// 互检
					if (wIPTValue.Result == 1) {
						wProblemList.forEach(p -> p.Status = IPTPreCheckProblemStatus.ToSpecial.getValue());
					} else if (wIPTValue.Result == 2) {
						wProblemList.forEach(p -> p.Status = IPTPreCheckProblemStatus.Issued.getValue());
					}
					break;
				case SpecialCheck:// 专检
					wProblemList.forEach(p -> p.Status = IPTPreCheckProblemStatus.Done.getValue());
					break;
				default:
					break;
				}
				for (IPTPreCheckProblem wIPTPreCheckProblem : wProblemList) {
					IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Map<Integer, Integer>> IPT_QueryPointTree(BMSEmployee wLoginUser, Integer wProductID,
			int wLineID, int wPartID) {
		ServiceResult<Map<Integer, Integer>> wResult = new ServiceResult<Map<Integer, Integer>>();
		wResult.Result = new HashMap<Integer, Integer>();
		try {
			List<FMCLineUnit> wLineUnitList = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(wLoginUser, wLineID, -1, wProductID, false).List(FMCLineUnit.class);
			if (wLineUnitList == null || wLineUnitList.size() <= 0) {
				return wResult;
			}

			// 找到此工位的所有工序
			wLineUnitList = wLineUnitList.stream().filter(p -> p.LevelID == 3 && p.ParentUnitID == wPartID)
					.collect(Collectors.toList());
			if (wLineUnitList == null || wLineUnitList.size() <= 0) {
				return wResult;
			}

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			int wGray = 1;
			int wRed = 2;
			int wGreen = 3;
			IPTStandard wIPTStandard;
			List<IPTStandard> wList;
			for (FMCLineUnit wFMCLineUnit : wLineUnitList) {
				// 当前标准
				wIPTStandard = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.Default, -1,
						-1, -1, -1, -1, wLineID, wPartID, wFMCLineUnit.UnitID, -1, wProductID, wErrorCode).Result;
				if (wIPTStandard != null && wIPTStandard.ID > 0) {
					wResult.Result.put(wFMCLineUnit.UnitID, wGreen);
				} else {
					wList = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, -1, -1, IPTMode.Default, -1, -1,
							-1, -1, -1, wLineID, wPartID, wFMCLineUnit.UnitID, -1, wProductID, null, -1, 100, null,
							null, wErrorCode);
					if (wList != null && wList.size() > 0) {
						wResult.Result.put(wFMCLineUnit.UnitID, wRed);
					} else {
						wResult.Result.put(wFMCLineUnit.UnitID, wGray);
					}
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Map<Integer, Integer>> IPT_QueryProductTree(BMSEmployee wLoginUser, int wLineID, int wPartID) {
		ServiceResult<Map<Integer, Integer>> wResult = new ServiceResult<Map<Integer, Integer>>();
		wResult.Result = new HashMap<Integer, Integer>();
		try {
			List<FPCProduct> wList = FMCServiceImpl.getInstance().FPC_QueryProductList(wLoginUser, -1, -1)
					.List(FPCProduct.class);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			int wGreen = 3;
			int wRed = 2;
			int wGray = 1;
			Map<Integer, Integer> wPointTreeMap;
			for (FPCProduct wFPCProduct : wList) {
				wPointTreeMap = IPT_QueryPointTree(wLoginUser, wFPCProduct.ID, wLineID, wPartID).Result;
				if (wPointTreeMap != null && wPointTreeMap.values().size() > 0
						&& wPointTreeMap.values().stream().allMatch(p -> p == 3)) {
					wResult.Result.put(wFPCProduct.ID, wGreen);
				} else if (wPointTreeMap != null && wPointTreeMap.values().size() > 0
						&& wPointTreeMap.values().stream().anyMatch(p -> p == 3)) {
					wResult.Result.put(wFPCProduct.ID, wRed);
				} else {
					wResult.Result.put(wFPCProduct.ID, wGray);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTItem>> IPT_QueryIPTItemTree(BMSEmployee wLoginUser, List<IPTItem> wList) {
		ServiceResult<List<IPTItem>> wResult = new ServiceResult<List<IPTItem>>();
		wResult.Result = new ArrayList<IPTItem>();
		try {
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			wResult.Result = wList.stream().filter(p -> p.GroupID == 0).collect(Collectors.toList());
			if (wResult.Result != null && wResult.Result.size() > 0) {
				for (IPTItem wIPTItem : wResult.Result) {
					this.SetItemList(wIPTItem, wList);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private void SetItemList(IPTItem wItem, List<IPTItem> wList) {
		try {
			List<IPTItem> wChildList = this.FindChild(wList, wItem);
			if (wChildList == null || wChildList.size() <= 0) {
				return;
			} else {
				wItem.IPTItemList = wChildList;
				for (IPTItem wIPTItem : wChildList) {
					SetItemList(wIPTItem, wList);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private List<IPTItem> FindChild(List<IPTItem> wList, IPTItem wItem) {
		List<IPTItem> wResult = new ArrayList<IPTItem>();
		try {
			if (wList == null || wList.size() <= 0 || wItem == null || wItem.ID <= 0) {
				return wResult;
			}

			wResult = wList.stream().filter(p -> p.GroupID == wItem.ID).collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_ImportPartPoint(BMSEmployee wLoginUser, ExcelData wExcelData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			wResult.Result = QMSUtils.getInstance().IPT_ImportPartPoint(wLoginUser, wExcelData);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> IPT_ImportBOM(BMSEmployee wLoginUser, ExcelData wExcelData, String wFileName) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			wResult = QMSUtils.getInstance().IPT_ImportBOM(wLoginUser, wExcelData, wFileName);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_ExportBOM(BMSEmployee wLoginUser, List<MSSBOMItem> wItemList,
			HttpServletResponse response) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			MSSBOM wBOM = WMSServiceImpl.getInstance().MSS_QueryBOM(wLoginUser, wItemList.get(0).BOMID, "")
					.Custom("list", MSSBOM.class);
			if (wBOM == null || wBOM.ID <= 0) {
				wResult.FaultCode += "BOM未找到!";
				return wResult;
			}
			String wProductNo = QMSConstants.GetFPCProductNo(wBOM.ProductID);
			String wLine = QMSConstants.GetFMCLineName(wBOM.LineID);
			// 清空下载文件的空白行（空白行是因为有的前端代码编译后产生的）
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			response.reset();
			String wFileName = StringUtils.Format("{0}-{1}-{2}-BOM数据.xls", wProductNo, wLine, wCurTime);
			wFileName = URLEncoder.encode(wFileName, "UTF-8");
			// 设置响应头，把文件名字设置好
			response.setHeader("Content-Disposition", "attachment; filename=" + wFileName);
			// 解决编码问题
			response.setContentType("application/octet-stream; charset=utf-8");
			// 导出BOM数据
			wResult.Result = QMSUtils.getInstance().IPT_ExportBOM(wLoginUser, response.getOutputStream(), wBOM,
					wItemList);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> ExportYJReport(BMSEmployee wLoginUser, Integer wOrderID,
			HttpServletResponse response) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OMSOrder wOMSOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOMSOrder == null || wOMSOrder.ID <= 0) {
				wResult.FaultCode += "提示：参数错误!";
				return wResult;
			}

			IPTPreCheckReport wReport = SFCServiceImpl.getInstance().SFC_QueryPreCheckReport(wLoginUser,
					wOMSOrder.PartNo).Result;
			if (wReport == null) {
				wResult.FaultCode += "提示：预检数据丢失!";
				return wResult;
			}

			// 清空下载文件的空白行（空白行是因为有的前端代码编译后产生的）
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			response.reset();
			String wFileName = StringUtils.Format("{0}-{1}-{2}-机车预检报告.xls", wOMSOrder.PartNo, wOMSOrder.LineName,
					wCurTime);
			wFileName = URLEncoder.encode(wFileName, "UTF-8");
			// 设置响应头，把文件名字设置好
			response.setHeader("Content-Disposition", "attachment; filename=" + wFileName);
			// 解决编码问题
			response.setContentType("application/octet-stream; charset=utf-8");

			ExcelUtil.YJ_CreateTitle(StringUtils.Format("{0}-{1}-{2}-机车预检报告", wOMSOrder.PartNo, wOMSOrder.LineName,
					wReport.CustomerName));
			ExcelUtil.YJ_CreateHeaders(
					new String[] { "序号", "检查项点", "质量标准", "图例", "厂家", "型号", "编号", "填写值", "结果", "备注", "图片", "视频" });
			int wRowNum = 2;
			int wIndex = 1;
			IPTValue wIPTValue;
			List<String> wValueList;
			for (IPTPreCheckItem wIPTPreCheckItem : wReport.IPTPreCheckItemList) {
				// 创建工序
				ExcelUtil.YJ_CreatePartPoint(NewCreditReportUtil.toChinese(String.valueOf(wIndex++)),
						wIPTPreCheckItem.ItemName, wRowNum++);
				// 处理编号问题
				wIPTPreCheckItem.IPTItemList = NewCreditReportUtil.handleNumber(wIPTPreCheckItem.IPTItemList);
				for (IPTItem wIPTItem : wIPTPreCheckItem.IPTItemList) {
					if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
						ExcelUtil.YJ_CreateGroup(wIPTItem.Code, wIPTItem.Text, wRowNum++);
					} else {
						// 找到值
						if (wIPTPreCheckItem.IPTValueList != null
								&& wIPTPreCheckItem.IPTValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
							wIPTValue = wIPTPreCheckItem.IPTValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
									.findFirst().get();
							wValueList = new ArrayList<String>(
									Arrays.asList(wIPTItem.Code, wIPTItem.Text, wIPTItem.Standard, wIPTItem.Legend,
											wIPTValue.Manufactor, wIPTValue.Modal, wIPTValue.Number, wIPTValue.Value,
											wIPTValue.Result == 1 ? "合格" : "不合格", wIPTValue.Remark,
											(wIPTValue.ImagePath == null || wIPTValue.ImagePath.size() <= 0) ? ""
													: wIPTValue.ImagePath.get(0),
											(wIPTValue.VideoPath == null || wIPTValue.VideoPath.size() <= 0) ? ""
													: wIPTValue.VideoPath.get(0)));
						} else {
							wValueList = new ArrayList<String>(Arrays.asList(wIPTItem.Code, wIPTItem.Text,
									wIPTItem.Standard, wIPTItem.Legend, "", "", "", "", "", "", "", ""));
						}
						ExcelUtil.WriteRowItem(wValueList, wRowNum++);
					}
				}
			}
			// 导出
			ExcelUtil.Export(response.getOutputStream());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTProblemAssess>> IPT_QueryProblemAssessListByEmployee(BMSEmployee wLoginUser) {
		ServiceResult<List<IPTProblemAssess>> wResult = new ServiceResult<List<IPTProblemAssess>>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// 待做集合
			List<IPTProblemAssess> wToDoList = IPTProblemAssessDAO.getInstance().SelectList(wLoginUser, -1, -1,
					wLoginUser.ID, 0, null, null, wErrorCode);
			for (IPTProblemAssess wIPTProblemAssess : wToDoList) {
				wIPTProblemAssess.IPTPreCheckProblem = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
						wIPTProblemAssess.ProblemID, wErrorCode);
			}
			// 已做集合
			Calendar wStartTime = Calendar.getInstance();
			wStartTime.set(Calendar.HOUR_OF_DAY, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);
			wStartTime.add(Calendar.DATE, -7);

			Calendar wEndTime = Calendar.getInstance();
			wStartTime.set(Calendar.HOUR_OF_DAY, 23);
			wStartTime.set(Calendar.MINUTE, 59);
			wStartTime.set(Calendar.SECOND, 59);
			List<IPTProblemAssess> wDoneList = IPTProblemAssessDAO.getInstance().SelectList(wLoginUser, -1, -1,
					wLoginUser.ID, 1, wStartTime, wEndTime, wErrorCode);
			for (IPTProblemAssess wIPTProblemAssess : wDoneList) {
				wIPTProblemAssess.IPTPreCheckProblem = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
						wIPTProblemAssess.ProblemID, wErrorCode);
			}
			wResult.Result = wToDoList;
			wResult.CustomResult.put("Done", wDoneList);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_AuditProblemAssess(BMSEmployee wLoginUser, IPTProblemAssess wIPTProblemAssess) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wIPTProblemAssess == null || wIPTProblemAssess.ID <= 0) {
				return wResult;
			}

			wIPTProblemAssess.Status = 1;
			wIPTProblemAssess.AuditTime = Calendar.getInstance();
			int wID = IPTProblemAssessDAO.getInstance().Update(wLoginUser, wIPTProblemAssess, wErrorCode);
			if (wID <= 0) {
				return wResult;
			}

			// ①关闭任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, -1, BPMEventModule.PreProblemHandle1.getValue(),
							wIPTProblemAssess.ID, BFCMessageType.Task.getValue(), -1, -1, null, null)
					.List(BFCMessage.class);
			wMessageList = wMessageList.stream().filter(p -> p.StepID == 1).collect(Collectors.toList());
			for (BFCMessage wMessage : wMessageList) {
				if (wMessage.ResponsorID == wLoginUser.ID) {
					wMessage.Active = BFCMessageStatus.Finished.getValue();
				}
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);

			List<IPTProblemAssess> wList = IPTProblemAssessDAO.getInstance().SelectList(wLoginUser, -1,
					wIPTProblemAssess.ProblemID, -1, -1, null, null, wErrorCode);
			if (wList != null && wList.size() > 0 && wList.stream().allMatch(p -> p.Status == 1)) {
				IPTPreCheckProblem wItem = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
						wIPTProblemAssess.ProblemID, wErrorCode);
				if (wItem != null && wItem.ID > 0) {
					wItem.Status = IPTPreCheckProblemStatus.ToCraftGiveSolve.getValue();
					IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wItem, wErrorCode);

					// ①发送任务消息给预检工位工艺师
					if (QMSConstants.GetFPCPartList().values().stream()
							.anyMatch(p -> p.PartType == FPCPartTypes.PrevCheck.getValue())) {
						FPCPart wPart = QMSConstants.GetFPCPartList().values().stream()
								.filter(p -> p.PartType == FPCPartTypes.PrevCheck.getValue()).findFirst().get();
						List<BFCMessage> wBFCMessageList = new ArrayList<>();
						BFCMessage wMessage = null;
						int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(),
								APSShiftPeriod.Day, FMCShiftLevel.Day);
						for (int wUserID : wPart.TechnicianList) {
							// 发送任务消息到人员
							wMessage = new BFCMessage();
							wMessage.Active = 0;
							wMessage.CompanyID = 0;
							wMessage.CreateTime = Calendar.getInstance();
							wMessage.EditTime = Calendar.getInstance();
							wMessage.ID = 0;
							wMessage.MessageID = wItem.ID;
							wMessage.Title = StringUtils.Format("【{0}】 {1}", BPMEventModule.PreProblemHandle.getLable(),
									String.valueOf(wShiftID));
							wMessage.MessageText = StringUtils.Format("模块：{0} 发起人：{1}  发起时刻：{2} 待现场工艺给解决方案",
									new Object[] { BPMEventModule.PreProblemHandle.getLable(), wLoginUser.Name,
											StringUtils.parseCalendarToString(Calendar.getInstance(),
													"yyyy-MM-dd HH:mm") })
									.trim();
							wMessage.ModuleID = BPMEventModule.PreProblemHandle.getValue();
							wMessage.ResponsorID = wUserID;
							wMessage.ShiftID = wShiftID;
							wMessage.StationID = 0;
							wMessage.Type = BFCMessageType.Task.getValue();
							wMessage.StepID = 2;
							wBFCMessageList.add(wMessage);
						}
						CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
					}
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_ProblemSendAudit(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wIPTPreCheckProblem == null || wIPTPreCheckProblem.ID <= 0
					|| wIPTPreCheckProblem.IPTProblemAssessList == null
					|| wIPTPreCheckProblem.IPTProblemAssessList.size() <= 0) {
				wResult.FaultCode += "提示：参数错误!";
				return wResult;
			}

			for (IPTProblemAssess wIPTProblemAssess : wIPTPreCheckProblem.IPTProblemAssessList) {
				wIPTProblemAssess.ID = IPTProblemAssessDAO.getInstance().Update(wLoginUser, wIPTProblemAssess,
						wErrorCode);
			}

			wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.ToRelaPersonAudit.getValue();
			IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);

			// ①关闭处理消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, -1, BPMEventModule.PreProblemHandle.getValue(),
							wIPTPreCheckProblem.ID, BFCMessageType.Task.getValue(), -1, -1, null, null)
					.List(BFCMessage.class);
			wMessageList = wMessageList.stream().filter(p -> p.StepID == 0).collect(Collectors.toList());
			for (BFCMessage wMessage : wMessageList) {
				if (wMessage.ResponsorID == wLoginUser.ID) {
					wMessage.Active = BFCMessageStatus.Finished.getValue();
				} else {
					wMessage.Active = BFCMessageStatus.Close.getValue();
				}
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
			// ②发送任务消息给相关评审人
			List<BFCMessage> wList = new ArrayList<BFCMessage>();
			BFCMessage wMessage = null;
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);
			for (IPTProblemAssess wIPTProblemAssess : wIPTPreCheckProblem.IPTProblemAssessList) {
				// 发送任务消息到人员
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wIPTProblemAssess.ID;
				wMessage.Title = StringUtils.Format("【{0}】 {1}", BPMEventModule.PreProblemHandle1.getLable(),
						String.valueOf(wShiftID));
				wMessage.MessageText = StringUtils
						.Format("模块：{0} 发起人：{1}  发起时刻：{2} 待相关人员评审",
								new Object[] { BPMEventModule.PreProblemHandle1.getLable(), wLoginUser.Name,
										StringUtils.parseCalendarToString(Calendar.getInstance(), "yyyy-MM-dd HH:mm") })
						.trim();
				wMessage.ModuleID = BPMEventModule.PreProblemHandle1.getValue();
				wMessage.ResponsorID = wIPTProblemAssess.AuditID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = 0;
				wMessage.Type = BFCMessageType.Task.getValue();
				wMessage.StepID = 1;
				wList.add(wMessage);
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wList);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_HandlePreCheckProblem(BMSEmployee wLoginUser, IPTProblemActionType wActionType,
			List<IPTPreCheckProblem> wList, String wRemark) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			switch (wActionType) {
			case CraftSendAudit:
				for (IPTPreCheckProblem wIPTPreCheckProblem : wList) {
					wResult = this.IPT_ProblemSendAudit(wLoginUser, wIPTPreCheckProblem);
					if (StringUtils.isNotEmpty(wResult.FaultCode)) {
						return wResult;
					}
				}
				break;
			case CraftGiveSOP:
				for (IPTPreCheckProblem wIPTPreCheckProblem : wList) {
					wResult = this.IPT_GiveSOPList(wLoginUser, wIPTPreCheckProblem, wIPTPreCheckProblem.IPTSOPList);
					if (StringUtils.isNotEmpty(wResult.FaultCode)) {
						return wResult;
					}
				}
				break;
			case ManagerConfirm:
				for (IPTPreCheckProblem wIPTPreCheckProblem : wList) {
					wResult = this.IPT_ManagerConfirm(wLoginUser, wIPTPreCheckProblem, wRemark);
					if (StringUtils.isNotEmpty(wResult.FaultCode)) {
						return wResult;
					}
				}
				break;
			default:
				wResult.FaultCode += RetCode.SERVER_RST_ERROR_OUT;
				return wResult;
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private ServiceResult<Integer> IPT_ManagerConfirm(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem,
			String wRemark) {
		ServiceResult<Integer> wResult = new ServiceResult<>(Integer.valueOf(0));
		OutResult<Integer> wErrorCode = new OutResult<>(Integer.valueOf(0));
		try {
			if (wIPTPreCheckProblem.IPTProblemConfirmerList == null
					|| wIPTPreCheckProblem.IPTProblemConfirmerList.size() <= 0) {
				return wResult;
			}

			if (!wIPTPreCheckProblem.IPTProblemConfirmerList.stream().anyMatch(p -> p.ConfirmerID == wLoginUser.ID)) {
				return wResult;
			}

			IPTProblemConfirmer wConfrimItem = wIPTPreCheckProblem.IPTProblemConfirmerList.stream()
					.filter(p -> p.ConfirmerID == wLoginUser.ID).findFirst().get();
			wConfrimItem.Status = 1;
			wConfrimItem.ConfirmTime = Calendar.getInstance();
			wConfrimItem.Remark = wRemark;
			IPTProblemConfirmerDAO.getInstance().Update(wLoginUser, wConfrimItem, wErrorCode);

			List<IPTProblemConfirmer> wList = IPTProblemConfirmerDAO.getInstance().SelectList(wLoginUser, -1,
					wIPTPreCheckProblem.ID, -1, null, wErrorCode);

			if (wList != null && wList.size() > 0 && wList.stream().allMatch(p -> p.Status == 1)) {
				wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.ToSendItem.getValue();
				IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);

				// 更新台车BOM
				UpdateProblemBOM(wLoginUser, wIPTPreCheckProblem);
			}

			// ①关闭消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, -1, BPMEventModule.PreProblemHandle.getValue(),
							wIPTPreCheckProblem.ID, BFCMessageType.Task.getValue(), -1, -1, null, null)
					.List(BFCMessage.class);
			wMessageList = wMessageList.stream().filter(p -> p.StepID == 3).collect(Collectors.toList());
			for (BFCMessage wMessage : wMessageList) {
				if (wMessage.ResponsorID == wLoginUser.ID) {
					wMessage.Active = BFCMessageStatus.Finished.getValue();
				}
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);

			wResult.setFaultCode(MESException.getEnumType(((Integer) wErrorCode.Result).intValue()).getLable());
		} catch (Exception e) {
			wResult.FaultCode = String.valueOf(wResult.FaultCode) + e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTPreCheckReport> IPT_CreatePreCheckReport(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<IPTPreCheckReport> wResult = new ServiceResult<IPTPreCheckReport>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<IPTPreCheckReport> wIsExist = IPTPreCheckReportDAO.getInstance().SelectList(wLoginUser, -1, "", -1,
					wOrderID, null, null, null, wErrorCode);
			if (wIsExist != null && wIsExist.size() > 0) {
				return wResult;
			}

			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				wResult.FaultCode += RetCode.SERVER_RST_ERROR_OUT;
				return wResult;
			}

			if (wOrder.RouteID <= 0) {
				wResult.FaultCode += "提示：该订单无工艺路线!";
				return wResult;
			}

			int wRouteID = wOrder.RouteID;
			// 工艺数据
//			List<FPCRoutePart> wFPCRoutePartList = FMCServiceImpl.getInstance()
//					.FPC_QueryRoutePartListByRouteID(wLoginUser, 0).List(FPCRoutePart.class);
//			List<FPCRoutePartPoint> wFPCRoutePartPointList = FMCServiceImpl.getInstance()
//					.FPC_QueryRoutePartPointListByRouteID(wLoginUser, 0, 0).List(FPCRoutePartPoint.class);
			// 判断该车是否具备生成报告的条件
			List<Integer> wYJStationIDList = QMSUtils.getInstance().GetYJStationIDList(wLoginUser);
			int wYJStationID = 0;
			if (wYJStationIDList != null && wYJStationIDList.size() > 0) {
				wYJStationID = wYJStationIDList.get(0);
			}

			List<FPCRoutePartPoint> wRoutePartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartPointListByRouteID(wLoginUser, wRouteID, wYJStationID)
					.List(FPCRoutePartPoint.class);

			// ①所有的工序完工
			List<Integer> wToDoPartPointIDList = wRoutePartPointList.stream().map(p -> p.PartPointID).distinct()
					.collect(Collectors.toList());
			if (wToDoPartPointIDList == null || wToDoPartPointIDList.size() <= 0) {
				wResult.FaultCode += "提示：工艺工序数据缺失!";
				return wResult;
			}
			for (Integer wStepID : wToDoPartPointIDList) {
				List<SFCTaskIPT> wList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID,
						wYJStationID, SFCTaskType.PreCheck.getValue(), -1, wStepID, -1, "", null, null, wErrorCode);
				if (wList != null && wList.size() > 0
						&& wList.stream().anyMatch(p -> p.Status == SFCTaskStatus.Done.getValue())) {
					continue;
				} else {
					wResult.FaultCode += StringUtils.Format("提示：【{0}】工序未完成，无法生成预检报告!",
							QMSConstants.GetFPCStepName(wStepID));
					return wResult;
				}
			}
			// ②所有的问题项的状态都变为问题项待下发
			List<IPTPreCheckProblem> wList = IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, wOrderID, -1,
					-1, -1, -1, -1, -1, wErrorCode);
			if (wList != null && wList.size() > 0
					&& wList.stream().anyMatch(p -> p.Status != IPTPreCheckProblemStatus.ToSendItem.getValue())) {
				wResult.FaultCode += StringUtils.Format("提示：问题项【{0}】未处理完，无法生成预检报告!",
						wList.stream().filter(p -> p.Status != IPTPreCheckProblemStatus.ToSendItem.getValue())
								.findFirst().get().IPTItemName);
				return wResult;
			}
			// 生成台车预检报告
			IPTPreCheckReport wIPTPreCheckReport = SFCServiceImpl.getInstance().SFC_QueryPreCheckReport(wLoginUser,
					wOrder.PartNo).Result;

			wIPTPreCheckReport.Code = IPTPreCheckReportDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wIPTPreCheckReport.UpFlowID = wLoginUser.ID;
			wIPTPreCheckReport.UpFlowName = wLoginUser.Name;
			wIPTPreCheckReport.CreateTime = Calendar.getInstance();
			wIPTPreCheckReport.SubmitTime = Calendar.getInstance();
			wIPTPreCheckReport.ID = 0;
			wIPTPreCheckReport.Status = IPTPreCheckReportStatus.Default.getValue();
			wIPTPreCheckReport.StatusText = "";
			wIPTPreCheckReport.FlowType = BPMEventModule.YJReport.getValue();

			IPTPreCheckReport wNewObj = IPTPreCheckReportDAO.getInstance().Update(wLoginUser, wIPTPreCheckReport,
					wErrorCode);
			if (wNewObj.ID > 0) {
				wIPTPreCheckReport.ID = wNewObj.ID;
				for (IPTPreCheckItem wIPTPreCheckItem : wIPTPreCheckReport.IPTPreCheckItemList) {
					wIPTPreCheckItem.ReportID = wNewObj.ID;
					IPTPreCheckItemDAO.getInstance().Update(wLoginUser, wIPTPreCheckItem, wErrorCode);
				}
				wResult.Result = wIPTPreCheckReport;
			} else {
				wResult.Result = new IPTPreCheckReport();
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 根据修程、车型、工位获取工序ID集合
	 * 
	 * @param wFPCProductRouteList   产品工艺列表
	 * @param wFPCRoutePartList      工位工艺列表
	 * @param wFPCRoutePartPointList 工序工艺列表
	 * @param wPartID                工位ID
	 * @param wLineName              修程
	 * @param wProductNo             产品型号
	 * @return 工序ID集合
	 */
	@SuppressWarnings("unused")
	private List<Integer> IPT_GetPartPointIDList(List<FPCRoutePart> wFPCRoutePartList,
			List<FPCRoutePartPoint> wFPCRoutePartPointList, int wPartID, int wRouteID) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			Optional<FPCRoutePart> wPartOption = wFPCRoutePartList.stream()
					.filter(p -> p.RouteID == wRouteID && p.PartID == wPartID).findFirst();
			if (!wPartOption.isPresent()) {
				return wResult;
			}

			FPCRoutePart wFPCRoutePart = wPartOption.get();

			List<FPCRoutePartPoint> wList = wFPCRoutePartPointList.stream()
					.filter(p -> p.PartID == wFPCRoutePart.PartID && p.RouteID == wRouteID)
					.collect(Collectors.toList());

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			wResult = wList.stream().map(p -> p.PartPointID).collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTPreCheckReport>> IPT_QueryReportAuditList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<IPTPreCheckReport>> wResult = new ServiceResult<List<IPTPreCheckReport>>();
		wResult.Result = new ArrayList<IPTPreCheckReport>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
//			List<OMSOrder> wOrderList = PDFServiceImpl.getInstance().IPT_QeuryPDFOrderList(wLoginUser, wStartTime,
//					wEndTime).Result;
			List<OMSOrder> wOrderList = IPTPreCheckReportDAO.getInstance().IPT_QueryOrderList(wLoginUser, wStartTime,
					wEndTime, wErrorCode);
			if (wOrderList == null || wOrderList.size() <= 0) {
				return wResult;
			}

			for (OMSOrder wOMSOrder : wOrderList) {
				List<IPTPreCheckReport> wReport = IPTPreCheckReportDAO.getInstance().SelectList(wLoginUser, -1, "", -1,
						wOMSOrder.ID, null, null, null, wErrorCode);
				if (wReport == null || wReport.size() <= 0) {
					IPTPreCheckReport wIPTPreCheckReport = new IPTPreCheckReport();
					wIPTPreCheckReport.ID = 0;
					wIPTPreCheckReport.OrderID = wOMSOrder.ID;
					wIPTPreCheckReport.OrderNo = wOMSOrder.OrderNo;
					wIPTPreCheckReport.CustomerName = wOMSOrder.BureauSection;
					wIPTPreCheckReport.LineName = wOMSOrder.LineName;
					wIPTPreCheckReport.PartNo = wOMSOrder.PartNo;
					wResult.Result.add(wIPTPreCheckReport);
				} else {
					wReport.get(0).LineName = wOMSOrder.LineName;
					wResult.Result.add(wReport.get(0));
				}
			}
			// 返回错误信息
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_AuditPreCheckReport(BMSEmployee wLoginUser, List<IPTPreCheckReport> wList,
			APSOperateType wOperateType) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wList == null || wList.size() <= 0) {
				wResult.FaultCode += RetCode.SERVER_RST_ERROR_OUT;
				return wResult;
			}

			for (IPTPreCheckReport wIPTPreCheckReport : wList) {
				IPTPreCheckReportDAO.getInstance().Audit(wLoginUser, wIPTPreCheckReport, wOperateType.getValue(),
						wErrorCode);
				if (wErrorCode.Result != 0) {
					wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
					return wResult;
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportPreCheckReport(BMSEmployee wLoginUser, IPTPreCheckReport wIPTPreCheckReport) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			// 清空下载文件的空白行（空白行是因为有的前端代码编译后产生的）
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}-机车预检报告.xls", wCurTime,
					wIPTPreCheckReport.PartNo.replace("#", "-"));

//			response.reset();
//			wFileName = URLEncoder.encode(wFileName, "UTF-8");
//			// 设置响应头，把文件名字设置好
//			response.setHeader("Content-Disposition", "attachment; filename=" + wFileName);
//			// 解决编码问题
//			response.setContentType("application/octet-stream; charset=utf-8");

			ExcelUtil.YJ_CreateTitle(
					StringUtils.Format("{0}-{1}-机车预检报告", wIPTPreCheckReport.PartNo, wIPTPreCheckReport.CustomerName));
			ExcelUtil.YJ_CreateHeaders(
					new String[] { "序号", "检查项点", "质量标准", "图例", "厂家", "型号", "编号", "填写值", "结果", "备注", "图片", "视频" });
			int wRowNum = 2;
			int wIndex = 1;
			IPTValue wIPTValue;
			List<String> wValueList;
			for (IPTPreCheckItem wIPTPreCheckItem : wIPTPreCheckReport.IPTPreCheckItemList) {
				// 创建工序
				ExcelUtil.YJ_CreatePartPoint(NewCreditReportUtil.toChinese(String.valueOf(wIndex++)),
						wIPTPreCheckItem.ItemName, wRowNum++);
				// 处理编号问题
				wIPTPreCheckItem.IPTItemList = NewCreditReportUtil.handleNumber(wIPTPreCheckItem.IPTItemList);
				for (IPTItem wIPTItem : wIPTPreCheckItem.IPTItemList) {
					if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
						ExcelUtil.YJ_CreateGroup(wIPTItem.Code, wIPTItem.Text, wRowNum++);
					} else {
						// 找到值
						if (wIPTPreCheckItem.IPTValueList != null
								&& wIPTPreCheckItem.IPTValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
							wIPTValue = wIPTPreCheckItem.IPTValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
									.findFirst().get();
							wValueList = new ArrayList<String>(
									Arrays.asList(wIPTItem.Code, wIPTItem.Text, wIPTItem.Standard, wIPTItem.Legend,
											wIPTValue.Manufactor, wIPTValue.Modal, wIPTValue.Number, wIPTValue.Value,
											wIPTValue.Result == 1 ? "合格" : "不合格", wIPTValue.Remark,
											(wIPTValue.ImagePath == null || wIPTValue.ImagePath.size() <= 0) ? ""
													: wIPTValue.ImagePath.get(0),
											(wIPTValue.VideoPath == null || wIPTValue.VideoPath.size() <= 0) ? ""
													: wIPTValue.VideoPath.get(0)));
						} else {
							wValueList = new ArrayList<String>(Arrays.asList(wIPTItem.Code, wIPTItem.Text,
									wIPTItem.Standard, wIPTItem.Legend, "", "", "", "", "", "", "", ""));
						}
						ExcelUtil.WriteRowItem(wValueList, wRowNum++);
					}
				}
			}

			// ①获取本地文件路径
			String wDirePath = StringUtils.Format("{0}static/export/",
					Constants.getConfigPath().replace("config/", ""));
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			// ②创建本地文件
			String wFilePath = StringUtils.Format("{0}{1}", wDirePath, wFileName);
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			// ③写入本地文件
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			ExcelUtil.Export(wFileOutputStream);
			// ④返回文件链接
			String wUri = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);
			wResult.Result = wUri;
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 生成台车BOM
	 */
	@Override
	public ServiceResult<List<APSBOMItem>> IPT_CreateAPSBOMItemList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<APSBOMItem>> wResult = new ServiceResult<>();
		wResult.Result = new ArrayList<>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			OMSOrder wOMSOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOMSOrder == null || wOMSOrder.ID <= 0) {
				ServiceResult<List<APSBOMItem>> serviceResult = wResult;
				serviceResult.FaultCode = String.valueOf(serviceResult.FaultCode) + RetCode.SERVER_RST_ERROR_OUT;
				return wResult;
			}
			List<IPTPreCheckProblem> wProblemList = IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser,
					wOMSOrder.ID, -1, -1, -1, -1, -1, -1, wErrorCode);
			List<MSSBOMItem> wMSSBOMItemList = (List<MSSBOMItem>) WMSServiceImpl.getInstance()
					.MSS_QueryBOMItemAll(wLoginUser, 0, wOMSOrder.LineID, wOMSOrder.ProductID, wOMSOrder.CustomerID, 0,
							0, 0, 2)
					.List(MSSBOMItem.class);
			for (IPTPreCheckProblem wIPTPreCheckProblem : wProblemList) {
				if (wIPTPreCheckProblem.IPTProblemBomItemList != null) {
					if (wIPTPreCheckProblem.IPTProblemBomItemList.size() <= 0) {
						continue;
					}
					for (IPTProblemBomItem wIPTProblemBomItem : wIPTPreCheckProblem.IPTProblemBomItemList) {
						if (wMSSBOMItemList.stream().anyMatch(p -> p.ID == wIPTProblemBomItem.BOMID)) {
							wMSSBOMItemList.add(wMSSBOMItemList.stream().filter(p -> p.ID == wIPTProblemBomItem.BOMID)
									.findFirst().get());
						}
					}
				}
			}
			List<APSBOMItem> wAPSBOMItemList = new ArrayList<APSBOMItem>();
			APSBOMItem wAPSBOMItem = null;
			for (MSSBOMItem wMSSBOMItem : wMSSBOMItemList) {
				wAPSBOMItem = new APSBOMItem(wMSSBOMItem, wOMSOrder.LineID, wOMSOrder.ProductID,
						wOMSOrder.BureauSectionID, wOMSOrder.ID, wOMSOrder.WBSNo, wOMSOrder.PartNo);
				wAPSBOMItemList.add(wAPSBOMItem);
			}
			wResult.Result = WMSServiceImpl.getInstance().APS_SaveBOMItemList(wLoginUser, wAPSBOMItemList)
					.List(APSBOMItem.class);
			if (wResult.Result == null) {
				wResult.Result = new ArrayList<>();
			}
		} catch (Exception e) {
			ServiceResult<List<APSBOMItem>> serviceResult2 = wResult;
			serviceResult2.FaultCode = String.valueOf(serviceResult2.FaultCode) + e.toString();
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 导出台车BOM
	 */
	@Override
	public ServiceResult<String> IPT_ExportAPSBOM(BMSEmployee wLoginUser, List<APSBOMItem> wItemList) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			if (wItemList == null || wItemList.size() <= 0) {
				return wResult;
			}

			String wPartNo = wItemList.get(0).PartNo;
			String wLine = wItemList.get(0).LineName;
			// 文件名
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}-{2}-台车BOM.xls", wPartNo.replace("#", "-"), wLine, wCurTime);
			// ①获取本地文件路径
			String wDirePath = StringUtils.Format("{0}static/export/",
					Constants.getConfigPath().replace("config/", ""));
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			// ②创建本地文件
			String wFilePath = StringUtils.Format("{0}{1}", wDirePath, wFileName);
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			// ③写入本地文件
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			// 导出BOM数据
			QMSUtils.getInstance().IPT_ExportAPSBOM(wLoginUser, wFileOutputStream, wItemList);
			// ④返回文件链接
			String wUri = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);
			wResult.Result = wUri;
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTProblemAssess> IPT_QueryProblemAssessByID(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTProblemAssess> wResult = new ServiceResult<IPTProblemAssess>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = IPTProblemAssessDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			wResult.Result.IPTPreCheckProblem = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
					wResult.Result.ProblemID, wErrorCode);

			if (((IPTProblemAssess) wResult.Result).IPTPreCheckProblem != null
					&& ((IPTProblemAssess) wResult.Result).IPTPreCheckProblem.ID > 0) {
				List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1L,
						((IPTProblemAssess) wResult.Result).IPTPreCheckProblem.IPTItemID,
						((IPTProblemAssess) wResult.Result).IPTPreCheckProblem.IPTPreCheckTaskID, -1, -1, -1,
						wErrorCode);
				if (wValueList != null && wValueList.size() > 0) {
					((IPTProblemAssess) wResult.Result).IPTPreCheckProblem.PreCheckValue = wValueList.get(0);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTPreCheckProblem>> IPT_QueryPreCheckProblemByEmployee(BMSEmployee wLoginUser) {
		ServiceResult<List<IPTPreCheckProblem>> wResult = new ServiceResult<>();
		wResult.Result = new ArrayList<>();
		OutResult<Integer> wErrorCode = new OutResult<>(Integer.valueOf(0));
		try {
			// ①获取预检工位工艺师列表
			List<Integer> wTechnicianList = this.GetTechnicianList(wLoginUser);

			Calendar wStartTime = Calendar.getInstance();
			wStartTime.add(5, -7);
			wStartTime.set(10, 0);
			wStartTime.set(12, 0);
			wStartTime.set(13, 0);

			Calendar wEndTime = Calendar.getInstance();
			wEndTime.set(10, 23);
			wEndTime.set(12, 59);
			wEndTime.set(13, 59);

			List<IPTPreCheckProblem> wDoneList = new ArrayList<>();
			List<IPTPreCheckProblem> wToDoList = new ArrayList<>();

			if (wTechnicianList.stream().anyMatch(p -> p == wLoginUser.ID)) {
				wToDoList.addAll(IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, -1, -1, null, null,
						new ArrayList<>(Arrays.asList(IPTPreCheckProblemStatus.Auditing.getValue(),
								IPTPreCheckProblemStatus.Issued.getValue(),
								IPTPreCheckProblemStatus.ToCraftGiveSolve.getValue(),
								IPTPreCheckProblemStatus.ToCraftSendAudit.getValue(),
								IPTPreCheckProblemStatus.ToMutual.getValue(),
								IPTPreCheckProblemStatus.ToRelaPersonAudit.getValue(),
								IPTPreCheckProblemStatus.ToSendItem.getValue(),
								IPTPreCheckProblemStatus.ToConfirm.getValue(),
								IPTPreCheckProblemStatus.ToSpecial.getValue())),
						wErrorCode));
				wToDoList.forEach(p -> {
					if (p.Status == IPTPreCheckProblemStatus.ToCraftGiveSolve.getValue()
							|| p.Status == IPTPreCheckProblemStatus.ToCraftSendAudit.getValue()) {
						p.IsPower = true;
					}
				});
				wDoneList
						.addAll(IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, -1, -1, wStartTime, wEndTime,
								new ArrayList<>(
										Arrays.asList(Integer.valueOf(IPTPreCheckProblemStatus.Done.getValue()))),
								wErrorCode));
			}

			wToDoList.addAll(
					IPTPreCheckProblemDAO.getInstance().SelectListByConfirmID(wLoginUser, wLoginUser.ID, null, null,
							new ArrayList<>(Arrays.asList(Integer.valueOf(IPTPreCheckProblemStatus.Auditing.getValue()),
									Integer.valueOf(IPTPreCheckProblemStatus.Issued.getValue()),
									Integer.valueOf(IPTPreCheckProblemStatus.ToCraftGiveSolve.getValue()),
									Integer.valueOf(IPTPreCheckProblemStatus.ToCraftSendAudit.getValue()),
									Integer.valueOf(IPTPreCheckProblemStatus.ToMutual.getValue()),
									Integer.valueOf(IPTPreCheckProblemStatus.ToConfirm.getValue()),
									Integer.valueOf(IPTPreCheckProblemStatus.ToRelaPersonAudit.getValue()),
									Integer.valueOf(IPTPreCheckProblemStatus.ToSendItem.getValue()),
									Integer.valueOf(IPTPreCheckProblemStatus.ToSpecial.getValue()))),
							wErrorCode));
			wToDoList.forEach(p -> {
				if (p.Status == IPTPreCheckProblemStatus.ToConfirm.getValue()) {
					p.IsPower = true;
				}
			});
			wDoneList.addAll(IPTPreCheckProblemDAO.getInstance().SelectListByConfirmID(wLoginUser, wLoginUser.ID,
					wStartTime, wEndTime,
					new ArrayList<>(Arrays.asList(Integer.valueOf(IPTPreCheckProblemStatus.Done.getValue()))),
					wErrorCode));

			if (wToDoList.size() > 0) {
				((List<IPTPreCheckProblem>) wResult.Result).addAll(wToDoList);
			}
			if (wDoneList.size() > 0) {
				((List<IPTPreCheckProblem>) wResult.Result).addAll(wDoneList);
			}

			for (IPTPreCheckProblem wIPTPreCheckProblem : wResult.Result) {
				if (wIPTPreCheckProblem.IPTProblemConfirmerList != null
						&& wIPTPreCheckProblem.IPTProblemConfirmerList.size() > 0
						&& wIPTPreCheckProblem.IPTProblemConfirmerList.stream()
								.anyMatch(p -> p.ConfirmerID == wLoginUser.ID)) {
					wIPTPreCheckProblem.ConfirmStatus = wIPTPreCheckProblem.IPTProblemConfirmerList.stream()
							.filter(p -> p.ConfirmerID == wLoginUser.ID).findFirst().get().Status;
				}
			}

			wResult.Result = new ArrayList<>(((wResult.Result).stream()
					.collect(Collectors.toMap(IPTPreCheckProblem::getID, account -> account, (k1, k2) -> k2)))
							.values());
		} catch (Exception e) {
			wResult.FaultCode = String.valueOf(wResult.FaultCode) + e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取预检工位工艺师列表
	 */
	private List<Integer> GetTechnicianList(BMSEmployee wLoginUser) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			List<FPCPart> wList = QMSConstants.GetFPCPartList().values().stream()
					.filter(p -> p.Active == 1 && p.PartType == FPCPartTypes.PrevCheck.getValue())
					.collect(Collectors.toList());

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			for (FPCPart wFPCPart : wList) {
				wResult.addAll(wFPCPart.TechnicianList);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 班组长查询派工的问题项集合，作为派工的数据源
	 */
	@Override
	public ServiceResult<List<IPTPreCheckProblem>> IPT_QueryPGProblemList(BMSEmployee wLoginUser) {
		ServiceResult<List<IPTPreCheckProblem>> wResult = new ServiceResult<List<IPTPreCheckProblem>>();
		wResult.Result = new ArrayList<IPTPreCheckProblem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<Integer> wPartIDList = this.GetPartIDListByLoginUser(wLoginUser);
			if (wPartIDList == null || wPartIDList.size() <= 0) {
				wResult.Result = new ArrayList<IPTPreCheckProblem>();
				wResult.CustomResult.put("DoneList", new ArrayList<IPTPreCheckProblem>());
				return wResult;
			}

			List<IPTPreCheckProblem> wList = IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, -1, -1, null,
					null, new ArrayList<Integer>(Arrays.asList(IPTPreCheckProblemStatus.Issued.getValue())),
					wErrorCode);
			// 处理实际执行工位
			for (IPTPreCheckProblem wIPTPreCheckProblem : wList) {
				List<RSMTurnOrderTask> wTurnOrderTaskList = RSMTurnOrderTaskDAO.getInstance().SelectList(wLoginUser, -1,
						wIPTPreCheckProblem.OrderID, wIPTPreCheckProblem.DoStationID, -1,
						new ArrayList<Integer>(Arrays.asList(RSMTurnOrderTaskStatus.Passed.getValue())), null, null,
						wErrorCode);
				if (wTurnOrderTaskList != null && wTurnOrderTaskList.size() > 0) {
					List<RSMTurnOrderTask> wTempList = RSMTurnOrderTaskDAO.getInstance().SelectNewList(wLoginUser,
							wIPTPreCheckProblem.OrderID, wErrorCode);
					if (wTempList != null && wTempList.size() > 0) {
						if (wTempList.get(0).Status == RSMTurnOrderTaskStatus.Auditing.getValue()) {
							wIPTPreCheckProblem.RealStationID = wTempList.get(0).ApplyStationID;
						} else if (wTempList.get(0).Status == RSMTurnOrderTaskStatus.Passed.getValue()) {
							wIPTPreCheckProblem.RealStationID = wTempList.get(0).TargetStationID;
						} else {
							wIPTPreCheckProblem.RealStationID = wIPTPreCheckProblem.DoStationID;
						}
					}
				} else {
					wIPTPreCheckProblem.RealStationID = wIPTPreCheckProblem.DoStationID;
				}
				IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);
			}

			for (IPTPreCheckProblem wIPTPreCheckProblem : wList) {
				// ①若执行工位在工位列表中，添加
				if (wPartIDList.stream().anyMatch(p -> p == wIPTPreCheckProblem.RealStationID)) {
					wResult.Result.add(wIPTPreCheckProblem);
				}
			}
			List<IPTPreCheckProblem> wDoneList = new ArrayList<IPTPreCheckProblem>();
			List<IPTPreCheckProblem> wDone1List = IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, -1, -1,
					null, null,
					new ArrayList<Integer>(Arrays.asList(IPTPreCheckProblemStatus.Start.getValue(),
							IPTPreCheckProblemStatus.ToMutual.getValue(),
							IPTPreCheckProblemStatus.ToSpecial.getValue())),
					wErrorCode);
			for (IPTPreCheckProblem wIPTPreCheckProblem : wDone1List) {
				if (wPartIDList.stream().anyMatch(p -> p == wIPTPreCheckProblem.DoStationID)) {
					wDoneList.add(wIPTPreCheckProblem);
				}
			}
			// 前七日凌晨
			Calendar wStartTime = Calendar.getInstance();
			wStartTime.add(Calendar.DATE, -7);
			wStartTime.set(Calendar.HOUR, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);
			// 今日23点59分59秒
			Calendar wEndTime = Calendar.getInstance();
			wEndTime.set(Calendar.HOUR, 23);
			wEndTime.set(Calendar.MINUTE, 59);
			wEndTime.set(Calendar.SECOND, 59);

			List<IPTPreCheckProblem> wDone2List = IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, -1, -1,
					wStartTime, wEndTime,
					new ArrayList<Integer>(Arrays.asList(IPTPreCheckProblemStatus.Done.getValue())), wErrorCode);
			for (IPTPreCheckProblem wIPTPreCheckProblem : wDone2List) {
				if (wPartIDList.stream().anyMatch(p -> p == wIPTPreCheckProblem.RealStationID)) {
					wDoneList.add(wIPTPreCheckProblem);
				}
			}
			wResult.CustomResult.put("DoneList", wDoneList);

			if (wDoneList.size() > 0) {
				wResult.Result.addAll(wDoneList);
				wResult.Result = new ArrayList<IPTPreCheckProblem>(wResult.Result.stream()
						.collect(Collectors.toMap(IPTPreCheckProblem::getID, account -> account, (k1, k2) -> k2))
						.values());
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取登录者所管辖的工位列表(班组长)
	 * 
	 * @param wLoginUser
	 * @return
	 */
	private List<Integer> GetPartIDListByLoginUser(BMSEmployee wLoginUser) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			// 判断此人是否是班组长
			if (QMSConstants.GetBMSDepartment(wLoginUser.DepartmentID).Type != BMSDepartmentType.Class.getValue()
					|| QMSConstants.GetBMSPosition(wLoginUser.Position).DutyID != 1) {
				return wResult;
			}

			int wClassID = wLoginUser.DepartmentID;

			// 班组工位列表
			List<FMCWorkCharge> wList1 = CoreServiceImpl.getInstance().FMC_QueryWorkChargeList(wLoginUser)
					.List(FMCWorkCharge.class);
			if (wList1 == null) {
				return wResult;
			}

			wList1 = wList1.stream().filter(p -> p.ClassID == wClassID).collect(Collectors.toList());
			if (wList1 == null) {
				return wResult;
			}

			wResult = wList1.stream().map(p -> p.StationID).collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_SavePGPerson(BMSEmployee wLoginUser, int wPersonID, IPTPreCheckProblem wData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wData.DoPersonID = wPersonID;
			wData.IsDischarged = false;
			wResult.Result = IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wData, wErrorCode);
			// 删除该问题项的派工人员
			List<SFCTaskStep> wList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(wLoginUser, wData.ID, -1, -1).List(SFCTaskStep.class);
			wList = wList.stream().filter(p -> p.Type == SFCTaskStepType.Question.getValue())
					.collect(Collectors.toList());
			if (wList != null && wList.size() > 0) {
				LOCOAPSServiceImpl.getInstance().SFC_DeleteTaskStepList(wLoginUser, wList);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTStandard>> IPT_QueryStepVersionAll(BMSEmployee wLoginUser, int wLineID, int wProductID,
			int wPartID, int wCustomID) {
		ServiceResult<List<IPTStandard>> wResult = new ServiceResult<List<IPTStandard>>();
		wResult.Result = new ArrayList<IPTStandard>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①根据修程、工位查询工序列表
			List<Integer> wStepIDList = QMSUtils.getInstance().FMC_QueryStepIDList(wLoginUser, wLineID, wPartID,
					wProductID);
			if (wStepIDList == null || wStepIDList.size() <= 0) {
				return wResult;
			}
			// ②根据修程、车型、工位查询工序版本列表
			List<IPTStandard> wStandardList = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wLoginUser.CompanyID, IPTMode.Default, wCustomID, -1, -1, -1, -1, wLineID, wPartID, -1, -1,
					wProductID, "", 1000, null, null, wErrorCode).Result;
			if (wStandardList == null) {
				wStandardList = new ArrayList<IPTStandard>();
			}
			// ③遍历工序列表，依次添加或构造假版本
			IPTStandard wIPTStandard;
			for (Integer wStepID : wStepIDList) {
				if (wStandardList.stream().anyMatch(p -> p.PartPointID == wStepID)) {
					wResult.Result.addAll(
							wStandardList.stream().filter(p -> p.PartPointID == wStepID).collect(Collectors.toList()));
				} else {
					wIPTStandard = new IPTStandard();
					wIPTStandard.LineID = wLineID;
					wIPTStandard.PartID = wPartID;
					wIPTStandard.PartPointID = wStepID;
					wIPTStandard.ProductID = wProductID;
					wIPTStandard.CustomID = wCustomID;
					wResult.Result.add(wIPTStandard);
				}
			}
			// 翻译
			if (wResult.Result != null && wResult.Result.size() > 0) {
				for (IPTStandard wItem : wResult.Result) {
					wItem.LineName = QMSConstants.GetFMCLineName(wItem.LineID);
					wItem.PartName = QMSConstants.GetFPCPartName(wItem.PartID);
					wItem.PartPointName = QMSConstants.GetFPCStepName(wItem.PartPointID);
					wItem.ProductNo = QMSConstants.GetFPCProductNo(wItem.ProductID);
					wItem.CustomName = QMSConstants.GetCRMCustomerName(wItem.CustomID);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTPreCheckReport> IPT_QueryPreCheckReportByID(BMSEmployee wLoginUser, int wReportID) {
		ServiceResult<IPTPreCheckReport> wResult = new ServiceResult<IPTPreCheckReport>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		wResult.Result = new IPTPreCheckReport();
		try {
			IPTPreCheckReport wReport = IPTPreCheckReportDAO.getInstance().SelectByID(wLoginUser, wReportID,
					wErrorCode);
			if (wReport == null || wReport.ID <= 0) {
				wResult.CustomResult.put("OperateList", new ArrayList<Integer>());
				return wResult;
			}

			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wReport.OrderID)
					.Info(OMSOrder.class);
			if (wOrder != null && wOrder.ID > 0) {
				wReport.LineName = wOrder.LineName;
			}

			wResult.Result = wReport;
			BFCAuditConfig wCurrentConfig = CoreServiceImpl.getInstance()
					.BFC_CurrentConfig(wLoginUser, BPMEventModule.YJReport.getValue(), wReport.ID, wLoginUser.getID())
					.Info(BFCAuditConfig.class);
			if (wCurrentConfig != null && wCurrentConfig.AuditActions != null && wCurrentConfig.AuditActions.size() > 0
					&& CoreServiceImpl.getInstance().BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID,
							wCurrentConfig.FunctionID, 0, 0).Info(Boolean.class)) {
				wResult.CustomResult.put("OperateList", wCurrentConfig.AuditActions);
			} else {
				wResult.CustomResult.put("OperateList", new ArrayList<Integer>());
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 设置是否为质量项点
	 */
	@Override
	public ServiceResult<Integer> IPT_SetIsQuality(BMSEmployee wLoginUser, List<IPTItem> wIPTItemList, int wIsQuality) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wIPTItemList == null || wIPTItemList.size() <= 0) {
				return wResult;
			}

			for (IPTItem wIPTItem : wIPTItemList) {
				wIPTItem.IsQuality = wIsQuality;
				IPTItemDAO.getInstance().Update(wLoginUser, wIPTItem.VID, wIPTItem, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_SetPartCoding(BMSEmployee wLoginUser, IPTItem wIPTItem, String wPartCoding,
			int wConfigID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wIPTItem == null || wIPTItem.ID <= 0) {
				return wResult;
			}

			wIPTItem.PartsCoding = wPartCoding;
			wIPTItem.ConfigID = wConfigID;
			IPTItemDAO.getInstance().Update(wLoginUser, wIPTItem.VID, wIPTItem, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_SetItemOrder(BMSEmployee wLoginUser, IPTItem wIPTItem, int wOrderID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wIPTItem == null || wIPTItem.ID <= 0) {
				return wResult;
			}

			wIPTItem.OrderID = wOrderID;
			IPTItemDAO.getInstance().Update(wLoginUser, wIPTItem.VID, wIPTItem, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTPreCheckReport>> IPT_QueryPreCheckReportByOrder(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<IPTPreCheckReport>> wResult = new ServiceResult<List<IPTPreCheckReport>>();
		wResult.Result = new ArrayList<IPTPreCheckReport>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = IPTPreCheckReportDAO.getInstance().SelectList(wLoginUser, -1, "", wOrderID, -1, null, null,
					null, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<IPTOrderReport> IPT_QueryOrderInfo(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<IPTOrderReport> wResult = new ServiceResult<>();
		wResult.Result = new IPTOrderReport();
		OutResult<Integer> wErrorCode = new OutResult<>(Integer.valueOf(0));
		try {
			List<IPTOrderReport> wReportList = IPTOrderReportDAO.getInstance().SelectList(wLoginUser, -1, wOrderID,
					wErrorCode);

			List<APSTaskStep> wAllTaskStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, wOrderID, -1, -1, null).List(APSTaskStep.class);
			if (wReportList == null || wReportList.size() <= 0) {
				OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);
				if (wOrder == null || wOrder.ID <= 0) {
					wResult.FaultCode = String.valueOf(wResult.FaultCode) + "提示：参数错误，订单数据丢失!";
					return wResult;
				}
				if (wOrder.RouteID <= 0) {
					wResult.FaultCode = String.valueOf(wResult.FaultCode) + "提示：该订单未设置工艺路线!";
					return wResult;
				}

				List<FPCRoutePart> wRoutePartList = (List<FPCRoutePart>) QMSConstants.GetFPCRoutePartList().stream()
						.filter(p -> (p.RouteID == wOrder.RouteID)).collect(Collectors.toList());

				List<FPCRoutePartPoint> wRoutePartPointList = (List<FPCRoutePartPoint>) QMSConstants
						.GetFPCRoutePartPointList().stream().filter(p -> (p.RouteID == wOrder.RouteID))
						.collect(Collectors.toList());

				List<FPCPart> wPrevPartList = FMCServiceImpl.getInstance()
						.FPC_QueryPartList(wLoginUser, -1, -1, -1, FPCPartTypes.PrevCheck.getValue())
						.List(FPCPart.class);

				List<FPCPart> wFinalPartList = FMCServiceImpl.getInstance()
						.FPC_QueryPartList(wLoginUser, -1, -1, -1, FPCPartTypes.QTFinally.getValue())
						.List(FPCPart.class);

				List<FPCPart> wOutPartList = FMCServiceImpl.getInstance()
						.FPC_QueryPartList(wLoginUser, -1, -1, -1, FPCPartTypes.OutFactory.getValue())
						.List(FPCPart.class);

				IPTOrderReport wIPTOrderReport = new IPTOrderReport(0, wOrder.ID, wOrder.OrderNo, wOrder.PartNo,
						wOrder.CustomerID, wOrder.LineID, wLoginUser.ID, Calendar.getInstance(), wLoginUser.ID,
						Calendar.getInstance());
				wIPTOrderReport.ID = IPTOrderReportDAO.getInstance().Update(wLoginUser, wIPTOrderReport, wErrorCode);

				for (FPCPart wFPCPart : wPrevPartList) {
					IPTOrderReportPart wIPTOrderReportPart = new IPTOrderReportPart(0, wIPTOrderReport.ID, wFPCPart.ID,
							IPTOrderReportPartType.PreCheck.getValue());
					wIPTOrderReportPart.ID = IPTOrderReportPartDAO.getInstance().Update(wLoginUser, wIPTOrderReportPart,
							wErrorCode);
					wIPTOrderReport.IPTOrderReportPartList.add(wIPTOrderReportPart);

					List<FPCRoutePartPoint> wRouteStepList = (List<FPCRoutePartPoint>) wRoutePartPointList.stream()
							.filter(p -> (p.PartID == wFPCPart.ID)).collect(Collectors.toList());
					for (FPCRoutePartPoint wFPCRoutePartPoint : wRouteStepList) {
						int wStatus = 0;
						Optional<APSTaskStep> wOption = wAllTaskStepList.stream()
								.filter(p -> (p.PartID == wFPCPart.ID && p.StepID == wFPCRoutePartPoint.PartPointID))
								.findFirst();
						if (wOption.isPresent()) {
							wStatus = ((APSTaskStep) wOption.get()).Status;
						}
						IPTOrderReportPartPoint wIPTOrderReportPartPoint = new IPTOrderReportPartPoint(0,
								wIPTOrderReportPart.ID, wFPCRoutePartPoint.PartPointID, wStatus);
						wIPTOrderReportPartPoint.ID = IPTOrderReportPartPointDAO.getInstance().Update(wLoginUser,
								wIPTOrderReportPartPoint, wErrorCode);
						wIPTOrderReportPart.IPTOrderReportPartPointList.add(wIPTOrderReportPartPoint);
					}
				}

				for (FPCPart wFPCPart : wFinalPartList) {
					IPTOrderReportPart wIPTOrderReportPart = new IPTOrderReportPart(0, wIPTOrderReport.ID, wFPCPart.ID,
							IPTOrderReportPartType.Quality.getValue());
					wIPTOrderReportPart.ID = IPTOrderReportPartDAO.getInstance().Update(wLoginUser, wIPTOrderReportPart,
							wErrorCode);
					wIPTOrderReport.IPTOrderReportPartList.add(wIPTOrderReportPart);

					List<Integer> wStepIDList = QMSUtils.getInstance().FMC_QueryStepIDList(wLoginUser, wOrder.LineID,
							wFPCPart.ID, wOrder.ProductID);
					for (Integer wStepID : wStepIDList) {
						int wStatus = 0;
						Optional<APSTaskStep> wOption = wAllTaskStepList.stream()
								.filter(p -> (p.PartID == wFPCPart.ID && p.StepID == wStepID.intValue())).findFirst();
						if (wOption.isPresent()) {
							wStatus = ((APSTaskStep) wOption.get()).Status;
						}
						IPTOrderReportPartPoint wIPTOrderReportPartPoint = new IPTOrderReportPartPoint(0,
								wIPTOrderReportPart.ID, wStepID.intValue(), wStatus);
						wIPTOrderReportPartPoint.ID = IPTOrderReportPartPointDAO.getInstance().Update(wLoginUser,
								wIPTOrderReportPartPoint, wErrorCode);
						wIPTOrderReportPart.IPTOrderReportPartPointList.add(wIPTOrderReportPartPoint);
					}
				}

				for (FPCPart wFPCPart : wOutPartList) {
					IPTOrderReportPart wIPTOrderReportPart = new IPTOrderReportPart(0, wIPTOrderReport.ID, wFPCPart.ID,
							IPTOrderReportPartType.Quality.getValue());
					wIPTOrderReportPart.ID = IPTOrderReportPartDAO.getInstance().Update(wLoginUser, wIPTOrderReportPart,
							wErrorCode);
					wIPTOrderReport.IPTOrderReportPartList.add(wIPTOrderReportPart);

					List<Integer> wStepIDList = QMSUtils.getInstance().FMC_QueryStepIDList(wLoginUser, wOrder.LineID,
							wFPCPart.ID, wOrder.ProductID);
					for (Integer wStepID : wStepIDList) {
						int wStatus = 0;
						Optional<APSTaskStep> wOption = wAllTaskStepList.stream()
								.filter(p -> (p.PartID == wFPCPart.ID && p.StepID == wStepID.intValue())).findFirst();
						if (wOption.isPresent()) {
							wStatus = ((APSTaskStep) wOption.get()).Status;
						}
						IPTOrderReportPartPoint wIPTOrderReportPartPoint = new IPTOrderReportPartPoint(0,
								wIPTOrderReportPart.ID, wStepID.intValue(), wStatus);
						wIPTOrderReportPartPoint.ID = IPTOrderReportPartPointDAO.getInstance().Update(wLoginUser,
								wIPTOrderReportPartPoint, wErrorCode);
						wIPTOrderReportPart.IPTOrderReportPartPointList.add(wIPTOrderReportPartPoint);
					}
				}

				wRoutePartList.removeIf(p -> wPrevPartList.stream().anyMatch(q -> q.ID == p.PartID));
				for (FPCRoutePart wFPCRoutePart : wRoutePartList) {
					IPTOrderReportPart wIPTOrderReportPart = new IPTOrderReportPart(0, wIPTOrderReport.ID,
							wFPCRoutePart.PartID, IPTOrderReportPartType.Process.getValue());
					wIPTOrderReportPart.ID = IPTOrderReportPartDAO.getInstance().Update(wLoginUser, wIPTOrderReportPart,
							wErrorCode);
					wIPTOrderReport.IPTOrderReportPartList.add(wIPTOrderReportPart);

					List<FPCRoutePartPoint> wRouteStepList = (List<FPCRoutePartPoint>) wRoutePartPointList.stream()
							.filter(p -> (p.PartID == wFPCRoutePart.PartID)).collect(Collectors.toList());
					for (FPCRoutePartPoint wFPCRoutePartPoint : wRouteStepList) {
						int wStatus = 0;
						Optional<APSTaskStep> wOption = wAllTaskStepList.stream().filter(
								p -> (p.PartID == wFPCRoutePart.PartID && p.StepID == wFPCRoutePartPoint.PartPointID))
								.findFirst();
						if (wOption.isPresent()) {
							wStatus = ((APSTaskStep) wOption.get()).Status;
						}
						IPTOrderReportPartPoint wIPTOrderReportPartPoint = new IPTOrderReportPartPoint(0,
								wIPTOrderReportPart.ID, wFPCRoutePartPoint.PartPointID, wStatus);
						wIPTOrderReportPartPoint.ID = IPTOrderReportPartPointDAO.getInstance().Update(wLoginUser,
								wIPTOrderReportPartPoint, wErrorCode);
						wIPTOrderReportPart.IPTOrderReportPartPointList.add(wIPTOrderReportPartPoint);
					}
				}

				wResult.Result = wIPTOrderReport;
			} else {
				IPTOrderReport wIPTOrderReport = wReportList.get(0);
				for (IPTOrderReportPart wIPTOrderReportPart : wIPTOrderReport.IPTOrderReportPartList) {
					for (IPTOrderReportPartPoint wIPTOrderReportPartPoint : wIPTOrderReportPart.IPTOrderReportPartPointList) {
						int wStatus = 0;
						Optional<APSTaskStep> wOption = wAllTaskStepList.stream()
								.filter(p -> (p.PartID == wIPTOrderReportPart.PartID
										&& p.StepID == wIPTOrderReportPartPoint.StepID))
								.findFirst();
						if (wOption.isPresent()) {
							wStatus = ((APSTaskStep) wOption.get()).Status;
						}
						if (wIPTOrderReportPartPoint.Status != wStatus) {
							IPTOrderReportPartPointDAO.getInstance().Update(wLoginUser, wIPTOrderReportPartPoint,
									wErrorCode);
							wIPTOrderReport.EditID = wLoginUser.ID;
							wIPTOrderReport.EditTime = Calendar.getInstance();
							IPTOrderReportDAO.getInstance().Update(wLoginUser, wIPTOrderReport, wErrorCode);
						}
					}
				}

				wResult.Result = wIPTOrderReport;
			}

			if (wResult.Result != null && ((IPTOrderReport) wResult.Result).ID > 0) {
				((IPTOrderReport) wResult.Result).IPTOrderReportPartList.forEach(p -> {
					p.OrderID = ((IPTOrderReport) wResult.Result).OrderID;
					p.OrderNo = ((IPTOrderReport) wResult.Result).OrderNo;
					p.PartNo = ((IPTOrderReport) wResult.Result).PartNo;
					p.CustomerID = ((IPTOrderReport) wResult.Result).CustomerID;
					p.CustomerName = ((IPTOrderReport) wResult.Result).CustomerName;
					p.LineID = ((IPTOrderReport) wResult.Result).LineID;
					p.LineName = ((IPTOrderReport) wResult.Result).LineName;
				});
				for (IPTOrderReportPart wIPTOrderReportPart : ((IPTOrderReport) wResult.Result).IPTOrderReportPartList) {
					wIPTOrderReportPart.IPTOrderReportPartPointList.forEach(p -> {
						p.PartID = wIPTOrderReportPart.PartID;
						p.PartName = wIPTOrderReportPart.PartName;
						p.Type = wIPTOrderReportPart.Type;
						p.OrderID = wIPTOrderReportPart.OrderID;
						p.OrderNo = wIPTOrderReportPart.OrderNo;
						p.PartNo = wIPTOrderReportPart.PartNo;
						p.CustomerID = wIPTOrderReportPart.CustomerID;
						p.CustomerName = wIPTOrderReportPart.CustomerName;
						p.LineID = wIPTOrderReportPart.LineID;
						p.LineName = wIPTOrderReportPart.LineName;
					});
				}
			}

			wResult.setFaultCode(MESException.getEnumType(((Integer) wErrorCode.Result).intValue()).getLable());
		} catch (Exception e) {
			wResult.FaultCode = String.valueOf(wResult.FaultCode) + e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<List<SFCTaskIPT>> IPT_QueryIPTListByReportPartPointID(BMSEmployee wLoginUser, int wID) {
		final ServiceResult<List<SFCTaskIPT>> wResult = new ServiceResult<>();
		wResult.Result = new ArrayList<>();
		final OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			IPTOrderReportPartPoint wOrderPartPoint = IPTOrderReportPartPointDAO.getInstance().SelectByID(wLoginUser,
					wID, wErrorCode);
			if (wOrderPartPoint == null || wOrderPartPoint.ID <= 0) {
				return wResult;
			}
			IPTOrderReportPart wOrderPart = IPTOrderReportPartDAO.getInstance().SelectByID(wLoginUser,
					wOrderPartPoint.ReportPartID, wErrorCode);
			if (wOrderPart == null || wOrderPart.ID <= 0) {
				return wResult;
			}
			IPTOrderReport wOrderReport = IPTOrderReportDAO.getInstance().SelectByID(wLoginUser, wOrderPart.ReportID,
					wErrorCode);
			if (wOrderReport == null || wOrderReport.ID <= 0) {
				return wResult;
			}
			wResult.Result = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderReport.OrderID,
					wOrderPart.PartID, -1, wOrderReport.LineID, wOrderPartPoint.StepID, -1, "", null, null, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			final ServiceResult<List<SFCTaskIPT>> serviceResult = wResult;
			serviceResult.FaultCode = String.valueOf(serviceResult.FaultCode) + e.toString();
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<List<IPTItem>> IPT_QueryPeriodChangeAll(final BMSEmployee wLoginUser, final int wOrderID) {
		final ServiceResult<List<IPTItem>> wResult = new ServiceResult<>();
		wResult.Result = new ArrayList<>();
		final OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			final List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
					wOrderID, -1, SFCTaskType.PreCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			final List<IPTValue> wValueList = new ArrayList<IPTValue>();
			for (final SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				List<IPTItem> wList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1L,
						wSFCTaskIPT.PeriodChangeStandard, -1, -1, wErrorCode);
				if (wList != null) {
					if (wList.size() <= 0) {
						continue;
					}
//					wList = wList.stream().filter(p -> p.IsPeriodChange == wSFCTaskIPT.CustomerID)
//							.collect(Collectors.toList());
//					if (wList == null) {
//						continue;
//					}
//					if (wList.size() <= 0) {
//						continue;
//					}
					(wResult.Result).addAll(wList);
					List<IPTValue> wTempValueList = (List<IPTValue>) IPTStandardDAO.getInstance()
							.SelectValue(wLoginUser, wSFCTaskIPT.ID, -1, -1, wErrorCode).Result;
					if (wTempValueList == null) {
						continue;
					}
					if (wTempValueList.size() <= 0) {
						continue;
					}
					final List<IPTItem> wTempList = wList;
					wTempValueList = wTempValueList.stream()
							.filter(p -> wTempList.stream().anyMatch(q -> q.ID == p.IPTItemID))
							.collect(Collectors.toList());
					if (wTempValueList == null) {
						continue;
					}
					if (wTempValueList.size() <= 0) {
						continue;
					}
					wValueList.addAll(wTempValueList);
				}
			}
			wResult.CustomResult.put("ValueList", wValueList);
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			final ServiceResult<List<IPTItem>> serviceResult = wResult;
			serviceResult.FaultCode = String.valueOf(serviceResult.FaultCode) + e.toString();
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<List<APSTaskStep>> IPT_QueryRecordAll(final BMSEmployee wLoginUser, final Calendar wStartTime,
			final Calendar wEndTime, final int wOrderID, final int wStationID, final int wStepID, final int wSubmitID) {
		final ServiceResult<List<APSTaskStep>> wResult = new ServiceResult<>();
		wResult.Result = new ArrayList<>();
		final OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID,
					wStationID, -1, -1, wStepID, -1, "", wStartTime, wEndTime, wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			if (wSubmitID > 0) {
				wSFCTaskIPTList = wSFCTaskIPTList.stream().filter(p -> p.OperatorList != null
						&& p.OperatorList.size() > 0 && p.OperatorList.stream().anyMatch(q -> q == wSubmitID))
						.collect(Collectors.toList());
			}
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			for (final SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				final APSTaskStep wAPSTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wAPSTaskStep != null) {
					if (wAPSTaskStep.ID <= 0) {
						continue;
					}
					(wResult.Result).add(wAPSTaskStep);
				}
			}
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			final ServiceResult<List<APSTaskStep>> serviceResult = wResult;
			serviceResult.FaultCode = String.valueOf(serviceResult.FaultCode) + e.toString();
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<List<IPTCheckRecord>> IPT_QueryPreCheckRecord(final BMSEmployee wLoginUser, final int wOrderID,
			final Calendar wStartTime, final Calendar wEndTime, final int wStepID, final int wRecordType) {
		final ServiceResult<List<IPTCheckRecord>> wResult = new ServiceResult<>();
		wResult.Result = new ArrayList<>();
		final OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			switch (IPTPreCheckRecordType.getEnumType(wRecordType)) {
			case PeriodChangeItem: {
				wResult.Result = this.IPT_QueryPeriodChangeItem(wLoginUser, wOrderID, wStartTime, wEndTime, wStepID);
				break;
			}
			case ExceptionAll: {
				wResult.Result = this.IPT_QueryExceptionAll(wLoginUser, wOrderID, wStartTime, wEndTime, wStepID);
				break;
			}
			case KeyComponents: {
				wResult.Result = this.IPT_QueryKeyComponents(wLoginUser, wOrderID, wStartTime, wEndTime, wStepID);
				break;
			}
			case ControlRecord: {
				wResult.Result = this.IPT_QueryControlRecord(wLoginUser, wOrderID, wStartTime, wEndTime, wStepID);
				break;
			}
			default:
				break;
			}
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			final ServiceResult<List<IPTCheckRecord>> serviceResult = wResult;
			serviceResult.FaultCode = String.valueOf(serviceResult.FaultCode) + e.toString();
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	private List<IPTCheckRecord> IPT_QueryExceptionAll(final BMSEmployee wLoginUser, final int wOrderID,
			final Calendar wStartTime, final Calendar wEndTime, final int wStepID) {
		final List<IPTCheckRecord> wResult = new ArrayList<IPTCheckRecord>();
		final OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			final List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
					wOrderID, -1, SFCTaskType.PreCheck.getValue(), -1, wStepID, -1, "", wStartTime, wEndTime,
					wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			final Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance()
					.SelectItem(wLoginUser, wSFCTaskIPTList.stream().map(p -> (long) p.ModuleVersionID).distinct()
							.collect(Collectors.toList()), wErrorCode).Result;
			final List<IPTPreCheckProblem> wAllProblemList = IPTPreCheckProblemDAO.getInstance()
					.SelectListByTaskIPTIDList(wLoginUser,
							wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);
			final List<OMSOrder> wAllOrderList = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser,
							wSFCTaskIPTList.stream().map(p -> p.OrderID).distinct().collect(Collectors.toList()))
					.List(OMSOrder.class);
			OMSOrder wOrder = null;
			List<IPTItem> wItemList = null;
			List<IPTPreCheckProblem> wProblemList = null;
			for (final SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				if (wAllOrderList != null && wAllOrderList.size() > 0
						&& wAllOrderList.stream().anyMatch(p -> p.ID == wSFCTaskIPT.OrderID)) {
					wOrder = wAllOrderList.stream().filter(p -> p.ID == wSFCTaskIPT.OrderID).findFirst().get();
				}
				if (wOrder == null || wOrder.ID <= 0) {
					return wResult;
				}
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);
				}
				if (wItemList == null) {
					continue;
				}
				if (wItemList.size() <= 0) {
					continue;
				}
				for (final IPTItem wIPTItem : wItemList) {
					if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
						continue;
					}
					final List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
					wIPTItem.Text = this.GetDescription(wFatherText, wIPTItem);
				}
				if (wAllProblemList != null && wAllProblemList.size() > 0
						&& wAllProblemList.stream().anyMatch(p -> p.IPTPreCheckTaskID == wSFCTaskIPT.ID)) {
					wProblemList = wAllProblemList.stream().filter(p -> p.IPTPreCheckTaskID == wSFCTaskIPT.ID)
							.collect(Collectors.toList());
				} else {
					wProblemList = new ArrayList<IPTPreCheckProblem>();
				}
				if (wProblemList == null) {
					continue;
				}
				if (wProblemList.size() <= 0) {
					continue;
				}
				wProblemList.removeIf(p -> p.IPTItem.IsPeriodChange > 0);
				if (wProblemList.size() <= 0) {
					continue;
				}
				for (final IPTPreCheckProblem wIPTPreCheckProblem : wProblemList) {
					if (wItemList.stream().anyMatch(p -> p.ID == wIPTPreCheckProblem.IPTItemID)) {
						wIPTPreCheckProblem.IPTItem = wItemList.stream()
								.filter(p -> p.ID == wIPTPreCheckProblem.IPTItemID).findFirst().get();
						wIPTPreCheckProblem.IPTItemName = wIPTPreCheckProblem.IPTItem.Text;
					}
				}
				final List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1L, -1L,
						wSFCTaskIPT.ID, -1, -1, -1, wErrorCode);
				for (final IPTPreCheckProblem wIPTPreCheckProblem2 : wProblemList) {
					final IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
					wIPTCheckRecord.OrderNo = wOrder.OrderNo;
					wIPTCheckRecord.WBSNo = wOrder.WBSNo;
					wIPTCheckRecord.ProductNo = wOrder.ProductNo;
					wIPTCheckRecord.PartNo = wOrder.PartNo;
					wIPTCheckRecord.LineName = wOrder.LineName;
					wIPTCheckRecord.CustomerName = wOrder.Customer;
					wIPTCheckRecord.ItemName = wIPTPreCheckProblem2.IPTItemName;
					wIPTCheckRecord.Standard = wIPTPreCheckProblem2.IPTItem.Standard;
					if (wIPTPreCheckProblem2.IPTSOPList != null && wIPTPreCheckProblem2.IPTSOPList.size() > 0) {
						wIPTCheckRecord.Opinion = wIPTPreCheckProblem2.IPTSOPList.get(0).Detail;
					}
					wIPTCheckRecord.Operator = QMSConstants.GetBMSEmployeeName(wIPTPreCheckProblem2.CarftID);
					if (wIPTPreCheckProblem2.IPTProblemAssessList != null
							&& wIPTPreCheckProblem2.IPTProblemAssessList.size() > 0) {
						wIPTCheckRecord.RelaClassMembers = StringUtils.Join(",",
								wIPTPreCheckProblem2.IPTProblemAssessList.stream().map(p -> p.Auditor)
										.collect(Collectors.toList()));
						final List<String> wClassNames = new ArrayList<String>();
						for (final IPTProblemAssess wIPTProblemAssess : wIPTPreCheckProblem2.IPTProblemAssessList) {
							final Optional<BMSDepartment> wOption = QMSConstants.GetBMSDepartmentList().values()
									.stream()
									.filter(p -> QMSConstants
											.GetBMSEmployee(wIPTProblemAssess.AuditID).DepartmentID == p.ID)
									.findFirst();
							if (wOption.isPresent() && StringUtils.isNotEmpty(wOption.get().Name)) {
								wClassNames.add(wOption.get().Name);
							}
						}
						if (wClassNames.size() > 0) {
							wIPTCheckRecord.RelaDepartments = StringUtils.Join(",", wClassNames);
						}
					}
					wIPTCheckRecord.StepName = wSFCTaskIPT.PartPointName;
					if (wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTPreCheckProblem2.IPTItemID)) {
						wIPTCheckRecord.ResultDescribe = wValueList.stream()
								.filter(p -> p.IPTItemID == wIPTPreCheckProblem2.IPTItemID).findFirst().get().Remark;
					}
					if (wIPTPreCheckProblem2.IPTProblemBomItemList != null
							&& wIPTPreCheckProblem2.IPTProblemBomItemList.size() > 0) {
						final List<String> wInfo = new ArrayList<String>();
						for (final IPTProblemBomItem wIPTProblemBomItem : wIPTPreCheckProblem2.IPTProblemBomItemList) {
							final String wStr = StringUtils.Format("【{0}:{1}】",
									new Object[] { wIPTProblemBomItem.MaterialName, wIPTProblemBomItem.Number });
							wInfo.add(wStr);
						}
						wIPTCheckRecord.MaterialInfo = StringUtils.Join(",", wInfo);
					}
					if (wIPTPreCheckProblem2.IPTSOPList != null && wIPTPreCheckProblem2.IPTSOPList.size() > 0
							&& wIPTPreCheckProblem2.IPTSOPList.get(0).PathList != null
							&& wIPTPreCheckProblem2.IPTSOPList.get(0).PathList.size() > 0) {
						wIPTCheckRecord.Picture = StringUtils.Join(",",
								wIPTPreCheckProblem2.IPTSOPList.get(0).PathList);
					}
					wResult.add(wIPTCheckRecord);
				}
			}
		} catch (Exception e) {
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	private List<IPTCheckRecord> IPT_QueryKeyComponents(final BMSEmployee wLoginUser, final int wOrderID,
			final Calendar wStartTime, final Calendar wEndTime, final int wStepID) {
		final List<IPTCheckRecord> wResult = new ArrayList<IPTCheckRecord>();
		final OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			final List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
					wOrderID, -1, SFCTaskType.PreCheck.getValue(), -1, wStepID, -1, "", wStartTime, wEndTime,
					wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			final Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance()
					.SelectItem(wLoginUser, wSFCTaskIPTList.stream().map(p -> (long) p.ModuleVersionID).distinct()
							.collect(Collectors.toList()), wErrorCode).Result;
			final List<IPTValue> wAllValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					(List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);
			final List<OMSOrder> wAllOrderList = (List<OMSOrder>) LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser,
							wSFCTaskIPTList.stream().map(p -> p.OrderID).distinct().collect(Collectors.toList()))
					.List(OMSOrder.class);
			OMSOrder wOrder = null;
			List<IPTItem> wItemList = null;
			List<IPTValue> wValueList = null;
			for (final SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				if (wAllOrderList != null && wAllOrderList.size() > 0
						&& wAllOrderList.stream().anyMatch(p -> p.ID == wSFCTaskIPT.OrderID)) {
					wOrder = wAllOrderList.stream().filter(p -> p.ID == wSFCTaskIPT.OrderID).findFirst().get();
				}
				if (wOrder == null || wOrder.ID <= 0) {
					return wResult;
				}
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);
				}
				if (wItemList == null) {
					continue;
				}
				if (wItemList.size() <= 0) {
					continue;
				}
				for (final IPTItem wIPTItem : wItemList) {
					if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
						continue;
					}
					final List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
					wIPTItem.Text = this.GetDescription(wFatherText, wIPTItem);
				}
//				wItemList = wItemList.stream().filter(p -> StringUtils.isNotEmpty(p.PartsCoding))
//						.collect(Collectors.toList());
				wItemList = wItemList.stream().filter(p -> (p.ManufactorOption != null && p.ManufactorOption.size() > 0)
						|| (p.ModalOption != null && p.ModalOption.size() > 0)).collect(Collectors.toList());
				if (wItemList == null) {
					continue;
				}
				if (wItemList.size() <= 0) {
					continue;
				}
				if (wAllValueList != null && wAllValueList.size() > 0
						&& wAllValueList.stream().anyMatch(p -> p.TaskID == wSFCTaskIPT.ID)) {
					wValueList = wAllValueList.stream().filter(p -> p.TaskID == wSFCTaskIPT.ID)
							.collect(Collectors.toList());
				} else {
					wValueList = new ArrayList<IPTValue>();
				}
				for (final IPTItem wIPTItem : wItemList) {
					final IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
					wIPTCheckRecord.OrderNo = wOrder.OrderNo;
					wIPTCheckRecord.WBSNo = wOrder.WBSNo;
					wIPTCheckRecord.ProductNo = wOrder.ProductNo;
					wIPTCheckRecord.PartNo = wOrder.PartNo;
					wIPTCheckRecord.LineName = wOrder.LineName;
					wIPTCheckRecord.CustomerName = wOrder.Customer;
					wIPTCheckRecord.ItemName = wIPTItem.PartsCoding;
					wIPTCheckRecord.Standard = wIPTItem.Standard;
					if (wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						final IPTValue wValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst()
								.get();
						wIPTCheckRecord.PartsFactory = wValue.Manufactor;
						wIPTCheckRecord.PartsModal = wValue.Modal;
						wIPTCheckRecord.PartsNumber = wValue.Number;
						wIPTCheckRecord.CheckResult = ((wValue.Result == 1) ? "合格" : "不合格");
						wIPTCheckRecord.ResultDescribe = wValue.Remark;
						wIPTCheckRecord.Picture = StringUtils.Join(",", wValue.ImagePath);
						if (wValue.IPTProblemBomItemList != null && wValue.IPTProblemBomItemList.size() > 0) {
							final List<String> wInfo = new ArrayList<String>();
							for (final IPTProblemBomItem wIPTProblemBomItem : wValue.IPTProblemBomItemList) {
								final String wStr = StringUtils.Format("【{0}:{1}】",
										new Object[] { wIPTProblemBomItem.MaterialName, wIPTProblemBomItem.Number });
								wInfo.add(wStr);
							}
							wIPTCheckRecord.MaterialInfo = StringUtils.Join(",", wInfo);
						}
					}
					wResult.add(wIPTCheckRecord);
				}
			}
		} catch (Exception e) {
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	private List<IPTCheckRecord> IPT_QueryControlRecord(BMSEmployee wLoginUser, int wOrderID, Calendar wStartTime,
			Calendar wEndTime, int wStepID) {
		List<IPTCheckRecord> wResult = new ArrayList<IPTCheckRecord>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID, -1,
					SFCTaskType.PreCheck.getValue(), -1, wStepID, -1, "", wStartTime, wEndTime, wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance().SelectItem(
					wLoginUser,
					wSFCTaskIPTList.stream().map(p -> (long) p.ModuleVersionID).distinct().collect(Collectors.toList()),
					wErrorCode).Result;
			List<IPTValue> wAllValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					(List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);
			List<OMSOrder> wAllOrderList = (List<OMSOrder>) LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser,
							wSFCTaskIPTList.stream().map(p -> p.OrderID).distinct().collect(Collectors.toList()))
					.List(OMSOrder.class);
			OMSOrder wOrder = null;
			List<IPTItem> wItemList = null;
			List<IPTValue> wValueList = null;
			for (SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				if (wAllOrderList != null && wAllOrderList.size() > 0
						&& wAllOrderList.stream().anyMatch(p -> p.ID == wSFCTaskIPT.OrderID)) {
					wOrder = wAllOrderList.stream().filter(p -> p.ID == wSFCTaskIPT.OrderID).findFirst().get();
				}
				if (wOrder == null || wOrder.ID <= 0) {
					return wResult;
				}
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);
				}
				if (wItemList == null) {
					continue;
				}
				if (wItemList.size() <= 0) {
					continue;
				}
				for (final IPTItem wIPTItem : wItemList) {
					if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
						continue;
					}
					final List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
					wIPTItem.Text = this.GetDescription(wFatherText, wIPTItem);
				}
				if (wAllValueList != null && wAllValueList.size() > 0
						&& wAllValueList.stream().anyMatch(p -> p.TaskID == wSFCTaskIPT.ID)) {
					wValueList = wAllValueList.stream().filter(p -> p.TaskID == wSFCTaskIPT.ID)
							.collect(Collectors.toList());
				} else {
					wValueList = new ArrayList<IPTValue>();
				}
				for (IPTItem wIPTItem : wItemList) {
					IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
					wIPTCheckRecord.OrderNo = wOrder.OrderNo;
					wIPTCheckRecord.WBSNo = wOrder.WBSNo;
					wIPTCheckRecord.ProductNo = wOrder.ProductNo;
					wIPTCheckRecord.PartNo = wOrder.PartNo;
					wIPTCheckRecord.LineName = wOrder.LineName;
					wIPTCheckRecord.CustomerName = wOrder.Customer;
					wIPTCheckRecord.StepName = wSFCTaskIPT.PartPointName;
					wIPTCheckRecord.ItemName = wIPTItem.Text;
					wIPTCheckRecord.Standard = wIPTItem.Standard;
					if (wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						IPTValue wIPTValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst()
								.get();
						wIPTCheckRecord.CheckResult = ((wIPTValue.Result == 1) ? "合格" : "不合格");
						wIPTCheckRecord.PartsFactory = wIPTValue.Manufactor;
						wIPTCheckRecord.PartsModal = wIPTValue.Modal;
						wIPTCheckRecord.PartsNumber = wIPTValue.Number;
						wIPTCheckRecord.Value = GetValue(wIPTItem, wIPTValue);
						wIPTCheckRecord.ResultDescribe = wIPTValue.Remark;
						wIPTCheckRecord.Picture = StringUtils.Join(",", wIPTValue.ImagePath);
						wIPTCheckRecord.Operator = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
						if (wIPTValue.IPTProblemBomItemList != null && wIPTValue.IPTProblemBomItemList.size() > 0) {
							List<String> wInfo = new ArrayList<String>();
							for (IPTProblemBomItem wIPTProblemBomItem : wIPTValue.IPTProblemBomItemList) {
								String wStr = StringUtils.Format("【{0}:{1}】",
										new Object[] { wIPTProblemBomItem.MaterialName, wIPTProblemBomItem.Number });
								wInfo.add(wStr);
							}
							wIPTCheckRecord.MaterialInfo = StringUtils.Join(",", wInfo);
						}
					}
					wResult.add(wIPTCheckRecord);
				}
			}
		} catch (Exception e) {
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	private List<IPTCheckRecord> IPT_QueryPeriodChangeItem(BMSEmployee wLoginUser, int wOrderID, Calendar wStartTime,
			Calendar wEndTime, int wStepID) {
		List<IPTCheckRecord> wResult = new ArrayList<IPTCheckRecord>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID, -1,
					SFCTaskType.PreCheck.getValue(), -1, wStepID, -1, "", wStartTime, wEndTime, wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance()
					.SelectItem(wLoginUser, wSFCTaskIPTList.stream().map(p -> (long) p.PeriodChangeStandard).distinct()
							.collect(Collectors.toList()), wErrorCode).Result;
			List<IPTPreCheckProblem> wAllProblemList = IPTPreCheckProblemDAO.getInstance().SelectListByTaskIPTIDList(
					wLoginUser, (List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()),
					wErrorCode);
			List<OMSOrder> wAllOrderList = (List<OMSOrder>) LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser,
							wSFCTaskIPTList.stream().map(p -> p.OrderID).distinct().collect(Collectors.toList()))
					.List(OMSOrder.class);
			OMSOrder wOrder = null;
			List<IPTItem> wItemList = null;
			List<IPTPreCheckProblem> wProblemList = null;
			for (SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				if (wAllOrderList != null && wAllOrderList.size() > 0
						&& wAllOrderList.stream().anyMatch(p -> p.ID == wSFCTaskIPT.OrderID)) {
					wOrder = wAllOrderList.stream().filter(p -> p.ID == wSFCTaskIPT.OrderID).findFirst().get();
				}
				if (wOrder == null || wOrder.ID <= 0) {
					return wResult;
				}
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.PeriodChangeStandard)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.PeriodChangeStandard);
				}
				if (wItemList == null) {
					continue;
				}
				if (wItemList.size() <= 0) {
					continue;
				}
				for (IPTItem wIPTItem : wItemList) {
					if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
						continue;
					}
					final List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
					wIPTItem.Text = this.GetDescription(wFatherText, wIPTItem);
				}
//				wItemList = wItemList.stream().filter(p -> p.IsPeriodChange == wSFCTaskIPT.CustomerID)
//						.collect(Collectors.toList());
//				if (wItemList == null) {
//					continue;
//				}
				if (wItemList.size() <= 0) {
					continue;
				}
				if (wAllProblemList != null && wAllProblemList.size() > 0
						&& wAllProblemList.stream().anyMatch(p -> p.IPTPreCheckTaskID == wSFCTaskIPT.ID)) {
					wProblemList = wAllProblemList.stream().filter(p -> p.IPTPreCheckTaskID == wSFCTaskIPT.ID)
							.collect(Collectors.toList());
				} else {
					wProblemList = new ArrayList<IPTPreCheckProblem>();
				}
				for (IPTItem wIPTItem : wItemList) {
					if (wProblemList != null && wProblemList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						IPTPreCheckProblem wIPTPreCheckProblem = wProblemList.stream()
								.filter(p -> p.IPTItemID == wIPTItem.ID).findFirst().get();
						IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
						wIPTCheckRecord.OrderNo = wOrder.OrderNo;
						wIPTCheckRecord.WBSNo = wOrder.WBSNo;
						wIPTCheckRecord.ProductNo = wOrder.ProductNo;
						wIPTCheckRecord.PartNo = wOrder.PartNo;
						wIPTCheckRecord.LineName = wOrder.LineName;
						wIPTCheckRecord.CustomerName = wOrder.Customer;
						wIPTCheckRecord.ItemName = wIPTPreCheckProblem.IPTItemName;
						wIPTCheckRecord.Standard = wIPTItem.Standard;
						if (wIPTPreCheckProblem.IPTSOPList != null && wIPTPreCheckProblem.IPTSOPList.size() > 0) {
							wIPTCheckRecord.Opinion = wIPTPreCheckProblem.IPTSOPList.get(0).Detail;
						}
						wIPTCheckRecord.Crafter = QMSConstants.GetBMSEmployeeName(wIPTPreCheckProblem.CarftID);
						wIPTCheckRecord.Operator = this.getOperNameByIDs(wSFCTaskIPT.OperatorList);
						if (wIPTPreCheckProblem.IPTProblemAssessList != null
								&& wIPTPreCheckProblem.IPTProblemAssessList.size() > 0) {
							wIPTCheckRecord.RelaClassMembers = StringUtils.Join(",",
									wIPTPreCheckProblem.IPTProblemAssessList.stream().map(p -> p.Auditor)
											.collect(Collectors.toList()));
							final List<String> wClassNames = new ArrayList<String>();
							for (final IPTProblemAssess wIPTProblemAssess : wIPTPreCheckProblem.IPTProblemAssessList) {
								final Optional<BMSDepartment> wOption = QMSConstants.GetBMSDepartmentList().values()
										.stream()
										.filter(p -> QMSConstants
												.GetBMSEmployee(wIPTProblemAssess.AuditID).DepartmentID == p.ID)
										.findFirst();
								if (wOption.isPresent() && StringUtils.isNotEmpty(wOption.get().Name)) {
									wClassNames.add(wOption.get().Name);
								}
							}
							if (wClassNames.size() > 0) {
								wIPTCheckRecord.RelaDepartments = StringUtils.Join(",", wClassNames);
							}
						}
						wIPTCheckRecord.Confirmation = "已预检";
						wResult.add(wIPTCheckRecord);
					} else {
						final IPTCheckRecord wIPTCheckRecord2 = new IPTCheckRecord();
						wIPTCheckRecord2.OrderNo = wOrder.OrderNo;
						wIPTCheckRecord2.WBSNo = wOrder.WBSNo;
						wIPTCheckRecord2.ProductNo = wOrder.ProductNo;
						wIPTCheckRecord2.PartNo = wOrder.PartNo;
						wIPTCheckRecord2.LineName = wOrder.LineName;
						wIPTCheckRecord2.CustomerName = wOrder.Customer;
						wIPTCheckRecord2.ItemName = wIPTItem.Text;
						wIPTCheckRecord2.Standard = wIPTItem.Standard;
						wIPTCheckRecord2.Confirmation = "待预检";
						wResult.add(wIPTCheckRecord2);
					}
				}
			}
		} catch (Exception e) {
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	private String getOperNameByIDs(List<Integer> wOperatorList) {
		String wResult = "";
		try {
			if (wOperatorList == null || wOperatorList.size() <= 0) {
				return wResult;
			}
			List<String> wNames = new ArrayList<String>();
			for (Integer wID : wOperatorList) {
				String wName = QMSConstants.GetBMSEmployeeName(wID);
				if (StringUtils.isNotEmpty(wName)) {
					wNames.add(wName);
				}
			}
			if (wNames.size() > 0) {
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			IPTServiceImpl.logger.error(ex.toString());
		}
		return wResult;
	}

	public ServiceResult<List<IPTCheckRecord>> IPT_QueryProcessRecord(BMSEmployee wLoginUser, int wOrderID,
			Calendar wStartTime, Calendar wEndTime, int wStationID, int wStepID, int wRecordType, int wSubmitID,
			boolean wIsQuality) {
		final ServiceResult<List<IPTCheckRecord>> wResult = new ServiceResult<>();
		wResult.Result = new ArrayList<>();
		final OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			int wSFCTaskType = 0;
			switch (IPTPreCheckRecordType.getEnumType(wRecordType)) {
			case SelfCheck: {
				wSFCTaskType = SFCTaskType.SelfCheck.getValue();
				break;
			}
			case MutualCheck: {
				wSFCTaskType = SFCTaskType.MutualCheck.getValue();
				break;
			}
			case SpecialCheck: {
				wSFCTaskType = SFCTaskType.SpecialCheck.getValue();
				break;
			}
			default:
				break;
			}

			List<SFCTaskIPT> wSFCTaskIPTList = new ArrayList<SFCTaskIPT>();

			int wChangeOrderID = SFCBogiesChangeBPMDAO.getInstance().GetChangeOrderID(wLoginUser, wOrderID, wErrorCode);
			// 处理转向架互换情况
			if (wChangeOrderID > 0) {
				// ①获取工区工位基础数据
				List<LFSWorkAreaStation> wWSList = LFSServiceImpl.getInstance().LFS_QueryWorkAreaStationList(wLoginUser)
						.List(LFSWorkAreaStation.class);
				wWSList = wWSList.stream().filter(p -> p.Active == 1).collect(Collectors.toList());
				// ②获取车体和整车的工位列表
				List<Integer> wBodyPartIDList = wWSList.stream()
						.filter(p -> p.StationType == LFSStationType.WholeTrain.getValue()
								|| p.StationType == LFSStationType.Body.getValue())
						.map(p -> p.StationID).collect(Collectors.toList());
				// ③获取转向架的工位列表
				List<Integer> wBogiesPartIDList = wWSList.stream()
						.filter(p -> p.StationType == LFSStationType.Bogies.getValue()).map(p -> p.StationID)
						.collect(Collectors.toList());
				if (wStationID > 0) {
					if (wBogiesPartIDList.stream().anyMatch(p -> p == wStationID)) {
						wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wChangeOrderID,
								wStationID, wSFCTaskType, -1, wStepID, -1, "", wStartTime, wEndTime, wErrorCode);
					} else {
						wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID,
								wStationID, wSFCTaskType, -1, wStepID, -1, "", wStartTime, wEndTime, wErrorCode);
					}
				} else {
					// ④查询车体部分检验单
					wSFCTaskIPTList.addAll(SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID,
							wBodyPartIDList, wSFCTaskType, -1, wStepID, -1, "", wStartTime, wEndTime, wErrorCode));
					// ⑤查询转向架部分检验单
					wSFCTaskIPTList.addAll(SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wChangeOrderID,
							wBogiesPartIDList, wSFCTaskType, -1, wStepID, -1, "", wStartTime, wEndTime, wErrorCode));
				}
			} else {
				wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID, wStationID,
						wSFCTaskType, -1, wStepID, -1, "", wStartTime, wEndTime, wErrorCode);
			}

			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}

			wSFCTaskIPTList.removeIf(p -> p.TaskType != SFCTaskType.SelfCheck.getValue()
					&& p.TaskType != SFCTaskType.MutualCheck.getValue()
					&& p.TaskType != SFCTaskType.SpecialCheck.getValue());
			if (wSubmitID > 0) {
				wSFCTaskIPTList = wSFCTaskIPTList.stream().filter(p -> p.OperatorList != null
						&& p.OperatorList.size() > 0 && p.OperatorList.stream().allMatch(q -> q == wSubmitID))
						.collect(Collectors.toList());
			}
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance().SelectItem(
					wLoginUser,
					wSFCTaskIPTList.stream().map(p -> (long) p.ModuleVersionID).distinct().collect(Collectors.toList()),
					wErrorCode).Result;
			List<IPTValue> wAllValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					(List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);
			List<OMSOrder> wAllOrderList = (List<OMSOrder>) LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser,
							wSFCTaskIPTList.stream().map(p -> p.OrderID).distinct().collect(Collectors.toList()))
					.List(OMSOrder.class);
			OMSOrder wOrder = null;
			List<IPTItem> wItemList = null;
			List<IPTValue> wValueList = null;

			// 根据工序任务ID集合查询所有检验单集合
			List<SFCTaskIPT> wIPTList = SFCTaskIPTDAO.getInstance().SelectListByTaskStepIDList(wLoginUser,
					wSFCTaskIPTList.stream().map(p -> p.TaskStepID).collect(Collectors.toList()),
					SFCTaskStepType.Step.getValue(), wErrorCode);

			for (SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				int wCloneOrderID = wSFCTaskIPT.OrderID;
				if (wAllOrderList != null && wAllOrderList.size() > 0
						&& wAllOrderList.stream().anyMatch(p -> p.ID == wCloneOrderID)) {
					wOrder = wAllOrderList.stream().filter(p -> p.ID == wCloneOrderID).findFirst().get();
				}
				if (wOrder == null || wOrder.ID <= 0) {
					return wResult;
				}
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);
				}
				if (wItemList == null) {
					continue;
				}
				if (wItemList.size() <= 0) {
					continue;
				}
				for (IPTItem wIPTItem : wItemList) {
					if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
						continue;
					}
					List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
					wIPTItem.Text = this.GetDescription(wFatherText, wIPTItem);
				}

				if (wIsQuality) {
					wItemList = wItemList.stream().filter(p -> p.IsQuality == 1).collect(Collectors.toList());
				}

				int wCloneTaskID = wSFCTaskIPT.ID;
				if (wAllValueList != null && wAllValueList.size() > 0
						&& wAllValueList.stream().anyMatch(p -> p.TaskID == wCloneTaskID)) {
					wValueList = wAllValueList.stream().filter(p -> p.TaskID == wCloneTaskID)
							.collect(Collectors.toList());
				} else {
					wValueList = new ArrayList<IPTValue>();
				}

				SFCTaskIPT wChangeTask = null;
				List<IPTValue> wChangeValueList = null;
				List<SFCTaskIPT> wChangeTaskList = null;
				for (IPTItem wIPTItem : wItemList) {
					// 判断是否发生了部件互换
					if (StringUtils.isNotEmpty(wIPTItem.Components)) {
						int wPartChangeOrderID = MSSPartRecordDAO.getInstance().MSS_QueryChangeOrderID(wLoginUser,
								wIPTItem.Components, wSFCTaskIPT.OrderID, wErrorCode);
						if (wPartChangeOrderID > 0) {
							// ①查询新订单的wSFCTaskIPT
							wChangeTaskList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
									wChangeOrderID, wSFCTaskIPT.StationID, -1, wSFCTaskIPT.LineID,
									wSFCTaskIPT.PartPointID, -1, "", null, null, wErrorCode);
							int wCloneTaskType = wSFCTaskIPT.TaskType;
							if (wChangeTaskList != null && wChangeTaskList.size() > 0) {
								wChangeTask = wChangeTaskList.stream().filter(p -> p.TaskType == wCloneTaskType)
										.findFirst().get();
							}
							// ②查询新订单的wValueList
							if (wChangeTask != null && wChangeTask.ID > 0) {
								wChangeValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1,
										wChangeTask.ID, -1, -1, -1, wErrorCode);
							}
						}
					}

					IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
					wIPTCheckRecord.OrderNo = wOrder.OrderNo;
					wIPTCheckRecord.WBSNo = wOrder.WBSNo;
					wIPTCheckRecord.ProductNo = wOrder.ProductNo;
					wIPTCheckRecord.PartNo = wOrder.PartNo;
					wIPTCheckRecord.LineName = wOrder.LineName;
					wIPTCheckRecord.CustomerName = wOrder.Customer;
					wIPTCheckRecord.StationName = wSFCTaskIPT.StationName;
					wIPTCheckRecord.StepName = wSFCTaskIPT.PartPointName;
					wIPTCheckRecord.StationID = wSFCTaskIPT.StationID;
					wIPTCheckRecord.StepID = wSFCTaskIPT.PartPointID;

					// 部件过程记录互换
					if (wChangeTask != null && StringUtils.isNotEmpty(wIPTItem.Components)) {
						wSFCTaskIPT = wChangeTask;
						wValueList = wChangeValueList;
						wIPTList = wChangeTaskList;
						wIPTCheckRecord.PartSource = wChangeTask.PartNo;
					}

					// 转向架过程记录互换
					if (wChangeOrderID > 0 && wSFCTaskIPT.OrderID != wOrderID) {
						OMSOrder wBogiesOrder = LOCOAPSServiceImpl.getInstance()
								.OMS_QueryOrderByID(wLoginUser, wOrderID).Info(OMSOrder.class);

						wIPTCheckRecord.OrderNo = wBogiesOrder.OrderNo;
						wIPTCheckRecord.WBSNo = wBogiesOrder.WBSNo;
						wIPTCheckRecord.ProductNo = wBogiesOrder.ProductNo;
						wIPTCheckRecord.PartNo = wBogiesOrder.PartNo;
						wIPTCheckRecord.LineName = wBogiesOrder.LineName;
						wIPTCheckRecord.CustomerName = wBogiesOrder.Customer;

						wIPTCheckRecord.BogiesSource = wOrder.PartNo;
					}

					int wCloneTaskStepID = wSFCTaskIPT.TaskStepID;
					switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
					case SelfCheck: {
						wIPTCheckRecord.Operator = this.GetNamesByIDList(wLoginUser, wSFCTaskIPT.OperatorList);
						wIPTCheckRecord.RecordType = IPTPreCheckRecordType.SelfCheck.getLable();
						break;
					}
					case MutualCheck: {
						// 赋值自检人、互检人
						wIPTCheckRecord.RecordType = IPTPreCheckRecordType.MutualCheck.getLable();
						if (wIPTList.stream().anyMatch(p -> p.TaskStepID == wCloneTaskStepID
								&& p.TaskType == SFCTaskType.SelfCheck.getValue())) {
							wIPTCheckRecord.Operator = this.GetNamesByIDList(wLoginUser,
									wIPTList.stream()
											.filter(p -> p.TaskStepID == wCloneTaskStepID
													&& p.TaskType == SFCTaskType.SelfCheck.getValue())
											.findFirst().get().OperatorList);
						}
						if (wIPTList.stream().anyMatch(p -> p.TaskStepID == wCloneTaskStepID
								&& p.TaskType == SFCTaskType.MutualCheck.getValue())) {
							wIPTCheckRecord.Mutualer = this.GetNamesByIDList(wLoginUser,
									wIPTList.stream()
											.filter(p -> p.TaskStepID == wCloneTaskStepID
													&& p.TaskType == SFCTaskType.MutualCheck.getValue())
											.findFirst().get().OperatorList);
						}
						break;
					}
					case SpecialCheck: {
						// 赋值自检人、互检人、专检人
						if (wIPTList.stream().anyMatch(p -> p.TaskStepID == wCloneTaskStepID
								&& p.TaskType == SFCTaskType.SelfCheck.getValue())) {
							wIPTCheckRecord.Operator = this.GetNamesByIDList(wLoginUser,
									wIPTList.stream()
											.filter(p -> p.TaskStepID == wCloneTaskStepID
													&& p.TaskType == SFCTaskType.SelfCheck.getValue())
											.findFirst().get().OperatorList);
						}
						if (wIPTList.stream().anyMatch(p -> p.TaskStepID == wCloneTaskStepID
								&& p.TaskType == SFCTaskType.MutualCheck.getValue())) {
							wIPTCheckRecord.Mutualer = this.GetNamesByIDList(wLoginUser,
									wIPTList.stream()
											.filter(p -> p.TaskStepID == wCloneTaskStepID
													&& p.TaskType == SFCTaskType.MutualCheck.getValue())
											.findFirst().get().OperatorList);
						}
						if (wIPTList.stream().anyMatch(p -> p.TaskStepID == wCloneTaskStepID
								&& p.TaskType == SFCTaskType.SpecialCheck.getValue())) {
							wIPTCheckRecord.Speciler = this.GetNamesByIDList(wLoginUser,
									wIPTList.stream()
											.filter(p -> p.TaskStepID == wCloneTaskStepID
													&& p.TaskType == SFCTaskType.SpecialCheck.getValue())
											.findFirst().get().OperatorList);
						}
						wIPTCheckRecord.RecordType = IPTPreCheckRecordType.SpecialCheck.getLable();
						break;
					}
					default:
						break;
					}
					wIPTCheckRecord.ItemName = wIPTItem.Text;
					wIPTCheckRecord.Standard = wIPTItem.Standard;
					if (wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
//						IPTValue wIPTValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst()
//								.get();

						IPTValue wIPTValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max(Comparator.comparing(IPTValue::getSubmitTime)).get();

						wIPTCheckRecord.CheckResult = ((wIPTValue.Result == 1) ? "合格" : "不合格");
						wIPTCheckRecord.PartsFactory = wIPTValue.Manufactor;
						wIPTCheckRecord.PartsModal = wIPTValue.Modal;
						wIPTCheckRecord.PartsNumber = wIPTValue.Number;
						wIPTCheckRecord.Value = GetValue(wIPTItem, wIPTValue);
						wIPTCheckRecord.ResultDescribe = wIPTValue.Remark;
						wIPTCheckRecord.Picture = StringUtils.Join(",", wIPTValue.ImagePath);
					}

					if (StringUtils.isEmpty(wIPTCheckRecord.CheckResult)
							&& StringUtils.isNotEmpty(wSFCTaskIPT.PicUri)) {
						wIPTCheckRecord.CheckResult = wSFCTaskIPT.PicUri;
						wIPTCheckRecord.IsPic = 1;
					}

					(wResult.Result).add(wIPTCheckRecord);
				}
			}

			// 排序(工位→工序→作业顺序→类型)
			if (wResult.Result.size() > 0) {
				// 排序
				wResult.Result.sort(Comparator.comparing(IPTCheckRecord::getStationID)
						.thenComparing(IPTCheckRecord::getStepID).thenComparing(IPTCheckRecord::getItemName)
						.thenComparing(IPTCheckRecord::getRecordType));
			}

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			ServiceResult<List<IPTCheckRecord>> serviceResult = wResult;
			serviceResult.FaultCode = String.valueOf(serviceResult.FaultCode) + e.toString();
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取计算类的值
	 */
	private String GetValue(IPTItem wIPTItem, IPTValue wIPTValue) {
		String wResult = "";
		try {
			if (wIPTItem.AutoCalc != 1) {
				wResult = wIPTValue.Value;
				return wResult;
			}

			if (StringUtils.isEmpty(wIPTValue.Value)) {
				return wResult;
			}

			List<List<String>> wAllList = new ArrayList<List<String>>();
			String[] wList = wIPTValue.Value.split("#;#");
			for (String wItem : wList) {
				String[] wItemList = wItem.split("~,~");
				List<String> wStrList = new ArrayList<String>();
				for (String wStr : wItemList) {
					wStrList.add(wStr);
				}
				wAllList.add(wStrList);
			}

			List<String> wJoinList = new ArrayList<String>();
			for (int i = 0; i < wAllList.size(); i++) {
				if (i == wAllList.size() - 1) {
					String wFormat = StringUtils.Format("(A0:{0},a0:{1},β0:{2})", wAllList.get(i).get(0),
							wAllList.get(i).get(1), wAllList.get(i).get(2));
					wJoinList.add(wFormat);
				} else {
					String wFormat = StringUtils.Format("(A{0}:{1},a{0}:{2},β{0}:{3})", i + 1, wAllList.get(i).get(0),
							wAllList.get(i).get(1), wAllList.get(i).get(2));
					wJoinList.add(wFormat);
				}
			}

			wResult = StringUtils.Join(";", wJoinList);

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据人员ID集合获取人员名称
	 * 
	 * @param wLoginUser
	 * @param wIDList
	 * @return
	 */
	private String GetNamesByIDList(BMSEmployee wLoginUser, List<Integer> wIDList) {
		String wResult = "";
		try {
			if (wIDList == null || wIDList.size() <= 0) {
				return wResult;
			}

			List<String> wNames = new ArrayList<String>();
			wIDList.stream().forEach(p -> {
				if (p > 0) {
					wNames.add(QMSConstants.GetBMSEmployeeName(p));
				}
			});
			wNames.removeIf(p -> StringUtils.isEmpty(p));
			wResult = StringUtils.Join(",", wNames);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public ServiceResult<List<IPTCheckRecord>> IPT_QueryOutCheckRecord(BMSEmployee wLoginUser, int wOrderID,
			Calendar wStartTime, Calendar wEndTime, int wPartID, int wStepID, int wSubmitID) {
		ServiceResult<List<IPTCheckRecord>> wResult = new ServiceResult<>();
		wResult.Result = new ArrayList<>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			List<SFCTaskIPT> wSFCTaskIPTList = new ArrayList<SFCTaskIPT>();
			if (wPartID <= 0) {
				List<FPCPart> wFinalList = FMCServiceImpl.getInstance()
						.FPC_QueryPartList(wLoginUser, -1, -1, -1, FPCPartTypes.QTFinally.getValue())
						.List(FPCPart.class);
				if (wFinalList != null && wFinalList.size() > 0) {
					for (FPCPart wFPCPart : wFinalList) {
						List<SFCTaskIPT> wTempList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
								wOrderID, wFPCPart.ID, SFCTaskType.Final.getValue(), -1, wStepID, -1, "", wStartTime,
								wEndTime, wErrorCode);
						if (wTempList != null) {
							if (wTempList.size() <= 0) {
								continue;
							}
							wSFCTaskIPTList.addAll(wTempList);
						}
					}
				}
				List<FPCPart> wOutList = FMCServiceImpl.getInstance()
						.FPC_QueryPartList(wLoginUser, -1, -1, -1, FPCPartTypes.OutFactory.getValue())
						.List(FPCPart.class);
				if (wOutList != null && wOutList.size() > 0) {
					for (FPCPart wFPCPart2 : wOutList) {
						List<SFCTaskIPT> wTempList2 = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
								wOrderID, wFPCPart2.ID, SFCTaskType.OutPlant.getValue(), -1, wStepID, -1, "",
								wStartTime, wEndTime, wErrorCode);
						if (wTempList2 != null) {
							if (wTempList2.size() <= 0) {
								continue;
							}
							wSFCTaskIPTList.addAll(wTempList2);
						}
					}
				}
			} else {
				List<SFCTaskIPT> wTempList3 = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID,
						wPartID, -1, -1, wStepID, -1, "", wStartTime, wEndTime, wErrorCode);
				wTempList3 = wTempList3.stream().filter(p -> p.TaskType == SFCTaskType.Final.getValue()
						|| p.TaskType == SFCTaskType.OutPlant.getValue()).collect(Collectors.toList());
				if (wTempList3 != null && wTempList3.size() > 0) {
					wSFCTaskIPTList.addAll(wTempList3);
				}
			}
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			if (wSubmitID > 0) {
				wSFCTaskIPTList = wSFCTaskIPTList.stream().filter(p -> p.OperatorList != null
						&& p.OperatorList.size() > 0 && p.OperatorList.stream().anyMatch(q -> q == wSubmitID))
						.collect(Collectors.toList());
			}
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance().SelectItem(
					wLoginUser,
					wSFCTaskIPTList.stream().map(p -> (long) p.ModuleVersionID).distinct().collect(Collectors.toList()),
					wErrorCode).Result;
			List<IPTValue> wAllValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					(List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);
			List<OMSOrder> wAllOrderList = (List<OMSOrder>) LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser,
							wSFCTaskIPTList.stream().map(p -> p.OrderID).distinct().collect(Collectors.toList()))
					.List(OMSOrder.class);
			OMSOrder wOrder = null;
			List<IPTItem> wItemList = null;
			List<IPTValue> wValueList = null;
			for (SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				if (wAllOrderList != null && wAllOrderList.size() > 0
						&& wAllOrderList.stream().anyMatch(p -> p.ID == wSFCTaskIPT.OrderID)) {
					wOrder = wAllOrderList.stream().filter(p -> p.ID == wSFCTaskIPT.OrderID).findFirst().get();
				}
				if (wOrder == null || wOrder.ID <= 0) {
					return wResult;
				}
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);
				}
				if (wItemList == null) {
					continue;
				}
				if (wItemList.size() <= 0) {
					continue;
				}
				for (IPTItem wIPTItem : wItemList) {
					if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
						continue;
					}
					List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
					wIPTItem.Text = this.GetDescription(wFatherText, wIPTItem);
				}
				if (wAllValueList != null && wAllValueList.size() > 0
						&& wAllValueList.stream().anyMatch(p -> p.TaskID == wSFCTaskIPT.ID)) {
					wValueList = wAllValueList.stream().filter(p -> p.TaskID == wSFCTaskIPT.ID)
							.collect(Collectors.toList());
				} else {
					wValueList = new ArrayList<IPTValue>();
				}
				for (IPTItem wIPTItem : wItemList) {
					if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
						continue;
					}
					IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
					wIPTCheckRecord.OrderNo = wOrder.OrderNo;
					wIPTCheckRecord.WBSNo = wOrder.WBSNo;
					wIPTCheckRecord.ProductNo = wOrder.ProductNo;
					wIPTCheckRecord.PartNo = wOrder.PartNo;
					wIPTCheckRecord.LineName = wOrder.LineName;
					wIPTCheckRecord.CustomerName = wOrder.Customer;
					wIPTCheckRecord.StationName = wSFCTaskIPT.StationName;
					wIPTCheckRecord.StepName = wSFCTaskIPT.PartPointName;
					wIPTCheckRecord.ItemName = wIPTItem.Text;
					wIPTCheckRecord.Standard = wIPTItem.Standard;
					if (wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						IPTValue wIPTValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst()
								.get();
						wIPTCheckRecord.Operator = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
						wIPTCheckRecord.CheckResult = ((wIPTValue.Result == 1) ? "合格" : "不合格");
						wIPTCheckRecord.PartsFactory = wIPTValue.Manufactor;
						wIPTCheckRecord.PartsModal = wIPTValue.Modal;
						wIPTCheckRecord.PartsNumber = wIPTValue.Number;
						wIPTCheckRecord.Value = GetValue(wIPTItem, wIPTValue);
						wIPTCheckRecord.ResultDescribe = wIPTValue.Remark;
						wIPTCheckRecord.Picture = StringUtils.Join(",", wIPTValue.ImagePath);
					}
					(wResult.Result).add(wIPTCheckRecord);
				}
			}
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			ServiceResult<List<IPTCheckRecord>> serviceResult = wResult;
			serviceResult.FaultCode = String.valueOf(serviceResult.FaultCode) + e.toString();
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<String> ExportPreCheckReportByOrder(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}-机车预检报告.xls",
					new Object[] { wCurTime, wOrder.PartNo.replace("#", "-") });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			List<IPTCheckRecord> wPreCheckRecordList = this.IPT_QueryPreCheckRecordList(wLoginUser, wOrder);
			List<IPTCheckRecord> wExceptionList = this.IPT_QueryExceptionList(wLoginUser, wOrder);
			List<IPTCheckRecord> wPeriodChangeList = this.IPT_QueryPeriodChangeList(wLoginUser, wOrder);
			List<IPTCheckRecord> wKeyComponentList = this.IPT_QueryKeyComponentList(wLoginUser, wOrder);
			ExcelUtil.YJ_WriteReport(wPreCheckRecordList, wExceptionList, wPeriodChangeList, wKeyComponentList,
					wFileOutputStream, StringUtils.Format("{0}-{1}", new Object[] { wOrder.PartNo, wOrder.Customer }));
			String wUri = StringUtils.Format("/{0}/export/{1}",
					new Object[] { Configuration.readConfigString("project.name", "application"), wFileName });
			wResult.Result = wUri;
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			ServiceResult<String> serviceResult = wResult;
			serviceResult.FaultCode = String.valueOf(serviceResult.FaultCode) + e.toString();
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	private List<IPTCheckRecord> IPT_QueryKeyComponentList(BMSEmployee wLoginUser, OMSOrder wOrder) {
		List<IPTCheckRecord> wResult = new ArrayList<IPTCheckRecord>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrder.ID,
					-1, SFCTaskType.PreCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance().SelectItem(
					wLoginUser,
					wSFCTaskIPTList.stream().map(p -> (long) p.ModuleVersionID).distinct().collect(Collectors.toList()),
					wErrorCode).Result;
			List<IPTValue> wAllValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					(List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);
			List<IPTItem> wItemList = null;
			List<IPTValue> wValueList = null;
			for (SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);
				}
				if (wItemList != null) {
					if (wItemList.size() <= 0) {
						continue;
					}
//					wItemList = wItemList.stream().filter(p -> StringUtils.isNotEmpty(p.PartsCoding))
//							.collect(Collectors.toList());

					wItemList = wItemList.stream()
							.filter(p -> (p.ManufactorOption != null && p.ManufactorOption.size() > 0)
									|| (p.ModalOption != null && p.ModalOption.size() > 0))
							.collect(Collectors.toList());

					if (wItemList == null) {
						continue;
					}
					if (wItemList.size() <= 0) {
						continue;
					}
					if (wAllValueList != null && wAllValueList.size() > 0
							&& wAllValueList.stream().anyMatch(p -> p.TaskID == wSFCTaskIPT.ID)) {
						wValueList = wAllValueList.stream().filter(p -> p.TaskID == wSFCTaskIPT.ID)
								.collect(Collectors.toList());
					} else {
						wValueList = new ArrayList<IPTValue>();
					}
					for (IPTItem wIPTItem : wItemList) {
						IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
						wIPTCheckRecord.OrderNo = wOrder.OrderNo;
						wIPTCheckRecord.WBSNo = wOrder.WBSNo;
						wIPTCheckRecord.ProductNo = wOrder.ProductNo;
						wIPTCheckRecord.PartNo = wOrder.PartNo;
						wIPTCheckRecord.LineName = wOrder.LineName;
						wIPTCheckRecord.CustomerName = wOrder.Customer;
						wIPTCheckRecord.ItemName = wIPTItem.Text;
						wIPTCheckRecord.Standard = wIPTItem.Standard;
						wIPTCheckRecord.IsGroupOrStep = 0;
						if (wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
							IPTValue wValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst()
									.get();
							wIPTCheckRecord.PartsFactory = wValue.Manufactor;
							wIPTCheckRecord.PartsModal = wValue.Modal;
							wIPTCheckRecord.PartsNumber = wValue.Number;
							wIPTCheckRecord.CheckResult = ((wValue.Result == 1) ? "合格" : "不合格");
							wIPTCheckRecord.ResultDescribe = wValue.Remark;
							wIPTCheckRecord.Picture = StringUtils.Join(",", wValue.ImagePath);
							if (wValue.IPTProblemBomItemList != null && wValue.IPTProblemBomItemList.size() > 0) {
								List<String> wInfo = new ArrayList<String>();
								for (IPTProblemBomItem wIPTProblemBomItem : wValue.IPTProblemBomItemList) {
									String wStr = StringUtils.Format("【{0}:{1}】", new Object[] {
											wIPTProblemBomItem.MaterialName, wIPTProblemBomItem.Number });
									wInfo.add(wStr);
								}
								wIPTCheckRecord.MaterialInfo = StringUtils.Join(",", wInfo);
							}
						}
						wResult.add(wIPTCheckRecord);
					}
				}
			}
			if (wResult.size() > 0) {
				int wNo = 1;
				for (final IPTCheckRecord wIPTCheckRecord2 : wResult) {
					wIPTCheckRecord2.No = String.valueOf(wNo++);
				}
			}
		} catch (Exception e) {
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	private List<IPTCheckRecord> IPT_QueryPeriodChangeList(BMSEmployee wLoginUser, OMSOrder wOrder) {
		List<IPTCheckRecord> wResult = new ArrayList<IPTCheckRecord>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrder.ID,
					-1, SFCTaskType.PreCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance()
					.SelectItem(wLoginUser, wSFCTaskIPTList.stream().map(p -> (long) p.PeriodChangeStandard).distinct()
							.collect(Collectors.toList()), wErrorCode).Result;
			List<IPTPreCheckProblem> wAllProblemList = IPTPreCheckProblemDAO.getInstance().SelectListByTaskIPTIDList(
					wLoginUser, (List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()),
					wErrorCode);
			List<IPTItem> wItemList = null;
			List<IPTPreCheckProblem> wProblemList = null;
			for (SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.PeriodChangeStandard)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.PeriodChangeStandard);
				}
				if (wItemList != null) {
					if (wItemList.size() <= 0) {
						continue;
					}

					if (wAllProblemList != null && wAllProblemList.size() > 0
							&& wAllProblemList.stream().anyMatch(p -> p.IPTPreCheckTaskID == wSFCTaskIPT.ID)) {
						wProblemList = wAllProblemList.stream().filter(p -> p.IPTPreCheckTaskID == wSFCTaskIPT.ID)
								.collect(Collectors.toList());
					} else {
						wProblemList = new ArrayList<IPTPreCheckProblem>();
					}
//					wProblemList = wProblemList.stream().filter(p -> p.IPTItem != null && p.IPTItem.ID > 0L
//							&& p.IPTItem.IsPeriodChange == wSFCTaskIPT.CustomerID).collect(Collectors.toList());
					for (IPTItem wIPTItem : wItemList) {
						if (wProblemList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
							IPTPreCheckProblem wIPTPreCheckProblem = wProblemList.stream()
									.filter(p -> p.IPTItemID == wIPTItem.ID).findFirst().get();
							IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
							wIPTCheckRecord.OrderNo = wOrder.OrderNo;
							wIPTCheckRecord.WBSNo = wOrder.WBSNo;
							wIPTCheckRecord.ProductNo = wOrder.ProductNo;
							wIPTCheckRecord.PartNo = wOrder.PartNo;
							wIPTCheckRecord.LineName = wOrder.LineName;
							wIPTCheckRecord.CustomerName = wOrder.Customer;
							wIPTCheckRecord.ItemName = wIPTPreCheckProblem.IPTItemName;
							wIPTCheckRecord.Standard = wIPTItem.Standard;
							if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
								continue;
							}
							if (wIPTPreCheckProblem.IPTSOPList != null && wIPTPreCheckProblem.IPTSOPList.size() > 0) {
								wIPTCheckRecord.Opinion = wIPTPreCheckProblem.IPTSOPList.get(0).Detail;
							}
							wIPTCheckRecord.Crafter = QMSConstants.GetBMSEmployeeName(wIPTPreCheckProblem.CarftID);
							wIPTCheckRecord.Operator = this.getOperNameByIDs(wSFCTaskIPT.OperatorList);
							if (wIPTPreCheckProblem.IPTProblemAssessList != null
									&& wIPTPreCheckProblem.IPTProblemAssessList.size() > 0) {
								wIPTCheckRecord.RelaClassMembers = StringUtils.Join(",",
										wIPTPreCheckProblem.IPTProblemAssessList.stream().map(p -> p.Auditor)
												.collect(Collectors.toList()));
								List<String> wClassNames = new ArrayList<String>();
								for (IPTProblemAssess wIPTProblemAssess : wIPTPreCheckProblem.IPTProblemAssessList) {
									Optional<BMSDepartment> wOption = QMSConstants.GetBMSDepartmentList().values()
											.stream()
											.filter(p -> QMSConstants
													.GetBMSEmployee(wIPTProblemAssess.AuditID).DepartmentID == p.ID)
											.findFirst();
									if (wOption.isPresent() && StringUtils.isNotEmpty(wOption.get().Name)) {
										wClassNames.add(wOption.get().Name);
									}
								}
								if (wClassNames.size() > 0) {
									wIPTCheckRecord.RelaDepartments = StringUtils.Join(",", wClassNames);
								}
							}
							wIPTCheckRecord.Confirmation = "已改造";
							// 改进项目及特殊要求
							wIPTCheckRecord.SpecialRequirement = GetSpecialRequirement(wIPTItem, wItemList);
							// 段方要求
							wIPTCheckRecord.PeriodRequirement = GetPeriodRequirement(wIPTItem, wItemList);

							wResult.add(wIPTCheckRecord);
						} else {
							final IPTCheckRecord wIPTCheckRecord2 = new IPTCheckRecord();
							wIPTCheckRecord2.OrderNo = wOrder.OrderNo;
							wIPTCheckRecord2.WBSNo = wOrder.WBSNo;
							wIPTCheckRecord2.ProductNo = wOrder.ProductNo;
							wIPTCheckRecord2.PartNo = wOrder.PartNo;
							wIPTCheckRecord2.LineName = wOrder.LineName;
							wIPTCheckRecord2.CustomerName = wOrder.Customer;
							wIPTCheckRecord2.ItemName = wIPTItem.Text;
							wIPTCheckRecord2.Standard = wIPTItem.Standard;
							if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
								continue;
							}
							wIPTCheckRecord2.Confirmation = "待改造";
							wResult.add(wIPTCheckRecord2);
						}
					}
				}
			}
			if (wResult.size() > 0) {
				int wNo = 1;
				for (final IPTCheckRecord wIPTCheckRecord3 : wResult) {
					wIPTCheckRecord3.No = String.valueOf(wNo++);
				}
			}
		} catch (Exception e) {
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取段方要求
	 */
	private String GetPeriodRequirement(IPTItem wIPTItem, List<IPTItem> wItemList) {
		String wResult = "";
		try {
			if (wIPTItem.GroupID > 0) {
				if (wItemList.stream().anyMatch(p -> p.ID == wIPTItem.GroupID)) {
					IPTItem wItem = wItemList.stream().filter(p -> p.ID == wIPTItem.GroupID).findFirst().get();
					wResult = wItem.Text;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取段改项目及特殊要求
	 */
	private String GetSpecialRequirement(IPTItem wIPTItem, List<IPTItem> wItemList) {
		String wResult = "";
		try {
			if (wIPTItem.GroupID > 0) {
				if (wItemList.stream().anyMatch(p -> p.ID == wIPTItem.GroupID)) {
					IPTItem wItem = wItemList.stream().filter(p -> p.ID == wIPTItem.GroupID).findFirst().get();
					if (wItem.GroupID > 0) {
						if (wItemList.stream().anyMatch(p -> p.ID == wItem.GroupID)) {
							IPTItem wRItem = wItemList.stream().filter(p -> p.ID == wItem.GroupID).findFirst().get();
							wResult = wRItem.Text;
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private List<IPTCheckRecord> IPT_QueryExceptionList(BMSEmployee wLoginUser, OMSOrder wOrder) {
		List<IPTCheckRecord> wResult = new ArrayList<IPTCheckRecord>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrder.ID,
					-1, SFCTaskType.PreCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance().SelectItem(
					wLoginUser,
					wSFCTaskIPTList.stream().map(p -> (long) p.ModuleVersionID).distinct().collect(Collectors.toList()),
					wErrorCode).Result;
			List<IPTPreCheckProblem> wAllProblemList = IPTPreCheckProblemDAO.getInstance().SelectListByTaskIPTIDList(
					wLoginUser, (List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()),
					wErrorCode);
			List<IPTValue> wAllValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					(List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);
			List<IPTPreCheckProblem> wProblemList = null;
			List<IPTValue> wValueList = null;
			List<IPTItem> wItemList = null;
			for (SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);
				}
				if (wItemList != null) {
					if (wItemList.size() <= 0) {
						continue;
					}
					for (IPTItem wIPTItem : wItemList) {
						if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
							continue;
						}
						List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
						wIPTItem.Text = this.GetDescription(wFatherText, wIPTItem);
						wIPTItem.Text = wIPTItem.Text.replace("+|;|+", "-");
					}
					if (wAllProblemList != null && wAllProblemList.size() > 0
							&& wAllProblemList.stream().anyMatch(p -> p.IPTPreCheckTaskID == wSFCTaskIPT.ID)) {
						wProblemList = wAllProblemList.stream().filter(p -> p.IPTPreCheckTaskID == wSFCTaskIPT.ID)
								.collect(Collectors.toList());
					} else {
						wProblemList = new ArrayList<IPTPreCheckProblem>();
					}
					if (wProblemList == null) {
						continue;
					}
					if (wProblemList.size() <= 0) {
						continue;
					}
					wProblemList.removeIf(p -> p.IPTItem.IsPeriodChange > 0);
					if (wProblemList.size() <= 0) {
						continue;
					}
					List<IPTItem> wTempList = wItemList;
					wProblemList.forEach(p -> {
						if (wTempList.stream().anyMatch(q -> q.ID == p.IPTItemID)) {
							p.IPTItem = wTempList.stream().filter(q -> q.ID == p.IPTItemID).findFirst().get();
							p.IPTItemName = p.IPTItem.Text;
						}
						return;
					});
					if (wAllValueList != null && wAllValueList.size() > 0
							&& wAllValueList.stream().anyMatch(p -> p.TaskID == wSFCTaskIPT.ID)) {
						wValueList = wAllValueList.stream().filter(p -> p.TaskID == wSFCTaskIPT.ID)
								.collect(Collectors.toList());
					} else {
						wValueList = new ArrayList<IPTValue>();
					}
					for (IPTPreCheckProblem wIPTPreCheckProblem : wProblemList) {
						IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
						wIPTCheckRecord.OrderNo = wOrder.OrderNo;
						wIPTCheckRecord.WBSNo = wOrder.WBSNo;
						wIPTCheckRecord.ProductNo = wOrder.ProductNo;
						wIPTCheckRecord.PartNo = wOrder.PartNo;
						wIPTCheckRecord.LineName = wOrder.LineName;
						wIPTCheckRecord.CustomerName = wOrder.Customer;
						wIPTCheckRecord.ItemName = wIPTPreCheckProblem.IPTItemName;
						wIPTCheckRecord.Standard = wIPTPreCheckProblem.IPTItem.Standard;
						wIPTCheckRecord.Legend = wIPTPreCheckProblem.IPTItem.Legend;
						if (wValueList != null
								&& wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTPreCheckProblem.IPTItemID)) {
							IPTValue wValue = wValueList.stream()
									.filter(p -> p.IPTItemID == wIPTPreCheckProblem.IPTItemID).findFirst().get();
							wIPTCheckRecord.PartsFactory = wValue.Manufactor;
							wIPTCheckRecord.PartsModal = wValue.Modal;
							wIPTCheckRecord.PartsNumber = wValue.Number;
							wIPTCheckRecord.Value = GetValue(wIPTPreCheckProblem.IPTItem, wValue);
							wIPTCheckRecord.CheckResult = ((wValue.Result == 1) ? "合格" : "不合格");
							wIPTCheckRecord.Operator = QMSConstants.GetBMSEmployeeName(wValue.SubmitID);
						}
						if (wIPTPreCheckProblem.IPTSOPList != null && wIPTPreCheckProblem.IPTSOPList.size() > 0) {
							wIPTCheckRecord.Opinion = wIPTPreCheckProblem.IPTSOPList.get(0).Detail;
						}
						if (wIPTPreCheckProblem.IPTProblemAssessList != null
								&& wIPTPreCheckProblem.IPTProblemAssessList.size() > 0) {
							wIPTCheckRecord.RelaClassMembers = StringUtils.Join(",",
									wIPTPreCheckProblem.IPTProblemAssessList.stream().map(p -> p.Auditor)
											.collect(Collectors.toList()));
							List<String> wClassNames = new ArrayList<String>();
							for (IPTProblemAssess wIPTProblemAssess : wIPTPreCheckProblem.IPTProblemAssessList) {
								Optional<BMSDepartment> wOption = QMSConstants.GetBMSDepartmentList().values().stream()
										.filter(p -> QMSConstants
												.GetBMSEmployee(wIPTProblemAssess.AuditID).DepartmentID == p.ID)
										.findFirst();
								if (wOption.isPresent() && StringUtils.isNotEmpty(wOption.get().Name)) {
									wClassNames.add(wOption.get().Name);
								}
							}
							if (wClassNames.size() > 0) {
								wIPTCheckRecord.RelaDepartments = StringUtils.Join(",", wClassNames);
							}
						}
						wIPTCheckRecord.StepName = wSFCTaskIPT.PartPointName;
						if (wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTPreCheckProblem.IPTItemID)) {
							wIPTCheckRecord.ResultDescribe = wValueList.stream()
									.filter(p -> p.IPTItemID == wIPTPreCheckProblem.IPTItemID).findFirst().get().Remark;
						}
						if (wIPTPreCheckProblem.IPTProblemBomItemList != null
								&& wIPTPreCheckProblem.IPTProblemBomItemList.size() > 0) {
							List<String> wInfo = new ArrayList<String>();
							for (IPTProblemBomItem wIPTProblemBomItem : wIPTPreCheckProblem.IPTProblemBomItemList) {
								String wStr = StringUtils.Format("【{0}:{1}】",
										new Object[] { wIPTProblemBomItem.MaterialName, wIPTProblemBomItem.Number });
								wInfo.add(wStr);
							}
							wIPTCheckRecord.MaterialInfo = StringUtils.Join(",", wInfo);
						}
						if (wIPTPreCheckProblem.IPTSOPList != null && wIPTPreCheckProblem.IPTSOPList.size() > 0
								&& wIPTPreCheckProblem.IPTSOPList.get(0).PathList != null
								&& wIPTPreCheckProblem.IPTSOPList.get(0).PathList.size() > 0) {
							wIPTCheckRecord.Picture = StringUtils.Join(",",
									wIPTPreCheckProblem.IPTSOPList.get(0).PathList);
						}
						wResult.add(wIPTCheckRecord);
					}
				}
			}
			if (wResult.size() > 0) {
				int wNo = 1;
				for (IPTCheckRecord wIPTCheckRecord2 : wResult) {
					wIPTCheckRecord2.No = String.valueOf(wNo++);
					if (StringUtils.isNotEmpty(wIPTCheckRecord2.Picture)) {
						wIPTCheckRecord2.PicNo = StringUtils.Format("(sheet2-{0})", wIPTCheckRecord2.No);
					}
				}
			}
		} catch (Exception e) {
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	private List<IPTCheckRecord> IPT_QueryPreCheckRecordList(BMSEmployee wLoginUser, OMSOrder wOrder) {
		List<IPTCheckRecord> wResult = new ArrayList<IPTCheckRecord>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			List<SFCTaskIPT> wSFCTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrder.ID,
					-1, SFCTaskType.PreCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);
			if (wSFCTaskIPTList == null || wSFCTaskIPTList.size() <= 0) {
				return wResult;
			}
			Map<Long, List<IPTItem>> wItemMap = (Map<Long, List<IPTItem>>) IPTStandardDAO.getInstance().SelectItem(
					wLoginUser,
					wSFCTaskIPTList.stream().map(p -> (long) p.ModuleVersionID).distinct().collect(Collectors.toList()),
					wErrorCode).Result;
			List<IPTValue> wAllValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					(List<Integer>) wSFCTaskIPTList.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);
			int wIndex = 1;
			List<IPTValue> wValueList = null;
			List<IPTItem> wItemList = null;
			for (SFCTaskIPT wSFCTaskIPT : wSFCTaskIPTList) {
				if (wItemMap != null && wItemMap.size() > 0
						&& wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
					wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);
				}
				if (wItemList != null) {
					if (wItemList.size() <= 0) {
						continue;
					}
					if (wAllValueList != null && wAllValueList.size() > 0
							&& wAllValueList.stream().anyMatch(p -> p.TaskID == wSFCTaskIPT.ID)) {
						wValueList = wAllValueList.stream().filter(p -> p.TaskID == wSFCTaskIPT.ID)
								.collect(Collectors.toList());
					} else {
						wValueList = new ArrayList<IPTValue>();
					}
					IPTCheckRecord wItem = new IPTCheckRecord();
					wItem.No = NewCreditReportUtil.toChinese(String.valueOf(wIndex++));
					wItem.ItemName = wSFCTaskIPT.PartPointName;
					wItem.IsGroupOrStep = 2;
					wResult.add(wItem);
					wItemList = NewCreditReportUtil.handleNumber(wItemList);
					for (IPTItem wIPTItem : wItemList) {
						IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
						wIPTCheckRecord.No = wIPTItem.Code;
						wIPTCheckRecord.OrderNo = wOrder.OrderNo;
						wIPTCheckRecord.WBSNo = wOrder.WBSNo;
						wIPTCheckRecord.ProductNo = wOrder.ProductNo;
						wIPTCheckRecord.PartNo = wOrder.PartNo;
						wIPTCheckRecord.LineName = wOrder.LineName;
						wIPTCheckRecord.CustomerName = wOrder.Customer;
						wIPTCheckRecord.StepName = wSFCTaskIPT.PartPointName;
						wIPTCheckRecord.ItemName = wIPTItem.Text;
						wIPTCheckRecord.Standard = wIPTItem.Standard;
						if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
							wIPTCheckRecord.IsGroupOrStep = 1;
						}
						if (wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
							IPTValue wIPTValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst()
									.get();
							wIPTCheckRecord.Operator = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
							wIPTCheckRecord.CheckResult = ((wIPTValue.Result == 1) ? "合格" : "不合格");
							wIPTCheckRecord.PartsFactory = wIPTValue.Manufactor;
							wIPTCheckRecord.PartsModal = wIPTValue.Modal;
							wIPTCheckRecord.PartsNumber = wIPTValue.Number;
							wIPTCheckRecord.Value = GetValue(wIPTItem, wIPTValue);
							wIPTCheckRecord.ResultDescribe = wIPTValue.Remark;
							wIPTCheckRecord.Picture = StringUtils.Join(",", wIPTValue.ImagePath);
							wIPTCheckRecord.PicNo = StringUtils.Format("([sheet1-{0}]-{1})", wIndex - 1,
									wIPTCheckRecord.No);
							if (wIPTValue.IPTProblemBomItemList != null && wIPTValue.IPTProblemBomItemList.size() > 0) {
								List<String> wInfo = new ArrayList<String>();
								for (IPTProblemBomItem wIPTProblemBomItem : wIPTValue.IPTProblemBomItemList) {
									if (StringUtils.isEmpty(wIPTProblemBomItem.MaterialName)) {
										continue;
									}
									String wStr = StringUtils.Format("【{0}:{1}】", new Object[] {
											wIPTProblemBomItem.MaterialName, wIPTProblemBomItem.Number });
									wInfo.add(wStr);
								}
								wIPTCheckRecord.MaterialInfo = StringUtils.Join(",", wInfo);
							}
						}
						wResult.add(wIPTCheckRecord);
					}
				}
			}
		} catch (Exception e) {
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_AddProblems(BMSEmployee wLoginUser, String wWBSNo, String wPartNo,
			String wPeriodChangeItem, String wRequirements, String wOrderNo) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①根据订单号找到订单
			OMSOrder wOMSOrder = null;
			if (StringUtils.isNotEmpty(wOrderNo)) {
				List<OMSOrder> wOMSOrderList = LOCOAPSServiceImpl.getInstance()
						.OMS_QueryOrderList(wLoginUser, -1, wOrderNo, -1, -1, -1, "", "", 1, null).List(OMSOrder.class);

				if (wOMSOrderList.size() > 0)
					wOMSOrder = wOMSOrderList.get(0);
			}

			if (wOMSOrder == null || wOMSOrder.ID <= 0) {
				wResult.FaultCode += "提示：订单数据缺失!";
				return wResult;
			}
			if (wOMSOrder.RouteID <= 0) {
				wResult.FaultCode += "提示：该订单未设置工艺路线!";
				return wResult;
			}
			// ②根据订单工艺路线找到工艺工位数据
			int wRouteID = wOMSOrder.RouteID;
			List<FPCRoutePart> wRoutePartList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wRouteID).collect(Collectors.toList());
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				wResult.FaultCode += "提示：该订单的工艺工位数据缺失!";
				return wResult;
			}
			// ③根据工艺工位数据找到预检工位
			List<FPCRoutePart> wPreRoutePartList = wRoutePartList.stream()
					.filter(p -> QMSConstants.GetFPCPart(p.PartID).PartType == FPCPartTypes.PrevCheck.getValue())
					.collect(Collectors.toList());
			if (wPreRoutePartList == null || wPreRoutePartList.size() <= 0) {
				wResult.FaultCode += "提示：该订单的工艺工位数据中未设置预检工位!";
				return wResult;
			}
			// ④根据订单工艺路线找到工艺工序数据
			List<FPCRoutePartPoint> wRoutePartPointList = QMSConstants.GetFPCRoutePartPointList().stream()
					.filter(p -> p.RouteID == wRouteID).collect(Collectors.toList());
			if (wRoutePartPointList == null || wRoutePartPointList.size() <= 0) {
				wResult.FaultCode += "提示：该订单的工艺工序数据缺失!";
				return wResult;
			}
			// ⑤根据工位和工艺工序数据找到工序列表
			List<FPCRoutePartPoint> wPreRoutePartPointList = wRoutePartPointList.stream()
					.filter(p -> p.PartID == wPreRoutePartList.get(0).PartID).collect(Collectors.toList());
			if (wPreRoutePartPointList == null || wPreRoutePartPointList.size() <= 0) {
				wResult.FaultCode += "提示：该订单的预检工位中未设置工艺工序!";
				return wResult;
			}
			// ⑥根据订单、工位、工序找到预检单
			List<SFCTaskIPT> wTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOMSOrder.ID,
					wPreRoutePartList.get(0).PartID, SFCTaskType.PreCheck.getValue(), -1,
					wPreRoutePartPointList.get(0).PartPointID, -1, "", null, null, wErrorCode);
			IPTStandard wStandard = null;
			if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
				wStandard = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, wLoginUser.CompanyID,
						IPTMode.PreCheck, -1, -1, -1, -1, -1, wOMSOrder.LineID, wPreRoutePartList.get(0).PartID,
						wPreRoutePartPointList.get(0).PartPointID, -1, wOMSOrder.ProductID, wErrorCode).Result;
				if (wStandard == null || wStandard.ID <= 0) {
					wResult.FaultCode += StringUtils.Format("提示：{0}-{1} 未设置当前标准!",
							QMSConstants.GetFPCPartName(wPreRoutePartList.get(0).PartID),
							QMSConstants.GetFPCStepName(wPreRoutePartPointList.get(0).PartPointID));
					return wResult;
				}
			} else {
				wStandard = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
						wTaskIPTList.get(0).ModuleVersionID, wErrorCode).Result;
				if (wStandard == null || wStandard.ID <= 0) {
					wResult.FaultCode += StringUtils.Format("提示：{0}-{1} 标准未找到!",
							QMSConstants.GetFPCPartName(wPreRoutePartList.get(0).PartID),
							QMSConstants.GetFPCStepName(wPreRoutePartPointList.get(0).PartPointID));
					return wResult;
				}
			}
			// ⑥根据标准获取标准项
			List<IPTItem> wItemList = null;
			Map<Long, List<IPTItem>> wMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser,
					new ArrayList<Long>(Arrays.asList(wStandard.ID)), wErrorCode).Result;
			if (wMap != null && wMap.containsKey(wStandard.ID)) {
				wItemList = wMap.get(wStandard.ID);
			}
			// ⑦创建项目，将项目添加进标准里
			IPTItem wItem = null;
			if (wItemList != null && wItemList.stream()
					.anyMatch(p -> p.Text.equals(wPeriodChangeItem) && p.Standard.equals(wRequirements))) {
				wItem = wItemList.stream()
						.filter(p -> p.Text.equals(wPeriodChangeItem) && p.Standard.equals(wRequirements)).findFirst()
						.get();
			} else {
				wItem = new IPTItem();
				wItem.Active = 2;
				wItem.Text = wPeriodChangeItem;
				wItem.Standard = wRequirements;
				wItem.IsPeriodChange = wTaskIPTList.get(0).CustomerID;
				wItem.ItemType = IPTItemType.ProblemItem.getValue();
				wItem.StandardType = IPTStandardType.Text.getValue();
				wItem.ID = IPTItemDAO.getInstance().Update(wLoginUser, wStandard.ID, wItem, wErrorCode);
			}
			// ⑧创建预检知识库
			IPTSolveLib wIPTSolveLib = new IPTSolveLib();
			wIPTSolveLib.CreateID = wLoginUser.ID;
			wIPTSolveLib.CreateTime = Calendar.getInstance();
			wIPTSolveLib.Creator = wLoginUser.Name;
			wIPTSolveLib.CustomID = wOMSOrder.CustomerID;
			wIPTSolveLib.Description = wPeriodChangeItem;
			wIPTSolveLib.Details = wRequirements;
			wIPTSolveLib.EditID = wLoginUser.ID;
			wIPTSolveLib.Editor = wLoginUser.Name;
			wIPTSolveLib.EditTime = Calendar.getInstance();
			wIPTSolveLib.ID = 0;
			wIPTSolveLib.ImageList = new ArrayList<String>();
			wIPTSolveLib.IPTItemID = (int) wItem.ID;
			wIPTSolveLib.LineID = wOMSOrder.LineID;
			wIPTSolveLib.ProductID = wOMSOrder.ProductID;
			wIPTSolveLib.VideoList = new ArrayList<String>();
			int wSolveID = IPTSolveLibDAO.getInstance().Update(wLoginUser, wIPTSolveLib, wErrorCode);
			if (wSolveID <= 0) {
				wResult.FaultCode += "提示：知识库创建失败!";
				return wResult;
			}

			// ⑨保存问项题
			IPTPreCheckProblem wIPTPreCheckProblem = new IPTPreCheckProblem();
			wIPTPreCheckProblem.IPTPreCheckTaskID = wTaskIPTList != null && wTaskIPTList.size() > 0
					? wTaskIPTList.get(0).ID
					: 0;
			wIPTPreCheckProblem.IPTItemID = (int) wItem.ID;
			wIPTPreCheckProblem.SolveID = wSolveID;
			wIPTPreCheckProblem.ProductID = wOMSOrder.ProductID;
			wIPTPreCheckProblem.CarNumber = wOMSOrder.PartNo;
			wIPTPreCheckProblem.LineID = wOMSOrder.LineID;
			wIPTPreCheckProblem.CustomID = wOMSOrder.CustomerID;
			wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.ToCraftSendAudit.getValue();
			wIPTPreCheckProblem.OrderID = wOMSOrder.ID;
			wIPTPreCheckProblem.OrderNo = wOMSOrder.OrderNo;
			wIPTPreCheckProblem.PreCheckID = wLoginUser.ID;
			wIPTPreCheckProblem.PreCheckTime = Calendar.getInstance();
			wIPTPreCheckProblem.APSTaskStepID = wTaskIPTList != null && wTaskIPTList.size() > 0
					? wTaskIPTList.get(0).TaskStepID
					: 0;
			int wNewID = IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);

			// 预检问题项触发出来时，发送任务消息给现场工艺人员
			String wText = wItem.Text;
			IPT_SendMessageToCraftWhenProblemTaskCreated(wLoginUser, wNewID, wText);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTItem>> QueryItemListByCustomer(BMSEmployee wLoginUser, int wStandardID,
			int wCustomerID) {
		ServiceResult<List<IPTItem>> wResult = new ServiceResult<List<IPTItem>>();
		wResult.Result = new ArrayList<IPTItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wStandardID, -1, -1,
					wErrorCode);
			if (wItemList == null || wItemList.size() <= 0) {
				return wResult;
			}

			// 查询所有局段的段改项
			if (wCustomerID < 0) {
				wResult.Result = wItemList.stream().filter(p -> p.IsPeriodChange > 0).collect(Collectors.toList());
			}
			// 查询所有的检验项
			else if (wCustomerID == 0) {
				wResult.Result = wItemList;
			}
			// 查询指定局段的段改项
			else {
				wResult.Result = wItemList.stream().filter(p -> p.IsPeriodChange == wCustomerID)
						.collect(Collectors.toList());
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportProduceProcess(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}-机车生产过程检验报告.xls",
					new Object[] { wCurTime, wOrder.PartNo.replace("#", "-") });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			Map<String, List<IPTCheckRecord>> wRecordMap = this.IPT_QueryRecordMap(wLoginUser, wOrder, false, -1, -1);
			if (wRecordMap == null || wRecordMap.size() <= 0) {
				wResult.FaultCode += "提示：未查询到该订单的生产过程检记录!";
				return wResult;
			}

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			ExcelUtil.SCGC_WriteReport(wRecordMap, wFileOutputStream,
					StringUtils.Format("{0}-{1}", wOrder.PartNo, wOrder.Customer), false, "生产过程检验记录");
			String wUri = StringUtils.Format("/{0}/export/{1}",
					new Object[] { Configuration.readConfigString("project.name", "application"), wFileName });
			wResult.Result = wUri;
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取生产过程检验记录
	 */
	private Map<String, List<IPTCheckRecord>> IPT_QueryRecordMap(BMSEmployee wLoginUser, OMSOrder wOrder,
			boolean wIsQuality, int wSFCTaskType, int wMyPartID) {
		Map<String, List<IPTCheckRecord>> wResult = new LinkedHashMap<String, List<IPTCheckRecord>>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd");

			// ①根据订单获取该车的所有检验单
			List<SFCTaskIPT> wTaskList = new ArrayList<SFCTaskIPT>();

			int wChangeOrderID = SFCBogiesChangeBPMDAO.getInstance().GetChangeOrderID(wLoginUser, wOrder.ID,
					wErrorCode);
			// 处理转向架互换情况
			if (wChangeOrderID > 0) {
				// ①获取工区工位基础数据
				List<LFSWorkAreaStation> wWSList = LFSServiceImpl.getInstance().LFS_QueryWorkAreaStationList(wLoginUser)
						.List(LFSWorkAreaStation.class);
				wWSList = wWSList.stream().filter(p -> p.Active == 1).collect(Collectors.toList());
				// ②获取车体和整车的工位列表
				List<Integer> wBodyPartIDList = wWSList.stream()
						.filter(p -> p.StationType == LFSStationType.WholeTrain.getValue()
								|| p.StationType == LFSStationType.Body.getValue())
						.map(p -> p.StationID).collect(Collectors.toList());
				// ③获取转向架的工位列表
				List<Integer> wBogiesPartIDList = wWSList.stream()
						.filter(p -> p.StationType == LFSStationType.Bogies.getValue()).map(p -> p.StationID)
						.collect(Collectors.toList());
				// ④查询车体部分检验单
				wTaskList.addAll(SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrder.ID, wBodyPartIDList,
						wSFCTaskType, -1, -1, -1, "", null, null, wErrorCode));
				// ⑤查询转向架部分检验单
				wTaskList.addAll(SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wChangeOrderID,
						wBogiesPartIDList, wSFCTaskType, -1, -1, -1, "", null, null, wErrorCode));

				if (wMyPartID > 0)
					wTaskList = wTaskList.stream().filter(p -> p.StationID == wMyPartID).collect(Collectors.toList());
			} else {
				wTaskList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrder.ID, wMyPartID,
						wSFCTaskType, -1, -1, -1, "", null, null, wErrorCode);
			}

			// ②筛选自检、互检、专检，类型为工序任务的检验单
			if (wSFCTaskType != SFCTaskType.Final.getValue() && wSFCTaskType != SFCTaskType.OutPlant.getValue()) {
				wTaskList = wTaskList.stream()
						.filter(p -> (p.TaskType == SFCTaskType.SelfCheck.getValue()
								|| p.TaskType == SFCTaskType.MutualCheck.getValue()
								|| p.TaskType == SFCTaskType.SpecialCheck.getValue())
								&& p.Type == SFCTaskStepType.Step.getValue())
						.collect(Collectors.toList());
			}
			if (wTaskList == null || wTaskList.size() <= 0) {
				return wResult;
			}
			// ③根据检验单集合获取所有检验值集合
			List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					wTaskList.stream().map(p -> p.ID).distinct().collect(Collectors.toList()), wErrorCode);
			// ③获取标准集合
			List<Long> wStandardIDList = wTaskList.stream().map(p -> (long) p.ModuleVersionID).distinct()
					.collect(Collectors.toList());
			// ④根据标准集合获取所有检验项集合
			Map<Long, List<IPTItem>> wAllItemMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser, wStandardIDList,
					wErrorCode).Result;
			// ⑤根据检验单获取所有工位集合
			List<Integer> wPartIDList = wTaskList.stream().map(p -> p.StationID).distinct()
					.collect(Collectors.toList());
			if (wPartIDList == null || wPartIDList.size() <= 0) {
				return wResult;
			}
			// ⑥遍历工位，取值
			int wSheet = 1;
			for (Integer wPartID : wPartIDList) {
				// ①根据工位筛选该工位下的所有工序
				List<Integer> wPartPointIDList = wTaskList.stream().filter(p -> p.StationID == wPartID)
						.map(p -> p.PartPointID).distinct().collect(Collectors.toList());
				// ②遍历工序，获取该工序下的所有检验单
				List<IPTCheckRecord> wRecordList = new ArrayList<IPTCheckRecord>();
				wResult.put(QMSConstants.GetFPCPartName(wPartID), wRecordList);
				int wIndex = 1;
				for (Integer wPartPointID : wPartPointIDList) {
					// 添加工序
					IPTCheckRecord wItem = new IPTCheckRecord();
					wItem.No = NewCreditReportUtil.toChinese(String.valueOf(wIndex++));
					wItem.ItemName = QMSConstants.GetFPCStepName(wPartPointID);
					wItem.IsGroupOrStep = 2;
					wRecordList.add(wItem);

					List<SFCTaskIPT> wIPTList = wTaskList.stream()
							.filter(p -> p.StationID == wPartID && p.PartPointID == wPartPointID)
							.collect(Collectors.toList());
					if (wIPTList == null || wIPTList.size() <= 0) {
						continue;
					}
					// ③取第一个检验单，根据该检验单中的标准获取所有检验项
					SFCTaskIPT wSFCTaskIPT = wIPTList.get(0);
					if (!wAllItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
						continue;
					}
					List<IPTItem> wItemList = wAllItemMap.get((long) wSFCTaskIPT.ModuleVersionID);
					if (wIsQuality) {
						wItemList = wItemList.stream()
								.filter(p -> p.ItemType == IPTItemType.Group.getValue() || p.IsQuality == 1)
								.collect(Collectors.toList());
						// 去除没有子项的组
						List<IPTItem> wSourceList = CloneTool.CloneArray(wItemList, IPTItem.class);
						wItemList.removeIf(p -> p.ItemType == IPTItemType.Group.getValue()
								&& !wSourceList.stream().anyMatch(q -> q.GroupID == p.ID));
					}
					wItemList = NewCreditReportUtil.handleNumber(wItemList);
					// ④遍历所有检验项，依次赋值记录
					for (IPTItem wIPTItem : wItemList) {
						String wOperator = "";
						String wMutualer = "";
						String wSpeciler = "";
						List<Object> wObjects = this.IPT_GetValue(wIPTItem, wTaskList, wValueList, wSFCTaskIPT,
								wOperator, wMutualer, wSpeciler);
						IPTValue wValue = (IPTValue) wObjects.get(0);
						wOperator = (String) wObjects.get(1);
						wMutualer = (String) wObjects.get(2);
						wSpeciler = (String) wObjects.get(3);

						IPTCheckRecord wIPTCheckRecord = new IPTCheckRecord();
						wIPTCheckRecord.No = wIPTItem.Code;
						wIPTCheckRecord.ItemName = wIPTItem.Text;
						wIPTCheckRecord.WorkContent = wIPTItem.WorkContent;
						wIPTCheckRecord.Standard = wIPTItem.Standard;
						wIPTCheckRecord.Legend = "";
						if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
							wIPTCheckRecord.IsGroupOrStep = 1;
						}
						if (wValue.ID > 0) {
							IPTValue wIPTValue = wValue;
							wIPTCheckRecord.PartsFactory = wIPTValue.Manufactor;
							wIPTCheckRecord.PartsModal = wIPTValue.Modal;
							wIPTCheckRecord.PartsNumber = wIPTValue.Number;
							wIPTCheckRecord.Value = GetValue(wIPTItem, wIPTValue);
							wIPTCheckRecord.CheckResult = ((wIPTValue.Result == 1) ? "合格" : "不合格");
							wIPTCheckRecord.ResultDescribe = wIPTValue.Remark;
							wIPTCheckRecord.Picture = StringUtils.Join(",", wIPTValue.ImagePath);
							wIPTCheckRecord.PicNo = StringUtils.Format("([sheet{0}-{1}]-{2})", wSheet, wIndex - 1,
									wIPTCheckRecord.No);
							wIPTCheckRecord.Video = StringUtils.Join(",", wIPTValue.VideoPath);
							wIPTCheckRecord.Operator = wOperator;
							wIPTCheckRecord.Mutualer = wMutualer;
							wIPTCheckRecord.Speciler = wSpeciler;
							wIPTCheckRecord.CheckDate = wSDF.format(wIPTValue.SubmitTime.getTime());
						}
						// 拍照工序，赋值处理
						if (StringUtils.isNotEmpty(wSFCTaskIPT.PicUri)) {
							if (wIPTList.stream().anyMatch(p -> p.TaskType == SFCTaskType.SelfCheck.getValue())) {
								SFCTaskIPT wTask = wIPTList.stream()
										.filter(p -> p.TaskType == SFCTaskType.SelfCheck.getValue()).findFirst().get();
								wIPTCheckRecord.Operator = wTask.OperatorListNames;
								wIPTCheckRecord.CheckDate = wSDF.format(wTask.SubmitTime.getTime());
							}
							if (wIPTList.stream().anyMatch(p -> p.TaskType == SFCTaskType.MutualCheck.getValue())) {
								SFCTaskIPT wTask = wIPTList.stream()
										.filter(p -> p.TaskType == SFCTaskType.MutualCheck.getValue()).findFirst()
										.get();
								wIPTCheckRecord.Mutualer = wTask.OperatorListNames;
								wIPTCheckRecord.CheckDate = wSDF.format(wTask.SubmitTime.getTime());
							}
							if (wIPTList.stream().anyMatch(p -> p.TaskType == SFCTaskType.SpecialCheck.getValue())) {
								SFCTaskIPT wTask = wIPTList.stream()
										.filter(p -> p.TaskType == SFCTaskType.SpecialCheck.getValue()).findFirst()
										.get();
								wIPTCheckRecord.Speciler = wTask.OperatorListNames;
								wIPTCheckRecord.CheckDate = wSDF.format(wTask.SubmitTime.getTime());
							}

							wIPTCheckRecord.CheckResult = "合格";
							wIPTCheckRecord.Picture = wSFCTaskIPT.PicUri;
						}

						wRecordList.add(wIPTCheckRecord);
					}
				}
				wSheet++;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取检验值(做了自检以自检为准，做了互检以互检为准，做了专检以专检为准)
	 * 
	 * @param wIPTItem    检验项
	 * @param wTaskList   所有检验单集合
	 * @param wValueList  所有检验值集合
	 * @param wSFCTaskIPT 某个检验单
	 * @return 检验值
	 */
	private List<Object> IPT_GetValue(IPTItem wIPTItem, List<SFCTaskIPT> wTaskList, List<IPTValue> wValueList,
			SFCTaskIPT wSFCTaskIPT, String wOperator, String wMutualer, String wSpeciler) {
		List<Object> wResult = new ArrayList<Object>();
		try {
			IPTValue wValue = new IPTValue();
			// ①筛选出工序任务对应的检验单
			List<SFCTaskIPT> wMyList = wTaskList.stream().filter(p -> p.TaskStepID == wSFCTaskIPT.TaskStepID)
					.collect(Collectors.toList());
			if (wMyList == null || wMyList.size() <= 0) {
				return wResult;
			}
			// ②分别获取自检单、互检单、专检单
			SFCTaskIPT wSelfIPT = null;
			SFCTaskIPT wMutualIPT = null;
			SFCTaskIPT wSpecialIPT = null;
			SFCTaskIPT wFinalIPT = null;
			SFCTaskIPT wOutIPT = null;
			if (wMyList.stream().anyMatch(p -> p.TaskType == SFCTaskType.SelfCheck.getValue())) {
				wSelfIPT = wMyList.stream().filter(p -> p.TaskType == SFCTaskType.SelfCheck.getValue()).findFirst()
						.get();
			}
			if (wMyList.stream().anyMatch(p -> p.TaskType == SFCTaskType.MutualCheck.getValue())) {
				wMutualIPT = wMyList.stream().filter(p -> p.TaskType == SFCTaskType.MutualCheck.getValue()).findFirst()
						.get();
			}
			if (wMyList.stream().anyMatch(p -> p.TaskType == SFCTaskType.SpecialCheck.getValue())) {
				wSpecialIPT = wMyList.stream().filter(p -> p.TaskType == SFCTaskType.SpecialCheck.getValue())
						.findFirst().get();
			}
			if (wMyList.stream().anyMatch(p -> p.TaskType == SFCTaskType.Final.getValue())) {
				wFinalIPT = wMyList.stream().filter(p -> p.TaskType == SFCTaskType.Final.getValue()).findFirst().get();
			}
			if (wMyList.stream().anyMatch(p -> p.TaskType == SFCTaskType.OutPlant.getValue())) {
				wOutIPT = wMyList.stream().filter(p -> p.TaskType == SFCTaskType.OutPlant.getValue()).findFirst().get();
			}
			// ③根据专检单获取检验值，判断目标项是否做了专检，若做了，Value就是他，那么对应的专检人就是他
			if (wSpecialIPT != null && wSpecialIPT.ID > 0) {
				int wIPTID = wSpecialIPT.ID;
				List<IPTValue> wSpecialValueList = wValueList.stream().filter(p -> p.TaskID == wIPTID)
						.collect(Collectors.toList());
				if (wSpecialValueList != null && wSpecialValueList.size() > 0
						&& wSpecialValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
					wSpecialValueList = wSpecialValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
							.collect(Collectors.toList());
					wValue = wSpecialValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get();
					wSpeciler = QMSConstants.GetBMSEmployeeName(wValue.SubmitID);
				}
			}
			// ④根据互检单获取检验值，判断目标项是否做了互检，若做了，且value的ID还是小于0，那么VAlue就是他，那么对应的互检人就是他
			if (wMutualIPT != null && wMutualIPT.ID > 0) {
				int wIPTID = wMutualIPT.ID;
				List<IPTValue> wTempValueList = wValueList.stream().filter(p -> p.TaskID == wIPTID)
						.collect(Collectors.toList());
				if (wTempValueList != null && wTempValueList.size() > 0
						&& wTempValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
					wTempValueList = wTempValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
							.collect(Collectors.toList());
					IPTValue wTempValue = wTempValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime))
							.get();
					if (wValue == null || wValue.ID <= 0) {
						wValue = wTempValue;
					}
					wMutualer = QMSConstants.GetBMSEmployeeName(wTempValue.SubmitID);
				}
			}
			// ⑤根据自检单获取检验值，判断目标项是否做了自检，若做了，且value的ID还是小于0，那么value就是他，那么对应的自检人就是他
			if (wSelfIPT != null && wSelfIPT.ID > 0) {
				int wIPTID = wSelfIPT.ID;
				List<IPTValue> wTempValueList = wValueList.stream().filter(p -> p.TaskID == wIPTID)
						.collect(Collectors.toList());
				if (wTempValueList != null && wTempValueList.size() > 0
						&& wTempValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
					wTempValueList = wTempValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
							.collect(Collectors.toList());
					IPTValue wTempValue = wTempValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime))
							.get();
					if (wValue == null || wValue.ID <= 0) {
						wValue = wTempValue;
					}
					wOperator = QMSConstants.GetBMSEmployeeName(wTempValue.SubmitID);
				}
			}
			// 判断终检单
			if (wFinalIPT != null && wFinalIPT.ID > 0) {
				int wIPTID = wFinalIPT.ID;
				List<IPTValue> wTempValueList = wValueList.stream().filter(p -> p.TaskID == wIPTID)
						.collect(Collectors.toList());
				if (wTempValueList != null && wTempValueList.size() > 0
						&& wTempValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
					wTempValueList = wTempValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
							.collect(Collectors.toList());
					IPTValue wTempValue = wTempValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime))
							.get();
					if (wValue == null || wValue.ID <= 0) {
						wValue = wTempValue;
					}
					wOperator = QMSConstants.GetBMSEmployeeName(wTempValue.SubmitID);
				}
			}
			// 判断出厂检单
			if (wOutIPT != null && wOutIPT.ID > 0) {
				int wIPTID = wOutIPT.ID;
				List<IPTValue> wTempValueList = wValueList.stream().filter(p -> p.TaskID == wIPTID)
						.collect(Collectors.toList());
				if (wTempValueList != null && wTempValueList.size() > 0
						&& wTempValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
					wTempValueList = wTempValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
							.collect(Collectors.toList());
					IPTValue wTempValue = wTempValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime))
							.get();
					if (wValue == null || wValue.ID <= 0) {
						wValue = wTempValue;
					}
					wOperator = QMSConstants.GetBMSEmployeeName(wTempValue.SubmitID);
				}
			}

			wResult.add(wValue);
			wResult.add(wOperator);
			wResult.add(wMutualer);
			wResult.add(wSpeciler);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportQualityProcess(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}.xls",
					new Object[] { wCurTime, wOrder.PartNo.replace("#", "-") });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			Map<String, List<IPTCheckRecord>> wRecordMap = this.IPT_QueryRecordMap(wLoginUser, wOrder, true, -1, -1);
			if (wRecordMap == null || wRecordMap.size() <= 0) {
				wResult.FaultCode += "提示：未查询到该订单的质量过程检记录!";
				return wResult;
			}

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			ExcelUtil.SCGC_WriteReport(wRecordMap, wFileOutputStream,
					StringUtils.Format("{0}-{1}", wOrder.PartNo, wOrder.Customer), false, "质量过程检验记录");
			String wUri = StringUtils.Format("/{0}/export/{1}",
					new Object[] { Configuration.readConfigString("project.name", "application"), wFileName });
			wResult.Result = wUri;
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportFinalCheck(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}-机车质量终检报告.xls",
					new Object[] { wCurTime, wOrder.PartNo.replace("#", "-") });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			Map<String, List<IPTCheckRecord>> wRecordMap = this.IPT_QueryRecordMap(wLoginUser, wOrder, false,
					SFCTaskType.Final.getValue(), -1);
			if (wRecordMap == null || wRecordMap.size() <= 0) {
				wResult.FaultCode += "提示：未查询到该订单的终检记录!";
				return wResult;
			}
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);

			ExcelUtil.SCGC_WriteReport(wRecordMap, wFileOutputStream,
					StringUtils.Format("{0}-{1}", wOrder.PartNo, wOrder.Customer), true, "质量终检记录");
			String wUri = StringUtils.Format("/{0}/export/{1}",
					new Object[] { Configuration.readConfigString("project.name", "application"), wFileName });
			wResult.Result = wUri;
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportOutCheck(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}-机车质量出厂检报告.xls",
					new Object[] { wCurTime, wOrder.PartNo.replace("#", "-") });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			Map<String, List<IPTCheckRecord>> wRecordMap = this.IPT_QueryRecordMap(wLoginUser, wOrder, false,
					SFCTaskType.OutPlant.getValue(), -1);
			if (wRecordMap == null || wRecordMap.size() <= 0) {
				wResult.FaultCode += "提示：未查询到该订单的出厂检记录!";
				return wResult;
			}

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			ExcelUtil.SCGC_WriteReport(wRecordMap, wFileOutputStream,
					StringUtils.Format("{0}-{1}", wOrder.PartNo, wOrder.Customer), true, "质量出厂检记录");
			String wUri = StringUtils.Format("/{0}/export/{1}",
					new Object[] { Configuration.readConfigString("project.name", "application"), wFileName });
			wResult.Result = wUri;
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_ImportProblems(BMSEmployee wLoginUser, ExcelData wExcelData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			if (wExcelData == null || wExcelData.sheetData == null || wExcelData.sheetData.size() <= 0) {
				wResult.FaultCode += "提示：Excel数据解析失败或无数据!";
				return wResult;
			}

			ExcelSheetData wSheetData = wExcelData.sheetData.get(0);

			if (wSheetData.lineData == null || wSheetData.lineData.size() <= 0) {
				wResult.FaultCode += "提示：Excel数据解析失败或无数据!";
				return wResult;
			}

			Map<String, OMSOrder> wOrderMap = new HashMap<String, OMSOrder>();
			OMSOrder wOrder = null;
			for (int i = 1; i < wSheetData.lineData.size(); i++) {
				int wIndex = 0;
				// 车型
				String wProductNo = wSheetData.lineData.get(i).colData.get(wIndex++);
				// 车号
				String wNumber = wSheetData.lineData.get(i).colData.get(wIndex++);
				// 一级项点
				String wOneItem = wSheetData.lineData.get(i).colData.get(wIndex++);
				// 二级项点
				String wTwoItem = wSheetData.lineData.get(i).colData.get(wIndex++);
				// 三级项点
				String wThreeItem = wSheetData.lineData.get(i).colData.get(wIndex++);
				// 四级项点
				String wFourItem = wSheetData.lineData.get(i).colData.get(wIndex++);
				// 五级项点
				String wFiveItem = wSheetData.lineData.get(i).colData.get(wIndex++);
				// 段方要求
				String wRequirment = wSheetData.lineData.get(i).colData.get(wIndex++);

				String wPartNo = StringUtils.Format("{0}#{1}", wProductNo, wNumber);

				wOrder = null;

				if (StringUtils.isNotEmpty(wPartNo)) {
					if (wOrderMap.containsKey(wPartNo)) {
						wOrder = wOrderMap.get(wPartNo);
					} else {

						List<OMSOrder> wOMSOrderList = LOCOAPSServiceImpl.getInstance()
								.OMS_QueryOrderList(wLoginUser, -1, "", -1, -1, -1, wPartNo, "", 1, null)
								.List(OMSOrder.class);

						if (wOMSOrderList.size() > 0) {
							wOrder = wOMSOrderList.get(0);
							wOrderMap.put(wPartNo, wOrder);
						}
					}
				}

				if (wOrder == null || wOrder.ID <= 0) {
					wResult.FaultCode += StringUtils.Format("提示：系统未找到车号为【{0}】的订单", wPartNo);
					return wResult;
				}

				// 总段改项
				String wItem = this.GetItem(wOneItem, wTwoItem, wThreeItem, wFourItem, wFiveItem);

				if (StringUtils.isEmpty(wItem)) {
					wResult.FaultCode += StringUtils.Format("提示：第{0}行数据有问题，项点输入不合法!", i + 1);
					return wResult;
				}

				wResult = this.IPT_AddProblems(wLoginUser, wOrder.WBSNo, wOrder.PartNo, wItem, wRequirment,
						wOrder.OrderNo);

				if (StringUtils.isNotEmpty(wResult.FaultCode)) {
					return wResult;
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取项点
	 * 
	 * @param wOneItem   一级项点
	 * @param wTwoItem   二级项点
	 * @param wThreeItem 三级项点
	 * @param wFourItem  四级项点
	 * @param wFiveItem  五级项点
	 * @return 总项点
	 */
	private String GetItem(String wOneItem, String wTwoItem, String wThreeItem, String wFourItem, String wFiveItem) {
		String wResult = "";
		try {
			if (StringUtils.isNotEmpty(wOneItem) && StringUtils.isEmpty(wTwoItem) && StringUtils.isEmpty(wThreeItem)
					&& StringUtils.isEmpty(wFourItem) && StringUtils.isEmpty(wFiveItem)) {
				wResult = wOneItem;
			} else if (StringUtils.isNotEmpty(wOneItem) && StringUtils.isNotEmpty(wTwoItem)
					&& StringUtils.isEmpty(wThreeItem) && StringUtils.isEmpty(wFourItem)
					&& StringUtils.isEmpty(wFiveItem)) {
				wResult = StringUtils.Format("【{0}】+|;|+{1}", wOneItem, wTwoItem);
			} else if (StringUtils.isNotEmpty(wOneItem) && StringUtils.isNotEmpty(wTwoItem)
					&& StringUtils.isNotEmpty(wThreeItem) && StringUtils.isEmpty(wFourItem)
					&& StringUtils.isEmpty(wFiveItem)) {
				wResult = StringUtils.Format("【{0}】+|;|+【{1}】+|;|+{2}", wOneItem, wTwoItem, wThreeItem);
			} else if (StringUtils.isNotEmpty(wOneItem) && StringUtils.isNotEmpty(wTwoItem)
					&& StringUtils.isNotEmpty(wThreeItem) && StringUtils.isNotEmpty(wFourItem)
					&& StringUtils.isEmpty(wFiveItem)) {
				wResult = StringUtils.Format("【{0}】+|;|+【{1}】+|;|+【{2}】+|;|+{3}", wOneItem, wTwoItem, wThreeItem,
						wFourItem);
			} else if (StringUtils.isNotEmpty(wOneItem) && StringUtils.isNotEmpty(wTwoItem)
					&& StringUtils.isNotEmpty(wThreeItem) && StringUtils.isNotEmpty(wFourItem)
					&& StringUtils.isNotEmpty(wFiveItem)) {
				wResult = StringUtils.Format("【{0}】+|;|+【{1}】+|;|+【{2}】+|;|+【{3}】+|;|+{4}", wOneItem, wTwoItem,
						wThreeItem, wFourItem, wFiveItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTStandardC>> IPT_Compare(BMSEmployee wLoginUser, int wARecordID, int wBRecordID) {
		ServiceResult<List<IPTStandardC>> wResult = new ServiceResult<List<IPTStandardC>>();
		wResult.Result = new ArrayList<IPTStandardC>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①根据A记录ID获取A的标准集合
			List<IPTStandard> wASList = new ArrayList<IPTStandard>();
			IMPResultRecord wRecordA = IMPResultRecordDAO.getInstance().SelectByID(wLoginUser, wARecordID, wErrorCode);
			if (wRecordA.PID != null && wRecordA.PID.size() > 0) {
				for (Integer wID : wRecordA.PID) {
					ServiceResult<IPTStandard> wItem = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, wID,
							wErrorCode);
					if (wItem.Result != null && wItem.Result.ID > 0) {
						wASList.add(wItem.Result);
					}
				}
			}
			wASList.forEach(p -> {
				p.LineName = QMSConstants.GetFMCLineName(p.LineID);
				p.PartName = QMSConstants.GetFPCPartName(p.PartID);
				p.PartPointName = QMSConstants.GetFPCStepName(p.PartPointID);
			});
			// ②根据B记录ID获取B的标准集合
			List<IPTStandard> wBSList = new ArrayList<IPTStandard>();
			IMPResultRecord wRecordB = IMPResultRecordDAO.getInstance().SelectByID(wLoginUser, wBRecordID, wErrorCode);
			if (wRecordB.PID != null && wRecordB.PID.size() > 0) {
				for (Integer wID : wRecordB.PID) {
					ServiceResult<IPTStandard> wItem = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, wID,
							wErrorCode);
					if (wItem.Result != null && wItem.Result.ID > 0) {
						wBSList.add(wItem.Result);
					}
				}
			}
			wBSList.forEach(p -> {
				p.LineName = QMSConstants.GetFMCLineName(p.LineID);
				p.PartName = QMSConstants.GetFPCPartName(p.PartID);
				p.PartPointName = QMSConstants.GetFPCStepName(p.PartPointID);
			});
			// ③返回结果
			wResult.Result = this.GetCompareResult(wLoginUser, wASList, wBSList, wRecordA, wRecordB);

//			// ③找到A和B相同的标准项列表，赋值对比结构
//			List<IPTStandardC> wSameCList = new ArrayList<IPTStandardC>();
//			List<IPTStandard> wSameList = wASList.stream()
//					.filter(p -> wBSList.stream().anyMatch(q -> q.PartPointID == p.PartPointID))
//					.collect(Collectors.toList());
//			for (IPTStandard wIPTStandard : wSameList) {
//				IPTStandardC wIPTStandardC = IPTStandardC.Clone(wIPTStandard);
//				wIPTStandardC.IsSame = 0;
//				wIPTStandardC.Remark = "";
//				wSameCList.add(wIPTStandardC);
//			}
//			// ④遍历此标准项列表，找到A的所有子项
//			for (IPTStandardC wIPTStandardC : wSameCList) {
//				IPTStandard wAS = wASList.stream().filter(p -> p.PartPointID == wIPTStandardC.PartPointID).findFirst()
//						.get();
//				List<IPTItem> wItemAList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wAS.ID, -1, -1,
//						wErrorCode);
//				this.AsignFullText(wItemAList);
//				// ⑤为A的所有子项编号
//				wItemAList = NewCreditReportUtil.handleNumber(wItemAList);
//				// ⑥找到B的所有子项
//				IPTStandard wBS = wBSList.stream().filter(p -> p.PartPointID == wIPTStandardC.PartPointID).findFirst()
//						.get();
//				List<IPTItem> wItemBList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wBS.ID, -1, -1,
//						wErrorCode);
//				this.AsignFullText(wItemBList);
//				// ⑦为B的所有子项编号
//				wItemBList = NewCreditReportUtil.handleNumber(wItemBList);
//				// ⑧遍历A的所有子项，找到B对应层级、Text相同的子项，赋值对比结构
//				for (IPTItem wIPTItem : wItemAList) {
//					if (wItemBList.stream()
//							.anyMatch(p -> getLevel(p) == getLevel(wIPTItem) && p.Text.equals(wIPTItem.Text))) {
//						IPTItem wBItem = wItemBList.stream()
//								.filter(p -> getLevel(p) == getLevel(wIPTItem) && p.Text.equals(wIPTItem.Text))
//								.findFirst().get();
//						ServiceResult<Boolean> wSResult = IPTItemC.SameAs(wIPTItem, wBItem);
//						IPTItemC wIPTItemC = IPTItemC.Clone(wIPTItem);
//						wIPTItemC.IsSame = wSResult.Result ? 0 : 1;
//						wIPTItemC.Remark = wSResult.FaultCode;
//						wIPTStandardC.IPTItemCList.add(wIPTItemC);
//					} else {
//						IPTItemC wIPTItemC = IPTItemC.Clone(wIPTItem);
//						wIPTItemC.IsSame = 2;
//						wIPTItemC.Remark = "项点不存在!";
//						wIPTStandardC.IPTItemCList.add(wIPTItemC);
//					}
//				}
//				// ⑨找到B所有在A中不存在相同层级且Text相同的子项，赋值对比结构
//				List<IPTItem> wACloneList = wItemAList;
//				List<IPTItem> wBNotsList = wItemBList.stream().filter(
//						p -> !wACloneList.stream().anyMatch(q -> getLevel(p) == getLevel(q) && p.Text.equals(q.Text)))
//						.collect(Collectors.toList());
//				for (IPTItem wIPTItem : wBNotsList) {
//					IPTItemC wIPTItemC = IPTItemC.Clone(wIPTItem);
//					wIPTItemC.IsSame = 3;
//					wIPTItemC.Remark = "项点不存在!";
//					wIPTStandardC.IPTItemCList.add(wIPTItemC);
//				}
//			}
//			// ⑩从A中找到B中不存在的标准项列表，赋值对比结构
//			List<IPTStandardC> wANSCList = new ArrayList<IPTStandardC>();
//			List<IPTStandard> wANSList = wASList.stream()
//					.filter(p -> !wBSList.stream().anyMatch(q -> q.PartPointID == p.PartPointID))
//					.collect(Collectors.toList());
//			for (IPTStandard wIPTStandard : wANSList) {
//				IPTStandardC wIPTStandardC = IPTStandardC.Clone(wIPTStandard);
//				wIPTStandardC.IsSame = 2;
//				wIPTStandardC.Remark = "标准不存在!";
//				wANSCList.add(wIPTStandardC);
//				// ①找到此标准项的子项列表，赋值对比结构
//				List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wIPTStandard.ID, -1, -1,
//						wErrorCode);
//				this.AsignFullText(wItemList);
//				for (IPTItem wIPTItem : wItemList) {
//					IPTItemC wIPTItemC = IPTItemC.Clone(wIPTItem);
//					wIPTItemC.IsSame = 2;
//					wIPTItemC.Remark = "标准不存在!";
//					wIPTStandardC.IPTItemCList.add(wIPTItemC);
//				}
//			}
//			// ②从B中找到A中不存在的标准项列表，赋值对比结构
//			List<IPTStandardC> wBNSCList = new ArrayList<IPTStandardC>();
//			List<IPTStandard> wBNSList = wBSList.stream()
//					.filter(p -> !wASList.stream().anyMatch(q -> q.PartPointID == p.PartPointID))
//					.collect(Collectors.toList());
//			for (IPTStandard wIPTStandard : wBNSList) {
//				IPTStandardC wIPTStandardC = IPTStandardC.Clone(wIPTStandard);
//				wIPTStandardC.IsSame = 3;
//				wIPTStandardC.Remark = "标准不存在!";
//				wBNSCList.add(wIPTStandardC);
//				// ①找到此标准项的子项列表，赋值对比结构
//				List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wIPTStandard.ID, -1, -1,
//						wErrorCode);
//				this.AsignFullText(wItemList);
//				for (IPTItem wIPTItem : wItemList) {
//					IPTItemC wIPTItemC = IPTItemC.Clone(wIPTItem);
//					wIPTItemC.IsSame = 3;
//					wIPTItemC.Remark = "标准不存在!";
//					wIPTStandardC.IPTItemCList.add(wIPTItemC);
//				}
//			}
//			// ①返回结果集
//			wResult.Result.addAll(wSameCList);
//			wResult.Result.addAll(wANSCList);
//			wResult.Result.addAll(wBNSCList);
			// 处理差异项数量
//			if (wResult.Result.size() > 0) {
//				for (IPTStandardC wIPTStandardC : wResult.Result) {
//					if (wIPTStandardC.IPTItemCList == null || wIPTStandardC.IPTItemCList.size() <= 0) {
//						continue;
//					}
//					// ①获取子项IsSame=2的数量
//					long wCount1 = wIPTStandardC.IPTItemCList.stream().filter(p -> p.IsSame == 2 || p.IsSame == 3)
//							.count();
//					// ②获取子项IsSame=1的数量
//					long wCount2 = wIPTStandardC.IPTItemCList.stream().filter(p -> p.IsSame == 1).count();
//					// ③赋值差异项数量
//					wIPTStandardC.Number = (int) (wCount1 + wCount2 / 2);
//				}
//			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 赋值全项点名称
	 * 
	 * @param wItemAList
	 */

	private void AsignFullText(List<IPTItem> wItemAList) {
		try {
			if (wItemAList == null || wItemAList.size() <= 0) {
				return;
			}

			for (IPTItem wIPTItem : wItemAList) {
				List<String> wFatherText = this.GetFatherText(wItemAList, wIPTItem);
				wIPTItem.FullText = this.GetDescription(wFatherText, wIPTItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取对比结果
	 * 
	 * @param wASList A标准集合
	 * @param wBSList B标准集合
	 * @return
	 */
	private List<IPTStandardC> GetCompareResult(BMSEmployee wLoginUser, List<IPTStandard> wASList,
			List<IPTStandard> wBSList, IMPResultRecord wRecordA, IMPResultRecord wRecordB) {
		List<IPTStandardC> wResult = new ArrayList<IPTStandardC>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①空值初始化
			if (wASList == null) {
				wASList = new ArrayList<IPTStandard>();
			}
			if (wBSList == null) {
				wBSList = new ArrayList<IPTStandard>();
			}
			// ②遍历A标准
			for (IPTStandard wIPTStandard : wASList) {
				IPTStandardC wIPTStandardC = IPTStandardC.Clone(wIPTStandard);
				// ①获取A标准的所有子项
				List<IPTItem> wItemAList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wIPTStandard.ID, -1, -1,
						wErrorCode);
				this.AsignFullText(wItemAList);
				// ②为子项编号
				wItemAList = NewCreditReportUtil.handleNumber(wItemAList);
				// ③查找B中是否有跟A相同条件的标准
				if (wBSList.stream()
						.anyMatch(p -> p.ProductID == wIPTStandard.ProductID && p.LineID == wIPTStandard.LineID
								&& p.PartID == wIPTStandard.PartID && p.PartPointID == wIPTStandard.PartPointID)) {
					// ⑥若有，找到相同条件的B标准
					IPTStandard wBS = wBSList.stream()
							.filter(p -> p.ProductID == wIPTStandard.ProductID && p.LineID == wIPTStandard.LineID
									&& p.PartID == wIPTStandard.PartID && p.PartPointID == wIPTStandard.PartPointID)
							.findFirst().get();
					// ⑦获取B标准的子项
					List<IPTItem> wItemBList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wBS.ID, -1, -1,
							wErrorCode);
					this.AsignFullText(wItemBList);
					// ⑧为B标准的子项编号
					wItemBList = NewCreditReportUtil.handleNumber(wItemBList);
					// ⑨遍历A中的子项，查找同级B子项列表中是否有相同的项点
					List<IPTItemC> wSonList = new ArrayList<IPTItemC>();
					for (IPTItem wIPTItem : wItemAList) {
						IPTItemC wIPTItemC = IPTItemC.Clone(wIPTItem);
						if (wItemBList.stream()
								.anyMatch(p -> getLevel(p) == getLevel(wIPTItem) && p.Text.equals(wIPTItem.Text))) {
							// ⑩若有，则找到此项点，进行对比，赋值对比结构
							IPTItem wBItem = wItemBList.stream()
									.filter(p -> getLevel(p) == getLevel(wIPTItem) && p.Text.equals(wIPTItem.Text))
									.findFirst().get();
							ServiceResult<Boolean> wSR = IPTItemC.SameAs(wIPTItem, wBItem);
							if (wSR.Result) {
								wIPTItemC.IsSame = IMPSameType.Same.getValue();
								wIPTItemC.Remark = "";
								wIPTItemC.CodeA = wRecordA.Code;
								wIPTItemC.CodeB = wRecordB.Code;
								wSonList.add(wIPTItemC);
							} else {
								wIPTItemC.IsSame = IMPSameType.Update.getValue();
								wIPTItemC.Remark = wSR.FaultCode;
								wIPTItemC.CodeA = wRecordA.Code;
								wIPTItemC.CodeB = wRecordB.Code;
								wSonList.add(wIPTItemC);
							}
						} else {
							// ①若没有，说明此项点为删除的项点，赋值对比结构
							wIPTItemC.IsSame = IMPSameType.Delete.getValue();
							wIPTItemC.Remark = wIPTItem.Text;
							wIPTItemC.CodeA = wRecordA.Code;
							wIPTItemC.CodeB = "";
							wSonList.add(wIPTItemC);
						}
					}
					// ②从B项点中找到A同级项点不存在相同项点名称的项点
					List<IPTItem> wACloneList = wItemAList;
					List<IPTItem> wItemList = wItemBList.stream()
							.filter(p -> !wACloneList.stream()
									.anyMatch(q -> getLevel(q) == getLevel(p) && q.Text.equals(p.Text)))
							.collect(Collectors.toList());
					// ③说明此项点为新增的项点，赋值对比结构
					for (IPTItem wIPTItem : wItemList) {
						IPTItemC wIPTItemC = IPTItemC.Clone(wIPTItem);
						wIPTItemC.IsSame = IMPSameType.Add.getValue();
						wIPTItemC.CodeA = "";
						wIPTItemC.CodeB = wRecordB.Code;
						wIPTItemC.Remark = wIPTItem.Text;
						wSonList.add(wIPTItemC);
					}
					// ④赋值标准的对比结构
					if (wSonList.stream().anyMatch(p -> p.IsSame != IMPSameType.Same.getValue())) {
						wIPTStandardC.IsSame = IMPSameType.SonSpe.getValue();
						wIPTStandardC.Remark = "子项差异";
						wIPTStandardC.CodeA = wRecordA.Code;
						wIPTStandardC.CodeB = wRecordB.Code;
						wIPTStandardC.IPTItemCList = wSonList;
						wResult.add(wIPTStandardC);
					} else {
						wIPTStandardC.IsSame = IMPSameType.Same.getValue();
						wIPTStandardC.Remark = "";
						wIPTStandardC.CodeA = wRecordA.Code;
						wIPTStandardC.CodeB = wRecordB.Code;
						wIPTStandardC.IPTItemCList = wSonList;
						wResult.add(wIPTStandardC);
					}
				} else {
					// ④若没有，说明A标准为删除的，赋值对比结构
					wIPTStandardC.IsSame = IMPSameType.Delete.getValue();
					wIPTStandardC.CodeA = wRecordA.Code;
					wIPTStandardC.CodeB = "";
					wIPTStandardC.Remark = StringUtils.Format("【{0}】-【{1}】-【{2}】-【{3}】",
							QMSConstants.GetFPCProductName(wIPTStandard.ProductID),
							QMSConstants.GetFMCLineName(wIPTStandard.LineID),
							QMSConstants.GetFPCPart(wIPTStandard.PartID),
							QMSConstants.GetFPCStepName(wIPTStandard.PartPointID));
					// ⑤找到A标准下的所有子项，赋值对比结构
					List<IPTItemC> wSonList = new ArrayList<IPTItemC>();
					for (IPTItem wIPTItem : wItemAList) {
						IPTItemC wIPTItemC = IPTItemC.Clone(wIPTItem);
						wIPTItemC.IsSame = IMPSameType.Delete.getValue();
						wIPTItemC.CodeA = wRecordA.Code;
						wIPTItemC.CodeB = "";
						wIPTItemC.Remark = wIPTStandardC.Remark;
						wSonList.add(wIPTItemC);
					}
					wIPTStandardC.IPTItemCList = wSonList;
					wResult.add(wIPTStandardC);
				}
			}
			// ④从B中找到A中不相同的标准，赋值对比结构
			List<IPTStandard> wACloneList = wASList;
			List<IPTStandard> wList = wBSList
					.stream().filter(
							p -> !wACloneList.stream()
									.anyMatch(q -> q.ProductID == p.ProductID && q.LineID == p.LineID
											&& q.PartID == p.PartID && q.PartPointID == p.PartPointID))
					.collect(Collectors.toList());
			// ⑤遍历这些标准
			for (IPTStandard wIPTStandard : wList) {
				IPTStandardC wIPTStandardC = IPTStandardC.Clone(wIPTStandard);
				wIPTStandardC.IsSame = IMPSameType.Add.getValue();
				wIPTStandardC.Remark = StringUtils.Format("【{0}】-【{1}】-【{2}】-【{3}】",
						QMSConstants.GetFPCProductName(wIPTStandard.ProductID),
						QMSConstants.GetFMCLineName(wIPTStandard.LineID), QMSConstants.GetFPCPart(wIPTStandard.PartID),
						QMSConstants.GetFPCStepName(wIPTStandard.PartPointID));
				wIPTStandardC.CodeA = "";
				wIPTStandardC.CodeB = wRecordB.Code;
				// ⑥获取每个标准的子项，赋值对比结构
				List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wIPTStandard.ID, -1, -1,
						wErrorCode);
				this.AsignFullText(wItemList);
				// ②为子项编号
				wItemList = NewCreditReportUtil.handleNumber(wItemList);
				List<IPTItemC> wSonList = new ArrayList<IPTItemC>();
				for (IPTItem wIPTItem : wItemList) {
					IPTItemC wIPTItemC = IPTItemC.Clone(wIPTItem);
					wIPTItemC.IsSame = IMPSameType.Add.getValue();
					wIPTItemC.Remark = wIPTStandardC.Remark;
					wIPTItemC.CodeA = "";
					wIPTItemC.CodeB = wRecordB.Code;
					wSonList.add(wIPTItemC);
				}
				wIPTStandardC.IPTItemCList = wSonList;
				wResult.add(wIPTStandardC);
			}

			// 处理差异数
			if (wResult.size() > 0) {
				for (IPTStandardC wIPTStandardC : wResult) {
					if (wIPTStandardC.IPTItemCList == null || wIPTStandardC.IPTItemCList.size() <= 0) {
						continue;
					}
					wIPTStandardC.Number = (int) wIPTStandardC.IPTItemCList.stream()
							.filter(p -> p.IsSame != IMPSameType.Same.getValue()).count();
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取此项的层级
	 * 
	 * @param wItem
	 * @return
	 */
	private int getLevel(IPTItem wItem) {
		int wResult = 0;
		try {
			if (wItem == null || StringUtils.isEmpty(wItem.Code)) {
				return wResult;
			}

			// 获取字符串的长度
			int wStrLen = wItem.Code.length();

			// 把需要查找的元素都替换为空
			String wAfterStr = wItem.Code.replaceAll("\\.", "");

			// 获取替换后的字符串的长度
			int wAfterLen = wAfterStr.length();

			// 要被查找的元素的个数=原长度-替换后的长度
			int wEndLen = wStrLen - wAfterLen;

			wResult = wEndLen + 1;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTItem>> QueryItemListByStandard(BMSEmployee wLoginUser, int wStandardID) {
		ServiceResult<List<IPTItem>> wResult = new ServiceResult<List<IPTItem>>();
		wResult.Result = new ArrayList<IPTItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {

			wResult.Result = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wStandardID, -1, -1, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> IPT_ImportMaterial(BMSEmployee wLoginUser, ExcelData wExcelData,
			String wOriginalFileName) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wExcelData == null || wExcelData.sheetData == null || wExcelData.sheetData.size() <= 0) {
				wResult.FaultCode += "提示：物料数据解析失败!";

				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wOriginalFileName, wResult.FaultCode,
						wExcelData.sheetData.get(0).lineData.size() - 1, IMPType.Material.getValue());

				return wResult;
			}

			ExcelSheetData wSheetData = wExcelData.sheetData.get(0);
			if (wSheetData.lineData == null || wSheetData.lineData.size() <= 0) {
				wResult.FaultCode += "提示：物料数据解析失败!";

				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wOriginalFileName, wResult.FaultCode,
						wExcelData.sheetData.get(0).lineData.size() - 1, IMPType.Material.getValue());

				return wResult;
			}

			// 检查物料数据正确性
			String wMsg = this.CheckMaterialData(wLoginUser, wOriginalFileName, wSheetData);
			if (StringUtils.isNotEmpty(wMsg)) {
				wResult.FaultCode = wMsg;
				return wResult;
			}

			// 解析行数据
			String wMaterialNo = "";
			String wMaterialName = "";
			String wName = "";
			String wSize = "";
			String wRemark = "";
			String wGroup = "";
			String wMWeight = "";
			String wJWeight = "";
			// 单位列表
			List<CFGUnit> wCFGUnitList = CoreServiceImpl.getInstance().CFG_QueryUnitList(BaseDAO.SysAdmin)
					.List(CFGUnit.class);
			// 添加日志
			IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
					IMPType.Material.getValue(), "", null, IMPResult.Doding.getValue(), wOriginalFileName,
					wSheetData.lineData.size() - 1, 0);
			wIMPResultRecord.ID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
			// 开始导入
			List<MSSMaterial> wTempList = new ArrayList<MSSMaterial>();
			for (int i = 1; i < wSheetData.lineData.size(); i++) {
				if (wSheetData.lineData.get(i).colData == null || wSheetData.lineData.get(i).colData.size() <= 0) {
					continue;
				}
				if (wSheetData.lineData.get(0).colData.stream().allMatch(p -> StringUtils.isEmpty(p))) {
					continue;
				}

				wMaterialNo = wSheetData.lineData.get(i).colData.size() > 0 ? wSheetData.lineData.get(i).colData.get(0)
						: "";
				wMaterialName = wSheetData.lineData.get(i).colData.size() > 1
						? wSheetData.lineData.get(i).colData.get(1)
						: "";
				wName = wSheetData.lineData.get(i).colData.size() > 1
						? wSheetData.lineData.get(i).colData.get(1).contains("\\")
								? wSheetData.lineData.get(i).colData.get(1).split("\\\\")[0]
								: wSheetData.lineData.get(i).colData.get(1)
						: "";
				// 大小、量纲
				wSize = wSheetData.lineData.get(i).colData.size() > 3 ? wSheetData.lineData.get(i).colData.get(3) : "";
				// 单位
				String wUnit = wSheetData.lineData.get(i).colData.size() > 4 ? wSheetData.lineData.get(i).colData.get(4)
						: "";
				// 备注
				wRemark = wSheetData.lineData.get(i).colData.size() > 7 ? wSheetData.lineData.get(i).colData.get(7)
						: "";
				// 物料组
				wGroup = wSheetData.lineData.get(i).colData.size() > 13 ? wSheetData.lineData.get(i).colData.get(13)
						: "";
				// 毛重
				wMWeight = wSheetData.lineData.get(i).colData.size() > 16 ? wSheetData.lineData.get(i).colData.get(16)
						: "";
				// 净重
				wJWeight = wSheetData.lineData.get(i).colData.size() > 22 ? wSheetData.lineData.get(i).colData.get(22)
						: "";

				// 添加单位
				int wUnitID = 0;
				if (wCFGUnitList.stream().anyMatch(p -> p.Name.equals(wUnit))) {
					wUnitID = wCFGUnitList.stream().filter(p -> p.Name.equals(wUnit)).findFirst().get().ID;
				} else {
					CFGUnit wCFGUnit = new CFGUnit();
					wCFGUnit.Name = wUnit;
					wCFGUnit.OperatorID = wLoginUser.ID;
					wCFGUnit.EditTime = Calendar.getInstance();
					wCFGUnit.Active = 1;
					wCFGUnit = CoreServiceImpl.getInstance().CFG_SaveUnit(wLoginUser, wCFGUnit).Info(CFGUnit.class);
					if (wCFGUnit != null && wCFGUnit.ID > 0) {
						wUnitID = wCFGUnit.ID;
						wCFGUnitList.add(wCFGUnit);
					}
				}

				// 新增物料
				List<MSSMaterial> wMaterialList = WMSServiceImpl.getInstance()
						.MSS_QueryMaterialList(wLoginUser, wMaterialNo).List(MSSMaterial.class);
				MSSMaterial wMSSMaterial = null;
				if (wMaterialList == null || wMaterialList.size() <= 0) {
					wMSSMaterial = new MSSMaterial();
					this.SetValue(wLoginUser, wMaterialNo, wMaterialName, wName, wSize, wRemark, wGroup, wMWeight,
							wJWeight, wUnitID, wMSSMaterial);
				} else {
					wMSSMaterial = wMaterialList.get(0);
					this.SetValue(wLoginUser, wMaterialNo, wMaterialName, wName, wSize, wRemark, wGroup, wMWeight,
							wJWeight, wUnitID, wMSSMaterial);
				}

				wTempList.add(wMSSMaterial);
				if (i % 100 == 0 || i == wSheetData.lineData.size() - 1) {
					WMSServiceImpl.getInstance().MSS_SaveMaterialList(BaseDAO.SysAdmin, wTempList);
					wTempList = new ArrayList<MSSMaterial>();
					// 更新日志
					wIMPResultRecord.Progress = i;
					IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
				}
			}

			// 导入成功，更新日志
			wIMPResultRecord.Result = IMPResult.Success.getValue();
			wIMPResultRecord.Progress = wSheetData.lineData.size() - 1;
			IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 检查物料数据
	 * 
	 * @param wLoginUser
	 * @param wOriginalFileName
	 * @param wSheetData
	 * @return
	 */
	private String CheckMaterialData(BMSEmployee wLoginUser, String wOriginalFileName, ExcelSheetData wSheetData) {
		String wResult = "";
		try {
			// 记录所有错误
			RecordAllMaterialError(wLoginUser, wOriginalFileName, wSheetData);

			// 解析行数据
			String wMaterialName = "";
			for (int i = 1; i < wSheetData.lineData.size(); i++) {
				if (wSheetData.lineData.get(i).colData == null || wSheetData.lineData.get(i).colData.size() <= 0) {
					continue;
				}
				if (wSheetData.lineData.get(0).colData.stream().allMatch(p -> StringUtils.isEmpty(p))) {
					continue;
				}

				String wMaterialNo = wSheetData.lineData.get(i).colData.size() > 0
						? wSheetData.lineData.get(i).colData.get(0)
						: "";
				wMaterialName = wSheetData.lineData.get(i).colData.size() > 1
						? wSheetData.lineData.get(i).colData.get(1)
						: "";
				String wUnit = wSheetData.lineData.get(i).colData.size() > 4 ? wSheetData.lineData.get(i).colData.get(4)
						: "";

				if (StringUtils.isEmpty(wMaterialNo)) {
					wResult = StringUtils.Format("提示：第{0}行数据不合法，物料号不能为空!", i + 1);
					return wResult;
				}

				if (StringUtils.isEmpty(wMaterialName)) {
					wResult = StringUtils.Format("提示：第{0}行数据不合法，物料描述不能为空!", i + 1);
					return wResult;
				}

				if (StringUtils.isEmpty(wUnit)) {
					wResult = StringUtils.Format("提示：第{0}行数据不合法，计量单位不能为空!", i + 1);
					return wResult;
				}

				if (StringUtils.isNotEmpty(wMaterialNo)) {
					if (wSheetData.lineData.stream().filter(p -> p.colData.get(0).equals(wMaterialNo)).count() >= 2) {
						wResult = StringUtils.Format("提示：第{0}行数据不合法，物料号【{1}】重复!", i + 1, wMaterialNo);
						return wResult;
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 记录物料所有错误
	 * 
	 * @param wLoginUser
	 * @param wOriginalFileName
	 * @param wSheetData
	 */
	private void RecordAllMaterialError(BMSEmployee wLoginUser, String wOriginalFileName, ExcelSheetData wSheetData) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<IMPErrorRecord> wRecordList = new ArrayList<IMPErrorRecord>();
			// 解析行数据
			String wMaterialName = "";
			for (int i = 1; i < wSheetData.lineData.size(); i++) {
				if (wSheetData.lineData.get(i).colData == null || wSheetData.lineData.get(i).colData.size() <= 0) {
					continue;
				}
				if (wSheetData.lineData.get(0).colData.stream().allMatch(p -> StringUtils.isEmpty(p))) {
					continue;
				}

				String wMaterialNo = wSheetData.lineData.get(i).colData.size() > 0
						? wSheetData.lineData.get(i).colData.get(0)
						: "";
				wMaterialName = wSheetData.lineData.get(i).colData.size() > 1
						? wSheetData.lineData.get(i).colData.get(1)
						: "";
				String wUnit = wSheetData.lineData.get(i).colData.size() > 4 ? wSheetData.lineData.get(i).colData.get(4)
						: "";

				if (StringUtils.isEmpty(wMaterialNo)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，物料号不能为空!", i + 1)));
				}

				if (StringUtils.isEmpty(wMaterialName)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，物料描述不能为空!", i + 1)));
				}

				if (StringUtils.isEmpty(wUnit)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，计量单位不能为空!", i + 1)));
				}

				if (StringUtils.isNotEmpty(wMaterialNo)) {
					if (wSheetData.lineData.stream().filter(p -> p.colData.get(0).equals(wMaterialNo)).count() >= 2) {
						wRecordList.add(new IMPErrorRecord(0, 0, 0,
								StringUtils.Format("提示：第{0}行数据不合法，物料号【{1}】重复!", i + 1, wMaterialNo)));
					}
				}
			}

			if (wRecordList.size() > 0) {
				IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
						IMPType.Material.getValue(), "", null, IMPResult.Failed.getValue(), wOriginalFileName,
						wSheetData.lineData.size() - 1, wRecordList.size());
				int wNewID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

				wRecordList.forEach(p -> p.ParentID = wNewID);
				for (IMPErrorRecord wIMPErrorRecord : wRecordList) {
					IMPErrorRecordDAO.getInstance().Update(wLoginUser, wIMPErrorRecord, wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 物料属性赋值
	 */
	private void SetValue(BMSEmployee wLoginUser, String wMaterialNo, String wMaterialName, String wName, String wSize,
			String wRemark, String wGroup, String wMWeight, String wJWeight, int wUnitID, MSSMaterial wMSSMaterial) {
		try {
			wMSSMaterial.Name = wName;
			wMSSMaterial.AuditTime = Calendar.getInstance();
			wMSSMaterial.Author = wLoginUser.Name;
			wMSSMaterial.Auditor = wLoginUser.Name;
			wMSSMaterial.MaterialNo = wMaterialNo;
			wMSSMaterial.MaterialName = wMaterialName;
			wMSSMaterial.CYUnitID = wUnitID;
			wMSSMaterial.Groes = wSize;
			wMSSMaterial.Remark = wRemark;
			wMSSMaterial.GrossWeight = StringUtils.parseDouble(wMWeight);
			wMSSMaterial.NetWeight = StringUtils.parseDouble(wJWeight);
			wMSSMaterial.MaterialGroup = wGroup;
			wMSSMaterial.MaterialType = "";
			wMSSMaterial.EditTime = Calendar.getInstance();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<IMPResultRecord> IPT_QueryMaterialProgress(BMSEmployee wLoginUser, int wImportType,
			Calendar wTime) {
		ServiceResult<IMPResultRecord> wResult = new ServiceResult<IMPResultRecord>();
		wResult.Result = new IMPResultRecord();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<IMPResultRecord> wList = IMPResultRecordDAO.getInstance().SelectList(wLoginUser, -1, wLoginUser.ID,
					wImportType, null, null, wErrorCode);
			if (wList != null && wList.size() > 0) {
				wResult.Result = wList.stream().max(Comparator.comparing(IMPResultRecord::getOperateTime)).get();
				if (wResult.Result.OperateTime.compareTo(wTime) <= 0) {
					wResult.Result = new IMPResultRecord();
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportProduceProcessZip(BMSEmployee wLoginUser, int wOrderID, int wPartID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}.xls",
					new Object[] { wOrder.PartNo.replace("#", "-"), wCurTime });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			// 创建压缩文件目录
			String wFileDir = StringUtils.Format("{0}{1}-{2}-机车生产过程检验报告/", wDirePath, wCurTime,
					wOrder.PartNo.replace("#", "-"));
			File wFile = new File(wFileDir);
			if (!wFile.exists()) {
				wFile.mkdirs();
			}

			// 创建图片文件夹
			String wPicDir = StringUtils.Format("{0}picture/", wFileDir);
			File wPicDirFile = new File(wPicDir);
			if (!wPicDirFile.exists()) {
				wPicDirFile.mkdirs();
			}
			// 创建视频文件夹
			String wVideoDir = StringUtils.Format("{0}video/", wFileDir);
			File wVideoDirFile = new File(wVideoDir);
			if (!wVideoDirFile.exists()) {
				wVideoDirFile.mkdirs();
			}

			// 生成Excel文件
			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wFileDir, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			Map<String, List<IPTCheckRecord>> wRecordMap = this.IPT_QueryRecordMap(wLoginUser, wOrder, false, -1,
					wPartID);
			if (wRecordMap == null || wRecordMap.size() <= 0) {
				wResult.FaultCode += "提示：未查询到该订单的生产过程检记录!";
				return wResult;
			}

			// 获取图片路径集合
			List<String> wPicNoList = new ArrayList<String>();
			List<String> wPicList = this.GetPicList(wRecordMap, wPicNoList);

			// 获取视频路径集合
//			List<String> wVideoList = this.GetVideoList(wRecordMap);
			// 遍历下载图片
//			int wIndex = 1;
			for (String wPic : wPicList) {
				String wSuffix = this.GetSuffix(wPic);
//				String wDesPath = StringUtils.Format("{0}{1}{3}.{2}", wPicDir, String.format("%02d", wIndex), wSuffix,
//						wPicNoList.get(wIndex - 1));

				String wPicName = MESFileUtils.GetFileName(wPic);
				String wDesPath = StringUtils.Format("{0}{1}.{2}", wPicDir, wPicName, wSuffix);

				MESFileUtils.DownloadPicture(wPic, wDesPath);
//				wIndex++;
			}
			// 遍历下载视频
//			wIndex = 1;
//			for (String wVideo : wVideoList) {
//				String wSuffix = this.GetSuffix(wVideo);
//				String wDesPath = StringUtils.Format("{0}{1}.{2}", wVideoDir, String.format("%02d", wIndex), wSuffix);
//				MESFileUtils.DownloadPicture(wVideo, wDesPath);
//				wIndex++;
//			}

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			ExcelUtil.SCGC_WriteReport(wRecordMap, wFileOutputStream,
					StringUtils.Format("{0}-{1}", wOrder.PartNo, wOrder.Customer), false, "生产过程检验记录");

			// 导出台车部件信息
//			List<MSSPartItem> wPartItemList = CoreServiceImpl.getInstance()
//					.MSS_PartItemAll(wLoginUser, wOrderID, "", -1, -1, "").List(MSSPartItem.class);
//			wFilePath = StringUtils.Format("{0}{1}", new Object[] { wFileDir, "部件信息.xls" });
//			wNewFile = new File(wFilePath);
//			wNewFile.createNewFile();
//			FileOutputStream wStream = new FileOutputStream(wNewFile);
//			ExcelUtil.SCGC_WritePartItemList(wPartItemList, wStream, wOrder.PartNo);

			// 压缩文件夹
			ZipUtils.Zip(StringUtils.Format("{0}{1}-{2}-{3}-机车生产过程检验报告.zip", wDirePath, wOrder.Customer,
					wOrder.PartNo.replace("#", "-"), wCurTime), wFileDir);

			// 返回压缩文件的下载路径
			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), StringUtils.Format(
							"{0}-{1}-{2}-机车生产过程检验报告.zip", wOrder.Customer, wOrder.PartNo.replace("#", "-"), wCurTime));

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取文件后缀名
	 */
	private String GetSuffix(String wPath) {
		String wResult = "";
		try {
			wResult = wPath.substring(wPath.lastIndexOf('.') + 1);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取视频路径集合
	 */
	private List<String> GetVideoList(Map<String, List<IPTCheckRecord>> wRecordMap) {
		List<String> wResult = new ArrayList<String>();
		try {
			if (wRecordMap.values() == null || wRecordMap.values().size() <= 0) {
				return wResult;
			}

			String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

			for (List<IPTCheckRecord> wList : wRecordMap.values()) {
				for (IPTCheckRecord wIPTCheckRecord : wList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Video)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Video.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取图片路径集合
	 */
	private List<String> GetPicList(Map<String, List<IPTCheckRecord>> wRecordMap, List<String> wPicNoList) {
		List<String> wResult = new ArrayList<String>();
		try {
			if (wRecordMap.values() == null || wRecordMap.values().size() <= 0) {
				return wResult;
			}

			String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

			for (List<IPTCheckRecord> wList : wRecordMap.values()) {
				for (IPTCheckRecord wIPTCheckRecord : wList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Picture)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Picture.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

//							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));

							String wPicItem = StringUtils.Format("{0}{1}", wUri, wStr);
							if (!wResult.stream().anyMatch(p -> p.equals(wPicItem)))
								wResult.add(wPicItem);

							wPicNoList.add(wIPTCheckRecord.PicNo);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportOutCheckZip(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}.xls",
					new Object[] { wOrder.PartNo.replace("#", "-"), wCurTime });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			// 创建压缩文件目录
			String wFileDir = StringUtils.Format("{0}{1}-{2}-机车质量出厂检报告/", wDirePath, wCurTime,
					wOrder.PartNo.replace("#", "-"));
			File wFile = new File(wFileDir);
			if (!wFile.exists()) {
				wFile.mkdirs();
			}

			// 创建图片文件夹
			String wPicDir = StringUtils.Format("{0}picture/", wFileDir);
			File wPicDirFile = new File(wPicDir);
			if (!wPicDirFile.exists()) {
				wPicDirFile.mkdirs();
			}
			// 创建视频文件夹
			String wVideoDir = StringUtils.Format("{0}video/", wFileDir);
			File wVideoDirFile = new File(wVideoDir);
			if (!wVideoDirFile.exists()) {
				wVideoDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wFileDir, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			Map<String, List<IPTCheckRecord>> wRecordMap = this.IPT_QueryRecordMap(wLoginUser, wOrder, false,
					SFCTaskType.OutPlant.getValue(), -1);
			if (wRecordMap == null || wRecordMap.size() <= 0) {
				wResult.FaultCode += "提示：未查询到该订单的出厂检记录!";
				return wResult;
			}

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			ExcelUtil.SCGC_WriteReport(wRecordMap, wFileOutputStream,
					StringUtils.Format("{0}-{1}", wOrder.PartNo, wOrder.Customer), true, "质量出厂检记录");

			// 获取图片路径集合
			List<String> wPicNoList = new ArrayList<String>();
			List<String> wPicList = this.GetPicList(wRecordMap, wPicNoList);
			// 获取视频路径集合
			List<String> wVideoList = this.GetVideoList(wRecordMap);
			// 遍历下载图片
			int wIndex = 1;
			for (String wPic : wPicList) {
				String wSuffix = this.GetSuffix(wPic);
				String wDesPath = StringUtils.Format("{0}{1}{3}.{2}", wPicDir, String.format("%02d", wIndex), wSuffix,
						wPicNoList.get(wIndex - 1));
				MESFileUtils.DownloadPicture(wPic, wDesPath);
				wIndex++;
			}
			// 遍历下载视频
			wIndex = 1;
			for (String wVideo : wVideoList) {
				String wSuffix = this.GetSuffix(wVideo);
				String wDesPath = StringUtils.Format("{0}{1}.{2}", wVideoDir, String.format("%02d", wIndex), wSuffix);
				MESFileUtils.DownloadPicture(wVideo, wDesPath);
				wIndex++;
			}

			// 压缩文件夹
			ZipUtils.Zip(StringUtils.Format("{0}{1}-{2}-{3}-机车质量出厂检报告.zip", wDirePath, wOrder.Customer,
					wOrder.PartNo.replace("#", "-"), wCurTime), wFileDir);

			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), StringUtils.Format(
							"{0}-{1}-{2}-机车质量出厂检报告.zip", wOrder.Customer, wOrder.PartNo.replace("#", "-"), wCurTime));

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportFinalCheckZip(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}.xls",
					new Object[] { wOrder.PartNo.replace("#", "-"), wCurTime });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			// 创建压缩文件目录
			String wFileDir = StringUtils.Format("{0}{1}-{2}-机车质量终检报告/", wDirePath, wCurTime,
					wOrder.PartNo.replace("#", "-"));
			File wFile = new File(wFileDir);
			if (!wFile.exists()) {
				wFile.mkdirs();
			}

			// 创建图片文件夹
			String wPicDir = StringUtils.Format("{0}picture/", wFileDir);
			File wPicDirFile = new File(wPicDir);
			if (!wPicDirFile.exists()) {
				wPicDirFile.mkdirs();
			}
			// 创建视频文件夹
			String wVideoDir = StringUtils.Format("{0}video/", wFileDir);
			File wVideoDirFile = new File(wVideoDir);
			if (!wVideoDirFile.exists()) {
				wVideoDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wFileDir, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			Map<String, List<IPTCheckRecord>> wRecordMap = this.IPT_QueryRecordMap(wLoginUser, wOrder, false,
					SFCTaskType.Final.getValue(), -1);
			if (wRecordMap == null || wRecordMap.size() <= 0) {
				wResult.FaultCode += "提示：未查询到该订单的终检记录!";
				return wResult;
			}
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);

			ExcelUtil.SCGC_WriteReport(wRecordMap, wFileOutputStream,
					StringUtils.Format("{0}-{1}", wOrder.PartNo, wOrder.Customer), true, "质量终检记录");

			// 获取图片路径集合
			List<String> wPicNoList = new ArrayList<String>();
			List<String> wPicList = this.GetPicList(wRecordMap, wPicNoList);
			// 获取视频路径集合
			List<String> wVideoList = this.GetVideoList(wRecordMap);
			// 遍历下载图片
			int wIndex = 1;
			for (String wPic : wPicList) {
				String wSuffix = this.GetSuffix(wPic);
				String wDesPath = StringUtils.Format("{0}{1}{3}.{2}", wPicDir, String.format("%02d", wIndex), wSuffix,
						wPicNoList.get(wIndex - 1));
				MESFileUtils.DownloadPicture(wPic, wDesPath);
				wIndex++;
			}
			// 遍历下载视频
			wIndex = 1;
			for (String wVideo : wVideoList) {
				String wSuffix = this.GetSuffix(wVideo);
				String wDesPath = StringUtils.Format("{0}{1}.{2}", wVideoDir, String.format("%02d", wIndex), wSuffix);
				MESFileUtils.DownloadPicture(wVideo, wDesPath);
				wIndex++;
			}

			// 压缩文件夹
			ZipUtils.Zip(StringUtils.Format("{0}{1}-{2}-{3}-机车质量终检报告.zip", wDirePath, wOrder.Customer,
					wOrder.PartNo.replace("#", "-"), wCurTime), wFileDir);

			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), StringUtils.Format(
							"{0}-{1}-{2}-机车质量终检报告.zip", wOrder.Customer, wOrder.PartNo.replace("#", "-"), wCurTime));

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportQualityProcessZip(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}.xls",
					new Object[] { wOrder.PartNo.replace("#", "-"), wCurTime });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			// 创建压缩文件目录
			String wFileDir = StringUtils.Format("{0}{1}-{2}-机车质量过程检验报告/", wDirePath, wCurTime,
					wOrder.PartNo.replace("#", "-"));
			File wFile = new File(wFileDir);
			if (!wFile.exists()) {
				wFile.mkdirs();
			}

			// 创建图片文件夹
			String wPicDir = StringUtils.Format("{0}picture/", wFileDir);
			File wPicDirFile = new File(wPicDir);
			if (!wPicDirFile.exists()) {
				wPicDirFile.mkdirs();
			}
			// 创建视频文件夹
			String wVideoDir = StringUtils.Format("{0}video/", wFileDir);
			File wVideoDirFile = new File(wVideoDir);
			if (!wVideoDirFile.exists()) {
				wVideoDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wFileDir, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			Map<String, List<IPTCheckRecord>> wRecordMap = this.IPT_QueryRecordMap(wLoginUser, wOrder, true, -1, -1);
			if (wRecordMap == null || wRecordMap.size() <= 0) {
				wResult.FaultCode += "提示：未查询到该订单的质量过程检记录!";
				return wResult;
			}

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			ExcelUtil.SCGC_WriteReport(wRecordMap, wFileOutputStream,
					StringUtils.Format("{0}-{1}", wOrder.PartNo, wOrder.Customer), false, "质量过程检验记录");

			// 获取图片路径集合
			List<String> wPicNoList = new ArrayList<String>();
			List<String> wPicList = this.GetPicList(wRecordMap, wPicNoList);
			// 获取视频路径集合
			List<String> wVideoList = this.GetVideoList(wRecordMap);
			// 遍历下载图片
			int wIndex = 1;
			for (String wPic : wPicList) {
				String wSuffix = this.GetSuffix(wPic);
				String wDesPath = StringUtils.Format("{0}{1}{3}.{2}", wPicDir, String.format("%02d", wIndex), wSuffix,
						wPicNoList.get(wIndex - 1));
				MESFileUtils.DownloadPicture(wPic, wDesPath);
				wIndex++;
			}
			// 遍历下载视频
			wIndex = 1;
			for (String wVideo : wVideoList) {
				String wSuffix = this.GetSuffix(wVideo);
				String wDesPath = StringUtils.Format("{0}{1}.{2}", wVideoDir, String.format("%02d", wIndex), wSuffix);
				MESFileUtils.DownloadPicture(wVideo, wDesPath);
				wIndex++;
			}

			// 压缩文件夹
			ZipUtils.Zip(StringUtils.Format("{0}{1}-{2}-{3}-机车质量过程检验报告.zip", wDirePath, wOrder.Customer,
					wOrder.PartNo.replace("#", "-"), wCurTime), wFileDir);

			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), StringUtils.Format(
							"{0}-{1}-{2}-机车质量过程检验报告.zip", wOrder.Customer, wOrder.PartNo.replace("#", "-"), wCurTime));

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportPreCheckReportByOrderZip(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<String> wResult = new ServiceResult<>();
		OutResult<Integer> wErrorCode = new OutResult<>(0);
		try {
			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}.xls",
					new Object[] { wOrder.PartNo.replace("#", "-"), wCurTime });
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			// 创建压缩文件目录
			String wFileDir = StringUtils.Format("{0}{1}-{2}-机车预检报告/", wDirePath, wCurTime,
					wOrder.PartNo.replace("#", "-"));
			File wFile = new File(wFileDir);
			if (!wFile.exists()) {
				wFile.mkdirs();
			}

			// 创建图片文件夹
			String wPicDir = StringUtils.Format("{0}图片/", wFileDir);
			File wPicDirFile = new File(wPicDir);
			if (!wPicDirFile.exists()) {
				wPicDirFile.mkdirs();
			}
			// 创建视频文件夹
			String wVideoDir = StringUtils.Format("{0}video/", wFileDir);
			File wVideoDirFile = new File(wVideoDir);
			if (!wVideoDirFile.exists()) {
				wVideoDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wFileDir, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			List<IPTCheckRecord> wPeriodChangeList = this.IPT_QueryPeriodChangeList(wLoginUser, wOrder);
			List<IPTCheckRecord> wPreCheckRecordList = this.IPT_QueryPreCheckRecordList(wLoginUser, wOrder);
			List<IPTCheckRecord> wExceptionList = this.IPT_QueryExceptionList(wLoginUser, wOrder);
			List<IPTCheckRecord> wKeyComponentList = this.IPT_QueryKeyComponentList(wLoginUser, wOrder);
			ExcelUtil.YJ_WriteReport(wPreCheckRecordList, wExceptionList, wPeriodChangeList, wKeyComponentList,
					wFileOutputStream, StringUtils.Format("{0}-{1}", new Object[] { wOrder.PartNo, wOrder.Customer }));

			// 获取图片路径集合
			List<String> wPicNoList = new ArrayList<String>();
			List<String> wPicList = this.GetPicList(wPreCheckRecordList, wExceptionList, wPeriodChangeList,
					wKeyComponentList, wPicNoList);
			// 获取视频路径集合
			List<String> wVideoList = this.GetVideoList(wPreCheckRecordList, wExceptionList, wPeriodChangeList,
					wKeyComponentList);
			// 遍历下载图片
			int wIndex = 1;
			for (String wPic : wPicList) {
				String wSuffix = this.GetSuffix(wPic);
				String wDesPath = StringUtils.Format("{0}{1}{3}.{2}", wPicDir, String.format("%02d", wIndex), wSuffix,
						wPicNoList.get(wIndex - 1));
				MESFileUtils.DownloadPicture(wPic, wDesPath);
				wIndex++;
			}
			// 遍历下载视频
			wIndex = 1;
			for (String wVideo : wVideoList) {
				String wSuffix = this.GetSuffix(wVideo);
				String wDesPath = StringUtils.Format("{0}{1}.{2}", wVideoDir, String.format("%02d", wIndex), wSuffix);
				MESFileUtils.DownloadPicture(wVideo, wDesPath);
				wIndex++;
			}

			// 压缩文件夹
//			ZipUtils.Zip(StringUtils.Format("{0}{1}-{2}-{3}-机车预检报告.zip", wDirePath, wOrder.Customer,
//					wOrder.PartNo.replace("#", "-"), wCurTime), wFileDir);

			MESFileUtils.compressToZip(wFileDir, wDirePath, StringUtils.Format("{0}-{1}-{2}-机车预检报告.zip",
					wOrder.Customer, wOrder.PartNo.replace("#", "-"), wCurTime));

			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), StringUtils.Format(
							"{0}-{1}-{2}-机车预检报告.zip", wOrder.Customer, wOrder.PartNo.replace("#", "-"), wCurTime));

			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			ServiceResult<String> serviceResult = wResult;
			serviceResult.FaultCode = String.valueOf(serviceResult.FaultCode) + e.toString();
			IPTServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	private List<String> GetVideoList(List<IPTCheckRecord> wPreCheckRecordList, List<IPTCheckRecord> wExceptionList,
			List<IPTCheckRecord> wPeriodChangeList, List<IPTCheckRecord> wKeyComponentList) {
		List<String> wResult = new ArrayList<String>();
		try {
			String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

			if (wPreCheckRecordList != null && wPreCheckRecordList.size() > 0) {
				for (IPTCheckRecord wIPTCheckRecord : wPreCheckRecordList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Video)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Video.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));
						}
					}
				}
			}

			if (wPeriodChangeList != null && wPeriodChangeList.size() > 0) {
				for (IPTCheckRecord wIPTCheckRecord : wPeriodChangeList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Video)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Video.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));
						}
					}
				}
			}

			if (wExceptionList != null && wExceptionList.size() > 0) {
				for (IPTCheckRecord wIPTCheckRecord : wExceptionList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Video)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Video.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));
						}
					}
				}
			}

			if (wKeyComponentList != null && wKeyComponentList.size() > 0) {
				for (IPTCheckRecord wIPTCheckRecord : wKeyComponentList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Video)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Video.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private List<String> GetPicList(List<IPTCheckRecord> wPreCheckRecordList, List<IPTCheckRecord> wExceptionList,
			List<IPTCheckRecord> wPeriodChangeList, List<IPTCheckRecord> wKeyComponentList, List<String> wPicNoList) {
		List<String> wResult = new ArrayList<String>();
		try {
			String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

			if (wPreCheckRecordList != null && wPreCheckRecordList.size() > 0) {
				for (IPTCheckRecord wIPTCheckRecord : wPreCheckRecordList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Picture)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Picture.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));
							wPicNoList.add(wIPTCheckRecord.PicNo);
						}
					}
				}
			}

			if (wPeriodChangeList != null && wPeriodChangeList.size() > 0) {
				for (IPTCheckRecord wIPTCheckRecord : wPeriodChangeList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Picture)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Picture.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));
							wPicNoList.add(wIPTCheckRecord.PicNo);
						}
					}
				}
			}

			if (wExceptionList != null && wExceptionList.size() > 0) {
				for (IPTCheckRecord wIPTCheckRecord : wExceptionList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Picture)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Picture.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));

							wPicNoList.add(wIPTCheckRecord.PicNo);
						}
					}
				}
			}

			if (wKeyComponentList != null && wKeyComponentList.size() > 0) {
				for (IPTCheckRecord wIPTCheckRecord : wKeyComponentList) {
					if (StringUtils.isEmpty(wIPTCheckRecord.Picture)) {
						continue;
					}

					String[] wStrs = wIPTCheckRecord.Picture.split(",");
					if (wStrs.length > 0) {
						for (String wStr : wStrs) {
							if (StringUtils.isEmpty(wStr)) {
								continue;
							}

							wResult.add(StringUtils.Format("{0}{1}", wUri, wStr));
							wPicNoList.add(wIPTCheckRecord.PicNo);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTPreCheckReport> IPT_QueryDefaultPreCheckReport(BMSEmployee wLoginUser, int wEventID,
			int wOrderID) {
		ServiceResult<IPTPreCheckReport> wResult = new ServiceResult<IPTPreCheckReport>();
		wResult.Result = new IPTPreCheckReport();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<IPTPreCheckReport> wList = IPTPreCheckReportDAO.getInstance().SelectList(wLoginUser, -1, "", -1,
					wOrderID, null, null, new ArrayList<Integer>(Arrays.asList(0)), wErrorCode);
			if (wList.size() > 0) {
				wResult.Result = wList.get(0);
				wResult.Result.UpFlowID = wLoginUser.ID;
				wResult.Result.UpFlowName = wLoginUser.Name;
				wResult.Result.CreateTime = Calendar.getInstance();
				wResult.Result.SubmitTime = Calendar.getInstance();
			} else {
				wResult.FaultCode += "提示：这台车的预检报告已经生成，不能重复生成!";
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<IPTPreCheckReport> IPT_CreatePreCheckReport(BMSEmployee wLoginUser,
			BPMEventModule wEventID) {
		ServiceResult<IPTPreCheckReport> wResult = new ServiceResult<IPTPreCheckReport>();
		wResult.Result = new IPTPreCheckReport();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result.Code = IPTPreCheckReportDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wResult.Result.UpFlowID = wLoginUser.ID;
			wResult.Result.UpFlowName = wLoginUser.Name;
			wResult.Result.CreateTime = Calendar.getInstance();
			wResult.Result.SubmitTime = Calendar.getInstance();
			wResult.Result.ID = 0;
			wResult.Result.Status = IPTPreCheckReportStatus.Default.getValue();
			wResult.Result.StatusText = "";
			wResult.Result.FlowType = wEventID.getValue();

			wResult.Result = (IPTPreCheckReport) IPTPreCheckReportDAO.getInstance().BPM_UpdateTask(wLoginUser,
					wResult.Result, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTPreCheckReport> IPT_SubmitPreCheckReport(BMSEmployee wLoginUser, IPTPreCheckReport wData) {
		ServiceResult<IPTPreCheckReport> wResult = new ServiceResult<IPTPreCheckReport>();
		wResult.Result = new IPTPreCheckReport();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wData.Status == IPTPreCheckReportStatus.ToAudit.getValue()) {
				IPTPreCheckReport wReport = IPTPreCheckReportDAO.getInstance().SelectByID(wLoginUser, wData.ID,
						wErrorCode);
				for (IPTPreCheckItem wIPTPreCheckItem : wReport.IPTPreCheckItemList) {
					for (IPTPreCheckProblem wIPTPreCheckProblem : wIPTPreCheckItem.IPTProblemList) {
						wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.Auditing.getValue();
						IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);
					}
				}
			} else if (wData.Status == IPTPreCheckReportStatus.NomalClose.getValue()) {
				IPTPreCheckReport wReport = IPTPreCheckReportDAO.getInstance().SelectByID(wLoginUser, wData.ID,
						wErrorCode);
				for (IPTPreCheckItem wIPTPreCheckItem : wReport.IPTPreCheckItemList) {
					for (IPTPreCheckProblem wIPTPreCheckProblem : wIPTPreCheckItem.IPTProblemList) {
						wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.Issued.getValue();
						IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);
					}
				}
			} else if (wData.Status == IPTPreCheckReportStatus.Rejected.getValue() || wData.Status == 22) {
				IPTPreCheckReport wReport = IPTPreCheckReportDAO.getInstance().SelectByID(wLoginUser, wData.ID,
						wErrorCode);
				for (IPTPreCheckItem wIPTPreCheckItem : wReport.IPTPreCheckItemList) {
					for (IPTPreCheckProblem wIPTPreCheckProblem : wIPTPreCheckItem.IPTProblemList) {
						wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.ToSendItem.getValue();
						IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);
					}
				}
				wData.StatusText = "待提交报告";
			}

			if (wData.Status == 1) {
				wData.StatusText = "待审批";
			}

			if (wData.Status == 20) {
				wData.AuditID = wLoginUser.ID;
				wData.AuditTime = Calendar.getInstance();
				wData.StatusText = "已审批";
			}

			if (wData.Status == 22) {
				wData.StatusText = "已撤销";
				wData.IPTPreCheckItemList = null;
				wData.Status = 0;
				wData.FlowID = BPMServiceImpl.getInstance()
						.BPM_CreateProcess(wLoginUser, BPMEventModule.getEnumType(wData.FlowType), wData.getID(), wData)
						.Info(Integer.class);
			}

			wResult.Result = (IPTPreCheckReport) IPTPreCheckReportDAO.getInstance().BPM_UpdateTask(wLoginUser, wData,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTPreCheckReport> IPT_GetPreCheckReport(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTPreCheckReport> wResult = new ServiceResult<IPTPreCheckReport>();
		wResult.Result = new IPTPreCheckReport();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (IPTPreCheckReport) IPTPreCheckReportDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wID, "",
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> IPT_QueryPreCheckReportEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// 2发起
				wResult.Result = IPTPreCheckReportDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 1待做
				wResult.Result = IPTPreCheckReportDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
						wErrorCode);
				break;
			case Approver:// 4已做
				wResult.Result = IPTPreCheckReportDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			if (wResult.Result.size() > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}

			// 排序
			wResult.Result.sort(Comparator.comparing(BPMTaskBase::getCreateTime, Comparator.reverseOrder()));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTPreCheckReport>> IPT_QueryPreCheckReportHistory(BMSEmployee wLoginUser, int wID,
			String wCode, int wUpFlowID, int wOrderID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<IPTPreCheckReport>> wResult = new ServiceResult<List<IPTPreCheckReport>>();
		wResult.Result = new ArrayList<IPTPreCheckReport>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = IPTPreCheckReportDAO.getInstance().SelectList(wLoginUser, wID, wCode, wUpFlowID, wOrderID,
					wStartTime, wEndTime, null, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTPreCheckProblemCar>> IPT_QueryEmployeeAllCar(BMSEmployee wLoginUser) {
		ServiceResult<List<IPTPreCheckProblemCar>> wResult = new ServiceResult<List<IPTPreCheckProblemCar>>();
		wResult.Result = new ArrayList<IPTPreCheckProblemCar>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			ServiceResult<List<IPTPreCheckProblem>> wSR = IPTServiceImpl.getInstance()
					.IPT_QueryPreCheckProblemByEmployee(wLoginUser);
			if (wSR.Result == null || wSR.Result.size() <= 0) {
				return wResult;
			}

			for (IPTPreCheckProblem wItem : wSR.Result) {
				if (wResult.Result.stream().anyMatch(p -> p.OrderID == wItem.OrderID)) {
					continue;
				}

				IPTPreCheckProblemCar wIPTPreCheckProblemCar = new IPTPreCheckProblemCar();

				wIPTPreCheckProblemCar.CarNumber = wItem.CarNumber;
				wIPTPreCheckProblemCar.CustomID = wItem.CustomID;
				wIPTPreCheckProblemCar.CustomName = wItem.CustomName;
				wIPTPreCheckProblemCar.LineID = wItem.LineID;
				wIPTPreCheckProblemCar.LineName = wItem.LineName;
				wIPTPreCheckProblemCar.OrderID = wItem.OrderID;
				wIPTPreCheckProblemCar.OrderNo = wItem.OrderNo;
				wIPTPreCheckProblemCar.ProductID = wItem.ProductID;
				wIPTPreCheckProblemCar.ProductNo = wItem.ProductNo;
				wIPTPreCheckProblemCar.Totals = (int) wSR.Result.stream().filter(p -> p.OrderID == wItem.OrderID)
						.count();

				wIPTPreCheckProblemCar.ToDo = (int) wSR.Result.stream()
						.filter(p -> p.OrderID == wItem.OrderID
								&& ((!p.IPTProblemConfirmerList.stream().anyMatch(q -> q.ConfirmerID == wLoginUser.ID)
										&& (p.Status == 1 || p.Status == 3))
										|| (p.Status == 10 && p.IPTProblemConfirmerList.stream()
												.anyMatch(q -> q.ConfirmerID == wLoginUser.ID && q.Status == 0))))
						.count();

				wResult.Result.add(wIPTPreCheckProblemCar);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTPreCheckProblem>> IPT_QueryEmployeeAllItemList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<IPTPreCheckProblem>> wResult = new ServiceResult<List<IPTPreCheckProblem>>();
		wResult.Result = new ArrayList<IPTPreCheckProblem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			ServiceResult<List<IPTPreCheckProblem>> wSR = IPTServiceImpl.getInstance()
					.IPT_QueryPreCheckProblemByEmployee(wLoginUser);
			if (wSR.Result == null || wSR.Result.size() <= 0) {
				return wResult;
			}

			wResult.Result = wSR.Result.stream().filter(p -> p.OrderID == wOrderID).collect(Collectors.toList());

			if (wResult.Result.size() > 0) {
				// 判断是否有权限做确认
				for (IPTPreCheckProblem wIPTPreCheckProblem : wResult.Result) {
					if (wIPTPreCheckProblem.Status == 10) {
						if (wIPTPreCheckProblem.IPTProblemConfirmerList.stream()
								.anyMatch(p -> p.ConfirmerID == wLoginUser.ID && p.Status == 0)) {
							wIPTPreCheckProblem.Flag = 1;
						} else if (wIPTPreCheckProblem.IPTProblemConfirmerList.stream()
								.anyMatch(p -> p.ConfirmerID == wLoginUser.ID && p.Status == 1)) {
							wIPTPreCheckProblem.Flag = 2;
						} else {
							wIPTPreCheckProblem.Flag = 0;
						}
					}
				}
				// 排序
				wResult.Result.sort(Comparator.comparing(IPTPreCheckProblem::getStepID));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_SkipAudit(BMSEmployee wLoginUser, IPTPreCheckProblem wData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wData.Status = IPTPreCheckProblemStatus.ToCraftGiveSolve.getValue();
			IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wData, wErrorCode);

			// ①关闭处理消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, -1, BPMEventModule.PreProblemHandle.getValue(), wData.ID,
							BFCMessageType.Task.getValue(), -1, -1, null, null)
					.List(BFCMessage.class);
			wMessageList = wMessageList.stream().filter(p -> p.StepID == 0).collect(Collectors.toList());
			for (BFCMessage wMessage : wMessageList) {
				if (wMessage.ResponsorID == wLoginUser.ID) {
					wMessage.Active = BFCMessageStatus.Finished.getValue();
				} else {
					wMessage.Active = BFCMessageStatus.Close.getValue();
				}
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
			// ①发送任务消息给预检工位工艺师
			if (QMSConstants.GetFPCPartList().values().stream()
					.anyMatch(p -> p.PartType == FPCPartTypes.PrevCheck.getValue())) {
				FPCPart wPart = QMSConstants.GetFPCPartList().values().stream()
						.filter(p -> p.PartType == FPCPartTypes.PrevCheck.getValue()).findFirst().get();
				List<BFCMessage> wBFCMessageList = new ArrayList<>();
				BFCMessage wMessage = null;
				int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
						FMCShiftLevel.Day);
				for (int wUserID : wPart.TechnicianList) {
					// 发送任务消息到人员
					wMessage = new BFCMessage();
					wMessage.Active = 0;
					wMessage.CompanyID = 0;
					wMessage.CreateTime = Calendar.getInstance();
					wMessage.EditTime = Calendar.getInstance();
					wMessage.ID = 0;
					wMessage.MessageID = wData.ID;
					wMessage.Title = StringUtils.Format("【{0}】 {1}", BPMEventModule.PreProblemHandle.getLable(),
							String.valueOf(wShiftID));
					wMessage.MessageText = StringUtils.Format("模块：{0} 发起人：{1}  发起时刻：{2} 待现场工艺给解决方案",
							new Object[] { BPMEventModule.PreProblemHandle.getLable(), wLoginUser.Name,
									StringUtils.parseCalendarToString(Calendar.getInstance(), "yyyy-MM-dd HH:mm") })
							.trim();
					wMessage.ModuleID = BPMEventModule.PreProblemHandle.getValue();
					wMessage.ResponsorID = wUserID;
					wMessage.ShiftID = wShiftID;
					wMessage.StationID = 0;
					wMessage.Type = BFCMessageType.Task.getValue();
					wMessage.StepID = 2;
					wBFCMessageList.add(wMessage);
				}
				CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTExportCheckRecord>> IPT_QueryCheckRecordList(BMSEmployee wLoginUser, int wID,
			String wCode, int wOperateID, int wOrderID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<IPTExportCheckRecord>> wResult = new ServiceResult<List<IPTExportCheckRecord>>();
		wResult.Result = new ArrayList<IPTExportCheckRecord>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = IPTCheckRecordDAO.getInstance().SelectList(wLoginUser, wID, wCode, wOperateID, wOrderID,
					wStartTime, wEndTime, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<IPTExportCheckRecord> IPT_ExportVerificate(BMSEmployee wLoginUser, int wOrderID,
			String wCode) {
		ServiceResult<IPTExportCheckRecord> wResult = new ServiceResult<IPTExportCheckRecord>();
		wResult.Result = new IPTExportCheckRecord();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①创建记录
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			IPTExportCheckRecord wIPTExportCheckRecord = new IPTExportCheckRecord();
			wIPTExportCheckRecord.ID = 0;
			wIPTExportCheckRecord.Code = wCode;
			wIPTExportCheckRecord.CreateTime = Calendar.getInstance();
			wIPTExportCheckRecord.ItemProgress = 0;
			wIPTExportCheckRecord.ItemSize = 0;
			wIPTExportCheckRecord.ItemTip = "";
			wIPTExportCheckRecord.OperateID = wLoginUser.ID;
			wIPTExportCheckRecord.OrderID = wOrderID;
			wIPTExportCheckRecord.PartNo = wOrder.PartNo;
			wIPTExportCheckRecord.TotalProgress = 0;
			wIPTExportCheckRecord.TotalSize = 6;
			wIPTExportCheckRecord.TotalTip = "";
			wIPTExportCheckRecord.ID = IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord,
					wErrorCode);
			// ②预检(预检报告审批完成)
			// ①工序任务
			wIPTExportCheckRecord.TotalProgress = 1;
			wIPTExportCheckRecord.ItemSize = 3;
			wIPTExportCheckRecord.ItemProgress = 1;
			wIPTExportCheckRecord.TotalTip = "预检";
			wIPTExportCheckRecord.ItemTip = "工序任务";
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

			FPCPart wPrePart = QMSConstants.GetFPCPartList().values().stream()
					.filter(p -> p.PartType == FPCPartTypes.PrevCheck.getValue()).findFirst().get();
			List<APSTaskStep> wPreTaskStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, wOrderID, wPrePart.ID, -1, null).List(APSTaskStep.class);
			if (wPreTaskStepList == null || wPreTaskStepList.size() <= 0) {
				IPTCheckResult wIPTCheckResult = new IPTCheckResult();
				wIPTCheckResult.ID = 0;
				wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
				wIPTCheckResult.CheckContent = "【预检】工序任务";
				wIPTCheckResult.CheckResult = "工序任务未生成";
				wIPTCheckResult.CheckTime = Calendar.getInstance();
				IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
			} else if (wPreTaskStepList.stream().anyMatch(p -> p.Status != APSTaskStatus.Done.getValue())) {
				IPTCheckResult wIPTCheckResult = new IPTCheckResult();
				wIPTCheckResult.ID = 0;
				wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
				wIPTCheckResult.CheckContent = "【预检】工序任务";
				wIPTCheckResult.CheckResult = StringUtils.Format("已做{0}条，待做{1}条",
						wPreTaskStepList.stream().filter(p -> p.Status == APSTaskStatus.Done.getValue()).count(),
						wPreTaskStepList.stream().filter(p -> p.Status != APSTaskStatus.Done.getValue()).count());
				wIPTCheckResult.CheckTime = Calendar.getInstance();
				IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
			}
			// ②问题项
			wIPTExportCheckRecord.TotalProgress = 1;
			wIPTExportCheckRecord.ItemSize = 3;
			wIPTExportCheckRecord.ItemProgress = 2;
			wIPTExportCheckRecord.TotalTip = "预检";
			wIPTExportCheckRecord.ItemTip = "问题项";
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

			List<IPTPreCheckProblem> wProblemList = IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, wOrderID,
					-1, -1, -1, -1, -1, -1, wErrorCode);
			if (wProblemList != null
					&& wProblemList.stream().anyMatch(p -> p.Status == 1 || p.Status == 2 || p.Status == 3)) {
				IPTCheckResult wIPTCheckResult = new IPTCheckResult();
				wIPTCheckResult.ID = 0;
				wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
				wIPTCheckResult.CheckContent = "【预检】问题项";
				wIPTCheckResult.CheckResult = StringUtils.Format("【待现场工艺发起评审】{0}条，【待相关人员评审】{1}条，【待现场工艺给解决方案】{2}条",
						wProblemList.stream().filter(p -> p.Status == 1).count(),
						wProblemList.stream().filter(p -> p.Status == 2).count(),
						wProblemList.stream().filter(p -> p.Status == 3).count());
				wIPTCheckResult.CheckTime = Calendar.getInstance();
				IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
			}
			// ③预检报告
			wIPTExportCheckRecord.TotalProgress = 1;
			wIPTExportCheckRecord.ItemSize = 3;
			wIPTExportCheckRecord.ItemProgress = 3;
			wIPTExportCheckRecord.TotalTip = "预检";
			wIPTExportCheckRecord.ItemTip = "预检报告";
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

			List<IPTPreCheckReport> wReportList = IPTPreCheckReportDAO.getInstance().SelectList(wLoginUser, -1, "", -1,
					wOrderID, null, null, null, wErrorCode);
			if (wReportList == null || wReportList.size() <= 0) {
				IPTCheckResult wIPTCheckResult = new IPTCheckResult();
				wIPTCheckResult.ID = 0;
				wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
				wIPTCheckResult.CheckContent = "【预检】预检报告";
				wIPTCheckResult.CheckResult = "预检报告未生成";
				wIPTCheckResult.CheckTime = Calendar.getInstance();
				IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
			} else if (wReportList.stream().anyMatch(p -> p.Status != 20)) {
				IPTCheckResult wIPTCheckResult = new IPTCheckResult();
				wIPTCheckResult.ID = 0;
				wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
				wIPTCheckResult.CheckContent = "【预检】预检报告";
				wIPTCheckResult.CheckResult = wReportList.get(0).StatusText;
				wIPTCheckResult.CheckTime = Calendar.getInstance();
				IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
			}
			// ③质量三检(最后工位所有工序任务完成)
			List<FPCRoutePart> wRoutePartList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wOrder.RouteID && p.PartID != wPrePart.ID).collect(Collectors.toList());

			wIPTExportCheckRecord.TotalProgress = 2;
			wIPTExportCheckRecord.TotalTip = "质量三检";
			wIPTExportCheckRecord.ItemSize = wRoutePartList.size();
			wIPTExportCheckRecord.ItemProgress = 0;
			wIPTExportCheckRecord.ItemTip = "工位任务";
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

			for (int i = 0; i < wRoutePartList.size(); i++) {
				List<APSTaskStep> wTaskStepList = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepList(wLoginUser, wOrderID, wRoutePartList.get(i).PartID, -1, null)
						.List(APSTaskStep.class);
				if (wTaskStepList == null || wTaskStepList.size() <= 0) {
					IPTCheckResult wIPTCheckResult = new IPTCheckResult();
					wIPTCheckResult.ID = 0;
					wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
					wIPTCheckResult.CheckContent = StringUtils.Format("【质量三检】{0}",
							QMSConstants.GetFPCPartName(wRoutePartList.get(i).PartID));
					wIPTCheckResult.CheckResult = "工序任务未生成";
					wIPTCheckResult.CheckTime = Calendar.getInstance();
					IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
				} else if (wTaskStepList.stream().anyMatch(p -> p.Status != APSTaskStatus.Done.getValue())) {
					IPTCheckResult wIPTCheckResult = new IPTCheckResult();
					wIPTCheckResult.ID = 0;
					wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
					wIPTCheckResult.CheckContent = StringUtils.Format("【质量三检】{0}",
							QMSConstants.GetFPCPartName(wRoutePartList.get(i).PartID));
					wIPTCheckResult.CheckResult = StringUtils.Format("已做{0}条，待做{1}条",
							wTaskStepList.stream().filter(p -> p.Status == APSTaskStatus.Done.getValue()).count(),
							wTaskStepList.stream().filter(p -> p.Status != APSTaskStatus.Done.getValue()).count());
					wIPTCheckResult.CheckTime = Calendar.getInstance();
					IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
				}

				wIPTExportCheckRecord.ItemProgress = i + 1;
				IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);
			}
			// ④质量终检(工序任务全部完成)
			wIPTExportCheckRecord.TotalProgress = 3;
			wIPTExportCheckRecord.TotalTip = "质量终检";
			wIPTExportCheckRecord.ItemSize = 1;
			wIPTExportCheckRecord.ItemProgress = 0;
			wIPTExportCheckRecord.ItemTip = "工序任务";
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

			FPCPart wFinalPart = QMSConstants.GetFPCPartList().values().stream()
					.filter(p -> p.PartType == FPCPartTypes.QTFinally.getValue()).findFirst().get();
			List<APSTaskStep> wFinalTaskStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, wOrderID, wFinalPart.ID, -1, null).List(APSTaskStep.class);
			if (wFinalTaskStepList == null || wFinalTaskStepList.size() <= 0) {
				IPTCheckResult wIPTCheckResult = new IPTCheckResult();
				wIPTCheckResult.ID = 0;
				wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
				wIPTCheckResult.CheckContent = "【质量终检】工序任务";
				wIPTCheckResult.CheckResult = "工序任务未生成";
				wIPTCheckResult.CheckTime = Calendar.getInstance();
				IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
			} else if (wFinalTaskStepList.stream().anyMatch(p -> p.Status != APSTaskStatus.Done.getValue())) {
				IPTCheckResult wIPTCheckResult = new IPTCheckResult();
				wIPTCheckResult.ID = 0;
				wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
				wIPTCheckResult.CheckContent = "【质量终检】工序任务";
				wIPTCheckResult.CheckResult = StringUtils.Format("已做{0}条，待做{1}条",
						wFinalTaskStepList.stream().filter(p -> p.Status == APSTaskStatus.Done.getValue()).count(),
						wFinalTaskStepList.stream().filter(p -> p.Status != APSTaskStatus.Done.getValue()).count());
				wIPTCheckResult.CheckTime = Calendar.getInstance();
				IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
			}

			wIPTExportCheckRecord.ItemProgress = 1;
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);
			// ⑤出厂普查(工序任务全部完成)
			wIPTExportCheckRecord.TotalProgress = 4;
			wIPTExportCheckRecord.TotalTip = "出厂普查";
			wIPTExportCheckRecord.ItemSize = 1;
			wIPTExportCheckRecord.ItemProgress = 0;
			wIPTExportCheckRecord.ItemTip = "工序任务";
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

			FPCPart wOutPart = QMSConstants.GetFPCPartList().values().stream()
					.filter(p -> p.PartType == FPCPartTypes.OutFactory.getValue()).findFirst().get();
			List<APSTaskStep> wOutTaskStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, wOrderID, wOutPart.ID, -1, null).List(APSTaskStep.class);

			if (wOutTaskStepList == null || wOutTaskStepList.size() <= 0) {
				IPTCheckResult wIPTCheckResult = new IPTCheckResult();
				wIPTCheckResult.ID = 0;
				wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
				wIPTCheckResult.CheckContent = "【出厂普查】工序任务";
				wIPTCheckResult.CheckResult = "工序任务未生成";
				wIPTCheckResult.CheckTime = Calendar.getInstance();
				IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
			} else if (wOutTaskStepList.stream().anyMatch(p -> p.Status != APSTaskStatus.Done.getValue())) {
				IPTCheckResult wIPTCheckResult = new IPTCheckResult();
				wIPTCheckResult.ID = 0;
				wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
				wIPTCheckResult.CheckContent = "【出厂普查】工序任务";
				wIPTCheckResult.CheckResult = StringUtils.Format("已做{0}条，待做{1}条",
						wOutTaskStepList.stream().filter(p -> p.Status == APSTaskStatus.Done.getValue()).count(),
						wOutTaskStepList.stream().filter(p -> p.Status != APSTaskStatus.Done.getValue()).count());
				wIPTCheckResult.CheckTime = Calendar.getInstance();
				IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);
			}

			wIPTExportCheckRecord.ItemProgress = 1;
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);
			// ⑥返修(所有返修任务全部关闭)
			wIPTExportCheckRecord.TotalProgress = 5;
			wIPTExportCheckRecord.TotalTip = "返修";
			wIPTExportCheckRecord.ItemSize = 1;
			wIPTExportCheckRecord.ItemProgress = 0;
			wIPTExportCheckRecord.ItemTip = "返修任务";
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

			List<RROItemTask> wRROItemList = WDWServiceImpl.getInstance().WDW_QueryItemByOrderID(wLoginUser, wOrderID)
					.List(RROItemTask.class);

			if (wRROItemList != null && wRROItemList.size() > 0) {
				wIPTExportCheckRecord.ItemSize = wRROItemList.size();
				wIPTExportCheckRecord.ItemProgress = 0;
				IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

				for (int i = 0; i < wRROItemList.size(); i++) {
					IPTCheckResult wIPTCheckResult = new IPTCheckResult();
					wIPTCheckResult.ID = 0;
					wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
					wIPTCheckResult.CheckContent = "【返修】返修任务";
					wIPTCheckResult.CheckResult = StringUtils.Format("【{2}{0}】{1}", wRROItemList.get(i).Content,
							wRROItemList.get(i).StatusText, wRROItemList.get(i).Code);
					wIPTCheckResult.CheckTime = Calendar.getInstance();
					IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);

					wIPTExportCheckRecord.ItemProgress = i++;
					IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);
				}
			}
			// ⑦不合格评审(所有不合格评审任务全部关闭)
			wIPTExportCheckRecord.TotalProgress = 6;
			wIPTExportCheckRecord.TotalTip = "不合格评审";
			wIPTExportCheckRecord.ItemSize = 1;
			wIPTExportCheckRecord.ItemProgress = 0;
			wIPTExportCheckRecord.ItemTip = "不合格评审任务";
			IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

			List<NCRTask> wNCRTaskList = WDWServiceImpl.getInstance().NCR_QueryTimeAll(wLoginUser, wOrderID)
					.List(NCRTask.class);
			wNCRTaskList = wNCRTaskList.stream().filter(p -> p.Status != 12 && p.Status != 0)
					.collect(Collectors.toList());
			if (wNCRTaskList != null && wNCRTaskList.size() > 0) {
				wIPTExportCheckRecord.ItemSize = wNCRTaskList.size();
				wIPTExportCheckRecord.ItemProgress = 0;
				IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);

				for (int i = 0; i < wNCRTaskList.size(); i++) {
					IPTCheckResult wIPTCheckResult = new IPTCheckResult();
					wIPTCheckResult.ID = 0;
					wIPTCheckResult.RecordID = wIPTExportCheckRecord.ID;
					wIPTCheckResult.CheckContent = "【不合格评审】不合格评审任务";
					wIPTCheckResult.CheckResult = StringUtils.Format("【{2}{0}】{1}", wNCRTaskList.get(i).DescribeInfo,
							wNCRTaskList.get(i).StatusText, wNCRTaskList.get(i).Code);
					wIPTCheckResult.CheckTime = Calendar.getInstance();
					IPTCheckResultDAO.getInstance().Update(wLoginUser, wIPTCheckResult, wErrorCode);

					wIPTExportCheckRecord.ItemProgress = i++;
					IPTCheckRecordDAO.getInstance().Update(wLoginUser, wIPTExportCheckRecord, wErrorCode);
				}
			}

			wResult.Result = IPTCheckRecordDAO.getInstance().SelectByID(wLoginUser, wIPTExportCheckRecord.ID,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTExportCheckRecord> IPT_VerificateProgress(BMSEmployee wLoginUser, String wCode) {
		ServiceResult<IPTExportCheckRecord> wResult = new ServiceResult<IPTExportCheckRecord>();
		wResult.Result = new IPTExportCheckRecord();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<IPTExportCheckRecord> wList = IPTCheckRecordDAO.getInstance().SelectList(wLoginUser, -1, wCode, -1, -1,
					null, null, wErrorCode);
			if (wList != null && wList.size() > 0) {
				wResult.Result = wList.get(0);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> AddRemark(BMSEmployee wLoginUser, IPTValue wData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = (int) IPTValueDAO.getInstance().Update(wLoginUser, wData, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 查询单条
	 */
	@Override
	public ServiceResult<IPTTool> IPT_QueryTool(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTTool> wResult = new ServiceResult<IPTTool>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = IPTToolDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询
	 */
	@Override
	public ServiceResult<List<IPTTool>> IPT_QueryToolList(BMSEmployee wLoginUser, int wID, int wStandardID) {
		ServiceResult<List<IPTTool>> wResult = new ServiceResult<List<IPTTool>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = IPTToolDAO.getInstance().SelectList(wLoginUser, wID, wStandardID, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 更新或新增
	 */
	@Override
	public ServiceResult<Integer> IPT_UpdateTool(BMSEmployee wLoginUser, IPTTool wIPTTool) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = IPTToolDAO.getInstance().Update(wLoginUser, wIPTTool, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTStandardBPM> IPT_QueryDefaultStandardBPM(BMSEmployee wLoginUser, int wEventID) {
		ServiceResult<IPTStandardBPM> wResult = new ServiceResult<IPTStandardBPM>();
		wResult.Result = new IPTStandardBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<IPTStandardBPM> wList = IPTStandardBPMDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1, null,
					null, new ArrayList<Integer>(Arrays.asList(0)), wErrorCode);
			if (wList.size() > 0) {
				wResult.Result = wList.get(0);
				wResult.Result.CreateTime = Calendar.getInstance();
				wResult.Result.UpFlowID = wLoginUser.ID;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<IPTStandardBPM> IPT_CreateStandardBPM(BMSEmployee wLoginUser,
			BPMEventModule wEventID) {
		ServiceResult<IPTStandardBPM> wResult = new ServiceResult<IPTStandardBPM>();
		wResult.Result = new IPTStandardBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result.Code = IPTStandardBPMDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wResult.Result.UpFlowID = wLoginUser.ID;
			wResult.Result.UpFlowName = wLoginUser.Name;
			wResult.Result.CreateTime = Calendar.getInstance();
			wResult.Result.SubmitTime = Calendar.getInstance();
			wResult.Result.ID = 0;
			wResult.Result.Status = IPTStandardBPMStatus.Default.getValue();
			wResult.Result.StatusText = "";
			wResult.Result.FlowType = wEventID.getValue();

			wResult.Result = (IPTStandardBPM) IPTStandardBPMDAO.getInstance().BPM_UpdateTask(wLoginUser, wResult.Result,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTStandardBPM> IPT_SubmitStandardBPM(BMSEmployee wLoginUser, IPTStandardBPM wData) {
		ServiceResult<IPTStandardBPM> wResult = new ServiceResult<IPTStandardBPM>();
		wResult.Result = new IPTStandardBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wData.Status == IPTStandardBPMStatus.ToAudit.getValue()) {
				String[] wStrs = wData.StandardID.split(",");
				for (String wStr : wStrs) {
					int wStandardID = StringUtils.parseInt(wStr);
					ServiceResult<IPTStandard> wItem = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
							wStandardID, wErrorCode);
					if (wItem.Result != null && wItem.Result.ID > 0) {
						wItem.Result.Status = IPTStandardStatus.ToAudit.getValue();
						IPTStandardDAO.getInstance().Update(wLoginUser, wItem.Result, wErrorCode);
					}
				}
			} else if (wData.Status == IPTStandardBPMStatus.Canceled.getValue()) {
				String[] wStrs = wData.StandardID.split(",");
				for (String wStr : wStrs) {
					int wStandardID = StringUtils.parseInt(wStr);
					ServiceResult<IPTStandard> wItem = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
							wStandardID, wErrorCode);
					if (wItem.Result != null && wItem.Result.ID > 0) {
						wItem.Result.Status = IPTStandardStatus.Default.getValue();
						IPTStandardDAO.getInstance().Update(wLoginUser, wItem.Result, wErrorCode);
					}
				}

				wData.StatusText = "已撤销";
			} else if (wData.Status == IPTStandardBPMStatus.ExceptionClose.getValue()) {
				String[] wStrs = wData.StandardID.split(",");
				for (String wStr : wStrs) {
					int wStandardID = StringUtils.parseInt(wStr);
					ServiceResult<IPTStandard> wItem = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
							wStandardID, wErrorCode);
					if (wItem.Result != null && wItem.Result.ID > 0) {
						wItem.Result.Status = IPTStandardStatus.Default.getValue();
						IPTStandardDAO.getInstance().Update(wLoginUser, wItem.Result, wErrorCode);
					}
				}

				wData.StatusText = "已驳回";
			} else if (wData.Status == IPTStandardBPMStatus.NomalClose.getValue()) {
				String[] wStrs = wData.StandardID.split(",");
				for (String wStr : wStrs) {
					int wStandardID = StringUtils.parseInt(wStr);
					ServiceResult<IPTStandard> wItem = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
							wStandardID, wErrorCode);
					if (wItem.Result != null && wItem.Result.ID > 0) {
						wItem.Result.Status = IPTStandardStatus.Audited.getValue();
						IPTStandardDAO.getInstance().Update(wLoginUser, wItem.Result, wErrorCode);
					}
				}
			}

			wResult.Result = (IPTStandardBPM) IPTStandardBPMDAO.getInstance().BPM_UpdateTask(wLoginUser, wData,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTStandardBPM> IPT_GetStandardBPM(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTStandardBPM> wResult = new ServiceResult<IPTStandardBPM>();
		wResult.Result = new IPTStandardBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (IPTStandardBPM) IPTStandardBPMDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wID, "",
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> IPT_QueryStandardBPMEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// 2发起
				wResult.Result = IPTStandardBPMDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 1待做
				wResult.Result = IPTStandardBPMDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
						wErrorCode);
				break;
			case Approver:// 4已做
				wResult.Result = IPTStandardBPMDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			if (wResult.Result.size() > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}

			// 排序
			wResult.Result.sort(Comparator.comparing(BPMTaskBase::getCreateTime, Comparator.reverseOrder()));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTStandardBPM>> IPT_QueryStandardBPMHistory(BMSEmployee wLoginUser, int wID,
			String wCode, int wUpFlowID, int wStandardID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<IPTStandardBPM>> wResult = new ServiceResult<List<IPTStandardBPM>>();
		wResult.Result = new ArrayList<IPTStandardBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = IPTStandardBPMDAO.getInstance().SelectList(wLoginUser, wID, wCode, wUpFlowID, wStandardID,
					wStartTime, wEndTime, null, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_DeletItems(BMSEmployee wLoginUser, List<IPTItem> wItemList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult = IPTItemDAO.getInstance().DeleteList(wLoginUser, wItemList, wErrorCode);

			// ①重新维护标准时间
			ServiceResult<IPTStandard> wStandardRst = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wItemList.get(0).VID, wErrorCode);
			List<IPTItem> wNewItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wItemList.get(0).VID, -1,
					-1, wErrorCode);
			double wWorkTime = 0;
			for (IPTItem wIPTItem : wNewItemList) {
				wWorkTime += wIPTItem.WorkTime;
			}
			wStandardRst.Result.WorkTime = wWorkTime;
			IPTStandardDAO.getInstance().Update(wLoginUser, wStandardRst.Result, wErrorCode);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_DeleteTooList(BMSEmployee wLoginUser, List<IPTTool> wList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult = IPTToolDAO.getInstance().DeleteList(wLoginUser, wList, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_CopyStandard(BMSEmployee wLoginUser, int wStandardID, OMSOrder wOMSOrder) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①查询标准
			IPTStandard wIPTStandard = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, wStandardID,
					wErrorCode).Result;
			if (wIPTStandard == null || wIPTStandard.ID <= 0) {
				return wResult;
			}
			// ②复制标准
			wIPTStandard.ID = 0;
			wIPTStandard.IsCurrent = 0;
			wIPTStandard.IsUsed = 0;
			wIPTStandard.IsEnd = 0;
			wIPTStandard.TModify = Calendar.getInstance();
			wIPTStandard.Status = 0;
			wIPTStandard.OrderID = wOMSOrder.ID;
			wIPTStandard.PartNo = wOMSOrder.PartNo;
			Long wNewID = IPTStandardDAO.getInstance().InsertStandard(wLoginUser, wIPTStandard, wErrorCode).Result;
			if (wNewID <= 0) {
				return wResult;
			}
			// ③查询项点
			List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wStandardID, -1, -1,
					wErrorCode);
			// ④复制项点
			if (wItemList.size() > 0) {
				for (IPTItem wIPTItem : wItemList) {
					wIPTItem.ID = 0;
					wIPTItem.VID = wNewID.intValue();
					IPTItemDAO.getInstance().Update(wLoginUser, wNewID, wIPTItem, wErrorCode);
				}
			}
			// ⑤查询工具
			List<IPTTool> wToolList = IPTToolDAO.getInstance().SelectList(wLoginUser, -1, wStandardID, wErrorCode);
			// ⑥复制工具
			if (wToolList.size() > 0) {
				for (IPTTool wIPTTool : wToolList) {
					wIPTTool.ID = 0;
					wIPTTool.StandardID = wNewID.intValue();
					IPTToolDAO.getInstance().Update(wLoginUser, wIPTTool, wErrorCode);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_ImportStandardNew(BMSEmployee wLoginUser, int wIPTMode, int wProductID,
			int wLineID, int wCustomerID, int wStationID, ExcelData wExcelData, String wOriginalFileName) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<FPCPartPoint> wFPCStepList = FMCServiceImpl.getInstance()
					.FPC_QueryPartPointList(BaseDAO.SysAdmin, 0, 0, 0).List(FPCPartPoint.class);

			String wMsg = CheckExcelData(wExcelData, wFPCStepList);
			if (StringUtils.isNotEmpty(wMsg)) {
				wResult.FaultCode = wMsg;

				// ①记录导入日志
				RecordErrorLog(wLoginUser, wExcelData, wFPCStepList);

				return wResult;
			}

			// ①导入标准
			List<String> wHandleStep = new ArrayList<String>();
			int wFlag = 0;
			for (ExcelLineData wExcelLineData : wExcelData.sheetData.get(0).lineData) {
				if (wFlag == 0) {
					wFlag++;
					continue;
				}

				if (wHandleStep.stream().anyMatch(p -> p.equals(wExcelLineData.colData.get(2)))) {
					continue;
				}

				int wStepID = wFPCStepList.stream().filter(p -> p.Name.equals(wExcelLineData.colData.get(2)))
						.findFirst().get().ID;

				// ①查找标准
				List<IPTStandard> wStandardList = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, -1,
						IPTMode.getEnumType(wIPTMode), wCustomerID, -1, -1, -1, -1, wLineID, wStationID, wStepID, -1,
						wProductID, "", 100, null, null, wErrorCode).Result;
				if (wStandardList.stream().anyMatch(p -> p.Status == 0 && p.IsCurrent == 0)) {
					wStandardList = wStandardList.stream().filter(p -> p.Status == 0 && p.IsCurrent == 0)
							.collect(Collectors.toList());
					IPTStandard wIPTStandard = wStandardList.stream().max(Comparator.comparing(IPTStandard::getTModify))
							.get();
					// ①维护作业时间
					List<ExcelLineData> wLineDataList_Item = wExcelData.sheetData.get(0).lineData.stream()
							.filter(p -> p.colData.get(2).equals(wExcelLineData.colData.get(2)))
							.collect(Collectors.toList());
					wIPTStandard.Code = wExcelLineData.colData.get(0);
					wIPTStandard.PersonNumber = StringUtils.parseDouble(wExcelLineData.colData.get(1)).intValue();
					wIPTStandard.WorkTime = GetWorkTime(wLineDataList_Item);
					wIPTStandard.TModify = Calendar.getInstance();
					IPTStandardDAO.getInstance().Update(wLoginUser, wIPTStandard, wErrorCode);
					// ①删除项点
					List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wIPTStandard.ID, -1,
							-1, wErrorCode);
					if (wItemList.size() > 0) {
						IPTItemDAO.getInstance().DeleteList(wLoginUser, wItemList, wErrorCode);
					}
					// ①删除工具
					List<IPTTool> wToolList = IPTToolDAO.getInstance().SelectList(wLoginUser, -1, (int) wIPTStandard.ID,
							wErrorCode);
					if (wToolList.size() > 0) {
						IPTToolDAO.getInstance().DeleteList(wLoginUser, wToolList, wErrorCode);
					}
					// ②导入工具
					List<ExcelLineData> wLineDataList_Tool = wExcelData.sheetData.get(1).lineData.stream()
							.filter(p -> p.colData.get(0).equals(wExcelLineData.colData.get(2)))
							.collect(Collectors.toList());
					for (ExcelLineData wItem : wLineDataList_Tool) {
						IPTTool wIPTTool = new IPTTool();

						wIPTTool.CreateID = wLoginUser.ID;
						wIPTTool.CreateTime = Calendar.getInstance();
						wIPTTool.EditID = wLoginUser.ID;
						wIPTTool.EditTime = Calendar.getInstance();
						wIPTTool.ID = 0;
						wIPTTool.Modal = wItem.colData.size() >= 6 ? wItem.colData.get(3) : "";
						wIPTTool.Name = wItem.colData.size() >= 6 ? wItem.colData.get(2) : "";
						wIPTTool.Number = wItem.colData.size() >= 6
								? StringUtils.parseDouble(wItem.colData.get(4)).intValue()
								: 0;
						wIPTTool.OrderNum = wItem.colData.size() >= 6
								? StringUtils.parseDouble(wItem.colData.get(1)).intValue()
								: 1;
						wIPTTool.StandardID = (int) wIPTStandard.ID;
						wIPTTool.UnitID = wItem.colData.size() >= 6 ? QMSConstants.GetCFGUnit(wItem.colData.get(5)).ID
								: 0;
						IPTToolDAO.getInstance().Update(wLoginUser, wIPTTool, wErrorCode);
					}
					// ③导入项点

					for (ExcelLineData wItem : wLineDataList_Item) {
						IPTItem wItemItem = GetIPTItem(wLoginUser, wItem, (int) wIPTStandard.ID);
						IPTItemDAO.getInstance().Update(wLoginUser, (int) wIPTStandard.ID, wItemItem, wErrorCode);
					}
					// ④添加到已处理结果集
					wHandleStep.add(wExcelLineData.colData.get(2));
				} else {
					IPTStandard wIPTStandard = new IPTStandard();

					wIPTStandard.ProductID = wProductID;
					wIPTStandard.ProductNo = QMSConstants.GetFPCProductNo(wProductID);
					wIPTStandard.LineID = wLineID;
					wIPTStandard.IPTMode = wIPTMode;
					wIPTStandard.UserID = wLoginUser.ID;
					wIPTStandard.PartID = wStationID;
					wIPTStandard.CustomID = wCustomerID;
					wIPTStandard.PartPointID = wStepID;
					wIPTStandard.Remark = wExcelLineData.colData.get(0);
					wIPTStandard.Code = wExcelLineData.colData.get(0);
					wIPTStandard.PersonNumber = StringUtils.parseDouble(wExcelLineData.colData.get(1)).intValue();
					wIPTStandard.Version = IPTStandardDAO.getInstance().SelectNewVersion_V2(wLoginUser, wProductID,
							wLineID, wCustomerID, wStationID, wStepID, wErrorCode);
					List<ExcelLineData> wLineDataList_Item = wExcelData.sheetData.get(0).lineData.stream()
							.filter(p -> p.colData.get(2).equals(wExcelLineData.colData.get(2)))
							.collect(Collectors.toList());
					wIPTStandard.WorkTime = GetWorkTime(wLineDataList_Item);

					wIPTStandard.ID = IPTStandardDAO.getInstance().InsertStandard(wLoginUser, wIPTStandard,
							wErrorCode).Result;
					// ②导入工具
					List<ExcelLineData> wLineDataList_Tool = wExcelData.sheetData.get(1).lineData.stream()
							.filter(p -> p.colData.get(0).equals(wExcelLineData.colData.get(2)))
							.collect(Collectors.toList());
					for (ExcelLineData wItem : wLineDataList_Tool) {
						IPTTool wIPTTool = new IPTTool();

						wIPTTool.CreateID = wLoginUser.ID;
						wIPTTool.CreateTime = Calendar.getInstance();
						wIPTTool.EditID = wLoginUser.ID;
						wIPTTool.EditTime = Calendar.getInstance();
						wIPTTool.ID = 0;
						wIPTTool.Modal = wItem.colData.size() >= 6 ? wItem.colData.get(3) : "";
						wIPTTool.Name = wItem.colData.size() >= 6 ? wItem.colData.get(2) : "";
						wIPTTool.Number = wItem.colData.size() >= 6
								? StringUtils.parseDouble(wItem.colData.get(4)).intValue()
								: 0;
						wIPTTool.OrderNum = wItem.colData.size() >= 6
								? StringUtils.parseDouble(wItem.colData.get(1)).intValue()
								: 1;
						wIPTTool.StandardID = (int) wIPTStandard.ID;
						wIPTTool.UnitID = wItem.colData.size() >= 6 ? QMSConstants.GetCFGUnit(wItem.colData.get(5)).ID
								: 0;
						IPTToolDAO.getInstance().Update(wLoginUser, wIPTTool, wErrorCode);
					}
					// ③导入项点

					for (ExcelLineData wItem : wLineDataList_Item) {
						IPTItem wItemItem = GetIPTItem(wLoginUser, wItem, (int) wIPTStandard.ID);
						IPTItemDAO.getInstance().Update(wLoginUser, (int) wIPTStandard.ID, wItemItem, wErrorCode);
					}
					// ④添加到已处理结果集
					wHandleStep.add(wExcelLineData.colData.get(2));
				}

				wFlag++;
			}

			IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
					IMPType.Standard.getValue(), "", null, IMPResult.Success.getValue(), wExcelData.fileName,
					wExcelData.sheetData.get(0).lineData.size() - 1, 0);
			IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 记录导入日志
	 */
	private void RecordErrorLog(BMSEmployee wLoginUser, ExcelData wExcelData, List<FPCPartPoint> wStepList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<IMPErrorRecord> wRecordList = new ArrayList<IMPErrorRecord>();

			int wFlag = 0;
			for (ExcelLineData wExcelLineData : wExcelData.sheetData.get(0).lineData) {
				if (wFlag == 0) {
					wFlag++;
					continue;
				}

				if (!wStepList.stream().anyMatch(p -> p.Name.equals(wExcelLineData.colData.get(2)))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【{1}】工序不存在。", wFlag + 1, wExcelLineData.colData.get(2))));
				}

				wFlag++;
			}

			if (wRecordList.size() > 0) {
				IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
						IMPType.Standard.getValue(), "", null, IMPResult.Failed.getValue(), wExcelData.fileName,
						wExcelData.sheetData.get(0).lineData.size() - 1, wRecordList.size());
				int wNewID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

				wRecordList.forEach(p -> p.ParentID = wNewID);
				for (IMPErrorRecord wIMPErrorRecord : wRecordList) {
					IMPErrorRecordDAO.getInstance().Update(wLoginUser, wIMPErrorRecord, wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取总的作业时间
	 */
	private double GetWorkTime(List<ExcelLineData> wLineDataList_Item) {
		double wResult = 0;
		try {
			for (ExcelLineData wExcelLineData : wLineDataList_Item) {
				Double wTime = StringUtils.parseDouble(wExcelLineData.colData.get(4));
				wResult += wTime;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取标准项点
	 */
	private IPTItem GetIPTItem(BMSEmployee wLoginUser, ExcelLineData wExcelLineData, int wStandardID) {
		IPTItem wResult = new IPTItem();
		try {
			wResult.ID = 0;

			wResult.Active = 1;

			wResult.SerialNumber = wExcelLineData.colData.size() >= 4 ? wExcelLineData.colData.get(3).replace(".0", "")
					: "";
			wResult.WorkTime = wExcelLineData.colData.size() >= 5
					? StringUtils.parseDouble(wExcelLineData.colData.get(4))
					: 0;
			wResult.Text = wExcelLineData.colData.size() >= 6 ? wExcelLineData.colData.get(5) : "";
			wResult.WorkContent = wExcelLineData.colData.size() >= 7 ? wExcelLineData.colData.get(6) : "";
			wResult.Standard = wExcelLineData.colData.size() >= 8 ? wExcelLineData.colData.get(7) : "";
			wResult.StandardType = wExcelLineData.colData.size() >= 9 ? GetStandardType(wExcelLineData.colData.get(8))
					: 0;
			wResult.StandardLeft = wExcelLineData.colData.size() >= 10
					? StringUtils.parseDouble(wExcelLineData.colData.get(9))
					: 0;
			wResult.StandardValue = wExcelLineData.colData.size() >= 11 ? wExcelLineData.colData.get(10) : "";
			wResult.StandardRight = wExcelLineData.colData.size() >= 12
					? StringUtils.parseDouble(wExcelLineData.colData.get(11))
					: 0;
			wResult.Unit = wExcelLineData.colData.size() >= 13 ? wExcelLineData.colData.get(12) : "";
			wResult.AutoCalc = wExcelLineData.colData.size() >= 14
					? wExcelLineData.colData.get(13).equals("软管动力试验") ? 1 : 0
					: 0;
			wResult.ItemType = 1;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取标准值类型
	 */
	private int GetStandardType(String wExpress) {
		int wResult = 0;
		try {
			wExpress = wExpress.replace(" ", "");

			switch (wExpress) {
			case "文本":
				wResult = 0;
				break;
			case "n<b":
			case "n＜b":
			case "b>n":
			case "b＞n":
			case "n<a":
			case "n＜a":
			case "a>n":
			case "a＞n":
				wResult = 6;
				break;
			case "n>b":
			case "n＞b":
			case "b<n":
			case "b＜n":
			case "n>a":
			case "n＞a":
			case "a<n":
			case "a＜n":
				wResult = 7;
				break;
			case "n<=b":
			case "b>=n":
			case "n≤b":
			case "b≥n":
				wResult = 8;
				break;
			case "n>=b":
			case "n≥b":
			case "b<=n":
			case "b≤n":
				wResult = 9;
				break;
			case "n=b":
			case "b=n":
				wResult = 10;
				break;
			case "a<n<b":
			case "a＜n＜b":
				wResult = 2;
				break;
			case "a<=n<=b":
			case "a≤n≤b":
				wResult = 3;
				break;
			case "a<n<=b":
			case "a<n≤b":
			case "a＜n≤b":
				wResult = 5;
				break;
			case "a<=n<b":
			case "a≤n<b":
			case "a≤n＜b":
				wResult = 4;
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 检查Excel数据是否合法
	 */
	private String CheckExcelData(ExcelData wExcelData, List<FPCPartPoint> wFPCStepList) {
		String wResult = "";
		try {
			if (wExcelData.sheetData == null || wExcelData.sheetData.size() <= 0) {
				wResult = "提示：未解析到Sheet，请检查文件。";
				return wResult;
			}

			if (wExcelData.sheetData.size() > 3) {
				wResult = "提示：Sheet数量大于3，请检查文件模板是否正确。";
				return wResult;
			}

			if (!wExcelData.sheetData.get(0).sheetName.equals("作业内容及要求")) {
				wResult = "提示：Sheet1的名称不为【作业内容及要求】，请检查文件模板是否正确。";
				return wResult;
			}

			if (!wExcelData.sheetData.get(1).sheetName.equals("工具、工装准备")) {
				wResult = "提示：Sheet2的名称不为【工具、工装准备】，请检查文件模板是否正确。";
				return wResult;
			}

			if (wExcelData.sheetData.get(0).lineData == null || wExcelData.sheetData.get(0).lineData.size() <= 0) {
				wResult = "提示：Sheet1未解析到数据，请检查Sheet1的内容是否正确。";
				return wResult;
			}

			if (wExcelData.sheetData.get(1).lineData == null || wExcelData.sheetData.get(1).lineData.size() <= 0) {
				wResult = "提示：Sheet2未解析到数据，请检查Sheet2的内容是否正确。";
				return wResult;
			}

			if (wExcelData.sheetData.get(0).lineData.get(0).colData == null
					|| wExcelData.sheetData.get(0).lineData.get(0).colData.size() < 14) {
				wResult = "提示：Sheet1的列数小于11，请检查Sheet1模板是否正确。";
				return wResult;
			}

			if (wExcelData.sheetData.get(1).lineData.get(0).colData == null
					|| wExcelData.sheetData.get(1).lineData.get(0).colData.size() < 6) {
				wResult = "提示：Sheet2的列数小于5，请检查Sheet2模板是否正确。";
				return wResult;
			}

			// ①检查工序是否存在

			for (int i = 1; i < wExcelData.sheetData.get(0).lineData.size(); i++) {
				ExcelLineData wLineData = wExcelData.sheetData.get(0).lineData.get(i);
				if (!wFPCStepList.stream().anyMatch(p -> p.Name.equals(wLineData.colData.get(2)))) {
					wResult = StringUtils.Format("提示：第【{0}】行数据不合法，【{1}】工序不存在。", i + 1, wLineData.colData.get(2));
					return wResult;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_EditStandard(BMSEmployee wLoginUser, IPTStandard wData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			IPTStandard wIPTStandard = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, wData.ID,
					wErrorCode).Result;

			wResult.Result = (int) IPTStandardDAO.getInstance().Update(wLoginUser, wData, wErrorCode);

			// 记录是否拍照操作日志
			RecordIsPicLog(wLoginUser, wIPTStandard, wData);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private void RecordIsPicLog(BMSEmployee wLoginUser, IPTStandard wIPTStandard, IPTStandard wData) {
		try {
			if (wIPTStandard.IsPic == wData.IsPic) {
				return;
			}

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String wCurrentTime = wSDF.format(Calendar.getInstance().getTime());

			String wContent = StringUtils.Format("【{0}】在【{1}】将【{2}】-【{3}】过程控制记录的【拍照】属性修改为【{4}】", wLoginUser.Name,
					wCurrentTime, wIPTStandard.PartName, wIPTStandard.PartPointName, wData.IsPic == 1 ? "是" : "否");

			LFSOperationLog wLFSOperationLog = new LFSOperationLog(0, (int) wData.ID,
					LFSOperationLogType.SetStepPic.getValue(), LFSOperationLogType.SetStepPic.getLable(), wLoginUser.ID,
					wLoginUser.Name, Calendar.getInstance(), wContent);
			LFSServiceImpl.getInstance().LFS_UpdateOperationLog(wLoginUser, wLFSOperationLog);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<IPTStandardBPM>> IPT_QueryStandardBPMEmployeeAllWeb(BMSEmployee wLoginUser,
			Integer wProductID, Integer wLineID, Integer wPartID, Integer wStatus, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<IPTStandardBPM>> wResult = new ServiceResult<List<IPTStandardBPM>>();
		wResult.Result = new ArrayList<IPTStandardBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (wStatus) {
			case 1:
				wResult.Result.addAll(IPTStandardBPMDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1, wStartTime,
						wEndTime, new ArrayList<Integer>(Arrays.asList(20, 21, 22)), wErrorCode));
				break;
			case 0:
				wResult.Result.addAll(IPTStandardBPMDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1, null, null,
						new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), wErrorCode));
				break;
			default:
				wResult.Result.addAll(IPTStandardBPMDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1, null, null,
						new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), wErrorCode));
				wResult.Result.addAll(IPTStandardBPMDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1, wStartTime,
						wEndTime, new ArrayList<Integer>(Arrays.asList(20, 21, 22)), wErrorCode));
				break;
			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			// 条件筛选数据
			if (wProductID > 0) {
				wResult.Result = wResult.Result.stream()
						.filter(p -> p.IPTStandard.stream().anyMatch(q -> q.ProductID == wProductID))
						.collect(Collectors.toList());
			}
			if (wLineID > 0) {
				wResult.Result = wResult.Result.stream()
						.filter(p -> p.IPTStandard.stream().anyMatch(q -> q.LineID == wLineID))
						.collect(Collectors.toList());
			}
			if (wPartID > 0) {
				wResult.Result = wResult.Result.stream()
						.filter(p -> p.IPTStandard.stream().anyMatch(q -> q.PartID == wPartID))
						.collect(Collectors.toList());
			}

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}

			// 待办数据处理
			List<BPMTaskBase> wBaseList = IPTStandardBPMDAO.getInstance().BPM_GetUndoTaskList(wLoginUser,
					wLoginUser.getID(), wErrorCode);
			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {
				if (!(wTaskBase instanceof IPTStandardBPM))
					continue;
				IPTStandardBPM wIPTStandardBPM = (IPTStandardBPM) wTaskBase;
				wIPTStandardBPM.TagTypes = TaskQueryType.ToHandle.getValue();
				for (int i = 0; i < wResult.Result.size(); i++) {
					if (wResult.Result.get(i).ID == wIPTStandardBPM.ID)
						wResult.Result.set(i, wIPTStandardBPM);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_BatchCopyStandard(BMSEmployee wLoginUser, int wProductID, int wLineID,
			int wPartID1, int wPartPoint1, int wPartID2, int wPartPoint2, boolean wNotCurrent) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// 复制工位所有工序内容
			if (wPartPoint1 <= 0 || wPartPoint2 <= 0) {
				CopyPartStandard(wLoginUser, wProductID, wLineID, wPartID1, wPartID2);
			}
			// 精确复制到工序
			else {
				CopyStepStandard(wLoginUser, wProductID, wLineID, wPartID1, wPartPoint1, wPartID2, wPartPoint2,
						wNotCurrent);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private void CopyStepStandard(BMSEmployee wLoginUser, int wProductID, int wLineID, int wPartID1, int wPartPoint1,
			int wPartID2, int wPartPoint2, boolean wNotCurrent) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<IPTStandard> wList = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, -1, -1,
					IPTMode.Default, -1, -1, -1, -1, -1, wLineID, wPartID1, wPartPoint1, -1, wProductID, "", -1,
					10000000, null, null, wErrorCode);
			for (IPTStandard wStandard : wList) {
				if (wNotCurrent) {
					wStandard.IsCurrent = 0;
				}
				// 复制到标准
				CopyToStandard(wLoginUser, wPartID2, wPartPoint2, wErrorCode, wStandard);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 复制标准到指定的工位、工序下
	 */
	private void CopyToStandard(BMSEmployee wLoginUser, int wPartID2, int wPartPoint2, OutResult<Integer> wErrorCode,
			IPTStandard wStandard) {
		try {
			long wOldID = wStandard.ID;
			wStandard.ID = 0;
			wStandard.PartID = wPartID2;
			wStandard.PartPointID = wPartPoint2;
			Long wNewID = IPTStandardDAO.getInstance().Update(wLoginUser, wStandard, wErrorCode);
			// ③查询项点
			List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wOldID, -1, -1, wErrorCode);
			// ④复制项点
			if (wItemList.size() > 0) {
				for (IPTItem wIPTItem : wItemList) {
					wIPTItem.ID = 0;
					wIPTItem.VID = wNewID.intValue();
					IPTItemDAO.getInstance().Update(wLoginUser, wNewID, wIPTItem, wErrorCode);
				}
			}
			// ⑤查询工具
			List<IPTTool> wToolList = IPTToolDAO.getInstance().SelectList(wLoginUser, -1, (int) wOldID, wErrorCode);
			// ⑥复制工具
			if (wToolList.size() > 0) {
				for (IPTTool wIPTTool : wToolList) {
					wIPTTool.ID = 0;
					wIPTTool.StandardID = wNewID.intValue();
					IPTToolDAO.getInstance().Update(wLoginUser, wIPTTool, wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private void CopyPartStandard(BMSEmployee wLoginUser, int wProductID, int wLineID, int wPartID1, int wPartID2) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<IPTStandard> wList = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, -1, -1,
					IPTMode.Default, -1, -1, -1, -1, -1, wLineID, wPartID1, -1, -1, wProductID, "", -1, 10000000, null,
					null, wErrorCode);
			for (IPTStandard wStandard : wList) {
				// 复制到标准
				CopyToStandard(wLoginUser, wPartID2, wStandard.PartPointID, wErrorCode, wStandard);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<Integer> IPT_ImportOrder(BMSEmployee wLoginUser, ExcelData result) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			ExcelSheetData wSheetData = result.sheetData.get(0);

			int wIndex = 0;
			for (ExcelLineData wExcelLineData : wSheetData.lineData) {
				if (wIndex == 0) {
					wIndex++;
					continue;
				}

				List<String> wValueList = wExcelLineData.colData;

				// ①获取WBSNo
				String wWBSNo = wValueList.get(1);
				// ②获取车型
				String wProductNo = wValueList.get(2);
				// ③获取车号
				String wNo = wValueList.get(3);
				// ④获取实际进厂时间
				String wInTime = wValueList.get(4);
				Calendar wTime = StringUtils.parseCalendar(wInTime);
				// ⑤获取实际完工时间
				String wFinishTime = wValueList.get(5);
				Calendar wFiTime = StringUtils.parseCalendar(wFinishTime);
				// ⑥根据车号找订单ID
				String wPartNo = StringUtils.Format("{0}#{1}", wProductNo, wNo);
				int wOrderID = SFCOrderFormDAO.getInstance().SelectOrderIDByPartNo(wLoginUser, wPartNo, wErrorCode);
				// ⑦根据ID修改订单的WBS，进厂时间，完工时间
				if (wOrderID > 0) {
					OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
							.Info(OMSOrder.class);
					wOrder.OrderNo = wWBSNo;
					wOrder.RealReceiveDate = wTime;
					wOrder.RealFinishDate = wFiTime;

					SFCOrderFormDAO.getInstance().UpdateOrder(wLoginUser, wOrder, wErrorCode);
				}
				wIndex++;
				System.out.println(wIndex);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> IPT_GetMaterialSQL(BMSEmployee wLoginUser, ExcelData result) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<String> wIDList = new ArrayList<String>();

			ExcelSheetData wSheet = result.sheetData.get(0);

			int wIndex = 0;
			for (ExcelLineData wExcelLineData : wSheet.lineData) {
				if (wIndex == 0) {
					wIndex++;
					continue;
				}
				wIDList.add(wExcelLineData.colData.get(0).replace(".0", ""));
			}

			wResult.Result = StringUtils.Format(
					"select t1.ID,t1.PartNo,t2.name as partname,t2.code as partcode,"
							+ "t3.name,t1.MaterialNo,t4.materialname,t1.Number,t1.ReplaceType,t1.OutsourceType "
							+ "from {1}.aps_bomitem t1,{1}.fpc_part t2,{1}.fpc_partpoint t3,"
							+ "{1}.mss_material t4 where t1.partid=t2.id "
							+ "and t1.partpointid=t3.id and t1.materialid=t4.id and t1.id in ({0});",
					StringUtils.Join(",", wIDList), MESDBSource.Basic.getDBName());

			System.out.println(wResult.Result);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTStandard>> IPT_QueryStepVersionAllPro(BMSEmployee wLoginUser, int wLineID,
			int wProductID, int wPartID, String wStepName) {
		ServiceResult<List<IPTStandard>> wResult = new ServiceResult<List<IPTStandard>>();
		wResult.Result = new ArrayList<IPTStandard>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①根据修程、工位查询工序列表
			List<FMCLineUnit> wStepUnitList = IPTStandardDAO.getInstance().FMC_QueryStepUnitList(wLoginUser, wLineID,
					wProductID, wPartID, wStepName, wErrorCode);
			if (wStepUnitList == null || wStepUnitList.size() <= 0) {
				return wResult;
			}
			// ③遍历工序列表，依次添加或构造假版本
			IPTStandard wIPTStandard;
			for (FMCLineUnit wFMCLineUnit : wStepUnitList) {

				List<IPTStandard> wList = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
						wLoginUser.CompanyID, IPTMode.Default, 0, -1, -1, -1, -1, wLineID, wFMCLineUnit.ParentUnitID,
						wFMCLineUnit.UnitID, -1, wProductID, "", 1000, null, null, wErrorCode).Result;

				if (wList != null && wList.size() > 0) {
					wResult.Result.addAll(wList);
				} else {
					wIPTStandard = new IPTStandard();
					wIPTStandard.LineID = wLineID;
					wIPTStandard.PartID = wFMCLineUnit.ParentUnitID;
					wIPTStandard.PartPointID = wFMCLineUnit.UnitID;
					wIPTStandard.ProductID = wProductID;
					wResult.Result.add(wIPTStandard);
				}
			}
			// 翻译
			if (wResult.Result != null && wResult.Result.size() > 0) {
				for (IPTStandard wItem : wResult.Result) {
					wItem.LineName = QMSConstants.GetFMCLineName(wItem.LineID);
					wItem.PartName = QMSConstants.GetFPCPartName(wItem.PartID);
					wItem.PartPointName = QMSConstants.GetFPCStepName(wItem.PartPointID);
					wItem.ProductNo = QMSConstants.GetFPCProductNo(wItem.ProductID);
					wItem.CustomName = QMSConstants.GetCRMCustomerName(wItem.CustomID);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}
}
