package com.mes.qms.server.service.po.imp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.ipt.IPTStandardC;
import com.mes.qms.server.service.utils.CloneTool;

/**
 * 导入结果对比
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-4 22:23:35
 * @LastEditTime 2020-7-4 22:23:38
 *
 */
public class IMPResultRecordC extends IMPResultRecord implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 对比结果
	 */
	public int IsSame = 0;
	/**
	 * 备注
	 */
	public String Remark = "";
	/**
	 * 标准对比数据
	 */
	List<IPTStandardC> IPTStandardCList = new ArrayList<IPTStandardC>();

	public IMPResultRecordC() {
	}

	/**
	 * 克隆
	 * 
	 * @param wFPCRoutePartPoint
	 * @return
	 */
	public static IMPResultRecordC Clone(IMPResultRecord wIMPResultRecord) {
		return CloneTool.Clone(wIMPResultRecord, IMPResultRecordC.class);
	}

	/**
	 * 对比两个工艺路线是否相同
	 * 
	 * @param wFPCRouteA 路线A
	 * @param wFPCRouteB 路线B
	 * @return
	 */
	public static ServiceResult<Boolean> SameAs(IMPResultRecord wA, IMPResultRecord wB) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		try {
			if (wA == null || wB == null) {
				wResult.Result = false;
				wResult.FaultCode = "对比元素为空!";
				return wResult;
			}

			if (wA.FileName.equals(wB.FileName)) {
				wResult.Result = true;
			} else {
				wResult.Result = false;
				wResult.FaultCode = "导入文件名不一致!";
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

	public List<IPTStandardC> getIPTStandardCList() {
		return IPTStandardCList;
	}

	public void setIPTStandardCList(List<IPTStandardC> iPTStandardCList) {
		IPTStandardCList = iPTStandardCList;
	}
}
