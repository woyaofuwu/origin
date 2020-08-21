
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务登记后条件判断:当前的营销活动是[%s][%s]，业务不能继续！
 * @author: xiaocl
 */
public class CheckPreSaleactiveTradeByProductRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckPreSaleactiveTradeByProductRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckSvcGivingRule() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;

        StringBuilder strError = new StringBuilder();
        IDataset tradeSaleActivelist = databus.getDataset("TF_B_TRADE_SALEACTIVE");
        String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        for (int i = 0, iSize = tradeSaleActivelist.size(); i < iSize; i++)
        {
            IData tradeSaleActive = tradeSaleActivelist.getData(i);
            String strProduct = tradeSaleActive.getString("PRODUCT_ID");
            String strPackageId = tradeSaleActive.getString("PACKAGE_ID");
            if (tradeSaleActive.getString("MODIFY_TAG").equals("0"))
            {
                IData map = new DataMap();
                map.put("PRODUCT_ID", strProduct);
                map.put("PACKAGE_ID", strPackageId);
                map.put("EPARCHY_CODE", strEparchyCode);
                IDataset tdBProductPackage = ProductPkgInfoQry.getProductPackageForSale(strProduct, strPackageId, strEparchyCode);

                if (IDataUtil.isEmpty(tdBProductPackage))
                {
                    strError.append("业务登记后条件判断:当前的营销活动是").append(strProduct).append(strPackageId).append(",业务不能继续！");
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201187, strError.toString());
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckPreSaleactiveTradeByProductRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }

}
