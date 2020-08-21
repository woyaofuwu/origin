
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author think
 */
public class ProtectPassInfoRequestData extends BaseProtectPassInfoRequestData
{
    private List<SvcProtectPassInfoData> svcInfo = new ArrayList<SvcProtectPassInfoData>();

    private List<OtherProtectPassInfoData> otherInfo = new ArrayList<OtherProtectPassInfoData>();

    private String flag;

    public final String getFlag()
    {
        return flag;
    }

    public final List<OtherProtectPassInfoData> getOtherInfo()
    {
        return otherInfo;
    }

    public final List<SvcProtectPassInfoData> getSvcInfo()
    {
        return svcInfo;
    }

    public final void setFlag(String flag)
    {
        this.flag = flag;
    }

    public final void setOtherInfo(List<OtherProtectPassInfoData> otherInfo)
    {
        this.otherInfo = otherInfo;
    }

    public final void setSvcInfo(List<SvcProtectPassInfoData> svcInfo)
    {
        this.svcInfo = svcInfo;
    }

}
