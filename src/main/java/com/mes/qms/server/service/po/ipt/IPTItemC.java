package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;

import com.mes.qms.server.service.mesenum.IPTStandardType;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;

/**
 * 标准项对比类
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-4 23:14:16
 * @LastEditTime 2020-7-4 23:14:20
 *
 */
public class IPTItemC extends IPTItem implements Serializable {
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
	 * A记录流水号
	 */
	public String CodeA = "";
	/**
	 * B记录流水号
	 */
	public String CodeB = "";

	public IPTItemC() {
	}

	/**
	 * 克隆
	 * 
	 * @param wFPCRoutePartPoint
	 * @return
	 */
	public static IPTItemC Clone(IPTItem wIPTItem) {
		return CloneTool.Clone(wIPTItem, IPTItemC.class);
	}

	/**
	 * 对比两个标准是否相同
	 * 
	 * @param wA 路线A
	 * @param wB 路线B
	 * @return
	 */
	public static ServiceResult<Boolean> SameAs(IPTItem wA, IPTItem wB) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		wResult.Result = true;
		try {
			if (wA.StandardType != wB.StandardType) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("标准类型：【{0}】  ",
						IPTStandardType.getEnumType(wB.StandardType).getLable());
			}

			if (!wA.StandardValue.equals(wB.StandardValue)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("标准值：【{0}】  ", wB.StandardValue);
			}

			if (!wA.StandardBaisc.equals(wB.StandardBaisc)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("基准值：【{0}】  ", wB.StandardBaisc);
			}

			if (!wA.DefaultValue.equals(wB.DefaultValue)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("默认值：【{0}】  ", wB.DefaultValue);
			}

