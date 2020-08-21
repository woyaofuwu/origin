
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroySuperTeleGroupMember extends DestroyGroupMember
{

    public DestroySuperTeleGroupMember()
    {

    }

    /**
     * 生成其它台帐数据
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 登记VPNMEB用户信息
        super.infoRegDataVpn();
    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    public void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();
        tradeData.put("RSRV_STR10", "0"); // 成员付费方式[服务开通特殊参数]
    }

}
