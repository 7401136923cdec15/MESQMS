package com.mes.qms.server.serviceimpl.dao.imp;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.imp.IMPResultRecord;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class IMPResultRecordDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IMPResultRecordDAO.class);

	private static IMPResultRecordDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wIMPResultRecord
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IMPResultRecord wIMPResultRecord, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIMPResultRecord == null)
				return 0;

			String wSQL = "";
			if (wIMPResultRecord.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.imp_resultrecord(OperatorID,OperateTime,ImportType,"
						+ "Code,PID,Result,FileName,DataCount,ErrorCount,Progress) "
						+ "VALUES(:OperatorID,:OperateTime,:ImportType,:Code,:PID,"
						+ ":Result,:FileName,:DataCount,:ErrorCount,:Progress);", wInstance.Result);
				// 设置流水号
				wIMPResultRecord.Code = SelectNewCode(wLoginUser, wErrorCode);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.imp_resultrecord SET OperatorID = :OperatorID,"
								+ "OperateTime = :OperateTime,ImportType = :ImportType,"
								+ "Code = :Code,PID = :PID,Result = :Result,FileName = :FileName,"
								+ "DataCount = :DataCount,ErrorCount=:ErrorCount,Progress=:Progress WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIMPResultRecord.ID);
			wParamMap.put("OperatorID", wIMPResultRecord.OperatorID);
			wParamMap.put("OperateTime", wIMPResultRecord.OperateTime);
			wParamMap.put("ImportType", wIMPResultRecord.ImportType);
			wParamMap.put("Code", wIMPResultRecord.Code);
			wParamMap.put("PID", StringUtils.Join(",", wIMPResultRecord.PID));
			wParamMap.put("Result", wIMPResultRecord.Result);
			wParamMap.put("FileName", wIMPResultRecord.FileName);
			wParamMap.put("DataCount", wIMPResultRecord.DataCount);
			wParamMap.put("ErrorCount", wIMPResultRecord.ErrorCount);
			wParamMap.put("Progress", wIMPResultRecord.Progress);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIMPResultRecord.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIMPResultRecord.setID(wResult);
			} else {
				wResult = wIMPResultRecord.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 删除单条
	 * 
	 * @param wLoginUser
	 * @param wIMPResultRecord
	 * @param wErrorCode
	 */
	public void Delete(BMSEmployee wLoginUser, IMPResultRecord wIMPResultRecord, OutResult<Integer> wErrorCode) {
		try {
			List<IMPResultRecord> wList = new ArrayList<IMPResultRecord>(Arrays.asList(wIMPResultRecord));
			this.DeleteList(wLoginUser, wList, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 删除集合
	 * 
	 * @param wList
	 */
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IMPResultRecord> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (IMPResultRecord wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.imp_resultrecord WHERE ID IN({0}) ;",
					String.join(",", wIDList), wInstance.Result);
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查单条
	 * 
	 * @return
	 */
	public IMPResultRecord SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IMPResultRecord wResult = new IMPResultRecord();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IMPResultRecord> wList = SelectList(wLoginUser, wID, -1, -1, null, null, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<IMPResultRecord> SelectList(BMSEmployee wLoginUser, int wID, int wOperatorID, int wImportType,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<IMPResultRecord> wResultList = new ArrayList<IMPResultRecord>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.imp_resultrecord WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wOperatorID <= 0 or :wOperatorID = OperatorID ) "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  OperateTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  OperateTime ) "
					+ "and ( :wImportType <= 0 or :wImportType = ImportType );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wOperatorID", wOperatorID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wImportType", wImportType);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IMPResultRecord wItem = new IMPResultRecord();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID"));
				wItem.OperateTime = StringUtils.parseCalendar(wReader.get("OperateTime"));
				wItem.ImportType = StringUtils.parseInt(wReader.get("ImportType"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.PID = StringUtils.parseIntList((StringUtils.parseString(wReader.get("PID")).split(",|;")));
				wItem.Result = StringUtils.parseInt(wReader.get("Result"));
				wItem.FileName = StringUtils.parseString(wReader.get("FileName"));
				wItem.DataCount = StringUtils.parseInt(wReader.get("DataCount"));
				wItem.ErrorCount = StringUtils.parseInt(wReader.get("ErrorCount"));
				wItem.Progress = StringUtils.parseInt(wReader.get("Progress"));

				wItem.Operator = QMSConstants.GetBMSEmployeeName(wItem.OperatorID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 获取最新的流水号
	 * 
	 * @param wLoginUser 登录信息
	 * @param wErrorCode 错误码
	 * @return 流水号(IMP-流水号)
	 */
	public String SelectNewCode(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT CONCAT(''IMT-'',LPAD(COUNT(*)+1, 5 , 0)) as SCode FROM {0}.imp_resultrecord;",
					wInstance.Result);
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseString(wReader.get("SCode"));
				break;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	private IMPResultRecordDAO() {
		super();
	}

	public static IMPResultRecordDAO getInstance() {
		if (Instance == null)
			Instance = new IMPResultRecordDAO();
		return Instance;
	}

}
