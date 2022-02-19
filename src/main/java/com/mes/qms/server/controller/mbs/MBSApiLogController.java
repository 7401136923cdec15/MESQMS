package com.mes.qms.server.controller.mbs;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.FPCRouteImportService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.mbs.MBSApiLog;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

@RestController
@RequestMapping("/api/MBSApiLog")
public class MBSApiLogController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MBSApiLogController.class);

	@Autowired
	FPCRouteImportService wFPCRouteImportService;

	/**
	 * 获取分页的数据
	 */
	@GetMapping("/PageAll")
	public Object PageAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wPageSize = StringUtils.parseInt(request.getParameter("PageSize"));
			int wCurPage = StringUtils.parseInt(request.getParameter("CurPage"));

			int wLoginID = StringUtils.parseInt(request.getParameter("LoginID"));
			String wProjectName = StringUtils.parseString(request.getParameter("ProjectName"));
			String wURI = StringUtils.parseString(request.getParameter("URI"));

			ServiceResult<List<MBSApiLog>> wServiceResult = wFPCRouteImportService.MBS_QueryPageAll(wLoginUser,
					wPageSize, wCurPage, wLoginID, wProjectName, wURI);

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
	 * 获取总记录数
	 */
	@GetMapping("/RecordSize")
	public Object RecordSize(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wLoginID = StringUtils.parseInt(request.getParameter("LoginID"));
			String wProjectName = StringUtils.parseString(request.getParameter("ProjectName"));
			String wURI = StringUtils.parseString(request.getParameter("URI"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.MBS_QueryRecordSize(wLoginUser, wLoginID,
					wProjectName, wURI);

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
	 * 清空表数据
	 */
	@GetMapping("/ClearData")
	public Object ClearData(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.MBS_ClearData(wLoginUser);

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
