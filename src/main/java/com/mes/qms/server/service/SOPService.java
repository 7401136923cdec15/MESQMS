package com.mes.qms.server.service;

import java.util.List;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.ipt.IPTSolveLib;

public interface SOPService {

	ServiceResult<IPTSolveLib> IPT_QuerySolveLib(BMSEmployee wLoginUser, int wID);

	ServiceResult<List<IPTSolveLib>> IPT_QuerySolveLibList(BMSEmployee wLoginUser, int wID, int wIPTItemID,
			int wProductID, int wLineID, int wCustomID);

	ServiceResult<Integer> IPT_UpdateSolveLib(BMSEmployee wLoginUser, IPTSolveLib wIPTSolveLib);

	ServiceResult<IPTSOP> IPT_QuerySOP(BMSEmployee wLoginUser, int wID);

	ServiceResult<List<IPTSOP>> IPT_QuerySOPList(BMSEmployee wLoginUser, int wID, int wType);

	ServiceResult<Integer> IPT_UpdateSOP(BMSEmployee wLoginUser, IPTSOP wIPTSOP);
}
