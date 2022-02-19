package com.mes.qms.server.serviceimpl.dao.ipt;

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTOrderReportPart;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTOrderReportPartDAO;
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

public class IPTOrderReportPartDAO extends BaseDAO {
	private static Logger logger = LoggerFactory.getLogger(IPTOrderReportPartDAO.class);

	private static IPTOrderReportPartDAO Instance = null;

	public int Update(BMSEmployee wLoginUser, IPTOrderReportPart wIPTOrderReportPart, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			if (wIPTOrderReportPart == null) {
				return 0;
			}
			String wSQL = "";
			if (wIPTOrderReportPart.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.ipt_orderreportpart(ReportID,PartID,Type) VALUES(:ReportID,:PartID,:Type);",
						new Object[] { wInstance.Result });
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.ipt_orderreportpart SET ReportID = :ReportID,PartID = :PartID,Type = :Type WHERE ID = :ID;",
						new Object[] { wInstance.Result });
			}
			wSQL = DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("ID", Integer.valueOf(wIPTOrderReportPart.ID));
			wParamMap.put("ReportID", Integer.valueOf(wIPTOrderReportPart.ReportID));
			wParamMap.put("PartID", Integer.valueOf(wIPTOrderReportPart.PartID));
			wParamMap.put("Type", Integer.valueOf(wIPTOrderReportPart.Type));

			GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
			MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(wParamMap);

			this.nameJdbcTemplate.update(wSQL, (SqlParameterSource) mapSqlParameterSource,
					(KeyHolder) generatedKeyHolder);

			if (wIPTOrderReportPart.getID() <= 0) {
				wResult = generatedKeyHolder.getKey().intValue();
				wIPTOrderReportPart.setID(wResult);
			} else {
				wResult = wIPTOrderReportPart.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTOrderReportPart> wList,
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
			for (IPTOrderReportPart wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.ipt_orderreportpart WHERE ID IN({0}) ;",
					String.join(",", wIDList), wInstance.Result);
			ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResult;
	}

	public IPTOrderReportPart SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTOrderReportPart wResult = new IPTOrderReportPart();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResult;
			}

			List<IPTOrderReportPart> wList = SelectList(wLoginUser, wID, -1, -1, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(e.toString());
		}
		return wResult;
	}

	public List<IPTOrderReportPart> SelectList(BMSEmployee wLoginUser, int wID, int wReportID, int wType,
			OutResult<Integer> wErrorCode) {
		List<IPTOrderReportPart> wResultList = new ArrayList<>();
		try {
			ServiceResult<String> wInstance = GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(Integer.valueOf(wInstance.ErrorCode));
			if (((Integer) wErrorCode.Result).intValue() != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.ipt_orderreportpart WHERE  1=1  and ( :wID <= 0 or :wID = ID ) and ( :wReportID <= 0 or :wReportID = ReportID ) and ( :wType <= 0 or :wType = Type );",
					new Object[] {

							wInstance.Result });
			Map<String, Object> wParamMap = new HashMap<>();

			wParamMap.put("wID", Integer.valueOf(wID));
			wParamMap.put("wReportID", Integer.valueOf(wReportID));
			wParamMap.put("wType", Integer.valueOf(wType));

			wSQL = DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = this.nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTOrderReportPart wItem = new IPTOrderReportPart();

				wItem.ID = StringUtils.parseInt(wReader.get("ID")).intValue();
				wItem.ReportID = StringUtils.parseInt(wReader.get("ReportID")).intValue();
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID")).intValue();
				wItem.Type = StringUtils.parseInt(wReader.get("Type")).intValue();

				wItem.PartName = QMSConstants.GetFPCPartName(wItem.PartID);

				wItem.IPTOrderReportPartPointList = IPTOrderReportPartPointDAO.getInstance().SelectList(wLoginUser, -1,
						wItem.ID, wErrorCode);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(Integer.valueOf(MESException.DBSQL.getValue()));
			logger.error(ex.toString());
		}
		return wResultList;
	}

	public static IPTOrderReportPartDAO getInstance() {
		if (Instance == null)
			Instance = new IPTOrderReportPartDAO();
		return Instance;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\serviceimpl\dao\ipt\
 * IPTOrderReportPartDAO.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */