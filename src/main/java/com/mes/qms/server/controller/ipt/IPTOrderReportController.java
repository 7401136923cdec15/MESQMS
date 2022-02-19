package com.mes.qms.server.controller.ipt;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.controller.ipt.IPTOrderReportController;
import com.mes.qms.server.service.IPTService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTCheckRecord;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTOrderReport;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/api/IPTOrderReport" })
public class IPTOrderReportController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(IPTOrderReportController.class);

	@Autowired
	IPTService wIPTService;

	@GetMapping({ "/OrderInfo" })
	public Object OrderInfo(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID")).intValue();

			ServiceResult<IPTOrderReport> wServiceResult = this.wIPTService.IPT_QueryOrderInfo(wLoginUser, wOrderID);

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

	@GetMapping({ "/IPTList" })
	public Object IPTList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wID = StringUtils.parseInt(request.getParameter("ReportPartPointID")).intValue();

			ServiceResult<List<SFCTaskIPT>> wServiceResult = this.wIPTService
					.IPT_QueryIPTListByReportPartPointID(wLoginUser, wID);

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

	@GetMapping({ "/PeriodChangeAll" })
	public Object PeriodChangeAll(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wID = StringUtils.parseInt(request.getParameter("OrderID")).intValue();

			ServiceResult<List<IPTItem>> wServiceResult = this.wIPTService.IPT_QueryPeriodChangeAll(wLoginUser, wID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
				SetResult(wResult, "ValueList", wServiceResult.CustomResult.get("ValueList"));
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping({ "/RecordAll" })
	public Object RecordAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID")).intValue();
			int wStationID = StringUtils.parseInt(request.getParameter("StationID")).intValue();
			int wStepID = StringUtils.parseInt(request.getParameter("StepID")).intValue();
			int wSubmitID = StringUtils.parseInt(request.getParameter("SubmitID")).intValue();

			ServiceResult<List<APSTaskStep>> wServiceResult = this.wIPTService.IPT_QueryRecordAll(wLoginUser,
					wStartTime, wEndTime, wOrderID, wStationID, wStepID, wSubmitID);

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

	@GetMapping({ "/PreCheckRecord" })
	public Object PreCheckRecord(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID")).intValue();

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			int wStepID = StringUtils.parseInt(request.getParameter("StepID")).intValue();

			int wRecordType = StringUtils.parseInt(request.getParameter("RecordType")).intValue();

			ServiceResult<List<IPTCheckRecord>> wServiceResult = this.wIPTService.IPT_QueryPreCheckRecord(wLoginUser,
					wOrderID, wStartTime, wEndTime, wStepID, wRecordType);

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

	@GetMapping({ "/ProcessRecord" })
	public Object ProcessRecord(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID")).intValue();

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));

			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			int wStationID = StringUtils.parseInt(request.getParameter("StationID")).intValue();

			int wStepID = StringUtils.parseInt(request.getParameter("StepID")).intValue();

			int wRecordType = StringUtils.parseInt(request.getParameter("RecordType")).intValue();

			int wSubmitID = StringUtils.parseInt(request.getParameter("SubmitID")).intValue();

			boolean wIsQuality = StringUtils.parseBoolean(request.getParameter("IsQuality")).booleanValue();

			ServiceResult<List<IPTCheckRecord>> wServiceResult = this.wIPTService.IPT_QueryProcessRecord(wLoginUser,
					wOrderID, wStartTime, wEndTime, wStationID, wStepID, wRecordType, wSubmitID, wIsQuality);

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

	@GetMapping({ "/OutCheckRecord" })
	public Object OutCheckRecord(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID")).intValue();

			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			int wPartID = StringUtils.parseInt(request.getParameter("PartID")).intValue();

			int wStepID = StringUtils.parseInt(request.getParameter("StepID")).intValue();

			int wSubmitID = StringUtils.parseInt(request.getParameter("SubmitID")).intValue();

			ServiceResult<List<IPTCheckRecord>> wServiceResult = this.wIPTService.IPT_QueryOutCheckRecord(wLoginUser,
					wOrderID, wStartTime, wEndTime, wPartID, wStepID, wSubmitID);

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
	 * 导出生产过程检验报告
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@GetMapping("/ExportProduceProcess")
	public Object ExportProduceProcess(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<String> wServiceResult = wIPTService.ExportProduceProcess(wLoginUser, wOrderID);

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
	 * 导出生产过程检验报告(zip)
	 */
	@ResponseBody
	@GetMapping("/ExportProduceProcessZip")
	public Object ExportProduceProcessZip(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			ServiceResult<String> wServiceResult = wIPTService.ExportProduceProcessZip(wLoginUser, wOrderID, wPartID);

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
	 * 导出质量过程检验报告
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@GetMapping("/ExportQualityProcess")
	public Object ExportQualityProcess(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<String> wServiceResult = wIPTService.ExportQualityProcess(wLoginUser, wOrderID);

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
	 * 导出质量过程检验报告
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@GetMapping("/ExportQualityProcessZip")
	public Object ExportQualityProcessZip(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<String> wServiceResult = wIPTService.ExportQualityProcessZip(wLoginUser, wOrderID);

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
	 * 导出终检报告
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@GetMapping("/ExportFinalCheck")
	public Object ExportFinalCheck(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<String> wServiceResult = wIPTService.ExportFinalCheck(wLoginUser, wOrderID);

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
	 * 导出终检报告
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@GetMapping("/ExportFinalCheckZip")
	public Object ExportFinalCheckZip(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<String> wServiceResult = wIPTService.ExportFinalCheckZip(wLoginUser, wOrderID);

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
	 * 导出出厂检报告
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@GetMapping("/ExportOutCheck")
	public Object ExportOutCheck(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<String> wServiceResult = wIPTService.ExportOutCheck(wLoginUser, wOrderID);

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
	 * 导出出厂检报告
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@GetMapping("/ExportOutCheckZip")
	public Object ExportOutCheckZip(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<String> wServiceResult = wIPTService.ExportOutCheckZip(wLoginUser, wOrderID);

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
}
