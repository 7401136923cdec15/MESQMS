package com.mes.qms.server.controller.focas;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.FPCRouteImportService;
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.utils.RetCode;

@RestController
@RequestMapping("/api/BFCMessage")
public class BFCMessageController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(BFCMessageController.class);

	@Autowired
	SFCService wSFCService;

	@Autowired
	FPCRouteImportService wFPCRouteImportService;

	/**
	 * 消息单条数据查询
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

			Integer wMessageID = StringUtils.parseInt(request.getParameter("MessageID"));

			ServiceResult<BFCMessage> wServiceResult = wFPCRouteImportService.BFC_QueryMessageInfo(wLoginUser,
					wMessageID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
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
	 * 获取待办消息列表
	 */
	@GetMapping("/ToDoList")
	public Object ToDoList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			String wLoginID = StringUtils.parseString(request.getParameter("user"));

			ServiceResult<String> wServiceResult = wSFCService.SFC_QueryMessageToDoList(BaseDAO.SysAdmin, wLoginID);

			wResult = wServiceResult.Result;
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 获取待阅消息列表
	 */
	@GetMapping("/ToReadList")
	public Object ToReadList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			String wLoginID = StringUtils.parseString(request.getParameter("user"));

			ServiceResult<String> wServiceResult = wSFCService.SFC_QueryMessageToReadList(BaseDAO.SysAdmin, wLoginID);

			wResult = wServiceResult.Result;
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 关闭消息(工艺变更-不合格评审消息)
	 */
	@GetMapping("/CloseMessage")
	public Object CloseMessage(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wStepID = StringUtils.parseInt(request.getParameter("StepID"));

			ServiceResult<String> wServiceResult = wSFCService.BFC_CloseMessage(wLoginUser, wOrderID, wPartID, wStepID);

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
	 * 关闭消息
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/CloseAllMessage")
	public Object CloseAllMessage(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wModuleID = StringUtils.parseInt(request.getParameter("ModuleID"));
			int wMessageID = StringUtils.parseInt(request.getParameter("MessageID"));

			ServiceResult<Integer> wServiceResult = wSFCService.BFC_CloseAllMessage(wLoginUser, wModuleID, wMessageID);

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
