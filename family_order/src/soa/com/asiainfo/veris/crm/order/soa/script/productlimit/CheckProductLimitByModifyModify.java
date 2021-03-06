
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

public class CheckProductLimitByModifyModify implements IProductLimitByModifyModify
{
    private static Logger logger = Logger.getLogger(CheckProductLimitByModifyModify.class);

    @Override
    public void checkProductLimitByModify(IData databus, IDataset listUserAllProduct, IDataset listTradeProduct, CheckProductData checkProductData) throws Exception
    {

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" rule 进入 prodcheck CheckProductLimitByModifyModify 函数");

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

            if (listTradeProduct.getData(itpIdx).getString("PRODUCT_MODE").equals("00"))
            {
                // 互斥
                iplCount = BreQryForProduct.getProductLimitA(strLimitProductId, "0", listProductLimit);
                if (iplCount > 0)
                {
                    int iCountUserAllProduct = listUserAllProduct.size();
                    for (int iupIdx = 0; iupIdx < iCountUserAllProduct; iupIdx++)
                    {
                        IData userAllProduct = listUserAllProduct.getData(iupIdx);
                        strupProductId = userAllProduct.getString("PRODUCT_ID","");
                        strupStartDate = userAllProduct.getString("START_DATE");
                        strupEndDate = userAllProduct.getString("END_DATE");
                        strupModifyTag = userAllProduct.getString("MODIFY_TAG");

                        if (!listTradeProduct.getData(itpIdx).getString("OLD_PRODUCT_ID","").equals("") && listTradeProduct.getData(itpIdx).getString("OLD_PRODUCT_ID","").equals(strupProductId))
                        {
                            continue;
                        }

                        for (iplIdx = 0; iplIdx < iplCount; iplIdx++)
                        {
                            strrlProductId = listProductLimit.getData(iplIdx).getString("PRODUCT_ID_B");

                            if (strupProductId.equals(strrlProductId)
                                    && ((strupEndDate.compareTo(strtpStartDate) >= 0 && strupStartDate.compareTo(strtpStartDate) <= 0) || (strupStartDate.compareTo(strtpStartDate) >= 0 && strupStartDate.compareTo(strtpEndDate) <= 0 && strupEndDate
                                            .compareTo(strtpStartDate) >= 0)))
                            {
                                String strProductNameA = BreQueryHelp.getNameByCode("ProductName", strtpProductId);
                                String strProductNameB = BreQueryHelp.getNameByCode("ProductName", strrlProductId);

                                String strError = "#产品依赖互斥判断:当前订购的产品[" + strtpProductId + "|" + strProductNameA + "]和产品[" + strrlProductId + "|" + strProductNameB + "]互斥，不能同时生效，业务不能继续办理！";

                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201422, strError);
                            }
                        }
                    }
                }
                // 全部依赖
                iplCount = BreQryForProduct.getProductLimitA(strLimitProductId, "2", listProductLimit);
                if (iplCount > 0)
                {
                    boolean bfinded = false;
                    for (iplIdx = 0; iplIdx < iplCount; iplIdx++)
                    {
                        strrlProductId = listProductLimit.getData(iplIdx).getString("PRODUCT_ID_B");

                        int iCountUserAllProduct = listUserAllProduct.size();
                        for (int iupIdx = 0; iupIdx < iCountUserAllProduct; iupIdx++)
                        {
                            IData userAllProduct = listUserAllProduct.getData(iupIdx);
                            strupProductId = userAllProduct.getString("PRODUCT_ID","");
                            strupStartDate = userAllProduct.getString("START_DATE");
                            strupEndDate = userAllProduct.getString("END_DATE");
                            strupModifyTag = userAllProduct.getString("MODIFY_TAG");

                            if (strupProductId.equals(strrlProductId) && strupStartDate.compareTo(strtpStartDate) <= 0 && strupEndDate.compareTo(strtpEndDate) >= 0)
                            {
                                bfinded = true;
                                break;
                            }
                            if (bfinded)
                            {
                                break;
                            }
                        }
                        if (!bfinded)
                        {
                            String strProductNameA = BreQueryHelp.getNameByCode("ProductName", strtpProductId);
                            String strProductNameB = BreQueryHelp.getNameByCode("ProductName", strrlProductId);

                            String strError = "#产品依赖互斥判断:修改后产品[" + strtpProductId + "|" + strrlProductId + "]不能生效，因为它所依赖的产品[" + strrlProductId + "|" + strProductNameB + "]不存在。业务不能继续办理！";

                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201423, strError);
                        }
                    }
                }
            }
        }

    }

}
