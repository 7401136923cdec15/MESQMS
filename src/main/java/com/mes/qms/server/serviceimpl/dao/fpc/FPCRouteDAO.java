package com.mes.qms.server.serviceimpl.dao.fpc;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.fpc.FPCPartPoint;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.po.fpc.FPCStepSOP;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class FPCRouteDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(FPCRouteDAO.class);

	private static FPCRouteDAO Instance = null;

	private FPCRouteDAO() {
		super();
	}

	public static FPCRouteDAO getInstance() {
		if (Instance == null)
			Instance = new FPCRouteDAO();
		return Instance;
	}

	/**
	 * 根据RouteID查询所有，同车型、修程、局段的RouteID集合
	 */
	public List<Integer> FPC_QueryRouteIDList(BMSEmployee wLoginUser, int wRouteID, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT t2.ID FROM {0}.fpc_route t1,{0}.fpc_route t2 "
					+ "where t1.ProductID=t2.ProductID and t1.LineID=t2.LineID and t1.CustomerID=t2.CustomerID and t1.ID=:wRouteID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wRouteID", wRouteID);

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

	/**
	 * 根据RouteID集合、工位查找所有工艺工位数据ID集合
	 */
	public List<Integer> FPC_QueryRoutePartIDList(BMSEmployee wLoginUser, List<Integer> wRouteIDList, int wPartID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wRouteIDList == null || wRouteIDList.size() <= 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select ID from {0}.fpc_routepart " + "where routeid in ({1}) and partid=:wPartID;",
					wInstance.Result, StringUtils.Join(",", wRouteIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wPartID", wPartID);

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

	/**
	 * 根据工艺工位数据ID集合、转序控制值修改所有工艺工位数据
	 */
	public void FPC_BatchUpdateChangeControl(BMSEmployee wLoginUser, List<Integer> wRoutePartIDList, int wChangeControl,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			if (wRoutePartIDList == null || wRoutePartIDList.size() <= 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"update {0}.fpc_routepart set ChangeControl=:wChangeControl where ID in ({1});", wInstance.Result,
					StringUtils.Join(",", wRoutePartIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wChangeControl", wChangeControl);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 根据工位获取班组长ID集合
	 */
	public List<Integer> GetMonitorIDList(BMSEmployee wLoginUser, int wPartID, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT t1.ID FROM {0}.mbs_user t1,{0}.bms_position t2 "
							+ "where t1.Position=t2.ID and t2.DutyID=1 and  t1. DepartmentID "
							+ "in (SELECT ClassID FROM {0}.bms_workcharge where StationID=:StationID and Active=1);",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("StationID", wPartID);

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

	/**
	 * 根据订单、工位、工序判断工序任务是否已完工
	 */
	public boolean JudgeStepTaskIsFinished(BMSEmployee wLoginUser, Integer wOrderID, int wPartID, Integer wStepID,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT Status FROM {0}.aps_taskstep "
							+ "where OrderID=:OrderID and PartID=:PartID and StepID=:StepID and Active=1;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("PartID", wPartID);
			wParamMap.put("StepID", wStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wStatus = StringUtils.parseInt(wReader.get("Status"));
				if (wStatus == 5) {
					wResult = true;
					return wResult;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据订单、工位、工序获取此工序的自检或预检员
	 */
	public List<Integer> GetWorkerIDList(BMSEmployee wLoginUser, Integer wOrderID, int wPartID, Integer wStepID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT OperatorList FROM {0}.sfc_taskipt "
					+ "where OrderID=:OrderID and StationID=:StationID and PartPointID=:PartPointID "
					+ "and TaskType in (6,14);", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("StationID", wPartID);
			wParamMap.put("PartPointID", wStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				String wOperatorList = StringUtils.parseString(wReader.get("OperatorList"));
				if (StringUtils.isNotEmpty(wOperatorList)) {
					wResult = StringUtils.parseIntList(wOperatorList.split(";"));
					return wResult;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<FPCPartPoint> FMC_QueryLineUnitStepList(BMSEmployee wLoginUser, int wProductID, int wLineID,
			int wPartID, OutResult<Integer> wErrorCode) {
		List<FPCPartPoint> wResult = new ArrayList<FPCPartPoint>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils
					.Format("SELECT UnitID FROM {0}.fmc_lineunit " + "where ProductID=:ProductID and LineID=:LineID "
							+ "and LevelID=3 and ParentUnitID=:PartID and Active=1;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ProductID", wProductID);
			wParamMap.put("LineID", wLineID);
			wParamMap.put("PartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				FPCPartPoint wItem = new FPCPartPoint();

				wItem.ID = StringUtils.parseInt(wReader.get("UnitID"));
				wItem.Name = QMSConstants.GetFPCStepName(wItem.ID);

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询车体部分的工位ID集合
	 */
	public List<Integer> FPC_QueryBodyPartList(BMSEmployee wLoginUser, int wStationType,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT StationID FROM {0}.lfs_workareastation " + "where Active=1 and StationType=:wStationType;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wStationType", wStationType);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wStationID = StringUtils.parseInt(wReader.get("StationID"));
				wResult.add(wStationID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据RoutePartPointID集合获取工艺文件
	 */
	public List<FPCStepSOP> SelectSOPList(BMSEmployee wLoginUser, List<Integer> wIDList,
			OutResult<Integer> wErrorCode) {
		List<FPCStepSOP> wResult = new ArrayList<FPCStepSOP>();
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

			String wSQL = StringUtils.Format(
					"SELECT RoutePartPointID,FilePath FROM {0}.fpc_stepsop " + "where RoutePartPointID in ({1});",
					wInstance.Result, StringUtils.Join(",", wIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				FPCStepSOP wItem = new FPCStepSOP();

				wItem.RoutePartPointID = StringUtils.parseInt(wReader.get("RoutePartPointID"));
				wItem.FilePath = StringUtils.parseString(wReader.get("FilePath"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工艺路线ID获取订单ID集合
	 */
	public List<Integer> SelectOrderIDListByRouteID(BMSEmployee wLoginUser, int wRouteID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select ID from {0}.oms_order " + "where RouteID=:wRouteID and Status in (3,4);", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wRouteID", wRouteID);

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
	 * 禁用工位计划
	 */
	public void DisableTaskPart(BMSEmployee wLoginUser, int wOrderID, int wPartID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSql = MessageFormat.format(
					"update {0}.aps_taskpart set Active=2 "
							+ "where OrderID={1} and ShiftPeriod=5 and Active=1 and PartID={2} and ID>0;",
					wInstance.Result, String.valueOf(wOrderID), String.valueOf(wPartID));

			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 禁用工序计划
	 */
	public void DisableTaskPartPoint(BMSEmployee wLoginUser, Integer wOrderID, int wPartID,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSql = MessageFormat.format(
					"update {0}.aps_taskstep set Active=2 where OrderID={1} and Active=1 and PartID={2} and ID>0;",
					wInstance.Result, String.valueOf(wOrderID), String.valueOf(wPartID));

			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 查询转序控制字典
	 */
	public Map<Integer, Integer> FPC_QueryControlMap(BMSEmployee wLoginUser, List<Integer> wRoutePartIDList,
			OutResult<Integer> wErrorCode) {
		Map<Integer, Integer> wResult = new HashMap<Integer, Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT ChangeControl,ID FROM {0}.fpc_routepart where ID in ({1});",
					wInstance.Result, StringUtils.Join(",", wRoutePartIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wChangeControl = StringUtils.parseInt(wReader.get("ChangeControl"));
				int wID = StringUtils.parseInt(wReader.get("ID"));
				wResult.put(wID, wChangeControl);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public void UpdateSpecialControl(BMSEmployee wLoginUser, FPCRoutePart wFPCRoutePart,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("update {0}.fpc_routepart set ControlPartIDList=''{1}'' where ID=:ID;",
					wInstance.Result, StringUtils.Join(",", wFPCRoutePart.ControlPartIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wFPCRoutePart.ID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public String GetControls(BMSEmployee wLoginUser, FPCRoutePart wFPCRoutePart, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT ControlPartIDList FROM {0}.fpc_routepart where ID=:ID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wFPCRoutePart.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseString(wReader.get("ControlPartIDList"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<FPCRoutePartPoint> FPC_QueryErrorStepList(BMSEmployee wLoginUser, int wPartPointID, int wRightPartID,
			OutResult<Integer> wErrorCode) {
		List<FPCRoutePartPoint> wResult = new ArrayList<FPCRoutePartPoint>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select t1.* from {0}.fpc_routepartpoint t1,"
					+ "{0}.fpc_route t2 where t1.routeid=t2.id and t1.partpointid=:wPartPointID "
					+ "and t2.IsStandard=1 and t1.PartID != :PartID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wPartPointID", wPartPointID);
			wParamMap.put("PartID", wRightPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				FPCRoutePartPoint wItem = new FPCRoutePartPoint();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("RouteID"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.PartPointID = StringUtils.parseInt(wReader.get("PartPointID"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public void UpdateRightPartID(BMSEmployee wLoginUser, FPCRoutePartPoint wFPCRoutePartPoint,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("update {0}.fpc_routepartpoint set PartID=:PartID where ID=:ID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("PartID", wFPCRoutePartPoint.PartID);
			wParamMap.put("ID", wFPCRoutePartPoint.ID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取工序任务ID
	 */
	public APSTaskStep GetTaskStepID(BMSEmployee wLoginUser, int wOrderID, int wPartPointID,
			OutResult<Integer> wErrorCode) {
		APSTaskStep wResult = new APSTaskStep();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select * from {0}.aps_taskstep "
					+ "where orderid=:wOrderID and stepid=:wPartPointID and active=1;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartPointID", wPartPointID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult.ID = StringUtils.parseInt(wReader.get("ID"));
				wResult.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wResult.StepID = StringUtils.parseInt(wReader.get("StepID"));
				wResult.TaskPartID = StringUtils.parseInt(wReader.get("TaskPartID"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取工位任务ID
	 */
	public int GetTaskPartID(BMSEmployee wLoginUser, int wOrderID, int wPartID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select ID from iplantmes.aps_taskpart where "
							+ "shiftperiod=5 and active=1 and orderid=:wOrderID and partid=:wPartID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wPartID);

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

	public List<List<String>> GetBOPList(BMSEmployee wLoginUser, int wRouteID, OutResult<Integer> wErrorCode) {
		List<List<String>> wResult = new ArrayList<List<String>>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select t3.Name as PartName,t4.Name as StepName from {0}.fpc_routepartpoint t1,"
							+ "{0}.fpc_routepart t2,{0}.fpc_part t3,{0}.fpc_partpoint t4 "
							+ "where t1.PartID=t3.ID and t1.PartPointID=t4.ID and t1.RouteID=:RouteID "
							+ "and t2.PartID=t1.PartID and t2.RouteID=:RouteID order by t2.OrderID asc;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("RouteID", wRouteID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			int wIndex = 1;
			for (Map<String, Object> wReader : wQueryResult) {
				List<String> wList = new ArrayList<String>();

				String wPartName = StringUtils.parseString(wReader.get("PartName"));
				String wStepName = StringUtils.parseString(wReader.get("StepName"));

				wList.add(String.valueOf(wIndex));
				wList.add(wPartName);
				wList.add(wStepName);
				wResult.add(wList);

				wIndex++;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

}
