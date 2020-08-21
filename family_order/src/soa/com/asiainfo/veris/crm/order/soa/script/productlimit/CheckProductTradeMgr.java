
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

/**
 * Copyright: Copyright 2014 6 3 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 較老版規則：
 * @author: xiaocl
 */
public class CheckProductTradeMgr
{

    private static Logger logger = Logger.getLogger(CheckProductTradeMgr.class);

    public void checkProductTradeMgr(IData databus) throws Exception
    {
        CheckProductData checkProductData = new CheckProductData();
        IinitArgments initObj = new InitArgments(databus, checkProductData);
        initObj.initArgments(databus, checkProductData);
        CheckProductDecision checkProductDecision = new CheckProductDecision();
        InitCheckTag initCheckTag = new InitCheckTag();
        initCheckTag.initCheckTag(databus, checkProductDecision, checkProductData);

        /*
         * if ( CheckProductDecision.getBIsCheckLimit()) { return; }
         */

        IDataset listTradeSvcState = new DatasetList();
        IDataset listUserAllSvcState = new DatasetList();
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" prodcheck+getBOnlyCheckSvcState " + checkProductDecision.getBOnlyCheckSvcState());
        if (!checkProductDecision.getBOnlyCheckSvcState())
        {
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" prodcheck 准备开始检查依赖互斥!");

            ICheckProductDataSet cpdsBus = new CheckProductDataSet(databus, checkProductDecision);
            /* 0.1 获取台账服务,资费,产品数据 */

            if (checkProductDecision.hasTradesvc() || checkProductDecision.hasTradeDiscnt())
            {
                cpdsBus.setTradeElementList(BreQryForProduct.getAllElement(cpdsBus.getUserTradeElementList(), cpdsBus.getUserTradeSvcList(), cpdsBus.getUserTradeDiscntList()));
                cpdsBus.setTradePackageList(BreQryForProduct.getAllPackage(cpdsBus.getUserTradePackageList(), cpdsBus.getUserTradeElementList()));

            }
            checkProductDecision.setBHasTradeElement(cpdsBus.getUserTradeElementList().size() > 0 ? true : false);
            checkProductDecision.setBHasTradePackage(cpdsBus.getUserTradePackageList().size() > 0 ? true : false);

            String strUserId = checkProductData.getUserId();
            cpdsBus.setTradeAttrList(BreQryForProduct.fixedDate(cpdsBus.getUserTradeAttrList(), strUserId));
            cpdsBus.setTradeSvcList(BreQryForProduct.fixedDate(cpdsBus.getUserTradeSvcList(), strUserId));
            cpdsBus.setTradeDiscntList(BreQryForProduct.fixedDate(cpdsBus.getUserTradeDiscntList(), strUserId));
            cpdsBus.setTradeProductList(BreQryForProduct.fixedDate(cpdsBus.getUserTradeProductList(), strUserId));
            cpdsBus.setTradePackageList(BreQryForProduct.fixedDate(cpdsBus.getUserTradePackageList(), strUserId));
            cpdsBus.setTradeElementList(BreQryForProduct.fixedDate(cpdsBus.getUserTradeElementList(), strUserId));
            cpdsBus.setTradeSvcstateList(BreQryForProduct.fixedDate(cpdsBus.getUserTradeSvcList(), strUserId));

            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listTradeAttr %s " + cpdsBus.getUserTradeAttrList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listTradeService %s " + cpdsBus.getUserTradeSvcList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listTradeDiscnt %s " + cpdsBus.getUserTradeDiscntList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listTradeProduct %s " + cpdsBus.getUserTradeProductList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listTradePackage %s " + cpdsBus.getUserTradePackageList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listTradeElement %s " + cpdsBus.getUserTradeElementList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listTradeSvcState %s " + cpdsBus.getUserTradeSvcstateList());

