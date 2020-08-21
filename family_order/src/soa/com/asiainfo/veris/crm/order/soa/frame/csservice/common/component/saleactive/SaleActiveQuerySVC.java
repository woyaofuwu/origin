
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.ailk.org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.label.LabelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.GGCardInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SaleTerminalLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TerminalOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;

public class SaleActiveQuerySVC extends CSBizService
{
    private static final long serialVersionUID = 5063927661840074197L;

    private static transient Logger logger = Logger.getLogger(SaleActiveQuerySVC.class);

    public IDataset getCampnTypes(IData params) throws Exception
    {
        String labelId = params.getString("LABEL_ID");
        if (StringUtils.isBlank(labelId))
        {
            String userId = params.getString("USER_ID");
            IDataset userInfo = UserInfoQry.getUserInfoByUserId(userId, "0", params.getString(Route.ROUTE_EPARCHY_CODE));

            if ("00".equals(userInfo.getData(0).getString("NET_TYPE_CODE")))
            {
                labelId = SaleActiveConst.SALE_ACTIVE_LABEL_SN;
            }
            if ("18".equals(userInfo.getData(0).getString("NET_TYPE_CODE")))
            {
                labelId = SaleActiveConst.SALE_ACTIVE_LABEL_GH;
            }
        }

        return LabelInfoQry.getLabelsByLabelId(labelId);
    }

    /**
     * IPHONE6活动前提条件校验 caolin 20141015
     */
    public IDataset isIphone6Cons(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        IData result = new DataMap();
        SaleActiveQueryBean saleActiveQueryBean = BeanManager.createBean(SaleActiveQueryBean.class);
        result = saleActiveQueryBean.isIphone6Cons(input);
        results.add(result);
        return results;
    }

    /**
     * IPHONE6裸机销售校验 caolin 20141015
     */
    public IDataset iphone6CheckIMEISaleInfo(IData input) throws Exception
    {
        String iphone6_imei = input.getString("IPHONE6_IMEI");
        String sale_tag = input.getString("SALE_TAG");
        IDataset results = HwTerminalCall.iphone6CheckIMEISaleInfo(iphone6_imei, sale_tag);
        return results;
    }

    /**
     * 获取限制营销活动 caolin 20141015
     */
    public IDataset getSaleTerminalLimitInfo(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        String product_id = input.getString("PRODUCT_ID");
        String package_id = input.getString("PACKAGE_ID");
        String terminal_type_code = input.getString("TERMINAL_TYPE_CODE");
        String device_model_code = input.getString("DEVICE_MODEL_CODE");
        String eparchy_code = input.getString("EPARCHY_CODE");
        IData terminalLimit = SaleTerminalLimitInfoQry.queryByPK(product_id, package_id, terminal_type_code, device_model_code, eparchy_code);
        results.add(terminalLimit);
        return results;
    }
    
