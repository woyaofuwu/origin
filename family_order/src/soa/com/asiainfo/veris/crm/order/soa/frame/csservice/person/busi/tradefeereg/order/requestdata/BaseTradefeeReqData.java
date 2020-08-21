
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.tradefeereg.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BaseTradefeeReqData extends BaseReqData
{
    // 接口过来的数据
    // TRADEFEELISTS 费用列表，build时，用的是基类的setFeeList
    private String payMoneyCode; // 付款方式 0-现金

    public String getPayMoneyCode()
    {
        return payMoneyCode;
    }

    public void setPayMoneyCode(String payMoneyCode)
    {
        this.payMoneyCode = payMoneyCode;
    }
}
