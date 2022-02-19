package com.mes.qms.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.utils.Configuration;

/**
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-13 09:58:19
 * @LastEditTime 2020-1-13 09:58:22
 *
 */
public interface LOCOAPSService {

	static String ServerUrl = Configuration.readConfigString("loco.aps.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("loco.aps.server.project.name", "config/config");

	/**
	 * 查询当天的派工任务
	 * 
	 * @param wLoginUser 登陆者
	 * @param wShiftDate 查询日期
	 * @param wErrorCode 错误码
	 * @return 派工任务集合
	 */
	APIResult SFC_QueryTaskStepListByEmployee(BMSEmployee wLoginUser, Calendar wShiftDate);

	/**
	 * 根据派工任务ID查询派工任务
	 * 
	 * @param wLoginUser     登录人
	 * @param wSFCTaskStepID 派工任务ID
	 * @param wErrorCode     错误码
	 * @return 派工任务
	 */
	APIResult SFC_QueryTaskStepByID(BMSEmployee wLoginUser, int wSFCTaskStepID);

	/**
	 * 根据工序任务ID获取工序任务
	 * 
	 * @param wLoginUser     登录人
	 * @param wAPSTaskStepID 工序任务ID
	 * @param wErrorCode     错误码
	 * @return 工序任务
	 */
	APIResult APS_QueryTaskStepByID(BMSEmployee wLoginUser, int wAPSTaskStepID);

	/**
	 * 更新或保存工序任务
	 * 
	 * @param wLoginUser   登陆者
	 * @param wAPSTaskStep 工序任务
	 * @param wErrorCode   错误码
	 * @return 主键
	 */
	APIResult APS_UpdateTaskStep(BMSEmployee wLoginUser, APSTaskStep wAPSTaskStep);

	APIResult OMS_QueryOrderByID(BMSEmployee wLoginUser, int wOrderID);

	APIResult OMS_QueryOrderList(BMSEmployee wLoginUser, int wCommandID, String wOrderNo, int wLineID, int wProductID,
			int wBureauSectionID, String wPartNo, String wBOMNo, int wActive, List<Integer> wStatusList);

	APIResult OMS_UpdateOrder(BMSEmployee wLoginUser, OMSOrder wOrder);

	APIResult OMS_QueryRFOrderList(BMSEmployee wLoginUser, int wCustomerID, int wLineID, int wProductID, String wPartNo,
			Calendar wStartTime, Calendar wEndTime);

	APIResult APS_QueryTaskPartByID(BMSEmployee wLoginUser, int wTaskPartID);

	APIResult APS_QueryTaskPartList(BMSEmployee wLoginUser, int wShiftID, int wShiftPeriod);

	APIResult APS_QueryTaskPartList(BMSEmployee wLoginUser, List<Integer> wStateIDList);

	APIResult APS_QueryTaskStepList(BMSEmployee wLoginUser, int wOrderID, int wStationID, int wTaskPartID,
			List<Integer> wStateIDList);

	APIResult SFC_UpdateTaskStep(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep);

	APIResult SFC_QueryTaskStepList(BMSEmployee wLoginUser, int wAPSTaskStepID, int wShiftID, int wOperatorID);

	APIResult APS_UpdateTaskPart(BMSEmployee wLoginUser, APSTaskPart wAPSTaskPart);

	APIResult OMS_QueryOrderListByIDList(BMSEmployee wLoginUser, List<Integer> wIDList);

	APIResult SFC_QueryTaskStepList(BMSEmployee wLoginUser, List<Integer> wAPSTaskStepIDList);

	APIResult SCH_QuerySecondmentList(BMSEmployee wLoginUser, int wOperateID);

	APIResult SFC_DeleteTaskStepList(BMSEmployee wLoginUser, List<SFCTaskStep> wList);

	APIResult APS_QueryWorkHour(BMSEmployee wLoginUser);

	/**
	 * 条件查询所有工位计划
	 */
	APIResult APS_QueryTaskPartAll(BMSEmployee wLoginUser, int wOrderID, int wShiftPeriod);

	APIResult APS_BOMItemUpdate(BMSEmployee wLoginUser, APSBOMItem wAPSBOMItem);

	APIResult APS_BOMItemDelete(BMSEmployee wLoginUser, APSBOMItem wBOMItem);

	APIResult APS_BOMItemUpdateProperty(BMSEmployee wLoginUser, APSBOMItem wBOMItem);

	APIResult Andon_QueryProductStatus(BMSEmployee wLoginUser);

	APIResult SCH_EmployeePartList(BMSEmployee wLoginUser, int wClassID);
}
