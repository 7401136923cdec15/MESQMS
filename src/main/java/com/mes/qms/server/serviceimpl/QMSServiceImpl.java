package com.mes.qms.server.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.QMSService;
import com.mes.qms.server.service.mesenum.RSMTurnOrderTaskStatus;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.rsm.RSMTurnOrderTaskDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-4-1 13:53:36
 * @LastEditTime 2020-4-1 13:53:40
 *
 */
@Service
public class QMSServiceImpl implements QMSService {

	private static Logger logger = LoggerFactory.getLogger(QMSServiceImpl.class);

	public QMSServiceImpl() {
	}

	private static QMSService Instance;

	public static QMSService getInstance() {
		if (Instance == null)
			Instance = new QMSServiceImpl();
		return Instance;
	}

	@Override
	public ServiceResult<List<Integer>> FPC_QueryPreStationIDList(BMSEmployee wLoginUser, int wOrderID,
			int wStationID) {
		ServiceResult<List<Integer>> wResult = new ServiceResult<List<Integer>>();
		try {
			wResult.Result = new ArrayList<Integer>();
			APIResult wAPIResult = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID);
			OMSOrder wOMSOrder = wAPIResult.Info(OMSOrder.class);
			if (wOMSOrder == null || wOMSOrder.ID <= 0) {
				return wResult;
			}

			int wRouteID = QMSConstants.GetFPCRoute(wOMSOrder.ProductID, wOMSOrder.LineID, wOMSOrder.CustomerID).ID;

//			wAPIResult = FMCServiceImpl.getInstance().FPC_QueryProductRouteList(wLoginUser, 0, 0, 0);
//			List<FPCProductRoute> wProductRouteList = wAPIResult.List(FPCProductRoute.class);
//			Optional<FPCProductRoute> wOption = wProductRouteList.stream()
//					.filter(p -> p.ProductID == wOMSOrder.ProductID && p.Line.equals(wOMSOrder.LineName)).findFirst();

//			if (!wOption.isPresent()) {
//				return wResult;
//			}

//			FPCProductRoute wProductRoute = wOption.get();
			wAPIResult = FMCServiceImpl.getInstance().FPC_QueryRoutePartListByRouteID(wLoginUser, wRouteID);
			List<FPCRoutePart> wPartList = wAPIResult.List(FPCRoutePart.class);

			if (wPartList == null || wPartList.size() <= 0) {
				return wResult;
			}
			// 直接上级工位
			Optional<FPCRoutePart> wRoutePart = wPartList.stream().filter(p -> p.PartID == wStationID).findFirst();
			if (wRoutePart.isPresent()) {
				wResult.Result.add(wRoutePart.get().PrevPartID);
			}
			// 间接上级工位
			List<FPCRoutePart> wTempList = wPartList.stream()
					.filter(p -> p.NextPartIDMap.containsKey(String.valueOf(wStationID))).collect(Collectors.toList());
			if (wTempList.size() > 0) {
				wResult.Result.addAll(wTempList.stream().map(p -> p.PartID).collect(Collectors.toList()));
			}

			// 去除为0的工位ID
			if (wResult.Result.size() > 0) {
				wResult.Result.removeIf(p -> p <= 0);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> SFC_JudgeTaskStepIsCanDo(BMSEmployee wLoginUser, APSTaskStep wAPSTaskStep,
			List<FPCRoutePart> wThisRoutePartList, OutResult<Integer> wErrorCode) {
		ServiceResult<String> wResult = new ServiceResult<>();
		wResult.Result = "";
		try {
			List<Integer> wPartIDList = QMSUtils.getInstance().FPC_QueryPreStationIDListOnlyOne(wThisRoutePartList,
					wAPSTaskStep.PartID);
			if (wPartIDList == null || wPartIDList.size() <= 0) {
				return wResult;
			}

			List<String> wPartNameList = new ArrayList<>();
			for (int wPartID : wPartIDList) {
				List<RSMTurnOrderTask> wTempList = RSMTurnOrderTaskDAO.getInstance().SelectList(wLoginUser, -1,
						wAPSTaskStep.OrderID, wPartID, -1,
						new ArrayList<>(Arrays.asList(RSMTurnOrderTaskStatus.Passed.getValue())), null, null,
						wErrorCode);
				if (wTempList.size() <= 0) {
					wPartNameList.add(QMSConstants.GetFPCPartName(wPartID));
					break;
				}
			}

			if (wPartNameList == null || wPartNameList.size() <= 0) {
				return wResult;
			}

			StringBuffer wSB = new StringBuffer();
			for (int i = 0; i < wPartNameList.size(); i++) {
				if (i < wPartNameList.size() - 1) {
					wSB.append(StringUtils.Format("【{0}】、", new Object[] { wPartNameList.get(i) }));
				} else {
					wSB.append(StringUtils.Format("【{0}】", new Object[] { wPartNameList.get(i) }));
				}
			}
			wResult.Result = StringUtils.Format("{0}工位未转序，【{1}】工序无法开工!",
					new Object[] { wSB.toString(), QMSConstants.GetFPCStepName(wAPSTaskStep.StepID) });
		} catch (Exception e) {
			wResult.FaultCode = String.valueOf(wResult.FaultCode) + e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

//	@Override
//	public ServiceResult<Boolean> NCR_Create(BMSEmployee wLoginUser, IPTItem wIPTItem, OMSOrder wOMSOrder,
//			int wTaskID) {
//		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
//		try {
//			APIResult wAPIResult = WDWServiceImpl.getInstance().WDW_CreateNCRTask(wLoginUser);
//
//			NCRTask wNCRTask = wAPIResult.Info(NCRTask.class);
//			List<BPMStepAction> wBPMStepActionList = wAPIResult.List(BPMStepAction.class);
//
//			if (wNCRTask == null || wNCRTask.ID <= 0 || wBPMStepActionList == null || wBPMStepActionList.size() <= 0) {
//				wResult.Result = false;
//				return wResult;
//			}
//			// 提交操作步骤
//			Map<String, Object> wValueMap = new HashMap<String, Object>();
//			wValueMap.put("TaskStepID", wTaskID);
//			wValueMap.put("CarTypeID", wOMSOrder.ProductID);
//			wValueMap.put("CarNumber", Integer.parseInt(wOMSOrder.PartNo.split("#")[1]));
//			wValueMap.put("Number", 1);
//			wValueMap.put("DescribeInfo", MessageFormat.format("【{0}】待处理", wIPTItem.Text));
//			wValueMap.put("UpFlowID", wLoginUser.ID);
//			wValueMap.put("Status", NCRStatus.ToWorkAreaAudit.getValue());
//			wValueMap.put("ConfirmTime", Calendar.getInstance());
//			BPMStepAction wBPMStepAction = wBPMStepActionList.get(0);
//			for (BPMCustom wBPMCustom : wBPMStepAction.CustomList) {
//				if (!wValueMap.containsKey(wBPMCustom.PropertyName)) {
//					continue;
//				}
//				wBPMCustom.PropertyValue = wValueMap.get(wBPMCustom.PropertyName);
//			}
//			wBPMStepAction.Result = 1;
//			wBPMStepAction.Active = 1;
//
//			wAPIResult = WDWServiceImpl.getInstance().BPM_SubmitStepAction(wLoginUser, wNCRTask, wBPMStepAction);
//
//			wResult.Result = true;
//		} catch (Exception e) {
//			wResult.FaultCode += e.toString();
//			logger.error(e.toString());
//		}
//		return wResult;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public ServiceResult<Boolean> WDW_IsSendNCR(BMSEmployee wAdminUser, int taskID, int iPTItemID) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		try {
			List<Integer> wList = WDWServiceImpl.getInstance().WDW_SpecialItemAll_NCR(wAdminUser, taskID)
					.Info(List.class);
			if (wList == null || wList.size() <= 0 || !wList.stream().anyMatch(p -> p.intValue() == iPTItemID)) {
				wResult.Result = false;
			} else if (wList.stream().anyMatch(p -> p.intValue() == iPTItemID)) {
				wResult.Result = true;
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ServiceResult<Boolean> WDW_IsSendRepair(BMSEmployee wAdminUser, int taskID, int iPTItemID) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		wResult.Result = false;
		try {
			List<Integer> wList = WDWServiceImpl.getInstance().WDW_SpecialItemAll_Repair(wAdminUser, taskID)
					.Custom("info", List.class);
			if (wList == null || wList.size() <= 0) {
				wResult.Result = false;
			} else if (wList.stream().anyMatch(p -> p.intValue() == iPTItemID)) {
				wResult.Result = true;
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}
}
