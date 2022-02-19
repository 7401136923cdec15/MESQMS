package com.mes.qms.server.service;

import java.util.List;

import com.mes.qms.server.service.po.APIResult;
import com.mes.qms.server.service.po.aps.APSBOMItem;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.po.mss.MSSBOM;
import com.mes.qms.server.service.po.mss.MSSBOMItem;
import com.mes.qms.server.service.po.mss.MSSMaterial;
import com.mes.qms.server.service.po.mss.MSSSpotTask;
import com.mes.qms.server.utils.Configuration;

public interface WMSService {

	static String ServerUrl = Configuration.readConfigString("wms.server.url", "config/config");

	static String ServerName = Configuration.readConfigString("wms.server.project.name", "config/config");

	APIResult MSS_QueryBOM(BMSEmployee wLoginUser, int wBOMID, String wBOMNo);

	APIResult MSS_SaveBOM(BMSEmployee wLoginUser, MSSBOM wMSSBOM);

	APIResult MSS_SaveBOMItem(BMSEmployee wLoginUser, MSSBOMItem wMSSBOMItem);

	APIResult MSS_QueryBOMAll(BMSEmployee wLoginUser, String wName, String wBOMNo, int wWorkShopID, int wBOMType,
			int wProductID, int wStatus);

	APIResult MSS_QueryBOMItemAll(BMSEmployee wLoginUser, int wBOMID, int wLineID, int wProductID, int wCustomerID,
			int wPlaceID, int wPartPointID, int wBOMType, int wReplaceType);

	APIResult MSS_QueryBOMItemByID(BMSEmployee wLoginUser, int wBOMItemID);

	APIResult MSS_QueryUnitList(BMSEmployee wLoginUser);

	APIResult MSS_QueryMaterialList(BMSEmployee wLoginUser, String wMaterialNo);

	APIResult MSS_QueryMaterialByID(BMSEmployee wLoginUser, int wMaterialID, String wMaterialNo);

	APIResult MSS_SaveMaterial(BMSEmployee wLoginUser, MSSMaterial wMSSMaterial);

	APIResult APS_SaveBOMItemList(BMSEmployee wLoginUser, List<APSBOMItem> wAPSBOMItemList);

	APIResult MSS_ConfigAll(BMSEmployee wLoginUser, String wPartConfigNo, String wPartConfigName, int wActive,
			String wProductNo, int wCustomerID, int wLineID);

	APIResult APS_OrderStart(BMSEmployee wLoginUser, int wLineID, int wProductID, int wCustomerID, int wOrderID,
			String wWBSNo, String wPartNo, int wRouteID);

	APIResult APS_BOMItemUpdate(BMSEmployee wLoginUser, APSBOMItem wAPSBOMItem);

	APIResult MSS_SaveMaterialList(BMSEmployee wLoginUser, List<MSSMaterial> wMSSMaterialList);

	APIResult MSS_SaveBOMItemList(BMSEmployee wLoginUser, List<MSSBOMItem> wMSSBOMItemList);

	APIResult MSS_CreateSpotTask(BMSEmployee wLoginUser, MSSSpotTask wData);

	APIResult MSS_QuerySpotTask(BMSEmployee wLoginUser, int wTaskStepID, int wTypeID);
}
