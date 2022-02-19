package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class APSMRP implements Serializable {
	private static final long serialVersionUID = 1L;

	// [DataMember(Name = "ID", Order = 0)]
	public int ID= 0;
	// [DataMember(Name = "BOMID", Order = 1)]
	public int BOMID= 0; // 加工单元ID
	// [DataMember(Name = "BOMNo", Order = 2)]
	public String BOMNo="";
	// [DataMember(Name = "MaterialList", Order = 3)]
	public List<APSMaterial> MaterialList = new ArrayList<>(); // 物料需求
	// [DataMember(Name = "MessageList", Order = 4)]
	public List<APSMessage> MessageList = new ArrayList<>(); // 消息

	public APSMRP() {
		this.ID = 0;
		this.BOMID= 0;
		this.BOMNo = "";
		this.MaterialList = new ArrayList<>();
		this.MessageList = new ArrayList<>();
	}
}
