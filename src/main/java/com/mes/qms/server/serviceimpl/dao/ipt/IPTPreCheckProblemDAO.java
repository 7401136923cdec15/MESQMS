package com.mes.qms.server.serviceimpl.dao.ipt;

import com.alibaba.fastjson.JSON;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.SFCTaskStepType;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSDepartment;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTProblemBomItem;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.rsm.RSMTurnOrderTask;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.sfc.SFCTaskStep;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTItemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTPreCheckProblemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTProblemAssessDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTSOPDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTValueDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTaskIPTDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class IPTPreCheckProblemDAO extends BaseDAO {
	private static Logger logger = LoggerFactory.getLogger(IPTPreCheckProblemDAO.class);

	private static IPTPreCheckProblemDAO Instance = null;

	public static IPTPreCheckProblemDAO getInstance() {
		if (Instance == null)
			Instance = new IPTPreCheckProblemDAO();
		return Instance;
	}

	public int Update(BMSEmployee wLoginUser, IPTPreCheckProblem wIPTPreCheckProblem, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wIPTPreCheckProblem == null) {
				return 0;
			}
			String wSQL = "";
			if (wIPTPreCheckProblem.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.ipt_precheckproblem(IPTPreCheckTaskID,IPTItemID,SolveID,ProductID,"
								+ "CarNumber,LineID,CustomID,DoStationID,DoPartPointID,PreCheckTime,CarftID,"
								+ "CarftTime,DepartmentList,EmployeeList,DoDepartmentID,DepartmentIssueID,"
								+ "DepartmentIssueTime,DoClassID,ClassIssueID,ClassIssueTime,DoPersonID,Status,"
								+ "Manager,OrderID,OrderNo,APSTaskStepID,IPTProblemBomItemList,IsDischarged,RealStationID,StepID) "
								+ "VALUES(:IPTPreCheckTaskID,:IPTItemID,:SolveID,:ProductID,:CarNumber,:LineID,"
								+ ":CustomID,:DoStationID,:DoPartPointID,:PreCheckTime,:CarftID,:CarftTime,"
								+ ":DepartmentList,:EmployeeList,:DoDepartmentID,:DepartmentIssueID,:DepartmentIssueTime,"
								+ ":DoClassID,:ClassIssueID,:ClassIssueTime,:DoPersonID,:Status,:Manager,:OrderID,:OrderNo,"
								+ ":APSTaskStepID,:IPTProblemBomItemList,:IsDischarged,:RealStationID,:StepID);",
						new Object[] { wInstance.Result });
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.ipt_precheckproblem SET IPTPreCheckTaskID = :IPTPreCheckTaskID,"
						+ "IPTItemID = :IPTItemID,SolveID = :SolveID,ProductID = :ProductID,"
						+ "CarNumber = :CarNumber,LineID = :LineID,CustomID = :CustomID,"
						+ "DoStationID = :DoStationID,DoPartPointID = :DoPartPointID,"
						+ "PreCheckTime = :PreCheckTime,CarftID = :CarftID,CarftTime = :CarftTime,"
						+ "DepartmentList = :DepartmentList,EmployeeList = :EmployeeList,"
						+ "DoDepartmentID = :DoDepartmentID,DepartmentIssueID = :DepartmentIssueID,"
						+ "DepartmentIssueTime = :DepartmentIssueTime,DoClassID = :DoClassID,ClassIssueID = :ClassIssueID,"
						+ "ClassIssueTime = :ClassIssueTime,DoPersonID = :DoPersonID,Status = :Status,"
						+ "Manager=:Manager,OrderID=:OrderID,OrderNo=:OrderNo,APSTaskStepID=:APSTaskStepID,"
						+ "IPTProblemBomItemList=:IPTProblemBomItemList,IsDischarged=:IsDischarged,"
						+ "RealStationID=:RealStationID,StepID=:StepID WHERE ID = :ID;",
						new Object[] { wInstance.Result });
			}
			wSQL = DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("ID", Integer.valueOf(wIPTPreCheckProblem.ID));
			wParamMap.put("IPTPreCheckTaskID", Integer.valueOf(wIPTPreCheckProblem.IPTPreCheckTaskID));
			wParamMap.put("IPTItemID", Integer.valueOf(wIPTPreCheckProblem.IPTItemID));
			wParamMap.put("SolveID", Integer.valueOf(wIPTPreCheckProblem.SolveID));
			wParamMap.put("ProductID", Integer.valueOf(wIPTPreCheckProblem.ProductID));
			wParamMap.put("CarNumber", wIPTPreCheckProblem.CarNumber);
			wParamMap.put("LineID", Integer.valueOf(wIPTPreCheckProblem.LineID));
			wParamMap.put("CustomID", Integer.valueOf(wIPTPreCheckProblem.CustomID));
			wParamMap.put("DoStationID", Integer.valueOf(wIPTPreCheckProblem.DoStationID));
			wParamMap.put("DoPartPointID", Integer.valueOf(wIPTPreCheckProblem.DoPartPointID));
			wParamMap.put("PreCheckTime", wIPTPreCheckProblem.PreCheckTime);
			wParamMap.put("CarftID", Integer.valueOf(wIPTPreCheckProblem.CarftID));
			wParamMap.put("CarftTime", wIPTPreCheckProblem.CarftTime);

			String wDepartmentList = "";
			if (wIPTPreCheckProblem.DepartmentList != null && wIPTPreCheckProblem.DepartmentList.size() > 0) {
				List<Integer> wIDList = new ArrayList<>();
				for (BMSDepartment wBMSDepartment : wIPTPreCheckProblem.DepartmentList) {
					wIDList.add(Integer.valueOf(wBMSDepartment.ID));
				}
				wDepartmentList = StringUtils.Join(",", wIDList);
			}
			wParamMap.put("DepartmentList", wDepartmentList);

			String wEmployeeList = "";
			if (wIPTPreCheckProblem.EmployeeList != null && wIPTPreCheckProblem.EmployeeList.size() > 0) {
				List<Integer> wIDList = new ArrayList<>();
				for (BMSEmployee wBMSEmployee : wIPTPreCheckProblem.EmployeeList) {
					wIDList.add(Integer.valueOf(wBMSEmployee.ID));
				}
				wEmployeeList = StringUtils.Join(",", wIDList);
			}
			wParamMap.put("EmployeeList", wEmployeeList);

			wParamMap.put("DoDepartmentID", Integer.valueOf(wIPTPreCheckProblem.DoDepartmentID));
			wParamMap.put("DepartmentIssueID", Integer.valueOf(wIPTPreCheckProblem.DepartmentIssueID));
			wParamMap.put("DepartmentIssueTime", wIPTPreCheckProblem.DepartmentIssueTime);
			wParamMap.put("DoClassID", Integer.valueOf(wIPTPreCheckProblem.DoClassID));
			wParamMap.put("ClassIssueID", Integer.valueOf(wIPTPreCheckProblem.ClassIssueID));
			wParamMap.put("ClassIssueTime", wIPTPreCheckProblem.ClassIssueTime);
			wParamMap.put("DoPersonID", Integer.valueOf(wIPTPreCheckProblem.DoPersonID));
			wParamMap.put("Status", Integer.valueOf(wIPTPreCheckProblem.Status));
			wParamMap.put("Manager", Integer.valueOf(wIPTPreCheckProblem.Manager.ID));
			wParamMap.put("OrderID", Integer.valueOf(wIPTPreCheckProblem.OrderID));
			wParamMap.put("OrderNo", wIPTPreCheckProblem.OrderNo);
			wParamMap.put("APSTaskStepID", Integer.valueOf(wIPTPreCheckProblem.APSTaskStepID));
			wParamMap.put("IPTProblemBomItemList",
					IPTProblemBomItem.ListToString(wIPTPreCheckProblem.IPTProblemBomItemList));
			wParamMap.put("IsDischarged", Integer.valueOf(wIPTPreCheckProblem.IsDischarged ? 1 : 0));
			wParamMap.put("RealStationID", Integer.valueOf(wIPTPreCheckProblem.RealStationID));
			wParamMap.put("StepID", wIPTPreCheckProblem.StepID);

			GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
			MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(wParamMap);

			this.nameJdbcTemplate.update(wSQL, (SqlParameterSource) mapSqlParameterSource,
					(KeyHolder) generatedKeyHolder);

			if (wIPTPreCheckProblem.getID() <= 0) {
				wResult = generatedKeyHolder.getKey().intValue();
				wIPTPreCheckProblem.setID(wResult);
			} else {
				wResult = wIPTPreCheckProblem.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTPreCheckProblem> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<>(Integer.valueOf(0));
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}
			List<String> wIDList = new ArrayList<>();
			for (IPTPreCheckProblem wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_precheckproblem WHERE ID IN({0}) ;",
					new Object[] { String.join(",", wIDList), wInstance.Result });
			ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public IPTPreCheckProblem SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTPreCheckProblem wResult = new IPTPreCheckProblem();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			List<IPTPreCheckProblem> wList = SelectList(wLoginUser, wID, -1, -1, -1, -1, -1, null, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);

			wResult.IPTItem = IPTItemDAO.getInstance().SelectByID(wLoginUser, wResult.IPTItemID, wErrorCode);
		} catch (Exception e) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<IPTPreCheckProblem> SelectList(BMSEmployee wLoginUser, int wID, int wIPTPreCheckTaskID, int wIPTItemID,
			int wProductID, int wLineID, int wCustomID, List<Integer> wStateIDList, OutResult<Integer> wErrorCode) {
		List<IPTPreCheckProblem> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			if (wStateIDList == null) {
				wStateIDList = new ArrayList<>();
			}
			String wSQL = MessageFormat.format(
					"SELECT t1.*,t2.Text as IPTItemName,t3.Description,t3.Details,t3.ImageList,t3.VideoList,"
							+ "t3.IPTSOPList,t3.FullDescribe FROM {0}.ipt_precheckproblem t1,{0}.ipt_itemrecord t2,{0}.ipt_solvelib t3 "
							+ "WHERE  t1.IPTItemID=t2.ID and t3.ID=t1.SolveID  and ( :wID <= 0 or :wID = t1.ID ) "
							+ "and ( :wIPTPreCheckTaskID <= 0 or :wIPTPreCheckTaskID = t1.IPTPreCheckTaskID ) "
							+ "and ( :wIPTItemID <= 0 or :wIPTItemID = t1.IPTItemID ) and ( :wProductID <= 0 or :wProductID = t1.ProductID ) "
							+ "and ( :wLineID <= 0 or :wLineID = t1.LineID ) and ( :wCustomID <= 0 or :wCustomID = t1.CustomID ) "
							+ "and ( :wStatus is null or :wStatus = '''' or t1.Status in ({1}));",
					new Object[] {

							wInstance.Result, (wStateIDList.size() > 0) ? StringUtils.Join(",", wStateIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wID", Integer.valueOf(wID));
			wParamMap.put("wIPTPreCheckTaskID", Integer.valueOf(wIPTPreCheckTaskID));
			wParamMap.put("wIPTItemID", Integer.valueOf(wIPTItemID));
			wParamMap.put("wProductID", Integer.valueOf(wProductID));
			wParamMap.put("wLineID", Integer.valueOf(wLineID));
			wParamMap.put("wCustomID", Integer.valueOf(wCustomID));
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValueList(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<IPTPreCheckProblem> SelectList(BMSEmployee wLoginUser, int wDoClassID, int wManager,
			Calendar wStartTime, Calendar wEndTime, List<Integer> wStateIDList, OutResult<Integer> wErrorCode) {
		List<IPTPreCheckProblem> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
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
			if (wStartTime == null)
				wStartTime = wBaseTime;
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			String wSQL = MessageFormat.format(
					"SELECT t1.*,t2.Text as IPTItemName,t3.Description,t3.Details,t3.ImageList,"
							+ "t3.VideoList,t3.IPTSOPList,t3.FullDescribe "
							+ "FROM {0}.ipt_precheckproblem t1,{0}.ipt_itemrecord t2,{0}.ipt_solvelib t3  "
							+ "WHERE  t1.IPTItemID=t2.ID and t3.ID=t1.SolveID  "
							+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  PreCheckTime ) "
							+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  PreCheckTime ) "
							+ "and ( :wDoClassID <= 0 or :wDoClassID = DoClassID ) and ( :wManager <= 0 or :wManager = Manager ) "
							+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1}));",
					new Object[] {

							wInstance.Result, (wStateIDList.size() > 0) ? StringUtils.Join(",", wStateIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wDoClassID", Integer.valueOf(wDoClassID));
			wParamMap.put("wManager", Integer.valueOf(wManager));
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValueList(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<IPTPreCheckProblem> SelectList(BMSEmployee wLoginUser, int wCarftID, int wDoClassID, int wClassIssueID,
			int wManager, Calendar wStartTime, Calendar wEndTime, List<Integer> wStateIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTPreCheckProblem> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
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
			if (wStartTime == null)
				wStartTime = wBaseTime;
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}
			String wSQL = MessageFormat.format(
					"SELECT t1.*,t2.Text as IPTItemName,t3.Description,t3.Details,t3.ImageList,"
							+ "t3.VideoList,t3.IPTSOPList,t3.FullDescribe "
							+ "FROM {0}.ipt_precheckproblem t1,{0}.ipt_itemrecord t2,{0}.ipt_solvelib t3 "
							+ "WHERE  t1.IPTItemID=t2.ID and t3.ID=t1.SolveID  "
							+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  PreCheckTime ) "
							+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  PreCheckTime ) "
							+ "and ( :wCarftID <= 0 or :wCarftID = CarftID ) and ( :wDoClassID <= 0 or :wDoClassID = DoClassID ) "
							+ "and ( :wClassIssueID <= 0 or :wClassIssueID = ClassIssueID ) and ( :wManager <= 0 or :wManager = Manager ) "
							+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1}));",
					new Object[] {

							wInstance.Result, (wStateIDList.size() > 0) ? StringUtils.Join(",", wStateIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wCarftID", Integer.valueOf(wCarftID));
			wParamMap.put("wDoClassID", Integer.valueOf(wDoClassID));
			wParamMap.put("wClassIssueID", Integer.valueOf(wClassIssueID));
			wParamMap.put("wManager", Integer.valueOf(wManager));
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValueList(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<IPTPreCheckProblem> SelectList(BMSEmployee wLoginUser, int wOrderID, int wProductID, int wLineID,
			int wCustomID, int wStationID, int wPartPointID, int wPersonID, OutResult<Integer> wErrorCode) {
		List<IPTPreCheckProblem> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format(
					"SELECT t1.*,t2.Text as IPTItemName,t3.Description,t3.Details,"
							+ "t3.ImageList,t3.VideoList,t3.IPTSOPList,t3.FullDescribe "
							+ "FROM {0}.ipt_precheckproblem t1,{0}.ipt_itemrecord t2,{0}.ipt_solvelib t3 "
							+ "WHERE  t1.IPTItemID=t2.ID and t3.ID=t1.SolveID  "
							+ "and ( :wProductID <= 0 or :wProductID = t1.ProductID ) "
							+ "and ( :wOrderID <= 0 or :wOrderID = t1.OrderID ) "
							+ "and ( :wLineID <= 0 or :wLineID = t1.LineID ) "
							+ "and ( :wCustomID <= 0 or :wCustomID = t1.CustomID ) "
							+ "and ( :wStationID <= 0 or :wStationID = t1.DoStationID ) "
							+ "and ( :wPartPointID <= 0 or :wPartPointID = t1.DoPartPointID ) "
							+ "and ( :wPersonID <= 0 or :wPersonID = t1.DoPersonID ) ",
					new Object[] {

							wInstance.Result });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wProductID", Integer.valueOf(wProductID));
			wParamMap.put("wOrderID", Integer.valueOf(wOrderID));
			wParamMap.put("wLineID", Integer.valueOf(wLineID));
			wParamMap.put("wCustomID", Integer.valueOf(wCustomID));
			wParamMap.put("wStationID", Integer.valueOf(wStationID));
			wParamMap.put("wPartPointID", Integer.valueOf(wPartPointID));
			wParamMap.put("wPersonID", Integer.valueOf(wPersonID));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValueList(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<IPTPreCheckProblem> SelectListByTask(BMSEmployee wLoginUser, int wTaskID, int wOrderID, int wLineID,
			int wProductID, int wStationID, int wStepID, List<Integer> wStateIDList, Calendar wStartTime,
			Calendar wEndTime, OutResult<Integer> wErrorCode) {
		List<IPTPreCheckProblem> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
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

			String wSQL = MessageFormat.format(
					"SELECT t1.*,t2.Text as IPTItemName,t3.Description,t3.Details,t3.ImageList,t3.VideoList,t3.IPTSOPList,t3.FullDescribe "
							+ "FROM {0}.ipt_precheckproblem t1,{0}.ipt_itemrecord t2,{0}.ipt_solvelib t3 "
							+ "WHERE  t1.IPTItemID=t2.ID and t3.ID=t1.SolveID  "
							+ "and ( :wLineID <= 0 or :wLineID = t1.LineID ) "
							+ "and ( :wProductID <= 0 or :wProductID = t1.ProductID ) "
							+ "and ( :wTaskID <= 0 or :wTaskID = t1.IPTPreCheckTaskID ) "
							+ "and ( :wOrderID <= 0 or :wOrderID = t1.OrderID ) "
							+ "and ( :wStationID <= 0 or :wStationID = t1.DoStationID ) "
							+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  PreCheckTime ) "
							+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  PreCheckTime ) "
							+ "and ( :wStepID <= 0 or :wStepID = t1.DoPartPointID ) "
							+ "and ( :wStatus is null or :wStatus = '''' or t1.Status in ({1}));",
					new Object[] {

							wInstance.Result, (wStateIDList.size() > 0) ? StringUtils.Join(",", wStateIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wLineID", Integer.valueOf(wLineID));
			wParamMap.put("wTaskID", Integer.valueOf(wTaskID));
			wParamMap.put("wOrderID", Integer.valueOf(wOrderID));
			wParamMap.put("wProductID", Integer.valueOf(wProductID));
			wParamMap.put("wStationID", Integer.valueOf(wStationID));
			wParamMap.put("wStepID", Integer.valueOf(wStepID));
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValueList(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<IPTPreCheckProblem> SelectListByTaskIPTIDList(BMSEmployee wLoginUser, List<Integer> wTaskIPTIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTPreCheckProblem> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			if (wTaskIPTIDList == null) {
				wTaskIPTIDList = new ArrayList<>();
			}
			String wSQL = MessageFormat.format(
					"SELECT t1.*,t2.Text as IPTItemName,t3.Description,t3.Details,t3.ImageList,"
							+ "t3.VideoList,t3.IPTSOPList,t3.FullDescribe "
							+ "FROM {0}.ipt_precheckproblem t1,{0}.ipt_itemrecord t2,{0}.ipt_solvelib t3 "
							+ "WHERE  t1.IPTItemID=t2.ID and t3.ID=t1.SolveID  "
							+ "and ( :wTaskIPTID is null or :wTaskIPTID = '''' or t1.IPTPreCheckTaskID in ({1}));",
					new Object[] { wInstance.Result,
							(wTaskIPTIDList.size() > 0) ? StringUtils.Join(",", wTaskIPTIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wTaskIPTID", StringUtils.Join(",", wTaskIPTIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValueList(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<IPTPreCheckProblem> SelectListByIPTItemID(BMSEmployee wLoginUser, int wIPTItemID, int wAPSTaskStepID,
			OutResult<Integer> wErrorCode) {
		List<IPTPreCheckProblem> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format(
					"SELECT t1.*,t2.Text as IPTItemName,t3.Description,t3.Details,t3.ImageList,t3.VideoList,t3.IPTSOPList,t3.FullDescribe "
							+ "FROM {0}.ipt_precheckproblem t1,{0}.ipt_itemrecord t2,{0}.ipt_solvelib t3 "
							+ "WHERE  t1.IPTItemID=t2.ID and t3.ID=t1.SolveID  "
							+ "and ( :wIPTItemID <= 0 or :wIPTItemID = t1.IPTItemID ) "
							+ "and ( :wAPSTaskStepID <= 0 or :wAPSTaskStepID = t1.APSTaskStepID ) ;",
					new Object[] { wInstance.Result });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wIPTItemID", Integer.valueOf(wIPTItemID));
			wParamMap.put("wAPSTaskStepID", Integer.valueOf(wAPSTaskStepID));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValueList(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<IPTPreCheckProblem> SelectListByConfirmID(BMSEmployee wLoginUser, int wConfirmID, Calendar wStartTime,
			Calendar wEndTime, List<Integer> wStateIDList, OutResult<Integer> wErrorCode) {
		List<IPTPreCheckProblem> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
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
			if (wStartTime == null)
				wStartTime = wBaseTime;
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}

			String wSQL = MessageFormat.format(
					"SELECT t1.*,t2.Text as IPTItemName,t3.Description,t3.Details,t3.ImageList,t3.VideoList,t3.IPTSOPList,t3.FullDescribe "
							+ "FROM {0}.ipt_precheckproblem t1,{0}.ipt_itemrecord t2,{0}.ipt_solvelib t3,{0}.ipt_problemconfirmer t4 "
							+ "WHERE  t1.IPTItemID=t2.ID and t3.ID=t1.SolveID  "
							+ "and ( :wStartTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wStartTime <=  t1.PreCheckTime ) "
							+ "and ( :wEndTime <=str_to_date(''2010-01-01'', ''%Y-%m-%d'')  or :wEndTime >=  t1.PreCheckTime ) "
							+ "and ( :wStatus is null or :wStatus = '''' or t1.Status in ({1})) "
							+ "and t4.ProblemID=t1.ID  and t4.ConfirmerID=:ConfirmID;",
					new Object[] { wInstance.Result,
							(wStateIDList.size() > 0) ? StringUtils.Join(",", wStateIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("ConfirmID", Integer.valueOf(wConfirmID));
			wParamMap.put("wStartTime", wStartTime);
			wParamMap.put("wEndTime", wEndTime);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValueList(wLoginUser, wErrorCode, wResultList, wQueryResult);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private void SetValueList(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode,
			List<IPTPreCheckProblem> wResultList, List<Map<String, Object>> wQueryResult) {
		try {
			Map<Integer, BMSDepartment> wBMSDepartmentMap = QMSConstants.GetBMSDepartmentList();

			Map<Integer, BMSEmployee> wBMSEmployeeMap = QMSConstants.GetBMSEmployeeList();

			List<String> wIPTSOPIDList = null;
			List<IPTSOP> wIPTSOPList = null;
			IPTSOP wSOP = null;
			int wSOPID = 0;
			for (Map<String, Object> wReader : wQueryResult) {
				IPTPreCheckProblem wItem = new IPTPreCheckProblem();

				wItem.ID = StringUtils.parseInt(wReader.get("ID")).intValue();
				wItem.IPTPreCheckTaskID = StringUtils.parseInt(wReader.get("IPTPreCheckTaskID")).intValue();
				wItem.IPTItemID = StringUtils.parseInt(wReader.get("IPTItemID")).intValue();
				wItem.SolveID = StringUtils.parseInt(wReader.get("SolveID")).intValue();
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID")).intValue();
				wItem.CarNumber = StringUtils.parseString(wReader.get("CarNumber"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID")).intValue();
				wItem.CustomID = StringUtils.parseInt(wReader.get("CustomID")).intValue();
				wItem.DoStationID = StringUtils.parseInt(wReader.get("DoStationID")).intValue();
				wItem.DoPartPointID = StringUtils.parseInt(wReader.get("DoPartPointID")).intValue();
				wItem.PreCheckTime = StringUtils.parseCalendar(wReader.get("PreCheckTime"));
				wItem.CarftID = StringUtils.parseInt(wReader.get("CarftID")).intValue();
				wItem.CarftTime = StringUtils.parseCalendar(wReader.get("CarftTime"));

				String wDepartmentIDs = StringUtils.parseString(wReader.get("DepartmentList"));
				wItem.DepartmentList = QMSUtils.getInstance().GetDepartmentList(wDepartmentIDs, wBMSDepartmentMap);

				String wEmployeeIDs = StringUtils.parseString(wReader.get("EmployeeList"));
				wItem.EmployeeList = QMSUtils.getInstance().GetEmployeeList(wEmployeeIDs, wBMSEmployeeMap);

				wItem.DoDepartmentID = StringUtils.parseInt(wReader.get("DoDepartmentID")).intValue();
				wItem.DepartmentIssueID = StringUtils.parseInt(wReader.get("DepartmentIssueID")).intValue();
				wItem.DepartmentIssueTime = StringUtils.parseCalendar(wReader.get("DepartmentIssueTime"));
				wItem.DoClassID = StringUtils.parseInt(wReader.get("DoClassID")).intValue();
				wItem.ClassIssueID = StringUtils.parseInt(wReader.get("ClassIssueID")).intValue();
				wItem.ClassIssueTime = StringUtils.parseCalendar(wReader.get("ClassIssueTime"));
				wItem.DoPersonID = StringUtils.parseInt(wReader.get("DoPersonID")).intValue();
				wItem.Status = StringUtils.parseInt(wReader.get("Status")).intValue();
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID")).intValue();
				wItem.OrderNo = StringUtils.parseString(wReader.get("OrderNo"));
				wItem.APSTaskStepID = StringUtils.parseInt(wReader.get("APSTaskStepID")).intValue();
				wItem.RealStationID = StringUtils.parseInt(wReader.get("RealStationID")).intValue();
				wItem.StepID = StringUtils.parseInt(wReader.get("StepID"));
				wItem.IPTProblemBomItemList = IPTProblemBomItem
						.StringToList(StringUtils.parseString(wReader.get("IPTProblemBomItemList")));

				int wManager = StringUtils.parseInt(wReader.get("Manager")).intValue();
				if (wBMSEmployeeMap.containsKey(Integer.valueOf(wManager))) {
					wItem.Manager = QMSConstants.GetBMSEmployee(wManager);
				}

				wItem.IPTItemName = StringUtils.parseString(wReader.get("IPTItemName"));

				wItem.Description = StringUtils.parseString(wReader.get("Description"));
				wItem.Details = StringUtils.parseString(wReader.get("Details"));
				wItem.ImageList = StringUtils.splitList(StringUtils.parseString(wReader.get("ImageList")), ";");
				wItem.VideoList = StringUtils.splitList(StringUtils.parseString(wReader.get("VideoList")), ";");

				wIPTSOPIDList = StringUtils.splitList(StringUtils.parseString(wReader.get("IPTSOPList")), ";");
				wIPTSOPList = new ArrayList<>();
				for (String wSOPIDStr : wIPTSOPIDList) {
					wSOPID = Integer.parseInt(wSOPIDStr);
					wSOP = IPTSOPDAO.getInstance().SelectByID(wLoginUser, wSOPID, wErrorCode);
					if (wSOP == null || wSOP.ID <= 0) {
						continue;
					}
					wIPTSOPList.add(wSOP);
				}
				wItem.IPTSOPList = wIPTSOPList;

				wItem.FullDescribe = StringUtils.parseString(wReader.get("FullDescribe"));

				wItem.CustomName = QMSConstants.GetCRMCustomerName(wItem.CustomID);

				wItem.StepName = QMSConstants.GetFPCStepName(wItem.StepID);

				wItem.IPTProblemAssessList = IPTProblemAssessDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID, -1,
						-1, null, null, wErrorCode);

				wItem.IPTProblemConfirmerList = IPTProblemConfirmerDAO.getInstance().SelectList(wLoginUser, -1,
						wItem.ID, -1, null, wErrorCode);

				wItem.IsDischarged = (StringUtils.parseInt(wReader.get("IsDischarged")).intValue() == 1);

				IPT_SetPreCheckText(wItem);
				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public List<IPTPreCheckProblem> SelectByIDList(BMSEmployee wLoginUser, List<Integer> wIDList,
			OutResult<Integer> wErrorCode) {
		List<IPTPreCheckProblem> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			if (wIDList == null) {
				wIDList = new ArrayList<>();
			}

			String wSQL = MessageFormat.format(
					"SELECT t1.*,t2.Text as IPTItemName,t3.Description,t3.Details,t3.ImageList,t3.VideoList,t3.IPTSOPList,t3.FullDescribe "
							+ "FROM {0}.ipt_precheckproblem t1,{0}.ipt_itemrecord t2,{0}.ipt_solvelib t3 "
							+ "WHERE  t1.IPTItemID=t2.ID and t3.ID=t1.SolveID  and ( :wIDs is null or :wIDs = '''' or t1.ID in ({1}));",
					new Object[] { wInstance.Result, (wIDList.size() > 0) ? StringUtils.Join(",", wIDList) : "0" });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wIDs", StringUtils.Join(",", wIDList));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			SetValueList(wLoginUser, wErrorCode, wResultList, wQueryResult);

			if (wResultList != null && wResultList.size() > 0) {
				for (IPTPreCheckProblem wIPTPreCheckProblem : wResultList) {
					List<SFCTaskIPT> wIPTList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1,
							wIPTPreCheckProblem.ID, -1, -1, -1, -1, null, SFCTaskStepType.Question.getValue(), null,
							null, wErrorCode);
					if (wIPTList == null || wIPTList.size() <= 0) {
						continue;
					}
					List<IPTValue> wAllList = new ArrayList<>();
					for (SFCTaskIPT wSFCTaskIPT : wIPTList) {
						List<IPTValue> wValueList = IPTValueDAO.getInstance().SelectList(wLoginUser, -1L, -1L,
								wSFCTaskIPT.ID, -1, -1, -1, wErrorCode);
						if (wValueList != null && wValueList.size() > 0) {
							IPTValue wValue = wValueList.stream().max(Comparator.comparing(IPTValue::getSubmitTime))
									.get();
							wAllList.add(wValue);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private void IPT_SetPreCheckText(IPTPreCheckProblem wItem) {
		try {
			if (wItem == null)
				return;
			wItem.PreCheckName = QMSConstants.GetBMSEmployeeName(wItem.PreCheckID);

			wItem.ProductNo = QMSConstants.GetFPCProductNo(wItem.ProductID);

			wItem.LineName = QMSConstants.GetFMCLineName(wItem.LineID);

			wItem.CustomName = QMSConstants.GetCRMCustomerName(wItem.CustomID);

			wItem.CraftName = QMSConstants.GetBMSEmployeeName(wItem.CarftID);

			wItem.DoStationName = QMSConstants.GetFPCPartName(wItem.DoStationID);

			wItem.DoDepartmentName = QMSConstants.GetBMSDepartmentName(wItem.DoDepartmentID);

			wItem.DepartmentIssueName = QMSConstants.GetBMSEmployeeName(wItem.DepartmentIssueID);

			wItem.DoClassName = QMSConstants.GetBMSDepartmentName(wItem.DoClassID);

			wItem.DoPartPointName = QMSConstants.GetFPCStepName(wItem.DoPartPointID);

			wItem.ClassIssueName = QMSConstants.GetBMSEmployeeName(wItem.ClassIssueID);

			wItem.DoPersonName = QMSConstants.GetBMSEmployeeName(wItem.DoPersonID);

			wItem.RealStationName = QMSConstants.GetFPCPartName(wItem.RealStationID);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public List<SFCTaskStep> SelectTaskStepListNoProblem(BMSEmployee wLoginUser, Calendar wTodaySTime,
			Calendar wTodayETime, int wOrderID, int wPartID, OutResult<Integer> wErrorCode) {
		List<SFCTaskStep> wResultList = new ArrayList<SFCTaskStep>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Default,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT t2.PartNo,t2.PartID,t2.OrderID,t3.Name PartName,t2.OperatorList, "
					+ "t4.Name StepName,t1.IsStartWork,t1.Type,t5.Name LineName,t1.Active, "
					+ "t1.ID,t1.TaskStepID,t2.StepID,t2.LineID,t6.ProductID,t2.TaskPartID,t1.ShiftID,t1.WorkHour, "
					+ "t1.OperatorID,t1.CreateTime,t1.ReadyTime,t1.MonitorID,t1.EditTime,t1.RealHour,t6.BureauSectionID "
					+ "FROM {1}.sfc_taskstep t1,{1}.aps_taskstep t2,{0}.fpc_part t3, "
					+ "{0}.fpc_partpoint t4,{0}.fmc_line t5,{1}.oms_order t6 "
					+ "where t2.OrderID=t6.ID and t2.LineID=t5.ID " + "and ( :OrderID <= 0 or :OrderID = t2.OrderID ) "
					+ "and ( :PartID <= 0 or :PartID = t2.PartID ) "
					+ "and t2.StepID=t4.ID and t2.PartID=t3.ID and t1.Type !=2 "
					+ "and t1.TaskStepID=t2.ID and t1.OperatorID=:UserID "
					+ "and ( (t2.Status=5 and t1.EditTime>:StartTime and t1.EditTime<:EndTime) "
					+ "or t2.Status in (2,4));", wInstance.Result, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("StartTime", wTodaySTime);
			wParamMap.put("EndTime", wTodayETime);
			wParamMap.put("UserID", wLoginUser.ID);
			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("PartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCTaskStep wItem = new SFCTaskStep();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID"));
				wItem.TaskPartID = StringUtils.parseInt(wReader.get("TaskPartID"));
				wItem.StepID = StringUtils.parseInt(wReader.get("StepID"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.LineName = StringUtils.parseString(wReader.get("LineName"));
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wItem.WorkHour = StringUtils.parseInt(wReader.get("WorkHour"));
				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.ReadyTime = StringUtils.parseCalendar(wReader.get("ReadyTime"));
				wItem.MonitorID = StringUtils.parseInt(wReader.get("MonitorID"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.RealHour = StringUtils.parseDouble(wReader.get("RealHour"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.PartName = StringUtils.parseString(wReader.get("PartName"));
				wItem.Operators = GetNamesByIDList(wLoginUser,
						StringUtils.parseIntList(StringUtils.parseString(wReader.get("OperatorList")).split(",|;")));
				wItem.PartPointName = StringUtils.parseString(wReader.get("StepName"));
				wItem.IsStartWork = StringUtils.parseInt(wReader.get("IsStartWork"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));
				wItem.CustomerID = StringUtils.parseInt(wReader.get("BureauSectionID"));
				wItem.Active = StringUtils.parseInt(wReader.get("Active"));

				wItem.CustomerName = QMSConstants.GetCRMCustomerName(wItem.CustomerID);

				wItem.Type = StringUtils.parseInt(wReader.get("Type"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public List<SFCTaskStep> SelectTaskStepListWithProblem(BMSEmployee wLoginUser, Calendar wTodaySTime,
			Calendar wTodayETime, int wOrderID, int wPartID, OutResult<Integer> wErrorCode) {
		List<SFCTaskStep> wResultList = new ArrayList<SFCTaskStep>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Default,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wInstance1 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance1.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			ServiceResult<String> wInstance2 = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance2.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"select t1.*,t3.Text StepName,t2.OrderID ,t4.PartNo,t2.RealStationID  PartID,"
							+ "t5.Name PartName,t2.DoPersonID OperatorList,t6.Name LineName,t2.StepID,t4.LineID,"
							+ "t4.ProductID "
							+ "from {1}.sfc_taskstep t1,{2}.ipt_precheckproblem t2,{2}.ipt_itemrecord t3 ,"
							+ "{1}.oms_order t4, {0}.fpc_part t5, {0}.fmc_line t6 "
							+ "where t4.LineID=t6.ID and t5.ID=t2.RealStationID "
							+ "and t2.OrderID=t4.ID and t2.IPTItemID=t3.ID "
							+ "and ( :OrderID <= 0 or :OrderID = t2.OrderID ) "
							+ "and ( :PartID <= 0 or :PartID = t2.RealStationID ) "
							+ "and t1.Type =2 and t1.TaskStepID=t2.ID and t1.OperatorID=:UserID  "
							+ "and ( (t2.Status in(8,9) and t1.EditTime>:StartTime "
							+ "and t1.EditTime<:EndTime) or t2.Status in (6,7,10,11));",
					wInstance.Result, wInstance1.Result, wInstance2.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("StartTime", wTodaySTime);
			wParamMap.put("EndTime", wTodayETime);
			wParamMap.put("UserID", wLoginUser.ID);
			wParamMap.put("OrderID", wOrderID);
			wParamMap.put("PartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCTaskStep wItem = new SFCTaskStep();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.TaskStepID = StringUtils.parseInt(wReader.get("TaskStepID"));
				wItem.StepID = StringUtils.parseInt(wReader.get("StepID"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.ShiftID = StringUtils.parseInt(wReader.get("ShiftID"));
				wItem.WorkHour = StringUtils.parseInt(wReader.get("WorkHour"));
				wItem.OperatorID = StringUtils.parseInt(wReader.get("OperatorID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.ReadyTime = StringUtils.parseCalendar(wReader.get("ReadyTime"));
				wItem.MonitorID = StringUtils.parseInt(wReader.get("MonitorID"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.RealHour = StringUtils.parseDouble(wReader.get("RealHour"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.Active = StringUtils.parseInt(wReader.get("Active"));
				wItem.PartName = StringUtils.parseString(wReader.get("PartName"));
				wItem.Operators = GetNamesByIDList(wLoginUser,
						StringUtils.parseIntList(StringUtils.parseString(wReader.get("OperatorList")).split(",|;")));
				wItem.PartPointName = StringUtils.parseString(wReader.get("StepName"));
				wItem.IsStartWork = StringUtils.parseInt(wReader.get("IsStartWork"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));
				wItem.LineName = StringUtils.parseString(wReader.get("LineName"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	@SuppressWarnings("unchecked")
	public List<FPCRoutePart> SelectRoutePartListByOrderIDs(BMSEmployee wLoginUser, String wOrderIDs,
			OutResult<Integer> wErrorCode) {
		List<FPCRoutePart> wResult = new ArrayList<FPCRoutePart>();
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
					"select t1.RouteID,t1.PartID,t1.PrevPartID,t1.NextPartIDMap,t1.ChangeControl "
							+ "from {2}.fpc_routepart t1, {0}.oms_order t2 "
							+ "where t1.RouteID = t2.RouteID and t2.ID in({1});",
					wInstance.Result, wOrderIDs, wInstance1.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				FPCRoutePart wItem = new FPCRoutePart();

				wItem.RouteID = StringUtils.parseInt(wReader.get("RouteID"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.PrevPartID = StringUtils.parseInt(wReader.get("PrevPartID"));
				wItem.ChangeControl = StringUtils.parseInt(wReader.get("ChangeControl"));
				wItem.NextPartIDMap = (Map<String, String>) JSON.parse(wReader.get("NextPartIDMap").toString());

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<RSMTurnOrderTask> SelectTurnOrderTaskListByOrderIDs(BMSEmployee wLoginUser, String wOrderIDs,
			OutResult<Integer> wErrorCode) {
		List<RSMTurnOrderTask> wResult = new ArrayList<RSMTurnOrderTask>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.Basic,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format(
					"select OrderID,ApplyStationID,Status " + "from {0}.sfc_turnordertask where OrderID in({1});",
					wInstance.Result, wOrderIDs);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				RSMTurnOrderTask wItem = new RSMTurnOrderTask();

				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.ApplyStationID = StringUtils.parseInt(wReader.get("ApplyStationID"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<OMSOrder> SelectOrderListByOrderIDs(BMSEmployee wLoginUser, String wOrderIDs,
			OutResult<Integer> wErrorCode) {
		List<OMSOrder> wResult = new ArrayList<OMSOrder>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("select ID,RouteID from {0}.oms_order where ID in({1});", wInstance.Result,
					wOrderIDs);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				OMSOrder wItem = new OMSOrder();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.RouteID = StringUtils.parseInt(wReader.get("RouteID"));

				wResult.add(wItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据人员ID集合获取人员名称
	 * 
	 * @param wLoginUser
	 * @param wIDList
	 * @return
	 */
	private String GetNamesByIDList(BMSEmployee wLoginUser, List<Integer> wIDList) {
		String wResult = "";
		try {
			if (wIDList == null || wIDList.size() <= 0) {
				return wResult;
			}

			List<String> wNames = new ArrayList<String>();
			wIDList.stream().forEach(p -> {
				if (p > 0) {
					wNames.add(QMSConstants.GetBMSEmployeeName(p));
				}
			});
			wNames.removeIf(p -> StringUtils.isEmpty(p));
			wResult = StringUtils.Join(",", wNames);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\serviceimpl\dao\ipt\
 * IPTPreCheckProblemDAO.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */