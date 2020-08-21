
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyJTCLBGroupUser extends DestroyGroupUser
{

    /**
     * 构造函数
     */
    public DestroyJTCLBGroupUser()
    {

    }

    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData map = bizData.getTrade();
    }

}
