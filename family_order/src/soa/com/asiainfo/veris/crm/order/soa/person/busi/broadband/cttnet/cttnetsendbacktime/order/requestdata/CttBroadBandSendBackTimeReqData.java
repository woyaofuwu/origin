
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetsendbacktime.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class CttBroadBandSendBackTimeReqData extends BaseReqData
{
    private String send_back_days;

    public String getSend_back_days()
    {
        return send_back_days;
    }

    public void setSend_back_days(String send_back_days)
    {
        this.send_back_days = send_back_days;
    }

}
