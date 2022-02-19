package com.mes.qms.server.controller.sfc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import com.mes.qms.server.service.QMSService;
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.mesenum.SFCLoginType;
import com.mes.qms.server.service.mesenum.SFCTaskStepType;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSDepartment;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.sfc.SFCLoginEvent;
import com.mes.qms.server.service.po.sfc.SFCLoginEventPart;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 打卡控制器
 * 
 * @author PengYouWang
 * @CreateTime 2020-3-31 19:49:38
 * @LastEditTime 2020-3-31 19:49:41
 */
@RestController
@RequestMapping("/api/SFCLoginEvent")
public class SFCLoginEventController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(SFCLoginEventController.class);

	@Autowired
	SFCService wSFCService;

	@Autowired
	QMSService wQMSService;

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

			ServiceResult<SFCLoginEvent> wServiceResult = wSFCService.SFC_QueryLoginEvent(wLoginUser, wID);

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

			int wWorkShopID = StringUtils.parseInt(request.getParameter("WorkShopID"));
			int wStationID = StringUtils.parseInt(request.getParameter("StationID"));
			int wModuleID = StringUtils.parseInt(request.getParameter("ModuleID"));
			Calendar wTime = StringUtils.parseCalendar(request.getParameter("Time"));
			int wActive = StringUtils.parseInt(request.getParameter("Active"));
			int wType = StringUtils.parseInt(request.getParameter("Type"));

			ServiceResult<List<SFCLoginEvent>> wServiceResult = wSFCService.SFC_QueryLoginEventList(wLoginUser,
					wWorkShopID, wStationID, wModuleID, wTime, wActive, wType);

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
	 * 获取工位分组的打卡任务
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAllPart")
	public Object EmployeeAllPart(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

//			ServiceResult<List<SFCLoginEventPart>> wServiceResult = wSFCService.SFC_QueryEmployeeAllPart(wLoginUser);
			ServiceResult<List<SFCLoginEventPart>> wServiceResult = wSFCService.SFC_QueryEmployeeAllPart_V2(wLoginUser);

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
	 * 获取打卡任务集合
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAllNew")
	public Object EmployeeAllNew(HttpServletRequest request) {
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
			int wTagTypes = StringUtils.parseInt(request.getParameter("TagTypes"));

//			ServiceResult<List<SFCTaskStep>> wServiceResult = wSFCService.SFC_QueryClockEmployeeAllNew(wLoginUser,
//					wOrderID, wPartID, null, null, wTagTypes);
			ServiceResult<List<SFCTaskStep>> wServiceResult = wSFCService.SFC_QueryClockEmployeeAllNew_V2(wLoginUser,
					wOrderID, wPartID, wTagTypes);

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
	 * 查询某人某天的打卡记录
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/EmployeeAll")
	public Object EmployeeAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			Calendar wDate = StringUtils.parseCalendar(request.getParameter("Date"));

			ServiceResult<List<SFCLoginEvent>> wServiceResult = wSFCService
					.SFC_QueryLoginEventListByEmployee(wLoginUser, wDate);

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
	 * 打卡
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/Clock")
	public Object Clock(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			if (!wParam.containsKey("Type") || !wParam.containsKey("DataList"))
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);

			int wType = StringUtils.parseInt(wParam.get("Type"));
			int wEventID = StringUtils.parseInt(wParam.get("EventID"));
			List<SFCTaskStep> wDataList = CloneTool.CloneArray(wParam.get("DataList"), SFCTaskStep.class);

			if (wDataList == null || wDataList.size() <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
			}

			// 开工限制
			if (wType == SFCLoginType.StartWork.getValue()) {
				List<SFCTaskStep> wSFCTaskStepList = new ArrayList<SFCTaskStep>();
				for (SFCTaskStep wSFCTaskStep : wDataList) {
					if (wSFCTaskStep.Type == SFCTaskStepType.Step.getValue()) {
						wSFCTaskStepList.add(wSFCTaskStep);
					}
				}
				ServiceResult<String> wMsgSR = wSFCService.SFC_CheckPGPowerNew(wLoginUser, wSFCTaskStepList);
				if (StringUtils.isNotEmpty(wMsgSR.Result)) {
					return GetResult(RetCode.SERVER_CODE_ERR, wMsgSR.Result);
				}
			}

//			ServiceResult<Integer> wServiceResult = wSFCService.SFC_Clock(wLoginUser, wType, wDataList, wEventID);

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_Clock_V2(wLoginUser, wType, wDataList, wEventID);

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
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);

			SFCLoginEvent wSFCLoginEvent = CloneTool.Clone(wParam.get("data"), SFCLoginEvent.class);
			if (wSFCLoginEvent == null)
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);

			ServiceResult<Long> wServiceResult = wSFCService.SFC_UpdateLoginEvent(wLoginUser, wSFCLoginEvent);

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
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
			int wActive = StringUtils.parseInt(wParam.get("Active"));

			if (!wParam.containsKey("data"))
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);

			List<SFCLoginEvent> wList = CloneTool.CloneArray(wParam.get("data"), SFCLoginEvent.class);
			if (wList == null || wList.size() <= 0)
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);

			List<Integer> wIDList = wList.stream().map(p -> p.getID()).collect(Collectors.toList());

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_ActiveLoginEventList(wLoginUser, wIDList, wActive);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "操作成功", null, null);
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
	 * 通过工区ID查询所有班组列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ClassList")
	public Object ClassList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wAreaID = StringUtils.parseInt(request.getParameter("AreaID"));

			ServiceResult<List<BMSDepartment>> wServiceResult = wSFCService.SFC_QueryClassListByAreaID(wLoginUser,
					wAreaID);

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
	 * 通过班组ID查询所有班组成员和班组长
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ClassMemberList")
	public Object ClassMemberList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wClassID = StringUtils.parseInt(request.getParameter("ClassID"));

			ServiceResult<List<BMSEmployee>> wServiceResult = wSFCService.SFC_QueryClassMemberListByClassID(wLoginUser,
					wClassID);

			if (StringUtils.isEmpty(wServiceResult.getFaultCode())) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
				if (wServiceResult.CustomResult.containsKey("Monitor")) {
					this.SetResult(wResult, "Monitor", wServiceResult.CustomResult.get("Monitor"));
				} else {
					this.SetResult(wResult, "Monitor", new BMSEmployee());
				}
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.getFaultCode());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}
}
