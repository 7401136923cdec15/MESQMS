package com.mes.qms.server.serviceimpl.dao.sfc;

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
import com.mes.qms.server.service.mesenum.SFCLoginType;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.sfc.SFCLoginEvent;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class SFCLoginEventDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCLoginEventDAO.class);

	private static SFCLoginEventDAO Instance = null;

	private SFCLoginEventDAO() {
		super();
	}

	public static SFCLoginEventDAO getInstance() {
		if (Instance == null)
			Instance = new SFCLoginEventDAO();
		return Instance;
	}

	/**
	 * 添加或修改
	 * 
	 * @param wSFCLoginEvent
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCLoginEvent wSFCLoginEvent, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Default,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCLoginEvent == null)
				return 0;

			String wSQL = "";
			if (wSFCLoginEvent.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.sfc_loginstation(WorkShopID,StationID," + "OperatorID,ModuleID,ShiftID,"
								+ "Active,LoginTime,Type,SFCTaskStepID,Remark) VALUES(:WorkShopID,"
								+ ":StationID,:OperatorID,:ModuleID,:ShiftID,"
								+ ":Active,:LoginTime,:Type,:SFCTaskStepID,:Remark);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.sfc_loginstation SET " + "WorkShopID = :WorkShopID,StationID = :StationID,"
								+ "OperatorID = :OperatorID,ModuleID = :ModuleID,"
								+ "ShiftID = :ShiftID,Active = :Active," + "LoginTime = :LoginTime,"
								+ "Type = :Type,SFCTaskStepID=:SFCTaskStepID,Remark=:Remark WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCLoginEvent.ID);
			wParamMap.put("WorkShopID", wSFCLoginEvent.WorkShopID);
			wParamMap.put("StationID", wSFCLoginEvent.StationID);
			wParamMap.put("OperatorID", wSFCLoginEvent.OperatorID);
			wParamMap.put("ModuleID", wSFCLoginEvent.ModuleID);
			wParamMap.put("ShiftID", wSFCLoginEvent.ShiftID);
			wParamMap.put("Active", wSFCLoginEvent.Active);
			wParamMap.put("LoginTime", wSFCLoginEvent.LoginTime);
			wParamMap.put("Type", wSFCLoginEvent.Type);
			wParamMap.put("SFCTaskStepID", wSFCLoginEvent.SFCTaskStepID);
			wParamMap.put("Remark", wSFCLoginEvent.Remark);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCLoginEvent.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCLoginEvent.setID(wResult);
			} else {
				wResult = wSFCLoginEvent.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCLoginEvent> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Default,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (SFCLoginEvent wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_loginstation WHERE ID IN({0}) ;",
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
	public SFCLoginEvent SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCLoginEvent wResult = new SFCLoginEvent();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Default,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCLoginEvent> wList = SelectList(wLoginUser, wID, -1, -1, -1, -1, -1, -1, -1, -1, wErrorCode);
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
	public List<SFCLoginEvent> SelectList(BMSEmployee wLoginUser, int wID, int wWorkShopID, int wStationID,
			int wOperatorID, int wModuleID, int wShiftID, int wActive, int wType, int wSFCTaskStepID,
			OutResult<Integer> wErrorCode) {
		List<SFCLoginEvent> wResultList = new ArrayList<SFCLoginEvent>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Default,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.sfc_loginstation WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wWorkShopID <= 0 or :wWorkShopID = WorkShopID ) "
					+ "and ( :wStationID <= 0 or :wStationID = StationID ) "
					+ "and ( :wOperatorID <= 0 or :wOperatorID = OperatorID ) "
					+ "and ( :wModuleID <= 0 or :wModuleID = ModuleID ) "
					+ "and ( :wShiftID <= 0 or :wShiftID = ShiftID ) " + "and ( :wActive <= 0 or :wActive = Active ) "
					+ "and ( :wSFCTaskStepID <= 0 or :wSFCTaskStepID = SFCTaskStepID ) "
					+ "and ( :wType <= 0 or :wType = Type );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wWorkShopID", wWorkShopID);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wOperatorID", wOperatorID);
			wParamMap.put("wModuleID", wModuleID);
			wParamMap.put("wShiftID", wShiftID);
			wParamMap.put("wActive", wActive);
			wParamMap.put("wType", wType);
			wParamMap.put("wSFCTaskStepID", wSFCTaskStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			// 人员映射

			Map<Integer, BMSEmployee> wEmployeeMap = QMSConstants.GetBMSEmployeeList();

			for (Map<String, Object> wReader : wQueryResult) {
				SFCLoginEvent wItem = new SFCLoginEvent();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.WorkShopID = StringUtils.parseInt(wReader.get("WorkShopID"));
				wItem.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID"));
				wItem.ModuleID = StringUtils.parseInt(wReader.get("ModuleID"));
				wItem.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wItem.Active = StringUtils.parseInt(wReader.get("Active"));
				wItem.LoginTime = StringUtils.parseCalendar(wReader.get("LoginTime"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));
				wItem.SFCTaskStepID = StringUtils.parseInt(wReader.get("SFCTaskStepID"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));

				if (wEmployeeMap.containsKey(wItem.OperatorID))
					wItem.OperatorName = wEmployeeMap.get(wItem.OperatorID).Name;

				switch (SFCLoginType.getEnumType(wItem.Type)) {
				case StartWork:
					wItem.LoginText = SFCLoginType.StartWork.getLable();
					break;
				case AfterWork:
					wItem.LoginText = SFCLoginType.AfterWork.getLable();
					break;
				default:
					break;
				}

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 批量激活或禁用
	 */
	public ServiceResult<Integer> Active(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Default,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIDList == null || wIDList.size() <= 0)
				return wResult;
			for (Integer wItem : wIDList) {
				SFCLoginEvent wSFCLoginEvent = SelectByID(wLoginUser, wItem, wErrorCode);
				if (wSFCLoginEvent == null || wSFCLoginEvent.ID <= 0)
					continue;
				// 只有激活的才能禁用
				if (wActive == 2 && wSFCLoginEvent.Active != 1) {
					wErrorCode.set(MESException.Logic.getValue());
					return wResult;
				}
				wSFCLoginEvent.Active = wActive;
				long wID = Update(wLoginUser, wSFCLoginEvent, wErrorCode);
				if (wID <= 0)
					break;
			}
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<SFCTaskStep> SFC_SelectTaskStepList(BMSEmployee wLoginUser, Calendar wTodaySTime, Calendar wTodayETime,
			int wOrderID, int wPartID, OutResult<Integer> wErrorCode) {
		List<SFCTaskStep> wResultList = new ArrayList<SFCTaskStep>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Default,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT "
					+ "(select count(*) from {1}.aps_taskpart where Status!=5 and OrderID=t2.OrderID "
					+ "and PartID in(select ID from  {0}.fpc_part where ID in(select PrevPartID from "
					+ "{0}.fpc_routepart where RouteID in( select RouteID from {1}.oms_order "
					+ "where ID=t2.OrderID) and PartID=t2.PartID) or ID in(select PartID from {0}.fpc_routepart "
					+ "where RouteID in(select RouteID from {1}.oms_order where ID=t2.OrderID) and NextPartIDMap "
					+ "like CONCAT(CONCAT(''%\"'',t2.PartID),''\"%''))) and ShiftPeriod=5 and Active=1) NotTurnOrderSize,"
					+ "t2.PartNo,t2.PartID,t2.OrderID,t3.Name PartName,t2.OperatorList,t4.Name StepName,t1.IsStartWork,"
					+ "t1.Type,t5.Name LineName,t1.ID,t1.TaskStepID,t2.StepID,t2.LineID,t6.ProductID,t2.TaskPartID,"
					+ "t1.ShiftID,t1.WorkHour,t1.OperatorID,t1.CreateTime,t1.ReadyTime,t1.MonitorID,t1.EditTime,t1.RealHour "
					+ "FROM {1}.sfc_taskstep t1,{1}.aps_taskstep t2,"
					+ "{0}.fpc_part t3,{0}.fpc_partpoint t4,{0}.fmc_line t5,{1}.oms_order t6 "
					+ "where t2.OrderID=t6.ID and t2.LineID=t5.ID " + "and t2.StepID=t4.ID and t2.PartID=t3.ID "
					+ "and ( :OrderID <= 0 or :OrderID = t2.OrderID ) " + "and ( :PartID <= 0 or :PartID = t2.PartID ) "
					+ "and t1.TaskStepID=t2.ID and (t2.Status!=5 or "
					+ "(t2.Status=5 and t1.EditTime>:StartTime and t1.EditTime < :EndTime) ) "
					+ "and OperatorID=:UserID;", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("StartTime", wTodaySTime);
			wParamMap.put("EndTime", wTodayETime);
			wParamMap.put("UserID", wLoginUser.ID);
			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("PartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCTaskStep wItem = new SFCTaskStep();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID"));
				wItem.TaskPartID = StringUtils.parseInt(wReader.get("TaskPartID"));
				wItem.StepID = StringUtils.parseInt(wReader.get("StepID"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wItem.WorkHour = StringUtils.parseInt(wReader.get("WorkHour"));
				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.ReadyTime = StringUtils.parseCalendar(wReader.get("ReadyTime"));
				wItem.MonitorID = StringUtils.parseInt(wReader.get("MonitorID"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.RealHour = StringUtils.parseDouble(wReader.get("RealHour"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.PartName = StringUtils.parseString(wReader.get("PartName"));
				wItem.Operators = GetNamesByIDList(wLoginUser,
						StringUtils.parseIntList(StringUtils.parseString(wReader.get("OperatorList")).split(";")));
				wItem.PartPointName = StringUtils.parseString(wReader.get("StepName"));
				wItem.IsStartWork = StringUtils.parseInt(wReader.get("IsStartWork"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));
				wItem.LineName = StringUtils.parseString(wReader.get("LineName"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 根据人员ID集合获取人员名称
	 * 
	 * @param wLoginUser
	 * @param wIDList
	 * @return
	 */
	private String GetNamesByIDList(BMSEmployee wLoginUser, List<Integer> wIDList) {
		String wResult = "";
		try {
			if (wIDList == null || wIDList.size() <= 0) {
				return wResult;
			}

			List<String> wNames = new ArrayList<String>();
			wIDList.stream().forEach(p -> {
				if (p > 0) {
					wNames.add(QMSConstants.GetBMSEmployeeName(p));
				}
			});
			wNames.removeIf(p -> StringUtils.isEmpty(p));
			wResult = StringUtils.Join(",", wNames);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 关掉此派工任务的消息
	 */
	public void SFC_CloseTaskStepMessage(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

//			String wSQL = StringUtils.Format(
//					"update {0}.bfc_message set Active=3 "
//							+ "where ModuleID=8103 and MessageID=:wMessageID and ResponsorID=:wLoginID;",
//					wInstance.Result);

			String wSQL = StringUtils.Format(
					"update {0}.bfc_message set Active=3 " + "where ModuleID=8103 and MessageID=:wMessageID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wMessageID", wSFCTaskStep.ID);
//			wParamMap.put("wLoginID", wLoginUser.ID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}
