
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.handler;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class BaseHandler implements IBackSmsHandler
{

    @Override
    public IData actSmsBack(IData param)
    {
        IData result = new DataMap();
        result.put("DEAL_STATE", "11");
        return result;
    }

}
