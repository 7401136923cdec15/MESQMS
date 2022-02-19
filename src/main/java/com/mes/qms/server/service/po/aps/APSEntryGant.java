package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class APSEntryGant implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;
	// [DataMember(Name = "TaskLineList", Order = 1)]
	public List<APSTaskLine> TaskLineList = new ArrayList<>();
	// [DataMember(Name = "GantPartList", Order = 2)]
	public List<APSGantPart> GantPartList = new ArrayList<>();
	// [DataMember(Name = "ColumnList", Order = 3)]
	public List<APSColumn> ColumnList = new ArrayList<>();

	public APSEntryGant() {
		this.TaskLineList = new ArrayList<>();
		this.ColumnList = new ArrayList<>();
		this.GantPartList = new ArrayList<>();
	}
}
