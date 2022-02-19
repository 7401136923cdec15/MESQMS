package com.mes.qms.server.serviceimpl.dao.ipt;

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTProblemBomItem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.LOCOAPSServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTValueDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
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

public class IPTValueDAO extends BaseDAO {
	private static Logger logger = LoggerFactory.getLogger(IPTValueDAO.class);

	private static IPTValueDAO Instance = null;

	public long Update(BMSEmployee wLoginUser, IPTValue wIPTValue, OutResult<Integer> wErrorCode) {
		long wResult = 0L;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wIPTValue == null) {
				return 0L;
			}
			String wSQL = "";
			if (wIPTValue.getID() <= 0L) {
				wSQL = StringUtils.Format("INSERT INTO {0}.ipt_value(StandardID,IPTItemID,Value,Remark,Result,TaskID,"
						+ "IPTMode,ItemType,ImagePath,VideoPath,SolveID,SubmitTime,SubmitID,"
						+ "Manufactor,Modal,Number,Status,IPTProblemBomItemList,"
						+ "OrderID,DisassemblyComponents,RepairParts,AssemblyParts,"
						+ "SRDisassemblyComponents,SRRepairParts,SRAssemblyParts,SRScrapParts,SRLYParts,MaterialID,MaterialNo,MaterialName) "
						+ "VALUES(:StandardID,:IPTItemID,:Value,:Remark,:Result,:TaskID,:IPTMode,"
						+ ":ItemType,:ImagePath,:VideoPath,:SolveID,:SubmitTime,:SubmitID,:Manufactor,"
						+ ":Modal,:Number,:Status,:IPTProblemBomItemList,:OrderID,:DisassemblyComponents,"
						+ ":RepairParts,:AssemblyParts,:SRDisassemblyComponents,:SRRepairParts,"
						+ ":SRAssemblyParts,:SRScrapParts,:SRLYParts,:MaterialID,:MaterialNo,:MaterialName);",
						new Object[] { wInstance.Result });
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.ipt_value SET StandardID = :StandardID,IPTItemID = :IPTItemID,"
						+ "Value = :Value,Remark = :Remark,Result = :Result,TaskID = :TaskID,"
						+ "IPTMode = :IPTMode,ItemType = :ItemType,ImagePath = :ImagePath,"
						+ "VideoPath = :VideoPath,SolveID = :SolveID,SubmitTime = :SubmitTime,"
						+ "SubmitID = :SubmitID,Manufactor = :Manufactor,Modal = :Modal,Number = :Number,"
						+ "Status = :Status,IPTProblemBomItemList=:IPTProblemBomItemList,"
						+ "OrderID=:OrderID,DisassemblyComponents=:DisassemblyComponents,"
						+ "RepairParts=:RepairParts,AssemblyParts=:AssemblyParts,SRDisassemblyComponents=:SRDisassemblyComponents,"
						+ "SRRepairParts=:SRRepairParts,SRAssemblyParts=:SRAssemblyParts,SRScrapParts=:SRScrapParts,"
						+ "SRLYParts=:SRLYParts,"
						+ "MaterialID=:MaterialID,MaterialNo=:MaterialNo,MaterialName=:MaterialName WHERE ID = :ID;",
						new Object[] { wInstance.Result });
			}
			wSQL = DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("ID", Long.valueOf(wIPTValue.ID));
			wParamMap.put("StandardID", Long.valueOf(wIPTValue.StandardID));
			wParamMap.put("IPTItemID", Long.valueOf(wIPTValue.IPTItemID));
			wParamMap.put("Value", wIPTValue.Value);
			wParamMap.put("Remark", wIPTValue.Remark);
			wParamMap.put("Result", Integer.valueOf(wIPTValue.Result));
			wParamMap.put("TaskID", Integer.valueOf(wIPTValue.TaskID));
			wParamMap.put("IPTMode", Integer.valueOf(wIPTValue.IPTMode));
			wParamMap.put("ItemType", Integer.valueOf(wIPTValue.ItemType));
			wParamMap.put("ImagePath", StringUtils.Join(";", wIPTValue.ImagePath));
			wParamMap.put("VideoPath", StringUtils.Join(";", wIPTValue.VideoPath));
			wParamMap.put("SolveID", Integer.valueOf(wIPTValue.SolveID));
			wParamMap.put("SubmitTime", wIPTValue.SubmitTime);
			wParamMap.put("SubmitID", Integer.valueOf(wIPTValue.SubmitID));
			wParamMap.put("Manufactor", wIPTValue.Manufactor);
			wParamMap.put("Modal", wIPTValue.Modal);
			wParamMap.put("Number", wIPTValue.Number);
			wParamMap.put("Status", Integer.valueOf(wIPTValue.Status));
			wParamMap.put("IPTProblemBomItemList", IPTProblemBomItem.ListToString(wIPTValue.IPTProblemBomItemList));
			wParamMap.put("OrderID", Integer.valueOf(wIPTValue.OrderID));

			wParamMap.put("DisassemblyComponents", wIPTValue.DisassemblyComponents);
			wParamMap.put("RepairParts", wIPTValue.RepairParts);
			wParamMap.put("AssemblyParts", wIPTValue.AssemblyParts);

			wParamMap.put("SRDisassemblyComponents", wIPTValue.SRDisassemblyComponents);
			wParamMap.put("SRRepairParts", wIPTValue.SRRepairParts);
			wParamMap.put("SRAssemblyParts", wIPTValue.SRAssemblyParts);
			wParamMap.put("SRScrapParts", wIPTValue.SRScrapParts);
			wParamMap.put("SRLYParts", wIPTValue.SRLYParts);

			wParamMap.put("MaterialID", wIPTValue.MaterialID);
			wParamMap.put("MaterialNo", wIPTValue.MaterialNo);
			wParamMap.put("MaterialName", wIPTValue.MaterialName);

			GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
			MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(wParamMap);

			this.nameJdbcTemplate.update(wSQL, (SqlParameterSource) mapSqlParameterSource,
					(KeyHolder) generatedKeyHolder);

			if (wIPTValue.getID() <= 0L) {
				wResult = generatedKeyHolder.getKey().intValue();
				wIPTValue.setID(wResult);
			} else {
				wResult = wIPTValue.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTValue> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<>(0);
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}
			List<String> wIDList = new ArrayList<>();
			for (IPTValue wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.ipt_value WHERE ID IN({0}) ;",
					new Object[] { String.join(",", wIDList), wInstance.Result });
			ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public IPTValue SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTValue wResult = new IPTValue();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			List<IPTValue> wList = SelectList(wLoginUser, wID, -1L, -1, -1, -1, -1, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<IPTValue> SelectList(BMSEmployee wLoginUser, long wID, long wIPTItemID, int wTaskID, int wIPTMode,
			int wItemType, int wStatus, OutResult<Integer> wErrorCode) {
		List<IPTValue> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.ipt_value WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wIPTItemID <= 0 or :wIPTItemID = IPTItemID ) "
					+ "and ( :wTaskID <= 0 or :wTaskID = TaskID ) " + "and ( :wIPTMode <= 0 or :wIPTMode = IPTMode ) "
					+ "and ( :wItemType <= 0 or :wItemType = ItemType ) "
					+ "and ( :wStatus <= 0 or :wStatus = Status );",
					new Object[] {

							wInstance.Result });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wID", Long.valueOf(wID));
			wParamMap.put("wIPTItemID", Long.valueOf(wIPTItemID));
			wParamMap.put("wTaskID", Integer.valueOf(wTaskID));
			wParamMap.put("wIPTMode", Integer.valueOf(wIPTMode));
			wParamMap.put("wItemType", Integer.valueOf(wItemType));
			wParamMap.put("wStatus", Integer.valueOf(wStatus));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 根据专检单ID查询已做的互检值
	 */
	public List<IPTValue> SelectList(BMSEmployee wLoginUser, int wSpecialTaskID, OutResult<Integer> wErrorCode) {
		List<IPTValue> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ipt_value where TaskID " + "in(select ID from {1}.sfc_taskipt where TaskStepID "
							+ "in(select TaskStepID from {1}.sfc_taskipt where ID=:SpecialTaskID)  "
							+ "and TaskType=12) and IPTMode=12 and Status=2;",
					wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("SpecialTaskID", wSpecialTaskID);

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<IPTValue> SelectByIDList(BMSEmployee wLoginUser, List<Integer> wIDList, OutResult<Integer> wErrorCode) {
		List<IPTValue> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			if (wIDList == null) {
				wIDList = new ArrayList<>();
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ipt_value WHERE  1=1  and ( :wIDs is null or :wIDs = '''' or ID in ({1}));",
					new Object[] { wInstance.Result, (wIDList.size() > 0) ? StringUtils.Join(",", wIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wIDs", StringUtils.Join(",", wIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<IPTValue> SelectByTaskIDList(BMSEmployee wLoginUser, List<Integer> wTaskIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTValue> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			if (wTaskIDList == null) {
				wTaskIDList = new ArrayList<>();
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ipt_value WHERE  1=1  and ( :wIDs is null or :wIDs = '''' or TaskID in ({1}));",
					new Object[] { wInstance.Result,
							(wTaskIDList.size() > 0) ? StringUtils.Join(",", wTaskIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wIDs", StringUtils.Join(",", wTaskIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private void SetValue(List<IPTValue> wResultList, List<Map<String, Object>> wQueryResult) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				IPTValue wItem = new IPTValue();

				wItem.ID = StringUtils.parseLong(wReader.get("ID")).longValue();
				wItem.StandardID = StringUtils.parseInt(wReader.get("StandardID")).intValue();
				wItem.IPTItemID = StringUtils.parseLong(wReader.get("IPTItemID")).longValue();
				wItem.Value = StringUtils.parseString(wReader.get("Value"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.Result = StringUtils.parseInt(wReader.get("Result")).intValue();
				wItem.TaskID = StringUtils.parseInt(wReader.get("TaskID")).intValue();
				wItem.IPTMode = StringUtils.parseInt(wReader.get("IPTMode")).intValue();
				wItem.ItemType = StringUtils.parseInt(wReader.get("ItemType")).intValue();
				wItem.ImagePath = StringUtils.parseList(StringUtils.parseString(wReader.get("ImagePath")).split(";"));
				wItem.VideoPath = StringUtils.parseList(StringUtils.parseString(wReader.get("VideoPath")).split(";"));
				wItem.SolveID = StringUtils.parseInt(wReader.get("SolveID")).intValue();
				wItem.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wItem.SubmitID = StringUtils.parseInt(wReader.get("SubmitID")).intValue();
				wItem.Manufactor = StringUtils.parseString(wReader.get("Manufactor"));
				wItem.Modal = StringUtils.parseString(wReader.get("Modal"));
				wItem.Number = StringUtils.parseString(wReader.get("Number"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status")).intValue();
				wItem.IPTProblemBomItemList = IPTProblemBomItem
						.StringToList(StringUtils.parseString(wReader.get("IPTProblemBomItemList")));

				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID")).intValue();

				wItem.DisassemblyComponents = StringUtils.parseString(wReader.get("DisassemblyComponents"));
				wItem.RepairParts = StringUtils.parseString(wReader.get("RepairParts"));
				wItem.AssemblyParts = StringUtils.parseString(wReader.get("AssemblyParts"));

				wItem.SRDisassemblyComponents = StringUtils.parseString(wReader.get("SRDisassemblyComponents"));
				wItem.SRRepairParts = StringUtils.parseString(wReader.get("SRRepairParts"));
				wItem.SRAssemblyParts = StringUtils.parseString(wReader.get("SRAssemblyParts"));
				wItem.SRScrapParts = StringUtils.parseString(wReader.get("SRScrapParts"));
				wItem.SRLYParts = StringUtils.parseString(wReader.get("SRLYParts"));

				wItem.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));

				wItem.Submitor = QMSConstants.GetBMSEmployeeName(wItem.SubmitID);

				if (wItem.OrderID > 0) {
					wItem.OMSOrder = LOCOAPSServiceImpl.getInstance()
							.OMS_QueryOrderByID(BaseDAO.SysAdmin, wItem.OrderID).Info(OMSOrder.class);
				}

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static IPTValueDAO getInstance() {
		if (Instance == null)
			Instance = new IPTValueDAO();
		return Instance;
	}

	/**
	 * 根据订单获取检验值集合
	 */
	public List<IPTValue> SelectListByOrder(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wStepID,
			long wIPTItemID, OutResult<Integer> wErrorCode) {
		List<IPTValue> wResult = new ArrayList<IPTValue>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select * from {0}.ipt_value where TaskID in (SELECT ID FROM {1}.sfc_taskipt "
							+ "where OrderID=:OrderID and StationID=:StationID "
							+ "and PartPointID=:PartPointID) and IPTItemID=:IPTItemID;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("StationID", wPartID);
			wParamMap.put("PartPointID", wStepID);
			wParamMap.put("IPTItemID", wIPTItemID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResult, wQueryResult);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\serviceimpl\dao\ipt\
 * IPTValueDAO.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */