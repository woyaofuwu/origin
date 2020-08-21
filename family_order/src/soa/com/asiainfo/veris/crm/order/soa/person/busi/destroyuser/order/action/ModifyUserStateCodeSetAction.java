
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ModifyUserStateCodeSetAction.java
 * @Description: 销户修改用户携转标识
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-5 下午9:07:22
 */
public class ModifyUserStateCodeSetAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        String serialNumber = ucaData.getSerialNumber();
        String tradeTypeCode = btd.getTradeTypeCode();
        UserTradeData userTradeData = ucaData.getUser();
        String codeSet = "";
        String stateCodeSet = userTradeData.getUserStateCodeset();
        if (StringUtils.equals("48", tradeTypeCode))// 携出发起欠费注销
        {
            if (StringUtils.equals("XY", stateCodeSet) || StringUtils.equals("YX", stateCodeSet))
            {
                codeSet = "Z";// Z-携出欠费销号
            }
            else
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "修改用户状态集时查询用户资料记录异常，USER_STATE_CODESET不为【XY】！");
            }
        }
        else if (StringUtils.equals("47", tradeTypeCode))
        {
            boolean iTag = false;
            for (int i = 0, count = stateCodeSet.length(); i < count; i++)
            {
                String tag = StringUtils.substring(stateCodeSet, i, i + 1);
                if (StringUtils.equals(tag, "Y"))
                {
                    iTag = true;
                    break;
                }
            }

            if (iTag)
            {
                codeSet = "Z";// Z-携出欠费销号
            }
            else
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "修改用户状态集时查询用户资料记录异常，USER_STATE_CODESET中不包含【Y】！");
            }
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取用户状态集时业务类型编码异常！");
        }

        userTradeData.setUserStateCodeset(codeSet);
        userTradeData.setLastStopTime(btd.getRD().getAcceptTime());
        btd.add(serialNumber, userTradeData);
    }

}
