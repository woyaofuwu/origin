
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

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化
 * @author: xiaocl
 */
public class CheckForceElementForPackage implements IForceElemenetForPackage
{

    private static Logger logger = Logger.getLogger(CheckForceElementForPackage.class);

    @Override
    public void checkForceElementForPackage(IData databus, String strTypeCode, IDataset listUserAllPackage, IDataset listTradePackage, IDataset listUserAllElement, CheckProductData checkProductData) throws Exception
    {
        // TODO Auto-generated method stub

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("rule 进入 prodcheck CheckForceElementForPackage(%s) 函数");

        boolean isBreak = false;

        String strPPackageId = "", strPModifyTag = "", strBookStartDate = "", strBookTradeId = "";
        String strEElementId = "", strEModifyTag = "", strEPackageId = "", strEEndDate = "", strETypeCode = "";
        String strFElementId = "", strFPackageId = "", strFTypeCode = "";

        String strPackageIdBunch = "|";

        IDataset listFcoreElementForPkg = new DatasetList();

        int iCountTradePackage = listTradePackage.size();
        for (int iupIdx = 0; iupIdx < iCountTradePackage; iupIdx++)
        {
            isBreak = false;
            IData tradePackage = listTradePackage.getData(iupIdx);
            strPModifyTag = tradePackage.getString("MODIFY_TAG","");
            strPPackageId = tradePackage.getString("PACKAGE_ID","");
            String strProductId = tradePackage.getString("PRODUCT_ID","");
            String strEndData = tradePackage.getString("END_DATE","");

            if (!checkProductData.getUserId().equals(listTradePackage.getData(iupIdx).getString("USER_ID")))
            {
                continue;
            }

            if (strPModifyTag.equals("1"))
            {
                int iCountUserAllPackage = listUserAllPackage.size();
                for (int ipIdx = 0; ipIdx < iCountUserAllPackage; ipIdx++)
                {

                    IData userAllPackage = listUserAllPackage.getData(ipIdx);
                    if (strPPackageId.equals(userAllPackage.getString("PACKAGE_ID","")) && !"1".equals(userAllPackage.getString("MODIFY_TAG")))
                    {
                        isBreak = true;
                    }
                   
                    if("310".equals(checkProductData.getTradeTypeCode())){
                        if (strEndData.compareTo(checkProductData.getStrLastDayOfCurMonth()) <= 0 &&"1".equals(strPModifyTag)
                                && (strPPackageId.equals(userAllPackage.getString("PACKAGE_ID","")) && !"1".equals(userAllPackage.getString("MODIFY_TAG"))))
                                {
                                    isBreak = false;
                                    break;
                                }
                                if (isBreak)
                                    break;
                    }
                    
                }
            }

            if (!isBreak || strPackageIdBunch.indexOf(("|" + strPPackageId + "|")) > -1) // 检查过的package_id 不再检查- 增加
            // isBreak liuke
            {
                continue;
            }
            strPackageIdBunch += (strPPackageId + "|");

            /* 获取包下所有必选元素 */
            listFcoreElementForPkg = BreQryForProduct.getForceElementByPackage(strPPackageId, strTypeCode);

            int iCountFcoreELementForPkg = listFcoreElementForPkg.size();

            for (int iflIdx = 0; iflIdx < iCountFcoreELementForPkg; iflIdx++)
            {
                boolean bfinded = false;
                IData fcoreElementForPkg = listFcoreElementForPkg.getData(iflIdx);
                strFElementId = fcoreElementForPkg.getString("ELEMENT_ID");
                strFPackageId = fcoreElementForPkg.getString("PACKAGE_ID","");
                strFTypeCode = fcoreElementForPkg.getString("ELEMENT_TYPE_CODE");

                int iCountUserAllElement = listUserAllElement.size();
                for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
                {
                    IData userAllElement = listUserAllElement.getData(iueIdx);
                    strEElementId = userAllElement.getString("ELEMENT_ID");
                    strEModifyTag = userAllElement.getString("MODIFY_TAG");
                    strEPackageId = userAllElement.getString("PACKAGE_ID","");
                    strEEndDate = userAllElement.getString("END_DATE");
                    strETypeCode = userAllElement.getString("ELEMENT_TYPE_CODE");

                    if (strFPackageId.equals(strEPackageId) && strFElementId.equals(strEElementId) && strFTypeCode.equals(strETypeCode) && !"1".equals(strEModifyTag))
                    {
                        bfinded = true;
                        break;
                    }
                }

                if (!bfinded)
                {
                    String strKeyColumn = "", strElementType = "", strKeyName = "";
                    if (strFTypeCode.equals("S"))
                    {
                        strKeyColumn = "SERVICE_ID";
                        strElementType = "服务";
                        strKeyName = "ServiceName";
                    }
                    else if (strFTypeCode.equals("D"))
                    {
                        strKeyColumn = "DISCNT_CODE";
                        strElementType = "优惠";
                        strKeyName = "DiscntName";
                    }

                    String strNameA = BreQueryHelp.getNameByCode("PackageName", strFPackageId);
                    String strNameB = BreQueryHelp.getNameByCode(strKeyName, strFElementId);

                    String strError = "#产品依赖互斥判断:[" + strFPackageId + "|" + strNameA + "]包下，" + strElementType + "[" + strFElementId + "|" + strNameB + "]是必选的，不能删除，业务不能继续，请重新办理！";

                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201453, strError);
                }
            }
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug("rule 退出 prodcheck CheckForceElementForPackage() 函数");

    }

}
