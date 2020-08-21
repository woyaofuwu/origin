
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyUserNpTradeAction.java
 * @Description: 销户更新用户携转表数据
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-5 下午9:07:22
 */
public class DestroyUserNpTradeAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData ucaData = btd.getRD().getUca();
        String serialNumber = ucaData.getSerialNumber();
        String userId = ucaData.getUserId();
        String tradeTypeCode = btd.getTradeTypeCode();
        IDataset userNpDataset = UserNpInfoQry.qryUserNpInfosByUserId(userId);
        if (IDataUtil.isEmpty(userNpDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "根据用户ID查询携转资料异常！");
        }
        IData userNpData = userNpDataset.getData(0);
        String npTag = userNpData.getString("NP_TAG");
        if (StringUtils.equals("47", tradeTypeCode))// 47-携入时，携出方发起欠费注销
        {
            if (StringUtils.equals("1", npTag))// 1-已携入
            {
                npTag = "2";
            }
            else if (StringUtils.equals("6", npTag))// 6-已携回
            {
                npTag = "7";// 7-携回已销
            }
            else
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "修改用户状态集时查询用户资料记录NP_TAG异常！");
            }
        }
        else if (StringUtils.equals("48", tradeTypeCode))// 携出欠费注销
        {
            if (StringUtils.equals("4", npTag))// 4-已携出
            {
                npTag = "5";// 5-携出已销
            }
            else if (StringUtils.equals("8", npTag))// 8-携入携出
            {
                npTag = "2";// 2-携入已销
            }
            else
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "修改用户状态集时查询用户资料记录NP_TAG异常！");
            }
        }
        else if (StringUtils.equals("49", tradeTypeCode))// 49-携入注销
        {
            npTag = StringUtils.substring(ucaData.getUser().getUserTagSet(), 0, 1);
        }
        NpTradeData npTradeData = new NpTradeData(userNpData);
        npTradeData.setNpTag(npTag);
        npTradeData.setNpDestroyTime(btd.getRD().getAcceptTime());
        npTradeData.setApplyDate(btd.getRD().getAcceptTime());
        btd.add(serialNumber, npTradeData);
    }

}
