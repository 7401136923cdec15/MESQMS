package com.mes.qms.server.serviceimpl.utils.qms;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mes.qms.server.service.mesenum.MESDBSource;
import com.mes.qms.server.service.po.OutResult;
import com.mes.qms.server.service.po.ServiceResult;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.ipt.IPTItem;
import com.mes.qms.server.service.po.ipt.IPTProblemBomItem;
import com.mes.qms.server.service.po.ipt.IPTSOP;
import com.mes.qms.server.service.po.ipt.IPTValue;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.dao.BaseDAO;

public class SQLStringUtils extends BaseDAO {
	private static Logger logger = LoggerFactory.getLogger(SQLStringUtils.class);

	private static SQLStringUtils Instance;

	private SQLStringUtils() {
		super();
	}

	public static SQLStringUtils getInstance() {
		if (Instance == null)
			Instance = new SQLStringUtils();
		return Instance;
	}

	public String InsertItem(BMSEmployee wLoginUser, List<IPTItem> wIPTItemList, long wVID,
			OutResult<Integer> wErrorCode) {
		String wResultStr = "";
		try {
			List<String> wResult = new ArrayList<>();

			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return "";
			}

			if (wIPTItemList == null || wIPTItemList.size() < 1)
				return "";

			// 创建编码
			SetCode(wLoginUser, wIPTItemList);

			String wResultTemp = "INSERT INTO {0}.ipt_itemrecord (Text,StandardType,"
					+ "StandardValue,DefaultValue,StandardLeft,"
					+ "StandardRight,Standard,UnitID,Unit,Visiable,ValueSource,VID,"
					+ "StandardBasic,ItemType,IPTSOPList,Details,Process,GroupID,DefaultStationID,"
					+ "OtherValue,DefaultManufactor,DefaultModal,DefaultNumber,IsShowStandard,Legend,"
					+ "SerialNumber,Code,DefaultPartPointID,IsWriteFill,IsPictureFill,IsVideoFill,"
					+ "ManufactorOption,ModalOption,IsManufactorFill,IsModalFill,"
					+ "IsPeriodChange,IsNumberFill,ProjectNo,CheckPoint,IsQuality,PartsCoding,"
					+ "OrderID,ConfigID,AutoCalc,WorkContent,WorkTime,FileNames,OrderType,"
					+ "DisassemblyComponents,RepairParts,AssemblyParts,Components,"
					+ "DisassemblyComponentsID,RepairPartsID,AssemblyPartsID,ComponentsID,"
					+ "SRDisassemblyComponentsID,SRDisassemblyComponents,SRRepairPartsID,SRRepairParts,"
					+ "SRAssemblyPartsID,SRAssemblyParts) VALUES {1};";

			String wValueTemplate = "(''{0}'',{1},''{2}'' ,''{3}'',{4},{5},''{6}'',''{7}'',''{8}'',"
					+ "{9},''{10}'',{11},''{12}'',{13},''{14}'',''{15}'',''{16}'',"
					+ "{17},{18},{19},''{20}'',''{21}'',''{22}'',{23},''{24}'',"
					+ "''{25}'',''{26}'',{27},{28},{29},{30},''{31}'',''{32}'',{33},{34},{35},{36},"
					+ "''{37}'',''{38}'',{39},''{40}'',{41},{42},{43},''{44}'',{45},''{46}'',{47},''{48}'',"
					+ "''{49}'',''{50}'',''{51}'',{52},{53},{54},{55},{56},''{57}'',{58},''{59}'',{60},''{61}'')";

			String wSOPIDList = "";
			for (IPTItem wIPTItem : wIPTItemList) {
				// 处理指导书
				if (wIPTItem.IPTSOPList != null && wIPTItem.IPTSOPList.size() > 0) {
					List<Integer> wIDList = new ArrayList<Integer>();
					for (IPTSOP wIPTSOP : wIPTItem.IPTSOPList) {
						wIDList.add(wIPTSOP.ID);
					}
					wSOPIDList = StringUtils.Join(";", wIDList);
				}

				wResult.add(StringUtils.Format(wValueTemplate, wIPTItem.Text, String.valueOf(wIPTItem.StandardType),
						wIPTItem.StandardValue, wIPTItem.DefaultValue, String.valueOf(wIPTItem.StandardLeft),
						String.valueOf(wIPTItem.StandardRight), wIPTItem.Standard, String.valueOf(wIPTItem.UnitID),
						wIPTItem.Unit, wIPTItem.Visiable ? 1 : 0, StringUtils.Join(";", wIPTItem.ValueSource),
						String.valueOf(wVID), String.valueOf(wIPTItem.StandardBaisc), String.valueOf(wIPTItem.ItemType),
						wSOPIDList, wIPTItem.Details, wIPTItem.Process, String.valueOf(wIPTItem.GroupID),
						String.valueOf(wIPTItem.DefaultStationID), String.valueOf(wIPTItem.OtherValue),
						wIPTItem.DefaultManufactor, wIPTItem.DefaultModal, wIPTItem.DefaultNumber,
						String.valueOf(wIPTItem.IsShowStandard), wIPTItem.Legend, wIPTItem.SerialNumber, wIPTItem.Code,
						String.valueOf(wIPTItem.DefaultPartPointID), String.valueOf(wIPTItem.IsWriteFill),
						String.valueOf(wIPTItem.IsPictureFill), String.valueOf(wIPTItem.IsVideoFill),
						StringUtils.Join(";", wIPTItem.ManufactorOption), StringUtils.Join(";", wIPTItem.ModalOption),
						String.valueOf(wIPTItem.IsManufactorFill), String.valueOf(wIPTItem.IsModalFill),
						String.valueOf(wIPTItem.IsPeriodChange), String.valueOf(wIPTItem.IsNumberFill),
						wIPTItem.ProjectNo, wIPTItem.CheckPoint, String.valueOf(wIPTItem.IsQuality),
						wIPTItem.PartsCoding, String.valueOf(wIPTItem.OrderID), String.valueOf(wIPTItem.ConfigID),
						String.valueOf(wIPTItem.AutoCalc), wIPTItem.WorkContent, String.valueOf(wIPTItem.WorkTime),
						wIPTItem.FileNames, String.valueOf(wIPTItem.OrderType), wIPTItem.DisassemblyComponents,
						wIPTItem.RepairParts, wIPTItem.AssemblyParts, wIPTItem.Components,
						String.valueOf(wIPTItem.DisassemblyComponentsID), String.valueOf(wIPTItem.RepairPartsID),
						String.valueOf(wIPTItem.AssemblyPartsID), String.valueOf(wIPTItem.ComponentsID),
						String.valueOf(wIPTItem.SRDisassemblyComponentsID), wIPTItem.SRDisassemblyComponents,
						String.valueOf(wIPTItem.SRRepairPartsID), wIPTItem.SRRepairParts,
						String.valueOf(wIPTItem.SRAssemblyPartsID), wIPTItem.SRAssemblyParts));
			}

			wResultStr = StringUtils.Format(wResultTemp, wInstance.Result, StringUtils.Join(",", wResult));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultStr;
	}

