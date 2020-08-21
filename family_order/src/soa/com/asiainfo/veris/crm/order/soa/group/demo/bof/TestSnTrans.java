
package com.asiainfo.veris.crm.order.soa.group.demo.bof;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;

public class TestSnTrans implements ITrans
{

    @Override
    public void transRequestData(IData data) throws Exception
    {

        data.put("SERIAL_NUMBER", data.getString("SN"));
    }

}
