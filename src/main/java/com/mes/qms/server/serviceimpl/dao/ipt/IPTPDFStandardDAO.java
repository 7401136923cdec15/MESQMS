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
import com.mes.qms.server.service.po.ipt.IPTPDFStandard;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

/**
 * PDF导出配置
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-19 09:43:10
 * @LastEditTime 2020-4-17 15:51:49
 *
 */
public class IPTPDFStandardDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTPDFStandardDAO.class);

	private static IPTPDFStandardDAO Instance = null;

	/**
	 * 权限码
	 */
	private static int AccessCode = 502100;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTPDFStandard
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTPDFStandard wIPTPDFStandard, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTPDFStandard == null)
				return 0;

			String wSQL = "";
			if (wIPTPDFStandard.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.ipt_pdfstandard(PDFPartID,StandardID,TableType,OrderNo,TitleName,IsShowTitle) VALUES(:PDFPartID,:StandardID,:TableType,:OrderNo,:TitleName,:IsShowTitle);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.ipt_pdfstandard SET PDFPartID = :PDFPartID,StandardID = :StandardID,TableType = :TableType,OrderNo = :OrderNo,TitleName = :TitleName,IsShowTitle = :IsShowTitle WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTPDFStandard.ID);
			wParamMap.put("PDFPartID", wIPTPDFStandard.PDFPartID);
			wParamMap.put("StandardID", wIPTPDFStandard.StandardID);
			wParamMap.put("TableType", wIPTPDFStandard.TableType);
			wParamMap.put("OrderNo", wIPTPDFStandard.OrderNo);
			wParamMap.put("TitleName", wIPTPDFStandard.TitleName);
			wParamMap.put("IsShowTitle", wIPTPDFStandard.IsShowTitle ? 1 : 0);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTPDFStandard.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTPDFStandard.setID(wResult);
			} else {
				wResult = wIPTPDFStandard.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTPDFStandard> wList,
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
			for (IPTPDFStandard wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.ipt_pdfstandard WHERE ID IN({0}) ;",
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
	public IPTPDFStandard SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTPDFStandard wResult = new IPTPDFStandard();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTPDFStandard> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<IPTPDFStandard> SelectList(BMSEmployee wLoginUser, int wID, int wPDFPartID,
			OutResult<Integer> wErrorCode) {
		List<IPTPDFStandard> wResultList = new ArrayList<IPTPDFStandard>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format(
					"SELECT * FROM {0}.ipt_pdfstandard WHERE  1=1  and ( :wID <= 0 or :wID = ID ) and ( :wPDFPartID <= 0 or :wPDFPartID = PDFPartID );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wPDFPartID", wPDFPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTPDFStandard wItem = new IPTPDFStandard();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.PDFPartID = StringUtils.parseInt(wReader.get("PDFPartID"));
				wItem.StandardID = StringUtils.parseInt(wReader.get("StandardID"));
				wItem.TableType = StringUtils.parseInt(wReader.get("TableType"));
				wItem.OrderNo = StringUtils.parseInt(wReader.get("OrderNo"));
				wItem.TitleName = StringUtils.parseString(wReader.get("TitleName"));
				wItem.IsShowTitle = StringUtils.parseInt(wReader.get("IsShowTitle")) == 1 ? true : false;

				wResultList.add(wItem);
			}

			// 排序
			if (wResultList.size() > 0) {
				wResultList.sort(Comparator.comparing(IPTPDFStandard::getOrderNo));
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTPDFStandardDAO() {
		super();
	}

	public static IPTPDFStandardDAO getInstance() {
		if (Instance == null)
			Instance = new IPTPDFStandardDAO();
		return Instance;
	}
}
