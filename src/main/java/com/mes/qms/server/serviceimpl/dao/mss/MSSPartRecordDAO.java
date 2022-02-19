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
import com.mes.qms.server.service.mesenum.MSSOperateType;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.mss.MSSPartRecord;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class MSSPartRecordDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(MSSPartRecordDAO.class);

	private static MSSPartRecordDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wMSSPartRecord
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, MSSPartRecord wMSSPartRecord, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wMSSPartRecord == null)
				return 0;

			String wSQL = "";
			if (wMSSPartRecord.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.mss_partrecord(ProductID,ProductNo,LineID,LineName,"
						+ "CustomerID,Customer,PartID,PartName,StepID,StepName,ItemID,ItemName,"
						+ "MSSPartCode,MSSPartSerial,OrderID,PartNo,TartgetOrderID,TargetPartNo,"
						+ "OperateType,OpereateTypeName,OperateID,Operator,CreateTime) "
						+ "VALUES(:ProductID,:ProductNo,:LineID,:LineName,:CustomerID,:Customer,"
						+ ":PartID,:PartName,:StepID,:StepName,:ItemID,:ItemName,:MSSPartCode,"
						+ ":MSSPartSerial,:OrderID,:PartNo,:TartgetOrderID,:TargetPartNo,:OperateType,"
						+ ":OpereateTypeName,:OperateID,:Operator,:CreateTime);", wInstance.Result);
			} else {
				wSQL = MessageFormat
						.format("UPDATE {0}.mss_partrecord SET ProductID = :ProductID,ProductNo = :ProductNo,"
								+ "LineID = :LineID,LineName = :LineName,CustomerID = :CustomerID,Customer = :Customer,"
								+ "PartID = :PartID,PartName = :PartName,StepID = :StepID,StepName = :StepName,"
								+ "ItemID = :ItemID,ItemName = :ItemName,MSSPartCode = :MSSPartCode,"
								+ "MSSPartSerial = :MSSPartSerial,OrderID = :OrderID,PartNo = :PartNo,"
								+ "TartgetOrderID = :TartgetOrderID,TargetPartNo = :TargetPartNo,OperateType = :OperateType,"
								+ "OpereateTypeName = :OpereateTypeName,OperateID = :OperateID,Operator = :Operator,"
								+ "CreateTime = :CreateTime WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wMSSPartRecord.ID);
			wParamMap.put("ProductID", wMSSPartRecord.ProductID);
			wParamMap.put("ProductNo", wMSSPartRecord.ProductNo);
			wParamMap.put("LineID", wMSSPartRecord.LineID);
			wParamMap.put("LineName", wMSSPartRecord.LineName);
			wParamMap.put("CustomerID", wMSSPartRecord.CustomerID);
			wParamMap.put("Customer", wMSSPartRecord.Customer);
			wParamMap.put("PartID", wMSSPartRecord.PartID);
			wParamMap.put("PartName", wMSSPartRecord.PartName);
			wParamMap.put("StepID", wMSSPartRecord.StepID);
			wParamMap.put("StepName", wMSSPartRecord.StepName);
			wParamMap.put("ItemID", wMSSPartRecord.ItemID);
			wParamMap.put("ItemName", wMSSPartRecord.ItemName);
			wParamMap.put("MSSPartCode", wMSSPartRecord.MSSPartCode);
			wParamMap.put("MSSPartSerial", wMSSPartRecord.MSSPartSerial);
			wParamMap.put("OrderID", wMSSPartRecord.OrderID);
			wParamMap.put("PartNo", wMSSPartRecord.PartNo);
			wParamMap.put("TartgetOrderID", wMSSPartRecord.TartgetOrderID);
			wParamMap.put("TargetPartNo", wMSSPartRecord.TargetPartNo);
			wParamMap.put("OperateType", wMSSPartRecord.OperateType);
			wParamMap.put("OpereateTypeName", wMSSPartRecord.OpereateTypeName);
			wParamMap.put("OperateID", wMSSPartRecord.OperateID);
			wParamMap.put("Operator", wMSSPartRecord.Operator);
			wParamMap.put("CreateTime", wMSSPartRecord.CreateTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wMSSPartRecord.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wMSSPartRecord.setID(wResult);
			} else {
				wResult = wMSSPartRecord.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<MSSPartRecord> wList,
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
			for (MSSPartRecord wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.mss_partrecord WHERE ID IN({0}) ;",
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
	public MSSPartRecord SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		MSSPartRecord wResult = new MSSPartRecord();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<MSSPartRecord> wList = SelectList(wLoginUser, wID, -1, -1, -1, "", "", -1, -1, -1, -1, wErrorCode);
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
	 */
	public List<MSSPartRecord> SelectList(BMSEmployee wLoginUser, int wID, int wProductID, int wLineID, int wCustomerID,
			String wMSSPartCode, String wMSSPartSerial, int wOrderID, int wTartgetOrderID, int wOperateType,
			int wIPTItemID, OutResult<Integer> wErrorCode) {
		List<MSSPartRecord> wResultList = new ArrayList<MSSPartRecord>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.mss_partrecord WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wProductID <= 0 or :wProductID = ProductID ) "
					+ "and ( :wLineID <= 0 or :wLineID = LineID ) "
					+ "and ( :wCustomerID <= 0 or :wCustomerID = CustomerID ) "
					+ "and ( :wMSSPartCode is null or :wMSSPartCode = '''' or :wMSSPartCode = MSSPartCode ) "
					+ "and ( :wMSSPartSerial is null or :wMSSPartSerial = '''' or :wMSSPartSerial = MSSPartSerial ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
					+ "and ( :wIPTItemID <= 0 or :wIPTItemID = ItemID ) "
					+ "and ( :wTartgetOrderID <= 0 or :wTartgetOrderID = TartgetOrderID ) "
					+ "and ( :wOperateType <= 0 or :wOperateType = OperateType );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wCustomerID", wCustomerID);
			wParamMap.put("wMSSPartCode", wMSSPartCode);
			wParamMap.put("wMSSPartSerial", wMSSPartSerial);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wTartgetOrderID", wTartgetOrderID);
			wParamMap.put("wOperateType", wOperateType);
			wParamMap.put("wIPTItemID", wIPTItemID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				MSSPartRecord wItem = new MSSPartRecord();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.ProductNo = StringUtils.parseString(wReader.get("ProductNo"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.LineName = StringUtils.parseString(wReader.get("LineName"));
				wItem.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wItem.Customer = StringUtils.parseString(wReader.get("Customer"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.PartName = StringUtils.parseString(wReader.get("PartName"));
				wItem.StepID = StringUtils.parseInt(wReader.get("StepID"));
				wItem.StepName = StringUtils.parseString(wReader.get("StepName"));
				wItem.ItemID = StringUtils.parseInt(wReader.get("ItemID"));
				wItem.ItemName = StringUtils.parseString(wReader.get("ItemName"));
				wItem.MSSPartCode = StringUtils.parseString(wReader.get("MSSPartCode"));
				wItem.MSSPartSerial = StringUtils.parseString(wReader.get("MSSPartSerial"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.TartgetOrderID = StringUtils.parseInt(wReader.get("TartgetOrderID"));
				wItem.TargetPartNo = StringUtils.parseString(wReader.get("TargetPartNo"));
				wItem.OperateType = StringUtils.parseInt(wReader.get("OperateType"));
				wItem.OpereateTypeName = StringUtils.parseString(wReader.get("OpereateTypeName"));
				wItem.OperateID = StringUtils.parseInt(wReader.get("OperateID"));
				wItem.Operator = StringUtils.parseString(wReader.get("Operator"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private MSSPartRecordDAO() {
		super();
	}

	public static MSSPartRecordDAO getInstance() {
		if (Instance == null)
			Instance = new MSSPartRecordDAO();
		return Instance;
	}

	/**
	 * 获取部件字典
	 */
	public Map<String, String> GetPartCodeMap(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		Map<String, String> wResult = new HashMap<String, String>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT Code,Name FROM {0}.mss_parttype;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				String wCode = StringUtils.parseString(wReader.get("Code"));
				String wName = StringUtils.parseString(wReader.get("Name"));
				wResult.put(wCode, wName);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public int MSS_QueryChangeOrderID(BMSEmployee wLoginUser, String wComponents, int wOrderID,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			List<MSSPartRecord> wList = this.SelectList(wLoginUser, -1, -1, -1, -1, wComponents, "", -1, wOrderID,
					MSSOperateType.Zuzhuang.getValue(), -1, wErrorCode);
			if (wList.size() > 0 && wList.stream().anyMatch(p -> p.OrderID != wOrderID)) {
				wResult = wList.stream().filter(p -> p.OrderID != wOrderID).findFirst().get().OrderID;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
