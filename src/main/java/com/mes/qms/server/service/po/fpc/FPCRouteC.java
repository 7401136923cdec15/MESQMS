package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.utils.CloneTool;

/**
 * 工艺路线对比类
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-4 16:37:16
 * @LastEditTime 2020-7-4 16:37:20
 *
 */
public class FPCRouteC extends FPCRoute implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 对比结果
	 */
	public int IsSame = 0;
	/**
	 * 对比备注
	 */
	public String Remark = "";
	/**
	 * 工艺工位对比数据
	 */
	public List<FPCRoutePartC> FPCRoutePartCList = new ArrayList<>();

	/**
	 * 克隆
	 * 
	 * @param wFPCRoutePartPoint
	 * @return
	 */
	public static FPCRouteC Clone(FPCRoute wFPCRoute) {
		return CloneTool.Clone(wFPCRoute, FPCRouteC.class);
	}

	/**
	 * 对比两个工艺路线是否相同
	 * 
	 * @param wFPCRouteA 路线A
	 * @param wFPCRouteB 路线B
	 * @return
	 */
	public static ServiceResult<Boolean> SameAs(FPCRoute wFPCRouteA, FPCRoute wFPCRouteB) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		try {
			if (wFPCRouteA == null || wFPCRouteB == null) {
				wResult.Result = false;
				wResult.FaultCode = "对比元素为空!";
				return wResult;
			}

			if (wFPCRouteA.Name.equals(wFPCRouteB.Name)) {
				wResult.Result = true;
			} else {
				wResult.Result = false;
				wResult.FaultCode = "工艺BOP编号不一致!";
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

	public List<FPCRoutePartC> getFPCRoutePartCList() {
		return FPCRoutePartCList;
	}

	public void setFPCRoutePartCList(List<FPCRoutePartC> fPCRoutePartCList) {
		FPCRoutePartCList = fPCRoutePartCList;
	}
}
