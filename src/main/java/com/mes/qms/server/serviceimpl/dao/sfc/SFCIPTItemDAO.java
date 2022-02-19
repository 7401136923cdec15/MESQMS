package com.mes.qms.server.serviceimpl.dao.sfc;

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
import com.mes.qms.server.service.po.sfc.SFCIPTItem;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTItemDAO;

public class SFCIPTItemDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCIPTItemDAO.class);

	private static SFCIPTItemDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCIPTItem
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCIPTItem wSFCIPTItem, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCIPTItem == null)
				return 0;

			String wSQL = "";
			if (wSFCIPTItem.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.sfc_iptitem(ParentID,ItemID,ItemValue) VALUES(:ParentID,:ItemID,:ItemValue);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.sfc_iptitem SET ParentID = :ParentID,ItemID = :ItemID,ItemValue = :ItemValue WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCIPTItem.ID);
			wParamMap.put("ParentID", wSFCIPTItem.ParentID);
			wParamMap.put("ItemID", wSFCIPTItem.ItemID);
			wParamMap.put("ItemValue", wSFCIPTItem.ItemValue);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCIPTItem.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCIPTItem.setID(wResult);
			} else {
				wResult = wSFCIPTItem.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCIPTItem> wList,
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
			for (SFCIPTItem wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.sfc_iptitem WHERE ID IN({0}) ;",
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
	public SFCIPTItem SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCIPTItem wResult = new SFCIPTItem();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCIPTItem> wList = SelectList(wLoginUser, wID, -1, -1, wErrorCode);
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
	public List<SFCIPTItem> SelectList(BMSEmployee wLoginUser, int wID, int wParentID, int wItemID,
			OutResult<Integer> wErrorCode) {
		List<SFCIPTItem> wResultList = new ArrayList<SFCIPTItem>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.sfc_iptitem WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wParentID <= 0 or :wParentID = ParentID ) "
					+ "and ( :wItemID <= 0 or :wItemID = ItemID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wParentID", wParentID);
			wParamMap.put("wItemID", wItemID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCIPTItem wItem = new SFCIPTItem();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.ParentID = StringUtils.parseInt(wReader.get("ParentID"));
				wItem.ItemID = StringUtils.parseInt(wReader.get("ItemID"));
				wItem.ItemValue = StringUtils.parseFloat(wReader.get("ItemValue"));

				// 标准项
				wItem.IPTItem = IPTItemDAO.getInstance().SelectByID(wLoginUser, wItem.ItemID, wErrorCode);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private SFCIPTItemDAO() {
		super();
	}

	public static SFCIPTItemDAO getInstance() {
		if (Instance == null)
			Instance = new SFCIPTItemDAO();
		return Instance;
	}
}
