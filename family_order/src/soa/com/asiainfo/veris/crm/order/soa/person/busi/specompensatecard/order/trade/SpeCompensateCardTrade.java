/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.specompensatecard.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SimCardComPFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.specompensatecard.order.requestdata.SpeCompensateCardRequestData;

/**
 * @CREATED by gongp@2014-9-2 修改历史 Revision 2014-9-2 上午10:54:09
 */
public class SpeCompensateCardTrade extends BaseTrade implements ITrade
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        SpeCompensateCardRequestData reqData = (SpeCompensateCardRequestData) btd.getRD();

        long fee = (long) Double.parseDouble(reqData.getFee()) * 100;

        MainTradeData mainTd = btd.getMainTradeData();

        mainTd.setRemark(reqData.getRemark());
        mainTd.setRsrvStr1("1");
        mainTd.setRsrvStr2(String.valueOf(fee));
        mainTd.setRsrvStr3(String.valueOf(fee));
        mainTd.setRsrvStr4(reqData.getPayMode());
        mainTd.setRsrvStr5(reqData.getResTypeCode());
        mainTd.setRsrvStr6(reqData.getSimCardNo());
        mainTd.setRsrvStr7(reqData.getSimCardNo());
        mainTd.setRsrvStr8(reqData.getResKindCode());
        mainTd.setRsrvStr9(reqData.getCardKindCode());

        if ("0".equals(reqData.getUca().getUserId()))
        {
            mainTd.setOlcomTag("0");
            mainTd.setRsrvStr10("0");
            btd.setMainTradeProcessTagSet(1, "0");
        }
        else
        {
            mainTd.setOlcomTag("1");
            mainTd.setRsrvStr10("1");
            btd.setMainTradeProcessTagSet(1, "1");
        }

        SimCardComPFeeTradeData simTd = new SimCardComPFeeTradeData();

        simTd.setFee(String.valueOf(fee));
        simTd.setSimcardnum("1");
        simTd.setTotalFee(String.valueOf(fee));
        simTd.setPayMoneyCode(reqData.getPayMode());
        simTd.setStartValue(reqData.getSimCardNo());
        simTd.setEndValue(reqData.getSimCardNo());
        simTd.setEndValue(reqData.getRemark());
        simTd.setSimTypeCode(reqData.getResTypeCode());
        simTd.setRsrvStr1("");
        simTd.setRsrvStr2("");
        simTd.setRsrvStr3("");

        btd.add(reqData.getUca().getSerialNumber(), simTd);
    }

}
