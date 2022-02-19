package com.mes.qms.server.controller.mss;

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
import com.mes.qms.server.service.FPCRouteImportService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.mss.MSSPartItem;
import com.mes.qms.server.service.po.mss.MSSRepairRecord;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 自修件拆解、维修、组装控制器
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-7-1 15:59:55
 */
@RestController
@RequestMapping("/api/MSSRepairRecord")
public class MSSRepairRecordController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MSSRepairRecordController.class);

	@Autowired
	FPCRouteImportService wFPCRouteImportService;

	/**
	 * 自修件入库
	 */
	@PostMapping("/SRPartInstock")
	public Object PartInstock(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			SFCTaskIPT wTaskIPT = CloneTool.Clone(wParam.get("SFCTaskIPT"), SFCTaskIPT.class);
			IPTItem wItem = CloneTool.Clone(wParam.get("IPTItem"), IPTItem.class);
			IPTValue wValue = CloneTool.Clone(wParam.get("IPTValue"), IPTValue.class);
			Integer wOperateType = StringUtils.parseInt(wParam.get("OperateType"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.MSS_SRPartInstock(wLoginUser, wTaskIPT,
					wItem, wValue, wOperateType);

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
	 * 自修件待维修列表
	 */
	@GetMapping("/SRRepairList")
	public Object RepairList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			String wPartsCode = StringUtils.parseString(request.getParameter("PartCode"));

			ServiceResult<List<MSSPartItem>> wServiceResult = wFPCRouteImportService.MSS_QuerySRRepairList(wLoginUser,
					wOrderID, wPartsCode);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result,
						wServiceResult.CustomResult.get("Material"));
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
	 * 自修件待组装列表
	 */
	@GetMapping("/SRAssembleList")
	public Object AssembleList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			String wPartsCode = StringUtils.parseString(request.getParameter("PartCode"));

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<MSSPartItem>> wServiceResult = wFPCRouteImportService.MSS_QuerySRAssembleList(wLoginUser,
					wProductID, wLineID, wPartsCode, wOrderID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result,
						wServiceResult.CustomResult.get("Material"));

				this.SetResult(wResult, "Number", wServiceResult.CustomResult.get("Number"));
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
	 * 获取自修件拆解记录
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
			int wOperateType = StringUtils.parseInt(request.getParameter("OperateType"));

			ServiceResult<List<MSSRepairRecord>> wServiceResult = wFPCRouteImportService
					.MSS_QueryAllSRPartRecord(wLoginUser, wOrderID, wOperateType);

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
	 * 根据订单获取自修件拆解列表
	 */
	@GetMapping("/SRList")
	public Object SRList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<MSSPartItem>> wServiceResult = wFPCRouteImportService.MSS_QuerySRList(wLoginUser,
					wOrderID);

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
