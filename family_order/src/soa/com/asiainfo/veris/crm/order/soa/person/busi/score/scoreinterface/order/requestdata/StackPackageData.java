
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import java.util.List;

public class StackPackageData
{
    private String pagDeliverFee;

    private String pagProductFee;

    private List<ItemInfoData> itemInfo;

    private String storeId;

    public List<ItemInfoData> getItemInfo()
    {
        return itemInfo;
    }

    public String getPagDeliverFee()
    {
        return pagDeliverFee;
    }

    public String getPagProductFee()
    {
        return pagProductFee;
    }

    public String getStoreId()
    {
        return storeId;
    }

    public void setItemInfo(List<ItemInfoData> itemInfo)
    {
        this.itemInfo = itemInfo;
    }

    public void setPagDeliverFee(String pagDeliverFee)
    {
        this.pagDeliverFee = pagDeliverFee;
    }

    public void setPagProductFee(String pagProductFee)
    {
        this.pagProductFee = pagProductFee;
    }

    public void setStoreId(String storeId)
    {
        this.storeId = storeId;
    }
}
