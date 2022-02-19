package com.mes.qms.server.service.po.mss;

import java.io.Serializable;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

/**
 * BOM子项对比类
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-5 14:06:58
 * @LastEditTime 2020-7-5 14:07:02
 *
 */
public class MSSBOMItemC extends MSSBOMItem implements Serializable {
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
	public String CompareRemark = "";
	/**
	 * ABOM编号
	 */
	public String BOMNoA = "";
	/**
	 * BBOM编号
	 */
	public String BOMNoB = "";

	/**
	 * 克隆
	 * 
	 * @param wFPCRoutePartPoint
	 * @return
	 */
	public static MSSBOMItemC Clone(MSSBOMItem wMSSBOMItem) {
		return CloneTool.Clone(wMSSBOMItem, MSSBOMItemC.class);
	}

	/**
	 * 对比两个标准是否相同
	 * 
	 * @param wA 路线A
	 * @param wB 路线B
	 * @return
	 */
	public static ServiceResult<Boolean> SameAs(MSSBOMItem wA, MSSBOMItem wB) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		wResult.Result = true;
		try {
			// 数量
			if (wA.MaterialNumber != wB.MaterialNumber) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("数量：【{0}】", wB.MaterialNumber);
			}
			// 单位
			if (wA.UnitID != wB.UnitID) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("单位：【{0}】", QMSConstants.GetCFGUnitName(wB.UnitID));
			}
			// 偶换率
			if (wA.ReplaceRatio != wB.ReplaceRatio) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("偶换率：【{0}】", wB.ReplaceRatio);
			}
			// 原拆原装要求
			if (wA.OriginalType != wB.OriginalType) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("原拆原装要求：【{0}】", wB.OriginalType);
			}
			// 是否拆解下车
			if (wA.DisassyType != wB.DisassyType) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("是否拆解下车：【{0}】", wB.DisassyType);
			}
			// 备注
			if (!wA.Remark.equals(wB.Remark)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("备注：【{0}】", wB.Remark);
			}
			// 上层物料
			if (wA.ParentID != wB.ParentID) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("上层物料：【{0}】", wB.ParentID);
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
		return CompareRemark;
	}

	public void setRemark(String remark) {
		CompareRemark = remark;
	}

}
