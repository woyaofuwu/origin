
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateGprsAppCardGroupUser extends CreateGroupUser
{

    /**
     * 构造函数
     */
    public CreateGprsAppCardGroupUser()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

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
                userTradeData.setRsrvStr4(productParams.getString("NOTIN_APN_TYPE"));

                userTradeData.setRsrvStr6(productParams.getString("NOTIN_MGR_NAME"));
                userTradeData.setRsrvStr7(productParams.getString("NOTIN_MGR_PHONE"));
                userTradeData.setRsrvStr8(productParams.getString("NOTIN_DETADDRESS"));
                userTradeData.setRsrvStr9(productParams.getString("NOTIN_APN_NAME"));
                userTradeData.setRsrvStr10(reqData.getUca().getSerialNumber());
                userTradeData.setRemark(productParams.getString("NOTIN_APN_REMARK"));
            }
        }
        super.actTradeUser();
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        data.put("RSRV_STR4", paramData.getString("NOTIN_APN_TYPE", ""));
        data.put("RSRV_STR6", paramData.getString("NOTIN_MGR_NAME", ""));
        data.put("RSRV_STR7", paramData.getString("NOTIN_MGR_PHONE", ""));
        data.put("RSRV_STR8", paramData.getString("NOTIN_DETADDRESS", ""));
        data.put("RSRV_STR9", paramData.getString("NOTIN_APN_NAME", ""));
        data.put("RSRV_STR10", reqData.getUca().getSerialNumber());
    }

}
