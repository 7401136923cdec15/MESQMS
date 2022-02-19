package com.mes.qms.server.serviceimpl.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import com.mes.qms.server.service.mesenum.APSShiftPeriod;
import com.mes.qms.server.service.mesenum.FMCShiftLevel;
import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.mesenum.MESException;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.fmc.FMCShift;
import com.mes.qms.server.service.po.fmc.FMCWorkDay;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.CoreServiceImpl;
import com.mes.qms.server.serviceimpl.FMCServiceImpl;
import com.mes.qms.server.shristool.LoggerTool;
import com.mes.qms.server.utils.RetCode;

public class MESServer {

	public static List<MESInstance> MESInstanceList = new ArrayList<MESInstance>();

	public static int Instance = 0; // 0:单机版,1000,网络版

	public static boolean ERPEnable = false;

	public static int LoggerDays = 120;
	public static int ExceptionDays = 120; 

	public static ServiceResult<String> MES_GetDatabaseName(int wID, MESDBSource wDBSource) {
		ServiceResult<String> wInstance = new ServiceResult<String>();

		wInstance.Result = MESDBSource.Basic.getDBName();
		try {
			if (wID < 1) {
				wInstance.Result = wDBSource.getDBName();
			}
			for (MESInstance wItem : MESServer.MESInstanceList) {
				if (wItem.ID == wID) {
					switch (wDBSource) {
					case Instance:
						wInstance.Result = wDBSource.getDBName();
						break;
					case Basic:
						wInstance.Result = wItem.BasicDB;
						break;
					default:
						wInstance.Result = wDBSource.getDBName();
						break;
					}
					break;
				}
			}
			/*
			 * switch (MESServer.DBType) { case MySQL: break; case SQLServer:
			 * wInstance.Result = StringUtils.Format("{0}.dbo", wInstance.Result); break;
			 * default: break; }
			 */
		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_GetDatabaseName", "Function Exception:" + ex.toString());
			wInstance.ErrorCode = MESException.DBInstance.getValue();
		}
		return wInstance;
	}

	public static boolean BMS_CheckPowerByAuthorityID(int wCompanyID, int wLoginID, int wFunctionID, int wRangeID,
			int wTypeID) {
		try {
			APIResult wAPIResult = CoreServiceImpl.getInstance().BMS_CheckPowerByAuthorityID(wCompanyID, wLoginID,
					wFunctionID, 0, 0);
			if (wAPIResult != null && wAPIResult.getResultCode() == RetCode.SERVER_CODE_SUC) {
				return wAPIResult.Info(Boolean.class);
			}
		} catch (Exception e) {
			LoggerTool.SaveException("MESServer", "BMS_CheckPowerByAuthorityID", "Function Exception:" + e.toString());
		}
		return false;
	}

	public static ServiceResult<String> MES_GetDatabaseName(int wCompanyID, MESDBSource wMESDBSource, int wLoginID,
			int wFunctionID) {
		ServiceResult<String> wInstance = new ServiceResult<String>();
		wInstance.Result = MESDBSource.Basic.getDBName();
		try {
			if (wCompanyID < 1) {
				wInstance.Result = wMESDBSource.getDBName();
			}
			for (MESInstance wItem : MESServer.MESInstanceList) {
				if (wItem.ID == wCompanyID) {
					switch (wMESDBSource) {
					case Instance:
						wInstance.Result = wMESDBSource.getDBName();
						break;
					case Basic:
						wInstance.Result = wItem.BasicDB;
						break;
					default:
						break;
					}
					break;
				}
			}
			/*
			 * switch (MESServer.DBType) { case MySQL: break; case SQLServer:
			 * wInstance.Result = StringUtils.Format("{0}.dbo", wInstance.Result); break;
			 * default: break; }
			 */
			if (wInstance.Result.length() > 5 && wFunctionID > 0) {
				OutResult<Integer> wErrorCode = new OutResult<Integer>();
				wErrorCode.set(0);
				boolean wPower = BMS_CheckPowerByAuthorityID(wCompanyID, wLoginID, wFunctionID, 0, 0);
				if (!wPower)
					wInstance.ErrorCode = MESException.UnPower.getValue();
			}
		} catch (Exception ex) {
			wInstance.ErrorCode = MESException.DBInstance.getValue();
			LoggerTool.SaveException("MESServer", "MES_GetDatabaseName", "Function Exception:" + ex.toString());
		}
		return wInstance;
	}

