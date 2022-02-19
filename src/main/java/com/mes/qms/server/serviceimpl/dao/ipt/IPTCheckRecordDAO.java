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

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.record.IPTExportCheckRecord;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class IPTCheckRecordDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTCheckRecordDAO.class);

	private static IPTCheckRecordDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTCheckRecord
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTExportCheckRecord wIPTCheckRecord, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTCheckRecord == null)
				return 0;

			String wSQL = "";
			if (wIPTCheckRecord.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.ipt_checkrecord(Code,OperateID,CreateTime,OrderID,PartNo,"
						+ "TotalSize,TotalProgress,TotalTip,ItemSize,ItemProgress,ItemTip) "
						+ "VALUES(:Code,:OperateID,:CreateTime,:OrderID,:PartNo,:TotalSize,:TotalProgress,"
						+ ":TotalTip,:ItemSize,:ItemProgress,:ItemTip);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.ipt_checkrecord SET Code = :Code,OperateID = :OperateID,"
						+ "CreateTime = :CreateTime,OrderID = :OrderID,PartNo = :PartNo,"
						+ "TotalSize = :TotalSize,TotalProgress = :TotalProgress,TotalTip = :TotalTip,"
						+ "ItemSize = :ItemSize,ItemProgress = :ItemProgress,ItemTip = :ItemTip WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTCheckRecord.ID);
			wParamMap.put("Code", wIPTCheckRecord.Code);
			wParamMap.put("OperateID", wIPTCheckRecord.OperateID);
			wParamMap.put("CreateTime", wIPTCheckRecord.CreateTime);
			wParamMap.put("OrderID", wIPTCheckRecord.OrderID);
			wParamMap.put("PartNo", wIPTCheckRecord.PartNo);
			wParamMap.put("TotalSize", wIPTCheckRecord.TotalSize);
			wParamMap.put("TotalProgress", wIPTCheckRecord.TotalProgress);
			wParamMap.put("TotalTip", wIPTCheckRecord.TotalTip);
			wParamMap.put("ItemSize", wIPTCheckRecord.ItemSize);
			wParamMap.put("ItemProgress", wIPTCheckRecord.ItemProgress);
			wParamMap.put("ItemTip", wIPTCheckRecord.ItemTip);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTCheckRecord.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTCheckRecord.setID(wResult);
			} else {
				wResult = wIPTCheckRecord.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTExportCheckRecord> wList,
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
			for (IPTExportCheckRecord wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_checkrecord WHERE ID IN({0}) ;",
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
	public IPTExportCheckRecord SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTExportCheckRecord wResult = new IPTExportCheckRecord();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTExportCheckRecord> wList = SelectList(wLoginUser, wID, "", -1, -1, null, null, wErrorCode);
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
	public List<IPTExportCheckRecord> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wOperateID,
			int wOrderID, Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<IPTExportCheckRecord> wResultList = new ArrayList<IPTExportCheckRecord>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
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

			String wSQL = MessageFormat.format("SELECT * FROM {0}.ipt_checkrecord WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wCode is null or :wCode = '''' or :wCode = Code ) "
					+ "and ( :wOperateID <= 0 or :wOperateID = OperateID ) "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  CreateTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  CreateTime ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = OrderID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wOperateID", wOperateID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTExportCheckRecord wItem = new IPTExportCheckRecord();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.OperateID = StringUtils.parseInt(wReader.get("OperateID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.TotalSize = StringUtils.parseInt(wReader.get("TotalSize"));
				wItem.TotalProgress = StringUtils.parseInt(wReader.get("TotalProgress"));
				wItem.TotalTip = StringUtils.parseString(wReader.get("TotalTip"));
				wItem.ItemSize = StringUtils.parseInt(wReader.get("ItemSize"));
				wItem.ItemProgress = StringUtils.parseInt(wReader.get("ItemProgress"));
				wItem.ItemTip = StringUtils.parseString(wReader.get("ItemTip"));

				wItem.Operator = QMSConstants.GetBMSEmployeeName(wItem.OperateID);
				wItem.IPTCheckResultList = IPTCheckResultDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID,
						wErrorCode);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTCheckRecordDAO() {
		super();
	}

	public static IPTCheckRecordDAO getInstance() {
		if (Instance == null)
			Instance = new IPTCheckRecordDAO();
		return Instance;
	}
}
