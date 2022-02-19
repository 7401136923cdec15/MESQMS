package com.mes.qms.server.serviceimpl.dao.sfc;

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
import com.mes.qms.server.service.po.sfc.SFCTaskRecord;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class SFCTaskRecordDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCTaskRecordDAO.class);

	private static SFCTaskRecordDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCTaskRecord
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCTaskRecord wSFCTaskRecord, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCTaskRecord == null)
				return 0;

			String wSQL = "";
			if (wSFCTaskRecord.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.sfc_taskrecord(OrderID,PartID,Status,CreateID,Creator,CreateTime) VALUES(:OrderID,:PartID,:Status,:CreateID,:Creator,:CreateTime);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.sfc_taskrecord SET OrderID = :OrderID,PartID = :PartID,Status = :Status,CreateID = :CreateID,Creator = :Creator,CreateTime = :CreateTime WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCTaskRecord.ID);
			wParamMap.put("OrderID", wSFCTaskRecord.OrderID);
			wParamMap.put("PartID", wSFCTaskRecord.PartID);
			wParamMap.put("Status", wSFCTaskRecord.Status);
			wParamMap.put("CreateID", wSFCTaskRecord.CreateID);
			wParamMap.put("Creator", wSFCTaskRecord.Creator);
			wParamMap.put("CreateTime", wSFCTaskRecord.CreateTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCTaskRecord.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCTaskRecord.setID(wResult);
			} else {
				wResult = wSFCTaskRecord.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCTaskRecord> wList,
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
			for (SFCTaskRecord wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_taskrecord WHERE ID IN({0}) ;",
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
	public SFCTaskRecord SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCTaskRecord wResult = new SFCTaskRecord();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCTaskRecord> wList = SelectList(wLoginUser, wID, -1, -1, -1, wErrorCode);
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
	public List<SFCTaskRecord> SelectList(BMSEmployee wLoginUser, int wID, int wOrderID, int wPartID, int wStatus,
			OutResult<Integer> wErrorCode) {
		List<SFCTaskRecord> wResultList = new ArrayList<SFCTaskRecord>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.sfc_taskrecord WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
					+ "and ( :wPartID <= 0 or :wPartID = PartID ) " + "and ( :wStatus < 0 or :wStatus = Status );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wPartID);
			wParamMap.put("wStatus", wStatus);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCTaskRecord wItem = new SFCTaskRecord();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.Creator = StringUtils.parseString(wReader.get("Creator"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private SFCTaskRecordDAO() {
		super();
	}

	public static SFCTaskRecordDAO getInstance() {
		if (Instance == null)
			Instance = new SFCTaskRecordDAO();
		return Instance;
	}
}
