package com.mes.qms.server.serviceimpl.dao.ipt;

import java.util.ArrayList;
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

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.ipt.IPTPDFConfig;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

/**
 * PDF导出配置
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-19 09:43:10
 * @LastEditTime 2020-4-17 15:50:09
 *
 */
public class IPTPDFConfigDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTPDFConfigDAO.class);

	private static IPTPDFConfigDAO Instance = null;

	/**
	 * 权限码
	 */
	private static int AccessCode = 502100;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTPDFConfig
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTPDFConfig wIPTPDFConfig, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTPDFConfig == null)
				return 0;

			String wSQL = "";
			if (wIPTPDFConfig.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.ipt_pdfconfig(ConfigName,LineID,ProductID,CustomID,CreateID,CreateTime,Active) VALUES(:ConfigName,:LineID,:ProductID,:CustomID,:CreateID,:CreateTime,:Active);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.ipt_pdfconfig SET ConfigName = :ConfigName,LineID = :LineID,ProductID = :ProductID,CustomID = :CustomID,CreateID = :CreateID,CreateTime = :CreateTime,Active = :Active WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTPDFConfig.ID);
			wParamMap.put("ConfigName", wIPTPDFConfig.ConfigName);
			wParamMap.put("LineID", wIPTPDFConfig.LineID);
			wParamMap.put("ProductID", wIPTPDFConfig.ProductID);
			wParamMap.put("CustomID", wIPTPDFConfig.CustomID);
			wParamMap.put("CreateID", wIPTPDFConfig.CreateID);
			wParamMap.put("CreateTime", wIPTPDFConfig.CreateTime);
			wParamMap.put("Active", wIPTPDFConfig.Active);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTPDFConfig.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTPDFConfig.setID(wResult);
			} else {
				wResult = wIPTPDFConfig.getID();
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTPDFConfigDAO", "Update", ex.toString()));
		}
		return wResult;
	}

	/**
	 * 删除集合
	 * 
	 * @param wList
	 */
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTPDFConfig> wList,
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
			for (IPTPDFConfig wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.ipt_pdfconfig WHERE ID IN({0}) ;",
					String.join(",", wIDList), wInstance.Result);
			this.ExecuteSqlTransaction(wSql);
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTPDFConfigDAO", "DeleteList", ex.toString()));
		}
		return wResult;
	}

	/**
	 * 查单条
	 * 
	 * @return
	 */
	public IPTPDFConfig SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTPDFConfig wResult = new IPTPDFConfig();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTPDFConfig> wList = SelectList(wLoginUser, wID, -1, -1, -1, -1, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTPDFConfigDAO", "SelectByID", e.toString()));
		}
		return wResult;
	}

	/**
	 * 条件查询集合
	 * 
	 * @return
	 */
	public List<IPTPDFConfig> SelectList(BMSEmployee wLoginUser, int wID, int wLineID, int wProductID, int wCustomID,
			int wActive, OutResult<Integer> wErrorCode) {
		List<IPTPDFConfig> wResultList = new ArrayList<IPTPDFConfig>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.ipt_pdfconfig WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wLineID <= 0 or :wLineID = LineID ) "
					+ "and ( :wProductID <= 0 or :wProductID = ProductID ) "
					+ "and ( :wCustomID <= 0 or :wCustomID = CustomID ) "
					+ "and ( :wActive <= 0 or :wActive = Active );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wCustomID", wCustomID);
			wParamMap.put("wActive", wActive);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTPDFConfig wItem = new IPTPDFConfig();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.ConfigName = StringUtils.parseString(wReader.get("ConfigName"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.CustomID = StringUtils.parseInt(wReader.get("CustomID"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.Active = StringUtils.parseInt(wReader.get("Active"));
				wItem.IPTPDFPartList = IPTPDFPartDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID, wErrorCode);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTPDFConfigDAO", "SelectList", ex.toString()));
		}
		return wResultList;
	}

	/**
	 * 批量激活或禁用
	 */
	public ServiceResult<Integer> Active(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIDList == null || wIDList.size() <= 0)
				return wResult;
			List<IPTPDFConfig> wTempList = null;
			for (Integer wItem : wIDList) {
				IPTPDFConfig wIPTPDFConfig = SelectByID(wLoginUser, wItem, wErrorCode);
				if (wIPTPDFConfig == null || wIPTPDFConfig.ID <= 0)
					continue;
				// 激活时要处理相同车型、局段、修程的状态为关闭
				if (wActive == 1) {
					wTempList = this.SelectList(wLoginUser, -1, wIPTPDFConfig.LineID, wIPTPDFConfig.ProductID,
							wIPTPDFConfig.CustomID, 1, wErrorCode);
					if (wTempList != null && wTempList.size() > 1) {
						wTempList = wTempList.stream().filter(p -> p.ID != wItem).collect(Collectors.toList());
						for (IPTPDFConfig wTempItem : wTempList) {
							wTempItem.Active = 2;
							this.Update(wLoginUser, wTempItem, wErrorCode);
						}
					}
				}
				// 只有激活的才能禁用
				if (wActive == 2 && wIPTPDFConfig.Active != 1) {
					wErrorCode.set(MESException.Logic.getValue());
					return wResult;
				}
				wIPTPDFConfig.Active = wActive;
				long wID = Update(wLoginUser, wIPTPDFConfig, wErrorCode);
				if (wID <= 0)
					break;
			}
		} catch (Exception e) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(StringUtils.Format("{0} {1} ex：{2}", "IPTPDFConfigDAO", "Active", e.toString()));
		}
		return wResult;
	}

	private IPTPDFConfigDAO() {
		super();
	}

	public static IPTPDFConfigDAO getInstance() {
		if (Instance == null)
			Instance = new IPTPDFConfigDAO();
		return Instance;
	}
}
