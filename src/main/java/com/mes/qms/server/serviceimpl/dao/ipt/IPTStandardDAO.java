package com.mes.qms.server.serviceimpl.dao.ipt;

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

import com.mes.qms.server.service.mesenum.IPTMode;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.fmc.FMCLineUnit;
import com.mes.qms.server.service.po.ipt.IPTConstants;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTProblemBomItem;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.ipt.IPTStandard;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.mss.MSSMaterial;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.LOCOAPSServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class IPTStandardDAO extends BaseDAO {
	private static Logger logger = LoggerFactory.getLogger(IPTStandardDAO.class);

	private static IPTStandardDAO Instance;

	private IPTStandardDAO() {
		super();
	}

	public static IPTStandardDAO getInstance() {
		if (Instance == null)
			Instance = new IPTStandardDAO();
		return Instance;
	}

	public ServiceResult<IPTStandard> SelectIPTStandardCurrent(BMSEmployee wLoginUser, int wCompanyID, IPTMode wIPTMode,
			int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID, int wLineID, int wPartID,
			int wPartPointID, int wStationID, String wProductNo, OutResult<Integer> wErrorCode) {
		ServiceResult<IPTStandard> wResult = new ServiceResult<IPTStandard>();
		try {
			if (wProductNo == null)
				wProductNo = "";
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			List<IPTStandard> wIPTStandardList = SelectIPTStandard(wLoginUser, -1, wCompanyID, wIPTMode, wCustomID,
					wBusinessUnitID, wBaseID, wFactoryID, wWorkShopID, wLineID, wPartID, wPartPointID, wStationID, -1,
					wProductNo, 1, IPTConstants.getIPTConfigsObject().getTOPNum(), wBaseTime, wBaseTime, wErrorCode);
			if (wIPTStandardList.size() < 1) {
				wResult.FaultCode = StringUtils.Format(
						"标准的IPTStandard表中CompanyID={0},IPTMode={1},LineID={2},PartPointID={3},StationID={4},ProductNo={5}的当前标准未找到",
						wCompanyID, wIPTMode, wLineID, wPartPointID, wStationID, wProductNo);
				return wResult;
			} else if (wIPTStandardList.size() > 1) {
				wResult.FaultCode = StringUtils.Format(
						"标准的IPTStandard表中CompanyID={0},IPTMode={1},LineID={2},PartPointID={3},StationID={4},ProductNo={5}的当前标准个数大于1",
						wCompanyID, wIPTMode, wLineID, wPartPointID, wStationID, wProductNo);
			} else {
				wResult.Result = wIPTStandardList.get(0);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<IPTStandard> SelectIPTStandardCurrent(BMSEmployee wLoginUser, int wCompanyID, IPTMode wIPTMode,
			int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID, int wLineID, int wPartID,
			int wPartPointID, int wStationID, int wProductID, OutResult<Integer> wErrorCode) {
		ServiceResult<IPTStandard> wResult = new ServiceResult<IPTStandard>();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			List<IPTStandard> wIPTStandardList = SelectIPTStandard(wLoginUser, -1, wCompanyID, wIPTMode, wCustomID,
					wBusinessUnitID, wBaseID, wFactoryID, wWorkShopID, wLineID, wPartID, wPartPointID, wStationID,
					wProductID, "", 1, IPTConstants.getIPTConfigsObject().getTOPNum(), wBaseTime, wBaseTime,
					wErrorCode);
			if (wIPTStandardList.size() < 1) {
				// 查询关联的工序的标准
				if (wProductID > 0 && wLineID > 0 && wPartID > 0 && wPartPointID > 0) {
					int wRelaStandardID = IPTStandardDAO.getInstance().SelectRelaStandardID(wLoginUser,
							wIPTMode.getValue(), wProductID, wLineID, wPartID, wPartPointID, wCustomID, wErrorCode);
					if (wRelaStandardID > 0) {
						wResult = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, wRelaStandardID,
								wErrorCode);
						return wResult;
					}
				}

				wResult.FaultCode = StringUtils.Format(
						"标准的IPTStandard表中CompanyID={0},IPTMode={1},LineID={2},PartPointID={3},StationID={4},ProductID={5}的当前标准未找到",
						wCompanyID, wIPTMode, wLineID, wPartPointID, wStationID, wProductID);
				return wResult;
			} else if (wIPTStandardList.size() > 1) {
				wResult.FaultCode = StringUtils.Format(
						"标准的IPTStandard表中CompanyID={0},IPTMode={1},LineID={2},PartPointID={3},StationID={4},ProductID={5}的当前标准个数大于1",
						wCompanyID, wIPTMode, wLineID, wPartPointID, wStationID, wProductID);
			} else {
				wResult.Result = wIPTStandardList.get(0);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private int SelectRelaStandardID(BMSEmployee wLoginUser, int wIPTMode, int wProductID, int wLineID, int wPartID,
			int wPartPointID, int wCustomID, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select ID from {0}.ipt_standard where lineid=:wLineID and partid=:wPartID "
							+ "and productid=:wProductID and  (:wCustomID <=0 or CustomID=:wCustomID ) "
							+ "and (:wIPTMode <=0 or iptmode=:wIPTMode) "
							+ "and iscurrent=1 and find_in_set(''{1}'',StepIDs);",
					wInstance.Result, String.valueOf(wPartPointID));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wPartID", wPartID);
			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wCustomID", wCustomID);
			wParamMap.put("wIPTMode", wIPTMode);

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

	public List<IPTStandard> SelectIPTStandard(BMSEmployee wLoginUser, long wStandardID, int wCompanyID,
			IPTMode wIPTMode, int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID,
			int wLineID, int wPartID, int wPartPointID, int wStationID, int wProductID, String wProductNo, int wCurrent,
			int wNum, Calendar wStartTime, Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<IPTStandard> wResult = new ArrayList<IPTStandard>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			if (wStartTime == null || wStartTime.compareTo(wBaseTime) < 0)
				wStartTime = wBaseTime;
			if (wEndTime == null || wEndTime.compareTo(wBaseTime) < 0)
				wEndTime = wBaseTime;
			if (wStartTime.compareTo(wEndTime) > 0)
				return wResult;

			if (wProductNo == null)
				wProductNo = "";

			String wSQL = StringUtils.Format("SELECT * "
					+ "FROM {0}.ipt_standard WHERE 1=1   and ( :wID <= 0 or ID = :wID)    "
					+ "and ( :wCompanyID < 0 or CompanyID = :wCompanyID)   "
					+ "and ( :wBusinessUnitID < 0 or BusinessUnitID = :wBusinessUnitID)     "
					+ "and ( :wBaseID < 0 or BaseID = :wBaseID)      "
					+ "and ( :wFactoryID < 0 or FactoryID = :wFactoryID)    "
					+ "and ( :wCustomID < 0 or CustomID = :wCustomID)     "
					+ "and ( :wWorkShopID < 0 or WorkShopID = :wWorkShopID)     "
					+ "and ( :wLineID < 0 or LineID = :wLineID)    and ( :wPartID < 0 or PartID = :wPartID)   "
					+ "and ( :wPartPointID < 0 or PartPointID = :wPartPointID)    "
					+ "and ( :wStationID < 0 or StationID = :wStationID)   "
					+ "and ( :wIPTMode <= 0 or IPTMode = :wIPTMode)   "
					+ "and ( :wCurrent < 0 or IsCurrent = :wCurrent)    "
					+ "and ( :wProductID <= 0 or ProductID = :wProductID)   "
					+ "and ( :wProductNo is null or :wProductNo = '''' or ProductNo = :wProductNo) "
					+ "and ( :wDTStart <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or TModify > :wDTStart) "
					+ "and ( :wDTEnd <= str_to_date(''2010-01-01'', ''%Y-%m-%d'') or TModify < :wDTEnd) order by TModify desc limit :wNum;",
					wInstance.Result);
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wID", wStandardID);
			wParamMap.put("wCompanyID", wCompanyID);
			wParamMap.put("wIPTMode", (int) wIPTMode.getValue());
			wParamMap.put("wBusinessUnitID", wBusinessUnitID);
			wParamMap.put("wBaseID", wBaseID);
			wParamMap.put("wFactoryID", wFactoryID);
			wParamMap.put("wWorkShopID", wWorkShopID);
			wParamMap.put("wCustomID", wCustomID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wPartID", wPartID);
			wParamMap.put("wPartPointID", wPartPointID);
			wParamMap.put("wStationID", wStationID);
			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wProductNo", wProductNo);
			wParamMap.put("wCurrent", wCurrent);
			wParamMap.put("wNum", wNum);
			wParamMap.put("wDTStart", wStartTime);
			wParamMap.put("wDTEnd", wEndTime);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]
			for (Map<String, Object> wReader : wQueryResult) {
				IPTStandard wIPTStandard = new IPTStandard();

				wIPTStandard.ID = StringUtils.parseLong(wReader.get("ID"));
				wIPTStandard.CompanyID = StringUtils.parseInt(wReader.get("CompanyID"));
				wIPTStandard.BusinessUnitID = StringUtils.parseInt(wReader.get("BusinessUnitID"));
				wIPTStandard.BaseID = StringUtils.parseInt(wReader.get("BaseID"));
				wIPTStandard.FactoryID = StringUtils.parseInt(wReader.get("FactoryID"));
				wIPTStandard.WorkShopID = StringUtils.parseInt(wReader.get("WorkShopID"));
				wIPTStandard.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wIPTStandard.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wIPTStandard.PartPointID = StringUtils.parseInt(wReader.get("PartPointID"));
				wIPTStandard.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wIPTStandard.IPTMode = StringUtils.parseInt(wReader.get("IPTMode"));
				wIPTStandard.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wIPTStandard.ProductNo = StringUtils.parseString(wReader.get("ProductNo"));
				wIPTStandard.UserID = StringUtils.parseInt(wReader.get("UserID"));
				wIPTStandard.IsCurrent = StringUtils.parseInt(wReader.get("IsCurrent"));
				wIPTStandard.IsEnd = StringUtils.parseInt(wReader.get("IsEnd"));
				wIPTStandard.IsUsed = StringUtils.parseInt(wReader.get("IsUsed"));
				wIPTStandard.TModify = StringUtils.parseCalendar(wReader.get("TModify"));
				wIPTStandard.Remark = StringUtils.parseString(wReader.get("Remark"));
				wIPTStandard.Version = StringUtils.parseString(wReader.get("Version"));
				wIPTStandard.PersonNumber = StringUtils.parseInt(wReader.get("PersonNumber"));
				wIPTStandard.Code = StringUtils.parseString(wReader.get("Code"));
				wIPTStandard.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wIPTStandard.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wIPTStandard.Status = StringUtils.parseInt(wReader.get("Status"));
				wIPTStandard.CustomID = StringUtils.parseInt(wReader.get("CustomID"));
				wIPTStandard.WorkTime = StringUtils.parseDouble(wReader.get("WorkTime"));
				wIPTStandard.IsPic = StringUtils.parseInt(wReader.get("IsPic"));
				wIPTStandard.StepIDs = StringUtils.parseString(wReader.get("StepIDs"));
				wIPTStandard.StepNames = GetStepNames(wIPTStandard.StepIDs);

				wIPTStandard.ItemList = new ArrayList<IPTItem>();

				wIPTStandard.PartName = QMSConstants.GetFPCPartName(wIPTStandard.PartID);
				wIPTStandard.PartPointName = QMSConstants.GetFPCStepName(wIPTStandard.PartPointID);
				wIPTStandard.LineName = QMSConstants.GetFMCLineName(wIPTStandard.LineID);
				wIPTStandard.CustomName = QMSConstants.GetCRMCustomerName(wIPTStandard.CustomID);

				wResult.add(wIPTStandard);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取工序
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
				Integer wStepID = StringUtils.parseInt(wStr);
				if (wStepID <= 0) {
					continue;
				}
				String wStepName = QMSConstants.GetFPCStepName(wStepID);
				if (StringUtils.isEmpty(wStepName)) {
					continue;
				}
				wNames.add(wStepName);
			}
			wResult = StringUtils.Join(",", wNames);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public ServiceResult<IPTStandard> SelectIPTStandard(BMSEmployee wLoginUser, long wStandardID,
			OutResult<Integer> wErrorCode) {
		ServiceResult<IPTStandard> wResult = new ServiceResult<IPTStandard>();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			List<IPTStandard> wIPTStandardList = SelectIPTStandard(wLoginUser, wStandardID, 0, IPTMode.Default, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1, "", -1, IPTConstants.getIPTConfigsObject().getTOPNum(), wBaseTime,
					wBaseTime, wErrorCode);
			if (wIPTStandardList.size() < 1) {
				wResult.FaultCode = StringUtils.Format("标准的Standard表中Version={0}的Standard未找到", wStandardID);
				return wResult;
			} else if (wIPTStandardList.size() > 1) {
				wResult.FaultCode = StringUtils.Format("标准的Standard表中Version={0}的Standard个数大于1", wStandardID);
			} else {
				wResult.Result = wIPTStandardList.get(0);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<List<IPTValue>> SelectValue(BMSEmployee wLoginUser, int wTaskID, int wIPTMode, int wItemType,
			OutResult<Integer> wErrorCode) {
		ServiceResult<List<IPTValue>> wResult = new ServiceResult<List<IPTValue>>();
		try {

			ServiceResult<Map<Integer, List<IPTValue>>> wIPTValueListDicResult = SelectValue(wLoginUser,
					StringUtils.parseList(new Integer[] { wTaskID }), wIPTMode, wItemType, wErrorCode);

			wResult.FaultCode = wIPTValueListDicResult.FaultCode;

			Map<Integer, List<IPTValue>> wIPTValueListDic = wIPTValueListDicResult.Result;
			if (wIPTValueListDic == null || !wIPTValueListDic.containsKey(wTaskID)) {
				wResult.FaultCode = StringUtils.Format("IPTValue表中TaskID={0}的值未找到", wTaskID);
				return wResult;
			} else {
				wResult.Result = wIPTValueListDic.get(wTaskID);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<Map<Integer, List<IPTValue>>> SelectValue(BMSEmployee wLoginUser, List<Integer> wTaskIDList,
			int wIPTMode, int wItemType, OutResult<Integer> wErrorCode) {
		ServiceResult<Map<Integer, List<IPTValue>>> wResult = new ServiceResult<Map<Integer, List<IPTValue>>>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			wResult.Result = new HashMap<Integer, List<IPTValue>>();
			if (wTaskIDList == null)
				wTaskIDList = new ArrayList<>();

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ipt_value WHERE 1=1  " + "and (:wIPTMode<=0 or IPTMode = :wIPTMode) "
							+ "and (:wItemType<=0 or ItemType = :wItemType) "
							+ "and ( :wTaskID is null or :wTaskID = '''' or TaskID IN( {1} ) ) ",
					wInstance.Result, wTaskIDList.size() > 0 ? StringUtils.Join(",", wTaskIDList) : "0");
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wTaskID", StringUtils.Join(",", wTaskIDList));
			wParamMap.put("wIPTMode", (int) wIPTMode);
			wParamMap.put("wItemType", wItemType);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			// wReader\[\"(\w+)\"\]
			String[] wTemps;
			for (Map<String, Object> wReader : wQueryResult) {
				IPTValue wIPTValue = new IPTValue();
				wIPTValue.ID = StringUtils.parseLong(wReader.get("ID"));

				wIPTValue.StandardID = StringUtils.parseInt(wReader.get("StandardID"));
				wIPTValue.IPTItemID = StringUtils.parseLong(wReader.get("IPTItemID"));
				wIPTValue.Value = StringUtils.parseString(wReader.get("Value"));
				wIPTValue.Remark = StringUtils.parseString(wReader.get("Remark"));
				wIPTValue.Result = StringUtils.parseInt(wReader.get("Result"));
				wIPTValue.IPTMode = StringUtils.parseInt(wReader.get("IPTMode"));
				wIPTValue.TaskID = StringUtils.parseInt(wReader.get("TaskID"));
				wIPTValue.ItemType = StringUtils.parseInt(wReader.get("ItemType"));
				// 图片
				wIPTValue.ImagePath = new ArrayList<String>();
				wTemps = StringUtils.parseString(wReader.get("ImagePath")).split(";");
				if (wTemps != null && wTemps.length > 0) {
					for (String wItem : wTemps) {
						if (StringUtils.isNotEmpty(wItem)) {
							wIPTValue.ImagePath.add(wItem);
						}
					}
				}
				// 视频
				wIPTValue.VideoPath = new ArrayList<String>();
				wTemps = StringUtils.parseString(wReader.get("VideoPath")).split(";");
				if (wTemps != null && wTemps.length > 0) {
					for (String wItem : wTemps) {
						if (StringUtils.isNotEmpty(wItem)) {
							wIPTValue.VideoPath.add(wItem);
						}
					}
				}

				wIPTValue.SolveID = StringUtils.parseInt(wReader.get("SolveID"));
				wIPTValue.SubmitID = StringUtils.parseInt(wReader.get("SubmitID"));

				wIPTValue.Submitor = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);

				wIPTValue.SubmitTime = StringUtils.parseCalendar(wReader.get("SubmitTime"));
				wIPTValue.Manufactor = StringUtils.parseString(wReader.get("Manufactor"));
				wIPTValue.Modal = StringUtils.parseString(wReader.get("Modal"));
				wIPTValue.Number = StringUtils.parseString(wReader.get("Number"));
				wIPTValue.Status = StringUtils.parseInt(wReader.get("Status"));
				wIPTValue.IPTProblemBomItemList = IPTProblemBomItem
						.StringToList(StringUtils.parseString(wReader.get("IPTProblemBomItemList")));

				wIPTValue.OrderID = StringUtils.parseInt(wReader.get("OrderID"));

				wIPTValue.DisassemblyComponents = StringUtils.parseString(wReader.get("DisassemblyComponents"));
				wIPTValue.RepairParts = StringUtils.parseString(wReader.get("RepairParts"));
				wIPTValue.AssemblyParts = StringUtils.parseString(wReader.get("AssemblyParts"));

				wIPTValue.SRDisassemblyComponents = StringUtils.parseString(wReader.get("SRDisassemblyComponents"));
				wIPTValue.SRRepairParts = StringUtils.parseString(wReader.get("SRRepairParts"));
				wIPTValue.SRAssemblyParts = StringUtils.parseString(wReader.get("SRAssemblyParts"));
				wIPTValue.SRScrapParts = StringUtils.parseString(wReader.get("SRScrapParts"));
				wIPTValue.SRLYParts = StringUtils.parseString(wReader.get("SRLYParts"));

				wIPTValue.MaterialID = StringUtils.parseInt(wReader.get("MaterialID"));
				wIPTValue.MaterialNo = StringUtils.parseString(wReader.get("MaterialNo"));
				wIPTValue.MaterialName = StringUtils.parseString(wReader.get("MaterialName"));

				if (wIPTValue.OrderID > 0) {
					wIPTValue.OMSOrder = LOCOAPSServiceImpl.getInstance()
							.OMS_QueryOrderByID(BaseDAO.SysAdmin, wIPTValue.OrderID).Info(OMSOrder.class);
				}

				int wTaskIDSql = StringUtils.parseInt(wReader.get("TaskID"));

				if (!wResult.Result.containsKey(wTaskIDSql))
					wResult.Result.put(wTaskIDSql, new ArrayList<IPTValue>());
				wResult.Result.get(wTaskIDSql).add(wIPTValue);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@SuppressWarnings("unused")
	private String ItemValueChangeToString(Map<Long, String> wItemValueList) {
		return StringUtils.Join("+|;|+", wItemValueList.keySet().stream()
				.map(p -> StringUtils.Format("{0}+|:|+{1}", p, wItemValueList.get(p))).collect(Collectors.toList()));
	}

	@SuppressWarnings("unused")
	private Map<Long, String> ItemValueChangeToList(String wItemValueListString) {
		Map<Long, String> wResult = new HashMap<Long, String>();
		String[] wStringList = wItemValueListString.split("\\+\\|;\\|\\+");
		String[] wStringItem = null;
		long wItemID = 0;
		for (String wItemString : wStringList) {
			wStringItem = wItemString.split("\\+\\|:\\|\\+");

			if (wStringItem.length != 2)
				continue;

			wItemID = StringUtils.parseLong(wStringItem[0]);

			wResult.put(wItemID, wStringItem[1]);
		}

		return wResult;
	}

	public ServiceResult<List<IPTStandard>> SelectIPTStandard(BMSEmployee wLoginUser, int wCompanyID, IPTMode wIPTMode,
			int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID, int wLineID, int wPartID,
			int wPartPointID, int wStationID, int wProductID, String wProductNo, int wNum, Calendar wTs, Calendar wTe,
			OutResult<Integer> wErrorCode) {
		ServiceResult<List<IPTStandard>> wResult = new ServiceResult<List<IPTStandard>>();
		try {
			wResult.Result = SelectIPTStandard(wLoginUser, -1, wCompanyID, wIPTMode, wCustomID, wBusinessUnitID,
					wBaseID, wFactoryID, wWorkShopID, wLineID, wPartID, wPartPointID, wStationID, wProductID,
					wProductNo, -1, wNum, wTs, wTe, wErrorCode);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<List<IPTStandard>> SelectIPTStandardCurrent(BMSEmployee wLoginUser, int wCompanyID,
			IPTMode wIPTMode, int wCustomID, int wBusinessUnitID, int wBaseID, int wFactoryID, int wWorkShopID,
			int wLineID, int wPartID, int wProductID, String wProductNo, int wNum, OutResult<Integer> wErrorCode) {
		ServiceResult<List<IPTStandard>> wResult = new ServiceResult<List<IPTStandard>>();
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 1, 1);
			wResult.Result = SelectIPTStandard(wLoginUser, -1, wCompanyID, wIPTMode, wCustomID, wBusinessUnitID,
					wBaseID, wFactoryID, wWorkShopID, wLineID, wPartID, -1, -1, wProductID, wProductNo, 1, wNum,
					wBaseTime, wBaseTime, wErrorCode);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<Map<Long, List<IPTItem>>> SelectItem(BMSEmployee wLoginUser, List<Long> wVIDList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Map<Long, List<IPTItem>>> wResult = new ServiceResult<Map<Long, List<IPTItem>>>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			wResult.Result = new HashMap<Long, List<IPTItem>>();
			if (wVIDList == null)
				return wResult;
			for (Long wStandardID : wVIDList) {
				wResult.Result.put(wStandardID, new ArrayList<IPTItem>());
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ipt_itemrecord WHERE  1=1    "
							+ "and ( :wVID is null or :wVID = '''' or VID IN( {1} ) )",
					wInstance.Result, wVIDList.size() > 0 ? StringUtils.Join(",", wVIDList) : "0");
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wVID", StringUtils.Join(",", wVIDList));

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);
			for (Map<String, Object> wReader : wQueryResult) {
				IPTItem wIPTItem = new IPTItem();
				wIPTItem.ID = StringUtils.parseLong(wReader.get("ID"));
				wIPTItem.GroupID = StringUtils.parseInt(wReader.get("GroupID"));
				wIPTItem.Text = StringUtils.parseString(wReader.get("Text"));
				wIPTItem.StandardType = StringUtils.parseInt(wReader.get("StandardType"));
				wIPTItem.StandardValue = StringUtils.parseString(wReader.get("StandardValue"));
				wIPTItem.DefaultValue = StringUtils.parseString(wReader.get("DefaultValue"));
				wIPTItem.StandardLeft = StringUtils.parseDouble(wReader.get("StandardLeft"));
				wIPTItem.StandardRight = StringUtils.parseDouble(wReader.get("StandardRight"));
				wIPTItem.Standard = StringUtils.parseString(wReader.get("Standard"));
				wIPTItem.Unit = StringUtils.parseString(wReader.get("Unit"));
				wIPTItem.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wIPTItem.Visiable = StringUtils.parseBoolean(wReader.get("Visiable"));
				wIPTItem.ValueSource = StringUtils
						.parseList(StringUtils.parseString(wReader.get("ValueSource")).split(";"));
				wIPTItem.StandardBaisc = StringUtils.parseString(wReader.get("StandardBasic"));
				wIPTItem.ItemType = StringUtils.parseInt(wReader.get("ItemType"));
				wIPTItem.VID = StringUtils.parseInt(wReader.get("VID"));
				wIPTItem.Active = StringUtils.parseInt(wReader.get("Active"));
				wIPTItem.Details = StringUtils.parseString(wReader.get("Details"));
				wIPTItem.Process = StringUtils.parseString(wReader.get("Process"));
				wIPTItem.DefaultStationID = StringUtils.parseInt(wReader.get("DefaultStationID"));
				wIPTItem.OtherValue = StringUtils.parseInt(wReader.get("OtherValue"));
				wIPTItem.DefaultManufactor = StringUtils.parseString(wReader.get("DefaultManufactor"));
				wIPTItem.DefaultModal = StringUtils.parseString(wReader.get("DefaultModal"));
				wIPTItem.DefaultNumber = StringUtils.parseString(wReader.get("DefaultNumber"));
				wIPTItem.IsShowStandard = StringUtils.parseInt(wReader.get("IsShowStandard")) == 1 ? true : false;
				wIPTItem.Legend = StringUtils.parseString(wReader.get("Legend"));

				wIPTItem.SerialNumber = StringUtils.parseString(wReader.get("SerialNumber"));
				wIPTItem.ProjectNo = StringUtils.parseString(wReader.get("ProjectNo"));
				wIPTItem.CheckPoint = StringUtils.parseString(wReader.get("CheckPoint"));
				wIPTItem.Code = StringUtils.parseString(wReader.get("Code"));
				wIPTItem.DefaultPartPointID = StringUtils.parseInt(wReader.get("DefaultPartPointID"));
				wIPTItem.DefaultPartPointName = QMSConstants.GetFPCStepName(wIPTItem.DefaultPartPointID);
				wIPTItem.IsWriteFill = StringUtils.parseInt(wReader.get("IsWriteFill"));
				wIPTItem.IsPictureFill = StringUtils.parseInt(wReader.get("IsPictureFill"));
				wIPTItem.IsVideoFill = StringUtils.parseInt(wReader.get("IsVideoFill"));
				wIPTItem.ManufactorOption = StringUtils
						.parseList(StringUtils.parseString(wReader.get("ManufactorOption")).split(";"));
				wIPTItem.ModalOption = StringUtils
						.parseList(StringUtils.parseString(wReader.get("ModalOption")).split(";"));
				wIPTItem.IsManufactorFill = StringUtils.parseInt(wReader.get("IsManufactorFill"));
				wIPTItem.IsModalFill = StringUtils.parseInt(wReader.get("IsModalFill"));
				wIPTItem.IsNumberFill = StringUtils.parseInt(wReader.get("IsNumberFill"));
				wIPTItem.IsPeriodChange = StringUtils.parseInt(wReader.get("IsPeriodChange"));
				wIPTItem.IsQuality = StringUtils.parseInt(wReader.get("IsQuality"));
				wIPTItem.DefaultStation = QMSConstants.GetFPCPartName(wIPTItem.DefaultStationID);
				wIPTItem.PartsCoding = StringUtils.parseString(wReader.get("PartsCoding"));
				wIPTItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wIPTItem.ConfigID = StringUtils.parseInt(wReader.get("ConfigID"));
				wIPTItem.AutoCalc = StringUtils.parseInt(wReader.get("AutoCalc"));
				wIPTItem.WorkContent = StringUtils.parseString(wReader.get("WorkContent"));
				wIPTItem.FileNames = StringUtils.parseString(wReader.get("FileNames"));
				wIPTItem.WorkTime = StringUtils.parseDouble(wReader.get("WorkTime"));

				wIPTItem.OrderType = StringUtils.parseInt(wReader.get("OrderType"));

				wIPTItem.DisassemblyComponents = StringUtils.parseString(wReader.get("DisassemblyComponents"));
				wIPTItem.RepairParts = StringUtils.parseString(wReader.get("RepairParts"));
				wIPTItem.AssemblyParts = StringUtils.parseString(wReader.get("AssemblyParts"));
				wIPTItem.Components = StringUtils.parseString(wReader.get("Components"));

				wIPTItem.DisassemblyComponentsID = StringUtils.parseInt(wReader.get("DisassemblyComponentsID"));
				wIPTItem.RepairPartsID = StringUtils.parseInt(wReader.get("RepairPartsID"));
				wIPTItem.AssemblyPartsID = StringUtils.parseInt(wReader.get("AssemblyPartsID"));
				wIPTItem.ComponentsID = StringUtils.parseInt(wReader.get("ComponentsID"));

				wIPTItem.SRDisassemblyComponentsID = StringUtils.parseInt(wReader.get("SRDisassemblyComponentsID"));
				wIPTItem.SRDisassemblyComponents = StringUtils.parseString(wReader.get("SRDisassemblyComponents"));
				wIPTItem.SRRepairPartsID = StringUtils.parseInt(wReader.get("SRRepairPartsID"));
				wIPTItem.SRRepairParts = StringUtils.parseString(wReader.get("SRRepairParts"));
				wIPTItem.SRAssemblyPartsID = StringUtils.parseInt(wReader.get("SRAssemblyPartsID"));
				wIPTItem.SRAssemblyParts = StringUtils.parseString(wReader.get("SRAssemblyParts"));

				List<String> wSOPIDList = StringUtils.splitList(StringUtils.parseString(wReader.get("IPTSOPList")),
						";");
				List<IPTSOP> wIPTSOPList = new ArrayList<IPTSOP>();
				if (wSOPIDList != null && wSOPIDList.size() > 0) {
					for (String wID : wSOPIDList) {
						int wSOPID = Integer.parseInt(wID);
						IPTSOP wIPTSOP = IPTSOPDAO.getInstance().SelectByID(wLoginUser, wSOPID, wErrorCode);
						if (wIPTSOP != null && wIPTSOP.ID > 0) {
							wIPTSOPList.add(wIPTSOP);
						}
					}
				}
				wIPTItem.IPTSOPList = wIPTSOPList;

				long wVIDSql = StringUtils.parseLong(wReader.get("VID"));

				if (wResult.Result.containsKey(wVIDSql))
					wResult.Result.get(wVIDSql).add(wIPTItem);
			}

		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	public ServiceResult<Long> InsertStandard(BMSEmployee wLoginUser, IPTStandard wIPTStandard,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Long> wResult = new ServiceResult<Long>(0L);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("INSERT INTO {0}.ipt_standard ( CompanyID, "
					+ "BusinessUnitID, BaseID, FactoryID, WorkShopID, LineID, "
					+ "PartID, PartPointID, StationID, IPTMode, ProductID,  "
					+ "ProductNo,  UserID,  IsCurrent, IsEnd, IsUsed, "
					+ "TModify, Remark,CustomID,Version,PersonNumber,Code,OrderID,PartNo,Status,WorkTime,IsPic,StepIDs) VALUES ( :wCompanyID, "
					+ ":wBusinessUnitID, :wBaseID, :wFactoryID, :wWorkShopID, :wLineID, "
					+ ":wPartID, :wPartPointID, :wStationID, :wIPTMode, :wProductID, "
					+ ":wProductNo,  :wUserID,  0, 0, 0, now(), :wRemark,:wCustomID,:wVersion,"
					+ ":PersonNumber,:Code,:OrderID,:PartNo,:Status,:WorkTime,:IsPic,:StepIDs);", wInstance.Result);
			Map<String, Object> wParamMap = new HashMap<String, Object>();
			wParamMap.put("wCompanyID", wIPTStandard.CompanyID);
			wParamMap.put("wIPTMode", wIPTStandard.IPTMode);
			wParamMap.put("wBusinessUnitID", wIPTStandard.BusinessUnitID);
			wParamMap.put("wBaseID", wIPTStandard.BaseID);
			wParamMap.put("wFactoryID", wIPTStandard.FactoryID);
			wParamMap.put("wWorkShopID", wIPTStandard.WorkShopID);
			wParamMap.put("wLineID", wIPTStandard.LineID);
			wParamMap.put("wPartID", wIPTStandard.PartID);
			wParamMap.put("wPartPointID", wIPTStandard.PartPointID);
			wParamMap.put("wStationID", wIPTStandard.StationID);
			wParamMap.put("wProductID", wIPTStandard.ProductID);
			wParamMap.put("wProductNo", wIPTStandard.ProductNo);
			wParamMap.put("wUserID", wIPTStandard.UserID);
			wParamMap.put("wRemark", wIPTStandard.Remark);
			wParamMap.put("wCustomID", wIPTStandard.CustomID);
			wParamMap.put("wVersion", wIPTStandard.Version);
			wParamMap.put("PersonNumber", wIPTStandard.PersonNumber);
			wParamMap.put("Code", wIPTStandard.Code);
			wParamMap.put("OrderID", wIPTStandard.OrderID);
			wParamMap.put("PartNo", wIPTStandard.PartNo);
			wParamMap.put("Status", wIPTStandard.Status);
			wParamMap.put("WorkTime", wIPTStandard.WorkTime);
			wParamMap.put("IsPic", wIPTStandard.IsPic);
			wParamMap.put("StepIDs", wIPTStandard.StepIDs);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);
			if (wIPTStandard.getID() <= 0) {
				wResult.Result = keyHolder.getKey().longValue();
				wIPTStandard.setID(wResult.Result);
			} else {
				wResult.Result = wIPTStandard.getID();
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 添加或修改
	 * 
	 * @param wIPTStandard
	 * @return
	 */
	public long Update(BMSEmployee wLoginUser, IPTStandard wIPTStandard, OutResult<Integer> wErrorCode) {
		long wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTStandard == null)
				return 0;

			String wSQL = "";
			if (wIPTStandard.getID() <= 0) {
				wSQL = StringUtils.Format("INSERT INTO {0}.ipt_standard(CompanyID,BusinessUnitID,BaseID,"
						+ "FactoryID,WorkShopID,LineID,PartID,PartPointID,StationID,"
						+ "IPTMode,ProductNo,UserID,IsCurrent,IsEnd,IsUsed,TModify,"
						+ "Remark,ProductID,CustomID,Version,PersonNumber,Code,OrderID,PartNo,Status,WorkTime,IsPic,StepIDs) "
						+ "VALUES(:CompanyID,:BusinessUnitID,"
						+ ":BaseID,:FactoryID,:WorkShopID,:LineID,:PartID,:PartPointID,"
						+ ":StationID,:IPTMode,:ProductNo,:UserID,:IsCurrent,:IsEnd,"
						+ ":IsUsed,:TModify,:Remark,:ProductID,:CustomID,:Version,"
						+ ":PersonNumber,:Code,:OrderID,:PartNo,:Status,:WorkTime,:IsPic,:StepIDs);", wInstance.Result);
//				wIPTStandard.Version = this.SelectNewVersion(wLoginUser, wErrorCode);
			} else {
				wSQL = StringUtils.Format("UPDATE {0}.ipt_standard SET CompanyID = :CompanyID,"
						+ "BusinessUnitID = :BusinessUnitID,BaseID = :BaseID,"
						+ "FactoryID = :FactoryID,WorkShopID = :WorkShopID,"
						+ "LineID = :LineID,PartID = :PartID,PartPointID = :PartPointID,"
						+ "StationID = :StationID,IPTMode = :IPTMode," + "ProductNo = :ProductNo,UserID = :UserID,"
						+ "IsCurrent = :IsCurrent,IsEnd = :IsEnd,IsUsed = :IsUsed,"
						+ "TModify = :TModify,Remark = :Remark,ProductID = :ProductID,"
						+ "CustomID = :CustomID,Version=:Version,PersonNumber=:PersonNumber,"
						+ "Code=:Code,OrderID=:OrderID,PartNo=:PartNo,Status=:Status,WorkTime=:WorkTime,IsPic=:IsPic,StepIDs=:StepIDs WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTStandard.ID);
			wParamMap.put("CompanyID", wIPTStandard.CompanyID);
			wParamMap.put("BusinessUnitID", wIPTStandard.BusinessUnitID);
			wParamMap.put("BaseID", wIPTStandard.BaseID);
			wParamMap.put("FactoryID", wIPTStandard.FactoryID);
			wParamMap.put("WorkShopID", wIPTStandard.WorkShopID);
			wParamMap.put("LineID", wIPTStandard.LineID);
			wParamMap.put("PartID", wIPTStandard.PartID);
			wParamMap.put("PartPointID", wIPTStandard.PartPointID);
			wParamMap.put("StationID", wIPTStandard.StationID);
			wParamMap.put("IPTMode", wIPTStandard.IPTMode);
			wParamMap.put("ProductNo", wIPTStandard.ProductNo);
			wParamMap.put("UserID", wIPTStandard.UserID);
			wParamMap.put("IsCurrent", wIPTStandard.IsCurrent);
			wParamMap.put("IsEnd", wIPTStandard.IsEnd);
			wParamMap.put("IsUsed", wIPTStandard.IsUsed);
			wParamMap.put("TModify", wIPTStandard.TModify);
			wParamMap.put("Remark", wIPTStandard.Remark);
			wParamMap.put("ProductID", wIPTStandard.ProductID);
			wParamMap.put("CustomID", wIPTStandard.CustomID);
			wParamMap.put("Version", wIPTStandard.Version);
			wParamMap.put("PersonNumber", wIPTStandard.PersonNumber);
			wParamMap.put("Code", wIPTStandard.Code);
			wParamMap.put("OrderID", wIPTStandard.OrderID);
			wParamMap.put("PartNo", wIPTStandard.PartNo);
			wParamMap.put("Status", wIPTStandard.Status);
			wParamMap.put("WorkTime", wIPTStandard.WorkTime);
			wParamMap.put("IsPic", wIPTStandard.IsPic);
			wParamMap.put("StepIDs", wIPTStandard.StepIDs);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTStandard.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTStandard.setID(wResult);
			} else {
				wResult = wIPTStandard.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取最新的标准版本号
	 * 
	 * @param wLoginUser 登录信息
	 * @param wErrorCode 错误码
	 * @return 标准版本号(GC-流水号)
	 */
	public String SelectNewVersion(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT CONCAT(''GC-'',LPAD(COUNT(*)+1, 5 , 0)) as Version FROM {0}.ipt_standard;",
					wInstance.Result);
			wSQL = this.DMLChange(wSQL);
			Map<String, Object> wParamMap = new HashMap<String, Object>();

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseString(wReader.get("Version"));
				break;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取最新的标准版本号(A.B.C)以此类推
	 */
	public String SelectNewVersion_V2(BMSEmployee wLoginUser, int wProductID, int wLineID, int CustomerID, int wPartID,
			int wStepID, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT count(*) Number FROM {0}.ipt_standard where "
					+ "ProductID=:ProductID and LineID=:LineID and CustomID=:CustomID "
					+ "and PartID=:PartID and PartPointID=:PartPointID;", wInstance.Result);

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ProductID", wProductID);
			wParamMap.put("LineID", wLineID);
			wParamMap.put("CustomID", CustomerID);
			wParamMap.put("PartID", wPartID);
			wParamMap.put("PartPointID", wStepID);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wNumber = StringUtils.parseInt(wReader.get("Number"));
				int wCodeNo = 65 + wNumber;
				wResult = String.valueOf((char) wCodeNo);
				return wResult;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 多主键查询
	 */
	public List<IPTStandard> SelectIPTStandardList(BMSEmployee wLoginUser, List<Integer> wIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTStandard> wResult = new ArrayList<IPTStandard>();
		try {
			if (wIDList == null || wIDList.size() <= 0) {
				return wResult;
			}

			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.ipt_standard " + "where ID in({1});", wInstance.Result,
					StringUtils.Join(",", wIDList));

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTStandard wIPTStandard = new IPTStandard();

				wIPTStandard.ID = StringUtils.parseLong(wReader.get("ID"));
				wIPTStandard.CompanyID = StringUtils.parseInt(wReader.get("CompanyID"));
				wIPTStandard.BusinessUnitID = StringUtils.parseInt(wReader.get("BusinessUnitID"));
				wIPTStandard.BaseID = StringUtils.parseInt(wReader.get("BaseID"));
				wIPTStandard.FactoryID = StringUtils.parseInt(wReader.get("FactoryID"));
				wIPTStandard.WorkShopID = StringUtils.parseInt(wReader.get("WorkShopID"));
				wIPTStandard.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wIPTStandard.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wIPTStandard.PartPointID = StringUtils.parseInt(wReader.get("PartPointID"));
				wIPTStandard.StationID = StringUtils.parseInt(wReader.get("StationID"));
				wIPTStandard.IPTMode = StringUtils.parseInt(wReader.get("IPTMode"));
				wIPTStandard.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wIPTStandard.ProductNo = StringUtils.parseString(wReader.get("ProductNo"));
				wIPTStandard.UserID = StringUtils.parseInt(wReader.get("UserID"));
				wIPTStandard.IsCurrent = StringUtils.parseInt(wReader.get("IsCurrent"));
				wIPTStandard.IsEnd = StringUtils.parseInt(wReader.get("IsEnd"));
				wIPTStandard.IsUsed = StringUtils.parseInt(wReader.get("IsUsed"));
				wIPTStandard.TModify = StringUtils.parseCalendar(wReader.get("TModify"));
				wIPTStandard.Remark = StringUtils.parseString(wReader.get("Remark"));
				wIPTStandard.Version = StringUtils.parseString(wReader.get("Version"));
				wIPTStandard.PersonNumber = StringUtils.parseInt(wReader.get("PersonNumber"));
				wIPTStandard.Code = StringUtils.parseString(wReader.get("Code"));
				wIPTStandard.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wIPTStandard.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wIPTStandard.Status = StringUtils.parseInt(wReader.get("Status"));
				wIPTStandard.CustomID = StringUtils.parseInt(wReader.get("CustomID"));
				wIPTStandard.WorkTime = StringUtils.parseDouble(wReader.get("WorkTime"));
				wIPTStandard.IsPic = StringUtils.parseInt(wReader.get("IsPic"));
				wIPTStandard.ItemList = new ArrayList<IPTItem>();

				wIPTStandard.PartName = QMSConstants.GetFPCPartName(wIPTStandard.PartID);
				wIPTStandard.PartPointName = QMSConstants.GetFPCStepName(wIPTStandard.PartPointID);
				wIPTStandard.LineName = QMSConstants.GetFMCLineName(wIPTStandard.LineID);
				wIPTStandard.CustomName = QMSConstants.GetCRMCustomerName(wIPTStandard.CustomID);

				wResult.add(wIPTStandard);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据车型、修程、工位、工序获取当前标准ID
	 */
	public int SelectCurrentID(BMSEmployee wLoginUser, int productID, int lineID, int wPartID, int wStepID,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT ID FROM {0}.ipt_standard "
					+ "where ProductID=:ProductID and LineID=:LineID and PartID=:PartID "
					+ "and PartPointID=:PartPointID and IsCurrent=1 ;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ProductID", productID);
			wParamMap.put("LineID", lineID);
			wParamMap.put("PartID", wPartID);
			wParamMap.put("PartPointID", wStepID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				wResult = StringUtils.parseInt(wReader.get("ID"));
			}

			if (wResult < 0) {
				wResult = SelectRelaStandardID(wLoginUser, -1, productID, lineID, wPartID, wStepID, -1, wErrorCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 查询工序单元明细
	 */
	public List<FMCLineUnit> FMC_QueryStepUnitList(BMSEmployee wLoginUser, int wLineID, int wProductID, int wPartID,
			String wStepName, OutResult<Integer> wErrorCode) {
		List<FMCLineUnit> wResult = new ArrayList<FMCLineUnit>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"SELECT t1.* FROM {0}.fmc_lineunit t1,{0}.fpc_partpoint t2 where t1.LevelID=3 "
							+ "and t1.UnitID=t2.ID and t1.LineID=:LineID and t1.ProductID=:ProductID "
							+ "and t2.Name like ''%{1}%'' " + "and ( :wPartID <= 0 or ParentUnitID = :wPartID);",
					wInstance.Result, wStepName);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("LineID", wLineID);
			wParamMap.put("ProductID", wProductID);
			wParamMap.put("wPartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				FMCLineUnit wFMCLineUnit = new FMCLineUnit();

				wFMCLineUnit.UnitID = StringUtils.parseInt(wReader.get("UnitID"));
				wFMCLineUnit.ParentUnitID = StringUtils.parseInt(wReader.get("ParentUnitID"));

				wResult.add(wFMCLineUnit);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据物料号更新物料其他属性
	 */
	public void MSS_UpdateMaterial(BMSEmployee wLoginUser, MSSMaterial wMSSMaterial, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils.Format("update {0}.mss_material set CYUnitID=:CYUnitID,"
					+ "MaterialGroup=:MaterialGroup,Groes=:Groes,Normt=:Normt,MaterialType=:MaterialType,"
					+ "NetWeight=:NetWeight,GrossWeight=:GrossWeight,Remark=:Remark where MaterialNo =:MaterialNo and ID>0;",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("CYUnitID", wMSSMaterial.CYUnitID);
			wParamMap.put("MaterialGroup", wMSSMaterial.MaterialGroup);
			wParamMap.put("Groes", wMSSMaterial.Groes);
			wParamMap.put("Normt", wMSSMaterial.Normt);
			wParamMap.put("MaterialType", wMSSMaterial.MaterialType);
			wParamMap.put("NetWeight", wMSSMaterial.NetWeight);
			wParamMap.put("GrossWeight", wMSSMaterial.GrossWeight);
			wParamMap.put("MaterialNo", wMSSMaterial.MaterialNo);
			wParamMap.put("Remark", wMSSMaterial.Remark);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}
