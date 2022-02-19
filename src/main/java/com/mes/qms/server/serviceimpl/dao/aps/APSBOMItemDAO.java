package com.mes.qms.server.serviceimpl.dao.aps;

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

import com.mes.qms.server.service.mesenum.BPMStatus;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import com.mes.qms.server.shristool.LoggerTool;

public class APSBOMItemDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(APSBOMItemDAO.class);
	private static APSBOMItemDAO Instance = null;

	private APSBOMItemDAO() {
		super();
	}

	public static APSBOMItemDAO getInstance() {
		if (Instance == null)
			Instance = new APSBOMItemDAO();
		return Instance;
	}

	private final int RoleFunctionID = 200301;

	@SuppressWarnings("unused")
	private APSBOMItem APS_CheckBOMItem(BMSEmployee wLoginUser, APSBOMItem wAPSBOMItem, OutResult<Integer> wErrorCode) {
		APSBOMItem wBOMItemDB = new APSBOMItem();
		wErrorCode.set(0);
		try {
			List<APSBOMItem> wAPSBOMItemList = this.APS_QueryBOMItemList(wLoginUser, 0, wAPSBOMItem.getOrderID(),
					wAPSBOMItem.getWBSNo(), wAPSBOMItem.getPartNo(), wAPSBOMItem.getLineID(),
					wAPSBOMItem.getProductID(), wAPSBOMItem.getCustomerID(), wAPSBOMItem.PartID,
					wAPSBOMItem.PartPointID, wAPSBOMItem.MaterialID, wAPSBOMItem.MaterialNo, wAPSBOMItem.BOMType, 0, 0,
					null, -1, -1, -1, wErrorCode);

			if (wAPSBOMItemList != null && wAPSBOMItemList.size() > 0) {
				wBOMItemDB = wAPSBOMItemList.get(0);
			}
		} catch (Exception ex) {
			LoggerTool.SaveException("APSBOMItemDAO", "APS_CheckBOMItem", "Function Exception:" + ex.toString());
		}
		return wBOMItemDB;
	}

	public int APS_UpdateBOMItem(BMSEmployee wLoginUser, APSBOMItem wBOMItem, OutResult<Integer> wErrorCode) {
		wErrorCode.set(0);
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, RoleFunctionID);
			// Step01:Logic Check
			if (wErrorCode.Result == 0) {
//				APSBOMItem wBOMItemDB = this.APS_CheckBOMItem(wLoginUser, wBOMItem, wErrorCode);
//				if (wBOMItemDB.ID > 0) {
//					wErrorCode.set(MESException.Logic.getValue());
//				}
			}
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			Map<String, Object> wParms = new HashMap<String, Object>();

			String wSQLText = "";
			if (wBOMItem.ID <= 0) {
				wSQLText = StringUtils.Format(
						"INSERT INTO {0}.aps_bomitem ( BOMType, FactoryID, WBSNo, OrderID, PartNo, LineID, ProductID,CustomerID, PartID, "
								+ "PartPointID, MaterialID, MaterialNo, Number, UnitID, ReplaceType, OutsourceType, OriginalType, DisassyType, "
								+ "OverLine, PartChange, ReceiveDepart, StockID, QTType, QTItemType, CustomerMaterial,EditorID,"
								+ "EditTime,Status,RelaDemandNo,TextCode,WorkCenter,DeleteID,SubRelaDemandNo,AssessmentType,"
								+ "AccessoryLogo,RepairPartClass,Remark,DingrongGroup,RepairCoreIdentification,"
								+ "PickingQuantity,EvenExchangeRate,Client,OrderNum,SourceType,SourceID,DifferenceItem,OverQuota)  Values ( :BOMType, :FactoryID, :WBSNo, "
								+ ":OrderID, :PartNo, :LineID, :ProductID,"
								+ ":CustomerID, :PartID, :PartPointID, :MaterialID, :MaterialNo, :Number, :UnitID, :ReplaceType, "
								+ ":OutsourceType,"
								+ ":OriginalType, :DisassyType,:OverLine, :PartChange, :ReceiveDepart, :StockID, :QTType, :QTItemType, "
								+ ":CustomerMaterial,"
								+ ":EditorID,now(),:Status,:RelaDemandNo,:TextCode,:WorkCenter,:DeleteID,"
								+ ":SubRelaDemandNo,:AssessmentType,:AccessoryLogo,:RepairPartClass,:Remark,:DingrongGroup,"
								+ ":RepairCoreIdentification,:PickingQuantity,:EvenExchangeRate,:Client,:OrderNum,:SourceType,:SourceID,:DifferenceItem,:OverQuota)",
						wInstance.Result);
				// 重新赋值顺序
				wBOMItem.OrderNum = APS_QueryOrdrNumber(wLoginUser, wBOMItem.OrderID, wErrorCode);
			} else {
				wSQLText = StringUtils.Format(
						"UPDATE {0}.aps_bomitem SET BOMType=:BOMType, PartID=:PartID, PartPointID=:PartPointID, MaterialID=:MaterialID, "
								+ "MaterialNo=:MaterialNo, "
								+ "Number=:Number, UnitID=:UnitID, ReplaceType=:ReplaceType, OutsourceType=:OutsourceType, "
								+ "OriginalType=:OriginalType, "
								+ "DisassyType=:DisassyType, OverLine=:OverLine, PartChange=:PartChange, ReceiveDepart=:ReceiveDepart, StockID=:StockID,"
								+ "QTType=:QTType, QTItemType=:QTItemType, CustomerMaterial=:CustomerMaterial,EditorID=:EditorID,"
								+ "EditTime=now(),RelaDemandNo=:RelaDemandNo,TextCode=:TextCode,WorkCenter=:WorkCenter,"
								+ "DeleteID=:DeleteID,SubRelaDemandNo=:SubRelaDemandNo,AssessmentType=:AssessmentType,"
								+ "AccessoryLogo=:AccessoryLogo,RepairPartClass=:RepairPartClass,Remark=:Remark,"
								+ "DingrongGroup=:DingrongGroup,RepairCoreIdentification=:RepairCoreIdentification,"
								+ "PickingQuantity=:PickingQuantity,EvenExchangeRate=:EvenExchangeRate,"
								+ "Client=:Client,OrderNum=:OrderNum,SourceType=:SourceType,SourceID=:SourceID,DifferenceItem=:DifferenceItem,OverQuota=:OverQuota"
								+ " where ID=:ID AND Status=:Status;",
						wInstance.Result);
			}

			wParms.clear();

			wParms.put("ID", wBOMItem.ID);
			wParms.put("BOMType", wBOMItem.BOMType);
			wParms.put("FactoryID", wBOMItem.FactoryID);
			wParms.put("WBSNo", wBOMItem.WBSNo);
			wParms.put("OrderID", wBOMItem.OrderID);
			wParms.put("PartNo", wBOMItem.PartNo);
			wParms.put("LineID", wBOMItem.LineID);
			wParms.put("ProductID", wBOMItem.ProductID);
			wParms.put("CustomerID", wBOMItem.CustomerID);
			wParms.put("PartID", wBOMItem.PartID);
			wParms.put("PartPointID", wBOMItem.PartPointID);
			wParms.put("MaterialID", wBOMItem.MaterialID);
			wParms.put("MaterialNo", wBOMItem.MaterialNo);
			wParms.put("Number", wBOMItem.Number);
			wParms.put("UnitID", wBOMItem.UnitID);
			wParms.put("ReplaceType", wBOMItem.ReplaceType);
			wParms.put("OutsourceType", wBOMItem.OutsourceType);
			wParms.put("OriginalType", wBOMItem.OriginalType);
			wParms.put("DisassyType", wBOMItem.DisassyType);
			wParms.put("OverLine", wBOMItem.OverLine);
			wParms.put("PartChange", wBOMItem.PartChange);
			wParms.put("ReceiveDepart", wBOMItem.ReceiveDepart);
			wParms.put("StockID", wBOMItem.StockID);
			wParms.put("QTType", wBOMItem.QTType);
			wParms.put("QTItemType", wBOMItem.QTItemType);
			wParms.put("CustomerMaterial", wBOMItem.CustomerMaterial);

			wParms.put("EditorID", wBOMItem.EditorID);
			wParms.put("Status", BPMStatus.Save.getValue());

			wParms.put("RelaDemandNo", wBOMItem.RelaDemandNo);
			wParms.put("TextCode", wBOMItem.TextCode);
			wParms.put("WorkCenter", wBOMItem.WorkCenter);
			wParms.put("DeleteID", wBOMItem.DeleteID);
			wParms.put("SubRelaDemandNo", wBOMItem.SubRelaDemandNo);
			wParms.put("AssessmentType", wBOMItem.AssessmentType);
			wParms.put("AccessoryLogo", wBOMItem.AccessoryLogo);
			wParms.put("RepairPartClass", wBOMItem.RepairPartClass);
			wParms.put("Remark", wBOMItem.Remark);
			wParms.put("DingrongGroup", wBOMItem.DingrongGroup);
			wParms.put("RepairCoreIdentification", wBOMItem.RepairCoreIdentification);
			wParms.put("PickingQuantity", wBOMItem.PickingQuantity);
			wParms.put("EvenExchangeRate", wBOMItem.EvenExchangeRate);
			wParms.put("Client", wBOMItem.Client);
			wParms.put("OrderNum", wBOMItem.OrderNum);
			wParms.put("SourceType", wBOMItem.SourceType);
			wParms.put("SourceID", wBOMItem.SourceID);
			wParms.put("DifferenceItem", wBOMItem.DifferenceItem);
			wParms.put("OverQuota", wBOMItem.OverQuota);

			wSQLText = this.DMLChange(wSQLText);

			if (wBOMItem.ID <= 0) {
				KeyHolder keyHolder = new GeneratedKeyHolder();
				SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParms);
				nameJdbcTemplate.update(wSQLText, wSqlParameterSource, keyHolder);

				wBOMItem.setID(keyHolder.getKey().intValue());
				wResult = keyHolder.getKey().intValue();
			} else {
				nameJdbcTemplate.update(wSQLText, wParms);
				wResult = wBOMItem.ID;
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
			LoggerTool.SaveException("APSBOMItemDAO", "APS_UpdateBOMItem", "Function Exception:" + ex.toString());
		}
		return wBOMItem.getID();
	}

	public int APS_QueryOrdrNumber(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT max(OrderNum)+1  Number FROM {0}.aps_bomitem where OrderID=:OrderID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("Number"));
				if (wResult <= 0) {
					wResult = 1;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public synchronized void APS_CreateBOMItem(BMSEmployee wLoginUser, List<APSBOMItem> wBOMItemList,
			OutResult<Integer> wErrorCode) {
		wErrorCode.set(0);
		try {
			if (wBOMItemList == null || wBOMItemList.size() <= 0) {
				return;
			}
			int wOrderID = wBOMItemList.get(0).OrderID;
			if (wOrderID <= 0) {
				wErrorCode.set(MESException.Parameter.getValue());
				return;
			}

			int wStatus = this.APS_QueryBOMItemCount(wLoginUser, wOrderID, wErrorCode);

			if (wStatus == BPMStatus.Save.getValue()) {
				// 删除
				this.APS_DeleteBOMItem(wLoginUser, wOrderID, wErrorCode);
				wStatus = 0;
			}

			if (wStatus != 0 || wErrorCode.Result != 0) {
				return;
			}

			// 查询订单是否存在已生成的台车BOM 可以全删
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, RoleFunctionID);

			if (wErrorCode.Result != 0) {
				return;
			}
			Map<String, Object> wParms = new HashMap<String, Object>();

			String wValueTemp = "( {0}, {1}, ''{2}'', {3}, ''{4}'', {5}, {6},"
					+ "{7}, {8}, {9}, {10}, ''{11}'', {12}, {13}, {14}, {15},"
					+ "{16}, {17},{18}, {19}, ''{20}'', {21}, {22}, {23}, {24},{25},now(),{26},"
					+ "''{27}'',''{28}'',''{29}'',''{30}'',''{31}'',''{32}'',''{33}'',''{34}'',''{35}'',''{36}'',''{37}'',{38},{39},''{40}'',{41},{42},{43},{44},{45})";

			List<String> wValueStringList = new ArrayList<String>();

			for (APSBOMItem wAPSBOMItem : wBOMItemList) {
				wValueStringList.add(StringUtils.Format(wValueTemp, wAPSBOMItem.BOMType, wAPSBOMItem.FactoryID,
						wAPSBOMItem.WBSNo, wAPSBOMItem.OrderID, wAPSBOMItem.PartNo, wAPSBOMItem.LineID,
						wAPSBOMItem.ProductID, wAPSBOMItem.CustomerID, wAPSBOMItem.PartID, wAPSBOMItem.PartPointID,
						wAPSBOMItem.MaterialID, wAPSBOMItem.MaterialNo, wAPSBOMItem.Number, wAPSBOMItem.UnitID,
						wAPSBOMItem.ReplaceType, wAPSBOMItem.OutsourceType, wAPSBOMItem.OriginalType,
						wAPSBOMItem.DisassyType, wAPSBOMItem.OverLine, wAPSBOMItem.PartChange,
						wAPSBOMItem.ReceiveDepart, wAPSBOMItem.StockID, wAPSBOMItem.QTType, wAPSBOMItem.QTItemType,
						wAPSBOMItem.CustomerMaterial, wAPSBOMItem.EditorID, BPMStatus.Save.getValue(),
						wAPSBOMItem.RelaDemandNo, wAPSBOMItem.TextCode, wAPSBOMItem.WorkCenter, wAPSBOMItem.DeleteID,
						wAPSBOMItem.SubRelaDemandNo, wAPSBOMItem.AssessmentType, wAPSBOMItem.AccessoryLogo,
						wAPSBOMItem.RepairPartClass, wAPSBOMItem.Remark, wAPSBOMItem.DingrongGroup,
						wAPSBOMItem.RepairCoreIdentification, wAPSBOMItem.PickingQuantity, wAPSBOMItem.EvenExchangeRate,
						wAPSBOMItem.Client, wAPSBOMItem.OrderNum, wAPSBOMItem.SourceType, wAPSBOMItem.SourceID,
						wAPSBOMItem.DifferenceItem, wAPSBOMItem.OverQuota));
			}

			if (wValueStringList.size() <= 0) {
				return;
			}

			String wSQLText = StringUtils.Format(
					"INSERT INTO {0}.aps_bomitem ( BOMType, FactoryID, WBSNo, OrderID, PartNo, LineID, ProductID,CustomerID, PartID, "
							+ "PartPointID, MaterialID, MaterialNo, Number, UnitID, ReplaceType, OutsourceType, OriginalType, DisassyType, "
							+ "OverLine, PartChange, ReceiveDepart, StockID, QTType, QTItemType, CustomerMaterial,EditorID,"
							+ "EditTime,Status,RelaDemandNo,TextCode,WorkCenter,DeleteID,SubRelaDemandNo,AssessmentType,"
							+ "AccessoryLogo,RepairPartClass,Remark,DingrongGroup,RepairCoreIdentification,PickingQuantity,"
							+ "EvenExchangeRate,Client,OrderNum,SourceType,SourceID,DifferenceItem,OverQuota)  Values {1}",
					wInstance.Result, StringUtils.Join(",", wValueStringList));

			nameJdbcTemplate.update(wSQLText, wParms);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
			LoggerTool.SaveException("APSBOMItemDAO", "APS_CreateBOMItem", "Function Exception:" + ex.toString());
		}
	}

	public int APS_DeleteBOMItem(BMSEmployee wLoginUser, APSBOMItem wBOMItem, OutResult<Integer> wErrorCode) {
		wErrorCode.set(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, RoleFunctionID);

			if (wErrorCode.Result == 0) {
				Map<String, Object> wParms = new HashMap<String, Object>();

				String wSQLText = StringUtils.Format("Delete from {0}.aps_bomitem  Where ID=:ID and Status = :Status;",
						wInstance.Result);
				wParms.clear();

				wParms.put("ID", wBOMItem.ID);
				wParms.put("Status", BPMStatus.Save.getValue());
				wSQLText = this.DMLChange(wSQLText);
				nameJdbcTemplate.update(wSQLText, wParms);
			}
		} catch (Exception ex) {

			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
			LoggerTool.SaveException("APSBOMItemDAO", "APS_DeleteBOMItem", "Function Exception:" + ex.toString());
		}
		return wErrorCode.Result;
	}

	public int APS_DeleteBOMItem(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		wErrorCode.set(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, RoleFunctionID);

			if (wErrorCode.Result == 0) {
				Map<String, Object> wParms = new HashMap<String, Object>();

				String wSQLText = StringUtils.Format(
						"Delete from {0}.aps_bomitem  Where ID>0 and OrderID = :OrderID AND Status=:Status;",
						wInstance.Result);
				wParms.clear();

				wParms.put("OrderID", wOrderID);
				wParms.put("Status", BPMStatus.Save.getValue());
				wSQLText = this.DMLChange(wSQLText);
				nameJdbcTemplate.update(wSQLText, wParms);
			}
		} catch (Exception ex) {

			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
			LoggerTool.SaveException("APSBOMItemDAO", "APS_DeleteBOMItem", "Function Exception:" + ex.toString());
		}
		return wErrorCode.Result;
	}

	private List<APSBOMItem> APS_QueryBOMItemList(BMSEmployee wLoginUser, int wID, int wOrderID, String wWBSNo,
			String wPartNo, int wLineID, int wProductID, int wCustomerID, int wPartID, int wPartPointID,
			int wMaterialID, String wMaterialNo, int wBOMType, int wReplaceType, int wOutsourceType,
			List<Integer> wStatus, int wDifferenceItem, int wOverQuota, int wSourceType,
			OutResult<Integer> wErrorCode) {
		List<APSBOMItem> wBOMItemList = new ArrayList<APSBOMItem>();
		wErrorCode.set(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic);
			if (wErrorCode.Result != 0) {
				return wBOMItemList;
			}

			if (wStatus == null) {
				wStatus = new ArrayList<Integer>();
			}
			wStatus.removeIf(p -> p <= 0);

			if (wWBSNo == null)
				wWBSNo = "";

			if (wPartNo == null)
				wPartNo = "";

			if (wMaterialNo == null)
				wMaterialNo = "";

			Map<String, Object> wParms = new HashMap<String, Object>();

			String wSQLText = StringUtils.Format(
					"SELECT t.*,m.MaterialName,p.ProductNo FROM {0}.aps_bomitem t left join {0}.mss_material m on  t.MaterialID=m.ID "
							+ "left join {0}.fpc_product p on  t.ProductID=p.ID  " + " where t.ID > 0 "
							+ "and (:ID<=0 or t.ID=:ID) " + "and (:OrderID<=0 or t.OrderID=:OrderID) "
							+ "and (:LineID<=0 or t.LineID=:LineID)" + " and (:WBSNo = '''' or t.WBSNo=:WBSNo) "
							+ "and (:PartNo = '''' or t.PartNo=:PartNo) " + " and (:PartID<=0 or t.PartID=:PartID) "
							+ " and (:DifferenceItem<=0 or t.DifferenceItem=:DifferenceItem) "
							+ " and (:OverQuota<=0 or t.OverQuota=:OverQuota) "
							+ " and (:SourceType<=0 or t.SourceType=:SourceType) "
							+ " and (:PartPointID<=0 or t.PartPointID=:PartPointID) "
							+ " and (:MaterialNo = '''' or t.MaterialNo=:MaterialNo) and (:MaterialID<=0 or t.MaterialID=:MaterialID) "
							+ " and (:ReplaceType<=0 or t.ReplaceType=:ReplaceType) and (:OutsourceType<=0 or t.OutsourceType=:OutsourceType) "
							+ " and (:ProductID<=0 or t.ProductID=:ProductID) and (:BOMType<=0 or t.BOMType=:BOMType) "
							+ " and (:CustomerID<=0 or t.CustomerID=:CustomerID) and (:Status = '''' or t.Status IN ({1}))"
							+ " order by t.ID ;",
					wInstance.Result, wStatus.size() > 0 ? StringUtils.Join(",", wStatus) : "0");

			wParms.clear();
			wParms.put("ID", wID);
			wParms.put("OrderID", wOrderID);
			wParms.put("WBSNo", wWBSNo);
			wParms.put("PartNo", wPartNo);
			wParms.put("LineID", wLineID);
			wParms.put("ProductID", wProductID);
			wParms.put("CustomerID", wCustomerID);
			wParms.put("BOMType", wBOMType);
			wParms.put("Status", StringUtils.Join(",", wStatus));
			wParms.put("PartID", wPartID);
			wParms.put("PartPointID", wPartPointID);
			wParms.put("MaterialID", wMaterialID);
			wParms.put("MaterialNo", wMaterialNo);
			wParms.put("ReplaceType", wReplaceType);
			wParms.put("OutsourceType", wOutsourceType);
			wParms.put("DifferenceItem", wDifferenceItem);
			wParms.put("OverQuota", wOverQuota);
			wParms.put("SourceType", wSourceType);

			wSQLText = this.DMLChange(wSQLText);
			List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
			for (Map<String, Object> wReader : wQueryResultList) {
				APSBOMItem wBOMItem = new APSBOMItem();

				wBOMItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wBOMItem.BOMType = StringUtils.parseInt(wReader.get("BOMType"));
				wBOMItem.FactoryID = StringUtils.parseInt(wReader.get("FactoryID"));
				wBOMItem.WBSNo = StringUtils.parseString(wReader.get("WBSNo"));
				wBOMItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wBOMItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wBOMItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wBOMItem.LineName = QMSConstants.GetFMCLineName(wBOMItem.LineID);
				wBOMItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wBOMItem.ProductNo = StringUtils.parseString(wReader.get("ProductNo"));
				wBOMItem.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wBOMItem.CustomerCode = QMSConstants.GetCRMCustomer(wBOMItem.CustomerID).getCustomerCode();
				wBOMItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wBOMItem.PartName = QMSConstants.GetFPCPartName(wBOMItem.PartID);
				wBOMItem.PartPointID = StringUtils.parseInt(wReader.get("PartPointID"));
				wBOMItem.PartPointName = QMSConstants.GetFPCStepName(wBOMItem.PartPointID);
				wBOMItem.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wBOMItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wBOMItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));
				wBOMItem.Number = StringUtils.parseDouble(wReader.get("Number"));
				wBOMItem.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wBOMItem.UnitText = QMSConstants.GetCFGUnitName(wBOMItem.UnitID);
				wBOMItem.ReplaceType = StringUtils.parseInt(wReader.get("ReplaceType"));
				wBOMItem.OutsourceType = StringUtils.parseInt(wReader.get("OutsourceType"));
				wBOMItem.OriginalType = StringUtils.parseInt(wReader.get("OriginalType"));
				wBOMItem.DisassyType = StringUtils.parseInt(wReader.get("DisassyType"));
				wBOMItem.OverLine = StringUtils.parseInt(wReader.get("OverLine"));
				wBOMItem.PartChange = StringUtils.parseInt(wReader.get("PartChange"));
				wBOMItem.ReceiveDepart = StringUtils.parseString(wReader.get("ReceiveDepart"));
				wBOMItem.StockID = StringUtils.parseInt(wReader.get("StockID"));
				wBOMItem.QTType = StringUtils.parseInt(wReader.get("QTType"));
				wBOMItem.QTItemType = StringUtils.parseInt(wReader.get("QTItemType"));
				wBOMItem.CustomerMaterial = StringUtils.parseInt(wReader.get("CustomerMaterial"));
				wBOMItem.AuditorID = StringUtils.parseInt(wReader.get("AuditorID"));
				wBOMItem.Auditor = QMSConstants.GetBMSEmployeeName(wBOMItem.AuditorID);
				wBOMItem.AuditTime = StringUtils.parseCalendar(wReader.get("AuditTime"));
				wBOMItem.EditorID = StringUtils.parseInt(wReader.get("EditorID"));
				wBOMItem.Editor = QMSConstants.GetBMSEmployeeName(wBOMItem.EditorID);
				wBOMItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wBOMItem.Status = StringUtils.parseInt(wReader.get("Status"));

				wBOMItem.RelaDemandNo = StringUtils.parseString(wReader.get("RelaDemandNo"));
				wBOMItem.TextCode = StringUtils.parseString(wReader.get("TextCode"));
				wBOMItem.WorkCenter = StringUtils.parseString(wReader.get("WorkCenter"));
				wBOMItem.DeleteID = StringUtils.parseString(wReader.get("DeleteID"));
				wBOMItem.SubRelaDemandNo = StringUtils.parseString(wReader.get("SubRelaDemandNo"));
				wBOMItem.AssessmentType = StringUtils.parseString(wReader.get("AssessmentType"));
				wBOMItem.AccessoryLogo = StringUtils.parseString(wReader.get("AccessoryLogo"));
				wBOMItem.RepairPartClass = StringUtils.parseString(wReader.get("RepairPartClass"));
				wBOMItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wBOMItem.DingrongGroup = StringUtils.parseString(wReader.get("DingrongGroup"));
				wBOMItem.RepairCoreIdentification = StringUtils.parseString(wReader.get("RepairCoreIdentification"));
				wBOMItem.PickingQuantity = StringUtils.parseInt(wReader.get("PickingQuantity"));
				wBOMItem.EvenExchangeRate = StringUtils.parseDouble(wReader.get("EvenExchangeRate"));
				wBOMItem.Client = StringUtils.parseString(wReader.get("Client"));
				wBOMItem.OrderNum = StringUtils.parseInt(wReader.get("OrderNum"));
				wBOMItem.SourceType = StringUtils.parseInt(wReader.get("SourceType"));
				wBOMItem.SourceID = StringUtils.parseInt(wReader.get("SourceID"));
				wBOMItem.DifferenceItem = StringUtils.parseInt(wReader.get("DifferenceItem"));
				wBOMItem.OverQuota = StringUtils.parseInt(wReader.get("OverQuota"));

				wBOMItemList.add(wBOMItem);

			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error("APSBOMItemDAO APS_QueryBOMItemList Error:", ex.toString());
		}
		return wBOMItemList;
	}

	public List<APSBOMItem> APS_QueryBOMItemList(BMSEmployee wLoginUser, int wOrderID, String wWBSNo, String wPartNo,
			int wLineID, int wProductID, int wCustomerID, int wPartID, int wPartPointID, int wMaterialID,
			String wMaterialNo, int wBOMType, int wReplaceType, int wOutsourceType, List<Integer> wStatus,
			int wDifferenceItem, int wOverQuota, int wSourceType, OutResult<Integer> wErrorCode) {
		List<APSBOMItem> wBOMItemList = new ArrayList<APSBOMItem>();
		wErrorCode.set(0);
		try {
			wBOMItemList = this.APS_QueryBOMItemList(wLoginUser, 0, wOrderID, wWBSNo, wPartNo, wLineID, wProductID,
					wCustomerID, wPartID, wPartPointID, wMaterialID, wMaterialNo, wBOMType, wReplaceType,
					wOutsourceType, wStatus, wDifferenceItem, wOverQuota, wSourceType, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
			LoggerTool.SaveException("APSBOMItemDAO", "APS_QueryBOMItemList", "Function Exception:" + ex.toString());
		}
		return wBOMItemList;
	}

	public int APS_QueryBOMItemCount(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		wErrorCode.set(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			String wSQLText = StringUtils.Format(
					"SELECT DISTINCT Status  from {0}.aps_bomitem t where t.ID > 0 and (:OrderID<=0 or t.OrderID=:OrderID) ;",
					wInstance.Result);

			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.clear();
			wParms.put("OrderID", wOrderID);
			wSQLText = this.DMLChange(wSQLText);
			List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
			int wStatus = 0;
			for (Map<String, Object> wReader : wQueryResultList) {
				wStatus = StringUtils.parseInt(wReader.get("Status"));
				if (wStatus > wResult)
					wResult = wStatus;
			}

		} catch (Exception ex) {
			logger.error(ex.toString());
			LoggerTool.SaveException("APSBOMItemDAO", "APS_QueryBOMItemCount", "Function Exception:" + ex.toString());
		}
		return wResult;
	}

	public APSBOMItem APS_QueryBOMItemByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		APSBOMItem wBOMItem = new APSBOMItem();
		wErrorCode.set(0);
		try {
			if (wID <= 0)
				return wBOMItem;

			List<APSBOMItem> wBOMItemList = this.APS_QueryBOMItemList(wLoginUser, wID, -1, "", "", -1, -1, -1, -1, -1,
					-1, "", -1, -1, -1, null, -1, -1, -1, wErrorCode);
			if (wBOMItemList != null && wBOMItemList.size() > 0)
				wBOMItem = wBOMItemList.get(0);
		} catch (Exception ex) {

			logger.error(ex.toString());
			LoggerTool.SaveException("APSBOMItemDAO", "APS_QueryBOMItemByID", "Function Exception:" + ex.toString());
		}
		return wBOMItem;
	}

	/**
	 * 判断是否为差异项
	 */
	public int JudgeIsDifferenceItem(BMSEmployee wLoginUser, APSBOMItem wBOMItem, OutResult<Integer> wErrorCode) {
		int wResult = 1;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT count(*) Number FROM {0}.mss_bomitem where BOMID in "
					+ "(select ID from {0}.mss_bom where LineID=:wLineID and ProductID=:wProductID "
					+ "and CustomerID=:wCustomerID and IsStandard=1) and PlaceID=:wPlaceID and PartPointID=:wPartPointID "
					+ "and MaterialID=:wMaterialID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wLineID", wBOMItem.LineID);
			wParamMap.put("wProductID", wBOMItem.ProductID);
			wParamMap.put("wCustomerID", wBOMItem.CustomerID);
			wParamMap.put("wPlaceID", wBOMItem.PartID);
			wParamMap.put("wPartPointID", wBOMItem.PartPointID);
			wParamMap.put("wMaterialID", wBOMItem.MaterialID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wNumber = StringUtils.parseInt(wReader.get("Number"));
				if (wNumber > 0)
					wResult = 0;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断是否超定额
	 */
	public int JudgeIsOverQuota(BMSEmployee wLoginUser, APSBOMItem wBOMItem, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT MaterialNumber FROM {0}.mss_bomitem where BOMID in "
					+ "(select ID from {0}.mss_bom where LineID=:wLineID and ProductID=:wProductID "
					+ "and CustomerID=:wCustomerID and IsStandard=1) and PlaceID=:wPlaceID and PartPointID=:wPartPointID "
					+ "and MaterialID=:wMaterialID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wLineID", wBOMItem.LineID);
			wParamMap.put("wProductID", wBOMItem.ProductID);
			wParamMap.put("wCustomerID", wBOMItem.CustomerID);
			wParamMap.put("wPlaceID", wBOMItem.PartID);
			wParamMap.put("wPartPointID", wBOMItem.PartPointID);
			wParamMap.put("wMaterialID", wBOMItem.MaterialID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wMaterialNumber = StringUtils.parseInt(wReader.get("MaterialNumber"));
				if (wBOMItem.Number > wMaterialNumber) {
					wResult = 1;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据车型、修程、局段查找当前标准BOMID
	 */
	public int GetCurrentStandardBOMID(BMSEmployee wLoginUser, int wProductID, int wLineID, int wCustomerID,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT ID FROM {0}.mss_bom where ProductID=:wProductID "
					+ "and LineID=:wLineID and CustomerID=:wCustomerID and IsStandard=1;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wCustomerID", wCustomerID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("ID"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据物料ID查询单位ID
	 */
	public int GetUnitID(BMSEmployee wLoginUser, int wMaterialID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT CYUnitID FROM {0}.mss_material where ID=:wMaterialID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wMaterialID", wMaterialID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("CYUnitID"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询台车bom
	 */
	public APSBOMItem APS_SelectItem(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wPartPointID,
			int wMaterialID, OutResult<Integer> wErrorCode) {
		APSBOMItem wResult = new APSBOMItem();
		try {
			List<APSBOMItem> wList = this.APS_QueryBOMItemList(wLoginUser, -1, wOrderID, "", "", -1, -1, -1, wPartID,
					wPartPointID, wMaterialID, "", -1, -1, -1, null, -1, -1, -1, wErrorCode);
			if (wList != null && wList.size() > 0) {
				wResult = wList.get(0);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<Integer> SelectAssessErrorIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select t1.ID from {0}.aps_bomitem t1,{0}.fpc_part t2,"
					+ "{0}.fpc_partpoint t3,{0}.mss_material t4 where t1.MaterialID=t4.ID "
					+ "and t2.ID=t1.PartID and t3.ID=t1.PartPointID "
					+ "and t1.outsourcetype=1 and t1.replacetype=2 and t1.AssessmentType=''常规新件'' and SourceType=3;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

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

	public List<APSBOMItem> SelectByIDList(BMSEmployee wLoginUser, List<Integer> wIDList,
			OutResult<Integer> wErrorCode) {
		List<APSBOMItem> wBOMItemList = new ArrayList<APSBOMItem>();
		wErrorCode.set(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic);
			if (wErrorCode.Result != 0) {
				return wBOMItemList;
			}

			if (wIDList == null || wIDList.size() <= 0) {
				return wBOMItemList;
			}

			Map<String, Object> wParms = new HashMap<String, Object>();

			String wSQLText = StringUtils.Format(
					"SELECT t.*,m.MaterialName,p.ProductNo FROM {0}.aps_bomitem t left join {0}.mss_material m on  t.MaterialID=m.ID "
							+ "left join {0}.fpc_product p on  t.ProductID=p.ID  " + " where t.ID > 0 "
							+ "and ( t.ID IN ({1}))" + " order by t.ID ;",
					wInstance.Result, StringUtils.Join(",", wIDList));

			wParms.clear();

			wSQLText = this.DMLChange(wSQLText);
			List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
			for (Map<String, Object> wReader : wQueryResultList) {
				APSBOMItem wBOMItem = new APSBOMItem();

				wBOMItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wBOMItem.BOMType = StringUtils.parseInt(wReader.get("BOMType"));
				wBOMItem.FactoryID = StringUtils.parseInt(wReader.get("FactoryID"));
				wBOMItem.WBSNo = StringUtils.parseString(wReader.get("WBSNo"));
				wBOMItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wBOMItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wBOMItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wBOMItem.LineName = QMSConstants.GetFMCLineName(wBOMItem.LineID);
				wBOMItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wBOMItem.ProductNo = StringUtils.parseString(wReader.get("ProductNo"));
				wBOMItem.CustomerID = StringUtils.parseInt(wReader.get("CustomerID"));
				wBOMItem.CustomerCode = QMSConstants.GetCRMCustomer(wBOMItem.CustomerID).getCustomerCode();
				wBOMItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wBOMItem.PartName = QMSConstants.GetFPCPartName(wBOMItem.PartID);
				wBOMItem.PartPointID = StringUtils.parseInt(wReader.get("PartPointID"));
				wBOMItem.PartPointName = QMSConstants.GetFPCStepName(wBOMItem.PartPointID);
				wBOMItem.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wBOMItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wBOMItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));
				wBOMItem.Number = StringUtils.parseDouble(wReader.get("Number"));
				wBOMItem.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wBOMItem.UnitText = QMSConstants.GetCFGUnitName(wBOMItem.UnitID);
				wBOMItem.ReplaceType = StringUtils.parseInt(wReader.get("ReplaceType"));
				wBOMItem.OutsourceType = StringUtils.parseInt(wReader.get("OutsourceType"));
				wBOMItem.OriginalType = StringUtils.parseInt(wReader.get("OriginalType"));
				wBOMItem.DisassyType = StringUtils.parseInt(wReader.get("DisassyType"));
				wBOMItem.OverLine = StringUtils.parseInt(wReader.get("OverLine"));
				wBOMItem.PartChange = StringUtils.parseInt(wReader.get("PartChange"));
				wBOMItem.ReceiveDepart = StringUtils.parseString(wReader.get("ReceiveDepart"));
				wBOMItem.StockID = StringUtils.parseInt(wReader.get("StockID"));
				wBOMItem.QTType = StringUtils.parseInt(wReader.get("QTType"));
				wBOMItem.QTItemType = StringUtils.parseInt(wReader.get("QTItemType"));
				wBOMItem.CustomerMaterial = StringUtils.parseInt(wReader.get("CustomerMaterial"));
				wBOMItem.AuditorID = StringUtils.parseInt(wReader.get("AuditorID"));
				wBOMItem.Auditor = QMSConstants.GetBMSEmployeeName(wBOMItem.AuditorID);
				wBOMItem.AuditTime = StringUtils.parseCalendar(wReader.get("AuditTime"));
				wBOMItem.EditorID = StringUtils.parseInt(wReader.get("EditorID"));
				wBOMItem.Editor = QMSConstants.GetBMSEmployeeName(wBOMItem.EditorID);
				wBOMItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wBOMItem.Status = StringUtils.parseInt(wReader.get("Status"));

				wBOMItem.RelaDemandNo = StringUtils.parseString(wReader.get("RelaDemandNo"));
				wBOMItem.TextCode = StringUtils.parseString(wReader.get("TextCode"));
				wBOMItem.WorkCenter = StringUtils.parseString(wReader.get("WorkCenter"));
				wBOMItem.DeleteID = StringUtils.parseString(wReader.get("DeleteID"));
				wBOMItem.SubRelaDemandNo = StringUtils.parseString(wReader.get("SubRelaDemandNo"));
				wBOMItem.AssessmentType = StringUtils.parseString(wReader.get("AssessmentType"));
				wBOMItem.AccessoryLogo = StringUtils.parseString(wReader.get("AccessoryLogo"));
				wBOMItem.RepairPartClass = StringUtils.parseString(wReader.get("RepairPartClass"));
				wBOMItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wBOMItem.DingrongGroup = StringUtils.parseString(wReader.get("DingrongGroup"));
				wBOMItem.RepairCoreIdentification = StringUtils.parseString(wReader.get("RepairCoreIdentification"));
				wBOMItem.PickingQuantity = StringUtils.parseInt(wReader.get("PickingQuantity"));
				wBOMItem.EvenExchangeRate = StringUtils.parseDouble(wReader.get("EvenExchangeRate"));
				wBOMItem.Client = StringUtils.parseString(wReader.get("Client"));
				wBOMItem.OrderNum = StringUtils.parseInt(wReader.get("OrderNum"));
				wBOMItem.SourceType = StringUtils.parseInt(wReader.get("SourceType"));
				wBOMItem.SourceID = StringUtils.parseInt(wReader.get("SourceID"));
				wBOMItem.DifferenceItem = StringUtils.parseInt(wReader.get("DifferenceItem"));
				wBOMItem.OverQuota = StringUtils.parseInt(wReader.get("OverQuota"));

				wBOMItemList.add(wBOMItem);

			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error("APSBOMItemDAO APS_QueryBOMItemList Error:", ex.toString());
		}
		return wBOMItemList;
	}
}
