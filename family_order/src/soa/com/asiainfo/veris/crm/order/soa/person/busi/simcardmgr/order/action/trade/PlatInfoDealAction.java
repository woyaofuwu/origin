
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

/**
 * 订购了行车卫士产品，跟行车卫士平台进行信息同步
 * 
 * @author
 */
public class PlatInfoDealAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SimCardReqData simcardData = (SimCardReqData) btd.getRD();
        String oldImsi = simcardData.getOldSimCardInfo().getImsi();
        String newImsi = simcardData.getNewSimCardInfo().getImsi();
        String serialNumber = btd.getRD().getUca().getSerialNumber();

        //TODO huanghua 06 涉及产商品解耦---已解决
        IDataset platInfo = UserPlatInfoQry.getUserPlatElecInfoByUserId(simcardData.getUca().getUserId());
        if (IDataUtil.isNotEmpty(platInfo))
        {
            CustPersonTradeData custInfo = simcardData.getUca().getCustPerson();
            IBossCall.synPlatInfo(custInfo.getPhone(), serialNumber, oldImsi, newImsi, simcardData.getTradeId());
        }
    }

}
