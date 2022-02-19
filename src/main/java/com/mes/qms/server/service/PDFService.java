package com.mes.qms.server.service;

import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletOutputStream;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTPDFConfig;
import com.mes.qms.server.service.po.ipt.IPTPDFPart;
import com.mes.qms.server.service.po.ipt.IPTPDFStandard;
import com.mes.qms.server.service.po.oms.OMSOrder;

/**
 * 转序服务
 * 
 * @author ShrisJava
 *
 */
public interface PDFService {

	/**
	 * 输出PDF文档流
	 * 
	 * @param wLoginUser    登陆者
	 * @param wLineID       修程ID
	 * @param wProductID    车型ID
	 * @param wCustomID     局段ID
	 * @param wCarNo        车号
	 * @param wOutputStream 输出流
	 * @param wErrorCode    错误码
	 * @return 输出结果：成功与否
	 */
	ServiceResult<Boolean> IPT_OutputPDFStream(BMSEmployee wLoginUser, String wPartNo, ServletOutputStream wOutputStream);

	ServiceResult<IPTPDFConfig> IPT_QueryPDFConfig(BMSEmployee wLoginUser, int wID);

	ServiceResult<List<IPTPDFConfig>> IPT_QueryPDFConfigList(BMSEmployee wLoginUser, int wID, int wLineID,
			int wProductID, int wCustomID, int wActive);

	ServiceResult<Integer> IPT_UpdatePDFConfig(BMSEmployee wLoginUser, IPTPDFConfig wIPTPDFConfig);

	ServiceResult<Integer> IPT_ActivePDFConfigList(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive);

	ServiceResult<IPTPDFPart> IPT_QueryPDFPart(BMSEmployee wLoginUser, int wID);

	ServiceResult<List<IPTPDFPart>> IPT_QueryPDFPartList(BMSEmployee wLoginUser, int wID, int wPDFConfigID);

	ServiceResult<Integer> IPT_UpdatePDFPart(BMSEmployee wLoginUser, IPTPDFPart wIPTPDFPart);

	ServiceResult<Integer> IPT_DeletePDFPartList(BMSEmployee wLoginUser, List<IPTPDFPart> wIPTPDFPartList);

	ServiceResult<IPTPDFStandard> IPT_QueryPDFStandard(BMSEmployee wLoginUser, int wID);

	ServiceResult<List<IPTPDFStandard>> IPT_QueryPDFStandardList(BMSEmployee wLoginUser, int wID, int wPDFPartID);

	ServiceResult<Integer> IPT_UpdatePDFStandard(BMSEmployee wLoginUser, IPTPDFStandard wIPTPDFStandard);

	ServiceResult<Integer> IPT_DeletePDFStandardList(BMSEmployee wLoginUser, List<IPTPDFStandard> wIPTPDFStandardList);

	ServiceResult<List<OMSOrder>> IPT_QeuryPDFOrderList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime);
}
