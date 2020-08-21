
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreatePushemailGroupUser extends CreateGroupUser
{

    protected void actTradeUser() throws Exception
    {
        UserTradeData userTradeData = reqData.getUca().getUser();

        // 用户
        if (userTradeData != null)
        {
            // 存产品产品信息到user表
            String product_id = reqData.getUca().getProductId();
            IData productParams = reqData.cd.getProductParamMap(product_id);

            if (IDataUtil.isNotEmpty(productParams))
            {
                userTradeData.setRsrvStr1(productParams.getString("MasID"));
                userTradeData.setRsrvStr6(productParams.getString("ManagerName"));
                userTradeData.setRsrvStr7(productParams.getString("ManagerPhone"));
                userTradeData.setRsrvStr8(productParams.getString("ManagerInfo"));
                userTradeData.setRsrvStr10(reqData.getUca().getUser().getSerialNumber()); // if tag=1;
            }
        }

        super.actTradeUser();

    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        // 存产品产品信息到主台账表
        String product_id = reqData.getUca().getProductId();
        IData productParams = reqData.cd.getProductParamMap(product_id);

        if (IDataUtil.isNotEmpty(productParams))
        {

            data.put("RSRV_STR1", productParams.getString("MasID"));
            data.put("RSRV_STR6", productParams.getString("ManagerName"));
            data.put("RSRV_STR7", productParams.getString("ManagerPhone"));
            data.put("RSRV_STR8", productParams.getString("ManagerInfo"));
            data.put("RSRV_STR10", reqData.getUca().getUser().getSerialNumber());// if tag=1;
        }
    }
}
