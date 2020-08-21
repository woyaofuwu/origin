
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyBusiRegUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class FamilyMemberManagerRegSVC extends OrderService
{
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return FamilyConstants.FamilyTradeType.UPDATE_MEMBER.getValue();
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return FamilyConstants.FamilyTradeType.UPDATE_MEMBER.getValue();
    }

    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        // 新增成员
        if (input.containsKey("SUB_ROLES"))
        {
        	FamilyBusiRegUtil.callAddRoles(input, btd);
        }
        // 删除成员
        if (input.containsKey("DEL_ROLES"))
        {
        	FamilyBusiRegUtil.callDelRoles(input, btd);
        }

    }
}
