
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateHltGroupUser extends CreateGroupUser
{

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    @Override
    protected void actTradeSvcState() throws Exception
    {
    	super.actTradeSvcState();
    }
}
