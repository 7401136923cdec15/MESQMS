package com.mes.qms.server.serviceimpl.dao.ipt;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckItem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class IPTPreCheckItemDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTPreCheckItemDAO.class);

	private static IPTPreCheckItemDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTPreCheckItem
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTPreCheckItem wIPTPreCheckItem, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTPreCheckItem == null)
				return 0;

			String wSQL = "";
			if (wIPTPreCheckItem.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.ipt_precheckitem(ReportID,ItemID,ItemName,PreChecker,"
						+ "IPTItemList,IPTValueList,IPTProblemList) VALUES(:ReportID,:ItemID,:ItemName,:PreChecker,"
						+ ":IPTItemList,:IPTValueList,:IPTProblemList);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.ipt_precheckitem SET ReportID = :ReportID,ItemID = :ItemID,"
								+ "ItemName = :ItemName,PreChecker = :PreChecker,IPTItemList = :IPTItemList,"
								+ "IPTValueList = :IPTValueList,IPTProblemList = :IPTProblemList WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTPreCheckItem.ID);
			wParamMap.put("ReportID", wIPTPreCheckItem.ReportID);
			wParamMap.put("ItemID", wIPTPreCheckItem.ItemID);
			wParamMap.put("ItemName", wIPTPreCheckItem.ItemName);
			wParamMap.put("PreChecker", wIPTPreCheckItem.PreChecker);
			wParamMap.put("IPTItemList", StringUtils.Join(";",
					wIPTPreCheckItem.IPTItemList.stream().map(p -> p.ID).collect(Collectors.toList())));
			wParamMap.put("IPTValueList", StringUtils.Join(";",
					wIPTPreCheckItem.IPTValueList.stream().map(p -> p.ID).collect(Collectors.toList())));
			wParamMap.put("IPTProblemList", StringUtils.Join(";",
					wIPTPreCheckItem.IPTProblemList.stream().map(p -> p.ID).collect(Collectors.toList())));

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTPreCheckItem.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTPreCheckItem.setID(wResult);
			} else {
				wResult = wIPTPreCheckItem.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTPreCheckItem> wList,
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
			for (IPTPreCheckItem wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_precheckitem WHERE ID IN({0}) ;",
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
	public IPTPreCheckItem SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTPreCheckItem wResult = new IPTPreCheckItem();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTPreCheckItem> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<IPTPreCheckItem> SelectList(BMSEmployee wLoginUser, int wID, int wReportID,
			OutResult<Integer> wErrorCode) {
		List<IPTPreCheckItem> wResultList = new ArrayList<IPTPreCheckItem>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat
					.format("SELECT * FROM {0}.ipt_precheckitem WHERE  1=1  and ( :wID <= 0 or :wID = ID ) "
							+ "and ( :wReportID <= 0 or :wReportID = ReportID ) ;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wReportID", wReportID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTPreCheckItem wItem = new IPTPreCheckItem();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.ReportID = StringUtils.parseInt(wReader.get("ReportID"));
				wItem.ItemID = StringUtils.parseInt(wReader.get("ItemID"));
				wItem.ItemName = StringUtils.parseString(wReader.get("ItemName"));
				wItem.PreChecker = StringUtils.parseString(wReader.get("PreChecker"));

				String wStr = StringUtils.parseString(wReader.get("IPTItemList"));
				if (StringUtils.isEmpty(wStr)) {
					wItem.IPTItemList = new ArrayList<IPTItem>();
				} else {
					List<Integer> wIDList = StringUtils.parseIntList(wStr.split(";"));
					wItem.IPTItemList = IPTItemDAO.getInstance().SelectByIDList(wLoginUser, wIDList, wErrorCode);
				}

				wStr = StringUtils.parseString(wReader.get("IPTValueList"));
				if (StringUtils.isEmpty(wStr)) {
					wItem.IPTValueList = new ArrayList<IPTValue>();
				} else {
					List<Integer> wIDList = StringUtils.parseIntList(wStr.split(";"));
					wItem.IPTValueList = IPTValueDAO.getInstance().SelectByIDList(wLoginUser, wIDList, wErrorCode);
				}

				wStr = StringUtils.parseString(wReader.get("IPTProblemList"));
				if (StringUtils.isEmpty(wStr)) {
					wItem.IPTProblemList = new ArrayList<IPTPreCheckProblem>();
				} else {
					List<Integer> wIDList = StringUtils.parseIntList(wStr.split(";"));
					wItem.IPTProblemList = IPTPreCheckProblemDAO.getInstance().SelectByIDList(wLoginUser, wIDList,
							wErrorCode);
				}

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTPreCheckItemDAO() {
		super();
	}

	public static IPTPreCheckItemDAO getInstance() {
		if (Instance == null)
			Instance = new IPTPreCheckItemDAO();
		return Instance;
	}
}
