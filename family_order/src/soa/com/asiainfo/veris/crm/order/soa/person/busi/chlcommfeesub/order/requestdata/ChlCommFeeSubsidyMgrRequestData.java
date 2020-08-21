/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.requestdata;

/**
 * @CREATED by gongp@2014-4-15 修改历史 Revision 2014-4-15 下午03:09:22
 */
public class ChlCommFeeSubsidyMgrRequestData extends BaseChlCommFeeSubRequestData
{

    private String chlType;

    private String chlCode;

    private String chlLevel;

    private String chlName;

    private String staffId;

    public String getChlCode()
    {
        return chlCode;
    }

    public String getChlLevel()
    {
        return chlLevel;
    }

    public String getChlName()
    {
        return chlName;
    }

    public String getChlType()
    {
        return chlType;
    }

    public String getStaffId()
    {
        return staffId;
    }

    public void setChlCode(String chlCode)
    {
        this.chlCode = chlCode;
    }

    public void setChlLevel(String chlLevel)
    {
        this.chlLevel = chlLevel;
    }

    public void setChlName(String chlName)
    {
        this.chlName = chlName;
    }

    public void setChlType(String chlType)
    {
        this.chlType = chlType;
    }

    public void setStaffId(String staffId)
    {
        this.staffId = staffId;
    }

}
