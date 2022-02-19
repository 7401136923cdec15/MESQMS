package com.mes.qms.server.utils.qms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.excel.ExcelLineData;
import com.mes.qms.server.service.po.excel.ExcelSheetData;
import com.mes.qms.server.service.utils.StringUtils;

/**
 * Excel工具类
 * 
 * @author PengYouWang
 * @CreateTime 2020-1-9 15:19:23
 * @LastEditTime 2020-1-9 15:19:26
 *
 */
public class ExcelReader {
	private static Logger logger = LoggerFactory.getLogger(ExcelReader.class);

	public ExcelReader() {
	}

	private static ExcelReader Instance;

	public static ExcelReader getInstance() {
		if (Instance == null)
			Instance = new ExcelReader();
		return Instance;
	}

	private static POIFSFileSystem fs;
	private static Workbook wb;
	private static Sheet sheet;
	private static Row row;

	/**
	 * 获取多个sheetExcel表格数据
	 *
	 * @param fileName Excel 数据表格
	 * @param wSuffix  文件后缀名
	 * @return
	 */
	public ExcelData readMultiSheetExcel(String fileName, String wSuffix) {
		InputStream is = null;
		File file = new File(fileName);
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ExcelData excelData = new ExcelData();
		try {
			try {
				if (wSuffix.equals("xls")) {
					fs = new POIFSFileSystem(is);
					wb = new HSSFWorkbook(fs);
				} else if (wSuffix.equals("xlsx"))
					wb = new XSSFWorkbook(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Integer sheetNum = wb.getNumberOfSheets();
			excelData.setSheetSum(sheetNum);
			excelData.setFileName(file.getName());

			// 循环获取所有sheet数据
			List<ExcelSheetData> sheetDatas = new ArrayList<>();
			for (int i = 0; i < sheetNum; i++) {
				ExcelSheetData sheetData = new ExcelSheetData();
				sheet = wb.getSheetAt(i);
				sheetData.setLineSum(sheet.getPhysicalNumberOfRows());
				sheetData.setSheetName(sheet.getSheetName());

				List<ExcelLineData> lineDatas = readExcelContentBySheet(sheet);
				sheetData.setLineData(lineDatas);
				sheetDatas.add(sheetData);
			}
			excelData.setSheetData(sheetDatas);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return excelData;
	}

	/**
	 * 获取多个sheetExcel表格数据
	 *
	 * @param fileName Excel 数据表格
	 * @param wSuffix  文件后缀名
	 * @return
	 */
	public ServiceResult<ExcelData> readMultiSheetExcel(InputStream wInputStream, String wFileName, String wSuffix,
			int wMaxRow) {
		ServiceResult<ExcelData> excelData = new ServiceResult<ExcelData>();
		excelData.Result = new ExcelData();
		try {
			try {
				if (wSuffix.equals("xls")) {
					fs = new POIFSFileSystem(wInputStream);
					wb = new HSSFWorkbook(fs);
				} else if (wSuffix.equals("xlsx"))
					wb = new XSSFWorkbook(wInputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Integer sheetNum = wb.getNumberOfSheets();
			excelData.Result.setSheetSum(sheetNum);
			excelData.Result.setFileName(wFileName);

			// 循环获取所有sheet数据
			List<ExcelSheetData> sheetDatas = new ArrayList<>();
			for (int i = 0; i < sheetNum; i++) {
				ExcelSheetData sheetData = new ExcelSheetData();
				sheet = wb.getSheetAt(i);
				int wRows = sheet.getPhysicalNumberOfRows();
				if (wRows > wMaxRow) {
					excelData.FaultCode += StringUtils.Format("提示：不能导入超过{0}行的数据!", wMaxRow);
					return excelData;
				}
				sheetData.setLineSum(wRows);
				sheetData.setSheetName(sheet.getSheetName());

				List<ExcelLineData> lineDatas = readExcelContentBySheet(sheet);
				sheetData.setLineData(lineDatas);
				sheetDatas.add(sheetData);
			}
			excelData.Result.setSheetData(sheetDatas);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return excelData;
	}

	private List<ExcelLineData> readExcelContentBySheet(Sheet sheet) {
		List<ExcelLineData> lineDatas = new ArrayList<>();
		try {
			// 得到总行数
			int rowNum = sheet.getLastRowNum();
			for (int i = 0; i <= rowNum; i++) {
				int j = 0;
				row = sheet.getRow(i);
				if (Objects.isNull(row)) {
					continue;
				}

				int colNum = row.getPhysicalNumberOfCells();
				ExcelLineData lineData = new ExcelLineData();
				List<String> colData = new ArrayList<>();
				lineData.setColSum(colNum);
				while (j < colNum) {
					String value = getCellValue(row.getCell((short) j)).trim();
					colData.add(value);
					j++;
				}
				lineData.setColData(colData);
				lineDatas.add(lineData);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return lineDatas;
	}

	/**
	 * 获取单元格数据
	 *
	 * @param cell Excel单元格
	 * @return String 单元格数据内容
	 */
	private String getCellValue(Cell cell) {
		if (Objects.isNull(cell)) {
			return "";
		}

		String value = "";
		try {
			switch (cell.getCellType()) {
			case STRING:
				value = cell.getStringCellValue(); // 如果是字符串则保存
				break;
			case _NONE:
				break;
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					value = sdf.format(DateUtil.getJavaDate(cell.getNumericCellValue())).toString();
					break;
				} else {
					value = String.valueOf(cell.getNumericCellValue());
//					value = new DecimalFormat("0").format(cell.getNumericCellValue());
				}
				break;
			case BOOLEAN:
				value = cell.getBooleanCellValue() + "";
				break;
			case FORMULA:
				value = cell.getCellFormula() + "";
				break;
			case BLANK:
				value = "";
				break;
			case ERROR:
				value = "非法字符";
				break;
			default:
				value = "未知类型";
				break;
			}

//			switch (cell.getCellType()) {
//			case HSSFCell.CELL_TYPE_NUMERIC: // 数字
//				// 如果为时间格式的内容
//				if (HSSFDateUtil.isCellDateFormatted(cell)) {
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString();
//					break;
//				} else {
//					value = new DecimalFormat("0").format(cell.getNumericCellValue());
//				}
//				break;
//			case HSSFCell.CELL_TYPE_STRING: // 字符串
//				value = cell.getStringCellValue();
//				break;
//			case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
//				value = cell.getBooleanCellValue() + "";
//				break;
//			case HSSFCell.CELL_TYPE_FORMULA: // 公式
//				value = cell.getCellFormula() + "";
//				break;
//			case HSSFCell.CELL_TYPE_BLANK: // 空值
//				value = "";
//				break;
//			case HSSFCell.CELL_TYPE_ERROR: // 故障
//				value = "非法字符";
//				break;
//			default:
//				value = "未知类型";
//				break;
//			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return value;
	}
}
