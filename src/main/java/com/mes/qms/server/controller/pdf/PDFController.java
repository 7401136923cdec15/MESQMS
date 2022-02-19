package com.mes.qms.server.controller.pdf;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.LOCOAPSService;
import com.mes.qms.server.service.PDFService;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 预检导出PDF控制器
 * 
 * @author ShrisJava
 *
 */
@RestController
@RequestMapping("/api/PDF")
public class PDFController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(PDFController.class);

	@Autowired
	PDFService wPDFService;

	@Autowired
	LOCOAPSService wLOCOAPSService;

	/**
	 * 导出PDF文档
	 */
	@GetMapping("/OutputPDF")
	public Object OutputPDF(HttpServletRequest request, HttpServletResponse response) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			OMSOrder wOrder = wLOCOAPSService.OMS_QueryOrderByID(wLoginUser, wOrderID).Info(OMSOrder.class);

			if (wOrder == null || wOrder.ID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			// ①清空下载文件的空白行（空白行是因为有的前端代码编译后产生的）
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			response.reset();
			String wFileName = wOrder.PartNo + "预检报告_" + wCurTime + ".pdf";
			wFileName = URLEncoder.encode(wFileName, "UTF-8");
			// ①设置响应头，把文件名字设置好
			response.setHeader("Content-Disposition", "attachment; filename=" + wFileName);
			// ①解决编码问题
			response.setContentType("application/octet-stream; charset=utf-8");
			// ①开始输出
			wPDFService.IPT_OutputPDFStream(wLoginUser, wOrder.PartNo, response.getOutputStream());

			wResult = GetResult(RetCode.SERVER_CODE_SUC, "导出成功!", null, null);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}
}
