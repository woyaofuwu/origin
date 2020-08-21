
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.trade;

import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyMemberManageReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;

public class FamilyMemberManagerTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        this.addFusionProductToMainTrade(btd);
    }

    private void addFusionProductToMainTrade(BusiTradeData btd) throws Exception
    {
        FamilyMemberManageReqData reqData = (FamilyMemberManageReqData) btd.getRD();
        MainTradeData mtd = btd.getMainTradeData();
        mtd.setRsrvStr1(reqData.getFmyProductId());
        mtd.setRsrvStr2(reqData.getManagerSn());
        mtd.setRsrvStr3(reqData.getFamilySn());
        mtd.setRsrvStr4(reqData.getFamilyUserId());
    }
}
