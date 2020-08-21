
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

public class ItemInfoData
{

    private String itemName;

    private String itemId;

    private String subOrderId;

    private String status;

    private String statusUpdateTime;

    private String itemType;
    
    private String busiType;

    public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	private String type;

    private String itemPayCash;

    public String getItemId()
    {
        return itemId;
    }

    public String getItemName()
    {
        return itemName;
    }

    public String getItemPayCash()
    {
        return itemPayCash;
    }

    public String getItemType()
    {
        return itemType;
    }

    public String getStatus()
    {
        return status;
    }

    public String getStatusUpdateTime()
    {
        return statusUpdateTime;
    }

    public String getSubOrderId()
    {
        return subOrderId;
    }

    public String getType()
    {
        return type;
    }

    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public void setItemPayCash(String itemPayCash)
    {
        this.itemPayCash = itemPayCash;
    }

    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setStatusUpdateTime(String statusUpdateTime)
    {
        this.statusUpdateTime = statusUpdateTime;
    }

    public void setSubOrderId(String subOrderId)
    {
        this.subOrderId = subOrderId;
    }

    public void setType(String type)
    {
        this.type = type;
    }

}
