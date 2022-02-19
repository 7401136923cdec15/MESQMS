package com.mes.qms.server.controller.mss;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.FPCRouteImportService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.mss.MSSMaterial;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;
import com.mes.qms.server.utils.qms.ExcelReader;

@RestController
@RequestMapping("/api/MSSMaterial")
public class MSSMaterialController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MSSMaterialController.class);

	@Autowired
	FPCRouteImportService wFPCRouteImportService;

	/**
	 * 获取分页的物料数据
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
			String wMaterialNo = StringUtils.parseString(request.getParameter("MaterialNo"));
			String wMaterialName = StringUtils.parseString(request.getParameter("MaterialName"));

			ServiceResult<List<MSSMaterial>> wServiceResult = wFPCRouteImportService.MSS_QueryPageAll(wLoginUser,
					wPageSize, wCurPage, wMaterialNo, wMaterialName);

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
	 * 获取物料总记录数
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
			String wMaterialNo = StringUtils.parseString(request.getParameter("MaterialNo"));
			String wMaterialName = StringUtils.parseString(request.getParameter("MaterialName"));

			ServiceResult<Integer> wServiceResult = wFPCRouteImportService.MSS_QueryRecordSize(wLoginUser, wMaterialNo,
					wMaterialName);

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
	 * 同步物料属性
	 */
	@PostMapping("/Synchronized")
	public Object Synchronized(HttpServletRequest request, @RequestParam("file") MultipartFile[] files) {
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

				wServiceResult = wFPCRouteImportService.MSS_SynchronizedMaterial(wLoginUser, wExcelData.Result,
						wOriginalFileName);

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

	        wServiceResult = wFPCRouteImportService.MSS_Import(wLoginUser, wExcelData);

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
}
