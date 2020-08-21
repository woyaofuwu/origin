
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.filter;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class ReturnActiveTradeNewCheckFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        // TODO Auto-generated method stub

        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        // IDataUtil.chkParam(input, "IN_MODE_CODE");

        input.put("DO_MODE", input.getString("FLAG", "3"));
        input.put("IN_MODE", "1");// 校验
        // input.put("preType", "1");//预处理

    }

}
