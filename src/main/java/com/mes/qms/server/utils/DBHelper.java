package com.mes.qms.server.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mes.qms.server.service.mesenum.DBEnumType;
import com.mes.qms.server.service.utils.StringUtils;

public class DBHelper {
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(DBHelper.class);

	private static DataSource MySqlDataSource = null;

	private static DataSource GetMySqlDataSource() {
		if (MySqlDataSource == null)
			MySqlDataSource = new ComboPooledDataSource("Mysql_dataSource");

		return MySqlDataSource;
	}

	private static DataSource AccessDataSource = null;

	private static DataSource GetAccessDataSource() {
		if (AccessDataSource == null)
			AccessDataSource = new ComboPooledDataSource("Access_dataSource");

		return AccessDataSource;
	}

	private static DataSource OracleDataSource = null;

	private static DataSource GetOracleDataSource() {
		if (OracleDataSource == null)
			OracleDataSource = new ComboPooledDataSource("Oracle_dataSource");

		return OracleDataSource;
	}

	private static DataSource SQLServerDataSource = null;

	private static DataSource GetSQLServerDataSource() {
		if (SQLServerDataSource == null)
			SQLServerDataSource = new ComboPooledDataSource("SQLServer_dataSource");

		return SQLServerDataSource;
	}

	public static Connection getConnection(DBEnumType wDBType) throws SQLException {

		DataSource wDataSource = getDataSource(wDBType);
		return wDataSource.getConnection();

	}

	public static DataSource getDataSource() {
		DataSource wResult = null;

		switch (DBType) {
		case Default:
			wResult = GetMySqlDataSource();
			break;
		case Access:
			wResult = GetAccessDataSource();
			break;
		case MySQL:
			wResult = GetMySqlDataSource();
			break;
		case Oracle:
			wResult = GetOracleDataSource();
			break;
		case SQLServer:
			wResult = GetSQLServerDataSource();
			break;
		default:
			wResult = GetMySqlDataSource();
			break;
		}
		return wResult;
	}
	public static DataSource getDataSource(DBEnumType wDBType) {
		DataSource wResult = null;

		switch (wDBType) {
		case Default:
			wResult = GetMySqlDataSource();
			break;
		case Access:
			wResult = GetAccessDataSource();
			break;
		case MySQL:
			wResult = GetMySqlDataSource();
			break;
		case Oracle:
			wResult = GetOracleDataSource();
			break;
		case SQLServer:
			wResult = GetSQLServerDataSource();
			break;
		default:
			wResult = GetMySqlDataSource();
			break;
		}
		return wResult;
	}
	
	public static Connection getConnection() throws SQLException {

		DataSource wDataSource = getDataSource();
		return wDataSource.getConnection();

	}

	private static DBEnumType DBType = DBEnumType
			.getEnumType(StringUtils.parseInt(Configuration.readConfigString("mes.server.sql.type", "config/config")));

	public static NamedParameterJdbcTemplate getTemplate() {

		NamedParameterJdbcTemplate jdbcTemplate = null;

		switch (DBType) {
		case Default:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetMySqlDataSource());
			break;
		case Access:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetAccessDataSource());
			break;
		case MySQL:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetMySqlDataSource());
			break;
		case Oracle:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetOracleDataSource());
			break;
		case SQLServer:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetSQLServerDataSource());
			break;
		default:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetMySqlDataSource());
			break;
		}
		return jdbcTemplate;
	}

	public static NamedParameterJdbcTemplate getTemplate(DBEnumType wDBEnumType) {
		NamedParameterJdbcTemplate jdbcTemplate = null;

		switch (wDBEnumType) {
		case Default:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetMySqlDataSource());
			break;
		case Access:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetAccessDataSource());
			break;
		case MySQL:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetMySqlDataSource());
			break;
		case Oracle:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetOracleDataSource());
			break;
		case SQLServer:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetSQLServerDataSource());
			break;
		default:
			jdbcTemplate = new NamedParameterJdbcTemplate(GetMySqlDataSource());
			break;
		}
		return jdbcTemplate;
	}

}
