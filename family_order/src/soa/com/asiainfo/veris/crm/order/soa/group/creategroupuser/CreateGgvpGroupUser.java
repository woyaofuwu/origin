package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

/**
 * 集团电子流量包新增产品成员受理类
 * @author chenzg
 *
 */
public class CreateGgvpGroupUser extends CreateGroupUser
{
    private static transient Logger logger = Logger.getLogger(CreateGgvpGroupUser.class);
    public CreateGgvpGroupUser()
    {

    }

    /**
     * @description 业务执行前处理
     * @author chenzg
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * @description 子类执行的动作
     * @author yish
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

    /**
     * @description 处理user表数据
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
    }
}
