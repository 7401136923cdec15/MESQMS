package com.mes.qms.server.serviceimpl.dao.ipt;

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTOrderReport;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTOrderReportDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTOrderReportPartDAO;
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

public class IPTOrderReportDAO extends BaseDAO {
	private static Logger logger = LoggerFactory.getLogger(IPTOrderReportDAO.class);

	private static IPTOrderReportDAO Instance = null;

	public int Update(BMSEmployee wLoginUser, IPTOrderReport wIPTOrderReport, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wIPTOrderReport == null) {
				return 0;
			}
			String wSQL = "";
			if (wIPTOrderReport.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.ipt_orderreport(OrderID,OrderNo,PartNo,CustomerID,LineID,CreateID,CreateTime,EditID,EditTime) VALUES(:OrderID,:OrderNo,:PartNo,:CustomerID,:LineID,:CreateID,now(),:EditID,:EditTime);",
						new Object[] {

								wInstance.Result });
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.ipt_orderreport SET OrderID = :OrderID,OrderNo = :OrderNo,PartNo = :PartNo,CustomerID = :CustomerID,LineID = :LineID,CreateID = :CreateID,CreateTime = :CreateTime,EditID = :EditID,EditTime = now() WHERE ID = :ID;",
						new Object[] {

								wInstance.Result });
			}
			wSQL = DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("ID", Integer.valueOf(wIPTOrderReport.ID));
			wParamMap.put("OrderID", Integer.valueOf(wIPTOrderReport.OrderID));
			wParamMap.put("OrderNo", wIPTOrderReport.OrderNo);
			wParamMap.put("PartNo", wIPTOrderReport.PartNo);
			wParamMap.put("CustomerID", Integer.valueOf(wIPTOrderReport.CustomerID));
			wParamMap.put("LineID", Integer.valueOf(wIPTOrderReport.LineID));
			wParamMap.put("CreateID", Integer.valueOf(wIPTOrderReport.CreateID));
			wParamMap.put("CreateTime", wIPTOrderReport.CreateTime);
			wParamMap.put("EditID", Integer.valueOf(wIPTOrderReport.EditID));
			wParamMap.put("EditTime", wIPTOrderReport.EditTime);

			GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
			MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(wParamMap);

			this.nameJdbcTemplate.update(wSQL, (SqlParameterSource) mapSqlParameterSource,
					(KeyHolder) generatedKeyHolder);

			if (wIPTOrderReport.getID() <= 0) {
				wResult = generatedKeyHolder.getKey().intValue();
				wIPTOrderReport.setID(wResult);
			} else {
				wResult = wIPTOrderReport.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTOrderReport> wList,
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
			for (IPTOrderReport wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_orderreport WHERE ID IN({0}) ;",
					String.join(",", wIDList), wInstance.Result);
			ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public IPTOrderReport SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTOrderReport wResult = new IPTOrderReport();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			List<IPTOrderReport> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<IPTOrderReport> SelectList(BMSEmployee wLoginUser, int wID, int wOrderID,
			OutResult<Integer> wErrorCode) {
		List<IPTOrderReport> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.ipt_orderreport WHERE  1=1  and ( :wID <= 0 or :wID = ID ) and ( :wOrderID <= 0 or :wOrderID = OrderID );",
					new Object[] {

							wInstance.Result });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wID", Integer.valueOf(wID));
			wParamMap.put("wOrderID", Integer.valueOf(wOrderID));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTOrderReport wItem = new IPTOrderReport();

				wItem.ID = StringUtils.parseInt(wReader.get("ID")).intValue();
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID")).intValue();
				wItem.OrderNo = StringUtils.parseString(wReader.get("OrderNo"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.CustomerID = StringUtils.parseInt(wReader.get("CustomerID")).intValue();
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID")).intValue();
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID")).intValue();
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.EditID = StringUtils.parseInt(wReader.get("EditID")).intValue();
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));

				wItem.CustomerName = QMSConstants.GetCRMCustomerName(wItem.CustomerID);
				wItem.LineName = QMSConstants.GetFMCLineName(wItem.LineID);
				wItem.Creator = QMSConstants.GetBMSEmployeeName(wItem.CreateID);
				wItem.Editor = QMSConstants.GetBMSEmployeeName(wItem.EditID);

				wItem.IPTOrderReportPartList = IPTOrderReportPartDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID,
						-1, wErrorCode);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public static IPTOrderReportDAO getInstance() {
		if (Instance == null)
			Instance = new IPTOrderReportDAO();
		return Instance;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\serviceimpl\dao\ipt\
 * IPTOrderReportDAO.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */