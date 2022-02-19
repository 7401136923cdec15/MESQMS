package com.mes.qms.server.serviceimpl.utils.qms;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;

import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.qms.server.service.mesenum.APSShiftPeriod;
import com.mes.qms.server.service.mesenum.BMSDepartmentType;
import com.mes.qms.server.service.mesenum.FPCPartTypes;
import com.mes.qms.server.service.mesenum.IMPResult;
import com.mes.qms.server.service.mesenum.IMPType;
import com.mes.qms.server.service.mesenum.IPTItemType;
import com.mes.qms.server.service.mesenum.IPTMode;
import com.mes.qms.server.service.mesenum.IPTStandardType;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.bms.BMSDepartment;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bms.BMSWorkCharge;
import com.mes.qms.server.service.po.cfg.CFGUnit;
import com.mes.qms.server.service.po.crm.CRMCustomer;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.excel.ExcelLineData;
import com.mes.qms.server.service.po.fmc.FMCLine;
import com.mes.qms.server.service.po.fmc.FMCLineUnit;
import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.fpc.FPCPartPoint;
import com.mes.qms.server.service.po.fpc.FPCProduct;
import com.mes.qms.server.service.po.fpc.FPCRoute;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.po.imp.IMPErrorRecord;
import com.mes.qms.server.service.po.imp.IMPResultRecord;
import com.mes.qms.server.service.po.ipt.IPTGroupInfo;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTStandard;
import com.mes.qms.server.service.po.mss.MSSBOM;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.mss.MSSMaterial;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.CoreServiceImpl;
import com.mes.qms.server.serviceimpl.FMCServiceImpl;
import com.mes.qms.server.serviceimpl.SCMServiceImpl;
import com.mes.qms.server.serviceimpl.WMSServiceImpl;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.imp.IMPErrorRecordDAO;
import com.mes.qms.server.serviceimpl.dao.imp.IMPResultRecordDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTItemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTStandardDAO;
import com.mes.qms.server.serviceimpl.utils.MESServer;
import com.mes.qms.server.utils.qms.ExcelUtil;

/**
 * 本地工具类
 * 
 * @author PengYouWang
 * @CreateTime 2020-2-13 14:21:25
 * @LastEditTime 2020-2-13 14:21:29
 *
 */
public class QMSUtils {
	private static Logger logger = LoggerFactory.getLogger(QMSUtils.class);

	public QMSUtils() {
	}

	private static QMSUtils Instance;

	public static QMSUtils getInstance() {
		if (Instance == null)
			Instance = new QMSUtils();
		return Instance;
	}

