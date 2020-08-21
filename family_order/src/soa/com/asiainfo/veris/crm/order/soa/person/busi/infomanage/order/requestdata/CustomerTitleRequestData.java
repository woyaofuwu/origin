
package com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.requestdata;

/**
 * @author think
 */
public class CustomerTitleRequestData extends BaseCustomerTitleRequestData
{
    public String custTitle;// 客户昵称

    public final String getCustTitle()
    {
        return custTitle;
    }

    public final void setCustTitle(String custTitle)
    {
        this.custTitle = custTitle;
    }

}
