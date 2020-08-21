
package com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.ResInfo;

public class RestoreUserNpReqData extends BaseReqData
{

    private String invoiceNo;// 发票号

    private List<ResInfo> resInfos = new ArrayList<ResInfo>();// 资源信息

    // ---------------------------产品信息---------------------------------//

    private ProductData mainProduct;

    private List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();// 本次变化的元素列表

    public void addPmd(ProductModuleData pmd)
    {
        this.pmds.add(pmd);
    }

    public final void addResInfo(ResInfo e)
    {
        this.resInfos.add(e);
    }

    public final String getInvoiceNo()
    {
        return invoiceNo;
    }

    public final ProductData getMainProduct()
    {
        return mainProduct;
    }

    public final List<ProductModuleData> getPmds()
    {
        return pmds;
    }

    public final List<ResInfo> getResInfos()
    {
        return resInfos;
    }

    public final void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public final void setMainProduct(ProductData mainProduct)
    {
        this.mainProduct = mainProduct;
    }

    public final void setPmds(List<ProductModuleData> pmds)
    {
        this.pmds = pmds;
    }

    public final void setResInfos(List<ResInfo> resInfos)
    {
        this.resInfos = resInfos;
    }

}
