
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember;

public class DestroyBBossMemberReqData extends DestroyGroupMemberReqData
{
    public boolean isMerchInfo;// 商产品区分的标志，商品为true,产品为false

    public String productUserId;// 产品用户编号，仅用于网外用户销户

    public String getProductUserId()
    {
        return productUserId;
    }

    public boolean isMerchInfo()
    {
        return isMerchInfo;
    }

    public void setMerchInfo(boolean isMerchInfo)
    {
        this.isMerchInfo = isMerchInfo;
    }

    public void setProductUserId(String productUserId)
    {
        this.productUserId = productUserId;
    }
}
