
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 初始化相關配置接口
 * @author: xiaocl
 */
public class InitCheckTag
{
    private static Logger logger = Logger.getLogger(InitCheckTag.class);

    public void initCheckTag(IData databus, CheckProductDecision checkProductDecision, CheckProductData checkProductData) throws Exception
    {
        IDataset list = BreQryForProduct.getProductLimitOnCommpara(checkProductData.getTradeTypeCode(), checkProductData.getEparchyCode());
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" prodcheck+[list] " + list);
        int iCount = list.size();
        for (int idx = 0; idx < iCount; idx++)
        {
            if ("4444".equals(list.get(idx, "PARAM_ATTR")))
            {
                checkProductDecision.setBPackage(true);
            }
            else if ("4445".equals(list.get(idx, "PARAM_ATTR")))
            {
                checkProductDecision.setBService(true);
            }
            else if ("4446".equals(list.get(idx, "PARAM_ATTR")))
            {
                checkProductDecision.setBDiscntLimit(true);
            }
            /*
             * else if ("4448".equals(list.get(idx, "PARAM_ATTR"))) { CheckProductDecision.setBIsCheckLimit(true); }
             */
            else if ("4447".equals(list.get(idx, "PARAM_ATTR")))
            {
                checkProductDecision.setBOnlyCheckSvcState(true);
            }
        }

        /* 新增对有某权限就不走产品依赖互斥校验 start */
        /*
         * IDataset list = new DatasetList(); list = BreQryForCommparaOrTag.getCommpara("CSM", 3730,
         * CheckProductData.getTradeTypeCode(), CheckProductData.getEparchyCode()); if (list.size() > 0) { String
         * privClass = StaffPrivUtil.getFieldPrivClass(CheckProductData.getTradeStaffId(),
         * list.getData(0).getString("PARA_CODE1", "")); if (StringUtils.isNotBlank(privClass) &&
         * !"0".equals(privClass)) { CheckProductDecision.setBIsCheckLimit(true); //bIsCheckLimit = true; } }
         */
    }

}
