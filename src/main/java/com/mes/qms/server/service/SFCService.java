package com.mes.qms.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.SFCTaskType;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.bms.BMSDepartment;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.focas.FocasHistoryData;
import com.mes.qms.server.service.po.focas.FocasPart;
import com.mes.qms.server.service.po.focas.FocasReport;
import com.mes.qms.server.service.po.focas.FocasResult;
import com.mes.qms.server.service.po.fpc.FPCCommonFile;
import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckReport;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.mss.MSSPartItem;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.pic.SFCCarInfo;
import com.mes.qms.server.service.po.pic.SFCProgress;
import com.mes.qms.server.service.po.pic.SFCRankInfo;
import com.mes.qms.server.service.po.sfc.SFCBogiesChangeBPM;
import com.mes.qms.server.service.po.sfc.SFCLetPassBPM;
import com.mes.qms.server.service.po.sfc.SFCLoginEvent;
import com.mes.qms.server.service.po.sfc.SFCLoginEventPart;
import com.mes.qms.server.service.po.sfc.SFCReturnOverMaterial;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTInfo;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTPart;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTPartNo;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.po.sfc.SFCTemporaryExamination;
import com.mes.qms.server.service.po.sfc.SFCTemporaryExaminationPartItem;
import com.mes.qms.server.service.po.sfc.SFCTemporaryExaminationStepItem;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress01;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress02;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress03;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-2-21 13:42:53
 * @LastEditTime 2020-2-21 13:42:57
 *
 */
public interface SFCService {

	/**
	 * 根据主键获取单条打卡记录
	 * 
	 * @param wLoginUser
	 * @param wID
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<SFCLoginEvent> SFC_QueryLoginEvent(BMSEmployee wLoginUser, int wID);

	/**
	 * 条件查询打卡记录
	 * 
	 * @param wLoginUser  登陆者
	 * @param wWorkShopID 车间ID
	 * @param wStationID  工位ID
	 * @param wModuleID   模块ID
	 * @param wTime       查询时间：哪一天
	 * @param wActive     激活、禁用
	 * @param wType       类型：上班、下班
	 * @param wErrorCode  错误码
	 * @return 打卡集合
	 */
	ServiceResult<List<SFCLoginEvent>> SFC_QueryLoginEventList(BMSEmployee wLoginUser, int wWorkShopID, int wStationID,
			int wModuleID, Calendar wTime, int wActive, int wType);

	/**
	 * 新增打卡记录
	 * 
	 * @param wLoginUser
	 * @param wSFCLoginEvent
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<Long> SFC_UpdateLoginEvent(BMSEmployee wLoginUser, SFCLoginEvent wSFCLoginEvent);

	/**
	 * 批量激活或禁用打卡记录
	 * 
	 * @param wLoginUser
	 * @param wIDList
	 * @param wActive
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<Integer> SFC_ActiveLoginEventList(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive);

	/**
	 * 查询某人某天的打卡记录
	 * 
	 * @param wLoginUser
	 * @param wDate
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<List<SFCLoginEvent>> SFC_QueryLoginEventListByEmployee(BMSEmployee wLoginUser, Calendar wDate);

	/**
	 * 打卡
	 * 
	 * @param wLoginUser 登录人
	 * @param wType      打卡类型：上班、下班
	 * @param wDataList  派工任务集合
	 * @param wErrorCode 错误码
	 * @return
	 */
	ServiceResult<Integer> SFC_Clock(BMSEmployee wLoginUser, int wType, List<SFCTaskStep> wDataList, int wAPPEventID);

	/**
	 * 查询预检问题项集合
	 * 
	 * @param wLoginUser    登录信息
	 * @param wSFCTaskIPTID 预检单ID
	 * @param wErrorCode    错误码
	 * @return
	 */
	ServiceResult<List<IPTPreCheckProblem>> SFC_QueryPreCheckProblemList(BMSEmployee wLoginUser, int wSFCTaskIPTID);

	/**
	 * 查询巡检任务集合
	 * 
	 * @param wLoginUser
	 * @param wTaskType
	 * @return
	 */
	ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTList(BMSEmployee wLoginUser, int wTaskType, String wPartNo,
			Calendar wStartTime, Calendar wEndTime, int wOrderID);

	ServiceResult<SFCTaskIPT> SFC_QueryTaskIPTByID(BMSEmployee wLoginUser, int wSFCTaskIPTID);

