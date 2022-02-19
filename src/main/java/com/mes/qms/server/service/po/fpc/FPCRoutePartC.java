package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

/**
 * 工艺工位对比类
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-4 16:57:42
 * @LastEditTime 2020-7-4 16:57:45
 *
 */
public class FPCRoutePartC extends FPCRoutePart implements Serializable {
	/**
	 * 序列号
	 */
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
	 * 工艺工序对比数据
	 */
	public List<FPCRoutePartPointC> FPCRoutePartPointCList = new ArrayList<>();
	/**
	 * 差异数量
	 */
	public int Number = 0;
	/**
	 * B工艺路线版本
	 */
	public String BVersionNo = "";

	public FPCRoutePartC() {
		super();
	}

	/**
	 * 克隆
	 * 
	 * @param wFPCRoutePartPoint
	 * @return
	 */
	public static FPCRoutePartC Clone(FPCRoutePart wFPCRoutePart) {
		return CloneTool.Clone(wFPCRoutePart, FPCRoutePartC.class);
	}

	/**
	 * 对比两个工艺路线是否相同
	 * 
	 * @param wFPCRouteA 路线A
	 * @param wFPCRouteB 路线B
	 * @return
	 */
	public static ServiceResult<Boolean> SameAs(FPCRoutePart wFPCRouteA, FPCRoutePart wFPCRouteB) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		wResult.Result = true;
		try {
			if (!wFPCRouteA.Name.equals(wFPCRouteB.Name)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("工艺集名称：【{0}】 ", wFPCRouteB.Name);
			}

			if (wFPCRouteA.PrevPartID != wFPCRouteB.PrevPartID) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("前工位：【{0}】 ",
						QMSConstants.GetFPCPartName(wFPCRouteB.PrevPartID));
			}

			String wNextA = "";
			String wNextB = "";
			for (String wKey : wFPCRouteA.NextPartIDMap.keySet()) {
				wNextA += StringUtils.Format("【{0}】", QMSConstants.GetFPCPartName(StringUtils.parseInt(wKey)));
			}
			for (String wKey : wFPCRouteB.NextPartIDMap.keySet()) {
				wNextB += StringUtils.Format("【{0}】", QMSConstants.GetFPCPartName(StringUtils.parseInt(wKey)));
			}

			if (!wNextA.equals(wNextB)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("后工位：{0} ", wNextB);
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

	public List<FPCRoutePartPointC> getFPCRoutePartPointCList() {
		return FPCRoutePartPointCList;
	}

	public void setFPCRoutePartPointCList(List<FPCRoutePartPointC> fPCRoutePartPointCList) {
		FPCRoutePartPointCList = fPCRoutePartPointCList;
	}

	public int getNumber() {
		return Number;
	}

	public void setNumber(int number) {
		Number = number;
	}

	public String getBVersionNo() {
		return BVersionNo;
	}

	public void setBVersionNo(String bVersionNo) {
		BVersionNo = bVersionNo;
	}
}
