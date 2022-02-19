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
import com.mes.qms.server.service.po.ipt.IPTTool;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class IPTToolDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTToolDAO.class);

	private static IPTToolDAO Instance = null;

	/**
	 * 更新或新增
	 * 
	 * @param wIPTTool
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTTool wIPTTool, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTTool == null)
				return 0;

			String wSQL = "";
			if (wIPTTool.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.ipt_tool(StandardID,OrderNum,Name,Modal,Number,UnitID,"
						+ "CreateID,CreateTime,EditID,EditTime) VALUES(:StandardID,:OrderNum,:Name,:Modal,"
						+ ":Number,:UnitID,:CreateID,now(),:EditID,now());", wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.ipt_tool SET StandardID = :StandardID,OrderNum = :OrderNum,Name = :Name,"
								+ "Modal = :Modal,Number = :Number,UnitID = :UnitID,CreateID = :CreateID,"
								+ "CreateTime = :CreateTime,EditID = :EditID,EditTime = now() WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTTool.ID);
			wParamMap.put("StandardID", wIPTTool.StandardID);
			wParamMap.put("OrderNum", wIPTTool.OrderNum);
			wParamMap.put("Name", wIPTTool.Name);
			wParamMap.put("Modal", wIPTTool.Modal);
			wParamMap.put("Number", wIPTTool.Number);
			wParamMap.put("UnitID", wIPTTool.UnitID);
			wParamMap.put("CreateID", wIPTTool.CreateID);
			wParamMap.put("CreateTime", wIPTTool.CreateTime);
			wParamMap.put("EditID", wIPTTool.EditID);
			wParamMap.put("EditTime", wIPTTool.EditTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTTool.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTTool.setID(wResult);
			} else {
				wResult = wIPTTool.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTTool> wList,
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
			for (IPTTool wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.ipt_tool WHERE ID IN({0}) ;", String.join(",", wIDList),
					wInstance.Result);
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 主键查询
	 * 
	 * @return
	 */
	public IPTTool SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTTool wResult = new IPTTool();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTTool> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	 * 条件查询
	 * 
	 * @return
	 */
	public List<IPTTool> SelectList(BMSEmployee wLoginUser, int wID, int wStandardID, OutResult<Integer> wErrorCode) {
		List<IPTTool> wResultList = new ArrayList<IPTTool>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.ipt_tool WHERE  1=1  and ( :wID <= 0 or :wID = ID ) "
					+ "and ( :wStandardID <= 0 or :wStandardID = StandardID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wStandardID", wStandardID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTTool wItem = new IPTTool();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.StandardID = StringUtils.parseInt(wReader.get("StandardID"));
				wItem.OrderNum = StringUtils.parseInt(wReader.get("OrderNum"));
				wItem.Name = StringUtils.parseString(wReader.get("Name"));
				wItem.Modal = StringUtils.parseString(wReader.get("Modal"));
				wItem.Number = StringUtils.parseInt(wReader.get("Number"));
				wItem.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.EditID = StringUtils.parseInt(wReader.get("EditID"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));

				wItem.UnitText = QMSConstants.GetCFGUnitName(wItem.UnitID);
				wItem.Creator = QMSConstants.GetBMSEmployeeName(wItem.CreateID);
				wItem.Editor = QMSConstants.GetBMSEmployeeName(wItem.EditID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTToolDAO() {
		super();
	}

	public static IPTToolDAO getInstance() {
		if (Instance == null)
			Instance = new IPTToolDAO();
		return Instance;
	}
}
