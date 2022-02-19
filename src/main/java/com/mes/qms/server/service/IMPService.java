package com.mes.qms.server.service;

import java.util.Calendar;
import java.util.List;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.imp.IMPErrorRecord;
import com.mes.qms.server.service.po.imp.IMPResultRecord;

public interface IMPService {

	/**
	 * 根据ID查询
	 * 
	 * @return
	 */
	ServiceResult<IMPResultRecord> IMP_QueryResultRecord(BMSEmployee wLoginUser, int wID);

	/**
	 * 条件查询
	 * 
	 * @return
	 */
	ServiceResult<List<IMPResultRecord>> IMP_QueryResultRecordList(BMSEmployee wLoginUser, int wID, int wOperatorID,
			int wImportType, Calendar wStartTime, Calendar wEndTime);

	/**
	 * 根据ID查询
	 * 
	 * @return
	 */
	ServiceResult<IMPErrorRecord> IMP_QueryErrorRecord(BMSEmployee wLoginUser, int wID);

	/**
	 * 条件查询
	 * 
	 * @return
	 */
	ServiceResult<List<IMPErrorRecord>> IMP_QueryErrorRecordList(BMSEmployee wLoginUser, int wID, int wParentID);
}
