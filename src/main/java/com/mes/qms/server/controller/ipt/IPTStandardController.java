package com.mes.qms.server.controller.ipt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.mes.qms.server.service.mesenum.IPTMode;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.imp.IMPResultRecord;
import com.mes.qms.server.service.po.ipt.IPTConfigs;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.ipt.IPTStandard;
import com.mes.qms.server.service.po.ipt.IPTStandardC;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;
import com.mes.qms.server.utils.qms.ExcelReader;

@RestController
@RequestMapping("/api/IPTStandard")
public class IPTStandardController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(IPTStandardController.class);

	@Autowired
	IPTService wIPTService;

	@Autowired
	CoreService wCoreService;

	@GetMapping("/IPTConfig")
	public Object IPTConfig(HttpServletRequest request) {
		Object wResult = new Object();
		try {

			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<IPTConfigs> wServiceResult = wIPTService.IPT_GetIPTConfig(wLoginUser);

			IPTConfigs wServerRst = wServiceResult.Result;

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServerRst);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode, null, wServerRst);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/IPTValue")
	public Object IPTValue(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			String wFaultCode = "";

			BMSEmployee wLoginUser = GetSession(request);

			int wTaskID = StringUtils.parseInt(request.getParameter("TaskID"));
			int wIPTMode = StringUtils.parseInt(request.getParameter("IPTMode"));
			int wIPTItemType = StringUtils.parseInt(request.getParameter("IPTItemType"));

			ServiceResult<List<IPTValue>> wServiceResult = wIPTService.IPT_GetIPTValueByTaskID(wLoginUser, wTaskID,
					wIPTMode, wIPTItemType);

			List<IPTValue> wServerRst = wServiceResult.Result;

			if (StringUtils.isEmpty(wFaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wFaultCode, wServerRst, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wFaultCode, wServerRst, null);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/IPTValueAll")
	public Object IPTValueAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			List<Integer> wTaskIDList = CloneTool.CloneArray(request.getParameter("TaskIDList"), Integer.class);

			int wIPTMode = StringUtils.parseInt(request.getParameter("IPTMode"));
			int wIPTItemType = StringUtils.parseInt(request.getParameter("IPTItemType"));

			ServiceResult<Map<Integer, List<IPTValue>>> wServiceResult = wIPTService.IPT_GetIPTValue(wLoginUser,
					wTaskIDList, wIPTMode, wIPTItemType);
			Map<Integer, List<IPTValue>> wIPTValueListDic = wServiceResult.Result;

			Map<String, List<IPTValue>> wServerRst = new HashMap<String, List<IPTValue>>();
			for (Integer wInteger : wTaskIDList) {
				if (!wIPTValueListDic.containsKey(wInteger)) {
					continue;
				}

				if (!wServerRst.containsKey(wInteger.toString())) {
					wServerRst.put(wInteger.toString(), wIPTValueListDic.get(wInteger));
				}
			}

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wServiceResult.FaultCode, null, wServerRst);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode, null, wServerRst);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/Current")
	public Object Current(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);
			@SuppressWarnings("unused")
			int wUserID = wBMSEmployee.ID;
			int wCompanyID = wBMSEmployee.CompanyID;
			@SuppressWarnings("unused")
			String wUserName = wBMSEmployee.Name;

			Integer wITPMode = StringUtils.parseInt(request.getParameter("IPTMode"));
			int wBusinessUnitID = StringUtils.parseInt(request.getParameter("BusinessUnitID"));
			int wBaseID = StringUtils.parseInt(request.getParameter("BaseID"));
			int wFactoryID = StringUtils.parseInt(request.getParameter("FactoryID"));
			Integer wWorkShopID = StringUtils.parseInt(request.getParameter("WorkShopID"));

			Integer wLineID = StringUtils.parseInt(request.getParameter("LineID"));

			Integer wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			Integer wPartPointID = StringUtils.parseInt(request.getParameter("PartPointID"));
			Integer wStationID = StringUtils.parseInt(request.getParameter("StationID"));
			String wProductNo = StringUtils.parseString(request.getParameter("ProductNo"));
			int wCustomID = StringUtils.parseInt(request.getParameter("CustomID"));

			ServiceResult<IPTStandard> wServiceResult = wIPTService.IPT_GetStandardCurrent(wBMSEmployee, wCompanyID,
					IPTMode.getEnumType(wITPMode), wCustomID, wBusinessUnitID, wBaseID, wFactoryID, wWorkShopID,
					wLineID, wPartID, wPartPointID, wStationID, wProductNo);
			IPTStandard wServerRst = wServiceResult.Result;

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServerRst);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), null, wServerRst);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/LineCurrent")
	public Object LineCurrent(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Integer wITPMode = StringUtils.parseInt(request.getParameter("IPTMode"));
			int wBusinessUnitID = StringUtils.parseInt(request.getParameter("BusinessUnitID"));
			int wBaseID = StringUtils.parseInt(request.getParameter("BaseID"));
			int wFactoryID = StringUtils.parseInt(request.getParameter("FactoryID"));
			Integer wWorkShopID = StringUtils.parseInt(request.getParameter("WorkShopID"));
			Integer wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			Integer wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			Integer wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			String wProductNo = StringUtils.parseString(request.getParameter("ProductNo"));
			int wCustomID = StringUtils.parseInt(request.getParameter("CustomID"));

			Integer wNum = StringUtils.parseInt(request.getParameter("Num"));
			ServiceResult<List<IPTStandard>> wServiceResult = wIPTService.IPT_GetStandardListCurrent(wLoginUser,
					wLoginUser.CompanyID, IPTMode.getEnumType(wITPMode), wCustomID, wBusinessUnitID, wBaseID,
					wFactoryID, wWorkShopID, wLineID, wPartID, wProductID, wProductNo, wNum);
			List<IPTStandard> wServerRst = wServiceResult.Result;

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wServiceResult.FaultCode, wServerRst, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode, wServerRst, null);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/StandardAll")
	public Object StandardAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Integer wITPMode = StringUtils.parseInt(request.getParameter("IPTMode"));
			int wBusinessUnitID = StringUtils.parseInt(request.getParameter("BusinessUnitID"));
			int wBaseID = StringUtils.parseInt(request.getParameter("BaseID"));
			int wFactoryID = StringUtils.parseInt(request.getParameter("FactoryID"));
			Integer wWorkShopID = StringUtils.parseInt(request.getParameter("WorkShopID"));
			Integer wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			Integer wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			Integer wPartPointID = StringUtils.parseInt(request.getParameter("PartPointID"));
			Integer wStationID = StringUtils.parseInt(request.getParameter("StationID"));
			Integer wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			String wProductNo = StringUtils.parseString(request.getParameter("ProductNo"));

			Calendar wDTStart = StringUtils.parseCalendar(request.getParameter("DTStart"));
			Calendar wDTEnd = StringUtils.parseCalendar(request.getParameter("DTEnd"));

			int wCustomID = StringUtils.parseInt(request.getParameter("CustomID"));

			ServiceResult<List<IPTStandard>> wServiceResult = wIPTService.IPT_GetStandardListByTime(wLoginUser,
					wLoginUser.CompanyID, IPTMode.getEnumType(wITPMode), wCustomID, wBusinessUnitID, wBaseID,
					wFactoryID, wWorkShopID, wLineID, wPartID, wPartPointID, wStationID, wProductID, wProductNo,
					wDTStart, wDTEnd);
			List<IPTStandard> wServerRst = wServiceResult.Result;

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServerRst, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), wServerRst, null);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping("/StandardInfo")
	public Object StandardInfo(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}
			BMSEmployee wBMSEmployee = GetSession(request);

			long wID = StringUtils.parseLong(request.getParameter("ID"));

			ServiceResult<IPTStandard> wServiceResult = wIPTService.IPT_GetStandard(wBMSEmployee, wID);
			IPTStandard wServerRst = wServiceResult.Result;
			if (wServerRst.ItemList == null)
				wServerRst.ItemList = new ArrayList<IPTItem>();

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wServiceResult.FaultCode, null, wServerRst);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode, wServerRst, null);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@PostMapping("/SaveValue")
	public Object SaveValue(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("data")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			List<IPTValue> wIPTValueList = CloneTool.CloneArray(wParam.get("data"), IPTValue.class);

			int wTaskID = StringUtils.parseInt(wParam.containsKey("TaskID") ? wParam.get("TaskID") : 0);

			int wEventID = StringUtils.parseInt(wParam.containsKey("EventID") ? wParam.get("EventID") : 0);

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_SaveIPTValue(wLoginUser, wIPTValueList, wTaskID,
					wEventID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wServiceResult.FaultCode);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@PostMapping("/SaveConfig")
	public Object SaveConfig(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("data")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			IPTConfigs wIPTConfig = CloneTool.Clone(wParam.get("data"), IPTConfigs.class);
			ServiceResult<String> wServiceResult = wIPTService.IPT_SetIPTConfig(wLoginUser, wIPTConfig);
			String wServerRst = wServiceResult.Result;

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wServerRst);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServerRst);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@PostMapping("/SaveStandard")
	public Object SaveStandard(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("data")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			IPTStandard wIPTStandard = CloneTool.Clone(wParam.get("data"), IPTStandard.class);
			wIPTStandard.UserID = wLoginUser.ID;
			wIPTStandard.CompanyID = wLoginUser.CompanyID;
			String wServerRst = "";

			if (wIPTStandard.IPTMode == IPTMode.QTXJ.getValue()) {
				// 【质量标准设置】权限控制
				if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 600003, 0, 0)
						.Info(Boolean.class)) {
					return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
				}
			} else if (wIPTStandard.IPTMode == IPTMode.PreCheck.getValue()) {
				// 【预检标准设置】权限控制
				if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 600004, 0, 0)
						.Info(Boolean.class)) {
					return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
				}
			}

			if (wIPTStandard.ID <= 0) {
				ServiceResult<IPTStandard> wServiceResult = wIPTService.IPT_InsertStandard(wLoginUser, wIPTStandard);
				wIPTStandard = wServiceResult.Result;
				wServerRst = wServiceResult.FaultCode;
			} else {
				ServiceResult<String> wServiceResult = wIPTService.IPT_SaveStandard(wLoginUser, wIPTStandard);
				wServerRst = wServiceResult.Result;
			}

			if (StringUtils.isEmpty(wServerRst)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wServerRst, null, wIPTStandard);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServerRst, null, wIPTStandard);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@PostMapping("/DeleteStandard")
	public Object DeleteStandard(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("data")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			IPTStandard wIPTStandard = CloneTool.Clone(wParam.get("data"), IPTStandard.class);

			String wServerRst = "";

			if (wIPTStandard.ID >= 0) {
				ServiceResult<String> wServiceResult = wIPTService.IPT_DeleteStandard(wLoginUser, wIPTStandard.ID);
				wServerRst = wServiceResult.Result;
			}

			if (StringUtils.isEmpty(wServerRst)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wServerRst, null, wIPTStandard);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServerRst, null, wIPTStandard);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@PostMapping("/StandardStatus")
	public Object StandardStatus(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("data")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			IPTStandard wIPTStandard = CloneTool.Clone(wParam.get("data"), IPTStandard.class);
			int wEND = StringUtils.parseInt(wParam.containsKey("IsEnd") ? wParam.get("IsEnd") : -1);
			int wCurrent = StringUtils.parseInt(wParam.containsKey("IsCurrent") ? wParam.get("IsCurrent") : -1);

			String wServerRst = "";

			if (wEND >= 0) {
				ServiceResult<String> wServiceResult = wIPTService.IPT_SaveStandardEnd(wLoginUser, wIPTStandard.ID,
						wEND);
				wServerRst = wServiceResult.Result;
			}

			if (wCurrent >= 0) {
				ServiceResult<String> wServiceResult = wIPTService.IPT_SaveStandardState(wLoginUser, wIPTStandard.ID,
						wCurrent);
				wServerRst = wServiceResult.Result;
			}

			if (StringUtils.isEmpty(wServerRst)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wServerRst, null, wIPTStandard);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServerRst, null, wIPTStandard);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 导入标准项
	 * 
	 * @param request
	 * @param files   Excel文件
	 * @return
	 */
	@PostMapping("/Import")
	public Object Import(HttpServletRequest request, @RequestParam("file") MultipartFile[] files,
			@RequestParam("IPTMode") int wIPTMode, @RequestParam("ProductID") int wProductID,
			@RequestParam("LineID") int wLineID, @RequestParam("StationID") int wStationID,
			@RequestParam("CustomerID") int wCustomerID) {
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

				wServiceResult = wIPTService.IPT_ImportStandard(wLoginUser, wIPTMode, wExcelData.Result, wProductID,
						wLineID, wStationID, wCustomerID, wOriginalFileName);

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
	 * 导入工序清单
	 * 
	 * @param request
	 * @param files   Excel文件
	 * @return
	 */
	@PostMapping("/ImportPartPoint")
	public Object ImportPartPoint(HttpServletRequest request, @RequestParam("file") MultipartFile[] files) {
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

//				wExcelData = new ExcelData();
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

				wServiceResult = wIPTService.IPT_ImportPartPoint(wLoginUser, wExcelData.Result);

				if (!StringUtils.isEmpty(wServiceResult.FaultCode))
					break;
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
	 * 导入订单
	 */
	@PostMapping("/ImportOrder")
	public Object ImportOrder(HttpServletRequest request, @RequestParam("file") MultipartFile[] files) {
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

				wServiceResult = wIPTService.IPT_ImportOrder(wLoginUser, wExcelData.Result);

				if (!StringUtils.isEmpty(wServiceResult.FaultCode))
					break;
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
	 * 导入标准BOM文档
	 * 
	 * @param request
	 * @param files   Excel文件
	 * @return
	 */
	@PostMapping("/ImportBOM")
	public Object ImportBOM(HttpServletRequest request, @RequestParam("file") MultipartFile[] files) {
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

			ServiceResult<String> wServiceResult = new ServiceResult<String>();
			ServiceResult<ExcelData> wExcelData = null;
			String wOriginalFileName = null;
			for (MultipartFile wMultipartFile : files) {
				wOriginalFileName = wMultipartFile.getOriginalFilename();

				if (wOriginalFileName.contains("xlsx") || wOriginalFileName.contains("XLSX")) {
					wExcelData = ExcelReader.getInstance().readMultiSheetExcel(wMultipartFile.getInputStream(),
							wOriginalFileName, "xlsx", 1000000);
				} else if (wOriginalFileName.contains("xls") || wOriginalFileName.contains("XLS")) {
					wExcelData = ExcelReader.getInstance().readMultiSheetExcel(wMultipartFile.getInputStream(),
							wOriginalFileName, "xls", 1000000);
				}

				if (StringUtils.isNotEmpty(wExcelData.FaultCode)) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wExcelData.FaultCode);
					return wResult;
				}

				wServiceResult = wIPTService.IPT_ImportBOM(wLoginUser, wExcelData.Result, wOriginalFileName);

				if (!StringUtils.isEmpty(wServiceResult.FaultCode))
					break;
			}

			if (StringUtils.isEmpty(wServiceResult.Result)) {
				int wBOMID = 0;
				if (wServiceResult.CustomResult.containsKey("BOMID")) {
					wBOMID = (int) wServiceResult.CustomResult.get("BOMID");
				}
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "导入成功!", null, wBOMID);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.Result);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 导入物料
	 * 
	 * @param request
	 * @param files   Excel文件
	 * @return
	 */
	@PostMapping("/ImportMaterial")
	public Object ImportMaterial(HttpServletRequest request, @RequestParam("file") MultipartFile[] files) {
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

			ServiceResult<String> wServiceResult = new ServiceResult<String>();
			ServiceResult<ExcelData> wExcelData = null;
			String wOriginalFileName = null;
			for (MultipartFile wMultipartFile : files) {
				wOriginalFileName = wMultipartFile.getOriginalFilename();

				if (wOriginalFileName.contains("xlsx") || wOriginalFileName.contains("XLSX")) {
					wExcelData = ExcelReader.getInstance().readMultiSheetExcel(wMultipartFile.getInputStream(),
							wOriginalFileName, "xlsx", 1000000);
				} else if (wOriginalFileName.contains("xls") || wOriginalFileName.contains("XLS")) {
					wExcelData = ExcelReader.getInstance().readMultiSheetExcel(wMultipartFile.getInputStream(),
							wOriginalFileName, "xls", 1000000);
				}

				if (StringUtils.isNotEmpty(wExcelData.FaultCode)) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wExcelData.FaultCode);
					return wResult;
				}

				wServiceResult = wIPTService.IPT_ImportMaterial(wLoginUser, wExcelData.Result, wOriginalFileName);

				if (!StringUtils.isEmpty(wServiceResult.FaultCode))
					break;
			}

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				int wBOMID = 0;
				if (wServiceResult.CustomResult.containsKey("BOMID")) {
					wBOMID = (int) wServiceResult.CustomResult.get("BOMID");
				}
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "导入成功!", null, wBOMID);
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
	 * 获取重复物料的SQL
	 */
	@PostMapping("/GetMaterilaSQL")
	public Object GetMaterilaSQL(HttpServletRequest request, @RequestParam("file") MultipartFile[] files) {
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

			ServiceResult<String> wServiceResult = new ServiceResult<String>();
			ServiceResult<ExcelData> wExcelData = null;
			String wOriginalFileName = null;
			for (MultipartFile wMultipartFile : files) {
				wOriginalFileName = wMultipartFile.getOriginalFilename();

				if (wOriginalFileName.contains("xlsx") || wOriginalFileName.contains("XLSX")) {
					wExcelData = ExcelReader.getInstance().readMultiSheetExcel(wMultipartFile.getInputStream(),
							wOriginalFileName, "xlsx", 1000000);
				} else if (wOriginalFileName.contains("xls") || wOriginalFileName.contains("XLS")) {
					wExcelData = ExcelReader.getInstance().readMultiSheetExcel(wMultipartFile.getInputStream(),
							wOriginalFileName, "xls", 1000000);
				}

				if (StringUtils.isNotEmpty(wExcelData.FaultCode)) {
					wResult = GetResult(RetCode.SERVER_CODE_ERR, wExcelData.FaultCode);
					return wResult;
				}

				wServiceResult = wIPTService.IPT_GetMaterialSQL(wLoginUser, wExcelData.Result);

				if (!StringUtils.isEmpty(wServiceResult.FaultCode))
					break;
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
	 * 获取导入进度
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("Progress")
	public Object Progress(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wImportType = StringUtils.parseInt(request.getParameter("ImportType"));
			Calendar wTime = StringUtils.parseCalendar(request.getParameter("ImportTime"));

			ServiceResult<IMPResultRecord> wServiceResult = wIPTService.IPT_QueryMaterialProgress(wLoginUser,
					wImportType, wTime);

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
	 * 导出BOM文档
	 * 
	 * @param request
	 * @param response
	 */
	@PostMapping("/ExportBOM")
	public Object ExportBOM(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			List<MSSBOMItem> wItemList = CloneTool.CloneArray(wParam.get("data"), MSSBOMItem.class);

			if (wItemList == null || wItemList.size() <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：" + RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_ExportBOM(wLoginUser, wItemList, response);

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
	 * 保存预检项的解决方案
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/SaveSOPList")
	public Object SaveSOPList(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("data")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}
			List<IPTItem> wIPTItemList = CloneTool.CloneArray(wParam.get("data"), IPTItem.class);

			ServiceResult<Integer> wServiceResult = new ServiceResult<Integer>();
			for (IPTItem wIPTItem : wIPTItemList) {
				wServiceResult = wIPTService.IPT_SaveSOPList(wLoginUser, wIPTItem);
			}

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, null);
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
	 * 根据车型、修程、工位获取工序颜色映射
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/PointTree")
	public Object PointTree(HttpServletRequest request) {
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

			ServiceResult<Map<Integer, Integer>> wServiceResult = wIPTService.IPT_QueryPointTree(wLoginUser, wProductID,
					wLineID, wPartID);

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
	 * 根据修程、工位获取车型颜色映射
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ProductTree")
	public Object ProductTree(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			ServiceResult<Map<Integer, Integer>> wServiceResult = wIPTService.IPT_QueryProductTree(wLoginUser, wLineID,
					wPartID);

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
	 * 根据车型、修程、工位、工序获取所有标准(包含子项)
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/StandardList")
	public Object StandardList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Integer wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			Integer wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			Integer wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			Integer wPartPointID = StringUtils.parseInt(request.getParameter("PartPointID"));
			Integer wCustomID = StringUtils.parseInt(request.getParameter("CustomID"));

			ServiceResult<List<IPTStandard>> wServiceResult = wIPTService.IPT_GetStandardListByTime(wLoginUser,
					wLoginUser.CompanyID, IPTMode.Default, wCustomID, -1, -1, -1, -1, wLineID, wPartID, wPartPointID,
					-1, wProductID, "", null, null);
			List<IPTStandard> wServerRst = wServiceResult.Result;

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServerRst, null);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode(), wServerRst, null);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 将扁平化项转化为树形项
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/IPTItemTree")
	public Object IPTItemTree(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			List<IPTItem> wList = CloneTool.CloneArray(wParam.get("data"), IPTItem.class);

			if (wList == null || wList.size() <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<List<IPTItem>> wServiceResult = wIPTService.IPT_QueryIPTItemTree(wLoginUser, wList);

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
	 * 生成台车BOM
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/CreateAPSBOM")
	public Object CreateAPSBOM(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(wParam.get("OrderID"));

			ServiceResult<List<APSBOMItem>> wServiceResult = wIPTService.IPT_CreateAPSBOMItemList(wLoginUser, wOrderID);

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
	 * 导出台车BOM
	 * 
	 * @param request
	 * @param response
	 */
	@PostMapping("/ExportAPSBOM")
	public Object ExportAPSBOM(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			List<APSBOMItem> wItemList = CloneTool.CloneArray(wParam.get("data"), APSBOMItem.class);

			if (wItemList == null || wItemList.size() <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "提示：" + RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<String> wServiceResult = wIPTService.IPT_ExportAPSBOM(wLoginUser, wItemList);

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
	 * 查询工序版本
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/StepVersionAll")
	public Object StepVersionAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wCustomID = StringUtils.parseInt(request.getParameter("CustomID"));

			ServiceResult<List<IPTStandard>> wServiceResult = wIPTService.IPT_QueryStepVersionAll(wLoginUser, wLineID,
					wProductID, wPartID, wCustomID);

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
	 * 查询工序版本(支持模糊搜索工序)、车型修程、工序描述必填。
	 */
	@GetMapping("/StepVersionAllPro")
	public Object StepVersionAllPro(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			String wStepName = StringUtils.parseString(request.getParameter("StepName"));

			if (StringUtils.isEmpty(wStepName)) {
				return GetResult(RetCode.SERVER_CODE_ERR, "工序名称必填!");
			}

			ServiceResult<List<IPTStandard>> wServiceResult = wIPTService.IPT_QueryStepVersionAllPro(wLoginUser,
					wLineID, wProductID, wPartID, wStepName);

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
	 * 设置质量项点
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/IsQuality")
	public Object IsQuality(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 【设置质量项点】权限控制
			if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 600005, 0, 0)
					.Info(Boolean.class)) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
			}

			if (!wParam.containsKey("data") || !wParam.containsKey("IsQuality")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			// 获取参数
			List<IPTItem> wIPTItemList = CloneTool.CloneArray(wParam.get("data"), IPTItem.class);
			int wIsQuality = StringUtils.parseInt(wParam.get("IsQuality"));

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_SetIsQuality(wLoginUser, wIPTItemList, wIsQuality);

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
	 * 设置项点部件编码
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/SetPartCoding")
	public Object SetPartCoding(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 【设置项点部件编码】权限控制
			if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 600006, 0, 0)
					.Info(Boolean.class)) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
			}

			if (!wParam.containsKey("data") || !wParam.containsKey("PartsCoding")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			// 获取参数
			IPTItem wIPTItem = CloneTool.Clone(wParam.get("data"), IPTItem.class);
			String wPartCoding = StringUtils.parseString(wParam.get("PartsCoding"));
			int wConfigID = StringUtils.parseInt(wParam.get("ConfigID"));

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_SetPartCoding(wLoginUser, wIPTItem, wPartCoding,
					wConfigID);

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
	 * 设置项点检验顺序
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/SetItemOrder")
	public Object SetItemOrder(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 【设置项点检验顺序】权限控制
			if (!wCoreService.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 600007, 0, 0)
					.Info(Boolean.class)) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_CODE_UNROLE);
			}

			if (!wParam.containsKey("data") || !wParam.containsKey("OrderID")) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			// 获取参数
			IPTItem wIPTItem = CloneTool.Clone(wParam.get("data"), IPTItem.class);
			int wOrderID = StringUtils.parseInt(wParam.get("OrderID"));

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_SetItemOrder(wLoginUser, wIPTItem, wOrderID);

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
	 * 通过标准和局段获取局段项列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ItemList")
	public Object ItemList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wStandardID = StringUtils.parseInt(request.getParameter("StandardID"));
			int wCustomerID = StringUtils.parseInt(request.getParameter("CustomerID"));

			ServiceResult<List<IPTItem>> wServiceResult = wIPTService.QueryItemListByCustomer(wLoginUser, wStandardID,
					wCustomerID);

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
	 * 通过标准获取项列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ItemListByStandard")
	public Object ItemListByStandard(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wStandardID = StringUtils.parseInt(request.getParameter("StandardID"));

			ServiceResult<List<IPTItem>> wServiceResult = wIPTService.QueryItemListByStandard(wLoginUser, wStandardID);

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
	 * 对比工位下的所有标准和标准项
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/Compare")
	public Object Compare(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// A导入记录ID
			int wARecordID = StringUtils.parseInt(request.getParameter("ARecordID"));
			// B导入记录ID
			int wBRecordID = StringUtils.parseInt(request.getParameter("BRecordID"));

			ServiceResult<List<IPTStandardC>> wServiceResult = wIPTService.IPT_Compare(wLoginUser, wARecordID,
					wBRecordID);

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
	 * 添加备注
	 */
	@PostMapping("/AddRemark")
	public Object AddRemark(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			IPTValue wData = CloneTool.Clone(wParam.get("data"), IPTValue.class);

			ServiceResult<Integer> wServiceResult = wIPTService.AddRemark(wLoginUser, wData);

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
	 * 批量删除项点
	 */
	@PostMapping("/DeletItems")
	public Object DeletItems(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			List<IPTItem> wItemList = CloneTool.CloneArray(wParam.get("data"), IPTItem.class);

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_DeletItems(wLoginUser, wItemList);

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
	 * 复制标准
	 */
	@PostMapping("/Copy")
	public Object Copy(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wStandardID = StringUtils.parseInt(wParam.get("StandardID"));
			OMSOrder wOMSOrder = CloneTool.Clone(wParam.get("data"), OMSOrder.class);

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_CopyStandard(wLoginUser, wStandardID, wOMSOrder);

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
	 * 新版 导入标准项
	 */
	@PostMapping("/ImportNew")
	public Object ImportNew(HttpServletRequest request, @RequestParam("file") MultipartFile[] files,
			@RequestParam("IPTMode") int wIPTMode, @RequestParam("ProductID") int wProductID,
			@RequestParam("LineID") int wLineID, @RequestParam("StationID") int wStationID,
			@RequestParam("CustomerID") int wCustomerID) {
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

				wServiceResult = wIPTService.IPT_ImportStandardNew(wLoginUser, wIPTMode, wProductID, wLineID,
						wCustomerID, wStationID, wExcelData.Result, wOriginalFileName);

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
	 * 编辑标准
	 */
	@PostMapping("/EditStandard")
	public Object SaveTaskItemList(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			IPTStandard wData = CloneTool.Clone(wParam.get("data"), IPTStandard.class);

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_EditStandard(wLoginUser, wData);

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
	 * 批量复制标准
	 */
	@GetMapping("/BatchCopy")
	public Object BatchCopy(HttpServletRequest request) {
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
			int wPartID1 = StringUtils.parseInt(request.getParameter("PartID1"));
			int wPartPoint1 = StringUtils.parseInt(request.getParameter("PartPoint1"));

			int wPartID2 = StringUtils.parseInt(request.getParameter("PartID2"));
			int wPartPoint2 = StringUtils.parseInt(request.getParameter("PartPoint2"));

			ServiceResult<Integer> wServiceResult = wIPTService.IPT_BatchCopyStandard(wLoginUser, wProductID, wLineID,
					wPartID1, wPartPoint1, wPartID2, wPartPoint2, false);

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
