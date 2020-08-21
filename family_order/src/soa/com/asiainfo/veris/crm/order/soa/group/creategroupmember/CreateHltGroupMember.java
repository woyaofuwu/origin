
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateHltGroupMember extends CreateGroupMember
{
    private static final Logger logger = Logger.getLogger(CreateHltGroupMember.class);

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
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }
    
    

}
