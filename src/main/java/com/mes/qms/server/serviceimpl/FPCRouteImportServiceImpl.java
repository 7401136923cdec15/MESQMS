package com.mes.qms.server.serviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mes.qms.server.service.FPCRouteImportService;
import com.mes.qms.server.service.mesenum.APSShiftPeriod;
import com.mes.qms.server.service.mesenum.APSTaskStatus;
import com.mes.qms.server.service.mesenum.BFCMessageType;
import com.mes.qms.server.service.mesenum.BMSDepartmentType;
import com.mes.qms.server.service.mesenum.BMSEmployeeType;
import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.FMCShiftLevel;
import com.mes.qms.server.service.mesenum.FPCChangeControlType;
import com.mes.qms.server.service.mesenum.IMPResult;
import com.mes.qms.server.service.mesenum.IMPSameType;
import com.mes.qms.server.service.mesenum.IMPType;
import com.mes.qms.server.service.mesenum.LFSOperationLogType;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.MSSOperateType;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.aps.APSTaskPart;
import com.mes.qms.server.service.po.aps.APSTaskStep;
import com.mes.qms.server.service.po.bfc.BFCMessage;
import com.mes.qms.server.service.po.bms.BMSClass;
import com.mes.qms.server.service.po.bms.BMSDepartment;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bms.BMSRoleItem;
import com.mes.qms.server.service.po.bms.BMSWorkCharge;
import com.mes.qms.server.service.po.bms.BMSWorkareaOrgnization;
import com.mes.qms.server.service.po.bpm.BPMActivitiTask;
import com.mes.qms.server.service.po.cfg.CFGUnit;
import com.mes.qms.server.service.po.excel.ExcelData;
import com.mes.qms.server.service.po.excel.ExcelLineData;
import com.mes.qms.server.service.po.excel.ExcelSheetData;
import com.mes.qms.server.service.po.excel.MyExcelSheet;
import com.mes.qms.server.service.po.fmc.FMCLineUnit;
import com.mes.qms.server.service.po.fpc.FPCFlowLine;
import com.mes.qms.server.service.po.fpc.FPCFlowPart;
import com.mes.qms.server.service.po.fpc.FPCFlowPoint;
import com.mes.qms.server.service.po.fpc.FPCPart;
import com.mes.qms.server.service.po.fpc.FPCPartPoint;
import com.mes.qms.server.service.po.fpc.FPCRoute;
import com.mes.qms.server.service.po.fpc.FPCRouteC;
import com.mes.qms.server.service.po.fpc.FPCRouteImport;
import com.mes.qms.server.service.po.fpc.FPCRoutePart;
import com.mes.qms.server.service.po.fpc.FPCRoutePartC;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPoint;
import com.mes.qms.server.service.po.fpc.FPCRoutePartPointC;
import com.mes.qms.server.service.po.fpc.FPCStepSOP;
import com.mes.qms.server.service.po.imp.IMPErrorRecord;
import com.mes.qms.server.service.po.imp.IMPResultRecord;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.po.lfs.LFSOperationLog;
import com.mes.qms.server.service.po.mbs.MBSApiLog;
import com.mes.qms.server.service.po.mss.MSSBOM;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.mss.MSSBOMItemC;
import com.mes.qms.server.service.po.mss.MSSMaterial;
import com.mes.qms.server.service.po.mss.MSSPartItem;
import com.mes.qms.server.service.po.mss.MSSPartRecord;
import com.mes.qms.server.service.po.mss.MSSRepairRecord;
import com.mes.qms.server.service.po.ncr.NCRTask;
import com.mes.qms.server.service.po.oms.OMSOrder;
import com.mes.qms.server.service.po.sfc.SFCTaskIPT;
import com.mes.qms.server.service.po.tcm.TCMRework;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.Configuration;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.bfc.BFCMessageDAO;
import com.mes.qms.server.serviceimpl.dao.fpc.FPCCommonFileDAO;
import com.mes.qms.server.serviceimpl.dao.fpc.FPCRouteDAO;
import com.mes.qms.server.serviceimpl.dao.imp.IMPErrorRecordDAO;
import com.mes.qms.server.serviceimpl.dao.imp.IMPResultRecordDAO;
import com.mes.qms.server.serviceimpl.dao.ipt.IPTStandardDAO;
import com.mes.qms.server.serviceimpl.dao.mbs.MBSApiLogDAO;
import com.mes.qms.server.serviceimpl.dao.mss.MSSBOMItemDAO;
import com.mes.qms.server.serviceimpl.dao.mss.MSSPartRecordDAO;
import com.mes.qms.server.serviceimpl.dao.mss.MSSRepairRecordDAO;
import com.mes.qms.server.serviceimpl.dao.sfc.SFCTaskIPTDAO;
import com.mes.qms.server.serviceimpl.utils.MESServer;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import com.mes.qms.server.serviceimpl.utils.qms.QMSUtils;
import com.mes.qms.server.utils.Constants;
import com.mes.qms.server.utils.qms.ExcelUtil;

@Service
public class FPCRouteImportServiceImpl implements FPCRouteImportService {

	private static Logger logger = LoggerFactory.getLogger(FPCRouteImportServiceImpl.class);

	private static FPCRouteImportService _instance;

	public static FPCRouteImportService getInstance() {
		if (_instance == null)
			_instance = new FPCRouteImportServiceImpl();

		return _instance;
	}

