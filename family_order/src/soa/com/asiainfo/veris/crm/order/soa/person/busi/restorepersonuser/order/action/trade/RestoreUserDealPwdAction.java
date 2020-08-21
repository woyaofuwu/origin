
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreUserDealPwdAction.java
 * @Description: 前台复机处理用户密码
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-8-5 下午2:17:44
 */
public class RestoreUserDealPwdAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // 如果用户换了新sim卡，使用sim卡的密码
        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        List<UserTradeData> userTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
        UserTradeData userTradeData = userTradeDatas.get(0);
        String newSimCardNo = "";
        for (ResTradeData resTradeData : resTradeDatas)
        {
            if (BofConst.MODIFY_TAG_ADD.equals(resTradeData.getModifyTag())
                    && StringUtils.equals("1", resTradeData.getResTypeCode())
                    && StringUtils.equals("1", resTradeData.getRsrvTag1()))
            {
                // 代表是复机时新换的资源（处理资源时写在rsrv_tag1上的）
                newSimCardNo = resTradeData.getResCode();
                break;
            }
        }
        if (StringUtils.isNotEmpty(newSimCardNo))
        {
            IDataset simCardInfoDataset = ResCall.getSimCardInfo("0", newSimCardNo, "", "");
            if (IDataUtil.isNotEmpty(simCardInfoDataset))
            {
                String cardPasswd = simCardInfoDataset.getData(0).getString("PASSWORD");
                String passCode = simCardInfoDataset.getData(0).getString("KIND");
                if (StringUtils.isNotEmpty(cardPasswd) && StringUtils.isNotEmpty(passCode))
                {
                    String processTagSet = btd.getMainTradeData().getProcessTagSet();
                    processTagSet = "N" + StringUtils.substring(processTagSet, 1);
                    // 如果是新的初始化服务密码，置台帐PROCESS_TAG_SET第一位为N，完工时根据此标识发提醒短信
                    btd.getMainTradeData().setProcessTagSet(processTagSet);
                    btd.getMainTradeData().setRsrvStr7("1");// 需变更密码
                    userTradeData.setUserPasswd(cardPasswd);
                    userTradeData.setRsrvStr3(passCode);
                    userTradeData.setRsrvTag1("1");// 密码卡
                }
            }
        }
    }
}
