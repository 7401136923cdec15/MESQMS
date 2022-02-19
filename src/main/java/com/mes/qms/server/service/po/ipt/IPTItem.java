package com.mes.qms.server.service.po.ipt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 标准项
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-1-13 09:58:19
 * @LastEditTime 2020-2-12 13:25:36
 *
 */
public class IPTItem implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	public long ID;

	/**
	 * 组ID
	 */
	public int GroupID;

	public String Text;

	public int StandardType;

	public String StandardValue;

	public String StandardBaisc;

	public String DefaultValue;

	public double StandardLeft;

	public double StandardRight;

	public String Standard;

	public int UnitID;

	public String Unit;

	public boolean Again;

	public boolean Visiable;

	public int VID = 0;

	public List<String> ValueSource;

	/**
	 * 指导书列表
	 */
	public List<IPTSOP> IPTSOPList = new ArrayList<IPTSOP>();
	/**
	 * 激活、关闭
	 */
	public int Active = 0;
	/**
	 * 项类型 填写类型 问题类型 组类型
	 */
	public int ItemType = 1;
	/**
	 * 详细描述
	 */
	public String Details = "";
	/**
	 * 过程
	 */
	public String Process = "";
	/**
	 * 默认工位ID
	 */
	public int DefaultStationID;
	/**
	 * 默认工位
	 */
	public String DefaultStation = "";

	// 20200506修改预检适配字段
	/**
	 * 其他需填字段枚举
	 */
	public int OtherValue = 0;
	/**
	 * 默认厂家
	 */
	public String DefaultManufactor = "";
	/**
	 * 默认型号
	 */
	public String DefaultModal = "";
	/**
	 * 默认编号
	 */
	public String DefaultNumber = "";
	/**
	 * 是否显示技术标准
	 */
	public boolean IsShowStandard = false;
	/**
	 * 图例
	 */
	public String Legend = "";
	/**
	 * 项列表(仅供分组用)
	 */
	public List<IPTItem> IPTItemList;
	/**
	 * 序号
	 */
	public String SerialNumber = "";
	/**
	 * 编码
	 */
	public String Code = "";
	/**
	 * 默认工序ID
	 */
	public int DefaultPartPointID = 0;
	/**
	 * 默认工序
	 */
	public String DefaultPartPointName = "";
	/**
	 * 填写必填
	 */
	public int IsWriteFill = 0;
	/**
	 * 图片必填
	 */
	public int IsPictureFill = 0;
	/**
	 * 视频必填
	 */
	public int IsVideoFill = 0;
	/**
	 * 厂家选项
	 */
	public List<String> ManufactorOption = new ArrayList<String>();
	/**
	 * 型号选项
	 */
	public List<String> ModalOption = new ArrayList<String>();
	/**
	 * 厂家必填
	 */
	public int IsManufactorFill = 0;
	/**
	 * 型号必填
	 */
	public int IsModalFill = 0;
	/**
	 * 编号必填
	 */
	public int IsNumberFill = 0;
	/**
	 * 段改含义修改为：若=1，表示需要预检，否则，需要预检
	 */
	public int IsPeriodChange = 0;
	/**
	 * 项目号
	 */
	public String ProjectNo = "";
	/**
	 * 检测点
	 */
	public String CheckPoint = "";
	/**
	 * 是否为质量数据
	 */
	public int IsQuality = 0;
	/**
	 * 部件编码
	 */
	public String PartsCoding = "";
	/**
	 * 顺序ID
	 */
	public int OrderID = 1;
	/**
	 * 全项点
	 */
	public String FullText = "";

	// 辅助属性
	/**
	 * 自检人
	 */
	public String Selfer = "";
	/**
	 * 互检人
	 */
	public String Mutualer = "";
	/**
	 * 专检人
	 */
	public String Specialer = "";
	/**
	 * 部件ID
	 */
	public int ConfigID = 0;
	/**
	 * 自动计算 0：非计算类项点 1：计算类项点
	 */
	public int AutoCalc = 0;

	// 2021-1-5 13:46:27
	/**
	 * 作业内容
	 */
	public String WorkContent = "";
	/**
	 * 作业时间
	 */
	public double WorkTime = 0.0;
	/**
	 * 图解文件名(支持多个)
	 */
	public String FileNames = "";

	/**
	 * 订单类型
	 */
	public int OrderType = 0;

	// 2021-6-17 14:53:05
	/**
	 * 拆解部件
	 */
	public String DisassemblyComponents = "";
	public int DisassemblyComponentsID = 0;
	/**
	 * 维修部件
	 */
	public String RepairParts = "";
	public int RepairPartsID = 0;
	/**
	 * 组装部件
	 */
	public String AssemblyParts = "";
	public int AssemblyPartsID = 0;
	/**
	 * 所属部件
	 */
	public String Components = "";
	public int ComponentsID = 0;

	// 自修件2021-7-1 11:12:20
	/**
	 * 自修件拆解
	 */
	public int SRDisassemblyComponentsID = 0;
	public String SRDisassemblyComponents = "";
	/**
	 * 自修件维修
	 */
	public int SRRepairPartsID = 0;
	public String SRRepairParts = "";
	/**
	 * 自修件组装
	 */
	public int SRAssemblyPartsID = 0;
	public String SRAssemblyParts = "";

	public IPTItem() {
		Text = "";
		StandardType = 0;
		Standard = "";
		StandardBaisc = "";
		StandardValue = "";
		DefaultValue = "";
		Unit = "";
		Again = false;
		ValueSource = new ArrayList<String>();
	}

	public long getID() {
		return ID;
	}

	public String getProjectNo() {
		return ProjectNo;
	}

	public int getOrderType() {
		return OrderType;
	}

	public int getDisassemblyComponentsID() {
		return DisassemblyComponentsID;
	}

	public int getRepairPartsID() {
		return RepairPartsID;
	}

	public int getAssemblyPartsID() {
		return AssemblyPartsID;
	}

	public int getComponentsID() {
		return ComponentsID;
	}

	public void setDisassemblyComponentsID(int disassemblyComponentsID) {
		DisassemblyComponentsID = disassemblyComponentsID;
	}

	public void setRepairPartsID(int repairPartsID) {
		RepairPartsID = repairPartsID;
	}

	public void setAssemblyPartsID(int assemblyPartsID) {
		AssemblyPartsID = assemblyPartsID;
	}

	public void setComponentsID(int componentsID) {
		ComponentsID = componentsID;
	}

	public void setOrderType(int orderType) {
		OrderType = orderType;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public String getWorkContent() {
		return WorkContent;
	}

	public double getWorkTime() {
		return WorkTime;
	}

	public String getDisassemblyComponents() {
		return DisassemblyComponents;
	}

	public String getRepairParts() {
		return RepairParts;
	}

	public String getAssemblyParts() {
		return AssemblyParts;
	}

	public String getComponents() {
		return Components;
	}

	public void setDisassemblyComponents(String disassemblyComponents) {
		DisassemblyComponents = disassemblyComponents;
	}

	public void setRepairParts(String repairParts) {
		RepairParts = repairParts;
	}

	public void setAssemblyParts(String assemblyParts) {
		AssemblyParts = assemblyParts;
	}

	public void setComponents(String components) {
		Components = components;
	}

	public void setWorkContent(String workContent) {
		WorkContent = workContent;
	}

	public void setWorkTime(double workTime) {
		WorkTime = workTime;
	}

	public void setProjectNo(String projectNo) {
		ProjectNo = projectNo;
	}

	public String getPartsCoding() {
		return PartsCoding;
	}

	public void setPartsCoding(String partsCoding) {
		PartsCoding = partsCoding;
	}

	public String getCheckPoint() {
		return CheckPoint;
	}

	public int getIsQuality() {
		return IsQuality;
	}

	public void setIsQuality(int isQuality) {
		IsQuality = isQuality;
	}

	public void setCheckPoint(String checkPoint) {
		CheckPoint = checkPoint;
	}

	public int getVID() {
		return VID;
	}

	public String getProcess() {
		return Process;
	}

	public int isIsNumberFill() {
		return IsNumberFill;
	}

	public void setIsNumberFill(int isNumberFill) {
		IsNumberFill = isNumberFill;
	}

	public int getOtherValue() {
		return OtherValue;
	}

	public void setOtherValue(int otherValue) {
		OtherValue = otherValue;
	}

	public String getDefaultManufactor() {
		return DefaultManufactor;
	}

	public void setDefaultManufactor(String defaultManufactor) {
		DefaultManufactor = defaultManufactor;
	}

	public String getDefaultModal() {
		return DefaultModal;
	}

	public void setDefaultModal(String defaultModal) {
		DefaultModal = defaultModal;
	}

	public String getDefaultNumber() {
		return DefaultNumber;
	}

	public void setDefaultNumber(String defaultNumber) {
		DefaultNumber = defaultNumber;
	}

	public boolean isIsShowStandard() {
		return IsShowStandard;
	}

	public void setIsShowStandard(boolean isShowStandard) {
		IsShowStandard = isShowStandard;
	}

	public String getLegend() {
		return Legend;
	}

	public void setLegend(String legend) {
		Legend = legend;
	}

	public void setProcess(String process) {
		Process = process;
	}

	public void setVID(int vID) {
		VID = vID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public String getText() {
		return Text;
	}

	public int getDefaultStationID() {
		return DefaultStationID;
	}

	public void setDefaultStationID(int defaultStationID) {
		DefaultStationID = defaultStationID;
	}

	public String getDefaultStation() {
		return DefaultStation;
	}

	public void setDefaultStation(String defaultStation) {
		DefaultStation = defaultStation;
	}

	public int getUnitID() {
		return UnitID;
	}

	public int getGroupID() {
		return GroupID;
	}

	public void setGroupID(int groupID) {
		GroupID = groupID;
	}

	public void setUnitID(int unitID) {
		UnitID = unitID;
	}

	public void setText(String text) {
		Text = text;
	}

	public int getStandardType() {
		return StandardType;
	}

	public void setStandardType(int standardType) {
		StandardType = standardType;
	}

	public String getStandardValue() {
		return StandardValue;
	}

	public void setStandardValue(String standardValue) {
		StandardValue = standardValue;
	}

	public String getDetails() {
		return Details;
	}

	public void setDetails(String details) {
		Details = details;
	}

	public String getStandardBaisc() {
		return StandardBaisc;
	}

	public void setStandardBaisc(String standardBaisc) {
		StandardBaisc = standardBaisc;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public int getIsWriteFill() {
		return IsWriteFill;
	}

	public int getIsPictureFill() {
		return IsPictureFill;
	}

	public int getIsVideoFill() {
		return IsVideoFill;
	}

	public int getIsManufactorFill() {
		return IsManufactorFill;
	}

	public int getIsModalFill() {
		return IsModalFill;
	}

	public int getIsNumberFill() {
		return IsNumberFill;
	}

	public String getDefaultValue() {
		return DefaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		DefaultValue = defaultValue;
	}

	public double getStandardLeft() {
		return StandardLeft;
	}

	public void setStandardLeft(double standardLeft) {
		StandardLeft = standardLeft;
	}

	public double getStandardRight() {
		return StandardRight;
	}

	public void setStandardRight(double standardRight) {
		StandardRight = standardRight;
	}

	public String getStandard() {
		return Standard;
	}

	public void setStandard(String standard) {
		Standard = standard;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public boolean isAgain() {
		return Again;
	}

	public void setAgain(boolean again) {
		Again = again;
	}

	public boolean isVisiable() {
		return Visiable;
	}

	public void setVisiable(boolean visiable) {
		Visiable = visiable;
	}

	public List<String> getValueSource() {
		return ValueSource;
	}

	public void setValueSource(List<String> valueSource) {
		ValueSource = valueSource;
	}

	public int getItemType() {
		return ItemType;
	}

	public void setItemType(int itemType) {
		ItemType = itemType;
	}

	public List<IPTSOP> getIPTSOPList() {
		return IPTSOPList;
	}

	public void setIPTSOPList(List<IPTSOP> iPTSOPList) {
		IPTSOPList = iPTSOPList;
	}

	public List<IPTItem> getIPTItemList() {
		return IPTItemList;
	}

	public void setIPTItemList(List<IPTItem> iPTItemList) {
		IPTItemList = iPTItemList;
	}

	public String getSerialNumber() {
		return SerialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public int getDefaultPartPointID() {
		return DefaultPartPointID;
	}

	public void setDefaultPartPointID(int defaultPartPointID) {
		DefaultPartPointID = defaultPartPointID;
	}

	public String getDefaultPartPointName() {
		return DefaultPartPointName;
	}

	public void setDefaultPartPointName(String defaultPartPointName) {
		DefaultPartPointName = defaultPartPointName;
	}

	public int isIsWriteFill() {
		return IsWriteFill;
	}

	public void setIsWriteFill(int isWriteFill) {
		IsWriteFill = isWriteFill;
	}

	public int isIsPictureFill() {
		return IsPictureFill;
	}

	public void setIsPictureFill(int isPictureFill) {
		IsPictureFill = isPictureFill;
	}

	public int isIsVideoFill() {
		return IsVideoFill;
	}

	public void setIsVideoFill(int isVideoFill) {
		IsVideoFill = isVideoFill;
	}

	public List<String> getManufactorOption() {
		return ManufactorOption;
	}

	public void setManufactorOption(List<String> manufactorOption) {
		ManufactorOption = manufactorOption;
	}

	public List<String> getModalOption() {
		return ModalOption;
	}

	public void setModalOption(List<String> modalOption) {
		ModalOption = modalOption;
	}

	public int isIsManufactorFill() {
		return IsManufactorFill;
	}

	public void setIsManufactorFill(int isManufactorFill) {
		IsManufactorFill = isManufactorFill;
	}

	public int isIsModalFill() {
		return IsModalFill;
	}

	public void setIsModalFill(int isModalFill) {
		IsModalFill = isModalFill;
	}

	public int getIsPeriodChange() {
		return IsPeriodChange;
	}

	public void setIsPeriodChange(int isPeriodChange) {
		IsPeriodChange = isPeriodChange;
	}

	public String getFullText() {
		return FullText;
	}

	public void setFullText(String fullText) {
		FullText = fullText;
	}

	public String getSelfer() {
		return Selfer;
	}

	public void setSelfer(String selfer) {
		Selfer = selfer;
	}

	public String getMutualer() {
		return Mutualer;
	}

	public void setMutualer(String mutualer) {
		Mutualer = mutualer;
	}

	public int getConfigID() {
		return ConfigID;
	}

	public void setConfigID(int configID) {
		ConfigID = configID;
	}

	public String getSpecialer() {
		return Specialer;
	}

	public void setSpecialer(String specialer) {
		Specialer = specialer;
	}

	public int getAutoCalc() {
		return AutoCalc;
	}

	public void setAutoCalc(int autoCalc) {
		AutoCalc = autoCalc;
	}

	public String getFileNames() {
		return FileNames;
	}

	public int getSRDisassemblyComponentsID() {
		return SRDisassemblyComponentsID;
	}

	public String getSRDisassemblyComponents() {
		return SRDisassemblyComponents;
	}

	public int getSRRepairPartsID() {
		return SRRepairPartsID;
	}

	public String getSRRepairParts() {
		return SRRepairParts;
	}

	public int getSRAssemblyPartsID() {
		return SRAssemblyPartsID;
	}

	public String getSRAssemblyParts() {
		return SRAssemblyParts;
	}

	public void setSRDisassemblyComponentsID(int sRDisassemblyComponentsID) {
		SRDisassemblyComponentsID = sRDisassemblyComponentsID;
	}

	public void setSRDisassemblyComponents(String sRDisassemblyComponents) {
		SRDisassemblyComponents = sRDisassemblyComponents;
	}

	public void setSRRepairPartsID(int sRRepairPartsID) {
		SRRepairPartsID = sRRepairPartsID;
	}

	public void setSRRepairParts(String sRRepairParts) {
		SRRepairParts = sRRepairParts;
	}

	public void setSRAssemblyPartsID(int sRAssemblyPartsID) {
		SRAssemblyPartsID = sRAssemblyPartsID;
	}

	public void setSRAssemblyParts(String sRAssemblyParts) {
		SRAssemblyParts = sRAssemblyParts;
	}

	public void setFileNames(String fileNames) {
		FileNames = fileNames;
	}
}
