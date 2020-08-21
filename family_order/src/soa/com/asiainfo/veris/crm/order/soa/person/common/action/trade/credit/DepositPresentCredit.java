package com.asiainfo.veris.crm.order.soa.person.common.action.trade.credit;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;

/**    
 * Copyright: Copyright  2014 Asiainfo-Linkage
 * 
 * @ClassName: DepositPresentCredit.java
 * @Description: 押金赠送信用度ACTION
 *
 * @version: v1.0.0
 * @author: maoke
 * @date: Oct 14, 2014 10:45:25 AM 
 *
 * Modification History:
 * Date            Author      Version        Description
 *-------------------------------------------------------*
 * Oct 14, 2014    maoke       v1.0.0           修改原因    
 */
public class DepositPresentCredit implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        if(Integer.parseInt(btd.getForeGift()) > 0)
        {
            CreditTradeData creditTradeData = new CreditTradeData();
            
            creditTradeData.setUserId(btd.getRD().getUca().getUserId());
            creditTradeData.setCreditValue("" + btd.getForeGift());
            creditTradeData.setCreditMode("addCredit");// 信用度类型标识'addCredit-增加扣减信用度;callCredit-触发信控';
            creditTradeData.setCreditGiftMonths("12");// 赠送信用度月份数 没什么实际意义其实是一次性赠送
            creditTradeData.setStartDate(btd.getRD().getAcceptTime());
            creditTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            creditTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            
            btd.add(btd.getRD().getUca().getSerialNumber(), creditTradeData);
        }
    }
}
