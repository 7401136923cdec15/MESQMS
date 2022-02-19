package com.mes.qms.server.serviceimpl.dao.ipt;

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

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

/**
 * 预检解决方案
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-19 09:43:10
 * @LastEditTime 2020-4-17 15:56:54
 *
 */
public class IPTSOPDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTSOPDAO.class);

	private static IPTSOPDAO Instance = null;

	/**
	 * 权限码
	 */
	private static int AccessCode = 0;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTSOP
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTSOP wIPTSOP, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTSOP == null)
				return 0;

			String wSQL = "";
			if (wIPTSOP.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.ipt_sop(Name,Detail,Type,PathList) VALUES(:Name,:Detail,:Type,:PathList);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.ipt_sop SET Name = :Name,Detail = :Detail,Type = :Type,PathList = :PathList WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTSOP.ID);
			wParamMap.put("Name", wIPTSOP.Name);
			wParamMap.put("Detail", wIPTSOP.Detail);
			wParamMap.put("Type", wIPTSOP.Type);

			String wPathList = "";
			if (wIPTSOP.PathList != null && wIPTSOP.PathList.size() > 0) {
				wPathList = StringUtils.Join(";", wIPTSOP.PathList);
			}
			wParamMap.put("PathList", wPathList);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTSOP.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTSOP.setID(wResult);
			} else {
				wResult = wIPTSOP.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTSOP> wList,
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
			for (IPTSOP wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.ipt_sop WHERE ID IN({0}) ;", String.join(",", wIDList),
					wInstance.Result);
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
	public IPTSOP SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTSOP wResult = new IPTSOP();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTSOP> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<IPTSOP> SelectList(BMSEmployee wLoginUser, int wID, int wType, OutResult<Integer> wErrorCode) {
		List<IPTSOP> wResultList = new ArrayList<IPTSOP>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.ipt_sop WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wType <= 0 or :wType = Type );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wType", wType);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTSOP wItem = new IPTSOP();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.Name = StringUtils.parseString(wReader.get("Name"));
				wItem.Detail = StringUtils.parseString(wReader.get("Detail"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));

				List<String> wPathList = new ArrayList<String>();

				String wPaths = StringUtils.parseString(wReader.get("PathList"));
				if (StringUtils.isNotEmpty(wPaths)) {
					String[] wPathArray = wPaths.split(";");
					if (wPathArray != null && wPathArray.length > 0) {
						for (String wPath : wPathArray) {
							wPathList.add(wPath);
						}
					}
				}

				wItem.PathList = wPathList;

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTSOPDAO() {
		super();
	}

	public static IPTSOPDAO getInstance() {
		if (Instance == null)
			Instance = new IPTSOPDAO();
		return Instance;
	}
}
