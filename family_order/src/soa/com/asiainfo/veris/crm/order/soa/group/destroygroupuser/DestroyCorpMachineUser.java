
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyCorpMachineUser extends DestroyGroupUser
{
    public DestroyCorpMachineUser()
    {

    }

    public void setRegAfterData() throws Exception
    {
        super.actTradeSub();
    }

    public void setRegBeforeData() throws Exception
    {
        super.actTradeBefore();
    }

}
