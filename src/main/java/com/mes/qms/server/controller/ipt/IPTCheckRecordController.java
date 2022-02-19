package com.mes.qms.server.controller.ipt;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.IPTService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.record.IPTExportCheckRecord;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 检验记录导出校验控制器
 * 
 * @author PengYouWang
 * @CreateTime 2020-10-27 09:58:34
 * @LastEditTime 2020-10-27 09:58:37
 */
@RestController
@RequestMapping("/api/IPTCheckRecord")
public class IPTCheckRecordController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(IPTCheckRecordController.class);

	@Autowired
	IPTService wIPTService;

	/**
	 * 条件查询
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

			int wID = StringUtils.parseInt(request.getParameter("ID"));
			String wCode = StringUtils.parseString(request.getParameter("Code"));
			int wOperateID = StringUtils.parseInt(request.getParameter("OperateID"));
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<IPTExportCheckRecord>> wServiceResult = wIPTService.IPT_QueryCheckRecordList(wLoginUser,
					wID, wCode, wOperateID, wOrderID, wStartTime, wEndTime);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
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
	 * 导出校验
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ExportVerificate")
	public Object ExportVerificate(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			String wCode = StringUtils.parseString(request.getParameter("Code"));

			ServiceResult<IPTExportCheckRecord> wServiceResult = wIPTService.IPT_ExportVerificate(wLoginUser, wOrderID,
					wCode);

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
	 * 获取校验进度
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/VerificateProgress")
	public Object VerificateProgress(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			String wCode = StringUtils.parseString(request.getParameter("Code"));

			ServiceResult<IPTExportCheckRecord> wServiceResult = wIPTService.IPT_VerificateProgress(wLoginUser, wCode);

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
