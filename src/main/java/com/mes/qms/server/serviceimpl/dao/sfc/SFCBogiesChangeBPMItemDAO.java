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
import com.mes.qms.server.service.po.sfc.SFCBogiesChangeBPMItem;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.LOCOAPSServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class SFCBogiesChangeBPMItemDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCBogiesChangeBPMItemDAO.class);

	private static SFCBogiesChangeBPMItemDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCBogiesChangeBPMItem
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCBogiesChangeBPMItem wSFCBogiesChangeBPMItem,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCBogiesChangeBPMItem == null)
				return 0;

			String wSQL = "";
			if (wSFCBogiesChangeBPMItem.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.sfc_bogieschangebpmitem(TaskID,BodyOrderID,BogiesOrderID,"
						+ "BodyBogiesNo,BogiesBogiesNo,BodyDriveNo,BogiesDriveNo) " + "VALUES(:TaskID,:BodyOrderID,"
						+ ":BogiesOrderID,:BodyBogiesNo,:BogiesBogiesNo,:BodyDriveNo,:BogiesDriveNo);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat
						.format("UPDATE {0}.sfc_bogieschangebpmitem SET TaskID = :TaskID,BodyOrderID = :BodyOrderID,"
								+ "BogiesOrderID = :BogiesOrderID,BodyBogiesNo=:BodyBogiesNo,"
								+ "BogiesBogiesNo=:BogiesBogiesNo,BodyDriveNo=:BodyDriveNo,"
								+ "BogiesDriveNo=:BogiesDriveNo WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCBogiesChangeBPMItem.ID);
			wParamMap.put("TaskID", wSFCBogiesChangeBPMItem.TaskID);
			wParamMap.put("BodyOrderID", wSFCBogiesChangeBPMItem.BodyOrderID);
			wParamMap.put("BogiesOrderID", wSFCBogiesChangeBPMItem.BogiesOrderID);
			wParamMap.put("BodyBogiesNo", wSFCBogiesChangeBPMItem.BodyBogiesNo);
			wParamMap.put("BogiesBogiesNo", wSFCBogiesChangeBPMItem.BogiesBogiesNo);
			wParamMap.put("BodyDriveNo", wSFCBogiesChangeBPMItem.BodyDriveNo);
			wParamMap.put("BogiesDriveNo", wSFCBogiesChangeBPMItem.BogiesDriveNo);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCBogiesChangeBPMItem.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCBogiesChangeBPMItem.setID(wResult);
			} else {
				wResult = wSFCBogiesChangeBPMItem.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCBogiesChangeBPMItem> wList,
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
			for (SFCBogiesChangeBPMItem wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_bogieschangebpmitem WHERE ID IN({0}) ;",
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
	public SFCBogiesChangeBPMItem SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCBogiesChangeBPMItem wResult = new SFCBogiesChangeBPMItem();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCBogiesChangeBPMItem> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<SFCBogiesChangeBPMItem> SelectList(BMSEmployee wLoginUser, int wID, int wTaskID,
			OutResult<Integer> wErrorCode) {
		List<SFCBogiesChangeBPMItem> wResultList = new ArrayList<SFCBogiesChangeBPMItem>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.sfc_bogieschangebpmitem WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wTaskID <= 0 or :wTaskID = TaskID );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wTaskID", wTaskID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCBogiesChangeBPMItem wItem = new SFCBogiesChangeBPMItem();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.TaskID = StringUtils.parseInt(wReader.get("TaskID"));
				wItem.BodyOrderID = StringUtils.parseInt(wReader.get("BodyOrderID"));
				wItem.BogiesOrderID = StringUtils.parseInt(wReader.get("BogiesOrderID"));

				wItem.BodyBogiesNo = StringUtils.parseString(wReader.get("BodyBogiesNo"));
				wItem.BogiesBogiesNo = StringUtils.parseString(wReader.get("BogiesBogiesNo"));
				wItem.BodyDriveNo = StringUtils.parseString(wReader.get("BodyDriveNo"));
				wItem.BogiesDriveNo = StringUtils.parseString(wReader.get("BogiesDriveNo"));

				wItem.BodyOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wItem.BodyOrderID)
						.Info(OMSOrder.class);
				wItem.BogiesOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wItem.BogiesOrderID)
						.Info(OMSOrder.class);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private SFCBogiesChangeBPMItemDAO() {
		super();
	}

	public static SFCBogiesChangeBPMItemDAO getInstance() {
		if (Instance == null)
			Instance = new SFCBogiesChangeBPMItemDAO();
		return Instance;
	}
}
