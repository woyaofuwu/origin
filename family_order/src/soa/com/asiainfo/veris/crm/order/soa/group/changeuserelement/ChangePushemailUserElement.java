
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangePushemailUserElement extends ChangeUserElement
{

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        actTradeUser(); // 插用户表
    }

    protected void actTradeUser() throws Exception
    {
        IData userData = reqData.getUca().getUser().toData();

        // 用户
        if (IDataUtil.isNotEmpty(userData))
        {
            // 存产品产品信息到user表
            String product_id = reqData.getUca().getProductId();
            IData productParams = reqData.cd.getProductParamMap(product_id);

            if (IDataUtil.isNotEmpty(productParams))
            {

                userData.put("RSRV_STR1", productParams.getString("MasID"));
                userData.put("RSRV_STR6", productParams.getString("ManagerName"));
                userData.put("RSRV_STR7", productParams.getString("ManagerPhone"));
                userData.put("RSRV_STR8", productParams.getString("ManagerInfo"));
                userData.put("RSRV_STR10", reqData.getUca().getUser().getSerialNumber());// if tag=1;

                userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                super.addTradeUser(userData);
            }
        }

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
            data.put("RSRV_STR10", reqData.getUca().getUser().getSerialNumber());
        }
    }
}
