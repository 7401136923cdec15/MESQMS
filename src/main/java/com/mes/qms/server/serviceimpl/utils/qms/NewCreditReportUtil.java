package com.mes.qms.server.serviceimpl.utils.qms;

import java.awt.Color;
import java.awt.FontMetrics;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.mes.qms.server.service.mesenum.IPTItemType;
import com.mes.qms.server.service.mesenum.IPTTableTypes;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTPDFPart;
import com.mes.qms.server.service.po.ipt.IPTPDFStandard;
import com.mes.qms.server.service.po.ipt.IPTPreCheckItem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckReport;
import com.mes.qms.server.service.po.ipt.IPTRowValue;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.IPTServiceImpl;
import com.mes.qms.server.utils.Configuration;
import com.mes.qms.server.utils.Constants;

/**
 * 用iText生成PDF文档需要5个步骤：
 * 
 * ①建立com.lowagie.text.Document对象的实例。 Document document = new Document();
 * ②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
 * PDFWriter.getInstance(document, new FileOutputStream("Helloworld.PDF"));
 * ③打开文档。 document.open(); ④向文档中添加内容。 document.add(new Paragraph("Hello
 * World")); ⑤关闭文档。 document.close();
 *
 */
public class NewCreditReportUtil {
	private static Logger logger = LoggerFactory.getLogger(NewCreditReportUtil.class);
	/**
	 * 定义静态变量，用于生成水印文件名称
	 */
	private final static String RESULT_FILE = Configuration.readConfigString("pdf.test.file.path", "config/config");

