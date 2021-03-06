package com.mes.qms.server.serviceimpl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.RSMService;
import com.mes.qms.server.service.mesenum.APSBOMSourceType;
import com.mes.qms.server.service.mesenum.APSShiftPeriod;
import com.mes.qms.server.service.mesenum.APSTaskStatus;
import com.mes.qms.server.service.mesenum.BFCMessageType;
import com.mes.qms.server.service.mesenum.BMSDepartmentType;
import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.FMCShiftLevel;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.NCRStatus;
import com.mes.qms.server.service.mesenum.RSMPartTaskStatus;
import com.mes.qms.server.service.mesenum.SFCTaskType;
import com.mes.qms.server.service.mesenum.TCMChangeType;
import com.mes.qms.server.service.mesenum.RSMTurnOrderTaskStatus;
import com.mes.qms.server.service.mesenum.SCHSecondStatus;
import com.mes.qms.server.service.mesenum.SFCTaskStatus;
import com.mes.qms.server.service.mesenum.SFCTaskStepType;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bms.BMSRoleItem;
import com.mes.qms.server.service.po.bms.BMSWorkCharge;
import com.mes.qms.server.service.po.bpm.BPMActivitiTask;
import com.mes.qms.server.service.po.fmc.FMCWorkCharge;
import com.mes.qms.server.service.po.focas.FocasHistoryData;
import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.ncr.NCRTask;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.qms.server.service.po.sch.SCHSecondmentApply;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.tcm.TCMMaterialChangeItems;
import com.mes.qms.server.service.po.tcm.TCMMaterialChangeLog;
import com.mes.qms.server.service.po.tcm.TCMRework;
import com.mes.qms.server.service.po.tcm.TCMTechChangeNotice;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.aps.APSMaterialReturnDAO;
import com.mes.qms.server.serviceimpl.dao.focas.FocasHistoryDataDAO;
import com.mes.qms.server.serviceimpl.dao.fpc.FPCRouteDAO;
import com.mes.qms.server.serviceimpl.dao.rsm.RSMTurnOrderTaskDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTaskIPTDAO;
import com.mes.qms.server.serviceimpl.dao.tcm.TCMMaterialChangeItemsDAO;
import com.mes.qms.server.serviceimpl.dao.tcm.TCMMaterialChangeLogDAO;
import com.mes.qms.server.serviceimpl.utils.MESServer;
import com.mes.qms.server.serviceimpl.utils.qms.QMSUtils;
import com.mes.qms.server.utils.Constants;
import com.mes.qms.server.utils.qms.MESFileUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

@Service
public class RSMServiceImpl implements RSMService {

	private static Logger logger = LoggerFactory.getLogger(RSMServiceImpl.class);

	private static RSMService Instance;

	public static RSMService getInstance() {
		if (Instance == null)
			Instance = new RSMServiceImpl();
		return Instance;
	}

