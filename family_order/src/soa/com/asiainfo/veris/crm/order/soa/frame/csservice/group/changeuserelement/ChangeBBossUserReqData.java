
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class ChangeBBossUserReqData extends ChangeUserElementReqData
{
    private IData OUT_MERCH_INFO;// 创建商品台账信息返回的用户编号等

    public IData GOOD_INFO;// BBOSS侧的商品信息

    public String orderId;// 订单号

    public String merchpOperType;// 产品操作类型

    public IDataset BBOSS_PRODUCT_PARAM_INFO;// BBOSS侧的产品参数信息
    
    private IData BBOSS_PRODUCT_INFO;// 前台传过来的BBOSS产品信息

    public IDataset getBBOSS_PRODUCT_PARAM_INFO()
    {
        return BBOSS_PRODUCT_PARAM_INFO;
    }

    public IData getGOOD_INFO()
    {
        return GOOD_INFO;
    }

    public String getMerchpOperType()
    {
        return merchpOperType;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public IData getOUT_MERCH_INFO()
    {
        return OUT_MERCH_INFO;
    }

    public void setBBOSS_PRODUCT_PARAM_INFO(IDataset bboss_product_param_info)
    {
        BBOSS_PRODUCT_PARAM_INFO = bboss_product_param_info;
    }

    public void setGOOD_INFO(IData good_info)
    {
        GOOD_INFO = good_info;
    }

    public void setMerchpOperType(String merchpOperType)
    {
        this.merchpOperType = merchpOperType;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setOUT_MERCH_INFO(IData out_merch_info)
    {
        OUT_MERCH_INFO = out_merch_info;
    }
    public IData getBBOSS_PRODUCT_INFO() {
        return BBOSS_PRODUCT_INFO;
    }

    public void setBBOSS_PRODUCT_INFO(IData bboss_prouct_info) {
        BBOSS_PRODUCT_INFO = bboss_prouct_info;
    }
}
