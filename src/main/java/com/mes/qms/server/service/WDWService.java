package com.mes.qms.server.service;

import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.utils.Configuration;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2020-3-31 18:31:20
 * @LastEditTime 2020-3-31 18:31:25
 *
 */
public interface WDWService {
	static String ServerUrl = Configuration.readConfigString("wdw.server.url", "config/config");
	static String ServerName = Configuration.readConfigString("wdw.server.project.name", "config/config");

	/**
	 * 根据专检任务ID和检验项ID判断该项是否发起不合格评审
	 * 
	 * @param wAdminUser 登录信息
	 * @param wTaskID     专检任务ID
	 * @param iPTItemID  检验项ID
	 * @return
	 */
	APIResult WDW_SpecialItemAll_NCR(BMSEmployee wAdminUser, int wTaskID);

	/**
	 * 根据专检任务ID和检验项ID判断该项是否发起返修
	 * 
	 * @param wAdminUser 登录信息
	 * @param wTaskID     专检任务ID
	 * @param iPTItemID  检验项ID
	 * @return
	 */
	APIResult WDW_SpecialItemAll_Repair(BMSEmployee wAdminUser, int wTaskID);

	/**
	 * 判断返修项是否都关闭
	 */
	APIResult WDW_IsAllRepairItemClosed(BMSEmployee wAdminUser, int wOrderID, int wPartID);
	
	/**
	 * 订单获取未完成返修项
	 */
	APIResult WDW_QueryItemByOrderID(BMSEmployee wAdminUser, int wOrderID);
	
	/**
	 * 条件查询不合格评审单
	 */
	APIResult NCR_QueryTimeAll(BMSEmployee wAdminUser, int wOrderID);
}
