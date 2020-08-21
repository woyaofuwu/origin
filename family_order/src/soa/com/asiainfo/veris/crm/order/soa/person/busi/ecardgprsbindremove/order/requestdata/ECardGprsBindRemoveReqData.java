
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbindremove.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ECardGprsBindRemoveReqData extends BaseReqData
{

    private String serial_number_b;

    private String user_id_b;

    public String getSerial_number_b()
    {
        return serial_number_b;
    }

    public String getUser_id_b()
    {
        return user_id_b;
    }

    public void setSerial_number_b(String serial_number_b)
    {
        this.serial_number_b = serial_number_b;
    }

    public void setUser_id_b(String user_id_b)
    {
        this.user_id_b = user_id_b;
    }

}
