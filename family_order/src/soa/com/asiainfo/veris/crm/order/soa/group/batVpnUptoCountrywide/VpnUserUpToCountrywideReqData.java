
package com.asiainfo.veris.crm.order.soa.group.batVpnUptoCountrywide;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElementReqData;

public class VpnUserUpToCountrywideReqData extends ChangeUserElementReqData
{
    private IData productParam;

    private boolean hasDel801;

    private boolean hasAdd801;

    private boolean old801Flag;

    private boolean hasVpnScare;

    public boolean isHasVpnScare()
    {
        return hasVpnScare;
    }

    public void setHasVpnScare(boolean hasVpnScare)
    {
        this.hasVpnScare = hasVpnScare;
    }

    public boolean isHasAdd801()
    {
        return hasAdd801;
    }

    public void setHasAdd801(boolean hasAdd801)
    {
        this.hasAdd801 = hasAdd801;
    }

    public boolean isHasDel801()
    {
        return hasDel801;
    }

    public void setHasDel801(boolean hasDel801)
    {
        this.hasDel801 = hasDel801;
    }

    public boolean isOld801Flag()
    {
        return old801Flag;
    }

    public void setOld801Flag(boolean old801Flag)
    {
        this.old801Flag = old801Flag;
    }

    public IData getProductParam()
    {
        return productParam;
    }

    public void setProductParam(IData productParam)
    {
        this.productParam = productParam;
    }

}
