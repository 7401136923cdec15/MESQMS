package com.mes.qms.server.serviceimpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mes.qms.server.service.TCMService;
import com.mes.qms.server.service.mesenum.BPMEventModule;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.mesenum.TCMReworkStatus;
import com.mes.qms.server.service.mesenum.TCMTechChangeNoticeStatus;
import com.mes.qms.server.service.mesenum.TagTypes;
import com.mes.qms.server.service.mesenum.TaskQueryType;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.bpm.BPMTaskBase;
import com.mes.qms.server.service.po.tcm.TCMFileResponse;
import com.mes.qms.server.service.po.tcm.TCMMaterialChangeItems;
import com.mes.qms.server.service.po.tcm.TCMMaterialChangeLog;
import com.mes.qms.server.service.po.tcm.TCMPreviewFile;
import com.mes.qms.server.service.po.tcm.TCMRework;
import com.mes.qms.server.service.po.tcm.TCMTechChangeNotice;
import com.mes.qms.server.service.utils.CloneTool;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;
import com.mes.qms.server.serviceimpl.dao.fpc.FPCCommonFileDAO;
import com.mes.qms.server.serviceimpl.dao.tcm.TCMMaterialChangeItemsDAO;
import com.mes.qms.server.serviceimpl.dao.tcm.TCMMaterialChangeLogDAO;
import com.mes.qms.server.serviceimpl.dao.tcm.TCMReworkDAO;
import com.mes.qms.server.serviceimpl.dao.tcm.TCMTechChangeNoticeDAO;
import com.mes.qms.server.serviceimpl.utils.qms.QMSConstants;
import com.mes.qms.server.serviceimpl.utils.qms.QMSUtils;
import com.mes.qms.server.utils.Constants;
import com.mes.qms.server.utils.qms.MESFileUtils;

@Service
public class TCMServiceImpl implements TCMService {
	private static Logger logger = LoggerFactory.getLogger(TCMServiceImpl.class);

	private static TCMService _instance;

	public static TCMService getInstance() {
		if (_instance == null)
			_instance = new TCMServiceImpl();

		return _instance;
	}

