
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.FamilyCallerBean;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyBusiRegUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class FamilyMemberAddSingleRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return FamilyConstants.FamilyTradeType.ADD_MEMBER.getValue();
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return FamilyConstants.FamilyTradeType.ADD_MEMBER.getValue();
    }

    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        // 递归子角色成员添加
    	FamilyBusiRegUtil.callAddRoles(input, btd);
        // 业务特殊处理
        FamilyCallerBean.busiSpecDeal(input, btd);
    }

    @Override
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {

    }
}
