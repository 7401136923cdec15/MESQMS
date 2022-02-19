package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.bms.BMSDepartment;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTProblemAssess;
import com.mes.qms.server.service.po.ipt.IPTProblemBomItem;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.ipt.IPTValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 预检问题项
 */
public class IPTPreCheckProblem implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	public int ID = 0;
	/**
	 * 预检单ID
	 */
	public int IPTPreCheckTaskID = 0;
	/**
	 * 问题项ID
	 */
	public int IPTItemID = 0;
	/**
	 * 问题项名称
	 */
	public String IPTItemName = "";
	/**
	 * 解决方案信息
	 */
	public int SolveID = 0;
	public String Description = "";
	public String Details = "";
	public List<String> ImageList = new ArrayList<>();
	public List<String> VideoList = new ArrayList<>();
	public List<IPTSOP> IPTSOPList = new ArrayList<>();
	public String FullDescribe = "";
	/**
	 * 预检人信息
	 */
	public int PreCheckID = 0;
	public String PreCheckName = "";
	public Calendar PreCheckTime = Calendar.getInstance();
	/**
	 * 车型
	 */
	public int ProductID = 0;
	public String ProductNo = "";
	/**
	 * 车号
	 */
	public String CarNumber = "";
	/**
	 * 修程
	 */
	public int LineID = 0;
	public String LineName = "";
	/**
	 * 局段
	 */
	public int CustomID = 0;
	public String CustomName = "";
	/**
	 * 现场工艺节点
	 */
	public int CarftID = 0;
	public String CraftName = "";
	public Calendar CarftTime = Calendar.getInstance();
	/**
	 * 部门列表(暂时不用)
	 */
	public List<BMSDepartment> DepartmentList = new ArrayList<>();
	/**
	 * 人员列表(暂时不用)
	 */
	public List<BMSEmployee> EmployeeList = new ArrayList<>();
	/**
	 * 部门负责人
	 */
	public BMSEmployee Manager = new BMSEmployee();
	/**
	 * 执行工位
	 */
	public int DoStationID = 0;
	public String DoStationName = "";
	/**
	 * 实际执行工位
	 */
	public int RealStationID = 0;
	public String RealStationName = "";
	/**
	 * 执行工序(暂时不用)
	 */
	public int DoPartPointID = 0;
	public String DoPartPointName = "";
	/**
	 * 执行部门
	 */
	public int DoDepartmentID = 0;
	public String DoDepartmentName = "";
	/**
	 * 部门下发节点信息
	 */
	public int DepartmentIssueID = 0;
	public String DepartmentIssueName = "";
	public Calendar DepartmentIssueTime = Calendar.getInstance();
	/**
	 * 执行班组
	 */
	public int DoClassID = 0;
	public String DoClassName = "";
	/**
	 * 班组处理节点
	 */
	public int ClassIssueID = 0;
	public String ClassIssueName = "";
	public Calendar ClassIssueTime = Calendar.getInstance();
	/**
	 * 执行人
	 */
	public int DoPersonID = 0;
	public String DoPersonName = "";
	/**
	 * 状态
	 */
	public int Status = 0;
	/**
	 * 检验值(自检、互检、专检)辅助属性
	 */
	public List<IPTValue> IPTValueList = new ArrayList<>();
	/**
	 * 问题项点(辅助属性)
	 */
	public IPTItem IPTItem = new IPTItem();
	/**
	 * 工序任务ID
	 */
	public int APSTaskStepID = 0;
	/**
	 * 订单
	 */
	public int OrderID = 0;
	public String OrderNo = "";
	/**
	 * 物料信息
	 */
	public List<IPTProblemBomItem> IPTProblemBomItemList = new ArrayList<>();
	/**
	 * 评审人列表
	 */
	public List<IPTProblemAssess> IPTProblemAssessList = new ArrayList<>();
	/**
	 * 是否派工
	 */
	public boolean IsDischarged = false;
	/**
	 * 预检值
	 */
	public IPTValue PreCheckValue = new IPTValue();
	/**
	 * 是否有权限处理
	 */
	public boolean IsPower = false;
	/**
	 * 确认人列表
	 */
	public List<IPTProblemConfirmer> IPTProblemConfirmerList = new ArrayList<>();
	/**
	 * 确认状态
	 */
	public int ConfirmStatus = 0;
	/**
	 * 预检工序ID
	 */
	public int StepID = 0;
	/**
	 * 预检工序名称
	 */
	public String StepName = "";

	// 辅助属性
	/**
	 * 1待办 2已办
	 */
	public int Flag = 0;

	public IPTPreCheckProblem() {
		this.PreCheckTime.set(2000, 0, 1);
		this.CarftTime.set(2000, 0, 1);
		this.DepartmentIssueTime.set(2000, 0, 1);
		this.ClassIssueTime.set(2000, 0, 1);
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int iD) {
		this.ID = iD;
	}

	public int getAPSTaskStepID() {
		return this.APSTaskStepID;
	}

	public boolean isIsPower() {
		return this.IsPower;
	}

	public void setIsPower(boolean isPower) {
		this.IsPower = isPower;
	}

	public List<IPTProblemAssess> getIPTProblemAssessList() {
		return this.IPTProblemAssessList;
	}

	public void setIPTProblemAssessList(List<IPTProblemAssess> iPTProblemAssessList) {
		this.IPTProblemAssessList = iPTProblemAssessList;
	}

	public boolean isIsDischarged() {
		return this.IsDischarged;
	}

	public void setIsDischarged(boolean isDischarged) {
		this.IsDischarged = isDischarged;
	}

	public void setAPSTaskStepID(int aPSTaskStepID) {
		this.APSTaskStepID = aPSTaskStepID;
	}

	public List<IPTProblemBomItem> getIPTProblemBomItemList() {
		return this.IPTProblemBomItemList;
	}

	public void setIPTProblemBomItemList(List<IPTProblemBomItem> iPTProblemBomItemList) {
		this.IPTProblemBomItemList = iPTProblemBomItemList;
	}

	public IPTItem getIPTItem() {
		return this.IPTItem;
	}

	public void setIPTItem(IPTItem iPTItem) {
		this.IPTItem = iPTItem;
	}

	public int getOrderID() {
		return this.OrderID;
	}

	public void setOrderID(int orderID) {
		this.OrderID = orderID;
	}

	public String getOrderNo() {
		return this.OrderNo;
	}

	public void setOrderNo(String orderNo) {
		this.OrderNo = orderNo;
	}

	public int getIPTPreCheckTaskID() {
		return this.IPTPreCheckTaskID;
	}

	public void setIPTPreCheckTaskID(int iPTPreCheckTaskID) {
		this.IPTPreCheckTaskID = iPTPreCheckTaskID;
	}

	public int getIPTItemID() {
		return this.IPTItemID;
	}

	public void setIPTItemID(int iPTItemID) {
		this.IPTItemID = iPTItemID;
	}

	public String getIPTItemName() {
		return this.IPTItemName;
	}

	public void setIPTItemName(String iPTItemName) {
		this.IPTItemName = iPTItemName;
	}

	public int getSolveID() {
		return this.SolveID;
	}

	public void setSolveID(int solveID) {
		this.SolveID = solveID;
	}

	public String getDescription() {
		return this.Description;
	}

	public void setDescription(String description) {
		this.Description = description;
	}

	public String getDetails() {
		return this.Details;
	}

	public void setDetails(String details) {
		this.Details = details;
	}

	public List<String> getImageList() {
		return this.ImageList;
	}

	public void setImageList(List<String> imageList) {
		this.ImageList = imageList;
	}

	public List<String> getVideoList() {
		return this.VideoList;
	}

	public void setVideoList(List<String> videoList) {
		this.VideoList = videoList;
	}

	public List<IPTSOP> getIPTSOPList() {
		return this.IPTSOPList;
	}

	public void setIPTSOPList(List<IPTSOP> iPTSOPList) {
		this.IPTSOPList = iPTSOPList;
	}

	public String getFullDescribe() {
		return this.FullDescribe;
	}

	public void setFullDescribe(String fullDescribe) {
		this.FullDescribe = fullDescribe;
	}

	public int getPreCheckID() {
		return this.PreCheckID;
	}

	public void setPreCheckID(int preCheckID) {
		this.PreCheckID = preCheckID;
	}

	public String getPreCheckName() {
		return this.PreCheckName;
	}

	public void setPreCheckName(String preCheckName) {
		this.PreCheckName = preCheckName;
	}

	public int getProductID() {
		return this.ProductID;
	}

	public void setProductID(int productID) {
		this.ProductID = productID;
	}

	public String getProductNo() {
		return this.ProductNo;
	}

	public void setProductNo(String productNo) {
		this.ProductNo = productNo;
	}

	public String getCarNumber() {
		return this.CarNumber;
	}

	public void setCarNumber(String carNumber) {
		this.CarNumber = carNumber;
	}

	public int getLineID() {
		return this.LineID;
	}

	public void setLineID(int lineID) {
		this.LineID = lineID;
	}

	public String getLineName() {
		return this.LineName;
	}

	public void setLineName(String lineName) {
		this.LineName = lineName;
	}

	public int getCustomID() {
		return this.CustomID;
	}

	public void setCustomID(int customID) {
		this.CustomID = customID;
	}

	public String getCustomName() {
		return this.CustomName;
	}

	public void setCustomName(String customName) {
		this.CustomName = customName;
	}

	public int getDoStationID() {
		return this.DoStationID;
	}

	public void setDoStationID(int doStationID) {
		this.DoStationID = doStationID;
	}

	public String getDoStationName() {
		return this.DoStationName;
	}

	public void setDoStationName(String doStationName) {
		this.DoStationName = doStationName;
	}

	public int getDoPartPointID() {
		return this.DoPartPointID;
	}

	public void setDoPartPointID(int doPartPointID) {
		this.DoPartPointID = doPartPointID;
	}

	public String getDoPartPointName() {
		return this.DoPartPointName;
	}

	public void setDoPartPointName(String doPartPointName) {
		this.DoPartPointName = doPartPointName;
	}

	public Calendar getPreCheckTime() {
		return this.PreCheckTime;
	}

	public void setPreCheckTime(Calendar preCheckTime) {
		this.PreCheckTime = preCheckTime;
	}

	public int getCarftID() {
		return this.CarftID;
	}

	public void setCarftID(int carftID) {
		this.CarftID = carftID;
	}

	public String getCraftName() {
		return this.CraftName;
	}

	public void setCraftName(String craftName) {
		this.CraftName = craftName;
	}

	public Calendar getCarftTime() {
		return this.CarftTime;
	}

	public void setCarftTime(Calendar carftTime) {
		this.CarftTime = carftTime;
	}

	public List<BMSDepartment> getDepartmentList() {
		return this.DepartmentList;
	}

	public void setDepartmentList(List<BMSDepartment> departmentList) {
		this.DepartmentList = departmentList;
	}

	public List<BMSEmployee> getEmployeeList() {
		return this.EmployeeList;
	}

	public void setEmployeeList(List<BMSEmployee> employeeList) {
		this.EmployeeList = employeeList;
	}

	public int getDoDepartmentID() {
		return this.DoDepartmentID;
	}

	public void setDoDepartmentID(int doDepartmentID) {
		this.DoDepartmentID = doDepartmentID;
	}

	public String getDoDepartmentName() {
		return this.DoDepartmentName;
	}

	public void setDoDepartmentName(String doDepartmentName) {
		this.DoDepartmentName = doDepartmentName;
	}

	public int getDepartmentIssueID() {
		return this.DepartmentIssueID;
	}

	public void setDepartmentIssueID(int departmentIssueID) {
		this.DepartmentIssueID = departmentIssueID;
	}

	public String getDepartmentIssueName() {
		return this.DepartmentIssueName;
	}

	public void setDepartmentIssueName(String departmentIssueName) {
		this.DepartmentIssueName = departmentIssueName;
	}

	public Calendar getDepartmentIssueTime() {
		return this.DepartmentIssueTime;
	}

	public void setDepartmentIssueTime(Calendar departmentIssueTime) {
		this.DepartmentIssueTime = departmentIssueTime;
	}

	public int getDoClassID() {
		return this.DoClassID;
	}

	public void setDoClassID(int doClassID) {
		this.DoClassID = doClassID;
	}

	public String getDoClassName() {
		return this.DoClassName;
	}

	public void setDoClassName(String doClassName) {
		this.DoClassName = doClassName;
	}

	public int getClassIssueID() {
		return this.ClassIssueID;
	}

	public void setClassIssueID(int classIssueID) {
		this.ClassIssueID = classIssueID;
	}

	public String getClassIssueName() {
		return this.ClassIssueName;
	}

	public void setClassIssueName(String classIssueName) {
		this.ClassIssueName = classIssueName;
	}

	public Calendar getClassIssueTime() {
		return this.ClassIssueTime;
	}

	public void setClassIssueTime(Calendar classIssueTime) {
		this.ClassIssueTime = classIssueTime;
	}

	public int getDoPersonID() {
		return this.DoPersonID;
	}

	public void setDoPersonID(int doPersonID) {
		this.DoPersonID = doPersonID;
	}

	public String getDoPersonName() {
		return this.DoPersonName;
	}

	public void setDoPersonName(String doPersonName) {
		this.DoPersonName = doPersonName;
	}

	public int getStatus() {
		return this.Status;
	}

	public void setStatus(int status) {
		this.Status = status;
	}

	public BMSEmployee getManager() {
		return this.Manager;
	}

	public void setManager(BMSEmployee manager) {
		this.Manager = manager;
	}

	public List<IPTValue> getIPTValueList() {
		return this.IPTValueList;
	}

	public void setIPTValueList(List<IPTValue> iPTValueList) {
		this.IPTValueList = iPTValueList;
	}

	public int getRealStationID() {
		return this.RealStationID;
	}

	public void setRealStationID(int realStationID) {
		this.RealStationID = realStationID;
	}

	public String getRealStationName() {
		return this.RealStationName;
	}

	public void setRealStationName(String realStationName) {
		this.RealStationName = realStationName;
	}

	public IPTValue getPreCheckValue() {
		return this.PreCheckValue;
	}

	public void setPreCheckValue(IPTValue preCheckValue) {
		this.PreCheckValue = preCheckValue;
	}

	public List<IPTProblemConfirmer> getIPTProblemConfirmerList() {
		return IPTProblemConfirmerList;
	}

	public void setIPTProblemConfirmerList(List<IPTProblemConfirmer> iPTProblemConfirmerList) {
		IPTProblemConfirmerList = iPTProblemConfirmerList;
	}

	public int getConfirmStatus() {
		return ConfirmStatus;
	}

	public void setConfirmStatus(int confirmStatus) {
		ConfirmStatus = confirmStatus;
	}

	public int getStepID() {
		return StepID;
	}

	public void setStepID(int stepID) {
		StepID = stepID;
	}

	public String getStepName() {
		return StepName;
	}

	public void setStepName(String stepName) {
		StepName = stepName;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\ipt\
 * IPTPreCheckProblem.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */