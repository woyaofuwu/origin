
package com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class GPRSDiscntChangeReqData extends BaseReqData
{
    private String newDiscntCode = "";// 新套餐ID

    public String getNewDiscntCode()
    {
        return newDiscntCode;
    }

    public void setNewDiscntCode(String newDiscntCode)
    {
        this.newDiscntCode = newDiscntCode;
    }

}
