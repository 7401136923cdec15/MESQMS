package com.mes.qms.server.serviceimpl;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.IMPService;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.imp.IMPErrorRecord;
import com.mes.qms.server.service.po.imp.IMPResultRecord;
import com.mes.qms.server.serviceimpl.dao.imp.IMPErrorRecordDAO;
import com.mes.qms.server.serviceimpl.dao.imp.IMPResultRecordDAO;

@Service
public class IMPServiceImpl implements IMPService {

	private static Logger logger = LoggerFactory.getLogger(IMPServiceImpl.class);

	private static IMPService _instance;

	public static IMPService getInstance() {
		if (_instance == null)
			_instance = new IMPServiceImpl();

		return _instance;
	}

	/**
	 * ID查询
	 */
	@Override
	public ServiceResult<IMPResultRecord> IMP_QueryResultRecord(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IMPResultRecord> wResult = new ServiceResult<IMPResultRecord>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = IMPResultRecordDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询
	 */
	@Override
	public ServiceResult<List<IMPResultRecord>> IMP_QueryResultRecordList(BMSEmployee wLoginUser, int wID,
			int wOperatorID, int wImportType, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<IMPResultRecord>> wResult = new ServiceResult<List<IMPResultRecord>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = IMPResultRecordDAO.getInstance().SelectList(wLoginUser, wID, wOperatorID, wImportType,
					wStartTime, wEndTime, wErrorCode);

			if (wResult.Result.size() > 0) {
				// 排序
				wResult.Result.sort(Comparator.comparing(IMPResultRecord::getOperateTime, Comparator.reverseOrder()));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ID查询
	 */
	@Override
	public ServiceResult<IMPErrorRecord> IMP_QueryErrorRecord(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IMPErrorRecord> wResult = new ServiceResult<IMPErrorRecord>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = IMPErrorRecordDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询s
	 */
	@Override
	public ServiceResult<List<IMPErrorRecord>> IMP_QueryErrorRecordList(BMSEmployee wLoginUser, int wID,
			int wParentID) {
		ServiceResult<List<IMPErrorRecord>> wResult = new ServiceResult<List<IMPErrorRecord>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = IMPErrorRecordDAO.getInstance().SelectList(wLoginUser, wID, wParentID, wErrorCode);
			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}
}