    public IData checkCreditPurchases(IData input) throws Exception
    {
        IData result = new DataMap();
        String product_id = input.getString("PRODUCT_ID");
        IDataset commparaInfos = CommparaInfoQry.getCommNetInfo("CSM","3119",product_id) ;
        if(DataUtils.isEmpty(commparaInfos)){
            result.put("CREDIT_PURCHASES", "0");
        }else{
            result.put("CREDIT_PURCHASES", "1");
        }
        return result;
    }
    
    
    /**
     * 根据LABEL_ID和USER_ID查询全部用户可办理营销方案
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryAllAvailableProducts(IData input) throws Exception {
        IDataset allProducts = new DatasetList();

        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
        IDataset campnTypes = CSAppCall.call("CS.SaleActiveQuerySVC.getCampnTypes", input);
        for (Object obj : campnTypes) {
            IData capmnType = (IData) obj;
            IData inParam = new DataMap();
            IDataset productsInfo;
            inParam.put("CAMPN_TYPE", capmnType.getString("LABEL_ID"));
            inParam.put("USER_ID", input.getString("USER_ID"));
            inParam.put("EPARCHY_CODE", input.getString("EPARCHY_CODE"));
            try {
                productsInfo = queryProductsByLabel(inParam);
                if("1".equals(input.getString("CREDIT_PURCHASES"))&&IDataUtil.isNotEmpty(productsInfo)){
                	IDataset productsInfo2 =new DatasetList();
                	IDataset commparaInfos = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","3119",null,null);
                	String commpara="|";
                	for(int i=0;i<commparaInfos.size();i++){
                		if("Y".equals(commparaInfos.getData(i).getString("PARA_CODE8"))){
                			commpara = commpara+commparaInfos.getData(i).getString("PARAM_CODE")+"|";
                		}
                	}
                	for(Object products : productsInfo){
                		IData product = (IData) products;
                		if(commpara.indexOf(product.getString("PRODUCT_ID")) > -1){
                			product.put("PRODUCT_NAME", product.getString("UP_CATALOG_NAME")+" | "+product.getString("PRODUCT_NAME"));
                			product.put("PRODUCT_TITLE", product.getString("UP_CATALOG_ID"));
                			productsInfo2.add(product);
                		}
                	}
                	productsInfo.clear();
                	productsInfo.addAll(productsInfo2);
                	
                }
            } catch (Exception e) {
                // 当用户无法办理iPhone6活动类型（YX11）时，该异常不再抛到前台。
                continue;
            }
            if (IDataUtil.isNotEmpty(productsInfo)) {
                allProducts.addAll(productsInfo);
            }
        }

        return allProducts;
    }

    public IDataset queryProductsByLabel(IData input) throws Exception
    {
        String labelId = input.getString("CAMPN_TYPE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        // 对IPHONE6活动进行处理
        if (StringUtils.equals(labelId, "YX11"))
        {
            input.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            IData isIphone6Cons = CSAppCall.call("CS.SaleActiveQuerySVC.isIphone6Cons", input).first();
            if (!isIphone6Cons.getBoolean("RESULT"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, isIphone6Cons.getString("RESULT_INFO", "该用户不能办理该活动！"));
            }
        }
        IDataset results = UpcCall.qryCatalogsByUpCatalogId(labelId);
        // 查询活动类型转译文本值
        for (Object obj : results) {
            IData catalog = (IData) obj;
            IDataset labelInfo = UpcCall.qryCatalogByCatalogId(labelId);
            if (IDataUtil.isNotEmpty(labelInfo)) {
                String catalogName = labelInfo.first().getString("CATALOG_NAME");
                catalog.put("UP_CATALOG_NAME", catalogName);
            }
        }
        // IDataset results = ProductInfoQry.querySaleActiveProductByLabel(labelId, eparchyCode);
        // ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), results);
        IDataset productInfos = SaleActiveUtil.filterSaleProductsByParamAttr522(results, labelId);

        SaleActiveUtil.sortSaleActivesByTotalNum(productInfos);
        
        IData usd = UcaInfoQry.qryUserInfoByUserIdFromDB(input.getString("USER_ID"),"0898");
        String sn = "KD_"+usd.getString("SERIAL_NUMBER");
        IDataset tipresults = new DatasetList();
        if(IDataUtil.isEmpty(UserInfoQry.getUserInfoBySn(sn, "0", null, "0898")))
        {
            if(IDataUtil.isEmpty(TradeInfoQry.getMainTradeBySN(sn, "600")))
            {
                for(int i=0;i<productInfos.size();i++)
                {
                    IData saleProduct = productInfos.getData(i);
                    IDataset commparaInfos = CommparaInfoQry.getCommparaByCode1("CSM", "8424", "YXXX", labelId, null);
                    if(IDataUtil.isNotEmpty(commparaInfos))
                    {
                        saleProduct.put("TIP_FLAG", "1");
                        saleProduct.put("TIP_INFO", commparaInfos.getData(0).getString("PARA_CODE20", "购机客户还未办理宽带，建议给予推介！"));
                    }
                
                    tipresults.add(saleProduct);
                }
                return tipresults;
            }else{
                return productInfos;
            }
            
            
        }else
        {
            return productInfos;
        }     
    }

    public IData querySaleActiveInfoByPackageId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String productId = input.getString("PRODUCT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");

        // IDataset result = PkgExtInfoQry.queryPackageExtInfo(packageId, eparchyCode);
        IData saleactive = UPackageExtInfoQry.qryPkgExtEnableByPackageId(packageId);
        if (IDataUtil.isEmpty(saleactive))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_1);
        }

        IData pkgInfo = UPackageInfoQry.getPackageLimitByPackageId(packageId);
        if (IDataUtil.isEmpty(pkgInfo))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_1);
        }

        saleactive.put("PACKAGE_NAME", pkgInfo.getString("PACKAGE_NAME"));
        saleactive.put("LIMIT_TYPE", pkgInfo.getString("LIMIT_TYPE", "D"));
        saleactive.put("MIN_NUMBER", pkgInfo.getString("MIN_NUMBER"));
        saleactive.put("MAX_NUMBER", pkgInfo.getString("MAX_NUMBER"));

        IData product = UProductInfoQry.qrySaleActiveProductByPK(productId);
        if (IDataUtil.isEmpty(product))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_24);
        }

        saleactive.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        saleactive.put("PRODUCT_ID", productId);
        saleactive.put("PRODUCT_NAME", product.getString("PRODUCT_NAME"));
        saleactive.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
        saleactive.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        
        if("Y".equals(input.getString("SP_TAG"))){//商品订购新增字段
            saleactive.put("SP_TAG", "Y");
        }
        IDataset saleActiveDataSet = CSAppCall.call("CS.SaleActiveDateSVC.callActiveStartEndDate", saleactive);

        IData saleActiveDate = saleActiveDataSet.getData(0);
        saleactive.put("START_DATE", saleActiveDate.getString("START_DATE"));
        saleactive.put("END_DATE", saleActiveDate.getString("END_DATE"));
        saleactive.put("BOOK_DATE", saleActiveDate.getString("BOOK_DATE"));
        saleactive.put("IS_BOOK", saleActiveDate.getString("IS_BOOK"));
        saleactive.put("ONNET_START_DATE", saleActiveDate.getString("ONNET_START_DATE"));
        saleactive.put("ONNET_END_DATE", saleActiveDate.getString("ONNET_END_DATE"));
        if("Y".equals(input.getString("SP_TAG"))){//商品订购新增字段
            saleactive.put("REL_PACKAGE_ID", saleActiveDate.getString("REL_PACKAGE_ID"));
            saleactive.put("NET_PACKAGE_ID", saleActiveDate.getString("NET_PACKAGE_ID"));
        }
        
        IDataset combineElementLimits = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "1154", productId, packageId, eparchyCode);
        if (IDataUtil.isNotEmpty(combineElementLimits))
        {
            saleactive.put("COMBINE_MIN", combineElementLimits.getData(0).getString("PARA_CODE3"));
            saleactive.put("COMBINE_MAX", combineElementLimits.getData(0).getString("PARA_CODE4"));
        }
        return saleactive;
    }

    public IDataset querySaleActives(IData input) throws Exception
    {
        IData otherInfo = new DataMap();
        otherInfo.put("OTHER_INFO", "true");
        String productId = input.getString("PRODUCT_ID", "");
        String campnType = input.getString("CAMPN_TYPE", "");
        String eparchyCode = input.getString("EPARCHY_CODE");
        String searchContent = input.getString("SEARCH_CONTENT", "");

        if (StringUtils.isBlank(productId) && StringUtils.isBlank(searchContent))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "PRODUCT_ID,SEARCH_CONTENT不能都为空!");
        }

        IDataset saleActives = new DatasetList();
        if ("|YX02|YX04|YX05|YX11|YX12|".indexOf(campnType) < 0)
        {
            return saleActives;
        }

        if (StringUtils.isBlank(searchContent))
        {
            saleActives = UpcCall.qryOfferTempChasByCatalogId(productId);
            //saleActives = SaleActiveUtil.filterSalePackagesByParamAttr526(saleActives, productId, campnType, false);
            // saleActives = ProductPkgInfoQry.qryNoTerminalActiveByPid(campnType, productId, eparchyCode);
            if("1".equals(input.getString("CREDIT_PURCHASES"))){
            	saleActives = SaleActiveUtil.filterSalePackagesByParamAttr526New(saleActives, productId, campnType, false,input);
            }else{
            	saleActives = SaleActiveUtil.filterSalePackagesByParamAttr526(saleActives, productId, campnType, false);
            }
        }
        else
        {
            saleActives = SaleActiveUtil.searchActives("SALEACTIVE", searchContent, productId, campnType, eparchyCode);
            saleActives = SaleActiveUtil.filterSalePackagesByParamAttr526(saleActives, productId, campnType, true);
        }
        
        if("1".equals(input.getString("CREDIT_PURCHASES"))){
        	IDataset packagesInfo2 =new DatasetList();
        	IDataset commparaInfos = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","3119",input.getString("PRODUCT_ID"),null);
        	String commpara="|";
        	for(int i=0;i<commparaInfos.size();i++){
        		if("Y".equals(commparaInfos.getData(i).getString("PARA_CODE8"))){
        			commpara = commpara+commparaInfos.getData(i).getString("PARA_CODE4")+"|";
        		}
        	}
        	for(Object packages : saleActives){
        		IData product = (IData) packages;
        		if(commpara.indexOf(product.getString("PACKAGE_ID")) > -1){
        			packagesInfo2.add(product);
        		}
        	}
        	saleActives.clear();
        	saleActives.addAll(packagesInfo2);
        	
        }
        
        if (IDataUtil.isEmpty(saleActives))
        {
            return saleActives;
        }

        // filter priv
        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), saleActives);

        // 循环调用规则
        SaleActiveCheckBean.checkPackages(saleActives, input.getString("SERIAL_NUMBER"));

        //
        String allFail = "true";
        for (int i = 0, size = saleActives.size(); i < size; i++)
        {
            IData saleActive = saleActives.getData(i);
            if (!"1".equals(saleActive.getString("ERROR_FLAG")))
            {
                allFail = "false";
                break;
            }
        }

        otherInfo.put("ALL_FAIL", allFail);

        // sort
        DataHelper.sort(saleActives, "PACKAGE_NAME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        DataHelper.sort(saleActives, "ERROR_FLAG", IDataset.TYPE_INTEGER);

        saleActives.add(otherInfo);

        return saleActives;
    }

    public IDataset querySaleActivesByImei(IData input) throws Exception
    {
        IData param = new DataMap();
        IData otherInfo = new DataMap();
        otherInfo.put("OTHER_INFO", "true");
        param.put("RES_NO", input.getString("NEW_IMEI"));
        param.put("BUY_TYPE", SaleActiveConst.TERMINAL_BUY_TYPE_PURCHASE);
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("SALE_STAFF_ID", input.getString("SALE_STAFF_ID"));
        param.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
        param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));

        IDataset terminalInfos = CSAppCall.call("CS.TerminalQuerySVC.getEnableTerminalByResNo", param);
        IData terminalInfo = terminalInfos.getData(0);

        String deviceTypeCode = terminalInfo.getString("TERMINAL_TYPE_CODE");
        String deviceModelCode = terminalInfo.getString("DEVICE_MODEL_CODE");
        String deviceCost = terminalInfo.getString("DEVICE_COST");
        String salePrice = terminalInfo.getString("SALE_PRICE");

        String searchContent = input.getString("SEARCH_CONTENT", "");
        String productId = input.getString("PRODUCT_ID", "");
        String packageId = input.getString("PACKAGE_ID");
        String campnType = input.getString("CAMPN_TYPE", "");
        String eparchyCode = input.getString("EPARCHY_CODE");

        IDataset salePackages = new DatasetList();

        if (StringUtils.isNotBlank(packageId))
        {
            // salePackages = ProductPkgInfoQry.qryTerminalActiveByPidPkIdRid(campnType, productId, packageId,
            // deviceModelCode, eparchyCode);
            // salePackages = UpcCall.qryOfferTempChasByCatalogIdOfferId(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId,
            // productId, campnType, deviceModelCode);
            salePackages = UpcCall.qrySaleActiveOffersByResIdOfferIdCatalogId(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, productId, campnType, deviceModelCode);
            salePackages = SaleActiveUtil.filterSalePackagesByParamAttr526(salePackages, productId, campnType, false);
        }
        else if (StringUtils.isBlank(searchContent))
        {
            // salePackages = ProductPkgInfoQry.qryTerminalActiveByPidRid(campnType, productId, deviceModelCode,
            // eparchyCode);
            // salePackages = UpcCall.qryOfferTempChasByCatalogId(productId, campnType, deviceModelCode);
            salePackages = UpcCall.qrySaleActiveOffersByResIdOfferIdCatalogId(null, null, productId, campnType, deviceModelCode);
            if("1".equals(input.getString("CREDIT_PURCHASES"))){
            	salePackages = SaleActiveUtil.filterSalePackagesByParamAttr526New(salePackages, productId, campnType, false,input);
            }else{
            	salePackages = SaleActiveUtil.filterSalePackagesByParamAttr526(salePackages, productId, campnType, false);
            }
            
        }
        else
        {
            salePackages = SaleActiveUtil.searchActives("SALEACTIVE_FOR_IMEI", searchContent, productId, campnType, eparchyCode);
            salePackages = SaleActiveUtil.filterSalePackagesByParamAttr526(salePackages, productId, campnType, true);
        }
        
        if("1".equals(input.getString("CREDIT_PURCHASES"))){
        	IDataset packagesInfo2 =new DatasetList();
        	IDataset commparaInfos = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","3119",input.getString("PRODUCT_ID"),null);
        	String commpara="|";
        	for(int i=0;i<commparaInfos.size();i++){
        		if("Y".equals(commparaInfos.getData(i).getString("PARA_CODE8"))){
        			commpara = commpara+commparaInfos.getData(i).getString("PARA_CODE4")+"|";
        		}
        	}
        	for(Object packages : salePackages){
        		IData product = (IData) packages;
        		if(commpara.indexOf(product.getString("PACKAGE_ID")) > -1){
        			packagesInfo2.add(product);
        		}
        	}
        	salePackages.clear();
        	salePackages.addAll(packagesInfo2);
        	
        }

        DataHelper.sort(salePackages, "PACKAGE_NAME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);

        if (IDataUtil.isEmpty(salePackages))
            return salePackages;

        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), salePackages);

        IData svcParam = new DataMap();

        svcParam.put("SALE_ACTIVES", salePackages);
        svcParam.put("DEVICE_TYPE_CODE", deviceTypeCode);
        svcParam.put("DEVICE_MODEL_CODE", deviceModelCode);
        svcParam.put("DEVICE_COST", deviceCost);
        svcParam.put("SALE_PRICE", salePrice);
        svcParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        salePackages = CSAppCall.call("CS.SalePackagesFilteSVC.filterPackagesByTerminalConfig", svcParam);

        // REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151211
        // 如果入参中含有礼品码参数，则进行对礼品码的校验，同时过滤出礼品码对应能办理的活动包
        String isGiftActive = input.getString("IS_GIFT_ACTIVE", "");
        String giftCode = input.getString("GIFT_CODE", "");
        // IS_GIFT_ACTIVE值为1时，表示含有礼品码
        if ("1".equals(isGiftActive))
        {
            // 根据礼品码的配置，对包进行过滤
            salePackages = giftSaleActiveDeal(salePackages, giftCode);
        }

        // 20160111 by songlm 冼乃捷 紧急需求，暂无需求名，作用：将终端类型传进规则内
        for (int i = 0, size = salePackages.size(); i < size; i++)
        {
            IData saleActive = salePackages.getData(i);
            saleActive.put("DEVICE_MODEL_CODE", deviceModelCode);
			saleActive.put("PACKAGE_TITLE", salePrice);
        }
        // end

        // 循环调用规则
        SaleActiveCheckBean.checkPackages(salePackages, input.getString("SERIAL_NUMBER"));
        String allFail = "true";
        for (int i = 0, size = salePackages.size(); i < size; i++)
        {
            IData saleActive = salePackages.getData(i);
            if (!"1".equals(saleActive.getString("ERROR_FLAG")))
            {
                allFail = "false";
                break;
            }
        }
        otherInfo.put("ALL_FAIL", allFail);

        otherInfo.put("GOODS_INFO", SaleActiveUtil.buildTerminalInfo(terminalInfo));
        salePackages.add(otherInfo);

        DataHelper.sort(salePackages, "ERROR_FLAG", IDataset.TYPE_INTEGER);

        return salePackages;
    }

    public IDataset querySaleCombineByPackageId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");

        // IDataset combines = SaleCombineInfoQry.queryByPkgIdEparchy(packageId, eparchyCode);
        IDataset combines = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        DataHelper.sort(combines, "COMBINE_NAME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        
        int size = combines.size();
        for (int i = 0; i < size; i++)
        {
            IData combine = combines.getData(i);
            combine.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
            combine.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            combine.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            combine.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
            combine.put("BASIC_CAL_START_DATE", input.getString("BASIC_DATE"));
            combine.put("CUSTOM_ABSOLUTE_START_DATE", input.getString("BASIC_DATE"));
            combine.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            IDataset combineDateSet = CSAppCall.call("CS.SaleActiveElementDateSVC.callElementStartEndDate", combine);
            IData combineDate = combineDateSet.getData(0);

            combine.put("START_DATE", combineDate.getString("START_DATE"));
            combine.put("END_DATE", combineDate.getString("END_DATE"));
        }

        return combines;
    }

    public IDataset querySaleCreditsByPackageId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");

        String serialNumber = input.getString("SERIAL_NUMBER");

        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);

        // IDataset credits = SaleCreditInfoQry.queryByPkgIdEparchy(packageId, eparchyCode);
        IDataset credits = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_CREDIT);

        credits = SaleActiveUtil.filterElementByCreditClass(credits, uca.getUserId());

        for (int i = 0, size = credits.size(); i < size; i++)
        {
            IData credit = credits.getData(i);
            credit.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
            credit.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            credit.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            credit.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
            credit.put("BASIC_CAL_START_DATE", input.getString("BASIC_DATE"));
            credit.put("CUSTOM_ABSOLUTE_START_DATE", input.getString("BASIC_DATE"));
            credit.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

            IDataset creditDateSet = CSAppCall.call("CS.SaleActiveElementDateSVC.callElementStartEndDate", credit);
            IData creditDate = creditDateSet.getData(0);

            credit.put("START_DATE", creditDate.getString("START_DATE"));
            credit.put("END_DATE", creditDate.getString("END_DATE"));
        }

        return credits;
    }

    public IDataset querySaleDepositsByPackageId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");

        UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));

        // IDataset deposits = SaleDepositInfoQry.querySaleDepositByPackageIdEparchy(packageId, eparchyCode);
        IDataset deposits = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT);

        deposits = SaleActiveUtil.filterElementByCreditClass(deposits, uca.getUserId());

        for (int i = 0, size = deposits.size(); i < size; i++)
        {
            IData deposit = deposits.getData(i);

            deposit.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
            deposit.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            deposit.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            deposit.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
            deposit.put("BASIC_CAL_START_DATE", input.getString("BASIC_DATE"));
            deposit.put("CUSTOM_ABSOLUTE_START_DATE", input.getString("BASIC_DATE"));
            deposit.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            IDataset dateDataset = CSAppCall.call("CS.SaleActiveElementDateSVC.callElementStartEndDate", deposit);
            IData dateData = dateDataset.getData(0);

            deposit.put("START_DATE", dateData.getString("START_DATE"));
            deposit.put("END_DATE", dateData.getString("END_DATE"));

            IData feeParam = new DataMap();
            feeParam.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            feeParam.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
            feeParam.put("ELEMENT_TYPE_CODE", deposit.getString("ELEMENT_TYPE_CODE"));
            feeParam.put("ELEMENT_ID", deposit.getString("ELEMENT_ID"));
            feeParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            IDataset feeDataSet = CSAppCall.call("CS.SaleActiveElementFeeSVC.calElementFee", feeParam);
            if (IDataUtil.isNotEmpty(feeDataSet))
            {
                deposit.putAll(feeDataSet.getData(0));
                /**
                 * REQ201604180021 网上营业厅合约终端销售价格调整需求 chenxy3 20160428
                 */
                IData param = new DataMap();
                param.put("FEE", deposit.getString("FEE"));
                param.put("ELEMENT_TYPE_CODE", deposit.getString("ELEMENT_TYPE_CODE"));
                param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
                param.put("PACKAGE_ID", packageId);
                param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
                param.put("NET_ORDER_ID", input.getString("NET_ORDER_ID"));
                String fee = getTerminalOrderInfo(param);

                
                
