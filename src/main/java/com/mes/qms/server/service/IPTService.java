package com.mes.qms.server.service;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.mes.qms.server.service.mesenum.APSOperateType;
import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.IPTMode;
import com.mes.qms.server.service.mesenum.IPTProblemActionType;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.imp.IMPResultRecord;
import com.mes.qms.server.service.po.ipt.IPTCheckRecord;
import com.mes.qms.server.service.po.ipt.IPTConfigs;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTItemApply;
import com.mes.qms.server.service.po.ipt.IPTOrderReport;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblemCar;
import com.mes.qms.server.service.po.ipt.IPTPreCheckReport;
import com.mes.qms.server.service.po.ipt.IPTProblemAssess;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.ipt.IPTStandard;
import com.mes.qms.server.service.po.ipt.IPTStandardBPM;
import com.mes.qms.server.service.po.ipt.IPTStandardC;
import com.mes.qms.server.service.po.ipt.IPTTool;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.record.IPTExportCheckRecord;
import com.mes.qms.server.service.po.sfc.SFCLoginEvent;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;

public interface IPTService {
	ServiceResult<String> IPT_SetIPTConfig(BMSEmployee wLoginUser, IPTConfigs wIPTConfigs);

	ServiceResult<IPTConfigs> IPT_GetIPTConfig(BMSEmployee wLoginUser);

	ServiceResult<IPTStandard> IPT_InsertStandard(BMSEmployee wLoginUser, IPTStandard wIPTStandard);

	ServiceResult<String> IPT_SaveStandard(BMSEmployee wLoginUser, IPTStandard wIPTStandard);

	ServiceResult<String> IPT_SaveStandardState(BMSEmployee wLoginUser, long wStandardID, int wIsCurrent);

	ServiceResult<String> IPT_DeleteStandard(BMSEmployee wLoginUser, long wStandardID);

	ServiceResult<IPTStandard> IPT_GetStandard(BMSEmployee wLoginUser, long wStandardID);

	ServiceResult<IPTStandard> IPT_GetStandardCurrent(BMSEmployee wLoginUser, int wCompanyID, IPTMode wIPTMode,
			int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID, int wLineID, int wPartID,
			int wPartPointID, int wStationID, String wProductNo);

	ServiceResult<IPTStandard> IPT_GetStandardCurrentByProductID(BMSEmployee wLoginUser, int wCompanyID,
			IPTMode wIPTMode, int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID,
			int wLineID, int wPartID, int wPartPointID, int wStationID, int wProductID);

	ServiceResult<List<IPTStandard>> IPT_GetStandardListByTime(BMSEmployee wLoginUser, int wCompanyID, IPTMode wIPTMode,
			int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID, int wLineID, int wPartID,
			int wPartPointID, int wStationID, int wProductID, String wProductNo, Calendar wTimeS, Calendar wTimeE);

	ServiceResult<List<IPTStandard>> IPT_SelectStandardList(BMSEmployee wLoginUser, int wCompanyID, IPTMode wIPTMode,
			int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID, int wLineID, int wPartID,
			int wPartPointID, int wStationID, int wProductID, String wProductNo);

	ServiceResult<List<IPTStandard>> IPT_GetStandardListCurrent(BMSEmployee wLoginUser, int wCompanyID,
			IPTMode wIPTMode, int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID,
			int wLineID, int wPartID, int wProductID, String wProductNo, int wNum);

	ServiceResult<String> IPT_SaveStandardEnd(BMSEmployee wLoginUser, long wStandardID, int wIsEnd);

	ServiceResult<Integer> IPT_SaveIPTValue(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList, int wTaskID,
			int wEventID);

	ServiceResult<Integer> IPT_BadResonAI(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList);

	ServiceResult<List<IPTValue>> IPT_GetIPTValueByTaskID(BMSEmployee wLoginUser, int wTaskID, int wEventID,
			int wItemType);

	ServiceResult<Map<Integer, List<IPTValue>>> IPT_GetIPTValue(BMSEmployee wLoginUser, List<Integer> wTaskIDList,
			int wEventID, int wItemType);

	// 巡检任务（质量巡检、工艺巡检）
	ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListByShiftID(BMSEmployee wLoginUser, int wCompanyID, int wLoginID,
			int wTaskType, int wShiftID);

	ServiceResult<SFCTaskIPT> SFC_QueryTaskIPTByID(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, int wID);

