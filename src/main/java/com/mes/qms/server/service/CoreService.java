package com.mes.qms.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.bfc.BFCAuditAction;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.bfc.BFCMessageResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bms.BMSWorkCharge;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.cfg.CFGUnit;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.utils.Configuration;

public interface CoreService {

	static String ServerUrl = Configuration.readConfigString("core.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("core.server.project.name", "config/config");

	APIResult BMS_LoginEmployee(String wLoginName, String wPassword, String wToken, long wMac, int wnetJS);

	APIResult BMS_GetEmployeeAll(BMSEmployee wLoginUser, int wDepartmentID, int wPosition, int wActive);

	APIResult BMS_QueryEmployeeByID(BMSEmployee wLoginUser, int wID);

	APIResult BMS_CheckPowerByAuthorityID(int wCompanyID, int wUserID, int wFunctionID, int wRangeID, int wTypeID);

	APIResult BMS_QueryRangeList(BMSEmployee wLoginUser, int wUserID, int wFunctionID);

	APIResult BMS_QueryPositionList(BMSEmployee wLoginUser);

	APIResult BMS_QueryPosition(BMSEmployee wLoginUser, int wID);

	APIResult BMS_QueryDepartmentList(BMSEmployee wLoginUser);

	APIResult BMS_QueryDepartment(BMSEmployee wLoginUser, int wID);

	APIResult CFG_QueryCalendarList(BMSEmployee wLoginUser, int wYear, int wWorkShopID);

	APIResult CFG_QueryCalendarList(BMSEmployee wLoginUser, int wWorkShopID, Calendar wStartTime, Calendar wEndTime);

	APIResult CFG_QueryRegionList(BMSEmployee wLoginUser);

	APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wType, int wActive,
			int wShiftID, Calendar wStartTime, Calendar wEndTime);

	APIResult BFC_UpdateMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList);

	APIResult BFC_SendMessageList(BMSEmployee wLoginUser, List<BFCMessage> wBFCMessageList);

	APIResult BMS_QueryWorkChargeList(BMSEmployee wLoginUser, int wStationID, int wClassID, int wActive);

	APIResult BMS_SaveWorkCharge(BMSEmployee wLoginUser, BMSWorkCharge wBMSWorkCharge);

	APIResult CFG_QueryUnitList(BMSEmployee wLoginUser);

	APIResult CFG_SaveUnit(BMSEmployee wLoginUser, CFGUnit wUnit);

	APIResult BFC_UpdateAction(BMSEmployee wLoginUser, BFCAuditAction wBFCAuditAction, String wTitle);

	APIResult BFC_CurrentConfig(BMSEmployee wLoginUser, int wModuleID, int wTaskID, int wUserID);

	APIResult BFC_ActionAll(BMSEmployee wLoginUser, int wModuleID, int wTaskID);

	APIResult FMC_QueryWorkChargeList(BMSEmployee wLoginUser);

	APIResult BMS_UserAllByFunction(BMSEmployee wLoginUser, int wFunctionID);

	APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, int wMessageID, int wType,
			int wActive, int wShiftID, Calendar wStartTime, Calendar wEndTime);

	APIResult BFC_GetMessageList(BMSEmployee wLoginUser, int wResponsorID, int wModuleID, List<Integer> wMessageID,
			int wType, int wActive);

	APIResult MSS_PartItemAll(BMSEmployee wLoginUser, int wOrderID, String wPartItemNo, int wCustomerID, int wLineID,
			String wProductNo);

	APIResult QMS_StartInstance(BMSEmployee wLoginUser, String processDefinitionKey);

	APIResult QMS_CompleteInstance(BMSEmployee wLoginUser, BPMTaskBase wBPMTaskBase, String wTaskID);

	APIResult QMS_DynamicTurnBop(BMSEmployee wLoginUser, int wOldRouteID, int wNewRouteID,
			List<FPCRoutePartPoint> wAddedList, List<FPCRoutePartPoint> wRemovedList,
			List<FPCRoutePartPoint> wChangedList, List<String> wPartNoList, List<Integer> wReworkOrderIDList);

	APIResult BMS_UserAll(BMSEmployee wLoginUser, int wRoleID);

	BFCMessageResult BFC_MessageSend(BMSEmployee wLoginUser, String wLoginID, String wTitle, String wContent);
}
