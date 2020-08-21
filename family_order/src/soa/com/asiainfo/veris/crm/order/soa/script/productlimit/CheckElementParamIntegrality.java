
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
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
public class CheckElementParamIntegrality extends AbstractElementParamIntegrality
{

    private static Logger logger = Logger.getLogger(CheckElementParamIntegrality.class);

    @Override
    public void checkElementParamIntegrality(IData databus, IDataset listTradeElement, IDataset listUserAllAttr, CheckProductData checkProductData) throws Exception
    {
        // TODO Auto-generated method stub
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" rule 进入 prodcheck CheckElementParamIntegrality 函数");

        String strTypeCode, strKeyName, strSqlRef, strAttrName, strspAttrCode;
        String strteTradeId, strteModifyTag, strteInstId, strteEndDate, strteElementId;
        String strtaTradeId, strtaModifyTag, strtaInstId, strtaEndData, strtaAttrCode, strtaAttrValue, strtaInstType, strIsNeedPf;

        IDataset listAttrItema;

        int iCountTradeElement = listTradeElement.size();
        for (int iteIdx = 0; iteIdx < iCountTradeElement; iteIdx++)
        {
            IData tradeEelement = listTradeElement.getData(iteIdx);
            strteElementId = tradeEelement.getString("ELEMENT_ID");
            strteTradeId = tradeEelement.getString("TRADE_ID");
            strteModifyTag = tradeEelement.getString("MODIFY_TAG");
            strteInstId = tradeEelement.getString("INST_ID");
            strteEndDate = tradeEelement.getString("END_DATE");
            strTypeCode = tradeEelement.getString("ELEMENT_TYPE_CODE");
            strIsNeedPf = tradeEelement.getString("IS_NEED_PF");

            if ("U".equals(strteModifyTag) || "1".equals(strteModifyTag) || "4".equals(strteModifyTag))
                continue;
            if (!"S".equals(strTypeCode) && !"D".equals(strTypeCode))
                continue;

            /* 每次重置 listAttrLimit 然后再查询 */
            listAttrItema = null;
            listAttrItema = BreQryForProduct.getElementForceAttr(strTypeCode, checkProductData.getEparchyCode(), strteElementId);

            int iCountAttrItema = listAttrItema.size();
            for (int iaiIdx = 0; iaiIdx < iCountAttrItema; iaiIdx++)
            {
                boolean bExists = false;
                IData attrItema = listAttrItema.getData(iaiIdx);
                strAttrName = attrItema.getString("CHA_SPEC_NAME");
                strspAttrCode = attrItema.getString("FIELD_NAME");

                int iCountTradeAttr = listUserAllAttr.size();
                for (int itaIdx = 0; itaIdx < iCountTradeAttr; itaIdx++)
                {
                    IData tradeAttr = listUserAllAttr.getData(itaIdx);
                    strtaInstId = tradeAttr.getString("RELA_INST_ID");
                    strtaEndData = tradeAttr.getString("END_DATE");
                    strtaAttrCode = tradeAttr.getString("ATTR_CODE");
                    strtaAttrValue = tradeAttr.getString("ATTR_VALUE", "");
                    strtaInstType = tradeAttr.getString("INST_TYPE");

                    if (strteInstId.equals(strtaInstId) && strteEndDate.compareTo(strtaEndData) <= 0 && strtaAttrCode.equals(strspAttrCode) && (!"".equals(strtaAttrValue.trim()) || strtaAttrValue.trim().length() > 0)
                            && strtaInstType.equals(strTypeCode))
                    {
                        if (!strteModifyTag.equals("1"))
                        {
                            bExists = true;
                            break;
                        }

                    }
                }

                if (!bExists)
                {
                    String strNameA = "";
                    String strTypeName = "";
                    if (strTypeCode.equals("S"))
                    {
                        strNameA = BreQueryHelp.getNameByCode("ServiceName", strteElementId);
                        strTypeName = "服务";
                    }
                    else if (strTypeCode.equals("D"))
                    {
                        strNameA = BreQueryHelp.getNameByCode("DiscntName", strteElementId);
                        strTypeName = "优惠";
                    }

                    String strErr = "#产品依赖互斥判断:" + strTypeCode + "【" + strNameA + "】的参数【" + strAttrName + "】为必输项，请输入后再提交！";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201564", strErr);
                }
            }
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" rule 退出 prodcheck CheckElementParamIntegrality 函数");

    }

}
