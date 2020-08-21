
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GiftFeeTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

public class TradeGiftFeeAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();
        String giftFeeList = createPersonUserRD.getXTradeGiftFee();
        if (StringUtils.isNotBlank(giftFeeList))
        {
            IDataset giftFees = new DatasetList(giftFeeList);
            for (int i = 0; i < giftFees.size(); i++)
            {
                IData giftFee = giftFees.getData(i);
                GiftFeeTradeData giftFeeTD = new GiftFeeTradeData();
                giftFeeTD.setUserId(createPersonUserRD.getUca().getUserId());
                giftFeeTD.setFeeMode(giftFee.getString("FEE_MODE"));
                giftFeeTD.setFeeTypeCode(giftFee.getString("FEE_TYPE_CODE"));
                giftFeeTD.setFee(giftFee.getString("FEE"));
                giftFeeTD.setDiscntGiftId(giftFee.getString("DISCNT_GIFT_ID"));
                giftFeeTD.setLimitMoney(giftFee.getString("LIMIT_MONEY"));
                giftFeeTD.setMonths(giftFee.getString("MONTHS"));
                giftFeeTD.setMonths(giftFee.getString("EFFICET_DATE"));
                btd.add(serialNumber, giftFeeTD);
            }
        }

    }

}
