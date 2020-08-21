
package com.asiainfo.veris.crm.order.soa.script.rule.undo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: UndoCheckTrade.java
 * @Description: 返销校验订单
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-29 下午2:44:18
 */
public class UndoCheckTrade extends BreBase implements IBREScript
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(UndoCheckTrade.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UndoCheckTrade() >>>>>>>>>>>>>>>>>>");

        IData idataHisTrade = databus.getData("TRADE_INFO");
        int iTradeTypeCode = databus.getInt("TRADE_TYPE_CODE");
        String sUserId = idataHisTrade.getString("USER_ID");
        IData userInfoData = UcaInfoQry.qryUserInfoByUserId(sUserId);

        // 批开返销检查
        if (iTradeTypeCode == 500)
        {

            if (!StringUtils.equals("1", userInfoData.getString("OPEN_MODE")))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751089, "业务返销校验-号码已返单，此业务无法返销！");
                return true;
            }
        }

        // 销户返销检查
        if (iTradeTypeCode == 192)
        {
            if (!StringUtils.equals("2", userInfoData.getString("REMOVE_TAG")))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751092, "业务返销校验-非销户用户不能办理销户返销！");
                return true;
            }
        }
        return false;
    }

}
