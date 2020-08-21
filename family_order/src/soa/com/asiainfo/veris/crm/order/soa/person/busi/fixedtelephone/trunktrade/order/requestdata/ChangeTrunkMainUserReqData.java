
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class ChangeTrunkMainUserReqData extends BaseReqData
{

    private String newMianSn;

    private UcaData newUca;

    public String getNewMianSn()
    {
        return newMianSn;
    }

    public UcaData getNewUca()
    {
        return newUca;
    }

    public void setNewMianSn(String newMianSn)
    {
        this.newMianSn = newMianSn;
    }

    public void setNewUca(UcaData newUca)
    {
        this.newUca = newUca;
    }

}
