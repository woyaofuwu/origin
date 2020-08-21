
package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class SupplierChargeReqData extends BaseReqData
{
    public String custName;

    public String remark;
    
    public String acceptMonth;
    
    public String chnlId;
    
    public String factoryCode;
    
    public String year;
    
    public String rsrvStr3;
    

    public String getCustName()
    {
        return custName;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }
    
    public String getAcceptMonth()
    {
        return acceptMonth;
    }

    public void setAcceptMonth(String acceptMonth)
    {
        this.acceptMonth = acceptMonth;
    }
    
    public String getChnlId()
    {
        return chnlId;
    }

    public void setChnlId(String chnlId)
    {
        this.chnlId = chnlId;
    }
    
    public String getFactoryCode()
    {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode)
    {
        this.factoryCode = factoryCode;
    }
    
    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }
    
    public String getsetRsrvStr3()
    {
        return rsrvStr3;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }
}
