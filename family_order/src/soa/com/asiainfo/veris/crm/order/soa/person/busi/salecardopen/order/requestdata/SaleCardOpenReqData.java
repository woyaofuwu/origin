
package com.asiainfo.veris.crm.order.soa.person.busi.salecardopen.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class SaleCardOpenReqData extends BaseReqData
{

    private String SaleTypeCode;// 资源销售类型

    private String AdvancePay;// 预存费用

    private String DevicePrice;// 卡费

    private String AgentId;// 代理商部门

    private String ProductId;// 营销活动产品id

    private String PackageId;// 营销活动包id

    private String IsTag;// 是否校园卡促销买断方式
    
    private String ActiveTime;// 是否校园卡促销买断方式

    public String getAdvancePay()
    {
        return AdvancePay;
    }

    public String getAgentId()
    {
        return AgentId;
    }

    public String getDevicePrice()
    {
        return DevicePrice;
    }

    public String getIsTag()
    {
        return IsTag;
    }

    public String getPackageId()
    {
        return PackageId;
    }

    public String getProductId()
    {
        return ProductId;
    }

    public String getSaleTypeCode()
    {
        return SaleTypeCode;
    }
    
    public String getActiveTime()
    {
        return ActiveTime;
    }


    public void setAdvancePay(String AdvancePay)
    {
        this.AdvancePay = AdvancePay;
    }

    public void setAgentId(String AgentId)
    {
        this.AgentId = AgentId;
    }

    public void setDevicePrice(String DevicePrice)
    {
        this.DevicePrice = DevicePrice;
    }

    public void setIsTag(String IsTag)
    {
        this.IsTag = IsTag;
    }

    public void setPackageId(String PackageId)
    {
        this.PackageId = PackageId;
    }

    public void setProductId(String ProductId)
    {
        this.ProductId = ProductId;
    }
    

    public void setActiveTime(String ActiveTime)
    {
        this.ActiveTime = ActiveTime;
    }

    public void setSaleTypeCode(String SaleTypeCode)
    {
        this.SaleTypeCode = SaleTypeCode;
    }

}
