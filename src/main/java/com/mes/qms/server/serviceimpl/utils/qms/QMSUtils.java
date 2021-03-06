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
 * ???????????????
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
	 * ??????????????????
	 * 
	 * @param wDepartmentIDs    ??????ID???????????????
	 * @param wBMSDepartmentMap ????????????
	 * @return ????????????
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
	 * ??????????????????
	 * 
	 * @param wEmployeeIDs    ??????ID???????????????
	 * @param wBMSEmployeeMap ????????????
	 * @return ????????????
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
				wResult = "?????????" + wIPTItem.StandardLeft + "<n<" + wIPTItem.StandardRight;
				break;
			case RangeEQ:
				wResult = "?????????" + wIPTItem.StandardLeft + "???n???" + wIPTItem.StandardRight;
				break;
			case RangeLTE:
				wResult = "?????????" + wIPTItem.StandardLeft + "<n???" + wIPTItem.StandardRight;
				break;
			case RangeGTE:
				wResult = "?????????" + wIPTItem.StandardLeft + "???n<" + wIPTItem.StandardRight;
				break;
			case LT:
				wResult = "?????????" + "n<" + wIPTItem.StandardRight;
				break;
			case GT:
				wResult = "?????????" + "n>" + wIPTItem.StandardLeft;
				break;
			case LTE:
				wResult = "?????????" + "n???" + wIPTItem.StandardRight;
				break;
			case GTE:
				wResult = "?????????" + "n???" + wIPTItem.StandardLeft;
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

			String wNewValue = wValueSourceCol.replaceAll("???", ";");
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
			case "??????":
				wResult = IPTStandardType.Combo.getValue();
				break;
			case "??????":
				wResult = IPTStandardType.Check.getValue();
				break;
			case "????????????":
				wResult = IPTStandardType.LTE.getValue();
				break;
			case "????????????":
				wResult = IPTStandardType.GTE.getValue();
				break;
			case "??????":
				wResult = IPTStandardType.LT.getValue();
				break;
			case "??????":
				wResult = IPTStandardType.GT.getValue();
				break;
			case "??????":
				wResult = IPTStandardType.EQ.getValue();
				break;
			case "????????????":
				wResult = IPTStandardType.RangeLTE.getValue();
				break;
			case "????????????":
				wResult = IPTStandardType.RangeEQ.getValue();
				break;
			case "????????????":
				wResult = IPTStandardType.RangeGTE.getValue();
				break;
			case "????????????":
				wResult = IPTStandardType.Range.getValue();
				break;
			case "??????":
				wResult = IPTStandardType.Text.getValue();
				break;
			case "??????":
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
				if (wExcelLineData.colData.get(0).equals("??????")) {
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
	 * ??????????????????
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
					wIPTStandard.Remark = StringUtils.Format("???{0}-{1}-{2}-{3}???-{4}-????????????",
							QMSConstants.GetFPCProductNo(wProductID), QMSConstants.GetFMCLineName(wLineID),
							wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(0),
							wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(1), wCurTime);
					long wID = IPTStandardDAO.getInstance().InsertStandard(wLoginUser, wIPTStandard, wErrorCode).Result;
					wIPTStandard.ID = wID;
					wStandardList.add(wIPTStandard);
				}
				// ????????????
				int wLevel = wExcelData.sheetData.get(0).lineData.get(0).colSum - 11;
				if (wExcelData.sheetData.get(0).lineData.get(0).colSum == 22) {
					wLevel = wExcelData.sheetData.get(0).lineData.get(0).colSum - 17;
				}
				List<String> wColData = wExcelData.sheetData.get(0).lineData.get(wIndex).colData;
				// ????????????
				String wStandard = wColData.get(wLevel + 2);
				// ?????????
				String wProjectNo = wColData.get(wLevel + 3);
				// ?????????
				String wCheckPoint = wColData.get(wLevel + 4);
				// ?????????
				String wType = wColData.get(wLevel + 5);
				// ??????
				String wUnit = wColData.get(wLevel + 6);
				// ???????????????
				String wValueFill = wColData.get(wLevel + 7);
				// ????????????
				String wOptionList = wColData.get(wLevel + 8);
				// ????????????
				String wPictureFill = wColData.get(wLevel + 9);
				// ????????????
				String wVideoFill = wColData.get(wLevel + 10);
				// ????????????
				String wYSCJ = wColData.size() > wLevel + 11 ? wColData.get(wLevel + 11) : "";
				// ????????????
				String wYSXH = wColData.size() > wLevel + 12 ? wColData.get(wLevel + 12) : "";
				// ????????????
				String wCJBT = wColData.size() > wLevel + 13 ? wColData.get(wLevel + 13) : "";
				// ????????????
				String wXHBT = wColData.size() > wLevel + 14 ? wColData.get(wLevel + 14) : "";
				// ????????????
				String wCJXX = wColData.size() > wLevel + 15 ? wColData.get(wLevel + 15) : "";
				// ????????????
				String wXHXX = wColData.size() > wLevel + 16 ? wColData.get(wLevel + 16) : "";

				// ??????????????????
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
						// ??????????????????
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

			// ????????????????????????

		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	private int GetIsVideoFill(String wVideoFill) {
		int wResult = 0;
		try {
			switch (wVideoFill) {
			case "???":
				wResult = 1;
				break;
			case "???":
				wResult = 2;
				break;
			case "?????????":
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
			case "???":
				wResult = 1;
				break;
			case "???":
				wResult = 2;
				break;
			case "?????????":
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
			case "???":
				wResult = 1;
				break;
			case "???":
				wResult = 2;
				break;
			case "?????????":
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
	 * ??????????????????
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

			// ?????????
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
					wIPTStandard.Remark = StringUtils.Format("???{0}-{1}-{2}???-{3}-{4}??????",
							QMSConstants.GetFPCProductNo(wProductID), QMSConstants.GetFMCLineName(wLineID),
							wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(0), wCurTime,
							wIPTMode == IPTMode.PreCheck.getValue() ? "??????"
									: wIPTMode == IPTMode.QTXJ.getValue() ? "??????" : "??????");
					long wID = IPTStandardDAO.getInstance().InsertStandard(wLoginUser, wIPTStandard, wErrorCode).Result;
					wIPTStandard.ID = wID;
					wStandardList.add(wIPTStandard);
					wResult.add((int) wID);
				}
				// ????????????
				int wLevel = 5;
				// ????????????
				String wStandard = wLevel + 1 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 1)
						: "";
				// ????????????
				String wPreFactor = wLevel + 2 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 2)
						: "";
				// ????????????
				String wPreModal = wLevel + 3 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 3)
						: "";
				// ????????????
				String wFactorFill = wLevel + 4 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 4)
						: "";
				// ????????????
				String wModalFill = wLevel + 5 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 5)
						: "";
				// ????????????
				String wNumberFill = wLevel + 6 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 6)
						: "";
				// ???????????????
				String wType = wLevel + 7 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 7)
						: "";
				// ???????????????
				String wValueFill = wLevel + 8 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 8)
						: "";
				// ??????
				String wUnit = wLevel + 9 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 9)
						: "";
				// ?????????????????????
				String wOptionList = wLevel + 10 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 10)
						: "";
				// ????????????
				String wFactorList = wLevel + 11 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 11)
						: "";
				// ????????????
				String wModalList = wLevel + 12 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 12)
						: "";
				// ??????????????????
				String wPreStation = wLevel + 13 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 13)
						: "";
				// ??????????????????
				String wPrePartPoint = wLevel + 14 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 14)
						: "";
				// ????????????
				String wPictureFill = wLevel + 16 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 16)
						: "";
				// ????????????
				String wVideoFill = wLevel + 17 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 17)
						: "";
				// ?????????
				String wStandardValue = wLevel + 18 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 18)
						: "";
				// ?????????
				String wLeft = wLevel + 19 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 19)
						: "";
				// ?????????
				String wRight = wLevel + 20 < wExcelData.sheetData.get(0).lineData.get(wIndex).colData.size()
						? wExcelData.sheetData.get(0).lineData.get(wIndex).colData.get(wLevel + 20)
						: "";

				// ??????????????????
				IPTStandard wTempStandard = wIPTStandard;
				int wMaxLevel = GetLineMaxLevel(1, wExcelData.sheetData.get(0).lineData.get(wIndex), wLevel);

				// ??????????????????
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
	 * ??????????????????
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
			case PreCheck:// ????????????
				wItemType = IPTItemType.InPlant.getValue();
				break;
			case QTXJ:// ????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 1, wItem1, "", "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);
				}
				// ?????????
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
				// ???????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 1, wItem1, "", "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo1 = wInfo;
				}
				// ???????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 2, wItem1, wItem2, "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo2 = wInfo;
				}
				// ???????????????
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
				// ???????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 1, wItem1, "", "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo1 = wInfo;
				}
				// ???????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 2, wItem1, wItem2, "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo2 = wInfo;
				}
				// ???????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 3, wItem1, wItem2, wItem3, "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo3 = wInfo;
				}
				// ???????????????
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
				// ???????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 1, wItem1, "", "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo1 = wInfo;
				}
				// ???????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 2, wItem1, wItem2, "", "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo2 = wInfo;
				}
				// ???????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 3, wItem1, wItem2, wItem3, "", (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo3 = wInfo;
				}
				// ???????????????
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
					// ????????????????????????
					IPTGroupInfo wInfo = new IPTGroupInfo(wStepName, 4, wItem1, wItem2, wItem3, wItem4, (int) wItemID);
					wIPTGroupInfoList.add(wInfo);

					wIPTGroupInfo4 = wInfo;
				}
				// ???????????????
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
	 * ??????????????????
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

			// BOM??????
			List<MSSBOM> wBOMList = WMSServiceImpl.getInstance().MSS_QueryBOMAll(wLoginUser, "", "", -1, -1, -1, -1)
					.List(MSSBOM.class);
			// ??????????????????
			List<FMCLineUnit> wLineUnitList = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(wLoginUser, -1, -1, -1, false).List(FMCLineUnit.class);
			// ????????????
			List<FPCPart> wPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(wLoginUser, -1, -1, -1, -1)
					.List(FPCPart.class);
			// ????????????
			List<FPCPartPoint> wPartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryPartPointList(wLoginUser, -1, -1, -1).List(FPCPartPoint.class);
			// ????????????
			List<CRMCustomer> wCustomerList = SCMServiceImpl.getInstance()
					.CRM_QueryCustomerList(wLoginUser, "", -1, -1, -1, -1).List(CRMCustomer.class);
			// ??????????????????
			List<BMSWorkCharge> wWorkChargeList = CoreServiceImpl.getInstance()
					.BMS_QueryWorkChargeList(wLoginUser, -1, -1, -1).List(BMSWorkCharge.class);
			// ????????????
			List<FMCLine> wLineList = FMCServiceImpl.getInstance().FMC_QueryLineList(wLoginUser, -1, -1, -1)
					.List(FMCLine.class);
			// ????????????
			List<FPCProduct> wProductList = FMCServiceImpl.getInstance().FPC_QueryProductList(wLoginUser, -1, -1)
					.List(FPCProduct.class);
			// ????????????
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
				// BOM??????
				wBOMNO = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(0)
						: "";
				// BOM??????
				wMaterialName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(1)
						: "";
				// ????????????
				wMaterialNo = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(2)
						: "";
				// ??????
				wProductNo = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(3)
						: "";
				// ??????
				wLineName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(4)
						: "";
				// ??????
				wCustomerName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(5)
						: "";
				// ??????
				wPartName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(6)
						: "";
				// ??????
				wPartPointName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(7)
						: "";
				// ?????????
				wPartPointNumber = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(8)
						: "";
				// ??????
				wClassName = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(13)
						: "";
				// ????????????ID
				wPartPointCode = wExcelLineDataList.get(i).colData.size() >= wMinCount
						? wExcelLineDataList.get(i).colData.get(14)
						: "";

				// ???????????????
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
	 * ???????????????(??????????????????)
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

			// ????????????
			int wLineID = 0;
			if ((wLineList != null && wLineList.stream().anyMatch(p -> p.Name.equals(wLineName)))) {
				wLineID = wLineList.stream().filter(p -> p.Name.equals(wLineName)).findFirst().get().ID;
			}
			// ????????????
			int wPartID = 0;
			if (wPartList != null && wPartList.stream().anyMatch(p -> p.Code.equals(wPartName))) {
				wPartID = wPartList.stream().filter(p -> p.Code.equals(wPartName)).findFirst().get().ID;
			}
			// ????????????
			int wCustomerID = 0;
			if (wCustomerList != null && wCustomerList.stream().anyMatch(p -> p.CustomerCode.equals(wCustomerName))) {
				wCustomerID = wCustomerList.stream().filter(p -> p.CustomerCode.equals(wCustomerName)).findFirst()
						.get().ID;
			}
			// ????????????
			int wProductID = 0;
			if ((wProductList != null && wProductList.stream().anyMatch(p -> p.ProductNo.equals(wProductNo)))) {
				wProductID = wProductList.stream().filter(p -> p.ProductNo.equals(wProductNo)).findFirst().get().ID;
			}
			// ????????????
			int wClassID = 0;
			if (wDepartmentList != null && wDepartmentList.size() > 0 && wDepartmentList.stream()
					.anyMatch(p -> p.Name.equals(wClassName) && p.Type == BMSDepartmentType.Class.getValue())) {
				wClassID = wDepartmentList.stream()
						.filter(p -> p.Name.equals(wClassName) && p.Type == BMSDepartmentType.Class.getValue())
						.findFirst().get().ID;
			}
			// ??????????????????
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
			// ????????????
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
			// ??????????????????
			if (wPartID > 0 && wPartPointID > 0) {
				// ??????????????????
				SetFMCLineUnit(wLoginUser, wLineUnitList, wLineID, wPartID, wPartPointID, wTempLineID, wProductID);
			}
			// ??????BOM
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
			// ????????????
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
			// ??????BOM
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
	 * ??????????????????
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
				// ?????????
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
				// ?????????
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
	 * ????????????BOM
	 * 
	 * @param wLoginUser
	 * @param wExcelData
	 * @return
	 */
	public ServiceResult<String> IPT_ImportBOM(BMSEmployee wLoginUser, ExcelData wExcelData, String wFileName) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ???BOM?????????????????????
			if (!CoreServiceImpl.getInstance()
					.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 200100, 0, 0)
					.Info(Boolean.class)) {
				String wMsg = "????????????BOM????????????!";
				// ?????????????????????????????????
				AddImportRecord(wLoginUser, wFileName, wMsg, wExcelData.sheetData.get(0).lineSum - 4,
						IMPType.BOM.getValue());

				wResult.Result = wMsg;
				return wResult;
			}

			if (wExcelData == null || wExcelData.sheetData == null || wExcelData.sheetData.size() <= 0
					|| wExcelData.sheetData.get(0).lineData == null
					|| wExcelData.sheetData.get(0).lineData.size() <= 0) {

				String wMsg = "?????????Excel?????????????????????????????????!";
				// ?????????????????????????????????
				AddImportRecord(wLoginUser, wFileName, wMsg, wExcelData.sheetData.get(0).lineSum - 4,
						IMPType.BOM.getValue());

				wResult.Result = wMsg;
				return wResult;
			}

			// ??? ????????????
			List<FPCProduct> wProductList = FMCServiceImpl.getInstance().FPC_QueryProductList(wLoginUser, -1, -1)
					.List(FPCProduct.class);
			// ??? ????????????
			List<FMCLine> wLineList = FMCServiceImpl.getInstance().FMC_QueryLineList(wLoginUser, -1, -1, -1)
					.List(FMCLine.class);
			// ??? ????????????
			List<CRMCustomer> wCustomerList = SCMServiceImpl.getInstance()
					.CRM_QueryCustomerList(BaseDAO.SysAdmin, "", 0, 0, 0, 1).List(CRMCustomer.class);
			// ??? ????????????
			List<FPCPart> wPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(wLoginUser, -1, -1, -1, -1)
					.List(FPCPart.class);
			// ???????????????
			List<CFGUnit> wCFGUnitList = CoreServiceImpl.getInstance().CFG_QueryUnitList(BaseDAO.SysAdmin)
					.List(CFGUnit.class);
			// BOM??????
			List<MSSBOM> wBOMList = new ArrayList<MSSBOM>();
			List<MSSBOMItem> wBOMItemList = new ArrayList<MSSBOMItem>();
			// ????????????
			List<FPCRoutePartPoint> wRoutePartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartPointListByRouteID(wLoginUser, -1, -1).List(FPCRoutePartPoint.class);
			// ??????BOP
			List<FPCRoute> wRouteList = FMCServiceImpl.getInstance().FPC_QueryRouteList(wLoginUser, -1, -1, -1)
					.List(FPCRoute.class);

			if (wExcelData.sheetData.size() > 1) {
				List<ExcelLineData> wLineDataList = wExcelData.sheetData.get(1).lineData;
				// ??????????????????
				this.ImportCustomerList(wLoginUser, wLineDataList, wCustomerList);
			}

			List<ExcelLineData> wExcelLineDataList = wExcelData.sheetData.get(0).lineData;

			// ????????????
			List<MSSMaterial> wMaterialList = WMSServiceImpl.getInstance().MSS_QueryMaterialList(wLoginUser, "")
					.List(MSSMaterial.class);

			// ??????????????????
			String wTip = this.RecordErrorLog(wLoginUser, wExcelLineDataList, wFileName, wProductList, wLineList,
					wCustomerList, wPartList, wRoutePartPointList, wCFGUnitList, wMaterialList);
			if (StringUtils.isNotEmpty(wTip)) {
				wResult.Result = wTip;
				return wResult;
			}

			// ?????????????????????
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
				// BOM??????
				wBomType = wExcelLineDataList.get(i).colData.size() >= 1
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????
				wProductType = wExcelLineDataList.get(i).colData.size() >= 2
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				wLine = wExcelLineDataList.get(i).colData.size() >= 3
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????
				wCustormer = wExcelLineDataList.get(i).colData.size() >= 4
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				wFactory = wExcelLineDataList.get(i).colData.size() >= 5
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????
				wPart = wExcelLineDataList.get(i).colData.size() >= 6 ? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				wPartPoint = wExcelLineDataList.get(i).colData.size() >= 7
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ????????????
				wMaterialNo = wExcelLineDataList.get(i).colData.size() >= 8
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ????????????
				wMaterialName = wExcelLineDataList.get(i).colData.size() >= 9
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				wNumber = wExcelLineDataList.get(i).colData.size() >= 10
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				wUnit = wExcelLineDataList.get(i).colData.size() >= 11 ? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ???????????????
				wIsChange = wExcelLineDataList.get(i).colData.size() >= 12
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ?????????
				wChageRate = wExcelLineDataList.get(i).colData.size() >= 13
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ???????????????????????????
				wIsRepair = wExcelLineDataList.get(i).colData.size() >= 14
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????????????????
				wRequirment = wExcelLineDataList.get(i).colData.size() >= 15
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????????????????
				wIsOutTrain = wExcelLineDataList.get(i).colData.size() >= 16
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				wRemark = wExcelLineDataList.get(i).colData.size() >= 17
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ?????????
				wCrafter = wExcelLineDataList.get(i).colData.size() >= 18
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ????????????
				wParentMaterial = wExcelLineDataList.get(i).colData.size() >= 19
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";

				// ??????BOMItem???
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

					// ?????????????????????????????????
					AddImportRecord(wLoginUser, wFileName, wMsg.Result, wExcelData.sheetData.get(0).lineSum - 4,
							IMPType.BOM.getValue());

					return wMsg;
				}
			}

			wResult.CustomResult.put("BOMID", wBOMID);

			// ????????????????????????
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
				wResult.FaultCode += "?????????????????????!";
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
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

				// BOM??????
				String wBomType = wExcelLineDataList.get(i).colData.size() >= 1
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????
				String wProductType = wExcelLineDataList.get(i).colData.size() >= 2
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wLine = wExcelLineDataList.get(i).colData.size() >= 3
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????
				String wCustormer = wExcelLineDataList.get(i).colData.size() >= 4
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wFactory = wExcelLineDataList.get(i).colData.size() >= 5
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????
				String wPart = wExcelLineDataList.get(i).colData.size() >= 6
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wPartPoint = wExcelLineDataList.get(i).colData.size() >= 7
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ????????????
				String wMaterialNo = wExcelLineDataList.get(i).colData.size() >= 8
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ????????????
				wIndex++;
				// ??????
				String wNumber = wExcelLineDataList.get(i).colData.size() >= 10
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wUnit = wExcelLineDataList.get(i).colData.size() >= 11
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ???????????????
				String wChange = wExcelLineDataList.get(i).colData.size() >= 12
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ?????????
				String wRate = wExcelLineDataList.get(i).colData.size() >= 13
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ???????????????????????????
				String wRepair = wExcelLineDataList.get(i).colData.size() >= 14
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????????????????
				String wRequire = wExcelLineDataList.get(i).colData.size() >= 15
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????????????????
				String wOut = wExcelLineDataList.get(i).colData.size() >= 16
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wRemark = wExcelLineDataList.get(i).colData.size() >= 17
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ?????????
				String wCraft = wExcelLineDataList.get(i).colData.size() >= 18
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ????????????
				String wParentMaterial = wExcelLineDataList.get(i).colData.size() >= 19
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";

				// ??? ??????BOM??????
				if (StringUtils.isEmpty(wBomType)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}?????????????????????BOM??????????????????!", i + 1)));
				} else if (!wBomType.equals("1") && !wBomType.equals("2") && !wBomType.equals("2.0")
						&& !wBomType.equals("2.0")) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}?????????????????????BOM????????????????????????!", i + 1)));
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wProductType)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1)));
				} else if (!wProductList.stream().anyMatch(p -> p.ProductNo.equals(wProductType))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, wProductType)));
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wLine)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1)));
				} else if (!wLineList.stream().anyMatch(p -> p.Name.equals("C" + wLine))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, "C" + wLine)));
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wCustormer)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1)));
				} else if (!wCustomerList.stream().anyMatch(p -> p.CustomerCode.equals(wCustormer))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, wCustormer)));
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wFactory)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1)));
				} else if (!wFactory.equals("1900")) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}?????????????????????????????????????????????!", i + 1)));
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wPart)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1)));
				} else if (!wPartList.stream().anyMatch(p -> p.Code.equals(wPart))) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, wPart)));
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wPartPoint)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1)));
				} else if (!wStepList.stream().anyMatch(p -> p.Code.equals(wPartPoint))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, wPartPoint)));
				}
				// ??? ??????????????????
				if (StringUtils.isEmpty(wMaterialNo)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}?????????????????????????????????????????????!", i + 1)));
				}
				if (!wMaterialList.stream().anyMatch(p -> p.MaterialNo.equals(wMaterialNo))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("????????????{0}????????????????????????????????????{1}????????????!", i + 1, wMaterialNo)));
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wNumber)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1)));
				} else if (!isNumber(wNumber)) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}?????????????????????????????????????????????!", i + 1)));
				}
				// ????????????????????????
				if (!(wChange.equals("1") || wChange.equals("2") || wChange.equals(""))) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}???????????????????????????(1)/??????(2)??????????????????!", i + 1)));
				}
				// ??????????????????
				if (!(StringUtils.isEmpty(wRate) || this.isNumeric(wRate)
						|| (Double.parseDouble(wRate) >= 0 && Double.parseDouble(wRate) < 100))) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}????????????????????????????????????????????????!", i + 1)));
				}
				// ????????????????????????????????????
				if (!(StringUtils.isEmpty(wRepair) || wRepair.equals("1") || wRepair.equals("2"))) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}??????????????????????????????????????????????????????????????????!", i + 1)));
				}
				// ???????????????????????????
				if (!(StringUtils.isEmpty(wRequire) || wRequire.equals("X"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}?????????????????????????????????????????????????????????!", i + 1)));
				}
				// ???????????????????????????
				if (!(StringUtils.isEmpty(wOut) || wOut.equals("X"))) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("????????????{0}?????????????????????????????????????????????????????????!", i + 1)));
				}

				// ????????????????????????(???????????????????????????????????????????????????)
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
							.Format("????????????{0}????????????????????????{1}???-???{2}???-???{3}?????????????????????!", i + 1, wPart, wPartPoint, wMaterialNo)));
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
	 * ??????????????????????????????
	 * 
	 * @param str
	 * @return
	 */
	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * ??????BOM?????????????????????
	 * 
	 * @param wLoginUser         ????????????
	 * @param wExcelLineDataList Excel?????????
	 * @return ?????????????????????
	 */
	@SuppressWarnings("unused")
	private String RecordErrorLog(BMSEmployee wLoginUser, List<ExcelLineData> wExcelLineDataList, String wFileName,
			List<FPCProduct> wProductList, List<FMCLine> wLineList, List<CRMCustomer> wCustomerList,
			List<FPCPart> wPartList, List<FPCRoutePartPoint> wRoutePartPointList, List<CFGUnit> wCFGUnitList,
			List<MSSMaterial> wMaterialList) {
		String wResult = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ??????????????????????????????
			AddRecored(wLoginUser, wExcelLineDataList, wFileName, wProductList, wLineList, wCustomerList, wPartList,
					wRoutePartPointList, wCFGUnitList, wMaterialList, wErrorCode);

			for (int i = 4; i < wExcelLineDataList.size(); i++) {
				int wIndex = 0;

				// BOM??????
				String wBomType = wExcelLineDataList.get(i).colData.size() >= 1
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????
				String wProductType = wExcelLineDataList.get(i).colData.size() >= 2
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wLine = wExcelLineDataList.get(i).colData.size() >= 3
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????
				String wCustormer = wExcelLineDataList.get(i).colData.size() >= 4
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wFactory = wExcelLineDataList.get(i).colData.size() >= 5
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????
				String wPart = wExcelLineDataList.get(i).colData.size() >= 6
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wPartPoint = wExcelLineDataList.get(i).colData.size() >= 7
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ????????????
				String wMaterialNo = wExcelLineDataList.get(i).colData.size() >= 8
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ????????????
				wIndex++;
				// ??????
				String wNumber = wExcelLineDataList.get(i).colData.size() >= 10
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wUnit = wExcelLineDataList.get(i).colData.size() >= 11
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ???????????????
				String wChange = wExcelLineDataList.get(i).colData.size() >= 12
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ?????????
				String wRate = wExcelLineDataList.get(i).colData.size() >= 13
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ???????????????????????????
				String wRepair = wExcelLineDataList.get(i).colData.size() >= 14
						? wExcelLineDataList.get(i).colData.get(wIndex++).replace(".0", "")
						: "";
				// ??????????????????
				String wRequire = wExcelLineDataList.get(i).colData.size() >= 15
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????????????????
				String wOut = wExcelLineDataList.get(i).colData.size() >= 16
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ??????
				String wRemark = wExcelLineDataList.get(i).colData.size() >= 17
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ?????????
				String wCraft = wExcelLineDataList.get(i).colData.size() >= 18
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";
				// ????????????
				String wParentMaterial = wExcelLineDataList.get(i).colData.size() >= 19
						? wExcelLineDataList.get(i).colData.get(wIndex++)
						: "";

				// ??? ??????BOM??????
				if (StringUtils.isEmpty(wBomType)) {
					return StringUtils.Format("????????????{0}?????????????????????BOM??????????????????!", i + 1);
				} else if (!wBomType.equals("1") && !wBomType.equals("2")
						&& (!wBomType.equals("1.0") && !wBomType.equals("2.0"))) {
					return StringUtils.Format("????????????{0}?????????????????????BOM????????????????????????!", i + 1);
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wProductType)) {
					return StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1);
				} else if (!wProductList.stream().anyMatch(p -> p.ProductNo.equals(wProductType))) {
					return StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, wProductType);
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wLine)) {
					return StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1);
				} else if (!wLineList.stream().anyMatch(p -> p.Name.equals("C" + wLine))) {
					return StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, "C" + wLine);
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wCustormer)) {
					return StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1);
				} else if (!wCustomerList.stream().anyMatch(p -> p.CustomerCode.equals(wCustormer))) {
					return StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, wCustormer);
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wFactory)) {
					return StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1);
				} else if (!wFactory.equals("1900")) {
					return StringUtils.Format("????????????{0}?????????????????????????????????????????????!", i + 1);
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wPart)) {
					return StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1);
				} else if (!wPartList.stream().anyMatch(p -> p.Code.equals(wPart))) {
					return StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, wPart);
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wPartPoint)) {
					return StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1);
				} else if (!wRoutePartPointList.stream().anyMatch(p -> p.Code.equals(wPartPoint))) {
					return StringUtils.Format("????????????{0}????????????????????????{1}??????????????????!", i + 1, wPartPoint);
				}
				// ??? ??????????????????
				if (StringUtils.isEmpty(wMaterialNo)) {
					return StringUtils.Format("????????????{0}?????????????????????????????????????????????!", i + 1);
				}
				if (!wMaterialList.stream().anyMatch(p -> p.MaterialNo.equals(wMaterialNo))) {
					return StringUtils.Format("????????????{0}????????????????????????????????????{1}????????????!", i + 1, wMaterialNo);
				}
				// ??? ????????????
				if (StringUtils.isEmpty(wNumber)) {
					return StringUtils.Format("????????????{0}???????????????????????????????????????!", i + 1);
				} else if (!isNumber(wNumber)) {
					return StringUtils.Format("????????????{0}?????????????????????????????????????????????!", i + 1);
				}
				// ????????????????????????
				if (!(wChange.equals("1") || wChange.equals("2") || wChange.equals(""))) {
					return StringUtils.Format("????????????{0}???????????????????????????(1)/??????(2)??????????????????!", i + 1);
				}
				// ??????????????????
				if (!(StringUtils.isEmpty(wRate) || this.isNumeric(wRate)
						|| (Double.parseDouble(wRate) >= 0 && Double.parseDouble(wRate) < 100))) {
					return StringUtils.Format("????????????{0}????????????????????????????????????????????????!", i + 1);
				}
				// ????????????????????????????????????
				if (!(StringUtils.isEmpty(wRepair) || wRepair.equals("1") || wRepair.equals("2"))) {
					return StringUtils.Format("????????????{0}??????????????????????????????????????????????????????????????????!", i + 1);
				}
				// ???????????????????????????
				if (!(StringUtils.isEmpty(wRequire) || wRequire.equals("X"))) {
					return StringUtils.Format("????????????{0}?????????????????????????????????????????????????????????!", i + 1);
				}
				// ???????????????????????????
				if (!(StringUtils.isEmpty(wOut) || wOut.equals("X"))) {
					return StringUtils.Format("????????????{0}?????????????????????????????????????????????????????????!", i + 1);
				}
				// ????????????????????????(???????????????????????????????????????????????????)
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
					return StringUtils.Format("????????????{0}????????????????????????{1}???-???{2}???-???{3}?????????????????????!", i + 1, wPart, wPartPoint,
							wMaterialNo);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ??????????????????
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
	 * ????????????????????????
	 * 
	 * @param wLoginUser ????????????
	 * @param wFileName  ?????????
	 * @param wMsg       ????????????
	 * @param wDataCount ????????????
	 * @param wType      ????????????
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
	 * ??????BOMItem???
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
			// ??????
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

			// ????????????
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

			// ??????
			String wRealLine = wLine.equals("6") ? "C6" : "C5";
			int wLineID = 0;
			if (wLineList.stream().anyMatch(p -> p.Name.equals(wRealLine))) {
				wLineID = wLineList.stream().filter(p -> p.Name.equals(wRealLine)).findFirst().get().ID;
			}
			if (wLineID <= 0) {
				wMsg.Result = StringUtils.Format("????????????{0}?????????????????????!", wRealLine);
				return wMsg;
			}
			// ??????
			int wProductID = 0;
			if (wProductList.stream().anyMatch(p -> p.ProductNo.equals(wProductType))) {
				wProductID = wProductList.stream().filter(p -> p.ProductNo.equals(wProductType)).findFirst().get().ID;
			}
			if (wProductID <= 0) {
				wMsg.Result = StringUtils.Format("????????????{0}?????????????????????!", wProductType);
				return wMsg;
			}
			// ??????
			int wCustomerID = 0;
			if (wCustomerList.stream().anyMatch(p -> p.CustomerCode.equals(wCustormer))) {
				wCustomerID = wCustomerList.stream().filter(p -> p.CustomerCode.equals(wCustormer)).findFirst()
						.get().ID;
			}
			if (wCustomerID <= 0) {
				wMsg.Result = StringUtils.Format("????????????{0}?????????????????????!", wCustormer);
				return wMsg;
			}
			// ??????BOM(??????????????????????????????)
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
				// ??????RouteID
				if (wRouteList != null && wRouteList.size() > 0
						&& wRouteList.stream()
								.anyMatch(p -> p.ProductID == wMSSBOM.ProductID && p.LineID == wMSSBOM.LineID
										&& p.CustomerID == wMSSBOM.CustomerID && p.IsStandard == 1)) {
					wMSSBOM.RouteID = wRouteList.stream().filter(p -> p.ProductID == wMSSBOM.ProductID
							&& p.LineID == wMSSBOM.LineID && p.CustomerID == wMSSBOM.CustomerID && p.IsStandard == 1)
							.findFirst().get().ID;
				} else {
					wMsg.Result = "??????????????????BOP!";
					return wMsg;
				}
				MSSBOM wNewBom = WMSServiceImpl.getInstance().MSS_SaveBOM(wLoginUser, wMSSBOM).Custom("list",
						MSSBOM.class);
				if (wNewBom.ID > 0) {
					wBOMID = wNewBom.ID;
					wBOMList.add(wNewBom);
				}
				if (wBOMID <= 0) {
					wMsg.Result = "??????????????????BOM??????!";
					return wMsg;
				}
			}
			// ??????
			int wPartID = wPartList.stream().filter(p -> p.Code.equals(wPart)).findFirst().get().ID;
			// ??????
			int wPartPointID = wRoutePartPointList.stream().filter(p -> p.Code.equals(wPartPoint)).findFirst()
					.get().PartPointID;
			if (wPartPointID <= 0) {
				wMsg.Result = StringUtils.Format("????????????{0}?????????????????????!", wPartPoint);
				return wMsg;
			}
			// ??????BOM??????
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
			// ????????????
			if (StringUtils.isNotEmpty(wParentMaterial)) {
				wParentM = WMSServiceImpl.getInstance().MSS_QueryMaterialByID(wLoginUser, -1, wParentMaterial)
						.Info(MSSMaterial.class);
				wMSSBOMItem.ParentID = wParentM == null ? 0 : wParentM.getID();
			} else {
				wMSSBOMItem.ParentID = 0;
			}
			wBOMItemList.add(wMSSBOMItem);
			// ??????bom??????
