
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

public class CheckProductTransLimit implements IProductTransLimit
{

    private static Logger logger = Logger.getLogger(CheckProductTransLimit.class);

    public void checkProductTransLimit(IData databus, IDataset listTradeProduct, CheckProductData checkProductData) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" rule 进入 prodcheck CheckProductTransLimit 函数");

        int iCount = 0;

        String strProductId = "", strOldProductId = "";

        for (int itpIdx = 0, iSize = listTradeProduct.size(); itpIdx < iSize; itpIdx++)
        {
            IData tradeProduct = listTradeProduct.getData(itpIdx);
            if ("00".equals(tradeProduct.getString("PRODUCT_MODE")) && "2".equals(tradeProduct.getString("MODIFY_TAG")))
            {
                strProductId = tradeProduct.getString("PRODUCT_ID");
                strOldProductId = tradeProduct.getString("OLD_PRODUCT_ID");

                IDataset listTag = BreQryForCommparaOrTag.getTagByTagCode(checkProductData.getEparchyCode(), "CS_CHR_CHK_EXCLUDE_BY_DTYPE", "CSM", "0");

                if (listTag.size() > 0 && "1".equals(listTag.getData(0).getString("TAG_CHAR")))
                {
                    iCount = BreQryForProduct.getProductTransLimitNew(strProductId, strOldProductId).size();
                }

                iCount = BreQryForProduct.getProductTransLimit(strProductId, strOldProductId).size();

                if (iCount == 0)
                {
                    String strOldProductName = BreQueryHelp.getNameByCode("ProductName", strOldProductId);
                    String strProductName = BreQueryHelp.getNameByCode("ProductName", strProductId);
                    String strError = "#产品依赖互斥判断:不允许由产品[" + strOldProductName + "]变更为产品[" + strProductName + "]！";

                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201415", strError);
                }
            }
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" rule 退出 prodcheck CheckProductTransLimit 函数");
    }

}
