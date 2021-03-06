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

	// ?????????????????????????????????????????????
	ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListByShiftID(BMSEmployee wLoginUser, int wCompanyID, int wLoginID,
			int wTaskType, int wShiftID);

	ServiceResult<SFCTaskIPT> SFC_QueryTaskIPTByID(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, int wID);

	ServiceResult<Integer> SFC_AddTaskIPT(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, SFCTaskIPT wTaskIPT);

	ServiceResult<Integer> SFC_SaveTaskIPT(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, SFCTaskIPT wTaskIPT);

	ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListByLoginID(BMSEmployee wLoginUser, int wCompanyID, int wLoginID,
			int wEventID, boolean wIncludeSub);

	ServiceResult<List<SFCTaskIPT>> SFC_QueryTaskIPTListByStationID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wStationID, int wEventID);

	// ????????????
	ServiceResult<SFCLoginEvent> SFC_QueryActiveLoginEventByLoginID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wWorkShopID, int wStationID, int wAPPEventID);

	ServiceResult<List<SFCLoginEvent>> SFC_QueryLoginEventListByLoginID(BMSEmployee wLoginUser, int wCompanyID,
			int wLoginID, int wAPPEventID);

	ServiceResult<Integer> SFC_LoginByAPPEvent(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, int wWorkShopID,
			int wStationID, int wEventID);

	ServiceResult<Integer> SFC_LayoutByAPPEvent(BMSEmployee wLoginUser, int wCompanyID, int wLoginID, int wWorkShopID,
			int wStationID, int wEventID);

	/**
	 * ????????????
	 * 
	 * @param wExcelData Excel??????
	 * @return ??????ID
	 */
	ServiceResult<Integer> IPT_ImportStandard(BMSEmployee wLoginUser, int wIPTMode, ExcelData wExcelData,
			int wProductID, int wLineID, int wStationID, int wCustomerID, String wFileName);

	/**
	 * ??????-???????????????
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPTID
	 * @param wYJApplyList
	 * @return
	 */
	ServiceResult<IPTItem> IPT_ItemApply(BMSEmployee wLoginUser, IPTItemApply wIPTItemApply);

	/**
	 * ?????????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wID
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<IPTPreCheckProblem> IPT_QueryPreCheckProblem(BMSEmployee wLoginUser, int wID);

	/**
	 * ????????????????????????????????????????????????
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
	 * ??????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wIPTPreCheckProblem
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<Integer> IPT_SavePreCheckProblem(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem);

	/**
	 * ??????????????????(?????????)
	 * 
	 * @param wLoginUser
	 * @param wIPTItem
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<Integer> IPT_SaveSOPList(BMSEmployee wLoginUser, IPTItem wIPTItem);

	/**
	 * ???????????????????????????
	 * 
	 * @param wLoginUser    ?????????
	 * @param wSFCTaskIPTID ????????????ID
	 * @param wValueList    ???????????????
	 * @param wErrorCode    ?????????
	 * @return
	 */
	ServiceResult<List<IPTItem>> IPT_QueryIPTItemList(BMSEmployee wLoginUser, int wSFCTaskIPTID);

	/**
	 * ??????????????????
	 * 
	 * @param wLoginUser
	 * @param wSFCTaskIPTID
	 * @param wIPTValueList
	 * @param wErrorCode
	 * @return
	 */
	ServiceResult<Integer> IPT_TriggerTask(BMSEmployee wLoginUser, int wSFCTaskIPTID, List<IPTValue> wValueList);

	/**
	 * ??????????????????ID???????????????????????????
	 * 
	 * @param wLoginUser    ?????????
	 * @param wSFCTaskIPTID ????????????ID
	 * @param wErrorCode    ?????????
	 * @return ?????????????????????
	 */
	ServiceResult<List<IPTPreCheckProblem>> IPT_QueryProblemList(BMSEmployee wLoginUser, int wOrderID, int wProductID,
			int wLineID, int wStationID, int wStepID, Calendar wStartTime, Calendar wEndTime);

	/**
	 * ?????????????????????
	 * 
	 * @param wLoginUser    ?????????
	 * @param wSFCTaskIPTID ????????????ID
	 * @param wIPTValueList ???????????????
	 * @return
	 */
	ServiceResult<Integer> IPT_TriggerYJProblem(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			List<IPTValue> wIPTValueList);

	/**
	 * ????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wIPTPreCheckProblem
	 * @param wIPTSOPList
	 * @return
	 */
	ServiceResult<Integer> IPT_GiveSOPList(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem,
			List<IPTSOP> wIPTSOPList);

	/**
	 * ????????????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wProblemList
	 * @return
	 */
	ServiceResult<Integer> IPT_SaveProblemList(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList,
			SFCTaskIPT wSFCTaskIPT);

	/**
	 * ?????????????????????????????????????????????????????????(????????????????????????)
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
	 * ????????????????????????????????????????????????(????????????????????????)
	 * 
	 * @param wLoginUser
	 * @param wLineID
	 * @param wPartID
	 * @return
	 */
	ServiceResult<Map<Integer, Integer>> IPT_QueryProductTree(BMSEmployee wLoginUser, int wLineID, int wPartID);

	/**
	 * ???????????????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wList
	 * @return
	 */
	ServiceResult<List<IPTItem>> IPT_QueryIPTItemTree(BMSEmployee wLoginUser, List<IPTItem> wList);

	/**
	 * ??????????????????
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	ServiceResult<Integer> IPT_ImportPartPoint(BMSEmployee wLoginUser, ExcelData wExcelData);

	/**
	 * ????????????BOM
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	ServiceResult<String> IPT_ImportBOM(BMSEmployee wLoginUser, ExcelData wExcelData, String wFileName);

	/**
	 * ??????BOMItem
	 * 
	 * @param wLoginUser
	 * @param wItemList
	 * @param response
	 * @return
	 */
	ServiceResult<Integer> IPT_ExportBOM(BMSEmployee wLoginUser, List<MSSBOMItem> wItemList,
			HttpServletResponse response);

	/**
	 * ??????????????????
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @param response
	 * @return
	 */
	ServiceResult<Integer> ExportYJReport(BMSEmployee wLoginUser, Integer wOrderID, HttpServletResponse response);

	/**
	 * ???????????????????????????
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<IPTProblemAssess>> IPT_QueryProblemAssessListByEmployee(BMSEmployee wLoginUser);

	/**
	 * ???????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wIPTProblemAssess
	 * @return
	 */
	ServiceResult<Integer> IPT_AuditProblemAssess(BMSEmployee wLoginUser, IPTProblemAssess wIPTProblemAssess);

	/**
	 * ?????????????????????
	 * 
	 * @param wLoginUser
	 * @param wIPTPreCheckProblem
	 * @return
	 */
	ServiceResult<Integer> IPT_ProblemSendAudit(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem);

	/**
	 * ?????????????????????
	 * 
	 * @param wLoginUser
	 * @param wActionType
	 * @param wList
	 * @return
	 */
	ServiceResult<Integer> IPT_HandlePreCheckProblem(BMSEmployee wLoginUser, IPTProblemActionType wActionType,
			List<IPTPreCheckProblem> wList, String wRemark);

	/**
	 * ??????????????????
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @param wPartNo
	 * @return
	 */
	ServiceResult<IPTPreCheckReport> IPT_CreatePreCheckReport(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * ??????????????????????????????
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<IPTPreCheckReport>> IPT_QueryReportAuditList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);

	/**
	 * ??????????????????
	 * 
	 * @param wLoginUser
	 * @param wList
	 * @param wOperateType
	 * @return
	 */
	ServiceResult<Integer> IPT_AuditPreCheckReport(BMSEmployee wLoginUser, List<IPTPreCheckReport> wList,
			APSOperateType wOperateType);

	/**
	 * ??????????????????
	 * 
	 * @param wLoginUser
	 * @param wIPTPreCheckReport
	 * @param response
	 * @return
	 */
	ServiceResult<String> ExportPreCheckReport(BMSEmployee wLoginUser, IPTPreCheckReport wIPTPreCheckReport);

	/**
	 * ????????????BOM
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<List<APSBOMItem>> IPT_CreateAPSBOMItemList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * ????????????BOM
	 * 
	 * @param wLoginUser
	 * @param wItemList
	 * @param response
	 * @return
	 */
	ServiceResult<String> IPT_ExportAPSBOM(BMSEmployee wLoginUser, List<APSBOMItem> wItemList);

	/**
	 * ID??????????????????
	 * 
	 * @param wLoginUser
	 * @param wID
	 * @return
	 */
	ServiceResult<IPTProblemAssess> IPT_QueryProblemAssessByID(BMSEmployee wLoginUser, int wID);

	/**
	 * ?????????????????????????????????
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<IPTPreCheckProblem>> IPT_QueryPreCheckProblemByEmployee(BMSEmployee wLoginUser);

	/**
	 * ???????????????????????????????????????(????????????????????????)
	 * 
	 * @param wLoginUser
	 * @return
	 */
	ServiceResult<List<IPTPreCheckProblem>> IPT_QueryPGProblemList(BMSEmployee wLoginUser);

	/**
	 * ??????????????????????????????
	 * 
	 * @param wLoginUser ????????????
	 * @param wPersonID  ???????????????
	 * @param wData      ?????????
	 * @return
	 */
	ServiceResult<Integer> IPT_SavePGPerson(BMSEmployee wLoginUser, int wPersonID, IPTPreCheckProblem wData);

	/**
	 * ????????????????????????????????????????????????
	 * 
	 * @param wLoginUser ?????????
	 * @param wLineID    ??????
	 * @param wProductID ??????
	 * @param wPartID    ??????
	 * @return ????????????
	 */
	ServiceResult<List<IPTStandard>> IPT_QueryStepVersionAll(BMSEmployee wLoginUser, int wLineID, int wProductID,
			int wPartID, int wCustomID);

	/**
	 * ?????????
	 * 
	 * @param wLoginUser
	 * @param wReportID
	 * @return
	 */
	ServiceResult<IPTPreCheckReport> IPT_QueryPreCheckReportByID(BMSEmployee wLoginUser, int wReportID);

	/**
	 * ???????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wIPTItemList
	 * @param wIsQuality
	 * @return
	 */
	ServiceResult<Integer> IPT_SetIsQuality(BMSEmployee wLoginUser, List<IPTItem> wIPTItemList, int wIsQuality);

	/**
	 * ????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wIPTItem
	 * @param wPartCoding
	 * @return
	 */
	ServiceResult<Integer> IPT_SetPartCoding(BMSEmployee wLoginUser, IPTItem wIPTItem, String wPartCoding,
			int wConfigID);

	/**
	 * ????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wIPTItem
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<Integer> IPT_SetItemOrder(BMSEmployee wLoginUser, IPTItem wIPTItem, int wOrderID);

	/**
	 * ????????????????????????
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
	 * ?????????????????????(????????????)
	 * 
	 * @param wLoginUser        ????????????
	 * @param wWBSNo            WBS???
	 * @param wPartNo           ??????
	 * @param wPeriodChangeItem ??????????????????
	 * @param wRequirements     ????????????
	 * @param wOrderNo          ?????????
	 * @return ????????????????????????
	 */
	ServiceResult<Integer> IPT_AddProblems(BMSEmployee wLoginUser, String wWBSNo, String wPartNo,
			String wPeriodChangeItem, String wRequirements, String wOrderNo);

	/**
	 * ????????????????????????????????????
	 * 
	 * @param wLoginUser  ????????????
	 * @param wStandardID ??????ID
	 * @param wCustomerID ??????ID
	 * @return ???????????????
	 */
	ServiceResult<List<IPTItem>> QueryItemListByCustomer(BMSEmployee wLoginUser, int wStandardID, int wCustomerID);

	/**
	 * ??????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<String> ExportProduceProcess(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * ??????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<String> ExportQualityProcess(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * ??????????????????
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<String> ExportFinalCheck(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * ?????????????????????
	 * 
	 * @param wLoginUser
	 * @param wOrderID
	 * @return
	 */
	ServiceResult<String> ExportOutCheck(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * ???????????????
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	ServiceResult<Integer> IPT_ImportProblems(BMSEmployee wLoginUser, ExcelData wExcelData);

	/**
	 * ??????????????????????????????
	 * 
	 * @param wLoginUser
	 * @param wARecordID
	 * @param wBRecordID
	 * @return
	 */
	ServiceResult<List<IPTStandardC>> IPT_Compare(BMSEmployee wLoginUser, int wARecordID, int wBRecordID);

	/**
	 * ????????????ID?????????ID??????
	 * 
	 * @param wLoginUser
	 * @param wStandardID
	 * @return
	 */
	ServiceResult<List<IPTItem>> QueryItemListByStandard(BMSEmployee wLoginUser, int wStandardID);

	/**
	 * ????????????
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
	 * ????????????????????????zip
	 */
	ServiceResult<String> ExportProduceProcessZip(BMSEmployee wLoginUser, int wOrderID, int wPartID);

	ServiceResult<String> ExportOutCheckZip(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<String> ExportFinalCheckZip(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<String> ExportQualityProcessZip(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<String> ExportPreCheckReportByOrderZip(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * ???????????????????????????
	 */
	ServiceResult<IPTPreCheckReport> IPT_QueryDefaultPreCheckReport(BMSEmployee wLoginUser, int wEventID, int wOrderID);

	/**
	 * ????????????
	 */
	ServiceResult<IPTPreCheckReport> IPT_CreatePreCheckReport(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * ????????????
	 */
	ServiceResult<IPTPreCheckReport> IPT_SubmitPreCheckReport(BMSEmployee wLoginUser, IPTPreCheckReport wData);

	/**
	 * ??????????????????
	 */
	ServiceResult<IPTPreCheckReport> IPT_GetPreCheckReport(BMSEmployee wLoginUser, int wID);

	/**
	 * ??????????????????
	 */
	ServiceResult<List<BPMTaskBase>> IPT_QueryPreCheckReportEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * ??????????????????
	 */
	ServiceResult<List<IPTPreCheckReport>> IPT_QueryPreCheckReportHistory(BMSEmployee wLoginUser, int wID, String wCode,
			int wUpFlowID, int wOrderID, Calendar wStartTime, Calendar wEndTime);

	/**
	 * ???????????????????????????
	 */
	ServiceResult<List<IPTPreCheckProblemCar>> IPT_QueryEmployeeAllCar(BMSEmployee wLoginUser);

	/**
	 * ???????????????????????????-?????????
	 */
	ServiceResult<List<IPTPreCheckProblem>> IPT_QueryEmployeeAllItemList(BMSEmployee wLoginUser, int wOrderID);

	/**
	 * ????????????
	 */
	ServiceResult<Integer> IPT_SkipAudit(BMSEmployee wLoginUser, IPTPreCheckProblem wData);

	/**
	 * ????????????????????????
	 */
	ServiceResult<List<com.mes.qms.server.service.po.record.IPTExportCheckRecord>> IPT_QueryCheckRecordList(
			BMSEmployee wLoginUser, int wID, String wCode, int wOperateID, int wOrderID, Calendar wStartTime,
			Calendar wEndTime);

	/**
	 * ????????????
	 */
	ServiceResult<IPTExportCheckRecord> IPT_ExportVerificate(BMSEmployee wLoginUser, int wOrderID, String wCode);

	/**
	 * ??????????????????
	 */
	ServiceResult<IPTExportCheckRecord> IPT_VerificateProgress(BMSEmployee wLoginUser, String wCode);

	/**
	 * ????????????
	 */
	ServiceResult<Integer> AddRemark(BMSEmployee wLoginUser, IPTValue wData);

	/**
	 * ?????????
	 * 
	 * @return
	 */
	ServiceResult<IPTTool> IPT_QueryTool(BMSEmployee wLoginUser, int wID);

	/**
	 * ????????????
	 * 
	 * @return
	 */
	ServiceResult<List<IPTTool>> IPT_QueryToolList(BMSEmployee wLoginUser, int wID, int wStandardID);

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	ServiceResult<Integer> IPT_UpdateTool(BMSEmployee wLoginUser, IPTTool wIPTTool);

	/**
	 * ???????????????????????????
	 */
	ServiceResult<IPTStandardBPM> IPT_QueryDefaultStandardBPM(BMSEmployee wLoginUser, int wEventID);

	/**
	 * ????????????
	 */
	ServiceResult<IPTStandardBPM> IPT_CreateStandardBPM(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * ????????????
	 */
	ServiceResult<IPTStandardBPM> IPT_SubmitStandardBPM(BMSEmployee wLoginUser, IPTStandardBPM wData);

	/**
	 * ??????????????????
	 */
	ServiceResult<IPTStandardBPM> IPT_GetStandardBPM(BMSEmployee wLoginUser, int wID);

	/**
	 * ??????????????????
	 */
	ServiceResult<List<BPMTaskBase>> IPT_QueryStandardBPMEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * ??????????????????
	 */
	ServiceResult<List<IPTStandardBPM>> IPT_QueryStandardBPMHistory(BMSEmployee wLoginUser, int wID, String wCode,
			int wUpFlowID, int wStandardID, Calendar wStartTime, Calendar wEndTime);

	/**
	 * ??????????????????
	 */
	ServiceResult<Integer> IPT_DeletItems(BMSEmployee wLoginUser, List<IPTItem> wItemList);

	/**
	 * ??????????????????
	 */
	ServiceResult<Integer> IPT_DeleteTooList(BMSEmployee wLoginUser, List<IPTTool> wList);

	/**
	 * ????????????
	 */
	ServiceResult<Integer> IPT_CopyStandard(BMSEmployee wLoginUser, int wStandardID, OMSOrder wOMSOrder);

	/**
	 * ??????????????????????????????
	 */
	ServiceResult<Integer> IPT_ImportStandardNew(BMSEmployee wLoginUser, int wIPTMode, int wProductID, int wLineID,
			int wCustomerID, int wStationID, ExcelData wResult, String wOriginalFileName);

	/**
	 * ????????????
	 */
	ServiceResult<Integer> IPT_EditStandard(BMSEmployee wLoginUser, IPTStandard wData);

	/**
	 * ?????????????????????
	 */
	ServiceResult<List<IPTStandardBPM>> IPT_QueryStandardBPMEmployeeAllWeb(BMSEmployee wLoginUser, Integer wProductID,
			Integer wLineID, Integer wPartID, Integer wStatus, Calendar wStartTime, Calendar wEndTime);

	/**
	 * ???????????????????????????(????????????????????????)
	 */
	ServiceResult<Integer> IPT_BatchCopyStandard(BMSEmployee wLoginUser, int wProductID, int wLineID, int wPartID1,
			int wPartPoint1, int wPartID2, int wPartPoint2, boolean wNotCurrent);

	/**
	 * ???????????????????????????????????????WBS??????????????????????????????????????????
	 */
	ServiceResult<Integer> IPT_ImportOrder(BMSEmployee wLoginUser, ExcelData result);

	/**
	 * ?????????????????????SQL??????
	 */
	ServiceResult<String> IPT_GetMaterialSQL(BMSEmployee wLoginUser, ExcelData result);

	ServiceResult<List<IPTStandard>> IPT_QueryStepVersionAllPro(BMSEmployee wLoginUser, int wLineID, int wProductID,
			int wPartID, String wStepName);
}
