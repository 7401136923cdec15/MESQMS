package com.mes.qms.server.serviceimpl.dao.sfc;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
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

import com.mes.qms.server.service.mesenum.BFCMessageType;
import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.SFCExceptionType;
import com.mes.qms.server.service.mesenum.SFCResponseLevel;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.sfc.SFCBogiesChangeBPM;
import com.mes.qms.server.service.po.sfc.SFCBogiesChangeBPMItem;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.CoreServiceImpl;
import com.mes.qms.server.serviceimpl.LOCOAPSServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class SFCBogiesChangeBPMDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCBogiesChangeBPMDAO.class);

	private static SFCBogiesChangeBPMDAO Instance = null;

	private SFCBogiesChangeBPMDAO() {
		super();
	}

	public static SFCBogiesChangeBPMDAO getInstance() {
		if (Instance == null)
			Instance = new SFCBogiesChangeBPMDAO();
		return Instance;
	}

	/**
	 * 添加或修改
	 * 
	 * @param wSFCBogiesChangeBPM
	 * @return
	 */
	public SFCBogiesChangeBPM Update(BMSEmployee wLoginUser, SFCBogiesChangeBPM wSFCBogiesChangeBPM,
			OutResult<Integer> wErrorCode) {
		SFCBogiesChangeBPM wResult = wSFCBogiesChangeBPM;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCBogiesChangeBPM == null)
				return wResult;

			String wSQL = "";
			if (wSFCBogiesChangeBPM.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.sfc_bogieschangebpm(Code,FlowType,FlowID,UpFlowID,FollowerID,Status,StatusText,"
								+ "CreateTime,SubmitTime,SOrderID,TOrderID,RespondLevel,ExceptionType,"
								+ "DutyDepartmentID,OccurPartID,SolveDeadLineTime) "
								+ "VALUES(:Code,:FlowType,:FlowID,:UpFlowID,:FollowerID,"
								+ ":Status,:StatusText,:CreateTime,:SubmitTime,:SOrderID,:TOrderID,"
								+ ":RespondLevel,:ExceptionType,:DutyDepartmentID,:OccurPartID,:SolveDeadLineTime);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat
						.format("UPDATE {0}.sfc_bogieschangebpm SET Code = :Code,FlowType = :FlowType,FlowID = :FlowID,"
								+ "UpFlowID = :UpFlowID,FollowerID = :FollowerID,Status = :Status,StatusText = :StatusText,"
								+ "CreateTime = :CreateTime,SubmitTime = :SubmitTime,SOrderID = :SOrderID,"
								+ "TOrderID = :TOrderID,RespondLevel=:RespondLevel,ExceptionType=:ExceptionType,"
								+ "DutyDepartmentID=:DutyDepartmentID,OccurPartID=:OccurPartID,"
								+ "SolveDeadLineTime=:SolveDeadLineTime WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCBogiesChangeBPM.ID);
			wParamMap.put("Code", wSFCBogiesChangeBPM.Code);
			wParamMap.put("FlowType", wSFCBogiesChangeBPM.FlowType);
			wParamMap.put("FlowID", wSFCBogiesChangeBPM.FlowID);
			wParamMap.put("UpFlowID", wSFCBogiesChangeBPM.UpFlowID);
			wParamMap.put("FollowerID", StringUtils.Join(",", wSFCBogiesChangeBPM.FollowerID));
			wParamMap.put("Status", wSFCBogiesChangeBPM.Status);
			wParamMap.put("StatusText", wSFCBogiesChangeBPM.StatusText);
			wParamMap.put("CreateTime", wSFCBogiesChangeBPM.CreateTime);
			wParamMap.put("SubmitTime", wSFCBogiesChangeBPM.SubmitTime);
			wParamMap.put("SOrderID", wSFCBogiesChangeBPM.SOrderID);
			wParamMap.put("TOrderID", wSFCBogiesChangeBPM.TOrderID);
			wParamMap.put("RespondLevel", wSFCBogiesChangeBPM.RespondLevel);
			wParamMap.put("ExceptionType", wSFCBogiesChangeBPM.ExceptionType);
			wParamMap.put("DutyDepartmentID", wSFCBogiesChangeBPM.DutyDepartmentID);
			wParamMap.put("OccurPartID", wSFCBogiesChangeBPM.OccurPartID);
			wParamMap.put("SolveDeadLineTime", wSFCBogiesChangeBPM.SolveDeadLineTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCBogiesChangeBPM.getID() <= 0) {
				wResult.setID(keyHolder.getKey().intValue());
				wSFCBogiesChangeBPM.setID(wResult.ID);
			} else {
				wResult.setID(wSFCBogiesChangeBPM.getID());
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCBogiesChangeBPM> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (SFCBogiesChangeBPM wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_bogieschangebpm WHERE ID IN({0}) ;",
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
	public SFCBogiesChangeBPM SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCBogiesChangeBPM wResult = new SFCBogiesChangeBPM();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCBogiesChangeBPM> wList = SelectList(wLoginUser, wID, "", -1, null, null, null, -1, -1, wErrorCode);
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
	public List<SFCBogiesChangeBPM> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID,
			Calendar wStartTime, Calendar wEndTime, List<Integer> wStateIDList, int wSOrderID, int wTOrderID,
			OutResult<Integer> wErrorCode) {
		List<SFCBogiesChangeBPM> wResultList = new ArrayList<SFCBogiesChangeBPM>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
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

			if (wStateIDList == null) {
				wStateIDList = new ArrayList<Integer>();
			}

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.sfc_bogieschangebpm WHERE  1=1  and ( :wID <= 0 or :wID = ID ) "
							+ "and ( :wCode is null or :wCode = '''' or :wCode = Code ) "
							+ "and ( :wUpFlowID <= 0 or :wUpFlowID = UpFlowID ) "
							+ "and ( :wSOrderID <= 0 or :wSOrderID = SOrderID ) "
							+ "and ( :wTOrderID <= 0 or :wTOrderID = TOrderID ) "
							+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  SubmitTime ) "
							+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  CreateTime ) "
							+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1}));",
					wInstance.Result, wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wSOrderID", wSOrderID);
			wParamMap.put("wTOrderID", wTOrderID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
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

	private void SetValue(BMSEmployee wLoginUser, List<SFCBogiesChangeBPM> wResultList,
			List<Map<String, Object>> wQueryResult) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				SFCBogiesChangeBPM wItem = new SFCBogiesChangeBPM();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.FlowType = StringUtils.parseInt(wReader.get("FlowType"));
				wItem.FlowID = StringUtils.parseInt(wReader.get("FlowID"));
				wItem.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
				wItem.FollowerID = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("FollowerID")).split(","));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.StatusText = StringUtils.parseString(wReader.get("StatusText"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wItem.SOrderID = StringUtils.parseInt(wReader.get("SOrderID"));
				wItem.TOrderID = StringUtils.parseInt(wReader.get("TOrderID"));
				wItem.RespondLevel = StringUtils.parseInt(wReader.get("RespondLevel"));

				wItem.ExceptionType = StringUtils.parseInt(wReader.get("ExceptionType"));
				wItem.DutyDepartmentID = StringUtils.parseInt(wReader.get("DutyDepartmentID"));
				wItem.OccurPartID = StringUtils.parseInt(wReader.get("OccurPartID"));
				wItem.SolveDeadLineTime = StringUtils.parseCalendar(wReader.get("SolveDeadLineTime"));

				wItem.RespondLevelName = SFCResponseLevel.getEnumType(wItem.RespondLevel).getLable();
				wItem.ExceptionTypeName = SFCExceptionType.getEnumType(wItem.ExceptionType).getLable();
				wItem.DutyDepartmentName = QMSConstants.GetBMSDepartmentName(wItem.DutyDepartmentID);
				wItem.OccurPartName = QMSConstants.GetFPCPartName(wItem.OccurPartID);

				if (wItem.SOrderID > 0) {
					OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wItem.SOrderID)
							.Info(OMSOrder.class);
					wItem.SCustomerName = wOrder.Customer;
					wItem.SLineName = wOrder.LineName;
					wItem.SPartNo = wOrder.PartNo;
					wItem.SProductNo = wOrder.ProductNo;
				}

				if (wItem.TOrderID > 0) {
					OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wItem.TOrderID)
							.Info(OMSOrder.class);
					wItem.TCustomerName = wOrder.Customer;
					wItem.TLineName = wOrder.LineName;
					wItem.TPartNo = wOrder.PartNo;
					wItem.TProductNo = wOrder.ProductNo;
				}

				wItem.UpFlowName = QMSConstants.GetBMSEmployeeName(wItem.UpFlowID);

				wItem.ItemList = SFCBogiesChangeBPMItemDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID,
						wErrorCode);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ID集合获取任务集合
	 */
	private List<SFCBogiesChangeBPM> SelectList(BMSEmployee wLoginUser, List<Integer> wTaskIDList, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<SFCBogiesChangeBPM> wResultList = new ArrayList<SFCBogiesChangeBPM>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wTaskIDList == null || wTaskIDList.size() <= 0) {
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

			String wSQL = StringUtils.Format("SELECT * FROM {0}.sfc_bogieschangebpm WHERE  1=1  "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  SubmitTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  CreateTime ) "
					+ "and ( :wIDs is null or :wIDs = '''' or ID in ({1}));", wInstance.Result,
					wTaskIDList.size() > 0 ? StringUtils.Join(",", wTaskIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wIDs", StringUtils.Join(",", wTaskIDList));
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStartTime", wStartTime);

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
	 * 获取最新的编码
	 */
	public String GetNewCode(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			// 本月时间
			int wYear = Calendar.getInstance().get(Calendar.YEAR);
			int wMonth = Calendar.getInstance().get(Calendar.MONTH);
			Calendar wSTime = Calendar.getInstance();
			wSTime.set(wYear, wMonth, 1, 0, 0, 0);
			Calendar wETime = Calendar.getInstance();
			wETime.set(wYear, wMonth + 1, 1, 23, 59, 59);
			wETime.add(Calendar.DATE, -1);

			String wSQL = StringUtils.Format("select count(*)+1 as Number from {0}.sfc_bogieschangebpm "
					+ "where CreateTime > :wSTime and CreateTime < :wETime;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wSTime", wSTime);
			wParamMap.put("wETime", wETime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			int wNumber = 0;
			for (Map<String, Object> wReader : wQueryResult) {
				if (wReader.containsKey("Number")) {
					wNumber = StringUtils.parseInt(wReader.get("Number"));
					break;
				}
			}

			wResult = StringUtils.Format("BO{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
					String.format("%04d", wNumber));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public List<BPMTaskBase> BPM_GetUndoTaskList(BMSEmployee wLoginUser, int wResponsorID,
			OutResult<Integer> wErrorCode) {
		List<SFCBogiesChangeBPM> wResult = new ArrayList<SFCBogiesChangeBPM>();
		try {
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.BogiesChange.getValue(), -1,
							BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.BogiesChange.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.BogiesChange.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			Map<Integer, SFCBogiesChangeBPM> wTaskMap = new HashMap<Integer, SFCBogiesChangeBPM>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				List<SFCBogiesChangeBPM> wMTCTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, null, null,
						wErrorCode);

				wTaskMap = wMTCTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			SFCBogiesChangeBPM wTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wTaskTemp = CloneTool.Clone(wTaskMap.get((int) wBFCMessage.getMessageID()), SFCBogiesChangeBPM.class);
				wTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wTaskTemp);
			}

			wResult.sort(Comparator.comparing(SFCBogiesChangeBPM::getSubmitTime).reversed());
			// 剔除任务状态为0的任务（废弃任务）
			if (wResult != null && wResult.size() > 0) {
				wResult = wResult.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}
		} catch (Exception e) {
			wErrorCode.set(MESException.Exception.getValue());
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public List<BPMTaskBase> BPM_GetDoneTaskList(BMSEmployee wLoginUser, int wResponsorID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<SFCBogiesChangeBPM> wResult = new ArrayList<SFCBogiesChangeBPM>();
		wErrorCode.set(0);
		try {
			List<SFCBogiesChangeBPM> wTaskList = new ArrayList<SFCBogiesChangeBPM>();
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.BogiesChange.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.BogiesChange.getValue(),
									-1, BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			wTaskList = this.SelectList(wLoginUser, wTaskIDList, wStartTime, wEndTime, wErrorCode);

			wTaskList.sort(Comparator.comparing(SFCBogiesChangeBPM::getSubmitTime).reversed());

			wResult = wTaskList;
			// 剔除任务状态为0的任务（废弃任务）
			if (wResult != null && wResult.size() > 0) {
				wResult = wResult.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public List<BPMTaskBase> BPM_GetSendTaskList(BMSEmployee wLoginUser, int wResponsorID, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<SFCBogiesChangeBPM> wResult = new ArrayList<SFCBogiesChangeBPM>();
		try {
			wResult = this.SelectList(wLoginUser, -1, "", wResponsorID, wStartTime, wEndTime, null, -1, -1, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		BPMTaskBase wResult = new BPMTaskBase();
		try {
			wResult = this.Update(wLoginUser, (SFCBogiesChangeBPM) wTask, wErrorCode);

			// ①保存子项数据
			if (((SFCBogiesChangeBPM) wTask).ItemList != null && ((SFCBogiesChangeBPM) wTask).ItemList.size() > 0) {
				for (SFCBogiesChangeBPMItem wItem : ((SFCBogiesChangeBPM) wTask).ItemList) {
					wItem.TaskID = wResult.ID;
					SFCBogiesChangeBPMItemDAO.getInstance().Update(wLoginUser, wItem, wErrorCode);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		SFCBogiesChangeBPM wResult = new SFCBogiesChangeBPM();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCBogiesChangeBPM> wList = this.SelectList(wLoginUser, wTaskID, wCode, -1, null, null, null, -1, -1,
					wErrorCode);
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
	 * 获取转向架互换的订单ID
	 */
	public int GetChangeOrderID(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT BogiesOrderID FROM {0}.sfc_bogieschangebpmitem t1,{0}.sfc_bogieschangebpm t2 "
							+ "where t1.TaskID=t2.ID and t2.Status in (20) and t1.BodyOrderID = :wOrderID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("BogiesOrderID"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取转向架互换的订单ID
	 */
	public int GetChangingOrderID(BMSEmployee wLoginUser, int wOrderID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT SOrderID,TOrderID FROM {0}.sfc_bogieschangebpm "
							+ "where (SOrderID = :wOrderID or TOrderID=:wOrderID) and Status in (1,20)  ;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wSOrderID = StringUtils.parseInt(wReader.get("SOrderID"));
				int wTOrderID = StringUtils.parseInt(wReader.get("TOrderID"));
				wResult = wSOrderID == wOrderID ? wTOrderID : wSOrderID;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询可用的订单ID集合
	 */
	public List<Integer> QueryUsableOrderIDList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select ID from {0}.oms_order where Status=4 "
							+ "and ID not in (select t1.BodyOrderID from {0}.sfc_bogieschangebpmitem t1,"
							+ "{0}.sfc_bogieschangebpm t2 where t1.TaskID=t2.ID and t2.Status not in (0,20,21,22)) "
							+ "and ID not in (select t1.BogiesOrderID from {0}.sfc_bogieschangebpmitem t1,"
							+ "{0}.sfc_bogieschangebpm t2 where t1.TaskID=t2.ID and t2.Status not in (0,20,21,22));",
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

}
