package com.mes.qms.server.serviceimpl.dao.sfc;

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
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.sfc.SFCReturnOverMaterial;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.CoreServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.TaskBaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

/**
 * 偶换件不合格评审
 * 
 * @author ShrisJava
 *
 */
public class SFCReturnOverMaterialDAO extends BaseDAO implements TaskBaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCReturnOverMaterialDAO.class);

	private static SFCReturnOverMaterialDAO Instance = null;

	private SFCReturnOverMaterialDAO() {
		super();
	}

	public static SFCReturnOverMaterialDAO getInstance() {
		if (Instance == null)
			Instance = new SFCReturnOverMaterialDAO();
		return Instance;
	}

	/**
	 * 添加或修改
	 * 
	 * @param wSFCReturnOverMaterial
	 * @return
	 */
	public SFCReturnOverMaterial Update(BMSEmployee wLoginUser, SFCReturnOverMaterial wSFCReturnOverMaterial,
			OutResult<Integer> wErrorCode) {
		SFCReturnOverMaterial wResult = new SFCReturnOverMaterial();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCReturnOverMaterial == null)
				return wResult;

			if (wSFCReturnOverMaterial.FollowerID == null) {
				wSFCReturnOverMaterial.FollowerID = new ArrayList<Integer>();
			}

			String wSQL = "";
			if (wSFCReturnOverMaterial.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.sfc_returnovermaterial(Code,FlowType,FlowID,UpFlowID,FollowerID,"
								+ "Status,StatusText,CreateTime,SubmitTime,MaterialID,MaterialNo,"
								+ "Reason,ReturnDemand,PartID) "
								+ "VALUES(:Code,:FlowType,:FlowID,:UpFlowID,:FollowerID,:Status,:StatusText,"
								+ ":CreateTime,:SubmitTime,:MaterialID,:MaterialNo,:Reason,:ReturnDemand,:PartID);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.sfc_returnovermaterial SET Code = :Code,FlowType = :FlowType,"
						+ "FlowID = :FlowID,UpFlowID = :UpFlowID,FollowerID = :FollowerID,Status = :Status,"
						+ "StatusText = :StatusText,CreateTime = :CreateTime,SubmitTime = now(),"
						+ "MaterialID = :MaterialID,MaterialNo = :MaterialNo,Reason = :Reason,"
						+ "ReturnDemand=:ReturnDemand,PartID=:PartID WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCReturnOverMaterial.ID);
			wParamMap.put("Code", wSFCReturnOverMaterial.Code);
			wParamMap.put("FlowType", wSFCReturnOverMaterial.FlowType);
			wParamMap.put("FlowID", wSFCReturnOverMaterial.FlowID);
			wParamMap.put("UpFlowID", wSFCReturnOverMaterial.UpFlowID);
			wParamMap.put("FollowerID", StringUtils.Join(",", wSFCReturnOverMaterial.FollowerID));
			wParamMap.put("Status", wSFCReturnOverMaterial.Status);
			wParamMap.put("StatusText", wSFCReturnOverMaterial.StatusText);
			wParamMap.put("CreateTime", wSFCReturnOverMaterial.CreateTime);
			wParamMap.put("SubmitTime", wSFCReturnOverMaterial.SubmitTime);
			wParamMap.put("MaterialID", wSFCReturnOverMaterial.MaterialID);
			wParamMap.put("MaterialNo", wSFCReturnOverMaterial.MaterialNo);
			wParamMap.put("Reason", wSFCReturnOverMaterial.Reason);
			wParamMap.put("ReturnDemand", wSFCReturnOverMaterial.ReturnDemand);
			wParamMap.put("PartID", wSFCReturnOverMaterial.PartID);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCReturnOverMaterial.getID() <= 0) {
				wSFCReturnOverMaterial.setID(keyHolder.getKey().intValue());
			}
			wResult = wSFCReturnOverMaterial;
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<SFCReturnOverMaterial> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID,
			int wMaterialID, int wPartID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStateIDList,
			OutResult<Integer> wErrorCode) {
		return SelectList(wLoginUser, wID, wCode, wUpFlowID, wMaterialID, wPartID, wStartTime, wEndTime, wStateIDList,
				null, wErrorCode);
	}

	public List<SFCReturnOverMaterial> SelectList(BMSEmployee wLoginUser, int wMaterialID, int wPartID,
			Calendar wStartTime, Calendar wEndTime, List<Integer> wStateIDList, List<Integer> wNoStateIDList,
			OutResult<Integer> wErrorCode) {
		return SelectList(wLoginUser, -1, "", -1, wMaterialID, wPartID, wStartTime, wEndTime, wStateIDList,
				wNoStateIDList, wErrorCode);
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	private List<SFCReturnOverMaterial> SelectList(BMSEmployee wLoginUser, int wID, String wCode, int wUpFlowID,
			int wMaterialID, int wPartID, Calendar wStartTime, Calendar wEndTime, List<Integer> wStateIDList,
			List<Integer> wNoStateIDList, OutResult<Integer> wErrorCode) {
		List<SFCReturnOverMaterial> wResultList = new ArrayList<SFCReturnOverMaterial>();
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

			if (wStateIDList == null) {
				wStateIDList = new ArrayList<Integer>();
			}
			wStateIDList.removeIf(p -> p < 0);
			if (wNoStateIDList == null) {
				wNoStateIDList = new ArrayList<Integer>();
			}
			wNoStateIDList.removeIf(p -> p < 0);

			String wSQL = StringUtils.Format("SELECT t.*,t1.MaterialName FROM {0}.sfc_returnovermaterial t"
					+ " left join {0}.mss_material t1 on t.MaterialID=t1.ID where 1=1  "
					+ " and ( :wID <= 0 or :wID = t.ID ) " + "and ( :wCode = '''' or :wCode = t.Code ) "
					+ " and ( :wUpFlowID <= 0 or :wUpFlowID = t.UpFlowID ) "
					+ " and ( :wPartID <= 0 or :wPartID = t.PartID ) "
					+ " and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  t.SubmitTime ) "
					+ " and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  t.CreateTime ) "
					+ " and ( :wMaterialID <= 0 or :wMaterialID = t.MaterialID ) "
					+ " and ( :wStatus = '''' or t.Status in ({1}))"
					+ " and ( :wNoStatus = '''' or t.Status not in ({2}));", wInstance.Result,
					wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0",
					wNoStateIDList.size() > 0 ? StringUtils.Join(",", wNoStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCode", wCode);
			wParamMap.put("wUpFlowID", wUpFlowID);
			wParamMap.put("wPartID", wPartID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wMaterialID", wMaterialID);
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));
			wParamMap.put("wNoStatus", StringUtils.Join(",", wNoStateIDList));

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
	 * 赋值
	 */
	private void SetValue(BMSEmployee wLoginUser, List<SFCReturnOverMaterial> wResultList,
			List<Map<String, Object>> wQueryResult) {
		try {
			for (Map<String, Object> wReader : wQueryResult) {
				SFCReturnOverMaterial wItem = new SFCReturnOverMaterial();

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
				wItem.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wItem.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wItem.Reason = StringUtils.parseString(wReader.get("Reason"));
				wItem.ReturnDemand = StringUtils.parseString(wReader.get("ReturnDemand"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));

				wItem.UpFlowName = QMSConstants.GetBMSEmployeeName(wItem.UpFlowID);
				wItem.FollowerName = GetNames(wItem.FollowerID);

				wItem.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));
				wItem.PartName = QMSConstants.GetFPCPartName(wItem.PartID);
				wItem.PartCode = QMSConstants.GetFPCPartCode(wItem.PartID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取处理人名称(多人)
	 */
	private String GetNames(List<Integer> wIDList) {
		String wResult = "";
		try {
			if (wIDList == null || wIDList.size() <= 0) {
				return wResult;
			}

			List<String> wNames = new ArrayList<String>();
			wIDList.forEach(p -> {
				if (StringUtils.isNotEmpty(QMSConstants.GetBMSEmployeeName(p))) {
					wNames.add(QMSConstants.GetBMSEmployeeName(p));
				}
			});

			if (wNames.size() > 0) {
				wResult = StringUtils.Join(",", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ID集合获取任务集合
	 */
	private List<SFCReturnOverMaterial> SelectList(BMSEmployee wLoginUser, List<Integer> wTaskIDList,
			Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<SFCReturnOverMaterial> wResultList = new ArrayList<SFCReturnOverMaterial>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
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

			String wSQL = StringUtils.Format("SELECT t.*,t1.MaterialName FROM {0}.sfc_returnovermaterial t"
					+ " left join  {0}.mss_material t1 on t.MaterialID=t1.ID where 1=1  "
					+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  t.SubmitTime ) "
					+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  t.CreateTime ) "
					+ "and ( :wIDs = '''' or t.ID in ({1}));", wInstance.Result,
					wTaskIDList.size() > 0 ? StringUtils.Join(",", wTaskIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wIDs", StringUtils.Join(",", wTaskIDList));
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
	 * 获取最新的编码
	 */
	public String GetNewCode(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
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
					"select count(*)+1 as Number from {0}.sfc_returnovermaterial where CreateTime > :wSTime and CreateTime < :wETime;",
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

			wResult = StringUtils.Format("RM{0}{1}{2}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
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
		List<SFCReturnOverMaterial> wResult = new ArrayList<SFCReturnOverMaterial>();
		try {
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.ReturnOverMaterial.getValue(),
							-1, BFCMessageType.Task.getValue(), 0, -1, null, null)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.ReturnOverMaterial.getValue(), -1, BFCMessageType.Task.getValue(), 1, -1, null, null)
					.List(BFCMessage.class));
			wMessageList.addAll(CoreServiceImpl.getInstance().BFC_GetMessageList(wLoginUser, wLoginUser.getID(),
					BPMEventModule.ReturnOverMaterial.getValue(), -1, BFCMessageType.Task.getValue(), 2, -1, null, null)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			// 所有未完成的任务
			Map<Integer, SFCReturnOverMaterial> wTaskMap = new HashMap<Integer, SFCReturnOverMaterial>();
			if (wTaskIDList != null && wTaskIDList.size() > 0) {
				List<SFCReturnOverMaterial> wMTCTaskListTemp = this.SelectList(wLoginUser, wTaskIDList, null, null,
						wErrorCode);

				wTaskMap = wMTCTaskListTemp.stream().collect(Collectors.toMap(p -> p.ID, p -> p, (o1, o2) -> o1));

			}
			SFCReturnOverMaterial wTaskTemp = null;
			for (BFCMessage wBFCMessage : wMessageList) {
				if (!wTaskMap.containsKey((int) wBFCMessage.getMessageID()))
					continue;

				wTaskTemp = CloneTool.Clone(wTaskMap.get((int) wBFCMessage.getMessageID()),
						SFCReturnOverMaterial.class);
				wTaskTemp.StepID = wBFCMessage.getStepID();
				wResult.add(wTaskTemp);
			}

			wResult.sort(Comparator.comparing(SFCReturnOverMaterial::getSubmitTime).reversed());
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
		List<SFCReturnOverMaterial> wResult = new ArrayList<SFCReturnOverMaterial>();
		wErrorCode.set(0);
		try {
			List<SFCReturnOverMaterial> wTaskList = new ArrayList<SFCReturnOverMaterial>();
			// 获取所有任务消息
			List<BFCMessage> wMessageList = CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.ReturnOverMaterial.getValue(),
							-1, BFCMessageType.Task.getValue(), 3, -1, wStartTime, wEndTime)
					.List(BFCMessage.class);
			wMessageList.addAll(CoreServiceImpl.getInstance()
					.BFC_GetMessageList(wLoginUser, wLoginUser.getID(), BPMEventModule.ReturnOverMaterial.getValue(),
							-1, BFCMessageType.Task.getValue(), 4, -1, wStartTime, wEndTime)
					.List(BFCMessage.class));

			List<Integer> wTaskIDList = wMessageList.stream().map(p -> (int) p.MessageID).distinct()
					.collect(Collectors.toList());

			wTaskList = this.SelectList(wLoginUser, wTaskIDList, wStartTime, wEndTime, wErrorCode);

			wTaskList.sort(Comparator.comparing(SFCReturnOverMaterial::getSubmitTime).reversed());

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
		List<SFCReturnOverMaterial> wResult = new ArrayList<SFCReturnOverMaterial>();
		try {
			wResult = this.SelectList(wLoginUser, -1, "", wResponsorID, -1, -1, wStartTime, wEndTime, null, wErrorCode);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return new ArrayList<BPMTaskBase>(wResult);
	}

	@Override
	public BPMTaskBase BPM_UpdateTask(BMSEmployee wLoginUser, BPMTaskBase wTask, OutResult<Integer> wErrorCode) {
		BPMTaskBase wResult = new BPMTaskBase();
		try {
			wResult = this.Update(wLoginUser, (SFCReturnOverMaterial) wTask, wErrorCode);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public BPMTaskBase BPM_GetTaskInfo(BMSEmployee wLoginUser, int wTaskID, String wCode,
			OutResult<Integer> wErrorCode) {
		SFCReturnOverMaterial wResult = new SFCReturnOverMaterial();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCReturnOverMaterial> wList = this.SelectList(wLoginUser, wTaskID, wCode, -1, -1, -1, null, null,
					null, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(e.toString());
		}
		return wResult;
	}
}
