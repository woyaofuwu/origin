
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.EmergencyOpenReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: EmergencyOpenSendCreditTradeAction.java
 * @Description: 紧急开机写trade_credit表
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2013-03-19 下午03:08:02
 */
public class EmergencyOpenSendCreditAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        EmergencyOpenReqData reqData = (EmergencyOpenReqData) btd.getRD();
        CreditTradeData creditTrade = new CreditTradeData();
        creditTrade.setUserId(reqData.getUca().getUserId());
        creditTrade.setCreditMode("callCredit");// 'addCredit-增加扣减信用度;callCredit-触发信控';
        creditTrade.setRsrvStr1(String.valueOf(Integer.valueOf(reqData.getOpenHours()) * 60));// 开机时长;前台传入的是小时，信控接受是分钟,所以转换成分钟
        creditTrade.setStartDate(reqData.getAcceptTime());
        creditTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
        creditTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(reqData.getUca().getSerialNumber(), creditTrade);
    }

}
