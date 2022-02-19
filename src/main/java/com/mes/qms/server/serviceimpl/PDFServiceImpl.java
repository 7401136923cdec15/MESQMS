package com.mes.qms.server.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.PDFService;
import com.mes.qms.server.service.mesenum.IPTItemType;
import com.mes.qms.server.service.mesenum.IPTMode;
import com.mes.qms.server.service.mesenum.IPTPreCheckProblemStatus;
import com.mes.qms.server.service.mesenum.IPTSOPType;
import com.mes.qms.server.service.mesenum.IPTTableTypes;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.SFCTaskStatus;
import com.mes.qms.server.service.mesenum.SFCTaskType;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.crm.CRMCustomer;
import com.mes.qms.server.service.po.fmc.FMCLine;
import com.mes.qms.server.service.po.fpc.FPCProduct;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTPDFConfig;
import com.mes.qms.server.service.po.ipt.IPTPDFPart;
import com.mes.qms.server.service.po.ipt.IPTPDFStandard;
import com.mes.qms.server.service.po.ipt.IPTPreCheckProblem;
import com.mes.qms.server.service.po.ipt.IPTPreCheckReport;
import com.mes.qms.server.service.po.ipt.IPTRowValue;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.ipt.IPTStandard;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.sfc.SFCIPTItem;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTItemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTPDFConfigDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTPDFPartDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTPDFStandardDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTPreCheckProblemDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTStandardDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTaskIPTDAO;
import com.mes.qms.server.serviceimpl.utils.qms.NewCreditReportUtil;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import com.mes.qms.server.utils.Configuration;

@Service
public class PDFServiceImpl implements PDFService {

	private static Logger logger = LoggerFactory.getLogger(PDFServiceImpl.class);

