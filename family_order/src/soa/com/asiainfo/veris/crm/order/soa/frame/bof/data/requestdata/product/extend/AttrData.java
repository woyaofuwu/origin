
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend;

/**
 * @author Administrator
 */
public class AttrData
{
    private String attrCode;

    private String attrValue;

    private String modifyTag;

    private String instId;

    public String getAttrCode()
    {
        return attrCode;
    }

    public String getAttrValue()
    {
        return attrValue;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public void setAttrCode(String attrCode)
    {
        this.attrCode = attrCode;
    }

    public void setAttrValue(String attrValue)
    {
        this.attrValue = attrValue;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }
}
