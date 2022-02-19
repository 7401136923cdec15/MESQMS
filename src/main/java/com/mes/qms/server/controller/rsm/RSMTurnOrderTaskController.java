package com.mes.qms.server.controller.rsm;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.mes.qms.server.service.CoreService;
import com.mes.qms.server.service.RSMService;
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.mesenum.BMSDepartmentType;
import com.mes.qms.server.service.mesenum.MBSRoleTree;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.bms.BMSDepartment;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bms.BMSPosition;
import com.mes.qms.server.service.po.bms.BMSWorkCharge;
import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 转序任务控制器
 * 
 * @author ShrisJava
 *
 */
@RestController
@RequestMapping("/api/RSMTurnOrderTask")
public class RSMTurnOrderTaskController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(RSMTurnOrderTaskController.class);

	@Autowired
	RSMService wRSMService;

	@Autowired
	CoreService wCoreService;

	@Autowired
	SFCService wSFCService;

	/**
	 * 条件查询所有转序单
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/All")
	public Object All(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(wParam.get("OrderID"));
			int wApplyStationID = StringUtils.parseInt(wParam.get("ApplyStationID"));
			int wTargetStationID = StringUtils.parseInt(wParam.get("TargetStationID"));
			Calendar wStartTime = StringUtils.parseCalendar(wParam.get("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(wParam.get("EndTime"));

			ServiceResult<List<RSMTurnOrderTask>> wServiceResult = wRSMService.RSM_QueryTurnOrderTaskList(wLoginUser,
					wOrderID, wApplyStationID, wTargetStationID, null, wStartTime, wEndTime);

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

	/**
	 * 判断是否有权限发起申请
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/CheckPower")
	public Object CheckPower(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ①获取参数
			APSTaskPart wAPSTaskPart = CloneTool.Clone(wParam.get("APSTaskPart"), APSTaskPart.class);
			if (wAPSTaskPart == null || wAPSTaskPart.ID <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
			}

			// ①判断申请转序的权限
			if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.getCompanyID(), wLoginUser.getID(),
					MBSRoleTree.SQZX.getValue(), 0, 0).Info(Boolean.class)) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
			}

			// ①班组工位集合
			List<BMSDepartment> wDepartmentList = wCoreService.BMS_QueryDepartmentList(wLoginUser)
					.List(BMSDepartment.class);
			BMSDepartment wDepartment = null;
			Optional<BMSDepartment> wDepartmentOption = wDepartmentList.stream()
					.filter(p -> p.ID == wLoginUser.DepartmentID).findFirst();
			if (wDepartmentOption.isPresent()) {
				wDepartment = wDepartmentOption.get();
			}
			List<BMSPosition> wPositionList = wCoreService.BMS_QueryPositionList(wLoginUser).List(BMSPosition.class);
			BMSPosition wBMSPosition = null;
			Optional<BMSPosition> wPositionOption = wPositionList.stream().filter(p -> p.ID == wLoginUser.Position)
					.findFirst();
			if (wPositionOption.isPresent()) {
				wBMSPosition = wPositionOption.get();
			}

			if (wDepartment == null || wDepartment.Type != BMSDepartmentType.Class.getValue() || wBMSPosition == null
					|| wBMSPosition.DutyID != 1) {
				return GetResult(RetCode.SERVER_CODE_ERR, "用户管理未设置此人为班组长");
			}

			List<BMSWorkCharge> wWorkChargeList = wCoreService
					.BMS_QueryWorkChargeList(wLoginUser, 0, wLoginUser.DepartmentID, 1).List(BMSWorkCharge.class);
			if (wWorkChargeList == null || wWorkChargeList.size() <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, "该班组无管辖工位!");
			}

			if (!wWorkChargeList.stream().anyMatch(p -> p.StationID == wAPSTaskPart.PartID)) {
				return GetResult(RetCode.SERVER_CODE_ERR, "无权限发起此工位的转序!");
			}

			ServiceResult<List<RSMTurnOrderTask>> wServiceResult = wRSMService.RSM_QueryTurnOrderTaskList(wLoginUser,
					wAPSTaskPart.ID);

			List<RSMTurnOrderTask> wTurnOrderList = wServiceResult.GetResult();
			if (wTurnOrderList.size() > 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, "该工位已发起转序申请!");
			}

			ServiceResult<Boolean> wResultSR = wSFCService.SFC_JudgeIsAllStepOK(wLoginUser, wAPSTaskPart);

			ServiceResult<List<Integer>> wStationSR = wSFCService.SFC_QueryYJStationIDList(wLoginUser);

			if (!wResultSR.Result && !wStationSR.Result.stream().anyMatch(p -> p == wAPSTaskPart.PartID)) {
				return GetResult(RetCode.SERVER_CODE_ERR, wResultSR.FaultCode);
			}

			if (StringUtils.isNotEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, true);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 获取可转序的工位列表
	 */
	@GetMapping("/NextStationList")
	public Object NextStationList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ①获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wStationID = StringUtils.parseInt(request.getParameter("StationID"));

			ServiceResult<List<FPCPart>> wServiceResult = wRSMService.RSM_QueryNextStationList(wLoginUser, wOrderID,
					wStationID);

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

	/**
	 * 提交申请
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/Submit")
	public Object Submit(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 【申请转序】权限控制
			if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 240001, 0, 0)
					.Info(Boolean.class)) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
			}

			// 获取参数
			APSTaskPart wAPSTaskPart = CloneTool.Clone(wParam.get("APSTaskPart"), APSTaskPart.class);
			List<Integer> wStationIDList = CloneTool.CloneArray(wParam.get("StationIDList"), Integer.class);

			if (wStationIDList == null || wStationIDList.size() <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, "目标工位不能为空!");
			}

			ServiceResult<Integer> wServiceResult = wRSMService.RSM_SubmitTurnOrderApply(wLoginUser, wAPSTaskPart,
					wStationIDList);

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

	/**
	 * 获取转序申请单
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/TaskAll")
	public Object TaskAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ①获取参数
			int wTaskPartID = StringUtils.parseInt(request.getParameter("TaskPartID"));

			ServiceResult<List<RSMTurnOrderTask>> wServiceResult = wRSMService.RSM_QueryTurnOrderTaskList(wLoginUser,
					wTaskPartID);

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

	/**
	 * 获取本周的所有工位计划(包括所有下达的未完成的任务)(状态取转序单的状态)
	 */
	@GetMapping("/TaskPartTaskAll")
	public Object TaskPartTaskAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2010, 0, 1, 0, 0, 0);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = Calendar.getInstance();

			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = Calendar.getInstance();

			ServiceResult<List<APSTaskPart>> wServiceResult = wRSMService.RSM_QueryAllTaskPartListNew(wLoginUser,
					wStartTime, wEndTime, wPartNo, wPartID);

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

	/**
	 * 获取时间段内的班组的已完成工位计划转序单
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/AllByEmployeeClass")
	public Object AllByEmployeeClass(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			int wOperatorID = StringUtils.parseInt(request.getParameter("OperatorID"));

			if (wOperatorID == 0)
				wOperatorID = wLoginUser.getID();

			List<Integer> wClassIDList = StringUtils.parseIntList(request.getParameter("ClassIDList"), ",");
			wClassIDList.removeIf(p -> p <= 0);
			ServiceResult<List<RSMTurnOrderTask>> wServiceResult = null;
			if (wClassIDList.size() > 0) {
				wServiceResult = wRSMService.RSM_QueryTurnOrderTaskList(wLoginUser, wClassIDList, wStartTime, wEndTime);
			} else {
				wServiceResult = wRSMService.RSM_QueryTurnOrderTaskList(wLoginUser, wOperatorID, wStartTime, wEndTime);
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

	/**
	 * 渲染已完成的工位
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/BOPDoneList")
	public Object BOPDoneList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<Integer>> wServiceResult = wRSMService.RSM_QueryBOPDoneList(wLoginUser, wOrderID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
				this.SetResult(wResult, "DoingList", wServiceResult.CustomResult.get("DoingList"));
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 查单条
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/Info")
	public Object Info(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wID = StringUtils.parseInt(request.getParameter("ID"));

			ServiceResult<RSMTurnOrderTask> wServiceResult = wRSMService.RSM_QueryInfo(wLoginUser, wID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 获取工位检验员的转序确认单列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/CheckerConfirmList")
	public Object CheckerConfirmList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<RSMTurnOrderTask>> wServiceResult = wRSMService.RSM_QueryCheckerConfirmList(wLoginUser,
					wStartTime, wEndTime);

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

	/**
	 * 转序确认
	 */
	@PostMapping("/Confirm")
	public Object Confirm(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			RSMTurnOrderTask wData = CloneTool.Clone(wParam.get("data"), RSMTurnOrderTask.class);

			ServiceResult<Integer> wServiceResult = wRSMService.RMS_PostConfirm(wLoginUser, wData);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 手动触发质量日计划
	 */
	@GetMapping("/TriggerFinalTask")
	public Object TriggerFinalTask(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			String wOrderIDs = StringUtils.parseString(request.getParameter("OrderIDs"));

			ServiceResult<Integer> wServiceResult = wRSMService.APS_TriggerFinalTask(wLoginUser, wOrderIDs);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
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