	/**
	 * 设置编码
	 * 
	 * @param wLoginUser
	 * @param wIPTItemList
	 */
	private void SetCode(BMSEmployee wLoginUser, List<IPTItem> wIPTItemList) {
		try {
			DecimalFormat wDecimalFormat = new DecimalFormat("00");
			int wFlag = 1;
			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String wCurrentTime = wSimpleDateFormat.format(Calendar.getInstance().getTime());
			for (IPTItem wIPTItem : wIPTItemList) {
				wIPTItem.Code = StringUtils.Format("{0}{1}", wCurrentTime, wDecimalFormat.format(++wFlag));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
	}

	public String DeleteItem(BMSEmployee wLoginUser, List<IPTItem> wIPTItemList, long wVID,
			OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			List<Long> wIPTItemIDList = new ArrayList<Long>();
			if (wIPTItemList == null || wIPTItemList.size() < 1)
				wIPTItemIDList = StringUtils.parseList(new Long[] { 0L });
			else
				wIPTItemIDList = wIPTItemList.stream().map(p -> p.ID).collect(Collectors.toList());

			String wResultTemp = "DELETE FROM {0}.ipt_itemrecord WHERE {0}.ipt_itemrecord.ID>0 "
					+ "AND {0}.ipt_itemrecord.VID ={1} " + "AND  {0}.ipt_itemrecord.ID  NOT IN ({2}) ;";

			wResult = StringUtils.Format(wResultTemp, wInstance.Result, String.valueOf(wVID),
					StringUtils.Join(",", wIPTItemIDList));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public List<String> UpdateItem(BMSEmployee wLoginUser, List<IPTItem> wItemList, long wVID,
			OutResult<Integer> wErrorCode) {
		List<String> wResult = new ArrayList<String>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wItemList == null || wItemList.size() < 1)
				return wResult;
			String wValueTemplate = " UPDATE  {0}.ipt_itemrecord SET Text =''{1}'' ,StandardType ={2} , "
					+ " StandardValue= ''{3}'' ,  DefaultValue= ''{4}'' ,"
					+ "StandardLeft = {5} , StandardRight={6}, Standard=''{7}'' , Unit = ''{8}''  ,"
					+ "  Visiable = {9} ,ValueSource = ''{10}'' ,VID = {11},"
					+ "StandardBasic = ''{12}'',ItemType={14},IPTSOPList=''{15}'',Details=''{16}'',"
					+ "UnitID={17},Process=''{18}'',GroupID={19},DefaultStationID={20}"
					+ ",OtherValue={21},DefaultManufactor=''{22}'',DefaultModal=''{23}'',"
					+ "DefaultNumber=''{24}'',IsShowStandard={25},Legend=''{26}'',"
					+ "SerialNumber=''{27}'',Code=''{28}'',DefaultPartPointID={29},IsWriteFill={30},"
					+ "IsPictureFill={31},IsVideoFill={32},ManufactorOption=''{33}'',ModalOption=''{34}'',"
					+ "IsManufactorFill={35},IsModalFill={36},IsPeriodChange={37},IsNumberFill={38},"
					+ "ProjectNo=''{39}'',CheckPoint=''{40}'',IsQuality={41},PartsCoding=''{42}'',"
					+ "OrderID={43},ConfigID={44},AutoCalc={45},WorkContent=''{46}'',WorkTime={47},FileNames=''{48}'',"
					+ "OrderType={49},DisassemblyComponents=''{50}'',RepairParts=''{51}'',"
					+ "AssemblyParts=''{52}'',Components=''{53}''," + "DisassemblyComponentsID={54},RepairPartsID={55},"
					+ "AssemblyPartsID={56},"
					+ "ComponentsID={57},SRDisassemblyComponentsID={58},SRDisassemblyComponents=''{59}'',"
					+ "SRRepairPartsID={60},SRRepairParts=''{61}'',SRAssemblyPartsID={62},"
					+ "SRAssemblyParts=''{63}'' " + "WHERE ID = {13} ;";

			String wSOPIDList = "";
			for (IPTItem wIPTItem : wItemList) {
				// 处理指导书
				if (wIPTItem.IPTSOPList != null && wIPTItem.IPTSOPList.size() > 0) {
					List<Integer> wIDList = new ArrayList<Integer>();
					for (IPTSOP wIPTSOP : wIPTItem.IPTSOPList) {
						wIDList.add(wIPTSOP.ID);
					}
					wSOPIDList = StringUtils.Join(";", wIDList);
				}

				wResult.add(StringUtils.Format(wValueTemplate, wInstance.Result, wIPTItem.Text,
						String.valueOf(wIPTItem.StandardType), wIPTItem.StandardValue, wIPTItem.DefaultValue,
						String.valueOf(wIPTItem.StandardLeft), String.valueOf(wIPTItem.StandardRight),
						wIPTItem.Standard, wIPTItem.Unit, wIPTItem.Visiable ? 1 : 0,
						StringUtils.Join(";", wIPTItem.ValueSource), String.valueOf(wVID), wIPTItem.StandardBaisc,
						String.valueOf(wIPTItem.ID), wIPTItem.ItemType, wSOPIDList, wIPTItem.Details, wIPTItem.UnitID,
						wIPTItem.Process, String.valueOf(wIPTItem.GroupID), String.valueOf(wIPTItem.DefaultStationID),
						String.valueOf(wIPTItem.OtherValue), wIPTItem.DefaultManufactor, wIPTItem.DefaultModal,
						wIPTItem.DefaultNumber, String.valueOf(wIPTItem.IsShowStandard), wIPTItem.Legend,
						wIPTItem.SerialNumber, wIPTItem.Code, String.valueOf(wIPTItem.DefaultPartPointID),
						String.valueOf(wIPTItem.IsWriteFill), String.valueOf(wIPTItem.IsPictureFill),
						String.valueOf(wIPTItem.IsVideoFill), StringUtils.Join(";", wIPTItem.ManufactorOption),
						StringUtils.Join(";", wIPTItem.ModalOption), String.valueOf(wIPTItem.IsManufactorFill),
						String.valueOf(wIPTItem.IsModalFill), String.valueOf(wIPTItem.IsPeriodChange),
						String.valueOf(wIPTItem.IsNumberFill), wIPTItem.ProjectNo, wIPTItem.CheckPoint,
						String.valueOf(wIPTItem.IsQuality), wIPTItem.PartsCoding, String.valueOf(wIPTItem.OrderID),
						String.valueOf(wIPTItem.ConfigID), String.valueOf(wIPTItem.AutoCalc), wIPTItem.WorkContent,
						String.valueOf(wIPTItem.WorkTime), wIPTItem.FileNames, String.valueOf(wIPTItem.OrderType),
						wIPTItem.DisassemblyComponents, wIPTItem.RepairParts, wIPTItem.AssemblyParts,
						wIPTItem.Components, String.valueOf(wIPTItem.DisassemblyComponentsID),
						String.valueOf(wIPTItem.RepairPartsID), String.valueOf(wIPTItem.AssemblyPartsID),
						String.valueOf(wIPTItem.ComponentsID), String.valueOf(wIPTItem.SRDisassemblyComponentsID),
						wIPTItem.SRDisassemblyComponents, String.valueOf(wIPTItem.SRRepairPartsID),
						wIPTItem.SRRepairParts, String.valueOf(wIPTItem.SRAssemblyPartsID), wIPTItem.SRAssemblyParts));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public String UpdateStandardCurrent(BMSEmployee wLoginUser, int wCompanyID, int wIPTMode, int wWorkShopID,
			int wLineID, int PartID, int wPartPointID, String wProductNo, int wIsCurrent, int wCustomID,
			OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			wResult = "UPDATE {0}.ipt_standard SET IsCurrent = {1} ,TModify = now()  WHERE CompanyID ={2} "
					+ "and IPTMode ={3} and WorkShopID ={4} and LineID ={5} and PartID ={6} and PartPointID ={7} and ProductNo =''{8}'' and CustomID={9} ;";

			wResult = StringUtils.Format(wResult, wInstance.Result, String.valueOf(wIsCurrent),
					String.valueOf(wCompanyID), String.valueOf(wIPTMode), String.valueOf(wWorkShopID),
					String.valueOf(wLineID), String.valueOf(PartID), String.valueOf(wPartPointID), wProductNo,
					String.valueOf(wCustomID));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public String UpdateStandardCurrent(BMSEmployee wLoginUser, int wCompanyID, int wIPTMode, int wWorkShopID,
			int wLineID, int PartID, int wPartPointID, int wProductID, int wIsCurrent, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			wResult = "UPDATE {0}.ipt_standard SET IsCurrent = {1} ,TModify = now()  WHERE CompanyID ={2} "
					+ "and IPTMode ={3} and WorkShopID ={4} and LineID ={5} and PartID ={6} and PartPointID ={7} and ProductID =''{8}'';";

			wResult = StringUtils.Format(wResult, wInstance.Result, String.valueOf(wIsCurrent),
					String.valueOf(wCompanyID), String.valueOf(wIPTMode), String.valueOf(wWorkShopID),
					String.valueOf(wLineID), String.valueOf(PartID), String.valueOf(wPartPointID),
					String.valueOf(wProductID));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public String DeleteStandardByID(BMSEmployee wLoginUser, long wStandardID, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			wResult = "DELETE FROM {0}.ipt_standard  WHERE ID ={1}  AND IsUsed=0;";

			wResult = StringUtils.Format(wResult, wInstance.Result, String.valueOf(wStandardID));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public String DeleteStandardItemByStandardID(BMSEmployee wLoginUser, long wStandardID,
			OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			wResult = " DELETE FROM {0}.ipt_itemrecord WHERE ID>0 " + "AND VID = {1} "
					+ "AND (select  {0}.ipt_standard.IsUsed " + "FROM {0}.ipt_standard WHERE ID={2})=0;";

			wResult = StringUtils.Format(wResult, wInstance.Result, String.valueOf(wStandardID),
					String.valueOf(wStandardID));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}

		return wResult;
	}

	public String UpdateStandardCurrent(BMSEmployee wLoginUser, long wVID, int wIsCurrent,
			OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			wResult = "UPDATE {0}.ipt_standard SET IsCurrent = {1} ,IsUsed = 1 , TModify = now()  WHERE ID ={2} ;";

			wResult = StringUtils.Format(wResult, wInstance.Result, String.valueOf(wIsCurrent), String.valueOf(wVID));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public String UpdateStandardEnd(BMSEmployee wLoginUser, long wVID, int wIsEnd, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			wResult = "UPDATE {0}.ipt_standard SET IsEnd = {1} ,IsUsed = 1,TModify = now()  WHERE ID ={2} ;";

			wResult = StringUtils.Format(wResult, wInstance.Result, String.valueOf(wIsEnd), String.valueOf(wVID));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public String UpdateStandardDetail(BMSEmployee wLoginUser, long wVID, int wUserID, OutResult<Integer> wErrorCode) {
		String wResult = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			wResult = "UPDATE {0}.ipt_standard SET UserID = {1} , TModify = now()  WHERE ID ={2} ;";

			wResult = StringUtils.Format(wResult, wInstance.Result, String.valueOf(wUserID), String.valueOf(wVID));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	public String InsertIPTValue(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList, int wTaskID, int wIPTMode,
			OutResult<Integer> wErrorCode) {
		String wResultStr = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultStr;
			}

			List<String> wResult = new ArrayList<String>();
			if (wIPTValueList == null || wIPTValueList.size() < 1)
				return "";

			String wResultTemp = " INSERT INTO {0}.ipt_value ( StandardID,IPTItemID,"
					+ "Value,Remark,Result,TaskID,IPTMode," + "ItemType,ImagePath,VideoPath,"
					+ "SolveID,SubmitID,SubmitTime,Manufactor,Modal,Number,Status,IPTProblemBomItemList,"
					+ "OrderID,DisassemblyComponents,RepairParts,AssemblyParts,"
					+ "SRDisassemblyComponents,SRRepairParts,SRAssemblyParts,SRScrapParts,"
					+ "SRLYParts,MaterialID,MaterialNo,MaterialName) VALUES {1};";

			String wValueTemplate = "({0},{1},''{2}'' ,''{3}'',{4},{5} ,{6},{7}," + "''{8}'',''{9}''"
					+ ",{10},{11},''{12}'',''{13}'',''{14}'',''{15}'',{16},''{17}''"
					+ ",{18},''{19}'',''{20}'',''{21}'',''{22}'',''{23}'',''{24}'',''{25}'',''{26}'',{27},''{28}'',''{29}'')";

			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			for (IPTValue wIPTValue : wIPTValueList) {
				wResult.add(StringUtils.Format(wValueTemplate, String.valueOf(wIPTValue.StandardID),
						String.valueOf(wIPTValue.IPTItemID), wIPTValue.Value, wIPTValue.Remark, wIPTValue.Result,
						String.valueOf(wTaskID), wIPTValue.IPTMode, String.valueOf(wIPTValue.ItemType),
						(wIPTValue.ImagePath == null || wIPTValue.ImagePath.size() <= 0) ? ""
								: StringUtils.Join(";", wIPTValue.ImagePath),
						(wIPTValue.VideoPath == null || wIPTValue.VideoPath.size() <= 0) ? ""
								: StringUtils.Join(";", wIPTValue.VideoPath),
						String.valueOf(wIPTValue.SolveID), String.valueOf(wIPTValue.SubmitID),
						wSimpleDateFormat.format(wIPTValue.SubmitTime.getTime()), wIPTValue.Manufactor, wIPTValue.Modal,
						wIPTValue.Number, String.valueOf(wIPTValue.Status),
						IPTProblemBomItem.ListToString(wIPTValue.IPTProblemBomItemList),
						String.valueOf(wIPTValue.OrderID), wIPTValue.DisassemblyComponents, wIPTValue.RepairParts,
						wIPTValue.AssemblyParts, wIPTValue.SRDisassemblyComponents, wIPTValue.SRRepairParts,
						wIPTValue.SRAssemblyParts, wIPTValue.SRScrapParts, wIPTValue.SRLYParts,
						String.valueOf(wIPTValue.MaterialID), wIPTValue.MaterialNo, wIPTValue.MaterialName));
			}

			wResultStr = StringUtils.Format(wResultTemp, wInstance.Result, StringUtils.Join(",", wResult));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultStr;
	}

	public String InsertIPTValue(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList, OutResult<Integer> wErrorCode) {
		String wResultStr = "";
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResultStr;
			}

			List<String> wResult = new ArrayList<String>();
			if (wIPTValueList == null || wIPTValueList.size() < 1)
				return "";

			String wResultTemp = " INSERT INTO {0}.ipt_value ( StandardID,IPTItemID,"
					+ "Value,Remark,Result,TaskID,IPTMode," + "ItemType,ImagePath,VideoPath,SolveID,"
					+ "SubmitID,SubmitTime,Manufactor,Modal,Number,Status,"
					+ "IPTProblemBomItemList,OrderID,DisassemblyComponents," + "RepairParts,AssemblyParts,"
					+ "SRDisassemblyComponents,SRRepairParts,SRAssemblyParts,SRScrapParts,SRLYParts,"
					+ "MaterialID,MaterialNo,MaterialName) VALUES {1};";

			String wValueTemplate = "({0},{1},''{2}'' ,''{3}'',{4},{5} " + ",{6},{7},''{8}'',"
					+ "''{9}'',{10},{11},''{12}'',''{13}'',''{14}'',''{15}'',{16},"
					+ "''{17}'',{18},''{19}'',''{20}'',''{21}'',''{22}'',''{23}'',''{24}'',"
					+ "''{25}'',''{26}'',{27},''{28}'',''{29}'')";

			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			for (IPTValue wIPTValue : wIPTValueList) {
				wResult.add(StringUtils.Format(wValueTemplate, String.valueOf(wIPTValue.StandardID),
						String.valueOf(wIPTValue.IPTItemID), wIPTValue.Value, wIPTValue.Remark, wIPTValue.Result,
						wIPTValue.TaskID, wIPTValue.IPTMode, String.valueOf(wIPTValue.ItemType),
						(wIPTValue.ImagePath == null || wIPTValue.ImagePath.size() <= 0) ? ""
								: StringUtils.Join(";", wIPTValue.ImagePath),
						(wIPTValue.VideoPath == null || wIPTValue.VideoPath.size() <= 0) ? ""
								: StringUtils.Join(";", wIPTValue.VideoPath),
						String.valueOf(wIPTValue.SolveID), String.valueOf(wIPTValue.SubmitID),
						wSimpleDateFormat.format(wIPTValue.SubmitTime.getTime()), wIPTValue.Manufactor, wIPTValue.Modal,
						wIPTValue.Number, String.valueOf(wIPTValue.Status),
						IPTProblemBomItem.ListToString(wIPTValue.IPTProblemBomItemList),
						String.valueOf(wIPTValue.OrderID), wIPTValue.DisassemblyComponents, wIPTValue.RepairParts,
						wIPTValue.AssemblyParts, wIPTValue.SRDisassemblyComponents, wIPTValue.SRRepairParts,
						wIPTValue.SRAssemblyParts, wIPTValue.SRScrapParts, wIPTValue.SRLYParts,
						String.valueOf(wIPTValue.MaterialID), wIPTValue.MaterialNo, wIPTValue.MaterialName));
			}

			wResultStr = StringUtils.Format(wResultTemp, wInstance.Result, StringUtils.Join(",", wResult));
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResultStr;
	}

	public List<String> UpdateIPTValue(BMSEmployee wLoginUser, List<IPTValue> wIPTValueList, int wTaskID, int wIPTMode,
			OutResult<Integer> wErrorCode) {
		List<String> wResult = new ArrayList<String>();
		try {
			ServiceResult<String> wInstance = this.GetDataBaseName(wLoginUser, MESDBSource.EXC, 0);
			wErrorCode.set(wInstance.ErrorCode);
			if (wErrorCode.Result != 0) {
				return wResult;
			}

			if (wIPTValueList == null || wIPTValueList.size() < 1)
				return wResult;

			String wValueTemplate = "  UPDATE {0}.ipt_value SET StandardID = {1},IPTItemID = {2},Value = ''{3}'',"
					+ "Remark =  ''{4}'',Result = {5},TaskID = {6},IPTMode = {7}," + "ItemType={9},ImagePath=''{10}'',"
					+ "VideoPath=''{11}'',SolveID={12},SubmitID={13},"
					+ "SubmitTime=''{14}'',Manufactor = ''{15}'',Modal = ''{16}'',Number = ''{17}''"
					+ ",Status={18},IPTProblemBomItemList={19}," + "OrderID={20},DisassemblyComponents=''{21}'',"
					+ "RepairParts=''{22}'',AssemblyParts=''{23}'',"
					+ "SRDisassemblyComponents=''{24}'',SRRepairParts=''{25}'',SRAssemblyParts=''{26}'',"
					+ "SRScrapParts=''{27}'',SRLYParts=''{28}'',MaterialID={29},MaterialNo=''{30}'',MaterialName=''{31}''"
					+ " WHERE ID = {8}; ";

			SimpleDateFormat wSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			for (IPTValue wIPTValue : wIPTValueList) {
				wResult.add(StringUtils.Format(wValueTemplate, wInstance.Result, String.valueOf(wIPTValue.StandardID),
						String.valueOf(wIPTValue.IPTItemID), wIPTValue.Value, wIPTValue.Remark,
						String.valueOf(wIPTValue.Result), String.valueOf(wTaskID), String.valueOf(wIPTMode),
						String.valueOf(wIPTValue.ID), String.valueOf(wIPTValue.ItemType),
						(wIPTValue.ImagePath == null || wIPTValue.ImagePath.size() <= 0) ? ""
								: StringUtils.Join(";", wIPTValue.ImagePath),
						(wIPTValue.VideoPath == null || wIPTValue.VideoPath.size() <= 0) ? ""
								: StringUtils.Join(";", wIPTValue.VideoPath),
						String.valueOf(wIPTValue.SolveID), String.valueOf(wIPTValue.SubmitID),
						wSimpleDateFormat.format(wIPTValue.SubmitTime.getTime()), wIPTValue.Manufactor, wIPTValue.Modal,
						wIPTValue.Number, String.valueOf(wIPTValue.Status),
						IPTProblemBomItem.ListToString(wIPTValue.IPTProblemBomItemList),
						String.valueOf(wIPTValue.OrderID), wIPTValue.DisassemblyComponents, wIPTValue.RepairParts,
						wIPTValue.AssemblyParts, wIPTValue.SRDisassemblyComponents, wIPTValue.SRRepairParts,
						wIPTValue.SRAssemblyParts, wIPTValue.SRScrapParts, wIPTValue.SRLYParts,
						String.valueOf(wIPTValue.MaterialID), wIPTValue.MaterialNo, wIPTValue.MaterialName));
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}
}