	@Override
	public ServiceResult<TCMTechChangeNotice> TCM_QueryDefaultTechChangeNotice(BMSEmployee wLoginUser, int wEventID) {
		ServiceResult<TCMTechChangeNotice> wResult = new ServiceResult<TCMTechChangeNotice>();
		wResult.Result = new TCMTechChangeNotice();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<TCMTechChangeNotice> wList = TCMTechChangeNoticeDAO.getInstance().SelectList(wLoginUser, -1, "",
					wLoginUser.ID, -1, new ArrayList<Integer>(Arrays.asList(0)), null, null, wErrorCode);
			if (wList.size() > 0) {
				wResult.Result = wList.get(0);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<TCMTechChangeNotice> TCM_CreateTechChangeNotice(BMSEmployee wLoginUser,
			BPMEventModule wEventID) {
		ServiceResult<TCMTechChangeNotice> wResult = new ServiceResult<TCMTechChangeNotice>();
		wResult.Result = new TCMTechChangeNotice();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result.Code = TCMTechChangeNoticeDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wResult.Result.UpFlowID = wLoginUser.ID;
			wResult.Result.UpFlowName = wLoginUser.Name;
			wResult.Result.CreateTime = Calendar.getInstance();
			wResult.Result.SubmitTime = Calendar.getInstance();
			wResult.Result.ID = 0;
			wResult.Result.Status = TCMTechChangeNoticeStatus.Default.getValue();
			wResult.Result.StatusText = "";
			wResult.Result.FlowType = wEventID.getValue();

			wResult.Result = (TCMTechChangeNotice) TCMTechChangeNoticeDAO.getInstance().BPM_UpdateTask(wLoginUser,
					wResult.Result, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<TCMTechChangeNotice> TCM_SubmitTechChangeNotice(BMSEmployee wLoginUser,
			TCMTechChangeNotice wData) {
		ServiceResult<TCMTechChangeNotice> wResult = new ServiceResult<TCMTechChangeNotice>();
		wResult.Result = new TCMTechChangeNotice();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wData.Status == 1) {
				// 变更单状态改为1已发起
				ChangeLogStatus(wLoginUser, wData.ChangeLogID);
			}

			if (wData.Status == 22) {
				wData.StatusText = "已驳回";
			} else if (wData.Status == 20) {
				wData.StatusText = "已完成";

				// 线程处理工艺变更结果
				ExecutorService wES = Executors.newFixedThreadPool(1);
				wES.submit(() -> RSMServiceImpl.getInstance().HandleTechCahnge(wLoginUser, wData));
				wES.shutdown();

//				QMSConstants.mTCMTechChangeNotice = wData;
			} else if (wData.Status == 21) {
				wData.StatusText = "已撤销";
			} else if (wData.Status == 25) {
				wData.StatusText = "待批准";
			}

			wResult.Result = (TCMTechChangeNotice) TCMTechChangeNoticeDAO.getInstance().BPM_UpdateTask(wLoginUser,
					wData, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 改变工艺变更单的状态
	 */
	private void ChangeLogStatus(BMSEmployee wLoginUser, int changeLogID) {
		try {
			if (changeLogID <= 0) {
				return;
			}

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			TCMMaterialChangeLog wTCMMaterialChangeLog = TCMMaterialChangeLogDAO.getInstance()
					.SelectByIDNoSub(wLoginUser, changeLogID, wErrorCode);
			if (wTCMMaterialChangeLog == null || wTCMMaterialChangeLog.ID <= 0) {
				return;
			}

			wTCMMaterialChangeLog.ShowStatus = 1;
			TCMMaterialChangeLogDAO.getInstance().Update(wLoginUser, wTCMMaterialChangeLog, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	@Override
	public ServiceResult<TCMTechChangeNotice> TCM_GetTechChangeNotice(BMSEmployee wLoginUser, int wID) {
		ServiceResult<TCMTechChangeNotice> wResult = new ServiceResult<TCMTechChangeNotice>();
		wResult.Result = new TCMTechChangeNotice();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (TCMTechChangeNotice) TCMTechChangeNoticeDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wID,
					"", wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> TCM_QueryTechChangeNoticeEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// 2发起
				wResult.Result = TCMTechChangeNoticeDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			case Dispatcher:// 1待做
				wResult.Result = TCMTechChangeNoticeDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID,
						wErrorCode);
				break;
			case Approver:// 4已做
				wResult.Result = TCMTechChangeNoticeDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID,
						wStartTime, wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			if (wResult.Result.size() > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}

			// 排序
			wResult.Result.sort(Comparator.comparing(BPMTaskBase::getCreateTime, Comparator.reverseOrder()));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<TCMTechChangeNotice>> TCM_QueryTechChangeNoticeHistory(BMSEmployee wLoginUser, int wID,
			String wCode, int wUpFlowID, int wChangeLogID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<TCMTechChangeNotice>> wResult = new ServiceResult<List<TCMTechChangeNotice>>();
		wResult.Result = new ArrayList<TCMTechChangeNotice>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = TCMTechChangeNoticeDAO.getInstance().SelectList(wLoginUser, wID, wCode, wUpFlowID,
					wChangeLogID, null, wStartTime, wEndTime, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<TCMTechChangeNotice>> TCM_QueryTechChangeNoticeEmployeeAllWeb(BMSEmployee wLoginUser,
			Integer wStatus, Calendar wStartTime, Calendar wEndTime, int wStepID, String wMaterialNo) {
		ServiceResult<List<TCMTechChangeNotice>> wResult = new ServiceResult<List<TCMTechChangeNotice>>();
		wResult.Result = new ArrayList<TCMTechChangeNotice>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (wStatus) {
			case 1:
				wResult.Result.addAll(TCMTechChangeNoticeDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1,
						new ArrayList<Integer>(Arrays.asList(20, 21, 22)), wStartTime, wEndTime, wErrorCode));
				break;
			case 0:
				wResult.Result.addAll(TCMTechChangeNoticeDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1,
						new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 25)), wStartTime, wEndTime, wErrorCode));
				break;
			default:
				wResult.Result.addAll(TCMTechChangeNoticeDAO.getInstance().SelectList(wLoginUser, -1, "", -1, -1,
						new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 20, 21, 22, 25)), wStartTime, wEndTime,
						wErrorCode));
				break;
			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			// 工序、物料查询单号
			if (wStepID > 0 || StringUtils.isNotEmpty(wMaterialNo)) {
				List<Integer> wTaskIDList = TCMTechChangeNoticeDAO.getInstance().SelectTaskIDList(wLoginUser, wStepID,
						wMaterialNo, wErrorCode);
				wResult.Result = wResult.Result.stream().filter(p -> wTaskIDList.stream().anyMatch(q -> q == p.ID))
						.collect(Collectors.toList());
			}

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}

			// 待办数据处理
			List<BPMTaskBase> wBaseList = TCMTechChangeNoticeDAO.getInstance().BPM_GetUndoTaskList(wLoginUser,
					wLoginUser.getID(), wErrorCode);
			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {
				if (!(wTaskBase instanceof TCMTechChangeNotice))
					continue;
				TCMTechChangeNotice wTCMTechChangeNotice = (TCMTechChangeNotice) wTaskBase;
				wTCMTechChangeNotice.TagTypes = TaskQueryType.ToHandle.getValue();
				for (int i = 0; i < wResult.Result.size(); i++) {
					if (wResult.Result.get(i).ID == wTCMTechChangeNotice.ID)
						wResult.Result.set(i, wTCMTechChangeNotice);
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
	public ServiceResult<TCMRework> TCM_QueryDefaultRework(BMSEmployee wLoginUser, int wEventID) {
		ServiceResult<TCMRework> wResult = new ServiceResult<TCMRework>();
		wResult.Result = new TCMRework();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<TCMRework> wList = TCMReworkDAO.getInstance().SelectList(wLoginUser, -1, "", wLoginUser.ID, null, null,
					new ArrayList<Integer>(Arrays.asList(0)), wErrorCode);
			if (wList.size() > 0) {
				wResult.Result = wList.get(0);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public synchronized ServiceResult<TCMRework> TCM_CreateRework(BMSEmployee wLoginUser, BPMEventModule wEventID) {
		ServiceResult<TCMRework> wResult = new ServiceResult<TCMRework>();
		wResult.Result = new TCMRework();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result.Code = TCMReworkDAO.getInstance().GetNewCode(wLoginUser, wErrorCode);
			wResult.Result.UpFlowID = wLoginUser.ID;
			wResult.Result.UpFlowName = wLoginUser.Name;
			wResult.Result.CreateTime = Calendar.getInstance();
			wResult.Result.SubmitTime = Calendar.getInstance();
			wResult.Result.ID = 0;
			wResult.Result.Status = TCMReworkStatus.Default.getValue();
			wResult.Result.StatusText = "";
			wResult.Result.FlowType = wEventID.getValue();

			wResult.Result = (TCMRework) TCMReworkDAO.getInstance().BPM_UpdateTask(wLoginUser, wResult.Result,
					wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<TCMRework> TCM_SubmitRework(BMSEmployee wLoginUser, TCMRework wData) {
		ServiceResult<TCMRework> wResult = new ServiceResult<TCMRework>();
		wResult.Result = new TCMRework();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			if (wData.Status == 20) {
				wData.StatusText = "已完工";
			}

			wResult.Result = (TCMRework) TCMReworkDAO.getInstance().BPM_UpdateTask(wLoginUser, wData, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<TCMRework> TCM_GetRework(BMSEmployee wLoginUser, int wID) {
		ServiceResult<TCMRework> wResult = new ServiceResult<TCMRework>();
		wResult.Result = new TCMRework();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = (TCMRework) TCMReworkDAO.getInstance().BPM_GetTaskInfo(wLoginUser, wID, "", wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BPMTaskBase>> TCM_QueryReworkEmployeeAll(BMSEmployee wLoginUser, int wTagTypes,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<BPMTaskBase>> wResult = new ServiceResult<List<BPMTaskBase>>();
		wResult.Result = new ArrayList<BPMTaskBase>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (TagTypes.getEnumType(wTagTypes)) {
			case Applicant:// 2发起
				wResult.Result = TCMReworkDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.ID, wStartTime,
						wEndTime, wErrorCode);
				break;
			case Dispatcher:// 1待做
				wResult.Result = TCMReworkDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.ID, wErrorCode);
				break;
			case Approver:// 4已做
				wResult.Result = TCMReworkDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.ID, wStartTime,
						wEndTime, wErrorCode);
				break;
			default:
				break;
			}

			if (wResult.Result.size() > 0) {
				wResult.Result = wResult.Result.stream().filter(p -> p.Status != 0).collect(Collectors.toList());
			}

			// 排序
			wResult.Result.sort(Comparator.comparing(BPMTaskBase::getCreateTime, Comparator.reverseOrder()));

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<TCMRework>> TCM_QueryReworkHistory(BMSEmployee wLoginUser, int wID, String wCode,
			int wUpFlowID, Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<TCMRework>> wResult = new ServiceResult<List<TCMRework>>();
		wResult.Result = new ArrayList<TCMRework>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = TCMReworkDAO.getInstance().SelectList(wLoginUser, wID, wCode, wUpFlowID, wStartTime,
					wEndTime, null, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<TCMRework>> TCM_QueryReworkEmployeeAllNew(BMSEmployee wLoginUser, Integer wStatus,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<TCMRework>> wResult = new ServiceResult<List<TCMRework>>();
		wResult.Result = new ArrayList<TCMRework>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<TCMRework> wSendList = new ArrayList<TCMRework>();
			List<TCMRework> wToDoList = new ArrayList<TCMRework>();
			List<TCMRework> wDoneList = new ArrayList<TCMRework>();

			List<BPMTaskBase> wBaseList = TCMReworkDAO.getInstance().BPM_GetSendTaskList(wLoginUser, wLoginUser.getID(),
					wStartTime, wEndTime, wErrorCode);
			wSendList = CloneTool.CloneArray(wBaseList, TCMRework.class);

			wBaseList = TCMReworkDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(), wErrorCode);
			wToDoList = CloneTool.CloneArray(wBaseList, TCMRework.class);

			wBaseList = TCMReworkDAO.getInstance().BPM_GetDoneTaskList(wLoginUser, wLoginUser.getID(), wStartTime,
					wEndTime, wErrorCode);
			wDoneList = CloneTool.CloneArray(wBaseList, TCMRework.class);

			for (TCMRework wTCMRework : wToDoList) {
				wTCMRework.TagTypes = TaskQueryType.ToHandle.getValue();
				wResult.Result.add(wTCMRework);
			}

			for (TCMRework wTCMRework : wDoneList) {
				wTCMRework.TagTypes = TaskQueryType.Handled.getValue();
				wResult.Result.add(wTCMRework);
			}

			for (TCMRework wTCMRework : wSendList) {
				wTCMRework.TagTypes = TaskQueryType.Sended.getValue();
				wResult.Result.add(wTCMRework);
			}

			wResult.Result.removeIf(p -> p.Status == 0);

			for (TCMRework wTCMRework : wResult.Result) {
				if (wResult.Result.stream()
						.anyMatch(p -> p.ID == wTCMRework.ID && p.TagTypes != wTCMRework.TagTypes
								&& wTCMRework.TagTypes == TaskQueryType.Handled.getValue()
								&& p.TagTypes == TaskQueryType.Sended.getValue())) {
					wTCMRework.TagTypes = TaskQueryType.Sended.getValue();
				}
			}

			// ID去重
			wResult.Result = new ArrayList<TCMRework>(wResult.Result.stream()
					.collect(Collectors.toMap(TCMRework::getID, account -> account, (k1, k2) -> k2)).values());

		} catch (Exception e) {
			wResult.FaultCode += e.getMessage();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<BMSEmployee>> TCM_QueryUserList(BMSEmployee wLoginUser) {
		ServiceResult<List<BMSEmployee>> wResult = new ServiceResult<List<BMSEmployee>>();
		wResult.Result = new ArrayList<BMSEmployee>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			wResult.Result = QMSConstants.GetBMSEmployeeList().values().stream()
					.filter(p -> p.DepartmentID == wLoginUser.DepartmentID && p.Active == 1)
					.collect(Collectors.toList());

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<TCMMaterialChangeLog> TCM_QueryMaterialChangeLog(BMSEmployee wLoginUser, int wID) {
		ServiceResult<TCMMaterialChangeLog> wResult = new ServiceResult<TCMMaterialChangeLog>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			wResult.Result = TCMMaterialChangeLogDAO.getInstance().SelectByID(wLoginUser, wID, wErrorCode);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> TCM_UpdateList(BMSEmployee wLoginUser, List<TCMMaterialChangeItems> wItemList) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wItemList) {
				TCMMaterialChangeItemsDAO.getInstance().Update(wLoginUser, wTCMMaterialChangeItems, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<List<TCMRework>> TCM_QueryReworkEmployeeAllWeb(BMSEmployee wLoginUser, Integer wStatus,
			Calendar wStartTime, Calendar wEndTime) {
		ServiceResult<List<TCMRework>> wResult = new ServiceResult<List<TCMRework>>();
		wResult.Result = new ArrayList<TCMRework>();
		OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
		try {
			switch (wStatus) {
			case 1:
				wResult.Result.addAll(TCMReworkDAO.getInstance().SelectList(wLoginUser, -1, "", -1, wStartTime,
						wEndTime, new ArrayList<Integer>(Arrays.asList(20)), wErrorCode));
				break;
			case 0:
				wResult.Result.addAll(TCMReworkDAO.getInstance().SelectList(wLoginUser, -1, "", -1, null, null,
						new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)), wErrorCode));
				break;
			default:
				wResult.Result.addAll(TCMReworkDAO.getInstance().SelectList(wLoginUser, -1, "", -1, wStartTime,
						wEndTime, new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 20, 21, 22)), wErrorCode));
				break;
			}
			wResult.Result.sort((o1, o2) -> o2.SubmitTime.compareTo(o1.SubmitTime));

			if (wResult.Result.size() <= 0 || wStatus == 1) {
				return wResult;
			}

			// 待办数据处理
			List<BPMTaskBase> wBaseList = TCMReworkDAO.getInstance().BPM_GetUndoTaskList(wLoginUser, wLoginUser.getID(),
					wErrorCode);
			if (wBaseList == null || wBaseList.size() <= 0) {
				return wResult;
			}
			for (BPMTaskBase wTaskBase : wBaseList) {
				if (!(wTaskBase instanceof TCMRework))
					continue;
				TCMRework wTCMRework = (TCMRework) wTaskBase;
				wTCMRework.TagTypes = TaskQueryType.ToHandle.getValue();
				for (int i = 0; i < wResult.Result.size(); i++) {
					if (wResult.Result.get(i).ID == wTCMRework.ID)
						wResult.Result.set(i, wTCMRework);
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
	public ServiceResult<String> TCM_getSignature(BMSEmployee wLoginUser, String wSha1) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			Map<String, Integer> wParams = new HashMap<String, Integer>();

			wParams.put("sha1", StringUtils.parseInt(wSha1));

			wResult.Result = QMSUtils.getInstance().getSignature(wParams, QMSUtils.appId, QMSUtils.appSecret);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> TCM_fileinfo(String wSignature, String wAppid, String wSha1) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			String wUrl = getUrl(wSha1);
			String wFileName = getFileName(wUrl);
			TCMFileResponse wResponse;
			if (StringUtils.isEmpty(wUrl) || StringUtils.isEmpty(wFileName)) {
				wResponse = new TCMFileResponse(40010008, wSha1, wFileName, "localfile", wUrl, true, 0, "", "",
						"appid不存在：" + wAppid, "");
			} else {
				wResponse = new TCMFileResponse(200, wSha1, wFileName, "localfile", wUrl, true, 0, "", "", "", "");
			}
			wResult.Result = JSON.toJSONString(wResponse);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	/**
	 * 获取文件名称
	 */
	private String getFileName(String wUrl) {
		String wResult = "";
		try {
			wResult = wUrl.substring(wUrl.lastIndexOf("/") + 1);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	/**
	 * 根据sha1获取文件访问路径
	 */
	private String getUrl(String wSha1) {
		String wResult = "";
		try {
			int wID = StringUtils.parseInt(wSha1);
			if (wID <= 0) {
				return wResult;
			}

			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);
			wResult = FPCCommonFileDAO.getInstance().SelectStepSopUrl(BaseDAO.SysAdmin, wID, wErrorCode);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> TCM_UpdateOldMaterial(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①查询变更类型为5，OldMaterialNumber为0的数据
			List<TCMMaterialChangeItems> wList = TCMMaterialChangeItemsDAO.getInstance().SelectListNoNumber(wLoginUser,
					wErrorCode);
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList) {
				// ②遍历查询标准bomID
				String[] wStr = wTCMMaterialChangeItems.BOMNo1.split(",");
				if (wStr.length <= 0) {
					continue;
				}

				int wBOMID = TCMMaterialChangeItemsDAO.getInstance().SelectBOMID(wLoginUser, wStr[0], wErrorCode);
				if (wBOMID <= 0) {
					continue;
				}
				// ③根据标准bomID，工位、工序、物料、属性查询MaterialNumber
				double wMaterialNumber = TCMMaterialChangeItemsDAO.getInstance().SelectMaterialNumber(wLoginUser,
						wBOMID, wTCMMaterialChangeItems.PlaceID, wTCMMaterialChangeItems.PartPointID,
						wTCMMaterialChangeItems.MaterialID, wTCMMaterialChangeItems.ReplaceType,
						wTCMMaterialChangeItems.OutsourceType, wErrorCode);
				if (wMaterialNumber <= 0) {
					continue;
				}
				// ④赋值,更新
				TCMMaterialChangeItems wItem = TCMMaterialChangeItemsDAO.getInstance().SelectByID(wLoginUser,
						wTCMMaterialChangeItems.ID, wErrorCode);
				wItem.OldMaterialNumber = wMaterialNumber;
				TCMMaterialChangeItemsDAO.getInstance().Update(wLoginUser, wItem, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> TCM_UpdateOldProperty(BMSEmployee wLoginUser) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①查询changetype=6且，oldreplacetype=0且oldoutsourcetype=0的ID集合
			List<Integer> wIDList = TCMMaterialChangeItemsDAO.getInstance().GetOldPropertyIDList(wLoginUser,
					wErrorCode);
			// ②根据ID获取oldreplacetype，oldoutsourcetype
			for (int wID : wIDList) {
				TCMMaterialChangeItems wItem = TCMMaterialChangeItemsDAO.getInstance().GetOldProperty(wLoginUser, wID,
						wErrorCode);
				// ③根据ID更新oldreplacetype，oldoutsourcetype属性
				TCMMaterialChangeItemsDAO.getInstance().UpdateOldProperty(wLoginUser, wID, wItem, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> TCM_FilePreview(BMSEmployee wLoginUser, String wFTPPath) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ①文件下载到本地
			String wOldFileName = wFTPPath.replace("ftp://10.200.1.51", "");

			String wTarDir = Constants.getConfigPath();

			String wNewName = wFTPPath.substring(wFTPPath.lastIndexOf('/') + 1);

			FTPClient ftpClient = MESFileUtils.ftpConnection("10.200.1.51", "21", "ftpadmin", "ftpadmin");
			boolean flag = MESFileUtils.downFile(ftpClient, wNewName, wOldFileName, wTarDir);
			MESFileUtils.close(ftpClient);
			if (flag) {
				// ②文件上传到预览服务器
				String strUrl = StringUtils.Format("{0}{1}", wTarDir, wNewName);
				File file = new File(strUrl);
				InputStream inputStream = new FileInputStream(file);
				MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);

				JSONObject wObject = MESFileUtils.sendPostWithFile("http://10.200.1.29:8261/api/v1/xview/file/upload",
						multipartFile, new HashMap<String, Object>());
				TCMPreviewFile wPreviewFile = CloneTool.Clone(wObject, TCMPreviewFile.class);
				wResult.Result = wPreviewFile.url;
				// ③删除本地文件
				if (file.exists()) {
					file.delete();
				}
			} else {
				wResult.FaultCode += "提示：下载文件失败!请检查服务器10.200.1.51:21是否正常!";
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<String> TCM_FilePreviewLocalFile(BMSEmployee wLoginUser, String wPathUrl) {
		ServiceResult<String> wResult = new ServiceResult<String>();
		wResult.Result = "";
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			// ②文件上传到预览服务器
			String staticPath = Constants.getConfigPath();
			String strUrl = StringUtils.Format("{0}{1}",
					staticPath.replace("MESQMS", "MESCore").replace("config/", "static"), wPathUrl);
			File file = new File(strUrl);
			if (file.exists()) {
				InputStream inputStream = new FileInputStream(file);
				MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);

				JSONObject wObject = MESFileUtils.sendPostWithFile("http://10.200.1.29:8261/api/v1/xview/file/upload",
						multipartFile, new HashMap<String, Object>());
				TCMPreviewFile wPreviewFile = CloneTool.Clone(wObject, TCMPreviewFile.class);
				wResult.Result = wPreviewFile.url;
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> TCM_CopyItem(BMSEmployee wLoginUser, int wSourceID, int wTargetID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			List<TCMMaterialChangeItems> wList = TCMMaterialChangeItemsDAO.getInstance().SelectList(wLoginUser, -1,
					wSourceID, -1, wErrorCode);
			for (TCMMaterialChangeItems wTCMMaterialChangeItems : wList) {
				if (wTCMMaterialChangeItems.ChangeType == 3 || wTCMMaterialChangeItems.ChangeType == 4
						|| wTCMMaterialChangeItems.ChangeType == 7 || wTCMMaterialChangeItems.ChangeType == 8) {
					continue;
				}

				wTCMMaterialChangeItems.ID = 0;
				wTCMMaterialChangeItems.ChangeLogID = wTargetID;
				TCMMaterialChangeItemsDAO.getInstance().Update(wLoginUser, wTCMMaterialChangeItems, wErrorCode);
			}

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public ServiceResult<Integer> TCM_ReSendNoticeList(BMSEmployee wLoginUser, int wNoticeID) {
		ServiceResult<Integer> wResult = new ServiceResult<Integer>(0);
		try {
			OutResult<Integer> wErrorCode = new OutResult<Integer>(0);

			TCMTechChangeNotice wTask = (TCMTechChangeNotice) TCMTechChangeNoticeDAO.getInstance()
					.BPM_GetTaskInfo(wLoginUser, wNoticeID, "", wErrorCode);

			if (wTask != null && wTask.ID > 0)
				RSMServiceImpl.getInstance().HandleTechCahnge(wLoginUser, wTask);

			wResult.setFaultCode(MESException.getEnumType(wErrorCode.Result).getLable());
		} catch (Exception e) {
			wResult.FaultCode += e.toString();
			logger.error(e.toString());
		}
		return wResult;
	}
}
