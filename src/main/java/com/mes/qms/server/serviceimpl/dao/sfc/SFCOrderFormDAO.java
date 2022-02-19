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
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.sfc.SFCOrderForm;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

public class SFCOrderFormDAO extends BaseDAO {

	private static Logger logger = LoggerFactory.getLogger(SFCOrderFormDAO.class);

	private static SFCOrderFormDAO Instance = null;

	/**
	 * 添加或修改
	 * 
	 * @param wSFCOrderForm
	 * @return
	 */
	public int Update(BMSEmployee wLoginUser, SFCOrderForm wSFCOrderForm, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wSFCOrderForm == null)
				return 0;

			String wSQL = "";
			if (wSFCOrderForm.getID() <= 0) {
				wSQL = MessageFormat.format(
						"INSERT INTO {0}.sfc_orderform(OrderID,OrderNo,PartNo,CreateID,CreateTime,ConfirmID,ConfirmTime,Status,Type) VALUES(:OrderID,:OrderNo,:PartNo,:CreateID,:CreateTime,:ConfirmID,:ConfirmTime,:Status,:Type);",
						wInstance.Result);
			} else {
				wSQL = MessageFormat.format(
						"UPDATE {0}.sfc_orderform SET OrderID = :OrderID,OrderNo = :OrderNo,PartNo = :PartNo,CreateID = :CreateID,CreateTime = :CreateTime,ConfirmID = :ConfirmID,ConfirmTime = :ConfirmTime,Status = :Status,Type = :Type WHERE ID = :ID;",
						wInstance.Result);
			}

			wSQL = this.DMLChange(wSQL);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("ID", wSFCOrderForm.ID);
			wParamMap.put("OrderID", wSFCOrderForm.OrderID);
			wParamMap.put("OrderNo", wSFCOrderForm.OrderNo);
			wParamMap.put("PartNo", wSFCOrderForm.PartNo);
			wParamMap.put("CreateID", wSFCOrderForm.CreateID);
			wParamMap.put("CreateTime", wSFCOrderForm.CreateTime);
			wParamMap.put("ConfirmID", wSFCOrderForm.ConfirmID);
			wParamMap.put("ConfirmTime", wSFCOrderForm.ConfirmTime);
			wParamMap.put("Status", wSFCOrderForm.Status);
			wParamMap.put("Type", wSFCOrderForm.Type);

			KeyHolder keyHolder = new GeneratedKeyHolder();
			SqlParameterSource wSqlParameterSource = new MapSqlParameterSource(wParamMap);

			nameJdbcTemplate.update(wSQL, wSqlParameterSource, keyHolder);

			if (wSFCOrderForm.getID() <= 0) {
				wResult = keyHolder.getKey().intValue();
				wSFCOrderForm.setID(wResult);
			} else {
				wResult = wSFCOrderForm.getID();
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
	public ServiceResult<Integer> DeleteList(BMSEmployee wLoginUser, List<SFCOrderForm> wList,
			OutResult<Integer> wErrorCode) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wList == null || wList.size() <= 0)
				return wResult;

