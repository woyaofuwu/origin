
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

/**
 * 集团客户预缴款(虚拟)产品新增
 * @author think
 *
 */
public class CreatePrepayGroupUser extends CreateGroupUser
{
	private static final Logger logger = Logger.getLogger(CreatePrepayGroupUser.class);
	
    public CreatePrepayGroupUser()
    {
       
    }

    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
    }

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        
    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

    }
    
    @Override
    protected void chkTradeAfter() throws Exception
    {
        super.chkTradeAfter();
        
    }
    
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }
    
}
