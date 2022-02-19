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
			// 工位映射
			Map<Integer, FPCPart> wPartMap = QMSConstants.GetFPCPartList();
			// 找不到下级的情况
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

			// ②保存所有的转序申请单
			for (RSMTurnOrderTask wSFCTurnOrderTask : wSFCTurnOrderTaskList) {
				wResult.Result = RSMTurnOrderTaskDAO.getInstance().Update(wLoginUser, wSFCTurnOrderTask, wErrorCode);
				// 主动申请转序时发送通知消息给对应的工位班长
				RSM_SendMessageToMonitorWhenAcApplyTurnOrder(wLoginUser, wResult.Result, wSFCTurnOrderTask, false);
			}

			// 将工序所有未做自检项以不合格的形式提交给专检
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
					wIPTValue.Remark = "未做自检!";
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
	 * 主动申请转序时发送通知消息给对应的工位班长
	 * 
	 * @param wLoginUser
	 * @param result
	 */
	private void RSM_SendMessageToMonitorWhenAcApplyTurnOrder(BMSEmployee wLoginUser, Integer wNewID,
			RSMTurnOrderTask wSFCTurnOrderTask, boolean wIsAuto) {
		try {
			List<BFCMessage> wBFCMessageList = new ArrayList<BFCMessage>();
			BFCMessage wMessage = null;
			// ①通过申请工位找到申请班组
			FPCPart wFPCPart = QMSConstants.GetFPCPart(wSFCTurnOrderTask.ApplyStationID);
			List<Integer> wChargeClassIDList = wFPCPart.DepartmentIDList;
			if (wFPCPart != null && wFPCPart.ID > 0 && wFPCPart.DepartmentIDList.size() > 0) {
				// ②通过申请班组找到申请班组长
				List<BMSEmployee> wApplyMonitorList = QMSConstants.GetBMSEmployeeList().values().stream()
						.filter(p -> wChargeClassIDList.contains(p.DepartmentID)
								&& QMSConstants.GetBMSPosition(p.Position).DutyID == 1)
						.collect(Collectors.toList());
				int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
						FMCShiftLevel.Day);
				if (wApplyMonitorList != null && wApplyMonitorList.size() > 0) {
					// ③创建发送给申请班组长的通知消息
					for (BMSEmployee wBMSEmployee : wApplyMonitorList) {
						wMessage = new BFCMessage();
						wMessage.Active = 0;
						wMessage.CompanyID = 0;
						wMessage.CreateTime = Calendar.getInstance();
						wMessage.EditTime = Calendar.getInstance();
						wMessage.ID = 0;
						wMessage.MessageID = wNewID;
						if (wIsAuto) {
							wMessage.Title = StringUtils.Format("系统{0} {1} {2}", BPMEventModule.TurnOrder.getLable(),
									wShiftID, wSFCTurnOrderTask.CarNo);
						} else {
							wMessage.Title = StringUtils.Format("人工{0} {1} {2}", BPMEventModule.TurnOrder.getLable(),
									wShiftID, wSFCTurnOrderTask.CarNo);
						}
						wMessage.MessageText = StringUtils.Format("{0} 【{1}】->【{2}】",
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
			// ④通过目标工位找到目标班组
			wFPCPart = QMSConstants.GetFPCPart(wSFCTurnOrderTask.TargetStationID);
			List<Integer> wClassIDList = wFPCPart.DepartmentIDList;
			if (wFPCPart != null && wFPCPart.ID > 0 && wFPCPart.DepartmentIDList.size() > 0) {
				// ⑤通过目标班组找到目标班组长
				List<BMSEmployee> wTargetMonitorList = QMSConstants.GetBMSEmployeeList().values().stream()
						.filter(p -> wClassIDList.contains(p.DepartmentID)
								&& QMSConstants.GetBMSPosition(p.Position).DutyID == 1)
						.collect(Collectors.toList());
				int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
						FMCShiftLevel.Day);
				if (wTargetMonitorList != null && wTargetMonitorList.size() > 0) {
					// ⑥创建发送给目标班组长的通知消息
					for (BMSEmployee wBMSEmployee : wTargetMonitorList) {
						wMessage = new BFCMessage();
						wMessage.Active = 0;
						wMessage.CompanyID = 0;
						wMessage.CreateTime = Calendar.getInstance();
						wMessage.EditTime = Calendar.getInstance();
						wMessage.ID = 0;
						wMessage.MessageID = wNewID;
						if (wIsAuto) {
							wMessage.Title = StringUtils.Format("系统{0} {1} {2}", BPMEventModule.TurnOrder.getLable(),
									wShiftID, wSFCTurnOrderTask.CarNo);
						} else {
							wMessage.Title = StringUtils.Format("人工{0} {1} {2}", BPMEventModule.TurnOrder.getLable(),
									wShiftID, wSFCTurnOrderTask.CarNo);
						}
						wMessage.MessageText = StringUtils.Format("{0} 【{1}】->【{2}】",
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
			// ⑦批量发送消息
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

			// 生成shiftID
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Week, 0);

			List<APSTaskPart> wList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartList(wLoginUser, wShiftID, APSShiftPeriod.Week.getValue())
					.List(APSTaskPart.class);
			// 未完成的工位任务
			List<APSTaskPart> wTempList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartList(wLoginUser,
							new ArrayList<Integer>(Arrays.asList(APSTaskStatus.Issued.getValue(),
									APSTaskStatus.Started.getValue(), APSTaskStatus.Done.getValue())))
					.List(APSTaskPart.class);
			if (wTempList != null && wTempList.size() > 0) {
				wList.addAll(wTempList);
				// 去重
				wList = new ArrayList<APSTaskPart>(wList.stream()
						.collect(Collectors.toMap(APSTaskPart::getID, account -> account, (k1, k2) -> k2)).values());
			}
			if (wList.size() <= 0) {
				return wResult;
			}

			List<RSMTurnOrderTask> wTurnOrderList = null;

			for (APSTaskPart wAPSTaskPart : wList) {
				// 修改转序的状态
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
					wAPSTaskPart.TaskText = "完成";
					continue;
				}
				wAPSTaskPart.TaskText = "未完成";
			}

			// 【全转序工位任务】权限控制
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

			// 去除月计划的或未激活的数据
			if (wResult.Result != null && wResult.Result.size() > 0) {
				wResult.Result.removeIf(p -> p.ShiftPeriod == APSShiftPeriod.Month.getValue() || p.Active != 1);

				// 去除今日之前完成的任务
				Calendar wTime = Calendar.getInstance();
				wTime.set(Calendar.HOUR_OF_DAY, 0);
				wTime.set(Calendar.MINUTE, 0);
				wTime.set(Calendar.SECOND, 0);

				wResult.Result.removeIf(
						p -> p.FinishWorkTime.compareTo(wTime) < 0 && p.Status == APSTaskStatus.Done.getValue());

				// 排序
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
			// ①获取所有已完工的周计划
			int wShiftID = MESServer.MES_QueryShiftID(wAdminUser, 0, Calendar.getInstance(), APSShiftPeriod.Week, 0);

			List<APSTaskPart> wList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartList(wAdminUser, wShiftID, APSShiftPeriod.Week.getValue())
					.List(APSTaskPart.class);

			// 未完成的工位任务
			List<APSTaskPart> wTempTaskPartList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartList(wAdminUser,
							new ArrayList<Integer>(
									Arrays.asList(APSTaskStatus.Issued.getValue(), APSTaskStatus.Started.getValue())))
					.List(APSTaskPart.class);
			if (wTempTaskPartList != null && wTempTaskPartList.size() > 0) {
				wList.addAll(wTempTaskPartList);
				// 去重
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
				// 判断此工位任务是否能自动发起转序单
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
					// 自动转序申请时，发送通知消息给两工位的班组长
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
	 * 判断此工位任务是否能自动发起转序(判断专检任务)
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
	 * 判断某工位任务下的任务是否全部做完(包括不合格评审单和返修单)
	 * 
	 * @param wLoginUser   登录信息
	 * @param wAPSTaskPart 工位任务
	 * @return 提示信息
	 */
	private String JudgeTaskPartIsDone(BMSEmployee wLoginUser, int wAPSTaskPartID) {
		String wResult = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			APSTaskPart wAPSTaskPart = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartByID(wLoginUser, wAPSTaskPartID).Info(APSTaskPart.class);

			if (wAPSTaskPart == null || wAPSTaskPart.ID <= 0) {
				wResult = "提示：主键为" + wAPSTaskPartID + "的工位任务数据缺失!";
				return wResult;
			}

			// 检查预检工位是否有未完成的项
			String wRemark = RSM_CheckYJItems(wLoginUser, wAPSTaskPart.OrderID, wAPSTaskPart.PartID);
			if (StringUtils.isNotEmpty(wRemark)) {
				return wRemark;
			}

			// ①检查专检任务是否做完
			List<APSTaskStep> wStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wLoginUser, -1, -1, wAPSTaskPart.ID, null).List(APSTaskStep.class);

			if (wStepList == null || wStepList.size() <= 0) {
				wRemark = StringUtils.Format("提示：【{0}】该工位没有工序任务!", wAPSTaskPart.PartName);
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
					wRemark = StringUtils.Format("提示：【{0}】该工位存在未完成的工序任务，无法自动转序!", wAPSTaskPart.PartName);
					return wRemark;
				}
			}

			k: for (APSTaskStep wAPSTaskStep : wStepList) {
//				List<Object> wObjectList = SFCServiceImpl.getInstance().SFC_QueryToDoAndDoneList(wLoginUser,
//						wAPSTaskStep.ID, SFCTaskType.SpecialCheck.getValue()).Result;
//				if (wObjectList != null && wObjectList.size() > 0) {
//					List<IPTItem> wIPTItemList = (List<IPTItem>) wObjectList.get(0);
//					if (wIPTItemList != null && wIPTItemList.size() > 0) {
//						wRemark += StringUtils.Format("提示：【{0}】-【{1}】专检任务未完成!", wAPSTaskPart.PartName,
//								wAPSTaskStep.StepName);
//						break k;
//					}
//				}
				// 查询专检单
				List<SFCTaskIPT> wSpecialTaskList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
						wAPSTaskStep.ID, SFCTaskType.SpecialCheck.getValue(), -1, -1, -1, null, -1, null, null,
						wErrorCode);
				for (SFCTaskIPT wSFCTaskIPT : wSpecialTaskList) {
					if (wSFCTaskIPT.Status != 2) {
						wRemark += StringUtils.Format("提示：【{0}】-【{1}】专检任务未完成!", wAPSTaskPart.PartName,
								wAPSTaskStep.StepName);
						break k;
					}
				}
			}

			if (StringUtils.isNotEmpty(wRemark)) {
				return wRemark;
			}
			// ②当前车的所有返修单均已关闭
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wAPSTaskPart.OrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				wRemark = StringUtils.Format("提示：【{0}】该订单数据缺失!", wAPSTaskPart.OrderNo);
				return wRemark;
			}

			Boolean wCheckResult = WDWServiceImpl.getInstance()
					.WDW_IsAllRepairItemClosed(wLoginUser, wOrder.ID, wAPSTaskPart.PartID).Info(Boolean.class);

			if (!wCheckResult) {
				wRemark = StringUtils.Format("提示：【{0}】该工位存在未关闭的返修项!", wAPSTaskPart.PartName);
				return wRemark;
			}

			// ③当前工位的不合格评审单均已关闭
			List<NCRTask> wNcrList = RSMTurnOrderTaskDAO.getInstance().SelectNcrList(wLoginUser, wOrder.ID,
					wAPSTaskPart.PartID, wErrorCode);
			wNcrList = wNcrList.stream().filter(p -> p.Status != NCRStatus.Confirmed.getValue())
					.collect(Collectors.toList());
			if (wNcrList.stream().anyMatch(p -> p.Result != 3)) {
				wRemark = StringUtils.Format("提示：【{0}】该工位存在结果不是让步放行且未确认的不合格评审单!", wAPSTaskPart.PartName);
				return wRemark;
			}

			if (wNcrList.stream().anyMatch(p -> p.CloseStationID == wAPSTaskPart.PartID)) {
				wRemark = StringUtils.Format("提示：【{0}】该工位存在关闭工位是本工位且未确认的不合格评审单!", wAPSTaskPart.PartName);
				return wRemark;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1);

			if (wNcrList.stream().anyMatch(
					p -> p.CloseTime.compareTo(wBaseTime) > 0 && p.CloseTime.compareTo(Calendar.getInstance()) < 0)) {
				wRemark = StringUtils.Format("提示：【{0}】该工位存在关闭时间已过期且未确认的不合格评审单!", wAPSTaskPart.PartName);
				return wRemark;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 自动通过转序单
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
				// ①判断申请工位是否需要转序控制
				boolean wIsChangeControl = JudgePartIsChangeControl(wAdminUser, wSFCTurnOrderTask);
				if (!wIsChangeControl) {
					wSFCTurnOrderTask.Remark = "不做转序控制。";
					PassTurnOrderTask(wAdminUser, wErrorCode, wSFCTurnOrderTask);
					continue;
				} else {
					// 先检查此工位是否做完
					String wText = this.JudgeTaskPartIsDone(wAdminUser, wSFCTurnOrderTask.TaskPartID);
					if (StringUtils.isNotEmpty(wText)) {
						wSFCTurnOrderTask.Remark = wText;
						RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);
						continue;
					}
//					// ①查找申请工位的所有前序，且不转序控制的工位列表
//					List<Integer> wPrevPartIDList = this.GetPrevChangeControlPartIDList(wAdminUser, wSFCTurnOrderTask);
//					if (wPrevPartIDList == null || wPrevPartIDList.size() <= 0) {
//						PassTurnOrderTask(wAdminUser, wErrorCode, wSFCTurnOrderTask);
//						continue;
//					}
//					// ②根据工位列表和订单查找转序单列表
//					List<RSMTurnOrderTask> wList1 = RSMTurnOrderTaskDAO.getInstance().SelectListByPartIDList(wAdminUser,
//							wSFCTurnOrderTask.OrderID, wPrevPartIDList, wErrorCode);
//					if (wList1 == null || wList1.size() <= 0) {
//						PassTurnOrderTask(wAdminUser, wErrorCode, wSFCTurnOrderTask);
//						continue;
//					}
//					// ③遍历转序单，判断工位任务是否做完
//					for (RSMTurnOrderTask wItem : wList1) {
//						String wRemark = this.JudgeTaskPartIsDone(wAdminUser, wItem.TaskPartID);
//						if (StringUtils.isNotEmpty(wRemark)) {
//							wSFCTurnOrderTask.Remark = wRemark;
//							RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);
//							continue;
//						}
//					}
					// ③判断专检管控工位是否全部做完专检
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
					// ④（此申请工位作为关闭工位）判断例外放行单是否关闭，判断放行工序是否完成
					String wCheckResult = RSMTurnOrderTaskDAO.getInstance().CheckLetGo(BaseDAO.SysAdmin,
							wSFCTurnOrderTask, wErrorCode);
					if (StringUtils.isNotEmpty(wCheckResult)) {
						wSFCTurnOrderTask.Remark = wCheckResult;
						RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);
						continue;
					}
					// ③判断检验员是否确认了
					if (wSFCTurnOrderTask.ConfirmID <= 0) {
						wSFCTurnOrderTask.Remark = StringUtils.Format("【{0}】检验员未确认",
								QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID));
						RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);

						// ①生成转序确认待办消息
						CreateTurnOrderConfirmToDoTaskMessage(wSFCTurnOrderTask);

						continue;
					}
					// ④执行到此，说明全部做完，通过转序单
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
	 * 生成转序确认待办消息
	 */
	private void CreateTurnOrderConfirmToDoTaskMessage(RSMTurnOrderTask wSFCTurnOrderTask) {
		try {
			// ①查询工位检验员
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
			// ②根据工位检验员，工位任务查询是否有此消息存在
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			boolean wFlag = RSMTurnOrderTaskDAO.getInstance().JudgeIsSendMessage(BaseDAO.SysAdmin, wSFCTurnOrderTask.ID,
					wErrorCode);
			if (wFlag) {
				return;
			}
			// ①赋值CarNo
			APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartByID(BaseDAO.SysAdmin, wSFCTurnOrderTask.TaskPartID).Info(APSTaskPart.class);
			if (wTaskPart != null && wTaskPart.ID > 0) {
				wSFCTurnOrderTask.CarNo = wTaskPart.PartNo;
			}

			// ③创建待办消息
			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");
			int wShiftID = Integer.parseInt(wSDF.format(Calendar.getInstance().getTime()));
			for (int wUserID : wUserIDList) {
				// 发送任务消息到人员
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wSFCTurnOrderTask.ID;
				wMessage.Title = StringUtils.Format("{0} {1} {2}", BPMEventModule.SFCTurnOrderConfirm.getLable(),
						wSFCTurnOrderTask.CarNo, QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID));
				wMessage.MessageText = StringUtils.Format("{0}-{1}→{2}的工序任务已全部完成，请及时做转序确认。", wSFCTurnOrderTask.CarNo,
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
	 * 获取申请工位前工位(不需要转序控制的工位)
	 * 
	 * @param wAdminUser        登录信息
	 * @param wSFCTurnOrderTask 转序单
	 * @return 前序工位ID集合
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
			// ②根据订单中的RouteID获取工艺工位集合
			List<FPCRoutePart> wRoutePartList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wOrder.RouteID).collect(Collectors.toList());
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				return wResult;
			}
			// ③找到申请工位对应的唯一工艺工位
			if (!wRoutePartList.stream().anyMatch(p -> p.PartID == wSFCTurnOrderTask.ApplyStationID)) {
				return wResult;
			}
			FPCRoutePart wRoutePart = wRoutePartList.stream().filter(p -> p.PartID == wSFCTurnOrderTask.ApplyStationID)
					.findFirst().get();
			if (wRoutePart == null || wRoutePart.ID <= 0) {
				return wResult;
			}
			// ④通过工艺工位递归找到前序所有不需要转序控制的工位
			wResult = GetPrevPartIDList(wRoutePart, wRoutePartList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取前序工位ID集合(需要转序控制的工位)
	 * 
	 * @param wRoutePart     工艺工位
	 * @param wRoutePartList 工艺工位集合
	 * @return 前工位ID集合
	 */
	private List<Integer> GetPrevPartIDList(FPCRoutePart wRoutePart, List<FPCRoutePart> wRoutePartList) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			// ①获取直接前序工艺工位
			if (wRoutePartList.stream().anyMatch(p -> p.PartID == wRoutePart.PrevPartID && p.ChangeControl == 1)) {
				FPCRoutePart wPreRoutePart = wRoutePartList.stream()
						.filter(p -> p.PartID == wRoutePart.PrevPartID && p.ChangeControl == 1).findFirst().get();
				wResult.add(wPreRoutePart.PartID);
				// ①递归获取值
//				List<Integer> wList1 = GetPrevPartIDList(wPreRoutePart, wRoutePartList);
//				if (wList1.size() > 0) {
//					wResult.addAll(wList1);
//				}
			}
			// ②获取间接前序工艺工位集合
			if (wRoutePartList.stream().anyMatch(p -> p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
					&& p.NextPartIDMap.containsKey(String.valueOf(wRoutePart.PartID)) && p.ChangeControl == 1)) {
				List<FPCRoutePart> wList1 = wRoutePartList.stream()
						.filter(p -> p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
								&& p.NextPartIDMap.containsKey(String.valueOf(wRoutePart.PartID))
								&& p.ChangeControl == 1)
						.collect(Collectors.toList());
				for (FPCRoutePart wItem : wList1) {
					wResult.add(wItem.PartID);
					// ①递归获取值
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
	 * 通过转序单
	 * 
	 * @param wAdminUser        系统账户
	 * @param wErrorCode        错误码
	 * @param wSFCTurnOrderTask 转序单
	 */
	private void PassTurnOrderTask(BMSEmployee wAdminUser, OutResult<Integer> wErrorCode,
			RSMTurnOrderTask wSFCTurnOrderTask) {
		try {
			// 通过
			wSFCTurnOrderTask.Remark = "";
			wSFCTurnOrderTask.FinishTime = Calendar.getInstance();
			wSFCTurnOrderTask.Status = RSMTurnOrderTaskStatus.Passed.getValue();
			RSMTurnOrderTaskDAO.getInstance().Update(wAdminUser, wSFCTurnOrderTask, wErrorCode);

			// 转序成功时将工位任务完工
			FinishTaskpartTask(wAdminUser, wSFCTurnOrderTask);
			// 转序成功时发送通知消息给两个工位的班长。
			SendMesssageToMonitorWhenPassed(wAdminUser, wSFCTurnOrderTask);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 转序成功时将工位任务完工
	 * 
	 * @param wAdminUser
	 * @param wSFCTurnOrderTask
	 */
	private void FinishTaskpartTask(BMSEmployee wAdminUser, RSMTurnOrderTask wSFCTurnOrderTask) {
		try {
			// ①获取工位任务
			APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskPartByID(wAdminUser, wSFCTurnOrderTask.TaskPartID).Info(APSTaskPart.class);
			if (wTaskPart == null || wTaskPart.ID <= 0) {
				return;
			}
			// ②根据工位任务ID获取工序任务
			List<APSTaskStep> wTaskStepList = LOCOAPSServiceImpl.getInstance()
					.APS_QueryTaskStepList(wAdminUser, -1, -1, wSFCTurnOrderTask.TaskPartID, null)
					.List(APSTaskStep.class);
			// ③判断所有工序任务完成，若是，更新状态
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
	 * 判断申请工位是否需要转序控制
	 * 
	 * @param wAdminUser        系统账户
	 * @param wSFCTurnOrderTask 转序单
	 * @return 判断结果
	 */
	private boolean JudgePartIsChangeControl(BMSEmployee wAdminUser, RSMTurnOrderTask wSFCTurnOrderTask) {
		boolean wResult = true;
		try {
			// ①获取订单

			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wAdminUser, wSFCTurnOrderTask.OrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0 || wOrder.RouteID <= 0) {
				return wResult;
			}
			// ②通过订单中的工艺路线找到工艺工位列表
			List<FPCRoutePart> wRoutePartList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wOrder.RouteID).collect(Collectors.toList());
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				return wResult;
			}
			// ③通过申请工位找到唯一的工艺工位
			if (!wRoutePartList.stream().anyMatch(p -> p.PartID == wSFCTurnOrderTask.ApplyStationID)) {
				return wResult;
			}
			FPCRoutePart wRoutePart = wRoutePartList.stream().filter(p -> p.PartID == wSFCTurnOrderTask.ApplyStationID)
					.findFirst().get();
			if (wRoutePart == null || wRoutePart.ID <= 0) {
				return wResult;
			}
			// ④判断工艺工位是否需要转序控制
			if (wRoutePart.ChangeControl != 1) {
				wResult = false;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 转序成功时发送通知消息给两个工位的班长。
	 * 
	 * @param wAdminUser        MES系统
	 * @param wSFCTurnOrderTask 转序单
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
					wMessage.Title = StringUtils.Format("转序 {0} {1}",
							new Object[] { wSFCTurnOrderTask.ApplyStationName, wOrder.PartNo });
					wMessage.MessageText = StringUtils.Format("【{0}】 {1}自动通过了{0}申请,发起工位：【{2}】,目标工位：【{3}】",
							new Object[] { BPMEventModule.TurnOrder.getLable(), "MES系统",
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
					wMessage.MessageText = StringUtils.Format("【{0}】 {1}自动通过了{0}申请,发起工位：【{2}】,目标工位：【{3}】",
							new Object[] { BPMEventModule.TurnOrder.getLable(), "MES系统",
									QMSConstants.GetFPCPartName(wSFCTurnOrderTask.ApplyStationID),
									QMSConstants.GetFPCPartName(wSFCTurnOrderTask.TargetStationID) });
					wMessage.ModuleID = BPMEventModule.TurnOrder.getValue();
					wMessage.ResponsorID = wMonitorID;
					wMessage.ShiftID = wShiftID;
					wMessage.StationID = 0L;
					wMessage.Title = StringUtils.Format("【{0}】 {1}自动通过了{0}申请。发起工位：【{2}】,目标工位：【{3}】",
							new Object[] { BPMEventModule.TurnOrder.getLable(), "MES系统",
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
	 * 判断此转序单转序后的工位是否需要触发质量工位的日计划，并生成质量工位日计划
	 * 
	 * @param wAdminUser
	 * @param wSFCTurnOrderTask
	 */
	@SuppressWarnings("unused")
	private void TriggerQualityDayPlans(BMSEmployee wAdminUser, RSMTurnOrderTask wSFCTurnOrderTask) {
		try {
			// 质量工位列表
			List<FPCPart> wQPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(wAdminUser, -1, -1, -1, 3)
					.List(FPCPart.class);
			if (wQPartList == null || wQPartList.size() <= 0) {
				return;
			}
			// 筛选目标工位的触发质量工位
			wQPartList = wQPartList.stream().filter(p -> p.QTPartID == wSFCTurnOrderTask.TargetStationID)
					.collect(Collectors.toList());
			if (wQPartList == null || wQPartList.size() <= 0) {
				return;
			}
			// 遍历质量工位，触发日计划的生成
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

				// 根据订单和工位查询工序任务(避免重复触发)
				List<APSTaskStep> wList = LOCOAPSServiceImpl.getInstance()
						.APS_QueryTaskStepList(wAdminUser, wOrder.ID, wFPCPart.ID, -1, null).List(APSTaskStep.class);
				if (wList != null && wList.size() > 0) {
					continue;
				}
				// 创建质量日计划
				this.CreateQualityDayPlans(wAdminUser, wStepIDList, wFPCPart, wOrder);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 生成质量日计划
	 * 
	 * @param wStepIDList 工序ID集合
	 * @param wFPCPart    工位
	 * @param wOrder      订单
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
	 * 检查该工位之前的预检工位是否有未完成的项
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
							wResult = StringUtils.Format("【{0}】-【{1}】-【{2}】未完成，无法转序!",
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
			// 工位映射
			Map<Integer, FPCPart> wPartMap = QMSConstants.GetFPCPartList();
			// 找不到下级的情况
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
			// ①获取登陆者所管辖的工位(检验员)
			List<Integer> wPartIDList = QMSConstants.GetFPCPartList().values().stream()
					.filter(p -> p.CheckerList != null && p.CheckerList.size() > 0 && p.Active == 1
							&& p.CheckerList.stream().anyMatch(q -> q == wLoginUser.ID))
					.map(p -> p.ID).collect(Collectors.toList());
			if (wPartIDList == null || wPartIDList.size() <= 0) {
				return wResult;
			}
			// ②查询结果
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
				wResult.FaultCode += "提示：该转序单已转序，无法确认!";
				return wResult;
			}

			wData.ConfirmID = wLoginUser.ID;
			wData.ConfirmTime = Calendar.getInstance();
			RSMTurnOrderTaskDAO.getInstance().Update(wLoginUser, wData, wErrorCode);

			// 关闭转序确认消息
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
					logger.info("提示：删除了export文件夹");
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
			// 今日时间
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
				// 修改转序的状态
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
					wAPSTaskPart.TaskText = "完成";
					wAPSTaskPart.PartOrder = 2;
				} else {
					wAPSTaskPart.TaskText = "未完成";
					wAPSTaskPart.PartOrder = 3;
				}
			}

			// 【全转序工位任务】权限控制
			if (!CoreServiceImpl.getInstance()
					.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 240003, 0, 0)
					.Info(Boolean.class)) {

				List<Integer> wPartIDList = new ArrayList<Integer>();
				// 获取本人所在班组

				List<Integer> wClassListID = new ArrayList<Integer>();

				// 获取当前所在班组
				if (wLoginUser.ID > 0 && wLoginUser.DepartmentID > 0
						&& QMSConstants.GetBMSDepartment(wLoginUser.DepartmentID).ID > 0
						&& QMSConstants.GetBMSDepartment(wLoginUser.DepartmentID).Type == BMSDepartmentType.Class
								.getValue()) {
					wClassListID.add(wLoginUser.DepartmentID);
					// 获取所有借调单
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
					// 获取班组对应工位
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

			// 筛选
			if (StringUtils.isNotEmpty(wPartNo)) {
				wResult.Result = wResult.Result.stream().filter(p -> p.PartNo.contains(wPartNo))
						.collect(Collectors.toList());
			}
			if (wPartID > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.PartID == wPartID).collect(Collectors.toList());
			}

			// 排序
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
			// 今日时间

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
				// 获取班组对应工位
				List<BMSWorkCharge> wList = QMSConstants.GetBMSWorkChargeList().values().stream()
						.filter(p -> p.Active == 1 && wClassListID.contains(p.ClassID)).collect(Collectors.toList());
				if (wList == null || wList.size() <= 0) {
					return wResult;
				}
				wPartIDList.addAll(wList.stream().map(p -> p.StationID).distinct().collect(Collectors.toList()));
			}
			// 获取时间段内周计划
			List<APSTaskPart> wAPSTaskPartList = RSMTurnOrderTaskDAO.getInstance().SelectTaskPartList(wLoginUser,
					wStartTime, wEndTime, 1, wErrorCode);

			// 根据工位筛选周计划
			List<Integer> wTaskPartIDList = new ArrayList<Integer>();
			if (wPartIDList.size() >= 0) {
				wTaskPartIDList = wAPSTaskPartList.stream().filter(p -> wPartIDList.contains(p.PartID)).map(p -> p.ID)
						.collect(Collectors.toList());
			} else {
				wTaskPartIDList = wAPSTaskPartList.stream().map(p -> p.ID).collect(Collectors.toList());
			}

			// 根据周计划ID列表查询转序单
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
			// 今日时间

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
			// 获取本人所在班组
			if (wOperatorID > 0) {
				List<Integer> wClassListID = new ArrayList<Integer>();

				// 获取当前所在班组
				if (QMSConstants.GetBMSEmployee(wOperatorID).ID > 0
						&& QMSConstants.GetBMSEmployee(wOperatorID).DepartmentID > 0
						&& QMSConstants.GetBMSDepartment(QMSConstants.GetBMSEmployee(wOperatorID).DepartmentID).ID > 0
						&& QMSConstants.GetBMSDepartment(
								QMSConstants.GetBMSEmployee(wOperatorID).DepartmentID).Type == BMSDepartmentType.Class
										.getValue()) {
					wClassListID.add(QMSConstants.GetBMSEmployee(wOperatorID).DepartmentID);
					// 获取所有借调单
					List<SCHSecondmentApply> wSecondmentList = LOCOAPSServiceImpl.getInstance()
							.SCH_QuerySecondmentList(wLoginUser, wOperatorID).List(SCHSecondmentApply.class);

					wSecondmentList.removeIf(p -> p.Status != SCHSecondStatus.Seconded.getValue()
							|| p.EndTime.compareTo(Calendar.getInstance()) <= 0
							|| p.StartTime.compareTo(Calendar.getInstance()) > 0);

					for (SCHSecondmentApply schSecondmentApply : wSecondmentList) {
						wClassListID.add(schSecondmentApply.NewClassID);
					}
				}
				// 获取班组对应工位
				List<BMSWorkCharge> wList = QMSConstants.GetBMSWorkChargeList().values().stream()
						.filter(p -> p.Active == 1 && wClassListID.contains(p.ClassID)).collect(Collectors.toList());
				if (wList == null || wList.size() <= 0) {
					return wResult;
				}
				wPartIDList.addAll(wList.stream().map(p -> p.StationID).distinct().collect(Collectors.toList()));

			}
			// 获取时间段内周计划
			List<APSTaskPart> wAPSTaskPartList = RSMTurnOrderTaskDAO.getInstance().SelectTaskPartList(wLoginUser,
					wStartTime, wEndTime, 1, wErrorCode);

			// 根据工位筛选周计划
			List<Integer> wTaskPartIDList = new ArrayList<Integer>();
			if (wPartIDList.size() >= 0) {
				wTaskPartIDList = wAPSTaskPartList.stream().filter(p -> wPartIDList.contains(p.PartID)).map(p -> p.ID)
						.collect(Collectors.toList());
			} else {
				wTaskPartIDList = wAPSTaskPartList.stream().map(p -> p.ID).collect(Collectors.toList());
			}

			// 根据周计划ID列表查询转序单
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
//				// ①根据预检单获取该车该工位所有工序任务
//				List<APSTaskStep> wTaskStepList = SFCTaskIPTDAO.getInstance().SelectTaskStepListByTaskIPT(wAdminUser,
//						wSFCTaskIPT, wErrorCode);
//				// ②判断是否所有的工序任务都完工
//				if (wTaskStepList.stream().allMatch(p -> p.Status == 5)) {
//					// ③若是，则获取工位任务，并修改工位任务的状态、和完工时间
//					APSTaskPart wTaskPart = LOCOAPSServiceImpl.getInstance()
//							.APS_QueryTaskPartByID(wAdminUser, wTaskStepList.get(0).TaskPartID).Info(APSTaskPart.class);
//					if (wTaskPart != null && wTaskPart.ID > 0) {
//						wTaskPart.Status = 5;
//						wTaskPart.FinishWorkTime = Calendar.getInstance();
//						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskPart(wAdminUser, wTaskPart);
//					}
//				}
//				// ④剔除此预检单
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
			// ①根据预检单获取该车该工位所有工序任务
			List<APSTaskStep> wTaskStepList = SFCTaskIPTDAO.getInstance().SelectTaskStepListByTaskIPT(wAdminUser,
					wSFCTaskIPT, wErrorCode);
			// ②判断是否所有的工序任务都完工
			if (wTaskStepList.stream().allMatch(p -> p.Status == 5)) {
				// ③若是，则获取工位任务，并修改工位任务的状态、和完工时间
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

			// 质量工位列表
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

					// 根据订单和工位查询工序任务(避免重复触发)
					List<APSTaskStep> wList = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskStepList(wLoginUser, wOrder.ID, wFPCPart.ID, -1, null)
							.List(APSTaskStep.class);
					if (wList != null && wList.size() > 0) {
						continue;
					}
					// 创建质量日计划
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

			// ①自动维护工位任务的开工时间
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
	 * 处理工艺变更
	 */
	@Override
	public void HandleTechCahnge(BMSEmployee wLoginUser, TCMTechChangeNotice wTCMTechChangeNotice) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ①查询变更单
			List<TCMMaterialChangeItems> wItemList = TCMMaterialChangeItemsDAO.getInstance().SelectList(wLoginUser, -1,
					wTCMTechChangeNotice.ChangeLogID, -1, wErrorCode);
			// ②工序变更
			StepChange(wLoginUser, wItemList, wTCMTechChangeNotice.OrderList, wTCMTechChangeNotice.OrderList,
					wTCMTechChangeNotice.ChangeLogID);
			// ③物料变更
			MaterialChange(wLoginUser, wItemList, wTCMTechChangeNotice.OrderList, wTCMTechChangeNotice.ChangeLogID,
					wTCMTechChangeNotice.OrderList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 测试工艺变更
	 */
	@Override
	public void HandleTechCahngeTest(BMSEmployee wLoginUser, int wChangeLogID, List<Integer> wOrderIDList) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ①查询变更单
			List<TCMMaterialChangeItems> wItemList = TCMMaterialChangeItemsDAO.getInstance().SelectList(wLoginUser, -1,
					wChangeLogID, -1, wErrorCode);
			TCMMaterialChangeLog wLog = TCMMaterialChangeLogDAO.getInstance().SelectByID(wLoginUser, wChangeLogID,
					wErrorCode);
			List<Integer> wSourceList = StringUtils.parseIntList(wLog.OrderIDList.split(","));
			// ②工序变更
			StepChange(wLoginUser, wItemList, wSourceList, wOrderIDList, wChangeLogID);
			// ③物料变更
			MaterialChange(wLoginUser, wItemList, wSourceList, wChangeLogID, wOrderIDList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 工序变更
	 */
	private void StepChange(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList, List<Integer> wOrderList,
			List<Integer> wReworkOrderIDList, int wChangeLogID) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			int wOldRouteID = 0;
			int wNewRouteID = 0;
			// ①根据订单ID集合获取车号集合
			List<String> wPartNoList = TCMMaterialChangeItemsDAO.getInstance().GetPartNoList(wLoginUser, wOrderList,
					wErrorCode);
			// ②获取新增的工序
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
			// ③获取删除的工序
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
			// ④获取变更的工序
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
			// ⑤获取修改的工序
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
			// ⑤调用接口处理
			if (wOldRouteID > 0 && wNewRouteID > 0) {
				FPCRouteImportServiceImpl.getInstance().FPC_DynamicTurnBop(wLoginUser, wOldRouteID, wAddedList,
						wRemovedList, wChangedList, wPartNoList, wNewRouteID, wReworkOrderIDList, wUpdatedList,
						wChangeLogID);
//				CoreServiceImpl.getInstance().QMS_DynamicTurnBop(wLoginUser, wOldRouteID, wNewRouteID, wAddedList,
//						wRemovedList, wChangedList, wPartNoList, wReworkOrderIDList);
				logger.info("工序变更调整完成。");
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 物料变更
	 */
	private void MaterialChange(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList,
			List<Integer> wOrderList, int wChangeLogID, List<Integer> wReworkOrderList) {
		try {
			// ①新增的物料
			MaterialAdd(wLoginUser, wItemList, wOrderList, wChangeLogID, wReworkOrderList);
			// ②删除的物料
			MaterialRemove(wLoginUser, wItemList, wOrderList);
			// ③数量变更的物料
			MaterialNumberChange(wLoginUser, wItemList, wOrderList);
			// ④属性变更的物料
			MaterialPropertyChange(wLoginUser, wItemList, wOrderList);

			logger.info("物料变更调整完成");
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 物料属性变更
	 */
	private void MaterialPropertyChange(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList,
			List<Integer> wOrderList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<TCMMaterialChangeItems> wList = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.MaterialPropertyChange.getValue())
					.collect(Collectors.toList());

			// 退料列表
			List<APSBOMItem> wBackMaterialList = new ArrayList<APSBOMItem>();

			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList) {
				MSSBOMItem wMSSBOMItem = CloneTool.Clone(wTCMMaterialChangeItems, MSSBOMItem.class);
				// 偶换变必换，新增物料
				if (wTCMMaterialChangeItems.OutsourceType != 1 && wTCMMaterialChangeItems.ReplaceType == 1) {
					AddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
				// 必换变偶换，删除物料
				else if (wTCMMaterialChangeItems.ReplaceType == 2 && wTCMMaterialChangeItems.OutsourceType != 1) {
					DeleteM(wLoginUser, wOrderList, wErrorCode, wBackMaterialList, wTCMMaterialChangeItems,
							wMSSBOMItem);
				}
				// 必换变委外必修
				else if (wTCMMaterialChangeItems.ReplaceType == 2 && wTCMMaterialChangeItems.OutsourceType == 1) {
					DeleteAndAddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
				// 偶换件变委外必修
				else if (wTCMMaterialChangeItems.ReplaceType == 0 && wTCMMaterialChangeItems.OutsourceType == 1) {
					AddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
				// 委外必修变委外偶修
				else if (wTCMMaterialChangeItems.ReplaceType == 0 && wTCMMaterialChangeItems.OutsourceType == 2) {
					DeleteM(wLoginUser, wOrderList, wErrorCode, wBackMaterialList, wTCMMaterialChangeItems,
							wMSSBOMItem);
				}
				// 偶换变偶换、委外必修件
				else if (wTCMMaterialChangeItems.OldReplaceType == 2 && wTCMMaterialChangeItems.OldOutSourceType != 1
						&& wTCMMaterialChangeItems.ReplaceType == 2 && wTCMMaterialChangeItems.OutsourceType == 1) {
					AddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
				// 必换、自修必修变必换(删除、更新)
				else if (wTCMMaterialChangeItems.OldReplaceType == 1 && wTCMMaterialChangeItems.OldOutSourceType == 3
						&& wTCMMaterialChangeItems.ReplaceType == 1 && wTCMMaterialChangeItems.OldOutSourceType == 0) {
					DeleteAndAddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
				}
			}

			// 保存退料列表
			if (wBackMaterialList.size() > 0) {
				APSMaterialReturnDAO.getInstance().APS_CreateBOMItem(wLoginUser, wBackMaterialList, wErrorCode);
			}

			// ①处置偶换变必换的情况
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
						// ①发送待办消息给此工序的作业人员，发起不合格评审
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
	 * 删除、然后新增物料
	 */
	private void DeleteAndAddM(BMSEmployee wLoginUser, List<Integer> wOrderList, OutResult<Integer> wErrorCode,
			TCMMaterialChangeItems wTCMMaterialChangeItems, MSSBOMItem wMSSBOMItem) {
		try {
			for (int wOrderID : wOrderList) {
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);

				// 修改工位
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
	 * 新增物料
	 */
	private void AddM(BMSEmployee wLoginUser, List<Integer> wOrderList, OutResult<Integer> wErrorCode,
			TCMMaterialChangeItems wTCMMaterialChangeItems, MSSBOMItem wMSSBOMItem) {
		try {
			for (int wOrderID : wOrderList) {
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);

				// 修改工位
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
	 * 删除物料
	 */
	private void DeleteM(BMSEmployee wLoginUser, List<Integer> wOrderList, OutResult<Integer> wErrorCode,
			List<APSBOMItem> wBackMaterialList, TCMMaterialChangeItems wTCMMaterialChangeItems,
			MSSBOMItem wMSSBOMItem) {
		try {
			for (int wOrderID : wOrderList) {
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);

				// 修改工位
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

				// 工序是否完工
				boolean wIsFinish = TCMMaterialChangeItemsDAO.getInstance().JudgeIsStepFinish(wLoginUser, wOrderID,
						wTCMMaterialChangeItems.PlaceID, wTCMMaterialChangeItems.PartPointID, wErrorCode);

				// 已完工，发起不合格评审
				if (wIsFinish) {
				}
				// 未完工，生成退库物料清单
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
	 * 发送消息给作业人员执行不合格评审
	 */
	private void SendMessageToSendNCR(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wSendList, Integer wOrderID,
			int wPartID, Integer wStepID) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①获取作业人员ID
			List<Integer> wWorkerList = FPCRouteDAO.getInstance().GetWorkerIDList(BaseDAO.SysAdmin, wOrderID, wPartID,
					wStepID, wErrorCode);
			// ②拼接消息内容
			List<String> wSList = wSendList.stream().map(p -> p.MaterialNo + p.MaterialName)
					.collect(Collectors.toList());
			String wContent = StringUtils.Format("【{0}】工位，【{1}】工序，删除物料【{2}】", QMSConstants.GetFPCPartName(wPartID),
					QMSConstants.GetFPCStepName(wStepID), StringUtils.Join(",", wSList));
			// ③发送消息
			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");
			int wShiftID = Integer.parseInt(wSDF.format(Calendar.getInstance().getTime()));
			for (Integer wUserID : wWorkerList) {
				// 发送任务消息到人员
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
	 * 物料数量变更
	 */
	private void MaterialNumberChange(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList,
			List<Integer> wOrderList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<TCMMaterialChangeItems> wList = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.MaterialNumberChange.getValue())
					.collect(Collectors.toList());
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList) {

				// 必换件或委外必修件才推送给SAP
				if (!(wTCMMaterialChangeItems.ReplaceType == 1 || wTCMMaterialChangeItems.OutsourceType == 1)) {
					continue;
				}

				MSSBOMItem wMSSBOMItem = CloneTool.Clone(wTCMMaterialChangeItems, MSSBOMItem.class);
				for (Integer wOrderID : wOrderList) {
					OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
							.Info(OMSOrder.class);

					// 修改工位
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
	 * 物料新增
	 */
	private void MaterialAdd(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList, List<Integer> wOrderList,
			int wChangeLogID, List<Integer> wReworkOrderList) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<TCMMaterialChangeItems> wAddedList = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.MaterialInsert.getValue()).collect(Collectors.toList());
			// 推送给SAP
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wAddedList) {

				// 必换件或委外必修件才推送给SAP
				if (!(wTCMMaterialChangeItems.ReplaceType == 1 || wTCMMaterialChangeItems.OutsourceType == 1)) {
					continue;
				}

				MSSBOMItem wMSSBOMItem = CloneTool.Clone(wTCMMaterialChangeItems, MSSBOMItem.class);

				AddM(wLoginUser, wOrderList, wErrorCode, wTCMMaterialChangeItems, wMSSBOMItem);
			}
			// 发送给物流采购部采购
//			SendMessageToMaterialPurchase(wLoginUser, wChangeLogID);
			// ①工序已完工的生成返工任务
			CreateReworkTask(BaseDAO.SysAdmin, wReworkOrderList, wAddedList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 新增物料，工序已完工，生成返工任务
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
					// ①根据工位、工序、订单ID查询工序是否已完工
					boolean wCheckResult = FPCRouteDAO.getInstance().JudgeStepTaskIsFinished(wLoginUser, wOrderID,
							wPartID, wStepID, wErrorCode);
					// ②已完工工序生成返工任务
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
	 * 创建工艺变更-返工任务
	 */
	private void CreateReworkTask(BMSEmployee wLoginUser, Integer wOrderID, int wPartID, int wStepID,
			List<TCMMaterialChangeItems> wMList) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ①获取工位的班组长ID集合
			List<Integer> wUserIDList = FPCRouteDAO.getInstance().GetMonitorIDList(wLoginUser, wPartID, wErrorCode);
			if (wUserIDList.size() <= 0) {
				return;
			}
			// ②获取订单
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return;
			}
			// ③创建任务
			APIResult wAPiResult = CoreServiceImpl.getInstance().QMS_StartInstance(wLoginUser, "8209");
			List<BPMActivitiTask> wBPMActivitiTask = wAPiResult.List(BPMActivitiTask.class);
			TCMRework wData = wAPiResult.Custom("data", TCMRework.class);

			List<String> wStList = wMList.stream().map(p -> p.MaterialNo + p.MaterialName).collect(Collectors.toList());

			wData.Content = StringUtils.Format("工艺内容发生变更，【{0}】工位，【{1}】工序，新增物料【{2}】，系统自动发起工艺变更返工。",
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

			// ①辅助属性赋值
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
	 * 发送消息给物流采购部执行采购
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
				// 发送任务消息到人员
				wMessage = new BFCMessage();
				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wChangeLogID;
				wMessage.Title = StringUtils.Format("工艺变更-新增物料 {0}", String.valueOf(wShiftID));
				wMessage.MessageText = StringUtils.Format("工艺新增了物料，请查看详情，及时采购。");
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
	 * 物料删除
	 */
	private void MaterialRemove(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList,
			List<Integer> wOrderList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<TCMMaterialChangeItems> wRemovedList = wItemList.stream()
					.filter(p -> p.ChangeType == TCMChangeType.MaterialDelete.getValue()).collect(Collectors.toList());

			// 退料列表
			List<APSBOMItem> wBackMaterialList = new ArrayList<APSBOMItem>();

			// 推送给SAP
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wRemovedList) {

				// 必换件或委外必修件才推送给SAP
				if (!(wTCMMaterialChangeItems.ReplaceType == 1 || wTCMMaterialChangeItems.OutsourceType == 1)) {
					continue;
				}

				MSSBOMItem wMSSBOMItem = CloneTool.Clone(wTCMMaterialChangeItems, MSSBOMItem.class);

				DeleteM(wLoginUser, wOrderList, wErrorCode, wBackMaterialList, wTCMMaterialChangeItems, wMSSBOMItem);
			}

			// 保存退料列表数据
			APSMaterialReturnDAO.getInstance().APS_CreateBOMItem(wLoginUser, wBackMaterialList, wErrorCode);

			// ①处理需发起不合格评审物料
			List<Integer> wStepIDList = wRemovedList.stream().map(p -> p.PartPointID).distinct()
					.collect(Collectors.toList());
			for (Integer wOrderID : wOrderList) {
				for (Integer wStepID : wStepIDList) {
					int wPartID = wRemovedList.stream().filter(p -> p.PartPointID == wStepID).findFirst().get().PlaceID;
					boolean wIsFinish = TCMMaterialChangeItemsDAO.getInstance().JudgeIsStepFinish(wLoginUser, wOrderID,
							wPartID, wStepID, wErrorCode);
					if (wIsFinish) {
						// ①发送待办消息给此工序的作业人员，发起不合格评审
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
				// 查询未关闭的转序确认消息
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
			// 12月31日23时59分开始统计
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

			// ①根据年份查询是否有历史数据，若有，不统计
			List<FocasHistoryData> wList = FocasHistoryDataDAO.getInstance().SelectList(wLoginUser, -1, wYear,
					wErrorCode);
			if (wList != null && wList.size() > 0) {
				return;
			}

			// ②查询所有实际完工时间在今年的订单ID集合
			List<Integer> wOrderIDList = FocasHistoryDataDAO.getInstance().SelectOrderIDList(wLoginUser, wYearStart,
					wYearEnd, wErrorCode);
			// ③查询完工、停时数据
			FocasHistoryData wFocasHistoryData = FocasHistoryDataDAO.getInstance().CalculateFocasHistoryData(wLoginUser,
					wYearStart, wYearEnd, wErrorCode);
			// ④遍历获取台车返工件数,求平均数
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
			// ①保存数据
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