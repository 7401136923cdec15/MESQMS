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
import com.mes.qms.server.service.po.ipt.IPTProblemConfirmer;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class IPTProblemConfirmerDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTProblemConfirmerDAO.class);

	private static IPTProblemConfirmerDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTProblemConfirmer
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTProblemConfirmer wIPTProblemConfirmer, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTProblemConfirmer == null)
				return 0;

			String wSQL = "";
			if (wIPTProblemConfirmer.getID() <= 0) {
				wSQL = MessageFormat
						.format("INSERT INTO {0}.ipt_problemconfirmer(ProblemID,ConfirmerID,ConfirmTime,Status,Remark) "
								+ "VALUES(:ProblemID,:ConfirmerID,:ConfirmTime,:Status,:Remark);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.ipt_problemconfirmer SET ProblemID = :ProblemID,"
						+ "ConfirmerID = :ConfirmerID,ConfirmTime = :ConfirmTime,"
						+ "Status = :Status,Remark = :Remark WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTProblemConfirmer.ID);
			wParamMap.put("ProblemID", wIPTProblemConfirmer.ProblemID);
			wParamMap.put("ConfirmerID", wIPTProblemConfirmer.ConfirmerID);
			wParamMap.put("ConfirmTime", wIPTProblemConfirmer.ConfirmTime);
			wParamMap.put("Status", wIPTProblemConfirmer.Status);
			wParamMap.put("Remark", wIPTProblemConfirmer.Remark);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTProblemConfirmer.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTProblemConfirmer.setID(wResult);
			} else {
				wResult = wIPTProblemConfirmer.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTProblemConfirmer> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (IPTProblemConfirmer wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_problemconfirmer WHERE ID IN({0}) ;",
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
	public IPTProblemConfirmer SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTProblemConfirmer wResult = new IPTProblemConfirmer();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTProblemConfirmer> wList = SelectList(wLoginUser, wID, -1, -1, null, wErrorCode);
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
	public List<IPTProblemConfirmer> SelectList(BMSEmployee wLoginUser, int wID, int wProblemID, int wConfirmerID,
			List<Integer> wStateIDList, OutResult<Integer> wErrorCode) {
		List<IPTProblemConfirmer> wResultList = new ArrayList<IPTProblemConfirmer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wStateIDList == null) {
				wStateIDList = new ArrayList<Integer>();
			}

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.ipt_problemconfirmer WHERE  1=1  " + "and ( :wID <= 0 or :wID = ID ) "
							+ "and ( :wProblemID <= 0 or :wProblemID = ProblemID ) "
							+ "and ( :wConfirmerID <= 0 or :wConfirmerID = ConfirmerID ) "
							+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1}));",
					wInstance.Result, wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wProblemID", wProblemID);
			wParamMap.put("wConfirmerID", wConfirmerID);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTProblemConfirmer wItem = new IPTProblemConfirmer();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.ProblemID = StringUtils.parseInt(wReader.get("ProblemID"));
				wItem.ConfirmerID = StringUtils.parseInt(wReader.get("ConfirmerID"));
				wItem.ConfirmTime = StringUtils.parseCalendar(wReader.get("ConfirmTime"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));

				wItem.ConfirmerName = QMSConstants.GetBMSEmployeeName(wItem.ConfirmerID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTProblemConfirmerDAO() {
		super();
	}

	public static IPTProblemConfirmerDAO getInstance() {
		if (Instance == null)
			Instance = new IPTProblemConfirmerDAO();
		return Instance;
	}
}
