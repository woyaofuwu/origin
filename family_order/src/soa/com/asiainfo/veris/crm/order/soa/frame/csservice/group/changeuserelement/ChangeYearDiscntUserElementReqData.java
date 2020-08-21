
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

public class ChangeYearDiscntUserElementReqData extends ChangeUserElementReqData
{
    // 短信序列码
    private String smsSeq = null;

    // 集团联系人号码
    private String serialNumber = null;

    // 用户回复标识
    private String userTag = null;

    // 产品标识
    private String productId = null;

    // 产品名称
    private String productName = null;

    // 地州
    private String eparchyCode = null;

    // 集团联系人用户id
    private String grpMgrUserId = null;

    // 集团管理员编码
    private String custMgrId = null;

    // 集团名称
    private String custName = null;

    // 集团用户id
    private String grpUserId = null;

    public String getCustMgrId()
    {
        return custMgrId;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getGrpMgrUserId()
    {
        return grpMgrUserId;
    }

    public String getGrpUserId()
    {
        return grpUserId;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getProductName()
    {
        return productName;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSmsSeq()
    {
        return smsSeq;
    }

    public String getUserTag()
    {
        return userTag;
    }

    public void setCustMgrId(String custMgrId)
    {
        this.custMgrId = custMgrId;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setGrpMgrUserId(String grpMgrUserId)
    {
        this.grpMgrUserId = grpMgrUserId;
    }

    public void setGrpUserId(String grpUserId)
    {
        this.grpUserId = grpUserId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSmsSeq(String smsSeq)
    {
        this.smsSeq = smsSeq;
    }

    public void setUserTag(String userTag)
    {
        this.userTag = userTag;
    }

}
