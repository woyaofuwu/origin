
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

// 产品下必选包
public class CheckForcePackageForProduct implements IForcePackageForProduct
{

    private static Logger logger = Logger.getLogger(CheckForcePackageForProduct.class);

    @Override
    public void checkForcePackageForProduct(IData databus, IDataset listUserAllProduct, IDataset listUserAllPackage, IDataset listTradePackage, CheckProductData checkProductData) throws Exception
    {
        // TODO Auto-generated method stub
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("rule 进入 prodcheck CheckForcePackageForProduct 函数！");

        IDataset listFcoreLimitPkgForProd;
        String strProductBatch = "|";

        String tradeProductId = "";
        String tradePackageId = "";
        String tradeModifyTag = "";
        String tradeUserId = "";
        String tradeUserId_a = "";
        String strupProductEndDate = "";

        // TODO
        if ("1".equals(checkProductData.getSkipForcePackageForProduct()))
        {
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug("rule 退出 prodcheck CheckForcePackageForProduct 函数！因为在前台判断, 后台不管！");

            return;
        }
        for (int itdP = 0; itdP < listTradePackage.size(); itdP++)
        {
            IData tradePackage = listTradePackage.getData(itdP);
            tradeProductId = tradePackage.getString("PRODUCT_ID","");
            tradePackageId = tradePackage.getString("PACKAGE_ID","");
            tradeModifyTag = tradePackage.getString("MODIFY_TAG");
            tradeUserId = tradePackage.getString("USER_ID");
            tradeUserId_a = tradePackage.getString("USER_ID_A");
            if (strProductBatch.indexOf("|" + tradeProductId + "|") > -1)
            {
                if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                    logger.debug("add by gaoyuan @2013-11-25 : find product_id to continue");

                continue;
            }

            strProductBatch += (tradeProductId + "|");

            /* add by gaoyuan @2013-11-25 desc :: 获取产品的结束时间，用于必选包的判断 start */
            strupProductEndDate = "";
            boolean btag = true;
            boolean isTag = true;//不存在标识
            for (Iterator iterator = listUserAllProduct.iterator(); iterator.hasNext();)
            {
                IData userproduct = (IData) iterator.next();

                if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                    logger.debug("add by gaoyuan @2013-11-25 : package_product_id=[" + tradeProductId + "] : user_product_id=[" + userproduct.getString("PRODUCT_ID") + "]");

                //MODIFY BY XIAOCL
                if (tradeProductId.equals(userproduct.getString("PRODUCT_ID","")) && userproduct.getString("MODIFY_TAG").equals("1"))
                {
                     btag = false;  
                     break;
                }
                
                //海南特殊场景
                if(tradeProductId.equals(userproduct.getString("PRODUCT_ID",""))) {
                	isTag = false ; //表示存在
                }
                
                if (tradeProductId.equals(userproduct.getString("PRODUCT_ID","")) && (strupProductEndDate.equals("") || strupProductEndDate.compareTo(userproduct.getString("END_DATE")) < 0))
                {
                    strupProductEndDate = userproduct.getString("END_DATE");
                }
            }
            /* add by gaoyuan @2013-11-25 desc :: 获取产品的结束时间，用于必选包的判断 end */
            if(!btag) continue ;
            if(isTag) continue ;//表示不存在于userallproduct之中
            listFcoreLimitPkgForProd = BreQryForProduct.getForcePackageByProduct(tradeProductId, checkProductData.getEparchyCode());
            int iCountFcoreLimitPkgForProd = listFcoreLimitPkgForProd.size();
            for (int iflIdx = 0; iflIdx < iCountFcoreLimitPkgForProd; iflIdx++)
            {
                boolean bfinded = false;
                IData fvireLimitPkgForProd = listFcoreLimitPkgForProd.getData(iflIdx);
                String strflProductId = fvireLimitPkgForProd.getString("PRODUCT_ID","");
                String strflPackageId = fvireLimitPkgForProd.getString("PACKAGE_ID","");
                if (StringUtils.equals("-1", strflPackageId))
                {
                    continue; //包id为-1调过判断
                }

                int iCountUserAllPackage = listUserAllPackage.size();
                for (int iupkIdx = 0; iupkIdx < iCountUserAllPackage; iupkIdx++)
                {
                    IData userAllPackage = listUserAllPackage.getData(iupkIdx);
                    String strupPackageId = userAllPackage.getString("PACKAGE_ID","");
                    String strupEndDate = userAllPackage.getString("END_DATE");
                    String strupModifyTag = userAllPackage.getString("MODIFY_TAG","");
                    String strupProductId = userAllPackage.getString("PRODUCT_ID","");
                    String strupUserId = userAllPackage.getString("USER_ID","");
                    String strupUserId_a = userAllPackage.getString("USER_ID_A","");
                    
                    if (strflPackageId.equals(strupPackageId) && strflProductId.equals(strupProductId) && !strupModifyTag.equals("1"))
                    {
                        bfinded = true;
                        if (checkProductData.getTradeTypeCode().equals("110") || checkProductData.getTradeTypeCode().equals("150") || checkProductData.getTradeTypeCode().equals("120"))
                        {
                            bfinded = false;
                            if (!"".equals(strupProductEndDate) 
                                    && strupEndDate != null 
                                    && !"".equals(strupEndDate) 
                                    && strupEndDate.substring(0, 10).compareTo(strupProductEndDate.substring(0, 10)) >= 0)
                            {
                                bfinded = true;
                                break;
                            }
                        }
                    }
                }
                if (!bfinded)
                {
                    String strNameA = BreQueryHelp.getNameByCode("ProductName", strflProductId);
                    String strNameB = BreQueryHelp.getNameByCode("PackageName", strflPackageId);

                    String strError = "#产品依赖互斥判断:产品[" + strflProductId + "|" + strNameA + "]下，包[" + strflPackageId + "|" + strNameB + "]是必选的，业务不能继续，请重新办理！";

                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201512, strError);
                }
            }
            
        }
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("rule 退出 prodcheck CheckForcePackageForProduct 函数！");
    }

}
