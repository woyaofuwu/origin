
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.mvelmisc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzy;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPsptTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveQueryBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.TroopMemberInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.label.LabelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductdLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCreditInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePostInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPostInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry; 
import com.ailk.org.apache.commons.lang3.StringUtils;

public class MvelMiscQry
{
	private static final Logger log = Logger.getLogger(MvelMiscQry.class); 
    public static String getBrandNameByBrandCode(String brandCode) throws Exception
    {
        return UBrandInfoQry.getBrandNameByBrandCode(brandCode);
    }

    public static IDataset getCommpara(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {

        return CommparaInfoQry.getCommpara(subsysCode, paramAttr, paramCode, eparchyCode);
    }

    public static IDataset getCommparaByCode(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        return ParamInfoQry.getCommparaByCode(subsysCode, paramAttr, paramCode, eparchyCode);
    }

    public static IDataset getCommparaInfoBy5(String subsysCode, String paramAttr, String paramCode, String paraCode1, String eparchyCode, Pagination pagination) throws Exception
    {
        return CommparaInfoQry.getCommparaInfoBy5(subsysCode, paramAttr, paramCode, paraCode1, eparchyCode, null);
    }

    public static IDataset getCommparaInfos(String subsysCode, String paramAttr, String paramCode) throws Exception
    {
        return CommparaInfoQry.getCommparaInfos(subsysCode, paramAttr, paramCode);
    }

    public static IDataset getCommparaInfosByUserId(String userId) throws Exception
    {
        return CommparaInfoQry.getCommparaInfosByUserId(userId);
    }

    public static IDataset getCommparaInfosByUserId(String userId, String paramCode, String paraCode3) throws Exception
    {
        return CommparaInfoQry.getCommparaInfosByUserId(userId, paramCode, paraCode3);
    }

    public static IDataset getCommPkInfo(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        return CommparaInfoQry.getCommPkInfo(subsysCode, paramAttr, paramCode, eparchyCode);
    }

    public static String getCustManageNameByCustManagerId(String custMangerId) throws Exception
    {
        return UStaffInfoQry.getCustManageNameByCustManagerId(custMangerId);
    }

    public static IDataset getCustomerInfoByUserId(String userId) throws Exception
    {
        return CustomerInfoQry.getCustomerInfoByUserId(userId);
    }

    public static IDataset getDefaultSvcCount(String custId, String serviceId) throws Exception
    {
        return ParamInfoQry.getDefaultSvcCount(custId, serviceId);
    }

    public static IDataset getDiscntInfoByDisCode(String discntCode) throws Exception
    {
        return IDataUtil.idToIds(UDiscntInfoQry.getDiscntInfoByPk(discntCode));
    }

    public static IDataset getDiscntIsValid(String discnt_type_code, String discnt_code) throws Exception
    {
        return DiscntInfoQry.getDiscntIsValid(discnt_type_code, discnt_code);
    }

    public static String getGrpProductNameByPidB(String productIdB) throws Exception
    {
        return ProductMebInfoQry.getGrpProductNameByPidB(productIdB);
    }

    public static IDataset getHintVipInfoBySn(String serialNumber) throws Exception
    {
        return CustVipInfoQry.getHintVipInfoBySn(serialNumber);
    }

    public static IDataset getNpWarningInfo(String userId, String tradeAttr) throws Exception
    {
        return UserInfoQry.getNpWarningInfo(userId, tradeAttr);
    }

    public static IDataset getPackageByPackage(String packageId, String eparchy_code) throws Exception
    {
//        return PkgInfoQry.getPackageByPackage(packageId, eparchy_code);

    	 return UPackageExtInfoQry.queryPackageExtByPackageId("TD_B_PACKAGE_EXT", packageId);
    }
    
    
    
    

    public static IData getPackageByPK(String packageId) throws Exception
    {
        return UPackageInfoQry.getPackageByPK(packageId);

    }

    public static IDataset getPackageExtInfo(String package_id, String eparchy_code) throws Exception
    {
        return PkgExtInfoQry.getPackageExtInfo(package_id, eparchy_code);
    }

    public static String getPackageNameByPackageId(String packageId) throws Exception
    {
        return PkgInfoQry.getPackageNameByPackageId(packageId);
    }

    public static IDataset getPlatDiscntConfigByServiceId(String eparchyCode, String serviceId, String attrCode, String attrValue) throws Exception
    {
        return PlatInfoQry.getPlatDiscntConfigByServiceId(eparchyCode, serviceId, attrCode, attrValue);
    }

    public static IDataset getPlatSvcPintInfos(String tradeId) throws Exception
    {
        return PlatInfoQry.getPlatSvcPintInfos(tradeId);
    }

    public static String getProductNameByProductId(String productId) throws Exception
    {
        return UProductInfoQry.getProductNameByProductId(productId);
    }
    
    public static String getCampnType(String productId) throws Exception
    {
        return LabelInfoQry.getLogicLabelIdByElementId(productId);
    }

    public static IDataset getProductPackagesForSpec(String productId) throws Exception
    {
        return ProductPkgInfoQry.getProductPackagesForSpec(productId);
    }

    public static String getPsptTypeName(String eparchyCode, String psptTypeCode) throws Exception
    {
        return UPsptTypeInfoQry.getPsptTypeName(eparchyCode, psptTypeCode);
    }

    public static IDataset getRelaUUInfoByUserRelarIdB(String userIdB, String relationTypeCode, Pagination page) throws Exception
    {
        return RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(userIdB, relationTypeCode, page);
    }

    public static IDataset getServElementByPk(String package_id, String element_type_code, String element_id) throws Exception
    {
        return PkgElemInfoQry.getServElementByPk(package_id, element_type_code, element_id);
    }
    
    public static IDataset getProdPkgElementByPk(String package_id, String element_type_code, String element_id) throws Exception
    {
    	    	
        return PkgElemInfoQry.getProdPkgElementByPk(package_id, element_type_code, element_id);
    }

    public static IDataset getServInfos(String service_id) throws Exception
    {
        return IDataUtil.idToIds(USvcInfoQry.qryServInfoBySvcId(service_id));
    }

    public static IDataset getSmsMessageByTradeId(String tradeId) throws Exception
    {
        return UserDiscntInfoQry.getSmsMessageByTradeId(tradeId);
    }

    public static IDataset getSmsTemplate(String tradeId, String paramCode) throws Exception
    {
        return CommparaInfoQry.getSmsTemplate(tradeId, paramCode);
    }

    public static IDataset getSnPaymentInfo(String userId, String stateCode) throws Exception
    {
        return UserInfoQry.getSnPaymentInfo(userId, stateCode);
    }

    public static IDataset getSpecElementByTradeId(String tradeId) throws Exception
    {
    	IDataset  tradeElements= TradeInfoQry.getSpecElementByTradeId(tradeId);
    	return UpcCallIntf.qryOffersByPkgEleRsrvTagOfferIds(tradeElements);
    }

    public static IDataset getSpecElementByTradeIdProductId(String tradeId, String productId) throws Exception
    {
		IDataset iupcSpecElements =UpcCall.qryOffersByOfferIdWithPackageElementFilter("P", productId);
		IDataset tradeElements = TradeInfoQry.getSpecElementByTradeIdProductId(tradeId);
		if(IDataUtil.isNotEmpty(iupcSpecElements)&&IDataUtil.isNotEmpty(tradeElements)){
		for (int j = 0; j < iupcSpecElements.size(); j++) {
			IData temp = iupcSpecElements.getData(j);
			for (int k = 0; k < tradeElements.size(); k++) {
				IData tradetemp = tradeElements.getData(j);
				if (temp.getString("ELEMENT_ID").equals(tradetemp.getString("ELEMENT_ID"))&&temp.getString("ELEMENT_TYPE_CODE").equals(tradetemp.getString("ELEMENT_TYPE_CODE"))) {
					iupcSpecElements.remove(j);
					j--;
					break;
				}
			}
		 }
		}
		return iupcSpecElements;
	 }

    public static IDataset getTagInfoBySubSys(String EPARCHY_CODE, String TAG_CODE, String USE_TAG, String SUBSYS_CODE, Pagination page) throws Exception
    {
        return TagInfoQry.getTagInfoBySubSys(EPARCHY_CODE, TAG_CODE, USE_TAG, SUBSYS_CODE, page);
    }

    public static IData getTradeAttrByAttrCodeAndTradeIdAndModifyTag(String tradeId, String modifyTag, String attrCode) throws Exception
    {
        return TradeAttrInfoQry.getTradeAttrByAttrCodeAndTradeIdAndModifyTag(tradeId, modifyTag, attrCode);
    }

    public static IDataset getTradeAttrByTradeIDandAttrCode(String trade_id, String attr_code, Pagination pagination) throws Exception
    {
        return TradeAttrInfoQry.getTradeAttrByTradeIDandAttrCode(trade_id, attr_code, pagination);
    }

    public static IDataset getTradeCustPersonByTradeId(String tradeId) throws Exception
    {
        return TradeCustPersonInfoQry.getTradeCustPersonByTradeId(tradeId);
    }

    public static IDataset getTradeDiscntInfoByTradeId(String strTradeId) throws Exception
    {
        return TradeDiscntInfoQry.getTradeDiscntInfoByTradeId(strTradeId);
    }

    public static IDataset getTradeInfos(String orderId, String tradeStaffId, String serialNumber, String tradeTypeCode) throws Exception
    {
        return TradeInfoQry.getTradeInfos(orderId, tradeStaffId, serialNumber, tradeTypeCode);
    }

    public static IDataset getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(String tradeId, String rsrvValueCode) throws Exception
    {
        return TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId, rsrvValueCode);
    }

    public static IDataset getTradePostInfoByTradeId(String tradeId) throws Exception
    {
        return TradePostInfoQry.getTradePostInfoByTradeId(tradeId);
    }

    public static IDataset getTradeSvcByTradeId(String tradeId) throws Exception
    {
        return TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
    }

    public static IDataset getTradeUURelaByTradeId(String tradeId) throws Exception
    {
        return TradeRelaInfoQry.getTradeUURelaByTradeId(tradeId);
    }

