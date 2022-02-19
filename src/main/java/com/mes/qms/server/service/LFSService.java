package com.mes.qms.server.service;

import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.lfs.LFSOperationLog;
import com.mes.qms.server.utils.Configuration;

/**
 * 
 * @author PengYouWang
 * @CreateTime 2019年12月31日23:52:51
 * @LastEditTime 2019年12月31日23:52:55
 *
 */
public interface LFSService {
	static String ServerUrl = Configuration.readConfigString("lfs.server.url", "config/config");
	static String ServerName = Configuration.readConfigString("lfs.server.project.name", "config/config");

	/**
	 * 查询工位工区集合
	 * 
	 * @return
	 */
	APIResult LFS_QueryWorkAreaStationList(BMSEmployee wLoginUser);

	/**
	 * 查询工区班组集合
	 * 
	 * @param wLoginUser
	 * @return
	 */
	APIResult LFS_QueryAreaDepartmentList(BMSEmployee wLoginUser);

	APIResult LFS_QueryWorkAreaCheckerList(BMSEmployee wLoginUser);

	APIResult LFS_UpdateOperationLog(BMSEmployee wLoginUser, LFSOperationLog wLFSOperationLog);
}
