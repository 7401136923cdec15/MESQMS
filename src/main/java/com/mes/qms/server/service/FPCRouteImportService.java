package com.mes.qms.server.service;

import java.util.List;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.fpc.FPCFlowPart;
import com.mes.qms.server.service.po.fpc.FPCPartPoint;
import com.mes.qms.server.service.po.fpc.FPCRouteC;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.mbs.MBSApiLog;
import com.mes.qms.server.service.po.mss.MSSBOM;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.mss.MSSBOMItemC;
import com.mes.qms.server.service.po.mss.MSSMaterial;
import com.mes.qms.server.service.po.mss.MSSPartItem;
import com.mes.qms.server.service.po.mss.MSSPartRecord;
import com.mes.qms.server.service.po.mss.MSSRepairRecord;
import com.mes.qms.server.service.po.ncr.NCRTask;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;

/**
 * 工艺BOP导入服务
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-6-27 10:58:07
 * @LastEditTime 2020-6-27 10:58:13
 *
 */
public interface FPCRouteImportService {

	/**
	 * 导入工艺BOP
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	ServiceResult<Integer> FPC_ImportRoute(BMSEmployee wLoginUser, ExcelData wExcelData, String wFileName);

	/**
	 * 工艺数据对比
	 * 
	 * @param wLoginUser 登录信息
	 * @param wARouteID  工艺路线A
	 * @param wBRouteID  工艺路线B
	 * @return
	 */
	ServiceResult<List<FPCRouteC>> FPC_Compare(BMSEmployee wLoginUser, int wARouteID, int wBRouteID);

	/**
	 * BOM数据对比
	 * 
	 * @param wLoginUser
	 * @param wABOMID
	 * @param wBBOMID
	 * @return
	 */
	ServiceResult<List<MSSBOMItemC>> FPC_CompareBOM(BMSEmployee wLoginUser, int wABOMID, int wBBOMID);

	/**
	 * 导出工序明细(项点模板)
	 */
	ServiceResult<String> FPC_ExportSteps(BMSEmployee wLoginUser, int wProductID, int wLineID, int wPartID);

	/**
	 * 导出制造中心员工组织架构
	 */
	ServiceResult<String> ExportOrgnization(BMSEmployee wLoginUser);

	/**
	 * 复制工艺BOP
	 */
	ServiceResult<Integer> FPC_CopyBOP(BMSEmployee wLoginUser, int wSourceRouteID, int wTargetRouteID);

	/**
	 * 复制标准BOM
	 */
	ServiceResult<Integer> FPC_CopyBOM(BMSEmployee wLoginUser, int wSourceBOMID, int wTargetBOMID);

	/**
	 * 导出标准BOM
	 */
	ServiceResult<String> IPT_ExportMSSBOM(BMSEmployee wLoginUser, List<MSSBOM> wItemList);

	/**
	 * 导出标准BOM子项
	 */
	ServiceResult<String> IPT_ExportMSSBOMItem(BMSEmployee wLoginUser, int wBOMID);

	/**
	 * 复制产线单元明细
	 */
	ServiceResult<Integer> FPC_CopyLineUnit(BMSEmployee wLoginUser, int wSProductID, int wSLineID, int wTLineID,
			int wTProductID);

	/**
	 * 逆向同步产线单元明细
	 */
	ServiceResult<Integer> FPC_SynchronizeLineUnit(BMSEmployee wLoginUser, int wRouteID);

	/**
	 * 动态比对工艺bop
	 */
	ServiceResult<Integer> FPC_DynamicCompareBop(BMSEmployee wLoginUser, int wNewRouteID);

	/**
	 * 动态调整bop
	 */
	ServiceResult<Integer> FPC_DynamicTurnBop(BMSEmployee wLoginUser, int wOldRouteID,
			List<FPCRoutePartPoint> wAddedList, List<FPCRoutePartPoint> wRemovedList,
			List<FPCRoutePartPoint> wChangedList, List<String> wPartNoList, int wNewRouteID,
			List<Integer> wReworkOrdreIDList, List<FPCRoutePartPoint> wUpdatedList, int wChangeLogID);

	/**
	 * 根据工位类型查询终检、出厂检、预检工序列表
	 */
	ServiceResult<List<FPCPartPoint>> FPC_QueryStepListByPartType(BMSEmployee wLoginUser, int wPartType);

	/**
	 * 获取分页的物料数据
	 */
	ServiceResult<List<MSSMaterial>> MSS_QueryPageAll(BMSEmployee wLoginUser, int wPageSize, int wCurPage,
			String wMaterialNo, String wMaterialName);

