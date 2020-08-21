
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class ReturnActiveTradeNewFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        // TODO Auto-generated method stub
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        // IDataUtil.chkParam(input, "IN_MODE_CODE");

        input.put("DO_MODE", input.getString("FLAG", "3"));
        input.put("IN_MODE", "2");// 办理
        input.put("ACCEPT_NUM", input.getString("ACCEPT_NUM", "1"));
        input.put("CARD_LIST", new DatasetList());// 领卡数据为空，因为通过接口办理不能领卡，只能抽奖

    }

}