	@Override
	public ServiceResult<Boolean> IPT_OutputPDFStream(BMSEmployee wLoginUser, String wPartNo,
			ServletOutputStream wOutputStream) {
		ServiceResult<Boolean> wResult = new ServiceResult<Boolean>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

//			OMSOrder wOMSOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
//					.Info(OMSOrder.class);
//			int wLineID = wOMSOrder.LineID;
//			int wProductID = wOMSOrder.ProductID;
//			int wCustomID = wOMSOrder.BureauSectionID;
//			String wCarNo = wOMSOrder.PartNo;
//
//			// 获取导出PDF配置
//			List<IPTPDFConfig> wConfigList = IPTPDFConfigDAO.getInstance().SelectList(wLoginUser, -1, wLineID,
//					wProductID, wCustomID, 1, wErrorCode);
//			if (wConfigList == null || wConfigList.size() != 1) {
//				wResult.Result = false;
//				wResult.FaultCode += "激活的唯一一条PDF配置未找到!";
//				return wResult;
//			}
//			// 获取预检记录
//			IPTPDFConfig wIPTPDFConfig = wConfigList.get(0);
//			String wPrechecker = "";
//			Map<Integer, List<IPTRowValue>> wRowValueList = IPT_GetRowValueList(wLoginUser, wIPTPDFConfig, wLineID,
//					wProductID, wCustomID, wCarNo, wPrechecker, wOrderID);
//			if (wRowValueList == null || wRowValueList.size() <= 0)
//				return wResult;
//			// 局段赋值
//			String wCustomName = "";
//			Map<Integer, CRMCustomer> wCustomMap = QMSConstants.GetCRMCustomerList();
//			if (wCustomMap.containsKey(wCustomID)) {
//				wCustomName = wCustomMap.get(wCustomID).CustomerName;
//			}
//			// 输出PDF文档
//			NewCreditReportUtil.generateDynamicPrecheckReport(wIPTPDFConfig.IPTPDFPartList, wRowValueList, wCarNo,
//					wPrechecker, wCustomName, wOutputStream);
			// 输出PDF文档
			IPTPreCheckReport wReport = SFCServiceImpl.getInstance().SFC_QueryPreCheckReport(wLoginUser,
					wPartNo).Result;
			if (wReport != null) {
				NewCreditReportUtil.ReportPreCheck(wReport, wOutputStream);

//				FileOutputStream wFileOutputStream = new FileOutputStream(
//						new File("C:\\Users\\ShrisJava\\Desktop\\test.pdf"));
//				NewCreditReportUtil.ReportPreCheck(wReport, wFileOutputStream);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取行数据集合
	 * 
	 * @param wIPTPDFConfig PDF配置
	 * @param wLineID       修程
	 * @param wProductID    车型
	 * @param wCustomID     局段
	 * @param wCarNo        车号
	 * @param wPrechecker   预检人(返回参数)
	 * @return 行数据映射
	 */
	@SuppressWarnings("unused")
	private Map<Integer, List<IPTRowValue>> IPT_GetRowValueList(BMSEmployee wLoginUser, IPTPDFConfig wIPTPDFConfig,
			int wLineID, int wProductID, int wCustomID, String wCarNo, String wPrechecker, int wOrderID) {
		Map<Integer, List<IPTRowValue>> wResult = new HashMap<Integer, List<IPTRowValue>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			if (wIPTPDFConfig == null || wIPTPDFConfig.ID <= 0 || wIPTPDFConfig.IPTPDFPartList == null
					|| wIPTPDFConfig.IPTPDFPartList.size() <= 0)
				return wResult;

			// 根据车号获取该车的所有预检单
			List<SFCTaskIPT> wTaskList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOrderID, -1,
					SFCTaskType.PreCheck.getValue(), -1, -1, -1, null, null, null, wErrorCode);
			wTaskList = wTaskList.stream().filter(p -> p.Status == SFCTaskStatus.Done.getValue())
					.collect(Collectors.toList());
			if (wTaskList == null || wTaskList.size() <= 0)
				return wResult;

			wPrechecker = QMSConstants.GetBMSEmployeeName(wTaskList.get(0).OperatorID);

			List<IPTRowValue> wIPTRowValueList = null;
			Optional<SFCTaskIPT> wOption = null;
			SFCTaskIPT wSFCTaskIPT = null;
			for (IPTPDFPart wIPTPDFPart : wIPTPDFConfig.IPTPDFPartList) {
				if (wIPTPDFPart.IPTPDFStandardList == null || wIPTPDFPart.IPTPDFStandardList.size() <= 0)
					continue;
				for (IPTPDFStandard wIPTPDFStandard : wIPTPDFPart.IPTPDFStandardList) {
					if (wResult.containsKey(wIPTPDFStandard.ID)) {
						continue;
					}

					// 根据标准ID找到对应的预检单
					wOption = wTaskList.stream().filter(p -> p.ModuleVersionID == wIPTPDFStandard.StandardID)
							.findFirst();
					if (!wOption.isPresent()) {
						continue;
					}
					wSFCTaskIPT = wOption.get();

					switch (IPTItemType.getEnumType(wSFCTaskIPT.ItemList.get(0).IPTItem.ItemType)) {
					case InPlant:// 入场状态
						wIPTRowValueList = GetRowValueListByInPlant(wLoginUser, wIPTPDFStandard, wSFCTaskIPT);
						break;
					case ProblemItem:// 问题项
						wIPTRowValueList = GetRowValueListByProblemItem(wLoginUser, wIPTPDFStandard, wSFCTaskIPT);
						break;
					default:
						break;
					}
					if (wIPTRowValueList != null && wIPTRowValueList.size() > 0) {
						wResult.put(wIPTPDFStandard.ID, wIPTRowValueList);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取入场状态的预检单的填写值
	 */
	private List<IPTRowValue> GetRowValueListByInPlant(BMSEmployee wLoginUser, IPTPDFStandard wIPTPDFStandard,
			SFCTaskIPT wSFCTaskIPT) {
		List<IPTRowValue> wResult = new ArrayList<IPTRowValue>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<IPTItem> wIPTItemList = new ArrayList<IPTItem>();
			for (SFCIPTItem wSFCIPTItem : wSFCTaskIPT.ItemList) {
				wIPTItemList.add(wSFCIPTItem.IPTItem);
			}
			List<IPTValue> wIPTValueList = new ArrayList<IPTValue>();
			wIPTValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSFCTaskIPT.ID, -1, -1,
					wErrorCode).Result;

			List<IPTItem> wNoMajorList = wIPTItemList;

			IPTRowValue wIPTRowValue = null;
			IPTValue wIPTValue = null;
			Optional<IPTValue> wOption = null;
			int wFlag = 1;

			String wRCZT = "";
			String wCJBH = "";
			String wYCQK = "";
			String wBZ = "";

			String wTL = "";

			String wCJ = "";
			String wBH = "";
			String wXH = "";

			switch (IPTTableTypes.getEnumType(wIPTPDFStandard.TableType)) {
			case StaticRecord:// 静态记录表
				// 获取无主项的值集合
				for (IPTItem wIPTItem : wNoMajorList) {
					if (wIPTValueList != null && wIPTValueList.size() > 0) {
						wOption = wIPTValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst();
						if (wOption != null && wOption.isPresent()) {
							wIPTValue = wOption.get();
						}
					}
					if (wIPTValue != null && wIPTValue.ID > 0) {
						// 入场状态
						wRCZT = wIPTValue.Value;
						// 厂家/编号
						wCJBH = wIPTValue.Value;
						// 异常情况
						wYCQK = wIPTValue.Remark;
						// 备注
						wBZ = wIPTValue.Remark;
					}
					wIPTRowValue = new IPTRowValue();
					wIPTRowValue.IsBold = false;
					wIPTRowValue.IsGrayBackground = false;
					wIPTRowValue.ValueList = new ArrayList<String>(
							Arrays.asList(String.valueOf(wFlag), wIPTItem.Text, wRCZT, wCJBH, wYCQK, wBZ));
					wResult.add(wIPTRowValue);
					wFlag++;
				}
				break;
			case DynamicRecord:// 动态记录表
				break;
			case InsulationRubberSleeve:// 绝缘胶套表
				wFlag = 1;
				for (IPTItem wIPTItem : wIPTItemList) {
					if (wIPTValueList != null && wIPTValueList.size() > 0) {
						wOption = wIPTValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst();
						if (wOption != null && wOption.isPresent()) {
							wIPTValue = wOption.get();
						}
					}
					if (wIPTValue != null && wIPTValue.ID > 0) {
						// 入场状态
						wRCZT = wIPTValue.Value;
					}
					if (wIPTItem.IPTSOPList != null && wIPTItem.IPTSOPList.size() > 0) {
						Optional<IPTSOP> wImageOption = wIPTItem.IPTSOPList.stream()
								.filter(p -> p.Type == IPTSOPType.Image.getValue()).findFirst();
						if (wImageOption.isPresent()) {
							if (wImageOption.get().PathList != null && wImageOption.get().PathList.size() > 0) {
								String wCoreServelUrl = Configuration.readConfigString("core.server.url",
										"config/config");
								wTL = StringUtils.Format("{0}{1}", wCoreServelUrl, wImageOption.get().PathList.get(0));
							}
						}
					}
					wIPTRowValue = new IPTRowValue();
					wIPTRowValue.IsBold = false;
					wIPTRowValue.IsGrayBackground = false;
					wIPTRowValue.ValueList = new ArrayList<String>(
							Arrays.asList(String.valueOf(wFlag), wIPTItem.Text, wTL, "有 / 无", wRCZT));
					wResult.add(wIPTRowValue);
					wFlag++;
				}
				break;
			case KeyComponent:// 关键部件表
				wFlag = 1;
				for (IPTItem wIPTItem : wIPTItemList) {
					if (wIPTValueList != null && wIPTValueList.size() > 0) {
						wOption = wIPTValueList.stream().filter(p -> p.IPTItemID == wIPTItem.ID).findFirst();
						if (wOption != null && wOption.isPresent()) {
							wIPTValue = wOption.get();
						}
					}
					if (wIPTValue != null && wIPTValue.ID > 0) {
						// 厂家
						wCJ = wIPTValue.Value;
						// 型号
						wXH = wIPTValue.Value;
						// 编号
						wBH = wIPTValue.Value;
						// 入场状态
						wRCZT = wIPTValue.Value;
					}

					wIPTRowValue = new IPTRowValue();
					wIPTRowValue.IsBold = false;
					wIPTRowValue.IsGrayBackground = false;
					wIPTRowValue.ValueList = new ArrayList<String>(
							Arrays.asList(String.valueOf(wFlag), wIPTItem.Text, wCJ, wXH, wBH, wRCZT));
					wResult.add(wIPTRowValue);
					wFlag++;
				}
				break;
			case SoftwardVerson:// 软件版本表
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取问题项的预检单的填写值
	 * 
	 * @param wIPTPDFStandard
	 * @param wIPTPreCheckTask
	 * @return
	 */
	private List<IPTRowValue> GetRowValueListByProblemItem(BMSEmployee wLoginUser, IPTPDFStandard wIPTPDFStandard,
			SFCTaskIPT wSFCTaskIPT) {
		List<IPTRowValue> wResult = new ArrayList<IPTRowValue>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			List<IPTPreCheckProblem> wIPTPreCheckProblemList = IPTPreCheckProblemDAO.getInstance()
					.SelectList(wLoginUser, -1, wSFCTaskIPT.ID, -1, -1, -1, -1, null, wErrorCode);

			if (wIPTPreCheckProblemList == null || wIPTPreCheckProblemList.size() <= 0)
				return wResult;

			// 筛选已经完工的预检问题项
			List<IPTPreCheckProblem> wProblemList = wIPTPreCheckProblemList.stream()
					.filter(p -> p.Status == IPTPreCheckProblemStatus.Done.getValue()).collect(Collectors.toList());
			if (wProblemList == null || wProblemList.size() <= 0) {
				return wResult;
			}

			// 给问题项赋检验值(自检、互检)
			SetIPTValue(wLoginUser, wErrorCode, wProblemList);

			IPTRowValue wIPTRowValue = null;
			IPTValue wIPTValue = null;

			String wZJ = "";
			String wHJ = "";
			String wJCJL = "";
			String wGZYY = "";
			String wBZ = "";

			switch (IPTTableTypes.getEnumType(wIPTPDFStandard.TableType)) {
			case DynamicRecord:// 动态记录表
				for (IPTPreCheckProblem wIPTPreCheckProblem : wProblemList) {
					if (wIPTPreCheckProblem.IPTItem == null || wIPTPreCheckProblem.IPTItem.ID <= 0) {
						continue;
					}
					if (wIPTPreCheckProblem.IPTValueList != null && wIPTPreCheckProblem.IPTValueList.size() > 0) {
						Optional<IPTValue> wOption = wIPTPreCheckProblem.IPTValueList.stream()
								.filter(p -> p.IPTMode == SFCTaskType.SelfCheck.getValue()).findFirst();
						if (wOption.isPresent()) {
							wIPTValue = wOption.get();
							wZJ = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
						}
					}
					if (wIPTValue != null && wIPTValue.ID > 0) {
						// 检查记录
						wJCJL = wIPTValue.Value;
						// 故障原因
						wGZYY = wIPTValue.Remark;
					}

					wIPTRowValue = new IPTRowValue();
					wIPTRowValue.IsBold = false;
					wIPTRowValue.IsGrayBackground = false;
					wIPTRowValue.ValueList = new ArrayList<String>(Arrays.asList(wIPTPreCheckProblem.IPTItem.Text,
							wIPTPreCheckProblem.IPTItem.Standard, wJCJL, wGZYY));
					wResult.add(wIPTRowValue);
				}
				break;
			case SoftwardVerson:// 软件版本表
				int wFlag = 1;
				for (IPTPreCheckProblem wIPTPreCheckProblem : wProblemList) {
					if (wIPTPreCheckProblem.IPTItem == null || wIPTPreCheckProblem.IPTItem.ID <= 0) {
						continue;
					}
					if (wIPTPreCheckProblem.IPTValueList != null && wIPTPreCheckProblem.IPTValueList.size() > 0) {
						Optional<IPTValue> wOption = wIPTPreCheckProblem.IPTValueList.stream()
								.filter(p -> p.IPTMode == SFCTaskType.SelfCheck.getValue()).findFirst();
						if (wOption.isPresent()) {
							wIPTValue = wOption.get();
							wZJ = QMSConstants.GetBMSEmployeeName(wIPTValue.SubmitID);
						}
					}
					if (wIPTValue != null && wIPTValue.ID > 0) {
						// 检查记录
						wJCJL = wIPTValue.Value;
						// 备注
						wBZ = wIPTValue.Remark;
					}

					wIPTRowValue = new IPTRowValue();
					wIPTRowValue.IsBold = false;
					wIPTRowValue.IsGrayBackground = false;
					wIPTRowValue.ValueList = new ArrayList<String>(
							Arrays.asList(String.valueOf(wFlag), wIPTPreCheckProblem.IPTItem.Text, wJCJL, wBZ));
					wResult.add(wIPTRowValue);

					wFlag++;
				}
				break;
			default:
				break;
			}
			// 添加自检、互检人
			if (wProblemList.get(0).IPTValueList.stream().filter(p -> p.IPTMode == IPTMode.QTXJ.getValue()).findFirst()
					.isPresent()) {
				IPTValue wMutualValue = wProblemList.get(0).IPTValueList.stream()
						.filter(p -> p.IPTMode == SFCTaskType.MutualCheck.getValue()).findFirst().get();
				wHJ = QMSConstants.GetBMSEmployeeName(wMutualValue.SubmitID);
			}

			wIPTRowValue = new IPTRowValue();
			wIPTRowValue.IsBold = false;
			wIPTRowValue.IsGrayBackground = false;
			wIPTRowValue.ValueList = new ArrayList<String>(Arrays.asList(wZJ, wHJ));
			wResult.add(wIPTRowValue);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 给问题项赋检验值
	 * 
	 * @param wLoginUser   登录信息
	 * @param wErrorCode   错误码
	 * @param wProblemList 预检问题项集合
	 */
	private void SetIPTValue(BMSEmployee wLoginUser, OutResult<Integer> wErrorCode,
			List<IPTPreCheckProblem> wProblemList) {
		try {
			if (wProblemList == null || wProblemList.size() <= 0) {
				return;
			}

			List<SFCTaskIPT> wIPTList = null;
			SFCTaskIPT wSeflIPT = null;
			SFCTaskIPT wMutualIPT = null;
			Optional<SFCTaskIPT> wOptionIPT = null;
			List<IPTValue> wValueList = null;
			Optional<IPTValue> wOptionValue = null;
			for (IPTPreCheckProblem wIPTPreCheckProblem : wProblemList) {
				if (wIPTPreCheckProblem.IPTItem == null || wIPTPreCheckProblem.IPTItem.ID <= 0) {
					wIPTPreCheckProblem.IPTItem = IPTItemDAO.getInstance().SelectByID(wLoginUser,
							wIPTPreCheckProblem.IPTItemID, wErrorCode);
				}

				wIPTPreCheckProblem.IPTValueList = new ArrayList<IPTValue>();

				// 自检检验值
				wIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wIPTPreCheckProblem.OrderID,
						wIPTPreCheckProblem.DoStationID, -1, wIPTPreCheckProblem.LineID,
						wIPTPreCheckProblem.DoPartPointID, wIPTPreCheckProblem.ProductID, null, null, null, wErrorCode);

				if (wIPTList != null && wIPTList.size() > 0) {
					wOptionIPT = wIPTList.stream().filter(p -> p.TaskType == SFCTaskType.SelfCheck.getValue())
							.max((a, b) -> a.ID - b.ID);
					if (wOptionIPT.isPresent()) {
						wSeflIPT = wOptionIPT.get();
					}
					wOptionIPT = wIPTList.stream().filter(p -> p.TaskType == SFCTaskType.MutualCheck.getValue())
							.max((a, b) -> a.ID - b.ID);
					if (wOptionIPT.isPresent()) {
						wMutualIPT = wOptionIPT.get();
					}

					if (wSeflIPT != null && wSeflIPT.ID > 0) {
						// 自检值
						wValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wSeflIPT.ID, -1, -1,
								wErrorCode).Result;
						if (wValueList != null && wValueList.size() > 0) {
							wOptionValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTPreCheckProblem.IPTItemID)
									.max((a, b) -> (int) (a.ID - b.ID));
							if (wOptionValue.isPresent()) {
								wIPTPreCheckProblem.IPTValueList.add(wOptionValue.get());
							}
						}
					}

					if (wMutualIPT != null && wMutualIPT.ID > 0) {
						// 互检值
						wValueList = IPTStandardDAO.getInstance().SelectValue(wLoginUser, wMutualIPT.ID, -1, -1,
								wErrorCode).Result;
						if (wValueList != null && wValueList.size() > 0) {
							wOptionValue = wValueList.stream().filter(p -> p.IPTItemID == wIPTPreCheckProblem.IPTItemID)
									.max((a, b) -> (int) (a.ID - b.ID));
							if (wOptionValue.isPresent()) {
								wIPTPreCheckProblem.IPTValueList.add(wOptionValue.get());
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * ID查询
	 */
	@Override
	public ServiceResult<IPTPDFConfig> IPT_QueryPDFConfig(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTPDFConfig> wResult = new ServiceResult<IPTPDFConfig>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPDFConfigDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);
			// 翻译中文
			if (wResult.Result != null && wResult.Result.ID > 0) {
				// 人员映射

				Map<Integer, BMSEmployee> wEmployeeMap = QMSConstants.GetBMSEmployeeList();
				// 修程映射
				Map<Integer, FMCLine> wLineMap = QMSConstants.GetFMCLineList();
				// 局段映射
				Map<Integer, CRMCustomer> wCustomMap = QMSConstants.GetCRMCustomerList();
				// 产品映射
				Map<Integer, FPCProduct> wProductMap = QMSConstants.GetFPCProductList();

				if (wEmployeeMap.containsKey(wResult.Result.CreateID)) {
					wResult.Result.CreateName = wEmployeeMap.get(wResult.Result.CreateID).Name;
				}
				if (wCustomMap.containsKey(wResult.Result.CustomID)) {
					wResult.Result.CustomName = wCustomMap.get(wResult.Result.CustomID).CustomerName;
				}
				if (wLineMap.containsKey(wResult.Result.LineID)) {
					wResult.Result.LineName = wLineMap.get(wResult.Result.LineID).Name;
				}
				if (wProductMap.containsKey(wResult.Result.ProductID)) {
					wResult.Result.ProductNo = wProductMap.get(wResult.Result.ProductID).ProductNo;
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询
	 */
	@Override
	public ServiceResult<List<IPTPDFConfig>> IPT_QueryPDFConfigList(BMSEmployee wLoginUser, int wID, int wLineID,
			int wProductID, int wCustomID, int wActive) {
		ServiceResult<List<IPTPDFConfig>> wResult = new ServiceResult<List<IPTPDFConfig>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPDFConfigDAO.getInstance().SelectList(wLoginUser, wID, wLineID, wProductID, wCustomID,
					wActive, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 更新或修改
	 */
	@Override
	public ServiceResult<Integer> IPT_UpdatePDFConfig(BMSEmployee wLoginUser, IPTPDFConfig wIPTPDFConfig) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// 新增时关闭掉其他相关联的激活配置
			if (wIPTPDFConfig.ID == 0) {
				List<IPTPDFConfig> wTempList = IPTPDFConfigDAO.getInstance().SelectList(wLoginUser, -1,
						wIPTPDFConfig.LineID, wIPTPDFConfig.ProductID, wIPTPDFConfig.CustomID, 1, wErrorCode);
				if (wTempList != null && wTempList.size() > 0) {
					for (IPTPDFConfig wItem : wTempList) {
						wItem.Active = 2;
						IPTPDFConfigDAO.getInstance().Update(wLoginUser, wItem, wErrorCode);
					}
				}
			}
			wResult.Result = IPTPDFConfigDAO.getInstance().Update(wLoginUser, wIPTPDFConfig, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 批量激活或禁用
	 */
	@Override
	public ServiceResult<Integer> IPT_ActivePDFConfigList(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			IPTPDFConfigDAO.getInstance().Active(wLoginUser, wIDList, wActive, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ID查询
	 */
	@Override
	public ServiceResult<IPTPDFPart> IPT_QueryPDFPart(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTPDFPart> wResult = new ServiceResult<IPTPDFPart>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPDFPartDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询
	 */
	@Override
	public ServiceResult<List<IPTPDFPart>> IPT_QueryPDFPartList(BMSEmployee wLoginUser, int wID, int wPDFConfigID) {
		ServiceResult<List<IPTPDFPart>> wResult = new ServiceResult<List<IPTPDFPart>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPDFPartDAO.getInstance().SelectList(wLoginUser, wID, wPDFConfigID, wErrorCode);
			if (wResult.Result != null && wResult.Result.size() > 0) {
				// 先按照配置ID排序，再按照顺序字段排序
				wResult.Result
						.sort(Comparator.comparing(IPTPDFPart::getPDFConfigID).thenComparing(IPTPDFPart::getOrderNo));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 更新或修改
	 */
	@Override
	public ServiceResult<Integer> IPT_UpdatePDFPart(BMSEmployee wLoginUser, IPTPDFPart wIPTPDFPart) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPDFPartDAO.getInstance().Update(wLoginUser, wIPTPDFPart, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_DeletePDFPartList(BMSEmployee wLoginUser, List<IPTPDFPart> wIPTPDFPartList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult = IPTPDFPartDAO.getInstance().DeleteList(wLoginUser, wIPTPDFPartList, wErrorCode);

			// 处理顺序问题
			List<Integer> wConfigIDList = new ArrayList<Integer>();
			for (IPTPDFPart wIPTPDFPart : wIPTPDFPartList) {
				if (!wConfigIDList.contains(wIPTPDFPart.PDFConfigID)) {
					wConfigIDList.add(wIPTPDFPart.PDFConfigID);
				}
			}
			// ①先查询出所有的一级配置
			for (Integer wConfigID : wConfigIDList) {
				IPTPDFConfig wConfig = IPTPDFConfigDAO.getInstance().SelectByID(wLoginUser, wConfigID, wErrorCode);
				if (wConfig != null && wConfig.ID > 0) {
					List<IPTPDFPart> wPartList = IPTPDFPartDAO.getInstance().SelectList(wLoginUser, -1, wConfigID,
							wErrorCode);
					if (wPartList != null && wPartList.size() > 0) {
						// 排序
						wPartList.sort(Comparator.comparing(IPTPDFPart::getOrderNo));
						// 重新编排顺序，并保存
						int wFlag = 1;
						for (IPTPDFPart wIPTPDFPart : wPartList) {
							wIPTPDFPart.OrderNo = wFlag++;
							IPTPDFPartDAO.getInstance().Update(wLoginUser, wIPTPDFPart, wErrorCode);
						}
					}
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * ID查询
	 */
	@Override
	public ServiceResult<IPTPDFStandard> IPT_QueryPDFStandard(BMSEmployee wLoginUser, int wID) {
		ServiceResult<IPTPDFStandard> wResult = new ServiceResult<IPTPDFStandard>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPDFStandardDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);

			// 翻译中文
			if (wResult.Result != null && wResult.Result.ID > 0) {
				ServiceResult<IPTStandard> wRst = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser,
						wResult.Result.StandardID, wErrorCode);
				if (wRst.Result != null && wRst.Result.ID > 0) {
					wResult.Result.StandardName = wRst.Result.Remark;
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 条件查询
	 */
	@Override
	public ServiceResult<List<IPTPDFStandard>> IPT_QueryPDFStandardList(BMSEmployee wLoginUser, int wID,
			int wPDFPartID) {
		ServiceResult<List<IPTPDFStandard>> wResult = new ServiceResult<List<IPTPDFStandard>>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPDFStandardDAO.getInstance().SelectList(wLoginUser, wID, wPDFPartID, wErrorCode);
			// 翻译中文
			if (wResult.Result != null && wResult.Result.size() > 0) {
				ServiceResult<IPTStandard> wRst = null;
				for (IPTPDFStandard wIPTPDFStandard : wResult.Result) {
					wRst = IPTStandardDAO.getInstance().SelectIPTStandard(wLoginUser, wIPTPDFStandard.StandardID,
							wErrorCode);
					if (wRst.Result != null && wRst.Result.ID > 0) {
						wIPTPDFStandard.StandardName = wRst.Result.Remark;
					}
				}
				// 先按照PDF部分配置排序，再按照顺序字段排序
				wResult.Result.sort(
						Comparator.comparing(IPTPDFStandard::getPDFPartID).thenComparing(IPTPDFStandard::getOrderNo));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 更新或修改
	 */
	@Override
	public ServiceResult<Integer> IPT_UpdatePDFStandard(BMSEmployee wLoginUser, IPTPDFStandard wIPTPDFStandard) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult.Result = IPTPDFStandardDAO.getInstance().Update(wLoginUser, wIPTPDFStandard, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> IPT_DeletePDFStandardList(BMSEmployee wLoginUser,
			List<IPTPDFStandard> wIPTPDFStandardList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult = IPTPDFStandardDAO.getInstance().DeleteList(wLoginUser, wIPTPDFStandardList, wErrorCode);

			// 处理顺序问题
			List<Integer> wPartIDList = new ArrayList<Integer>();
			for (IPTPDFStandard wIPTPDFStandard : wIPTPDFStandardList) {
				if (!wPartIDList.contains(wIPTPDFStandard.PDFPartID)) {
					wPartIDList.add(wIPTPDFStandard.PDFPartID);
				}
			}
			// ①先查询出所有的一级配置
			for (Integer wPartID : wPartIDList) {
				IPTPDFPart wPart = IPTPDFPartDAO.getInstance().SelectByID(wLoginUser, wPartID, wErrorCode);
				if (wPart != null && wPart.ID > 0) {
					List<IPTPDFStandard> wStandardList = IPTPDFStandardDAO.getInstance().SelectList(wLoginUser, -1,
							wPartID, wErrorCode);
					if (wStandardList != null && wStandardList.size() > 0) {
						// 排序
						wStandardList.sort(Comparator.comparing(IPTPDFStandard::getOrderNo));
						// 重新编排顺序，并保存
						int wFlag = 1;
						for (IPTPDFStandard wIPTPDFStandard : wStandardList) {
							wIPTPDFStandard.OrderNo = wFlag++;
							IPTPDFStandardDAO.getInstance().Update(wLoginUser, wIPTPDFStandard, wErrorCode);
						}
					}
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<OMSOrder>> IPT_QeuryPDFOrderList(BMSEmployee wLoginUser, Calendar wStartTime,
			Calendar wEndTime) {
		ServiceResult<List<OMSOrder>> wResult = new ServiceResult<List<OMSOrder>>();
		wResult.Result = new ArrayList<OMSOrder>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①用时间段查询订单
			List<OMSOrder> wOrderList = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryRFOrderList(wLoginUser, -1, -1, -1, "", wStartTime, wEndTime).List(OMSOrder.class);
			if (wOrderList == null || wOrderList.size() <= 0) {
				return wResult;
			}
			// ②遍历订单查询该订单是否有预检记录
			for (OMSOrder wOMSOrder : wOrderList) {
				List<SFCTaskIPT> wIPTList = SFCTaskIPTDAO.getInstance().SelectListByOrderID(wLoginUser, wOMSOrder.ID,
						-1, SFCTaskType.PreCheck.getValue(), -1, -1, -1, "", null, null, wErrorCode);
				if (wIPTList == null || wIPTList.size() <= 0) {
					continue;
				}
				wResult.Result.add(wOMSOrder);
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	public PDFServiceImpl() {
	}

	private static PDFService Instance;

	public static PDFService getInstance() {
		if (Instance == null)
			Instance = new PDFServiceImpl();
		return Instance;
	}
}
