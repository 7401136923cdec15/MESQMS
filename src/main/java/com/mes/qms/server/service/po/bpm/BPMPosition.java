package com.mes.qms.server.service.po.bpm;

import java.io.Serializable;
import java.util.Calendar;

public class BPMPosition implements Serializable {
	private static final long serialVersionUID = 1L;

	public int ID= 0;

	public String Name= "";

	public String Text= "";

	public Calendar CreateTime = Calendar.getInstance();

	public int CreatorID= 0;

	public int Status= 0; // 状态

	public Calendar AuditTime = Calendar.getInstance();

	public int AuditorID= 0;

	public int WorkShopID= 0; // 车间

	public int LineID= 0; // 产线

	public String WorkShopName= "";

	public String LineName= "";

	public String Auditor= "";

	public String Creator= "";

	public String VersionText= "";// 版本ID（已使用的版本则必须另存为）

	public int FunctionID= 0; // 职能

	public String FunctionName= ""; // 职能文本

	public String StatusText= ""; // 状态

	public int ParentID= 0; // 上级岗位ID

	public String ParentName= "";// 上级岗位名称

	public int ModuleID= 0; // 模块ID

	public String ModuleName= "";// 模块名称

	public BPMPosition() {
		this.Name = "";
		this.Text = "";
		this.WorkShopName = "";
		this.LineName = "";
		this.Auditor = "";
		this.Creator = "";
		this.FunctionName = "";
		this.VersionText = "";
		this.ModuleName = "";
		this.ParentName = "";
		this.CreateTime = Calendar.getInstance();
		this.AuditTime = Calendar.getInstance();
	}

	public BPMPosition Clone() {
		BPMPosition wItem = new BPMPosition();
		wItem.ID = this.ID;
		wItem.CreatorID = this.CreatorID;
		wItem.AuditorID = this.AuditorID;
		wItem.WorkShopID = this.WorkShopID;
		wItem.LineID = this.LineID;
		wItem.FunctionID = this.FunctionID;
		wItem.ModuleID = this.ModuleID;
		wItem.Status = this.Status;
		wItem.Name = this.Name;
		wItem.Text = this.Text;
		wItem.CreateTime = this.CreateTime;
		wItem.AuditTime = this.AuditTime;
		return wItem;
	}
}
