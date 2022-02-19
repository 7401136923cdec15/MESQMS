package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.utils.CloneTool;

public class IPTStandardC extends IPTStandard implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 导入结果
	 */
	public int IsSame = 0;
	/**
	 * 导入备注
	 */
	public String Remark = "";
	/**
	 * 标准项对比数据
	 */
	public List<IPTItemC> IPTItemCList = new ArrayList<>();
	/**
	 * 差异项数量
	 */
	public int Number = 0;
	/**
	 * A记录流水号
	 */
	public String CodeA = "";
	/**
	 * B记录流水号
	 */
	public String CodeB = "";

	public IPTStandardC() {
	}

	/**
	 * 克隆
	 * 
	 * @param wFPCRoutePartPoint
	 * @return
	 */
	public static IPTStandardC Clone(IPTStandard wIPTStandard) {
		return CloneTool.Clone(wIPTStandard, IPTStandardC.class);
	}

	/**
	 * 对比两个标准是否相同
	 * 
	 * @param wA 路线A
	 * @param wB 路线B
	 * @return
	 */
	public static ServiceResult<Boolean> SameAs(IPTStandard wA, IPTStandard wB) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		try {
			if (wA.PartPointID == wB.PartPointID) {
				wResult.Result = true;
			} else {
				wResult.Result = false;
				wResult.FaultCode = "工序不一致!";
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

	public List<IPTItemC> getIPTItemCList() {
		return IPTItemCList;
	}

	public void setIPTItemCList(List<IPTItemC> iPTItemCList) {
		IPTItemCList = iPTItemCList;
	}

	public int getNumber() {
		return Number;
	}

	public void setNumber(int number) {
		Number = number;
	}

	public String getCodeA() {
		return CodeA;
	}

	public void setCodeA(String codeA) {
		CodeA = codeA;
	}

	public String getCodeB() {
		return CodeB;
	}

	public void setCodeB(String codeB) {
		CodeB = codeB;
	}
}
