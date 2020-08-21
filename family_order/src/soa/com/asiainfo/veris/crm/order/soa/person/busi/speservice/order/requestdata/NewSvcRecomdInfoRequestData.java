
package com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.requestdata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author think
 */
public class NewSvcRecomdInfoRequestData extends BaseNewSvcRecomdInfoRequestData
{
    private List<NewSvcRecomdInfoData> recomdInfo = new ArrayList<NewSvcRecomdInfoData>();

    public final List<NewSvcRecomdInfoData> getRecomdInfo()
    {
        return recomdInfo;
    }

    public final void setRecomdInfo(List<NewSvcRecomdInfoData> recomdInfo)
    {
        this.recomdInfo = recomdInfo;
    }

}
