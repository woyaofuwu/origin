
package com.asiainfo.veris.crm.order.soa.person.busi.uec;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UECMmsSVC extends CSBizService
{

    public IData sendMMS(IData data) throws Exception
    {
        UECMmsBean bean = BeanManager.createBean(UECMmsBean.class);
        IData result = bean.sendMMS(data);
        return result;
    }

    public void setTrans(IData input) throws Exception
    {
        input.put("SERIAL_NUMBER", input.getString("RECV_OBJECT"));
    }

}
