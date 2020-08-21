
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ChangeTrunkIdReqData extends BaseReqData
{

    private String trunkId;

    private String oldTrunkId;

    private String switchId;

    public String getOldTrunkId()
    {
        return oldTrunkId;
    }

    public String getSwitchId()
    {
        return switchId;
    }

    public String getTrunkId()
    {
        return trunkId;
    }

    public void setOldTrunkId(String oldTrunkId)
    {
        this.oldTrunkId = oldTrunkId;
    }

    public void setSwitchId(String switchId)
    {
        this.switchId = switchId;
    }

    public void setTrunkId(String trunkId)
    {
        this.trunkId = trunkId;
    }
}
