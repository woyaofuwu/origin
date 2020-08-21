
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser.order.filter;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser.WideChangeUserCheckBean;

public class WideChangeUserFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "SERIAL_NUMBER_PRE");
        input.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE", "640"));
        input.put("SERIAL_NUMBER_A", input.getString("SERIAL_NUMBER_PRE"));
        WideChangeUserCheckBean checkBean = BeanManager.createBean(WideChangeUserCheckBean.class);
        IData res = checkBean.checkNewNumForChangeUser(input);
        input.putAll(res);
    }

}
