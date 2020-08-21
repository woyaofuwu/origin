
package com.asiainfo.veris.crm.order.soa.script.rule.family;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class ExistsFamilyAndProductDiscntByMainCard extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsFamilyAndProductDiscntByMainCard.class);

    /**
     * 判断主卡用户的亲情优惠和产品优惠关系
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsFamilyAndProductDiscntByMainCard() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        boolean bExists = true;

        /* 获取规则配置信息 */
        String strFanilyDiscnt = ruleParam.getString(databus, "FAMILY_DISCNT");
        String strProductDiscnt = ruleParam.getString(databus, "PRODUCT_DISCNT");
        String strBrandCode = ruleParam.getString(databus, "BRAND_CODE");
        String strBrandName = ruleParam.getString(databus, "BRAND_NAME");
        String strProductDiscntName = ruleParam.getString(databus, "PRODUCT_DISCNT_NAME");

        if (strProductDiscntName == null || "".equals(strProductDiscntName))
        {
            strProductDiscntName = strProductDiscnt;
        }

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

        /* 开始逻辑规则校验 */
        for (Iterator iterTradeDiscnt = listTradeDiscnt.iterator(); iterTradeDiscnt.hasNext();)
        {
            IData tradeDiscnt = (IData) iterTradeDiscnt.next();

            if ("0".equals(tradeDiscnt.getString("MODIFY_TAG")) && strFanilyDiscnt.equals(tradeDiscnt.getString("DISCNT_CODE")))
            {
                if (!"ZZZZ".equals(strBrandCode) && !strBrandCode.equals(databus.getDataset("TF_F_USER").getData(0).getString("BRAND_CODE")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201670, "#业务后特殊限制表判断：用户品牌不是【" + strBrandName + "】，不可以办理该业务！");
                }

                IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");

                for (Iterator iterUserDiscnt = listUserDiscnt.iterator(); iterUserDiscnt.hasNext();)
                {
                    IData userDiscnt = (IData) iterUserDiscnt.next();

                    if (strProductDiscnt.indexOf("|" + userDiscnt.getString("DISCNT_CODE") + "|") > -1)
                    {
                        bExists = false;
                        break;
                    }
                }

                if (bExists)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201670, "#业务后特殊限制表判断：主卡用户没有办理【" + strProductDiscntName + "】产品优惠，不可以选择该亲情主卡优惠！");
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsFamilyAndProductDiscntByMainCard() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