	/**
	 * 生成PDF模板样例
	 * 
	 * @param NewCorpReportMap 输入参数映射
	 * @return 生成成功与否
	 * @throws MalformedURLException 格式异常
	 * @throws IOException           IO异常
	 * @throws DocumentException     文本异常
	 */
	public static boolean generateDeepthCreditReport(Map<String, Object> NewCorpReportMap)
			throws MalformedURLException, IOException, DocumentException {
		boolean wReslut = false;
		try {
			// ①建立com.lowagie.text.Document对象的实例。
			Document doc = new Document();
			doc.setMargins(20, 20, 30, 30);

			String fontPath = Constants.getConfigPath();

			// ②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
			PdfWriter.getInstance(doc, new FileOutputStream(RESULT_FILE));

			// 设置中文字体
			BaseFont fontChinese = BaseFont.createFont(fontPath + "simkai.ttf", BaseFont.IDENTITY_H,
					BaseFont.NOT_EMBEDDED);
			Font chinese = new Font(fontChinese, 10, Font.NORMAL);

			// 添加页眉页脚
			String headertitle = "*****报告" + "         " + "*******";
			addHeaderAndFooter(doc, chinese, headertitle);

			// ③打开文档。
			doc.open();

			// 18.0F是字体大小，0表示字体倾斜度, font.setStyle(1); 0：无变化1：加粗；2：斜体...
			// font.setFamily("微软雅黑"); // 设置字体
			Font myfont = setfont(fontPath + "/simkai.ttf", 13.0F, 0, Color.BLACK, 0);// 基本字体
			// Font myfont1 = setfont(fontPath + "/msyh.ttf", 36.0F, 0, Color.BLACK, 1);//
			// 标头字体（一级字体）
			// Font myfont2 = setfont(fontPath + "/msyh.ttf", 27.0F, 0, Color.BLACK, 1);//
			// 标头字体（二级字体）
			Font myfont3 = setfont(fontPath + "/simkai.ttf", 18.0F, 0, Color.BLACK, 1);// 标头字体（三级字体）
			// Font myfont4 = setfont(fontPath + "/msyh.ttf", 13.0F, 0, Color.BLACK, 1);//
			// 标头字体（四级字体）
			// Font myfont5 = setfont(fontPath + "/msyh.ttf", 12.0F, 0, Color.BLACK, 0);//
			// 标头字体（五级字体）

			// 初始化pdf基本功能性文本
			Image image = null;
			PdfPTable table;
			PdfPCell cell = null;
			Paragraph paragraph = null;

			// 准备工作结束，进行文档内容填充：
			// 添加公司logo图片
			table = new PdfPTable(1);
			String picpath = NewCorpReportMap.get("reportLogoFilePath").toString();
			addpicture(table, image, picpath, cell, doc);

			// 添加报告信息
			firstPage(cell, table, paragraph, NewCorpReportMap.get("corpname").toString(), "企业信用报告",
					NewCorpReportMap.get("reportNo").toString(), myfont, myfont3, myfont3, doc);

			// 第二页 （固定死页面）
			doc.newPage();
			doc.add(new Paragraph("       ", myfont));
			paragraph = new Paragraph("报告说明", myfont3);
			paragraph.setAlignment(1);
			doc.add(paragraph);
			doc.add(new Paragraph("       ", myfont));

			geshi1(new Paragraph("1. 内容1", myfont), doc);
			geshi1(new Paragraph("2. 内容2", myfont), doc);
			geshi1(new Paragraph("3. 内容3", myfont), doc);
			geshi1(new Paragraph("4. 内容4", myfont), doc);

			// 第三页 报告摘要,每页空2行留给页眉
//			doc.newPage();
//			doc.add(new Paragraph("       ", myfont));
//			doc.add(new Paragraph("       ", myfont));
			// 第四页添加Table
			/*
			 * doc.newPage(); PdfPTable wTable=new PdfPTable(3); wTable.setTotalWidth(new
			 * float[] {105,170,105,170}); wTable.setLockedWidth(true);
			 * 
			 * doc.add(wTable); doc.close();
			 */

			doc.newPage();
			doc.add(new Paragraph("       ", myfont));
			doc.add(new Paragraph("       ", myfont));
			PdfPTable table1 = new PdfPTable(5);
			for (int aw = 0; aw < 10; aw++) {
				// 构建每一格
				table1.addCell("cell");
			}
			doc.add(table1);
			doc.close();

			/*
			 * // 创建PdfWriter对象 PdfWriter writer = PdfWriter.getInstance(doc, new
			 * FileOutputStream(RESULT_FILE)); // 打开文档 doc.open();
			 * 
			 * // 添加表格，4列 PdfPTable wPdfPTable = new PdfPTable(4); //// 设置表格宽度比例为%100
			 * wPdfPTable.setWidthPercentage(100); // 设置表格的宽度 wPdfPTable.setTotalWidth(500);
			 * // 也可以每列分别设置宽度 wPdfPTable.setTotalWidth(new float[] { 160, 70, 130, 100 });
			 * // 锁住宽度 wPdfPTable.setLockedWidth(true); // 设置表格上面空白宽度
			 * wPdfPTable.setSpacingBefore(10f); // 设置表格下面空白宽度
			 * wPdfPTable.setSpacingAfter(10f); // 设置表格默认为无边框
			 * wPdfPTable.getDefaultCell().setBorder(0); PdfContentByte cb =
			 * writer.getDirectContent();
			 * 
			 * // 构建每个单元格 PdfPCell cell1 = new PdfPCell(new Paragraph("Cell 1")); // 设置跨两行
			 * cell1.setRowspan(2); // 设置距左边的距离 cell1.setPaddingLeft(10); // 设置高度
			 * cell1.setFixedHeight(20); // 设置内容水平居中显示
			 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER); // 设置垂直居中
			 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE); table.addCell(cell1);
			 * 
			 * PdfPCell cell2 = new PdfPCell(new Paragraph("Cell 2"));
			 * cell2.setPaddingLeft(10); cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell2.setVerticalAlignment(Element.ALIGN_MIDDLE); table.addCell(cell2);
			 * 
			 * PdfPCell cell3 = new PdfPCell(new Paragraph("Cell 3"));
			 * cell3.setPaddingLeft(10); // 设置无边框 cell3.setBorder(Rectangle.NO_BORDER);
			 * cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell3.setVerticalAlignment(Element.ALIGN_MIDDLE); table.addCell(cell3);
			 * 
			 * PdfPCell cell5 = new PdfPCell(new Paragraph("Cell 5"));
			 * cell5.setPaddingLeft(10); // 设置占用列数 cell5.setColspan(1);
			 * cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell5.setVerticalAlignment(Element.ALIGN_MIDDLE); table.addCell(cell5);
			 * doc.add(table); // 关闭文档 doc.close();
			 */
			wReslut = true;
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return wReslut;
	}

	/**
	 * 生成机车预检报告模板
	 * 
	 * @param wParamMap 输入参数映射
	 * @return 生成成功与否
	 * @throws MalformedURLException 格式异常
	 * @throws IOException           IO异常
	 * @throws DocumentException     文本异常
	 */
	public static boolean generatePrecheckReport(OutputStream wOutputStream)
			throws MalformedURLException, IOException, DocumentException {
		boolean wReslut = false;
		try {
			// ①建立com.lowagie.text.Document对象的实例。
			Document wDoc = new Document();
			wDoc.setMargins(20, 20, 30, 30);

			String wFontPath = Constants.getConfigPath() + "simkai.ttf";

			// ②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
			PdfWriter.getInstance(wDoc, wOutputStream);

			// 设置中文字体
			BaseFont wBaseFont = BaseFont.createFont(wFontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font wChineseFont = new Font(wBaseFont, 10, Font.NORMAL);

			// 添加页眉页脚
			addHeaderAndFooter(wDoc, wChineseFont, "机车预检报告");

			// ③打开文档。
			wDoc.open();

			// 基本字体
			Font wMyBaseFont = setfont(wFontPath, 13.0F, 0, Color.BLACK, 0);
			// 表格标题字体
			Font wMyTitleFont = setfont(wFontPath, 18.0F, 0, Color.BLACK, 1);
			// 下划线字体
			Font wUnderlineFont = new Font(wBaseFont, 13.0F, Font.UNDERLINE);
			// 加粗字体
			Font wBoldFont = new Font(wBaseFont, 13.0F, Font.BOLD);

			// 电气静态预检记录
			ElectricalStaticPrecheckRecord(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont);

			// 防雾闪绝缘胶套检查记录
			AdhesiveCoverInspectionRecord(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont);

			// 机械静态预检记录
			MechanicalStaticPrecheckRecord(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont);

			// 制动预检记录
			BrakePrecheckRecord(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont);

			// 车体预检记录
			VehicleBodyPrecheckRecord(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont);

			// 转向架预检记录
			BogiePrecheckRecord(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont);

			// 关键部件入场检查
			KeyComponentsInCheck(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont);

			// 电气动态性能预检记录
			ElectricalDynamicPerformancePrecheckRecord(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont);

			// 制动动态性能预检记录
			PrecheckRecordsOfBrakingDynamicPerformance(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont);

			wDoc.close();
			wReslut = true;
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return wReslut;
	}

	/**
	 * 生成动态机车预检报告
	 * 
	 * @param wIPTPDFPartList PDF配置组成集合
	 * @param wValueMap       表格值映射(key：PDF配置标准ID；value：值集合)
	 * @param wCarNum         车号
	 * @param wPrecheker      预检人名称
	 * @param wCustomName     局段名称
	 * @param wOutputStream   输出流
	 * @return 成功与否
	 * @throws MalformedURLException 格式异常
	 * @throws IOException           IO异常
	 * @throws DocumentException     文档异常
	 */
	public static boolean generateDynamicPrecheckReport(List<IPTPDFPart> wIPTPDFPartList,
			Map<Integer, List<IPTRowValue>> wValueMap, String wCarNum, String wPrecheker, String wCustomName,
			OutputStream wOutputStream) throws MalformedURLException, IOException, DocumentException {
		boolean wReslut = false;
		try {
			// ①建立com.lowagie.text.Document对象的实例。
			Document wDoc = new Document();
			wDoc.setMargins(20, 20, 30, 30);

			String wFontPath = Constants.getConfigPath() + "simkai.ttf";

			// ②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
			PdfWriter.getInstance(wDoc, wOutputStream);

			// 设置中文字体
			BaseFont wBaseFont = BaseFont.createFont(wFontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font wChineseFont = new Font(wBaseFont, 10, Font.NORMAL);

			// 添加页眉页脚
			addHeaderAndFooter(wDoc, wChineseFont, "机车预检报告");

			// ③打开文档。
			wDoc.open();

			// 基本字体
			Font wMyBaseFont = setfont(wFontPath, 13.0F, 0, Color.BLACK, 0);
			// 表格标题字体
			Font wMyTitleFont = setfont(wFontPath, 18.0F, 0, Color.BLACK, 1);
			// 下划线字体
			Font wUnderlineFont = new Font(wBaseFont, 13.0F, Font.UNDERLINE);
			// 加粗字体
			Font wBoldFont = new Font(wBaseFont, 13.0F, Font.BOLD);

			for (IPTPDFPart wIPTPDFPart : wIPTPDFPartList) {
				// 绘制组成部分
				PaintIPTPDFPart(wDoc, wMyBaseFont, wMyTitleFont, wUnderlineFont, wBoldFont, wIPTPDFPart, wValueMap,
						wCarNum, wPrecheker, wCustomName);
			}

			wDoc.close();
			wReslut = true;
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return wReslut;
	}

	/**
	 * 绘制PDF某一大部分
	 * 
	 * @param wDoc           文档上下文
	 * @param wMyBaseFont    基本字体
	 * @param wMyTitleFont   标题字体
	 * @param wUnderlineFont 下划线字体
	 * @param wBoldFont      加粗字体
	 * @param wIPTPDFPart    PDF部分
	 * @param wValueMap      值映射
	 */
	private static void PaintIPTPDFPart(Document wDoc, Font wMyBaseFont, Font wMyTitleFont, Font wUnderlineFont,
			Font wBoldFont, IPTPDFPart wIPTPDFPart, Map<Integer, List<IPTRowValue>> wValueMap, String wCarNum,
			String wPrecheker, String wCustomName) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph(wIPTPDFPart.PartTitle, wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk(wCarNum, wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk(wPrecheker, wUnderlineFont));
			wInfo.add(new Chunk("    局段：", wMyBaseFont));
			wInfo.add(new Chunk(wCustomName, wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 循环绘制表格
			for (IPTPDFStandard wIPTPDFStandard : wIPTPDFPart.IPTPDFStandardList) {
				if (!wValueMap.containsKey(wIPTPDFStandard.ID))
					continue;
				DrawDynamicBlock(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont,
						wValueMap.get(wIPTPDFStandard.ID), wIPTPDFStandard);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 制动动态性能预检记录
	 */
	private static void PrecheckRecordsOfBrakingDynamicPerformance(Document wDoc, Font wMyTitleFont, Font wMyBaseFont,
			Font wUnderlineFont, Font wBoldFont) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph("第九部分 制动动态性能预检记录", wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk("HXD3C-231", wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk("张毅", wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			List<List<String>> wValueListList = null;

			wValueListList = new ArrayList<List<String>>();
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("张毅", "")));
			DrawDynamicBlock(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont, wValueListList, "", false);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 电气动态性能预检记录
	 */
	private static void ElectricalDynamicPerformancePrecheckRecord(Document wDoc, Font wMyTitleFont, Font wMyBaseFont,
			Font wUnderlineFont, Font wBoldFont) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph("第八部分 电气动态性能预检记录", wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk("HXD3C-231", wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk("张毅", wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			List<List<String>> wValueListList = null;

			// 一
			wValueListList = new ArrayList<List<String>>();
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("张毅", "")));
			DrawDynamicBlock(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont, wValueListList,
					"一、性能试验前状态检查记录(1)机车无火转有火", true);

			// 二
			wValueListList = new ArrayList<List<String>>();
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("张毅", "")));
			DrawDynamicBlock(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont, wValueListList,
					"二、机车110V电源、设备及照明记录(1)110V电源及设备检查", true);

			// 三
			wValueListList = new ArrayList<List<String>>();
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("张毅", "")));
			DrawDynamicBlock(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont, wValueListList, "(2) 三方检测设备检查",
					true);

			// 三
			wValueListList = new ArrayList<List<String>>();
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("张毅", "")));
			DrawDynamicBlock(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont, wValueListList,
					"(3) 6A系统及CMD系统检查", true);

			// 软件版本记录
			wValueListList = new ArrayList<List<String>>();
			wValueListList.add(new ArrayList<String>(Arrays.asList("1", "APU1版本", "8009", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("2", "APU2版本", "8000", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("3", "DISPLAY", "0124", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("张毅", "")));
			DrawDynamicBlock1(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont, wValueListList, "二、软件版本记录");

			// 高压前检查
			wValueListList = new ArrayList<List<String>>();
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("张毅", "")));
			DrawDynamicBlock(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont, wValueListList, "三、高压前检查",
					true);

			// 高压预检
			wValueListList = new ArrayList<List<String>>();
			wValueListList
					.add(new ArrayList<String>(Arrays.asList("车顶受电弓", "①受电弓已解绑；②受电弓部件完整；③碳滑板无严重破损", "符合", "如：碳滑板破损")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("车端平均管塞门", "①平均管塞门手柄关闭", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("制动柜内B40.06塞门", "①B40.06黄色碟形塞门置于“水平”位", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("张毅", "")));
			DrawDynamicBlock(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont, wValueListList, "四、高压预检",
					true);

			// 段改项目
			wValueListList = new ArrayList<List<String>>();
			wValueListList.add(new ArrayList<String>(Arrays.asList("1", "大小复位改造", "符合", "无")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("2", "欠风压报警装置", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("3", "拾音器改造", "符合", "")));
			wValueListList.add(new ArrayList<String>(Arrays.asList("张毅", "")));
			DrawDynamicBlock1(wDoc, wMyTitleFont, wMyBaseFont, wUnderlineFont, wBoldFont, wValueListList, "五、段改项目");
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 绘制动态表格+标题
	 * 
	 * @param wDoc             文本上下文
	 * @param wMyTitleFont     标题字体
	 * @param wMyBaseFont      基础字体
	 * @param wUnderlineFont   下划线字体
	 * @param wBoldFont        基础加粗字体
	 * @param wIPTRowValueList 行数据值集合
	 * @param wIPTPDFStandard  PDF标准配置
	 */
	private static void DrawDynamicBlock(Document wDoc, Font wMyTitleFont, Font wMyBaseFont, Font wUnderlineFont,
			Font wBoldFont, List<IPTRowValue> wIPTRowValueList, IPTPDFStandard wIPTPDFStandard) {
		try {
			if (wIPTPDFStandard.IsShowTitle) {
				// 绘制标题
				Paragraph wTitleParagraph = new Paragraph(wIPTPDFStandard.TitleName, wBoldFont);
				wDoc.add(wTitleParagraph);
				wDoc.add(new Paragraph("    ", wMyBaseFont));
			}
			// 绘制表格
			PdfPTable wPdfPTable = null;
			PdfPCell wTitleCell = null;
			List<String> wTitleNames = null;
			boolean wIsPaintCheck = false;
			switch (IPTTableTypes.getEnumType(wIPTPDFStandard.TableType)) {
			case StaticRecord:// 静态记录表
				wTitleNames = new ArrayList<String>(Arrays.asList("序号", "检查项点", "入场状态", "厂家/编号", "异常情况(含段改)", "备注"));
				wPdfPTable = new PdfPTable(6);
				wPdfPTable.setWidths(new int[] { 80, 220, 220, 250, 210, 70 });
				wIsPaintCheck = false;
				break;
			case DynamicRecord:// 动态记录表
				wTitleNames = new ArrayList<String>(Arrays.asList("检查项点", "合格标准", "检查记录(不符合标准注明原因)", "故障原因分析与记录"));
				wPdfPTable = new PdfPTable(4);
				wIsPaintCheck = true;
				break;
			case InsulationRubberSleeve:// 绝缘胶套表
				wTitleNames = new ArrayList<String>(Arrays.asList("序号", "检查项点", "图例", "检查记录(需拍照留证)", ""));
				wPdfPTable = new PdfPTable(5);
				wPdfPTable.setWidths(new int[] { 80, 220, 220, 250, 210 });
				wIsPaintCheck = false;
				break;
			case KeyComponent:// 关键部件表
				wTitleNames = new ArrayList<String>(Arrays.asList("序号", "项点", "厂家", "型号", "编号", "入场状态(含段改)"));
				wPdfPTable = new PdfPTable(6);
				wPdfPTable.setWidths(new int[] { 80, 250, 220, 250, 210, 300 });
				wIsPaintCheck = false;
				break;
			case SoftwardVerson:// 软件版本表
				wTitleNames = new ArrayList<String>(Arrays.asList("序号", "检查项目", "检查记录", ""));
				wPdfPTable = new PdfPTable(4);
				wPdfPTable.setWidths(new int[] { 80, 220, 300, 70 });
				wIsPaintCheck = true;
				break;
			default:
				break;
			}
			wPdfPTable.setWidthPercentage(100);
			// 绘制表头
			for (String wName : wTitleNames) {
				wTitleCell = new PdfPCell(new Paragraph(wName, wBoldFont));
				wTitleCell.setPaddingTop(10);
				wTitleCell.setPaddingBottom(10);
				wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				wPdfPTable.addCell(wTitleCell);
			}
			// 绘制内容
			if (wIsPaintCheck) {
				for (int i = 0; i < wIPTRowValueList.size() - 1; i++) {
					List<String> wRowList = wIPTRowValueList.get(i).ValueList;
					for (String wValue : wRowList) {
						wTitleCell = new PdfPCell(new Paragraph(wValue, wMyBaseFont));
						wTitleCell.setPaddingLeft(10);
						wTitleCell.setPaddingTop(10);
						wTitleCell.setPaddingBottom(10);
						wTitleCell.setPaddingRight(10);
						wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						wPdfPTable.addCell(wTitleCell);
					}
				}
				// 绘制自检、互检
				List<String> wLastList = wIPTRowValueList.get(wIPTRowValueList.size() - 1).ValueList;

				wTitleCell = new PdfPCell(new Paragraph("自检", wBoldFont));
				wTitleCell.setPaddingLeft(10);
				wTitleCell.setPaddingTop(10);
				wTitleCell.setPaddingBottom(10);
				wTitleCell.setPaddingRight(10);
				wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				wPdfPTable.addCell(wTitleCell);

				wTitleCell = new PdfPCell(new Paragraph(wLastList.get(0), wMyBaseFont));
				wTitleCell.setPaddingLeft(10);
				wTitleCell.setPaddingTop(10);
				wTitleCell.setPaddingBottom(10);
				wTitleCell.setPaddingRight(10);
				wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				wPdfPTable.addCell(wTitleCell);

				wTitleCell = new PdfPCell(new Paragraph("互检", wBoldFont));
				wTitleCell.setPaddingLeft(10);
				wTitleCell.setPaddingTop(10);
				wTitleCell.setPaddingBottom(10);
				wTitleCell.setPaddingRight(10);
				wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				wPdfPTable.addCell(wTitleCell);

				wTitleCell = new PdfPCell(new Paragraph(wLastList.get(1), wMyBaseFont));
				wTitleCell.setPaddingLeft(10);
				wTitleCell.setPaddingTop(10);
				wTitleCell.setPaddingBottom(10);
				wTitleCell.setPaddingRight(10);
				wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				wPdfPTable.addCell(wTitleCell);
			} else {
				Image wImage = null;
				for (IPTRowValue wIPTRowValue : wIPTRowValueList) {
					for (int i = 0; i < wIPTRowValue.ValueList.size(); i++) {
						if (i == 2 && wIPTPDFStandard.TableType == IPTTableTypes.InsulationRubberSleeve.getValue()
								&& StringUtils.isNotEmpty(wIPTRowValue.ValueList.get(i))) {
							wImage = Image.getInstance(wIPTRowValue.ValueList.get(i));
							wImage.scaleAbsolute(120, 80);
							wTitleCell = new PdfPCell(wImage);
							wTitleCell.setPaddingBottom(10);
							wTitleCell.setPaddingTop(10);
							wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							wPdfPTable.addCell(wTitleCell);
						} else {
							if (wIPTRowValue.IsBold) {
								wTitleCell = new PdfPCell(new Paragraph(wIPTRowValue.ValueList.get(i), wBoldFont));
							} else {
								wTitleCell = new PdfPCell(new Paragraph(wIPTRowValue.ValueList.get(i), wMyBaseFont));
							}
							if (wIPTRowValue.IsGrayBackground) {
								wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
							}
							wTitleCell.setPaddingLeft(10);
							wTitleCell.setPaddingTop(10);
							wTitleCell.setPaddingBottom(10);
							wTitleCell.setPaddingRight(10);
							wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
							wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
							wPdfPTable.addCell(wTitleCell);
						}
					}
				}
			}
			// 添加表格到文本上下文中
			wDoc.add(wPdfPTable);
			// 静态记录表格添加备注
			if (wIPTPDFStandard.TableType == IPTTableTypes.StaticRecord.getValue()) {
				// 添加备注
				List<String> wRemarks = new ArrayList<String>();
				wRemarks.add("1.入场状态和厂家/编号由各预检人员填写，请务必字迹清晰可识；");
				wRemarks.add("2.如有额外补充项点，请自行补充；");
				wRemarks.add("3.入场状态若无异常，则填写“√”。");
				AddRemarks(wDoc, wMyBaseFont, wBoldFont, wRemarks);
			}
			wDoc.add(new Paragraph("    ", wMyBaseFont));
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 动态块绘制(标题+表格+自检+互检)
	 * 
	 */
	private static void DrawDynamicBlock(Document wDoc, Font wMyTitleFont, Font wMyBaseFont, Font wUnderlineFont,
			Font wBoldFont, List<List<String>> wValueListList, String wTitle, boolean wIsShowTitle) {
		try {
			if (wIsShowTitle) {
				// 绘制标题
				Paragraph wTitleParagraph = new Paragraph(wTitle, wBoldFont);
				wDoc.add(wTitleParagraph);
				wDoc.add(new Paragraph("    ", wMyBaseFont));
			}

			// 绘制表格
			PdfPTable wPdfPTable = new PdfPTable(4);
			wPdfPTable.setWidthPercentage(100);

			PdfPCell wTitleCell = null;
			// 表头
			List<String> wNames = new ArrayList<String>(Arrays.asList("检查项点", "合格标准", "检查记录(不符合标准注明原因)", "故障原因分析与记录"));
			for (String wName : wNames) {
				wTitleCell = new PdfPCell(new Paragraph(wName, wBoldFont));
				wTitleCell.setPaddingTop(10);
				wTitleCell.setPaddingBottom(10);
				wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				wPdfPTable.addCell(wTitleCell);
			}
			// 表内容
			for (int i = 0; i < wValueListList.size() - 1; i++) {
				List<String> wRowList = wValueListList.get(i);
				for (String wValue : wRowList) {
					wTitleCell = new PdfPCell(new Paragraph(wValue, wMyBaseFont));
					wTitleCell.setPaddingLeft(10);
					wTitleCell.setPaddingTop(10);
					wTitleCell.setPaddingBottom(10);
					wTitleCell.setPaddingRight(10);
					wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					wPdfPTable.addCell(wTitleCell);
				}
			}
			// 绘制自检、互检
			List<String> wLastList = wValueListList.get(wValueListList.size() - 1);

			wTitleCell = new PdfPCell(new Paragraph("自检", wBoldFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingRight(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph(wLastList.get(0), wMyBaseFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingRight(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("互检", wBoldFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingRight(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph(wLastList.get(1), wMyBaseFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingRight(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wDoc.add(wPdfPTable);
			wDoc.add(new Paragraph("    ", wMyBaseFont));
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 动态块绘制(标题+表格+自检+互检)(软件版本)
	 * 
	 */
	private static void DrawDynamicBlock1(Document wDoc, Font wMyTitleFont, Font wMyBaseFont, Font wUnderlineFont,
			Font wBoldFont, List<List<String>> wValueListList, String wTitle) {
		try {
			// 绘制标题
			Paragraph wTitleParagraph = new Paragraph(wTitle, wBoldFont);
			wDoc.add(wTitleParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 绘制表格
			PdfPTable wPdfPTable = new PdfPTable(4);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 70, 220, 300, 70 });

			PdfPCell wTitleCell = null;
			// 表头
			List<String> wNames = new ArrayList<String>(Arrays.asList("序号", "检查项目", "检查记录", ""));
			for (String wName : wNames) {
				wTitleCell = new PdfPCell(new Paragraph(wName, wBoldFont));
				wTitleCell.setPaddingTop(10);
				wTitleCell.setPaddingBottom(10);
				wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				wPdfPTable.addCell(wTitleCell);
			}
			// 表内容
			for (int i = 0; i < wValueListList.size() - 1; i++) {
				List<String> wRowList = wValueListList.get(i);
				for (String wValue : wRowList) {
					wTitleCell = new PdfPCell(new Paragraph(wValue, wMyBaseFont));
					wTitleCell.setPaddingLeft(10);
					wTitleCell.setPaddingTop(10);
					wTitleCell.setPaddingBottom(10);
					wTitleCell.setPaddingRight(10);
					wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					wPdfPTable.addCell(wTitleCell);
				}
			}
			// 绘制自检、互检
			List<String> wLastList = wValueListList.get(wValueListList.size() - 1);

			wTitleCell = new PdfPCell(new Paragraph("自检", wBoldFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingRight(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph(wLastList.get(0), wMyBaseFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingRight(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("互检", wBoldFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingRight(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph(wLastList.get(1), wMyBaseFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingRight(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wDoc.add(wPdfPTable);
			wDoc.add(new Paragraph("    ", wMyBaseFont));
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 关键部件入场检查
	 */
	private static void KeyComponentsInCheck(Document wDoc, Font wMyTitleFont, Font wMyBaseFont, Font wUnderlineFont,
			Font wBoldFont) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph("第七部分 关键部件入场检查", wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk("HXD3C-231", wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk("张毅", wUnderlineFont));
			wInfo.add(new Chunk("    局段：", wMyBaseFont));
			wInfo.add(new Chunk("广铁长段", wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检记录表格
			PdfPTable wPdfPTable = new PdfPTable(6);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 80, 250, 220, 250, 210, 300 });
			// 添加表头行
			AddTitleRow2(wBoldFont, wPdfPTable);
			// 添加检查项点明细行
			AddCheckItemDetailRow(wPdfPTable, wMyBaseFont,
					new ArrayList<String>(Arrays.asList("1", "主司机座椅", "中国北车", "", "", "√")));
			AddCheckItemDetailRow(wPdfPTable, wMyBaseFont,
					new ArrayList<String>(Arrays.asList("2", "副司机座椅", "中国北车", "", "", "√")));
			AddCheckItemDetailRow(wPdfPTable, wMyBaseFont,
					new ArrayList<String>(Arrays.asList("3", "添乘座椅(左)", "中国北车", "", "", "棕色折叠床，单皮受损")));
			AddCheckItemDetailRow(wPdfPTable, wMyBaseFont,
					new ArrayList<String>(Arrays.asList("4", "添乘座椅(右)", "中国北车", "", "", "棕色单床，单皮受损")));
			AddCheckItemDetailRow(wPdfPTable, wMyBaseFont,
					new ArrayList<String>(Arrays.asList("5", "空调控制箱", "石家庄国祥", "TTKC4.5-1TEC", "06101396", "√")));
			// 添加表格
			wDoc.add(wPdfPTable);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 转向架预检记录
	 * 
	 */
	private static void BogiePrecheckRecord(Document wDoc, Font wMyTitleFont, Font wMyBaseFont, Font wUnderlineFont,
			Font wBoldFont) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph("第六部分 转向架预检记录", wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk("HXD3C-231", wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk("张毅", wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检记录表格
			PdfPTable wPdfPTable = new PdfPTable(6);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 70, 220, 220, 250, 210, 70 });
			// 添加表头行
			AddTitleRow(wMyBaseFont, wPdfPTable);
			// 添加检查项点行
			AddCheckItemRow(wPdfPTable, "1", "空气管路及扫石器装配", wBoldFont);
			// 添加检查项点明细行
			AddCheckItemDetailRow(wPdfPTable, "1.1", "左前扫石器支架", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.2", "左前扫石器安装板", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.3", "左前撒砂管", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.4", "右前扫石器支架", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.5", "右前扫石器安装板", wMyBaseFont);
			// 添加表格
			wDoc.add(wPdfPTable);

			// 添加备注
			List<String> wRemarks = new ArrayList<String>();
			wRemarks.add("1.入场状态和厂家/编号由各预检人员填写，请务必字迹清晰可识；");
			wRemarks.add("2.如有额外补充项点，请自行补充；");
			wRemarks.add("3.入场状态若无异常，则填写“√”。");
			AddRemarks(wDoc, wMyBaseFont, wBoldFont, wRemarks);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 车体预检记录
	 * 
	 */
	private static void VehicleBodyPrecheckRecord(Document wDoc, Font wMyTitleFont, Font wMyBaseFont,
			Font wUnderlineFont, Font wBoldFont) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph("第五部分 车体预检记录", wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk("HXD3C-231", wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk("张毅", wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检记录表格
			PdfPTable wPdfPTable = new PdfPTable(6);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 70, 220, 220, 250, 210, 70 });
			// 添加表头行
			AddTitleRow(wMyBaseFont, wPdfPTable);
			// 添加检查项点行
			AddCheckItemRow(wPdfPTable, "1", "车钩", wBoldFont);
			// 添加检查项点明细行
			AddCheckItemDetailRow(wPdfPTable, "1.1", "钩体", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.2", "钩舌", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.3", "钩舌销", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.4", "防跳装置", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.5", "防脱钢丝绳", wMyBaseFont);
			// 添加表格
			wDoc.add(wPdfPTable);

			// 添加备注
			List<String> wRemarks = new ArrayList<String>();
			wRemarks.add("1.入场状态和厂家/编号由各预检人员填写，请务必字迹清晰可识；");
			wRemarks.add("2.如有额外补充项点，请自行补充；");
			wRemarks.add("3.入场状态若无异常，则填写“√”。");
			AddRemarks(wDoc, wMyBaseFont, wBoldFont, wRemarks);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 制动预检记录
	 * 
	 */
	private static void BrakePrecheckRecord(Document wDoc, Font wMyTitleFont, Font wMyBaseFont, Font wUnderlineFont,
			Font wBoldFont) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph("第四部分 制动预检记录", wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk("HXD3C-231", wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk("张毅", wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检记录表格
			PdfPTable wPdfPTable = new PdfPTable(6);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 70, 220, 220, 250, 210, 70 });
			// 添加表头行
			AddTitleRow(wMyBaseFont, wPdfPTable);
			// 添加检查项点行
			AddCheckItemRow(wPdfPTable, "1", "机械间制动部件", wBoldFont);
			// 添加检查项点明细行
			AddCheckItemDetailRow(wPdfPTable, "1.1", "双管飓风装置(风表)", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.2", "NB11", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.3", "A3安全阀", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.4", "A7安全阀", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.5", "轮喷鸣笛气路板", wMyBaseFont);
			// 添加表格
			wDoc.add(wPdfPTable);

			// 添加备注
			List<String> wRemarks = new ArrayList<String>();
			wRemarks.add("1.入场状态和厂家/编号由各预检人员填写，请务必字迹清晰可识；");
			wRemarks.add("2.如有额外补充项点，请自行补充；");
			wRemarks.add("3.入场状态若无异常，则填写“√”。");
			AddRemarks(wDoc, wMyBaseFont, wBoldFont, wRemarks);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 机械静态预检记录
	 */
	private static void MechanicalStaticPrecheckRecord(Document wDoc, Font wMyTitleFont, Font wMyBaseFont,
			Font wUnderlineFont, Font wBoldFont) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph("第三部分 机械静态预检记录", wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk("HXD3C-231", wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk("张毅", wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检记录表格
			PdfPTable wPdfPTable = new PdfPTable(6);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 70, 220, 220, 250, 210, 70 });
			// 添加表头行
			AddTitleRow(wMyBaseFont, wPdfPTable);
			// 添加检查项点行
			AddCheckItemRow(wPdfPTable, "1", "司机室机械设备", wBoldFont);
			// 添加检查项点明细行
			AddCheckItemDetailRow(wPdfPTable, "1.1", "操纵台左柜门", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.2", "操纵台中柜门", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.3", "操纵台右柜门", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.4", "操纵台面板", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.5", "操纵台面板胶皮", wMyBaseFont);
			// 添加表格
			wDoc.add(wPdfPTable);

			// 添加备注
			List<String> wRemarks = new ArrayList<String>();
			wRemarks.add("1.入场状态和厂家/编号由各预检人员填写，请务必字迹清晰可识；");
			wRemarks.add("2.如有额外补充项点，请自行补充；");
			wRemarks.add("3.入场状态若无异常，则填写“√”。");
			AddRemarks(wDoc, wMyBaseFont, wBoldFont, wRemarks);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加备注
	 */
	private static void AddRemarks(Document wDoc, Font wMyBaseFont, Font wBoldFont, List<String> wRemarks) {
		try {
			PdfPTable wPdfPTable = new PdfPTable(2);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 45, 500 });
			PdfPCell wCell = null;

			wCell = new PdfPCell(new Phrase("备注：", wBoldFont));
			wCell.setPaddingTop(10);
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wPdfPTable.addCell(wCell);

			for (int i = 0; i < wRemarks.size(); i++) {
				if (i == 0) {
					wCell = new PdfPCell(new Phrase(wRemarks.get(i), wMyBaseFont));
					wCell.setPaddingTop(10);
					wCell.setBorder(Rectangle.NO_BORDER);
					wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					wPdfPTable.addCell(wCell);
				} else {
					wCell = new PdfPCell(new Phrase("  ", wMyBaseFont));
					wCell.setBorder(Rectangle.NO_BORDER);
					wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					wPdfPTable.addCell(wCell);

					wCell = new PdfPCell(new Phrase(wRemarks.get(i), wMyBaseFont));
					wCell.setBorder(Rectangle.NO_BORDER);
					wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
					wPdfPTable.addCell(wCell);
				}
			}
			wDoc.add(wPdfPTable);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 防雾闪绝缘胶套检查记录
	 * 
	 */
	private static void AdhesiveCoverInspectionRecord(Document wDoc, Font wMyTitleFont, Font wMyBaseFont,
			Font wUnderlineFont, Font wBoldFont) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph("第二部分 防雾闪绝缘胶套检查记录", wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk("HXD3C-231", wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk("张毅", wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			PdfPTable wPdfPTable = new PdfPTable(5);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 70, 220, 220, 250, 210 });
			// 添加表头行
			AddTitleRow1(wMyBaseFont, wPdfPTable);
			// 添加检查项点明细行
			AddCheckItemDetailRow(wPdfPTable, "1", "高压套管顶部绝缘护套", wMyBaseFont, "无",
					"http://localhost:8088/MESCore/upload/image/%E5%9F%BA%E7%A1%80%E8%AE%BE%E7%BD%AE.png");
			AddCheckItemDetailRow(wPdfPTable, "2", "高压套管底部绝缘护套", wMyBaseFont, "有",
					"http://localhost:8088/MESCore/upload/image/%E5%9F%BA%E7%A1%80%E8%AE%BE%E7%BD%AE.png");
			AddCheckItemDetailRow(wPdfPTable, "3", "避雷器顶部绝缘护套", wMyBaseFont, "无",
					"http://localhost:8088/MESCore/upload/image/%E5%9F%BA%E7%A1%80%E8%AE%BE%E7%BD%AE.png");
			AddCheckItemDetailRow(wPdfPTable, "4", "避雷器底部绝缘护套", wMyBaseFont, "有",
					"http://localhost:8088/MESCore/upload/image/%E5%9F%BA%E7%A1%80%E8%AE%BE%E7%BD%AE.png");
			// 添加表格
			wDoc.add(wPdfPTable);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 生成电气静态预检记录
	 * 
	 * @param wDoc         文本上下文
	 * @param wMyTitleFont 标题字体
	 * @param wMyBaseFont  基本字体
	 */
	private static void ElectricalStaticPrecheckRecord(Document wDoc, Font wMyTitleFont, Font wMyBaseFont,
			Font wUnderlineFont, Font wBoldFont) {
		try {
			// 新开一页
			wDoc.newPage();
			// 标题
			Paragraph wParagraph = new Paragraph("第一部分 电气静态预检记录", wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk("HXD3C-231", wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk("张毅", wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检记录表格
			PdfPTable wPdfPTable = new PdfPTable(6);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 70, 220, 220, 250, 210, 70 });
			// 添加表头行
			AddTitleRow(wMyBaseFont, wPdfPTable);
			// 添加检查项点行
			AddCheckItemRow(wPdfPTable, "1", "司机室电气设备静态检查", wBoldFont);
			// 添加检查项点明细行
			AddCheckItemDetailRow(wPdfPTable, "1.1", "重联电话", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.2", "车载通话器", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.3", "空调控制面板", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.4", "脚踏总成", wMyBaseFont);
			AddCheckItemDetailRow(wPdfPTable, "1.5", "阅读灯开关", wMyBaseFont);
			// 添加表格
			wDoc.add(wPdfPTable);

			// 添加备注
			List<String> wRemarks = new ArrayList<String>();
			wRemarks.add("1.入场状态和厂家/编号由各预检人员填写，请务必字迹清晰可识；");
			wRemarks.add("2.如有额外补充项点，请自行补充；");
			wRemarks.add("3.入场状态若无异常，则填写“√”。");
			AddRemarks(wDoc, wMyBaseFont, wBoldFont, wRemarks);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加表头行
	 * 
	 * @param wMyBaseFont 基本字体
	 * @param wPdfPTable  表格
	 */
	private static void AddTitleRow(Font wMyBaseFont, PdfPTable wPdfPTable) {
		try {
			PdfPCell wTitleCell = null;
			// 序号
			wTitleCell = new PdfPCell(new Paragraph("序号", wMyBaseFont));
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 检查项点
			wTitleCell = new PdfPCell(new Paragraph("检查项点", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 合格标准
			wTitleCell = new PdfPCell(new Paragraph("合格标准", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 图例
			wTitleCell = new PdfPCell(new Paragraph("图例", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 检查记录
			wTitleCell = new PdfPCell(new Paragraph("检查记录", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 厂家
			wTitleCell = new PdfPCell(new Paragraph("厂家", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 型号
			wTitleCell = new PdfPCell(new Paragraph("型号", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 编号
			wTitleCell = new PdfPCell(new Paragraph("编号", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 异常情况(含段改)
			wTitleCell = new PdfPCell(new Paragraph("异常情况(含段改)", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 备注
			wTitleCell = new PdfPCell(new Paragraph("备注", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加表头行
	 * 
	 * @param wMyBaseFont 基本字体
	 * @param wPdfPTable  表格
	 */
	private static void AddTitleRow1(Font wMyBaseFont, PdfPTable wPdfPTable) {
		try {
			PdfPCell wTitleCell = null;
			// 序号
			wTitleCell = new PdfPCell(new Paragraph("序号", wMyBaseFont));
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 检查项点
			wTitleCell = new PdfPCell(new Paragraph("检查项点", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 图例
			wTitleCell = new PdfPCell(new Paragraph("图例", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 检查记录(需拍照留证)
			wTitleCell = new PdfPCell(new Paragraph("检查记录(需拍照留证)", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			//
			wTitleCell = new PdfPCell(new Paragraph("", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加表头行
	 * 
	 * @param wBoldFont  基本字体
	 * @param wPdfPTable 表格
	 */
	private static void AddTitleRow2(Font wBoldFont, PdfPTable wPdfPTable) {
		try {
			PdfPCell wTitleCell = null;
			// 序号
			wTitleCell = new PdfPCell(new Paragraph("序号", wBoldFont));
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 检查项点
			wTitleCell = new PdfPCell(new Paragraph("项点", wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 厂家
			wTitleCell = new PdfPCell(new Paragraph("厂家", wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 型号
			wTitleCell = new PdfPCell(new Paragraph("型号", wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 编号
			wTitleCell = new PdfPCell(new Paragraph("编号", wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 入场状态(含段改)
			wTitleCell = new PdfPCell(new Paragraph("入场状态(含段改)", wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加检查项点明细行
	 * 
	 * @param wPdfPTable  表格
	 * @param wNo         序号
	 * @param wContent    内容
	 * @param wMyBaseFont 基本字体
	 */
	private static void AddCheckItemDetailRow(PdfPTable wPdfPTable, String wNo, String wContent, Font wMyBaseFont) {
		try {
			PdfPCell wTitleCell = null;

			wTitleCell = new PdfPCell(new Paragraph(wNo, wMyBaseFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph(wContent, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加检查项点明细行
	 * 
	 * @param wPdfPTable  表格
	 * @param wMyBaseFont 基本字体
	 * @param wValues     值集合
	 */
	private static void AddCheckItemDetailRow(PdfPTable wPdfPTable, Font wMyBaseFont, List<String> wValues) {
		try {
			if (wValues == null || wValues.size() <= 0)
				return;

			PdfPCell wTitleCell = null;
			for (String wValue : wValues) {
				wTitleCell = new PdfPCell(new Paragraph(wValue, wMyBaseFont));
				wTitleCell.setPaddingTop(10);
				wTitleCell.setPaddingBottom(10);
				wTitleCell.setPaddingLeft(10);
				wTitleCell.setPaddingRight(10);
				wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				wPdfPTable.addCell(wTitleCell);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加检查项点明细行
	 * 
	 * @param wPdfPTable  表格
	 * @param wNo         序号
	 * @param wContent    内容
	 * @param wMyBaseFont 基本字体
	 */
	private static void AddCheckItemDetailRow(PdfPTable wPdfPTable, String wNo, String wContent, Font wMyBaseFont,
			String wResult, String wPicPath) {
		try {
			PdfPCell wTitleCell = null;

			wTitleCell = new PdfPCell(new Paragraph(wNo, wMyBaseFont));
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph(wContent, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);

			// 图例
			Image wImage = Image.getInstance(wPicPath);
			wImage.scaleAbsolute(120, 80);
			wTitleCell = new PdfPCell(wImage);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 检查记录
			wTitleCell = new PdfPCell(new Paragraph("有  /  无", wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
			// 结果
			wTitleCell = new PdfPCell(new Paragraph(wResult, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wPdfPTable.addCell(wTitleCell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加检查项点行
	 * 
	 * @param wPdfPTable  表格
	 * @param wMyBaseFont 基本字体
	 */
	private static void AddCheckItemRow(PdfPTable wPdfPTable, String wNo, String wContent, Font wBoldFont) {
		try {
			PdfPCell wTitleCell = null;
			Paragraph wParagraph = null;

			wParagraph = new Paragraph(wNo, wBoldFont);
			wTitleCell = new PdfPCell(wParagraph);
			wTitleCell.setPaddingLeft(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph(wContent, wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("", wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("", wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("", wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			wPdfPTable.addCell(wTitleCell);

			wTitleCell = new PdfPCell(new Paragraph("", wBoldFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			wPdfPTable.addCell(wTitleCell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 给PDF文件添加图片水印
	 * 
	 * @param InPdfFile  要加水印的原pdf文件路径
	 * @param outPdfFile 加了水印后要输出的路径
	 * @param object     水印图片路径
	 * @param pageSize   原pdf文件的总页数（该方法是我当初将数据导入excel中然后再转换成pdf所以我这里的值是用excel的行数计算出来的，
	 *                   如果不是我这种可以 直接用reader.getNumberOfPages()获取pdf的总页数）
	 * @throws Exception
	 */
	public static void addPdfMark(String InPdfFile, String outPdfFile, String readpicturepath) throws Exception {
		try {
			PdfReader reader = new PdfReader(InPdfFile);
			int pageSize = reader.getNumberOfPages();
			PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outPdfFile));
			Image img = Image.getInstance(readpicturepath);// 插入水印
			img.setAbsolutePosition(50, 50);
			for (int i = 1; i <= pageSize; i++) {
				PdfContentByte under = stamp.getUnderContent(i);
				under.addImage(img);
			}
			stamp.close();// 关闭
			File tempfile = new File(InPdfFile);
			if (tempfile.exists()) {
				tempfile.delete();
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 添加文字水印
	 * 
	 * @param inputFile     输入PDF的文件路径
	 * @param outputFile    输出PDF的文件路径
	 * @param waterMarkName 水印文字
	 */
	public static void waterMark(String inputFile, String outputFile, String waterMarkName) {
		int interval = -5;
		try {
			PdfReader reader = new PdfReader(inputFile);
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFile));

			BaseFont base = BaseFont.createFont("C:/windows/fonts/simsun.ttc,1", BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED);
			Rectangle pageRect = null;
			PdfGState gs = new PdfGState();
			// 设置透明度
			gs.setFillOpacity(0.1f);
			gs.setStrokeOpacity(0.4f);
			int total = reader.getNumberOfPages() + 1;

			JLabel label = new JLabel();
			FontMetrics metrics;
			int textH = 0;
			int textW = 0;
			label.setText(waterMarkName);
			metrics = label.getFontMetrics(label.getFont());
			textH = metrics.getHeight();
			textW = metrics.stringWidth(label.getText());

			PdfContentByte under;
			for (int i = 1; i < total; i++) {
				pageRect = reader.getPageSizeWithRotation(i);
				under = stamper.getOverContent(i);
				under.saveState();
				under.setGState(gs);
				under.beginText();
				under.setFontAndSize(base, 20);

				// 水印文字成30度角倾斜
				// 你可以随心所欲的改你自己想要的角度
				for (int height = interval + textH; height < pageRect.getHeight(); height = height + textH * 3) {
					for (int width = interval + textW; width < pageRect.getWidth() + textW; width = width + textW * 2) {
						under.showTextAligned(Element.ALIGN_LEFT, waterMarkName, width - textW, height - textH, 30);
					}
				}
				// 添加水印文字
				under.endText();
			}

			// 一定不要忘记关闭流
			stamper.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 段落格式
	 * 
	 * @param paragraph 段落对象
	 * @param doc       文本上下文
	 * @throws DocumentException 文本异常
	 */
	public static void geshi1(Paragraph paragraph, Document doc) throws DocumentException {// 段落的格式
		try {
			paragraph.setIndentationLeft(30);
			paragraph.setIndentationRight(30);
			paragraph.setFirstLineIndent(20f);
			paragraph.setSpacingAfter(10f);
			paragraph.setSpacingBefore(10f);
			doc.add(paragraph);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 单元格格式(居中无边框)
	 * 
	 * @param cell  单元格
	 * @param table 表格
	 * @throws DocumentException 文本异常
	 */
	public static void geshi2(PdfPCell cell, PdfPTable table) throws DocumentException {// 表格的格式
		try {
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 单元格格式(不居中无边框)
	 * 
	 * @param cell  单元格
	 * @param table 表格
	 * @throws DocumentException 文本异常
	 */
	public static void geshi12(PdfPCell cell, PdfPTable table) throws DocumentException {// 表格的格式
		try {
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 单元格格式(居中有边框)
	 * 
	 * @param cell  单元格
	 * @param table 表格
	 * @throws DocumentException 文本异常
	 */
	public static void geshi22(PdfPCell cell, PdfPTable table) throws DocumentException {// 表格的格式
		try {
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 单元格个格式(居中有边框)
	 * 
	 * @param cell  单元格
	 * @param table 表格
	 * @throws DocumentException 文本异常
	 */
	public static void geshi32(PdfPCell cell, PdfPTable table) throws DocumentException {// 表格的格式
		try {
			cell.setColspan(3);
			cell.setBorder(0);
			table.addCell(cell);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 对doc文件设置页面页脚(页眉页脚的设置一定要在open前设置好)
	 * 
	 * @param wDoc         文件上下文
	 * @param wChineseFont 字体对象
	 * @param wHeaderTitle 标题
	 */
	public static void addHeaderAndFooter(Document wDoc, Font wChineseFont, String wHeaderTitle) {
		try {
			// HeaderFooter的第2个参数为非false时代表打印页码 页眉页脚中也可以加入图片，并非只能是文字
			HeaderFooter wHeader = new HeaderFooter(new Phrase(wHeaderTitle, wChineseFont), false);
			wHeader.setBorder(Rectangle.NO_BORDER);
			wHeader.setBorder(Rectangle.BOTTOM);
			wHeader.setAlignment(1);
			wHeader.setBorderColor(Color.red);
			wDoc.setHeader(wHeader);

			HeaderFooter wFooter = new HeaderFooter(new Phrase("第-", wChineseFont), new Phrase("-页", wChineseFont));
			// 0是靠左 1是居中 2是居右
			wFooter.setAlignment(1);
			wFooter.setBorderColor(Color.red);
			wFooter.setBorder(Rectangle.NO_BORDER);
			wDoc.setFooter(wFooter);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 设置字体
	 * 
	 * @param fonttype  字体类型
	 * @param fontsize  字体大小
	 * @param fontflag  字体标记
	 * @param fontcolor 字体颜色
	 * @param fontstyle 字体样式
	 * @return
	 * @throws DocumentException 文本异常
	 * @throws IOException       IO异常
	 */
	public static Font setfont(String fonttype, float fontsize, int fontflag, Color fontcolor, int fontstyle)
			throws DocumentException, IOException {
		BaseFont baseFont5 = BaseFont.createFont(fonttype, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		Font font = new Font(baseFont5, fontsize, fontflag);
		try {
			font.setColor(fontcolor);
			if (fontstyle != 0) {// 如果传参为0不设置字体
				font.setStyle(fontstyle);
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return font;
	}

	/**
	 * 插入图片
	 * 
	 * @param table   表格
	 * @param image   图片对象
	 * @param picpath 图片路径
	 * @param cell    单元格
	 * @param doc     文本上下文
	 * @throws MalformedURLException 格式异常
	 * @throws IOException           IO异常
	 * @throws DocumentException     文本异常
	 */
	public static void addpicture(PdfPTable table, Image image, String picpath, PdfPCell cell, Document doc)
			throws MalformedURLException, IOException, DocumentException {
		try {
			image = Image.getInstance(picpath);
			cell = new PdfPCell(image);
			geshi2(cell, table);
			doc.add(table);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 首页-固定格式布局
	 * 
	 * @param wCell       单元格
	 * @param wTable      表格
	 * @param wParagraph  段落对象
	 * @param wCorName    公司名称
	 * @param wReportType 报告类型
	 * @param wRepoartNo  报告编号
	 * @param wMyFont     我的字体对象
	 * @param wMyFont3    我的字体对象
	 * @param wDoc        文本上下文
	 * @throws DocumentException 文本异常
	 */
	public static void firstPage(PdfPCell wCell, PdfPTable wTable, Paragraph wParagraph, String wCorName,
			String wReportType, String wRepoartNo, Font wMyFont, Font wMyFont3, Font wUnderlineFont, Document wDoc)
			throws DocumentException {
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy年MM月dd日");

			// 公司名
			wParagraph = new Paragraph(wCorName, wMyFont3);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			// 报告类型
			wParagraph = new Paragraph(wReportType, wMyFont3);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("       ", wMyFont));
			wDoc.add(new Paragraph("       ", wMyFont));
			wDoc.add(new Paragraph("       ", wMyFont));
			wDoc.add(new Paragraph("       ", wMyFont));
			wDoc.add(new Paragraph("       ", wMyFont));

			wTable = new PdfPTable(2);

			wCell = new PdfPCell(new Phrase("报告编号：", wMyFont3));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase(wRepoartNo, wUnderlineFont));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("报告生成时间：", wMyFont3));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase(wSDF.format(new Date()), wUnderlineFont));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("报告生成机构：", wMyFont3));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("广州电力机车", wUnderlineFont));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("报告结论机构：", wMyFont3));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			wTable.addCell(wCell);

			wCell = new PdfPCell(new Phrase("技术中心", wUnderlineFont));
			wCell.setBorder(Rectangle.NO_BORDER);
			wCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			wTable.addCell(wCell);

			wDoc.add(wTable);
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * 输出预检报告
	 * 
	 * @param wReport
	 * @param wOutputStream
	 */
	public static void ReportPreCheck(IPTPreCheckReport wReport, OutputStream wOutputStream) {
		try {
			if (wReport == null || wReport.IPTPreCheckItemList == null || wReport.IPTPreCheckItemList.size() <= 0) {
				return;
			}

			// ①建立com.lowagie.text.Document对象的实例。
			Document wDoc = new Document();
			wDoc.setMargins(20, 20, 30, 30);

			String wFontPath = Constants.getConfigPath() + "simkai.ttf";

			// ②建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
			PdfWriter.getInstance(wDoc, wOutputStream);

			// 设置中文字体
			BaseFont wBaseFont = BaseFont.createFont(wFontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			Font wChineseFont = new Font(wBaseFont, 10, Font.NORMAL);

			// 添加页眉页脚
			addHeaderAndFooter(wDoc, wChineseFont, "机车预检报告");

			// ③打开文档。
			wDoc.open();

			// 基本字体
			Font wMyBaseFont = setfont(wFontPath, 13.0F, 0, Color.BLACK, 0);
			// 表格标题字体
			Font wMyTitleFont = setfont(wFontPath, 18.0F, 0, Color.BLACK, 1);
			// 下划线字体
			Font wUnderlineFont = new Font(wBaseFont, 13.0F, Font.UNDERLINE);
			// 加粗字体
			Font wBoldFont = new Font(wBaseFont, 13.0F, Font.BOLD);

			for (int i = 0; i < wReport.IPTPreCheckItemList.size(); i++) {
				WritePartPoint(i, wReport.IPTPreCheckItemList.get(i), wDoc, wMyBaseFont, wMyTitleFont, wUnderlineFont,
						wBoldFont, wReport.PartNo, wReport.CustomerName);
			}

			// 关闭文档
			wDoc.close();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 预检工序写入
	 */
	private static void WritePartPoint(int wIndex, IPTPreCheckItem wIPTPreCheckItem, Document wDoc, Font wMyBaseFont,
			Font wMyTitleFont, Font wUnderlineFont, Font wBoldFont, String wPartNo, String wCustomerName) {
		try {
			// 新开一页
			wDoc.newPage();
			String wNumber = toChinese(String.valueOf(wIndex + 1));
			// 标题
			Paragraph wParagraph = new Paragraph(StringUtils.Format("第{0}部分  {1}", wNumber, wIPTPreCheckItem.ItemName),
					wMyTitleFont);
			wParagraph.setAlignment(1);
			wDoc.add(wParagraph);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 处理预检项的编号问题
			wIPTPreCheckItem.IPTItemList = handleNumber(wIPTPreCheckItem.IPTItemList);

			// 预检信息
			Paragraph wInfo = new Paragraph(new Chunk("车型号：", wMyBaseFont));
			wInfo.add(new Chunk(wPartNo, wUnderlineFont));
			wInfo.add(new Chunk("    预检人员：", wMyBaseFont));
			wInfo.add(new Chunk(wIPTPreCheckItem.PreChecker, wUnderlineFont));
			wInfo.add(new Chunk("    局段：", wMyBaseFont));
			wInfo.add(new Chunk(wCustomerName, wUnderlineFont));
			wDoc.add(wInfo);

			wDoc.add(new Paragraph("    ", wMyBaseFont));

			// 预检记录表格
			PdfPTable wPdfPTable = new PdfPTable(10);
			wPdfPTable.setWidthPercentage(100);
			wPdfPTable.setWidths(new int[] { 70, 70, 70, 70, 70, 70, 70, 70, 70, 70 });
			// 添加表头行
			AddTitleRow(wMyBaseFont, wPdfPTable);
			// 添加内容行
			for (IPTItem wIPTItem : wIPTPreCheckItem.IPTItemList) {
				AddContentRow(wMyBaseFont, wPdfPTable, wIPTItem, wIPTPreCheckItem.IPTValueList);
			}
			// 添加表格
			wDoc.add(wPdfPTable);

			// 添加备注
			List<String> wRemarks = new ArrayList<String>();
			wRemarks.add("1.检查记录和厂家、型号、编号由各预检人员填写；");
			wRemarks.add("2.如有额外补充项点，请自行申请项填写；");
			wRemarks.add("3.检查项点若有异常，请填写备注。");
			AddRemarks(wDoc, wMyBaseFont, wBoldFont, wRemarks);

			wDoc.add(new Paragraph("    ", wMyBaseFont));
			wDoc.add(new Paragraph("    ", wMyBaseFont));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 添加内容行
	 * 
	 */
	private static void AddContentRow(Font wMyBaseFont, PdfPTable wPdfPTable, IPTItem wIPTItem,
			List<IPTValue> wIPTValueList) {
		try {
			IPTValue wIPTValue = null;
			if (wIPTValueList != null && wIPTValueList.size() > 0) {
				Optional<IPTValue> wOption = wIPTValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst();
				if (wOption.isPresent()) {
					wIPTValue = wOption.get();
				}
			}

			PdfPCell wTitleCell = null;

			// 序号
			wTitleCell = new PdfPCell(new Paragraph(wIPTItem.Code, wMyBaseFont));
			wTitleCell.setPaddingTop(10);
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
			// 检查项点
			wTitleCell = new PdfPCell(new Paragraph(wIPTItem.Text, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
			// 合格标准
			wTitleCell = new PdfPCell(new Paragraph(wIPTItem.Standard, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
			// 图例
			if (StringUtils.isNotEmpty(wIPTItem.Legend)) {
				String wCoreServelUrl = Configuration.readConfigString("core.server.url", "config/config");

				int wIndex = wIPTItem.Legend.lastIndexOf("/");
				String wSuffix = URLEncoder.encode(wIPTItem.Legend.substring(wIndex + 1), "UTF-8");
				String wPreStr = wIPTItem.Legend.substring(0, wIndex + 1);

				String wTL = StringUtils.Format("{0}{1}{2}", wCoreServelUrl, wPreStr, wSuffix);
				Image wImage = Image.getInstance(wTL);
				wImage.scaleAbsolute(40, 40);
				wTitleCell = new PdfPCell(wImage);
			} else {
				wTitleCell = new PdfPCell(new Paragraph("", wMyBaseFont));
			}
			wTitleCell.setPaddingBottom(10);
			wTitleCell.setPaddingTop(10);
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
			// 检查记录
			wTitleCell = new PdfPCell(new Paragraph(wIPTValue == null ? "" : wIPTValue.Value, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
			// 厂家
			wTitleCell = new PdfPCell(new Paragraph(wIPTValue == null ? "" : wIPTValue.Manufactor, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
			// 型号
			wTitleCell = new PdfPCell(new Paragraph(wIPTValue == null ? "" : wIPTValue.Modal, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
			// 编号
			wTitleCell = new PdfPCell(new Paragraph(wIPTValue == null ? "" : wIPTValue.Number, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
			// 异常情况(含段改)
			wTitleCell = new PdfPCell(new Paragraph(wIPTItem.Details, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
			// 备注
			wTitleCell = new PdfPCell(new Paragraph(wIPTValue == null ? "" : wIPTValue.Remark, wMyBaseFont));
			wTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			wTitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			if (wIPTItem.ItemType == IPTItemType.Group.getValue()) {
				wTitleCell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			wPdfPTable.addCell(wTitleCell);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 处理编号问题
	 * 
	 * @param wIPTItemList
	 */
	public static List<IPTItem> handleNumber(List<IPTItem> wIPTItemList) {
		List<IPTItem> wResult = new ArrayList<IPTItem>();
		try {
			if (wIPTItemList == null || wIPTItemList.size() <= 0) {
				return wResult;
			}

			List<IPTItem> wTreeList = IPTServiceImpl.getInstance().IPT_QueryIPTItemTree(null, wIPTItemList).Result;
			setNumber(wTreeList, "");
			wResult = changeTreeToList(wTreeList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 将树形数据转化为扁平数据
	 * 
	 * @return
	 */
	private static List<IPTItem> changeTreeToList(List<IPTItem> wTreeList) {
		List<IPTItem> wResult = new ArrayList<IPTItem>();
		try {
			if (wTreeList == null || wTreeList.size() <= 0) {
				return wResult;
			}

			IPTItem wTemp = null;
			for (IPTItem wIPTItem : wTreeList) {
				wTemp = CloneTool.Clone(wIPTItem, IPTItem.class);
				wTemp.IPTItemList = null;
				wResult.add(wTemp);
				if (wIPTItem.IPTItemList == null || wIPTItem.IPTItemList.size() <= 0) {
					continue;
				} else {
					wResult.addAll(changeTreeToList(wIPTItem.IPTItemList));
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 设置编号
	 * 
	 * @param wTreeList
	 * @param wNo
	 */
	private static void setNumber(List<IPTItem> wTreeList, String wNo) {
		try {
			int wIndex = 1;
			for (IPTItem wIPTItem : wTreeList) {
				if (StringUtils.isEmpty(wNo)) {
					wIPTItem.Code = StringUtils.Format("{0}{1}", wNo, wIndex++);
				} else {
					wIPTItem.Code = StringUtils.Format("{0}.{1}", wNo, wIndex++);
				}
				if (wIPTItem.IPTItemList == null) {
					continue;
				} else {
					setNumber(wIPTItem.IPTItemList, wIPTItem.Code);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 将数字转换为汉字
	 * 
	 * @param wStr
	 * @return
	 */
	public static String toChinese(String wStr) {
		String wResult = "";
		try {
			String[] wS1 = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
			String[] wS2 = { "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千" };
			int wN = wStr.length();
			for (int i = 0; i < wN; i++) {
				int wNum = wStr.charAt(i) - '0';
				if (i != wN - 1 && wNum != 0) {
					wResult += wS1[wNum] + wS2[wN - 2 - i];
				} else {
					wResult += wS1[wNum];
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 非空判断
	 * 
	 * @param a 对象
	 * @return 对象字符串
	 */
	public static String isnull(Object a) {
		if (a != null && a != "" && a != "null" && a.toString() != "null") {
			return a.toString();
		} else {
			return "";
		}
	}
}