
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.req.upgradeVpn;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class UpgradeVpnBeanReqData extends GroupReqData
{
    private String VPN_NO; // VPMN编号

    private String VPN_REMARK; // 备注

    public String getVPN_NO()
    {
        return VPN_NO;
    }

    public String getVPN_REMARK()
    {
        return VPN_REMARK;
    }

    public void setVPN_NO(String vpn_no)
    {
        VPN_NO = vpn_no;
    }

    public void setVPN_REMARK(String vpn_remark)
    {
        VPN_REMARK = vpn_remark;
    }

}
