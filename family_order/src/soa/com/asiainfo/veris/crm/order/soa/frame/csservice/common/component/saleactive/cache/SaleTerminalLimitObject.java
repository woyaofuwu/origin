
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.cache;

public class SaleTerminalLimitObject
{
    private String productId;

    private String packageId;

    private String terminalTypeCode;

    private String terminalModeCode;

    private String eparchyCode;

    public static final String SLAE_TERMINAL_LIMIT_CHCHE_KEY = "SaleTerminalLimitObject";

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getTerminalModeCode()
    {
        return terminalModeCode;
    }

    public String getTerminalTypeCode()
    {
        return terminalTypeCode;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setTerminalModeCode(String terminalModeCode)
    {
        this.terminalModeCode = terminalModeCode;
    }

    public void setTerminalTypeCode(String terminalTypeCode)
    {
        this.terminalTypeCode = terminalTypeCode;
    }
}
