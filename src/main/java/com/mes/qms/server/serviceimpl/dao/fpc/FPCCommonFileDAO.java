package com.mes.qms.server.serviceimpl.dao.fpc;

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
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.fpc.FPCCommonFile;
import com.mes.qms.server.service.po.fpc.FPCRoute;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.utils.DesUtil;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class FPCCommonFileDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(FPCCommonFileDAO.class);

	private static FPCCommonFileDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wFPCCommonFile
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, FPCCommonFile wFPCCommonFile, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wFPCCommonFile == null)
				return 0;

			String wSQL = "";
			if (wFPCCommonFile.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.fpc_commonfile(Code,FilePath,FileName,DocRev,CreateTime) "
						+ "VALUES(:Code,:FilePath,:FileName,:DocRev,now());", wInstance.Result);
			} else {
				wSQL = MessageFormat
						.format("UPDATE {0}.fpc_commonfile SET Code = :Code,FilePath = :FilePath,FileName = :FileName,"
								+ "DocRev = :DocRev,CreateTime=now() WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wFPCCommonFile.ID);
			wParamMap.put("Code", wFPCCommonFile.Code);
			wParamMap.put("FilePath", wFPCCommonFile.FilePath);
			wParamMap.put("FileName", wFPCCommonFile.FileName);
			wParamMap.put("DocRev", wFPCCommonFile.DocRev);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wFPCCommonFile.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wFPCCommonFile.setID(wResult);
			} else {
				wResult = wFPCCommonFile.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<FPCCommonFile> wList,
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
			for (FPCCommonFile wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.fpc_commonfile WHERE ID IN({0}) ;",
					StringUtils.Join(",", wIDList), wInstance.Result);
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
	public FPCCommonFile SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		FPCCommonFile wResult = new FPCCommonFile();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<FPCCommonFile> wList = SelectList(wLoginUser, wID, "", null, null, wErrorCode);
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
	 * @param wEndTime
	 * @param wStartTime
	 * 
	 * @return
	 */
	public List<FPCCommonFile> SelectList(BMSEmployee wLoginUser, int wID, String wCode, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<FPCCommonFile> wResultList = new ArrayList<FPCCommonFile>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
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

			String wSQL = MessageFormat.format("SELECT * FROM {0}.fpc_commonfile WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) "
					+ "and ( :wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wStartTime <= CreateTime) "
					+ "and ( :wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wEndTime >= CreateTime) "
					+ "and ( :wCode is null or :wCode = '''' or :wCode = Code );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				FPCCommonFile wItem = new FPCCommonFile();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.FilePath = StringUtils.parseString(wReader.get("FilePath"));
				wItem.FileName = StringUtils.parseString(wReader.get("FileName"));
				wItem.DocRev = StringUtils.parseString(wReader.get("DocRev"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private FPCCommonFileDAO() {
		super();
	}

	public static FPCCommonFileDAO getInstance() {
		if (Instance == null)
			Instance = new FPCCommonFileDAO();
		return Instance;
	}

	/**
	 * 删除产线单元明细
	 */
	public void FPC_DeleteLineUnit(BMSEmployee wLoginUser, int wTLineID, int wSProductID,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"delete FROM {0}.fmc_lineunit " + "where LineID=:LineID and ProductID=:ProductID and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("LineID", wTLineID);
			wParamMap.put("ProductID", wSProductID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 根据用户ID获取加密字符串
	 */
	public String GetTokenUser(BMSEmployee wLoginUser, int wUserID, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT LoginName,Password FROM {0}.mbs_user where ID=:UserID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("UserID", wUserID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				String wLoginName = StringUtils.parseString(wReader.get("LoginName"));
				String wPass = StringUtils.parseString(wReader.get("Password"));

				wResult = StringUtils.Format("cadv_ao={0}&cade_po={1}", DesUtil.encrypt(wLoginName, appSecret), wPass);
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据新版工艺路线ID获取正在使用的工艺bopID
	 */
	public int GetUsingRouteID(BMSEmployee wLoginUser, int wNewRouteID, OutResult<Integer> wErrorCode) {
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

//			String wSQL = StringUtils
//					.Format("SELECT t1.ID FROM {0}.fpc_route t1,{0}.fpc_route t2 where t2.ID=:wNewRouteID "
//							+ "and t1.LineID=t2.LineID and t1.ProductID=t2.ProductID "
//							+ "and t1.CustomerID=t2.CustomerID and t1.IsStandard=1;", wInstance.Result);

			String wSQL = StringUtils.Format(
					"select distinct t1.RouteID ID from {1}.oms_order t1,{0}.fpc_route t2 "
							+ "where t1.status=4 and t1.ProductID=t2.ProductID "
							+ "and t1.LineID=t2.LineID and t1.BureauSectionID=t2.CustomerID and t2.ID=:wNewRouteID;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wNewRouteID", wNewRouteID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wID = StringUtils.parseInt(wReader.get("ID"));
				if (wID > 0) {
					wResult = wID;
				}
			}

			if (wResult <= 0) {
				wResult = GetCurrentRouteID(wLoginUser, wNewRouteID, wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据当前的工艺BOP
	 */
	public int GetCurrentRouteID(BMSEmployee wLoginUser, int wNewRouteID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils
					.Format("SELECT t1.ID FROM {0}.fpc_route t1,{0}.fpc_route t2 where t2.ID=:wNewRouteID "
							+ "and t1.LineID=t2.LineID and t1.ProductID=t2.ProductID "
							+ "and t1.CustomerID=t2.CustomerID and t1.IsStandard=1;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wNewRouteID", wNewRouteID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wID = StringUtils.parseInt(wReader.get("ID"));
				if (wID > 0) {
					wResult = wID;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工艺bopID获取正在使用的车辆列表
	 */
	public List<String> GetPartNoList(BMSEmployee wLoginUser, int wRouteID, OutResult<Integer> wErrorCode) {
		List<String> wResult = new ArrayList<String>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT PartNo,ID FROM {0}.oms_order where RouteID=:wRouteID and Status=4;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wRouteID", wRouteID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			List<Integer> wOrderIDList = new ArrayList<Integer>();
			for (Map<String, Object> wReader : wQueryResult) {
				String wPartNo = StringUtils.parseString(wReader.get("PartNo"));
				int wOrderID = StringUtils.parseInt(wReader.get("ID"));
				if (StringUtils.isNotEmpty(wPartNo)) {
					wResult.add(wPartNo);
				}
				wOrderIDList.add(wOrderID);
			}
			wErrorCode.add("OrderIDList", wOrderIDList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取订单ID集合
	 */
	public List<Integer> GetOrderIDList(BMSEmployee wLoginUser, List<String> wPartNoList,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			if (wPartNoList == null || wPartNoList.size() <= 0) {
				return wResult;
			}

			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT ID FROM {0}.oms_order " + "where PartNo in (''{1}'');",
					wInstance.Result, StringUtils.Join("','", wPartNoList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wOrderID = StringUtils.parseInt(wReader.get("ID"));
				if (wOrderID > 0) {
					wResult.add(wOrderID);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断是否需要添加日计划
	 */
	public boolean JugdeIsNeedAddDayPlan(BMSEmployee wLoginUser, Integer wOrderID, int wPartID,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select "
					+ "(SELECT Count(*) FROM {0}.aps_taskstep where OrderID=:wOrderID and PartID=:wPartID) APSTaskStepNumber,"
					+ "(select Count(*) from {0}.aps_taskpart where OrderID=:wOrderID and PartID=:wPartID "
					+ "and ShiftPeriod=5 and Active=1 and Status !=5) APSTaskPartNumber,"
					+ "(select ID from {0}.aps_taskpart where OrderID=:wOrderID and PartID=:wPartID "
					+ "and ShiftPeriod=5 and Active=1 and Status !=5) TaskPartID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wAPSTaskStepNumber = StringUtils.parseInt(wReader.get("APSTaskStepNumber"));
				int wAPSTaskPartNumber = StringUtils.parseInt(wReader.get("APSTaskPartNumber"));
				if (wAPSTaskStepNumber > 0 && wAPSTaskPartNumber > 0) {
					wResult = true;
				}
				Integer wTaskPartID = StringUtils.parseInt(wReader.get("TaskPartID"));
				if (wTaskPartID > 0) {
					wErrorCode.set(wTaskPartID);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 修改工艺bop工序的工位ID
	 */
	public void UpdatePartID_RoutePartPoint(BMSEmployee wLoginUser, FPCRoutePartPoint wFPCRoutePartPoint, int wRouteID,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"update {0}.fpc_routepartpoint set PartID=:wNewPartID "
							+ "where RouteID=:wRouteID and PartPointID=:wPartPointID and PartID=:wPartID and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wNewPartID", wFPCRoutePartPoint.NewPartID);
			wParamMap.put("wRouteID", wRouteID);
			wParamMap.put("wPartPointID", wFPCRoutePartPoint.PartPointID);
			wParamMap.put("wPartID", wFPCRoutePartPoint.PartID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 修改工序任务中的工位ID和工位任务ID
	 * 
	 * @param wErrorCode
	 */
	public void UpdateTaskPartPoint(BMSEmployee wLoginUser, FPCRoutePartPoint wFPCRoutePartPoint, Integer wOrderID,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("update {0}.aps_taskstep t1,{0}.aps_taskpart t2 "
					+ "set t1.TaskPartID=t2.ID , t1.PartID=:wNewPartID where  t2.OrderID=:wOrderID and t2.PartID=:wNewPartID "
					+ "and t2.Active=1 and t2.ShiftPeriod=5 and  t1.OrderID=:wOrderID "
					+ "and t1.PartID=:wPartID and t1.StepID=:wStepID and t1.ID>0;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wNewPartID", wFPCRoutePartPoint.NewPartID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wFPCRoutePartPoint.PartID);
			wParamMap.put("wStepID", wFPCRoutePartPoint.PartPointID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 修改工序单元明细中的工位ID
	 */
	public void UpdateLineUnit(BMSEmployee wLoginUser, FPCRoutePartPoint wFPCRoutePartPoint, FPCRoute wFPCRoute,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"update {0}.fmc_lineunit set ParentUnitID=:wNewPartID where LineID=:wLineID "
							+ "and ProductID=:wProductID and LevelID=3 and ParentUnitID=:wPartID and UnitID=:wStepID and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wNewPartID", wFPCRoutePartPoint.NewPartID);
			wParamMap.put("wLineID", wFPCRoute.LineID);
			wParamMap.put("wProductID", wFPCRoute.ProductID);
			wParamMap.put("wPartID", wFPCRoutePartPoint.PartID);
			wParamMap.put("wStepID", wFPCRoutePartPoint.PartPointID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 标准设置为非当前
	 */
	public void UpdateStandard(BMSEmployee wLoginUser, FPCRoutePartPoint wFPCRoutePartPoint, FPCRoute wFPCRoute,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

//			String wSQL = StringUtils.Format(
//					"update {0}.ipt_standard set PartID=:wNewPartID,IsCurrent=0 where LineID=:wLineID "
//							+ "and PartID=:wPartID and PartPointID=:wPartPointID and ProductID=:wProductID and ID>0;",
//					wInstance.Result);

			String wSQL = StringUtils.Format(
					"update {0}.ipt_standard set IsCurrent=0 where LineID=:wLineID "
							+ "and PartID=:wPartID and PartPointID=:wPartPointID and ProductID=:wProductID and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wLineID", wFPCRoute.LineID);
			wParamMap.put("wPartID", wFPCRoutePartPoint.NewPartID);
			wParamMap.put("wPartPointID", wFPCRoutePartPoint.PartPointID);
			wParamMap.put("wProductID", wFPCRoute.ProductID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 删除工艺bop工序
	 */
	public void DeleteRoutePartPoint(BMSEmployee wLoginUser, FPCRoutePartPoint wFPCRoutePartPoint,
			OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("delete from {0}.fpc_routepartpoint where ID=:wID and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wFPCRoutePartPoint.ID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 禁用工序计划
	 */
	public void DisableTaskStep(BMSEmployee wLoginUser, FPCRoutePartPoint wFPCRoutePartPoint, FPCRoute wFPCRoute,
			int wOrderID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"update {0}.aps_taskstep set Active=2,Remark=''0+|:|+2000-01-01 09:06:38+|:|+因工艺BOP变更取消该工序计划。'' "
							+ "where OrderID=:wOrderID and PartID=:wPartID and StepID=:wStepID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wFPCRoutePartPoint.PartID);
			wParamMap.put("wStepID", wFPCRoutePartPoint.PartPointID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 禁用自检单、专检单、预检单
	 */

	public void DisableTaskIPT(BMSEmployee wLoginUser, FPCRoutePartPoint wFPCRoutePartPoint, FPCRoute wFPCRoute,
			int wOrderID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format(
					"update {0}.sfc_taskipt set Active=2 "
							+ "where OrderID=:wOrderID and StationID=:wPartID and PartPointID=:wStepID and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wFPCRoutePartPoint.PartID);
			wParamMap.put("wStepID", wFPCRoutePartPoint.PartPointID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 禁用派工单
	 */
	public void DisableSFCTaskStep(BMSEmployee wLoginUser, FPCRoutePartPoint wFPCRoutePartPoint, FPCRoute wFPCRoute,
			int wOrderID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("update {0}.sfc_taskstep t1,{0}.aps_taskstep t2 set t1.Active=2 "
					+ "where t1.TaskStepID=t2.ID and t2.OrderID=:wOrderID and t2.PartID=:wPartID and t2.StepID=:wStepID and t1.ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wFPCRoutePartPoint.PartID);
			wParamMap.put("wStepID", wFPCRoutePartPoint.PartPointID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 判断工位是否已转序
	 */
	public boolean JugdeIsTurnOrder(BMSEmployee wLoginUser, Integer wOrderID, int partID,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select count(*) Number from {0}.aps_taskpart "
					+ "where OrderID=:wOrderID and PartID=:wPartID and ShiftPeriod=5 and Active=1 and Status=5;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", partID);

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

	/**
	 * 判断产线单元明细是否存在此工序
	 */
	public boolean IsExistLineUnitStep(BMSEmployee wLoginUser, int lineID, int productID, int partID, int partPointID,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT count(*) Number FROM {0}.fmc_lineunit "
					+ "where LevelID=3 and LineID=:lineID and ProductID=:productID and UnitID=:partPointID and ParentUnitID=:partID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("lineID", lineID);
			wParamMap.put("productID", productID);
			wParamMap.put("partID", partID);
			wParamMap.put("partPointID", partPointID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wNumber = StringUtils.parseInt(wReader.get("Number"));
				if (wNumber > 0)
					wResult = true;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据ID获取工艺工序文件
	 */
	public String SelectStepSopUrl(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT FilePath FROM {0}.fpc_stepsop where ID=:wID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseString(wReader.get("FilePath"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断同车型、修程、工位、工序是否有当前标准
	 */
	public boolean JudgeIsHasStandard(BMSEmployee wLoginUser, int productID, int lineID, int newPartID, int partPointID,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("Select count(*) Number from {0}.ipt_standard where LineID=:wLineID "
					+ "and PartID=:wPartID and PartPointID=:wPartPointID and ProductID=:wProductID and IsCurrent=1 and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wLineID", lineID);
			wParamMap.put("wProductID", productID);
			wParamMap.put("wPartID", newPartID);
			wParamMap.put("wPartPointID", partPointID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				int wNumber = StringUtils.parseInt(wReader.get("Number"));
				if (wNumber > 0) {
					wResult = true;
					return wResult;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
