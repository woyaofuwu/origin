
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateDatalineUser extends CreateGroupUser
{
    protected CreateInternetGroupUserReqData reqData = null;

    /**
     * 构造函数
     */
    public CreateDatalineUser()
    {

    }
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateInternetGroupUserReqData();
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
                userTradeData.setRsrvStr7(productParams.getString("NOTIN_DETMANAGER_INFO"));
                userTradeData.setRsrvStr8(productParams.getString("NOTIN_DETMANAGER_PHONE"));
                userTradeData.setRsrvStr9(productParams.getString("NOTIN_DETADDRESS"));
                userTradeData.setRsrvStr10(productParams.getString("NOTIN_PROJECT_NAME"));

            }
            userTradeData.setCityCode(reqData.getUca().getCustGroup().getCityCode());
        }
        super.actTradeUser();
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        data.put("RSRV_STR7", paramData.getString("NOTIN_DETMANAGER_INFO", ""));
        data.put("RSRV_STR8", paramData.getString("NOTIN_DETMANAGER_PHONE", ""));
        data.put("RSRV_STR9", paramData.getString("NOTIN_DETADDRESS", ""));
        data.put("RSRV_STR10", paramData.getString("NOTIN_PROJECT_NAME", ""));

    }

    protected void setTradeBase() throws Exception
    {

        super.setTradeBase();

        IData map = bizData.getTrade();

        map.put("OLCOM_TAG", "0");
        
        map.put("PF_WAIT", reqData.getPfWait());
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();
        
        reqData = (CreateInternetGroupUserReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        reqData.setPfWait(map.getInt("PF_WAIT"));
    }

}
