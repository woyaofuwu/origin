
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class CreateBBossMemberReqData extends CreateGroupMemberReqData
{
    public IData bbossProductInfo;// 成员产品信息

    public boolean isMerchInfo;// 商产品区分的标志，商品为true,产品为false

    public String effDate;
    
    public IDataset fluxDiscnt;//流量统付资费

    public IDataset getFluxDiscnt()
    {
        return fluxDiscnt;
    }
    
    public void setFluxDiscnt(IDataset discnt)
    {
        this.fluxDiscnt = discnt;
    }

    public IData getBbossProductInfo()
    {
        return bbossProductInfo;
    }

    public String getEffDate()
    {
        return effDate;
    }

    public boolean isMerchInfo()
    {
        return isMerchInfo;
    }

    public void setBbossProductInfo(IData bbossProductInfo)
    {
        this.bbossProductInfo = bbossProductInfo;
    }

    public void setEffDate(String effDate)
    {
        this.effDate = effDate;
    }

    public void setMerchInfo(boolean isMerchInfo)
    {
        this.isMerchInfo = isMerchInfo;
    }

}
