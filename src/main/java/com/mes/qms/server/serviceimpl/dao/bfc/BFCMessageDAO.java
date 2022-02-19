package com.mes.qms.server.serviceimpl.dao.bfc;

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
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class BFCMessageDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(BFCMessageDAO.class);

	private static BFCMessageDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wBFCMessage
	 * @return
	 */
	public long Update(BMSEmployee wLoginUser, BFCMessage wBFCMessage, OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wBFCMessage == null)
				return 0;

			String wSQL = "";
			if (wBFCMessage.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.bfc_message(ResponsorID,Type,MessageText,Title,CreateTime,Active,EditTime,ModuleID,MessageID,StationID,StationNo,CompanyID,ShiftID,StepID,SendStatus) VALUES(:ResponsorID,:Type,:MessageText,:Title,:CreateTime,:Active,:EditTime,:ModuleID,:MessageID,:StationID,:StationNo,:CompanyID,:ShiftID,:StepID,:SendStatus);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.bfc_message SET ResponsorID = :ResponsorID,Type = :Type,MessageText = :MessageText,Title = :Title,CreateTime = :CreateTime,Active = :Active,EditTime = :EditTime,ModuleID = :ModuleID,MessageID = :MessageID,StationID = :StationID,StationNo = :StationNo,CompanyID = :CompanyID,ShiftID = :ShiftID,StepID = :StepID,SendStatus = :SendStatus WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wBFCMessage.ID);
			wParamMap.put("ResponsorID", wBFCMessage.ResponsorID);
			wParamMap.put("Type", wBFCMessage.Type);
			wParamMap.put("MessageText", wBFCMessage.MessageText);
			wParamMap.put("Title", wBFCMessage.Title);
			wParamMap.put("CreateTime", wBFCMessage.CreateTime);
			wParamMap.put("Active", wBFCMessage.Active);
			wParamMap.put("EditTime", wBFCMessage.EditTime);
			wParamMap.put("ModuleID", wBFCMessage.ModuleID);
			wParamMap.put("MessageID", wBFCMessage.MessageID);
			wParamMap.put("StationID", wBFCMessage.StationID);
			wParamMap.put("StationNo", wBFCMessage.StationNo);
			wParamMap.put("CompanyID", wBFCMessage.CompanyID);
			wParamMap.put("ShiftID", wBFCMessage.ShiftID);
			wParamMap.put("StepID", wBFCMessage.StepID);
			wParamMap.put("SendStatus", wBFCMessage.SendStatus);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wBFCMessage.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wBFCMessage.setID(wResult);
			} else {
				wResult = wBFCMessage.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<BFCMessage> wList,
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
			for (BFCMessage wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.bfc_message WHERE ID IN({0}) ;",
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
	public BFCMessage SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		BFCMessage wResult = new BFCMessage();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<BFCMessage> wList = SelectList(wLoginUser, wID, wErrorCode);
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
	public List<BFCMessage> SelectList(BMSEmployee wLoginUser, long wID, OutResult<Integer> wErrorCode) {
		List<BFCMessage> wResultList = new ArrayList<BFCMessage>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.bfc_message WHERE  1=1  and ( :wID <= 0 or :wID = ID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				BFCMessage wItem = new BFCMessage();

				wItem.ID = StringUtils.parseLong(wReader.get("ID"));
				wItem.ResponsorID = StringUtils.parseInt(wReader.get("ResponsorID"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));
				wItem.MessageText = StringUtils.parseString(wReader.get("MessageText"));
				wItem.Title = StringUtils.parseString(wReader.get("Title"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.Active = StringUtils.parseInt(wReader.get("Active"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.ModuleID = StringUtils.parseInt(wReader.get("ModuleID"));
				wItem.MessageID = StringUtils.parseLong(wReader.get("MessageID"));
				wItem.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wItem.StationNo = StringUtils.parseString(wReader.get("StationNo"));
				wItem.CompanyID = StringUtils.parseInt(wReader.get("CompanyID"));
				wItem.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wItem.StepID = StringUtils.parseInt(wReader.get("StepID"));
				wItem.SendStatus = StringUtils.parseInt(wReader.get("SendStatus"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 批量激活或禁用
	 */
	public ServiceResult<Integer> Active(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIDList == null || wIDList.size() <= 0)
				return wResult;
			if (wActive != 0 && wActive != 1)
				return wResult;
			for (Integer wItem : wIDList) {
				BFCMessage wBFCMessage = SelectByID(wLoginUser, wItem, wErrorCode);
				if (wBFCMessage == null || wBFCMessage.ID <= 0)
					continue;
				wBFCMessage.Active = wActive;
				long wID = Update(wLoginUser, wBFCMessage, wErrorCode);
				if (wID <= 0)
					break;
			}
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	private BFCMessageDAO() {
		super();
	}

	public static BFCMessageDAO getInstance() {
		if (Instance == null)
			Instance = new BFCMessageDAO();
		return Instance;
	}
}