	ServiceResult<Integer> SFC_UpdateTaskIPT(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT);

	ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTList(BMSEmployee wLoginUser, int wOrderID, int wLineID,
			int wProductID, int wPartID, int wTaskType, int wStepID, String wPartNo, Calendar wStartTime,
			Calendar wEndTime);

	/**
	 * 判断该工位计划中所有下达的工序计划是否全部做完互检
	 * 
	 * @param wLoginUser   登录信息
	 * @param wAPSTaskPart 工位计划
	 * @return 布尔结果
	 */
	ServiceResult<Boolean> SFC_JudgeIsAllStepOK(BMSEmployee wLoginUser, APSTaskPart wAPSTaskPart);

	/**
	 * 查询预检工位ID集合
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<Integer>> SFC_QueryYJStationIDList(BMSEmployee wLoginUser);

	/**
	 * 检查派工权限
	 * 
	 * @param wLoginUser
	 * @param wAPSTaskStepIDList
	 * @return
	 */
	ServiceResult<String> SFC_CheckPGPower(BMSEmployee wLoginUser, List<Integer> wAPSTaskStepIDList);

	/**
	 * 查询待做、已做检验项和值
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPTID
	 * @return
	 */
	ServiceResult<List<Object>> SFC_QueryIPTItemValueList(BMSEmployee wLoginUser, int wSFCTaskIPTID);

	/**
	 * 根据工序任务ID和巡检任务类型查询待做和已做的检验项集合
	 * 
	 * @param wAPSTaskStepID
	 * @param wTaskType
	 * @return
	 */
	ServiceResult<List<Object>> SFC_QueryToDoAndDoneList(BMSEmployee wLoginUser, int wAPSTaskStepID, int wTaskType);

	/**
	 * 根据工序任务ID查询巡检单集合
	 * 
	 * @param wLoginUser
	 * @param wAPSTaskStepID
	 * @return
	 */
	ServiceResult<List<SFCTaskIPT>> SFC_QueryRFTaskIPTList(BMSEmployee wLoginUser, int wAPSTaskStepID);

	/**
	 * 通过工区ID查询所有班组列表
	 * 
	 * @param wLoginUser
	 * @param wAreaID
	 * @return
	 */
	ServiceResult<List<BMSDepartment>> SFC_QueryClassListByAreaID(BMSEmployee wLoginUser, int wAreaID);

	/**
	 * 通过班组ID查询所有班组成员列表
	 * 
	 * @param wLoginUser
	 * @param wClassID
	 * @return
	 */
	ServiceResult<List<BMSEmployee>> SFC_QueryClassMemberListByClassID(BMSEmployee wLoginUser, int wClassID);

	/**
	 * 根据问题项ID获取问题项各节点信息
	 * 
	 * @param wLoginUser
	 * @param wProblemID
	 * @return
	 */
	ServiceResult<IPTPreCheckProblem> SFC_QueryProblemNodeInfo(BMSEmployee wLoginUser, int wProblemID);

	/**
	 * 查询台车报告
	 * 
	 * @param wLoginUser
	 * @param wPartNo
	 * @return
	 */
	ServiceResult<IPTPreCheckReport> SFC_QueryPreCheckReport(BMSEmployee wLoginUser, String wPartNo);

	/**
	 * 查询问题项检验任务集合
	 * 
	 * @param wLoginUser
	 * @param wTaskType
	 * @return
	 */
	ServiceResult<List<SFCTaskIPT>> SFC_QueryProblemTaskAll(BMSEmployee wLoginUser, SFCTaskType wTaskType);

	/**
	 * 根据预检问题项检验单获取检验项和检验值
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPTID
	 * @return
	 */
	ServiceResult<IPTItem> SFC_QueryProblemItemInfo(BMSEmployee wLoginUser, int wSFCTaskIPTID);

	/**
	 * 修改问题项状态和执行班组并触发新的问题项检验任务
	 * 
	 * @param wLoginUser  登录信息
	 * @param wIPTValue   填写值
	 * @param wSFCTaskIPT 问题项检验任务
	 * @return
	 */
	ServiceResult<Integer> SFC_ProblemValueSubmit(BMSEmployee wLoginUser, IPTValue wIPTValue, SFCTaskIPT wSFCTaskIPT);

