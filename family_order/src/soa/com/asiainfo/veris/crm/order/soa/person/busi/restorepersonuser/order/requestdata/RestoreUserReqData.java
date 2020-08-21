
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class RestoreUserReqData extends BaseReqData
{
    private String x_coding_str; // 用户资源数据串

    private String invoiceNo;// 发票号

    private String opcValue;

    private String oldSimCardNo;

    private String newSimCardNo;

    private String needRePosses;  // 使用原号码复机，是否需要重新占用

    private String SimFeeTag;

    private String SimCardSaleMoney;

    // ---------------------------产品信息---------------------------------//
    private ProductData mainProduct;

    private List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();// 本次变化的元素列表

    public void addPmd(ProductModuleData pmd)
    {
        this.pmds.add(pmd);
    }

    public String getInvoiceNo()
    {
        return invoiceNo;
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

    public String getX_coding_str()
    {
        return x_coding_str;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
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

    public void setX_coding_str(String xCodingStr)
    {
        x_coding_str = xCodingStr;
    }

    public String getOldSimCardNo()
    {
        return oldSimCardNo;
    }

    public void setOldSimCardNo(String oldSimCardNo)
    {
        this.oldSimCardNo = oldSimCardNo;
    }

    public String getNewSimCardNo()
    {
        return newSimCardNo;
    }

    public void setNewSimCardNo(String newSimCardNo)
    {
        this.newSimCardNo = newSimCardNo;
    }

    public String getNeedRePosses()
    {
        return needRePosses;
    }

    public void setNeedRePosses(String needRePosses)
    {
        this.needRePosses = needRePosses;
    }

    public String getSimFeeTag()
    {
        return SimFeeTag;
    }

    public void setSimFeeTag(String simFeeTag)
    {
        SimFeeTag = simFeeTag;
    }

    public String getSimCardSaleMoney()
    {
        return SimCardSaleMoney;
    }

    public void setSimCardSaleMoney(String simCardSaleMoney)
    {
        SimCardSaleMoney = simCardSaleMoney;
    }
}
