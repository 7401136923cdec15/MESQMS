package com.mes.qms.server.serviceimpl.dao.tcm;

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
import com.mes.qms.server.service.po.tcm.TCMTechChangeNotice;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.CoreServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class TCMTechChangeNoticeDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(TCMTechChangeNoticeDAO.class);

	private static TCMTechChangeNoticeDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wTCMTechChangeNotice
	 * @return
	 */
	public TCMTechChangeNotice Update(BMSEmployee wLoginUser, TCMTechChangeNotice wTCMTechChangeNotice,
			OutResult<Integer> wErrorCode) {
		TCMTechChangeNotice wResult = wTCMTechChangeNotice;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wTCMTechChangeNotice == null)
				return wResult;

			String wSQL = "";
			if (wTCMTechChangeNotice.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.tcm_techchangenotice(Code,FlowType,FlowID,UpFlowID,FollowerID,"
								+ "Status,StatusText,CreateTime,SubmitTime,ChangeLogID,OrderList,NoHandleOrders,LogID,AllOrderList) VALUES(:Code,:FlowType,"
								+ ":FlowID,:UpFlowID,:FollowerID,:Status,:StatusText,:CreateTime,:SubmitTime,:ChangeLogID,:OrderList,:NoHandleOrders,:LogID,:AllOrderList);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.tcm_techchangenotice SET Code = :Code,FlowType = :FlowType,"
						+ "FlowID = :FlowID,UpFlowID = :UpFlowID,FollowerID = :FollowerID,Status = :Status,"
						+ "StatusText = :StatusText,CreateTime = :CreateTime,SubmitTime = :SubmitTime,"
						+ "ChangeLogID = :ChangeLogID,OrderList=:OrderList,NoHandleOrders=:NoHandleOrders,LogID=:LogID,AllOrderList=:AllOrderList WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wTCMTechChangeNotice.ID);
			wParamMap.put("Code", wTCMTechChangeNotice.Code);
			wParamMap.put("FlowType", wTCMTechChangeNotice.FlowType);
			wParamMap.put("FlowID", wTCMTechChangeNotice.FlowID);
			wParamMap.put("UpFlowID", wTCMTechChangeNotice.UpFlowID);
			wParamMap.put("FollowerID", StringUtils.Join(",", wTCMTechChangeNotice.FollowerID));
			wParamMap.put("Status", wTCMTechChangeNotice.Status);
			wParamMap.put("StatusText", wTCMTechChangeNotice.StatusText);
			wParamMap.put("CreateTime", wTCMTechChangeNotice.CreateTime);
			wParamMap.put("SubmitTime", wTCMTechChangeNotice.SubmitTime);
			wParamMap.put("ChangeLogID", wTCMTechChangeNotice.ChangeLogID);
			wParamMap.put("OrderList", StringUtils.Join(",", wTCMTechChangeNotice.OrderList));
			wParamMap.put("NoHandleOrders", wTCMTechChangeNotice.NoHandleOrders);
			wParamMap.put("LogID", wTCMTechChangeNotice.LogID);
			wParamMap.put("AllOrderList", StringUtils.Join(",", wTCMTechChangeNotice.AllOrderList));

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wTCMTechChangeNotice.getID() <= 0) {
				int wNewID = keyHolder.getKey().intValue();
				wTCMTechChangeNotice.setID(wNewID);
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<TCMTechChangeNotice> wList,
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
			for (TCMTechChangeNotice wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.tcm_techchangenotice WHERE ID IN({0}) ;",
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
	public TCMTechChangeNotice SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		TCMTechChangeNotice wResult = new TCMTechChangeNotice();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<TCMTechChangeNotice> wList = SelectList(wLoginUser, wID, "", -1, -1, null, null, null, wErrorCode);
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
	public List<TCMTechChangeNotice> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID,
			int wChangeLogID, List<Integer> wStateIDList, Calendar wStartTime, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		List<TCMTechChangeNotice> wResultList = new ArrayList<TCMTechChangeNotice>();
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

			String wSQL = MessageFormat.format("SELECT * FROM {0}.tcm_techchangenotice WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wUpFlowID <= 0 or :wUpFlowID = UpFlowID ) "
					+ "and ( :wCode is null or :wCode = '''' or :wCode = Code ) "
					+ "and ( :wStartTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wStartTime <= SubmitTime) "
					+ "and ( :wEndTime <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or :wEndTime >= CreateTime) "
					+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1})) "
					+ "and ( :wChangeLogID <= 0 or :wChangeLogID = ChangeLogID );", wInstance.Result,
					wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wChangeLogID", wChangeLogID);
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

	private void SetValue(List<TCMTechChangeNotice> wResultList, List<Map<String, Object>> wQueryResult) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				TCMTechChangeNotice wItem = new TCMTechChangeNotice();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.LogID = StringUtils.parseInt(wReader.get("LogID"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.FlowType = StringUtils.parseInt(wReader.get("FlowType"));
				wItem.FlowID = StringUtils.parseInt(wReader.get("FlowID"));
				wItem.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.StatusText = StringUtils.parseString(wReader.get("StatusText"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wItem.ChangeLogID = StringUtils.parseInt(wReader.get("ChangeLogID"));
				wItem.NoHandleOrders = StringUtils.parseString(wReader.get("NoHandleOrders"));
				wItem.OrderList = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("OrderList")).split(","));
				wItem.AllOrderList = StringUtils
						.parseIntList(StringUtils.parseString(wReader.get("AllOrderList")).split(","));

				wItem.UpFlowName = QMSConstants.GetBMSEmployeeName(wItem.UpFlowID);

				wItem.TCMMaterialChangeLog = TCMMaterialChangeLogDAO.getInstance().SelectByIDNoSub(BaseDAO.SysAdmin,
						wItem.ChangeLogID, wErrorCode);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private TCMTechChangeNoticeDAO() {
		super();
	}

	public static TCMTechChangeNoticeDAO getInstance() {
		if (Instance == null)
			Instance = new TCMTechChangeNoticeDAO();
		return Instance;
	}

	/**
	 * ID集合获取任务集合
	 */
	private List<TCMTechChangeNotice> SelectList(BMSEmployee wLoginUser, List<Integer> wTaskIDList, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<TCMTechChangeNotice> wResultList = new ArrayList<TCMTechChangeNotice>();
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

			String wSQL = StringUtils.Format("SELECT * FROM {0}.tcm_techchangenotice WHERE  1=1  "
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

			String wSQL = StringUtils.Format(
					"select count(*)+1 as Number from {0}.tcm_techchangenotice where CreateTime > :wSTime and CreateTime < :wETime;",
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

			wResult = StringUtils.Format("TC{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
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
		List<TCMTechChangeNotice> wResult = new ArrayList<TCMTechChangeNotice>();
		try {
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.TechChangeNotice.getValue(), -1,
							BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.TechChangeNotice.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.TechChangeNotice.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			Map<Integer, TCMTechChangeNotice> wTaskMap = new HashMap<Integer, TCMTechChangeNotice>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				List<TCMTechChangeNotice> wMTCTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, null, null,
						wErrorCode);

				wTaskMap = wMTCTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			TCMTechChangeNotice wTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wTaskTemp = CloneTool.Clone(wTaskMap.get((int) wBFCMessage.getMessageID()), TCMTechChangeNotice.class);
				wTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wTaskTemp);
			}

			wResult.sort(Comparator.comparing(TCMTechChangeNotice::getSubmitTime).reversed());
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
		List<TCMTechChangeNotice> wResult = new ArrayList<TCMTechChangeNotice>();
		wErrorCode.set(0);
		try {
			List<TCMTechChangeNotice> wTaskList = new ArrayList<TCMTechChangeNotice>();
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.TechChangeNotice.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.TechChangeNotice.getValue(), -1,
							BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			wTaskList = this.SelectList(wLoginUser, wTaskIDList, wStartTime, wEndTime, wErrorCode);

			wTaskList.sort(Comparator.comparing(TCMTechChangeNotice::getSubmitTime).reversed());

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
		List<TCMTechChangeNotice> wResult = new ArrayList<TCMTechChangeNotice>();
		try {
			wResult = this.SelectList(wLoginUser, -1, "", wResponsorID, -1, null, wStartTime, wEndTime, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		BPMTaskBase wResult = new BPMTaskBase();
		try {
			wResult = this.Update(wLoginUser, (TCMTechChangeNotice) wTask, wErrorCode);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		TCMTechChangeNotice wResult = new TCMTechChangeNotice();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<TCMTechChangeNotice> wList = this.SelectList(wLoginUser, wTaskID, wCode, -1, -1, null, null, null,
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
	 * 通过工序、物料查询变更单ID
	 */
	public List<Integer> SelectTaskIDList(BMSEmployee wLoginUser, int wStepID, String wMaterialNo,
			OutResult<Integer> wErrorCode) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wMaterialNo == null) {
				wMaterialNo = "";
			}

			String wSQL = StringUtils.Format(
					"SELECT ID FROM {0}.tcm_techchangenotice where ChangeLogID in (SELECT distinct ChangeLogID FROM "
							+ "{0}.tcm_materialchangeitems "
							+ "where (:wMaterialNo = '''' or MaterialNo like ''%{1}%'') "
							+ "and (:wStepID<=0 or PartPointID=:wStepID));",
					wInstance.Result, wMaterialNo);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wMaterialNo", wMaterialNo);
			wParamMap.put("wStepID", wStepID);

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
