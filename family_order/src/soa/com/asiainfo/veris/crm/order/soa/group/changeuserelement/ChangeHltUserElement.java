
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeHltUserElement extends ChangeUserElement
{

    @Override
    public void actTradeBefore() throws Exception
    {
    	super.actTradeBefore();
    }

    /**
     * 其它台帐处理
     */
    @Override
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
