//package com.mes.qms.server.serviceimpl.dao.sfc;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.SqlParameterSource;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//
//import com.mes.qms.server.service.mesenum.APSTaskStatus;
//import com.mes.qms.server.service.mesenum.BPMEventModule;
//import com.mes.qms.server.service.mesenum.IPTMode;
//import com.mes.qms.server.service.mesenum.MESDBSource;
//import com.mes.qms.server.service.mesenum.MESException;
//import com.mes.qms.server.service.mesenum.SFCLoginType;
//import com.mes.qms.server.service.mesenum.SFCTaskMode;
//import com.mes.qms.server.service.mesenum.SFCTaskStatus;
//import com.mes.qms.server.service.mesenum.SFCTaskType;
//import com.mes.qms.server.service.po.OutResult;
//import com.mes.qms.server.service.po.ServiceResult;
//import com.mes.qms.server.service.po.aps.APSTaskStep;
//import com.mes.qms.server.service.po.bms.BMSEmployee;
//import com.mes.qms.server.service.po.ipt.IPTStandard;
//import com.mes.qms.server.service.po.sfc.SFCIPTItem;
//import com.mes.qms.server.service.po.sfc.SFCLoginEvent;
//import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
//import com.mes.qms.server.service.po.sfc.SFCTaskStep;
//import com.mes.qms.server.service.utils.StringUtils;
//import com.mes.qms.server.serviceimpl.LOCOAPSServiceImpl;
//import com.mes.qms.server.serviceimpl.dao.BaseDAO;
//import com.mes.qms.server.serviceimpl.dao.ipt.IPTStandardDAO;
//import com.mes.qms.server.serviceimpl.utils.qms.IPTUtils;
//import com.mes.qms.server.shristool.LoggerTool;
//
///**
// * 巡检操作类
// * 
// * @author ShrisJava
// *
// */
//public class SFCIPTDAO extends BaseDAO {
//	private static SFCIPTDAO Instance = null;
//
//	private SFCIPTDAO() {
//		super();
//	}
//
//	public static SFCIPTDAO getInstance() {
//		if (Instance == null)
//			Instance = new SFCIPTDAO();
//		return Instance;
//	}
//
//	// 巡检任务（质量巡检、工艺巡检）
//	// 逻辑函数
//	private int SFC_TriggerTaskIPT(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
//		wErrorCode.set(0);
//		try {
//			SFCTaskIPT wTaskTrigger = wTaskIPT.Clone();
//			wTaskTrigger.ID = 0;
//			wTaskTrigger.ModuleVersionID = 0;
//			wTaskTrigger.TaskType = wTaskIPT.TaskType;
//			if (wTaskTrigger.TaskType > 0) {
//				wTaskTrigger.EventID = wTaskIPT.EventID;
//				wTaskTrigger.Status = SFCTaskStatus.Active.getValue();
//				wTaskTrigger.FQTYGood = 0;
//				wTaskTrigger.FQTYBad = 0;
//				this.SFC_AddTaskIPT(wLoginUser, wTaskTrigger, wErrorCode);
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.Exception.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_TriggerTaskIPT", "Function Exception:" + ex.toString());
//		}
//		return wErrorCode.get();
//	}
//
//	private SFCTaskIPT SFC_CheckTaskIPT(int wCompanyID, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
//		SFCTaskIPT wTaskIPTDB = new SFCTaskIPT();
//		wErrorCode.set(0);
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wCompanyID, MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//
//				if (wTaskIPT.ID > 0) {
//					wSQLText = StringUtils.Format("Select * from {0}.sfc_taskipt", wInstance.Result)
//							+ " where ID!=:ID and TaskStepID=:TaskStepID and ShiftID=:ShiftID and Status=0 and TaskType=:TaskType";
//					wParms.clear();
//					wParms.put("ID", wTaskIPT.ID);
//					wParms.put("TaskStepID", wTaskIPT.TaskStepID);
//					wParms.put("ShiftID", wTaskIPT.ShiftID);
//					wParms.put("TaskType", wTaskIPT.TaskType);
//				} else {
//					wSQLText = StringUtils.Format("Select * from {0}.sfc_taskipt", wInstance.Result)
//							+ "  where TaskStepID=:TaskStepID and ShiftID=:ShiftID and Status=0 and TaskType=:TaskType";
//					wParms.clear();
//					wParms.put("TaskStepID", wTaskIPT.TaskStepID);
//					wParms.put("ShiftID", wTaskIPT.ShiftID);
//					wParms.put("TaskType", wTaskIPT.TaskType);
//				}
//				wSQLText = this.DMLChange(wSQLText);
//				List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
//				for (Map<String, Object> wSqlDataReader : wQueryResultList) {
//					wTaskIPTDB.ID = StringUtils.parseInt(wSqlDataReader.get("ID"));
//					wTaskIPTDB.LineID = StringUtils.parseInt(wSqlDataReader.get("LineID"));
//					wTaskIPTDB.PartID = StringUtils.parseInt(wSqlDataReader.get("PartID"));
//					wTaskIPTDB.PartPointID = StringUtils.parseInt(wSqlDataReader.get("PartPointID"));
//					wTaskIPTDB.StationID = StringUtils.parseInt(wSqlDataReader.get("StationID"));
//					wTaskIPTDB.ProductID = StringUtils.parseInt(wSqlDataReader.get("ProductID"));
//					wTaskIPTDB.TaskStepID = StringUtils.parseInt(wSqlDataReader.get("TaskStepID"));
//					wTaskIPTDB.TaskType = StringUtils.parseInt(wSqlDataReader.get("TaskType"));
//
//					wTaskIPTDB.ModuleVersionID = StringUtils.parseInt(wSqlDataReader.get("ModuleVersionID"));
//					wTaskIPTDB.OperatorID = StringUtils.parseInt(wSqlDataReader.get("OperatorID"));
//					wTaskIPTDB.ShiftID = StringUtils.parseInt(wSqlDataReader.get("ShiftID"));
//
//					wTaskIPTDB.ActiveTime = StringUtils.parseCalendar(wSqlDataReader.get("ActiveTime"));
//					wTaskIPTDB.Status = StringUtils.parseInt(wSqlDataReader.get("Status"));
//					wTaskIPTDB.SubmitTime = StringUtils.parseCalendar(wSqlDataReader.get("SubmitTime"));
//					wTaskIPTDB.Result = StringUtils.parseInt(wSqlDataReader.get("Result"));
//
//					wTaskIPTDB.TaskMode = StringUtils.parseInt(wSqlDataReader.get("TaskMode"));
//					wTaskIPTDB.Times = StringUtils.parseInt(wSqlDataReader.get("Times"));
//					wTaskIPTDB.FQTYGood = StringUtils.parseInt(wSqlDataReader.get("FQTYGood"));
//					wTaskIPTDB.FQTYBad = StringUtils.parseInt(wSqlDataReader.get("FQTYBad"));
//				}
//
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_CheckTaskIPT", "Function Exception:" + ex.toString());
//		}
//		return wTaskIPTDB;
//	}
//
//	private int SFC_SaveIPTItemList(int wCompanyID, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
//		wErrorCode.set(0);
//		int wID = 0;
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wCompanyID, MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//
//				if (wTaskIPT.ID > 0) {
//					for (SFCIPTItem wItem : wTaskIPT.ItemList) {
//						wSQLText = StringUtils.Format("Insert Into {0}.sfc_iptitem", wInstance.Result)
//								+ "(ParentID,ItemID,ItemValue) " + " Values(:ParentID,:ItemID,:ItemValue);";
//
//						wParms.clear();
//						wParms.put("ParentID", wTaskIPT.ID);
//						wParms.put("ItemID", wItem.ItemID);
//						wParms.put("ItemValue", wItem.ItemValue);
//
//						wSQLText = this.DMLChange(wSQLText);
//						KeyHolder keyHolder = new GeneratedKeyHolder();
//
//						SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParms);
//						nameJdbcTemplate.update(wSQLText, wSqlParameterSource, keyHolder);
//
//						wID = keyHolder.getKey().intValue();
//
//					}
//				}
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_SaveIPTItemList", "Function Exception:" + ex.toString());
//		}
//		return wID;
//	}
//
//	private List<SFCIPTItem> SFC_QueryIPTItemListByID(int wCompanyID, int wID, OutResult<Integer> wErrorCode) {
//		List<SFCIPTItem> wItemList = new ArrayList<SFCIPTItem>();
//		wErrorCode.set(0);
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wCompanyID, MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//
//				if (wID > 0) {
//					wSQLText = StringUtils.Format("Select * from {0}.sfc_iptitem  where ParentID=:ParentID",
//							wInstance.Result);
//
//					wParms.clear();
//					wParms.put("ParentID", wID);
//
//					wSQLText = this.DMLChange(wSQLText);
//					List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
//					for (Map<String, Object> wSqlDataReader : wQueryResultList) {
//						SFCIPTItem wItem = new SFCIPTItem();
//						wItem.ID = StringUtils.parseInt(wSqlDataReader.get("ID"));
//						wItem.ParentID = StringUtils.parseInt(wSqlDataReader.get("ParentID"));
//						wItem.ItemID = StringUtils.parseInt(wSqlDataReader.get("ItemID"));
//						wItem.ItemValue = StringUtils.parseFloat(wSqlDataReader.get("ItemValue"));
//						wItemList.add(wItem);
//					}
//				}
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_QueryIPTItemListByID", "Function Exception:" + ex.toString());
//		}
//		return wItemList;
//	}
//
//	// 接口函数
//	public List<SFCTaskIPT> SFC_QueryTaskIPTListByShiftID(BMSEmployee wLoginUser, int wTaskType, int wShiftID,
//			OutResult<Integer> wErrorCode) {
//		List<SFCTaskIPT> wTaskIPTList = new ArrayList<SFCTaskIPT>();
//		wErrorCode.set(0);
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.CompanyID, MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//
//				if (wShiftID > 0) {
//					wSQLText = StringUtils.Format(
//							"Select t.*,O.OrderNo,O.ProductNo from {0}.sfc_taskipt t,{1}.aps_taskstep s,{2}.oms_mesorder O",
//							wInstance.Result, wInstance.Result, wInstance.Result)
//							+ " where t.TaskType=:TaskType and t.ShiftID=:ShiftID and t.TaskStepID=s.ID and s.OrderID=O.ID";
//					wParms.clear();
//					wParms.put("TaskType", wTaskType);
//					wParms.put("ShiftID", wShiftID);
//
//					wSQLText = this.DMLChange(wSQLText);
//					List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
//					for (Map<String, Object> wSqlDataReader : wQueryResultList) {
//						SFCTaskIPT wTaskIPTDB = new SFCTaskIPT();
//						wTaskIPTDB.ID = StringUtils.parseInt(wSqlDataReader.get("ID"));
//						wTaskIPTDB.LineID = StringUtils.parseInt(wSqlDataReader.get("LineID"));
//						wTaskIPTDB.PartID = StringUtils.parseInt(wSqlDataReader.get("PartID"));
//						wTaskIPTDB.PartPointID = StringUtils.parseInt(wSqlDataReader.get("PartPointID"));
//						wTaskIPTDB.StationID = StringUtils.parseInt(wSqlDataReader.get("StationID"));
//						wTaskIPTDB.ProductID = StringUtils.parseInt(wSqlDataReader.get("ProductID"));
//						wTaskIPTDB.TaskStepID = StringUtils.parseInt(wSqlDataReader.get("TaskStepID"));
//						wTaskIPTDB.TaskType = StringUtils.parseInt(wSqlDataReader.get("TaskType"));
//
//						wTaskIPTDB.ModuleVersionID = StringUtils.parseInt(wSqlDataReader.get("ModuleVersionID"));
//						wTaskIPTDB.OperatorID = StringUtils.parseInt(wSqlDataReader.get("OperatorID"));
//						wTaskIPTDB.ShiftID = StringUtils.parseInt(wSqlDataReader.get("ShiftID"));
//
//						wTaskIPTDB.ActiveTime = StringUtils.parseCalendar(wSqlDataReader.get("ActiveTime"));
//						wTaskIPTDB.Status = StringUtils.parseInt(wSqlDataReader.get("Status"));
//						wTaskIPTDB.SubmitTime = StringUtils.parseCalendar(wSqlDataReader.get("SubmitTime"));
//						wTaskIPTDB.Result = StringUtils.parseInt(wSqlDataReader.get("Result"));
//
//						wTaskIPTDB.TaskMode = StringUtils.parseInt(wSqlDataReader.get("TaskMode"));
//						wTaskIPTDB.Times = StringUtils.parseInt(wSqlDataReader.get("Times"));
//						wTaskIPTDB.FQTYGood = StringUtils.parseInt(wSqlDataReader.get("FQTYGood"));
//						wTaskIPTDB.FQTYBad = StringUtils.parseInt(wSqlDataReader.get("FQTYBad"));
//						wTaskIPTDB.EventID = StringUtils.parseInt(wSqlDataReader.get("EventID"));
//						wTaskIPTDB.OrderNo = StringUtils.parseString(wSqlDataReader.get("OrderNo"));
//						wTaskIPTDB.ProductNo = StringUtils.parseString(wSqlDataReader.get("ProductNo"));
//						wTaskIPTList.add(wTaskIPTDB);
//					}
//
//				}
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_QueryTaskIPTListByShiftID",
//					"Function Exception:" + ex.toString());
//		}
//		return wTaskIPTList;
//	}
//
//	public SFCTaskIPT SFC_QueryTaskIPTByID(int wCompanyID, int wLoginID, int wID, OutResult<Integer> wErrorCode) {
//		SFCTaskIPT wTaskIPTDB = new SFCTaskIPT();
//		wErrorCode.set(0);
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wCompanyID, MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//
//				wSQLText = StringUtils.Format(
//						"Select t.*,O.OrderNo,O.ProductNo from {0}.sfc_taskipt t,{1}.aps_taskstep s,{2}.oms_mesorder O",
//						wInstance.Result, wInstance.Result, wInstance.Result)
//						+ " where t.ID=:ID and t.TaskStepID=s.ID and s.OrderID=O.ID";
//				wParms.clear();
//				wParms.put("ID", wID);
//
//				wSQLText = this.DMLChange(wSQLText);
//				List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
//				for (Map<String, Object> wSqlDataReader : wQueryResultList) {
//
//					wTaskIPTDB.ID = StringUtils.parseInt(wSqlDataReader.get("ID"));
//					wTaskIPTDB.LineID = StringUtils.parseInt(wSqlDataReader.get("LineID"));
//					wTaskIPTDB.PartID = StringUtils.parseInt(wSqlDataReader.get("PartID"));
//					wTaskIPTDB.PartPointID = StringUtils.parseInt(wSqlDataReader.get("PartPointID"));
//					wTaskIPTDB.StationID = StringUtils.parseInt(wSqlDataReader.get("StationID"));
//					wTaskIPTDB.ProductID = StringUtils.parseInt(wSqlDataReader.get("ProductID"));
//					wTaskIPTDB.TaskStepID = StringUtils.parseInt(wSqlDataReader.get("TaskStepID"));
//					wTaskIPTDB.TaskType = StringUtils.parseInt(wSqlDataReader.get("TaskType"));
//
//					wTaskIPTDB.ModuleVersionID = StringUtils.parseInt(wSqlDataReader.get("ModuleVersionID"));
//					wTaskIPTDB.OperatorID = StringUtils.parseInt(wSqlDataReader.get("OperatorID"));
//					wTaskIPTDB.ShiftID = StringUtils.parseInt(wSqlDataReader.get("ShiftID"));
//
//					wTaskIPTDB.ActiveTime = StringUtils.parseCalendar(wSqlDataReader.get("ActiveTime"));
//					wTaskIPTDB.Status = StringUtils.parseInt(wSqlDataReader.get("Status"));
//					wTaskIPTDB.SubmitTime = StringUtils.parseCalendar(wSqlDataReader.get("SubmitTime"));
//					wTaskIPTDB.Result = StringUtils.parseInt(wSqlDataReader.get("Result"));
//
//					wTaskIPTDB.TaskMode = StringUtils.parseInt(wSqlDataReader.get("TaskMode"));
//					wTaskIPTDB.Times = StringUtils.parseInt(wSqlDataReader.get("Times"));
//					wTaskIPTDB.FQTYGood = StringUtils.parseInt(wSqlDataReader.get("FQTYGood"));
//					wTaskIPTDB.FQTYBad = StringUtils.parseInt(wSqlDataReader.get("FQTYBad"));
//					wTaskIPTDB.EventID = StringUtils.parseInt(wSqlDataReader.get("EventID"));
//					wTaskIPTDB.OrderNo = StringUtils.parseString(wSqlDataReader.get("OrderNo"));
//					wTaskIPTDB.ProductNo = StringUtils.parseString(wSqlDataReader.get("ProductNo"));
//				}
//
//				if (wTaskIPTDB.ID > 0) {
//					wTaskIPTDB.ItemList = this.SFC_QueryIPTItemListByID(wCompanyID, wTaskIPTDB.ID, wErrorCode);
//					// Step01：人员姓名
////					MESEntry wEntryEmployee = MESServer.MES_QueryEntryByMemory(wCompanyID, MESEntryEnum.BMSModel);
////
////					// Step02：工厂与事业部
////					MESEntry wFactoryModel = MESServer.MES_QueryEntryByMemory(wCompanyID, MESEntryEnum.FactoryModel);
////
////					// Step03：工艺模型
////					MESEntry wRouteModel = MESServer.MES_QueryEntryByMemory(wCompanyID, MESEntryEnum.RouteModel);
//					wTaskIPTDB.TypeText = SFCTaskType.getEnumType(wTaskIPTDB.TaskType).getLable();
//					wTaskIPTDB.ModeText = SFCTaskMode.getEnumType(wTaskIPTDB.TaskMode).getLable();
////					wTaskIPTDB.OperatorName = BMSEmployeeDAO.getInstance().BMS_QueryEmployeeNameByID(wCompanyID,
////							wTaskIPTDB.OperatorID, wEntryEmployee);
////
////					wTaskIPTDB.LineName = FMCLineDAO.getInstance().FMC_QueryLineNameByID(wCompanyID, wTaskIPTDB.LineID,
////							wFactoryModel);
////					wTaskIPTDB.PartName = FPCPartDAO.getInstance().FPC_QueryPartNameByID(wCompanyID, wTaskIPTDB.PartID,
////							wRouteModel);
////					wTaskIPTDB.PartPointName = FPCPartDAO.getInstance().FPC_QueryPartPointNameByID(wCompanyID,
////							wTaskIPTDB.PartPointID, wRouteModel);
////					wTaskIPTDB.StationName = FMCStationDAO.getInstance().FMC_QueryStationNameByID(wCompanyID,
////							wTaskIPTDB.StationID, wFactoryModel);
//				}
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_QueryTaskIPTByID", "Function Exception:" + ex.toString());
//		}
//		return wTaskIPTDB;
//	}
//
//	public int SFC_AddTaskIPT(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
//		wErrorCode.set(0);
//		int wID = 0;
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
//					wLoginUser.getID(), 0);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				SFCTaskIPT wTaskIPTDB = this.SFC_CheckTaskIPT(wLoginUser.getCompanyID(), wTaskIPT, wErrorCode);
//				if (wTaskIPTDB.ID > 0 || wTaskIPT.ID > 0)
//					wErrorCode.set(MESException.Logic.getValue());
//			}
//			if (wErrorCode.Result == 0) {
//				int wShiftID = IPTUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance());
//
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//				wTaskIPT.ShiftID = wShiftID;
//				wSQLText = StringUtils.Format("Select count(*) as Times from {0}.sfc_taskipt", wInstance.Result)
//						+ " where TaskStepID=:TaskStepID and ShiftID=:ShiftID and OperatorID=:OperatorID and TaskType=:TaskType;";
//				wParms.clear();
//				wParms.put("TaskStepID", wTaskIPT.TaskStepID);
//				wParms.put("ShiftID", wTaskIPT.ShiftID);
//				wParms.put("TaskType", wTaskIPT.TaskType);
//				wParms.put("OperatorID", wLoginUser.ID);
//
//				wSQLText = this.DMLChange(wSQLText);
//				List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQLText, wParms);
//				for (Map<String, Object> wSqlDataReader : wQueryResult) {
//					wTaskIPT.Times = StringUtils.parseInt(wSqlDataReader.get("Times"));
//					wTaskIPT.Times = wTaskIPT.Times + 1;
//				}
//
//				if (wTaskIPT.Times < 1)
//					wTaskIPT.Times = 1;
//
//				wSQLText = StringUtils.Format("Insert Into {0}.sfc_taskipt", wInstance.Result)
//						+ "(LineID,PartID,PartPointID,StationID,ProductID,TaskStepID,"
//						+ " Status,OperatorID,ShiftID,Result,ActiveTime,SubmitTime,TaskType,TaskMode,ModuleVersionID,Times,FQTYGood,FQTYBad,EventID) "
//						+ " Values(:LineID,:PartID,:PartPointID,:StationID,:ProductID,:TaskStepID,:Status,:OperatorID,:ShiftID,:Result,:ActiveTime,"
//						+ " :SubmitTime,:TaskType,:TaskMode,:ModuleVersionID,:Times,:FQTYGood,:FQTYBad,:EventID);";
//
//				wParms.clear();
//				wParms.put("LineID", wTaskIPT.LineID);
//				wParms.put("PartID", wTaskIPT.PartID);
//				wParms.put("PartPointID", wTaskIPT.PartPointID);
//				wParms.put("StationID", wTaskIPT.StationID);
//				wParms.put("ProductID", wTaskIPT.ProductID);
//
//				wParms.put("TaskStepID", wTaskIPT.TaskStepID);
//				wParms.put("Status", wTaskIPT.Status);
//				wParms.put("OperatorID", wTaskIPT.OperatorID);
//				wParms.put("ShiftID", wTaskIPT.ShiftID);
//				wParms.put("Result", wTaskIPT.Result);
//				wParms.put("ActiveTime", Calendar.getInstance());
//
//				wParms.put("SubmitTime", Calendar.getInstance());
//				wParms.put("TaskType", wTaskIPT.TaskType);
//				wParms.put("TaskMode", wTaskIPT.TaskMode);
//				wParms.put("ModuleVersionID", wTaskIPT.ModuleVersionID);
//				wParms.put("Times", wTaskIPT.Times);
//
//				wParms.put("FQTYGood", wTaskIPT.FQTYGood);
//				wParms.put("FQTYBad", wTaskIPT.FQTYBad);
//				wParms.put("EventID", wTaskIPT.EventID);
//
//				wSQLText = this.DMLChange(wSQLText);
//				KeyHolder keyHolder = new GeneratedKeyHolder();
//
//				SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParms);
//				nameJdbcTemplate.update(wSQLText, wSqlParameterSource, keyHolder);
//
//				wID = keyHolder.getKey().intValue();
//
//				if (wID > 0)
//					this.SFC_SaveIPTItemList(wLoginUser.getCompanyID(), wTaskIPT, wErrorCode);
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_AddTaskIPT", "Function Exception:" + ex.toString());
//		}
//		return wID;
//	}
//
//	public int SFC_SaveTaskIPT(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
//		wErrorCode.set(0);
//		int wID = 0;
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//				wTaskIPT.OperatorID = wLoginUser.ID;
//				if (wTaskIPT.ID > 0) {
//					SFCTaskIPT wTaskIPTDB = this.SFC_QueryTaskIPTByID(wLoginUser.getCompanyID(), 0, wTaskIPT.ID,
//							wErrorCode);
//					if (wTaskIPTDB.ItemList.size() > 0 && wTaskIPT.ItemList.size() > 0) {
//						wSQLText = StringUtils.Format("delete from  {0}.sfc_iptitem", wInstance.Result)
//								+ " where ParentID=:ParentID";
//						wParms.clear();
//						wParms.put("ParentID", wTaskIPT.ID);
//						wSQLText = this.DMLChange(wSQLText);
//						nameJdbcTemplate.update(wSQLText, wParms);
//					}
//					wSQLText = StringUtils.Format("Update {0}.sfc_taskipt", wInstance.Result)
//							+ "  Set Status=:Status,OperatorID=:OperatorID,Result=:Result,SubmitTime=:SubmitTime, "
//							+ " ModuleVersionID=:ModuleVersionID,FQTYGood=:FQTYGood,FQTYBad=:FQTYBad where ID=:ID";
//					wParms.clear();
//
//					wParms.put("ID", wTaskIPT.ID);
//
//					wParms.put("Status", wTaskIPT.Status);
//					wParms.put("OperatorID", wLoginUser.ID);
//					wParms.put("Result", wTaskIPT.Result);
//					wParms.put("ModuleVersionID", wTaskIPT.ModuleVersionID);
//					wParms.put("SubmitTime", Calendar.getInstance());
//					wParms.put("FQTYGood", wTaskIPT.FQTYGood);
//					wParms.put("FQTYBad", wTaskIPT.FQTYBad);
//					wSQLText = this.DMLChange(wSQLText);
//					nameJdbcTemplate.update(wSQLText, wParms);
//					wID = wTaskIPT.ID;
//
//					this.SFC_SaveIPTItemList(wLoginUser.CompanyID, wTaskIPT, wErrorCode);
//				} else {
//					if (wTaskIPT.TaskMode == SFCTaskMode.Default.getValue())
//						wTaskIPT.TaskMode = SFCTaskMode.FreeShift.getValue();
//
//					wID = this.SFC_AddTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
//				}
//				if (wTaskIPT.Status != SFCTaskStatus.Done.getValue()) {
//					return wID;
//				}
//				// 触发任务(合格情况，自检触发互检、互检触发专检)(不合格情况，一直触发自检，直到合格为止)
//				switch (SFCTaskType.getEnumType(wTaskIPT.TaskType)) {
//				case SelfCheck:// 自检
//					if (wTaskIPT.Result == 1) {
//						wTaskIPT.TaskType = SFCTaskType.MutualCheck.getValue();
//						this.SFC_TriggerTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
//					} else {
//						wTaskIPT.TaskType = SFCTaskType.SelfCheck.getValue();
//						this.SFC_TriggerTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
//					}
//					break;
//				case MutualCheck:// 互检
//					if (wTaskIPT.Result == 1) {
//						wTaskIPT.TaskType = SFCTaskType.SpecialCheck.getValue();
//						this.SFC_TriggerTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
//						// 修改工序任务的状态为完成
//						APSTaskStep wAPSTaskStep = LOCOAPSServiceImpl.getInstance()
//								.APS_QueryTaskStepByID(wLoginUser, wTaskIPT.TaskStepID).Info(APSTaskStep.class);
//						if (wAPSTaskStep != null && wAPSTaskStep.ID > 0) {
//							wAPSTaskStep.Status = APSTaskStatus.Done.getValue();
//							LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wAPSTaskStep);
//						}
//					} else {
//						wTaskIPT.TaskType = SFCTaskType.SelfCheck.getValue();
//						this.SFC_TriggerTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
//					}
//					break;
//				default:
//					break;
//				}
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_SaveTaskIPT", "Function Exception:" + ex.toString());
//		}
//		return wID;
//	}
//
//	public List<SFCTaskIPT> SFC_QueryTaskIPTListByLoginID(BMSEmployee wLoginUser, int wEventID, boolean wIncludeSub,
//			OutResult<Integer> wErrorCode) {
//		List<SFCTaskIPT> wTaskIPTList = new ArrayList<SFCTaskIPT>();
//		wErrorCode.set(0);
//		try {
//			int wTaskType = SFCTaskType.Default.getValue();
//			switch (BPMEventModule.getEnumType(wEventID)) {
//			case SCZJ:// 自检
//				wTaskType = SFCTaskType.SelfCheck.getValue();
//				break;
//			case SCHJ:// 互检
//				wTaskType = SFCTaskType.MutualCheck.getValue();
//				break;
//			case SCZuanJ:// 专检
//				wTaskType = SFCTaskType.SpecialCheck.getValue();
//				break;
//			case SCYJ:// 预检
//				wTaskType = SFCTaskType.PreCheck.getValue();
//				break;
//			default:
//				break;
//			}
//			List<SFCTaskIPT> wTaskIPTDBList = this.SFC_QueryTaskIPTListByShiftID(wLoginUser, wTaskType,
//					IPTUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance()), wErrorCode);
//			wTaskIPTList = wTaskIPTDBList;
//		} catch (Exception ex) {
//			LoggerTool.SaveException("SFCService", "SFC_QueryTaskIPTListByLoginID",
//					"Function Exception:" + ex.toString());
//		}
//		return wTaskIPTList;
//	}
//
//	public List<SFCTaskIPT> SFC_QueryTaskIPTListByStationID(BMSEmployee wLoginUser, int wStationID, int wEventID,
//			OutResult<Integer> wErrorCode) {
//		List<SFCTaskIPT> wTaskIPTList = new ArrayList<SFCTaskIPT>();
//		wErrorCode.set(0);
//		try {
//			int wTaskType = SFCTaskType.Default.getValue();
//			switch (BPMEventModule.getEnumType(wEventID)) {
//			case SCZJ:
//				wTaskType = SFCTaskType.SelfCheck.getValue();
//				break;
//			case SCHJ:
//				wTaskType = SFCTaskType.MutualCheck.getValue();
//				break;
//			case SCZuanJ:
//				wTaskType = SFCTaskType.SpecialCheck.getValue();
//				break;
//			case SCYJ:
//				wTaskType = SFCTaskType.PreCheck.getValue();
//				break;
//			default:
//				break;
//			}
//			List<SFCTaskIPT> wTaskIPTDBList = this.SFC_QueryTaskIPTListByShiftID(wLoginUser, wTaskType,
//					IPTUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance()), wErrorCode);
////			for (SFCTaskIPT wTaskIPT : wTaskIPTDBList) {
////				boolean wIncluded = false;
////				if (wTaskIPT.StationID == wStationID) {
////					wIncluded = true;
////				}
////				if (wIncluded) {
////					wTaskIPTList.add(wTaskIPT);
////				} else {
////					if (wTaskIPT.OperatorID == wLoginUser.ID)
////						wTaskIPTList.add(wTaskIPT);
////				}
////			}
////			wTaskIPTList = this.SFC_SetTextOfTaskIPTList(wLoginUser, wTaskIPTList, wErrorCode);
//			wTaskIPTList = wTaskIPTDBList;
//		} catch (Exception ex) {
//			LoggerTool.SaveException("SFCService", "SFC_QueryTaskIPTListByStationID",
//					"Function Exception:" + ex.toString());
//		}
//		return wTaskIPTList;
//	}
//
//	// 上岗打卡
//	private List<SFCLoginEvent> SFC_QueryLoginEventListByLoginEvent(BMSEmployee wLoginUser, SFCLoginEvent wLoginEvent,
//			OutResult<Integer> wErrorCode) {
//		List<SFCLoginEvent> wLoginEventList = new ArrayList<SFCLoginEvent>();
//		wErrorCode.set(0);
//
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.CompanyID, MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				// Step0:查询
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//
//				wSQLText = StringUtils.Format("Select * from {0}.sfc_loginstation t ", wInstance.Result)
//						+ " where t.WorkShopID=:WorkShopID and t.StationID=:StationID and OperatorID=OperatorID and ShiftID=:ShiftID Order by LoginTime";
//				wParms.clear();
//				wParms.put("WorkShopID", wLoginEvent.WorkShopID);
//				wParms.put("StationID", wLoginEvent.StationID);
//				wParms.put("OperatorID", wLoginEvent.OperatorID);
//				wParms.put("ModuleID", wLoginEvent.ModuleID);
//				wParms.put("ShiftID", wLoginEvent.ShiftID);
//				wSQLText = this.DMLChange(wSQLText);
//				List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
//				for (Map<String, Object> wSqlDataReader : wQueryResultList) {
//					SFCLoginEvent wLoginEventDB = new SFCLoginEvent();
//					wLoginEventDB.ID = StringUtils.parseInt(wSqlDataReader.get("ID"));
//					wLoginEventDB.WorkShopID = StringUtils.parseInt(wSqlDataReader.get("WorkShopID"));
//					wLoginEventDB.StationID = StringUtils.parseInt(wSqlDataReader.get("StationID"));
//					wLoginEventDB.OperatorID = StringUtils.parseInt(wSqlDataReader.get("OperatorID"));
//					wLoginEventDB.ModuleID = StringUtils.parseInt(wSqlDataReader.get("ModuleID"));
//
//					wLoginEventDB.ShiftID = StringUtils.parseInt(wSqlDataReader.get("ShiftID"));
//					wLoginEventDB.Active = StringUtils.parseBoolean(wSqlDataReader.get("Active"));
//					wLoginEventDB.LoginTime = StringUtils.parseCalendar(wSqlDataReader.get("LoginTime"));
//					wLoginEventDB.Type = StringUtils.parseInt(wSqlDataReader.get("Type"));
//
//					wLoginEventList.add(wLoginEventDB);
//				}
//
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_QueryLoginEventList", "Function Exception:" + ex.toString());
//		}
//		return wLoginEventList;
//	}
//
//	private int SFC_AddLoginEvent(BMSEmployee wLoginUser, SFCLoginEvent wActiveLoginEvent,
//			List<SFCLoginEvent> wLoginEventDBList, OutResult<Integer> wErrorCode) {
//		wErrorCode.set(0);
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.CompanyID, MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//				// Step01:检查所有打卡信息判断是否可以添加新的打卡信息
//				for (SFCLoginEvent wItem : wLoginEventDBList) {
//					boolean wShutDBEvent = false;
//					if (wItem.Active) {
//						switch (SFCLoginType.getEnumType(wActiveLoginEvent.Type)) {
//						case StartWork:
//							if (wItem.Type == SFCLoginType.StartWork.getValue())
//								wShutDBEvent = true;
//							break;
//						case AfterWork:
//							if (wItem.Type == SFCLoginType.AfterWork.getValue())
//								wShutDBEvent = true;
//							break;
//						default:
//							break;
//						}
//					}
//					if (wShutDBEvent) {
//						wSQLText = StringUtils.Format("Update {0}.sfc_loginstation", wInstance.Result)
//								+ " Set Active=0 where ID=:ID";
//						wParms.clear();
//						wParms.put("ID", wItem.ID);
//						wSQLText = this.DMLChange(wSQLText);
//						nameJdbcTemplate.update(wSQLText, wParms);
//					}
//				}
//				// Step02:添加打卡记录
//				wSQLText = StringUtils.Format("Insert Into {0}.sfc_loginstation", wInstance.Result)
//						+ "(WorkShopID,StationID,OperatorID,ModuleID,ShiftID,Active,LoginTime,Type) "
//						+ " Values(:WorkShopID,:StationID,:OperatorID,:ModuleID,:ShiftID,:Active,:LoginTime,:Type)";
//				wParms.clear();
//
//				wParms.put("WorkShopID", wActiveLoginEvent.WorkShopID);
//				wParms.put("StationID", wActiveLoginEvent.StationID);
//				wParms.put("OperatorID", wActiveLoginEvent.OperatorID);
//				wParms.put("ModuleID", wActiveLoginEvent.ModuleID);
//				wParms.put("ShiftID", IPTUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance()));
//				wParms.put("Active", 1);
//				wParms.put("LoginTime", Calendar.getInstance());
//				wParms.put("Type", wActiveLoginEvent.Type);
//				wSQLText = this.DMLChange(wSQLText);
//				nameJdbcTemplate.update(wSQLText, wParms);
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_AddLoginEvent", "Function Exception:" + ex.toString());
//		}
//		return wErrorCode.get();
//	}
//
//	private int SFC_TriggerEventByLoginEvent(BMSEmployee wLoginUser, int wWorkShopID, int wStationID, int wAPPEventID,
//			OutResult<Integer> wErrorCode) {
//		wErrorCode.set(0);
//		try {
//			// Step02:触发班前点检操作
////			MESEntry wFactoryEntry = MESServer.MES_QueryEntryByMemory(wLoginUser.CompanyID, MESEntryEnum.FactoryModel);
////			if (wWorkShopID > 0) {
////				SFCTaskSpot wTaskSpot = new SFCTaskSpot();
////				if (wStationID > 0) {
////					FMCStation wStation = FMCStationDAO.getInstance().FMC_QueryStationByID(wCompanyID, wStationID,
////							wFactoryEntry);
////					wTaskSpot = new SFCTaskSpot(wStation, SFCTaskType.StationSpotCheck);
////				} else {
////					FMCWorkShop wWorkShop = FMCFactoryDAO.getInstance().FMC_QueryWorkShopByID(wCompanyID, wWorkShopID,
////							wFactoryEntry);
////					wTaskSpot = new SFCTaskSpot(wWorkShop, SFCTaskType.ProduceSpotCheck);
////				}
////				if (wTaskSpot.DeviceID > 0) {
////					wTaskSpot.EventID = wAPPEventID;
////					wTaskSpot.ShiftID = LocalUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance());
////					SFCSpotDAO.getInstance().SFC_TriggerTaskSpot(wLoginUser, wTaskSpot, wErrorCode);
////				}
////			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.Exception.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_LayoutByAPPEvent", "Function Exception:" + ex.toString());
//		}
//		return wErrorCode.get();
//	}
//
//	// 接口函数
//	public SFCLoginEvent SFC_QueryActiveLoginEventByLoginID(BMSEmployee wLoginUser, int wWorkShopID, int wStationID,
//			int wAPPEventID, OutResult<Integer> wErrorCode) {
//		SFCLoginEvent wLoginEvent = new SFCLoginEvent();
//		wErrorCode.set(0);
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.CompanyID, MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				// Step0:查询
////				int wModuleID = BPMFunctionDAO.getInstance().BPM_QueryModuleIDByEventID(wLoginUser, wAPPEventID,
////						wErrorCode);
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//
//				wSQLText = StringUtils.Format("Select * from {0}.sfc_loginstation t ", wInstance.Result)
//						+ " where t.WorkShopID=:WorkShopID and t.StationID=:StationID "
//						+ "and OperatorID=OperatorID and ShiftID=:ShiftID " + "and ModuleID=:ModuleID and Active=1";
//				wParms.clear();
//				wParms.put("WorkShopID", wLoginEvent.WorkShopID);
//				wParms.put("StationID", wLoginEvent.StationID);
//				wParms.put("OperatorID", wLoginUser.ID);
//				wParms.put("ModuleID", 0);
//				wParms.put("ShiftID", IPTUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance()));
//				wSQLText = this.DMLChange(wSQLText);
//				List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
//				for (Map<String, Object> wSqlDataReader : wQueryResultList) {
//					wLoginEvent.ID = StringUtils.parseInt(wSqlDataReader.get("ID"));
//					wLoginEvent.WorkShopID = StringUtils.parseInt(wSqlDataReader.get("WorkShopID"));
//					wLoginEvent.StationID = StringUtils.parseInt(wSqlDataReader.get("StationID"));
//					wLoginEvent.OperatorID = StringUtils.parseInt(wSqlDataReader.get("OperatorID"));
//					wLoginEvent.ModuleID = StringUtils.parseInt(wSqlDataReader.get("ModuleID"));
//
//					wLoginEvent.ShiftID = StringUtils.parseInt(wSqlDataReader.get("ShiftID"));
//					wLoginEvent.Active = StringUtils.parseBoolean(wSqlDataReader.get("Active"));
//					wLoginEvent.LoginTime = StringUtils.parseCalendar(wSqlDataReader.get("LoginTime"));
//					wLoginEvent.Type = StringUtils.parseInt(wSqlDataReader.get("Type"));
//				}
//
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_QueryActiveLoginEventByLoginID",
//					"Function Exception:" + ex.toString());
//		}
//		return wLoginEvent;
//	}
//
//	public List<SFCLoginEvent> SFC_QueryLoginEventListByLoginID(BMSEmployee wLoginUser, int wAPPEventID,
//			OutResult<Integer> wErrorCode) {
//		List<SFCLoginEvent> wLoginEventList = new ArrayList<SFCLoginEvent>();
//		wErrorCode.set(0);
//
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.CompanyID, MESDBSource.Basic);
//			wErrorCode.set(wInstance.ErrorCode);
//
//			if (wErrorCode.Result == 0) {
//				// Step0:查询
////				int wModuleID = BPMFunctionDAO.getInstance().BPM_QueryModuleIDByEventID(wLoginUser.CompanyID,
////						wAPPEventID, wErrorCode);
//				Map<String, Object> wParms = new HashMap<String, Object>();
//				String wSQLText = "";
//
//				wSQLText = StringUtils.Format("Select * from {0}.sfc_loginstation t ", wInstance.Result)
//						+ " where OperatorID=:OperatorID " + "and ShiftID=:ShiftID " + "and ModuleID=:ModuleID";
//				wParms.clear();
//				wParms.put("OperatorID", wLoginUser.ID);
//				wParms.put("ModuleID", 0);
//				wParms.put("ShiftID", IPTUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance()));
//				wSQLText = this.DMLChange(wSQLText);
//				List<Map<String, Object>> wQueryResultList = nameJdbcTemplate.queryForList(wSQLText, wParms);
//				for (Map<String, Object> wSqlDataReader : wQueryResultList) {
//					SFCLoginEvent wLoginEvent = new SFCLoginEvent();
//					wLoginEvent.ID = StringUtils.parseInt(wSqlDataReader.get("ID"));
//					wLoginEvent.WorkShopID = StringUtils.parseInt(wSqlDataReader.get("WorkShopID"));
//					wLoginEvent.StationID = StringUtils.parseInt(wSqlDataReader.get("StationID"));
//					wLoginEvent.OperatorID = StringUtils.parseInt(wSqlDataReader.get("OperatorID"));
//					wLoginEvent.ModuleID = StringUtils.parseInt(wSqlDataReader.get("ModuleID"));
//
//					wLoginEvent.ShiftID = StringUtils.parseInt(wSqlDataReader.get("ShiftID"));
//					wLoginEvent.Active = StringUtils.parseBoolean(wSqlDataReader.get("Active"));
//					wLoginEvent.LoginTime = StringUtils.parseCalendar(wSqlDataReader.get("LoginTime"));
//					wLoginEvent.Type = StringUtils.parseInt(wSqlDataReader.get("Type"));
//					wLoginEventList.add(wLoginEvent);
//				}
//
////				wLoginEventList = this.SFC_SetTextOfLoginEventList(wLoginUser.CompanyID, wLoginEventList, wErrorCode);
//			}
//		} catch (Exception ex) {
//			wErrorCode.set(MESException.DBSQL.getValue());
//			LoggerTool.SaveException("SFCService", "SFC_QueryLoginEventByLoginID",
//					"Function Exception:" + ex.toString());
//		}
//		return wLoginEventList;
//	}
//
//	// 打卡(派工任务打卡)
//	public int SFC_LoginByAPPEvent(BMSEmployee wLoginUser, int wType, List<SFCTaskStep> wSFCTaskStepList,
//			int wAPPEventID, OutResult<Integer> wErrorCode) {
//		wErrorCode.set(0);
//		int wMessageCode = 0;
//		try {
//			if (wSFCTaskStepList == null || wSFCTaskStepList.size() <= 0)
//				return wMessageCode;
//			int wShiftID = IPTUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance());
//
//			ServiceResult<IPTStandard> wRst = null;
//			for (SFCTaskStep wSFCTaskStep : wSFCTaskStepList) {
//				this.SFC_ClockTask(wLoginUser, wType, wErrorCode, wShiftID, wSFCTaskStep);
//				if (wErrorCode.Result > 0) {
//					continue;
//				}
//
//				int wSFCTaskType = 0;
//				// 自检
//				wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.QTXJ, -1, -1, -1,
//						-1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
//						wSFCTaskStep.ProductID, wErrorCode);
//				if (wRst != null && wRst.Result != null && wRst.Result.ID > 0) {
//					wSFCTaskType = SFCTaskType.SelfCheck.getValue();
//					this.SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep, wSFCTaskType, 0);
//				}
//				// 预检
//				wRst = IPTStandardDAO.getInstance().SelectIPTStandardCurrent(wLoginUser, 0, IPTMode.PreCheck, -1, -1,
//						-1, -1, -1, wSFCTaskStep.LineID, wSFCTaskStep.PartID, wSFCTaskStep.StepID, -1,
//						wSFCTaskStep.ProductID, wErrorCode);
//				if (wRst != null && wRst.Result != null && wRst.Result.ID > 0) {
//					wSFCTaskType = SFCTaskType.PreCheck.getValue();
//					this.SFC_CreateTask(wLoginUser, wAPPEventID, wErrorCode, wShiftID, wSFCTaskStep, wSFCTaskType,
//							wRst.Result.ID);
//				}
//			}
//		} catch (Exception ex) {
//			LoggerTool.SaveException("SFCService", "SFC_LoginByAPPEvent", "Function Exception:" + ex.toString());
//		}
//		return wMessageCode;
//	}
//
//	private void SFC_CreateTask(BMSEmployee wLoginUser, int wAPPEventID, OutResult<Integer> wErrorCode, int wShiftID,
//			SFCTaskStep wSFCTaskStep, int wSFCTaskType, long wStandardID) {
//		try {
//			List<SFCTaskIPT> wList;
//			SFCTaskIPT wTaskIPT;
//			// ①查询该派工任务是否已触发了任务
//			wList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wSFCTaskStep.TaskStepID, wSFCTaskType, -1,
//					wShiftID, -1, null, wErrorCode);
//			// 触发自检任务
//			if (wList.size() <= 0) {
//				wTaskIPT = new SFCTaskIPT();
//				wTaskIPT.ActiveTime = Calendar.getInstance();
//				wTaskIPT.EventID = wAPPEventID;
//				wTaskIPT.FQTYBad = 0;
//				wTaskIPT.FQTYGood = 0;
//				wTaskIPT.ID = 0;
//				wTaskIPT.ItemList = new ArrayList<SFCIPTItem>();
//				wTaskIPT.LineID = wSFCTaskStep.LineID;
//				wTaskIPT.ModuleVersionID = 0;
//				wTaskIPT.OperatorID = wSFCTaskStep.OperatorID;
//				wTaskIPT.OrderNo = wSFCTaskStep.OrderNo;
//				wTaskIPT.ProductID = wSFCTaskStep.ProductID;
//				wTaskIPT.PartPointID = wSFCTaskStep.StepID;
//				wTaskIPT.StationID = wSFCTaskStep.PartID;
//				wTaskIPT.Result = 0;
//				wTaskIPT.ShiftID = wShiftID;
//				wTaskIPT.Status = SFCTaskStatus.Active.getValue();
//				wTaskIPT.SubmitTime = Calendar.getInstance();
//				wTaskIPT.TaskMode = SFCTaskMode.FreeShift.getValue();
//				wTaskIPT.TaskStepID = wSFCTaskStep.TaskStepID;
//				wTaskIPT.TaskType = wSFCTaskType;
//				wTaskIPT.Times = 1;
//				wTaskIPT.WorkShopID = 0;
//				wTaskIPT.ModuleVersionID = (int) wStandardID;
//				SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wTaskIPT, wErrorCode);
//			}
//		} catch (Exception ex) {
//			LoggerTool.SaveException("SFCIPTDAO", "CreateTask", "Function Exception:" + ex.toString());
//		}
//	}
//
//	// 完工打卡
//	public int SFC_LayoutByAPPEvent(BMSEmployee wLoginUser, int wWorkShopID, int wStationID, int wAPPEventID,
//			OutResult<Integer> wErrorCode) {
//		wErrorCode.set(0);
//		int wMessageCode = 0;
//		try {
//			SFCLoginEvent wLoginEvent = new SFCLoginEvent(wLoginUser.ID, wWorkShopID, wStationID, 0,
//					SFCLoginType.AfterWork.getValue());
//			wLoginEvent.ShiftID = IPTUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance());
//			// Step02:判断是否可以增加新的打卡记录
//			boolean wRepeatLayout = false;
//			boolean wLayouted = false; // 是否已
//			List<SFCLoginEvent> wLoginEventDBList = this.SFC_QueryLoginEventListByLoginEvent(wLoginUser, wLoginEvent,
//					wErrorCode);
//			for (SFCLoginEvent wItem : wLoginEventDBList) {
//				if (wItem.Type == SFCLoginType.AfterWork.getValue()) {
//					wLayouted = true;
//					if (wItem.Active) {
//						wRepeatLayout = true;
//						break;
//					}
//				}
//			}
//			if (!wRepeatLayout) {
//				this.SFC_AddLoginEvent(wLoginUser, wLoginEvent, wLoginEventDBList, wErrorCode);
//
//				if (!wLayouted && wErrorCode.Result == 0) {
//					this.SFC_TriggerEventByLoginEvent(wLoginUser, wWorkShopID, wStationID, wAPPEventID, wErrorCode);
//				}
//			}
//		} catch (Exception ex) {
//			LoggerTool.SaveException("SFCService", "SFC_LayoutByAPPEvent", "Function Exception:" + ex.toString());
//		}
//		return wMessageCode;
//	}
//
//	/**
//	 * 工序任务打卡
//	 */
//	private void SFC_ClockTask(BMSEmployee wLoginUser, int wType, OutResult<Integer> wErrorCode, int wShiftID,
//			SFCTaskStep wSFCTaskStep) {
//		try {
//			List<SFCLoginEvent> wList = SFCLoginEventDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, wLoginUser.ID,
//					-1, wShiftID, 1, -1, wSFCTaskStep.TaskStepID, wErrorCode);
//			SFCLoginEvent wEndEvent = null;
//			if (wList != null && wList.size() > 0) {
//				// 排序
//				wList.sort(Comparator.comparing(SFCLoginEvent::getLoginTime));
//				wEndEvent = wList.get(wList.size() - 1);
//			}
//			switch (SFCLoginType.getEnumType(wType)) {
//			case StartWork:
//				if (wEndEvent == null) {
//					SFCLoginEvent wSFCLoginEvent = new SFCLoginEvent();
//					wSFCLoginEvent.ID = 0;
//					wSFCLoginEvent.WorkShopID = 0;
//					wSFCLoginEvent.StationID = 0;
//					wSFCLoginEvent.OperatorID = wLoginUser.ID;
//					wSFCLoginEvent.ModuleID = 0;
//					wSFCLoginEvent.ShiftID = wShiftID;
//					wSFCLoginEvent.SFCTaskStepID = wSFCTaskStep.ID;
//					wSFCLoginEvent.Active = true;
//					wSFCLoginEvent.LoginTime = Calendar.getInstance();
//					wSFCLoginEvent.Type = SFCLoginType.StartWork.getValue();
//					SFCLoginEventDAO.getInstance().Update(wLoginUser, wSFCLoginEvent, wErrorCode);
//					wSFCTaskStep.IsStartWork = 1;
//					LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
//				} else if (wEndEvent.Type == SFCLoginType.AfterWork.getValue()) {
//					SFCLoginEvent wSFCLoginEvent = new SFCLoginEvent();
//					wSFCLoginEvent.ID = 0;
//					wSFCLoginEvent.WorkShopID = 0;
//					wSFCLoginEvent.StationID = 0;
//					wSFCLoginEvent.OperatorID = wLoginUser.ID;
//					wSFCLoginEvent.ModuleID = 0;
//					wSFCLoginEvent.ShiftID = wShiftID;
//					wSFCLoginEvent.Active = true;
//					wSFCLoginEvent.SFCTaskStepID = wSFCTaskStep.ID;
//					wSFCLoginEvent.LoginTime = Calendar.getInstance();
//					wSFCLoginEvent.Type = SFCLoginType.StartWork.getValue();
//					SFCLoginEventDAO.getInstance().Update(wLoginUser, wSFCLoginEvent, wErrorCode);
//					wSFCTaskStep.IsStartWork = 1;
//					LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
//				} else {
//					wErrorCode.set(MESException.Logic.getValue());
//				}
//				break;
//			case AfterWork:
//				if (wEndEvent != null && wEndEvent.Type == SFCLoginType.StartWork.getValue()) {
//					SFCLoginEvent wSFCLoginEvent = new SFCLoginEvent();
//					wSFCLoginEvent.ID = 0;
//					wSFCLoginEvent.WorkShopID = 0;
//					wSFCLoginEvent.StationID = 0;
//					wSFCLoginEvent.OperatorID = wLoginUser.ID;
//					wSFCLoginEvent.ModuleID = 0;
//					wSFCLoginEvent.ShiftID = wShiftID;
//					wSFCLoginEvent.Active = true;
//					wSFCLoginEvent.SFCTaskStepID = wSFCTaskStep.ID;
//					wSFCLoginEvent.LoginTime = Calendar.getInstance();
//					wSFCLoginEvent.Type = SFCLoginType.AfterWork.getValue();
//					SFCLoginEventDAO.getInstance().Update(wLoginUser, wSFCLoginEvent, wErrorCode);
//					wSFCTaskStep.IsStartWork = 2;
//					LOCOAPSServiceImpl.getInstance().SFC_UpdateTaskStep(wLoginUser, wSFCTaskStep);
//				} else {
//					wErrorCode.set(MESException.Logic.getValue());
//				}
//				break;
//			default:
//				break;
//			}
//			// 将工序任务的状态变为开工
//			APSTaskStep wAPSTaskStep = LOCOAPSServiceImpl.getInstance()
//					.APS_QueryTaskStepByID(wLoginUser, wSFCTaskStep.TaskStepID).Info(APSTaskStep.class);
//			if (wAPSTaskStep != null && wAPSTaskStep.ID > 0 && wAPSTaskStep.Status == APSTaskStatus.Issued.getValue()) {
//				wAPSTaskStep.Status = APSTaskStatus.Started.getValue();
//				LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wAPSTaskStep);
//			}
//		} catch (Exception ex) {
//			LoggerTool.SaveException("SFCService", "ClockTask", "Function Exception:" + ex.toString());
//		}
//	}
//}
