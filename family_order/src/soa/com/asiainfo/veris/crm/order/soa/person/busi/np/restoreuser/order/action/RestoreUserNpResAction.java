
package com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.order.action;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class RestoreUserNpResAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        String tradeId = btd.getTradeId();
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        for (ResTradeData resTradeData : resTradeDatas)
        {
            if (BofConst.MODIFY_TAG_ADD.equals(resTradeData.getModifyTag()))
            {

                if (StringUtils.equals("1", resTradeData.getResTypeCode()))// 暂时只可以换sim卡
                {

                    String sim = resTradeData.getResCode();
                    ResCall.modifyNpSimInfo(sim, "2", serialNumber, tradeId, userId);
                    break;

                }

            }
        }

    }

}
