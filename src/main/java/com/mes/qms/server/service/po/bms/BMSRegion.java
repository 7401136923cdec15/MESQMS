package com.mes.qms.server.service.po.bms;

/**
 * 单元格合并
 * 
 * @author YouWang·Peng
 * @CreateTime 2020-7-25 13:59:10
 * @LastEditTime 2020-7-25 13:59:14
 *
 */
public class BMSRegion {
	/**
	 * 起始行
	 */
	public int StartRow = 0;
	/**
	 * 结束行
	 */
	public int EndRow = 0;
	/**
	 * 起始列
	 */
	public int StartCol = 0;
	/**
	 * 结束列
	 */
	public int EndCol = 0;
	
	public BMSRegion() {
	}
	
	public BMSRegion(int startRow, int endRow, int startCol, int endCol) {
		super();
		StartRow = startRow;
		EndRow = endRow;
		StartCol = startCol;
		EndCol = endCol;
	}

	public int getStartRow() {
		return StartRow;
	}

	public void setStartRow(int startRow) {
		StartRow = startRow;
	}

	public int getEndRow() {
		return EndRow;
	}

	public void setEndRow(int endRow) {
		EndRow = endRow;
	}

	public int getStartCol() {
		return StartCol;
	}

	public void setStartCol(int startCol) {
		StartCol = startCol;
	}

	public int getEndCol() {
		return EndCol;
	}

	public void setEndCol(int endCol) {
		EndCol = endCol;
	}
}
