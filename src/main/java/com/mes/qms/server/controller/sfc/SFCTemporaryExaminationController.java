package com.mes.qms.server.controller.sfc;

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
import com.mes.qms.server.service.CoreService;
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.mesenum.SFCTaskType;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.sfc.SFCTemporaryExamination;
import com.mes.qms.server.service.po.sfc.SFCTemporaryExaminationPartItem;
import com.mes.qms.server.service.po.sfc.SFCTemporaryExaminationStepItem;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 临时性检查控制器
 * 
 * @author PengYouWang
 * @CreateTime 2020-3-31 19:49:38
 * @LastEditTime 2020-3-31 19:49:41
 */
@RestController
@RequestMapping("/api/SFCTemporaryExamination")
public class SFCTemporaryExaminationController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(SFCTemporaryExaminationController.class);

	@Autowired
	SFCService wSFCService;

	@Autowired
	CoreService wCoreService;

	/**
	 * 创建临时性检查单
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/Create")
	public Object Create(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 【质量主管】权限控制
			if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 500907, 0, 0)
					.Info(Boolean.class)) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
			}

			Integer wOrderID = StringUtils.parseInt(wParam.get("OrderID"));

			ServiceResult<SFCTemporaryExamination> wServiceResult = wSFCService
					.SFC_CreateTemporaryExamination(wLoginUser, wOrderID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
				// ①返回指定订单中可检查的工位列表
				ServiceResult<List<FPCPart>> wPartResult = wSFCService.SFC_QueryCanCheckPartList(wLoginUser, wOrderID);
				this.SetResult(wResult, "PartList", wPartResult.Result);
				this.SetResult(wResult, "CheckerList", wServiceResult.CustomResult.get("CheckerList"));
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
	 * 提交临时性检查单
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

			// ①获取参数
			SFCTemporaryExamination wData = CloneTool.Clone(wParam.get("data"), SFCTemporaryExamination.class);

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_SubmitTemporaryExamination(wLoginUser, wData);

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
	 * 查询待做、已做任务
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAll")
	public Object EmployeeAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ①获取参数
			int wTagTypes = StringUtils.parseInt(request.getParameter("TagTypes"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<SFCTemporaryExamination>> wServiceResult = wSFCService
					.SFC_QueryEmployeeAllTemporaryExaminationList(wLoginUser, wTagTypes, wStartTime, wEndTime);

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
	 * 查询待做、已做、发起的临时性检查任务
	 */
	@GetMapping("/EmployeeAllNew")
	public Object EmployeeAllNew(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wStatus = StringUtils.parseInt(request.getParameter("Status"));

			ServiceResult<List<SFCTemporaryExamination>> wServiceResult = wSFCService
					.SFC_QueryEmployeeAllTemporaryExaminationListNew(wLoginUser, wStartTime, wEndTime, wOrderID,
							wProductID, wPartNo, wPartID, wStatus);

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
	 * 查询待做、已做、发起的临时性检查任务
	 */
	@GetMapping("/EmployeeAllWeb")
	public Object EmployeeAllWeb(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wStatus = StringUtils.parseInt(request.getParameter("Status"));

			ServiceResult<List<SFCTemporaryExamination>> wServiceResult = wSFCService
					.SFC_QueryEmployeeAllTemporaryExaminationList(wLoginUser, wOrderID,
							wProductID, wPartNo, wPartID, wStatus, wStartTime, wEndTime);

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
	 * 获取检查工位列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ItemPartList")
	public Object ItemPartList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ①获取参数
			int wSFCTemporaryExaminationID = StringUtils.parseInt(request.getParameter("SFCTemporaryExaminationID"));

			ServiceResult<List<SFCTemporaryExaminationPartItem>> wServiceResult = wSFCService
					.SFC_QueryItemPartList(wLoginUser, wSFCTemporaryExaminationID);

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
	 * 获取检查的工序列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ItemStepList")
	public Object ItemStepList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ①获取参数
			int wSFCTemporaryExaminationID = StringUtils.parseInt(request.getParameter("SFCTemporaryExaminationID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			ServiceResult<List<SFCTemporaryExaminationStepItem>> wServiceResult = wSFCService
					.SFC_QueryItemStepList(wLoginUser, wSFCTemporaryExaminationID, wPartID);

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
	 * 获取检查的检验项列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ItemItemList")
	public Object ItemItemList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// ①获取参数
			int wSFCTemporaryExaminationID = StringUtils.parseInt(request.getParameter("SFCTemporaryExaminationID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wStepID = StringUtils.parseInt(request.getParameter("StepID"));

			ServiceResult<List<IPTItem>> wServiceResult = wSFCService.SFC_QueryItemItemList(wLoginUser,
					wSFCTemporaryExaminationID, wPartID, wStepID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, wServiceResult.Get("Info"));
				this.SetResult(wResult, "DoneList", wServiceResult.Get("DoneList"));
				this.SetResult(wResult, "ValueList", wServiceResult.Get("ValueList"));
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
	 * 保存填写的检验项和检验值
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/SubmitValue")
	public Object SubmitValue(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			List<IPTValue> wDataList = CloneTool.CloneArray(wParam.get("data"), IPTValue.class);
			wDataList.forEach(p -> p.IPTMode = SFCTaskType.TemporaryCheck.getValue());

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_SubmitValueList(wLoginUser, wDataList);

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
