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
import com.mes.qms.server.service.po.mss.MSSBOM;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.mss.MSSBOMItemC;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

@RestController
@RequestMapping("/api/MSSBom")
public class MSSBomController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MSSBomController.class);

	@Autowired
	FPCRouteImportService wFPCRouteImportService;

	/**
	 * 标准BOM数据对比
	 */
	@GetMapping("/Compare")
	public Object Compare(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wABOMID = StringUtils.parseInt(request.getParameter("ABOMID"));
			int wBBOMID = StringUtils.parseInt(request.getParameter("BBOMID"));

			ServiceResult<List<MSSBOMItemC>> wServiceResult = wFPCRouteImportService.FPC_CompareBOM(wLoginUser, wABOMID,
					wBBOMID);

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
	 * 导出标准BOM
	 */
	@PostMapping("/ExportBOM")
	public Object ExportBOM(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			List<MSSBOM> wItemList = CloneTool.CloneArray(wParam.get("data"), MSSBOM.class);

			if (wItemList == null || wItemList.size() <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：" + RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<String> wServiceResult = wFPCRouteImportService.IPT_ExportMSSBOM(wLoginUser, wItemList);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "提示：导出成功!", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 导出标准BOM子项
	 * 
	 * @param request
	 * @param response
	 */
	@PostMapping("/ExportBOMItem")
	public Object ExportBOMItem(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wBOMID = StringUtils.parseInt(wParam.get("BOMID"));

			ServiceResult<String> wServiceResult = wFPCRouteImportService.IPT_ExportMSSBOMItem(wLoginUser, wBOMID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "提示：导出成功!", null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询标准bom子项
	 */
	@GetMapping("/BomItemList")
	public Object BomItemList(HttpServletRequest request) {
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

			ServiceResult<List<MSSBOMItem>> wServiceResult = wFPCRouteImportService.MSS_QueryBomItemList(wLoginUser,
					wOrderID, wPartID, wStepID);

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
	 * 条件查询标准bom子项
	 */
	@GetMapping("/BomItemAll")
	public Object BomItemAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wBOMID = StringUtils.parseInt(request.getParameter("BOMID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wStepID = StringUtils.parseInt(request.getParameter("StepID"));
			String wMaterialNo = StringUtils.parseString(request.getParameter("MaterialNo"));
			String wMaterialName = StringUtils.parseString(request.getParameter("MaterialName"));
			int wReplaceType = StringUtils.parseInt(request.getParameter("ReplaceType"));
			int wOutsourceType = StringUtils.parseInt(request.getParameter("OutsourceType"));

			if (wBOMID <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, "提示：参数错误，BOM主键不能为0!");
			}

			ServiceResult<List<MSSBOMItem>> wServiceResult = wFPCRouteImportService.MSS_QueryBomItemAll(wLoginUser,
					wBOMID, wPartID, wStepID, wMaterialNo, wMaterialName, wReplaceType, wOutsourceType);

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
	 * 批量激活、禁用标准BOM子项
	 */
	@PostMapping("/ActiveList")
	public Object ActiveList(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			List<Integer> wIDList = CloneTool.CloneArray(wParam.get("data"), Integer.class);
			int wActive = StringUtils.parseInt(wParam.get("Active"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.MSS_ActiveListBomItem(wLoginUser, wIDList,
					wActive);

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
