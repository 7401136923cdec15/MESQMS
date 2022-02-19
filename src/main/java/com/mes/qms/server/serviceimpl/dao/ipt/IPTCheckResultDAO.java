package com.mes.qms.server.serviceimpl.dao.ipt;

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
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.record.IPTCheckResult;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class IPTCheckResultDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTCheckResultDAO.class);

	private static IPTCheckResultDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTCheckResult
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTCheckResult wIPTCheckResult, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTCheckResult == null)
				return 0;

			String wSQL = "";
			if (wIPTCheckResult.getID() <= 0) {
				wSQL = MessageFormat
						.format("INSERT INTO {0}.ipt_checkresult(RecordID,CheckContent,CheckResult,CheckTime) "
								+ "VALUES(:RecordID,:CheckContent,:CheckResult,:CheckTime);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.ipt_checkresult SET RecordID = :RecordID,CheckContent = :CheckContent,"
								+ "CheckResult = :CheckResult,CheckTime = :CheckTime WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTCheckResult.ID);
			wParamMap.put("RecordID", wIPTCheckResult.RecordID);
			wParamMap.put("CheckContent", wIPTCheckResult.CheckContent);
			wParamMap.put("CheckResult", wIPTCheckResult.CheckResult);
			wParamMap.put("CheckTime", wIPTCheckResult.CheckTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTCheckResult.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTCheckResult.setID(wResult);
			} else {
				wResult = wIPTCheckResult.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTCheckResult> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (IPTCheckResult wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_checkresult WHERE ID IN({0}) ;",
					StringUtils.Join(",", wIDList), wInstance.Result);
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
	public IPTCheckResult SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTCheckResult wResult = new IPTCheckResult();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTCheckResult> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<IPTCheckResult> SelectList(BMSEmployee wLoginUser, int wID, int wRecordID,
			OutResult<Integer> wErrorCode) {
		List<IPTCheckResult> wResultList = new ArrayList<IPTCheckResult>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.ipt_checkresult WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wRecordID <= 0 or :wRecordID = RecordID );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wRecordID", wRecordID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTCheckResult wItem = new IPTCheckResult();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.RecordID = StringUtils.parseInt(wReader.get("RecordID"));
				wItem.CheckContent = StringUtils.parseString(wReader.get("CheckContent"));
				wItem.CheckResult = StringUtils.parseString(wReader.get("CheckResult"));
				wItem.CheckTime = StringUtils.parseCalendar(wReader.get("CheckTime"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTCheckResultDAO() {
		super();
	}

	public static IPTCheckResultDAO getInstance() {
		if (Instance == null)
			Instance = new IPTCheckResultDAO();
		return Instance;
	}
}
