package com.mes.qms.server.serviceimpl.dao.mss;

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
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.mss.MSSBOMItemHistory;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class MSSBOMItemHistoryDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(MSSBOMItemHistoryDAO.class);

	private static MSSBOMItemHistoryDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wMSSBOMItemHistory
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, MSSBOMItemHistory wMSSBOMItemHistory, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wMSSBOMItemHistory == null)
				return 0;

			String wSQL = "";
			if (wMSSBOMItemHistory.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.mss_bomitemhistory(ProductID,LineID,CustomerID,PlaceID,"
						+ "PartPointID,MaterialID,MaterialNo,MaterialName,MaterialNumber,UnitID,UnitText) "
						+ "VALUES(:ProductID,:LineID,:CustomerID,:PlaceID,:PartPointID,:MaterialID,:MaterialNo,"
						+ ":MaterialName,:MaterialNumber,:UnitID,:UnitText);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.mss_bomitemhistory SET ProductID = :ProductID,LineID = :LineID,"
						+ "CustomerID = :CustomerID,PlaceID = :PlaceID,PartPointID = :PartPointID,"
						+ "MaterialID = :MaterialID,MaterialNo = :MaterialNo,MaterialName = :MaterialName,"
						+ "MaterialNumber = :MaterialNumber,UnitID = :UnitID,UnitText = :UnitText WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wMSSBOMItemHistory.ID);
			wParamMap.put("ProductID", wMSSBOMItemHistory.ProductID);
			wParamMap.put("LineID", wMSSBOMItemHistory.LineID);
			wParamMap.put("CustomerID", wMSSBOMItemHistory.CustomerID);
			wParamMap.put("PlaceID", wMSSBOMItemHistory.PlaceID);
			wParamMap.put("PartPointID", wMSSBOMItemHistory.PartPointID);
			wParamMap.put("MaterialID", wMSSBOMItemHistory.MaterialID);
			wParamMap.put("MaterialNo", wMSSBOMItemHistory.MaterialNo);
			wParamMap.put("MaterialName", wMSSBOMItemHistory.MaterialName);
			wParamMap.put("MaterialNumber", wMSSBOMItemHistory.MaterialNumber);
			wParamMap.put("UnitID", wMSSBOMItemHistory.UnitID);
			wParamMap.put("UnitText", wMSSBOMItemHistory.UnitText);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wMSSBOMItemHistory.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wMSSBOMItemHistory.setID(wResult);
			} else {
				wResult = wMSSBOMItemHistory.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<MSSBOMItemHistory> wList,
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
			for (MSSBOMItemHistory wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.mss_bomitemhistory WHERE ID IN({0}) ;",
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
	public MSSBOMItemHistory SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		MSSBOMItemHistory wResult = new MSSBOMItemHistory();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<MSSBOMItemHistory> wList = SelectList(wLoginUser, wID, -1, -1, -1, -1, -1, -1, wErrorCode);
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
	public List<MSSBOMItemHistory> SelectList(BMSEmployee wLoginUser, int wID, int wProductID, int wLineID,
			int wCustomerID, int wPlaceID, int wPartPointID, int wMaterialID, OutResult<Integer> wErrorCode) {
		List<MSSBOMItemHistory> wResultList = new ArrayList<MSSBOMItemHistory>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.mss_bomitemhistory WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wProductID <= 0 or :wProductID = ProductID ) "
					+ "and ( :wLineID <= 0 or :wLineID = LineID ) "
					+ "and ( :wCustomerID <= 0 or :wCustomerID = CustomerID ) "
					+ "and ( :wPlaceID <= 0 or :wPlaceID = PlaceID ) "
					+ "and ( :wPartPointID <= 0 or :wPartPointID = PartPointID ) "
					+ "and ( :wMaterialID <= 0 or :wMaterialID = MaterialID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wCustomerID", wCustomerID);
			wParamMap.put("wPlaceID", wPlaceID);
			wParamMap.put("wPartPointID", wPartPointID);
			wParamMap.put("wMaterialID", wMaterialID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				MSSBOMItemHistory wItem = new MSSBOMItemHistory();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wItem.PlaceID = StringUtils.parseInt(wReader.get("PlaceID"));
				wItem.PartPointID = StringUtils.parseInt(wReader.get("PartPointID"));
				wItem.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));
				wItem.MaterialNumber = StringUtils.parseDouble(wReader.get("MaterialNumber"));
				wItem.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wItem.UnitText = StringUtils.parseString(wReader.get("UnitText"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private MSSBOMItemHistoryDAO() {
		super();
	}

	public static MSSBOMItemHistoryDAO getInstance() {
		if (Instance == null)
			Instance = new MSSBOMItemHistoryDAO();
		return Instance;
	}
}
