package com.mes.qms.server.service.po.bpm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BPMFunction implements Serializable {
	private static final long serialVersionUID = 1L;
	

	        public int ID= 0;

	        public String Name= "";

	        public String Text= "";

	        public Calendar CreateTime= Calendar.getInstance();

	        public int CreatorID= 0;

	        public int Status= 0;             //状态

	        public Calendar AuditTime= Calendar.getInstance();

	        public int AuditorID= 0;

	        public int WorkShopID= 0;         //车间

	        public int LineID= 0;             //产线

	        public String WorkShopName= "";

	        public String LineName= "";

	        public String Auditor= "";

	        public String Creator= "";

	        public String VersionText= "";      //版本ID（已使用的版本则必须另存为）

	        public int ModuleID= 0;            //职能分类

	        public String ModuleName= "";     //职能文本

	        public String StatusText= "";       //状态

	        public List<BPMEvent> EventList= new ArrayList<>();      //状态
	        
	        public BPMFunction()
	        {
	            this.Name = "";
	            this.Text = "";
	            this.WorkShopName = "";
	            this.LineName = "";
	            this.Auditor = "";
	            this.Creator = "";
	            this.ModuleName = "";
	            this.VersionText = "";
	            this.CreateTime = Calendar.getInstance();
	            this.AuditTime = Calendar.getInstance();
	            this.EventList = new ArrayList<>();     
	        }
	        public BPMFunction Clone()
	        {
	            BPMFunction wItem = new BPMFunction();
	            wItem.ID = this.ID;
	            wItem.CreatorID = this.CreatorID;
	            wItem.AuditorID = this.AuditorID;
	            wItem.WorkShopID = this.WorkShopID;
	            wItem.LineID = this.LineID;
	            wItem.ModuleID = this.ModuleID;
	            wItem.Status = this.Status;
	            wItem.Name = this.Name;
	            wItem.Text = this.Text;
	            wItem.CreateTime = this.CreateTime;
	            wItem.AuditTime = this.AuditTime; 
	            wItem.EventList=new ArrayList<BPMEvent>(this.EventList);
	            return wItem;
	        }
}
