
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

/**
 * 物联网换卡登记
 * 
 * @author wangf
 */
public class InternetOfThingsAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SimCardReqData simCardRD = (SimCardReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        if ("07".equals(uca.getUser().getNetTypeCode()))
        {// 07代表是物联网用户
            btd.getMainTradeData().setNetTypeCode("07");
            btd.getMainTradeData().setRsrvStr5("01");
            // 物联网和其它卡存入的SIM_TYPE_CODE不一样，物联网存入的是SIM_TYPE_CODE，其它卡存入的是TD_S_RESKIND和TD_S_SIMCAPACITY

            List<ResTradeData> resInfos = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
            for (int i = 0; i < resInfos.size(); i++)
            {
                ResTradeData resInfo = resInfos.get(0);
                if (BofConst.MODIFY_TAG_ADD.equals(resInfo.getModifyTag()))
                {
                    resInfo.setRsrvStr5("01");
                }
            }
        }
    }

}
