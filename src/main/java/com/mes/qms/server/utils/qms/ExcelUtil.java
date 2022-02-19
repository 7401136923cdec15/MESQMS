package com.mes.qms.server.utils.qms;

import com.mes.qms.server.service.mesenum.BMSEmployeeType;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.bms.BMSClass;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bms.BMSRegion;
import com.mes.qms.server.service.po.bms.BMSWorkareaOrgnization;
import com.mes.qms.server.service.po.excel.MyExcelSheet;
import com.mes.qms.server.service.po.ipt.IPTCheckRecord;
import com.mes.qms.server.service.po.ipt.IPTExport;
import com.mes.qms.server.service.po.ipt.IPTItemExport;
import com.mes.qms.server.service.po.mss.MSSPartItem;
import com.mes.qms.server.service.utils.Configuration;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import com.mes.qms.server.utils.Constants;
import com.mes.qms.server.utils.qms.ExcelUtil;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtil {
	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	static HSSFWorkbook mWorkbook;

	static Sheet mSheet;

	static String[] mHeads;

	public static void CreateFirst(String[] wHeaders, String wSheetName) {
		try {
			mWorkbook = new HSSFWorkbook();

			mSheet = (Sheet) mWorkbook.createSheet(wSheetName);

			CellStyle wStyle1 = Style2();

			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);

			mHeads = wHeaders;

			Cell wCell = null;

			for (int i = 0; i < mHeads.length; i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(mHeads[i]);
				wCell.setCellStyle(wStyle1);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private static CellStyle TitleStyle() {
		HSSFCellStyle wCellStyle = mWorkbook.createCellStyle();
		try {
			wCellStyle.setAlignment(HorizontalAlignment.CENTER);
			wCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			wCellStyle.setWrapText(true);

			HSSFFont wFont = mWorkbook.createFont();
			wFont.setBold(true);
			wFont.setFontName("微软雅黑");
			wFont.setFontHeightInPoints((short) 15);
			wCellStyle.setFont((Font) wFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) wCellStyle;
	}

	private static CellStyle Style2() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle HeadStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();

			hSSFFont.setColor(IndexedColors.WHITE.getIndex());
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle LinkStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();
			hSSFFont.setColor(IndexedColors.BLUE_GREY.getIndex());
			hSSFFont.setUnderline(Font.U_SINGLE);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle StepStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.LEFT);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle ItemStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

//			hSSFCellStyle.setIndention((short) 10);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle NoneStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setBorderTop(BorderStyle.NONE);
			hSSFCellStyle.setBorderBottom(BorderStyle.NONE);
			hSSFCellStyle.setBorderRight(BorderStyle.NONE);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle LeftStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.LEFT);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle ItemStyle_NoBorder() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle ItemStyle_Purple() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();
			hSSFFont.setColor(IndexedColors.VIOLET.getIndex());
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle ItemStyle_Red() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();
			hSSFFont.setColor(IndexedColors.RED.getIndex());
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle ItemStyle_Blue() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();
			hSSFFont.setColor(IndexedColors.BLUE.getIndex());
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle GroupStyle() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.LEFT);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle Style6() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle Style7() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();

			hSSFFont.setColor(IndexedColors.BLACK.getIndex());
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle Style8() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			HSSFFont hSSFFont = mWorkbook.createFont();

			hSSFFont.setColor(IndexedColors.BLACK.getIndex());
			hSSFFont.setBold(true);
			hSSFCellStyle.setFont((Font) hSSFFont);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle Style9() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	private static CellStyle Style10() {
		HSSFCellStyle hSSFCellStyle = mWorkbook.createCellStyle();
		try {
			hSSFCellStyle.setAlignment(HorizontalAlignment.CENTER);
			hSSFCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			hSSFCellStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
			hSSFCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			hSSFCellStyle.setWrapText(true);
			hSSFCellStyle.setBorderLeft(BorderStyle.THIN);
			hSSFCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderTop(BorderStyle.THIN);
			hSSFCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderBottom(BorderStyle.THIN);
			hSSFCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			hSSFCellStyle.setBorderRight(BorderStyle.THIN);
			hSSFCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) hSSFCellStyle;
	}

	public static void CreateOtherRow_Tip() {
		try {
			HSSFCellStyle wStyle = mWorkbook.createCellStyle();
			wStyle.setAlignment(HorizontalAlignment.CENTER);
			wStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			wStyle.setWrapText(true);

			wStyle.setBorderLeft(BorderStyle.THIN);
			wStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setBorderTop(BorderStyle.THIN);
			wStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setBorderRight(BorderStyle.THIN);
			wStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setBorderBottom(BorderStyle.THIN);
			wStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

			List<String> wRow1 = new ArrayList<>(Arrays.asList(new String[] { "1新造\n2检修", "车型编号", "目前只允许5、6", "2位的局段编号",
					"固定值1900", "工位编号", "工序描述", "零部件编号", "物料描述，仅在EXCEL显示，系统不检查", "用量", "用量单位", "必换填1，偶换填2，其他不填",
					"数值≥0并且<100", "委外必修1、委外偶修2、其他不填", "X或空", "X或空", "" }));

			Row wHSSFRow = mSheet.createRow(2);

			for (int i = 0; i < wRow1.size(); i++) {
				Cell wHSSFCell = wHSSFRow.createCell(i);
				wHSSFCell.setCellValue(wRow1.get(i));
				wHSSFCell.setCellStyle((CellStyle) wStyle);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void CreateOtherRow_Orange() {
		try {
			HSSFCellStyle wStyle = mWorkbook.createCellStyle();
			wStyle.setAlignment(HorizontalAlignment.CENTER);
			wStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			wStyle.setWrapText(true);

			wStyle.setBorderLeft(BorderStyle.THIN);
			wStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setBorderTop(BorderStyle.THIN);
			wStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setBorderRight(BorderStyle.THIN);
			wStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setBorderBottom(BorderStyle.THIN);
			wStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			wStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			HSSFCellStyle wStyle1 = mWorkbook.createCellStyle();
			wStyle1.setAlignment(HorizontalAlignment.CENTER);
			wStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
			wStyle1.setWrapText(true);

			wStyle1.setBorderLeft(BorderStyle.THIN);
			wStyle1.setLeftBorderColor(IndexedColors.BLACK.getIndex());

			wStyle1.setBorderTop(BorderStyle.THIN);
			wStyle1.setTopBorderColor(IndexedColors.BLACK.getIndex());

			wStyle1.setBorderRight(BorderStyle.THIN);
			wStyle1.setRightBorderColor(IndexedColors.BLACK.getIndex());

			wStyle1.setBorderBottom(BorderStyle.THIN);
			wStyle1.setBottomBorderColor(IndexedColors.BLACK.getIndex());

			wStyle1.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
			wStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Row wHSSFRow = mSheet.createRow(3);

			Cell wHSSFCell = null;
			for (int i = 0; i < 8; i++) {
				wHSSFCell = wHSSFRow.createCell(i);
				wHSSFCell.setCellValue("必输");
				wHSSFCell.setCellStyle((CellStyle) wStyle);
			}

			wHSSFCell = wHSSFRow.createCell(8);
			wHSSFCell.setCellValue("可选");
			wHSSFCell.setCellStyle((CellStyle) wStyle1);

			wHSSFCell = wHSSFRow.createCell(9);
			wHSSFCell.setCellValue("必输");
			wHSSFCell.setCellStyle((CellStyle) wStyle);

			wHSSFCell = wHSSFRow.createCell(10);
			wHSSFCell.setCellValue("必输");
			wHSSFCell.setCellStyle((CellStyle) wStyle);

			for (int i = 11; i < 17; i++) {
				wHSSFCell = wHSSFRow.createCell(i);
				wHSSFCell.setCellValue("可选");
				wHSSFCell.setCellStyle((CellStyle) wStyle1);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void CreateOtherRow(List<List<String>> wRowList) {
		try {
			if (wRowList == null || wRowList.size() <= 0) {
				return;
			}

			HSSFCellStyle wStyle = mWorkbook.createCellStyle();
			wStyle.setAlignment(HorizontalAlignment.CENTER);
			wStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			wStyle.setWrapText(true);

			wStyle.setBorderLeft(BorderStyle.THIN);
			wStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setBorderTop(BorderStyle.THIN);
			wStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setBorderRight(BorderStyle.THIN);
			wStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());

			wStyle.setBorderBottom(BorderStyle.THIN);
			wStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

			for (int i = 0; i < wRowList.size(); i++) {
				Row wHSSFRow = mSheet.createRow(i + 1);
				for (int j = 0; j < (wRowList.get(i)).size(); j++) {
					Cell wHSSFCell = wHSSFRow.createCell(j);
					wHSSFCell.setCellValue(((List<String>) wRowList.get(i)).get(j));
					wHSSFCell.setCellStyle((CellStyle) wStyle);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 输出数据
	 */
	public static void Export(OutputStream wOutput) {
		try {
			mWorkbook.write(wOutput);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void YJ_CreateTitle(String wTitleName) {
		try {
			mWorkbook = new HSSFWorkbook();
			mItemStyle = ItemStyle();
			mGroupStyle = GroupStyle();

			mSheet = (Sheet) mWorkbook.createSheet("机车预检报告");

			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, 11);
			mSheet.addMergedRegion(wRegion);

			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(wTitleName);
			wCell.setCellStyle(TitleStyle());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void YJ_CreateHeaders(String[] wHeaders) {
		try {
			Row wRow = mSheet.createRow(1);
			wRow.setHeight((short) 500);
			mHeads = wHeaders;

			CellStyle wStyle = HeadStyle();
			for (int i = 0; i < mHeads.length; i++) {
				Cell wCell = wRow.createCell(i);
				wCell.setCellValue(mHeads[i]);
				wCell.setCellStyle(wStyle);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void YJ_CreatePartPoint(String wNumber, String wName, int wRowNum) {
		try {
			Row wRow = mSheet.createRow(wRowNum);
			wRow.setHeight((short) 350);

			CellRangeAddress wRegion = new CellRangeAddress(wRowNum, wRowNum, 1, 11);
			mSheet.addMergedRegion(wRegion);

			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(wNumber);
			wCell.setCellStyle(Style6());

			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);

			wCell = wRow.createCell(1);
			wCell.setCellValue(wName);
			wCell.setCellStyle(mStepStyle);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void YJ_CreateGroup(String wNumber, String wName, int wRowNum) {
		try {
			Row wRow = mSheet.createRow(wRowNum);

			CellRangeAddress wRegion = new CellRangeAddress(wRowNum, wRowNum, 1, 11);
			mSheet.addMergedRegion(wRegion);

			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(wNumber);
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(1);
			wCell.setCellValue(wName);
			wCell.setCellStyle(mGroupStyle);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void YJ_CreatePartPoint(String wNumber, String wName, int wRowNum, int wLength) {
		try {
			Row wRow = mSheet.createRow(wRowNum);
			wRow.setHeight((short) 350);

			CellRangeAddress wRegion = new CellRangeAddress(wRowNum, wRowNum, 1, wLength - 1);
			mSheet.addMergedRegion(wRegion);

			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(wNumber);
			wCell.setCellStyle(Style6());

			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);

			wCell = wRow.createCell(1);
			wCell.setCellValue(wName);
			wCell.setCellStyle(mStepStyle);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void YJ_CreateGroup(String wNumber, String wName, int wRowNum, int wLength) {
		try {
			Row wRow = mSheet.createRow(wRowNum);

			CellRangeAddress wRegion = new CellRangeAddress(wRowNum, wRowNum, 1, wLength - 1);
			mSheet.addMergedRegion(wRegion);

			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(wNumber);
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(1);
			wCell.setCellValue(wName);
			wCell.setCellStyle(mGroupStyle);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static CellStyle mTitleStyle = null;
	public static CellStyle mHeadStyle = null;
	public static CellStyle mStepStyle = null;
	public static CellStyle mGroupStyle = null;
	public static CellStyle mItemStyle = null;
	public static CellStyle mNoneStyle = null;
	public static CellStyle mLinkStyle = null;
	public static CellStyle mStyle7 = null;
	public static CellStyle mStyle8 = null;
	public static CellStyle mStyle9 = null;
	public static CellStyle mStyle10 = null;
	public static CellStyle mLeftStyle = null;

	public static CreationHelper mCreateHelper = null;

	/**
	 * 输出Excel行数据
	 */
	public static void WriteRowItem(List<String> wValueList, int wRowNum) {
		try {
			Row wRow = mSheet.createRow(wRowNum);
			wRow.setHeight((short) 375);

			for (int i = 0; i < wValueList.size(); i++) {
				if (wValueList.get(i).contains(".jpg") || wValueList.get(i).contains(".png")
						|| wValueList.get(i).contains(".jpeg")) {

					String[] wPics = wValueList.get(i).split(",");
					int wIndex = 0;
					for (String wPic : wPics) {

						String wFileName = MESFileUtils.GetFileName(wPic);
						String wSuffix = MESFileUtils.GetSuffiex(wPic);
						String wAddress = StringUtils.Format("picture/{0}.{1}", wFileName, wSuffix);

						Cell wCell = wRow.createCell(i + wIndex);
						wCell.setCellValue("图片" + (wIndex + 1));
						HSSFHyperlink wLink = (HSSFHyperlink) mCreateHelper.createHyperlink(HyperlinkType.FILE);
						wLink.setAddress(wAddress);
						wCell.setHyperlink(wLink);
						wCell.setCellStyle(mLinkStyle);

						wIndex++;
					}
				} else {
					Cell wCell = wRow.createCell(i);
					wCell.setCellValue(wValueList.get(i));
					wCell.setCellStyle(mItemStyle);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void APSBom_CreateHeaders(String wPartNo) {
		try {
			// 1,先创建工作簿
			mWorkbook = new HSSFWorkbook();
			mItemStyle = ItemStyle();
			mStyle7 = Style7();
			mStyle8 = Style8();
			mStyle9 = Style9();
			mStyle10 = Style10();
			// 2.创建sheet文件
			mSheet = mWorkbook.createSheet(wPartNo);
			// 创建第一行
			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);

			CellStyle wStyle7 = mStyle7;
			CellStyle wStyle8 = mStyle8;

			Map<String, Integer> wValueMap = new LinkedHashMap<String, Integer>() {
				/**
				 * 序列号
				 */
				private static final long serialVersionUID = 1L;
				{
					put("BOM类型", 7);
					put("车型", 7);
					put("修程", 7);
					put("局段信息", 7);
					put("工厂", 7);
					put("WBS元素", 7);
					put("工位", 7);
					put("工序描述", 7);
					put("物料编码", 7);
					put("物料名称", 8);
					put("数量", 8);
					put("单位", 8);
					put("必换/偶换", 8);
					put("必修/偶修", 8);
					put("评估类型", 8);
					put("是否互换件", 8);
					put("是否超修程", 8);
					put("领料部门", 8);
					put("仓库号", 8);
					put("质量损失大类", 8);
					put("质量损失小类", 8);
					put("客供料标识", 8);
				}
			};

			int wIndex = 0;
			Cell wCell;
			for (String wKey : wValueMap.keySet()) {
				wCell = wRow.createCell(wIndex++);
				wCell.setCellValue(wKey);
				wCell.setCellStyle(wValueMap.get(wKey) == 7 ? wStyle7 : wStyle8);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void APSBom_CreateTips() {
		try {
			Row wRow = mSheet.createRow(1);

			List<String> wValueList = new ArrayList<>(Arrays.asList(new String[] { "1、新造\r\n2、检修\r\n9、计划外", "车型编号",
					"目前只允许5、6", "2位的局段编号", "固定值1900", "每台车对应的WBS号", "工位编号", "工序描述", "零部件编号", "物料描述，仅在EXCEL显示，系统不检查",
					"用量", "用量单位", "必换-1/偶换-2", "委外必修-1\r\n委外偶修-2\r\n自修必修-3\r\n自修偶修-4\r\n其他不填",
					"必换/偶换件：常规新件，\r\n委外修件：修复旧件，\r\n自修件：可用旧件\r\n互换件：修复旧件", "互换件填X", "超修程填X", "固定值0001",
					"新造1100\r\n检修1200", "报废001\r\n返工返修002\r\n停产003\r\n内部质量收入004",
					"设计差错01\r\n工艺差错02\r\n制造差错03\r\n供方原因04\r\n其他原因05", "客供料填X" }));

			for (int i = 0; i < wValueList.size(); i++) {
				Cell wCell = wRow.createCell(i);
				wCell.setCellValue(wValueList.get(i));
				wCell.setCellStyle(mItemStyle);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void APSBom_CreateOptions() {
		try {
			Row wRow = mSheet.createRow(2);

			String wValue1 = "必输";
			String wValue2 = "可选";

			CellStyle wStyle9 = mStyle9;
			CellStyle wStyle10 = mStyle10;

			int wIndex = 0;
			Cell wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue2);
			wCell.setCellStyle(wStyle10);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue2);
			wCell.setCellStyle(wStyle10);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue2);
			wCell.setCellStyle(wStyle10);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue2);
			wCell.setCellStyle(wStyle10);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue2);
			wCell.setCellStyle(wStyle10);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue2);
			wCell.setCellStyle(wStyle10);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue1);
			wCell.setCellStyle(wStyle9);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue2);
			wCell.setCellStyle(wStyle10);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue2);
			wCell.setCellStyle(wStyle10);

			wCell = wRow.createCell(wIndex++);
			wCell.setCellValue(wValue2);
			wCell.setCellStyle(wStyle10);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void APSBom_CreateItems(List<String> wValueList, int wRowNum) {
		try {
			Row wRow = mSheet.createRow(wRowNum);

			for (int i = 0; i < wValueList.size(); i++) {
				Cell wCell = wRow.createCell(i);
				wCell.setCellValue(wValueList.get(i));
				wCell.setCellStyle(mItemStyle);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public static void YJ_WriteReport(List<IPTCheckRecord> wPreCheckRecordList, List<IPTCheckRecord> wExceptionList,
			List<IPTCheckRecord> wPeriodChangeList, List<IPTCheckRecord> wKeyComponentList, OutputStream wOutput,
			String wTitleName) {
		try {
			mWorkbook = new HSSFWorkbook();

			mTitleStyle = TitleStyle();
			mHeadStyle = HeadStyle();
			mStepStyle = StepStyle();
			mGroupStyle = GroupStyle();
			mItemStyle = ItemStyle();

			YJ_WritePreCheckRecord(wPreCheckRecordList, wTitleName);

			YJ_WriteExceptionRecord(wExceptionList, wTitleName);

			YJ_WritePeriodRecord(wPeriodChangeList, wTitleName);

			YJ_WriteKeyComponetRecord(wKeyComponentList, wTitleName);

			Export(wOutput);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private static void YJ_WritePreCheckRecord(List<IPTCheckRecord> wList, String wTitleName) {
		try {
			String[] wHeaders = { "序号", "检查项点", "质量标准", "图例", "厂家", "型号", "编号", "填写值", "结果", "备注", "替换物料信息", "预检人" };
			String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

			mSheet = (Sheet) mWorkbook.createSheet("1-机车预检记录");

			mSheet.setColumnWidth(1, 2340 * 2);
			mSheet.setColumnWidth(2, 2340 * 3);

			// 冻结窗口
			mSheet.createFreezePane(0, 2, 0, 11);

			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wHeaders.length - 1);
			mSheet.addMergedRegion(wRegion);
			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(StringUtils.Format("{0}-机车预检记录", new Object[] { wTitleName }));
			wCell.setCellStyle(mTitleStyle);

			wRow = mSheet.createRow(1);
			wRow.setHeight((short) 550);
			for (int i = 0; i < wHeaders.length; i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wHeaders[i]);
				wCell.setCellStyle(mHeadStyle);
			}

			int wRowNum = 2;
			for (IPTCheckRecord wIPTCheckRecord : wList) {
				List<String> wValueList;
				switch (wIPTCheckRecord.IsGroupOrStep) {
				case 0:
					wValueList = new ArrayList<>(Arrays.asList(new String[] { wIPTCheckRecord.No,
							wIPTCheckRecord.ItemName, wIPTCheckRecord.Standard,
							StringUtils.isEmpty(wIPTCheckRecord.Legend) ? ""
									: (String.valueOf(wUri) + wIPTCheckRecord.Legend),
							wIPTCheckRecord.PartsFactory, wIPTCheckRecord.PartsModal, wIPTCheckRecord.PartsNumber,
							wIPTCheckRecord.Value, wIPTCheckRecord.CheckResult, wIPTCheckRecord.ResultDescribe,
							wIPTCheckRecord.MaterialInfo, wIPTCheckRecord.Operator }));
					WriteRowItem(wValueList, wRowNum++);
					break;
				case 1:
					YJ_CreateGroup(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				case 2:
					YJ_CreatePartPoint(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private static void YJ_WriteExceptionRecord(List<IPTCheckRecord> wPreCheckRecordList, String wTitleName) {
		try {
			String[] wHeaders = { "序号", "检查项点", "质量标准", "图例", "厂家", "型号", "编号", "填写值", "结果", "描述", "预检人", "替换物料信息",
					"工艺员", "处置意见", "相关部门", "涉及班组人员" };
			String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

			mSheet = (Sheet) mWorkbook.createSheet("2-问题汇总");

			// 冻结窗口
			mSheet.createFreezePane(0, 2, 0, 11);

			mSheet.setColumnWidth(1, 2340 * 2);
			mSheet.setColumnWidth(2, 2340 * 3);

			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wHeaders.length - 1);
			mSheet.addMergedRegion(wRegion);
			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(StringUtils.Format("{0}-预检异常汇总", new Object[] { wTitleName }));
			wCell.setCellStyle(mTitleStyle);

			wRow = mSheet.createRow(1);
			wRow.setHeight((short) 550);
			for (int i = 0; i < wHeaders.length; i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wHeaders[i]);
				wCell.setCellStyle(mHeadStyle);
			}

			int wRowNum = 2;
			for (IPTCheckRecord wIPTCheckRecord : wPreCheckRecordList) {
				List<String> wValueList;
				switch (wIPTCheckRecord.IsGroupOrStep) {
				case 0:
					wValueList = new ArrayList<>(Arrays.asList(new String[] { wIPTCheckRecord.No,
							wIPTCheckRecord.ItemName, wIPTCheckRecord.Standard,
							StringUtils.isEmpty(wIPTCheckRecord.Legend) ? ""
									: (String.valueOf(wUri) + wIPTCheckRecord.Legend),
							wIPTCheckRecord.PartsFactory, wIPTCheckRecord.PartsModal, wIPTCheckRecord.PartsNumber,
							wIPTCheckRecord.Value, wIPTCheckRecord.CheckResult, wIPTCheckRecord.ResultDescribe,
							wIPTCheckRecord.Operator, wIPTCheckRecord.MaterialInfo, wIPTCheckRecord.Crafter,
							wIPTCheckRecord.Opinion, wIPTCheckRecord.RelaDepartments,
							wIPTCheckRecord.RelaClassMembers }));
					WriteRowItem(wValueList, wRowNum++);
					break;
				case 1:
					YJ_CreateGroup(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				case 2:
					YJ_CreatePartPoint(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				}

			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private static void YJ_WritePeriodRecord(List<IPTCheckRecord> wPreCheckRecordList, String wTitleName) {
		try {
			String[] wHeaders = { "序号", "改进项目及特殊要求", "段方要求", "处置措施", "负责人", "涉及单位", "确认情况", "责任班组" };

			mSheet = (Sheet) mWorkbook.createSheet("3-段方要求");

			// 冻结窗口
			mSheet.createFreezePane(0, 2, 0, 11);

			mSheet.setColumnWidth(1, 2340 * 2);
			mSheet.setColumnWidth(2, 2340 * 3);

			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wHeaders.length - 1);
			mSheet.addMergedRegion(wRegion);
			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(StringUtils.Format("{0}-段方要求", new Object[] { wTitleName }));
			wCell.setCellStyle(mTitleStyle);

			wRow = mSheet.createRow(1);
			wRow.setHeight((short) 550);
			for (int i = 0; i < wHeaders.length; i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wHeaders[i]);
				wCell.setCellStyle(mHeadStyle);
			}

			int wRowNum = 2;
			for (IPTCheckRecord wIPTCheckRecord : wPreCheckRecordList) {
				List<String> wValueList;
				switch (wIPTCheckRecord.IsGroupOrStep) {
				case 0:
					wValueList = new ArrayList<>(Arrays.asList(new String[] { wIPTCheckRecord.No,
							wIPTCheckRecord.SpecialRequirement, wIPTCheckRecord.PeriodRequirement,
							wIPTCheckRecord.Standard, wIPTCheckRecord.Operator, wIPTCheckRecord.RelaDepartments,
							wIPTCheckRecord.Confirmation, wIPTCheckRecord.RelaClassMembers }));
					WriteRowItem(wValueList, wRowNum++);
					break;
				case 1:
					YJ_CreateGroup(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				case 2:
					YJ_CreatePartPoint(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private static void YJ_WriteKeyComponetRecord(List<IPTCheckRecord> wPreCheckRecordList, String wTitleName) {
		try {
			String[] wHeaders = { "序号", "部件", "厂家", "型号", "编号", "预检人", "结果", "描述" };

			mSheet = (Sheet) mWorkbook.createSheet("4-关键部件入场检查");

			// 冻结窗口
			mSheet.createFreezePane(0, 2, 0, 11);

			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wHeaders.length - 1);
			mSheet.addMergedRegion(wRegion);
			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(StringUtils.Format("{0}-关键部件入场检查", new Object[] { wTitleName }));
			wCell.setCellStyle(mTitleStyle);

			wRow = mSheet.createRow(1);
			wRow.setHeight((short) 550);
			for (int i = 0; i < wHeaders.length; i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wHeaders[i]);
				wCell.setCellStyle(mHeadStyle);
			}

			int wRowNum = 2;
			for (IPTCheckRecord wIPTCheckRecord : wPreCheckRecordList) {
				List<String> wValueList;
				switch (wIPTCheckRecord.IsGroupOrStep) {
				case 0:
					wValueList = new ArrayList<>(Arrays.asList(
							new String[] { wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wIPTCheckRecord.PartsFactory,
									wIPTCheckRecord.PartsModal, wIPTCheckRecord.PartsNumber, wIPTCheckRecord.Operator,
									wIPTCheckRecord.CheckResult, wIPTCheckRecord.ResultDescribe }));
					WriteRowItem(wValueList, wRowNum++);
					break;
				case 1:
					YJ_CreateGroup(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				case 2:
					YJ_CreatePartPoint(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				}

			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 生产过程、质量过程检验记录导出
	 * 
	 * @param wRecordMap 数据映射
	 * @param wOutput    输出流
	 * @param wTitleName 文件名称(车号+局段)
	 */
	public static void SCGC_WriteReport(Map<String, List<IPTCheckRecord>> wRecordMap, OutputStream wOutput,
			String wTitleName, boolean wIsOut, String wTitle) {
		try {
			if (wRecordMap == null || wRecordMap.size() <= 0) {
				return;
			}

			mWorkbook = new HSSFWorkbook();

			mTitleStyle = TitleStyle();
			mHeadStyle = HeadStyle();
			mStepStyle = StepStyle();
			mGroupStyle = GroupStyle();
			mItemStyle = ItemStyle();
			mLinkStyle = LinkStyle();

			mCreateHelper = mWorkbook.getCreationHelper();

			int wSheet = 1;
			for (String wStationName : wRecordMap.keySet()) {
				List<IPTCheckRecord> wRecordList = wRecordMap.get(wStationName);
				SCGC_WriteRecord(wSheet + "-" + wStationName, wRecordList, wTitleName, wIsOut, wTitle);
				wSheet++;
			}

			Export(wOutput);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private static void SCGC_WriteRecord(String wStationName, List<IPTCheckRecord> wRecordList, String wTitleName,
			boolean wIsOut, String wTitle) {
		try {
			String[] wHeaders = { "序号", "作业顺序", "作业内容", "要求、事项", "图例", "厂家", "型号", "编号", "填写值", "结果", "备注", "自检人",
					"互检人", "专检人", "检验日期" };
			if (wIsOut) {
				wHeaders = new String[] { "序号", "作业顺序", "作业内容", "要求、事项", "图例", "厂家", "型号", "编号", "填写值", "结果", "备注",
						"自检人", "检验日期" };
			}
			String wUri = Configuration.readConfigString("excel.image.uri", "config/config");

			mSheet = (Sheet) mWorkbook.createSheet(wStationName);

			mSheet.setColumnWidth(1, 2340 * 2);
			mSheet.setColumnWidth(2, 2340 * 3);

			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wHeaders.length - 1);
			mSheet.addMergedRegion(wRegion);

			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(StringUtils.Format("{0}-{1}-{2}", new Object[] { wTitleName, wTitle, wStationName }));
			wCell.setCellStyle(mTitleStyle);

			wRow = mSheet.createRow(1);
			wRow.setHeight((short) 550);
			for (int i = 0; i < wHeaders.length; i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wHeaders[i]);
				wCell.setCellStyle(mHeadStyle);
			}

			int wRowNum = 2;
			for (IPTCheckRecord wIPTCheckRecord : wRecordList) {
				List<String> wValueList;
				switch (wIPTCheckRecord.IsGroupOrStep) {
				case 0:
					wValueList = new ArrayList<>(Arrays.asList(new String[] { wIPTCheckRecord.No,
							wIPTCheckRecord.ItemName, wIPTCheckRecord.WorkContent, wIPTCheckRecord.Standard,
							StringUtils.isEmpty(wIPTCheckRecord.Legend) ? ""
									: (String.valueOf(wUri) + wIPTCheckRecord.Legend),
							wIPTCheckRecord.PartsFactory, wIPTCheckRecord.PartsModal, wIPTCheckRecord.PartsNumber,
							wIPTCheckRecord.Value, wIPTCheckRecord.CheckResult, wIPTCheckRecord.ResultDescribe,
							wIPTCheckRecord.Operator, wIPTCheckRecord.Mutualer, wIPTCheckRecord.Speciler,
							wIPTCheckRecord.CheckDate, wIPTCheckRecord.Picture }));
					if (wIsOut) {
						wValueList = new ArrayList<>(Arrays.asList(new String[] { wIPTCheckRecord.No,
								wIPTCheckRecord.ItemName, wIPTCheckRecord.WorkContent, wIPTCheckRecord.Standard,
								StringUtils.isEmpty(wIPTCheckRecord.Legend) ? ""
										: (String.valueOf(wUri) + wIPTCheckRecord.Legend),
								wIPTCheckRecord.PartsFactory, wIPTCheckRecord.PartsModal, wIPTCheckRecord.PartsNumber,
								wIPTCheckRecord.Value, wIPTCheckRecord.CheckResult, wIPTCheckRecord.ResultDescribe,
								wIPTCheckRecord.Operator, wIPTCheckRecord.CheckDate, wIPTCheckRecord.Picture }));
					}
					WriteRowItem(wValueList, wRowNum++);
					break;
				case 1:
					YJ_CreateGroup(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				case 2:
					YJ_CreatePartPoint(wIPTCheckRecord.No, wIPTCheckRecord.ItemName, wRowNum++, wHeaders.length);
					break;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 绘制工序项点模板
	 */
	public static void SCGC_WriteModel(List<List<String>> wSourceList, FileOutputStream wFileOutputStream,
			FileInputStream wInputStream) {
		try {
			if (wSourceList == null || wSourceList.size() <= 0) {
				return;
			}

			XSSFWorkbook wWorkbook = new XSSFWorkbook(wInputStream);

			mItemStyle = ItemStyle(wWorkbook);

			mSheet = (Sheet) wWorkbook.getSheet("导入模板");

			Row wRow = null;
			Cell wCell = null;

			for (int i = 1; i < wSourceList.size(); i++) {
				wRow = mSheet.createRow(i);
				for (int j = 0; j < wSourceList.get(i).size(); j++) {
					wCell = wRow.createCell(j);
					wCell.setCellValue(wSourceList.get(i).get(j));
					wCell.setCellStyle(mItemStyle);
				}
			}

			wWorkbook.write(wFileOutputStream);

			wWorkbook.close();
			wInputStream.close();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private static CellStyle ItemStyle(XSSFWorkbook wWorkbook) {
		XSSFCellStyle wStyle = wWorkbook.createCellStyle();
		try {
			wStyle.setAlignment(HorizontalAlignment.CENTER);
			wStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			wStyle.setWrapText(true);
			wStyle.setBorderLeft(BorderStyle.THIN);
			wStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			wStyle.setBorderTop(BorderStyle.THIN);
			wStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
			wStyle.setBorderBottom(BorderStyle.THIN);
			wStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			wStyle.setBorderRight(BorderStyle.THIN);
			wStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return (CellStyle) wStyle;
	}

	public static CellStyle mItemStyle_Purple;
	public static CellStyle mItemStyle_Red;
	public static CellStyle mItemStyle_Blue;
	public static CellStyle mItemStyle_NoBorder;

	/**
	 * 绘制组织架构Excel
	 */
	public static void BMS_WriteOrgnization(List<BMSWorkareaOrgnization> wSourceList,
			FileOutputStream wFileOutputStream) {
		try {
			if (wSourceList == null || wSourceList.size() <= 0) {
				return;
			}

			List<BMSWorkareaOrgnization> wFirstList = new ArrayList<BMSWorkareaOrgnization>();
			for (int i = 0; i < 4; i++) {
				wFirstList.add(wSourceList.get(i));
			}
			// ①工区总数、正式总数、实习总数、劳务返聘总数、班组总数、在岗总数
			int wF1 = 0;
			int wF2 = 0;
			int wF3 = 0;
			int wF4 = 0;
			int wF5 = 0;
			int wF6 = 0;
			for (BMSWorkareaOrgnization wBMSWorkareaOrgnization : wFirstList) {
				wF1 += wBMSWorkareaOrgnization.FQTYWorkarea;
				for (BMSClass wBMSClass : wBMSWorkareaOrgnization.ClassList) {
					wF2 += wBMSClass.FQTYRegularWorkers;
					wF3 += wBMSClass.FQTYIntern;
					wF4 += wBMSClass.FQTYReemployment;
					wF5 += wBMSClass.FQTYClass;
					wF6 += wBMSClass.FQTYOnTheJob;
				}
			}
			List<BMSWorkareaOrgnization> wTwoList = new ArrayList<BMSWorkareaOrgnization>();
			for (int i = 4; i < 5; i++) {
				wTwoList.add(wSourceList.get(i));
			}
			int wT1 = 0;
			int wT2 = 0;
			int wT3 = 0;
			int wT4 = 0;
			int wT5 = 0;
			int wT6 = 0;
			wT1 += wF1;
			wT2 += wF2;
			wT3 += wF3;
			wT4 += wF4;
			wT5 += wF5;
			wT6 += wF6;
			for (BMSWorkareaOrgnization wBMSWorkareaOrgnization : wTwoList) {
				wT1 += wBMSWorkareaOrgnization.FQTYWorkarea;
				for (BMSClass wBMSClass : wBMSWorkareaOrgnization.ClassList) {
					wT2 += wBMSClass.FQTYRegularWorkers;
					wT3 += wBMSClass.FQTYIntern;
					wT4 += wBMSClass.FQTYReemployment;
					wT5 += wBMSClass.FQTYClass;
					wT6 += wBMSClass.FQTYOnTheJob;
				}
			}
			// ①工区总数、正式总数、实习总数、劳务返聘总数、班组总数、在岗总数
			List<BMSWorkareaOrgnization> wThreeList = new ArrayList<BMSWorkareaOrgnization>();
			for (int i = 5; i < 6; i++) {
				wThreeList.add(wSourceList.get(i));
			}
			// ①工区总数、正式总数、实习总数、劳务返聘总数、班组总数、在岗总数
			int wTh1 = 0;
			int wTh2 = 0;
			int wTh3 = 0;
			int wTh4 = 0;
			int wTh5 = 0;
			int wTh6 = 0;
			wTh1 += wT1;
			wTh2 += wT2;
			wTh3 += wT3;
			wTh4 += wT4;
			wTh5 += wT5;
			wTh6 += wT6;
			for (BMSWorkareaOrgnization wBMSWorkareaOrgnization : wThreeList) {
				wTh1 += wBMSWorkareaOrgnization.FQTYWorkarea;
				for (BMSClass wBMSClass : wBMSWorkareaOrgnization.ClassList) {
					wTh2 += wBMSClass.FQTYRegularWorkers;
					wTh3 += wBMSClass.FQTYIntern;
					wTh4 += wBMSClass.FQTYReemployment;
					wTh5 += wBMSClass.FQTYClass;
					wTh6 += wBMSClass.FQTYOnTheJob;
				}
			}

			mWorkbook = new HSSFWorkbook();

			mTitleStyle = TitleStyle();
			mItemStyle = ItemStyle();
			mItemStyle_Purple = ItemStyle_Purple();
			mItemStyle_Red = ItemStyle_Red();
			mItemStyle_Blue = ItemStyle_Blue();
			mItemStyle_NoBorder = ItemStyle_NoBorder();

			String[] wHeaders = { "序号", "工区", "班组名称", "工区人数", "正式", "实习", "劳务反聘", "班组人数", "在岗", "班组负责工位", "1", "2", "3",
					"4", "5", "6", "7", "8", "9", "10" };

			mSheet = (Sheet) mWorkbook.createSheet("组织架构");

			// 设置列宽
			SetColumnWidthToA4(wHeaders.length);

			// 标题
			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wHeaders.length - 1);
			mSheet.addMergedRegion(wRegion);

			// 合并单元格
			List<BMSRegion> wBMSRegionList = GetBMSRegionList(wFirstList, 1);
			for (BMSRegion wBMSRegion : wBMSRegionList) {
				wRegion = new CellRangeAddress(wBMSRegion.StartRow, wBMSRegion.EndRow, wBMSRegion.StartCol,
						wBMSRegion.EndCol);
				mSheet.addMergedRegion(wRegion);
			}

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy年MM月");
			String wTimeStr = wSDF.format(Calendar.getInstance().getTime());

			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(StringUtils.Format("制造中心员工组织架构（{0}）", wTimeStr));
			wCell.setCellStyle(mTitleStyle);
			// 表头
			wRow = mSheet.createRow(1);
			wRow.setHeight((short) 550);
			for (int i = 0; i < wHeaders.length; i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wHeaders[i]);
				wCell.setCellStyle(mItemStyle);
			}
			// 内容
			List<List<String>> wRowValueList = GetRowValueList(wFirstList);

			int wRowNum = 2;
			for (List<String> wValueList : wRowValueList) {
				wRow = mSheet.createRow(wRowNum++);
				for (int i = 0; i < wValueList.size(); i++) {
					wCell = wRow.createCell(i);

					if (wValueList.get(i).contains(":Red")) {
						wCell.setCellValue(wValueList.get(i).replace(":Red", ""));
						wCell.setCellStyle(mItemStyle_Red);
					} else if (wValueList.get(i).contains(":Blue")) {
						wCell.setCellValue(wValueList.get(i).replace(":Blue", ""));
						wCell.setCellStyle(mItemStyle_Blue);
					} else if (wValueList.get(i).contains(":Purple")) {
						wCell.setCellValue(wValueList.get(i).replace(":Purple", ""));
						wCell.setCellStyle(mItemStyle_Purple);
					} else {
						wCell.setCellValue(wValueList.get(i));
						wCell.setCellStyle(mItemStyle);
					}
				}
			}

			// ①工区小计
			wRegion = new CellRangeAddress(wRowNum, wRowNum, 0, 2);
			mSheet.addMergedRegion(wRegion);
			wRow = mSheet.createRow(wRowNum++);
			wCell = wRow.createCell(0);
			wCell.setCellValue("小计");
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(3);
			wCell.setCellValue(String.valueOf(wF1));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(4);
			wCell.setCellValue(String.valueOf(wF2));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(5);
			wCell.setCellValue(String.valueOf(wF3));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(6);
			wCell.setCellValue(String.valueOf(wF4));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(7);
			wCell.setCellValue(String.valueOf(wF5));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(8);
			wCell.setCellValue(String.valueOf(wF6));
			wCell.setCellStyle(mItemStyle_NoBorder);
			// ②技能员工
			wBMSRegionList = GetBMSRegionList(wTwoList, wRowNum - 1);
			for (BMSRegion wBMSRegion : wBMSRegionList) {
				wRegion = new CellRangeAddress(wBMSRegion.StartRow, wBMSRegion.EndRow, wBMSRegion.StartCol,
						wBMSRegion.EndCol);
				mSheet.addMergedRegion(wRegion);
			}

			wRowValueList = GetRowValueList(wTwoList);
			for (List<String> wValueList : wRowValueList) {
				wRow = mSheet.createRow(wRowNum++);
				for (int i = 0; i < wValueList.size(); i++) {
					wCell = wRow.createCell(i);

					if (wValueList.get(i).contains(":Red")) {
						wCell.setCellValue(wValueList.get(i).replace(":Red", ""));
						wCell.setCellStyle(mItemStyle_Red);
					} else if (wValueList.get(i).contains(":Blue")) {
						wCell.setCellValue(wValueList.get(i).replace(":Blue", ""));
						wCell.setCellStyle(mItemStyle_Blue);
					} else if (wValueList.get(i).contains(":Purple")) {
						wCell.setCellValue(wValueList.get(i).replace(":Purple", ""));
						wCell.setCellStyle(mItemStyle_Purple);
					} else {
						wCell.setCellValue(wValueList.get(i));
						wCell.setCellStyle(mItemStyle);
					}
				}
			}
			// ③技能员工小计
			wRegion = new CellRangeAddress(wRowNum, wRowNum, 0, 1);
			mSheet.addMergedRegion(wRegion);
			wRow = mSheet.createRow(wRowNum++);

			wCell = wRow.createCell(0);
			wCell.setCellValue("小计");
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(2);
			wCell.setCellValue("技能员工");
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(3);
			wCell.setCellValue(String.valueOf(wT1));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(4);
			wCell.setCellValue(String.valueOf(wT2));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(5);
			wCell.setCellValue(String.valueOf(wT3));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(6);
			wCell.setCellValue(String.valueOf(wT4));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(7);
			wCell.setCellValue(String.valueOf(wT5));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(8);
			wCell.setCellValue(String.valueOf(wT6));
			wCell.setCellStyle(mItemStyle_NoBorder);
			// ④管理人员
			wBMSRegionList = GetBMSRegionList(wThreeList, wRowNum - 1);
			for (BMSRegion wBMSRegion : wBMSRegionList) {
				wRegion = new CellRangeAddress(wBMSRegion.StartRow, wBMSRegion.EndRow, wBMSRegion.StartCol,
						wBMSRegion.EndCol);
				mSheet.addMergedRegion(wRegion);
			}

			wRowValueList = GetRowValueList(wThreeList);
			for (List<String> wValueList : wRowValueList) {
				wRow = mSheet.createRow(wRowNum++);
				for (int i = 0; i < wValueList.size(); i++) {
					wCell = wRow.createCell(i);

					if (wValueList.get(i).contains(":Red")) {
						wCell.setCellValue(wValueList.get(i).replace(":Red", ""));
						wCell.setCellStyle(mItemStyle_Red);
					} else if (wValueList.get(i).contains(":Blue")) {
						wCell.setCellValue(wValueList.get(i).replace(":Blue", ""));
						wCell.setCellStyle(mItemStyle_Blue);
					} else if (wValueList.get(i).contains(":Purple")) {
						wCell.setCellValue(wValueList.get(i).replace(":Purple", ""));
						wCell.setCellStyle(mItemStyle_Purple);
					} else {
						wCell.setCellValue(wValueList.get(i));
						wCell.setCellStyle(mItemStyle);
					}
				}
			}
			// ⑤管理人员小计
			wRegion = new CellRangeAddress(wRowNum, wRowNum, 0, 2);
			mSheet.addMergedRegion(wRegion);
			wRow = mSheet.createRow(wRowNum++);

			wCell = wRow.createCell(0);
			wCell.setCellValue("制造中心总人数：");
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(3);
			wCell.setCellValue(String.valueOf(wTh1));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(4);
			wCell.setCellValue(String.valueOf(wTh2));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(5);
			wCell.setCellValue(String.valueOf(wTh3));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(6);
			wCell.setCellValue(String.valueOf(wTh4));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(7);
			wCell.setCellValue(String.valueOf(wTh5));
			wCell.setCellStyle(mItemStyle_NoBorder);

			wCell = wRow.createCell(8);
			wCell.setCellValue(String.valueOf(wTh6));
			wCell.setCellStyle(mItemStyle_NoBorder);
			// 输出
			Export(wFileOutputStream);
		} catch (Exception ex) {
			logger.error(ex.toString());
			ex.printStackTrace();
		}
	}

	/**
	 * 设置Excel列的宽度适应A4纸张
	 */
	private static void SetColumnWidthToA4(int wLength) {
		try {
			for (int i = 0; i < wLength; i++) {
				mSheet.setColumnWidth(i, 2340);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 设置Excel列的宽度适应A4纸张
	 */
	private static void SetColumnWidth(int wLength, int wWidth) {
		try {
			for (int i = 0; i < wLength; i++) {
				mSheet.setColumnWidth(i, wWidth);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取合并单元格数据
	 */
	private static List<BMSRegion> GetBMSRegionList(List<BMSWorkareaOrgnization> wSourceList, int wAreaRow) {
		List<BMSRegion> wResult = new ArrayList<BMSRegion>();
		try {
			if (wSourceList == null || wSourceList.size() <= 0) {
				return wResult;
			}

			BMSRegion wBMSRegion;
			for (BMSWorkareaOrgnization wBMSWorkareaOrgnization : wSourceList) {
				int wSRow = wAreaRow + 1;
				for (BMSClass wBMSClass : wBMSWorkareaOrgnization.ClassList) {
					int wSCRow = wAreaRow + 1;
					int wRow = GetRows(wBMSClass.TeamMembers);
					wAreaRow += wRow;
					if (wRow == 1 || wRow == 0) {
						continue;
					}
					// 序号
					wBMSRegion = new BMSRegion(wSCRow, wAreaRow, 0, 0);
					wResult.add(wBMSRegion);
					// 班组名称
					wBMSRegion = new BMSRegion(wSCRow, wAreaRow, 2, 2);
					wResult.add(wBMSRegion);
					// 正式
					wBMSRegion = new BMSRegion(wSCRow, wAreaRow, 4, 4);
					wResult.add(wBMSRegion);
					// 实习
					wBMSRegion = new BMSRegion(wSCRow, wAreaRow, 5, 5);
					wResult.add(wBMSRegion);
					// 劳务反聘
					wBMSRegion = new BMSRegion(wSCRow, wAreaRow, 6, 6);
					wResult.add(wBMSRegion);
					// 班组人数
					wBMSRegion = new BMSRegion(wSCRow, wAreaRow, 7, 7);
					wResult.add(wBMSRegion);
					// 在岗
					wBMSRegion = new BMSRegion(wSCRow, wAreaRow, 8, 8);
					wResult.add(wBMSRegion);
					// 班组负责工位
					wBMSRegion = new BMSRegion(wSCRow, wAreaRow, 9, 9);
					wResult.add(wBMSRegion);
				}
				// 工区
				wBMSRegion = new BMSRegion(wSRow, wAreaRow, 1, 1);
				wResult.add(wBMSRegion);
				// 工区人数
				wBMSRegion = new BMSRegion(wSRow, wAreaRow, 3, 3);
				wResult.add(wBMSRegion);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 将组织架构结构转换为Excel行数据
	 */
	private static List<List<String>> GetRowValueList(List<BMSWorkareaOrgnization> wSourceList) {
		List<List<String>> wResult = new ArrayList<List<String>>();
		try {
			int wNumber = 1;
			for (BMSWorkareaOrgnization wBMSWorkareaOrgnization : wSourceList) {
				int wArea = 1;
				for (BMSClass wBMSClass : wBMSWorkareaOrgnization.ClassList) {

					int wRow = GetRows(wBMSClass.TeamMembers);

					for (int i = 1; i <= wRow; i++) {
						List<String> wValueList = new ArrayList<String>();
						if (i == 1) {
							// 序号
							wValueList.add(String.valueOf(wNumber));
							// 工区
							wValueList.add(wArea == 1 ? wBMSWorkareaOrgnization.WorkareaName : "");
							// 班组名称
							wValueList.add(wBMSClass.ClassName);
							// 工区人数
							wValueList
									.add(wArea == 1
											? wBMSWorkareaOrgnization.FQTYWorkarea > 0
													? String.valueOf(wBMSWorkareaOrgnization.FQTYWorkarea)
													: ""
											: "");
							// 正式
							wValueList
									.add(wBMSClass.FQTYRegularWorkers > 0 ? String.valueOf(wBMSClass.FQTYRegularWorkers)
											: "");
							// 实习
							wValueList.add(wBMSClass.FQTYIntern > 0 ? String.valueOf(wBMSClass.FQTYIntern) : "");
							// 劳务反聘
							wValueList.add(
									wBMSClass.FQTYReemployment > 0 ? String.valueOf(wBMSClass.FQTYReemployment) : "");
							// 班组人数
							wValueList.add(wBMSClass.FQTYClass > 0 ? String.valueOf(wBMSClass.FQTYClass) : "");
							// 在岗
							wValueList.add(wBMSClass.FQTYOnTheJob > 0 ? String.valueOf(wBMSClass.FQTYOnTheJob) : "");
							// 班组负责工位
							wValueList.add(wBMSClass.ResponsibleStations);
							// 1
							if (wBMSClass.TeamMembers.size() > 0) {
								if (wBMSClass.TeamMembers.get(0).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(0).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(0).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(0).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(0).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(0).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(0).Name);
								}
							} else {
								wValueList.add("");
							}
							// 2
							if (wBMSClass.TeamMembers.size() > 1) {
								if (wBMSClass.TeamMembers.get(1).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(1).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(1).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(1).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(1).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(1).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(1).Name);
								}
							} else {
								wValueList.add("");
							}
							// 3
							if (wBMSClass.TeamMembers.size() > 2) {
								if (wBMSClass.TeamMembers.get(2).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(2).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(2).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(2).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(2).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(2).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(2).Name);
								}
							} else {
								wValueList.add("");
							}
							// 4
							if (wBMSClass.TeamMembers.size() > 3) {
								if (wBMSClass.TeamMembers.get(3).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(3).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(3).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(3).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(3).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(3).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(3).Name);
								}
							} else {
								wValueList.add("");
							}
							// 5
							if (wBMSClass.TeamMembers.size() > 4) {
								if (wBMSClass.TeamMembers.get(4).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(4).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(4).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(4).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(4).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(4).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(4).Name);
								}
							} else {
								wValueList.add("");
							}
							// 6
							if (wBMSClass.TeamMembers.size() > 5) {
								if (wBMSClass.TeamMembers.get(5).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(5).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(5).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(5).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(5).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(5).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(5).Name);
								}
							} else {
								wValueList.add("");
							}
							// 7
							if (wBMSClass.TeamMembers.size() > 6) {
								if (wBMSClass.TeamMembers.get(6).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(6).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(6).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(6).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(6).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(6).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(6).Name);
								}
							} else {
								wValueList.add("");
							}
							// 8
							if (wBMSClass.TeamMembers.size() > 7) {
								if (wBMSClass.TeamMembers.get(7).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(7).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(7).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(7).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(7).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(7).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(7).Name);
								}
							} else {
								wValueList.add("");
							}
							// 9
							if (wBMSClass.TeamMembers.size() > 8) {
								if (wBMSClass.TeamMembers.get(8).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(8).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(8).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(8).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(8).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(8).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(8).Name);
								}
							} else {
								wValueList.add("");
							}
							// 10
							if (wBMSClass.TeamMembers.size() > 9) {
								if (wBMSClass.TeamMembers.get(9).Type == BMSEmployeeType.Interns.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(9).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get(9).Type == BMSEmployeeType.OutSource.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(9).Name + ":Blue");
								} else if (wBMSClass.TeamMembers.get(9).Type == BMSEmployeeType.RaRetirement
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get(9).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get(9).Name);
								}
							} else {
								wValueList.add("");
							}
						} else {
							// 序号
							wValueList.add("");
							// 工区
							wValueList.add("");
							// 班组名称
							wValueList.add("");
							// 工区人数
							wValueList.add("");
							// 正式
							wValueList.add("");
							// 实习
							wValueList.add("");
							// 劳务反聘
							wValueList.add("");
							// 班组人数
							wValueList.add("");
							// 在岗
							wValueList.add("");
							// 班组负责工位
							wValueList.add("");
							// 1
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 0) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 0).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 0).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 0).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 0).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 0).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 0).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 0).Name);
								}
							} else {
								wValueList.add("");
							}
							// 2
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 1) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 1).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 1).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 1).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 1).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 1).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 1).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 1).Name);
								}
							} else {
								wValueList.add("");
							}
							// 3
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 2) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 2).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 2).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 2).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 2).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 2).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 2).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 2).Name);
								}
							} else {
								wValueList.add("");
							}
							// 4
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 3) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 3).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 3).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 3).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 3).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 3).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 3).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 3).Name);
								}
							} else {
								wValueList.add("");
							}
							// 5
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 4) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 4).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 4).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 4).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 4).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 4).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 4).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 4).Name);
								}
							} else {
								wValueList.add("");
							}
							// 6
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 5) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 5).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 5).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 5).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 5).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 5).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 5).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 5).Name);
								}
							} else {
								wValueList.add("");
							}
							// 7
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 6) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 6).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 6).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 6).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 6).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 6).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 6).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 6).Name);
								}
							} else {
								wValueList.add("");
							}
							// 8
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 7) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 7).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 7).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 7).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 7).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 7).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 7).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 7).Name);
								}
							} else {
								wValueList.add("");
							}
							// 9
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 8) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 8).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 8).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 8).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 8).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 8).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 8).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 8).Name);
								}
							} else {
								wValueList.add("");
							}
							// 10
							if (wBMSClass.TeamMembers.size() > (i - 1) * 10 + 9) {
								if (wBMSClass.TeamMembers.get((i - 1) * 10 + 9).Type == BMSEmployeeType.Interns
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 9).Name + ":Red");
								} else if (wBMSClass.TeamMembers.get((i - 1) * 10 + 9).Type == BMSEmployeeType.OutSource
										.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 9).Name + ":Blue");
								} else if (wBMSClass.TeamMembers
										.get((i - 1) * 10 + 9).Type == BMSEmployeeType.RaRetirement.getValue()) {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 9).Name + ":Purple");
								} else {
									wValueList.add(wBMSClass.TeamMembers.get((i - 1) * 10 + 9).Name);
								}
							} else {
								wValueList.add("");
							}
						}
						wResult.add(wValueList);
					}

					wNumber++;
					wArea++;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据人数获取行数
	 */
	private static int GetRows(List<BMSEmployee> wEmployeeList) {
		int wResult = 0;
		try {
			if (wEmployeeList == null || wEmployeeList.size() <= 0) {
				return wResult;
			}

			wResult = wEmployeeList.size() % 10 == 0 ? wEmployeeList.size() / 10 : wEmployeeList.size() / 10 + 1;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 导出过程检验项点Excel-工位
	 */
	public static void IPT_WriteStepItems(List<IPTExport> wDataList, FileOutputStream wFileOutputStream) {
		try {
			// ①初始化工作蒲
			mWorkbook = new HSSFWorkbook();
			// ③初始化样式
			mTitleStyle = TitleStyle();
			mItemStyle = ItemStyle();
			mLeftStyle = LeftStyle();
			mNoneStyle = NoneStyle();
			// ②添加工序sheet
			for (IPTExport wData : wDataList) {
				AddStepSheet(wData);
			}
			// ⑤输出
			Export(wFileOutputStream);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private static void AddStepSheet(IPTExport wData) {
		try {
			// ②初始化工作表
			wData.StepName = wData.StepName.replace("/", "-");
			mSheet = (Sheet) mWorkbook.createSheet(wData.StepName);
			mSheet.protectSheet(UUID.randomUUID().toString());

			// ①设置打印方向为横向
			PrintSetup wPrintSetup = mSheet.getPrintSetup();
			wPrintSetup.setLandscape(true);

			// ①绘制标题
			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, 12);
			mSheet.addMergedRegion(wRegion);

			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(StringUtils.Format("{0} 型机车 {1}修  {2}工位  {3}", wData.ProductNo, wData.LineName,
					wData.PartName, wData.StepName));
			wCell.setCellStyle(mTitleStyle);
			// ①绘制第二行
			IPT_WriteRow2(wData);
			// ②绘制第三行
			IPT_WriteRow3(wData);
			// ③绘制第四行
			IPT_WriteRow4(wData);
			// ④绘制第五行
			IPT_WriteRow5(wData);
			// ⑤绘制第六行
			IPT_WriteRow6(wData);
			// ⑥绘制第七行
			IPT_WriteRow7(wData);
			// ③绘制工具
			int wRowNum = IPT_WriteTool(wData);
			// ④绘制项点
			wRowNum = IPT_WriteItems(wData, wRowNum);
			// ⑤绘制物料
			IPT_WriteMaterial(wData, wRowNum);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 绘制物料
	 */
	private static void IPT_WriteMaterial(IPTExport wData, int wRowNum) {
		try {
			wRowNum = wRowNum - 1;
			Row wRow;
			Cell wCell;
			wRow = mSheet.createRow(wRowNum + 2);

			// ①绘制表头
			List<String> wHeaders = new ArrayList<String>(
					Arrays.asList("工序描述", "物料名称", "物料编码", "数量", "单位", "必换(1)/偶换(2)/工艺辅料(3)", "偶换率"));
			for (int i = 0; i < wHeaders.size(); i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wHeaders.get(i));
				wCell.setCellStyle(mItemStyle);
			}

			// ①绘制内容
			int wLastRow = wRowNum + 3;
			for (APSBOMItem wAPSBOMItem : wData.APSBOMItemList) {
				wRow = mSheet.createRow(wLastRow);

				wCell = wRow.createCell(0);
				wCell.setCellValue(QMSConstants.GetFPCPartCode(wAPSBOMItem.PartID) + " " + wAPSBOMItem.PartPointName);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(1);
				wCell.setCellValue(wAPSBOMItem.MaterialName);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(2);
				wCell.setCellValue(wAPSBOMItem.MaterialNo);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(3);
				wCell.setCellValue(String.valueOf(wAPSBOMItem.Number));
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(4);
				wCell.setCellValue(wAPSBOMItem.UnitText);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(5);
				wCell.setCellValue(String.valueOf(wAPSBOMItem.ReplaceType));
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(6);
				wCell.setCellValue("");
				wCell.setCellStyle(mItemStyle);

				wLastRow++;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 绘制项点
	 */
	private static int IPT_WriteItems(IPTExport wData, int wRowNum) {
		int wResult = 0;
		try {
			// ②绘制表头
			Row wRow;
			Cell wCell;
			wRow = mSheet.createRow(wRowNum + 2);

			wCell = wRow.createCell(0);
			wCell.setCellValue("项目");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(1);
			wCell.setCellValue("序号");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(2);
			wCell.setCellValue("作业顺序");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(3);
			wCell.setCellValue("作业内容");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(4);
			wCell.setCellValue("质量要求及安全注意事项");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(5);
			wCell.setCellValue("图解");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(6);
			wCell.setCellValue("作业时间");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(7);
			wCell.setCellValue("过程自检");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(9);
			wCell.setCellValue("过程互检");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(11);
			wCell.setCellValue("终端检查");
			wCell.setCellStyle(mItemStyle);

			wRow = mSheet.createRow(wRowNum + 3);

			wCell = wRow.createCell(7);
			wCell.setCellValue("检验结果");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(8);
			wCell.setCellValue("工作者(日期)");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(9);
			wCell.setCellValue("检验结果");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(10);
			wCell.setCellValue("互检者(日期)");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(11);
			wCell.setCellValue("检验结果");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(12);
			wCell.setCellValue("检验员(日期)");
			wCell.setCellStyle(mItemStyle);
			// ③绘制项点内容
			int wLastRow = wRowNum + 4;
			for (IPTItemExport wIPTItemExport : wData.IPTItemExportList) {
				wRow = mSheet.createRow(wLastRow);

				// 计算高度
				int wHeight = CalcHeight(wIPTItemExport);
				wRow.setHeight((short) wHeight);

				wCell = wRow.createCell(0);
				wCell.setCellValue(wIPTItemExport.Project);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(1);
				wCell.setCellValue(wIPTItemExport.SerialNumber);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(2);
				wCell.setCellValue(wIPTItemExport.WorkOrder);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(3);
				wCell.setCellValue(wIPTItemExport.WorkContent);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(4);
				wCell.setCellValue(wIPTItemExport.Standard);
				wCell.setCellStyle(mItemStyle);

				// ①此处要修改为图片
				if (StringUtils.isNotEmpty(wIPTItemExport.Legend)) {
					AddPicture(wLastRow, wIPTItemExport.Legend, wRow);
				} else {
					wCell = wRow.createCell(5);
					wCell.setCellValue(wIPTItemExport.Legend);
					wCell.setCellStyle(mItemStyle);
				}

				wCell = wRow.createCell(6);
				wCell.setCellValue(wIPTItemExport.WorkTime);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(7);
				wCell.setCellValue(wIPTItemExport.SelfResult);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(8);
				wCell.setCellValue(wIPTItemExport.SelftDate);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(9);
				wCell.setCellValue(wIPTItemExport.MutualResult);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(10);
				wCell.setCellValue(wIPTItemExport.MutualDate);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(11);
				wCell.setCellValue(wIPTItemExport.SpecialResult);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(12);
				wCell.setCellValue(wIPTItemExport.SpecialDate);
				wCell.setCellStyle(mItemStyle);

				wCell = wRow.createCell(13);
				wCell.setCellValue("");
				wCell.setCellStyle(mNoneStyle);

				wLastRow++;
			}
			// ①合并单元格
			CellRangeAddress wRegion;

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 3, 0, 0);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 3, 1, 1);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 3, 2, 2);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 3, 3, 3);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 3, 4, 4);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 3, 5, 5);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 3, 6, 6);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 2, 7, 8);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 2, 9, 10);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(wRowNum + 2, wRowNum + 2, 11, 12);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderLeft(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wResult = wLastRow;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 计算高度，方便打印
	 */
	private static int CalcHeight(IPTItemExport wIPTItemExport) {
		int wResult = 0;
		try {
			int wSize = 0;
			if (wIPTItemExport.WorkOrder.length() > wSize) {
				wSize = wIPTItemExport.WorkOrder.length();
			}
			if (wIPTItemExport.WorkContent.length() > wSize) {
				wSize = wIPTItemExport.WorkContent.length();
			}
			if (wIPTItemExport.Standard.length() > wSize) {
				wSize = wIPTItemExport.Standard.length();
			}
			if (wIPTItemExport.SelftDate.length() > wSize) {
				wSize = wIPTItemExport.SelftDate.length();
			}
			if (wIPTItemExport.MutualDate.length() > wSize) {
				wSize = wIPTItemExport.MutualDate.length();
			}
			if (wIPTItemExport.SpecialDate.length() > wSize) {
				wSize = wIPTItemExport.SpecialDate.length();
			}

			int wRows = 0;
			if (wSize % 4 == 0) {
				wRows = wSize / 4;
			} else {
				wRows = wSize / 4 + 1;
			}

			wResult = wRows * 20 * 1000 / 66;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 添加图片到单元格
	 */
	private static void AddPicture(int wLastRow, String legend, Row wRow) {
		try {
			String[] wStrs = legend.split(";");
			wRow.setHeight((short) (2000 * wStrs.length));

			int wIndex = 0;
			for (String wStr : wStrs) {
				// ①获取后缀
				String wSuffiex = wStr.substring(wStr.lastIndexOf('.') + 1);

				ByteArrayOutputStream wByteArrayOut = new ByteArrayOutputStream();
				BufferedImage wBufferImg = ImageIO.read(new File(wStr));
				ImageIO.write(wBufferImg, wSuffiex, wByteArrayOut);

				HSSFSheet wSheet1 = (HSSFSheet) mSheet;
				// 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
				HSSFPatriarch wPatriarch = wSheet1.createDrawingPatriarch();
				// anchor主要用于设置图片的属性
				HSSFClientAnchor wAnchor = new HSSFClientAnchor(0, wIndex * (255 / wStrs.length), 1023,
						(wIndex + 1) * (255 / wStrs.length), (short) 5, wLastRow, (short) 5, wLastRow);
				// 注意：这个方法在新版本的POI中参数类型改成了（AnchorType anchorType）
				wAnchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);

				// 插入图片
				int wPicIndex = 0;
				if (wSuffiex.equals("jpg") || wSuffiex.equals("jpeg")) {
					wPicIndex = HSSFWorkbook.PICTURE_TYPE_JPEG;
				} else if (wSuffiex.equals("png")) {
					wPicIndex = HSSFWorkbook.PICTURE_TYPE_PNG;
				}

				wPatriarch.createPicture(wAnchor, mWorkbook.addPicture(wByteArrayOut.toByteArray(), wPicIndex));

				wIndex++;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 绘制工具
	 */
	private static int IPT_WriteTool(IPTExport wData) {
		int wResult = 0;
		try {
			// ①计算行数
			int wRows = wData.IPTToolList.size() % 2 == 0 ? wData.IPTToolList.size() / 2
					: wData.IPTToolList.size() / 2 + 1;
			wResult = wRows + 7;

			// ③获取行数据
			List<List<String>> wRowValueList = new ArrayList<List<String>>();
			for (int i = 0; i < wRows; i++) {
				List<String> wRowList = new ArrayList<String>(Arrays.asList(String.valueOf(i + 1),
						wData.IPTToolList.get(i).Name, wData.IPTToolList.get(i).Modal,
						String.valueOf(wData.IPTToolList.get(i).Number), wData.IPTToolList.get(i).UnitText,
						wData.IPTToolList.size() >= i + wRows + 1 ? String.valueOf(i + wRows + 1) : "",
						wData.IPTToolList.size() >= i + wRows + 1 ? wData.IPTToolList.get(i + wRows).Name : "",
						wData.IPTToolList.size() >= i + wRows + 1 ? wData.IPTToolList.get(i + wRows).Modal : "",
						wData.IPTToolList.size() >= i + wRows + 1
								? String.valueOf(wData.IPTToolList.get(i + wRows).Number)
								: "",
						wData.IPTToolList.size() >= i + wRows + 1 ? wData.IPTToolList.get(i + wRows).UnitText : ""));
				wRowValueList.add(wRowList);
			}
			// ④绘制表头
			Row wRow;
			Cell wCell;
			wRow = mSheet.createRow(7);
			List<String> wHeaders = new ArrayList<String>(
					Arrays.asList("序号", "名称", "规格型号", "数量", "单位", "序号", "名称", "规格型号", "数量", "单位"));
			for (int i = 0; i < wHeaders.size(); i++) {
				wCell = wRow.createCell(i + 1);
				wCell.setCellValue(wHeaders.get(i));
				wCell.setCellStyle(mItemStyle);
			}
			// ⑤绘制左侧标题
			wCell = wRow.createCell(0);
			wCell.setCellValue("工装、工具准备");
			wCell.setCellStyle(mItemStyle);
			// ⑥遍历绘制行数据
			for (int i = 0; i < wRowValueList.size(); i++) {
				wRow = mSheet.createRow(8 + i);
				for (int j = 0; j < wRowValueList.get(i).size(); j++) {
					wCell = wRow.createCell(j + 1);
					wCell.setCellValue(wRowValueList.get(i).get(j));
					wCell.setCellStyle(mItemStyle);
				}
			}
			// ②合并左侧单元格
			if (wData.IPTToolList == null || wData.IPTToolList.size() <= 0) {
				return wResult;
			}

			CellRangeAddress wRegion;

			wRegion = new CellRangeAddress(7, 7, 10, 12);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			for (int i = 0; i < wRows; i++) {
				wRegion = new CellRangeAddress(8 + i, 8 + i, 10, 12);
				mSheet.addMergedRegion(wRegion);
				RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
				RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
				RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);
			}

			wRegion = new CellRangeAddress(7, 7 + wRows, 0, 0);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 绘制第七行
	 */
	private static void IPT_WriteRow7(IPTExport wData) {
		try {
			Row wRow;
			Cell wCell;
			wRow = mSheet.createRow(6);

			CellRangeAddress wRegion;

			wRegion = new CellRangeAddress(6, 6, 1, 12);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

//			mSheet.addMergedRegion(new CellRangeAddress(6, 6, 1, 12));

			wCell = wRow.createCell(0);
			wCell.setCellValue("物料准备");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(1);
			wCell.setCellValue(wData.MaterialPrepare);
			wCell.setCellStyle(mLeftStyle);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 绘制第六行
	 */
	private static void IPT_WriteRow6(IPTExport wData) {
		try {
			Row wRow;
			Cell wCell;
			wRow = mSheet.createRow(5);

			CellRangeAddress wRegion;

			wRegion = new CellRangeAddress(5, 5, 1, 12);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

//			mSheet.addMergedRegion(new CellRangeAddress(5, 5, 1, 12));

			wCell = wRow.createCell(0);
			wCell.setCellValue("人员资质要求");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(1);
			wCell.setCellValue(wData.Requirement);
			wCell.setCellStyle(mLeftStyle);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 绘制第五行
	 */
	private static void IPT_WriteRow5(IPTExport wData) {
		try {
			Row wRow;
			Cell wCell;
			wRow = mSheet.createRow(4);

			CellRangeAddress wRegion;

			wRegion = new CellRangeAddress(4, 4, 1, 8);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(4, 4, 10, 12);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

//			mSheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 8));
//			mSheet.addMergedRegion(new CellRangeAddress(4, 4, 10, 12));

			wCell = wRow.createCell(0);
			wCell.setCellValue("工艺文件");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(1);
			wCell.setCellValue(wData.CraftFile);
			wCell.setCellStyle(mLeftStyle);

			wCell = wRow.createCell(9);
			wCell.setCellValue("工序性质");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(10);
			wCell.setCellValue(wData.StepNature);
			wCell.setCellStyle(mLeftStyle);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 绘制第四行
	 */
	private static void IPT_WriteRow4(IPTExport wData) {
		try {
			Row wRow;
			Cell wCell;
			wRow = mSheet.createRow(3);

			CellRangeAddress wRegion;

			wRegion = new CellRangeAddress(3, 3, 1, 7);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(3, 3, 9, 12);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wCell = wRow.createCell(0);
			wCell.setCellValue("人数");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(1);
			wCell.setCellValue(wData.PersonNumber);
			wCell.setCellStyle(mLeftStyle);

			wCell = wRow.createCell(8);
			wCell.setCellValue("作业时间");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(9);
			wCell.setCellValue(wData.WorkTime);
			wCell.setCellStyle(mLeftStyle);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 绘制第三行
	 */
	private static void IPT_WriteRow3(IPTExport wData) {
		try {
			Row wRow;
			Cell wCell;
			CellRangeAddress wRegion;
			wRow = mSheet.createRow(2);

			wCell = wRow.createCell(0);
			wCell.setCellValue("名称");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(1);
			wCell.setCellValue(wData.StepName);
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(4);
			wCell.setCellValue("编号");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(5);
			wCell.setCellValue(wData.Code);
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(8);
			wCell.setCellValue("版本");
			wCell.setCellStyle(mItemStyle);

			wCell = wRow.createCell(9);
			wCell.setCellValue(wData.Version);
			wCell.setCellStyle(mItemStyle);

			if (wData.Version.length() > 10) {
				wRow.setHeight((short) 600);
			}

			wRegion = new CellRangeAddress(2, 2, 1, 3);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(2, 2, 5, 7);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wRegion = new CellRangeAddress(2, 2, 9, 12);
			mSheet.addMergedRegion(wRegion);
			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 绘制第二行
	 */
	private static void IPT_WriteRow2(IPTExport wData) {
		try {
			CellRangeAddress wRegion;
			Row wRow;
			Cell wCell;
			wRow = mSheet.createRow(1);
			wRegion = new CellRangeAddress(1, 1, 0, 9);
			mSheet.addMergedRegion(wRegion);

			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wCell = wRow.createCell(0);
			String wValue = StringUtils.Format(
					"编制：{0}        日期：{1}        会签：{2}        日期：{3}        审核：{4}        日期：{5}        批准：{6}        日期：{7}",
					wData.Maker, wData.MakerDate, wData.JointlySign, wData.JointlySignDate, wData.Audit,
					wData.AuditDate, wData.Approval, wData.ApprovalDate);
			wCell.setCellValue(wValue);
			wCell.setCellStyle(mLeftStyle);

			wRegion = new CellRangeAddress(1, 1, 10, 12);
			mSheet.addMergedRegion(wRegion);

			RegionUtil.setBorderRight(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, wRegion, mSheet);
			RegionUtil.setBorderTop(BorderStyle.THIN, wRegion, mSheet);

			wCell = wRow.createCell(10);
			wCell.setCellValue(StringUtils.Format("车号：{0}", wData.PartNo));
			wCell.setCellStyle(mLeftStyle);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 创建部件信息
	 */
	public static void SCGC_WritePartItemList(List<MSSPartItem> wPartItemList, FileOutputStream wStream,
			String wPartNo) {
		try {
			if (wPartItemList == null || wPartItemList.size() <= 0) {
				return;
			}

			mWorkbook = new HSSFWorkbook();

			mTitleStyle = TitleStyle();
			mHeadStyle = HeadStyle();
			mItemStyle = ItemStyle();

			String[] wHeaders = { "序号", "车型", "修程", "局段", "部件名称", "数量", "供应商名称", "供应商型号", "供应商编号", "合格证", "二维码" };

			mSheet = (Sheet) mWorkbook.createSheet("台车部件信息");

			CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wHeaders.length - 1);
			mSheet.addMergedRegion(wRegion);

			Row wRow = mSheet.createRow(0);
			wRow.setHeight((short) 500);
			Cell wCell = wRow.createCell(0);
			wCell.setCellValue(StringUtils.Format("{0}-台车部件信息", new Object[] { wPartNo }));
			wCell.setCellStyle(mTitleStyle);

			wRow = mSheet.createRow(1);
			wRow.setHeight((short) 550);
			for (int i = 0; i < wHeaders.length; i++) {
				wCell = wRow.createCell(i);
				wCell.setCellValue(wHeaders[i]);
				wCell.setCellStyle(mHeadStyle);
			}

			int wRowNum = 2;
			int wIndex = 1;
			for (MSSPartItem wMSSPartItem : wPartItemList) {
				List<String> wValueList = GetValueList(wMSSPartItem);
				wValueList.set(0, String.valueOf(wIndex));
				WriteRowItem(wValueList, wRowNum++);
				wIndex++;
			}

			Export(wStream);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 将台车部件信息转换为行数据源
	 */
	private static List<String> GetValueList(MSSPartItem wMSSPartItem) {
		List<String> wResult = new ArrayList<String>();
		try {
			wResult = new ArrayList<String>(Arrays.asList("", wMSSPartItem.ProductNo, wMSSPartItem.LineName,
					wMSSPartItem.CustomerName, wMSSPartItem.Name, String.valueOf(wMSSPartItem.Number),
					wMSSPartItem.SupplierName, wMSSPartItem.SupplierProductNo, wMSSPartItem.SupplierPartNo,
					wMSSPartItem.Certificate == 1 ? "有" : "无", wMSSPartItem.QRCode == 1 ? "有" : "无"));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 导出Excel数据，通用函数
	 */
	public static String ExportData(List<MyExcelSheet> wMyExcelSheetList, String wShortFileName) {
		String wResult = "";
		try {
			if (wMyExcelSheetList == null || wMyExcelSheetList.size() <= 0) {
				return wResult;
			}

			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());

			String wFileName = StringUtils.Format("{1}{0}.xls", wCurTime, wShortFileName);
			String wDirePath = StringUtils.Format("{0}static/export/",
					Constants.getConfigPath().replace("config/", ""));

			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}

			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);

			mWorkbook = new HSSFWorkbook();

			mTitleStyle = TitleStyle();
			mHeadStyle = HeadStyle();
			mItemStyle = ItemStyle();

			for (MyExcelSheet wMyExcelSheet : wMyExcelSheetList) {
				CreateSheet(wMyExcelSheet);
			}

			Export(wFileOutputStream);

			wResult = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 创建一张表格数据
	 */
	private static void CreateSheet(MyExcelSheet wMyExcelSheet) {
		try {
			mSheet = (Sheet) mWorkbook.createSheet(wMyExcelSheet.SheetName);

			int wRowIndex = 0;
			Row wRow = null;
			Cell wCell = null;

			// 设置列宽
			SetColumnWidth(wMyExcelSheet.HeaderList.size(), 7114);

			if (StringUtils.isEmpty(wMyExcelSheet.TitleName)) {

			} else {
				CellRangeAddress wRegion = new CellRangeAddress(0, 0, 0, wMyExcelSheet.HeaderList.size() - 1);
				mSheet.addMergedRegion(wRegion);

				wRow = mSheet.createRow(wRowIndex);
				wRow.setHeight((short) 500);
				wCell = wRow.createCell(0);
				wCell.setCellValue(wMyExcelSheet.TitleName);
				wCell.setCellStyle(mTitleStyle);

				wRowIndex++;
			}

			wRow = mSheet.createRow(wRowIndex);
			wRow.setHeight((short) 550);
			for (int j = 0; j < wMyExcelSheet.HeaderList.size(); j++) {
				wCell = wRow.createCell(j);
				wCell.setCellValue(wMyExcelSheet.HeaderList.get(j));
				wCell.setCellStyle(mHeadStyle);
			}
			wRowIndex++;

			for (List<String> wValueList : wMyExcelSheet.DataList) {
				WriteRowItem(wValueList, wRowIndex++);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\serve\\utils\qms\ExcelUtil.
 * class Java compiler version: 8 (52.0) JD-Core Version: 1.1.2
 */