			List<String> wIDList = new ArrayList<String>();
			for (SFCOrderForm wItem : wList) {
				wIDList.add(String.valueOf(wItem.ID));
			}
			String wSql = MessageFormat.format("delete from {1}.sfc_orderform WHERE ID IN({0}) ;",
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
	public SFCOrderForm SelectByID(BMSEmployee wLoginUser, int wID, OutResult<Integer> wErrorCode) {
		SFCOrderForm wResult = new SFCOrderForm();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<SFCOrderForm> wList = SelectList(wLoginUser, wID, -1, "", -1, null, wErrorCode);
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
	public List<SFCOrderForm> SelectList(BMSEmployee wLoginUser, int wID, int wOrderID, String wPartNo, int wType,
			List<Integer> wStateIDList, OutResult<Integer> wErrorCode) {
		List<SFCOrderForm> wResultList = new ArrayList<SFCOrderForm>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultList;
			}

			String wSQL = MessageFormat.format(
					"SELECT * FROM {0}.sfc_orderform WHERE  1=1  " + "and ( :wID <= 0 or :wID = ID ) "
							+ "and ( :wOrderID <= 0 or :wOrderID = OrderID ) "
							+ "and ( :wPartNo is null or :wPartNo = '' or :wPartNo = PartNo ) "
							+ "and ( :wType <= 0 or :wType = Type ) "
							+ "and ( :wStatus is null or :wStatus = '''' or Status in ({1}));",
					wInstance.Result, wStateIDList.size() > 0 ? StringUtils.Join(",", wStateIDList) : "0");

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("wID", wID);
			wParamMap.put("wOrderID", wOrderID);
			wParamMap.put("wPartNo", wPartNo);
			wParamMap.put("wType", wType);
			wParamMap.put("wStatus", StringUtils.Join(",", wStateIDList));

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				SFCOrderForm wItem = new SFCOrderForm();

				wItem.ID = StringUtils.parseInt(wReader.get("ID"));
				wItem.OrderID = StringUtils.parseInt(wReader.get("OrderID"));
				wItem.OrderNo = StringUtils.parseString(wReader.get("OrderNo"));
				wItem.PartNo = StringUtils.parseString(wReader.get("PartNo"));
				wItem.CreateID = StringUtils.parseInt(wReader.get("CreateID"));
				wItem.CreateTime = StringUtils.parseCalendar(wReader.get("CreateTime"));
				wItem.ConfirmID = StringUtils.parseInt(wReader.get("ConfirmID"));
				wItem.ConfirmTime = StringUtils.parseCalendar(wReader.get("ConfirmTime"));
				wItem.Status = StringUtils.parseInt(wReader.get("Status"));
				wItem.Type = StringUtils.parseInt(wReader.get("Type"));

				wItem.Creator = QMSConstants.GetBMSEmployeeName(wItem.CreateID);
				wItem.ConfirmName = QMSConstants.GetBMSEmployeeName(wItem.ConfirmID);

				wResultList.add(wItem);
			}
		} catch (Exception ex) {
			wErrorCode.set(MESException.DBSQL.getValue());
			logger.error(ex.toString());
		}
		return wResultList;
	}

	private SFCOrderFormDAO() {
		super();
	}

	public static SFCOrderFormDAO getInstance() {
		if (Instance == null)
			Instance = new SFCOrderFormDAO();
		return Instance;
	}

	/**
	 * 查找订单ID
	 */
	public int SelectOrderIDByPartNo(BMSEmployee wLoginUser, String wPartNo, OutResult<Integer> wErrorCode) {
		int wResult = 0;
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			String wSQL = StringUtils.Format("SELECT ID FROM {0}.oms_order where PartNo=:PartNo;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("PartNo", wPartNo);

			wSQL = this.DMLChange(wSQL);

			List<Map<String, Object>> wQueryResult = nameJdbcTemplate.queryForList(wSQL, wParamMap);

			for (Map<String, Object> wReader : wQueryResult) {
				int wOrderID = StringUtils.parseInt(wReader.get("ID"));
				if (wOrderID > 0) {
					wResult = wOrderID;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 修改订单属性
	 */
	public void UpdateOrder(BMSEmployee wLoginUser, OMSOrder wOrder, OutResult<Integer> wErrorCode) {
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser.getCompanyID(), MESDBSource.APS,
					wLoginUser.getID(), 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return;
			}

			String wSQL = StringUtils
					.Format("update {0}.oms_order set OrderNo=:OrderNo,RealReceiveDate=:RealReceiveDate,"
							+ "RealFinishDate=:RealFinishDate where ID = :ID;", wInstance.Result);

			Map<String, Object> wParamMap = new HashMap<String, Object>();

			wParamMap.put("OrderNo", wOrder.OrderNo);
			wParamMap.put("RealReceiveDate", wOrder.RealReceiveDate);
			wParamMap.put("RealFinishDate", wOrder.RealFinishDate);
			wParamMap.put("ID", wOrder.ID);

			wSQL = this.DMLChange(wSQL);

			nameJdbcTemplate.update(wSQL, wParamMap);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}
