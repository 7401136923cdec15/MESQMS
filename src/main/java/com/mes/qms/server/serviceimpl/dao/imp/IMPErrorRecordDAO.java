package com.mes.qms.server.serviceimpl.dao.imp;

import java.text.MessageFormat;
import java.util.ArrayList;
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
import com.mes.qms.server.service.po.imp.IMPErrorRecord;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class IMPErrorRecordDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IMPErrorRecordDAO.class);

	private static IMPErrorRecordDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wIMPErrorRecord
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IMPErrorRecord wIMPErrorRecord, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIMPErrorRecord == null)
				return 0;

			String wSQL = "";
			if (wIMPErrorRecord.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.imp_errorrecord(ParentID,PID,Message) VALUES(:ParentID,:PID,:Message);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.imp_errorrecord SET ParentID = :ParentID,PID = :PID,Message = :Message WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIMPErrorRecord.ID);
			wParamMap.put("ParentID", wIMPErrorRecord.ParentID);
			wParamMap.put("PID", wIMPErrorRecord.PID);
			wParamMap.put("Message", wIMPErrorRecord.Message);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIMPErrorRecord.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIMPErrorRecord.setID(wResult);
			} else {
				wResult = wIMPErrorRecord.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 删除集合
	 * 
	 * @param wList
	 */
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IMPErrorRecord> wList,
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
			for (IMPErrorRecord wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.imp_errorrecord WHERE ID IN({0}) ;",
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
	public IMPErrorRecord SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IMPErrorRecord wResult = new IMPErrorRecord();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IMPErrorRecord> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<IMPErrorRecord> SelectList(BMSEmployee wLoginUser, int wID, int wParentID,
			OutResult<Integer> wErrorCode) {
		List<IMPErrorRecord> wResultList = new ArrayList<IMPErrorRecord>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.imp_errorrecord WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wParentID <= 0 or :wParentID = ParentID );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wParentID", wParentID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IMPErrorRecord wItem = new IMPErrorRecord();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.ParentID = StringUtils.parseInt(wReader.get("ParentID"));
				wItem.PID = StringUtils.parseInt(wReader.get("PID"));
				wItem.Message = StringUtils.parseString(wReader.get("Message"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IMPErrorRecordDAO() {
		super();
	}

	public static IMPErrorRecordDAO getInstance() {
		if (Instance == null)
			Instance = new IMPErrorRecordDAO();
		return Instance;
	}
}