	/**
	 * 获取部门列表
	 * 
	 * @param wDepartmentIDs    部门ID集合字符串
	 * @param wBMSDepartmentMap 部门映射
	 * @return 部门列表
	 */
	public List<BMSDepartment> GetDepartmentList(String wDepartmentIDs, Map<Integer, BMSDepartment> wBMSDepartmentMap) {
		List<BMSDepartment> wResult = new ArrayList<BMSDepartment>();
		try {
			if (StringUtils.isEmpty(wDepartmentIDs))
				return wResult;

			if (wBMSDepartmentMap == null || wBMSDepartmentMap.size() <= 0)
				return wResult;

			List<String> wList = StringUtils.splitList(wDepartmentIDs, ",|;");
			if (wList == null || wList.size() <= 0)
				return wResult;

			for (String wID : wList) {
				int wIDItem = Integer.parseInt(wID);
				if (wBMSDepartmentMap.containsKey(wIDItem)) {
					wResult.add(wBMSDepartmentMap.get(wIDItem));
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取人员列表
	 * 
	 * @param wEmployeeIDs    人员ID集合字符串
	 * @param wBMSEmployeeMap 人员映射
	 * @return 人员列表
	 */
	public List<BMSEmployee> GetEmployeeList(String wEmployeeIDs, Map<Integer, BMSEmployee> wBMSEmployeeMap) {
		List<BMSEmployee> wResult = new ArrayList<BMSEmployee>();
		try {
			if (StringUtils.isEmpty(wEmployeeIDs))
				return wResult;

			if (wBMSEmployeeMap == null || wBMSEmployeeMap.size() <= 0)
				return wResult;

			List<String> wList = StringUtils.splitList(wEmployeeIDs, ",|;");
			if (wList == null || wList.size() <= 0)
				return wResult;

			for (String wID : wList) {
				int wIDItem = Integer.parseInt(wID);
				if (wBMSEmployeeMap.containsKey(wIDItem)) {
					wResult.add(wBMSEmployeeMap.get(wIDItem));
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public void AddItemList(ExcelData wExcelData, int wCarTypeLine, List<IPTItem> wIPTItemList) {
		try {
			for (int i = wCarTypeLine + 1; i < wExcelData.sheetData.get(0).lineData.size(); i++) {
				ExcelLineData wLineData = wExcelData.sheetData.get(0).lineData.get(i);
				String wStandardTypeCol = wLineData.colData.get(10);
				String wStandardValueCol = wLineData.colData.get(9);
				String wStandardLeftCol = wLineData.colData.get(11);
				String wStandardRightCol = wLineData.colData.get(12);
				String wUnitCol = wLineData.colData.get(8);
				String wValueSourceCol = wLineData.colData.get(13);
				String wText = wLineData.colData.get(4);
				String wDetail = wLineData.colData.get(5);
				String wDescribe = wLineData.colData.get(6);
				String wTechStandardCol = wLineData.colData.get(7);

				int wStandardType = GetStandardType(wStandardTypeCol);
				String wStandardValue = wStandardValueCol;
				double wStandardLeft = Double.parseDouble(wStandardLeftCol);
				double wStandardRight = Double.parseDouble(wStandardRightCol);
				String wUnit = wUnitCol;
				List<String> wValueSource = GetValueSource(wValueSourceCol);
				String wTechStandard = wTechStandardCol;

				IPTItem wIPTItem = new IPTItem();
				wIPTItem.ID = 0;
				wIPTItem.StandardType = wStandardType;
				wIPTItem.StandardValue = wStandardValue;
				wIPTItem.StandardLeft = wStandardLeft;
				wIPTItem.StandardRight = wStandardRight;
				wIPTItem.Standard = "";
				wIPTItem.Unit = wUnit;
				wIPTItem.Visiable = true;
				wIPTItem.Text = wText;
				wIPTItem.ValueSource = wValueSource;
				wIPTItem.Process = wDetail;
				wIPTItem.Details = wDescribe;

				wIPTItem.Standard = wTechStandard;
				wIPTItem.Standard = GetStandard(wStandardType, wIPTItem);
				wIPTItemList.add(wIPTItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public String GetStandard(int wStandardType, IPTItem wIPTItem) {
		String wResult = "";
		try {
			switch (IPTStandardType.getEnumType(wStandardType)) {
			case Text:
			case Combo:
			case Check:
			case BadReason:
			case ArrayNumber:
			case ArrayText:
				wResult = wIPTItem.StandardValue;
				break;
			case Range:
				wResult = "范围：" + wIPTItem.StandardLeft + "<n<" + wIPTItem.StandardRight;
				break;
			case RangeEQ:
				wResult = "范围：" + wIPTItem.StandardLeft + "≤n≤" + wIPTItem.StandardRight;
				break;
			case RangeLTE:
				wResult = "范围：" + wIPTItem.StandardLeft + "<n≤" + wIPTItem.StandardRight;
				break;
			case RangeGTE:
				wResult = "范围：" + wIPTItem.StandardLeft + "≤n<" + wIPTItem.StandardRight;
				break;
			case LT:
				wResult = "范围：" + "n<" + wIPTItem.StandardRight;
				break;
			case GT:
				wResult = "范围：" + "n>" + wIPTItem.StandardLeft;
				break;
			case LTE:
				wResult = "范围：" + "n≤" + wIPTItem.StandardRight;
				break;
			case GTE:
				wResult = "范围：" + "n≥" + wIPTItem.StandardLeft;
				break;
			case EQ:
				wResult = "n=" + wIPTItem.StandardValue;
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<String> GetValueSource(String wValueSourceCol) {
		List<String> wResult = new ArrayList<String>();
		try {
			if (StringUtils.isEmpty(wValueSourceCol))
				return wResult;

			String wNewValue = wValueSourceCol.replaceAll("；", ";");
			if (wNewValue.contains(";")) {
				String[] wStrings = wNewValue.split(";");
				for (String wItem : wStrings) {
					wResult.add(wItem);
				}
			} else if (wNewValue.contains("||")) {
				String[] wStrings = wNewValue.split("\\|\\|");
				for (String wItem : wStrings) {
					wResult.add(wItem);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public int GetStandardType(String wStandardTypeCol) {
		int wResult = 0;
		try {
			switch (wStandardTypeCol) {
			case "单选":
				wResult = IPTStandardType.Combo.getValue();
				break;
			case "多选":
				wResult = IPTStandardType.Check.getValue();
				break;
			case "小于等于":
				wResult = IPTStandardType.LTE.getValue();
				break;
			case "大于等于":
				wResult = IPTStandardType.GTE.getValue();
				break;
			case "小于":
				wResult = IPTStandardType.LT.getValue();
				break;
			case "大于":
				wResult = IPTStandardType.GT.getValue();
				break;
			case "等于":
				wResult = IPTStandardType.EQ.getValue();
				break;
			case "左包区间":
				wResult = IPTStandardType.RangeLTE.getValue();
				break;
			case "全包区间":
				wResult = IPTStandardType.RangeEQ.getValue();
				break;
			case "右包区间":
				wResult = IPTStandardType.RangeGTE.getValue();
				break;
			case "全开区间":
				wResult = IPTStandardType.Range.getValue();
				break;
			case "文本":
				wResult = IPTStandardType.Text.getValue();
				break;
			case "数字":
				wResult = IPTStandardType.Number.getValue();
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public int GetCarTypeLine(ExcelData wExcelData) {
		int wResult = 0;
		try {
			int wFlag = 0;
			for (ExcelLineData wExcelLineData : wExcelData.sheetData.get(0).lineData) {
				if (wExcelLineData.colData.get(0).equals("车型")) {
					return wFlag;
				}
				wFlag++;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public int GetDayShiftID(BMSEmployee wLoginUser, Calendar wTime) {
		int wResult = 0;
		try {
			wResult = MESServer.MES_QueryShiftID(wLoginUser, 0, wTime, APSShiftPeriod.Day, 0);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<Integer> GetYJStationIDList(BMSEmployee wLoginUser) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			List<FPCPart> wPartList = FMCServiceImpl.getInstance()
					.FPC_QueryPartList(wLoginUser, -1, -1, -1, FPCPartTypes.PrevCheck.getValue()).List(FPCPart.class);

			if (wPartList == null || wPartList.size() <= 0) {
				return wResult;
			} else {
				wResult = wPartList.stream().map(p -> p.ID).collect(Collectors.toList());
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 导入质量项点
	 * 
	 * @param wExcelData
	 */
	public void IPT_ImportQTXJ(BMSEmployee wLoginUser, ExcelData wExcelData, int wProductID, int wLineID) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSDF.format(Calendar.getInstance().getTime());

			DecimalFormat wDecimalFormat = new DecimalFormat("0000");
			int wFlag = 1;

			List<IPTStandard> wStandardList = new ArrayList<IPTStandard>();
			List<IPTItem> wIPTItemList = new ArrayList<IPTItem>();
			IPTStandard wIPTStandard = null;
			for (int i = 1; i < wExcelData.sheetData.get(0).lineData.size(); i++) {
				if (wExcelData.getSheetData().get(0).getLineData().get(i).getColData().stream()
						.allMatch(p -> StringUtils.isEmpty(p))) {
					continue;
				}

				int wIndex = i;
				if (wStandardList.stream()
						.anyMatch(p -> p.PartID == QMSConstants
								.GetFPCPart(wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(0)).ID
								&& p.PartPointID == QMSConstants.GetFPCStep(
										wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(1)).ID)) {
					wIPTStandard = wStandardList.stream()
							.filter(p -> p.PartID == QMSConstants
									.GetFPCPart(wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(0)).ID
									&& p.PartPointID == QMSConstants.GetFPCStep(
											wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(1)).ID)
							.findFirst().get();
				} else {
					wIPTStandard = new IPTStandard();
					wIPTStandard.ProductID = wProductID;
					wIPTStandard.ProductNo = QMSConstants.GetFPCProductNo(wProductID);
					wIPTStandard.LineID = wLineID;
					wIPTStandard.IPTMode = IPTMode.QTXJ.getValue();
					wIPTStandard.UserID = wLoginUser.ID;
					wIPTStandard.PartID = QMSConstants
							.GetFPCPart(wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(0)).ID;
					wIPTStandard.PartPointID = QMSConstants
							.GetFPCStep(wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(1)).ID;
					wIPTStandard.Remark = StringUtils.Format("【{0}-{1}-{2}-{3}】-{4}-质量标准",
							QMSConstants.GetFPCProductNo(wProductID), QMSConstants.GetFMCLineName(wLineID),
							wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(0),
							wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(1), wCurTime);
					long wID = IPTStandardDAO.getInstance().InsertStandard(wLoginUser, wIPTStandard, wErrorCode).Result;
					wIPTStandard.ID = wID;
					wStandardList.add(wIPTStandard);
				}
				// 项点级数
				int wLevel = wExcelData.sheetData.get(0).lineData.get(0).colSum - 11;
				if (wExcelData.sheetData.get(0).lineData.get(0).colSum == 22) {
					wLevel = wExcelData.sheetData.get(0).lineData.get(0).colSum - 17;
				}
				List<String> wColData = wExcelData.sheetData.get(0).lineData.get(wIndex).colData;
				// 检查标准
				String wStandard = wColData.get(wLevel + 2);
				// 项目号
				String wProjectNo = wColData.get(wLevel + 3);
				// 检测点
				String wCheckPoint = wColData.get(wLevel + 4);
				// 值类型
				String wType = wColData.get(wLevel + 5);
				// 单位
				String wUnit = wColData.get(wLevel + 6);
				// 填写值必填
				String wValueFill = wColData.get(wLevel + 7);
				// 选项列表
				String wOptionList = wColData.get(wLevel + 8);
				// 图片必填
				String wPictureFill = wColData.get(wLevel + 9);
				// 视频必填
				String wVideoFill = wColData.get(wLevel + 10);
				// 预设厂家
				String wYSCJ = wColData.size() > wLevel + 11 ? wColData.get(wLevel + 11) : "";
				// 预设型号
				String wYSXH = wColData.size() > wLevel + 12 ? wColData.get(wLevel + 12) : "";
				// 厂家必填
				String wCJBT = wColData.size() > wLevel + 13 ? wColData.get(wLevel + 13) : "";
				// 型号必填
				String wXHBT = wColData.size() > wLevel + 14 ? wColData.get(wLevel + 14) : "";
				// 厂家选项
				String wCJXX = wColData.size() > wLevel + 15 ? wColData.get(wLevel + 15) : "";
				// 型号选项
				String wXHXX = wColData.size() > wLevel + 16 ? wColData.get(wLevel + 16) : "";

				// 行最大项级数
				IPTStandard wTempStandard = wIPTStandard;
				int wMaxLevel = GetLineMaxLevel(2, wExcelData.sheetData.get(0).lineData.get(wIndex), wLevel);
				for (int j = 2; j < wMaxLevel + 2; j++) {
					int wJIndex = j;
					if (j == wMaxLevel + 1) {
						IPTItem wIPTItem = new IPTItem();
						wIPTItem.ID = 0;
						wIPTItem.ItemType = IPTItemType.Write.getValue();
						wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
						wIPTItem.Text = wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(j);
						if (wMaxLevel == 1) {
							wIPTItem.GroupID = 0;
						} else {
							List<IPTItem> wTempIPTList = wIPTItemList.stream()
									.filter(p -> p.ItemType == IPTItemType.Group.getValue()
											&& (int) p.VID == wTempStandard.ID
											&& p.Text.equals(wExcelData.sheetData.get(0).lineData.get(wIndex).colData
													.get(wJIndex - 1)))
									.collect(Collectors.toList());
							wIPTItem.GroupID = (int) wTempIPTList.get(wTempIPTList.size() - 1).ID;
						}
						wIPTItem.VID = (int) wTempStandard.ID;
						wIPTItem.Standard = wStandard;
						wIPTItem.StandardType = GetStandardType(wType);
						wIPTItem.IsWriteFill = GetIsWriteFill(wValueFill);
						wIPTItem.Unit = wUnit;
						wIPTItem.ValueSource = GetValueSource(wOptionList);
						wIPTItem.IsPictureFill = GetIsPictureFill(wPictureFill);
						wIPTItem.IsVideoFill = GetIsVideoFill(wVideoFill);
						wIPTItem.UnitID = GetUnitID(wUnit);
						wIPTItem.ProjectNo = wProjectNo;
						wIPTItem.CheckPoint = wCheckPoint;
						// 新增兼容属性
						wIPTItem.DefaultManufactor = wYSCJ;
						wIPTItem.DefaultModal = wYSXH;
						wIPTItem.IsManufactorFill = GetIsPictureFill(wCJBT);
						wIPTItem.IsModalFill = GetIsPictureFill(wXHBT);
						wIPTItem.ManufactorOption = GetValueSource(wCJXX);
						wIPTItem.ModalOption = GetValueSource(wXHXX);

						long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, wTempStandard.ID, wIPTItem,
								wErrorCode);
						wIPTItem.ID = wItemID;
						wIPTItemList.add(wIPTItem);
					} else {
						int wGroupID = 0;
						List<IPTItem> wTempList = wIPTItemList.stream()
								.filter(q -> q.ItemType == IPTItemType.Group.getValue()
										&& (int) q.VID == wTempStandard.ID
										&& q.Text.equals(wExcelData.sheetData.get(0).lineData.get(wIndex).colData
												.get(wJIndex - 1)))
								.collect(Collectors.toList());
						if (wTempList != null && wTempList.size() > 0) {
							wGroupID = (int) wTempList.get(wTempList.size() - 1).ID;
						}
						int wTempGroupID = wGroupID;
						if (wIPTItemList.stream().anyMatch(p -> p.VID == wTempStandard.ID
								&& p.Text.equals(wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wJIndex))
								&& p.ItemType == IPTItemType.Group.getValue() && p.GroupID == wTempGroupID)) {
							continue;
						} else {
							IPTItem wIPTItem = new IPTItem();
							wIPTItem.VID = (int) wTempStandard.ID;
							wIPTItem.Text = wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wJIndex);
							wIPTItem.ItemType = IPTItemType.Group.getValue();
							if (j == 2) {
								wIPTItem.GroupID = 0;
							} else {
								wIPTItem.GroupID = (int) wIPTItemList.stream()
										.filter(p -> p.ItemType == IPTItemType.Group.getValue()
												&& (int) p.VID == wTempStandard.ID
												&& p.Text
														.equals(wExcelData.sheetData.get(0).lineData.get(wIndex).colData
																.get(wJIndex - 1)))
										.findFirst().get().ID;
							}
							wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
							long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, wTempStandard.ID, wIPTItem,
									wErrorCode);
							wIPTItem.ID = wItemID;
							wIPTItemList.add(wIPTItem);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private int GetUnitID(String wUnit) {
		int wResult = 0;
		try {
			wResult = QMSConstants.GetCFGUnit(wUnit).ID;

			// 若没有单位就添加

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private int GetIsVideoFill(String wVideoFill) {
		int wResult = 0;
		try {
			switch (wVideoFill) {
			case "是":
				wResult = 1;
				break;
			case "否":
				wResult = 2;
				break;
			case "不显示":
				wResult = 3;
				break;
			default:
				wResult = 3;
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private int GetIsPictureFill(String wPictureFill) {
		int wResult = 0;
		try {
			switch (wPictureFill) {
			case "是":
				wResult = 1;
				break;
			case "否":
				wResult = 2;
				break;
			case "不显示":
				wResult = 3;
				break;
			default:
				wResult = 3;
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private int GetIsWriteFill(String wValueFill) {
		int wResult = 0;
		try {
			switch (wValueFill) {
			case "是":
				wResult = 1;
				break;
			case "否":
				wResult = 2;
				break;
			case "不显示":
				wResult = 3;
				break;
			default:
				wResult = 3;
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private int GetLineMaxLevel(int wIndex, ExcelLineData wExcelLineData, int wLevel) {
		int wResult = 0;
		try {
			for (int i = wIndex; i < wLevel + wIndex; i++) {
				if (StringUtils.isEmpty(wExcelLineData.colData.get(i))) {
					wResult = i - wIndex;
					return wResult;
				}
			}
			wResult = wLevel;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 导入预检项点
	 * 
	 * @param wExcelData
	 */
	public List<Integer> IPT_ImportPreCheck(BMSEmployee wLoginUser, ExcelData wExcelData, int wProductID, int wLineID,
			int wIPTMode, int wStationID, int wCustomerID, List<FPCPartPoint> wStepList,
			IMPResultRecord wIMPResultRecord) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			int wPartID = wStationID;

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSDF.format(Calendar.getInstance().getTime());

			int wFlag = 1;

			// 组信息
			List<IPTGroupInfo> wIPTGroupInfoList = new ArrayList<IPTGroupInfo>();

			List<IPTStandard> wStandardList = new ArrayList<IPTStandard>();
			IPTStandard wIPTStandard = null;
			for (int i = 1; i < wExcelData.sheetData.get(0).lineData.size(); i++) {
				int wIndex = i;

				String wPartPointName = wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(0);
				int wPartPointID = 0;
				if (wStepList.stream().anyMatch(p -> p.Name.equals(wPartPointName))) {
					wPartPointID = wStepList.stream().filter(p -> p.Name.equals(wPartPointName)).findFirst().get().ID;
				}
				int wStepID = wPartPointID;

				if (wStandardList.stream().anyMatch(p -> p.PartPointID == wStepID)) {
					wIPTStandard = wStandardList.stream().filter(p -> p.PartPointID == wStepID).findFirst().get();
				} else {
					wIPTStandard = new IPTStandard();
					wIPTStandard.ProductID = wProductID;
					wIPTStandard.ProductNo = QMSConstants.GetFPCProductNo(wProductID);
					wIPTStandard.LineID = wLineID;
					wIPTStandard.IPTMode = wIPTMode;
					wIPTStandard.UserID = wLoginUser.ID;
					wIPTStandard.PartID = wPartID;
					wIPTStandard.CustomID = wCustomerID;
					wIPTStandard.PartPointID = QMSConstants
							.GetFPCStep(wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(0)).ID;
					wIPTStandard.Remark = StringUtils.Format("【{0}-{1}-{2}】-{3}-{4}标准",
							QMSConstants.GetFPCProductNo(wProductID), QMSConstants.GetFMCLineName(wLineID),
							wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(0), wCurTime,
							wIPTMode == IPTMode.PreCheck.getValue() ? "预检"
									: wIPTMode == IPTMode.QTXJ.getValue() ? "过程" : "质量");
					long wID = IPTStandardDAO.getInstance().InsertStandard(wLoginUser, wIPTStandard, wErrorCode).Result;
					wIPTStandard.ID = wID;
					wStandardList.add(wIPTStandard);
					wResult.add((int) wID);
				}
				// 项点级数
				int wLevel = 5;
				// 检查标准
				String wStandard = wLevel + 1 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 1)
						: "";
				// 预设厂家
				String wPreFactor = wLevel + 2 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 2)
						: "";
				// 预设型号
				String wPreModal = wLevel + 3 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 3)
						: "";
				// 厂家必填
				String wFactorFill = wLevel + 4 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 4)
						: "";
				// 型号必填
				String wModalFill = wLevel + 5 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 5)
						: "";
				// 编号必填
				String wNumberFill = wLevel + 6 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 6)
						: "";
				// 填写值类型
				String wType = wLevel + 7 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 7)
						: "";
				// 填写值必填
				String wValueFill = wLevel + 8 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 8)
						: "";
				// 单位
				String wUnit = wLevel + 9 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 9)
						: "";
				// 填写值选项列表
				String wOptionList = wLevel + 10 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 10)
						: "";
				// 厂家选项
				String wFactorList = wLevel + 11 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 11)
						: "";
				// 型号选项
				String wModalList = wLevel + 12 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 12)
						: "";
				// 预设处理工位
				String wPreStation = wLevel + 13 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 13)
						: "";
				// 预设处理工序
				String wPrePartPoint = wLevel + 14 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 14)
						: "";
				// 图片必填
				String wPictureFill = wLevel + 16 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 16)
						: "";
				// 视频必填
				String wVideoFill = wLevel + 17 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 17)
						: "";
				// 标准值
				String wStandardValue = wLevel + 18 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 18)
						: "";
				// 上限值
				String wLeft = wLevel + 19 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 19)
						: "";
				// 下限值
				String wRight = wLevel + 20 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 20)
						: "";

				// 行最大项级数
				IPTStandard wTempStandard = wIPTStandard;
				int wMaxLevel = GetLineMaxLevel(1, wExcelData.sheetData.get(0).lineData.get(wIndex), wLevel);

				// 创建项点和组
				List<String> wColDataList = wExcelData.sheetData.get(0).lineData.get(wIndex).colData;
				this.IPT_CreateItemAndGroup(wLoginUser, wColDataList, wMaxLevel, wIPTGroupInfoList, wIPTMode, wStandard,
						wPreFactor, wPreModal, wFactorFill, wModalFill, wNumberFill, wType, wValueFill, wUnit,
						wOptionList, wFactorList, wModalList, wPreStation, wPrePartPoint, wPictureFill, wVideoFill,
						wTempStandard.ID, wFlag, wStandardValue, wLeft, wRight);

				if (i % 100 == 0 || i == wExcelData.sheetData.get(0).lineData.size() - 1) {
					wIMPResultRecord.Progress = i;
					IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 创建项点和组
	 */
	private void IPT_CreateItemAndGroup(BMSEmployee wLoginUser, List<String> wColDataList, int wMaxLevel,
			List<IPTGroupInfo> wIPTGroupInfoList, int wIPTMode, String wStandard, String wPreFactor, String wPreModal,
			String wFactorFill, String wModalFill, String wNumberFill, String wType, String wValueFill, String wUnit,
			String wOptionList, String wFactorList, String wModalList, String wPreStation, String wPrePartPoint,
			String wPictureFill, String wVideoFill, long wVID, int wFlag, String wStandardValue, String wLeft,
			String wRight) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSDF.format(Calendar.getInstance().getTime());

			DecimalFormat wDecimalFormat = new DecimalFormat("0000");

			String wStepName = wColDataList.get(0);

			int wItemType = 0;

			int wIsPeriordChange = 0;

			switch (IPTMode.getEnumType(wIPTMode)) {
			case PreCheck:// 预检项点
				wItemType = IPTItemType.InPlant.getValue();
				break;
			case QTXJ:// 质量项点
			case Quality:
			case OutCheck:
				wItemType = IPTItemType.Write.getValue();
				break;
			case PeriodChange:
				wIsPeriordChange = 1;
				wItemType = IPTItemType.Write.getValue();
				break;
			default:
				break;
			}

			switch (wMaxLevel) {
			case 1: {
				String wItem1 = wColDataList.get(1);
				AddItem(wLoginUser, wColDataList, wStandard, wPreFactor, wPreModal, wFactorFill, wModalFill,
						wNumberFill, wType, wValueFill, wUnit, wOptionList, wFactorList, wModalList, wPreStation,
						wPrePartPoint, wPictureFill, wVideoFill, wVID, wFlag, wErrorCode, wCurTime, wDecimalFormat,
						wItemType, 0, wItem1, wStandardValue, wLeft, wRight, wIsPeriordChange);
			}
				break;
			case 2: {
				String wItem1 = wColDataList.get(1);
				String wItem2 = wColDataList.get(2);

				long wItemID = 0;
				if (wIPTGroupInfoList.stream()
						.anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 1 && p.OneItem.equals(wItem1))) {
					wItemID = wIPTGroupInfoList.stream()
							.filter(p -> p.StepName.equals(wStepName) && p.Level == 1 && p.OneItem.equals(wItem1))
							.findFirst().get().GroupID;
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem1;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = 0;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 1, wItem1, "", "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);
				}
				// 添加项
				AddItem(wLoginUser, wColDataList, wStandard, wPreFactor, wPreModal, wFactorFill, wModalFill,
						wNumberFill, wType, wValueFill, wUnit, wOptionList, wFactorList, wModalList, wPreStation,
						wPrePartPoint, wPictureFill, wVideoFill, wVID, wFlag, wErrorCode, wCurTime, wDecimalFormat,
						wItemType, (int) wItemID, wItem2, wStandardValue, wLeft, wRight, wIsPeriordChange);
			}
				break;
			case 3: {
				String wItem1 = wColDataList.get(1);
				String wItem2 = wColDataList.get(2);
				String wItem3 = wColDataList.get(3);
				// 创建一级组
				IPTGroupInfo wIPTGroupInfo1 = null;
				if (wIPTGroupInfoList.stream()
						.anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 1 && p.OneItem.equals(wItem1))) {
					wIPTGroupInfo1 = wIPTGroupInfoList.stream()
							.filter(p -> p.StepName.equals(wStepName) && p.Level == 1 && p.OneItem.equals(wItem1))
							.findFirst().get();
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem1;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = 0;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 1, wItem1, "", "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo1 = wInfo;
				}
				// 创建二级组
				IPTGroupInfo wIPTGroupInfo2 = null;
				if (wIPTGroupInfoList.stream().anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 2
						&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2))) {
					wIPTGroupInfo2 = wIPTGroupInfoList.stream().filter(p -> p.StepName.equals(wStepName) && p.Level == 2
							&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2)).findFirst().get();
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem2;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = wIPTGroupInfo1.GroupID;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 2, wItem1, wItem2, "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo2 = wInfo;
				}
				// 创建三级项
				AddItem(wLoginUser, wColDataList, wStandard, wPreFactor, wPreModal, wFactorFill, wModalFill,
						wNumberFill, wType, wValueFill, wUnit, wOptionList, wFactorList, wModalList, wPreStation,
						wPrePartPoint, wPictureFill, wVideoFill, wVID, wFlag, wErrorCode, wCurTime, wDecimalFormat,
						wItemType, wIPTGroupInfo2.GroupID, wItem3, wStandardValue, wLeft, wRight, wIsPeriordChange);
			}
				break;
			case 4: {
				String wItem1 = wColDataList.get(1);
				String wItem2 = wColDataList.get(2);
				String wItem3 = wColDataList.get(3);
				String wItem4 = wColDataList.get(4);
				// 创建一级组
				IPTGroupInfo wIPTGroupInfo1 = null;
				if (wIPTGroupInfoList.stream()
						.anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 1 && p.OneItem.equals(wItem1))) {
					wIPTGroupInfo1 = wIPTGroupInfoList.stream()
							.filter(p -> p.StepName.equals(wStepName) && p.Level == 1 && p.OneItem.equals(wItem1))
							.findFirst().get();
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem1;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = 0;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 1, wItem1, "", "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo1 = wInfo;
				}
				// 创建二级组
				IPTGroupInfo wIPTGroupInfo2 = null;
				if (wIPTGroupInfoList.stream().anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 2
						&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2))) {
					wIPTGroupInfo2 = wIPTGroupInfoList.stream().filter(p -> p.StepName.equals(wStepName) && p.Level == 2
							&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2)).findFirst().get();
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem2;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = wIPTGroupInfo1.GroupID;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 2, wItem1, wItem2, "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo2 = wInfo;
				}
				// 创建三级组
				IPTGroupInfo wIPTGroupInfo3 = null;
				if (wIPTGroupInfoList.stream().anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 3
						&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2) && p.ThreeItem.equals(wItem3))) {
					wIPTGroupInfo3 = wIPTGroupInfoList.stream().filter(p -> p.StepName.equals(wStepName) && p.Level == 3
							&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2) && p.ThreeItem.equals(wItem3))
							.findFirst().get();
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem3;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = wIPTGroupInfo2.GroupID;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 3, wItem1, wItem2, wItem3, "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo3 = wInfo;
				}
				// 创建四级项
				AddItem(wLoginUser, wColDataList, wStandard, wPreFactor, wPreModal, wFactorFill, wModalFill,
						wNumberFill, wType, wValueFill, wUnit, wOptionList, wFactorList, wModalList, wPreStation,
						wPrePartPoint, wPictureFill, wVideoFill, wVID, wFlag, wErrorCode, wCurTime, wDecimalFormat,
						wItemType, wIPTGroupInfo3.GroupID, wItem4, wStandardValue, wLeft, wRight, wIsPeriordChange);
			}
				break;
			case 5: {
				String wItem1 = wColDataList.get(1);
				String wItem2 = wColDataList.get(2);
				String wItem3 = wColDataList.get(3);
				String wItem4 = wColDataList.get(4);
				String wItem5 = wColDataList.get(5);
				// 创建一级组
				IPTGroupInfo wIPTGroupInfo1 = null;
				if (wIPTGroupInfoList.stream()
						.anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 1 && p.OneItem.equals(wItem1))) {
					wIPTGroupInfo1 = wIPTGroupInfoList.stream()
							.filter(p -> p.StepName.equals(wStepName) && p.Level == 1 && p.OneItem.equals(wItem1))
							.findFirst().get();
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem1;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = 0;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 1, wItem1, "", "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo1 = wInfo;
				}
				// 创建二级组
				IPTGroupInfo wIPTGroupInfo2 = null;
				if (wIPTGroupInfoList.stream().anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 2
						&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2))) {
					wIPTGroupInfo2 = wIPTGroupInfoList.stream().filter(p -> p.StepName.equals(wStepName) && p.Level == 2
							&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2)).findFirst().get();
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem2;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = wIPTGroupInfo1.GroupID;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 2, wItem1, wItem2, "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo2 = wInfo;
				}
				// 创建三级组
				IPTGroupInfo wIPTGroupInfo3 = null;
				if (wIPTGroupInfoList.stream().anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 3
						&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2) && p.ThreeItem.equals(wItem3))) {
					wIPTGroupInfo3 = wIPTGroupInfoList.stream().filter(p -> p.StepName.equals(wStepName) && p.Level == 3
							&& p.OneItem.equals(wItem1) && p.TwoItem.equals(wItem2) && p.ThreeItem.equals(wItem3))
							.findFirst().get();
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem3;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = wIPTGroupInfo2.GroupID;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 3, wItem1, wItem2, wItem3, "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo3 = wInfo;
				}
				// 创建四级组
				IPTGroupInfo wIPTGroupInfo4 = null;
				if (wIPTGroupInfoList.stream()
						.anyMatch(p -> p.StepName.equals(wStepName) && p.Level == 4 && p.OneItem.equals(wItem1)
								&& p.TwoItem.equals(wItem2) && p.ThreeItem.equals(wItem3)
								&& p.FourItem.equals(wItem4))) {
					wIPTGroupInfo4 = wIPTGroupInfoList.stream()
							.filter(p -> p.StepName.equals(wStepName) && p.Level == 4 && p.OneItem.equals(wItem1)
									&& p.TwoItem.equals(wItem2) && p.ThreeItem.equals(wItem3)
									&& p.FourItem.equals(wItem4))
							.findFirst().get();
				} else {
					IPTItem wIPTItem = new IPTItem();
					wIPTItem.VID = (int) wVID;
					wIPTItem.Text = wItem4;
					wIPTItem.ItemType = IPTItemType.Group.getValue();
					wIPTItem.GroupID = wIPTGroupInfo3.GroupID;
					wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
					long wItemID = IPTItemDAO.getInstance().Update(wLoginUser, (int) wVID, wIPTItem, wErrorCode);
					// 添加到组信息列表
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 4, wItem1, wItem2, wItem3, wItem4, (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo4 = wInfo;
				}
				// 创建五级项
				AddItem(wLoginUser, wColDataList, wStandard, wPreFactor, wPreModal, wFactorFill, wModalFill,
						wNumberFill, wType, wValueFill, wUnit, wOptionList, wFactorList, wModalList, wPreStation,
						wPrePartPoint, wPictureFill, wVideoFill, wVID, wFlag, wErrorCode, wCurTime, wDecimalFormat,
						wItemType, wIPTGroupInfo4.GroupID, wItem5, wStandardValue, wLeft, wRight, wIsPeriordChange);
			}
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	private void AddItem(BMSEmployee wLoginUser, List<String> wColDataList, String wStandard, String wPreFactor,
			String wPreModal, String wFactorFill, String wModalFill, String wNumberFill, String wType,
			String wValueFill, String wUnit, String wOptionList, String wFactorList, String wModalList,
			String wPreStation, String wPrePartPoint, String wPictureFill, String wVideoFill, long wVID, int wFlag,
			OutResult<Integer> wErrorCode, String wCurTime, DecimalFormat wDecimalFormat, int wItemType, int wGroupID,
			String wText, String wStandardValue, String wLeft, String wRight, int wIsPeriodChange) {
		try {
			IPTItem wIPTItem = new IPTItem();
			wIPTItem.ID = 0;
			wIPTItem.ItemType = wItemType;
			wIPTItem.Code = wCurTime + "-" + wDecimalFormat.format(wFlag++);
			wIPTItem.Text = wText;
			wIPTItem.GroupID = wGroupID;
			wIPTItem.VID = (int) wVID;
			wIPTItem.Standard = wStandard;
			wIPTItem.StandardType = GetStandardType(wType);
			wIPTItem.IsWriteFill = GetIsWriteFill(wValueFill);
			wIPTItem.Unit = wUnit;
			wIPTItem.ValueSource = GetValueSource(wOptionList);
			wIPTItem.IsPictureFill = GetIsPictureFill(wPictureFill);
			wIPTItem.IsVideoFill = GetIsVideoFill(wVideoFill);
			wIPTItem.UnitID = GetUnitID(wUnit);
			wIPTItem.DefaultManufactor = wPreFactor;
			wIPTItem.DefaultModal = wPreModal;
			wIPTItem.IsManufactorFill = GetIsPictureFill(wFactorFill);
			wIPTItem.IsModalFill = GetIsPictureFill(wModalFill);
			wIPTItem.IsNumberFill = GetIsPictureFill(wNumberFill);
			wIPTItem.ManufactorOption = GetValueSource(wFactorList);
			wIPTItem.ModalOption = GetValueSource(wModalList);
			wIPTItem.DefaultStationID = QMSConstants.GetFPCPart(wPreStation).ID;
			wIPTItem.DefaultPartPointID = QMSConstants.GetFPCStep(wPrePartPoint).ID;
			wIPTItem.StandardValue = wStandardValue;
			wIPTItem.StandardLeft = StringUtils.isEmpty(wLeft) ? 0.0 : Double.parseDouble(wLeft);
			wIPTItem.StandardRight = StringUtils.isEmpty(wRight) ? 0.0 : Double.parseDouble(wRight);
			wIPTItem.IsPeriodChange = wIsPeriodChange;

			IPTItemDAO.getInstance().Update(wLoginUser, wIPTItem.VID, wIPTItem, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 导入工序清单
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	public Integer IPT_ImportPartPoint(BMSEmployee wLoginUser, ExcelData wExcelData) {
		int wResult = 0;
		try {
			if (wExcelData == null || wExcelData.sheetData == null || wExcelData.sheetData.size() <= 0
					|| wExcelData.sheetData.get(0).lineData == null
					|| wExcelData.sheetData.get(0).lineData.size() <= 0) {
				return wResult;
			}

			// BOM列表
			List<MSSBOM> wBOMList = WMSServiceImpl.getInstance().MSS_QueryBOMAll(wLoginUser, "", "", -1, -1, -1, -1)
					.List(MSSBOM.class);
			// 产线单元明细
			List<FMCLineUnit> wLineUnitList = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(wLoginUser, -1, -1, -1, false).List(FMCLineUnit.class);
			// 工位列表
			List<FPCPart> wPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(wLoginUser, -1, -1, -1, -1)
					.List(FPCPart.class);
			// 工序列表
			List<FPCPartPoint> wPartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryPartPointList(wLoginUser, -1, -1, -1).List(FPCPartPoint.class);
			// 局段列表
			List<CRMCustomer> wCustomerList = SCMServiceImpl.getInstance()
					.CRM_QueryCustomerList(wLoginUser, "", -1, -1, -1, -1).List(CRMCustomer.class);
			// 班组工位列表
			List<BMSWorkCharge> wWorkChargeList = CoreServiceImpl.getInstance()
					.BMS_QueryWorkChargeList(wLoginUser, -1, -1, -1).List(BMSWorkCharge.class);
			// 修程列表
			List<FMCLine> wLineList = FMCServiceImpl.getInstance().FMC_QueryLineList(wLoginUser, -1, -1, -1)
					.List(FMCLine.class);
			// 车型列表
			List<FPCProduct> wProductList = FMCServiceImpl.getInstance().FPC_QueryProductList(wLoginUser, -1, -1)
					.List(FPCProduct.class);
			// 部门列表
			List<BMSDepartment> wDepartmentList = CoreServiceImpl.getInstance().BMS_QueryDepartmentList(wLoginUser)
					.List(BMSDepartment.class);

			List<ExcelLineData> wExcelLineDataList = wExcelData.sheetData.get(0).lineData;
			int wMinCount = 14;
			String wBOMNO = "";
			String wMaterialName = "";
			String wMaterialNo = "";
			String wProductNo = "";
			String wLineName = "";
			String wCustomerName = "";
			String wPartName = "";
			String wPartPointName = "";
			String wPartPointNumber = "";
			String wClassName = "";
			String wPartPointCode = "";
			for (int i = 1; i < wExcelLineDataList.size(); i++) {
				// BOM编码
				wBOMNO = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(0)
						: "";
				// BOM名称
				wMaterialName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(1)
						: "";
				// 物料编码
				wMaterialNo = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(2)
						: "";
				// 车型
				wProductNo = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(3)
						: "";
				// 修程
				wLineName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(4)
						: "";
				// 局段
				wCustomerName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(5)
						: "";
				// 工位
				wPartName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(6)
						: "";
				// 工序
				wPartPointName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(7)
						: "";
				// 工序号
				wPartPointNumber = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(8)
						: "";
				// 班组
				wClassName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(13)
						: "";
				// 内部工序ID
				wPartPointCode = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(14)
						: "";

				// 处理列数据
				this.HandlePartPointRow(wLoginUser, wBOMNO, wMaterialName, wMaterialNo, wProductNo, wLineName,
						wCustomerName, wPartName, wPartPointName, wPartPointNumber, wClassName, wPartPointCode,
						wBOMList, wLineUnitList, wLineList, wPartList, wPartPointList, wCustomerList, wWorkChargeList,
						wProductList, wDepartmentList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 处理列数据(导入工序清单)
	 * 
	 */
	private void HandlePartPointRow(BMSEmployee wLoginUser, String wBOMNO, String wMaterialName, String wMaterialNo,
			String wProductNo, String wLineName, String wCustomerName, String wPartName, String wPartPointName,
			String wPartPointNumber, String wClassName, String wPartPointCode, List<MSSBOM> wBOMList,
			List<FMCLineUnit> wLineUnitList, List<FMCLine> wLineList, List<FPCPart> wPartList,
			List<FPCPartPoint> wPartPointList, List<CRMCustomer> wCustomerList, List<BMSWorkCharge> wWorkChargeList,
			List<FPCProduct> wProductList, List<BMSDepartment> wDepartmentList) {
		try {
			if (StringUtils.isEmpty(wPartPointName)) {
				return;
			}

			// 修程赋值
			int wLineID = 0;
			if ((wLineList != null && wLineList.stream().anyMatch(p -> p.Name.equals(wLineName)))) {
				wLineID = wLineList.stream().filter(p -> p.Name.equals(wLineName)).findFirst().get().ID;
			}
			// 工位赋值
			int wPartID = 0;
			if (wPartList != null && wPartList.stream().anyMatch(p -> p.Code.equals(wPartName))) {
				wPartID = wPartList.stream().filter(p -> p.Code.equals(wPartName)).findFirst().get().ID;
			}
			// 局段赋值
			int wCustomerID = 0;
			if (wCustomerList != null && wCustomerList.stream().anyMatch(p -> p.CustomerCode.equals(wCustomerName))) {
				wCustomerID = wCustomerList.stream().filter(p -> p.CustomerCode.equals(wCustomerName)).findFirst()
						.get().ID;
			}
			// 车型赋值
			int wProductID = 0;
			if ((wProductList != null && wProductList.stream().anyMatch(p -> p.ProductNo.equals(wProductNo)))) {
				wProductID = wProductList.stream().filter(p -> p.ProductNo.equals(wProductNo)).findFirst().get().ID;
			}
			// 班组赋值
			int wClassID = 0;
			if (wDepartmentList != null && wDepartmentList.size() > 0 && wDepartmentList.stream()
					.anyMatch(p -> p.Name.equals(wClassName) && p.Type == BMSDepartmentType.Class.getValue())) {
				wClassID = wDepartmentList.stream()
						.filter(p -> p.Name.equals(wClassName) && p.Type == BMSDepartmentType.Class.getValue())
						.findFirst().get().ID;
			}
			// 更新班组工位
			int wTempClassID = wClassID;
			if (wWorkChargeList.stream().anyMatch(p -> p.ClassID == wTempClassID)) {
				List<BMSWorkCharge> wChargeList = wWorkChargeList.stream().filter(p -> p.ClassID == wTempClassID)
						.collect(Collectors.toList());
				int wTempPartID = wPartID;
				if (!wChargeList.stream().anyMatch(p -> p.StationID == wTempPartID) && wPartID > 0) {
					BMSWorkCharge wBMSWorkCharge = new BMSWorkCharge();
					wBMSWorkCharge.Active = 1;
					wBMSWorkCharge.ClassID = wTempClassID;
					wBMSWorkCharge.CreateTime = Calendar.getInstance();
					wBMSWorkCharge.CreatorID = wLoginUser.ID;
					wBMSWorkCharge.EditorID = 0;
					wBMSWorkCharge.EditTime = Calendar.getInstance();
					wBMSWorkCharge.ID = 0;
					wBMSWorkCharge.StationID = wPartID;
					wBMSWorkCharge = CoreServiceImpl.getInstance().BMS_SaveWorkCharge(wLoginUser, wBMSWorkCharge)
							.Info(BMSWorkCharge.class);
					if (wBMSWorkCharge != null && wBMSWorkCharge.ID > 0) {
						wWorkChargeList.add(wBMSWorkCharge);
					}
				}
			}
			// 新增工序
			int wPartPointID = 0;
			if (wPartPointList.stream().anyMatch(p -> p.Name.equals(wPartPointName))) {
				wPartPointID = wPartPointList.stream().filter(p -> p.Name.equals(wPartPointName)).findFirst().get().ID;
			} else {
				FPCPartPoint wFPCPartPoint = new FPCPartPoint();
				wFPCPartPoint.Active = 1;
				wFPCPartPoint.Code = wPartPointCode;
				wFPCPartPoint.CreateTime = Calendar.getInstance();
				wFPCPartPoint.CreatorID = wLoginUser.ID;
				wFPCPartPoint.StepType = 1;
				wFPCPartPoint.EditorID = wLoginUser.ID;
				wFPCPartPoint.EditTime = Calendar.getInstance();
				wFPCPartPoint.ID = 0;
				wFPCPartPoint.Name = wPartPointName;
				wFPCPartPoint.Status = 3;
				wFPCPartPoint.FactoryID = 1;
				wFPCPartPoint = FMCServiceImpl.getInstance().FPC_SavePartPoint(wLoginUser, wFPCPartPoint)
						.Info(FPCPartPoint.class);
				if (wFPCPartPoint != null && wFPCPartPoint.ID > 0) {
					wPartPointList.add(wFPCPartPoint);
					wPartPointID = wFPCPartPoint.ID;
				}
			}

			int wTempLineID = wLineID;
			// 产线单元新增
			if (wPartID > 0 && wPartPointID > 0) {
				// 设置产线单元
				SetFMCLineUnit(wLoginUser, wLineUnitList, wLineID, wPartID, wPartPointID, wTempLineID, wProductID);
			}
			// 设置BOM
			SetMSSBOM(wLoginUser, wProductNo, wLineName, wCustomerName, wBOMList, wLineID, wPartID, wCustomerID,
					wProductID, wPartPointID, wMaterialName, wTempLineID, wBOMNO, wMaterialNo);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.toString());
		}
	}

	private void SetMSSBOM(BMSEmployee wLoginUser, String wProductNo, String wLineName, String wCustomerName,
			List<MSSBOM> wBOMList, int wLineID, int wPartID, int wCustomerID, int wProductID, int wPartPointID,
			String wMaterialName, int wTempLineID, String wBOMNO, String wMaterialNo) {
		try {
			// 新增物料
			List<MSSMaterial> wMaterialList = WMSServiceImpl.getInstance()
					.MSS_QueryMaterialList(wLoginUser, wMaterialNo).List(MSSMaterial.class);
			MSSMaterial wMSSMaterial = null;
			if (wMaterialList == null || wMaterialList.size() <= 0) {
				wMSSMaterial = new MSSMaterial();
				wMSSMaterial.AuditTime = Calendar.getInstance();
				wMSSMaterial.Author = wLoginUser.Name;
				wMSSMaterial.Auditor = wLoginUser.Name;
				wMSSMaterial.MaterialNo = wMaterialNo;
				wMSSMaterial.MaterialName = wMaterialName;
				wMSSMaterial.EditTime = Calendar.getInstance();
				int wNewID = WMSServiceImpl.getInstance().MSS_SaveMaterial(wLoginUser, wMSSMaterial)
						.Info(Integer.class);
				if (wNewID > 0) {
					wMSSMaterial.ID = wNewID;
				}
			} else {
				wMSSMaterial = wMaterialList.get(0);
			}
			// 新增BOM
			MSSBOM wMSSBOM = null;
			if (!wBOMList.stream().anyMatch(p -> p.BOMNo.equals(wBOMNO))) {
				wMSSBOM = new MSSBOM();
				wMSSBOM.BOMNo = wBOMNO;
				wMSSBOM.BOMName = StringUtils.Format("{0}-{1}", wProductNo, wLineName);
				wMSSBOM.CustomerID = wCustomerID;
				wMSSBOM.EditTime = Calendar.getInstance();
				wMSSBOM.ID = 0;
				wMSSBOM.LineID = wLineID;
				wMSSBOM.ProductID = wProductID;
				wMSSBOM.PartID = wPartID;
				wMSSBOM.Status = 1;
				wMSSBOM.MaterialID = wMSSMaterial.ID;
				wMSSBOM.Auditor = wLoginUser.Name;
				int wNewID = WMSServiceImpl.getInstance().MSS_SaveBOM(wLoginUser, wMSSBOM).Custom("list",
						Integer.class);
				if (wNewID > 0) {
					wMSSBOM.ID = wNewID;
					wBOMList.add(wMSSBOM);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.toString());
		}
	}

	/**
	 * 设置产线单元
	 * 
	 * @param wLoginUser
	 * @param wLineUnitList
	 * @param wLineID
	 * @param wPartID
	 * @param wPartPointID
	 * @param wTempLineID
	 */
	private void SetFMCLineUnit(BMSEmployee wLoginUser, List<FMCLineUnit> wLineUnitList, int wLineID, int wPartID,
			int wPartPointID, int wTempLineID, int wProductID) {
		try {
			List<FMCLineUnit> wTempUnitList = wLineUnitList.stream()
					.filter(p -> p.LineID == wTempLineID && p.ProductID == wProductID).collect(Collectors.toList());
			if (wTempUnitList == null || wTempUnitList.size() <= 0) {
				FMCLineUnit wFMCLineUnit = new FMCLineUnit();
				wFMCLineUnit.Active = 1;
				wFMCLineUnit.LineID = wLineID;
				wFMCLineUnit.UnitID = wPartID;
				wFMCLineUnit.OrderID = 1;
				wFMCLineUnit.LevelID = 2;
				wFMCLineUnit.CreatorID = wLoginUser.ID;
				wFMCLineUnit.CreateTime = Calendar.getInstance();
				wFMCLineUnit.Status = 1;
				wFMCLineUnit.ProductID = wProductID;
				wFMCLineUnit.ParentUnitID = 1;
				FMCServiceImpl.getInstance().FMC_SaveLineUnit(wLoginUser, wFMCLineUnit);

				wFMCLineUnit = new FMCLineUnit();
				wFMCLineUnit.Active = 1;
				wFMCLineUnit.LineID = wLineID;
				wFMCLineUnit.UnitID = wPartPointID;
				wFMCLineUnit.OrderID = 1;
				wFMCLineUnit.LevelID = 3;
				wFMCLineUnit.CreatorID = wLoginUser.ID;
				wFMCLineUnit.CreateTime = Calendar.getInstance();
				wFMCLineUnit.Status = 1;
				wFMCLineUnit.ProductID = wProductID;
				wFMCLineUnit.ParentUnitID = wPartID;
				FMCServiceImpl.getInstance().FMC_SaveLineUnit(wLoginUser, wFMCLineUnit);
			} else {
				// 工位级
				int wTempPartID = 0;
				if (!wTempUnitList.stream().anyMatch(p -> p.UnitID == wTempPartID)) {
					int wMaxOrderID = wTempUnitList.stream().filter(p -> p.LevelID == 2).collect(Collectors.toList())
							.stream().max(Comparator.comparing(FMCLineUnit::getOrderID)).get().OrderID + 1;
					FMCLineUnit wFMCLineUnit = new FMCLineUnit();
					wFMCLineUnit.Active = 1;
					wFMCLineUnit.LineID = wLineID;
					wFMCLineUnit.UnitID = wPartID;
					wFMCLineUnit.OrderID = wMaxOrderID;
					wFMCLineUnit.LevelID = 2;
					wFMCLineUnit.CreatorID = wLoginUser.ID;
					wFMCLineUnit.ProductID = wProductID;
					wFMCLineUnit.CreateTime = Calendar.getInstance();
					wFMCLineUnit.Status = 1;
					wFMCLineUnit.ParentUnitID = 1;
					FMCServiceImpl.getInstance().FMC_SaveLineUnit(wLoginUser, wFMCLineUnit);
				}
				// 工序级
				int wTempPartPointID = wPartPointID;
				if (!wTempUnitList.stream().anyMatch(p -> p.LevelID == 3 && p.UnitID == wTempPartPointID)) {
					int wMaxOrderID = wTempUnitList.stream().filter(p -> p.LevelID == 3).collect(Collectors.toList())
							.stream().max(Comparator.comparing(FMCLineUnit::getOrderID)).get().OrderID + 1;
					FMCLineUnit wFMCLineUnit = new FMCLineUnit();
					wFMCLineUnit.Active = 1;
					wFMCLineUnit.LineID = wLineID;
					wFMCLineUnit.UnitID = wPartPointID;
					wFMCLineUnit.OrderID = wMaxOrderID;
					wFMCLineUnit.LevelID = 3;
					wFMCLineUnit.CreatorID = wLoginUser.ID;
					wFMCLineUnit.CreateTime = Calendar.getInstance();
					wFMCLineUnit.Status = 1;
					wFMCLineUnit.ProductID = wProductID;
					wFMCLineUnit.ParentUnitID = wPartID;
					FMCServiceImpl.getInstance().FMC_SaveLineUnit(wLoginUser, wFMCLineUnit);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 导入标准BOM
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	public ServiceResult<String> IPT_ImportBOM(BMSEmployee wLoginUser, ExcelData wExcelData, String wFileName) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// 【BOM管理】权限控制
			if (!CoreServiceImpl.getInstance()
					.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 200100, 0, 0)
					.Info(Boolean.class)) {
				String wMsg = "提示：无BOM管理权限!";
				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wFileName, wMsg, wExcelData.sheetData.get(0).lineSum - 4,
						IMPType.BOM.getValue());

				wResult.Result = wMsg;
				return wResult;
			}

			if (wExcelData == null || wExcelData.sheetData == null || wExcelData.sheetData.size() <= 0
					|| wExcelData.sheetData.get(0).lineData == null
					|| wExcelData.sheetData.get(0).lineData.size() <= 0) {

				String wMsg = "提示：Excel模板错误，数据解析失败!";
				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wFileName, wMsg, wExcelData.sheetData.get(0).lineSum - 4,
						IMPType.BOM.getValue());

				wResult.Result = wMsg;
				return wResult;
			}

			// ② 车型列表
			List<FPCProduct> wProductList = FMCServiceImpl.getInstance().FPC_QueryProductList(wLoginUser, -1, -1)
					.List(FPCProduct.class);
			// ③ 修程列表
			List<FMCLine> wLineList = FMCServiceImpl.getInstance().FMC_QueryLineList(wLoginUser, -1, -1, -1)
					.List(FMCLine.class);
			// ④ 局段列表
			List<CRMCustomer> wCustomerList = SCMServiceImpl.getInstance()
					.CRM_QueryCustomerList(BaseDAO.SysAdmin, "", 0, 0, 0, 1).List(CRMCustomer.class);
			// ⑥ 工位列表
			List<FPCPart> wPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(wLoginUser, -1, -1, -1, -1)
					.List(FPCPart.class);
			// ⑧单位列表
			List<CFGUnit> wCFGUnitList = CoreServiceImpl.getInstance().CFG_QueryUnitList(BaseDAO.SysAdmin)
					.List(CFGUnit.class);
			// BOM列表
			List<MSSBOM> wBOMList = new ArrayList<MSSBOM>();
			List<MSSBOMItem> wBOMItemList = new ArrayList<MSSBOMItem>();
			// 工艺工序
			List<FPCRoutePartPoint> wRoutePartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartPointListByRouteID(wLoginUser, -1, -1).List(FPCRoutePartPoint.class);
			// 工艺BOP
			List<FPCRoute> wRouteList = FMCServiceImpl.getInstance().FPC_QueryRouteList(wLoginUser, -1, -1, -1)
					.List(FPCRoute.class);

			if (wExcelData.sheetData.size() > 1) {
				List<ExcelLineData> wLineDataList = wExcelData.sheetData.get(1).lineData;
				// 导入局段列表
				this.ImportCustomerList(wLoginUser, wLineDataList, wCustomerList);
			}

			List<ExcelLineData> wExcelLineDataList = wExcelData.sheetData.get(0).lineData;

			// 物料列表
			List<MSSMaterial> wMaterialList = WMSServiceImpl.getInstance().MSS_QueryMaterialList(wLoginUser, "")
					.List(MSSMaterial.class);

			// 记录错误提示
			String wTip = this.RecordErrorLog(wLoginUser, wExcelLineDataList, wFileName, wProductList, wLineList,
					wCustomerList, wPartList, wRoutePartPointList, wCFGUnitList, wMaterialList);
			if (StringUtils.isNotEmpty(wTip)) {
				wResult.Result = wTip;
				return wResult;
			}

			// 遍历获取列的值
			String wBomType = "";
			String wProductType = "";
			String wLine = "";
			String wCustormer = "";
			String wFactory = "";
			String wPart = "";
			String wPartPoint = "";
			String wMaterialNo = "";
			String wMaterialName = "";
			String wNumber = "";
			String wUnit = "";
			String wIsChange = "";
			String wChageRate = "";
			String wIsRepair = "";
			String wRequirment = "";
			String wIsOutTrain = "";
			String wRemark = "";
			String wCrafter = "";
			String wParentMaterial = "";

			int wBOMID = 0;
			IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
					IMPType.BOM.getValue(), "", null, IMPResult.Doding.getValue(), wFileName,
					wExcelData.sheetData.get(0).lineSum - 4, 0);
			wIMPResultRecord.ID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

			for (int i = 4; i < wExcelLineDataList.size(); i++) {
				int wIndex = 0;
				// BOM类型
				wBomType = wExcelLineDataList.get(i).colData.size() >= 1
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 车型
				wProductType = wExcelLineDataList.get(i).colData.size() >= 2
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 修程
				wLine = wExcelLineDataList.get(i).colData.size() >= 3
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 局段
				wCustormer = wExcelLineDataList.get(i).colData.size() >= 4
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 工厂
				wFactory = wExcelLineDataList.get(i).colData.size() >= 5
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 工位
				wPart = wExcelLineDataList.get(i).colData.size() >= 6 ? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 工序
				wPartPoint = wExcelLineDataList.get(i).colData.size() >= 7
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 物料编码
				wMaterialNo = wExcelLineDataList.get(i).colData.size() >= 8
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 物料名称
				wMaterialName = wExcelLineDataList.get(i).colData.size() >= 9
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 数量
				wNumber = wExcelLineDataList.get(i).colData.size() >= 10
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 单位
				wUnit = wExcelLineDataList.get(i).colData.size() >= 11 ? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 必换、偶换
				wIsChange = wExcelLineDataList.get(i).colData.size() >= 12
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 偶换率
				wChageRate = wExcelLineDataList.get(i).colData.size() >= 13
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 委外必修、委外偶修
				wIsRepair = wExcelLineDataList.get(i).colData.size() >= 14
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 原拆原装要求
				wRequirment = wExcelLineDataList.get(i).colData.size() >= 15
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 是否拆解下车
				wIsOutTrain = wExcelLineDataList.get(i).colData.size() >= 16
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 备注
				wRemark = wExcelLineDataList.get(i).colData.size() >= 17
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 工艺员
				wCrafter = wExcelLineDataList.get(i).colData.size() >= 18
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 上层物料
				wParentMaterial = wExcelLineDataList.get(i).colData.size() >= 19
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";

				// 处理BOMItem行
				ServiceResult<String> wMsg = this.HandleBomItemRow(wLoginUser, wCustomerList, wBomType, wProductType,
						wLine, wCustormer, wFactory, wPart, wPartPoint, wMaterialNo, wMaterialName, wNumber, wUnit,
						wIsChange, wChageRate, wIsRepair, wRequirment, wIsOutTrain, wRemark, wBOMList, wLineList,
						wProductList, wRoutePartPointList, wRouteList, wCrafter, wParentMaterial, wCFGUnitList, i,
						wIMPResultRecord, wExcelLineDataList.size() - 1, wPartList, wBOMItemList, wMaterialList);

				if (wMsg.CustomResult.containsKey("BOMID")) {
					wBOMID = (int) wMsg.CustomResult.get("BOMID");
				}

				if (StringUtils.isNotEmpty(wMsg.Result)) {

					IMPResultRecordDAO.getInstance().Delete(wLoginUser, wIMPResultRecord, wErrorCode);

					// 新增导入记录和错误记录
					AddImportRecord(wLoginUser, wFileName, wMsg.Result, wExcelData.sheetData.get(0).lineSum - 4,
							IMPType.BOM.getValue());

					return wMsg;
				}
			}

			wResult.CustomResult.put("BOMID", wBOMID);

			// 更新导入成功记录
			int wNewID = wBOMID;
			wIMPResultRecord.PID = new ArrayList<Integer>(Arrays.asList(wNewID));
			wIMPResultRecord.Result = wIMPResultRecord.Progress == wExcelLineDataList.size() - 4
					? IMPResult.Success.getValue()
					: IMPResult.Failed.getValue();
			IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

			if (wBOMList != null && wBOMList.size() == 1
					&& wIMPResultRecord.Progress == wExcelLineDataList.size() - 4) {
				wBOMList.get(0).Status = 1;
				WMSServiceImpl.getInstance().MSS_SaveBOM(wLoginUser, wBOMList.get(0));
			} else {
				wResult.FaultCode += "提示：导入失败!";
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 添加错误记录
	 * 
	 * @param wLoginUser
	 * @param wList
	 * @param wFileName
	 * @param wErrorCode
	 */
	@SuppressWarnings("unused")
	private void AddRecored(BMSEmployee wLoginUser, List<ExcelLineData> wExcelLineDataList, String wFileName,
			List<FPCProduct> wProductList, List<FMCLine> wLineList, List<CRMCustomer> wCustomerList,
			List<FPCPart> wPartList, List<FPCRoutePartPoint> wStepList, List<CFGUnit> wCFGUnitList,
			List<MSSMaterial> wMaterialList, OutResult<Integer> wErrorCode) {
		try {
			List<IMPErrorRecord> wRecordList = new ArrayList<IMPErrorRecord>();

			for (int i = 4; i < wExcelLineDataList.size(); i++) {
				int wIndex = 0;

				// BOM类型
				String wBomType = wExcelLineDataList.get(i).colData.size() >= 1
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 车型
				String wProductType = wExcelLineDataList.get(i).colData.size() >= 2
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 修程
				String wLine = wExcelLineDataList.get(i).colData.size() >= 3
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 局段
				String wCustormer = wExcelLineDataList.get(i).colData.size() >= 4
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 工厂
				String wFactory = wExcelLineDataList.get(i).colData.size() >= 5
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 工位
				String wPart = wExcelLineDataList.get(i).colData.size() >= 6
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 工序
				String wPartPoint = wExcelLineDataList.get(i).colData.size() >= 7
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 物料编码
				String wMaterialNo = wExcelLineDataList.get(i).colData.size() >= 8
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 物料名称
				wIndex++;
				// 数量
				String wNumber = wExcelLineDataList.get(i).colData.size() >= 10
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 单位
				String wUnit = wExcelLineDataList.get(i).colData.size() >= 11
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 必换、偶换
				String wChange = wExcelLineDataList.get(i).colData.size() >= 12
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 偶换率
				String wRate = wExcelLineDataList.get(i).colData.size() >= 13
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 委外必修、委外偶修
				String wRepair = wExcelLineDataList.get(i).colData.size() >= 14
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 原拆原装要求
				String wRequire = wExcelLineDataList.get(i).colData.size() >= 15
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 是否拆解下车
				String wOut = wExcelLineDataList.get(i).colData.size() >= 16
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 备注
				String wRemark = wExcelLineDataList.get(i).colData.size() >= 17
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 工艺员
				String wCraft = wExcelLineDataList.get(i).colData.size() >= 18
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 上层物料
				String wParentMaterial = wExcelLineDataList.get(i).colData.size() >= 19
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";

				// ① 检查BOM类型
				if (StringUtils.isEmpty(wBomType)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，BOM类型不能为空!", i + 1)));
				} else if (!wBomType.equals("1") && !wBomType.equals("2") && !wBomType.equals("2.0")
						&& !wBomType.equals("2.0")) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，BOM类型输入值不合法!", i + 1)));
				}
				// ② 检查车型
				if (StringUtils.isEmpty(wProductType)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，车型不能为空!", i + 1)));
				} else if (!wProductList.stream().anyMatch(p -> p.ProductNo.equals(wProductType))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【{1}】车型不存在!", i + 1, wProductType)));
				}
				// ③ 检查修程
				if (StringUtils.isEmpty(wLine)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，修程不能为空!", i + 1)));
				} else if (!wLineList.stream().anyMatch(p -> p.Name.equals("C" + wLine))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【{1}】修程不存在!", i + 1, "C" + wLine)));
				}
				// ④ 检查局段
				if (StringUtils.isEmpty(wCustormer)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，局段不能为空!", i + 1)));
				} else if (!wCustomerList.stream().anyMatch(p -> p.CustomerCode.equals(wCustormer))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【{1}】局段不存在!", i + 1, wCustormer)));
				}
				// ⑤ 检查工厂
				if (StringUtils.isEmpty(wFactory)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，工厂不能为空!", i + 1)));
				} else if (!wFactory.equals("1900")) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，工厂输入值不合法!", i + 1)));
				}
				// ⑥ 检查工位
				if (StringUtils.isEmpty(wPart)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，工位不能为空!", i + 1)));
				} else if (!wPartList.stream().anyMatch(p -> p.Code.equals(wPart))) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，【{1}】工位不存在!", i + 1, wPart)));
				}
				// ⑦ 检查工序
				if (StringUtils.isEmpty(wPartPoint)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，工序不能为空!", i + 1)));
				} else if (!wStepList.stream().anyMatch(p -> p.Code.equals(wPartPoint))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【{1}】工序不存在!", i + 1, wPartPoint)));
				}
				// ⑧ 检查物料编码
				if (StringUtils.isEmpty(wMaterialNo)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，物料编码不能为空!", i + 1)));
				}
				if (!wMaterialList.stream().anyMatch(p -> p.MaterialNo.equals(wMaterialNo))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，物料编码【{1}】不存在!", i + 1, wMaterialNo)));
				}
				// ⑩ 检查数量
				if (StringUtils.isEmpty(wNumber)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，数量不能为空!", i + 1)));
				} else if (!isNumber(wNumber)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，数量输入值不合法!", i + 1)));
				}
				// ①检查必换、偶换
				if (!(wChange.equals("1") || wChange.equals("2") || wChange.equals(""))) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，必换(1)/偶换(2)输入值不合法!", i + 1)));
				}
				// ②检查偶换率
				if (!(StringUtils.isEmpty(wRate) || this.isNumeric(wRate)
						|| (Double.parseDouble(wRate) >= 0 && Double.parseDouble(wRate) < 100))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，偶换率输入值不合法!", i + 1)));
				}
				// ③检查委外必修、委外偶修
				if (!(StringUtils.isEmpty(wRepair) || wRepair.equals("1") || wRepair.equals("2"))) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，委外必修、委外偶修输入值不合法!", i + 1)));
				}
				// ④检查原拆原装要求
				if (!(StringUtils.isEmpty(wRequire) || wRequire.equals("X"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，原拆原装要求输入值不合法!", i + 1)));
				}
				// ⑤检查是否拆解下车
				if (!(StringUtils.isEmpty(wOut) || wOut.equals("X"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，是否拆解下车输入值不合法!", i + 1)));
				}

				// ⑥检查数据重复性(车型、修程、局段、工位、工序、物料)
				if (wExcelLineDataList.stream()
						.filter(p -> p.colData != null && p.colData.size() > 0
								&& (p.colData.size() > 1 ? p.colData.get(1) : "").equals(wProductType)
								&& (p.colData.size() > 2 ? p.colData.get(2).replace(".0", "") : "").equals(wLine)
								&& (p.colData.size() > 3 ? p.colData.get(3) : "").equals(wCustormer)
								&& (p.colData.size() > 5 ? p.colData.get(5) : "").equals(wPart)
								&& (p.colData.size() > 6 ? p.colData.get(6) : "").equals(wPartPoint)
								&& (p.colData.size() > 7 ? p.colData.get(7) : "").equals(wMaterialNo)
								&& (p.colData.size() > 11 ? p.colData.get(11).replace(".0", "") : "").equals(wChange)
								&& (p.colData.size() > 13 ? p.colData.get(13).replace(".0", "") : "").equals(wRepair)
								&& (p.colData.size() > 18 ? p.colData.get(18) : "").equals(wParentMaterial))
						.count() >= 2) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils
							.Format("提示：第{0}行数据不合法，【{1}】-【{2}】-【{3}】物料数据重复!", i + 1, wPart, wPartPoint, wMaterialNo)));
				}
			}

			if (wRecordList.size() > 0) {
				IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
						IMPType.BOM.getValue(), "", null, IMPResult.Failed.getValue(), wFileName,
						wExcelLineDataList.size() - 4, wRecordList.size());
				int wNewID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

				wRecordList.forEach(p -> p.ParentID = wNewID);
				for (IMPErrorRecord wIMPErrorRecord : wRecordList) {
					IMPErrorRecordDAO.getInstance().Update(wLoginUser, wIMPErrorRecord, wErrorCode);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.toString());
		}
	}

	/**
	 * 判断字符串为数字类型
	 * 
	 * @param str
	 * @return
	 */
	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * 记录BOM导入的错误日志
	 * 
	 * @param wLoginUser         登录信息
	 * @param wExcelLineDataList Excel数据源
	 * @return 第一个错误提示
	 */
	@SuppressWarnings("unused")
	private String RecordErrorLog(BMSEmployee wLoginUser, List<ExcelLineData> wExcelLineDataList, String wFileName,
			List<FPCProduct> wProductList, List<FMCLine> wLineList, List<CRMCustomer> wCustomerList,
			List<FPCPart> wPartList, List<FPCRoutePartPoint> wRoutePartPointList, List<CFGUnit> wCFGUnitList,
			List<MSSMaterial> wMaterialList) {
		String wResult = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// 记录错误日志到数据库
			AddRecored(wLoginUser, wExcelLineDataList, wFileName, wProductList, wLineList, wCustomerList, wPartList,
					wRoutePartPointList, wCFGUnitList, wMaterialList, wErrorCode);

			for (int i = 4; i < wExcelLineDataList.size(); i++) {
				int wIndex = 0;

				// BOM类型
				String wBomType = wExcelLineDataList.get(i).colData.size() >= 1
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 车型
				String wProductType = wExcelLineDataList.get(i).colData.size() >= 2
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 修程
				String wLine = wExcelLineDataList.get(i).colData.size() >= 3
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 局段
				String wCustormer = wExcelLineDataList.get(i).colData.size() >= 4
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 工厂
				String wFactory = wExcelLineDataList.get(i).colData.size() >= 5
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 工位
				String wPart = wExcelLineDataList.get(i).colData.size() >= 6
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 工序
				String wPartPoint = wExcelLineDataList.get(i).colData.size() >= 7
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 物料编码
				String wMaterialNo = wExcelLineDataList.get(i).colData.size() >= 8
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 物料名称
				wIndex++;
				// 数量
				String wNumber = wExcelLineDataList.get(i).colData.size() >= 10
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 单位
				String wUnit = wExcelLineDataList.get(i).colData.size() >= 11
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 必换、偶换
				String wChange = wExcelLineDataList.get(i).colData.size() >= 12
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 偶换率
				String wRate = wExcelLineDataList.get(i).colData.size() >= 13
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 委外必修、委外偶修
				String wRepair = wExcelLineDataList.get(i).colData.size() >= 14
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// 原拆原装要求
				String wRequire = wExcelLineDataList.get(i).colData.size() >= 15
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 是否拆解下车
				String wOut = wExcelLineDataList.get(i).colData.size() >= 16
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 备注
				String wRemark = wExcelLineDataList.get(i).colData.size() >= 17
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 工艺员
				String wCraft = wExcelLineDataList.get(i).colData.size() >= 18
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// 上层物料
				String wParentMaterial = wExcelLineDataList.get(i).colData.size() >= 19
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";

				// ① 检查BOM类型
				if (StringUtils.isEmpty(wBomType)) {
					return StringUtils.Format("提示：第{0}行数据不合法，BOM类型不能为空!", i + 1);
				} else if (!wBomType.equals("1") && !wBomType.equals("2")
						&& (!wBomType.equals("1.0") && !wBomType.equals("2.0"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，BOM类型输入值不合法!", i + 1);
				}
				// ② 检查车型
				if (StringUtils.isEmpty(wProductType)) {
					return StringUtils.Format("提示：第{0}行数据不合法，车型不能为空!", i + 1);
				} else if (!wProductList.stream().anyMatch(p -> p.ProductNo.equals(wProductType))) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】车型不存在!", i + 1, wProductType);
				}
				// ③ 检查修程
				if (StringUtils.isEmpty(wLine)) {
					return StringUtils.Format("提示：第{0}行数据不合法，修程不能为空!", i + 1);
				} else if (!wLineList.stream().anyMatch(p -> p.Name.equals("C" + wLine))) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】修程不存在!", i + 1, "C" + wLine);
				}
				// ④ 检查局段
				if (StringUtils.isEmpty(wCustormer)) {
					return StringUtils.Format("提示：第{0}行数据不合法，局段不能为空!", i + 1);
				} else if (!wCustomerList.stream().anyMatch(p -> p.CustomerCode.equals(wCustormer))) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】局段不存在!", i + 1, wCustormer);
				}
				// ⑤ 检查工厂
				if (StringUtils.isEmpty(wFactory)) {
					return StringUtils.Format("提示：第{0}行数据不合法，工厂不能为空!", i + 1);
				} else if (!wFactory.equals("1900")) {
					return StringUtils.Format("提示：第{0}行数据不合法，工厂输入值不合法!", i + 1);
				}
				// ⑥ 检查工位
				if (StringUtils.isEmpty(wPart)) {
					return StringUtils.Format("提示：第{0}行数据不合法，工位不能为空!", i + 1);
				} else if (!wPartList.stream().anyMatch(p -> p.Code.equals(wPart))) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】工位不存在!", i + 1, wPart);
				}
				// ⑦ 检查工序
				if (StringUtils.isEmpty(wPartPoint)) {
					return StringUtils.Format("提示：第{0}行数据不合法，工序不能为空!", i + 1);
				} else if (!wRoutePartPointList.stream().anyMatch(p -> p.Code.equals(wPartPoint))) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】工序不存在!", i + 1, wPartPoint);
				}
				// ⑧ 检查物料编码
				if (StringUtils.isEmpty(wMaterialNo)) {
					return StringUtils.Format("提示：第{0}行数据不合法，物料编码不能为空!", i + 1);
				}
				if (!wMaterialList.stream().anyMatch(p -> p.MaterialNo.equals(wMaterialNo))) {
					return StringUtils.Format("提示：第{0}行数据不合法，物料编码【{1}】不存在!", i + 1, wMaterialNo);
				}
				// ⑩ 检查数量
				if (StringUtils.isEmpty(wNumber)) {
					return StringUtils.Format("提示：第{0}行数据不合法，数量不能为空!", i + 1);
				} else if (!isNumber(wNumber)) {
					return StringUtils.Format("提示：第{0}行数据不合法，数量输入值不合法!", i + 1);
				}
				// ①检查必换、偶换
				if (!(wChange.equals("1") || wChange.equals("2") || wChange.equals(""))) {
					return StringUtils.Format("提示：第{0}行数据不合法，必换(1)/偶换(2)输入值不合法!", i + 1);
				}
				// ②检查偶换率
				if (!(StringUtils.isEmpty(wRate) || this.isNumeric(wRate)
						|| (Double.parseDouble(wRate) >= 0 && Double.parseDouble(wRate) < 100))) {
					return StringUtils.Format("提示：第{0}行数据不合法，偶换率输入值不合法!", i + 1);
				}
				// ③检查委外必修、委外偶修
				if (!(StringUtils.isEmpty(wRepair) || wRepair.equals("1") || wRepair.equals("2"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，委外必修、委外偶修输入值不合法!", i + 1);
				}
				// ④检查原拆原装要求
				if (!(StringUtils.isEmpty(wRequire) || wRequire.equals("X"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，原拆原装要求输入值不合法!", i + 1);
				}
				// ⑤检查是否拆解下车
				if (!(StringUtils.isEmpty(wOut) || wOut.equals("X"))) {
					return StringUtils.Format("提示：第{0}行数据不合法，是否拆解下车输入值不合法!", i + 1);
				}
				// ⑥检查数据重复性(车型、修程、局段、工位、工序、物料)
				if (wExcelLineDataList.stream()
						.filter(p -> p.colData != null && p.colData.size() > 0
								&& (p.colData.size() > 1 ? p.colData.get(1) : "").equals(wProductType)
								&& (p.colData.size() > 2 ? p.colData.get(2).replace(".0", "") : "").equals(wLine)
								&& (p.colData.size() > 3 ? p.colData.get(3) : "").equals(wCustormer)
								&& (p.colData.size() > 5 ? p.colData.get(5) : "").equals(wPart)
								&& (p.colData.size() > 6 ? p.colData.get(6) : "").equals(wPartPoint)
								&& (p.colData.size() > 7 ? p.colData.get(7) : "").equals(wMaterialNo)
								&& (p.colData.size() > 11 ? p.colData.get(11).replace(".0", "") : "").equals(wChange)
								&& (p.colData.size() > 13 ? p.colData.get(13).replace(".0", "") : "").equals(wRepair)
								&& (p.colData.size() > 18 ? p.colData.get(18) : "").equals(wParentMaterial))
						.count() >= 2) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】-【{2}】-【{3}】物料数据重复!", i + 1, wPart, wPartPoint,
							wMaterialNo);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 导入成功记录
	 * 
	 * @param wLoginUser
	 * @param wFileName
	 * @param size
	 */
//	private void RecordSuccess(BMSEmployee wLoginUser, String wFileName, int wSize, int wBOMID, int wType) {
//		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
//		try {
//			IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(), wType, "",
//					new ArrayList<Integer>(Arrays.asList(wBOMID)), IMPResult.Success.getValue(), wFileName, wSize, 0);
//			IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
//		} catch (Exception ex) {
//			logger.error(ex.toString());
//		}
//	}

	/**
	 * 添加导入错误记录
	 * 
	 * @param wLoginUser 登录信息
	 * @param wFileName  文件名
	 * @param wMsg       错误信息
	 * @param wDataCount 数据行数
	 * @param wType      导入类型
	 */
	private void AddImportRecord(BMSEmployee wLoginUser, String wFileName, String wMsg, int wDataCount, int wType) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(), wType, "",
					null, IMPResult.Failed.getValue(), wFileName, wDataCount, 1);
			wIMPResultRecord.ID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
			if (wIMPResultRecord.ID <= 0) {
				return;
			}

			IMPErrorRecord wIMPErrorRecord = new IMPErrorRecord(0, wIMPResultRecord.ID, 0, wMsg);
			IMPErrorRecordDAO.getInstance().Update(wLoginUser, wIMPErrorRecord, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 导入BOMItem行
	 * 
	 */
	private ServiceResult<String> HandleBomItemRow(BMSEmployee wLoginUser, List<CRMCustomer> wCustomerList,
			String wBomType, String wProductType, String wLine, String wCustormer, String wFactory, String wPart,
			String wPartPoint, String wMaterialNo, String wMaterialName, String wNumber, String wUnit, String wIsChange,
			String wChageRate, String wIsRepair, String wRequirment, String wIsOutTrain, String wRemark,
			List<MSSBOM> wBOMList, List<FMCLine> wLineList, List<FPCProduct> wProductList,
			List<FPCRoutePartPoint> wRoutePartPointList, List<FPCRoute> wRouteList, String wCrafter,
			String wParentMaterial, List<CFGUnit> wCFGUnitList, int wIndex, IMPResultRecord wIMPResultRecord,
			int wMaxIndex, List<FPCPart> wPartList, List<MSSBOMItem> wBOMItemList, List<MSSMaterial> wMaterialList) {
		ServiceResult<String> wMsg = new ServiceResult<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wBOMList == null) {
				wBOMList = new ArrayList<MSSBOM>();
			}
			if (wLineList == null) {
				wLineList = new ArrayList<FMCLine>();
			}
			if (wProductList == null) {
				wProductList = new ArrayList<FPCProduct>();
			}
			MSSMaterial wParentM = null;
			// 单位
			int wUnitID = 0;
			if (wCFGUnitList.stream().anyMatch(p -> p.Name.equals(wUnit))) {
				wUnitID = wCFGUnitList.stream().filter(p -> p.Name.equals(wUnit)).findFirst().get().ID;
			} else {
				CFGUnit wCFGUnit = new CFGUnit();
				wCFGUnit.Name = wUnit;
				wCFGUnit.OperatorID = wLoginUser.ID;
				wCFGUnit.EditTime = Calendar.getInstance();
				wCFGUnit.Active = 1;
				wCFGUnit = CoreServiceImpl.getInstance().CFG_SaveUnit(wLoginUser, wCFGUnit).Info(CFGUnit.class);
				if (wCFGUnit != null && wCFGUnit.ID > 0) {
					wUnitID = wCFGUnit.ID;
				}
				wCFGUnitList.add(wCFGUnit);
			}

			// 新增物料
//			List<MSSMaterial> wMaterialList = WMSServiceImpl.getInstance()
//					.MSS_QueryMaterialList(wLoginUser, wMaterialNo).List(MSSMaterial.class);
			MSSMaterial wMSSMaterial = wMaterialList.stream().filter(p -> p.MaterialNo.equals(wMaterialNo)).findFirst()
					.get();
//			if (wMaterialList == null || wMaterialList.size() <= 0) {
//				wMSSMaterial = new MSSMaterial();
//				wMSSMaterial.AuditTime = Calendar.getInstance();
//				wMSSMaterial.Author = wLoginUser.Name;
//				wMSSMaterial.Auditor = wLoginUser.Name;
//				wMSSMaterial.MaterialNo = wMaterialNo;
//				wMSSMaterial.MaterialName = wMaterialName;
//				wMSSMaterial.Name = wMaterialName.split("\\\\")[0];
//				wMSSMaterial.CYUnitID = wUnitID;
//				wMSSMaterial.EditTime = Calendar.getInstance();
//				int wNewID = WMSServiceImpl.getInstance().MSS_SaveMaterial(wLoginUser, wMSSMaterial)
//						.Info(Integer.class);
//				if (wNewID > 0) {
//					wMSSMaterial.ID = wNewID;
//				}
//			} else {
//				wMSSMaterial = wMaterialList.get(0);
//			}

			// 修程
			String wRealLine = wLine.equals("6") ? "C6" : "C5";
			int wLineID = 0;
			if (wLineList.stream().anyMatch(p -> p.Name.equals(wRealLine))) {
				wLineID = wLineList.stream().filter(p -> p.Name.equals(wRealLine)).findFirst().get().ID;
			}
			if (wLineID <= 0) {
				wMsg.Result = StringUtils.Format("提示：【{0}】该修程不存在!", wRealLine);
				return wMsg;
			}
			// 车型
			int wProductID = 0;
			if (wProductList.stream().anyMatch(p -> p.ProductNo.equals(wProductType))) {
				wProductID = wProductList.stream().filter(p -> p.ProductNo.equals(wProductType)).findFirst().get().ID;
			}
			if (wProductID <= 0) {
				wMsg.Result = StringUtils.Format("提示：【{0}】该车型不存在!", wProductType);
				return wMsg;
			}
			// 局段
			int wCustomerID = 0;
			if (wCustomerList.stream().anyMatch(p -> p.CustomerCode.equals(wCustormer))) {
				wCustomerID = wCustomerList.stream().filter(p -> p.CustomerCode.equals(wCustormer)).findFirst()
						.get().ID;
			}
			if (wCustomerID <= 0) {
				wMsg.Result = StringUtils.Format("提示：【{0}】该局段不存在!", wCustormer);
				return wMsg;
			}
			// 新增BOM(根据修程、车型、局段)
			int wBOMID = 0;
			if (wBOMList.size() == 1) {
				wBOMID = wBOMList.get(0).ID;
			} else {
				MSSBOM wMSSBOM = new MSSBOM();
				wMSSBOM.ID = 0;
				wMSSBOM.BOMNo = "";
				wMSSBOM.BOMName = StringUtils.Format("{0}-{1}-{2}", wCustormer, wRealLine, wProductType);
				wMSSBOM.Status = 2;
				wMSSBOM.EditTime = Calendar.getInstance();
				wMSSBOM.AuditTime = Calendar.getInstance();
				wMSSBOM.Author = wLoginUser.Name;
				wMSSBOM.Auditor = wLoginUser.Name;
				wMSSBOM.MaterialID = wMSSMaterial.ID;
				wMSSBOM.LineID = wLineID;
				wMSSBOM.ProductID = wProductID;
				wMSSBOM.CustomerID = wCustomerID;
				// 赋值RouteID
				if (wRouteList != null && wRouteList.size() > 0
						&& wRouteList.stream()
								.anyMatch(p -> p.ProductID == wMSSBOM.ProductID && p.LineID == wMSSBOM.LineID
										&& p.CustomerID == wMSSBOM.CustomerID && p.IsStandard == 1)) {
					wMSSBOM.RouteID = wRouteList.stream().filter(p -> p.ProductID == wMSSBOM.ProductID
							&& p.LineID == wMSSBOM.LineID && p.CustomerID == wMSSBOM.CustomerID && p.IsStandard == 1)
							.findFirst().get().ID;
				} else {
					wMsg.Result = "提示：无标准BOP!";
					return wMsg;
				}
				MSSBOM wNewBom = WMSServiceImpl.getInstance().MSS_SaveBOM(wLoginUser, wMSSBOM).Custom("list",
						MSSBOM.class);
				if (wNewBom.ID > 0) {
					wBOMID = wNewBom.ID;
					wBOMList.add(wNewBom);
				}
				if (wBOMID <= 0) {
					wMsg.Result = "提示：保存主BOM失败!";
					return wMsg;
				}
			}
			// 工位
			int wPartID = wPartList.stream().filter(p -> p.Code.equals(wPart)).findFirst().get().ID;
			// 工序
			int wPartPointID = wRoutePartPointList.stream().filter(p -> p.Code.equals(wPartPoint)).findFirst()
					.get().PartPointID;
			if (wPartPointID <= 0) {
				wMsg.Result = StringUtils.Format("提示：【{0}】该工序不存在!", wPartPoint);
				return wMsg;
			}
			// 新增BOM子项
			MSSBOMItem wMSSBOMItem = new MSSBOMItem();
			wMSSBOMItem.Active = 0;
			wMSSBOMItem.Auditor = wLoginUser.Name;
			wMSSBOMItem.AuditTime = Calendar.getInstance();
			wMSSBOMItem.Author = wLoginUser.Name;
			wMSSBOMItem.BOMID = wBOMID;
			wMSSBOMItem.BOMType = Integer.parseInt(wBomType);
			wMSSBOMItem.ProductQD = wCustormer;
			wMSSBOMItem.DeviceNo = wFactory;
			wMSSBOMItem.PlaceID = wPartID;
			wMSSBOMItem.PartPointID = wPartPointID;
			wMSSBOMItem.MaterialID = wMSSMaterial.ID;
			wMSSBOMItem.MaterialNo = wMSSMaterial.MaterialNo;
			wMSSBOMItem.MaterialName = wMSSMaterial.MaterialName;
			wMSSBOMItem.MaterialNumber = StringUtils.isEmpty(wNumber) ? 0 : StringUtils.parseDouble(wNumber);
			wMSSBOMItem.UnitID = wUnitID;
			wMSSBOMItem.ReplaceType = StringUtils.isEmpty(wIsChange) ? 0 : StringUtils.parseInt(wIsChange);
			wMSSBOMItem.ReplaceRatio = StringUtils.isEmpty(wChageRate) ? 0 : StringUtils.parseFloat(wChageRate);
			wMSSBOMItem.OutsourceType = StringUtils.isEmpty(wIsRepair) ? 0 : StringUtils.parseInt(wIsRepair);
			wMSSBOMItem.OriginalType = wRequirment.equals("X") ? 1 : 0;
			wMSSBOMItem.DisassyType = wIsOutTrain.equals("X") ? 1 : 0;
			wMSSBOMItem.Remark = wRemark;
			// 上层物料
			if (StringUtils.isNotEmpty(wParentMaterial)) {
				wParentM = WMSServiceImpl.getInstance().MSS_QueryMaterialByID(wLoginUser, -1, wParentMaterial)
						.Info(MSSMaterial.class);
				wMSSBOMItem.ParentID = wParentM == null ? 0 : wParentM.getID();
			} else {
				wMSSBOMItem.ParentID = 0;
			}
			wBOMItemList.add(wMSSBOMItem);
			// 保存bom子项
//			wMSSBOMItem = WMSServiceImpl.getInstance().MSS_SaveBOMItem(wLoginUser, wMSSBOMItem).Info(MSSBOMItem.class);

			wMsg.CustomResult.put("BOMID", wBOMID);

			if ((wIndex - 3) % 100 == 0 || wIndex == wMaxIndex) {
				wIMPResultRecord.Progress = wIndex - 3;
				IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
				// 批量保存子bom
				WMSServiceImpl.getInstance().MSS_SaveBOMItemList(wLoginUser, wBOMItemList);
				// 重置
				wBOMItemList = new ArrayList<MSSBOMItem>();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.toString());
		}
		return wMsg;
	}

	/**
	 * 导入局段列表
	 * 
	 * @param wLineDataList
	 */
	private void ImportCustomerList(BMSEmployee wLoginUser, List<ExcelLineData> wLineDataList,
			List<CRMCustomer> wCustomerList) {
		try {
			if (wLineDataList == null || wLineDataList.size() <= 0) {
				return;
			}

			// 遍历获取
			String wCode = "";
			String wName = "";
			String wTaxCode = "";
			for (int i = 1; i < wLineDataList.size(); i++) {
				if (StringUtils.isEmpty(wLineDataList.get(i).colData.get(0))) {
					continue;
				}

				wCode = wLineDataList.get(i).colData.size() >= 3 ? wLineDataList.get(i).colData.get(0) : "";
				wName = wLineDataList.get(i).colData.size() >= 3 ? wLineDataList.get(i).colData.get(1) : "";
				wTaxCode = wLineDataList.get(i).colData.size() >= 3 ? wLineDataList.get(i).colData.get(2) : "";

				// 处理局段导入
				this.HandleCustomerRow(wLoginUser, wCustomerList, wCode, wName, wTaxCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 导入局段行数据
	 */
	private void HandleCustomerRow(BMSEmployee wLoginUser, List<CRMCustomer> wCustomerList, String wCode, String wName,
			String wTaxCode) {
		try {
			if (StringUtils.isEmpty(wCode)) {
				return;
			}

			if (wCustomerList == null) {
				wCustomerList = new ArrayList<CRMCustomer>();
			}

			if (wCustomerList.stream().anyMatch(p -> p.CustomerCode.equals(wCode))) {
				return;
			}

			CRMCustomer wCRMCustomer = new CRMCustomer();
			wCRMCustomer.ID = 0;
			wCRMCustomer.Active = 1;
			wCRMCustomer.Status = 1;
			wCRMCustomer.AuditTime = Calendar.getInstance();
			wCRMCustomer.CreateTime = Calendar.getInstance();
			wCRMCustomer.Auditor = wLoginUser.Name;
			wCRMCustomer.Creator = wLoginUser.Name;
			wCRMCustomer.TaxCode = wTaxCode;
			wCRMCustomer.CustomerCode = wCode;
			wCRMCustomer.CustomerName = wName;

			CRMCustomer wNewItem = SCMServiceImpl.getInstance().CRM_SaveCustomer(wLoginUser, wCRMCustomer)
					.Info(CRMCustomer.class);
			if (wNewItem != null && wNewItem.ID > 0) {
				wCustomerList.add(wNewItem);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 导出BOM数据
	 * 
	 * @return
	 */
	public Integer IPT_ExportBOM(BMSEmployee wLoginUser, ServletOutputStream wOutputStream, MSSBOM wBOM,
			List<MSSBOMItem> wItemList) {
		int wResult = 0;
		try {
			List<List<String>> wRowList = new ArrayList<List<String>>();

			String[] wHeaders = { "BOM类型", "车型", "修程", "局段信息", "工厂", "工位", "工序描述", "物料编码", "物料名称", "数量", "单位",
					"必换(1)/偶换(2)", "偶换率", "委外必修1/委外偶修2", "原拆原装要求", "是否拆解下车", "备注" };

			ExcelUtil.CreateFirst(wHeaders, "标准BOM");
			ExcelUtil.CreateOtherRow_Tip();
			ExcelUtil.CreateOtherRow_Orange();
			ExcelUtil.CreateOtherRow(wRowList);
			ExcelUtil.Export(wOutputStream);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 导出台车BOM
	 * 
	 * @param wLoginUser   登录信息
	 * @param outputStream 文件输出流
	 * @param wItemList    台车bom数据集合
	 * @return
	 */
	public Integer IPT_ExportAPSBOM(BMSEmployee wLoginUser, OutputStream wOutput, List<APSBOMItem> wItemList) {
		int wResult = 0;
		try {
			if (wItemList == null || wItemList.size() <= 0) {
				return wResult;
			}

			ExcelUtil.APSBom_CreateHeaders(wItemList.get(0).PartNo);
			ExcelUtil.APSBom_CreateTips();
			ExcelUtil.APSBom_CreateOptions();
			int wRowNum = 3;
			for (APSBOMItem wAPSBOMItem : wItemList) {
				List<String> wValueList = this.IPT_GetValueList(wAPSBOMItem);
				ExcelUtil.APSBom_CreateItems(wValueList, wRowNum++);
			}
			ExcelUtil.Export(wOutput);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据台车BOM类数据获取Excel行值集合
	 * 
	 * @param wAPSBOMItem
	 * @return
	 */
	private List<String> IPT_GetValueList(APSBOMItem wAPSBOMItem) {
		List<String> wResult = new ArrayList<String>();
		try {
			String wPro1 = "";
			String wPro2 = "";
			String wPro3 = "";
			String wPro4 = "";
			String wPro5 = "";
			String wPro6 = "";
			String wPro7 = "";
			String wPro8 = "";
			String wPro9 = "";
			String wPro10 = "";
			String wPro11 = "";
			String wPro12 = "";
			String wPro13 = "";
			String wPro14 = "";
			String wPro15 = "";
			String wPro16 = "";
			String wPro17 = "";
			String wPro18 = "";
			String wPro19 = "";
			String wPro20 = "";
			String wPro21 = "";
			String wPro22 = "";

			// BOM类型
			wPro1 = String.valueOf(wAPSBOMItem.BOMType);
			// 车型
			wPro2 = wAPSBOMItem.ProductNo;
			// 修程
			wPro3 = StringUtils.isEmpty(wAPSBOMItem.LineName) ? "" : wAPSBOMItem.LineName.substring(1);
			// 局段
			wPro4 = wAPSBOMItem.CustomerCode;
			// 工厂
			wPro5 = "1900";
			// WBS元素
			wPro6 = wAPSBOMItem.WBSNo;
			// 工位
			wPro7 = QMSConstants.GetFPCPart(wAPSBOMItem.PartID).Code;
			// 工序描述
			wPro8 = wAPSBOMItem.PartPointName;
			// 物料编码
			wPro9 = wAPSBOMItem.MaterialNo;
			// 物料名称
			wPro10 = wAPSBOMItem.MaterialName;
			// 数量
			wPro11 = String.valueOf(wAPSBOMItem.Number);
			// 单位
			wPro12 = wAPSBOMItem.UnitText;
			// 必换/偶换
			wPro13 = String.valueOf(wAPSBOMItem.ReplaceType);
			// 必修/偶修
			wPro14 = String.valueOf(wAPSBOMItem.OutsourceType);
			// 评估类型
			wPro15 = wAPSBOMItem.ReplaceType > 0 ? "常规新件"
					: wAPSBOMItem.OutsourceType > 0 ? "修复旧件" : wAPSBOMItem.PartChange > 0 ? "修复旧件" : "";
			// 是否互换件
			wPro16 = wAPSBOMItem.PartChange > 0 ? "X" : "";
			// 是否超修程
			wPro17 = wAPSBOMItem.OverLine > 0 ? "X" : "";
			// 领料部门
			wPro18 = "0001";
			// 仓库号
			wPro19 = wAPSBOMItem.BOMType == 1 ? "1100" : wAPSBOMItem.BOMType == 2 ? "1200" : "";
			// 质量损失大类
			wPro20 = String.valueOf(wAPSBOMItem.QTType);
			// 质量损失小类
			wPro21 = String.valueOf(wAPSBOMItem.QTItemType);
			// 可供料标识
			wPro22 = wAPSBOMItem.CustomerMaterial > 0 ? "X" : "";

			wResult = new ArrayList<String>(
					Arrays.asList(wPro1, wPro2, wPro3, wPro4, wPro5, wPro6, wPro7, wPro8, wPro9, wPro10, wPro11, wPro12,
							wPro13, wPro14, wPro15, wPro16, wPro17, wPro18, wPro19, wPro20, wPro21, wPro22));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据修程、工位查询工序列表
	 * 
	 * @return
	 */
	public List<Integer> FMC_QueryStepIDList(BMSEmployee wLoginUser, int wLineID, int wPartID, int wProductID) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			List<FMCLineUnit> wLineUnitList = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(wLoginUser, wLineID, -1, wProductID, false).List(FMCLineUnit.class);
			if (wLineUnitList == null || wLineUnitList.size() <= 0) {
				return wResult;
			}

			// 找到此工位的所有工序
			wLineUnitList = wLineUnitList.stream().filter(p -> p.LevelID == 3 && p.ParentUnitID == wPartID)
					.collect(Collectors.toList());
			if (wLineUnitList == null || wLineUnitList.size() <= 0) {
				return wResult;
			}

			wResult = wLineUnitList.stream().map(p -> p.UnitID).distinct().collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 通过工艺工序列表和工序ID获取前工序ID列表
	 * 
	 * @param wRoutePartPointList 同路线同工位 工艺工序列表
	 * @param wPartPointID        工序ID
	 * @return 前工序ID集合
	 */
	public List<Integer> FPC_QueryPreStepIDList(List<FPCRoutePartPoint> wRoutePartPointList, int wPartPointID) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			if (wRoutePartPointList == null || wRoutePartPointList.size() <= 0) {
				return wResult;
			}

			// ①根据工序找到工艺工序
			if (wRoutePartPointList.stream().anyMatch(p -> p.PartPointID == wPartPointID)) {
				FPCRoutePartPoint wRoutPartPoint = wRoutePartPointList.stream()
						.filter(p -> p.PartPointID == wPartPointID).findFirst().get();
				// ②通过PrevStepID找
				if (wRoutePartPointList.stream().anyMatch(p -> p.PartPointID == wRoutPartPoint.PrevStepID)) {
					FPCRoutePartPoint wPrevRoutePartPoint = wRoutePartPointList.stream()
							.filter(p -> p.PartPointID == wRoutPartPoint.PrevStepID).findFirst().get();
					wResult.add(wPrevRoutePartPoint.PartPointID);
					// ④递归查找
					wResult.addAll(FPC_QueryPreStepIDList(wRoutePartPointList, wPrevRoutePartPoint.PartPointID));
				}
				// ③通过NextStepIDMap找
				if (wRoutePartPointList.stream().anyMatch(p -> p.NextStepIDMap != null && p.NextStepIDMap.size() > 0
						&& p.NextStepIDMap.containsKey(String.valueOf(wRoutPartPoint.PartPointID)))) {
					List<FPCRoutePartPoint> wList = wRoutePartPointList.stream()
							.filter(p -> p.NextStepIDMap != null && p.NextStepIDMap.size() > 0
									&& p.NextStepIDMap.containsKey(String.valueOf(wRoutPartPoint.PartPointID)))
							.collect(Collectors.toList());
					for (FPCRoutePartPoint wFPCRoutePartPoint : wList) {
						wResult.add(wFPCRoutePartPoint.PartPointID);
						// ⑤递归查找
						wResult.addAll(FPC_QueryPreStepIDList(wRoutePartPointList, wFPCRoutePartPoint.PartPointID));
					}
				}
			}

			// 去重
			wResult = wResult.stream().distinct().collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 根据工艺工位列表、工位ID获取前工位ID列表
	 * 
	 * @param wRoutePartList
	 * @param wPartID
	 * @return
	 */
	public List<Integer> FPC_QueryPreStationIDList(List<FPCRoutePart> wRoutePartList, int wPartID) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				return wResult;
			}

			// ①根据工序找到工艺工序
			if (wRoutePartList.stream().anyMatch(p -> p.PartID == wPartID)) {
				FPCRoutePart wRoutPart = wRoutePartList.stream().filter(p -> p.PartID == wPartID).findFirst().get();
				// ②通过PrevStepID找
				if (wRoutePartList.stream().anyMatch(p -> p.PartID == wRoutPart.PrevPartID)) {
					FPCRoutePart wPrevRoutePart = wRoutePartList.stream().filter(p -> p.PartID == wRoutPart.PrevPartID)
							.findFirst().get();
					wResult.add(wPrevRoutePart.PartID);
					// ④递归查找
					wResult.addAll(FPC_QueryPreStationIDList(wRoutePartList, wPrevRoutePart.PartID));
				}
				// ③通过NextStepIDMap找
				if (wRoutePartList.stream().anyMatch(p -> p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
						&& p.NextPartIDMap.containsKey(String.valueOf(wRoutPart.PartID)))) {
					List<FPCRoutePart> wList = wRoutePartList.stream()
							.filter(p -> p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
									&& p.NextPartIDMap.containsKey(String.valueOf(wRoutPart.PartID)))
							.collect(Collectors.toList());
					for (FPCRoutePart wFPCRoutePart : wList) {
						wResult.add(wFPCRoutePart.PartID);
						// ⑤递归查找
						wResult.addAll(FPC_QueryPreStationIDList(wRoutePartList, wFPCRoutePart.PartID));
					}
				}
			}

			// 去重
			wResult = wResult.stream().distinct().collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<Integer> FPC_QueryPreStationIDListOnlyOne(List<FPCRoutePart> wRoutePartList, int wPartID) {
		List<Integer> wResult = new ArrayList<>();
		try {
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				return wResult;
			}

			if (wRoutePartList.stream().anyMatch(p -> (p.PartID == wPartID))) {
				FPCRoutePart wRoutPart = wRoutePartList.stream().filter(p -> (p.PartID == wPartID)).findFirst().get();

				if (wRoutePartList.stream().anyMatch(p -> (p.PartID == wRoutPart.PrevPartID))) {
					FPCRoutePart wPrevRoutePart = wRoutePartList.stream()
							.filter(p -> (p.PartID == wRoutPart.PrevPartID)).findFirst().get();
					wResult.add(Integer.valueOf(wPrevRoutePart.PartID));
				}

				if (wRoutePartList.stream().anyMatch(p -> (p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
						&& p.NextPartIDMap.containsKey(String.valueOf(wRoutPart.PartID))))) {
					List<FPCRoutePart> wList = (List<FPCRoutePart>) wRoutePartList.stream()
							.filter(p -> (p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
									&& p.NextPartIDMap.containsKey(String.valueOf(wRoutPart.PartID))))
							.collect(Collectors.toList());
					for (FPCRoutePart wFPCRoutePart : wList) {
						wResult.add(Integer.valueOf(wFPCRoutePart.PartID));
					}
				}
			}

			wResult = (List<Integer>) wResult.stream().distinct().collect(Collectors.toList());
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取日期所在月最后一天
	 * 
	 * @param wDate
	 * @return
	 */
	public Calendar getLastDayOfMonth(Calendar wDate) {
		Calendar wResult = Calendar.getInstance();
		try {
			wResult = wDate;
			int wLast = wResult.getActualMaximum(Calendar.DAY_OF_MONTH);
			wResult.set(Calendar.DAY_OF_MONTH, wLast);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取指定日期所在周最后一天
	 * 
	 * @param dataStr
	 * @param dateFormat
	 * @param resultDateFormat
	 * @return
	 */
	public Calendar getLastOfWeek(Calendar wDate) {
		Calendar wResult = Calendar.getInstance();
		try {
			int wD = 0;
			if (wDate.get(Calendar.DAY_OF_WEEK) == 1) {
				wD = -6;
			} else {
				wD = 2 - wDate.get(Calendar.DAY_OF_WEEK);
			}
			wDate.add(Calendar.DAY_OF_WEEK, wD);
			wDate.add(Calendar.DAY_OF_WEEK, 6);
			wResult = wDate;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 判断是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNumber(String wStr) {
		boolean wResult = false;
		try {
//			String wReg = "^[0-9]+(.[0-9]+)?$";

			String wReg = "-[0-9]+(.[0-9]+)?|[0-9]+(.[0-9]+)?";

			wResult = wStr.matches(wReg);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 计算开始时间和结束时间之间的工作时长(去除休息日和工作开始之前的时间)
	 * 
	 * @param wStartTime    开始时间
	 * @param wEndTime      结束时间
	 * @param wHolidayList  休息日列表
	 * @param wWorkTimeSpot 每日开工时刻(例：8点)
	 * @return
	 */
	public double QMS_CalTimeDuration(Calendar wStartTime, Calendar wEndTime, List<Calendar> wHolidayList,
			int wWorkTimeSpot, int wLeaveTimeSpot) {
		double wResult = 0.0;
		try {
			// ①初始化输入条件
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}
			if (wStartTime.compareTo(wEndTime) >= 0) {
				return wResult;
			}
			if (wHolidayList == null) {
				wHolidayList = new ArrayList<Calendar>();
			}
			if (wLeaveTimeSpot <= wWorkTimeSpot) {
				return wResult;
			}
			// ②分三种情况计算
			int wIntevalDays = CalIntevalDays(wStartTime, wEndTime);
			// ③第一种：开始时间和结束时间是同一天
			if (wIntevalDays == 0) {
				// ④不考虑工作时间和休息日，直接计算时间间隔
				wResult = CalIntervalHour(wStartTime, wEndTime);
				return wResult;
			}
			// ⑤第二种：开始时间和结束时间相差一天
			if (wIntevalDays == 1) {
				// ⑥不考虑休息日，考虑工作时长，下班时间-开始时间+第二天的工作时长(若小于上班时间，结束时间-0点，若大于上班时间，结束时间-上班时间)
				Calendar wFirstDayLeave = (Calendar) wStartTime.clone();
				int wStartHour = wStartTime.get(Calendar.HOUR_OF_DAY);
				if (wStartHour >= wLeaveTimeSpot) {
					wFirstDayLeave.set(Calendar.HOUR_OF_DAY, 23);
					wFirstDayLeave.set(Calendar.MINUTE, 59);
					wFirstDayLeave.set(Calendar.SECOND, 59);
				} else {
					wFirstDayLeave.set(Calendar.HOUR_OF_DAY, wLeaveTimeSpot);
					wFirstDayLeave.set(Calendar.MINUTE, 0);
					wFirstDayLeave.set(Calendar.SECOND, 0);
				}
				double wFirstHour = CalIntervalHour(wStartTime, wFirstDayLeave);

				int wEndHour = wEndTime.get(Calendar.HOUR_OF_DAY);
				// 最后一天的上班时刻
				Calendar wLastDayWork = (Calendar) wEndTime.clone();
				double wLastHour = 0.0;
				if (wEndHour < wWorkTimeSpot) {
					wLastDayWork.set(Calendar.HOUR_OF_DAY, 0);
					wLastDayWork.set(Calendar.MINUTE, 0);
					wLastDayWork.set(Calendar.SECOND, 0);
					wLastHour = CalIntervalHour(wLastDayWork, wEndTime);
				} else {
					wLastDayWork.set(Calendar.HOUR_OF_DAY, wWorkTimeSpot);
					wLastDayWork.set(Calendar.MINUTE, 0);
					wLastDayWork.set(Calendar.SECOND, 0);
					wLastHour = CalIntervalHour(wLastDayWork, wEndTime);
				}

				wResult = wFirstHour + wLastHour;
			}
			// ⑦第三种：开始时间和结束时间相差多天
			if (wIntevalDays > 1) {
				// ⑧第一天和最后一天计算方式同第二种情况，中间天，考虑休息日(如果是休息日，不计算在内，否则用下班时间-上班时间)
				Calendar wS = (Calendar) wStartTime.clone();
				Calendar wE = (Calendar) wEndTime.clone();

				int wFirstInt = GetDateInt(wS);
				int wEndInt = GetDateInt(wE);
				while (GetDateInt(wS) <= GetDateInt(wE)) {
					if (GetDateInt(wS) == wFirstInt) {// 第一天
						Calendar wFirstDayLeave = (Calendar) wS.clone();
						int wStartHour = wS.get(Calendar.HOUR_OF_DAY);
						if (wStartHour >= wLeaveTimeSpot) {
							wFirstDayLeave.set(Calendar.HOUR_OF_DAY, 23);
							wFirstDayLeave.set(Calendar.MINUTE, 59);
							wFirstDayLeave.set(Calendar.SECOND, 59);
						} else {
							wFirstDayLeave.set(Calendar.HOUR_OF_DAY, wLeaveTimeSpot);
							wFirstDayLeave.set(Calendar.MINUTE, 0);
							wFirstDayLeave.set(Calendar.SECOND, 0);
						}
						double wFirstHour = CalIntervalHour(wS, wFirstDayLeave);
						wResult += wFirstHour;
					} else if (GetDateInt(wS) == wEndInt) {// 最后一天
						int wEndHour = wE.get(Calendar.HOUR_OF_DAY);
						Calendar wLastDayWork = (Calendar) wE.clone();
						double wLastHour = 0.0;
						if (wEndHour < wWorkTimeSpot) {
							wLastDayWork.set(Calendar.HOUR_OF_DAY, 0);
							wLastDayWork.set(Calendar.MINUTE, 0);
							wLastDayWork.set(Calendar.SECOND, 0);
							wLastHour = CalIntervalHour(wLastDayWork, wEndTime);
						} else {
							wLastDayWork.set(Calendar.HOUR_OF_DAY, wWorkTimeSpot);
							wLastDayWork.set(Calendar.MINUTE, 0);
							wLastDayWork.set(Calendar.SECOND, 0);
							wLastHour = CalIntervalHour(wLastDayWork, wEndTime);
						}
						wResult += wLastHour;
					} else {// 中间天
						double wWholeHour = CalWholeHour(wWorkTimeSpot, wLeaveTimeSpot, wHolidayList, wS);
						wResult += wWholeHour;
					}

					wS.add(Calendar.DATE, 1);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 计算一整天的工时
	 * 
	 * @param wWorkTimeSpot
	 * @param wLeaveTimeSpot
	 * @return
	 */
	private double CalWholeHour(int wWorkTimeSpot, int wLeaveTimeSpot, List<Calendar> wHolidayList, Calendar wDay) {
		double wResult = 0.0;
		try {
			if (wHolidayList == null) {
				wHolidayList = new ArrayList<Calendar>();
			}

			if (wHolidayList.stream().anyMatch(p -> GetDateInt(p) == GetDateInt(wDay))) {
				return wResult;
			}

			Calendar wStartTime = Calendar.getInstance();
			wStartTime.set(Calendar.HOUR_OF_DAY, wWorkTimeSpot);
			wStartTime.set(Calendar.MINUTE, 0);
			wStartTime.set(Calendar.SECOND, 0);

			Calendar wEndTime = Calendar.getInstance();
			wEndTime.set(Calendar.HOUR_OF_DAY, wLeaveTimeSpot);
			wEndTime.set(Calendar.MINUTE, 0);
			wEndTime.set(Calendar.SECOND, 0);

			wResult = CalIntervalHour(wStartTime, wEndTime);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 计算开始时间和结束时间之间的间隔小时
	 * 
	 * @param wStartTime 开始时间
	 * @param wEndTime   结束时间
	 * @return 间隔小时
	 */
	private double CalIntervalHour(Calendar wStartTime, Calendar wEndTime) {
		double wResult = 0.0;
		try {
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1);
			if (wStartTime == null) {
				wStartTime = wBaseTime;
			}
			if (wEndTime == null) {
				wEndTime = wBaseTime;
			}
			if (wStartTime.compareTo(wEndTime) >= 0) {
				return wResult;
			}

			long wStart = wStartTime.getTime().getTime();
			long wEnd = wEndTime.getTime().getTime();
			long wMinutes = (wEnd - wStart) / (1000 * 60);
			double wHour = (double) wMinutes / 60;
			wResult = formatDouble(wHour);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 四舍五入保留两位小数
	 * 
	 * @param wNumber
	 * @return
	 */
	public double formatDouble(double wNumber) {
		double wResult = 0.0;
		try {
			// 如果不需要四舍五入，可以使用RoundingMode.DOWN
			BigDecimal wBG = new BigDecimal(wNumber).setScale(2, RoundingMode.UP);
			wResult = wBG.doubleValue();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取两个时间之前的间隔天数
	 * 
	 * @param wStartTime 开始时间
	 * @param wEndTime   结束时间
	 * @return
	 */
	public int CalIntevalDays(Calendar wStartTime, Calendar wEndTime) {
		int wResult = 0;
		try {
			if (wStartTime.compareTo(wEndTime) >= 0) {
				return wResult;
			}

			Calendar wS = (Calendar) wStartTime.clone();
			Calendar wE = (Calendar) wEndTime.clone();

			while (GetDateInt(wS) < GetDateInt(wE)) {
				wResult++;
				wS.add(Calendar.DATE, 1);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取时间的日期部分格式化整数(例如：2020-07-12 22:55:55->20200712)
	 * 
	 * @param wTime 时间
	 * @return
	 */
	public int GetDateInt(Calendar wTime) {
		int wResult = 0;
		try {
			if (wTime == null) {
				return wResult;
			}

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");
			String wTimeStr = wSDF.format(wTime.getTime());
			wResult = Integer.parseInt(wTimeStr);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 返回错误消息提示
	 * 
	 * @param wAPIResult
	 * @return
	 */
	public String GetMsg(APIResult wAPIResult) {
		String wResult = "";
		try {
			if (wAPIResult == null || wAPIResult.getReturnObject() == null
					|| !wAPIResult.getReturnObject().containsKey("msg")) {
				return wResult;
			}

			wResult = (String) wAPIResult.getReturnObject().get("msg");
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取后续工位列表
	 */
	public List<Integer> FPC_QueryNextStationIDListOnlyOne(List<FPCRoutePart> wRoutePartList, int wStationID) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			if (wRoutePartList == null || wRoutePartList.size() <= 0 || wStationID <= 0) {
				return wResult;
			}

			if (!wRoutePartList.stream().anyMatch(p -> p.PartID == wStationID)) {
				return wResult;
			}

			FPCRoutePart wRoutePart = wRoutePartList.stream().filter(p -> p.PartID == wStationID).findFirst().get();
			if (wRoutePart.NextPartIDMap != null && wRoutePart.NextPartIDMap.size() > 0) {
				for (String wPartStr : wRoutePart.NextPartIDMap.keySet()) {
					if (StringUtils.isEmpty(wPartStr)) {
						continue;
					}

					Integer wPartID = StringUtils.parseInt(wPartStr);
					if (wPartID <= 0 || wResult.stream().anyMatch(p -> p.intValue() == wPartID)) {
						continue;
					}

					wResult.add(wPartID);
				}
			}

			if (wRoutePartList.stream().anyMatch(p -> p.PrevPartID == wStationID)) {
				List<FPCRoutePart> wList = wRoutePartList.stream().filter(p -> p.PrevPartID == wStationID)
						.collect(Collectors.toList());
				for (FPCRoutePart wFPCRoutePart : wList) {
					if (wResult.stream().anyMatch(p -> p.intValue() == wFPCRoutePart.PartID)
							|| wFPCRoutePart.PartID <= 0) {
						continue;
					}
					wResult.add(wFPCRoutePart.PartID);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 导出标准BOM
	 */
	public void IPT_ExportMSSBOM(BMSEmployee wLoginUser, FileOutputStream wOutputStream, List<MSSBOM> wItemList) {
		try {
			String[] wHeaders = { "类", "BOM", "BOM类型", "车型", "修程", "局段信息", "工厂", "创建日期", "时间", "创建者", "更改日期", "时间",
					"更改者", "删除标识" };

			List<List<String>> wRowList = GetRowList(wLoginUser, wItemList);

			ExcelUtil.CreateFirst(wHeaders, "标准BOM");
			ExcelUtil.CreateOtherRow(wRowList);
			ExcelUtil.Export(wOutputStream);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取BOM数据
	 */
	private List<List<String>> GetRowList(BMSEmployee wLoginUser, List<MSSBOM> wItemList) {
		List<List<String>> wResult = new ArrayList<List<String>>();
		try {
			if (wItemList == null || wItemList.size() <= 0) {
				return wResult;
			}

			SimpleDateFormat wDateF = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat wTimeF = new SimpleDateFormat("HH:mm:ss");

			for (MSSBOM wMSSBOM : wItemList) {
				List<String> wList = new ArrayList<String>();

				wList.add("");
				wList.add(String.valueOf(wMSSBOM.ID));
				wList.add("2");
				wList.add(QMSConstants.GetFPCProductNo(wMSSBOM.ProductID));
				wList.add(wMSSBOM.LineID == 2 ? "6" : "5");

				wList.add(QMSConstants.GetCRMCustomer(wMSSBOM.CustomerID).TaxCode);
				wList.add("1900");

				wList.add(wDateF.format(wMSSBOM.AuditTime.getTime()));
				wList.add(wTimeF.format(wMSSBOM.AuditTime.getTime()));

				wList.add(QMSConstants.GetBMSEmployeeLoginID(wMSSBOM.Author));

				wList.add(wDateF.format(wMSSBOM.EditTime.getTime()));
				wList.add(wTimeF.format(wMSSBOM.EditTime.getTime()));

				wList.add(QMSConstants.GetBMSEmployeeLoginID(wMSSBOM.Auditor));

				wList.add("");

				wResult.add(wList);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 导出标准BOM子项
	 */
	public void IPT_ExportMSSBOMItem(BMSEmployee wLoginUser, FileOutputStream wOutputStream, int wBOMID) {
		try {
			String[] wHeaders = { "行号", "工位描述", "工位", "工序描述", "物料编码", "物料描述", "单位", "BOM类型", "数量", "必换偶换", "必修偶修", "备注",
					"状态", "编辑时间" };

			List<List<String>> wRowList = GetRowList(wLoginUser, wBOMID);

			ExcelUtil.CreateFirst(wHeaders, "标准BOM子项");
			ExcelUtil.CreateOtherRow(wRowList);
			ExcelUtil.Export(wOutputStream);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取BOM子项
	 */
	private List<List<String>> GetRowList(BMSEmployee wLoginUser, int wBOMID) {
		List<List<String>> wResult = new ArrayList<List<String>>();
		try {
			List<MSSBOMItem> wList = WMSServiceImpl.getInstance()
					.MSS_QueryBOMItemAll(wLoginUser, wBOMID, -1, -1, -1, -1, -1, -1, -1).List(MSSBOMItem.class);

			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			SimpleDateFormat wDateF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			int wFlag = 1;
			for (MSSBOMItem wMSSBOMItem : wList) {
				List<String> wItemList = new ArrayList<String>();

				wItemList.add(String.valueOf(wFlag));
				wItemList.add(QMSConstants.GetFPCPartName(wMSSBOMItem.PlaceID));
				wItemList.add(QMSConstants.GetFPCPartCode(wMSSBOMItem.PlaceID));
				wItemList.add(QMSConstants.GetFPCStepName(wMSSBOMItem.PartPointID));
				wItemList.add(wMSSBOMItem.MaterialNo);
				wItemList.add(wMSSBOMItem.MaterialName);
				wItemList.add(wMSSBOMItem.UnitText);
				wItemList.add(wMSSBOMItem.BOMType == 1 ? "新造" : "检修");
				wItemList.add(String.valueOf(wMSSBOMItem.MaterialNumber));
				wItemList.add(wMSSBOMItem.ReplaceType == 1 ? "必换" : wMSSBOMItem.ReplaceType == 2 ? "偶换" : "");
				wItemList.add(wMSSBOMItem.OutsourceType == 1 ? "委外必修"
						: wMSSBOMItem.OutsourceType == 2 ? "委外偶修"
								: wMSSBOMItem.OutsourceType == 3 ? "自修必修"
										: wMSSBOMItem.OutsourceType == 4 ? "自修偶修" : "");
				wItemList.add(wMSSBOMItem.Remark);
				wItemList.add(wMSSBOMItem.Active == 2 ? "禁用" : "启用");
				wItemList.add(wDateF.format(wMSSBOMItem.EditTime.getTime()));

				wResult.add(wItemList);

				wFlag++;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public static final String appId = "6756f1a21a5746afad96b74d42189cd4";
	public static final String signKey = "shrismcis";
	public static final String appidKey = "shrismcis";
	public static final String appSecret = "shrismcis";

	/**
	 * 获取签名(文件预览)
	 */
	public String getSignature(Map<String, Integer> params, String appId, String appSecret) {
		String sign = "";
		try {
			List<String> keys = new ArrayList<String>();
			for (Map.Entry<String, Integer> entry : params.entrySet()) {
				keys.add(entry.getKey());
			}

			// 将所有参数按key的升序排序
			Collections.sort(keys, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});

			// 构造签名的源字符串
			StringBuilder contents = new StringBuilder(appId);
			for (String key : keys) {

				if (key == signKey || key == appidKey) {
					continue;
				}
				contents.append(key).append(params.get(key));
			}
			contents.append(appSecret);

			System.out.println(appSecret);
			System.out.println(contents.toString());

			// 进行hmac sha1 签名
			@SuppressWarnings("deprecation")
			byte[] bytes = HmacUtils.hmacSha1(appSecret.getBytes(), contents.toString().getBytes());

			// 字符串经过Base64编码
			sign = Base64.encodeBase64String(bytes);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return sign;
	}

}
