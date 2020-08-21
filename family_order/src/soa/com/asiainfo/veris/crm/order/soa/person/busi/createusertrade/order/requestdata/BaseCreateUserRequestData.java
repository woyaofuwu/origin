
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class BaseCreateUserRequestData extends BaseReqData
{
    // TODO 所有信息目前都写在基类里，后续可能分开，目前跟界面区域保持一致

    // ---------------------------基本信息---------------------------------//

    // private String serialNumber; // 开户号码

    // ---------------------------产品信息---------------------------------//

    private ProductData mainProduct;

    private String otherList; // 其他信息

    /* 产品元素信息 */
    private List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();

    public void addPmd(ProductModuleData pmd)
    {
        this.pmds.add(pmd);
    }

    /*
     * public String getSerialNumber() { return serialNumber; }
     */

    public ProductData getMainProduct()
    {
        return mainProduct;
    }

    public String getOtherList()
    {
        return otherList;
    }

    public List<ProductModuleData> getPmds()
    {
        return pmds;
    }

    /*
     * public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
     */

    public void setMainProduct(String productId) throws Exception
    {
        this.mainProduct = new ProductData(productId);
    }

    public void setOtherList(String otherList)
    {
        this.otherList = otherList;
    }

    public void setPmds(List<ProductModuleData> pmds)
    {
        this.pmds = pmds;
    }

}
