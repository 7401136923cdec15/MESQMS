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
import com.mes.qms.server.service.po.pic.SFCUploadPic;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class SFCUploadPicDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCUploadPicDAO.class);

	private static SFCUploadPicDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCUploadPic
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCUploadPic wSFCUploadPic, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCUploadPic == null)
				return 0;

			String wSQL = "";
			if (wSFCUploadPic.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.sfc_uploadpic(PartID,PicUrl,No,Remark,`Describe`) "
						+ "VALUES(:PartID,:PicUrl,:No,:Remark,:Describe);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format("UPDATE {0}.sfc_uploadpic SET PartID = :PartID,PicUrl = :PicUrl,"
						+ "No = :No,Remark = :Remark,`Describe`=:Describe WHERE ID = :ID;", wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCUploadPic.ID);
			wParamMap.put("PartID", wSFCUploadPic.PartID);
			wParamMap.put("PicUrl", wSFCUploadPic.PicUrl);
			wParamMap.put("No", wSFCUploadPic.No);
			wParamMap.put("Remark", wSFCUploadPic.Remark);
			wParamMap.put("Describe", wSFCUploadPic.Describe);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCUploadPic.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCUploadPic.setID(wResult);
			} else {
				wResult = wSFCUploadPic.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCUploadPic> wList,
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
			for (SFCUploadPic wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_uploadpic WHERE ID IN({0}) ;",
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
	public SFCUploadPic SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCUploadPic wResult = new SFCUploadPic();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCUploadPic> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
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
	public List<SFCUploadPic> SelectList(BMSEmployee wLoginUser, int wID, int wPartID, OutResult<Integer> wErrorCode) {
		List<SFCUploadPic> wResultList = new ArrayList<SFCUploadPic>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT * FROM {0}.sfc_uploadpic WHERE  1=1  "
					+ "and ( :wID <= 0 or :wID = ID ) " + "and ( :wPartID <= 0 or :wPartID = PartID );",
					wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wPartID", wPartID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCUploadPic wItem = new SFCUploadPic();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.PartID = StringUtils.parseInt(wReader.get("PartID"));
				wItem.PicUrl = StringUtils.parseString(wReader.get("PicUrl"));
				wItem.No = StringUtils.parseInt(wReader.get("No"));
				wItem.Remark = StringUtils.parseString(wReader.get("Remark"));
				wItem.Describe = StringUtils.parseString(wReader.get("Describe"));

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private SFCUploadPicDAO() {
		super();
	}

	public static SFCUploadPicDAO getInstance() {
		if (Instance == null)
			Instance = new SFCUploadPicDAO();
		return Instance;
	}
}
