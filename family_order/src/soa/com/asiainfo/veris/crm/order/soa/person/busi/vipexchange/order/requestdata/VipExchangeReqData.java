
package com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class VipExchangeReqData extends BaseReqData
{

    private String ruleCheck; // 是否校验礼品符合大客户级别

    private List<VipExchangeData> vipExchangeDataList = new ArrayList<VipExchangeData>();

    public void addVipExchangeData(VipExchangeData vipExchangeData)
    {
        this.vipExchangeDataList.add(vipExchangeData);
    }

    public String getRuleCheck()
    {
        return ruleCheck;
    }

    public List<VipExchangeData> getVipExchangeDataList()
    {
        return vipExchangeDataList;
    }

    public void setRuleCheck(String ruleCheck)
    {
        this.ruleCheck = ruleCheck;
    }

    public void setVipExchangeDataList(List<VipExchangeData> vipExchangeDataList)
    {
        this.vipExchangeDataList = vipExchangeDataList;
    }

}
