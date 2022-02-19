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
import com.mes.qms.server.service.po.pic.SFCPartInfo;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class SFCPartInfoDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCPartInfoDAO.class);

	private static SFCPartInfoDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCPartInfo
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCPartInfo wSFCPartInfo, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCPartInfo == null)
				return 0;

			String wSQL = "";
			if (wSFCPartInfo.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.sfc_partinfo(RankID,Remark,No,Status) "
						+ "VALUES(:RankID,:Remark,:No,:Status);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.sfc_partinfo SET RankID = :RankID,Remark = :Remark,"
						+ "No = :No,Status = :Status WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCPartInfo.ID);
			wParamMap.put("RankID", wSFCPartInfo.RankID);
			wParamMap.put("Remark", wSFCPartInfo.Remark);
			wParamMap.put("No", wSFCPartInfo.No);
			wParamMap.put("Status", wSFCPartInfo.Status);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCPartInfo.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCPartInfo.setID(wResult);
			} else {
				wResult = wSFCPartInfo.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCPartInfo> wList,
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
			for (SFCPartInfo wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_partinfo WHERE ID IN({0}) ;",
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
	public SFCPartInfo SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCPartInfo wResult = new SFCPartInfo();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCPartInfo> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<SFCPartInfo> SelectList(BMSEmployee wLoginUser, int wID, int wRankID, OutResult<Integer> wErrorCode) {
		List<SFCPartInfo> wResultList = new ArrayList<SFCPartInfo>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.sfc_partinfo WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wRankID <= 0 or :wRankID = RankID );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wRankID", wRankID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCPartInfo wItem = new SFCPartInfo();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.RankID = StringUtils.parseInt(wReader.get("RankID"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.No = StringUtils.parseInt(wReader.get("No"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.SFCUploadPicList = SFCUploadPicDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID, wErrorCode);

				// 赋值进度
				wItem.FinishSize = (int) wItem.SFCUploadPicList.stream().filter(p -> StringUtils.isNotEmpty(p.PicUrl))
						.count();
				wItem.TotalSize = wItem.SFCUploadPicList.size();
				wItem.Progress = StringUtils.Format("({0}/{1})", wItem.FinishSize, wItem.TotalSize);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private SFCPartInfoDAO() {
		super();
	}

	public static SFCPartInfoDAO getInstance() {
		if (Instance == null)
			Instance = new SFCPartInfoDAO();
		return Instance;
	}
}
