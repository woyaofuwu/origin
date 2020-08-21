
package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.destroyuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ReleasePhoneAndDevciesAction.java
 * @Description:
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-7-29 上午10:18:59 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-29 yxd v1.0.0 修改原因
 */
public class ReleasePhoneAndDevciesAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset resSet = TradeResInfoQry.queryAllTradeResByTradeId(tradeId);
        // N:商务固话号码 W:物品
        if (DataSetUtils.isNotBlank(resSet))
        {
            for (Object obj : resSet)
            {
                IData resData = (IData) obj;
                String resTypeCode = resData.getString("RES_TYPE_CODE");
                String resCode = resData.getString("RES_CODE");
                if (StringUtils.equals("N", resTypeCode))
                {
                    PBossCall.releasePhone(resCode, "1");
                }
                else if (StringUtils.equals("W", resTypeCode))
                {
                    PBossCall.releaseMaterial(resCode);
                }
            }
        }
    }

}
