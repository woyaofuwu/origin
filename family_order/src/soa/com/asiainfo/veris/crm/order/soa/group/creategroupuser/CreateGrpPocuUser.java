
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateGrpPocuUser extends CreateGroupUser
{

    /**
     * 构造函数
     */
    public CreateGrpPocuUser()
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

    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        data.put("RSRV_STR7", paramData.getString("MGR_INFO", ""));
        data.put("RSRV_STR8", paramData.getString("MGR_PHONE", ""));
        data.put("RSRV_STR9", paramData.getString("ADDRESS", ""));

    }

    /**
     * @description 处理user表数据
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        if (IDataUtil.isNotEmpty(paramData))
        {
            map.put("RSRV_STR7", paramData.getString("MGR_INFO", ""));
            map.put("RSRV_STR8", paramData.getString("MGR_PHONE", ""));
            map.put("RSRV_STR9", paramData.getString("ADDRESS", ""));
        }
    }

}
