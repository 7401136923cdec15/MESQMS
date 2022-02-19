package com.mes.qms.server.controller.fpc;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.CoreService;
import com.mes.qms.server.service.FPCRouteImportService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.fpc.FPCFlowPart;
import com.mes.qms.server.service.po.fpc.FPCPartPoint;
import com.mes.qms.server.service.po.fpc.FPCRouteC;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;
import com.mes.qms.server.utils.qms.ExcelReader;

@RestController
@RequestMapping("/api/FPCRoute")
public class FPCRouteController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(FPCRouteController.class);

	@Autowired
	FPCRouteImportService wFPCRouteImportService;

	@Autowired
	CoreService wCoreService;

	/**
	 * 导入工艺BOP
	 * 
	 * @param request
	 * @param files   Excel文件
	 * @return
	 */
	@PostMapping("/Import")
	public Object Import(HttpServletRequest request, @RequestParam("file") MultipartFile[] files) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (files.length == 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, "提示：没有要上传的Excel文件！");
			}

			ServiceResult<Integer> wServiceResult = new ServiceResult<Integer>();
			ServiceResult<ExcelData> wExcelData = null;
			String wOriginalFileName = null;
			for (MultipartFile wMultipartFile : files) {
				wOriginalFileName = wMultipartFile.getOriginalFilename();

				if (wOriginalFileName.contains("xlsx"))
					wExcelData = ExcelReader.getInstance().readMultiSheetExcel(wMultipartFile.getInputStream(),
							wOriginalFileName, "xlsx", 1000000);
				else if (wOriginalFileName.contains("xls"))
					wExcelData = ExcelReader.getInstance().readMultiSheetExcel(wMultipartFile.getInputStream(),
							wOriginalFileName, "xls", 1000000);

				if (StringUtils.isNotEmpty(wExcelData.FaultCode)) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wExcelData.FaultCode);
					return wResult;
				}

				if (wExcelData == null || wExcelData.Result.sheetData == null || wExcelData.Result.sheetData.size() <= 0
						|| wExcelData.Result.sheetData.get(0).lineData == null
						|| wExcelData.Result.sheetData.get(0).lineData.size() <= 0
						|| wExcelData.Result.sheetData.get(0).lineData.get(0).colData == null
						|| wExcelData.Result.sheetData.get(0).lineData.get(0).colData.size() <= 0
						|| !wExcelData.Result.sheetData.get(0).lineData.get(0).colData.get(0).equals("工艺BOP编号")) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：Excel格式不正确!");
					return wResult;
				}

				wServiceResult = wFPCRouteImportService.FPC_ImportRoute(wLoginUser, wExcelData.Result,
						wOriginalFileName);

				if (!StringUtils.isEmpty(wServiceResult.FaultCode)) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
					return wResult;
				}
			}

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "导入成功!", null, wServiceResult.Result);
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
	 * 工艺数据对比3456
	 * 
	 * @param request
	 * @return
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
			int wARouteID = StringUtils.parseInt(request.getParameter("ARouteID"));
			int wBRouteID = StringUtils.parseInt(request.getParameter("BRouteID"));

			ServiceResult<List<FPCRouteC>> wServiceResult = wFPCRouteImportService.FPC_Compare(wLoginUser, wARouteID,
					wBRouteID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.CustomResult.get("CompareList"), null);
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
	 * 导出工序明细(项点模板)
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@GetMapping("/ExportSteps")
	public Object ExportSteps(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			ServiceResult<String> wServiceResult = wFPCRouteImportService.FPC_ExportSteps(wLoginUser, wProductID,
					wLineID, wPartID);

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
	 * 复制工艺BOP
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/CopyBOP")
	public Object CopyBOP(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wSourceRouteID = StringUtils.parseInt(request.getParameter("SourceRouteID"));
			int wTargetRouteID = StringUtils.parseInt(request.getParameter("TargetRouteID"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.FPC_CopyBOP(wLoginUser, wSourceRouteID,
					wTargetRouteID);

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
	 * 复制工艺BOM
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/CopyBOM")
	public Object CopyBOM(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wSourceBOMID = StringUtils.parseInt(request.getParameter("SourceBOMID"));
			int wTargetBOMID = StringUtils.parseInt(request.getParameter("TargetBOMID"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.FPC_CopyBOM(wLoginUser, wSourceBOMID,
					wTargetBOMID);

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
	 * 复制产线单元明细
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/CopyLineUnit")
	public Object CopyLineUnit(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wSProductID = StringUtils.parseInt(request.getParameter("SProductID"));
			int wSLineID = StringUtils.parseInt(request.getParameter("SLineID"));

			int wTProductID = StringUtils.parseInt(request.getParameter("TProductID"));
			int wTLineID = StringUtils.parseInt(request.getParameter("TLineID"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.FPC_CopyLineUnit(wLoginUser, wSProductID,
					wSLineID, wTLineID, wTProductID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "复制成功!", null, wServiceResult.Result);
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
	 * 逆向同步产线单元明细
	 */
	@GetMapping("/SynchronizeLineUnit")
	public Object SynchronizeLineUnit(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wRouteID = StringUtils.parseInt(request.getParameter("RouteID"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.FPC_SynchronizeLineUnit(wLoginUser,
					wRouteID);

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
	 * 动态比对工艺bop
	 */
	@GetMapping("/DynamicCompareBop")
	public Object DynamicCompareBop(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wNewRouteID = StringUtils.parseInt(request.getParameter("NewRouteID"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.FPC_DynamicCompareBop(wLoginUser,
					wNewRouteID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, null);

				// ①正在使用的工艺bopID
				this.SetResult(wResult, "OldRouteID", wServiceResult.Get("OldRouteID"));
				// ①对比的工艺bopID
				this.SetResult(wResult, "NewRouteID", wServiceResult.Get("NewRouteID"));
				// ①新增的工序列表
				this.SetResult(wResult, "AddedList", wServiceResult.Get("AddedList"));
				// ①移除的工序列表
				this.SetResult(wResult, "RemovedList", wServiceResult.Get("RemovedList"));
				// ①改变工位的工序列表
				this.SetResult(wResult, "ChangedList", wServiceResult.Get("ChangedList"));
				// ①改变工艺文件的工序列表
				this.SetResult(wResult, "UpdatedList", wServiceResult.Get("UpdatedList"));
				// ①受影响的车辆列表
				this.SetResult(wResult, "PartNoList", wServiceResult.Get("PartNoList"));
				// ①受影响的订单ID列表
				this.SetResult(wResult, "OrderIDList", wServiceResult.Get("OrderIDList"));
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 动态调整工艺bop
	 */
	@PostMapping("/DynamicTurnBop")
	public Object DynamicTurnBop(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOldRouteID = StringUtils.parseInt(wParam.get("OldRouteID"));
			int wNewRouteID = StringUtils.parseInt(wParam.get("NewRouteID"));
			int wChangeLogID = StringUtils.parseInt(wParam.get("ChangeLogID"));

			List<FPCRoutePartPoint> wAddedList = CloneTool.CloneArray(wParam.get("AddedList"), FPCRoutePartPoint.class);
			List<FPCRoutePartPoint> wRemovedList = CloneTool.CloneArray(wParam.get("RemovedList"),
					FPCRoutePartPoint.class);
			List<FPCRoutePartPoint> wChangedList = CloneTool.CloneArray(wParam.get("ChangedList"),
					FPCRoutePartPoint.class);
			// 修改了工艺文件的工序
			List<FPCRoutePartPoint> wUpdatedList = CloneTool.CloneArray(wParam.get("UpdatedList"),
					FPCRoutePartPoint.class);
			List<String> wPartNoList = CloneTool.CloneArray(wParam.get("PartNoList"), String.class);
			List<Integer> wReworkOrdreIDList = CloneTool.CloneArray(wParam.get("ReworkOrdreIDList"), Integer.class);

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.FPC_DynamicTurnBop(wLoginUser, wOldRouteID,
					wAddedList, wRemovedList, wChangedList, wPartNoList, wNewRouteID, wReworkOrdreIDList, wUpdatedList,
					wChangeLogID);

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
	 * 根据工位类型查询终检、出厂检、预检工序列表
	 */
	@GetMapping("/StepList")
	public Object StepList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wPartType = StringUtils.parseInt(request.getParameter("PartType"));

			ServiceResult<List<FPCPartPoint>> wServiceResult = wFPCRouteImportService
					.FPC_QueryStepListByPartType(wLoginUser, wPartType);

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
	 * 批量修改同车型、修程、局段的转序控制属性
	 */
	@PostMapping("/BatchUpdateChangeControl")
	public Object BatchUpdateChangeControl(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 【转序控制】权限控制
			if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 1340071, 0, 0)
					.Info(Boolean.class)) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
			}

			// 获取参数
			int wRouteID = StringUtils.parseInt(wParam.get("RouteID"));
			int wPartID = StringUtils.parseInt(wParam.get("PartID"));
			int wChangeControl = StringUtils.parseInt(wParam.get("ChangeControl"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.FPC_BatchUpdateChangeControl(wLoginUser,
					wRouteID, wPartID, wChangeControl);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "设置成功。", null, wServiceResult.Result);
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
	 * 获取工序列表(产线单元明细)
	 */
	@GetMapping("/LineUnitStepList")
	public Object LineUnitStepList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			ServiceResult<List<FPCPartPoint>> wServiceResult = wFPCRouteImportService
					.FMC_QueryLineUnitStepList(wLoginUser, wProductID, wLineID, wPartID);

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
	 * 根据工艺路线ID获取流程图数据
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/FlowDataPart")
	public Object FlowDataPart(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wRouteID = StringUtils.parseInt(request.getParameter("RouteID"));

			ServiceResult<List<FPCFlowPart>> wServiceResult = wFPCRouteImportService.FPC_QueryFlowDataPart(wLoginUser,
					wRouteID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result,
						wServiceResult.CustomResult.get("LineList"));
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
	 * 获取工艺工位(车体在前，转向架在后)
	 */
	@GetMapping("/RoutePartList")
	public Object RoutePartList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wRouteID = StringUtils.parseInt(request.getParameter("RouteID"));

			ServiceResult<List<FPCRoutePart>> wServiceResult = wFPCRouteImportService.FPC_QeuryRoutePartList(wLoginUser,
					wRouteID);

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
	 * 同步专检控制
	 */
	@PostMapping("/UpdateSpecialControl")
	public Object UpdateSpecialControl(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			FPCRoutePart wFPCRoutePart = CloneTool.Clone(wParam.get("data"), FPCRoutePart.class);

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.FPC_UpdateSpecialControl(wLoginUser,
					wFPCRoutePart);

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
	 * 调整工序的工位，BOP和工序任务对应变化。
	 */
	@GetMapping("/AdjustStep")
	public Object AdjustStep(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wPartPointID = StringUtils.parseInt(request.getParameter("PartPointID"));
			int wRightPartID = StringUtils.parseInt(request.getParameter("RightPartID"));

			if (wPartPointID <= 0 || wRightPartID <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
			}

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.FPC_AdjustStep(wLoginUser, wPartPointID,
					wRightPartID);

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
	 * 导出工艺BOP
	 */
	@ResponseBody
	@GetMapping("/ExportBOP")
	public Object ExportBOP(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wRouteID = StringUtils.parseInt(request.getParameter("RouteID"));

			ServiceResult<String> wServiceResult = wFPCRouteImportService.FPC_ExportBOP(wLoginUser, wRouteID);

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
