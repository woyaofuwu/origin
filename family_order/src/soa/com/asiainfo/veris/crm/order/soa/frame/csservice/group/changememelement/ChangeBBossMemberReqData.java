
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class ChangeBBossMemberReqData extends ChangeMemElementReqData
{
    public boolean isMerchInfo;// 商产品区分的标志，商品为true,产品为false

    public IData bbossProductInfo;// 成员产品信息
    
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

    public boolean isMerchInfo()
    {
        return isMerchInfo;
    }

    public void setBbossProductInfo(IData bbossProductInfo)
    {
        this.bbossProductInfo = bbossProductInfo;
    }

    public void setMerchInfo(boolean isMerchInfo)
    {
        this.isMerchInfo = isMerchInfo;
    }
}
