package com.mes.qms.server.controller.tcm;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.TCMService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 文件预览
 * 
 * @author PengYouWang
 * @CreateTime 2021-7-16 09:18:25
 */
@RestController
@RequestMapping("/api/FilePreview")
public class FilePreviewController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(FilePreviewController.class);

	@Autowired
	TCMService wTCMService;

	/**
	 * 获取签名
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/getSignature")
	public Object getSignature(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			String wSha1 = StringUtils.parseString(request.getParameter("sha1"));

			ServiceResult<String> wServiceResult = wTCMService.TCM_getSignature(wLoginUser, wSha1);

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
	 * 获取文件信息
	 */
	@GetMapping("/fileinfo")
	public Object fileinfo(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			// 获取参数
			String wSignature = StringUtils.parseString(request.getParameter("signature"));
			String wAppid = StringUtils.parseString(request.getParameter("appid"));
			String wSha1 = StringUtils.parseString(request.getParameter("sha1"));

			ServiceResult<String> wServiceResult = wTCMService.TCM_fileinfo(wSignature, wAppid, wSha1);

			wResult = wServiceResult.Result;
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 文件预览
	 */
	@GetMapping("/FilePreview")
	public Object FilePreview(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			String wFTPPath = StringUtils.parseString(request.getParameter("FTPPath"));

			if (StringUtils.isEmpty(wFTPPath)) {
				return GetResult(RetCode.SERVER_CODE_ERR, "提示：参数错误，文件地址不能为空!");
			}

			ServiceResult<String> wServiceResult = wTCMService.TCM_FilePreview(wLoginUser, wFTPPath);

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
	 * 文件预览本地文件
	 */
	@GetMapping("/LocalFile")
	public Object LocalFile(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			String wPathUrl = StringUtils.parseString(request.getParameter("PathUrl"));

			ServiceResult<String> wServiceResult = wTCMService.TCM_FilePreviewLocalFile(wLoginUser, wPathUrl);

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