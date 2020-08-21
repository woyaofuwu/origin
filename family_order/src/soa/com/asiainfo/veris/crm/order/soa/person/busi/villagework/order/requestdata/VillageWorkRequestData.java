
package com.asiainfo.veris.crm.order.soa.person.busi.villagework.order.requestdata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author think
 */
public class VillageWorkRequestData extends BaseVillageWorkRequestData
{
    private List<VillageWorkData> serNumInfo = new ArrayList<VillageWorkData>();

    public final List<VillageWorkData> getSerNumInfo()
    {
        return serNumInfo;
    }

    public final void setSerNumInfo(List<VillageWorkData> serNumInfo)
    {
        this.serNumInfo = serNumInfo;
    }

}