			if (wA.StandardLeft != wB.StandardLeft) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("上限值：【{0}】  ", wB.StandardLeft);
			}

			if (wA.StandardRight != wB.StandardRight) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("下限值：【{0}】  ", wB.StandardRight);
			}

			if (!wA.Standard.equals(wB.Standard)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("检验标准：【{0}】  ", wB.Standard);
			}

			if (wA.UnitID != wB.UnitID) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("单位：【{0}】  ", QMSConstants.GetCFGUnitName(wB.UnitID));
			}

			if (wA.ValueSource == null || wA.ValueSource.size() <= 0) {
				if (wB.ValueSource != null && wB.ValueSource.size() > 0) {
					wResult.Result = false;
					String wValueSource = StringUtils.Join(",", wB.ValueSource);
					wResult.FaultCode += StringUtils.Format("数据源：【{0}】 ", wValueSource);
				}
			}

			if (wB.ValueSource == null || wB.ValueSource.size() <= 0) {
				if (wA.ValueSource != null && wA.ValueSource.size() > 0) {
					wResult.Result = false;
					wResult.FaultCode += "数据源 ";
				}
			}

			if (wA.ValueSource != null && wA.ValueSource.size() > 0 && wB.ValueSource != null
					&& wB.ValueSource.size() > 0) {
				if (wA.ValueSource.stream().anyMatch(p -> !wB.ValueSource.stream().anyMatch(q -> q.equals(p)))
						|| wB.ValueSource.stream().anyMatch(p -> !wA.ValueSource.stream().anyMatch(q -> q.equals(p)))) {
					wResult.Result = false;
					String wValueSource = StringUtils.Join(",", wB.ValueSource);
					wResult.FaultCode += StringUtils.Format("数据源：【{0}】", wValueSource);
				}
			}

			if (!wA.Details.equals(wB.Details)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("描述：【{0}】  ", wB.Details);
			}

			if (!wA.Process.equals(wB.Process)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("过程描述：【{0}】  ", wB.Process);
			}

			if (wA.DefaultStationID != wB.DefaultStationID) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("默认工位：【{0}】  ",
						QMSConstants.GetFPCPartName(wB.DefaultStationID));
			}

			if (!wA.DefaultManufactor.equals(wB.DefaultManufactor)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("默认厂家：【{0}】  ", wB.DefaultManufactor);
			}

			if (!wA.DefaultModal.equals(wB.DefaultModal)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("默认型号：【{0}】  ", wB.DefaultModal);
			}

			if (!wA.DefaultNumber.equals(wB.DefaultNumber)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("默认编号：【{0}】  ", wB.DefaultNumber);
			}

			if (wA.IsShowStandard != wB.IsShowStandard) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("显示技术标准：【{0}】  ", wB.IsShowStandard ? "是" : "否");
			}

			if (!wA.Legend.equals(wB.Legend)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("图例：【{0}】  ", wB.Legend);
			}

			if (wA.DefaultPartPointID != wB.DefaultPartPointID) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("默认工序：【{0}】  ",
						QMSConstants.GetFPCStepName(wB.DefaultPartPointID));
			}

			if (wA.IsWriteFill != wB.IsWriteFill) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("填写值必填：【{0}】  ",
						wB.IsWriteFill == 1 ? "是" : wB.IsWriteFill == 2 ? "否" : "不显示");
			}

			if (wA.IsPictureFill != wB.IsPictureFill) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("图片必填：【{0}】  ",
						wB.IsPictureFill == 1 ? "是" : wB.IsPictureFill == 2 ? "否" : "不显示");
			}

			if (wA.IsVideoFill != wB.IsVideoFill) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("视频必填：【{0}】  ",
						wB.IsVideoFill == 1 ? "是" : wB.IsVideoFill == 2 ? "否" : "不显示");
			}

			if (wA.ManufactorOption == null || wA.ManufactorOption.size() <= 0) {
				if (wB.ManufactorOption != null && wB.ManufactorOption.size() > 0) {
					wResult.Result = false;
					String wValueSource = StringUtils.Join(",", wB.ManufactorOption);
					wResult.FaultCode += StringUtils.Format("厂家选项：【{0}】", wValueSource);
				}
			}

			if (wB.ManufactorOption == null || wB.ManufactorOption.size() <= 0) {
				if (wA.ManufactorOption != null && wA.ManufactorOption.size() > 0) {
					wResult.Result = false;
					wResult.FaultCode += "厂家选项  ";
				}
			}

			if (wA.ManufactorOption != null && wA.ManufactorOption.size() > 0 && wB.ManufactorOption != null
					&& wB.ManufactorOption.size() > 0) {
				if (wA.ManufactorOption.stream().anyMatch(p -> !wB.ManufactorOption.stream().anyMatch(q -> q.equals(p)))
						|| wB.ManufactorOption.stream()
								.anyMatch(p -> !wA.ManufactorOption.stream().anyMatch(q -> q.equals(p)))) {
					wResult.Result = false;
					String wValueSource = StringUtils.Join(",", wB.ManufactorOption);
					wResult.FaultCode += StringUtils.Format("厂家选项：【{0}】", wValueSource);
				}
			}

			if (wA.ModalOption == null || wA.ModalOption.size() <= 0) {
				if (wB.ModalOption != null && wB.ModalOption.size() > 0) {
					wResult.Result = false;
					String wValueSource = StringUtils.Join(",", wB.ModalOption);
					wResult.FaultCode += StringUtils.Format("型号选项：【{0}】", wValueSource);
				}
			}

			if (wB.ModalOption == null || wB.ModalOption.size() <= 0) {
				if (wA.ModalOption != null && wA.ModalOption.size() > 0) {
					wResult.Result = false;
					wResult.FaultCode += "型号选项 ";
				}
			}

			if (wA.ModalOption != null && wA.ModalOption.size() > 0 && wB.ModalOption != null
					&& wB.ModalOption.size() > 0) {
				if (wA.ModalOption.stream().anyMatch(p -> !wB.ModalOption.stream().anyMatch(q -> q.equals(p)))
						|| wB.ModalOption.stream().anyMatch(p -> !wA.ModalOption.stream().anyMatch(q -> q.equals(p)))) {
					wResult.Result = false;
					String wValueSource = StringUtils.Join(",", wB.ModalOption);
					wResult.FaultCode += StringUtils.Format("型号选项：【{0}】", wValueSource);
				}
			}

			if (wA.IsManufactorFill != wB.IsManufactorFill) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("厂家必填：【{0}】  ",
						wB.IsManufactorFill == 1 ? "是" : wB.IsManufactorFill == 2 ? "否" : "不显示");
			}

			if (wA.IsModalFill != wB.IsModalFill) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("型号必填：【{0}】  ",
						wB.IsModalFill == 1 ? "是" : wB.IsModalFill == 2 ? "否" : "不显示");
			}

			if (wA.IsNumberFill != wB.IsNumberFill) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("编号必填：【{0}】  ",
						wB.IsNumberFill == 1 ? "是" : wB.IsNumberFill == 2 ? "否" : "不显示");
			}

			if (wA.IsQuality != wB.IsQuality) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("质量数据：【{0}】  ",
						wB.IsQuality == 1 ? "是" : wB.IsQuality == 2 ? "否" : "不显示");
			}

			if (!wA.PartsCoding.equals(wB.PartsCoding)) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("部件编码：【{0}】  ", wB.PartsCoding);
			}

			if (wA.OrderID != wB.OrderID) {
				wResult.Result = false;
				wResult.FaultCode += StringUtils.Format("顺序：【{0}】  ", wB.OrderID);
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