    public static IDataset getUserAcctDays(String userId, String eparchy_code) throws Exception
    {
        return UserAcctDayInfoQry.getUserAcctDays(userId, eparchy_code);
    }

    public static IDataset getUserAttrInfoByAttrCode2(String user_id, String attr_code) throws Exception
    {
        return UserAttrInfoQry.getUserAttrInfoByAttrCode2(user_id, attr_code);// 原mvel传了三个参数
    }

    public static IDataset getUserDiscntsByUserId(String userId, String eparchyCode) throws Exception
    {
        return UserDiscntInfoQry.getUserDiscntsByUserId(userId, eparchyCode);
    }

    public static IDataset getUserInfoByUserId(String user_id, String remove_tag, String eparchyCode) throws Exception
    {
        return UserInfoQry.getUserInfoByUserId(user_id, remove_tag, eparchyCode);
    }

    public static IDataset getUserOther(String userId, String rsrvValueCode) throws Exception
    {
        return UserOtherInfoQry.getUserOther(userId, rsrvValueCode);
    }

    public static IDataset getUserOtherInfo(String user_id, String rsrv_value_code, String partition_id, String rsrv_value) throws Exception
    {
        return UserOtherInfoQry.getUserOtherInfo(user_id, rsrv_value_code, partition_id, partition_id);
    }

    public static IDataset getUserPlatSvc(String userId, String productId, String brandCode, String eparchyCode, String rsrv_str9) throws Exception
    {
    	IDataset userPlatSvcSpInfo =new DatasetList();
    	 IDataset userPlatSvcInfos=UserPlatSvcInfoQry.getUserPlatSvc(userId, productId, brandCode, eparchyCode);
         IDataset userSpInfos=UPlatSvcInfoQry.qryServInfoByrsrvStr(rsrv_str9);
         for(int j=0; j<userPlatSvcInfos.size();j++){
        	 
  	       IData temp = userPlatSvcInfos.getData(j);
  	       
  	      for(int k=0; k<userSpInfos.size();k++){
  	    	     if(temp.getString("SERVICE_ID").equals(userSpInfos.getData(k).getString("SERVICE_ID"))){
  	    	    	temp.putAll(userSpInfos.getData(k));
  	    	    	userPlatSvcSpInfo.add(temp);
  	    	     }
  	      }  
     }
        return userPlatSvcInfos; 
    }

    public static IDataset getUserSaleActive(String userId, String relTradeId) throws Exception
    {
        return UserSaleActiveInfoQry.getUserSaleActive(userId, relTradeId);
    }

    public static IDataset getUserSaleActiveBySelAallActiveReturn(String userId, String relTradeId) throws Exception
    {
        return UserSaleActiveInfoQry.getUserSaleActiveBySelAallActiveReturn(userId, relTradeId);
    }