	public static int MES_CheckPowerByFunctionID(int wCompanyID, int wLoginID, int wFunctionID) {
		OutResult<Integer> wErrorCode = new OutResult<Integer>();
		try {

			wErrorCode.set(0);
			boolean wPower = BMS_CheckPowerByAuthorityID(wCompanyID, wLoginID, wFunctionID, 0, 0);
			if (!wPower)
				wErrorCode.Result = MESException.UnPower.getValue();
		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_CheckPowerByFunctionID", "Function Exception:" + ex.toString());
		}
		return wErrorCode.Result;
	}

	public static Calendar MES_GetShiftTimeByShiftID(int wCompanyID, int wShiftID) {
		Calendar wShiftTime = Calendar.getInstance();
		try {
			int wYear = wShiftID / 1000000;
			int wMonth = (wShiftID / 10000) % 100;
			int wDay = (wShiftID / 100) % 100;
			wShiftTime.set(Calendar.YEAR, wYear);
			wShiftTime.set(Calendar.MONTH, wMonth);
			wShiftTime.set(Calendar.DATE, wDay);
		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_GetShiftTimeByShiftID", "Function Exception:" + ex.toString());
		}
		return wShiftTime;
	}

	private static Calendar MES_QueryMondayByDate(Calendar wShiftTime) {
		Calendar wMonday = wShiftTime;
		try {
			if (wMonday.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				wMonday.add(Calendar.DATE, -1);
			}
			wMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_QueryMondayByDate", "Function Exception:" + ex.toString());
		}
		return wMonday;
	}

	// 需要有一个班次记录表，

