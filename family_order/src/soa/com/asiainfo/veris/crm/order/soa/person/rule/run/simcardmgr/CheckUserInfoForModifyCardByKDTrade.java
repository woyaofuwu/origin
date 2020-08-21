
package com.asiainfo.veris.crm.order.soa.person.rule.run.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 查询是否有在途宽带工单
 */
public class CheckUserInfoForModifyCardByKDTrade extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if ("0".equals(xChoiceTag))
        {
            IDataset set = TradeInfoQry.getTradeInfoBySn("KD_" + databus.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(set))
            {
                for (int i = 0; i < set.size(); i++)
                {
                    String typeCode = set.getData(i).getString("TRADE_TYPE_CODE", "");
                    if ("600".equals(typeCode) || "611".equals(typeCode) || "612".equals(typeCode) || "613".equals(typeCode))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201509, "该用户已经开通宽带业务，请完工后再来办理【改号】业务！");
                    }
                }
            }
        }
        return false;
    }
}
