package com.mes.qms.server.serviceimpl.dao.mss;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class MSSBOMItemDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(MSSBOMItemDAO.class);

	private static MSSBOMItemDAO Instance = null;

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<MSSBOMItem> SelectList(BMSEmployee wLoginUser, int wBOMID, int wPartPointID, int wPlaceID,
			OutResult<Integer> wErrorCode) {
		List<MSSBOMItem> wResultList = new ArrayList<MSSBOMItem>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.mss_bomitem t1,{0}.mss_material t2 WHERE 1=1 "
					+ "and t1.MaterialID=t2.ID and ( :wBOMID <= 0 or :wBOMID = t1.BOMID ) "
					+ "and ( :wPartPointID <= 0 or :wPartPointID = t1.PartPointID ) "
					+ "and ( :wPlaceID <= 0 or :wPlaceID = t1.PlaceID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wBOMID", wBOMID);
			wParamMap.put("wPartPointID", wPartPointID);
			wParamMap.put("wPlaceID", wPlaceID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				MSSBOMItem wItem = new MSSBOMItem();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.BOMID = StringUtils.parseInt(wReader.get("BOMID"));
				wItem.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wItem.TypeID = StringUtils.parseInt(wReader.get("TypeID"));
				wItem.MaterialUnit = StringUtils.parseFloat(wReader.get("MaterialUnit"));
				wItem.MaterialUnitRatio = StringUtils.parseFloat(wReader.get("MaterialUnitRatio"));
				wItem.DeviceNo = StringUtils.parseString(wReader.get("DeviceNo"));
				wItem.Author = StringUtils.parseString(wReader.get("Author"));
				wItem.Auditor = StringUtils.parseString(wReader.get("Auditor"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.AuditTime = StringUtils.parseCalendar(wReader.get("AuditTime"));
				wItem.LossRatio = StringUtils.parseFloat(wReader.get("LossRatio"));
				wItem.Active = StringUtils.parseInt(wReader.get("Active"));
				wItem.PartPointID = StringUtils.parseInt(wReader.get("PartPointID"));
				wItem.ParentID = StringUtils.parseInt(wReader.get("ParentID"));
				wItem.GradeID = StringUtils.parseInt(wReader.get("GradeID"));
				wItem.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wItem.PlaceID = StringUtils.parseInt(wReader.get("PlaceID"));
				wItem.BOMType = StringUtils.parseInt(wReader.get("BOMType"));
				wItem.MaterialNumber = StringUtils.parseDouble(wReader.get("MaterialNumber"));
				wItem.ProductQD = StringUtils.parseString(wReader.get("ProductQD"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.ReplaceType = StringUtils.parseInt(wReader.get("ReplaceType"));
				wItem.ReplaceRatio = StringUtils.parseFloat(wReader.get("ReplaceRatio"));
				wItem.OutsourceType = StringUtils.parseInt(wReader.get("OutsourceType"));
				wItem.OriginalType = StringUtils.parseInt(wReader.get("OriginalType"));
				wItem.DisassyType = StringUtils.parseInt(wReader.get("DisassyType"));
				wItem.OrderNum = StringUtils.parseInt(wReader.get("OrderNum"));

				// 需翻译字段
				wItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));
				wItem.PartPointName = QMSConstants.GetFPCStepName(wItem.PartPointID);
				wItem.UnitText = QMSConstants.GetCFGUnitName(wItem.UnitID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private MSSBOMItemDAO() {
		super();
	}

	public static MSSBOMItemDAO getInstance() {
		if (Instance == null)
			Instance = new MSSBOMItemDAO();
		return Instance;
	}

	public int QueryBOMID(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select max(t1.ID) ID from {0}.mss_bom t1,{1}.oms_order t2 where "
							+ "t2.ID=:OrderID and t1.ProductID=t2.ProductID and t1.LineID=t2.LineID "
							+ "and t1.CustomerID=t2.BureauSectionID and t1.IsStandard=1;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wID = StringUtils.parseInt(wReader.get("ID"));
				wResult = wID;
//				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询
	 */
	public List<MSSBOMItem> SelectList(BMSEmployee wLoginUser, int wBOMID, int wStepID, int wPartID, String wMaterialNo,
			String wMaterialName, int wReplaceType, int wOutsourceType, OutResult<Integer> wErrorCode) {
		List<MSSBOMItem> wResultList = new ArrayList<MSSBOMItem>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.mss_bomitem t1,{0}.mss_material t2 WHERE 1=1 "
					+ "and t1.MaterialID=t2.ID and ( :wBOMID <= 0 or :wBOMID = t1.BOMID ) "
					+ "and ( :wPartPointID <= 0 or :wPartPointID = t1.PartPointID ) "
					+ "and ( :wMaterialNo = null or :wMaterialNo = '''' or t2.MaterialNo like ''%{1}%'' ) "
					+ "and ( :wMaterialName = null or :wMaterialName = '''' or t2.MaterialName like ''%{2}%'' ) "
					+ "and ( :wReplaceType <= 0 or :wReplaceType = t1.ReplaceType ) "
					+ "and ( :wOutsourceType <= 0 or :wOutsourceType = t1.OutsourceType ) "
					+ "and ( :wPlaceID <= 0 or :wPlaceID = t1.PlaceID );", wInstance.Result, wMaterialNo,
					wMaterialName);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wBOMID", wBOMID);
			wParamMap.put("wPartPointID", wStepID);
			wParamMap.put("wPlaceID", wPartID);
			wParamMap.put("wMaterialNo", wMaterialNo);
			wParamMap.put("wMaterialName", wMaterialName);
			wParamMap.put("wReplaceType", wReplaceType);
			wParamMap.put("wOutsourceType", wOutsourceType);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				MSSBOMItem wItem = new MSSBOMItem();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.BOMID = StringUtils.parseInt(wReader.get("BOMID"));
				wItem.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wItem.TypeID = StringUtils.parseInt(wReader.get("TypeID"));
				wItem.MaterialUnit = StringUtils.parseFloat(wReader.get("MaterialUnit"));
				wItem.MaterialUnitRatio = StringUtils.parseFloat(wReader.get("MaterialUnitRatio"));
				wItem.DeviceNo = StringUtils.parseString(wReader.get("DeviceNo"));
				wItem.Author = StringUtils.parseString(wReader.get("Author"));
				wItem.Auditor = StringUtils.parseString(wReader.get("Auditor"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.AuditTime = StringUtils.parseCalendar(wReader.get("AuditTime"));
				wItem.LossRatio = StringUtils.parseFloat(wReader.get("LossRatio"));
				wItem.Active = StringUtils.parseInt(wReader.get("Active"));
				wItem.PartPointID = StringUtils.parseInt(wReader.get("PartPointID"));
				wItem.ParentID = StringUtils.parseInt(wReader.get("ParentID"));
				wItem.GradeID = StringUtils.parseInt(wReader.get("GradeID"));
				wItem.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wItem.PlaceID = StringUtils.parseInt(wReader.get("PlaceID"));
				wItem.BOMType = StringUtils.parseInt(wReader.get("BOMType"));
				wItem.MaterialNumber = StringUtils.parseDouble(wReader.get("MaterialNumber"));
				wItem.ProductQD = StringUtils.parseString(wReader.get("ProductQD"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.ReplaceType = StringUtils.parseInt(wReader.get("ReplaceType"));
				wItem.ReplaceRatio = StringUtils.parseFloat(wReader.get("ReplaceRatio"));
				wItem.OutsourceType = StringUtils.parseInt(wReader.get("OutsourceType"));
				wItem.OriginalType = StringUtils.parseInt(wReader.get("OriginalType"));
				wItem.DisassyType = StringUtils.parseInt(wReader.get("DisassyType"));
				wItem.OrderNum = StringUtils.parseInt(wReader.get("OrderNum"));

				// 需翻译字段
				wItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));
				wItem.PartPointName = QMSConstants.GetFPCStepName(wItem.PartPointID);
				wItem.UnitText = QMSConstants.GetCFGUnitName(wItem.UnitID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 批量激活、禁用标准BOM子项
	 */
	public Integer MSS_ActiveListBomItem(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive,
			OutResult<Integer> wErrorCode) {
		Integer wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIDList == null || wIDList.size() <= 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("update {0}.mss_bomitem set Active=:Active where ID in ({1});",
					wInstance.Result, StringUtils.Join(",", wIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("Active", wActive);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public void UpdateLevelList(BMSEmployee wLoginUser, String wMaterialNo, int wLevel, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"update {0}.mss_material set BOMID=:wLevel where MaterialNo in (''{1}'') and ID>0;",
					wInstance.Result, wMaterialNo);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wLevel", wLevel);
			wParamMap.put("wMaterial", wMaterialNo);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}

	}
}
