
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class CentrexTeamManaBeanSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询是否完工
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset checkTradeTeam(IData inparam) throws Exception
    {
        String userId = inparam.getString("USER_ID");
        IDataset tempData = TradeInfoQry.getMainTradeByUserIdTypeCodeForGrp(userId, "1070");// 获取是否有未完工的工单
        if (null == tempData || tempData.size() < 1)
        {
            tempData = TradeInfoQry.getMainTradeByUserIdTypeCodeForGrp(userId, "1071");
        }
        return tempData;
    }

    public IDataset crtTrade(IData inparam) throws Exception
    {
        CentrexTeamManaBean centrex = new CentrexTeamManaBean();
        return centrex.crtTrade(inparam);
    }
}
