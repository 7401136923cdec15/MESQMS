package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;

/**
 * 工序卡
 */
public class FPCStepSOP implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	public int ID = 0;
	public int RoutePartPointID = 0;
	public String FilePath = "";
	public int FileType = 0;
	public int SourceType = 0;
	public int Active = 0;
	public String FileName = "";

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getRoutePartPointID() {
		return RoutePartPointID;
	}

	public void setRoutePartPointID(int routePartPointID) {
		RoutePartPointID = routePartPointID;
	}

	public String getFilePath() {
		return FilePath;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	public int getFileType() {
		return FileType;
	}

	public void setFileType(int fileType) {
		FileType = fileType;
	}

	public int getSourceType() {
		return SourceType;
	}

	public void setSourceType(int sourceType) {
		SourceType = sourceType;
	}

	public int getActive() {
		return Active;
	}

	public void setActive(int active) {
		Active = active;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}
}
