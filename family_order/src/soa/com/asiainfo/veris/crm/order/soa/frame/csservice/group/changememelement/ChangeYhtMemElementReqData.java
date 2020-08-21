
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement;

public class ChangeYhtMemElementReqData extends ChangeMemElementReqData
{
    private String zyht_nameid; // 主一号通 名称标识

    private String zyht_zsn; // 主一号通 主号码

    public String getZyht_nameid()
    {
        return zyht_nameid;
    }

    public String getZyht_zsn()
    {
        return zyht_zsn;
    }

    public void setZyht_nameid(String zyht_nameid)
    {
        this.zyht_nameid = zyht_nameid;
    }

    public void setZyht_zsn(String zyht_zsn)
    {
        this.zyht_zsn = zyht_zsn;
    }
}
