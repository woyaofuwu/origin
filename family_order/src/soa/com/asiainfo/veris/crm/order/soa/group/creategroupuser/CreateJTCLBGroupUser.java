
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateJTCLBGroupUser extends CreateGroupUser
{        
    private static transient Logger logger = Logger.getLogger(CreateJTCLBGroupUser.class);

    /**
     * 构造函数
     */
    public CreateJTCLBGroupUser()
    {

    }
    

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();
        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
    }
    
    /**
     * @description 处理user表数据
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
    }
}
