
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

/**
 * 集团客户预缴款(虚拟)销户
 * 
 * @author 
 */
public class DestroyPrepayGroupUser extends DestroyGroupUser
{
    public DestroyPrepayGroupUser()
    {

    }
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }
      
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
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
    
}
