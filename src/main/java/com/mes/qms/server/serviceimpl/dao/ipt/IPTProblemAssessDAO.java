package com.mes.qms.server.serviceimpl.dao.ipt;

import java.text.MessageFormat;
import java.util.ArrayList;
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

import com.alibaba.fastjson.JSON;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.ipt.IPTProblemAssess;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class IPTProblemAssessDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTProblemAssessDAO.class);

	private static IPTProblemAssessDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTProblemAssess
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTProblemAssess wIPTProblemAssess, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTProblemAssess == null)
				return 0;

			String wSQL = "";
			if (wIPTProblemAssess.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.ipt_problemassess(ProblemID,CreateID,CreateTime,AuditID,IPTSop) "
						+ "VALUES(:ProblemID,:CreateID,now(),:AuditID,:IPTSop);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.ipt_problemassess SET AuditTime = now(),IPTSop = :IPTSop,Status = :Status WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTProblemAssess.ID);
			wParamMap.put("ProblemID", wIPTProblemAssess.ProblemID);
			wParamMap.put("CreateID", wIPTProblemAssess.CreateID);
			wParamMap.put("AuditID", wIPTProblemAssess.AuditID);
			wParamMap.put("IPTSop", JSON.toJSONString(wIPTProblemAssess.IPTSOP));
			wParamMap.put("Status", wIPTProblemAssess.Status);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTProblemAssess.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTProblemAssess.setID(wResult);
			} else {
				wResult = wIPTProblemAssess.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTProblemAssess> wList,
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
			for (IPTProblemAssess wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_problemassess WHERE ID IN({0}) ;",
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
	public IPTProblemAssess SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTProblemAssess wResult = new IPTProblemAssess();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTProblemAssess> wList = SelectList(wLoginUser, wID, -1, -1, -1, null, null, wErrorCode);
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
	public List<IPTProblemAssess> SelectList(BMSEmployee wLoginUser, int wID, int wProblemID, int wAuditID, int wStatus,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<IPTProblemAssess> wResultList = new ArrayList<IPTProblemAssess>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
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

			String wSQL = MessageFormat.format("SELECT * FROM {0}.ipt_problemassess WHERE  1=1  "
					+ "and ( :wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or CreateTime >= :wStartTime) "
					+ "and ( :wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or CreateTime <= :wEndTime) "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wStatus < 0 or :wStatus = Status ) "
					+ "and ( :wProblemID <= 0 or :wProblemID = ProblemID ) "
					+ "and ( :wAuditID <= 0 or :wAuditID = AuditID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wProblemID", wProblemID);
			wParamMap.put("wAuditID", wAuditID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", wStatus);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTProblemAssess wItem = new IPTProblemAssess();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.ProblemID = StringUtils.parseInt(wReader.get("ProblemID"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.AuditID = StringUtils.parseInt(wReader.get("AuditID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.AuditTime = StringUtils.parseCalendar(wReader.get("AuditTime"));
				wItem.IPTSOP = JSON.parseObject(StringUtils.parseString(wReader.get("IPTSop")), IPTSOP.class);
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));

				wItem.Creator = QMSConstants.GetBMSEmployeeName(wItem.CreateID);
				wItem.Auditor = QMSConstants.GetBMSEmployeeName(wItem.AuditID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTProblemAssessDAO() {
		super();
	}

	public static IPTProblemAssessDAO getInstance() {
		if (Instance == null)
			Instance = new IPTProblemAssessDAO();
		return Instance;
	}
}
