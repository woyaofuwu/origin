
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

import com.ailk.common.data.IData;

public class CreateBBossUserReqData extends CreateGroupUserReqData
{
    private IData MERCH_INFO;// 商品信息

    private IData merchp;// 产品信息

    private IData OUT_MERCH_INFO;// 创建商品台账信息返回的用户编号等

    private IData BBOSS_PRODUCT_INFO;// 前台传过来的BBOSS产品信息

    private IData PRODUCT_ELEMENT;// 产品元素信息

    public IData getBBOSS_PRODUCT_INFO()
    {
        return BBOSS_PRODUCT_INFO;
    }

    public IData getMERCH_INFO()
    {
        return MERCH_INFO;
    }

    public IData getMerchp()
    {
        return merchp;
    }

    public IData getOUT_MERCH_INFO()
    {
        return OUT_MERCH_INFO;
    }

    public IData getPRODUCT_ELEMENT()
    {
        return PRODUCT_ELEMENT;
    }

    public void setBBOSS_PRODUCT_INFO(IData bboss_proudct_info)
    {
        BBOSS_PRODUCT_INFO = bboss_proudct_info;
    }

    public void setMERCH_INFO(IData merch_info)
    {
        MERCH_INFO = merch_info;
    }

    public void setMerchp(IData merchp)
    {
        this.merchp = merchp;
    }

    public void setOUT_MERCH_INFO(IData out_merch_info)
    {
        OUT_MERCH_INFO = out_merch_info;
    }

    public void setPRODUCT_ELEMENT(IData product_element)
    {
        PRODUCT_ELEMENT = product_element;
    }

}
