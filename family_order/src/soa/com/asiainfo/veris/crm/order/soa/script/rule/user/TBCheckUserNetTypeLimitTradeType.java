
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TBCheckUserNetTypeLimitTradeType.java
 * @Description: 强制限制此网别的用户不能办理哪些业务类型业务【TradeCheckBefore】，比如物联网用户只能办理物联网的业务， 目前只对物联网用户做了限制，其他网别的用户待完善
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-30 上午10:42:09
 */
public class TBCheckUserNetTypeLimitTradeType extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckUserNetTypeLimitTradeType.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckUserNetTypeLimitTradeType() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        // 0:输号码校验;1:提交校验;
        if (!StringUtils.equals("0", xChoiceTag))
        {
            return bResult;
        }
        // 查询号码时才执行此规则
        if ("changeuserinfo.ModifyEPostInfo".equals(databus.getString("page","")) || "einvoicehistory.EInvoiceHistory".equals(databus.getString("page","")))
        {
            return bResult;
        }

        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String netTypeCode = databus.getString("NET_TYPE_CODE", "00");
        String eparchyCode = databus.getString("EPARCHY_CODE");

        String brandCode = databus.getString("BRAND_CODE");// 用品牌判断
        // 如果是物联网用户
        if (StringUtils.equals("PWLW", brandCode))
        {
            // 从配置中取物联网 允许的业务类型
            IDataset allowTradeTypeCode = BreQryForCommparaOrTag.getCommpara("CSM", 4741, "WLW_BUSI_LIMIT", eparchyCode);
            if (IDataUtil.isNotEmpty(allowTradeTypeCode))
            {
                boolean bLimit = true;
                for (int i = 0, count = allowTradeTypeCode.size(); i < count; i++)
                {
                    if (StringUtils.equals(tradeTypeCode, allowTradeTypeCode.getData(i).getString("PARA_CODE1")))
                    {
                        bLimit = false;
                        break;
                    }
                }
                if (bLimit)
                {
                    bResult = true;
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1414, "物联网用户不能办理该业务！");
                }
            }
        }
        // 无线固话网别限制受理业务类型
        else if (StringUtils.equals("18", netTypeCode))
        {
            // 从配置中取物联网 允许的业务类型
            IDataset limitTradeTypeList = BreQryForCommparaOrTag.getCommpara("CSM", 7634, "0", eparchyCode);
            if (IDataUtil.isNotEmpty(limitTradeTypeList))
            {
                boolean bLimit = true;
                for (int i = 0, count = limitTradeTypeList.size(); i < count; i++)
                {
                    if (StringUtils.equals(tradeTypeCode, limitTradeTypeList.getData(i).getString("PARA_CODE1")))
                    {
                        bLimit = false;
                        break;
                    }
                }
                if (bLimit)
                {
                    bResult = true;
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1415, "无线固话号码不能办理该业务！");
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckUserNetTypeLimitTradeType() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