	/**
	 * 车的部件记录查询接口
	 * 
	 * @param wLoginUser 登陆者
	 * @param wOrderID   订单ID
	 * @return 部件记录列表
	 */
	ServiceResult<List<MSSPartItem>> SFC_PartItemList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 查询预检在厂台车部件数据
	 * 
	 * @param wLoginUser
	 * @param wCode
	 * @param wSuplierPartNo
	 * @param wLineID
	 * @param wProductID
	 * @return
	 */
	ServiceResult<List<MSSPartItem>> SFC_QueryPrePartItemList(BMSEmployee wLoginUser, String wCode,
			String wSuplierPartNo, int wLineID, int wProductID);

	ServiceResult<Integer> SFC_QueryPartConfigDetails(BMSEmployee wLoginUser, String wPartConfigNo);

	/**
	 * 做自检任务时要检查此人在此工序任务是否开工打卡，且此人能否做自检
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPT
	 * @return
	 */
	ServiceResult<String> SFC_CheckIsTaskClocked(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT);

	/**
	 * 通过订单获取可检查的工位列表
	 */
	ServiceResult<List<FPCPart>> SFC_QueryCanCheckPartList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 将自检不合格项提交给专检或自检待做
	 */
	ServiceResult<Integer> SFC_HandleSelfCheckItem(BMSEmployee wLoginUser, int wTaskIPTID, int wIPTItemID,
			int wAssessResult);

	/**
	 * 创建临时性检查单
	 */
	ServiceResult<SFCTemporaryExamination> SFC_CreateTemporaryExamination(BMSEmployee wLoginUser, Integer wOrderID);

	/**
	 * 提交临时性检查单
	 */
	ServiceResult<Integer> SFC_SubmitTemporaryExamination(BMSEmployee wLoginUser, SFCTemporaryExamination wData);