            /* 0.2 获取用户服务,资费,产品数据 */
            if (cpdsBus.getUserAllServiceList().size() > 0 || cpdsBus.getUserAllDiscntList().size() > 0 /*
                                                                                                         * listUserAllService.
                                                                                                         * size() > 0 ||
                                                                                                         * listUserAllDiscnt
                                                                                                         * .size() > 0
                                                                                                         */)
            {
                cpdsBus.setUserAllElementList((BreQryForProduct.getAllElement(cpdsBus.getUserAllElementList(), cpdsBus.getUserAllServiceList(), cpdsBus.getUserAllDiscntList())));
                cpdsBus.setUserAllPackageList((BreQryForProduct.getAllPackage(cpdsBus.getUserAllPackageList(), cpdsBus.getUserAllElementList())));
            }
            cpdsBus.setUserAllElementList(BreQryForProduct.fixedDate(cpdsBus.getUserAllElementList(), strUserId));
            cpdsBus.setUserAllProductList(BreQryForProduct.fixedDate(cpdsBus.getUserAllProductList(), strUserId));
            cpdsBus.setUserAllServiceList(BreQryForProduct.fixedDate(cpdsBus.getUserAllServiceList(), strUserId));
            cpdsBus.setUserAllDiscntList(BreQryForProduct.fixedDate(cpdsBus.getUserAllDiscntList(), strUserId));
            cpdsBus.setUserAllAttrList(BreQryForProduct.fixedDate(cpdsBus.getUserAllAttrList(), strUserId));
            cpdsBus.setUserAllPackageList(BreQryForProduct.fixedDate(cpdsBus.getUserAllPackageList(), strUserId));
            cpdsBus.setUserAllSvcstateList(BreQryForProduct.fixedDate(cpdsBus.getUserAllSvcstateList(), strUserId));

            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listUserAllProduct %s " + cpdsBus.getUserAllProductList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listUserAllElement %s " + cpdsBus.getUserAllElementList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listUserAllService %s " + cpdsBus.getUserAllServiceList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listUserAllDiscnt %s " + cpdsBus.getUserAllDiscntList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listUserAllAttr %s " + cpdsBus.getUserAllAttrList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listUserAllPackage %s " + cpdsBus.getUserAllPackageList());
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" listUserAllSvcState %s " + cpdsBus.getUserAllSvcstateList());

            // 1 检查当前操作元素[listTradeELement]的必选参数
            if (checkProductDecision.hasTradeElement())
            {
                IElementParamIntegrality elementParamIntegrality = new CheckElementParamIntegrality();
                elementParamIntegrality.checkElementParamIntegrality(databus, cpdsBus.getUserTradeElementList(), cpdsBus.getUserAllAttrList(), checkProductData);
            }
            // 4 开始对产品, 服务, 优惠的依赖互斥关系限制进行检查
            if ((checkProductDecision.hasTradeProduct() || checkProductDecision.hasTradesvc() || checkProductDecision.hasTradeDiscnt()) && !"190".equals(checkProductData.getTradeTypeCode()) && !"252".equals(checkProductData.getTradeTypeCode())
                    && !"1066".equals(checkProductData.getTradeTypeCode()))
            {
                // 4.1 主产品转换限制判断
                IProductTransLimit aa = new CheckProductTransLimit();
                aa.checkProductTransLimit(databus, cpdsBus.getUserTradeProductList(), checkProductData);
                // 4.4 判断产品与产品间的关系
                IProductLimitByModifyAdd ProductLimitByModifyAdd = new CheckProductlimitByModifyAdd();
                IProductLimitByModifyModify ProductLimitByModifyModify = new CheckProductLimitByModifyModify();
                IProductLimitByModifyDelete ProductLimitByModifyDelete = new CheckProductLimitByModifyDelete();
                ProductLimitByModifyAdd.checkProductLimitByAdd(databus, cpdsBus.getUserAllProductList(), cpdsBus.getUserTradeProductList(), checkProductData);
                ProductLimitByModifyModify.checkProductLimitByModify(databus, cpdsBus.getUserAllProductList(), cpdsBus.getUserTradeProductList(), checkProductData);
                ProductLimitByModifyDelete.checkProductLimitByDelete(databus, cpdsBus.getUserAllProductList(), cpdsBus.getUserTradeProductList(), checkProductData);
                // 4.5.1 产品必选包
                if (checkProductDecision.getBPackage())
                {
                    if (checkProductDecision.hasTradePackage() && !"true".equals(databus.getString("IS_COMPONENT")))
                    {
                        IForcePackageForProduct ForcePackageForProduct = new CheckForcePackageForProduct();
                        ForcePackageForProduct.checkForcePackageForProduct(databus, cpdsBus.getUserAllProductList(), cpdsBus.getUserAllPackageList(), cpdsBus.getUserTradePackageList(), checkProductData);
                    }
                }
                // 4.5 判断相关元素的必选性
                {
                    // 4.5.0 包元素选择个数限制 Max_Number <= Valid Element Count <=Min_Number
                    if ((checkProductDecision.hasTradesvc() || checkProductDecision.hasTradeDiscnt()))
                    {
                        CheckPackageHasElementCount.tacCheckPackageHasElementCount(databus, checkProductData.getEparchyCode(), cpdsBus.getUserAllElementList());
                    }
                    // 4.5.2 包必选元素
                    if (checkProductDecision.getBService())
                    {
                        IForceElemenetForPackage ForceElemenetForPackage = new CheckForceElementForPackage();
                        ForceElemenetForPackage.checkForceElementForPackage(databus, "B", cpdsBus.getUserAllPackageList(), cpdsBus.getUserTradePackageList(), cpdsBus.getUserAllElementList(), checkProductData);
                    }
                }

                if (checkProductDecision.hasTradeElement())
                {
                    /* 全局元素互斥 */
                    IElementLimitByAdd ElementLimitByAdd = new CheckElementLimitByAdd();
                    ElementLimitByAdd.checkAllElementLimitAdd(databus, cpdsBus.getUserTradeElementList(), cpdsBus.getUserAllElementList(), checkProductDecision.getBisPkgInsideElementLimit(), checkProductData);

                    if (!checkProductData.getTradeTypeCode().equals("10") || !checkProductData.getTradeTypeCode().equals("500"))
                    {// 涉及元素新增業務只跑元素新增校驗接口
                        IElementLimitByDelete ElementLimitByDelete = new CheckElementLimitByDelete();
                        ElementLimitByDelete.checkAllElementLimitDelete(databus, cpdsBus.getUserTradeElementList(), cpdsBus.getUserAllElementList(), checkProductDecision.getBisPkgInsideElementLimit(), checkProductData);
                    }

                    /* 包内元素互斥 */
                    // tacCheckAllElementLimitInPackage(databus, listTradeElement, listUserAllElement,
                    // BreFactory.IS_PKG_INSIDE_ELEMENT_LIMIT);
                }
            }
        }
        // 5 服务状态变更限制判断
        IServiceStateLimit ServiceStateLimit = new CheckServiceStateLimit();
        ServiceStateLimit.CheckServiceStateLimit(databus, listTradeSvcState, listUserAllSvcState, checkProductData);
    }
}
