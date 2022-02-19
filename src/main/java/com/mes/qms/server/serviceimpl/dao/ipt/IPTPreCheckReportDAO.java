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

import com.mes.qms.server.service.mesenum.APSOperateType;
import com.mes.qms.server.service.mesenum.BFCMessageType;
import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.BPMStatus;
import com.mes.qms.server.service.mesenum.IPTPreCheckProblemStatus;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bfc.BFCAuditAction;
import com.mes.qms.server.service.po.bfc.BFCAuditConfig;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.ipt.IPTPreCheckItem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckReport;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.CoreServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class IPTPreCheckReportDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTPreCheckReportDAO.class);

	private static IPTPreCheckReportDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTPreCheckReport
	 * @return
	 */
	public IPTPreCheckReport Update(BMSEmployee wLoginUser, IPTPreCheckReport wIPTPreCheckReport,
			OutResult<Integer> wErrorCode) {
		IPTPreCheckReport wResult = new IPTPreCheckReport();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTPreCheckReport == null)
				return wResult;

			String wSQL = "";
			if (wIPTPreCheckReport.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.ipt_precheckreport(Code,FlowType,FlowID,UpFlowID,FollowerID,"
								+ "OrderID,OrderNo,PartNo,CustomerName,CreateID,CreateTime,SubmitTime,"
								+ "AuditID,AuditTime,Status,StatusText) VALUES(:Code,:FlowType,:FlowID,"
								+ ":UpFlowID,:FollowerID,:OrderID,:OrderNo,:PartNo,:CustomerName,:CreateID,"
								+ ":CreateTime,:SubmitTime,:AuditID,:AuditTime,:Status,:StatusText);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.ipt_precheckreport SET Code = :Code,FlowType = :FlowType,"
						+ "FlowID = :FlowID,UpFlowID = :UpFlowID,FollowerID = :FollowerID,"
						+ "OrderID = :OrderID,OrderNo = :OrderNo,PartNo = :PartNo,CustomerName = :CustomerName,"
						+ "CreateID = :CreateID,CreateTime = :CreateTime,SubmitTime = :SubmitTime,"
						+ "AuditID = :AuditID,AuditTime = :AuditTime,Status = :Status,StatusText = :StatusText "
						+ "WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTPreCheckReport.ID);
			wParamMap.put("Code", wIPTPreCheckReport.Code);
			wParamMap.put("FlowType", wIPTPreCheckReport.FlowType);
			wParamMap.put("FlowID", wIPTPreCheckReport.FlowID);
			wParamMap.put("UpFlowID", wIPTPreCheckReport.UpFlowID);
			wParamMap.put("FollowerID", StringUtils.Join(",", wIPTPreCheckReport.FollowerID));
			wParamMap.put("OrderID", wIPTPreCheckReport.OrderID);
			wParamMap.put("OrderNo", wIPTPreCheckReport.OrderNo);
			wParamMap.put("PartNo", wIPTPreCheckReport.PartNo);
			wParamMap.put("CustomerName", wIPTPreCheckReport.CustomerName);
			wParamMap.put("CreateID", wIPTPreCheckReport.CreateID);
			wParamMap.put("CreateTime", wIPTPreCheckReport.CreateTime);
			wParamMap.put("SubmitTime", wIPTPreCheckReport.SubmitTime);
			wParamMap.put("AuditID", wIPTPreCheckReport.AuditID);
			wParamMap.put("AuditTime", wIPTPreCheckReport.AuditTime);
			wParamMap.put("Status", wIPTPreCheckReport.Status);
			wParamMap.put("StatusText", wIPTPreCheckReport.StatusText);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTPreCheckReport.getID() <= 0) {
				wIPTPreCheckReport.setID(keyHolder.getKey().intValue());
			}
			wResult = wIPTPreCheckReport;
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
	public IPTPreCheckReport SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTPreCheckReport wResult = new IPTPreCheckReport();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTPreCheckReport> wList = SelectList(wLoginUser, wID, "", -1, -1, null, null, null, wErrorCode);
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
	public List<IPTPreCheckReport> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID,
			int wOrderID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStateIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTPreCheckReport> wResultList = new ArrayList<IPTPreCheckReport>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wAPSInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wAPSInstance.ErrorCode);
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

			String wSQL = StringUtils.Format("SELECT t.*,o.LineID FROM {0}.ipt_precheckreport t"
					+ " left join {1}.oms_order o on t.OrderID=o.ID WHERE  1=1  " + "and ( :wID <= 0 or :wID = t.ID ) "
					+ "and ( :wCode = '''' or :wCode = t.Code ) "
					+ "and ( :wUpFlowID <= 0 or :wUpFlowID = t.UpFlowID ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = t.OrderID ) "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  t.SubmitTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  t.CreateTime ) "
					+ "and ( :wStatus is null or :wStatus = '''' or t.Status in ({2}));", wInstance.Result,
					wAPSInstance.Result, wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 审批
	 * 
	 * @return
	 */
	public int Audit(BMSEmployee wLoginUser, IPTPreCheckReport wIPTPreCheckReport, int wOperateType,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			if (wIPTPreCheckReport == null) {
				wErrorCode.set(MESException.Parameter.getValue());
				return wResult;
			}

			int wModuleID = BPMEventModule.YJReport.getValue();

			BFCAuditConfig wCurrentConfig = CoreServiceImpl.getInstance()
					.BFC_CurrentConfig(wLoginUser, wModuleID, wIPTPreCheckReport.ID, wLoginUser.getID())
					.Info(BFCAuditConfig.class);
			if (wCurrentConfig == null || wCurrentConfig.ID <= 0) {
				wErrorCode.set(MESException.UnPower.getValue());
				return wResult;
			}
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC,
					wCurrentConfig.FunctionID);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}
			if (wCurrentConfig.AuditActions != null
					&& wCurrentConfig.AuditActions.stream().filter(p -> p == wOperateType).count() == 0) {
				wErrorCode.set(MESException.UnPower.getValue());
				return wResult;
			}

			BFCAuditAction wAction = new BFCAuditAction();
			wAction.TaskID = wIPTPreCheckReport.ID;
			wAction.EventModule = wCurrentConfig.EventModule;
			wAction.ConfigID = wCurrentConfig.ID;
			wAction.ConfigName = wCurrentConfig.Name;
			wAction.AuditorID = wLoginUser.getID();
			wAction.AuditorName = wLoginUser.getName();
			wAction.Result = wOperateType;

			switch (APSOperateType.getEnumType(wOperateType)) {
			case Reject:
			case Cancel:
				wIPTPreCheckReport.Status = BPMStatus.Save.getValue();
				break;
			case Audit:
				wIPTPreCheckReport.Status = BPMStatus.ToAudit.getValue();
				break;
			case Submit:
				wIPTPreCheckReport.Status = BPMStatus.ToAudit.getValue();
				break;
			default:
				break;
			}

			// 提交Action
			CoreServiceImpl.getInstance().BFC_UpdateAction(wLoginUser, wAction,
					StringUtils.Format("{0} {1}{3}了{2}预检报告", BPMEventModule.getEnumType(wModuleID).getLable(),
							wLoginUser.Name, wIPTPreCheckReport.PartNo,
							APSOperateType.getEnumType(wAction.Result).getLable()));
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wOperateType == APSOperateType.Audit.getValue()) {
				wCurrentConfig = CoreServiceImpl.getInstance()
						.BFC_CurrentConfig(wLoginUser, wModuleID, wIPTPreCheckReport.ID, wLoginUser.getID())
						.Info(BFCAuditConfig.class);
				if (wCurrentConfig == null || wCurrentConfig.ID <= 0) {
					wIPTPreCheckReport.Status = BPMStatus.Audited.getValue();
				}
			}

			String wSQL = StringUtils.Format(
					"UPDATE {0}.ipt_precheckreport  SET Status = :Status,AuditID=:AuditID,AuditTime=now()  WHERE ID = :ID;",
					wInstance.Result);

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTPreCheckReport.ID);
			wParamMap.put("Status", wIPTPreCheckReport.Status);
			wParamMap.put("AuditID", wLoginUser.ID);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTPreCheckReport.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTPreCheckReport.setID(wResult);
			} else {
				wResult = wIPTPreCheckReport.getID();
			}

			// 将问题项的状态改为已下发
			if (wIPTPreCheckReport.Status == BPMStatus.Audited.getValue()) {
				List<IPTPreCheckProblem> wList = new ArrayList<IPTPreCheckProblem>();
				for (IPTPreCheckItem wIPTPreCheckItem : wIPTPreCheckReport.IPTPreCheckItemList) {
					if (wIPTPreCheckItem.IPTProblemList == null || wIPTPreCheckItem.IPTProblemList.size() <= 0) {
						continue;
					}
					wList.addAll(wIPTPreCheckItem.IPTProblemList);
				}
				for (IPTPreCheckProblem wIPTPreCheckProblem : wList) {
					wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.Issued.getValue();
					IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);
				}
			} else if (wOperateType == APSOperateType.Submit.getValue()) {
				List<IPTPreCheckProblem> wList = new ArrayList<IPTPreCheckProblem>();
				for (IPTPreCheckItem wIPTPreCheckItem : wIPTPreCheckReport.IPTPreCheckItemList) {
					if (wIPTPreCheckItem.IPTProblemList == null || wIPTPreCheckItem.IPTProblemList.size() <= 0) {
						continue;
					}
					wList.addAll(wIPTPreCheckItem.IPTProblemList);
				}
				for (IPTPreCheckProblem wIPTPreCheckProblem : wList) {
					wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.Auditing.getValue();
					IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);
				}
			} else if (wOperateType == APSOperateType.Reject.getValue()) {
				List<IPTPreCheckProblem> wList = new ArrayList<IPTPreCheckProblem>();
				for (IPTPreCheckItem wIPTPreCheckItem : wIPTPreCheckReport.IPTPreCheckItemList) {
					if (wIPTPreCheckItem.IPTProblemList == null || wIPTPreCheckItem.IPTProblemList.size() <= 0) {
						continue;
					}
					wList.addAll(wIPTPreCheckItem.IPTProblemList);
				}
				for (IPTPreCheckProblem wIPTPreCheckProblem : wList) {
					wIPTPreCheckProblem.Status = IPTPreCheckProblemStatus.ToSendItem.getValue();
					IPTPreCheckProblemDAO.getInstance().Update(wLoginUser, wIPTPreCheckProblem, wErrorCode);
				}
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ID集合获取任务集合
	 */
	private List<IPTPreCheckReport> SelectList(BMSEmployee wLoginUser, List<Integer> wTaskIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTPreCheckReport> wResultList = new ArrayList<IPTPreCheckReport>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}
			ServiceResult<String> wAPSInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wAPSInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}
			if (wTaskIDList == null || wTaskIDList.size() <= 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT t.*,o.LineID FROM {0}.ipt_precheckreport t"
							+ " left join {1}.oms_order o on t.OrderID=o.ID WHERE  1=1  "
							+ "and ( :wIDs = '''' or t.ID in ({2}));",
					wInstance.Result, wAPSInstance.Result,
					wTaskIDList.size() > 0 ? StringUtils.Join(",", wTaskIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wIDs", StringUtils.Join(",", wTaskIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	/**
	 * 根据ID集合查询预检报告集合
	 * 
	 * @param wLoginUser
	 * @param wIDList
	 * @return
	 */
	public List<IPTPreCheckReport> SelectByIDList(BMSEmployee wLoginUser, List<Integer> wIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTPreCheckReport> wResultList = new ArrayList<IPTPreCheckReport>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}
			ServiceResult<String> wAPSInstance = this.GetDataBaseName(wLoginUser, MESDBSource.APS, 0);
			wErrorCode.set(wAPSInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			if (wIDList == null) {
				wIDList = new ArrayList<Integer>();
			}

			String wSQL = MessageFormat.format(
					"SELECT t.*,o.LineID FROM {0}.ipt_precheckreport t"
							+ " left join {1}.oms_order o on t.OrderID=o.ID WHERE  1=1  "
							+ "and ( :wIDs = '''' or t.ID in ({2}));",
					wInstance.Result, wAPSInstance.Result, wIDList.size() > 0 ? StringUtils.Join(",", wIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wIDs", StringUtils.Join(",", wIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValue(wLoginUser, wErrorCode, wResultList, wQueryResult);
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
					"select count(*)+1 as Number from {0}.ipt_precheckreport where CreateTime > :wSTime and CreateTime < :wETime;",
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

			wResult = StringUtils.Format("RE{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
					String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1),
					String.format("%04d", wNumber));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 赋值操作
	 */
	private void SetValue(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode, List<IPTPreCheckReport> wResultList,
			List<Map<String, Object>> wQueryResult) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				IPTPreCheckReport wItem = new IPTPreCheckReport();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Code = StringUtils.parseString(wReader.get("Code"));
				wItem.FlowType = StringUtils.parseInt(wReader.get("FlowType"));
				wItem.FlowID = StringUtils.parseInt(wReader.get("FlowID"));
				wItem.UpFlowID = StringUtils.parseInt(wReader.get("UpFlowID"));
				wItem.FollowerID = StringUtils
						.parseIntList((StringUtils.parseString(wReader.get("FollowerID"))).split(",|;"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.OrderNo = StringUtils.parseString(wReader.get("OrderNo"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.CustomerName = StringUtils.parseString(wReader.get("CustomerName"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wItem.AuditID = StringUtils.parseInt(wReader.get("AuditID"));
				wItem.AuditTime = StringUtils.parseCalendar(wReader.get("AuditTime"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.StatusText = StringUtils.parseString(wReader.get("StatusText"));

				// 子项集合
				wItem.IPTPreCheckItemList = IPTPreCheckItemDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID,
						wErrorCode);

				wItem.LineName = QMSConstants.GetFMCLineName(StringUtils.parseInt(wReader.get("LineID")));

				wItem.Creator = QMSConstants.GetBMSEmployeeName(wItem.CreateID);
				wItem.Auditor = QMSConstants.GetBMSEmployeeName(wItem.AuditID);
				wItem.UpFlowName = QMSConstants.GetBMSEmployeeName(wItem.UpFlowID);
				wItem.Auditor = QMSConstants.GetBMSEmployeeName(wItem.AuditID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private IPTPreCheckReportDAO() {
		super();
	}

	public static IPTPreCheckReportDAO getInstance() {
		if (Instance == null)
			Instance = new IPTPreCheckReportDAO();
		return Instance;
	}

	@Override
	public List<BPMTaskBase> BPM_GetUndoTaskList(BMSEmployee wLoginUser, int wResponsorID,
			OutResult<Integer> wErrorCode) {
		List<IPTPreCheckReport> wResult = new ArrayList<IPTPreCheckReport>();
		try {
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.YJReport.getValue(), -1, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.YJReport.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));
			wMessageList.addAll(CoreServiceImpl
					.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
							BPMEventModule.YJReport.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			Map<Integer, IPTPreCheckReport> wTaskMap = new HashMap<Integer, IPTPreCheckReport>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				List<IPTPreCheckReport> wMTCTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, wErrorCode);

				wTaskMap = wMTCTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			IPTPreCheckReport wTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wTaskTemp = CloneTool.Clone(wTaskMap.get((int) wBFCMessage.getMessageID()), IPTPreCheckReport.class);
				wTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wTaskTemp);
			}

			wResult.sort(Comparator.comparing(IPTPreCheckReport::getSubmitTime).reversed());
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
		List<IPTPreCheckReport> wResult = new ArrayList<IPTPreCheckReport>();
		wErrorCode.set(0);
		try {
			List<IPTPreCheckReport> wTaskList = new ArrayList<IPTPreCheckReport>();
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.YJReport.getValue(), -1,
							BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList
					.addAll(CoreServiceImpl.getInstance()
							.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.YJReport.getValue(), -1,
									BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
							.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			wTaskList = this.SelectList(wLoginUser, wTaskIDList, wErrorCode);

			wTaskList.sort(Comparator.comparing(IPTPreCheckReport::getSubmitTime).reversed());

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
		List<IPTPreCheckReport> wResult = new ArrayList<IPTPreCheckReport>();
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
			wResult = this.Update(wLoginUser, (IPTPreCheckReport) wTask, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		IPTPreCheckReport wResult = new IPTPreCheckReport();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTPreCheckReport> wList = this.SelectList(wLoginUser, wTaskID, wCode, -1, -1, null, null, null,
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
	 * 查询有预检记录的订单-时间段查询
	 */
	public List<OMSOrder> IPT_QueryOrderList(BMSEmployee wLoginUser, Calendar wStartTime, Calendar wEndTime,
			OutResult<Integer> wErrorCode) {
		List<OMSOrder> wResult = new ArrayList<OMSOrder>();
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

			String wSQL = StringUtils.Format("SELECT * FROM {0}.oms_order "
					+ "where RealReceiveDate>= :wStartTime and RealReceiveDate <= :wEndTime "
					+ "and Active=1 and ID in (SELECT distinct OrderID FROM {1}.sfc_taskipt "
					+ "where TaskType=14 and Active=1);", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				OMSOrder wItem = new OMSOrder();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.OrderNo = StringUtils.parseString(wReader.get("OrderNo"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.BureauSectionID = StringUtils.parseInt(wReader.get("BureauSectionID"));
				wItem.BureauSection = QMSConstants.GetCRMCustomerName(wItem.BureauSectionID);
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.LineName = QMSConstants.GetFMCLineName(wItem.LineID);

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
