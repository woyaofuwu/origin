
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateLbsGroupUser extends CreateGroupUser
{

    /**
     * @description 处理user表数据
     */
    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);// setTrade(map);
        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        map.put("RSRV_STR8", paramData.getString("MGR_PHONE", ""));
        map.put("RSRV_STR7", paramData.getString("MGR_INFO", ""));
        map.put("RSRV_STR9", paramData.getString("ADDRESS", ""));
        map.put("RSRV_STR4", paramData.getString("LBS_PWD", ""));
    }

    @Override
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();

        IData tradeData = bizData.getTrade();

        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        tradeData.put("RSRV_STR8", paramData.getString("MGR_PHONE", ""));
        tradeData.put("RSRV_STR7", paramData.getString("MGR_INFO", ""));
        tradeData.put("RSRV_STR9", paramData.getString("ADDRESS", ""));
        tradeData.put("RSRV_STR4", paramData.getString("LBS_PWD", ""));
    }

}