//			wMSSBOMItem = WMSServiceImpl.getInstance().MSS_SaveBOMItem(wLoginUser, wMSSBOMItem).Info(MSSBOMItem.class);

			wMsg.CustomResult.put("BOMID", wBOMID);

			if ((wIndex - 3) % 100 == 0 || wIndex == wMaxIndex) {
				wIMPResultRecord.Progress = wIndex - 3;
				IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
				// ???????????????bom
				WMSServiceImpl.getInstance().MSS_SaveBOMItemList(wLoginUser, wBOMItemList);
				// ??????
				wBOMItemList = new ArrayList<MSSBOMItem>();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.toString());
		}
		return wMsg;
	}

	/**
	 * ??????????????????
	 * 
	 * @param wLineDataList
	 */
	private void ImportCustomerList(BMSEmployee wLoginUser, List<ExcelLineData> wLineDataList,
			List<CRMCustomer> wCustomerList) {
		try {
			if (wLineDataList == null || wLineDataList.size() <= 0) {
				return;
			}

			// ????????????
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

				// ??????????????????
				this.HandleCustomerRow(wLoginUser, wCustomerList, wCode, wName, wTaxCode);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ?????????????????????
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
	 * ??????BOM??????
	 * 
	 * @return
	 */
	public Integer IPT_ExportBOM(BMSEmployee wLoginUser, ServletOutputStream wOutputStream, MSSBOM wBOM,
			List<MSSBOMItem> wItemList) {
		int wResult = 0;
		try {
			List<List<String>> wRowList = new ArrayList<List<String>>();

			String[] wHeaders = { "BOM??????", "??????", "??????", "????????????", "??????", "??????", "????????????", "????????????", "????????????", "??????", "??????",
					"??????(1)/??????(2)", "?????????", "????????????1/????????????2", "??????????????????", "??????????????????", "??????" };

			ExcelUtil.CreateFirst(wHeaders, "??????BOM");
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
	 * ????????????BOM
	 * 
	 * @param wLoginUser   ????????????
	 * @param outputStream ???????????????
	 * @param wItemList    ??????bom????????????
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
	 * ????????????BOM???????????????Excel????????????
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

			// BOM??????
			wPro1 = String.valueOf(wAPSBOMItem.BOMType);
			// ??????
			wPro2 = wAPSBOMItem.ProductNo;
			// ??????
			wPro3 = StringUtils.isEmpty(wAPSBOMItem.LineName) ? "" : wAPSBOMItem.LineName.substring(1);
			// ??????
			wPro4 = wAPSBOMItem.CustomerCode;
			// ??????
			wPro5 = "1900";
			// WBS??????
			wPro6 = wAPSBOMItem.WBSNo;
			// ??????
			wPro7 = QMSConstants.GetFPCPart(wAPSBOMItem.PartID).Code;
			// ????????????
			wPro8 = wAPSBOMItem.PartPointName;
			// ????????????
			wPro9 = wAPSBOMItem.MaterialNo;
			// ????????????
			wPro10 = wAPSBOMItem.MaterialName;
			// ??????
			wPro11 = String.valueOf(wAPSBOMItem.Number);
			// ??????
			wPro12 = wAPSBOMItem.UnitText;
			// ??????/??????
			wPro13 = String.valueOf(wAPSBOMItem.ReplaceType);
			// ??????/??????
			wPro14 = String.valueOf(wAPSBOMItem.OutsourceType);
			// ????????????
			wPro15 = wAPSBOMItem.ReplaceType > 0 ? "????????????"
					: wAPSBOMItem.OutsourceType > 0 ? "????????????" : wAPSBOMItem.PartChange > 0 ? "????????????" : "";
			// ???????????????
			wPro16 = wAPSBOMItem.PartChange > 0 ? "X" : "";
			// ???????????????
			wPro17 = wAPSBOMItem.OverLine > 0 ? "X" : "";
			// ????????????
			wPro18 = "0001";
			// ?????????
			wPro19 = wAPSBOMItem.BOMType == 1 ? "1100" : wAPSBOMItem.BOMType == 2 ? "1200" : "";
			// ??????????????????
			wPro20 = String.valueOf(wAPSBOMItem.QTType);
			// ??????????????????
			wPro21 = String.valueOf(wAPSBOMItem.QTItemType);
			// ???????????????
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
	 * ???????????????????????????????????????
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

			// ??????????????????????????????
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
	 * ?????????????????????????????????ID???????????????ID??????
	 * 
	 * @param wRoutePartPointList ?????????????????? ??????????????????
	 * @param wPartPointID        ??????ID
	 * @return ?????????ID??????
	 */
	public List<Integer> FPC_QueryPreStepIDList(List<FPCRoutePartPoint> wRoutePartPointList, int wPartPointID) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			if (wRoutePartPointList == null || wRoutePartPointList.size() <= 0) {
				return wResult;
			}

			// ?????????????????????????????????
			if (wRoutePartPointList.stream().anyMatch(p -> p.PartPointID == wPartPointID)) {
				FPCRoutePartPoint wRoutPartPoint = wRoutePartPointList.stream()
						.filter(p -> p.PartPointID == wPartPointID).findFirst().get();
				// ?????????PrevStepID???
				if (wRoutePartPointList.stream().anyMatch(p -> p.PartPointID == wRoutPartPoint.PrevStepID)) {
					FPCRoutePartPoint wPrevRoutePartPoint = wRoutePartPointList.stream()
							.filter(p -> p.PartPointID == wRoutPartPoint.PrevStepID).findFirst().get();
					wResult.add(wPrevRoutePartPoint.PartPointID);
					// ???????????????
					wResult.addAll(FPC_QueryPreStepIDList(wRoutePartPointList, wPrevRoutePartPoint.PartPointID));
				}
				// ?????????NextStepIDMap???
				if (wRoutePartPointList.stream().anyMatch(p -> p.NextStepIDMap != null && p.NextStepIDMap.size() > 0
						&& p.NextStepIDMap.containsKey(String.valueOf(wRoutPartPoint.PartPointID)))) {
					List<FPCRoutePartPoint> wList = wRoutePartPointList.stream()
							.filter(p -> p.NextStepIDMap != null && p.NextStepIDMap.size() > 0
									&& p.NextStepIDMap.containsKey(String.valueOf(wRoutPartPoint.PartPointID)))
							.collect(Collectors.toList());
					for (FPCRoutePartPoint wFPCRoutePartPoint : wList) {
						wResult.add(wFPCRoutePartPoint.PartPointID);
						// ???????????????
						wResult.addAll(FPC_QueryPreStepIDList(wRoutePartPointList, wFPCRoutePartPoint.PartPointID));
					}
				}
			}

			// ??????
			wResult = wResult.stream().distinct().collect(Collectors.toList());
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ?????????????????????????????????ID???????????????ID??????
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

			// ?????????????????????????????????
			if (wRoutePartList.stream().anyMatch(p -> p.PartID == wPartID)) {
				FPCRoutePart wRoutPart = wRoutePartList.stream().filter(p -> p.PartID == wPartID).findFirst().get();
				// ?????????PrevStepID???
				if (wRoutePartList.stream().anyMatch(p -> p.PartID == wRoutPart.PrevPartID)) {
					FPCRoutePart wPrevRoutePart = wRoutePartList.stream().filter(p -> p.PartID == wRoutPart.PrevPartID)
							.findFirst().get();
					wResult.add(wPrevRoutePart.PartID);
					// ???????????????
					wResult.addAll(FPC_QueryPreStationIDList(wRoutePartList, wPrevRoutePart.PartID));
				}
				// ?????????NextStepIDMap???
				if (wRoutePartList.stream().anyMatch(p -> p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
						&& p.NextPartIDMap.containsKey(String.valueOf(wRoutPart.PartID)))) {
					List<FPCRoutePart> wList = wRoutePartList.stream()
							.filter(p -> p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
									&& p.NextPartIDMap.containsKey(String.valueOf(wRoutPart.PartID)))
							.collect(Collectors.toList());
					for (FPCRoutePart wFPCRoutePart : wList) {
						wResult.add(wFPCRoutePart.PartID);
						// ???????????????
						wResult.addAll(FPC_QueryPreStationIDList(wRoutePartList, wFPCRoutePart.PartID));
					}
				}
			}

			// ??????
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
	 * ?????????????????????????????????
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
	 * ???????????????????????????????????????
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
	 * ?????????????????????
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
	 * ??????????????????????????????????????????????????????(?????????????????????????????????????????????)
	 * 
	 * @param wStartTime    ????????????
	 * @param wEndTime      ????????????
	 * @param wHolidayList  ???????????????
	 * @param wWorkTimeSpot ??????????????????(??????8???)
	 * @return
	 */
	public double QMS_CalTimeDuration(Calendar wStartTime, Calendar wEndTime, List<Calendar> wHolidayList,
			int wWorkTimeSpot, int wLeaveTimeSpot) {
		double wResult = 0.0;
		try {
			// ????????????????????????
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
			// ????????????????????????
			int wIntevalDays = CalIntevalDays(wStartTime, wEndTime);
			// ??????????????????????????????????????????????????????
			if (wIntevalDays == 0) {
				// ???????????????????????????????????????????????????????????????
				wResult = CalIntervalHour(wStartTime, wEndTime);
				return wResult;
			}
			// ??????????????????????????????????????????????????????
			if (wIntevalDays == 1) {
				// ?????????????????????????????????????????????????????????-????????????+????????????????????????(????????????????????????????????????-0??????????????????????????????????????????-????????????)
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
				// ???????????????????????????
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
			// ??????????????????????????????????????????????????????
			if (wIntevalDays > 1) {
				// ???????????????????????????????????????????????????????????????????????????????????????(????????????????????????????????????????????????????????????-????????????)
				Calendar wS = (Calendar) wStartTime.clone();
				Calendar wE = (Calendar) wEndTime.clone();

				int wFirstInt = GetDateInt(wS);
				int wEndInt = GetDateInt(wE);
				while (GetDateInt(wS) <= GetDateInt(wE)) {
					if (GetDateInt(wS) == wFirstInt) {// ?????????
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
					} else if (GetDateInt(wS) == wEndInt) {// ????????????
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
					} else {// ?????????
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
	 * ????????????????????????
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
	 * ??????????????????????????????????????????????????????
	 * 
	 * @param wStartTime ????????????
	 * @param wEndTime   ????????????
	 * @return ????????????
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
	 * ??????????????????????????????
	 * 
	 * @param wNumber
	 * @return
	 */
	public double formatDouble(double wNumber) {
		double wResult = 0.0;
		try {
			// ??????????????????????????????????????????RoundingMode.DOWN
			BigDecimal wBG = new BigDecimal(wNumber).setScale(2, RoundingMode.UP);
			wResult = wBG.doubleValue();
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * ???????????????????????????????????????
	 * 
	 * @param wStartTime ????????????
	 * @param wEndTime   ????????????
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
	 * ??????????????????????????????????????????(?????????2020-07-12 22:55:55->20200712)
	 * 
	 * @param wTime ??????
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
	 * ????????????????????????
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
	 * ????????????????????????
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
	 * ????????????BOM
	 */
	public void IPT_ExportMSSBOM(BMSEmployee wLoginUser, FileOutputStream wOutputStream, List<MSSBOM> wItemList) {
		try {
			String[] wHeaders = { "???", "BOM", "BOM??????", "??????", "??????", "????????????", "??????", "????????????", "??????", "?????????", "????????????", "??????",
					"?????????", "????????????" };

			List<List<String>> wRowList = GetRowList(wLoginUser, wItemList);

			ExcelUtil.CreateFirst(wHeaders, "??????BOM");
			ExcelUtil.CreateOtherRow(wRowList);
			ExcelUtil.Export(wOutputStream);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????BOM??????
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
	 * ????????????BOM??????
	 */
	public void IPT_ExportMSSBOMItem(BMSEmployee wLoginUser, FileOutputStream wOutputStream, int wBOMID) {
		try {
			String[] wHeaders = { "??????", "????????????", "??????", "????????????", "????????????", "????????????", "??????", "BOM??????", "??????", "????????????", "????????????", "??????",
					"??????", "????????????" };

			List<List<String>> wRowList = GetRowList(wLoginUser, wBOMID);

			ExcelUtil.CreateFirst(wHeaders, "??????BOM??????");
			ExcelUtil.CreateOtherRow(wRowList);
			ExcelUtil.Export(wOutputStream);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ??????BOM??????
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
				wItemList.add(wMSSBOMItem.BOMType == 1 ? "??????" : "??????");
				wItemList.add(String.valueOf(wMSSBOMItem.MaterialNumber));
				wItemList.add(wMSSBOMItem.ReplaceType == 1 ? "??????" : wMSSBOMItem.ReplaceType == 2 ? "??????" : "");
				wItemList.add(wMSSBOMItem.OutsourceType == 1 ? "????????????"
						: wMSSBOMItem.OutsourceType == 2 ? "????????????"
								: wMSSBOMItem.OutsourceType == 3 ? "????????????"
										: wMSSBOMItem.OutsourceType == 4 ? "????????????" : "");
				wItemList.add(wMSSBOMItem.Remark);
				wItemList.add(wMSSBOMItem.Active == 2 ? "??????" : "??????");
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
	 * ????????????(????????????)
	 */
	public String getSignature(Map<String, Integer> params, String appId, String appSecret) {
		String sign = "";
		try {
			List<String> keys = new ArrayList<String>();
			for (Map.Entry<String, Integer> entry : params.entrySet()) {
				keys.add(entry.getKey());
			}

			// ??????????????????key???????????????
			Collections.sort(keys, new Comparator<String>() {
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});

			// ???????????????????????????
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

			// ??????hmac sha1 ??????
			@SuppressWarnings("deprecation")
			byte[] bytes = HmacUtils.hmacSha1(appSecret.getBytes(), contents.toString().getBytes());

			// ???????????????Base64??????
			sign = Base64.encodeBase64String(bytes);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return sign;
	}

}
