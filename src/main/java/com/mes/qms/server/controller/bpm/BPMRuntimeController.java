package com.mes.qms.server.controller.bpm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.APSService;
import com.mes.qms.server.service.BPMService;
import com.mes.qms.server.service.CoreService;
import com.mes.qms.server.service.IPTService;
import com.mes.qms.server.service.LFSService;
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.TCMService;
import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.BPMHistoryTaskStatus;
import com.mes.qms.server.service.mesenum.IPTPreCheckReportStatus;
import com.mes.qms.server.service.mesenum.IPTStandardBPMStatus;
import com.mes.qms.server.service.mesenum.SFCBogiesChangeBPMStatus;
import com.mes.qms.server.service.mesenum.SFCLetPassBPMStatus;
import com.mes.qms.server.service.mesenum.SFCReturnOverMaterialStatus;
import com.mes.qms.server.service.mesenum.TCMReworkStatus;
import com.mes.qms.server.service.mesenum.TCMTechChangeNoticeStatus;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMActivitiHisTask;
import com.mes.qms.server.service.po.bpm.BPMActivitiProcessInstance;
import com.mes.qms.server.service.po.bpm.BPMActivitiTask;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.ipt.IPTPreCheckReport;
import com.mes.qms.server.service.po.ipt.IPTStandardBPM;
import com.mes.qms.server.service.po.sfc.SFCBogiesChangeBPM;
import com.mes.qms.server.service.po.sfc.SFCLetPassBPM;
import com.mes.qms.server.service.po.sfc.SFCReturnOverMaterial;
import com.mes.qms.server.service.po.tcm.TCMRework;
import com.mes.qms.server.service.po.tcm.TCMTechChangeNotice;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.BPMServiceImpl;
import com.mes.qms.server.utils.RetCode;

