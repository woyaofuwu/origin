
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyJWTVpnGroupUser extends DestroyGroupUser
{

    public DestroyJWTVpnGroupUser()
    {

    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVpn();
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        String userId = reqData.getUca().getUserId();
        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userId);

        if (IDataUtil.isEmpty(userVpnList))
            return;

        IData param = userVpnList.getData(0);

        data.put("RSRV_STR2", param.getString("CUST_MANAGER"));
        data.put("RSRV_STR4", param.getString("WORK_TYPE_CODE"));
        data.put("RSRV_STR5", param.getString("MAX_LINKMAN_NUM"));
        data.put("RSRV_STR6", param.getString("WORK_TYPE_CODE"));
    }
}
