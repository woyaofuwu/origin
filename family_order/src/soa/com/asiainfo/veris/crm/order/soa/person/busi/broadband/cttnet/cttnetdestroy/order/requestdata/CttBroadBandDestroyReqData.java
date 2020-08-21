
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetdestroy.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class CttBroadBandDestroyReqData extends BaseReqData
{
    private String serialNumber;

    private String bookDestroyDate;

    private List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();// 产品元素信息

    private UcaData bdUCA;// 宽带三户资料

    public void addPmd(ProductModuleData pmd)
    {
        this.pmds.add(pmd);
    }

    public UcaData getBdUCA()
    {
        return bdUCA;
    }

    public String getBookDestroyDate()
    {
        return bookDestroyDate;
    }

    public List<ProductModuleData> getPmds()
    {
        return pmds;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setBdUCA(UcaData bdUCA)
    {
        this.bdUCA = bdUCA;
    }

    public void setBookDestroyDate(String bookDestroyDate)
    {
        this.bookDestroyDate = bookDestroyDate;
    }

    public void setPmds(List<ProductModuleData> pmds)
    {
        this.pmds = pmds;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }
}