	@Override
	public ServiceResult<Integer> FPC_ImportRoute(BMSEmployee wLoginUser, ExcelData wExcelData, String wFileName) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// 【工艺BOP新增】权限控制
			if (!CoreServiceImpl.getInstance()
					.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 134001, 0, 0)
					.Info(Boolean.class)) {
				wResult.FaultCode += "提示：无工艺BOP新增权限!";

				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wFileName, wResult.FaultCode, wExcelData.sheetData.get(0).lineSum - 2);

				return wResult;
			}

			// 【工艺集新增】权限控制
			if (!CoreServiceImpl.getInstance()
					.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 134007, 0, 0)
					.Info(Boolean.class)) {
				wResult.FaultCode += "提示：无工艺集新增权限!";

				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wFileName, wResult.FaultCode, wExcelData.sheetData.get(0).lineSum - 2);

				return wResult;
			}

			// 【工艺BOP工序新增】权限控制
			if (!CoreServiceImpl.getInstance()
					.BMS_CheckPowerByAuthorityID(wLoginUser.CompanyID, wLoginUser.ID, 134008, 0, 0)
					.Info(Boolean.class)) {
				wResult.FaultCode += "提示：无工艺BOP工序新增权限!";

				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wFileName, wResult.FaultCode, wExcelData.sheetData.get(0).lineSum - 2);

				return wResult;
			}

			if (wExcelData == null || wExcelData.sheetData == null || wExcelData.sheetData.size() <= 0) {
				wResult.FaultCode += "提示：Excel数据解析失败!";

				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wFileName, wResult.FaultCode, wExcelData.sheetData.get(0).lineSum - 2);

				return wResult;
			}

			ExcelSheetData wSheetData = wExcelData.sheetData.get(0);
			List<ExcelLineData> wLineDataList = wSheetData.lineData;
			if (wLineDataList == null || wLineDataList.size() <= 0) {
				wResult.FaultCode += "提示：Excel数据解析失败!";

				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wFileName, wResult.FaultCode, wExcelData.sheetData.get(0).lineSum - 2);

				return wResult;
			}

			int wColCount = wLineDataList.get(0).colSum;
			if (wColCount <= 0) {
				wResult.FaultCode += "提示：Excel格式错误!";

				// 新增导入记录和错误记录
				AddImportRecord(wLoginUser, wFileName, wResult.FaultCode, wExcelData.sheetData.get(0).lineSum - 2);

				return wResult;
			}

			// 补齐数据
			for (ExcelLineData wLineData : wLineDataList) {
				if (wLineData.colSum >= wColCount) {
					continue;
				}
				for (int i = wLineData.colSum; i < wColCount; i++) {
					wLineData.colData.add("");
				}
			}

			// 遍历解析数据
			List<FPCRouteImport> wList = new ArrayList<FPCRouteImport>();
			for (int i = 2; i < wLineDataList.size(); i++) {
				FPCRouteImport wFPCRouteImport = new FPCRouteImport();
				int wIndex = 0;
				// 赋值属性
				this.AsignProperty(wLineDataList, i, wFPCRouteImport, wIndex);
				wList.add(wFPCRouteImport);
			}

			// 检查数据是否合法
			String wMsg = this.CheckData(wLoginUser, wList, wFileName);

			if (StringUtils.isNotEmpty(wMsg)) {
				wResult.FaultCode += wMsg;
				return wResult;
			}

			// 创建所有的FPCRoute
			int wRouteID = 0;
			List<FPCRoute> wFPCRouteList = new ArrayList<FPCRoute>();
			for (FPCRouteImport wFPCRouteImport : wList) {
				if (wFPCRouteList.stream().anyMatch(p -> p.Name.equals(wFPCRouteImport.BOPNo))) {
					continue;
				}
				FPCRoute wFPCRoute = new FPCRoute();
				wFPCRoute = AddRoute(wLoginUser, wFPCRouteImport, wFPCRoute);

				if (wFPCRoute.ID <= 0) {
					wResult.FaultCode += "提示：保存工艺BOP失败!";
					return wResult;
				}

				wRouteID = wFPCRoute.ID;

				wFPCRouteList.add(wFPCRoute);
			}

			// 创建所有的FPCPart
			List<FPCPart> wPartList = FMCServiceImpl.getInstance().FPC_QueryPartList(BaseDAO.SysAdmin, 0, 0, 0, -1)
					.List(FPCPart.class);
			// 工艺集、工位ID映射
			Map<String, Integer> wRoutePartMap = new HashMap<String, Integer>();
			for (FPCRouteImport wFPCRouteImport : wList) {
				if (wRoutePartMap.containsKey(wFPCRouteImport.BOPPartNo)) {
					continue;
				}
				// 工位ID
				int wPartID = wPartList.stream().filter(p -> p.Code.equals(wFPCRouteImport.PartCode)).findFirst()
						.get().ID;

				wRoutePartMap.put(wFPCRouteImport.BOPPartNo, wPartID);
			}
			// 创建所有的FPCPartPoint
			List<FPCPartPoint> wStepList = FMCServiceImpl.getInstance()
					.FPC_QueryPartPointList(BaseDAO.SysAdmin, 0, 0, 0).List(FPCPartPoint.class);
			// 工艺工位顺序映射
			Map<String, Integer> wRoutePartOrderMap = this.GetRoutePartOrderMap(wList);
			// 工艺工序顺序映射
			Map<FPCRouteImport, Integer> wRoutePartPointOrderMap = this.GetRoutePartPointOrderMap(wList);
			// 工艺集、工位ID映射
			Map<String, Integer> wRoutePartPointMap = new HashMap<String, Integer>();
			// 创建所有的FPCRoutePart
			List<FPCRoutePart> wFPCRoutePartList = new ArrayList<FPCRoutePart>();

			// 开始导入
			IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
					IMPType.BOP.getValue(), "", null, IMPResult.Doding.getValue(), wFileName, wList.size(), 0);
			wIMPResultRecord.ID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
			int wIndex = 0;
			for (FPCRouteImport wFPCRouteImport : wList) {
				// 添加工序
				AddPartPoint(wLoginUser, wStepList, wRoutePartPointMap, wFPCRouteImport);
				// 添加工艺工位
				AddRoutePart(wLoginUser, wList, wFPCRouteList, wRoutePartMap, wRoutePartOrderMap, wFPCRoutePartList,
						wFPCRouteImport);
				// 添加工艺工序
				AddRoutePartPoint(wLoginUser, wList, wFPCRouteList, wRoutePartMap, wRoutePartPointMap,
						wRoutePartPointOrderMap, wFPCRouteImport);
				// 每100条更新一次进度
				if ((wIndex + 1) % 100 == 0 || wIndex == wList.size() - 1) {
					wIMPResultRecord.Progress = wIndex + 1;
					IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
				}
				wIndex++;
			}
			// 导入产线单元明细
			List<FMCLineUnit> wLineUnitList = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(wLoginUser, -1, -1, -1, false).List(FMCLineUnit.class);
			this.ImportLineUnit(wLoginUser, wLineUnitList, wList, wPartList, wStepList);

			// 更新导入成功记录
			int wNewRouteID = wRouteID;
			wIMPResultRecord.PID = new ArrayList<Integer>(Arrays.asList(wNewRouteID));
			wIMPResultRecord.Result = wIMPResultRecord.Progress == wList.size() ? IMPResult.Success.getValue()
					: IMPResult.Failed.getValue();
			IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

			wResult.Result = wRouteID;

			if (wFPCRouteList != null && wFPCRouteList.size() == 1 && wIMPResultRecord.Progress == wList.size()) {
				wFPCRouteList.get(0).Active = 1;
				FMCServiceImpl.getInstance().FPC_SaveRoute(wLoginUser, wFPCRouteList.get(0));
			} else {
				wResult.FaultCode += "提示：导入失败!";
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	// 赋值属性
	private void AsignProperty(List<ExcelLineData> wLineDataList, int i, FPCRouteImport wFPCRouteImport, int wIndex) {
		try {
			wFPCRouteImport.BOPNo = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.LineName = wLineDataList.get(i).colData.get(wIndex++).replace(".0", "");
			wFPCRouteImport.ProductNo = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.CustomerName = wLineDataList.get(i).colData.get(wIndex++);

			wFPCRouteImport.BOPPartNo = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.BOPPartName = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.PartCode = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.PreBOPPartNo = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.NextBOPPartNo = wLineDataList.get(i).colData.get(wIndex++);

			wFPCRouteImport.PartPointCode = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.PartPointName = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.MaterialNo = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.Unit = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.PartPointNo = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.PrePartPointID = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.NextPartPointID = wLineDataList.get(i).colData.get(wIndex++);
			wFPCRouteImport.StandardWorkTime = wLineDataList.get(i).colData.get(wIndex++);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	// 添加路线
	private FPCRoute AddRoute(BMSEmployee wLoginUser, FPCRouteImport wFPCRouteImport, FPCRoute wFPCRoute) {
		try {
			wFPCRoute.ID = 0;
			wFPCRoute.VersionNo = wFPCRouteImport.BOPNo;
			wFPCRoute.BusinessUnitID = 1;
			wFPCRoute.ProductTypeID = 0;
			wFPCRoute.CreatorID = wLoginUser.ID;
			wFPCRoute.EditorID = wLoginUser.ID;
			wFPCRoute.AuditorID = wLoginUser.ID;
			wFPCRoute.Status = 1;
			wFPCRoute.Active = 3;
			wFPCRoute.CreateTime = Calendar.getInstance();
			wFPCRoute.EditTime = Calendar.getInstance();
			wFPCRoute.AuditTime = Calendar.getInstance();
			wFPCRoute.Description = wFPCRouteImport.BOPNo;
			wFPCRoute.FactoryID = 1;
			wFPCRoute.LineID = FPC_GetLineID(wLoginUser, wFPCRouteImport.LineName);
			wFPCRoute.Name = wFPCRouteImport.BOPNo;
			wFPCRoute.ProductID = QMSConstants.GetFPCProduct(wFPCRouteImport.ProductNo).ID;
			wFPCRoute.CustomerID = QMSConstants.GetCRMCustomerByCode(wFPCRouteImport.CustomerName).ID;
			wFPCRoute.IsStandard = 0;

			wFPCRoute = FMCServiceImpl.getInstance().FPC_SaveRoute(wLoginUser, wFPCRoute).Info(FPCRoute.class);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wFPCRoute;
	}

	// 添加工序
	private void AddPartPoint(BMSEmployee wLoginUser, List<FPCPartPoint> wStepList,
			Map<String, Integer> wRoutePartPointMap, FPCRouteImport wFPCRouteImport) {
		try {
			FPCPartPoint wFPCPartPoint;
			int wPartPointID = 0;
			if (wRoutePartPointMap.containsKey(wFPCRouteImport.PartPointCode)) {
				return;
			}
			// 工序ID
			if (!wStepList.stream().anyMatch(
					p -> p.Code.equals(wFPCRouteImport.PartPointNo) || p.Name.equals(wFPCRouteImport.PartPointName))) {
				wFPCPartPoint = new FPCPartPoint();
				wFPCPartPoint.ID = 0;
				wFPCPartPoint.Name = wFPCRouteImport.PartPointName;
				wFPCPartPoint.Code = StringUtils.isEmpty(wFPCRouteImport.PartPointNo) ? this.GetStepNumber(wStepList)
						: wFPCRouteImport.PartPointNo;
				wFPCPartPoint.CreatorID = wLoginUser.ID;
				wFPCPartPoint.CreateTime = Calendar.getInstance();
				wFPCPartPoint.EditorID = wLoginUser.ID;
				wFPCPartPoint.EditTime = Calendar.getInstance();
				wFPCPartPoint.AuditorID = wLoginUser.ID;
				wFPCPartPoint.AuditTime = Calendar.getInstance();
				wFPCPartPoint.Active = 1;
				wFPCPartPoint.Status = 3;
				wFPCPartPoint.FactoryID = 1;
				wFPCPartPoint.StepType = 1;
				wFPCPartPoint = FMCServiceImpl.getInstance().FPC_SavePartPoint(BaseDAO.SysAdmin, wFPCPartPoint)
						.Info(FPCPartPoint.class);
				if (wFPCPartPoint != null && wFPCPartPoint.ID > 0) {
					wPartPointID = wFPCPartPoint.ID;
					wStepList.add(wFPCPartPoint);
				}
			} else {
				wPartPointID = wStepList.stream().filter(
						p -> p.Code.equals(wFPCRouteImport.PartPointNo) || p.Name.equals(wFPCRouteImport.PartPointName))
						.findFirst().get().ID;
			}
			wRoutePartPointMap.put(wFPCRouteImport.PartPointCode, wPartPointID);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	// 添加工艺工位
	private void AddRoutePart(BMSEmployee wLoginUser, List<FPCRouteImport> wList, List<FPCRoute> wFPCRouteList,
			Map<String, Integer> wRoutePartMap, Map<String, Integer> wRoutePartOrderMap,
			List<FPCRoutePart> wFPCRoutePartList, FPCRouteImport wFPCRouteImport) {
		try {
			FPCRoutePart wFPCRoutePart;
			if (!wFPCRoutePartList.stream().anyMatch(
					p -> p.RouteName.equals(wFPCRouteImport.BOPNo) && p.Code.equals(wFPCRouteImport.BOPPartNo))) {
				wFPCRoutePart = new FPCRoutePart();
				wFPCRoutePart.ID = 0;
				wFPCRoutePart.RouteID = wFPCRouteList.stream().filter(p -> p.Name.equals(wFPCRouteImport.BOPNo))
						.findFirst().get().ID;
				wFPCRoutePart.RouteName = wFPCRouteImport.BOPNo;
				wFPCRoutePart.Name = wFPCRouteImport.BOPPartName;
				wFPCRoutePart.Code = wFPCRouteImport.BOPPartNo;
				wFPCRoutePart.PartID = wRoutePartMap.get(wFPCRouteImport.BOPPartNo);
				wFPCRoutePart.CreatorID = wLoginUser.ID;
				wFPCRoutePart.CreateTime = Calendar.getInstance();
				wFPCRoutePart.OrderID = wRoutePartOrderMap.containsKey(wFPCRouteImport.BOPPartNo)
						? wRoutePartOrderMap.get(wFPCRouteImport.BOPPartNo)
						: 1;
				wFPCRoutePart.PrevPartID = GetPrevPartID(wLoginUser, wRoutePartMap, wList, wFPCRouteImport);
				wFPCRoutePart.NextPartIDMap = GetNextPartID(wLoginUser, wRoutePartMap, wList, wFPCRouteImport);
				wFPCRoutePart = FMCServiceImpl.getInstance().FPC_SaveRoutePart(wLoginUser, wFPCRoutePart)
						.Info(FPCRoutePart.class);
				wFPCRoutePartList.add(wFPCRoutePart);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	// 添加工艺工序
	private void AddRoutePartPoint(BMSEmployee wLoginUser, List<FPCRouteImport> wList, List<FPCRoute> wFPCRouteList,
			Map<String, Integer> wRoutePartMap, Map<String, Integer> wRoutePartPointMap,
			Map<FPCRouteImport, Integer> wRoutePartPointOrderMap, FPCRouteImport wFPCRouteImport) {
		try {
			FPCRoutePartPoint wFPCRoutePartPoint;
			wFPCRoutePartPoint = new FPCRoutePartPoint();
			wFPCRoutePartPoint.ID = 0;
			wFPCRoutePartPoint.RouteID = wFPCRouteList.stream().filter(p -> p.Name.equals(wFPCRouteImport.BOPNo))
					.findFirst().get().ID;
			wFPCRoutePartPoint.RouteName = wFPCRouteImport.BOPNo;
			wFPCRoutePartPoint.Code = wFPCRouteImport.PartPointCode;
			wFPCRoutePartPoint.PartID = wRoutePartMap.get(wFPCRouteImport.BOPPartNo);
			wFPCRoutePartPoint.PartName = wFPCRouteImport.BOPPartName;
			wFPCRoutePartPoint.PartPointID = wRoutePartPointMap.get(wFPCRouteImport.PartPointCode);
			wFPCRoutePartPoint.CreatorID = wLoginUser.ID;
			wFPCRoutePartPoint.CreateTime = Calendar.getInstance();
			wFPCRoutePartPoint.OrderID = wRoutePartPointOrderMap.containsKey(wFPCRouteImport)
					? wRoutePartPointOrderMap.get(wFPCRouteImport)
					: 1;
			wFPCRoutePartPoint.PrevStepID = GetPrevStepID(wLoginUser, wRoutePartPointMap, wList, wFPCRouteImport);
			wFPCRoutePartPoint.NextStepIDMap = GetNextStepID(wLoginUser, wRoutePartPointMap, wList, wFPCRouteImport);
			wFPCRoutePartPoint.StandardPeriod = GetStandardPeriod(wFPCRouteImport);
			wFPCRoutePartPoint.ActualPeriod = wFPCRoutePartPoint.StandardPeriod;
			wFPCRoutePartPoint.MaterialID = GetMaterialID(wLoginUser, wFPCRouteImport);
			wFPCRoutePartPoint.DefaultOrder = "";
			wFPCRoutePartPoint = FMCServiceImpl.getInstance().FPC_SaveRoutePartPoint(wLoginUser, wFPCRoutePartPoint)
					.Info(FPCRoutePartPoint.class);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 验证数据正确性
	 * 
	 * @param wLoginUser 登录信息
	 * @param wList      导入信息
	 * @return 提示信息
	 */
	private String CheckData(BMSEmployee wLoginUser, List<FPCRouteImport> wList, String wFileName) {
		String wResult = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wList == null || wList.size() <= 0) {
				wResult = "提示：解析的数据为空!";

				// 新增导入记录和错误记录
				String wMsg = wResult;
				AddImportRecord(wLoginUser, wFileName, wMsg, 0);

				return wResult;
			}

			// 添加所有错误提示
			AddRecored(wLoginUser, wList, wFileName, wErrorCode);
			int wIndex = 1;
			for (FPCRouteImport wFPCRouteImport : wList) {
				// 检查车型
				if (StringUtils.isEmpty(wFPCRouteImport.ProductNo)) {
					return StringUtils.Format("提示：第{0}行数据不合法，车型不能为空!", wIndex + 2);
				}
				if (QMSConstants.GetFPCProduct(wFPCRouteImport.ProductNo).ID <= 0) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】车型不存在!", wIndex + 2, wFPCRouteImport.ProductNo);
				}
				// 检查局段
				if (StringUtils.isEmpty(wFPCRouteImport.CustomerName)) {
					return StringUtils.Format("提示：第{0}行数据不合法，局段不能为空!", wIndex + 2);
				}
				if (QMSConstants.GetCRMCustomerByCode(wFPCRouteImport.CustomerName).ID <= 0) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】局段不存在!", wIndex + 2, wFPCRouteImport.CustomerName);
				}
				// 检查修程
				if (StringUtils.isEmpty(wFPCRouteImport.LineName)) {
					return StringUtils.Format("提示：第{0}行数据不合法，修程不能为空!", wIndex + 2);
				}
				if (this.FPC_GetLineID(wLoginUser, wFPCRouteImport.LineName) <= 0) {
					return StringUtils.Format("提示：第{0}行数据不合法，【C{1}】修程不存在!", wIndex + 2, wFPCRouteImport.LineName);
				}
				// 检查工位编号
				if (StringUtils.isEmpty(wFPCRouteImport.PartCode)) {
					return StringUtils.Format("提示：第{0}行数据不合法，工位编号不能为空!", wIndex + 2);
				}
				if (QMSConstants.GetFPCPartByCode(wFPCRouteImport.PartCode).ID <= 0) {
					return StringUtils.Format("提示：第{0}行数据不合法，【{1}】工位不存在!", wIndex + 2, wFPCRouteImport.PartCode);
				}
				// 检查工序名称
				if (StringUtils.isEmpty(wFPCRouteImport.PartPointName)) {
					return StringUtils.Format("提示：第{0}行数据不合法，工序名称不能为空!", wIndex + 2);
				}

				wIndex++;
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
	private void AddRecored(BMSEmployee wLoginUser, List<FPCRouteImport> wList, String wFileName,
			OutResult<Integer> wErrorCode) {
		try {
			List<IMPErrorRecord> wRecordList = new ArrayList<IMPErrorRecord>();
			int wIndex = 1;
			for (FPCRouteImport wFPCRouteImport : wList) {
				// 检查车型
				if (StringUtils.isEmpty(wFPCRouteImport.ProductNo)) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，车型不能为空!", wIndex + 2)));
				} else if (QMSConstants.GetFPCProduct(wFPCRouteImport.ProductNo).ID <= 0) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【{1}】车型不存在!", wIndex + 2, wFPCRouteImport.ProductNo)));
				}
				// 检查局段
				if (StringUtils.isEmpty(wFPCRouteImport.CustomerName)) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，局段不能为空!", wIndex + 2)));
				} else if (QMSConstants.GetCRMCustomerByCode(wFPCRouteImport.CustomerName).ID <= 0) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【{1}】局段不存在!", wIndex + 2, wFPCRouteImport.CustomerName)));
				}
				// 检查修程
				if (StringUtils.isEmpty(wFPCRouteImport.LineName)) {
					wRecordList
							.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，修程不能为空!", wIndex + 2)));
				} else if (this.FPC_GetLineID(wLoginUser, wFPCRouteImport.LineName) <= 0) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【C{1}】修程不存在!", wIndex + 2, wFPCRouteImport.LineName)));
				}
				// 检查工位编号
				if (StringUtils.isEmpty(wFPCRouteImport.PartCode)) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，工位编号不能为空!", wIndex + 2)));
				} else if (QMSConstants.GetFPCPartByCode(wFPCRouteImport.PartCode).ID <= 0) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0,
							StringUtils.Format("提示：第{0}行数据不合法，【{1}】工位不存在!", wIndex + 2, wFPCRouteImport.PartCode)));
				}
				// 检查工序名称
				if (StringUtils.isEmpty(wFPCRouteImport.PartPointName)) {
					wRecordList.add(
							new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，工序名称不能为空!", wIndex + 2)));
				} else if (QMSConstants.GetFPCStep(wFPCRouteImport.PartPointName).ID <= 0) {
					wRecordList.add(new IMPErrorRecord(0, 0, 0, StringUtils.Format("提示：第{0}行数据不合法，【{1}】工序不存在!",
							wIndex + 2, wFPCRouteImport.PartPointName)));
				}

				wIndex++;
			}

			if (wRecordList.size() > 0) {
				IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
						IMPType.BOP.getValue(), "", null, IMPResult.Failed.getValue(), wFileName, wList.size(),
						wRecordList.size());
				int wNewID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);

				wRecordList.forEach(p -> p.ParentID = wNewID);
				for (IMPErrorRecord wIMPErrorRecord : wRecordList) {
					IMPErrorRecordDAO.getInstance().Update(wLoginUser, wIMPErrorRecord, wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 记录导入错误日志
	 * 
	 * @param wLoginUser 登录信息
	 * @param wFileName  文件名称
	 * @param faultCode  错误码
	 */
	private void AddImportRecord(BMSEmployee wLoginUser, String wFileName, String wFaultCode, int wLineCount) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			IMPResultRecord wIMPResultRecord = new IMPResultRecord(0, wLoginUser.ID, Calendar.getInstance(),
					IMPType.BOP.getValue(), "", null, IMPResult.Failed.getValue(), wFileName, wLineCount, 1);
			wIMPResultRecord.ID = IMPResultRecordDAO.getInstance().Update(wLoginUser, wIMPResultRecord, wErrorCode);
			if (wIMPResultRecord.ID <= 0) {
				return;
			}

			IMPErrorRecord wIMPErrorRecord = new IMPErrorRecord(0, wIMPResultRecord.ID, 0, wFaultCode);
			IMPErrorRecordDAO.getInstance().Update(wLoginUser, wIMPErrorRecord, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 导入产线单元明细
	 */
	private void ImportLineUnit(BMSEmployee wLoginUser, List<FMCLineUnit> wLineUnitList, List<FPCRouteImport> wList,
			List<FPCPart> wPartList, List<FPCPartPoint> wStepList) {
		try {
			List<FPCRouteImport> wStationImportList = new ArrayList<FPCRouteImport>(wList.stream()
					.collect(Collectors.toMap(FPCRouteImport::getBOPPartNo, account -> account, (k1, k2) -> k2))
					.values());

			int wLineID = this.FPC_GetLineID(wLoginUser, wStationImportList.get(0).LineName);
			int wProductID = QMSConstants.GetFPCProduct(wStationImportList.get(0).ProductNo).ID;

			List<FMCLineUnit> wTempUnitList = null;
			for (FPCRouteImport wFPCRouteImport : wStationImportList) {
				if (!wPartList.stream().anyMatch(
						p -> p.Code.equals(wFPCRouteImport.PartCode) || p.Name.equals(wFPCRouteImport.BOPPartName))) {
					continue;
				}

				FPCPart wPart = wPartList.stream().filter(
						p -> p.Code.equals(wFPCRouteImport.PartCode) || p.Name.equals(wFPCRouteImport.BOPPartName))
						.findFirst().get();

				wTempUnitList = wLineUnitList.stream().filter(p -> p.LineID == wLineID && p.ProductID == wProductID)
						.collect(Collectors.toList());
				// 工位级
				if (wTempUnitList == null || wTempUnitList.size() <= 0
						|| !wTempUnitList.stream().anyMatch(p -> p.UnitID == wPart.ID && p.LevelID == 2)) {
					int wMaxOrderID = 1;
					List<FMCLineUnit> wUnitList1 = wTempUnitList.stream().filter(p -> p.LevelID == 2)
							.collect(Collectors.toList());
					if (wUnitList1 != null && wUnitList1.size() > 0) {
						wMaxOrderID = wUnitList1.stream().max(Comparator.comparing(FMCLineUnit::getOrderID))
								.get().OrderID + 1;
					}

					FMCLineUnit wFMCLineUnit = new FMCLineUnit();
					wFMCLineUnit.Active = 1;
					wFMCLineUnit.LineID = wLineID;
					wFMCLineUnit.UnitID = wPart.ID;
					wFMCLineUnit.OrderID = wMaxOrderID;
					wFMCLineUnit.LevelID = 2;
					wFMCLineUnit.CreatorID = wLoginUser.ID;
					wFMCLineUnit.ProductID = wProductID;
					wFMCLineUnit.CreateTime = Calendar.getInstance();
					wFMCLineUnit.Status = 1;
					wFMCLineUnit.ParentUnitID = wLineID;
					FMCServiceImpl.getInstance().FMC_SaveLineUnit(wLoginUser, wFMCLineUnit);
					wLineUnitList.add(wFMCLineUnit);
				}

				// 找到该工位下所有工序
				List<FPCRouteImport> wStepImportList = wList.stream()
						.filter(p -> p.BOPPartNo.equals(wFPCRouteImport.BOPPartNo)).collect(Collectors.toList());
				for (FPCRouteImport wStepImport : wStepImportList) {
					if (!wStepList.stream().anyMatch(p -> p.Name.equals(wStepImport.PartPointName))) {
						continue;
					}

					FPCPartPoint wStep = wStepList.stream().filter(p -> p.Name.equals(wStepImport.PartPointName))
							.findFirst().get();
					if (!wTempUnitList.stream().anyMatch(p -> p.LevelID == 3 && p.UnitID == wStep.ID)) {
						int wMaxOrderID = 1;
						List<FMCLineUnit> wUnitList1 = wTempUnitList.stream().filter(p -> p.LevelID == 3)
								.collect(Collectors.toList());
						if (wUnitList1 != null && wUnitList1.size() > 0) {
							wMaxOrderID = wUnitList1.stream().max(Comparator.comparing(FMCLineUnit::getOrderID))
									.get().OrderID + 1;
						}
						FMCLineUnit wFMCLineUnit = new FMCLineUnit();
						wFMCLineUnit.Active = 1;
						wFMCLineUnit.LineID = wLineID;
						wFMCLineUnit.UnitID = wStep.ID;
						wFMCLineUnit.OrderID = wMaxOrderID;
						wFMCLineUnit.LevelID = 3;
						wFMCLineUnit.CreatorID = wLoginUser.ID;
						wFMCLineUnit.CreateTime = Calendar.getInstance();
						wFMCLineUnit.Status = 1;
						wFMCLineUnit.ProductID = wProductID;
						wFMCLineUnit.ParentUnitID = wPart.ID;
						FMCServiceImpl.getInstance().FMC_SaveLineUnit(wLoginUser, wFMCLineUnit);
						wLineUnitList.add(wFMCLineUnit);
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 获取工序序列号
	 * 
	 * @param wStepList
	 * @return
	 */
	private String GetStepNumber(List<FPCPartPoint> wStepList) {
		String wResult = "";
		try {
			wResult = String.format("%07d", wStepList.size() + 1);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 工艺工序顺序映射
	 * 
	 * @param wList
	 * @return
	 */
	private Map<FPCRouteImport, Integer> GetRoutePartPointOrderMap(List<FPCRouteImport> wList) {
		Map<FPCRouteImport, Integer> wResult = new HashMap<FPCRouteImport, Integer>();
		try {
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			for (FPCRouteImport wFPCRouteImport : wList) {
				if (wResult.containsKey(wFPCRouteImport)) {
					continue;
				}

				List<FPCRouteImport> wTempList = wList.stream()
						.filter(p -> p.BOPPartNo.equals(wFPCRouteImport.BOPPartNo)).collect(Collectors.toList());
				if (wTempList == null || wTempList.size() <= 0) {
					continue;
				}

				int wMaxLevel = this.GetMaxLevel(wFPCRouteImport, wTempList);
				wResult.put(wFPCRouteImport, wMaxLevel);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取工序集编号的最大层数
	 * 
	 * @param wFPCRouteImport 工艺工序数据
	 * @param wList           Excel数据源
	 * @return 最大层数
	 */
	private int GetMaxLevel(FPCRouteImport wFPCRouteImport, List<FPCRouteImport> wList) {
		int wResult = 1;
		try {
			// ①获取此工序集的前工序集编号
			if (StringUtils.isNotEmpty(wFPCRouteImport.PrePartPointID)) {
				String[] wNOs = wFPCRouteImport.PrePartPointID.split(",");
				for (String wNo : wNOs) {
					FPCRouteImport wItem = wList.stream().filter(p -> p.PartPointCode.equals(wNo)).findFirst().get();
					int wTempLevel = GetMaxLevel(wItem, wList);
					if (wResult < wTempLevel + 1) {
						wResult = wTempLevel + 1;
					}
				}
			}
			// ②获取其他工序集的后工序集包含此工序集的工序集
			List<FPCRouteImport> wPreNoList = wList.stream()
					.filter(p -> p.NextPartPointID != null && p.NextPartPointID.contains(wFPCRouteImport.PartPointCode))
					.collect(Collectors.toList());
			if (wPreNoList != null && wPreNoList.size() > 0) {
				for (FPCRouteImport wItem : wPreNoList) {
					int wTempLevel = GetMaxLevel(wItem, wList);
					if (wResult < wTempLevel + 1) {
						wResult = wTempLevel + 1;
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 工艺工位顺序映射
	 * 
	 * @param wList
	 * @return
	 */
	private Map<String, Integer> GetRoutePartOrderMap(List<FPCRouteImport> wList) {
		Map<String, Integer> wResult = new HashMap<String, Integer>();
		try {
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			List<String> wNOList = wList.stream().map(p -> p.BOPPartNo).distinct().collect(Collectors.toList());
			if (wNOList == null || wNOList.size() <= 0) {
				return wResult;
			}

			wList = new ArrayList<FPCRouteImport>(wList.stream()
					.collect(Collectors.toMap(FPCRouteImport::getBOPPartNo, account -> account, (k1, k2) -> k2))
					.values());

			int wMaxLevel = 0;
			for (String wNo : wNOList) {
				if (wResult.containsKey(wNo)) {
					continue;
				}

				wMaxLevel = this.GetMaxLevel(wNo, wList, wResult);
				wResult.put(wNo, wMaxLevel);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取工艺集编号的最大层数
	 * 
	 * @param wNo   工艺集编号
	 * @param wList Excel数据源
	 * @return 最大层数
	 */
	private int GetMaxLevel(String wNo, List<FPCRouteImport> wList, Map<String, Integer> wMap) {
		int wResult = 1;
		try {
			// ①获取此工序集的前工序集编号
			String wPreNo = wList.stream().filter(p -> p.BOPPartNo.equals(wNo)).findFirst().get().PreBOPPartNo;
			if (StringUtils.isNotEmpty(wPreNo)) {
				String[] wNOs = wPreNo.split(",");
				int wTempLevel = 0;
				for (String wItem : wNOs) {
					if (wMap.containsKey(wItem)) {
						wTempLevel = wMap.get(wItem);
					} else {
						wTempLevel = GetMaxLevel(wItem, wList, wMap);
					}
					if (!wMap.containsKey(wItem)) {
						wMap.put(wItem, wTempLevel);
					}
					if (wResult < wTempLevel + 1) {
						wResult = wTempLevel + 1;
					}
				}
			}
			// ②获取其他工序集的后工序集包含此工序集的工序集
			List<FPCRouteImport> wPreNoList = wList.stream()
					.filter(p -> p.NextBOPPartNo != null && p.NextBOPPartNo.contains(wNo) && !p.BOPPartNo.equals(wNo))
					.collect(Collectors.toList());

			if (wPreNoList != null && wPreNoList.size() > 0) {
				int wTempLevel = 0;
				for (FPCRouteImport wFPCRouteImport : wPreNoList) {
					if (wMap.containsKey(wFPCRouteImport.BOPPartNo)) {
						wTempLevel = wMap.get(wFPCRouteImport.BOPPartNo);
					} else {
						wTempLevel = GetMaxLevel(wFPCRouteImport.BOPPartNo, wList, wMap);
					}
					if (!wMap.containsKey(wFPCRouteImport.BOPPartNo)) {
						wMap.put(wFPCRouteImport.BOPPartNo, wTempLevel);
					}
					if (wResult < wTempLevel + 1) {
						wResult = wTempLevel + 1;
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取物料ID
	 * 
	 * @param wLoginUser
	 * @param wFPCRouteImport
	 * @return
	 */
	private int GetMaterialID(BMSEmployee wLoginUser, FPCRouteImport wFPCRouteImport) {
		int wResult = 0;
		try {
			if (StringUtils.isEmpty(wFPCRouteImport.MaterialNo)) {
				return wResult;
			}

			List<MSSMaterial> wList = WMSServiceImpl.getInstance()
					.MSS_QueryMaterialList(wLoginUser, wFPCRouteImport.MaterialNo).List(MSSMaterial.class);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			wResult = wList.get(0).ID;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取标准工时
	 * 
	 * @param wFPCRouteImport
	 * @return
	 */
	private Double GetStandardPeriod(FPCRouteImport wFPCRouteImport) {
		Double wResult = 0.0;
		try {
			if (StringUtils.isEmpty(wFPCRouteImport.StandardWorkTime)) {
				return wResult;
			}

			wResult = Double.parseDouble(wFPCRouteImport.StandardWorkTime);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取后续工序ID集合
	 * 
	 * @param wLoginUser
	 * @param wRoutePartPointMap
	 * @param wList
	 * @param wFPCRouteImport
	 * @return
	 */
	private Map<String, String> GetNextStepID(BMSEmployee wLoginUser, Map<String, Integer> wRoutePartPointMap,
			List<FPCRouteImport> wList, FPCRouteImport wFPCRouteImport) {
		Map<String, String> wResult = new HashMap<String, String>();
		try {
			// 添加
			if (StringUtils.isNotEmpty(wFPCRouteImport.NextPartPointID)) {
				String[] wCodes = wFPCRouteImport.NextPartPointID.split(",");
				for (String wCode : wCodes) {
					if (!wRoutePartPointMap.containsKey(wCode)) {
						continue;
					}
					Integer wPartID = wRoutePartPointMap.get(wCode);
					if (wPartID <= 0) {
						continue;
					}
					String wPart = String.valueOf(wPartID);
					if (!wResult.containsKey(wPart)) {
						wResult.put(wPart, "0");
					}
				}
			}

			// 添加
			List<FPCRouteImport> wTempList = wList.stream()
					.filter(p -> p.BOPPartNo.equals(wFPCRouteImport.BOPPartNo)
							&& !p.PartPointCode.equals(wFPCRouteImport.PartPointCode) && p.PrePartPointID.contains(",")
							&& p.PrePartPointID.contains(wFPCRouteImport.PartPointCode))
					.collect(Collectors.toList());
			if (wTempList != null && wTempList.size() > 0) {
				for (FPCRouteImport wItem : wTempList) {
					if (!wRoutePartPointMap.containsKey(wItem.PartPointCode)) {
						continue;
					}
					int wPartPointID = wRoutePartPointMap.get(wItem.PartPointCode);
					if (wPartPointID <= 0) {
						continue;
					}
					String wPartPoint = String.valueOf(wPartPointID);
					if (!wResult.containsKey(wPartPoint)) {
						wResult.put(wPartPoint, "0");
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取前工序ID
	 * 
	 * @param wLoginUser
	 * @param wRoutePartPointMap
	 * @param wList
	 * @param wFPCRouteImport
	 * @return
	 */
	private int GetPrevStepID(BMSEmployee wLoginUser, Map<String, Integer> wRoutePartPointMap,
			List<FPCRouteImport> wList, FPCRouteImport wFPCRouteImport) {
		int wResult = 0;
		try {
			String[] wCodes = wFPCRouteImport.PrePartPointID.split(",");
			if (wCodes == null || wCodes.length <= 0) {
				return wResult;
			}

			if (!wRoutePartPointMap.containsKey(wCodes[0])) {
				return wResult;
			}

			wResult = wRoutePartPointMap.get(wCodes[0]);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取后续工位ID
	 * 
	 * @param wLoginUser
	 * @param wRoutePartMap
	 * @param wList
	 * @param wFPCRouteImport
	 * @return
	 */
	private Map<String, String> GetNextPartID(BMSEmployee wLoginUser, Map<String, Integer> wRoutePartMap,
			List<FPCRouteImport> wList, FPCRouteImport wFPCRouteImport) {
		Map<String, String> wResult = new HashMap<String, String>();
		try {
			// 添加
			if (StringUtils.isNotEmpty(wFPCRouteImport.NextBOPPartNo)) {
				String[] wCodes = wFPCRouteImport.NextBOPPartNo.split(",");
				for (String wCode : wCodes) {
					if (!wRoutePartMap.containsKey(wCode)) {
						continue;
					}
					Integer wPartID = wRoutePartMap.get(wCode);
					if (wPartID <= 0) {
						continue;
					}
					String wPart = String.valueOf(wPartID);
					if (!wResult.containsKey(wPart)) {
						wResult.put(wPart, "0");
					}
				}
			}

			// 添加
			List<FPCRouteImport> wTempList = wList.stream().filter(p -> !p.BOPPartNo.equals(wFPCRouteImport.BOPPartNo)
					&& p.PreBOPPartNo.contains(",") && p.PreBOPPartNo.contains(wFPCRouteImport.BOPPartNo))
					.collect(Collectors.toList());
			if (wTempList != null && wTempList.size() > 0) {
				for (FPCRouteImport wItem : wTempList) {
					if (!wRoutePartMap.containsKey(wItem.BOPPartNo)) {
						continue;
					}
					int wPartID = wRoutePartMap.get(wItem.BOPPartNo);
					if (wPartID <= 0) {
						continue;
					}
					String wPart = String.valueOf(wPartID);
					if (!wResult.containsKey(wPart)) {
						wResult.put(wPart, "0");
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取前工位ID
	 * 
	 * @param wLoginUser
	 * @param wRoutePartMap
	 * @param wList
	 * @param wFPCRouteImport
	 * @return
	 */
	private int GetPrevPartID(BMSEmployee wLoginUser, Map<String, Integer> wRoutePartMap, List<FPCRouteImport> wList,
			FPCRouteImport wFPCRouteImport) {
		int wResult = 0;
		try {
			if (StringUtils.isEmpty(wFPCRouteImport.PreBOPPartNo)) {
				return wResult;
			}

			String[] wCodes = wFPCRouteImport.PreBOPPartNo.split(",");
			if (wCodes == null || wCodes.length <= 0) {
				return wResult;
			}

			if (!wRoutePartMap.containsKey(wCodes[0])) {
				return wResult;
			}

			wResult = wRoutePartMap.get(wCodes[0]);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 获取修程
	 * 
	 * @param wLoginUser
	 * @param lineName
	 * @return
	 */
	private int FPC_GetLineID(BMSEmployee wLoginUser, String wLineName) {
		int wResult = 0;
		try {
			if (StringUtils.isEmpty(wLineName)) {
				return wResult;
			}

			wResult = QMSConstants.GetFMCLine(StringUtils.Format("C{0}", wLineName)).ID;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FPCRouteC>> FPC_Compare(BMSEmployee wLoginUser, int wARouteID, int wBRouteID) {
		ServiceResult<List<FPCRouteC>> wResult = new ServiceResult<List<FPCRouteC>>();
		wResult.Result = new ArrayList<FPCRouteC>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①获取A工艺路线和B工艺路线
			FPCRoute wRouteA = QMSConstants.GetFPCRouteList().stream().filter(p -> p.ID == wARouteID).findFirst().get();
			FPCRoute wRouteB = QMSConstants.GetFPCRouteList().stream().filter(p -> p.ID == wBRouteID).findFirst().get();
			// ②获取A工艺工位集合和B工艺工位集合
			List<FPCRoutePart> wRoutePartAList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wARouteID).collect(Collectors.toList());
			List<FPCRoutePart> wRoutePartBList = QMSConstants.GetFPCRoutePartList().stream()
					.filter(p -> p.RouteID == wBRouteID).collect(Collectors.toList());
			// ③获取A工艺工序集合和B工艺工序集合
			List<FPCRoutePartPoint> wRoutePartPointAList = QMSConstants.GetFPCRoutePartPointList().stream()
					.filter(p -> p.RouteID == wARouteID).collect(Collectors.toList());
			List<FPCRoutePartPoint> wRoutePartPointBList = QMSConstants.GetFPCRoutePartPointList().stream()
					.filter(p -> p.RouteID == wBRouteID).collect(Collectors.toList());

			// 获取对比结果
			List<FPCRoutePartC> wCResult = this.GetCompareResult(wRouteA, wRouteB, wRoutePartAList, wRoutePartBList,
					wRoutePartPointAList, wRoutePartPointBList);

			// 返回数据
			wResult.CustomResult.put("CompareList", wCResult);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			e.printStackTrace();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取对比结果
	 * 
	 * @param wRouteA
	 * @param wRouteB
	 * @param wRoutePartAList
	 * @param wRoutePartBList
	 * @param wRoutePartPointAList
	 * @param wRoutePartPointBList
	 * @return
	 */
	private List<FPCRoutePartC> GetCompareResult(FPCRoute wRouteA, FPCRoute wRouteB, List<FPCRoutePart> wRoutePartAList,
			List<FPCRoutePart> wRoutePartBList, List<FPCRoutePartPoint> wRoutePartPointAList,
			List<FPCRoutePartPoint> wRoutePartPointBList) {
		List<FPCRoutePartC> wResult = new ArrayList<FPCRoutePartC>();
		try {
			// ①遍历工艺工位A，逐一跟工艺工位B对比
			for (FPCRoutePart wFPCRoutePart : wRoutePartAList) {
				if (wFPCRoutePart.PartID == 85) {
					System.out.println();
				}

				FPCRoutePartC wFPCRoutePartC = FPCRoutePartC.Clone(wFPCRoutePart);
				List<FPCRoutePartPoint> wAPointList = wRoutePartPointAList.stream()
						.filter(p -> p.PartID == wFPCRoutePart.PartID).collect(Collectors.toList());
				// ②如果B中存在和A相同Code的工艺工位，获取B工艺工位
				if (wRoutePartBList != null && wRoutePartBList.size() > 0
						&& wRoutePartBList.stream().anyMatch(p -> p.Code.equals(wFPCRoutePart.Code))) {
					FPCRoutePart wBRoutePart = wRoutePartBList.stream().filter(p -> p.Code.equals(wFPCRoutePart.Code))
							.findFirst().get();
					// ③A和B进行对比,获取A工艺工序，获取B工艺工序
					ServiceResult<Boolean> wR1 = FPCRoutePartC.SameAs(wFPCRoutePart, wBRoutePart);
					List<FPCRoutePartPoint> wBPointList = wRoutePartPointBList.stream()
							.filter(p -> p.PartID == wBRoutePart.PartID).collect(Collectors.toList());
					// ④遍历工艺工序A，逐一对比工艺工序B，若A的工艺工序和B完全相同，则A工艺工位的结果为相同，反之则A工艺工位的结果为不同
					List<FPCRoutePartPointC> wSonList = new ArrayList<FPCRoutePartPointC>();
					wFPCRoutePartC.FPCRoutePartPointCList = wSonList;
					for (FPCRoutePartPoint wFPCRoutePartPoint : wAPointList) {
						FPCRoutePartPointC wFPCRoutePartPointC = FPCRoutePartPointC.Clone(wFPCRoutePartPoint);
						if (wBPointList != null && wBPointList.size() > 0
								&& wBPointList.stream().anyMatch(p -> p.Code.equals(wFPCRoutePartPoint.Code))) {
							FPCRoutePartPoint wBRoutePartPoint = wBPointList.stream()
									.filter(p -> p.Code.equals(wFPCRoutePartPoint.Code)).findFirst().get();
							ServiceResult<Boolean> wR2 = FPCRoutePartPointC.SameAs(wFPCRoutePartPoint,
									wBRoutePartPoint);
							wFPCRoutePartPointC.IsSame = wR2.Result ? IMPSameType.Same.getValue()
									: IMPSameType.Update.getValue();
							wFPCRoutePartPointC.Remark = wR2.FaultCode;
							wFPCRoutePartPointC.VersionNo = wRouteA.VersionNo;
							wFPCRoutePartPointC.BVersionNo = wRouteB.VersionNo;
						} else {
							wFPCRoutePartPointC.IsSame = IMPSameType.Delete.getValue();
							wFPCRoutePartPointC.Remark = StringUtils.Format("工序编码：【{0}】", wFPCRoutePartPoint.Code);
							wFPCRoutePartPointC.VersionNo = wRouteA.VersionNo;
							wFPCRoutePartPointC.BVersionNo = "";
						}
						wSonList.add(wFPCRoutePartPointC);
					}
					// ⑤找到B工艺工序中所有A中不存在的工序，赋值为新增的项
					List<FPCRoutePartPoint> wBList = wBPointList.stream()
							.filter(p -> !wAPointList.stream().anyMatch(q -> q.Code.equals(p.Code)))
							.collect(Collectors.toList());
					for (FPCRoutePartPoint wFPCRoutePartPoint : wBList) {
						FPCRoutePartPointC wFPCRoutePartPointC = FPCRoutePartPointC.Clone(wFPCRoutePartPoint);
						wFPCRoutePartPointC.IsSame = IMPSameType.Add.getValue();
						wFPCRoutePartPointC.Remark = StringUtils.Format("工序编码：【{0}】", wFPCRoutePartPoint.Code);
						wFPCRoutePartPointC.VersionNo = "";
						wFPCRoutePartPointC.BVersionNo = wRouteB.VersionNo;
						wSonList.add(wFPCRoutePartPointC);
					}
					// ⑥赋值工艺工位对比
					if (wR1.Result) {
						if (wSonList.stream().anyMatch(p -> p.IsSame != IMPSameType.Same.getValue())) {
							wFPCRoutePartC.IsSame = IMPSameType.SonSpe.getValue();
							wFPCRoutePartC.Remark = "子项差异!";
							wFPCRoutePartC.VersionNo = wRouteA.VersionNo;
							wFPCRoutePartC.BVersionNo = wRouteB.VersionNo;
							wResult.add(wFPCRoutePartC);
						} else {
							wFPCRoutePartC.IsSame = IMPSameType.Same.getValue();
							wFPCRoutePartC.Remark = "";
							wFPCRoutePartC.VersionNo = wRouteA.VersionNo;
							wFPCRoutePartC.BVersionNo = wRouteB.VersionNo;
							wResult.add(wFPCRoutePartC);
						}
					} else {
						if (wSonList.stream().anyMatch(p -> p.IsSame != IMPSameType.Same.getValue())) {
							wFPCRoutePartC.IsSame = IMPSameType.Update.getValue();
							wFPCRoutePartC.Remark = wR1.FaultCode + " 【子项差异】";
							wFPCRoutePartC.VersionNo = wRouteA.VersionNo;
							wFPCRoutePartC.BVersionNo = wRouteB.VersionNo;
							wResult.add(wFPCRoutePartC);
						} else {
							wFPCRoutePartC.IsSame = IMPSameType.Update.getValue();
							wFPCRoutePartC.Remark = wR1.FaultCode;
							wFPCRoutePartC.VersionNo = wRouteA.VersionNo;
							wFPCRoutePartC.BVersionNo = wRouteB.VersionNo;
							wResult.add(wFPCRoutePartC);
						}
					}
				} else {
					wFPCRoutePartC.IsSame = IMPSameType.Delete.getValue();
					wFPCRoutePartC.Remark = StringUtils.Format("工艺集编号：【{0}】", wFPCRoutePart.Code);
					wFPCRoutePartC.VersionNo = wRouteA.VersionNo;
					wFPCRoutePartC.BVersionNo = "";
					List<FPCRoutePartPointC> wSonList = new ArrayList<FPCRoutePartPointC>();
					for (FPCRoutePartPoint wFPCRoutePartPoint : wAPointList) {
						FPCRoutePartPointC wFPCRoutePartPointC = FPCRoutePartPointC.Clone(wFPCRoutePartPoint);
						wFPCRoutePartPointC.IsSame = IMPSameType.Delete.getValue();
						wFPCRoutePartPointC.Remark = StringUtils.Format("工艺集编号：【{0}】", wFPCRoutePart.Code);
						wFPCRoutePartPointC.VersionNo = wRouteA.VersionNo;
						wFPCRoutePartPointC.BVersionNo = "";
						wSonList.add(wFPCRoutePartPointC);
					}
					wFPCRoutePartC.FPCRoutePartPointCList = wSonList;
					wResult.add(wFPCRoutePartC);
				}
			}
			// ⑥从B中找到A中不存在相同Code的工艺工位，说明为新增操作，找到此工艺工位下 的工艺工序，依次赋值
			List<FPCRoutePart> wBSList = wRoutePartBList.stream()
					.filter(p -> !wRoutePartAList.stream().anyMatch(q -> q.Code.equals(p.Code)))
					.collect(Collectors.toList());
			for (FPCRoutePart wFPCRoutePart : wBSList) {
				FPCRoutePartC wFPCRoutePartC = FPCRoutePartC.Clone(wFPCRoutePart);
				wFPCRoutePartC.IsSame = IMPSameType.Add.getValue();
				wFPCRoutePartC.Remark = StringUtils.Format("工艺集编号：【{0}】", wFPCRoutePart.Code);
				wFPCRoutePartC.VersionNo = "";
				wFPCRoutePartC.BVersionNo = wRouteB.VersionNo;

				List<FPCRoutePartPoint> wBPointList = wRoutePartPointBList.stream()
						.filter(p -> p.PartID == wFPCRoutePart.PartID).collect(Collectors.toList());
				List<FPCRoutePartPointC> wSonList = new ArrayList<FPCRoutePartPointC>();
				for (FPCRoutePartPoint wFPCRoutePartPoint : wBPointList) {
					FPCRoutePartPointC wFPCRoutePartPointC = FPCRoutePartPointC.Clone(wFPCRoutePartPoint);
					wFPCRoutePartPointC.IsSame = IMPSameType.Add.getValue();
					wFPCRoutePartPointC.Remark = StringUtils.Format("工艺集编号：【{0}】", wFPCRoutePart.Code);
					wFPCRoutePartPointC.VersionNo = "";
					wFPCRoutePartPointC.BVersionNo = wRouteB.VersionNo;
					wSonList.add(wFPCRoutePartPointC);
				}
				wFPCRoutePartC.FPCRoutePartPointCList = wSonList;
				wResult.add(wFPCRoutePartC);
			}

			// ⑦处理差异数量
			if (wResult.size() > 0) {
				for (FPCRoutePartC wFPCRoutePartC : wResult) {
					wFPCRoutePartC.Number = (int) wFPCRoutePartC.FPCRoutePartPointCList.stream()
							.filter(p -> p.IsSame != IMPSameType.Same.getValue()).count();
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSBOMItemC>> FPC_CompareBOM(BMSEmployee wLoginUser, int wABOMID, int wBBOMID) {
		ServiceResult<List<MSSBOMItemC>> wResult = new ServiceResult<List<MSSBOMItemC>>();
		wResult.Result = new ArrayList<MSSBOMItemC>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①根据bomID查询BOM
			MSSBOM wABOM = WMSServiceImpl.getInstance().MSS_QueryBOM(wLoginUser, wABOMID, "").Info(MSSBOM.class);
			MSSBOM wBBOM = WMSServiceImpl.getInstance().MSS_QueryBOM(wLoginUser, wBBOMID, "").Info(MSSBOM.class);
			// ②
			// ①根据ABOMID和BBOMID获取A的子BOM集合和B的子BOM集合
			List<MSSBOMItem> wAList = WMSServiceImpl.getInstance()
					.MSS_QueryBOMItemAll(wLoginUser, wABOMID, -1, -1, -1, -1, -1, -1, -1).List(MSSBOMItem.class);
			if (wAList == null) {
				wAList = new ArrayList<MSSBOMItem>();
			}
			List<MSSBOMItem> wBList = WMSServiceImpl.getInstance()
					.MSS_QueryBOMItemAll(wLoginUser, wBBOMID, -1, -1, -1, -1, -1, -1, -1).List(MSSBOMItem.class);
			if (wBList == null) {
				wBList = new ArrayList<MSSBOMItem>();
			}
			// ②找到A子集合和B子集合重复物料的数据，赋值对比数据
			List<MSSBOMItem> wBCloneLis = wBList;
			List<MSSBOMItem> wSameList = wAList.stream()
					.filter(p -> wBCloneLis.stream()
							.anyMatch(q -> q.PlaceID == p.PlaceID && q.PartPointID == p.PartPointID
									&& q.MaterialNo.equals(p.MaterialNo) & q.ReplaceType == p.ReplaceType))
					.collect(Collectors.toList());
			for (MSSBOMItem wMSSBOMItem : wSameList) {
				MSSBOMItemC wMSSBOMItemC = MSSBOMItemC.Clone(wMSSBOMItem);

				MSSBOMItem wItem = wBList.stream()
						.filter(p -> wMSSBOMItem.PlaceID == p.PlaceID && wMSSBOMItem.PartPointID == p.PartPointID
								&& wMSSBOMItem.MaterialNo.equals(p.MaterialNo)
								&& wMSSBOMItem.ReplaceType == p.ReplaceType)
						.findFirst().get();

				ServiceResult<Boolean> wSR = MSSBOMItemC.SameAs(wMSSBOMItem, wItem);

				if (wSR.Result) {
					wMSSBOMItemC.IsSame = IMPSameType.Same.getValue();
					wMSSBOMItemC.CompareRemark = "";
					wMSSBOMItemC.BOMNoA = wABOM.BOMNo;
					wMSSBOMItemC.BOMNoB = wBBOM.BOMNo;
					wResult.Result.add(wMSSBOMItemC);
				} else {
					wMSSBOMItemC.IsSame = IMPSameType.Update.getValue();
					wMSSBOMItemC.CompareRemark = wSR.FaultCode;
					wMSSBOMItemC.BOMNoA = wABOM.BOMNo;
					wMSSBOMItemC.BOMNoB = wBBOM.BOMNo;
					wResult.Result.add(wMSSBOMItemC);
				}
			}
			// ③从A中找到B中不存在相同物料编码的数据，赋值对比数据
			List<MSSBOMItem> wANList = wAList.stream()
					.filter(p -> !wBCloneLis.stream()
							.anyMatch(q -> q.PlaceID == p.PlaceID && q.PartPointID == p.PartPointID
									&& q.MaterialNo.equals(p.MaterialNo) && q.ReplaceType == p.ReplaceType))
					.collect(Collectors.toList());
			for (MSSBOMItem wMSSBOMItem : wANList) {
				MSSBOMItemC wMSSBOMItemC = MSSBOMItemC.Clone(wMSSBOMItem);
				wMSSBOMItemC.IsSame = IMPSameType.Delete.getValue();
				wMSSBOMItemC.CompareRemark = wMSSBOMItem.MaterialNo;
				wMSSBOMItemC.BOMNoA = wABOM.BOMNo;
				wMSSBOMItemC.BOMNoB = "";
				wResult.Result.add(wMSSBOMItemC);
			}
			// ④从B中找到A中不存在相同物料编码的数据，赋值对比数据
			List<MSSBOMItem> wACloneList = wAList;
			List<MSSBOMItem> wBNList = wBList.stream()
					.filter(p -> !wACloneList.stream()
							.anyMatch(q -> q.PlaceID == p.PlaceID && q.PartPointID == p.PartPointID
									&& q.MaterialNo.equals(p.MaterialNo) && q.ReplaceType == p.ReplaceType))
					.collect(Collectors.toList());
			for (MSSBOMItem wMSSBOMItem : wBNList) {
				MSSBOMItemC wMSSBOMItemC = MSSBOMItemC.Clone(wMSSBOMItem);
				wMSSBOMItemC.IsSame = IMPSameType.Add.getValue();
				wMSSBOMItemC.CompareRemark = wMSSBOMItem.MaterialNo;
				wMSSBOMItemC.BOMNoA = "";
				wMSSBOMItemC.BOMNoB = wBBOM.BOMNo;
				wResult.Result.add(wMSSBOMItemC);
			}

			if (wResult.Result.size() > 0) {
				// 排序
				wResult.Result.sort(Comparator.comparing(MSSBOMItemC::getPlaceID));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> FPC_ExportSteps(BMSEmployee wLoginUser, int wProductID, int wLineID, int wPartID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-{1}-{2}-{3}-过程控制记录.xlsx", QMSConstants.GetFMCLineName(wLineID),
					QMSConstants.GetFPCProductName(wProductID), QMSConstants.GetFPCPartName(wPartID), wCurTime);
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();

			List<List<String>> wSourceList = this.GetSourceList(wLoginUser, wProductID, wLineID, wPartID);

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);

			String wPath = Constants.getConfigPath();
			FileInputStream wInputStream = new FileInputStream(new File(wPath + "Template.xlsx"));

			ExcelUtil.SCGC_WriteModel(wSourceList, wFileOutputStream, wInputStream);
			String wUri = StringUtils.Format("/{0}/export/{1}",
					new Object[] { Configuration.readConfigString("project.name", "application"), wFileName });
			wResult.Result = wUri;
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取产线单元明细数据源
	 */
	private List<List<String>> GetSourceList(BMSEmployee wLoginUser, int wProductID, int wLineID, int wPartID) {
		List<List<String>> wResult = new ArrayList<List<String>>();
		try {
			List<FMCLineUnit> wList = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(wLoginUser, wLineID, -1, wProductID, true).List(FMCLineUnit.class);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			wList = wList.stream().filter(p -> p.Active == 1 && p.LevelID == 3 && p.ParentUnitID == wPartID)
					.collect(Collectors.toList());

			List<String> wHeadList = new ArrayList<String>(Arrays.asList("工序\r\n", "一级项点\r\n", "二级项点\r\n", "三级项点\r\n",
					"四级项点\r\n", "五级项点\r\n", "检查标准\r\n", "预设厂家\r\n", "预设型号\r\n", "\"厂家必填\r\n（是/否/不显示）\"\r\n",
					"\"型号必填\r\n（是/否/不显示）\"\r\n", "编号必填（是/否/不显示）\r\n", "填写值类型\r\n", "\"填写值必填\r\n", "单位\r\n",
					"填写值选项列表（用\"||\"隔开，示例：abc||def）\r\n", "厂家选项列表（用\"||\"隔开，示例：abc||def）\r\n",
					"型号选项列表（用\"||\"隔开，示例：abc||def）\r\n", "预设处理工位\r\n", "预设处理工序\r\n", "段改项是否预检（是/否）\r\n",
					"图片必填（是/否/不显示）\r\n", "视频必填（是/否/不显示）\r\n", "标准值\r\n", "上限值\r\n", "下限值\r\n"));
			wResult.add(wHeadList);

			for (FMCLineUnit wFMCLineUnit : wList) {
				String wStepName = QMSConstants.GetFPCStepName(wFMCLineUnit.UnitID);
				if (StringUtils.isEmpty(wStepName)) {
					continue;
				}

				List<String> wRowValueList = new ArrayList<String>(Arrays.asList(wStepName, "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
				wResult.add(wRowValueList);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> ExportOrgnization(BMSEmployee wLoginUser) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyy年MM月-ddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-制造中心员工组织架构.xls", wCurTime);
			String wDirePath = StringUtils.Format("{0}static/export/",
					new Object[] { Constants.getConfigPath().replace("config/", "") });
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			String wFilePath = StringUtils.Format("{0}{1}", new Object[] { wDirePath, wFileName });
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();

			List<BMSWorkareaOrgnization> wSourceList = this.GetSourceList(wLoginUser);

			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);

			ExcelUtil.BMS_WriteOrgnization(wSourceList, wFileOutputStream);
			String wUri = StringUtils.Format("/{0}/export/{1}",
					new Object[] { Configuration.readConfigString("project.name", "application"), wFileName });
			wResult.Result = wUri;
			wResult.setFaultCode(MESException.getEnumType((int) wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取制造架构数据源
	 */
	private List<BMSWorkareaOrgnization> GetSourceList(BMSEmployee wLoginUser) {
		List<BMSWorkareaOrgnization> wResult = new ArrayList<BMSWorkareaOrgnization>();
		try {
			// ①获取工区列表
			List<BMSDepartment> wWorkareaList = QMSConstants.GetBMSDepartmentList().values().stream()
					.filter(p -> p.Type == BMSDepartmentType.Area.getValue()).collect(Collectors.toList());
			// ②获取班组列表
			List<BMSDepartment> wClassList = QMSConstants.GetBMSDepartmentList().values().stream()
					.filter(p -> p.Type == BMSDepartmentType.Class.getValue()).collect(Collectors.toList());
			// ③获取人员列表
			List<BMSEmployee> wEmployeeList = QMSConstants.GetBMSEmployeeList().values().stream()
					.filter(p -> p.Active == 1).collect(Collectors.toList());
			// ④班组工位列表
			List<BMSWorkCharge> wWorkChargeList = QMSConstants.GetBMSWorkChargeList().values().stream()
					.filter(p -> p.Active == 1).collect(Collectors.toList());
			// ④遍历工区，获取工区班组，获取班组人员，统计
			for (BMSDepartment wBMSDepartment : wWorkareaList) {
				BMSWorkareaOrgnization wBMSWorkareaOrgnization = new BMSWorkareaOrgnization();
				wBMSWorkareaOrgnization.WorkareaName = wBMSDepartment.Name;
				wBMSWorkareaOrgnization.ClassList = new ArrayList<BMSClass>();
				wResult.add(wBMSWorkareaOrgnization);

				List<BMSDepartment> wChildList = wClassList.stream().filter(p -> p.ParentID == wBMSDepartment.ID)
						.collect(Collectors.toList());
				for (BMSDepartment wChild : wChildList) {
					List<BMSEmployee> wMemberList = wEmployeeList.stream().filter(p -> p.DepartmentID == wChild.ID)
							.collect(Collectors.toList());

					BMSClass wBMSClass = new BMSClass();
					wBMSClass.ClassName = wChild.Name;
					wBMSClass.FQTYClass = wMemberList.size();
					wBMSClass.FQTYIntern = (int) wMemberList.stream()
							.filter(p -> p.Type == BMSEmployeeType.Interns.getValue()).count();
					wBMSClass.FQTYOnTheJob = wMemberList.size();
					wBMSClass.FQTYReemployment = (int) wMemberList.stream()
							.filter(p -> p.Type == BMSEmployeeType.RaRetirement.getValue()
									|| p.Type == BMSEmployeeType.OutSource.getValue())
							.count();
					wBMSClass.FQTYRegularWorkers = (int) wMemberList.stream()
							.filter(p -> p.Type == BMSEmployeeType.Regular.getValue()).count();
					wBMSClass.TeamMembers = wMemberList;
					List<BMSWorkCharge> wChargeList = wWorkChargeList.stream().filter(p -> p.ClassID == wChild.ID)
							.collect(Collectors.toList());
					wBMSClass.ResponsibleStations = this.GetPartNames(wChargeList);
					wBMSWorkareaOrgnization.ClassList.add(wBMSClass);

					wBMSWorkareaOrgnization.FQTYWorkarea += wBMSClass.FQTYClass;
				}
			}

			// ①技能员工(物流配送班、工具室、助勤)
			BMSWorkareaOrgnization wTechOrg = new BMSWorkareaOrgnization();
			List<BMSDepartment> wTechClassList = QMSConstants.GetBMSDepartmentList().values().stream()
					.filter(p -> p.Name.equals("物流配送班") || p.Name.equals("工具室") || p.Name.equals("助勤"))
					.collect(Collectors.toList());
			for (BMSDepartment wBMSDepartment : wTechClassList) {
				List<BMSEmployee> wMemberList = wEmployeeList.stream().filter(p -> p.DepartmentID == wBMSDepartment.ID)
						.collect(Collectors.toList());

				BMSClass wBMSClass = new BMSClass();
				wBMSClass.ClassName = wBMSDepartment.Name;
				wBMSClass.FQTYClass = wMemberList.size();
				wBMSClass.FQTYIntern = (int) wMemberList.stream()
						.filter(p -> p.Type == BMSEmployeeType.Interns.getValue()).count();
				wBMSClass.FQTYOnTheJob = wMemberList.size();
				wBMSClass.FQTYReemployment = (int) wMemberList.stream()
						.filter(p -> p.Type == BMSEmployeeType.RaRetirement.getValue()
								|| p.Type == BMSEmployeeType.OutSource.getValue())
						.count();
				wBMSClass.FQTYRegularWorkers = (int) wMemberList.stream()
						.filter(p -> p.Type == BMSEmployeeType.Regular.getValue()).count();
				wBMSClass.TeamMembers = wMemberList;
				wBMSClass.ResponsibleStations = "";
				wTechOrg.ClassList.add(wBMSClass);

				wTechOrg.FQTYWorkarea += wBMSClass.FQTYClass;
			}
			wResult.add(wTechOrg);
			// ②管理人员
			BMSWorkareaOrgnization wManOrg = new BMSWorkareaOrgnization();
			wManOrg.WorkareaName = "管理人员";
			List<BMSDepartment> wManClassList = QMSConstants.GetBMSDepartmentList().values().stream()
					.filter(p -> p.Name.equals("生产管理室") || p.Name.equals("技术质量室") || p.Name.equals("技师工作室"))
					.collect(Collectors.toList());

			List<BMSEmployee> wSjList = wEmployeeList.stream()
					.filter(p -> QMSConstants.GetBMSPositionName(p.Position).equals("(制造中心)部门负责人"))
					.collect(Collectors.toList());
			BMSClass wBMSClass0 = new BMSClass();
			wBMSClass0.ClassName = "书记 经理";
			wBMSClass0.FQTYClass = wSjList.size();
			wBMSClass0.FQTYIntern = (int) wSjList.stream().filter(p -> p.Type == BMSEmployeeType.Interns.getValue())
					.count();
			wBMSClass0.FQTYOnTheJob = wSjList.size();
			wBMSClass0.FQTYReemployment = (int) wSjList.stream()
					.filter(p -> p.Type == BMSEmployeeType.RaRetirement.getValue()
							|| p.Type == BMSEmployeeType.OutSource.getValue())
					.count();
			wBMSClass0.FQTYRegularWorkers = (int) wSjList.stream()
					.filter(p -> p.Type == BMSEmployeeType.Regular.getValue()).count();
			wBMSClass0.TeamMembers = wSjList;
			wBMSClass0.ResponsibleStations = "";
			wManOrg.ClassList.add(wBMSClass0);

			wManOrg.FQTYWorkarea += wBMSClass0.FQTYClass;

			for (BMSDepartment wBMSDepartment : wManClassList) {
				List<BMSEmployee> wMemberList = wEmployeeList.stream().filter(p -> p.DepartmentID == wBMSDepartment.ID)
						.collect(Collectors.toList());

				BMSClass wBMSClass = new BMSClass();
				wBMSClass.ClassName = wBMSDepartment.Name;
				wBMSClass.FQTYClass = wMemberList.size();
				wBMSClass.FQTYIntern = (int) wMemberList.stream()
						.filter(p -> p.Type == BMSEmployeeType.Interns.getValue()).count();
				wBMSClass.FQTYOnTheJob = wMemberList.size();
				wBMSClass.FQTYReemployment = (int) wMemberList.stream()
						.filter(p -> p.Type == BMSEmployeeType.RaRetirement.getValue()
								|| p.Type == BMSEmployeeType.OutSource.getValue())
						.count();
				wBMSClass.FQTYRegularWorkers = (int) wMemberList.stream()
						.filter(p -> p.Type == BMSEmployeeType.Regular.getValue()).count();
				wBMSClass.TeamMembers = wMemberList;
				wBMSClass.ResponsibleStations = "";
				wManOrg.ClassList.add(wBMSClass);

				wManOrg.FQTYWorkarea += wBMSClass.FQTYClass;
			}
			wResult.add(wManOrg);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取工位名称
	 */
	private String GetPartNames(List<BMSWorkCharge> wChargeList) {
		String wResult = "";
		try {
			if (wChargeList == null || wChargeList.size() <= 0) {
				return wResult;
			}

			List<String> wNames = new ArrayList<String>();
			for (BMSWorkCharge wBMSWorkCharge : wChargeList) {
				String wName = QMSConstants.GetFPCPartName(wBMSWorkCharge.StationID);
				if (StringUtils.isEmpty(wName)) {
					continue;
				}
				wNames.add(wName);
			}

			if (wNames.size() > 0) {
				wResult = StringUtils.Join("、", wNames);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> FPC_CopyBOP(BMSEmployee wLoginUser, int wSourceRouteID, int wTargetRouteID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			// 复制工艺工位
			List<FPCRoutePart> wRoutePartList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartListByRouteID(wLoginUser, wSourceRouteID).List(FPCRoutePart.class);
			if (wRoutePartList != null && wRoutePartList.size() > 0) {
				wRoutePartList.forEach(p -> {
					p.RouteID = wTargetRouteID;
					p.ID = 0;
				});
				for (FPCRoutePart wFPCRoutePart : wRoutePartList) {
					FMCServiceImpl.getInstance().FPC_SaveRoutePart(wLoginUser, wFPCRoutePart);
				}
			}
			// 复制工艺工序
			List<FPCRoutePartPoint> wRoutePartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartPointListByRouteID(wLoginUser, wSourceRouteID, -1).List(FPCRoutePartPoint.class);
			if (wRoutePartPointList != null && wRoutePartList.size() > 0) {
				wRoutePartPointList.forEach(p -> {
					p.RouteID = wTargetRouteID;
					p.ID = 0;
				});
				for (FPCRoutePartPoint wFPCRoutePartPoint : wRoutePartPointList) {
					FMCServiceImpl.getInstance().FPC_SaveRoutePartPoint(wLoginUser, wFPCRoutePartPoint);
				}
			}
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> FPC_CopyBOM(BMSEmployee wLoginUser, int wSourceBOMID, int wTargetBOMID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>();
		try {
			List<MSSBOMItem> wItemList = WMSServiceImpl.getInstance()
					.MSS_QueryBOMItemAll(wLoginUser, wSourceBOMID, -1, -1, -1, -1, -1, -1, -1).List(MSSBOMItem.class);
			for (MSSBOMItem wMSSBOMItem : wItemList) {
				wMSSBOMItem.ID = 0;
				wMSSBOMItem.BOMID = wTargetBOMID;
			}
			WMSServiceImpl.getInstance().MSS_SaveBOMItemList(wLoginUser, wItemList);
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> IPT_ExportMSSBOM(BMSEmployee wLoginUser, List<MSSBOM> wItemList) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wItemList == null || wItemList.size() <= 0) {
				return wResult;
			}

			// 文件名
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-标准BOM.xls", wCurTime);
			// ①获取本地文件路径
			String wDirePath = StringUtils.Format("{0}static/export/",
					Constants.getConfigPath().replace("config/", ""));
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			// ②创建本地文件
			String wFilePath = StringUtils.Format("{0}{1}", wDirePath, wFileName);
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			// ③写入本地文件
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			// 导出BOM数据
			QMSUtils.getInstance().IPT_ExportMSSBOM(wLoginUser, wFileOutputStream, wItemList);
			// ④返回文件链接
			String wUri = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);
			wResult.Result = wUri;

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> IPT_ExportMSSBOMItem(BMSEmployee wLoginUser, int wBOMID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// 文件名
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			String wFileName = StringUtils.Format("{0}-标准BOM子项.xls", wCurTime);
			// ①获取本地文件路径
			String wDirePath = StringUtils.Format("{0}static/export/",
					Constants.getConfigPath().replace("config/", ""));
			File wDirFile = new File(wDirePath);
			if (!wDirFile.exists()) {
				wDirFile.mkdirs();
			}
			// ②创建本地文件
			String wFilePath = StringUtils.Format("{0}{1}", wDirePath, wFileName);
			File wNewFile = new File(wFilePath);
			wNewFile.createNewFile();
			// ③写入本地文件
			FileOutputStream wFileOutputStream = new FileOutputStream(wNewFile);
			// 导出BOM数据
			QMSUtils.getInstance().IPT_ExportMSSBOMItem(wLoginUser, wFileOutputStream, wBOMID);
			// ④返回文件链接
			String wUri = StringUtils.Format("/{0}/export/{1}",
					Configuration.readConfigString("project.name", "application"), wFileName);
			wResult.Result = wUri;

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> FPC_CopyLineUnit(BMSEmployee wLoginUser, int wSProductID, int wSLineID, int wTLineID,
			int wTProductID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<FMCLineUnit> wLineUnitList = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(BaseDAO.SysAdmin, -1, -1, -1, false).List(FMCLineUnit.class);
			if (wLineUnitList == null || wLineUnitList.size() <= 0) {
				return wResult;
			}

			// ①删除目标修程、目标车型的产线单元明细
			FPCCommonFileDAO.getInstance().FPC_DeleteLineUnit(wLoginUser, wTLineID, wTProductID, wErrorCode);
			// ②查询目标修程、目标车型的产线单元明细
			wLineUnitList = wLineUnitList.stream().filter(p -> p.ProductID == wSProductID && p.LineID == wSLineID)
					.collect(Collectors.toList());
			// ③复制目标修程、目标车型的产线单元明细
			for (FMCLineUnit wFMCLineUnit : wLineUnitList) {
				if (wFMCLineUnit.LevelID == 2) {
					wFMCLineUnit.ParentUnitID = wTLineID;
				}
				wFMCLineUnit.ID = 0;
				wFMCLineUnit.LineID = wTLineID;
				wFMCLineUnit.ProductID = wTProductID;
				FMCServiceImpl.getInstance().FMC_SaveLineUnit(BaseDAO.SysAdmin, wFMCLineUnit);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> FPC_SynchronizeLineUnit(BMSEmployee wLoginUser, int wRouteID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①查询工艺BOP
			FPCRoute wRoute = FMCServiceImpl.getInstance().FPC_QueryRouteByID(wLoginUser, wRouteID)
					.Info(FPCRoute.class);
			if (wRoute == null || wRoute.ID <= 0) {
				return wResult;
			}
			// 查询产线单元明细
			List<FMCLineUnit> wLineUnitList = FMCServiceImpl.getInstance()
					.FMC_QueryLineUnitListByLineID(wLoginUser, wRoute.LineID, -1, wRoute.ProductID, true)
					.List(FMCLineUnit.class);
			// 查询工艺工位
			List<FPCRoutePart> wRoutePartList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartListByRouteID(wLoginUser, wRouteID).List(FPCRoutePart.class);
			List<FMCLineUnit> wPartLineUnitList = wLineUnitList.stream().filter(p -> p.LevelID == 2)
					.collect(Collectors.toList());
			for (FPCRoutePart wFPCRoutePart : wRoutePartList) {
				if (!wPartLineUnitList.stream().anyMatch(p -> p.LineID == wRoute.LineID
						&& p.ProductID == wRoute.ProductID && p.UnitID == wFPCRoutePart.PartID)) {
					FMCLineUnit wFMCLineUnit = new FMCLineUnit();
					wFMCLineUnit.Active = 1;
					wFMCLineUnit.LineID = wRoute.LineID;
					wFMCLineUnit.UnitID = wFPCRoutePart.PartID;
					wFMCLineUnit.OrderID = 1;
					wFMCLineUnit.LevelID = 2;
					wFMCLineUnit.ProductID = wRoute.ProductID;
					wFMCLineUnit.CreateTime = Calendar.getInstance();
					wFMCLineUnit.Status = 1;
					wFMCLineUnit.ParentUnitID = wRoute.LineID;
					FMCServiceImpl.getInstance().FMC_SaveLineUnit(BaseDAO.SysAdmin, wFMCLineUnit);
				}
			}
			// ②查询工艺工序
			List<FPCRoutePartPoint> wRoutePartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartPointListByRouteID(wLoginUser, wRouteID, -1).List(FPCRoutePartPoint.class);
			// ③查询产线单元明细
			wLineUnitList = wLineUnitList.stream().filter(p -> p.LevelID == 3).collect(Collectors.toList());
			// ④遍历工艺工序，新增
			for (FPCRoutePartPoint wFPCRoutePartPoint : wRoutePartPointList) {
				if (!wLineUnitList.stream().anyMatch(p -> p.LineID == wRoute.LineID && p.ProductID == wRoute.ProductID
						&& p.ParentUnitID == wFPCRoutePartPoint.PartID && p.UnitID == wFPCRoutePartPoint.PartPointID)) {
					FMCLineUnit wFMCLineUnit = new FMCLineUnit();
					wFMCLineUnit.Active = 1;
					wFMCLineUnit.LineID = wRoute.LineID;
					wFMCLineUnit.UnitID = wFPCRoutePartPoint.PartPointID;
					wFMCLineUnit.OrderID = 1;
					wFMCLineUnit.LevelID = 3;
					wFMCLineUnit.ProductID = wRoute.ProductID;
					wFMCLineUnit.CreateTime = Calendar.getInstance();
					wFMCLineUnit.Status = 1;
					wFMCLineUnit.ParentUnitID = wFPCRoutePartPoint.PartID;
					FMCServiceImpl.getInstance().FMC_SaveLineUnit(BaseDAO.SysAdmin, wFMCLineUnit);
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
	public ServiceResult<Integer> FPC_DynamicCompareBop(BMSEmployee wLoginUser, int wNewRouteID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①查询正在使用的工艺bop
			int wRouteID = FPCCommonFileDAO.getInstance().GetUsingRouteID(wLoginUser, wNewRouteID, wErrorCode);
			if (wRouteID <= 0) {
				wResult.FaultCode += "提示：同车型、修程、局段无正在使用的工艺bop。";
				return wResult;
			}
			// ②获取新版工艺工序集合
			List<FPCRoutePartPoint> wNewRoutePartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartPointListByRouteID(wLoginUser, wNewRouteID, -1).List(FPCRoutePartPoint.class);
			if (wNewRoutePartPointList == null || wNewRoutePartPointList.size() <= 0) {
				wResult.FaultCode += "提示：新版bop工艺工序获取失败，请检查数据。";
				return wResult;
			}
			// ③获取老版工艺工序集合
			List<FPCRoutePartPoint> wOldRoutePartPointList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartPointListByRouteID(wLoginUser, wRouteID, -1).List(FPCRoutePartPoint.class);
			if (wOldRoutePartPointList == null || wOldRoutePartPointList.size() <= 0) {
				wResult.FaultCode += "提示：老版bop工艺工序获取失败，请检查数据。";
				return wResult;
			}
			// ④提取工位列表
			List<Integer> wPartIDList = wNewRoutePartPointList.stream().map(p -> p.PartID).distinct()
					.collect(Collectors.toList());
			// ⑤遍历筛选
			List<FPCRoutePartPoint> wAddedList = new ArrayList<FPCRoutePartPoint>();
			List<FPCRoutePartPoint> wRemovedList = new ArrayList<FPCRoutePartPoint>();
			List<FPCRoutePartPoint> wChangedList = new ArrayList<FPCRoutePartPoint>();
			List<FPCRoutePartPoint> wUpdatedList = new ArrayList<FPCRoutePartPoint>();
			for (Integer wPartID : wPartIDList) {
				List<FPCRoutePartPoint> wNewList = wNewRoutePartPointList.stream().filter(p -> p.PartID == wPartID)
						.collect(Collectors.toList());
				List<FPCRoutePartPoint> wOldList = wOldRoutePartPointList.stream().filter(p -> p.PartID == wPartID)
						.collect(Collectors.toList());

				// ⑥新增的工序列表
				List<FPCRoutePartPoint> wAddTempList = wNewList.stream()
						.filter(p -> !wOldList.stream().anyMatch(q -> q.PartPointID == p.PartPointID))
						.collect(Collectors.toList());
				if (wAddTempList.size() > 0) {
					wAddedList.addAll(wAddTempList);
				}

				// ⑦删除的工序列表
				List<FPCRoutePartPoint> wRemoveTempList = wOldList.stream()
						.filter(p -> !wNewList.stream().anyMatch(q -> p.PartPointID == q.PartPointID))
						.collect(Collectors.toList());
				if (wRemoveTempList.size() > 0) {
					wRemovedList.addAll(wRemoveTempList);
				}

				// ③修改了工艺文件的工序列表
				AddUpdatedStep(wErrorCode, wUpdatedList, wNewList, wOldList);
			}
			// ⑧修改的工序列表
			wChangedList = wOldRoutePartPointList.stream()
					.filter(p -> wNewRoutePartPointList.stream()
							.anyMatch(q -> p.PartPointID == q.PartPointID && p.PartID != q.PartID)
							&& !wOldRoutePartPointList.stream()
									.anyMatch(q -> q.PartPointID == p.PartPointID && q.PartID != p.PartID))
					.collect(Collectors.toList());
			for (FPCRoutePartPoint wFPCRoutePartPoint : wChangedList) {
				FPCRoutePartPoint wItem = wNewRoutePartPointList.stream()
						.filter(p -> p.PartPointID == wFPCRoutePartPoint.PartPointID).findFirst().get();
				wFPCRoutePartPoint.NewPartID = wItem.PartID;
				wFPCRoutePartPoint.NewPartName = wItem.PartName;
			}
			// ⑨修改和删除重复，以修改为准
			List<FPCRoutePartPoint> wClonedChangeList = wChangedList;
			wRemovedList.removeIf(p -> wClonedChangeList.stream().anyMatch(q -> q.ID == p.ID));
			// ⑨受影响的车辆列表
			List<String> wPartNoList = FPCCommonFileDAO.getInstance().GetPartNoList(wLoginUser, wRouteID, wErrorCode);
			@SuppressWarnings("unchecked")
			List<Integer> wOrderIDList = (List<Integer>) wErrorCode.get("OrderIDList");
			// ⑨移除同工位数据
			wChangedList.removeIf(p -> p.PartID == p.NewPartID);
			// ⑨修改和新增的工序重复，以修改为准
			wAddedList.removeIf(p -> wClonedChangeList.stream()
					.anyMatch(q -> p.PartID == q.NewPartID && p.PartPointID == q.PartPointID));

			wResult.CustomResult.put("OldRouteID", wRouteID);
			wResult.CustomResult.put("NewRouteID", wNewRouteID);
			wResult.CustomResult.put("AddedList", wAddedList);
			wResult.CustomResult.put("RemovedList", wRemovedList);
			wResult.CustomResult.put("ChangedList", wChangedList);
			wResult.CustomResult.put("PartNoList", wPartNoList);
			wResult.CustomResult.put("UpdatedList", wUpdatedList);
			wResult.CustomResult.put("OrderIDList", wOrderIDList);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取修改了工艺文件的工序
	 */
	private void AddUpdatedStep(OutResult<Integer> wErrorCode, List<FPCRoutePartPoint> wUpdatedList,
			List<FPCRoutePartPoint> wNewList, List<FPCRoutePartPoint> wOldList) {
		try {
			List<FPCRoutePartPoint> wSameNewRoutePartPointList = wNewList.stream()
					.filter(p -> wOldList.stream().anyMatch(q -> q.PartPointID == p.PartPointID))
					.collect(Collectors.toList());
			List<FPCRoutePartPoint> wSameOldRoutePartPointList = wOldList.stream()
					.filter(p -> wNewList.stream().anyMatch(q -> q.PartPointID == p.PartPointID))
					.collect(Collectors.toList());
			List<Integer> wIDList = wSameNewRoutePartPointList.stream().map(p -> p.ID).distinct()
					.collect(Collectors.toList());
			wIDList.addAll(wSameOldRoutePartPointList.stream().map(p -> p.ID).distinct().collect(Collectors.toList()));
			List<FPCStepSOP> wSOPList = FPCRouteDAO.getInstance().SelectSOPList(BaseDAO.SysAdmin, wIDList, wErrorCode);
			for (FPCRoutePartPoint wFPCRoutePartPoint : wSameOldRoutePartPointList) {
				List<FPCStepSOP> wOList = wSOPList.stream().filter(p -> p.RoutePartPointID == wFPCRoutePartPoint.ID)
						.collect(Collectors.toList());
				int wNewRoutePartPointID = wSameNewRoutePartPointList.stream()
						.filter(p -> p.PartPointID == wFPCRoutePartPoint.PartPointID).findFirst().get().ID;
				List<FPCStepSOP> wNList = wSOPList.stream().filter(p -> p.RoutePartPointID == wNewRoutePartPointID)
						.collect(Collectors.toList());
				// ①新旧工艺文件对比
				if (wOList.size() != wNList.size() || wOList.stream()
						.anyMatch(p -> !wNList.stream().anyMatch(q -> p.FilePath.equals(q.FilePath)))) {
					wUpdatedList.add(wFPCRoutePartPoint);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<Integer> FPC_DynamicTurnBop(BMSEmployee wLoginUser, int wOldRouteID,
			List<FPCRoutePartPoint> wAddedList, List<FPCRoutePartPoint> wRemovedList,
			List<FPCRoutePartPoint> wChangedList, List<String> wPartNoList, int wNewRouteID,
			List<Integer> wReworkOrdreIDList, List<FPCRoutePartPoint> wUpdatedList, int wChangeLogID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①获取工艺bop
			FPCRoute wFPCRoute = FMCServiceImpl.getInstance().FPC_QueryRouteByID(wLoginUser, wOldRouteID)
					.Info(FPCRoute.class);
			// ①根据车号列表获取订单号列表(修改后，选择的车做变更)
//			List<Integer> wOrderIDList = FPCCommonFileDAO.getInstance().GetOrderIDList(wLoginUser, wPartNoList,
//					wErrorCode);
			List<Integer> wOrderIDList = wReworkOrdreIDList;
			// ①遍历修改订单的RouteID
			for (int wOrderID : wOrderIDList) {
				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
						.Info(OMSOrder.class);
				if (wOrder != null && wOrder.ID > 0) {
					wOrder.RouteID = wNewRouteID;
					LOCOAPSServiceImpl.getInstance().OMS_UpdateOrder(BaseDAO.SysAdmin, wOrder);
				}
			}
			// ①新增工序
			AddPartPoint01(wLoginUser, wOrderIDList, wAddedList, wOldRouteID, wFPCRoute, wReworkOrdreIDList);
			// ②删除工序
			RemovePartPoint02(wLoginUser, wOrderIDList, wRemovedList, wOldRouteID, wFPCRoute);
			// ③修改工序(工位)
			UpdatePartPoint03(wLoginUser, wOrderIDList, wChangedList, wOldRouteID, wFPCRoute);
			// ④修改工序(工艺文件)
//			UpdatePartPoint04(wLoginUser, wFPCRoute, wUpdatedList, wChangeLogID);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 修改工序(工艺文件)→将变更内容推送到技术质量室主管修改对应工序的检验标准
	 */
	@SuppressWarnings("unused")
	private void UpdatePartPoint04(BMSEmployee wLoginUser, FPCRoute wFPCRoute, List<FPCRoutePartPoint> wUpdatedList,
			int wChangeLogID) {
		try {
			if (wUpdatedList == null || wUpdatedList.size() <= 0) {
				return;
			}

			List<BMSEmployee> wUserList = QMSConstants.GetBMSEmployeeList().values().stream()
					.filter(p -> p.Position == 11).collect(Collectors.toList());

			if (wUserList == null || wUserList.size() <= 0) {
				return;
			}

			List<BFCMessage> wBFCMessageList = new ArrayList<>();
			BFCMessage wMessage = null;
			SimpleDateFormat wSDF = new SimpleDateFormat("yyyyMMdd");
			int wShiftID = Integer.parseInt(wSDF.format(Calendar.getInstance().getTime()));
			for (BMSEmployee wItem : wUserList) {
				wMessage = new BFCMessage();

				wMessage.Active = 0;
				wMessage.CompanyID = 0;
				wMessage.CreateTime = Calendar.getInstance();
				wMessage.EditTime = Calendar.getInstance();
				wMessage.ID = 0;
				wMessage.MessageID = wChangeLogID;
				wMessage.Title = StringUtils.Format("{0} {1}", BPMEventModule.StepChangeUpdateFile.getLable(),
						String.valueOf(wShiftID));
				wMessage.MessageText = StringUtils.Format("【{0}】-【{1}】工序的工艺文件发生变更，请查看详情并及时修改对应工序的检验规程。",
						QMSConstants.GetFPCProductNo(wFPCRoute.ProductID),
						QMSConstants.GetFMCLineName(wFPCRoute.LineID));
				wMessage.ModuleID = BPMEventModule.StepChangeUpdateFile.getValue();
				wMessage.ResponsorID = wItem.ID;
				wMessage.ShiftID = wShiftID;
				wMessage.StationID = 0;
				wMessage.Type = BFCMessageType.Task.getValue();

				wBFCMessageList.add(wMessage);
			}
			CoreServiceImpl.getInstance().BFC_UpdateMessageList(wLoginUser, wBFCMessageList);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 修改工序
	 */
	private void UpdatePartPoint03(BMSEmployee wLoginUser, List<Integer> wOrderIDList,
			List<FPCRoutePartPoint> wChangedList, int wOldRouteID, FPCRoute wFPCRoute) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			for (FPCRoutePartPoint wFPCRoutePartPoint : wChangedList) {
				// ②修改工序任务
				for (Integer wOrderID : wOrderIDList) {
					FPCCommonFileDAO.getInstance().UpdateTaskPartPoint(wLoginUser, wFPCRoutePartPoint, wOrderID,
							wErrorCode);
				}
				// ③修改产线单元明细
				FPCCommonFileDAO.getInstance().UpdateLineUnit(wLoginUser, wFPCRoutePartPoint, wFPCRoute, wErrorCode);
				// ④修改标准(复制标准)
				// 目标车型、修程、工位、工序所有标准设置为非当前
				// 判断新工位是否有当前标准
				boolean wCheckResult = FPCCommonFileDAO.getInstance().JudgeIsHasStandard(wLoginUser,
						wFPCRoute.ProductID, wFPCRoute.LineID, wFPCRoutePartPoint.NewPartID,
						wFPCRoutePartPoint.PartPointID, wErrorCode);
				if (wCheckResult) {
					// 复制一份到新的工位上
					IPTServiceImpl.getInstance().IPT_BatchCopyStandard(wLoginUser, wFPCRoute.ProductID,
							wFPCRoute.LineID, wFPCRoutePartPoint.PartID, wFPCRoutePartPoint.PartPointID,
							wFPCRoutePartPoint.NewPartID, wFPCRoutePartPoint.PartPointID, true);
				} else {
					// 复制一份到新的工位上
					IPTServiceImpl.getInstance().IPT_BatchCopyStandard(wLoginUser, wFPCRoute.ProductID,
							wFPCRoute.LineID, wFPCRoutePartPoint.PartID, wFPCRoutePartPoint.PartPointID,
							wFPCRoutePartPoint.NewPartID, wFPCRoutePartPoint.PartPointID, false);
				}

//				FPCCommonFileDAO.getInstance().UpdateStandard(wLoginUser, wFPCRoutePartPoint, wFPCRoute, wErrorCode);

			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 删除工序
	 */
	private void RemovePartPoint02(BMSEmployee wLoginUser, List<Integer> wOrderIDList,
			List<FPCRoutePartPoint> wRemovedList, int wOldRouteID, FPCRoute wFPCRoute) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			for (FPCRoutePartPoint wFPCRoutePartPoint : wRemovedList) {
				// ①删除工艺bop工序
//				FPCCommonFileDAO.getInstance().DeleteRoutePartPoint(wLoginUser, wFPCRoutePartPoint, wErrorCode);
				for (int wOrderID : wOrderIDList) {
					// ①判断是否需要禁用日计划
					boolean wIsMustAdd = FPCCommonFileDAO.getInstance().JugdeIsNeedAddDayPlan(wLoginUser, wOrderID,
							wFPCRoutePartPoint.PartID, wErrorCode);
					if (!wIsMustAdd) {
						continue;
					}
					// ②禁用工序计划
					FPCCommonFileDAO.getInstance().DisableTaskStep(wLoginUser, wFPCRoutePartPoint, wFPCRoute, wOrderID,
							wErrorCode);
					// ③禁用自检单
					// ④禁用互检单
					// ⑤禁用专检单
					// ⑥禁用预检单
					FPCCommonFileDAO.getInstance().DisableTaskIPT(wLoginUser, wFPCRoutePartPoint, wFPCRoute, wOrderID,
							wErrorCode);
					// ⑦禁用派工单
					FPCCommonFileDAO.getInstance().DisableSFCTaskStep(wLoginUser, wFPCRoutePartPoint, wFPCRoute,
							wOrderID, wErrorCode);
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 新增工序
	 */
	private void AddPartPoint01(BMSEmployee wLoginUser, List<Integer> wOrderIDList, List<FPCRoutePartPoint> wAddedList,
			int wRouteID, FPCRoute wFPCRoute, List<Integer> wReworkOrdreIDList) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wAddedList == null || wAddedList.size() <= 0) {
				return;
			}

			// ③新增日计划
			Calendar wBaseTime = Calendar.getInstance();
			wBaseTime.set(2000, 0, 1, 0, 0, 0);
			int wShiftID = MESServer.MES_QueryShiftID(wLoginUser, 0, Calendar.getInstance(), APSShiftPeriod.Day,
					FMCShiftLevel.Day);
			for (Integer wOrderID : wOrderIDList) {
				for (FPCRoutePartPoint wFPCRoutePartPoint : wAddedList) {
					// ①判断是否需要新增日计划
					boolean wIsMustAdd = FPCCommonFileDAO.getInstance().JugdeIsNeedAddDayPlan(wLoginUser, wOrderID,
							wFPCRoutePartPoint.PartID, wErrorCode);
					if (!wIsMustAdd) {
						continue;
					}
					// ②新增日计划
					APSTaskPart wAPSTaskPart = LOCOAPSServiceImpl.getInstance()
							.APS_QueryTaskPartByID(wLoginUser, wErrorCode.Result).Info(APSTaskPart.class);

					APSTaskStep wAPSTaskStep = new APSTaskStep();

					wAPSTaskStep.Active = 1;
					wAPSTaskStep.ID = 0;
					wAPSTaskStep.LineID = wAPSTaskPart.LineID;
					wAPSTaskStep.MaterialNo = wAPSTaskPart.MaterialNo;
					wAPSTaskStep.OrderID = wAPSTaskPart.OrderID;
					wAPSTaskStep.PartID = wAPSTaskPart.PartID;
					wAPSTaskStep.PartNo = wAPSTaskPart.PartNo;
					wAPSTaskStep.PlanerID = wLoginUser.ID;
					wAPSTaskStep.ProductNo = wAPSTaskPart.ProductNo;
					wAPSTaskStep.ShiftID = wShiftID;
					wAPSTaskStep.Status = APSTaskStatus.Issued.getValue();
					wAPSTaskStep.StepID = wFPCRoutePartPoint.PartPointID;
					wAPSTaskStep.TaskPartID = wAPSTaskPart.ID;
					wAPSTaskStep.TaskLineID = wAPSTaskPart.TaskLineID;
					wAPSTaskStep.TaskText = wAPSTaskPart.TaskText;
					wAPSTaskStep.WorkHour = 0;
					wAPSTaskStep.WorkShopID = wAPSTaskPart.WorkShopID;
					wAPSTaskStep.StartTime = wBaseTime;
					wAPSTaskStep.EndTime = wBaseTime;
					wAPSTaskStep.ReadyTime = wBaseTime;
					wAPSTaskStep.CreateTime = Calendar.getInstance();
					wAPSTaskStep.PlanStartTime = wAPSTaskPart.StartTime;

					LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wAPSTaskStep);
				}
			}

			// ①处理返工工序
			for (Integer wOrderID : wReworkOrdreIDList) {
				for (FPCRoutePartPoint wFPCRoutePartPoint : wAddedList) {
					// ①判断是否已转序
					if (FPCCommonFileDAO.getInstance().JugdeIsTurnOrder(wLoginUser, wOrderID, wFPCRoutePartPoint.PartID,
							wErrorCode)) {
						// ①创建添加返工任务
						CreateReworkTask(BaseDAO.SysAdmin, wOrderID, wFPCRoutePartPoint);

						continue;
					}
				}
			}

			// ②新增产线单元明细
			for (FPCRoutePartPoint wFPCRoutePartPoint : wAddedList) {
				// ①判断是否存在，若不存在，添加
				boolean wCheckResult = FPCCommonFileDAO.getInstance().IsExistLineUnitStep(wLoginUser, wFPCRoute.LineID,
						wFPCRoute.ProductID, wFPCRoutePartPoint.PartID, wFPCRoutePartPoint.PartPointID, wErrorCode);
				if (wCheckResult) {
					continue;
				}

				// ②新增工序产线单元明细
				FMCLineUnit wFMCLineUnit = new FMCLineUnit();
				wFMCLineUnit.Active = 1;
				wFMCLineUnit.LineID = wFPCRoute.LineID;
				wFMCLineUnit.UnitID = wFPCRoutePartPoint.PartPointID;
				wFMCLineUnit.OrderID = 1;
				wFMCLineUnit.LevelID = 3;
				wFMCLineUnit.ProductID = wFPCRoute.ProductID;
				wFMCLineUnit.CreateTime = Calendar.getInstance();
				wFMCLineUnit.Status = 1;
				wFMCLineUnit.ParentUnitID = wFPCRoutePartPoint.PartID;
				FMCServiceImpl.getInstance().FMC_SaveLineUnit(BaseDAO.SysAdmin, wFMCLineUnit);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	/**
	 * 创建工艺变更-返工任务
	 */
	private void CreateReworkTask(BMSEmployee wLoginUser, Integer wOrderID, FPCRoutePartPoint wFPCRoutePartPoint) {
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			// ①获取工位的班组长ID集合
			List<Integer> wUserIDList = FPCRouteDAO.getInstance().GetMonitorIDList(wLoginUser,
					wFPCRoutePartPoint.PartID, wErrorCode);
			if (wUserIDList.size() <= 0) {
				return;
			}
			// ②获取订单
			OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
					.Info(OMSOrder.class);
			if (wOrder == null || wOrder.ID <= 0) {
				return;
			}
			// ③创建任务
			APIResult wAPiResult = CoreServiceImpl.getInstance().QMS_StartInstance(wLoginUser, "8209");
			List<BPMActivitiTask> wBPMActivitiTask = wAPiResult.List(BPMActivitiTask.class);
			TCMRework wData = wAPiResult.Custom("data", TCMRework.class);

			wData.Content = StringUtils.Format("工艺内容发生变更，【{0}】工位新增工序【{1}】，系统自动发起工艺变更返工。",
					QMSConstants.GetFPCPartName(wFPCRoutePartPoint.PartID),
					QMSConstants.GetFPCStepName(wFPCRoutePartPoint.PartPointID));
			wData.LineID = wOrder.LineID;
			wData.LineName = wOrder.LineName;
			wData.ProductID = wOrder.ProductID;
			wData.ProductNo = wOrder.ProductNo;
			wData.PartNo = wOrder.PartNo;
			wData.CustomerID = wOrder.CustomerID;
			wData.Customer = wOrder.Customer;
			wData.PartID = wFPCRoutePartPoint.PartID;
			wData.PartName = QMSConstants.GetFPCPartName(wFPCRoutePartPoint.PartID);
			wData.StepID = wFPCRoutePartPoint.PartPointID;
			wData.StepName = QMSConstants.GetFPCStepName(wFPCRoutePartPoint.PartPointID);
			wData.Status = 1;
			wData.MonitorList = StringUtils.Join(",", wUserIDList);

			// ①辅助属性赋值
			wData.Content_txt_ = wData.Content;
			wData.LineName_txt_ = wData.LineName;
			wData.ProductNo_txt_ = wData.ProductNo;
			wData.PartNo_txt_ = wData.PartNo;
			wData.Customer_txt_ = wData.Customer;
			wData.PartName_txt_ = wData.PartName;
			wData.StepName_txt_ = wData.StepName;

			CoreServiceImpl.getInstance().QMS_CompleteInstance(wLoginUser, wData, wBPMActivitiTask.get(0).ID);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<FPCPartPoint>> FPC_QueryStepListByPartType(BMSEmployee wLoginUser, int wPartType) {
		ServiceResult<List<FPCPartPoint>> wResult = new ServiceResult<List<FPCPartPoint>>();
		wResult.Result = new ArrayList<FPCPartPoint>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = SFCTaskIPTDAO.getInstance().GetStepListByPartType(wLoginUser, wPartType, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSMaterial>> MSS_QueryPageAll(BMSEmployee wLoginUser, int wPageSize, int wCurPage,
			String wMaterialNo, String wMaterialName) {
		ServiceResult<List<MSSMaterial>> wResult = new ServiceResult<List<MSSMaterial>>();
		wResult.Result = new ArrayList<MSSMaterial>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = SFCTaskIPTDAO.getInstance().MSS_QueryPageAll(wLoginUser, wPageSize, wCurPage,
					wMaterialName, wMaterialNo, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MSS_QueryRecordSize(BMSEmployee wLoginUser, String wMaterialNo,
			String wMaterialName) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = SFCTaskIPTDAO.getInstance().MSS_QueryPageNumber(wLoginUser, wMaterialNo, wMaterialName,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MBS_QueryRecordSize(BMSEmployee wLoginUser, int wLoginID, String wProjectName,
			String wURI) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = MBSApiLogDAO.getInstance().SelectPageNumber(wLoginUser, -1, wLoginID, wProjectName, wURI,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MBSApiLog>> MBS_QueryPageAll(BMSEmployee wLoginUser, int wPageSize, int wCurPage,
			int wLoginID, String wProjectName, String wURI) {
		ServiceResult<List<MBSApiLog>> wResult = new ServiceResult<List<MBSApiLog>>();
		wResult.Result = new ArrayList<MBSApiLog>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = MBSApiLogDAO.getInstance().SelectPageAll(wLoginUser, wPageSize, wCurPage, -1, wLoginID,
					wProjectName, wURI, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> FPC_BatchUpdateChangeControl(BMSEmployee wLoginUser, int wRouteID, int wPartID,
			int wChangeControl) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①根据RouteID查询所有，同车型、修程、局段的RouteID集合
			List<Integer> wRouteIDList = FPCRouteDAO.getInstance().FPC_QueryRouteIDList(wLoginUser, wRouteID,
					wErrorCode);
			// ②根据RouteID集合、工位查找所有工艺工位数据ID集合
			List<Integer> wRoutePartIDList = FPCRouteDAO.getInstance().FPC_QueryRoutePartIDList(wLoginUser,
					wRouteIDList, wPartID, wErrorCode);

			// 根据RoutePartIDList查询转序控制字典
			Map<Integer, Integer> wControlMap = FPCRouteDAO.getInstance().FPC_QueryControlMap(wLoginUser,
					wRoutePartIDList, wErrorCode);

			// ③根据工艺工位数据ID集合、转序控制值修改所有工艺工位数据
			FPCRouteDAO.getInstance().FPC_BatchUpdateChangeControl(wLoginUser, wRoutePartIDList, wChangeControl,
					wErrorCode);

			// 记录操作日志
			RecordOperationLog(wLoginUser, wPartID, wRoutePartIDList, wChangeControl, wControlMap, wErrorCode);

			// 禁用不做的计划
			if (wChangeControl == 3) {
				// ①找到用到此RouteID的3,4的订单ID集合
				List<Integer> wOrderIDList = FPCRouteDAO.getInstance().SelectOrderIDListByRouteID(wLoginUser, wRouteID,
						wErrorCode);
				// ②遍历订单
				for (Integer wOrderID : wOrderIDList) {
					// ③根据订单ID和工位ID禁用工位计划
					FPCRouteDAO.getInstance().DisableTaskPart(wLoginUser, wOrderID, wPartID, wErrorCode);
					// ④根据订单ID和工位ID禁用工序计划
					FPCRouteDAO.getInstance().DisableTaskPartPoint(wLoginUser, wOrderID, wPartID, wErrorCode);
				}
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private void RecordOperationLog(BMSEmployee wLoginUser, int wPartID, List<Integer> wRoutePartIDList,
			int wChangeControl, Map<Integer, Integer> wControlMap, OutResult<Integer> wErrorCode) {
		try {
			for (int wRoutePartID : wRoutePartIDList) {
				if (!wControlMap.containsKey(wRoutePartID)) {
					continue;
				}

				int wOldControl = wControlMap.get(wRoutePartID);
				if (wOldControl == wChangeControl) {
					continue;
				}

				SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String wCurrentTime = wSDF.format(Calendar.getInstance().getTime());
				String wPartName = QMSConstants.GetFPCPartName(wPartID);

				String wContent = StringUtils.Format("【{0}】在【{1}】将【{2}】的转序控制修改为【{3}】", wLoginUser.Name, wCurrentTime,
						wPartName, FPCChangeControlType.getEnumType(wChangeControl).getLable());

				LFSOperationLog wLFSOperationLog = new LFSOperationLog(0, wRoutePartID,
						LFSOperationLogType.UpdateTurnOrderControl.getValue(),
						LFSOperationLogType.UpdateTurnOrderControl.getLable(), wLoginUser.ID, wLoginUser.Name,
						Calendar.getInstance(), wContent);
				LFSServiceImpl.getInstance().LFS_UpdateOperationLog(wLoginUser, wLFSOperationLog);
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<List<FPCPartPoint>> FMC_QueryLineUnitStepList(BMSEmployee wLoginUser, int wProductID,
			int wLineID, int wPartID) {
		ServiceResult<List<FPCPartPoint>> wResult = new ServiceResult<List<FPCPartPoint>>();
		wResult.Result = new ArrayList<FPCPartPoint>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = FPCRouteDAO.getInstance().FMC_QueryLineUnitStepList(wLoginUser, wProductID, wLineID,
					wPartID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSBOMItem>> MSS_QueryBomItemList(BMSEmployee wLoginUser, int wOrderID, int wPartID,
			int wStepID) {
		ServiceResult<List<MSSBOMItem>> wResult = new ServiceResult<List<MSSBOMItem>>();
		wResult.Result = new ArrayList<MSSBOMItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			if (wOrderID <= 0 || wPartID <= 0 || wStepID <= 0) {
				wResult.FaultCode += "提示：输入的参数不允许小于或等于0!";
				return wResult;
			}

			// ①查询BOMID
			int wBOMID = MSSBOMItemDAO.getInstance().QueryBOMID(wLoginUser, wOrderID, wErrorCode);
			if (wBOMID <= 0) {
				return wResult;
			}

			// ②查询BOM子项列表
			wResult.Result = MSSBOMItemDAO.getInstance().SelectList(wLoginUser, wBOMID, wStepID, wPartID, wErrorCode);

			// 添加历史记录
//			if (wOrderID > 0 && wPartID > 0 && wStepID > 0) {
//				OMSOrder wOrder = LOCOAPSServiceImpl.getInstance().OMS_QueryOrderByID(wLoginUser, wOrderID)
//						.Info(OMSOrder.class);
//
//				List<MSSBOMItemHistory> wList = MSSBOMItemHistoryDAO.getInstance().SelectList(wLoginUser, -1,
//						wOrder.ProductID, wOrder.LineID, wOrder.CustomerID, wPartID, wStepID, -1, wErrorCode);
//
//				List<MSSBOMItem> wHisList = CloneTool.CloneArray(wList, MSSBOMItem.class);
//				for (MSSBOMItem wMSSBOMItem : wHisList) {
//					if (wResult.Result.stream()
//							.anyMatch(p -> p.CustomerID == wMSSBOMItem.CustomerID && p.PlaceID == wMSSBOMItem.PlaceID
//									&& p.PartPointID == wMSSBOMItem.PartPointID
//									&& p.MaterialID == wMSSBOMItem.MaterialID)) {
//						continue;
//					}
//
//					wMSSBOMItem.BOMID = 0;
//					wMSSBOMItem.ID = 0;
//					wResult.Result.add(wMSSBOMItem);
//				}
//			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FPCFlowPart>> FPC_QueryFlowDataPart(BMSEmployee wLoginUser, int wRouteID) {
		ServiceResult<List<FPCFlowPart>> wResult = new ServiceResult<List<FPCFlowPart>>();
		wResult.Result = new ArrayList<FPCFlowPart>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①获取工艺工位
			List<FPCRoutePart> wRoutePartList = FMCServiceImpl.getInstance()
					.FPC_QueryRoutePartListByRouteID(wLoginUser, wRouteID).List(FPCRoutePart.class);

			// ①获取车体工位集合
			List<Integer> wBodyPartList = FPCRouteDAO.getInstance().FPC_QueryBodyPartList(wLoginUser, 1, wErrorCode);

			// ②获取工位节点集合
			wResult.Result = GetFlowPartList(wRoutePartList, wBodyPartList);
			// ③获取线集合
			List<FPCFlowLine> wLineList = GetFlowLineList(wRoutePartList, wResult.Result, wBodyPartList);
			wResult.CustomResult.put("LineList", wLineList);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private List<FPCFlowLine> GetFlowLineList(List<FPCRoutePart> wRoutePartList, List<FPCFlowPart> wFlowPartList,
			List<Integer> wBodyPartList) {
		List<FPCFlowLine> wResult = new ArrayList<FPCFlowLine>();
		try {
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				return wResult;
			}

			for (FPCRoutePart wFPCRoutePart : wRoutePartList) {
				// ①查询所有后节点
				List<FPCRoutePart> wNextList = wRoutePartList.stream()
						.filter(p -> p.PrevPartID == wFPCRoutePart.PartID
								|| wFPCRoutePart.NextPartIDMap.containsKey(String.valueOf(p.PartID)))
						.collect(Collectors.toList());
				int wRow1 = GetRow(wFPCRoutePart, wRoutePartList, wBodyPartList);

				for (FPCRoutePart wNextNode : wNextList) {
					int wRow2 = GetRow(wNextNode, wRoutePartList, wBodyPartList);

					FPCFlowLine wFPCFlowLine = new FPCFlowLine();

					wFPCFlowLine.anode = new FPCFlowPoint();
					wFPCFlowLine.anode.id = String.valueOf(wFPCRoutePart.PartID);
					wFPCFlowLine.anode.anchor = "Right";
					wFPCFlowLine.anode.uuid = wFPCFlowLine.anode.id + "_r";

					wFPCFlowLine.bnode = new FPCFlowPoint();
					wFPCFlowLine.bnode.id = String.valueOf(wNextNode.PartID);
					wFPCFlowLine.bnode.anchor = "Left";
					wFPCFlowLine.bnode.uuid = wFPCFlowLine.bnode.id + "_l";

					// ①a节点和b节点在同一层，列相隔大于1，连上节点
					if (wRow1 == wRow2 && wNextNode.OrderID - wFPCRoutePart.OrderID > 1) {
						wFPCFlowLine.anode.anchor = "Top";
						wFPCFlowLine.anode.uuid = wFPCFlowLine.anode.id + "_t";

						wFPCFlowLine.bnode.anchor = "Top";
						wFPCFlowLine.bnode.uuid = wFPCFlowLine.bnode.id + "_t";
					}
					// ②a节点层数大于b节点层数，且不存在和A在同一层，且列小于B的工位
					else if (wRow1 > wRow2 && !wFlowPartList.stream().anyMatch(p -> p.row == wRow1
							&& p.col < wNextNode.OrderID && !p.id.equals(String.valueOf(wFPCRoutePart.PartID)))) {
						wFPCFlowLine.anode.anchor = "Right";
						wFPCFlowLine.anode.uuid = wFPCFlowLine.anode.id + "_r";

						wFPCFlowLine.bnode.anchor = "Bottom";
						wFPCFlowLine.bnode.uuid = wFPCFlowLine.bnode.id + "_b";
					}

					wResult.add(wFPCFlowLine);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	private List<FPCFlowPart> GetFlowPartList(List<FPCRoutePart> wRoutePartList, List<Integer> wBodyPartList) {
		List<FPCFlowPart> wResult = new ArrayList<FPCFlowPart>();
		try {
			if (wRoutePartList == null || wRoutePartList.size() <= 0) {
				return wResult;
			}

			for (FPCRoutePart wFPCRoutePart : wRoutePartList) {
				FPCFlowPart wItem = new FPCFlowPart();

				// ⑥row
				wItem.row = GetRow(wFPCRoutePart, wRoutePartList, wBodyPartList);
				// ⑦col
				wItem.col = wFPCRoutePart.OrderID;
				// ①id
				wItem.id = String.valueOf(wFPCRoutePart.PartID);
				// ②name
				wItem.name = QMSConstants.GetFPCPartName(wFPCRoutePart.PartID);
				// ③left
				wItem.left = String.valueOf((wItem.col - 1) * 180 + 20);
				// ④top
				wItem.top = String.valueOf((wItem.row - 1) * 75 + 35);
				// ⑤showclass
				wItem.showclass = wFPCRoutePart.ChangeControl == 2 ? "mytipshow" : "mytiphide";

				wResult.add(wItem);
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	private int GetRow(FPCRoutePart wItem, List<FPCRoutePart> wRoutePartList, List<Integer> wBodyPartList) {
		int wResult = 0;
		try {
			List<FPCRoutePart> wList = wRoutePartList.stream().filter(p -> p.OrderID == wItem.OrderID)
					.collect(Collectors.toList());

			for (FPCRoutePart wFPCRoutePart : wList) {
				if (wBodyPartList.stream().anyMatch(p -> p == wFPCRoutePart.PartID)) {
					wFPCRoutePart.OrderNumber = 1;
				} else {
					wFPCRoutePart.OrderNumber = 0;
				}
			}

			wList.sort(Comparator.comparing(FPCRoutePart::getOrderNumber, Comparator.reverseOrder()));

			for (int i = 0; i < wList.size(); i++) {
				if (wList.get(i).PartID == wItem.PartID) {
					wResult = i + 1;
					return wResult;
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<BFCMessage> BFC_QueryMessageInfo(BMSEmployee wLoginUser, Integer wMessageID) {
		ServiceResult<BFCMessage> wResult = new ServiceResult<BFCMessage>();
		wResult.Result = new BFCMessage();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = BFCMessageDAO.getInstance().SelectByID(wLoginUser, wMessageID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<FPCRoutePart>> FPC_QeuryRoutePartList(BMSEmployee wLoginUser, int wRouteID) {
		ServiceResult<List<FPCRoutePart>> wResult = new ServiceResult<List<FPCRoutePart>>();
		wResult.Result = new ArrayList<FPCRoutePart>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①获取工艺工位集合
			wResult.Result = FMCServiceImpl.getInstance().FPC_QueryRoutePartListByRouteID(wLoginUser, wRouteID)
					.List(FPCRoutePart.class);
			// ②获取车体工位集合
			List<Integer> wBodyPartList = FPCRouteDAO.getInstance().FPC_QueryBodyPartList(wLoginUser, 1, wErrorCode);
			List<Integer> wTransPartList = FPCRouteDAO.getInstance().FPC_QueryBodyPartList(wLoginUser, 2, wErrorCode);
			// ③遍历工艺工位，给车体工位加顺序
			for (FPCRoutePart wFPCRoutePart : wResult.Result) {
				// 找到该工位的前工位
				List<FPCRoutePart> wPreList = wResult.Result.stream()
						.filter(p -> p.NextPartIDMap != null && p.NextPartIDMap.size() > 0
								&& p.NextPartIDMap.containsKey(String.valueOf(wFPCRoutePart.PartID)))
						.collect(Collectors.toList());
				if (wBodyPartList.stream().anyMatch(p -> p == wFPCRoutePart.PartID)) {
					wFPCRoutePart.OrderNumber = 999;
					// ④若车体的直接前工位无值，赋上值
					if (wFPCRoutePart.PrevPartID <= 0) {
						if (wPreList != null && wPreList.size() > 0) {
							wFPCRoutePart.PrevPartID = wPreList.get(0).PartID;
						}
					}
					// ⑤若前工艺集有多个，且有车体和转向架，则直接前工艺集改为车体
					if (wFPCRoutePart.PrevPartID > 0 && wPreList.size() > 0
							&& !wBodyPartList.stream().anyMatch(p -> p == wFPCRoutePart.PrevPartID)
							&& wPreList.stream().anyMatch(p -> wBodyPartList.stream().anyMatch(q -> q == p.PartID))) {
						wFPCRoutePart.PrevPartID = wPreList.stream()
								.filter(p -> wBodyPartList.stream().anyMatch(q -> q == p.PartID)).findFirst()
								.get().PartID;
					}
					// ⑥若前工艺集有多个，且都是车体，则直接前工艺集改为OrderID最大的车体
					if (wFPCRoutePart.PrevPartID > 0 && wPreList.size() > 1
							&& wBodyPartList.stream().anyMatch(p -> p == wFPCRoutePart.PrevPartID)) {
						List<FPCRoutePart> wCloneList = CloneTool.CloneArray(wPreList, FPCRoutePart.class);
						FPCRoutePart wItem = wResult.Result.stream().filter(p -> p.PartID == wFPCRoutePart.PrevPartID)
								.findFirst().get();
						wCloneList.add(wItem);
						wCloneList = wCloneList.stream()
								.filter(p -> wBodyPartList.stream().anyMatch(q -> q == p.PartID))
								.collect(Collectors.toList());
						wFPCRoutePart.PrevPartID = wCloneList.stream()
								.max(Comparator.comparing(FPCRoutePart::getOrderID)).get().PartID;
					}
				} else {
					if (wFPCRoutePart.PrevPartID <= 0 && wPreList.size() > 0) {
						if (wPreList.stream().anyMatch(p -> wBodyPartList.stream().anyMatch(q -> q == p.PartID))) {
							wFPCRoutePart.PrevPartID = wPreList.stream()
									.filter(p -> wBodyPartList.stream().anyMatch(q -> q == p.PartID)).findFirst()
									.get().PartID;
						} else if (wPreList.stream()
								.anyMatch(p -> wTransPartList.stream().anyMatch(q -> q == p.PartID))) {
							wFPCRoutePart.PrevPartID = wPreList.stream()
									.filter(p -> wTransPartList.stream().anyMatch(q -> q == p.PartID)).findFirst()
									.get().PartID;
						}
					}
				}
			}
			// ⑤按照OrderID升序排序，车体顺序降序排序
			wResult.Result.sort(Comparator.comparing(FPCRoutePart::getOrderID)
					.thenComparing(FPCRoutePart::getOrderNumber, Comparator.reverseOrder()));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSEmployee>> BMS_QueryUserSonList(BMSEmployee wLoginUser, int wDepartmentID) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<BMSEmployee> wUserList = QMSConstants.GetBMSEmployeeList().values().stream()
					.collect(Collectors.toList());
			List<BMSDepartment> wDepartmentList = QMSConstants.GetBMSDepartmentList().values().stream()
					.collect(Collectors.toList());
			// ①获取所有子部门ID集合
			List<Integer> wSonDepIDList = GetSonDepIDList(wDepartmentID, wDepartmentList);
			wSonDepIDList.add(wDepartmentID);
			// ②遍历获取部门下的用户
			for (Integer wDeID : wSonDepIDList) {
				wResult.Result.addAll(wUserList.stream().filter(p -> p.DepartmentID == wDeID && p.Active == 1)
						.collect(Collectors.toList()));
			}
			// ③去重
			wResult.Result = new ArrayList<BMSEmployee>(wResult.Result.stream()
					.collect(Collectors.toMap(BMSEmployee::getID, account -> account, (k1, k2) -> k2)).values());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private List<Integer> GetSonDepIDList(int wDepartmentID, List<BMSDepartment> wDepartmentList) {
		List<Integer> wResult = new ArrayList<Integer>();
		try {
			// ①找到子项部门集合
			List<BMSDepartment> wSonList = wDepartmentList.stream().filter(p -> p.ParentID == wDepartmentID)
					.collect(Collectors.toList());
			if (wSonList.size() > 0) {
				// ②添加进结果集
				wResult.addAll(wSonList.stream().map(p -> p.ID).collect(Collectors.toList()));
				// ③遍历子项，递归添加子集合
				for (BMSDepartment wDep : wSonList) {
					wResult.addAll(GetSonDepIDList(wDep.ID, wDepartmentList));
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MSS_PartInstock(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, IPTItem wItem,
			IPTValue wValue, Integer wOperateType) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			MSSPartRecord wRecord;
			switch (MSSOperateType.getEnumType(wOperateType)) {
			case ChaiJie:
				wRecord = new MSSPartRecord(0, wTaskIPT.ProductID, wTaskIPT.ProductNo, wTaskIPT.LineID,
						wTaskIPT.LineName, wTaskIPT.CustomerID, wTaskIPT.CustomerName, wTaskIPT.StationID,
						wTaskIPT.StationName, wTaskIPT.PartPointID, wTaskIPT.PartPointName, (int) wItem.ID, wItem.Text,
						wItem.DisassemblyComponents, wValue.DisassemblyComponents, wTaskIPT.OrderID, wTaskIPT.PartNo, 0,
						"", MSSOperateType.ChaiJie.getValue(), MSSOperateType.ChaiJie.getLable(), wLoginUser.ID,
						wLoginUser.Name, Calendar.getInstance());
				MSSPartRecordDAO.getInstance().Update(wLoginUser, wRecord, wErrorCode);
				break;
			case WeiXiu:
				wRecord = new MSSPartRecord(0, wTaskIPT.ProductID, wTaskIPT.ProductNo, wTaskIPT.LineID,
						wTaskIPT.LineName, wTaskIPT.CustomerID, wTaskIPT.CustomerName, wTaskIPT.StationID,
						wTaskIPT.StationName, wTaskIPT.PartPointID, wTaskIPT.PartPointName, (int) wItem.ID, wItem.Text,
						wItem.RepairParts, wValue.RepairParts, wTaskIPT.OrderID, wTaskIPT.PartNo, 0, "",
						MSSOperateType.WeiXiu.getValue(), MSSOperateType.WeiXiu.getLable(), wLoginUser.ID,
						wLoginUser.Name, Calendar.getInstance());
				MSSPartRecordDAO.getInstance().Update(wLoginUser, wRecord, wErrorCode);
				break;
			case Zuzhuang:
				// ①通过车型、修程、局段部件编号，部件序列号查询拆解、维修记录
				List<MSSPartRecord> wList = MSSPartRecordDAO.getInstance().SelectList(wLoginUser, -1,
						wTaskIPT.ProductID, wTaskIPT.LineID, -1, wItem.AssemblyParts, wValue.AssemblyParts, -1, -1, -1,
						-1, wErrorCode);
				// ②修改目标车
				int wOrderID = 0;
				String wPartNo = "";
				for (MSSPartRecord wMSSPartRecord : wList) {
					wOrderID = wMSSPartRecord.OrderID;
					wPartNo = wMSSPartRecord.PartNo;
					wMSSPartRecord.TargetPartNo = wTaskIPT.PartNo;
					wMSSPartRecord.TartgetOrderID = wTaskIPT.OrderID;
					MSSPartRecordDAO.getInstance().Update(wLoginUser, wMSSPartRecord, wErrorCode);
				}
				// ③保存组装记录
				wRecord = new MSSPartRecord(0, wTaskIPT.ProductID, wTaskIPT.ProductNo, wTaskIPT.LineID,
						wTaskIPT.LineName, wTaskIPT.CustomerID, wTaskIPT.CustomerName, wTaskIPT.StationID,
						wTaskIPT.StationName, wTaskIPT.PartPointID, wTaskIPT.PartPointName, (int) wItem.ID, wItem.Text,
						wItem.AssemblyParts, wValue.AssemblyParts, wOrderID, wPartNo, wTaskIPT.OrderID, wTaskIPT.PartNo,
						MSSOperateType.Zuzhuang.getValue(), MSSOperateType.Zuzhuang.getLable(), wLoginUser.ID,
						wLoginUser.Name, Calendar.getInstance());
				MSSPartRecordDAO.getInstance().Update(wLoginUser, wRecord, wErrorCode);
				break;
			default:
				break;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSPartItem>> MSS_QueryRepairList(BMSEmployee wLoginUser, int wOrderID,
			String wPartsCode) {
		ServiceResult<List<MSSPartItem>> wResult = new ServiceResult<List<MSSPartItem>>();
		wResult.Result = new ArrayList<MSSPartItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①根据订单查询部件记录集合
			List<MSSPartRecord> wList = MSSPartRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1,
					wPartsCode, "", wOrderID, -1, -1, -1, wErrorCode);
			// ②获取部件列表
			// 多条件
			List<MSSPartRecord> wNRList = wList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
					() -> new TreeSet<>(Comparator.comparing(o -> o.getMSSPartCode() + ";" + o.getMSSPartSerial()))),
					ArrayList::new));
			// ③判断部件是否只有拆解记录
			for (MSSPartRecord wMSSPartRecord : wNRList) {
				if (wList.stream()
						.anyMatch(p -> p.MSSPartCode.equals(wMSSPartRecord.MSSPartCode)
								&& p.MSSPartSerial.equals(wMSSPartRecord.MSSPartSerial)
								&& (p.OperateType == MSSOperateType.WeiXiu.getValue()
										|| p.OperateType == MSSOperateType.Zuzhuang.getValue()))) {
					continue;
				}

				MSSPartItem wItem = new MSSPartItem();

				wItem.ID = wMSSPartRecord.ID;
				wItem.Code = wMSSPartRecord.MSSPartCode;
				wItem.SupplierPartNo = wMSSPartRecord.MSSPartSerial;

				wResult.Result.add(wItem);
			}
			if (wResult.Result.size() > 0) {
				// ④获取编码，名称字典
				Map<String, String> wPartCodeMap = MSSPartRecordDAO.getInstance().GetPartCodeMap(wLoginUser,
						wErrorCode);
				// ⑤翻译数据，返回结果
				for (MSSPartItem wMSSPartItem : wResult.Result) {
					if (wPartCodeMap.containsKey(wMSSPartItem.Code)) {
						wMSSPartItem.Name = wPartCodeMap.get(wMSSPartItem.Code);
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
	public ServiceResult<List<MSSPartItem>> MSS_QueryAssembleList(BMSEmployee wLoginUser, int wProductID, int wLineID,
			String wPartsCode) {
		ServiceResult<List<MSSPartItem>> wResult = new ServiceResult<List<MSSPartItem>>();
		wResult.Result = new ArrayList<MSSPartItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<MSSPartRecord> wList = MSSPartRecordDAO.getInstance().SelectList(wLoginUser, -1, wProductID, wLineID,
					-1, wPartsCode, "", -1, -1, MSSOperateType.WeiXiu.getValue(), -1, wErrorCode);
			// 多条件
			wList = wList.stream().collect(Collectors.collectingAndThen(
					Collectors.toCollection(() -> new TreeSet<>(Comparator
							.comparing(o -> o.getOrderID() + ";" + o.getMSSPartCode() + ";" + o.getMSSPartSerial()))),
					ArrayList::new));
			for (MSSPartRecord wMSSPartRecord : wList) {
				// ①判断此部件是否已组装
				List<MSSPartRecord> wFlagList = MSSPartRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1,
						wMSSPartRecord.MSSPartCode, wMSSPartRecord.MSSPartSerial, wMSSPartRecord.OrderID, -1,
						MSSOperateType.Zuzhuang.getValue(), -1, wErrorCode);
				// ②若没组装，添加进结果集
				if (wFlagList.size() > 0) {
					continue;
				}
				// ③添加
				MSSPartItem wItem = new MSSPartItem();

				wItem.ID = wMSSPartRecord.ID;
				wItem.Code = wMSSPartRecord.MSSPartCode;
				wItem.SupplierPartNo = wMSSPartRecord.MSSPartSerial;

				wResult.Result.add(wItem);
			}

			if (wResult.Result.size() > 0) {
				// ④获取编码，名称字典
				Map<String, String> wPartCodeMap = MSSPartRecordDAO.getInstance().GetPartCodeMap(wLoginUser,
						wErrorCode);
				// ⑤翻译数据，返回结果
				for (MSSPartItem wMSSPartItem : wResult.Result) {
					if (wPartCodeMap.containsKey(wMSSPartItem.Code)) {
						wMSSPartItem.Name = wPartCodeMap.get(wMSSPartItem.Code);
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
	public ServiceResult<List<MSSPartRecord>> MSS_QueryAllPartRecord(BMSEmployee wLoginUser, int wOrderID,
			int wOperateType) {
		ServiceResult<List<MSSPartRecord>> wResult = new ServiceResult<List<MSSPartRecord>>();
		wResult.Result = new ArrayList<MSSPartRecord>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result.addAll(MSSPartRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, "", "",
					wOrderID, -1, wOperateType, -1, wErrorCode));
			wResult.Result.addAll(MSSPartRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, "", "", -1,
					wOrderID, wOperateType, -1, wErrorCode));

			// 排序，按照部件、操作时间排序
			if (wResult.Result.size() > 0) {
				// 排序
				wResult.Result.sort(Comparator.comparing(MSSPartRecord::getMSSPartCode)
						.thenComparing(MSSPartRecord::getMSSPartSerial).thenComparing(MSSPartRecord::getCreateTime));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MSS_SRPartInstock(BMSEmployee wLoginUser, SFCTaskIPT wTaskIPT, IPTItem wItem,
			IPTValue wValue, Integer wOperateType) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			MSSRepairRecord wRecord;
			String[] wStrs;
			int wFQTY = 0;
			int wFQTYScrap = 0;
			int wFQTYLY = 0;
			switch (MSSOperateType.getEnumType(wOperateType)) {
			case ChaiJie:
				wStrs = wValue.SRDisassemblyComponents.split(",");
				wFQTY = wStrs.length;
				wStrs = wValue.SRScrapParts.split(",");
				if (StringUtils.isNotEmpty(wValue.SRScrapParts))
					wFQTYScrap = wStrs.length;
				wStrs = wValue.SRLYParts.split(",");
				if (StringUtils.isNotEmpty(wValue.SRLYParts))
					wFQTYLY = wStrs.length;
				wRecord = new MSSRepairRecord(0, wTaskIPT.ProductID, wTaskIPT.ProductNo, wTaskIPT.LineID,
						wTaskIPT.LineName, wTaskIPT.CustomerID, wTaskIPT.CustomerName, wTaskIPT.StationID,
						wTaskIPT.StationName, wTaskIPT.PartPointID, wTaskIPT.PartPointName, (int) wItem.ID, wItem.Text,
						wItem.SRDisassemblyComponents, wValue.SRDisassemblyComponents, wValue.SRScrapParts,
						wValue.SRLYParts, wFQTY, wFQTYScrap, wFQTYLY, wTaskIPT.OrderID, wTaskIPT.PartNo, 0, "",
						MSSOperateType.ChaiJie.getValue(), MSSOperateType.ChaiJie.getLable(), wLoginUser.ID,
						wLoginUser.Name, Calendar.getInstance(), wValue.MaterialID, wValue.MaterialNo,
						wValue.MaterialName);
				MSSRepairRecordDAO.getInstance().Update(wLoginUser, wRecord, wErrorCode);
				break;
			case WeiXiu:
				wStrs = wValue.SRRepairParts.split(",");
				wFQTY = wStrs.length;
				wStrs = wValue.SRScrapParts.split(",");
				if (StringUtils.isNotEmpty(wValue.SRScrapParts))
					wFQTYScrap = wStrs.length;
				wStrs = wValue.SRLYParts.split(",");
				if (StringUtils.isNotEmpty(wValue.SRLYParts))
					wFQTYLY = wStrs.length;
				wRecord = new MSSRepairRecord(0, wTaskIPT.ProductID, wTaskIPT.ProductNo, wTaskIPT.LineID,
						wTaskIPT.LineName, wTaskIPT.CustomerID, wTaskIPT.CustomerName, wTaskIPT.StationID,
						wTaskIPT.StationName, wTaskIPT.PartPointID, wTaskIPT.PartPointName, (int) wItem.ID, wItem.Text,
						wItem.SRRepairParts, wValue.SRRepairParts, wValue.SRScrapParts, wValue.SRLYParts, wFQTY,
						wFQTYScrap, wFQTYLY, wTaskIPT.OrderID, wTaskIPT.PartNo, 0, "", MSSOperateType.WeiXiu.getValue(),
						MSSOperateType.WeiXiu.getLable(), wLoginUser.ID, wLoginUser.Name, Calendar.getInstance(),
						wValue.MaterialID, wValue.MaterialNo, wValue.MaterialName);
				MSSRepairRecordDAO.getInstance().Update(wLoginUser, wRecord, wErrorCode);
				break;
			case Zuzhuang:
				// ①通过部件编号，模糊查询拆解、维修记录
//				String[] wSS = wValue.SRAssemblyParts.split("#,#");
//				List<MSSRepairRecord> wList = MSSRepairRecordDAO.getInstance().SelectListBySerilalNo(wLoginUser, wSS[0],
//						wErrorCode);
				// ②修改目标车
				int wOrderID = 0;
				String wPartNo = "";
//				for (MSSRepairRecord wMSSRepairRecord : wList) {
//					wOrderID = wMSSRepairRecord.OrderID;
//					wPartNo = wMSSRepairRecord.PartNo;
//					wMSSRepairRecord.TargetPartNo = wTaskIPT.PartNo;
//					wMSSRepairRecord.TartgetOrderID = wTaskIPT.OrderID;
//					MSSRepairRecordDAO.getInstance().Update(wLoginUser, wMSSRepairRecord, wErrorCode);
//				}
				// ③保存组装记录
				wStrs = wValue.SRAssemblyParts.split(",");
				wFQTY = wStrs.length;
				wStrs = wValue.SRScrapParts.split(",");
				if (StringUtils.isNotEmpty(wValue.SRScrapParts))
					wFQTYScrap = wStrs.length;
				wStrs = wValue.SRLYParts.split(",");
				if (StringUtils.isNotEmpty(wValue.SRLYParts))
					wFQTYLY = wStrs.length;
				wRecord = new MSSRepairRecord(0, wTaskIPT.ProductID, wTaskIPT.ProductNo, wTaskIPT.LineID,
						wTaskIPT.LineName, wTaskIPT.CustomerID, wTaskIPT.CustomerName, wTaskIPT.StationID,
						wTaskIPT.StationName, wTaskIPT.PartPointID, wTaskIPT.PartPointName, (int) wItem.ID, wItem.Text,
						wItem.SRAssemblyParts, wValue.SRAssemblyParts, wValue.SRScrapParts, wValue.SRLYParts, wFQTY,
						wFQTYScrap, wFQTYLY, wOrderID, wPartNo, wTaskIPT.OrderID, wTaskIPT.PartNo,
						MSSOperateType.Zuzhuang.getValue(), MSSOperateType.Zuzhuang.getLable(), wLoginUser.ID,
						wLoginUser.Name, Calendar.getInstance(), wValue.MaterialID, wValue.MaterialNo,
						wValue.MaterialName);
				MSSRepairRecordDAO.getInstance().Update(wLoginUser, wRecord, wErrorCode);
				break;
			default:
				break;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSPartItem>> MSS_QuerySRRepairList(BMSEmployee wLoginUser, int wOrderID,
			String wPartsCode) {
		ServiceResult<List<MSSPartItem>> wResult = new ServiceResult<List<MSSPartItem>>();
		wResult.Result = new ArrayList<MSSPartItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			// ①根据订单查询部件记录集合
			List<MSSRepairRecord> wList = MSSRepairRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, -1,
					-1, wPartsCode, wOrderID, -1, -1, -1, wErrorCode);
			// ②获取部件列表
			// 多条件
//			List<MSSRepairRecord> wNRList = wList.stream()
//					.collect(Collectors.collectingAndThen(
//							Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getMSSPartCode()))),
//							ArrayList::new));
			// 返回物料号
			MSSPartItem wMaterilaItem = new MSSPartItem();
			if (wList.size() > 0) {
				wMaterilaItem.MaterailID = wList.get(0).MaterialID;
				wMaterilaItem.MaterialNo = wList.get(0).MaterialNo;
				wMaterilaItem.MaterialName = wList.get(0).MaterialName;
			}
			wResult.CustomResult.put("Material", wMaterilaItem);
			// ③判断部件是否只有拆解记录
			List<MSSRepairRecord> wChaiJieList = wList.stream()
					.filter(p -> p.OperateType == MSSOperateType.ChaiJie.getValue()).collect(Collectors.toList());
			for (MSSRepairRecord wMSSRepairRecord : wChaiJieList) {

				String[] wStrs = wMSSRepairRecord.MSSPartSerial.split("#,#");
				for (String wNo : wStrs) {
					if (wMSSRepairRecord.MSSPartSerialScrap.contains(wNo)
							|| wList.stream().anyMatch(p -> p.OperateType == MSSOperateType.WeiXiu.getValue()
									&& (p.MSSPartSerial.contains(wNo) || p.MSSPartSerialScrap.contains(wNo)))) {
						continue;
					}

					MSSPartItem wItem = new MSSPartItem();
					wItem.ID = wMSSRepairRecord.ID;
					wItem.Code = wMSSRepairRecord.MSSPartCode;
					wItem.SupplierPartNo = wNo;
					wItem.MaterailID = wMSSRepairRecord.MaterialID;
					wItem.MaterialNo = wMSSRepairRecord.MaterialNo;
					wItem.MaterialName = wMSSRepairRecord.MaterialName;
					wResult.Result.add(wItem);
				}

			}
			if (wResult.Result.size() > 0) {
				// ④获取编码，名称字典
				Map<String, String> wPartCodeMap = MSSPartRecordDAO.getInstance().GetPartCodeMap(wLoginUser,
						wErrorCode);
				// ⑤翻译数据，返回结果
				for (MSSPartItem wMSSPartItem : wResult.Result) {
					if (wPartCodeMap.containsKey(wMSSPartItem.Code)) {
						wMSSPartItem.Name = wPartCodeMap.get(wMSSPartItem.Code);
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
	public ServiceResult<List<MSSPartItem>> MSS_QuerySRAssembleList(BMSEmployee wLoginUser, int wProductID, int wLineID,
			String wPartsCode, int wOrderID) {
		ServiceResult<List<MSSPartItem>> wResult = new ServiceResult<List<MSSPartItem>>();
		wResult.Result = new ArrayList<MSSPartItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<MSSRepairRecord> wList = MSSRepairRecordDAO.getInstance().SelectList(wLoginUser, -1, wProductID,
					wLineID, -1, -1, -1, wPartsCode, -1, -1, MSSOperateType.WeiXiu.getValue(), -1, wErrorCode);
			// 多条件
			wList = wList.stream().collect(Collectors.collectingAndThen(
					Collectors.toCollection(() -> new TreeSet<>(Comparator
							.comparing(o -> o.getOrderID() + ";" + o.getMSSPartCode() + ";" + o.getMSSPartSerial()))),
					ArrayList::new));
			// 返回物料号
			MSSPartItem wMaterilaItem = new MSSPartItem();
			if (wList.size() > 0) {
				wMaterilaItem.MaterailID = wList.get(0).MaterialID;
				wMaterilaItem.MaterialNo = wList.get(0).MaterialNo;
				wMaterilaItem.MaterialName = wList.get(0).MaterialName;
			}
			wResult.CustomResult.put("Material", wMaterilaItem);
			for (MSSRepairRecord wMSSRepairRecord : wList) {
				String[] wStrs = wMSSRepairRecord.MSSPartSerial.split("#,#");
				for (String wCode : wStrs) {
					if (wMSSRepairRecord.MSSPartSerialScrap.contains(wCode)) {
						continue;
					}

					boolean wFlag = MSSRepairRecordDAO.getInstance().IsCodeZuZhuang(wLoginUser, wCode, wErrorCode);
					if (wFlag) {
						continue;
					}

					// ③添加
					MSSPartItem wItem = new MSSPartItem();

					wItem.ID = wMSSRepairRecord.ID;
					wItem.Code = wMSSRepairRecord.MSSPartCode;
					wItem.SupplierPartNo = wCode;
					wItem.MaterailID = wMSSRepairRecord.MaterialID;
					wItem.MaterialNo = wMSSRepairRecord.MaterialNo;
					wItem.MaterialName = wMSSRepairRecord.MaterialName;

					wResult.Result.add(wItem);
				}
			}

			if (wResult.Result.size() > 0) {
				// ④获取编码，名称字典
				Map<String, String> wPartCodeMap = MSSPartRecordDAO.getInstance().GetPartCodeMap(wLoginUser,
						wErrorCode);
				// ⑤翻译数据，返回结果
				for (MSSPartItem wMSSPartItem : wResult.Result) {
					if (wPartCodeMap.containsKey(wMSSPartItem.Code)) {
						wMSSPartItem.Name = wPartCodeMap.get(wMSSPartItem.Code);
					}
				}
			}

			// 查询车辆拆解自修件的数量
			List<MSSRepairRecord> wChaijie = MSSRepairRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, -1,
					-1, wPartsCode, wOrderID, -1, MSSOperateType.ChaiJie.getValue(), -1, wErrorCode);
			int wNumber = 0;
			if (wChaijie.size() > 0) {
				wNumber = wChaijie.get(0).FQTY + wChaijie.get(0).FQTYScrap;
			}
			wResult.CustomResult.put("Number", wNumber);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSRepairRecord>> MSS_QueryAllSRPartRecord(BMSEmployee wLoginUser, int wOrderID,
			int wOperateType) {
		ServiceResult<List<MSSRepairRecord>> wResult = new ServiceResult<List<MSSRepairRecord>>();
		wResult.Result = new ArrayList<MSSRepairRecord>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<MSSRepairRecord> wRecordList = new ArrayList<MSSRepairRecord>();

			wRecordList.addAll(MSSRepairRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, -1, -1, "",
					wOrderID, -1, wOperateType, -1, wErrorCode));
			wRecordList.addAll(MSSRepairRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, -1, -1, "", -1,
					wOrderID, wOperateType, -1, wErrorCode));

			// ①遍历赋值，明细区分，集合切割
			String[] wOKList;
			String[] wNoOKList;
			for (MSSRepairRecord wRecord : wRecordList) {
				switch (MSSOperateType.getEnumType(wRecord.OperateType)) {
				case ChaiJie:
					// 拆解
					wOKList = wRecord.MSSPartSerial.split("#,#");
					for (String wCode : wOKList) {
						if (StringUtils.isEmpty(wCode)) {
							continue;
						}
						MSSRepairRecord wCloneItem = CloneTool.Clone(wRecord, MSSRepairRecord.class);
						wCloneItem.MSSPartSerial = wCode;
						wCloneItem.MSSPartSerialScrap = "";
						wCloneItem.MSSPartSerialLY = "";
						// 组装车赋值
						MSSRepairRecord wCodeRecord = MSSRepairRecordDAO.getInstance().SelectByCode(wLoginUser, wCode,
								MSSOperateType.Zuzhuang.getValue(), wErrorCode);
						wCloneItem.TartgetOrderID = wCodeRecord.TartgetOrderID;
						wCloneItem.TargetPartNo = wCodeRecord.TargetPartNo;
						wResult.Result.add(wCloneItem);
					}
					// 拆解报废
					wNoOKList = wRecord.MSSPartSerialScrap.split("#,#");
					for (String wCode : wNoOKList) {
						if (StringUtils.isEmpty(wCode)) {
							continue;
						}
						MSSRepairRecord wCloneItem = CloneTool.Clone(wRecord, MSSRepairRecord.class);
						wCloneItem.MSSPartSerial = wCode;
						wCloneItem.MSSPartSerialScrap = "";
						wCloneItem.MSSPartSerialLY = "";
						wCloneItem.OpereateTypeName = "拆解报废";
						wResult.Result.add(wCloneItem);
					}
					break;
				case WeiXiu:
					// 维修
					wOKList = wRecord.MSSPartSerial.split("#,#");
					for (String wCode : wOKList) {
						if (StringUtils.isEmpty(wCode)) {
							continue;
						}
						MSSRepairRecord wCloneItem = CloneTool.Clone(wRecord, MSSRepairRecord.class);
						wCloneItem.MSSPartSerial = wCode;
						wCloneItem.MSSPartSerialScrap = "";
						wCloneItem.MSSPartSerialLY = "";
						// 组装车赋值
						MSSRepairRecord wCodeRecord = MSSRepairRecordDAO.getInstance().SelectByCode(wLoginUser, wCode,
								MSSOperateType.Zuzhuang.getValue(), wErrorCode);
						wCloneItem.TartgetOrderID = wCodeRecord.TartgetOrderID;
						wCloneItem.TargetPartNo = wCodeRecord.TargetPartNo;
						wResult.Result.add(wCloneItem);
					}
					// 维修报废
					wNoOKList = wRecord.MSSPartSerialScrap.split("#,#");
					for (String wCode : wNoOKList) {
						if (StringUtils.isEmpty(wCode)) {
							continue;
						}
						MSSRepairRecord wCloneItem = CloneTool.Clone(wRecord, MSSRepairRecord.class);
						wCloneItem.MSSPartSerial = wCode;
						wCloneItem.MSSPartSerialScrap = "";
						wCloneItem.MSSPartSerialLY = "";
						wCloneItem.OpereateTypeName = "维修报废";
						wResult.Result.add(wCloneItem);
					}
					break;
				case Zuzhuang:
					// 组装
					wOKList = wRecord.MSSPartSerial.split("#,#");
					for (String wCode : wOKList) {
						if (StringUtils.isEmpty(wCode)) {
							continue;
						}
						MSSRepairRecord wCloneItem = CloneTool.Clone(wRecord, MSSRepairRecord.class);
						wCloneItem.MSSPartSerial = wCode;
						wCloneItem.MSSPartSerialScrap = "";
						wCloneItem.MSSPartSerialLY = "";
						// 组装车赋值
						MSSRepairRecord wCodeRecord = MSSRepairRecordDAO.getInstance().SelectByCode(wLoginUser, wCode,
								MSSOperateType.ChaiJie.getValue(), wErrorCode);
						wCloneItem.OrderID = wCodeRecord.OrderID;
						wCloneItem.PartNo = wCodeRecord.PartNo;
						wResult.Result.add(wCloneItem);
					}
					// 组装领用
					wNoOKList = wRecord.MSSPartSerialLY.split("#,#");
					for (String wCode : wNoOKList) {
						if (StringUtils.isEmpty(wCode)) {
							continue;
						}
						MSSRepairRecord wCloneItem = CloneTool.Clone(wRecord, MSSRepairRecord.class);
						wCloneItem.MSSPartSerial = wCode;
						wCloneItem.MSSPartSerialScrap = "";
						wCloneItem.MSSPartSerialLY = "";
						wCloneItem.OpereateTypeName = "组装领用";
						wResult.Result.add(wCloneItem);
					}
					break;
				default:
					break;
				}
			}

			// 排序，按照部件、操作时间排序
			if (wResult.Result.size() > 0) {
				// 排序
				wResult.Result.sort(Comparator.comparing(MSSRepairRecord::getMSSPartCode)
						.thenComparing(MSSRepairRecord::getCreateTime));
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MBS_ClearData(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = MBSApiLogDAO.getInstance().ClearData(wLoginUser, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSPartItem>> MSS_QuerySRList(BMSEmployee wLoginUser, int wOrderID) {
		ServiceResult<List<MSSPartItem>> wResult = new ServiceResult<List<MSSPartItem>>();
		wResult.Result = new ArrayList<MSSPartItem>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			List<MSSRepairRecord> wList = MSSRepairRecordDAO.getInstance().SelectList(wLoginUser, -1, -1, -1, -1, -1,
					-1, "", wOrderID, -1, -1, -1, wErrorCode);
			int wIndex = 1;
			for (MSSRepairRecord wMSSRepairRecord : wList) {
				String[] wCodes = wMSSRepairRecord.MSSPartSerial.split("#,#");
				for (String wCode : wCodes) {
					MSSPartItem wItem = new MSSPartItem();

					wItem.ID = wIndex++;
					if (wMSSRepairRecord.MSSPartCode.contains("#")) {
						wItem.Code = wMSSRepairRecord.MSSPartCode.split("#")[1];
					} else {
						wItem.Code = wMSSRepairRecord.MSSPartCode;
					}
					wItem.SupplierPartNo = wCode;

					if (!wResult.Result.stream().anyMatch(p -> p.SupplierPartNo.equals(wCode)))
						wResult.Result.add(wItem);
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
	public ServiceResult<String> NCR_ExportTaskList(BMSEmployee wLoginUser, List<NCRTask> wList) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<MyExcelSheet> wSheetList = GetMyExcelSheetList(wLoginUser, wList);

			wResult.Result = ExcelUtil.ExportData(wSheetList, "不合格品评审与处置表台账");

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 转换不合格评审导出结构
	 */
	private List<MyExcelSheet> GetMyExcelSheetList(BMSEmployee wLoginUser, List<NCRTask> wList) {
		List<MyExcelSheet> wResult = new ArrayList<MyExcelSheet>();
		try {
			List<String> headerList = new ArrayList<String>(Arrays.asList("单据编号", "评审级别", "类别", "车型", "车号", "修程",
					"部件名称", "部件编码", "数量", "不合格描述", "回修性质", "检验节点", "责任单位", "处置意见", "检验人员", "发起日期", "任务更新日期", "状态"));

			// 排序
			wList.sort(Comparator.comparing(NCRTask::getCreateTime));

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy/MM/dd");

			List<List<String>> dataList = new ArrayList<List<String>>();
			for (NCRTask wNCRTask : wList) {
				if (wNCRTask.LevelName.equals("默认")) {
					wNCRTask.LevelName = "";
				}
				if (wNCRTask.TypeName.equals("默认")) {
					wNCRTask.TypeName = "";
				}
				if (wNCRTask.ResultName.equals("默认")) {
					wNCRTask.ResultName = "";
				}
				if (wNCRTask.QuestionTypeText.equals("默认")) {
					wNCRTask.QuestionTypeText = "";
				}

				List<String> wRowList = new ArrayList<String>(Arrays.asList(wNCRTask.Code, wNCRTask.LevelName,
						wNCRTask.TypeName, wNCRTask.CarType, wNCRTask.CarNumber, wNCRTask.LineName,
						wNCRTask.ProductName, wNCRTask.ModelNo, String.valueOf(wNCRTask.Number), wNCRTask.DescribeInfo,
						wNCRTask.QuestionTypeText, wNCRTask.StationName, wNCRTask.DutyDepartmentName,
						wNCRTask.ResultName, wNCRTask.UpFlowName, wSDF.format(wNCRTask.CreateTime.getTime()),
						wSDF.format(wNCRTask.SubmitTime.getTime()), wNCRTask.StatusText));
				dataList.add(wRowList);
			}

			MyExcelSheet wMyExcelSheet = new MyExcelSheet(dataList, headerList, "台账", "<不合格品评审>登记台账");
			wResult.add(wMyExcelSheet);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<MSSBOMItem>> MSS_QueryBomItemAll(BMSEmployee wLoginUser, int wBOMID, int wPartID,
			int wStepID, String wMaterialNo, String wMaterialName, int wReplaceType, int wOutsourceType) {
		ServiceResult<List<MSSBOMItem>> wResult = new ServiceResult<List<MSSBOMItem>>();
		wResult.Result = new ArrayList<MSSBOMItem>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ②查询BOM子项列表
			wResult.Result = MSSBOMItemDAO.getInstance().SelectList(wLoginUser, wBOMID, wStepID, wPartID, wMaterialNo,
					wMaterialName, wReplaceType, wOutsourceType, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> BMS_IsManager(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<BMSRoleItem> wList = CoreServiceImpl.getInstance().BMS_UserAll(wLoginUser, 2).List(BMSRoleItem.class);
			if (wList == null || wList.size() <= 0) {
				return wResult;
			}

			if (wList.stream().anyMatch(p -> p.FunctionID == wLoginUser.ID)) {
				wResult.Result = 1;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> MSS_ActiveListBomItem(BMSEmployee wLoginUser, List<Integer> wIDList, int wActive) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = MSSBOMItemDAO.getInstance().MSS_ActiveListBomItem(wLoginUser, wIDList, wActive,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> FPC_UpdateSpecialControl(BMSEmployee wLoginUser, FPCRoutePart wFPCRoutePart) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			String wControls = FPCRouteDAO.getInstance().GetControls(wLoginUser, wFPCRoutePart, wErrorCode);

			RecordChangeControlLog(wLoginUser, wFPCRoutePart, wControls, wErrorCode);

			FPCRouteDAO.getInstance().UpdateSpecialControl(wLoginUser, wFPCRoutePart, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private void RecordChangeControlLog(BMSEmployee wLoginUser, FPCRoutePart wFPCRoutePart, String wControls,
			OutResult<Integer> wErrorCode) {
		try {
			String wNewControls = StringUtils.Join(",", wFPCRoutePart.ControlPartIDList);
			if (wNewControls.equals(wControls)) {
				return;
			}

			List<String> wNames = new ArrayList<String>();
			for (int wPartID : wFPCRoutePart.ControlPartIDList) {
				String wPartName = QMSConstants.GetFPCPartName(wPartID);
				if (StringUtils.isEmpty(wPartName)) {
					continue;
				}
				wNames.add(wPartName);
			}
			String wNewParts = StringUtils.Join(",", wNames);

			SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String wCurrentTime = wSDF.format(Calendar.getInstance().getTime());

			String wContent = StringUtils.Format("【{0}】在【{1}】将【{2}】的专检控制属性修改为【{3}】", wLoginUser.Name, wCurrentTime,
					QMSConstants.GetFPCPartName(wFPCRoutePart.PartID), wNewParts);

			LFSOperationLog wLFSOperationLog = new LFSOperationLog(0, wFPCRoutePart.ID,
					LFSOperationLogType.SpecialControl.getValue(), LFSOperationLogType.SpecialControl.getLable(),
					wLoginUser.ID, wLoginUser.Name, Calendar.getInstance(), wContent);
			LFSServiceImpl.getInstance().LFS_UpdateOperationLog(wLoginUser, wLFSOperationLog);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<Integer> FPC_AdjustStep(BMSEmployee wLoginUser, int wPartPointID, int wRightPartID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// 0、找到错误的所有工艺工序集合。
			List<FPCRoutePartPoint> wFPCRoutePartPointList = FPCRouteDAO.getInstance()
					.FPC_QueryErrorStepList(wLoginUser, wPartPointID, wRightPartID, wErrorCode);
			if (wFPCRoutePartPointList == null || wFPCRoutePartPointList.size() <= 0) {
				return wResult;
			}
			// 1、修改工艺工序的工位ID。
			for (FPCRoutePartPoint wFPCRoutePartPoint : wFPCRoutePartPointList) {
				FPCRouteDAO.getInstance().UpdateRightPartID(wLoginUser, wFPCRoutePartPoint, wErrorCode);
			}
			// 2、获取在厂维修车辆。
			List<Integer> wOrderIDList = SFCTaskIPTDAO.getInstance().GetRepairingOrderIDList(wLoginUser, wErrorCode);
			if (wOrderIDList == null || wOrderIDList.size() <= 0) {
				return wResult;
			}
			List<OMSOrder> wOrderList = LOCOAPSServiceImpl.getInstance()
					.OMS_QueryOrderListByIDList(wLoginUser, wOrderIDList).List(OMSOrder.class);
			if (wOrderList == null || wOrderList.size() <= 0) {
				return wResult;
			}
			// 3、根据工艺工序RouteID找到对应车辆。
			for (FPCRoutePartPoint wFPCRoutePartPoint : wFPCRoutePartPointList) {
				if (!wOrderList.stream().anyMatch(p -> p.RouteID == wFPCRoutePartPoint.RouteID)) {
					continue;
				}

				List<OMSOrder> wOList = wOrderList.stream().filter(p -> p.RouteID == wFPCRoutePartPoint.RouteID)
						.collect(Collectors.toList());
				for (OMSOrder wOMSOrder : wOList) {
					// 4、找到工序任务
					APSTaskStep wAPSTaskStep = FPCRouteDAO.getInstance().GetTaskStepID(wLoginUser, wOMSOrder.ID,
							wPartPointID, wErrorCode);
					if (wAPSTaskStep.ID <= 0) {
						continue;
					}
					// 5、找到工位任务ID。
					int wTaskPartID = FPCRouteDAO.getInstance().GetTaskPartID(wLoginUser, wOMSOrder.ID, wRightPartID,
							wErrorCode);
					if (wTaskPartID > 0) {
						// 6、修改工序任务的工位ID，工位任务ID。
						APSTaskStep wInfo = LOCOAPSServiceImpl.getInstance()
								.APS_QueryTaskStepByID(wLoginUser, wAPSTaskStep.ID).Info(APSTaskStep.class);
						wInfo.PartID = wRightPartID;
						wInfo.TaskPartID = wTaskPartID;
						LOCOAPSServiceImpl.getInstance().APS_UpdateTaskStep(wLoginUser, wInfo);
					}
					// 7、通过工序任务ID找到检验任务集合。
					List<SFCTaskIPT> wList = SFCTaskIPTDAO.getInstance().SelectList(wLoginUser, -1, wAPSTaskStep.ID, -1,
							-1, -1, -1, null, -1, null, null, wErrorCode);
					// 8、修改检验任务的工位ID。
					for (SFCTaskIPT wSFCTaskIPT : wList) {
						wSFCTaskIPT.StationID = wRightPartID;
						SFCTaskIPTDAO.getInstance().SFC_SaveTaskIPT(wLoginUser, wSFCTaskIPT, wErrorCode);
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
	public ServiceResult<String> MSS_SynchronizedMaterial(BMSEmployee wLoginUser, ExcelData wExcelData,
			String wOriginalFileName) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wExcelData.sheetData == null || wExcelData.sheetData.size() <= 0) {
				return wResult;
			}
			ExcelSheetData wSheetData = wExcelData.sheetData.get(0);

			// 单位列表
			List<CFGUnit> wCFGUnitList = CoreServiceImpl.getInstance().CFG_QueryUnitList(BaseDAO.SysAdmin)
					.List(CFGUnit.class);

			int wIndex = 0;
			for (ExcelLineData wExcelLineData : wSheetData.lineData) {
				if (wIndex == 0) {
					wIndex++;
					continue;
				}
				// ①物料号
				String wMaterialNo = "";
				if (wExcelLineData.colData.size() > 0) {
					wMaterialNo = wExcelLineData.colData.get(0);
				}
				// ②常用计量单位
				String wUnitText = "";
				if (wExcelLineData.colData.size() > 4) {
					wUnitText = wExcelLineData.colData.get(4);
				}
				// ③物料类型
				String wMaterailType = "";
				if (wExcelLineData.colData.size() > 10) {
					wMaterailType = wExcelLineData.colData.get(10);
				}
				// ④物料组
				String wMaterailGroup = "";
				if (wExcelLineData.colData.size() > 13) {
					wMaterailGroup = wExcelLineData.colData.get(13);
				}
				// ⑤大小量纲
				String wDXLG = "";
				if (wExcelLineData.colData.size() > 3) {
					wDXLG = wExcelLineData.colData.get(3);
				}
				// ⑥工业标准
				String wStandart = "";
				if (wExcelLineData.colData.size() > 24) {
					wStandart = wExcelLineData.colData.get(24);
				}
				// ⑦净重
				String wJZ = "";
				if (wExcelLineData.colData.size() > 22) {
					wJZ = wExcelLineData.colData.get(22);
				}
				// ⑧毛重
				String wMZ = "";
				if (wExcelLineData.colData.size() > 16) {
					wMZ = wExcelLineData.colData.get(16);
				}

				// ⑨附加信息
				String wRemark = "";
				if (wExcelLineData.colData.size() > 7) {
					wRemark = wExcelLineData.colData.get(7);
				}

				MSSMaterial wMSSMaterial = GetMaterial(wMaterialNo, wUnitText, wMaterailType, wMaterailGroup, wDXLG,
						wStandart, wJZ, wMZ, wCFGUnitList, wRemark);
				IPTStandardDAO.getInstance().MSS_UpdateMaterial(wLoginUser, wMSSMaterial, wErrorCode);
				System.out.println(wIndex++);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private MSSMaterial GetMaterial(String wMaterialNo, String wUnitText, String wMaterailType, String wMaterailGroup,
			String wDXLG, String wStandart, String wJZ, String wMZ, List<CFGUnit> wCFGUnitList, String wRemark) {
		MSSMaterial wResult = new MSSMaterial();
		try {
			wResult.MaterialNo = wMaterialNo;

			int wUnitID = 0;
			if (wCFGUnitList.stream().anyMatch(p -> p.Name.equals(wUnitText))) {
				wUnitID = wCFGUnitList.stream().filter(p -> p.Name.equals(wUnitText)).findFirst().get().ID;
			} else {
				CFGUnit wCFGUnit = new CFGUnit();
				wCFGUnit.Name = wUnitText;
				wCFGUnit.OperatorID = -100;
				wCFGUnit.EditTime = Calendar.getInstance();
				wCFGUnit.Active = 1;
				wCFGUnit = CoreServiceImpl.getInstance().CFG_SaveUnit(BaseDAO.SysAdmin, wCFGUnit).Info(CFGUnit.class);
				if (wCFGUnit != null && wCFGUnit.ID > 0) {
					wUnitID = wCFGUnit.ID;
					wCFGUnitList.add(wCFGUnit);
				}
			}
			wResult.CYUnitID = wUnitID;

			wResult.MaterialType = wMaterailType;
			wResult.MaterialGroup = wMaterailGroup;
			wResult.Groes = wDXLG;
			wResult.Normt = wStandart;
			wResult.NetWeight = StringUtils.parseDouble(wJZ);
			wResult.GrossWeight = StringUtils.parseDouble(wMZ);
			wResult.Remark = wRemark;
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> FPC_ExportBOP(BMSEmployee wLoginUser, int wRouteID) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			MyExcelSheet wMyExcelSheet = GetMyExcelSheet(wLoginUser, wRouteID, wErrorCode);

			List<MyExcelSheet> wMyExcelSheetList = new ArrayList<MyExcelSheet>(Arrays.asList(wMyExcelSheet));

			wResult.Result = ExcelUtil.ExportData(wMyExcelSheetList, "工艺BOP");

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	private MyExcelSheet GetMyExcelSheet(BMSEmployee wLoginUser, int wRouteID, OutResult<Integer> wErrorCode) {
		MyExcelSheet wResult = new MyExcelSheet();
		try {
			wResult.HeaderList = new ArrayList<String>(Arrays.asList("序号", "工位", "工序"));
			wResult.DataList = FPCRouteDAO.getInstance().GetBOPList(wLoginUser, wRouteID, wErrorCode);
			wResult.SheetName = "工艺BOP";
			wResult.TitleName = "工艺BOP";
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}
	
	@Override
	public ServiceResult<Integer> MSS_Import(BMSEmployee wLoginUser, ServiceResult<ExcelData> wExcelData) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			String wMaterialNo = "";
//			int wLevel = 0;

			ExcelSheetData wSheet = wExcelData.Result.sheetData.get(0);
			int wIndex = 1;
			List<String> wMaterialAList = new ArrayList<String>();
			List<String> wMaterialBList = new ArrayList<String>();
			List<String> wMaterialCList = new ArrayList<String>();
			for (ExcelLineData wExcelLineData : wSheet.lineData) {
				if (wIndex == 1) {
					wIndex++;
					continue;
				}
				wMaterialNo = wExcelLineData.colData.get(1);

				switch (wExcelLineData.colData.get(6)) {
				case "A":
//					wLevel = 1;
					String wTempA = wMaterialNo;
					if (!wMaterialAList.stream().anyMatch(p -> p.equals(wTempA))) {
						wMaterialAList.add(wMaterialNo);
					}
					break;
				case "B":
//					wLevel = 2;
					String wTempB = wMaterialNo;
					if (!wMaterialBList.stream().anyMatch(p -> p.equals(wTempB))) {
						wMaterialBList.add(wMaterialNo);
					}
					break;
				case "C":
//					wLevel = 3;
					String wTempC = wMaterialNo;
					if (!wMaterialCList.stream().anyMatch(p -> p.equals(wTempC))) {
						wMaterialCList.add(wMaterialNo);
					}
					break;

				default:
					break;
				}

//				MSSBOMItemDAO.getInstance().UpdateLevel(wLoginUser, wMaterialNo, wLevel, wErrorCode);

				System.out.println(wIndex++);
			}

			if (wMaterialAList.size() > 0) {
				String wMA = StringUtils.Join("','", wMaterialAList);
				MSSBOMItemDAO.getInstance().UpdateLevelList(wLoginUser, wMA, 1, wErrorCode);
			}
			if (wMaterialBList.size() > 0) {
				String wMB = StringUtils.Join("','", wMaterialBList);
				MSSBOMItemDAO.getInstance().UpdateLevelList(wLoginUser, wMB, 2, wErrorCode);
			}
			if (wMaterialCList.size() > 0) {
				String wMC = StringUtils.Join("','", wMaterialCList);
				MSSBOMItemDAO.getInstance().UpdateLevelList(wLoginUser, wMC, 3, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}
}
