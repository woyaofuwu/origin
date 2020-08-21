
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TBCheckLimitNpUser.java
 * @Description: 携转审核通过后客户仅能办理缴费业务，其他业务不能办理
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-30 上午10:42:09
 */
public class TBCheckLimitNpUser extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckLimitNpUser.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckLimitNpUser() >>>>>>>>>>>>>>>>>>");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckLimitNpUser() >>>>>>>>>>>>>>>>>>");
        String userId = databus.getString("USER_ID", "0");
        String removeTag = databus.getString("REMOVE_TAG", "0");
        String userTagSet = databus.getString("USER_TAG_SET", "");
        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        IDataset list = StaticUtil.getStaticList("CHECKLIMITNPUSER");
        if (IDataUtil.isNotEmpty(list))
        {
            for (int i = 0, len = list.size(); i < len; i++)
            {
                if (list.getData(i).getString("DATA_ID", "").equals(tradeTypeCode))
                {
                    return false;
                }
            }
        }
        boolean bResult = false;
        //updata by panyu5 去掉授权码申请判断
        if(!"AUTHCODE_REQ".equals(databus.getString("COMMANDCODE"))){
        IDataset npTrade = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("41", userId, "0");
        if (IDataUtil.isNotEmpty(npTrade))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 701021, "该号码携出中，不允许办理该业务！");
        }
        else
        {
            if (StringUtils.equals("0", removeTag) && StringUtils.isNotEmpty(userTagSet) && StringUtils.equals("3", StringUtils.substring(userTagSet, 0, 1)))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 701021, "该号码携出中，不允许办理该业务！");
            }
        }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckLimitNpUser() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
