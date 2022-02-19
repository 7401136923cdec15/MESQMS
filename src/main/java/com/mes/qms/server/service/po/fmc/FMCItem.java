package com.mes.qms.server.service.po.fmc;

import com.mes.qms.server.service.po.fmc.FMCItem;
import java.io.Serializable;

public class FMCItem implements Serializable {
	private static final long serialVersionUID = 1L;
	public int ID = 0;

	public String Name = "";

	public FMCItem() {
		this.Name = "";
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int iD) {
		this.ID = iD;
	}

	public String getName() {
		return this.Name;
	}

	public void setName(String name) {
		this.Name = name;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\fmc\FMCItem.
 * class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */