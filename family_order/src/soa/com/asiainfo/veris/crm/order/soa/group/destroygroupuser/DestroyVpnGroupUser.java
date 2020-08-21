
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUserReqData;

public class DestroyVpnGroupUser extends DestroyGroupUser
{
    protected DestroyGroupUserReqData reqData = null;

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataVpn();
    }
}
