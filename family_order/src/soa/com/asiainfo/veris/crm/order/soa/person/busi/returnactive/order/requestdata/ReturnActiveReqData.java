
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ReturnActiveReqData extends BaseReqData
{

    private String acceptNum;// 兑换次数

    public String getAcceptNum()
    {
        return acceptNum;
    }

    public void setAcceptNum(String acceptNum)
    {
        this.acceptNum = acceptNum;
    }
}
