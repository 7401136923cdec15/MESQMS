package com.mes.qms.server.controller.pdf;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.mes.qms.server.service.PDFService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTPDFConfig;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * PDF配置控制器
 * 
 * @author PengYouWang
 * @CreateTime 2020-3-10 12:42:54
 * @LastEditTime 2020-3-10 12:42:59
 */
@RestController
@RequestMapping("/api/IPTPDFConfig")
public class IPTPDFConfigController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(IPTPDFConfigController.class);

	@Autowired
	PDFService wPDFService;

	/**
	 * 查单条
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

			ServiceResult<IPTPDFConfig> wServiceResult = wPDFService.IPT_QueryPDFConfig(wLoginUser, wID);

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
	 * 条件查询
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

			int wID = StringUtils.parseInt(request.getParameter("ID"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			int wCustomID = StringUtils.parseInt(request.getParameter("CustomID"));
			int wActive = StringUtils.parseInt(request.getParameter("Active"));

			ServiceResult<List<IPTPDFConfig>> wServiceResult = wPDFService.IPT_QueryPDFConfigList(wLoginUser, wID,
					wLineID, wProductID, wCustomID, wActive);

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
	 * 新增或更新
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/Update")
	public Object Update(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("data"))
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "参数错误!");

			IPTPDFConfig wIPTPDFConfig = CloneTool.Clone(wParam.get("data"), IPTPDFConfig.class);
			if (wIPTPDFConfig == null)
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "参数错误!");

			ServiceResult<Integer> wServiceResult = wPDFService.IPT_UpdatePDFConfig(wLoginUser, wIPTPDFConfig);

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
	 * 批量激活或禁用
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/Active")
	public Object Active(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("Active"))
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "参数错误!");
			int wActive = StringUtils.parseInt(wParam.get("Active"));

			if (!wParam.containsKey("data"))
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "参数错误!");

			List<IPTPDFConfig> wList = CloneTool.CloneArray(wParam.get("data"), IPTPDFConfig.class);
			if (wList == null || wList.size() <= 0)
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "参数错误!");

			List<Integer> wIDList = wList.stream().map(p -> p.getID()).collect(Collectors.toList());

			wPDFService.IPT_ActivePDFConfigList(wLoginUser, wIDList, wActive);

			wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, null);
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}
}
