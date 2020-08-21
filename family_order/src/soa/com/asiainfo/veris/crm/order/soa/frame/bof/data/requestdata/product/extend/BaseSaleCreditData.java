
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

/**
 * @author Administrator
 */
public class BaseSaleCreditData extends ProductModuleData
{
    String creditValue;

    String creditGiftMonths;

    public BaseSaleCreditData()
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_CREDIT);
    }

    public BaseSaleCreditData(IData data)
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_CREDIT);
        this.setElementId(data.getString("ELEMENT_ID"));
        this.setCreditValue(data.getString("CREDIT_VALUE", "0"));
        this.setCreditGiftMonths(data.getString("CREDIT_GIFT_MONTHS", "0"));
        this.setModifyTag(data.getString("MODIFY_TAG"));
        this.setRemark(data.getString("REMARK"));
        this.setStartDate(data.getString("START_DATE"));
        this.setEndDate(data.getString("END_DATE"));
    }

    public String getCreditGiftMonths()
    {
        return creditGiftMonths;
    }

    public String getCreditValue()
    {
        return creditValue;
    }

    public void setCreditGiftMonths(String creditGiftMonths)
    {
        this.creditGiftMonths = creditGiftMonths;
    }

    public void setCreditValue(String creditValue)
    {
        this.creditValue = creditValue;
    }
}