	/**
	 * 查询待做、已做临时性检查任务列表
	 */
	ServiceResult<List<SFCTemporaryExamination>> SFC_QueryEmployeeAllTemporaryExaminationList(BMSEmployee wLoginUser,
			int wTagTypes, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 提交临时性检查单的检验值
	 */
	ServiceResult<Integer> SFC_SubmitValueList(BMSEmployee wLoginUser, List<IPTValue> wDataList);

	/**
	 * 通过临时性检查任务单ID查询所有的检验项和检验值(分工位、工序)
	 */
	ServiceResult<List<SFCTemporaryExaminationPartItem>> SFC_QueryTemporaryExaminationPartItemList(
			BMSEmployee wLoginUser, int wSFCTemporaryExaminationID);

	/**
	 * 分车号展示检验任务
	 */
	ServiceResult<List<SFCTaskIPTPartNo>> SFC_GetTaskIPTPartNoList(BMSEmployee wLoginUser, List<SFCTaskIPT> wToDoList,
			List<SFCTaskIPT> wAllList, boolean wIsToDo);

	/**
	 * 分车展示专检任务
	 */
	ServiceResult<List<SFCTaskIPTPartNo>> SFC_QuerySpecialPartNo(BMSEmployee wLoginUser);

	/**
	 * 获取工位分类的专检任务
	 */
	ServiceResult<List<SFCTaskIPTPart>> SFC_QuerySpecialPartList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 查询默认状态的单据
	 */
	ServiceResult<SFCReturnOverMaterial> SFC_QueryDefaultReturnOverMaterial(BMSEmployee wLoginUser, int wEventID);

	/**
	 * 创建单据
	 */
	ServiceResult<SFCReturnOverMaterial> SFC_CreateReturnOverMaterial(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * 提交单据
	 */
	ServiceResult<SFCReturnOverMaterial> SFC_SubmitReturnOverMaterial(BMSEmployee wLoginUser,
			SFCReturnOverMaterial wData);

	/**
	 * 查询单条单据
	 */
	ServiceResult<SFCReturnOverMaterial> SFC_GetReturnOverMaterial(BMSEmployee wLoginUser, int wID);

	/**
	 * 用人员拿任务
	 */
	ServiceResult<List<BPMTaskBase>> SFC_QueryReturnOverMaterialEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询单据历史
	 */
	ServiceResult<List<SFCReturnOverMaterial>> SFC_QueryReturnOverMaterialHistory(BMSEmployee wLoginUser, int wID,
			String wCode, int wUpFlowID, int wMaterialID, int wPartID, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 撤回专检项点
	 */
	ServiceResult<Integer> SFC_WithdrawSpecialItem(BMSEmployee wLoginUser, IPTValue wValue);

	/**
	 * 获取登录者所在班组的工位列表(激活的)
	 */
	ServiceResult<List<FPCPart>> SFC_QueryClassList(BMSEmployee wLoginUser);

	/**
	 * 获取检查的工位列表
	 */
	ServiceResult<List<SFCTemporaryExaminationPartItem>> SFC_QueryItemPartList(BMSEmployee wLoginUser,
			int wSFCTemporaryExaminationID);

	/**
	 * 获取检查的工序列表
	 */
	ServiceResult<List<SFCTemporaryExaminationStepItem>> SFC_QueryItemStepList(BMSEmployee wLoginUser,
			int wSFCTemporaryExaminationID, int wPartID);

	/**
	 * 获取检验项集合
	 */
	ServiceResult<List<IPTItem>> SFC_QueryItemItemList(BMSEmployee wLoginUser, int wSFCTemporaryExaminationID,
			int wPartID, int wStepID);

	/**
	 * 批量提交项点(多个工序)
	 */
	ServiceResult<Integer> SFC_SubmitStepList(BMSEmployee wLoginUser, List<Integer> wDataIDList);

	/**
	 * 查询单条车辆图片
	 */
	ServiceResult<SFCCarInfo> SFC_QueryCarInfo(BMSEmployee wLoginUser, int wID);

	ServiceResult<List<SFCCarInfo>> SFC_QueryCarInfoList(BMSEmployee wLoginUser, int wID, int wOrderID);

	ServiceResult<Integer> SFC_UpdateCarInfo(BMSEmployee wLoginUser, SFCCarInfo wSFCCarInfo);

	/**
	 * 导出图片zip
	 */
	ServiceResult<String> SFC_ExportPicZip(BMSEmployee wLoginUser, int wID);

	/**
	 * 删除整车图片
	 */
	ServiceResult<Integer> SFC_DeleteAllPic(BMSEmployee wLoginUser, int wID);

	/**
	 * 删除单个图片
	 */
	ServiceResult<Integer> SFC_DeleteSinglePic(BMSEmployee wLoginUser, String wUri);

	/**
	 * 查询进度
	 */
	ServiceResult<SFCProgress> SFC_QueryProgress(BMSEmployee wLoginUser, String wUUID);

	/**
	 * 导出图片加进度条
	 */
	ServiceResult<String> SFC_ExportPicZip(BMSEmployee wLoginUser, int wID, String wUUID);

	/**
	 * 验证派工任务是否能开工(检查前工位是否已转序)
	 */
	ServiceResult<String> SFC_CheckPGPowerNew(BMSEmployee wLoginUser, List<SFCTaskStep> wSFCTaskStepList);

	/**
	 * 获取车辆分类的专检任务
	 */
	ServiceResult<List<SFCTaskIPTPartNo>> SFC_QuerySpecialPartNoNew(BMSEmployee wLoginUser);

	/**
	 * 获取工位分类的专检任务
	 */
	ServiceResult<List<SFCTaskIPTPart>> SFC_QuerySpecialPartListNew(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 获取检验任务(自检、互检、专检、预检、出厂检、终检)
	 * 
	 * @param wPartID
	 */
	ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListNew(BMSEmployee wLoginUser, int wTaskType, String wPartNo,
			Calendar wStartTime, Calendar wEndTime, int wOrderID, int wPartID);

	/**
	 * 导出图片(根据筛选条件)
	 */
	ServiceResult<String> SFC_ExportPartInfoByCondition(BMSEmployee wLoginUser, List<Integer> wIDList,
			List<Integer> wPartIDList);

	/**
	 * 根据车号获取台车部件信息
	 */
	ServiceResult<List<FocasPart>> SFC_QueryMSSPartList(BMSEmployee wLoginUser, String wPartNo);

	/**
	 * 获取工序任务详情
	 */
	ServiceResult<List<SFCTaskIPTInfo>> SFC_QueryTaskStepDetails(BMSEmployee wLoginUser, int wAPSTaskStepID);

	/**
	 * 获取打卡工位分组任务
	 */
	ServiceResult<List<SFCLoginEventPart>> SFC_QueryEmployeeAllPart(BMSEmployee wLoginUser);

	/**
	 * 获取打卡的数据源
	 */
	ServiceResult<List<SFCTaskStep>> SFC_QueryClockEmployeeAllNew(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			Calendar wStartTime, Calendar wEndTime, int wTagTypes);

	/**
	 * 提交检验值
	 */
	ServiceResult<Integer> SFC_SaveValueList_V2(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList, int wOperateType);

	/**
	 * 打卡
	 */
	ServiceResult<Integer> SFC_Clock_V2(BMSEmployee wLoginUser, int wType, List<SFCTaskStep> wDataList, int wEventID);

	/**
	 * 获取工位分组的打卡任务
	 */
	ServiceResult<List<SFCLoginEventPart>> SFC_QueryEmployeeAllPart_V2(BMSEmployee wLoginUser);

	/**
	 * 获取打卡任务
	 */
	ServiceResult<List<SFCTaskStep>> SFC_QueryClockEmployeeAllNew_V2(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			int wTagTypes);

	/**
	 * 条件查询通用文件
	 */
	ServiceResult<List<FPCCommonFile>> SFC_QueryCommonFileList(BMSEmployee wLoginUser, int wID, String wCode,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 导出新版过程控制记录
	 */
	ServiceResult<String> SFC_ExportNewStandard(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wStepID);

	/**
	 * 导出新版过程控制记录_V2
	 */
	ServiceResult<String> SFC_ExportNewStandard_V2(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wStepID,
			int wType);

	/**
	 * 位次列表
	 */
	ServiceResult<List<SFCRankInfo>> SFC_QueryRankInfoList(BMSEmployee wLoginUser, int wCarID);

	/**
	 * 获取待办消息列表
	 */
	ServiceResult<String> SFC_QueryMessageToDoList(BMSEmployee wSysAdmin, String wLoginID);

	/**
	 * 获取待阅消息列表
	 */
	ServiceResult<String> SFC_QueryMessageToReadList(BMSEmployee sysAdmin, String wLoginID);

	ServiceResult<List<SFCReturnOverMaterial>> SFC_QueryReturnOverMaterialEmployeeAllNew(BMSEmployee wLoginUser,
			Calendar wStartTime, Calendar wEndTime, int wPartID, int wStatus);

	ServiceResult<List<SFCReturnOverMaterial>> SFC_QueryReturnOverMaterialList(BMSEmployee wLoginUser, int wMaterialID,
			int wPartID, int wStatus, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 获取待办、已办、发起的临时性检查任务
	 */
	ServiceResult<List<SFCTemporaryExamination>> SFC_QueryEmployeeAllTemporaryExaminationListNew(BMSEmployee wLoginUser,
			Calendar wStartTime, Calendar wEndTime, int wOrderID, int wProductID, String wPartNo, int wPartID,
			int wStatus);

	ServiceResult<List<SFCTemporaryExamination>> SFC_QueryEmployeeAllTemporaryExaminationList(BMSEmployee wLoginUser,
			int wOrderID, int wProductID, String wPartNo, int wPartID, int wStatus, Calendar wStartTime,
			Calendar wEndTime);

	/**
	 * 查询默认状态的单据
	 */
	ServiceResult<SFCBogiesChangeBPM> SFC_QueryDefaultBogiesChangeBPM(BMSEmployee wLoginUser, int wEventID);

	/**
	 * 创建单据
	 */
	ServiceResult<SFCBogiesChangeBPM> SFC_CreateBogiesChangeBPM(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * 提交单据
	 */
	ServiceResult<SFCBogiesChangeBPM> SFC_SubmitBogiesChangeBPM(BMSEmployee wLoginUser, SFCBogiesChangeBPM wData);

	/**
	 * 查询单条单据
	 */
	ServiceResult<SFCBogiesChangeBPM> SFC_GetBogiesChangeBPM(BMSEmployee wLoginUser, int wID);

	/**
	 * 用人员拿任务
	 */
	ServiceResult<List<BPMTaskBase>> SFC_QueryBogiesChangeBPMEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询单据历史
	 */
	ServiceResult<List<SFCBogiesChangeBPM>> SFC_QueryBogiesChangeBPMHistory(BMSEmployee wLoginUser, int wID,
			String wCode, int wUpFlowID, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询转向架互换的订单ID
	 */
	ServiceResult<Integer> SFC_QueryChangeOrderID(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<List<SFCBogiesChangeBPM>> SFC_QueryBogiesChangeBPMEmployeeAllNew(BMSEmployee wLoginUser, int wOrderID,
			int wLevel, int wStatus, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 判断能否提交(转向架互换流程)
	 */
	ServiceResult<String> SFC_JudgeIsCanSubmit(BMSEmployee wLoginUser, SFCBogiesChangeBPM wTask);

	/**
	 * 获取统计数据-focas对接
	 */
	ServiceResult<FocasReport> SFC_QueryFocasData(BMSEmployee wLoginUser);

	ServiceResult<List<FocasResult>> SFC_QueryFocasMonthData(BMSEmployee wLoginUser);

	ServiceResult<List<FocasResult>> SFC_QueryFocasYearData(BMSEmployee wLoginUser);

	ServiceResult<List<FocasHistoryData>> SFC_QueryFocasHistoryDataList(BMSEmployee wLoginUser);

	/**
	 * 关闭工艺变更-不合格评审消息
	 */
	ServiceResult<String> BFC_CloseMessage(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wStepID);

	ServiceResult<List<OMSOrder>> SFC_QueryUsableOrderList(BMSEmployee wLoginUser);

	ServiceResult<Integer> BFC_CloseAllMessage(BMSEmployee wLoginUser, int wModuleID, int wMessageID);

	/**
	 * 直接提交照片
	 */
	ServiceResult<Integer> SFC_SaveSavePic(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT);

	/**
	 * 查询默认状态的单据
	 */
	ServiceResult<SFCLetPassBPM> SFC_QueryDefaultLetPassBPM(BMSEmployee wLoginUser, int wEventID);

	/**
	 * 创建单据
	 */
	ServiceResult<SFCLetPassBPM> SFC_CreateLetPassBPM(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * 提交单据
	 */
	ServiceResult<SFCLetPassBPM> SFC_SubmitLetPassBPM(BMSEmployee wLoginUser, SFCLetPassBPM wData);

	/**
	 * 查询单条单据
	 */
	ServiceResult<SFCLetPassBPM> SFC_GetLetPassBPM(BMSEmployee wLoginUser, int wID);

	/**
	 * 用人员拿任务
	 */
	ServiceResult<List<BPMTaskBase>> SFC_QueryLetPassBPMEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询单据历史
	 */
	ServiceResult<List<SFCLetPassBPM>> SFC_QueryLetPassBPMHistory(BMSEmployee wLoginUser, int wID, String wCode,
			int wUpFlowID, int wOrderID, int wPartID, int wClosePartID, Calendar wStartTime, Calendar wEndTime);

	ServiceResult<SFCLetPassBPM> SFC_UpdateLetPass(BMSEmployee wLoginUser, SFCLetPassBPM wData);

	ServiceResult<List<SFCLetPassBPM>> SFC_QueryLetPassBPMEmployeeAllWeb(BMSEmployee wLoginUser, int wOrderID,
			int wPartID, int wClosePartID, int wStatus, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询专检车辆列表
	 */
	ServiceResult<List<SFCTaskIPTPartNo>> SFC_QuerySpecialPartNoNew_V2(BMSEmployee wLoginUser);

	ServiceResult<List<SFCTaskIPTPart>> SFC_QuerySpecialPartListNew_V2(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 专检存档
	 */
	ServiceResult<Integer> SFC_SaveRecord(BMSEmployee wLoginUser, int wOrderID, List<Integer> wPartIDList);

	/**
	 * 专检拍照工序驳回
	 */
	ServiceResult<Integer> SFC_BackPic(BMSEmployee wLoginUser, SFCTaskIPT wTask);

	/**
	 * 更新检查员
	 */
	ServiceResult<Integer> SFC_UpdateChecker(BMSEmployee wLoginUser);

	ServiceResult<Integer> SFC_RejectStep(BMSEmployee wLoginUser, SFCTaskIPT wTask);

	/**
	 * 查询车辆转序情况第一层(车辆进度)
	 */
	ServiceResult<List<SFCTrainProgress01>> SFC_QueryTrainProgress01(BMSEmployee wLoginUser);

	/**
	 * 查询车辆转序情况第二层(工位进度)
	 */
	ServiceResult<List<SFCTrainProgress02>> SFC_QueryTrainProgress02(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 查询车辆转序情况第三层(工序进度)
	 */
	ServiceResult<List<SFCTrainProgress03>> SFC_QueryTrainProgress03(BMSEmployee wLoginUser, int wOrderID, int wPartID);

	ServiceResult<Integer> SFC_SetQuality(BMSEmployee wLoginUser, int wSFCTaskIPT, int wIPTItemID);

	ServiceResult<Integer> SFC_RepairBack(BMSEmployee wLoginUser, int wSFCTaskIPT, int wIPTItemID);
}