	ServiceResult<Integer> SFC_AddTaskIPT(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, SFCTaskIPT wTaskIPT);

	ServiceResult<Integer> SFC_SaveTaskIPT(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, SFCTaskIPT wTaskIPT);

	ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListByLoginID(BMSEmployee wLoginUser, int wCompanyID, int wLoginID,
			int wEventID, boolean wIncludeSub);

	ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListByStationID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wStationID, int wEventID);

	// 上班打卡
	ServiceResult<SFCLoginEvent> SFC_QueryActiveLoginEventByLoginID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wWorkShopID, int wStationID, int wAPPEventID);

	ServiceResult<List<SFCLoginEvent>> SFC_QueryLoginEventListByLoginID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wAPPEventID);

	ServiceResult<Integer> SFC_LoginByAPPEvent(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, int wWorkShopID,
			int wStationID, int wEventID);

	ServiceResult<Integer> SFC_LayoutByAPPEvent(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, int wWorkShopID,
			int wStationID, int wEventID);

	/**
	 * 导入标准
	 * 
	 * @param wExcelData Excel数据
	 * @return 标准ID
	 */
	ServiceResult<Integer> IPT_ImportStandard(BMSEmployee wLoginUser, int wIPTMode, ExcelData wExcelData,
			int wProductID, int wLineID, int wStationID, int wCustomerID, String wFileName);

	/**
	 * 预检-标准项申请
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPTID
	 * @param wYJApplyList
	 * @return
	 */
	ServiceResult<IPTItem> IPT_ItemApply(BMSEmployee wLoginUser, IPTItemApply wIPTItemApply);

	/**
	 * 根据主键查询预检问题项
	 * 
	 * @param wLoginUser
	 * @param wID
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<IPTPreCheckProblem> IPT_QueryPreCheckProblem(BMSEmployee wLoginUser, int wID);

	/**
	 * 根据人员和标签查询预检问题项集合
	 * 
	 * @param wLoginUser
	 * @param wTagType
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<List<IPTPreCheckProblem>> IPT_QueryPreCheckProblemListByTime(BMSEmployee wLoginUser, int wCraftID,
			int wDoClassID, int wClassIssueID, int wManager, Calendar wStartTime, Calendar wEndTime,
			List<Integer> wStateList);

	/**
	 * 新增或修改预检问题项
	 * 
	 * @param wLoginUser
	 * @param wIPTPreCheckProblem
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<Integer> IPT_SavePreCheckProblem(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem);

	/**
	 * 保存解决方案(指导书)
	 * 
	 * @param wLoginUser
	 * @param wIPTItem
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<Integer> IPT_SaveSOPList(BMSEmployee wLoginUser, IPTItem wIPTItem);

	/**
	 * 获取检验项和检验值
	 * 
	 * @param wLoginUser    登陆者
	 * @param wSFCTaskIPTID 巡检任务ID
	 * @param wValueList    检验值集合
	 * @param wErrorCode    错误码
	 * @return
	 */
	ServiceResult<List<IPTItem>> IPT_QueryIPTItemList(BMSEmployee wLoginUser, int wSFCTaskIPTID);

	/**
	 * 触发巡检任务
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPTID
	 * @param wIPTValueList
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<Integer> IPT_TriggerTask(BMSEmployee wLoginUser, int wSFCTaskIPTID, List<IPTValue> wValueList);

	/**
	 * 根据巡检任务ID获取预检问题项集合
	 * 
	 * @param wLoginUser    登陆者
	 * @param wSFCTaskIPTID 巡检任务ID
	 * @param wErrorCode    错误码
	 * @return 预检问题项集合
	 */
	ServiceResult<List<IPTPreCheckProblem>> IPT_QueryProblemList(BMSEmployee wLoginUser, int wOrderID, int wProductID,
			int wLineID, int wStationID, int wStepID, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 触发预检问题项
	 * 
	 * @param wLoginUser    登录人
	 * @param wSFCTaskIPTID 预检任务ID
	 * @param wIPTValueList 预检值集合
	 * @return
	 */
	ServiceResult<Integer> IPT_TriggerYJProblem(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList);

	/**
	 * 问题项给解决方案
	 * 
	 * @param wLoginUser
	 * @param wIPTPreCheckProblem
	 * @param wIPTSOPList
	 * @return
	 */
	ServiceResult<Integer> IPT_GiveSOPList(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem,
			List<IPTSOP> wIPTSOPList);

	/**
	 * 保存质量三检提交的问题项
	 * 
	 * @param wLoginUser
	 * @param wProblemList
	 * @return
	 */
	ServiceResult<Integer> IPT_SaveProblemList(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList,
			SFCTaskIPT wSFCTaskIPT);

	/**
	 * 根据成型、修程、工位查询工序的标准情况(灰色、红色、绿色)
	 * 
	 * @param wLoginUser
	 * @param wProductID
	 * @param wLineID
	 * @param wPartID
	 * @return
	 */
	ServiceResult<Map<Integer, Integer>> IPT_QueryPointTree(BMSEmployee wLoginUser, Integer wProductID, int wLineID,
			int wPartID);

	/**
	 * 根据修程、工位查询车薪的标准情况(灰色、红色、绿色)
	 * 
	 * @param wLoginUser
	 * @param wLineID
	 * @param wPartID
	 * @return
	 */
	ServiceResult<Map<Integer, Integer>> IPT_QueryProductTree(BMSEmployee wLoginUser, int wLineID, int wPartID);

	/**
	 * 将扁平化的项转化为树形的项
	 * 
	 * @param wLoginUser
	 * @param wList
	 * @return
	 */
	ServiceResult<List<IPTItem>> IPT_QueryIPTItemTree(BMSEmployee wLoginUser, List<IPTItem> wList);

	/**
	 * 导入工序清单
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	ServiceResult<Integer> IPT_ImportPartPoint(BMSEmployee wLoginUser, ExcelData wExcelData);

	/**
	 * 导入标准BOM
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	ServiceResult<String> IPT_ImportBOM(BMSEmployee wLoginUser, ExcelData wExcelData, String wFileName);

	/**
	 * 导出BOMItem
	 * 
	 * @param wLoginUser
	 * @param wItemList
	 * @param response
	 * @return
	 */
	ServiceResult<Integer> IPT_ExportBOM(BMSEmployee wLoginUser, List<MSSBOMItem> wItemList,
			HttpServletResponse response);

	/**
	 * 导出预检报告
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @param response
	 * @return
	 */
	ServiceResult<Integer> ExportYJReport(BMSEmployee wLoginUser, Integer wOrderID, HttpServletResponse response);

	/**
	 * 人员查询评审单任务
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<IPTProblemAssess>> IPT_QueryProblemAssessListByEmployee(BMSEmployee wLoginUser);

	/**
	 * 审批问题项评审任务
	 * 
	 * @param wLoginUser
	 * @param wIPTProblemAssess
	 * @return
	 */
	ServiceResult<Integer> IPT_AuditProblemAssess(BMSEmployee wLoginUser, IPTProblemAssess wIPTProblemAssess);

	/**
	 * 问题项发起评审
	 * 
	 * @param wLoginUser
	 * @param wIPTPreCheckProblem
	 * @return
	 */
	ServiceResult<Integer> IPT_ProblemSendAudit(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem);

	/**
	 * 处理预检问题项
	 * 
	 * @param wLoginUser
	 * @param wActionType
	 * @param wList
	 * @return
	 */
	ServiceResult<Integer> IPT_HandlePreCheckProblem(BMSEmployee wLoginUser, IPTProblemActionType wActionType,
			List<IPTPreCheckProblem> wList, String wRemark);

	/**
	 * 生成预检报告
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @param wPartNo
	 * @return
	 */
	ServiceResult<IPTPreCheckReport> IPT_CreatePreCheckReport(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 查询预检报告审批任务
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<IPTPreCheckReport>> IPT_QueryReportAuditList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	/**
	 * 审批预检报告
	 * 
	 * @param wLoginUser
	 * @param wList
	 * @param wOperateType
	 * @return
	 */
	ServiceResult<Integer> IPT_AuditPreCheckReport(BMSEmployee wLoginUser, List<IPTPreCheckReport> wList,
			APSOperateType wOperateType);

	/**
	 * 导出预检报告
	 * 
	 * @param wLoginUser
	 * @param wIPTPreCheckReport
	 * @param response
	 * @return
	 */
	ServiceResult<String> ExportPreCheckReport(BMSEmployee wLoginUser, IPTPreCheckReport wIPTPreCheckReport);

	/**
	 * 生成台车BOM
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<List<APSBOMItem>> IPT_CreateAPSBOMItemList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 导出台车BOM
	 * 
	 * @param wLoginUser
	 * @param wItemList
	 * @param response
	 * @return
	 */
	ServiceResult<String> IPT_ExportAPSBOM(BMSEmployee wLoginUser, List<APSBOMItem> wItemList);

	/**
	 * ID查询评审任务
	 * 
	 * @param wLoginUser
	 * @param wID
	 * @return
	 */
	ServiceResult<IPTProblemAssess> IPT_QueryProblemAssessByID(BMSEmployee wLoginUser, int wID);

	/**
	 * 人员查询预检问题项集合
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<IPTPreCheckProblem>> IPT_QueryPreCheckProblemByEmployee(BMSEmployee wLoginUser);

	/**
	 * 班组长查询派工的问题项集合(作为派工的数据源)
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<IPTPreCheckProblem>> IPT_QueryPGProblemList(BMSEmployee wLoginUser);

	/**
	 * 保存问题项的派工人员
	 * 
	 * @param wLoginUser 登录信息
	 * @param wPersonID  选择的人员
	 * @param wData      问题项
	 * @return
	 */
	ServiceResult<Integer> IPT_SavePGPerson(BMSEmployee wLoginUser, int wPersonID, IPTPreCheckProblem wData);

	/**
	 * 根据修程、车型、工位查询工序版本
	 * 
	 * @param wLoginUser 登录者
	 * @param wLineID    修程
	 * @param wProductID 车型
	 * @param wPartID    工位
	 * @return 工序版本
	 */
	ServiceResult<List<IPTStandard>> IPT_QueryStepVersionAll(BMSEmployee wLoginUser, int wLineID, int wProductID,
			int wPartID, int wCustomID);

	/**
	 * 查单条
	 * 
	 * @param wLoginUser
	 * @param wReportID
	 * @return
	 */
	ServiceResult<IPTPreCheckReport> IPT_QueryPreCheckReportByID(BMSEmployee wLoginUser, int wReportID);

	/**
	 * 设置是否为质量项点
	 * 
	 * @param wLoginUser
	 * @param wIPTItemList
	 * @param wIsQuality
	 * @return
	 */
	ServiceResult<Integer> IPT_SetIsQuality(BMSEmployee wLoginUser, List<IPTItem> wIPTItemList, int wIsQuality);

	/**
	 * 设置项点部件编码
	 * 
	 * @param wLoginUser
	 * @param wIPTItem
	 * @param wPartCoding
	 * @return
	 */
	ServiceResult<Integer> IPT_SetPartCoding(BMSEmployee wLoginUser, IPTItem wIPTItem, String wPartCoding,
			int wConfigID);

	/**
	 * 设置项点检验顺序
	 * 
	 * @param wLoginUser
	 * @param wIPTItem
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<Integer> IPT_SetItemOrder(BMSEmployee wLoginUser, IPTItem wIPTItem, int wOrderID);

	/**
	 * 条件查询预检报告
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<List<IPTPreCheckReport>> IPT_QueryPreCheckReportByOrder(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<IPTOrderReport> IPT_QueryOrderInfo(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<List<SFCTaskIPT>> IPT_QueryIPTListByReportPartPointID(BMSEmployee wLoginUser, int wID);

	ServiceResult<List<IPTItem>> IPT_QueryPeriodChangeAll(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<List<APSTaskStep>> IPT_QueryRecordAll(BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime,
			int wOrderID, int wStationID, int wStepID, int wSubmitID);

	ServiceResult<List<IPTCheckRecord>> IPT_QueryPreCheckRecord(BMSEmployee wLoginUser, int wOrderID,
			Calendar wStartTime, Calendar wEndTime, int wStepID, int wRecordType);

	ServiceResult<List<IPTCheckRecord>> IPT_QueryProcessRecord(BMSEmployee wLoginUser, int wOrderID,
			Calendar wStartTime, Calendar wEndTime, int wStationID, int wStepID, int wRecordType, int wSubmitID,
			boolean wIsQuality);

	ServiceResult<List<IPTCheckRecord>> IPT_QueryOutCheckRecord(BMSEmployee wLoginUser, int wOrderID,
			Calendar wStartTime, Calendar wEndTime, int wPartID, int wStepID, int wSubmitID);

	ServiceResult<String> ExportPreCheckReportByOrder(final BMSEmployee wLoginUser, final int wOrderID);

	/**
	 * 添加预检问题项(段该项目)
	 * 
	 * @param wLoginUser        登录信息
	 * @param wWBSNo            WBS号
	 * @param wPartNo           车号
	 * @param wPeriodChangeItem 段改项目名称
	 * @param wRequirements     段方要求
	 * @param wOrderNo          订单号
	 * @return 新增的问题项主键
	 */
	ServiceResult<Integer> IPT_AddProblems(BMSEmployee wLoginUser, String wWBSNo, String wPartNo,
			String wPeriodChangeItem, String wRequirements, String wOrderNo);

	/**
	 * 通过局段和标准获取标准项
	 * 
	 * @param wLoginUser  登录信息
	 * @param wStandardID 标准ID
	 * @param wCustomerID 局段ID
	 * @return 标准项列表
	 */
	ServiceResult<List<IPTItem>> QueryItemListByCustomer(BMSEmployee wLoginUser, int wStandardID, int wCustomerID);

	/**
	 * 导出生产过程检验报告
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<String> ExportProduceProcess(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 导出质量过程检验报告
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<String> ExportQualityProcess(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 导出终检报告
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<String> ExportFinalCheck(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 导出出厂检报告
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<String> ExportOutCheck(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 导入问题项
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	ServiceResult<Integer> IPT_ImportProblems(BMSEmployee wLoginUser, ExcelData wExcelData);

	/**
	 * 获取导入记录对比结果
	 * 
	 * @param wLoginUser
	 * @param wARecordID
	 * @param wBRecordID
	 * @return
	 */
	ServiceResult<List<IPTStandardC>> IPT_Compare(BMSEmployee wLoginUser, int wARecordID, int wBRecordID);

	/**
	 * 通过标准ID获取项ID集合
	 * 
	 * @param wLoginUser
	 * @param wStandardID
	 * @return
	 */
	ServiceResult<List<IPTItem>> QueryItemListByStandard(BMSEmployee wLoginUser, int wStandardID);

	/**
	 * 导入物料
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @param wOriginalFileName
	 * @return
	 */
	ServiceResult<String> IPT_ImportMaterial(BMSEmployee wLoginUser, ExcelData wExcelData, String wOriginalFileName);

	/**
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<IMPResultRecord> IPT_QueryMaterialProgress(BMSEmployee wLoginUser, int wImportType, Calendar wTime);

	/**
	 * 导出过程检验报告zip
	 */
	ServiceResult<String> ExportProduceProcessZip(BMSEmployee wLoginUser, int wOrderID, int wPartID);

	ServiceResult<String> ExportOutCheckZip(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<String> ExportFinalCheckZip(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<String> ExportQualityProcessZip(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<String> ExportPreCheckReportByOrderZip(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 查询默认状态的单据
	 */
	ServiceResult<IPTPreCheckReport> IPT_QueryDefaultPreCheckReport(BMSEmployee wLoginUser, int wEventID, int wOrderID);

	/**
	 * 创建单据
	 */
	ServiceResult<IPTPreCheckReport> IPT_CreatePreCheckReport(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * 提交单据
	 */
	ServiceResult<IPTPreCheckReport> IPT_SubmitPreCheckReport(BMSEmployee wLoginUser, IPTPreCheckReport wData);

	/**
	 * 查询单条单据
	 */
	ServiceResult<IPTPreCheckReport> IPT_GetPreCheckReport(BMSEmployee wLoginUser, int wID);

	/**
	 * 用人员拿任务
	 */
	ServiceResult<List<BPMTaskBase>> IPT_QueryPreCheckReportEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询单据历史
	 */
	ServiceResult<List<IPTPreCheckReport>> IPT_QueryPreCheckReportHistory(BMSEmployee wLoginUser, int wID, String wCode,
			int wUpFlowID, int wOrderID, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 获取车分层的问题项
	 */
	ServiceResult<List<IPTPreCheckProblemCar>> IPT_QueryEmployeeAllCar(BMSEmployee wLoginUser);

	/**
	 * 根据订单获取问题项-车分层
	 */
	ServiceResult<List<IPTPreCheckProblem>> IPT_QueryEmployeeAllItemList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * 跳过评审
	 */
	ServiceResult<Integer> IPT_SkipAudit(BMSEmployee wLoginUser, IPTPreCheckProblem wData);

	/**
	 * 查询导出校验记录
	 */
	ServiceResult<List<com.mes.qms.server.service.po.record.IPTExportCheckRecord>> IPT_QueryCheckRecordList(
			BMSEmployee wLoginUser, int wID, String wCode, int wOperateID, int wOrderID, Calendar wStartTime,
			Calendar wEndTime);

	/**
	 * 导出校验
	 */
	ServiceResult<IPTExportCheckRecord> IPT_ExportVerificate(BMSEmployee wLoginUser, int wOrderID, String wCode);

	/**
	 * 获取校验进度
	 */
	ServiceResult<IPTExportCheckRecord> IPT_VerificateProgress(BMSEmployee wLoginUser, String wCode);

	/**
	 * 添加备注
	 */
	ServiceResult<Integer> AddRemark(BMSEmployee wLoginUser, IPTValue wData);

	/**
	 * 查单条
	 * 
	 * @return
	 */
	ServiceResult<IPTTool> IPT_QueryTool(BMSEmployee wLoginUser, int wID);

	/**
	 * 条件查询
	 * 
	 * @return
	 */
	ServiceResult<List<IPTTool>> IPT_QueryToolList(BMSEmployee wLoginUser, int wID, int wStandardID);

	/**
	 * 更新或修改
	 * 
	 * @return
	 */
	ServiceResult<Integer> IPT_UpdateTool(BMSEmployee wLoginUser, IPTTool wIPTTool);

	/**
	 * 查询默认状态的单据
	 */
	ServiceResult<IPTStandardBPM> IPT_QueryDefaultStandardBPM(BMSEmployee wLoginUser, int wEventID);

	/**
	 * 创建单据
	 */
	ServiceResult<IPTStandardBPM> IPT_CreateStandardBPM(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * 提交单据
	 */
	ServiceResult<IPTStandardBPM> IPT_SubmitStandardBPM(BMSEmployee wLoginUser, IPTStandardBPM wData);

	/**
	 * 查询单条单据
	 */
	ServiceResult<IPTStandardBPM> IPT_GetStandardBPM(BMSEmployee wLoginUser, int wID);

	/**
	 * 用人员拿任务
	 */
	ServiceResult<List<BPMTaskBase>> IPT_QueryStandardBPMEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询单据历史
	 */
	ServiceResult<List<IPTStandardBPM>> IPT_QueryStandardBPMHistory(BMSEmployee wLoginUser, int wID, String wCode,
			int wUpFlowID, int wStandardID, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 删除项点批量
	 */
	ServiceResult<Integer> IPT_DeletItems(BMSEmployee wLoginUser, List<IPTItem> wItemList);

	/**
	 * 批量删除工具
	 */
	ServiceResult<Integer> IPT_DeleteTooList(BMSEmployee wLoginUser, List<IPTTool> wList);

	/**
	 * 复制标准
	 */
	ServiceResult<Integer> IPT_CopyStandard(BMSEmployee wLoginUser, int wStandardID, OMSOrder wOMSOrder);

	/**
	 * 新版过程控制记录导入
	 */
	ServiceResult<Integer> IPT_ImportStandardNew(BMSEmployee wLoginUser, int wIPTMode, int wProductID, int wLineID,
			int wCustomerID, int wStationID, ExcelData wResult, String wOriginalFileName);

	/**
	 * 编辑标准
	 */
	ServiceResult<Integer> IPT_EditStandard(BMSEmployee wLoginUser, IPTStandard wData);

	/**
	 * 查询标准审批单
	 */
	ServiceResult<List<IPTStandardBPM>> IPT_QueryStandardBPMEmployeeAllWeb(BMSEmployee wLoginUser, Integer wProductID,
			Integer wLineID, Integer wPartID, Integer wStatus, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 批量多条件复制标准(包含子项和工量具)
	 */
	ServiceResult<Integer> IPT_BatchCopyStandard(BMSEmployee wLoginUser, int wProductID, int wLineID, int wPartID1,
			int wPartPoint1, int wPartID2, int wPartPoint2, boolean wNotCurrent);

	/**
	 * 导入有问题的订单，需要修改WBS和实际进厂时间，实际完工时间
	 */
	ServiceResult<Integer> IPT_ImportOrder(BMSEmployee wLoginUser, ExcelData result);

	/**
	 * 获取物料重复的SQL语句
	 */
	ServiceResult<String> IPT_GetMaterialSQL(BMSEmployee wLoginUser, ExcelData result);

	ServiceResult<List<IPTStandard>> IPT_QueryStepVersionAllPro(BMSEmployee wLoginUser, int wLineID, int wProductID,
			int wPartID, String wStepName);
}
