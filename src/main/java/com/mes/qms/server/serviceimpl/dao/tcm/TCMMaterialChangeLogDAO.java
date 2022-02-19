package com.mes.qms.server.serviceimpl.dao.tcm;

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

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.tcm.TCMMaterialChangeLog;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class TCMMaterialChangeLogDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(TCMMaterialChangeLogDAO.class);

	private static TCMMaterialChangeLogDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wTCMMaterialChangeLog
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, TCMMaterialChangeLog wTCMMaterialChangeLog,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wTCMMaterialChangeLog == null)
				return 0;

			String wSQL = "";
			if (wTCMMaterialChangeLog.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.tcm_materialchangelog(CreateTime,CreateID,OrderIDList,"
						+ "PartNoList,ProductID,LineID,CustomerID,ChangeFormNo,ChangeFormUri,ChangeUser,ChangeType,ShowStatus,Customer) "
						+ "VALUES(:CreateTime,:CreateID,:OrderIDList,:PartNoList,"
						+ ":ProductID,:LineID,:CustomerID,:ChangeFormNo,:ChangeFormUri,:ChangeUser,:ChangeType,:ShowStatus,:Customer);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.tcm_materialchangelog SET CreateTime = :CreateTime,"
						+ "CreateID = :CreateID," + "OrderIDList = :OrderIDList,PartNoList = :PartNoList,"
						+ "ProductID=:ProductID,LineID=:LineID," + "CustomerID=:CustomerID,ChangeFormNo=:ChangeFormNo,"
						+ "ChangeFormUri=:ChangeFormUri,ChangeUser=:ChangeUser,"
						+ "ChangeType=:ChangeType,ShowStatus=:ShowStatus,Customer=:Customer WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wTCMMaterialChangeLog.ID);
			wParamMap.put("CreateTime", wTCMMaterialChangeLog.CreateTime);
			wParamMap.put("CreateID", wTCMMaterialChangeLog.CreateID);
			wParamMap.put("OrderIDList", wTCMMaterialChangeLog.OrderIDList);
			wParamMap.put("PartNoList", wTCMMaterialChangeLog.PartNoList);
			wParamMap.put("ProductID", wTCMMaterialChangeLog.ProductID);
			wParamMap.put("LineID", wTCMMaterialChangeLog.LineID);
			wParamMap.put("CustomerID", wTCMMaterialChangeLog.CustomerID);
			wParamMap.put("ChangeFormNo", wTCMMaterialChangeLog.ChangeFormNo);
			wParamMap.put("ChangeFormUri", wTCMMaterialChangeLog.ChangeFormUri);
			wParamMap.put("ChangeUser", wTCMMaterialChangeLog.ChangeUser);
			wParamMap.put("ChangeType", wTCMMaterialChangeLog.ChangeType);
			wParamMap.put("ShowStatus", wTCMMaterialChangeLog.ShowStatus);
			wParamMap.put("Customer", wTCMMaterialChangeLog.Customer);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wTCMMaterialChangeLog.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wTCMMaterialChangeLog.setID(wResult);
			} else {
				wResult = wTCMMaterialChangeLog.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<TCMMaterialChangeLog> wList,
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
			for (TCMMaterialChangeLog wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.tcm_materialchangelog WHERE ID IN({0}) ;",
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
	 */
	public TCMMaterialChangeLog SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		TCMMaterialChangeLog wResult = new TCMMaterialChangeLog();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<TCMMaterialChangeLog> wList = SelectList(wLoginUser, wID, null, null, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
			wResult.ItemList = TCMMaterialChangeItemsDAO.getInstance().SelectList(wLoginUser, -1, wResult.ID, -1,
					wErrorCode);
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 查单条
	 */
	public TCMMaterialChangeLog SelectByIDNoSub(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		TCMMaterialChangeLog wResult = new TCMMaterialChangeLog();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<TCMMaterialChangeLog> wList = SelectList(wLoginUser, wID, null, null, wErrorCode);
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
	public List<TCMMaterialChangeLog> SelectList(BMSEmployee wLoginUser, int wID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<TCMMaterialChangeLog> wResultList = new ArrayList<TCMMaterialChangeLog>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1, 0, 0, 0);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}
			if (wStartTime.compareTo(wEndTime) > 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.tcm_materialchangelog WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  CreateTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  CreateTime );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				TCMMaterialChangeLog wItem = new TCMMaterialChangeLog();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.OrderIDList = StringUtils.parseString(wReader.get("OrderIDList"));
				wItem.PartNoList = StringUtils.parseString(wReader.get("PartNoList"));
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wItem.ChangeFormNo = StringUtils.parseString(wReader.get("ChangeFormNo"));
				wItem.ChangeFormUri = StringUtils.parseString(wReader.get("ChangeFormUri"));
				wItem.ChangeUser = StringUtils.parseString(wReader.get("ChangeUser"));
				wItem.ChangeType = StringUtils.parseString(wReader.get("ChangeType"));
				wItem.ShowStatus = StringUtils.parseInt(wReader.get("ShowStatus"));
				wItem.Customer = StringUtils.parseString(wReader.get("Customer"));

				wItem.ProductNo = QMSConstants.GetFPCProductNo(wItem.ProductID);
				wItem.LineName = QMSConstants.GetFMCLineName(wItem.LineID);
				if (StringUtils.isEmpty(wItem.Customer)) {
					wItem.Customer = QMSConstants.GetCRMCustomerName(wItem.CustomerID);
				}

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private TCMMaterialChangeLogDAO() {
		super();
	}

	public static TCMMaterialChangeLogDAO getInstance() {
		if (Instance == null)
			Instance = new TCMMaterialChangeLogDAO();
		return Instance;
	}
}
