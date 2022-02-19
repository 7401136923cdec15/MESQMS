package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.ipt.IPTCheckRecord;
import java.io.Serializable;

public class IPTCheckRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 编号
	 */
	public String No = "";
	/**
	 * 订单号
	 */
	public String OrderNo = "";
	/**
	 * WBS编号
	 */
	public String WBSNo = "";
	/**
	 * 车型
	 */
	public String ProductNo = "";
	/**
	 * 车号
	 */
	public String PartNo = "";
	/**
	 * 修程
	 */
	public String LineName = "";
	/**
	 * 局段
	 */
	public String CustomerName = "";
	/**
	 * 检验项(作业顺序)
	 */
	public String ItemName = "";
	/**
	 * 作业内容
	 */
	public String WorkContent = "";
	/**
	 * 标准(要求、事项)
	 */
	public String Standard = "";
	/**
	 * 处理意见
	 */
	public String Opinion = "";
	/**
	 * 操作工(自检人)
	 */
	public String Operator = "";
	/**
	 * 相关部门
	 */
	public String RelaDepartments = "";
	/**
	 * 相关人员
	 */
	public String RelaClassMembers = "";
	/**
	 * 确认情况
	 */
	public String Confirmation = "";
	/**
	 * 工序名称
	 */
	public String StepName = "";
	/**
	 * 工序ID
	 */
	public int StepID = 0;
	/**
	 * 备注(结果描述)
	 */
	public String ResultDescribe = "";
	/**
	 * 图片
	 */
	public String Picture = "";
	/**
	 * 物料信息
	 */
	public String MaterialInfo = "";
	/**
	 * 厂家
	 */
	public String PartsFactory = "";
	/**
	 * 型号
	 */
	public String PartsModal = "";
	/**
	 * 编号
	 */
	public String PartsNumber = "";
	/**
	 * 结果
	 */
	public String CheckResult = "";
	/**
	 * 填写值
	 */
	public String Value = "";
	/**
	 * 记录类型
	 */
	public String RecordType = "";
	/**
	 * 工位名称
	 */
	public String StationName = "";
	/**
	 * 工位ID
	 */
	public int StationID = 0;
	/**
	 * 是否是组或工序
	 */
	public int IsGroupOrStep = 0;
	/**
	 * 图例
	 */
	public String Legend = "";
	/**
	 * 视频
	 */
	public String Video = "";
	/**
	 * 工艺员
	 */
	public String Crafter = "";
	/**
	 * 互检人
	 */
	public String Mutualer = "";
	/**
	 * 专检人
	 */
	public String Speciler = "";
	/**
	 * 检验日期
	 */
	public String CheckDate = "";
	/**
	 * 改进项目及特殊要求
	 */
	public String SpecialRequirement = "";
	/**
	 * 段方要求
	 */
	public String PeriodRequirement = "";

	/**
	 * 图片的序号
	 */
	public String PicNo = "";

	/**
	 * 部件来源
	 */
	public String PartSource = "";
	/**
	 * 转向架来源
	 */
	public String BogiesSource = "";

	/**
	 * 是否为图片工序
	 */
	public int IsPic = 0;

	public String getOrderNo() {
		return this.OrderNo;
	}

	public void setOrderNo(String orderNo) {
		this.OrderNo = orderNo;
	}

	public String getWBSNo() {
		return this.WBSNo;
	}

	public void setWBSNo(String wBSNo) {
		this.WBSNo = wBSNo;
	}

	public String getProductNo() {
		return this.ProductNo;
	}

	public void setProductNo(String productNo) {
		this.ProductNo = productNo;
	}

	public String getPartNo() {
		return this.PartNo;
	}

	public void setPartNo(String partNo) {
		this.PartNo = partNo;
	}

	public String getCrafter() {
		return this.Crafter;
	}

	public void setCrafter(String crafter) {
		this.Crafter = crafter;
	}

	public String getLineName() {
		return this.LineName;
	}

	public String getPartSource() {
		return PartSource;
	}

	public String getBogiesSource() {
		return BogiesSource;
	}

	public void setPartSource(String partSource) {
		PartSource = partSource;
	}

	public void setBogiesSource(String bogiesSource) {
		BogiesSource = bogiesSource;
	}

	public String getValue() {
		return this.Value;
	}

	public void setValue(String value) {
		this.Value = value;
	}

	public void setLineName(String lineName) {
		this.LineName = lineName;
	}

	public String getCustomerName() {
		return this.CustomerName;
	}

	public void setCustomerName(String customerName) {
		this.CustomerName = customerName;
	}

	public String getItemName() {
		return this.ItemName;
	}

	public void setItemName(String itemName) {
		this.ItemName = itemName;
	}

	public String getStandard() {
		return this.Standard;
	}

	public void setStandard(String standard) {
		this.Standard = standard;
	}

	public String getOpinion() {
		return this.Opinion;
	}

	public String getStationName() {
		return this.StationName;
	}

	public void setStationName(String stationName) {
		this.StationName = stationName;
	}

	public void setRecordType(String recordType) {
		this.RecordType = recordType;
	}

	public void setOpinion(String opinion) {
		this.Opinion = opinion;
	}

	public String getRecordType() {
		return this.RecordType;
	}

	public String getOperator() {
		return this.Operator;
	}

	public void setOperator(String operator) {
		this.Operator = operator;
	}

	public String getRelaDepartments() {
		return this.RelaDepartments;
	}

	public void setRelaDepartments(String relaDepartments) {
		this.RelaDepartments = relaDepartments;
	}

	public String getRelaClassMembers() {
		return this.RelaClassMembers;
	}

	public void setRelaClassMembers(String relaClassMembers) {
		this.RelaClassMembers = relaClassMembers;
	}

	public String getConfirmation() {
		return this.Confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.Confirmation = confirmation;
	}

	public String getNo() {
		return this.No;
	}

	public void setNo(String no) {
		this.No = no;
	}

	public String getStepName() {
		return this.StepName;
	}

	public void setStepName(String stepName) {
		this.StepName = stepName;
	}

	public String getResultDescribe() {
		return this.ResultDescribe;
	}

	public void setResultDescribe(String resultDescribe) {
		this.ResultDescribe = resultDescribe;
	}

	public String getPicture() {
		return this.Picture;
	}

	public void setPicture(String picture) {
		this.Picture = picture;
	}

	public String getMaterialInfo() {
		return this.MaterialInfo;
	}

	public void setMaterialInfo(String materialInfo) {
		this.MaterialInfo = materialInfo;
	}

	public String getPartsFactory() {
		return this.PartsFactory;
	}

	public void setPartsFactory(String partsFactory) {
		this.PartsFactory = partsFactory;
	}

	public String getPartsModal() {
		return this.PartsModal;
	}

	public void setPartsModal(String partsModal) {
		this.PartsModal = partsModal;
	}

	public String getPartsNumber() {
		return this.PartsNumber;
	}

	public void setPartsNumber(String partsNumber) {
		this.PartsNumber = partsNumber;
	}

	public String getCheckResult() {
		return this.CheckResult;
	}

	public void setCheckResult(String checkResult) {
		this.CheckResult = checkResult;
	}

	public int getIsGroupOrStep() {
		return this.IsGroupOrStep;
	}

	public void setIsGroupOrStep(int isGroupOrStep) {
		this.IsGroupOrStep = isGroupOrStep;
	}

	public String getLegend() {
		return this.Legend;
	}

	public void setLegend(String legend) {
		this.Legend = legend;
	}

	public String getVideo() {
		return this.Video;
	}

	public void setVideo(String video) {
		this.Video = video;
	}

	public String getMutualer() {
		return Mutualer;
	}

	public void setMutualer(String mutualer) {
		Mutualer = mutualer;
	}

	public String getSpeciler() {
		return Speciler;
	}

	public void setSpeciler(String speciler) {
		Speciler = speciler;
	}

	public String getCheckDate() {
		return CheckDate;
	}

	public void setCheckDate(String checkDate) {
		CheckDate = checkDate;
	}

	public String getSpecialRequirement() {
		return SpecialRequirement;
	}

	public void setSpecialRequirement(String specialRequirement) {
		SpecialRequirement = specialRequirement;
	}

	public String getPeriodRequirement() {
		return PeriodRequirement;
	}

	public void setPeriodRequirement(String periodRequirement) {
		PeriodRequirement = periodRequirement;
	}

	public String getPicNo() {
		return PicNo;
	}

	public void setPicNo(String picNo) {
		PicNo = picNo;
	}

	public int getIsPic() {
		return IsPic;
	}

	public void setIsPic(int isPic) {
		IsPic = isPic;
	}

	public String getWorkContent() {
		return WorkContent;
	}

	public void setWorkContent(String workContent) {
		WorkContent = workContent;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}
}