@RestController
@RequestMapping("/api/Runtime")
public class BPMRuntimeController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(BPMRuntimeController.class);

	@Autowired
	LFSService wLFSService;
	@Autowired
	APSService wAPSService;
	@Autowired
	BPMService wBPMService;
	@Autowired
	CoreService wCoreService;
	@Autowired
	SFCService wSFCService;
	@Autowired
	IPTService wIPTService;
	@Autowired
	TCMService wTCMService;

	/**
	 * 创建流程任务
	 */
	@PostMapping("/startProcessByProcessDefinitionKey")
	public Object startProcessByProcessDefinitionKey(HttpServletRequest request,
			@RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("processDefinitionKey")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			String wModuleIDString = StringUtils.parseString(wParam.get("processDefinitionKey"));
			int wOrderID = StringUtils.parseInt(wParam.get("OrderID"));
			if (wModuleIDString.startsWith("_")) {
				wModuleIDString = wModuleIDString.substring(1);
			}
			int wModuleID = StringUtils.parseInt(wModuleIDString);

			BPMEventModule wEventID = BPMEventModule.getEnumType(wModuleID);

			String wMsg = "";

			BPMTaskBase wData = null;
			@SuppressWarnings("rawtypes")
			ServiceResult wServiceResult = null;
			List<BPMActivitiTask> wBPMActivitiTask = new ArrayList<BPMActivitiTask>();
			switch (wEventID) {
			case ReturnOverMaterial:
				// 【积压物资退库申请】权限控制
				if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 990003, 0, 0)
						.Info(Boolean.class)) {
					wResult = GetResult(RetCode.SERVER_CODE_SUC, "无授权", null, null);
					return wResult;
				}
				// SFCDayPlanAudit 单据状态为0且创建人是自己的任务
				// 创建审批单(先查询默认状态单据)
				wServiceResult = wSFCService.SFC_QueryDefaultReturnOverMaterial(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.GetResult() == null
						|| ((BPMTaskBase) wServiceResult.GetResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.GetResult()).FlowID <= 0)
					wServiceResult = wSFCService.SFC_CreateReturnOverMaterial(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.FaultCode)) {
					wMsg += wServiceResult.getFaultCode();
				}
				wData = (SFCReturnOverMaterial) wServiceResult.GetResult();

				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					wData.CreateTime = Calendar.getInstance();
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wSFCService.SFC_SubmitReturnOverMaterial(wLoginUser,
								(SFCReturnOverMaterial) wData);
						if (wServiceResult.ErrorCode != 0) {
							wMsg += wServiceResult.getFaultCode();
						}

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			case YJReport:
				// 【预检报告】权限控制
				if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 81070000, 0, 0)
						.Info(Boolean.class)) {
					return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
				}
				// 生成预检报告
				ServiceResult<IPTPreCheckReport> wRstMsg = wIPTService.IPT_CreatePreCheckReport(wLoginUser, wOrderID);
				if (StringUtils.isNotEmpty(wRstMsg.FaultCode)) {
					return GetResult(RetCode.SERVER_CODE_ERR, wRstMsg.FaultCode);
				}
				// 查询默认状态单据
				wServiceResult = wIPTService.IPT_QueryDefaultPreCheckReport(wLoginUser, wEventID.getValue(), wOrderID);
				if (StringUtils.isNotEmpty(wServiceResult.FaultCode)) {
					return GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
				}
				wData = (IPTPreCheckReport) wServiceResult.GetResult();

				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					wData.CreateTime = Calendar.getInstance();
					if (wData.FlowID <= 0) {
						((IPTPreCheckReport) wData).IPTPreCheckItemList = null;
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wIPTService.IPT_SubmitPreCheckReport(wLoginUser, (IPTPreCheckReport) wData);
						if (wServiceResult.ErrorCode != 0) {
							wMsg += wServiceResult.getFaultCode();
						}

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			case StandardAudit:
				// 查询默认状态单据
				wServiceResult = wIPTService.IPT_QueryDefaultStandardBPM(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.GetResult() == null
						|| ((BPMTaskBase) wServiceResult.GetResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.GetResult()).FlowID <= 0)
					wServiceResult = wIPTService.IPT_CreateStandardBPM(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.FaultCode)) {
					wMsg += wServiceResult.getFaultCode();
				}
				wData = (IPTStandardBPM) wServiceResult.GetResult();

				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					wData.CreateTime = Calendar.getInstance();
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wIPTService.IPT_SubmitStandardBPM(wLoginUser, (IPTStandardBPM) wData);
						if (wServiceResult.ErrorCode != 0) {
							wMsg += wServiceResult.getFaultCode();
						}

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			case BogiesChange:
				// 【转向架互换】权限控制
				if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 390000009, 0, 0)
						.Info(Boolean.class)) {
					return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
				}
				// 查询默认状态单据
				wServiceResult = wSFCService.SFC_QueryDefaultBogiesChangeBPM(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.GetResult() == null
						|| ((BPMTaskBase) wServiceResult.GetResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.GetResult()).FlowID <= 0)
					wServiceResult = wSFCService.SFC_CreateBogiesChangeBPM(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.FaultCode)) {
					wMsg += wServiceResult.getFaultCode();
				}
				wData = (SFCBogiesChangeBPM) wServiceResult.GetResult();

				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wSFCService.SFC_SubmitBogiesChangeBPM(wLoginUser, (SFCBogiesChangeBPM) wData);
						if (wServiceResult.ErrorCode != 0) {
							wMsg += wServiceResult.getFaultCode();
						}

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			case TechChangeNotice:
				// 查询默认状态单据
				wServiceResult = wTCMService.TCM_QueryDefaultTechChangeNotice(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.GetResult() == null
						|| ((BPMTaskBase) wServiceResult.GetResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.GetResult()).FlowID <= 0)
					wServiceResult = wTCMService.TCM_CreateTechChangeNotice(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.FaultCode)) {
					wMsg += wServiceResult.getFaultCode();
				}
				wData = (TCMTechChangeNotice) wServiceResult.GetResult();

				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wTCMService.TCM_SubmitTechChangeNotice(wLoginUser,
								(TCMTechChangeNotice) wData);
						if (wServiceResult.ErrorCode != 0) {
							wMsg += wServiceResult.getFaultCode();
						}

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			case SBOMChange_Repair:
				// 查询默认状态单据
				wServiceResult = wTCMService.TCM_QueryDefaultRework(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.GetResult() == null
						|| ((BPMTaskBase) wServiceResult.GetResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.GetResult()).FlowID <= 0)
					wServiceResult = wTCMService.TCM_CreateRework(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.FaultCode)) {
					wMsg += wServiceResult.getFaultCode();
				}
				wData = (TCMRework) wServiceResult.GetResult();

				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wTCMService.TCM_SubmitRework(wLoginUser, (TCMRework) wData);
						if (wServiceResult.ErrorCode != 0) {
							wMsg += wServiceResult.getFaultCode();
						}

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			case SFCLetPass:
				// 查询默认状态单据
				wServiceResult = wSFCService.SFC_QueryDefaultLetPassBPM(wLoginUser, wEventID.getValue());
				if (wServiceResult == null || wServiceResult.GetResult() == null
						|| ((BPMTaskBase) wServiceResult.GetResult()).ID <= 0
						|| ((BPMTaskBase) wServiceResult.GetResult()).FlowID <= 0)
					wServiceResult = wSFCService.SFC_CreateLetPassBPM(wLoginUser, wEventID);
				if (StringUtils.isNotEmpty(wServiceResult.FaultCode)) {
					wMsg += wServiceResult.getFaultCode();
				}
				wData = (SFCLetPassBPM) wServiceResult.GetResult();

				if (wParam.containsKey("data")) {
					wData = StringUtils.CombineData(wData, wParam.get("data"));
				}
				if (wData.ID > 0) {
					if (wData.FlowID <= 0) {
						wData.FlowID = wBPMService.BPM_CreateProcess(wLoginUser, wEventID, wData.getID(), wData)
								.Info(Integer.class);
					}
					if (wData.FlowID <= 0) {
						wMsg += "创建流程失败！";
					} else {
						wServiceResult = wSFCService.SFC_SubmitLetPassBPM(wLoginUser, (SFCLetPassBPM) wData);
						if (wServiceResult.ErrorCode != 0) {
							wMsg += wServiceResult.getFaultCode();
						}

						wBPMActivitiTask = wBPMService.BPM_GetTaskListByInstance(wLoginUser, wData.FlowID)
								.List(BPMActivitiTask.class);
					}
				}
				break;
			default:
				break;
			}
			if (wData == null) {
				wMsg += "该流程暂不支持";
			}
			if (StringUtils.isEmpty(wMsg)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wBPMActivitiTask, wData.FlowID);
				SetResult(wResult, "data", wData);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wMsg);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 提交待办任务
	 */
	@SuppressWarnings("rawtypes")
	@PostMapping("/CompleteMyPersonalTask")
	public Object CompleteMyPersonalTask(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("TaskID") || !wParam.containsKey("data")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			int wTaskID = CloneTool.Clone(wParam.get("TaskID"), Integer.class);
			BPMTaskBase wBPMTaskBase = CloneTool.Clone(wParam.get("data"), BPMTaskBase.class);
			int wLocalScope = wParam.containsKey("localScope") ? StringUtils.parseInt(wParam.get("localScope")) : 0;
			if (wTaskID <= 0 || wBPMTaskBase == null || wBPMTaskBase.ID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			// 判断任务是否已完成(防止任务重复提交)
			BPMActivitiHisTask wHisTask = wBPMService.BPM_GetTask(wLoginUser, wTaskID).Info(BPMActivitiHisTask.class);
			if (wHisTask == null || StringUtils.isEmpty(wHisTask.ID)) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：该任务不存在!");
				return wResult;
			} else if (wHisTask.Status == BPMHistoryTaskStatus.NomalFinished.getValue()
					|| wHisTask.Status == BPMHistoryTaskStatus.Canceled.getValue()) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：该任务已完成或已取消!");
				return wResult;
			}

			int wModuleID = wBPMTaskBase.getFlowType();
			BPMEventModule wEventID = BPMEventModule.getEnumType(wModuleID);
			ServiceResult wServiceResult = null;
			BPMActivitiProcessInstance wBPMActivitiProcessInstance = null;

			ServiceResult<Boolean> wServiceResultBool = new ServiceResult<Boolean>(false);
			switch (wEventID) {
			case ReturnOverMaterial: {
				// 提交任务单
				SFCReturnOverMaterial wTask = CloneTool.Clone(wParam.get("data"), SFCReturnOverMaterial.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wSFCService.SFC_SubmitReturnOverMaterial(wLoginUser, wTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				/**
				 * 判断流程关闭
				 */
				if (wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& (wTask.Status != SFCReturnOverMaterialStatus.NomalClose.getValue()
								&& wTask.Status != SFCReturnOverMaterialStatus.ExceptionClose.getValue())) {
					// 若流程关闭且状态非正常关闭与异常关闭则改为正常关闭
					wTask.Status = SFCReturnOverMaterialStatus.NomalClose.getValue();
					wServiceResult = wSFCService.SFC_SubmitReturnOverMaterial(wLoginUser, wTask);
				}
			}
				break;
			case YJReport: {
				// 提交任务单
				IPTPreCheckReport wTask = CloneTool.Clone(wParam.get("data"), IPTPreCheckReport.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wIPTService.IPT_SubmitPreCheckReport(wLoginUser, wTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				/**
				 * 判断流程关闭
				 */
				if (wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& (wTask.Status != IPTPreCheckReportStatus.NomalClose.getValue()
								&& wTask.Status != IPTPreCheckReportStatus.ExceptionClose.getValue())) {
					// 若流程关闭且状态非正常关闭与异常关闭则改为正常关闭
					wTask.Status = IPTPreCheckReportStatus.NomalClose.getValue();
					wServiceResult = wIPTService.IPT_SubmitPreCheckReport(wLoginUser, wTask);
				}
			}
				break;
			case StandardAudit: {
				// 提交任务单
				IPTStandardBPM wTask = CloneTool.Clone(wParam.get("data"), IPTStandardBPM.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wIPTService.IPT_SubmitStandardBPM(wLoginUser, wTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				/**
				 * 判断流程关闭
				 */
				if (wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& (wTask.Status != IPTStandardBPMStatus.NomalClose.getValue()
								&& wTask.Status != IPTStandardBPMStatus.ExceptionClose.getValue())) {
					// 若流程关闭且状态非正常关闭与异常关闭则改为正常关闭
					wTask.Status = IPTStandardBPMStatus.NomalClose.getValue();
					wServiceResult = wIPTService.IPT_SubmitStandardBPM(wLoginUser, wTask);
				}
			}
				break;
			case BogiesChange: {
				// 提交任务单
				SFCBogiesChangeBPM wTask = CloneTool.Clone(wParam.get("data"), SFCBogiesChangeBPM.class);

				// 判断，防止重复提交
				ServiceResult<String> wRst = wSFCService.SFC_JudgeIsCanSubmit(wLoginUser, wTask);
				if (StringUtils.isNotEmpty(wRst.Result)) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wRst.Result);
					return wResult;
				}

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wSFCService.SFC_SubmitBogiesChangeBPM(wLoginUser, wTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				/**
				 * 判断流程关闭
				 */
				if (wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& (wTask.Status != SFCBogiesChangeBPMStatus.NomalClose.getValue()
								&& wTask.Status != SFCBogiesChangeBPMStatus.ExceptionClose.getValue())) {
					// 若流程关闭且状态非正常关闭与异常关闭则改为正常关闭
					wTask.Status = SFCBogiesChangeBPMStatus.NomalClose.getValue();
					wServiceResult = wSFCService.SFC_SubmitBogiesChangeBPM(wLoginUser, wTask);
				}
			}
				break;
			case TechChangeNotice: {
				// 提交任务单
				TCMTechChangeNotice wTask = CloneTool.Clone(wParam.get("data"), TCMTechChangeNotice.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wTCMService.TCM_SubmitTechChangeNotice(wLoginUser, wTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				/**
				 * 判断流程关闭
				 */
				if (wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& (wTask.Status != TCMTechChangeNoticeStatus.NomalClose.getValue()
								&& wTask.Status != TCMTechChangeNoticeStatus.Canceled.getValue())) {
					// 若流程关闭且状态非正常关闭与异常关闭则改为正常关闭
					wTask.Status = TCMTechChangeNoticeStatus.NomalClose.getValue();
					wServiceResult = wTCMService.TCM_SubmitTechChangeNotice(wLoginUser, wTask);
				}
			}
				break;
			case SBOMChange_Repair: {
				// 提交任务单
				TCMRework wTask = CloneTool.Clone(wParam.get("data"), TCMRework.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wTCMService.TCM_SubmitRework(wLoginUser, wTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				/**
				 * 判断流程关闭
				 */
				if (wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& (wTask.Status != TCMReworkStatus.NomalClose.getValue()
								&& wTask.Status != TCMReworkStatus.ExceptionClose.getValue())) {
					// 若流程关闭且状态非正常关闭与异常关闭则改为正常关闭
					wTask.Status = TCMReworkStatus.NomalClose.getValue();
					wServiceResult = wTCMService.TCM_SubmitRework(wLoginUser, wTask);
				}
			}
				break;
			case SFCLetPass: {
				// 提交任务单
				SFCLetPassBPM wTask = CloneTool.Clone(wParam.get("data"), SFCLetPassBPM.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, wLocalScope, wTask,
						wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wSFCService.SFC_SubmitLetPassBPM(wLoginUser, wTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				/**
				 * 判断流程关闭
				 */
				if (wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& (wTask.Status != SFCLetPassBPMStatus.NomalClose.getValue()
								&& wTask.Status != SFCLetPassBPMStatus.ExceptionClose.getValue())) {
					// 若流程关闭且状态非正常关闭与异常关闭则改为正常关闭
					wTask.Status = SFCLetPassBPMStatus.NomalClose.getValue();
					wServiceResult = wSFCService.SFC_SubmitLetPassBPM(wLoginUser, wTask);
				}
			}
				break;
			default:
				break;
			}
			if (wServiceResult == null) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "流程未配置!");
				return wResult;
			}
			List<BPMActivitiTask> wBPMActivitiTask = new ArrayList<BPMActivitiTask>();
			if (wServiceResult.Result != null && ((BPMTaskBase) wServiceResult.Result).FlowID > 0) {
				wBPMActivitiTask = wBPMService
						.BPM_GetTaskListByInstance(wLoginUser, ((BPMTaskBase) wServiceResult.Result).FlowID)
						.List(BPMActivitiTask.class);
				if (wBPMActivitiTask != null) {
					wBPMActivitiTask.removeIf(
							p -> !StringUtils.parseIntList(p.Assignee.split(",")).contains(wLoginUser.getID()));
				}
			}
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wBPMActivitiTask, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 撤销待办任务
	 */
	@GetMapping("/deleteProcessInstance")
	public Object DelectInstance(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wLoginUser = GetSession(request);

			int wFlowID = StringUtils.parseInt(request.getParameter("processInstanceId"));
			int wID = StringUtils.parseInt(request.getParameter("ID"));
			int wFlowType = StringUtils.parseInt(request.getParameter("FlowType"));
			String wReason = StringUtils.parseString(request.getParameter("deleteReason"));

			if (StringUtils.isEmpty(wReason))
				wReason = "撤销";

			if (wFlowID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			APIResult wAPIResult = wBPMService.BPM_DeleteInstanceByID(wLoginUser, wFlowID, wReason);
			if (wAPIResult.getResultCode() != RetCode.SERVER_CODE_SUC)
				return wAPIResult;

			if (wFlowType > 0 && wID > 0) {
				BPMEventModule wEventID = BPMEventModule.getEnumType(wFlowType);
				switch (wEventID) {
				case ReturnOverMaterial: {
					// 提交任务单
					ServiceResult<SFCReturnOverMaterial> wTaskResult = wSFCService.SFC_GetReturnOverMaterial(wLoginUser,
							wID);
					if (wTaskResult.Result != null && wTaskResult.Result.ID > 0) {
						wTaskResult.Result.Status = SFCReturnOverMaterialStatus.Canceled.getValue();
						wTaskResult.Result.StatusText = "已撤销";
						wTaskResult.Result.FollowerID = null;

						wSFCService.SFC_SubmitReturnOverMaterial(wLoginUser, wTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
				}
					break;
				case YJReport: {
					// 提交任务单
					ServiceResult<IPTPreCheckReport> wTaskResult = wIPTService.IPT_GetPreCheckReport(wLoginUser, wID);
					if (wTaskResult.Result != null && wTaskResult.Result.ID > 0) {
						wTaskResult.Result.Status = IPTPreCheckReportStatus.Canceled.getValue();
						wTaskResult.Result.StatusText = "已撤销";
						wTaskResult.Result.FollowerID = new ArrayList<Integer>();

						wIPTService.IPT_SubmitPreCheckReport(wLoginUser, wTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
				}
					break;
				case StandardAudit: {
					// 提交任务单
					ServiceResult<IPTStandardBPM> wTaskResult = wIPTService.IPT_GetStandardBPM(wLoginUser, wID);
					if (wTaskResult.Result != null && wTaskResult.Result.ID > 0) {
						wTaskResult.Result.Status = IPTStandardBPMStatus.Canceled.getValue();
						wTaskResult.Result.StatusText = "已撤销";
						wTaskResult.Result.FollowerID = new ArrayList<Integer>();

						wIPTService.IPT_SubmitStandardBPM(wLoginUser, wTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
				}
					break;
				case BogiesChange: {
					// 提交任务单
					ServiceResult<SFCBogiesChangeBPM> wTaskResult = wSFCService.SFC_GetBogiesChangeBPM(wLoginUser, wID);
					if (wTaskResult.Result != null && wTaskResult.Result.ID > 0) {
						wTaskResult.Result.Status = SFCBogiesChangeBPMStatus.Canceled.getValue();
						wTaskResult.Result.StatusText = "已撤销";
						wTaskResult.Result.FollowerID = new ArrayList<Integer>();

						wSFCService.SFC_SubmitBogiesChangeBPM(wLoginUser, wTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
				}
					break;
				case TechChangeNotice: {
					// 提交任务单
					ServiceResult<TCMTechChangeNotice> wTaskResult = wTCMService.TCM_GetTechChangeNotice(wLoginUser,
							wID);
					if (wTaskResult.Result != null && wTaskResult.Result.ID > 0) {
						wTaskResult.Result.Status = 21;
						wTaskResult.Result.StatusText = "已撤销";
						wTaskResult.Result.FollowerID = new ArrayList<Integer>();

						wTCMService.TCM_SubmitTechChangeNotice(wLoginUser, wTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
				}
					break;
				case SBOMChange_Repair: {
					// 提交任务单
					ServiceResult<TCMRework> wTaskResult = wTCMService.TCM_GetRework(wLoginUser, wID);
					if (wTaskResult.Result != null && wTaskResult.Result.ID > 0) {
						wTaskResult.Result.Status = TCMReworkStatus.Canceled.getValue();
						wTaskResult.Result.StatusText = "已撤销";
						wTaskResult.Result.FollowerID = new ArrayList<Integer>();

						wTCMService.TCM_SubmitRework(wLoginUser, wTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
				}
					break;
				case SFCLetPass: {
					// 提交任务单
					ServiceResult<SFCLetPassBPM> wTaskResult = wSFCService.SFC_GetLetPassBPM(wLoginUser, wID);
					if (wTaskResult.Result != null && wTaskResult.Result.ID > 0) {
						wTaskResult.Result.Status = SFCLetPassBPMStatus.Canceled.getValue();
						wTaskResult.Result.StatusText = "已撤销";
						wTaskResult.Result.FollowerID = new ArrayList<Integer>();

						wSFCService.SFC_SubmitLetPassBPM(wLoginUser, wTaskResult.Result);
					} else {
						wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
						return wResult;
					}
				}
					break;
				default:
					break;
				}
			}
			wResult = GetResult(RetCode.SERVER_CODE_SUC, "撤销成功！", null, null);
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 批量审批过程检验记录
	 */
	@PostMapping("/BatchSubmit")
	public Object BatchSubmit(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			List<IPTStandardBPM> wDataList = CloneTool.CloneArray(wParam.get("data"), IPTStandardBPM.class);

			@SuppressWarnings("rawtypes")
			ServiceResult wServiceResult = null;
			for (IPTStandardBPM wIPTStandardBPM : wDataList) {
				BPMActivitiTask wSelfTask = (BPMActivitiTask) BPMServiceImpl.getInstance()
						.BPM_GetTask(wLoginUser, wIPTStandardBPM.StepID).Info(BPMActivitiTask.class);
				int wTaskID = StringUtils.parseInt(wSelfTask.ID);

				// 判断任务是否已完成(防止任务重复提交)
				BPMActivitiHisTask wHisTask = wBPMService.BPM_GetTask(wLoginUser, wTaskID)
						.Info(BPMActivitiHisTask.class);
				if (wHisTask == null || StringUtils.isEmpty(wHisTask.ID)) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：该任务不存在!");
					return wResult;
				} else if (wHisTask.Status == BPMHistoryTaskStatus.NomalFinished.getValue()
						|| wHisTask.Status == BPMHistoryTaskStatus.Canceled.getValue()) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：该任务已完成或已取消!");
					return wResult;
				}

				BPMActivitiProcessInstance wBPMActivitiProcessInstance = null;

				ServiceResult<Boolean> wServiceResultBool = new ServiceResult<Boolean>(false);

				// 提交任务单
				IPTStandardBPM wTask = CloneTool.Clone(wParam.get("data"), IPTStandardBPM.class);

				wServiceResultBool = this.wBPMService.BPM_MsgUpdate(wLoginUser, wTaskID, 0, wTask, wParam.get("data"));
				if (wServiceResultBool.getResult() || !StringUtils.isEmpty(wServiceResultBool.getFaultCode())) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提交失败:" + wServiceResultBool.getFaultCode());
					return wResult;
				}
				wServiceResult = wIPTService.IPT_SubmitStandardBPM(wLoginUser, wTask);

				wBPMActivitiProcessInstance = wBPMService.BPM_GetInstanceByID(wLoginUser, wTask.FlowID)
						.Info(BPMActivitiProcessInstance.class);

				/**
				 * 判断流程关闭
				 */
				if (wBPMActivitiProcessInstance.DurationInMillis > 0
						&& StringUtils.isEmpty(wBPMActivitiProcessInstance.DeleteReason)
						&& (wTask.Status != IPTStandardBPMStatus.NomalClose.getValue()
								&& wTask.Status != IPTStandardBPMStatus.ExceptionClose.getValue())) {
					// 若流程关闭且状态非正常关闭与异常关闭则改为正常关闭
					wTask.Status = IPTStandardBPMStatus.NomalClose.getValue();
					wServiceResult = wIPTService.IPT_SubmitStandardBPM(wLoginUser, wTask);
				}
			}

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}
}
