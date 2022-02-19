package com.mes.qms.server.serviceimpl.dao.mbs;

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
import com.mes.qms.server.service.po.mbs.MBSApiLog;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class MBSApiLogDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(MBSApiLogDAO.class);

	private static MBSApiLogDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wMBSApiLog
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, MBSApiLog wMBSApiLog, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wMBSApiLog == null)
				return 0;

			String wSQL = "";
			if (wMBSApiLog.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.mbs_apilog(CompanyID,LoginID,ProjectName,URI,Method,Params,Result,RequestTime,ResponseTime,RequestBody,IntervalTime,ResponseStatus) VALUES(:CompanyID,:LoginID,:ProjectName,:URI,:Method,:Params,:Result,:RequestTime,:ResponseTime,:RequestBody,:IntervalTime,:ResponseStatus);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.mbs_apilog SET CompanyID = :CompanyID,LoginID = :LoginID,ProjectName = :ProjectName,URI = :URI,Method = :Method,Params = :Params,Result = :Result,RequestTime = :RequestTime,ResponseTime = :ResponseTime,RequestBody = :RequestBody,IntervalTime = :IntervalTime,ResponseStatus = :ResponseStatus WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wMBSApiLog.ID);
			wParamMap.put("CompanyID", wMBSApiLog.CompanyID);
			wParamMap.put("LoginID", wMBSApiLog.LoginID);
			wParamMap.put("ProjectName", wMBSApiLog.ProjectName);
			wParamMap.put("URI", wMBSApiLog.URI);
			wParamMap.put("Method", wMBSApiLog.Method);
			wParamMap.put("Params", wMBSApiLog.Params);
			wParamMap.put("Result", wMBSApiLog.Result);
			wParamMap.put("RequestTime", wMBSApiLog.RequestTime);
			wParamMap.put("ResponseTime", wMBSApiLog.ResponseTime);
			wParamMap.put("RequestBody", wMBSApiLog.RequestBody);
			wParamMap.put("IntervalTime", wMBSApiLog.IntervalTime);
			wParamMap.put("ResponseStatus", wMBSApiLog.ResponseStatus);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wMBSApiLog.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wMBSApiLog.setID(wResult);
			} else {
				wResult = wMBSApiLog.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<MBSApiLog> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (MBSApiLog wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.mbs_apilog WHERE ID IN({0}) ;",
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
	public MBSApiLog SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		MBSApiLog wResult = new MBSApiLog();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<MBSApiLog> wList = SelectList(wLoginUser, wID, -1, "", "", wErrorCode);
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
	public List<MBSApiLog> SelectList(BMSEmployee wLoginUser, long wID, int wLoginID, String wProjectName, String wURI,
			OutResult<Integer> wErrorCode) {
		List<MBSApiLog> wResultList = new ArrayList<MBSApiLog>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat
					.format("SELECT * FROM {0}.mbs_apilog WHERE  1=1  and ( :wID <= 0 or :wID = ID ) "
							+ "and ( :wLoginID <= 0 or :wLoginID = LoginID ) "
							+ "and ( :wProjectName is null or :wProjectName = '''' or :wProjectName = ProjectName ) "
							+ "and ( :wURI is null or :wURI = '''' or :wURI = URI );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wLoginID", wLoginID);
			wParamMap.put("wProjectName", wProjectName);
			wParamMap.put("wURI", wURI);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public Integer SelectPageNumber(BMSEmployee wLoginUser, long wID, int wLoginID, String wProjectName, String wURI,
			OutResult<Integer> wErrorCode) {
		Integer wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select count(*) Number from {0}.mbs_apilog where 1=1  and ( :wID <= 0 or :wID = ID ) "
							+ "and ( :wLoginID <= 0 or :wLoginID = LoginID ) "
							+ "and ( :wProjectName is null or :wProjectName = '''' or :wProjectName = ProjectName ) "
							+ "and ( :wURI is null or :wURI = '''' or  URI like  ''%{1}%''  );",
					wInstance.Result, wURI);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wLoginID", wLoginID);
			wParamMap.put("wProjectName", wProjectName);
			wParamMap.put("wURI", wURI);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("Number"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取分页数据
	 */
	public List<MBSApiLog> SelectPageAll(BMSEmployee wLoginUser, int wPageSize, int wCurPage, long wID, int wLoginID,
			String wProjectName, String wURI, OutResult<Integer> wErrorCode) {
		List<MBSApiLog> wResult = new ArrayList<MBSApiLog>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select * from {0}.mbs_apilog where 1=1  and ( :wID <= 0 or :wID = ID ) "
							+ "and ( :wLoginID <= 0 or :wLoginID = LoginID ) "
							+ "and ( :wProjectName is null or :wProjectName = '''' or :wProjectName = ProjectName ) "
							+ "and ( :wURI is null or :wURI = '''' or  URI like  ''%{1}%''  ) limit {2},{3};",
					wInstance.Result, wURI, (wCurPage - 1) * wPageSize, wPageSize);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wLoginID", wLoginID);
			wParamMap.put("wProjectName", wProjectName);
			wParamMap.put("wURI", wURI);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResult, wQueryResult);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private void SetValue(List<MBSApiLog> wResultList, List<Map<String, Object>> wQueryResult) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				MBSApiLog wItem = new MBSApiLog();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.CompanyID = StringUtils.parseInt(wReader.get("CompanyID"));
				wItem.LoginID = StringUtils.parseInt(wReader.get("LoginID"));
				wItem.ProjectName = StringUtils.parseString(wReader.get("ProjectName"));
				wItem.URI = StringUtils.parseString(wReader.get("URI"));
				wItem.Method = StringUtils.parseString(wReader.get("Method"));
				wItem.Params = StringUtils.parseString(wReader.get("Params"));
				wItem.Result = StringUtils.parseString(wReader.get("Result"));
				wItem.RequestTime = StringUtils.parseCalendar(wReader.get("RequestTime"));
				wItem.ResponseTime = StringUtils.parseCalendar(wReader.get("ResponseTime"));
				wItem.RequestBody = StringUtils.parseString(wReader.get("RequestBody"));
				wItem.IntervalTime = StringUtils.parseInt(wReader.get("IntervalTime"));
				wItem.ResponseStatus = StringUtils.parseInt(wReader.get("ResponseStatus"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private MBSApiLogDAO() {
		super();
	}

	public static MBSApiLogDAO getInstance() {
		if (Instance == null)
			Instance = new MBSApiLogDAO();
		return Instance;
	}

	public Integer ClearData(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("truncate table {0}.mbs_apilog;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