    public static IDataset getUserSvcInfos(String userId, String productId, String brandCode, String eparchyCode, String rsrv_str9) throws Exception
    {
    	IDataset userSvcInfo =new DatasetList();
    	IDataset userSvcList =UserSvcInfoQry.getUserSvcInfos(userId, productId, brandCode, eparchyCode, rsrv_str9);
        if(IDataUtil.isNotEmpty(userSvcList)){
        	for(int j=0; j<userSvcList.size();j++){
       	       IData temp = userSvcList.getData(j);
               IDataset chas = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_SVC, temp.getString("SERVICE_ID"), "TD_B_SERVICE");
               if(IDataUtil.isNotEmpty(chas)){
            	   IData tempcha = chas.getData(0);
            	   String RSRVSTR9 =tempcha.getString("RSRV_STR9","");
                   if(RSRVSTR9.equals(rsrv_str9)){
                	   temp.putAll(chas.getData(0));
                	   temp.put("BILLFLG", tempcha.getString("RSRV_STR4",""));
                	   temp.put("PRICE", tempcha.getString("RSRV_STR3",""));
                	   temp.put("TAG_CHAR", tempcha.getString("RSRV_TAG3",""));
                	   temp.put("RSRV_STR10", tempcha.getString("RSRV_STR2",""));
                	   temp.put("BIZ_NAME", USvcInfoQry.getSvcNameBySvcId(temp.getString("SERVICE_ID")));
                	   userSvcInfo.add(temp);
                   }
               }
          }
        }
        return userSvcInfo; 
    }

    public static String getVipClassNameByVipTypeCodeClassId(String vipTypeCode, String VipClassId) throws Exception
    {
        return UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(vipTypeCode, VipClassId);
    }

    public static IDataset getVpnInfoByUser(String userId) throws Exception
    {
        return UserVpnInfoQry.getVpnInfoByUser(userId);
    }

    public static IDataset getVPNInfoByUserId(String user_id) throws Exception
    {
        return CustomerInfoQry.getVPNInfoByUserId(user_id);
    }

    public static IDataset qryCustManagerInfoById(String custManagerId) throws Exception
    {
        IData data = UStaffInfoQry.qryCustManagerInfoByCustManagerId(custManagerId);
        return IDataUtil.idToIds(data);
    }

    public static IDataset qryCustManagerStaffById(String custManagerId) throws Exception
    {
        IData data = UStaffInfoQry.qryCustManagerInfoByCustManagerId(custManagerId);
        return IDataUtil.idToIds(data);
    }

    public static IData qryCustomerInfoByCustId(String custId) throws Exception
    {
        return UcaInfoQry.qryCustomerInfoByCustId(custId);
    }

    public static IData qryGrpInfoByCustId(String custId) throws Exception
    {
        return UcaInfoQry.qryGrpInfoByCustId(custId);
    }

    public static IData qryPerInfoByCustId(String custId, String routeId) throws Exception
    {
        return UcaInfoQry.qryPerInfoByCustId(custId, routeId);
    }

    public static IData qryPerInfoByCustId(String custId) throws Exception
    {
        return UcaInfoQry.qryPerInfoByCustId(custId);
    }

    public static IDataset qryPostInfosByIdIdType(String id, String id_type) throws Exception
    {
        return UserPostInfoQry.qryPostInfosByIdIdType(id, id_type);
    }

    public static IDataset qryTradeDiscntInfos(String trade_id, String modify_tag, Pagination pagination) throws Exception
    {
        return TradeDiscntInfoQry.qryTradeDiscntInfos(trade_id, modify_tag, pagination);
    }

    public static IDataset qryTradeImpuInfo(String tradeId) throws Exception
    {
        return TradeImpuInfoQry.qryTradeImpuInfo(tradeId);
    }

    public static IDataset qryTradeResInfosByType(String trade_id, String res_type_code, Pagination pagination) throws Exception
    {
        return TradeResInfoQry.qryTradeResInfosByType(trade_id, res_type_code, pagination);
    }

    public static IDataset qryUserAcctDaysByUserId(String userId) throws Exception
    {
        return UcaInfoQry.qryUserAcctDaysByUserId(userId);
    }

    public static IDataset qryUserResByIdDescEd(String user_id) throws Exception
    {
        return UserResInfoQry.qryUserResByIdDescEd(user_id);
    }

    public static IDataset queryAllUnionPayMembers(String userIdA, String relationTypeCode, String roleCodeB) throws Exception
    {
        return RelaUUInfoQry.queryAllUnionPayMembers(userIdA, relationTypeCode, roleCodeB);
    }

    public static IDataset queryExistTarget(String userId) throws Exception
    {
        return TroopMemberInfoQry.queryExistTarget(userId);
    }

    public static IDataset queryExRuleByRuleId(String eparchyCode, String ruleId) throws Exception
    {
        return ExchangeRuleInfoQry.queryExRuleByRuleId(eparchyCode, ruleId);
    }

    // 个人业务不用UserOtherInfoQry.getUserOtherInfos("","DEST");
    // 个人业务不用 UserOtherInfoQry.getUserOtherInfos(userId,"DEST");
    // 个人业务不用 UserOtherInfoQry.getUserOtherInfos(userId,"DEST");
    // 个人业务不用UserOtherInfoQry.getUserOtherInfos(userId,"DEST");
    // 个人业务不用 UserOtherInfoQry.getUserOtherInfos(userId,"DEST");
    public static IDataset queryNormalTagInfoByTagCode(String eparchy_code, String tag_code, String subsys_code, String use_tag) throws Exception
    {
        return TagInfoQry.queryNormalTagInfoByTagCode(eparchy_code, tag_code, subsys_code, use_tag);
    }

    public static IDataset queryOneResAll(String userId, String resTypeCode) throws Exception
    {
        return UserResInfoQry.queryOneResAll(userId, resTypeCode);
    }

    public static IDataset queryPlatsvcDiscnt(String trade_id, String user_id, String eparchy_code, String service_id, String para_code3) throws Exception
    {
        return TradeDiscntInfoQry.queryPlatsvcDiscnt(trade_id, user_id, eparchy_code, service_id, para_code3);
    }

    public static IDataset queryTagInfoByTagCode1(String eparchy_code, String tag_code, String subsys_code, String use_tag) throws Exception
    {
        return TagInfoQry.queryTagInfoByTagCode1(eparchy_code, tag_code, subsys_code, use_tag);
    }

    public static IDataset queryTradeHisInfoByUserId(String userId) throws Exception
    {
        return TradeHistoryInfoQry.queryTradeHisInfoByUserId(userId);
    }

    public static IDataset queryTradeWideNet(String trade_id) throws Exception
    {

        return TradeWideNetInfoQry.queryTradeWideNet(trade_id);
    }

    public static IDataset queryTradeProduct(String trade_id) throws Exception
    {
        return TradeProductInfoQry.getTradeProductByTradeId(trade_id);
    }

    public static IDataset queryTradeSaleActive(String trade_id) throws Exception
    {
        return TradeSaleActive.getTradeSaleActiveByTradeId(trade_id);
    }

    public static IDataset queryUserAndProductByUserId(String userId) throws Exception
    {
        return UserInfoQry.queryUserAndProductByUserId(userId);
    }

    public static IDataset queryUserSvcByUserId(String userId, String serviceId, String routeId) throws Exception
    {
        return UserSvcInfoQry.queryUserSvcByUserId(userId, serviceId, routeId);
    }

    public static IDataset getUserAttr(String user_id, String inst_type, String attr_code) throws Exception
    {
        return UserAttrInfoQry.getUserAttr(user_id, inst_type, attr_code, null);
    }
    
    public static IData getPackageElement(String packageId, String elementId)throws Exception
    {
    	IDataset pkgElementInfos =  PkgElemInfoQry.getServElementByPk(packageId, "D", elementId);
    	return IDataUtil.isNotEmpty(pkgElementInfos) ? pkgElementInfos.getData(0) : new DataMap();
    }

    public static boolean isLteCardUser(String userId) throws Exception
    {
    	IDataset resDatas = UserResInfoQry.getUserResInfosByUserIdResTypeCode(userId, "1");

        if(IDataUtil.isNotEmpty(resDatas))
        {
            String lteTag = resDatas.getData(0).getString("RSRV_TAG3", "");

            if("1".equals(lteTag))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    public static String fazzyName(String name) throws Exception
    {
    	return DataFuzzy.fuzzyName(name);
    }

    public static String getUserScore(String userId) throws Exception
    {
        String scoreValue = "0";
        IDataset scoreDataset = AcctCall.queryUserScore(userId);
        if (IDataUtil.isNotEmpty(scoreDataset))
        {
            scoreValue = scoreDataset.getData(0).getString("SUM_SCORE", "0");
        }
        
        return scoreValue;
    }

    public static IDataset getUserAvgPayFee(String userId, String monthsNum) throws Exception
    {
        return UserInfoQry.getUserAvgPayFee2(userId, monthsNum);
    }

    public static String getUserResKindName(String userId) throws Exception
    {
        String resKindName = "";
        IDataset userResDataset = UserResInfoQry.getUserResInfosByUserIdResTypeCode(userId,"1");
        if (IDataUtil.isNotEmpty(userResDataset))
        {
            String simCardNo = userResDataset.getData(0).getString("RES_CODE");
            IDataset simCardDataset = ResCall.getSimCardInfo("0", simCardNo, "", "");
            if (IDataUtil.isNotEmpty(simCardDataset))
            {
                String resKindCode = simCardDataset.getData(0).getString("RES_KIND_CODE");
                IDataset simResKindDataset = ResCall.qryResKindByCode(resKindCode);
                if (IDataUtil.isNotEmpty(simResKindDataset))
                {
                    resKindName = simResKindDataset.getData(0).getString("RES_KIND_NAME");
                }
            }
        }

        return resKindName;
    }
    
    public static IDataset getCommparaInfosByTradeTypeCode(String tradeTypeCode) throws Exception
    {
        return CommparaInfoQry.getCommparaInfosByTradeTypeCode(tradeTypeCode);
    }
    
    /**
     * REQ201505180002业务受理单显示产品优惠资费标准
     * 2015-05-28 CHENXY3
     * */
    public static IData getDiscntInfoByCode(String discntCode) throws Exception
    {
        return DiscntInfoQry.getDiscntInfoByCode2(discntCode);
    }
    
    /**
     * REQ201509300010 客户信息展示页展示内容优化
     * 2015-10-10 CHENXY3
	 * 获取用户星级
	 * <br/>
	 * REQ201608160006 将NGBOSS界面涉及“五星普”全改为“五星银”的需求 
	 * 20160909 
	 * @author zhuoyingzhi
	 * 用户星级名称,修改为直接读取账务接口返回的名称(CREDIT_CLASS_NAME)
     * */
    public static String getUserCreditClass(String userId) throws Exception
    {
    	IDataset result = AcctCall.getUserCreditInfos("0", userId);
		String creditClassName="";
		if (!IDataUtil.isEmpty(result) && result.size()>0)
		{ 
			IData creditInfo = result.getData(0);
			//直接读取账务接口      用户星级名称
			creditClassName=creditInfo.getString("CREDIT_CLASS_NAME");
		}
		return creditClassName;
    }
	
	  public static String GetBankExchangeMoney(String rate, String ScoreValue) throws Exception
    {
		BigDecimal bRate = new BigDecimal(rate);
		BigDecimal bScoreValue = new BigDecimal(ScoreValue);

		BigDecimal bResult = new  BigDecimal("0.0");
		
		bResult = bScoreValue.multiply(bRate);
		String strRes = bResult.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        return strRes;
    }
	  
	    
    /**
     * 过户时打印免填内容
     * @param userId
     * @return
     * @throws Exception
     */
    public static String queryAllUserValidSaleActive(String userId)throws Exception{
    	StringBuilder content=new StringBuilder();
    	
    	//查询所有的营销活动
    	IDataset saleActives=UserSaleActiveInfoQry.queryUserValidActiveForChangeCust(userId);
    	if(saleActives!=null&&saleActives.size()>0){
    		
    		content.append("原用户目前生效的营销活动有：");
    		content.append("~");
    		
    		for(int i=0,size=saleActives.size();i<size;i++){
    			IData saleActive=saleActives.getData(i);
    			
    			String packageName=saleActive.getString("PACKAGE_NAME");
    			String endData=saleActive.getString("END_DATE");
    			String onlineEndDate=saleActive.getString("RSRV_DATE2");
    			String packageDesc=saleActive.getString("PACKAGE_DESC");
    			
    			
    			content.append(packageName);
    			content.append("，");
    			content.append("活动结束时间到");
    			content.append(endData);
    			content.append("，");
    			content.append("约定在网结束时间到");
    			content.append(onlineEndDate);
    			content.append("，");
    			content.append(packageDesc);
    			content.append("。");
    			content.append("~");
    		}
    		
    		content.append("办理过户业务后，原用户目前生效的营销活动会转移至新用户，营销活动涉及的赠送礼品、话费、手机等实物由原、新用户协商处理，我公司不承担由此引起的一切新用户损失。  ");
    	}
    	
    	return content.toString();
    }
    
    
    /**
     * 获取用户的宽带信息
     * @param userId
     * @return
     * @throws Exception
     */
    public static String obtainUserWidenetInfo(String serialNumber)throws Exception{
    	
    	StringBuilder content=new StringBuilder();
    	
    	
    	IData widenetInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
    	if (IDataUtil.isNotEmpty(widenetInfo)){
    		
    		IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
    		
    		if(widenetInfos!=null&&!widenetInfos.isEmpty()){
        		String userId=widenetInfo.getString("USER_ID");
        		
        		String installAddress=widenetInfos.getData(0).getString("DETAIL_ADDRESS","");
        	
        		
        		IDataset userProdInfos=UserProductInfoQry.queryUserMainProduct(userId);
        		if(userProdInfos!=null&&userProdInfos.size()>0){
        			
        			content.append("原用户办理的宽带产品信息有：");
        			
        			for(int j=0,sizej=userProdInfos.size();j<sizej;j++){
        				IData userProdInfo=userProdInfos.getData(j);
        				
        				String productId=userProdInfo.getString("PRODUCT_ID");
        				String productName=null;	//产品名称
        				
        				//查询产品信息
        				IDataset prodInfo= ProductInfoQry.getProductInfoByid(productId);
        				if(prodInfo!=null&&prodInfo.size()>0){
        					productName=prodInfo.getData(0).getString("PRODUCT_NAME");
        				}else{
        					return content.toString();
        				}
        				
        				content.append("~");
        				content.append(productName);
        				content.append("，");
        				content.append("安装地址：");
        				content.append(installAddress);
        				content.append("；");
        				
        				
        				//获取产品的优惠信息
        				IDataset allUserDiscnts=UserDiscntInfoQry.queryUserProductDiscntByProdIdAndUserId(userId,productId);
        				if(allUserDiscnts!=null&&allUserDiscnts.size()>0){
        					
        					content.append("资费：");
        					
        					for(int i=0,size=allUserDiscnts.size();i<size;i++){
        						
        						IData userDiscnt=allUserDiscnts.getData(i);
        						String endDate=userDiscnt.getString("END_DATE");
        						String discntName=userDiscnt.getString("DISCNT_NAME","");
        						
        						content.append(discntName);
    							content.append("，");
    							content.append("资费结束时间到");
    							content.append(endDate);
    							content.append("；");
    							
        					}
        					
        				}
        			}
        			
    				content.append("~办理过户业务后，原用户办理的宽带产品资费会由新用户付费。");
    				
        		}
        	}
    	}
    	
    	
    	return content.toString();
    }
    
    
    
    public static String obtainHolidayDiscntSubscribeSms(List<DiscntTradeData> discntTradeDatas)throws Exception{
    	String content="";

    	if(discntTradeDatas!=null&&discntTradeDatas.size()>0){
    		
			for (DiscntTradeData discntTradeData : discntTradeDatas) {
				if(discntTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
					
					//获取元素的办理时间
                	IDataset elementSpecialTimeLimit=CommparaInfoQry.queryValidHolidayDiscntByDiscntId(discntTradeData.getElementId());
    				
                	if(IDataUtil.isNotEmpty(elementSpecialTimeLimit)){
                		
                		IData element=elementSpecialTimeLimit.getData(0);
                		String discntName=element.getString("PARA_CODE2", "");
                		
                		IDataset templatenew=CommparaInfoQry.queryComparaByAttrAndCode1NEW("CSM", "7688", "HOLIDAY_DISCNT_SMS_TEMPLATE", 
                				"HOLIDAY_DISCNT_SMS_TEMPLATE",discntTradeData.getElementId());
                		if(IDataUtil.isNotEmpty(templatenew)){
                			
                			IData templateInfo=templatenew.getData(0);
                			
                			StringBuilder templateContent=new StringBuilder();
                			templateContent.append(templateInfo.getString("PARA_CODE4",""));
                			templateContent.append(templateInfo.getString("PARA_CODE5",""));
                			templateContent.append(templateInfo.getString("PARA_CODE6",""));
                			templateContent.append(templateInfo.getString("PARA_CODE7",""));
                			templateContent.append(templateInfo.getString("PARA_CODE8",""));
                			templateContent.append(templateInfo.getString("PARA_CODE9",""));
                			templateContent.append(templateInfo.getString("PARA_CODE10",""));
                			templateContent.append(templateInfo.getString("PARA_CODE11",""));
                			templateContent.append(templateInfo.getString("PARA_CODE12",""));
                			templateContent.append(templateInfo.getString("PARA_CODE13",""));
                			templateContent.append(templateInfo.getString("PARA_CODE14",""));
                			templateContent.append(templateInfo.getString("PARA_CODE15",""));
                			templateContent.append(templateInfo.getString("PARA_CODE16",""));
                			
                			
                			content=templateContent.toString();
                			
                			String startDate=discntTradeData.getStartDate();
                			String endDate=discntTradeData.getEndDate();
                			
                			content=content.replaceAll("@DISCNT_NAME@", discntName)
                						.replaceAll("@BEGIN_DATE@", startDate).replaceAll("@END_DATE@", endDate);
                			
                		}else{
                			IDataset template=CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "7687", "HOLIDAY_DISCNT_SMS_TEMPLATE", 
                    				"HOLIDAY_DISCNT_SMS_TEMPLATE");
                    		
                    		if(IDataUtil.isNotEmpty(template)){
                    			IData templateInfo=template.getData(0);
                    			
                    			StringBuilder templateContent=new StringBuilder();
                    			templateContent.append(templateInfo.getString("PARA_CODE4",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE5",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE6",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE7",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE8",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE9",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE10",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE11",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE12",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE13",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE14",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE15",""));
                    			templateContent.append(templateInfo.getString("PARA_CODE16",""));
                    			
                    			
                    			content=templateContent.toString();
                    			
                    			String startDate=discntTradeData.getStartDate();
                    			String endDate=discntTradeData.getEndDate();
                    			
                    			content=content.replaceAll("@DISCNT_NAME@", discntName)
                    						.replaceAll("@BEGIN_DATE@", startDate).replaceAll("@END_DATE@", endDate);
                    			
                    		}
                		}
                		break;
                	}
				}
			}
	
	    }
    	
    	return content;
    }
     
    /**
     * REQ201603280028 关于新增集团机惠专享积分购机活动的需求（优化规则及新增参数）
     * */
    public static String getProdAStartDate(String productId_B,String userId) throws Exception
    {
        String prod_A_start_date="";
    	IDataset prodAinfos= ProductdLimitInfoQry.getSaleActiveLimitProd(productId_B);
    	String prod_As="";
        for(int k=0;k<prodAinfos.size();k++){
        	String prod_A = prodAinfos.getData(k).getString("LIMIT_CODE");
        	if("".equals(prod_As)){
        		prod_As=prod_A;
        	}else{
        		prod_As=prod_As+","+prod_A;
        	}
        }
        //如果存在多个，则取最近一个，这种语句未必可行
        IDataset userProdAs=UserSaleActiveInfoQry.querySaleActiveByUserIdProdId(userId,prod_As);
    	if(userProdAs!=null && userProdAs.size()>0){
    		prod_A_start_date=userProdAs.getData(0).getString("START_DATE_A","");
    	}
        return prod_A_start_date;
    } 
    
    
    public static String obtainEndSaleActivePrintContent(MainTradeData mainTradeData)throws Exception{
    	StringBuilder content=new StringBuilder();
    	
    	String rsrvStr8=mainTradeData.getRsrvStr8();
    	
    	if(rsrvStr8!=null&&!rsrvStr8.trim().equals("")){
    		String[] contentData=rsrvStr8.split("#null#");
    		
    		if(contentData!=null&&contentData.length>0){
    			for(int i=0,size=contentData.length;i<size;i++){
    				
    				if(contentData[i]!=null&&!contentData[i].trim().equals("")){
    					if(i==0){
    						content.append("终止活动名称：");
    						content.append(contentData[i]);
        				}else if(i==1){
        					content.append("~~");
        	        		content.append("活动正常履约月份：");
        	        		content.append(contentData[i]);
        				}else if(i==2){
        					content.append("~~");
        	        		content.append("违约终止时间：");
        	        		content.append(contentData[i]);
        				}else if(i==3){
        					content.append("~~");
        		    		content.append("实收终止活动违约金：");
        		    		content.append(contentData[i]);
        		    		content.append("~~");
        				}
    				}else{
    					if(i==0){
    						content.append("终止活动名称：");
        				}else if(i==1){
        					content.append("~~");
        	        		content.append("活动正常履约月份：");
        				}else if(i==2){
        					content.append("~~");
        	        		content.append("违约终止时间：");
        				}else if(i==3){
        					content.append("~~");
        		    		content.append("实收终止活动违约金：");
        		    		content.append("~~");
        				}
    				}
    			}
    		}else{
    			content.append("终止活动名称：");
        		content.append("~~");
        		content.append("活动正常履约月份：");
        		content.append("~~");
        		content.append("违约终止时间：");
        		content.append("~~");
        		content.append("实收终止活动违约金：");
        		content.append("~~");
    		}
    	}
    	else{
    		content.append("终止活动名称：");
    		content.append("~~");
    		content.append("活动正常履约月份：");
    		content.append("~~");
    		content.append("违约终止时间：");
    		content.append("~~");
    		content.append("实收终止活动违约金：");
    		content.append("~~");
    	}
    	
    	
    	return content.toString();
    }
    
    public static String SaleActivePrintContent(RegTradeData rtd)throws Exception{
    	
    	String rsrvStr1="";
    	String userId="";
    	String tradeId="";
    	rsrvStr1 = rtd.getMainTradeData().getRsrvStr1();
    	userId = rtd.getMainTradeData().getUserId();
    	tradeId = rtd.getTradeId();
    	String strContent="";
    	String endOffset = "";
    	String appointEndOffset ="";
    	String tempContent2 ="";
    	String tempContent1 = "";
    	String tempContent3 = "";
    	String tempContent4 = "";
    	String startDate="";
    	String endDate="";
    	String appointEndDate="";
    	String endDateMark = "0";//初始化结束时间标记
    	
    	//add by zhangxing for 候鸟短期套餐，不打印这段内容！
    	if("66000602".equals(rsrvStr1) ){
    		return strContent;
    	}
    	
    	if("69900360" != rsrvStr1){
    		String rsrvStr2 = rtd.getMainTradeData().getRsrvStr2();
    		IDataset comms1 = MvelMiscQry.getCommparaInfoBy5("CSM","158","1",rsrvStr2,"0898",null);
    		IDataset comms0 = MvelMiscQry.getCommparaInfoBy5("CSM","158","0",rsrvStr1,"0898",null);
    		IDataset comms = new DatasetList();
    		if(comms0!= null && comms0.size()>0)
    		{
    			comms.addAll(comms0);
    		}
    		if(comms1!= null && comms1.size()>0)
    		{
    			comms.addAll(comms1);
    		}
    		if(comms.size()>0)
    		{
    			strContent = "";
    		}
    		else
    		{
    			startDate = rtd.getMainTradeData().getRsrvStr5();
    			endDate = rtd.getMainTradeData().getRsrvStr6();
    			String appointStartDate = rtd.getMainTradeData().getRsrvStr9();
    			appointEndDate = rtd.getMainTradeData().getRsrvStr10();
    			if(startDate !=null && startDate!="")
    			{
    				startDate = SysDateMgr.getChinaDate(startDate,SysDateMgr.PATTERN_CHINA_DATE);
    			}
    			if(endDate !=null && endDate!="")
    			{
    				//取当前日期，加10年，格式化为yyyyMMdd
    				String newSysDateTemp = SysDateMgr.decodeTimestamp(SysDateMgr.addYears(SysDateMgr.getSysDate(),10),"yyyyMMdd");
    				//将结束日期格式化为yyyyMMdd
    				String endDateTemp = SysDateMgr.decodeTimestamp(endDate,"yyyyMMdd");
    				//将日期都转为数字
    				int intSysDateTemp = Integer.parseInt(newSysDateTemp);
    				int intEndDate = Integer.parseInt(endDateTemp);
    				//如果结束时间大于10年后， 将结束时间标记置为1
    				if(intEndDate>intSysDateTemp)
    				{
    					endDateMark = "1";
    				}
    				
    				endDate =  SysDateMgr.getChinaDate(endDate,SysDateMgr.PATTERN_CHINA_DATE);
    			}
    			if(appointStartDate !=null && appointStartDate != ""){
    				appointStartDate =  SysDateMgr.getChinaDate(appointStartDate,SysDateMgr.PATTERN_CHINA_DATE);
    			}
    			if(appointEndDate !=null && appointEndDate !="")
    			{
    				appointEndDate =  SysDateMgr.getChinaDate(appointEndDate,SysDateMgr.PATTERN_CHINA_DATE);
    			}else
    			{
    				appointEndDate="";
    			}
    			IDataset pkgs = MvelMiscQry.getPackageByPackage(rsrvStr2,"0898");
    			if(pkgs !=null && pkgs.size()>0)
    			{
    				endOffset = pkgs.getData(0).getString("END_OFFSET","");
    				appointEndOffset = pkgs.getData(0).getString("RSRV_STR23","-1");
    				if(appointEndOffset=="-1"||appointEndOffset=="")
    				{
    					appointEndOffset=endOffset;
    				}
    			}
    	    
    			IDataset salses1 = new DatasetList();
    			IDataset ids = MvelMiscQry.getUserSaleActive(userId,tradeId);
    			if(ids !=null && ids.size()>0)
    			{
    				for(int i = 0; i < ids.size(); i++)
    				{
    					if(com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil.isQyyx(ids.getData(i).getString("CAMPN_TYPE" ,"")))
    					{ 
    						salses1.add(ids.getData(i));
    					}
    				}
    			}
    			IDataset salses2 = new DatasetList();
    			ids = MvelMiscQry.getUserSaleActiveBySelAallActiveReturn(userId,tradeId);
    			if(ids !=null && ids.size()>0)
    			{	
    				for(int i = 0; i < ids.size(); i++)
    				{
    					if(com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil.isQyyx(ids.getData(i).getString("CAMPN_TYPE" ,"")))
    					{
    						salses2.add(ids.getData(i));
    					}
    				}
    			}
    			comms0 = MvelMiscQry.getCommparaInfoBy5("CSM","155","0",rsrvStr1,"0898",null);
    			comms1 = MvelMiscQry.getCommparaInfoBy5("CSM","155","1",rsrvStr1,"0898",null);
    			IDataset comms2 = new DatasetList();
    			IDataset  comms3 = new DatasetList();
    			boolean  flag2 = false;
    			boolean flag3 = false;
    			if(comms0!= null && comms0.size()>0)
    			{
    				comms.addAll(comms0);
    			}
    			if(comms1!= null && comms1.size()>0)
    			{
    				comms.addAll(comms1);
    			}
    			if(salses1 !=null && salses1.size()>0)
    			{
    				if(salses2 !=null && salses2.size()>0){
    					for(int i =0; i < salses1.size(); i++)
    					{
    						tempContent1=tempContent1+salses1.getData(i).getString("PRODUCT_NAME" ,"")+"，";
    						comms2 = MvelMiscQry.getCommparaInfoBy5("CSM","1593",rsrvStr1,salses1.getData(i).getString("PRODUCT_ID"),"0898",null);
    						if(comms2!= null && comms2.size()>0)
    						{
    							flag2 = true;tempContent4=tempContent4+comms2.getData(0).getString("PARA_CODE3","")+"，";
    						}
    					}
    					if(flag2)
    					{
    						tempContent1="~您已参加的约定在网活动有:"+tempContent1+"其中("+tempContent4+")活动约定在网时间截止到本月底，参加本活动后约定在网时间延长至"+appointEndDate+"止。";
    					}else
    					{
    						tempContent1="~您已参加的约定在网活动有:"+tempContent1+"约定在网时间到"+SysDateMgr.getChinaDate(salses1.getData(0).getString("RSRV_DATE2",""),SysDateMgr.PATTERN_CHINA_DATE)+"止，参加本活动后约定在网时间延长至"+appointEndDate+"止。";
    					}

    					String infoname = "";
    					for(int i =0; i < salses2.size(); i++)
    					{
    						tempContent2=tempContent2+salses2.getData(i).getString("PRODUCT_NAME" ,"")+"，";
    						comms3 = MvelMiscQry.getCommparaInfoBy5("CSM","1593",rsrvStr1,salses2.getData(i).getString("PRODUCT_ID"),"0898",null);
    						if(comms3!= null && comms3.size()>0)
    						{	
    							flag3 = true;
    							infoname = comms3.getData(0).getString("PARA_CODE3","");
    						}
    					}
    	      
    	      
    					if(flag3)
    					{
    						tempContent2="~您已参加的话费返还/约定消费活动有："+tempContent2+"其中("+infoname+")活动在参加本次活动后立即终止。";
    					}else
    					{
    						tempContent2="~您已参加的话费返还/约定消费活动有："+tempContent2+"返还时间到"+SysDateMgr.getChinaDate(salses2.getData(0).getString("END_DATE",""),SysDateMgr.PATTERN_CHINA_DATE)+"止。";
    					}

    					if(tempContent2.length()>50)
    					{
    						tempContent2=tempContent2+"~";
    					}
    				}else
    				{
    					for(int i =0; i < salses1.size(); i++)
    					{
    						tempContent1=tempContent1+salses1.getData(i).getString("PRODUCT_NAME" ,"")+"，";
    						comms2 = MvelMiscQry.getCommparaInfoBy5("CSM","1593",rsrvStr1,salses1.getData(i).getString("PRODUCT_ID"),"0898",null);
    						if(comms2!= null && comms2.size()>0)
    						{
    							flag2 = true;
    							tempContent4=tempContent4+comms2.getData(0).getString("PARA_CODE3","")+"，";
    						}
    					}
    					if(flag2)
    					{
    						tempContent1="~您已参加的约定在网活动有:"+tempContent1+"其中("+tempContent4+")活动约定在网时间截止到本月底，参加本活动后约定在网时间延长至"+appointEndDate+"止。";
    					}else
    					{
    						tempContent1="~您已参加的约定在网活动有:"+tempContent1+"约定在网时间到"+SysDateMgr.getChinaDate(salses1.getData(0).getString("RSRV_DATE2",""),SysDateMgr.PATTERN_CHINA_DATE)+"止，参加本活动后约定在网时间延长至"+appointEndDate+"止。";
    					}
    				}
    			}else{
    				tempContent1="~您参加本活动的约定在网截止时间到"+appointEndDate+"止。";
    			}
    	    
    			if(comms !=null && comms.size()>0){
    				strContent="本活动的约定在网时间为"+appointEndOffset+"个月。";
    				strContent=strContent+tempContent2+tempContent1;
    	    	}else{
    	    		strContent="本活动的话费返还/约定消费时间为"+endOffset +"个月，约定在网时间为"+appointEndOffset+"个月。";
    	    		tempContent3="~本活动话费返还/约定消费生效时间为"+startDate+"，结束时间为"+endDate+"。";
    	    		if(rsrvStr1=="69900370"){
    	    			strContent="本活动的约定消费时间为"+endOffset+"个月，约定在网时间为"+appointEndOffset+"个月。";
    	    			tempContent3="~本活动约定消费生效时间为"+startDate+"，结束时间为"+endDate+"。";
    	    		}
    	    		strContent=strContent+tempContent2+tempContent3+tempContent1;
    	    	}
    	    }
    	}

    	String camptype = MvelMiscQry.getCampnType(rsrvStr1);
    	if("YX04".equals(camptype)||  "YX07".equals(camptype))
    	{
    		if("1".equals(endDateMark))
    		{
    			strContent = "本活动生效时间" + startDate + "。";
    		}
    		else
    		{
    			strContent = "本活动生效时间" + startDate + ",结束时间"+endDate+"。";
    		}
    	}
    	
    	return strContent;
    }
    
    public static String SaleActivePrintContent2(RegTradeData rtd)throws Exception{
    	
    	String rsrvStr1="";
    	String rsrvStr2="";
    	String eparchyCode="";
    	rsrvStr1 = rtd.getMainTradeData().getRsrvStr1();
    	rsrvStr2 = rtd.getMainTradeData().getRsrvStr2();
    	eparchyCode = rtd.getMainTradeData().getEparchyCode();
    	String strContent= "";
    	IDataset ids = MvelMiscQry.getPackageByPackage(rsrvStr2,eparchyCode);
    	if("69900212" == rsrvStr1){
    	    strContent = strContent + "本次受理业务：约定消费优惠购机"+"~";
    	    strContent = strContent + "本业务规定：自下月起按20元/月的标准约定消费6个月（不含信息费），"+"~";
    	    strContent = strContent + "约定消费期内如客户未达到约定最低消费额，按每月约定最低消费额收取。";
    	}else if("69900241" == rsrvStr1){
    	    strContent = strContent + "1、客户在办理本全球通回馈购机业务时须同时登记选用月租或每月约定消费80元以上（含80元）的全球通套"+"~";
    	    strContent = strContent + "餐，自次月起（含次月）连续使用满一年，期间每月消费不得低于80元，否则将按80元/月收取费用。"+"~";
    	    strContent = strContent + "2、参加本活动的客户在活动有效期内不能参加其他优惠活动，不得办理退网、改名、过户，否则需交纳违约"+"~";
    	    strContent = strContent + "金（违约金=80元*违约月份数）。";

    	}else if(ids !=null && ids.size()>0){
    		String rsrvInfo1 = "";
    		String rsrvInfo2 = "";
    	    rsrvInfo1 = ids.getData(0).getString("RSRV_INFO1","");
    	    if(rsrvInfo1 != ""){
    	    	rsrvInfo2 = ids.getData(0).getString("RSRV_INFO2","");
    	    	strContent = strContent + rsrvInfo1 +"~";
    	    	strContent = strContent + rsrvInfo2 +"~";
    	    }
    	}else{
    		String rsrvInfo3 = "";
    		String rsrvInfo4 = "";
    	    rsrvInfo3 = rtd.getMainTradeData().getRsrvStr3();
    	    rsrvInfo4 = rtd.getMainTradeData().getRsrvStr4();
    	    if(rsrvInfo3!=null){strContent = "营销活动方案："+ rsrvInfo3+"~";}
    	    if(rsrvInfo4!=null){strContent = "营销包："+ rsrvInfo4 +"~";}
    	}
    	
    	IDataset list = TradeCreditInfoQry.getTradeCreditByPK(rtd.getTradeId(),"0");
    	if(list !=null && list.size()>0){
    	   IDataset comms =  MvelMiscQry.getCommparaInfos("CSM","1559",rsrvStr1);
    	   if(comms !=null && comms.size()>0){
    		   for(int i =0;i<comms.size();i++){
    			   String paraCode1  =  comms.getData(i).getString("PARA_CODE1");
    			   if(paraCode1 == rsrvStr2 || paraCode1 == "-1"){
    				   strContent += "~" + comms.getData(i).getString("PARA_CODE22","")+ comms.getData(i).getString("PARA_CODE23","");
    			   }
    		   }
    	   }
    	}
    	return strContent;
    } 
    
    public static String getUserDiscntThisMonthCount(String userId) throws Exception
    {
    	String userDiscntCount = UserDiscntInfoQry.getUserDiscntMonthCount(userId);
    	userDiscntCount = String.valueOf(Integer.parseInt(userDiscntCount)+1);//将获取到用户已经办理的数据+1
        return userDiscntCount;
    }
    
    /**
     * 获取魔百和产品变更的免填单内容
     * @param tradeId
     * @param mainData
     * @return
     * @throws Exception
     */
     public static String obtainTopsetboxPrintData(String acceptTime, String tradeId, MainTradeData mainData)throws Exception{
     	String printContent="";
     	
     	String selectionOper=mainData.getRsrvStr2();
     	
     	String template=null;
     	String paramCode="";
     	if(selectionOper.equals("1")){		//产品变更
     		paramCode="11";
     		template="尊敬的客户，您好！您于@ACCEPT_TIME@申请变更中国移动魔百和（互联网电视）业务，从“@ORIGINAL_PACKAGE@”变更为“@NEW_PACKAGE@”，资费不变；详询当地移动营业厅或拨打10086。中国移动海南公司";
     	}else if(selectionOper.equals("2")){		//机顶盒变更
     		paramCode="12";
     		template="尊敬的客户，您好！您于@ACCEPT_TIME@申请变更机顶盒，将您的原机顶盒（串号：@ORIGINAL_NUMBER@）更换为新版机顶盒（串号：@NEW_NUMBER@），原有押金继续使用；详询当地移动营业厅或拨打10086。中国移动海南公司";
     	}else if(selectionOper.equals("3")){		//产品变更和机顶盒变更
     		paramCode="13";
     		template="尊敬的客户，您于@ACCEPT_TIME@变更中国移动魔百和（互联网电视）业务，从“@ORIGINAL_PACKAGE@”变更为“@NEW_PACKAGE@”，资费不变；同时将您的原机顶盒（串号：@ORIGINAL_NUMBER@）更换为新版机顶盒（串号：@NEW_NUMBER@），原有押金继续使用。详询当地移动营业厅或拨打10086  中国移动海南公司";
     	}
     	
     	IDataset templateDatas=CommparaInfoQry.queryCommparaSmsTemplate("3902", paramCode);
     	if(IDataUtil.isNotEmpty(templateDatas)){
     		template=templateDatas.getData(0).getString("SMS_CONTENT","");
     	}
     	
     	if(selectionOper.equals("1")){		//产品变更
     		String packageInfo=mainData.getRsrvStr3();
     		String[] packageInfoArr=packageInfo.split("@NULL@");
     		
     		printContent=template.replaceAll("@ACCEPT_TIME@", acceptTime)
     							.replaceAll("@ORIGINAL_PACKAGE@", packageInfoArr[0])
     							.replaceAll("@NEW_PACKAGE@", packageInfoArr[1]);
     	}else if(selectionOper.equals("2")){		//机顶盒变更
     		printContent=template.replaceAll("@ACCEPT_TIME@", acceptTime)
 					.replaceAll("@ORIGINAL_NUMBER@", mainData.getRsrvStr8())
 					.replaceAll("@NEW_NUMBER@", mainData.getRsrvStr6());
     	}else if(selectionOper.equals("3")){		//产品变更和机顶盒变更
     		String packageInfo=mainData.getRsrvStr3();
     		String[] packageInfoArr=packageInfo.split("@NULL@");
     		
     		printContent=template.replaceAll("@ACCEPT_TIME@", acceptTime)
 					.replaceAll("@ORIGINAL_PACKAGE@", packageInfoArr[0])
 					.replaceAll("@NEW_PACKAGE@", packageInfoArr[1])
 					.replaceAll("@ORIGINAL_NUMBER@", mainData.getRsrvStr8())
 					.replaceAll("@NEW_NUMBER@", mainData.getRsrvStr6());
     	}
     	
     	
     	return printContent;
     	
     }
     
     
     /**
      * 获取魔百和产品变更的短信内容
      * @param tradeId
      * @param mainData
      * @return
      * @throws Exception
      */
      public static String obtainTopsetboxSmsData(String acceptTime, String tradeId, MainTradeData mainData)throws Exception{
      	String printContent="";
      	
      	String selectionOper=mainData.getRsrvStr2();
      	
      	String template=null;
      	String paramCode="";
      	if(selectionOper.equals("1")){		//产品变更
      		paramCode="1";
      		template="尊敬的客户，您好！您于@ACCEPT_TIME@申请变更中国移动魔百和（互联网电视）业务，从“@ORIGINAL_PACKAGE@”变更为“@NEW_PACKAGE@”，资费不变；详询当地移动营业厅或拨打10086。中国移动海南公司";
      	}else if(selectionOper.equals("2")){		//机顶盒变更
      		paramCode="2";
      		template="尊敬的客户，您好！您于@ACCEPT_TIME@申请变更机顶盒，将您的原机顶盒（串号：@ORIGINAL_NUMBER@）更换为新版机顶盒（串号：@NEW_NUMBER@），原有押金继续使用；详询当地移动营业厅或拨打10086。中国移动海南公司";
      	}else if(selectionOper.equals("3")){		//产品变更和机顶盒变更
      		paramCode="3";
      		template="尊敬的客户，您于@ACCEPT_TIME@变更中国移动魔百和（互联网电视）业务，从“@ORIGINAL_PACKAGE@”变更为“@NEW_PACKAGE@”，资费不变；同时将您的原机顶盒（串号：@ORIGINAL_NUMBER@）更换为新版机顶盒（串号：@NEW_NUMBER@），原有押金继续使用。详询当地移动营业厅或拨打10086  中国移动海南公司";
      	}
      	
      	IDataset templateDatas=CommparaInfoQry.queryCommparaSmsTemplate("3902", paramCode);
      	if(IDataUtil.isNotEmpty(templateDatas)){
      		template=templateDatas.getData(0).getString("SMS_CONTENT","");
      	}
      	
      	if(selectionOper.equals("1")){		//产品变更
     		String packageInfo=mainData.getRsrvStr3();
     		String[] packageInfoArr=packageInfo.split("@NULL@");
     		
     		printContent=template.replaceAll("@ACCEPT_TIME@", acceptTime)
     							.replaceAll("@ORIGINAL_PACKAGE@", packageInfoArr[0])
     							.replaceAll("@NEW_PACKAGE@", packageInfoArr[1]);
     	}else if(selectionOper.equals("2")){		//机顶盒变更
     		printContent=template.replaceAll("@ACCEPT_TIME@", acceptTime)
 					.replaceAll("@ORIGINAL_NUMBER@", mainData.getRsrvStr8())
 					.replaceAll("@NEW_NUMBER@", mainData.getRsrvStr6());
     	}else if(selectionOper.equals("3")){		//产品变更和机顶盒变更
     		String packageInfo=mainData.getRsrvStr3();
     		String[] packageInfoArr=packageInfo.split("@NULL@");
     		
     		printContent=template.replaceAll("@ACCEPT_TIME@", acceptTime)
 					.replaceAll("@ORIGINAL_PACKAGE@", packageInfoArr[0])
 					.replaceAll("@NEW_PACKAGE@", packageInfoArr[1])
 					.replaceAll("@ORIGINAL_NUMBER@", mainData.getRsrvStr8())
 					.replaceAll("@NEW_NUMBER@", mainData.getRsrvStr6());
     	}
      	
      	
      	return printContent;
      	
      }
      /**
       * 20160601
       * @param userId
       * @return
       * @throws Exception
       * <br/>
       * 说明：
       *   根据userid判断用户是否为4G用户
       */
      public static boolean is4GUser(String userId) throws Exception
      {
		IDataset resDatas= UserResInfoQry.get4GUserByUserId(userId);
		if (IDataUtil.isNotEmpty(resDatas)) {
			return true;
		} else {
			return false;
		}	

      } 
      
      /**
       * REQ201607140023 关于非实名用户关停改造需求
       * chenxy3
       * 2016-08-11
       * 判断用户表TF_F_USER USER_STATE_CODESET状态，并返回中文
       * */
      public static String getUserStateCodeset(String userId,String userStateCodeset) throws Exception
      {
    	  //log.info("("####*cxt*###userId="+userId+"***userStateCodeset="+userStateCodeset);
    	  String userState=""; 
    	  IData insData=new DataMap();
		  insData.put("USER_ID", userId);
    	  if("4".equals(userStateCodeset)){ 
    		  IDataset stopUser=CSAppCall.call("SS.GetUser360ViewSVC.qryUserIfNotRealName", insData);
    		  //log.info("("####*cxt*###insData="+insData+"***stopUser="+stopUser);
    		  if(stopUser !=null && stopUser.size()>0){
    			  userStateCodeset="HT";
    		  }
    	  }
    	  /**
           * REQ201608260010 关于非实名用户关停改造需求
           * 20160912 chenxy3
           * 欠费停机转“非实名制全停”
           * */
          if("5".equals(userStateCodeset)){
          	IDataset stopUsers = CSAppCall.call("SS.GetUser360ViewSVC.qryUserIfAllStop", insData);
          	if(stopUsers!=null && stopUsers.size()>0){
          		userStateCodeset="AT";
          	}
          }
    	  
    	  userState=StaticUtil.getStaticValue("USER_STATE_CODESET", userStateCodeset);
    	  return userState;
      }
      
      /**
       * REQ201607140023 关于非实名用户关停改造需求
       * chenxy3
       * 2016-08-11
       * 判断用户表TF_F_USER USER_STATE_CODESET状态，并返回中文
       * */
      public static String queryRedPakAmt(String userId) throws Exception
      {
    	  String amt="";
    	  IData data = new DataMap();
          data.put("USER_ID", userId); 
          data.put("RSRV_VALUE", "2"); //
    	  IDataset selResult1= SaleActiveQueryBean.getRedPakOtherInfo(data);
    	  if(IDataUtil.isNotEmpty(selResult1)){
    		  amt=selResult1.getData(0).getString("RSRV_STR14","");
    	  }else{
    		  data.put("RSRV_VALUE", "3"); //
        	  selResult1= SaleActiveQueryBean.getRedPakOtherInfo(data);
        	  if(IDataUtil.isNotEmpty(selResult1)){
        		  amt=selResult1.getData(0).getString("RSRV_STR14","");
        	  }
    	  }
    	  return amt;
      }
      /**
       * @param productId,packageId,discntCode
       * zhangxing3
       * 2017-02-08
       * 根据productId,packageId,discntCode判断改优惠是否为必选优惠
       */
      public static boolean isForcedDiscnt(String tradeTypeCode,String productId,String packageId,String discntCode,String strtype) throws Exception
      {
		IDataset ids= DiscntInfoQry.getForcedDiscntByProdPackDiscnt(productId,packageId,discntCode, strtype);
		if ("110".equals(tradeTypeCode) && IDataUtil.isNotEmpty(ids)) {
			return true;
		} else {
			return false;
		}	

      }
      /**
       * 根据discntCode trade_id  modify_tag查询 productId,packageId
       * fangwz
       * 2017-04-12
       */
      public static IDataset queryTradeOfferRelsByTradeIdAndCode(String tradeId, String modifyTag, String relOfferType, String relOfferCode) throws Exception{
      	IData param = new DataMap();
      	param.put("TRADE_ID", tradeId);
      	param.put("MODIFY_TAG", modifyTag);
      	param.put("REL_OFFER_TYPE", relOfferType);
      	param.put("REL_OFFER_CODE", relOfferCode);
      	StringBuilder sql = new StringBuilder();
      	sql.append(" SELECT A.TRADE_ID,A.ACCEPT_MONTH,A.OFFER_CODE,A.OFFER_TYPE,A.OFFER_INS_ID,A.USER_ID,A.GROUP_ID,A.REL_OFFER_CODE,A.REL_OFFER_TYPE,A.REL_OFFER_INS_ID,A.REL_USER_ID,A.REL_TYPE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.INST_ID");
      	sql.append(" FROM TF_B_TRADE_OFFER_REL A");
      	sql.append(" WHERE 1=1");
      	sql.append(" AND A.TRADE_ID =:TRADE_ID");
      	sql.append(" AND A.MODIFY_TAG =:MODIFY_TAG");
      	sql.append(" AND A.REL_OFFER_TYPE = :REL_OFFER_TYPE");
      	sql.append(" AND A.REL_OFFER_CODE = :REL_OFFER_CODE");
      	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
          return ids; 
      }
      public static IDataset query_TF_B_TRADE_PCRF_ByTradeId(String tradeId) throws Exception{                

          IData param = new DataMap();
          param.put("TRADE_ID", tradeId);         
          IDataset ids = Dao.qryByCode("TF_B_TRADE_PCRF", "SEL_BY_TRADE_ID", param);
          return ids; 
        }
      /**
       * @param user_id
       * fangwz
       * 2017-06-12
       * 根据user_id判断改是否为代理商批量买断预开户号码非实名制（is_real_name不为1），未激活号码（acct_tag不为0） 
       */
      public static boolean notisRealNameAndAcctTag(String userid) throws Exception
      {
  		UcaData Uca = UcaDataFactory.getUcaByUserId(userid);
        String isrealname=Uca.getCustomer().getIsRealName();
        String accttag=Uca.getAccount().getAcctTag();
		if (!"1".equals(isrealname)&& !"0".equals(accttag)) {
			return true;
		} else {
			return false;
		}	
      }
      /**
       * @param SERIAL_NUMBER
       * fangwz
       * 2017-06-12
       * 根据SERIAL_NUMBER判断改是否为吉祥号码
       */
      public static boolean isJXNum(String sn) throws Exception
      {
          IDataset numberInfo = ResCall.getMphonecodeInfo(sn);// 查询号码信息
          if (IDataUtil.isNotEmpty(numberInfo))
          {
              String jxNumber = numberInfo.getData(0).getString("BEAUTIFUAL_TAG");// BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
              if ("1".equals(jxNumber))
              {// 是吉祥号码 
              	return true;
              }
          }
		return false;
      }
      
      /*
       * @param user_id
       * 获取吉祥号码对应绑定的营销包描述
       * 20170717
       * 
       */
	public static String getPackageDescBySn(String serialNumber) throws Exception {
		// 应绑定的营销包描述
		String strContent = "";
		IDataset dataSet = ResCall.getMphonecodeInfo(serialNumber);
		if (IDataUtil.isNotEmpty(dataSet)){
			IData mphonecodeInfo = dataSet.first();
			String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
			if (StringUtils.equals("1", beautifulTag)) {
				String productId = mphonecodeInfo.getString("BIND_PRODUCT_ID");

				String packageId = mphonecodeInfo.getString("BIND_PACKAGE_ID");
				if(packageId==null){
					packageId="";
				}
				// 根据
				IDataset offers = UpcCall.qryOffersByCatalogId(productId, null);

				if (IDataUtil.isNotEmpty(offers)) {
					for (int i = 0; i < offers.size(); i++) {
						if (packageId.equals(offers.getData(i).getString("OFFER_CODE"))) {
							strContent = "提示：" + offers.getData(i).getString("DESCRIPTION");
							break;
						}
					}
				}
			}
		}		
		return strContent;
	}
	/**
	 *  REQ201805180005_关于套餐适配推荐结果在CRM营业前台展示及统计分析的需求
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 * @author zhuoyingzhi
	 * @date 20180608
	 */
	public static String getSaleplatInfoBySn(String serialNumber,String productId) throws Exception {
		
		IData param=new DataMap();
			  //只查询当天的数据
			  param.put("IN_DATE", SysDateMgr.getSysDateYYYYMMDD());
			  param.put("MSISDN", serialNumber);
        IDataset saleplatInfo= Dao.qryByCode("TL_B_SALEPLAT_INFO", "SEL_SALEPLAT_INFO_BY_MSISDN", param);
        log.debug("---MvelMiscQry---getSaleplatInfoBySn--saleplatInfo:"+saleplatInfo);
        String content="";
        if(IDataUtil.isNotEmpty(saleplatInfo)){
        		IData saleplat=saleplatInfo.getData(0);
        		
        		boolean insertInfoFlag=false;
        		//近三月实际平均消费（元）
        		String last3Mon=saleplat.getString("LAST_3MON_ALL_FEE", "");
        		if(!"".equals(last3Mon)&&last3Mon!=null){
        			content=content+"~"+"<span style=\"font-weight: bold;\">近三个月平均消费:"+last3Mon+"元</span>";
        			insertInfoFlag=true;
        			
        		}
        		
        		//推荐套餐名称
        		String recDiscntName=saleplat.getString("REC_DISCNT_NAME", "");
        		if(!"".equals(recDiscntName)&&recDiscntName!=null){
        			content=content+"~"+"<span style=\"font-weight: bold;\">推荐套餐:"+recDiscntName+"</span>";
        			insertInfoFlag=true;
        		}
        		
        		//当前语音自选套餐名称
        		String discntNameVoice=saleplat.getString("RECOMMEND_VCALL_NAME", "");
        		if(!"".equals(discntNameVoice)&&discntNameVoice!=null){
        			content=content+"~"+"<span style=\"font-weight: bold;\">推荐语音自选套餐:"+discntNameVoice+"</span>";
        			insertInfoFlag=true;
        		}
        		
        		//当前流量自选套餐名称
        		String discntNameGprs=saleplat.getString("RECOMMEND_FLUX_NAME", "");
        		if(!"".equals(discntNameGprs)&&discntNameGprs!=null){
        			content=content+"~"+"<span style=\"font-weight: bold;\">推荐流量自选套餐:"+discntNameGprs+"</span>";
        			insertInfoFlag=true;
        		}
        		
        		if(insertInfoFlag){
        			//有内容展示,则记录日志
        			IData logInfo=new DataMap();
        				  logInfo.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());//受理业务区
        				  logInfo.put("SERIAL_NUMBER", serialNumber);//手机号码
        				  logInfo.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());//受理员工(接触人员工号)
        				  logInfo.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());//受理渠道(接触网点)
        				  logInfo.put("IN_DATE", SysDateMgr.getSysDateYYYYMMDD());//更新时间
        				  
        				  logInfo.put("P_DAY", SysDateMgr.getIntDayByDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS()));
        				  logInfo.put("REC_DISCNT_CODE", saleplat.getString("REC_DISCNT_CODE", ""));//展示推荐套餐编码
        				  logInfo.put("REC_DISCNT_NAME", recDiscntName);//展示推荐套餐名称
        				  String produceName=UpcCall.qryOfferNameByOfferTypeOfferCode(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        				  logInfo.put("PRODUCT_NAME", produceName);//客户当前基础套餐名称
        				  logInfo.put("RSVR_STR1", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//推荐套餐展示时间
        				  
        		    Dao.insert("TL_B_TO_SALEPLAT_INFO", logInfo);
        		}
        }
        
		return content;
	}	
	public static String effecttimeDiscntSubscribeSms(List<DiscntTradeData> discntTradeDatas) throws Exception {
		String content = "";
		if (discntTradeDatas != null && discntTradeDatas.size() > 0) {
			for (DiscntTradeData discntTradeData : discntTradeDatas) {
				if (discntTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)) {
					IDataset templatenew = CommparaInfoQry.queryComparaByAttrAndCode1NEW("CSM", "3466", "EFFCTTIME_DISCNT_SMS_TEMPLATE", "EFFCTTIME_DISCNT_SMS_TEMPLATE", discntTradeData.getElementId());
					if (IDataUtil.isNotEmpty(templatenew)) {
						IData templateInfo = templatenew.getData(0);
						if (discntTradeData.getElementId().equals(templateInfo.getString("PARA_CODE2"))) {
						StringBuilder templateContent = new StringBuilder();
						templateContent.append(templateInfo.getString("PARA_CODE4", ""));
						templateContent.append(templateInfo.getString("PARA_CODE5", ""));
						templateContent.append(templateInfo.getString("PARA_CODE6", ""));
						templateContent.append(templateInfo.getString("PARA_CODE7", ""));
						templateContent.append(templateInfo.getString("PARA_CODE8", ""));
						templateContent.append(templateInfo.getString("PARA_CODE9", ""));
						templateContent.append(templateInfo.getString("PARA_CODE10", ""));
						templateContent.append(templateInfo.getString("PARA_CODE11", ""));
						templateContent.append(templateInfo.getString("PARA_CODE12", ""));
						templateContent.append(templateInfo.getString("PARA_CODE13", ""));
						templateContent.append(templateInfo.getString("PARA_CODE14", ""));
						templateContent.append(templateInfo.getString("PARA_CODE15", ""));
						templateContent.append(templateInfo.getString("PARA_CODE16", ""));
						content = templateContent.toString();
						String startDate = discntTradeData.getStartDate();
						String endDate = discntTradeData.getEndDate();
						String discntName =  getDiscntInfoByCode(discntTradeData.getElementId()).getString("DISCNT_NAME","");		
						content = content.replaceAll("@DISCNT_NAME@", discntName).replaceAll("@BEGIN_DATE@", startDate).replaceAll("@END_DATE@", endDate);
							break;
						}
					}
				}
			}
		}
		return content;
	}
	
	  /**
     * 查网龄送流量上线时间调研
     * 2019-04-25 lizj
     * */
	 public static String getUserOpenDateToNow(String userId) throws Exception {
		    IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");
		    String openDate = userInfo.getString("OPEN_DATE");
		    if(StringUtils.isNotBlank(openDate)){
		    	Date now = new Date();
		    	String nowDate=SysDateMgr.date2String(now,SysDateMgr.PATTERN_STAND);
		    	//总月数
		    	 int monthSum=SysDateMgr.monthInterval(openDate,nowDate)-1;
		    	 //开户年数
		    	 int year=(int)monthSum/12;
		    	 //月数
		    	 int month=monthSum-(monthSum/12)*12;
		    	 String rel="";
		    	 if(year>0)
		    		 rel=year+"年";
		    	 
		    	if(month>0)
		    	 rel=rel+month+"个月";
		    	 
		    	if(monthSum==0)
		    		rel="0个月";
		    	
		    	 return  rel;
		    }else{
		    	return "-1";
		    }
	 }
	 
	 
	 
	 /**
	  * 查网龄送流量上线时间调研  返回opendate年月日
	  * @param userId
	  * @return
	  * @throws Exception
	  * xuzh5 2019-5-2 9:14:18
	  */
	 public static String getUserOpenTime(String userId) throws Exception {
		    IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");
		    String openDate = userInfo.getString("OPEN_DATE");
		    if(StringUtils.isNotBlank(openDate)){
		    	Date oDate=(new SimpleDateFormat(SysDateMgr.PATTERN_STAND_YYYYMMDD).parse(openDate));
		    	String nowDate=SysDateMgr.date2String(oDate,SysDateMgr.PATTERN_CHINA_DATE);
		    	
		    	return nowDate;
		    }else{
		    	return "-1";
		    }
	 }
	 
	 /**
	  * k3
	  * @param tradeId
	  * @return
	  * @throws Exception
	  */
	 public static IDataset queryTransNumberByTradeId(String tradeId) throws Exception{
		 	IData param=new DataMap();
		 	param.put("TRADE_ID", tradeId);
	        SQLParser parser = new SQLParser(param);

	        parser.addSQL("SELECT * ");
	        parser.addSQL("FROM TF_B_DISPATCH_ORDER ");
	        parser.addSQL("WHERE TRADE_ID=:TRADE_ID");
	        IDataset rtnData=Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	        System.out.println("==================rtnData=="+rtnData);
	        return rtnData;
	 }
	  /**
	  * REQ201907110021 办和多号送话费活动需求
	  * @param userId
	  * @return
	  * @throws Exception
	  * xuzh5 2019-9-3 9:14:18
	  */
	 public static String getSaleActivePrint(String tradeId) throws Exception {
		 String content =null;
		 System.out.println("REQ201907110021 办和多号送话费活动需求"+tradeId);
		 IDataset tradeRelationInfo = TradeRelaInfoQry.getTradeUURelaByTradeId(tradeId);
		 if(IDataUtil.isNotEmpty(tradeRelationInfo)){
			 for(int i=0;i<tradeRelationInfo.size();i++){
				 String modify = tradeRelationInfo.getData(i).getString("MODIFY_TAG");
				 if(BofConst.MODIFY_TAG_ADD.equals(modify))
				 {
					 String tag = tradeRelationInfo.getData(i).getString("RSRV_TAG3");
					 if("Y".equals(tag)){
						 Date now = new Date();
						 String nowDate=SysDateMgr.date2String(now,SysDateMgr.PATTERN_CHINA_DATE);
						 String serialNumB = tradeRelationInfo.getData(i).getString("SERIAL_NUMBER_B");
						 content = "您于"+nowDate+"办理和多号（副号："+serialNumB+"），5元/月。" +
						 		"您于"+nowDate+"参与“开通和多号折扣包”营销活动，首两月折扣期为1.88元/月，第三月起5元/月，一次性收取，如您2个月内退订和多号业务，则活动终止。";
					 }
				 }
			 }
		 }
		 return content;
	 }
	 
	 /**
	  * REQ201908210025   关于开发2019尊享信用购机活动的需求--短信动态字段
	  * @param tradeId
	  * @return
	  * @throws Exception
	  * xuzh5 2019-9-10 9:14:18
	  */
	 public static String saleActiveFeelZHIJ (String packageId,String rsrvStr6) throws Exception {
		 String result =null;
		 IDataset chas = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
		 for(int j=0;j<chas.size();j++){
			 String fieldName = chas.getData(j).getString("GOODS_SALE");
			 if(StringUtils.isNotBlank(fieldName)){
				 BigDecimal salePrice = new BigDecimal(rsrvStr6);
				 result =(salePrice.subtract(new BigDecimal(chas.getData(j).getString("GOODS_SALE","0"))).multiply(new BigDecimal("0.01"))).toString();
			 }
		 }
					 
		 return result;
	 }
	 public static String saleActiveFeelCal(String deviceCostt,String rsrvStr6) throws Exception {
		 String result =null;
		 BigDecimal deviceCost = new BigDecimal(deviceCostt);
    	 BigDecimal salePrice = new BigDecimal(rsrvStr6);
    	 result = (salePrice.subtract(salePrice.subtract(deviceCost).multiply(new BigDecimal("0.9")))).multiply(new BigDecimal("0.01")).toString();
		 
		 return result;
	 }
	 
	 
	 public static String familyWideJXSn(RegTradeData rtd) throws Exception {
		 String result ="";
		 String tradeId =rtd.getTradeId();
		 String serialNumber="";
		 
		 IDataset imsSnDataset = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,"IMSTRADE"); 
		 if (IDataUtil.isNotEmpty(imsSnDataset)) {
			 serialNumber=imsSnDataset.getData(0).getString("RSRV_STR3");
			 
			 log.debug("---RegTradeData---cnc----rss++++"+imsSnDataset);
		}else {
			
			return result;
		}
		
		 IDataset rss = ResCall.getMphonecodeInfo(serialNumber);
		 
		 log.debug("---serialNumber---cnc----rss++++"+serialNumber);
		 
		 log.debug("---familyWideJXSn---cnc----rss++++"+rss);
		 
		 if (IDataUtil.isNotEmpty(rss)) {
			 IData rssData=rss.getData(0);
			 if (rssData.getString("BEAUTIFUL_TAG", "").equals("1")) {
				 IDataset commparaSet = CommparaInfoQry.getCommparaInfoBy5("CSM", "7466", "FAMILY_WIDE_USER", rssData.getString("LEVEL_ID"), CSBizBean.getVisit().getStaffEparchyCode(), null);
				 
				 log.debug("---familyWideJXSn---cnc----commparaSet++++"+commparaSet);
				 
				 IData commparas=commparaSet.getData(0);
				 if (IDataUtil.isNotEmpty(commparaSet)) {
					String paraCode2 = commparas.getString("PARA_CODE2");  //预存款
					String paraCode3 = commparas.getString("PARA_CODE3");  //月消费
					result = result + "~"+"尊敬的客户，开通号码后，您同意按照以下约定使用号码:"+"~";
					result = result + "1、承诺使用中国移动海南公司通信服务；"+"~";
					result = result + "2、预存款"+paraCode2+"一次性到帐并生效，生效后不能办理退款；"+"~";
					result = result + "3、从活动生效当月起，三年内约定每月最低消费"+paraCode3+"元（最低消费含套餐月使用费、通话费及其他费用，";
					result = result + "不含信息费、宽带费等费用），若约定期内月实际消费低于约定的月最低消费，将按约定的月最低消费收取费用；"+"~";
					result = result + "4、开户即日起三年内，不能办理过户及主动销户业务；"+"~";
					result = result + "5、吉祥号码办理主动销户成功后将无法复机，如需复入网则按新用户入网方式来开户，新开户按现有吉祥号码营销方案执行；"+"~";
					result = result + "6、吉祥号码系统开户时，中国移动海南公司不向客户收取任何形式的号码选号费、资源占用费或押金；"+"~";
					result = result + "7、如因无法安装或其他原因需要撤单，请前往当地营业厅前台办理。";
				}
			}
			 
		}
		 return result;
	 }
	 
	 
	 /**
	  * REQ202005050005 开发智能家居有一套营销活动—BOSS侧 
	  * @param tradeId
	  * @return
	  * @throws Exception
	  * lizj 2020-5-15 
	  */
	 public static String otherTopSetPlatSvc(String serviceId) throws Exception {
		 String result ="";
		 if(StringUtils.isNotBlank(serviceId)){
			 IDataset commparaInfos2509 = CommparaInfoQry.getCommparaByCode4to6("CSM","2509","TOPSETBOX",serviceId,null,null,"0898");
			 if(IDataUtil.isNotEmpty(commparaInfos2509)){
				 result = commparaInfos2509.first().getString("PARA_CODE20");
			 }
		 }
		 return result;
	 }
	 public static String otherWideNetPlatSvc(String serviceId) throws Exception {
		 String result ="";
		 if(StringUtils.isNotBlank(serviceId)){
			 IDataset commparaInfos2509 = CommparaInfoQry.getCommparaByCode4to6("CSM","2509","WIDENET",serviceId,null,null,"0898");
			 if(IDataUtil.isNotEmpty(commparaInfos2509)){
				 result = commparaInfos2509.first().getString("PARA_CODE20");
			 }
		 }
		 return result;
	 }
	 public static String otherWideNetPlatSvc2(String serviceId) throws Exception {
		 String result ="";
		 if(StringUtils.isNotBlank(serviceId)){
			 IDataset commparaInfos2509 = CommparaInfoQry.getCommparaByCode4to6("CSM","2509","WIDENET",serviceId,null,null,"0898");
			 if(IDataUtil.isNotEmpty(commparaInfos2509)){
				 result = commparaInfos2509.first().getString("PARA_CODE21");
			 }
		 }
		 return result;
	 }
	 
}
