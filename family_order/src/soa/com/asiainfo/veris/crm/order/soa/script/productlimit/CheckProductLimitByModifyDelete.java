
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

public class CheckProductLimitByModifyDelete implements IProductLimitByModifyDelete
{
    private static Logger logger = Logger.getLogger(CheckProductLimitByModifyDelete.class);

    @Override
    public void checkProductLimitByDelete(IData databus, IDataset listUserAllProduct, IDataset listTradeProduct, CheckProductData checkProductData) throws Exception
    {
        // TODO Auto-generated method stub

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" rule 进入 prodcheck checkProductLimitByDelete 函数");

        int iplCount = 0;
        int iplIdx = 0;

        String strLimitProductId;
        String strtpProductId, strtpStartDate, strtpEndDate, strtpModifyTag, strtpUserId;
        String strupProductId, strupStartDate, strupEndDate, strupModifyTag;
        String strrlProductId;

        IDataset listProductLimit = new DatasetList();

        if (listTradeProduct.size() == 0)
        {
            return;
        }

        int iCountTradeProduct = listTradeProduct.size();
        for (int itpIdx = 0; itpIdx < iCountTradeProduct; itpIdx++)
        {
            IData tradeProduct = listTradeProduct.getData(itpIdx);
            strLimitProductId = tradeProduct.getString("PRODUCT_ID","");
            strtpProductId = tradeProduct.getString("PRODUCT_ID","");
            strtpStartDate = tradeProduct.getString("START_DATE");
            strtpEndDate = tradeProduct.getString("END_DATE");
            strtpModifyTag = tradeProduct.getString("MODIFY_TAG");
            strtpUserId = tradeProduct.getString("USER_ID");

            if (!strtpUserId.equals(checkProductData.getUserId()))
            {
                continue;
            }
            // 被完全依赖
            iplCount = BreQryForProduct.getProductLimitB(strLimitProductId, "2", listProductLimit);
            if (iplCount > 0)
            {
                for (iplIdx = 0; iplIdx < iplCount; iplIdx++)
                {
                    strrlProductId = listProductLimit.getData(iplIdx).getString("PRODUCT_ID_A");

                    int iCountUserAllProduct = listUserAllProduct.size();
                    for (int iupIdx = 0; iupIdx < iCountUserAllProduct; iupIdx++)
                    {
                        IData userAllProduct = listUserAllProduct.getData(iupIdx);
                        strupProductId = userAllProduct.getString("PRODUCT_ID","");
                        strupStartDate = userAllProduct.getString("START_DATE");
                        strupEndDate = userAllProduct.getString("END_DATE");
                        strupModifyTag = userAllProduct.getString("MODIFY_TAG");

                        if (strupProductId.equals(strrlProductId) && !strupModifyTag.equals("1") && strupEndDate.compareTo(strtpEndDate) >= 0)
                        {
                            String strProductNameA = BreQueryHelp.getNameByCode("ProductName", strtpProductId);
                            String strProductNameB = BreQueryHelp.getNameByCode("ProductName", strrlProductId);

                            String strError = "#产品依赖互斥判断:产品[" + strtpProductId + "|" + strProductNameA + "]不能被删除，因为它被用户的另一个产品[" + strrlProductId + "|" + strProductNameB + "]所依赖。业务不能继续办理！";

                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201421, strError);
                        }
                    }
                }
            }
        }
    }
}
