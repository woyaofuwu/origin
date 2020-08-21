
package com.asiainfo.veris.crm.order.soa.group.batVpnUptoCountrywide;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElementReqData;

public class VpnMemUpToCountrywideReqData extends ChangeMemElementReqData
{
    private String tradeTypeCode;

    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }

}
