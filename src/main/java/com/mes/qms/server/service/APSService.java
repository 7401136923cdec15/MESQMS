package com.mes.qms.server.service;

import java.util.List;

import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSMaterialReturn;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.utils.Configuration;

public interface APSService {
	static String ServerUrl = Configuration.readConfigString("aps.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("aps.server.project.name", "config/config");

	APIResult SCH_QueryWorkerListByShiftID(BMSEmployee wLoginUser, int wWorkShopID, int wLineID, int wFunctionModule,
			int wShiftID);

	APIResult SCH_QuerySubWorkerListByLoginID(BMSEmployee wLoginUser, int wEventID);

	APIResult SCH_QueryWorkerByPositionID(BMSEmployee wLoginUser, int wPositionID, int wWorkShopID, int wShiftID);

	APIResult SCH_QueryLeadWorkerByUserID(BMSEmployee wLoginUser, int wUserID, int wWorkShopID, boolean wIsTop);

	APIResult SCH_QueryLeadWorkerByPositionID(BMSEmployee wLoginUser, int wPositionID, int wWorkShopID, int wShiftID);

	APIResult SCH_QueryPositionListByDeviceID(BMSEmployee wLoginUser, int wDeviceID);

	APIResult SCH_QueryPositionListByStationID(BMSEmployee wLoginUser, int wStationID, int wEventID);

	APIResult SCH_QueryPositionListByLoginID(BMSEmployee wLoginUser, int wEventID, boolean wIncludeSub);

	APIResult SCH_QueryPositionListByShiftID(BMSEmployee wLoginUser, int wLineID, int wWorkShopID, int wPositionLevel,
			int wShiftID, boolean wFillShift);

	APIResult SCH_QueryPositionListByShiftID(BMSEmployee wLoginUser, int wShiftID, int wWorkShopID, int wStationID,
			int wEventID);

	APIResult SCH_QueryShiftID(BMSEmployee wLoginUser, int wWorkShopID, int wShiftPeriod, int wShifts);

	ServiceResult<List<APSMaterialReturn>> APS_QueryMaterialReturnList(BMSEmployee wLoginUser, String wWBSNo,
			int wPartID, int wStepID);

	/**
	 * 导出退料列表
	 */
	ServiceResult<String> ExportReturnMaterialList(BMSEmployee wLoginUser, List<APSMaterialReturn> wList);

	/**
	 * 维护工位任务的开工时间
	 */
	ServiceResult<Integer> APS_UpdateTaskPartStartWorkTime(BMSEmployee wLoginUser);
}
