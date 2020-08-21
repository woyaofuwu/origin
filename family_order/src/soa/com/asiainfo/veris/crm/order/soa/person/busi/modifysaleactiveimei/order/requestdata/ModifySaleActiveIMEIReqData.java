
package com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ModifySaleActiveIMEIReqData extends BaseReqData
{
    private String oldIMEI;// 旧IMEI

    private String newIMEI;// 新IMEI

    private String relationTradeId;// 关联订单号

    private String checkTradeId;// 审批工单号

    public String getNewIMEI()
    {
        return newIMEI;
    }

    public String getOldIMEI()
    {
        return oldIMEI;
    }

    public String getRelationTradeId()
    {
        return relationTradeId;
    }

    public void setNewIMEI(String newIMEI)
    {
        this.newIMEI = newIMEI;
    }

    public void setOldIMEI(String oldIMEI)
    {
        this.oldIMEI = oldIMEI;
    }

    public void setRelationTradeId(String relationTradeId)
    {
        this.relationTradeId = relationTradeId;
    }

    public String getCheckTradeId() {
        return checkTradeId;
    }

    public void setCheckTradeId(String checkTradeId) {
        this.checkTradeId = checkTradeId;
    }
}
