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
import com.mes.qms.server.service.po.mss.MSSRepairRecord;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class MSSRepairRecordDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(MSSRepairRecordDAO.class);

	private static MSSRepairRecordDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wMSSRepairRecord
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, MSSRepairRecord wMSSRepairRecord, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wMSSRepairRecord == null)
				return 0;

			String wSQL = "";
			if (wMSSRepairRecord.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.mss_repairrecord(ProductID,ProductNo,LineID,LineName,CustomerID,"
								+ "Customer,PartID,PartName,StepID,StepName,ItemID,ItemName,MSSPartCode,"
								+ "MSSPartSerial,MSSPartSerialScrap,MSSPartSerialLY,FQTY,FQTYScrap,FQTYLY,"
								+ "OrderID,PartNo,TartgetOrderID,TargetPartNo,OperateType,OpereateTypeName,"
								+ "OperateID,Operator,CreateTime,MaterialID,MaterialNo,MaterialName) VALUES(:ProductID,:ProductNo,"
								+ ":LineID,:LineName,:CustomerID,:Customer,:PartID,:PartName,:StepID,:StepName,:ItemID,"
								+ ":ItemName,:MSSPartCode,:MSSPartSerial,:MSSPartSerialScrap,:MSSPartSerialLY,:FQTY,"
								+ ":FQTYScrap,:FQTYLY,:OrderID,:PartNo,:TartgetOrderID,:TargetPartNo,:OperateType,"
								+ ":OpereateTypeName,:OperateID,:Operator,:CreateTime,:MaterialID,:MaterialNo,:MaterialName);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.mss_repairrecord SET ProductID = :ProductID,ProductNo = :ProductNo,"
								+ "LineID = :LineID,LineName = :LineName,CustomerID = :CustomerID,Customer = :Customer,"
								+ "PartID = :PartID,PartName = :PartName,StepID = :StepID,StepName = :StepName,ItemID = :ItemID,"
								+ "ItemName = :ItemName,MSSPartCode = :MSSPartCode,MSSPartSerial = :MSSPartSerial,"
								+ "MSSPartSerialScrap = :MSSPartSerialScrap,MSSPartSerialLY = :MSSPartSerialLY,"
								+ "FQTY = :FQTY,FQTYScrap = :FQTYScrap,FQTYLY = :FQTYLY,OrderID = :OrderID,PartNo = :PartNo,"
								+ "TartgetOrderID = :TartgetOrderID,TargetPartNo = :TargetPartNo,OperateType = :OperateType,"
								+ "OpereateTypeName = :OpereateTypeName,OperateID = :OperateID,Operator = :Operator,"
								+ "CreateTime = :CreateTime,"
								+ "MaterialID=:MaterialID,MaterialNo=:MaterialNo,MaterialName=:MaterialName WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wMSSRepairRecord.ID);
			wParamMap.put("ProductID", wMSSRepairRecord.ProductID);
			wParamMap.put("ProductNo", wMSSRepairRecord.ProductNo);
			wParamMap.put("LineID", wMSSRepairRecord.LineID);
			wParamMap.put("LineName", wMSSRepairRecord.LineName);
			wParamMap.put("CustomerID", wMSSRepairRecord.CustomerID);
			wParamMap.put("Customer", wMSSRepairRecord.Customer);
			wParamMap.put("PartID", wMSSRepairRecord.PartID);
			wParamMap.put("PartName", wMSSRepairRecord.PartName);
			wParamMap.put("StepID", wMSSRepairRecord.StepID);
			wParamMap.put("StepName", wMSSRepairRecord.StepName);
			wParamMap.put("ItemID", wMSSRepairRecord.ItemID);
			wParamMap.put("ItemName", wMSSRepairRecord.ItemName);
			wParamMap.put("MSSPartCode", wMSSRepairRecord.MSSPartCode);
			wParamMap.put("MSSPartSerial", wMSSRepairRecord.MSSPartSerial);
			wParamMap.put("MSSPartSerialScrap", wMSSRepairRecord.MSSPartSerialScrap);
			wParamMap.put("MSSPartSerialLY", wMSSRepairRecord.MSSPartSerialLY);
			wParamMap.put("FQTY", wMSSRepairRecord.FQTY);
			wParamMap.put("FQTYScrap", wMSSRepairRecord.FQTYScrap);
			wParamMap.put("FQTYLY", wMSSRepairRecord.FQTYLY);
			wParamMap.put("OrderID", wMSSRepairRecord.OrderID);
			wParamMap.put("PartNo", wMSSRepairRecord.PartNo);
			wParamMap.put("TartgetOrderID", wMSSRepairRecord.TartgetOrderID);
			wParamMap.put("TargetPartNo", wMSSRepairRecord.TargetPartNo);
			wParamMap.put("OperateType", wMSSRepairRecord.OperateType);
			wParamMap.put("OpereateTypeName", wMSSRepairRecord.OpereateTypeName);
			wParamMap.put("OperateID", wMSSRepairRecord.OperateID);
			wParamMap.put("Operator", wMSSRepairRecord.Operator);
			wParamMap.put("CreateTime", wMSSRepairRecord.CreateTime);

			wParamMap.put("MaterialID", wMSSRepairRecord.MaterialID);
			wParamMap.put("MaterialNo", wMSSRepairRecord.MaterialNo);
			wParamMap.put("MaterialName", wMSSRepairRecord.MaterialName);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wMSSRepairRecord.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wMSSRepairRecord.setID(wResult);
			} else {
				wResult = wMSSRepairRecord.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<MSSRepairRecord> wList,
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
			for (MSSRepairRecord wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.mss_repairrecord WHERE ID IN({0}) ;",
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
	public MSSRepairRecord SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		MSSRepairRecord wResult = new MSSRepairRecord();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<MSSRepairRecord> wList = SelectList(wLoginUser, wID, -1, -1, -1, -1, -1, "", -1, -1, -1, -1,
					wErrorCode);
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
	public List<MSSRepairRecord> SelectList(BMSEmployee wLoginUser, int wID, int wProductID, int wLineID,
			int wCustomerID, int wPartID, int wStepID, String wMSSPartCode, int wOrderID, int wTartgetOrderID,
			int wOperateType, int wItemID, OutResult<Integer> wErrorCode) {
		List<MSSRepairRecord> wResultList = new ArrayList<MSSRepairRecord>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.mss_repairrecord WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wProductID <= 0 or :wProductID = ProductID ) "
					+ "and ( :wLineID <= 0 or :wLineID = LineID ) " + "and ( :wItemID <= 0 or :wItemID = ItemID ) "
					+ "and ( :wCustomerID <= 0 or :wCustomerID = CustomerID ) "
					+ "and ( :wPartID <= 0 or :wPartID = PartID ) " + "and ( :wStepID <= 0 or :wStepID = StepID ) "
					+ "and ( :wMSSPartCode is null or :wMSSPartCode = '''' or :wMSSPartCode = MSSPartCode ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
					+ "and ( :wTartgetOrderID <= 0 or :wTartgetOrderID = TartgetOrderID ) "
					+ "and ( :wOperateType <= 0 or :wOperateType = OperateType );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wCustomerID", wCustomerID);
			wParamMap.put("wPartID", wPartID);
			wParamMap.put("wStepID", wStepID);
			wParamMap.put("wMSSPartCode", wMSSPartCode);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wTartgetOrderID", wTartgetOrderID);
			wParamMap.put("wOperateType", wOperateType);
			wParamMap.put("wItemID", wItemID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private void SetValue(List<MSSRepairRecord> wResultList, List<Map<String, Object>> wQueryResult) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				MSSRepairRecord wItem = new MSSRepairRecord();

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
				wItem.MSSPartSerialScrap = StringUtils.parseString(wReader.get("MSSPartSerialScrap"));
				wItem.MSSPartSerialLY = StringUtils.parseString(wReader.get("MSSPartSerialLY"));
				wItem.FQTY = StringUtils.parseInt(wReader.get("FQTY"));
				wItem.FQTYScrap = StringUtils.parseInt(wReader.get("FQTYScrap"));
				wItem.FQTYLY = StringUtils.parseInt(wReader.get("FQTYLY"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.TartgetOrderID = StringUtils.parseInt(wReader.get("TartgetOrderID"));
				wItem.TargetPartNo = StringUtils.parseString(wReader.get("TargetPartNo"));
				wItem.OperateType = StringUtils.parseInt(wReader.get("OperateType"));
				wItem.OpereateTypeName = StringUtils.parseString(wReader.get("OpereateTypeName"));
				wItem.OperateID = StringUtils.parseInt(wReader.get("OperateID"));
				wItem.Operator = StringUtils.parseString(wReader.get("Operator"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));

				wItem.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private MSSRepairRecordDAO() {
		super();
	}

	public static MSSRepairRecordDAO getInstance() {
		if (Instance == null)
			Instance = new MSSRepairRecordDAO();
		return Instance;
	}

	public List<MSSRepairRecord> SelectListBySerilalNo(BMSEmployee wLoginUser, String wCode,
			OutResult<Integer> wErrorCode) {
		List<MSSRepairRecord> wResultList = new ArrayList<MSSRepairRecord>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (StringUtils.isEmpty(wCode)) {
				return wResultList;
			}

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.mss_repairrecord WHERE  1=1  " + "and (MSSPartSerial like '%{1}%');",
					wInstance.Result, wCode);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

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
	 * 判断自修件是否已组装
	 */
	public boolean IsCodeZuZhuang(BMSEmployee wLoginUser, String wCode, OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (StringUtils.isEmpty(wCode)) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT count(*) Number FROM {0}.mss_repairrecord " + "where "
							+ "MSSPartSerialScrap like ''%{1}%''  or (OperateType=3 and MSSPartSerial like ''%{1}%'');",
					wInstance.Result, wCode);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wNumber = StringUtils.parseInt(wReader.get("Number"));
				if (wNumber > 0) {
					return true;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public MSSRepairRecord SelectByCode(BMSEmployee wLoginUser, String wCode, int wOperateType,
			OutResult<Integer> wErrorCode) {
		MSSRepairRecord wResult = new MSSRepairRecord();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.mss_repairrecord "
					+ "where find_in_set( :wCode,replace(MSSPartSerial,''#,#'','','') ) and OperateType=:wOperateType;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wCode", wCode);
			wParamMap.put("wOperateType", wOperateType);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				wResult.ID = StringUtils.parseInt(wReader.get("ID"));
				wResult.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wResult.ProductNo = StringUtils.parseString(wReader.get("ProductNo"));
				wResult.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wResult.LineName = StringUtils.parseString(wReader.get("LineName"));
				wResult.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wResult.Customer = StringUtils.parseString(wReader.get("Customer"));
				wResult.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wResult.PartName = StringUtils.parseString(wReader.get("PartName"));
				wResult.StepID = StringUtils.parseInt(wReader.get("StepID"));
				wResult.StepName = StringUtils.parseString(wReader.get("StepName"));
				wResult.ItemID = StringUtils.parseInt(wReader.get("ItemID"));
				wResult.ItemName = StringUtils.parseString(wReader.get("ItemName"));
				wResult.MSSPartCode = StringUtils.parseString(wReader.get("MSSPartCode"));
				wResult.MSSPartSerial = StringUtils.parseString(wReader.get("MSSPartSerial"));
				wResult.MSSPartSerialScrap = StringUtils.parseString(wReader.get("MSSPartSerialScrap"));
				wResult.MSSPartSerialLY = StringUtils.parseString(wReader.get("MSSPartSerialLY"));
				wResult.FQTY = StringUtils.parseInt(wReader.get("FQTY"));
				wResult.FQTYScrap = StringUtils.parseInt(wReader.get("FQTYScrap"));
				wResult.FQTYLY = StringUtils.parseInt(wReader.get("FQTYLY"));
				wResult.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wResult.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wResult.TartgetOrderID = StringUtils.parseInt(wReader.get("TartgetOrderID"));
				wResult.TargetPartNo = StringUtils.parseString(wReader.get("TargetPartNo"));
				wResult.OperateType = StringUtils.parseInt(wReader.get("OperateType"));
				wResult.OpereateTypeName = StringUtils.parseString(wReader.get("OpereateTypeName"));
				wResult.OperateID = StringUtils.parseInt(wReader.get("OperateID"));
				wResult.Operator = StringUtils.parseString(wReader.get("Operator"));
				wResult.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wResult.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wResult.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wResult.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
