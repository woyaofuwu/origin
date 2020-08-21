
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

/**
 * 用户是否有VoIP业务
 * 
 * @author
 */
public class VoIPAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SimCardReqData simCardRD = (SimCardReqData) btd.getRD();
        String oldImsi = simCardRD.getOldSimCardInfo().getImsi();
        String newImsi = simCardRD.getNewSimCardInfo().getImsi();
        String serialNumber = simCardRD.getUca().getSerialNumber();
        //TODO huanghua 08 涉及产商品解耦---已解决
        IDataset platInfos = UserPlatSvcInfoQry.queryUserPlatByUserIdAndServiceId(simCardRD.getUca().getUserId(), "71");
        if (IDataUtil.isNotEmpty(platInfos))
        {
            IBossCall.mbVisionNotifyIBOSS(simCardRD.getTradeId(), oldImsi, newImsi, serialNumber);
        }
    }

}
