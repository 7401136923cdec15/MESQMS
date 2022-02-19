package com.mes.qms.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.tcm.TCMMaterialChangeItems;
import com.mes.qms.server.service.po.tcm.TCMMaterialChangeLog;
import com.mes.qms.server.service.po.tcm.TCMRework;
import com.mes.qms.server.service.po.tcm.TCMTechChangeNotice;

public interface TCMService {
	/**
	 * 查询默认状态的单据
	 */
	ServiceResult<TCMTechChangeNotice> TCM_QueryDefaultTechChangeNotice(BMSEmployee wLoginUser, int wEventID);

	/**
	 * 创建单据
	 */
	ServiceResult<TCMTechChangeNotice> TCM_CreateTechChangeNotice(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * 提交单据
	 */
	ServiceResult<TCMTechChangeNotice> TCM_SubmitTechChangeNotice(BMSEmployee wLoginUser, TCMTechChangeNotice wData);

	/**
	 * 查询单条单据
	 */
	ServiceResult<TCMTechChangeNotice> TCM_GetTechChangeNotice(BMSEmployee wLoginUser, int wID);

	/**
	 * 用人员拿任务
	 */
	ServiceResult<List<BPMTaskBase>> TCM_QueryTechChangeNoticeEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询单据历史
	 */
	ServiceResult<List<TCMTechChangeNotice>> TCM_QueryTechChangeNoticeHistory(BMSEmployee wLoginUser, int wID,
			String wCode, int wUpFlowID, int wChangeLogID, Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<TCMTechChangeNotice>> TCM_QueryTechChangeNoticeEmployeeAllWeb(BMSEmployee wLoginUser,
			Integer wStatus, Calendar wStartTime, Calendar wEndTime, int wStepID, String wMaterialNo);

	/**
	 * 查询默认状态的单据
	 */
	ServiceResult<TCMRework> TCM_QueryDefaultRework(BMSEmployee wLoginUser, int wEventID);

	/**
	 * 创建单据
	 */
	ServiceResult<TCMRework> TCM_CreateRework(BMSEmployee wLoginUser, BPMEventModule wEventID);

	/**
	 * 提交单据
	 */
	ServiceResult<TCMRework> TCM_SubmitRework(BMSEmployee wLoginUser, TCMRework wData);

	/**
	 * 查询单条单据
	 */
	ServiceResult<TCMRework> TCM_GetRework(BMSEmployee wLoginUser, int wID);

	/**
	 * 用人员拿任务
	 */
	ServiceResult<List<BPMTaskBase>> TCM_QueryReworkEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime);

	/**
	 * 查询单据历史
	 */
	ServiceResult<List<TCMRework>> TCM_QueryReworkHistory(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<TCMRework>> TCM_QueryReworkEmployeeAllNew(BMSEmployee wLoginUser, Integer wStatus,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<List<BMSEmployee>> TCM_QueryUserList(BMSEmployee wLoginUser);

	ServiceResult<TCMMaterialChangeLog> TCM_QueryMaterialChangeLog(BMSEmployee sysAdmin, int wID);

	ServiceResult<Integer> TCM_UpdateList(BMSEmployee sysAdmin, List<TCMMaterialChangeItems> wItemList);

	ServiceResult<List<TCMRework>> TCM_QueryReworkEmployeeAllWeb(BMSEmployee wLoginUser, Integer wStatus,
			Calendar wStartTime, Calendar wEndTime);

	ServiceResult<String> TCM_getSignature(BMSEmployee wLoginUser, String wSha1);

	ServiceResult<String> TCM_fileinfo(String wSignature, String wAppid, String wSha1);

	ServiceResult<Integer> TCM_UpdateOldMaterial(BMSEmployee wLoginUser);

	ServiceResult<Integer> TCM_UpdateOldProperty(BMSEmployee sysAdmin);

	ServiceResult<String> TCM_FilePreview(BMSEmployee wLoginUser, String wFTPPath);

	ServiceResult<String> TCM_FilePreviewLocalFile(BMSEmployee wLoginUser, String wPathUrl);

	ServiceResult<Integer> TCM_CopyItem(BMSEmployee wLoginUser, int wSourceID, int wTargetID);

	ServiceResult<Integer> TCM_ReSendNoticeList(BMSEmployee wLoginUser, int wNoticeID);
}
