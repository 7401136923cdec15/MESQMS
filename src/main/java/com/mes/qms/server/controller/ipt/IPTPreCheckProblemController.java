package com.mes.qms.server.controller.ipt;

import java.util.Calendar;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.CoreService;
import com.mes.qms.server.service.IPTService;
import com.mes.qms.server.service.LOCOAPSService;
import com.mes.qms.server.service.QMSService;
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.mesenum.IPTProblemActionType;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblemCar;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;
import com.mes.qms.server.utils.qms.ExcelReader;

/**
 * 预检问题项控制器
 * 
 * @author PengYouWang
 * @CreateTime 2020-2-18 19:18:25
 * @LastEditTime 2020-2-18 19:18:30
 */
@RestController
@RequestMapping("/api/IPTPreCheckProblem")
public class IPTPreCheckProblemController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(IPTPreCheckProblemController.class);

	@Autowired
	IPTService wIPTService;

	@Autowired
	SFCService wSFCService;

	@Autowired
	QMSService wQMSService;

	@Autowired
	CoreService wCoreService;

	@Autowired
	LOCOAPSService wLOCOAPSService;

	/**
	 * 查单条任务
	 * 
	 * @param request
	 * @return
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

			int wID = StringUtils.parseInt(request.getParameter("ID"));

			ServiceResult<IPTPreCheckProblem> wServiceResult = wIPTService.IPT_QueryPreCheckProblem(wLoginUser, wID);

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
	 * 更新预检问题项
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/Update")
	public Object SaveTaskItemList(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			IPTPreCheckProblem wData = CloneTool.Clone(wParam.get("data"), IPTPreCheckProblem.class);

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_SavePreCheckProblem(wLoginUser, wData);

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
	 * 条件查询任务
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

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<IPTPreCheckProblem>> wServiceResult = wIPTService.IPT_QueryProblemList(wLoginUser,
					wOrderID, wProductID, wLineID, -1, -1, wStartTime, wEndTime);

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
	 * 获取问题项各节点信息
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/NodeInfo")
	public Object NodeInfo(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wProblemID = StringUtils.parseInt(request.getParameter("ProblemID"));

			ServiceResult<IPTPreCheckProblem> wServiceResult = wSFCService.SFC_QueryProblemNodeInfo(wLoginUser,
					wProblemID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
				this.SetResult(wResult, "PreCheckNode", wServiceResult.CustomResult.get("PreCheckNode"));
				this.SetResult(wResult, "SelfCheckNode", wServiceResult.CustomResult.get("SelfCheckNode"));
				this.SetResult(wResult, "MutualCheckNode", wServiceResult.CustomResult.get("MutualCheckNode"));
				this.SetResult(wResult, "SpecialCheckNode", wServiceResult.CustomResult.get("SpecialCheckNode"));
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
	 * 用人拿任务
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAll")
	public Object EmployeeAll(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<IPTPreCheckProblem>> wServiceResult = wIPTService
					.IPT_QueryPreCheckProblemByEmployee(wLoginUser);

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
	 * 获取车分类的问题项
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAllCar")
	public Object EmployeeAllCar(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<IPTPreCheckProblemCar>> wServiceResult = wIPTService.IPT_QueryEmployeeAllCar(wLoginUser);

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
	 * 根据订单获取问题项-车分层
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAllItemList")
	public Object EmployeeAllItemList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<IPTPreCheckProblem>> wServiceResult = wIPTService
					.IPT_QueryEmployeeAllItemList(wLoginUser, wOrderID);

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
	 * 处理预检问题项
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/Handle")
	public Object Handle(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("data") || !wParam.containsKey("ActionType")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			IPTProblemActionType wActionType = IPTProblemActionType
					.getEnumType(StringUtils.parseInt(wParam.get("ActionType")));

			String wRemark = StringUtils.parseString(wParam.get("Remark"));

			List<IPTPreCheckProblem> wList = CloneTool.CloneArray(wParam.get("data"), IPTPreCheckProblem.class);

			if (wList == null || wList.size() <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_HandlePreCheckProblem(wLoginUser, wActionType,
					wList, wRemark);

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
	 * 班组长查询派工问题项的集合(作为派工的数据源)
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/PGProblemList")
	public Object ProblemList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<IPTPreCheckProblem>> wServiceResult = wIPTService.IPT_QueryPGProblemList(wLoginUser);

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
	 * 保存问题项派工人员
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/SavePGPerson")
	public Object SavePGPerson(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wPersonID = StringUtils.parseInt(wParam.get("PersonID"));
			IPTPreCheckProblem wData = CloneTool.Clone(wParam.get("data"), IPTPreCheckProblem.class);
			if (wData == null || wData.ID <= 0 || wPersonID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_SavePGPerson(wLoginUser, wPersonID, wData);

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
	 * 界面添加预检问题项(段该项目)
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/AddProblems")
	public Object AddProblems(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// WBS号
			String wWBSNo = StringUtils.parseString(wParam.get("WBSNo"));
			// 车号
			String wPartNo = StringUtils.parseString(wParam.get("PartNo"));
			// 段改项目
			String wPeriodChangeItem = StringUtils.parseString(wParam.get("PeriodChangeItem"));
			// 段方要求
			String wRequirements = StringUtils.parseString(wParam.get("Requirements"));
			// 子订单号
			String wOrderNo = StringUtils.parseString(wParam.get("OrderNo"));

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_AddProblems(wLoginUser, wWBSNo, wPartNo,
					wPeriodChangeItem, wRequirements, wOrderNo);

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
	 * 导入问题项
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
							wOriginalFileName, "xlsx", 5000);
				else if (wOriginalFileName.contains("xls"))
					wExcelData = ExcelReader.getInstance().readMultiSheetExcel(wMultipartFile.getInputStream(),
							wOriginalFileName, "xls", 5000);

				if (StringUtils.isNotEmpty(wExcelData.FaultCode)) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wExcelData.FaultCode);
					return wResult;
				}

				if (wExcelData == null || wExcelData.Result.sheetData == null || wExcelData.Result.sheetData.size() <= 0
						|| wExcelData.Result.sheetData.get(0).lineData == null
						|| wExcelData.Result.sheetData.get(0).lineData.size() <= 0
						|| wExcelData.Result.sheetData.get(0).lineData.get(0).colData == null
						|| wExcelData.Result.sheetData.get(0).lineData.get(0).colData.size() <= 0) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：Excel格式不正确!");
					return wResult;
				}

				wServiceResult = wIPTService.IPT_ImportProblems(wLoginUser, wExcelData.Result);

				if (!StringUtils.isEmpty(wServiceResult.FaultCode)) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
					return wResult;
				}
			}

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "导入成功!", null, null);
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
	 * 跳过评审
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/SkipAudit")
	public Object SkipAudit(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			IPTPreCheckProblem wData = CloneTool.Clone(wParam.get("data"), IPTPreCheckProblem.class);

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_SkipAudit(wLoginUser, wData);

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
