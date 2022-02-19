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
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.focas.FocasPart;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

@RestController
@RequestMapping("/api/MSSPart")
public class MSSPartController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MSSPartController.class);

	@Autowired
	SFCService wSFCService;

	/**
	 * 根据车号获取台车部件
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/MSSPartList")
	public Object MSSPartList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));

			ServiceResult<List<FocasPart>> wServiceResult = wSFCService.SFC_QueryMSSPartList(wLoginUser, wPartNo);

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
