package com.mes.qms.server.controller.pic;

import java.util.ArrayList;
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
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.pic.SFCCarInfo;
import com.mes.qms.server.service.po.pic.SFCPartInfo;
import com.mes.qms.server.service.po.pic.SFCProgress;
import com.mes.qms.server.service.po.pic.SFCRankInfo;
import com.mes.qms.server.service.po.pic.SFCUploadPic;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-10-28 15:19:47
 * @LastEditTime 2020-10-28 15:19:51
 */
@RestController
@RequestMapping("/api/SFCCarInfo")
public class SFCCarInfoController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(SFCCarInfoController.class);

	@Autowired
	SFCService wSFCService;

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

			ServiceResult<SFCCarInfo> wServiceResult = wSFCService.SFC_QueryCarInfo(wLoginUser, wID);

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
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<SFCCarInfo>> wServiceResult = wSFCService.SFC_QueryCarInfoList(wLoginUser, wID,
					wOrderID);

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

			SFCCarInfo wSFCCarInfo = this.GetSFCCarInfo(wParam.get("data"));

			if (wSFCCarInfo == null) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, "参数错误!");
				return wResult;
			}

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_UpdateCarInfo(wLoginUser, wSFCCarInfo);

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
	 * 解析数据
	 */
	@SuppressWarnings("unchecked")
	private SFCCarInfo GetSFCCarInfo(Object wObject) {
		SFCCarInfo wResult = new SFCCarInfo();
		try {
			Map<String, Object> wMap = (Map<String, Object>) wObject;
			wResult.ID = (int) wMap.get("ID");
			wResult.OrderID = (int) wMap.get("OrderID");
			wResult.ProductNo = (String) wMap.get("ProductNo");
			wResult.CarNo = (String) wMap.get("CarNo");
			wResult.CreaterID = (int) wMap.get("CreaterID");
			wResult.CreateTime = StringUtils.parseCalendar(wMap.get("CreateTime"));
			wResult.SFCRankInfoList = new ArrayList<SFCRankInfo>();
			if (wMap.containsKey("SFCRankInfoList")) {
				List<Map<String, Object>> wRankList = (List<Map<String, Object>>) wMap.get("SFCRankInfoList");
				if (wRankList != null && wRankList.size() > 0) {
					for (Map<String, Object> wRankMap : wRankList) {
						SFCRankInfo wSFCRankInfo = new SFCRankInfo();
						wSFCRankInfo.ID = (int) wRankMap.get("ID");
						wSFCRankInfo.CarID = (int) wRankMap.get("CarID");
						wSFCRankInfo.No = (int) wRankMap.get("No");
						wSFCRankInfo.Remark = (String) wRankMap.get("Remark");
						wSFCRankInfo.SFCPartInfoList = new ArrayList<SFCPartInfo>();
						wResult.SFCRankInfoList.add(wSFCRankInfo);
						if (wRankMap.containsKey("SFCPartInfoList")) {
							List<Map<String, Object>> wPartList = (List<Map<String, Object>>) wRankMap
									.get("SFCPartInfoList");
							if (wPartList != null && wPartList.size() > 0) {
								for (Map<String, Object> wPartMap : wPartList) {
									SFCPartInfo wSFCPartInfo = new SFCPartInfo();
									wSFCPartInfo.ID = (int) wPartMap.get("ID");
									wSFCPartInfo.RankID = (int) wPartMap.get("RankID");
									wSFCPartInfo.Remark = (String) wPartMap.get("Remark");
									wSFCPartInfo.No = (int) wPartMap.get("No");
									wSFCPartInfo.Status = (int) wPartMap.get("Status");
									wSFCPartInfo.SFCUploadPicList = new ArrayList<SFCUploadPic>();
									wSFCRankInfo.SFCPartInfoList.add(wSFCPartInfo);
									if (wPartMap.containsKey("SFCUploadPicList")) {
										List<Map<String, Object>> wUploadList = (List<Map<String, Object>>) wPartMap
												.get("SFCUploadPicList");
										if (wUploadList != null && wUploadList.size() > 0) {
											for (Map<String, Object> wUploadMap : wUploadList) {
												SFCUploadPic wSFCUploadPic = new SFCUploadPic();
												wSFCUploadPic.ID = (int) wUploadMap.get("ID");
												wSFCUploadPic.PartID = (int) wUploadMap.get("PartID");
												wSFCUploadPic.PicUrl = (String) wUploadMap.get("PicUrl");
												wSFCUploadPic.No = (int) wUploadMap.get("No");
												wSFCUploadPic.Remark = (String) wUploadMap.get("Remark");
												wSFCUploadPic.Describe = (String) wUploadMap.get("Describe");
												wSFCPartInfo.SFCUploadPicList.add(wSFCUploadPic);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 导出整车图片Zip
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ExportZip")
	public Object ExportZip(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wID = StringUtils.parseInt(request.getParameter("ID"));

			ServiceResult<String> wServiceResult = wSFCService.SFC_ExportPicZip(wLoginUser, wID);

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
	 * 根据筛选条件导出机车图片
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/ExportByCondition")
	public Object ExportByCondition(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			List<Integer> wIDList = CloneTool.CloneArray(wParam.get("IDList"), Integer.class);
			List<Integer> wPartIDList = CloneTool.CloneArray(wParam.get("PartIDList"), Integer.class);

			ServiceResult<String> wServiceResult = wSFCService.SFC_ExportPartInfoByCondition(wLoginUser, wIDList,
					wPartIDList);

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
	 * 导出整车图片Zip加进度条
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ExportZipProgress")
	public Object ExportZipProgress(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wID = StringUtils.parseInt(request.getParameter("ID"));
			String wUUID = StringUtils.parseString(request.getParameter("UUID"));

			ServiceResult<String> wServiceResult = wSFCService.SFC_ExportPicZip(wLoginUser, wID, wUUID);

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
	 * 删除整车图片(服务器上传的)
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/DeleteAll")
	public Object DeleteAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wID = StringUtils.parseInt(request.getParameter("ID"));

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_DeleteAllPic(wLoginUser, wID);

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
	 * 删除单个图片
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/Delete")
	public Object Delete(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			String wUri = StringUtils.parseString(request.getParameter("Uri"));

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_DeleteSinglePic(wLoginUser, wUri);

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
	 * 获取预检任务集合
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/Progress")
	public Object Progress(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			String wUUID = StringUtils.parseString(request.getParameter("UUID"));

			ServiceResult<SFCProgress> wServiceResult = wSFCService.SFC_QueryProgress(wLoginUser, wUUID);

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
	 * 获取位次列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/RankInfoList")
	public Object RankInfoList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wCarID = StringUtils.parseInt(request.getParameter("CarID"));
			
			ServiceResult<List<SFCRankInfo>> wServiceResult=wSFCService.SFC_QueryRankInfoList(wLoginUser,wCarID);

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
