
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class CreateGroupUserReqData extends GroupReqData
{
    private IData ACCT_INFO;// 账户信息

    private String acctId;// 账户标识

    // 三户资料块区
    private boolean acctIsAdd;// 账户判断标识:账户是否新增,true为新增,false为取已有的

    private IData PLAN_INFO;// 集团定制付费计划

    private IData ACCT_CONSIGN;// 托收

    private String netTypeCode;

    // private IDataset power100Infos; // 动力100

    public IData getACCT_CONSIGN()
    {
        return ACCT_CONSIGN;
    }

    public IData getACCT_INFO()
    {
        return ACCT_INFO;
    }

    public String getAcctId()
    {
        return acctId;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    // public IDataset getPower100Infos()
    // {
    // return power100Infos;
    // }

    public IData getPLAN_INFO()
    {
        return PLAN_INFO;
    }

    public boolean isAcctIsAdd()
    {
        return acctIsAdd;
    }

    public void setACCT_CONSIGN(IData acct_consign)
    {
        ACCT_CONSIGN = acct_consign;
    }

    public void setACCT_INFO(IData acct_info)
    {
        ACCT_INFO = acct_info;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAcctIsAdd(boolean acctIsAdd)
    {
        this.acctIsAdd = acctIsAdd;
    }

    // public void setPower100Infos(IDataset power100Infos)
    // {
    // this.power100Infos = power100Infos;
    // }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setPLAN_INFO(IData plan_info)
    {
        PLAN_INFO = plan_info;
    }
}
