package com.mes.qms.server.serviceimpl.dao.ipt;

import java.util.ArrayList;
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

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.ipt.IPTPDFPart;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

/**
 * PDF导出配置
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-19 09:43:10
 * @LastEditTime 2020-4-17 15:51:04
 *
 */
public class IPTPDFPartDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTPDFPartDAO.class);

	private static IPTPDFPartDAO Instance = null;

	/**
	 * 权限码
	 */
	private static int AccessCode = 502100;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTPDFPart
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTPDFPart wIPTPDFPart, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTPDFPart == null)
				return 0;

			String wSQL = "";
			if (wIPTPDFPart.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.ipt_pdfpart(PDFConfigID,PartTitle,OrderNo) VALUES(:PDFConfigID,:PartTitle,:OrderNo);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.ipt_pdfpart SET PDFConfigID = :PDFConfigID,PartTitle = :PartTitle,OrderNo = :OrderNo WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTPDFPart.ID);
			wParamMap.put("PDFConfigID", wIPTPDFPart.PDFConfigID);
			wParamMap.put("PartTitle", wIPTPDFPart.PartTitle);
			wParamMap.put("OrderNo", wIPTPDFPart.OrderNo);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTPDFPart.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTPDFPart.setID(wResult);
			} else {
				wResult = wIPTPDFPart.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTPDFPart> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (IPTPDFPart wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.ipt_pdfpart WHERE ID IN({0}) ;",
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
	public IPTPDFPart SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTPDFPart wResult = new IPTPDFPart();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTPDFPart> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<IPTPDFPart> SelectList(BMSEmployee wLoginUser, int wID, int wPDFConfigID,
			OutResult<Integer> wErrorCode) {
		List<IPTPDFPart> wResultList = new ArrayList<IPTPDFPart>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ipt_pdfpart WHERE  1=1  and ( :wID <= 0 or :wID = ID ) and ( :wPDFConfigID <= 0 or :wPDFConfigID = PDFConfigID );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wPDFConfigID", wPDFConfigID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTPDFPart wItem = new IPTPDFPart();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.PDFConfigID = StringUtils.parseInt(wReader.get("PDFConfigID"));
				wItem.PartTitle = StringUtils.parseString(wReader.get("PartTitle"));
				wItem.OrderNo = StringUtils.parseInt(wReader.get("OrderNo"));
				wItem.IPTPDFStandardList = IPTPDFStandardDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID,
						wErrorCode);

				wResultList.add(wItem);
			}

			// 排序
			if (wResultList.size() > 0) {
				wResultList.sort(Comparator.comparing(IPTPDFPart::getOrderNo));
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTPDFPartDAO() {
		super();
	}

	public static IPTPDFPartDAO getInstance() {
		if (Instance == null)
			Instance = new IPTPDFPartDAO();
		return Instance;
	}
}
