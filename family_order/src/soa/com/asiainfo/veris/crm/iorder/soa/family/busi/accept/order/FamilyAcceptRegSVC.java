
package com.asiainfo.veris.crm.iorder.soa.family.busi.accept.order;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyBusiRegUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @Description 家庭资料登记类
 * @Auther: zhenggang
 * @Date: 2020/7/30 10:23
 * @version: V1.0
 */
public class FamilyAcceptRegSVC extends OrderService
{
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return input.getString(KeyConstants.ORDER_TYPE_CODE, FamilyConstants.FamilyTradeType.ACCEPT.getValue());
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return input.getString(KeyConstants.TRADE_TYPE_CODE, FamilyConstants.FamilyTradeType.ACCEPT.getValue());
    }

    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
    	FamilyBusiRegUtil.callAddRoles(input, btd);

        // 处理折扣资费
        // FusionHandleFactory.otherTradeDeal(input, btd);
        // 发送短信新增成员信息
        // FusionUtil.dealTradeSMS(btd, input.getString("SERIAL_NUMBER"), input.getString("FUSION_PRODUCT_ID"), roles, null, null);
    }

}
