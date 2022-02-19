package com.mes.qms.server.serviceimpl.dao.ipt;

import java.util.ArrayList;
import java.util.Collections;
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
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class IPTItemDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTItemDAO.class);

	private static IPTItemDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTItem
	 * @return
	 */
	public long Update(BMSEmployee wLoginUser, long wVID, IPTItem wIPTItem, OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTItem == null)
				return 0;

			String wSQL = "";
			if (wIPTItem.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.ipt_itemrecord(GroupID,Text,StandardType,StandardValue,StandardBasic,DefaultValue,"
								+ "StandardLeft,StandardRight,Standard,UnitID,Unit,Visiable,ValueSource,"
								+ "VID,ItemType,IPTSOPList,Active,Details,Process,DefaultStationID,"
								+ "OtherValue,DefaultManufactor,DefaultModal,DefaultNumber,IsShowStandard,Legend,"
								+ "SerialNumber,Code,DefaultPartPointID,IsWriteFill,IsPictureFill,IsVideoFill,"
								+ "ManufactorOption,ModalOption,"
								+ "IsManufactorFill,IsModalFill,IsNumberFill,IsPeriodChange,ProjectNo,CheckPoint,"
								+ "IsQuality,PartsCoding,OrderID,ConfigID,AutoCalc,WorkContent,WorkTime,FileNames,"
								+ "OrderType,DisassemblyComponents,RepairParts,AssemblyParts,Components,"
								+ "DisassemblyComponentsID,RepairPartsID,AssemblyPartsID,ComponentsID,SRDisassemblyComponentsID,SRDisassemblyComponents,SRRepairPartsID,SRRepairParts,SRAssemblyPartsID,SRAssemblyParts) "
								+ "VALUES(:GroupID,:Text,:StandardType,:StandardValue,:StandardBasic,:DefaultValue,:StandardLeft,"
								+ ":StandardRight,:Standard,:UnitID,:Unit,:Visiable,:ValueSource,:VID,"
								+ ":ItemType,:IPTSOPList,:Active,:Details,:Process,:DefaultStationID,"
								+ ":OtherValue,:DefaultManufactor,:DefaultModal,:DefaultNumber,:IsShowStandard,:Legend,"
								+ ":SerialNumber,:Code,:DefaultPartPointID,:IsWriteFill,:IsPictureFill,:IsVideoFill,"
								+ ":ManufactorOption,:ModalOption,:IsManufactorFill,:IsModalFill,:IsNumberFill,"
								+ ":IsPeriodChange,:ProjectNo,:CheckPoint,:IsQuality,:PartsCoding,:OrderID,:ConfigID,"
								+ ":AutoCalc,:WorkContent,:WorkTime,:FileNames,:OrderType,"
								+ ":DisassemblyComponents,:RepairParts,:AssemblyParts,:Components,"
								+ ":DisassemblyComponentsID,:RepairPartsID,:AssemblyPartsID,:ComponentsID,:SRDisassemblyComponentsID,:SRDisassemblyComponents,:SRRepairPartsID,:SRRepairParts,:SRAssemblyPartsID,SRAssemblyParts);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.ipt_itemrecord SET GroupID=:GroupID,Text = :Text,StandardType = :StandardType,"
								+ "StandardValue = :StandardValue,StandardBasic = :StandardBasic,DefaultValue = :DefaultValue,"
								+ "StandardLeft = :StandardLeft,StandardRight = :StandardRight,Standard = :Standard,"
								+ "UnitID=:UnitID,Unit = :Unit,Visiable = :Visiable,ValueSource = :ValueSource,VID = :VID,"
								+ "ItemType = :ItemType,IPTSOPList = :IPTSOPList,Active=:Active,Details=:Details,"
								+ "Process=:Process,DefaultStationID=:DefaultStationID,"
								+ "OtherValue=:OtherValue,DefaultManufactor=:DefaultManufactor,DefaultModal=:DefaultModal,"
								+ "DefaultNumber=:DefaultNumber,IsShowStandard=:IsShowStandard,Legend=:Legend,"
								+ "SerialNumber=:SerialNumber,Code=:Code,DefaultPartPointID=:DefaultPartPointID,"
								+ "IsWriteFill=:IsWriteFill,IsPictureFill=:IsPictureFill,IsVideoFill=:IsVideoFill,"
								+ "ManufactorOption=:ManufactorOption,ModalOption=:ModalOption,"
								+ "IsManufactorFill=:IsManufactorFill,IsModalFill=:IsModalFill,IsNumberFill=:IsNumberFill,"
								+ "IsPeriodChange=:IsPeriodChange,ProjectNo=:ProjectNo,"
								+ "CheckPoint=:CheckPoint,IsQuality=:IsQuality,PartsCoding=:PartsCoding,"
								+ "OrderID=:OrderID,ConfigID=:ConfigID,AutoCalc=:AutoCalc,"
								+ "WorkContent=:WorkContent,WorkTime=:WorkTime,FileNames=:FileNames,"
								+ "OrderType=:OrderType,DisassemblyComponents=:DisassemblyComponents,"
								+ "RepairParts=:RepairParts,AssemblyParts=:AssemblyParts,"
								+ "Components=:Components,DisassemblyComponentsID=:DisassemblyComponentsID,"
								+ "RepairPartsID=:RepairPartsID,AssemblyPartsID=:AssemblyPartsID,ComponentsID=:ComponentsID,"
								+ "SRDisassemblyComponentsID=:SRDisassemblyComponentsID,"
								+ "SRDisassemblyComponents=:SRDisassemblyComponents,SRRepairPartsID=:SRRepairPartsID,"
								+ "SRRepairParts=:SRRepairParts,SRAssemblyPartsID=:SRAssemblyPartsID,"
								+ "SRAssemblyParts=:SRAssemblyParts WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTItem.ID);
			wParamMap.put("GroupID", wIPTItem.GroupID);
			wParamMap.put("Text", wIPTItem.Text);
			wParamMap.put("StandardType", wIPTItem.StandardType);
			wParamMap.put("StandardValue", wIPTItem.StandardValue);
			wParamMap.put("StandardBasic", wIPTItem.StandardBaisc);
			wParamMap.put("DefaultValue", wIPTItem.DefaultValue);
			wParamMap.put("StandardLeft", wIPTItem.StandardLeft);
			wParamMap.put("StandardRight", wIPTItem.StandardRight);
			wParamMap.put("Standard", wIPTItem.Standard);
			wParamMap.put("UnitID", wIPTItem.UnitID);
			wParamMap.put("Unit", wIPTItem.Unit);
			wParamMap.put("Visiable", wIPTItem.Visiable);
			wParamMap.put("ValueSource", StringUtils.Join(";", wIPTItem.ValueSource));
			wParamMap.put("VID", wVID);
			wParamMap.put("ItemType", wIPTItem.ItemType);
			wParamMap.put("Active", wIPTItem.Active);
			wParamMap.put("Details", wIPTItem.Details);
			wParamMap.put("Process", wIPTItem.Process);
			wParamMap.put("DefaultStationID", wIPTItem.DefaultStationID);
			wParamMap.put("OtherValue", wIPTItem.OtherValue);
			wParamMap.put("DefaultManufactor", wIPTItem.DefaultManufactor);
			wParamMap.put("DefaultModal", wIPTItem.DefaultModal);
			wParamMap.put("DefaultNumber", wIPTItem.DefaultNumber);
			wParamMap.put("IsShowStandard", wIPTItem.IsShowStandard ? 1 : 0);
			wParamMap.put("Legend", wIPTItem.Legend);
			wParamMap.put("SerialNumber", wIPTItem.SerialNumber);
			wParamMap.put("Code", wIPTItem.Code);
			wParamMap.put("DefaultPartPointID", wIPTItem.DefaultPartPointID);
			wParamMap.put("IsWriteFill", wIPTItem.IsWriteFill);
			wParamMap.put("IsPictureFill", wIPTItem.IsPictureFill);
			wParamMap.put("IsVideoFill", wIPTItem.IsVideoFill);
			wParamMap.put("ManufactorOption", StringUtils.Join(";", wIPTItem.ManufactorOption));
			wParamMap.put("ModalOption", StringUtils.Join(";", wIPTItem.ModalOption));
			wParamMap.put("IsManufactorFill", wIPTItem.IsManufactorFill);
			wParamMap.put("IsModalFill", wIPTItem.IsModalFill);
			wParamMap.put("IsNumberFill", wIPTItem.IsNumberFill);
			wParamMap.put("IsPeriodChange", wIPTItem.IsPeriodChange);
			wParamMap.put("ProjectNo", wIPTItem.ProjectNo);
			wParamMap.put("CheckPoint", wIPTItem.CheckPoint);
			wParamMap.put("IsQuality", wIPTItem.IsQuality);
			wParamMap.put("PartsCoding", wIPTItem.PartsCoding);
			wParamMap.put("OrderID", wIPTItem.OrderID);
			wParamMap.put("ConfigID", wIPTItem.ConfigID);
			wParamMap.put("AutoCalc", wIPTItem.AutoCalc);
			wParamMap.put("WorkContent", wIPTItem.WorkContent);
			wParamMap.put("WorkTime", wIPTItem.WorkTime);
			wParamMap.put("FileNames", wIPTItem.FileNames);
			wParamMap.put("OrderType", wIPTItem.OrderType);

			wParamMap.put("DisassemblyComponents", wIPTItem.DisassemblyComponents);
			wParamMap.put("RepairParts", wIPTItem.RepairParts);
			wParamMap.put("AssemblyParts", wIPTItem.AssemblyParts);
			wParamMap.put("Components", wIPTItem.Components);

			wParamMap.put("DisassemblyComponentsID", wIPTItem.DisassemblyComponentsID);
			wParamMap.put("RepairPartsID", wIPTItem.RepairPartsID);
			wParamMap.put("AssemblyPartsID", wIPTItem.AssemblyPartsID);
			wParamMap.put("ComponentsID", wIPTItem.ComponentsID);

			wParamMap.put("SRDisassemblyComponentsID", wIPTItem.SRDisassemblyComponentsID);
			wParamMap.put("SRDisassemblyComponents", wIPTItem.SRDisassemblyComponents);
			wParamMap.put("SRRepairPartsID", wIPTItem.SRRepairPartsID);
			wParamMap.put("SRRepairParts", wIPTItem.SRRepairParts);
			wParamMap.put("SRAssemblyPartsID", wIPTItem.SRAssemblyPartsID);
			wParamMap.put("SRAssemblyParts", wIPTItem.SRAssemblyParts);

			String wIPTSOPList = "";
			if (wIPTItem.IPTSOPList != null && wIPTItem.IPTSOPList.size() > 0) {
				List<Integer> wIDList = new ArrayList<Integer>();
				for (IPTSOP wIPTSOP : wIPTItem.IPTSOPList) {
					wIDList.add(wIPTSOP.ID);
				}
				wIPTSOPList = StringUtils.Join(";", wIDList);
			}
			wParamMap.put("IPTSOPList", wIPTSOPList);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTItem.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTItem.setID(wResult);
			} else {
				wResult = wIPTItem.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTItemDAO", "Update", ex.toString()));
		}
		return wResult;
	}

	/**
	 * 删除集合
	 * 
	 * @param wList
	 */
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTItem> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (IPTItem wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.ipt_itemrecord WHERE ID IN({0}) ;",
					String.join(",", wIDList), wInstance.Result);
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTItemDAO", "DeleteList", ex.toString()));
		}
		return wResult;
	}

	/**
	 * 查单条
	 * 
	 * @return
	 */
	public IPTItem SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTItem wResult = new IPTItem();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTItem> wList = SelectList(wLoginUser, wID, -1, -1, -1, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTItemDAO", "SelectByID", e.toString()));
		}
		return wResult;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<IPTItem> SelectList(BMSEmployee wLoginUser, long wID, long wVID, int wItemType, int wGroupID,
			OutResult<Integer> wErrorCode) {
		List<IPTItem> wResultList = new ArrayList<IPTItem>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils
					.Format("SELECT * FROM {0}.ipt_itemrecord WHERE  1=1  " + "and ( :wID <= 0 or :wID = ID ) "
							+ "and ( :wVID <= 0 or :wVID = VID ) " + "and ( :wGroupID <= 0 or :wGroupID = GroupID ) "
							+ "and ( :wItemType <= 0 or :wItemType = ItemType );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wVID", wVID);
			wParamMap.put("wItemType", wItemType);
			wParamMap.put("wGroupID", wGroupID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTItemDAO", "SelectList", ex.toString()));
		}
		return wResultList;
	}

	/**
	 * 根据ID集合查询项列表
	 * 
	 * @return
	 */
	public List<IPTItem> SelectByIDList(BMSEmployee wLoginUser, List<Integer> wIDList, OutResult<Integer> wErrorCode) {
		List<IPTItem> wResultList = new ArrayList<IPTItem>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wIDList == null) {
				wIDList = new ArrayList<Integer>();
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ipt_itemrecord WHERE  1=1  "
							+ "and ( :wIDs is null or :wIDs = '''' or ID in ({1}));",
					wInstance.Result, wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wIDs", StringUtils.Join(",", wIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTItemDAO", "SelectList", ex.toString()));
		}
		return wResultList;
	}

	/**
	 * 查询专检单未做的项
	 * 
	 * @return
	 */
	public List<IPTItem> SelectList(BMSEmployee wLoginUser, int wSpecialTaskID, OutResult<Integer> wErrorCode) {
		List<IPTItem> wResultList = new ArrayList<IPTItem>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.ipt_itemrecord "
					+ "where Vid in(select ModuleVersionID from {1}.sfc_taskipt where ID=:SpecialTaskID) "
					+ "and ItemType != 4 and ID not in(SELECT IPTItemID FROM {0}.ipt_value where TaskID=:SpecialTaskID "
					+ "and IPTMode=13 and Status=2);", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("SpecialTaskID", wSpecialTaskID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTItemDAO", "SelectList", ex.toString()));
		}
		return wResultList;
	}

	/**
	 * 赋值操作
	 * 
	 * @param wLoginUser
	 * @param wErrorCode
	 * @param wResultList
	 * @param wQueryResult
	 */
	private void SetValue(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode, List<IPTItem> wResultList,
			List<Map<String, Object>> wQueryResult) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				IPTItem wItem = new IPTItem();

				wItem.ID = StringUtils.parseLong(wReader.get("ID"));
				wItem.GroupID = StringUtils.parseInt(wReader.get("GroupID"));
				wItem.Text = StringUtils.parseString(wReader.get("Text"));
				wItem.StandardType = StringUtils.parseInt(wReader.get("StandardType"));
				wItem.StandardValue = StringUtils.parseString(wReader.get("StandardValue"));
				wItem.StandardBaisc = StringUtils.parseString(wReader.get("StandardBasic"));
				wItem.DefaultValue = StringUtils.parseString(wReader.get("DefaultValue"));
				wItem.StandardLeft = StringUtils.parseDouble(wReader.get("StandardLeft"));
				wItem.StandardRight = StringUtils.parseDouble(wReader.get("StandardRight"));
				wItem.Standard = StringUtils.parseString(wReader.get("Standard"));
				wItem.Unit = StringUtils.parseString(wReader.get("Unit"));
				wItem.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wItem.ValueSource = StringUtils.splitList(StringUtils.parseString(wReader.get("ValueSource")), "+|;|+");
				wItem.ItemType = StringUtils.parseInt(wReader.get("ItemType"));
				wItem.Active = StringUtils.parseInt(wReader.get("Active"));
				wItem.VID = StringUtils.parseInt(wReader.get("VID"));
				wItem.Details = StringUtils.parseString(wReader.get("Details"));
				wItem.Process = StringUtils.parseString(wReader.get("Process"));
				wItem.DefaultStationID = StringUtils.parseInt(wReader.get("DefaultStationID"));
				wItem.OtherValue = StringUtils.parseInt(wReader.get("OtherValue"));
				wItem.DefaultManufactor = StringUtils.parseString(wReader.get("DefaultManufactor"));
				wItem.DefaultModal = StringUtils.parseString(wReader.get("DefaultModal"));
				wItem.DefaultNumber = StringUtils.parseString(wReader.get("DefaultNumber"));
				wItem.IsShowStandard = StringUtils.parseInt(wReader.get("IsShowStandard")) == 1 ? true : false;
				wItem.Legend = StringUtils.parseString(wReader.get("Legend"));
				wItem.SerialNumber = StringUtils.parseString(wReader.get("SerialNumber"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.DefaultPartPointID = StringUtils.parseInt(wReader.get("DefaultPartPointID"));
				wItem.DefaultPartPointName = QMSConstants.GetFPCStepName(wItem.DefaultPartPointID);
				wItem.IsWriteFill = StringUtils.parseInt(wReader.get("IsWriteFill"));
				wItem.IsPictureFill = StringUtils.parseInt(wReader.get("IsPictureFill"));
				wItem.IsVideoFill = StringUtils.parseInt(wReader.get("IsVideoFill"));
				List<String> wTempList = new ArrayList<String>();
				Collections.addAll(wTempList, StringUtils.parseString(wReader.get("ManufactorOption")).split(";"));
				wItem.ManufactorOption = StringUtils.isEmpty(StringUtils.parseString(wReader.get("ManufactorOption")))
						? new ArrayList<String>()
						: wTempList;
				wTempList = new ArrayList<String>();
				Collections.addAll(wTempList, StringUtils.parseString(wReader.get("ModalOption")).split(";"));
				wItem.ModalOption = StringUtils.isEmpty(StringUtils.parseString(wReader.get("ModalOption")))
						? new ArrayList<String>()
						: wTempList;
				wItem.IsManufactorFill = StringUtils.parseInt(wReader.get("IsManufactorFill"));
				wItem.IsModalFill = StringUtils.parseInt(wReader.get("IsModalFill"));
				wItem.IsNumberFill = StringUtils.parseInt(wReader.get("IsNumberFill"));
				wItem.IsPeriodChange = StringUtils.parseInt(wReader.get("IsPeriodChange"));
				wItem.ProjectNo = StringUtils.parseString(wReader.get("ProjectNo"));
				wItem.CheckPoint = StringUtils.parseString(wReader.get("CheckPoint"));
				wItem.IsQuality = StringUtils.parseInt(wReader.get("IsQuality"));
				wItem.PartsCoding = StringUtils.parseString(wReader.get("PartsCoding"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.ConfigID = StringUtils.parseInt(wReader.get("ConfigID"));
				wItem.AutoCalc = StringUtils.parseInt(wReader.get("AutoCalc"));
				wItem.WorkContent = StringUtils.parseString(wReader.get("WorkContent"));
				wItem.FileNames = StringUtils.parseString(wReader.get("FileNames"));
				wItem.WorkTime = StringUtils.parseDouble(wReader.get("WorkTime"));

				wItem.OrderType = StringUtils.parseInt(wReader.get("OrderType"));

				wItem.DefaultStation = QMSConstants.GetFPCPartName(wItem.DefaultStationID);

				wItem.DisassemblyComponents = StringUtils.parseString(wReader.get("DisassemblyComponents"));
				wItem.RepairParts = StringUtils.parseString(wReader.get("RepairParts"));
				wItem.AssemblyParts = StringUtils.parseString(wReader.get("AssemblyParts"));
				wItem.Components = StringUtils.parseString(wReader.get("Components"));

				wItem.DisassemblyComponentsID = StringUtils.parseInt(wReader.get("DisassemblyComponentsID"));
				wItem.RepairPartsID = StringUtils.parseInt(wReader.get("RepairPartsID"));
				wItem.AssemblyPartsID = StringUtils.parseInt(wReader.get("AssemblyPartsID"));
				wItem.ComponentsID = StringUtils.parseInt(wReader.get("ComponentsID"));

				wItem.SRDisassemblyComponentsID = StringUtils.parseInt(wReader.get("SRDisassemblyComponentsID"));
				wItem.SRDisassemblyComponents = StringUtils.parseString(wReader.get("SRDisassemblyComponents"));
				wItem.SRRepairPartsID = StringUtils.parseInt(wReader.get("SRRepairPartsID"));
				wItem.SRRepairParts = StringUtils.parseString(wReader.get("SRRepairParts"));
				wItem.SRAssemblyPartsID = StringUtils.parseInt(wReader.get("SRAssemblyPartsID"));
				wItem.SRAssemblyParts = StringUtils.parseString(wReader.get("SRAssemblyParts"));

				List<String> wSOPIDList = StringUtils.splitList(StringUtils.parseString(wReader.get("IPTSOPList")),
						";");
				List<IPTSOP> wIPTSOPList = new ArrayList<IPTSOP>();
				if (wSOPIDList != null && wSOPIDList.size() > 0) {
					for (String wItemStr : wSOPIDList) {
						int wSOPID = Integer.parseInt(wItemStr);
						IPTSOP wIPTSOP = IPTSOPDAO.getInstance().SelectByID(wLoginUser, wSOPID, wErrorCode);
						if (wIPTSOP != null && wIPTSOP.ID > 0) {
							wIPTSOPList.add(wIPTSOP);
						}
					}
				}
				wItem.IPTSOPList = wIPTSOPList;

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private IPTItemDAO() {
		super();
	}

	public static IPTItemDAO getInstance() {
		if (Instance == null)
			Instance = new IPTItemDAO();
		return Instance;
	}
}
