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
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.sfc.SFCLetPassBPM;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.ncr.NCRTask;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.CoreServiceImpl;
import com.mes.qms.server.serviceimpl.LOCOAPSServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

/**
 * 例外放行
 */
public class SFCLetPassBPMDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCLetPassBPMDAO.class);

	private static SFCLetPassBPMDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCLetPassBPM
	 * @return
	 */
	public SFCLetPassBPM Update(BMSEmployee wLoginUser, SFCLetPassBPM wSFCLetPassBPM, OutResult<Integer> wErrorCode) {
		SFCLetPassBPM wResult = wSFCLetPassBPM;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCLetPassBPM == null)
				return wResult;

			String wSQL = "";
			if (wSFCLetPassBPM.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.sfc_letpassbpm(Code,FlowType,FlowID,UpFlowID,FollowerID,Status,StatusText,CreateTime,SubmitTime,OrderID,PartID,ClosePartID,StepIDs) VALUES(:Code,:FlowType,:FlowID,:UpFlowID,:FollowerID,:Status,:StatusText,:CreateTime,:SubmitTime,:OrderID,:PartID,:ClosePartID,:StepIDs,DescribInfo,ImageUrl);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.sfc_letpassbpm SET Code = :Code,FlowType = :FlowType,FlowID = :FlowID,UpFlowID = :UpFlowID,FollowerID = :FollowerID,Status = :Status,StatusText = :StatusText,CreateTime = :CreateTime,SubmitTime = :SubmitTime,OrderID = :OrderID,PartID = :PartID,ClosePartID = :ClosePartID,StepIDs = :StepIDs,DescribInfo=:DescribInfo,ImageUrl=:ImageUrl WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCLetPassBPM.ID);
			wParamMap.put("Code", wSFCLetPassBPM.Code);
			wParamMap.put("FlowType", wSFCLetPassBPM.FlowType);
			wParamMap.put("FlowID", wSFCLetPassBPM.FlowID);
			wParamMap.put("UpFlowID", wSFCLetPassBPM.UpFlowID);
			wParamMap.put("FollowerID", wSFCLetPassBPM.FollowerID);
			wParamMap.put("Status", wSFCLetPassBPM.Status);
			wParamMap.put("StatusText", wSFCLetPassBPM.StatusText);
			wParamMap.put("CreateTime", wSFCLetPassBPM.CreateTime);
			wParamMap.put("SubmitTime", wSFCLetPassBPM.SubmitTime);
			wParamMap.put("OrderID", wSFCLetPassBPM.OrderID);
			wParamMap.put("PartID", wSFCLetPassBPM.PartID);
			wParamMap.put("ClosePartID", wSFCLetPassBPM.ClosePartID);
			wParamMap.put("StepIDs", wSFCLetPassBPM.StepIDs);
			wParamMap.put("DescribInfo", wSFCLetPassBPM.DescribInfo);
			wParamMap.put("ImageUrl", wSFCLetPassBPM.ImageUrl);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCLetPassBPM.getID() <= 0) {
				wSFCLetPassBPM.setID(keyHolder.getKey().intValue());
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCLetPassBPM> wList,
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
			for (SFCLetPassBPM wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_letpassbpm WHERE ID IN({0}) ;",
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
	public SFCLetPassBPM SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCLetPassBPM wResult = new SFCLetPassBPM();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCLetPassBPM> wList = SelectList(wLoginUser, wID, "", null, null, -1, -1, -1, null, -1, wErrorCode);
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
	public List<SFCLetPassBPM> SelectList(BMSEmployee wLoginUser, int wID, String wCode, Calendar wStartTime,
			Calendar wEndTime, int wOrderID, int wPartID, int wClosePartID, List<Integer> wStateIDList, int wUpFlowID,
			OutResult<Integer> wErrorCode) {
		List<SFCLetPassBPM> wResultList = new ArrayList<SFCLetPassBPM>();
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

			String wSQL = MessageFormat.format("SELECT * FROM {0}.sfc_letpassbpm WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wCode is null or :wCode = '''' or :wCode = Code ) "
					+ "and ( :wUpFlowID <= 0 or :wUpFlowID = UpFlowID ) "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  SubmitTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  CreateTime ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = OrderID ) " + "and ( :wPartID <= 0 or :wPartID = PartID ) "
					+ "and ( :wClosePartID <= 0 or :wClosePartID = ClosePartID ) "
					+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1}));", wInstance.Result,
					wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartID", wPartID);
			wParamMap.put("wClosePartID", wClosePartID);
			wParamMap.put("wUpFlowID", wUpFlowID);
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

	/**
	 * 属性赋值
	 */
	private void SetValue(BMSEmployee wLoginUser, List<SFCLetPassBPM> wResultList,
			List<Map<String, Object>> wQueryResult) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				SFCLetPassBPM wItem = new SFCLetPassBPM();

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
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.ClosePartID = StringUtils.parseInt(wReader.get("ClosePartID"));
				wItem.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));
				wItem.DescribInfo = StringUtils.parseString(wReader.get("DescribInfo"));
				wItem.ImageUrl = StringUtils.parseString(wReader.get("ImageUrl"));

				// 辅助属性赋值
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wItem.OrderID)
						.Info(OMSOrder.class);
				if (wOrder != null && wOrder.ID > 0) {
					wItem.ProductNo = wOrder.ProductNo;
					wItem.PartNo = wOrder.PartNo;
					wItem.LineName = wOrder.LineName;
					wItem.CustomerName = wOrder.Customer;
				}
				wItem.PartName = QMSConstants.GetFPCPartName(wItem.PartID);
				wItem.StepNames = GetStepNames(wItem.StepIDs);
				wItem.ClosePartName = QMSConstants.GetFPCPartName(wItem.ClosePartID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取工序列表
	 */
	private String GetStepNames(String stepIDs) {
		String wResult = "";
		try {
			if (StringUtils.isEmpty(stepIDs)) {
				return wResult;
			}

			String[] wStrs = stepIDs.split(",");
			List<String> wNames = new ArrayList<String>();
			for (String wStr : wStrs) {
				int wStepID = StringUtils.parseInt(wStr);
				if (wStepID <= 0) {
					continue;
				}
				String wName = QMSConstants.GetFPCStepName(wStepID);
				if (StringUtils.isEmpty(wName)) {
					continue;
				}
				wNames.add(wName);
			}
			wResult = StringUtils.Join(",", wNames);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private SFCLetPassBPMDAO() {
		super();
	}

	public static SFCLetPassBPMDAO getInstance() {
		if (Instance == null)
			Instance = new SFCLetPassBPMDAO();
		return Instance;
	}

	/**
	 * ID集合获取任务集合
	 */
	private List<SFCLetPassBPM> SelectList(BMSEmployee wLoginUser, List<Integer> wTaskIDList, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<SFCLetPassBPM> wResultList = new ArrayList<SFCLetPassBPM>();
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

			String wSQL = StringUtils.Format("SELECT * FROM {0}.sfc_letpassbpm WHERE  1=1  "
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

			String wSQL = StringUtils.Format("select count(*)+1 as Number from {0}.sfc_letpassbpm "
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

			wResult = StringUtils.Format("LP{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
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
		List<SFCLetPassBPM> wResult = new ArrayList<SFCLetPassBPM>();
		try {
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SFCLetPass.getValue(), -1, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SFCLetPass.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));
			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.SFCLetPass.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			Map<Integer, SFCLetPassBPM> wTaskMap = new HashMap<Integer, SFCLetPassBPM>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				List<SFCLetPassBPM> wMTCTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, null, null, wErrorCode);

				wTaskMap = wMTCTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			SFCLetPassBPM wTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wTaskTemp = CloneTool.Clone(wTaskMap.get((int) wBFCMessage.getMessageID()), SFCLetPassBPM.class);
				wTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wTaskTemp);
			}

			wResult.sort(Comparator.comparing(SFCLetPassBPM::getSubmitTime).reversed());
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
		List<SFCLetPassBPM> wResult = new ArrayList<SFCLetPassBPM>();
		wErrorCode.set(0);
		try {
			List<SFCLetPassBPM> wTaskList = new ArrayList<SFCLetPassBPM>();
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SFCLetPass.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.SFCLetPass.getValue(),
									-1, BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			wTaskList = this.SelectList(wLoginUser, wTaskIDList, wStartTime, wEndTime, wErrorCode);

			wTaskList.sort(Comparator.comparing(SFCLetPassBPM::getSubmitTime).reversed());

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
		List<SFCLetPassBPM> wResult = new ArrayList<SFCLetPassBPM>();
		try {
			wResult = this.SelectList(wLoginUser, -1, "", wStartTime, wEndTime, -1, -1, -1, null, wResponsorID,
					wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		BPMTaskBase wResult = new BPMTaskBase();
		try {
			wResult = this.Update(wLoginUser, (SFCLetPassBPM) wTask, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		SFCLetPassBPM wResult = new SFCLetPassBPM();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCLetPassBPM> wList = this.SelectList(wLoginUser, wTaskID, wCode, null, null, -1, -1, -1, null, -1,
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
	 * 判断此工位的工序是否全部完成或设置了例外放行，且放行工位不是本工位
	 */
	public boolean JudgeIsLetGo(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep, Integer wPartID,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			// ①根据订单，工位查询是否有例外放行单，没有，直接返回
			List<NCRTask> wList = SelectList_1(wLoginUser, wSFCTaskStep.OrderID, wPartID, wErrorCode);
			if (wList.size() <= 0) {
				return wResult;
			}

			// ③根据订单、工位获取工序，状态Map
			Map<Integer, Integer> wStepStatusMap = SelectStepTaskStatusMap(wLoginUser, wSFCTaskStep.OrderID, wPartID,
					wErrorCode);

			// ②判断关闭工位是否为本工位，如果是，则直接返回
			List<Integer> wStepList = new ArrayList<Integer>();

			for (NCRTask sendNCRTask : wList) {
				if (StringUtils.isEmpty(sendNCRTask.StepIDs) && sendNCRTask.CloseStationID != wSFCTaskStep.PartID) {
					return true;
				}

				if (StringUtils.isEmpty(sendNCRTask.StepIDs) && sendNCRTask.CloseStationID == wSFCTaskStep.PartID) {
					continue;
				}

				// 设置了放行工序的情况
				// ③获取放行工序ID集合
				String[] wStrs = sendNCRTask.StepIDs.split(",");

				for (String wStr : wStrs) {
					int wStep = StringUtils.parseInt(wStr);
					wStepList.add(wStep);
				}
			}

			// ④遍历判断，状态为5，直接继续，不为5，判断是否为放行工序，是继续，否返回false
			for (int wStepID : wStepStatusMap.keySet()) {
				int wStatus = wStepStatusMap.get(wStepID);
				if (wStatus == 5) {
					continue;
				}

				if (wStepList.stream().anyMatch(p -> p == wStepID)) {
					continue;
				} else {
					wResult = false;
					return wResult;
				}
			}
			// ⑤结果置为true
			wResult = true;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

//	/**
//	 * 查询不合格评审单，例外放行单
//	 */
//	private List<SendNCRTask> SelectList(BMSEmployee wLoginUser, int orderID, Integer wPartID,
//			OutResult<Integer> wErrorCode) {
//		List<SendNCRTask> wResult = new ArrayList<SendNCRTask>();
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
//					wLoginUser.getID(), 0);
//			wErrorCode.set(wInstance.ErrorCode);
//			if (wErrorCode.Result != 0) {
//				return wResult;
//			}
//
//			String wSQL = StringUtils.Format(
//					"SELECT ClosePartID,StepIDs FROM {0}.ncr_sendtask "
//							+ "where IsRelease=1 and Status=12 and OrderID=:OrderID and StationID=:StationID;",
//					wInstance.Result);
//
//			Map<String, Object> wParamMap = new HashMap<String, Object>();
//
//			wParamMap.put("OrderID", orderID);
//			wParamMap.put("StationID", wPartID);
//
//			wSQL = this.DMLChange(wSQL);
//
//			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
//
//			for (Map<String, Object> wReader : wQueryResult) {
//				SendNCRTask wItem = new SendNCRTask();
//
//				wItem.ClosePartID = StringUtils.parseInt(wReader.get("ClosePartID"));
//				wItem.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));
//
//				wResult.add(wItem);
//			}
//		} catch (Exception ex) {
//			logger.error(ex.toString());
//		}
//		return wResult;
//	}

	/**
	 * 查询不合格评审单，例外放行单
	 */
	public List<NCRTask> SelectList_1(BMSEmployee wLoginUser, int orderID, Integer wPartID,
			OutResult<Integer> wErrorCode) {
		List<NCRTask> wResult = new ArrayList<NCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT CloseStationID,StepIDs FROM {0}.ncr_task "
							+ "where Result=8 and Status=12 and OrderID=:OrderID and StationID=:StationID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", orderID);
			wParamMap.put("StationID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				NCRTask wItem = new NCRTask();

				wItem.CloseStationID = StringUtils.parseInt(wReader.get("CloseStationID"));
				wItem.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询不合格评审单，例外放行单
	 */
//	public List<SendNCRTask> SelectListByClosePartID(BMSEmployee wLoginUser, int orderID, int wPartID,
//			OutResult<Integer> wErrorCode) {
//		List<SendNCRTask> wResult = new ArrayList<SendNCRTask>();
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
//					wLoginUser.getID(), 0);
//			wErrorCode.set(wInstance.ErrorCode);
//			if (wErrorCode.Result != 0) {
//				return wResult;
//			}
//
//			String wSQL = StringUtils.Format(
//					"SELECT ClosePartID,StepIDs,StationID FROM {0}.ncr_sendtask "
//							+ "where IsRelease=1 and Status=12 and OrderID=:OrderID and ClosePartID=:StationID;",
//					wInstance.Result);
//
//			Map<String, Object> wParamMap = new HashMap<String, Object>();
//
//			wParamMap.put("OrderID", orderID);
//			wParamMap.put("StationID", wPartID);
//
//			wSQL = this.DMLChange(wSQL);
//
//			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
//
//			for (Map<String, Object> wReader : wQueryResult) {
//				SendNCRTask wItem = new SendNCRTask();
//
//				wItem.ClosePartID = StringUtils.parseInt(wReader.get("ClosePartID"));
//				wItem.StationID = StringUtils.parseInt(wReader.get("StationID"));
//				wItem.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));
//
//				wResult.add(wItem);
//			}
//		} catch (Exception ex) {
//			logger.error(ex.toString());
//		}
//		return wResult;
//	}

	/**
	 * 查询不合格评审单，例外放行单
	 */
//	public List<SendNCRTask> SelectListByClosePartID(BMSEmployee wLoginUser, int orderID, int wPartID,
//			List<Integer> wNotStatusList, OutResult<Integer> wErrorCode) {
//		List<SendNCRTask> wResult = new ArrayList<SendNCRTask>();
//		try {
//			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
//					wLoginUser.getID(), 0);
//			wErrorCode.set(wInstance.ErrorCode);
//			if (wErrorCode.Result != 0) {
//				return wResult;
//			}
//
//			if (wNotStatusList == null || wNotStatusList.size() <= 0) {
//				return wResult;
//			}
//
//			String wSQL = StringUtils.Format(
//					"SELECT ClosePartID,StepIDs,StationID,Code,StatusText FROM {0}.ncr_sendtask "
//							+ "where IsRelease=1 and Status not in ({1}) and OrderID=:OrderID and ClosePartID=:StationID;",
//					wInstance.Result, StringUtils.Join(",", wNotStatusList));
//
//			Map<String, Object> wParamMap = new HashMap<String, Object>();
//
//			wParamMap.put("OrderID", orderID);
//			wParamMap.put("StationID", wPartID);
//
//			wSQL = this.DMLChange(wSQL);
//
//			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
//
//			for (Map<String, Object> wReader : wQueryResult) {
//				SendNCRTask wItem = new SendNCRTask();
//
//				wItem.Code = StringUtils.parseString(wReader.get("Code"));
//				wItem.ClosePartID = StringUtils.parseInt(wReader.get("ClosePartID"));
//				wItem.StationID = StringUtils.parseInt(wReader.get("StationID"));
//				wItem.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));
//				wItem.StatusText = StringUtils.parseString(wReader.get("StatusText"));
//
//				wResult.add(wItem);
//			}
//		} catch (Exception ex) {
//			logger.error(ex.toString());
//		}
//		return wResult;
//	}

	/**
	 * 查询不合格评审单，例外放行单
	 */
	public List<NCRTask> SelectListByClosePartID_1(BMSEmployee wLoginUser, int orderID, int wPartID,
			OutResult<Integer> wErrorCode) {
		List<NCRTask> wResult = new ArrayList<NCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT Code,CloseStationID,StepIDs,StationID,StatusText FROM {0}.ncr_task "
							+ "where Result=8 and Status=12 and OrderID=:OrderID and CloseStationID=:StationID;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", orderID);
			wParamMap.put("StationID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				NCRTask wItem = new NCRTask();

				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.CloseStationID = StringUtils.parseInt(wReader.get("CloseStationID"));
				wItem.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wItem.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));
				wItem.StatusText = StringUtils.parseString(wReader.get("StatusText"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询不合格评审单，例外放行单
	 */
	public List<NCRTask> SelectListByClosePartID_1(BMSEmployee wLoginUser, int orderID, int wPartID,
			List<Integer> wNotStatusList, OutResult<Integer> wErrorCode) {
		List<NCRTask> wResult = new ArrayList<NCRTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wNotStatusList == null || wNotStatusList.size() <= 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT Code,CloseStationID,StepIDs,StationID,StatusText FROM {0}.ncr_task "
							+ "where Result=8 and Status not in ({1}) and OrderID=:OrderID and CloseStationID=:StationID;",
					wInstance.Result, StringUtils.Join(",", wNotStatusList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", orderID);
			wParamMap.put("StationID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				NCRTask wItem = new NCRTask();

				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.CloseStationID = StringUtils.parseInt(wReader.get("CloseStationID"));
				wItem.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wItem.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));
				wItem.StatusText = StringUtils.parseString(wReader.get("StatusText"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据订单，工位获取工序，状态字典
	 */
	private Map<Integer, Integer> SelectStepTaskStatusMap(BMSEmployee wLoginUser, int orderID, Integer wPartID,
			OutResult<Integer> wErrorCode) {
		Map<Integer, Integer> wResult = new HashMap<Integer, Integer>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT StepID,Status FROM {0}.aps_taskstep "
					+ "where OrderID=:OrderID and PartID=:PartID and Active=1;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", orderID);
			wParamMap.put("PartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wStepID = StringUtils.parseInt(wReader.get("StepID"));
				int wStatus = StringUtils.parseInt(wReader.get("Status"));
				wResult.put(wStepID, wStatus);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断关闭工位
	 */
	public boolean JudgeClosePartStepIsFinish(BMSEmployee wLoginUser, SFCTaskStep wSFCTaskStep,
			OutResult<Integer> wErrorCode) {
		boolean wResult = true;
		try {
			// ①根据订单，工位查询是否有例外放行单，没有，直接返回
			List<NCRTask> wList = SelectListByClosePartID_1(wLoginUser, wSFCTaskStep.OrderID, wSFCTaskStep.PartID,
					wErrorCode);
			if (wList.size() <= 0) {
				return true;
			}

			for (NCRTask sendNCRTask : wList) {
				// ③根据订单、工位获取工序，状态Map
				Map<Integer, Integer> wStepStatusMap = SelectStepTaskStatusMap(wLoginUser, wSFCTaskStep.OrderID,
						sendNCRTask.StationID, wErrorCode);

				// ②判断关闭工位是否为本工位，如果是，则直接返回
				List<Integer> wStepList = new ArrayList<Integer>();

				// ③获取放行工序ID集合
				String[] wStrs = sendNCRTask.StepIDs.split(",");

				for (String wStr : wStrs) {
					int wStep = StringUtils.parseInt(wStr);
					wStepList.add(wStep);
				}

				// ④遍历判断，状态为5，直接继续，不为5，判断是否为放行工序，是继续，否返回false
				for (int wStepID : wStepList) {
					if (wStepStatusMap.containsKey(wStepID)) {
						int wStatus = wStepStatusMap.get(wStepID);
						if (wStatus != 5) {
							return false;
						}
					} else {
						return false;
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断某个工序是否例外放行
	 */
	public boolean JudgeStepIsLetGo(BMSEmployee wLoginUser, int orderID, int wStepID, OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.WDW,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT count(*) Number FROM {0}.ncr_sendtask where "
							+ "IsRelease=1 and Status=12 and OrderID=:OrderID and find_in_set( ''{1}'',StepIDs);",
					wInstance.Result, String.valueOf(wStepID));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderID", orderID);

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
	 * 判断工序任务是否已完成
	 */

	public boolean IsStepFinished(BMSEmployee wLoginUser, OMSOrder wOrder, int wPartID, int wStepID,
			OutResult<Integer> wErrorCode) {
		boolean wResult = false;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT Status FROM {0}.aps_taskstep where OrderID=:wOrderID "
					+ "and PartID=:PartID and StepID=:StepID and Active=1;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wOrderID", wOrder.ID);
			wParamMap.put("PartID", wPartID);
			wParamMap.put("StepID", wStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wStatus = StringUtils.parseInt(wReader.get("Status"));
				if (wStatus == 5) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
