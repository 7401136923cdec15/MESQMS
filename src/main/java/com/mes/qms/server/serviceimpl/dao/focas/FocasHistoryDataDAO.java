package com.mes.qms.server.serviceimpl.dao.focas;

import java.math.BigDecimal;
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
import com.mes.qms.server.service.po.focas.FocasHistoryData;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class FocasHistoryDataDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(FocasHistoryDataDAO.class);

	private static FocasHistoryDataDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wFocasHistoryData
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, FocasHistoryData wFocasHistoryData, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wFocasHistoryData == null)
				return 0;

			String wSQL = "";
			if (wFocasHistoryData.getId() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.focas_historydata(year,completeNumber,completeNumberC6,completeNumberC5,"
								+ "stopTimeC6,stopTimeC5,repairNumber,qualityRate) VALUES(:year,:completeNumber,"
								+ ":completeNumberC6,:completeNumberC5,:stopTimeC6,:stopTimeC5,:repairNumber,:qualityRate);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.focas_historydata SET year = :year,completeNumber = :completeNumber,"
								+ "completeNumberC6 = :completeNumberC6,completeNumberC5 = :completeNumberC5,stopTimeC6 = :stopTimeC6,"
								+ "stopTimeC5 = :stopTimeC5,repairNumber = :repairNumber,qualityRate = :qualityRate WHERE id = :id;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("id", wFocasHistoryData.getId());
			wParamMap.put("year", wFocasHistoryData.getYear());
			wParamMap.put("completeNumber", wFocasHistoryData.getCompleteNumber());
			wParamMap.put("completeNumberC6", wFocasHistoryData.getCompleteNumberC6());
			wParamMap.put("completeNumberC5", wFocasHistoryData.getCompleteNumberC5());
			wParamMap.put("stopTimeC6", wFocasHistoryData.getStopTimeC6());
			wParamMap.put("stopTimeC5", wFocasHistoryData.getStopTimeC5());
			wParamMap.put("repairNumber", wFocasHistoryData.getRepairNumber());
			wParamMap.put("qualityRate", wFocasHistoryData.getQualityRate());

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wFocasHistoryData.getId() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wFocasHistoryData.setId(wResult);
			} else {
				wResult = wFocasHistoryData.getId();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<FocasHistoryData> wList,
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
			for (FocasHistoryData wItem : wList) {
				wIDList.add(String.valueOf(wItem.getId()));
			}
			String wSql = MessageFormat.format("delete from {1}.focas_historydata WHERE ID IN({0}) ;",
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
	public FocasHistoryData SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		FocasHistoryData wResult = new FocasHistoryData();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<FocasHistoryData> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<FocasHistoryData> SelectList(BMSEmployee wLoginUser, int wid, int wyear,
			OutResult<Integer> wErrorCode) {
		List<FocasHistoryData> wResultList = new ArrayList<FocasHistoryData>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.focas_historydata WHERE  1=1  "
					+ "and ( :wid <= 0 or :wid = id ) " + "and ( :wyear <= 0 or :wyear = year );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wid", wid);
			wParamMap.put("wyear", wyear);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				FocasHistoryData wItem = new FocasHistoryData();

				wItem.setId(StringUtils.parseInt(wReader.get("id")));
				wItem.setYear(StringUtils.parseInt(wReader.get("year")));
				wItem.setCompleteNumber(StringUtils.parseInt(wReader.get("completeNumber")));
				wItem.setCompleteNumberC6(StringUtils.parseInt(wReader.get("completeNumberC6")));
				wItem.setCompleteNumberC5(StringUtils.parseInt(wReader.get("completeNumberC5")));
				wItem.setStopTimeC6(StringUtils.parseDouble(wReader.get("stopTimeC6")));
				wItem.setStopTimeC5(StringUtils.parseDouble(wReader.get("stopTimeC5")));
				wItem.setRepairNumber(StringUtils.parseDouble(wReader.get("repairNumber")));
				wItem.setQualityRate(StringUtils.parseDouble(wReader.get("qualityRate")));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private FocasHistoryDataDAO() {
		super();
	}

	public static FocasHistoryDataDAO getInstance() {
		if (Instance == null)
			Instance = new FocasHistoryDataDAO();
		return Instance;
	}

	public void CloseNCRMessage(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wStepID,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils
					.Format("update {0}.bfc_message set Active=3 " + "where MessageID=:wOrderID and StationID=:wPartID "
							+ "and StepID=:wStepID and ModuleID=8217 and Active in (0,1,2);", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wPartID);
			wParamMap.put("wStepID", wStepID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public void CloseAllMessage(BMSEmployee wLoginUser, int wModuleID, int wMessageID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"update {0}.bfc_message set Active=3 "
							+ "where MessageID=:wMessageID and ModuleID=:wModuleID and Active in (0,1,2);",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wModuleID", wModuleID);
			wParamMap.put("wMessageID", wMessageID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 统计今年的完工数据
	 */
	public FocasHistoryData CalculateFocasHistoryData(BMSEmployee wLoginUser, Calendar wYearStart, Calendar wYearEnd,
			OutResult<Integer> wErrorCode) {
		FocasHistoryData wResult = new FocasHistoryData();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT 	(SELECT COUNT(*) FROM {0}.oms_order WHERE RealFinishDate>=:wYearStart AND RealFinishDate<=:wYearEnd AND ACTIVE=1) JCJGS,"
							+ "	(SELECT COUNT(*) FROM {0}.oms_order WHERE RealFinishDate>=:wYearStart AND RealFinishDate<=:wYearEnd AND ACTIVE=1 AND LineID=2) C6JGS,"
							+ "	(SELECT COUNT(*) FROM {0}.oms_order WHERE RealFinishDate>=:wYearStart AND RealFinishDate<=:wYearEnd AND ACTIVE=1 AND LineID=1) C5JGS,"
							+ "	(SELECT COUNT(*) FROM {0}.rro_repairitem WHERE FlowType=5010 AND STATUS=25 AND CreateTime>=:wYearStart AND CreateTime<=:wYearEnd) YSFXS,"
							+ "	(SELECT AVG(ActualRepairStopTimes) FROM {0}.oms_order WHERE RealFinishDate>=:wYearStart AND RealFinishDate<=:wYearEnd AND ACTIVE=1 AND LineID=2) C6XTS,"
							+ "	(SELECT AVG(ActualRepairStopTimes) FROM {0}.oms_order WHERE RealFinishDate>=:wYearStart AND RealFinishDate<=:wYearEnd AND ACTIVE=1 AND LineID=1) C5XTS;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wYearStart", wYearStart);
			wParamMap.put("wYearEnd", wYearEnd);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int completeNumber = StringUtils.parseInt(wReader.get("JCJGS"));
				int completeNumberC6 = StringUtils.parseInt(wReader.get("C6JGS"));
				int completeNumberC5 = StringUtils.parseInt(wReader.get("C5JGS"));
				double C6XTS = StringUtils.parseDouble(wReader.get("C6XTS"));
				double C5XTS = StringUtils.parseDouble(wReader.get("C5XTS"));
				int wYSFXS = StringUtils.parseInt(wReader.get("YSFXS"));

				double wQuality = new BigDecimal((double) (205 - wYSFXS) / 205 * 100)
						.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

				C6XTS = new BigDecimal(C6XTS).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
				C5XTS = new BigDecimal(C5XTS).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

				wResult = new FocasHistoryData(0, Calendar.getInstance().get(Calendar.YEAR), completeNumber,
						completeNumberC6, completeNumberC5, C6XTS, C5XTS, 0, wQuality);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 选择订单ID
	 */
	public List<Integer> SelectOrderIDList(BMSEmployee wLoginUser, Calendar wYearStart, Calendar wYearEnd,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT ID FROM {0}.oms_order WHERE "
							+ "RealFinishDate>=:wYearStart AND RealFinishDate<=:wYearEnd AND ACTIVE=1;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wYearStart", wYearStart);
			wParamMap.put("wYearEnd", wYearEnd);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wID = StringUtils.parseInt(wReader.get("ID"));
				wResult.add(wID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询台车返工件数
	 */
	public int QueryRepairNum(BMSEmployee wLoginUser, Integer wOrderID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT COUNT(*) AS FQTY FROM {0}.rro_repairitem WHERE OrderID=:OrderID AND STATUS=25;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wFQTY = StringUtils.parseInt(wReader.get("FQTY"));
				wResult = wFQTY;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

}
