package com.mes.qms.server.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.mes.qms.server.service.WMSService;
import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.mss.MSSBOM;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.mss.MSSMaterial;
import com.mes.qms.server.service.po.mss.MSSSpotTask;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.utils.RemoteInvokeUtils;

public class WMSServiceImpl implements WMSService {

	private static Logger logger = LoggerFactory.getLogger(WMSServiceImpl.class);

	public WMSServiceImpl() {
		super();
	}

	private static WMSService Instance;

	public static WMSService getInstance() {
		if (Instance == null)
			Instance = new WMSServiceImpl();
		return Instance;
	}

	@Override
	public APIResult MSS_QueryBOM(BMSEmployee wLoginUser, int wBOMID, String wBOMNo) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("bom_no", wBOMNo);
			wParms.put("bom_id", wBOMID);
			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Bom/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_QueryBOMAll(BMSEmployee wLoginUser, String wName, String wBOMNo, int wWorkShopID, int wBOMType,
			int wProductID, int wStatus) {
		;
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wParms.put("bom_no", wBOMNo);
			wParms.put("bom_name", wBOMNo);
			wParms.put("workshop_id", wWorkShopID);
			wParms.put("type_id", wBOMType);
			wParms.put("ProductID", wProductID);
			wParms.put("status", wStatus);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName, StringUtils
					.Format("api/Bom/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(), wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_QueryUnitList(BMSEmployee wLoginUser) {
		// TODO Auto-generated method stub
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Unit/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_QueryMaterialList(BMSEmployee wLoginUser, String wMaterialNo) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("material_no", wMaterialNo);
			wParms.put("material_name", "");
			wParms.put("type_id", -1);
			wParms.put("status", -1);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Material/All?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_SaveBOM(BMSEmployee wLoginUser, MSSBOM wMSSBOM) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wMSSBOM);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Bom/Update?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_SaveBOMItem(BMSEmployee wLoginUser, MSSBOMItem wMSSBOMItem) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wMSSBOMItem);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/BomItem/Update?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_SaveBOMItemList(BMSEmployee wLoginUser, List<MSSBOMItem> wMSSBOMItemList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wMSSBOMItemList);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/BomItem/UpdateList?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_SaveMaterial(BMSEmployee wLoginUser, MSSMaterial wMSSMaterial) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wMSSMaterial);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Material/Update?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_SaveMaterialList(BMSEmployee wLoginUser, List<MSSMaterial> wMSSMaterialList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wMSSMaterialList);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Material/UpdateList?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_QueryBOMItemAll(BMSEmployee wLoginUser, int wBOMID, int wLineID, int wProductID,
			int wCustomerID, int wPlaceID, int wPartPointID, int wBOMType, int wReplaceType) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();
			wParms.put("bom_id", Integer.valueOf(wBOMID));
			wParms.put("PlaceID", Integer.valueOf(-1));
			wParms.put("LineID", Integer.valueOf(-1));
			wParms.put("ProductID", Integer.valueOf(-1));
			wParms.put("CustomerID", Integer.valueOf(-1));
			wParms.put("ReplaceType", Integer.valueOf(-1));
			wParms.put("BOMType", Integer.valueOf(-1));
			wParms.put("PartPointID", Integer.valueOf(-1));
			wParms.put("OutSourceType", Integer.valueOf(-1));
			wParms.put("IsList", true);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/BomItem/All?cadv_ao={0}&cade_po={1}",
							new Object[] { wLoginUser.getLoginName(), wLoginUser.getPassword() }),
					wParms, HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_QueryBOMItemByID(BMSEmployee wLoginUser, int wBOMItemID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("id", wBOMItemID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/BomItem/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_QueryMaterialByID(BMSEmployee wLoginUser, int wMaterialID, String wMaterialNo) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("material_id", wMaterialID);
			wParms.put("MaterialNo", wMaterialNo);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/Material/Info?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.GET);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_SaveBOMItemList(BMSEmployee wLoginUser, List<APSBOMItem> wAPSBOMItemList) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wAPSBOMItemList);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/APSBOM/Create?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_OrderStart(BMSEmployee wLoginUser, int wLineID, int wProductID, int wCustomerID, int wOrderID,
			String wWBSNo, String wPartNo, int wRouteID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("LineID", wLineID);
			wParms.put("ProductID", wProductID);
			wParms.put("CustomerID", wCustomerID);
			wParms.put("OrderID", wOrderID);
			wParms.put("WBSNo", wWBSNo);
			wParms.put("PartNo", wPartNo);
			wParms.put("RouteID", wRouteID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/APSBOM/OrderStart?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult APS_BOMItemUpdate(BMSEmployee wLoginUser, APSBOMItem wAPSBOMItem) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<String, Object>();
			wParms.put("data", wAPSBOMItem);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/APSBOM/Update?cadv_ao={0}&cade_po={1}", wLoginUser.getLoginName(),
							wLoginUser.getPassword()),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	public APIResult MSS_ConfigAll(BMSEmployee wLoginUser, String wPartConfigNo, String wPartConfigName, int wActive,
			String wProductNo, int wCustomerID, int wLineID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();
			wParms.put("PartConfigNo", wPartConfigNo);
			wParms.put("PartConfigName", wPartConfigName);
			wParms.put("Active", Integer.valueOf(wActive));
			wParms.put("ProductNo", wProductNo);
			wParms.put("CustomerID", Integer.valueOf(wCustomerID));
			wParms.put("LineID", Integer.valueOf(wLineID));

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/MSSPart/ConfigAll?cadv_ao={0}&cade_po={1}",
							new Object[] { wLoginUser.getLoginName(), wLoginUser.getPassword() }),
					wParms, HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_CreateSpotTask(BMSEmployee wLoginUser, MSSSpotTask wData) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();
			wParms.put("data", wData);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/MSSSpotTask/Create?cadv_ao={0}&cade_po={1}",
							new Object[] { wLoginUser.getLoginName(), wLoginUser.getPassword() }),
					wParms, HttpMethod.POST);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}

	@Override
	public APIResult MSS_QuerySpotTask(BMSEmployee wLoginUser, int wTaskStepID, int wTypeID) {
		APIResult wResult = new APIResult();
		try {
			Map<String, Object> wParms = new HashMap<>();
			wParms.put("TaskStepID", wTaskStepID);
			wParms.put("TypeID", wTypeID);

			wResult = RemoteInvokeUtils.getInstance().HttpInvokeAPI(ServerUrl, ServerName,
					StringUtils.Format("api/MSSSpotTask/Info?cadv_ao={0}&cade_po={1}",
							new Object[] { wLoginUser.getLoginName(), wLoginUser.getPassword() }),
					wParms, HttpMethod.GET);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return wResult;
	}
}
