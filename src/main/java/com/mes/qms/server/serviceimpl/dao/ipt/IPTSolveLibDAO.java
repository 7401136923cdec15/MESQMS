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
import com.mes.qms.server.service.po.ipt.IPTSolveLib;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

/**
 * 预检知识库
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-19 09:43:10
 * @LastEditTime 2020-4-17 15:54:11
 *
 */
public class IPTSolveLibDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(IPTSolveLibDAO.class);

	private static IPTSolveLibDAO Instance = null;
	
	/**
	 * 预检知识库
	 */
	private static int AccessCode = 0;

	/**
	 * 添加或修改
	 * 
	 * @param wIPTSolveLib
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, IPTSolveLib wIPTSolveLib, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), AccessCode);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTSolveLib == null)
				return 0;

			String wSQL = "";
			if (wIPTSolveLib.getID() <= 0) {
				wSQL = StringUtils.Format(
						"INSERT INTO {0}.ipt_solvelib(IPTItemID,Description,Details,ImageList,VideoList,ProductID,LineID,CustomID,IPTSOPList,FullDescribe,CreateTime,CreateID,EditTime,EditID) VALUES(:IPTItemID,:Description,:Details,:ImageList,:VideoList,:ProductID,:LineID,:CustomID,:IPTSOPList,:FullDescribe,:CreateTime,:CreateID,:EditTime,:EditID);",
						wInstance.Result);
			} else {
				wSQL = StringUtils.Format(
						"UPDATE {0}.ipt_solvelib SET IPTItemID = :IPTItemID,Description = :Description,Details = :Details,ImageList = :ImageList,VideoList = :VideoList,ProductID = :ProductID,LineID = :LineID,CustomID = :CustomID,IPTSOPList = :IPTSOPList,FullDescribe = :FullDescribe,CreateTime = :CreateTime,CreateID = :CreateID,EditTime = :EditTime,EditID = :EditID WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wIPTSolveLib.ID);
			wParamMap.put("IPTItemID", wIPTSolveLib.IPTItemID);
			wParamMap.put("Description", wIPTSolveLib.Description);
			wParamMap.put("Details", wIPTSolveLib.Details);
			wParamMap.put("ImageList", StringUtils.Join(";", wIPTSolveLib.ImageList));
			wParamMap.put("VideoList", StringUtils.Join(";", wIPTSolveLib.VideoList));
			wParamMap.put("ProductID", wIPTSolveLib.ProductID);
			wParamMap.put("LineID", wIPTSolveLib.LineID);
			wParamMap.put("CustomID", wIPTSolveLib.CustomID);

			String wSOPIDList = "";
			if (wIPTSolveLib.IPTSOPList != null && wIPTSolveLib.IPTSOPList.size() > 0) {
				List<Integer> wIDList = new ArrayList<Integer>();
				for (IPTSOP wIPTSOP : wIPTSolveLib.IPTSOPList) {
					wIDList.add(wIPTSOP.ID);
				}
				wSOPIDList = StringUtils.Join(";", wIDList);
			}
			wParamMap.put("IPTSOPList", wSOPIDList);

			wParamMap.put("FullDescribe", wIPTSolveLib.FullDescribe);
			wParamMap.put("CreateTime", wIPTSolveLib.CreateTime);
			wParamMap.put("CreateID", wIPTSolveLib.CreateID);
			wParamMap.put("EditTime", wIPTSolveLib.EditTime);
			wParamMap.put("EditID", wIPTSolveLib.EditID);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wIPTSolveLib.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wIPTSolveLib.setID(wResult);
			} else {
				wResult = wIPTSolveLib.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<IPTSolveLib> wList,
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
			for (IPTSolveLib wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = StringUtils.Format("delete from {1}.ipt_solvelib WHERE ID IN({0}) ;",
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
	public IPTSolveLib SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		IPTSolveLib wResult = new IPTSolveLib();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<IPTSolveLib> wList = SelectList(wLoginUser, wID, -1, -1, -1, -1, wErrorCode);
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
	public List<IPTSolveLib> SelectList(BMSEmployee wLoginUser, int wID, int wIPTItemID, int wProductID, int wLineID,
			int wCustomID, OutResult<Integer> wErrorCode) {
		List<IPTSolveLib> wResultList = new ArrayList<IPTSolveLib>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.EXC,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = StringUtils.Format("SELECT * FROM {0}.ipt_solvelib WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wIPTItemID <= 0 or :wIPTItemID = IPTItemID ) "
					+ "and ( :wProductID <= 0 or :wProductID = ProductID ) "
					+ "and ( :wLineID <= 0 or :wLineID = LineID ) "
					+ "and ( :wCustomID <= 0 or :wCustomID = CustomID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wIPTItemID", wIPTItemID);
			wParamMap.put("wProductID", wProductID);
			wParamMap.put("wLineID", wLineID);
			wParamMap.put("wCustomID", wCustomID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				IPTSolveLib wItem = new IPTSolveLib();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.IPTItemID = StringUtils.parseInt(wReader.get("IPTItemID"));
				wItem.Description = StringUtils.parseString(wReader.get("Description"));
				wItem.Details = StringUtils.parseString(wReader.get("Details"));

				List<String> wImageList = new ArrayList<String>();
				String wImages = StringUtils.parseString(wReader.get("ImageList"));
				if (StringUtils.isNotEmpty(wImages)) {
					String[] wImageArray = wImages.split(";");
					if (wImageArray != null && wImageArray.length > 0) {
						for (String wImage : wImageArray) {
							wImageList.add(wImage);
						}
					}
				}
				wItem.ImageList = wImageList;

				wItem.VideoList = StringUtils.splitList(StringUtils.parseString(wReader.get("VideoList")), ";");
				wItem.ProductID = StringUtils.parseInt(wReader.get("ProductID"));
				wItem.LineID = StringUtils.parseInt(wReader.get("LineID"));
				wItem.CustomID = StringUtils.parseInt(wReader.get("CustomID"));

				List<String> wSOPIDList = StringUtils.splitList(StringUtils.parseString(wReader.get("IPTSOPList")), ";");
				List<IPTSOP> wIPTSOPList = new ArrayList<IPTSOP>();
				if (wSOPIDList != null && wSOPIDList.size() > 0) {
					for (String wSOPID : wSOPIDList) {
						int wSID = Integer.parseInt(wSOPID);
						IPTSOP wIPTSOP = IPTSOPDAO.getInstance().SelectByID(wLoginUser, wSID, wErrorCode);
						if (wIPTSOP != null && wIPTSOP.ID > 0) {
							wIPTSOPList.add(wIPTSOP);
						}
					}
				}
				wItem.IPTSOPList = wIPTSOPList;

				wItem.FullDescribe = StringUtils.parseString(wReader.get("FullDescribe"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.EditTime = StringUtils.parseCalendar(wReader.get("EditTime"));
				wItem.EditID = StringUtils.parseInt(wReader.get("EditID"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private IPTSolveLibDAO() {
		super();
	}

	public static IPTSolveLibDAO getInstance() {
		if (Instance == null)
			Instance = new IPTSolveLibDAO();
		return Instance;
	}
}
