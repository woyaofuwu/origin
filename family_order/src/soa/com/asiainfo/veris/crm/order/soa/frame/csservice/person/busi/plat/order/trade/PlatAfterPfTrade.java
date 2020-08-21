
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.trade;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatAfterPfReqData;

public class PlatAfterPfTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        PlatAfterPfReqData prd = (PlatAfterPfReqData) btd.getRD();
        String serial_number = prd.getUca().getUser().getSerialNumber();

        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setRsrvValueCode("NET_USER_INFO");
        otherTD.setRsrvValue(prd.getPassId());
        otherTD.setRsrvStr1(prd.getTradeId());

        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setUserId(prd.getUca().getUser().getUserId());
        otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
        otherTD.setRemark("服务开通参数回写接口");
        otherTD.setModifyTag("0");
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(serial_number, otherTD);
    }

}
