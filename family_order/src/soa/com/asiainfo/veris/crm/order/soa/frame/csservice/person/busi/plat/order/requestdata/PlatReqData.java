
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;

public class PlatReqData extends BaseReqData
{

    public List<PlatSvcData> platSvcDatas;

    public List<String> allPlatCancels;

    public List<String> switches;

    private boolean sync;

    public List<String> getAllPlatCancels()
    {
        return allPlatCancels;
    }

    public List<PlatSvcData> getPlatSvcDatas()
    {
        return platSvcDatas;
    }

    public List<String> getSwitches()
    {
        return switches;
    }

    public boolean isSync()
    {
        return sync;
    }

    public void setAllPlatCancels(List<String> allPlatCancels)
    {
        this.allPlatCancels = allPlatCancels;
    }

    public void setPlatSvcDatas(List<PlatSvcData> platSvcDatas)
    {
        this.platSvcDatas = platSvcDatas;
    }

    public void setSwitches(List<String> switches)
    {
        this.switches = switches;
    }

    public void setSync(boolean sync)
    {
        this.sync = sync;
    }
}
