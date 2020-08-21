
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.credit;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DealCallCreditAction.java
 * @Description: 触发信控的公用action
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-8-20 下午5:02:43
 */
public class DealCallCreditAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        CreditTradeData creditTrade = new CreditTradeData();
        creditTrade.setUserId(ucaData.getUserId());
        creditTrade.setCreditMode("callCredit"); // 'addCredit-增加扣减信用度;callCredit-触发信控';
        creditTrade.setStartDate(btd.getRD().getAcceptTime());
        creditTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
        creditTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(ucaData.getSerialNumber(), creditTrade);
    }
}
