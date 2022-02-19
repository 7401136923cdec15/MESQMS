package com.mes.qms.server.controller.focas;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.focas.FocasHistoryData;
import com.mes.qms.server.service.po.focas.FocasResult;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.utils.RetCode;

@RestController
@RequestMapping("/api/FocasHistoryData")
public class FocasHistoryDataController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(FocasHistoryDataController.class);

	@Autowired
	SFCService wSFCService;

	/**
	 * 获取近三年统计数据
	 */
	@GetMapping("/History")
	public Object History(HttpServletRequest request) {
		Object wResult = new Object();
		try {
//			if (CheckCookieEmpty(request)) {
//				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
//				return wResult;
//			}

//			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<FocasHistoryData>> wServiceResult = wSFCService
					.SFC_QueryFocasHistoryDataList(BaseDAO.SysAdmin);

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
	 * 获取月度统计数据
	 */
	@GetMapping("/MonthData")
	public Object MonthData(HttpServletRequest request) {
		Object wResult = new Object();
		try {
//			if (CheckCookieEmpty(request)) {
//				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
//				return wResult;
//			}

//			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<FocasResult>> wServiceResult = wSFCService.SFC_QueryFocasMonthData(BaseDAO.SysAdmin);

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
