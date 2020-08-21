
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateMDMGroupUser extends CreateGroupUser
{        
    //private static transient Logger logger = Logger.getLogger(CreateMDMGroupUser.class);

    /**
     * 构造函数
     */
    public CreateMDMGroupUser()
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
    }
    
    /**
     * @description 处理user表数据
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
    }

}
