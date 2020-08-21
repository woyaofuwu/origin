
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyParentVpnGroupUser extends DestroyGroupUser
{

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理VPN用户信息
        super.infoRegDataVpn();
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();

        if (IDataUtil.isEmpty(bizData.getTradeVpn()))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_20, reqData.getUca().getSerialNumber());
        }

        IData tradeVpnData = bizData.getTradeVpn().toData();

        tradeData.put("RSRV_STR4", tradeVpnData.getString("CUST_MANAGER"));
        tradeData.put("RSRV_STR5", tradeVpnData.getString("RSRV_STR4"));
        tradeData.put("RSRV_STR6", tradeVpnData.getString("MAX_LINKMAN_NUM"));
        tradeData.put("RSRV_STR7", tradeVpnData.getString("WORK_TYPE_CODE"));
    }

}
