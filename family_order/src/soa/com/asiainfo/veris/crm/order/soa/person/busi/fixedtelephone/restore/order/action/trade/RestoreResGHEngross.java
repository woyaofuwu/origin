
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.action.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreResEngross.java
 * @Description: 复机时资源的预占
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-4-22 上午10:39:56
 */
public class RestoreResGHEngross implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        for (ResTradeData resTradeData : resTradeDatas)
        {
            if (BofConst.MODIFY_TAG_ADD.equals(resTradeData.getModifyTag()))
            {
                if (StringUtils.equals("0", resTradeData.getRsrvTag1()))// 代表是复机时新换的资源（处理资源时写在rsrv_tag1上的）
                {
                    String netTypeCode = btd.getRD().getUca().getUser().getNetTypeCode();
                    if (StringUtils.equals("0", resTradeData.getResTypeCode())) // 换手机号码
                    {
                        if (StringUtils.equals("07", netTypeCode))// 物联网
                        {
                            // ResCall.resEngrossForIOTSim("0", "0", resTradeData.getResCode(),
                            // resTradeData.getResTypeCode());
                        }
                        else
                        {
                            // ResCall.resEngrossForSim("0", "0", resTradeData.getResCode(),
                            // resTradeData.getResTypeCode());
                        }
                    }
                    else if (StringUtils.equals("1", resTradeData.getResTypeCode())) // 换sim卡
                    {
                        if (StringUtils.equals("07", netTypeCode))// 物联网
                        {
                            // ResCall.resEngrossForIOTSim("0", "0", resTradeData.getResCode(),
                            // resTradeData.getResTypeCode());
                        }
                        else
                        {
                            // ResCall.resEngrossForSim("0", "0", resTradeData.getResCode(),
                            // resTradeData.getResTypeCode());
                        }
                    }
                }
            }
        }
    }
}