	public static int MES_QueryShiftID(BMSEmployee wLoginUser, int wWorkShopID, Calendar wShiftTime,
			APSShiftPeriod wShiftPeriod, FMCShiftLevel wZoneID) {
		int wShiftID = 0;
		try {
			if (wShiftTime == null) {
				wShiftTime = Calendar.getInstance();
			} else {
				Calendar wBaseTime = Calendar.getInstance();
				wBaseTime.set(2000, 0, 1);

				if (wBaseTime.compareTo(wShiftTime) > 0)
					wShiftTime = Calendar.getInstance();
			}
			switch (wShiftPeriod) {
			case Minute:
				wShiftTime.add(Calendar.MINUTE, 0);
				break;
			case Hour:
				wShiftTime.add(Calendar.HOUR_OF_DAY, 0);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMMddHH"));
				break;
			case Shift:

				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime,
						"yyyyMMdd" + wWorkShopID%10 + "0"));
				wShiftID = wShiftID + wZoneID.getValue();

				break;
			case Day:
				wShiftTime.add(Calendar.DAY_OF_MONTH, 0);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMMdd"));
				break;
			case Week:
				wShiftTime.add(Calendar.WEEK_OF_MONTH, 0);
				wShiftTime = MESServer.MES_QueryMondayByDate(wShiftTime);
				int wWeekOfYear = wShiftTime.get(Calendar.WEEK_OF_YEAR);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMM40"));
				wShiftID = wShiftID + wWeekOfYear;
				break;
			case Month:
				wShiftTime.add(Calendar.MONTH, 0);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMM"));
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LoggerTool.SaveException("MESServer", "MES_QueryShiftID", "Function Exception:" + ex.toString());
		}
		return wShiftID;
	}

	public static int MES_QueryShiftID(BMSEmployee wLoginUser, int wWorkShopID, Calendar wShiftTime,
			APSShiftPeriod wShiftPeriod, int wShifts) {
		int wShiftID = 0;
		try {
			if (wShiftTime == null) {
				wShiftTime = Calendar.getInstance();
			} else {
				Calendar wBaseTime = Calendar.getInstance();
				wBaseTime.set(2000, 0, 1);

				if (wBaseTime.compareTo(wShiftTime) > 0)
					wShiftTime = Calendar.getInstance();
			}

			switch (wShiftPeriod) {
			case Minute:
				wShiftTime.add(Calendar.MINUTE, wShifts);
				break;
			case Hour:
				wShiftTime.add(Calendar.HOUR_OF_DAY, wShifts);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMMddHH"));
				break;
			case Shift:
				FMCWorkDay wWorkDayDB = FMCServiceImpl.getInstance()
						.FMC_QueryActiveWorkDayByWorkShop(wLoginUser, wWorkShopID).Info(FMCWorkDay.class);

				if (wWorkDayDB == null) {
					wWorkDayDB = new FMCWorkDay();
				}

				int wZoneID = MES_QueryShiftLevelIndex(wWorkDayDB);

				if (wShifts == 0) {
					wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime,
							"yyyyMMdd" + wWorkShopID%10 + "0"));
					wShiftID = wShiftID + wWorkDayDB.ShiftList.get(wZoneID).LevelID;
				} else {
					wShifts += wZoneID;

					int wDayShift = wShifts / wWorkDayDB.ShiftList.size();
					wShifts = wShifts % wWorkDayDB.ShiftList.size();
					wShiftTime.add(Calendar.DAY_OF_MONTH, wDayShift);
					if (wShifts < 0) {
						wShiftTime.add(Calendar.DAY_OF_MONTH, -1);
						wShifts += wWorkDayDB.ShiftList.size();
					}
					wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMMdd"+ wWorkShopID%10 +"0"));
					wShiftID = wShiftID + wWorkDayDB.ShiftList.get(wShifts).LevelID;
				}

				break;
			case Day:
				wShiftTime.add(Calendar.DAY_OF_MONTH, wShifts);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMMdd"));
				break;
			case Week:
				wShiftTime.add(Calendar.WEEK_OF_MONTH, wShifts);
				wShiftTime = MESServer.MES_QueryMondayByDate(wShiftTime);
				int wWeekOfYear = wShiftTime.get(Calendar.WEEK_OF_YEAR);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMM40"));
				wShiftID = wShiftID + wWeekOfYear;
				break;
			case Month:
				wShiftTime.add(Calendar.MONTH, wShifts);
				wShiftID = Integer.parseInt(StringUtils.parseCalendarToString(wShiftTime, "yyyyMM"));
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LoggerTool.SaveException("MESServer", "MES_QueryShiftID", "Function Exception:" + ex.toString());
		}
		return wShiftID;
	}

	private static int MES_QueryShiftLevelIndex(FMCWorkDay wWorkDayDB) {
		int wIndex = 0;
		try {
			Calendar wNow = Calendar.getInstance();

			Calendar wEndTime = Calendar.getInstance();

			if (wWorkDayDB.ShiftList == null) {
				wWorkDayDB.ShiftList = new ArrayList<FMCShift>();
			}
			if (wWorkDayDB.ShiftList.size() <= 0) {

				FMCShift wFMCShift = new FMCShift();
				wFMCShift.StartTime = Calendar.getInstance();
				wFMCShift.StartTime.set(Calendar.HOUR_OF_DAY, 0);
				wFMCShift.StartTime.set(Calendar.MINUTE, 0);
				wFMCShift.StartTime.set(Calendar.SECOND, 0);
				wFMCShift.Minutes = 24 * 60;
				wFMCShift.LevelID = FMCShiftLevel.MinValue();
				wWorkDayDB.ShiftList.add(wFMCShift);
			}
			wWorkDayDB.ShiftList.sort(Comparator.comparing(p -> p.LevelID));

			for (int i = 0; i < wWorkDayDB.ShiftList.size(); i++) {
				FMCShift wShift = wWorkDayDB.ShiftList.get(i);

				wEndTime = (Calendar) wShift.StartTime.clone();
				wEndTime.add(Calendar.MINUTE, wShift.Minutes);

				wNow.set(Calendar.YEAR, wShift.StartTime.get(Calendar.YEAR));
				wNow.set(Calendar.MONTH, wShift.StartTime.get(Calendar.MONTH));
				wNow.set(Calendar.DATE, wShift.StartTime.get(Calendar.DATE));

				if (wNow.compareTo(wShift.StartTime) >= 0 && wNow.compareTo(wEndTime) <= 0) {
					wIndex = i;
					break;
				}

				if (wNow.compareTo(wShift.StartTime) <= 0) {
					wNow.add(Calendar.DAY_OF_MONTH, 1);
					if (wNow.compareTo(wShift.StartTime) >= 0 && wNow.compareTo(wEndTime) <= 0) {
						wIndex = i;
						break;
					}
				} else if (wNow.compareTo(wEndTime) >= 0) {
					wNow.add(Calendar.DAY_OF_MONTH, -1);
					if (wNow.compareTo(wShift.StartTime) >= 0 && wNow.compareTo(wEndTime) <= 0) {
						wIndex = i;
						break;
					}
				}
			}

		} catch (Exception ex) {
			LoggerTool.SaveException("MESServer", "MES_QueryShiftLevel", "Function Exception:" + ex.toString());
		}
		return wIndex;
	}

}