	/**
	 * 获取物料总记录数
	 */
	ServiceResult<Integer> MSS_QueryRecordSize(BMSEmployee wLoginUser, String wMaterialNo, String wMaterialName);

	ServiceResult<Integer> MBS_QueryRecordSize(BMSEmployee wLoginUser, int wLoginID, String wProjectName, String wURI);

	ServiceResult<List<MBSApiLog>> MBS_QueryPageAll(BMSEmployee wLoginUser, int wPageSize, int wCurPage, int wLoginID,
			String wProjectName, String wURI);

	/**
	 * 批量修改同车型、修程、局段的转序控制
	 */
	ServiceResult<Integer> FPC_BatchUpdateChangeControl(BMSEmployee wLoginUser, int wRouteID, int wPartID,
			int wChangeControl);

	ServiceResult<List<FPCPartPoint>> FMC_QueryLineUnitStepList(BMSEmployee wLoginUser, int wProductID, int wLineID,
			int wPartID);

	ServiceResult<List<MSSBOMItem>> MSS_QueryBomItemList(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			int wStepID);

	ServiceResult<List<FPCFlowPart>> FPC_QueryFlowDataPart(BMSEmployee wLoginUser, int wRouteID);

	ServiceResult<BFCMessage> BFC_QueryMessageInfo(BMSEmployee wLoginUser, Integer wMessageID);

	ServiceResult<List<FPCRoutePart>> FPC_QeuryRoutePartList(BMSEmployee wLoginUser, int wRouteID);

	ServiceResult<List<BMSEmployee>> BMS_QueryUserSonList(BMSEmployee wLoginUser, int wDepartmentID);

	ServiceResult<Integer> MSS_PartInstock(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, IPTItem wItem, IPTValue wValue,
			Integer wOperateType);

	ServiceResult<List<MSSPartItem>> MSS_QueryRepairList(BMSEmployee wLoginUser, int wOrderID, String wPartsCode);

	ServiceResult<List<MSSPartItem>> MSS_QueryAssembleList(BMSEmployee wLoginUser, int wProductID, int wLineID,
			String wPartsCode);

	ServiceResult<List<MSSPartRecord>> MSS_QueryAllPartRecord(BMSEmployee wLoginUser, int wOrderID, int wOperateType);

	ServiceResult<Integer> MSS_SRPartInstock(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, IPTItem wItem,
			IPTValue wValue, Integer wOperateType);

	ServiceResult<List<MSSPartItem>> MSS_QuerySRRepairList(BMSEmployee wLoginUser, int wOrderID, String wPartsCode);

	ServiceResult<List<MSSPartItem>> MSS_QuerySRAssembleList(BMSEmployee wLoginUser, int wProductID, int wLineID,
			String wPartsCode, int wOrderID);

	ServiceResult<List<MSSRepairRecord>> MSS_QueryAllSRPartRecord(BMSEmployee wLoginUser, int wOrderID,
			int wOperateType);

	ServiceResult<Integer> MBS_ClearData(BMSEmployee wLoginUser);

	ServiceResult<List<MSSPartItem>> MSS_QuerySRList(BMSEmployee wLoginUser, int wOrderID);

	ServiceResult<String> NCR_ExportTaskList(BMSEmployee wLoginUser, List<NCRTask> wList);

	ServiceResult<List<MSSBOMItem>> MSS_QueryBomItemAll(BMSEmployee wLoginUser, int wBOMID, int wPartID, int wStepID,
			String wMaterialNo, String wMaterialName, int wReplaceType, int wOutsourceType);

	ServiceResult<Integer> BMS_IsManager(BMSEmployee wLoginUser);

	/**
	 * 批量激活、禁用标准BOM子项
	 */
	ServiceResult<Integer> MSS_ActiveListBomItem(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive);

	/**
	 * 修改专检控制
	 */
	ServiceResult<Integer> FPC_UpdateSpecialControl(BMSEmployee wLoginUser, FPCRoutePart wFPCRoutePart);

	/**
	 * 调整工序的工位
	 */
	ServiceResult<Integer> FPC_AdjustStep(BMSEmployee wLoginUser, int wPartPointID, int wRightPartID);

	ServiceResult<String> MSS_SynchronizedMaterial(BMSEmployee wLoginUser, ExcelData result, String wOriginalFileName);

	ServiceResult<String> FPC_ExportBOP(BMSEmployee wLoginUser, int wRouteID);
	
	ServiceResult<Integer> MSS_Import(BMSEmployee wLoginUser, ServiceResult<ExcelData> wExcelData);
}
