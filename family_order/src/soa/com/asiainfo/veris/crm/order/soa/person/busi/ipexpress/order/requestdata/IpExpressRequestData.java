
package com.asiainfo.veris.crm.order.soa.person.busi.ipexpress.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;

public class IpExpressRequestData extends BaseReqData
{
    private List<UserTradeData> ipUserDatas = new ArrayList<UserTradeData>();

    public List<UserTradeData> getIpUserDatas()
    {
        return ipUserDatas;
    }

    public void setIpUserDatas(List<UserTradeData> ipUserDatas)
    {
        this.ipUserDatas = ipUserDatas;
    }

}
