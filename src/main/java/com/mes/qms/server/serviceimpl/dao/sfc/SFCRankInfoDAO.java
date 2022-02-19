package com.mes.qms.server.serviceimpl.dao.sfc;

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

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.pic.SFCRankInfo;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class SFCRankInfoDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCRankInfoDAO.class);

	private static SFCRankInfoDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCRankInfo
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCRankInfo wSFCRankInfo, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCRankInfo == null)
				return 0;

			String wSQL = "";
			if (wSFCRankInfo.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.sfc_rankinfo(CarID,No,Remark) " + "VALUES(:CarID,:No,:Remark);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.sfc_rankinfo SET CarID = :CarID,No = :No," + "Remark = :Remark WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCRankInfo.ID);
			wParamMap.put("CarID", wSFCRankInfo.CarID);
			wParamMap.put("No", wSFCRankInfo.No);
			wParamMap.put("Remark", wSFCRankInfo.Remark);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCRankInfo.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCRankInfo.setID(wResult);
			} else {
				wResult = wSFCRankInfo.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCRankInfo> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (SFCRankInfo wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_rankinfo WHERE ID IN({0}) ;",
					StringUtils.Join(",", wIDList), wInstance.Result);
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
	public SFCRankInfo SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCRankInfo wResult = new SFCRankInfo();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCRankInfo> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<SFCRankInfo> SelectList(BMSEmployee wLoginUser, int wID, int wCarID, OutResult<Integer> wErrorCode) {
		List<SFCRankInfo> wResultList = new ArrayList<SFCRankInfo>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.sfc_rankinfo WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wCarID <= 0 or :wCarID = CarID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wCarID", wCarID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCRankInfo wItem = new SFCRankInfo();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.CarID = StringUtils.parseInt(wReader.get("CarID"));
				wItem.No = StringUtils.parseInt(wReader.get("No"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.SFCPartInfoList = SFCPartInfoDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID, wErrorCode);

				// 赋值进度
				wItem.FinishSize = (int) wItem.SFCPartInfoList.stream()
						.filter(p -> p.SFCUploadPicList != null && p.SFCUploadPicList.size() > 0
								&& p.SFCUploadPicList.stream().allMatch(q -> StringUtils.isNotEmpty(q.PicUrl)))
						.count();
				wItem.TotalSize = wItem.SFCPartInfoList.size();
				wItem.Progress = StringUtils.Format("({0}/{1})", wItem.FinishSize, wItem.TotalSize);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private SFCRankInfoDAO() {
		super();
	}

	public static SFCRankInfoDAO getInstance() {
		if (Instance == null)
			Instance = new SFCRankInfoDAO();
		return Instance;
	}
}
