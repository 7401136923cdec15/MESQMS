package com.mes.qms.server.serviceimpl.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.alibaba.fastjson.JSON;
import com.mes.qms.server.service.mesenum.DBEnumType;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.DesUtil;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.utils.MESServer;
import com.mes.qms.server.utils.Configuration;
import com.mes.qms.server.utils.DBHelper;

public class BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(BaseDAO.class);

	protected NamedParameterJdbcTemplate nameJdbcTemplate;

	public BaseDAO() {
		nameJdbcTemplate = DBHelper.getTemplate();
		this.SQLType = DBEnumType.getEnumType(
				StringUtils.parseInt(Configuration.readConfigString("mes.server.sql.type", "config/config")));
	}

	public final static String defaultPassword = "123456";

	public final static String appSecret = "c5e330214fb33e2d80f14e3fc45ed214";

	public static BMSEmployee SysAdmin = new BMSEmployee();

	static {
		SysAdmin.CompanyID = 0;
		SysAdmin.ID = -100;
		SysAdmin.LoginName = DesUtil.encrypt("SHRISMCIS", appSecret);
		SysAdmin.Password = DesUtil.encrypt("shrismcis", appSecret);
	}

	protected DBEnumType SQLType = DBEnumType.MySQL;

	public BaseDAO(NamedParameterJdbcTemplate nameJdbcTemplate) {
		this.nameJdbcTemplate = nameJdbcTemplate;
		this.SQLType = DBEnumType.getEnumType(
				StringUtils.parseInt(Configuration.readConfigString("mes.server.sql.type", "config/config")));
	}

	public BaseDAO(NamedParameterJdbcTemplate nameJdbcTemplate, DBEnumType wSQLTypes) {
		this.nameJdbcTemplate = nameJdbcTemplate;
		this.SQLType = wSQLTypes;
	}

	public BaseDAO(DBEnumType wSQLTypes) {
		nameJdbcTemplate = DBHelper.getTemplate(wSQLTypes);
		this.SQLType = wSQLTypes;
	}

	protected Object GetMapObject(Map<String, Object> wMap, String wKey) {
		Object wResult = null;
		try {
			if (wMap == null || wMap.size() < 1 || wKey == null || wKey.isEmpty())
				return wResult;

			if (wMap.containsKey(wKey))
				wResult = wMap.get(wKey);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	protected ServiceResult<String> GetDataBaseName(MESDBSource wDataBaseFiled, DBEnumType wSQLTypeFiled) {
		ServiceResult<String> wResult = MESServer.MES_GetDatabaseName(0, wDataBaseFiled);
		if (wResult.ErrorCode == 0) {
			wResult.Result = ChangeDataBaseName(wSQLTypeFiled, wResult.Result);
		}
		return wResult;
	}

	private String ChangeDataBaseName(String wDBName) {
		switch (SQLType) {
		case MySQL:

			break;
		case SQLServer:
			wDBName = wDBName + ".dbo";
			break;
		case Oracle:

			break;
		case Access:

			break;
		default:
			break;
		}
		return wDBName;
	}

	private String ChangeDataBaseName(DBEnumType wDBEnumType, String wDBName) {
		switch (wDBEnumType) {
		case MySQL:

			break;
		case SQLServer:
			wDBName = wDBName + ".dbo";
			break;
		case Oracle:

			break;
		case Access:

			break;
		default:
			break;
		}
		return wDBName;
	}

	protected ServiceResult<String> GetDataBaseName(int wCompanyID, MESDBSource wMESDBSource, int wLoginID,
			int wFunctionID) {

		ServiceResult<String> wResult = MESServer.MES_GetDatabaseName(wCompanyID, wMESDBSource, wLoginID, wFunctionID);
		if (wResult.ErrorCode == 0) {
			wResult.Result = ChangeDataBaseName(wResult.Result);
		}

		return wResult;
	}

	protected ServiceResult<String> GetDataBaseName(int wCompanyID, MESDBSource wMESDBSource, int wLoginID,
			int wFunctionID, DBEnumType wDBEnumType) {

		ServiceResult<String> wResult = MESServer.MES_GetDatabaseName(wCompanyID, wMESDBSource, wLoginID, wFunctionID);
		if (wResult.ErrorCode == 0) {
			wResult.Result = ChangeDataBaseName(wDBEnumType, wResult.Result);

		}

		return wResult;
	}

	protected ServiceResult<String> GetDataBaseName(int wCompanyID, MESDBSource wMESDBSource) {

		ServiceResult<String> wResult = MESServer.MES_GetDatabaseName(wCompanyID, wMESDBSource);
		if (wResult.ErrorCode == 0) {
			wResult.Result = ChangeDataBaseName(wResult.Result);

		}

		return wResult;
	}

	protected ServiceResult<String> GetDataBaseName(BMSEmployee wLoginUser, MESDBSource wMESDBSource) {

		ServiceResult<String> wResult = MESServer.MES_GetDatabaseName(wLoginUser.getCompanyID(), wMESDBSource);
		if (wResult.ErrorCode == 0) {
			wResult.Result = ChangeDataBaseName(wResult.Result);
		}
		return wResult;
	}

	protected ServiceResult<String> GetDataBaseName(BMSEmployee wLoginUser, MESDBSource wMESDBSource,
			DBEnumType wDBEnumType) {

		ServiceResult<String> wResult = MESServer.MES_GetDatabaseName(wLoginUser.getCompanyID(), wMESDBSource);
		if (wResult.ErrorCode == 0) {
			wResult.Result = ChangeDataBaseName(wDBEnumType, wResult.Result);
		}
		return wResult;
	}

	protected ServiceResult<String> GetDataBaseName(BMSEmployee wLoginUser, MESDBSource wMESDBSource, int wFunctionID) {

		ServiceResult<String> wResult = MESServer.MES_GetDatabaseName(wLoginUser.getCompanyID(), wMESDBSource,
				wLoginUser.getID(), wFunctionID);
		if (wResult.ErrorCode == 0) {
			wResult.Result = ChangeDataBaseName(wResult.Result);
		}
		return wResult;
	}

	protected ServiceResult<String> GetDataBaseName(BMSEmployee wLoginUser, MESDBSource wMESDBSource, int wFunctionID,
			DBEnumType wDBEnumType) {

		ServiceResult<String> wResult = MESServer.MES_GetDatabaseName(wLoginUser.getCompanyID(), wMESDBSource,
				wLoginUser.getID(), wFunctionID);
		if (wResult.ErrorCode == 0) {
			wResult.Result = ChangeDataBaseName(wDBEnumType, wResult.Result);
		}
		return wResult;
	}

	protected ServiceResult<String> GetDataBaseName(int wCompanyID, MESDBSource wMESDBSource, DBEnumType wDBEnumType) {

		ServiceResult<String> wResult = MESServer.MES_GetDatabaseName(wCompanyID, wMESDBSource);
		if (wResult.ErrorCode == 0) {
			wResult.Result = ChangeDataBaseName(wDBEnumType, wResult.Result);

		}

		return wResult;
	}

	protected String MysqlChangeToSqlServer(String wMySqlString) {
		String wResult = "";
		try {
			StringBuffer wStringBuffer = new StringBuffer("");

			Matcher wMatcher = Pattern
					.compile("SELECT\\s+LAST_INSERT_ID\\(\\)(\\s+as\\s+ID)?\\s*\\;", Pattern.CASE_INSENSITIVE)
					.matcher(wMySqlString);
			if (wMatcher.matches()) {
				wMySqlString = wMatcher.replaceAll("");

				wMatcher = Pattern.compile("\\)\\s*VALUES\\s*\\(", Pattern.CASE_INSENSITIVE).matcher(wMySqlString);
				if (wMatcher.matches()) {
					wMySqlString = wMatcher.replaceAll(") output inserted.* \\n VALUES (");
				}
			}
			wMySqlString = wMySqlString.replaceAll("now()", "GETDATE()");

			wMatcher = Pattern.compile("\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE).matcher(wMySqlString);

			if (wMatcher.matches()) {
				wMatcher = Pattern.compile("\\s+limit\\s+(?<Num>\\d+)\\s*\\,?(?<Num2>\\d*)", Pattern.CASE_INSENSITIVE)
						.matcher(wMySqlString);
				if (wMatcher.matches()) {
					wMySqlString = wMatcher.replaceAll("");

					wMatcher = Pattern.compile("\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE).matcher(wMySqlString);

					wStringBuffer.setLength(0);

					while (wMatcher.find()) {
						wMatcher.appendReplacement(wStringBuffer,
								StringUtils.Format(" SELECT Top ({0}) ", wMatcher.group("Num")));
					}

					wMatcher.appendTail(wStringBuffer);
					wMySqlString = wStringBuffer.toString();
				}
			}

			wMatcher = Pattern.compile("\\`(?<Column>[a-zA-Z]+[a-zA-Z0-9_]+)\\`", Pattern.CASE_INSENSITIVE)
					.matcher(wMySqlString);
			wStringBuffer.setLength(0);

			while (wMatcher.find()) {
				wMatcher.appendReplacement(wStringBuffer, wMatcher.group("Column"));
			}

			wMatcher.appendTail(wStringBuffer);
			wMySqlString = wStringBuffer.toString();

			wMatcher = Pattern.compile(
					"str_to_date\\(\\s*(?<STR>[\\']{1,2}2010\\-01\\-01[\\']{1,2})\\s*\\,\\s*[\\']{1,2}\\%Y\\-\\%m\\-\\%d\\s*\\%H[\\']{1,2}\\)",
					Pattern.CASE_INSENSITIVE).matcher(wMySqlString);
			wStringBuffer.setLength(0);

			while (wMatcher.find()) {
				wMatcher.appendReplacement(wStringBuffer,
						String.format("cast( %s  as datetime)", wMatcher.group("STR")));
			}

			wMatcher.appendTail(wStringBuffer);
			wMySqlString = wStringBuffer.toString();

			wResult = wMySqlString;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}

		return wResult;
	}

	protected String DMLChange(String wMySqlString, DBEnumType wSQLTypeFiled) {
		switch (wSQLTypeFiled) {
		case MySQL:
			wMySqlString = wMySqlString.replaceAll("\\\\", "\\\\\\\\");
			break;
		case SQLServer:
			wMySqlString = this.MysqlChangeToSqlServer(wMySqlString);
			break;
		case Oracle:

			break;
		case Access:

			break;
		default:
			break;
		}
		return wMySqlString;
	}

	protected String DMLChange(String wMySqlString) {
		switch (SQLType) {
		case MySQL:
			wMySqlString = wMySqlString.replaceAll("\\\\", "\\\\\\\\");
			break;
		case SQLServer:
			wMySqlString = this.MysqlChangeToSqlServer(wMySqlString);
			break;
		case Oracle:

			break;
		case Access:

			break;
		default:
			break;
		}
		return wMySqlString;
	}

	/**
	 * SelectAll?????????????????????
	 * 
	 * @param wSQL      ??????sql?????? ???:??????????????????
	 * @param wParamMap sql?????????
	 * @param clazz     ?????????????????? ??????sql??????????????????????????????????????????
	 * @return
	 */
	protected <T> List<T> QueryForList(String wSQL, Map<String, Object> wParamMap, Class<T> clazz) {
		List<T> wResult = new ArrayList<T>();
		try {

			List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			wResult = JSON.parseArray(JSON.toJSONString(wQueryResultList), clazz);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * Select?????????????????????
	 * 
	 * @param wSQL      ??????sql?????? ???:??????????????????
	 * @param wParamMap sql?????????
	 * @param clazz     ?????????????????? ??????sql?????????????????????????????????????????? ?????????????????????
	 * @return
	 */
	protected <T> T QueryForObject(String wSQL, Map<String, Object> wParamMap, Class<T> clazz) {
		T wResult = null;
		try {

			Map<String, Object> wQueryResult = nameJdbcTemplate.queryForMap(wSQL, wParamMap);

			wResult = JSON.parseObject(JSON.toJSONString(wQueryResult), clazz);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	public synchronized boolean Execute(List<String> wSqlList) {
		boolean wResult = false;
		if (wSqlList == null || wSqlList.size() <= 0)
			return wResult;

		wSqlList.removeIf(p -> StringUtils.isEmpty(p));

		if (wSqlList.size() <= 0)
			return wResult;

		Connection connection = null;
		try {
			TransactionSynchronizationManager.initSynchronization();
			connection = DBHelper.getConnection();

			connection.setAutoCommit(false);

			Statement stat = connection.createStatement();

			for (String string : wSqlList) {

				stat.addBatch(string);
			}
			stat.executeBatch();
			// ??????????????????????????????insert??????????????????????????????????????????
			connection.commit();
			wResult = true;
		} catch (SQLException e) {
			logger.error("BaseDAO Execute commit", e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				logger.error("BaseDAO Execute rollback", e1);
			}
		} finally {
			try {
				TransactionSynchronizationManager.clearSynchronization();
			} catch (IllegalStateException e) {
				logger.error("BaseDAO Execute clearSynchronization", e);
			}
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				logger.error("BaseDAO Execute setAutoCommit", e);
			}
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error("BaseDAO Execute close", e);
			}
		}

		return wResult;
	}

	public void ExecuteSqlTransaction(List<String> wSqlList) {
		try {
			// ???????????????SQL
			wSqlList.removeIf(p -> StringUtils.isEmpty(p));

			String wSQL = "";
			for (String wString : wSqlList) {

				wSQL = this.DMLChange(wString, SQLType);
				nameJdbcTemplate.update(wSQL, new HashMap<String, Object>());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	public void ExecuteSqlTransaction(String wSqlString) {
		try {
			String wSQL = this.DMLChange(wSqlString, SQLType);
			nameJdbcTemplate.update(wSQL, new HashMap<String, Object>());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
	}

}
