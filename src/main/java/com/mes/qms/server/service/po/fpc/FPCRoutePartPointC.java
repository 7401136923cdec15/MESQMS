package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

/**
 * 工艺工序对比类
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-4 17:29:33
 * @LastEditTime 2020-7-4 17:29:37
 *
 */
public class FPCRoutePartPointC extends FPCRoutePartPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否相同
	 */
	public int IsSame = 0;
	/**
	 * 备注
	 */
	public String Remark = "";
	/**
	 * B工艺路线版本
	 */
	public String BVersionNo = "";

	public FPCRoutePartPointC() {
		super();
	}

	/**
	 * 克隆
	 * 
	 * @param wFPCRoutePartPoint
	 * @return
	 */
	public static FPCRoutePartPointC Clone(FPCRoutePartPoint wFPCRoutePartPoint) {
		return CloneTool.Clone(wFPCRoutePartPoint, FPCRoutePartPointC.class);
	}

	/**
	 * 对比两个工艺路线是否相同
	 * 
	 * @param wFPCRoutePartPointA 路线A
	 * @param wFPCRoutePartPointB 路线B
	 * @return
	 */
	public static ServiceResult<Boolean> SameAs(FPCRoutePartPoint wFPCRoutePartPointA,
			FPCRoutePartPoint wFPCRoutePartPointB) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		wResult.Result = true;
		try {
			if (wFPCRoutePartPointA.PrevStepID != wFPCRoutePartPointB.PrevStepID) {
				wResult.Result = false;
				wResult.FaultCode = StringUtils.Format("前工序：【{0}】 ",
						QMSConstants.GetFPCStepName(wFPCRoutePartPointB.PrevStepID));
			}

			String wNextA = "";
			String wNextB = "";
			for (String wKey : wFPCRoutePartPointA.NextStepIDMap.keySet()) {
				wNextA += StringUtils.Format("【{0}】", QMSConstants.GetFPCStepName(Integer.parseInt(wKey)));
			}
			for (String wKey : wFPCRoutePartPointB.NextStepIDMap.keySet()) {
				wNextB += StringUtils.Format("【{0}】", QMSConstants.GetFPCStepName(Integer.parseInt(wKey)));
			}

			if (!wNextA.equals(wNextB)) {
				wResult.Result = false;
				wResult.FaultCode = StringUtils.Format("后工序：{0} ", wNextB);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return wResult;
	}

	public int getIsSame() {
		return IsSame;
	}

	public void setIsSame(int isSame) {
		IsSame = isSame;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getBVersionNo() {
		return BVersionNo;
	}

	public void setBVersionNo(String bVersionNo) {
		BVersionNo = bVersionNo;
	}
}
