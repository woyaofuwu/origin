
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

/**
 * 集团ABB产品修改
 * 
 * @author 
 */
public class ChangeABBUserElement extends ChangeUserElement
{
    
    public ChangeABBUserElement()
    {
    }


    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

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
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
    }
    
}
