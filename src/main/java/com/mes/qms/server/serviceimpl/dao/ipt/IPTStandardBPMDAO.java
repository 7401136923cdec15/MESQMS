package com.mes.qms.server.serviceimpl.dao.ipt;

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
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.ipt.IPTStandardBPM;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.CoreServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class IPTStandardBPMDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTStandardBPMDAO.class);

	private static IPTStandardBPMDAO Instance = null;

	/**
	 * Add Or Update
	 * 
	 * @param wIPTStandardBPM
	 * @return
	 */
	public IPTStandardBPM Update(BMSEmployee wLoginUser, IPTStandardBPM wIPTStandardBPM,
			OutResult<Integer> wErrorCode) {
		IPTStandardBPM wResult = new IPTStandardBPM();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTStandardBPM == null)
				return wResult;

			String wSQL = "";
			if (wIPTStandardBPM.getID() <= 0) {
				wSQL = StringUtils.Format("INSERT INTO {0}.ipt_standardbpm(Code,FlowType,FlowID,UpFlowID,FollowerID,"
						+ "Status,StatusText,CreateTime,SubmitTime,StandardID) VALUES(:Code,:FlowType,"
						+ ":FlowID,:UpFlowID,:FollowerID,:Status,:StatusText,:CreateTime,:SubmitTime,:StandardID);",
						wInstance.Result);

				wIPTStandardBPM.Code = GetNewCode(wLoginUser, wErrorCode);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.ipt_standardbpm SET Code = :Code,FlowType = :FlowType,FlowID = :FlowID,"
								+ "UpFlowID = :UpFlowID,FollowerID = :FollowerID,Status = :Status,StatusText = :StatusText,"
								+ "CreateTime = :CreateTime,SubmitTime = now(),StandardID = :StandardID WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTStandardBPM.ID);
			wParamMap.put("Code", wIPTStandardBPM.Code);
			wParamMap.put("FlowType", wIPTStandardBPM.FlowType);
			wParamMap.put("FlowID", wIPTStandardBPM.FlowID);
			wParamMap.put("UpFlowID", wIPTStandardBPM.UpFlowID);
			wParamMap.put("FollowerID", StringUtils.Join(",", wIPTStandardBPM.FollowerID));
			wParamMap.put("Status", wIPTStandardBPM.Status);
			wParamMap.put("StatusText", wIPTStandardBPM.StatusText);
			wParamMap.put("CreateTime", wIPTStandardBPM.CreateTime);
			wParamMap.put("SubmitTime", wIPTStandardBPM.SubmitTime);
			wParamMap.put("StandardID", wIPTStandardBPM.StandardID);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTStandardBPM.getID() <= 0) {
				wIPTStandardBPM.setID(keyHolder.getKey().intValue());
			}
			wResult = wIPTStandardBPM;
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * Delete List
	 * 
	 * @param wList
	 */
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTStandardBPM> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (IPTStandardBPM wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_standardbpm WHERE ID IN({0}) ;",
					String.join(",", wIDList), wInstance.Result);
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * Query Single Info
	 * 
	 * @return
	 */
	public IPTStandardBPM SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTStandardBPM wResult = new IPTStandardBPM();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTStandardBPM> wList = SelectList(wLoginUser, wID, "", -1, -1, null, null, null, wErrorCode);
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
	 * Condition Query
	 * 
	 * @return
	 */
	public List<IPTStandardBPM> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID,
			int wStandardID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStateIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTStandardBPM> wResultList = new ArrayList<IPTStandardBPM>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
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

			String wSQL = StringUtils.Format("SELECT * FROM {0}.ipt_standardbpm WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wCode is null or :wCode = '''' or :wCode = Code ) "
					+ "and ( :wUpFlowID <= 0 or :wUpFlowID = UpFlowID ) "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  SubmitTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  CreateTime ) "
					+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1})) "
					+ "and ( :wStandardID <= 0 or :wStandardID = StandardID );", wInstance.Result,
					wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wStandardID", wStandardID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private void SetValue(List<IPTStandardBPM> wResultList, List<Map<String, Object>> wQueryResult) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				IPTStandardBPM wItem = new IPTStandardBPM();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.FlowType = StringUtils.parseInt(wReader.get("FlowType"));
				wItem.FlowID = StringUtils.parseInt(wReader.get("FlowID"));
				wItem.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
				wItem.FollowerID = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("FollowerID")).split(",|;"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.StatusText = StringUtils.parseString(wReader.get("StatusText"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wItem.StandardID = StringUtils.parseString(wReader.get("StandardID"));

				wItem.UpFlowName = QMSConstants.GetBMSEmployeeName(wItem.UpFlowID);
				List<Integer> wIDList = StringUtils.parseIntList(wItem.StandardID.split(",|;"));

				wItem.IPTStandard = IPTStandardDAO.getInstance().SelectIPTStandardList(BaseDAO.SysAdmin, wIDList,
						wErrorCode);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private IPTStandardBPMDAO() {
		super();
	}

	public static IPTStandardBPMDAO getInstance() {
		if (Instance == null)
			Instance = new IPTStandardBPMDAO();
		return Instance;
	}

	/**
	 * ID集合获取任务集合
	 */
	private List<IPTStandardBPM> SelectList(BMSEmployee wLoginUser, List<Integer> wTaskIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTStandardBPM> wResultList = new ArrayList<IPTStandardBPM>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wTaskIDList == null || wTaskIDList.size() <= 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ipt_standardbpm WHERE  1=1  "
							+ "and ( :wIDs is null or :wIDs = '''' or ID in ({1}));",
					wInstance.Result, wTaskIDList.size() > 0 ? StringUtils.Join(",", wTaskIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wIDs", StringUtils.Join(",", wTaskIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wResultList, wQueryResult);
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
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
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

			String wSQL = StringUtils.Format(
					"select count(*)+1 as Number from {0}.ipt_standardbpm where CreateTime > :wSTime and CreateTime < :wETime;",
					wInstance.Result);

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

			wResult = StringUtils.Format("ST{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
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
		List<IPTStandardBPM> wResult = new ArrayList<IPTStandardBPM>();
		try {
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.StandardAudit.getValue(), -1,
							BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.StandardAudit.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.StandardAudit.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			Map<Integer, IPTStandardBPM> wTaskMap = new HashMap<Integer, IPTStandardBPM>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				List<IPTStandardBPM> wMTCTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, wErrorCode);

				wTaskMap = wMTCTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			IPTStandardBPM wTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wTaskTemp = CloneTool.Clone(wTaskMap.get((int) wBFCMessage.getMessageID()), IPTStandardBPM.class);
				wTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wTaskTemp);
			}

			wResult.sort(Comparator.comparing(IPTStandardBPM::getSubmitTime).reversed());
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
		List<IPTStandardBPM> wResult = new ArrayList<IPTStandardBPM>();
		wErrorCode.set(0);
		try {
			List<IPTStandardBPM> wTaskList = new ArrayList<IPTStandardBPM>();
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.StandardAudit.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.StandardAudit.getValue(),
									-1, BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			wTaskList = this.SelectList(wLoginUser, wTaskIDList, wErrorCode);

			wTaskList.sort(Comparator.comparing(IPTStandardBPM::getSubmitTime).reversed());

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
		List<IPTStandardBPM> wResult = new ArrayList<IPTStandardBPM>();
		try {
			wResult = this.SelectList(wLoginUser, -1, "", wResponsorID, -1, wStartTime, wEndTime, null, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		BPMTaskBase wResult = new BPMTaskBase();
		try {
			wResult = this.Update(wLoginUser, (IPTStandardBPM) wTask, wErrorCode);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		IPTStandardBPM wResult = new IPTStandardBPM();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTStandardBPM> wList = this.SelectList(wLoginUser, wTaskID, wCode, -1, -1, null, null, null,
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
	 * 根据标准ID获取流程实例ID
	 */
	public int SelectFlowID(BMSEmployee wLoginUser, int wStandardID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT FlowID FROM {0}.ipt_standardbpm " + "WHERE StandardID=:StandardID and Status=20;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("StandardID", wStandardID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("FlowID"));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
