package com.mes.qms.server.serviceimpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.SOPService;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.ipt.IPTSolveLib;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTSOPDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTSolveLibDAO;

@Service
public class SOPServiceImpl implements SOPService {

	private static Logger logger = LoggerFactory.getLogger(SOPServiceImpl.class);

	@Override
	public ServiceResult<IPTSolveLib> IPT_QuerySolveLib(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTSolveLib> wResult = new ServiceResult<IPTSolveLib>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTSolveLibDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTSolveLib>> IPT_QuerySolveLibList(BMSEmployee wLoginUser, int wID, int wIPTItemID,
			int wProductID, int wLineID, int wCustomID) {
		ServiceResult<List<IPTSolveLib>> wResult = new ServiceResult<List<IPTSolveLib>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTSolveLibDAO.getInstance().SelectList(wLoginUser, wID, wIPTItemID, wProductID, wLineID,
					wCustomID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_UpdateSolveLib(BMSEmployee wLoginUser, IPTSolveLib wIPTSolveLib) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTSolveLibDAO.getInstance().Update(wLoginUser, wIPTSolveLib, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<IPTSOP> IPT_QuerySOP(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTSOP> wResult = new ServiceResult<IPTSOP>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTSOPDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<IPTSOP>> IPT_QuerySOPList(BMSEmployee wLoginUser, int wID, int wType) {
		ServiceResult<List<IPTSOP>> wResult = new ServiceResult<List<IPTSOP>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTSOPDAO.getInstance().SelectList(wLoginUser, wID, wType, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_UpdateSOP(BMSEmployee wLoginUser, IPTSOP wIPTSOP) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTSOPDAO.getInstance().Update(wLoginUser, wIPTSOP, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}
}