	public ServiceResult<List<RSMTurnOrderTask>> RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser, int wTaskPartID) {
		ServiceResult<List<RSMTurnOrderTask>> wResult = new ServiceResult<List<RSMTurnOrderTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = RSMTurnOrderTaskDAO.getInstance().SelectList(wLoginUser, wTaskPartID, wErrorCode);

			if (wResult.Result != null && wResult.Result.size() > 0) {
				OMSOrder wOMSOrder = null;
				for (RSMTurnOrderTask wRSMTurnOrderTask : wResult.Result) {
					wOMSOrder = LOCOAPSServiceImpl.getInstance()
							.OMS_QueryOrderByID(wLoginUser, wRSMTurnOrderTask.OrderID).Info(OMSOrder.class);
					if (wOMSOrder != null && wOMSOrder.ID > 0) {
						wRSMTurnOrderTask.CarNo = wOMSOrder.PartNo;
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

	@Override
	public ServiceResult<List<FPCPart>> RSM_QueryNextStationList(BMSEmployee wLoginUser, int wOrderID, int wStationID) {
		ServiceResult<List<FPCPart>> wResult = new ServiceResult<List<FPCPart>>();
		try {
			wResult.Result = new ArrayList<FPCPart>();

			OMSOrder wOMSOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOMSOrder == null || wOMSOrder.ID <= 0) {
				return wResult;
			}

			List<FPCRoutePart> wTempList = new ArrayList<>();

			List<FPCRoutePart> wPartList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wOMSOrder.RouteID).collect(Collectors.toList());

			wTempList = wPartList.stream().filter(p -> p.PrevPartID == wStationID).collect(Collectors.toList());
			// ????????????
			Map<Integer, FPCPart> wPartMap = QMSConstants.GetFPCPartList();
			// ????????????????????????
			if (wTempList.size() <= 0) {
				Optional<FPCRoutePart> wTempOption = wPartList.stream().filter(p -> p.PartID == wStationID).findFirst();
				if (wTempOption.isPresent()) {
					Map<String, String> wPartIDMap = wTempOption.get().NextPartIDMap;
					if (wPartIDMap != null && wPartIDMap.size() > 0) {
						for (String wPartID : wPartIDMap.keySet()) {
							int wID = Integer.parseInt(wPartID);
							if (wPartMap.containsKey(wID)) {
								wResult.Result.add(wPartMap.get(wID));
							}
						}
					}
				}
			}
			for (FPCRoutePart wFPCRoutePart : wTempList) {
				if (wPartMap.containsKey(wFPCRoutePart.PartID)) {
					wResult.Result.add(wPartMap.get(wFPCRoutePart.PartID));
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
	public ServiceResult<Integer> RSM_SubmitTurnOrderApply(BMSEmployee wLoginUser, APSTaskPart wAPSTaskPart,
			List<Integer> wStationIDList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<RSMTurnOrderTask> wSFCTurnOrderTaskList = new ArrayList<RSMTurnOrderTask>();
			RSMTurnOrderTask wTask = null;
			List<Integer> wYJIDList = QMSUtils.getInstance().GetYJStationIDList(wLoginUser);
			for (int wItemID : wStationIDList) {
				wTask = new RSMTurnOrderTask();
				wTask.ApplyID = wLoginUser.ID;
				wTask.ApplyStationID = wAPSTaskPart.PartID;
				wTask.ApplyTime = Calendar.getInstance();
				wTask.ID = 0;
				wTask.OrderID = wAPSTaskPart.OrderID;
				wTask.Status = RSMTurnOrderTaskStatus.Auditing.getValue();
				if (wYJIDList.stream().anyMatch(p -> p == wAPSTaskPart.PartID)) {
					wTask.FinishTime = Calendar.getInstance();
					wTask.Status = RSMTurnOrderTaskStatus.Passed.getValue();
				}
				wTask.TargetStationID = wItemID;
				wTask.Type = 1;
				wTask.TaskPartID = wAPSTaskPart.ID;
				wTask.CarNo = wAPSTaskPart.PartNo;
				wSFCTurnOrderTaskList.add(wTask);
			}

			// ?????????????????????????????????
			for (RSMTurnOrderTask wSFCTurnOrderTask : wSFCTurnOrderTaskList) {
				wResult.Result = RSMTurnOrderTaskDAO.getInstance().Update(wLoginUser, wSFCTurnOrderTask, wErrorCode);
				// ???????????????????????????????????????????????????????????????
				RSM_SendMessageToMonitorWhenAcApplyTurnOrder(wLoginUser, wResult.Result, wSFCTurnOrderTask, false);
			}

			// ??????????????????????????????????????????????????????????????????
			List<APSTaskStep> wStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, -1, -1, wAPSTaskPart.ID, new ArrayList<Integer>())
					.List(APSTaskStep.class);
			if (wStepList == null || wStepList.size() <= 0) {
				return wResult;
			}

			ServiceResult<List<Object>> wObjectList = null;
			List<IPTItem> wToDoList = null;
			List<IPTValue> wIPTValueList = null;
			List<SFCTaskIPT> wSpecialList = null;
			IPTValue wIPTValue = null;
			for (APSTaskStep wAPSTaskStep : wStepList) {
				wObjectList = SFCServiceImpl.getInstance().SFC_QueryToDoAndDoneList(wLoginUser, wAPSTaskStep.ID,
						SFCTaskType.SelfCheck.getValue());
				if (wObjectList == null || wObjectList.Result == null || wObjectList.Result.size() <= 0) {
					continue;
				}

				wToDoList = (List<IPTItem>) wObjectList.Result.get(0);
				if (wToDoList == null || wToDoList.size() <= 0) {
					continue;
				}

				int wTaskID = 0;
				wSpecialList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wAPSTaskStep.ID,
						SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Step.getValue(), null,
						null, wErrorCode);
				if (wSpecialList == null || wSpecialList.size() <= 0) {
					wSpecialList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wAPSTaskStep.ID,
							SFCTaskType.SelfCheck.getValue(), -1, -1, -1, null, SFCTaskStepType.Step.getValue(), null,
							null, wErrorCode);
					if (wSpecialList != null && wSpecialList.size() > 0) {
						SFCTaskIPT wIPT = wSpecialList.get(0);
						wIPT.ID = 0;
						wIPT.ActiveTime = Calendar.getInstance();
						wIPT.Status = SFCTaskStatus.Active.getValue();
						wIPT.ShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(),
								APSShiftPeriod.Day, FMCShiftLevel.Day);
						wTaskID = SFCTaskIPTDAO.getInstance().SFC_AddTaskIPT(wLoginUser, wIPT, wErrorCode);
					}
				} else {
					wTaskID = wSpecialList.get(0).ID;
				}

				wIPTValueList = new ArrayList<IPTValue>();
				for (IPTItem wIPTItem : wToDoList) {
					wIPTValue = new IPTValue();
					wIPTValue.ID = 0;
					wIPTValue.IPTItemID = wIPTItem.ID;
					wIPTValue.Remark = "????????????!";
					wIPTValue.Result = 2;
					wIPTValue.SubmitID = -100;
					wIPTValue.SubmitTime = Calendar.getInstance();
					wIPTValue.TaskID = wTaskID;
					wIPTValue.StandardID = wIPTItem.VID;
					wIPTValue.IPTMode = SFCTaskType.SpecialCheck.getValue();
					wIPTValue.Status = 2;
					wIPTValueList.add(wIPTValue);
				}
				IPTServiceImpl.getInstance().IPT_SaveIPTValue(wLoginUser, wIPTValueList, wTaskID, -1);
			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????????????????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param result
	 */
	private void RSM_SendMessageToMonitorWhenAcApplyTurnOrder(BMSEmployee wLoginUser, Integer wNewID,
			RSMTurnOrderTask wSFCTurnOrderTask, boolean wIsAuto) {
		try {
			List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
			BFCMessage wMessage = null;
			// ???????????????????????????????????????
			FPCPart wFPCPart = QMSConstants.GetFPCPart(wSFCTurnOrderTask.ApplyStationID);
			List<Integer> wChargeClassIDList = wFPCPart.DepartmentIDList;
			if (wFPCPart != null && wFPCPart.ID > 0 && wFPCPart.DepartmentIDList.size() > 0) {
				// ??????????????????????????????????????????
				List<BMSEmployee> wApplyMonitorList = QMSConstants.GetBMSEmployeeList().values().stream()
						.filter(p -> wChargeClassIDList.contains(p.DepartmentID)
								&& QMSConstants.GetBMSPosition(p.Position).DutyID == 1)
						.collect(Collectors.toList());
				int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
						FMCShiftLevel.Day);
				if (wApplyMonitorList != null && wApplyMonitorList.size() > 0) {
					// ????????????????????????????????????????????????
					for (BMSEmployee wBMSEmployee : wApplyMonitorList) {
						wMessage = new BFCMessage();
						wMessage.Active = 0;
						wMessage.CompanyID = 0;
						wMessage.CreateTime = Calendar.getInstance();
						wMessage.EditTime = Calendar.getInstance();
						wMessage.ID = 0;
						wMessage.MessageID = wNewID;
						if (wIsAuto) {
							wMessage.Title = StringUtils.Format("??????{0} {1} {2}", BPMEventModule.TurnOrder.getLable(),
									wShiftID, wSFCTurnOrderTask.CarNo);
						} else {
							wMessage.Title = StringUtils.Format("??????{0} {1} {2}", BPMEventModule.TurnOrder.getLable(),
									wShiftID, wSFCTurnOrderTask.CarNo);
						}
						wMessage.MessageText = StringUtils.Format("{0} ???{1}???->???{2}???",
								BPMEventModule.TurnOrder.getLable(),
								QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID),
								QMSConstants.GetFPCPartName(wSFCTurnOrderTask.TargetStationID));
						wMessage.ModuleID = BPMEventModule.TurnOrder.getValue();
						wMessage.ResponsorID = wBMSEmployee.ID;
						wMessage.ShiftID = wShiftID;
						wMessage.StationID = 0;
						wMessage.Type = BFCMessageType.Notify.getValue();
						wBFCMessageList.add(wMessage);
					}
				}
			}
			// ???????????????????????????????????????
			wFPCPart = QMSConstants.GetFPCPart(wSFCTurnOrderTask.TargetStationID);
			List<Integer> wClassIDList = wFPCPart.DepartmentIDList;
			if (wFPCPart != null && wFPCPart.ID > 0 && wFPCPart.DepartmentIDList.size() > 0) {
				// ??????????????????????????????????????????
				List<BMSEmployee> wTargetMonitorList = QMSConstants.GetBMSEmployeeList().values().stream()
						.filter(p -> wClassIDList.contains(p.DepartmentID)
								&& QMSConstants.GetBMSPosition(p.Position).DutyID == 1)
						.collect(Collectors.toList());
				int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
						FMCShiftLevel.Day);
				if (wTargetMonitorList != null && wTargetMonitorList.size() > 0) {
					// ????????????????????????????????????????????????
					for (BMSEmployee wBMSEmployee : wTargetMonitorList) {
						wMessage = new BFCMessage();
						wMessage.Active = 0;
						wMessage.CompanyID = 0;
						wMessage.CreateTime = Calendar.getInstance();
						wMessage.EditTime = Calendar.getInstance();
						wMessage.ID = 0;
						wMessage.MessageID = wNewID;
						if (wIsAuto) {
							wMessage.Title = StringUtils.Format("??????{0} {1} {2}", BPMEventModule.TurnOrder.getLable(),
									wShiftID, wSFCTurnOrderTask.CarNo);
						} else {
							wMessage.Title = StringUtils.Format("??????{0} {1} {2}", BPMEventModule.TurnOrder.getLable(),
									wShiftID, wSFCTurnOrderTask.CarNo);
						}
						wMessage.MessageText = StringUtils.Format("{0} ???{1}???->???{2}???",
								BPMEventModule.TurnOrder.getLable(),
								QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID),
								QMSConstants.GetFPCPartName(wSFCTurnOrderTask.TargetStationID));
						wMessage.ModuleID = BPMEventModule.TurnOrder.getValue();
						wMessage.ResponsorID = wBMSEmployee.ID;
						wMessage.ShiftID = wShiftID;
						wMessage.StationID = 0;
						wMessage.Type = BFCMessageType.Notify.getValue();
						wBFCMessageList.add(wMessage);
					}
				}
			}
			// ?????????????????????
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<APSTaskPart>> RSM_QueryAllTaskPartList(BMSEmployee wLoginUser) {
		ServiceResult<List<APSTaskPart>> wResult = new ServiceResult<List<APSTaskPart>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = new ArrayList<APSTaskPart>();

			// ??????shiftID
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Week, 0);

			List<APSTaskPart> wList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartList(wLoginUser, wShiftID, APSShiftPeriod.Week.getValue())
					.List(APSTaskPart.class);
			// ????????????????????????
			List<APSTaskPart> wTempList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartList(wLoginUser,
							new ArrayList<Integer>(Arrays.asList(APSTaskStatus.Issued.getValue(),
									APSTaskStatus.Started.getValue(), APSTaskStatus.Done.getValue())))
					.List(APSTaskPart.class);
			if (wTempList != null && wTempList.size() > 0) {
				wList.addAll(wTempList);
				// ??????
				wList = new ArrayList<APSTaskPart>(wList.stream()
						.collect(Collectors.toMap(APSTaskPart::getID, account -> account, (k1, k2) -> k2)).values());
			}
			if (wList.size() <= 0) {
				return wResult;
			}

			List<RSMTurnOrderTask> wTurnOrderList = null;

			for (APSTaskPart wAPSTaskPart : wList) {
				// ?????????????????????
				wTurnOrderList = RSMTurnOrderTaskDAO.getInstance().SelectList(wLoginUser, wAPSTaskPart.ID, wErrorCode);
				if (wTurnOrderList.size() <= 0) {
					wAPSTaskPart.TaskText = RSMPartTaskStatus.NotTurnOrder.getLable();
				} else {
					if (wTurnOrderList.stream()
							.anyMatch(p -> p.Type == 1 && p.Status == RSMTurnOrderTaskStatus.Auditing.getValue())) {
						wAPSTaskPart.TaskText = RSMPartTaskStatus.AcTurnOrdering.getLable();
					} else if (wTurnOrderList.stream()
							.anyMatch(p -> p.Type == 1 && p.Status == RSMTurnOrderTaskStatus.Passed.getValue())) {
						wAPSTaskPart.TaskText = RSMPartTaskStatus.AcTurnOrdered.getLable();
					} else if (wTurnOrderList.stream()
							.anyMatch(p -> p.Type == 2 && p.Status == RSMTurnOrderTaskStatus.Auditing.getValue())) {
						wAPSTaskPart.TaskText = RSMPartTaskStatus.AutoTurnOrdering.getLable();
					} else if (wTurnOrderList.stream()
							.anyMatch(p -> p.Type == 2 && p.Status == RSMTurnOrderTaskStatus.Passed.getValue())) {
						wAPSTaskPart.TaskText = RSMPartTaskStatus.AutoTurnOrdered.getLable();
					}
				}

				List<FPCPart> wPartList = (List<FPCPart>) (RSM_QueryNextStationList(wLoginUser, wAPSTaskPart.OrderID,
						wAPSTaskPart.PartID)).Result;
				if (wPartList != null && wPartList.size() > 0) {
					continue;
				}

				List<APSTaskStep> wTaskSetpList = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepList(wLoginUser, -1, -1, wAPSTaskPart.ID, new ArrayList<Integer>())
						.List(APSTaskStep.class);
				if (wTaskSetpList != null && wTaskSetpList.size() > 0
						&& wTaskSetpList.stream().allMatch(p -> (p.Status == APSTaskStatus.Done.getValue()))) {
					wAPSTaskPart.TaskText = "??????";
					continue;
				}
				wAPSTaskPart.TaskText = "?????????";
			}

			// ???????????????????????????????????????
			if (CoreServiceImpl.getInstance()
					.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 240003, 0, 0)
					.Info(Boolean.class)) {
				wResult.Result = wList;
			} else {
				FPCPart wFPCPart;
				for (APSTaskPart wAPSTaskPart : wList) {

					wFPCPart = QMSConstants.GetFPCPart(wAPSTaskPart.PartID);
					if (wFPCPart == null || wFPCPart.ID <= 0
							|| !wFPCPart.DepartmentIDList.contains(wLoginUser.DepartmentID))
						continue;

					wResult.Result.add(wAPSTaskPart);
				}
			}

			// ???????????????????????????????????????
			if (wResult.Result != null && wResult.Result.size() > 0) {
				wResult.Result.removeIf(p -> p.ShiftPeriod == APSShiftPeriod.Month.getValue() || p.Active != 1);

				// ?????????????????????????????????
				Calendar wTime = Calendar.getInstance();
				wTime.set(Calendar.HOUR_OF_DAY, 0);
				wTime.set(Calendar.MINUTE, 0);
				wTime.set(Calendar.SECOND, 0);

				wResult.Result.removeIf(
						p -> p.FinishWorkTime.compareTo(wTime) < 0 && p.Status == APSTaskStatus.Done.getValue());

				// ??????
				wResult.Result
						.sort(Comparator.comparing(APSTaskPart::getProductNo).thenComparing(APSTaskPart::getPartNo));

			}

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<Integer> RSM_AutoTurnOrder(BMSEmployee wAdminUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ????????????????????????????????????
			int wShiftID = MESServer.MES_QueryShiftID(wAdminUser, 0, Calendar.getInstance(), APSShiftPeriod.Week, 0);

			List<APSTaskPart> wList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartList(wAdminUser, wShiftID, APSShiftPeriod.Week.getValue())
					.List(APSTaskPart.class);

			// ????????????????????????
			List<APSTaskPart> wTempTaskPartList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartList(wAdminUser,
							new ArrayList<Integer>(
									Arrays.asList(APSTaskStatus.Issued.getValue(), APSTaskStatus.Started.getValue())))
					.List(APSTaskPart.class);
			if (wTempTaskPartList != null && wTempTaskPartList.size() > 0) {
				wList.addAll(wTempTaskPartList);
				// ??????
				wList = new ArrayList<APSTaskPart>(wList.stream()
						.collect(Collectors.toMap(APSTaskPart::getID, account -> account, (k1, k2) -> k2)).values());
			}

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			List<RSMTurnOrderTask> wTempList = null;
			ServiceResult<List<FPCPart>> wIDList = null;
			RSMTurnOrderTask wTask = null;
			for (APSTaskPart wAPSTaskPart : wList) {
				// ???????????????????????????????????????????????????
				if (!IsPartCanAutoTurnOrder(wAdminUser, wAPSTaskPart)) {
					continue;
				}

				wTempList = RSMTurnOrderTaskDAO.getInstance().SelectList(wAdminUser, -1, wAPSTaskPart.OrderID,
						wAPSTaskPart.PartID, -1,
						new ArrayList<Integer>(Arrays.asList(RSMTurnOrderTaskStatus.Auditing.getValue(),
								RSMTurnOrderTaskStatus.Passed.getValue())),
						null, null, wErrorCode);
				if (wTempList.size() > 0) {
					continue;
				}
				wIDList = this.RSM_QueryNextStationList(wAdminUser, wAPSTaskPart.OrderID, wAPSTaskPart.PartID);
				if (wIDList.Result == null || wIDList.Result.size() <= 0) {
					continue;
				}
				for (FPCPart wFPCPart : wIDList.Result) {
					wTask = new RSMTurnOrderTask();
					wTask.ApplyID = -1;
					wTask.ApplyStationID = wAPSTaskPart.PartID;
					wTask.ApplyTime = Calendar.getInstance();
					wTask.ID = 0;
					wTask.OrderID = wAPSTaskPart.OrderID;
					wTask.Status = RSMTurnOrderTaskStatus.Auditing.getValue();
					wTask.TargetStationID = wFPCPart.ID;
					wTask.Type = 2;
					wTask.TaskPartID = wAPSTaskPart.ID;
					wTask.CarNo = wAPSTaskPart.PartNo;
					int wNewID = RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wTask, wErrorCode);
					// ??????????????????????????????????????????????????????????????????
					RSMTurnOrderTask wRSMTurnOrderTask = wTask;
					RSM_SendMessageToMonitorWhenAcApplyTurnOrder(wAdminUser, wNewID, wRSMTurnOrderTask, true);
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
	 * ????????????????????????????????????????????????(??????????????????)
	 * 
	 * @param wAPSTaskPart
	 * @return
	 */
	private boolean IsPartCanAutoTurnOrder(BMSEmployee wLoginUser, APSTaskPart wAPSTaskPart) {
		boolean wResult = false;
		try {
			if (wAPSTaskPart == null || wAPSTaskPart.ID <= 0) {
				return wResult;
			}

			List<APSTaskStep> wList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, -1, -1, wAPSTaskPart.ID, new ArrayList<Integer>())
					.List(APSTaskStep.class);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			if (wList.stream().allMatch(p -> p.Status == APSTaskStatus.Done.getValue())) {
				wResult = true;
			} else {
				wResult = false;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????????????????????????????(????????????????????????????????????)
	 * 
	 * @param wLoginUser   ????????????
	 * @param wAPSTaskPart ????????????
	 * @return ????????????
	 */
	private String JudgeTaskPartIsDone(BMSEmployee wLoginUser, int wAPSTaskPartID) {
		String wResult = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			APSTaskPart wAPSTaskPart = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartByID(wLoginUser, wAPSTaskPartID).Info(APSTaskPart.class);

			if (wAPSTaskPart == null || wAPSTaskPart.ID <= 0) {
				wResult = "??????????????????" + wAPSTaskPartID + "???????????????????????????!";
				return wResult;
			}

			// ??????????????????????????????????????????
			String wRemark = RSM_CheckYJItems(wLoginUser, wAPSTaskPart.OrderID, wAPSTaskPart.PartID);
			if (StringUtils.isNotEmpty(wRemark)) {
				return wRemark;
			}

			// ?????????????????????????????????
			List<APSTaskStep> wStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, -1, -1, wAPSTaskPart.ID, null).List(APSTaskStep.class);

			if (wStepList == null || wStepList.size() <= 0) {
				wRemark = StringUtils.Format("????????????{0}??????????????????????????????!", wAPSTaskPart.PartName);
				return wRemark;
			}

			wStepList = wStepList.stream()
					.filter(p -> p.Status == APSTaskStatus.Issued.getValue()
							|| p.Status == APSTaskStatus.Started.getValue() || p.Status == APSTaskStatus.Done.getValue()
							|| p.Status == APSTaskStatus.Aborted.getValue())
					.collect(Collectors.toList());

			int wPartID = wAPSTaskPart.PartID;
			if (QMSUtils.getInstance().GetYJStationIDList(wLoginUser).stream().anyMatch(p -> p == wPartID)) {
				if (wStepList.stream().anyMatch(p -> p.Status != APSTaskStatus.Done.getValue()
						&& p.Status != APSTaskStatus.Aborted.getValue())) {
					wRemark = StringUtils.Format("????????????{0}???????????????????????????????????????????????????????????????!", wAPSTaskPart.PartName);
					return wRemark;
				}
			}

			k: for (APSTaskStep wAPSTaskStep : wStepList) {
//				List<Object> wObjectList = SFCServiceImpl.getInstance().SFC_QueryToDoAndDoneList(wLoginUser,
//						wAPSTaskStep.ID, SFCTaskType.SpecialCheck.getValue()).Result;
//				if (wObjectList != null && wObjectList.size() > 0) {
//					List<IPTItem> wIPTItemList = (List<IPTItem>) wObjectList.get(0);
//					if (wIPTItemList != null && wIPTItemList.size() > 0) {
//						wRemark += StringUtils.Format("????????????{0}???-???{1}????????????????????????!", wAPSTaskPart.PartName,
//								wAPSTaskStep.StepName);
//						break k;
//					}
//				}
				// ???????????????
				List<SFCTaskIPT> wSpecialTaskList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
						wAPSTaskStep.ID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, null, -1, null, null,
						wErrorCode);
				for (SFCTaskIPT wSFCTaskIPT : wSpecialTaskList) {
					if (wSFCTaskIPT.Status != 2) {
						wRemark += StringUtils.Format("????????????{0}???-???{1}????????????????????????!", wAPSTaskPart.PartName,
								wAPSTaskStep.StepName);
						break k;
					}
				}
			}

			if (StringUtils.isNotEmpty(wRemark)) {
				return wRemark;
			}
			// ??????????????????????????????????????????
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wAPSTaskPart.OrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				wRemark = StringUtils.Format("????????????{0}????????????????????????!", wAPSTaskPart.OrderNo);
				return wRemark;
			}

			Boolean wCheckResult = WDWServiceImpl.getInstance()
					.WDW_IsAllRepairItemClosed(wLoginUser, wOrder.ID, wAPSTaskPart.PartID).Info(Boolean.class);

			if (!wCheckResult) {
				wRemark = StringUtils.Format("????????????{0}???????????????????????????????????????!", wAPSTaskPart.PartName);
				return wRemark;
			}

			// ????????????????????????????????????????????????
			List<NCRTask> wNcrList = RSMTurnOrderTaskDAO.getInstance().SelectNcrList(wLoginUser, wOrder.ID,
					wAPSTaskPart.PartID, wErrorCode);
			wNcrList = wNcrList.stream().filter(p -> p.Status != NCRStatus.Confirmed.getValue())
					.collect(Collectors.toList());
			if (wNcrList.stream().anyMatch(p -> p.Result != 3)) {
				wRemark = StringUtils.Format("????????????{0}???????????????????????????????????????????????????????????????????????????!", wAPSTaskPart.PartName);
				return wRemark;
			}

			if (wNcrList.stream().anyMatch(p -> p.CloseStationID == wAPSTaskPart.PartID)) {
				wRemark = StringUtils.Format("????????????{0}???????????????????????????????????????????????????????????????????????????!", wAPSTaskPart.PartName);
				return wRemark;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1);

			if (wNcrList.stream().anyMatch(
					p -> p.CloseTime.compareTo(wBaseTime) > 0 && p.CloseTime.compareTo(Calendar.getInstance()) < 0)) {
				wRemark = StringUtils.Format("????????????{0}????????????????????????????????????????????????????????????????????????!", wAPSTaskPart.PartName);
				return wRemark;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????
	 */
	@Override
	public ServiceResult<Integer> RSM_AutoPassApply(BMSEmployee wAdminUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<RSMTurnOrderTask> wList = RSMTurnOrderTaskDAO.getInstance().SelectList(wAdminUser, -1, -1, -1, -1,
					new ArrayList<Integer>(Arrays.asList(RSMTurnOrderTaskStatus.Auditing.getValue())), null, null,
					wErrorCode);
			if (wList.size() <= 0) {
				return wResult;
			}

			k: for (RSMTurnOrderTask wSFCTurnOrderTask : wList) {
				// ?????????????????????????????????????????????
				boolean wIsChangeControl = JudgePartIsChangeControl(wAdminUser, wSFCTurnOrderTask);
				if (!wIsChangeControl) {
					wSFCTurnOrderTask.Remark = "?????????????????????";
					PassTurnOrderTask(wAdminUser, wErrorCode, wSFCTurnOrderTask);
					continue;
				} else {
					// ??????????????????????????????
					String wText = this.JudgeTaskPartIsDone(wAdminUser, wSFCTurnOrderTask.TaskPartID);
					if (StringUtils.isNotEmpty(wText)) {
						wSFCTurnOrderTask.Remark = wText;
						RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);
						continue;
					}
//					// ????????????????????????????????????????????????????????????????????????
//					List<Integer> wPrevPartIDList = this.GetPrevChangeControlPartIDList(wAdminUser, wSFCTurnOrderTask);
//					if (wPrevPartIDList == null || wPrevPartIDList.size() <= 0) {
//						PassTurnOrderTask(wAdminUser, wErrorCode, wSFCTurnOrderTask);
//						continue;
//					}
//					// ???????????????????????????????????????????????????
//					List<RSMTurnOrderTask> wList1 = RSMTurnOrderTaskDAO.getInstance().SelectListByPartIDList(wAdminUser,
//							wSFCTurnOrderTask.OrderID, wPrevPartIDList, wErrorCode);
//					if (wList1 == null || wList1.size() <= 0) {
//						PassTurnOrderTask(wAdminUser, wErrorCode, wSFCTurnOrderTask);
//						continue;
//					}
//					// ???????????????????????????????????????????????????
//					for (RSMTurnOrderTask wItem : wList1) {
//						String wRemark = this.JudgeTaskPartIsDone(wAdminUser, wItem.TaskPartID);
//						if (StringUtils.isNotEmpty(wRemark)) {
//							wSFCTurnOrderTask.Remark = wRemark;
//							RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);
//							continue;
//						}
//					}
					// ???????????????????????????????????????????????????
					List<Integer> wControlPartIDList = SFCTaskIPTDAO.getInstance().GetControlPartIDList(wAdminUser,
							wSFCTurnOrderTask.TaskPartID, wSFCTurnOrderTask.ApplyStationID, wErrorCode);
					if (wControlPartIDList != null && wControlPartIDList.size() > 0) {
						for (int wControlPartID : wControlPartIDList) {
							String wCheckRemark = SFCTaskIPTDAO.getInstance().JudgeSpecialTaskIsOk(wAdminUser,
									wSFCTurnOrderTask.OrderID, wControlPartID, wErrorCode);
							if (StringUtils.isNotEmpty(wCheckRemark)) {
								wSFCTurnOrderTask.Remark = wCheckRemark;
								RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);
								continue k;
							}
						}
					}
					// ????????????????????????????????????????????????????????????????????????????????????????????????????????????
					String wCheckResult = RSMTurnOrderTaskDAO.getInstance().CheckLetGo(BaseDAO.SysAdmin,
							wSFCTurnOrderTask, wErrorCode);
					if (StringUtils.isNotEmpty(wCheckResult)) {
						wSFCTurnOrderTask.Remark = wCheckResult;
						RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);
						continue;
					}
					// ?????????????????????????????????
					if (wSFCTurnOrderTask.ConfirmID <= 0) {
						wSFCTurnOrderTask.Remark = StringUtils.Format("???{0}?????????????????????",
								QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID));
						RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);

						// ?????????????????????????????????
						CreateTurnOrderConfirmToDoTaskMessage(wSFCTurnOrderTask);

						continue;
					}
					// ??????????????????????????????????????????????????????
					PassTurnOrderTask(wAdminUser, wErrorCode, wSFCTurnOrderTask);
					continue;
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
	 * ??????????????????????????????
	 */
	private void CreateTurnOrderConfirmToDoTaskMessage(RSMTurnOrderTask wSFCTurnOrderTask) {
		try {
			// ????????????????????????
			List<BMSWorkCharge> wList = QMSConstants.GetBMSWorkChargeList().values().stream()
					.filter(p -> p.Active == 1 && p.StationID == wSFCTurnOrderTask.ApplyStationID)
					.collect(Collectors.toList());
			if (wList == null || wList.size() <= 0) {
				return;
			}
			List<Integer> wUserIDList = new ArrayList<Integer>();
			for (BMSWorkCharge wBMSWorkCharge : wList) {
				for (Integer wUserID : wBMSWorkCharge.CheckerList) {
					if (wUserIDList.stream().anyMatch(p -> p.intValue() == wUserID.intValue())) {
						continue;
					}
					wUserIDList.add(wUserID);
				}
			}
			if (wUserIDList.size() <= 0) {
				return;
			}
			// ?????????????????????????????????????????????????????????????????????
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			boolean wFlag = RSMTurnOrderTaskDAO.getInstance().JudgeIsSendMessage(BaseDAO.SysAdmin, wSFCTurnOrderTask.ID,
					wErrorCode);
			if (wFlag) {
				return;
			}
			// ?????????CarNo
			APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartByID(BaseDAO.SysAdmin, wSFCTurnOrderTask.TaskPartID).Info(APSTaskPart.class);
			if (wTaskPart != null && wTaskPart.ID > 0) {
				wSFCTurnOrderTask.CarNo = wTaskPart.PartNo;
			}

			// ?????????????????????
			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");
			int wShiftID = Integer.parseInt(wSDF.format(Calendar.getInstance().getTime()));
			for (int wUserID : wUserIDList) {
				// ???????????????????????????
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wSFCTurnOrderTask.ID;
				wMessage.Title = StringUtils.Format("{0} {1} {2}", BPMEventModule.SFCTurnOrderConfirm.getLable(),
						wSFCTurnOrderTask.CarNo, QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID));
				wMessage.MessageText = StringUtils.Format("{0}-{1}???{2}????????????????????????????????????????????????????????????", wSFCTurnOrderTask.CarNo,
						QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID),
						QMSConstants.GetFPCPartName(wSFCTurnOrderTask.TargetStationID));
				wMessage.ModuleID = BPMEventModule.SFCTurnOrderConfirm.getValue();
				wMessage.ResponsorID = wUserID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = 0;
				wMessage.Type = BFCMessageType.Task.getValue();
				wBFCMessageList.add(wMessage);
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(BaseDAO.SysAdmin, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????????????????(??????????????????????????????)
	 * 
	 * @param wAdminUser        ????????????
	 * @param wSFCTurnOrderTask ?????????
	 * @return ????????????ID??????
	 */
	@SuppressWarnings("unused")
	private List<Integer> GetPrevChangeControlPartIDList(BMSEmployee wAdminUser, RSMTurnOrderTask wSFCTurnOrderTask) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {

			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderByID(wAdminUser, wSFCTurnOrderTask.OrderID).Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return wResult;
			}
			// ?????????????????????RouteID????????????????????????
			List<FPCRoutePart> wRoutePartList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wOrder.RouteID).collect(Collectors.toList());
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				return wResult;
			}
			// ????????????????????????????????????????????????
			if (!wRoutePartList.stream().anyMatch(p -> p.PartID == wSFCTurnOrderTask.ApplyStationID)) {
				return wResult;
			}
			FPCRoutePart wRoutePart = wRoutePartList.stream().filter(p -> p.PartID == wSFCTurnOrderTask.ApplyStationID)
					.findFirst().get();
			if (wRoutePart == null || wRoutePart.ID <= 0) {
				return wResult;
			}
			// ???????????????????????????????????????????????????????????????????????????
			wResult = GetPrevPartIDList(wRoutePart, wRoutePartList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????ID??????(???????????????????????????)
	 * 
	 * @param wRoutePart     ????????????
	 * @param wRoutePartList ??????????????????
	 * @return ?????????ID??????
	 */
	private List<Integer> GetPrevPartIDList(FPCRoutePart wRoutePart, List<FPCRoutePart> wRoutePartList) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			// ?????????????????????????????????
			if (wRoutePartList.stream().anyMatch(p -> p.PartID == wRoutePart.PrevPartID && p.ChangeControl == 1)) {
				FPCRoutePart wPreRoutePart = wRoutePartList.stream()
						.filter(p -> p.PartID == wRoutePart.PrevPartID && p.ChangeControl == 1).findFirst().get();
				wResult.add(wPreRoutePart.PartID);
				// ??????????????????
//				List<Integer> wList1 = GetPrevPartIDList(wPreRoutePart, wRoutePartList);
//				if (wList1.size() > 0) {
//					wResult.addAll(wList1);
//				}
			}
			// ???????????????????????????????????????
			if (wRoutePartList.stream().anyMatch(p -> p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
					&& p.NextPartIDMap.containsKey(String.valueOf(wRoutePart.PartID)) && p.ChangeControl == 1)) {
				List<FPCRoutePart> wList1 = wRoutePartList.stream()
						.filter(p -> p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
								&& p.NextPartIDMap.containsKey(String.valueOf(wRoutePart.PartID))
								&& p.ChangeControl == 1)
						.collect(Collectors.toList());
				for (FPCRoutePart wItem : wList1) {
					wResult.add(wItem.PartID);
					// ??????????????????
//					List<Integer> wList2 = GetPrevPartIDList(wItem, wRoutePartList);
//					if (wList2.size() > 0) {
//						wResult.addAll(wList2);
//					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????
	 * 
	 * @param wAdminUser        ????????????
	 * @param wErrorCode        ?????????
	 * @param wSFCTurnOrderTask ?????????
	 */
	private void PassTurnOrderTask(BMSEmployee wAdminUser, OutResult<Integer> wErrorCode,
			RSMTurnOrderTask wSFCTurnOrderTask) {
		try {
			// ??????
			wSFCTurnOrderTask.Remark = "";
			wSFCTurnOrderTask.FinishTime = Calendar.getInstance();
			wSFCTurnOrderTask.Status = RSMTurnOrderTaskStatus.Passed.getValue();
			RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);

			// ????????????????????????????????????
			FinishTaskpartTask(wAdminUser, wSFCTurnOrderTask);
			// ????????????????????????????????????????????????????????????
			SendMesssageToMonitorWhenPassed(wAdminUser, wSFCTurnOrderTask);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param wAdminUser
	 * @param wSFCTurnOrderTask
	 */
	private void FinishTaskpartTask(BMSEmployee wAdminUser, RSMTurnOrderTask wSFCTurnOrderTask) {
		try {
			// ?????????????????????
			APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartByID(wAdminUser, wSFCTurnOrderTask.TaskPartID).Info(APSTaskPart.class);
			if (wTaskPart == null || wTaskPart.ID <= 0) {
				return;
			}
			// ?????????????????????ID??????????????????
			List<APSTaskStep> wTaskStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wAdminUser, -1, -1, wSFCTurnOrderTask.TaskPartID, null)
					.List(APSTaskStep.class);
			// ?????????????????????????????????????????????????????????
			if (wTaskStepList.stream().allMatch(
					p -> p.Status == APSTaskStatus.Done.getValue() || p.Status == APSTaskStatus.Aborted.getValue())) {
				wTaskPart.Status = APSTaskStatus.Done.getValue();
				wTaskPart.FinishWorkTime = Calendar.getInstance();
				LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wAdminUser, wTaskPart);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????????????????????????????
	 * 
	 * @param wAdminUser        ????????????
	 * @param wSFCTurnOrderTask ?????????
	 * @return ????????????
	 */
	private boolean JudgePartIsChangeControl(BMSEmployee wAdminUser, RSMTurnOrderTask wSFCTurnOrderTask) {
		boolean wResult = true;
		try {
			// ???????????????

			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wAdminUser, wSFCTurnOrderTask.OrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0 || wOrder.RouteID <= 0) {
				return wResult;
			}
			// ?????????????????????????????????????????????????????????
			List<FPCRoutePart> wRoutePartList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wOrder.RouteID).collect(Collectors.toList());
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				return wResult;
			}
			// ????????????????????????????????????????????????
			if (!wRoutePartList.stream().anyMatch(p -> p.PartID == wSFCTurnOrderTask.ApplyStationID)) {
				return wResult;
			}
			FPCRoutePart wRoutePart = wRoutePartList.stream().filter(p -> p.PartID == wSFCTurnOrderTask.ApplyStationID)
					.findFirst().get();
			if (wRoutePart == null || wRoutePart.ID <= 0) {
				return wResult;
			}
			// ?????????????????????????????????????????????
			if (wRoutePart.ChangeControl != 1) {
				wResult = false;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ????????????????????????????????????????????????????????????
	 * 
	 * @param wAdminUser        MES??????
	 * @param wSFCTurnOrderTask ?????????
	 */
	private void SendMesssageToMonitorWhenPassed(BMSEmployee wAdminUser, RSMTurnOrderTask wSFCTurnOrderTask) {
		try {
			List<BFCMessage> wMessageList = new ArrayList<>();

			OMSOrder wOrder = (OMSOrder) LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderByID(wAdminUser, wSFCTurnOrderTask.OrderID).Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return;
			}

			List<FMCWorkCharge> wApplyWorkCharges = CoreServiceImpl.getInstance()
					.BMS_QueryWorkChargeList(wAdminUser, wSFCTurnOrderTask.ApplyStationID, -1, 1)
					.List(FMCWorkCharge.class);
			if (wApplyWorkCharges != null && wApplyWorkCharges.size() > 0) {

				List<Integer> wMonitorIDList = new ArrayList<>();
				List<BMSEmployee> wClassMemberList = (List<BMSEmployee>) QMSConstants.GetBMSEmployeeList().values()
						.stream().filter(p -> (p.DepartmentID == ((FMCWorkCharge) wApplyWorkCharges.get(0)).ClassID))
						.collect(Collectors.toList());
				if (wClassMemberList != null && wClassMemberList.size() > 0) {
					for (BMSEmployee wBMSEmployee : wClassMemberList) {
						if (wBMSEmployee.Position <= 0) {
							continue;
						}
						if ((QMSConstants.GetBMSPosition(wBMSEmployee.Position)).DutyID == 1) {
							wMonitorIDList.add(Integer.valueOf(wBMSEmployee.ID));
						}
					}
				}
				int wShiftID = MESServer.MES_QueryShiftID(wAdminUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
						FMCShiftLevel.Day);
				BFCMessage wMessage = null;
				for (Integer wMonitorID : wMonitorIDList) {
					wMessage = new BFCMessage();
					wMessage.Active = 0;
					wMessage.CompanyID = 0;
					wMessage.CreateTime = Calendar.getInstance();
					wMessage.EditTime = Calendar.getInstance();
					wMessage.ID = 0L;
					wMessage.MessageID = wSFCTurnOrderTask.ID;
					wMessage.Title = StringUtils.Format("?????? {0} {1}",
							new Object[] { wSFCTurnOrderTask.ApplyStationName, wOrder.PartNo });
					wMessage.MessageText = StringUtils.Format("???{0}??? {1}???????????????{0}??????,??????????????????{2}???,??????????????????{3}???",
							new Object[] { BPMEventModule.TurnOrder.getLable(), "MES??????",
									QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID),
									QMSConstants.GetFPCPartName(wSFCTurnOrderTask.TargetStationID) });
					wMessage.ModuleID = BPMEventModule.TurnOrder.getValue();
					wMessage.ResponsorID = wMonitorID;
					wMessage.ShiftID = wShiftID;
					wMessage.StationID = 0L;
					wMessage.Type = BFCMessageType.Notify.getValue();
					wMessageList.add(wMessage);
				}

			}

			List<FMCWorkCharge> wTargetWorkCharges = CoreServiceImpl.getInstance()
					.BMS_QueryWorkChargeList(wAdminUser, wSFCTurnOrderTask.TargetStationID, -1, 1)
					.List(FMCWorkCharge.class);
			if (wTargetWorkCharges != null && wTargetWorkCharges.size() > 0) {

				List<Integer> wMonitorIDList = new ArrayList<>();
				List<BMSEmployee> wClassMemberList = (List<BMSEmployee>) QMSConstants.GetBMSEmployeeList().values()
						.stream().filter(p -> (p.DepartmentID == ((FMCWorkCharge) wTargetWorkCharges.get(0)).ClassID))
						.collect(Collectors.toList());
				if (wClassMemberList != null && wClassMemberList.size() > 0) {
					for (BMSEmployee wBMSEmployee : wClassMemberList) {
						if (wBMSEmployee.Position <= 0) {
							continue;
						}
						if ((QMSConstants.GetBMSPosition(wBMSEmployee.Position)).DutyID == 1) {
							wMonitorIDList.add(Integer.valueOf(wBMSEmployee.ID));
						}
					}
				}
				int wShiftID = MESServer.MES_QueryShiftID(wAdminUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
						FMCShiftLevel.Day);
				BFCMessage wMessage = null;
				for (Integer wMonitorID : wMonitorIDList) {
					wMessage = new BFCMessage();
					wMessage.Active = 0;
					wMessage.CompanyID = 0;
					wMessage.CreateTime = Calendar.getInstance();
					wMessage.EditTime = Calendar.getInstance();
					wMessage.ID = 0L;
					wMessage.MessageID = wSFCTurnOrderTask.ID;
					wMessage.MessageText = StringUtils.Format("???{0}??? {1}???????????????{0}??????,??????????????????{2}???,??????????????????{3}???",
							new Object[] { BPMEventModule.TurnOrder.getLable(), "MES??????",
									QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID),
									QMSConstants.GetFPCPartName(wSFCTurnOrderTask.TargetStationID) });
					wMessage.ModuleID = BPMEventModule.TurnOrder.getValue();
					wMessage.ResponsorID = wMonitorID;
					wMessage.ShiftID = wShiftID;
					wMessage.StationID = 0L;
					wMessage.Title = StringUtils.Format("???{0}??? {1}???????????????{0}???????????????????????????{2}???,??????????????????{3}???",
							new Object[] { BPMEventModule.TurnOrder.getLable(), "MES??????",
									QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID),
									QMSConstants.GetFPCPartName(wSFCTurnOrderTask.TargetStationID) });
					wMessage.Type = BFCMessageType.Notify.getValue();
					wMessageList.add(wMessage);
				}

			}

			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wAdminUser, wMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * @param wAdminUser
	 * @param wSFCTurnOrderTask
	 */
	@SuppressWarnings("unused")
	private void TriggerQualityDayPlans(BMSEmployee wAdminUser, RSMTurnOrderTask wSFCTurnOrderTask) {
		try {
			// ??????????????????
			List<FPCPart> wQPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(wAdminUser, -1, -1, -1, 3)
					.List(FPCPart.class);
			if (wQPartList == null || wQPartList.size() <= 0) {
				return;
			}
			// ???????????????????????????????????????
			wQPartList = wQPartList.stream().filter(p -> p.QTPartID == wSFCTurnOrderTask.TargetStationID)
					.collect(Collectors.toList());
			if (wQPartList == null || wQPartList.size() <= 0) {
				return;
			}
			// ?????????????????????????????????????????????
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wAdminUser, wSFCTurnOrderTask.OrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return;
			}

			for (FPCPart wFPCPart : wQPartList) {
				List<Integer> wStepIDList = QMSUtils.getInstance().FMC_QueryStepIDList(wAdminUser, wOrder.LineID,
						wFPCPart.ID, wOrder.ProductID);
				if (wStepIDList == null || wStepIDList.size() <= 0) {
					continue;
				}

				// ???????????????????????????????????????(??????????????????)
				List<APSTaskStep> wList = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepList(wAdminUser, wOrder.ID, wFPCPart.ID, -1, null).List(APSTaskStep.class);
				if (wList != null && wList.size() > 0) {
					continue;
				}
				// ?????????????????????
				this.CreateQualityDayPlans(wAdminUser, wStepIDList, wFPCPart, wOrder);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ?????????????????????
	 * 
	 * @param wStepIDList ??????ID??????
	 * @param wFPCPart    ??????
	 * @param wOrder      ??????
	 */
	private void CreateQualityDayPlans(BMSEmployee wAdminUser, List<Integer> wStepIDList, FPCPart wFPCPart,
			OMSOrder wOrder) {
		try {
			int wShiftID = MESServer.MES_QueryShiftID(wAdminUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);
			APSTaskStep wAPSTaskStep;
			for (Integer wStepID : wStepIDList) {
				wAPSTaskStep = new APSTaskStep();
				wAPSTaskStep.ID = 0;
				wAPSTaskStep.Active = 1;
				wAPSTaskStep.CreateTime = Calendar.getInstance();
				wAPSTaskStep.LineID = wOrder.LineID;
				wAPSTaskStep.OrderID = wOrder.ID;
				wAPSTaskStep.PartID = wFPCPart.ID;
				wAPSTaskStep.ProductNo = wOrder.ProductNo;
				wAPSTaskStep.ShiftID = wShiftID;
				wAPSTaskStep.Status = APSTaskStatus.Issued.getValue();
				wAPSTaskStep.StepID = wStepID;
				wAPSTaskStep.IsDispatched = false;
				wAPSTaskStep.PartNo = wOrder.PartNo;
				LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wAdminUser, wAPSTaskStep);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????????????????????????????????????????
	 * 
	 * @param wAdminUser
	 * @param wOrderID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String RSM_CheckYJItems(BMSEmployee wAdminUser, int wOrderID, int wStationID) {
		String wResult = "";
		try {
			List<Integer> wYJIDList = QMSUtils.getInstance().GetYJStationIDList(wAdminUser);
			if (wYJIDList == null || wYJIDList.size() <= 0) {
				return wResult;
			}

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<RSMTurnOrderTask> wList;
			RSMTurnOrderTask wTurnOrderTask;
			List<APSTaskStep> wAPSTaskStepList;
			List<Object> wObjectList;
			for (Integer wYJStatinID : wYJIDList) {
				wList = RSMTurnOrderTaskDAO.getInstance().SelectList(wAdminUser, -1, wOrderID, wYJStatinID, -1, null,
						null, null, wErrorCode);
				if (wList == null || wList.size() <= 0) {
					continue;
				}

				wTurnOrderTask = wList.get(0);

				wAPSTaskStepList = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepList(wAdminUser, -1, -1, wTurnOrderTask.TaskPartID, null)
						.List(APSTaskStep.class);
				if (wAPSTaskStepList == null || wAPSTaskStepList.size() <= 0) {
					continue;
				}

				for (APSTaskStep wAPSTaskStep : wAPSTaskStepList) {
					wObjectList = SFCServiceImpl.getInstance().SFC_QueryToDoAndDoneList(wAdminUser, wAPSTaskStep.ID,
							SFCTaskType.PreCheck.getValue()).Result;
					if (wObjectList == null || wObjectList.size() != 3) {
						continue;
					}

					if (((List<IPTItem>) wObjectList.get(0)) == null
							|| ((List<IPTItem>) wObjectList.get(0)).size() <= 0) {
						continue;
					}

					for (IPTItem wIPTItem : (List<IPTItem>) wObjectList.get(0)) {
						if (wIPTItem.DefaultStationID == wStationID) {
							wResult = StringUtils.Format("???{0}???-???{1}???-???{2}???????????????????????????!",
									QMSConstants.GetFPCPartName(wYJStatinID), wAPSTaskStep.StepName, wIPTItem.Text);
							return wResult;
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
	public ServiceResult<List<FPCPart>> SFC_QueryNextStationList(BMSEmployee wLoginUser, int wOrderID, int wStationID) {
		ServiceResult<List<FPCPart>> wResult = new ServiceResult<List<FPCPart>>();
		try {
			wResult.Result = new ArrayList<FPCPart>();

			APIResult wAPIResult = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID);
			OMSOrder wOMSOrder = wAPIResult.Info(OMSOrder.class);
			if (wOMSOrder == null || wOMSOrder.ID <= 0) {
				return wResult;
			}

//			FPCProductRoute wProductRoute = FMCServiceImpl.getInstance()
//					.FPC_QueryRouteByProduct(wLoginUser, wOMSOrder.LineID, wOMSOrder.ProductID, "")
//					.Info(FPCProductRoute.class);

			List<FPCRoutePart> wTempList = new ArrayList<FPCRoutePart>();
//			if (wProductRoute == null || wProductRoute.ID <= 0) {
//				return wResult;
//			}

			int wRouteID = QMSConstants.GetFPCRoute(wOMSOrder.ProductID, wOMSOrder.LineID, wOMSOrder.CustomerID).ID;

			wAPIResult = FMCServiceImpl.getInstance().FPC_QueryRoutePartListByRouteID(wLoginUser, wRouteID);
			List<FPCRoutePart> wPartList = wAPIResult.List(FPCRoutePart.class);
			wTempList = wPartList.stream().filter(p -> p.PrevPartID == wStationID).collect(Collectors.toList());
			// ????????????
			Map<Integer, FPCPart> wPartMap = QMSConstants.GetFPCPartList();
			// ????????????????????????
			if (wTempList.size() <= 0) {
				Optional<FPCRoutePart> wTempOption = wPartList.stream().filter(p -> p.PartID == wStationID).findFirst();
				if (wTempOption.isPresent()) {
					Map<String, String> wPartIDMap = wTempOption.get().NextPartIDMap;
					if (wPartIDMap != null && wPartIDMap.size() > 0) {
						for (String wPartID : wPartIDMap.keySet()) {
							int wID = Integer.parseInt(wPartID);
							if (wPartMap.containsKey(wID)) {
								wResult.Result.add(wPartMap.get(wID));
							}
						}
					}
				}
			}
			for (FPCRoutePart wFPCRoutePart : wTempList) {
				if (wPartMap.containsKey(wFPCRoutePart.PartID)) {
					wResult.Result.add(wPartMap.get(wFPCRoutePart.PartID));
				}
			}
		}

		catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RSMTurnOrderTask>> RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser, int wOrderID,
			int wApplyStationID, int wTargetStationID, List<Integer> wStateIDList, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<RSMTurnOrderTask>> wResult = new ServiceResult<List<RSMTurnOrderTask>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = RSMTurnOrderTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, wApplyStationID,
					wTargetStationID, wStateIDList, wStartTime, wEndTime, wErrorCode);

			if (wResult.Result != null && wResult.Result.size() > 0) {
				OMSOrder wOMSOrder = null;
				for (RSMTurnOrderTask wRSMTurnOrderTask : wResult.Result) {
					wOMSOrder = LOCOAPSServiceImpl.getInstance()
							.OMS_QueryOrderByID(wLoginUser, wRSMTurnOrderTask.OrderID).Info(OMSOrder.class);
					if (wOMSOrder != null && wOMSOrder.ID > 0) {
						wRSMTurnOrderTask.CarNo = wOMSOrder.PartNo;
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
	public ServiceResult<List<Integer>> RSM_QueryBOPDoneList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<Integer>> wResult = new ServiceResult<List<Integer>>();
		wResult.Result = new ArrayList<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		List<Integer> wDoingList = new ArrayList<Integer>();
		try {
			Map<Integer, Integer> wPartStatusMap = RSMTurnOrderTaskDAO.getInstance().SelectPartStatus(wLoginUser,
					wOrderID, wErrorCode);
			for (int wPartID : wPartStatusMap.keySet()) {
				int wStatus = wPartStatusMap.get(wPartID);
				if (wStatus == 4) {
					wDoingList.add(wPartID);
				} else if (wStatus == 5) {
					wResult.Result.add(wPartID);
				}
			}

//			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
//					.Info(OMSOrder.class);
//
//			if (wOrder == null || wOrder.ID <= 0) {
//				return wResult;
//			}
//
//			List<RSMTurnOrderTask> wList = RSMTurnOrderTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID, -1,
//					-1, new ArrayList<Integer>(Arrays.asList(RSMTurnOrderTaskStatus.Passed.getValue())), null, null,
//					wErrorCode);
//
//			List<RSMTurnOrderTask> wToAuditList = RSMTurnOrderTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID,
//					-1, -1, new ArrayList<Integer>(Arrays.asList(RSMTurnOrderTaskStatus.Auditing.getValue())), null,
//					null, wErrorCode);
//
//			for (RSMTurnOrderTask wRSMTurnOrderTask : wToAuditList) {
//				if (!wDoingList.stream().anyMatch(p -> p.intValue() == wRSMTurnOrderTask.ApplyStationID)) {
//					wDoingList.add(wRSMTurnOrderTask.ApplyStationID);
//				}
//			}
//
//			wResult.Result.addAll(wList.stream().map(p -> p.ApplyStationID).distinct().collect(Collectors.toList()));
//
//			List<APSTaskPart> wTaskList = LOCOAPSServiceImpl.getInstance()
//					.APS_QueryTaskPartAll(wLoginUser, wOrderID, APSShiftPeriod.Week.getValue()).List(APSTaskPart.class);
//			if (wTaskList == null) {
//				wTaskList = new ArrayList<APSTaskPart>();
//			}
//			wTaskList = wTaskList.stream().filter(p -> p.Active == 1 && p.Status == APSTaskStatus.Done.getValue())
//					.collect(Collectors.toList());
//
//			for (RSMTurnOrderTask wRSMTurnOrderTask : wList) {
//				if (wResult.Result.stream().anyMatch(p -> p.intValue() == wRSMTurnOrderTask.TargetStationID)) {
//					continue;
//				}
//				if (wTaskList.stream().anyMatch(p -> p.PartID == wRSMTurnOrderTask.TargetStationID)) {
//					wResult.Result.add(wRSMTurnOrderTask.TargetStationID);
//				} else {
//					wDoingList.add(wRSMTurnOrderTask.TargetStationID);
//				}
//			}

			wResult.CustomResult.put("DoingList", wDoingList);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<RSMTurnOrderTask> RSM_QueryInfo(BMSEmployee wLoginUser, int wID) {
		ServiceResult<RSMTurnOrderTask> wResult = new ServiceResult<RSMTurnOrderTask>();
		wResult.Result = new RSMTurnOrderTask();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = RSMTurnOrderTaskDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RSMTurnOrderTask>> RSM_QueryCheckerConfirmList(BMSEmployee wLoginUser,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RSMTurnOrderTask>> wResult = new ServiceResult<List<RSMTurnOrderTask>>();
		wResult.Result = new ArrayList<RSMTurnOrderTask>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ????????????????????????????????????(?????????)
			List<Integer> wPartIDList = QMSConstants.GetFPCPartList().values().stream()
					.filter(p -> p.CheckerList != null && p.CheckerList.size() > 0 && p.Active == 1
							&& p.CheckerList.stream().anyMatch(q -> q == wLoginUser.ID))
					.map(p -> p.ID).collect(Collectors.toList());
			if (wPartIDList == null || wPartIDList.size() <= 0) {
				return wResult;
			}
			// ???????????????
			wResult.Result = RSMTurnOrderTaskDAO.getInstance().SelectListByChecker(wLoginUser, wPartIDList, wStartTime,
					wEndTime, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> RMS_PostConfirm(BMSEmployee wLoginUser, RSMTurnOrderTask wData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wData.Status == 2) {
				wResult.FaultCode += "?????????????????????????????????????????????!";
				return wResult;
			}

			wData.ConfirmID = wLoginUser.ID;
			wData.ConfirmTime = Calendar.getInstance();
			RSMTurnOrderTaskDAO.getInstance().Update(wLoginUser, wData, wErrorCode);

			// ????????????????????????
			RSMTurnOrderTaskDAO.getInstance().CloseMessage(wLoginUser, wData, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public void RSM_DeleteExport(BMSEmployee wAdminUser) {
		try {
			Calendar wSTime = Calendar.getInstance();
			wSTime.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
					Calendar.getInstance().get(Calendar.DATE), 2, 0, 0);
			Calendar wETime = Calendar.getInstance();
			wETime.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
					Calendar.getInstance().get(Calendar.DATE), 2, 0, 30);

			if (Calendar.getInstance().compareTo(wSTime) > 0 && Calendar.getInstance().compareTo(wETime) < 0) {
				String wDirePath = StringUtils.Format("{0}static/export/",
						new Object[] { Constants.getConfigPath().replace("config/", "") });
				File wFile = new File(wDirePath);
				if (MESFileUtils.deleteDir(wFile)) {
					logger.info("??????????????????export?????????");
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<APSTaskPart>> RSM_QueryAllTaskPartListNew(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime, String wPartNo, int wPartID) {
		ServiceResult<List<APSTaskPart>> wResult = new ServiceResult<List<APSTaskPart>>();
		wResult.Result = new ArrayList<APSTaskPart>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ????????????
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1, 0, 0, 0);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;

			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;

			wStartTime.set(Calendar.HOUR_OF_DAY, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);

			wEndTime.set(Calendar.HOUR_OF_DAY, 23);
			wEndTime.set(Calendar.MINUTE, 59);
			wEndTime.set(Calendar.SECOND, 59);

			if (wStartTime.compareTo(wEndTime) >= 0) {
				return wResult;
			}

			wResult.Result = RSMTurnOrderTaskDAO.getInstance().SelectTaskPartList(wLoginUser, wStartTime, wEndTime, -1,
					wErrorCode);

			List<RSMTurnOrderTask> wAllList = RSMTurnOrderTaskDAO.getInstance().SelectByTaskPartIDList(wLoginUser,
					wResult.Result.stream().map(p -> p.ID).collect(Collectors.toList()), wErrorCode);

			List<RSMTurnOrderTask> wTurnOrderList = null;

			List<Integer> wRouteIDList = wResult.Result.stream().map(p -> p.RouteID).distinct()
					.collect(Collectors.toList());

//			List<Integer> wRouteIDList = RSMTurnOrderTaskDAO.getInstance().SelectRouteIDList(BaseDAO.SysAdmin,
//					wResult.Result.stream().map(p -> p.OrderID).collect(Collectors.toList()), wErrorCode);

			List<FPCRoutePart> wLastPartIDMap = RSMTurnOrderTaskDAO.getInstance().SelectLastPartIDMap(wLoginUser,
					wRouteIDList, wErrorCode);

			for (APSTaskPart wAPSTaskPart : wResult.Result) {
				// ?????????????????????
				wTurnOrderList = wAllList.stream().filter(p -> p.TaskPartID == wAPSTaskPart.ID)
						.collect(Collectors.toList());
				if (wTurnOrderList.size() <= 0) {
					wAPSTaskPart.TaskText = RSMPartTaskStatus.NotTurnOrder.getLable();
					wAPSTaskPart.PartOrder = 3;
				} else {
					if (wTurnOrderList.stream()
							.anyMatch(p -> p.Type == 1 && p.Status == RSMTurnOrderTaskStatus.Auditing.getValue())) {
						wAPSTaskPart.TaskText = RSMPartTaskStatus.AcTurnOrdering.getLable();
						wAPSTaskPart.PartOrder = 1;
					} else if (wTurnOrderList.stream()
							.anyMatch(p -> p.Type == 1 && p.Status == RSMTurnOrderTaskStatus.Passed.getValue())) {
						wAPSTaskPart.TaskText = RSMPartTaskStatus.AcTurnOrdered.getLable();
						wAPSTaskPart.PartOrder = 2;
					} else if (wTurnOrderList.stream()
							.anyMatch(p -> p.Type == 2 && p.Status == RSMTurnOrderTaskStatus.Auditing.getValue())) {
						wAPSTaskPart.TaskText = RSMPartTaskStatus.AutoTurnOrdering.getLable();
						wAPSTaskPart.PartOrder = 1;
					} else if (wTurnOrderList.stream()
							.anyMatch(p -> p.Type == 2 && p.Status == RSMTurnOrderTaskStatus.Passed.getValue())) {
						wAPSTaskPart.TaskText = RSMPartTaskStatus.AutoTurnOrdered.getLable();
						wAPSTaskPart.PartOrder = 2;
					}
				}

				if (!wLastPartIDMap.stream()
						.anyMatch(p -> p.RouteID == wAPSTaskPart.RouteID && p.PartID == wAPSTaskPart.PartID)) {
					continue;
				}

//				if (!(wLastPartIDMap.containsKey(wAPSTaskPart.RouteID)
//						&& wLastPartIDMap.get(wAPSTaskPart.RouteID).stream().anyMatch(p -> p == wAPSTaskPart.PartID))) {
//					continue;
//				}

				if (wAPSTaskPart.Status == 5) {
					wAPSTaskPart.TaskText = "??????";
					wAPSTaskPart.PartOrder = 2;
				} else {
					wAPSTaskPart.TaskText = "?????????";
					wAPSTaskPart.PartOrder = 3;
				}
			}

			// ???????????????????????????????????????
			if (!CoreServiceImpl.getInstance()
					.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 240003, 0, 0)
					.Info(Boolean.class)) {

				List<Integer> wPartIDList = new ArrayList<Integer>();
				// ????????????????????????

				List<Integer> wClassListID = new ArrayList<Integer>();

				// ????????????????????????
				if (wLoginUser.ID > 0 && wLoginUser.DepartmentID > 0
						&& QMSConstants.GetBMSDepartment(wLoginUser.DepartmentID).ID > 0
						&& QMSConstants.GetBMSDepartment(wLoginUser.DepartmentID).Type == BMSDepartmentType.Class
								.getValue()) {
					wClassListID.add(wLoginUser.DepartmentID);
					// ?????????????????????
					List<SCHSecondmentApply> wSecondmentList = LOCOAPSServiceImpl.getInstance()
							.SCH_QuerySecondmentList(wLoginUser, wLoginUser.ID).List(SCHSecondmentApply.class);

					wSecondmentList.removeIf(p -> p.Status != SCHSecondStatus.Seconded.getValue()
							|| p.EndTime.compareTo(Calendar.getInstance()) <= 0
							|| p.StartTime.compareTo(Calendar.getInstance()) > 0);

					for (SCHSecondmentApply schSecondmentApply : wSecondmentList) {
						wClassListID.add(schSecondmentApply.NewClassID);
					}
				}
				if (wClassListID.size() > 0) {
					// ????????????????????????
					List<BMSWorkCharge> wBMSWorkChargeList = QMSConstants.GetBMSWorkChargeList().values().stream()
							.filter(p -> p.Active == 1 && wClassListID.contains(p.ClassID))
							.collect(Collectors.toList());
					if (wBMSWorkChargeList != null && wBMSWorkChargeList.size() > 0) {
						wPartIDList.addAll(wBMSWorkChargeList.stream().map(p -> p.StationID).distinct()
								.collect(Collectors.toList()));
					}
				}
				if (wPartIDList.size() <= 0) {
					wResult.Result.clear();
				} else {
					wResult.Result.removeIf(p -> !wPartIDList.contains(p.PartID));
				}
			}

			// ??????
			if (StringUtils.isNotEmpty(wPartNo)) {
				wResult.Result = wResult.Result.stream().filter(p -> p.PartNo.contains(wPartNo))
						.collect(Collectors.toList());
			}
			if (wPartID > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.PartID == wPartID).collect(Collectors.toList());
			}

			// ??????
			if (wResult.Result != null && wResult.Result.size() > 0) {
				wResult.Result.sort(Comparator.comparing(APSTaskPart::getPartOrder)
						.thenComparing(APSTaskPart::getPartID).thenComparing(APSTaskPart::getPartNo));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RSMTurnOrderTask>> RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser,
			List<Integer> wClassListID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RSMTurnOrderTask>> wResult = new ServiceResult<List<RSMTurnOrderTask>>();
		wResult.Result = new ArrayList<RSMTurnOrderTask>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ????????????

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1, 0, 0, 0);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;

			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;

			wStartTime.set(Calendar.HOUR_OF_DAY, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);

			wEndTime.set(Calendar.HOUR_OF_DAY, 23);
			wEndTime.set(Calendar.MINUTE, 59);
			wEndTime.set(Calendar.SECOND, 59);

			if (wStartTime.compareTo(wEndTime) >= 0) {
				return wResult;
			}

			List<Integer> wPartIDList = new ArrayList<Integer>();
			if (wClassListID != null && wClassListID.size() > 0) {
				// ????????????????????????
				List<BMSWorkCharge> wList = QMSConstants.GetBMSWorkChargeList().values().stream()
						.filter(p -> p.Active == 1 && wClassListID.contains(p.ClassID)).collect(Collectors.toList());
				if (wList == null || wList.size() <= 0) {
					return wResult;
				}
				wPartIDList.addAll(wList.stream().map(p -> p.StationID).distinct().collect(Collectors.toList()));
			}
			// ???????????????????????????
			List<APSTaskPart> wAPSTaskPartList = RSMTurnOrderTaskDAO.getInstance().SelectTaskPartList(wLoginUser,
					wStartTime, wEndTime, 1, wErrorCode);

			// ???????????????????????????
			List<Integer> wTaskPartIDList = new ArrayList<Integer>();
			if (wPartIDList.size() >= 0) {
				wTaskPartIDList = wAPSTaskPartList.stream().filter(p -> wPartIDList.contains(p.PartID)).map(p -> p.ID)
						.collect(Collectors.toList());
			} else {
				wTaskPartIDList = wAPSTaskPartList.stream().map(p -> p.ID).collect(Collectors.toList());
			}

			// ???????????????ID?????????????????????
			wResult.Result = RSMTurnOrderTaskDAO.getInstance().SelectByTaskPartIDList(wLoginUser, wTaskPartIDList,
					wErrorCode);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<RSMTurnOrderTask>> RSM_QueryTurnOrderTaskList(BMSEmployee wLoginUser, int wOperatorID,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<RSMTurnOrderTask>> wResult = new ServiceResult<List<RSMTurnOrderTask>>();
		wResult.Result = new ArrayList<RSMTurnOrderTask>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ????????????

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1, 0, 0, 0);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;

			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;

			if (wStartTime.compareTo(wEndTime) >= 0) {
				return wResult;
			}
			wStartTime.set(Calendar.HOUR_OF_DAY, 0);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);

			wEndTime.set(Calendar.HOUR_OF_DAY, 23);
			wEndTime.set(Calendar.MINUTE, 59);
			wEndTime.set(Calendar.SECOND, 59);

			List<Integer> wPartIDList = new ArrayList<Integer>();
			// ????????????????????????
			if (wOperatorID > 0) {
				List<Integer> wClassListID = new ArrayList<Integer>();

				// ????????????????????????
				if (QMSConstants.GetBMSEmployee(wOperatorID).ID > 0
						&& QMSConstants.GetBMSEmployee(wOperatorID).DepartmentID > 0
						&& QMSConstants.GetBMSDepartment(QMSConstants.GetBMSEmployee(wOperatorID).DepartmentID).ID > 0
						&& QMSConstants.GetBMSDepartment(
								QMSConstants.GetBMSEmployee(wOperatorID).DepartmentID).Type == BMSDepartmentType.Class
										.getValue()) {
					wClassListID.add(QMSConstants.GetBMSEmployee(wOperatorID).DepartmentID);
					// ?????????????????????
					List<SCHSecondmentApply> wSecondmentList = LOCOAPSServiceImpl.getInstance()
							.SCH_QuerySecondmentList(wLoginUser, wOperatorID).List(SCHSecondmentApply.class);

					wSecondmentList.removeIf(p -> p.Status != SCHSecondStatus.Seconded.getValue()
							|| p.EndTime.compareTo(Calendar.getInstance()) <= 0
							|| p.StartTime.compareTo(Calendar.getInstance()) > 0);

					for (SCHSecondmentApply schSecondmentApply : wSecondmentList) {
						wClassListID.add(schSecondmentApply.NewClassID);
					}
				}
				// ????????????????????????
				List<BMSWorkCharge> wList = QMSConstants.GetBMSWorkChargeList().values().stream()
						.filter(p -> p.Active == 1 && wClassListID.contains(p.ClassID)).collect(Collectors.toList());
				if (wList == null || wList.size() <= 0) {
					return wResult;
				}
				wPartIDList.addAll(wList.stream().map(p -> p.StationID).distinct().collect(Collectors.toList()));

			}
			// ???????????????????????????
			List<APSTaskPart> wAPSTaskPartList = RSMTurnOrderTaskDAO.getInstance().SelectTaskPartList(wLoginUser,
					wStartTime, wEndTime, 1, wErrorCode);

			// ???????????????????????????
			List<Integer> wTaskPartIDList = new ArrayList<Integer>();
			if (wPartIDList.size() >= 0) {
				wTaskPartIDList = wAPSTaskPartList.stream().filter(p -> wPartIDList.contains(p.PartID)).map(p -> p.ID)
						.collect(Collectors.toList());
			} else {
				wTaskPartIDList = wAPSTaskPartList.stream().map(p -> p.ID).collect(Collectors.toList());
			}

			// ???????????????ID?????????????????????
			wResult.Result = RSMTurnOrderTaskDAO.getInstance().SelectByTaskPartIDList(wLoginUser, wTaskPartIDList,
					wErrorCode);

			wResult.FaultCode += MESException.getEnumType(wErrorCode.Result).getLable();
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public void RSM_AutoCloseMessage(BMSEmployee adminUser) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			SFCTaskIPTDAO.getInstance().SFC_CloseRelaMessage(adminUser, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

//	@Override
//	public void RSM_AutoFinishTaskPart(BMSEmployee wAdminUser) {
//		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
//		try {
//			if (QMSConstants.mSFCTaskIPTList.size() <= 0) {
//				return;
//			}
//
//			for (SFCTaskIPT wSFCTaskIPT : QMSConstants.mSFCTaskIPTList) {
//				// ?????????????????????????????????????????????????????????
//				List<APSTaskStep> wTaskStepList = SFCTaskIPTDAO.getInstance().SelectTaskStepListByTaskIPT(wAdminUser,
//						wSFCTaskIPT, wErrorCode);
//				// ?????????????????????????????????????????????
//				if (wTaskStepList.stream().allMatch(p -> p.Status == 5)) {
//					// ????????????????????????????????????????????????????????????????????????????????????
//					APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance()
//							.APS_QueryTaskPartByID(wAdminUser, wTaskStepList.get(0).TaskPartID).Info(APSTaskPart.class);
//					if (wTaskPart != null && wTaskPart.ID > 0) {
//						wTaskPart.Status = 5;
//						wTaskPart.FinishWorkTime = Calendar.getInstance();
//						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wAdminUser, wTaskPart);
//					}
//				}
//				// ?????????????????????
//				QMSConstants.mSFCTaskIPTList.removeIf(p -> p.ID == wSFCTaskIPT.ID);
//			}
//		} catch (Exception ex) {
//			logger.error(ex.toString());
//		}
//	}

	@Override
	public void RSM_AutoFinishTaskPart(BMSEmployee wAdminUser, SFCTaskIPT wSFCTaskIPT) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ?????????????????????????????????????????????????????????
			List<APSTaskStep> wTaskStepList = SFCTaskIPTDAO.getInstance().SelectTaskStepListByTaskIPT(wAdminUser,
					wSFCTaskIPT, wErrorCode);
			// ?????????????????????????????????????????????
			if (wTaskStepList.stream().allMatch(p -> p.Status == 5)) {
				// ????????????????????????????????????????????????????????????????????????????????????
				APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskPartByID(wAdminUser, wTaskStepList.get(0).TaskPartID).Info(APSTaskPart.class);
				if (wTaskPart != null && wTaskPart.ID > 0) {
					wTaskPart.Status = 5;
					wTaskPart.FinishWorkTime = Calendar.getInstance();
					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wAdminUser, wTaskPart);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<Integer> APS_TriggerFinalTask(BMSEmployee wLoginUser, String wOrderIDs) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (StringUtils.isEmpty(wOrderIDs)) {
				return wResult;
			}

			// ??????????????????
			List<FPCPart> wQPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(wLoginUser, -1, -1, -1, 3)
					.List(FPCPart.class);
			if (wQPartList == null || wQPartList.size() <= 0) {
				return wResult;
			}

			String[] wIDs = wOrderIDs.split(",");
			for (String wID : wIDs) {
				int wOrderID = Integer.parseInt(wID);
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);
				if (wOrder == null || wOrder.ID <= 0) {
					continue;
				}

				for (FPCPart wFPCPart : wQPartList) {
					List<Integer> wStepIDList = QMSUtils.getInstance().FMC_QueryStepIDList(wLoginUser, wOrder.LineID,
							wFPCPart.ID, wOrder.ProductID);
					if (wStepIDList == null || wStepIDList.size() <= 0) {
						continue;
					}

					// ???????????????????????????????????????(??????????????????)
					List<APSTaskStep> wList = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskStepList(wLoginUser, wOrder.ID, wFPCPart.ID, -1, null)
							.List(APSTaskStep.class);
					if (wList != null && wList.size() > 0) {
						continue;
					}
					// ?????????????????????
					this.CreateQualityDayPlans(wLoginUser, wStepIDList, wFPCPart, wOrder);
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
	public void RSM_AutoCreateTurnOrderForm(BMSEmployee wLoginUser) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ??????????????????????????????????????????
			APSServiceImpl.getInstance().APS_UpdateTaskPartStartWorkTime(wLoginUser);

			List<APSTaskPart> wList = RSMTurnOrderTaskDAO.getInstance().SelectNotGenerateTurnOrderFormList(wLoginUser,
					wErrorCode);
			for (APSTaskPart wAPSTaskPart : wList) {
				ServiceResult<List<FPCPart>> wPartList = RSMServiceImpl.getInstance()
						.RSM_QueryNextStationList(wLoginUser, wAPSTaskPart.OrderID, wAPSTaskPart.PartID);
				for (FPCPart wFPCPart : wPartList.Result) {
					RSMTurnOrderTask wTask = new RSMTurnOrderTask();

					wTask.ID = 0;
					wTask.ApplyID = -100;
					wTask.ApplyTime = wAPSTaskPart.FinishWorkTime;
					wTask.OrderID = wAPSTaskPart.OrderID;
					wTask.ApplyStationID = wAPSTaskPart.PartID;
					wTask.TargetStationID = wFPCPart.ID;
					wTask.Status = 2;
					wTask.Type = 2;
					wTask.TaskPartID = wAPSTaskPart.ID;
					wTask.Remark = "";
					wTask.FinishTime = wAPSTaskPart.FinishWorkTime;
					wTask.ConfirmID = -100;
					wTask.ConfirmTime = Calendar.getInstance();

					RSMTurnOrderTaskDAO.getInstance().Update(wLoginUser, wTask, wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public synchronized void RSM_HandelTechChange(BMSEmployee wLoginUser) {
		try {
//			if (QMSConstants.mTCMTechChangeNotice != null && QMSConstants.mTCMTechChangeNotice.ID > 0) {
//				HandleTechCahnge(wLoginUser, QMSConstants.mTCMTechChangeNotice);
//
//				QMSConstants.mTCMTechChangeNotice = null;
//			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????
	 */
	@Override
	public void HandleTechCahnge(BMSEmployee wLoginUser, TCMTechChangeNotice wTCMTechChangeNotice) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????
			List<TCMMaterialChangeItems> wItemList = TCMMaterialChangeItemsDAO.getInstance().SelectList(wLoginUser, -1,
					wTCMTechChangeNotice.ChangeLogID, -1, wErrorCode);
			// ???????????????
			StepChange(wLoginUser, wItemList, wTCMTechChangeNotice.OrderList, wTCMTechChangeNotice.OrderList,
					wTCMTechChangeNotice.ChangeLogID);
			// ???????????????
			MaterialChange(wLoginUser, wItemList, wTCMTechChangeNotice.OrderList, wTCMTechChangeNotice.ChangeLogID,
					wTCMTechChangeNotice.OrderList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????
	 */
	@Override
	public void HandleTechCahngeTest(BMSEmployee wLoginUser, int wChangeLogID, List<Integer> wOrderIDList) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ??????????????????
			List<TCMMaterialChangeItems> wItemList = TCMMaterialChangeItemsDAO.getInstance().SelectList(wLoginUser, -1,
					wChangeLogID, -1, wErrorCode);
			TCMMaterialChangeLog wLog = TCMMaterialChangeLogDAO.getInstance().SelectByID(wLoginUser, wChangeLogID,
					wErrorCode);
			List<Integer> wSourceList = StringUtils.parseIntList(wLog.OrderIDList.split(","));
			// ???????????????
			StepChange(wLoginUser, wItemList, wSourceList, wOrderIDList, wChangeLogID);
			// ???????????????
			MaterialChange(wLoginUser, wItemList, wSourceList, wChangeLogID, wOrderIDList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????
	 */
	private void StepChange(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList, List<Integer> wOrderList,
			List<Integer> wReworkOrderIDList, int wChangeLogID) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			int wOldRouteID = 0;
			int wNewRouteID = 0;
			// ???????????????ID????????????????????????
			List<String> wPartNoList = TCMMaterialChangeItemsDAO.getInstance().GetPartNoList(wLoginUser, wOrderList,
					wErrorCode);
			// ????????????????????????
			List<TCMMaterialChangeItems> wList = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.StepInsert.getValue()).collect(Collectors.toList());
			List<FPCRoutePartPoint> wAddedList = new ArrayList<FPCRoutePartPoint>();
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList) {
				FPCRoutePartPoint wFPCRoutePartPoint = new FPCRoutePartPoint();
				wFPCRoutePartPoint.PartID = wTCMMaterialChangeItems.PlaceID;
				wFPCRoutePartPoint.PartPointID = wTCMMaterialChangeItems.PartPointID;
				wFPCRoutePartPoint.NewPartID = wTCMMaterialChangeItems.NewPartID;
				wAddedList.add(wFPCRoutePartPoint);

				wOldRouteID = wTCMMaterialChangeItems.RouteID1;
				wNewRouteID = wTCMMaterialChangeItems.RouteID2;
			}
			// ????????????????????????
			List<TCMMaterialChangeItems> wList1 = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.StepDelete.getValue()).collect(Collectors.toList());
			List<FPCRoutePartPoint> wRemovedList = new ArrayList<FPCRoutePartPoint>();
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList1) {
				FPCRoutePartPoint wFPCRoutePartPoint = new FPCRoutePartPoint();
				wFPCRoutePartPoint.PartID = wTCMMaterialChangeItems.PlaceID;
				wFPCRoutePartPoint.PartPointID = wTCMMaterialChangeItems.PartPointID;
				wFPCRoutePartPoint.NewPartID = wTCMMaterialChangeItems.NewPartID;
				wRemovedList.add(wFPCRoutePartPoint);

				wOldRouteID = wTCMMaterialChangeItems.RouteID1;
				wNewRouteID = wTCMMaterialChangeItems.RouteID2;
			}
			// ????????????????????????
			List<TCMMaterialChangeItems> wList2 = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.StepChange.getValue()).collect(Collectors.toList());
			List<FPCRoutePartPoint> wChangedList = new ArrayList<FPCRoutePartPoint>();
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList2) {
				FPCRoutePartPoint wFPCRoutePartPoint = new FPCRoutePartPoint();
				wFPCRoutePartPoint.PartID = wTCMMaterialChangeItems.PlaceID;
				wFPCRoutePartPoint.PartPointID = wTCMMaterialChangeItems.PartPointID;
				wFPCRoutePartPoint.NewPartID = wTCMMaterialChangeItems.NewPartID;
				wChangedList.add(wFPCRoutePartPoint);

				wOldRouteID = wTCMMaterialChangeItems.RouteID1;
				wNewRouteID = wTCMMaterialChangeItems.RouteID2;
			}
			// ????????????????????????
			List<TCMMaterialChangeItems> wList3 = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.StepUpdate.getValue()).collect(Collectors.toList());
			List<FPCRoutePartPoint> wUpdatedList = new ArrayList<FPCRoutePartPoint>();
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList3) {
				FPCRoutePartPoint wFPCRoutePartPoint = new FPCRoutePartPoint();
				wFPCRoutePartPoint.PartID = wTCMMaterialChangeItems.PlaceID;
				wFPCRoutePartPoint.PartPointID = wTCMMaterialChangeItems.PartPointID;
				wFPCRoutePartPoint.NewPartID = wTCMMaterialChangeItems.NewPartID;
				wUpdatedList.add(wFPCRoutePartPoint);

				wOldRouteID = wTCMMaterialChangeItems.RouteID1;
				wNewRouteID = wTCMMaterialChangeItems.RouteID2;
			}
			// ?????????????????????
			if (wOldRouteID > 0 && wNewRouteID > 0) {
				FPCRouteImportServiceImpl.getInstance().FPC_DynamicTurnBop(wLoginUser, wOldRouteID, wAddedList,
						wRemovedList, wChangedList, wPartNoList, wNewRouteID, wReworkOrderIDList, wUpdatedList,
						wChangeLogID);
//				CoreServiceImpl.getInstance().QMS_DynamicTurnBop(wLoginUser, wOldRouteID, wNewRouteID, wAddedList,
//						wRemovedList, wChangedList, wPartNoList, wReworkOrderIDList);
				logger.info("???????????????????????????");
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????
	 */
	private void MaterialChange(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList,
			List<Integer> wOrderList, int wChangeLogID, List<Integer> wReworkOrderList) {
		try {
			// ??????????????????
			MaterialAdd(wLoginUser, wItemList, wOrderList, wChangeLogID, wReworkOrderList);
			// ??????????????????
			MaterialRemove(wLoginUser, wItemList, wOrderList);
			// ????????????????????????
			MaterialNumberChange(wLoginUser, wItemList, wOrderList);
			// ????????????????????????
			MaterialPropertyChange(wLoginUser, wItemList, wOrderList);

			logger.info("????????????????????????");
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????
	 */
	private void MaterialPropertyChange(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList,
			List<Integer> wOrderList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<TCMMaterialChangeItems> wList = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.MaterialPropertyChange.getValue())
					.collect(Collectors.toList());

			// ????????????
			List<APSBOMItem> wBackMaterialList = new ArrayList<APSBOMItem>();

			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList) {
				MSSBOMItem wMSSBOMItem = CloneTool.Clone(wTCMMaterialChangeItems, MSSBOMItem.class);
				// ??????????????????????????????
				if (wTCMMaterialChangeItems.OutsourceType != 1 && wTCMMaterialChangeItems.ReplaceType == 1) {
					AddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
				// ??????????????????????????????
				else if (wTCMMaterialChangeItems.ReplaceType == 2 && wTCMMaterialChangeItems.OutsourceType != 1) {
					DeleteM(wLoginUser, wOrderList, wErrorCode, wBackMaterialList, wTCMMaterialChangeItems,
							wMSSBOMItem);
				}
				// ?????????????????????
				else if (wTCMMaterialChangeItems.ReplaceType == 2 && wTCMMaterialChangeItems.OutsourceType == 1) {
					DeleteAndAddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
				// ????????????????????????
				else if (wTCMMaterialChangeItems.ReplaceType == 0 && wTCMMaterialChangeItems.OutsourceType == 1) {
					AddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
				// ???????????????????????????
				else if (wTCMMaterialChangeItems.ReplaceType == 0 && wTCMMaterialChangeItems.OutsourceType == 2) {
					DeleteM(wLoginUser, wOrderList, wErrorCode, wBackMaterialList, wTCMMaterialChangeItems,
							wMSSBOMItem);
				}
				// ?????????????????????????????????
				else if (wTCMMaterialChangeItems.OldReplaceType == 2 && wTCMMaterialChangeItems.OldOutSourceType != 1
						&& wTCMMaterialChangeItems.ReplaceType == 2 && wTCMMaterialChangeItems.OutsourceType == 1) {
					AddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
				// ??????????????????????????????(???????????????)
				else if (wTCMMaterialChangeItems.OldReplaceType == 1 && wTCMMaterialChangeItems.OldOutSourceType == 3
						&& wTCMMaterialChangeItems.ReplaceType == 1 && wTCMMaterialChangeItems.OldOutSourceType == 0) {
					DeleteAndAddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
			}

			// ??????????????????
			if (wBackMaterialList.size() > 0) {
				APSMaterialReturnDAO.getInstance().APS_CreateBOMItem(wLoginUser, wBackMaterialList, wErrorCode);
			}

			// ?????????????????????????????????
			List<TCMMaterialChangeItems> wMatList = wList.stream().filter(p -> p.ReplaceType == 2)
					.collect(Collectors.toList());
			List<Integer> wStepIDList = wMatList.stream().map(p -> p.PartPointID).distinct()
					.collect(Collectors.toList());
			for (Integer wOrderID : wOrderList) {
				for (Integer wStepID : wStepIDList) {
					int wPartID = wMatList.stream().filter(p -> p.PartPointID == wStepID).findFirst().get().PlaceID;
					boolean wIsFinish = TCMMaterialChangeItemsDAO.getInstance().JudgeIsStepFinish(wLoginUser, wOrderID,
							wPartID, wStepID, wErrorCode);
					if (wIsFinish) {
						// ????????????????????????????????????????????????????????????????????????
						List<TCMMaterialChangeItems> wSendList = wMatList.stream().filter(p -> p.PartPointID == wStepID)
								.collect(Collectors.toList());
						SendMessageToSendNCR(BaseDAO.SysAdmin, wSendList, wOrderID, wPartID, wStepID);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????????????????
	 */
	private void DeleteAndAddM(BMSEmployee wLoginUser, List<Integer> wOrderList, OutResult<Integer> wErrorCode,
			TCMMaterialChangeItems wTCMMaterialChangeItems, MSSBOMItem wMSSBOMItem) {
		try {
			for (int wOrderID : wOrderList) {
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);

				// ????????????
				int wPartID = TCMMaterialChangeItemsDAO.getInstance().SelectPartID(wLoginUser, wOrder.RouteID,
						wTCMMaterialChangeItems.PartPointID, wErrorCode);
				if (wPartID > 0) {
					wMSSBOMItem.PlaceID = wPartID;
				}

				APSBOMItem wBOMItem = new APSBOMItem(wMSSBOMItem, wOrder.LineID, wOrder.ProductID, wOrder.CustomerID,
						wOrderID, wOrder.OrderNo, wOrder.PartNo);

				wBOMItem.SourceType = APSBOMSourceType.BOMChange.getValue();
				wBOMItem.SourceID = wMSSBOMItem.ID;
				wBOMItem.AuditorID = BaseDAO.SysAdmin.ID;
				wBOMItem.EditorID = BaseDAO.SysAdmin.ID;
				wBOMItem.AuditTime = Calendar.getInstance();
				wBOMItem.EditTime = Calendar.getInstance();

				LOCOAPSServiceImpl.getInstance().APS_BOMItemDelete(wLoginUser, wBOMItem);

				LOCOAPSServiceImpl.getInstance().APS_BOMItemUpdate(wLoginUser, wBOMItem);

			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????
	 */
	private void AddM(BMSEmployee wLoginUser, List<Integer> wOrderList, OutResult<Integer> wErrorCode,
			TCMMaterialChangeItems wTCMMaterialChangeItems, MSSBOMItem wMSSBOMItem) {
		try {
			for (int wOrderID : wOrderList) {
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);

				// ????????????
				int wPartID = TCMMaterialChangeItemsDAO.getInstance().SelectPartID(wLoginUser, wOrder.RouteID,
						wTCMMaterialChangeItems.PartPointID, wErrorCode);
				if (wPartID > 0) {
					wMSSBOMItem.PlaceID = wPartID;
				}

				APSBOMItem wBOMItem = new APSBOMItem(wMSSBOMItem, wOrder.LineID, wOrder.ProductID, wOrder.CustomerID,
						wOrderID, wOrder.OrderNo, wOrder.PartNo);

				wBOMItem.SourceType = APSBOMSourceType.BOMChange.getValue();
				wBOMItem.SourceID = wMSSBOMItem.ID;
				wBOMItem.AuditorID = BaseDAO.SysAdmin.ID;
				wBOMItem.EditorID = BaseDAO.SysAdmin.ID;
				wBOMItem.AuditTime = Calendar.getInstance();
				wBOMItem.EditTime = Calendar.getInstance();

				LOCOAPSServiceImpl.getInstance().APS_BOMItemUpdate(wLoginUser, wBOMItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????
	 */
	private void DeleteM(BMSEmployee wLoginUser, List<Integer> wOrderList, OutResult<Integer> wErrorCode,
			List<APSBOMItem> wBackMaterialList, TCMMaterialChangeItems wTCMMaterialChangeItems,
			MSSBOMItem wMSSBOMItem) {
		try {
			for (int wOrderID : wOrderList) {
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);

				// ????????????
				int wPartID = TCMMaterialChangeItemsDAO.getInstance().SelectPartID(wLoginUser, wOrder.RouteID,
						wTCMMaterialChangeItems.PartPointID, wErrorCode);
				if (wPartID > 0) {
					wMSSBOMItem.PlaceID = wPartID;
				}

				APSBOMItem wBOMItem = new APSBOMItem(wMSSBOMItem, wOrder.LineID, wOrder.ProductID, wOrder.CustomerID,
						wOrderID, wOrder.OrderNo, wOrder.PartNo);

				wBOMItem.SourceType = APSBOMSourceType.BOMChange.getValue();
				wBOMItem.SourceID = wMSSBOMItem.ID;
				wBOMItem.AuditorID = BaseDAO.SysAdmin.ID;
				wBOMItem.EditorID = BaseDAO.SysAdmin.ID;
				wBOMItem.AuditTime = Calendar.getInstance();
				wBOMItem.EditTime = Calendar.getInstance();

				// ??????????????????
				boolean wIsFinish = TCMMaterialChangeItemsDAO.getInstance().JudgeIsStepFinish(wLoginUser, wOrderID,
						wTCMMaterialChangeItems.PlaceID, wTCMMaterialChangeItems.PartPointID, wErrorCode);

				// ?????????????????????????????????
				if (wIsFinish) {
				}
				// ????????????????????????????????????
				else {
					wBackMaterialList.add(wBOMItem);
				}

				wBOMItem.ReplaceType = wTCMMaterialChangeItems.OldReplaceType;
				wBOMItem.OutsourceType = wTCMMaterialChangeItems.OldOutSourceType;
				LOCOAPSServiceImpl.getInstance().APS_BOMItemDelete(wLoginUser, wBOMItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????????????????????????????????????????
	 */
	private void SendMessageToSendNCR(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wSendList, Integer wOrderID,
			int wPartID, Integer wStepID) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ?????????????????????ID
			List<Integer> wWorkerList = FPCRouteDAO.getInstance().GetWorkerIDList(BaseDAO.SysAdmin, wOrderID, wPartID,
					wStepID, wErrorCode);
			// ?????????????????????
			List<String> wSList = wSendList.stream().map(p -> p.MaterialNo + p.MaterialName)
					.collect(Collectors.toList());
			String wContent = StringUtils.Format("???{0}???????????????{1}???????????????????????????{2}???", QMSConstants.GetFPCPartName(wPartID),
					QMSConstants.GetFPCStepName(wStepID), StringUtils.Join(",", wSList));
			// ???????????????
			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");
			int wShiftID = Integer.parseInt(wSDF.format(Calendar.getInstance().getTime()));
			for (Integer wUserID : wWorkerList) {
				// ???????????????????????????
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wOrderID;
				wMessage.Title = StringUtils.Format("{0} {1}", BPMEventModule.MaterialChangeNCR.getLable(),
						String.valueOf(wShiftID));
				wMessage.MessageText = wContent;
				wMessage.ModuleID = BPMEventModule.MaterialChangeNCR.getValue();
				wMessage.ResponsorID = wUserID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = wPartID;
				wMessage.Type = BFCMessageType.Task.getValue();
				wMessage.StepID = wStepID;
				wBFCMessageList.add(wMessage);
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????
	 */
	private void MaterialNumberChange(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList,
			List<Integer> wOrderList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<TCMMaterialChangeItems> wList = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.MaterialNumberChange.getValue())
					.collect(Collectors.toList());
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList) {

				// ???????????????????????????????????????SAP
				if (!(wTCMMaterialChangeItems.ReplaceType == 1 || wTCMMaterialChangeItems.OutsourceType == 1)) {
					continue;
				}

				MSSBOMItem wMSSBOMItem = CloneTool.Clone(wTCMMaterialChangeItems, MSSBOMItem.class);
				for (Integer wOrderID : wOrderList) {
					OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
							.Info(OMSOrder.class);

					// ????????????
					int wPartID = TCMMaterialChangeItemsDAO.getInstance().SelectPartID(wLoginUser, wOrder.RouteID,
							wTCMMaterialChangeItems.PartPointID, wErrorCode);
					if (wPartID > 0) {
						wMSSBOMItem.PlaceID = wPartID;
					}

					APSBOMItem wBOMItem = new APSBOMItem(wMSSBOMItem, wOrder.LineID, wOrder.ProductID,
							wOrder.CustomerID, wOrderID, wOrder.OrderNo, wOrder.PartNo);

					wBOMItem.SourceType = APSBOMSourceType.BOMChange.getValue();
					wBOMItem.SourceID = wMSSBOMItem.ID;
					wBOMItem.AuditorID = BaseDAO.SysAdmin.ID;
					wBOMItem.EditorID = BaseDAO.SysAdmin.ID;
					wBOMItem.AuditTime = Calendar.getInstance();
					wBOMItem.EditTime = Calendar.getInstance();

					LOCOAPSServiceImpl.getInstance().APS_BOMItemUpdateProperty(wLoginUser, wBOMItem);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????
	 */
	private void MaterialAdd(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList, List<Integer> wOrderList,
			int wChangeLogID, List<Integer> wReworkOrderList) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<TCMMaterialChangeItems> wAddedList = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.MaterialInsert.getValue()).collect(Collectors.toList());
			// ?????????SAP
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wAddedList) {

				// ???????????????????????????????????????SAP
				if (!(wTCMMaterialChangeItems.ReplaceType == 1 || wTCMMaterialChangeItems.OutsourceType == 1)) {
					continue;
				}

				MSSBOMItem wMSSBOMItem = CloneTool.Clone(wTCMMaterialChangeItems, MSSBOMItem.class);

				AddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
			}
			// ??????????????????????????????
//			SendMessageToMaterialPurchase(wLoginUser, wChangeLogID);
			// ???????????????????????????????????????
			CreateReworkTask(BaseDAO.SysAdmin, wReworkOrderList, wAddedList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ???????????????????????????????????????????????????
	 */
	private void CreateReworkTask(BMSEmployee wLoginUser, List<Integer> wOrderList,
			List<TCMMaterialChangeItems> wAddedList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<Integer> wStepList = wAddedList.stream().map(p -> p.PartPointID).distinct()
					.collect(Collectors.toList());
			for (Integer wOrderID : wOrderList) {
				for (Integer wStepID : wStepList) {
					int wPartID = wAddedList.stream().filter(p -> p.PartPointID == wStepID).findFirst().get().PlaceID;
					// ?????????????????????????????????ID???????????????????????????
					boolean wCheckResult = FPCRouteDAO.getInstance().JudgeStepTaskIsFinished(wLoginUser, wOrderID,
							wPartID, wStepID, wErrorCode);
					// ????????????????????????????????????
					if (wCheckResult) {
						List<TCMMaterialChangeItems> wMList = wAddedList.stream()
								.filter(p -> p.PlaceID == wPartID && p.PartPointID == wStepID)
								.collect(Collectors.toList());
						CreateReworkTask(wLoginUser, wOrderID, wPartID, wStepID, wMList);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????-????????????
	 */
	private void CreateReworkTask(BMSEmployee wLoginUser, Integer wOrderID, int wPartID, int wStepID,
			List<TCMMaterialChangeItems> wMList) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ???????????????????????????ID??????
			List<Integer> wUserIDList = FPCRouteDAO.getInstance().GetMonitorIDList(wLoginUser, wPartID, wErrorCode);
			if (wUserIDList.size() <= 0) {
				return;
			}
			// ???????????????
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return;
			}
			// ???????????????
			APIResult wAPiResult = CoreServiceImpl.getInstance().QMS_StartInstance(wLoginUser, "8209");
			List<BPMActivitiTask> wBPMActivitiTask = wAPiResult.List(BPMActivitiTask.class);
			TCMRework wData = wAPiResult.Custom("data", TCMRework.class);

			List<String> wStList = wMList.stream().map(p -> p.MaterialNo + p.MaterialName).collect(Collectors.toList());

			wData.Content = StringUtils.Format("??????????????????????????????{0}???????????????{1}???????????????????????????{2}?????????????????????????????????????????????",
					QMSConstants.GetFPCPartName(wPartID), QMSConstants.GetFPCStepName(wStepID),
					StringUtils.Join(",", wStList));
			wData.LineID = wOrder.LineID;
			wData.LineName = wOrder.LineName;
			wData.ProductID = wOrder.ProductID;
			wData.ProductNo = wOrder.ProductNo;
			wData.PartNo = wOrder.PartNo;
			wData.CustomerID = wOrder.CustomerID;
			wData.Customer = wOrder.Customer;
			wData.PartID = wPartID;
			wData.PartName = QMSConstants.GetFPCPartName(wPartID);
			wData.StepID = wStepID;
			wData.StepName = QMSConstants.GetFPCStepName(wStepID);
			wData.Status = 1;
			wData.MonitorList = StringUtils.Join(",", wUserIDList);

			// ?????????????????????
			wData.Content_txt_ = wData.Content;
			wData.LineName_txt_ = wData.LineName;
			wData.ProductNo_txt_ = wData.ProductNo;
			wData.PartNo_txt_ = wData.PartNo;
			wData.Customer_txt_ = wData.Customer;
			wData.PartName_txt_ = wData.PartName;
			wData.StepName_txt_ = wData.StepName;

			CoreServiceImpl.getInstance().QMS_CompleteInstance(wLoginUser, wData, wBPMActivitiTask.get(0).ID);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????????????????????????????????????????
	 */
	@SuppressWarnings("unused")
	private void SendMessageToMaterialPurchase(BMSEmployee wLoginUser, int wChangeLogID) {
		try {
			List<BMSRoleItem> wRoleItemList = CoreServiceImpl.getInstance().BMS_UserAll(wLoginUser, 21)
					.List(BMSRoleItem.class);
			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");
			int wShiftID = Integer.parseInt(wSDF.format(Calendar.getInstance().getTime()));
			for (BMSRoleItem wItem : wRoleItemList) {
				// ???????????????????????????
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wChangeLogID;
				wMessage.Title = StringUtils.Format("????????????-???????????? {0}", String.valueOf(wShiftID));
				wMessage.MessageText = StringUtils.Format("?????????????????????????????????????????????????????????");
				wMessage.ModuleID = BPMEventModule.MaterialPurchase.getValue();
				wMessage.ResponsorID = wItem.UserID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = 0;
				wMessage.Type = BFCMessageType.Task.getValue();
				wBFCMessageList.add(wMessage);
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ????????????
	 */
	private void MaterialRemove(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList,
			List<Integer> wOrderList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<TCMMaterialChangeItems> wRemovedList = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.MaterialDelete.getValue()).collect(Collectors.toList());

			// ????????????
			List<APSBOMItem> wBackMaterialList = new ArrayList<APSBOMItem>();

			// ?????????SAP
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wRemovedList) {

				// ???????????????????????????????????????SAP
				if (!(wTCMMaterialChangeItems.ReplaceType == 1 || wTCMMaterialChangeItems.OutsourceType == 1)) {
					continue;
				}

				MSSBOMItem wMSSBOMItem = CloneTool.Clone(wTCMMaterialChangeItems, MSSBOMItem.class);

				DeleteM(wLoginUser, wOrderList, wErrorCode, wBackMaterialList, wTCMMaterialChangeItems, wMSSBOMItem);
			}

			// ????????????????????????
			APSMaterialReturnDAO.getInstance().APS_CreateBOMItem(wLoginUser, wBackMaterialList, wErrorCode);

			// ???????????????????????????????????????
			List<Integer> wStepIDList = wRemovedList.stream().map(p -> p.PartPointID).distinct()
					.collect(Collectors.toList());
			for (Integer wOrderID : wOrderList) {
				for (Integer wStepID : wStepIDList) {
					int wPartID = wRemovedList.stream().filter(p -> p.PartPointID == wStepID).findFirst().get().PlaceID;
					boolean wIsFinish = TCMMaterialChangeItemsDAO.getInstance().JudgeIsStepFinish(wLoginUser, wOrderID,
							wPartID, wStepID, wErrorCode);
					if (wIsFinish) {
						// ????????????????????????????????????????????????????????????????????????
						List<TCMMaterialChangeItems> wSendList = wRemovedList.stream()
								.filter(p -> p.PartPointID == wStepID).collect(Collectors.toList());
						SendMessageToSendNCR(BaseDAO.SysAdmin, wSendList, wOrderID, wPartID, wStepID);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public void RSM_FinishTaskStep(BMSEmployee wLoginUser) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<SFCTaskIPT> wList = RSMTurnOrderTaskDAO.getInstance().SelectRealFinishList(wLoginUser, wErrorCode);
			for (SFCTaskIPT wSFCTaskIPT : wList) {
				RSMTurnOrderTaskDAO.getInstance().UpdateStatus(wLoginUser, wSFCTaskIPT, wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public void RSM_DisableDispatchMessage(BMSEmployee adminUser) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<Integer> wMessageIDList = RSMTurnOrderTaskDAO.getInstance().SelectDisableMessageIDList(adminUser,
					wErrorCode);
			if (wMessageIDList == null || wMessageIDList.size() <= 0) {
				// ????????????????????????????????????
				wMessageIDList = RSMTurnOrderTaskDAO.getInstance().SelectTurnOrderConfirmMessageIDList(adminUser,
						wErrorCode);
				if (wMessageIDList == null || wMessageIDList.size() <= 0) {
					return;
				}
			}

			RSMTurnOrderTaskDAO.getInstance().DisableMessage(adminUser, StringUtils.Join(",", wMessageIDList),
					wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public void RSM_AutoCalculateTrainHistory(BMSEmployee wLoginUser) {
		try {
			// 12???31???23???59???????????????
			SimpleDateFormat wSDF = new SimpleDateFormat("MMddHHmm");
			String wCurrentTime = wSDF.format(Calendar.getInstance().getTime());
			if (!wCurrentTime.equals("12312359")) {
				return;
			}

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			int wYear = Calendar.getInstance().get(Calendar.YEAR);

			Calendar wYearStart = Calendar.getInstance();
			wYearStart.set(wYear, 0, 1, 0, 0, 0);
			Calendar wYearEnd = Calendar.getInstance();
			wYearEnd.set(wYear, 11, 31, 23, 59, 59);

			// ???????????????????????????????????????????????????????????????
			List<FocasHistoryData> wList = FocasHistoryDataDAO.getInstance().SelectList(wLoginUser, -1, wYear,
					wErrorCode);
			if (wList != null && wList.size() > 0) {
				return;
			}

			// ???????????????????????????????????????????????????ID??????
			List<Integer> wOrderIDList = FocasHistoryDataDAO.getInstance().SelectOrderIDList(wLoginUser, wYearStart,
					wYearEnd, wErrorCode);
			// ??????????????????????????????
			FocasHistoryData wFocasHistoryData = FocasHistoryDataDAO.getInstance().CalculateFocasHistoryData(wLoginUser,
					wYearStart, wYearEnd, wErrorCode);
			// ?????????????????????????????????,????????????
			int wSumRepair = 0;
			for (Integer wOrderID : wOrderIDList) {
				wSumRepair += FocasHistoryDataDAO.getInstance().QueryRepairNum(wLoginUser, wOrderID, wErrorCode);
			}
			double wAvg = 0.0;
			if (wSumRepair > 0) {
				wAvg = wSumRepair / wOrderIDList.size();
				wAvg = new BigDecimal(wAvg).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			wFocasHistoryData.setRepairNumber(wAvg);
			// ???????????????
			FocasHistoryDataDAO.getInstance().Update(wLoginUser, wFocasHistoryData, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public void RSM_AutoFinishTaskStep(BMSEmployee wLoginUser) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			Map<Integer, Calendar> wTaskStepMap = RSMTurnOrderTaskDAO.getInstance().GetTaskStepMap(wLoginUser,
					wErrorCode);
			for (int wTaskStepID : wTaskStepMap.keySet()) {
				if (RSMTurnOrderTaskDAO.getInstance().IsAllTaskIPTDone(wLoginUser, wTaskStepID, wErrorCode)) {
					RSMTurnOrderTaskDAO.getInstance().UpdateTaskStepStatus(wLoginUser, wTaskStepID,
							wTaskStepMap.get(wTaskStepID), wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public void RSM_AutoUpdateTaskPart(BMSEmployee wLoginUser) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<Integer> wTaskPartIDList = RSMTurnOrderTaskDAO.getInstance().GetTaskPartIDList(wLoginUser, wErrorCode);
			for (int wTaskPartID : wTaskPartIDList) {
				int wMaxStatus = RSMTurnOrderTaskDAO.getInstance().GetStatus(wLoginUser, wTaskPartID, wErrorCode);
				if (wMaxStatus >= 4)
					wMaxStatus = 4;
				RSMTurnOrderTaskDAO.getInstance().UpdateTaskPart(wLoginUser, wMaxStatus, wTaskPartID, wErrorCode);
				RSMTurnOrderTaskDAO.getInstance().DeleteTurnOrderTask(wLoginUser, wTaskPartID, wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}