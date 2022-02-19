package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;

/**
 * 流程图工位节点
 */
public class FPCFlowPart implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	public String id = "";
	public String name = "";
	public String left = "";
	public String top = "";
	public String showclass = "";

	public int row = 0;
	public int col = 0;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLeft() {
		return left;
	}

	public String getTop() {
		return top;
	}

	public String getShowclass() {
		return showclass;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public void setTop(String top) {
		this.top = top;
	}

	public void setShowclass(String showclass) {
		this.showclass = showclass;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setCol(int col) {
		this.col = col;
	}
}
