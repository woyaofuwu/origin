
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreResEngross.java
 * @Description: 复机时资源的预占
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-4-22 上午10:39:56
 */
public class RestoreResEngross implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        // 获取业务台帐资源子表
        String netTypeCode = btd.getRD().getUca().getUser().getNetTypeCode();
        List<ResTradeData> tradeResInfos = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        if (tradeResInfos!=null && tradeResInfos.size()>0)
        {
            boolean isChangePhone = false;// 是否换号码
            boolean needPossOldPhone = false;// 是否需要重新占有原号码
            boolean isChangeSimCard = false;// 是否换sim卡
            String simCardNo = "";
            String serialNumber = ""; // 要从资源台账中取
            for (int i = 0; i < tradeResInfos.size(); i++)
            {
                ResTradeData resTradeData = tradeResInfos.get(i);
                String strResTypeCode = resTradeData.getResTypeCode();
                if (StringUtils.equals("0", strResTypeCode))// 手机号码
                {
                    if (StringUtils.equals("1", resTradeData.getRsrvTag1())) // 复机时新选的资源
                    {
                        isChangePhone = true;
                    }                    
                    else if (StringUtils.equals("1", resTradeData.getRsrvTag2())) // 复机需要重新占有原号码
                    {
                        needPossOldPhone = true;
                    }

                    serialNumber = resTradeData.getResCode();
                }
                else if (StringUtils.equals("1", strResTypeCode))// sim卡
                {
                    if (StringUtils.equals("1", resTradeData.getRsrvTag1())) // 复机时新选的资源
                    {
                        isChangeSimCard = true;
                    }
                    simCardNo = resTradeData.getResCode();
                }
            }
            
            if (isChangePhone || needPossOldPhone)
            {
                // 预占新号码
                if (StringUtils.equals(PersonConst.M2M_NET_TYPE_CODE, netTypeCode))// 物联网
                {
                    ResCall.resEngrossForIOTMphone("0", "0", serialNumber,"0");
                }
                else
                {
                    ResCall.resEngrossForMphone(serialNumber);
                }
            }

            if (isChangeSimCard)
            {
                // 预占新sim卡
                if (StringUtils.equals(PersonConst.M2M_NET_TYPE_CODE, netTypeCode))// 物联网
                {
                    ResCall.resEngrossForIOTSim("0", "0", simCardNo, "1");
                }
                else
                {
                   ResCall.resEngrossForSim("0", simCardNo, serialNumber);
                }
            }
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机没有生成资源台账！");
        }
    }
}
