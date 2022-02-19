package com.mes.qms.server.controller.ncr;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.FPCRouteImportService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ncr.NCRTask;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 不合格评审任务控制器
 */
@RestController
@RequestMapping("/api/NCRTask")
public class NCRTaskController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(NCRTaskController.class);

	@Autowired
	FPCRouteImportService wFPCRouteImportService;

	/**
	 * 导出不合格评审
	 */
	@PostMapping("/Export")
	public Object StepPlanList(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			List<NCRTask> wList = CloneTool.CloneArray(wParam.get("data"), NCRTask.class);

			if (wList == null || wList.size() <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
			}

			ServiceResult<String> wServiceResult = wFPCRouteImportService.NCR_ExportTaskList(wLoginUser, wList);

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
