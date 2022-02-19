package com.mes.qms.server.serviceimpl.dao.sfc;

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.SFCSequentialInfoType;
import com.mes.qms.server.service.mesenum.SFCTaskMode;
import com.mes.qms.server.service.mesenum.SFCTaskStatus;
import com.mes.qms.server.service.mesenum.SFCTaskType;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.andon.AndonLocomotiveProductionStatus;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.focas.FocasReport;
import com.mes.qms.server.service.po.fpc.FPCPartPoint;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.mss.MSSMaterial;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.qms.server.service.po.sfc.SFCIPTItem;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTInfo;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTPart;
import com.mes.qms.server.service.po.sfc.SFCTaskIPTPartNo;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress01;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress02;
import com.mes.qms.server.service.po.sfc.SFCTrainProgress03;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.LOCOAPSServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.rsm.RSMTurnOrderTaskDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTaskIPTDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import com.mes.qms.server.shristool.LoggerTool;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class SFCTaskIPTDAO extends BaseDAO {
	private static Logger logger = LoggerFactory.getLogger(SFCTaskIPTDAO.class);

	private static SFCTaskIPTDAO Instance = null;

	public static SFCTaskIPTDAO getInstance() {
		if (Instance == null)
			Instance = new SFCTaskIPTDAO();
		return Instance;
	}

	public int SFC_SaveTaskIPT(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wSFCTaskIPT == null) {
				return 0;
			}
			String wSQL = "";
			if (wSFCTaskIPT.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.sfc_taskipt(LineID,PartID,PartPointID,StationID,ProductID,TaskStepID,"
								+ "TaskType,ModuleVersionID,OperatorID,ShiftID,ActiveTime,"
								+ "Status,SubmitTime,Result,TaskMode,Times,FQTYGood,FQTYBad,"
								+ "EventID,OrderID,PartNo,OrderNo,Type,"
								+ "StartTime,EndTime,OperatorList,PeriodChangeStandard,Active,PicUri,Remark,CheckerList) "
								+ "VALUES(:LineID,:PartID,:PartPointID,:StationID,:ProductID,"
								+ ":TaskStepID,:TaskType,:ModuleVersionID,:OperatorID,"
								+ ":ShiftID,:ActiveTime,:Status,:SubmitTime,:Result,:TaskMode,"
								+ ":Times,:FQTYGood,:FQTYBad,:EventID,:OrderID,:PartNo,:OrderNo,"
								+ ":Type,:StartTime,:EndTime,:OperatorList,:PeriodChangeStandard,:Active,:PicUri,:Remark,:CheckerList);",
						new Object[] { wInstance.Result });
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.sfc_taskipt SET LineID = :LineID,PartID = :PartID,"
						+ "PartPointID = :PartPointID,StationID = :StationID,"
						+ "TaskStepID = :TaskStepID,ModuleVersionID = :ModuleVersionID,"
						+ "OperatorID = :OperatorID,ActiveTime = :ActiveTime,Status = :Status,"
						+ "SubmitTime = :SubmitTime,Result = :Result,TaskMode = :TaskMode,"
						+ "Times = :Times,FQTYGood = :FQTYGood,FQTYBad = :FQTYBad,Type=:Type,"
						+ "StartTime=:StartTime,EndTime=:EndTime,OperatorList=:OperatorList,"
						+ "PeriodChangeStandard=:PeriodChangeStandard,"
						+ "Active=:Active,PicUri=:PicUri,Remark=:Remark,CheckerList=:CheckerList WHERE ID = :ID;",
						new Object[] {

								wInstance.Result });
			}
			wSQL = DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("ID", Integer.valueOf(wSFCTaskIPT.ID));
			wParamMap.put("LineID", Integer.valueOf(wSFCTaskIPT.LineID));
			wParamMap.put("PartID", Integer.valueOf(wSFCTaskIPT.PartID));
			wParamMap.put("PartPointID", Integer.valueOf(wSFCTaskIPT.PartPointID));
			wParamMap.put("StationID", Integer.valueOf(wSFCTaskIPT.StationID));
			wParamMap.put("ProductID", Integer.valueOf(wSFCTaskIPT.ProductID));
			wParamMap.put("TaskStepID", Integer.valueOf(wSFCTaskIPT.TaskStepID));
			wParamMap.put("TaskType", Integer.valueOf(wSFCTaskIPT.TaskType));
			wParamMap.put("ModuleVersionID", Integer.valueOf(wSFCTaskIPT.ModuleVersionID));
			wParamMap.put("OperatorID", Integer.valueOf(wSFCTaskIPT.OperatorID));
			wParamMap.put("ShiftID", Integer.valueOf(wSFCTaskIPT.ShiftID));
			wParamMap.put("ActiveTime", wSFCTaskIPT.ActiveTime);
			wParamMap.put("Status", Integer.valueOf(wSFCTaskIPT.Status));
			wParamMap.put("SubmitTime", wSFCTaskIPT.SubmitTime);
			wParamMap.put("Result", Integer.valueOf(wSFCTaskIPT.Result));
			wParamMap.put("TaskMode", Integer.valueOf(wSFCTaskIPT.TaskMode));
			wParamMap.put("Times", Integer.valueOf(wSFCTaskIPT.Times));
			wParamMap.put("FQTYGood", Integer.valueOf(wSFCTaskIPT.FQTYGood));
			wParamMap.put("FQTYBad", Integer.valueOf(wSFCTaskIPT.FQTYBad));
			wParamMap.put("EventID", Integer.valueOf(wSFCTaskIPT.EventID));
			wParamMap.put("OrderID", Integer.valueOf(wSFCTaskIPT.OrderID));
			wParamMap.put("PartNo", wSFCTaskIPT.PartNo);
			wParamMap.put("OrderNo", wSFCTaskIPT.OrderNo);
			wParamMap.put("Type", Integer.valueOf(wSFCTaskIPT.Type));
			wParamMap.put("StartTime", wSFCTaskIPT.StartTime);
			wParamMap.put("EndTime", wSFCTaskIPT.EndTime);
			wParamMap.put("OperatorList", StringUtils.Join(",", wSFCTaskIPT.OperatorList));
			wParamMap.put("PeriodChangeStandard", wSFCTaskIPT.PeriodChangeStandard);
			wParamMap.put("Active", wSFCTaskIPT.Active);
			wParamMap.put("PicUri", wSFCTaskIPT.PicUri);
			wParamMap.put("Remark", wSFCTaskIPT.Remark);
			wParamMap.put("CheckerList", wSFCTaskIPT.CheckerList);

			GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
			MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(wParamMap);

			this.nameJdbcTemplate.update(wSQL, (SqlParameterSource) mapSqlParameterSource,
					(KeyHolder) generatedKeyHolder);

			if (wSFCTaskIPT.getID() <= 0) {
				wResult = generatedKeyHolder.getKey().intValue();
				wSFCTaskIPT.setID(wResult);
			} else {
				wResult = wSFCTaskIPT.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public SFCTaskIPT SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCTaskIPT wResult = new SFCTaskIPT();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			List<SFCTaskIPT> wList = SelectList(wLoginUser, wID, -1, -1, -1, -1, -1, null, -1, null, null, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);

			// 操作人赋值
			wResult.OperatorListNames = GetUserNames(wResult.OperatorList);
		} catch (Exception e) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<SFCTaskIPT> SelectList(BMSEmployee wLoginUser, int wID, int wTaskStepID, int wTaskType, int wOperatorID,
			int wShiftID, int wEventID, List<Integer> wStateIDList, int wType, Calendar wStartTime, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			if (wStateIDList == null) {
				wStateIDList = new ArrayList<>();
			}
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.sfc_taskipt WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wTaskStepID <= 0 or :wTaskStepID = TaskStepID ) "
					+ "and ( :wTaskType <= 0 or :wTaskType = TaskType ) "
					+ "and ( :wOperatorID <= 0 or :wOperatorID = OperatorID ) "
					+ "and ( :wShiftID <= 0 or :wShiftID = ShiftID ) " + "and ( :wType <= 0 or :wType = Type ) "
					+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1})) "

					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  SubmitTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  SubmitTime ) "
					+ "and ( :wEventID <= 0 or :wEventID = EventID );",
					new Object[] {

							wInstance.Result, (wStateIDList.size() > 0) ? StringUtils.Join(",", wStateIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wID", Integer.valueOf(wID));
			wParamMap.put("wTaskStepID", Integer.valueOf(wTaskStepID));
			wParamMap.put("wTaskType", Integer.valueOf(wTaskType));
			wParamMap.put("wOperatorID", Integer.valueOf(wOperatorID));
			wParamMap.put("wShiftID", Integer.valueOf(wShiftID));
			wParamMap.put("wEventID", Integer.valueOf(wEventID));
			wParamMap.put("wType", Integer.valueOf(wType));
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<SFCTaskIPT> SelectListByTaskStepIDList(BMSEmployee wLoginUser, List<Integer> wTaskStepIDList, int wType,
			OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			if (wTaskStepIDList == null) {
				wTaskStepIDList = new ArrayList<>();
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.sfc_taskipt WHERE  1=1  " + "and ( :wType <= 0 or :wType = Type ) "
							+ "and ( :wTaskStepIDList is null or :wTaskStepIDList = '''' or TaskStepID in ({1}));",
					new Object[] { wInstance.Result,
							(wTaskStepIDList.size() > 0) ? StringUtils.Join(",", wTaskStepIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wType", Integer.valueOf(wType));
			wParamMap.put("wTaskStepIDList", StringUtils.Join(",", wTaskStepIDList));

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
	 * 赋值结果
	 * 
	 * @param wResultList
	 * @param wQueryResult
	 */
	private void SetValue(List<SFCTaskIPT> wResultList, List<Map<String, Object>> wQueryResult) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				SFCTaskIPT wItem = new SFCTaskIPT();

				wItem.ID = StringUtils.parseInt(wReader.get("ID")).intValue();
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID")).intValue();
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID")).intValue();
				wItem.PartPointID = StringUtils.parseInt(wReader.get("PartPointID")).intValue();
				wItem.StationID = StringUtils.parseInt(wReader.get("StationID")).intValue();
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID")).intValue();
				wItem.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID")).intValue();
				wItem.TaskType = StringUtils.parseInt(wReader.get("TaskType")).intValue();
				wItem.ModuleVersionID = StringUtils.parseInt(wReader.get("ModuleVersionID")).intValue();
				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID")).intValue();
				wItem.ShiftID = StringUtils.parseInt(wReader.get("ShiftID")).intValue();
				wItem.ActiveTime = StringUtils.parseCalendar(wReader.get("ActiveTime"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status")).intValue();
				wItem.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wItem.StartTime = StringUtils.parseCalendar(wReader.get("StartTime"));
				wItem.EndTime = StringUtils.parseCalendar(wReader.get("EndTime"));
				wItem.Result = StringUtils.parseInt(wReader.get("Result")).intValue();
				wItem.TaskMode = StringUtils.parseInt(wReader.get("TaskMode")).intValue();
				wItem.Times = StringUtils.parseInt(wReader.get("Times")).intValue();
				wItem.FQTYGood = StringUtils.parseInt(wReader.get("FQTYGood")).intValue();
				wItem.FQTYBad = StringUtils.parseInt(wReader.get("FQTYBad")).intValue();
				wItem.EventID = StringUtils.parseInt(wReader.get("EventID")).intValue();
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID")).intValue();
				wItem.Active = StringUtils.parseInt(wReader.get("Active")).intValue();

				wItem.PicUri = StringUtils.parseString(wReader.get("PicUri"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.CheckerList = StringUtils.parseString(wReader.get("CheckerList"));

				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.OrderNo = StringUtils.parseString(wReader.get("OrderNo"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type")).intValue();
				wItem.PeriodChangeStandard = StringUtils.parseInt(wReader.get("PeriodChangeStandard")).intValue();
				wItem.OperatorList = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("OperatorList")).split(",|;"));
				wItem.OperatorListNames = GetNames(wItem.OperatorList);

				wItem.OperatorName = QMSConstants.GetBMSEmployeeName(wItem.OperatorID);
				wItem.WorkShopName = "";
				wItem.LineName = QMSConstants.GetFMCLineName(wItem.LineID);
				wItem.PartName = "";
				wItem.PartPointName = QMSConstants.GetFPCStepName(wItem.PartPointID);
				wItem.StationName = QMSConstants.GetFPCPartName(wItem.StationID);

				wItem.ProductNo = QMSConstants.GetFPCProductNo(wItem.ProductID);
				wItem.StatusText = SFCTaskStatus.getEnumType(wItem.Status).getLable();
				wItem.TypeText = SFCTaskType.getEnumType(wItem.TaskType).getLable();
				wItem.ModeText = SFCTaskMode.getEnumType(wItem.TaskMode).getLable();

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取名称
	 */
	private String GetNames(List<Integer> wIDList) {
		String wResult = "";
		try {
			List<String> wNames = new ArrayList<String>();
			for (int wUserID : wIDList) {
				String wName = QMSConstants.GetBMSEmployeeName(wUserID);
				if (StringUtils.isNotEmpty(wName)) {
					wNames.add(wName);
				}
			}
			wResult = StringUtils.Join(",", wNames);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<SFCTaskIPT> SelectListByTime(BMSEmployee wLoginUser, int wTaskType, int wType, String wPartNo,
			Calendar wStartTime, Calendar wEndTime, int wOrderID, OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}
			if (wStartTime.compareTo(wEndTime) >= 0) {
				return wResultList;
			}
			String wSQL = StringUtils.Format("SELECT t.* FROM {0}.sfc_taskipt  t WHERE 1=1 "
					+ "and ( :wTaskType <= 0 or :wTaskType = TaskType )  " + "and ( :wType <= 0 or :wType = Type ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
					+ "and ( :wPartNo is null or :wPartNo = '''' or PartNo LIKE ''%{1}%'') "
					+ "and ((:wStartTime <=  t.EndTime and  :wEndTime >=  t.ActiveTime and t.Status=:wStatus )  "
					+ "or  (t.Status!=:wStatus  and  :wEndTime >=  t.ActiveTime));", wInstance.Result, wPartNo);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wTaskType", Integer.valueOf(wTaskType));
			wParamMap.put("wType", Integer.valueOf(wType));
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wStatus", Integer.valueOf(SFCTaskStatus.Done.getValue()));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<SFCTaskIPT> SelectListByOrderID(BMSEmployee wLoginUser, int wOrderID, int wPartID, int wTaskType,
			int wLineID, int wStepID, int wProductID, String wPartNo, Calendar wStartTime, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			String wSQL = StringUtils.Format("select * from {0}.sfc_taskipt where 1=1 "
					+ "and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
					+ "and ( :wPartID <= 0 or :wPartID = StationID ) " + "and ( :wLineID <= 0 or :wLineID = LineID ) "
					+ "and ( :wStepID <= 0 or :wStepID = PartPointID ) "
					+ "and ( :wProductID <= 0 or :wProductID = ProductID ) "
					+ "and ( :wPartNo = '''' or :wPartNo = PartNo ) "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  ActiveTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  ActiveTime ) "
					+ "and ( :wTaskType <= 0 or :wTaskType = TaskType ) ",
					new Object[] {

							wInstance.Result });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wOrderID", Integer.valueOf(wOrderID));
			wParamMap.put("wPartID", Integer.valueOf(wPartID));
			wParamMap.put("wTaskType", Integer.valueOf(wTaskType));
			wParamMap.put("wLineID", Integer.valueOf(wLineID));
			wParamMap.put("wStepID", Integer.valueOf(wStepID));
			wParamMap.put("wProductID", Integer.valueOf(wProductID));
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<SFCTaskIPT> SelectListByOrderID(BMSEmployee wLoginUser, int wOrderID, List<Integer> wPartIDList,
			int wTaskType, int wLineID, int wStepID, int wProductID, String wPartNo, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			if (wPartIDList == null)
				wPartIDList = new ArrayList<Integer>();

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			String wSQL = StringUtils.Format("select * from {0}.sfc_taskipt where 1=1 "
					+ "and ( :wOrderID <= 0 or :wOrderID = OrderID ) " + "and ( :wLineID <= 0 or :wLineID = LineID ) "
					+ "and ( :wStepID <= 0 or :wStepID = PartPointID ) "
					+ "and ( :wProductID <= 0 or :wProductID = ProductID ) "
					+ "and ( :wPartNo = '''' or :wPartNo = PartNo ) "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  ActiveTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  ActiveTime ) "
					+ "and ( :wPart is null or :wPart = '''' or StationID in ({1})) "
					+ "and ( :wTaskType <= 0 or :wTaskType = TaskType ) ",
					new Object[] { wInstance.Result,
							(wPartIDList.size() > 0) ? StringUtils.Join(",", wPartIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wOrderID", Integer.valueOf(wOrderID));
			wParamMap.put("wPart", StringUtils.Join(",", wPartIDList));
			wParamMap.put("wTaskType", Integer.valueOf(wTaskType));
			wParamMap.put("wLineID", Integer.valueOf(wLineID));
			wParamMap.put("wStepID", Integer.valueOf(wStepID));
			wParamMap.put("wProductID", Integer.valueOf(wProductID));
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public int SFC_AddTaskIPT(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
		wErrorCode.set(Integer.valueOf(0));
		int wID = 0;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));

			if (((Integer) wErrorCode.Result).intValue() == 0) {
				SFCTaskIPT wTaskIPTDB = SFC_CheckTaskIPT(wLoginUser.getCompanyID(), wTaskIPT, wErrorCode);
				if (wTaskIPTDB.ID > 0 || wTaskIPT.ID > 0)
					wErrorCode.set(Integer.valueOf(MESException.Logic.getValue()));
			}
			if (((Integer) wErrorCode.Result).intValue() == 0) {
				int wShiftID = QMSUtils.getInstance().GetDayShiftID(wLoginUser, Calendar.getInstance());

				Map<String, Object> wParms = new HashMap<>();
				String wSQLText = "";
				wTaskIPT.ShiftID = wShiftID;
				wSQLText = String
						.valueOf(StringUtils.Format("Select count(*) as Times from {0}.sfc_taskipt",
								new Object[] { wInstance.Result }))
						+ " where TaskStepID=:TaskStepID and ShiftID=:ShiftID and OperatorID=:OperatorID and TaskType=:TaskType;";
				wParms.clear();
				wParms.put("TaskStepID", Integer.valueOf(wTaskIPT.TaskStepID));
				wParms.put("ShiftID", Integer.valueOf(wTaskIPT.ShiftID));
				wParms.put("TaskType", Integer.valueOf(wTaskIPT.TaskType));
				wParms.put("OperatorID", Integer.valueOf(wLoginUser.ID));

				wSQLText = DMLChange(wSQLText);
				List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQLText, wParms);
				for (Map<String, Object> wSqlDataReader : wQueryResult) {
					wTaskIPT.Times = StringUtils.parseInt(wSqlDataReader.get("Times")).intValue();
					wTaskIPT.Times++;
				}

				if (wTaskIPT.Times < 1) {
					wTaskIPT.Times = 1;
				}
				wSQLText = String
						.valueOf(StringUtils.Format("Insert Into {0}.sfc_taskipt", new Object[] { wInstance.Result }))
						+ "(LineID,PartID,PartPointID,StationID,ProductID,TaskStepID,"
						+ " Status,OperatorID,ShiftID,Result,ActiveTime,SubmitTime,TaskType,TaskMode,ModuleVersionID,"
						+ "Times,FQTYGood,FQTYBad,EventID,OrderID,PartNo,OrderNo,Type,"
						+ "StartTime,EndTime,OperatorList,PeriodChangeStandard,Active,PicUri,Remark,CheckerList) "
						+ " Values(:LineID,:PartID,:PartPointID,:StationID,:ProductID,:TaskStepID,:Status,"
						+ ":OperatorID,:ShiftID,:Result,:ActiveTime,"
						+ " :SubmitTime,:TaskType,:TaskMode,:ModuleVersionID,:Times,:FQTYGood,:FQTYBad,"
						+ ":EventID,:OrderID,:PartNo,:OrderNo,:Type,:StartTime,:EndTime,"
						+ ":OperatorList,:PeriodChangeStandard,:Active,:PicUri,:Remark,:CheckerList);";

				wParms.clear();
				wParms.put("LineID", Integer.valueOf(wTaskIPT.LineID));
				wParms.put("PartID", Integer.valueOf(wTaskIPT.PartID));
				wParms.put("PartPointID", Integer.valueOf(wTaskIPT.PartPointID));
				wParms.put("StationID", Integer.valueOf(wTaskIPT.StationID));
				wParms.put("ProductID", Integer.valueOf(wTaskIPT.ProductID));

				wParms.put("TaskStepID", Integer.valueOf(wTaskIPT.TaskStepID));
				wParms.put("Status", Integer.valueOf(wTaskIPT.Status));
				wParms.put("OperatorID", Integer.valueOf(wTaskIPT.OperatorID));
				wParms.put("ShiftID", Integer.valueOf(wTaskIPT.ShiftID));
				wParms.put("Result", Integer.valueOf(wTaskIPT.Result));
				wParms.put("ActiveTime", Calendar.getInstance());

				wParms.put("SubmitTime", Calendar.getInstance());
				wParms.put("TaskType", Integer.valueOf(wTaskIPT.TaskType));
				wParms.put("TaskMode", Integer.valueOf(wTaskIPT.TaskMode));
				wParms.put("ModuleVersionID", Integer.valueOf(wTaskIPT.ModuleVersionID));
				wParms.put("Times", Integer.valueOf(wTaskIPT.Times));

				wParms.put("FQTYGood", Integer.valueOf(wTaskIPT.FQTYGood));
				wParms.put("FQTYBad", Integer.valueOf(wTaskIPT.FQTYBad));
				wParms.put("EventID", Integer.valueOf(wTaskIPT.EventID));
				wParms.put("OrderID", Integer.valueOf(wTaskIPT.OrderID));
				wParms.put("PartNo", wTaskIPT.PartNo);
				wParms.put("OrderNo", wTaskIPT.OrderNo);
				wParms.put("Type", Integer.valueOf(wTaskIPT.Type));
				wParms.put("StartTime", wTaskIPT.StartTime);
				wParms.put("EndTime", wTaskIPT.EndTime);
				wParms.put("OperatorList", StringUtils.Join(",", wTaskIPT.OperatorList));
				wParms.put("PeriodChangeStandard", Integer.valueOf(wTaskIPT.PeriodChangeStandard));
				wParms.put("Active", Integer.valueOf(wTaskIPT.Active));

				wParms.put("PicUri", wTaskIPT.PicUri);
				wParms.put("Remark", wTaskIPT.Remark);
				wParms.put("CheckerList", wTaskIPT.CheckerList);

				wSQLText = DMLChange(wSQLText);
				GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

				MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(wParms);
				this.nameJdbcTemplate.update(wSQLText, (SqlParameterSource) mapSqlParameterSource,
						(KeyHolder) generatedKeyHolder);

				wID = generatedKeyHolder.getKey().intValue();

				if (wID > 0)
					SFC_SaveIPTItemList(wLoginUser.getCompanyID(), wTaskIPT, wErrorCode);
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			LoggerTool.SaveException("SFCService", "SFC_AddTaskIPT", "Function Exception:" + ex.toString());
		}
		return wID;
	}

	private int SFC_SaveIPTItemList(int wCompanyID, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
		wErrorCode.set(Integer.valueOf(0));
		int wID = 0;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wCompanyID, MESDBSource.Basic);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));

			if (((Integer) wErrorCode.Result).intValue() == 0) {
				Map<String, Object> wParms = new HashMap<>();
				String wSQLText = "";

				if (wTaskIPT.ID > 0) {
					for (SFCIPTItem wItem : wTaskIPT.ItemList) {
						wSQLText = String.valueOf(
								StringUtils.Format("Insert Into {0}.sfc_iptitem", new Object[] { wInstance.Result }))
								+ "(ParentID,ItemID,ItemValue) " + " Values(:ParentID,:ItemID,:ItemValue);";

						wParms.clear();
						wParms.put("ParentID", Integer.valueOf(wTaskIPT.ID));
						wParms.put("ItemID", Integer.valueOf(wItem.ItemID));
						wParms.put("ItemValue", Float.valueOf(wItem.ItemValue));

						wSQLText = DMLChange(wSQLText);
						GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

						MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(wParms);
						this.nameJdbcTemplate.update(wSQLText, (SqlParameterSource) mapSqlParameterSource,
								(KeyHolder) generatedKeyHolder);

						wID = generatedKeyHolder.getKey().intValue();
					}

				}
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			LoggerTool.SaveException("SFCService", "SFC_SaveIPTItemList", "Function Exception:" + ex.toString());
		}
		return wID;
	}

	private SFCTaskIPT SFC_CheckTaskIPT(int wCompanyID, SFCTaskIPT wTaskIPT, OutResult<Integer> wErrorCode) {
		SFCTaskIPT wTaskIPTDB = new SFCTaskIPT();
		wErrorCode.set(Integer.valueOf(0));
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wCompanyID, MESDBSource.Basic);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));

			if (((Integer) wErrorCode.Result).intValue() == 0) {
				Map<String, Object> wParms = new HashMap<>();
				String wSQLText = "";

				if (wTaskIPT.ID > 0) {
					wSQLText = String.valueOf(
							StringUtils.Format("Select * from {0}.sfc_taskipt", new Object[] { wInstance.Result }))
							+ " where ID!=:ID and TaskStepID=:TaskStepID and ShiftID=:ShiftID and Status=0 and TaskType=:TaskType";
					wParms.clear();
					wParms.put("ID", Integer.valueOf(wTaskIPT.ID));
					wParms.put("TaskStepID", Integer.valueOf(wTaskIPT.TaskStepID));
					wParms.put("ShiftID", Integer.valueOf(wTaskIPT.ShiftID));
					wParms.put("TaskType", Integer.valueOf(wTaskIPT.TaskType));
				} else {
					wSQLText = String.valueOf(
							StringUtils.Format("Select * from {0}.sfc_taskipt", new Object[] { wInstance.Result }))
							+ "  where TaskStepID=:TaskStepID and ShiftID=:ShiftID and Status=0 and TaskType=:TaskType";
					wParms.clear();
					wParms.put("TaskStepID", Integer.valueOf(wTaskIPT.TaskStepID));
					wParms.put("ShiftID", Integer.valueOf(wTaskIPT.ShiftID));
					wParms.put("TaskType", Integer.valueOf(wTaskIPT.TaskType));
				}
				wSQLText = DMLChange(wSQLText);
				List<Map<String, Object>> wQueryResultList = this.nameJdbcTemplate.queryForList(wSQLText, wParms);
				for (Map<String, Object> wSqlDataReader : wQueryResultList) {
					wTaskIPTDB.ID = StringUtils.parseInt(wSqlDataReader.get("ID")).intValue();
					wTaskIPTDB.LineID = StringUtils.parseInt(wSqlDataReader.get("LineID")).intValue();
					wTaskIPTDB.PartID = StringUtils.parseInt(wSqlDataReader.get("PartID")).intValue();
					wTaskIPTDB.PartPointID = StringUtils.parseInt(wSqlDataReader.get("PartPointID")).intValue();
					wTaskIPTDB.StationID = StringUtils.parseInt(wSqlDataReader.get("StationID")).intValue();
					wTaskIPTDB.ProductID = StringUtils.parseInt(wSqlDataReader.get("ProductID")).intValue();
					wTaskIPTDB.TaskStepID = StringUtils.parseInt(wSqlDataReader.get("TaskStepID")).intValue();
					wTaskIPTDB.TaskType = StringUtils.parseInt(wSqlDataReader.get("TaskType")).intValue();

					wTaskIPTDB.ModuleVersionID = StringUtils.parseInt(wSqlDataReader.get("ModuleVersionID")).intValue();
					wTaskIPTDB.OperatorID = StringUtils.parseInt(wSqlDataReader.get("OperatorID")).intValue();
					wTaskIPTDB.ShiftID = StringUtils.parseInt(wSqlDataReader.get("ShiftID")).intValue();

					wTaskIPTDB.ActiveTime = StringUtils.parseCalendar(wSqlDataReader.get("ActiveTime"));
					wTaskIPTDB.Status = StringUtils.parseInt(wSqlDataReader.get("Status")).intValue();
					wTaskIPTDB.SubmitTime = StringUtils.parseCalendar(wSqlDataReader.get("SubmitTime"));
					wTaskIPTDB.StartTime = StringUtils.parseCalendar(wSqlDataReader.get("StartTime"));
					wTaskIPTDB.EndTime = StringUtils.parseCalendar(wSqlDataReader.get("EndTime"));
					wTaskIPTDB.Result = StringUtils.parseInt(wSqlDataReader.get("Result")).intValue();

					wTaskIPTDB.TaskMode = StringUtils.parseInt(wSqlDataReader.get("TaskMode")).intValue();
					wTaskIPTDB.Times = StringUtils.parseInt(wSqlDataReader.get("Times")).intValue();
					wTaskIPTDB.FQTYGood = StringUtils.parseInt(wSqlDataReader.get("FQTYGood")).intValue();
					wTaskIPTDB.FQTYBad = StringUtils.parseInt(wSqlDataReader.get("FQTYBad")).intValue();
					wTaskIPTDB.Type = StringUtils.parseInt(wSqlDataReader.get("Type")).intValue();
					wTaskIPTDB.Active = StringUtils.parseInt(wSqlDataReader.get("Active")).intValue();

					wTaskIPTDB.PicUri = StringUtils.parseString(wSqlDataReader.get("PicUri"));
					wTaskIPTDB.Remark = StringUtils.parseString(wSqlDataReader.get("Remark"));
					wTaskIPTDB.CheckerList = StringUtils.parseString(wSqlDataReader.get("CheckerList"));

					wTaskIPTDB.PeriodChangeStandard = StringUtils.parseInt(wSqlDataReader.get("PeriodChangeStandard"))
							.intValue();
					wTaskIPTDB.OperatorList = StringUtils
							.parseIntList(StringUtils.parseString(wSqlDataReader.get("OperatorList")).split(",|;"));
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			LoggerTool.SaveException("SFCService", "SFC_CheckTaskIPT", "Function Exception:" + ex.toString());
		}
		return wTaskIPTDB;
	}

	public int SelectDoneSize(BMSEmployee wLoginUser, int wOrderID, int wPartID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select (SELECT PartNo FROM {1}.oms_order where ID=:OrderID) as PartNo,"
					+ "(SELECT Name  FROM {0}.fpc_part where ID=:PartID) as PartName ,"
					+ "(SELECT count(*) FROM {0}.fpc_routepartpoint where RouteID "
					+ "in(SELECT RouteID FROM {1}.oms_order where ID=:OrderID) and PartID=:PartID) as StepSize,"
					+ "(select count(*)  FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID=:PartID "
					+ "and TaskType=13 and Status=2) as StepDone;", wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("PartID", wPartID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			int wDoneSize = 0;
			for (Map<String, Object> wMap : wQueryResult) {
				wResult = StringUtils.parseInt(wMap.get("StepSize"));
				wDoneSize = StringUtils.parseInt(wMap.get("StepDone"));
				if (wDoneSize > wResult)
					wDoneSize = wResult;
				break;
			}
			wErrorCode.put("DoneSize", wDoneSize);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取专检车辆分类的订单ID列表
	 */
	public List<Integer> SFC_QuerySpecialPartNoOrderIDList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT distinct(OrderID) FROM {0}.sfc_taskipt where Active=1 and TaskType=13 and StationID in(SELECT StationID "
							+ "FROM {0}.bms_workcharge where  find_in_set(:Checker,replace(CheckerList,'';'','','') )) "
							+ "and ((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1);",
					wInstance.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("Checker", wLoginUser.ID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wOrderID = StringUtils.parseInt(wMap.get("OrderID"));
				if (wOrderID > 0) {
					wResult.add(wOrderID);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取专检车辆分类的订单ID列表
	 */
	public List<Integer> SFC_QuerySpecialPartNoOrderIDList_V1(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT distinct(OrderID) FROM {0}.sfc_taskipt t1 where Active=1 and TaskType=13 "
							+ "and find_in_set(:Checker, (SELECT CheckerList FROM {0}.bms_workcharge "
							+ "where ClassID in (select DepartmentID from {0}.mbs_user where ID "
							+ "in (  SELECT MonitorID FROM {1}.sfc_taskstep where TaskStepID=t1.TaskStepID)) "
							+ "and StationID=t1.StationID and Active=1 limit 1)) "
							+ "and ((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1);",
					wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("Checker", wLoginUser.ID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wOrderID = StringUtils.parseInt(wMap.get("OrderID"));
				if (wOrderID > 0) {
					wResult.add(wOrderID);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据订单，开始结束时间获取专检车辆统计数据
	 */
	public SFCTaskIPTPartNo SFC_QuerySpecialPartNoNew_V1(BMSEmployee wLoginUser, Integer wOrderID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		SFCTaskIPTPartNo wResult = new SFCTaskIPTPartNo();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select (select ID from {0}.oms_order where ID=:wOrderID) as OrderID,"
					+ "(select OrderNo from {0}.oms_order where ID=:wOrderID) as OrderNo,"
					+ "(select PartNo from {0}.oms_order where ID=:wOrderID) as PartNo,"
					+ "(select CustomerName  from {1}.crm_customer t1,{0}.oms_order t2,"
					+ "{0}.oms_command t3 where t1.ID=t3.CustomerID and t2.CommandID=t3.ID and t2.ID=:wOrderID) "
					+ "as Customer,(select Name  from {1}.fmc_line t1,{0}.oms_order t2 "
					+ "where t1.ID=t2.LineID and t2.ID=:wOrderID) as LineName,(SELECT count(*)  FROM {1}.sfc_taskipt t1 "
					+ "where TaskType=13 and find_in_set(:Checker, (SELECT CheckerList FROM {1}.bms_workcharge "
					+ "where ClassID in (select DepartmentID from {1}.mbs_user where ID in (  SELECT MonitorID "
					+ "FROM {0}.sfc_taskstep where TaskStepID=t1.TaskStepID)) and StationID=t1.StationID and Active=1 limit 1)) "
					+ "and ((:StartTime < EndTime and :EndTime > StartTime and Status=2) or Status=1) "
					+ "and OrderID=:wOrderID) as FQTYTotal,(SELECT count(*)  FROM {1}.sfc_taskipt t1 where TaskType=13 "
					+ "and find_in_set(:Checker, (SELECT CheckerList FROM {1}.bms_workcharge where ClassID in "
					+ "(select DepartmentID from {1}.mbs_user where ID in (  SELECT MonitorID FROM {0}.sfc_taskstep "
					+ "where TaskStepID=t1.TaskStepID)) and StationID=t1.StationID and Active=1 limit 1)) "
					+ "and ((:StartTime < EndTime and :EndTime > StartTime and Status=2) or Status=1) "
					+ "and Status=2 and OrderID=:wOrderID) as FQTYDone,(select count(*) from {0}.aps_taskstep where PartID in (SELECT distinct StationID  FROM {1}.sfc_taskipt t1 "
					+ "	where TaskType=13 and find_in_set(:Checker, (SELECT CheckerList FROM {1}.bms_workcharge "
					+ "	where ClassID in (select DepartmentID from {1}.mbs_user where ID in (  SELECT MonitorID "
					+ "	FROM {0}.sfc_taskstep where TaskStepID=t1.TaskStepID)) and StationID=t1.StationID and Active=1 limit 1)) "
					+ "	and ((:StartTime < EndTime and :EndTime > StartTime and Status=2) or Status=1) "
					+ "	and OrderID=:wOrderID) and active=1 and OrderID=:wOrderID) as FQTY;", wInstance.Result,
					wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("Checker", wLoginUser.ID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				String wOrderNo = StringUtils.parseString(wMap.get("OrderNo"));
				String wPartNo = StringUtils.parseString(wMap.get("PartNo"));
				String wCustomer = StringUtils.parseString(wMap.get("Customer"));
				String wLineName = StringUtils.parseString(wMap.get("LineName"));
				int wFQTYTotal = StringUtils.parseInt(wMap.get("FQTYTotal"));
				int wDone = StringUtils.parseInt(wMap.get("FQTYDone"));
				int wFQTYDone = GetFQTYDone(wLoginUser, wStartTime, wEndTime, wOrderID, wErrorCode);
				int wFQTY = StringUtils.parseInt(wMap.get("FQTY"));

				wResult.Customer = wCustomer;
				wResult.FQTY = wFQTY;
				wResult.FQTYDone = wFQTYDone;
				wResult.FQTYTotal = wFQTYTotal;
				wResult.LineName = wLineName;
				wResult.OrderID = wOrderID;
				wResult.OrderNo = wOrderNo;
				wResult.FQTYToDo = wResult.FQTYTotal - wDone;
				wResult.PartNo = wPartNo;
				wResult.No = wResult.PartNo.split("#").length == 2 ? wResult.PartNo.split("#")[1] : "";
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取已完成的数据
	 */
	public int GetFQTYDone(BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime, int wOrderID,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<Integer> wIDList = GetStationIDList(wLoginUser, wStartTime, wEndTime, wOrderID, wErrorCode);
			if (wIDList.size() <= 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select count(*) Number from {0}.sfc_taskipt where TaskType=13 "
							+ "and Active=1 and Status=2 and OrderID=:OrderID and StationID in ({1});",
					wInstance.Result, StringUtils.Join(",", wIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);

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

	/**
	 * 获取已完成的数据
	 */
	public int GetFQTYDone(BMSEmployee wLoginUser, int wOrderID, int wPartID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select count(*) Number from {0}.sfc_taskipt where TaskType=13 "
							+ "and Active=1 and Status=2 and OrderID=:OrderID and StationID = :StationID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("StationID", wPartID);

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

	/**
	 * 获取正在做的专检工位ID集合
	 */
	public List<Integer> GetStationIDList(BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime, int wOrderID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT distinct StationID FROM {0}.sfc_taskipt t1 where TaskType=13 "
					+ "	and find_in_set(:Checker, (SELECT CheckerList FROM {0}.bms_workcharge where ClassID in "
					+ "	(select DepartmentID from {0}.mbs_user where ID in (  SELECT MonitorID FROM {1}.sfc_taskstep "
					+ "	where TaskStepID=t1.TaskStepID)) and StationID=t1.StationID and Active=1 limit 1)) "
					+ "	and ((:wStartTime < EndTime and :wEndTime > StartTime and Status=2) or Status=1) "
					+ "	and OrderID=:OrderID;", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("Checker", wLoginUser.ID);

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
	 * 根据订单，开始结束时间获取专检车辆统计数据
	 */
	public SFCTaskIPTPartNo SFC_QuerySpecialPartNoNew(BMSEmployee wLoginUser, Integer wOrderID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		SFCTaskIPTPartNo wResult = new SFCTaskIPTPartNo();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select (select ID from {0}.oms_order where ID=:wOrderID) as OrderID,"
					+ "(select OrderNo from {0}.oms_order where ID=:wOrderID) as OrderNo,"
					+ "(select PartNo from {0}.oms_order where ID=:wOrderID) as PartNo,"
					+ "(select CustomerName  from {1}.crm_customer t1,{0}.oms_order t2,"
					+ "{0}.oms_command t3 where t1.ID=t3.CustomerID and t2.CommandID=t3.ID and t2.ID=:wOrderID) "
					+ "as Customer,(select Name  from {1}.fmc_line t1,{0}.oms_order t2 "
					+ "where t1.ID=t2.LineID and t2.ID=:wOrderID) as LineName,(SELECT count(*)  FROM {1}.sfc_taskipt "
					+ "where TaskType=13 and StationID in(SELECT StationID FROM {1}.bms_workcharge "
					+ "where find_in_set(:Checker, replace(CheckerList,'';'','','') )) and ((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1) "
					+ "and OrderID=:wOrderID) as FQTYTotal,(SELECT count(*)  FROM {1}.sfc_taskipt where TaskType=13 "
					+ "and StationID in(SELECT StationID FROM {1}.bms_workcharge where find_in_set(:Checker, replace(CheckerList,'';'','','') )) "
					+ "and ((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1) "
					+ "and Status=2 and OrderID=:wOrderID) as FQTYDone;", wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("Checker", wLoginUser.ID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				String wOrderNo = StringUtils.parseString(wMap.get("OrderNo"));
				String wPartNo = StringUtils.parseString(wMap.get("PartNo"));
				String wCustomer = StringUtils.parseString(wMap.get("Customer"));
				String wLineName = StringUtils.parseString(wMap.get("LineName"));
				int wFQTYTotal = StringUtils.parseInt(wMap.get("FQTYTotal"));
				int wFQTYDone = StringUtils.parseInt(wMap.get("FQTYDone"));

				wResult.Customer = wCustomer;
				wResult.FQTYDone = wFQTYDone;
				wResult.FQTYTotal = wFQTYTotal;
				wResult.LineName = wLineName;
				wResult.OrderID = wOrderID;
				wResult.OrderNo = wOrderNo;
				wResult.FQTYToDo = wResult.FQTYTotal - wResult.FQTYDone;
				wResult.PartNo = wPartNo;
				wResult.No = wResult.PartNo.split("#").length == 2 ? wResult.PartNo.split("#")[1] : "";
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取专检工位分类的工位ID列表
	 */
	public List<Integer> SFC_QuerySpecialPartIDList(BMSEmployee wLoginUser, int wOrderID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT distinct(StationID) FROM {0}.sfc_taskipt where TaskType=13 and StationID "
							+ "in(SELECT StationID FROM {0}.bms_workcharge where find_in_set(:Checker, replace(CheckerList,'';'','','')) ) "
							+ "and ((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1) and OrderID=:OrderID;",
					wInstance.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("Checker", wLoginUser.ID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wPartID = StringUtils.parseInt(wMap.get("StationID"));
				if (wPartID > 0) {
					wResult.add(wPartID);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取专检工位分类的工位ID列表
	 */
	public List<Integer> SFC_QuerySpecialPartIDList_V1(BMSEmployee wLoginUser, int wOrderID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT distinct(StationID) FROM {0}.sfc_taskipt t1 where TaskType=13 "
					+ "and find_in_set(:Checker, (SELECT CheckerList FROM {0}.bms_workcharge "
					+ "where ClassID in (select DepartmentID from {0}.mbs_user where ID "
					+ "in (  SELECT MonitorID FROM {1}.sfc_taskstep where TaskStepID=t1.TaskStepID)) "
					+ "and StationID=t1.StationID and Active=1 limit 1)) "
					+ "and ((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1) "
					+ "and OrderID=:OrderID;", wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("Checker", wLoginUser.ID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wPartID = StringUtils.parseInt(wMap.get("StationID"));
				if (wPartID > 0) {
					wResult.add(wPartID);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取工位分类的专检信息
	 */
	public SFCTaskIPTPart SFC_QuerySpecialPartListNew(BMSEmployee wLoginUser, int wOrderID, Integer wStationID,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		SFCTaskIPTPart wResult = new SFCTaskIPTPart();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select (select ID from {0}.oms_order where ID=:wOrderID) as OrderID,"
					+ "(select OrderNo from {0}.oms_order where ID=:wOrderID) as OrderNo,"
					+ "(select PartNo from {0}.oms_order where ID=:wOrderID) as PartNo,"
					+ "(select CustomerName  from {1}.crm_customer t1,{0}.oms_order t2,{0}.oms_command t3 "
					+ "where t1.ID=t3.CustomerID and t2.CommandID=t3.ID and t2.ID=:wOrderID) as Customer,"
					+ "(select Name  from {1}.fmc_line t1,{0}.oms_order t2 where t1.ID=t2.LineID and t2.ID=:wOrderID) as LineName,"
					+ "(SELECT count(*)  FROM {1}.sfc_taskipt where TaskType=13 and StationID in(SELECT StationID "
					+ "FROM {1}.bms_workcharge where find_in_set(:Checker, replace(CheckerList,'';'','',''))) and "
					+ "((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1) and "
					+ "OrderID=:wOrderID and StationID=:wPartID) as FQTYTotal,"
					+ "(SELECT count(*)  FROM {1}.sfc_taskipt where TaskType=13 and StationID "
					+ "in(SELECT StationID FROM {1}.bms_workcharge where find_in_set(:Checker, replace(CheckerList,'';'','',''))) "
					+ "and ((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1) and Status=2 "
					+ "and OrderID=:wOrderID and StationID=:wPartID) as FQTYDone,(select Name from {1}.fpc_part "
					+ "where ID=:wPartID) as PartName;", wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wStationID);
			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("Checker", wLoginUser.ID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				String wOrderNo = StringUtils.parseString(wMap.get("OrderNo"));
				String wPartNo = StringUtils.parseString(wMap.get("PartNo"));
				String wCustomer = StringUtils.parseString(wMap.get("Customer"));
				String wLineName = StringUtils.parseString(wMap.get("LineName"));
				String wPartName = StringUtils.parseString(wMap.get("PartName"));
				int wFQTYTotal = StringUtils.parseInt(wMap.get("FQTYTotal"));
				int wFQTYDone = StringUtils.parseInt(wMap.get("FQTYDone"));

				wResult.Customer = wCustomer;
				wResult.FQTYDone = wFQTYDone;
				wResult.FQTYTotal = wFQTYTotal;
				wResult.LineName = wLineName;
				wResult.PartID = wStationID;
				wResult.PartName = wPartName;
				wResult.OrderID = wOrderID;
				wResult.OrderNo = wOrderNo;
				wResult.FQTYToDo = wResult.FQTYTotal - wResult.FQTYDone;
				wResult.PartNo = wPartNo;
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取工位分类的专检信息
	 */
	public SFCTaskIPTPart SFC_QuerySpecialPartListNew_V1(BMSEmployee wLoginUser, int wOrderID, Integer wStationID,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		SFCTaskIPTPart wResult = new SFCTaskIPTPart();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select (select ID from {0}.oms_order where ID=:wOrderID) as OrderID,"
					+ "(select OrderNo from {0}.oms_order where ID=:wOrderID) as OrderNo,"
					+ "(select PartNo from {0}.oms_order where ID=:wOrderID) as PartNo,"
					+ "(select CustomerName  from {1}.crm_customer t1,{0}.oms_order t2,{0}.oms_command t3 "
					+ "where t1.ID=t3.CustomerID and t2.CommandID=t3.ID and t2.ID=:wOrderID) as Customer,"
					+ "(select Name  from {1}.fmc_line t1,{0}.oms_order t2 where t1.ID=t2.LineID and t2.ID=:wOrderID) as LineName,"
					+ "(SELECT count(*)  FROM {1}.sfc_taskipt t1 where TaskType=13 and find_in_set(:Checker, (SELECT CheckerList FROM {1}.bms_workcharge where ClassID in (select DepartmentID from {1}.mbs_user where ID in (  SELECT MonitorID FROM {0}.sfc_taskstep where TaskStepID=t1.TaskStepID)) and StationID=t1.StationID and Active=1 limit 1)) and "
					+ "((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1) and "
					+ "OrderID=:wOrderID and StationID=:wPartID) as FQTYTotal,"
					+ "(SELECT count(*)  FROM {1}.sfc_taskipt t1 where TaskType=13 and find_in_set(:Checker, (SELECT CheckerList FROM {1}.bms_workcharge where ClassID in (select DepartmentID from {1}.mbs_user where ID in (  SELECT MonitorID FROM {0}.sfc_taskstep where TaskStepID=t1.TaskStepID)) and StationID=t1.StationID and Active=1 limit 1)) "
					+ "and ((:StartTime<EndTime and :EndTime>StartTime and Status=2) or Status=1) and Status=2 "
					+ "and OrderID=:wOrderID and StationID=:wPartID) as FQTYDone,(select Name from {1}.fpc_part "
					+ "where ID=:wPartID) as PartName,(select count(*) from {0}.aps_taskstep where PartID=:wPartID and active=1 and OrderID=:wOrderID) as FQTY;",
					wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wStationID);
			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("Checker", wLoginUser.ID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				String wOrderNo = StringUtils.parseString(wMap.get("OrderNo"));
				String wPartNo = StringUtils.parseString(wMap.get("PartNo"));
				String wCustomer = StringUtils.parseString(wMap.get("Customer"));
				String wLineName = StringUtils.parseString(wMap.get("LineName"));
				String wPartName = StringUtils.parseString(wMap.get("PartName"));
				int wFQTYTotal = StringUtils.parseInt(wMap.get("FQTYTotal"));
				int wDone = StringUtils.parseInt(wMap.get("FQTYDone"));
				int wFQTYDone = GetFQTYDone(wLoginUser, wOrderID, wStationID, wErrorCode);
				int wFQTY = StringUtils.parseInt(wMap.get("FQTY"));

				wResult.FQTY = wFQTY;
				wResult.Customer = wCustomer;
				wResult.FQTYDone = wFQTYDone;
				wResult.FQTYTotal = wFQTYTotal;
				wResult.LineName = wLineName;
				wResult.PartID = wStationID;
				wResult.PartName = wPartName;
				wResult.OrderID = wOrderID;
				wResult.OrderNo = wOrderNo;
				wResult.FQTYToDo = wResult.FQTYTotal - wDone;
				wResult.PartNo = wPartNo;
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取自检任务列表
	 */
	public List<SFCTaskIPT> SelectSelfTaskList(BMSEmployee wLoginUser, String wPartNo, Calendar wStartTime,
			Calendar wEndTime, int wOrderID, OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResult = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wAPSInstance = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wAPSInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			if (StringUtils.isEmpty(wPartNo)) {
				wPartNo = "";
			}

			String wSQL = StringUtils.Format("SELECT t1.* FROM {0}.sfc_taskipt t1,{1}.sfc_taskstep t2 "
					+ "where t1.TaskStepID=t2.TaskStepID and :OperatorID=t2.OperatorID "
					+ " and ( :wPartNo = '''' or t1.PartNo like ''%{2}%'' ) "
					+ " and ( :wOrderID <= 0 or :wOrderID = t1.OrderID ) "
					+ " and (t1.Status=1 or (t1.Status =2 and find_in_set(:OperatorID, replace(t1.OperatorList,'';'','','') ) and :StartTime <t1.EndTime and :EndTime>t1.StartTime)) "
					+ " and t1.TaskType=6 group by t1.ID;", wInstance.Result, wAPSInstance.Result, wPartNo);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("OperatorID", wLoginUser.ID);
			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wOrderID", wOrderID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResult, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断登录者是否能做自检任务
	 * 
	 * @param wErrorCode
	 */
	public boolean IsCanDoSelfTask(BMSEmployee wLoginUser, int wTaskIPTID, OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select count(*) as Number from {0}.mbs_user "
							+ "where ID=:OperatorID and ID in (  select SubmitID from  {1}.ipt_value "
							+ "where TaskID in( select ID from {0}.sfc_taskipt where TaskStepID in "
							+ "( select TaskStepID from {0}.sfc_taskipt where ID=:TaskIPTID) and TaskType=12));",
					wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("OperatorID", wLoginUser.ID);
			wParamMap.put("TaskIPTID", wTaskIPTID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wNumber = StringUtils.parseInt(wMap.get("Number"));
				if (wNumber <= 0) {
					wResult = true;
				}
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断互检任务是否派工了多人
	 * 
	 * @param wErrorCode
	 */
	public boolean IsDispatchedMulti(BMSEmployee wLoginUser, int wTaskIPTID, OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select count( distinct(OperatorID)) as Number "
					+ "from {1}.sfc_taskstep where TaskStepID in(select TaskStepID "
					+ "from {0}.sfc_taskipt where ID=:TaskIPTID);", wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskIPTID", wTaskIPTID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wNumber = StringUtils.parseInt(wMap.get("Number"));
				if (wNumber > 1) {
					wResult = true;
				}
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断登录者是否能做互检任务(派工多人的情况)
	 * 
	 * @param wErrorCode
	 */
	public boolean IsCanDoMutualTask(BMSEmployee wLoginUser, int wTaskIPTID, OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance2 = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance2.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select count(*) as Number from  {0}.mbs_user where ID=:OperaterID and ID "
							+ "in (select  distinct(OperatorID) as Number from {1}.sfc_taskstep "
							+ "where TaskStepID in(select TaskStepID from {0}.sfc_taskipt where ID=:TaskIPTID)) "
							+ "and ID not in(select distinct(SubmitID) from {2}.ipt_value "
							+ "where TaskID in(  select ID from {0}.sfc_taskipt where TaskStepID "
							+ "in ( select TaskStepID from {0}.sfc_taskipt where ID=:TaskIPTID) and TaskType=6));",
					wInstance.Result, wInstance1.Result, wInstance2.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("OperaterID", wLoginUser.ID);
			wParamMap.put("TaskIPTID", wTaskIPTID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wNumber = StringUtils.parseInt(wMap.get("Number"));
				if (wNumber > 0) {
					wResult = true;
				}
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断登录者是否能做互检任务(派工单人的情况)
	 * 
	 * @param wErrorCode
	 */
	public boolean IsCanDoMutualTaskSingle(BMSEmployee wLoginUser, int wTaskIPTID, OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select count(*) as Number from {0}.mbs_user where ID=:OperaterID and ID "
							+ " in( select ID from {0}.mbs_user where DepartmentID "
							+ " in(select distinct(DepartmentID) from {0}.mbs_user where ID "
							+ " in(select  distinct(OperatorID) as Number from {1}.sfc_taskstep "
							+ " where TaskStepID in(select TaskStepID from {0}.sfc_taskipt where ID=:TaskIPTID))) "
							+ " and ID not in(select  distinct(OperatorID) as Number from {1}.sfc_taskstep "
							+ " where TaskStepID in(select TaskStepID from {0}.sfc_taskipt where ID=:TaskIPTID)));",
					wInstance.Result, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("OperaterID", wLoginUser.ID);
			wParamMap.put("TaskIPTID", wTaskIPTID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wMap : wQueryResult) {
				int wNumber = StringUtils.parseInt(wMap.get("Number"));
				if (wNumber > 0) {
					wResult = true;
				}
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取互检任务列表
	 */
	public List<SFCTaskIPT> SelectMutualTaskList(BMSEmployee wLoginUser, String wPartNo, Calendar wStartTime,
			Calendar wEndTime, int wOrderID, OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResult = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			if (StringUtils.isEmpty(wPartNo)) {
				wPartNo = "";
			}

			String wSQL = StringUtils.Format("SELECT t1.* FROM {0}.sfc_taskipt t1 "
					+ "where (t1.Status=1 or (t1.Status =2 and find_in_set(:OperaterID, replace(t1.OperatorList,'';'','','') )"
					+ " and :StartTime <t1.EndTime and :EndTime>t1.StartTime)) "
					+ "and (:wPartNo is null or :wPartNo = '''' or t1.PartNo like ''%{1}%'' ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = t1.OrderID ) "
					+ "and t1.TaskType=12 and t1.Active=1 group by t1.ID;", wInstance.Result, wPartNo);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("OperaterID", wLoginUser.ID);
			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wOrderID", wOrderID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResult, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取专检
	 */
	public List<SFCTaskIPT> SelectSpecialTaskList(BMSEmployee wLoginUser, String wPartNo, Calendar wStartTime,
			Calendar wEndTime, int wOrderID, OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResult = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			if (StringUtils.isEmpty(wPartNo)) {
				wPartNo = "";
			}

			String wSQL = StringUtils.Format("SELECT t1.* FROM {0}.sfc_taskipt t1 "
					+ "where (t1.Status=1 or (t1.Status =2 and :StartTime <t1.EndTime and :EndTime>t1.StartTime)) "
					+ "and (:wPartNo is null or :wPartNo = '''' or t1.PartNo like ''%{1}%'' ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = t1.OrderID ) "
					+ "and t1.TaskType=13 and t1.StationID in (SELECT distinct(StationID) "
					+ "FROM {0}.bms_workcharge where  find_in_set(:Checker, replace(CheckerList,'';'','','')) )"
					+ " group by t1.ID;", wInstance.Result, wPartNo);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("wPartNo", wPartNo);

			wParamMap.put("Checker", wLoginUser.ID);
			wParamMap.put("wOrderID", wOrderID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResult, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取预检、终检、出厂检
	 */
	public List<SFCTaskIPT> SelectOtherTaskList(BMSEmployee wLoginUser, String wPartNo, Calendar wStartTime,
			Calendar wEndTime, int wOrderID, int wTaskType, OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResult = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			if (StringUtils.isEmpty(wPartNo)) {
				wPartNo = "";
			}

			String wSQL = StringUtils.Format("SELECT t1.* FROM {0}.sfc_taskipt t1,{2}.sfc_taskstep t2 "
					+ "where t1.TaskStepID=t2.TaskStepID and :OperatorID=t2.OperatorID "
					+ "and (:wPartNo is null or :wPartNo = '''' or t1.PartNo like ''%{1}%'' ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = t1.OrderID ) "
					+ "and (t1.Status=1 or (t1.Status =2 and :StartTime <t1.EndTime and :EndTime>t1.StartTime)) "
					+ "and t1.TaskType=:TaskType group by t1.ID;", wInstance.Result, wPartNo, wInstance1.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("OperatorID", wLoginUser.ID);
			wParamMap.put("StartTime", wStartTime);
			wParamMap.put("EndTime", wEndTime);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("TaskType", wTaskType);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResult, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工序任务ID获取检验单ID集合
	 */
	public List<Integer> SelectTaskIDList(BMSEmployee wLoginUser, int wAPSTaskStepID, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT distinct(ID) FROM {0}.sfc_taskipt where TaskStepID=:TaskStepID;",
					wInstance.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskStepID", wAPSTaskStepID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("ID"));
				if (wID > 0) {
					wResult.add(wID);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工序任务ID和检验任务ID获取检验任务详情
	 */
	public SFCTaskIPTInfo SelectSFCTaskIPTInfo(BMSEmployee wLoginUser, int wAPSTaskStepID, int wTaskIPTID,
			OutResult<Integer> wErrorCode) {
		SFCTaskIPTInfo wResult = new SFCTaskIPTInfo();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance2 = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance2.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select (select PartNo from {0}.aps_taskstep where ID=:TaskStepID) as PartNo,"
							+ "(select Name as LineName from {0}.aps_taskstep t1,{1}.fmc_line t2 where t1.LineID=t2.ID and t1.ID=:TaskStepID) as LineName,"
							+ "(select t2.CustomerName from {0}.oms_order t1,{1}.crm_customer t2 where t1.BureauSectionID=t2.ID and t1.ID in( select OrderID from {0}.aps_taskstep where ID=:TaskStepID)) as CustomerName,"
							+ "(select t2.ProductNo from {0}.oms_order t1,{1}.fpc_product t2 where t1.ProductID=t2.ID and t1.ID in( select OrderID from {0}.aps_taskstep where ID=:TaskStepID)) as ProductNo,"
							+ "(select t2.Name as StationName from {0}.aps_taskstep t1,{1}.fpc_part t2 where t1.PartID=t2.ID and t1.ID=:TaskStepID) as StationName,"
							+ "(select t2.Name as StepName from {0}.aps_taskstep t1,{1}.fpc_partpoint t2 where t1.StepID=t2.ID and t1.ID=:TaskStepID) as StepName,"
							+ "(SELECT TaskType FROM {1}.sfc_taskipt where ID=:TaskIPTID) as TaskType,"
							+ "(SELECT OperatorList FROM {1}.sfc_taskipt where ID=:TaskIPTID) as OperatorList,"
							+ "(SELECT Status FROM {1}.sfc_taskipt where ID=:TaskIPTID) as Status,"
							+ "(SELECT StartTime FROM {1}.sfc_taskipt where ID=:TaskIPTID) as StartTime,"
							+ "(SELECT EndTime FROM {1}.sfc_taskipt where ID=:TaskIPTID) as EndTime,"
							+ "(SELECT count(*) as TotalSize FROM {2}.ipt_itemrecord where VID in(SELECT ModuleVersionID FROM {1}.sfc_taskipt where ID=:TaskIPTID) and ItemType!=4) as TotalSize,"
							+ "(SELECT count(distinct(IPTItemID)) as FinishSize FROM {2}.ipt_value where TaskID=:TaskIPTID and Status=2) as FinishSize;",
					wInstance.Result, wInstance1.Result, wInstance2.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskStepID", wAPSTaskStepID);
			wParamMap.put("TaskIPTID", wTaskIPTID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				wResult.CustomerName = StringUtils.parseString(wMap.get("CustomerName"));
				wResult.EndTime = StringUtils.parseCalendar(wMap.get("EndTime"));
				wResult.FinishSize = StringUtils.parseInt(wMap.get("FinishSize"));
				wResult.LineName = StringUtils.parseString(wMap.get("LineName"));
				wResult.PartNo = StringUtils.parseString(wMap.get("PartNo"));
				wResult.ProductNo = StringUtils.parseString(wMap.get("ProductNo"));
				wResult.StartTime = StringUtils.parseCalendar(wMap.get("StartTime"));
				wResult.StationName = StringUtils.parseString(wMap.get("StationName"));
				wResult.StepName = StringUtils.parseString(wMap.get("StepName"));
				wResult.TaskID = wTaskIPTID;
				wResult.TotalSize = StringUtils.parseInt(wMap.get("TotalSize"));

				String wOperatorList = StringUtils.parseString(wMap.get("OperatorList"));
				if (StringUtils.isNotEmpty(wOperatorList)) {
					wResult.Persons = GetUserNames(StringUtils.parseIntList(wOperatorList.split(",|;")));
				}

				int wStatus = StringUtils.parseInt(wMap.get("Status"));
				wResult.StatusText = SFCTaskStatus.getEnumType(wStatus).getLable();

				int wTaskType = StringUtils.parseInt(wMap.get("TaskType"));
				switch (SFCTaskType.getEnumType(wTaskType)) {
				case PreCheck:
					wResult.Type = SFCSequentialInfoType.PreCheck.getValue();
					wResult.TypeText = SFCSequentialInfoType.PreCheck.getLable();
					break;
				case SelfCheck:
					wResult.Type = SFCSequentialInfoType.SelfCheck.getValue();
					wResult.TypeText = SFCSequentialInfoType.SelfCheck.getLable();
					break;
				case MutualCheck:
					wResult.Type = SFCSequentialInfoType.MutualCheck.getValue();
					wResult.TypeText = SFCSequentialInfoType.MutualCheck.getLable();
					break;
				case SpecialCheck:
					wResult.Type = SFCSequentialInfoType.SpecialCheck.getValue();
					wResult.TypeText = SFCSequentialInfoType.SpecialCheck.getLable();
					break;
				case Final:
					wResult.Type = SFCSequentialInfoType.FinalCheck.getValue();
					wResult.TypeText = SFCSequentialInfoType.FinalCheck.getLable();
					break;
				case OutPlant:
					wResult.Type = SFCSequentialInfoType.OutCheck.getValue();
					wResult.TypeText = SFCSequentialInfoType.OutCheck.getLable();
					break;
				default:
					break;
				}

				return wResult;
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工序任务ID获取不合格评审ID集合
	 */
	public List<Integer> SelectNCRTaskIDList(BMSEmployee wLoginUser, int wAPSTaskStepID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT distinct(ID) FROM {0}.ncr_sendtask where TaskStepID=:TaskStepID;",
					wInstance.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskStepID", wAPSTaskStepID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("ID"));
				if (wID > 0) {
					wResult.add(wID);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工序任务ID和不合格评审任务ID获取不合格评审任务详情
	 */
	public SFCTaskIPTInfo SelectNCRTaskIPTInfo(BMSEmployee wLoginUser, int wAPSTaskStepID, int wTaskID,
			OutResult<Integer> wErrorCode) {
		SFCTaskIPTInfo wResult = new SFCTaskIPTInfo();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance2 = GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(Integer.valueOf(wInstance2.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"Select (select PartNo from {0}.aps_taskstep where ID=:TaskStepID) as PartNo,"
							+ "(select Name as LineName from {0}.aps_taskstep t1,{1}.fmc_line t2 where t1.LineID=t2.ID and t1.ID=:TaskStepID) as LineName,"
							+ "(select t2.CustomerName from {0}.oms_order t1,{1}.crm_customer t2 where t1.BureauSectionID=t2.ID and t1.ID in( select OrderID from {0}.aps_taskstep where ID=:TaskStepID)) as CustomerName,"
							+ "(select t2.ProductNo from {0}.oms_order t1,{1}.fpc_product t2 where t1.ProductID=t2.ID and t1.ID in( select OrderID from {0}.aps_taskstep where ID=:TaskStepID)) as ProductNo,"
							+ "(select t2.Name as StationName from {0}.aps_taskstep t1,{1}.fpc_part t2 where t1.PartID=t2.ID and t1.ID=:TaskStepID) as StationName,"
							+ "(select t2.Name as StepName from {0}.aps_taskstep t1,{1}.fpc_partpoint t2 where t1.StepID=t2.ID and t1.ID=:TaskStepID) as StepName,"
							+ "(SELECT UpFlowID FROM {2}.ncr_task where ID=:TaskID) as UpFlowID,"
							+ "(SELECT StatusText FROM {2}.ncr_task where ID=:TaskID) as StatusText,"
							+ "(SELECT CreateTime FROM {2}.ncr_task where ID=:TaskID) as StartTime,"
							+ "(SELECT SubmitTime FROM {2}.ncr_task where ID=:TaskID) as EndTime,"
							+ "(SELECT DescribeInfo FROM {2}.ncr_task where ID=:TaskID) as DescribeInfo;",
					wInstance.Result, wInstance1.Result, wInstance2.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskStepID", wAPSTaskStepID);
			wParamMap.put("TaskID", wTaskID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				wResult.CustomerName = StringUtils.parseString(wMap.get("CustomerName"));
				wResult.EndTime = StringUtils.parseCalendar(wMap.get("EndTime"));
				wResult.LineName = StringUtils.parseString(wMap.get("LineName"));
				wResult.PartNo = StringUtils.parseString(wMap.get("PartNo"));
				wResult.ProductNo = StringUtils.parseString(wMap.get("ProductNo"));
				wResult.StartTime = StringUtils.parseCalendar(wMap.get("StartTime"));
				wResult.StationName = StringUtils.parseString(wMap.get("StationName"));
				wResult.StepName = StringUtils.parseString(wMap.get("StepName"));
				wResult.TaskID = wTaskID;
				wResult.Persons = QMSConstants.GetBMSEmployeeName(StringUtils.parseInt("UpFlowID"));
				wResult.StatusText = StringUtils.parseString(wMap.get("StatusText"));
				wResult.Remark = StringUtils.parseString(wMap.get("DescribeInfo"));

				wResult.Type = SFCSequentialInfoType.NCR.getValue();
				wResult.TypeText = SFCSequentialInfoType.NCR.getLable();

				return wResult;
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工序任务ID获取返修ID集合
	 */
	public List<Integer> SelectRepairTaskIDList(BMSEmployee wLoginUser, int wAPSTaskStepID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT distinct(ID) FROM {0}.rro_repairitem where TaskStepID=:TaskStepID;", wInstance.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskStepID", wAPSTaskStepID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("ID"));
				if (wID > 0) {
					wResult.add(wID);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工序任务ID和不合格评审任务ID获取不合格评审任务详情
	 */
	public SFCTaskIPTInfo SelectRepairTaskIPTInfo(BMSEmployee wLoginUser, int wAPSTaskStepID, int wTaskID,
			OutResult<Integer> wErrorCode) {
		SFCTaskIPTInfo wResult = new SFCTaskIPTInfo();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance2 = GetDataBaseName(wLoginUser, MESDBSource.WDW, 0);
			wErrorCode.set(Integer.valueOf(wInstance2.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"Select (select PartNo from {0}.aps_taskstep where ID=:TaskStepID) as PartNo,"
							+ "(select Name as LineName from {0}.aps_taskstep t1,{1}.fmc_line t2 where t1.LineID=t2.ID and t1.ID=:TaskStepID) as LineName,"
							+ "(select t2.CustomerName from {0}.oms_order t1,{1}.crm_customer t2 where t1.BureauSectionID=t2.ID and t1.ID in( select OrderID from {0}.aps_taskstep where ID=:TaskStepID)) as CustomerName,"
							+ "(select t2.ProductNo from {0}.oms_order t1,{1}.fpc_product t2 where t1.ProductID=t2.ID and t1.ID in( select OrderID from {0}.aps_taskstep where ID=:TaskStepID)) as ProductNo,"
							+ "(select t2.Name as StationName from {0}.aps_taskstep t1,{1}.fpc_part t2 where t1.PartID=t2.ID and t1.ID=:TaskStepID) as StationName,"
							+ "(select t2.Name as StepName from {0}.aps_taskstep t1,{1}.fpc_partpoint t2 where t1.StepID=t2.ID and t1.ID=:TaskStepID) as StepName,"
							+ "(SELECT UpFlowID FROM {2}.rro_repairitem where ID=:TaskID) as UpFlowID,"
							+ "(SELECT StatusText FROM {2}.rro_repairitem where ID=:TaskID) as StatusText,"
							+ "(SELECT CreateTime FROM {2}.rro_repairitem where ID=:TaskID) as StartTime,"
							+ "(SELECT SubmitTime FROM {2}.rro_repairitem where ID=:TaskID) as EndTime,"
							+ "(SELECT ItemLogo FROM {2}.rro_repairitem where ID=:TaskID) as ItemLogo,"
							+ "(SELECT Content FROM {2}.rro_repairitem where ID=:TaskID) as Content;",
					wInstance.Result, wInstance1.Result, wInstance2.Result);
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskStepID", wAPSTaskStepID);
			wParamMap.put("TaskID", wTaskID);

			wSQL = DMLChange(wSQL);
			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				wResult.CustomerName = StringUtils.parseString(wMap.get("CustomerName"));
				wResult.EndTime = StringUtils.parseCalendar(wMap.get("EndTime"));
				wResult.LineName = StringUtils.parseString(wMap.get("LineName"));
				wResult.PartNo = StringUtils.parseString(wMap.get("PartNo"));
				wResult.ProductNo = StringUtils.parseString(wMap.get("ProductNo"));
				wResult.StartTime = StringUtils.parseCalendar(wMap.get("StartTime"));
				wResult.StationName = StringUtils.parseString(wMap.get("StationName"));
				wResult.StepName = StringUtils.parseString(wMap.get("StepName"));
				wResult.TaskID = wTaskID;
				wResult.Persons = QMSConstants.GetBMSEmployeeName(StringUtils.parseInt("UpFlowID"));
				wResult.StatusText = StringUtils.parseString(wMap.get("StatusText"));
				wResult.Remark = StringUtils.Format("【{0}】{1}", StringUtils.parseString(wMap.get("ItemLogo")),
						StringUtils.parseString(wMap.get("Content")));

				wResult.Type = SFCSequentialInfoType.Repair.getValue();
				wResult.TypeText = SFCSequentialInfoType.Repair.getLable();

				return wResult;
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取人员名称
	 */
	private String GetUserNames(List<Integer> wList) {
		String wResult = "";
		try {
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			List<String> wNameList = new ArrayList<>();

			for (Integer wUserID : wList) {
				String wTemp = QMSConstants.GetBMSEmployeeName(wUserID.intValue());
				if (StringUtils.isNotEmpty(wTemp)) {
					wNameList.add(wTemp);
				}
			}

			if (wNameList.size() > 0) {
				wResult = StringUtils.Join(",", wNameList);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据检验单列表获取工序任务集合
	 */
	public List<APSTaskStep> SFC_SelectAPSTaskStepList(BMSEmployee wLoginUser, List<SFCTaskIPT> wList,
			OutResult<Integer> wErrorCode) {
		List<APSTaskStep> wResult = new ArrayList<APSTaskStep>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			String wIDs = StringUtils.Join(",", wList.stream().map(p -> p.ID).collect(Collectors.toList()));

			String wSQL = StringUtils.Format(
					"select t1.ID,t1.OperatorList,t3.DepartmentID from {0}.aps_taskstep t1,"
							+ "{0}.sfc_taskstep t2,{2}.mbs_user t3 "
							+ "where t1.ID=t2.TaskStepID and t2.MonitorID=t3.ID and t1.ID "
							+ "in(SELECT distinct(TaskStepID) FROM {2}.sfc_taskipt where ID in ({1}) );",
					wInstance.Result, wIDs, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<>();

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("ID"));
				int wDepartmentID = StringUtils.parseInt(wMap.get("DepartmentID"));
				String wOperatorList = StringUtils.parseString(wMap.get("OperatorList"));
				if (wID <= 0) {
					continue;
				}

				APSTaskStep wAPSTaskStep = new APSTaskStep();
				wAPSTaskStep.ID = wID;
				wAPSTaskStep.ShiftID = wDepartmentID;
				if (StringUtils.isNotEmpty(wOperatorList)) {
					wAPSTaskStep.OperatorList = StringUtils.parseIntList(wOperatorList.split(",|;"));
				}
				wResult.add(wAPSTaskStep);
			}

			// ID去重
			wResult = new ArrayList<APSTaskStep>(wResult.stream()
					.collect(Collectors.toMap(APSTaskStep::getID, account -> account, (k1, k2) -> k2)).values());

//			if (wResult.size() > 0) {
//				wResult = new ArrayList<APSTaskStep>(wResult.stream()
//						.collect(Collectors.toMap(APSTaskStep::getID, account -> account, (k1, k2) -> k2)).values());
//			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据自检单列表获取互检单列表
	 */
	public List<SFCTaskIPT> SFC_SelectSelfTaskIPTList(BMSEmployee wLoginUser, List<SFCTaskIPT> wList,
			OutResult<Integer> wErrorCode) {
		List<SFCTaskIPT> wResult = new ArrayList<SFCTaskIPT>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			String wIDs = StringUtils.Join(",", wList.stream().map(p -> p.ID).collect(Collectors.toList()));

			String wSQL = StringUtils.Format("select ID,TaskStepID,OperatorList,PicUri from "
					+ "{0}.sfc_taskipt where TaskStepID in(  SELECT distinct TaskStepID "
					+ "FROM {0}.sfc_taskipt where ID in({1}) ) and TaskType=6;", wInstance.Result, wIDs);

			Map<String, Object> wParamMap = new HashMap<>();

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("ID"));
				int wTaskStepID = StringUtils.parseInt(wMap.get("TaskStepID"));
				String wOperatorList = StringUtils.parseString(wMap.get("OperatorList"));
				String wPicUri = StringUtils.parseString(wMap.get("PicUri"));
				if (wID <= 0) {
					continue;
				}

				SFCTaskIPT wSFCTaskIPT = new SFCTaskIPT();
				wSFCTaskIPT.ID = wID;
				wSFCTaskIPT.TaskStepID = wTaskStepID;
				wSFCTaskIPT.PicUri = wPicUri;
				if (StringUtils.isNotEmpty(wOperatorList)) {
					wSFCTaskIPT.OperatorList = StringUtils.parseIntList(wOperatorList.split(",|;"));
				}
				wResult.add(wSFCTaskIPT);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询未关闭的派工消息集合
	 */
	public List<Integer> SFC_QueryDispatchMessageIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT t1.ID FROM {0}.bfc_message t1,{1}.sfc_taskstep t2 "
					+ " where t1.MessageID=t2.ID and t1.Type=2 and t2.IsStartWork in(1,2,3) "
					+ " and t1.ModuleID=8103 and t1.Active in (0,1,2);", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<>();

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("ID"));
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
	 * 查询未关闭的预检消息集合
	 */
	public List<Integer> SFC_QueryPreCheckMessageIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select t1.ID from {0}.bfc_message t1,{0}.sfc_taskipt t2 "
					+ "where t1.MessageID=t2.ID and t2.Status =2 and t1.Type=2 and t1.ModuleID=8114 and t1.Active in (0,1,2);",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<>();

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wID = StringUtils.parseInt(wMap.get("ID"));
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
	 * 自动关闭 自检、互检、专检任务完成的相关消息
	 */
	public void SFC_CloseRelaMessage(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return;
			}

			Map<String, Object> wParamMap = new HashMap<>();

			// 关闭自检 互检 专检消息
			String wSQL = StringUtils.Format(
					"update {0}.bfc_message t1 inner join {0}.sfc_taskipt t2"
							+ " on t1.MessageID=t2.ID and t2.Status=2  set t1.Active=4 where t1.ID>0 and t2.ID>0"
							+ " and t1.Type=2 and t1.ModuleID IN (1003,8112,8113,8114) and t1.Active IN (0,1,2) ;",
					wInstance.Result);
			wSQL = DMLChange(wSQL);
			this.nameJdbcTemplate.update(wSQL, wParamMap);
			// 关闭派工消息
			List<Integer> wIDList = SFC_QueryDispatchMessageIDList(wLoginUser, wErrorCode);
			if (wIDList.size() > 0) {
				wSQL = CloseMessageByIDList(wInstance, wIDList);
				this.nameJdbcTemplate.update(wSQL, wParamMap);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取关闭消息的SQL
	 */
	private String CloseMessageByIDList(ServiceResult<String> wInstance, List<Integer> wIDList) {
		String wSQL = "";
		try {
			wSQL = StringUtils.Format("update {0}.bfc_message set Active=4 where ID in({1}) and ID>0;",
					wInstance.Result, StringUtils.Join(",", wIDList));

			wSQL = DMLChange(wSQL);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wSQL;
	}

	/**
	 * 判断专检是否完全完工
	 */
	public boolean JudgeSpecialTaskIsFinished(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select (select count(*) from {1}.ipt_itemrecord t1,{0}.sfc_taskipt t2 "
							+ "where t1.VID=t2.ModuleVersionID and t1.ItemType !=4 and t2.ID=:TaskID) TSize,"
							+ "(select count(distinct(t1.IPTItemID)) from {1}.ipt_value t1,"
							+ "{0}.sfc_taskipt t2 where t1.TaskID=t2.ID and t2.ID=:TaskID and t1.Status=2) FSize;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskID", wSFCTaskIPT.ID);

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wTSize = StringUtils.parseInt(wMap.get("TSize"));
				int wFSize = StringUtils.parseInt(wMap.get("FSize"));
				if (wFSize >= wTSize) {
					wResult = true;
				} else {
					wResult = false;
				}
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断互检任务是否最终完工
	 */
	public boolean JudgeMutualTaskIsFinished(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			// 判断工位是否转序控制，若转序控制，直接返回false
			if (IsTurnOrderControl(wLoginUser, wSFCTaskIPT, wErrorCode)) {
				return false;
			}

			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select (select count(*) from {1}.ipt_itemrecord t1,{0}.sfc_taskipt t2 "
							+ "where t1.VID=t2.ModuleVersionID and t1.ItemType !=4 and t2.ID=:TaskID) TSize,"
							+ "(select count(distinct(t1.IPTItemID)) from {1}.ipt_value t1,"
							+ "{0}.sfc_taskipt t2 where t1.TaskID=t2.ID and t2.ID=:TaskID and t1.Status=2) FSize;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskID", wSFCTaskIPT.ID);

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wTSize = StringUtils.parseInt(wMap.get("TSize"));
				int wFSize = StringUtils.parseInt(wMap.get("FSize"));
				if (wFSize >= wTSize) {
					wResult = true;
				} else {
					wResult = false;
				}
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断工位是否设置了转序控制
	 */
	public boolean IsTurnOrderControl(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, OutResult<Integer> wErrorCode) {
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

			String wSQL = StringUtils.Format(
					"select ChangeControl from {0}.fpc_routepart where "
							+ "routeid in (select routeid from {1}.oms_order where id=:OrderID) and partid=:PartID;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wSFCTaskIPT.OrderID);
			wParamMap.put("PartID", wSFCTaskIPT.StationID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wChangeControl = StringUtils.parseInt(wReader.get("ChangeControl"));
				return wChangeControl == 1 ? true : false;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询检验任务的状态
	 */
	public int QueryTaskIPTStatus(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance1.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			String wSQL = "";
			switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
			case SelfCheck:
			case PreCheck:
			case Final:
			case OutPlant:
				wSQL = StringUtils.Format(
						"select (select count(*) from {1}.ipt_itemrecord t1,{0}.sfc_taskipt t2 "
								+ "where t1.VID=t2.ModuleVersionID and t1.ItemType !=4 and t2.ID=:TaskID) TSize,"
								+ "(select count(distinct(t1.IPTItemID)) from {1}.ipt_value t1,"
								+ "{0}.sfc_taskipt t2 where t1.TaskID=t2.ID and t2.ID=:TaskID and t1.Status=2) FSize;",
						wInstance.Result, wInstance1.Result);
				break;
			case MutualCheck:
				wSQL = StringUtils.Format("select (select Count(distinct(IPTItemID)) from {1}.ipt_value where TaskID "
						+ "in(SELECT ID FROM {0}.sfc_taskipt where TaskStepID=:TaskStepID and TaskType=6) "
						+ "and Status=2) TSize," + "(select count(distinct(t1.IPTItemID)) from {1}.ipt_value t1,"
						+ "{0}.sfc_taskipt t2 where t1.TaskID=t2.ID and t2.ID=:TaskID and t1.Status=2)  FSize;",
						wInstance.Result, wInstance1.Result);
				break;
			case SpecialCheck:
				wSQL = StringUtils.Format("select (select Count(distinct(IPTItemID)) from {1}.ipt_value where TaskID "
						+ "in(SELECT ID FROM {0}.sfc_taskipt where TaskStepID=:TaskStepID and TaskType=12) "
						+ "and Status=2) TSize," + "(select count(distinct(t1.IPTItemID)) from {1}.ipt_value t1,"
						+ "{0}.sfc_taskipt t2 where t1.TaskID=t2.ID and t2.ID=:TaskID and t1.Status=2)  FSize;",
						wInstance.Result, wInstance1.Result);
				break;
			default:
				break;
			}

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("TaskID", wSFCTaskIPT.ID);
			wParamMap.put("TaskStepID", wSFCTaskIPT.TaskStepID);

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wMap : wQueryResult) {
				int wTSize = StringUtils.parseInt(wMap.get("TSize"));
				int wFSize = StringUtils.parseInt(wMap.get("FSize"));
				if (wFSize >= wTSize) {
					wResult = 2;
				} else {
					wResult = 1;
				}
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 更新相关任务状态
	 */
	public void UpdateMessageStatus(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return;
			}

			int wActive = 0;
			if (wSFCTaskIPT.Status == 1) {
				wActive = 1;
			} else if (wSFCTaskIPT.Status == 2) {
				wActive = 3;
			} else if (wSFCTaskIPT.Status == 3) {
				wActive = 4;
			}

			String wSQL = "";
			switch (SFCTaskType.getEnumType(wSFCTaskIPT.TaskType)) {
			case SelfCheck:
				wSQL = StringUtils.Format("update {0}.bfc_message set Active=:Active where MessageID=:TaskID "
						+ "and ModuleID=1003 and ID>0;", wInstance.Result);
				break;
			case MutualCheck:
				wSQL = StringUtils.Format("update {0}.bfc_message set Active=:Active where MessageID=:TaskID "
						+ "and ModuleID=8112 and ID>0;", wInstance.Result);
				break;

			case SpecialCheck:
				wSQL = StringUtils.Format("update {0}.bfc_message set Active=:Active where MessageID=:TaskID "
						+ "and ModuleID=8113 and ID>0;", wInstance.Result);
				break;

			case PreCheck:
				wSQL = StringUtils.Format("update {0}.bfc_message set Active=:Active where MessageID=:TaskID "
						+ "and ModuleID=8114 and ID>0;", wInstance.Result);
				break;
			case Final:
				wSQL = StringUtils.Format("update {0}.bfc_message set Active=:Active where MessageID=:TaskID "
						+ "and ModuleID=8117 and ID>0;", wInstance.Result);
				break;
			case OutPlant:
				wSQL = StringUtils.Format("update {0}.bfc_message set Active=:Active where MessageID=:TaskID "
						+ "and ModuleID=8118 and ID>0;", wInstance.Result);
				break;
			default:
				break;
			}

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("Active", wActive);
			wParamMap.put("TaskID", wSFCTaskIPT.ID);
//			wParamMap.put("UserID", wLoginUser.ID);

			wSQL = DMLChange(wSQL);

			this.nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 根据预检单获取该车该工位所有工序任务
	 */
	public List<APSTaskStep> SelectTaskStepListByTaskIPT(BMSEmployee wAdminUser, SFCTaskIPT wSFCTaskIPT,
			OutResult<Integer> wErrorCode) {
		List<APSTaskStep> wResult = new ArrayList<APSTaskStep>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wAdminUser.getCompanyID(), MESDBSource.APS,
					wAdminUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wAdminUser.getCompanyID(), MESDBSource.Basic,
					wAdminUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT TaskPartID,StepID,Status from {0}.aps_taskstep where "
							+ " TaskPartID in(  SELECT t1.TaskPartID FROM {0}.aps_taskstep t1,"
							+ "{1}.sfc_taskipt t2 where t1.ID=t2.TaskStepID and t2.ID=:TaskIPTID);",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("TaskIPTID", wSFCTaskIPT.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				APSTaskStep wItem = new APSTaskStep();

				wItem.TaskPartID = StringUtils.parseInt(wReader.get("TaskPartID"));
				wItem.StepID = StringUtils.parseInt(wReader.get("StepID"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询问题单的派工任务集合
	 */
	public List<SFCTaskStep> SelectTaskStepProblem(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT,
			OutResult<Integer> wErrorCode) {
		List<SFCTaskStep> wResult = new ArrayList<SFCTaskStep>();
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
					"select t1.* from {0}.sfc_taskstep t1,{1}.sfc_taskipt t2 "
							+ "where t1.TaskStepID=t2.TaskStepID and t2.ID=:TaskIPTID and t1.Type=2;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("TaskIPTID", wSFCTaskIPT.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCTaskStep wItem = new SFCTaskStep();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID"));
				wItem.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wItem.WorkHour = StringUtils.parseDouble(wReader.get("WorkHour"));
				wItem.MonitorID = StringUtils.parseInt(wReader.get("MonitorID"));
				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.ReadyTime = StringUtils.parseCalendar(wReader.get("ReadyTime"));
				wItem.IsStartWork = StringUtils.parseInt(wReader.get("IsStartWork"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));
				wItem.RealHour = StringUtils.parseDouble(wReader.get("RealHour"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 
	 */
	public List<SFCTaskStep> SFC_QueryProblemTaskStepList(BMSEmployee wLoginUser, List<Integer> wTaskIPTIDList,
			OutResult<Integer> wErrorCode) {
		List<SFCTaskStep> wResult = new ArrayList<SFCTaskStep>();
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

			if (wTaskIPTIDList == null || wTaskIPTIDList.size() <= 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT t1.* FROM {0}.sfc_taskstep t1,{2}.sfc_taskipt t2 "
							+ "where t1.TaskStepID=t2.TaskStepID and t2.Type=2 and t1.Type=2 and t2.ID in ({1});",
					wInstance.Result, StringUtils.Join(",", wTaskIPTIDList), wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCTaskStep wItem = new SFCTaskStep();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID"));
				wItem.MonitorID = StringUtils.parseInt(wReader.get("MonitorID"));
				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工序任务ID获取所有班组长
	 */
	public String GetMonitorsByAPSTaskStepID(BMSEmployee wLoginUser, int wAPSTaskStepID,
			OutResult<Integer> wErrorCode) {
		String wResult = "";
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
					"select t1.Name from {0}.mbs_user t1,{0}.bms_position t2 "
							+ "where t1.Position=t2.ID and t2.DutyID=1 and t1.DepartmentID in "
							+ "(SELECT DepartmentID FROM {0}.mbs_user "
							+ "where ID in (   SELECT MonitorID FROM {1}.sfc_taskstep where TaskStepID=:TaskStepID));",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("TaskStepID", wAPSTaskStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			List<String> wNames = new ArrayList<String>();
			for (Map<String, Object> wReader : wQueryResult) {
				String wName = StringUtils.parseString(wReader.get("Name"));

				if (StringUtils.isNotEmpty(wName)) {
					wNames.add(wName);
				}
			}
			if (wNames.size() > 0) {
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据自检单ID禁用互检消息
	 */
	public void DisMutualMessage(BMSEmployee wLoginUser, int wSFCTaskIPTID, OutResult<Integer> wErrorCode) {
		try {
			int wID = GetMutualMessageID(wLoginUser, wSFCTaskIPTID, wErrorCode);
			if (wID > 0) {
				DisActiveMessage(wLoginUser, wID, wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public int GetMutualMessageID(BMSEmployee wLoginUser, int wSFCTaskIPTID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT ID FROM {0}.bfc_message where ModuleID=8112 "
					+ "and MessageID in(select ID from {0}.sfc_taskipt where TaskStepID "
					+ "in(SELECT TaskStepID FROM {0}.sfc_taskipt where ID=:SFCTaskIPTID) and TaskType=12) "
					+ "and ResponsorID=:UserID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("SFCTaskIPTID", wSFCTaskIPTID);
			wParamMap.put("UserID", wLoginUser.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("ID"));

				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public void DisActiveMessage(BMSEmployee wLoginUser, int wMessageID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("update {0}.bfc_message set Active=4 where ID = :MessageID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("MessageID", wMessageID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public List<FPCPartPoint> GetStepListByPartType(BMSEmployee wLoginUser, int wPartType,
			OutResult<Integer> wErrorCode) {
		List<FPCPartPoint> wResult = new ArrayList<FPCPartPoint>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT distinct UnitID FROM {0}.fmc_lineunit "
					+ "where LevelID=3 and ParentUnitID in (SELECT ID FROM {0}.fpc_part where PartType=:wPartType);",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wPartType", wPartType);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wUnitID = StringUtils.parseInt(wReader.get("UnitID"));
				if (wUnitID > 0) {
					FPCPartPoint wStep = QMSConstants.GetFPCStep(wUnitID);
					if (wStep != null && wStep.ID > 0) {
						wResult.add(wStep);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询focas报表统计数据
	 */
	public FocasReport SFC_QueryFocasData(BMSEmployee wLoginUser, Calendar wStartYear, Calendar wEndYear,
			Calendar wStartMonth, Calendar wEndMonth, OutResult<Integer> wErrorCode) {
		FocasReport wResult = new FocasReport();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select "
					+ "	(SELECT count(*)  FROM {0}.oms_order where RealFinishDate >= :wStartYear and RealFinishDate <= :wEndYear and Status in (5,6,7,8)"
					+ ") JCJG_Total_Year,"
					+ "    (SELECT count(*)  FROM {0}.oms_order where RealFinishDate >= :wStartYear and RealFinishDate <= :wEndYear and Status in (5,6,7,8) and LineID=2"
					+ ") JCJG_C6_Year,"
					+ "    (SELECT count(*)  FROM {0}.oms_order where RealFinishDate >= :wStartYear and RealFinishDate <= :wEndYear and Status in (5,6,7,8) and LineID=1"
					+ ") JCJG_C5_Year,"
					+ "    (SELECT avg(datediff( RealFinishDate,RealReceiveDate))  FROM {0}.oms_order where RealFinishDate >= :wStartYear and RealFinishDate <= :wEndYear and Status in (5,6,7,8) and LineID=2"
					+ ") JCTS_C6_Year,"
					+ "    (SELECT avg(datediff( RealFinishDate,RealReceiveDate))  FROM {0}.oms_order where RealFinishDate >= :wStartYear and RealFinishDate <= :wEndYear and Status in (5,6,7,8) and LineID=1"
					+ ") JCTS_C5_Year,"
					+ "    (SELECT count(*)  FROM {0}.oms_order where RealReceiveDate <= :wEndMonth and Status in (4)"
					+ ") ZXJC_Month,"
					+ "    (SELECT count(*)  FROM {0}.oms_order where RealReceiveDate <= :wEndMonth and Status in (3,4,5,6,7)"
					+ ") ZCJC_Month,"
					+ "    (SELECT count(*)  FROM {0}.oms_order where RealFinishDate >= :wStartMonth and RealFinishDate <= :wEndMonth and Status in (5,6,7,8)"
					+ ") JCJG_Total_Month,"
					+ "    (SELECT count(*)  FROM {0}.oms_order where RealFinishDate >= :wStartMonth and RealFinishDate <= :wEndMonth and Status in (5,6,7,8) and LineID=2"
					+ ") JCJG_C6_Month,"
					+ "    (SELECT count(*)  FROM {0}.oms_order where RealFinishDate >= :wStartMonth and RealFinishDate <= :wEndMonth and Status in (5,6,7,8) and LineID=1"
					+ ") JCJG_C5_Month;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wStartYear", wStartYear);
			wParamMap.put("wEndYear", wEndYear);
			wParamMap.put("wStartMonth", wStartMonth);
			wParamMap.put("wEndMonth", wEndMonth);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult.setJCJG_Total_Year(StringUtils.parseInt(wReader.get("JCJG_Total_Year")));
				wResult.setJCJG_C6_Year(StringUtils.parseInt(wReader.get("JCJG_C6_Year")));
				wResult.setJCJG_C5_Year(StringUtils.parseInt(wReader.get("JCJG_C5_Year")));
				wResult.setJCTS_C6_Year(StringUtils.parseDouble(wReader.get("JCTS_C6_Year")).intValue());
				wResult.setJCTS_C5_Year(StringUtils.parseDouble(wReader.get("JCTS_C5_Year")).intValue());

				AndonLocomotiveProductionStatus wInfo = LOCOAPSServiceImpl.getInstance()
						.Andon_QueryProductStatus(wLoginUser).Info(AndonLocomotiveProductionStatus.class);
				if (wInfo != null) {
					wResult.setJCJG_Total_Year(wInfo.LJXJ_Year);
					wResult.setJCJG_C6_Year(wInfo.LJXJ_Year_C6);
					wResult.setJCJG_C5_Year(wInfo.LJXJ_Year_C5);
					wResult.setJCTS_C6_Year(wInfo.JCTS_C6);
					wResult.setJCTS_C5_Year(wInfo.JCTS_C5);
				}

				wResult.setZXJC_Month(StringUtils.parseInt(wReader.get("ZXJC_Month")));
				wResult.setZCJC_Month(StringUtils.parseInt(wReader.get("ZCJC_Month")));
				wResult.setJCJG_Total_Month(StringUtils.parseInt(wReader.get("JCJG_Total_Month")));
				wResult.setJCJG_C6_Month(StringUtils.parseInt(wReader.get("JCJG_C6_Month")));
				wResult.setJCJG_C5_Month(StringUtils.parseInt(wReader.get("JCJG_C5_Month")));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public Integer MSS_QueryPageNumber(BMSEmployee wLoginUser, String wMaterialNo, String wMaterialName,
			OutResult<Integer> wErrorCode) {
		Integer wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select count(*) as Number from {0}.mss_material where MaterialNo like ''%{1}%'' and MaterialName like ''%{2}%'';",
					wInstance.Result, wMaterialNo, wMaterialName);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

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

	/**
	 * 获取物料分页数据
	 */
	public List<MSSMaterial> MSS_QueryPageAll(BMSEmployee wLoginUser, int wPageSize, int wCurPage, String wMaterialName,
			String wMaterialNo, OutResult<Integer> wErrorCode) {
		List<MSSMaterial> wResult = new ArrayList<MSSMaterial>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select * from {0}.mss_material where MaterialNo like ''%{1}%'' and MaterialName like ''%{2}%'' limit {3},{4};",
					wInstance.Result, wMaterialNo, wMaterialName, (wCurPage - 1) * wPageSize, wPageSize);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				MSSMaterial wItem = new MSSMaterial();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Name = StringUtils.parseString(wReader.get("Name"));
				wItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));
				wItem.OldMaterialNo = StringUtils.parseString(wReader.get("OldMaterialNo"));
				wItem.CYUnitID = StringUtils.parseInt(wReader.get("CYUnitID"));
				wItem.TypeID = StringUtils.parseInt(wReader.get("TypeID"));
				wItem.MaterialGroup = StringUtils.parseString(wReader.get("MaterialGroup"));
				wItem.Groes = StringUtils.parseString(wReader.get("Groes"));
				wItem.Normt = StringUtils.parseString(wReader.get("Normt"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.NetWeight = StringUtils.parseDouble(wReader.get("NetWeight"));
				wItem.GrossWeight = StringUtils.parseDouble(wReader.get("GrossWeight"));
				wItem.ZLUnitID = StringUtils.parseInt(wReader.get("ZLUnitID"));
				wItem.AuthorID = StringUtils.parseInt(wReader.get("AuthorID"));
				wItem.AuditorID = StringUtils.parseInt(wReader.get("AuditorID"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.StockID = StringUtils.parseInt(wReader.get("StockID"));
				wItem.LocationID = StringUtils.parseInt(wReader.get("LocationID"));
				wItem.BatchEnable = StringUtils.parseInt(wReader.get("BatchEnable"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.AuditTime = StringUtils.parseCalendar(wReader.get("AuditTime"));
				wItem.SafeFQTY = StringUtils.parseFloat(wReader.get("SafeFQTY"));
				wItem.ShiftFQTY = StringUtils.parseFloat(wReader.get("ShiftFQTY"));
				wItem.SafeMode = StringUtils.parseInt(wReader.get("SafeMode"));
				wItem.BuyDays = StringUtils.parseInt(wReader.get("BuyDays"));
				wItem.BOMID = StringUtils.parseInt(wReader.get("BOMID"));
				wItem.BoxTypeID = StringUtils.parseInt(wReader.get("BoxTypeID"));
				wItem.BoxFQTY = StringUtils.parseFloat(wReader.get("BoxFQTY"));
				wItem.SupplierID = StringUtils.parseInt(wReader.get("SupplierID"));
				wItem.LocationBoxs = StringUtils.parseInt(wReader.get("LocationBoxs"));
				wItem.CGUnitID = StringUtils.parseInt(wReader.get("CGUnitID"));
				wItem.SCUnitID = StringUtils.parseInt(wReader.get("SCUnitID"));
				wItem.XSUnitID = StringUtils.parseInt(wReader.get("XSUnitID"));
				wItem.KCUnitID = StringUtils.parseInt(wReader.get("KCUnitID"));
				wItem.ERPMaterialID = StringUtils.parseInt(wReader.get("ERPMaterialID"));
				wItem.CheckVersionID = StringUtils.parseInt(wReader.get("CheckVersionID"));
				wItem.MaterialType = StringUtils.parseString(wReader.get("MaterialType"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 关闭所有未关闭的派工消息
	 */
	public void CloseAllDispatchMessage(BMSEmployee wLoginUser, int taskStepID, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			List<Integer> wNotCloseIDList = QueryNotCloseDispathMessageIDList(wLoginUser, taskStepID, wErrorCode);

			if (wNotCloseIDList.size() <= 0) {
				return;
			}

			String wSQL = StringUtils.Format("update {0}.bfc_message set Active=4 where ID in ({1});", wInstance.Result,
					StringUtils.Join(",", wNotCloseIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private List<Integer> QueryNotCloseDispathMessageIDList(BMSEmployee wLoginUser, int taskStepID,
			OutResult<Integer> wErrorCode) {
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

			String wSQL = StringUtils.Format("select ID from {0}.bfc_message where MessageID "
					+ "in (SELECT ID FROM {1}.sfc_taskstep where TaskStepID=:TaskStepID) "
					+ "and ModuleID=8103 and Active in (0,1,2);", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("TaskStepID", taskStepID);

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
	 * 根据时间段查询完工订单ID集合
	 */
	public List<Integer> SelectOrderIDList(BMSEmployee wLoginUser, Calendar wStartYear, Calendar wEndYear, int wLineID,
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
					"SELECT ID FROM {0}.oms_order where Status in (5,6,7,8) "
							+ "and RealFinishDate > :wStartYear and RealFinishDate < :wEndYear and LineID=:wLineID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wStartYear", wStartYear);
			wParamMap.put("wEndYear", wEndYear);
			wParamMap.put("wLineID", wLineID);

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

	public Map<Integer, Integer> SelectRepairMap(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		Map<Integer, Integer> wResult = new HashMap<Integer, Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT OrderID,count(*) Size FROM {0}.rro_repairitem "
					+ "where Status not in (0,21) and FlowType != 5010 group by OrderID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wOrderID = StringUtils.parseInt(wReader.get("OrderID"));
				int wSize = StringUtils.parseInt(wReader.get("Size"));
				wResult.put(wOrderID, wSize);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public Map<Integer, Integer> SelectYSRepairMap(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		Map<Integer, Integer> wResult = new HashMap<Integer, Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select OrderID,count(*) Size FROM {0}.rro_repairitem where "
					+ "FlowType=5010 and Status not in (0,21) group by OrderID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wOrderID = StringUtils.parseInt(wReader.get("OrderID"));
				int wSize = StringUtils.parseInt(wReader.get("Size"));
				wResult.put(wOrderID, wSize);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 绑定新的订单
	 */
	public void BindingParentOrder(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, List<IPTValue> wIPTValueList) {
		try {
			for (IPTValue wIPTValue : wIPTValueList) {
				if (wIPTValue.OrderID <= 0) {
					continue;
				}

				// ①查询子订单
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wIPTValue.OrderID)
						.Info(OMSOrder.class);
				if (wOrder == null || wOrder.ID <= 0) {
					continue;
				}
				// ②绑定主订单
				wOrder.ParentID = wSFCTaskIPT.OrderID;
				LOCOAPSServiceImpl.getInstance().OMS_UpdateOrder(wLoginUser, wOrder);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 判断终检是否完成
	 */
	public boolean JudgeFinalCheckIsOK(BMSEmployee wLoginUser, int stationID, int orderID,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select count(*) Number from {0}.aps_taskstep "
					+ "where OrderID=:OrderID and PartID=:PartID and Status !=5;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", orderID);
			wParamMap.put("PartID", stationID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wNumber = StringUtils.parseInt(wReader.get("Number"));
				if (wNumber <= 0) {
					return true;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询工序检验员
	 */
	public List<Integer> SelectStepCheckerIDList(BMSEmployee wLoginUser, int stationID, int taskStepID,
			OutResult<Integer> wErrorCode) {
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

			String wSQL = StringUtils.Format("SELECT CheckerList FROM {0}.bms_workcharge where ClassID "
					+ "in (select DepartmentID from {0}.mbs_user where ID "
					+ "in (  SELECT MonitorID FROM {1}.sfc_taskstep where TaskStepID=:wTaskStepID)) "
					+ "and StationID=:wStationID and Active=1;", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wTaskStepID", taskStepID);
			wParamMap.put("wStationID", stationID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				String wCheckList = StringUtils.parseString(wReader.get("CheckerList"));
				String[] wStrs = wCheckList.split(",");
				for (String wStr : wStrs) {
					Integer wUserID = StringUtils.parseInt(wStr);
					if (wUserID > 0) {
						wResult.add(wUserID);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询未完成的专检任务ID集合
	 */
	public List<Integer> SFC_SelectNotFinishSpecialTaskIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
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

			String wSQL = StringUtils.Format("SELECT t1.ID FROM {0}.sfc_taskipt t1 where t1.Status=1 and"
					+ "					 t1.TaskType=13 and find_in_set(:wUserID, (SELECT CheckerList "
					+ "FROM {0}.bms_workcharge where ClassID in (select DepartmentID from {0}.mbs_user "
					+ "where ID in (  SELECT MonitorID FROM {1}.sfc_taskstep where TaskStepID=t1.TaskStepID)) "
					+ "and StationID=t1.StationID and Active=1 limit 1))" + "					 group by t1.ID;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wUserID", wLoginUser.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wTaskID = StringUtils.parseInt(wReader.get("ID"));
				if (wTaskID > 0) {
					wResult.add(wTaskID);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取标准、填写图片字典
	 */
	public Map<Integer, Integer> GetStandardIDMap(BMSEmployee wLoginUser, List<Integer> wIDList,
			OutResult<Integer> wErrorCode) {
		Map<Integer, Integer> wResult = new HashMap<Integer, Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIDList == null || wIDList.size() <= 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT ID,IsPic FROM {0}.ipt_standard where ID in ({1});",
					wInstance.Result, StringUtils.Join(",", wIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				int wID = StringUtils.parseInt(wReader.get("ID"));
				int wIsPic = StringUtils.parseInt(wReader.get("IsPic"));

				wResult.put(wID, wIsPic);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据工艺路线ID集合获取工艺工位集合
	 */
	public List<FPCRoutePart> SelectRoutePartListByRouteIDList(BMSEmployee wLoginUser, List<Integer> wRouteIDList,
			OutResult<Integer> wErrorCode) {
		List<FPCRoutePart> wResult = new ArrayList<FPCRoutePart>();
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

			String wSQL = StringUtils.Format("SELECT RouteID,PartID FROM {0}.fpc_routepart where RouteID in ({1});",
					wInstance.Result, StringUtils.Join(",", wRouteIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				FPCRoutePart wItem = new FPCRoutePart();

				wItem.RouteID = StringUtils.parseInt(wReader.get("RouteID"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取专检车统计数据
	 */
	public SFCTaskIPTPartNo GetSFCTaskIPTPartNo(BMSEmployee wLoginUser, OMSOrder wOMSOrder,
			List<FPCRoutePart> wMyRoutePartList, OutResult<Integer> wErrorCode) {
		SFCTaskIPTPartNo wResult = new SFCTaskIPTPartNo();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wMyRoutePartList == null || wMyRoutePartList.size() <= 0) {
				return wResult;
			}

//			String wSQL = StringUtils.Format(
//					"select (SELECT count(*) FROM {0}.fpc_routepartpoint where RouteID=:RouteID and PartID in ({1})) Total,"
//							+ "	(SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13) Report,"
//							+ "    (SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and Status=1) ToDo,"
//							+ "    (SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and Status=2) Done;",
//					wInstance.Result, StringUtils.Join(",",
//							wMyRoutePartList.stream().map(p -> p.PartID).distinct().collect(Collectors.toList())));

			String wSQL = StringUtils.Format(
					"select (SELECT count(*) FROM {0}.aps_taskstep where OrderID=:OrderID and PartID in ({1}) and Active=1 and find_in_set(''{2}'',MaterialNo)) Total,"
							+ "	(SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and find_in_set(''{2}'',CheckerList)) Report,"
							+ "    (SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and Status=1 and find_in_set(''{2}'',CheckerList)) ToDo,"
							+ "    (SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and Status=2 and find_in_set(''{2}'',CheckerList)) Done;",
					wInstance.Result,
					StringUtils.Join(",",
							wMyRoutePartList.stream().map(p -> p.PartID).distinct().collect(Collectors.toList())),
					String.valueOf(wLoginUser.ID));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("RouteID", wOMSOrder.RouteID);
			wParamMap.put("OrderID", wOMSOrder.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				wResult.FQTY = StringUtils.parseInt(wReader.get("Total"));
				wResult.FQTYTotal = StringUtils.parseInt(wReader.get("Report"));
				wResult.FQTYToDo = StringUtils.parseInt(wReader.get("ToDo"));
				wResult.FQTYDone = StringUtils.parseInt(wReader.get("Done"));

				wResult.Customer = wOMSOrder.Customer;
				wResult.LineName = wOMSOrder.LineName;
				wResult.No = wOMSOrder.PartNo.split("#")[1];
				wResult.OrderID = wOMSOrder.ID;
				wResult.OrderNo = wOMSOrder.OrderNo;
				wResult.PartNo = wOMSOrder.PartNo;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取专检工位统计数据
	 */
	public SFCTaskIPTPart GetSFCTaskIPTPart(BMSEmployee wLoginUser, OMSOrder wOrder, FPCRoutePart wFPCRoutePart,
			OutResult<Integer> wErrorCode) {
		SFCTaskIPTPart wResult = new SFCTaskIPTPart();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

//			String wSQL = StringUtils.Format(
//					"select (SELECT count(*) FROM {0}.fpc_routepartpoint where RouteID=:RouteID and PartID in ({1})) Total,"
//							+ "	(SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13) Report,"
//							+ "    (SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and Status=1) ToDo,"
//							+ "    (SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and Status=2) Done;",
//					wInstance.Result, String.valueOf(wFPCRoutePart.PartID));

			String wSQL = StringUtils.Format(
					"select (SELECT count(*) FROM {0}.aps_taskstep where OrderID=:OrderID and PartID in ({1}) and Active=1 and find_in_set(''{2}'',MaterialNo)) Total,"
							+ "	(SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and find_in_set(''{2}'',CheckerList)) Report,"
							+ "    (SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and Status=1 and find_in_set(''{2}'',CheckerList)) ToDo,"
							+ "    (SELECT count(*) FROM {0}.sfc_taskipt where OrderID=:OrderID and StationID in ({1}) and Active=1 and TaskType=13 and Status=2 and find_in_set(''{2}'',CheckerList)) Done;",
					wInstance.Result, String.valueOf(wFPCRoutePart.PartID), String.valueOf(wLoginUser.ID));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("RouteID", wOrder.RouteID);
			wParamMap.put("OrderID", wOrder.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult.FQTY = StringUtils.parseInt(wReader.get("Total"));
				wResult.FQTYTotal = StringUtils.parseInt(wReader.get("Report"));
				wResult.FQTYToDo = StringUtils.parseInt(wReader.get("ToDo"));
				wResult.FQTYDone = StringUtils.parseInt(wReader.get("Done"));

				wResult.Customer = wOrder.Customer;
				wResult.LineName = wOrder.LineName;
				wResult.PartID = wFPCRoutePart.PartID;
				wResult.PartName = QMSConstants.GetFPCPartName(wResult.PartID);
				wResult.OrderID = wOrder.ID;
				wResult.OrderNo = wOrder.OrderNo;
				wResult.PartNo = wOrder.PartNo;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<Integer> GetRepairingOrderIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT distinct OrderID FROM {0}.aps_taskstep t1,{0}.oms_order t2 where t1.OrderID=t2.ID "
							+ "and t2.Status=4 and t2.Active=1 and t2.RealReceiveDate > ''2021-08-29 00:00:00'';",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wID = StringUtils.parseInt(wReader.get("OrderID"));
				wResult.add(wID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 互检人
	 */
	public String GetMutualerByPic(BMSEmployee wLoginUser, SFCTaskIPT wSFCTaskIPT, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT OperatorList FROM {0}.sfc_taskipt " + "where TaskStepID=:TaskStepID and TaskType=12;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("TaskStepID", wSFCTaskIPT.TaskStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				String wOperatorList = StringUtils.parseString(wReader.get("OperatorList"));

				if (StringUtils.isEmpty(wOperatorList)) {
					return wResult;
				}

				List<Integer> wUserIDList = StringUtils.parseIntList(wOperatorList.split(","));
				wResult = GetUserNames(wUserIDList);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 删除专检单
	 */
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCTaskIPT> wList,
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
			for (SFCTaskIPT wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_taskipt WHERE ID IN({0}) ;",
					String.join(",", wIDList), wInstance.Result);
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	public String GetCheckerList(BMSEmployee wLoginUser, int taskStepID, int stationID, OutResult<Integer> wErrorCode) {
		String wResult = "";
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
					"select CheckerList from {0}.bms_workcharge where classid in (select DepartmentID from "
							+ "{0}.mbs_user where " + "id in (select MonitorID from {1}.sfc_taskstep "
							+ "where TaskStepID=:TaskStepID)) and StationID=:StationID and Active=1;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("TaskStepID", taskStepID);
			wParamMap.put("StationID", stationID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseString(wReader.get("CheckerList"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据订单获取车辆转序情况第一层数据
	 */
	public SFCTrainProgress01 GetSFCTrainProgress01(BMSEmployee wLoginUser, OMSOrder wOMSOrder,
			OutResult<Integer> wErrorCode) {
		SFCTrainProgress01 wResult = new SFCTrainProgress01();
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
					"select (select count(*) from {0}.fpc_routepart where routeid=:RouteID and ChangeControl not in (3)) StationNumber,"
							+ "	   (select count(*) from {1}.aps_taskpart where ShiftPeriod=5 and Active=1 and OrderID=:OrderID and Status=5) FinishedNumber;",
					wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("RouteID", wOMSOrder.RouteID);
			wParamMap.put("OrderID", wOMSOrder.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wStationNumber = StringUtils.parseInt(wReader.get("StationNumber"));
				int wFinishedNumber = StringUtils.parseInt(wReader.get("FinishedNumber"));

				wResult = new SFCTrainProgress01(wOMSOrder.ID, wOMSOrder.PartNo, wOMSOrder.LineName, wOMSOrder.Customer,
						wOMSOrder.ProductNo, wStationNumber, wFinishedNumber);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据订单，工位获取车辆转序情况第二层数据
	 */
	public SFCTrainProgress02 GetSFCTrainProgress02(BMSEmployee wLoginUser, OMSOrder wOrder, APSTaskPart wAPSTaskPart,
			OutResult<Integer> wErrorCode) {
		SFCTrainProgress02 wResult = new SFCTrainProgress02();
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

			ServiceResult<String> wInstance2 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance2.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select (SELECT Status FROM {0}.sfc_turnordertask where TaskPartID=:TaskPartID limit 1) IsTurnOrder,"
							+ "	   (SELECT Remark FROM {0}.sfc_turnordertask where TaskPartID=:TaskPartID limit 1) Remark,"
							+ "	   (select count(*) from {1}.aps_taskstep where TaskPartID=:TaskPartID and Active=1) StepNumber,"
							+ "	   (select count(*) from {1}.aps_taskstep where TaskPartID=:TaskPartID and Active=1 and Status = 5) StepFinishedNumber,"
							+ "	   (select count(*) from {2}.ncr_sendtask where OrderID=:OrderID and StationID=:StationID and Status >0) SendNCRNumber,"
							+ "	   (select count(*) from {2}.ncr_task where OrderID=:OrderID and StationID=:StationID and Status >0) NCRNumber,"
							+ "	   (select count(*) from {2}.ncr_sendtask where OrderID=:OrderID and StationID=:StationID and Status >0 and StatusText like ''%已%'') SendNCRFinishedNumber,"
							+ "	   (select count(*) from {2}.ncr_task where OrderID=:OrderID and StationID=:StationID and Status >0 and StatusText like ''%已%'') NCRFinishedNumber,"
							+ "	   (select count(*) from {2}.rro_repairitem where OrderID=:OrderID and StationID=:StationID and Status>0) RepairNumber,"
							+ "	   (select count(*) from {2}.rro_repairitem where OrderID=:OrderID and StationID=:StationID and Status>0 and StatusText like ''%已%'') RepairFinishedNumber;",
					wInstance.Result, wInstance1.Result, wInstance2.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("TaskPartID", wAPSTaskPart.ID);
			wParamMap.put("OrderID", wOrder.ID);
			wParamMap.put("StationID", wAPSTaskPart.PartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wIsTurnOrder = StringUtils.parseInt(wReader.get("IsTurnOrder"));
				String wRemark = StringUtils.parseString(wReader.get("Remark"));
				int wStepNumber = StringUtils.parseInt(wReader.get("StepNumber"));
				int wStepFinishedNumber = StringUtils.parseInt(wReader.get("StepFinishedNumber"));
				int wSendNCRNumber = StringUtils.parseInt(wReader.get("SendNCRNumber"));
				int wNCRNumber = StringUtils.parseInt(wReader.get("NCRNumber"));
				int wSendNCRFinishedNumber = StringUtils.parseInt(wReader.get("SendNCRFinishedNumber"));
				int wNCRFinishedNumber = StringUtils.parseInt(wReader.get("NCRFinishedNumber"));
				int wRepairNumber = StringUtils.parseInt(wReader.get("RepairNumber"));
				int wRepairFinishedNumber = StringUtils.parseInt(wReader.get("RepairFinishedNumber"));

				wResult = new SFCTrainProgress02(wOrder.ID, wOrder.PartNo, wOrder.LineName, wOrder.Customer,
						wOrder.ProductNo, wAPSTaskPart.PartID, wAPSTaskPart.PartName, wAPSTaskPart.Status, wIsTurnOrder,
						wRemark, wStepNumber, wStepFinishedNumber, wSendNCRNumber + wNCRNumber,
						wSendNCRFinishedNumber + wNCRFinishedNumber, wRepairNumber, wRepairFinishedNumber);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据订单，工位，工序获取车辆转序情况第三层数据
	 */
	public SFCTrainProgress03 GetSFCTrainProgress03(BMSEmployee wLoginUser, OMSOrder wOrder, APSTaskStep wAPSTaskStep,
			OutResult<Integer> wErrorCode) {
		SFCTrainProgress03 wResult = new SFCTrainProgress03();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			ServiceResult<String> wInstance2 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance2.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select (select count(*) from {1}.ncr_sendtask where OrderID=:OrderID and StationID=:StationID and TaskStepID=:TaskStepID and Status >0) SendNCRNumber,"
							+ "	   (select count(*) from {1}.ncr_task where OrderID=:OrderID and StationID=:StationID and ProcessID=:ProcessID and Status >0) NCRNumber,"
							+ "       (select count(*) from {1}.ncr_sendtask where OrderID=:OrderID and StationID=:StationID and TaskStepID=:TaskStepID and Status >0 and StatusText like ''%已%'') SendNCRFinishedNumber,"
							+ "       (select count(*) from {1}.ncr_task where OrderID=:OrderID and StationID=:StationID and ProcessID=:ProcessID and Status >0 and StatusText like ''%已%'') NCRFinishedNumber,"
							+ "       (select count(*) from {1}.rro_repairitem where OrderID=:OrderID and StationID=:StationID and ProcessID=:ProcessID and Status>0) RepairNumber,"
							+ "       (select count(*) from {1}.rro_repairitem where OrderID=:OrderID and StationID=:StationID and ProcessID=:ProcessID and Status>0 and StatusText like ''%已%'') RepairFinishedNumber,"
							+ "       (select ID from {0}.sfc_taskipt where Active=1 and TaskStepID=:TaskStepID and TaskType=6) SelfTaskID,"
							+ "       (select Status from {0}.sfc_taskipt where Active=1 and TaskStepID=:TaskStepID and TaskType=6) SelfStatus,"
							+ "       (select ID from {0}.sfc_taskipt where Active=1 and TaskStepID=:TaskStepID and TaskType=12) MutualTaskID,"
							+ "       (select Status from {0}.sfc_taskipt where Active=1 and TaskStepID=:TaskStepID and TaskType=12) MutualStatus,"
							+ "       (select ID from {0}.sfc_taskipt where Active=1 and TaskStepID=:TaskStepID and TaskType=13) SpecialTaskID,"
							+ "       (select Status from {0}.sfc_taskipt where Active=1 and TaskStepID=:TaskStepID and TaskType=13) SpecialStatus,"
							+ "       (select ID from {0}.sfc_taskipt where Active=1 and TaskStepID=:TaskStepID and TaskType=14) YJTaskID,"
							+ "       (select Status from {0}.sfc_taskipt where Active=1 and TaskStepID=:TaskStepID and TaskType=14) YJStatus;",
					wInstance.Result, wInstance2.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrder.ID);
			wParamMap.put("StationID", wAPSTaskStep.PartID);
			wParamMap.put("TaskStepID", wAPSTaskStep.ID);
			wParamMap.put("ProcessID", wAPSTaskStep.StepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {

				int wSendNCRNumber = StringUtils.parseInt(wReader.get("SendNCRNumber"));
				int wNCRNumber = StringUtils.parseInt(wReader.get("NCRNumber"));
				int wSendNCRFinishedNumber = StringUtils.parseInt(wReader.get("SendNCRFinishedNumber"));
				int wNCRFinishedNumber = StringUtils.parseInt(wReader.get("NCRFinishedNumber"));
				int wRepairNumber = StringUtils.parseInt(wReader.get("RepairNumber"));
				int wRepairFinishedNumber = StringUtils.parseInt(wReader.get("RepairFinishedNumber"));
				int wSelfTaskID = StringUtils.parseInt(wReader.get("SelfTaskID"));
				int wSelfStatus = StringUtils.parseInt(wReader.get("SelfStatus"));
				int wMutualTaskID = StringUtils.parseInt(wReader.get("MutualTaskID"));
				int wMutualStatus = StringUtils.parseInt(wReader.get("MutualStatus"));
				int wSpecialTaskID = StringUtils.parseInt(wReader.get("SpecialTaskID"));
				int wSpecialStatus = StringUtils.parseInt(wReader.get("SpecialStatus"));
				int wYJTaskID = StringUtils.parseInt(wReader.get("YJTaskID"));
				int wYJStatus = StringUtils.parseInt(wReader.get("YJStatus"));

				wResult = new SFCTrainProgress03(wOrder.ID, wOrder.PartNo, wOrder.LineName, wOrder.Customer,
						wOrder.ProductNo, wAPSTaskStep.PartID, wAPSTaskStep.PartName, wAPSTaskStep.StepID,
						wAPSTaskStep.StepName, wAPSTaskStep.ID, wAPSTaskStep.Status, wSendNCRNumber + wNCRNumber,
						wSendNCRFinishedNumber + wNCRFinishedNumber, wRepairNumber, wRepairFinishedNumber, wSelfTaskID,
						wSelfStatus, wMutualTaskID, wMutualStatus, wSpecialTaskID, wSpecialStatus, wYJTaskID,
						wYJStatus);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据订单、工位判断所有专检任务是否完成
	 */
	public String JudgeSpecialTaskIsOk(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils
					.Format("select t1.PartNo,t2.Name PartName,t3.Name PartPointName from {0}.sfc_taskipt t1,"
							+ "{0}.fpc_part t2,{0}.fpc_partpoint t3 where t1.StationID=t2.ID "
							+ "and t1.PartPointID=t3.ID and t1.OrderID=:OrderID and t1.StationID=:StationID and t1.TaskType=13 "
							+ "and t1.Active=1 and t1.Status=1;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("StationID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				String wPartNo = StringUtils.parseString(wReader.get("PartNo"));
				String wPartName = StringUtils.parseString(wReader.get("PartName"));
				String wPartPointName = StringUtils.parseString(wReader.get("PartPointName"));

				wResult = StringUtils.Format("【{0}】-【{1}】-【{2}】专检任务未完成，请及时处理。", wPartNo, wPartName, wPartPointName);
				return wResult;
			}

			if (StringUtils.isEmpty(wResult)) {
				List<RSMTurnOrderTask> wRList = RSMTurnOrderTaskDAO.getInstance().SelectList(wLoginUser, -1, wOrderID,
						wPartID, -1, null, null, null, wErrorCode);
				if (wRList != null && wRList.size() > 0 && StringUtils.isNotEmpty(wRList.get(0).Remark)) {
					wResult = wRList.get(0).Remark;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断该工序是否拍照
	 */
	public int GetIsPic(BMSEmployee wLoginUser, APSTaskStep wAPSTaskStep, OMSOrder wOrder,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT IsPic FROM {0}.ipt_standard where ProductID=:ProductID "
							+ "and LineID=:LineID and PartID=:PartID and PartPointID=:PartPointID and IsCurrent=1;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ProductID", wOrder.ProductID);
			wParamMap.put("LineID", wOrder.LineID);
			wParamMap.put("PartID", wAPSTaskStep.PartID);
			wParamMap.put("PartPointID", wAPSTaskStep.StepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("IsPic"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询转序控制的专检工位ID集合
	 */
	public List<Integer> GetControlPartIDList(BMSEmployee wLoginUser, int wTaskPartID, int wPartID,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select ControlPartIDList from {0}.fpc_routepart where RouteID in "
					+ "(select RouteID from {0}.oms_order where ID in (SELECT OrderID FROM {0}.aps_taskpart "
					+ "where ID=:wTaskPartID)) and PartID=:PartID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wTaskPartID", wTaskPartID);
			wParamMap.put("PartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				String wControlPartIDList = StringUtils.parseString(wReader.get("ControlPartIDList"));
				if (StringUtils.isNotEmpty(wControlPartIDList)) {
					wResult = StringUtils.parseIntList(wControlPartIDList.split(","));
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断互检消息是否存在
	 */
	public boolean JudgeMessageIsExist(BMSEmployee wLoginUser, Integer wPersonID, int wNewID) {
		boolean wResult = false;
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT count(*) Number FROM {0}.bfc_message where "
					+ "MessageID=:MessageID and ModuleID=8112 and active in (0,1,2) and ResponsorID=:ResponsorID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("MessageID", wNewID);
			wParamMap.put("ResponsorID", wPersonID);

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

	/**
	 * 返修撤销，专检撤回
	 */
	public void SFC_RepairBack(BMSEmployee wLoginUser, int wSFCTaskIPT, int wIPTItemID, OutResult<Integer> wErrorCode) {
		try {
			try {
				ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
						wLoginUser.getID(), 0);
				wErrorCode.set(wInstance.ErrorCode);
				if (wErrorCode.Result != 0) {
					return;
				}
				// 改状态
				String wSQL = StringUtils.Format("update {0}.sfc_taskipt set Status=1 where ID = :TaskID;",
						wInstance.Result);
				Map<String, Object> wParamMap = new HashMap<String, Object>();
				wParamMap.put("TaskID", wSFCTaskIPT);
				wSQL = this.DMLChange(wSQL);
				nameJdbcTemplate.update(wSQL, wParamMap);
				// 删除value
				wSQL = StringUtils.Format(
						"delete from {0}.ipt_value where TaskID=:TaskID and IPTItemID=:IPTItemID and ID>0;",
						wInstance.Result);
				wParamMap = new HashMap<String, Object>();
				wParamMap.put("TaskID", wSFCTaskIPT);
				wParamMap.put("IPTItemID", wIPTItemID);
				wSQL = this.DMLChange(wSQL);
				nameJdbcTemplate.update(wSQL, wParamMap);
			} catch (Exception ex) {
				logger.error(ex.toString());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 根据消息获取互检待办集合
	 */
	public List<Integer> GetMutualTaskIDListByMessage(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select MessageID from {0}.bfc_message where "
					+ "moduleid=8112 and active in (0,1,2) and responsorid=:wUserID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wUserID", wLoginUser.ID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wMessageID = StringUtils.parseInt(wReader.get("MessageID"));
				wResult.add(wMessageID);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取拍照的数据(自检、互检、专检数据)
	 */
	public List<String> SelectPicData(BMSEmployee wLoginUser, OMSOrder wOrder, int wPartID, int wStepID,
			OutResult<Integer> wErrorCode) {
		List<String> wResult = new ArrayList<String>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select OperatorList,SubmitTime from {0}.sfc_taskipt where orderid=:OrderID "
							+ "and stationid=:StationID and partpointid=:PartPointID and PicUri != '''';",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrder.ID);
			wParamMap.put("StationID", wPartID);
			wParamMap.put("PartPointID", wStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (Map<String, Object> wReader : wQueryResult) {
				String wOperatorList = StringUtils.parseString(wReader.get("OperatorList"));
				Calendar wSubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				if (StringUtils.isEmpty(wOperatorList)) {
					continue;
				}
				String wNames = GetNames(StringUtils.parseIntList(wOperatorList.split(",")));
				String wTime = wSDF.format(wSubmitTime.getTime());
				wResult.add(wNames);
				wResult.add(wTime);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取使用的标准ID
	 */
	public int GetUsedStandardID(BMSEmployee wLoginUser, OMSOrder wOrder, int wPartID, int wStepID,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select ModuleVersionID from {0}.sfc_taskipt where orderid=:OrderID "
					+ "and stationid=:StationID and partpointid=:PartPointID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", wOrder.ID);
			wParamMap.put("StationID", wPartID);
			wParamMap.put("PartPointID", wStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("ModuleVersionID"));
				return wResult;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询工位ID集合
	 */
	public List<Integer> APS_QueryTaskPartIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select ID from {0}.aps_taskpart where status in (4,5) and startworktime < ''2010-1-1'';",
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

	public Calendar APS_QueryMinStartTime(BMSEmployee wLoginUser, int wTaskPartID, OutResult<Integer> wErrorCode) {
		Calendar wBaseTime = Calendar.getInstance();
		wBaseTime.set(2000, 0, 1, 0, 0, 0);
		Calendar wResult = wBaseTime;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select min(StartTime) Time from {0}.aps_taskstep where TaskPartID=:TaskPartID and Status in (4,5);",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("TaskPartID", wTaskPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseCalendar(wReader.get("Time"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\serviceimpl\dao\sfc\
 * SFCTaskIPTDAO.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */