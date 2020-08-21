/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.requestdata;

/**
 * @CREATED by gongp@2014-6-7 修改历史 Revision 2014-6-7 下午06:51:32
 */
public class CancelGiveValueCardReqData extends ValueCardReqData
{
    private String dealMethod;

    private String vestStaffId;

    private String vestCityCode;

    private String vestDepartId;

    private String importCardNumList;

    public String getDealMethod()
    {
        return dealMethod;
    }

    public String getImportCardNumList()
    {
        return importCardNumList;
    }

    public String getVestCityCode()
    {
        return vestCityCode;
    }

    public String getVestDepartId()
    {
        return vestDepartId;
    }

    public String getVestStaffId()
    {
        return vestStaffId;
    }

    public void setDealMethod(String dealMethod)
    {
        this.dealMethod = dealMethod;
    }

    public void setImportCardNumList(String importCardNumList)
    {
        this.importCardNumList = importCardNumList;
    }

    public void setVestCityCode(String vestCityCode)
    {
        this.vestCityCode = vestCityCode;
    }

    public void setVestDepartId(String vestDepartId)
    {
        this.vestDepartId = vestDepartId;
    }

    public void setVestStaffId(String vestStaffId)
    {
        this.vestStaffId = vestStaffId;
    }
}
