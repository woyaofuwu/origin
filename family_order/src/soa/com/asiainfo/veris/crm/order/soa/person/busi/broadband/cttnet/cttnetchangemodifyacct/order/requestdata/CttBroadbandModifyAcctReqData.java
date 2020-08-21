
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemodifyacct.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class CttBroadbandModifyAcctReqData extends BaseReqData
{
    private String newAcctId; // 联系电话

    public String getNewAcctId()
    {
        return newAcctId;
    }

    public void setNewAcctId(String newAcctId)
    {
        this.newAcctId = newAcctId;
    }

}
