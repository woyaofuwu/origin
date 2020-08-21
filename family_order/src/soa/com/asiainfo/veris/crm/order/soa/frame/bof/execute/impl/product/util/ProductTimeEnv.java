
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util;

public class ProductTimeEnv
{

    private String basicCalStartDate;

    private String basicAbsoluteStartDate;

    private String basicAbsoluteCancelDate;

    private String basicAbsoluteEndDate;

    private boolean noResetStartDate = false;

    public String getBasicAbsoluteCancelDate()
    {
        return basicAbsoluteCancelDate;
    }

    public String getBasicAbsoluteEndDate()
    {
        return basicAbsoluteEndDate;
    }

    public String getBasicAbsoluteStartDate()
    {
        return basicAbsoluteStartDate;
    }

    public String getBasicCalStartDate()
    {
        return basicCalStartDate;
    }

    public boolean isNoResetStartDate()
    {
        return noResetStartDate;
    }

    public void setBasicAbsoluteCancelDate(String basicAbsoluteCancelDate)
    {
        this.basicAbsoluteCancelDate = basicAbsoluteCancelDate;
    }

    public void setBasicAbsoluteEndDate(String basicAbsoluteEndDate)
    {
        this.basicAbsoluteEndDate = basicAbsoluteEndDate;
    }

    public void setBasicAbsoluteStartDate(String basicAbsoluteStartDate)
    {
        this.basicAbsoluteStartDate = basicAbsoluteStartDate;
    }

    public void setBasicCalStartDate(String basicCalStartDate)
    {
        this.basicCalStartDate = basicCalStartDate;
    }

    public void setNoResetStartDate(boolean noResetStartDate)
    {
        this.noResetStartDate = noResetStartDate;
    }

}
