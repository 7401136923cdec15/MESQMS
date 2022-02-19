package com.mes.qms.server.serviceimpl.dao.sfc;

import java.text.MessageFormat;
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
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.pic.SFCCarInfo;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.LOCOAPSServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class SFCCarInfoDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCCarInfoDAO.class);

	private static SFCCarInfoDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCCarInfo
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCCarInfo wSFCCarInfo, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCCarInfo == null)
				return 0;

			String wSQL = "";
			if (wSFCCarInfo.getID() <= 0) {
				wSQL = MessageFormat.format("INSERT INTO {0}.sfc_carinfo(OrderID,ProductNo,CarNo,CreaterID,CreateTime) "
						+ "VALUES(:OrderID,:ProductNo,:CarNo,:CreaterID,:CreateTime);", wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.sfc_carinfo SET OrderID = :OrderID,ProductNo = :ProductNo,"
								+ "CarNo = :CarNo,CreaterID = :CreaterID,CreateTime = :CreateTime " + "WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCCarInfo.ID);
			wParamMap.put("OrderID", wSFCCarInfo.OrderID);
			wParamMap.put("ProductNo", wSFCCarInfo.ProductNo);
			wParamMap.put("CarNo", wSFCCarInfo.CarNo);
			wParamMap.put("CreaterID", wSFCCarInfo.CreaterID);
			wParamMap.put("CreateTime", wSFCCarInfo.CreateTime);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCCarInfo.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCCarInfo.setID(wResult);
			} else {
				wResult = wSFCCarInfo.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCCarInfo> wList,
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
			for (SFCCarInfo wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_carinfo WHERE ID IN({0}) ;",
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
	public SFCCarInfo SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCCarInfo wResult = new SFCCarInfo();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCCarInfo> wList = SelectList(wLoginUser, wID, -1, wErrorCode);
			if (wList == null || wList.size() != 1)
				return wResult;
			wResult = wList.get(0);

			wResult.SFCRankInfoList = SFCRankInfoDAO.getInstance().SelectList(wLoginUser, -1, wResult.ID, wErrorCode);
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
	public List<SFCCarInfo> SelectList(BMSEmployee wLoginUser, int wID, int wOrderID, OutResult<Integer> wErrorCode) {
		List<SFCCarInfo> wResultList = new ArrayList<SFCCarInfo>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.Basic, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format("SELECT ID,OrderID,ProductNo,CarNo,CreaterID,CreateTime, "
					+ "(SELECT count(*)  FROM {0}.sfc_rankinfo where CarID=t2.ID) TotalSize,"
					+ "(select count(*)  from {0}.sfc_rankinfo t where CarID=t2.ID and 0 "
					+ "in (select (select count(*) from {0}.sfc_partinfo where RankID=t.ID)-(SELECT count(*) "
					+ "FROM {0}.sfc_partinfo t1 where RankID=t.ID and 0 in (SELECT count(*) "
					+ "FROM {0}.sfc_uploadpic where PartID=t1.ID and PicUrl ='''')) as Num)) FinishSize "
					+ "FROM {0}.sfc_carinfo t2 where 1=1 " + "and ( :wID <= 0 or :wID = ID ) "
					+ "and ( :wOrderID <= 0 or :wOrderID = OrderID );", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wOrderID", wOrderID);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCCarInfo wItem = new SFCCarInfo();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.ProductNo = StringUtils.parseString(wReader.get("ProductNo"));
				wItem.CarNo = StringUtils.parseString(wReader.get("CarNo"));
				wItem.CreaterID = StringUtils.parseInt(wReader.get("CreaterID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));

				wItem.TotalSize = StringUtils.parseInt(wReader.get("TotalSize"));
				wItem.FinishSize = StringUtils.parseInt(wReader.get("FinishSize"));

				wItem.Creator = QMSConstants.GetBMSEmployeeName(wItem.CreaterID);
//				wItem.SFCRankInfoList = SFCRankInfoDAO.getInstance().SelectList(wLoginUser, -1, wItem.ID, wErrorCode);
				// wItem.Order = QMSConstants.GetOMSOrder(wItem.OrderID);

				// 赋值进度
//				wItem.FinishSize = (int) wItem.SFCRankInfoList.stream().filter(p -> p.SFCPartInfoList != null
//						&& p.SFCPartInfoList.size() > 0
//						&& p.SFCPartInfoList.stream()
//								.allMatch(q -> q.SFCUploadPicList != null && q.SFCUploadPicList.size() > 0
//										&& q.SFCUploadPicList.stream().allMatch(r -> StringUtils.isNotEmpty(r.PicUrl))))
//						.count();
//				wItem.TotalSize = wItem.SFCRankInfoList.size();

				wItem.Progress = StringUtils.Format("({0}/{1})", wItem.FinishSize, wItem.TotalSize);

				wResultList.add(wItem);
			}

			if (wResultList.size() <= 0)
				return wResultList;
			List<Integer> wOrderIDList = wResultList.stream().map(p -> p.OrderID).distinct()
					.collect(Collectors.toList());
			wOrderIDList.removeIf(p -> p <= 0);
			if (wOrderIDList.size() <= 0)
				return wResultList;
			Map<Integer, OMSOrder> wOMSOrderMap = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser, wOrderIDList).List(OMSOrder.class).stream()
					.collect(Collectors.toMap(p -> p.ID, p -> p));
			if (wOMSOrderMap.size() <= 0)
				return wResultList;

			wResultList.forEach(
					p -> p.Order = wOMSOrderMap.containsKey(p.OrderID) ? wOMSOrderMap.get(p.OrderID) : p.Order);

		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private SFCCarInfoDAO() {
		super();
	}

	public static SFCCarInfoDAO getInstance() {
		if (Instance == null)
			Instance = new SFCCarInfoDAO();
		return Instance;
	}
}
