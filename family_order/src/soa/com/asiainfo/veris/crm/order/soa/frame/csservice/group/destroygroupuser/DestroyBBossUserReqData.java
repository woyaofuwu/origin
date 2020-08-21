
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser;

import com.ailk.common.data.IData;

public class DestroyBBossUserReqData extends DestroyGroupUserReqData
{
    public IData MERCH;// 商品信息

    public IData GOOD_INFO;// 商品

    private IData OUT_MERCH_INFO;// 创建商品台账信息返回的用户编号等

    public IData getGOOD_INFO()
    {

        return GOOD_INFO;
    }

    public IData getMERCH()
    {
        return MERCH;
    }

    public IData getOUT_MERCH_INFO()
    {
        return OUT_MERCH_INFO;
    }

    public void setGOOD_INFO(IData good_info)
    {

        GOOD_INFO = good_info;
    }

    public void setMERCH(IData merch)
    {
        MERCH = merch;
    }

    public void setOUT_MERCH_INFO(IData out_merch_info)
    {
        OUT_MERCH_INFO = out_merch_info;
    }

}
