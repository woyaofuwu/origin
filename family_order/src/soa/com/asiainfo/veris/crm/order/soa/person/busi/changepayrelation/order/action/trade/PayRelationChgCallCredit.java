
package com.asiainfo.veris.crm.order.soa.person.busi.changepayrelation.order.action.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.changepayrelation.order.requestdata.ModifyPayRelationInfoReqData;

/**
 * 付费账户变更触发信用度重算
 * 
 * @author Administrator
 */
public class PayRelationChgCallCredit implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        List list = btd.get(TradeTableEnum.TRADE_PAYRELATION.getValue());
        int size = list.size();
        if (list.size() > 0)
        {
            ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();
            String newAcctId = reqData.getNewAcctId();
            String nowDate = SysDateMgr.getSysDateYYYYMMDD();
            for (int i = 0; i < size; i++)
            {
                PayRelationTradeData modPayRelTrade = (PayRelationTradeData) list.get(i);
                // 新账户付费的
                if (StringUtils.equals(newAcctId, modPayRelTrade.getAcctId()) && StringUtils.equals("0", modPayRelTrade.getModifyTag()))
                {
                    String tempStartCycleId = modPayRelTrade.getStartCycleId();
                    // 系统时间大于开始时间，则是正在生效的，需要处理
                    if (SysDateMgr.getTimeDiff(tempStartCycleId, nowDate, SysDateMgr.PATTERN_TIME_YYYYMMDD) >= 0)
                    {
                        // 触发信用度重算
                        CreditTradeData creditTradeData = new CreditTradeData();
                        creditTradeData.setUserId(modPayRelTrade.getUserId());
                        creditTradeData.setCreditMode("callCredit");// 信用度类型标识'addCredit-增加扣减信用度;callCredit-触发信控';
                        creditTradeData.setStartDate(reqData.getAcceptTime());
                        creditTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                        creditTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        btd.add(btd.getRD().getUca().getSerialNumber(), creditTradeData);
                    }
                }
            }
        }
    }

}
