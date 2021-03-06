package com.mes.qms.server.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.mesenum.APSShiftPeriod;
import com.mes.qms.server.service.mesenum.APSTaskStatus;
import com.mes.qms.server.service.mesenum.BFCMessageStatus;
import com.mes.qms.server.service.mesenum.BFCMessageType;
import com.mes.qms.server.service.mesenum.BMSDepartmentType;
import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.BPMStatus;
import com.mes.qms.server.service.mesenum.FMCShiftLevel;
import com.mes.qms.server.service.mesenum.FPCPartTypes;
import com.mes.qms.server.service.mesenum.IPTItemType;
import com.mes.qms.server.service.mesenum.IPTMode;
import com.mes.qms.server.service.mesenum.IPTPreCheckProblemStatus;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.MSSOperateType;
import com.mes.qms.server.service.mesenum.MSSSpotStatus;
import com.mes.qms.server.service.mesenum.OMSOrderStatus;
import com.mes.qms.server.service.mesenum.RSMTurnOrderTaskStatus;
import com.mes.qms.server.service.mesenum.SCHSecondStatus;
import com.mes.qms.server.service.mesenum.SFCBogiesChangeBPMStatus;
import com.mes.qms.server.service.mesenum.SFCLetPassBPMStatus;
import com.mes.qms.server.service.mesenum.SFCLoginType;
import com.mes.qms.server.service.mesenum.SFCOrderFormType;
import com.mes.qms.server.service.mesenum.SFCReturnOverMaterialStatus;
import com.mes.qms.server.service.mesenum.SFCTaskMode;
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
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.bms.BMSDepartment;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bms.BMSWorkCharge;
import com.mes.qms.server.service.po.bpm.BPMActivitiHisTask;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.cfg.CFGCalendar;
import com.mes.qms.server.service.po.fmc.FMCItem;
import com.mes.qms.server.service.po.focas.FocasHistoryData;
import com.mes.qms.server.service.po.focas.FocasMessageItem;
import com.mes.qms.server.service.po.focas.FocasMessageResult;
import com.mes.qms.server.service.po.focas.FocasPart;
import com.mes.qms.server.service.po.focas.FocasReport;
import com.mes.qms.server.service.po.focas.FocasResult;
import com.mes.qms.server.service.po.fpc.FPCCommonFile;
import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.po.ipt.IPTExport;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTItemExport;
import com.mes.qms.server.service.po.ipt.IPTPreCheckItem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckReport;
import com.mes.qms.server.service.po.ipt.IPTStandard;
import com.mes.qms.server.service.po.ipt.IPTTool;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.lfs.LFSWorkAreaChecker;
import com.mes.qms.server.service.po.lfs.LFSWorkAreaStation;
import com.mes.qms.server.service.po.mss.MSSPartConfig;
import com.mes.qms.server.service.po.mss.MSSPartItem;
import com.mes.qms.server.service.po.mss.MSSPartRecord;
import com.mes.qms.server.service.po.mss.MSSRepairRecord;
import com.mes.qms.server.service.po.mss.MSSSpotTask;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.pic.SFCCarInfo;
import com.mes.qms.server.service.po.pic.SFCPartInfo;
import com.mes.qms.server.service.po.pic.SFCProgress;
import com.mes.qms.server.service.po.pic.SFCRankInfo;
import com.mes.qms.server.service.po.pic.SFCUploadPic;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.qms.server.service.po.sch.SCHSecondmentApply;
import com.mes.qms.server.service.po.sfc.SFCBogiesChangeBPM;
import com.mes.qms.server.service.po.sfc.SFCIPTItem;
import com.mes.qms.server.service.po.sfc.SFCLetPassBPM;
import com.mes.qms.server.service.po.sfc.SFCLoginEvent;
import com.mes.qms.server.service.po.sfc.SFCLoginEventPart;
import com.mes.qms.server.service.po.sfc.SFCOrderForm;
import com.mes.qms.server.service.po.sfc.SFCReturnOverMaterial;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTInfo;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTPart;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTPartNo;
import com.mes.qms.server.service.po.sfc.SFCTaskRecord;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.po.sfc.SFCTemporaryExamination;
import com.mes.qms.server.service.po.sfc.SFCTemporaryExaminationPartItem;
import com.mes.qms.server.service.po.sfc.SFCTemporaryExaminationStepItem;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress01;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress02;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress03;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.Configuration;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.aps.APSBOMItemDAO;
import com.mes.qms.server.serviceimpl.dao.focas.FocasHistoryDataDAO;
import com.mes.qms.server.serviceimpl.dao.fpc.FPCCommonFileDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTItemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTPreCheckProblemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTStandardBPMDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTStandardDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTToolDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTValueDAO;
import com.mes.qms.server.serviceimpl.dao.mss.MSSPartRecordDAO;
import com.mes.qms.server.serviceimpl.dao.mss.MSSRepairRecordDAO;
import com.mes.qms.server.serviceimpl.dao.rsm.RSMTurnOrderTaskDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCBogiesChangeBPMDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCCarInfoDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCLetPassBPMDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCLoginEventDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCOrderFormDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCPartInfoDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCRankInfoDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCReturnOverMaterialDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTaskIPTDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTaskRecordDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTemporaryExaminationDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCUploadPicDAO;
import com.mes.qms.server.serviceimpl.utils.MESServer;
import com.mes.qms.server.serviceimpl.utils.qms.QMSUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import com.mes.qms.server.shristool.LoggerTool;
import com.mes.qms.server.utils.Constants;
import com.mes.qms.server.utils.qms.ExcelUtil;
import com.mes.qms.server.utils.qms.MESFileUtils;
import com.mes.qms.server.utils.qms.ZipUtils;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-2-21 13:43:25
 * @LastEditTime 2020-2-21 13:43:29
 *
 */
@Service
public class SFCServiceImpl implements SFCService {
	private static Logger logger = LoggerFactory.getLogger(SFCServiceImpl.class);

	public SFCServiceImpl() {
	}

	private static SFCService Instance;

	public static SFCService getInstance() {
		if (Instance == null)
			Instance = new SFCServiceImpl();
		return Instance;
	}

	@Override
	public ServiceResult<SFCLoginEvent> SFC_QueryLoginEvent(BMSEmployee wLoginUser, int wID) {
		ServiceResult<SFCLoginEvent> wResult = new ServiceResult<SFCLoginEvent>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = SFCLoginEventDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCLoginEvent>> SFC_QueryLoginEventList(BMSEmployee wLoginUser, int wWorkShopID,
			int wStationID, int wModuleID, Calendar wTime, int wActive, int wType) {
		ServiceResult<List<SFCLoginEvent>> wResult = new ServiceResult<List<SFCLoginEvent>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			int wShiftID = QMSUtils.getInstance().GetDayShiftID(wLoginUser, wTime);

			wResult.Result = SFCLoginEventDAO.getInstance().SelectList(wLoginUser, -1, wWorkShopID, wStationID, -1,
					wModuleID, wShiftID, wActive, wType, -1, wErrorCode);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Long> SFC_UpdateLoginEvent(BMSEmployee wLoginUser, SFCLoginEvent wSFCLoginEvent) {
		ServiceResult<Long> wResult = new ServiceResult<Long>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = (long) SFCLoginEventDAO.getInstance().Update(wLoginUser, wSFCLoginEvent, wErrorCode);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_ActiveLoginEventList(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult = SFCLoginEventDAO.getInstance().Active(wLoginUser, wIDList, wActive, wErrorCode);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCLoginEvent>> SFC_QueryLoginEventListByEmployee(BMSEmployee wLoginUser,
			Calendar wDate) {
		ServiceResult<List<SFCLoginEvent>> wResult = new ServiceResult<List<SFCLoginEvent>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1);

			int wShiftID = -1;
			if (wDate != null && wDate.compareTo(wBaseTime) > 0) {
				wShiftID = QMSUtils.getInstance().GetDayShiftID(wLoginUser, wDate);
			}

			wResult.Result = SFCLoginEventDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, wLoginUser.ID, -1,
					wShiftID, 1, -1, -1, wErrorCode);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<Integer> SFC_Clock(BMSEmployee wLoginUser, int wType, List<SFCTaskStep> wDataList,
			int wAPPEventID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			if (wDataList == null || wDataList.size() <= 0) {
				return wResult;
			}

			// ?????????????????????????????????
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

			long startTime = System.currentTimeMillis();

			ServiceResult<IPTStandard> wRst = null;
			for (SFCTaskStep wSFCTaskStep : wDataList) {
				if (wSFCTaskStep.Type == SFCTaskStepType.Step.getValue()) {
					wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.QTXJ, -1, -1,
							-1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
							wSFCTaskStep.ProductID, wErrorCode);
					if (wRst == null || wRst.Result == null || wRst.Result.ID <= 0) {
						wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.PreCheck,
								-1, -1, -1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
								wSFCTaskStep.ProductID, wErrorCode);
						if (wRst == null || wRst.Result == null || wRst.Result.ID <= 0) {
							wResult.FaultCode += StringUtils.Format("???{0}?????????????????????????????????????????????????????????????????????!",
									wSFCTaskStep.PartPointName);
							return wResult;
						}
					}
				} else if (wSFCTaskStep.Type == SFCTaskStepType.Quality.getValue()) {
					wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.Quality, -1, -1,
							-1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
							wSFCTaskStep.ProductID, wErrorCode);
					if (wRst == null || wRst.Result == null || wRst.Result.ID <= 0) {
						wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.OutCheck,
								wSFCTaskStep.CustomerID, -1, -1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID,
								wSFCTaskStep.StepID, -1, wSFCTaskStep.ProductID, wErrorCode);
						if (wRst == null || wRst.Result == null || wRst.Result.ID <= 0) {
							wResult.FaultCode += StringUtils.Format("???{0}?????????????????????????????????????????????????????????????????????!",
									wSFCTaskStep.PartPointName);
							return wResult;
						}
					}
				}
			}

			long endTime = System.currentTimeMillis();
			logger.info("??????????????????????????? " + (endTime - startTime) + "ms");

			if (wDataList == null || wDataList.size() <= 0)
				return wResult;
			int wShiftID = QMSUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance());

			String wMsg = "";
			for (SFCTaskStep wSFCTaskStep : wDataList) {

				startTime = System.currentTimeMillis();

				wMsg = this.SFC_ClockTask(wLoginUser, wType, wErrorCode, wShiftID, wSFCTaskStep, wHolidayList, wMinHour,
						wMaxHour);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode += wMsg;
					return wResult;
				}

				endTime = System.currentTimeMillis();
				logger.info("SFC_ClockTask??? " + (endTime - startTime) + "ms");

				startTime = System.currentTimeMillis();

				// ????????????
				if (wSFCTaskStep.Type == SFCTaskStepType.Step.getValue()) {
					// ??????
					wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.QTXJ, -1, -1,
							-1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
							wSFCTaskStep.ProductID, wErrorCode);
					if (wRst != null && wRst.Result != null && wRst.Result.ID > 0) {
						int wSFCTaskType = SFCTaskType.SelfCheck.getValue();

						int wIPTID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
								wSFCTaskType, wRst.Result.ID, SFCTaskStepType.Step.getValue(), 0);
						// ??????????????????????????????????????????????????????????????????????????????????????????
						if (wIPTID > 0) {
							SFC_SendMessageToClockerBySelfCheck(wLoginUser, wIPTID, wSFCTaskStep);
						}
					}
					// ??????
					wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.PreCheck, -1,
							-1, -1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
							wSFCTaskStep.ProductID, wErrorCode);
					if (wRst != null && wRst.Result != null && wRst.Result.ID > 0) {
						int wSFCTaskType = SFCTaskType.PreCheck.getValue();

						// ??????????????????????????????
						int wPeriodChangeStandard = 0;
						wPeriodChangeStandard = AsignPeriodChangeStandard(wLoginUser, wErrorCode, wSFCTaskStep,
								wPeriodChangeStandard);

						int wNewID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
								wSFCTaskType, wRst.Result.ID, SFCTaskStepType.Step.getValue(), wPeriodChangeStandard);

						// ?????????????????????????????????????????????????????????????????????????????????????????????
						SFC_SendMessageToMonitorAndOperatorWhenPreTaskCreated(wLoginUser, wNewID, wSFCTaskStep, "??????",
								BPMEventModule.PreCheck);
					}
				} else if (wSFCTaskStep.Type == SFCTaskStepType.Question.getValue()) {
					int wSFCTaskType = SFCTaskType.SelfCheck.getValue();
					int wIPTID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
							wSFCTaskType, 0, SFCTaskStepType.Question.getValue(), 0);
					if (wIPTID > 0) {
						// ????????????
//						SFC_SendMessageToMonitorAndOperatorWhenPreTaskCreated(wLoginUser, wIPTID, wSFCTaskStep, "???????????????",
//								BPMEventModule.PreProblemHandle);
					}
				} else if (wSFCTaskStep.Type == SFCTaskStepType.Quality.getValue()) {
					FPCPart wPart = QMSConstants.GetFPCPart(wSFCTaskStep.PartID);
					if (wPart != null && wPart.ID > 0) {
						if (wPart.PartType == FPCPartTypes.OutFactory.getValue()) {
							wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0,
									IPTMode.OutCheck, wSFCTaskStep.CustomerID, -1, -1, -1, -1, wSFCTaskStep.LineID,
									wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1, wSFCTaskStep.ProductID, wErrorCode);
							// ???????????????
							int wSFCTaskType = SFCTaskType.OutPlant.getValue();
							int wNewID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
									wSFCTaskType, wRst.Result.ID, SFCTaskStepType.Quality.getValue(), 0);

							SFC_SendMessageToMonitorAndOperatorWhenPreTaskCreated(wLoginUser, wNewID, wSFCTaskStep,
									"?????????", BPMEventModule.OutCheck);
						} else if (wPart.PartType == FPCPartTypes.QTFinally.getValue()) {
							wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.Quality,
									-1, -1, -1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID,
									-1, wSFCTaskStep.ProductID, wErrorCode);
							// ????????????
							int wSFCTaskType = SFCTaskType.Final.getValue();
							int wNewID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
									wSFCTaskType, wRst.Result.ID, SFCTaskStepType.Quality.getValue(), 0);

							SFC_SendMessageToMonitorAndOperatorWhenPreTaskCreated(wLoginUser, wNewID, wSFCTaskStep,
									"??????", BPMEventModule.FinalCheck);
						}
					}
				}

				endTime = System.currentTimeMillis();
				logger.info("??????????????? " + (endTime - startTime) + "ms");
			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param wLoginUser
	 * @param wErrorCode
	 * @param wSFCTaskStep
	 * @param wPeriodChangeStandard
	 * @return
	 */
	private int AsignPeriodChangeStandard(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode,
			SFCTaskStep wSFCTaskStep, int wPeriodChangeStandard) {
		try {
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTaskStep.OrderID)
					.Info(OMSOrder.class);
			int wCustomerID = wOrder.CustomerID;
			IPTStandard wStandart = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0,
					IPTMode.PeriodChange, wCustomerID, -1, -1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID,
					wSFCTaskStep.StepID, -1, wSFCTaskStep.ProductID, wErrorCode).Result;
			if (wStandart != null && wStandart.ID > 0) {
				wPeriodChangeStandard = (int) wStandart.ID;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wPeriodChangeStandard;
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wNewID
	 */
	private void SFC_SendMessageToMonitorAndOperatorWhenPreTaskCreated(BMSEmployee wLoginUser, int wNewID,
			SFCTaskStep wSFCTaskStep, String wTaskName, BPMEventModule wBPMEventModule) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wNewID <= 0) {
				return;
			}

			List<BFCMessage> wBFCMessageList = new ArrayList<>();

			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);

			BFCMessage wMessage = new BFCMessage();
			wMessage.Active = 0;
			wMessage.CompanyID = 0;
			wMessage.CreateTime = Calendar.getInstance();
			wMessage.EditTime = Calendar.getInstance();
			wMessage.ID = 0L;
			wMessage.MessageID = wNewID;

			if (wBPMEventModule == BPMEventModule.PreProblemHandle) {
				String wStepName = "";
				IPTPreCheckProblem wProblemInfo = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
						wSFCTaskStep.TaskStepID, wErrorCode);
				if (wProblemInfo != null && wProblemInfo.ID > 0) {
					wStepName = wProblemInfo.IPTItemName;
				}
				wMessage.MessageText = StringUtils.Format("???{0}??? {1}???????????????{3}?????????{2}???",
						new Object[] { wBPMEventModule.getLable(), wLoginUser.Name, wStepName, wTaskName });
			} else {
				wMessage.MessageText = StringUtils.Format("???{0}??? {1}???????????????{3}?????????{2}???",
						new Object[] { wBPMEventModule.getLable(), wLoginUser.Name,
								QMSConstants.GetFPCStepName(wSFCTaskStep.StepID), wTaskName });
			}

			wMessage.ModuleID = wBPMEventModule.getValue();
			wMessage.ResponsorID = wSFCTaskStep.OperatorID;
			wMessage.ShiftID = wShiftID;
			wMessage.StationID = wSFCTaskStep.PartID;
			wMessage.Title = StringUtils.Format("{0} {1} {2}", new Object[] { wBPMEventModule.getLable(),
					QMSConstants.GetFPCStepName(wSFCTaskStep.StepID), wSFCTaskStep.PartNo });
			wMessage.Type = BFCMessageType.Task.getValue();
			wBFCMessageList.add(wMessage);

			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wIPTID
	 * @param wSFCTaskStep
	 */
	private void SFC_SendMessageToClockerBySelfCheck(BMSEmployee wLoginUser, int wIPTID, SFCTaskStep wSFCTaskStep) {
		try {
			if (wIPTID <= 0) {
				return;
			}

			// ?????????????????????
			ExecutorService wES = Executors.newFixedThreadPool(1);
			wES.submit(() -> UpdateSpecialTaskCheckerList(wIPTID));
			wES.shutdown();

			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);
			List<BFCMessage> wMessageList = new ArrayList<>();

			APSTaskStep wInfo = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepByID(wLoginUser, wSFCTaskStep.TaskStepID).Info(APSTaskStep.class);

			List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(wLoginUser, wInfo.ID, -1, -1).List(SFCTaskStep.class);

			for (SFCTaskStep wItem : wSFCTaskStepList) {
				BFCMessage wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0L;
				wMessage.MessageID = wIPTID;
				wMessage.Title = StringUtils.Format("?????? {0} {1}",
						new Object[] { wSFCTaskStep.PartPointName, wSFCTaskStep.PartNo });
				wMessage.MessageText = StringUtils.Format("???{0}??? {1}????????????????????????????????????{2}???",
						new Object[] { BPMEventModule.SCZJ.getLable(), wLoginUser.Name, wSFCTaskStep.PartPointName });
				wMessage.ModuleID = BPMEventModule.SCZJ.getValue();
				wMessage.ResponsorID = wItem.OperatorID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = 0L;
				wMessage.Type = BFCMessageType.Task.getValue();
				wMessageList.add(wMessage);
			}

			logger.info(String.valueOf(wSFCTaskStepList.size()));
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private int SFC_CreateTask(BMSEmployee wLoginUser, int wAPPEventID, OutResult<Integer> wErrorCode, int wShiftID,
			SFCTaskStep wSFCTaskStep, int wSFCTaskType, long wStandardID, int wType, int wPeriodChangeStandard) {
		int wResult = 0;
		try {
			List<SFCTaskIPT> wList;
			SFCTaskIPT wTaskIPT;
			// ????????????????????????????????????????????????
			wList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskStep.TaskStepID, wSFCTaskType, -1,
					-1, -1, null, wType, null, null, wErrorCode);
			// ??????????????????
			if (wList.size() <= 0) {
				wTaskIPT = new SFCTaskIPT();
				wTaskIPT.ActiveTime = Calendar.getInstance();
				wTaskIPT.EventID = wAPPEventID;
				wTaskIPT.FQTYBad = 0;
				wTaskIPT.FQTYGood = 0;
				wTaskIPT.ID = 0;
				wTaskIPT.ItemList = new ArrayList<SFCIPTItem>();
				wTaskIPT.LineID = wSFCTaskStep.LineID;
				wTaskIPT.ModuleVersionID = 0;
				wTaskIPT.OperatorID = wSFCTaskStep.OperatorID;
				wTaskIPT.OrderNo = wSFCTaskStep.OrderNo;
				wTaskIPT.ProductID = wSFCTaskStep.ProductID;
				wTaskIPT.PartPointID = wSFCTaskStep.StepID;
				wTaskIPT.StationID = wSFCTaskStep.PartID;
				wTaskIPT.Result = 0;
				wTaskIPT.ShiftID = wShiftID;
				wTaskIPT.Status = SFCTaskStatus.Active.getValue();
				wTaskIPT.SubmitTime = Calendar.getInstance();
				wTaskIPT.TaskMode = SFCTaskMode.FreeShift.getValue();
				wTaskIPT.TaskStepID = wSFCTaskStep.TaskStepID;
				wTaskIPT.TaskType = wSFCTaskType;
				wTaskIPT.Times = 1;
				wTaskIPT.WorkShopID = 0;
				wTaskIPT.ModuleVersionID = (int) wStandardID;
				wTaskIPT.OrderID = wSFCTaskStep.OrderID;
				wTaskIPT.PartNo = wSFCTaskStep.PartNo;
				wTaskIPT.OrderNo = wSFCTaskStep.OrderNo;
				wTaskIPT.Type = wType;
				wTaskIPT.PeriodChangeStandard = wPeriodChangeStandard;
				wTaskIPT.StartTime = Calendar.getInstance();
				wResult = SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
			}
		} catch (Exception ex) {
			LoggerTool.SaveException("SFCIPTDAO", "CreateTask", "Function Exception:" + ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????ID????????????????????????????????????????????????????????????
	 * 
	 * @param wAPSTaskStepID
	 * @param wTaskType
	 * @return
	 */
	public List<Object> SFC_QueryToDoAndDoneIPTItemList(BMSEmployee wLoginUser, int wAPSTaskStepID, int wTaskType) {
		List<Object> wResult = new ArrayList<Object>();
		try {
			List<IPTItem> wToDoList = new ArrayList<IPTItem>();
			List<IPTItem> wDoneList = new ArrayList<IPTItem>();
			List<IPTValue> wValueList = new ArrayList<IPTValue>();

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			int wSFCTaskStepType = SFCTaskStepType.Step.getValue();
			if (wTaskType == SFCTaskType.OutPlant.getValue() || wTaskType == SFCTaskType.Final.getValue()) {
				wSFCTaskStepType = SFCTaskStepType.Quality.getValue();
			}

			List<SFCTaskIPT> wIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wAPSTaskStepID,
					wTaskType, -1, -1, -1, null, wSFCTaskStepType, null, null, wErrorCode);

			if (wIPTList == null || wIPTList.size() <= 0) {
				wResult.add(wToDoList);
				wResult.add(wDoneList);
				wResult.add(wValueList);
				return wResult;
			}

			SFCTaskIPT wSFCTaskIPT = wIPTList.get(0);

			if (wSFCTaskIPT.ModuleVersionID <= 0) {
				wResult.add(wToDoList);
				wResult.add(wDoneList);
				wResult.add(wValueList);
				return wResult;
			}

			List<Long> wStandardIDList = new ArrayList<Long>(Arrays.asList((long) wSFCTaskIPT.ModuleVersionID));
			if (wTaskType == SFCTaskType.PreCheck.getValue() && wSFCTaskIPT.PeriodChangeStandard > 0) {
				wStandardIDList.add((long) wSFCTaskIPT.PeriodChangeStandard);
			}

			Map<Long, List<IPTItem>> wItemListMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser, wStandardIDList,
					wErrorCode).Result;

			if (wItemListMap == null || wItemListMap.size() <= 0
					|| !wItemListMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
				wResult.add(wToDoList);
				wResult.add(wDoneList);
				wResult.add(wValueList);
				return wResult;
			}

			// ?????????
			List<IPTItem> wIPTItemList = wItemListMap.get((long) wSFCTaskIPT.ModuleVersionID);
			// ???????????????????????????
			if (wTaskType == SFCTaskType.PreCheck.getValue() && wSFCTaskIPT.PeriodChangeStandard > 0
					&& wItemListMap.containsKey((long) wSFCTaskIPT.PeriodChangeStandard)) {
				AddPeriodChangeItems(wSFCTaskIPT, wItemListMap, wIPTItemList);
			}
			// ?????????
			List<IPTItem> wGroupList = wIPTItemList.stream().filter(p -> p.ItemType == IPTItemType.Group.getValue())
					.collect(Collectors.toList());
			// ??????????????????
			wIPTItemList.removeIf(p -> p.ItemType == IPTItemType.Group.getValue() || p.Active == 2);

			// ???????????????????????????????????????
			List<SFCTaskIPT> wTaskIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
					wSFCTaskIPT.TaskStepID, -1, -1, -1, -1, null, SFCTaskStepType.Step.getValue(), null, null,
					wErrorCode);

			SFCTaskIPT wSelfIPT = null;
			SFCTaskIPT wMutualIPT = null;
			SFCTaskIPT wSpecialIPT = null;
			List<IPTValue> wSelfValueList = new ArrayList<IPTValue>();
			List<IPTValue> wMutualValueList = new ArrayList<IPTValue>();
			List<IPTValue> wSpecilaValueList = new ArrayList<IPTValue>();
			List<IPTValue> wPreOrOrOutValueList = new ArrayList<IPTValue>();

			Optional<SFCTaskIPT> wOption = wTaskIPTList.stream()
					.filter(p -> p.TaskType == SFCTaskType.SelfCheck.getValue()).findFirst();
			if (wOption.isPresent()) {
				wSelfIPT = wOption.get();
			}

			wOption = wTaskIPTList.stream().filter(p -> p.TaskType == SFCTaskType.MutualCheck.getValue()).findFirst();
			if (wOption.isPresent()) {
				wMutualIPT = wOption.get();
			}

			wOption = wTaskIPTList.stream().filter(p -> p.TaskType == SFCTaskType.SpecialCheck.getValue()).findFirst();
			if (wOption.isPresent()) {
				wSpecialIPT = wOption.get();
			}

			if (wSelfIPT != null && wSelfIPT.ID > 0) {
				wSelfValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSelfIPT.ID, -1, -1,
						wErrorCode).Result;
			}

			if (wMutualIPT != null && wMutualIPT.ID > 0) {
				wMutualValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wMutualIPT.ID, -1, -1,
						wErrorCode).Result;
			}

			if (wSpecialIPT != null && wSpecialIPT.ID > 0) {
				wSpecilaValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSpecialIPT.ID, -1, -1,
						wErrorCode).Result;
			}

			if (wSFCTaskIPT != null && wSFCTaskIPT.ID > 0) {
				wPreOrOrOutValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSFCTaskIPT.ID, -1, -1,
						wErrorCode).Result;
			}

			// ?????????????????????????????????
			for (IPTItem wIPTItem : wIPTItemList) {
				switch (SFCTaskType.getEnumType(wTaskType)) {
				case SelfCheck:// ??????
					// ????????????????????????????????????
					this.SetSelfCheckToDoAndDone(wToDoList, wDoneList, wSelfValueList, wMutualValueList, wIPTItem);
					break;
				case MutualCheck:// ??????
					// ????????????????????????????????????
					this.SetMutualCheckToDoAndDone(wToDoList, wDoneList, wSelfValueList, wMutualValueList, wIPTItem);
					break;
				case SpecialCheck:// ??????
					// ????????????????????????????????????
					this.SetSpecialCheckToDoAndDone(wToDoList, wDoneList, wMutualValueList, wSpecilaValueList,
							wIPTItem);
					break;
				case Final:// ??????
				case OutPlant:// ?????????
				case PreCheck:// ??????
					// ????????????????????????????????????
					this.SetPreCheckToDoAndDone(wToDoList, wDoneList, wPreOrOrOutValueList, wIPTItem);
					break;
				default:
					break;
				}
			}

			// ???????????????
			this.FillValueList(wTaskType, wToDoList, wDoneList, wValueList, wSelfValueList, wMutualValueList,
					wSpecilaValueList, wPreOrOrOutValueList);

			// ?????????????????????????????????
			if (wToDoList != null && wToDoList.size() > 0) {
				List<IPTItem> wFatherList = this.AddGroup(wGroupList, wToDoList);
				if (wFatherList != null && wFatherList.size() > 0) {
					wToDoList.addAll(wFatherList);
				}
				// ??????
				wToDoList = new ArrayList<IPTItem>(wToDoList.stream()
						.collect(Collectors.toMap(IPTItem::getID, account -> account, (k1, k2) -> k2)).values());
			}
			if (wDoneList != null && wDoneList.size() > 0) {
				List<IPTItem> wFatherList = this.AddGroup(wGroupList, wDoneList);
				if (wFatherList != null && wFatherList.size() > 0) {
					wDoneList.addAll(wFatherList);
				}
				// ??????
				wDoneList = new ArrayList<IPTItem>(wDoneList.stream()
						.collect(Collectors.toMap(IPTItem::getID, account -> account, (k1, k2) -> k2)).values());
			}

			// ????????????????????????
			this.HandlePerson(wToDoList, wDoneList, wValueList, wTaskType);

			// ??????????????????(??????????????????)
			this.AddMuturPerson(wLoginUser, wToDoList, wDoneList, wTaskType, wAPSTaskStepID);

			wResult.add(wToDoList);
			wResult.add(wDoneList);
			wResult.add(wValueList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????
	 */
	private void AddMuturPerson(BMSEmployee wLoginUser, List<IPTItem> wToDoList, List<IPTItem> wDoneList, int wTaskType,
			int wAPSTaskStepID) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wTaskType != SFCTaskType.SelfCheck.getValue()) {
				return;
			}

			APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance().APS_QueryTaskStepByID(wLoginUser, wAPSTaskStepID)
					.Info(APSTaskStep.class);
			if (wTaskStep == null || wTaskStep.ID <= 0) {
				return;
			}

			String wNames = "";

			if (wTaskStep.OperatorList.size() == 1) {
				wNames = SFCTaskIPTDAO.getInstance().GetMonitorsByAPSTaskStepID(wLoginUser, wAPSTaskStepID, wErrorCode);
			} else if (wTaskStep.OperatorList.size() > 1) {
				List<BMSEmployee> wEList = QMSConstants.GetBMSEmployeeList().values().stream()
						.filter(p -> wTaskStep.OperatorList.stream().anyMatch(q -> q == p.ID))
						.collect(Collectors.toList());
				wNames = StringUtils.Join(",", wEList.stream().map(p -> p.Name).collect(Collectors.toList()));
			}

			for (IPTItem wIPTItem : wToDoList) {
				wIPTItem.Mutualer = wNames;
			}

			for (IPTItem wIPTItem : wDoneList) {
				wIPTItem.Mutualer = wNames;
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????
	 */
	private void AddMuturPerson(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

//			if (wTaskIPT.TaskType != SFCTaskType.SelfCheck.getValue()
//					&& wTaskIPT.TaskType != SFCTaskType.MutualCheck.getValue()) {
//				return;
//			}

			APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepByID(wLoginUser, wTaskIPT.TaskStepID).Info(APSTaskStep.class);
			if (wTaskStep == null || wTaskStep.ID <= 0) {
				return;
			}

			String wNames = "";

			if (wTaskStep.OperatorList.size() == 1) {
				wNames = SFCTaskIPTDAO.getInstance().GetMonitorsByAPSTaskStepID(wLoginUser, wTaskIPT.TaskStepID,
						wErrorCode);
			} else if (wTaskStep.OperatorList.size() > 1) {
				List<BMSEmployee> wEList = QMSConstants.GetBMSEmployeeList().values().stream()
						.filter(p -> wTaskStep.OperatorList.stream().anyMatch(q -> q == p.ID))
						.collect(Collectors.toList());
				wNames = StringUtils.Join(",", wEList.stream().map(p -> p.Name).collect(Collectors.toList()));
			}

			wTaskIPT.Mutualer = wNames;
			wTaskIPT.DutyPerson = wNames;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????
	 */
	private void HandlePerson(List<IPTItem> wToDoList, List<IPTItem> wDoneList, List<IPTValue> wValueList,
			int wTaskType) {
		try {
			switch (SFCTaskType.getEnumType(wTaskType)) {
			case SelfCheck:
			case MutualCheck:
			case SpecialCheck:
				SetPeople(wToDoList, wDoneList, wValueList);
				break;
			case PreCheck:
			case Final:
			case OutPlant:
				SetPeople(wDoneList, wValueList, wTaskType);
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private void SetPeople(List<IPTItem> wDoneList, List<IPTValue> wValueList, int wTaskType) {
		try {
			if (wDoneList == null || wDoneList.size() <= 0) {
				return;
			}

			for (IPTItem wItem : wDoneList) {
				// ????????????
				if (wValueList != null && wValueList.size() > 0
						&& wValueList.stream().anyMatch(p -> p.IPTItemID == wItem.ID && p.IPTMode == wTaskType)) {
					List<IPTValue> wList = wValueList.stream()
							.filter(p -> p.IPTItemID == wItem.ID && p.IPTMode == wTaskType)
							.collect(Collectors.toList());
					if (wList.size() > 0) {
						List<String> wNames = new ArrayList<String>();
						for (IPTValue wIPTValue : wList) {
							String wName = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
							if (StringUtils.isNotEmpty(wName)) {
								wNames.add(wName);
							}
						}
						if (wNames.size() > 0) {
							wItem.Selfer = StringUtils.Join(",", wNames);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ?????????????????????????????????
	 */
	private void SetPeople(List<IPTItem> wToDoList, List<IPTItem> wDoneList, List<IPTValue> wValueList) {
		try {
			if (wToDoList != null && wToDoList.size() > 0) {
				// ?????????
				for (IPTItem wItem : wToDoList) {
					if (wValueList != null && wValueList.size() > 0 && wValueList.stream()
							.anyMatch(p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.SelfCheck.getValue())) {
						List<IPTValue> wList = wValueList.stream()
								.filter(p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.SelfCheck.getValue())
								.collect(Collectors.toList());
						if (wList.size() > 0) {
							List<String> wNames = new ArrayList<String>();
							for (IPTValue wIPTValue : wList) {
								String wName = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
								if (StringUtils.isNotEmpty(wName)) {
									wNames.add(wName);
								}
							}
							if (wNames.size() > 0) {
								wItem.Selfer = StringUtils.Join(",", wNames);
							}
						}
					}
				}
				// ??????
				for (IPTItem wItem : wToDoList) {
					if (wValueList != null && wValueList.size() > 0 && wValueList.stream().anyMatch(
							p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.MutualCheck.getValue())) {
						List<IPTValue> wList = wValueList.stream()
								.filter(p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.MutualCheck.getValue())
								.collect(Collectors.toList());
						if (wList.size() > 0) {
							List<String> wNames = new ArrayList<String>();
							for (IPTValue wIPTValue : wList) {
								String wName = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
								if (StringUtils.isNotEmpty(wName)) {
									wNames.add(wName);
								}
							}
							if (wNames.size() > 0) {
								wItem.Mutualer = StringUtils.Join(",", wNames);
							}
						}

					}
				}
				// ??????
				for (IPTItem wItem : wToDoList) {
					if (wValueList != null && wValueList.size() > 0 && wValueList.stream().anyMatch(
							p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.SpecialCheck.getValue())) {
						List<IPTValue> wList = wValueList.stream().filter(
								p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.SpecialCheck.getValue())
								.collect(Collectors.toList());
						if (wList.size() > 0) {
							List<String> wNames = new ArrayList<String>();
							for (IPTValue wIPTValue : wList) {
								String wName = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
								if (StringUtils.isNotEmpty(wName)) {
									wNames.add(wName);
								}
							}
							if (wNames.size() > 0) {
								wItem.Specialer = StringUtils.Join(",", wNames);
							}
						}
					}
				}
			}
			if (wDoneList != null && wDoneList.size() > 0) {
				for (IPTItem wItem : wDoneList) {
					// ?????????
					if (wValueList != null && wValueList.size() > 0 && wValueList.stream()
							.anyMatch(p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.SelfCheck.getValue())) {
						List<IPTValue> wList = wValueList.stream()
								.filter(p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.SelfCheck.getValue())
								.collect(Collectors.toList());
						if (wList.size() > 0) {
							List<String> wNames = new ArrayList<String>();
							for (IPTValue wIPTValue : wList) {
								String wName = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
								if (StringUtils.isNotEmpty(wName)) {
									wNames.add(wName);
								}
							}
							if (wNames.size() > 0) {
								wItem.Selfer = StringUtils.Join(",", wNames);
							}
						}
					}
					// ?????????
					if (wValueList != null && wValueList.size() > 0 && wValueList.stream().anyMatch(
							p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.MutualCheck.getValue())) {
						List<IPTValue> wList = wValueList.stream()
								.filter(p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.MutualCheck.getValue())
								.collect(Collectors.toList());
						if (wList.size() > 0) {
							List<String> wNames = new ArrayList<String>();
							for (IPTValue wIPTValue : wList) {
								String wName = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
								if (StringUtils.isNotEmpty(wName)) {
									wNames.add(wName);
								}
							}
							if (wNames.size() > 0) {
								wItem.Mutualer = StringUtils.Join(",", wNames);
							}
						}
					}
					// ?????????
					if (wValueList != null && wValueList.size() > 0 && wValueList.stream().anyMatch(
							p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.SpecialCheck.getValue())) {
						List<IPTValue> wList = wValueList.stream().filter(
								p -> p.IPTItemID == wItem.ID && p.IPTMode == SFCTaskType.SpecialCheck.getValue())
								.collect(Collectors.toList());
						if (wList.size() > 0) {
							List<String> wNames = new ArrayList<String>();
							for (IPTValue wIPTValue : wList) {
								String wName = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
								if (StringUtils.isNotEmpty(wName)) {
									wNames.add(wName);
								}
							}
							if (wNames.size() > 0) {
								wItem.Specialer = StringUtils.Join(",", wNames);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param wSFCTaskIPT
	 * @param wItemListMap
	 * @param wIPTItemList
	 */
	private void AddPeriodChangeItems(SFCTaskIPT wSFCTaskIPT, Map<Long, List<IPTItem>> wItemListMap,
			List<IPTItem> wIPTItemList) {
		try {
			List<IPTItem> wPeriodItemList = wItemListMap.get((long) wSFCTaskIPT.PeriodChangeStandard);
			if (wPeriodItemList != null && wPeriodItemList.size() > 0) {
				List<IPTItem> wNeedPreCheckItemList = wPeriodItemList.stream().filter(p -> p.IsPeriodChange == 1)
						.collect(Collectors.toList());
				if (wNeedPreCheckItemList != null && wNeedPreCheckItemList.size() > 0) {
					wIPTItemList.addAll(wNeedPreCheckItemList);
					// ????????????????????????
					List<IPTItem> wPeriodGroupItemList = wPeriodItemList.stream()
							.filter(p -> p.ItemType == IPTItemType.Group.getValue()).collect(Collectors.toList());
					if (wPeriodGroupItemList != null && wPeriodGroupItemList.size() > 0) {
						wIPTItemList.addAll(wPeriodGroupItemList);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param wToDoList     ???????????????
	 * @param wDoneList     ???????????????
	 * @param wPreValueList ???????????????
	 * @param wIPTItem      ???????????????
	 */
	private void SetPreCheckToDoAndDone(List<IPTItem> wToDoList, List<IPTItem> wDoneList, List<IPTValue> wPreValueList,
			IPTItem wIPTItem) {
		try {
			// ?????????????????????????????????
			if (wPreValueList == null
					|| !wPreValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)) {
				wToDoList.add(wIPTItem);
			}
			// ?????????????????????????????????
			else if (wPreValueList != null
					&& wPreValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)) {
				wDoneList.add(wIPTItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????
	 * 
	 * @param wTaskType         ??????????????????
	 * @param wToDoList         ???????????????
	 * @param wDoneList         ???????????????
	 * @param wValueList        ?????????
	 * @param wSelfValueList    ???????????????
	 * @param wMutualValueList  ???????????????
	 * @param wSpecilaValueList ???????????????
	 * @param wPreValueList     ???????????????
	 */
	private void FillValueList(int wTaskType, List<IPTItem> wToDoList, List<IPTItem> wDoneList,
			List<IPTValue> wValueList, List<IPTValue> wSelfValueList, List<IPTValue> wMutualValueList,
			List<IPTValue> wSpecilaValueList, List<IPTValue> wPreValueList) {
		try {
			// ?????????
			switch (SFCTaskType.getEnumType(wTaskType)) {
			case SelfCheck:// ??????
				for (IPTItem wIPTItem : wToDoList) {
					if (wSelfValueList != null && wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						wValueList.add(wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
				}
				for (IPTItem wIPTItem : wDoneList) {
					if (wSelfValueList != null && wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						wValueList.add(wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
				}
				break;
			case MutualCheck:// ??????
				for (IPTItem wIPTItem : wToDoList) {
					if (wMutualValueList != null
							&& wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						wValueList.add(wMutualValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
					if (wSelfValueList != null && wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						// ??????????????????
						wValueList.add(wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
				}
				for (IPTItem wIPTItem : wDoneList) {
					if (wMutualValueList != null
							&& wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						wValueList.add(wMutualValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
					if (wSelfValueList != null && wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						// ??????????????????
						wValueList.add(wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
				}
				break;
			case SpecialCheck:// ??????
				for (IPTItem wIPTItem : wToDoList) {
					if (wSpecilaValueList != null
							&& wSpecilaValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						wValueList.add(wSpecilaValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
					if (wMutualValueList != null
							&& wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						// ??????????????????
						wValueList.add(wMutualValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
					if (wSelfValueList != null && wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						// ??????????????????
						wValueList.add(wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
				}
				for (IPTItem wIPTItem : wDoneList) {
					if (wSpecilaValueList != null
							&& wSpecilaValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						wValueList.add(wSpecilaValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
					if (wMutualValueList != null
							&& wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						// ??????????????????
						wValueList.add(wMutualValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
					if (wSelfValueList != null && wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						// ??????????????????
						wValueList.add(wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
				}
				break;
			case Final:// ??????
			case OutPlant:// ?????????
			case PreCheck:// ??????
				for (IPTItem wIPTItem : wToDoList) {
					if (wPreValueList != null && wPreValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						wValueList.add(wPreValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
				}
				for (IPTItem wIPTItem : wDoneList) {
					if (wPreValueList != null && wPreValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
						wValueList.add(wPreValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
								.max((a, b) -> (int) (a.ID - b.ID)).get());
					}
				}
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param wToDoList         ????????????
	 * @param wDoneList         ????????????
	 * @param wMutualValueList  ???????????????
	 * @param wSpecilaValueList ???????????????
	 * @param wIPTItem          ???????????????
	 */
	private void SetSpecialCheckToDoAndDone(List<IPTItem> wToDoList, List<IPTItem> wDoneList,
			List<IPTValue> wMutualValueList, List<IPTValue> wSpecilaValueList, IPTItem wIPTItem) {
		try {
			// ????????????????????????????????????????????????????????????????????????????????????????????????
			if (wMutualValueList != null
					&& wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
					&& wMutualValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
							.max((a, b) -> (int) (a.ID - b.ID)).get().Result == 1
					&& (wSpecilaValueList == null || !wSpecilaValueList.stream()
							.anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2))) {
				wToDoList.add(wIPTItem);
				return;
			}
			// ?????????????????????????????????
			if (wSpecilaValueList != null
					&& wSpecilaValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)) {
				wDoneList.add(wIPTItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param wToDoList        ???????????????
	 * @param wDoneList        ?????????????????????
	 * @param wSelfValueList   ???????????????
	 * @param wMutualValueList ???????????????
	 * @param wIPTItem         ???????????????
	 */
	private void SetMutualCheckToDoAndDone(List<IPTItem> wToDoList, List<IPTItem> wDoneList,
			List<IPTValue> wSelfValueList, List<IPTValue> wMutualValueList, IPTItem wIPTItem) {
		try {
			// ?????????????????????????????????????????????????????????????????????????????????????????????
//			if (wSelfValueList != null
//					&& wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
//					&& wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
//							.max((a, b) -> (int) (a.ID - b.ID)).get().Result == 1
//					&& (wMutualValueList == null
//							|| !wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2))) {
//				wToDoList.add(wIPTItem);
//			}
			if (wSelfValueList != null
					&& wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
					&& (wMutualValueList == null
							|| !wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2))) {
				wToDoList.add(wIPTItem);
			}
			// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????ID???????????????????????????ID
			else if (wSelfValueList != null
					&& wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
					&& wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
							.max((a, b) -> (int) (a.ID - b.ID)).get().Result == 1
					&& wMutualValueList != null
					&& wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
					&& wMutualValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
							.max((a, b) -> (int) (a.ID - b.ID)).get().Result == 2
					&& wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
							.max((a, b) -> (int) (a.ID - b.ID)).get().ID > wMutualValueList.stream()
									.filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
									.max((a, b) -> (int) (a.ID - b.ID)).get().ID) {
				wToDoList.add(wIPTItem);
			}
			// ??????????????????????????????????????????????????????????????????????????????????????????????????????ID?????????????????????????????????ID
			else if (wMutualValueList != null
					&& wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
					&& wMutualValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
							.max((a, b) -> (int) (a.ID - b.ID)).get().Result == 2
					&& wSelfValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
							.max((a, b) -> (int) (a.ID - b.ID)).get().ID < wMutualValueList.stream()
									.filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
									.max((a, b) -> (int) (a.ID - b.ID)).get().ID) {
				wDoneList.add(wIPTItem);
			}
			// ???????????????????????????????????????????????????????????????
			else if (wMutualValueList != null
					&& wMutualValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
					&& wMutualValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID && p.Status == 2)
							.max((a, b) -> (int) (a.ID - b.ID)).get().Result == 1) {
				wDoneList.add(wIPTItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param wToDoList        ???????????????
	 * @param wDoneList        ???????????????
	 * @param wSelfValueList   ???????????????
	 * @param wMutualValueList ???????????????
	 * @param wIPTItem         ???????????????
	 */
	private void SetSelfCheckToDoAndDone(List<IPTItem> wToDoList, List<IPTItem> wDoneList,
			List<IPTValue> wSelfValueList, List<IPTValue> wMutualValueList, IPTItem wIPTItem) {
		try {
			if (wSelfValueList == null
					|| !wSelfValueList.stream().anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))) {
				wToDoList.add(wIPTItem);
			} else {
//				if (wSelfValueList != null
//						&& wSelfValueList.stream().anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
//						&& wMutualValueList != null
//						&& wMutualValueList.stream().anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
//						&& ((IPTValue) wMutualValueList.stream()
//								.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
//								.max((a, b) -> (int) (a.ID - b.ID)).get()).Result == 2)
//					if (((IPTValue) wSelfValueList.stream().filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
//							.max((a, b) -> (int) (a.ID - b.ID)).get()).ID < ((IPTValue) wMutualValueList.stream()
//									.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
//									.max((a, b) -> (int) (a.ID - b.ID)).get()).ID) {
//						wToDoList.add(wIPTItem);
//						return;
//					}
				if (wSelfValueList != null
						&& wSelfValueList.stream().anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
						&& ((IPTValue) wSelfValueList.stream()
								.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
								.max((a, b) -> (int) (a.ID - b.ID)).get()).Result == 2) {
//					wToDoList.add(wIPTItem);
					wDoneList.add(wIPTItem);
				} else {
					if (wSelfValueList != null
							&& wSelfValueList.stream().anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
							&& ((IPTValue) wSelfValueList.stream()
									.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
									.max((a, b) -> (int) (a.ID - b.ID)).get()).Result == 1)
						if (wMutualValueList == null || !wMutualValueList.stream()
								.anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))) {
							wDoneList.add(wIPTItem);
							return;
						}
					if (wSelfValueList != null
							&& wSelfValueList.stream().anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
							&& ((IPTValue) wSelfValueList.stream()
									.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
									.max((a, b) -> (int) (a.ID - b.ID)).get()).Result == 1)
						if (wMutualValueList != null
								&& wMutualValueList.stream()
										.anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
								&& ((IPTValue) wMutualValueList.stream()
										.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
										.max((a, b) -> (int) (a.ID - b.ID)).get()).Result == 1)
							if (((IPTValue) wMutualValueList.stream()
									.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
									.max((a, b) -> (int) (a.ID - b.ID)).get()).ID > ((IPTValue) wSelfValueList.stream()
											.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
											.max((a, b) -> (int) (a.ID - b.ID)).get()).ID) {
								wDoneList.add(wIPTItem);
								return;
							}
					if (wSelfValueList != null
							&& wSelfValueList.stream().anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
							&& ((IPTValue) wSelfValueList.stream()
									.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
									.max((a, b) -> (int) (a.ID - b.ID)).get()).Result == 1)
						if (wMutualValueList != null
								&& wMutualValueList.stream()
										.anyMatch(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
								&& ((IPTValue) wMutualValueList.stream()
										.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
										.max((a, b) -> (int) (a.ID - b.ID)).get()).Result == 2)
							if (((IPTValue) wSelfValueList.stream()
									.filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
									.max((a, b) -> (int) (a.ID - b.ID)).get()).ID > ((IPTValue) wMutualValueList
											.stream().filter(p -> (p.IPTItemID == wIPTItem.ID && p.Status == 2))
											.max((a, b) -> (int) (a.ID - b.ID)).get()).ID)
								wDoneList.add(wIPTItem);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ?????????
	 * 
	 * @param wGroupList
	 * @param wToDoList
	 */
	private List<IPTItem> AddGroup(List<IPTItem> wGroupList, List<IPTItem> wList) {
		List<IPTItem> wResult = new ArrayList<IPTItem>();
		try {
			if (wGroupList == null || wGroupList.size() <= 0) {
				return wResult;
			}

			List<IPTItem> wTempList = null;
			for (IPTItem wIPTItem : wList) {
				wTempList = wGroupList.stream().filter(p -> p.ID == wIPTItem.GroupID).collect(Collectors.toList());
				if (wTempList != null && wTempList.size() > 0) {
					wResult.addAll(wTempList);
					// ???????????????
					List<IPTItem> wFatherGroupList = AddGroup(wGroupList, wTempList);
					if (wFatherGroupList != null && wFatherGroupList.size() > 0) {
						wResult.addAll(wFatherGroupList);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private String SFC_ClockTask(BMSEmployee wLoginUser, int wType, OutResult<Integer> wErrorCode, int wShiftID,
			SFCTaskStep wSFCTaskStep, List<Calendar> wHolidayList, int wMinHour, int wMaxHour) {
		String wResult = "";
		try {
			OMSOrder wOMSOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTaskStep.OrderID)
					.Info(OMSOrder.class);
			if (wOMSOrder == null || wOMSOrder.ID <= 0) {
				wResult = StringUtils.Format("???????????????????????????!", new Object[] { wSFCTaskStep.PartPointName });
				return wResult;
			}
			if (wOMSOrder.Status == OMSOrderStatus.ReceivedTelegraph.getValue()
					|| wOMSOrder.Status == OMSOrderStatus.HasOrder.getValue()) {
				wResult = StringUtils.Format("????????????{0}????????????????????????????????????????????????!", new Object[] { wOMSOrder.OrderNo });
				return wResult;
			}

			// ???????????????????????????
			if (wSFCTaskStep.IsStartWork == SFCLoginType.StartWork.getValue()
					&& wType == SFCLoginType.StartWork.getValue()) {
				wResult = StringUtils.Format("???{0}??????????????????????????????????????????!", wSFCTaskStep.PartPointName);
				return wResult;
			}

			// ???????????????????????????
			if (wSFCTaskStep.IsStartWork == SFCLoginType.AfterWork.getValue()
					&& wType == SFCLoginType.AfterWork.getValue()) {
				wResult = StringUtils.Format("???{0}??????????????????????????????????????????!", wSFCTaskStep.PartPointName);
				return wResult;
			}

			// ??????????????????????????????
			if (wSFCTaskStep.IsStartWork == SFCLoginType.Default.getValue()
					&& wType == SFCLoginType.AfterWork.getValue()) {
				wResult = StringUtils.Format("???{0}??????????????????????????????????????????!", wSFCTaskStep.PartPointName);
				return wResult;
			}

			// ?????????????????????????????????
			if (wSFCTaskStep.IsStartWork != SFCLoginType.StartWork.getValue()
					&& wType == SFCLoginType.StopWork.getValue()) {
				wResult = StringUtils.Format("???{0}??????????????????????????????????????????!", wSFCTaskStep.PartPointName);
				return wResult;
			}

			// ????????????
			SFCLoginEvent wSFCLoginEvent = null;
			switch (SFCLoginType.getEnumType(wType)) {
			case StartWork:
				// ??????????????????(????????????????????????)
				if (wSFCTaskStep.Type == SFCTaskStepType.Step.getValue()) {
					this.CheckSpotTask(wLoginUser, wSFCTaskStep);
//					String wMsg = this.CheckSpotTask(wLoginUser, wSFCTaskStep);
//					if (StringUtils.isNotEmpty(wMsg)) {
//						return wMsg;
//					}
				}

				wSFCLoginEvent = new SFCLoginEvent();
				wSFCLoginEvent.ID = 0;
				wSFCLoginEvent.WorkShopID = 0;
				wSFCLoginEvent.StationID = 0;
				wSFCLoginEvent.OperatorID = wLoginUser.ID;
				wSFCLoginEvent.ModuleID = 0;
				wSFCLoginEvent.ShiftID = wShiftID;
				wSFCLoginEvent.SFCTaskStepID = wSFCTaskStep.ID;
				wSFCLoginEvent.Active = 1;
				wSFCLoginEvent.LoginTime = Calendar.getInstance();
				wSFCLoginEvent.Type = SFCLoginType.StartWork.getValue();
				wSFCTaskStep.IsStartWork = SFCLoginType.StartWork.getValue();
				wSFCTaskStep.EditTime = Calendar.getInstance();
				SFCLoginEventDAO.getInstance().Update(wLoginUser, wSFCLoginEvent, wErrorCode);
				LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);

				if (wSFCTaskStep.Type == SFCTaskStepType.Step.getValue()
						|| wSFCTaskStep.Type == SFCTaskStepType.Quality.getValue()) {
					// ????????????????????????????????????
					APSTaskStep wAPSTaskStep = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskStepByID(wLoginUser, wSFCTaskStep.TaskStepID).Info(APSTaskStep.class);
					if (wAPSTaskStep != null && wAPSTaskStep.ID > 0
							&& wAPSTaskStep.Status == APSTaskStatus.Issued.getValue()) {
						wAPSTaskStep.StartTime = Calendar.getInstance();
						wAPSTaskStep.Status = APSTaskStatus.Started.getValue();
						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wAPSTaskStep);
					}

					// ??????????????????
					APSTaskPart wAPSTaskPart = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskPartByID(wLoginUser, wAPSTaskStep.TaskPartID).Info(APSTaskPart.class);
					if (wAPSTaskPart != null && wAPSTaskPart.ID > 0
							&& (wAPSTaskPart.Status == APSTaskStatus.Issued.getValue()
									|| wAPSTaskPart.Status == APSTaskStatus.Confirm.getValue())) {
						wAPSTaskPart.StartWorkTime = Calendar.getInstance();
						wAPSTaskPart.Status = APSTaskStatus.Started.getValue();
						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wLoginUser, wAPSTaskPart);
					}
				}

				// ???????????????????????????
				ProblemTaskStart(wLoginUser, wSFCTaskStep);
				// ????????????
				SFC_StartOrder(wLoginUser, wSFCTaskStep);
				// ??????????????????????????????
				SFCLoginEventDAO.getInstance().SFC_CloseTaskStepMessage(wLoginUser, wSFCTaskStep, wErrorCode);
				break;
			case AfterWork:
				wSFCLoginEvent = new SFCLoginEvent();
				wSFCLoginEvent.ID = 0;
				wSFCLoginEvent.WorkShopID = 0;
				wSFCLoginEvent.StationID = 0;
				wSFCLoginEvent.OperatorID = wLoginUser.ID;
				wSFCLoginEvent.ModuleID = 0;
				wSFCLoginEvent.ShiftID = wShiftID;
				wSFCLoginEvent.Active = 1;
				wSFCLoginEvent.SFCTaskStepID = wSFCTaskStep.ID;
				wSFCLoginEvent.LoginTime = Calendar.getInstance();
				wSFCLoginEvent.Type = SFCLoginType.AfterWork.getValue();

				// ??????????????????
				wSFCTaskStep.RealHour += QMSUtils.getInstance().QMS_CalTimeDuration(wSFCTaskStep.EditTime,
						Calendar.getInstance(), wHolidayList, wMinHour, wMaxHour);
				wSFCTaskStep.EditTime = Calendar.getInstance();
				wSFCTaskStep.IsStartWork = SFCLoginType.AfterWork.getValue();
				SFCLoginEventDAO.getInstance().Update(wLoginUser, wSFCLoginEvent, wErrorCode);
				LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);

				// ????????????????????????????????????????????????????????????
				SendMessageToMonitorWhenFinishClock(wLoginUser, wSFCTaskStep);
				break;
			case StopWork:
				wSFCLoginEvent = new SFCLoginEvent();
				wSFCLoginEvent.ID = 0;
				wSFCLoginEvent.WorkShopID = 0;
				wSFCLoginEvent.StationID = 0;
				wSFCLoginEvent.OperatorID = wLoginUser.ID;
				wSFCLoginEvent.ModuleID = 0;
				wSFCLoginEvent.ShiftID = wShiftID;
				wSFCLoginEvent.Active = 1;
				wSFCLoginEvent.SFCTaskStepID = wSFCTaskStep.ID;
				wSFCLoginEvent.LoginTime = Calendar.getInstance();
				wSFCLoginEvent.Type = SFCLoginType.StopWork.getValue();

				wSFCTaskStep.EditTime = Calendar.getInstance();
				wSFCTaskStep.IsStartWork = SFCLoginType.StopWork.getValue();
				SFCLoginEventDAO.getInstance().Update(wLoginUser, wSFCLoginEvent, wErrorCode);
				LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			LoggerTool.SaveException("SFCService", "ClockTask", "Function Exception:" + ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private String CheckSpotTask(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep) {
		String wResult = "";
		try {
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTaskStep.OrderID)
					.Info(OMSOrder.class);
			APSTaskStep wAPSTaskStep = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepByID(wLoginUser, wSFCTaskStep.TaskStepID).Info(APSTaskStep.class);

			MSSSpotTask wMSSSpotTask = WMSServiceImpl.getInstance()
					.MSS_QuerySpotTask(wLoginUser, wSFCTaskStep.TaskStepID, -1).Info(MSSSpotTask.class);
			// ??????????????????
			if (wMSSSpotTask == null || wMSSSpotTask.ID <= 0) {
				wMSSSpotTask = new MSSSpotTask();
				wMSSSpotTask.ID = 0;
				wMSSSpotTask.RouteID = wOrder.RouteID;
				wMSSSpotTask.TaskPartID = wSFCTaskStep.TaskPartID;
				wMSSSpotTask.TaskStepID = wSFCTaskStep.TaskStepID;
				wMSSSpotTask.OrderID = wSFCTaskStep.OrderID;
				wMSSSpotTask.PartID = wSFCTaskStep.PartID;
				wMSSSpotTask.PartNo = StringUtils.isEmpty(wOrder.PartNo) ? wOrder.WBSNo : wOrder.PartNo;
				wMSSSpotTask.PartPointID = wAPSTaskStep.StepID;
				wMSSSpotTask.OperatorIDList = wAPSTaskStep.OperatorList;
				wMSSSpotTask.ProductID = wOrder.ProductID;
				wMSSSpotTask.ProductNo = wOrder.ProductNo;
				wMSSSpotTask.WBSNo = wOrder.WBSNo;
				wMSSSpotTask.CustomerID = wOrder.CustomerID;
				APIResult wAPIResult = WMSServiceImpl.getInstance().MSS_CreateSpotTask(BaseDAO.SysAdmin, wMSSSpotTask);
				if (wAPIResult.getResultCode() != 1000) {
					wResult = StringUtils.Format("????????????{0}????????????????????????????????????!\n???{1}???", wSFCTaskStep.PartPointName,
							QMSUtils.getInstance().GetMsg(wAPIResult));
					return wResult;
				}
				MSSSpotTask wNewSpotTask = wAPIResult.Info(MSSSpotTask.class);
				if (wNewSpotTask == null || wNewSpotTask.ID <= 0) {
					return wResult;
				}

				if (wNewSpotTask.TaskStatus != MSSSpotStatus.Done.getValue()) {
					wResult = StringUtils.Format("????????????{0}??????????????????????????????!", wSFCTaskStep.PartPointName);
					return wResult;
				}
			} else {
				if (wMSSSpotTask.TaskStatus != MSSSpotStatus.Done.getValue()) {
					wResult = StringUtils.Format("????????????{0}??????????????????????????????!", wSFCTaskStep.PartPointName);
					return wResult;
				}
			}
		} catch (Exception ex) {
			wResult = ex.toString();
			logger.error(ex.toString());
		}
		return wResult;
	}

	/*
	 * ?????????????????????
	 */
	private void ProblemTaskStart(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep) {
		try {
			if (wSFCTaskStep.Type != SFCTaskStepType.Question.getValue()) {
				return;
			}

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			IPTPreCheckProblem wProblem = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
					wSFCTaskStep.TaskStepID, wErrorCode);
			if (wProblem == null || wProblem.ID <= 0) {
				return;
			}

			wProblem.Status = IPTPreCheckProblemStatus.Start.getValue();
			IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wProblem, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private void SFC_StartOrder(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep) {
		try {
			OMSOrder wOMSOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTaskStep.OrderID)
					.Info(OMSOrder.class);
			if (wOMSOrder != null && wOMSOrder.ID > 0 && wOMSOrder.Status == OMSOrderStatus.EnterFactoryed.getValue()) {
				wOMSOrder.Status = OMSOrderStatus.Repairing.getValue();
				wOMSOrder.RealStartDate = Calendar.getInstance();
				LOCOAPSServiceImpl.getInstance().OMS_UpdateOrder(BaseDAO.SysAdmin, wOMSOrder);
				// ??????BOM
//				WMSServiceImpl.getInstance().APS_OrderStart(wLoginUser, wOMSOrder.LineID, wOMSOrder.ProductID,
//						wOMSOrder.CustomerID, wOMSOrder.ID, wOMSOrder.WBSNo, wOMSOrder.PartNo, wOMSOrder.RouteID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????????????????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskStep
	 */
	private void SendMessageToMonitorWhenFinishClock(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep) {
		try {
			if (wSFCTaskStep == null || wSFCTaskStep.ID <= 0) {
				return;
			}

			List<BFCMessage> wMessageList = new ArrayList<BFCMessage>();
			// QMS
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);
			// ??????????????????????????????
			BFCMessage wMessage = new BFCMessage();
			wMessage.Active = 0;
			wMessage.CompanyID = 0;
			wMessage.CreateTime = Calendar.getInstance();
			wMessage.EditTime = Calendar.getInstance();
			wMessage.ID = 0;
			wMessage.MessageID = wSFCTaskStep.ID;
			wMessage.Title = StringUtils.Format("???????????? {0} {1}",
					new Object[] { wSFCTaskStep.PartPointName, wSFCTaskStep.PartNo });
			wMessage.MessageText = StringUtils.Format("???{0}??? {1}??????{2}??????????????????????????????!",
					new Object[] { BPMEventModule.FinishClock.getLable(), wLoginUser.Name,
							QMSConstants.GetFPCStepName(wSFCTaskStep.StepID) });
			wMessage.ModuleID = BPMEventModule.FinishClock.getValue();
			wMessage.ResponsorID = wSFCTaskStep.MonitorID;
			wMessage.ShiftID = wShiftID;
			wMessage.StationID = 0;
			wMessage.Type = BFCMessageType.Notify.getValue();
			wMessageList.add(wMessage);

			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskStep
	 */
	@SuppressWarnings("unused")
	private void UpdateMessage(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep) {
		try {
			List<BFCMessage> wMessageList = null;
			wMessageList = CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.ID,
							BPMEventModule.SCDispatching.getValue(), BFCMessageType.Task.getValue(), -1, -1, null, null)
					.List(BFCMessage.class);

			if (wMessageList == null || wMessageList.size() <= 0) {
				return;
			}
			wMessageList = wMessageList.stream()
					.filter(p -> p.MessageID == wSFCTaskStep.ID && (p.Active == BFCMessageStatus.Default.getValue()
							|| p.Active == BFCMessageStatus.Sent.getValue()
							|| p.Active == BFCMessageStatus.Read.getValue()))
					.collect(Collectors.toList());
			if (wMessageList != null && wMessageList.size() > 0) {
				if (wSFCTaskStep.Type == SFCTaskStepType.Question.getValue()) {
					wMessageList.forEach(p -> {
						if (p.StepID == 1) {
							p.Active = BFCMessageStatus.Finished.getValue();
						}
					});
				} else {
					wMessageList.forEach(p -> {
						if (p.StepID == 0) {
							p.Active = BFCMessageStatus.Finished.getValue();
						}
					});
				}
				wMessageList.forEach(p -> p.Active = BFCMessageStatus.Finished.getValue());
				CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<IPTPreCheckProblem>> SFC_QueryPreCheckProblemList(BMSEmployee wLoginUser,
			int wSFCTaskIPTID) {
		ServiceResult<List<IPTPreCheckProblem>> wResult = new ServiceResult<List<IPTPreCheckProblem>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			SFCTaskIPT wTask = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wSFCTaskIPTID, wErrorCode);
			if (wTask == null || wTask.ID <= 0) {
				return wResult;
			}

			List<IPTPreCheckProblem> wList = IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, -1, wTask.ID,
					-1, -1, -1, -1, null, wErrorCode);
			if (wList != null && wList.size() > 0) {
				wResult.Result = wList;
			} else {
				wResult.Result = new ArrayList<IPTPreCheckProblem>();
			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTList(BMSEmployee wLoginUser, int wTaskType, String wPartNo,
			Calendar wQStartTime, Calendar wQEndTime, int wOrderID) {
		ServiceResult<List<SFCTaskIPT>> wResult = new ServiceResult<>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<>(0);
			wResult.Result = new ArrayList<>();
			List<SFCTaskIPT> wTaskList = new ArrayList<SFCTaskIPT>();

			int wSFCTaskStepType = 0;
			switch (SFCTaskType.getEnumType(wTaskType)) {
			case SelfCheck:
			case MutualCheck:
			case SpecialCheck:
			case PreCheck:
				wSFCTaskStepType = SFCTaskStepType.Step.getValue();
				break;
			case Final:
			case OutPlant:
				wSFCTaskStepType = SFCTaskStepType.Quality.getValue();
				break;
			default:
				break;
			}

			// ??????????????????
			wTaskList.addAll(SFCTaskIPTDAO.getInstance().SelectListByTime(wLoginUser, wTaskType, wSFCTaskStepType,
					wPartNo, wQStartTime, wQEndTime, wOrderID, wErrorCode));

			if (wTaskList == null || wTaskList.size() <= 0) {
				return wResult;
			}

			// ???????????????????????????????????????????????????
			List<SFCTaskStep> wSFCTaskStepList = new ArrayList<SFCTaskStep>();
			Map<Integer, List<IPTValue>> wValueMap = new HashMap<Integer, List<IPTValue>>();
			// ????????????
			wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(wLoginUser,
							wTaskList.stream().map(p -> p.TaskStepID).distinct().collect(Collectors.toList()))
					.List(SFCTaskStep.class);
			wSFCTaskStepList.removeIf(p -> p.Type == SFCTaskStepType.Question.getValue());
			// ???????????????
			wValueMap = (Map<Integer, List<IPTValue>>) IPTStandardDAO.getInstance().SelectValue(wLoginUser,
					wTaskList.stream().map(p -> p.ID).collect(Collectors.toList()), IPTMode.Default.getValue(), -1,
					wErrorCode).Result;

			// ??????????????????????????????
			List<SFCTaskIPT> wAllTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByTaskStepIDList(wLoginUser,
					wTaskList.stream().map(p -> p.TaskStepID).distinct().collect(Collectors.toList()),
					SFCTaskStepType.Step.getValue(), wErrorCode);
			// ?????????????????????
			List<IPTValue> wAllValueList = IPTValueDAO.getInstance().SelectByTaskIDList(wLoginUser,
					wAllTaskIPTList.stream().map(p -> p.ID).distinct().collect(Collectors.toList()), wErrorCode);

			switch (SFCTaskType.getEnumType(wTaskType)) {
			case SelfCheck:
			case Final:
			case OutPlant:
				for (SFCTaskIPT wSFCTaskIPT : wTaskList) {
					List<SFCTaskStep> wTempSFCTaskStepList = wSFCTaskStepList.stream()
							.filter(p -> p.TaskStepID == wSFCTaskIPT.TaskStepID).collect(Collectors.toList());
					if (wTempSFCTaskStepList == null || wTempSFCTaskStepList.size() <= 0) {
						continue;
					}

					List<IPTValue> wTempValueList = new ArrayList<IPTValue>();
					if (wValueMap.containsKey(wSFCTaskIPT.ID)) {
						wTempValueList = wValueMap.get(wSFCTaskIPT.ID);
					}

					List<SFCTaskIPT> wMutualList = wAllTaskIPTList.stream()
							.filter(p -> p.TaskStepID == wSFCTaskIPT.TaskStepID
									&& p.TaskType == SFCTaskType.MutualCheck.getValue())
							.collect(Collectors.toList());

					List<IPTValue> wMutualValueList = new ArrayList<IPTValue>();
					if (wMutualList != null && wMutualList.size() > 0) {
						wMutualValueList = wAllValueList.stream().filter(p -> p.TaskID == wMutualList.get(0).ID)
								.collect(Collectors.toList());
					}

					// ??????????????????
					AddTaskIPT_SelfCheck(wLoginUser, wResult, wSFCTaskIPT, wTempSFCTaskStepList, wTempValueList,
							wMutualValueList);
				}
				break;
			case MutualCheck: {
				for (SFCTaskIPT wSFCTaskIPT : wTaskList) {
					// ??????????????????
					AddTaskIPT_MutualCheck(wLoginUser, wResult, wErrorCode, wSFCTaskStepList, wSFCTaskIPT,
							wAllTaskIPTList, wAllValueList);
				}
				break;
			}
			case SpecialCheck: {
				List<Integer> wCheckIDList = null;
				for (SFCTaskIPT wSFCTaskIPT : wTaskList) {

					wCheckIDList = QMSConstants.GetFPCPart(wSFCTaskIPT.StationID).CheckerList;

					if (wCheckIDList != null && wCheckIDList.stream().anyMatch(p -> p == wLoginUser.ID)) {
						// ??????????????????
						(wResult.Result).add(wSFCTaskIPT);
					}
				}
				break;
			}
			case PreCheck: {
				List<SFCTaskStep> wTempList = wSFCTaskStepList;
				wResult.Result.addAll(wTaskList.stream()
						.filter(p -> wTempList != null && wTempList.size() > 0
								&& wTempList.stream()
										.anyMatch(q -> q.TaskStepID == p.TaskStepID && q.OperatorID == wLoginUser.ID))
						.collect(Collectors.toList()));
				break;
			}
			default:
				break;
			}

			if (wResult.Result == null || wResult.Result.size() <= 0) {
				return wResult;
			}

			List<Integer> wOrderIDList = wResult.Result.stream().map(p -> p.OrderID).distinct()
					.collect(Collectors.toList());
			wOrderIDList.removeIf(p -> p <= 0);

			if (wOrderIDList.size() > 0) {
				Map<Integer, OMSOrder> wOrderMap = LOCOAPSServiceImpl.getInstance()
						.OMS_QueryOrderListByIDList(wLoginUser, wOrderIDList).List(OMSOrder.class).stream()
						.collect(Collectors.toMap(p -> p.ID, p -> p));

				if (wOrderMap.size() > 0) {
					for (SFCTaskIPT wSFCTaskIPT : wResult.Result) {

						if (!wOrderMap.containsKey(wSFCTaskIPT.OrderID)) {
							continue;
						}
						wSFCTaskIPT.CustomerID = wOrderMap.get(wSFCTaskIPT.OrderID).CustomerID;
						wSFCTaskIPT.CustomerName = wOrderMap.get(wSFCTaskIPT.OrderID).Customer;
					}
				}
			}

			// ????????????
			UpdateTaskIPTStatus(wLoginUser, wResult.Result);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			SFCServiceImpl.logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 * 
	 * @param wLoginUser
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	private void UpdateTaskIPTStatus(BMSEmployee wLoginUser, List<SFCTaskIPT> wList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wList == null || wList.size() <= 0) {
				return;
			}

			List<IPTItem> wToDoList = null;
			for (SFCTaskIPT wSFCTaskIPT : wList) {
				ServiceResult<List<Object>> wServiceResult = this.SFC_QueryToDoAndDoneList(wLoginUser,
						wSFCTaskIPT.TaskStepID, wSFCTaskIPT.TaskType);
				if (wServiceResult == null || wServiceResult.Result == null || wServiceResult.Result.size() != 3) {
					continue;
				}

				wToDoList = (List<IPTItem>) wServiceResult.Result.get(0);
				if (wToDoList == null) {
					continue;
				}

				if (wToDoList.size() <= 0) {
					if (wSFCTaskIPT.Status == SFCTaskStatus.Active.getValue()) {
						wSFCTaskIPT.EndTime = Calendar.getInstance();
						wSFCTaskIPT.Status = SFCTaskStatus.Done.getValue();
					}
				} else {
					wSFCTaskIPT.Status = SFCTaskStatus.Active.getValue();
				}

				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private List<IPTValue> AddTaskIPT_SelfCheck(BMSEmployee wLoginUser, ServiceResult<List<SFCTaskIPT>> wResult,
			SFCTaskIPT wSFCTaskIPT, List<SFCTaskStep> wTempSFCTaskStepList, List<IPTValue> wTempValueList,
			List<IPTValue> wMutualValueList) {
		List<IPTValue> wValueTempList = wTempValueList;

		// ??????
		wTempSFCTaskStepList = new ArrayList<SFCTaskStep>(wTempSFCTaskStepList.stream()
				.collect(Collectors.toMap(SFCTaskStep::getOperatorID, account -> account, (k1, k2) -> k2)).values());

		if (wTempSFCTaskStepList.size() == 1) {
			if (!wTempSFCTaskStepList.stream().anyMatch(p -> p.OperatorID == wLoginUser.ID)) {
				return wValueTempList;
			}
			(wResult.Result).add(wSFCTaskIPT);
		} else if (wTempSFCTaskStepList.stream().anyMatch(p -> p.OperatorID == wLoginUser.ID)
				&& (wTempValueList == null || wTempValueList.size() <= 0)) {
			(wResult.Result).add(wSFCTaskIPT);
		} else if (wTempSFCTaskStepList.stream().anyMatch(p -> p.OperatorID == wLoginUser.ID) && wTempValueList != null
				&& wTempValueList.size() > 0 && wTempValueList.stream().anyMatch(p -> p.SubmitID == wLoginUser.ID)) {
			(wResult.Result).add(wSFCTaskIPT);
		} else {
			if (!wTempSFCTaskStepList.stream().anyMatch(p -> p.OperatorID == wLoginUser.ID) || wTempValueList == null
					|| wTempValueList.size() <= 0 || wTempValueList.stream().anyMatch(p -> p.SubmitID == wLoginUser.ID)
					|| wTempSFCTaskStepList.stream()
							.filter(p -> !wValueTempList.stream().anyMatch(q -> q.SubmitID == p.OperatorID))
							.count() < 2L
					|| (wMutualValueList != null && wMutualValueList.size() > 0
							&& wMutualValueList.stream().anyMatch(p -> p.SubmitID == wLoginUser.ID))) {
				return wValueTempList;
			}
			(wResult.Result).add(wSFCTaskIPT);
		}
		return wValueTempList;
	}

	private void AddTaskIPT_MutualCheck(BMSEmployee wLoginUser, ServiceResult<List<SFCTaskIPT>> wResult,
			OutResult<Integer> wErrorCode, List<SFCTaskStep> wSFCTaskStepList, SFCTaskIPT wSFCTaskIPT,
			List<SFCTaskIPT> wAllTaskIPTList, List<IPTValue> wAllValueList) {
		try {
			List<SFCTaskStep> wTempSFCTaskStepList = wSFCTaskStepList.stream()
					.filter(p -> p.TaskStepID == wSFCTaskIPT.TaskStepID).collect(Collectors.toList());
			if (wTempSFCTaskStepList != null) {
				if (wTempSFCTaskStepList.size() <= 0) {
					return;
				}

				List<SFCTaskIPT> wSelfCheckList = wAllTaskIPTList.stream().filter(
						p -> p.TaskStepID == wSFCTaskIPT.TaskStepID && p.TaskType == SFCTaskType.SelfCheck.getValue())
						.collect(Collectors.toList());

				List<IPTValue> wSelfValueList = new ArrayList<IPTValue>();
				if (wSelfCheckList != null && wSelfCheckList.size() > 0) {
					wSelfValueList = wAllValueList.stream().filter(p -> p.TaskID == wSelfCheckList.get(0).ID)
							.collect(Collectors.toList());
				}

				// ????????????
				wTempSFCTaskStepList = new ArrayList<SFCTaskStep>(wTempSFCTaskStepList.stream()
						.collect(Collectors.toMap(SFCTaskStep::getOperatorID, account -> account, (k1, k2) -> k2))
						.values());
				if (wTempSFCTaskStepList.size() == 1) {
					// ???????????????????????????
					AddValueWhenSizeIsOne(wLoginUser, wResult, wSFCTaskIPT, wTempSFCTaskStepList);
				} else {
					if (!wTempSFCTaskStepList.stream().anyMatch(p -> p.OperatorID == wLoginUser.ID)
							|| wSelfValueList.stream().anyMatch(p -> p.SubmitID == wLoginUser.ID)) {
						return;
					}
					wResult.Result.add(wSFCTaskIPT);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private void AddValueWhenSizeIsOne(BMSEmployee wLoginUser, ServiceResult<List<SFCTaskIPT>> wResult,
			SFCTaskIPT wSFCTaskIPT, List<SFCTaskStep> wTempSFCTaskStepList) {
		try {
			// ????????????????????????????????????????????????
			List<SCHSecondmentApply> wSecondmentList = LOCOAPSServiceImpl.getInstance()
					.SCH_QuerySecondmentList(wLoginUser, wTempSFCTaskStepList.get(0).OperatorID)
					.List(SCHSecondmentApply.class);
			wSecondmentList.removeIf(p -> p.Status != SCHSecondStatus.Seconded.getValue()
					|| p.EndTime.compareTo(Calendar.getInstance()) <= 0
					|| p.StartTime.compareTo(Calendar.getInstance()) > 0);
			if (wSecondmentList != null && wSecondmentList.size() > 0) {
				SCHSecondmentApply wSCHSecondment = wSecondmentList.get(0);
				// ??????????????????????????????
				List<Integer> wThisPersonOwnClassIDList = new ArrayList<Integer>();

				wThisPersonOwnClassIDList.add(wSCHSecondment.NewClassID);
				wThisPersonOwnClassIDList.add(wSCHSecondment.OldClassID);

				// ????????????????????????
				FPCPart wFPCPart = QMSConstants.GetFPCPart(wSFCTaskIPT.StationID);
				List<Integer> wChargeClassIDList = wFPCPart.DepartmentIDList;
				// ???????????????????????????
				if (wThisPersonOwnClassIDList.size() > 0 && wChargeClassIDList.size() > 0) {
					List<Integer> wList = wThisPersonOwnClassIDList.stream()
							.filter(p -> wChargeClassIDList.stream().anyMatch(q -> q == p))
							.collect(Collectors.toList());
					if (wList.size() > 1) {
						// ????????????????????????????????????????????????
						int wClassID = QMSConstants.GetBMSEmployee(wTempSFCTaskStepList.get(0).MonitorID).DepartmentID;
						if (wList.stream().anyMatch(p -> p == wClassID)) {
							AddValue(wLoginUser, wResult, wSFCTaskIPT, wTempSFCTaskStepList, wClassID);
						}
					} else {
						// ???????????????????????????
						int wClassID = wList.get(0);
						AddValue(wLoginUser, wResult, wSFCTaskIPT, wTempSFCTaskStepList, wClassID);
					}
				}
			} else {
				int wClassID = QMSConstants.GetBMSEmployee(wTempSFCTaskStepList.get(0).MonitorID).DepartmentID;
				if (wClassID > 0) {
					AddValue(wLoginUser, wResult, wSFCTaskIPT, wTempSFCTaskStepList, wClassID);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private void AddValue(BMSEmployee wLoginUser, ServiceResult<List<SFCTaskIPT>> wResult, SFCTaskIPT wSFCTaskIPT,
			List<SFCTaskStep> wTempSFCTaskStepList, int wClassID) {
		try {
			// ????????????????????????
			List<BMSEmployee> wOtherEmployeeList = QMSConstants.GetBMSEmployeeList().values().stream()
					.filter(p -> p.DepartmentID == wClassID && p.ID != wTempSFCTaskStepList.get(0).OperatorID)
					.collect(Collectors.toList());
			// ???????????????????????????
			if (wOtherEmployeeList != null && wOtherEmployeeList.size() > 0
					&& wOtherEmployeeList.stream().anyMatch(p -> p.ID == wLoginUser.ID)) {
				wResult.Result.add(wSFCTaskIPT);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????????????????????????????????????????
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
					.BFC_GetMessageList(wLoginUser, -1, wModuleID, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			if (wMessageList == null || wMessageList.size() <= 0) {
				return;
			}

			wMessageList = wMessageList.stream().filter(p -> p.MessageID == wSFCTaskIPT.ID)
					.collect(Collectors.toList());
			if (wMessageList == null || wMessageList.size() <= 0) {
				return;
			}

			wMessageList.forEach(p -> p.Active = wSFCTaskIPT.Status == SFCTaskStatus.Done.getValue()
					? BFCMessageStatus.Finished.getValue()
					: 0);
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<SFCTaskIPT> SFC_QueryTaskIPTByID(BMSEmployee wLoginUser, int wSFCTaskIPTID) {
		ServiceResult<SFCTaskIPT> wResult = new ServiceResult<SFCTaskIPT>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wSFCTaskIPTID, wErrorCode);

			// ?????????????????????????????????????????????????????????????????????
			if (wResult.Result.Status == 1) {
				int wCurrentStandardID = IPTStandardDAO.getInstance().SelectCurrentID(wLoginUser,
						wResult.Result.ProductID, wResult.Result.LineID, wResult.Result.StationID,
						wResult.Result.PartPointID, wErrorCode);
				if (wCurrentStandardID != wResult.Result.ModuleVersionID) {
					wResult.Result.ModuleVersionID = wCurrentStandardID;
					SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wResult.Result, wErrorCode);
				}
			}

			OMSOrder wOMSOrder = null;
			if (wResult.Result != null && wResult.Result.ID > 0) {
				wOMSOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wResult.Result.OrderID)
						.Info(OMSOrder.class);
				if (wOMSOrder != null && wOMSOrder.ID > 0) {
					wResult.Result.CustomerID = wOMSOrder.BureauSectionID;
					wResult.Result.CustomerName = wOMSOrder.BureauSection;
					wResult.Result.OrderNo = wOMSOrder.OrderNo;
				}
			}

			// ?????????????????????
			AddMuturPerson(wLoginUser, wResult.Result);

			// ??????????????????IsPic??????
			if (wResult.Result.ID > 0) {
				List<Integer> wIDList = new ArrayList<Integer>(Arrays.asList(wResult.Result.ModuleVersionID));
				Map<Integer, Integer> wStandardIDMap = SFCTaskIPTDAO.getInstance().GetStandardIDMap(wLoginUser, wIDList,
						wErrorCode);
				if (wStandardIDMap.containsKey(wResult.Result.ModuleVersionID)) {
					wResult.Result.IsPic = wStandardIDMap.get(wResult.Result.ModuleVersionID);
					if (wResult.Result.TaskType == SFCTaskType.SpecialCheck.getValue() && wResult.Result.IsPic == 1) {
						wResult.Result.Mutualer = SFCTaskIPTDAO.getInstance().GetMutualerByPic(wLoginUser,
								wResult.Result, wErrorCode);
					}
				}

				if (StringUtils.isNotEmpty(wResult.Result.PicUri)) {
					wResult.Result.IsPic = 1;
				}
			}

			// ???????????????????????????????????????????????????????????????????????????
			if (wResult.Result.TaskType == SFCTaskType.MutualCheck.getValue()) {
				List<SFCTaskIPT> wList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
						wResult.Result.TaskStepID, SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, -1, null, null,
						wErrorCode);
				if (wList.size() > 0) {
					wResult.Result.OldPicUri = wList.get(0).PicUri;
					wResult.Result.OldRemark = wList.get(0).Remark;
				}
			} else if (wResult.Result.TaskType == SFCTaskType.SpecialCheck.getValue()) {
				List<SFCTaskIPT> wList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
						wResult.Result.TaskStepID, SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, -1, null, null,
						wErrorCode);
				if (wList.size() > 0) {
					wResult.Result.OldPicUri = wList.get(0).PicUri;
					wResult.Result.OldRemark = wList.get(0).Remark;
				}
			}

			if (wResult.Result.IsPic == 1 && (wResult.Result.TaskType == SFCTaskType.MutualCheck.getValue()
					|| wResult.Result.TaskType == SFCTaskType.SpecialCheck.getValue())) {
				wResult.Result.Selfer = GetSelfer(wResult.Result);
			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????
	 */
	private String GetSelfer(SFCTaskIPT wSFCTaskIPT) {
		String wResult = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCTaskIPT> wList = SFCTaskIPTDAO.getInstance().SelectList(BaseDAO.SysAdmin, -1,
					wSFCTaskIPT.TaskStepID, SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, -1, null, null,
					wErrorCode);
			if (wList.size() > 0) {
				wResult = wList.get(0).OperatorListNames;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_UpdateTaskIPT(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTList(BMSEmployee wLoginUser, int wOrderID, int wLineID,
			int wProductID, int wPartID, int wTaskType, int wStepID, String wPartNo, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<SFCTaskIPT>> wResult = new ServiceResult<List<SFCTaskIPT>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID, wPartID, wTaskType,
					wLineID, wStepID, wProductID, wPartNo, wStartTime, wEndTime, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ServiceResult<Boolean> SFC_JudgeIsAllStepOK(BMSEmployee wLoginUser, APSTaskPart wAPSTaskPart) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		try {
			wResult.Result = true;
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<APSTaskStep> wList = LOCOAPSServiceImpl.getInstance().APS_QueryTaskStepList(wLoginUser,
					wAPSTaskPart.OrderID, wAPSTaskPart.PartID, wAPSTaskPart.ID, new ArrayList<Integer>())
					.List(APSTaskStep.class);

			if (wList == null || wList.size() <= 0) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("????????????{0}???????????????????????????!", wAPSTaskPart.PartName);
				return wResult;
			}

			List<SFCTaskIPT> wIPTList = null;
			List<Object> wObjectList = null;
			List<IPTItem> wToDoList = null;

			// ??????????????????????????????
			wList = wList.stream().filter(p -> p.Active == 1 && p.Status != APSTaskStatus.Done.getValue())
					.collect(Collectors.toList());

			if (wList == null || wList.size() <= 0) {
				wResult.Result = true;
				return wResult;
			}

			// ??????????????????????????????????????????
			List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(wLoginUser,
							wList.stream().map(p -> p.ID).distinct().collect(Collectors.toList()))
					.List(SFCTaskStep.class);

			for (APSTaskStep wAPSTaskStep : wList) {
				if (wAPSTaskStep.Status != APSTaskStatus.Started.getValue()) {
					wResult.Result = false;
					wResult.FaultCode += StringUtils.Format("????????????{0}??????????????????!", wAPSTaskStep.StepName);
					return wResult;
				}
				// ?????????????????????????????????????????????
				List<SFCTaskStep> wTempList = wSFCTaskStepList.stream().filter(p -> p.TaskStepID == wAPSTaskStep.ID)
						.collect(Collectors.toList());
				if (wTempList.stream().anyMatch(p -> p.IsStartWork != 2)) {
					wResult.Result = false;
					wResult.FaultCode += StringUtils.Format("????????????{0}?????????-???{1}??????????????????!", wAPSTaskStep.StepName,
							wTempList.stream().filter(p -> p.IsStartWork != 2).findFirst().get().Operator);
					return wResult;
				}
				// ???????????????????????????????????????
				wIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wAPSTaskStep.ID,
						SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Step.getValue(), null,
						null, wErrorCode);
				if (wIPTList == null || wIPTList.size() <= 0) {
					continue;
				}

				wObjectList = this.SFC_QueryToDoAndDoneIPTItemList(wLoginUser, wAPSTaskStep.ID,
						SFCTaskType.MutualCheck.getValue());
				if (wObjectList == null || wObjectList.size() <= 0) {
					wResult.Result = false;
					return wResult;
				}

				wToDoList = (List<IPTItem>) wObjectList.get(0);

				if (wToDoList != null && wToDoList.size() > 0) {
					wResult.Result = false;
					wResult.FaultCode += StringUtils.Format("????????????{0}???-???{1}????????????????????????!", wAPSTaskStep.StepName,
							wToDoList.get(0).Text);
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
	public ServiceResult<List<Integer>> SFC_QueryYJStationIDList(BMSEmployee wLoginUser) {
		ServiceResult<List<Integer>> wResult = new ServiceResult<List<Integer>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = QMSUtils.getInstance().GetYJStationIDList(wLoginUser);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> SFC_CheckPGPower(BMSEmployee wLoginUser, List<Integer> wAPSTaskStepIDList) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			if (wAPSTaskStepIDList == null || wAPSTaskStepIDList.size() <= 0) {
				return wResult;
			}

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<FPCRoutePartPoint> wRoutePartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartPointListByRouteID(wLoginUser, 0, 0).List(FPCRoutePartPoint.class);
			if (wRoutePartPointList == null || wRoutePartPointList.size() <= 0) {
				wResult.Result = "???????????????????????????!";
				return wResult;
			}

			List<FPCRoutePart> wRoutePartList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartListByRouteID(wLoginUser, 0).List(FPCRoutePart.class);
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				wResult.Result = "???????????????????????????!";
				return wResult;
			}

			APSTaskPart wAPSTaskPart = null;
			List<APSTaskStep> wTaskStepList = null;
			List<SFCTaskStep> wSFCTaskStepList = null;

			Map<Integer, OMSOrder> wOrderMap = new HashMap<Integer, OMSOrder>();
			OMSOrder wOrder = null;
			for (Integer wTaskStepID : wAPSTaskStepIDList) {
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance().APS_QueryTaskStepByID(wLoginUser, wTaskStepID)
						.Info(APSTaskStep.class);
				if (wTaskStep == null || wTaskStep.ID <= 0) {
					wResult.Result = "????????????!";
					return wResult;
				}
				wOrder = null;
				if (wOrderMap.containsKey(wTaskStep.OrderID)) {
					wOrder = wOrderMap.get(wTaskStep.OrderID);
				} else {
					wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wTaskStep.OrderID)
							.Info(OMSOrder.class);
					wOrderMap.put(wTaskStep.OrderID, wOrder);
				}
				int wRouteID = wOrder.RouteID;

				// ??????????????????
				List<FPCRoutePart> wThisRoutePartList = wRoutePartList.stream().filter(p -> p.RouteID == wRouteID)
						.collect(Collectors.toList());
				String wMsg = QMSServiceImpl.getInstance().SFC_JudgeTaskStepIsCanDo(wLoginUser, wTaskStep,
						wThisRoutePartList, wErrorCode).Result;
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.Result = wMsg;
					return wResult;
				}

				Optional<FPCRoutePart> wRoutePartOption = wRoutePartList.stream()
						.filter(p -> p.RouteID == wRouteID && p.PartID == wTaskStep.PartID).findFirst();
				if (!wRoutePartOption.isPresent()) {
					wResult.Result = StringUtils.Format("???{0}???-???{1}???-???{2}???????????????????????????!", wTaskStep.LineName,
							wTaskStep.ProductNo, wTaskStep.PartName);
					return wResult;
				}

				List<FPCRoutePartPoint> wTempRoutePartPointList = wRoutePartPointList.stream()
						.filter(p -> p.RouteID == wRouteID && p.PartID == wRoutePartOption.get().PartID)
						.collect(Collectors.toList());
				if (wTempRoutePartPointList == null || wTempRoutePartPointList.size() <= 0) {
					wResult.Result = StringUtils.Format("???{0}???-???{1}???-???{2}???????????????????????????!", wTaskStep.LineName,
							wTaskStep.ProductNo, wTaskStep.PartName);
					return wResult;
				}

				Optional<FPCRoutePartPoint> wStepOption = wTempRoutePartPointList.stream()
						.filter(p -> p.PartPointID == wTaskStep.StepID).findFirst();
				if (!wStepOption.isPresent()) {
					wResult.Result = StringUtils.Format("???{0}???-???{1}???-???{2}???-???{3}????????????????????????!", wTaskStep.LineName,
							wTaskStep.ProductNo, wTaskStep.PartName, wTaskStep.StepName);
					return wResult;
				}

				List<Integer> wPreStepIDList = QMSUtils.getInstance().FPC_QueryPreStepIDList(wTempRoutePartPointList,
						wTaskStep.StepID);
				if (wPreStepIDList == null || wPreStepIDList.size() <= 0) {
					continue;
				}

				wAPSTaskPart = LOCOAPSServiceImpl.getInstance().APS_QueryTaskPartByID(wLoginUser, wTaskStep.TaskPartID)
						.Info(APSTaskPart.class);
				wTaskStepList = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepList(wLoginUser, -1, -1, wAPSTaskPart.ID,
								new ArrayList<Integer>(
										Arrays.asList(APSTaskStatus.Issued.getValue(), APSTaskStatus.Started.getValue(),
												APSTaskStatus.Done.getValue(), APSTaskStatus.Aborted.getValue())))
						.List(APSTaskStep.class);

				wTaskStepList = wTaskStepList.stream().filter(p -> wPreStepIDList.stream().anyMatch(q -> q == p.StepID))
						.collect(Collectors.toList());
				for (APSTaskStep wAPSTaskStep : wTaskStepList) {
					wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
							.SFC_QueryTaskStepList(wLoginUser, wAPSTaskStep.ID, -1, -1).List(SFCTaskStep.class);
					if (wAPSTaskStep.Status == APSTaskStatus.Done.getValue()
							|| wAPSTaskPart.Status == APSTaskStatus.Aborted.getValue()
							|| (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0
									&& wSFCTaskStepList.stream().allMatch(p -> p.IsStartWork == 2))) {
						continue;
					} else {
						wResult.Result = StringUtils.Format("???{0}????????????,???{1}???????????????!", wAPSTaskStep.StepName,
								wTaskStep.StepName);
						return wResult;
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
	public ServiceResult<List<Object>> SFC_QueryIPTItemValueList(BMSEmployee wLoginUser, int wSFCTaskIPTID) {
		ServiceResult<List<Object>> wResult = new ServiceResult<List<Object>>();
		try {
			wResult.Result = new ArrayList<Object>();

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			SFCTaskIPT wIPT = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wSFCTaskIPTID, wErrorCode);
			if (wIPT == null || wIPT.ID <= 0) {
				wResult.Result.add(new ArrayList<IPTItem>());
				wResult.Result.add(new ArrayList<IPTItem>());
				wResult.Result.add(new ArrayList<IPTValue>());
				return wResult;
			}

			wResult.Result = this.SFC_QueryToDoAndDoneIPTItemList(wLoginUser, wIPT.TaskStepID, wIPT.TaskType);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<Object>> SFC_QueryToDoAndDoneList(BMSEmployee wLoginUser, int wAPSTaskStepID,
			int wTaskType) {
		ServiceResult<List<Object>> wResult = new ServiceResult<List<Object>>();
		try {
			wResult.Result = this.SFC_QueryToDoAndDoneIPTItemList(wLoginUser, wAPSTaskStepID, wTaskType);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPT>> SFC_QueryRFTaskIPTList(BMSEmployee wLoginUser, int wAPSTaskStepID) {
		ServiceResult<List<SFCTaskIPT>> wResult = new ServiceResult<List<SFCTaskIPT>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCTaskIPT> wList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wAPSTaskStepID, -1, -1, -1,
					-1, null, SFCTaskStepType.Step.getValue(), null, null, wErrorCode);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			// ????????????????????????
			List<IPTValue> wValueList = null;
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			for (SFCTaskIPT wSFCTaskIPT : wList) {

				if (wSFCTaskIPT.OperatorList != null && wSFCTaskIPT.OperatorList.size() > 0) {
					wSFCTaskIPT.OperatorName = GetOperatorName(wSFCTaskIPT.OperatorList);
				} else {
					wSFCTaskIPT.OperatorName = "";
				}

				wValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSFCTaskIPT.ID, -1, -1,
						wErrorCode).Result;
				wSFCTaskIPT.OperateTime = (wValueList == null || wValueList.size() <= 0) ? wBaseTime
						: wValueList.stream().min(Comparator.comparing(IPTValue::getSubmitTime)).get().SubmitTime;

				// ??????????????????
				if (StringUtils.isNotEmpty(wSFCTaskIPT.PicUri)) {
					wSFCTaskIPT.IsPic = 1;
				} else {
					wSFCTaskIPT.IsPic = 0;
				}
			}

			wResult.Result = wList;
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????
	 */
	private String GetOperatorName(List<Integer> operatorList) {
		String wResult = "";
		try {
			List<String> wNames = new ArrayList<String>();
			for (Integer integer : operatorList) {
				String wName = QMSConstants.GetBMSEmployeeName(integer);
				wNames.add(wName);
			}
			wResult = StringUtils.Join(",", wNames);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSDepartment>> SFC_QueryClassListByAreaID(BMSEmployee wLoginUser, int wAreaID) {
		ServiceResult<List<BMSDepartment>> wResult = new ServiceResult<List<BMSDepartment>>();
		try {
			wResult.Result = GetClassListByAreaID(wAreaID,
					CoreServiceImpl.getInstance().BMS_QueryDepartmentList(wLoginUser).List(BMSDepartment.class));
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private List<BMSDepartment> GetClassListByAreaID(int wWorkAreaID, List<BMSDepartment> wAllList) {
		List<BMSDepartment> wResult = new ArrayList<BMSDepartment>();
		try {
			List<BMSDepartment> wList = FindChildList(wAllList, wWorkAreaID);
			if (wList != null && wList.size() > 0) {
				for (BMSDepartment wBMSDepartment : wList) {
					if (wBMSDepartment.Type == BMSDepartmentType.Class.getValue()) {
						wResult.add(wBMSDepartment);
					}
					wResult.addAll(GetClassListByAreaID(wBMSDepartment.ID, wAllList));
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<BMSDepartment> FindChildList(List<BMSDepartment> wAllList, int wDepartmentID) {
		List<BMSDepartment> wResult = new ArrayList<BMSDepartment>();
		try {
			wResult = wAllList.stream().filter(p -> p.ParentID == wDepartmentID).collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSEmployee>> SFC_QueryClassMemberListByClassID(BMSEmployee wLoginUser, int wClassID) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		try {
			if (!QMSConstants.GetBMSDepartmentList().containsKey(wClassID)) {
				return wResult;
			}

//			wResult.Result = QMSConstants.GetBMSEmployeeList().values().stream().filter(p -> p.DepartmentID == wClassID)
//					.collect(Collectors.toList());

			// ??????????????????????????????
			wResult.Result = LOCOAPSServiceImpl.getInstance().SCH_EmployeePartList(wLoginUser, wClassID)
					.CustomArray("SourceList", BMSEmployee.class);

			if (wResult.Result != null && wResult.Result.size() > 0) {
				for (BMSEmployee wBMSEmployee : wResult.Result) {
					if (QMSConstants.GetBMSPositionList().containsKey(wBMSEmployee.Position)
							&& QMSConstants.GetBMSPositionList().get(wBMSEmployee.Position).DutyID == 1) {
						wResult.CustomResult.put("Monitor", wBMSEmployee);
					}
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ServiceResult<IPTPreCheckProblem> SFC_QueryProblemNodeInfo(BMSEmployee wLoginUser, int wProblemID) {
		ServiceResult<IPTPreCheckProblem> wResult = new ServiceResult<IPTPreCheckProblem>();
		wResult.Result = new IPTPreCheckProblem();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser, wProblemID, wErrorCode);
			if (wResult.Result == null || wResult.Result.ID <= 0) {
				wResult.CustomResult.put("PreCheckNode", new IPTValue());
				wResult.CustomResult.put("SelfCheckNode", new IPTValue());
				wResult.CustomResult.put("MutualCheckNode", new IPTValue());
				wResult.CustomResult.put("SpecialCheckNode", new IPTValue());
				return wResult;
			}
			// ????????????
			List<Object> wObjects = this.SFC_QueryToDoAndDoneList(wLoginUser, wResult.Result.APSTaskStepID,
					SFCTaskType.PreCheck.getValue()).Result;
			List<IPTValue> wValueList = wObjects.size() == 3 ? (List<IPTValue>) wObjects.get(2)
					: new ArrayList<IPTValue>();
			if (wValueList != null && wValueList.stream().anyMatch(p -> p.IPTItemID == wResult.Result.IPTItemID)) {
				wResult.CustomResult.put("PreCheckNode",
						wValueList.stream().filter(p -> p.IPTItemID == wResult.Result.IPTItemID).findFirst().get());
			} else {
				wResult.CustomResult.put("PreCheckNode", new IPTValue());
			}
			// ????????????
			List<SFCTaskIPT> wSelfIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wResult.Result.ID,
					SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Question.getValue(), null, null,
					wErrorCode);

			if (wSelfIPTList == null || wSelfIPTList.size() <= 0) {
				wResult.CustomResult.put("SelfCheckNode", new IPTValue());
			} else {
				List<IPTValue> wSelfValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser,
						wSelfIPTList.get(0).ID, SFCTaskType.SelfCheck.getValue(), IPTItemType.InPlant.getValue(),
						wErrorCode).Result;
				if (wSelfValueList != null && wSelfValueList.size() > 0
						&& wSelfValueList.stream().anyMatch(p -> p.IPTItemID == wResult.Result.IPTItemID)) {
					IPTValue wMaxValue = wSelfValueList.stream().filter(p -> p.IPTItemID == wResult.Result.IPTItemID)
							.collect(Collectors.toList()).stream().max(Comparator.comparing(IPTValue::getSubmitTime))
							.get();
					wResult.CustomResult.put("SelfCheckNode", wMaxValue);
				} else {
					wResult.CustomResult.put("SelfCheckNode", new IPTValue());
				}
			}
			// ????????????
			List<SFCTaskIPT> wMutualIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wResult.Result.ID,
					SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Question.getValue(), null,
					null, wErrorCode);
			if (wMutualIPTList == null || wMutualIPTList.size() <= 0) {
				wResult.CustomResult.put("MutualCheckNode", new IPTValue());
			} else {
				List<IPTValue> wMutalValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser,
						wMutualIPTList.get(0).ID, SFCTaskType.MutualCheck.getValue(), IPTItemType.InPlant.getValue(),
						wErrorCode).Result;
				if (wMutalValueList != null && wMutalValueList.size() > 0
						&& wMutalValueList.stream().anyMatch(p -> p.IPTItemID == wResult.Result.IPTItemID)) {
					IPTValue wMaxValue = wMutalValueList.stream().filter(p -> p.IPTItemID == wResult.Result.IPTItemID)
							.collect(Collectors.toList()).stream().max(Comparator.comparing(IPTValue::getSubmitTime))
							.get();
					wResult.CustomResult.put("MutualCheckNode", wMaxValue);
				} else {
					wResult.CustomResult.put("MutualCheckNode", new IPTValue());
				}
			}
			// ????????????
			List<SFCTaskIPT> wSpecialIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wResult.Result.ID,
					SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Question.getValue(), null,
					null, wErrorCode);
			if (wSpecialIPTList == null || wSpecialIPTList.size() <= 0) {
				wResult.CustomResult.put("SpecialCheckNode", new IPTValue());
			} else {
				List<IPTValue> wSpecialValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser,
						wSpecialIPTList.get(0).ID, SFCTaskType.SpecialCheck.getValue(), IPTItemType.InPlant.getValue(),
						wErrorCode).Result;
				if (wSpecialValueList != null && wSpecialValueList.size() > 0
						&& wSpecialValueList.stream().anyMatch(p -> p.IPTItemID == wResult.Result.IPTItemID)) {
					IPTValue wMaxValue = wSpecialValueList.stream().filter(p -> p.IPTItemID == wResult.Result.IPTItemID)
							.collect(Collectors.toList()).stream().max(Comparator.comparing(IPTValue::getSubmitTime))
							.get();
					wResult.CustomResult.put("SpecialCheckNode", wMaxValue);
				} else {
					wResult.CustomResult.put("SpecialCheckNode", new IPTValue());
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTPreCheckReport> SFC_QueryPreCheckReport(BMSEmployee wLoginUser, String wPartNo) {
		ServiceResult<IPTPreCheckReport> wResult = new ServiceResult<IPTPreCheckReport>();
		wResult.Result = new IPTPreCheckReport();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<SFCTaskIPT> wTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, -1, -1,
					SFCTaskType.PreCheck.getValue(), -1, -1, -1, wPartNo, null, null, wErrorCode);
			if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
				return wResult;
			}

			wResult.Result.PartNo = wPartNo;
			OMSOrder wOMSOrder = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderByID(wLoginUser, wTaskIPTList.get(0).OrderID).Info(OMSOrder.class);
			if (wOMSOrder != null && wOMSOrder.ID > 0) {
				wResult.Result.CustomerName = wOMSOrder.BureauSection;
				wResult.Result.ID = 0;
				wResult.Result.OrderID = wOMSOrder.ID;
				wResult.Result.OrderNo = wOMSOrder.OrderNo;
				wResult.Result.PartNo = wOMSOrder.PartNo;
				wResult.Result.CreateID = wLoginUser.ID;
				wResult.Result.CreateTime = Calendar.getInstance();
				wResult.Result.Status = BPMStatus.Save.getValue();
			}

			wResult.Result.IPTPreCheckItemList = new ArrayList<IPTPreCheckItem>();

			IPTPreCheckItem wIPTPreCheckItem = null;
			for (SFCTaskIPT wSFCTaskIPT : wTaskIPTList) {
				wIPTPreCheckItem = this.SFC_QueryPreCheckItem(wLoginUser, wSFCTaskIPT);
				if (wIPTPreCheckItem != null) {
					wResult.Result.IPTPreCheckItemList.add(wIPTPreCheckItem);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param wSFCTaskIPT
	 * @return
	 */
	private IPTPreCheckItem SFC_QueryPreCheckItem(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		IPTPreCheckItem wResult = new IPTPreCheckItem();
		try {
			if (wSFCTaskIPT == null || wSFCTaskIPT.ID <= 0) {
				return wResult;
			}

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.ItemName = QMSConstants.GetFPCStepName(wSFCTaskIPT.PartPointID);
			wResult.PreChecker = QMSConstants.GetBMSEmployeeName(wSFCTaskIPT.OperatorID);
			wResult.ReportID = 0;
			wResult.ItemID = wSFCTaskIPT.PartPointID;
			wResult.IPTProblemList = IPTPreCheckProblemDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskIPT.ID, -1,
					-1, -1, -1, null, wErrorCode);

			IPTStandard wStandard = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
					wSFCTaskIPT.ModuleVersionID, wErrorCode).Result;
			if (wStandard != null && wStandard.ID > 0) {
				Map<Long, List<IPTItem>> wMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser,
						new ArrayList<Long>(Arrays.asList(wStandard.ID)), wErrorCode).Result;
				if (wMap != null && wMap.size() > 0 && wMap.containsKey(wStandard.ID)) {
					wResult.IPTItemList = wMap.get(wStandard.ID);
					wResult.IPTValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSFCTaskIPT.ID, -1, -1,
							wErrorCode).Result;
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPT>> SFC_QueryProblemTaskAll(BMSEmployee wLoginUser, SFCTaskType wTaskType) {
		ServiceResult<List<SFCTaskIPT>> wResult = new ServiceResult<List<SFCTaskIPT>>();
		wResult.Result = new ArrayList<SFCTaskIPT>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);

			Calendar wStartTime = Calendar.getInstance();
			wStartTime.set(Calendar.HOUR_OF_DAY, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);
			Calendar wEndTime = Calendar.getInstance();
			wEndTime.set(Calendar.HOUR_OF_DAY, 23);
			wEndTime.set(Calendar.MINUTE, 59);
			wEndTime.set(Calendar.SECOND, 59);

			List<SFCTaskIPT> wList = new ArrayList<SFCTaskIPT>();
			// ??????????????????????????????
//			List<SFCTaskIPT> wTodayCheckTaskList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, -1,
////					wTaskType.getValue(), -1, wShiftID, -1, null, SFCTaskStepType.Question.getValue(), null, null,
//					wErrorCode);
			List<SFCTaskIPT> wTodayCheckTaskList = SFCTaskIPTDAO.getInstance().SelectListByTime(wLoginUser,
					SFCTaskType.SpecialCheck.getValue(), SFCTaskStepType.Question.getValue(), "", wStartTime, wEndTime,
					-1, wErrorCode);

			if (wTodayCheckTaskList != null && wTodayCheckTaskList.size() > 0) {
				wList.addAll(wTodayCheckTaskList);
			}
			// ???????????????????????????????????????
			List<SFCTaskIPT> wYestodayCheckTaskList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, -1,
					wTaskType.getValue(), -1, -1, -1,
					new ArrayList<Integer>(Arrays.asList(SFCTaskStatus.Active.getValue())),
					SFCTaskStepType.Question.getValue(), null, null, wErrorCode);
			wYestodayCheckTaskList.removeIf(p -> p.ShiftID >= wShiftID);
			if (wYestodayCheckTaskList != null && wYestodayCheckTaskList.size() > 0) {
				wList.addAll(wYestodayCheckTaskList);
			}
			// ?????????
			wList = new ArrayList<SFCTaskIPT>(wList.stream()
					.collect(Collectors.toMap(SFCTaskIPT::getID, account -> account, (k1, k2) -> k2)).values());
			// ?????????????????????????????????
			List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepListByEmployee(wLoginUser, Calendar.getInstance()).List(SFCTaskStep.class);
			if (wSFCTaskStepList == null || wSFCTaskStepList.size() <= 0) {
				wSFCTaskStepList = new ArrayList<SFCTaskStep>();
			}

			Map<Integer, List<IPTValue>> wValueMap = new HashMap<>();
			if (wList != null && wList.size() > 0) {
				wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser,
								wList.stream().map(p -> Integer.valueOf(p.TaskStepID)).collect(Collectors.toList()))
						.List(SFCTaskStep.class);
				wSFCTaskStepList.removeIf(p -> (p.Type == SFCTaskStepType.Step.getValue()));
				wValueMap = (IPTStandardDAO.getInstance().SelectValue(wLoginUser,
						wList.stream().map(p -> Integer.valueOf(p.ID)).collect(Collectors.toList()),
						IPTMode.Default.getValue(), -1, wErrorCode)).Result;
			}

			List<SFCTaskStep> wTempSFCTaskStepList = null;
			// ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			for (SFCTaskIPT wSFCTaskIPT : wList) {
				LFSWorkAreaStation wLFSWorkAreaStation;
				List<Integer> wCheckIDList;
				int wWorkAreaID;
				List<IPTValue> wSelfValueList;
				List<SFCTaskIPT> wSelfCheckList;
				List<IPTValue> wValueTempList;
				List<IPTValue> wMutualValueList;
				List<SFCTaskIPT> wMutualList;
				List<IPTValue> wTempValueList;
				switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
				case SelfCheck:
					wTempSFCTaskStepList = (List<SFCTaskStep>) wSFCTaskStepList.stream()
							.filter(p -> (p.TaskStepID == wSFCTaskIPT.TaskStepID)).collect(Collectors.toList());
					if (wTempSFCTaskStepList == null || wTempSFCTaskStepList.size() <= 0) {
						continue;
					}
					wTempValueList = new ArrayList<>();
					if (wValueMap.containsKey(Integer.valueOf(wSFCTaskIPT.ID))) {
						wTempValueList = wValueMap.get(Integer.valueOf(wSFCTaskIPT.ID));
					}

					wMutualList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskIPT.TaskStepID,
							SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Question.getValue(),
							null, null, wErrorCode);
					wMutualValueList = new ArrayList<>();
					if (wMutualList != null && wMutualList.size() > 0) {
						wMutualValueList = (List<IPTValue>) (IPTStandardDAO.getInstance().SelectValue(wLoginUser,
								((SFCTaskIPT) wMutualList.get(0)).ID, -1, -1, wErrorCode)).Result;
					}

					wValueTempList = wTempValueList;

					if (wTempSFCTaskStepList.size() == 1) {
						if (wTempSFCTaskStepList.stream().anyMatch(p -> (p.OperatorID == wLoginUser.ID))) {
							((List<SFCTaskIPT>) wResult.Result).add(wSFCTaskIPT);
						}
						continue;
					}
					if (wTempSFCTaskStepList.stream().anyMatch(p -> (p.OperatorID == wLoginUser.ID))
							&& (wTempValueList == null || wTempValueList.size() <= 0)) {
						((List<SFCTaskIPT>) wResult.Result).add(wSFCTaskIPT);
						continue;
					}
					if (wTempSFCTaskStepList.stream().anyMatch(p -> (p.OperatorID == wLoginUser.ID))
							&& wTempValueList != null && wTempValueList.size() > 0
							&& wTempValueList.stream().anyMatch(p -> (p.SubmitID == wLoginUser.ID))) {
						((List<SFCTaskIPT>) wResult.Result).add(wSFCTaskIPT);
						continue;
					}
					if (wTempSFCTaskStepList.stream().anyMatch(p -> (p.OperatorID == wLoginUser.ID))
							&& wTempValueList != null && wTempValueList.size() > 0
							&& !wTempValueList.stream().anyMatch(p -> (p.SubmitID == wLoginUser.ID))
							&& wTempSFCTaskStepList.stream()
									.filter(p -> !wValueTempList.stream().anyMatch(q -> q.SubmitID == p.OperatorID))
									.count() >= 2L
							&& (wMutualValueList == null || wMutualValueList.size() <= 0
									|| !wMutualValueList.stream().anyMatch(p -> (p.SubmitID == wLoginUser.ID)))) {
						((List<SFCTaskIPT>) wResult.Result).add(wSFCTaskIPT);
					}

				case MutualCheck:
					wTempSFCTaskStepList = (List<SFCTaskStep>) wSFCTaskStepList.stream()
							.filter(p -> (p.TaskStepID == wSFCTaskIPT.TaskStepID)).collect(Collectors.toList());
					if (wTempSFCTaskStepList == null || wTempSFCTaskStepList.size() <= 0) {
						continue;
					}

					wSelfCheckList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskIPT.TaskStepID,
							SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Question.getValue(),
							null, null, wErrorCode);
					wSelfValueList = new ArrayList<>();
					if (wSelfCheckList != null && wSelfCheckList.size() > 0) {
						wSelfValueList = (List<IPTValue>) (IPTStandardDAO.getInstance().SelectValue(wLoginUser,
								((SFCTaskIPT) wSelfCheckList.get(0)).ID, -1, -1, wErrorCode)).Result;
					}

					if (wTempSFCTaskStepList.size() == 1) {
						// ???????????????????????????
						AddValueWhenSizeIsOne(wLoginUser, wResult, wSFCTaskIPT, wTempSFCTaskStepList);
						// ??????????????????????????????
					} else if (wTempSFCTaskStepList.stream().anyMatch(p -> (p.OperatorID == wLoginUser.ID))
							&& !wSelfValueList.stream().anyMatch(p -> (p.SubmitID == wLoginUser.ID))) {
						wResult.Result.add(wSFCTaskIPT);
					}
				case SpecialCheck:
					wWorkAreaID = 0;
					wCheckIDList = null;
					wLFSWorkAreaStation = null;
					wLFSWorkAreaStation = QMSConstants.GetLFSWorkAreaStation(wSFCTaskIPT.StationID);
					wWorkAreaID = wLFSWorkAreaStation.WorkAreaID;
					wCheckIDList = QMSConstants.GetLFSWorkAreaCheckerID(wWorkAreaID);
					if (wCheckIDList != null && wCheckIDList.stream().anyMatch(p -> (p.intValue() == wLoginUser.ID))) {
						((List<SFCTaskIPT>) wResult.Result).add(wSFCTaskIPT);
					}
				default:
					break;
				}
			}

			// ????????????????????????
			if (wResult.Result != null && (wResult.Result).size() > 0) {
				// ????????????????????????
				RefreshTaskIPTStatus(wLoginUser, wResult, wErrorCode);
			}

			// ?????????????????????
			if (wResult.Result.size() > 0) {
				for (SFCTaskIPT wSFCTaskIPT : wResult.Result) {
					IPTPreCheckProblem wProblem = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
							wSFCTaskIPT.TaskStepID, wErrorCode);
					if (wProblem == null || wProblem.ID <= 0) {
						continue;
					}
					wSFCTaskIPT.PartPointName = wProblem.IPTItemName;
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wResult
	 * @param wErrorCode
	 */
	private void RefreshTaskIPTStatus(BMSEmployee wLoginUser, ServiceResult<List<SFCTaskIPT>> wResult,
			OutResult<Integer> wErrorCode) {
		try {
			OMSOrder wOrder = null;
			for (SFCTaskIPT wSFCTaskIPT : wResult.Result) {
				wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTaskIPT.OrderID)
						.Info(OMSOrder.class);
				if (wOrder != null) {
					if (wOrder.ID <= 0) {
						continue;
					}
					wSFCTaskIPT.CustomerID = wOrder.BureauSectionID;
					wSFCTaskIPT.CustomerName = wOrder.BureauSection;

					ServiceResult<IPTItem> wServiceResult = this.SFC_QueryProblemItemInfo(wLoginUser, wSFCTaskIPT.ID);
					if (wServiceResult.Result.ID <= 0) {
						wSFCTaskIPT.Status = SFCTaskStatus.Done.getValue();
						UpdateMessageStateWhenTaskFinishOrNotFinish(wLoginUser, wSFCTaskIPT);
					} else {
						wSFCTaskIPT.Status = SFCTaskStatus.Active.getValue();
						UpdateMessageStateWhenTaskFinishOrNotFinish(wLoginUser, wSFCTaskIPT);
					}

					SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<IPTItem> SFC_QueryProblemItemInfo(BMSEmployee wLoginUser, int wSFCTaskIPTID) {
		ServiceResult<IPTItem> wResult = new ServiceResult<IPTItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		wResult.Result = new IPTItem();
		try {
			SFCTaskIPT wItem = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wSFCTaskIPTID, wErrorCode);
			if (wItem == null || wItem.ID <= 0) {
				return wResult;
			}

			IPTPreCheckProblem wProblem = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser, wItem.TaskStepID,
					wErrorCode);
			if (wProblem == null || wProblem.ID <= 0) {
				return wResult;
			}

			List<IPTValue> wValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSFCTaskIPTID, -1, -1,
					wErrorCode).Result;
			IPTItem wDoneItem = new IPTItem();
			IPTValue wValue = new IPTValue();

			// ?????????
			List<SFCTaskIPT> wSelfIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wItem.TaskStepID,
					SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Question.getValue(), null, null,
					wErrorCode);
			SFCTaskIPT wSelfIPT = (wSelfIPTList != null && wSelfIPTList.size() > 0) ? wSelfIPTList.get(0) : null;
			List<IPTValue> wSelfValueList = wSelfIPT == null ? new ArrayList<IPTValue>()
					: IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSelfIPT.ID, -1, -1, wErrorCode).Result;
			// ?????????
			List<SFCTaskIPT> wMutualIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wItem.TaskStepID,
					SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Question.getValue(), null,
					null, wErrorCode);
			SFCTaskIPT wMutualIPT = (wMutualIPTList != null && wMutualIPTList.size() > 0) ? wMutualIPTList.get(0)
					: null;
			List<IPTValue> wMutualValueList = wMutualIPT == null ? new ArrayList<IPTValue>()
					: IPTStandardDAO.getInstance().SelectValue(wLoginUser, wMutualIPT.ID, -1, -1, wErrorCode).Result;

			if (wValueList == null) {
				wValueList = new ArrayList<IPTValue>();
			}
			switch (SFCTaskType.getEnumType(wItem.TaskType)) {
			case SelfCheck:
				if (wValueList == null || wValueList.size() <= 0 || wValueList.stream().allMatch(p -> p.Result == 2)) {
					wResult.Result = wProblem.IPTItem;
					wResult.CustomResult.put("DoneItem", wDoneItem);
					wResult.CustomResult.put("Value",
							wValueList.size() > 0
									? wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get()
									: wValue);
					wResult.CustomResult.put("OtherValue", new IPTValue());
				} else if (wMutualValueList != null && wMutualValueList.size() > 0
						&& wMutualValueList.stream().allMatch(p -> p.Result == 2)
						&& wMutualValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get().SubmitTime
								.compareTo(wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime))
										.get().SubmitTime) > 0) {
					wResult.Result = wProblem.IPTItem;
					wResult.CustomResult.put("DoneItem", wDoneItem);
					wResult.CustomResult.put("Value",
							wValueList.size() > 0
									? wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get()
									: wValue);
					wResult.CustomResult.put("OtherValue", new IPTValue());
				} else {
					wValue = wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get();
					wResult.CustomResult.put("DoneItem", wProblem.IPTItem);
					wResult.CustomResult.put("Value", wValue);
					wResult.CustomResult.put("OtherValue", new IPTValue());
				}
				break;
			case MutualCheck:
				if (wValueList == null || wValueList.size() <= 0) {
					wResult.Result = wProblem.IPTItem;
					wResult.CustomResult.put("DoneItem", wDoneItem);
					wResult.CustomResult.put("Value",
							wValueList.size() > 0
									? wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get()
									: wValue);
					wResult.CustomResult.put("OtherValue",
							wSelfValueList.size() > 0
									? wSelfValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get()
									: wValue);
				} else if (wValueList != null && wValueList.size() > 0
						&& wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get().Result == 2
						&& wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get().SubmitTime
								.compareTo(wSelfValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime))
										.get().SubmitTime) < 0
						&& wSelfValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime))
								.get().Result == 1) {
					wResult.Result = wProblem.IPTItem;
					wResult.CustomResult.put("DoneItem", wDoneItem);
					wResult.CustomResult.put("Value",
							wValueList.size() > 0
									? wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get()
									: wValue);
					wResult.CustomResult.put("OtherValue",
							wSelfValueList.size() > 0
									? wSelfValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get()
									: wValue);
				} else {
					wValue = wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get();
					wResult.CustomResult.put("DoneItem", wProblem.IPTItem);
					wResult.CustomResult.put("Value", wValue);
					wResult.CustomResult.put("OtherValue", new IPTValue());
				}
				break;
			case SpecialCheck:
				if (wValueList == null || wValueList.size() <= 0) {
					wResult.Result = wProblem.IPTItem;
					wResult.CustomResult.put("DoneItem", wDoneItem);
					wResult.CustomResult.put("Value", wValue);
					wResult.CustomResult.put("OtherValue",
							wMutualValueList.size() > 0
									? wMutualValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get()
									: wValue);
				} else {
					wValue = wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get();
					wResult.CustomResult.put("DoneItem", wProblem.IPTItem);
					wResult.CustomResult.put("Value", wValue);
					wResult.CustomResult.put("OtherValue", new IPTValue());
				}
				break;
			default:
				break;
			}

			// ???????????????
			if (wResult.Result.ID > 0) {
				IPTItem wIPTItem = IPTItemDAO.getInstance().SelectByID(wLoginUser, (int) wResult.Result.ID, wErrorCode);
				if (wIPTItem != null && wIPTItem.ID > 0) {
					List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wIPTItem.VID, -1, -1,
							wErrorCode);
					List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
					wResult.Result.Text = this.GetDescription(wFatherText, wIPTItem);
				}
			} else if (((IPTItem) wResult.CustomResult.get("DoneItem")).ID > 0) {
				IPTItem wIPTItem = IPTItemDAO.getInstance().SelectByID(wLoginUser,
						(int) ((IPTItem) wResult.CustomResult.get("DoneItem")).ID, wErrorCode);
				if (wIPTItem != null && wIPTItem.ID > 0) {
					List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wIPTItem.VID, -1, -1,
							wErrorCode);
					List<String> wFatherText = this.GetFatherText(wItemList, wIPTItem);
					((IPTItem) wResult.CustomResult.get("DoneItem")).Text = this.GetDescription(wFatherText, wIPTItem);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????????????????????????????
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

	private String GetDescription(List<String> wFatherText, IPTItem wIPTItem) {
		String wResult = "";
		try {
			if (wFatherText == null || wFatherText.size() <= 0) {
				wResult = StringUtils.Format("{0}", wIPTItem.Text);
			} else {
				Collections.reverse(wFatherText);
				for (String wFather : wFatherText) {
					wResult += StringUtils.Format("???{0}???-", wFather);
				}
				wResult += wIPTItem.Text;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	// ????????????
	private int SFC_TriggerTaskIPT(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
		int wResult = 0;
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
	public ServiceResult<Integer> SFC_ProblemValueSubmit(BMSEmployee wLoginUser, IPTValue wIPTValue,
			SFCTaskIPT wSFCTaskIPT) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<SFCTaskStep> wSFCTaskStepList = SFCTaskIPTDAO.getInstance().SelectTaskStepProblem(wLoginUser,
					wSFCTaskIPT, wErrorCode);

			IPTPreCheckProblem wProblem = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
					wSFCTaskIPT.TaskStepID, wErrorCode);

			if (!wSFCTaskIPT.OperatorList.stream().anyMatch(p -> (p.intValue() == wLoginUser.ID))) {
				wSFCTaskIPT.OperatorList.add(Integer.valueOf(wLoginUser.ID));
			}

			switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
			case SelfCheck:
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					Optional<SFCTaskStep> wOption = wSFCTaskStepList.stream()
							.filter(p -> p.TaskStepID == wSFCTaskIPT.TaskStepID
									&& p.Type == SFCTaskStepType.Question.getValue())
							.findFirst();
					if (wOption.isPresent() && wProblem != null && wProblem.ID > 0) {
						// ?????????
						wProblem.DoPersonID = wLoginUser.ID;
						// ????????????
						wProblem.DoClassID = QMSConstants.GetBMSEmployee(wOption.get().MonitorID).DepartmentID;
						if (wIPTValue.Result == 1) {
							wProblem.Status = IPTPreCheckProblemStatus.ToMutual.getValue();
							// ????????????
							List<SFCTaskIPT> wHJList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
									wSFCTaskIPT.TaskStepID, SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null,
									SFCTaskStepType.Question.getValue(), null, null, wErrorCode);
							if (wHJList == null || wHJList.size() <= 0) {
								wSFCTaskIPT.TaskType = SFCTaskType.MutualCheck.getValue();
								this.SFC_TriggerTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
							} else {
								wHJList.get(0).Status = 1;
								SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wHJList.get(0), wErrorCode);
							}
							// ????????????????????????
							wSFCTaskIPT.SubmitTime = Calendar.getInstance();
							wSFCTaskIPT.EndTime = Calendar.getInstance();
							wSFCTaskIPT.Status = SFCTaskStatus.Done.getValue();
							SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
						} else if (wIPTValue.Result == 2) {
							wProblem.Status = IPTPreCheckProblemStatus.Issued.getValue();
						}
					}
				}
				break;
			case MutualCheck:
				if (wProblem != null && wProblem.ID > 0) {
					if (wIPTValue.Result == 1) {
						wProblem.Status = IPTPreCheckProblemStatus.ToSpecial.getValue();
						// ????????????
						List<SFCTaskIPT> wHJList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
								wSFCTaskIPT.TaskStepID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, null,
								SFCTaskStepType.Question.getValue(), null, null, wErrorCode);
						if (wHJList == null || wHJList.size() <= 0) {
							wSFCTaskIPT.TaskType = SFCTaskType.SpecialCheck.getValue();
							this.SFC_TriggerTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
						}
						// ????????????????????????
						wSFCTaskIPT.SubmitTime = Calendar.getInstance();
						wSFCTaskIPT.EndTime = Calendar.getInstance();
						wSFCTaskIPT.Status = SFCTaskStatus.Done.getValue();
						SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
						// ?????????????????????????????????
						List<SFCTaskStep> wList = LOCOAPSServiceImpl.getInstance()
								.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1)
								.List(SFCTaskStep.class);
						wList = wList.stream().filter(p -> p.Type == SFCTaskStepType.Question.getValue())
								.collect(Collectors.toList());
						if (wList != null && wList.size() > 0) {
							wList.forEach(p -> {
								p.IsStartWork = 2;
								p.EditTime = Calendar.getInstance();
							});
							for (SFCTaskStep wSFCTaskStep : wList) {
								LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
							}
						}
					} else if (wIPTValue.Result == 2) {
						wSFCTaskIPT.SubmitTime = Calendar.getInstance();
						wSFCTaskIPT.EndTime = Calendar.getInstance();
						wSFCTaskIPT.Status = SFCTaskStatus.Done.getValue();
						SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);

						List<SFCTaskIPT> wMuList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
								wSFCTaskIPT.TaskStepID, SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null,
								SFCTaskStepType.Question.getValue(), null, null, wErrorCode);
						if (wMuList != null && wMuList.size() > 0) {
							wMuList.get(0).Status = SFCTaskStatus.Active.getValue();
							SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wMuList.get(0), wErrorCode);
						}

						wProblem.Status = IPTPreCheckProblemStatus.Issued.getValue();
					}
				}
				break;
			case SpecialCheck:
				if (wProblem != null && wProblem.ID > 0) {
					wProblem.Status = IPTPreCheckProblemStatus.Done.getValue();
					// ????????????????????????
					wSFCTaskIPT.SubmitTime = Calendar.getInstance();
					wSFCTaskIPT.EndTime = Calendar.getInstance();
					wSFCTaskIPT.Status = SFCTaskStatus.Done.getValue();
					SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
				}
				break;
			default:
				break;
			}

			if (wProblem != null && wProblem.ID > 0) {
				wResult.Result = IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wProblem, wErrorCode);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSPartItem>> SFC_PartItemList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<MSSPartItem>> wResult = new ServiceResult<List<MSSPartItem>>();
		wResult.Result = new ArrayList<MSSPartItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<SFCTaskIPT> wIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID, -1,
					SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);
			if (wIPTList == null || wIPTList.size() <= 0) {
				return wResult;
			}

			List<IPTValue> wValueList = null;
			MSSPartItem wMSSPartItem = null;
			for (SFCTaskIPT wSFCTaskIPT : wIPTList) {
				// ????????????ID?????????????????????
				Map<Long, List<IPTItem>> wMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser,
						new ArrayList<Long>(Arrays.asList(Long.valueOf(wSFCTaskIPT.ModuleVersionID))),
						wErrorCode).Result;
				if (wMap == null || wMap.size() <= 0 || !wMap.containsKey(Long.valueOf(wSFCTaskIPT.ModuleVersionID))) {
					continue;
				}
				List<IPTItem> wItemList = wMap.get(Long.valueOf(wSFCTaskIPT.ModuleVersionID));
				if (wItemList == null || wItemList.size() <= 0) {
					continue;
				}
				// ????????????ID?????????????????????
				wValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSFCTaskIPT.ID, -1, -1,
						wErrorCode).Result;
				if (wValueList == null) {
					wValueList = new ArrayList<IPTValue>();
				}
				// ???????????????
				wItemList = wItemList.stream().filter(p -> StringUtils.isNotEmpty(p.PartsCoding))
						.collect(Collectors.toList());
				if (wItemList == null || wItemList.size() <= 0) {
					continue;
				}
				for (IPTItem wIPTItem : wItemList) {
					wMSSPartItem = new MSSPartItem();
					wMSSPartItem.ProductNo = wSFCTaskIPT.ProductNo;
					wMSSPartItem.LineID = wSFCTaskIPT.LineID;
					wMSSPartItem.LineName = wSFCTaskIPT.LineName;
					wMSSPartItem.OrderID = wSFCTaskIPT.OrderID;
					wMSSPartItem.OrderNo = wSFCTaskIPT.OrderNo;
					wMSSPartItem.CustomerID = wSFCTaskIPT.CustomerID;
					wMSSPartItem.CustomerName = wSFCTaskIPT.CustomerName;
					wMSSPartItem.Code = wIPTItem.PartsCoding;

					// ??????
					// ??????
					// ??????
					Optional<IPTValue> wOption = wValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
							.findFirst();
					if (wOption.isPresent()) {
						wMSSPartItem.SupplierName = wOption.get().Manufactor;
						wMSSPartItem.SupplierProductNo = wOption.get().Modal;
						wMSSPartItem.SupplierPartNo = wOption.get().Number;
					} else {
						wMSSPartItem.SupplierName = "";
						wMSSPartItem.SupplierProductNo = "";
						wMSSPartItem.SupplierPartNo = "";
					}

					wResult.Result.add(wMSSPartItem);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSPartItem>> SFC_QueryPrePartItemList(BMSEmployee wLoginUser, String wCode,
			String wSuplierPartNo, int wLineID, int wProductID) {
		ServiceResult<List<MSSPartItem>> wResult = new ServiceResult<>();
		wResult.Result = new ArrayList<>();
		OutResult<Integer> wErrorCode = new OutResult<>(Integer.valueOf(0));
		try {

			List<OMSOrder> wOrderList = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderList(wLoginUser, -1, "", wLineID, wProductID, -1, "", "", 1,
							StringUtils.parseListArgs(OMSOrderStatus.Repairing.getValue(),
									OMSOrderStatus.FinishedWork.getValue(), OMSOrderStatus.ToOutChcek.getValue(),
									OMSOrderStatus.ToOutConfirm.getValue()))
					.List(OMSOrder.class);
			if (wOrderList == null || wOrderList.size() <= 0) {
				return wResult;
			}

			List<SFCTaskIPT> wIPTList = null;
			List<IPTValue> wValueList = null;
			MSSPartItem wMSSPartItem = null;
			for (OMSOrder wOMSOrder : wOrderList) {
				wIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOMSOrder.ID, -1,
						SFCTaskType.PreCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);
				if (wIPTList == null || wIPTList.size() <= 0) {
					continue;
				}

				for (SFCTaskIPT wSFCTaskIPT : wIPTList) {

					Map<Long, List<IPTItem>> wMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser,
							new ArrayList<>(Arrays.asList((long) wSFCTaskIPT.ModuleVersionID)), wErrorCode).Result;
					if (wMap == null || wMap.size() <= 0
							|| !wMap.containsKey(Long.valueOf(wSFCTaskIPT.ModuleVersionID))) {
						continue;
					}
					List<IPTItem> wItemList = wMap.get(Long.valueOf(wSFCTaskIPT.ModuleVersionID));
					if (wItemList == null || wItemList.size() <= 0) {
						continue;
					}

					wValueList = (List<IPTValue>) (IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSFCTaskIPT.ID,
							-1, -1, wErrorCode)).Result;
					if (wValueList == null) {
						wValueList = new ArrayList<>();
					}

					wItemList = (List<IPTItem>) wItemList.stream()
							.filter(p -> (StringUtils.isNotEmpty(p.PartsCoding) && p.PartsCoding.equals(wCode)))
							.collect(Collectors.toList());
					if (wItemList == null || wItemList.size() <= 0) {
						continue;
					}
					for (IPTItem wIPTItem : wItemList) {
						wMSSPartItem = new MSSPartItem();
						wMSSPartItem.ProductNo = wSFCTaskIPT.ProductNo;
						wMSSPartItem.LineID = wSFCTaskIPT.LineID;
						wMSSPartItem.LineName = wSFCTaskIPT.LineName;
						wMSSPartItem.OrderID = wSFCTaskIPT.OrderID;
						wMSSPartItem.OrderNo = wSFCTaskIPT.OrderNo;
						wMSSPartItem.CustomerID = wOMSOrder.CustomerID;
						wMSSPartItem.CustomerName = wOMSOrder.Customer;
						wMSSPartItem.ProductID = wOMSOrder.ProductID;
						wMSSPartItem.Code = wIPTItem.PartsCoding;
						wMSSPartItem.PartNo = wOMSOrder.PartNo;

						Optional<IPTValue> wOption = wValueList.stream().filter(p -> (p.IPTItemID == wIPTItem.ID))
								.findFirst();
						if (wOption.isPresent() && ((IPTValue) wOption.get()).Number != null
								&& ((IPTValue) wOption.get()).Number.equals(wSuplierPartNo)) {
							wMSSPartItem.SupplierName = ((IPTValue) wOption.get()).Manufactor;
							wMSSPartItem.SupplierProductNo = ((IPTValue) wOption.get()).Modal;
							wMSSPartItem.SupplierPartNo = ((IPTValue) wOption.get()).Number;
						}

						((List<MSSPartItem>) wResult.Result).add(wMSSPartItem);
					}
				}
			}

			if (wResult.Result != null && (wResult.Result).size() > 0) {
				List<FMCItem> wProductItemList = new ArrayList<>();
				List<FMCItem> wLineItemList = new ArrayList<>();
				List<FMCItem> wCustomerItemList = new ArrayList<>();
				List<Integer> wList = wResult.Result.stream().map(p -> p.ProductID).distinct()
						.collect(Collectors.toList());
				for (Integer wID : wList) {
					FMCItem wFMCItem = new FMCItem();
					wFMCItem.ID = wID.intValue();
					wFMCItem.Name = QMSConstants.GetFPCProductNo(wID.intValue());
					wProductItemList.add(wFMCItem);
				}
				wList = wResult.Result.stream().map(p -> Integer.valueOf(p.LineID)).distinct()
						.collect(Collectors.toList());
				for (Integer wID : wList) {
					FMCItem wFMCItem = new FMCItem();
					wFMCItem.ID = wID.intValue();
					wFMCItem.Name = QMSConstants.GetFMCLineName(wID.intValue());
					wLineItemList.add(wFMCItem);
				}
				wList = wResult.Result.stream().map(p -> Integer.valueOf(p.CustomerID)).distinct()
						.collect(Collectors.toList());
				for (Integer wID : wList) {
					FMCItem wFMCItem = new FMCItem();
					wFMCItem.ID = wID.intValue();
					wFMCItem.Name = QMSConstants.GetCRMCustomerName(wID.intValue());
					wCustomerItemList.add(wFMCItem);
				}
				wResult.CustomResult.put("ProductList", wProductItemList);
				wResult.CustomResult.put("LineList", wLineItemList);
				wResult.CustomResult.put("CustomerList", wCustomerItemList);
			} else {
				wResult.CustomResult.put("ProductList", new ArrayList<>());
				wResult.CustomResult.put("LineList", new ArrayList<>());
				wResult.CustomResult.put("CustomerList", new ArrayList<>());
			}
		} catch (Exception e) {
			wResult.FaultCode = String.valueOf(wResult.FaultCode) + e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_QueryPartConfigDetails(BMSEmployee wLoginUser, String wPartConfigNo) {
		ServiceResult<Integer> wResult = new ServiceResult<>(Integer.valueOf(0));
		OutResult<Integer> wErrorCode = new OutResult<>(Integer.valueOf(0));
		try {
			List<FMCItem> wProductList = new ArrayList<>();
			List<FMCItem> wLineList = new ArrayList<>();
			List<FMCItem> wCustomerList = new ArrayList<>();
			List<FMCItem> wMaterialList = new ArrayList<>();
			List<FMCItem> wUnitList = new ArrayList<>();

			List<MSSPartConfig> wConfigList = WMSServiceImpl.getInstance()
					.MSS_ConfigAll(wLoginUser, wPartConfigNo, "", -1, "", -1, -1).List(MSSPartConfig.class);
			if (wConfigList != null && wConfigList.size() > 0) {

				List<MSSPartConfig> wProductConfig = new ArrayList<>((wConfigList.stream()
						.collect(Collectors.toMap(MSSPartConfig::getProductNo, account -> account, (k1, k2) -> k2)))
								.values());
				if (wProductConfig != null && wProductConfig.size() > 0) {
					for (MSSPartConfig wMSSPartConfig : wProductConfig) {
						FMCItem wFMCItem = new FMCItem();
						wFMCItem.ID = (QMSConstants.GetFPCProduct(wMSSPartConfig.ProductNo)).ID;
						wFMCItem.Name = wMSSPartConfig.ProductNo;
						wProductList.add(wFMCItem);
					}
				}

				List<MSSPartConfig> wLineConfig = new ArrayList<>((wConfigList.stream()
						.collect(Collectors.toMap(MSSPartConfig::getLineID, account -> account, (k1, k2) -> k2)))
								.values());
				if (wLineConfig != null && wLineConfig.size() > 0) {
					for (MSSPartConfig wMSSPartConfig : wLineConfig) {
						FMCItem wFMCItem = new FMCItem();
						wFMCItem.ID = wMSSPartConfig.LineID;
						wFMCItem.Name = wMSSPartConfig.LineName;
						wLineList.add(wFMCItem);
					}
				}

				List<MSSPartConfig> wCustomerConfig = new ArrayList<>((wConfigList.stream()
						.collect(Collectors.toMap(MSSPartConfig::getCustomerID, account -> account, (k1, k2) -> k2)))
								.values());
				if (wCustomerConfig != null && wCustomerConfig.size() > 0) {
					for (MSSPartConfig wMSSPartConfig : wCustomerConfig) {
						FMCItem wFMCItem = new FMCItem();
						wFMCItem.ID = wMSSPartConfig.CustomerID;
						wFMCItem.Name = wMSSPartConfig.CustomerName;
						wCustomerList.add(wFMCItem);
					}
				}

				List<MSSPartConfig> wMaterialConfig = new ArrayList<>((wConfigList.stream()
						.collect(Collectors.toMap(MSSPartConfig::getMaterialID, account -> account, (k1, k2) -> k2)))
								.values());
				if (wMaterialConfig != null && wMaterialConfig.size() > 0) {
					for (MSSPartConfig wMSSPartConfig : wMaterialConfig) {
						FMCItem wFMCItem = new FMCItem();
						wFMCItem.ID = wMSSPartConfig.MaterialID;
						wFMCItem.Name = wMSSPartConfig.MaterialName;
						wMaterialList.add(wFMCItem);
					}
				}

				List<MSSPartConfig> wUnitConfig = new ArrayList<>((wConfigList.stream()
						.collect(Collectors.toMap(MSSPartConfig::getUnitID, account -> account, (k1, k2) -> k2)))
								.values());
				if (wUnitConfig != null && wUnitConfig.size() > 0) {
					for (MSSPartConfig wMSSPartConfig : wUnitConfig) {
						FMCItem wFMCItem = new FMCItem();
						wFMCItem.ID = wMSSPartConfig.UnitID;
						wFMCItem.Name = wMSSPartConfig.UnitText;
						wUnitList.add(wFMCItem);
					}
				}
			}

			wResult.CustomResult.put("ProductList", wProductList);
			wResult.CustomResult.put("LineList", wLineList);
			wResult.CustomResult.put("CustomerList", wCustomerList);
			wResult.CustomResult.put("MaterialList", wMaterialList);
			wResult.CustomResult.put("UnitList", wUnitList);
			wResult.setFaultCode(MESException.getEnumType(((Integer) wErrorCode.Result).intValue()).getLable());
		} catch (Exception e) {
			wResult.FaultCode = String.valueOf(wResult.FaultCode) + e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<String> SFC_CheckIsTaskClocked(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			// ?????????????????????ID?????????ID??????????????????
			List<SFCTaskStep> wList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
			if (wList == null || wList.size() <= 0) {
				wResult.Result = StringUtils.Format("????????????{0}?????????{1}??????????????????????????????!", wLoginUser.Name,
						wSFCTaskIPT.PartPointName);
				return wResult;
			}
			wList = new ArrayList<SFCTaskStep>(wList.stream()
					.collect(Collectors.toMap(SFCTaskStep::getOperatorID, account -> account, (k1, k2) -> k2))
					.values());
			// ???????????????????????????????????????????????????????????????
			if (wList.stream().anyMatch(p -> p.IsStartWork != 1 && p.OperatorID == wLoginUser.ID)) {
				wResult.Result = StringUtils.Format("????????????{0}?????????????????????{1}???????????????!", wLoginUser.Name,
						wSFCTaskIPT.PartPointName);
				return wResult;
			}

			if (wSFCTaskIPT.TaskType == SFCTaskType.SelfCheck.getValue() && wList.size() > 1) {
				OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
				// ??????????????????????????????????????????
				List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1, wSFCTaskIPT.ID, -1,
						-1, -1, wErrorCode);
				if (wValueList == null || wValueList.size() <= 0) {
					return wResult;
				}

				List<Integer> wPersonIDList = wValueList.stream().map(p -> p.SubmitID).distinct()
						.collect(Collectors.toList());
				List<Integer> wOtherIDList = wList.stream().map(p -> p.OperatorID).collect(Collectors.toList()).stream()
						.filter(p -> p != wLoginUser.ID).collect(Collectors.toList());
				if (wOtherIDList.stream()
						.allMatch(p -> wPersonIDList.stream().anyMatch(q -> p.intValue() == q.intValue()))) {
					String wNames = this.GetNames(wPersonIDList);
					wResult.Result = StringUtils.Format("????????????{0}????????????????????????????????????????????????????????????????????????!", wNames);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????
	 */
	private String GetNames(List<Integer> wPersonIDList) {
		String wResult = "";
		try {
			if (wPersonIDList == null || wPersonIDList.size() <= 0) {
				return wResult;
			}

			List<String> wNames = new ArrayList<String>();
			for (Integer wID : wPersonIDList) {
				String wName = QMSConstants.GetBMSEmployeeName(wID);
				if (StringUtils.isEmpty(wName)) {
					continue;
				}

				wNames.add(wName);
			}

			if (wNames.size() > 0) {
				wResult = StringUtils.Join(",", wNames);
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FPCPart>> SFC_QueryCanCheckPartList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<FPCPart>> wResult = new ServiceResult<List<FPCPart>>();
		wResult.Result = new ArrayList<FPCPart>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ???????????????????????????????????????
			List<SFCTemporaryExamination> wList = SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1,
					"", wOrderID, -1, "", -1, -1, -1, null, null, null, wErrorCode);
			// ???????????????ID?????????????????????????????????
			List<APSTaskPart> wTaskList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartAll(wLoginUser, wOrderID, APSShiftPeriod.Week.getValue()).List(APSTaskPart.class);
			wTaskList = wTaskList.stream().filter(p -> p.Status == APSTaskStatus.Done.getValue() && p.Active == 1)
					.collect(Collectors.toList());
			if (wTaskList == null || wTaskList.size() <= 0) {
				return wResult;
			}
			// ?????????????????????????????????????????????????????????????????????????????????
			if (wList != null && wList.size() > 0) {
				wTaskList.removeIf(p -> wList.stream().anyMatch(q -> q.PartIDList != null && q.PartIDList.size() > 0
						&& q.PartIDList.stream().anyMatch(x -> x.intValue() == p.PartID)));
			}
			// ?????????????????????
			for (APSTaskPart wAPSTaskPart : wTaskList) {
				FPCPart wPart = QMSConstants.GetFPCPart(wAPSTaskPart.PartID);
				if (wPart == null || wPart.ID <= 0) {
					continue;
				}

				if (wPart.PartType == FPCPartTypes.Product.getValue()) {
					wResult.Result.add(wPart);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????????????????????????????
	 */
	@Override
	public ServiceResult<Integer> SFC_HandleSelfCheckItem(BMSEmployee wLoginUser, int wTaskIPTID, int wIPTItemID,
			int wAssessResult) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			SFCTaskIPT wTaskIPT = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wTaskIPTID, wErrorCode);
			if (wTaskIPT == null || wTaskIPT.ID <= 0) {
				return wResult;
			}

			// ?????????????????????
			List<IPTValue> wList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, wIPTItemID, wTaskIPTID, -1, -1,
					-1, wErrorCode);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			// ???????????????????????????????????????????????????????????????
			if (wAssessResult == 1) {
				List<SFCTaskIPT> wSpecialList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
						wTaskIPT.TaskStepID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, null,
						SFCTaskStepType.Step.getValue(), null, null, wErrorCode);
				if (wSpecialList == null || wSpecialList.size() <= 0) {
					// ??????????????????
					wTaskIPT.TaskType = SFCTaskType.SpecialCheck.getValue();
					int wNewID = this.SFC_TriggerTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
					wTaskIPT.ID = wNewID;
					wSpecialList.add(wTaskIPT);
				}

				List<IPTValue> wIPTValueList = new ArrayList<IPTValue>();
				IPTValue wIPTValue = new IPTValue();
				wIPTValue.ID = 0;
				wIPTValue.IPTItemID = wIPTItemID;
				wIPTValue.Remark = "????????????????????????????????????????????????!";
				wIPTValue.Result = 2;
				wIPTValue.SubmitID = -1;
				wIPTValue.SubmitTime = Calendar.getInstance();
				wIPTValue.TaskID = wSpecialList.get(0).ID;
				wIPTValue.StandardID = wTaskIPT.ModuleVersionID;
				wIPTValue.Status = 2;
				wIPTValue.IPTMode = SFCTaskType.SpecialCheck.getValue();
				wIPTValueList.add(wIPTValue);
				IPTServiceImpl.getInstance().IPT_SaveIPTValue(wLoginUser, wIPTValueList, wSpecialList.get(0).ID, -1);
			}
			// ??????????????????????????????????????????????????????
			else if (wAssessResult == 0) {
				wList.forEach(p -> p.Status = 1);
				for (IPTValue wIPTValue : wList) {
					IPTValueDAO.getInstance().Update(wLoginUser, wIPTValue, wErrorCode);
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
	public ServiceResult<SFCTemporaryExamination> SFC_CreateTemporaryExamination(BMSEmployee wLoginUser,
			Integer wOrderID) {
		ServiceResult<SFCTemporaryExamination> wResult = new ServiceResult<SFCTemporaryExamination>();
		wResult.Result = new SFCTemporaryExamination();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ????????????????????????????????????????????????????????????????????????????????????
			List<SFCTemporaryExamination> wList = SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1,
					"", wOrderID, -1, "", -1, -1, -1, new ArrayList<Integer>(Arrays.asList(1)), null, null, wErrorCode);
			if (wList.size() > 0) {
				wResult.FaultCode += "???????????????????????????????????????????????????????????????????????????!";
				return wResult;
			}

			wResult.Result.ID = 0;
			wResult.Result.Code = SFCTemporaryExaminationDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wResult.Result.OrderID = wOrderID;
			wResult.Result.CreateID = wLoginUser.ID;
			wResult.Result.CreateTime = Calendar.getInstance();
			wResult.Result.Creator = wLoginUser.Name;
			wResult.Result.Status = 0;

			// ???????????????????????????
			List<List<LFSWorkAreaChecker>> wCheckerList = QMSConstants.GetLFSWorkAreaCheckerList().values().stream()
					.collect(Collectors.toList());
			List<BMSEmployee> wUserList = new ArrayList<BMSEmployee>();
			for (List<LFSWorkAreaChecker> wLFSWorkAreaCheckerList : wCheckerList) {
				for (LFSWorkAreaChecker wLFSWorkAreaChecker : wLFSWorkAreaCheckerList) {
					if (wLFSWorkAreaChecker.CheckerIDList == null || wLFSWorkAreaChecker.CheckerIDList.size() <= 0)
						continue;

					for (int wUserID : wLFSWorkAreaChecker.CheckerIDList) {
						if (wUserList.stream().anyMatch(p -> p.ID == wUserID))
							continue;

						BMSEmployee wEmployee = QMSConstants.GetBMSEmployee(wUserID);
						if (wEmployee != null && wEmployee.ID > 0) {
							wUserList.add(wEmployee);
						}
					}
				}
			}

			wResult.CustomResult.put("CheckerList", wUserList);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_SubmitTemporaryExamination(BMSEmployee wLoginUser,
			SFCTemporaryExamination wData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			if (wData.ID <= 0) {
				List<Integer> wPartIDList = this.GetPartIDList(wLoginUser, wData.OrderID, wData.PartID);
				wData.PartIDList = wPartIDList;

				// ???????????????????????????
				BFCMessage wMessage = null;
				int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
						FMCShiftLevel.Day);
				SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for (int wPersonID : wData.CheckIDList) {
					// ???????????????????????????
					wMessage = new BFCMessage();
					wMessage.Active = 0;
					wMessage.CompanyID = 0;
					wMessage.CreateTime = Calendar.getInstance();
					wMessage.EditTime = Calendar.getInstance();
					wMessage.ID = 0;
					wMessage.MessageID = wData.ID;
					wMessage.Title = wData.Code;
					wMessage.MessageText = StringUtils.Format("?????????{0} ????????????{1} ???????????????{2} ??????????????????",
							BPMEventModule.TempCheck.getLable(), wLoginUser.Name,
							wSDF.format(wData.CreateTime.getTime()));
					wMessage.ModuleID = BPMEventModule.TempCheck.getValue();
					wMessage.ResponsorID = wPersonID;
					wMessage.ShiftID = wShiftID;
					wMessage.StationID = wData.PartID;
					wMessage.Type = BFCMessageType.Task.getValue();
					wBFCMessageList.add(wMessage);
				}
			}

			wResult.Result = SFCTemporaryExaminationDAO.getInstance().Update(wLoginUser, wData, wErrorCode);

			if (wData.ID <= 0) {
				wBFCMessageList.forEach(p -> p.MessageID = wResult.Result);
				CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????????????????????(??????????????????)(????????????????????????)
	 */
	private List<Integer> GetPartIDList(BMSEmployee wLoginUser, int wOrderID, int wPartID) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ???????????????
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			// ???????????????????????????????????????????????????
			List<FPCRoutePart> wRoutePartList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wOrder.RouteID).collect(Collectors.toList());
			// ?????????????????????????????????
			List<Integer> wPreIDList = QMSUtils.getInstance().FPC_QueryPreStationIDList(wRoutePartList, wPartID);
			// ?????????????????????????????????????????????
			List<SFCTemporaryExamination> wCheckTaskList = SFCTemporaryExaminationDAO.getInstance().SelectList(
					wLoginUser, -1, "", wOrderID, -1, "", -1, -1, -1, new ArrayList<Integer>(Arrays.asList(2)), null,
					null, wErrorCode);
			// ?????????????????????????????????????????????????????????????????????????????????
			if (wCheckTaskList != null && wCheckTaskList.size() > 0) {
				for (SFCTemporaryExamination wSFCTemporaryExamination : wCheckTaskList) {
					wPreIDList.removeIf(p -> wSFCTemporaryExamination.PartIDList.stream()
							.anyMatch(q -> q.intValue() == p.intValue()));
				}
			}
			// ?????????????????????
			wPreIDList.add(wPartID);
			// ???????????????
			wResult = wPreIDList;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTemporaryExamination>> SFC_QueryEmployeeAllTemporaryExaminationList(
			BMSEmployee wLoginUser, int wTagTypes, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SFCTemporaryExamination>> wResult = new ServiceResult<List<SFCTemporaryExamination>>();
		wResult.Result = new ArrayList<SFCTemporaryExamination>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// ??????
				// ????????????????????????????????????
				wResult.Result.addAll(SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1,
						"", -1, wLoginUser.ID, -1, new ArrayList<Integer>(Arrays.asList(1)), null, null, wErrorCode));
				// ????????????????????????????????????
				wResult.Result.addAll(SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1,
						"", -1, wLoginUser.ID, -1, new ArrayList<Integer>(Arrays.asList(2)), wStartTime, wEndTime,
						wErrorCode));
				break;
			case Dispatcher:// ??????
				// ????????????????????????????????????
				wResult.Result.addAll(SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1,
						"", -1, -1, wLoginUser.ID, new ArrayList<Integer>(Arrays.asList(1)), null, null, wErrorCode));
				break;
			case Approver:// ??????
				// ????????????????????????????????????
				wResult.Result.addAll(SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1,
						"", -1, -1, wLoginUser.ID, new ArrayList<Integer>(Arrays.asList(2)), wStartTime, wEndTime,
						wErrorCode));
				break;
			default:
				break;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<Integer> SFC_SubmitValueList(BMSEmployee wLoginUser, List<IPTValue> wDataList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			for (IPTValue wIPTValue : wDataList) {
				IPTValueDAO.getInstance().Update(wLoginUser, wIPTValue, wErrorCode);
			}

			// ?????????????????????
			ChangeStatus(wLoginUser, wDataList.get(0).TaskID);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private void ChangeStatus(BMSEmployee wLoginUser, int wSFCTemporaryExaminationID) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			SFCTemporaryExamination wSFCTemTask = SFCTemporaryExaminationDAO.getInstance().SelectByID(wLoginUser,
					wSFCTemporaryExaminationID, wErrorCode);
			if (wSFCTemTask == null || wSFCTemTask.ID <= 0 || wSFCTemTask.PartIDList == null
					|| wSFCTemTask.PartIDList.size() <= 0) {
				return;
			}

			List<SFCTemporaryExaminationPartItem> wPartList = new ArrayList<SFCTemporaryExaminationPartItem>();

			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTemTask.OrderID)
					.Info(OMSOrder.class);

			for (int wPartID : wSFCTemTask.PartIDList) {
				SFCTemporaryExaminationPartItem wSFCTemporaryExaminationPartItem = new SFCTemporaryExaminationPartItem();
				wSFCTemporaryExaminationPartItem.CustomerID = wOrder.CustomerID;
				wSFCTemporaryExaminationPartItem.CustomerName = wOrder.Customer;
				wSFCTemporaryExaminationPartItem.LineID = wOrder.LineID;
				wSFCTemporaryExaminationPartItem.LineName = wOrder.LineName;
				wSFCTemporaryExaminationPartItem.OrderID = wSFCTemTask.OrderID;
				wSFCTemporaryExaminationPartItem.OrderNo = wOrder.OrderNo;
				wSFCTemporaryExaminationPartItem.PartID = wPartID;
				wSFCTemporaryExaminationPartItem.PartName = QMSConstants.GetFPCPartName(wPartID);
				wSFCTemporaryExaminationPartItem.PartNo = wOrder.PartNo;
				wSFCTemporaryExaminationPartItem.ProductID = wOrder.ProductID;
				wSFCTemporaryExaminationPartItem.ProductNo = wOrder.ProductNo;

				// ??????????????????????????????????????????
				List<SFCTaskIPT> wTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
						wSFCTemTask.OrderID, wPartID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, "", null, null,
						wErrorCode);
				if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
					continue;
				}
				// ?????????????????????????????????????????????
				List<Long> wStandardIDList = wTaskIPTList.stream().map(p -> (long) p.ModuleVersionID)
						.collect(Collectors.toList());
				Map<Long, List<IPTItem>> wItemMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser, wStandardIDList,
						wErrorCode).Result;
				// ???????????????????????????ID????????????????????????????????????
				List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1,
						wSFCTemporaryExaminationID, SFCTaskType.TemporaryCheck.getValue(), -1, -1, wErrorCode);
				// ???????????????????????????????????????
				for (SFCTaskIPT wSFCTaskIPT : wTaskIPTList) {
					if (!wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
						continue;
					}

					List<IPTItem> wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);

					SFCTemporaryExaminationStepItem wSFCTemporaryExaminationStepItem = new SFCTemporaryExaminationStepItem();
					wSFCTemporaryExaminationStepItem.CustomerID = wSFCTemporaryExaminationPartItem.CustomerID;
					wSFCTemporaryExaminationStepItem.CustomerName = wSFCTemporaryExaminationPartItem.CustomerName;
					wSFCTemporaryExaminationStepItem.LineID = wSFCTemporaryExaminationPartItem.LineID;
					wSFCTemporaryExaminationStepItem.LineName = wSFCTemporaryExaminationPartItem.LineName;
					wSFCTemporaryExaminationStepItem.OrderID = wSFCTemporaryExaminationPartItem.OrderID;
					wSFCTemporaryExaminationStepItem.OrderNo = wSFCTemporaryExaminationPartItem.OrderNo;
					wSFCTemporaryExaminationStepItem.PartID = wSFCTemporaryExaminationPartItem.PartID;
					wSFCTemporaryExaminationStepItem.PartName = wSFCTemporaryExaminationPartItem.PartName;
					wSFCTemporaryExaminationStepItem.PartNo = wSFCTemporaryExaminationPartItem.PartNo;
					wSFCTemporaryExaminationStepItem.ProductID = wSFCTemporaryExaminationPartItem.ProductID;
					wSFCTemporaryExaminationStepItem.ProductNo = wSFCTemporaryExaminationPartItem.ProductNo;
					wSFCTemporaryExaminationStepItem.StepID = wSFCTaskIPT.PartPointID;
					wSFCTemporaryExaminationStepItem.StepName = QMSConstants.GetFPCStepName(wSFCTaskIPT.PartPointID);
					wSFCTemporaryExaminationStepItem.Status = 1;
					wSFCTemporaryExaminationStepItem.ValueList = wValueList.stream()
							.filter(p -> wItemList.stream().anyMatch(q -> q.ID == p.IPTItemID))
							.collect(Collectors.toList());

					wSFCTemporaryExaminationStepItem.ToDoList = this.GetToDoList(wLoginUser, wItemList,
							wSFCTemporaryExaminationStepItem.ValueList);
					wSFCTemporaryExaminationStepItem.DoneList = this.GetDoneList(wLoginUser, wItemList,
							wSFCTemporaryExaminationStepItem.ValueList);
					wSFCTemporaryExaminationStepItem.Status = this.GetStatus(wLoginUser, wItemList,
							wSFCTemporaryExaminationStepItem.ValueList);
					wSFCTemporaryExaminationPartItem.StepList.add(wSFCTemporaryExaminationStepItem);
				}

				// ????????????
				if (wSFCTemporaryExaminationPartItem.StepList.stream()
						.allMatch(p -> p.Status == SFCTaskStatus.Done.getValue())) {
					wSFCTemporaryExaminationPartItem.Status = SFCTaskStatus.Done.getValue();
				} else {
					wSFCTemporaryExaminationPartItem.Status = SFCTaskStatus.Active.getValue();
				}

				wPartList.add(wSFCTemporaryExaminationPartItem);
			}

			if (wPartList.size() > 0 && wPartList.stream().allMatch(p -> p.Status == SFCTaskStatus.Done.getValue())) {
				wSFCTemTask.Status = SFCTaskStatus.Done.getValue();
				SFCTemporaryExaminationDAO.getInstance().Update(wLoginUser, wSFCTemTask, wErrorCode);

				// ????????????
				List<BFCMessage> wList1 = CoreServiceImpl.getInstance()
						.BFC_GetMessageList(wLoginUser, -1, BPMEventModule.TempCheck.getValue(),
								new ArrayList<Integer>(Arrays.asList(wSFCTemTask.ID)), BFCMessageType.Task.getValue(),
								BFCMessageStatus.Default.getValue())
						.List(BFCMessage.class);
				List<BFCMessage> wList2 = CoreServiceImpl.getInstance()
						.BFC_GetMessageList(wLoginUser, -1, BPMEventModule.TempCheck.getValue(),
								new ArrayList<Integer>(Arrays.asList(wSFCTemTask.ID)), BFCMessageType.Task.getValue(),
								BFCMessageStatus.Read.getValue())
						.List(BFCMessage.class);
				List<BFCMessage> wList3 = CoreServiceImpl.getInstance()
						.BFC_GetMessageList(wLoginUser, -1, BPMEventModule.TempCheck.getValue(),
								new ArrayList<Integer>(Arrays.asList(wSFCTemTask.ID)), BFCMessageType.Task.getValue(),
								BFCMessageStatus.Sent.getValue())
						.List(BFCMessage.class);
				List<BFCMessage> wList = new ArrayList<BFCMessage>();
				if (wList1 != null && wList1.size() > 0) {
					wList.addAll(wList1);
				}
				if (wList2 != null && wList2.size() > 0) {
					wList.addAll(wList2);
				}
				if (wList3 != null && wList3.size() > 0) {
					wList.addAll(wList3);
				}
				if (wList.size() > 0) {
					wList.forEach(p -> {
						if (p.ResponsorID == wLoginUser.ID) {
							p.Active = BFCMessageStatus.Finished.getValue();
						} else {
							p.Active = BFCMessageStatus.Close.getValue();
						}
					});
					CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wList);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<SFCTemporaryExaminationPartItem>> SFC_QueryTemporaryExaminationPartItemList(
			BMSEmployee wLoginUser, int wSFCTemporaryExaminationID) {
		ServiceResult<List<SFCTemporaryExaminationPartItem>> wResult = new ServiceResult<List<SFCTemporaryExaminationPartItem>>();
		wResult.Result = new ArrayList<SFCTemporaryExaminationPartItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SFCTemporaryExamination wSFCTemTask = SFCTemporaryExaminationDAO.getInstance().SelectByID(wLoginUser,
					wSFCTemporaryExaminationID, wErrorCode);
			if (wSFCTemTask == null || wSFCTemTask.ID <= 0) {
				return wResult;
			}
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTemTask.OrderID)
					.Info(OMSOrder.class);
			for (int wPartID : wSFCTemTask.PartIDList) {
				SFCTemporaryExaminationPartItem wSFCTemporaryExaminationPartItem = new SFCTemporaryExaminationPartItem();
				wSFCTemporaryExaminationPartItem.CustomerID = wOrder.CustomerID;
				wSFCTemporaryExaminationPartItem.CustomerName = wOrder.Customer;
				wSFCTemporaryExaminationPartItem.LineID = wOrder.LineID;
				wSFCTemporaryExaminationPartItem.LineName = wOrder.LineName;
				wSFCTemporaryExaminationPartItem.OrderID = wSFCTemTask.OrderID;
				wSFCTemporaryExaminationPartItem.OrderNo = wOrder.OrderNo;
				wSFCTemporaryExaminationPartItem.PartID = wPartID;
				wSFCTemporaryExaminationPartItem.PartName = QMSConstants.GetFPCPartName(wPartID);
				wSFCTemporaryExaminationPartItem.PartNo = wOrder.PartNo;
				wSFCTemporaryExaminationPartItem.ProductID = wOrder.ProductID;
				wSFCTemporaryExaminationPartItem.ProductNo = wOrder.ProductNo;

				// ??????????????????????????????????????????
				List<SFCTaskIPT> wTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
						wSFCTemTask.OrderID, wPartID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, "", null, null,
						wErrorCode);
				if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
					continue;
				}
				// ?????????????????????????????????????????????
				List<Long> wStandardIDList = wTaskIPTList.stream().map(p -> (long) p.ModuleVersionID)
						.collect(Collectors.toList());
				Map<Long, List<IPTItem>> wItemMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser, wStandardIDList,
						wErrorCode).Result;
				// ???????????????????????????ID????????????????????????????????????
				List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1,
						wSFCTemporaryExaminationID, SFCTaskType.TemporaryCheck.getValue(), -1, -1, wErrorCode);
				// ???????????????????????????????????????
				for (SFCTaskIPT wSFCTaskIPT : wTaskIPTList) {
					if (!wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
						continue;
					}

					List<IPTItem> wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);

					SFCTemporaryExaminationStepItem wSFCTemporaryExaminationStepItem = new SFCTemporaryExaminationStepItem();
					wSFCTemporaryExaminationStepItem.CustomerID = wSFCTemporaryExaminationPartItem.CustomerID;
					wSFCTemporaryExaminationStepItem.CustomerName = wSFCTemporaryExaminationPartItem.CustomerName;
					wSFCTemporaryExaminationStepItem.LineID = wSFCTemporaryExaminationPartItem.LineID;
					wSFCTemporaryExaminationStepItem.LineName = wSFCTemporaryExaminationPartItem.LineName;
					wSFCTemporaryExaminationStepItem.OrderID = wSFCTemporaryExaminationPartItem.OrderID;
					wSFCTemporaryExaminationStepItem.OrderNo = wSFCTemporaryExaminationPartItem.OrderNo;
					wSFCTemporaryExaminationStepItem.PartID = wSFCTemporaryExaminationPartItem.PartID;
					wSFCTemporaryExaminationStepItem.PartName = wSFCTemporaryExaminationPartItem.PartName;
					wSFCTemporaryExaminationStepItem.PartNo = wSFCTemporaryExaminationPartItem.PartNo;
					wSFCTemporaryExaminationStepItem.ProductID = wSFCTemporaryExaminationPartItem.ProductID;
					wSFCTemporaryExaminationStepItem.ProductNo = wSFCTemporaryExaminationPartItem.ProductNo;
					wSFCTemporaryExaminationStepItem.StepID = wSFCTaskIPT.PartPointID;
					wSFCTemporaryExaminationStepItem.StepName = QMSConstants.GetFPCStepName(wSFCTaskIPT.PartPointID);
					wSFCTemporaryExaminationStepItem.Status = 1;
					wSFCTemporaryExaminationStepItem.ValueList = wValueList.stream()
							.filter(p -> wItemList.stream().anyMatch(q -> q.ID == p.IPTItemID))
							.collect(Collectors.toList());

					wSFCTemporaryExaminationStepItem.ToDoList = this.GetToDoList(wLoginUser, wItemList,
							wSFCTemporaryExaminationStepItem.ValueList);
					wSFCTemporaryExaminationStepItem.DoneList = this.GetDoneList(wLoginUser, wItemList,
							wSFCTemporaryExaminationStepItem.ValueList);
					wSFCTemporaryExaminationStepItem.Status = this.GetStatus(wLoginUser, wItemList,
							wSFCTemporaryExaminationStepItem.ValueList);
					wSFCTemporaryExaminationPartItem.StepList.add(wSFCTemporaryExaminationStepItem);
				}

				// ????????????
				if (wSFCTemporaryExaminationPartItem.StepList.stream()
						.allMatch(p -> p.Status == SFCTaskStatus.Done.getValue())) {
					wSFCTemporaryExaminationPartItem.Status = SFCTaskStatus.Done.getValue();
				} else {
					wSFCTemporaryExaminationPartItem.Status = SFCTaskStatus.Active.getValue();
				}
				wResult.Result.add(wSFCTemporaryExaminationPartItem);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private int GetStatus(BMSEmployee wLoginUser, List<IPTItem> wItemList, List<IPTValue> wValueList) {
		int wResult = 0;
		try {
			List<IPTItem> wItems = wItemList.stream().filter(p -> p.ItemType != IPTItemType.Group.getValue())
					.collect(Collectors.toList());
			if (wItems.stream().allMatch(p -> wValueList.stream().anyMatch(q -> q.IPTItemID == p.ID))) {
				wResult = SFCTaskStatus.Done.getValue();
			} else {
				wResult = SFCTaskStatus.Active.getValue();
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????
	 */
	private List<IPTItem> GetDoneList(BMSEmployee wLoginUser, List<IPTItem> wItemList, List<IPTValue> wValueList) {
		List<IPTItem> wResult = new ArrayList<IPTItem>();
		try {
			List<IPTItem> wGroupList = wItemList.stream().filter(p -> p.ItemType == IPTItemType.Group.getValue())
					.collect(Collectors.toList());

			List<IPTItem> wCheckItemList = wItemList.stream().filter(p -> p.ItemType != IPTItemType.Group.getValue())
					.collect(Collectors.toList());
			for (IPTItem wIPTItem : wCheckItemList) {
				if (wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
					wResult.add(wIPTItem);
				}
			}

			if (wResult.size() > 0) {
				List<IPTItem> wFatherList = this.AddGroup(wGroupList, wResult);
				if (wFatherList != null && wFatherList.size() > 0) {
					wResult.addAll(wFatherList);
				}
				// ??????
				wResult = new ArrayList<IPTItem>(wResult.stream()
						.collect(Collectors.toMap(IPTItem::getID, account -> account, (k1, k2) -> k2)).values());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????
	 */
	private List<IPTItem> GetToDoList(BMSEmployee wLoginUser, List<IPTItem> wItemList, List<IPTValue> wValueList) {
		List<IPTItem> wResult = new ArrayList<IPTItem>();
		try {
			List<IPTItem> wGroupList = wItemList.stream().filter(p -> p.ItemType == IPTItemType.Group.getValue())
					.collect(Collectors.toList());

			List<IPTItem> wCheckItemList = wItemList.stream().filter(p -> p.ItemType != IPTItemType.Group.getValue())
					.collect(Collectors.toList());
			for (IPTItem wIPTItem : wCheckItemList) {
				if (wValueList.stream().allMatch(p -> p.IPTItemID != wIPTItem.ID)) {
					wResult.add(wIPTItem);
				}
			}

			if (wResult.size() > 0) {
				List<IPTItem> wFatherList = this.AddGroup(wGroupList, wResult);
				if (wFatherList != null && wFatherList.size() > 0) {
					wResult.addAll(wFatherList);
				}
				// ??????
				wResult = new ArrayList<IPTItem>(wResult.stream()
						.collect(Collectors.toMap(IPTItem::getID, account -> account, (k1, k2) -> k2)).values());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPTPartNo>> SFC_GetTaskIPTPartNoList(BMSEmployee wLoginUser,
			List<SFCTaskIPT> wList, List<SFCTaskIPT> wAllList, boolean wIsToDo) {
		ServiceResult<List<SFCTaskIPTPartNo>> wResult = new ServiceResult<List<SFCTaskIPTPartNo>>();
		wResult.Result = new ArrayList<SFCTaskIPTPartNo>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wAllList == null || wAllList.size() <= 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			List<String> wPartNoList = wList.stream().map(p -> p.PartNo).distinct().collect(Collectors.toList());
			for (String wPartNo : wPartNoList) {
				SFCTaskIPT wIPT = wAllList.stream().filter(p -> p.PartNo.equals(wPartNo)).findFirst().get();

				SFCTaskIPTPartNo wSFCTaskIPTPartNo = new SFCTaskIPTPartNo();
				wSFCTaskIPTPartNo.Customer = wIPT.CustomerName;
				wSFCTaskIPTPartNo.LineName = wIPT.LineName;
				wSFCTaskIPTPartNo.PartNo = wPartNo;
				wSFCTaskIPTPartNo.FQTYTotal = (int) wAllList.stream().filter(p -> p.PartNo.equals(wPartNo)).count();
				if (wIsToDo) {
					wSFCTaskIPTPartNo.FQTYToDo = (int) wList.stream().filter(p -> p.PartNo.equals(wPartNo)).count();
					wSFCTaskIPTPartNo.FQTYDone = wSFCTaskIPTPartNo.FQTYTotal - wSFCTaskIPTPartNo.FQTYToDo;
				} else {
					wSFCTaskIPTPartNo.FQTYDone = (int) wList.stream().filter(p -> p.PartNo.equals(wPartNo)).count();
					wSFCTaskIPTPartNo.FQTYToDo = wSFCTaskIPTPartNo.FQTYTotal - wSFCTaskIPTPartNo.FQTYToDo;
				}
				wSFCTaskIPTPartNo.SFCTaskIPTList = wList.stream().filter(p -> p.PartNo.equals(wPartNo))
						.collect(Collectors.toList());
				wResult.Result.add(wSFCTaskIPTPartNo);
			}

			// ??????
			wResult.Result.sort(Comparator.comparing(SFCTaskIPTPartNo::getPartNo));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPTPartNo>> SFC_QuerySpecialPartNo(BMSEmployee wLoginUser) {
		ServiceResult<List<SFCTaskIPTPartNo>> wResult = new ServiceResult<List<SFCTaskIPTPartNo>>();
		wResult.Result = new ArrayList<SFCTaskIPTPartNo>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			Calendar wStartTime = Calendar.getInstance();
			wStartTime.set(Calendar.HOUR_OF_DAY, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);

			Calendar wEndTime = Calendar.getInstance();
			wEndTime.set(Calendar.HOUR_OF_DAY, 23);
			wEndTime.set(Calendar.MINUTE, 59);
			wEndTime.set(Calendar.SECOND, 59);

			List<SFCTaskIPT> wTaskIPTList = this.SFC_QueryTaskIPTList(wLoginUser, SFCTaskType.SpecialCheck.getValue(),
					"", wStartTime, wEndTime, -1).Result;

			List<SFCTaskIPT> wIPTList = (List<SFCTaskIPT>) (this.SFC_QueryProblemTaskAll(wLoginUser,
					SFCTaskType.getEnumType(SFCTaskType.SpecialCheck.getValue()))).Result;
			if (wIPTList.size() > 0) {
				wTaskIPTList.addAll(wIPTList);
			}

			if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
				return wResult;
			}
			List<Integer> wOrderIDList = wTaskIPTList.stream().map(p -> p.OrderID).distinct()
					.collect(Collectors.toList());

			List<OMSOrder> wOrderList = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser, wOrderIDList).List(OMSOrder.class);

			if (wOrderList == null || wOrderList.size() <= 0) {
				return wResult;
			}

			for (OMSOrder wOrder : wOrderList) {
				SFCTaskIPTPartNo wSFCTaskIPTPartNo = new SFCTaskIPTPartNo();
				wSFCTaskIPTPartNo.OrderID = wOrder.ID;
				wSFCTaskIPTPartNo.OrderNo = wOrder.OrderNo;
				wSFCTaskIPTPartNo.Customer = wOrder.Customer;
				wSFCTaskIPTPartNo.LineName = wOrder.LineName;
				wSFCTaskIPTPartNo.PartNo = wOrder.PartNo;
				wSFCTaskIPTPartNo.No = wOrder.PartNo.split("#").length == 2 ? wOrder.PartNo.split("#")[1] : "";
				this.SetFQTYTotalAndDone(wLoginUser, wSFCTaskIPTPartNo, wTaskIPTList, wOrder);

				wSFCTaskIPTPartNo.FQTYToDo = wSFCTaskIPTPartNo.FQTYTotal - wSFCTaskIPTPartNo.FQTYDone;
				wResult.Result.add(wSFCTaskIPTPartNo);
			}

			if (wResult.Result.size() > 0) {
				// ??????
				wResult.Result.sort(Comparator.comparing(SFCTaskIPTPartNo::getNo));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????
	 */
	private void SetFQTYTotalAndDone(BMSEmployee wLoginUser, SFCTaskIPTPartNo wSFCTaskIPTPartNo,
			List<SFCTaskIPT> wTaskIPTList, OMSOrder wOrder) {

		try {
			// ???????????????????????????ID??????
			List<Integer> wPartIDList = wTaskIPTList.stream().filter(p -> p.OrderID == wOrder.ID).map(p -> p.StationID)
					.distinct().collect(Collectors.toList());
			// ????????????????????????????????????
			int wStepSize = 0;
			int wStepDone = 0;
			for (Integer wPartID : wPartIDList) {
				OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
				wStepSize += SFCTaskIPTDAO.getInstance().SelectDoneSize(wLoginUser, wOrder.ID, wPartID, wErrorCode);
				wStepDone += StringUtils.parseInt(wErrorCode.get("DoneSize"));
			}
			// ?????????
			wSFCTaskIPTPartNo.FQTYTotal = wStepSize;
			wSFCTaskIPTPartNo.FQTYDone = wStepDone;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<SFCTaskIPTPart>> SFC_QuerySpecialPartList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<SFCTaskIPTPart>> wResult = new ServiceResult<List<SFCTaskIPTPart>>();
		wResult.Result = new ArrayList<SFCTaskIPTPart>();

		try {
			Calendar wStartTime = Calendar.getInstance();
			wStartTime.set(Calendar.HOUR_OF_DAY, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);

			Calendar wEndTime = Calendar.getInstance();
			wEndTime.set(Calendar.HOUR_OF_DAY, 23);
			wEndTime.set(Calendar.MINUTE, 59);
			wEndTime.set(Calendar.SECOND, 59);

			List<SFCTaskIPT> wTaskIPTList = this.SFC_QueryTaskIPTList(wLoginUser, SFCTaskType.SpecialCheck.getValue(),
					"", wStartTime, wEndTime, -1).Result;

			List<SFCTaskIPT> wIPTList = (List<SFCTaskIPT>) (this.SFC_QueryProblemTaskAll(wLoginUser,
					SFCTaskType.getEnumType(SFCTaskType.SpecialCheck.getValue()))).Result;
			if (wIPTList.size() > 0) {
				wTaskIPTList.addAll(wIPTList);
			}

			if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
				return wResult;
			}

			wTaskIPTList = wTaskIPTList.stream().filter(p -> p.OrderID == wOrderID).collect(Collectors.toList());
			if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
				return wResult;
			}

			List<Integer> wPartIDList = wTaskIPTList.stream().map(p -> p.StationID).distinct()
					.collect(Collectors.toList());

			// ??????????????????
			List<LFSWorkAreaStation> wASList = QMSConstants.GetLFSWorkAreaStationList().values().stream()
					.filter(p -> p.Active == 1).collect(Collectors.toList());

			SFCTaskIPTPart wSFCTaskIPTPart;
			for (int wPartID : wPartIDList) {
				OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

				List<SFCTaskIPT> wPartIPTList = wTaskIPTList.stream().filter(p -> p.StationID == wPartID)
						.collect(Collectors.toList());

				wSFCTaskIPTPart = new SFCTaskIPTPart();
				wSFCTaskIPTPart.Customer = wPartIPTList.get(0).CustomerName;
				wSFCTaskIPTPart.LineName = wPartIPTList.get(0).LineName;
				wSFCTaskIPTPart.OrderID = wPartIPTList.get(0).OrderID;
				wSFCTaskIPTPart.OrderNo = wPartIPTList.get(0).OrderNo;
				wSFCTaskIPTPart.PartID = wPartID;
				wSFCTaskIPTPart.PartName = wPartIPTList.get(0).StationName;
				wSFCTaskIPTPart.PartNo = wPartIPTList.get(0).PartNo;
//				wSFCTaskIPTPart.FQTYTotal = wPartIPTList.size();
//				wSFCTaskIPTPart.FQTYDone = (int) wPartIPTList.stream()
//						.filter(p -> p.Status == SFCTaskStatus.Done.getValue()).count();

				wSFCTaskIPTPart.FQTYTotal = SFCTaskIPTDAO.getInstance().SelectDoneSize(wLoginUser,
						wPartIPTList.get(0).OrderID, wPartID, wErrorCode);
				wSFCTaskIPTPart.FQTYDone = StringUtils.parseInt(wErrorCode.get("DoneSize"));

				wSFCTaskIPTPart.FQTYToDo = wSFCTaskIPTPart.FQTYTotal - wSFCTaskIPTPart.FQTYDone;

				if (wASList.stream().anyMatch(p -> p.StationID == wPartID)) {
					wSFCTaskIPTPart.OrderNum = wASList.stream().filter(p -> p.StationID == wPartID).findFirst()
							.get().OrderNum;
				}

				wResult.Result.add(wSFCTaskIPTPart);
			}

			if (wResult.Result.size() > 0) {
				// ??????
				wResult.Result.sort(Comparator.comparing(SFCTaskIPTPart::getOrderNum));
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCReturnOverMaterial> SFC_QueryDefaultReturnOverMaterial(BMSEmployee wLoginUser,
			int wEventID) {
		ServiceResult<SFCReturnOverMaterial> wResult = new ServiceResult<SFCReturnOverMaterial>();
		wResult.Result = new SFCReturnOverMaterial();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCReturnOverMaterial> wList = SFCReturnOverMaterialDAO.getInstance().SelectList(wLoginUser, -1, "",
					wLoginUser.ID, -1, -1, null, null, new ArrayList<Integer>(Arrays.asList(0)), wErrorCode);

			if (wList.size() > 0) {
				wResult.Result = wList.get(0);
				wResult.Result.CreateTime = Calendar.getInstance();
				wResult.Result.SubmitTime = Calendar.getInstance();
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<SFCReturnOverMaterial> SFC_CreateReturnOverMaterial(BMSEmployee wLoginUser,
			BPMEventModule wEventID) {
		ServiceResult<SFCReturnOverMaterial> wResult = new ServiceResult<SFCReturnOverMaterial>();
		wResult.Result = new SFCReturnOverMaterial();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result.Code = SFCReturnOverMaterialDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wResult.Result.UpFlowID = wLoginUser.ID;
			wResult.Result.UpFlowName = wLoginUser.Name;
			wResult.Result.CreateTime = Calendar.getInstance();
			wResult.Result.SubmitTime = Calendar.getInstance();
			wResult.Result.ID = 0;
			wResult.Result.Status = SFCReturnOverMaterialStatus.Default.getValue();
//			wResult.Result.StatusText = SFCReturnOverMaterialStatus.Default.getLable();
			wResult.Result.FlowType = wEventID.getValue();

			wResult.Result = (SFCReturnOverMaterial) SFCReturnOverMaterialDAO.getInstance().BPM_UpdateTask(wLoginUser,
					wResult.Result, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCReturnOverMaterial> SFC_SubmitReturnOverMaterial(BMSEmployee wLoginUser,
			SFCReturnOverMaterial wData) {
		ServiceResult<SFCReturnOverMaterial> wResult = new ServiceResult<SFCReturnOverMaterial>();
		wResult.Result = new SFCReturnOverMaterial();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wData.Status == 20) {
				wData.StatusText = SFCReturnOverMaterialStatus.NomalClose.getLable();
			}

			wResult.Result = (SFCReturnOverMaterial) SFCReturnOverMaterialDAO.getInstance().BPM_UpdateTask(wLoginUser,
					wData, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCReturnOverMaterial> SFC_GetReturnOverMaterial(BMSEmployee wLoginUser, int wID) {
		ServiceResult<SFCReturnOverMaterial> wResult = new ServiceResult<SFCReturnOverMaterial>();
		wResult.Result = new SFCReturnOverMaterial();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (SFCReturnOverMaterial) SFCReturnOverMaterialDAO.getInstance().BPM_GetTaskInfo(wLoginUser,
					wID, "", wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> SFC_QueryReturnOverMaterialEmployeeAll(BMSEmployee wLoginUser,
			int wTagTypes, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// 2??????
				wResult.Result = SFCReturnOverMaterialDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 1??????
				wResult.Result = SFCReturnOverMaterialDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
						wErrorCode);
				break;
			case Approver:// 4??????
				wResult.Result = SFCReturnOverMaterialDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			if (wResult.Result.size() > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status != 0).collect(Collectors.toList());

				List<BPMTaskBase> wDoneList = wResult.Result.stream().filter(p -> p.Status == 20
						&& wStartTime.compareTo(p.SubmitTime) <= 0 && wEndTime.compareTo(p.CreateTime) >= 0)
						.collect(Collectors.toList());
				List<BPMTaskBase> wToDoList = wResult.Result.stream().filter(p -> p.Status != 20)
						.collect(Collectors.toList());
				wResult.Result = new ArrayList<BPMTaskBase>();
				wResult.Result.addAll(wToDoList);
				wResult.Result.addAll(wDoneList);

				// ??????
				wResult.Result.sort(Comparator.comparing(BPMTaskBase::getCreateTime, Comparator.reverseOrder()));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCReturnOverMaterial>> SFC_QueryReturnOverMaterialHistory(BMSEmployee wLoginUser,
			int wID, String wCode, int wUpFlowID, int wMaterialID, int wPartID, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<SFCReturnOverMaterial>> wResult = new ServiceResult<List<SFCReturnOverMaterial>>();
		wResult.Result = new ArrayList<SFCReturnOverMaterial>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = SFCReturnOverMaterialDAO.getInstance().SelectList(wLoginUser, wID, wCode, wUpFlowID,
					wMaterialID, wPartID, wStartTime, wEndTime, null, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_WithdrawSpecialItem(BMSEmployee wLoginUser, IPTValue wValue) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ?????????????????????
			SFCTaskIPT wTaskIPT = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wValue.TaskID, wErrorCode);
			if (wTaskIPT == null || wTaskIPT.ID <= 0) {
				wResult.FaultCode += "???????????????????????????!";
				return wResult;
			}
			// ???????????????
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wTaskIPT.OrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0 || wOrder.RouteID <= 0) {
				wResult.FaultCode += "???????????????????????????!";
				return wResult;
			}
			// ?????????????????????
			List<FPCRoutePart> wRoutePartList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wOrder.RouteID).collect(Collectors.toList());
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				wResult.FaultCode += "???????????????????????????!";
				return wResult;
			}
			// ?????????????????????
			List<Integer> wPartIDList = QMSUtils.getInstance().FPC_QueryNextStationIDListOnlyOne(wRoutePartList,
					wTaskIPT.StationID);
			// ??????????????????????????????????????????????????????
			APSTaskPart wThisTaskPart = null;
			if (wPartIDList.size() > 0) {
				List<APSTaskPart> wTaskPartList = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskPartAll(wLoginUser, wOrder.ID, APSShiftPeriod.Week.getValue())
						.List(APSTaskPart.class);
				if (wTaskPartList != null && wTaskPartList.size() > 0) {
					wTaskPartList = wTaskPartList.stream().filter(p -> p.Active == 1).collect(Collectors.toList());
//					if (wTaskPartList.stream()
//							.anyMatch(p -> wPartIDList.stream()
//									.anyMatch(q -> q == p.PartID && (p.Status == APSTaskStatus.Started.getValue()
//											|| p.Status == APSTaskStatus.Done.getValue())))) {
//						APSTaskPart wTaskPart = wTaskPartList.stream()
//								.filter(p -> wPartIDList.stream()
//										.anyMatch(q -> q == p.PartID && (p.Status == APSTaskStatus.Started.getValue()
//												|| p.Status == APSTaskStatus.Done.getValue())))
//								.findFirst().get();
//						wResult.FaultCode += StringUtils.Format("????????????{0}??????????????????????????????????????????!", wTaskPart.PartName);
//						return wResult;
//					}

					if (wTaskPartList.stream()
							.anyMatch(p -> p.PartID == wTaskIPT.StationID && p.Active == 1 && p.ShiftPeriod == 5)) {
						wThisTaskPart = wTaskPartList.stream()
								.filter(p -> p.PartID == wTaskIPT.StationID && p.Active == 1 && p.ShiftPeriod == 5)
								.findFirst().get();
					}

					if (wTaskPartList.stream().anyMatch(p -> wPartIDList.stream()
							.anyMatch(q -> q == p.PartID && p.Status == APSTaskStatus.Done.getValue()))) {
						APSTaskPart wTaskPart = wTaskPartList.stream()
								.filter(p -> wPartIDList.stream()
										.anyMatch(q -> q == p.PartID && p.Status == APSTaskStatus.Done.getValue()))
								.findFirst().get();
						wResult.FaultCode += StringUtils.Format("????????????{0}??????????????????????????????????????????!", wTaskPart.PartName);
						return wResult;
					}
				}
			}
			// ??????????????????????????????????????????????????????????????????????????????
			if (QMSServiceImpl.getInstance().WDW_IsSendNCR(wLoginUser, wTaskIPT.TaskStepID,
					(int) wValue.IPTItemID).Result
					|| QMSServiceImpl.getInstance().WDW_IsSendRepair(wLoginUser, wTaskIPT.TaskStepID,
							(int) wValue.IPTItemID).Result) {

				IPTItem wItem = IPTItemDAO.getInstance().SelectByID(wLoginUser, (int) wValue.IPTItemID, wErrorCode);

				wResult.FaultCode += StringUtils.Format("????????????{0}???-???{1}???-???{2}?????????????????????????????????????????????!", wTaskIPT.StationName,
						wTaskIPT.PartPointName, wItem.Text);
				return wResult;
			}
			// ???????????????
			wValue.Status = 1;
			IPTValueDAO.getInstance().Update(wLoginUser, wValue, wErrorCode);
			// ???????????????????????????????????????
			for (Integer wPartID : wPartIDList) {
				List<RSMTurnOrderTask> wTurnList = RSMTurnOrderTaskDAO.getInstance().SelectList(wLoginUser, -1,
						wOrder.ID, -1, wPartID,
						new ArrayList<Integer>(Arrays.asList(RSMTurnOrderTaskStatus.Passed.getValue())), null, null,
						wErrorCode);
				for (RSMTurnOrderTask wRSMTurnOrderTask : wTurnList) {
					wRSMTurnOrderTask.Status = RSMTurnOrderTaskStatus.Auditing.getValue();
					RSMTurnOrderTaskDAO.getInstance().Update(wLoginUser, wRSMTurnOrderTask, wErrorCode);
				}
			}
			// ????????????????????????
			wTaskIPT.Status = 1;
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
			// ???????????????????????????
			wThisTaskPart.Status = 4;
			LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wLoginUser, wThisTaskPart);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FPCPart>> SFC_QueryClassList(BMSEmployee wLoginUser) {
		ServiceResult<List<FPCPart>> wResult = new ServiceResult<List<FPCPart>>();
		wResult.Result = new ArrayList<FPCPart>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wLoginUser.DepartmentID <= 0) {
				return wResult;
			}

			// ?????????????????????????????????
			List<BMSWorkCharge> wList = QMSConstants.GetBMSWorkChargeList().values().stream()
					.filter(p -> p.Active == 1 && p.ClassID == wLoginUser.DepartmentID).collect(Collectors.toList());
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}
			// ?????????????????????
			List<Integer> wStationIDList = wList.stream().map(p -> p.StationID).collect(Collectors.toList());
			wStationIDList.removeIf(p -> p <= 0);
			// ???????????????
			for (Integer wPartID : wStationIDList) {
				FPCPart wPart = QMSConstants.GetFPCPart(wPartID);
				if (wPart == null || wPart.ID <= 0 || wPart.Active != 1) {
					continue;
				}

				wResult.Result.add(wPart);
			}
			// ??????
			if (wResult.Result.size() > 0) {
				wResult.Result = new ArrayList<FPCPart>(wResult.Result.stream()
						.collect(Collectors.toMap(FPCPart::getID, account -> account, (k1, k2) -> k2)).values());
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTemporaryExaminationPartItem>> SFC_QueryItemPartList(BMSEmployee wLoginUser,
			int wSFCTemporaryExaminationID) {
		ServiceResult<List<SFCTemporaryExaminationPartItem>> wResult = new ServiceResult<List<SFCTemporaryExaminationPartItem>>();
		wResult.Result = new ArrayList<SFCTemporaryExaminationPartItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SFCTemporaryExamination wSFCTemTask = SFCTemporaryExaminationDAO.getInstance().SelectByID(wLoginUser,
					wSFCTemporaryExaminationID, wErrorCode);
			if (wSFCTemTask == null || wSFCTemTask.ID <= 0) {
				return wResult;
			}
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTemTask.OrderID)
					.Info(OMSOrder.class);
			for (int wPartID : wSFCTemTask.PartIDList) {
				SFCTemporaryExaminationPartItem wSFCTemporaryExaminationPartItem = new SFCTemporaryExaminationPartItem();
				wSFCTemporaryExaminationPartItem.CustomerID = wOrder.CustomerID;
				wSFCTemporaryExaminationPartItem.CustomerName = wOrder.Customer;
				wSFCTemporaryExaminationPartItem.LineID = wOrder.LineID;
				wSFCTemporaryExaminationPartItem.LineName = wOrder.LineName;
				wSFCTemporaryExaminationPartItem.OrderID = wSFCTemTask.OrderID;
				wSFCTemporaryExaminationPartItem.OrderNo = wOrder.OrderNo;
				wSFCTemporaryExaminationPartItem.PartID = wPartID;
				wSFCTemporaryExaminationPartItem.PartName = QMSConstants.GetFPCPartName(wPartID);
				wSFCTemporaryExaminationPartItem.PartNo = wOrder.PartNo;
				wSFCTemporaryExaminationPartItem.ProductID = wOrder.ProductID;
				wSFCTemporaryExaminationPartItem.ProductNo = wOrder.ProductNo;

				// ??????????????????????????????????????????
				List<SFCTaskIPT> wTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
						wSFCTemTask.OrderID, wPartID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, "", null, null,
						wErrorCode);
				if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
					continue;
				}
				// ?????????????????????????????????????????????
				List<Long> wStandardIDList = wTaskIPTList.stream().map(p -> (long) p.ModuleVersionID)
						.collect(Collectors.toList());
				Map<Long, List<IPTItem>> wItemMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser, wStandardIDList,
						wErrorCode).Result;
				// ???????????????????????????ID????????????????????????????????????
				List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1,
						wSFCTemporaryExaminationID, SFCTaskType.TemporaryCheck.getValue(), -1, -1, wErrorCode);
				// ???????????????????????????????????????
				for (SFCTaskIPT wSFCTaskIPT : wTaskIPTList) {
					if (!wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
						continue;
					}

					List<IPTItem> wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);

					SFCTemporaryExaminationStepItem wSFCTemporaryExaminationStepItem = new SFCTemporaryExaminationStepItem();
					wSFCTemporaryExaminationStepItem.CustomerID = wSFCTemporaryExaminationPartItem.CustomerID;
					wSFCTemporaryExaminationStepItem.CustomerName = wSFCTemporaryExaminationPartItem.CustomerName;
					wSFCTemporaryExaminationStepItem.LineID = wSFCTemporaryExaminationPartItem.LineID;
					wSFCTemporaryExaminationStepItem.LineName = wSFCTemporaryExaminationPartItem.LineName;
					wSFCTemporaryExaminationStepItem.OrderID = wSFCTemporaryExaminationPartItem.OrderID;
					wSFCTemporaryExaminationStepItem.OrderNo = wSFCTemporaryExaminationPartItem.OrderNo;
					wSFCTemporaryExaminationStepItem.PartID = wSFCTemporaryExaminationPartItem.PartID;
					wSFCTemporaryExaminationStepItem.PartName = wSFCTemporaryExaminationPartItem.PartName;
					wSFCTemporaryExaminationStepItem.PartNo = wSFCTemporaryExaminationPartItem.PartNo;
					wSFCTemporaryExaminationStepItem.ProductID = wSFCTemporaryExaminationPartItem.ProductID;
					wSFCTemporaryExaminationStepItem.ProductNo = wSFCTemporaryExaminationPartItem.ProductNo;
					wSFCTemporaryExaminationStepItem.StepID = wSFCTaskIPT.PartPointID;
					wSFCTemporaryExaminationStepItem.StepName = QMSConstants.GetFPCStepName(wSFCTaskIPT.PartPointID);
					wSFCTemporaryExaminationStepItem.Status = 1;
					wSFCTemporaryExaminationStepItem.ValueList = wValueList.stream()
							.filter(p -> wItemList.stream().anyMatch(q -> q.ID == p.IPTItemID))
							.collect(Collectors.toList());

					wSFCTemporaryExaminationStepItem.ToDoList = this.GetToDoList(wLoginUser, wItemList,
							wSFCTemporaryExaminationStepItem.ValueList);
					wSFCTemporaryExaminationStepItem.DoneList = this.GetDoneList(wLoginUser, wItemList,
							wSFCTemporaryExaminationStepItem.ValueList);
					wSFCTemporaryExaminationStepItem.Status = this.GetStatus(wLoginUser, wItemList,
							wSFCTemporaryExaminationStepItem.ValueList);
					wSFCTemporaryExaminationPartItem.StepList.add(wSFCTemporaryExaminationStepItem);
				}

				// ????????????
				if (wSFCTemporaryExaminationPartItem.StepList.stream()
						.allMatch(p -> p.Status == SFCTaskStatus.Done.getValue())) {
					wSFCTemporaryExaminationPartItem.Status = SFCTaskStatus.Done.getValue();
				} else {
					wSFCTemporaryExaminationPartItem.Status = SFCTaskStatus.Active.getValue();
				}
				wResult.Result.add(wSFCTemporaryExaminationPartItem);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTemporaryExaminationStepItem>> SFC_QueryItemStepList(BMSEmployee wLoginUser,
			int wSFCTemporaryExaminationID, int wPartID) {
		ServiceResult<List<SFCTemporaryExaminationStepItem>> wResult = new ServiceResult<List<SFCTemporaryExaminationStepItem>>();
		wResult.Result = new ArrayList<SFCTemporaryExaminationStepItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SFCTemporaryExamination wSFCTemTask = SFCTemporaryExaminationDAO.getInstance().SelectByID(wLoginUser,
					wSFCTemporaryExaminationID, wErrorCode);
			if (wSFCTemTask == null || wSFCTemTask.ID <= 0) {
				return wResult;
			}

			if (!wSFCTemTask.PartIDList.stream().anyMatch(p -> p == wPartID)) {
				return wResult;
			}
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTemTask.OrderID)
					.Info(OMSOrder.class);
			SFCTemporaryExaminationPartItem wSFCTemporaryExaminationPartItem = new SFCTemporaryExaminationPartItem();
			wSFCTemporaryExaminationPartItem.CustomerID = wOrder.CustomerID;
			wSFCTemporaryExaminationPartItem.CustomerName = wOrder.Customer;
			wSFCTemporaryExaminationPartItem.LineID = wOrder.LineID;
			wSFCTemporaryExaminationPartItem.LineName = wOrder.LineName;
			wSFCTemporaryExaminationPartItem.OrderID = wSFCTemTask.OrderID;
			wSFCTemporaryExaminationPartItem.OrderNo = wOrder.OrderNo;
			wSFCTemporaryExaminationPartItem.PartID = wPartID;
			wSFCTemporaryExaminationPartItem.PartName = QMSConstants.GetFPCPartName(wPartID);
			wSFCTemporaryExaminationPartItem.PartNo = wOrder.PartNo;
			wSFCTemporaryExaminationPartItem.ProductID = wOrder.ProductID;
			wSFCTemporaryExaminationPartItem.ProductNo = wOrder.ProductNo;

			// ??????????????????????????????????????????
			List<SFCTaskIPT> wTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
					wSFCTemTask.OrderID, wPartID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, "", null, null,
					wErrorCode);
			if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
				return wResult;
			}
			// ?????????????????????????????????????????????
			List<Long> wStandardIDList = wTaskIPTList.stream().map(p -> (long) p.ModuleVersionID)
					.collect(Collectors.toList());
			Map<Long, List<IPTItem>> wItemMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser, wStandardIDList,
					wErrorCode).Result;
			// ???????????????????????????ID????????????????????????????????????
			List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1,
					wSFCTemporaryExaminationID, SFCTaskType.TemporaryCheck.getValue(), -1, -1, wErrorCode);
			// ???????????????????????????????????????
			for (SFCTaskIPT wSFCTaskIPT : wTaskIPTList) {
				if (!wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
					continue;
				}

				List<IPTItem> wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);

				SFCTemporaryExaminationStepItem wSFCTemporaryExaminationStepItem = new SFCTemporaryExaminationStepItem();
				wSFCTemporaryExaminationStepItem.CustomerID = wSFCTemporaryExaminationPartItem.CustomerID;
				wSFCTemporaryExaminationStepItem.CustomerName = wSFCTemporaryExaminationPartItem.CustomerName;
				wSFCTemporaryExaminationStepItem.LineID = wSFCTemporaryExaminationPartItem.LineID;
				wSFCTemporaryExaminationStepItem.LineName = wSFCTemporaryExaminationPartItem.LineName;
				wSFCTemporaryExaminationStepItem.OrderID = wSFCTemporaryExaminationPartItem.OrderID;
				wSFCTemporaryExaminationStepItem.OrderNo = wSFCTemporaryExaminationPartItem.OrderNo;
				wSFCTemporaryExaminationStepItem.PartID = wSFCTemporaryExaminationPartItem.PartID;
				wSFCTemporaryExaminationStepItem.PartName = wSFCTemporaryExaminationPartItem.PartName;
				wSFCTemporaryExaminationStepItem.PartNo = wSFCTemporaryExaminationPartItem.PartNo;
				wSFCTemporaryExaminationStepItem.ProductID = wSFCTemporaryExaminationPartItem.ProductID;
				wSFCTemporaryExaminationStepItem.ProductNo = wSFCTemporaryExaminationPartItem.ProductNo;
				wSFCTemporaryExaminationStepItem.StepID = wSFCTaskIPT.PartPointID;
				wSFCTemporaryExaminationStepItem.StepName = QMSConstants.GetFPCStepName(wSFCTaskIPT.PartPointID);
				wSFCTemporaryExaminationStepItem.Status = 1;
				wSFCTemporaryExaminationStepItem.ValueList = wValueList.stream()
						.filter(p -> wItemList.stream().anyMatch(q -> q.ID == p.IPTItemID))
						.collect(Collectors.toList());

				wSFCTemporaryExaminationStepItem.ToDoList = this.GetToDoList(wLoginUser, wItemList,
						wSFCTemporaryExaminationStepItem.ValueList);
				wSFCTemporaryExaminationStepItem.DoneList = this.GetDoneList(wLoginUser, wItemList,
						wSFCTemporaryExaminationStepItem.ValueList);
				wSFCTemporaryExaminationStepItem.Status = this.GetStatus(wLoginUser, wItemList,
						wSFCTemporaryExaminationStepItem.ValueList);
				wSFCTemporaryExaminationPartItem.StepList.add(wSFCTemporaryExaminationStepItem);
			}

			wResult.Result = wSFCTemporaryExaminationPartItem.StepList;

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTItem>> SFC_QueryItemItemList(BMSEmployee wLoginUser, int wSFCTemporaryExaminationID,
			int wPartID, int wStepID) {
		ServiceResult<List<IPTItem>> wResult = new ServiceResult<List<IPTItem>>();
		wResult.Result = new ArrayList<IPTItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SFCTemporaryExamination wSFCTemTask = SFCTemporaryExaminationDAO.getInstance().SelectByID(wLoginUser,
					wSFCTemporaryExaminationID, wErrorCode);
			if (wSFCTemTask == null || wSFCTemTask.ID <= 0) {
				return wResult;
			}

			if (!wSFCTemTask.PartIDList.stream().anyMatch(p -> p == wPartID)) {
				return wResult;
			}
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTemTask.OrderID)
					.Info(OMSOrder.class);
			SFCTemporaryExaminationPartItem wSFCTemporaryExaminationPartItem = new SFCTemporaryExaminationPartItem();
			wSFCTemporaryExaminationPartItem.CustomerID = wOrder.CustomerID;
			wSFCTemporaryExaminationPartItem.CustomerName = wOrder.Customer;
			wSFCTemporaryExaminationPartItem.LineID = wOrder.LineID;
			wSFCTemporaryExaminationPartItem.LineName = wOrder.LineName;
			wSFCTemporaryExaminationPartItem.OrderID = wSFCTemTask.OrderID;
			wSFCTemporaryExaminationPartItem.OrderNo = wOrder.OrderNo;
			wSFCTemporaryExaminationPartItem.PartID = wPartID;
			wSFCTemporaryExaminationPartItem.PartName = QMSConstants.GetFPCPartName(wPartID);
			wSFCTemporaryExaminationPartItem.PartNo = wOrder.PartNo;
			wSFCTemporaryExaminationPartItem.ProductID = wOrder.ProductID;
			wSFCTemporaryExaminationPartItem.ProductNo = wOrder.ProductNo;

			// ??????????????????????????????????????????
			List<SFCTaskIPT> wTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser,
					wSFCTemTask.OrderID, wPartID, SFCTaskType.SpecialCheck.getValue(), -1, wStepID, -1, "", null, null,
					wErrorCode);
			if (wTaskIPTList == null || wTaskIPTList.size() <= 0) {
				return wResult;
			}
			// ?????????????????????????????????????????????
			List<Long> wStandardIDList = wTaskIPTList.stream().map(p -> (long) p.ModuleVersionID)
					.collect(Collectors.toList());
			Map<Long, List<IPTItem>> wItemMap = IPTStandardDAO.getInstance().SelectItem(wLoginUser, wStandardIDList,
					wErrorCode).Result;
			// ???????????????????????????ID????????????????????????????????????
			List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1,
					wSFCTemporaryExaminationID, SFCTaskType.TemporaryCheck.getValue(), -1, -1, wErrorCode);
			// ???????????????????????????????????????
			SFCTaskIPT wSFCTaskIPT = wTaskIPTList.get(0);
			if (!wItemMap.containsKey((long) wSFCTaskIPT.ModuleVersionID)) {
				return wResult;
			}

			List<IPTItem> wItemList = wItemMap.get((long) wSFCTaskIPT.ModuleVersionID);

			SFCTemporaryExaminationStepItem wSFCTemporaryExaminationStepItem = new SFCTemporaryExaminationStepItem();
			wSFCTemporaryExaminationStepItem.CustomerID = wSFCTemporaryExaminationPartItem.CustomerID;
			wSFCTemporaryExaminationStepItem.CustomerName = wSFCTemporaryExaminationPartItem.CustomerName;
			wSFCTemporaryExaminationStepItem.LineID = wSFCTemporaryExaminationPartItem.LineID;
			wSFCTemporaryExaminationStepItem.LineName = wSFCTemporaryExaminationPartItem.LineName;
			wSFCTemporaryExaminationStepItem.OrderID = wSFCTemporaryExaminationPartItem.OrderID;
			wSFCTemporaryExaminationStepItem.OrderNo = wSFCTemporaryExaminationPartItem.OrderNo;
			wSFCTemporaryExaminationStepItem.PartID = wSFCTemporaryExaminationPartItem.PartID;
			wSFCTemporaryExaminationStepItem.PartName = wSFCTemporaryExaminationPartItem.PartName;
			wSFCTemporaryExaminationStepItem.PartNo = wSFCTemporaryExaminationPartItem.PartNo;
			wSFCTemporaryExaminationStepItem.ProductID = wSFCTemporaryExaminationPartItem.ProductID;
			wSFCTemporaryExaminationStepItem.ProductNo = wSFCTemporaryExaminationPartItem.ProductNo;
			wSFCTemporaryExaminationStepItem.StepID = wSFCTaskIPT.PartPointID;
			wSFCTemporaryExaminationStepItem.StepName = QMSConstants.GetFPCStepName(wSFCTaskIPT.PartPointID);
			wSFCTemporaryExaminationStepItem.Status = 1;
			wSFCTemporaryExaminationStepItem.ValueList = wValueList.stream()
					.filter(p -> wItemList.stream().anyMatch(q -> q.ID == p.IPTItemID)).collect(Collectors.toList());

			wSFCTemporaryExaminationStepItem.ToDoList = this.GetToDoList(wLoginUser, wItemList,
					wSFCTemporaryExaminationStepItem.ValueList);
			wSFCTemporaryExaminationStepItem.DoneList = this.GetDoneList(wLoginUser, wItemList,
					wSFCTemporaryExaminationStepItem.ValueList);
			wSFCTemporaryExaminationStepItem.Status = this.GetStatus(wLoginUser, wItemList,
					wSFCTemporaryExaminationStepItem.ValueList);
			wSFCTemporaryExaminationPartItem.StepList.add(wSFCTemporaryExaminationStepItem);

			wResult.Result = wSFCTemporaryExaminationStepItem.ToDoList;
			wResult.CustomResult.put("DoneList", wSFCTemporaryExaminationStepItem.DoneList);
			wResult.CustomResult.put("ValueList", wSFCTemporaryExaminationStepItem.ValueList);
			wResult.CustomResult.put("Info", wSFCTemporaryExaminationStepItem);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<Integer> SFC_SubmitStepList(BMSEmployee wLoginUser, List<Integer> wDataIDList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			for (Integer wSpecialTaskID : wDataIDList) {
				// ??????????????????
				SFCTaskIPT wTask = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wSpecialTaskID, wErrorCode);

				// ???????????????
				List<SFCTaskIPT> wMList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wTask.TaskStepID,
						SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, -1, null, null, wErrorCode);

				if (wTask.Type == 2) {
					// ??????????????????
					IPTPreCheckProblem wProblemInfo = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
							wTask.TaskStepID, wErrorCode);
					if (wProblemInfo.ID <= 0) {
						continue;
					}
					// ??????????????????
					List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, wSpecialTaskID,
							wErrorCode);
					// ?????????Value???
					IPTValue wMaxValue = wValueList.stream().filter(p -> p.IPTItemID == wProblemInfo.IPTItemID)
							.collect(Collectors.toList()).stream().max(Comparator.comparing(IPTValue::getSubmitTime))
							.get();
					wMaxValue.ID = 0;
					wMaxValue.TaskID = wSpecialTaskID;
					wMaxValue.IPTMode = SFCTaskType.SpecialCheck.getValue();
					wMaxValue.SubmitID = wLoginUser.ID;
					wMaxValue.SubmitTime = Calendar.getInstance();
					// ????????????????????????
					wProblemInfo.Status = IPTPreCheckProblemStatus.Done.getValue();
					IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wProblemInfo, wErrorCode);
					// ?????????Value???
					IPTValueDAO.getInstance().Update(wLoginUser, wMaxValue, wErrorCode);
					// ??????????????????
					wTask.Status = 2;
					wTask.EndTime = Calendar.getInstance();
					SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wTask, wErrorCode);
				} else {
					if (wMList.size() > 0 && StringUtils.isNotEmpty(wMList.get(0).PicUri)) {
						wTask.PicUri = wMList.get(0).PicUri;
						wTask.Remark = wMList.get(0).Remark;
						wTask.StartTime = wTask.SubmitTime;
						wTask.EndTime = Calendar.getInstance();
					} else {
						// ?????????????????????
						List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, wSpecialTaskID,
								wErrorCode);
						// ??????????????????
						List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, wSpecialTaskID,
								wErrorCode);
						// ?????????????????????????????????
						for (IPTItem wIPTItem : wItemList) {

							if (!wValueList.stream().anyMatch(p -> p.IPTItemID == wIPTItem.ID)) {
								continue;
							}

							IPTValue wMaxValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID)
									.collect(Collectors.toList()).stream()
									.max(Comparator.comparing(IPTValue::getSubmitTime)).get();
							wMaxValue.ID = 0;
							wMaxValue.TaskID = wSpecialTaskID;
							wMaxValue.IPTMode = SFCTaskType.SpecialCheck.getValue();
							wMaxValue.SubmitID = wLoginUser.ID;
							wMaxValue.SubmitTime = Calendar.getInstance();

							IPTValueDAO.getInstance().Update(wLoginUser, wMaxValue, wErrorCode);
						}
					}

					if (wTask.ID > 0) {
						if (!wTask.OperatorList.contains(wLoginUser.ID)) {
							wTask.OperatorList.add(wLoginUser.ID);
						}
						wTask.Status = 2;
						wTask.StartTime = wTask.SubmitTime;
						wTask.EndTime = Calendar.getInstance();
						SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wTask, wErrorCode);

						if (StringUtils.isNotEmpty(wTask.PicUri)) {
							// ?????????????????????
							APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
									.APS_QueryTaskStepByID(wLoginUser, wTask.TaskStepID).Info(APSTaskStep.class);
							if (wTaskStep != null && wTaskStep.ID > 0) {
								wTaskStep.Status = 5;
								wTaskStep.EndTime = Calendar.getInstance();
								LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
							}
							// ?????????????????????
							List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
									.SFC_QueryTaskStepList(wLoginUser, wTaskStep.ID, -1, -1).List(SFCTaskStep.class);
							if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
								for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
									wSFCTaskStep.EndTime = Calendar.getInstance();
									wSFCTaskStep.EditTime = Calendar.getInstance();
									wSFCTaskStep.IsStartWork = 2;
									LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
								}
							}
						}
					}
				}

				boolean wFlag = SFCTaskIPTDAO.getInstance().JudgeSpecialTaskIsFinished(wLoginUser, wTask, wErrorCode);
				if (wFlag) {
					// ?????????????????????
					APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskStepByID(wLoginUser, wTask.TaskStepID).Info(APSTaskStep.class);
					if (wTaskStep != null && wTaskStep.ID > 0) {
						wTaskStep.Status = 5;
						wTaskStep.EndTime = Calendar.getInstance();
						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
					}
					// ?????????????????????
					List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
							.SFC_QueryTaskStepList(wLoginUser, wTaskStep.ID, -1, -1).List(SFCTaskStep.class);
					if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
						for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
							wSFCTaskStep.EndTime = Calendar.getInstance();
							wSFCTaskStep.EditTime = Calendar.getInstance();
							wSFCTaskStep.IsStartWork = 2;
							LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
						}
					}
				}
			}

			// ?????????????????????
			ExecutorService wES = Executors.newFixedThreadPool(1);
			wES.submit(() -> SFCTaskIPTDAO.getInstance().SFC_CloseRelaMessage(wLoginUser, wErrorCode));
			wES.shutdown();

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCCarInfo> SFC_QueryCarInfo(BMSEmployee wLoginUser, int wID) {
		ServiceResult<SFCCarInfo> wResult = new ServiceResult<SFCCarInfo>();
		wResult.Result = new SFCCarInfo();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = SFCCarInfoDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCCarInfo>> SFC_QueryCarInfoList(BMSEmployee wLoginUser, int wID, int wOrderID) {
		ServiceResult<List<SFCCarInfo>> wResult = new ServiceResult<List<SFCCarInfo>>();
		wResult.Result = new ArrayList<SFCCarInfo>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = SFCCarInfoDAO.getInstance().SelectList(wLoginUser, wID, wOrderID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_UpdateCarInfo(BMSEmployee wLoginUser, SFCCarInfo wSFCCarInfo) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wSFCCarInfo.ID <= 0) {
				List<SFCCarInfo> wList = SFCCarInfoDAO.getInstance().SelectList(wLoginUser, -1, wSFCCarInfo.OrderID,
						wErrorCode);
				if (wList != null && wList.size() > 0) {
					wResult.FaultCode += StringUtils.Format("????????????{0}#{1}???????????????????????????????????????????????????!", wSFCCarInfo.ProductNo,
							wSFCCarInfo.CarNo);
					return wResult;
				}
			}

			// ?????????????????????
			wSFCCarInfo.CreaterID = wLoginUser.ID;
			wSFCCarInfo.CreateTime = Calendar.getInstance();
			wSFCCarInfo.ID = SFCCarInfoDAO.getInstance().Update(wLoginUser, wSFCCarInfo, wErrorCode);
			// ?????????????????????
			// ?????????????????????????????????????????????
			List<SFCRankInfo> wRankList = SFCRankInfoDAO.getInstance().SelectList(wLoginUser, -1, wSFCCarInfo.ID,
					wErrorCode);
			// ??????????????????????????????????????????????????????
			wRankList = wRankList.stream()
					.filter(p -> !wSFCCarInfo.SFCRankInfoList.stream().anyMatch(q -> q.ID == p.ID))
					.collect(Collectors.toList());
			if (wRankList != null && wRankList.size() > 0) {
				SFCRankInfoDAO.getInstance().DeleteList(wLoginUser, wRankList, wErrorCode);
				// ????????????
				for (SFCRankInfo wRankInfo : wRankList) {
					SFCPartInfoDAO.getInstance().DeleteList(wLoginUser, wRankInfo.SFCPartInfoList, wErrorCode);
					// ????????????
					for (SFCPartInfo wPartInfo : wRankInfo.SFCPartInfoList) {
						SFCUploadPicDAO.getInstance().DeleteList(wLoginUser, wPartInfo.SFCUploadPicList, wErrorCode);
					}
				}
			}
			// ???????????????????????????
			if (wSFCCarInfo.SFCRankInfoList != null && wSFCCarInfo.SFCRankInfoList.size() > 0) {
				wSFCCarInfo.SFCRankInfoList.forEach(p -> p.CarID = wSFCCarInfo.ID);
				for (SFCRankInfo wSFCRankInfo : wSFCCarInfo.SFCRankInfoList) {
					wSFCRankInfo.ID = SFCRankInfoDAO.getInstance().Update(wLoginUser, wSFCRankInfo, wErrorCode);
				}
			}
			// ?????????????????????
			if (wSFCCarInfo.SFCRankInfoList != null && wSFCCarInfo.SFCRankInfoList.size() > 0) {
				for (SFCRankInfo wSFCRankInfo : wSFCCarInfo.SFCRankInfoList) {
					if (wSFCRankInfo.SFCPartInfoList == null || wSFCRankInfo.SFCPartInfoList.size() <= 0) {
						continue;
					}
					// ?????????????????????????????????????????????
					List<SFCPartInfo> wPartInfoList = SFCPartInfoDAO.getInstance().SelectList(wLoginUser, -1,
							wSFCRankInfo.ID, wErrorCode);
					// ??????????????????????????????????????????????????????
					wPartInfoList = wPartInfoList.stream()
							.filter(p -> !wSFCRankInfo.SFCPartInfoList.stream().anyMatch(q -> q.ID == p.ID))
							.collect(Collectors.toList());
					if (wPartInfoList != null && wPartInfoList.size() > 0) {
						SFCPartInfoDAO.getInstance().DeleteList(wLoginUser, wPartInfoList, wErrorCode);
					}
					// ???????????????????????????
					if (wSFCRankInfo.SFCPartInfoList != null && wSFCRankInfo.SFCPartInfoList.size() > 0) {
						wSFCRankInfo.SFCPartInfoList.forEach(p -> p.RankID = wSFCRankInfo.ID);
						for (SFCPartInfo wSFCPartInfo : wSFCRankInfo.SFCPartInfoList) {
							wSFCPartInfo.ID = SFCPartInfoDAO.getInstance().Update(wLoginUser, wSFCPartInfo, wErrorCode);
						}
					}
				}
			}
			// ?????????????????????
			if (wSFCCarInfo.SFCRankInfoList != null && wSFCCarInfo.SFCRankInfoList.size() > 0) {
				for (SFCRankInfo wSFCRankInfo : wSFCCarInfo.SFCRankInfoList) {
					if (wSFCRankInfo.SFCPartInfoList == null || wSFCRankInfo.SFCPartInfoList.size() <= 0) {
						continue;
					}

					for (SFCPartInfo wSFCPartInfo : wSFCRankInfo.SFCPartInfoList) {
						// ?????????????????????????????????????????????
						List<SFCUploadPic> wPicList = SFCUploadPicDAO.getInstance().SelectList(wLoginUser, -1,
								wSFCPartInfo.ID, wErrorCode);
						// ??????????????????????????????????????????????????????
						wPicList = wPicList.stream()
								.filter(p -> !wSFCPartInfo.SFCUploadPicList.stream().anyMatch(q -> q.ID == p.ID))
								.collect(Collectors.toList());
						if (wPicList != null && wPicList.size() > 0) {
							SFCUploadPicDAO.getInstance().DeleteList(wLoginUser, wPicList, wErrorCode);
						}
						// ???????????????????????????
						if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() > 0) {
							wSFCPartInfo.SFCUploadPicList.forEach(p -> p.PartID = wSFCPartInfo.ID);
							for (SFCUploadPic wSFCUploadPic : wSFCPartInfo.SFCUploadPicList) {
								wSFCUploadPic.ID = SFCUploadPicDAO.getInstance().Update(wLoginUser, wSFCUploadPic,
										wErrorCode);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????(?????????)
	 */
	private void CreateProductDir(SFCCarInfo wCarInfo, String wTimestamp) {
		try {
			String wCode = "256247000981";
			// ??????????????????????????????
			String wRootPath = StringUtils.Format("{0}static/export/{1}/",
					new Object[] { Constants.getConfigPath().replace("config/", ""), wTimestamp });
			// ??????????????????
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wCarDir = StringUtils.Format("{0}{1}{2}-{3}", wRootPath, wCarInfo.ProductNo, wCarInfo.CarNo,
					wCurTime);
			File wDirFile = new File(wCarDir);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			// ??????????????????
			for (SFCRankInfo wSFCRankInfo : wCarInfo.SFCRankInfoList) {
				String wRankDir = StringUtils.Format("{0}/???{1}???", wCarDir, wSFCRankInfo.Remark);
				wDirFile = new File(wRankDir);
				if (!wDirFile.exists()) {
					wDirFile.mkdirs();
				}
				// ??????????????????
				for (SFCPartInfo wSFCPartInfo : wSFCRankInfo.SFCPartInfoList) {

					String wPartDir = StringUtils.Format("{0}/{1}", wRankDir, wSFCPartInfo.Remark);

					if (!wSFCPartInfo.Remark.equals("????????????????????????")) {
						wPartDir = StringUtils.Format("{0}/{1}({2})", wRankDir, wSFCPartInfo.Remark,
								GetCodeName(wSFCPartInfo, wCode));
					}

					wDirFile = new File(wPartDir);
					if (!wDirFile.exists()) {
						wDirFile.mkdirs();
					}

					if (wSFCPartInfo.Remark.equals("????????????????????????")) {
						// ??????????????????
						String wCCDir = StringUtils.Format("{0}/??????({1})", wPartDir,
								GetSixCodeName1(wSFCPartInfo, wCode));
						wDirFile = new File(wCCDir);
						if (!wDirFile.exists()) {
							wDirFile.mkdirs();
						}
						// ?????????????????????
						String wFCCDir = StringUtils.Format("{0}/?????????({1})", wPartDir,
								GetSixCodeName2(wSFCPartInfo, wCode));
						wDirFile = new File(wFCCDir);
						if (!wDirFile.exists()) {
							wDirFile.mkdirs();
						}
						// ???????????????
						if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() > 0
								&& wSFCPartInfo.SFCUploadPicList.stream().anyMatch(p -> p.Remark.contains("????????????"))) {
							List<SFCUploadPic> wList = wSFCPartInfo.SFCUploadPicList.stream()
									.filter(p -> p.Remark.contains("????????????")).collect(Collectors.toList());
							for (SFCUploadPic wSFCUploadPic : wList) {
								if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
									String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

									String[] wStrs = wSFCUploadPic.PicUrl.split(";");
									for (int i = 0; i < wStrs.length; i++) {
										String wWebUri = StringUtils.Format("{0}{1}", wUri, wStrs[i]);

										String wCorePath = Configuration.readConfigString("staticPath",
												"config/config");
										String wSrcPath = StringUtils.Format("{0}{1}", wCorePath,
												wStrs[i].replace("/MESCore", ""));

										String wSuffix = this.GetSuffix(wWebUri);
										String wDesPath = StringUtils.Format("{0}/{1}{2}.{3}", wCCDir,
												wSFCUploadPic.Remark.replace("????????????", ""), String.format("%02d", i + 1),
												wSuffix);

										MESFileUtils.copyFile(wSrcPath, wDesPath);
									}
								}
							}
						}
						// ??????????????????
						if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() > 0
								&& wSFCPartInfo.SFCUploadPicList.stream().anyMatch(p -> p.Remark.contains("???????????????"))) {
							List<SFCUploadPic> wList = wSFCPartInfo.SFCUploadPicList.stream()
									.filter(p -> p.Remark.contains("???????????????")).collect(Collectors.toList());
							for (SFCUploadPic wSFCUploadPic : wList) {
								if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
									String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

									String[] wStrs = wSFCUploadPic.PicUrl.split(";");
									for (int i = 0; i < wStrs.length; i++) {
										String wWebUri = StringUtils.Format("{0}{1}", wUri, wStrs[i]);

										String wCorePath = Configuration.readConfigString("staticPath",
												"config/config");
										String wSrcPath = StringUtils.Format("{0}{1}", wCorePath,
												wStrs[i].replace("/MESCore", ""));

										String wSuffix = this.GetSuffix(wWebUri);
										String wDesPath = StringUtils.Format("{0}/{1}{2}.{3}", wFCCDir,
												wSFCUploadPic.Remark.replace("???????????????", ""), String.format("%02d", i + 1),
												wSuffix);

										MESFileUtils.copyFile(wSrcPath, wDesPath);
									}
								}
							}
						}
					} else {
						// ??????????????????
						for (SFCUploadPic wSFCUploadPic : wSFCPartInfo.SFCUploadPicList) {
							String wPicDir = StringUtils.Format("{0}/{1}", wPartDir, wSFCUploadPic.Remark);
							wDirFile = new File(wPicDir);
							if (!wDirFile.exists()) {
								wDirFile.mkdirs();
							}
							// ?????????????????????
							if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
								String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

								String[] wStrs = wSFCUploadPic.PicUrl.split(";");
								for (int i = 0; i < wStrs.length; i++) {
									String wWebUri = StringUtils.Format("{0}{1}", wUri, wStrs[i]);

									String wCorePath = Configuration.readConfigString("staticPath", "config/config");
									String wSrcPath = StringUtils.Format("{0}{1}", wCorePath,
											wStrs[i].replace("/MESCore", ""));

									String wSuffix = this.GetSuffix(wWebUri);
									String wDesPath = StringUtils.Format("{0}/{1}.{2}", wPicDir,
											String.format("%02d", i + 1), wSuffix);

									MESFileUtils.copyFile(wSrcPath, wDesPath);
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<String> SFC_ExportPicZip(BMSEmployee wLoginUser, int wID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			String wCode = "256247000981";
			// ?????????????????????
			SFCCarInfo wCarInfo = SFCCarInfoDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			if (wCarInfo == null || wCarInfo.ID <= 0) {
				return wResult;
			}
			// ??????????????????????????????
			String wRootPath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			// ??????????????????
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wCarDir = StringUtils.Format("{0}{1}{2}-{3}", wRootPath, wCarInfo.ProductNo, wCarInfo.CarNo,
					wCurTime);
			File wDirFile = new File(wCarDir);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			// ??????????????????
			for (SFCRankInfo wSFCRankInfo : wCarInfo.SFCRankInfoList) {
				String wRankDir = StringUtils.Format("{0}/???{1}???", wCarDir, wSFCRankInfo.Remark);
				wDirFile = new File(wRankDir);
				if (!wDirFile.exists()) {
					wDirFile.mkdirs();
				}
				// ??????????????????
				int wIndex = 1;
				for (SFCPartInfo wSFCPartInfo : wSFCRankInfo.SFCPartInfoList) {

					String wPartDir = StringUtils.Format("{0}/{1}", wRankDir, wSFCPartInfo.Remark);

					if (wIndex != 6) {
						wPartDir = StringUtils.Format("{0}/{1}({2})", wRankDir, wSFCPartInfo.Remark,
								GetCodeName(wSFCPartInfo, wCode));
					}

					wDirFile = new File(wPartDir);
					if (!wDirFile.exists()) {
						wDirFile.mkdirs();
					}

					if (wIndex == 6) {
						// ??????????????????
						String wCCDir = StringUtils.Format("{0}/??????({1})", wPartDir,
								GetSixCodeName1(wSFCPartInfo, wCode));
						wDirFile = new File(wCCDir);
						if (!wDirFile.exists()) {
							wDirFile.mkdirs();
						}
						// ?????????????????????
						String wFCCDir = StringUtils.Format("{0}/?????????({1})", wPartDir,
								GetSixCodeName2(wSFCPartInfo, wCode));
						wDirFile = new File(wFCCDir);
						if (!wDirFile.exists()) {
							wDirFile.mkdirs();
						}
						// ???????????????
						if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() > 0
								&& wSFCPartInfo.SFCUploadPicList.stream().anyMatch(p -> p.Remark.contains("????????????"))) {
							List<SFCUploadPic> wList = wSFCPartInfo.SFCUploadPicList.stream()
									.filter(p -> p.Remark.contains("????????????")).collect(Collectors.toList());
							for (SFCUploadPic wSFCUploadPic : wList) {
								if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
									String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

									String[] wStrs = wSFCUploadPic.PicUrl.split(";");
									for (int i = 0; i < wStrs.length; i++) {
										String wWebUri = StringUtils.Format("{0}{1}", wUri, wStrs[i]);

										String wCorePath = Configuration.readConfigString("staticPath",
												"config/config");
										String wSrcPath = StringUtils.Format("{0}{1}", wCorePath,
												wStrs[i].replace("/MESCore", ""));

										String wSuffix = this.GetSuffix(wWebUri);
										String wDesPath = StringUtils.Format("{0}/{1}{2}.{3}", wCCDir,
												wSFCUploadPic.Remark.replace("????????????", ""), String.format("%02d", i + 1),
												wSuffix);

										MESFileUtils.copyFile(wSrcPath, wDesPath);
									}
								}
							}
						}
						// ??????????????????
						if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() > 0
								&& wSFCPartInfo.SFCUploadPicList.stream().anyMatch(p -> p.Remark.contains("???????????????"))) {
							List<SFCUploadPic> wList = wSFCPartInfo.SFCUploadPicList.stream()
									.filter(p -> p.Remark.contains("???????????????")).collect(Collectors.toList());
							for (SFCUploadPic wSFCUploadPic : wList) {
								if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
									String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

									String[] wStrs = wSFCUploadPic.PicUrl.split(";");
									for (int i = 0; i < wStrs.length; i++) {
										String wWebUri = StringUtils.Format("{0}{1}", wUri, wStrs[i]);

										String wCorePath = Configuration.readConfigString("staticPath",
												"config/config");
										String wSrcPath = StringUtils.Format("{0}{1}", wCorePath,
												wStrs[i].replace("/MESCore", ""));

										String wSuffix = this.GetSuffix(wWebUri);
										String wDesPath = StringUtils.Format("{0}/{1}{2}.{3}", wFCCDir,
												wSFCUploadPic.Remark.replace("???????????????", ""), String.format("%02d", i + 1),
												wSuffix);

										MESFileUtils.copyFile(wSrcPath, wDesPath);
									}
								}
							}
						}
					} else {
						// ??????????????????
						for (SFCUploadPic wSFCUploadPic : wSFCPartInfo.SFCUploadPicList) {
							String wPicDir = StringUtils.Format("{0}/{1}", wPartDir, wSFCUploadPic.Remark);
							wDirFile = new File(wPicDir);
							if (!wDirFile.exists()) {
								wDirFile.mkdirs();
							}
							// ?????????????????????
							if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
								String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

								String[] wStrs = wSFCUploadPic.PicUrl.split(";");
								for (int i = 0; i < wStrs.length; i++) {
									String wWebUri = StringUtils.Format("{0}{1}", wUri, wStrs[i]);

									String wCorePath = Configuration.readConfigString("staticPath", "config/config");
									String wSrcPath = StringUtils.Format("{0}{1}", wCorePath,
											wStrs[i].replace("/MESCore", ""));

									String wSuffix = this.GetSuffix(wWebUri);
									String wDesPath = StringUtils.Format("{0}/{1}.{2}", wPicDir,
											String.format("%02d", i + 1), wSuffix);

									MESFileUtils.copyFile(wSrcPath, wDesPath);
								}
							}
						}
					}

					wIndex++;
				}
			}
			// ?????????????????????
			String wZipFilename = StringUtils.Format("{0}{1}-{2}.zip", wCarInfo.ProductNo, wCarInfo.CarNo, wCurTime);
			MESFileUtils.compressToZip(wCarDir, wRootPath, wZipFilename);
			// ?????????????????????
			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wZipFilename);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private String GetCodeName(SFCPartInfo wSFCPartInfo, String wCode) {
		String wResult = "";
		try {
			if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() > 0) {
				wResult = StringUtils.Format("{0}{1}", wCode, wSFCPartInfo.SFCUploadPicList.get(0).Describe);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private String GetSixCodeName1(SFCPartInfo wSFCPartInfo, String wCode) {
		String wResult = wCode;
		try {
			if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() == 10) {
				wResult = StringUtils.Format("{0}{1}", wCode, wSFCPartInfo.SFCUploadPicList.get(0).Describe);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private String GetSixCodeName2(SFCPartInfo wSFCPartInfo, String wCode) {
		String wResult = wCode;
		try {
			if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() == 10) {
				wResult = StringUtils.Format("{0}{1}", wCode, wSFCPartInfo.SFCUploadPicList.get(5).Describe);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????
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

	@Override
	public ServiceResult<Integer> SFC_DeleteAllPic(BMSEmployee wLoginUser, int wID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SFCCarInfo wCarInfo = SFCCarInfoDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			if (wCarInfo == null || wCarInfo.ID <= 0) {
				return wResult;
			}

			// ?????????????????????
			String wRootPath = Configuration.readConfigString("staticPath", "config/config");

			for (SFCRankInfo wSFCRankInfo : wCarInfo.SFCRankInfoList) {
				for (SFCPartInfo wSFCPartInfo : wSFCRankInfo.SFCPartInfoList) {
					for (SFCUploadPic wSFCUploadPic : wSFCPartInfo.SFCUploadPicList) {
						if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
							String[] wStrs = wSFCUploadPic.PicUrl.split(";");
							for (String wStr : wStrs) {
								wStr = wStr.replace("/MESCore", "");
								String wFilePath = StringUtils.Format("{0}{1}", wRootPath, wStr);
								File wFile = new File(wFilePath);
								if (wFile.isFile() && wFile.exists()) {
									wFile.delete();
									logger.info(StringUtils.Format("???{0}?????????????????????", wFilePath));
								}
							}
						}
					}
				}
			}
			// ?????????????????????
			wCarInfo.CarNo = StringUtils.Format("{0}?????????????????????", wCarInfo.CarNo);
			SFCCarInfoDAO.getInstance().Update(wLoginUser, wCarInfo, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_DeleteSinglePic(BMSEmployee wLoginUser, String wUri) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			String wRootPath = Configuration.readConfigString("staticPath", "config/config");

			wUri = wUri.replace("/MESCore", "");
			String wFilePath = StringUtils.Format("{0}{1}", wRootPath, wUri);
			File wFile = new File(wFilePath);
			if (wFile.isFile() && wFile.exists()) {
				wFile.delete();
				logger.info(StringUtils.Format("???{0}?????????????????????", wFilePath));
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCProgress> SFC_QueryProgress(BMSEmployee wLoginUser, String wUUID) {
		ServiceResult<SFCProgress> wResult = new ServiceResult<SFCProgress>();
		wResult.Result = new SFCProgress();
		try {
			if (QMSConstants.mSFCProgressList.stream().anyMatch(p -> p.UUID.equals(wUUID))) {
				wResult.Result = QMSConstants.mSFCProgressList.stream().filter(p -> p.UUID.equals(wUUID)).findFirst()
						.get();
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> SFC_ExportPicZip(BMSEmployee wLoginUser, int wID, String wUUID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ?????????????????????
			SFCCarInfo wCarInfo = SFCCarInfoDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			if (wCarInfo == null || wCarInfo.ID <= 0) {
				return wResult;
			}

			// ??????????????????
			int wTotalSize = 0;
			for (SFCRankInfo wRankInfo : wCarInfo.SFCRankInfoList) {
				for (SFCPartInfo wPartInfo : wRankInfo.SFCPartInfoList) {
					for (SFCUploadPic wSFCUploadPic : wPartInfo.SFCUploadPicList) {
						if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
							String[] wStrs = wSFCUploadPic.PicUrl.split(";");
							wTotalSize += wStrs.length;
						}
					}
				}
			}
			wTotalSize++;
			// ?????????????????????
			SFCProgress wProgress = new SFCProgress();
			wProgress.Max = wTotalSize;
			wProgress.Percent = "0%";
			wProgress.UUID = wUUID;
			wProgress.Value = 0;
			QMSConstants.mSFCProgressList.add(wProgress);
			// ??????????????????????????????
			String wRootPath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			// ??????????????????
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wCarDir = StringUtils.Format("{0}{1}{2}-{3}", wRootPath, wCarInfo.ProductNo, wCarInfo.CarNo,
					wCurTime);
			File wDirFile = new File(wCarDir);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			// ??????????????????
			for (SFCRankInfo wSFCRankInfo : wCarInfo.SFCRankInfoList) {
				String wRankDir = StringUtils.Format("{0}/???{1}???", wCarDir, wSFCRankInfo.Remark);
				wDirFile = new File(wRankDir);
				if (!wDirFile.exists()) {
					wDirFile.mkdirs();
				}
				// ??????????????????
				int wIndex = 1;
				for (SFCPartInfo wSFCPartInfo : wSFCRankInfo.SFCPartInfoList) {
					String wPartDir = StringUtils.Format("{0}/{1}", wRankDir, wSFCPartInfo.Remark);
					wDirFile = new File(wPartDir);
					if (!wDirFile.exists()) {
						wDirFile.mkdirs();
					}

					if (wIndex == 6) {
						// ??????????????????
						String wCCDir = StringUtils.Format("{0}/??????", wPartDir);
						wDirFile = new File(wCCDir);
						if (!wDirFile.exists()) {
							wDirFile.mkdirs();
						}
						// ?????????????????????
						String wFCCDir = StringUtils.Format("{0}/?????????", wPartDir);
						wDirFile = new File(wFCCDir);
						if (!wDirFile.exists()) {
							wDirFile.mkdirs();
						}
						// ???????????????
						if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() > 0
								&& wSFCPartInfo.SFCUploadPicList.stream().anyMatch(p -> p.Remark.contains("????????????"))) {
							List<SFCUploadPic> wList = wSFCPartInfo.SFCUploadPicList.stream()
									.filter(p -> p.Remark.contains("????????????")).collect(Collectors.toList());
							for (SFCUploadPic wSFCUploadPic : wList) {
								if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
									String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

									String[] wStrs = wSFCUploadPic.PicUrl.split(";");
									for (int i = 0; i < wStrs.length; i++) {
										String wWebUri = StringUtils.Format("{0}{1}", wUri, wStrs[i]);

										String wCorePath = Configuration.readConfigString("staticPath",
												"config/config");
										String wSrcPath = StringUtils.Format("{0}{1}", wCorePath,
												wStrs[i].replace("/MESCore", ""));

										String wSuffix = this.GetSuffix(wWebUri);
										String wDesPath = StringUtils.Format("{0}/{1}{2}.{3}", wCCDir,
												wSFCUploadPic.Remark.replace("????????????", ""), String.format("%02d", i + 1),
												wSuffix);

										MESFileUtils.copyFile(wSrcPath, wDesPath);
										// ??????????????????
										wProgress.Value++;
										wProgress.Percent = StringUtils.Format("{0}%",
												(int) ((double) wProgress.Value / wProgress.Max * 100));
									}
								}
							}
						}
						// ??????????????????
						if (wSFCPartInfo.SFCUploadPicList != null && wSFCPartInfo.SFCUploadPicList.size() > 0
								&& wSFCPartInfo.SFCUploadPicList.stream().anyMatch(p -> p.Remark.contains("???????????????"))) {
							List<SFCUploadPic> wList = wSFCPartInfo.SFCUploadPicList.stream()
									.filter(p -> p.Remark.contains("???????????????")).collect(Collectors.toList());
							for (SFCUploadPic wSFCUploadPic : wList) {
								if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
									String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

									String[] wStrs = wSFCUploadPic.PicUrl.split(";");
									for (int i = 0; i < wStrs.length; i++) {
										String wWebUri = StringUtils.Format("{0}{1}", wUri, wStrs[i]);

										String wCorePath = Configuration.readConfigString("staticPath",
												"config/config");
										String wSrcPath = StringUtils.Format("{0}{1}", wCorePath,
												wStrs[i].replace("/MESCore", ""));

										String wSuffix = this.GetSuffix(wWebUri);
										String wDesPath = StringUtils.Format("{0}/{1}{2}.{3}", wFCCDir,
												wSFCUploadPic.Remark.replace("???????????????", ""), String.format("%02d", i + 1),
												wSuffix);

										MESFileUtils.copyFile(wSrcPath, wDesPath);

										// ??????????????????
										wProgress.Value++;
										wProgress.Percent = StringUtils.Format("{0}%",
												(int) ((double) wProgress.Value / wProgress.Max * 100));
									}
								}
							}
						}
					} else {
						// ??????????????????
						for (SFCUploadPic wSFCUploadPic : wSFCPartInfo.SFCUploadPicList) {
							String wPicDir = StringUtils.Format("{0}/{1}", wPartDir, wSFCUploadPic.Remark);
							wDirFile = new File(wPicDir);
							if (!wDirFile.exists()) {
								wDirFile.mkdirs();
							}
							// ?????????????????????
							if (StringUtils.isNotEmpty(wSFCUploadPic.PicUrl)) {
								String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

								String[] wStrs = wSFCUploadPic.PicUrl.split(";");
								for (int i = 0; i < wStrs.length; i++) {
									String wWebUri = StringUtils.Format("{0}{1}", wUri, wStrs[i]);

									String wCorePath = Configuration.readConfigString("staticPath", "config/config");
									String wSrcPath = StringUtils.Format("{0}{1}", wCorePath,
											wStrs[i].replace("/MESCore", ""));

									String wSuffix = this.GetSuffix(wWebUri);
									String wDesPath = StringUtils.Format("{0}/{1}.{2}", wPicDir,
											String.format("%02d", i + 1), wSuffix);

									MESFileUtils.copyFile(wSrcPath, wDesPath);

									// ??????????????????
									wProgress.Value++;
									wProgress.Percent = StringUtils.Format("{0}%",
											(int) ((double) wProgress.Value / wProgress.Max * 100));
								}
							}
						}
					}

					wIndex++;
				}
			}
			// ?????????????????????
			String wZipFilename = StringUtils.Format("{0}{1}-{2}.zip", wCarInfo.ProductNo, wCarInfo.CarNo, wCurTime);

			long startTime = System.currentTimeMillis();
			logger.info("compressToZip:");

			MESFileUtils.compressToZip(wCarDir, wRootPath, wZipFilename);

			long endTime = System.currentTimeMillis();
			logger.info("????????????????????? " + (endTime - startTime) + "ms");

			// ?????????????????????
			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wZipFilename);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());

			// ??????????????????
			wProgress.Value++;
			wProgress.Percent = StringUtils.Format("{0}%", (int) ((double) wProgress.Value / wProgress.Max * 100));
			// ??????????????????
			QMSConstants.mSFCProgressList.removeIf(p -> !p.UUID.equals(wUUID) && p.Value >= p.Max);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> SFC_CheckPGPowerNew(BMSEmployee wLoginUser, List<SFCTaskStep> wSFCTaskStepList) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			int wChangeOrderID = 0;
			if (wSFCTaskStepList.get(0).PartName.contains("????????????")) {
				wChangeOrderID = SFCBogiesChangeBPMDAO.getInstance().GetChangeOrderID(wLoginUser,
						wSFCTaskStepList.get(0).OrderID, wErrorCode);
			}

			// ??????????????????ID??????
			for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
				// ???????????????????????????????????????
				String wCheckFlag = RSMTurnOrderTaskDAO.getInstance().CheckLetGo(wLoginUser, wSFCTaskStep.OrderID,
						wSFCTaskStep.PartID, wErrorCode);
				if (StringUtils.isNotEmpty(wCheckFlag)) {
					wResult.Result = wCheckFlag;
					return wResult;
				}

				List<Integer> wIDList = RSMTurnOrderTaskDAO.getInstance().SelectPreStationIDList(wLoginUser,
						wSFCTaskStep.TaskStepID, wSFCTaskStep.PartID, wErrorCode);

				for (Integer wPartID : wIDList) {
					boolean wCheckResult = RSMTurnOrderTaskDAO.getInstance().JudgeIsLetGo(wLoginUser,
							wSFCTaskStep.OrderID, wPartID, wErrorCode);
					if (wCheckResult) {
						continue;
					}
					// ??????????????????????????????
					boolean wBoolean = RSMTurnOrderTaskDAO.getInstance().JudgeIsTurnOrder(wLoginUser,
							wSFCTaskStep.TaskStepID, wPartID, wErrorCode);
					if (wBoolean) {
						// ???????????????????????????????????????
//						List<Integer> wStepIDList = RSMTurnOrderTaskDAO.getInstance().SelectPreStepIDList(wLoginUser,
//								wSFCTaskStep.TaskStepID, wSFCTaskStep.StepID, wErrorCode);
//						for (Integer wStepID : wStepIDList) {
//							boolean wFlag = RSMTurnOrderTaskDAO.getInstance().JudgeIsFinish(wLoginUser,
//									wSFCTaskStep.TaskStepID, wStepID, wSFCTaskStep, wErrorCode);
//							if (!wFlag) {
//								wResult.Result = StringUtils.Format("???{0}????????????????????????,???{1}?????????????????????!",
//										QMSConstants.GetFPCStepName(wStepID), wSFCTaskStep.PartPointName);
//								return wResult;
//							}
//						}
						continue;
					}

					// ?????????????????????????????????????????????????????????????????????????????????????????????
					if (wChangeOrderID > 0) {
						continue;
					}

					// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
					boolean wIsLetGo = SFCLetPassBPMDAO.getInstance().JudgeIsLetGo(wLoginUser, wSFCTaskStep, wPartID,
							wErrorCode);
					if (wIsLetGo) {
						continue;
					}

					wResult.Result = StringUtils.Format("???{0}????????????????????????{1}?????????????????????!", QMSConstants.GetFPCPartName(wPartID),
							wSFCTaskStep.PartPointName);
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
	public ServiceResult<List<SFCTaskIPTPartNo>> SFC_QuerySpecialPartNoNew(BMSEmployee wLoginUser) {
		ServiceResult<List<SFCTaskIPTPartNo>> wResult = new ServiceResult<List<SFCTaskIPTPartNo>>();
		wResult.Result = new ArrayList<SFCTaskIPTPartNo>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			Calendar wStartTime = Calendar.getInstance();
			wStartTime.add(Calendar.DATE, -7);
			wStartTime.set(Calendar.HOUR_OF_DAY, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);

			Calendar wEndTime = Calendar.getInstance();
			wEndTime.set(Calendar.HOUR_OF_DAY, 23);
			wEndTime.set(Calendar.MINUTE, 59);
			wEndTime.set(Calendar.SECOND, 59);

			List<Integer> wOrderIDList = SFCTaskIPTDAO.getInstance().SFC_QuerySpecialPartNoOrderIDList_V1(wLoginUser,
					wStartTime, wEndTime, wErrorCode);

			for (Integer wOrderID : wOrderIDList) {
				SFCTaskIPTPartNo wInfo = SFCTaskIPTDAO.getInstance().SFC_QuerySpecialPartNoNew_V1(wLoginUser, wOrderID,
						wStartTime, wEndTime, wErrorCode);
				wResult.Result.add(wInfo);
			}

			if (wResult.Result.size() > 0) {
				// ??????
				wResult.Result.sort(Comparator.comparing(SFCTaskIPTPartNo::getNo));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPTPart>> SFC_QuerySpecialPartListNew(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<SFCTaskIPTPart>> wResult = new ServiceResult<List<SFCTaskIPTPart>>();
		wResult.Result = new ArrayList<SFCTaskIPTPart>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			Calendar wStartTime = Calendar.getInstance();
			wStartTime.add(Calendar.DATE, -7);
			wStartTime.set(Calendar.HOUR_OF_DAY, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);

			Calendar wEndTime = Calendar.getInstance();
			wEndTime.set(Calendar.HOUR_OF_DAY, 23);
			wEndTime.set(Calendar.MINUTE, 59);
			wEndTime.set(Calendar.SECOND, 59);

			List<Integer> wStationIDList = SFCTaskIPTDAO.getInstance().SFC_QuerySpecialPartIDList_V1(wLoginUser,
					wOrderID, wStartTime, wEndTime, wErrorCode);
			for (Integer wStationID : wStationIDList) {
				SFCTaskIPTPart wPartInfo = SFCTaskIPTDAO.getInstance().SFC_QuerySpecialPartListNew_V1(wLoginUser,
						wOrderID, wStationID, wStartTime, wEndTime, wErrorCode);
				wResult.Result.add(wPartInfo);
			}

			if (wResult.Result.size() > 0) {
				// ??????????????????
				List<LFSWorkAreaStation> wASList = QMSConstants.GetLFSWorkAreaStationList().values().stream()
						.filter(p -> p.Active == 1).collect(Collectors.toList());
				for (SFCTaskIPTPart wSFCTaskIPTPart : wResult.Result) {
					if (wASList.stream().anyMatch(p -> p.StationID == wSFCTaskIPTPart.PartID)) {
						wSFCTaskIPTPart.OrderNum = wASList.stream().filter(p -> p.StationID == wSFCTaskIPTPart.PartID)
								.findFirst().get().OrderNum;
					}
				}
				// ??????
				wResult.Result.sort(Comparator.comparing(SFCTaskIPTPart::getOrderNum));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListNew(BMSEmployee wLoginUser, int wTaskType,
			String wPartNo, Calendar wStartTime, Calendar wEndTime, int wOrderID, int wPartID) {
		ServiceResult<List<SFCTaskIPT>> wResult = new ServiceResult<List<SFCTaskIPT>>();
		wResult.Result = new ArrayList<SFCTaskIPT>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<SFCTaskIPT> wList = null;
			List<APSTaskStep> wTaskStepList = null;
			switch (SFCTaskType.getEnumType(wTaskType)) {
			case SelfCheck:
				wList = SFCTaskIPTDAO.getInstance().SelectSelfTaskList(wLoginUser, wPartNo, wStartTime, wEndTime,
						wOrderID, wErrorCode);
				// ?????????????????????????????????????????????
				wTaskStepList = SFCTaskIPTDAO.getInstance().SFC_SelectAPSTaskStepList(wLoginUser, wList, wErrorCode);
				// ???????????????????????? ????????????????????????1????????????????????????????????????????????????????????????-??????????????????<=1?????????
				if (wTaskStepList.size() > 0 && wList.size() > 0) {
//					List<APSTaskStep> wNewList = wTaskStepList;
//					wList.removeIf(p -> p.Status == 1 && !p.OperatorList.stream().anyMatch(q -> q == wLoginUser.ID)
//							&& wNewList.stream().anyMatch(r -> r.OperatorList.size() > 1 && r.ID == p.TaskStepID
//									&& r.OperatorList.size() - p.OperatorList.size() <= 1));
				}

				// ????????????????????????
				for (SFCTaskIPT wSFCTaskIPT : wList) {
					if (wSFCTaskIPT.Type != 2) {
						continue;
					}

					IPTPreCheckProblem wInfo = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
							wSFCTaskIPT.TaskStepID, wErrorCode);
					if (wInfo != null && wInfo.ID > 0) {
						wSFCTaskIPT.PartPointName = wInfo.IPTItemName;
					}
				}

				wResult.Result = wList;
				break;
			case MutualCheck:
				wList = SFCTaskIPTDAO.getInstance().SelectMutualTaskList(wLoginUser, wPartNo, wStartTime, wEndTime,
						wOrderID, wErrorCode);
				// ???????????????????????????
				wTaskStepList = SFCTaskIPTDAO.getInstance().SFC_SelectAPSTaskStepList(wLoginUser, wList, wErrorCode);
				// ????????????????????????
				List<SFCTaskIPT> wSelfIPTList = SFCTaskIPTDAO.getInstance().SFC_SelectSelfTaskIPTList(wLoginUser, wList,
						wErrorCode);
				// ???????????????????????????????????????????????????
				List<Integer> wTaskIPTIDList = wList.stream().filter(p -> p.Type == 2).map(p -> p.ID)
						.collect(Collectors.toList());
				List<SFCTaskStep> wProblemTaskStepList = SFCTaskIPTDAO.getInstance()
						.SFC_QueryProblemTaskStepList(wLoginUser, wTaskIPTIDList, wErrorCode);
				// ?????????????????????????????????????????????
				List<Integer> wTaskIDList = SFCTaskIPTDAO.getInstance().GetMutualTaskIDListByMessage(wLoginUser,
						wErrorCode);
				// ????????????????????????????????????????????????????????? ??????????????????????????????????????????????????????
				for (SFCTaskIPT wSFCTaskIPT : wList) {
					if (wSFCTaskIPT.Type == 2) {
						if (wProblemTaskStepList.stream().anyMatch(p -> p.TaskStepID == wSFCTaskIPT.TaskStepID
								&& QMSConstants.GetBMSEmployee(p.MonitorID).DepartmentID == wLoginUser.DepartmentID
								&& wSFCTaskIPT.OperatorID != wLoginUser.ID)) {
							wResult.Result.add(wSFCTaskIPT);
						}
					} else {
						if (wTaskStepList.stream()
								.anyMatch(p -> p.ID == wSFCTaskIPT.TaskStepID && p.OperatorList.size() == 1)) {
							if (wTaskStepList.stream().anyMatch(p -> p.ID == wSFCTaskIPT.TaskStepID
									&& p.OperatorList.get(0) != wLoginUser.ID && p.ShiftID == wLoginUser.DepartmentID
									&& QMSConstants.GetBMSPosition(wLoginUser.Position).DutyID == 1)) {
								wResult.Result.add(wSFCTaskIPT);
							} else {
								// ????????????????????????????????????????????????
								APSTaskStep wTaskStep = wTaskStepList.stream()
										.filter(p -> p.ID == wSFCTaskIPT.TaskStepID && p.OperatorList.size() == 1)
										.findFirst().get();
								if (QMSConstants.GetBMSEmployeeList().values().stream()
										.filter(p -> QMSConstants.GetBMSPosition(p.Position).DutyID == 1
												&& wTaskStep.ShiftID == p.DepartmentID)
										.count() == 1 && wLoginUser.ID == wTaskStep.OperatorList.get(0).intValue()) {
									wResult.Result.add(wSFCTaskIPT);
								}
							}
						} else if (wTaskStepList.stream()
								.anyMatch(p -> p.ID == wSFCTaskIPT.TaskStepID && p.OperatorList.size() > 1)) {
							// ????????????????????????????????????????????????????????? ????????????????????????????????????????????????????????????
//							if (wTaskStepList.stream()
//									.anyMatch(p -> p.ID == wSFCTaskIPT.TaskStepID
//											&& p.OperatorList.stream().anyMatch(q -> q == wLoginUser.ID))
//									&& wSelfIPTList.stream().anyMatch(p -> p.TaskStepID == wSFCTaskIPT.TaskStepID
//											&& !p.OperatorList.stream().anyMatch(q -> q == wLoginUser.ID))) {
//								wResult.Result.add(wSFCTaskIPT);
//							}

							if (wTaskStepList.stream().anyMatch(p -> p.ID == wSFCTaskIPT.TaskStepID
									&& p.OperatorList.stream().anyMatch(q -> q == wLoginUser.ID))) {

								SFCTaskIPT wSeftTask = wSelfIPTList.stream()
										.filter(p -> p.TaskStepID == wSFCTaskIPT.TaskStepID).findFirst().get();
								if (StringUtils.isNotEmpty(wSeftTask.PicUri)) {
									if (!wSeftTask.OperatorList.stream().anyMatch(p -> p.intValue() == wLoginUser.ID)) {
										wResult.Result.add(wSFCTaskIPT);
									}
								} else {
									wResult.Result.add(wSFCTaskIPT);
								}
							}
						}
					}
				}

				// ????????????????????????
				for (SFCTaskIPT wSFCTaskIPT : wResult.Result) {
					if (wSFCTaskIPT.Type != 2) {
						continue;
					}

					IPTPreCheckProblem wInfo = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
							wSFCTaskIPT.TaskStepID, wErrorCode);
					if (wInfo != null && wInfo.ID > 0) {
						wSFCTaskIPT.PartPointName = wInfo.IPTItemName;
					}
				}

				// ???????????????????????????????????????
				wResult.Result
						.removeIf(p -> p.Status == 1 && !wTaskIDList.stream().anyMatch(q -> q.intValue() == p.ID));
				break;
			case SpecialCheck:
//				wResult.Result = SFCTaskIPTDAO.getInstance().SelectSpecialTaskList(wLoginUser, wPartNo, wStartTime,
//						wEndTime, wOrderID, wErrorCode);
//
//				// ???????????????????????????????????????
//				List<Integer> wTaskIDList = SFCTaskIPTDAO.getInstance().SFC_SelectNotFinishSpecialTaskIDList(wLoginUser,
//						wErrorCode);
//				wResult.Result.removeIf(p -> p.Status == 1 && !wTaskIDList.stream().anyMatch(q -> q == p.ID));

				wResult.Result = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID, wPartID,
						SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);

				wResult.Result = wResult.Result.stream()
						.filter(p -> p.CheckerList.contains(String.valueOf(wLoginUser.ID)))
						.collect(Collectors.toList());

				for (SFCTaskIPT wSFCTaskIPT : wResult.Result) {
					if (wSFCTaskIPT.Type != 2) {
						continue;
					}

					IPTPreCheckProblem wInfo = IPTPreCheckProblemDAO.getInstance().SelectByID(wLoginUser,
							wSFCTaskIPT.TaskStepID, wErrorCode);
					if (wInfo != null && wInfo.ID > 0) {
						wSFCTaskIPT.PartPointName = wInfo.IPTItemName;
					}
				}
				break;
			case PreCheck:
			case Final:
			case OutPlant:
				wResult.Result = SFCTaskIPTDAO.getInstance().SelectOtherTaskList(wLoginUser, wPartNo, wStartTime,
						wEndTime, wOrderID, wTaskType, wErrorCode);
			default:
				break;
			}

			if (wResult.Result.size() <= 0)
				return wResult;
			List<Integer> wOrderIDList = wResult.Result.stream().map(p -> p.OrderID).distinct()
					.collect(Collectors.toList());

			Map<Integer, OMSOrder> wOMSOrderMap = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser, wOrderIDList).List(OMSOrder.class).stream()
					.collect(Collectors.toMap(p -> p.ID, p -> p));

			for (SFCTaskIPT wSFCTaskIPT : wResult.Result) {
				if (!wOMSOrderMap.containsKey(wSFCTaskIPT.OrderID))
					continue;

				wSFCTaskIPT.CustomerID = wOMSOrderMap.get(wSFCTaskIPT.OrderID).CustomerID;
				wSFCTaskIPT.CustomerName = wOMSOrderMap.get(wSFCTaskIPT.OrderID).Customer;
			}

			// ????????????????????????
			wResult.Result = wResult.Result.stream().filter(p -> p.Active == 1).collect(Collectors.toList());

			// ??????????????????IsPic??????
			if (wResult.Result.size() > 0) {
				List<Integer> wIDList = wResult.Result.stream().map(p -> p.ModuleVersionID).distinct()
						.collect(Collectors.toList());
				Map<Integer, Integer> wStandardIDMap = SFCTaskIPTDAO.getInstance().GetStandardIDMap(wLoginUser, wIDList,
						wErrorCode);
				for (SFCTaskIPT wSFCTaskIPT : wResult.Result) {
					if (!wStandardIDMap.containsKey(wSFCTaskIPT.ModuleVersionID)) {
						continue;
					}
					wSFCTaskIPT.IsPic = wStandardIDMap.get(wSFCTaskIPT.ModuleVersionID);
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
	public ServiceResult<String> SFC_ExportPartInfoByCondition(BMSEmployee wLoginUser, List<Integer> wIDList,
			List<Integer> wPartIDList) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wIDList == null || wIDList.size() <= 0 || (wIDList.size() == 1 && wIDList.get(0) == -1)) {
				List<SFCCarInfo> wList = SFCCarInfoDAO.getInstance().SelectList(wLoginUser, -1, -1, wErrorCode);
				wIDList = wList.stream().map(p -> p.ID).collect(Collectors.toList());
			}

			if (wPartIDList == null || wPartIDList.size() <= 0
					|| (wPartIDList.size() == 1 && wPartIDList.get(0) == -1)) {
				wPartIDList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6));
			}

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMddHHmmss");
			String wTimestamp = wSDF.format(Calendar.getInstance().getTime());

			List<Integer> wNewPartIDList = wPartIDList;
			for (Integer wTaskID : wIDList) {
				SFCCarInfo wCarInfo = SFCCarInfoDAO.getInstance().SelectByID(wLoginUser, wTaskID, wErrorCode);
				for (SFCRankInfo wSFCRankInfo : wCarInfo.SFCRankInfoList) {
					if (wSFCRankInfo.SFCPartInfoList == null || wSFCRankInfo.SFCPartInfoList.size() <= 0) {
						continue;
					}
					wSFCRankInfo.SFCPartInfoList = wSFCRankInfo.SFCPartInfoList.stream()
							.filter(p -> wNewPartIDList.stream().anyMatch(q -> q == p.No)).collect(Collectors.toList());
				}
				CreateProductDir(wCarInfo, wTimestamp);
			}
			// ?????????????????????
			String wRootPath = StringUtils.Format("{0}static/export/{1}",
					Constants.getConfigPath().replace("config/", ""), wTimestamp);
			String wRootDir = StringUtils.Format("{0}static/export/", Constants.getConfigPath().replace("config/", ""));
			MESFileUtils.compressToZip(wRootPath, wRootDir, wTimestamp + ".zip");
			// ???????????????????????????
			wResult.Result = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wTimestamp + ".zip");

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FocasPart>> SFC_QueryMSSPartList(BMSEmployee wLoginUser, String wPartNo) {
		ServiceResult<List<FocasPart>> wResult = new ServiceResult<List<FocasPart>>();
		wResult.Result = new ArrayList<FocasPart>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wPartNo == null || wPartNo.length() <= 4) {
				wResult.FaultCode += "????????????:" + wPartNo + " ????????????";
				return wResult;
			}

			if (!wPartNo.contains("#")) {
				wPartNo = wPartNo.substring(0, wPartNo.length() - 4) + "#" + wPartNo.substring(wPartNo.length() - 4);
			}

			List<OMSOrder> wOMSOrderList = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderList(wLoginUser, -1, "", -1, -1, -1, wPartNo, "", -1, null).List(OMSOrder.class);
			if (wOMSOrderList == null || wOMSOrderList.size() <= 0) {
				wResult.FaultCode += "??????:" + wPartNo + " ????????????";
				return wResult;
			}

			OMSOrder wOrder = wOMSOrderList.get(0);
			wResult.Result = RSMTurnOrderTaskDAO.getInstance().SelectMSSPartList(wLoginUser, wOrder.ID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPTInfo>> SFC_QueryTaskStepDetails(BMSEmployee wLoginUser, int wAPSTaskStepID) {
		ServiceResult<List<SFCTaskIPTInfo>> wResult = new ServiceResult<List<SFCTaskIPTInfo>>();
		wResult.Result = new ArrayList<SFCTaskIPTInfo>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ????????????
			List<Integer> wTaskIPTIDList = SFCTaskIPTDAO.getInstance().SelectTaskIDList(wLoginUser, wAPSTaskStepID,
					wErrorCode);
			for (Integer wTaskIPTID : wTaskIPTIDList) {
				wResult.Result.add(SFCTaskIPTDAO.getInstance().SelectSFCTaskIPTInfo(wLoginUser, wAPSTaskStepID,
						wTaskIPTID, wErrorCode));
			}
			// ??????????????????
			List<Integer> wNCRIDList = SFCTaskIPTDAO.getInstance().SelectNCRTaskIDList(wLoginUser, wAPSTaskStepID,
					wErrorCode);
			for (Integer wNCRTaskID : wNCRIDList) {
				wResult.Result.add(SFCTaskIPTDAO.getInstance().SelectNCRTaskIPTInfo(wLoginUser, wAPSTaskStepID,
						wNCRTaskID, wErrorCode));
			}
			// ?????????
			List<Integer> wRepariIDList = SFCTaskIPTDAO.getInstance().SelectRepairTaskIDList(wLoginUser, wAPSTaskStepID,
					wErrorCode);
			for (Integer wRepairTaskID : wRepariIDList) {
				wResult.Result.add(SFCTaskIPTDAO.getInstance().SelectRepairTaskIPTInfo(wLoginUser, wAPSTaskStepID,
						wRepairTaskID, wErrorCode));
			}

			if (wResult.Result.size() > 0) {
				int wIndex = 1;
				for (SFCTaskIPTInfo wSFCTaskIPTInfo : wResult.Result) {
					wSFCTaskIPTInfo.ID = wIndex++;
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
	public ServiceResult<List<SFCLoginEventPart>> SFC_QueryEmployeeAllPart(BMSEmployee wLoginUser) {
		ServiceResult<List<SFCLoginEventPart>> wResult = new ServiceResult<List<SFCLoginEventPart>>();
		wResult.Result = new ArrayList<SFCLoginEventPart>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ????????????
			Calendar wTodaySTime = Calendar.getInstance();
			wTodaySTime.set(Calendar.HOUR_OF_DAY, 0);
			wTodaySTime.set(Calendar.MINUTE, 0);
			wTodaySTime.set(Calendar.SECOND, 0);
			Calendar wTodayETime = Calendar.getInstance();
			wTodayETime.set(Calendar.HOUR_OF_DAY, 23);
			wTodayETime.set(Calendar.MINUTE, 59);
			wTodayETime.set(Calendar.SECOND, 59);

			List<SFCTaskStep> wList = SFCLoginEventDAO.getInstance().SFC_SelectTaskStepList(wLoginUser, wTodaySTime,
					wTodayETime, -1, -1, wErrorCode);

			List<LFSWorkAreaStation> wAreaStationList = QMSConstants.GetLFSWorkAreaStationList().values().stream()
					.filter(p -> p.Active == 1).collect(Collectors.toList());

			if (wList.size() > 0) {
				List<SFCTaskStep> wStepList = wList.stream()
						.collect(Collectors.collectingAndThen(Collectors.toCollection(
								() -> new TreeSet<>(Comparator.comparing(o -> o.getOrderID() + ";" + o.getPartID()))),
								ArrayList::new));
				for (SFCTaskStep wSFCTaskStep : wStepList) {
					SFCLoginEventPart wSFCLoginEventPart = new SFCLoginEventPart();

					wSFCLoginEventPart.ClockSize = (int) wList.stream()
							.filter(p -> p.OrderID == wSFCTaskStep.OrderID && p.PartID == wSFCTaskStep.PartID
									&& (p.IsStartWork == 1 || p.IsStartWork == 2 || p.IsStartWork == 3))
							.count();
//					wSFCLoginEventPart.IsTurnOrder = wSFCTaskStep.IsTurnOrder;
					wSFCLoginEventPart.LineName = wSFCTaskStep.LineName;
					wSFCLoginEventPart.OrderID = wSFCTaskStep.OrderID;
					wSFCLoginEventPart.PartID = wSFCTaskStep.PartID;
					wSFCLoginEventPart.PartName = wSFCTaskStep.PartName;
					wSFCLoginEventPart.PartNo = wSFCTaskStep.PartNo;
					wSFCLoginEventPart.StepSize = (int) wList.stream()
							.filter(p -> p.OrderID == wSFCTaskStep.OrderID && p.PartID == wSFCTaskStep.PartID).count();

					if (wAreaStationList.stream().anyMatch(p -> p.StationID == wSFCTaskStep.PartID)) {
						wSFCLoginEventPart.OrderNum = wAreaStationList.stream()
								.filter(p -> p.StationID == wSFCTaskStep.PartID).findFirst().get().OrderNum;
					}

					wResult.Result.add(wSFCLoginEventPart);
				}
			}

			if (wResult.Result.size() > 0) {
				// ??????
				wResult.Result.sort(Comparator.comparing(SFCLoginEventPart::getPartNo)
						.thenComparing(SFCLoginEventPart::getOrderNum));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskStep>> SFC_QueryClockEmployeeAllNew(BMSEmployee wLoginUser, int wOrderID,
			int wPartID, Calendar wStartTime, Calendar wEndTime, int wTagTypes) {
		ServiceResult<List<SFCTaskStep>> wResult = new ServiceResult<List<SFCTaskStep>>();
		wResult.Result = new ArrayList<SFCTaskStep>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1);
			if (wEndTime.compareTo(wBaseTime) < 0) {
				wEndTime = Calendar.getInstance();
			}

			List<SFCTaskStep> wList = SFCLoginEventDAO.getInstance().SFC_SelectTaskStepList(wLoginUser, wStartTime,
					wEndTime, wOrderID, wPartID, wErrorCode);

			switch (SFCLoginType.getEnumType(wTagTypes)) {
			case StartWork:
				wResult.Result = wList.stream().filter(p -> p.IsStartWork == SFCLoginType.Default.getValue()
						|| p.IsStartWork == SFCLoginType.StopWork.getValue()).collect(Collectors.toList());
				break;
			case AfterWork:
				wResult.Result = wList.stream().filter(p -> p.IsStartWork == SFCLoginType.AfterWork.getValue()
						|| p.IsStartWork == SFCLoginType.StartWork.getValue()).collect(Collectors.toList());
				break;
			default:
				break;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_SaveValueList_V2(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList, int wOperateType) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			// ??????????????????????????????????????????
			String wMsg = SFC_CheckIsCanSubmit(wLoginUser, wSFCTaskIPT, wIPTValueList);
			if (StringUtils.isNotEmpty(wMsg)) {
				wResult.FaultCode = wMsg;
				return wResult;
			}

			// ??????????????????
			if (wOperateType == 1) {
				wIPTValueList.forEach(p -> {
					p.SubmitID = wLoginUser.ID;
					p.SubmitTime = Calendar.getInstance();
					p.Status = 1;
				});
			} else if (wOperateType == 2) {// ??????
				wIPTValueList.forEach(p -> {
					p.SubmitID = wLoginUser.ID;
					p.SubmitTime = Calendar.getInstance();
					p.Status = 2;
				});
			}

			IPTServiceImpl.getInstance().IPT_SaveIPTValue(wLoginUser, wIPTValueList, wSFCTaskIPT.ID, 0);

			if (wOperateType == 1) {
				return wResult;
			}

			switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
			case SelfCheck:
				// ?????????
				wMsg = SFC_SubmitSelfCheckValue(wLoginUser, wSFCTaskIPT, wIPTValueList);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case MutualCheck:
				// ?????????
				wMsg = SFC_SubmitMutualCheckValue(wLoginUser, wSFCTaskIPT, wIPTValueList);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case SpecialCheck:
				// ?????????
				wMsg = SFC_SubmitSpecialCheckValue(wLoginUser, wSFCTaskIPT, wIPTValueList);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case PreCheck:
				// ?????????
				wMsg = SFC_SubmitPreCheckValue(wLoginUser, wSFCTaskIPT, wIPTValueList);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case Final:
				// ?????????
				wMsg = SFC_SubmitFinalCheckValue(wLoginUser, wSFCTaskIPT, wIPTValueList);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case OutPlant:
				// ????????????
				wMsg = SFC_SubmitOutPlantCheckValue(wLoginUser, wSFCTaskIPT, wIPTValueList);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????(???????????????)
	 */
	private String SFC_CheckIsCanSubmit(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, List<IPTValue> wIPTValueList) {
		String wMsg = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wSFCTaskIPT.TaskType != SFCTaskType.SelfCheck.getValue()
					&& wSFCTaskIPT.TaskType != SFCTaskType.MutualCheck.getValue()) {
				return wMsg;
			}

			if (wIPTValueList == null) {
				return wMsg;
			}

			// ?????????????????????
			APSTaskStep wAPSTaskStep = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
			switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
			case SelfCheck:
				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wAPSTaskStep.ID, -1, -1).List(SFCTaskStep.class);
				wSFCTaskStepList = new ArrayList<SFCTaskStep>(wSFCTaskStepList.stream()
						.collect(Collectors.toMap(SFCTaskStep::getOperatorID, account -> account, (k1, k2) -> k2))
						.values());
				// ??????????????????
//				if (wSFCTaskStepList.stream().anyMatch(p -> p.OperatorID == wLoginUser.ID && p.IsStartWork == 0)) {
//					wMsg = StringUtils.Format("????????????{0}?????????????????????{1}??????????????????????????????!", wLoginUser.Name, wAPSTaskStep.StepName);
//					return wMsg;
//				}
//				if (!wSFCTaskStepList.stream().anyMatch(p -> p.IsStartWork == 1)) {
//					wMsg = StringUtils.Format("????????????{0}?????????????????????{1}??????????????????????????????!", wLoginUser.Name, wAPSTaskStep.StepName);
//					return wMsg;
//				}
				// ??????????????????
//				List<SFCTaskIPT> wMutualTaskIPT = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
//						wAPSTaskStep.ID, SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, -1, null, null,
//						wErrorCode);
				// ?????????????????????????????????
//				if (wAPSTaskStep.OperatorList.size() > 1 && wMutualTaskIPT != null && wMutualTaskIPT.size() > 0
//						&& wMutualTaskIPT.stream().anyMatch(p -> p.OperatorList != null && p.OperatorList.size() > 0
//								&& p.OperatorList.contains(wLoginUser.ID))) {
//					wMsg = StringUtils.Format("????????????{0}?????????????????????{1}??????????????????????????????!", wLoginUser.Name, wAPSTaskStep.StepName);
//					return wMsg;
//				}
				// ????????????????????????????????????????????????????????????????????????????????????
//				if (wAPSTaskStep.OperatorList.size() > 1 && !wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)
//						&& wMutualTaskIPT.size() > 0 && !wMutualTaskIPT.get(0).OperatorList.contains(wLoginUser.ID)
//						&& wAPSTaskStep.OperatorList.size() - wSFCTaskIPT.OperatorList.size() <= 1) {
//					wMsg = StringUtils.Format("????????????????????????????????????{0}???????????????????????????????????????????????????!", wAPSTaskStep.StepName);
//					return wMsg;
//				}
				break;
			case MutualCheck:
				// ??????????????????
				List<SFCTaskIPT> wSelfIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wAPSTaskStep.ID,
						SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, -1, null, null, wErrorCode);
				// ???????????????
//				if (wSelfIPTList.size() > 0 && wSelfIPTList.get(0).OperatorList.contains(wLoginUser.ID)) {
//					wMsg = StringUtils.Format("????????????{0}????????????{1}???????????????????????????????????????????????????!", wLoginUser.Name,
//							wAPSTaskStep.StepName);
//					return wMsg;
//				}

				if (wSelfIPTList == null || wSelfIPTList.size() <= 0) {
					return wMsg;
				}

				// ?????????????????????????????????????????????
				List<IPTValue> wVList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1, wSelfIPTList.get(0).ID,
						-1, -1, -1, wErrorCode);

				for (IPTValue wIPTValue : wIPTValueList) {
					if (wVList.stream().anyMatch(p -> p.IPTItemID == wIPTValue.IPTItemID)) {
						List<IPTValue> wDoneList = wVList.stream().filter(p -> p.IPTItemID == wIPTValue.IPTItemID)
								.collect(Collectors.toList());
						IPTValue wMax = wDoneList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get();
						if (wMax.SubmitID == wLoginUser.ID) {
							IPTItem wItem = IPTItemDAO.getInstance().SelectByID(wLoginUser, (int) wMax.IPTItemID,
									wErrorCode);
							wMsg = StringUtils.Format("????????????{0}????????????{1}???-???{2}????????????????????????????????????!", wLoginUser.Name,
									wAPSTaskStep.StepName, wItem.Text);
							return wMsg;
						}
					}
				}

//				if (wIPTValueList.stream().anyMatch(p -> wVList.stream()
//						.anyMatch(q -> q.IPTItemID == p.IPTItemID && q.SubmitID == wLoginUser.ID))) {
//					IPTValue wValue = wIPTValueList.stream()
//							.filter(p -> wVList.stream()
//									.anyMatch(q -> q.IPTItemID == p.IPTItemID && q.SubmitID == wLoginUser.ID))
//							.findFirst().get();
//
//					IPTItem wItem = IPTItemDAO.getInstance().SelectByID(wLoginUser, (int) wValue.IPTItemID, wErrorCode);
//					wMsg = StringUtils.Format("????????????{0}????????????{1}???-???{2}????????????????????????????????????!", wLoginUser.Name,
//							wAPSTaskStep.StepName, wItem.Text);
//					return wMsg;
//				}
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wMsg;
	}

	/**
	 * ??????????????????
	 */
	private String SFC_SubmitOutPlantCheckValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList) {
		String wMsg = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ?????????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ???????????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ???????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ?????????????????????????????????
			wSFCTaskIPT.Status = SFCTaskIPTDAO.getInstance().QueryTaskIPTStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();
				// ?????????????????????
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wTaskStep != null && wTaskStep.ID > 0) {
					wTaskStep.EndTime = Calendar.getInstance();
					wTaskStep.Status = 5;
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
				}
				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.EndTime = Calendar.getInstance();
						wSFCTaskStep.EditTime = Calendar.getInstance();
						wSFCTaskStep.IsStartWork = 2;
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}
			// ??????????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wMsg;
	}

	/**
	 * ???????????????
	 */
	private String SFC_SubmitFinalCheckValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList) {
		String wMsg = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ????????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ??????????????????????????????
			wSFCTaskIPT.Status = SFCTaskIPTDAO.getInstance().QueryTaskIPTStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();
				// ?????????????????????
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wTaskStep != null && wTaskStep.ID > 0) {
					wTaskStep.EndTime = Calendar.getInstance();
					wTaskStep.Status = 5;
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
				}
				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.EndTime = Calendar.getInstance();
						wSFCTaskStep.EditTime = Calendar.getInstance();
						wSFCTaskStep.IsStartWork = 2;
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ????????????????????????
			CreateConfirmForm(wLoginUser, wSFCTaskIPT, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wMsg;
	}

	/**
	 * ?????????????????????
	 */
	private void CreateConfirmForm(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, OutResult<Integer> wErrorCode) {
		try {
			boolean wResult = SFCTaskIPTDAO.getInstance().JudgeFinalCheckIsOK(wLoginUser, wSFCTaskIPT.StationID,
					wSFCTaskIPT.OrderID, wErrorCode);
			if (!wResult) {
				return;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1, 0, 0, 0);

			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTaskIPT.OrderID)
					.Info(OMSOrder.class);

			SFCOrderForm wForm = new SFCOrderForm(0, wSFCTaskIPT.OrderID, wOrder.OrderNo, wSFCTaskIPT.PartNo,
					wLoginUser.ID, wLoginUser.Name, Calendar.getInstance(), 0, "", wBaseTime, 1,
					SFCOrderFormType.CompleteConfirm.getValue());
			SFCOrderFormDAO.getInstance().Update(wLoginUser, wForm, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????
	 */
	private String SFC_SubmitPreCheckValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList) {
		String wMsg = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ????????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ??????????????????????????????
			wSFCTaskIPT.Status = SFCTaskIPTDAO.getInstance().QueryTaskIPTStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();
				// ?????????????????????
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wTaskStep != null && wTaskStep.ID > 0) {
					wTaskStep.EndTime = Calendar.getInstance();
					wTaskStep.Status = 5;
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
				}
				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.EndTime = Calendar.getInstance();
						wSFCTaskStep.EditTime = Calendar.getInstance();
						wSFCTaskStep.IsStartWork = 2;
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ????????????????????????
			IPTServiceImpl.getInstance().IPT_TriggerYJProblem(wLoginUser, wSFCTaskIPT, wIPTValueList);
			// ???????????????????????????????????????(??????????????????????????????)
//			QMSConstants.mSFCTaskIPTList.add(wSFCTaskIPT);
			ExecutorService wES = Executors.newFixedThreadPool(1);
			wES.submit(() -> RSMServiceImpl.getInstance().RSM_AutoFinishTaskPart(wLoginUser, wSFCTaskIPT));
			wES.shutdown();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wMsg;
	}

	/**
	 * ???????????????
	 */
	private String SFC_SubmitSpecialCheckValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList) {
		String wMsg = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ??????????????????????????????
			wSFCTaskIPT.Status = SFCTaskIPTDAO.getInstance().QueryTaskIPTStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ????????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1, 0, 0, 0);
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();

				boolean wFlag = SFCTaskIPTDAO.getInstance().JudgeSpecialTaskIsFinished(wLoginUser, wSFCTaskIPT,
						wErrorCode);
				if (wFlag) {
					// ?????????????????????
					APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
					if (wTaskStep != null && wTaskStep.ID > 0) {
						wTaskStep.Status = 5;
						wTaskStep.EndTime = Calendar.getInstance();
						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
					}
					// ?????????????????????
					List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
							.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
					if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
						for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
							wSFCTaskStep.EndTime = Calendar.getInstance();
							wSFCTaskStep.EditTime = Calendar.getInstance();
							wSFCTaskStep.IsStartWork = 2;
							LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
						}
					}
				}
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);

			// ?????????????????????
			ExecutorService wES = Executors.newFixedThreadPool(1);
			wES.submit(() -> SFCTaskIPTDAO.getInstance().SFC_CloseRelaMessage(wLoginUser, wErrorCode));
			wES.shutdown();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wMsg;
	}

	/**
	 * ???????????????
	 */
	private String SFC_SubmitMutualCheckValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList) {
		String wMsg = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			int wTaskType = wSFCTaskIPT.TaskType;
			// ?????????????????????
			List<SFCTaskIPT> wSpecialTaskList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
					wSFCTaskIPT.TaskStepID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, null, -1, null, null,
					wErrorCode);
			if (wIPTValueList.stream().anyMatch(p -> p.Result == 1) && wSpecialTaskList.size() <= 0) {
				wSFCTaskIPT.TaskType = SFCTaskType.SpecialCheck.getValue();
				int wNewID = this.SFC_TriggerTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
				// ?????????????????????
				TriggerSpecialMessage(wLoginUser, wSFCTaskIPT, wNewID);
				// ????????????????????????????????????
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
//			if (wIPTValueList.stream().anyMatch(p -> p.Result == 2)) {
//				List<SFCTaskIPT> wSelfIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
//						wSFCTaskIPT.TaskStepID, SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, -1, null, null,
//						wErrorCode);
//				if (wSelfIPTList.size() > 0) {
//					wSelfIPTList.get(0).Status = 1;
//					SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSelfIPTList.get(0), wErrorCode);
//				}
//			}
			wSFCTaskIPT.TaskType = wTaskType;
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ??????????????????????????????
			wSFCTaskIPT.Status = SFCTaskIPTDAO.getInstance().QueryTaskIPTStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ?????????????????????
			if (SFCTaskIPTDAO.getInstance().JudgeMutualTaskIsFinished(wLoginUser, wSFCTaskIPT, wErrorCode)) {
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wTaskStep != null && wTaskStep.ID > 0) {
					wTaskStep.EndTime = Calendar.getInstance();
					wTaskStep.Status = 5;
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
				}
				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.EndTime = Calendar.getInstance();
						wSFCTaskStep.EditTime = Calendar.getInstance();
						wSFCTaskStep.IsStartWork = 2;
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}
			// ???????????????????????????1
			if (wSpecialTaskList.size() > 0 && wIPTValueList.stream().anyMatch(p -> p.Result == 1)) {
				wSpecialTaskList.get(0).Status = 1;
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSpecialTaskList.get(0), wErrorCode);
			}

			// ????????????????????????(????????????)
			for (IPTValue wValue : wIPTValueList) {
				if (wValue.Result == 1 || wValue.OrderID <= 0) {
					continue;
				}

				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wValue.OrderID)
						.Info(OMSOrder.class);
				if (wOrder != null && wOrder.ID > 0) {
					wOrder.ParentID = 0;
					LOCOAPSServiceImpl.getInstance().OMS_UpdateOrder(wLoginUser, wOrder);
				}
			}
			// ??????????????????????????????
			for (IPTValue wValue : wIPTValueList) {
				if (wValue.Result == 1) {
					continue;
				}

				IPTItem wItem = IPTItemDAO.getInstance().SelectByID(wLoginUser, (int) wValue.IPTItemID, wErrorCode);
				if (wItem.DisassemblyComponentsID > 0) {
					List<MSSPartRecord> wList = MSSPartRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1,
							wItem.DisassemblyComponents, "", wSFCTaskIPT.OrderID, -1, MSSOperateType.ChaiJie.getValue(),
							(int) wValue.IPTItemID, wErrorCode);
					if (wList.size() > 0) {
						MSSPartRecordDAO.getInstance().DeleteList(wLoginUser, wList, wErrorCode);
					}
				} else if (wItem.RepairPartsID > 0) {
					List<MSSPartRecord> wList = MSSPartRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1,
							wItem.RepairParts, "", wSFCTaskIPT.OrderID, -1, MSSOperateType.WeiXiu.getValue(),
							(int) wValue.IPTItemID, wErrorCode);
					if (wList.size() > 0) {
						MSSPartRecordDAO.getInstance().DeleteList(wLoginUser, wList, wErrorCode);
					}
				} else if (wItem.AssemblyPartsID > 0) {
					List<MSSPartRecord> wList = MSSPartRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1,
							wItem.AssemblyParts, "", wSFCTaskIPT.OrderID, -1, MSSOperateType.Zuzhuang.getValue(),
							(int) wValue.IPTItemID, wErrorCode);
					if (wList.size() > 0) {
						MSSPartRecordDAO.getInstance().DeleteList(wLoginUser, wList, wErrorCode);
					}
				}
			}
			// ?????????????????????
			for (IPTValue wValue : wIPTValueList) {
				if (wValue.Result == 1) {
					continue;
				}

				IPTItem wItem = IPTItemDAO.getInstance().SelectByID(wLoginUser, (int) wValue.IPTItemID, wErrorCode);
				if (wItem.SRDisassemblyComponentsID > 0) {
					List<MSSRepairRecord> wList = MSSRepairRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1,
							-1, -1, -1, wItem.SRDisassemblyComponents, wSFCTaskIPT.OrderID, -1,
							MSSOperateType.ChaiJie.getValue(), (int) wItem.ID, wErrorCode);
					if (wList.size() > 0) {
						MSSRepairRecordDAO.getInstance().DeleteList(wLoginUser, wList, wErrorCode);
					}
				} else if (wItem.SRRepairPartsID > 0) {
					List<MSSRepairRecord> wList = MSSRepairRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1,
							-1, -1, -1, wItem.SRRepairParts, wSFCTaskIPT.OrderID, -1, MSSOperateType.WeiXiu.getValue(),
							(int) wItem.ID, wErrorCode);
					if (wList.size() > 0) {
						MSSRepairRecordDAO.getInstance().DeleteList(wLoginUser, wList, wErrorCode);
					}
				} else if (wItem.SRAssemblyPartsID > 0) {
					List<MSSRepairRecord> wList = MSSRepairRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1,
							-1, -1, -1, wItem.SRAssemblyParts, wSFCTaskIPT.OrderID, -1,
							MSSOperateType.Zuzhuang.getValue(), (int) wItem.ID, wErrorCode);
					if (wList.size() > 0) {
						MSSRepairRecordDAO.getInstance().DeleteList(wLoginUser, wList, wErrorCode);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wMsg;
	}

	/**
	 * ??????????????????
	 */
	private synchronized void TriggerSpecialMessage(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, int wNewID) {
		try {
			// ??????????????????????????????????????????????????????
			List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
			BFCMessage wMessage = new BFCMessage();
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);

			// ?????????????????????
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<Integer> wIDList = SFCTaskIPTDAO.getInstance().SelectStepCheckerIDList(wLoginUser,
					wSFCTaskIPT.StationID, wSFCTaskIPT.TaskStepID, wErrorCode);

			wIDList = wIDList.stream().distinct().collect(Collectors.toList());

			// ?????????????????????
			ExecutorService wES = Executors.newFixedThreadPool(1);
			wES.submit(() -> UpdateSpecialTaskCheckerList(wNewID));
			wES.shutdown();

			List<Integer> wCheckerIDList = wIDList;
			if (wCheckerIDList != null && wCheckerIDList.size() > 0) {
				// ??????????????????????????????????????????
				for (Integer wCheckerID : wCheckerIDList) {
					if (wBFCMessageList.stream().anyMatch(p -> (int) p.ResponsorID == wCheckerID.intValue())) {
						continue;
					}
					wMessage = new BFCMessage();
					wMessage.Active = 0;
					wMessage.CompanyID = wLoginUser.CompanyID;
					wMessage.CreateTime = Calendar.getInstance();
					wMessage.EditTime = Calendar.getInstance();
					wMessage.ID = 0;
					wMessage.MessageID = wNewID;
					wMessage.Title = StringUtils.Format("?????? {0} {1}",
							new Object[] { wSFCTaskIPT.PartPointName, wSFCTaskIPT.PartNo });
					wMessage.MessageText = StringUtils.Format("???{0}??? {1}????????????????????????{2}???",
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
			// ?????????????????????
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????
	 */
	private void UpdateSpecialTaskCheckerList(int wNewID) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			SFCTaskIPT wTaskIPT = SFCTaskIPTDAO.getInstance().SelectByID(BaseDAO.SysAdmin, wNewID, wErrorCode);
			if (wTaskIPT == null || wTaskIPT.ID <= 0) {
				return;
			}

			String wCheckerList = SFCTaskIPTDAO.getInstance().GetCheckerList(BaseDAO.SysAdmin, wTaskIPT.TaskStepID,
					wTaskIPT.StationID, wErrorCode);
			if (StringUtils.isNotEmpty(wCheckerList)) {
				wTaskIPT.CheckerList = wCheckerList;
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(BaseDAO.SysAdmin, wTaskIPT, wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????
	 */
	private String SFC_SubmitSelfCheckValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList) {
		String wMsg = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			int wTaskType = wSFCTaskIPT.TaskType;
			// ?????????????????????
			List<SFCTaskIPT> wMuIPTList = null;
			if (wSFCTaskIPT.Type == 2) {
				wMuIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskIPT.TaskStepID,
						SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, 2, null, null, wErrorCode);
			} else {
				wMuIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskIPT.TaskStepID,
						SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, 1, null, null, wErrorCode);
			}
			if (wIPTValueList.stream().anyMatch(p -> p.Result == 1) && wMuIPTList.size() <= 0) {
				wSFCTaskIPT.TaskType = SFCTaskType.MutualCheck.getValue();
				int wNewID = this.SFC_TriggerTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
				// ?????????????????????
				TriggerMutualMessage(wLoginUser, wSFCTaskIPT, wNewID);
			} else if (wIPTValueList.stream().anyMatch(p -> p.Result == 1) && wMuIPTList.size() > 0) {
				wMuIPTList.get(0).Status = 1;
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wMuIPTList.get(0), wErrorCode);
			}

			if (wMuIPTList.size() > 0) {
				// ?????????????????????
				TriggerMutualMessage(wLoginUser, wSFCTaskIPT, wMuIPTList.get(0).ID);
			}

			wSFCTaskIPT.TaskType = wTaskType;
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ?????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1);
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ?????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ???????????????????????????
			wSFCTaskIPT.Status = SFCTaskIPTDAO.getInstance().QueryTaskIPTStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ?????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();

				// ???????????????????????????????????????
				SFCTaskIPTDAO.getInstance().CloseAllDispatchMessage(wLoginUser, wSFCTaskIPT.TaskStepID, wErrorCode);
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ?????????????????????
			SFCTaskIPTDAO.getInstance().DisMutualMessage(wLoginUser, wSFCTaskIPT.ID, wErrorCode);
			// ???????????????????????????ID
			SFCTaskIPTDAO.getInstance().BindingParentOrder(wLoginUser, wSFCTaskIPT, wIPTValueList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wMsg;
	}

	/**
	 * ??????????????????
	 */
	private void TriggerMutualMessage(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, int wNewID) {
		try {
			// ?????????????????????
			List<Integer> wUserIDList = new ArrayList<Integer>();
			APSTaskStep wAPSTaskStep = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
			List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
			if (wAPSTaskStep.OperatorList.size() <= 1) {
				int wDepartmentID = QMSConstants.GetBMSEmployee(wSFCTaskStepList.get(0).MonitorID).DepartmentID;
				wUserIDList = QMSConstants.GetBMSEmployeeList().values().stream()
						.filter(p -> p.DepartmentID == wDepartmentID && p.Active == 1 && p.ID != wLoginUser.ID
								&& QMSConstants.GetBMSPosition(p.Position).DutyID == 1)
						.map(p -> p.ID).distinct().collect(Collectors.toList());
			} else {
//				wUserIDList = wAPSTaskStep.OperatorList.stream()
//						.filter(p -> !wSFCTaskIPT.OperatorList.stream().anyMatch(q -> q.intValue() == p.intValue())
//								&& p.intValue() != wLoginUser.ID)
//						.collect(Collectors.toList());
				// ?????????????????????????????????
				wUserIDList = wSFCTaskStepList.stream().map(p -> p.OperatorID).distinct().collect(Collectors.toList());
				wUserIDList.removeIf(p -> p.intValue() == wLoginUser.ID);
			}
			// ?????????????????????
			// ???????????????????????????
			List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
			BFCMessage wMessage = null;
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);
			// ???????????????????????????????????????????????????????????????
			if (wUserIDList.size() <= 0) {
				wUserIDList = new ArrayList<Integer>(Arrays.asList(wLoginUser.ID));
			}
			for (Integer wPersonID : wUserIDList) {

				// ??????????????????????????????
				if (SFCTaskIPTDAO.getInstance().JudgeMessageIsExist(wLoginUser, wPersonID, wNewID)) {
					continue;
				}

				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wNewID;
				wMessage.Title = StringUtils.Format("?????? {0} {1}",
						new Object[] { wSFCTaskIPT.PartPointName, wSFCTaskIPT.PartNo });
				wMessage.MessageText = StringUtils.Format("???{0}??? {1}????????????????????????{2}???", new Object[] {
						BPMEventModule.MutualCheck.getLable(), wLoginUser.Name, wSFCTaskIPT.PartPointName });
				wMessage.ModuleID = BPMEventModule.MutualCheck.getValue();
				wMessage.ResponsorID = wPersonID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = wSFCTaskIPT.PartID;
				wMessage.Type = BFCMessageType.Task.getValue();
				wBFCMessageList.add(wMessage);
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public synchronized ServiceResult<Integer> SFC_Clock_V2(BMSEmployee wLoginUser, int wType,
			List<SFCTaskStep> wDataList, int wAPPEventID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			if (wDataList == null || wDataList.size() <= 0) {
				return wResult;
			}

			// ?????????????????????????????????
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

			ServiceResult<IPTStandard> wRst = null;
			for (SFCTaskStep wSFCTaskStep : wDataList) {
				if (wSFCTaskStep.Type == SFCTaskStepType.Step.getValue()) {
					wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.QTXJ, -1, -1,
							-1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
							wSFCTaskStep.ProductID, wErrorCode);
					if (wRst == null || wRst.Result == null || wRst.Result.ID <= 0) {
						wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.PreCheck,
								-1, -1, -1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
								wSFCTaskStep.ProductID, wErrorCode);
						if (wRst == null || wRst.Result == null || wRst.Result.ID <= 0) {
							wResult.FaultCode += StringUtils.Format("???{0}?????????????????????????????????????????????????????????????????????!",
									wSFCTaskStep.PartPointName);
							return wResult;
						}
					}
				} else if (wSFCTaskStep.Type == SFCTaskStepType.Quality.getValue()) {
					wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.Quality, -1, -1,
							-1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
							wSFCTaskStep.ProductID, wErrorCode);
					if (wRst == null || wRst.Result == null || wRst.Result.ID <= 0) {
						wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.OutCheck,
								wSFCTaskStep.CustomerID, -1, -1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID,
								wSFCTaskStep.StepID, -1, wSFCTaskStep.ProductID, wErrorCode);
						if (wRst == null || wRst.Result == null || wRst.Result.ID <= 0) {
							wResult.FaultCode += StringUtils.Format("???{0}?????????????????????????????????????????????????????????????????????!",
									wSFCTaskStep.PartPointName);
							return wResult;
						}
					}
				}
			}

			if (wDataList == null || wDataList.size() <= 0)
				return wResult;
			int wShiftID = QMSUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance());

			String wMsg = "";
			for (SFCTaskStep wSFCTaskStep : wDataList) {

				wMsg = this.SFC_ClockTask(wLoginUser, wType, wErrorCode, wShiftID, wSFCTaskStep, wHolidayList, wMinHour,
						wMaxHour);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode += wMsg;
					return wResult;
				}

				// ????????????
				if (wSFCTaskStep.Type == SFCTaskStepType.Step.getValue()) {
					// ??????
					wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.QTXJ, -1, -1,
							-1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
							wSFCTaskStep.ProductID, wErrorCode);
					if (wRst != null && wRst.Result != null && wRst.Result.ID > 0) {
						int wSFCTaskType = SFCTaskType.SelfCheck.getValue();

						int wIPTID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
								wSFCTaskType, wRst.Result.ID, SFCTaskStepType.Step.getValue(), 0);
						// ??????????????????????????????????????????????????????????????????????????????????????????
						if (wIPTID > 0) {
							SFC_SendMessageToClockerBySelfCheck(wLoginUser, wIPTID, wSFCTaskStep);

							// ??????????????????????????????????????????????????????????????????
							ExecutorService wES = Executors.newFixedThreadPool(1);
							wES.submit(() -> HandleStepTask(wSFCTaskStep));
							wES.shutdown();
						}
					}
					// ??????
					wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.PreCheck, -1,
							-1, -1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
							wSFCTaskStep.ProductID, wErrorCode);
					if (wRst != null && wRst.Result != null && wRst.Result.ID > 0) {
						int wSFCTaskType = SFCTaskType.PreCheck.getValue();

						// ??????????????????????????????
						int wPeriodChangeStandard = 0;
						wPeriodChangeStandard = AsignPeriodChangeStandard(wLoginUser, wErrorCode, wSFCTaskStep,
								wPeriodChangeStandard);

						int wNewID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
								wSFCTaskType, wRst.Result.ID, SFCTaskStepType.Step.getValue(), wPeriodChangeStandard);

						// ?????????????????????????????????????????????????????????????????????????????????????????????
						SFC_SendMessageToMonitorAndOperatorWhenPreTaskCreated(wLoginUser, wNewID, wSFCTaskStep, "??????",
								BPMEventModule.PreCheck);
					}
				} else if (wSFCTaskStep.Type == SFCTaskStepType.Question.getValue()) {
					int wSFCTaskType = SFCTaskType.SelfCheck.getValue();
					int wIPTID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
							wSFCTaskType, 0, SFCTaskStepType.Question.getValue(), 0);
					if (wIPTID > 0) {
						// ????????????
//						SFC_SendMessageToMonitorAndOperatorWhenPreTaskCreated(wLoginUser, wIPTID, wSFCTaskStep, "???????????????",
//								BPMEventModule.PreProblemHandle);
					}
				} else if (wSFCTaskStep.Type == SFCTaskStepType.Quality.getValue()) {
					FPCPart wPart = QMSConstants.GetFPCPart(wSFCTaskStep.PartID);
					if (wPart != null && wPart.ID > 0) {
						if (wPart.PartType == FPCPartTypes.OutFactory.getValue()) {
							wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0,
									IPTMode.OutCheck, wSFCTaskStep.CustomerID, -1, -1, -1, -1, wSFCTaskStep.LineID,
									wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1, wSFCTaskStep.ProductID, wErrorCode);
							// ???????????????
							int wSFCTaskType = SFCTaskType.OutPlant.getValue();
							int wNewID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
									wSFCTaskType, wRst.Result.ID, SFCTaskStepType.Quality.getValue(), 0);

							SFC_SendMessageToMonitorAndOperatorWhenPreTaskCreated(wLoginUser, wNewID, wSFCTaskStep,
									"?????????", BPMEventModule.OutCheck);
						} else if (wPart.PartType == FPCPartTypes.QTFinally.getValue()) {
							wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.Quality,
									-1, -1, -1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID,
									-1, wSFCTaskStep.ProductID, wErrorCode);
							// ????????????
							int wSFCTaskType = SFCTaskType.Final.getValue();
							int wNewID = SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep,
									wSFCTaskType, wRst.Result.ID, SFCTaskStepType.Quality.getValue(), 0);

							SFC_SendMessageToMonitorAndOperatorWhenPreTaskCreated(wLoginUser, wNewID, wSFCTaskStep,
									"??????", BPMEventModule.FinalCheck);
						}
					}
				}
			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????????????????????????????????????????????
	 */
	private void HandleStepTask(SFCTaskStep wSFCTaskStep) {
//		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
					.SFC_QueryTaskStepList(BaseDAO.SysAdmin, wSFCTaskStep.TaskStepID, -1, -1).List(SFCTaskStep.class);
			for (SFCTaskStep wItem : wSFCTaskStepList) {

				// ??????????????????????????????
//				SFCLoginEventDAO.getInstance().SFC_CloseTaskStepMessage(BaseDAO.SysAdmin, wItem, wErrorCode);

				wItem.IsStartWork = 1;
				LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(BaseDAO.SysAdmin, wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<SFCLoginEventPart>> SFC_QueryEmployeeAllPart_V2(BMSEmployee wLoginUser) {
		ServiceResult<List<SFCLoginEventPart>> wResult = new ServiceResult<List<SFCLoginEventPart>>();
		wResult.Result = new ArrayList<SFCLoginEventPart>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ?????????????????????
			Calendar wTodaySTime = Calendar.getInstance();
			wTodaySTime.set(Calendar.HOUR_OF_DAY, 0);
			wTodaySTime.set(Calendar.MINUTE, 0);
			wTodaySTime.set(Calendar.SECOND, 0);

			Calendar wTodayETime = Calendar.getInstance();
			wTodayETime.set(Calendar.HOUR_OF_DAY, 23);
			wTodayETime.set(Calendar.MINUTE, 59);
			wTodayETime.set(Calendar.SECOND, 59);
			// ??????????????????????????????????????????(????????????)
			List<SFCTaskStep> wTaskStepNoProblemList = IPTPreCheckProblemDAO.getInstance()
					.SelectTaskStepListNoProblem(wLoginUser, wTodaySTime, wTodayETime, -1, -1, wErrorCode);
			wTaskStepNoProblemList = wTaskStepNoProblemList.stream().filter(p -> p.Active == 1)
					.collect(Collectors.toList());
			// ??????????????????????????????????????????(?????????)
			List<SFCTaskStep> wTaskStepWithProblemList = IPTPreCheckProblemDAO.getInstance()
					.SelectTaskStepListWithProblem(wLoginUser, wTodaySTime, wTodayETime, -1, -1, wErrorCode);
			wTaskStepWithProblemList = wTaskStepWithProblemList.stream().filter(p -> p.Active == 1)
					.collect(Collectors.toList());
			// ?????????????????????
			List<SFCTaskStep> wList = new ArrayList<SFCTaskStep>();
			wList.addAll(wTaskStepNoProblemList);
			wList.addAll(wTaskStepWithProblemList);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}
			// ?????????????????????????????????
			List<Integer> wOrderIDList = wList.stream().map(p -> p.OrderID).distinct().collect(Collectors.toList());
			String wOrderIDs = StringUtils.Join(",", wOrderIDList);
			List<FPCRoutePart> wRoutePartList = IPTPreCheckProblemDAO.getInstance()
					.SelectRoutePartListByOrderIDs(wLoginUser, wOrderIDs, wErrorCode);
			// ?????????????????????
			List<OMSOrder> wOrderList = IPTPreCheckProblemDAO.getInstance().SelectOrderListByOrderIDs(wLoginUser,
					wOrderIDs, wErrorCode);
			// ????????????????????????
			List<RSMTurnOrderTask> wTurnOrderTaskList = IPTPreCheckProblemDAO.getInstance()
					.SelectTurnOrderTaskListByOrderIDs(wLoginUser, wOrderIDs, wErrorCode);
			// ????????????????????????
			List<SFCTaskStep> wNRList = wList.stream()
					.collect(Collectors.collectingAndThen(Collectors.toCollection(
							() -> new TreeSet<>(Comparator.comparing(o -> o.getOrderID() + ";" + o.getPartID()))),
							ArrayList::new));
			// ?????????????????????
			List<LFSWorkAreaStation> wAreaStationList = QMSConstants.GetLFSWorkAreaStationList().values().stream()
					.filter(p -> p.Active == 1).collect(Collectors.toList());
			// ?????????????????????
			for (SFCTaskStep wSFCTaskStep : wNRList) {
				SFCLoginEventPart wSFCLoginEventPart = new SFCLoginEventPart();

				if (QMSConstants.GetFPCPart(wSFCTaskStep.PartID).PartType == 3
						|| QMSConstants.GetFPCPart(wSFCTaskStep.PartID).PartType == 4) {
					wSFCLoginEventPart.IsTurnOrder = 1;
				} else {
					// ?????????????????????????????????
					OMSOrder wOMSOrder = wOrderList.stream().filter(p -> p.ID == wSFCTaskStep.OrderID).findFirst()
							.get();

					if (!wRoutePartList.stream()
							.anyMatch(p -> p.RouteID == wOMSOrder.RouteID && p.PartID == wSFCTaskStep.PartID)) {
						continue;
					}

					FPCRoutePart wRoutePart = wRoutePartList.stream()
							.filter(p -> p.RouteID == wOMSOrder.RouteID && p.PartID == wSFCTaskStep.PartID).findFirst()
							.get();
					List<FPCRoutePart> wPreRoutePartList = wRoutePartList.stream()
							.filter(p -> p.RouteID == wOMSOrder.RouteID
									&& ((p.PartID == wRoutePart.PrevPartID) || (p.NextPartIDMap != null
											&& p.NextPartIDMap.containsKey(String.valueOf(wRoutePart.PartID)))))
							.collect(Collectors.toList());
					if (wPreRoutePartList.size() > 0 && wPreRoutePartList.stream()
							.allMatch(p -> (p.ChangeControl == 2 || p.ChangeControl == 3)
									|| wTurnOrderTaskList.stream().anyMatch(q -> q.OrderID == wSFCTaskStep.OrderID
											&& q.Status == 2 && q.ApplyStationID == p.PartID))) {
						wSFCLoginEventPart.IsTurnOrder = 1;
					} else if (wPreRoutePartList.size() <= 0) {
						wSFCLoginEventPart.IsTurnOrder = 2;
					} else {
						wSFCLoginEventPart.IsTurnOrder = 0;
					}

					if (wPreRoutePartList.stream().allMatch(p -> p.ChangeControl == 2 || p.ChangeControl == 3)) {
						wSFCLoginEventPart.IsTurnOrder = 1;
					}

					// ?????????????????????????????????
					if (wSFCLoginEventPart.IsTurnOrder != 1) {
						boolean wFlag = true;
						for (FPCRoutePart wFPCRoutePart : wPreRoutePartList) {
							// ?????????????????????????????????
							if (wTurnOrderTaskList.stream().anyMatch(q -> q.OrderID == wSFCTaskStep.OrderID
									&& q.Status == 2 && q.ApplyStationID == wFPCRoutePart.PartID)) {
								continue;
							}
							// ???????????????????????????
							boolean wPLag = SFCLetPassBPMDAO.getInstance().JudgeIsLetGo(wLoginUser, wSFCTaskStep,
									wFPCRoutePart.PartID, wErrorCode);
							if (!wPLag) {
								wFlag = false;
							}
						}
						if (wFlag) {
							wSFCLoginEventPart.IsTurnOrder = 1;
						}
					}

					// ????????????????????????????????????????????????
					if (wSFCLoginEventPart.IsTurnOrder == 1) {
						String wCheckResult = RSMTurnOrderTaskDAO.getInstance().CheckLetGo(wLoginUser,
								wSFCTaskStep.OrderID, wSFCTaskStep.PartID, wErrorCode);
						if (StringUtils.isNotEmpty(wCheckResult)) {
							wSFCLoginEventPart.IsTurnOrder = 3;
							wSFCLoginEventPart.Tip = wCheckResult;
						}
					}
				}

				// ?????????????????????
				wSFCLoginEventPart.ClockSize = (int) wList.stream().filter(p -> p.OrderID == wSFCTaskStep.OrderID
						&& p.PartID == wSFCTaskStep.PartID && (p.IsStartWork == 1 || p.IsStartWork == 2)).count();
				wSFCLoginEventPart.LineName = wSFCTaskStep.LineName;
				wSFCLoginEventPart.OrderID = wSFCTaskStep.OrderID;
				wSFCLoginEventPart.OrderNum = wSFCTaskStep.OrderNum;
				wSFCLoginEventPart.PartID = wSFCTaskStep.PartID;
				wSFCLoginEventPart.PartName = wSFCTaskStep.PartName;
				wSFCLoginEventPart.PartNo = wSFCTaskStep.PartNo;
				wSFCLoginEventPart.StepSize = (int) wList.stream()
						.filter(p -> p.OrderID == wSFCTaskStep.OrderID && p.PartID == wSFCTaskStep.PartID).count();
				if (wAreaStationList.stream().anyMatch(p -> p.StationID == wSFCLoginEventPart.PartID)) {
					wSFCLoginEventPart.OrderNum = wAreaStationList.stream()
							.filter(p -> p.StationID == wSFCLoginEventPart.PartID).findFirst().get().OrderNum;
				}
				// ?????????????????????
				wResult.Result.add(wSFCLoginEventPart);
			}

			// ?????????
			if (wResult.Result.size() > 0) {
				wResult.Result.sort(Comparator.comparing(SFCLoginEventPart::getPartNo)
						.thenComparing(SFCLoginEventPart::getOrderNum));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskStep>> SFC_QueryClockEmployeeAllNew_V2(BMSEmployee wLoginUser, int wOrderID,
			int wPartID, int wTagTypes) {
		ServiceResult<List<SFCTaskStep>> wResult = new ServiceResult<List<SFCTaskStep>>();
		wResult.Result = new ArrayList<SFCTaskStep>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ?????????????????????
			Calendar wTodaySTime = Calendar.getInstance();
			wTodaySTime.set(Calendar.HOUR_OF_DAY, 0);
			wTodaySTime.set(Calendar.MINUTE, 0);
			wTodaySTime.set(Calendar.SECOND, 0);

			Calendar wTodayETime = Calendar.getInstance();
			wTodayETime.set(Calendar.HOUR_OF_DAY, 23);
			wTodayETime.set(Calendar.MINUTE, 59);
			wTodayETime.set(Calendar.SECOND, 59);

			List<SFCTaskStep> wList = IPTPreCheckProblemDAO.getInstance().SelectTaskStepListNoProblem(wLoginUser,
					wTodaySTime, wTodayETime, wOrderID, wPartID, wErrorCode);
			wList.addAll(IPTPreCheckProblemDAO.getInstance().SelectTaskStepListWithProblem(wLoginUser, wTodaySTime,
					wTodayETime, wOrderID, wPartID, wErrorCode));

			switch (SFCLoginType.getEnumType(wTagTypes)) {
			case StartWork:
				wResult.Result = wList.stream().filter(p -> p.IsStartWork == SFCLoginType.Default.getValue()
						|| p.IsStartWork == SFCLoginType.StopWork.getValue()).collect(Collectors.toList());
				break;
			case AfterWork:
				wResult.Result = wList.stream().filter(p -> p.IsStartWork == SFCLoginType.AfterWork.getValue()
						|| p.IsStartWork == SFCLoginType.StartWork.getValue()).collect(Collectors.toList());
				break;
			default:
				break;
			}

			// ????????????????????????
			wResult.Result = wResult.Result.stream().filter(p -> p.Active == 1).collect(Collectors.toList());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FPCCommonFile>> SFC_QueryCommonFileList(BMSEmployee wLoginUser, int wID, String wCode,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<FPCCommonFile>> wResult = new ServiceResult<List<FPCCommonFile>>();
		wResult.Result = new ArrayList<FPCCommonFile>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = FPCCommonFileDAO.getInstance().SelectList(wLoginUser, wID, wCode, wStartTime, wEndTime,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> SFC_ExportNewStandard(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wStepID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());

			String wFileName = StringUtils.Format("??????????????????{0}.xls", wCurTime);
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

			List<IPTExport> wDataList = new ArrayList<IPTExport>();
			IPTExport wData = GetData();
			wDataList.add(wData);

			IPTExport wCloneData = CloneTool.Clone(wData, IPTExport.class);
			wCloneData.StepName = "?????????";
			wDataList.add(wCloneData);
			ExcelUtil.IPT_WriteStepItems(wDataList, wFileOutputStream);

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
	 * ?????????????????????
	 */
	private IPTExport GetData() {
		IPTExport wResult = new IPTExport();
		try {
			wResult.Approval = "??????";
			wResult.ApprovalDate = "2021/1/13";
			wResult.Audit = "??????";
			wResult.AuditDate = "2021/1/13";
			wResult.Code = "HXD3C(C6)-04-77-067";
			wResult.CraftFile = "HXD3C(C6)-02-72-109 ????????????????????????";
			wResult.JointlySign = "??????";
			wResult.JointlySignDate = "2021/1/13";
			wResult.LineName = "C6";
			wResult.Maker = "??????";
			wResult.MakerDate = "2021/1/13";
			wResult.MaterialPrepare = "?????????Sheet??????";
			wResult.Name = "???????????????????????????";
			wResult.PartName = "?????????";
			wResult.PartNo = "0988";
			wResult.PersonNumber = "5";
			wResult.ProductNo = "HXD3C";
			wResult.Requirement = "??????????????????????????????????????????";
			wResult.StepName = "???????????????????????????";
			wResult.StepNature = "??????";
			wResult.Version = "A";
			wResult.WorkTime = "772min";
			wResult.IPTToolList = GetIPTToolList();
			wResult.IPTItemExportList = GEtIPTItemExportList();
			wResult.APSBOMItemList = GetAPSBOMItemList();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private List<APSBOMItem> GetAPSBOMItemList() {
		List<APSBOMItem> wResult = new ArrayList<APSBOMItem>();
		try {
			APSBOMItem wAPSBOMItem = new APSBOMItem();
			wAPSBOMItem.PartID = 1;
			wAPSBOMItem.PartPointName = "?????????????????????";
			wAPSBOMItem.MaterialName = "???????????????";
			wAPSBOMItem.MaterialNo = "M000001066922";
			wAPSBOMItem.Number = 8.0;
			wAPSBOMItem.UnitText = "PC";
			wAPSBOMItem.ReplaceType = 1;
			wResult.add(wAPSBOMItem);

			wAPSBOMItem = new APSBOMItem();
			wAPSBOMItem.PartID = 1;
			wAPSBOMItem.PartPointName = "?????????????????????";
			wAPSBOMItem.MaterialName = "???????????????";
			wAPSBOMItem.MaterialNo = "M000001066922";
			wAPSBOMItem.Number = 8.0;
			wAPSBOMItem.UnitText = "PC";
			wAPSBOMItem.ReplaceType = 1;
			wResult.add(wAPSBOMItem);

			wAPSBOMItem = new APSBOMItem();
			wAPSBOMItem.PartID = 1;
			wAPSBOMItem.PartPointName = "?????????????????????";
			wAPSBOMItem.MaterialName = "???????????????";
			wAPSBOMItem.MaterialNo = "M000001066922";
			wAPSBOMItem.Number = 8.0;
			wAPSBOMItem.UnitText = "PC";
			wAPSBOMItem.ReplaceType = 1;
			wResult.add(wAPSBOMItem);

			wAPSBOMItem = new APSBOMItem();
			wAPSBOMItem.PartID = 1;
			wAPSBOMItem.PartPointName = "?????????????????????";
			wAPSBOMItem.MaterialName = "???????????????";
			wAPSBOMItem.MaterialNo = "M000001066922";
			wAPSBOMItem.Number = 8.0;
			wAPSBOMItem.UnitText = "PC";
			wAPSBOMItem.ReplaceType = 1;
			wResult.add(wAPSBOMItem);

			wAPSBOMItem = new APSBOMItem();
			wAPSBOMItem.PartID = 1;
			wAPSBOMItem.PartPointName = "?????????????????????";
			wAPSBOMItem.MaterialName = "???????????????";
			wAPSBOMItem.MaterialNo = "M000001066922";
			wAPSBOMItem.Number = 8.0;
			wAPSBOMItem.UnitText = "PC";
			wAPSBOMItem.ReplaceType = 1;
			wResult.add(wAPSBOMItem);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	private List<IPTItemExport> GEtIPTItemExportList() {
		List<IPTItemExport> wResult = new ArrayList<IPTItemExport>();
		try {
			IPTItemExport wIPTItemExport = new IPTItemExport();
			wIPTItemExport.Project = "?????????????????????";
			wIPTItemExport.SerialNumber = "010";
			wIPTItemExport.WorkOrder = "??????????????????";
			wIPTItemExport.WorkContent = "??????????????????????????????????????????????????????????????????????????????????????????";
			wIPTItemExport.Standard = "a.????????????????????????????????????b.????????????????????????????????????5S??????????????????";
			wIPTItemExport.Legend = "C:\\Users\\Shris\\Desktop\\1.png;C:\\Users\\Shris\\Desktop\\2.png;C:\\Users\\Shris\\Desktop\\3.png";
			wIPTItemExport.WorkTime = "23";
			wIPTItemExport.SelfResult = "??????";
			wIPTItemExport.SelftDate = "2021/1/13";
			wIPTItemExport.MutualResult = "?????????";
			wIPTItemExport.MutualDate = "2021/1/13";
			wIPTItemExport.SpecialResult = "??????";
			wIPTItemExport.SpecialDate = "2021/1/13";
			wResult.add(wIPTItemExport);

			wIPTItemExport = new IPTItemExport();
			wIPTItemExport.Project = "?????????????????????";
			wIPTItemExport.SerialNumber = "020";
			wIPTItemExport.WorkOrder = "??????????????????";
			wIPTItemExport.WorkContent = "??????????????????????????????????????????????????????????????????????????????????????????";
			wIPTItemExport.Standard = "a.????????????????????????????????????b.????????????????????????????????????5S??????????????????";
			wIPTItemExport.Legend = "";
			wIPTItemExport.WorkTime = "23";
			wIPTItemExport.SelfResult = "??????";
			wIPTItemExport.SelftDate = "2021/1/13";
			wIPTItemExport.MutualResult = "?????????";
			wIPTItemExport.MutualDate = "2021/1/13";
			wIPTItemExport.SpecialResult = "??????";
			wIPTItemExport.SpecialDate = "2021/1/13";
			wResult.add(wIPTItemExport);

			wIPTItemExport = new IPTItemExport();
			wIPTItemExport.Project = "?????????????????????";
			wIPTItemExport.SerialNumber = "030";
			wIPTItemExport.WorkOrder = "??????????????????";
			wIPTItemExport.WorkContent = "??????????????????????????????????????????????????????????????????????????????????????????";
			wIPTItemExport.Standard = "a.????????????????????????????????????b.????????????????????????????????????5S??????????????????";
			wIPTItemExport.Legend = "";
			wIPTItemExport.WorkTime = "23";
			wIPTItemExport.SelfResult = "??????";
			wIPTItemExport.SelftDate = "2021/1/13";
			wIPTItemExport.MutualResult = "?????????";
			wIPTItemExport.MutualDate = "2021/1/13";
			wIPTItemExport.SpecialResult = "??????";
			wIPTItemExport.SpecialDate = "2021/1/13";
			wResult.add(wIPTItemExport);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	private List<IPTTool> GetIPTToolList() {
		List<IPTTool> wResult = new ArrayList<IPTTool>();
		try {
			IPTTool wIPTTool = new IPTTool();
			wIPTTool.OrderNum = 1;
			wIPTTool.Name = "?????????";
			wIPTTool.Modal = "PN-150mm";
			wIPTTool.Number = 2;
			wIPTTool.UnitText = "???";
			wResult.add(wIPTTool);

			wIPTTool = new IPTTool();
			wIPTTool.OrderNum = 2;
			wIPTTool.Name = "?????????";
			wIPTTool.Modal = "PN-160mm";
			wIPTTool.Number = 2;
			wIPTTool.UnitText = "???";
			wResult.add(wIPTTool);

			wIPTTool = new IPTTool();
			wIPTTool.OrderNum = 3;
			wIPTTool.Name = "?????????";
			wIPTTool.Modal = "PN-170mm";
			wIPTTool.Number = 2;
			wIPTTool.UnitText = "???";
			wResult.add(wIPTTool);

			wIPTTool = new IPTTool();
			wIPTTool.OrderNum = 3;
			wIPTTool.Name = "?????????";
			wIPTTool.Modal = "PN-170mm";
			wIPTTool.Number = 2;
			wIPTTool.UnitText = "???";
			wResult.add(wIPTTool);

			wIPTTool = new IPTTool();
			wIPTTool.OrderNum = 3;
			wIPTTool.Name = "?????????";
			wIPTTool.Modal = "PN-170mm";
			wIPTTool.Number = 2;
			wIPTTool.UnitText = "???";
			wResult.add(wIPTTool);

			wIPTTool = new IPTTool();
			wIPTTool.OrderNum = 3;
			wIPTTool.Name = "?????????";
			wIPTTool.Modal = "PN-170mm";
			wIPTTool.Number = 2;
			wIPTTool.UnitText = "???";
			wResult.add(wIPTTool);

			wIPTTool = new IPTTool();
			wIPTTool.OrderNum = 3;
			wIPTTool.Name = "?????????";
			wIPTTool.Modal = "PN-170mm";
			wIPTTool.Number = 2;
			wIPTTool.UnitText = "???";
			wResult.add(wIPTTool);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> SFC_ExportNewStandard_V2(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			int wStepID, int wType) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wOrderID <= 0) {
				wResult.FaultCode += "??????ID<=0?????????!";
				return wResult;
			}
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);

			// ???????????????????????????
			switch (wType) {
			case 1:
				// ????????????
				wResult.Result = SFC_ExportWholeTrain(wLoginUser, wOrder);
				if (StringUtils.isEmpty(wResult.Result)) {
					wResult.FaultCode += "?????????????????????????????????????????????????????????!";
				}
				break;
			case 2:
				// ????????????
				wResult.Result = SFC_ExportWholeStation(wLoginUser, wOrder, wPartID);
				if (StringUtils.isEmpty(wResult.Result)) {
					wResult.FaultCode += "?????????????????????????????????????????????????????????!";
				}
				break;
			case 3:
				// ????????????
				wResult.Result = SFC_ExportWholeStep(wLoginUser, wOrder, wPartID, wStepID);
				if (StringUtils.isEmpty(wResult.Result)) {
					wResult.FaultCode += "?????????????????????????????????????????????????????????!";
				}
				break;
			default:
				break;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????
	 */
	private String SFC_ExportWholeStep(BMSEmployee wLoginUser, OMSOrder wOrder, int wPartID, int wStepID) {
		String wResult = "";
		try {
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());

			String wFileName = StringUtils.Format("???{1}-{2}-{3}?????????????????????{0}.xls", wCurTime,
					wOrder.PartNo.replace("#", ""), QMSConstants.GetFPCPart(wPartID).Name,
					QMSConstants.GetFPCStepName(wStepID));
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });

			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			String wFilePath = StringUtils.Format("{0}{1}", wDirePath, wFileName);
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);

			List<IPTExport> wDataList = new ArrayList<IPTExport>();
			IPTExport wData = GetStepData(wLoginUser, wOrder, wPartID, wStepID);

			if (StringUtils.isEmpty(wData.StepName)) {
				wFileOutputStream.close();
				return wResult;
			}

			wDataList.add(wData);

			ExcelUtil.IPT_WriteStepItems(wDataList, wFileOutputStream);

//			Workbook wb = new Workbook();
//			wb.loadFromFile(wFilePath);
//
//			// ?????????????????????PDF??????
//			wFilePath = wFilePath.replace(".xls", ".pdf");
//			wb.saveToFile(wFilePath, FileFormat.PDF);
//
//			wFileName = wFileName.replace(".xls", ".pdf");

			String wUri = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);
			wResult = wUri;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????
	 */
	private IPTExport GetStepData(BMSEmployee wLoginUser, OMSOrder wOrder, int wPartID, int wStepID) {
		IPTExport wResult = new IPTExport();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ?????????????????????ID
//			int wStandardID = IPTStandardDAO.getInstance().SelectCurrentID(wLoginUser, wOrder.ProductID, wOrder.LineID,
//					wPartID, wStepID, wErrorCode);

			int wStandardID = SFCTaskIPTDAO.getInstance().GetUsedStandardID(wLoginUser, wOrder, wPartID, wStepID,
					wErrorCode);

			if (wStandardID <= 0) {
				return wResult;
			}
			// ?????????????????????
			IPTStandard wStandard = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, wStandardID,
					wErrorCode).Result;
			if (wStandard == null || wStandard.ID <= 0) {
				return wResult;
			}
			// ????????????????????????????????????ID
			int wFlowID = IPTStandardBPMDAO.getInstance().SelectFlowID(wLoginUser, wStandardID, wErrorCode);
			// ???????????????ID??????????????????
			if (wFlowID > 0) {
				List<BPMActivitiHisTask> wHisList = BPMServiceImpl.getInstance()
						.BPM_GetHistoryInstanceByID(wLoginUser, wFlowID).List(BPMActivitiHisTask.class);
				wHisList.removeIf(p -> p.Status == 0);
				if (wHisList.size() == 4) {
					SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					// ?????????
					wResult.Maker = QMSConstants.GetBMSEmployeeName(StringUtils.parseInt(wHisList.get(0).Assignee));
					wResult.MakerDate = wSDF.format(wHisList.get(0).EndTime.getTime());
					// ?????????
					wResult.JointlySign = QMSConstants
							.GetBMSEmployeeName(StringUtils.parseInt(wHisList.get(1).Assignee));
					wResult.JointlySignDate = wSDF.format(wHisList.get(1).EndTime.getTime());
					// ?????????
					wResult.Audit = QMSConstants.GetBMSEmployeeName(StringUtils.parseInt(wHisList.get(2).Assignee));
					wResult.AuditDate = wSDF.format(wHisList.get(2).EndTime.getTime());
					// ?????????
					wResult.Approval = QMSConstants.GetBMSEmployeeName(StringUtils.parseInt(wHisList.get(3).Assignee));
					wResult.ApprovalDate = wSDF.format(wHisList.get(3).EndTime.getTime());
				}
			}
			// ???????????????ID??????????????????
			wResult.IPTToolList = IPTToolDAO.getInstance().SelectList(wLoginUser, -1, wStandardID, wErrorCode);
			// ???????????????????????????????????????????????????
			wResult.APSBOMItemList = APSBOMItemDAO.getInstance().APS_QueryBOMItemList(wLoginUser, wOrder.ID, "", "",
					wOrder.LineID, wOrder.ProductID, -1, wPartID, wStepID, -1, "", -1, -1, -1, null, -1, -1, -1,
					wErrorCode);
			// ?????????????????????
			wResult.IPTItemExportList = GetIPTItemExportList(wLoginUser, wOrder, wStandard, wPartID, wStepID,
					wErrorCode);
			// ?????????????????????
			wResult.Code = wStandard.Code;
			wResult.CraftFile = "";
			wResult.LineName = wOrder.LineName;
			wResult.Name = wStandard.Remark;
			wResult.PartName = wStandard.PartName;
			wResult.PartNo = wOrder.PartNo;
			wResult.PersonNumber = String.valueOf(wStandard.PersonNumber);
			wResult.ProductNo = wOrder.ProductNo;
			wResult.Requirement = "";
			wResult.StepName = wStandard.PartPointName;
			wResult.StepNature = "";
			wResult.Version = wStandard.Version;
			wResult.WorkTime = String.valueOf(wStandard.WorkTime);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????
	 */
	private List<IPTItemExport> GetIPTItemExportList(BMSEmployee wLoginUser, OMSOrder wOrder, IPTStandard wStandard,
			int wPartID, int wStepID, OutResult<Integer> wErrorCode) {
		List<IPTItemExport> wResult = new ArrayList<IPTItemExport>();
		try {
			List<IPTItem> wItemList = IPTItemDAO.getInstance().SelectList(wLoginUser, -1, wStandard.ID, -1, -1,
					wErrorCode);

			if (wItemList == null || wItemList.size() <= 0) {
				return wResult;
			}

			// ???????????????????????????????????????????????????????????????????????????
			List<String> wPicDataList = SFCTaskIPTDAO.getInstance().SelectPicData(wLoginUser, wOrder, wPartID, wStepID,
					wErrorCode);
			String wPicselfResult = "";
			String wPicselftDate = "";
			String wSelfPic = "";
			if (wPicDataList.size() >= 2) {
				wPicselfResult = wPicDataList.get(0);
				wPicselftDate = wPicDataList.get(1);
				wSelfPic = StringUtils.Format("{0} {1}", wPicselfResult, wPicselftDate);
			}

			String wPicmutualResult = "";
			String wPicmutualDate = "";
			String wMutualPic = "";
			if (wPicDataList.size() >= 4) {
				wPicmutualResult = wPicDataList.get(2);
				wPicmutualDate = wPicDataList.get(3);
				wMutualPic = StringUtils.Format("{0} {1}", wPicmutualResult, wPicmutualDate);
			}
			String wPicspecialResult = "";
			String wPicspecialDate = "";
			String wSpecialPic = "";
			if (wPicDataList.size() >= 6) {
				wPicspecialResult = wPicDataList.get(4);
				wPicspecialDate = wPicDataList.get(5);
				wSpecialPic = StringUtils.Format("{0} {1}", wPicspecialResult, wPicspecialDate);
			}

			for (IPTItem wIPTItem : wItemList) {
				List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectListByOrder(wLoginUser, wOrder.ID, wPartID,
						wStepID, wIPTItem.ID, wErrorCode);

				String selfResult = GetCheckResult(wValueList, SFCTaskType.SelfCheck);
				String selftDate = GetCheckDate(wValueList, SFCTaskType.SelfCheck);
				if (StringUtils.isNotEmpty(wSelfPic)) {
					selfResult = "??????";
					selftDate = wSelfPic;
				}

				String mutualResult = GetCheckResult(wValueList, SFCTaskType.MutualCheck);
				String mutualDate = GetCheckDate(wValueList, SFCTaskType.MutualCheck);
				if (StringUtils.isNotEmpty(wMutualPic)) {
					mutualResult = "??????";
					mutualDate = wMutualPic;
				}

				String specialResult = GetCheckResult(wValueList, SFCTaskType.SpecialCheck);
				String specialDate = GetCheckDate(wValueList, SFCTaskType.SpecialCheck);
				if (StringUtils.isNotEmpty(wSpecialPic)) {
					specialResult = "??????";
					specialDate = wSpecialPic;
				}

				// ????????????
				wIPTItem.Legend = "";

				IPTItemExport wIPTItemExport = new IPTItemExport(wIPTItem.ProjectNo, wIPTItem.SerialNumber,
						wIPTItem.Text, wIPTItem.WorkContent, wIPTItem.Standard, wIPTItem.Legend,
						String.valueOf(wIPTItem.WorkTime), selfResult, selftDate, mutualResult, mutualDate,
						specialResult, specialDate);
				wResult.add(wIPTItemExport);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????(?????? ??????)
	 */
	private String GetCheckDate(List<IPTValue> wValueList, SFCTaskType wTaskType) {
		String wResult = "";
		try {
			List<IPTValue> wList = wValueList.stream().filter(p -> p.IPTMode == wTaskType.getValue())
					.collect(Collectors.toList());

			if (wList.size() <= 0) {
				return wResult;
			}

			IPTValue wMaxValue = wList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get();
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			wResult = StringUtils.Format("{0} {1}", wMaxValue.Submitor, wSDF.format(wMaxValue.SubmitTime.getTime()));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private String GetCheckResult(List<IPTValue> wValueList, SFCTaskType wTaskType) {
		String wResult = "";
		try {
			List<IPTValue> wList = wValueList.stream().filter(p -> p.IPTMode == wTaskType.getValue())
					.collect(Collectors.toList());

			if (wList.size() <= 0) {
				return wResult;
			}

			IPTValue wMaxValue = wList.stream().max(Comparator.comparing(IPTValue::getSubmitTime)).get();

			if (wMaxValue.Result == 1) {
				wResult = "??????";
			} else if (wMaxValue.Result == 2) {
				wResult = "?????????";
			} else {
				wResult = "?????????:" + wMaxValue.Result;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private String SFC_ExportWholeStation(BMSEmployee wLoginUser, OMSOrder wOrder, int wPartID) {
		String wResult = "";
		try {
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());

			String wFileName = StringUtils.Format("???{1}-{2}?????????????????????{0}.xls", wCurTime, wOrder.PartNo.replace("#", ""),
					QMSConstants.GetFPCPart(wPartID).Name);
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

			List<IPTExport> wDataList = GetStationData(wLoginUser, wOrder, wPartID);
			if (wDataList.size() <= 0) {
				wFileOutputStream.close();
				return wResult;
			}

			ExcelUtil.IPT_WriteStepItems(wDataList, wFileOutputStream);

			String wUri = StringUtils.Format("/{0}/export/{1}",
					new Object[] { Configuration.readConfigString("project.name", "application"), wFileName });
			wResult = wUri;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????????????????
	 */
	private List<IPTExport> GetStationData(BMSEmployee wLoginUser, OMSOrder wOMSOrder, int wPartID) {
		List<IPTExport> wResult = new ArrayList<IPTExport>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCTaskIPT> wList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOMSOrder.ID, wPartID,
					6, -1, -1, -1, "", null, null, wErrorCode);

			for (SFCTaskIPT wSFCTaskIPT : wList) {
				IPTExport wItem = GetStepData(wLoginUser, wOMSOrder, wSFCTaskIPT.StationID, wSFCTaskIPT.PartPointID);
				wResult.add(wItem);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????
	 */
	private String SFC_ExportWholeTrain(BMSEmployee wLoginUser, OMSOrder wOrder) {
		String wResult = "";
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMddHHmmss");
			// ????????????????????????(??????static/export/HXD3C0019-20210115152833)
			String wDirePath = StringUtils.Format("{0}static/export/{1}",
					Constants.getConfigPath().replace("config/", ""),
					wOrder.PartNo.replace("#", "") + "-" + wSDF.format(Calendar.getInstance().getTime()));

			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			// ??????????????????????????????
			List<List<IPTExport>> wListList = GetWholeTrainData(wLoginUser, wOrder);
			if (wListList.size() <= 0) {
				return wResult;
			}
			// ???????????????Excel??????????????????
			for (List<IPTExport> wDataList : wListList) {
				String wFilePath = StringUtils.Format("{0}/{1}.xls", wDirePath, wDataList.get(0).PartName);
				File wFile = new File(wFilePath);

				ExcelUtil.IPT_WriteStepItems(wDataList, new FileOutputStream(wFile));
			}
			// ????????????????????????
			String wTPath = StringUtils.Format("{0}static/export/{1}.zip",
					Constants.getConfigPath().replace("config/", ""),
					wOrder.PartNo.replace("#", "") + "-" + wSDF.format(Calendar.getInstance().getTime()));
			ZipUtils.Zip(wTPath, wDirePath);
			// ??????????????????????????????
			wResult = StringUtils.Format("/{0}/export/{1}.zip",
					Configuration.readConfigString("project.name", "application"),
					wOrder.PartNo.replace("#", "") + "-" + wSDF.format(Calendar.getInstance().getTime()));
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????????
	 */
	private List<List<IPTExport>> GetWholeTrainData(BMSEmployee wLoginUser, OMSOrder wOrder) {
		List<List<IPTExport>> wResult = new ArrayList<List<IPTExport>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCTaskIPT> wList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrder.ID, -1, 6, -1,
					-1, -1, "", null, null, wErrorCode);

			List<SFCTaskIPT> wPartIPTList = new ArrayList<SFCTaskIPT>(wList.stream()
					.collect(Collectors.toMap(SFCTaskIPT::getStationID, account -> account, (k1, k2) -> k2)).values());
			for (SFCTaskIPT wSFCTaskIPT : wPartIPTList) {
				List<IPTExport> wIPTExportList = new ArrayList<IPTExport>();
				List<SFCTaskIPT> wStepIPTList = wList.stream().filter(p -> p.StationID == wSFCTaskIPT.StationID)
						.collect(Collectors.toList());
				for (SFCTaskIPT wStepIPT : wStepIPTList) {
					IPTExport wItem = GetStepData(wLoginUser, wOrder, wStepIPT.StationID, wStepIPT.PartPointID);
					wIPTExportList.add(wItem);
				}
				wResult.add(wIPTExportList);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCRankInfo>> SFC_QueryRankInfoList(BMSEmployee wLoginUser, int wCarID) {
		ServiceResult<List<SFCRankInfo>> wResult = new ServiceResult<List<SFCRankInfo>>();
		wResult.Result = new ArrayList<SFCRankInfo>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = SFCRankInfoDAO.getInstance().SelectList(wLoginUser, -1, wCarID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> SFC_QueryMessageToDoList(BMSEmployee wSysAdmin, String wLoginID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<BMSEmployee> wUserList = QMSConstants.GetBMSEmployeeList().values().stream()
					.collect(Collectors.toList());

			if (!wUserList.stream().anyMatch(p -> p.LoginID.equals(wLoginID))) {
				FocasMessageResult wFocasMessageResult = new FocasMessageResult();
				wFocasMessageResult.setCode("error");
				wResult.Result = JSON.toJSONString(wFocasMessageResult);
				return wResult;
			}

			BMSEmployee wUser = wUserList.stream().filter(p -> p.LoginID.equals(wLoginID)).findFirst().get();

			String wTokenUser = FPCCommonFileDAO.getInstance().GetTokenUser(BaseDAO.SysAdmin, wUser.ID, wErrorCode);

			FocasMessageResult wFocasMessageResult = new FocasMessageResult();
			wFocasMessageResult.setCode("ok");
			// ?????????????????????
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance().BFC_GetMessageList(BaseDAO.SysAdmin, wUser.ID,
					-1, -1, BFCMessageType.Task.getValue(), 0, -1, null, null).List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(BaseDAO.SysAdmin, wUser.ID, -1, -1,
					BFCMessageType.Task.getValue(), 1, -1, null, null).List(BFCMessage.class));
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(BaseDAO.SysAdmin, wUser.ID, -1, -1,
					BFCMessageType.Task.getValue(), 2, -1, null, null).List(BFCMessage.class));
			// ????????????????????????(Web???????????????)
			List<Integer> wValidModuleList = StringUtils.parseListArgs(BPMEventModule.SCCall.getValue(),
					BPMEventModule.OccasionNCR.getValue(), BPMEventModule.TechNCR.getValue(), 1020,
					BPMEventModule.QTNCR.getValue(), BPMEventModule.SCNCR.getValue(),
					BPMEventModule.SCRepair.getValue(), BPMEventModule.QTRepair.getValue(),
					BPMEventModule.TechRepair.getValue(), BPMEventModule.CKRepair.getValue(),
					BPMEventModule.ToLoan.getValue(), BPMEventModule.ToLoanApply.getValue(),
					BPMEventModule.TempCheck.getValue());
			wMessageList = wMessageList.stream().filter(p -> wValidModuleList.stream().anyMatch(q -> p.ModuleID == q))
					.collect(Collectors.toList());
			// ?????????????????????
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			wFocasMessageResult.setCount(wMessageList.size());
			List<FocasMessageItem> wItemList = new ArrayList<FocasMessageItem>();

			// ???????????????
			Map<Long, String> wMessageTitleMap = GetMessageTitleMap(wSysAdmin, wMessageList);

			for (BFCMessage wBFCMessage : wMessageList) {
				FocasMessageItem wFocasMessageItem = new FocasMessageItem();

				String wUri = Configuration.readConfigString("core.server.url", "config/config");
				wFocasMessageItem.setLink(StringUtils.Format(
						"{3}MESCore/independent/contain.html?ModuleID={0}&MessageID={1}&StepID={2}&{4}&Order=1&ID={5}",
						wBFCMessage.ModuleID, wBFCMessage.MessageID, wBFCMessage.StepID, wUri, wTokenUser,
						wBFCMessage.ID));
				wFocasMessageItem.setTime(wSDF.format(wBFCMessage.CreateTime.getTime()));

				String wSender = "";
				String[] wStrs = wBFCMessage.MessageText.split("00 ");
				if (wStrs.length > 1) {
					wSender = wStrs[1].replace("???", "").replace("???", "");
				}

//				wFocasMessageItem.setTitle(StringUtils.Format("???{0}????????????  {1}",
//						BPMEventModule.getEnumType((int) wBFCMessage.ModuleID).getLable(), wSender));

				wFocasMessageItem.setTitle(StringUtils.Format("{0}-{1}",
						BPMEventModule.getEnumType((int) wBFCMessage.ModuleID).getLable(), wSender));

				if (wMessageTitleMap != null && wMessageTitleMap.size() > 0
						&& wMessageTitleMap.containsKey(wBFCMessage.ID)) {
					wFocasMessageItem.setTitle(wMessageTitleMap.get(wBFCMessage.ID));
				}

				wItemList.add(wFocasMessageItem);
			}
			wFocasMessageResult.setData(wItemList);
			// ???????????????
			wResult.Result = JSON.toJSONString(wFocasMessageResult);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ????????????(??????)
	 */
	private Map<Long, String> GetMessageTitleMap(BMSEmployee wSysAdmin, List<BFCMessage> wMessageList) {
		Map<Long, String> wResult = new HashMap<Long, String>();
		try {
			if (wMessageList == null || wMessageList.size() <= 0) {
				return wResult;
			}

			String wTitle = "";
			for (BFCMessage wBFCMessage : wMessageList) {
				switch (BPMEventModule.getEnumType((int) wBFCMessage.ModuleID)) {
				case SCMonthAudit:// ???????????????
					wTitle = StringUtils.Format("{0}???{1}??? ????????? {2}", wBFCMessage.CreateTime.get(Calendar.YEAR),
							wBFCMessage.CreateTime.get(Calendar.MONTH) + 1,
							wBFCMessage.MessageText.substring(wBFCMessage.MessageText.length() - 2));
					break;
				case SCWeekAudit:// ???????????????

					break;
				case SCDayAudit:// ???????????????

					break;
				default:
					break;
				}

				// ??????????????????
				if (StringUtils.isNotEmpty(wTitle) && !wResult.containsKey(wBFCMessage.ID)) {
					wResult.put(wBFCMessage.ID, wTitle);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> SFC_QueryMessageToReadList(BMSEmployee sysAdmin, String wLoginID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<BMSEmployee> wUserList = QMSConstants.GetBMSEmployeeList().values().stream()
					.collect(Collectors.toList());

			if (!wUserList.stream().anyMatch(p -> p.LoginID.equals(wLoginID))) {
				FocasMessageResult wFocasMessageResult = new FocasMessageResult();
				wFocasMessageResult.setCode("error");
				wResult.Result = JSON.toJSONString(wFocasMessageResult);
				return wResult;
			}

			BMSEmployee wUser = wUserList.stream().filter(p -> p.LoginID.equals(wLoginID)).findFirst().get();

			String wTokenUser = FPCCommonFileDAO.getInstance().GetTokenUser(BaseDAO.SysAdmin, wUser.ID, wErrorCode);

			FocasMessageResult wFocasMessageResult = new FocasMessageResult();
			wFocasMessageResult.setCode("ok");
			// ?????????????????????
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance().BFC_GetMessageList(BaseDAO.SysAdmin, wUser.ID,
					-1, -1, BFCMessageType.Notify.getValue(), 0, -1, null, null).List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(BaseDAO.SysAdmin, wUser.ID, -1, -1,
					BFCMessageType.Notify.getValue(), 1, -1, null, null).List(BFCMessage.class));
			// ????????????????????????(Web???????????????)
			List<Integer> wValidModuleList = new ArrayList<Integer>(
					Arrays.asList(1012, 8201, 1020, 3002, 2006, 1005, 1018, 2011, 3008, 5010, 8104, 8203, 8205));
			wMessageList = wMessageList.stream().filter(p -> wValidModuleList.stream().anyMatch(q -> p.ModuleID == q))
					.collect(Collectors.toList());
			// ?????????????????????
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			wFocasMessageResult.setCount(wMessageList.size());
			List<FocasMessageItem> wItemList = new ArrayList<FocasMessageItem>();
			for (BFCMessage wBFCMessage : wMessageList) {
				FocasMessageItem wFocasMessageItem = new FocasMessageItem();

				String wUri = Configuration.readConfigString("core.server.url", "config/config");
				wFocasMessageItem.setLink(StringUtils.Format(
						"{3}MESCore/independent/contain.html?ModuleID={0}&MessageID={1}&StepID={2}&{4}&Order=3&ID={5}",
						wBFCMessage.ModuleID, wBFCMessage.MessageID, wBFCMessage.StepID, wUri, wTokenUser,
						wBFCMessage.ID));
				wFocasMessageItem.setTime(wSDF.format(wBFCMessage.CreateTime.getTime()));

				String wSender = "";
				String[] wStrs = wBFCMessage.MessageText.split("00 ");
				if (wStrs.length > 1) {
					wSender = wStrs[1].replace("???", "").replace("???", "");
				}

				wFocasMessageItem.setTitle(StringUtils.Format("{0}-{1}",
						BPMEventModule.getEnumType((int) wBFCMessage.ModuleID).getLable(), wSender));

//				wFocasMessageItem.setTitle(StringUtils.Format("{0}???{1}???", wBFCMessage.Title, wBFCMessage.MessageText));
				wItemList.add(wFocasMessageItem);
			}
			wFocasMessageResult.setData(wItemList);
			// ???????????????
			wResult.Result = JSON.toJSONString(wFocasMessageResult);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCReturnOverMaterial>> SFC_QueryReturnOverMaterialEmployeeAllNew(BMSEmployee wLoginUser,
			Calendar wStartTime, Calendar wEndTime, int wPartID, int wStatus) {
		ServiceResult<List<SFCReturnOverMaterial>> wResult = new ServiceResult<List<SFCReturnOverMaterial>>();
		wResult.Result = new ArrayList<SFCReturnOverMaterial>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCReturnOverMaterial> wSendList = new ArrayList<SFCReturnOverMaterial>();
			List<SFCReturnOverMaterial> wToDoList = new ArrayList<SFCReturnOverMaterial>();
			List<SFCReturnOverMaterial> wDoneList = new ArrayList<SFCReturnOverMaterial>();

			List<BPMTaskBase> wBaseList = SFCReturnOverMaterialDAO.getInstance().BPM_GetSendTaskList(wLoginUser,
					wLoginUser.getID(), wStartTime, wEndTime, wErrorCode);
			wSendList = CloneTool.CloneArray(wBaseList, SFCReturnOverMaterial.class);

			wBaseList = SFCReturnOverMaterialDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
					wErrorCode);
			wToDoList = CloneTool.CloneArray(wBaseList, SFCReturnOverMaterial.class);

			wBaseList = SFCReturnOverMaterialDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(),
					wStartTime, wEndTime, wErrorCode);
			wDoneList = CloneTool.CloneArray(wBaseList, SFCReturnOverMaterial.class);

			List<Integer> wIDList = new ArrayList<Integer>();

			for (SFCReturnOverMaterial wSFCReturnOverMaterial : wToDoList) {
				if (wIDList.contains(wSFCReturnOverMaterial.ID))
					continue;
				wIDList.add(wSFCReturnOverMaterial.ID);
				wSFCReturnOverMaterial.TagTypes = TaskQueryType.ToHandle.getValue();
				wResult.Result.add(wSFCReturnOverMaterial);
			}

			for (SFCReturnOverMaterial wSFCReturnOverMaterial : wDoneList) {
				if (wIDList.contains(wSFCReturnOverMaterial.ID))
					continue;
				wIDList.add(wSFCReturnOverMaterial.ID);
				wSFCReturnOverMaterial.TagTypes = TaskQueryType.Handled.getValue();
				wResult.Result.add(wSFCReturnOverMaterial);
			}

			for (SFCReturnOverMaterial wSFCReturnOverMaterial : wSendList) {
				if (wIDList.contains(wSFCReturnOverMaterial.ID))
					continue;
				wIDList.add(wSFCReturnOverMaterial.ID);
				wSFCReturnOverMaterial.TagTypes = TaskQueryType.Sended.getValue();
				wResult.Result.add(wSFCReturnOverMaterial);
			}

			wResult.Result.removeIf(p -> p.Status == 0);

			// ??????
			if (wPartID > 0) {
				wResult.Result.removeIf(p -> p.PartID != wPartID);
			}
			// ??????
			if (wStatus >= 0) {
				if (wStatus == 0) {
					wResult.Result = wResult.Result.stream()
							.filter(p -> p.Status != 20 && p.Status != 21 && p.Status != 22)
							.collect(Collectors.toList());
				} else if (wStatus == 1) {
					wResult.Result = wResult.Result.stream()
							.filter(p -> p.Status == 20 || p.Status == 21 || p.Status == 22)
							.collect(Collectors.toList());
				}
			}

			wResult.Result.sort((o1, o2) -> o2.CreateTime.compareTo(o1.CreateTime));

			wResult.Result.sort((o1, o2) -> {
				if (o1.TagTypes == 1) {
					return -1;
				} else if (o2.TagTypes == 1) {
					return 1;
				}
				return 0;
			});

		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCReturnOverMaterial>> SFC_QueryReturnOverMaterialList(BMSEmployee wLoginUser,
			int wMaterialID, int wPartID, int wStatus, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SFCReturnOverMaterial>> wResult = new ServiceResult<List<SFCReturnOverMaterial>>();
		wResult.Result = new ArrayList<SFCReturnOverMaterial>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			switch (wStatus) {
			case 1:
				wResult.Result.addAll(
						SFCReturnOverMaterialDAO.getInstance().SelectList(wLoginUser, wMaterialID, wPartID, wStartTime,
								wEndTime, StringUtils.parseListArgs(SFCReturnOverMaterialStatus.NomalClose.getValue()),
								null, wErrorCode));
				break;
			case 0:

				wResult.Result.addAll(SFCReturnOverMaterialDAO.getInstance().SelectList(wLoginUser, wMaterialID,
						wPartID, wStartTime, wEndTime, null,
						StringUtils.parseListArgs(SFCReturnOverMaterialStatus.NomalClose.getValue(),
								SFCReturnOverMaterialStatus.ExceptionClose.getValue(),
								SFCReturnOverMaterialStatus.Canceled.getValue(),
								SFCReturnOverMaterialStatus.Default.getValue()),
						wErrorCode));

				break;
			default:
				wResult.Result.addAll(SFCReturnOverMaterialDAO.getInstance().SelectList(wLoginUser, wMaterialID,
						wPartID, wStartTime, wEndTime, null, null, wErrorCode));
				break;
			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}
			List<BPMTaskBase> wBaseList = SFCReturnOverMaterialDAO.getInstance().BPM_GetUndoTaskList(wLoginUser,
					wLoginUser.getID(), wErrorCode);
			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {
				if (!(wTaskBase instanceof SFCReturnOverMaterial))
					continue;
				SFCReturnOverMaterial wMTCTask = (SFCReturnOverMaterial) wTaskBase;
				wMTCTask.TagTypes = TaskQueryType.ToHandle.getValue();
				for (int i = 0; i < wResult.Result.size(); i++) {
					if (wResult.Result.get(i).ID == wMTCTask.ID)
						wResult.Result.set(i, wMTCTask);
				}
			}

		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTemporaryExamination>> SFC_QueryEmployeeAllTemporaryExaminationListNew(
			BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime, int wOrderID, int wProductID,
			String wPartNo, int wPartID, int wStatus) {
		ServiceResult<List<SFCTemporaryExamination>> wResult = new ServiceResult<List<SFCTemporaryExamination>>();
		wResult.Result = new ArrayList<SFCTemporaryExamination>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCTemporaryExamination> wSendList = new ArrayList<SFCTemporaryExamination>();
			List<SFCTemporaryExamination> wToDoList = new ArrayList<SFCTemporaryExamination>();
			List<SFCTemporaryExamination> wDoneList = new ArrayList<SFCTemporaryExamination>();

			wSendList.addAll(
					SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", wOrderID, wProductID,
							wPartNo, wPartID, wLoginUser.ID, -1, StringUtils.parseListArgs(1), null, null, wErrorCode));
			// ????????????????????????????????????
			wSendList.addAll(SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", wOrderID,
					wProductID, wPartNo, wPartID, wLoginUser.ID, -1, StringUtils.parseListArgs(2), wStartTime, wEndTime,
					wErrorCode));

			wToDoList.addAll(
					SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", wOrderID, wProductID,
							wPartNo, wPartID, -1, wLoginUser.ID, StringUtils.parseListArgs(1), null, null, wErrorCode));

			wDoneList.addAll(SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", wOrderID,
					wProductID, wPartNo, wPartID, -1, wLoginUser.ID, StringUtils.parseListArgs(2), wStartTime, wEndTime,
					wErrorCode));

			List<Integer> wIDList = new ArrayList<Integer>();
			for (SFCTemporaryExamination wMTCTask : wToDoList) {
				if (wIDList.contains(wMTCTask.ID))
					continue;
				wIDList.add(wMTCTask.ID);
				wMTCTask.TagTypes = TaskQueryType.ToHandle.getValue();
				wResult.Result.add(wMTCTask);

			}

			for (SFCTemporaryExamination wMTCTask : wDoneList) {
				if (wIDList.contains(wMTCTask.ID))
					continue;
				wMTCTask.TagTypes = TaskQueryType.Handled.getValue();
				wResult.Result.add(wMTCTask);
				wIDList.add(wMTCTask.ID);
			}

			for (SFCTemporaryExamination wMTCTask : wSendList) {
				if (wIDList.contains(wMTCTask.ID))
					continue;
				wMTCTask.TagTypes = TaskQueryType.Sended.getValue();
				wResult.Result.add(wMTCTask);
				wIDList.add(wMTCTask.ID);
			}

			wResult.Result.removeIf(p -> p.Status == 0);

			// ??????
			if (wStatus >= 0) {
				if (wStatus == 0) {
					wResult.Result = wResult.Result.stream().filter(p -> p.Status != 2).collect(Collectors.toList());
				} else if (wStatus == 1) {
					wResult.Result = wResult.Result.stream().filter(p -> p.Status == 2).collect(Collectors.toList());
				}
			}

			// ??????
			wResult.Result.sort((o1, o2) -> o2.CreateTime.compareTo(o1.CreateTime));

			wResult.Result.sort((o1, o2) -> {
				if (o1.TagTypes == 1) {
					return -1;
				} else if (o2.TagTypes == 1) {
					return 1;
				}
				return 0;
			});

		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTemporaryExamination>> SFC_QueryEmployeeAllTemporaryExaminationList(
			BMSEmployee wLoginUser, int wOrderID, int wProductID, String wPartNo, int wPartID, int wStatus,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SFCTemporaryExamination>> wResult = new ServiceResult<List<SFCTemporaryExamination>>();
		wResult.Result = new ArrayList<SFCTemporaryExamination>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			switch (wStatus) {
			case 1:
				wResult.Result.addAll(SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", wOrderID,
						wProductID, wPartNo, wPartID, -1, -1, StringUtils.parseListArgs(2), wStartTime, wEndTime,
						wErrorCode));
				break;
			case 0:
				wResult.Result.addAll(SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", wOrderID,
						wProductID, wPartNo, wPartID, -1, -1, StringUtils.parseListArgs(1), wStartTime, wEndTime,
						wErrorCode));
				break;
			default:
				wResult.Result.addAll(SFCTemporaryExaminationDAO.getInstance().SelectList(wLoginUser, -1, "", wOrderID,
						wProductID, wPartNo, wPartID, -1, -1, StringUtils.parseListArgs(1, 2), wStartTime, wEndTime,
						wErrorCode));
				break;
			}
			wResult.Result.sort((o1, o2) -> o2.CreateTime.compareTo(o1.CreateTime));

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}

			for (SFCTemporaryExamination wTaskBase : wResult.Result) {
				if (wTaskBase.Status == 1 && wTaskBase.CheckIDList.contains(wLoginUser.ID)) {
					wTaskBase.TagTypes = TaskQueryType.ToHandle.getValue();
				}
			}

		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCBogiesChangeBPM> SFC_QueryDefaultBogiesChangeBPM(BMSEmployee wLoginUser, int wEventID) {
		ServiceResult<SFCBogiesChangeBPM> wResult = new ServiceResult<SFCBogiesChangeBPM>();
		wResult.Result = new SFCBogiesChangeBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCBogiesChangeBPM> wList = SFCBogiesChangeBPMDAO.getInstance().SelectList(wLoginUser, -1, "",
					wLoginUser.ID, null, null, new ArrayList<Integer>(Arrays.asList(0)), -1, -1, wErrorCode);
			if (wList.size() > 0) {
				wResult.Result = wList.get(0);
				wResult.Result.CreateTime = Calendar.getInstance();
				wResult.Result.SubmitTime = Calendar.getInstance();
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<SFCBogiesChangeBPM> SFC_CreateBogiesChangeBPM(BMSEmployee wLoginUser,
			BPMEventModule wEventID) {
		ServiceResult<SFCBogiesChangeBPM> wResult = new ServiceResult<SFCBogiesChangeBPM>();
		wResult.Result = new SFCBogiesChangeBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result.Code = SFCBogiesChangeBPMDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wResult.Result.UpFlowID = wLoginUser.ID;
			wResult.Result.UpFlowName = wLoginUser.Name;
			wResult.Result.CreateTime = Calendar.getInstance();
			wResult.Result.SubmitTime = Calendar.getInstance();
			wResult.Result.ID = 0;
			wResult.Result.Status = SFCBogiesChangeBPMStatus.Default.getValue();
			wResult.Result.StatusText = "";
			wResult.Result.FlowType = wEventID.getValue();

			wResult.Result = (SFCBogiesChangeBPM) SFCBogiesChangeBPMDAO.getInstance().BPM_UpdateTask(wLoginUser,
					wResult.Result, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCBogiesChangeBPM> SFC_SubmitBogiesChangeBPM(BMSEmployee wLoginUser,
			SFCBogiesChangeBPM wData) {
		ServiceResult<SFCBogiesChangeBPM> wResult = new ServiceResult<SFCBogiesChangeBPM>();
		wResult.Result = new SFCBogiesChangeBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wData.Status == SFCBogiesChangeBPMStatus.ExceptionClose.getValue()) {
				wData.StatusText = "?????????";
			} else if (wData.Status == SFCBogiesChangeBPMStatus.NomalClose.getValue()) {
				wData.StatusText = "?????????";
			}

			wResult.Result = (SFCBogiesChangeBPM) SFCBogiesChangeBPMDAO.getInstance().BPM_UpdateTask(wLoginUser, wData,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCBogiesChangeBPM> SFC_GetBogiesChangeBPM(BMSEmployee wLoginUser, int wID) {
		ServiceResult<SFCBogiesChangeBPM> wResult = new ServiceResult<SFCBogiesChangeBPM>();
		wResult.Result = new SFCBogiesChangeBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (SFCBogiesChangeBPM) SFCBogiesChangeBPMDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wID,
					"", wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> SFC_QueryBogiesChangeBPMEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// 2??????
				wResult.Result = SFCBogiesChangeBPMDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 1??????
				wResult.Result = SFCBogiesChangeBPMDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
						wErrorCode);
				break;
			case Approver:// 4??????
				wResult.Result = SFCBogiesChangeBPMDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			if (wResult.Result.size() > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}

			// ??????
			wResult.Result.sort(Comparator.comparing(BPMTaskBase::getCreateTime, Comparator.reverseOrder()));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCBogiesChangeBPM>> SFC_QueryBogiesChangeBPMHistory(BMSEmployee wLoginUser, int wID,
			String wCode, int wUpFlowID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SFCBogiesChangeBPM>> wResult = new ServiceResult<List<SFCBogiesChangeBPM>>();
		wResult.Result = new ArrayList<SFCBogiesChangeBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = SFCBogiesChangeBPMDAO.getInstance().SelectList(wLoginUser, wID, wCode, wUpFlowID,
					wStartTime, wEndTime, null, -1, -1, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_QueryChangeOrderID(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = SFCBogiesChangeBPMDAO.getInstance().GetChangeOrderID(wLoginUser, wOrderID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCBogiesChangeBPM>> SFC_QueryBogiesChangeBPMEmployeeAllNew(BMSEmployee wLoginUser,
			int wOrderID, int wLevel, int wStatus, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SFCBogiesChangeBPM>> wResult = new ServiceResult<List<SFCBogiesChangeBPM>>();
		wResult.Result = new ArrayList<SFCBogiesChangeBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<SFCBogiesChangeBPM> wSendList = new ArrayList<SFCBogiesChangeBPM>();
			List<SFCBogiesChangeBPM> wToDoList = new ArrayList<SFCBogiesChangeBPM>();
			List<SFCBogiesChangeBPM> wDoneList = new ArrayList<SFCBogiesChangeBPM>();

			List<BPMTaskBase> wBaseList = SFCBogiesChangeBPMDAO.getInstance().BPM_GetSendTaskList(wLoginUser,
					wLoginUser.ID, wStartTime, wEndTime, wErrorCode);
			wSendList = CloneTool.CloneArray(wBaseList, SFCBogiesChangeBPM.class);

			wBaseList = SFCBogiesChangeBPMDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode);
			wToDoList = CloneTool.CloneArray(wBaseList, SFCBogiesChangeBPM.class);

			wBaseList = SFCBogiesChangeBPMDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID, wStartTime,
					wEndTime, wErrorCode);
			wDoneList = CloneTool.CloneArray(wBaseList, SFCBogiesChangeBPM.class);

			List<Integer> wIDList = new ArrayList<Integer>();

			for (SFCBogiesChangeBPM wMTCTask : wToDoList) {
				if (wIDList.contains(wMTCTask.ID))
					continue;
				wMTCTask.TagTypes = TaskQueryType.ToHandle.getValue();
				wResult.Result.add(wMTCTask);
				wIDList.add(wMTCTask.ID);
			}

			for (SFCBogiesChangeBPM wMTCTask : wDoneList) {
				if (wIDList.contains(wMTCTask.ID))
					continue;
				wMTCTask.TagTypes = TaskQueryType.Handled.getValue();
				wResult.Result.add(wMTCTask);
				wIDList.add(wMTCTask.ID);
			}

			for (SFCBogiesChangeBPM wMTCTask : wSendList) {
				if (wIDList.contains(wMTCTask.ID))
					continue;
				wMTCTask.TagTypes = TaskQueryType.Sended.getValue();
				wResult.Result.add(wMTCTask);
				wIDList.add(wMTCTask.ID);
			}

			wResult.Result.removeIf(p -> p.Status == 0);

			// ??????
			if (wOrderID > 0) {
				wResult.Result = wResult.Result.stream()
						.filter(p -> p.ItemList != null && p.ItemList.stream()
								.anyMatch(q -> q.BodyOrderID == wOrderID || q.BogiesOrderID == wOrderID))
						.collect(Collectors.toList());
			}
			// ??????
			if (wLevel > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.RespondLevel == wLevel)
						.collect(Collectors.toList());
			}
			// ??????
			if (wStatus >= 0) {
				if (wStatus == 0) {
					wResult.Result = wResult.Result.stream()
							.filter(p -> p.Status != 20 && p.Status != 21 && p.Status != 22)
							.collect(Collectors.toList());
				} else if (wStatus == 1) {
					wResult.Result = wResult.Result.stream()
							.filter(p -> p.Status == 20 || p.Status == 21 || p.Status == 22)
							.collect(Collectors.toList());
				}
			}

			wResult.Result.sort((o1, o2) -> o2.CreateTime.compareTo(o1.CreateTime));
			wResult.Result.sort((o1, o2) -> {
				if (o1.TagTypes == 1) {
					return -1;
				} else if (o2.TagTypes == 1) {
					return 1;
				}
				return 0;
			});

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> SFC_JudgeIsCanSubmit(BMSEmployee wLoginUser, SFCBogiesChangeBPM wTask) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wTask.Status == 1) {
				int wOrderID = SFCBogiesChangeBPMDAO.getInstance().GetChangingOrderID(wLoginUser, wTask.SOrderID,
						wErrorCode);
				if (wOrderID > 0) {
					OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wTask.SOrderID)
							.Info(OMSOrder.class);
					wResult.Result = StringUtils.Format("????????????{0}?????????????????????????????????????????????????????????", wOrder.PartNo);
					return wResult;
				} else {
					wOrderID = SFCBogiesChangeBPMDAO.getInstance().GetChangingOrderID(wLoginUser, wTask.TOrderID,
							wErrorCode);
					if (wOrderID > 0) {
						OMSOrder wOrder = LOCOAPSServiceImpl.getInstance()
								.OMS_QueryOrderByID(wLoginUser, wTask.TOrderID).Info(OMSOrder.class);
						wResult.Result = StringUtils.Format("????????????{0}???????????????????????????????????????????????????", wOrder.PartNo);
						return wResult;
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
	public ServiceResult<FocasReport> SFC_QueryFocasData(BMSEmployee wLoginUser) {
		ServiceResult<FocasReport> wResult = new ServiceResult<FocasReport>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			int wYear = Calendar.getInstance().get(Calendar.YEAR);

			Calendar wStartYear = Calendar.getInstance();
			wStartYear.set(wYear, 0, 1, 0, 0, 0);

			Calendar wEndYear = Calendar.getInstance();
			wEndYear.set(wYear, 11, 31, 23, 59, 59);

			int wMonth = Calendar.getInstance().get(Calendar.MONTH);

			Calendar wStartMonth = Calendar.getInstance();
			wStartMonth.set(wYear, wMonth, 1, 0, 0, 0);

			Calendar wEndMonth = Calendar.getInstance();
			wEndMonth.set(wYear, wMonth + 1, 1, 0, 0, 0);

			wResult.Result = SFCTaskIPTDAO.getInstance().SFC_QueryFocasData(wLoginUser, wStartYear, wEndYear,
					wStartMonth, wEndMonth, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FocasResult>> SFC_QueryFocasMonthData(BMSEmployee wLoginUser) {
		ServiceResult<List<FocasResult>> wResult = new ServiceResult<List<FocasResult>>();
		wResult.Result = new ArrayList<FocasResult>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			int wYear = Calendar.getInstance().get(Calendar.YEAR);

			Calendar wStartYear = Calendar.getInstance();
			wStartYear.set(wYear, 0, 1, 0, 0, 0);

			Calendar wEndYear = Calendar.getInstance();
			wEndYear.set(wYear, 11, 31, 23, 59, 59);

			int wMonth = Calendar.getInstance().get(Calendar.MONTH);

			Calendar wStartMonth = Calendar.getInstance();
			wStartMonth.set(wYear, wMonth, 1, 0, 0, 0);

			Calendar wEndMonth = Calendar.getInstance();
			wEndMonth.set(wYear, wMonth + 1, 1, 0, 0, 0);

			FocasReport wData = SFCTaskIPTDAO.getInstance().SFC_QueryFocasData(wLoginUser, wStartYear, wEndYear,
					wStartMonth, wEndMonth, wErrorCode);

			// ?????????????????????????????????????????????
			SetValue_Repair_Rate(wLoginUser, wData, wStartMonth, wEndMonth, 2);

			FocasResult wItem = new FocasResult();
			wItem.setName("ZXJC_Month");
			wItem.setCount(String.valueOf(wData.getZXJC_Month()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("ZCJC_Month");
			wItem.setCount(String.valueOf(wData.getZCJC_Month()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JCJG_Total_Month");
			wItem.setCount(String.valueOf(wData.getJCJG_Total_Month()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JCJG_C6_Month");
			wItem.setCount(String.valueOf(wData.getJCJG_C6_Month()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JCJG_C5_Month");
			wItem.setCount(String.valueOf(wData.getJCJG_C5_Month()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("TCFG_C5_Month");
			wItem.setCount(String.valueOf(wData.getTCFG_C5_Month()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("TCFG_C6_Month");
			wItem.setCount(String.valueOf(wData.getTCFG_C6_Month()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JYHGL_C5_Month");
			wItem.setCount(String.valueOf(wData.getJYHGL_C5_Month()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JYHGL_C6_Month");
			wItem.setCount(String.valueOf(wData.getJYHGL_C6_Month()));
			wResult.Result.add(wItem);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FocasResult>> SFC_QueryFocasYearData(BMSEmployee wLoginUser) {
		ServiceResult<List<FocasResult>> wResult = new ServiceResult<List<FocasResult>>();
		wResult.Result = new ArrayList<FocasResult>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			int wYear = Calendar.getInstance().get(Calendar.YEAR);

			Calendar wStartYear = Calendar.getInstance();
			wStartYear.set(wYear, 0, 1, 0, 0, 0);

			Calendar wEndYear = Calendar.getInstance();
			wEndYear.set(wYear, 11, 31, 23, 59, 59);

			int wMonth = Calendar.getInstance().get(Calendar.MONTH);

			Calendar wStartMonth = Calendar.getInstance();
			wStartMonth.set(wYear, wMonth, 1, 0, 0, 0);

			Calendar wEndMonth = Calendar.getInstance();
			wEndMonth.set(wYear, wMonth + 1, 1, 0, 0, 0);

			FocasReport wData = SFCTaskIPTDAO.getInstance().SFC_QueryFocasData(wLoginUser, wStartYear, wEndYear,
					wStartMonth, wEndMonth, wErrorCode);

			// ?????????????????????????????????????????????
			SetValue_Repair_Rate(wLoginUser, wData, wStartYear, wEndYear, 1);

			FocasResult wItem = new FocasResult();
			wItem.setName("JCJG_Total_Year");
			wItem.setCount(String.valueOf(wData.getJCJG_Total_Year()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JCJG_C6_Year");
			wItem.setCount(String.valueOf(wData.getJCJG_C6_Year()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JCJG_C5_Year");
			wItem.setCount(String.valueOf(wData.getJCJG_C5_Year()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JCTS_C6_Year");
			wItem.setCount(String.valueOf(wData.getJCTS_C6_Year()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JCTS_C5_Year");
			wItem.setCount(String.valueOf(wData.getJCTS_C5_Year()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("TCFG_C5_Year");
			wItem.setCount(String.valueOf(wData.getTCFG_C5_Year()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("TCFG_C6_Year");
			wItem.setCount(String.valueOf(wData.getTCFG_C6_Year()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JYHGL_C5_Year");
			wItem.setCount(String.valueOf(wData.getJYHGL_C5_Year()));
			wResult.Result.add(wItem);

			wItem = new FocasResult();
			wItem.setName("JYHGL_C6_Year");
			wItem.setCount(String.valueOf(wData.getJYHGL_C6_Year()));
			wResult.Result.add(wItem);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????????????????????????????
	 */
	private void SetValue_Repair_Rate(BMSEmployee wLoginUser, FocasReport wData, Calendar wStartYear, Calendar wEndYear,
			int wType) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ?????????C5???????????????????????????ID??????
			List<Integer> wOrderIDList_C5 = SFCTaskIPTDAO.getInstance().SelectOrderIDList(wLoginUser, wStartYear,
					wEndYear, 1, wErrorCode);
			// ?????????C6???????????????????????????ID??????
			List<Integer> wOrderIDList_C6 = SFCTaskIPTDAO.getInstance().SelectOrderIDList(wLoginUser, wStartYear,
					wEndYear, 2, wErrorCode);
			// ?????????????????????????????????
			Map<Integer, Integer> wRepairMap = SFCTaskIPTDAO.getInstance().SelectRepairMap(wLoginUser, wErrorCode);
			// ?????????C5??????????????????????????????
			int wTotal = 0;
			for (Integer wOrderID : wOrderIDList_C5) {
				if (wRepairMap.containsKey(wOrderID)) {
					wTotal += wRepairMap.get(wOrderID);
				}
			}
			double wC5RepairNumber = 0.0;
			if (wOrderIDList_C5.size() > 0) {
				wC5RepairNumber = new BigDecimal((double) wTotal / wOrderIDList_C5.size())
						.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			// ?????????C6??????????????????????????????
			wTotal = 0;
			for (Integer wOrderID : wOrderIDList_C6) {
				if (wRepairMap.containsKey(wOrderID)) {
					wTotal += wRepairMap.get(wOrderID);
				}
			}
			double wC6RepairNumber = 0.0;
			if (wOrderIDList_C6.size() > 0) {
				wC6RepairNumber = new BigDecimal((double) wTotal / wOrderIDList_C6.size())
						.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			// ???????????????????????????????????????
			Map<Integer, Integer> wYSRepairMap = SFCTaskIPTDAO.getInstance().SelectYSRepairMap(wLoginUser, wErrorCode);
			// ?????????C5??????????????????
			double wTotalReate = 0.0;
			for (Integer wOrderID : wOrderIDList_C5) {
				int wFlag = 0;
				if (wYSRepairMap.containsKey(wOrderID)) {
					wFlag = wYSRepairMap.get(wOrderID);
				}
				wTotalReate += new BigDecimal((double) (205 - wFlag) / 205 * 100).setScale(1, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
			}
			double wC5Rate = 0.0;
			if (wOrderIDList_C5.size() > 0) {
				wC5Rate = new BigDecimal(wTotalReate / wOrderIDList_C5.size()).setScale(1, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
			}
			// ?????????C6??????????????????
			wTotalReate = 0.0;
			for (Integer wOrderID : wOrderIDList_C6) {
				int wFlag = 0;
				if (wYSRepairMap.containsKey(wOrderID)) {
					wFlag = wYSRepairMap.get(wOrderID);
				}
				wTotalReate += new BigDecimal((double) (205 - wFlag) / 205 * 100).setScale(1, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
			}
			double wC6Rate = 0.0;
			if (wOrderIDList_C6.size() > 0) {
				wC6Rate = new BigDecimal(wTotalReate / wOrderIDList_C6.size()).setScale(1, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
			}
			// ?????????
			if (wType == 1) {// ??????
				wData.setTCFG_C5_Year(wC5RepairNumber);
				wData.setTCFG_C6_Year(wC6RepairNumber);
				wData.setJYHGL_C5_Year(wC5Rate);
				wData.setJYHGL_C6_Year(wC6Rate);
			} else if (wType == 2) {// ??????
				wData.setTCFG_C5_Month(wC5RepairNumber);
				wData.setTCFG_C6_Month(wC6RepairNumber);
				wData.setJYHGL_C5_Month(wC5Rate);
				wData.setJYHGL_C6_Month(wC6Rate);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<FocasHistoryData>> SFC_QueryFocasHistoryDataList(BMSEmployee wLoginUser) {
		ServiceResult<List<FocasHistoryData>> wResult = new ServiceResult<List<FocasHistoryData>>();
		wResult.Result = new ArrayList<FocasHistoryData>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			int wYear = Calendar.getInstance().get(Calendar.YEAR);

			wResult.Result.addAll(FocasHistoryDataDAO.getInstance().SelectList(wLoginUser, -1, wYear - 3, wErrorCode));
			wResult.Result.addAll(FocasHistoryDataDAO.getInstance().SelectList(wLoginUser, -1, wYear - 2, wErrorCode));
			wResult.Result.addAll(FocasHistoryDataDAO.getInstance().SelectList(wLoginUser, -1, wYear - 1, wErrorCode));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> BFC_CloseMessage(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wStepID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>();

			FocasHistoryDataDAO.getInstance().CloseNCRMessage(wLoginUser, wOrderID, wPartID, wStepID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<OMSOrder>> SFC_QueryUsableOrderList(BMSEmployee wLoginUser) {
		ServiceResult<List<OMSOrder>> wResult = new ServiceResult<List<OMSOrder>>();
		wResult.Result = new ArrayList<OMSOrder>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ????????????????????????ID??????
			List<Integer> wOrderIDList = SFCBogiesChangeBPMDAO.getInstance().QueryUsableOrderIDList(wLoginUser,
					wErrorCode);
			// ???????????????ID????????????????????????
			wResult.Result = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderListByIDList(wLoginUser, wOrderIDList)
					.List(OMSOrder.class);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> BFC_CloseAllMessage(BMSEmployee wLoginUser, int wModuleID, int wMessageID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			FocasHistoryDataDAO.getInstance().CloseAllMessage(wLoginUser, wModuleID, wMessageID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_SaveSavePic(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ??????????????????????????????????????????
			String wMsg = SFC_CheckIsCanSubmit(wLoginUser, wSFCTaskIPT, null);
			if (StringUtils.isNotEmpty(wMsg)) {
				wResult.FaultCode = wMsg;
				return wResult;
			}

			switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
			case SelfCheck:
				// ?????????
				wMsg = SFC_SubmitSelfPicValue(wLoginUser, wSFCTaskIPT);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case MutualCheck:
				// ?????????
				wMsg = SFC_SubmitMutualPicValue(wLoginUser, wSFCTaskIPT);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case SpecialCheck:
				// ?????????
				wMsg = SFC_SubmitSpecialPicValue(wLoginUser, wSFCTaskIPT);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case PreCheck:
				// ?????????
				wMsg = SFC_SubmitPrePicValue(wLoginUser, wSFCTaskIPT);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case Final:
				// ?????????
				wMsg = SFC_SubmitFinalPicValue(wLoginUser, wSFCTaskIPT);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			case OutPlant:
				// ????????????
				wMsg = SFC_SubmitOutPlantPicValue(wLoginUser, wSFCTaskIPT);
				if (StringUtils.isNotEmpty(wMsg)) {
					wResult.FaultCode = wMsg;
				}
				break;
			default:
				break;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????
	 */
	private String SFC_SubmitOutPlantPicValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		String wResult = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ?????????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ???????????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ???????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ?????????????????????????????????
			wSFCTaskIPT.Status = 2;
			// ???????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();
				// ?????????????????????
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wTaskStep != null && wTaskStep.ID > 0) {
					wTaskStep.EndTime = Calendar.getInstance();
					wTaskStep.Status = 5;
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
				}
				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.EndTime = Calendar.getInstance();
						wSFCTaskStep.EditTime = Calendar.getInstance();
						wSFCTaskStep.IsStartWork = 2;
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}
			// ??????????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private String SFC_SubmitFinalPicValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		String wResult = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ????????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ??????????????????????????????
			wSFCTaskIPT.Status = 2;
			// ????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();
				// ?????????????????????
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wTaskStep != null && wTaskStep.ID > 0) {
					wTaskStep.EndTime = Calendar.getInstance();
					wTaskStep.Status = 5;
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
				}
				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.EndTime = Calendar.getInstance();
						wSFCTaskStep.EditTime = Calendar.getInstance();
						wSFCTaskStep.IsStartWork = 2;
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ????????????????????????
			CreateConfirmForm(wLoginUser, wSFCTaskIPT, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private String SFC_SubmitPrePicValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		String wResult = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ????????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ??????????????????????????????
			wSFCTaskIPT.Status = 2;
			// ????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();
				// ?????????????????????
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wTaskStep != null && wTaskStep.ID > 0) {
					wTaskStep.EndTime = Calendar.getInstance();
					wTaskStep.Status = 5;
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
				}
				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.EndTime = Calendar.getInstance();
						wSFCTaskStep.EditTime = Calendar.getInstance();
						wSFCTaskStep.IsStartWork = 2;
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ????????????????????????
//			IPTServiceImpl.getInstance().IPT_TriggerYJProblem(wLoginUser, wSFCTaskIPT, wIPTValueList);
			// ???????????????????????????????????????(??????????????????????????????)
//			QMSConstants.mSFCTaskIPTList.add(wSFCTaskIPT);
			ExecutorService wES = Executors.newFixedThreadPool(1);
			wES.submit(() -> RSMServiceImpl.getInstance().RSM_AutoFinishTaskPart(wLoginUser, wSFCTaskIPT));
			wES.shutdown();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private String SFC_SubmitSpecialPicValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		String wResult = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ??????????????????????????????
			wSFCTaskIPT.Status = 2;
			// ????????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();

				// ?????????????????????
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wTaskStep != null && wTaskStep.ID > 0) {
					wTaskStep.Status = 5;
					wTaskStep.EndTime = Calendar.getInstance();
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
				}

				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wTaskStep.ID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.EndTime = Calendar.getInstance();
						wSFCTaskStep.EditTime = Calendar.getInstance();
						wSFCTaskStep.IsStartWork = 2;
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);

			// ?????????????????????
			ExecutorService wES = Executors.newFixedThreadPool(1);
			wES.submit(() -> SFCTaskIPTDAO.getInstance().SFC_CloseRelaMessage(wLoginUser, wErrorCode));
			wES.shutdown();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private String SFC_SubmitMutualPicValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		String wResult = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			int wTaskType = wSFCTaskIPT.TaskType;
			// ?????????????????????
			List<SFCTaskIPT> wSpecialTaskList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
					wSFCTaskIPT.TaskStepID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, null, -1, null, null,
					wErrorCode);
			if (wSpecialTaskList.size() <= 0) {
				wSFCTaskIPT.TaskType = SFCTaskType.SpecialCheck.getValue();
				int wNewID = this.SFC_TriggerTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
				// ?????????????????????
				TriggerSpecialMessage(wLoginUser, wSFCTaskIPT, wNewID);
				// ????????????????????????????????????
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}

			wSFCTaskIPT.TaskType = wTaskType;
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ????????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ??????????????????????????????
			wSFCTaskIPT.Status = 2;
			// ????????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ?????????????????????
			if (!SFCTaskIPTDAO.getInstance().IsTurnOrderControl(wLoginUser, wSFCTaskIPT, wErrorCode)) {
				APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepByID(wLoginUser, wSFCTaskIPT.TaskStepID).Info(APSTaskStep.class);
				if (wTaskStep != null && wTaskStep.ID > 0) {
					wTaskStep.EndTime = Calendar.getInstance();
					wTaskStep.Status = 5;
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
				}
				// ?????????????????????
				List<SFCTaskStep> wSFCTaskStepList = LOCOAPSServiceImpl.getInstance()
						.SFC_QueryTaskStepList(wLoginUser, wSFCTaskIPT.TaskStepID, -1, -1).List(SFCTaskStep.class);
				if (wSFCTaskStepList != null && wSFCTaskStepList.size() > 0) {
					for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
						wSFCTaskStep.EndTime = Calendar.getInstance();
						wSFCTaskStep.EditTime = Calendar.getInstance();
						wSFCTaskStep.IsStartWork = 2;
						LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
					}
				}
			}

			// ???????????????????????????1
			if (wSpecialTaskList.size() > 0) {
				wSpecialTaskList.get(0).Status = 1;
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSpecialTaskList.get(0), wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
	 */
	private String SFC_SubmitSelfPicValue(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT) {
		String wResult = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			int wTaskType = wSFCTaskIPT.TaskType;
			// ?????????????????????
			List<SFCTaskIPT> wMuIPTList = null;
			if (wSFCTaskIPT.Type == 2) {
				wMuIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskIPT.TaskStepID,
						SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, 2, null, null, wErrorCode);
			} else {
				wMuIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskIPT.TaskStepID,
						SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, 1, null, null, wErrorCode);
			}
			if (wMuIPTList.size() <= 0) {
				wSFCTaskIPT.TaskType = SFCTaskType.MutualCheck.getValue();
				int wNewID = this.SFC_TriggerTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
				// ?????????????????????
				TriggerMutualMessage(wLoginUser, wSFCTaskIPT, wNewID);
			} else if (wMuIPTList.size() > 0) {
				wMuIPTList.get(0).Status = 1;
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wMuIPTList.get(0), wErrorCode);
			}
			wSFCTaskIPT.TaskType = wTaskType;
			// ??????????????????????????????
			if (!wSFCTaskIPT.OperatorList.contains(wLoginUser.ID)) {
				wSFCTaskIPT.OperatorList.add(wLoginUser.ID);
			}
			// ?????????????????????????????????
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1);
			if (wSFCTaskIPT.StartTime.compareTo(wBaseTime) < 0) {
				wSFCTaskIPT.StartTime = Calendar.getInstance();
			}
			// ?????????????????????????????????
			wSFCTaskIPT.SubmitTime = Calendar.getInstance();
			// ???????????????????????????
			wSFCTaskIPT.Status = 2;
			// ?????????????????????????????????
			if (wSFCTaskIPT.Status == 2) {
				wSFCTaskIPT.EndTime = Calendar.getInstance();

				// ???????????????????????????????????????
				SFCTaskIPTDAO.getInstance().CloseAllDispatchMessage(wLoginUser, wSFCTaskIPT.TaskStepID, wErrorCode);
			}
			// ???????????????????????????????????????
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ???????????????
			SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			// ?????????????????????
			SFCTaskIPTDAO.getInstance().DisMutualMessage(wLoginUser, wSFCTaskIPT.ID, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCLetPassBPM> SFC_QueryDefaultLetPassBPM(BMSEmployee wLoginUser, int wEventID) {
		ServiceResult<SFCLetPassBPM> wResult = new ServiceResult<SFCLetPassBPM>();
		wResult.Result = new SFCLetPassBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCLetPassBPM> wList = SFCLetPassBPMDAO.getInstance().SelectList(wLoginUser, -1, "", null, null, -1,
					-1, -1, new ArrayList<Integer>(Arrays.asList(0)), wLoginUser.ID, wErrorCode);
			if (wList.size() > 0) {
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
	public synchronized ServiceResult<SFCLetPassBPM> SFC_CreateLetPassBPM(BMSEmployee wLoginUser,
			BPMEventModule wEventID) {
		ServiceResult<SFCLetPassBPM> wResult = new ServiceResult<SFCLetPassBPM>();
		wResult.Result = new SFCLetPassBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result.Code = SFCLetPassBPMDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wResult.Result.UpFlowID = wLoginUser.ID;
			wResult.Result.UpFlowName = wLoginUser.Name;
			wResult.Result.CreateTime = Calendar.getInstance();
			wResult.Result.SubmitTime = Calendar.getInstance();
			wResult.Result.ID = 0;
			wResult.Result.Status = SFCLetPassBPMStatus.Default.getValue();
			wResult.Result.StatusText = "";
			wResult.Result.FlowType = wEventID.getValue();

			wResult.Result = (SFCLetPassBPM) SFCLetPassBPMDAO.getInstance().BPM_UpdateTask(wLoginUser, wResult.Result,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCLetPassBPM> SFC_SubmitLetPassBPM(BMSEmployee wLoginUser, SFCLetPassBPM wData) {
		ServiceResult<SFCLetPassBPM> wResult = new ServiceResult<SFCLetPassBPM>();
		wResult.Result = new SFCLetPassBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (SFCLetPassBPM) SFCLetPassBPMDAO.getInstance().BPM_UpdateTask(wLoginUser, wData,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCLetPassBPM> SFC_GetLetPassBPM(BMSEmployee wLoginUser, int wID) {
		ServiceResult<SFCLetPassBPM> wResult = new ServiceResult<SFCLetPassBPM>();
		wResult.Result = new SFCLetPassBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (SFCLetPassBPM) SFCLetPassBPMDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wID, "",
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> SFC_QueryLetPassBPMEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// 2??????
				wResult.Result = SFCLetPassBPMDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 1??????
				wResult.Result = SFCLetPassBPMDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
						wErrorCode);
				break;
			case Approver:// 4??????
				wResult.Result = SFCLetPassBPMDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			if (wResult.Result.size() > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}

			// ??????
			wResult.Result.sort(Comparator.comparing(BPMTaskBase::getCreateTime, Comparator.reverseOrder()));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCLetPassBPM>> SFC_QueryLetPassBPMHistory(BMSEmployee wLoginUser, int wID, String wCode,
			int wUpFlowID, int wOrderID, int wPartID, int wClosePartID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SFCLetPassBPM>> wResult = new ServiceResult<List<SFCLetPassBPM>>();
		wResult.Result = new ArrayList<SFCLetPassBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = SFCLetPassBPMDAO.getInstance().SelectList(wLoginUser, wUpFlowID, wCode, wStartTime,
					wEndTime, wOrderID, wPartID, wClosePartID, null, wUpFlowID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<SFCLetPassBPM> SFC_UpdateLetPass(BMSEmployee wLoginUser, SFCLetPassBPM wData) {
		ServiceResult<SFCLetPassBPM> wResult = new ServiceResult<SFCLetPassBPM>();
		wResult.Result = new SFCLetPassBPM();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (SFCLetPassBPM) SFCLetPassBPMDAO.getInstance().BPM_UpdateTask(wLoginUser, wData,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCLetPassBPM>> SFC_QueryLetPassBPMEmployeeAllWeb(BMSEmployee wLoginUser, int wOrderID,
			int wPartID, int wClosePartID, int wStatus, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<SFCLetPassBPM>> wResult = new ServiceResult<List<SFCLetPassBPM>>();
		wResult.Result = new ArrayList<SFCLetPassBPM>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (wStatus) {
			case 1:
				wResult.Result.addAll(SFCLetPassBPMDAO.getInstance().SelectList(wLoginUser, -1, "", wStartTime,
						wEndTime, -1, -1, -1, new ArrayList<Integer>(Arrays.asList(20, 21, 22)), -1, wErrorCode));
				break;
			case 0:
				wResult.Result.addAll(
						SFCLetPassBPMDAO.getInstance().SelectList(wLoginUser, -1, "", wStartTime, wEndTime, -1, -1, -1,
								new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)), -1, wErrorCode));
				break;
			default:
				wResult.Result.addAll(SFCLetPassBPMDAO.getInstance().SelectList(wLoginUser, -1, "", wStartTime,
						wEndTime, -1, -1, -1,
						new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 20, 21, 22)), -1, wErrorCode));
				break;
			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			// ??????????????????
			if (wOrderID > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.OrderID == wOrderID)
						.collect(Collectors.toList());
			}
			if (wPartID > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.PartID == wPartID).collect(Collectors.toList());
			}
			if (wClosePartID > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.ClosePartID == wClosePartID)
						.collect(Collectors.toList());
			}

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}

			// ??????????????????
			List<BPMTaskBase> wBaseList = SFCLetPassBPMDAO.getInstance().BPM_GetUndoTaskList(wLoginUser,
					wLoginUser.getID(), wErrorCode);
			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {
				if (!(wTaskBase instanceof SFCLetPassBPM))
					continue;
				SFCLetPassBPM wSFCLetPassBPM = (SFCLetPassBPM) wTaskBase;
				wSFCLetPassBPM.TagTypes = TaskQueryType.ToHandle.getValue();
				for (int i = 0; i < wResult.Result.size(); i++) {
					if (wResult.Result.get(i).ID == wSFCLetPassBPM.ID)
						wResult.Result.set(i, wSFCLetPassBPM);
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
	public ServiceResult<List<SFCTaskIPTPartNo>> SFC_QuerySpecialPartNoNew_V2(BMSEmployee wLoginUser) {
		ServiceResult<List<SFCTaskIPTPartNo>> wResult = new ServiceResult<List<SFCTaskIPTPartNo>>();
		wResult.Result = new ArrayList<SFCTaskIPTPartNo>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ?????????????????????
			List<BMSWorkCharge> wWorkChargeList = QMSConstants.GetBMSWorkChargeList().values().stream()
					.filter(p -> p.Active == 1 && p.CheckerList != null && p.CheckerList.size() > 0
							&& p.CheckerList.stream().anyMatch(q -> q.intValue() == wLoginUser.ID))
					.collect(Collectors.toList());
			if (wWorkChargeList == null || wWorkChargeList.size() <= 0) {
				return wResult;
			}
			// ?????????????????????????????????
			List<Integer> wOrderIDList = SFCTaskIPTDAO.getInstance().GetRepairingOrderIDList(wLoginUser, wErrorCode);
			List<OMSOrder> wOrderList = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser, wOrderIDList).List(OMSOrder.class);
			wOrderList.removeIf(p -> p.RouteID <= 0);
			wOrderList.sort(Comparator.comparing(OMSOrder::getPartNo));
			// ????????????????????????????????????BOP????????????
			List<Integer> wRouteIDList = wOrderList.stream().map(p -> p.RouteID).distinct()
					.collect(Collectors.toList());
			List<FPCRoutePart> wRoutePartList = SFCTaskIPTDAO.getInstance().SelectRoutePartListByRouteIDList(wLoginUser,
					wRouteIDList, wErrorCode);
			// ?????????????????????
			for (OMSOrder wOMSOrder : wOrderList) {
				List<FPCRoutePart> wMyRoutePartList = wRoutePartList.stream()
						.filter(p -> p.RouteID == wOMSOrder.RouteID).collect(Collectors.toList());
				wMyRoutePartList = wMyRoutePartList.stream()
						.filter(p -> wWorkChargeList.stream().anyMatch(q -> q.StationID == p.PartID))
						.collect(Collectors.toList());

				// ????????????
				List<SFCTaskRecord> wCDList = SFCTaskRecordDAO.getInstance().SelectList(wLoginUser, -1, wOMSOrder.ID,
						-1, 1, wErrorCode);
				wMyRoutePartList.removeIf(p -> wCDList.stream().anyMatch(q -> q.PartID == p.PartID));

				if (wMyRoutePartList.size() <= 0) {
					continue;
				}

				SFCTaskIPTPartNo wSFCTaskIPTPartNo = SFCTaskIPTDAO.getInstance().GetSFCTaskIPTPartNo(wLoginUser,
						wOMSOrder, wMyRoutePartList, wErrorCode);
				wResult.Result.add(wSFCTaskIPTPartNo);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTaskIPTPart>> SFC_QuerySpecialPartListNew_V2(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<SFCTaskIPTPart>> wResult = new ServiceResult<List<SFCTaskIPTPart>>();
		wResult.Result = new ArrayList<SFCTaskIPTPart>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ?????????????????????
			List<BMSWorkCharge> wWorkChargeList = QMSConstants.GetBMSWorkChargeList().values().stream()
					.filter(p -> p.Active == 1 && p.CheckerList != null && p.CheckerList.size() > 0
							&& p.CheckerList.stream().anyMatch(q -> q.intValue() == wLoginUser.ID))
					.collect(Collectors.toList());
			if (wWorkChargeList == null || wWorkChargeList.size() <= 0) {
				return wResult;
			}
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			List<FPCRoutePart> wRoutePartList = SFCTaskIPTDAO.getInstance().SelectRoutePartListByRouteIDList(wLoginUser,
					new ArrayList<Integer>(Arrays.asList(wOrder.RouteID)), wErrorCode);
			List<FPCRoutePart> wMyRoutePartList = wRoutePartList.stream().filter(p -> p.RouteID == wOrder.RouteID)
					.collect(Collectors.toList());
			wMyRoutePartList = wMyRoutePartList.stream()
					.filter(p -> wWorkChargeList.stream().anyMatch(q -> q.StationID == p.PartID))
					.collect(Collectors.toList());

			// ????????????
			List<SFCTaskRecord> wCDList = SFCTaskRecordDAO.getInstance().SelectList(wLoginUser, -1, wOrder.ID, -1, 1,
					wErrorCode);
			wMyRoutePartList.removeIf(p -> wCDList.stream().anyMatch(q -> q.PartID == p.PartID));

			for (FPCRoutePart wFPCRoutePart : wMyRoutePartList) {
				SFCTaskIPTPart wSFCTaskIPTPart = SFCTaskIPTDAO.getInstance().GetSFCTaskIPTPart(wLoginUser, wOrder,
						wFPCRoutePart, wErrorCode);
				wResult.Result.add(wSFCTaskIPTPart);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_SaveRecord(BMSEmployee wLoginUser, int wOrderID, List<Integer> wPartIDList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wPartIDList == null || wPartIDList.size() <= 0 || wOrderID < 0) {
				return wResult;
			}

			for (int wPartID : wPartIDList) {
				List<SFCTaskRecord> wList = SFCTaskRecordDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wPartID,
						-1, wErrorCode);
				if (wList.size() > 0) {
					continue;
				}

				SFCTaskRecord wRecord = new SFCTaskRecord(0, wOrderID, wPartID, 1, wLoginUser.ID, wLoginUser.Name,
						Calendar.getInstance());
				SFCTaskRecordDAO.getInstance().Update(wLoginUser, wRecord, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_BackPic(BMSEmployee wLoginUser, SFCTaskIPT wTask) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wTask == null || wTask.ID <= 0) {
				return wResult;
			}

			// ??????????????????
			SFCTaskIPTDAO.getInstance().DeleteList(wLoginUser, new ArrayList<SFCTaskIPT>(Arrays.asList(wTask)),
					wErrorCode);
			// ???????????????????????????????????????
			wTask.Status = 3;
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wTask, wErrorCode);
			// ??????????????????
			List<SFCTaskIPT> wMList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wTask.TaskStepID,
					SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, -1, null, null, wErrorCode);
			if (wMList.size() > 0) {
				SFCTaskIPTDAO.getInstance().DeleteList(wLoginUser, wMList, wErrorCode);
			}
			wMList.get(0).Status = 3;
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wMList.get(0), wErrorCode);
			// ???????????????????????????????????????
			List<SFCTaskIPT> wSList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wTask.TaskStepID,
					SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, -1, null, null, wErrorCode);
			for (SFCTaskIPT wSFCTaskIPT : wSList) {
				wSFCTaskIPT.Status = 1;
				wSFCTaskIPT.OldPicUri = "";
				wSFCTaskIPT.PicUri = "";
				wSFCTaskIPT.OperatorList = new ArrayList<Integer>();
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			}
			wSList.get(0).Status = 3;
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSList.get(0), wErrorCode);
			// ?????????????????????
			SendMessageByBackPic(wLoginUser, wSList);
			// ???????????????????????????????????????
			APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance().APS_QueryTaskStepByID(wLoginUser, wTask.TaskStepID)
					.Info(APSTaskStep.class);
			if (wTaskStep != null && wTaskStep.ID > 0) {
				wTaskStep.Status = 4;
				LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????
	 */
	private void SendMessageByBackPic(BMSEmployee wLoginUser, List<SFCTaskIPT> wSList) {
		try {
			if (wSList == null || wSList.size() <= 0) {
				return;
			}

			List<Integer> wUserList = wSList.get(0).OperatorList;

			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");
			int wShiftID = Integer.parseInt(wSDF.format(Calendar.getInstance().getTime()));
			for (Integer wUserID : wUserList) {
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0L;
				wMessage.MessageID = wSList.get(0).ID;
				wMessage.Title = StringUtils.Format("?????? {0} {1}", wSList.get(0).PartPointName, wSList.get(0).PartNo);
				wMessage.MessageText = StringUtils.Format("???{0}??? {1}????????????????????????????????????{2}???", BPMEventModule.SCZJ.getLable(),
						wLoginUser.Name, wSList.get(0).PartPointName);
				wMessage.ModuleID = BPMEventModule.SCZJ.getValue();
				wMessage.ResponsorID = wUserID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = 0L;
				wMessage.Type = BFCMessageType.Task.getValue();
				wBFCMessageList.add(wMessage);
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<Integer> SFC_UpdateChecker(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ??????????????????????????????
			List<Integer> wOrderIDList = SFCTaskIPTDAO.getInstance().GetRepairingOrderIDList(wLoginUser, wErrorCode);
			// ??????????????????????????????
			for (Integer wOrderID : wOrderIDList) {
				List<SFCTaskIPT> wTaskIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID,
						-1, SFCTaskType.SelfCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);
				// ???????????????????????????????????????
//				wTaskIPTList.removeIf(p -> StringUtils.isNotEmpty(p.CheckerList));
				for (SFCTaskIPT wSFCTaskIPT : wTaskIPTList) {
					// ?????????????????????ID?????????ID???????????????
					String wCheckerList = SFCTaskIPTDAO.getInstance().GetCheckerList(wLoginUser, wSFCTaskIPT.TaskStepID,
							wSFCTaskIPT.StationID, wErrorCode);
					// ???????????????????????????
					if (StringUtils.isNotEmpty(wCheckerList)) {
						wSFCTaskIPT.CheckerList = wCheckerList;
						SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
					}
				}
			}

//			for (Integer wOrderID : wOrderIDList) {
//				List<APSTaskStep> wTaskStepList = LOCOAPSServiceImpl.getInstance()
//						.APS_QueryTaskStepList(wLoginUser, wOrderID, -1, -1, null).List(APSTaskStep.class);
//				// ???????????????????????????????????????
//				wTaskStepList.removeIf(p -> StringUtils.isNotEmpty(p.MaterialNo));
//				for (APSTaskStep wAPSTaskStep : wTaskStepList) {
//					// ?????????????????????ID?????????ID???????????????
//					String wCheckerList = SFCTaskIPTDAO.getInstance().GetCheckerList(wLoginUser, wAPSTaskStep.ID,
//							wAPSTaskStep.PartID, wErrorCode);
//					// ???????????????????????????
//					if (StringUtils.isNotEmpty(wCheckerList)) {
//						wAPSTaskStep.MaterialNo = wCheckerList;
//						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wAPSTaskStep);
//					}
//				}
//			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_RejectStep(BMSEmployee wLoginUser, SFCTaskIPT wTask) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wTask == null || wTask.ID <= 0) {
				return wResult;
			}

			// ??????????????????
			SFCTaskIPTDAO.getInstance().DeleteList(wLoginUser, new ArrayList<SFCTaskIPT>(Arrays.asList(wTask)),
					wErrorCode);
			// ?????????????????????
			wTask.Status = 3;
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wTask, wErrorCode);
			// ???????????????
			List<IPTValue> wValueList2 = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1, wTask.ID, -1, -1, -1,
					wErrorCode);
			IPTValueDAO.getInstance().DeleteList(wLoginUser, wValueList2, wErrorCode);
			// ??????????????????
			List<SFCTaskIPT> wMList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wTask.TaskStepID,
					SFCTaskType.MutualCheck.getValue(), -1, -1, -1, null, -1, null, null, wErrorCode);
			if (wMList.size() > 0) {
				SFCTaskIPTDAO.getInstance().DeleteList(wLoginUser, wMList, wErrorCode);
			}
			// ?????????????????????
			wMList.get(0).Status = 3;
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wMList.get(0), wErrorCode);
			// ???????????????
			List<IPTValue> wValueList1 = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1, wMList.get(0).ID, -1,
					-1, -1, wErrorCode);
			IPTValueDAO.getInstance().DeleteList(wLoginUser, wValueList1, wErrorCode);
			// ??????????????????
			List<SFCTaskIPT> wSList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wTask.TaskStepID,
					SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, -1, null, null, wErrorCode);
			for (SFCTaskIPT wSFCTaskIPT : wSList) {

				// ????????????????????????????????????
				int wNewVersionID = IPTStandardDAO.getInstance().SelectCurrentID(wLoginUser, wSFCTaskIPT.ProductID,
						wSFCTaskIPT.LineID, wSFCTaskIPT.StationID, wSFCTaskIPT.PartPointID, wErrorCode);
				if (wNewVersionID != wSFCTaskIPT.ModuleVersionID && wNewVersionID > 0) {
					wSFCTaskIPT.ModuleVersionID = wNewVersionID;
				}

				wSFCTaskIPT.Status = 1;
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
			}
			// ?????????????????????
			wSList.get(0).Status = 3;
			SFCTaskIPTDAO.getInstance().UpdateMessageStatus(wLoginUser, wSList.get(0), wErrorCode);
			// ???????????????value
			List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1, wSList.get(0).ID, -1,
					-1, -1, wErrorCode);
			for (IPTValue wIPTValue : wValueList) {
				wIPTValue.Status = 1;
				IPTValueDAO.getInstance().Update(wLoginUser, wIPTValue, wErrorCode);
			}
			// ???????????????????????????
			SendMessageByBackPic(wLoginUser, wSList);
			// ???????????????????????????
			APSTaskStep wTaskStep = LOCOAPSServiceImpl.getInstance().APS_QueryTaskStepByID(wLoginUser, wTask.TaskStepID)
					.Info(APSTaskStep.class);
			if (wTaskStep != null && wTaskStep.ID > 0) {
				wTaskStep.Status = 4;
				LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wTaskStep);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTrainProgress01>> SFC_QueryTrainProgress01(BMSEmployee wLoginUser) {
		ServiceResult<List<SFCTrainProgress01>> wResult = new ServiceResult<List<SFCTrainProgress01>>();
		wResult.Result = new ArrayList<SFCTrainProgress01>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ?????????????????????????????????
			List<Integer> wOrderIDList = SFCTaskIPTDAO.getInstance().GetRepairingOrderIDList(wLoginUser, wErrorCode);
			List<OMSOrder> wOrderList = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser, wOrderIDList).List(OMSOrder.class);
			wOrderList.removeIf(p -> p.RouteID <= 0);
			wOrderList.sort(Comparator.comparing(OMSOrder::getPartNo));

			for (OMSOrder wOMSOrder : wOrderList) {
				SFCTrainProgress01 wSFCTrainProgress01 = SFCTaskIPTDAO.getInstance().GetSFCTrainProgress01(wLoginUser,
						wOMSOrder, wErrorCode);
				wResult.Result.add(wSFCTrainProgress01);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTrainProgress02>> SFC_QueryTrainProgress02(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<SFCTrainProgress02>> wResult = new ServiceResult<List<SFCTrainProgress02>>();
		wResult.Result = new ArrayList<SFCTrainProgress02>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ???????????????
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			// ???????????????????????????ID??????
			List<APSTaskPart> wTaskPartList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartAll(wLoginUser, wOrderID, APSShiftPeriod.Week.getValue()).List(APSTaskPart.class);
			wTaskPartList = wTaskPartList.stream().filter(p -> p.Active == 1).collect(Collectors.toList());
			wTaskPartList.sort(Comparator.comparing(APSTaskPart::getStatus));

			// ????????????
			List<LFSWorkAreaStation> wASList = QMSConstants.GetLFSWorkAreaStationList().values().stream()
					.filter(p -> p.Active == 1).collect(Collectors.toList());
			// ??????
			wASList.sort(Comparator.comparing(LFSWorkAreaStation::getOrderNum));
			for (LFSWorkAreaStation wLFSWorkAreaStation : wASList) {
				if (!wTaskPartList.stream().anyMatch(p -> p.PartID == wLFSWorkAreaStation.StationID)) {
					continue;
				}

				APSTaskPart wAPSTaskPart = wTaskPartList.stream().filter(p -> p.PartID == wLFSWorkAreaStation.StationID)
						.findFirst().get();

				SFCTrainProgress02 wSFCTrainProgress02 = SFCTaskIPTDAO.getInstance().GetSFCTrainProgress02(wLoginUser,
						wOrder, wAPSTaskPart, wErrorCode);
				wResult.Result.add(wSFCTrainProgress02);
			}
			// ?????????????????????
//			for (APSTaskPart wAPSTaskPart : wTaskPartList) {
//				SFCTrainProgress02 wSFCTrainProgress02 = SFCTaskIPTDAO.getInstance().GetSFCTrainProgress02(wLoginUser,
//						wOrder, wAPSTaskPart, wErrorCode);
//				wResult.Result.add(wSFCTrainProgress02);
//			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<SFCTrainProgress03>> SFC_QueryTrainProgress03(BMSEmployee wLoginUser, int wOrderID,
			int wPartID) {
		ServiceResult<List<SFCTrainProgress03>> wResult = new ServiceResult<List<SFCTrainProgress03>>();
		wResult.Result = new ArrayList<SFCTrainProgress03>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ???????????????
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			// ?????????????????????
			List<APSTaskStep> wTaskStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, wOrderID, wPartID, -1, new ArrayList<Integer>())
					.List(APSTaskStep.class);
			wTaskStepList = wTaskStepList.stream().filter(p -> p.Active == 1).collect(Collectors.toList());
			// ?????????????????????
			for (APSTaskStep wAPSTaskStep : wTaskStepList) {
				SFCTrainProgress03 wSFCTrainProgress03 = SFCTaskIPTDAO.getInstance().GetSFCTrainProgress03(wLoginUser,
						wOrder, wAPSTaskStep, wErrorCode);
				wSFCTrainProgress03.IsPic = SFCTaskIPTDAO.getInstance().GetIsPic(wLoginUser, wAPSTaskStep, wOrder,
						wErrorCode);
				wResult.Result.add(wSFCTrainProgress03);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_SetQuality(BMSEmployee wLoginUser, int wSFCTaskIPT, int wIPTItemID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, wIPTItemID, wSFCTaskIPT,
					-1, -1, -1, wErrorCode);
			for (IPTValue wIPTValue : wValueList) {
				wIPTValue.Result = 1;
				IPTValueDAO.getInstance().Update(wLoginUser, wIPTValue, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> SFC_RepairBack(BMSEmployee wLoginUser, int wSFCTaskIPT, int wIPTItemID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wIPTItemID > 0) {
				SFCTaskIPTDAO.getInstance().SFC_RepairBack(wLoginUser, wSFCTaskIPT, wIPTItemID, wErrorCode);

				// ????????????????????????
				SFCTaskIPT wTaskIPT = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wSFCTaskIPT, wErrorCode);
				if (wTaskIPT != null && wTaskIPT.ID > 0) {
					APSTaskStep wInfo = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskStepByID(wLoginUser, wTaskIPT.TaskStepID).Info(APSTaskStep.class);
					if (wInfo != null && wInfo.ID > 0) {
						wInfo.Status = 4;
						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wInfo);
					}
				}
			} else {
				// ???????????????????????????
				SFCTaskIPT wTaskIPT = SFCTaskIPTDAO.getInstance().SelectByID(wLoginUser, wSFCTaskIPT, wErrorCode);
				if (wTaskIPT != null && wTaskIPT.ID > 0) {
					APSTaskStep wInfo = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskStepByID(wLoginUser, wTaskIPT.TaskStepID).Info(APSTaskStep.class);
					if (wInfo != null && wInfo.ID > 0) {
						wInfo.Status = 4;
						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wInfo);
					}
				}
				// ?????????????????????
				wTaskIPT.Status = 1;
				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
				// ?????????Value
				List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1, -1, wTaskIPT.ID, -1,
						-1, -1, wErrorCode);
				IPTValueDAO.getInstance().DeleteList(wLoginUser, wValueList, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}
}
