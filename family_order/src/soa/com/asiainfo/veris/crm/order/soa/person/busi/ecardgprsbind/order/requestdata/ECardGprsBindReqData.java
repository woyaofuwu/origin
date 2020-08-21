
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ECardGprsBindReqData extends BaseReqData
{

    private String e_serial_number;

    private String e_user_id;

    public String getE_serial_number()
    {
        return e_serial_number;
    }

    public String getE_user_id()
    {
        return e_user_id;
    }

    public void setE_serial_number(String e_serial_number)
    {
        this.e_serial_number = e_serial_number;
    }

    public void setE_user_id(String e_user_id)
    {
        this.e_user_id = e_user_id;
    }

}
