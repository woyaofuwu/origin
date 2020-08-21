
package com.asiainfo.veris.crm.order.soa.person.busi.fulltimephone.order.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.exception.SvcException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: FullTimePhoneTrade.java
 * @Description: 全时通登记
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 10, 2014 2:44:10 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 10, 2014 maoke v1.0.0 修改原因
 */
public class FullTimePhoneTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        List<SvcTradeData> svcDataList = btd.getRD().getUca().getUserSvcBySvcId("231");

        if (svcDataList.isEmpty())
        {
            CSAppException.apperr(SvcException.CRM_SVC_4);
        }
    }
}
