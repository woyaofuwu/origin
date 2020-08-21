
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class RestoreUserGHReqData extends BaseReqData
{
    private String x_coding_str; // 用户资源数据串

    private String opcValue; // 换3G卡时，记录其OPC值

    private String freeSimcardFeeTag; // 如果用户满足免费换卡条件，记录其免费换卡愿意

    private String screat;// 保密设置

    // ---------------------------产品信息---------------------------------//
    private ProductData mainProduct;

    private List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();// 本次变化的元素列表

    public void addPmd(ProductModuleData pmd)
    {
        this.pmds.add(pmd);
    }

    public String getFreeSimcardFeeTag()
    {
        return freeSimcardFeeTag;
    }

    public final ProductData getMainProduct()
    {
        return mainProduct;
    }

    public String getOpcValue()
    {
        return opcValue;
    }

    public final List<ProductModuleData> getPmds()
    {
        return pmds;
    }

    public String getScreat()
    {
        return screat;
    }

    public String getX_coding_str()
    {
        return x_coding_str;
    }

    public void setFreeSimcardFeeTag(String freeSimcardFeeTag)
    {
        this.freeSimcardFeeTag = freeSimcardFeeTag;
    }

    public final void setMainProduct(ProductData mainProduct)
    {
        this.mainProduct = mainProduct;
    }

    public void setOpcValue(String opcValue)
    {
        this.opcValue = opcValue;
    }

    public final void setPmds(List<ProductModuleData> pmds)
    {
        this.pmds = pmds;
    }

    public void setScreat(String screat)
    {
        this.screat = screat;
    }

    public void setX_coding_str(String xCodingStr)
    {
        x_coding_str = xCodingStr;
    }
}
