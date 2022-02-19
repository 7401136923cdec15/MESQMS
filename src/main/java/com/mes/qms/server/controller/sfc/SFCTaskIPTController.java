package com.mes.qms.server.controller.sfc;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.mes.qms.server.service.IPTService;
import com.mes.qms.server.service.SFCService;
import com.mes.qms.server.service.mesenum.SFCTaskStatus;
import com.mes.qms.server.service.mesenum.SFCTaskType;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTItemApply;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.mss.MSSPartItem;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTInfo;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTPart;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTPartNo;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress01;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress02;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress03;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RetCode;

/**
 * 巡检控制器
 * 
 * @author ShrisJava
 * 
 */
@RestController
@RequestMapping("/api/SFCTaskIPT")
public class SFCTaskIPTController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(SFCTaskIPTController.class);

	@Autowired
	SFCService wSFCService;

	@Autowired
	IPTService wIPTService;

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

			ServiceResult<SFCTaskIPT> wServiceResult = wSFCService.SFC_QueryTaskIPTByID(wLoginUser, wID);

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
	 * 更新巡检任务
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/Update")
	public Object Update(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			SFCTaskIPT wSFCTaskIPT = CloneTool.Clone(wParam.get("data"), SFCTaskIPT.class);

			if (wSFCTaskIPT == null || wSFCTaskIPT.ID <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
			}

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_UpdateTaskIPT(wLoginUser, wSFCTaskIPT);

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

			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wTaskType = StringUtils.parseInt(request.getParameter("TaskType"));

			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));
			int wStepID = StringUtils.parseInt(request.getParameter("StepID"));
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));

			ServiceResult<List<SFCTaskIPT>> wServiceResult = wSFCService.SFC_QueryTaskIPTList(wLoginUser, wOrderID,
					wLineID, wProductID, wPartID, wTaskType, wStepID, wPartNo, wStartTime, wEndTime);

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
	 * 根据工序任务ID查询检验单集合
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/RFTaskList")
	public Object RFTaskList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wAPSTaskStepID = StringUtils.parseInt(request.getParameter("APSTaskStepID"));

			ServiceResult<List<SFCTaskIPT>> wServiceResult = wSFCService.SFC_QueryRFTaskIPTList(wLoginUser,
					wAPSTaskStepID);

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
	 * 获取车号分类的专检任务列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/SpecialPartNo")
	public Object SpecialPartNo(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

//			ServiceResult<List<SFCTaskIPTPartNo>> wServiceResult = wSFCService.SFC_QuerySpecialPartNo(wLoginUser);
//			ServiceResult<List<SFCTaskIPTPartNo>> wServiceResult = wSFCService.SFC_QuerySpecialPartNoNew(wLoginUser);
			ServiceResult<List<SFCTaskIPTPartNo>> wServiceResult = wSFCService.SFC_QuerySpecialPartNoNew_V2(wLoginUser);

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
	 * 获取工位分类的专检任务列表
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/SpecialPart")
	public Object SpecialPart(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

//			ServiceResult<List<SFCTaskIPTPart>> wServiceResult = wSFCService.SFC_QuerySpecialPartList(wLoginUser,
//					wOrderID);
//			ServiceResult<List<SFCTaskIPTPart>> wServiceResult = wSFCService.SFC_QuerySpecialPartListNew(wLoginUser,
//					wOrderID);
			ServiceResult<List<SFCTaskIPTPart>> wServiceResult = wSFCService.SFC_QuerySpecialPartListNew_V2(wLoginUser,
					wOrderID);

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
	 * 获取巡检任务集合
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/TaskList")
	public Object TaskList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wTaskType = StringUtils.parseInt(request.getParameter("TaskType")).intValue();
			Calendar wStartTime = StringUtils.parseCalendar(request.getParameter("StartTime"));
			Calendar wEndTime = StringUtils.parseCalendar(request.getParameter("EndTime"));
			String wPartNo = StringUtils.parseString(request.getParameter("PartNo"));
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

//			ServiceResult<List<SFCTaskIPT>> wServiceResult = this.wSFCService.SFC_QueryTaskIPTList(wLoginUser,
//					wTaskType, wPartNo, wStartTime, wEndTime, wOrderID);

			ServiceResult<List<SFCTaskIPT>> wServiceResult = wSFCService.SFC_QueryTaskIPTListNew(wLoginUser, wTaskType,
					wPartNo, wStartTime, wEndTime, wOrderID, wPartID);

			if (wServiceResult.Result.size() > 0 && wPartID > 0) {
				wServiceResult.Result = wServiceResult.Result.stream().filter(p -> p.StationID == wPartID)
						.collect(Collectors.toList());
			}

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);

//				List<SFCTaskIPT> wIPTList = (List<SFCTaskIPT>) (this.wSFCService.SFC_QueryProblemTaskAll(wLoginUser,
//						SFCTaskType.getEnumType(wTaskType))).Result;

//				if (StringUtils.isNotEmpty(wPartNo)) {
//					wIPTList = wIPTList.stream().filter(p -> p.PartNo.equals(wPartNo)).collect(Collectors.toList());
//				}
//				if (wOrderID > 0) {
//					wIPTList = wIPTList.stream().filter(p -> p.OrderID == wOrderID).collect(Collectors.toList());
//				}
//				if (wPartID > 0) {
//					wIPTList = wIPTList.stream().filter(p -> p.StationID == wPartID).collect(Collectors.toList());
//				}

//				SetResult(wResult, "ProblemList", wIPTList);

				// 待做和已做
				List<SFCTaskIPT> wToDoList = new ArrayList<SFCTaskIPT>();
				if (wServiceResult.Result.size() > 0) {
					wToDoList = wServiceResult.Result.stream().filter(p -> p.Status == SFCTaskStatus.Active.getValue())
							.collect(Collectors.toList());
				}

				// 添加问题项待做
//				if (wIPTList.size() > 0) {
//					wToDoList.addAll(wIPTList.stream().filter(p -> p.Status == SFCTaskStatus.Active.getValue())
//							.collect(Collectors.toList()));
//				}

				List<SFCTaskIPT> wDoneList = new ArrayList<SFCTaskIPT>();
				if (wServiceResult.Result.size() > 0) {
					wDoneList = wServiceResult.Result.stream().filter(p -> p.Status == SFCTaskStatus.Done.getValue())
							.collect(Collectors.toList());
				}

				// 添加问题项已做
//				if (wIPTList.size() > 0) {
//					wDoneList.addAll(wIPTList.stream().filter(p -> p.Status == SFCTaskStatus.Done.getValue())
//							.collect(Collectors.toList()));
//				}

				this.SetResult(wResult, "ToDoList", wToDoList);
				this.SetResult(wResult, "DoneList", wDoneList);
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
	 * 根据检验单ID获取检验项和检验值集合
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GetMapping("/IPTItemList")
	public Object IPTItemList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wSFCTaskIPTID = StringUtils.parseInt(request.getParameter("SFCTaskIPTID"));

			ServiceResult<SFCTaskIPT> wIPTSR = wSFCService.SFC_QueryTaskIPTByID(wLoginUser, wSFCTaskIPTID);

			ServiceResult<List<Object>> wServiceResult = wSFCService.SFC_QueryIPTItemValueList(wLoginUser,
					wSFCTaskIPTID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wIPTSR.Result);
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}

			List<IPTItem> wToDoList = (List<IPTItem>) wServiceResult.Result.get(0);
			List<IPTItem> wDoneList = (List<IPTItem>) wServiceResult.Result.get(1);
			List<IPTValue> wValueList = (List<IPTValue>) wServiceResult.Result.get(2);

			// 待做任务
			SetResult(wResult, "ToDoList", wToDoList);
			// 已做任务
			SetResult(wResult, "DoneList", wDoneList);
			// 填写的检验值
			SetResult(wResult, "ValueList", wValueList);

			// 预检相关数据
			ServiceResult<List<IPTPreCheckProblem>> wProblemResult = wSFCService
					.SFC_QueryPreCheckProblemList(wLoginUser, wSFCTaskIPTID);
			// 预检问题项
			this.SetResult(wResult, "YJProblemList", wProblemResult.Result);
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	/**
	 * 保存填写检验值(提交自检、互检、专检、出厂检的值)
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/SaveValueList")
	public synchronized Object SaveValueList(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			SFCTaskIPT wSFCTaskIPT = CloneTool.Clone(wParam.get("SFCTaskIPT"), SFCTaskIPT.class);
			List<IPTValue> wIPTValueList = CloneTool.CloneArray(wParam.get("IPTValueList"), IPTValue.class);
			int wOperateType = StringUtils.parseInt(wParam.get("OperateType"));

			// 提交检验值
			ServiceResult<Integer> wServiceResult = wSFCService.SFC_SaveValueList_V2(wLoginUser, wSFCTaskIPT,
					wIPTValueList, wOperateType);

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
	 * 自检、互检、专检，预检、终检、出厂检直接提交照片
	 */
	@PostMapping("/SavePic")
	public Object SavePic(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			SFCTaskIPT wSFCTaskIPT = CloneTool.Clone(wParam.get("SFCTaskIPT"), SFCTaskIPT.class);

			if (StringUtils.isEmpty(wSFCTaskIPT.PicUri)) {
				return GetResult(RetCode.SERVER_CODE_ERR, "提示：照片必填!");
			}

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_SaveSavePic(wLoginUser, wSFCTaskIPT);

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
	 * 预检项目申请
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/ItemApply")
	public Object ItemApply(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			IPTItemApply wIPTItemApply = CloneTool.Clone(wParam.get("IPTItemApply"), IPTItemApply.class);

			ServiceResult<IPTItem> wServiceResult = wIPTService.IPT_ItemApply(wLoginUser, wIPTItemApply);

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
	 * 问题项检验任务查询
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ProblemTaskAll")
	public Object ProblemTaskAll(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 任务类型
			SFCTaskType wTaskType = SFCTaskType.getEnumType(StringUtils.parseInt(request.getParameter("TaskType")));

			ServiceResult<List<SFCTaskIPT>> wServiceResult = wSFCService.SFC_QueryProblemTaskAll(wLoginUser, wTaskType);

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
	 * 根据预检问题项检验单获取检验项和检验值
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ProblemItemInfo")
	public Object ProblemItemInfo(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wSFCTaskIPTID = StringUtils.parseInt(request.getParameter("SFCTaskIPTID"));
			ServiceResult<IPTItem> wServiceResult = wSFCService.SFC_QueryProblemItemInfo(wLoginUser, wSFCTaskIPTID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, wServiceResult.Result);
				this.SetResult(wResult, "DoneItem", wServiceResult.CustomResult.get("DoneItem"));
				this.SetResult(wResult, "Value", wServiceResult.CustomResult.get("Value"));
				ServiceResult<SFCTaskIPT> wIPTSR = wSFCService.SFC_QueryTaskIPTByID(wLoginUser, wSFCTaskIPTID);
				this.SetResult(wResult, "SFCTaskIPT", wIPTSR.Result);
				this.SetResult(wResult, "OtherValue", wServiceResult.CustomResult.get("OtherValue"));
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
	 * 提交或保存预检问题项的检验值
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/ProblemSubmitValue")
	public Object ProblemSubmitValue(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			SFCTaskIPT wSFCTaskIPT = CloneTool.Clone(wParam.get("SFCTaskIPT"), SFCTaskIPT.class);
			IPTValue wIPTValue = CloneTool.Clone(wParam.get("IPTValue"), IPTValue.class);
			int wOperateType = StringUtils.parseInt(wParam.get("OperateType"));

			// 数据验证
			if (wIPTValue == null || wSFCTaskIPT == null || wSFCTaskIPT.ID <= 0) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			wIPTValue.SubmitID = wLoginUser.ID;
			wIPTValue.SubmitTime = Calendar.getInstance();
			wIPTValue.Status = wOperateType;

			// 保存检验值
			wIPTService.IPT_SaveIPTValue(wLoginUser, new ArrayList<IPTValue>(Arrays.asList(wIPTValue)), wSFCTaskIPT.ID,
					0);

			ServiceResult<Integer> wServiceResult = new ServiceResult<Integer>();
			if (wOperateType == 2) {
				// 修改问题项状态和执行班组并触发新的问题项检验任务
				wServiceResult = wSFCService.SFC_ProblemValueSubmit(wLoginUser, wIPTValue, wSFCTaskIPT);
			}

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
	 * 根据订单获取部件数据
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/PartItemList")
	public Object PartItemList(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<MSSPartItem>> wServiceResult = wSFCService.SFC_PartItemList(wLoginUser, wOrderID);

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
	 * 查询预检在厂台车部件数据
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/PrePartItemList")
	public Object PrePartItemList(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			String wCode = StringUtils.parseString(request.getParameter("Code"));
			String wSuplierPartNo = StringUtils.parseString(request.getParameter("SuplierPartNo"));
			int wLineID = StringUtils.parseInt(request.getParameter("LineID"));
			int wProductID = StringUtils.parseInt(request.getParameter("ProductID"));

			ServiceResult<List<MSSPartItem>> wServiceResult = wSFCService.SFC_QueryPrePartItemList(wLoginUser, wCode,
					wSuplierPartNo, wLineID, wProductID);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", wServiceResult.Result, null);
				if (wServiceResult.CustomResult.containsKey("ProductList")) {
					SetResult(wResult, "ProductList", wServiceResult.CustomResult.get("ProductList"));
				}
				if (wServiceResult.CustomResult.containsKey("LineList")) {
					SetResult(wResult, "LineList", wServiceResult.CustomResult.get("LineList"));
				}
				if (wServiceResult.CustomResult.containsKey("CustomerList")) {
					SetResult(wResult, "CustomerList", wServiceResult.CustomResult.get("CustomerList"));
				}
			} else {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, wServiceResult.FaultCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			wResult = GetResult(RetCode.SERVER_CODE_ERR, ex.toString(), null, null);
		}
		return wResult;
	}

	@GetMapping({ "/PartConfigDetails" })
	public Object PartConfigDetails(HttpServletRequest request) {
		Map<String, Object> wResult = new HashMap<>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			String wPartConfigNo = StringUtils.parseString(request.getParameter("PartConfigNo"));

			if (StringUtils.isEmpty(wPartConfigNo)) {
				wResult = GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
				return wResult;
			}

			ServiceResult<Integer> wServiceResult = this.wSFCService.SFC_QueryPartConfigDetails(wLoginUser,
					wPartConfigNo);

			if (StringUtils.isEmpty(wServiceResult.FaultCode)) {
				wResult = GetResult(RetCode.SERVER_CODE_SUC, "", null, null);
				if (wServiceResult.CustomResult.containsKey("ProductList")) {
					SetResult(wResult, "ProductList", wServiceResult.CustomResult.get("ProductList"));
				}
				if (wServiceResult.CustomResult.containsKey("LineList")) {
					SetResult(wResult, "LineList", wServiceResult.CustomResult.get("LineList"));
				}
				if (wServiceResult.CustomResult.containsKey("CustomerList")) {
					SetResult(wResult, "CustomerList", wServiceResult.CustomResult.get("CustomerList"));
				}
				if (wServiceResult.CustomResult.containsKey("MaterialList")) {
					SetResult(wResult, "MaterialList", wServiceResult.CustomResult.get("MaterialList"));
				}
				if (wServiceResult.CustomResult.containsKey("UnitList")) {
					SetResult(wResult, "UnitList", wServiceResult.CustomResult.get("UnitList"));
				}
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
	 * 将自检不合格项提交给专检或自检待做
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/HandleSelfCheckItem")
	public Object HandleSelfCheckItem(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wTaskIPTID = StringUtils.parseInt(wParam.get("TaskIPTID"));
			int wIPTItemID = StringUtils.parseInt(wParam.get("IPTItemID"));
			int wAssessResult = StringUtils.parseInt(wParam.get("AssessResult"));

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_HandleSelfCheckItem(wLoginUser, wTaskIPTID,
					wIPTItemID, wAssessResult);

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
	 * 撤回专检项点
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/WithdrawSpecialItem")
	public Object WithdrawSpecialItem(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			IPTValue wValue = CloneTool.Clone(wParam.get("data"), IPTValue.class);
			if (wValue == null || wValue.ID <= 0 || wValue.IPTMode != SFCTaskType.SpecialCheck.getValue()) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
			}

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_WithdrawSpecialItem(wLoginUser, wValue);

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
	 * 批量提交项点(多个工序)
	 * 
	 * @param request
	 * @param wParam
	 * @return
	 */
	@PostMapping("/SubmitStepList")
	public Object SubmitStepList(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			List<Integer> wDataIDList = CloneTool.CloneArray(wParam.get("data"), Integer.class);

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_SubmitStepList(wLoginUser, wDataIDList);

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
	 * 获取工序任务详情
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/TaskStepDetails")
	public Object TaskStepDetails(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wAPSTaskStepID = StringUtils.parseInt(request.getParameter("APSTaskStepID"));

			ServiceResult<List<SFCTaskIPTInfo>> wServiceResult = wSFCService.SFC_QueryTaskStepDetails(wLoginUser,
					wAPSTaskStepID);

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
	 * 新版过程控制记录导出 Type：1：导整车 2：导工位 3：导工序
	 */
	@GetMapping("/ExportNewStandard")
	public Object ExportNewStandard(HttpServletRequest request) {
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
			int wStepID = StringUtils.parseInt(request.getParameter("StepID"));
			int wType = StringUtils.parseInt(request.getParameter("Type"));

			ServiceResult<String> wServiceResult = wSFCService.SFC_ExportNewStandard_V2(wLoginUser, wOrderID, wPartID,
					wStepID, wType);

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
	 * 专检存档
	 */
	@PostMapping("/SaveRecord")
	public Object SaveRecord(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(wParam.get("OrderID"));
			List<Integer> wPartIDList = CloneTool.CloneArray(wParam.get("PartIDList"), Integer.class);

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_SaveRecord(wLoginUser, wOrderID, wPartIDList);

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
	 * 专检拍照工序驳回
	 */
	@PostMapping("/BackPic")
	public Object BackPic(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			SFCTaskIPT wTask = CloneTool.Clone(wParam.get("data"), SFCTaskIPT.class);

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_BackPic(wLoginUser, wTask);

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
	 * 更新所有在修车辆的专检单的检察员
	 */
	@GetMapping("/UpdateChecker")
	public Object UpdateChecker(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_UpdateChecker(wLoginUser);

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
	 * 专检驳回普通工序
	 */
	@PostMapping("/RejectStep")
	public Object RejectStep(HttpServletRequest request, @RequestBody Map<String, Object> wParam) {
		Map<String, Object> wResult = new HashMap<String, Object>();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			SFCTaskIPT wTask = CloneTool.Clone(wParam.get("data"), SFCTaskIPT.class);

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_RejectStep(wLoginUser, wTask);

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
	 * 车辆转序情况第一层(台车进度展示)
	 */
	@GetMapping("/TrainProgress01")
	public Object TrainProgress01(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			ServiceResult<List<SFCTrainProgress01>> wServiceResult = wSFCService.SFC_QueryTrainProgress01(wLoginUser);

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
	 * 车辆转序情况第二层(工位进度展示)
	 */
	@GetMapping("/TrainProgress02")
	public Object TrainProgress02(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));

			ServiceResult<List<SFCTrainProgress02>> wServiceResult = wSFCService.SFC_QueryTrainProgress02(wLoginUser,
					wOrderID);

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
	 * 车辆转序情况第三层(工序进度展示)
	 */
	@GetMapping("/TrainProgress03")
	public Object TrainProgress03(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			int wOrderID = StringUtils.parseInt(request.getParameter("OrderID"));
			int wPartID = StringUtils.parseInt(request.getParameter("PartID"));

			ServiceResult<List<SFCTrainProgress03>> wServiceResult = wSFCService.SFC_QueryTrainProgress03(wLoginUser,
					wOrderID, wPartID);

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
	 * 设置为合格
	 */
	@GetMapping("/SetQuality")
	public Object SetQuality(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wSFCTaskIPT = StringUtils.parseInt(request.getParameter("SFCTaskIPT"));
			int wIPTItemID = StringUtils.parseInt(request.getParameter("IPTItemID"));

			if (wSFCTaskIPT <= 0 || wIPTItemID <= 0) {
				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
			}

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_SetQuality(wLoginUser, wSFCTaskIPT, wIPTItemID);

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
	 * 返修单撤销，专检改为未完成状态
	 */
	@GetMapping("/RepairBack")
	public Object RepairBack(HttpServletRequest request) {
		Object wResult = new Object();
		try {
			if (CheckCookieEmpty(request)) {
				wResult = GetResult(RetCode.SERVER_CODE_UNLOGIN, "");
				return wResult;
			}

			BMSEmployee wLoginUser = GetSession(request);

			// 获取参数
			int wSFCTaskIPT = StringUtils.parseInt(request.getParameter("SFCTaskIPT"));
			int wIPTItemID = StringUtils.parseInt(request.getParameter("IPTItemID"));

//			if (wSFCTaskIPT <= 0 || wIPTItemID <= 0) {
//				return GetResult(RetCode.SERVER_CODE_ERR, RetCode.SERVER_RST_ERROR_OUT);
//			}

			ServiceResult<Integer> wServiceResult = wSFCService.SFC_RepairBack(wLoginUser, wSFCTaskIPT, wIPTItemID);

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
