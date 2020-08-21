
package com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;

public class CloseSmsTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTrade = btd.getMainTradeData();
        mainTrade.setRemark(btd.getRD().getRemark());

        // ssTradeSvcStateMgr.GeneSVCStateChangeTrade
        ChangeSvcStateComm comm = new ChangeSvcStateComm();
        comm.getSvcStateChangeTrade(btd);
        comm.modifyMainSvcStateByUserId(btd);

        /** 以下由Action实现 */
        // TCS_PlatInfoRegForYY(已配置，PlatStateChangeAction）
        // TCS_ModifyStateByRelationUU(已配置，ModifyStateByRelationUUAction)
    }

}
