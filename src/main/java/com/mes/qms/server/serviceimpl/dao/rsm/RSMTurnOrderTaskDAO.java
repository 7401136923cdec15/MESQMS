package com.mes.qms.server.serviceimpl.dao.rsm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.RSMTurnOrderTaskStatus;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.focas.FocasPart;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.ncr.NCRTask;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.LOCOAPSServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCLetPassBPMDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class RSMTurnOrderTaskDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(RSMTurnOrderTaskDAO.class);

	private static RSMTurnOrderTaskDAO Instance = null;

	private RSMTurnOrderTaskDAO() {
		super();
	}

	public static RSMTurnOrderTaskDAO getInstance() {
		if (Instance == null)
			Instance = new RSMTurnOrderTaskDAO();
		return Instance;
	}

	/**
	 * 添加或修改
	 */
	public synchronized int Update(BMSEmployee wLoginUser, RSMTurnOrderTask wSFCTurnOrderTask,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCTurnOrderTask == null)
				return 0;

			String wSQL = "";
			if (wSFCTurnOrderTask.getID() <= 0) {
				wSQL = StringUtils.Format("INSERT INTO {0}.sfc_turnordertask(ApplyID,ApplyTime,"
						+ "OrderID,ApplyStationID,TargetStationID,"
						+ "Status,Type,TaskPartID,Remark,FinishTime,ConfirmID,ConfirmTime) VALUES(:ApplyID,:ApplyTime,:OrderID,"
						+ ":ApplyStationID,:TargetStationID,:Status,:Type,:TaskPartID,:Remark,:FinishTime,:ConfirmID,:ConfirmTime);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.sfc_turnordertask SET ApplyID = :ApplyID,"
						+ "ApplyTime = :ApplyTime,OrderID = :OrderID," + "ApplyStationID = :ApplyStationID,"
						+ "TargetStationID = :TargetStationID,"
						+ "Status = :Status,Type=:Type,TaskPartID=:TaskPartID,Remark=:Remark,FinishTime=:FinishTime,"
						+ "ConfirmID=:ConfirmID,ConfirmTime=:ConfirmTime WHERE ID = :ID;", wInstance.Result);

				// 已提交重置问题
				RSMTurnOrderTask wTask = SelectByID(wLoginUser, wSFCTurnOrderTask.ID, wErrorCode);
				if (wTask.ConfirmID > 0) {
					wSFCTurnOrderTask.ConfirmID = wTask.ConfirmID;
					wSFCTurnOrderTask.ConfirmTime = wTask.ConfirmTime;
				}
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCTurnOrderTask.ID);
			wParamMap.put("ApplyID", wSFCTurnOrderTask.ApplyID);
			wParamMap.put("ApplyTime", wSFCTurnOrderTask.ApplyTime);
			wParamMap.put("OrderID", wSFCTurnOrderTask.OrderID);
			wParamMap.put("ApplyStationID", wSFCTurnOrderTask.ApplyStationID);
			wParamMap.put("TargetStationID", wSFCTurnOrderTask.TargetStationID);
			wParamMap.put("Status", wSFCTurnOrderTask.Status);
			wParamMap.put("Type", wSFCTurnOrderTask.Type);
			wParamMap.put("TaskPartID", wSFCTurnOrderTask.TaskPartID);
			wParamMap.put("Remark", wSFCTurnOrderTask.Remark);
			wParamMap.put("FinishTime", wSFCTurnOrderTask.FinishTime);
			wParamMap.put("ConfirmID", wSFCTurnOrderTask.ConfirmID);
			wParamMap.put("ConfirmTime", wSFCTurnOrderTask.ConfirmTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCTurnOrderTask.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCTurnOrderTask.setID(wResult);
			} else {
				wResult = wSFCTurnOrderTask.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<RSMTurnOrderTask> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (RSMTurnOrderTask wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.sfc_turnordertask WHERE ID IN({0}) ;",
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
	public RSMTurnOrderTask SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		RSMTurnOrderTask wResult = new RSMTurnOrderTask();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<RSMTurnOrderTask> wList = SelectList(wLoginUser, wID, -1, -1, -1, null, null, null, wErrorCode);
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
	public List<RSMTurnOrderTask> SelectList(BMSEmployee wLoginUser, int wID, int wOrderID, int wApplyStationID,
			int wTargetStationID, List<Integer> wStateIDList, Calendar wStartTime, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		List<RSMTurnOrderTask> wResultList = new ArrayList<RSMTurnOrderTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wStateIDList == null) {
				wStateIDList = new ArrayList<Integer>();
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);

			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.sfc_turnordertask WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
					+ "and ( :wApplyStationID <= 0 or :wApplyStationID = ApplyStationID ) "
					+ "and ( :wTargetStationID <= 0 or :wTargetStationID = TargetStationID ) "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  ApplyTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  ApplyTime ) "
					+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1}));", wInstance.Result,
					wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wApplyStationID", wApplyStationID);
			wParamMap.put("wTargetStationID", wTargetStationID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<RSMTurnOrderTask> SelectListByPartIDList(BMSEmployee wLoginUser, int wOrderID,
			List<Integer> wPrevPartIDList, OutResult<Integer> wErrorCode) {
		List<RSMTurnOrderTask> wResultList = new ArrayList<RSMTurnOrderTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wPrevPartIDList == null) {
				wPrevPartIDList = new ArrayList<Integer>();
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.sfc_turnordertask WHERE  1=1  "
							+ "and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
							+ "and ( :wPrevPartID is null or :wPrevPartID = '''' or ApplyStationID in ({1}));",
					wInstance.Result, wPrevPartIDList.size() > 0 ? StringUtils.Join(",", wPrevPartIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPrevPartID", StringUtils.Join(",", wPrevPartIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<RSMTurnOrderTask> SelectList(BMSEmployee wLoginUser, int wTaskPartID, OutResult<Integer> wErrorCode) {
		List<RSMTurnOrderTask> wResultList = new ArrayList<RSMTurnOrderTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.sfc_turnordertask  WHERE  1=1 "
					+ "and ( :wTaskPartID <= 0 or :wTaskPartID = TaskPartID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wTaskPartID", wTaskPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 根据订单ID获取当前最新的转序单
	 * 
	 * @return
	 */
	public List<RSMTurnOrderTask> SelectNewList(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		List<RSMTurnOrderTask> wResultList = new ArrayList<RSMTurnOrderTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"select *,max(ApplyTime) as MaxTime from " + "{0}.sfc_turnordertask where OrderID=:OrderID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private void SetValue(BMSEmployee wLoginUser, List<RSMTurnOrderTask> wResultList,
			List<Map<String, Object>> wQueryResult) {
		try {

			for (Map<String, Object> wReader : wQueryResult) {
				RSMTurnOrderTask wItem = new RSMTurnOrderTask();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.ApplyID = StringUtils.parseInt(wReader.get("ApplyID"));
				wItem.ApplyTime = StringUtils.parseCalendar(wReader.get("ApplyTime"));
				wItem.FinishTime = StringUtils.parseCalendar(wReader.get("FinishTime"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.ApplyStationID = StringUtils.parseInt(wReader.get("ApplyStationID"));
				wItem.TargetStationID = StringUtils.parseInt(wReader.get("TargetStationID"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));
				wItem.TaskPartID = StringUtils.parseInt(wReader.get("TaskPartID"));
				wItem.ConfirmID = StringUtils.parseInt(wReader.get("ConfirmID"));
				wItem.ConfirmTime = StringUtils.parseCalendar(wReader.get("ConfirmTime"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				// 辅助信息
				wItem.ApplyName = QMSConstants.GetBMSEmployeeName(wItem.ApplyID);
				if (wItem.Type == 2) {
					wItem.ApplyName = "MES系统";
				}
				wItem.ApplyStationName = QMSConstants.GetFPCPartName(wItem.ApplyStationID);
				wItem.TargetStationName = QMSConstants.GetFPCPartName(wItem.TargetStationID);
				wItem.StatusText = RSMTurnOrderTaskStatus.getEnumType(wItem.Status).getLable();
				wItem.ConfirmName = QMSConstants.GetBMSEmployeeName(wItem.ConfirmID);

				wResultList.add(wItem);
			}

			if (wResultList.size() <= 0)
				return;
			List<Integer> wOrderIDList = wResultList.stream().map(p -> p.OrderID).distinct()
					.collect(Collectors.toList());
			wOrderIDList.removeIf(p -> p <= 0);
			if (wOrderIDList.size() <= 0)
				return;
			Map<Integer, OMSOrder> wOMSOrderMap = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser, wOrderIDList).List(OMSOrder.class).stream()
					.collect(Collectors.toMap(p -> p.ID, p -> p));
			if (wOMSOrderMap.size() <= 0)
				return;

			wResultList.forEach(p -> {
				if (wOMSOrderMap.containsKey(p.OrderID)) {
					p.OMSOrder = wOMSOrderMap.get(p.OrderID);
					p.OrderNo = wOMSOrderMap.get(p.OrderID).OrderNo;
				}
			});

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public List<NCRTask> SelectNcrList(BMSEmployee wLoginUser, int wOrderID, int wStationID,
			OutResult<Integer> wErrorCode) {
		List<NCRTask> wResultList = new ArrayList<NCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ncr_task WHERE OrderID={1} AND StationID={2} AND Status not in (0,21,22);",
					wInstance.Result, String.valueOf(wOrderID), String.valueOf(wStationID));

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				NCRTask wItem = new NCRTask();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Result = StringUtils.parseInt(wReader.get("Result"));
				wItem.CloseStationID = StringUtils.parseInt(wReader.get("CloseStationID"));
				wItem.CloseTime = StringUtils.parseCalendar(wReader.get("CloseTime"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 检验员查询转序单数据
	 * 
	 * @return
	 */
	public List<RSMTurnOrderTask> SelectListByChecker(BMSEmployee wLoginUser, List<Integer> wPartIDList,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<RSMTurnOrderTask> wResultList = new ArrayList<RSMTurnOrderTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wPartIDList == null) {
				wPartIDList = new ArrayList<Integer>();
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);

			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			String wSQL = StringUtils.Format(
					"select * from {0}.sfc_turnordertask " + "where ((Status=1 and ConfirmID<=0) or ( ConfirmID>0 "
							+ "and :wStartTime<ConfirmTime and ConfirmTime<:wEndTime)) "
							+ "and ApplyStationID in({1});",
					wInstance.Result, wPartIDList.size() > 0 ? StringUtils.Join(",", wPartIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 根据工序任务ID和工位ID获取前工位ID集合
	 */
	public List<Integer> SelectPreStationIDList(BMSEmployee wLoginUser, int wAPSTaskStepID, int wPartID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResultList = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT ID FROM {0}.fpc_part where id in(SELECT PrevPartID "
							+ "FROM {0}.fpc_routepart where RouteID in(select RouteID  "
							+ "FROM {2}.oms_order where id in(SELECT OrderID "
							+ "FROM {2}.aps_taskstep where ID=:APSTaskStepID)) and "
							+ "PartID in(SELECT PartID FROM {2}.aps_taskstep where ID=:APSTaskStepID)) or "
							+ "id in(select PartID from {0}.fpc_routepart where NextPartIDMap like ''%\"{1}\"%'' "
							+ "and RouteID in(select RouteID  FROM {2}.oms_order where id in(SELECT OrderID "
							+ "FROM {2}.aps_taskstep where ID=:APSTaskStepID)));",
					wInstance.Result, wPartID, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("APSTaskStepID", wAPSTaskStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("ID"));
				if (wID > 0 && !wResultList.stream().anyMatch(p -> p == wID)) {
					wResultList.add(wID);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 根据工序任务ID和工位ID判断此工位是否已转序
	 */
	public boolean JudgeIsTurnOrder(BMSEmployee wLoginUser, int wAPSTaskStepID, int wPartID,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT count(*) as Number FROM {0}.sfc_turnordertask where OrderID "
					+ "in(SELECT OrderID FROM {1}.aps_taskstep where ID=:APSTaskStepID) "
					+ "and ApplyStationID=:PartID and Status=2;", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("APSTaskStepID", wAPSTaskStepID);
			wParamMap.put("PartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("Number"));
				if (wID > 0) {
					return true;
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工序任务ID和工序ID获取前工序ID集合
	 */
	public List<Integer> SelectPreStepIDList(BMSEmployee wLoginUser, int wAPSTaskStepID, int wStepID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResultList = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT ID FROM {0}.fpc_partpoint where ID "
					+ "in(SELECT PrevStepID FROM {0}.fpc_routepartpoint where RouteID "
					+ "in(select RouteID  FROM {2}.oms_order where id "
					+ "in(SELECT OrderID FROM {2}.aps_taskstep where ID=:APSTaskStepID)) and "
					+ "PartID in(SELECT PartID FROM {2}.aps_taskstep where ID=:APSTaskStepID) "
					+ "and PartPointID in(SELECT StepID FROM {2}.aps_taskstep where ID=:APSTaskStepID)) "
					+ "or ID in(SELECT PartPointID FROM {0}.fpc_routepartpoint where "
					+ "RouteID in(select RouteID  FROM {2}.oms_order where id "
					+ "in(SELECT OrderID FROM {2}.aps_taskstep where ID=:APSTaskStepID)) "
					+ "and PartID in(SELECT PartID FROM {2}.aps_taskstep where ID=:APSTaskStepID) "
					+ "and NextStepIDMap like ''%\"{1}\"%'');", wInstance.Result, wStepID, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("APSTaskStepID", wAPSTaskStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("ID"));
				if (wID > 0 && !wResultList.stream().anyMatch(p -> p == wID)) {
					wResultList.add(wID);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 根据工序任务ID和工序ID判断此工序任务是否已完工
	 */
	public boolean JudgeIsFinish(BMSEmployee wLoginUser, int wAPSTaskStepID, int wStepID, SFCTaskStep wTaskStep,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT count(*) as Number FROM {0}.aps_taskstep "
					+ "where OrderID in(SELECT OrderID FROM {0}.aps_taskstep where ID=:APSTaskStepID) "
					+ "and PartID in(SELECT PartID FROM {0}.aps_taskstep where ID=:APSTaskStepID) "
					+ "and StepID=:StepID and Status=5;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("APSTaskStepID", wAPSTaskStepID);
			wParamMap.put("StepID", wStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("Number"));
				if (wID > 0) {
					return true;
				}
			}

			// 判断如果此工序放行了，返回true
			if (wResult == false) {
				boolean wCheckResult = SFCLetPassBPMDAO.getInstance().JudgeStepIsLetGo(wLoginUser, wTaskStep.OrderID,
						wStepID, wErrorCode);
				if (wCheckResult) {
					wResult = true;
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取台车部件编码
	 */
	public List<FocasPart> SelectMSSPartList(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		List<FocasPart> wResult = new ArrayList<FocasPart>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT t1.ID,t3.Code,t3.Name,t5.Name as LineName,t6.ProductNo,"
					+ "t7.CustomerName,t4.PartNo,t1.EditTime,t8.Name as Editor,t1.SupplierName,"
					+ "t1.SupplierProductNo,t1.SupplierPartNo,t1.Certificate,t1.Record,t1.QRCode,t1.Remark,"
					+ "t1.ImagePath FROM {1}.mss_partitem t1,{1}.mss_partconfig t2,"
					+ "{1}.mss_parttype t3,{0}.oms_order t4,{1}.fmc_line t5,"
					+ "{1}.fpc_product t6,{1}.crm_customer t7,{1}.mbs_user t8 "
					+ "where t1.EditorID=t8.ID and t1.OrderID=t4.ID and t4.LineID=t5.ID and t4.ProductID=t6.ID "
					+ "and t4.BureauSectionID=t7.ID and t1.ConfigID=t2.ID and t2.PartTypeID=t3.ID and t1.OrderID=:OrderID;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				FocasPart wFocasPart = new FocasPart();
				wFocasPart.setCertificate(StringUtils.parseInt(wMap.get("Certificate")) == 0 ? "无" : "有");
				wFocasPart.setCode(StringUtils.parseString(wMap.get("Code")));
				wFocasPart.setCustomerName(StringUtils.parseString(wMap.get("CustomerName")));
				wFocasPart.setEditor(StringUtils.parseString(wMap.get("Editor")));
				wFocasPart.setEditTime(StringUtils.parseCalendar(wMap.get("EditTime")));
				wFocasPart.setID(StringUtils.parseInt(wMap.get("ID")));

				String wImathPath = StringUtils.parseString(wMap.get("ImagePath"));
				if (StringUtils.isNotEmpty(wImathPath)) {
					String[] wStrs = StringUtils.split(wImathPath, "+|;|+");
					wFocasPart.setImagePath(StringUtils.Join(";", wStrs));
				}

				wFocasPart.setLineName(StringUtils.parseString(wMap.get("LineName")));
				wFocasPart.setName(StringUtils.parseString(wMap.get("Name")));
				wFocasPart.setPartNo(StringUtils.parseString(wMap.get("PartNo")));
				wFocasPart.setProductNo(StringUtils.parseString(wMap.get("ProductNo")));
				wFocasPart.setQRCode(StringUtils.parseInt(wMap.get("QRCode")) == 0 ? "无" : "有");
				wFocasPart.setRecord(StringUtils.parseInt(wMap.get("Record")) == 0 ? "无" : "有");
				wFocasPart.setRemark(StringUtils.parseString(wMap.get("Remark")));
				wFocasPart.setSupplierName(StringUtils.parseString(wMap.get("SupplierName")));
				wFocasPart.setSupplierPartNo(StringUtils.parseString(wMap.get("SupplierPartNo")));
				wFocasPart.setSupplierProductNo(StringUtils.parseString(wMap.get("SupplierProductNo")));
				wFocasPart.setCode(StringUtils.parseString(wMap.get("SupplierPartNo")));

				wResult.add(wFocasPart);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取工位任务集合
	 */
	public List<APSTaskPart> SelectTaskPartList(BMSEmployee wLoginUser, Calendar wTodaySTime, Calendar wTodayETime,
			int wIsComplete, OutResult<Integer> wErrorCode) {
		List<APSTaskPart> wResult = new ArrayList<APSTaskPart>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wEXCInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wEXCInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wAPSInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wAPSInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT t4.OrderNum,t1.ID,t1.OrderID,t2.Name as LineName,t1.StartWorkTime,"
					+ " t3.Name as PartName,t3.ID as PartID,t1.PartNo,t1.Status,t1.RouteID FROM {1}.aps_taskpart t1"
					+ " left join {0}.fmc_line t2 on t1.LineID=t2.ID "
					+ " left join {0}.fpc_part t3 on  t1.PartID=t3.ID "
					+ " left join {2}.lfs_workareastation t4 on t4.Active=1 and t1.PartID=t4.StationID  "
					+ " where t1.Active=1 and t1.ShiftPeriod=5"
					+ " and (t1.Status in (2,4) or (t1.Status=5 and :StartTime < t1.FinishWorkTime"
					+ " and :EndTime > t1.StartWorkTime) )  ;", wInstance.Result, wAPSInstance.Result,
					wEXCInstance.Result);
			if (wIsComplete == 1) {
				wSQL = StringUtils.Format(
						"SELECT t4.OrderNum,t1.ID,t1.OrderID,t2.Name as LineName,t1.StartWorkTime,"
								+ " t3.Name as PartName,t3.ID as PartID,t1.PartNo,t1.Status FROM {1}.aps_taskpart t1"
								+ " left join {0}.fmc_line t2 on t1.LineID=t2.ID "
								+ " left join {0}.fpc_part t3 on  t1.PartID=t3.ID "
								+ " left join {2}.lfs_workareastation t4 on t4.Active=1 and t1.PartID=t4.StationID  "
								+ " where t1.Active=1 and t1.ShiftPeriod=5 and t1.Status=5"
								+ " and :StartTime < t1.FinishWorkTime and :EndTime > t1.StartWorkTime  ;",
						wInstance.Result, wAPSInstance.Result, wEXCInstance.Result);
			} else if (wIsComplete == 0) {
				wSQL = StringUtils.Format(
						"SELECT t4.OrderNum,t1.ID,t1.OrderID,t2.Name as LineName,t1.StartWorkTime,"
								+ " t3.Name as PartName,t3.ID as PartID,t1.PartNo,t1.Status FROM {1}.aps_taskpart t1"
								+ " left join {0}.fmc_line t2 on t1.LineID=t2.ID "
								+ " left join {0}.fpc_part t3 on  t1.PartID=t3.ID "
								+ " left join {2}.lfs_workareastation t4 on t4.Active=1 and t1.PartID=t4.StationID  "
								+ " where t1.Active=1 and t1.ShiftPeriod=5" + " and t1.Status in (2,4);",
						wInstance.Result, wAPSInstance.Result, wEXCInstance.Result);
			}

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("StartTime", wTodaySTime);
			wParamMap.put("EndTime", wTodayETime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				APSTaskPart wAPSTaskPart = new APSTaskPart();

				wAPSTaskPart.ID = StringUtils.parseInt(wMap.get("ID"));
				wAPSTaskPart.LineName = StringUtils.parseString(wMap.get("LineName"));
				wAPSTaskPart.StartWorkTime = StringUtils.parseCalendar(wMap.get("StartWorkTime"));
				wAPSTaskPart.PartName = StringUtils.parseString(wMap.get("PartName"));
				wAPSTaskPart.PartNo = StringUtils.parseString(wMap.get("PartNo"));
				wAPSTaskPart.Status = StringUtils.parseInt(wMap.get("Status"));
				wAPSTaskPart.PartID = StringUtils.parseInt(wMap.get("PartID"));
				wAPSTaskPart.OrderID = StringUtils.parseInt(wMap.get("OrderID"));
				wAPSTaskPart.RouteID = StringUtils.parseInt(wMap.get("RouteID"));

				wResult.add(wAPSTaskPart);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<RSMTurnOrderTask> SelectByTaskPartIDList(BMSEmployee wLoginUser, List<Integer> wTaskPartIDList,
			OutResult<Integer> wErrorCode) {
		List<RSMTurnOrderTask> wResultList = new ArrayList<RSMTurnOrderTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wTaskPartIDList == null || wTaskPartIDList.size() <= 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.sfc_turnordertask WHERE  1=1  "
							+ "and ( :wTaskPartID is null or :wTaskPartID = '''' or TaskPartID in ({1}));",
					wInstance.Result, wTaskPartIDList.size() > 0 ? StringUtils.Join(",", wTaskPartIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wTaskPartID", StringUtils.Join(",", wTaskPartIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 查询出已完工，但转序单未生成的工位任务
	 */
	public List<APSTaskPart> SelectNotGenerateTurnOrderFormList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<APSTaskPart> wResult = new ArrayList<APSTaskPart>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
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
					"SELECT t1.* FROM {0}.aps_taskpart t1 where active=1 and shiftperiod=5 and status=5 and 0 "
							+ "in (select count(*) from {1}.sfc_turnordertask where taskpartid=t1.ID) and partid!=103;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				APSTaskPart wItem = new APSTaskPart();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.FinishWorkTime = StringUtils.parseCalendar(wReader.get("FinishWorkTime"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工艺路线集合查询最后的工位映射
	 */
	public List<FPCRoutePart> SelectLastPartIDMap(BMSEmployee wLoginUser, List<Integer> wRouteIDList,
			OutResult<Integer> wErrorCode) {
		List<FPCRoutePart> wResult = new ArrayList<FPCRoutePart>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT t1.* FROM {0}.fpc_routepart t1  where routeid in ({1}) and NextPartIDMap =''{2}'' "
							+ "and  0 in (select count(*) from {0}.fpc_routepart where PrevPartID=t1.PartID);",
					wInstance.Result, StringUtils.Join(",", wRouteIDList), "{}");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wRouteID = StringUtils.parseInt(wReader.get("RouteID"));
				int wPartID = StringUtils.parseInt(wReader.get("PartID"));

				FPCRoutePart wFPCRoutePart = new FPCRoutePart();
				wFPCRoutePart.RouteID = wRouteID;
				wFPCRoutePart.PartID = wPartID;
				wResult.add(wFPCRoutePart);

//				if (!wResult.containsKey(wRouteID)) {
//					wResult.put(wRouteID, new ArrayList<Integer>(Arrays.asList(wPartID)));
//				} else {
//					List<Integer> wPartIDList = wResult.get(wRouteID);
//					if (!wPartIDList.stream().anyMatch(p -> p == wPartID)) {
//						wPartIDList.add(wPartID);
//					}
//				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断此工位是否设置了放行
	 */
	public boolean JudgeIsLetGo(BMSEmployee wLoginUser, int wOrderID, Integer wPartID, OutResult<Integer> wErrorCode) {
		boolean wResult = false;
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

			String wSQL = StringUtils.Format("select ChangeControl from {0}.fpc_routepart "
					+ "where routeid in (select RouteID from {1}.oms_order where ID=:wOrderID) and PartID=:wPartID;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wChangeControl = StringUtils.parseInt(wReader.get("ChangeControl"));
				if (wChangeControl == 2 || wChangeControl == 3) {
					return true;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<SFCTaskIPT> SelectRealFinishList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResult = new ArrayList<SFCTaskIPT>();
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
					"SELECT t1.TaskStepID,t1.EndTime FROM {0}.sfc_taskipt t1,{1}.aps_taskstep t2 "
							+ "where t1.TaskStepID=t2.ID and t1.TaskType=13 "
							+ "and t1.Status=2 and t2.Status!=5 and t1.Active=1 and t2.Active=1;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCTaskIPT wItem = new SFCTaskIPT();

				wItem.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID"));
				wItem.EndTime = StringUtils.parseCalendar(wReader.get("EndTime"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 更新状态
	 */
	public void UpdateStatus(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("Update {0}.aps_taskstep set Status=5,EndTime=:EndTime where ID=:ID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("EndTime", wSFCTaskIPT.EndTime);
			wParamMap.put("ID", wSFCTaskIPT.TaskStepID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 查询订单工位状态字段
	 */
	public Map<Integer, Integer> SelectPartStatus(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		Map<Integer, Integer> wResult = new HashMap<Integer, Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT PartID,Status FROM {0}.aps_taskpart "
					+ "where OrderID=:OrderID and ShiftPeriod=5 and Active=1 and Status in (4,5) order by Status asc;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wPartID = StringUtils.parseInt(wReader.get("PartID"));
				int wStatus = StringUtils.parseInt(wReader.get("Status"));
				wResult.put(wPartID, wStatus);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断此工位任务是否已发起待办消息
	 */
	public boolean JudgeIsSendMessage(BMSEmployee wLoginUser, int taskPartID, OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select count(*) Number from {0}.bfc_message where " + "moduleid=8400 and messageid=:messageid;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("messageid", taskPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wNumber = StringUtils.parseInt(wReader.get("Number"));
				return wNumber > 0 ? true : false;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public void CloseMessage(BMSEmployee wLoginUser, RSMTurnOrderTask wData, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"update {0}.bfc_message set Active=3 where " + "ModuleID=8400 and MessageID=:MessageID and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("MessageID", wData.ID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public List<Integer> SelectDisableMessageIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
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
					"select t1.ID from {0}.bfc_message t1,{1}.sfc_taskstep t2 where t1.MessageID=t2.ID "
							+ "and t1.Active in (0,1,2) and t2.Active=2 and t1.ModuleID=8103;",
					wInstance.Result, wInstance1.Result);

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

	public void DisableMessage(BMSEmployee wLoginUser, String wIDs, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("update {0}.bfc_message set Active=4 where ID in ({1});", wInstance.Result,
					wIDs);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public List<Integer> SelectTurnOrderConfirmMessageIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select t1.ID from {0}.bfc_message t1,{0}.sfc_turnordertask t2 "
					+ "where t2.ID=t1.MessageID and (t2.Status=2 or t2.ConfirmID>0)  "
					+ "and t1.Active in(0,1,2) and t1.ModuleID=8400;", wInstance.Result);

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

	/**
	 * 判断例外放行单是否全部关闭，且例外放行工序是否全部完成
	 */
	public String CheckLetGo(BMSEmployee wLoginUser, RSMTurnOrderTask wSFCTurnOrderTask,
			OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			List<NCRTask> wList = SFCLetPassBPMDAO.getInstance().SelectListByClosePartID_1(wLoginUser,
					wSFCTurnOrderTask.OrderID, wSFCTurnOrderTask.ApplyStationID,
					new ArrayList<Integer>(Arrays.asList(0, 21, 22)), wErrorCode);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wSFCTurnOrderTask.OrderID)
					.Info(OMSOrder.class);

			for (NCRTask wSendNCRTask : wList) {
				if (!wSendNCRTask.StatusText.contains("已")) {
					wResult = StringUtils.Format("提示：【{0}】-【{1}】单号为【{2}】的例外放行单未关闭!", wOrder.PartNo,
							QMSConstants.GetFPCPartName(wSendNCRTask.StationID), wSendNCRTask.Code);
					return wResult;
				}
				// 判断例外放行工序是否完成
				if (StringUtils.isEmpty(wSendNCRTask.StepIDs)) {
					continue;
				}
				String[] wStrs = wSendNCRTask.StepIDs.split(",");
				for (String wStepStr : wStrs) {
					int wStepID = StringUtils.parseInt(wStepStr);
					if (wStepID <= 0) {
						continue;
					}
					// 判断工序任务是否完成
					boolean wFlag = SFCLetPassBPMDAO.getInstance().IsStepFinished(wLoginUser, wOrder,
							wSendNCRTask.StationID, wStepID, wErrorCode);
					if (!wFlag) {
						wResult = StringUtils.Format("提示：【{0}】-【{1}】单号为【{2}】的例外放行单中【{3}】工序任务未完成!", wOrder.PartNo,
								QMSConstants.GetFPCPartName(wSendNCRTask.StationID), wSendNCRTask.Code,
								QMSConstants.GetFPCStepName(wStepID));
						return wResult;
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断例外放行单是否全部关闭，且例外放行工序是否全部完成
	 */
	public String CheckLetGo(BMSEmployee wLoginUser, int wOrderID, int wClosePartID, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			List<NCRTask> wList = SFCLetPassBPMDAO.getInstance().SelectListByClosePartID_1(wLoginUser, wOrderID,
					wClosePartID, new ArrayList<Integer>(Arrays.asList(0, 21, 22)), wErrorCode);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);

			for (NCRTask wSendNCRTask : wList) {
				if (!wSendNCRTask.StatusText.contains("已")) {
					wResult = StringUtils.Format("提示：【{0}】-【{1}】单号为【{2}】的例外放行单未关闭!", wOrder.PartNo,
							QMSConstants.GetFPCPartName(wSendNCRTask.StationID), wSendNCRTask.Code);
					return wResult;
				}
				// 判断例外放行工序是否完成
				if (StringUtils.isEmpty(wSendNCRTask.StepIDs)) {
					continue;
				}
				String[] wStrs = wSendNCRTask.StepIDs.split(",");
				for (String wStepStr : wStrs) {
					int wStepID = StringUtils.parseInt(wStepStr);
					if (wStepID <= 0) {
						continue;
					}
					// 判断工序任务是否完成
					boolean wFlag = SFCLetPassBPMDAO.getInstance().IsStepFinished(wLoginUser, wOrder,
							wSendNCRTask.StationID, wStepID, wErrorCode);
					if (!wFlag) {
						wResult = StringUtils.Format("提示：【{0}】-【{1}】单号为【{2}】的例外放行单中【{3}】工序任务未完成!", wOrder.PartNo,
								QMSConstants.GetFPCPartName(wSendNCRTask.StationID), wSendNCRTask.Code,
								QMSConstants.GetFPCStepName(wStepID));
						return wResult;
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<Integer> SelectRouteIDList(BMSEmployee wLoginUser, List<Integer> wIDList,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT RouteID FROM {0}.oms_order WHERE id IN ({1});", wInstance.Result,
					StringUtils.Join(",", wIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wRouteID = StringUtils.parseInt(wReader.get("RouteID"));
				wResult.add(wRouteID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public Map<Integer, Calendar> GetTaskStepMap(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		Map<Integer, Calendar> wResult = new HashMap<Integer, Calendar>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT t1.ID,t2.EndTime FROM {0}.aps_taskstep t1,{0}.sfc_taskipt t2 "
							+ "WHERE t1.status=4 AND t2.taskstepid=t1.ID AND t2.tasktype=13 AND t2.status=2;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wID = StringUtils.parseInt(wReader.get("ID"));
				Calendar wEndTime = StringUtils.parseCalendar(wReader.get("EndTime"));

				wResult.put(wID, wEndTime);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public boolean IsAllTaskIPTDone(BMSEmployee wLoginUser, int wTaskStepID, OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT COUNT(*) Number FROM {0}.sfc_taskipt WHERE taskstepid=:taskstepid AND STATUS=2;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("taskstepid", wTaskStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wNumber = StringUtils.parseInt(wReader.get("Number"));
				if (wNumber == 3) {
					wResult = true;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public void UpdateTaskStepStatus(BMSEmployee wLoginUser, int wTaskStepID, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("UPDATE {0}.aps_taskstep SET STATUS=5,endtime=:endtime WHERE id=:id;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("endtime", wEndTime);
			wParamMap.put("id", wTaskStepID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public List<Integer> GetTaskPartIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT ID FROM {0}.APS_TaskPart WHERE ID IN (SELECT DISTINCT t1.taskpartid "
							+ "FROM {0}.aps_taskstep t1,{0}.aps_taskpart t2 "
							+ "WHERE t1.TaskPartID=t2.ID AND t1.active=1 AND t1.status !=5 AND t2.status=5);",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wID = StringUtils.parseInt(wReader.get("ID"));
				if (wID > 0) {
					wResult.add(wID);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public int GetStatus(BMSEmployee wLoginUser, int wTaskPartID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT MAX(STATUS) Number FROM {0}.aps_taskstep WHERE taskpartid=:taskpartid and Active=1;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("taskpartid", wTaskPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("Number"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public void UpdateTaskPart(BMSEmployee wLoginUser, int wStatus, int wTaskPartID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("UPDATE {0}.APS_TaskPart SET STATUS=:STATUS WHERE ID=:ID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wTaskPartID);
			wParamMap.put("STATUS", wStatus);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public void DeleteTurnOrderTask(BMSEmployee wLoginUser, int wTaskPartID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"DELETE FROM {0}.sfc_turnordertask WHERE TaskPartID=:TaskPartID AND ApplyID=-100 AND ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("TaskPartID", wTaskPartID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}
