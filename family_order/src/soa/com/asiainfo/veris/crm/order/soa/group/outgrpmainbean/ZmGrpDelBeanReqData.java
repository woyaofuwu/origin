
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class ZmGrpDelBeanReqData extends GroupReqData
{
    private String USER_ID_B; // 子集团USER_ID

    private String SERIAL_NUMBER_B; // 子集团SERIAL_NUMBER

    public String getSERIAL_NUMBER_B()
    {
        return SERIAL_NUMBER_B;
    }

    public String getUSER_ID_B()
    {
        return USER_ID_B;
    }

    public void setSERIAL_NUMBER_B(String serial_number_b)
    {
        SERIAL_NUMBER_B = serial_number_b;
    }

    public void setUSER_ID_B(String user_id_b)
    {
        USER_ID_B = user_id_b;
    }

}
