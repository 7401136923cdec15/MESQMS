package com.mes.qms.server.serviceimpl.dao.sfc;

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
import com.mes.qms.server.service.po.sfc.SFCTemporaryExamination;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class SFCTemporaryExaminationDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCTemporaryExaminationDAO.class);

	private static SFCTemporaryExaminationDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCTemporaryExamination
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCTemporaryExamination wSFCTemporaryExamination,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCTemporaryExamination == null)
				return 0;

			String wSQL = "";
			if (wSFCTemporaryExamination.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.sfc_temporaryexamination(Code,OrderID,PartID,PartIDList,"
								+ "CreateID,CreateTime,CheckIDList,Remark,Status,FinishTime) VALUES(:Code,"
								+ ":OrderID,:PartID,:PartIDList,:CreateID,now(),:CheckIDList,:Remark,:Status,now());",
						wInstance.Result);

				wSFCTemporaryExamination.Code = this.GetNewCode(wLoginUser, wErrorCode);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.sfc_temporaryexamination SET Code = :Code,OrderID = :OrderID,"
						+ "PartID = :PartID,PartIDList = :PartIDList,CreateID = :CreateID,"
						+ "CreateTime = :CreateTime,CheckIDList = :CheckIDList,Remark = :Remark,"
						+ "Status = :Status,FinishTime=now() WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCTemporaryExamination.ID);
			wParamMap.put("Code", wSFCTemporaryExamination.Code);
			wParamMap.put("OrderID", wSFCTemporaryExamination.OrderID);
			wParamMap.put("PartID", wSFCTemporaryExamination.PartID);
			wParamMap.put("PartIDList", StringUtils.Join(",", wSFCTemporaryExamination.PartIDList));
			wParamMap.put("CreateID", wSFCTemporaryExamination.CreateID);
			wParamMap.put("CreateTime", wSFCTemporaryExamination.CreateTime);
			wParamMap.put("CheckIDList", StringUtils.Join(",", wSFCTemporaryExamination.CheckIDList));
			wParamMap.put("Remark", wSFCTemporaryExamination.Remark);
			wParamMap.put("Status", wSFCTemporaryExamination.Status);
			wParamMap.put("FinishTime", wSFCTemporaryExamination.FinishTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCTemporaryExamination.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCTemporaryExamination.setID(wResult);
			} else {
				wResult = wSFCTemporaryExamination.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCTemporaryExamination> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (SFCTemporaryExamination wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.sfc_temporaryexamination WHERE ID IN({0}) ;",
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
	public SFCTemporaryExamination SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCTemporaryExamination wResult = new SFCTemporaryExamination();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCTemporaryExamination> wList = this.SelectList(wLoginUser, wID, "", -1, -1, "", -1, -1, -1,
					new ArrayList<Integer>(), null, null, wErrorCode);
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
	 * 获取最新的编码
	 */
	public String GetNewCode(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select count(*)+1 as Number from {0}.sfc_temporaryexamination;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			int wNumber = 0;
			for (Map<String, Object> wReader : wQueryResult) {
				if (wReader.containsKey("Number")) {
					wNumber = StringUtils.parseInt(wReader.get("Number"));
					break;
				}
			}

			wResult = StringUtils.Format("TET{0}-{1}-{2}", String.valueOf(wNumber),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
					String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<SFCTemporaryExamination> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wOrderID,
			int wProductID, String wPartNo, int wPartID, int wCreateID, int wCheckID, List<Integer> wStateIDList,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<SFCTemporaryExamination> wResultList = new ArrayList<SFCTemporaryExamination>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wAPSInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wAPSInstance.ErrorCode);
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

			if (wStateIDList == null) {
				wStateIDList = new ArrayList<Integer>();
			}

			String wSQL = StringUtils.Format("SELECT t.*,o.OrderNo,o.ProductID,o.PartNo,o.LineID,t2.CustomerID,"
					+ " t2.WBSNo FROM {0}.sfc_temporaryexamination t" + " inner join {1}.oms_order o on t.OrderID=o.ID "
					+ " left  join {1}.oms_command t2 on o.CommandID=t2.ID  WHERE  1=1  "
					+ " and ( :wID <= 0 or :wID = t.ID ) " + "and ( :wCode = '''' or :wCode = t.Code ) "
					+ " and ( :wOrderID <= 0 or :wOrderID = t.OrderID ) "
					+ " and ( :wPartNo = ''''  or :wPartNo = o.PartNo ) "
					+ " and ( :wPartID <= 0 or :wPartID = t.PartID ) "
					+ " and ( :wProductID <= 0 or :wProductID = o.ProductID ) "
					+ " and ( :wCreateID <= 0 or :wCreateID = t.CreateID ) "
					+ " and ( :wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wStartTime <= t.FinishTime) "
					+ " and ( :wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wEndTime >= t.CreateTime) "
					+ " and ( :wCheckID <=0 or find_in_set( :wCheckID ,replace(t.CheckIDList,'';'','','')) ) "
					+ " and ( :wStatus = '''' or t.Status in ({2}));", wInstance.Result, wAPSInstance.Result,
					wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wPartID", wPartID);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wCreateID", wCreateID);
			wParamMap.put("wCheckID", wCheckID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 赋值
	 */
	private void SetValue(List<SFCTemporaryExamination> wResultList, List<Map<String, Object>> wQueryResult) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				SFCTemporaryExamination wItem = new SFCTemporaryExamination();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.PartIDList = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("PartIDList")).split(",|;"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.FinishTime = StringUtils.parseCalendar(wReader.get("FinishTime"));
				wItem.CheckIDList = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("CheckIDList")).split(",|;"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));

				wItem.OrderNo = StringUtils.parseString(wReader.get("OrderNo"));
				wItem.WBSNo = StringUtils.parseString(wReader.get("WBSNo"));
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.ProductNo = QMSConstants.GetFPCProductNo(wItem.ProductID);
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));

				// 赋值辅助信息
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.LineName = QMSConstants.GetFMCLineName(wItem.LineID);
				wItem.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wItem.CustomerName = QMSConstants.GetCRMCustomerName(wItem.CustomerID);
				wItem.Creator = QMSConstants.GetBMSEmployeeName(wItem.CreateID);
				wItem.CheckNames = QMSConstants.GetBMSEmployeeName(wItem.CheckIDList);
				wItem.StatusText = "";
				wItem.PartName = QMSConstants.GetFPCPartName(wItem.PartID);
				wItem.PartNames = this.GetPartNames(wItem.PartIDList);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 赋值工位名称
	 */
	private String GetPartNames(List<Integer> wPartIDList) {
		String wResult = "";
		try {
			if (wPartIDList == null || wPartIDList.size() <= 0) {
				return wResult;
			}

			List<String> wNames = new ArrayList<String>();
			for (Integer wPartID : wPartIDList) {
				String wName = QMSConstants.GetFPCPartName(wPartID);
				if (StringUtils.isEmpty(wName)) {
					continue;
				}
				wNames.add(wName);
			}

			if (wNames.size() > 0) {
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private SFCTemporaryExaminationDAO() {
		super();
	}

	public static SFCTemporaryExaminationDAO getInstance() {
		if (Instance == null)
			Instance = new SFCTemporaryExaminationDAO();
		return Instance;
	}
}
