
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MobileLotteryActiveQry;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.MobileLotteryActiveReqData;

public class MobileLotteryActiveTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        MobileLotteryActiveReqData reqData = (MobileLotteryActiveReqData) bd.getRD();

        UcaData uca = reqData.getUca();

        IData inparam = new DataMap();
        IDataset lotteryinfo = MobileLotteryActiveQry.queryLotteryInfo(null, uca.getUserId(), null, "1");
        if (lotteryinfo.size() < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取不到用户的中奖信息！");
        }

        if (MobileLotteryActiveQry.updateLotteryInfo(getVisit().getStaffId(), getVisit().getDepartId(), reqData.getLot_month(), uca.getUserId()) <= 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经领取过奖品！");
        }

        MainTradeData mainTD = bd.getMainTradeData();
        mainTD.setRemark(reqData.getRemark());
        mainTD.setRsrvStr3(lotteryinfo.getData(0).getString("PRIZE_TYPE_CODE", ""));
        mainTD.setRsrvStr4(lotteryinfo.getData(0).getString("ACCEPT_DATE", ""));
    }

}
