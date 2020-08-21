
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import org.apache.log4j.Logger;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

/**
 * 
 * @author think
 */
public class DestroyDesktopTelTdGroupMember extends DestroyGroupMember
{
    private static transient Logger logger = Logger.getLogger(DestroyDesktopTelTdGroupMember.class);

    /**
     * @description 业务执行前处理
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * @description 子类执行的动作
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
    }
    
}
