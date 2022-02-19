package com.mes.qms.server.controller.test;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mes.qms.server.controller.BaseController;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMActivitiTask;
import com.mes.qms.server.service.po.tcm.TCMRework;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.CoreServiceImpl;
import com.mes.qms.server.serviceimpl.FPCRouteImportServiceImpl;
import com.mes.qms.server.utils.RetCode;

/**
 * 测试控制器
 * 
 * @author PengYouWang
 * @CreateTime 2020-4-2 16:57:38
 * @LastEditTime 2020-4-2 16:57:41
 */
@RestController
@RequestMapping("/api/Test")
public class TestController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(TestController.class);

	/**
	 * 接口测试
	 */
	@GetMapping("/Test")
	public Object Test(HttpServletRequest request, HttpServletResponse response) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			String wMsg = Test(wLoginUser, response);

			ServiceResult<Integer> wServiceResult = new ServiceResult<Integer>();
			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, wMsg, null, wServiceResult.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	public String Test(BMSEmployee wLoginUser, HttpServletResponse response) {
		String wMsg = "";
		try {
//			createRework(wLoginUser);

//			RSMServiceImpl.getInstance().HandleTechCahngeTest(wLoginUser, 1,
//					new ArrayList<Integer>(Arrays.asList(168)));

//			ServiceResult<List<APSMaterialReturn>> wList = APSServiceImpl.getInstance()
//					.APS_QueryMaterialReturnList(wLoginUser, "", -1, -1);
//
//			ServiceResult<String> wRst = APSServiceImpl.getInstance().ExportReturnMaterialList(wLoginUser,
//					wList.Result);
//
//			System.out.println(wRst.Result);

//			ServiceResult<String> wSrs = TCMServiceImpl.getInstance().TCM_fileinfo("", QMSUtils.appId, "1095");
//			System.out.println(wSrs);

//			ServiceResult<String> wRst = SFCServiceImpl.getInstance().SFC_ExportNewStandard_V2(wLoginUser, 97, 2, 1593,
//					3);
//			System.out.println(wRst.Result);

//			ServiceResult<Integer> wRs = FPCRouteImportServiceImpl.getInstance().FPC_SynchronizeLineUnit(wLoginUser,
//					238);
//			System.out.println(wRs.Result);

//			ServiceResult<Integer> wRst = TCMServiceImpl.getInstance().TCM_UpdateOldMaterial(wLoginUser);
//			System.out.println(wRst);

//			String strUrl = "C:\\Users\\Shris\\Desktop\\MES常规新件.xlsx";
//			File file = new File(strUrl);
//			InputStream inputStream = new FileInputStream(file);
//			MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
//
//			HashMap<String, Object> wMap = new HashMap<String, Object>();
//
//			wMap.put("enablePrint", "true");
//
//			JSONObject wObject = MESFileUtils.sendPostWithFile("http://10.200.1.29:8261/api/v1/xview/file/upload",
//					multipartFile, wMap);
//			TCMPreviewFile wPreviewFile = CloneTool.Clone(wObject, TCMPreviewFile.class);
//
//			System.out.println(wPreviewFile.url);

//			ServiceResult<String> wRst = SFCServiceImpl.getInstance().SFC_ExportNewStandard_V2(wLoginUser, 1656, 111,
//					1793, 3);
//			System.out.println(wRst.Result);

			ServiceResult<String> wRst = FPCRouteImportServiceImpl.getInstance().FPC_ExportBOP(wLoginUser, 598);
			System.out.println(wRst.Result);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wMsg;
	}

	@SuppressWarnings("unused")
	private void createRework(BMSEmployee wLoginUser) {
		try {
			APIResult wAPiResult = CoreServiceImpl.getInstance().QMS_StartInstance(wLoginUser, "8209");
			List<BPMActivitiTask> wBPMActivitiTask = wAPiResult.List(BPMActivitiTask.class);
			TCMRework wData = wAPiResult.Custom("data", TCMRework.class);

			wData.Content = "测试工艺变更返工内容";
			wData.LineID = 1;
			wData.LineName = "C5";
			wData.ProductID = 17;
			wData.ProductNo = "HXD3C";
			wData.PartNo = "HXD3C#0088";
			wData.CustomerID = 11;
			wData.Customer = "南局向段";
			wData.PartID = 26;
			wData.PartName = "机车预检工位";
			wData.StepID = 1123;
			wData.StepName = "转向架预检";
			wData.Status = 1;
			wData.MonitorList = "11139,11184";

			// ①辅助属性赋值
			wData.Content_txt_ = wData.Content;
			wData.LineName_txt_ = wData.LineName;
			wData.ProductNo_txt_ = wData.ProductNo;
			wData.PartNo_txt_ = wData.PartNo;
			wData.Customer_txt_ = wData.Customer;
			wData.PartName_txt_ = wData.PartName;
			wData.StepName_txt_ = wData.StepName;

			CoreServiceImpl.getInstance().QMS_CompleteInstance(wLoginUser, wData, wBPMActivitiTask.get(0).ID);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}
