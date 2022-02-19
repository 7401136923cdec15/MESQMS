package com.mes.qms.server.serviceimpl.dao.ipt;

import com.mes.qms.server.service.mesenum.APSTaskStatus;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTOrderReportPartPoint;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTOrderReportPartPointDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class IPTOrderReportPartPointDAO extends BaseDAO {
	private static Logger logger = LoggerFactory.getLogger(IPTOrderReportPartPointDAO.class);

	private static IPTOrderReportPartPointDAO Instance = null;

	public int Update(BMSEmployee wLoginUser, IPTOrderReportPartPoint wIPTOrderReportPartPoint,
			OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wIPTOrderReportPartPoint == null) {
				return 0;
			}
			String wSQL = "";
			if (wIPTOrderReportPartPoint.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.ipt_orderreportpartpoint(ReportPartID,StepID,Status) VALUES(:ReportPartID,:StepID,:Status);",
						new Object[] { wInstance.Result });
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.ipt_orderreportpartpoint SET ReportPartID = :ReportPartID,StepID = :StepID,Status = :Status WHERE ID = :ID;",
						new Object[] { wInstance.Result });
			}
			wSQL = DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("ID", Integer.valueOf(wIPTOrderReportPartPoint.ID));
			wParamMap.put("ReportPartID", Integer.valueOf(wIPTOrderReportPartPoint.ReportPartID));
			wParamMap.put("StepID", Integer.valueOf(wIPTOrderReportPartPoint.StepID));
			wParamMap.put("Status", Integer.valueOf(wIPTOrderReportPartPoint.Status));

			GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
			MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(wParamMap);

			this.nameJdbcTemplate.update(wSQL, (SqlParameterSource) mapSqlParameterSource,
					(KeyHolder) generatedKeyHolder);

			if (wIPTOrderReportPartPoint.getID() <= 0) {
				wResult = generatedKeyHolder.getKey().intValue();
				wIPTOrderReportPartPoint.setID(wResult);
			} else {
				wResult = wIPTOrderReportPartPoint.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTOrderReportPartPoint> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<>(Integer.valueOf(0));
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}
			List<String> wIDList = new ArrayList<>();
			for (IPTOrderReportPartPoint wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_orderreportpartpoint WHERE ID IN({0}) ;",
					String.join(",", wIDList), wInstance.Result);
			ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public IPTOrderReportPartPoint SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTOrderReportPartPoint wResult = new IPTOrderReportPartPoint();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			List<IPTOrderReportPartPoint> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<IPTOrderReportPartPoint> SelectList(BMSEmployee wLoginUser, int wID, int wReportPartID,
			OutResult<Integer> wErrorCode) {
		List<IPTOrderReportPartPoint> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.ipt_orderreportpartpoint WHERE  1=1  and ( :wID <= 0 or :wID = ID ) and ( :wReportPartID <= 0 or :wReportPartID = ReportPartID );",
					new Object[] {

							wInstance.Result });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wID", Integer.valueOf(wID));
			wParamMap.put("wReportPartID", Integer.valueOf(wReportPartID));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTOrderReportPartPoint wItem = new IPTOrderReportPartPoint();

				wItem.ID = StringUtils.parseInt(wReader.get("ID")).intValue();
				wItem.ReportPartID = StringUtils.parseInt(wReader.get("ReportPartID")).intValue();
				wItem.StepID = StringUtils.parseInt(wReader.get("StepID")).intValue();
				wItem.Status = StringUtils.parseInt(wReader.get("Status")).intValue();

				wItem.StepName = QMSConstants.GetFPCStepName(wItem.StepID);
				wItem.StatusText = APSTaskStatus.getEnumType(wItem.Status).getLable();

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public static IPTOrderReportPartPointDAO getInstance() {
		if (Instance == null)
			Instance = new IPTOrderReportPartPointDAO();
		return Instance;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\serviceimpl\dao\ipt\
 * IPTOrderReportPartPointDAO.class Java compiler version: 8 (52.0) JD-Core
 * Version: 1.1.2
 */