                /**
                 *  关于开发2019年校园营销活动-校园老客户花呗分期送终端活动的需求 lizj 20190806
                 */
                IDataset comm2601 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "2601", input.getString("PRODUCT_ID"), packageId, eparchyCode);
                if (IDataUtil.isNotEmpty(comm2601)){
                	  String paraCode2 = comm2601.first().getString("PARA_CODE2");//花呗金额
                	  String paraCode5 = comm2601.first().getString("PARA_CODE5");//预存金额
                	  String paraCode7 = comm2601.first().getString("PARA_CODE7");//购机款金额
                	
                	  IData callParam = new DataMap();
                	  if(StringUtils.isNotBlank(input.getString("NEW_IMEI"))){
                		  callParam.put("RES_NO", input.getString("NEW_IMEI"));
                          callParam.put("PRODUCT_ID", input.getString("PRODUCT_ID", ""));
                          callParam.put("CAMPN_TYPE", input.getString("CAMPN_TYPE", ""));
                          callParam.put("TM_CALL_TYPE", "1");
                          callParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
                          IDataset resDataSet = CSAppCall.call("CS.TerminalQuerySVC.getTerminalByResNoOnly", callParam);
                          if (IDataUtil.isNotEmpty(resDataSet))
                          {
                        	  /**
                               *即配置某个档次的营销包对应的花呗金额为X，预存金额至少为Y。当用户办理的终端销售价为Z元时
    						   *则有以下情况：
    						   *1、当Z+Y>=X时，系统显示该档次的营销包购机款为Z，预存款为Y
    						   *2、当Z+Y<X时,系统显示该档次的营销包购机款为Z，预存款为X-Z
                               */
                              String deviceCost = resDataSet.getData(0).getString("SALE_PRICE", "0"); // 终端成本价(结算价(进货价))
                              if(deviceCost.compareTo(paraCode7)<0){
                            	  BigDecimal feeSub = new BigDecimal(paraCode2).subtract(new BigDecimal(deviceCost));
                            	  fee = feeSub.toString();
                              }else{
                            	  fee = paraCode5 ;
                              }
                          }
                	  }
                      
                }

                deposit.put("FEE", fee);
            }
        }

        return deposits;
	}

    /**
     * REQ201604180021 网上营业厅合约终端销售价格调整需求 chenxy3 20160428
     */
    public String getTerminalOrderInfo(IData input) throws Exception
    {

        String fee_old = input.getString("FEE");
        String elementTypeCode = input.getString("ELEMENT_TYPE_CODE");
        String productId = input.getString("PRODUCT_ID");
        String packageId = input.getString("PACKAGE_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String orderId = input.getString("NET_ORDER_ID", "");

        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("NET_ORDER_ID", orderId);

        IDataset terminalOrderInfos = CSAppCall.call("CS.SaleActiveQuerySVC.qryTerminalOrderInfoForCheck", param);// TerminalOrderInfoQry.qryTerminalOrderInfoForCheck(orderId,productId,
                                                                                                                  // packageId,
                                                                                                                  // serialNumber,"0","0");
        if (terminalOrderInfos != null && terminalOrderInfos.size() > 0)
        {
            String orderPrice = terminalOrderInfos.getData(0).getString("ORDER_PRICE", "");// 订单总额
            String rsrvNum2 = terminalOrderInfos.getData(0).getString("RSRV_NUM2", "");// 购机款
            String rsrvNum3 = terminalOrderInfos.getData(0).getString("RSRV_NUM3", "0");// 预存款
            if (Double.parseDouble(orderPrice) * 100 != (Double.parseDouble(rsrvNum3) + Double.parseDouble(rsrvNum2)))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "订单总额不等于购机款与预存款相加。订单总额：" + Double.parseDouble(orderPrice) + "，相加总额：" + (Double.parseDouble(rsrvNum3) + Double.parseDouble(rsrvNum2)) * 0.01 + "。");
            }
            String orderModelCode = terminalOrderInfos.getData(0).getString("DEVICE_MODEL_CODE", "0");// 预定的终端编码

            if ("G".equals(elementTypeCode))
            {
                String newIMEI = input.getString("NEW_IMEI", "");
                String labelId = input.getString("CAMPN_TYPE", "");
                /** 根据手机串号调用华为接口获取型号信息 */
                IData terminalParam = new DataMap();
                terminalParam.put("RES_NO", newIMEI);
                terminalParam.put("PRODUCT_ID", productId);
                terminalParam.put("CAMPN_TYPE", labelId);
                TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
                IData terminalInfo = terminalBean.getTerminalByResNoOnly(terminalParam);
                String newModelCode = terminalInfo.getString("DEVICE_MODEL_CODE", "");
                if (!"".equals(newModelCode) && !newModelCode.equals(orderModelCode))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "预订的终端型号与输入的不符。预订型号：" + orderModelCode + "，录入型号：" + newModelCode + "。");
                }
                if (!"".equals(rsrvNum2))
                {
                    // 当前办理活动的购机款小于rsrv_num2时，本次购机款以rsrv_num2为准
                    // 当前办理活动的购机款大于rsrv_num2，拦截办理
                    if (Double.parseDouble(fee_old) < Double.parseDouble(rsrvNum2))
                    {
                        fee_old = rsrvNum2;
                    }
                    else if (Double.parseDouble(fee_old) > Double.parseDouble(rsrvNum2))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "购机款价格已发生变动，请重新办理。订单价：" + Integer.parseInt(rsrvNum2) * 0.01 + "，终端现价：" + Integer.parseInt(fee_old) * 0.01 + "。");
                    }
                }
            }
            if ("A".equals(elementTypeCode))
            {
                if (!"".equals(rsrvNum3))
                {
                    fee_old = rsrvNum3;
                }
                else
                {
                    if (!"".equals(rsrvNum2))
                    {
                        fee_old = "0";
                    }
                }
            }
        }
        return fee_old;
    }

    /**
     * REQ201604180021 网上营业厅合约终端销售价格调整需求 chenxy3 20160428
     */
    public IDataset qryTerminalOrderInfoForCheck(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String packageId = input.getString("PACKAGE_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String orderId = input.getString("NET_ORDER_ID", "");
        return TerminalOrderInfoQry.qryTerminalOrderInfoForCheck(orderId, productId, packageId, serialNumber, "0", "0");
    }

    /** *********END *****************/

    public IDataset querySaleDiscntsByPackageId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        // IDataset discnts = DiscntInfoQry.queryDiscntsByPkgIdEparchy(packageId, eparchyCode);
        IDataset discnts = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_DISCNT);

        int size = discnts.size();
        for (int i = 0; i < size; i++)
        {
            IData discnt = discnts.getData(i);

            discnt.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
            discnt.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            discnt.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            discnt.put("PACKAGE_ID", packageId);
            discnt.put("BASIC_CAL_START_DATE", input.getString("BASIC_DATE"));
            discnt.put("CUSTOM_ABSOLUTE_START_DATE", input.getString("BASIC_DATE"));
            discnt.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

            IDataset dataset = CSAppCall.call("CS.SaleActiveElementDateSVC.callElementStartEndDate", discnt);
            IData data = dataset.getData(0);

            discnt.put("START_DATE", data.getString("START_DATE"));
            discnt.put("END_DATE", data.getString("END_DATE"));
            discnt.put("NOW_DAY", SysDateMgr.getSysTime());
            discnt.put("NEXT_ACCT_DAY", SysDateMgr.firstDayOfMonth(1));
        }
        return discnts;
    }

    public IDataset querySaleGoodsByPackageId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");

        IDataset saleGoodDataset = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_SALEGOODS);// SaleGoodsInfoQry.queryByPkgIdEparchy(packageId,
        
        // eparchyCode);
        
        //BUS201911040060 关于新增新信用购活动办理条件的需求 --start
        String productId = input.getString("PRODUCT_ID");
        String sn = input.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySnAllCrm(sn);
        if(IDataUtil.isNotEmpty(userInfo)){
        	String userId = userInfo.getString("USER_ID");
        	
        	IDataset commParaInfo1202 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1202", productId, packageId);
            if(IDataUtil.isNotEmpty(commParaInfo1202)){
            	String salePrice="";
            	IData callParam = new DataMap();
                callParam.put("RES_NO", input.getString("NEW_IMEI", ""));
                callParam.put("PRODUCT_ID", input.getString("PRODUCT_ID", ""));
                callParam.put("CAMPN_TYPE", input.getString("CAMPN_TYPE", ""));
                callParam.put("TM_CALL_TYPE", "1");
                callParam.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
                IDataset resDataSet = CSAppCall.call("CS.TerminalQuerySVC.getTerminalByResNoOnly", callParam);
                if (IDataUtil.isNotEmpty(resDataSet))
                {
                    salePrice = resDataSet.getData(0).getString("SALE_PRICE", "");
                    
                    IDataset commparaInfos = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","1202",productId,null);
         	    	IDataset UserSaleActiveInfos = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId, Route.CONN_CRM_CG);
         	    	BigDecimal downAll = new BigDecimal("0");
         	    	if(IDataUtil.isNotEmpty(commparaInfos)){
         	    		
         	    		for(int i=0;i<commparaInfos.size();i++){
        	    			String packageId2 = commparaInfos.getData(i).getString("PARA_CODE1");
        	    			String downPrice = commparaInfos.getData(i).getString("PARA_CODE2");
        	    			if(packageId.equals(packageId2)){
        	    				downAll = new BigDecimal(downPrice).subtract(new BigDecimal(salePrice));
        	    			}
        	    			
//        	    			for(int j=0;j<UserSaleActiveInfos.size();j++){
//        	    				String userPackageId = UserSaleActiveInfos.getData(j).getString("PACKAGE_ID");
//        	    				String relationTradeId = UserSaleActiveInfos.getData(j).getString("RELATION_TRADE_ID");
//        	    				
//        	    				if(userPackageId.equals(packageId2)){
//        	    					IDataset saleGoodsInfos = UserSaleGoodsInfoQry.getByRelationTradeId(relationTradeId,Route.CONN_CRM_CG);
//            	    				if(IDataUtil.isNotEmpty(saleGoodsInfos)){
//            	    					IData saleGoods = saleGoodsInfos.first();
//            	    					String saleGoodsPrice = saleGoods.getString("RSRV_STR6");
//            	    					downAll = downAll.add(new BigDecimal(downPrice).subtract(new BigDecimal(saleGoodsPrice)));
//            	    					
//            	    				}
//        	    					
//        	    				}
//        	    				
//        	    			}
        	    			
        	    		}
         	    		
         	    		if(downAll.compareTo(new BigDecimal("20000"))>0 ){
         	    			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "终端销售价不得低于直降金额200元！");
         	    		}
         	    		
         	    	}
                }
            	
            }
        }
        //BUS201911040060 关于新增新信用购活动办理条件的需求 --end		

        for (int index = 0, size = saleGoodDataset.size(); index < size; index++)
        {
            IData saleGoodsData = saleGoodDataset.getData(index);
            String goodsId = saleGoodsData.getString("GOODS_ID");
            // String tagSet = chaInfo.getString("TAG_SET");//saleGoodsData.getString("TAG_SET");
            String saleStaffTag = SaleActiveUtil.getSaleGoodsTagSet(goodsId, 1);
            String saleCheckTag = SaleActiveUtil.getSaleGoodsTagSet(goodsId, 2);
            if ("1".equals(saleStaffTag))
            {
                saleGoodsData.put("ENTER_SALE_STAFF_TAG", "1"); // 需要录入促销员工
            }
            else
            {
                saleGoodsData.put("ENTER_SALE_STAFF_TAG", "0");
            }

            // if (tagSet.substring(1, 2).equals("0"))// 活动查询时已经校验，此处不再校验。
            if ("0".equals(saleCheckTag))
            {
                saleGoodsData.put("HAS_CHECK", "TRUE");
                saleGoodsData.put("NEED_CHECK", "0");
                saleGoodsData.put("IMEI", input.getString("NEW_IMEI"));
            }
            else
            // 赠送的终端，此处需要查询校验！
            {
                saleGoodsData.put("HAS_CHECK", "FALSE");
                saleGoodsData.put("NEED_CHECK", "1");
            }

            IData feeParam = new DataMap();
            feeParam.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            feeParam.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
            feeParam.put("ELEMENT_TYPE_CODE", saleGoodsData.getString("ELEMENT_TYPE_CODE"));
            feeParam.put("ELEMENT_ID", saleGoodsData.getString("ELEMENT_ID"));
            feeParam.put("RES_NO", input.getString("NEW_IMEI"));
            feeParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            IDataset feeDataSet = CSAppCall.call("CS.SaleActiveElementFeeSVC.calElementFee", feeParam);
            if (IDataUtil.isNotEmpty(feeDataSet))
            {
                saleGoodsData.putAll(feeDataSet.getData(0));
                /**
                 * REQ201604180021 网上营业厅合约终端销售价格调整需求 chenxy3 20160428
                 */
                IData param = new DataMap();
                param.put("FEE", saleGoodsData.getString("FEE"));
                param.put("ELEMENT_TYPE_CODE", saleGoodsData.getString("ELEMENT_TYPE_CODE"));
                param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
                param.put("PACKAGE_ID", packageId);
                param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
                param.put("NET_ORDER_ID", input.getString("NET_ORDER_ID"));
                param.put("NEW_IMEI", input.getString("NEW_IMEI"));
                param.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
                String fee = getTerminalOrderInfo(param);

                /**
                 * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求 chenxy3 2016-08-26 调平台 取红包余额进行扣减
                 */
                // 1、先根据活动获取是否使用红包活动
                IDataset comms = CommparaInfoQry.getCommparaInfoByCode("CSM", "6895", input.getString("PRODUCT_ID"), packageId, eparchyCode);
                if (IDataUtil.isNotEmpty(comms))
                {
                    String SMS_CODE = input.getString("SMS_CODE");//短信验证码--wangsc10--20190311
                    String redPakLimit = comms.getData(0).getString("PARA_CODE2", "");// 红包上限
                    String redPakLimitType = comms.getData(0).getString("PARA_CODE3", "");// 红包扣款类型（A=按code2值扣，B=大于0最高code2（最长8位）
                    // 调接口：
                    IData inparam = new DataMap();
                    // 生成14位数字
                    String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
                    String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
                    String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
                    requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS
                    String merid = "888002115000004";// 生产固定这个商户
                    // 测试商户号码，生产可不配置这条
                    IDataset userMerIds = CommparaInfoQry.getCommparaAllColByParser("CSM", "6896", "1", "0898");
                    if (IDataUtil.isNotEmpty(userMerIds))
                    {
                        merid = userMerIds.getData(0).getString("PARA_CODE1", "");
                    }
                    String signString = "MCODE=101776&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid + "&MBL_NO=" + input.getString("SERIAL_NUMBER") + "&CHK_NO=" + SMS_CODE;
                    String requestXML = "<MESSAGE><MCODE>101776</MCODE><MID>" + mid + "</MID><DATE>" + requestDate + "</DATE><TIME>" + requestTime + "</TIME><MERID>" + merid + "</MERID><MBL_NO>" + input.getString("SERIAL_NUMBER") + "</MBL_NO><CHK_NO>" + SMS_CODE + "</CHK_NO>";

                    inparam.put("SIGN_STRING", signString);
                    inparam.put("REQUEST_XML", requestXML);
                    inparam.put("CALL_TYPE", "CHECK_VALUE");// 查红包余额

                    IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
                    
                    if (callResults != null && callResults.size() > 0)
                    {
                        String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
                        String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
                        if ("1".equals(x_resultcode))
                        {
                            String allBalance = callResults.getData(0).getString("ALL_BALANCE", "");// 可用总余额（就是和包现金，这里无用）
                            String elecQuan = Integer.parseInt(callResults.getData(0).getString("ELEC_QUAN", "0"))*100+"";// 查余额根据沟通使用这个比对,查出金额单位是元，换成分
                            // 根据配置来，如果是A类型，则必须按上限金额扣，不多不少
                            if ("A".equals(redPakLimitType))
                            {
                                elecQuan = redPakLimit;
                            }
                            else
                            {
                                // 如果是B类型，而且[红包]大于[上限金额]，则选择上限金额
                                if (Integer.parseInt(elecQuan) > Integer.parseInt(redPakLimit))
                                {
                                    elecQuan = redPakLimit;
                                }
                            }
                            String oldfee = fee;
                            String deviceCost = "0"; // 终端成本价(结算价(进货价))
                            // 发验证码
                            // 金额，需要最终确认。目前是(购机款-红包 >0?红包：购机款 )
                            String amtVal = "";

                            String tradeFeeRsrvStr1 = saleGoodsData.getString("TRADE_FEE_RSRV_STR1", "");
                            if ("".equals(tradeFeeRsrvStr1))
                            {
                                /*******************************************************/
                                IData callParam = new DataMap();
                                callParam.put("RES_NO", input.getString("NEW_IMEI", ""));
                                callParam.put("PRODUCT_ID", input.getString("PRODUCT_ID", ""));
                                callParam.put("CAMPN_TYPE", input.getString("CAMPN_TYPE", ""));
                                callParam.put("TM_CALL_TYPE", "1");
                                callParam.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
                                IDataset resDataSet = CSAppCall.call("CS.TerminalQuerySVC.getTerminalByResNoOnly", callParam);
                                /*
                                 * returnData.put("DEVICE_MODEL_CODE", terminalData.getString("DEVICE_MODEL_CODE"));
                                 * returnData.put("DEVICE_MODEL", terminalData.getString("DEVICE_MODEL")); // 终端型号名称
                                 * returnData.put("DEVICE_COST", terminalData.getString("DEVICE_COST")); //
                                 * 终端成本价(结算价(进货价)) returnData.put("DEVICE_BRAND",
                                 * terminalData.getString("DEVICE_BRAND")); // 终端品牌名称
                                 * returnData.put("DEVICE_BRAND_CODE", terminalData.getString("DEVICE_BRAND_CODE")); //
                                 * 终端品牌编码 returnData.put("SUPPLY_COOP_ID", terminalData.getString("SUPPLY_COOP_ID")); //
                                 * 提供商编码 returnData.put("TERMINAL_TYPE_CODE",
                                 * terminalData.getString("TERMINAL_TYPE_CODE")); // 某一终端类型编码
                                 * returnData.put("TERMINAL_STATE", terminalData.getString("TERMINAL_STATE")); // 在途状态
                                 * returnData.put("SALE_PRICE", terminalData.getString("RSRV_STR6")); //
                                 * 终端销售价--rsrv_str6 returnData.put("DEPUTY_FEE", terminalData.getString("RSRV_STR7"));
                                 * // 代办费 rsrv_str7 returnData.put("IS_INTELL_TERMINAL",
                                 * terminalData.getString("RSRV_STR1")); // 是否智能机：0:非智能机 ,1:智能机 RSRV_STR1
                                 * returnData.put("RSRV_STR3", terminalData.getString("RSRV_STR3")); // 终端颜色描述
                                 * returnData.put("RSRV_STR4", terminalData.getString("RSRV_STR4")); // 终端电池配置
                                 */
                                if (IDataUtil.isNotEmpty(resDataSet))
                                {
                                    deviceCost = resDataSet.getData(0).getString("SALE_PRICE", ""); // 终端成本价(结算价(进货价))
                                    fee = saleGoodsData.getString("TRADE_FEE_FEE");
                                    if (Integer.parseInt(deviceCost) - Integer.parseInt(elecQuan) > 0)
                                    {
                                        amtVal = elecQuan;
                                    }
                                    else
                                    {
                                        amtVal = deviceCost;
                                    }
                                }
                                else
                                {
                                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取设备信息失败CS.TerminalQuerySVC.getTerminalByResNoOnly！失败信息：");
                                }
                                /*******************************************************/
                            }
                            else
                            {
                                deviceCost = oldfee;
                                if (Integer.parseInt(fee) - Integer.parseInt(elecQuan) > 0)
                                {
                                    fee = String.valueOf(Integer.parseInt(fee) - Integer.parseInt(elecQuan));// 减去红包金额得购机款
                                    amtVal = elecQuan;
                                }
                                else
                                {
                                    fee = "0";
                                    amtVal = oldfee;
                                }
                            }
                            // 将需要调用发验证嘛接口传回前台
                            // 测试
                            // if("0".equals(amtVal)){
                            // amtVal="1";
                            // }
                            saleGoodsData.put("DEVICE_COST", deviceCost);// 终端成本价(结算价(进货价))
                            saleGoodsData.put("RED_PACK_VALUE", amtVal);

                        }
                        else
                        {
                            // 如果调接口失败，也报错
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "活动办理失败，调用红包余额查询接口失败信息：" + x_resultinfo);
                        }
                    }
                }
                saleGoodsData.put("FEE", fee);
            }

            if (StringUtils.equals("S", saleGoodsData.getString("RES_TYPE_CODE")))
            {
                IDataset selectGoods = CommparaInfoQry.getCommPkInfo("CSM", "521", saleGoodsData.getString("ELEMENT_ID"), eparchyCode);
                if (IDataUtil.isNotEmpty(selectGoods))
                {
                    saleGoodsData.put("SELECT_GOODS", selectGoods);
                }
            }
        }

        return saleGoodDataset;
    }

    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求 chenxy3 2016-08-26 调平台 下订单
     */
    public IData redPackActiveOrder(IData input) throws Exception
    {
        /*IData rtnData = new DataMap();
        // 调接口：
        IData inparam = new DataMap();
        // 生成20位数字
        String orderId = SeqMgr.getInstId() + String.valueOf(RandomStringUtils.randomNumeric(4));// SysDateMgr.getSysDateYYYYMMDD()+String.valueOf(RandomStringUtils.randomNumeric(12));
        // 生成14位数字
        String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
        String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
        String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
        requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS
        IData callParam = new DataMap();
        callParam.put("RES_NO", input.getString("RES_NO", ""));
        callParam.put("PRODUCT_ID", input.getString("PRODUCT_ID", ""));
        callParam.put("CAMPN_TYPE", input.getString("CAMPN_TYPE", ""));
        callParam.put("TM_CALL_TYPE", "1");
        callParam.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
        IDataset resDataSet = CSAppCall.call("CS.TerminalQuerySVC.getTerminalByResNoOnly", callParam);
        
         // returnData.put("DEVICE_MODEL_CODE", terminalData.getString("DEVICE_MODEL_CODE"));
         // returnData.put("DEVICE_MODEL", terminalData.getString("DEVICE_MODEL")); // 终端型号名称
         // returnData.put("DEVICE_COST", terminalData.getString("DEVICE_COST")); // 终端成本价(结算价(进货价))
         // returnData.put("DEVICE_BRAND", terminalData.getString("DEVICE_BRAND")); // 终端品牌名称
         // returnData.put("DEVICE_BRAND_CODE", terminalData.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
         // returnData.put("SUPPLY_COOP_ID", terminalData.getString("SUPPLY_COOP_ID")); // 提供商编码
         // returnData.put("TERMINAL_TYPE_CODE", terminalData.getString("TERMINAL_TYPE_CODE")); // 某一终端类型编码
         // returnData.put("TERMINAL_STATE", terminalData.getString("TERMINAL_STATE")); // 在途状态
         // returnData.put("SALE_PRICE", terminalData.getString("RSRV_STR6")); // 终端销售价--rsrv_str6
         // returnData.put("DEPUTY_FEE", terminalData.getString("RSRV_STR7")); // 代办费 rsrv_str7
         // returnData.put("IS_INTELL_TERMINAL", terminalData.getString("RSRV_STR1")); // 是否智能机：0:非智能机 ,1:智能机 RSRV_STR1
         // returnData.put("RSRV_STR3", terminalData.getString("RSRV_STR3")); // 终端颜色描述 returnData.put("RSRV_STR4",
         // terminalData.getString("RSRV_STR4")); // 终端电池配置
         
        String supplyCoopId = ""; // 提供商编码
        String deviceBrand = "";// 终端品牌名称
        String deviceBrandCode = "";// 终端品牌编码
        String deviceModel = "";// 终端型号名称
        String deviceModelCode = "";// 终端型号编码
        if (IDataUtil.isNotEmpty(resDataSet))
        {
            supplyCoopId = resDataSet.getData(0).getString("SUPPLY_COOP_ID", ""); // 提供商编码
            deviceBrand = resDataSet.getData(0).getString("DEVICE_BRAND", "");// 终端品牌名称
            deviceBrand = new String(deviceBrand.getBytes("UTF-8"), "GBK");
            deviceBrandCode = resDataSet.getData(0).getString("DEVICE_BRAND_CODE", "");// 终端品牌编码
            deviceModel = resDataSet.getData(0).getString("DEVICE_MODEL", "");// 终端型号名称
            deviceModelCode = resDataSet.getData(0).getString("DEVICE_MODEL_CODE", "");// 终端型号编码
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取设备信息失败CS.TerminalQuerySVC.getTerminalByResNoOnly！失败信息：");
        }
        String merid = "";// 下订单就不能固定了，要调接口返回商户号
        // 测试商户号码，生产可不配置这条
        IDataset userMerIds = CommparaInfoQry.getCommparaAllColByParser("CSM", "6897", supplyCoopId, "0898");
        if (IDataUtil.isNotEmpty(userMerIds))
        {
            merid = userMerIds.getData(0).getString("PARA_CODE1", "");
        }
        String serialNum = input.getString("SERIAL_NUMBER", "");
        String amtVal = input.getString("AMT_VAL", "");
        String signString = "MCODE=101435&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid + "&ORDERID=" + orderId + "&ORDERDATE=" + requestDate + "&USERTOKEN=" + serialNum + "&USERTOKENTP=0&AMT=" + amtVal
                + "&AUTHORIZEMODE=SMS&CURRENCY=CNY&MERACDATE=" + requestDate + "&PERIOD=30&PERIODUNIT=00&MERCHANTABBR=" + deviceModelCode + "&PRODUCTDESC=" + deviceModelCode + "&PRODUCTID=" + deviceModelCode + "&PRODUCTNAME=" + deviceModelCode
                + "&PRODUCTNUM=1&TXNTYP=S&CALLBACKURL=#&SHOWURL=#&RESERVER1=#&RESERVER2=#&COUFLAG=0&VCHFLAG=0&CASHFLAG=0";
        String requestXML = "<MESSAGE>" + "<MCODE>101435</MCODE>" + "<MID>" + mid + "</MID>" + "<DATE>" + requestDate + "</DATE>" + "<TIME>" + requestTime + "</TIME>" + "<MERID>" + merid + "</MERID>" + "<ORDERID>" + orderId + "</ORDERID>" + // 预计要返回保存，否则提交时候无法处理
                "<ORDERDATE>" + requestDate + "</ORDERDATE>" + "<USERTOKEN>" + serialNum + "</USERTOKEN>" + // 手机号
                "<USERTOKENTP>0</USERTOKENTP>" + // 0-手机号（默认）1-条码号 2-NFC设备唯一标志
                "<AMT>" + amtVal + "</AMT>" + // 交易金额
                "<AUTHORIZEMODE>SMS</AUTHORIZEMODE>" + // SMS—短信 CAS-客户端
                "<CURRENCY>CNY</CURRENCY>" + // CNY：用可提现金额进行支付；用户只能选择可提现账户里的钱和银行账户里的钱进行支付。CMY：用不可提现金额支付，用户可以选择不可体现账户里面的钱+充值卡+银行卡进行支付
                "<MERACDATE>" + requestDate + "</MERACDATE>" + "<PERIOD>30</PERIOD>" + // 云支付最大有效期不超过30分钟 1-30以内数字
                "<PERIODUNIT>00</PERIODUNIT>" + // 00-分
                "<MERCHANTABBR>" + deviceModelCode + "</MERCHANTABBR>" + // 商品展示名称
                "<PRODUCTDESC>" + deviceModelCode + "</PRODUCTDESC>" + // 产品描述
                "<PRODUCTID>" + deviceModelCode + "</PRODUCTID>" + // 产品编号
                "<PRODUCTNAME>" + deviceModelCode + "</PRODUCTNAME>" + // 产品名称
                "<PRODUCTNUM>1</PRODUCTNUM>" + // 产品数量
                "<TXNTYP>S</TXNTYP>" + // 交易类型 L：传统 B：B2C C：C2C S：直接支付
                "<CALLBACKURL>#</CALLBACKURL>" + "<SHOWURL>#</SHOWURL>" + "<RESERVER1>#</RESERVER1>" + "<RESERVER2>#</RESERVER2>" + "<COUFLAG>0</COUFLAG>" + "<VCHFLAG>0</VCHFLAG>" + "<CASHFLAG>0</CASHFLAG>";

        inparam.put("SIGN_STRING", signString);
        inparam.put("REQUEST_XML", requestXML);
        inparam.put("MERID", merid);
        inparam.put("CALL_TYPE", "PLACE_ORDER");// 下订单

        IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
        if (callResults != null && callResults.size() > 0)
        {
            String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
            String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
            if ("1".equals(x_resultcode))
            {
                rtnData.put("X_RESULTCODE", x_resultcode);
                rtnData.put("RED_ORDERID", orderId);
                rtnData.put("RED_MERID", merid);

            }
            else
            {
                // 如果调接口失败，也报错
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台接口下订单发送验证码失败！失败信息：" + x_resultinfo);
            }
        }
        return rtnData;
        */
        
        //xingkj修改成下发动态密码2017-11-10===============================================================================================
         IData rtnData = new DataMap();
         // 调接口：
         IData inparam = new DataMap();
         // 生成20位数字
         String orderId = SeqMgr.getInstId() + String.valueOf(RandomStringUtils.randomNumeric(4));// SysDateMgr.getSysDateYYYYMMDD()+String.valueOf(RandomStringUtils.randomNumeric(12));
         // 生成14位数字
         String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
         String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
         String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
         requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS
         IData callParam = new DataMap();
         callParam.put("RES_NO", input.getString("RES_NO", ""));
         callParam.put("PRODUCT_ID", input.getString("PRODUCT_ID", ""));
         callParam.put("CAMPN_TYPE", input.getString("CAMPN_TYPE", ""));
         callParam.put("TM_CALL_TYPE", "1");
         callParam.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
         IDataset resDataSet = CSAppCall.call("CS.TerminalQuerySVC.getTerminalByResNoOnly", callParam);
         /*
          * returnData.put("DEVICE_MODEL_CODE", terminalData.getString("DEVICE_MODEL_CODE"));
          * returnData.put("DEVICE_MODEL", terminalData.getString("DEVICE_MODEL")); // 终端型号名称
          * returnData.put("DEVICE_COST", terminalData.getString("DEVICE_COST")); // 终端成本价(结算价(进货价))
          * returnData.put("DEVICE_BRAND", terminalData.getString("DEVICE_BRAND")); // 终端品牌名称
          * returnData.put("DEVICE_BRAND_CODE", terminalData.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
          * returnData.put("SUPPLY_COOP_ID", terminalData.getString("SUPPLY_COOP_ID")); // 提供商编码
          * returnData.put("TERMINAL_TYPE_CODE", terminalData.getString("TERMINAL_TYPE_CODE")); // 某一终端类型编码
          * returnData.put("TERMINAL_STATE", terminalData.getString("TERMINAL_STATE")); // 在途状态
          * returnData.put("SALE_PRICE", terminalData.getString("RSRV_STR6")); // 终端销售价--rsrv_str6
          * returnData.put("DEPUTY_FEE", terminalData.getString("RSRV_STR7")); // 代办费 rsrv_str7
          * returnData.put("IS_INTELL_TERMINAL", terminalData.getString("RSRV_STR1")); // 是否智能机：0:非智能机 ,1:智能机 RSRV_STR1
          * returnData.put("RSRV_STR3", terminalData.getString("RSRV_STR3")); // 终端颜色描述 returnData.put("RSRV_STR4",
          * terminalData.getString("RSRV_STR4")); // 终端电池配置
          */
         String supplyCoopId = ""; // 提供商编码
         String deviceBrand = "";// 终端品牌名称
         String deviceBrandCode = "";// 终端品牌编码
         String deviceModel = "";// 终端型号名称
         String deviceModelCode = "";// 终端型号编码
         if (IDataUtil.isNotEmpty(resDataSet))
         {
             supplyCoopId = resDataSet.getData(0).getString("SUPPLY_COOP_ID", ""); // 提供商编码
             deviceBrand = resDataSet.getData(0).getString("DEVICE_BRAND", "");// 终端品牌名称
             deviceBrand = new String(deviceBrand.getBytes("UTF-8"), "GBK");
             deviceBrandCode = resDataSet.getData(0).getString("DEVICE_BRAND_CODE", "");// 终端品牌编码
             deviceModel = resDataSet.getData(0).getString("DEVICE_MODEL", "");// 终端型号名称
             deviceModelCode = resDataSet.getData(0).getString("DEVICE_MODEL_CODE", "");// 终端型号编码
         }
         else
         {
             CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取设备信息失败CS.TerminalQuerySVC.getTerminalByResNoOnly！失败信息：");
         }
         String merid = "";// 下订单就不能固定了，要调接口返回商户号
         // 测试商户号码，生产可不配置这条
         IDataset userMerIds = CommparaInfoQry.getCommparaAllColByParser("CSM", "6897", supplyCoopId, "0898");
         if (IDataUtil.isNotEmpty(userMerIds))
         {
             merid = userMerIds.getData(0).getString("PARA_CODE1", "");
         }
         String serialNum = input.getString("SERIAL_NUMBER", "");
         String amtVal = input.getString("AMT_VAL", "");
         String signString = "MCODE=101431&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid  + "&USERTOKEN=" + serialNum + "&USERTOKENTP=0";
         String requestXML = "<MESSAGE>" + "<MCODE>101431</MCODE>" + "<MID>" + mid + "</MID>" + "<DATE>" + requestDate + "</DATE>" + "<TIME>" + requestTime + "</TIME>" + "<MERID>" + merid + "</MERID>" + // 预计要返回保存，否则提交时候无法处理
                 "<USERTOKEN>" + serialNum + "</USERTOKEN>" + // 手机号
                 "<USERTOKENTP>0</USERTOKENTP>";  // 0-手机号（默认）1-条码号 2-NFC设备唯一标志
              
         inparam.put("SIGN_STRING", signString);
         inparam.put("REQUEST_XML", requestXML);
         inparam.put("MERID", merid);
         inparam.put("CALL_TYPE", "PLACE_ORDER");// 下订单

         IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
         if (callResults != null && callResults.size() > 0)
         {
             String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
             String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
             if ("1".equals(x_resultcode))
             {
                 rtnData.put("X_RESULTCODE", x_resultcode);
                 rtnData.put("RED_ORDERID", orderId);
                 rtnData.put("RED_MERID", merid);
                 
                 rtnData.put("AMT_VAL", amtVal);//支付金额
                 rtnData.put("DEVICE_MODEL_CODE", deviceModelCode);
                 rtnData.put("SERIAL_NUMBER", serialNum);

             }
             else
             {
                 // 如果调接口失败，也报错
                 CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台接口下发动态密码失败！失败信息：" + x_resultinfo);
             }
         }
         return rtnData;
         
    }

    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求 chenxy3 2016-08-26 云支付短信验证码支付
     */
    public IData redPackOrderConfirm(IData input) throws Exception
    {
        /*IData rtnData = new DataMap();
        // 调接口：
        IData inparam = new DataMap();
        // 生成14位数字
        String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
        String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
        String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
        requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS
        String userId = input.getString("USER_ID", "");
        String merid = input.getString("RED_MERID", "");
        String orderId = input.getString("RED_ORDERID", "");
        String authCode = input.getString("REDPACK_CODE", "");
        String packageId = input.getString("PACKAGE_ID", "");
        String signString = "MCODE=101436&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid + "&ORDERID=" + orderId + "&SMSCD=" + authCode;
        String requestXML = "<MESSAGE>" + "<MCODE>101436</MCODE>" + "<MID>" + mid + "</MID>" + "<DATE>" + requestDate + "</DATE>" + "<TIME>" + requestTime + "</TIME>" + "<MERID>" + merid + "</MERID>" + "<ORDERID>" + orderId + "</ORDERID>" + // 预计要返回保存，否则提交时候无法处理
                "<SMSCD>" + authCode + "</SMSCD>";

        inparam.put("SIGN_STRING", signString);
        inparam.put("REQUEST_XML", requestXML);
        inparam.put("MERID", merid);
        inparam.put("CALL_TYPE", "PAY_ORDER");// 支付订单

        IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
        if (callResults != null && callResults.size() > 0)
        {
            String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
            String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
            if ("1".equals(x_resultcode))
            {
                
                 * AMT 交易金额 M 15 以分为单位 ORDERID 商户订单号 M 20 商户订单号 COUAMT 电子券消费金额 M 15 以分为单位 VCHAMT 代金券消费金额 M 15 以分为单位
                 * CASHAMT 现金消费金额 M 15 以分为单位
                 
                rtnData.put("X_RESULTCODE", x_resultcode);
                rtnData.put("AMT", callResults.getData(0).getString("AMT", ""));
                rtnData.put("ORDERID", callResults.getData(0).getString("ORDERID", ""));
                rtnData.put("COUAMT", callResults.getData(0).getString("COUAMT", ""));
                rtnData.put("VCHAMT", callResults.getData(0).getString("VCHAMT", ""));
                rtnData.put("CASHAMT", callResults.getData(0).getString("CASHAMT", ""));

                // 支付成功后更新OTHER表状态为中间状态，如果未完工或未办理，则用于冲正。
                // DBConnection conn = new DBConnection("crm1", true, false);
                IData inpara = new DataMap();
                inpara.put("RSRV_VALUE", "2");
                inpara.put("USER_ID", userId);
                inpara.put("MID", mid);
                inpara.put("MERID", merid);
                inpara.put("ORDERID", callResults.getData(0).getString("ORDERID", ""));
                inpara.put("AMT", callResults.getData(0).getString("AMT", ""));
                inpara.put("PACKAGE_ID", packageId);
                Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_OTHER_PLACE_ORDER_STATE", inpara);
                // conn.commit();

            }
            else
            {
                // 如果调接口失败，也报错
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台接口云支付短信验证码支付失败！失败信息：" + x_resultinfo);
            }
        }
        rtnData.put("X_RESULTCODE", "1");
        return rtnData;*/
        
        
        //xingkj修改成动态密码下单支付2017-11-10=========================================================================
        IData rtnData = new DataMap();
        // 调接口：
        IData inparam = new DataMap();
        // 生成14位数字
        String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
        String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
        String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
        requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS
        String userId = input.getString("USER_ID", "");
        String merid = input.getString("RED_MERID", "");
        String orderId = input.getString("RED_ORDERID", "");
        String authCode = input.getString("REDPACK_CODE", "");
        String packageId = input.getString("PACKAGE_ID", "");
        
        String amtVal = input.getString("AMT_VAL", "");
        String deviceModelCode = input.getString("DEVICE_MODEL_CODE", "");
        String serialNum = input.getString("SERIAL_NUMBER", "");
        
        String signString = "MCODE=101432&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid + "&ORDERID=" + orderId + "&ORDERDATE=" + requestDate
                            +"&AMT=" + amtVal+"&CURRENCY=CNY&PERIOD=30&PERIODUNIT=00&PRODUCTNAME=" + deviceModelCode+"&RESERVER1=#&RESERVER2=#&COUFLAG=0&VCHFLAG=0&CASHFLAG=0"+"&USERTOKEN=" + serialNum+"&DYNPWD="+authCode+"&PIKFLG=0";
        String requestXML = "<MESSAGE>" + "<MCODE>101432</MCODE>" + "<MID>" + mid + "</MID>" + "<DATE>" + requestDate + "</DATE>" + "<TIME>" + requestTime + "</TIME>" + "<MERID>" + merid + "</MERID>" + "<ORDERID>" + orderId + "</ORDERID>" + // 预计要返回保存，否则提交时候无法处理
                "<ORDERDATE>" + requestDate + "</ORDERDATE>"+"<AMT>" + amtVal + "</AMT><CURRENCY>CNY</CURRENCY><PERIOD>30</PERIOD><PERIODUNIT>00</PERIODUNIT>"
                +"<PRODUCTNAME>" + deviceModelCode + "</PRODUCTNAME><RESERVER1>#</RESERVER1><RESERVER2>#</RESERVER2><COUFLAG>0</COUFLAG><VCHFLAG>0</VCHFLAG><CASHFLAG>0</CASHFLAG>"
                +"<USERTOKEN>" + serialNum + "</USERTOKEN>"+"<DYNPWD>" + authCode + "</DYNPWD><PIKFLG>0</PIKFLG>";

        inparam.put("SIGN_STRING", signString);
        inparam.put("REQUEST_XML", requestXML);
        inparam.put("MERID", merid);
        inparam.put("CALL_TYPE", "PAY_ORDER");// 支付订单

        IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
        if (callResults != null && callResults.size() > 0)
        {
            String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
            String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
            if ("1".equals(x_resultcode))
            {
                /*
                 * AMT 交易金额 M 15 以分为单位 ORDERID 商户订单号 M 20 商户订单号 COUAMT 电子券消费金额 M 15 以分为单位 VCHAMT 代金券消费金额 M 15 以分为单位
                 * CASHAMT 现金消费金额 M 15 以分为单位
                 */
                rtnData.put("X_RESULTCODE", x_resultcode);
                rtnData.put("AMT", callResults.getData(0).getString("AMT", ""));
                rtnData.put("ORDERID", callResults.getData(0).getString("ORDERID", ""));
                rtnData.put("COUAMT", callResults.getData(0).getString("COUAMT", ""));
                rtnData.put("VCHAMT", callResults.getData(0).getString("VCHAMT", ""));
                rtnData.put("CASHAMT", callResults.getData(0).getString("CASHAMT", ""));

                // 支付成功后更新OTHER表状态为中间状态，如果未完工或未办理，则用于冲正。
                // DBConnection conn = new DBConnection("crm1", true, false);
                IData inpara = new DataMap();
                inpara.put("RSRV_VALUE", "2");
                inpara.put("USER_ID", userId);
                inpara.put("MID", mid);
                inpara.put("MERID", merid);
                inpara.put("ORDERID", callResults.getData(0).getString("ORDERID", ""));
                inpara.put("AMT", callResults.getData(0).getString("AMT", ""));
                inpara.put("PACKAGE_ID", packageId);
                Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_OTHER_PLACE_ORDER_STATE", inpara);
                // conn.commit();

            }
            else
            {
                // 如果调接口失败，也报错
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台接口云支付短信验证码支付失败！失败信息：" + x_resultinfo);
            }
        }
        rtnData.put("X_RESULTCODE", "1");
        return rtnData;
    }

    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求 chenxy3 2016-08-26 云支付短信验证码重发
     */
    public IData resendAuthCode(IData input) throws Exception
    {
        /*IData rtnData = new DataMap();
        // 调接口：
        IData inparam = new DataMap();
        // 生成14位数字
        String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
        String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
        String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
        requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS

        String merid = input.getString("RED_MERID", "");
        String orderId = input.getString("RED_ORDERID", "");
        String signString = "MCODE=101437&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid + "&ORDERID=" + orderId;
        String requestXML = "<MESSAGE>" + "<MCODE>101437</MCODE>" + "<MID>" + mid + "</MID>" + "<DATE>" + requestDate + "</DATE>" + "<TIME>" + requestTime + "</TIME>" + "<MERID>" + merid + "</MERID>" + "<ORDERID>" + orderId + "</ORDERID>";

        inparam.put("SIGN_STRING", signString);
        inparam.put("REQUEST_XML", requestXML);
        inparam.put("MERID", merid);
        inparam.put("CALL_TYPE", "RESEND_CODE");// 重发验证码

        IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
        if (callResults != null && callResults.size() > 0)
        {
            String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
            String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
            if ("1".equals(x_resultcode))
            {
                *//**
                 * AMT 交易金额 M 15 以分为单位 ORDERID 商户订单号 M 20 商户订单号 USERTOKEN 用户标志 M 64 用户手机号
                 *//*
                rtnData.put("X_RESULTCODE", x_resultcode);
                rtnData.put("USERTOKEN", callResults.getData(0).getString("USERTOKEN", ""));
                rtnData.put("AMT", callResults.getData(0).getString("AMT", ""));
                rtnData.put("ORDERID", callResults.getData(0).getString("ORDERID", ""));

            }
            else
            {
                // 如果调接口失败，也报错
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台云支付短信验证码重发失败！失败信息：" + x_resultinfo);
            }
        }
        return rtnData;*/
        
        
        //xingkj修改重发动态密码2017-11-10===================================================================================
        IData rtnData = new DataMap();
        // 调接口：
        IData inparam = new DataMap();
        // 生成14位数字
        String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
        String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
        String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
        requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS

        String merid = input.getString("RED_MERID", "");
        String orderId = input.getString("RED_ORDERID", "");
        String serialNum = input.getString("SERIAL_NUMBER", "");
        
        String signString = "MCODE=101431&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid  + "&USERTOKEN=" + serialNum + "&USERTOKENTP=0";
        String requestXML = "<MESSAGE>" + "<MCODE>101431</MCODE>" + "<MID>" + mid + "</MID>" + "<DATE>" + requestDate + "</DATE>" + "<TIME>" + requestTime + "</TIME>" + "<MERID>" + merid + "</MERID>" + // 预计要返回保存，否则提交时候无法处理
                "<USERTOKEN>" + serialNum + "</USERTOKEN>" + // 手机号
                "<USERTOKENTP>0</USERTOKENTP>";  // 0-手机号（默认）1-条码号 2-NFC设备唯一标志
        
        inparam.put("SIGN_STRING", signString);
        inparam.put("REQUEST_XML", requestXML);
        inparam.put("MERID", merid);
        inparam.put("CALL_TYPE", "PLACE_ORDER");// 重发验证码

        IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
        if (callResults != null && callResults.size() > 0)
        {
            String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
            String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
            if ("1".equals(x_resultcode))
            {
                /**
                 * AMT 交易金额 M 15 以分为单位 ORDERID 商户订单号 M 20 商户订单号 USERTOKEN 用户标志 M 64 用户手机号
                 */
                rtnData.put("X_RESULTCODE", x_resultcode);
                /*rtnData.put("USERTOKEN", callResults.getData(0).getString("USERTOKEN", ""));
                rtnData.put("AMT", callResults.getData(0).getString("AMT", ""));
                rtnData.put("ORDERID", callResults.getData(0).getString("ORDERID", ""))*/;

            }
            else
            {
                // 如果调接口失败，也报错
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台云支付动态密码重发失败！失败信息：" + x_resultinfo);
            }
        }
        return rtnData;
    }

    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求 chenxy3 2016-08-26 红包发放撤销
     */
    public IData backRedPack(IData input) throws Exception
    {
        IData rtnData = new DataMap();
        String userId = input.getString("USER_ID");
        input.put("RSRV_VALUE", "1");
        IDataset others = UserOtherInfoQry.queryUserOtherByRsrv4(input);
        if (IDataUtil.isNotEmpty(others))
        {
            // 调接口：
            IData inparam = new DataMap();
            // 生成14位数字
            String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
            String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
            String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
            requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS

            String serialNum = others.getData(0).getString("RSRV_STR1", "");
            String orderId = others.getData(0).getString("RSRV_STR4", "");// 发送红包订单号
            String amt = others.getData(0).getString("RSRV_STR6", "");// 发送红包金额
            String merid = others.getData(0).getString("RSRV_STR7", "");// 商户号
            String actid = others.getData(0).getString("RSRV_STR8", "");// 平台活动号
            String sendDate = others.getData(0).getString("RSRV_STR9", "");// 红包发送时间2016-09-08 19:41:47
            String sendDateYYYYMMDD = sendDate.substring(0, 8);
            String signString = "MCODE=101820&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid + "&ACT_ID=" + actid + "&MBL_NO=" + serialNum + "&BON_AMT=" + amt + "&MLOG_NO=" + orderId + "&TCNL_TYP=21&TTXN_DT="
                    + sendDateYYYYMMDD + "&OPTYP=1";
            String requestXML = "<MESSAGE>" + "<MCODE>101820</MCODE>" + "<MID>" + mid + "</MID>" + "<DATE>" + requestDate + "</DATE>" + "<TIME>" + requestTime + "</TIME>" + "<MERID>" + merid + "</MERID>" + "<ACT_ID>" + actid + "</ACT_ID>"
                    + "<MBL_NO>" + serialNum + "</MBL_NO>" + "<BON_AMT>" + amt + "</BON_AMT>" + "<MLOG_NO>" + orderId + "</MLOG_NO>" + "<TCNL_TYP>21</TCNL_TYP>" + "<TTXN_DT>" + sendDateYYYYMMDD + "</TTXN_DT>" + "<OPTYP>1</OPTYP>";

            inparam.put("SIGN_STRING", signString);
            inparam.put("REQUEST_XML", requestXML);
            inparam.put("MERID", merid);
            inparam.put("CALL_TYPE", "BACK_REDPACK");// 红包发放撤销

            IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
            if (callResults != null && callResults.size() > 0)
            {
                String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
                String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
                if ("1".equals(x_resultcode))
                {
                    rtnData.put("X_RESULTCODE", x_resultcode);
                    IData inpara = new DataMap();
                    inpara.put("CALL_FLAG", "SEND_PAK");
                    inpara.put("USER_ID", userId);
                    inpara.put("CANCEL_TAG", input.getString("CANCEL_TAG"));
                    inpara.put("CALL_XML", callResults.getData(0).getString("CALL_XML", ""));
                    inpara.put("CALL_RESPONSE", callResults.getData(0).getString("CALL_RESPONSE", ""));
                    SaleActiveQueryBean.updateVolteOtherVal(inpara);
                }
                else
                {
                    // 如果调接口失败，也报错
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台红包发放撤销失败！失败信息：" + x_resultinfo);
                }
            }
        }
        return rtnData;
    }

    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求 chenxy3 2016-08-26 撤销或退货（已扣费的进行撤销，用于返销、冲正费用）
     */
    public IData returnRedPackPay(IData input) throws Exception
    {
        IData rtnData = new DataMap();
        String userId = input.getString("USER_ID");
        String rsrvValue = input.getString("RSRV_VALUE"); // 必传，冲正的要传2，返销的要传3
        IDataset others = UserOtherInfoQry.queryUserOtherByRsrv4(input);
        if (IDataUtil.isNotEmpty(others))
        {
            // 调接口：
            IData inparam = new DataMap();
            // 生成14位数字
            String mid = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(6));
            String requestDate = SysDateMgr.getSysDateYYYYMMDD(); // YYYYMMDD
            String requestTime = SysDateMgr.getSysTime();// 2016-08-24 18:19:51
            requestTime = requestTime.substring(requestTime.indexOf(":") - 2).replaceAll(":", "");// 格式：HHMISS

            String requestId = SysDateMgr.getSysDateYYYYMMDD() + String.valueOf(RandomStringUtils.randomNumeric(42));
            String serialNum = others.getData(0).getString("RSRV_STR1", "");
            String orderId = others.getData(0).getString("RSRV_STR13", "");// 订单确认（扣款成功）的商户订单号ORDERID
            String amt = others.getData(0).getString("RSRV_STR14", "");// 使用红包金额
            String merid = others.getData(0).getString("RSRV_STR12", "");// 支付的商户号
            String payDate = others.getData(0).getString("RSRV_STR15", "").substring(0, 8);// 红包发送时间
            String signString = "MCODE=101450&MID=" + mid + "&DATE=" + requestDate + "&TIME=" + requestTime + "&MERID=" + merid + "&ORDERID=" + orderId + "&AMOUT=" + amt + "&REQUESTID=" + requestId + "&ORDERDATE=" + payDate + "&CNYTYP=";
            String requestXML = "<MESSAGE>" + "<MCODE>101450</MCODE>" + "<MID>" + mid + "</MID>" + "<DATE>" + requestDate + "</DATE>" + "<TIME>" + requestTime + "</TIME>" + "<MERID>" + merid + "</MERID>" + "<ORDERID>" + orderId + "</ORDERID>"
                    + "<AMOUT>" + amt + "</AMOUT>" + "<REQUESTID>" + requestId + "</REQUESTID>" + "<ORDERDATE>" + payDate + "</ORDERDATE>" + "<CNYTYP></CNYTYP>";

            inparam.put("SIGN_STRING", signString);
            inparam.put("REQUEST_XML", requestXML);
            inparam.put("MERID", merid);
            inparam.put("CALL_TYPE", "PAY_RETURN");// 扣款冲正（撤销）

            IDataset callResults = CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam);
            if (callResults != null && callResults.size() > 0)
            {
                String x_resultcode = callResults.getData(0).getString("X_RESULTCODE", "");
                String x_resultinfo = callResults.getData(0).getString("X_RESULTINFO", "");
                String platResultCode = callResults.getData(0).getString("PLAT_RESULT_CODE", "");// 已完成退款的也要更新<RCODE>CPS08008</RCODE><DESC>订单已经完成退款</DESC
                if ("1".equals(x_resultcode) || "CPS08008".equals(x_resultcode))
                {
                    rtnData.put("X_RESULTCODE", x_resultcode);
                    IData inpara = new DataMap();
                    inpara.put("CALL_FLAG", "PAY");
                    inpara.put("USER_ID", userId);
                    inpara.put("CANCEL_TAG", input.getString("CANCEL_TAG"));
                    inpara.put("CALL_XML", callResults.getData(0).getString("CALL_XML", ""));
                    inpara.put("CALL_RESPONSE", callResults.getData(0).getString("CALL_RESPONSE", ""));
                    SaleActiveQueryBean.updateVolteOtherVal(inpara);
                }
                else
                {
                    // 如果调接口失败，也报错
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包平台扣款撤销失败！失败信息：" + x_resultinfo);
                }
            }
        }
        return rtnData;
    }

    public IDataset querySalePlatSvcsByPackageId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");

        // IDataset svcs = PlatSvcInfoQry.queryByPkgId(packageId);
        IDataset svcs = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);

        int size = svcs.size();
        for (int i = 0; i < size; i++)
        {
            IData svc = svcs.getData(i);

            svc.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
            svc.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            svc.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            svc.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
            svc.put("BASIC_CAL_START_DATE", input.getString("BASIC_DATE"));
            svc.put("CUSTOM_ABSOLUTE_START_DATE", input.getString("BASIC_DATE"));
            svc.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));

            IDataset dataset = CSAppCall.call("CS.SaleActiveElementDateSVC.callElementStartEndDate", svc);
            IData data = dataset.getData(0);

            svc.put("START_DATE", data.getString("START_DATE"));
            svc.put("END_DATE", data.getString("END_DATE"));
        }

        return svcs;
    }

    public IDataset querySaleScoresByPackageId(IData input) throws Exception
    {
        IData cond = new DataMap();
        String packageId = input.getString("PACKAGE_ID");
        cond.put("PACKAGE_ID", packageId);
        String eparchyCode = input.getString("EPARCHY_CODE");
        String serialNumber = input.getString("SERIAL_NUMBER");

        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        int userScore = 0;
        // IDataset scores = SaleScoreInfoQry.queryByPkgIdEparchy(packageId, eparchyCode);
        IDataset scores = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_SCORE);
        int size = scores.size();

        if (size == 0)
            return null;

        /**
         * REQ201603090003 关于新增集团客户回馈购机活动的需求（积分） chenxy3 20160324
         */
        String productId = input.getString("PRODUCT_ID");
        IDataset specElems = CommparaInfoQry.getCommParas("CSM", "2401", packageId, productId, "0898");
        if (specElems != null && specElems.size() > 0)
        {
            // 取用户的该类型的积分
            IData callParam = new DataMap();
            String userId = uca.getUserId();
            String scoreTypeCode = scores.getData(0).getString("SCORE_TYPE_CODE", "");
            callParam.put("USER_ID", userId);
            callParam.put("INTEGRAL_TYPE_CODE", scoreTypeCode);// 积分类型
            // …………………………………………call…………………………………………
            SaleActiveQueryBean bean = BeanManager.createBean(SaleActiveQueryBean.class);
            IDataset callSet = bean.queryUserScoreValue(callParam);// 调账务接口取积分

            // …………………………………………call…………………………………………
            if (callSet != null && callSet.size() > 0)
            {
                IData callrtn = callSet.getData(0);
                userScore = Integer.parseInt(callrtn.getString("SCORE_VALUE", ""));// 账务提供 —— 用户积分
            }
        }
        else
        {
            userScore = uca.getUserScore();
        }

        scores.getData(0).put("USER_SCORE", userScore);

        for (int i = 0; i < size; i++)
        {
            IData score = scores.getData(i);

            /* 计算积分够不够或者需补足多少 */
            String depositValue = "";
            String depositTag = score.getString("DEPOSIT_TAG", "0");
            String depositRate = score.getString("DEPOSIT_RATE", "1");
            String scoreValue = score.getString("SCORE_VALUE", "0");
            int needScore = -Integer.parseInt(scoreValue);

            if (!depositTag.equals("1"))// 不能用预存补积分，海南都是为0，不能预存补积分。
            {
                if (needScore > userScore)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_163);
                }
                depositValue = "0";
            }
            else
            {
                depositValue = String.valueOf(SaleActiveUtil.valueForScore(depositRate, scoreValue, userScore));
                score.put("SCORE_VALUE", Integer.parseInt(scoreValue) + Math.abs(Integer.parseInt(depositValue)));
            }

            score.put("DEPOSIT_VALUE", depositValue);

            // 如果是可以用现金抵积分并且用户积分不够，则将需缴的现金增加到费用列表里
            if (depositTag.equals("1") && !depositValue.equals("0"))
            {
                score.put("FEE", depositValue);
                score.put("FEE_MODE", "2");
                score.put("FEE_TYPE_CODE", score.getString("PAYMENT_ID", "240"));
                score.put("PAY_MODE", "0");
            }
        }

        return scores;
    }

    public IDataset querySaleSvcsByPackageId(IData input) throws Exception
    {
        String packageId = input.getString("PACKAGE_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");

        // IDataset svcs = SvcInfoQry.queryByPkgId(packageId);
        IDataset svcs = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_SVC);
        int size = svcs.size();
        for (int i = 0; i < size; i++)
        {
            IData svc = svcs.getData(i);
            // IDataset itemas = AttrItemInfoQry.getelementItemaByPk(svc.getString("ELEMENT_ID"), "S", eparchyCode,
            // null);
            IDataset itemas = UItemAInfoQry.queryOfferChaByIdAndIdType(BofConst.ELEMENT_TYPE_CODE_SVC, svc.getString("ELEMENT_ID"), eparchyCode);
            if (IDataUtil.isNotEmpty(itemas))
            {
                IDataset attrs = new DatasetList();
                int attrSize = itemas.size();
                for (int j = 0; j < attrSize; j++)
                {
                    IData attr = new DataMap();
                    attr.put("ATTR_CODE", itemas.getData(j).getString("ATTR_CODE"));
                    attr.put("ATTR_VALUE", itemas.getData(j).getString("ATTR_INIT_VALUE", ""));
                    attrs.add(attr);
                }
                svc.put("HAS_ATTR", "true");
                svc.put("ATTR_ITEMAS", attrs);
            }
            else
            {
                svc.put("HAS_ATTR", "false");
            }
            svc.put("CAMPN_TYPE", input.getString("CAMPN_TYPE"));
            svc.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            svc.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            svc.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
            svc.put("BASIC_CAL_START_DATE", input.getString("BASIC_DATE"));
            svc.put("CUSTOM_ABSOLUTE_START_DATE", input.getString("BASIC_DATE"));
            svc.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));

            IDataset dataset = CSAppCall.call("CS.SaleActiveElementDateSVC.callElementStartEndDate", svc);
            IData data = dataset.getData(0);

            svc.put("START_DATE", data.getString("START_DATE"));
            svc.put("END_DATE", data.getString("END_DATE"));
        }

        return svcs;
    }

    public IDataset queryShortCutSaleActive(IData input) throws Exception
    {
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset list = ProductInfoQry.queryShortCutSaleActive(eparchyCode);
        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), list);

        return list;
    }

    /**
     * 根据活动类型和终端编码查询可以的营销方案
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryProductsByTerminal(IData input) throws Exception
    {

        String labelId = input.getString("CAMPN_TYPE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        // IDataset proList = ProductInfoQry.querySaleActiveProductByLabel(labelId, eparchyCode);
        IDataset results = UpcCall.qryCatalogsByUpCatalogId(labelId);

        IDataset proList = SaleActiveUtil.filterSaleProductsByParamAttr522(results, labelId);

        if (IDataUtil.isEmpty(proList))
            return proList;

        IDataset salePackages = new DatasetList();
        for (int i = 0; i < proList.size(); i++)
        {
            IData proData = proList.getData(i);
            String productId = proData.getString("CATALOG_ID");
            proData.put("PRODUCT_ID", productId);
            proData.put("PRODUCT_NAME", proData.getString("CATALOG_NAME"));
            proData.put("LABEL_ID", labelId);

            IDataset pkgInfos = ProductPkgInfoQry.qryActiveByPId(productId, eparchyCode);
            salePackages.addAll(pkgInfos);
        }

        if (IDataUtil.isEmpty(salePackages))
            return proList;

        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), salePackages);

        IData svcParam = new DataMap();
        svcParam.put("SALE_ACTIVES", salePackages);
        svcParam.put("DEVICE_TYPE_CODE", input.getString("DEVICE_TYPE_CODE"));
        svcParam.put("DEVICE_MODEL_CODE", input.getString("DEVICE_MODEL_CODE"));
        svcParam.put("DEVICE_COST", input.getString("DEVICE_COST"));
        svcParam.put("SALE_PRICE", input.getString("SALE_PRICE"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        salePackages = CSAppCall.call("CS.SalePackagesFilteSVC.filterPackagesByTerminalConfig", svcParam);
        IDataset retnList = new DatasetList();
        // 针对过滤规则过滤后的信息来重新编排产品信息
        if (!IDataUtil.isEmpty(salePackages))
        {
            for (int i = 0; i < proList.size(); i++)
            {
                IData proData = proList.getData(i);
                for (int j = 0; j < salePackages.size(); j++)
                {
                    if (proData.getString("PRODUCT_ID").equals(salePackages.getData(j).getString("PRODUCT_ID")))
                    {
                        retnList.add(proData);
                        break;
                    }
                }
            }
        }
        else
        {
            retnList = proList;
        }

        return retnList;
    }

    /**
     * 根据营销活动产品id获取终端
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getTerminalsByProductID(IData input) throws Exception
    {

        IDataset termianlList = new DatasetList();

        String productId = input.getString("PRODUCT_ID");
        if (StringUtils.isBlank(productId))
        {
            return new DatasetList();
        }

        IDataset salePackages = new DatasetList();
        String IsQueryTerminal = input.getString("QUERY_TERMINAL", "");
        // logger.info("linsl QUERY_TERMINAL = " + IsQueryTerminal);
        if ("1".equals(IsQueryTerminal))
        {
            // salePackages = ProductPkgInfoQry.qryActiveTerminalByPId(productId, input.getString("EPARCHY_CODE"));
            salePackages = UpcCall.qryOffersByCatalogIdAll(productId);
        }
        else
        {
            // salePackages = ProductPkgInfoQry.qryActiveByPId(productId, input.getString("EPARCHY_CODE"));
            salePackages = UpcCall.qryOffersByCatalogId(productId);
        }
        if (IDataUtil.isEmpty(salePackages))
            return new DatasetList();

        SaleActiveUtil.buildSalePackagesByPackageExt(salePackages, new String[]
        { "RSRV_STR2", "RSRV_STR5" });

        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), salePackages);

        for (int i = 0, size = salePackages.size(); i < size; i++)
        {
            IData salePackage = salePackages.getData(i);
            String terminalModelCode = salePackage.getString("RSRV_STR2");
            String terminalTypeCode = salePackage.getString("RSRV_STR5");

            // 只有deviceModelCode机型才能办理该营销包
            if (!"ZZZZ".equals(terminalModelCode) && !"".equals(terminalModelCode))
            {
                IData intfParam = new DataMap();
                intfParam.put("DEVICE_MODEL_CODE", terminalModelCode);
                intfParam.put("RES_TYPE_ID", terminalTypeCode);
                intfParam.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
                IDataset termianlInfos = CSAppCall.call("CS.TerminalQuerySVC.getTerminalByDeviceModelCode", intfParam);
                termianlInfos.getData(0).put("SALE_PRICE_NAME", chargeMoney(termianlInfos.getData(0).getString("SALE_PRICE")));
                termianlList.add(termianlInfos.getData(0));
            }
        }

        return termianlList;
    }

    /**
     * 查询用户三个月内的消费与gprs流量
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryUserConsumptionById(IData input) throws Exception
    {

        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_FOR_SALEACTIVE_SBXS", input);
        if (dataset != null && dataset.size() > 0)
        {
            return dataset.getData(0);
        }
        return new DataMap();
    }

    /**
     * 功能描述:钱从分转为元
     * 
     * @param str
     *            源串
     */
    public String chargeMoney(String str)
    {
        String retnStr = "0元";
        if (str == null || "".equals(str))
        {
            return retnStr;
        }
        try
        {
            float money = Float.parseFloat(str);
            money = money / 100;
            retnStr = money + "元";
        }
        catch (Exception e)
        {
            retnStr = "0元";
            throw new RuntimeException(e);
        }

        return retnStr;
    }

    /**
     * 选中产品节点，校验产品是否可选
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void choiceProductNode(IData input) throws Exception
    {
        IDataset commparaInfos = CommparaInfoQry.getCommparaByCode1("CSM", "8424", "YXXX", input.getString("LABEL_ID",""), null);
        if(IDataUtil.isNotEmpty(commparaInfos))
        {
            input.put("TIP_FLAG", "1");
            input.put("TIP_INFO", commparaInfos.getData(0).getString("PARA_CODE20", "购机客户还未办理宽带，建议给予推介！"));
        }
        
        if (!input.containsKey("USER_ID"))
            CSAppException.appError("123", "点选产品校验：用户标识[USER_ID]是必须的！");
        if (!input.containsKey("CUST_ID"))
            CSAppException.appError("123", "点选产品校验：客户标识[CUST_ID]是必须的！");
        if (!input.containsKey("PRODUCT_ID"))
            CSAppException.appError("123", "点选产品校验：营销产品标识[PRODUCT_ID]是必须的！");

        // 增加校验配置
        if (!ProductInfoQry.checkSaleActiveLimitProd(input.getString("PRODUCT_ID")))
        {
            return;
        }

        IData paramValue = new DataMap();

        paramValue.put("V_EVENT_TYPE", "MODE");
        paramValue.put("V_EPARCHY_CODE", this.getVisit().getStaffEparchyCode());
        paramValue.put("V_CITY_CODE", this.getVisit().getCityCode());
        paramValue.put("V_DEPART_ID", this.getVisit().getDepartId());
        paramValue.put("V_STAFF_ID", this.getVisit().getStaffId());

        paramValue.put("V_USER_ID", input.getString("USER_ID"));
        paramValue.put("V_DEPOSIT_GIFT_ID", input.getString("CUST_ID"));
        paramValue.put("V_PURCHASE_MODE", input.getString("PRODUCT_ID"));
        paramValue.put("V_PURCHASE_ATTR", "-1");
        paramValue.put("V_TRADE_ID", "-1");

        paramValue.put("V_CHECKINFO", input.getString("CHECKINFO", ""));
        paramValue.put("V_RESULTCODE", input.getString("RESULTCODE", ""));
        paramValue.put("V_RESULTINFO", input.getString("RESULTINFO", ""));
        paramValue.put("V_SALE_TYPE", input.getString("CAMPN_TYPE"));
        paramValue.put("V_VIP_TYPE_ID", input.getString("VIP_TYPE_ID", "-1"));

        paramValue.put("V_VIP_CLASS_ID", input.getString("VIP_CLASS_ID", "-1"));

        ProductInfoQry.checkSaleActiveProdByProced(paramValue);
        if (!"0".equals(paramValue.getString("V_RESULTCODE")))
        {
            CSAppException.appError(paramValue.getString("V_RESULTCODE"), paramValue.getString("V_RESULTINFO"));
        }
    }

    /**
     * 选中包节点，校验包是否可选
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void choicePackageNode(IData input) throws Exception
    {

        if (!input.containsKey("USER_ID"))
            CSAppException.appError("123", "点选产品校验：用户标识[USER_ID]是必须的！");
        if (!input.containsKey("CUST_ID"))
            CSAppException.appError("123", "点选产品校验：客户标识[CUST_ID]是必须的！");
        if (!input.containsKey("PRODUCT_ID"))
            CSAppException.appError("123", "点选产品校验：营销产品标识[PRODUCT_ID]是必须的！");
        if (!input.containsKey("PACKAGE_ID"))
            CSAppException.appError("123", "点选产品校验：营销包标识[PACKAGE_ID]是必须的！");

        // 增加校验配置
        if (!ProductInfoQry.checkSaleActiveLimitProd(input.getString("PRODUCT_ID")))
        {
            return;
        }

        String packageId = input.getString("PACKAGE_ID");
        IData pkgInfo = UPackageInfoQry.getPackageByPK(packageId);
        if (IDataUtil.isEmpty(pkgInfo))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_49, "未找到该营销包,或营销包已下线[" + packageId + "]");
        }

        IData paramValue = new DataMap();

        paramValue.put("V_EVENT_TYPE", "ATTR");
        paramValue.put("V_EPARCHY_CODE", this.getVisit().getStaffEparchyCode());
        paramValue.put("V_CITY_CODE", this.getVisit().getCityCode());
        paramValue.put("V_DEPART_ID", this.getVisit().getDepartId());
        paramValue.put("V_STAFF_ID", this.getVisit().getStaffId());

        paramValue.put("V_USER_ID", input.getString("USER_ID"));
        paramValue.put("V_DEPOSIT_GIFT_ID", input.getString("CUST_ID"));
        paramValue.put("V_PURCHASE_MODE", input.getString("PRODUCT_ID"));
        paramValue.put("V_PURCHASE_ATTR", input.getString("PACKAGE_ID"));
        paramValue.put("V_TRADE_ID", "-1");

        paramValue.put("V_CHECKINFO", input.getString("CHECKINFO", ""));
        paramValue.put("V_RESULTCODE", input.getString("RESULTCODE", ""));
        paramValue.put("V_RESULTINFO", input.getString("RESULTINFO", ""));
        paramValue.put("V_SALE_TYPE", input.getString("CAMPN_TYPE"));
        paramValue.put("V_VIP_TYPE_ID", input.getString("VIP_TYPE_ID", "-1"));

        paramValue.put("V_VIP_CLASS_ID", input.getString("VIP_CLASS_ID", "-1"));
        ProductInfoQry.checkSaleActiveProdByProced(paramValue);

        if (!"0".equals(paramValue.getString("V_RESULTCODE")))
        {
            CSAppException.appError(paramValue.getString("V_RESULTCODE"), paramValue.getString("V_RESULTINFO"));
        }
    }

    public IDataset checkGiftSaleactive(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        IDataset productInfos = CommparaInfoQry.getCommpara("CSM", "528", productId, "0898");
        return productInfos;
    }

    /**
     * REQ201512070014 关于4G终端社会化销售模式系统开发需求
     */
    public IDataset giftSaleActiveDeal(IDataset saleActives, String giftCode) throws Exception
    {
        if (StringUtils.isBlank(giftCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "礼品码不能为空!");
        }

        IDataset checkGiftCodeAndReturnConfig = new DatasetList();// 礼品卡与活动包的关系配置

        // 获取密码卡信息
        IDataset giftCodeInfos = GGCardInfoQry.getGGCardInfoByCardPassWord(giftCode);// 记录唯一
        if (IDataUtil.isNotEmpty(giftCodeInfos))
        {
            String processTag = giftCodeInfos.getData(0).getString("PROCESS_TAG", "");// 礼品码状态 0或空-未用 1-已用
            if ("1".equals(processTag))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "礼品码已使用过!");
            }
            else
            {
                String cardCode = giftCodeInfos.getData(0).getString("CARD_CODE", "");
                String itemCode = giftCodeInfos.getData(0).getString("ITEM_CODE", "");
                checkGiftCodeAndReturnConfig = CommparaInfoQry.getCommparaInfoByCode("CSM", "529", cardCode, itemCode, "0898");
            }

        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "礼品码无效!");
        }

        IDataset resultPackageList = new DatasetList();// 返回的活动包，默认是没有匹配的，空列表。匹配到的在条件中add

        // 如果配置不为空，取出配置进行包过滤
        if (IDataUtil.isNotEmpty(checkGiftCodeAndReturnConfig))
        {
            for (int i = 0, size = checkGiftCodeAndReturnConfig.size(); i < size; i++)
            {
                IData commparaInfo = checkGiftCodeAndReturnConfig.getData(i);
                String paraCode2 = commparaInfo.getString("PARA_CODE2", "");
                String paraCode3 = commparaInfo.getString("PARA_CODE3", "");

                // 如果返回了配置，说明礼品码匹配到了可以办理的产品和活动，如果paraCode2产品ID，paraCode3包ID不为空
                if (StringUtils.isNotBlank(paraCode2) && StringUtils.isNotBlank(paraCode3))
                {
                    for (int j = saleActives.size() - 1; j >= 0; j--)
                    {
                        IData saleActive = saleActives.getData(j);
                        String saleActiveProductId = saleActive.getString("PRODUCT_ID");
                        String saleActivePackageId = saleActive.getString("PACKAGE_ID");
                        if (paraCode2.equals(saleActiveProductId) && paraCode3.equals(saleActivePackageId))
                        {
                            resultPackageList.add(saleActive);
                        }
                    }
                }
            }
        }

        return resultPackageList;
    }

    //add by liangdg3 at 20191103 for REQ201910110011泛渠道拓展合作商家系统配置需求 start
    /**
     * 查询信用购机活动
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryCreditPurchasesProducts(IData params) throws Exception{
        IDataset products=queryAllAvailableProducts(params);
        if(IDataUtil.isEmpty(products)){
            return null;
        }
        return filterCreditPurchasesProducts(products);
    }

    /**
     * 根据合约类型查询信用购机活动
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryCreditPurchasesProductsByLabel(IData params) throws Exception{
        IDataset products=queryProductsByLabel(params);
        if(IDataUtil.isEmpty(products)){
            return null;
        }
        return filterCreditPurchasesProducts(products);
    }

    /**
     * 查询信用购机活动合约类型
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryCreditPurchasesCampnTypes(IData params) throws Exception{
        IDataset campnTypes = getCampnTypes(params);
        if(IDataUtil.isEmpty(campnTypes)){
            return null;
        }
        return filterCreditPurchasesCampnTypes(campnTypes);
    }

    /**
     * 查询信用购机活动包
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryCreditPurchasesSaleActives(IData params) throws Exception{
        IDataset saleActives=querySaleActives(params);
        if(IDataUtil.isEmpty(saleActives)){
            return null;
        }
        return filterCreditPurchasesSaleActives(saleActives);
    }

    /**
     * 根据终端号查询信用购机活动包
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset queryCreditPurchasesSaleActivesByImei(IData params) throws Exception{
        IDataset saleActives=querySaleActivesByImei(params);
        if(IDataUtil.isEmpty(saleActives)){
            return null;
        }
        return filterCreditPurchasesSaleActives(saleActives);
    }

    /**
     * 过滤返回信用购机活动合约类型
     * @param campnTypes
     * @return
     * @throws Exception
     */
    public IDataset filterCreditPurchasesCampnTypes(IDataset campnTypes) throws Exception{
        IDataset commparaInfos = CommparaInfoQry.getCommByParaAttr("CSM", "3119","0898");
        Set<String> campnTypesOfCreditPurchases = new HashSet<String>();
        Set<String> catalogIdsOfCreditPurchases = new HashSet<String>();
        if(IDataUtil.isNotEmpty(commparaInfos)){
            for (int i = 0; i < commparaInfos.size(); i++) {
                IData commparaInfo = commparaInfos.getData(i);
                String catalogId=commparaInfo.getString("PARAM_CODE");
                if(catalogIdsOfCreditPurchases.contains(catalogId)){
                    continue;
                }else{
                    catalogIdsOfCreditPurchases.add(catalogId);
                }
                if(StringUtils.isNotBlank(catalogId)){
                    IDataset catalogInfos = UpcCall.qryCatalogByCatalogId(catalogId);
                    if(IDataUtil.isNotEmpty(catalogInfos)){
                        String upCatalogId = catalogInfos.first().getString("UP_CATALOG_ID");
                        campnTypesOfCreditPurchases.add(upCatalogId);
                    }
                }
            }
        }

        IDataset results = new DatasetList();
        for (String campnTypeOfCreditPurchase : campnTypesOfCreditPurchases) {
            for (int j = 0; j < campnTypes.size(); j++) {
                IData campnType = campnTypes.getData(j);
                if(campnTypeOfCreditPurchase.equals(campnType.getString("CATALOG_ID"))){
                    results.add(campnType);
                }

            }
        }
        return results;
    }


    /**
     * 过滤返回信用购机活动
     * @param products
     * @return
     * @throws Exception
     */
    public IDataset filterCreditPurchasesProducts(IDataset products) throws Exception{
        IDataset commparaInfos =  CommparaInfoQry.getCommByParaAttr("CSM", "3119","0898");
        IDataset results = new DatasetList();
        Set<String> productIdsOfCreditPurchases = new HashSet<String>();
        if(IDataUtil.isNotEmpty(commparaInfos)) {
            for (int j = 0; j < commparaInfos.size(); j++) {
                IData commparaInfo = commparaInfos.getData(j);
                String productIdOfCreditPurchases = commparaInfo.getString("PARAM_CODE");
                if (productIdsOfCreditPurchases.contains(productIdOfCreditPurchases)) {
                    continue;
                } else {
                    productIdsOfCreditPurchases.add(productIdOfCreditPurchases);
                    for (int i = 0; i < products.size(); i++) {
                        IData product = products.getData(i);
                        String productId = product.getString("PRODUCT_ID");
                        if (StringUtils.isNotBlank(productIdOfCreditPurchases)
                                && productIdOfCreditPurchases.equals(productId)) {
                            results.add(product);
                        }
                    }
                }
            }
        }
        return results;
    }

    /**
     * 过滤返回信用购机活动包
     * @param saleActives
     * @return
     * @throws Exception
     */
    public IDataset filterCreditPurchasesSaleActives(IDataset saleActives) throws Exception{
        IDataset commparaInfos =  CommparaInfoQry.getCommByParaAttr("CSM", "3119","0898");
        IDataset results = new DatasetList();
        IData otherInfo=new DataMap();
        if(IDataUtil.isNotEmpty(commparaInfos)){
            for (int i = 0; i < saleActives.size(); i++) {
                IData saleActive = saleActives.getData(i);
                if(!"".equals(saleActive.getString("ALL_FAIL",""))){
                    otherInfo=saleActive;
                    continue;
                }
                String packageId = saleActive.getString("PACKAGE_ID");
                for (int j = 0; j < commparaInfos.size(); j++) {
                    IData commparaInfo = commparaInfos.getData(j);
                    String packageIdOfCreditPurchases=commparaInfo.getString("PARA_CODE4");
                    if(StringUtils.isNotBlank(packageIdOfCreditPurchases)
                            &&packageIdOfCreditPurchases.equals(packageId)){
                        results.add(saleActive);
                    }
                }
            }
        }
        if(DataUtils.isNotEmpty(otherInfo)){
            String allFail = "true";
            for (int i = 0, size = results.size(); i < size; i++)
            {
                IData result = results.getData(i);
                if (!"1".equals(result.getString("ERROR_FLAG")))
                {
                    allFail = "false";
                    break;
                }
            }
            otherInfo.put("ALL_FAIL", allFail);
            results.add(otherInfo);
        }
        return results;
    }
    //add by liangdg3 at 20191103 for REQ201910110011泛渠道拓展合作商家系统配置需求 end

    public IData queryFeeInfo(IData input) throws Exception{
        IData results = new DataMap();

        String packageId2 = input.getString("PACKAGE_ID2", "");
        String packageId3 = input.getString("PACKAGE_ID3", "");

        IData package2Fee = getFeeInfoByPackageId(packageId2);
        IData package3Fee = getFeeInfoByPackageId(packageId3);

        results.put("PACKAGE2_FEE",package2Fee);
        results.put("PACKAGE3_FEE",package3Fee);

        return results;
    }

    public IData getFeeInfoByPackageId(String packageId) throws Exception{

        IData result = new DataMap();
        IDataset packageIdInfo = CommparaInfoQry.getCommparaInfoByParacode4AndAttr("CSM","3119",packageId,"0898");

        if (CollectionUtils.isNotEmpty(packageIdInfo)){

            result.put("PACKAGE_FEE",packageIdInfo.getData(0).getString("PARA_CODE10","0"));
            result.put("PACKAGE_FEE_TYPE_CODE",packageIdInfo.getData(0).getString("PARA_CODE11",""));
        }

        return result;
    }
}
