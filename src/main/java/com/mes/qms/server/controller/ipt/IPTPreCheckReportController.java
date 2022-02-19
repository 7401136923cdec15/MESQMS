package com.mes.qms.server.controller.ipt;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.IPTService;
import com.mes.qms.server.service.mesenum.APSOperateType;
import com.mes.qms.server.service.mesenum.BPMStatus;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.ipt.IPTPreCheckReport;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 预检报告控制器
 * 
 * @author PengYouWang
 * @CreateTime 2020-5-19 20:23:29
 * @LastEditTime 2020-5-19 20:23:36
 */
@Controller
@RequestMapping("/api/IPTPreCheckReport")
public class IPTPreCheckReportController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(IPTPreCheckReportController.class);

	@Autowired
	IPTService wIPTService;

	/**
	 * 生成预检报告
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@PostMapping("/Create")
	public Object Create(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(wParam.get("OrderID"));

			ServiceResult<IPTPreCheckReport> wServiceResult = wIPTService.IPT_CreatePreCheckReport(wLoginUser,
					wOrderID);

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
	 * 获取预检任务集合
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@GetMapping("/Info")
	public Object Info(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wReportID = StringUtils.parseInt(request.getParameter("ReportID"));

			ServiceResult<IPTPreCheckReport> wServiceResult = wIPTService.IPT_QueryPreCheckReportByID(wLoginUser,
					wReportID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
				this.SetResult(wResult, "OperateList", wServiceResult.CustomResult.get("OperateList"));
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
	 * 根据订单查询预检报告
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/All")
	public Object All(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<IPTPreCheckReport>> wServiceResult = wIPTService
					.IPT_QueryPreCheckReportByOrder(wLoginUser, wOrderID);

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
	 * 查询审批任务
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@GetMapping("/AuditList")
	public Object AuditList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<IPTPreCheckReport>> wServiceResult = wIPTService.IPT_QueryReportAuditList(wLoginUser,
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
	 * 审批
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@ResponseBody
	@PostMapping("/Audit")
	public Object Audit(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			List<IPTPreCheckReport> wList = CloneTool.CloneArray(wParam.get("data"), IPTPreCheckReport.class);
			APSOperateType wOperateType = APSOperateType.getEnumType(StringUtils.parseInt(wParam.get("OperateType")));
			if (wList == null || wList.size() <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_AuditPreCheckReport(wLoginUser, wList,
					wOperateType);

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
	 * 导出预检报告
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@GetMapping("/Export")
	public Object Export(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wReportID = StringUtils.parseInt(request.getParameter("data"));

			IPTPreCheckReport wIPTPreCheckReport = wIPTService.IPT_QueryPreCheckReportByID(wLoginUser,
					wReportID).Result;

			if (wIPTPreCheckReport == null || wIPTPreCheckReport.ID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			} else if (wIPTPreCheckReport.Status != BPMStatus.Audited.getValue()) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：请选择已审批的数据!");
				return wResult;
			}

			ServiceResult<String> wServiceResult = wIPTService.ExportPreCheckReport(wLoginUser, wIPTPreCheckReport);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "提示：导出成功!", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception e) {
			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	@ResponseBody
	@GetMapping({ "/ExportByOrder" })
	public Object ExportByOrder(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID")).intValue();

			if (wOrderID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<String> wServiceResult = this.wIPTService.ExportPreCheckReportByOrder(wLoginUser, wOrderID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "提示：导出成功!", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception e) {
			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	@ResponseBody
	@GetMapping({ "/ExportByOrderZip" })
	public Object ExportByOrderZip(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID")).intValue();

			if (wOrderID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<String> wServiceResult = this.wIPTService.ExportPreCheckReportByOrderZip(wLoginUser,
					wOrderID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "提示：导出成功!", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception e) {
			wResult = GetResult(RetCode.SERVER_CODE_ERR, e.toString());
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 人员获取任务
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@GetMapping("/EmployeeAll")
	public Object EmployeeAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wTagTypes = StringUtils.parseInt(request.getParameter("TagTypes"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<BPMTaskBase>> wServiceResult = wIPTService.IPT_QueryPreCheckReportEmployeeAll(wLoginUser,
					wTagTypes, wStartTime, wEndTime);

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
	 * 查历史记录
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@GetMapping("/History")
	public Object History(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wID = StringUtils.parseInt(request.getParameter("ID"));
			String wCode = StringUtils.parseString(request.getParameter("Code"));
			int wUpFlowID = StringUtils.parseInt(request.getParameter("UpFlowID"));
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<IPTPreCheckReport>> wServiceResult = wIPTService
					.IPT_QueryPreCheckReportHistory(wLoginUser, wID, wCode, wUpFlowID, wOrderID, wStartTime, wEndTime);

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
